/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import RoomService from '../../../src/main/resources/META-INF/resources/js/common/services/RoomService';
import {IRoomShareProps} from '../../../src/main/resources/META-INF/resources/js/common/utils/types';
import RoomShare from '../../../src/main/resources/META-INF/resources/js/components/RoomShare';

const mockOpenToast = jest.fn();

jest.mock('frontend-js-components-web', () => ({
	openToast: (...args: unknown[]) => mockOpenToast(...args),
}));

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/common/services/RoomService',
	() => ({
		__esModule: true,
		default: {
			addRoomUserAccount: jest.fn(),
			deleteRoomInvitedMember: jest.fn(),
			deleteRoomUserAccount: jest.fn(),
			getRoomInvitedMembers: jest.fn(),
			getRoomUserAccounts: jest.fn(),
			updateRoomUserAccount: jest.fn(),
		},
	})
);

const mockUsers = [
	{
		emailAddress: 'john.doe@liferay.com',
		id: 1,
		name: 'John Doe',
		roleKey: 'Site Owner',
	},
	{
		emailAddress: 'ran.doe@liferay.com',
		id: 2,
		name: 'Ran Doe',
		roleKey: 'Site Administrator',
	},
	{
		emailAddress: 'win.doe@liferay.com',
		id: 3,
		name: 'Win Doe',
		roleKey: 'Site Member',
	},
];

const renderComponent = ({roomId}: IRoomShareProps) => {
	return render(<RoomShare roomId={roomId} />);
};

describe('RoomShare', () => {
	beforeEach(() => {
		jest.clearAllMocks();
		mockOpenToast.mockClear();

		(RoomService.addRoomUserAccount as jest.Mock).mockResolvedValue({});

		(RoomService.getRoomInvitedMembers as jest.Mock).mockResolvedValue([]);

		(RoomService.getRoomUserAccounts as jest.Mock).mockResolvedValue(
			mockUsers
		);

		(RoomService.updateRoomUserAccount as jest.Mock).mockImplementation(
			(roomId, userId, data) => {
				const user = mockUsers.find((u) => u.id === userId);

				return Promise.resolve({
					...user,
					...data,
				});
			}
		);
	});

	afterEach(() => {
		cleanup();
	});

	it('renders the correct UI elements', async () => {
		const {container} = renderComponent({
			roomId: 10,
		});

		await waitFor(() => {
			expect(screen.getByText('John Doe')).toBeInTheDocument();
		});

		expect(
			container.querySelector('[data-testid="emailAddressesInput"]')
		).toBeInTheDocument();
		expect(
			container.querySelector('[data-testid="roleKeyButton"]')
		).toBeInTheDocument();
		expect(
			container.querySelector('[data-testid="inviteButton"]')
		).toBeInTheDocument();
	});

	it('loads and displays users', async () => {
		renderComponent({
			roomId: 10,
		});

		await waitFor(() => {
			expect(RoomService.getRoomUserAccounts).toHaveBeenCalledWith(10);
		});
		await waitFor(() => {
			expect(RoomService.getRoomInvitedMembers).toHaveBeenCalledWith(10);
		});

		await waitFor(() => {
			expect(screen.getByText('John Doe')).toBeInTheDocument();
			expect(screen.getByText('Ran Doe')).toBeInTheDocument();
			expect(screen.getByText('Win Doe')).toBeInTheDocument();
		});
	});

	it('displays owner role without dropdown', async () => {
		renderComponent({
			roomId: 10,
		});

		await waitFor(() => {
			expect(screen.getByText('John Doe')).toBeInTheDocument();
		});

		expect(screen.getByText('owner')).toBeInTheDocument();
	});

	it('invites a new user with valid email', async () => {
		const {container} = renderComponent({
			roomId: 10,
		});

		await waitFor(() => {
			expect(screen.getByText('John Doe')).toBeInTheDocument();
		});

		const emailInput = container.querySelector(
			'[data-testid="emailAddressesInput"]'
		) as HTMLInputElement;
		const inviteButton = container.querySelector(
			'[data-testid="inviteButton"]'
		) as HTMLButtonElement;

		await userEvent.type(emailInput, 'newuser@liferay.com,');
		await userEvent.click(inviteButton);

		await waitFor(() => {
			expect(RoomService.addRoomUserAccount).toHaveBeenCalledWith(10, {
				emailAddress: 'newuser@liferay.com',
				roleKey: 'Site Member',
			});
		});
	});

	it('invites multiple users with comma-separated emails', async () => {
		const {container} = renderComponent({
			roomId: 10,
		});

		await waitFor(() => {
			expect(screen.getByText('John Doe')).toBeInTheDocument();
		});

		const emailInput = container.querySelector(
			'[data-testid="emailAddressesInput"]'
		) as HTMLInputElement;
		const inviteButton = container.querySelector(
			'[data-testid="inviteButton"]'
		) as HTMLButtonElement;

		await userEvent.type(
			emailInput,
			'user1@liferay.com,user2@liferay.com,'
		);

		await userEvent.click(inviteButton);

		await waitFor(() => {
			expect(RoomService.addRoomUserAccount).toHaveBeenCalledTimes(2);
			expect(RoomService.addRoomUserAccount).toHaveBeenCalledWith(10, {
				emailAddress: 'user1@liferay.com',
				roleKey: 'Site Member',
			});
			expect(RoomService.addRoomUserAccount).toHaveBeenCalledWith(10, {
				emailAddress: 'user2@liferay.com',
				roleKey: 'Site Member',
			});
		});
	});

	it('changes role for a user', async () => {
		renderComponent({
			roomId: 10,
		});

		await waitFor(() => {
			expect(screen.getByText('Win Doe')).toBeInTheDocument();
		});

		const userRow = screen.getByText('Win Doe').closest('.user-row');
		const dropdownButton = userRow?.querySelector(
			'.dropdown-toggle'
		) as HTMLElement;

		expect(dropdownButton.textContent).toContain('viewer');

		await userEvent.click(dropdownButton);

		await waitFor(() => {
			expect(document.querySelector('.dropdown-menu.show')).toBeTruthy();
		});

		const openDropdownMenu = document.querySelector('.dropdown-menu.show');
		const contributorMenuItem = openDropdownMenu?.querySelector(
			'.dropdown-item'
		) as HTMLElement;

		expect(contributorMenuItem.textContent).toContain('contributor');

		await userEvent.click(contributorMenuItem);

		await waitFor(() => {
			expect(RoomService.updateRoomUserAccount).toHaveBeenCalledWith(
				10,
				3,
				{
					roleKey: 'Site Administrator',
				}
			);
		});
	});

	it('shows error toast when loading users fails', async () => {
		const errorMessage = 'Failed to load users';

		(RoomService.getRoomUserAccounts as jest.Mock).mockRejectedValue(
			new Error(errorMessage)
		);

		renderComponent({
			roomId: 10,
		});

		await waitFor(() => {
			expect(mockOpenToast).toHaveBeenCalledWith({
				message: errorMessage,
				type: 'danger',
			});
		});
	});

	it('disables fields and buttons when loading users', async () => {
		let resolveUsersFetch: (value: any) => void;

		const usersPromise = new Promise((resolve) => {
			resolveUsersFetch = resolve;
		});

		(RoomService.getRoomUserAccounts as jest.Mock).mockReturnValue(
			usersPromise
		);

		const {container} = renderComponent({
			roomId: 10,
		});

		await new Promise((resolve) => setTimeout(resolve, 10));

		expect(
			container.querySelector('[data-testid="emailAddressesInput"]')
		).toBeDisabled();
		expect(
			container.querySelector('[data-testid="roleKeyButton"]')
		).toBeDisabled();
		expect(
			container.querySelector('[data-testid="inviteButton"]')
		).toBeDisabled();

		resolveUsersFetch!(mockUsers);

		await waitFor(() => {
			expect(screen.getByText('John Doe')).toBeInTheDocument();
		});
	});

	it('selects role from dropdown before inviting', async () => {
		const {container} = renderComponent({
			roomId: 10,
		});

		await waitFor(() => {
			expect(screen.getByText('John Doe')).toBeInTheDocument();
		});

		const roleKeyButton = container.querySelector(
			'[data-testid="roleKeyButton"]'
		) as HTMLButtonElement;
		await userEvent.click(roleKeyButton);

		await waitFor(() => {
			document.querySelector('[data-testid="roleKeyItem_contributor"]');
		});

		const contributorItem = document.querySelector(
			'[data-testid="roleKeyItem_contributor"]'
		) as HTMLButtonElement;
		await userEvent.click(contributorItem);

		const emailInput = container.querySelector(
			'[data-testid="emailAddressesInput"]'
		) as HTMLInputElement;
		await userEvent.type(emailInput, 'newuser@liferay.com,');

		const inviteButton = container.querySelector(
			'[data-testid="inviteButton"]'
		) as HTMLButtonElement;
		await userEvent.click(inviteButton);

		await waitFor(() => {
			expect(RoomService.addRoomUserAccount).toHaveBeenCalledWith(10, {
				emailAddress: 'newuser@liferay.com',
				roleKey: 'Site Administrator',
			});
		});
	});

	it('displays user initials when no image is available', async () => {
		renderComponent({
			roomId: 10,
		});

		await waitFor(() => {
			expect(screen.getByText('J')).toBeInTheDocument();
			expect(screen.getByText('R')).toBeInTheDocument();
			expect(screen.getByText('W')).toBeInTheDocument();
		});
	});

	it('shows users count in the header', async () => {
		renderComponent({
			roomId: 10,
		});

		await waitFor(() => {
			expect(
				screen.getByText('who-has-access-3-users')
			).toBeInTheDocument();
		});
	});

	it('loads and displays invited members along with regular users', async () => {
		const mockInvitedMembers = [
			{
				emailAddress: 'invited@example.com',
				id: 10,
			},
		];

		(RoomService.getRoomInvitedMembers as jest.Mock).mockResolvedValue(
			mockInvitedMembers
		);

		renderComponent({
			roomId: 10,
		});

		await waitFor(() => {
			expect(screen.getByText('John Doe')).toBeInTheDocument();
			expect(screen.getByText('invited@example.com')).toBeInTheDocument();
		});
	});

	it('hides role dropdown for invited members', async () => {
		const mockInvitedMembers = [
			{
				emailAddress: 'invited@example.com',
				id: 10,
			},
		];

		(RoomService.getRoomInvitedMembers as jest.Mock).mockResolvedValue(
			mockInvitedMembers
		);

		renderComponent({
			roomId: 10,
		});

		await waitFor(() => {
			expect(screen.getByText('invited@example.com')).toBeInTheDocument();
		});

		const invitedUserRow = screen
			.getByText('invited@example.com')
			.closest('.user-row');

		expect(invitedUserRow?.querySelector('.dropdown-toggle')).toBeNull();
		expect(invitedUserRow?.textContent).toContain('viewer');
	});
});

/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {
	cleanup,
	fireEvent,
	render,
	screen,
	waitFor,
} from '@testing-library/react';
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
			updateRoomInvitedMember: jest.fn(),
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
		roleKey: 'DSR Content Contributor',
	},
	{
		emailAddress: 'win.doe@liferay.com',
		id: 3,
		name: 'Win Doe',
		roleKey: 'Site Member',
	},
];

const renderComponent = ({canAssignAllRoles, roomId}: IRoomShareProps) => {
	return render(
		<RoomShare canAssignAllRoles={canAssignAllRoles} roomId={roomId} />
	);
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

		(RoomService.updateRoomInvitedMember as jest.Mock).mockResolvedValue(
			{}
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
		jest.restoreAllMocks();
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
		expect(container.querySelector('.reference-mark')).toBeInTheDocument();
	});

	it('disables the invite button until an email address is added', async () => {
		const {container} = renderComponent({
			roomId: 10,
		});

		await waitFor(() => {
			expect(screen.getByText('John Doe')).toBeInTheDocument();
		});

		const inviteButton = container.querySelector(
			'[data-testid="inviteButton"]'
		) as HTMLButtonElement;

		expect(inviteButton).toBeDisabled();

		const emailInput = container.querySelector(
			'[data-testid="emailAddressesInput"]'
		) as HTMLInputElement;

		await userEvent.type(emailInput, 'newuser@liferay.com,');

		expect(inviteButton).not.toBeDisabled();
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

	it('changes role for a user while preserving the expiration date', async () => {
		const membershipExpirationDate = new Date(
			Date.now() + 365 * 24 * 60 * 60 * 1000
		).toISOString();

		(RoomService.getRoomUserAccounts as jest.Mock).mockResolvedValue([
			...mockUsers.slice(0, 2),
			{
				...mockUsers[2],
				membershipExpirationDate,
			},
		]);

		renderComponent({
			canAssignAllRoles: true,
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
		const contributorMenuItem = Array.from(
			openDropdownMenu?.querySelectorAll('.dropdown-item') ?? []
		).find((item) =>
			item.textContent?.includes('content-contributor')
		) as HTMLElement;

		expect(contributorMenuItem.textContent).toContain(
			'content-contributor'
		);

		await userEvent.click(contributorMenuItem);

		await waitFor(() => {
			expect(RoomService.updateRoomUserAccount).toHaveBeenCalledWith(
				10,
				3,
				{
					membershipExpirationDate,
					roleKey: 'DSR Content Contributor',
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
			canAssignAllRoles: true,
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
			document.querySelector(
				'[data-testid="roleKeyItem_content-contributor"]'
			);
		});

		const contributorItem = document.querySelector(
			'[data-testid="roleKeyItem_content-contributor"]'
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
				roleKey: 'DSR Content Contributor',
			});
		});
	});

	it('displays user initials when no image is available', async () => {
		const {container} = renderComponent({
			roomId: 10,
		});

		await waitFor(() => {
			expect(screen.getByText('John Doe')).toBeInTheDocument();
		});

		const initials = Array.from(
			container.querySelectorAll('.sticker-user-icon')
		).map((sticker) => sticker.textContent);

		expect(initials).toEqual(['J', 'R', 'W']);
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

	it('hides role dropdown for invited members when current user is not owner or inviter', async () => {
		jest.spyOn(Liferay.ThemeDisplay, 'getUserId').mockReturnValue('999');

		const mockInvitedMembers = [
			{
				emailAddress: 'invited@example.com',
				id: 10,
				ownerId: 998,
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

	it('shows role dropdown for invited members when current user is owner', async () => {
		const mockInvitedMembers = [
			{
				emailAddress: 'invited@example.com',
				id: 10,
				ownerId: 999,
			},
		];

		(RoomService.getRoomInvitedMembers as jest.Mock).mockResolvedValue(
			mockInvitedMembers
		);

		renderComponent({
			canAssignAllRoles: true,
			roomId: 10,
		});

		await waitFor(() => {
			expect(screen.getByText('invited@example.com')).toBeInTheDocument();
		});

		const invitedUserRow = screen
			.getByText('invited@example.com')
			.closest('.user-row');

		expect(
			invitedUserRow?.querySelector('.dropdown-toggle')
		).not.toBeNull();
	});

	it('shows role dropdown for invited members when current user is the inviter', async () => {
		jest.spyOn(Liferay.ThemeDisplay, 'getUserId').mockReturnValue('998');

		const mockInvitedMembers = [
			{
				emailAddress: 'invited@example.com',
				id: 10,
				ownerId: 998,
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

		expect(
			invitedUserRow?.querySelector('.dropdown-toggle')
		).not.toBeNull();
	});

	it('displays no-expiration for a user without an expiration date', async () => {
		renderComponent({
			roomId: 10,
		});

		await waitFor(() => {
			expect(screen.getByText('Win Doe')).toBeInTheDocument();
		});

		const userRow = screen.getByText('Win Doe').closest('.user-row');

		expect(userRow?.textContent).toContain('no-expiration');
	});

	it('displays the expiration label in warning or info style depending on whether it is expiring soon', async () => {
		const expiringSoonDate = new Date(
			Date.now() + 3 * 24 * 60 * 60 * 1000
		).toISOString();
		const notExpiringSoonDate = new Date(
			Date.now() + 365 * 24 * 60 * 60 * 1000
		).toISOString();

		(RoomService.getRoomUserAccounts as jest.Mock).mockResolvedValue([
			mockUsers[0],
			{
				...mockUsers[1],
				membershipExpirationDate: expiringSoonDate,
			},
			{
				...mockUsers[2],
				membershipExpirationDate: notExpiringSoonDate,
			},
		]);

		renderComponent({
			roomId: 10,
		});

		await waitFor(() => {
			expect(screen.getByText('Win Doe')).toBeInTheDocument();
		});

		const expiringSoonRow = screen
			.getByText('Ran Doe')
			.closest('.user-row');

		expect(expiringSoonRow?.querySelector('.label-warning')).not.toBeNull();
		expect(
			expiringSoonRow?.querySelector('.lexicon-icon-warning-full')
		).not.toBeNull();
		expect(expiringSoonRow?.querySelector('.label-info')).toBeNull();

		const notExpiringSoonRow = screen
			.getByText('Win Doe')
			.closest('.user-row');

		expect(notExpiringSoonRow?.querySelector('.label-info')).not.toBeNull();
		expect(notExpiringSoonRow?.querySelector('.label-warning')).toBeNull();
		expect(
			notExpiringSoonRow?.querySelector('.lexicon-icon-warning-full')
		).toBeNull();
		expect(
			screen.getByText(
				new Date(notExpiringSoonDate).toLocaleDateString('en-US', {
					day: 'numeric',
					month: 'short',
					timeZone: 'UTC',
					year: 'numeric',
				})
			)
		).toBeInTheDocument();
	});

	it('updates the expiration date while preserving the role', async () => {
		const membershipExpirationDate = new Date(
			Date.now() + 365 * 24 * 60 * 60 * 1000
		).toISOString();

		(RoomService.getRoomUserAccounts as jest.Mock).mockResolvedValue([
			...mockUsers.slice(0, 2),
			{
				...mockUsers[2],
				membershipExpirationDate,
			},
		]);

		const {container} = renderComponent({
			canAssignAllRoles: true,
			roomId: 10,
		});

		await waitFor(() => {
			expect(screen.getByText('Win Doe')).toBeInTheDocument();
		});

		await userEvent.click(
			container.querySelector(
				'[data-testid="editExpiration_3"]'
			) as HTMLButtonElement
		);

		await userEvent.click(
			container.querySelector(
				'[data-testid="confirmExpiration_3"]'
			) as HTMLButtonElement
		);

		await waitFor(() => {
			expect(RoomService.updateRoomUserAccount).toHaveBeenCalledWith(
				10,
				3,
				expect.objectContaining({
					membershipExpirationDate: expect.any(String),
					roleKey: 'Site Member',
				})
			);
		});
	});

	it('does not update the expiration date when it is in the past', async () => {
		const membershipExpirationDate = new Date(
			Date.now() + 365 * 24 * 60 * 60 * 1000
		).toISOString();

		(RoomService.getRoomUserAccounts as jest.Mock).mockResolvedValue([
			...mockUsers.slice(0, 2),
			{
				...mockUsers[2],
				membershipExpirationDate,
			},
		]);

		const {container} = renderComponent({
			canAssignAllRoles: true,
			roomId: 10,
		});

		await waitFor(() => {
			expect(screen.getByText('Win Doe')).toBeInTheDocument();
		});

		await userEvent.click(
			container.querySelector(
				'[data-testid="editExpiration_3"]'
			) as HTMLButtonElement
		);

		fireEvent.change(
			container.querySelectorAll(
				'input[placeholder="YYYY-MM-DD"]'
			)[1] as HTMLInputElement,
			{
				target: {value: '2020-01-15'},
			}
		);

		await userEvent.click(
			container.querySelector(
				'[data-testid="confirmExpiration_3"]'
			) as HTMLButtonElement
		);

		expect(RoomService.updateRoomUserAccount).not.toHaveBeenCalled();
		expect(mockOpenToast).toHaveBeenCalledWith({
			message: 'expiration-date-must-be-a-future-date',
			type: 'danger',
		});
	});

	it('shows the expiration warning banner when a membership is expiring soon', async () => {
		const membershipExpirationDate = new Date(
			Date.now() + 3 * 24 * 60 * 60 * 1000
		).toISOString();

		(RoomService.getRoomUserAccounts as jest.Mock).mockResolvedValue([
			...mockUsers.slice(0, 2),
			{
				...mockUsers[2],
				membershipExpirationDate,
			},
		]);

		const {container} = renderComponent({
			roomId: 10,
		});

		await waitFor(() => {
			expect(screen.getByText('Win Doe')).toBeInTheDocument();
		});

		const alert = container.querySelector('.alert');

		expect(alert).toBeInTheDocument();
		expect(alert?.textContent).toContain('has-access-expiring-within');
	});

	it('invites a user with an expiration date', async () => {
		const {container} = renderComponent({
			roomId: 10,
		});

		await waitFor(() => {
			expect(screen.getByText('John Doe')).toBeInTheDocument();
		});

		fireEvent.change(
			container.querySelector(
				'input[placeholder="YYYY-MM-DD"]'
			) as HTMLInputElement,
			{
				target: {value: '2030-01-15'},
			}
		);

		const emailInput = container.querySelector(
			'[data-testid="emailAddressesInput"]'
		) as HTMLInputElement;
		await userEvent.type(emailInput, 'newuser@liferay.com,');

		await userEvent.click(
			container.querySelector(
				'[data-testid="inviteButton"]'
			) as HTMLButtonElement
		);

		await waitFor(() => {
			expect(RoomService.addRoomUserAccount).toHaveBeenCalledWith(
				10,
				expect.objectContaining({
					emailAddress: 'newuser@liferay.com',
					membershipExpirationDate: expect.any(String),
					roleKey: 'Site Member',
				})
			);
		});
	});

	it('does not invite a user when the expiration date is in the past', async () => {
		const {container} = renderComponent({
			roomId: 10,
		});

		await waitFor(() => {
			expect(screen.getByText('John Doe')).toBeInTheDocument();
		});

		fireEvent.change(
			container.querySelector(
				'input[placeholder="YYYY-MM-DD"]'
			) as HTMLInputElement,
			{
				target: {value: '2020-01-15'},
			}
		);

		await userEvent.type(
			container.querySelector(
				'[data-testid="emailAddressesInput"]'
			) as HTMLInputElement,
			'newuser@liferay.com,'
		);

		await userEvent.click(
			container.querySelector(
				'[data-testid="inviteButton"]'
			) as HTMLButtonElement
		);

		expect(RoomService.addRoomUserAccount).not.toHaveBeenCalled();
		expect(mockOpenToast).toHaveBeenCalledWith({
			message: 'expiration-date-must-be-a-future-date',
			type: 'danger',
		});
	});

	it('updates an invited member expiration date using updateRoomInvitedMember', async () => {
		const membershipExpirationDate = new Date(
			Date.now() + 365 * 24 * 60 * 60 * 1000
		).toISOString();

		(RoomService.getRoomInvitedMembers as jest.Mock).mockResolvedValue([
			{
				emailAddress: 'invited@example.com',
				id: 10,
				membershipExpirationDate,
				ownerId: 999,
			},
		]);

		const {container} = renderComponent({
			canAssignAllRoles: true,
			roomId: 10,
		});

		await waitFor(() => {
			expect(screen.getByText('invited@example.com')).toBeInTheDocument();
		});

		await userEvent.click(
			container.querySelector(
				'[data-testid="editExpiration_10"]'
			) as HTMLButtonElement
		);

		await userEvent.click(
			container.querySelector(
				'[data-testid="confirmExpiration_10"]'
			) as HTMLButtonElement
		);

		await waitFor(() => {
			expect(RoomService.updateRoomInvitedMember).toHaveBeenCalledWith(
				10,
				10,
				expect.objectContaining({
					membershipExpirationDate: expect.any(String),
				})
			);
		});
	});

	it('invites a user with the room collaborator role', async () => {
		const {container} = renderComponent({
			canAssignAllRoles: true,
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
			expect(
				document.querySelector(
					'[data-testid="roleKeyItem_room-collaborator"]'
				)
			).toBeInTheDocument();
		});

		const roomCollaboratorItem = document.querySelector(
			'[data-testid="roleKeyItem_room-collaborator"]'
		) as HTMLButtonElement;
		await userEvent.click(roomCollaboratorItem);

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
				roleKey: 'DSR Room Collaborator',
			});
		});
	});

	it('shows only the content contributor and viewer roles when the current user is a room collaborator', async () => {
		const roomCollaboratorUser = [
			{
				emailAddress: 'jack.doe@liferay.com',
				id: 1,
				name: 'Jack Doe',
				roleKey: 'DSR Room Collaborator',
			},
		];

		(RoomService.getRoomUserAccounts as jest.Mock).mockResolvedValue(
			roomCollaboratorUser
		);

		const {container} = renderComponent({
			roomId: 10,
		});

		await waitFor(() => {
			expect(screen.getByText('Jack Doe')).toBeInTheDocument();
		});

		const roleKeyButton = container.querySelector(
			'[data-testid="roleKeyButton"]'
		) as HTMLButtonElement;
		await userEvent.click(roleKeyButton);

		await waitFor(() => {
			expect(
				document.querySelector(
					'[data-testid="roleKeyItem_content-contributor"]'
				)
			).toBeInTheDocument();
		});

		expect(
			document.querySelector('[data-testid="roleKeyItem_viewer"]')
		).toBeInTheDocument();
		expect(
			document.querySelector(
				'[data-testid="roleKeyItem_room-collaborator"]'
			)
		).toBeNull();
	});

	it('shows all roles when the current user is an administrator who is not a room member', async () => {
		jest.spyOn(Liferay.ThemeDisplay, 'getUserId').mockReturnValue('999');

		const {container} = renderComponent({
			canAssignAllRoles: true,
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
			expect(
				document.querySelector(
					'[data-testid="roleKeyItem_room-collaborator"]'
				)
			).toBeInTheDocument();
		});

		expect(
			document.querySelector(
				'[data-testid="roleKeyItem_content-contributor"]'
			)
		).toBeInTheDocument();
		expect(
			document.querySelector('[data-testid="roleKeyItem_viewer"]')
		).toBeInTheDocument();
	});

	it('does not allow a content contributor to edit their own or a peer expiration date', async () => {
		jest.spyOn(Liferay.ThemeDisplay, 'getUserId').mockReturnValue('2');

		(RoomService.getRoomUserAccounts as jest.Mock).mockResolvedValue([
			...mockUsers,
			{
				emailAddress: 'sam.doe@liferay.com',
				id: 4,
				name: 'Sam Doe',
				roleKey: 'DSR Content Contributor',
			},
		]);

		const {container} = renderComponent({
			roomId: 10,
		});

		await waitFor(() => {
			expect(screen.getByText('Sam Doe')).toBeInTheDocument();
		});

		expect(
			container.querySelector('[data-testid="editExpiration_2"]')
		).toBeNull();
		expect(
			container.querySelector('[data-testid="editExpiration_3"]')
		).toBeInTheDocument();
		expect(
			container.querySelector('[data-testid="editExpiration_4"]')
		).toBeNull();
	});

	it('allows a content contributor to edit a viewer without an explicit role', async () => {
		jest.spyOn(Liferay.ThemeDisplay, 'getUserId').mockReturnValue('2');

		(RoomService.getRoomUserAccounts as jest.Mock).mockResolvedValue([
			...mockUsers.slice(0, 2),
			{
				emailAddress: 'win.doe@liferay.com',
				id: 3,
				name: 'Win Doe',
			},
		]);

		const {container} = renderComponent({
			roomId: 10,
		});

		await waitFor(() => {
			expect(screen.getByText('Win Doe')).toBeInTheDocument();
		});

		expect(
			container.querySelector('[data-testid="editExpiration_3"]')
		).toBeInTheDocument();
	});

	it('allows a room collaborator to edit a content contributor but not themselves', async () => {
		jest.spyOn(Liferay.ThemeDisplay, 'getUserId').mockReturnValue('4');

		(RoomService.getRoomUserAccounts as jest.Mock).mockResolvedValue([
			...mockUsers,
			{
				emailAddress: 'sam.doe@liferay.com',
				id: 4,
				name: 'Sam Doe',
				roleKey: 'DSR Room Collaborator',
			},
		]);

		const {container} = renderComponent({
			roomId: 10,
		});

		await waitFor(() => {
			expect(screen.getByText('Sam Doe')).toBeInTheDocument();
		});

		expect(
			container.querySelector('[data-testid="editExpiration_2"]')
		).toBeInTheDocument();
		expect(
			container.querySelector('[data-testid="editExpiration_3"]')
		).toBeInTheDocument();
		expect(
			container.querySelector('[data-testid="editExpiration_4"]')
		).toBeNull();
	});

	it('shows the content contributor and viewer roles when the current user is a content contributor', async () => {
		jest.spyOn(Liferay.ThemeDisplay, 'getUserId').mockReturnValue('2');

		const {container} = renderComponent({
			roomId: 10,
		});

		await waitFor(() => {
			expect(screen.getByText('Ran Doe')).toBeInTheDocument();
		});

		const roleKeyButton = container.querySelector(
			'[data-testid="roleKeyButton"]'
		) as HTMLButtonElement;
		await userEvent.click(roleKeyButton);

		await waitFor(() => {
			expect(
				document.querySelector(
					'[data-testid="roleKeyItem_content-contributor"]'
				)
			).toBeInTheDocument();
		});

		expect(
			document.querySelector('[data-testid="roleKeyItem_viewer"]')
		).toBeInTheDocument();
		expect(
			document.querySelector(
				'[data-testid="roleKeyItem_room-collaborator"]'
			)
		).toBeNull();
	});

	it('does not allow a content contributor to change their own role', async () => {
		jest.spyOn(Liferay.ThemeDisplay, 'getUserId').mockReturnValue('2');

		const {container} = renderComponent({
			roomId: 10,
		});

		await waitFor(() => {
			expect(screen.getByText('Ran Doe')).toBeInTheDocument();
		});

		expect(
			container.querySelector('[data-testid="memberRoleKeyButton_2"]')
		).toBeNull();
		expect(
			container.querySelector('[data-testid="memberRoleKeyButton_3"]')
		).toBeInTheDocument();
	});

	it('does not show an error message when the reload after an update fails', async () => {
		(RoomService.updateRoomUserAccount as jest.Mock).mockResolvedValue({});

		(RoomService.getRoomUserAccounts as jest.Mock)
			.mockResolvedValueOnce(mockUsers)
			.mockRejectedValue(new Error('Forbidden'));

		renderComponent({
			canAssignAllRoles: true,
			roomId: 10,
		});

		await waitFor(() => {
			expect(screen.getByText('Win Doe')).toBeInTheDocument();
		});

		const userRow = screen.getByText('Win Doe').closest('.user-row');
		const dropdownButton = userRow?.querySelector(
			'.dropdown-toggle'
		) as HTMLElement;

		await userEvent.click(dropdownButton);

		await waitFor(() => {
			expect(document.querySelector('.dropdown-menu.show')).toBeTruthy();
		});

		const openDropdownMenu = document.querySelector('.dropdown-menu.show');
		const contributorMenuItem = Array.from(
			openDropdownMenu?.querySelectorAll('.dropdown-item') ?? []
		).find((item) =>
			item.textContent?.includes('content-contributor')
		) as HTMLElement;

		await userEvent.click(contributorMenuItem);

		await waitFor(() => {
			expect(mockOpenToast).toHaveBeenCalledWith({
				message: 'your-request-completed-successfully',
				type: 'success',
			});
		});

		expect(mockOpenToast).toHaveBeenCalledTimes(1);
	});
});

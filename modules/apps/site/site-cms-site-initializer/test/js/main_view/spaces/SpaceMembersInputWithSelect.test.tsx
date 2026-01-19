/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {RenderResult, render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {
	UserAccount,
	UserGroup,
} from '../../../../src/main/resources/META-INF/resources/js/common/types/UserAccount';
import {
	SelectOptions,
	SpaceMembersInputWithSelect,
	SpaceMembersInputWithSelectProps,
} from '../../../../src/main/resources/META-INF/resources/js/main_view/spaces/SpaceMembersInputWithSelect';
import {createMockFetchMembersImplementation} from '../../__mocks__/createMockFetchMembersImplementation';
import {mockFetch} from '../../__mocks__/frontend-js-web';

const DEFAULT_PROPS: SpaceMembersInputWithSelectProps = {
	selectValue: SelectOptions.USERS,
};

const renderComponent = async (
	props?: Partial<SpaceMembersInputWithSelectProps>
): Promise<RenderResult> => {
	const mergedProps = {
		...DEFAULT_PROPS,
		...props,
	};

	const renderResult = render(
		<SpaceMembersInputWithSelect {...mergedProps} />
	);

	await waitFor(() => {
		expect(
			screen.getByPlaceholderText('enter-name-or-email')
		).toBeInTheDocument();
	});

	return renderResult;
};

describe('SpaceMembersInputWithSelect', () => {
	const {ResizeObserver: ResizeObserverOriginal} = window;

	const allUsers = [
		{
			emailAddress: 'john.doe@example.com',
			externalReferenceCode: 'john.doe',
			id: '1',
			image: '/image/user_portrait',
			imageId: 'john.doe.image',
			name: 'John Doe',
		},
		{
			emailAddress: 'jane.smith@example.com',
			externalReferenceCode: 'jane.smith',
			id: '2',
			image: '/image/user_portrait',
			imageId: 'jane.smith.image',
			name: 'Jane Smith',
		},
		{
			emailAddress: 'excluded.user@example.com',
			externalReferenceCode: 'excluded.user',
			id: '123',
			image: '/image/user_portrait',
			imageId: 'excluded.user.image',
			name: 'Excluded User',
		},
	] as UserAccount[];

	const allGroups = [
		{
			externalReferenceCode: 'group.1',
			id: '1',
			name: 'Group 1',
			roles: [],
			usersCount: 5,
		},
		{
			externalReferenceCode: 'group.2',
			id: '2',
			name: 'Group 2',
			roles: [],
			usersCount: 10,
		},
	];

	beforeAll(() => {
		window.ResizeObserver = jest.fn().mockImplementation(() => ({
			disconnect: jest.fn(),
			observe: jest.fn(),
			unobserve: jest.fn(),
		}));
	});

	beforeEach(() => {
		mockFetch.mockClear();
		createMockFetchMembersImplementation({
			groups: allGroups,
			users: allUsers,
		});
	});

	afterEach(() => {
		jest.clearAllMocks();
	});

	afterAll(() => {
		window.ResizeObserver = ResizeObserverOriginal;
		jest.restoreAllMocks();
		mockFetch.mockReset();
	});

	it('displays a list of users when the select value is "users"', async () => {
		await renderComponent();

		await userEvent.click(
			screen.getByPlaceholderText('enter-name-or-email')
		);

		await waitFor(() => {
			expect(screen.getAllByRole('option')).toHaveLength(allUsers.length);

			expect(
				screen.getByRole('option', {name: /John Doe \(john.doe\)/})
			).toBeInTheDocument();

			expect(
				screen.getByRole('option', {name: /Jane Smith \(jane.smith\)/})
			).toBeInTheDocument();
		});
	});

	it('displays a list of groups when the select value is "groups"', async () => {
		await renderComponent({
			selectValue: SelectOptions.GROUPS,
		});

		await userEvent.click(
			screen.getByPlaceholderText('enter-name-or-email')
		);

		await waitFor(() => {
			expect(screen.getAllByRole('option')).toHaveLength(
				allGroups.length
			);

			const group1 = screen.getByRole('option', {name: /Group 1/});
			expect(group1).toBeInTheDocument();
			expect(group1).toHaveTextContent('(5-members)');

			const group2 = screen.getByRole('option', {name: /Group 2/});
			expect(group2).toBeInTheDocument();
			expect(group2).toHaveTextContent('(10-members)');
		});
	});

	it('displays a group with 0 members if usersCount is not provided', async () => {
		mockFetch.mockResolvedValue({
			headers: new Headers([['Content-Type', 'application/json']]),
			json: async () => ({
				items: [{id: '1', name: 'Group 1'}],
			}),
		} as Response);

		await renderComponent({
			selectValue: SelectOptions.GROUPS,
		});

		await userEvent.click(
			screen.getByPlaceholderText('enter-name-or-email')
		);

		await waitFor(() => {
			screen.getByRole('option', {name: /Group 1/});

			const group1 = screen.getByRole('option', {name: /Group 1/});
			expect(group1).toBeInTheDocument();
			expect(group1).toHaveTextContent('(0-members)');
		});
	});

	it('displays "no results found" message when search returns no items', async () => {
		await renderComponent();

		const input = screen.getByPlaceholderText('enter-name-or-email');

		await userEvent.type(input, 'non-existent');

		expect(await screen.findByText('no-results-found')).toBeInTheDocument();
	});

	it('calls "onAutocompleteItemSelected" callback when a user is selected', async () => {
		const onAutocompleteItemSelected = jest.fn();

		await renderComponent({
			onAutocompleteItemSelected,
		});

		await userEvent.click(
			screen.getByPlaceholderText('enter-name-or-email')
		);

		await waitFor(() => {
			expect(
				screen.getByRole('option', {name: /John Doe \(john.doe\)/})
			).toBeInTheDocument();
		});

		await userEvent.click(
			screen.getByRole('option', {name: /John Doe \(john.doe\)/})
		);

		await waitFor(async () => {
			expect(onAutocompleteItemSelected).toHaveBeenCalledTimes(1);

			expect(onAutocompleteItemSelected).toHaveBeenCalledWith({
				emailAddress: 'john.doe@example.com',
				externalReferenceCode: 'john.doe',
				id: '1',
				image: '/image/user_portrait',
				imageId: 'john.doe.image',
				name: 'John Doe',
				roles: [],
			});

			expect(
				screen.getByPlaceholderText('enter-name-or-email')
			).toHaveValue('John Doe');
		});
	});

	it('calls "onAutocompleteItemSelected" callback when a group is selected', async () => {
		const onAutocompleteItemSelected = jest.fn();

		await renderComponent({
			onAutocompleteItemSelected,
			selectValue: SelectOptions.GROUPS,
		});

		const input = screen.getByPlaceholderText('enter-name-or-email');
		await userEvent.click(input);

		await waitFor(() => {
			expect(
				screen.getByRole('option', {name: /Group 1/})
			).toBeInTheDocument();
		});

		await userEvent.click(screen.getByRole('option', {name: /Group 1/}));

		await waitFor(async () => {
			expect(onAutocompleteItemSelected).toHaveBeenCalledTimes(1);

			expect(onAutocompleteItemSelected).toHaveBeenCalledWith({
				externalReferenceCode: 'group.1',
				id: '1',
				name: 'Group 1',
				numberOfUserAccounts: '5',
				roles: [],
			});
			expect(input).toHaveValue('Group 1');
		});
	});

	it('builds the apiURL with excludeMembers for users and exclude from UI', async () => {
		const excludeMembers = [
			{id: '123', name: 'Excluded User'},
		] as UserAccount[];

		await renderComponent({
			...DEFAULT_PROPS,
			excludeMembers,
		});

		await userEvent.click(
			screen.getByPlaceholderText('enter-name-or-email')
		);

		expect(
			await screen.findByRole('option', {
				name: /John Doe/,
			})
		).toBeInTheDocument();

		expect(
			screen.getByRole('option', {name: /Jane Smith/})
		).toBeInTheDocument();

		expect(
			screen.queryByRole('option', {name: /Excluded User/})
		).not.toBeInTheDocument();

		expect(mockFetch).toHaveBeenCalledWith(
			expect.stringContaining(`id+ne+%27123%27`)
		);
	});

	it('builds the apiURL with excludeMembers for groups and exclude from UI', async () => {
		const excludeMembers = [
			{id: '123', name: 'Excluded Group'},
		] as UserGroup[];

		await renderComponent({
			excludeMembers,
			selectValue: SelectOptions.GROUPS,
		});

		await userEvent.click(
			screen.getByPlaceholderText('enter-name-or-email')
		);

		expect(
			await screen.findByRole('option', {
				name: /Group 1/,
			})
		).toBeInTheDocument();

		expect(
			screen.getByRole('option', {name: /Group 2/})
		).toBeInTheDocument();

		expect(
			screen.queryByRole('option', {name: /Excluded Group/})
		).not.toBeInTheDocument();

		expect(mockFetch).toHaveBeenCalledWith(
			expect.stringContaining(`userGroupId+ne+%27123%27`)
		);
	});
});

/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, render, screen, waitFor, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {
	UserAccount,
	UserGroup,
} from '../../../../src/main/resources/META-INF/resources/js/common/types/UserAccount';
import {SPACE_MEMBER_ROLE_NAME} from '../../../../src/main/resources/META-INF/resources/js/main_view/spaces/SpaceMembersPermissionSelect';
import {SelectOptions} from '../../../../src/main/resources/META-INF/resources/js/main_view/spaces/SpaceMembersSelectOptions';
import {
	SpaceMembersWithList,
	SpaceMembersWithListProps,
} from '../../../../src/main/resources/META-INF/resources/js/main_view/spaces/SpaceMembersWithList';
import {useSpaceMembers} from '../../../../src/main/resources/META-INF/resources/js/main_view/spaces/hooks/useSpaceMembers';
import {createMockFetchMembersImplementation} from '../../__mocks__/createMockFetchMembersImplementation';
import {mockFetch} from '../../__mocks__/frontend-js-web';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/spaces/hooks/useSpaceMembers'
);

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as any),
	sub: (str: string, arg: string) => str.replace('x', arg),
}));

jest.mock('frontend-js-components-web', () => ({
	openToast: jest.fn(),
}));

describe('SpaceMembersWithList', () => {
	const testSpace = {
		externalReferenceCode: 'ERC',
		id: '123',
		name: 'Test Space',
	};

	const mockRoles = [
		{
			externalReferenceCode: '1',
			id: 100,
			name: SPACE_MEMBER_ROLE_NAME,
			name_i18n: {'en-US': 'Space Member'},
		},
		{
			externalReferenceCode: '2',
			id: 101,
			name: 'Role 1',
			name_i18n: {'en-US': 'Role 1'},
		},
	];

	const testUsers = [
		{
			emailAddress: 'john.doe@example.com',
			externalReferenceCode: 'ERC_1',
			id: '1',
			image: '/image/user_portrait',
			imageId: '1',
			name: 'John Doe',
			roles: [{id: 100, name: SPACE_MEMBER_ROLE_NAME}],
		},
		{
			emailAddress: 'jane.smith@example.com',
			externalReferenceCode: 'ERC_2',
			id: '2',
			image: '/image/user_portrait',
			imageId: '1',
			name: 'Jane Smith',
			roles: [{id: 100, name: SPACE_MEMBER_ROLE_NAME}],
		},
	] as UserAccount[];

	const testUserGroups = [
		{
			externalReferenceCode: 'ERC_1',
			id: '1',
			name: 'Group 1',
			roles: [{id: 100, name: SPACE_MEMBER_ROLE_NAME}],
		},
		{
			externalReferenceCode: 'ERC_2',
			id: '2',
			name: 'Group 2',
			roles: [{id: 100, name: SPACE_MEMBER_ROLE_NAME}],
		},
	] as UserGroup[];

	const allAvailableUsers = [
		...testUsers,
		{
			emailAddress: 'new@user.com',
			externalReferenceCode: 'ERC_3',
			id: '3',
			image: '/image/profile.jpg',
			imageId: '3.image.profile',
			name: 'New User',
			roles: [{id: 100, name: SPACE_MEMBER_ROLE_NAME}],
		},
	] as UserAccount[];

	const allAvailableGrups = [
		...testUserGroups,
		{
			externalReferenceCode: 'ERC_3',
			id: '3',
			name: 'New Group',
			roles: [{id: 100, name: SPACE_MEMBER_ROLE_NAME}],
		},
	] as UserGroup[];

	const props: SpaceMembersWithListProps = {
		assetLibraryCreatorUserId: testUsers[0].id,
		externalReferenceCode: testSpace.externalReferenceCode,
		hasAssignMembersPermission: true,
	};

	const mockUseSpaceMembers = useSpaceMembers as jest.Mock;
	const mockAddMember = jest.fn();
	const mockLoadMore = jest.fn();
	const mockRemoveMember = jest.fn();
	const mockUpdateMemberRoles = jest.fn();
	const mockSearch = jest.fn();

	const {ResizeObserver: ResizeObserverOriginal} = window;

	let intersectionObserverMock: jest.Mock;

	beforeEach(() => {
		mockFetch.mockClear();
		createMockFetchMembersImplementation({
			groups: allAvailableGrups,
			users: allAvailableUsers,
		});
		mockUseSpaceMembers.mockReturnValue({
			addMember: mockAddMember,
			loadMore: mockLoadMore,
			removeMember: mockRemoveMember,
			search: mockSearch,
			state: {
				groups: {items: testUserGroups, lastPage: 1},
				isFetching: false,
				isSearching: false,
				roles: mockRoles,
				users: {items: testUsers, lastPage: 1},
			},
			updateMemberRoles: mockUpdateMemberRoles,
		});

		intersectionObserverMock = jest.fn((callback) => {
			(intersectionObserverMock as any).mockCallback = callback;

			return {
				disconnect: jest.fn(),
				observe: jest.fn(),
				unobserve: jest.fn(),
			};
		});

		global.IntersectionObserver = intersectionObserverMock;
	});

	beforeAll(() => {
		window.ResizeObserver = jest.fn().mockImplementation(() => ({
			disconnect: jest.fn(),
			observe: jest.fn(),
			unobserve: jest.fn(),
		}));
	});

	afterEach(() => {
		jest.restoreAllMocks();
		mockFetch.mockClear();

		const alerts = document.body.querySelectorAll('[role="alert"]');
		alerts.forEach((alert) => alert.remove());
	});

	afterAll(() => {
		window.ResizeObserver = ResizeObserverOriginal;
		delete (global as any).IntersectionObserver;
	});

	it('lists users from a space', async () => {
		render(<SpaceMembersWithList {...props} />);

		await waitFor(() => {
			const usersList = screen.getByLabelText('who-has-access');
			const usersListItems = within(usersList).getAllByRole('listitem');
			expect(usersListItems).toHaveLength(testUsers.length);

			usersListItems.forEach((item, index) => {
				expect(item).toHaveTextContent(testUsers[index].name);
			});
		});
	});

	it('lists user groups from a space', async () => {
		render(<SpaceMembersWithList {...props} />);

		await userEvent.selectOptions(
			screen.getByRole('combobox', {name: 'add-people-to-collaborate'}),
			SelectOptions.GROUPS
		);

		const userGroupsList = screen.getByLabelText('who-has-access');
		expect(userGroupsList).toBeInTheDocument();

		await waitFor(() => {
			const userGroupsListItems =
				within(userGroupsList).getAllByRole('listitem');
			expect(userGroupsListItems).toHaveLength(testUserGroups.length);

			userGroupsListItems.forEach((item, index) => {
				expect(item).toHaveTextContent(testUserGroups[index].name);
			});
		});
	});

	it('loads more users when scrolling down', async () => {
		render(<SpaceMembersWithList {...props} />);

		await waitFor(() => {
			expect(
				within(screen.getByLabelText('who-has-access')).getAllByRole(
					'listitem'
				)
			).toHaveLength(testUsers.length);
		});

		act(() => {
			(intersectionObserverMock as any).mockCallback([
				{isIntersecting: true},
			]);
		});

		await waitFor(() => {
			expect(mockLoadMore).toHaveBeenCalledWith(SelectOptions.USERS);
		});
	});

	it('shows a "no members" message when the space is empty', async () => {
		mockUseSpaceMembers.mockReturnValue({
			...mockUseSpaceMembers(),
			state: {
				...mockUseSpaceMembers().state,
				groups: {items: []},
				users: {items: []},
			},
		});

		render(<SpaceMembersWithList {...props} />);

		await screen.findByText('no-members-yet');

		expect(
			screen.getByText('add-members-to-this-space')
		).toBeInTheDocument();

		await userEvent.selectOptions(
			screen.getByRole('combobox', {name: 'add-people-to-collaborate'}),
			SelectOptions.GROUPS
		);

		expect(screen.getByText('no-members-yet')).toBeInTheDocument();
		expect(
			screen.getByText('add-members-to-this-space')
		).toBeInTheDocument();
	});

	it('excludes added user members from the autocomplete list', async () => {
		render(<SpaceMembersWithList {...props} />);

		const input = screen.getByPlaceholderText('enter-name-or-email');

		await userEvent.click(input);

		await waitFor(() => {
			expect(mockFetch).toHaveBeenCalledWith(
				expect.stringContaining(`id+ne+%271%27+and+id+ne+%272%27`)
			);
		});

		await waitFor(() => {
			expect(
				screen.queryByRole('option', {name: /John Doe/})
			).not.toBeInTheDocument();
			expect(
				screen.queryByRole('option', {name: /Jane Smith/})
			).not.toBeInTheDocument();
			expect(
				screen.getByRole('option', {name: /New User/})
			).toBeInTheDocument();
		});
	});

	it('excludes added group members from the autocomplete list', async () => {
		render(<SpaceMembersWithList {...props} />);

		await userEvent.selectOptions(
			screen.getByRole('combobox', {
				name: 'add-people-to-collaborate',
			}),
			SelectOptions.GROUPS
		);

		const input = screen.getByPlaceholderText('enter-name-or-email');

		await userEvent.click(input);

		await waitFor(() => {
			expect(mockFetch).toHaveBeenCalledWith(
				expect.stringContaining(
					`userGroupId+ne+%271%27+and+userGroupId+ne+%272%27`
				)
			);
		});

		await waitFor(() => {
			expect(
				screen.queryByRole('option', {name: /Group 1/})
			).not.toBeInTheDocument();
			expect(
				screen.queryByRole('option', {name: /Group 2/})
			).not.toBeInTheDocument();
			expect(
				screen.getByRole('option', {name: /New Group/})
			).toBeInTheDocument();
		});
	});

	describe('Interactions', () => {
		it('calls addMember when an item is selected from autocomplete', async () => {
			render(<SpaceMembersWithList {...props} />);

			const input = screen.getByPlaceholderText('enter-name-or-email');

			await userEvent.type(input, 'New');

			await waitFor(() => {
				expect(
					screen.getByRole('option', {name: /New User/})
				).toBeInTheDocument();
			});

			await userEvent.click(
				screen.getByRole('option', {name: /New User/})
			);

			await waitFor(() => {
				const {_key, ...expectedValue} =
					allAvailableUsers[2] as (typeof allAvailableUsers)[2] & {
						_key: string;
					};

				expect(mockAddMember).toHaveBeenCalledWith(
					{...expectedValue, roles: []},
					SelectOptions.USERS
				);
			});
		});

		it('calls removeMember when the remove button is clicked', async () => {
			render(<SpaceMembersWithList {...props} />);

			const userItem = await screen.findByText(testUsers[1].name);
			const removeButton = within(userItem.closest('li')!).getByRole(
				'button',
				{name: /remove-user/}
			);
			await userEvent.click(removeButton);

			expect(mockRemoveMember).toHaveBeenCalledWith(
				testUsers[1],
				SelectOptions.USERS
			);
		});

		it('calls updateMemberRoles when a role for user is changed', async () => {
			render(<SpaceMembersWithList {...props} />);

			const userItem = await screen.findByText(testUsers[1].name);
			const permissionSelect = within(userItem.closest('li')!).getByRole(
				'button',
				{
					name: 'Space Member',
				}
			);

			await userEvent.click(permissionSelect);

			const roleCheckbox = await screen.findByLabelText('Role 1');
			await userEvent.click(roleCheckbox);

			await waitFor(() => {
				expect(mockUpdateMemberRoles).toHaveBeenCalledWith(
					testUsers[1],
					[SPACE_MEMBER_ROLE_NAME, 'Role 1'],
					SelectOptions.USERS
				);
			});
		});

		it('calls updateMemberRoles when a role for user group is changed', async () => {
			render(<SpaceMembersWithList {...props} />);

			await userEvent.selectOptions(
				screen.getByRole('combobox', {
					name: 'add-people-to-collaborate',
				}),
				SelectOptions.GROUPS
			);

			const groupItem = await screen.findByText(testUserGroups[0].name);
			const permissionSelect = within(groupItem.closest('li')!).getByRole(
				'button',
				{
					name: 'Space Member',
				}
			);

			await userEvent.click(permissionSelect);

			const menu = await screen.findByRole('menu');
			const roleCheckbox = await within(menu).findByLabelText('Role 1');
			await userEvent.click(roleCheckbox);

			await waitFor(() => {
				expect(mockUpdateMemberRoles).toHaveBeenCalledWith(
					testUserGroups[0],
					[SPACE_MEMBER_ROLE_NAME, 'Role 1'],
					SelectOptions.GROUPS
				);
			});
		});
	});

	describe('When hasAssignMembersPermission is false', () => {
		const propsWithNoPermission = {
			...props,
			hasAssignMembersPermission: false,
		};

		beforeEach(() => {
			mockSearch.mockClear();
		});

		it('renders a searcheable list instead of the add members input', () => {
			render(<SpaceMembersWithList {...propsWithNoPermission} />);

			expect(
				screen.queryByPlaceholderText('enter-name-or-email')
			).not.toBeInTheDocument();

			expect(
				screen.getByRole('textbox', {
					name: /search-for-name-or-email/i,
				})
			).toBeInTheDocument();
		});

		it('calls search when typing in the search input', async () => {
			jest.useFakeTimers();

			render(<SpaceMembersWithList {...propsWithNoPermission} />);

			const searchInput = screen.getByRole('textbox', {
				name: /search-for-name-or-email/i,
			});
			const searchText = 'test';

			await userEvent.type(searchInput, searchText, {delay: null});

			expect(mockSearch).not.toHaveBeenCalled();

			act(() => {
				jest.runAllTimers();
			});

			expect(mockSearch).toHaveBeenCalledWith(
				SelectOptions.USERS,
				searchText
			);

			jest.useRealTimers();
		});

		it('does not render the remove button for members', async () => {
			render(<SpaceMembersWithList {...propsWithNoPermission} />);

			await waitFor(() => {
				expect(screen.getByText(testUsers[1].name)).toBeInTheDocument();
			});

			expect(
				screen.queryByRole('button', {name: /remove/i})
			).not.toBeInTheDocument();
		});
	});
});

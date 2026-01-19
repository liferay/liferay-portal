/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, renderHook, waitFor} from '@testing-library/react';
import {openToast} from 'frontend-js-components-web';

import AdminUserService from '../../../../../src/main/resources/META-INF/resources/js/common/services/AdminUserService';
import SpaceService from '../../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService';
import {
	UserAccount,
	UserGroup,
} from '../../../../../src/main/resources/META-INF/resources/js/common/types/UserAccount';
import {SPACE_MEMBER_ROLE_NAME} from '../../../../../src/main/resources/META-INF/resources/js/main_view/spaces/SpaceMembersPermissionSelect';
import {SelectOptions} from '../../../../../src/main/resources/META-INF/resources/js/main_view/spaces/SpaceMembersSelectOptions';
import {useSpaceMembers} from '../../../../../src/main/resources/META-INF/resources/js/main_view/spaces/hooks/useSpaceMembers';

jest.mock('frontend-js-components-web', () => ({
	openToast: jest.fn(),
}));

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/common/services/AdminUserService'
);

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService'
);

const mockedOpenToast = openToast as jest.Mock;

describe('useSpaceMembers', () => {
	const externalReferenceCode = '123';
	const pageSize = 20;

	const testUsers = [
		{
			emailAddress: 'john.doe@example.com',
			externalReferenceCode: '1',
			id: '1',
			image: '/image/user_portrait',
			imageId: '1',
			name: 'John Doe',
			roles: [{id: 100, name: SPACE_MEMBER_ROLE_NAME}],
		},
		{
			emailAddress: 'jane.smith@example.com',
			externalReferenceCode: '2',
			id: '2',
			image: '/image/user_portrait',
			imageId: '1',
			name: 'Jane Smith',
			roles: [{id: 100, name: SPACE_MEMBER_ROLE_NAME}],
		},
	] as UserAccount[];

	const newUser = {
		emailAddress: 'user3@example.com',
		externalReferenceCode: '3',
		id: '3',
		image: '/image/user_portrait',
		imageId: '3',
		name: 'User 3',
		roles: [{id: 100, name: SPACE_MEMBER_ROLE_NAME}],
	} as UserAccount;

	const newGroup = {
		externalReferenceCode: '3',
		id: '3',
		name: 'Group 3',
		roles: [{id: 100, name: SPACE_MEMBER_ROLE_NAME}],
	} as UserGroup;

	const testUserGroups = [
		{
			externalReferenceCode: '1',
			id: '1',
			name: 'Group 1',
			roles: [{id: 100, name: SPACE_MEMBER_ROLE_NAME}],
		},
		{
			externalReferenceCode: '2',
			id: '2',
			name: 'Group 2',
			roles: [{id: 100, name: SPACE_MEMBER_ROLE_NAME}],
		},
	] as UserGroup[];

	const testRoles = [
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

	const mockUsers = {
		items: testUsers,
		lastPage: 3,
		page: 1,
		totalCount: testUsers.length,
	};

	const mockGroups = {
		items: testUserGroups,
		lastPage: 3,
		page: 1,
		totalCount: testUserGroups.length,
	};

	const mockRoles = {
		items: testRoles,
		lastPage: 1,
		page: 1,
		pageSize: 1,
		totalCount: testRoles.length,
	};

	let getSpaceUsersSpy: jest.SpyInstance;
	let getSpaceUserGroupsSpy: jest.SpyInstance;
	let getUserRolesSpy: jest.SpyInstance;

	beforeEach(() => {
		getSpaceUsersSpy = jest
			.spyOn(SpaceService, 'getSpaceUsers')
			.mockResolvedValue(mockUsers);
		getSpaceUserGroupsSpy = jest
			.spyOn(SpaceService, 'getSpaceUserGroups')
			.mockResolvedValue(mockGroups);
		getUserRolesSpy = jest
			.spyOn(AdminUserService, 'getUserRoles')
			.mockResolvedValue(mockRoles);
	});

	afterEach(() => {
		jest.clearAllMocks();
		mockedOpenToast.mockClear();
	});

	it('fetches initial members, groups, and roles on mount', async () => {
		const {result} = renderHook(() =>
			useSpaceMembers(externalReferenceCode, pageSize)
		);

		expect(result.current.state.isFetching).toBe(true);

		await waitFor(() => {
			expect(getSpaceUsersSpy).toHaveBeenCalledWith(
				expect.objectContaining({
					externalReferenceCode,
					page: 1,
				})
			);
			expect(getSpaceUserGroupsSpy).toHaveBeenCalledWith(
				expect.objectContaining({
					externalReferenceCode,
					page: 1,
				})
			);
			expect(getUserRolesSpy).toHaveBeenCalled();
		});

		await waitFor(() => {
			expect(result.current.state.isFetching).toBe(false);
			expect(result.current.state.users.items).toEqual(mockUsers.items);
			expect(result.current.state.groups.items).toEqual(mockGroups.items);
			expect(result.current.state.roles).toEqual(mockRoles.items);
		});
	});

	it('handles initial fetch error', async () => {
		const error = new Error('Fetch failed');
		getSpaceUsersSpy.mockRejectedValue(error);

		const {result} = renderHook(() =>
			useSpaceMembers(externalReferenceCode, pageSize)
		);

		await waitFor(() => {
			expect(result.current.state.isFetching).toBe(false);
			expect(result.current.state.error).toBe(error);
		});
	});

	describe.each([
		[SelectOptions.USERS, newUser],
		[SelectOptions.GROUPS, newGroup],
	])('loadMore', (type, newItem) => {
		it(`for ${type}`, async () => {
			const getSpy =
				type === SelectOptions.USERS
					? getSpaceUsersSpy
					: getSpaceUserGroupsSpy;
			const moreItems = {
				items: [newItem],
			};

			getSpy.mockResolvedValueOnce(
				type === SelectOptions.USERS ? mockUsers : mockGroups
			);
			getSpy.mockResolvedValueOnce(moreItems);

			const {result} = renderHook(() =>
				useSpaceMembers(externalReferenceCode, pageSize)
			);

			await waitFor(() =>
				expect(result.current.state.isFetching).toBe(false)
			);

			await act(async () => {
				await result.current.loadMore(type);
			});

			expect(getSpy).toHaveBeenCalledTimes(2);
			expect(getSpy).toHaveBeenLastCalledWith(
				expect.objectContaining({page: 2})
			);

			await waitFor(() => {
				const items = result.current.state[type].items;
				expect(items).toHaveLength(3);
				expect(items[2].name).toBe(newItem.name);
			});
		});
	});

	it('handles load more error', async () => {
		const error = new Error('Load more failed');
		getSpaceUsersSpy.mockResolvedValueOnce(mockUsers);
		getSpaceUsersSpy.mockRejectedValueOnce(error);

		const {result} = renderHook(() =>
			useSpaceMembers(externalReferenceCode, pageSize)
		);

		await waitFor(() =>
			expect(result.current.state.isFetching).toBe(false)
		);

		await act(async () => {
			await result.current.loadMore(SelectOptions.USERS);
		});

		await waitFor(() => {
			expect(result.current.state.error).toBe(error);
		});
	});

	it('does not load more if on the last page', async () => {
		getSpaceUserGroupsSpy.mockResolvedValue({...mockGroups, lastPage: 1});

		const {result} = renderHook(() =>
			useSpaceMembers(externalReferenceCode, pageSize)
		);

		await waitFor(() =>
			expect(result.current.state.isFetching).toBe(false)
		);

		await act(async () => result.current.loadMore(SelectOptions.GROUPS));
		expect(getSpaceUserGroupsSpy).toHaveBeenCalledTimes(1);
	});

	describe('search', () => {
		it('searches for users with a keyword', async () => {
			const {result} = renderHook(() =>
				useSpaceMembers(externalReferenceCode, pageSize)
			);
			await waitFor(() =>
				expect(result.current.state.isFetching).toBe(false)
			);

			const searchKeywords = 'John';
			const searchResults = {items: [testUsers[0]], lastPage: 1};
			getSpaceUsersSpy.mockResolvedValue(searchResults);

			await act(async () => {
				await result.current.search(
					SelectOptions.USERS,
					searchKeywords
				);
			});

			expect(getSpaceUsersSpy).toHaveBeenLastCalledWith(
				expect.objectContaining({
					keywords: searchKeywords,
					page: 1,
				})
			);

			await waitFor(() => {
				expect(result.current.state.isSearching).toBe(false);
			});

			expect(result.current.state.users.items).toEqual(
				searchResults.items
			);
			expect(result.current.state.keywords).toBe(searchKeywords);
		});

		it('searches for groups with a keyword', async () => {
			const {result} = renderHook(() =>
				useSpaceMembers(externalReferenceCode, pageSize)
			);
			await waitFor(() =>
				expect(result.current.state.isFetching).toBe(false)
			);

			const searchKeywords = 'Group';
			const searchResults = {items: [testUserGroups[0]], lastPage: 1};
			getSpaceUserGroupsSpy.mockResolvedValue(searchResults);

			await act(async () => {
				await result.current.search(
					SelectOptions.GROUPS,
					searchKeywords
				);
			});

			expect(getSpaceUserGroupsSpy).toHaveBeenLastCalledWith(
				expect.objectContaining({
					keywords: searchKeywords,
					page: 1,
				})
			);

			await waitFor(() => {
				expect(result.current.state.isSearching).toBe(false);
			});

			expect(result.current.state.groups.items).toEqual(
				searchResults.items
			);
			expect(result.current.state.keywords).toBe(searchKeywords);
		});

		it('clears search when keyword is empty', async () => {
			const {result} = renderHook(() =>
				useSpaceMembers(externalReferenceCode, pageSize)
			);
			await waitFor(() =>
				expect(result.current.state.isFetching).toBe(false)
			);

			expect(getSpaceUsersSpy).toHaveBeenCalledTimes(1);
			expect(getSpaceUserGroupsSpy).toHaveBeenCalledTimes(1);

			const searchKeyword = 'John';
			getSpaceUsersSpy.mockResolvedValue({
				items: [testUsers[0]],
				lastPage: 1,
			});
			await act(async () => {
				await result.current.search(SelectOptions.USERS, searchKeyword);
			});
			expect(getSpaceUsersSpy).toHaveBeenCalledTimes(2);

			getSpaceUsersSpy.mockResolvedValue(mockUsers);
			getSpaceUserGroupsSpy.mockResolvedValue(mockGroups);

			await act(async () => {
				await result.current.search(SelectOptions.USERS, '');
			});

			expect(getSpaceUsersSpy).toHaveBeenCalledTimes(3);
			expect(getSpaceUserGroupsSpy).toHaveBeenCalledTimes(2);

			await waitFor(() => {
				expect(result.current.state.users.items).toEqual(
					mockUsers.items
				);
			});
			expect(result.current.state.keywords).toBe('');
		});

		it('handles search error', async () => {
			const {result} = renderHook(() =>
				useSpaceMembers(externalReferenceCode, pageSize)
			);
			await waitFor(() =>
				expect(result.current.state.isFetching).toBe(false)
			);

			const error = new Error('Search failed');
			getSpaceUsersSpy.mockRejectedValue(error);

			await act(async () => {
				await result.current.search(
					SelectOptions.USERS,
					'some-keyword'
				);
			});

			await waitFor(() => {
				expect(result.current.state.error).toBe(error);
				expect(result.current.state.isSearching).toBe(false);
			});
		});
	});

	describe('addMember', () => {
		describe.each([
			[SelectOptions.USERS, newUser, 'linkUserToSpace'],
			[SelectOptions.GROUPS, newGroup, 'linkUserGroupToSpace'],
		])('for %s', (type, newItem, serviceMethod) => {
			it(`optimistically adds a ${type} and shows a success toast`, async () => {
				const linkSpy = jest
					.spyOn(SpaceService, serviceMethod as any)
					.mockResolvedValue({data: {}, error: null});

				const {result} = renderHook(() =>
					useSpaceMembers(externalReferenceCode, pageSize)
				);

				await waitFor(() =>
					expect(result.current.state.isFetching).toBe(false)
				);

				await act(async () => {
					await result.current.addMember(newItem, type);
				});

				expect(result.current.state[type].items[0].name).toBe(
					newItem.name
				);

				const externalReferenceCodeKey =
					type === SelectOptions.USERS
						? 'userExternalReferenceCode'
						: 'userGroupExternalReferenceCode';

				await waitFor(() => {
					expect(linkSpy).toHaveBeenCalledWith(
						expect.objectContaining({
							[externalReferenceCodeKey]:
								newItem.externalReferenceCode,
							spaceExternalReferenceCode: externalReferenceCode,
						})
					);
				});

				expect(mockedOpenToast).toHaveBeenCalledWith(
					expect.objectContaining({type: 'success'})
				);
			});

			it('rolls back the state and shows an error toast on failure', async () => {
				jest.spyOn(
					SpaceService,
					serviceMethod as any
				).mockResolvedValue({
					data: null,
					error: 'API Error',
				});

				const {result} = renderHook(() =>
					useSpaceMembers(externalReferenceCode, pageSize)
				);

				await waitFor(() =>
					expect(result.current.state.isFetching).toBe(false)
				);

				let addMemberPromise: Promise<void>;

				act(() => {
					addMemberPromise = result.current.addMember(newItem, type);
				});

				expect(result.current.state[type].items[0].name).toBe(
					newItem.name
				);

				await act(async () => {
					await addMemberPromise;
				});

				expect(mockedOpenToast).toHaveBeenCalledWith(
					expect.objectContaining({type: 'danger'})
				);

				const items = result.current.state[type].items;
				const originalFirstItemName =
					type === SelectOptions.USERS
						? testUsers[0].name
						: testUserGroups[0].name;

				expect(items).toHaveLength(2);
				expect(items[0].name).toBe(originalFirstItemName);
			});
		});

		it('does not add a member if they already exist', async () => {
			const {result} = renderHook(() =>
				useSpaceMembers(externalReferenceCode, pageSize)
			);

			await waitFor(() =>
				expect(result.current.state.isFetching).toBe(false)
			);

			const existingUser = testUsers[0];

			await act(async () => {
				await result.current.addMember(
					existingUser,
					SelectOptions.USERS
				);
			});

			expect(result.current.state.users.items).toHaveLength(2);

			const existingGroup = testUserGroups[0];

			await act(async () => {
				await result.current.addMember(
					existingGroup,
					SelectOptions.GROUPS
				);
			});

			expect(result.current.state.groups.items).toHaveLength(2);
		});
	});

	describe('removeMember', () => {
		describe.each([
			[
				SelectOptions.USERS,
				mockUsers.items[0],
				'Jane Smith',
				'unlinkUserFromSpace',
			],
			[
				SelectOptions.GROUPS,
				mockGroups.items[0],
				'Group 2',
				'unlinkUserGroupFromSpace',
			],
		])('for %s', (type, itemToRemove, remainingItemName, serviceMethod) => {
			it(`optimistically removes a ${type} and shows a success toast`, async () => {
				const unlinkSpy = jest
					.spyOn(SpaceService, serviceMethod as any)
					.mockResolvedValue({data: null, error: null});

				const {result} = renderHook(() =>
					useSpaceMembers(externalReferenceCode, pageSize)
				);

				await waitFor(() =>
					expect(result.current.state.isFetching).toBe(false)
				);

				await act(async () => {
					await result.current.removeMember(itemToRemove, type);
				});

				const items = result.current.state[type].items;
				expect(items).toHaveLength(1);
				expect(items[0].name).toBe(remainingItemName);

				const externalReferenceCodeKey =
					type === SelectOptions.USERS
						? 'userExternalReferenceCode'
						: 'userGroupExternalReferenceCode';

				await waitFor(() => {
					expect(unlinkSpy).toHaveBeenCalledWith(
						expect.objectContaining({
							[externalReferenceCodeKey]:
								itemToRemove.externalReferenceCode,
							spaceExternalReferenceCode: externalReferenceCode,
						})
					);
				});

				expect(mockedOpenToast).toHaveBeenCalledWith(
					expect.objectContaining({type: 'success'})
				);
			});

			it('rolls back the state and shows an error toast on failure', async () => {
				const unlinkSpy = jest
					.spyOn(SpaceService, serviceMethod as any)
					.mockResolvedValue({data: null, error: 'API Error'});

				const {result} = renderHook(() =>
					useSpaceMembers(externalReferenceCode, pageSize)
				);

				await waitFor(() =>
					expect(result.current.state.isFetching).toBe(false)
				);

				await act(async () => {
					await result.current.removeMember(itemToRemove, type);
				});

				await waitFor(() => expect(unlinkSpy).toHaveBeenCalled());

				expect(mockedOpenToast).toHaveBeenCalledWith(
					expect.objectContaining({type: 'danger'})
				);

				const items = result.current.state[type].items;
				expect(items).toHaveLength(2);
				expect(
					(items as (UserAccount | UserGroup)[]).find(
						(item) => item.id === itemToRemove.id
					)
				).toBe(itemToRemove);
			});
		});
	});

	describe('updateMemberRoles', () => {
		describe.each([
			[SelectOptions.USERS, mockUsers.items[0], 'updateUserRoles'],
			[SelectOptions.GROUPS, mockGroups.items[0], 'updateUserGroupRoles'],
		])('for %s', (type, itemToUpdate, serviceMethod) => {
			it(`optimistically updates ${type} roles and shows a success toast`, async () => {
				const updateSpy = jest
					.spyOn(SpaceService, serviceMethod as any)
					.mockResolvedValue({data: {}, error: null});
				const newRoles = ['Role 1'];

				const {result} = renderHook(() =>
					useSpaceMembers(externalReferenceCode, pageSize)
				);

				await waitFor(() =>
					expect(result.current.state.isFetching).toBe(false)
				);

				await act(async () => {
					await result.current.updateMemberRoles(
						itemToUpdate,
						newRoles,
						type
					);
				});

				expect(result.current.state[type].items[0].roles).toMatchObject(
					[{id: 101, name: 'Role 1'}]
				);

				const externalReferenceCodeKey =
					type === SelectOptions.USERS
						? 'userExternalReferenceCode'
						: 'userGroupExternalReferenceCode';

				await waitFor(() => {
					expect(updateSpy).toHaveBeenCalledWith({
						[externalReferenceCodeKey]:
							itemToUpdate.externalReferenceCode,
						roleNames: newRoles,
						spaceExternalReferenceCode: externalReferenceCode,
					});
				});

				expect(mockedOpenToast).toHaveBeenCalledWith(
					expect.objectContaining({type: 'success'})
				);
			});

			it(`rolls back ${type} roles and shows an error toast on failure`, async () => {
				const updateSpy = jest
					.spyOn(SpaceService, serviceMethod as any)
					.mockResolvedValue({data: null, error: 'API Error'});

				const {result} = renderHook(() =>
					useSpaceMembers(externalReferenceCode, pageSize)
				);

				await waitFor(() =>
					expect(result.current.state.isFetching).toBe(false)
				);

				await act(async () => {
					await result.current.updateMemberRoles(
						itemToUpdate,
						[...itemToUpdate.roles.map((role) => role.name)],
						type
					);
				});

				await waitFor(() => expect(updateSpy).toHaveBeenCalled());

				expect(mockedOpenToast).toHaveBeenCalledWith(
					expect.objectContaining({type: 'danger'})
				);

				expect(result.current.state[type].items[0].roles).toHaveLength(
					1
				);
			});
		});
	});
});

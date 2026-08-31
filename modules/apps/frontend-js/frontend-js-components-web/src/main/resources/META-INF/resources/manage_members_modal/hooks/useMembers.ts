/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useEffect, useReducer} from 'react';

import * as membersService from '../services/membersService';
import {
	MemberType,
	MembersConfig,
	Role,
	RoleExternalReferenceCode,
	UserAccount,
	UserGroup,
} from '../types';
import {ActionTypes, initialState, reducer} from './membersReducer';
import {runOptimisticMutation} from './runOptimisticMutation';

export function useMembers(
	config: MembersConfig,
	externalReferenceCode: string,
	pageSize: number,
	onChange?: () => void
) {
	const [state, dispatch] = useReducer(reducer, initialState);

	const fetchInitialData = useCallback(async () => {
		dispatch({type: ActionTypes.FetchStart});

		try {
			const [users, groups, allRoles] = await Promise.all([
				membersService.getMembers({
					externalReferenceCode,
					nestedFields: 'roles',
					page: 1,
					pageSize,
				}),
				membersService.getMemberGroups({
					externalReferenceCode,
					nestedFields: 'numberOfUserAccounts,roles',
					page: 1,
					pageSize,
				}),
				membersService.getRoles({
					externalReferenceCode,
					fields: ['externalReferenceCode', 'id', 'name'].join(','),
				}),
			]);

			const excludedRoleExternalReferenceCodes =
				config.excludedRoleExternalReferenceCodes ?? [];

			dispatch({
				payload: {
					groups,
					roles: allRoles.items.filter(
						(role) =>
							!excludedRoleExternalReferenceCodes.includes(
								role.externalReferenceCode as RoleExternalReferenceCode
							)
					),
					users,
				},
				type: ActionTypes.FetchSuccess,
			});
		}
		catch (error) {
			console.error(error);
			dispatch({
				payload: error as Error,
				type: ActionTypes.FetchError,
			});
		}
	}, [
		config.excludedRoleExternalReferenceCodes,
		externalReferenceCode,
		pageSize,
	]);

	useEffect(() => {
		fetchInitialData();
	}, [fetchInitialData]);

	const fetchPage = useCallback(
		(type: MemberType, page: number, keywords: string) => {
			const isUser = type === MemberType.USERS;

			const params = {
				externalReferenceCode,
				keywords,
				nestedFields: isUser ? 'roles' : 'numberOfUserAccounts,roles',
				page,
				pageSize,
			};

			return isUser
				? membersService.getMembers(params)
				: membersService.getMemberGroups(params);
		},
		[externalReferenceCode, pageSize]
	);

	const loadMore = useCallback(
		async (type: MemberType) => {
			if (state.isFetching) {
				return;
			}

			const isUser = type === MemberType.USERS;
			const currentState = isUser ? state.users : state.groups;
			const newPage = currentState.page + 1;

			if (newPage > currentState.lastPage) {
				return;
			}

			dispatch({type: ActionTypes.LoadMoreStart});

			try {
				const {items} = await fetchPage(type, newPage, state.keywords);

				dispatch({
					payload: {items, page: newPage, type},
					type: ActionTypes.LoadMoreSuccess,
				});
			}
			catch (error) {
				dispatch({
					payload: error as Error,
					type: ActionTypes.LoadMoreError,
				});
			}
		},
		[fetchPage, state.groups, state.isFetching, state.keywords, state.users]
	);

	const search = useCallback(
		async (type: MemberType, keywords: string) => {
			if (state.isSearching) {
				return;
			}

			dispatch({payload: keywords, type: ActionTypes.SetKeywords});

			if (!keywords) {
				fetchInitialData();

				return;
			}

			dispatch({payload: {type}, type: ActionTypes.SearchStart});

			try {
				const {items, lastPage} = await fetchPage(type, 1, keywords);

				dispatch({
					payload: {items, lastPage, type},
					type: ActionTypes.SearchSuccess,
				});
			}
			catch (error) {
				dispatch({
					payload: error as Error,
					type: ActionTypes.SearchError,
				});
			}
		},
		[fetchInitialData, fetchPage, state.isSearching]
	);

	const addMember = useCallback(
		async (item: UserAccount | UserGroup, type: MemberType) => {
			const isUser = type === MemberType.USERS;
			const items = isUser ? state.users.items : state.groups.items;

			if (items.some((existingItem) => existingItem.id === item.id)) {
				return;
			}

			await runOptimisticMutation(dispatch, {
				errorMessage: isUser
					? config.messages.addUserError
					: config.messages.addGroupError,
				name: item.name,
				onSuccess: onChange,
				optimisticAction: {
					payload: {item: {...item, roles: []}, type},
					type: ActionTypes.AddMemberSuccess,
				},
				performMutation: () => {
					return isUser
						? membersService.linkMember({
								externalReferenceCode,
								memberExternalReferenceCode:
									item.externalReferenceCode,
							})
						: membersService.linkMemberGroup({
								externalReferenceCode,
								memberGroupExternalReferenceCode:
									item.externalReferenceCode,
							});
				},
				rollbackAction: {
					payload: {id: item.id, type},
					type: ActionTypes.AddMemberFailure,
				},
				successMessage: isUser
					? config.messages.addUserSuccess
					: config.messages.addGroupSuccess,
			});
		},
		[
			config.messages,
			externalReferenceCode,
			onChange,
			state.groups.items,
			state.users.items,
		]
	);

	const removeMember = useCallback(
		async (item: UserAccount | UserGroup, type: MemberType) => {
			const isUser = type === MemberType.USERS;

			await runOptimisticMutation(dispatch, {
				errorMessage: isUser
					? config.messages.removeUserError
					: config.messages.removeGroupError,
				name: item.name,
				onSuccess: onChange,
				optimisticAction: {
					payload: {id: item.id, type},
					type: ActionTypes.RemoveMemberSuccess,
				},
				performMutation: () => {
					return isUser
						? membersService.unlinkMember({
								externalReferenceCode,
								memberExternalReferenceCode:
									item.externalReferenceCode,
							})
						: membersService.unlinkMemberGroup({
								externalReferenceCode,
								memberGroupExternalReferenceCode:
									item.externalReferenceCode,
							});
				},
				rollbackAction: {
					payload: {item, type},
					type: ActionTypes.RemoveMemberFailure,
				},
				successMessage: isUser
					? config.messages.removeUserSuccess
					: config.messages.removeGroupSuccess,
			});
		},
		[config.messages, externalReferenceCode, onChange]
	);

	const updateMemberRoles = useCallback(
		async (
			itemToUpdate: UserAccount | UserGroup,
			newRoles: string[],
			type: MemberType,
			showSuccessToast = true
		) => {
			const isUser = type === MemberType.USERS;
			const originalRoles = itemToUpdate.roles;

			const newRoleObjects = state.roles.filter((role) => {
				return newRoles.includes(role.name);
			});

			await runOptimisticMutation(dispatch, {
				errorMessage: isUser
					? config.messages.updateUserError
					: config.messages.updateGroupError,
				name: itemToUpdate.name,
				onSuccess: onChange,
				optimisticAction: {
					payload: {id: itemToUpdate.id, roles: newRoleObjects},
					type: ActionTypes.UpdateRolesSuccess,
				},
				performMutation: () => {
					return isUser
						? membersService.updateMemberRoles({
								externalReferenceCode,
								memberExternalReferenceCode:
									itemToUpdate.externalReferenceCode,
								roleNames: newRoles,
							})
						: membersService.updateMemberGroupRoles({
								externalReferenceCode,
								memberGroupExternalReferenceCode:
									itemToUpdate.externalReferenceCode,
								roleNames: newRoles,
							});
				},
				rollbackAction: {
					payload: {
						id: itemToUpdate.id,
						originalRoles: (originalRoles || [])
							.map((originalRole) => {
								return state.roles.find(
									(role) => role.id === originalRole.id
								);
							})
							.filter((role): role is Role => Boolean(role)),
					},
					type: ActionTypes.UpdateRolesFailure,
				},
				successMessage: showSuccessToast
					? config.messages.updateSuccess
					: undefined,
			});
		},
		[config.messages, externalReferenceCode, onChange, state.roles]
	);

	return {
		addMember,
		loadMore,
		removeMember,
		search,
		state,
		updateMemberRoles,
	};
}

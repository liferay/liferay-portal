/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import {useCallback, useEffect, useReducer} from 'react';

import AdminUserService from '../../../common/services/AdminUserService';
import SpaceService from '../../../common/services/SpaceService';
import {Role} from '../../../common/types/Role';
import {UserAccount, UserGroup} from '../../../common/types/UserAccount';
import {SelectOptions} from '../SpaceMembersSelectOptions';

enum ActionTypes {
	FetchStart = 'FETCH_START',
	FetchSuccess = 'FETCH_SUCCESS',
	FetchError = 'FETCH_ERROR',
	LoadMoreStart = 'LOAD_MORE_START',
	LoadMoreSuccess = 'LOAD_MORE_SUCCESS',
	LoadMoreError = 'LOAD_MORE_ERROR',
	AddMemberStart = 'ADD_MEMBER_START',
	AddMemberSuccess = 'ADD_MEMBER_SUCCESS',
	AddMemberFailure = 'ADD_MEMBER_FAILURE',
	AddMemberError = 'ADD_MEMBER_ERROR',
	RemoveMemberSuccess = 'REMOVE_MEMBER_SUCCESS',
	RemoveMemberFailure = 'REMOVE_MEMBER_FAILURE',
	UpdateRolesSuccess = 'UPDATE_ROLES_SUCCESS',
	UpdateRolesFailure = 'UPDATE_ROLES_FAILURE',
	SearchStart = 'SEARCH_START',
	SearchSuccess = 'SEARCH_SUCCESS',
	SearchError = 'SEARCH_ERROR',
	SetKeywords = 'SET_KEYWORDS',
}

interface State {
	error: Error | null;
	groups: {
		items: UserGroup[];
		lastPage: number;
		page: number;
	};
	isFetching: boolean;
	isSearching: boolean;
	keywords: string;
	roles: Role[];
	users: {
		items: UserAccount[];
		lastPage: number;
		page: number;
	};
}

type Action =
	| {
			type:
				| ActionTypes.FetchStart
				| ActionTypes.LoadMoreStart
				| ActionTypes.AddMemberStart;
	  }
	| {
			payload: {
				groups: {items: UserGroup[]; lastPage: number};
				roles: Role[];
				users: {items: UserAccount[]; lastPage: number};
			};
			type: ActionTypes.FetchSuccess;
	  }
	| {
			payload: Error;
			type:
				| ActionTypes.FetchError
				| ActionTypes.LoadMoreError
				| ActionTypes.AddMemberError
				| ActionTypes.SearchError;
	  }
	| {
			payload: {item: UserAccount | UserGroup; type: SelectOptions};
			type: ActionTypes.AddMemberSuccess;
	  }
	| {
			payload: {id: number | string; type: SelectOptions};
			type: ActionTypes.AddMemberFailure;
	  }
	| {
			payload: {id: number | string; type: SelectOptions};
			type: ActionTypes.RemoveMemberSuccess;
	  }
	| {
			payload: {item: UserAccount | UserGroup; type: SelectOptions};
			type: ActionTypes.RemoveMemberFailure;
	  }
	| {
			payload: {id: number | string; roles: Role[]};
			type: ActionTypes.UpdateRolesSuccess;
	  }
	| {
			payload: {id: number | string; originalRoles: Role[]};
			type: ActionTypes.UpdateRolesFailure;
	  }
	| {
			payload: {
				items: (UserAccount | UserGroup)[];
				page: number;
				type: SelectOptions;
			};
			type: ActionTypes.LoadMoreSuccess;
	  }
	| {
			payload: {
				type: SelectOptions;
			};
			type: ActionTypes.SearchStart;
	  }
	| {
			payload: {
				items: (UserAccount | UserGroup)[];
				lastPage: number;
				type: SelectOptions;
			};
			type: ActionTypes.SearchSuccess;
	  }
	| {payload: string; type: ActionTypes.SetKeywords};

const initialState: State = {
	error: null,
	groups: {
		items: [],
		lastPage: 1,
		page: 1,
	},
	isFetching: false,
	isSearching: false,
	keywords: '',
	roles: [],
	users: {
		items: [],
		lastPage: 1,
		page: 1,
	},
};

function reducer(state: State, action: Action): State {
	switch (action.type) {
		case ActionTypes.FetchStart:
		case ActionTypes.LoadMoreStart:
		case ActionTypes.AddMemberStart:
			return {...state, error: null, isFetching: true};
		case ActionTypes.FetchSuccess:
			return {
				...state,
				groups: {
					...state.groups,
					items: action.payload.groups.items,
					lastPage: action.payload.groups.lastPage,
				},
				isFetching: false,
				roles: action.payload.roles,
				users: {
					...state.users,
					items: action.payload.users.items,
					lastPage: action.payload.users.lastPage,
				},
			};
		case ActionTypes.AddMemberSuccess: {
			const {item, type} = action.payload;

			const key = type === SelectOptions.USERS ? 'users' : 'groups';

			return {
				...state,
				isFetching: false,
				[key]: {
					...state[key],
					items: [item, ...state[key].items],
				},
			};
		}
		case ActionTypes.AddMemberFailure: {
			const {id, type} = action.payload;

			const key = type === SelectOptions.USERS ? 'users' : 'groups';

			return {
				...state,
				isFetching: false,
				[key]: {
					...state[key],
					items: (
						state[key].items as (UserAccount | UserGroup)[]
					).filter((item) => item.id !== id),
				},
			};
		}
		case ActionTypes.RemoveMemberSuccess: {
			const {id, type} = action.payload;

			const key = type === SelectOptions.USERS ? 'users' : 'groups';

			return {
				...state,
				[key]: {
					...state[key],
					items: (
						state[key].items as (UserAccount | UserGroup)[]
					).filter((item) => item.id !== id),
				},
			};
		}
		case ActionTypes.RemoveMemberFailure: {
			const {item, type} = action.payload;

			const key = type === SelectOptions.USERS ? 'users' : 'groups';

			return {
				...state,
				[key]: {
					...state[key],
					items: [...state[key].items, item],
				},
			};
		}
		case ActionTypes.LoadMoreSuccess: {
			const {items, page, type} = action.payload;

			const key = type === SelectOptions.USERS ? 'users' : 'groups';

			return {
				...state,
				isFetching: false,
				[key]: {
					...state[key],
					items: [...state[key].items, ...items],
					page,
				},
			};
		}
		case ActionTypes.UpdateRolesSuccess:
			return {
				...state,
				groups: {
					...state.groups,
					items: state.groups.items.map((item) =>
						item.id === action.payload.id
							? {...item, roles: action.payload.roles}
							: item
					),
				},
				users: {
					...state.users,
					items: state.users.items.map((item) =>
						item.id === action.payload.id
							? {...item, roles: action.payload.roles}
							: item
					),
				},
			};
		case ActionTypes.UpdateRolesFailure: {
			const {id, originalRoles} = action.payload;

			return {
				...state,
				groups: {
					...state.groups,
					items: state.groups.items.map((item) =>
						item.id === id ? {...item, roles: originalRoles} : item
					),
				},
				users: {
					...state.users,
					items: state.users.items.map((item) =>
						item.id === action.payload.id
							? {...item, roles: originalRoles}
							: item
					),
				},
			};
		}
		case ActionTypes.FetchError:
		case ActionTypes.LoadMoreError:
		case ActionTypes.AddMemberError:
			return {...state, error: action.payload, isFetching: false};
		case ActionTypes.SetKeywords:
			return {
				...state,
				keywords: action.payload,
			};
		case ActionTypes.SearchStart: {
			const {type} = action.payload;
			const key = type === SelectOptions.USERS ? 'users' : 'groups';

			return {
				...state,
				isSearching: true,
				[key]: {
					...state[key],
					items: [],
					page: 1,
				},
			};
		}
		case ActionTypes.SearchSuccess: {
			const {items, lastPage, type} = action.payload;

			const key = type === SelectOptions.USERS ? 'users' : 'groups';

			return {
				...state,
				isSearching: false,
				[key]: {
					...state[key],
					items,
					lastPage,
					page: 1,
				},
			};
		}
		case ActionTypes.SearchError:
			return {...state, error: action.payload, isSearching: false};
		default:
			return state;
	}
}

export function useSpaceMembers(
	externalReferenceCode: string,
	pageSize: number
) {
	const [state, dispatch] = useReducer(reducer, initialState);

	const fetchInitialData = useCallback(async () => {
		dispatch({type: ActionTypes.FetchStart});

		try {
			const [spaceUsers, spaceUserGroups, userRoles] = await Promise.all([
				SpaceService.getSpaceUsers({
					externalReferenceCode,
					nestedFields: 'roles',
					page: 1,
					pageSize,
				}),
				SpaceService.getSpaceUserGroups({
					externalReferenceCode,
					nestedFields: 'numberOfUserAccounts,roles',
					page: 1,
					pageSize,
				}),
				AdminUserService.getUserRoles({
					filter: "name ne 'Asset Library Connected Site Member' and type eq 5",
				}),
			]);

			dispatch({
				payload: {
					groups: spaceUserGroups,
					roles: userRoles.items,
					users: spaceUsers,
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
	}, [externalReferenceCode, pageSize]);

	useEffect(() => {
		fetchInitialData();
	}, [fetchInitialData]);

	const fetchPage = useCallback(
		(type: SelectOptions, page: number, keywords: string) => {
			const isUser = type === SelectOptions.USERS;

			const params = {
				externalReferenceCode,
				keywords,
				nestedFields: isUser ? 'roles' : 'numberOfUserAccounts,roles',
				page,
				pageSize,
			};

			return isUser
				? SpaceService.getSpaceUsers(params)
				: SpaceService.getSpaceUserGroups(params);
		},

		[externalReferenceCode, pageSize]
	);

	const loadMore = useCallback(
		async (type: SelectOptions) => {
			if (state.isFetching) {
				return;
			}

			const isUser = type === SelectOptions.USERS;
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

		[state.isFetching, state.users, state.groups, state.keywords, fetchPage]
	);

	const search = useCallback(
		async (type: SelectOptions, keywords: string) => {
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

		[state.isSearching, fetchInitialData, fetchPage]
	);

	const addMember = useCallback(
		async (item: UserAccount | UserGroup, type: SelectOptions) => {
			const isUser = type === SelectOptions.USERS;
			const items = isUser ? state.users.items : state.groups.items;

			if (items.some((existingItem) => existingItem.id === item.id)) {
				return;
			}

			const itemWithEmptyRoles = {
				...item,
				roles: [],
			};

			dispatch({
				payload: {item: itemWithEmptyRoles, type},
				type: ActionTypes.AddMemberSuccess,
			});

			const {error} = isUser
				? await SpaceService.linkUserToSpace({
						spaceExternalReferenceCode: externalReferenceCode,
						userExternalReferenceCode: item.externalReferenceCode,
					})
				: await SpaceService.linkUserGroupToSpace({
						spaceExternalReferenceCode: externalReferenceCode,
						userGroupExternalReferenceCode:
							item.externalReferenceCode,
					});

			if (error) {
				dispatch({
					payload: {id: item.id, type},
					type: ActionTypes.AddMemberFailure,
				});

				openToast({
					message: sub(
						isUser
							? Liferay.Language.get(
									'failed-to-add-user-x-to-space'
								)
							: Liferay.Language.get(
									'failed-to-add-group-x-to-space'
								),
						[`<strong>${item.name}</strong>`]
					),
					type: 'danger',
				});
			}
			else {
				openToast({
					message: sub(
						isUser
							? Liferay.Language.get(
									'user-x-successfully-added-to-space'
								)
							: Liferay.Language.get(
									'group-x-successfully-added-to-space'
								),
						[`<strong>${item.name}</strong>`]
					),
					type: 'success',
				});
			}
		},
		[externalReferenceCode, state.users.items, state.groups.items]
	);

	const removeMember = useCallback(
		async (item: UserAccount | UserGroup, type: SelectOptions) => {
			const isUser = type === SelectOptions.USERS;

			dispatch({
				payload: {id: item.id, type},
				type: ActionTypes.RemoveMemberSuccess,
			});

			const {error} = isUser
				? await SpaceService.unlinkUserFromSpace({
						spaceExternalReferenceCode: externalReferenceCode,
						userExternalReferenceCode: item.externalReferenceCode,
					})
				: await SpaceService.unlinkUserGroupFromSpace({
						spaceExternalReferenceCode: externalReferenceCode,
						userGroupExternalReferenceCode:
							item.externalReferenceCode,
					});
			if (error) {
				dispatch({
					payload: {item, type},
					type: ActionTypes.RemoveMemberFailure,
				});

				openToast({
					message: sub(
						isUser
							? Liferay.Language.get(
									'unable-to-remove-user-x-from-space'
								)
							: Liferay.Language.get(
									'unable-to-remove-group-x-from-space'
								),
						[`<strong>${item.name}</strong>`]
					),
					type: 'danger',
				});
			}
			else {
				openToast({
					message: sub(
						isUser
							? Liferay.Language.get(
									'user-x-successfully-removed-from-space'
								)
							: Liferay.Language.get(
									'group-x-successfully-removed-from-space'
								),
						[`<strong>${item.name}</strong>`]
					),
					type: 'success',
				});
			}
		},
		[externalReferenceCode]
	);

	const updateMemberRoles = useCallback(
		async (
			itemToUpdate: UserAccount | UserGroup,
			newRoles: string[],
			type: SelectOptions
		) => {
			const isUser = type === SelectOptions.USERS;
			const originalRoles = itemToUpdate.roles;

			const newRoleObjects = state.roles.filter((role) =>
				newRoles.includes(role.name)
			);

			dispatch({
				payload: {id: itemToUpdate.id, roles: newRoleObjects},
				type: ActionTypes.UpdateRolesSuccess,
			});

			const {error} = isUser
				? await SpaceService.updateUserRoles({
						roleNames: newRoles,
						spaceExternalReferenceCode: externalReferenceCode,
						userExternalReferenceCode:
							itemToUpdate.externalReferenceCode,
					})
				: await SpaceService.updateUserGroupRoles({
						roleNames: newRoles,
						spaceExternalReferenceCode: externalReferenceCode,
						userGroupExternalReferenceCode:
							itemToUpdate.externalReferenceCode,
					});

			if (error) {
				dispatch({
					payload: {
						id: itemToUpdate.id,
						originalRoles:
							originalRoles?.map(
								(originalRole) =>
									state.roles.find(
										(role) => role.id === originalRole.id
									)!
							) || [],
					},
					type: ActionTypes.UpdateRolesFailure,
				});

				openToast({
					message: sub(
						isUser
							? Liferay.Language.get(
									'unable-to-update-roles-for-user-x'
								)
							: Liferay.Language.get(
									'unable-to-update-roles-for-group-x'
								),
						[`<strong>${itemToUpdate.name}</strong>`]
					),
					type: 'danger',
				});
			}
			else {
				openToast({
					message: sub(
						Liferay.Language.get('x-role-was-successfully-updated'),
						[`<strong>${itemToUpdate.name}</strong>`]
					),
					type: 'success',
				});
			}
		},
		[externalReferenceCode, state.roles]
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

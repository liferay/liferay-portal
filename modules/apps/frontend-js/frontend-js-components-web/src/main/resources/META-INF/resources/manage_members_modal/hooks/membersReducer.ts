/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MemberType, Role, UserAccount, UserGroup} from '../types';

export enum ActionTypes {
	AddMemberError = 'ADD_MEMBER_ERROR',
	AddMemberFailure = 'ADD_MEMBER_FAILURE',
	AddMemberStart = 'ADD_MEMBER_START',
	AddMemberSuccess = 'ADD_MEMBER_SUCCESS',
	FetchError = 'FETCH_ERROR',
	FetchStart = 'FETCH_START',
	FetchSuccess = 'FETCH_SUCCESS',
	LoadMoreError = 'LOAD_MORE_ERROR',
	LoadMoreStart = 'LOAD_MORE_START',
	LoadMoreSuccess = 'LOAD_MORE_SUCCESS',
	RemoveMemberFailure = 'REMOVE_MEMBER_FAILURE',
	RemoveMemberSuccess = 'REMOVE_MEMBER_SUCCESS',
	SearchError = 'SEARCH_ERROR',
	SearchStart = 'SEARCH_START',
	SearchSuccess = 'SEARCH_SUCCESS',
	SetKeywords = 'SET_KEYWORDS',
	UpdateRolesFailure = 'UPDATE_ROLES_FAILURE',
	UpdateRolesSuccess = 'UPDATE_ROLES_SUCCESS',
}

export interface State {
	assignableRoleIds: number[];
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

export type Action =
	| {
			payload: {
				assignableRoleIds: number[];
				groups: {items: UserGroup[]; lastPage: number};
				roles: Role[];
				users: {items: UserAccount[]; lastPage: number};
			};
			type: ActionTypes.FetchSuccess;
	  }
	| {
			payload: {id: number | string; originalRoles: Role[]};
			type: ActionTypes.UpdateRolesFailure;
	  }
	| {
			payload: {id: number | string; roles: Role[]};
			type: ActionTypes.UpdateRolesSuccess;
	  }
	| {
			payload: {id: number | string; type: MemberType};
			type:
				| ActionTypes.AddMemberFailure
				| ActionTypes.RemoveMemberSuccess;
	  }
	| {
			payload: {
				items: (UserAccount | UserGroup)[];
				lastPage: number;
				type: MemberType;
			};
			type: ActionTypes.SearchSuccess;
	  }
	| {
			payload: {
				items: (UserAccount | UserGroup)[];
				page: number;
				type: MemberType;
			};
			type: ActionTypes.LoadMoreSuccess;
	  }
	| {
			payload: {item: UserAccount | UserGroup; type: MemberType};
			type:
				| ActionTypes.AddMemberSuccess
				| ActionTypes.RemoveMemberFailure;
	  }
	| {
			payload: {type: MemberType};
			type: ActionTypes.SearchStart;
	  }
	| {
			payload: Error;
			type:
				| ActionTypes.AddMemberError
				| ActionTypes.FetchError
				| ActionTypes.LoadMoreError
				| ActionTypes.SearchError;
	  }
	| {
			payload: string;
			type: ActionTypes.SetKeywords;
	  }
	| {
			type:
				| ActionTypes.AddMemberStart
				| ActionTypes.FetchStart
				| ActionTypes.LoadMoreStart;
	  };

export const initialState: State = {
	assignableRoleIds: [],
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

export function reducer(state: State, action: Action): State {
	switch (action.type) {
		case ActionTypes.FetchStart:
		case ActionTypes.LoadMoreStart:
		case ActionTypes.AddMemberStart:
			return {...state, error: null, isFetching: true};

		case ActionTypes.FetchSuccess:
			return {
				...state,
				assignableRoleIds: action.payload.assignableRoleIds,
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

			const key = type === MemberType.USERS ? 'users' : 'groups';

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

			const key = type === MemberType.USERS ? 'users' : 'groups';

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

			const key = type === MemberType.USERS ? 'users' : 'groups';

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

			const key = type === MemberType.USERS ? 'users' : 'groups';

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

			const key = type === MemberType.USERS ? 'users' : 'groups';

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
					items: state.groups.items.map((item) => {
						return item.id === action.payload.id
							? {...item, roles: action.payload.roles}
							: item;
					}),
				},
				users: {
					...state.users,
					items: state.users.items.map((item) => {
						return item.id === action.payload.id
							? {...item, roles: action.payload.roles}
							: item;
					}),
				},
			};

		case ActionTypes.UpdateRolesFailure: {
			const {id, originalRoles} = action.payload;

			return {
				...state,
				groups: {
					...state.groups,
					items: state.groups.items.map((item) => {
						return item.id === id
							? {...item, roles: originalRoles}
							: item;
					}),
				},
				users: {
					...state.users,
					items: state.users.items.map((item) => {
						return item.id === id
							? {...item, roles: originalRoles}
							: item;
					}),
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
			const key = type === MemberType.USERS ? 'users' : 'groups';

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

			const key = type === MemberType.USERS ? 'users' : 'groups';

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

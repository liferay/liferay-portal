/* eslint-disable no-case-declarations */

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ReactNode, createContext, useReducer} from 'react';

import MarketplaceStorage, {
	CONSENT_TYPE,
	STORAGE_KEYS,
} from '../../../core/Storage';
import useStorage from '../../../hooks/useStorage';
import {
	PAGINATION_DELTA,
	SortDirection,
	SortOption,
} from '../../../utils/constants';

const marketplaceStorage =
	MarketplaceStorage.getInstance().getStorage('persisted');

export type Entry = {
	label: string;
	name: string;
	value: string;
};

export type Sort = {
	direction: SortDirection;
	key: string;
};

type ListViewFilter = {
	entries: Entry[];
	filter: {
		[key: string]: string;
	};
};

type ListViewColumns = {
	[key: string]: boolean;
};

export type InitialState = {
	checkAll: boolean;
	columns: ListViewColumns;
	columnsFixed: string[];
	filters: ListViewFilter;
	id: string;
	keywords: string;
	page: number;
	pageSize: number;
	selectedRows: number[];
	sort: Sort;
};

const initialState: InitialState = {
	checkAll: false,
	columns: {},
	columnsFixed: [],
	filters: {
		entries: [],
		filter: {},
	},
	id: '',
	keywords: '',
	page: 1,
	pageSize: PAGINATION_DELTA[0],
	selectedRows: [],
	sort: {direction: SortOption.ASC, key: ''},
};

export enum ListViewTypes {
	SET_CHECKED_ALL_ROWS = 'SET_CHECKED_ALL_ROWS',
	SET_CHECKED_ROW = 'SET_CHECKED_ROW',
	SET_CLEAR = 'SET_CLEAR',
	SET_COLUMNS = 'SET_COLUMNS',
	SET_FILTERS = 'SET_FILTERS',
	SET_PAGE = 'SET_PAGE',
	SET_PAGE_SIZE = 'SET_PAGE_SIZE',
	SET_REMOVE_FILTER = 'SET_REMOVE_FILTER',
	SET_SEARCH = 'SET_SEARCH',
	SET_SORT = 'SET_SORT',
}

type ListViewPayload = {
	[ListViewTypes.SET_CHECKED_ALL_ROWS]: boolean;
	[ListViewTypes.SET_CHECKED_ROW]: number | number[];
	[ListViewTypes.SET_CLEAR]: null;
	[ListViewTypes.SET_COLUMNS]: {columns: any};
	[ListViewTypes.SET_FILTERS]: {filters?: any; pin?: any};
	[ListViewTypes.SET_PAGE]: number;
	[ListViewTypes.SET_PAGE_SIZE]: number;
	[ListViewTypes.SET_REMOVE_FILTER]: string;
	[ListViewTypes.SET_SEARCH]: string;
	[ListViewTypes.SET_SORT]: Sort;
};

export type AppActions =
	ActionMap<ListViewPayload>[keyof ActionMap<ListViewPayload>];

export const ListViewContext = createContext<
	[InitialState, (_param: AppActions) => void]
>([initialState, () => null]);

const reducer = (state: InitialState, action: AppActions) => {
	switch (action.type) {
		case ListViewTypes.SET_CHECKED_ROW:
			const rowIds = action.payload;

			let selectedRows = [...state.selectedRows];

			if (Array.isArray(rowIds)) {
				selectedRows = state.checkAll ? [] : rowIds;

				state.checkAll = !state.checkAll;
			}
			else {
				const rowAlreadyInserted = state.selectedRows.includes(
					rowIds as number
				);

				rowAlreadyInserted
					? (selectedRows = selectedRows.filter(
							(row) => row !== rowIds
						))
					: (selectedRows = [...selectedRows, rowIds as number]);
			}

			return {
				...state,
				selectedRows,
			};

		case ListViewTypes.SET_CHECKED_ALL_ROWS:
			return {
				...state,
				checkAll: action.payload,
			};

		case ListViewTypes.SET_CLEAR:
			return {
				...state,
				filters: initialState.filters,
				keywords: '',
			};

		case ListViewTypes.SET_COLUMNS:
			const columns = action.payload.columns;
			const storageColumnsName =
				STORAGE_KEYS.LIST_VIEW_COLUMNS + state.id;

			marketplaceStorage.setItem(
				storageColumnsName,
				JSON.stringify(columns),
				CONSENT_TYPE.NECESSARY
			);

			return {
				...state,
				columns,
			};

		case ListViewTypes.SET_PAGE:
			return {
				...state,
				page: action.payload,
			};

		case ListViewTypes.SET_PAGE_SIZE:
			return {
				...state,
				page: 1,
				pageSize: action.payload,
			};

		case ListViewTypes.SET_REMOVE_FILTER: {
			const filterKey = action.payload;
			const updatedFilters = {...state.filters};

			delete updatedFilters.filter[filterKey];

			const filterEntries = updatedFilters.entries.filter(
				({name}) => name !== filterKey
			);

			const filters = {
				entries: filterEntries,
				filter: updatedFilters.filter,
			};

			return {
				...state,
				filters,
			};
		}

		case ListViewTypes.SET_SEARCH:
			return {
				...state,
				keywords: action.payload,
				page: 1,
			};

		case ListViewTypes.SET_SORT:
			return {
				...state,
				sort: action.payload,
			};

		case ListViewTypes.SET_FILTERS: {
			return {
				...state,
				filters: action.payload.filters || state.filters,
				page: 1,
			};
		}

		default:
			return state;
	}
};

export type ListViewContextProviderProps = Partial<InitialState>;

const ListViewContextProvider: React.FC<
	ListViewContextProviderProps & {children: ReactNode; id: string}
> = ({children, id, ...initialStateProps}) => {
	const [columnsStorage] = useStorage<ListViewColumns>(
		(STORAGE_KEYS.LIST_VIEW_COLUMNS + id) as STORAGE_KEYS,
		{consentType: CONSENT_TYPE.NECESSARY, storageType: 'persisted'}
	);

	const [state, dispatch] = useReducer(reducer, {
		...initialState,
		...initialStateProps,
		...(columnsStorage && {columns: columnsStorage}),
		id,
	});

	return (
		<ListViewContext.Provider value={[state, dispatch]}>
			{children}
		</ListViewContext.Provider>
	);
};

export default ListViewContextProvider;

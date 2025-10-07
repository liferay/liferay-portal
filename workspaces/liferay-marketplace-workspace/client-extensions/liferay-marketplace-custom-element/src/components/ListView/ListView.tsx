/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayPaginationBarWithBasicItems} from '@clayui/pagination-bar';
import React, {
	ComponentProps,
	ReactNode,
	useCallback,
	useContext,
	useEffect,
	useMemo,
} from 'react';
import {useSearchParams} from 'react-router-dom';
import {KeyedMutator} from 'swr';

import CreateFilters from '../../core/CreateFilters';
import {useFetch} from '../../hooks/useFetch';
import i18n from '../../i18n';
import {
	FilterSchema as FilterSchemaType,
	filterSchema as filterSchemas,
} from '../../schema/filters';
import {PAGINATION, SortDirection} from '../../utils/constants';
import {safeJSONParse} from '../../utils/util';
import EmptyState from '../EmptyState';
import {RendererFields} from '../Form/Renderer';
import Loading from '../Loading';
import ManagementToolbar, {
	ManagementToolbarProps,
} from './components/ManagementToolbar';
import Table, {TableProps} from './components/Table';
import ListViewContextProvider, {
	AppActions,
	InitialState as ListViewContextState,
	ListViewContext,
	ListViewContextProviderProps,
	ListViewTypes,
	Sort,
} from './hooks/ListViewContext';
import useUpdateUrlParams from './hooks/useUpdateUrlParams';

type ChildrenOptions = {
	dispatch: React.Dispatch<AppActions>;
	listViewContext: ListViewContextState;
	mutate: KeyedMutator<APIResponse<any>>;
};

export type ListViewProps<T extends Record<string, any>> = {
	children?: (
		response: APIResponse<T>,
		options: ChildrenOptions
	) => ReactNode;

	defaultFilters?: {filter: string};

	emptyStateProps?: ComponentProps<typeof EmptyState>;

	/**
	 * The key of SWR Cache for the list view.
	 * It must be provided to avoid cache collisions.
	 *
	 * @default 'listView:{id}?page={page}&pageSize={pageSize}'
	 */
	id: string;

	initialContext?: ListViewContextProviderProps;

	managementToolbarProps?: {
		visible?: boolean;
	} & Omit<
		ManagementToolbarProps,
		| 'actions'
		| 'onSelectAllRows'
		| 'rowSelectable'
		| 'tableProps'
		| 'totalItems'
	>;

	/**
	 * The options for the pagination.
	 *
	 * @default {displayType: true}
	 */
	paginationOptions?: {
		displayType: boolean;
	};

	refreshInterval?: number;

	resource: string;

	tableProps: Omit<
		TableProps<T>,
		'items' | 'mutate' | 'onSelectAllRows' | 'onSort'
	>;

	/**
	 * A function to transform the data before rendering.
	 * It can be used to format or filter the data.
	 *
	 * @default undefined
	 */
	transformData?: (response: APIResponse<T>) => APIResponse<T>;
};

function getMatchedOption(rawValue: string, field?: RendererFields) {
	const matchedOption = field?.options?.find((opt) => {
		if (typeof opt === 'object') {
			return opt.value === rawValue;
		}
	});

	return typeof matchedOption === 'object'
		? matchedOption
		: {label: rawValue, value: rawValue};
}

const ListView = <T extends Record<string, any>>({
	children,
	defaultFilters,
	emptyStateProps,
	managementToolbarProps: {
		visible: managementToolbarVisible = false,
		...managementToolbarProps
	} = {},
	paginationOptions = {displayType: true},
	refreshInterval,
	resource,
	tableProps,
	transformData = (item) => item,
}: ListViewProps<T>) => {
	const [listViewContext, dispatch] = useContext(ListViewContext);

	const updateUrlParams = useUpdateUrlParams();
	const [searchParams] = useSearchParams();

	const {filters, keywords, sort} = listViewContext;

	const filterSchema = (filterSchemas as any)[
		managementToolbarProps?.filterSchema ?? ''
	] as FilterSchemaType;

	const encodedFilter = searchParams.get('filter');

	const setFilters = useCallback(() => {
		const parsedFilter = safeJSONParse(encodedFilter, {});

		if (!Object.keys(parsedFilter).length) {
			return;
		}

		const fields = filterSchema?.fields ?? ([] as RendererFields[]);

		const normalizedFilter = Object.fromEntries(
			Object.entries(parsedFilter).map(([key, value]) => {
				const fieldSchema = fields.find((field) => field.name === key);
				const rawValues = Array.isArray(value)
					? value
					: [String(value)];

				return [
					key,
					rawValues.map((value) =>
						getMatchedOption(value, fieldSchema)
					),
				];
			})
		);

		dispatch({
			payload: {
				filters: {
					entries: Object.entries(normalizedFilter).map(
						([key, selectedOptions]) => ({
							label:
								fields.find(({name}) => name === key)?.label ??
								key,
							name: key,
							value: selectedOptions
								.map((opt) => opt.label)
								.join(', '),
						})
					),
					filter: normalizedFilter,
				},
			},
			type: ListViewTypes.SET_FILTERS,
		});
	}, [dispatch, encodedFilter, filterSchema?.fields]);

	useEffect(() => setFilters(), [encodedFilter, setFilters]);

	const currentPage = searchParams.get('page');
	const currentPageSize = searchParams.get('pageSize');

	const filterVariables = useMemo(
		() => ({
			appliedFilter: filters.filter,
			defaultFilter: defaultFilters?.filter,
			filterSchema,
		}),
		[filters, defaultFilters?.filter, filterSchema]
	);

	const filter = useMemo(() => {
		const baseFilter = CreateFilters.createFilter(filterVariables) || '';

		return {filter: baseFilter};
	}, [filterVariables]);

	const buildSort = (sort: Sort) =>
		sort.key ? `${sort.key}:${sort.direction.toLowerCase()}` : '';

	const onSort = useCallback(
		(key: string, direction: SortDirection) => {
			dispatch({
				payload: {direction, key},
				type: ListViewTypes.SET_SORT,
			});
		},
		[dispatch]
	);

	const getURLSearchParams = useCallback(
		() => ({
			...filter,
			page: currentPage ? Number(currentPage) : listViewContext.page,
			pageSize: currentPageSize
				? Number(currentPageSize)
				: listViewContext.pageSize,
			search: keywords,
			sort: buildSort(sort),
		}),
		[
			currentPage,
			currentPageSize,
			filter,
			listViewContext.page,
			listViewContext.pageSize,
			keywords,
			sort,
		]
	);

	const {
		data: response,
		error,
		isValidating,
		loading,
		mutate,
	} = useFetch(
		resource,
		{
			params: getURLSearchParams(),
		},
		refreshInterval
	);

	const {
		items = [],
		page = 1,
		pageSize,
		totalCount = 0,
	} = transformData(response || {items: []});

	if (loading || (isValidating && searchParams.get('filter'))) {
		return <Loading />;
	}

	const Pagination = (
		<ClayPaginationBarWithBasicItems
			activeDelta={pageSize}
			activePage={page}
			deltas={listViewContext.paginationDeltaOptions.map((label) => ({
				label,
			}))}
			ellipsisBuffer={PAGINATION.ellipsisBuffer}
			labels={{
				paginationResults: i18n.translate('showing-x-to-x-of-x'),
				perPageItems: i18n.translate('x-items'),
				selectPerPageItems: i18n.translate('x-items'),
			}}
			onDeltaChange={(delta) => {
				updateUrlParams({pageSize: delta});

				dispatch({payload: delta, type: ListViewTypes.SET_PAGE_SIZE});
			}}
			onPageChange={(page) => {
				updateUrlParams({page});

				dispatch({payload: page, type: ListViewTypes.SET_PAGE});
			}}
			totalItems={totalCount}
		/>
	);

	return (
		<>
			{managementToolbarVisible && (
				<ManagementToolbar
					{...managementToolbarProps}
					totalItems={totalCount}
				/>
			)}

			{!items.length && (
				<>
					<EmptyState
						description={error?.message}
						type={error ? 'EMPTY_SEARCH' : 'EMPTY_STATE'}
						{...emptyStateProps}
					/>
				</>
			)}
			{!!items.length && (
				<>
					<Table
						{...tableProps}
						items={items}
						mutate={mutate}
						onSort={onSort}
						sort={sort}
					/>

					{paginationOptions.displayType && Pagination}
				</>
			)}
			{children &&
				children(response!, {
					dispatch,
					listViewContext,
					mutate,
				})}
		</>
	);
};
const ListViewWithContext = <T extends Record<string, any>>({
	initialContext,
	...otherProps
}: ListViewProps<T>): React.ReactElement => (
	<ListViewContextProvider {...initialContext} id={otherProps.id}>
		<ListView {...otherProps} />
	</ListViewContextProvider>
);

export default ListViewWithContext;

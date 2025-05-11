/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayPaginationBarWithBasicItems} from '@clayui/pagination-bar';
import {
	ComponentProps,
	ReactNode,
	useCallback,
	useContext,
	useMemo,
} from 'react';
import useSWR, {KeyedMutator} from 'swr';

import i18n from '../../i18n';
import fetcher from '../../services/fetcher';
import {PAGINATION, SortDirection} from '../../utils/constants';
import EmptyState from '../EmptyState';
import Loading from '../Loading';
import Table, {TableProps} from './components/Table';
import ListViewContextProvider, {
	AppActions,
	InitialState as ListViewContextState,
	ListViewContext,
	ListViewContextProviderProps,
	ListViewTypes,
} from './hooks/ListViewContext';

type ResourceProps = Pick<ListViewContextState, 'page' | 'pageSize'>;

type ChildrenOptions = {
	dispatch: React.Dispatch<AppActions>;
	listViewContext: ListViewContextState;
	mutate: KeyedMutator<APIResponse<any>>;
};

type Resource<T> =
	| ((listViewContext: ResourceProps) => Promise<APIResponse<T>>)
	| string;

export type ListViewProps<T extends Record<string, any>> = {
	children?: (
		response: APIResponse<T>,
		options: ChildrenOptions
	) => ReactNode;

	emptyStateProps?: ComponentProps<typeof EmptyState>;

	/**
	 * The key of SWR Cache for the list view.
	 * It must be provided to avoid cache collisions.
	 *
	 * @default 'listView:{id}?page={page}&pageSize={pageSize}'
	 */
	id: string;

	initialContext?: ListViewContextProviderProps;

	/**
	 * The options for the pagination.
	 *
	 * @default {displayType: 'auto'}
	 */
	paginationOptions?: {
		displayType: 'always' | 'auto' | 'never';
	};

	/**
	 * The resource of the list view.
	 * It can be an async function or a string.
	 */
	resource: Resource<T>;

	tableProps: Omit<
		TableProps<T>,
		'items' | 'mutate' | 'onSelectAllRows' | 'onSort'
	>;
};

const ListView = <T extends Record<string, any>>({
	children,
	emptyStateProps,
	paginationOptions = {displayType: 'auto'},
	resource,
	tableProps,
}: ListViewProps<T>) => {
	const [listViewContext, dispatch] = useContext(ListViewContext);

	const {id, page, pageSize, sort} = listViewContext;

	const params = useMemo(() => {
		const isResourceString = typeof resource === 'string';

		if (isResourceString) {
			return {
				resource: () => fetcher(resource),
				resourceKey: resource,
			};
		}

		return {
			resource: () =>
				resource({
					page,
					pageSize,
				}),
			resourceKey: `listView:${id}?page=${page}&pageSize=${pageSize}`,
		};
	}, [id, page, pageSize, resource]);

	const {
		data: response,
		error,
		isLoading: loading,
		mutate,
	} = useSWR<APIResponse<T>>(params.resourceKey, params.resource);

	const {items = [], totalCount = 0} = response || {};

	const onSort = useCallback(
		(key: string, direction: SortDirection) => {
			dispatch({
				payload: {direction, key},
				type: ListViewTypes.SET_SORT,
			});
		},
		[dispatch]
	);

	const Pagination = useMemo(() => {
		const paginationDisplayType = paginationOptions?.displayType;

		if (
			(paginationDisplayType === 'auto' && totalCount < 5) ||
			paginationDisplayType === 'never'
		) {
			return null;
		}

		return (
			<ClayPaginationBarWithBasicItems
				activeDelta={pageSize}
				activePage={page}
				deltas={PAGINATION.delta.map((label) => ({label}))}
				ellipsisBuffer={PAGINATION.ellipsisBuffer}
				labels={{
					paginationResults: i18n.translate('showing-x-to-x-of-x'),
					perPageItems: i18n.translate('x-items'),
					selectPerPageItems: i18n.translate('x-items'),
				}}
				onDeltaChange={(delta) =>
					dispatch({
						payload: delta,
						type: ListViewTypes.SET_PAGE_SIZE,
					})
				}
				onPageChange={(page) =>
					dispatch({
						payload: page,
						type: ListViewTypes.SET_PAGE,
					})
				}
				totalItems={totalCount}
			/>
		);
	}, [dispatch, page, pageSize, paginationOptions?.displayType, totalCount]);

	if (loading) {
		return <Loading />;
	}

	return (
		<>
			{!items.length && (
				<EmptyState
					description={error?.message}
					type={error ? 'EMPTY_SEARCH' : 'EMPTY_STATE'}
					{...emptyStateProps}
				/>
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

					{Pagination}

					{children &&
						children(response!, {
							dispatch,
							listViewContext,
							mutate,
						})}
				</>
			)}
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

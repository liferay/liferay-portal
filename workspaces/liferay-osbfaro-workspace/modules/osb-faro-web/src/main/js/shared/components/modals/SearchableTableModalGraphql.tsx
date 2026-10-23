import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import CrossPageSelect from 'shared/hoc/CrossPageSelect';
import React, {useEffect} from 'react';
import type {Observer} from '@clayui/modal/lib/types';
import {
	ACTION_TYPES,
	useSelectionContext,
	withSelectionProvider,
} from 'shared/context/selection';
import {Columns, IPagination} from 'shared/types';
import {DocumentNode, QueryHookOptions, useQuery} from '@apollo/client';

import {noop} from 'lodash';
import {OrderedMap} from 'immutable';
import {OrderParams} from 'shared/util/records';

import {safeResultToProps} from 'shared/util/mappers';
import {useStatefulPagination} from 'shared/hooks/useStatefulPagination';

interface ISearchableTableModalGraphQLProps extends IPagination {
	[key: string]: any;
	className?: string;
	columns: Columns;
	graphqlQuery: DocumentNode;
	initialDelta?: number;
	initialOrderIOMap: OrderedMap<string, OrderParams>;
	instruction?: string;
	mapPropsToOptions: (props: {[key: string]: any}) => QueryHookOptions;
	mapResultToProps: (result: {[key: string]: any}) => {
		items: any[];
		total: number;
	};
	observer: Observer;
	onClose?: () => void;
	onSubmit: (selectedItems: OrderedMap<string, any>) => void;
	requireSelection?: boolean;
	selectedItems?: any[];
	submitMessage?: string;
	title?: string;
}

const SearchableTableModalGraphql: React.FC<
	ISearchableTableModalGraphQLProps
> = ({
	className,
	columns,
	graphqlQuery,
	initialDelta = 5,
	initialOrderIOMap,
	instruction = '',
	mapPropsToOptions,
	mapResultToProps,
	observer,
	onClose = noop,
	onSubmit,
	requireSelection = true,
	selectedItems = [],
	submitMessage = Liferay.Language.get('submit'),
	title = Liferay.Language.get('select-items'),
	...otherProps
}) => {
	const {
		delta,
		onDeltaChange,
		onOrderIOMapChange,
		onPageChange,
		onQueryChange,
		orderIOMap,
		page,
		query,
	} = useStatefulPagination(undefined, {initialDelta, initialOrderIOMap});

	const {data, error, loading} = useQuery(
		graphqlQuery,
		mapPropsToOptions({...otherProps, delta, orderIOMap, page, query})
	);

	const {selectedItems: contextSelectedItems, selectionDispatch} =
		useSelectionContext();

	useEffect(() => {
		if (selectedItems.length) {
			selectionDispatch?.({type: ACTION_TYPES.clearAll});
			selectionDispatch?.({
				payload: {items: selectedItems},
				type: ACTION_TYPES.add,
			});
		}
	}, []);

	const handleSubmit = () => onSubmit(contextSelectedItems);

	const {empty, items, total} = safeResultToProps(mapResultToProps)({
		data: {error, loading, ...data},
		ownProps: {},
	});

	return (
		<ClayModal className={className} observer={observer} size="lg">
			<ClayModal.Header>{title}</ClayModal.Header>

			<ClayModal.Body className="p-0">
				<div className="text-secondary">{instruction}</div>

				<CrossPageSelect
					{...otherProps}
					autoFocus
					columns={columns}
					delta={delta}
					empty={empty}
					items={items}
					loading={loading}
					onDeltaChange={onDeltaChange}
					onOrderIOMapChange={onOrderIOMapChange}
					onPageChange={onPageChange}
					onQueryChange={onQueryChange}
					orderIOMap={orderIOMap}
					page={page}
					pageDisplay={false}
					query={query}
					total={total}
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							className="button-root"
							displayType="secondary"
							onClick={onClose}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							className="button-root"
							disabled={
								requireSelection && !contextSelectedItems.size
							}
							displayType="primary"
							onClick={handleSubmit}
						>
							{submitMessage}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
};

export default withSelectionProvider(SearchableTableModalGraphql);

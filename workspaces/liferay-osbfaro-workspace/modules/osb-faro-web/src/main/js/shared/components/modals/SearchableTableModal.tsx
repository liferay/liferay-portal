import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import CrossPageSelect from 'shared/hoc/CrossPageSelect';
import React, {useEffect} from 'react';
import type {Observer} from '@clayui/modal/lib/types';
import {
	ActionTypes,
	useSelectionContext,
	withSelectionProvider,
} from 'shared/context/selection';
import {FilterByType} from 'shared/types';
import {OrderedMap} from 'immutable';
import {OrderParams} from 'shared/util/records';
import {useRequest} from 'shared/hooks/useRequest';
import {useStatefulPagination} from 'shared/hooks/useStatefulPagination';

interface ISearchableTableModalProps {
	[key: string]: any;
	checkDisabled?: (item?: object) => boolean;
	className?: string;
	columns: any[];
	dataSourceFn: (params: {[key: string]: any}) => Promise<any>;
	dataSourceParams?: {[key: string]: any};
	entityLabel?: string;
	initialDelta?: number;
	initialFilterBy?: FilterByType;
	initialOrderIOMap?: OrderedMap<string, OrderParams>;
	initialPage?: number;
	initialQuery?: string;
	initialSelectedItems?: {[key: string]: any}[];
	instruction?: string;
	noResultsIcon?: string | null;
	observer: Observer;
	onClose: () => void;
	onSubmit: (
		selectedItemsIOMap: OrderedMap<string, any>,
		refetch: () => void
	) => void;
	orderByOptions?: {label: string; value: string}[];
	requireSelection?: boolean;
	submitMessage?: string;
	title?: string;
}

const SearchableTableModal: React.FC<ISearchableTableModalProps> = ({
	checkDisabled,
	className,
	columns,
	dataSourceFn,
	dataSourceParams,
	entityLabel,
	initialDelta = 10,
	initialFilterBy,
	initialOrderIOMap,
	initialPage,
	initialQuery,
	initialSelectedItems,
	instruction,
	noResultsIcon,
	observer,
	onClose,
	onSubmit,
	orderByOptions,
	requireSelection = true,
	submitMessage = Liferay.Language.get('submit'),
	title = Liferay.Language.get('select-items'),
}) => {
	const {selectedItems, selectionDispatch} = useSelectionContext();

	const {
		delta,
		filterBy,
		onDeltaChange,
		onFilterByChange,
		onOrderIOMapChange,
		onPageChange,
		onQueryChange,
		orderIOMap,
		page,
		query,
	} = useStatefulPagination(undefined, {
		initialDelta,
		initialFilterBy,
		initialOrderIOMap,
		initialPage,
		initialQuery,
	});

	useEffect(() => {
		if (initialSelectedItems?.length) {
			selectionDispatch?.({type: ActionTypes.ClearAll});
			selectionDispatch?.({
				payload: {items: initialSelectedItems},
				type: ActionTypes.Add,
			});
		}
	}, []);

	const {data, error, loading, refetch} = useRequest({
		dataSourceFn,
		variables: {
			...dataSourceParams,
			delta,
			filterBy,
			orderIOMap,
			page,
			query,
		},
	});

	return (
		<ClayModal className={className} observer={observer} size="lg">
			<ClayModal.Header>{title}</ClayModal.Header>

			<ClayModal.Body className="p-0">
				<div className="text-secondary">{instruction}</div>

				<CrossPageSelect
					autoFocusSearch
					checkDisabled={checkDisabled}
					columns={columns}
					dataSourceFn={dataSourceFn}
					delta={delta}
					entityLabel={entityLabel}
					error={error}
					items={data?.items}
					loading={loading}
					noResultsIcon={noResultsIcon}
					onDeltaChange={onDeltaChange}
					onFilterByChange={onFilterByChange}
					onOrderIOMapChange={onOrderIOMapChange}
					onPageChange={onPageChange}
					onQueryChange={onQueryChange}
					orderByOptions={orderByOptions}
					orderIOMap={orderIOMap}
					page={page}
					pageDisplay={false}
					query={query}
					rowIdentifier="id"
					selectedItems={selectedItems.map(({id}) => id)}
					selectedItemsIOMap={selectedItems}
					showCheckbox
					total={data?.total}
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
							disabled={requireSelection && !selectedItems.size}
							displayType="primary"
							onClick={() => onSubmit(selectedItems, refetch)}
						>
							{submitMessage}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
};

export default withSelectionProvider(SearchableTableModal);

import getCN from 'classnames';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import StagedSubNav from './StagedSubnav';
import Table from 'shared/components/table';
import {Column} from 'shared/components/table';
import {compose, withPaginationBar, withToolbar} from 'shared/hoc';
import {createOrderIOMap, NAME} from 'shared/util/pagination';
import {fetchLocalData} from 'shared/hoc/CrossPageSelect';
import {OrderedMap} from 'immutable';
import {OrderParams} from 'shared/util/records';
import {Sizes} from 'shared/util/constants';
import {sub} from 'shared/util/lang';
import {useStatefulPagination} from 'shared/hooks/useStatefulPagination';
import {withEmpty} from 'cerebro-shared/hocs/utils';
import {withError, withLoading} from 'shared/hoc/util';

const ListComponent = compose<any>(
	withToolbar(null),
	withPaginationBar(),
	Component => ({className, renderSelectedToggle, ...otherProps}) => (
		<div
			className={getCN(
				'searchable-table-with-staged-root d-flex flex-column flex-grow-1',
				className
			)}
		>
			{renderSelectedToggle()}

			<Component {...otherProps} />
		</div>
	),
	withLoading(),
	withError(),
	withEmpty()
)(Table);

/**
 * This component is specifically for the static segment editor
 * because it does not fit the typical pattern of cross-page selection.
 *
 * addedItemsIOMap is not a reflection of selected items from the server list,
 * but is a list of individuals to be added to the segment
 * from a modal and it is controlled directly by SegmentEditStatic.
 */

interface ISearchableTableWithAddedProps {
	addedItemsIOMap: OrderedMap<string, any>;
	className: string;
	columns: Column[];
	delta: number;
	entityLabel: string;
	error: boolean;
	items: any[];
	loading: boolean;
	onDeltaChange: (delta: number) => void;
	onOrderIOMapChange: (orderIOMap: OrderedMap<string, OrderParams>) => void;
	onPageChange: (page: number) => void;
	onQueryChange: (query: string) => void;
	onSelectEntirePage: (items: any[]) => (checked: boolean) => void;
	onSelectItemsChange: (item: any[]) => void;
	onShowStagedToggle: () => void;
	orderByOptions: {label: string; value: string}[];
	orderIOMap: OrderedMap<string, OrderParams>;
	page: number;
	query: string;
	renderInlineRowActions: ({
		data,
		items,
		itemsSelected
	}: {
		data: any;
		items: {[key: string]: any};
		itemsSelected: OrderedMap<string, any>;
	}) => React.ReactNode;
	renderNav: ({
		selectedItemsIOMap
	}: {
		selectedItemsIOMap: OrderedMap<string, any>;
	}) => React.ReactNode;
	rowIdentifier: string;
	selectedItemsIOMap: OrderedMap<string, any>;
	selectEntirePage: boolean;
	selectEntirePageIndeterminate: boolean;
	showCheckbox?: boolean;
	showStaged: boolean;
	total: number;
}

const SearchableTableWithAdded: React.FC<ISearchableTableWithAddedProps> = ({
	addedItemsIOMap,
	className,
	columns,
	delta = 20,
	entityLabel = Liferay.Language.get('items'),
	items,
	onDeltaChange,
	onOrderIOMapChange,
	onPageChange,
	onQueryChange,
	onSelectEntirePage,
	onSelectItemsChange,
	onShowStagedToggle,
	orderIOMap,
	page,
	query,
	renderInlineRowActions,
	renderNav,
	selectEntirePage,
	selectEntirePageIndeterminate,
	selectedItemsIOMap,
	showCheckbox,
	showStaged,
	total,
	...otherProps
}) => {
	const {
		onPageChange: onStagedPageChange,
		onQueryChange: onStagedQueryChange,
		page: stagedPage,
		query: stagedQuery
	} = useStatefulPagination(null, {
		initialOrderIOMap: createOrderIOMap(NAME)
	});

	const stagedData = fetchLocalData({
		delta,
		items: addedItemsIOMap,
		orderIOMap,
		page: stagedPage,
		query: stagedQuery
	});

	const allChecked =
		!selectedItemsIOMap.isEmpty() &&
		stagedData.items.every(item => selectedItemsIOMap.has(item.id));

	const passThruProps = showStaged
		? {
				...stagedData,
				delta,
				onDeltaChange,
				onOrderIOMapChange,
				onPageChange: onStagedPageChange,
				onQueryChange: onStagedQueryChange,
				onSelectEntirePage: onSelectEntirePage(stagedData.items),
				onSelectItemsChange,
				orderIOMap,
				page: stagedPage,
				query: stagedQuery,
				selectedItemsIOMap,
				selectEntirePage: allChecked,
				selectEntirePageIndeterminate:
					!allChecked && !selectedItemsIOMap.isEmpty()
		  }
		: {
				delta,
				items,
				onDeltaChange,
				onOrderIOMapChange,
				onPageChange,
				onQueryChange,
				onSelectEntirePage: onSelectEntirePage(items),
				onSelectItemsChange,
				orderIOMap,
				page,
				query,
				selectedItemsIOMap,
				selectEntirePage,
				selectEntirePageIndeterminate,
				total
		  };

	return (
		<ListComponent
			{...otherProps}
			{...passThruProps}
			className={className}
			columns={columns}
			noResultsRenderer={
				<NoResultsDisplay
					icon={{
						size: Sizes.XXLarge,
						symbol: 'ac_individual'
					}}
					title={
						sub(Liferay.Language.get('there-are-no-x-found'), [
							Liferay.Language.get('individuals')
						]) as string
					}
				/>
			}
			renderInlineRowActions={renderInlineRowActions}
			renderNav={renderNav}
			renderSelectedToggle={() =>
				!!addedItemsIOMap.size && (
					<StagedSubNav
						onToggle={onShowStagedToggle}
						selectedCountMessage={sub(
							Liferay.Language.get('x-members-added'),
							[addedItemsIOMap.size]
						)}
						showStaged={showStaged}
						stagedMessage={sub(
							Liferay.Language.get('showing-only-added-x'),
							[entityLabel]
						)}
						viewCurrentLinkText={Liferay.Language.get(
							'view-current-members'
						)}
						viewStagedLinkText={Liferay.Language.get(
							'view-added-members'
						)}
					/>
				)
			}
			showCheckbox={showCheckbox}
		/>
	);
};

export default SearchableTableWithAdded;

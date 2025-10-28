import getCN from 'classnames';
import HeaderRow from './HeaderRow';
import Loading from 'shared/components/Loading';
import React from 'react';
import Row from './Row';
import {get, isArray, noop, orderBy} from 'lodash';
import {OrderedMap} from 'immutable';
import {OrderParams} from 'shared/util/records';
import type {Column} from './Row';

export const getRowIdentifierValue = (item, rowIdentifier) => {
	if (isArray(rowIdentifier)) {
		return rowIdentifier.reduce((acc, rowIdentifierKey) => {
			acc = acc.concat(get(item, rowIdentifierKey, rowIdentifierKey));

			return acc;
		}, '');
	}

	return get(item, rowIdentifier);
};

interface ITableProps {
	checkDisabled?: (item: {[key: string]: any}) => boolean;
	className?: string;
	columns: Column[];
	empty?: boolean;
	enableMultiSort?: boolean;
	headingNowrap?: boolean;
	internalSort?: boolean;
	items: {[key: string]: any}[];
	loading?: boolean;
	orderIOMap?: OrderedMap<string, OrderParams>;
	onOrderIOMapChange?: (orderIOMap: OrderedMap<string, OrderParams>) => void;
	onRowClick?: (item: {[key: string]: any}) => void;
	onRowDelete?: (item: {[key: string]: any}) => void;
	onRowSave?: (item: {[key: string]: any}) => void;
	onSelectItemsChange?: (item: {[key: string]: any}) => void;
	renderInlineRowActions?: (params: {
		data: {[key: string]: any};
		editing: boolean;
		edits: object;
		items: object[];
		itemsSelected: object[];
		rowEvents: {
			onRowCancel: () => void;
			onRowEdit: () => void;
			onRowSave: () => void;
		};
	}) => React.ReactNode;
	renderRowActions?: (params: {
		data: object;
		items: object[];
	}) => React.ReactNode;
	rowIdentifier: string | string[];
	selectedItemsIOMap?: OrderedMap<string, object>;
	showCheckbox?: boolean;
}

const Table: React.FC<ITableProps> = ({
	checkDisabled = () => false,
	className,
	columns,
	empty = false,
	enableMultiSort = false,
	internalSort = false,
	items = [],
	loading = false,
	onOrderIOMapChange,
	onRowClick,
	onRowDelete = noop,
	onRowSave = noop,
	onSelectItemsChange,
	orderIOMap = OrderedMap(),
	renderInlineRowActions,
	renderRowActions,
	rowIdentifier = 'id',
	selectedItemsIOMap = OrderedMap(),
	showCheckbox = false
}) => {
	const handleSortOrderChange = (orderParams: OrderParams) => {
		if (onOrderIOMapChange) {
			if (enableMultiSort) {
				onOrderIOMapChange(
					orderIOMap.set(orderParams.field, orderParams)
				);
			} else {
				onOrderIOMapChange(
					OrderedMap({[orderParams.field]: orderParams})
				);
			}
		}
	};

	const handleItemClick = item => {
		if (showCheckbox && onSelectItemsChange) {
			onSelectItemsChange(item);
		}

		if (onRowClick) {
			onRowClick(item);
		}
	};

	const sortItems = items => {
		const orderParams = orderIOMap.first();

		const {field, sortOrder} = orderParams;

		return orderBy(
			items,
			item => {
				const fieldValue = item[field];

				if (typeof fieldValue === 'string') {
					return fieldValue.toLowerCase();
				}

				return fieldValue;
			},
			sortOrder.toLowerCase() as 'asc' | 'desc'
		);
	};

	const classes = getCN(
		'table',
		'table-autofit',
		'table-striped',
		'table-list',
		'table-nowrap',
		'table-head-bordered',
		'table-hover',
		{
			'show-quick-actions-on-hover': renderRowActions
		}
	);

	const itemsSorted = internalSort ? sortItems(items) : items;

	return (
		<div className={getCN('flex-grow-1 mx-4 table-root', className)}>
			<div className='table-responsive'>
				<table className={classes}>
					<HeaderRow
						columns={columns}
						headerLink={!internalSort && !onOrderIOMapChange}
						onSortOrderChange={handleSortOrderChange}
						orderIOMap={orderIOMap}
						showCheckbox={showCheckbox}
						showInlineRowActions={
							!!renderInlineRowActions || !!renderRowActions
						}
					/>

					{!!itemsSorted.length && (
						<tbody className={className}>
							{itemsSorted.map(
								(item: {[key: string]: any}, rowIndex) => {
									const disabled = checkDisabled(item);

									return (
										<Row
											className={className}
											clickable={
												!!onRowClick ||
												(showCheckbox &&
													!!onSelectItemsChange)
											}
											columns={columns}
											data={item}
											disabled={disabled}
											items={items}
											itemsSelected={
												!selectedItemsIOMap.isEmpty()
											}
											key={
												empty
													? `empty${rowIndex}`
													: getRowIdentifierValue(
															item,
															rowIdentifier
													  )
											}
											onClick={
												disabled
													? noop
													: handleItemClick
											}
											onRowDelete={onRowDelete}
											onRowSave={onRowSave}
											renderInlineRowActions={
												renderInlineRowActions
											}
											renderRowActions={renderRowActions}
											rowIndex={rowIndex}
											selected={
												onSelectItemsChange
													? selectedItemsIOMap.has(
															item?.id
													  )
													: null
											}
											showCheckbox={showCheckbox}
										/>
									);
								}
							)}
						</tbody>
					)}
				</table>

				{loading && <Loading overlay />}
			</div>
		</div>
	);
};

export {Column};
export default Table;

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayDropDown, {Align} from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClayTable from '@clayui/table';
import classNames from 'classnames';
import React, {useState} from 'react';
import {Link} from 'react-router-dom';
import {KeyedMutator} from 'swr';

import {Action, SortDirection, SortOption} from '../../../utils/constants';
import {Sort} from '../hooks/ListViewContext';

export type Column<
	T extends Record<string, any>,
	K extends keyof T = keyof T,
> = {
	clickable?: boolean;
	id: K;
	name: string;
	render?: (
		itemValue: T[K],
		item: T,
		mutate: KeyedMutator<APIResponse<T>>
	) => String | React.ReactNode;
	selectable?: boolean;
	size?: 'sm' | 'md' | 'lg' | 'xl' | 'none';
	sortable?: boolean;
	truncate?: boolean;
	width?: '50' | '75' | '100' | '200' | '250' | '300' | '350' | '400';
};

export type TableProps<T extends Record<string, any> = any> = {
	actions?: Action[];
	bodyVerticalAlignment?: 'bottom' | 'middle' | 'top';
	columns: {
		[K in keyof T]: Column<T, K>;
	}[keyof T][];
	highlight?: (item: T) => boolean;
	items: T[];
	mutate: KeyedMutator<APIResponse<T>>;
	navigateTo?: (item: T) => string;
	onClickRow?: (item: T) => void;
	onSelectRow?: (row: T) => void;
	onSort: (columnTable: string, direction: SortDirection) => void;
	responsive?: boolean;
	rowWrap?: boolean;
	sort?: Sort;
};

const Table = <T extends Record<string, any>>({
	actions,
	bodyVerticalAlignment = 'middle',
	columns,
	highlight,
	items,
	mutate,
	navigateTo,
	onClickRow,
	onSort,
	responsive,
	rowWrap = false,
	sort,
}: TableProps<T>) => {
	const [sorted, setSorted] = useState<SortDirection>(SortOption.DESC);

	const changeSort = (key: string) => {
		onSort(key, sorted);

		setSorted(sorted === SortOption.ASC ? SortOption.DESC : SortOption.ASC);
	};

	const getSortSymbol = (key: string) => {
		if (!sort) {
			return '';
		}

		if (sort.key === key) {
			return sort.direction === SortOption.ASC
				? 'caret-top-l'
				: 'caret-bottom-l';
		}

		return 'caret-double-l';
	};

	let _columns = columns;

	if (actions) {
		_columns = [
			...columns,
			{
				id: '_actions_',
				name: '',
				render: (_, item) => (
					<ClayDropDown
						alignmentPosition={Align.BottomCenter}
						className="d-flex justify-content-end"
						closeOnClick
						items={actions.map((action) => ({
							...action,
							disabled:
								typeof action.disabled === 'boolean'
									? action.disabled
									: action?.disabled?.(item),
							hidden:
								typeof action.hidden === 'boolean'
									? action.hidden
									: action?.hidden?.(item),
							onClick: () => {
								if (action.onClick) {
									return action?.onClick(item, mutate);
								}
							},
						}))}
						trigger={
							<ClayButtonWithIcon
								aria-label="actions"
								displayType="unstyled"
								onClick={(event) => event.stopPropagation()}
								symbol="ellipsis-v"
							/>
						}
					>
						{(item, index) => (
							<ClayDropDown.Item
								disabled={item.disabled}
								hidden={!!item.hidden}
								onClick={() => item.onClick()}
								{...{['keyValue']: index}}
							>
								{item.icon && (
									<ClayIcon
										className="mr-2"
										symbol={item.icon}
									/>
								)}

								{typeof item.name === 'string'
									? item.name
									: item.name(item)}
							</ClayDropDown.Item>
						)}
					</ClayDropDown>
				),
			},
		];
	}

	return (
		<ClayTable
			borderless
			className="tr-table"
			hover
			responsive={responsive}
			striped={false}
			tableVerticalAlignment={bodyVerticalAlignment}
		>
			<ClayTable.Head>
				<ClayTable.Row>
					{_columns.map((column, index) => (
						<ClayTable.Cell headingTitle key={index}>
							<span className="d-flex justify-content-between">
								<span
									className={classNames({
										'cursor-pointer': column.sortable,
									})}
									onClick={() => {
										if (column.sortable) {
											changeSort(column.id.toString());
										}
									}}
								>
									{column.name}
								</span>

								{column.sortable && (
									<ClayIcon
										className="cursor-pointer mr-auto mt-1"
										onClick={() =>
											changeSort(column.id.toString())
										}
										symbol={getSortSymbol(
											column.id.toString()
										)}
									/>
								)}
							</span>
						</ClayTable.Cell>
					))}
				</ClayTable.Row>
			</ClayTable.Head>

			<ClayTable.Body>
				{items.map((item, rowIndex) => (
					<ClayTable.Row
						className={classNames('tr-table__row', {
							'text-nowrap': !rowWrap,
							'text-wrap': rowWrap,
							'tr-table__row--highligth':
								highlight && highlight(item),
						})}
						key={rowIndex}
					>
						{_columns.map((column, columnIndex) => {
							const Wrapper: React.ElementType =
								column.selectable ||
								(column.clickable && navigateTo)
									? Link
									: 'div';

							const itemValue = item[column.id];

							return (
								<ClayTable.Cell
									className={classNames('text-dark', {
										'cursor-pointer': column.clickable,
										[`table-cell-minw-${column.width}`]:
											column.width,
										'table-cell-expand':
											column.size === 'sm',
										'table-cell-expand-small':
											column.size === 'xl',
										'table-cell-expand-smaller':
											column.size === 'lg',
										'table-cell-expand-smallest':
											column.size === 'md',
									})}
									expanded={column.truncate}
									key={columnIndex}
									onClick={() => {
										if (onClickRow) {
											onClickRow(item);
										}
									}}
									truncate={column.truncate}
								>
									<Wrapper
										className="text-dark"
										{...(Wrapper === Link
											? {
													to: navigateTo?.(
														item
													)?.toString() as string,
												}
											: {})}
									>
										{column.render
											? column.render(
													itemValue,
													item,
													mutate
												)
											: itemValue}
									</Wrapper>
								</ClayTable.Cell>
							);
						})}
					</ClayTable.Row>
				))}
			</ClayTable.Body>
		</ClayTable>
	);
};

export default Table;

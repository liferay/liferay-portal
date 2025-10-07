/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayPaginationBarWithBasicItems} from '@clayui/pagination-bar';
import ClayTable from '@clayui/table';
import classNames from 'classnames';
import {ReactNode} from 'react';

import './Table.scss';

type TableProps<T = any> = {
	Actions?: React.FC<{row: T}>;
	children?: ReactNode;
	className?: string;
	columns: TableColumn<T>[];
	hasHover?: boolean;
	hasKebabButton?: boolean;
	hasPagination?: boolean;
	kebabClassName?: string;
	onClickRow?: (row: T) => void;
	paginationProps?: PaginationProps;
	rows: T[];
};

type TableColumn<T = any> = {
	align?: 'center' | 'left' | 'right';
	bodyClass?: string;
	columnTextAlignment?: 'center' | 'end' | 'start';
	disableCustomClickOnRow?: boolean;
	expanded?: boolean;
	key: string;
	noWrap?: boolean;
	onClick?: (item: T) => void;
	render?: (value: any, item: T) => ReactNode | string;
	styles?: string;
	title?: ReactNode;
	truncate?: boolean;
	width?: string;
};

type PaginationProps = {
	activeDelta: number;
	activePage: number;
	deltas?: {
		label: number;
	}[];
	onDeltaChange: (pageSize: number) => void;
	onPageChange: (page: number) => void;
	totalItems: number;
};

const Table: React.FC<TableProps> = ({
	Actions,
	children,
	className,
	columns,
	hasHover = true,
	hasKebabButton,
	hasPagination,
	kebabClassName = '',
	onClickRow,
	paginationProps,
	rows,
}) => {
	return (
		<>
			<ClayTable
				borderless
				className={className}
				hover={hasHover}
				striped={false}
			>
				<ClayTable.Head>
					<ClayTable.Row className="border-bottom header-row">
						{columns.map((column, index) => (
							<ClayTable.Cell
								align={column.align}
								className="bg-transparent font-weight-bold"
								headingCell
								key={index}
								noWrap={column.noWrap}
								style={{width: column.width}}
							>
								{column?.title}
							</ClayTable.Cell>
						))}

						{hasKebabButton && <ClayTable.Cell />}
					</ClayTable.Row>
				</ClayTable.Head>

				<ClayTable.Body className="table-body">
					{rows.map((row, rowIndex) => (
						<ClayTable.Row
							className={classNames({
								'cursor-pointer':
									typeof onClickRow === 'function',
							})}
							key={rowIndex}
						>
							{columns.map((column, columnIndex) => {
								const data = row[column.key];

								const value = column.render
									? column.render(data, {
											...row,
											rowIndex,
										})
									: data;

								return (
									<ClayTable.Cell
										align={column.align}
										className={column.bodyClass}
										columnTextAlignment={
											column.columnTextAlignment
										}
										expanded={column.expanded}
										key={`${rowIndex}-${columnIndex}`}
										noWrap={column.noWrap}
										onClick={() => {
											if (onClickRow) {
												onClickRow(row);
											}
										}}
										truncate={column.truncate}
									>
										{value}
									</ClayTable.Cell>
								);
							})}

							{hasKebabButton && (
								<ClayTable.Cell
									className={kebabClassName}
									columnTextAlignment="center"
								>
									{Actions && <Actions row={row} />}
								</ClayTable.Cell>
							)}
						</ClayTable.Row>
					))}
					{children}
				</ClayTable.Body>
			</ClayTable>

			{hasPagination && paginationProps && (
				<ClayPaginationBarWithBasicItems
					ellipsisBuffer={3}
					{...paginationProps}
				/>
			)}
		</>
	);
};

export default Table;

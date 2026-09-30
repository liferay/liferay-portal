/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Button from '@clayui/button';
import Icon from '@clayui/icon';
import Layout from '@clayui/layout';
import LoadingIndicator from '@clayui/loading-indicator';
import {Keys, sub} from '@clayui/shared';
import classNames from 'classnames';
import React, {useCallback, useState} from 'react';

import {useFocusWithin} from '../aria';
import {Scope, useScope} from './ScopeContext';
import {useRow, useTable} from './context';

const SORT_ICON_SYMBOLS = {
	ascending: 'order-arrow-up',
	descending: 'order-arrow-down',
};

interface IProps
	extends React.ThHTMLAttributes<HTMLTableCellElement>,
		React.TdHTMLAttributes<HTMLTableCellElement> {

	/**
	 * @ignore
	 */
	UNSAFE_resizable?: boolean;

	/**
	 * @ignore
	 */
	UNSAFE_resizerClassName?: string;

	/**
	 * @ignore
	 */
	UNSAFE_resizerOnMouseDown?: (
		event: React.MouseEvent<HTMLDivElement, MouseEvent>
	) => void;

	/**
	 * Aligns the text inside the Cell.
	 */
	align?: 'center' | 'left' | 'right';

	/**
	 * Children content to render content.
	 */
	children: React.ReactNode;

	/**
	 * Sometimes we are unable to remove specific table columns from the DOM
	 * and need to hide it using CSS. This property can be added to the "new"
	 * first or last cell to maintain table styles on the left and right side.
	 */
	delimiter?: 'start' | 'end';

	/**
	 * Fills out the remaining space inside a Cell.
	 */
	expanded?: boolean;

	/**
	 * @ignore
	 */
	index?: number;

	/**
	 * @ignore
	 */
	keyValue?: React.Key;

	/**
	 * Whether the column allows sortable. Only available in the header column.
	 */
	sortable?: boolean;

	/**
	 * Aligns horizontally contents inside the Cell.
	 */
	textAlign?: 'center' | 'end' | 'start';

	/**
	 * Sets a text value if the component's content is not plain text.
	 */
	textValue?: string;

	/**
	 * Truncates the text inside a Cell.
	 */
	truncate?: boolean;

	/**
	 * Sets the column width.
	 */
	width?: string;

	/*
	 * Break the text into lines when necessary.
	 */
	wrap?: boolean;
}

export const Cell = React.forwardRef(
	(
		{
			UNSAFE_resizable,
			UNSAFE_resizerClassName,
			UNSAFE_resizerOnMouseDown,
			align,
			children,
			className,
			delimiter,
			expanded,
			index,
			keyValue,
			onClick,
			sortable,
			textAlign,
			textValue,
			truncate,
			width = 'auto',
			wrap = true,
			...otherProps
		}: IProps,
		ref: React.Ref<HTMLTableCellElement>
	) => {
		const {
			columnsVisibility,
			expandedKeys,
			headCellsCount,
			messages,
			onExpandedChange,
			onSortChange,
			sort,
			sortDescriptionId,
			treegrid,
		} = useTable();

		const [isFocused, setIsFocused] = useState(false);

		const focusWithinProps = useFocusWithin({
			disabled: !treegrid,
			id: keyValue!,
			onFocusChange: setIsFocused,
		});
		const scope = useScope();
		const {divider, expandable, isLoading, key, lazy, level, loadMore} =
			useRow();

		const isHead = scope === Scope.Head;
		const As = isHead ? 'th' : 'td';

		const childrenCount = React.Children.count(children);

		const toggle = useCallback(
			(key: React.Key) => {
				const newExpandedKeys = new Set(expandedKeys);

				if (newExpandedKeys.has(key)) {
					newExpandedKeys.delete(key);
				}
				else {
					newExpandedKeys.add(key);
				}

				onExpandedChange(newExpandedKeys);
			},
			[expandedKeys, onExpandedChange]
		);

		const doSort = useCallback(
			() =>
				onSortChange(
					{
						column: keyValue!,
						direction:
							sort && keyValue === sort.column
								? sort.direction === 'ascending'
									? 'descending'
									: 'ascending'
								: 'ascending',
					},
					textValue!
				),
			[onSortChange, keyValue, sort]
		);

		const isExpandable = (expandable || lazy) && !isLoading;
		const isSortable = isHead && sortable;

		const activeSortDirection =
			isSortable && sort && keyValue === sort.column
				? sort.direction
				: null;

		return (
			<As
				{...otherProps}
				{...focusWithinProps}
				aria-colindex={isHead && !sortable ? undefined : index! + 1}
				aria-describedby={isSortable ? sortDescriptionId : undefined}
				aria-sort={
					isSortable ? activeSortDirection ?? 'none' : undefined
				}
				className={classNames(className, {
					'order-arrow-down-active':
						activeSortDirection === 'descending',
					'order-arrow-up-active':
						activeSortDirection === 'ascending',
					'table-cell-expand': truncate || expanded,
					[`table-cell-${delimiter}`]: delimiter,
					[`table-column-text-${textAlign}`]: textAlign,
					[`text-${align}`]: align,
					'table-cell-ws-nowrap': !wrap,
					'table-focus': focusWithinProps.tabIndex === 0 && isFocused,
					'table-head-title': isHead,
				})}
				colSpan={
					divider
						? headCellsCount + (columnsVisibility ? 1 : 0)
						: undefined
				}
				data-id={
					typeof keyValue === 'number'
						? `number,${keyValue}`
						: `string,${keyValue}`
				}
				onClick={(event) => {
					if (isSortable) {
						event.preventDefault();
						doSort();
					}

					if (onClick) {
						onClick(event);
					}
				}}
				onKeyDown={(event) => {
					if (event.key === Keys.Enter) {
						if (isSortable) {
							event.preventDefault();
							doSort();
						}

						if (treegrid && isExpandable) {
							toggle(key!);
						}
					}
				}}
				ref={ref}
				role={treegrid ? 'gridcell' : undefined}
				style={{
					width,
				}}
				tabIndex={focusWithinProps.tabIndex}
			>
				{isSortable ? (
					<Layout.ContentRow>
						<Layout.ContentCol expand>
							<span className="text-truncate-inline">
								<span className="text-truncate">
									{children}
								</span>
							</span>
						</Layout.ContentCol>

						<Layout.ContentCol>
							<button
								aria-label={
									messages['sortColumn'] && textValue
										? sub(messages['sortColumn'], [
												textValue,
											])
										: undefined
								}
								className="component-action"
								title={messages['sortDescription']}
								type="button"
							>
								<Icon
									symbol={
										activeSortDirection
											? SORT_ICON_SYMBOLS[
													activeSortDirection
												]
											: 'order-arrow'
									}
								/>
							</button>
						</Layout.ContentCol>
					</Layout.ContentRow>
				) : truncate ? (
					<span className="text-truncate-inline">
						<span className="text-truncate">{children}</span>
					</span>
				) : treegrid && index === 0 && !isHead ? (
					<Layout.ContentRow
						style={{
							paddingLeft:
								(level - (expandable || lazy ? 1 : 0)) * 28 -
								(expandable || lazy ? 4 : 0),
						}}
					>
						{isExpandable && (
							<Layout.ContentCol className="autofit-col-toggle">
								<Button
									borderless
									displayType="secondary"
									monospaced
									onClick={() => {
										if (expandable) {
											toggle(key);
										}
										else {
											loadMore();
										}
									}}
									size="xs"
									tabIndex={-1}
									title={messages['expandable']}
								>
									<Icon
										symbol={
											expandedKeys.has(key)
												? 'angle-down'
												: 'angle-right'
										}
									/>
								</Button>
							</Layout.ContentCol>
						)}

						{isLoading && (
							<Layout.ContentCol className="autofit-col-toggle">
								<div className="btn-monospaced btn-xs">
									<LoadingIndicator size="sm" />
								</div>
							</Layout.ContentCol>
						)}

						{React.Children.map(children, (child, index) => {
							if (!child) {
								return null;
							}

							return (
								<Layout.ContentCol
									className={classNames({
										'autofit-col-checkbox':
											React.isValidElement(child) &&

											// @ts-ignore

											child?.type.displayName ===
												'ClayIcon',
									})}
									expand={index === childrenCount - 1}
								>
									{child}
								</Layout.ContentCol>
							);
						})}
					</Layout.ContentRow>
				) : (
					children
				)}

				{UNSAFE_resizable && (
					<div
						className={UNSAFE_resizerClassName}
						onMouseDown={UNSAFE_resizerOnMouseDown}
					/>
				)}
			</As>
		);
	}
);

Cell.displayName = 'Item';

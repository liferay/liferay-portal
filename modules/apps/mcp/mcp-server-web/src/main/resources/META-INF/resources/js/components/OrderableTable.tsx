/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayDropDown, {ClayDropDownWithItems} from '@clayui/drop-down';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClayTable from '@clayui/table';
import classNames from 'classnames';
import {ManagementToolbar} from 'frontend-js-components-web';
import fuzzy from 'fuzzy';
import React, {useEffect, useRef, useState} from 'react';
import {DndProvider, useDrag, useDrop} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

import {FUZZY_OPTIONS} from '../utils/constants';
import Search from './Search';

import '../../css/components/OrderableTable.scss';

const ROW_DRAGGABLE = 'rowDraggable';

interface IAction {
	icon: string;
	isVisible?: ({item}: {item: any}) => boolean;
	label: string;
	onClick: Function;
}

interface IContentRendererProps {
	item: any;
	query: string;
}

interface IContentRenderer {
	component: React.FC<
		{children?: React.ReactNode | undefined} & IContentRendererProps
	>;
	textMatch?: Function;
}

interface IField {
	contentRenderer?: IContentRenderer;
	headingTitle?: boolean;
	label: string;
	name: string;
}

const Row = ({
	actions,
	fields,
	index,
	item,
	onDragCrossover,
	onDrop,
	query,
}: {
	actions?: Array<IAction>;
	fields: Array<IField>;
	index: number;
	item: any;
	onDragCrossover: Function;
	onDrop: Function;
	query: string;
}) => {
	const tableRowRef = useRef<HTMLTableRowElement>(null);

	const [{isDragging}, dragRef] = useDrag({
		collect: (monitor) => ({
			isDragging: monitor.isDragging(),
		}),
		item: {
			index,
			type: ROW_DRAGGABLE,
		},
	});

	const onBlur = () => {
		const currentRow = tableRowRef?.current;

		if (currentRow) {
			const dragging = currentRow.classList.contains('dragging');

			if (dragging) {
				currentRow.classList.remove('dragging');
				onDrop();
			}
		}
	};

	const onKeyDown = (event: React.KeyboardEvent<HTMLButtonElement>) => {
		const currentRow = tableRowRef?.current;

		if (currentRow) {
			const dragging = currentRow.classList.contains('dragging');

			if (event.key === 'Enter') {
				if (!dragging) {
					currentRow.classList.add('dragging');

					const draggedIndex = index;
					const targetIndex = index;

					onDragCrossover({draggedIndex, targetIndex});
				}
				else {
					currentRow.classList.remove('dragging');
					onDrop();
				}
			}
			else if (event.key === 'ArrowDown' && dragging) {
				const draggedIndex = index;
				const targetIndex = index + 1;

				onDragCrossover({draggedIndex, targetIndex});
			}
			else if (event.key === 'ArrowUp' && dragging) {
				const draggedIndex = index;
				const targetIndex = index - 1;

				if (targetIndex >= 0) {
					onDragCrossover({draggedIndex, targetIndex});
				}
			}
			else if (
				(event.key === 'Escape' || event.key === 'Tab') &&
				dragging
			) {
				currentRow.classList.remove('dragging');
				onDrop();
			}
		}
	};

	const [, dropRef] = useDrop({
		accept: ROW_DRAGGABLE,
		hover(item: {index: number; type: string}, monitor) {
			if (!tableRowRef.current || !onDragCrossover) {
				return;
			}

			const draggedIndex = item.index;
			const targetIndex = index;

			if (draggedIndex === targetIndex) {
				return;
			}

			const targetSize = tableRowRef.current.getBoundingClientRect();
			const targetCenter = (targetSize.bottom - targetSize.top) / 2;

			const draggedOffset: {
				x: number;
				y: number;
			} | null = monitor.getClientOffset();

			if (!draggedOffset) {
				return;
			}

			const draggedTop = draggedOffset.y - targetSize.top;

			if (
				(draggedIndex < targetIndex && draggedTop < targetCenter) ||
				(draggedIndex > targetIndex && draggedTop > targetCenter)
			) {
				return;
			}

			onDragCrossover({draggedIndex, targetIndex});

			item.index = targetIndex;
		},
	});

	dragRef(dropRef(tableRowRef));

	return (
		<ClayTable.Row
			className={classNames('orderable-table-row', {
				dragging: isDragging,
			})}
			ref={tableRowRef}
		>
			<ClayTable.Cell className="drag-handle-cell">
				{tableRowRef?.current?.classList.contains('dragging') ? (
					<span aria-live="assertive" className="sr-only">
						{Liferay.Language.get(
							'use-up-and-down-arrows-to-move-the-field-and-press-enter-to-place-it-in-desired-position'
						)}
					</span>
				) : null}

				<ClayButtonWithIcon
					aria-label={Liferay.Util.sub(
						Liferay.Language.get('drag-x'),
						item.label || Liferay.Language.get('item')
					)}
					displayType={null}
					onBlur={onBlur}
					onKeyDown={onKeyDown}
					size="sm"
					symbol="drag"
				/>
			</ClayTable.Cell>

			{fields.map((field) => {
				if (field.contentRenderer) {
					const Component = field.contentRenderer
						.component as React.FC<
						{
							children?: React.ReactNode | undefined;
						} & IContentRendererProps
					>;

					return (
						<ClayTable.Cell key={field.name}>
							<Component item={item} query={query} />
						</ClayTable.Cell>
					);
				}

				const itemFieldValue = String(item[field.name]);

				const fuzzyMatch = fuzzy.match(
					query,
					itemFieldValue,
					FUZZY_OPTIONS
				);

				return (
					<ClayTable.Cell
						headingTitle={field.headingTitle}
						key={field.name}
					>
						{fuzzyMatch ? (
							<span
								dangerouslySetInnerHTML={{
									__html: fuzzyMatch.rendered,
								}}
							/>
						) : (
							<span>{itemFieldValue}</span>
						)}
					</ClayTable.Cell>
				);
			})}

			<ClayTable.Cell className="actions-cell">
				{actions &&
					!!actions.filter(
						(action) =>
							!action.isVisible || action.isVisible({item})
					).length && (
						<ClayDropDown
							trigger={
								<ClayButton
									className="component-action"
									displayType="unstyled"
								>
									<ClayIcon symbol="ellipsis-v" />

									<span className="sr-only">
										{Liferay.Language.get('actions')}
									</span>
								</ClayButton>
							}
						>
							<ClayDropDown.ItemList>
								{actions.map(
									({icon, isVisible, label, onClick}) => {
										if (isVisible && !isVisible({item})) {
											return;
										}

										return (
											<ClayDropDown.Item
												key={label}
												onClick={() =>
													onClick({
														item,
													})
												}
											>
												{icon && (
													<span className="pr-2">
														<ClayIcon
															symbol={icon}
														/>
													</span>
												)}

												{label}
											</ClayDropDown.Item>
										);
									}
								)}
							</ClayDropDown.ItemList>
						</ClayDropDown>
					)}
			</ClayTable.Cell>
		</ClayTable.Row>
	);
};

const Table = ({
	actions,
	fields,
	items,
	onDragCrossover,
	onDrop,
	query,
}: {
	actions?: Array<IAction>;
	fields: Array<IField>;
	items: Array<any>;
	onDragCrossover: Function;
	onDrop: Function;
	query: string;
}) => {
	const [, dropRef] = useDrop({
		accept: ROW_DRAGGABLE,
		drop() {
			onDrop();
		},
	});

	return (
		<ClayTable className="orderable-table" ref={dropRef}>
			<ClayTable.Head>
				<ClayTable.Row>
					<ClayTable.Cell className="drag-handle-cell" />

					{fields.map((field) => (
						<ClayTable.Cell
							className={`cell-${field.name}`}
							headingCell
							key={field.name}
						>
							{field.label}
						</ClayTable.Cell>
					))}

					{actions && <ClayTable.Cell className="actions-cell" />}
				</ClayTable.Row>
			</ClayTable.Head>

			<ClayTable.Body>
				{items.map((item, index) => (
					<Row
						actions={actions}
						fields={fields}
						index={index}
						item={item}
						key={item.externalReferenceCode || item.id || index}
						onDragCrossover={onDragCrossover}
						onDrop={onDrop}
						query={query}
					/>
				))}
			</ClayTable.Body>
		</ClayTable>
	);
};

interface IOrderableTableProps {
	actions?: Array<IAction>;
	className?: string;
	creationMenuItems?: React.ComponentProps<
		typeof ClayDropDownWithItems
	>['items'];
	creationMenuLabel?: string;
	fields: Array<IField>;
	items: Array<any>;
	noItemsButtonLabel: string;
	noItemsDescription: string;
	noItemsTitle: string;
	onOrderChange: (args: {order: string}) => void;
	title?: string;
}

const OrderableTable = ({
	actions,
	className,
	creationMenuItems,
	creationMenuLabel = Liferay.Language.get('new'),
	fields,
	items: initialItems,
	noItemsButtonLabel,
	noItemsDescription,
	noItemsTitle,
	onOrderChange,
	title,
}: IOrderableTableProps) => {
	const [items, setItems] = useState(initialItems);
	const [order, setOrder] = useState(
		initialItems.map((item) => item.externalReferenceCode).join(',')
	);
	const [query, setQuery] = useState('');

	useEffect(() => setItems(initialItems), [initialItems]);

	const onSearch = (query: string) => {
		setQuery(query);

		const regexp = new RegExp(query, 'i');

		setItems(
			query
				? initialItems.filter((item) =>
						fields.some((field) => {
							if (field.contentRenderer?.textMatch) {
								return String(
									field.contentRenderer.textMatch(item)
								).match(regexp);
							}

							return String(item[field.name]).match(regexp);
						})
					) || []
				: initialItems
		);
	};

	return (
		<ClayLayout.Sheet
			className={classNames('orderable-table-sheet', className)}
		>
			{title && (
				<ClayLayout.SheetHeader>
					<h2 className="sheet-title">{title}</h2>
				</ClayLayout.SheetHeader>
			)}

			<ClayLayout.SheetSection>
				<ManagementToolbar.Container>
					<ManagementToolbar.ItemList expand>
						<ManagementToolbar.Item className="nav-item-expand">
							<Search onSearch={onSearch} query={query} />
						</ManagementToolbar.Item>

						{creationMenuItems?.length && (
							<ManagementToolbar.Item>
								{creationMenuItems.length > 1 ? (
									<ClayDropDownWithItems
										items={creationMenuItems}
										trigger={
											<ClayButtonWithIcon
												aria-label={creationMenuLabel}
												className="nav-btn nav-btn-monospaced"
												symbol="plus"
												title={creationMenuLabel}
											/>
										}
									/>
								) : (
									<ClayButtonWithIcon
										aria-label={
											creationMenuItems[0].label ??
											creationMenuLabel
										}
										className="nav-btn nav-btn-monospaced"
										onClick={creationMenuItems[0].onClick}
										symbol="plus"
										title={
											creationMenuItems[0].label ??
											creationMenuLabel
										}
									/>
								)}
							</ManagementToolbar.Item>
						)}
					</ManagementToolbar.ItemList>
				</ManagementToolbar.Container>

				{items.length ? (

					// @ts-ignore

					<DndProvider backend={HTML5Backend}>
						<Table
							actions={actions}
							fields={fields}
							items={items}
							onDragCrossover={({
								draggedIndex,
								targetIndex,
							}: {
								draggedIndex: number;
								targetIndex: number;
							}) => {
								const orderedItems = [...items];

								if (draggedIndex !== targetIndex) {
									orderedItems.splice(draggedIndex, 1);

									orderedItems.splice(
										targetIndex,
										0,
										items[draggedIndex]
									);
								}

								setItems(orderedItems);
							}}
							onDrop={() => {
								const newOrder = items
									.map((item) => item.externalReferenceCode)
									.join(',');

								if (newOrder !== order) {
									setOrder(newOrder);

									onOrderChange({order: newOrder});
								}
							}}
							query={query}
						/>
					</DndProvider>
				) : query ? (
					<ClayEmptyState
						className="text-center"
						description={Liferay.Language.get(
							'sorry,-no-results-were-found'
						)}
						title={Liferay.Language.get('no-results-found')}
					/>
				) : (
					<ClayEmptyState
						className="text-center"
						description={noItemsDescription}
						title={noItemsTitle}
					>
						{creationMenuItems?.length &&
							(creationMenuItems.length > 1 ? (
								<ClayDropDownWithItems
									alignmentPosition={4}
									items={creationMenuItems}
									trigger={
										<ClayButton
											aria-label={creationMenuLabel}
											displayType="secondary"
										>
											{noItemsButtonLabel}
										</ClayButton>
									}
								/>
							) : (
								<ClayButton
									displayType="secondary"
									onClick={creationMenuItems[0].onClick}
								>
									{noItemsButtonLabel}
								</ClayButton>
							))}
					</ClayEmptyState>
				)}
			</ClayLayout.SheetSection>
		</ClayLayout.Sheet>
	);
};

export default OrderableTable;

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {
	Body as ClayTableBody,
	Cell as ClayTableCell,
	Head as ClayTableHead,
	Row as ClayTableRow,
	Table as ClayTable,
} from '@clayui/core';
import {ClayCheckbox, ClayRadio} from '@clayui/form';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import {FDSTableCellHTMLElementBuilderArgs} from '@liferay/js-api/data-set';
import classNames from 'classnames';
import {ClientExtension} from 'frontend-js-components-web';
import {getObjectValueFromPath, throttle} from 'frontend-js-web';
import React, {useContext, useEffect, useMemo, useRef, useState} from 'react';

import FrontendDataSetContext, {
	IFrontendDataSetContext,
} from '../../FrontendDataSetContext';
import Actions from '../../actions/Actions';
import {getInternalCellRenderer} from '../../cell_renderers/getInternalCellRenderer';
import FDSDndProvider from '../../dnd/FDSDndProvider';
import useFDSDrop from '../../dnd/useFDSDrop';
import {
	ILocalizedItemDetails,
	getLocalizedValue,
} from '../../utils/getLocalizedValue';
import {getInputRendererById} from '../../utils/renderer';
import {saveViewSettings} from '../../utils/saveViewSettings';
import {
	IItemsActions,
	ITableSchema,
	IView,
	TRenderer,
	TSort,
	VisibleFieldNames,
} from '../../utils/types';
import ViewsContext, {
	IViewsContext,
	TViewsContextDispatch,
} from '../ViewsContext';
import getCellColumnClassName from '../utils/getCellColumnClassName';

// @ts-ignore

import {EViewsActionTypes} from '../viewsReducer';
import TableContext from './TableContext';
import TableContextProvider from './TableContextProvider';

type Column = {
	fieldName: string;
};

type Field = {
	contentRenderer?: string;
	contentRendererClientExtension?: boolean;
	fieldName: any;
	label?: string;
	localizeLabel?: boolean;
	mapData?: Function;
	sortable?: boolean;
};

type Sorting = {
	column: React.Key;
	direction: 'ascending' | 'descending';
};

const defaultAddItem = {
	editable: true,
	fieldName: 'add',
	id: 'add',
};

const defaultAlwaysVisibleColumns = new Set(['select']);

const getVisibleFields = ({
	fields,
	visibleFieldNames,
}: {
	fields: Array<any>;
	visibleFieldNames: Array<string>;
}) => {
	const visibleFields = fields.filter(
		({fieldName}) => visibleFieldNames[fieldName]
	);

	return visibleFields.length ? visibleFields : fields;
};

const Head = ({
	fields,
	items,
	selectionType,
}: {
	fields: Array<Field>;
	items: Array<any>;
	selectionType?: 'single' | 'multiple';
}) => {
	const {selectable} = useContext(FrontendDataSetContext);

	return (
		<ClayTableHead
			items={selectable ? [{fieldName: 'select'}, ...fields] : fields}
		>
			{(field: Field) => {
				if (field.fieldName === 'select') {
					if (!!items.length && selectionType !== 'multiple') {
						return (
							<ClayTableCell
								key="select"
								scope="col"
								textValue="select-item"
								width="51px"
							>
								<span className="sr-only">
									{Liferay.Language.get('item-selection')}
								</span>
							</ClayTableCell>
						);
					}
				}

				return (
					<HeadCellResizer
						className={getCellColumnClassName(field.fieldName)}
						columnName={field.fieldName}
						key={field.fieldName}
						sortable={field.sortable}
						textValue={field.fieldName}
					>
						{field.label || (
							<span className="sr-only">
								{field.fieldName === 'select'
									? Liferay.Language.get('item-selection')
									: field.fieldName}
							</span>
						)}
					</HeadCellResizer>
				);
			}}
		</ClayTableHead>
	);
};

const Row = ({
	active,
	columns,
	item,
	itemInlineChanges,
	items,
	itemsActions,
	onItemSelectionChange,
	selectionType,
	...otherProps
}: {
	active: boolean;
	columns: Array<Field>;
	item: any;
	itemInlineChanges?: {[key: string]: any};
	items: any[];
	itemsActions: Array<IItemsActions>;
	onItemSelectionChange: Function;
	selectionType?: 'single' | 'multiple';
}) => {
	const {itemsChanges, selectedItemsKey, updateItem} = useContext(
		FrontendDataSetContext
	);

	const SelectionComponent =
		selectionType === 'multiple' ? ClayCheckbox : ClayRadio;

	const id = getObjectValueFromPath({object: item, path: selectedItemsKey});

	return (
		<ClayTableRowOptionalDropTarget
			{...otherProps}
			className={classNames({'table-active': active})}
			item={item}
			items={columns}
			onItemSelectionChange={onItemSelectionChange}
		>
			{(cell: Column) => {
				const cellColumnName = getCellColumnClassName(cell.fieldName);

				switch (cell.fieldName) {
					case 'actions': {
						return (
							<ClayTableCell
								className="cell-item-actions"
								key={`${id}:actions`}
								textValue={Liferay.Language.get('item-actions')}
							>
								{item.editable ? (
									<AddActions />
								) : (
									(itemsActions?.length > 0 ||
										item.actionDropdownItems?.length >
											0) && (
										<Actions
											actions={
												itemsActions ||
												item.actionDropdownItems
											}
											itemData={item}
											itemId={id}
											items={items}
											onItemSelectionChange={
												onItemSelectionChange
											}
										/>
									)
								)}
							</ClayTableCell>
						);
					}
					case 'select':
						return (
							<ClayTableCell
								className="cell-select-item"
								key={`${id}:select`}
								textValue={Liferay.Language.get('select-item')}
							>
								{!item.editable && (
									<SelectionComponent
										checked={active}
										onChange={() =>
											onItemSelectionChange(item)
										}
										title={Liferay.Language.get(
											'select-item'
										)}
										value={id}
									/>
								)}
							</ClayTableCell>
						);
					default: {
						if (item.editable) {
							const field = cell as any;
							let InputRenderer: any = null;

							if (field.inlineEditSettings?.type) {
								InputRenderer = getInputRendererById(
									field.inlineEditSettings.type
								);
							}

							const valuePath = Array.isArray(field.fieldName)
								? field.fieldName.map((property: string) =>
										property === 'LANG'
											? Liferay.ThemeDisplay.getDefaultLanguageId()
											: property
									)
								: [field.fieldName];

							const rootPropertyName = valuePath[0];

							const newItem = itemsChanges![0] || {};

							return (
								<ClayTableCell
									className={cellColumnName}
									key={`${id}:${cell.fieldName}`}
								>
									{InputRenderer ? (
										<InputRenderer
											updateItem={(value: string) => {
												updateItem(
													0,
													rootPropertyName,
													valuePath,
													value
												);
											}}
											value={
												newItem[rootPropertyName] &&
												newItem[rootPropertyName].value
											}
											valuePath={rootPropertyName}
										/>
									) : null}
								</ClayTableCell>
							);
						}

						const localizedValue: ILocalizedItemDetails | null =
							getLocalizedValue(item, cell.fieldName);

						const valuePath =
							localizedValue?.valuePath ?? undefined;

						return (
							<ClayTableCell
								className={cellColumnName}
								key={`${id}:${cell.fieldName}`}
							>
								<CellRenderer
									actions={
										itemsActions || item.actionDropdownItems
									}
									field={cell}
									itemData={item}
									itemId={id}
									itemInlineChanges={itemInlineChanges}
									rootPropertyName={
										localizedValue?.rootPropertyName ??
										undefined
									}
									value={localizedValue?.value ?? undefined}
									valuePath={valuePath}
								/>
							</ClayTableCell>
						);
					}
				}
			}}
		</ClayTableRowOptionalDropTarget>
	);
};

const Body = ({
	fields,
	inlineAddingSettings,
	itemInlineChanges,
	items,
	itemsActions,
	onItemSelectionChange,
	selectionType,
}: {
	fields: Array<Field>;
	inlineAddingSettings?: {
		apiURL?: string;
		defaultBodyContent?: Record<string, any>;
	};
	itemInlineChanges?: {[key: string]: any};
	items: Array<any>;
	itemsActions: Array<IItemsActions>;
	onItemSelectionChange: Function;
	selectionType?: 'single' | 'multiple';
}) => {
	const {
		allItemsSelectedActive,
		selectable,
		selectedItemsKey,
		selectedItemsValue,
	} = useContext(FrontendDataSetContext);

	const columns: Array<Field> = [
		...(selectable ? [{fieldName: 'select'}] : []),
		...fields,
		{fieldName: 'actions'},
	] as Column[];

	return (
		<FDSDndProvider>
			<ClayTableBody
				items={
					inlineAddingSettings ? [...items, defaultAddItem] : items
				}
			>
				{(item: any) => {
					return (
						<Row
							active={
								allItemsSelectedActive ||
								!!selectedItemsValue?.find(
									(element: any) =>
										String(element) ===
										String(
											getObjectValueFromPath({
												object: item,
												path: selectedItemsKey,
											})
										)
								)
							}
							columns={columns}
							item={item}
							itemInlineChanges={itemInlineChanges}
							items={items}
							itemsActions={itemsActions}
							onItemSelectionChange={onItemSelectionChange}
							selectionType={selectionType}
						/>
					);
				}}
			</ClayTableBody>
		</FDSDndProvider>
	);
};

function ClayTableRowOptionalDropTarget({
	children,
	className,
	item,
	items,
	onItemSelectionChange,
	...otherProps
}: React.ComponentProps<typeof ClayTableRow<Column>> & {
	item: any;
	onItemSelectionChange: Function;
}) {
	const [viewsContext] = useContext(ViewsContext);
	const {selectable} = useContext(FrontendDataSetContext);

	const {className: dropClassName, dropRef} = useFDSDrop({item});

	const activeView: IView = viewsContext.activeView;

	const props = {
		...otherProps,
		className: classNames(className, dropClassName),
		items,
		onClick: selectable
			? () => {
					onItemSelectionChange(item, true);
				}
			: undefined,
		ref: dropRef,
	};

	return (
		<ClayTableRow
			{...props}
			{...(activeView.setItemComponentProps?.({item, props}) ?? {})}
		>
			{children}
		</ClayTableRow>
	);
}

/**
 * Wrapper on top of ClayCell to add column resizer capabilities. This
 * should be removed when Clay implements this feature with accessibility.
 */
function HeadCellResizer({
	children,
	columnName,
	...otherProps
}: React.ComponentProps<typeof ClayTableCell> & {
	columnName: string;
}) {
	const {
		draggingAllowed,
		draggingColumnName,
		resizeColumn,
		updateDraggingAllowed,
		updateDraggingColumnName,
	} = useContext(TableContext);

	const [{modifiedFields}, viewsDispatch]: [
		IViewsContext,
		TViewsContextDispatch,
	] = useContext(ViewsContext);

	const cellRef = useRef<HTMLTableCellElement>(null);
	const clientXRef = useRef({current: null});

	useEffect(() => {
		if (columnName && cellRef.current) {
			const boundingClientRect = cellRef.current.getBoundingClientRect();

			viewsDispatch({
				type: EViewsActionTypes.UPDATE_FIELD,
				value: {
					name: columnName,
					resizable: true,
					width: boundingClientRect.width,
				},
			});
		}
	}, [columnName, viewsDispatch]);

	const handleDrag = useMemo(() => {

		// eslint-disable-next-line react-compiler/react-compiler
		return throttle((event) => {
			if (event.clientX === clientXRef.current || !cellRef.current) {
				return;
			}

			updateDraggingColumnName(columnName);

			clientXRef.current = event.clientX;

			const {x: headerCellX} = cellRef.current.getClientRects()[0];
			const newWidth = event.clientX - headerCellX;

			resizeColumn(columnName, newWidth);
		}, 20);
	}, [columnName, resizeColumn, updateDraggingColumnName]);

	function initializeDrag() {
		window.addEventListener('mousemove', handleDrag);
		window.addEventListener(
			'mouseup',
			() => {
				updateDraggingAllowed(true);
				updateDraggingColumnName(null);
				window.removeEventListener('mousemove', handleDrag);
			},
			{once: true}
		);
	}

	const width = useMemo(() => {
		const columnDetails = modifiedFields[columnName];

		return columnDetails && columnDetails.width;
	}, [modifiedFields, columnName]);

	return (
		<ClayTableCell
			{...otherProps}
			UNSAFE_resizable
			UNSAFE_resizerClassName={classNames('dnd-th-resizer', {
				'is-active': columnName === draggingColumnName,
				'is-allowed': draggingAllowed,
			})}
			UNSAFE_resizerOnMouseDown={initializeDrag}
			ref={cellRef}
			scope="col"
			style={{width: width || 'auto'}}
			width={width || 'auto'}
		>
			{children}
		</ClayTableCell>
	);
}

HeadCellResizer.displayName = 'Item';

function AddActions() {
	const {createInlineItem, itemsChanges, toggleItemInlineEdit} = useContext(
		FrontendDataSetContext
	);

	const isMounted = useIsMounted();
	const [loading, setLoading] = useState(false);
	const itemHasChanged =
		itemsChanges![0] && !!Object.keys(itemsChanges![0]).length;

	return (
		<div className="d-flex ml-auto">
			<ClayButtonWithIcon
				aria-labelledby={Liferay.Language.get('close')}
				className="mr-1"
				disabled={!itemHasChanged}
				displayType="secondary"
				onClick={() => {
					toggleItemInlineEdit(0);
				}}
				size="sm"
				symbol="times-small"
			/>

			{loading ? (
				<ClayButton
					aria-labelledby={Liferay.Language.get('loading')}
					disabled
					monospaced
					size="sm"
				>
					<ClayLoadingIndicator size="sm" />
				</ClayButton>
			) : (
				<ClayButtonWithIcon
					aria-labelledby={Liferay.Language.get('confirm')}
					disabled={loading || !itemHasChanged}
					onClick={() => {
						setLoading(true);

						createInlineItem().finally(() => {
							if (isMounted()) {
								setLoading(false);
							}
						});
					}}
					size="sm"
					symbol="check"
				/>
			)}
		</div>
	);
}

function CellRenderer({
	actions,
	field,
	itemData,
	itemId,
	rootPropertyName,
	value,
	valuePath,
}: {
	actions: any;
	field: any;
	itemData: any;
	itemId: string;
	itemInlineChanges: any;
	rootPropertyName: any;
	value: any;
	valuePath: any;
}) {
	const {
		customDataRenderers,
		customRenderers,
		loadData,
		onItemsChange,
		openSidePanel,
	}: IFrontendDataSetContext = useContext(FrontendDataSetContext);
	const [{modifiedFields}] = useContext(ViewsContext) as any;

	const cellRenderer = useMemo(() => {
		const modifiedField = modifiedFields[field.fieldName];

		if (
			field.contentRendererClientExtension &&
			!modifiedField.clientExtensionResolutionError
		) {
			const mergedField = {...field, ...modifiedField};

			return {
				htmlElementBuilder: mergedField.htmlElementBuilder,
				type: 'clientExtension',
			};
		}

		const contentRenderer = field.contentRenderer || 'default';

		const customTableCellRenderer = customRenderers?.tableCell?.find(
			(renderer: TRenderer) => renderer.name === contentRenderer
		);

		if (customTableCellRenderer) {
			return customTableCellRenderer;
		}

		if (customDataRenderers && customDataRenderers[contentRenderer]) {
			return {
				component: customDataRenderers[contentRenderer],
				type: 'internal',
			};
		}

		return getInternalCellRenderer(contentRenderer);
	}, [customDataRenderers, customRenderers, field, modifiedFields]);

	if (cellRenderer?.type === 'clientExtension') {
		return (
			<>
				<ClientExtension<FDSTableCellHTMLElementBuilderArgs>
					args={{itemData, value}}
					htmlElementBuilder={cellRenderer.htmlElementBuilder}
				/>
			</>
		);
	}

	if (cellRenderer?.type === 'internal' && cellRenderer.component) {
		const CellRendererComponent = cellRenderer.component;

		return (
			<>
				{CellRendererComponent && (
					<CellRendererComponent
						actions={actions}
						itemData={itemData}
						itemId={itemId}
						loadData={loadData}
						onItemsChange={onItemsChange}
						openSidePanel={openSidePanel}
						options={field}
						rootPropertyName={rootPropertyName}
						value={value}
						valuePath={valuePath}
					/>
				)}
			</>
		);
	}

	return null;
}

function getVisibleFieldsMap(
	fields: Array<Field>,
	visibleFields: Array<Field>,
	selectable?: boolean
) {
	const visibleFieldsMap = new Map();

	if (selectable) {
		visibleFieldsMap.set('select', 0);
	}

	fields.forEach((field, index) => {
		if (
			visibleFields.findIndex(
				(visibleField) => visibleField.fieldName === field.fieldName
			) >= 0
		) {
			visibleFieldsMap.set(
				String(field.fieldName),
				selectable ? index + 1 : index
			);
		}
	});

	return visibleFieldsMap;
}

const Table = ({
	items = [],
	itemsActions,
	onItemSelectionChange,
	schema,
}: {
	items: Array<any>;
	itemsActions: Array<IItemsActions>;
	onItemSelectionChange: Function;
	schema: ITableSchema;
}) => {
	const {
		appURL,
		id,
		inlineAddingSettings,
		itemsChanges,
		nestedItemsKey,
		nestedItemsReferenceKey,
		portletId,
		selectable,
		selectionType,
		updateActiveSorts,
		updateVisibleFields,
	} = useContext(FrontendDataSetContext);

	const [{sorts, visibleFieldNames}, viewsDispatch] =
		useContext(ViewsContext);

	const visibleFields = getVisibleFields({
		fields: schema.fields,
		visibleFieldNames,
	});

	const [visibleColumns, setVisibleColumns] = useState(() =>
		getVisibleFieldsMap(
			schema.fields as Array<Field>,
			visibleFields,
			selectable
		)
	);

	const columnNames = [];

	if (selectable) {
		columnNames.push('item-selector');
	}

	columnNames.push(
		...visibleFields.map((field) => String(field.fieldName)),
		'item-actions'
	);

	const getSorting = (): Sorting | null => {
		const activeSort = sorts.find((sort: any) => sort.active);

		if (!activeSort) {
			return null;
		}

		return {
			column: activeSort.key,
			direction:
				activeSort.direction === 'desc' ? 'descending' : 'ascending',
		};
	};

	const onSortChange = (sorting: Sorting | null) => {
		let updatedSorts: TSort[] = [];

		updatedSorts = sorts.map((sort: any) =>
			sort.key === sorting?.column
				? {
						...sort,
						active: true,
						direction:
							sorting?.direction === 'ascending' ? 'asc' : 'desc',
					}
				: {
						...sort,
						active: false,
					}
		);

		const newSort: boolean = Boolean(
			!sorts.find((sort: any) => sort.key === sorting?.column)
		);

		if (newSort) {
			updatedSorts.push({
				active: true,
				direction: 'asc',
				key: String(sorting?.column),
			});
		}

		viewsDispatch(updateActiveSorts(updatedSorts));
	};

	return (
		<TableContextProvider columnNames={columnNames}>
			<ClayTable
				alwaysVisibleColumns={
					selectable ? defaultAlwaysVisibleColumns : undefined
				}
				itemIdKey={nestedItemsKey}
				messages={{
					columnsVisibility: Liferay.Language.get(
						'manage-columns-visibility'
					),
					columnsVisibilityCell: itemsActions?.length
						? Liferay.Language.get('item-actions')
						: undefined,
					columnsVisibilityDescription: Liferay.Language.get(
						'at-least-one-column-must-remain-visible'
					),
					columnsVisibilityHeader:
						Liferay.Language.get('columns-visibility'),
					expandable: Liferay.Language.get('expandable'),
					sortDescription: Liferay.Language.get('sortable-column'),
					sorting: Liferay.Language.get(
						'sorted-by-column-x-in-x-order'
					),
				}}
				nestedKey={nestedItemsReferenceKey}
				onSortChange={onSortChange}
				onVisibleColumnsChange={(visibleColumns: any) => {
					const visibleFieldNames: VisibleFieldNames = {};

					schema.fields.forEach(({fieldName}) => {
						visibleFieldNames[String(fieldName)] = false;
					});

					visibleColumns.forEach((value: any, key: any) => {
						visibleFieldNames[key] = true;
					});

					viewsDispatch(updateVisibleFields(visibleFieldNames));

					saveViewSettings({
						appURL,
						id,
						portletId,
						settings: {visibleFieldNames},
					});

					setVisibleColumns(visibleColumns);
				}}
				sort={getSorting()}
				visibleColumns={visibleColumns}
			>
				<Head
					fields={schema.fields as Array<Field>}
					items={items}
					selectionType={selectionType}
				/>

				<Body
					fields={schema.fields as Array<Field>}
					inlineAddingSettings={inlineAddingSettings}
					itemInlineChanges={itemsChanges}
					items={items}
					itemsActions={itemsActions}
					onItemSelectionChange={onItemSelectionChange}
					selectionType={selectionType}
				/>
			</ClayTable>
		</TableContextProvider>
	);
};

export default Table;

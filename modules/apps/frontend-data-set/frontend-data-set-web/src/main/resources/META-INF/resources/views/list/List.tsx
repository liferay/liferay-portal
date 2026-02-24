/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayCheckbox, ClayRadio} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayLayout from '@clayui/layout';
import ClayList from '@clayui/list';
import ClaySticker from '@clayui/sticker';
import {ClayTooltipProvider} from '@clayui/tooltip';
import classNames from 'classnames';
import {getObjectValueFromPath, sub} from 'frontend-js-web';
import React, {forwardRef, useContext} from 'react';

import FrontendDataSetContext from '../../FrontendDataSetContext';
import Actions from '../../actions/Actions';
import ImageRenderer from '../../cell_renderers/ImageRenderer';
import FDSDndProvider from '../../dnd/FDSDndProvider';
import useFDSDrop from '../../dnd/useFDSDrop';
import {getLocalizedValue} from '../../utils/getLocalizedValue';
import {
	DisplayType,
	IHeader,
	ILabelSchema,
	IListSchema,
	IListTitleRenderer,
	IView,
} from '../../utils/types';
import ViewsContext from '../ViewsContext';

const Title = ({
	item,
	title,
	titleRenderer,
}: {
	item: any;
	title: string;
	titleRenderer: IListTitleRenderer;
}) => {
	const TitleRendererComponent = titleRenderer?.component;

	if (TitleRendererComponent) {
		return <TitleRendererComponent itemData={item} />;
	}

	if (title) {
		return (
			<ClayList.ItemTitle>
				{getLocalizedValue(item, title)?.value}
			</ClayList.ItemTitle>
		);
	}

	return null;
};

const ListItem = forwardRef<HTMLLIElement, any>(
	(
		{
			className,
			clayListItemProps,
			item,
			items,
			onItemSelectionChange,
			schema,
		}: {
			className: string;
			clayListItemProps: Object;
			item: any;
			items: any[];
			onItemSelectionChange: Function;
			schema: IListSchema;
		},
		ref
	) => {
		const {
			itemsActions,
			selectable,
			selectedItemsKey,
			selectedItemsValue,
			selectionType,
		} = useContext(FrontendDataSetContext);

		const {
			accessibleNameField,
			description,
			image,
			labels,
			sticker,
			symbol,
			title,
			titleRenderer,
			tooltip,
		} = schema;

		const SelectionInput =
			selectionType === 'single' ? ClayRadio : ClayCheckbox;

		const itemId = getObjectValueFromPath({
			object: item,
			path: selectedItemsKey,
		});

		const accessibleNameItemKey =
			accessibleNameField || title || description;

		const accessibleName =
			getLocalizedValue(item, accessibleNameItemKey)?.value ||
			Liferay.Language.get('item');

		const getLabels = (
			item: any
		): Array<{
			displayType: DisplayType;
			value: string;
		}> => {
			if (!labels) {
				return [];
			}

			return labels.flatMap((label: ILabelSchema) => {
				const {displayTypeKey, displayTypeValues} = label;
				let {displayType} = label;

				if (!displayType && displayTypeValues && displayTypeKey) {
					const keyValue = getLocalizedValue(
						item,
						displayTypeKey
					)?.value;

					displayType = displayTypeValues[keyValue!];
				}

				const value = getLocalizedValue(item, label.value)?.value;

				if (!value) {
					return [];
				}

				return [
					{
						displayType: displayType || DisplayType.UNSTYLED,
						value,
					},
				];
			});
		};

		return (
			<ClayList.Item
				className={classNames(className, {
					active: selectedItemsValue?.includes(itemId),
				})}
				flex
				ref={ref}
				{...clayListItemProps}
			>
				{selectable && (
					<ClayList.ItemField className="justify-content-center selection-control">
						<SelectionInput
							aria-label={sub(
								Liferay.Language.get('select-x'),
								accessibleName
							)}
							checked={
								selectedItemsValue
									? selectedItemsValue
											.map((element) => String(element))
											.includes(String(itemId))
									: false
							}
							onChange={() => {
								onItemSelectionChange(item);
							}}
							value={itemId}
						/>
					</ClayList.ItemField>
				)}

				{image && item[image] ? (
					<ClayList.ItemField>
						<ImageRenderer
							sticker={sticker && item[sticker]}
							value={item[image]}
						/>
					</ClayList.ItemField>
				) : (
					symbol &&
					item[symbol] && (
						<ClayList.ItemField>
							{tooltip && item[tooltip] ? (
								<ClayTooltipProvider>
									<span title={item[tooltip]}>
										<ClaySticker
											{...(sticker && item[sticker])}
										>
											<ClayIcon symbol={item[symbol]} />
										</ClaySticker>
									</span>
								</ClayTooltipProvider>
							) : (
								<ClaySticker {...(sticker && item[sticker])}>
									<ClayIcon symbol={item[symbol]} />
								</ClaySticker>
							)}
						</ClayList.ItemField>
					)
				)}

				<ClayList.ItemField
					className="justify-content-center"
					expand
					onClick={() => {
						if (selectable) {
							onItemSelectionChange(item, true);
						}
					}}
				>
					<Title
						item={item}
						title={title}
						titleRenderer={titleRenderer}
					/>

					{description && (
						<ClayList.ItemText>
							{getLocalizedValue(item, description)?.value}
						</ClayList.ItemText>
					)}

					{labels && (
						<ClayLayout.ContentRow className="mt-1">
							{getLabels(item).map((label, index: number) => (
								<ClayLabel
									className="text-uppercase"
									displayType={label.displayType}
									key={index}
									large
								>
									{label.value}
								</ClayLabel>
							))}
						</ClayLayout.ContentRow>
					)}
				</ClayList.ItemField>

				{(itemsActions || item.actionDropdownItems) && (
					<ClayList.ItemField>
						<Actions
							accessibleName={accessibleName}
							actions={itemsActions || item.actionDropdownItems}
							itemData={item}
							itemId={itemId}
							items={items}
							onItemSelectionChange={onItemSelectionChange}
						/>
					</ClayList.ItemField>
				)}
			</ClayList.Item>
		);
	}
);

const ListItemOptionalDropTarget = ({
	className,
	clayListItemProps,
	item,
	items,
	onItemSelectionChange,
	schema,
}: {
	className?: string;
	clayListItemProps?: object;
	item: any;
	items: any[];
	onItemSelectionChange: Function;
	schema: IListSchema;
}) => {
	const {className: dropClassName, dropRef} = useFDSDrop({item});

	return (
		<ListItem
			className={classNames(className, dropClassName)}
			clayListItemProps={clayListItemProps}
			item={item}
			items={items}
			onItemSelectionChange={onItemSelectionChange}
			ref={dropRef}
			schema={schema}
		/>
	);
};

const List = ({
	header,
	items,
	onItemSelectionChange,
	schema,
}: {
	header: IHeader;
	items: any[];
	onItemSelectionChange: Function;
	schema: IListSchema;
}) => {
	const {selectedItemsKey} = useContext(FrontendDataSetContext);

	const [viewsContext] = useContext(ViewsContext);

	if (!items?.length) {
		return null;
	}

	const activeView: IView = viewsContext.activeView;

	const props = {
		items,
		onItemSelectionChange,
		schema,
	};

	return (
		<ClayLayout.Sheet
			className={classNames('list-sheet', {
				'no-header': !header?.title,
			})}
		>
			{header?.title && (
				<ClayLayout.SheetHeader className="mb-4">
					<h2 className="sheet-title">{header?.title}</h2>
				</ClayLayout.SheetHeader>
			)}

			<FDSDndProvider>
				<ClayList>
					{items.map((item: any, index: number) => (
						<ListItemOptionalDropTarget
							item={item}
							key={
								selectedItemsKey
									? getObjectValueFromPath({
											object: item,
											path: selectedItemsKey,
										})
									: index
							}
							{...props}
							{...(activeView.setItemComponentProps?.({
								item,
								props,
							}) ?? {})}
						/>
					))}
				</ClayList>
			</FDSDndProvider>
		</ClayLayout.Sheet>
	);
};

export default List;

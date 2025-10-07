/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayCheckbox, ClayRadio} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClayList from '@clayui/list';
import ClaySticker from '@clayui/sticker';
import classNames from 'classnames';
import {getObjectValueFromPath} from 'frontend-js-web';
import React, {forwardRef, useContext} from 'react';

import FrontendDataSetContext from '../../FrontendDataSetContext';
import Actions from '../../actions/Actions';
import ImageRenderer from '../../cell_renderers/ImageRenderer';
import FDSDndProvider from '../../dnd/FDSDndProvider';
import useFDSDrop from '../../dnd/useFDSDrop';
import {getLocalizedValue} from '../../utils/getLocalizedValue';
import {
	IHeader,
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
			item,
			items,
			onItemSelectionChange,
			schema,
		}: {
			className: string;
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

		const [viewsContext] = useContext(ViewsContext);

		const activeView: IView = viewsContext.activeView;

		const {description, image, sticker, symbol, title, titleRenderer} =
			schema;

		const SelectionInput =
			selectionType === 'single' ? ClayRadio : ClayCheckbox;

		const itemId = getObjectValueFromPath({
			object: item,
			path: selectedItemsKey,
		});

		const props = {
			className: classNames(className, {
				active: selectedItemsValue?.includes(itemId),
			}),
			flex: true,
		};

		return (
			<ClayList.Item
				{...props}
				{...(activeView.setItemComponentProps?.({item, props}) ?? {})}
				ref={ref}
			>
				{selectable && (
					<ClayList.ItemField className="justify-content-center selection-control">
						<SelectionInput
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
							<ClaySticker {...(sticker && item[sticker])}>
								{item[symbol] && (
									<ClayIcon symbol={item[symbol]} />
								)}
							</ClaySticker>
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
				</ClayList.ItemField>

				<ClayList.ItemField>
					{(itemsActions || item.actionDropdownItems) && (
						<Actions
							actions={itemsActions || item.actionDropdownItems}
							itemData={item}
							itemId={itemId}
							items={items}
							onItemSelectionChange={onItemSelectionChange}
						/>
					)}
				</ClayList.ItemField>
			</ClayList.Item>
		);
	}
);

const ListItemOptionalDropTarget = ({
	item,
	items,
	onItemSelectionChange,
	schema,
}: {
	item: any;
	items: any[];
	onItemSelectionChange: Function;
	schema: IListSchema;
}) => {
	const {className, dropRef} = useFDSDrop({item});

	return (
		<ListItem
			className={className}
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

	if (!items?.length) {
		return null;
	}

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
							items={items}
							key={
								selectedItemsKey
									? getObjectValueFromPath({
											object: item,
											path: selectedItemsKey,
										})
									: index
							}
							onItemSelectionChange={onItemSelectionChange}
							schema={schema}
						/>
					))}
				</ClayList>
			</FDSDndProvider>
		</ClayLayout.Sheet>
	);
};

export default List;

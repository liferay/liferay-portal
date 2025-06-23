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
import PropTypes from 'prop-types';
import React, {useContext} from 'react';

import FrontendDataSetContext from '../../FrontendDataSetContext';
import Actions from '../../actions/Actions';
import ImageRenderer from '../../cell_renderers/ImageRenderer';
import {getLocalizedValue} from '../../utils/getLocalizedValue';

const List = ({header, items, schema}) => {
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

			<ClayList>
				{items.map((item, index) => (
					<ListItem
						item={item}
						key={item[selectedItemsKey] || index}
						schema={schema}
					/>
				))}
			</ClayList>
		</ClayLayout.Sheet>
	);
};

const Title = ({item, title, titleRenderer}) => {
	const TitleRendererComponent = titleRenderer?.component;

	if (TitleRendererComponent) {
		return <TitleRendererComponent itemData={item} />;
	}

	if (title) {
		return (
			<ClayList.ItemTitle>
				{getLocalizedValue(item, title).value}
			</ClayList.ItemTitle>
		);
	}

	return null;
};

const ListItem = ({item, schema}) => {
	const {
		itemsActions,
		onSelect,
		selectItems,
		selectable,
		selectedItemsKey,
		selectedItemsValue,
		selectionType,
	} = useContext(FrontendDataSetContext);

	const {description, image, sticker, symbol, title, titleRenderer} = schema;

	const SelectionInput =
		selectionType === 'single' ? ClayRadio : ClayCheckbox;

	return (
		<ClayList.Item
			className={classNames({
				active: selectedItemsValue.includes(item[selectedItemsKey]),
			})}
			flex
			onClick={() => {
				if (selectable) {
					selectItems(item[selectedItemsKey]);

					onSelect?.({selectedItems: [item]});
				}
			}}
		>
			{selectable && (
				<ClayList.ItemField className="justify-content-center selection-control">
					<SelectionInput
						checked={selectedItemsValue
							.map((element) => String(element))
							.includes(String(item[selectedItemsKey]))}
						onChange={() => {}}
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
							{item[symbol] && <ClayIcon symbol={item[symbol]} />}
						</ClaySticker>
					</ClayList.ItemField>
				)
			)}

			<ClayList.ItemField className="justify-content-center" expand>
				<Title
					item={item}
					title={title}
					titleRenderer={titleRenderer}
				/>

				{description && (
					<ClayList.ItemText>
						{getLocalizedValue(item, description).value}
					</ClayList.ItemText>
				)}
			</ClayList.ItemField>

			<ClayList.ItemField>
				{(itemsActions || item.actionDropdownItems) && (
					<Actions
						actions={itemsActions || item.actionDropdownItems}
						itemData={item}
						itemId={item[selectedItemsKey]}
					/>
				)}
			</ClayList.ItemField>
		</ClayList.Item>
	);
};

List.propTypes = {
	context: PropTypes.any,
	items: PropTypes.arrayOf(
		PropTypes.shape({
			id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
		})
	),
	schema: PropTypes.shape({
		description: PropTypes.string,
		selectedItemValue: PropTypes.string,
		thumbnail: PropTypes.string,
		title: PropTypes.string,
	}),
};

List.defaultTypes = {
	activeItemValue: '',
};

export default List;

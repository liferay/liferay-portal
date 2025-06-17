/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayCardWithInfo} from '@clayui/card';
import classNames from 'classnames';
import React, {useContext, useRef} from 'react';

import FrontendDataSetContext, {
	IFrontendDataSetContext,
} from '../../FrontendDataSetContext';
import {
	DisplayType,
	ICardLabelSchema,
	ICardSchema,
	IItemsActions,
} from '../../index';
import filterItemActions from '../../utils/actionItems/filterItemActions';
import formatActionURL from '../../utils/actionItems/formatActionURL';
import handleActionClick from '../../utils/actionItems/handleActionClick';
import {getLocalizedValue} from '../../utils/getLocalizedValue';
import getRandomId from '../../utils/getRandomId';
import isLink from '../../utils/isLink';
import imagePropsTransformer from '../utils/imagePropsTransformer';

const Card = ({item, schema}: {item: any; schema: ICardSchema}) => {
	const {
		executeAsyncItemAction,
		highlightItems,
		itemsActions,
		loadData,
		onActionDropdownItemClick,
		onInfoPanelToggleButtonClick,
		openModal,
		openSidePanel,
		selectItems,
		selectable,
		selectedItemsKey,
		selectedItemsValue,
		toggleItemInlineEdit,
	}: IFrontendDataSetContext = useContext(FrontendDataSetContext);

	const actionsRef = useRef(
		(itemsActions?.length && itemsActions) || item.actionDropdownItems
	);

	const cardSelected =
		selectable &&
		!!selectedItemsValue?.find(
			(element) => selectedItemsKey && element === item[selectedItemsKey]
		);
	const imageProps =
		schema.image &&
		imagePropsTransformer(getLocalizedValue(item, schema.image)?.value);
	const localizedDescription = getLocalizedValue(
		item,
		schema.description
	)?.value;
	const localizedTitle = getLocalizedValue(item, schema.title)?.value || '';
	const selectedItemKey = selectedItemsKey && item[selectedItemsKey];
	const formattedActions =
		actionsRef.current &&
		(filterItemActions(actionsRef.current, item) as any);

	const getLabels = (
		item: any
	): Array<{
		displayType: DisplayType;
		value: string;
	}> => {
		if (!schema.labels) {
			return [];
		}

		return schema.labels.flatMap((label: ICardLabelSchema) => {
			const {displayTypeKey, displayTypeValues} = label;
			let {displayType} = label;

			if (!displayType && displayTypeValues && displayTypeKey) {
				const keyValue = getLocalizedValue(item, displayTypeKey)?.value;

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
		<ClayCardWithInfo
			actions={formattedActions?.map((action: IItemsActions) => ({
				...action,
				href: isLink(action.target, null)
					? formatActionURL(action.href, item, action.target)
					: null,
				onClick: (event: Event) => {
					handleActionClick({
						action,
						event,
						executeAsyncItemAction,
						highlightItems,
						itemData: item,
						itemId: selectedItemKey,
						loadData,
						onActionDropdownItemClick,
						onInfoPanelToggleButtonClick,
						openModal,
						openSidePanel,
						toggleItemInlineEdit,
					});
				},
			}))}
			description={localizedDescription}
			href={(schema.link && item[schema.link]) || null}
			imgProps={imageProps}
			labels={getLabels(item)}
			onSelectChange={
				selectable
					? () => {
							selectItems(selectedItemKey);
						}
					: undefined
			}
			selected={cardSelected}
			stickerProps={(schema.sticker && item[schema.sticker]) || null}
			symbol={schema.symbol && item[schema.symbol]}
			title={localizedTitle}
		/>
	);
};

const Cards = ({items, schema}: {items: Array<any>; schema: ICardSchema}) => {
	const {selectedItemsKey, style}: IFrontendDataSetContext = useContext(
		FrontendDataSetContext
	);

	if (!items?.length) {
		return null;
	}

	return (
		<div
			className={classNames(
				'cards-container mb-n4',
				style === 'default' && 'px-3 pt-4'
			)}
		>
			<div className="row">
				{items.map((item) => {
					return (
						<div
							className="col-md-3"
							key={
								selectedItemsKey
									? item[selectedItemsKey]
									: getRandomId()
							}
						>
							<Card item={item} schema={schema} />
						</div>
					);
				})}
			</div>
		</div>
	);
};

export {Card};
export default Cards;

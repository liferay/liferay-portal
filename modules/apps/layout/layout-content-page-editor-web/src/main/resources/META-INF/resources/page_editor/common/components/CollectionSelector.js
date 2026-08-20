/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import PropTypes from 'prop-types';
import React from 'react';

import {config} from '../../app/config/index';
import {useCustomCollectionSelectorURL} from '../../app/contexts/CollectionItemContext';
import {ITEM_SELECTOR_VARIANTS} from '../../app/utils/itemSelectorVariants';
import itemSelectorValueToCollection from '../../app/utils/item_selector_value/itemSelectorValueToCollection';
import ItemSelector from './ItemSelector';

const DEFAULT_OPTION_MENU_ITEMS = [];

export default function CollectionSelector({
	collectionItem,
	itemSelectorURL,
	label,
	onBeforeCollectionSelect,
	onCollectionSelect,
	optionsMenuItems = DEFAULT_OPTION_MENU_ITEMS,
	variant = ITEM_SELECTOR_VARIANTS.input,
}) {
	const eventName = `${config.portletNamespace}selectInfoList`;

	const customCollectionSelectorURL = useCustomCollectionSelectorURL();

	const filterConfig = collectionItem?.config ?? {};

	const isPrefiltered = !!Object.keys(filterConfig).length;

	return (
		<>
			<ItemSelector
				className="mb-0"
				eventName={eventName}
				itemSelectorURL={
					customCollectionSelectorURL ||
					itemSelectorURL ||
					config.infoListSelectorURL
				}
				label={label}
				onBeforeItemSelect={onBeforeCollectionSelect}
				onItemSelect={onCollectionSelect}
				optionsMenuItems={optionsMenuItems}
				quickMappedInfoItems={
					config.selectedMappingTypes?.linkedCollection
				}
				selectedItem={collectionItem}
				showMappedFeedback
				showMappedItems={
					!!config.selectedMappingTypes?.linkedCollection
				}
				transformValueCallback={itemSelectorValueToCollection}
				variant={variant}
			/>

			{isPrefiltered && (
				<p className="text-info">
					<ClayIcon className="mr-2 mt-0" symbol="info-circle-open" />

					<span className="text-2">
						{Liferay.Language.get('collection-filtered')}
					</span>
				</p>
			)}
		</>
	);
}

CollectionSelector.propTypes = {
	collectionItem: PropTypes.shape({title: PropTypes.string}),
	label: PropTypes.string,
	onBeforeCollectionSelect: PropTypes.func,
	onCollectionSelect: PropTypes.func.isRequired,
};

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {LayoutDataItem} from '../../types/layout_data/LayoutData';
import {
	LAYOUT_DATA_ITEM_TYPES,
	LayoutDataItemType,
} from '../config/constants/layoutDataItemTypes';

const RENAMABLE_ITEM_TYPES: LayoutDataItemType[] = [
	LAYOUT_DATA_ITEM_TYPES.collection,
	LAYOUT_DATA_ITEM_TYPES.container,
	LAYOUT_DATA_ITEM_TYPES.form,
	LAYOUT_DATA_ITEM_TYPES.fragment,
	LAYOUT_DATA_ITEM_TYPES.row,
];

export default function canBeRenamed(item: LayoutDataItem) {
	return RENAMABLE_ITEM_TYPES.includes(item.type);
}

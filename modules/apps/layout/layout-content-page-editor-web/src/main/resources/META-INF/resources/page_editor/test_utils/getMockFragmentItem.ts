/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {LAYOUT_DATA_ITEM_TYPES} from '../app/config/constants/layoutDataItemTypes';
import {FragmentLayoutDataItem} from '../types/layout_data/FragmentLayoutDataItem';

export default function getMockFragmentItem({
	fragmentEntryLinkId = '0',
	itemId = 'fragment-id',
}: {
	fragmentEntryLinkId?: string;
	itemId?: string;
} = {}): FragmentLayoutDataItem {
	return {
		children: [],
		config: {
			fragmentEntryLinkId,
			landscapeMobile: {},
			portraitMobile: {},
			tablet: {},
		},
		itemId,
		parentId: '',
		type: LAYOUT_DATA_ITEM_TYPES.fragment,
	};
}

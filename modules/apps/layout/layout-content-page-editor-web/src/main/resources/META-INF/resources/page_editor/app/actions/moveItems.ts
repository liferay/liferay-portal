/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FragmentEntryLink} from './addFragmentEntryLinks';
import {MOVE_ITEM} from './types';

import type {LayoutData} from '../../types/layout_data/LayoutData';

export default function moveItems({
	fragmentEntryLinks,
	itemIds,
	layoutData,
}: {
	fragmentEntryLinks: FragmentEntryLink[];
	itemIds: string[];
	layoutData: LayoutData;
}) {
	return {
		fragmentEntryLinks,
		itemIds,
		layoutData,
		type: MOVE_ITEM,
	} as const;
}

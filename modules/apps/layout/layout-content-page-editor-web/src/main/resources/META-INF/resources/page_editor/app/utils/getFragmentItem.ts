/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {LayoutData} from '../../types/layout_data/LayoutData';
import {isFragment} from './isFragment';

export default function getFragmentItem(
	layoutData: LayoutData | null,
	fragmentEntryLinkId: string | null
) {
	if (!layoutData || !fragmentEntryLinkId) {
		return null;
	}

	return Object.values(layoutData.items).find(
		(item) =>
			isFragment(item) &&
			item.config.fragmentEntryLinkId === fragmentEntryLinkId
	);
}

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {LayoutData, LayoutDataItem} from '../../types/layout_data/LayoutData';
import {VIEWPORT_SIZES, ViewportSize} from '../config/constants/viewportSizes';
import {isItemHidden} from './isItemHidden';

export default function isItemEmpty(
	item: LayoutDataItem,
	layoutData: LayoutData,
	selectedViewportSize: ViewportSize = VIEWPORT_SIZES.desktop
) {
	return (
		!item.children.length ||
		item.children.every((childId) =>
			isItemHidden(layoutData, childId, selectedViewportSize)
		)
	);
}

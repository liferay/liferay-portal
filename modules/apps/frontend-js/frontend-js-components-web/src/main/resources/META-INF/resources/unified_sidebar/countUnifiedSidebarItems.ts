/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {UnifiedSidebarItem} from './types';

export function countUnifiedSidebarItems(
	items: Array<UnifiedSidebarItem>
): number {
	return items.reduce(
		(count, item) =>
			count +
			(item.href ? 1 : 0) +
			(item.items ? countUnifiedSidebarItems(item.items) : 0),
		0
	);
}

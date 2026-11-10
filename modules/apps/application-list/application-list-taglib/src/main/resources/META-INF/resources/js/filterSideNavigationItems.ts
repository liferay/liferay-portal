/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	UnifiedSidebarFilter,
	filterUnifiedSidebarItems,
} from 'frontend-js-components-web';

import {SideNavigationItem, SideNavigationScope} from './types/SideNavigation';

function addScopeItems(
	items: Array<SideNavigationItem>,
	filteredItems: Array<SideNavigationItem>
): Array<SideNavigationItem> {
	return filteredItems.flatMap((filteredItem, index) => {
		if (
			!filteredItem.scope ||
			filteredItem.scope === filteredItems[index - 1]?.scope
		) {
			return filteredItem;
		}

		const scopeItem = items.find(
			(item) => isScopeMarker(item) && item.scope === filteredItem.scope
		);

		return scopeItem ? [scopeItem, filteredItem] : filteredItem;
	});
}

function removeScopeMarkers(
	items: Array<SideNavigationItem>
): Array<SideNavigationItem> {
	return items.flatMap((item) => {
		if (isScopeMarker(item)) {
			return [];
		}

		return item.items
			? {...item, items: removeScopeMarkers(item.items)}
			: item;
	});
}

export function filterSideNavigationItems(
	items: Array<SideNavigationItem>,
	query: string
): UnifiedSidebarFilter<SideNavigationItem> {
	if (!query) {
		return filterUnifiedSidebarItems(items, query);
	}

	const filter = filterUnifiedSidebarItems(removeScopeMarkers(items), query);

	return {...filter, items: addScopeItems(items, filter.items)};
}

export function isScopeMarker(
	item: SideNavigationItem
): item is SideNavigationItem & {scope: SideNavigationScope} {
	return Boolean(item.scopeMarker && item.scope);
}

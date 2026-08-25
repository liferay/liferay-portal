/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useMemo, useState} from 'react';

import {SideNavigationItem} from './types/SideNavigation';

interface SideNavigationFilter {
	expandedKeys?: Set<React.Key>;
	items: Array<SideNavigationItem>;
	numberOfMatches: number;
}

const EMPTY_KEYS_SET = new Set<React.Key>();
const EMPTY_FILTER = {
	expandedKeys: EMPTY_KEYS_SET,
	items: [],
	numberOfMatches: 0,
};

function removeFilterOnlyItems(
	items?: Array<SideNavigationItem>
): Array<SideNavigationItem> {
	if (!items) {
		return [];
	}

	const visibleItems = items.reduce<Array<SideNavigationItem>>(
		(visibleItems, item) => {
			if (item.filterOnly) {
				return visibleItems;
			}

			const visibleChildItems = removeFilterOnlyItems(item.items);

			if (item.items && visibleChildItems !== item.items) {
				return visibleItems.concat({
					...item,
					items: visibleChildItems.length
						? visibleChildItems
						: undefined,
				});
			}

			return visibleItems.concat(item);
		},
		[]
	);

	const itemsUnchanged =
		visibleItems.length === items.length &&
		visibleItems.every(
			(visibleItem, index) => visibleItem === items[index]
		);

	if (itemsUnchanged) {
		return items;
	}

	return visibleItems;
}

export function filterItemsByQuery(
	items: Array<SideNavigationItem>,
	query: string
): SideNavigationFilter {
	if (!query) {
		return {items: removeFilterOnlyItems(items), numberOfMatches: 0};
	}

	return items.reduce<Required<SideNavigationFilter>>((result, item) => {
		const labelMatches = item.label.toLowerCase().includes(query);

		if (item.items && item.items.length) {
			const {expandedKeys, items, numberOfMatches} = filterItemsByQuery(
				item.items,
				query
			);

			if (items.length) {
				return {
					expandedKeys: new Set([
						...result.expandedKeys,
						...(expandedKeys ?? EMPTY_KEYS_SET),
						item.id,
					]),

					items: result.items.concat({
						...item,
						items,
					}),
					numberOfMatches:
						result.numberOfMatches +
						numberOfMatches +
						(labelMatches ? 1 : 0),
				};
			}

			if (labelMatches) {
				const visibleChildItems = removeFilterOnlyItems(item.items);

				return {
					expandedKeys: new Set([...result.expandedKeys, item.id]),
					items: result.items.concat({
						...item,
						items: visibleChildItems.length
							? visibleChildItems
							: undefined,
					}),
					numberOfMatches: result.numberOfMatches + 1,
				};
			}
		}
		else if (labelMatches) {
			return {
				expandedKeys: result.expandedKeys,
				items: result.items.concat(item),
				numberOfMatches: result.numberOfMatches + 1,
			};
		}

		return result;
	}, EMPTY_FILTER);
}

export function useSideNavigationFilter(items: Array<SideNavigationItem>) {
	const [query, setQuery] = useState('');

	const filter = useMemo(
		() => filterItemsByQuery(items, query),
		[items, query]
	);

	return {
		expandedKeys: filter.expandedKeys,
		isFilterActive: !!query,
		items: filter.items,
		numberOfMatches: filter.numberOfMatches,
		setQuery: (query: string) => setQuery(query.trim().toLowerCase()),
	};
}

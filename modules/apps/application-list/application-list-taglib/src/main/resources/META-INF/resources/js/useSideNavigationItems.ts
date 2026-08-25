/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';
import {useCallback, useMemo, useRef, useState} from 'react';

import {
	SideNavigationItem,
	SideNavigationItemsMap,
} from './types/SideNavigation';

function mergeNavigationItems(
	items: Array<SideNavigationItem>,
	sideNavigationItemsMap: SideNavigationItemsMap
): Array<SideNavigationItem> {
	const mergedItems = items.map((item) => {
		const sideNavigationItems = sideNavigationItemsMap[item.id];

		if (sideNavigationItems?.length) {
			return {
				...item,
				items: sideNavigationItems.map((sideNavigationItem) => ({
					...sideNavigationItem,
					filterOnly: true,
				})),
			};
		}

		if (item.items) {
			const mergedChildItems = mergeNavigationItems(
				item.items,
				sideNavigationItemsMap
			);

			if (mergedChildItems !== item.items) {
				return {...item, items: mergedChildItems};
			}
		}

		return item;
	});

	if (mergedItems.every((mergedItem, index) => mergedItem === items[index])) {
		return items;
	}

	return mergedItems;
}

export function useSideNavigationItems(
	items: Array<SideNavigationItem>,
	navigationItemsUrl: string
) {
	const [loading, setLoading] = useState(false);
	const [sideNavigationItemsMap, setSideNavigationItemsMap] =
		useState<SideNavigationItemsMap>();

	const promiseRef = useRef<Promise<void>>();

	const prefetchFilterOnlyItems = useCallback(() => {
		if (promiseRef.current) {
			return;
		}

		setLoading(true);

		promiseRef.current = fetch(navigationItemsUrl)
			.then((response) => {
				if (!response.ok) {
					throw new Error(
						`Unable to load the navigation items: ${response.statusText}`
					);
				}

				return response.json();
			})
			.then((responseJSON) =>
				setSideNavigationItemsMap(responseJSON.navigationItems ?? {})
			)
			.catch((error) => {
				promiseRef.current = undefined;

				// eslint-disable-next-line no-console
				console.error(error);
			})
			.finally(() => setLoading(false));
	}, [navigationItemsUrl]);

	const mergedItems = useMemo(
		() =>
			sideNavigationItemsMap
				? mergeNavigationItems(items, sideNavigationItemsMap)
				: items,
		[items, sideNavigationItemsMap]
	);

	return {items: mergedItems, loading, prefetchFilterOnlyItems};
}

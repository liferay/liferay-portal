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

const REQUEST_TIMEOUT = 10000;

function mergeNavigationItems(
	items: Array<SideNavigationItem>,
	sideNavigationItemsMap: SideNavigationItemsMap
): Array<SideNavigationItem> {
	const mergedItems = items.map((item) => {
		const sideNavigationItems = Object.hasOwn(
			sideNavigationItemsMap,
			item.id
		)
			? sideNavigationItemsMap[item.id]
			: undefined;

		const mergedChildItems = item.items
			? mergeNavigationItems(item.items, sideNavigationItemsMap)
			: undefined;

		if (sideNavigationItems?.length) {
			const filterOnlyItems = sideNavigationItems.map(
				(sideNavigationItem) => ({
					...sideNavigationItem,
					filterOnly: true,
				})
			);

			return {
				...item,
				items: mergedChildItems
					? mergedChildItems.concat(filterOnlyItems)
					: filterOnlyItems,
			};
		}

		if (mergedChildItems && mergedChildItems !== item.items) {
			return {...item, items: mergedChildItems};
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
	navigationItemsURL: string
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

		const abortController = new AbortController();

		const timeoutId = setTimeout(
			() => abortController.abort(),
			REQUEST_TIMEOUT
		);

		promiseRef.current = fetch(navigationItemsURL, {
			signal: abortController.signal,
		})
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
			.finally(() => {
				clearTimeout(timeoutId);

				setLoading(false);
			});
	}, [navigationItemsURL]);

	const mergedItems = useMemo(
		() =>
			sideNavigationItemsMap
				? mergeNavigationItems(items, sideNavigationItemsMap)
				: items,
		[items, sideNavigationItemsMap]
	);

	return {items: mergedItems, loading, prefetchFilterOnlyItems};
}

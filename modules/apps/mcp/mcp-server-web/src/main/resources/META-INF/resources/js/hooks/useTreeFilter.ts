/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Key, useMemo, useRef, useState} from 'react';

import {TreeFilter, TreeItem} from '../utils/filterTree';

interface UseTreeFilterOptions<T extends TreeItem<T>> {
	expandedKeys: Set<Key>;
	filter: (items: T[], query: string) => TreeFilter;
	items: T[];
	onExpandedChange: (expandedKeys: Set<Key>) => void;
	restoreExpansion?: boolean;
}

export default function useTreeFilter<T extends TreeItem<T>>({
	expandedKeys,
	filter,
	items,
	onExpandedChange,
	restoreExpansion = false,
}: UseTreeFilterOptions<T>) {
	const [query, setQuery] = useState('');

	const expandedKeysBeforeFilterRef = useRef<Set<Key> | null>(null);

	const trimmedQuery = query.trim();

	const {matchCount, visibleKeys} = useMemo(
		() => filter(items, trimmedQuery),
		[filter, items, trimmedQuery]
	);

	const onSearch = (value: string) => {
		setQuery(value);

		if (restoreExpansion) {
			if (!value.trim()) {
				onExpandedChange(
					expandedKeysBeforeFilterRef.current ?? expandedKeys
				);

				expandedKeysBeforeFilterRef.current = null;

				return;
			}

			expandedKeysBeforeFilterRef.current ??= expandedKeys;
		}

		onExpandedChange(filter(items, value).expandedKeys);
	};

	const isVisible = (item: T) => !trimmedQuery || visibleKeys.has(item.id);

	const getRowClassName = (item: T) =>
		isVisible(item) ? undefined : 'd-none';

	return {
		getRowClassName,
		isVisible,
		matchCount,
		onSearch,
		query,
		trimmedQuery,
	};
}

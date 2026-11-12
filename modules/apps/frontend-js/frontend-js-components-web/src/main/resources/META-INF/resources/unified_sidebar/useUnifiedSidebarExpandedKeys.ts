/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useState} from 'react';

export function useUnifiedSidebarExpandedKeys({
	expandedKeys: externalExpandedKeys,
	expandedKeysSessionKey,
	filterExpandedKeys,
	isFilterActive,
}: {
	expandedKeys: Array<React.Key>;
	expandedKeysSessionKey: string;
	filterExpandedKeys?: Set<React.Key>;
	isFilterActive: boolean;
}) {
	const [initialExpandedKeys] = useState<Set<React.Key>>(
		() => new Set(externalExpandedKeys)
	);

	const [userExpandedKeys, setUserExpandedKeys] =
		useState<Set<React.Key>>(initialExpandedKeys);

	const [filterCollapse, setFilterCollapse] = useState<{
		appliesTo: Set<React.Key>;
		expandedKeys: Set<React.Key>;
	}>();

	const expandedKeys =
		filterCollapse && filterCollapse.appliesTo === filterExpandedKeys
			? filterCollapse.expandedKeys
			: filterExpandedKeys ?? userExpandedKeys;

	const updateExpandedKeys = useCallback(
		(updatedExpandedKeys: Set<React.Key>) => {
			if (isFilterActive && filterExpandedKeys) {
				setFilterCollapse({
					appliesTo: filterExpandedKeys,
					expandedKeys: updatedExpandedKeys,
				});

				return;
			}

			setUserExpandedKeys(updatedExpandedKeys);

			Liferay.Util.Session.set(
				expandedKeysSessionKey,
				Array.from(updatedExpandedKeys).join(',')
			).catch(() => {});
		},
		[expandedKeysSessionKey, filterExpandedKeys, isFilterActive]
	);

	return {expandedKeys, initialExpandedKeys, updateExpandedKeys};
}

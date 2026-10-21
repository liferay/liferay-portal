/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type TreeFilter = {
	expandedKeys: Set<string>;
	matchCount: number;
	visibleKeys: Set<string>;
};

export type TreeItem<T> = {
	children?: T[];
	id: string;
	name: string;
};

export default function filterTree<T extends TreeItem<T>>(
	tree: T[],
	matches: (item: T) => boolean
): TreeFilter {
	const expandedKeys = new Set<string>();
	const visibleKeys = new Set<string>();

	const matchCount = collectVisibleKeys(
		tree,
		matches,
		expandedKeys,
		visibleKeys
	);

	return {expandedKeys, matchCount, visibleKeys};
}

function collectVisibleKeys<T extends TreeItem<T>>(
	tree: T[],
	matches: (item: T) => boolean,
	expandedKeys: Set<string>,
	visibleKeys: Set<string>
): number {
	let matchCount = 0;

	for (const item of tree) {
		const childMatchCount = collectVisibleKeys(
			item.children ?? [],
			matches,
			expandedKeys,
			visibleKeys
		);

		if (childMatchCount) {
			expandedKeys.add(item.id);
		}

		const itemMatches = matches(item);

		if (itemMatches || childMatchCount) {
			visibleKeys.add(item.id);
		}

		matchCount += childMatchCount + Number(itemMatches);
	}

	return matchCount;
}

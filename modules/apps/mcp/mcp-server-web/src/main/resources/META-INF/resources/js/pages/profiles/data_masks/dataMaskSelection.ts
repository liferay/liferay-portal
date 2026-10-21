/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DataMask, DataMaskTypeKey} from '../../../types/DataMask';
import {ProfileDataMaskRow} from '../../../types/ProfileDataMask';
import filterTree, {TreeFilter} from '../../../utils/filterTree';

export type DataMaskTreeItem = {
	children?: DataMaskTreeItem[];
	id: string;
	maskCount: number;
	name: string;
};

const DATA_MASK_GROUP_ID_PREFIX = 'maskType:';

const DATA_MASK_GROUP_KEYS: DataMaskTypeKey[] = ['system', 'custom'];

export function buildDataMaskTree(dataMasks: DataMask[]): DataMaskTreeItem[] {
	return DATA_MASK_GROUP_KEYS.flatMap((groupKey) => {
		const groupDataMasks = dataMasks.filter(
			(dataMask) =>
				dataMask.maskType?.key === groupKey &&
				dataMask.externalReferenceCode
		);

		if (!groupDataMasks.length) {
			return [];
		}

		const children = groupDataMasks.map((dataMask) => ({
			id: dataMask.externalReferenceCode as string,
			maskCount: 1,
			name: dataMask.name,
		}));

		return [
			{
				children,
				id: `${DATA_MASK_GROUP_ID_PREFIX}${groupKey}`,
				maskCount: children.length,
				name: groupDataMasks[0].maskType.name,
			},
		];
	});
}

export function filterDataMaskTree(
	tree: DataMaskTreeItem[],
	query: string
): TreeFilter {
	const loweredQuery = query.trim().toLowerCase();

	if (!loweredQuery) {
		return {
			expandedKeys: new Set(tree.map((group) => group.id)),
			matchCount: 0,
			visibleKeys: new Set<string>(),
		};
	}

	return filterTree(
		tree,
		(item) =>
			!item.children && item.name.toLowerCase().includes(loweredQuery)
	);
}

export function getAvailableDataMasks(
	dataMasks: DataMask[],
	rows: ProfileDataMaskRow[]
): DataMask[] {
	const assignedExternalReferenceCodes = new Set(
		rows.map((row) => row.dataMaskExternalReferenceCode)
	);

	return dataMasks.filter(
		(dataMask) =>
			!assignedExternalReferenceCodes.has(
				dataMask.externalReferenceCode ?? ''
			)
	);
}

export function getNextExecutionOrder(rows: ProfileDataMaskRow[]): number {
	return (
		rows.reduce(
			(maxExecutionOrder, row) =>
				Math.max(maxExecutionOrder, row.executionOrder),
			0
		) + 1
	);
}

export function getSelectedDataMaskExternalReferenceCodes(
	tree: DataMaskTreeItem[],
	selectedKeys: Set<string | number>
): string[] {
	return tree.flatMap((group) =>
		(group.children ?? [])
			.filter((child) => selectedKeys.has(child.id))
			.map((child) => child.id)
	);
}

/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Group} from '../../types';
import {deleteEmptyGroups} from './deleteEmptyGroups';
import {detachNode} from './detachNode';
import {findGroup} from './findGroup';
import {findNode} from './findNode';
import {insertIntoGroup} from './insertIntoGroup';
import {unwrapRedundantGroups} from './unwrapRedundantGroups';

export function moveRule(
	root: Group,
	nodeId: string,
	targetGroupId: string,
	targetIndex: number
): Group {
	const node = findNode(root, nodeId);

	if (!node) {
		return root;
	}

	const targetGroup = findGroup(root, targetGroupId);

	const currentIndex = targetGroup
		? targetGroup.items.findIndex((item) => item.id === nodeId)
		: -1;

	const index =
		currentIndex !== -1 && currentIndex < targetIndex
			? targetIndex - 1
			: targetIndex;

	return unwrapRedundantGroups(
		deleteEmptyGroups(
			insertIntoGroup(
				detachNode(root, nodeId),
				targetGroupId,
				node,
				index
			)
		)
	);
}

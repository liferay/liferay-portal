/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Group} from '../../types';
import {deleteEmptyGroups} from './deleteEmptyGroups';
import {detachNode} from './detachNode';
import {findNode} from './findNode';
import {findParent} from './findParent';
import {insertIntoGroup} from './insertIntoGroup';
import {unwrapRedundantGroups} from './unwrapRedundantGroups';
import {wrapNode} from './wrapNode';

export function moveGroup(
	root: Group,
	nodeId: string,
	targetId: string,
	conjunction = 'AND'
): Group {
	const node = findNode(root, nodeId);

	if (!node || nodeId === targetId) {
		return root;
	}

	const detached = detachNode(root, nodeId);
	const parent = findParent(detached, targetId);

	if (parent && parent.items.length === 1) {
		return unwrapRedundantGroups(
			deleteEmptyGroups(insertIntoGroup(detached, parent.id, node, 1))
		);
	}

	return unwrapRedundantGroups(
		deleteEmptyGroups(wrapNode(detached, targetId, node, conjunction))
	);
}

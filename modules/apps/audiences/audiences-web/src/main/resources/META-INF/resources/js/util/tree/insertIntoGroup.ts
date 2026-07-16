/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CriteriaNode, Group} from '../../types';
import {insertAt} from './insertAt';
import {isGroup} from './isGroup';

export function insertIntoGroup(
	group: Group,
	groupId: string,
	node: CriteriaNode,
	index: number
): Group {
	if (group.id === groupId) {
		return {...group, items: insertAt(group.items, index, node)};
	}

	return {
		...group,
		items: group.items.map((child) =>
			isGroup(child)
				? insertIntoGroup(child, groupId, node, index)
				: child
		),
	};
}

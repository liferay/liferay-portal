/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CriteriaNode, Group} from '../../types';
import {createGroup} from './createGroup';
import {isGroup} from './isGroup';

export function wrapNode(
	group: Group,
	targetId: string,
	node: CriteriaNode,
	conjunction: string
): Group {
	return {
		...group,
		items: group.items.map((child) => {
			if (child.id === targetId) {
				return createGroup(conjunction, [child, node]);
			}

			return isGroup(child)
				? wrapNode(child, targetId, node, conjunction)
				: child;
		}),
	};
}

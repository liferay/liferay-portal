/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Group} from '../../types';
import {isGroup} from './isGroup';
import {unwrapGroup} from './unwrapGroup';

export function unwrapRedundantGroups(group: Group): Group {
	const items = group.items.map((node) =>
		isGroup(node) ? unwrapGroup(node) : node
	);

	const [onlyNode] = items;

	if (items.length === 1 && isGroup(onlyNode)) {
		return {
			...group,
			conjunction: onlyNode.conjunction,
			items: onlyNode.items,
		};
	}

	return {...group, items};
}

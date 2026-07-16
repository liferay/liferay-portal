/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CriteriaNode, Group} from '../../types';
import {isGroup} from './isGroup';

export function unwrapGroup(group: Group): CriteriaNode {
	const items = group.items.map((node) =>
		isGroup(node) ? unwrapGroup(node) : node
	);

	if (items.length === 1) {
		return items[0];
	}

	return {...group, items};
}

/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Structure, StructureChild} from '../types/Structure';

export default function getOwnChildren(
	children: Structure['children']
): StructureChild[] {
	const ownChildren: StructureChild[] = [];

	for (const child of children.values()) {
		if (child.type === 'group' && !child.isRepeatable) {
			ownChildren.push(...getOwnChildren(child.children));
		}
		else {
			ownChildren.push(child);
		}
	}

	return ownChildren;
}

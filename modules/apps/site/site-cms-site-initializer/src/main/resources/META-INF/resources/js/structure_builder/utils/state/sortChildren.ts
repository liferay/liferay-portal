/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Structure, StructureChild} from '../../types/Structure';

export default function sortChildren(
	children: Structure['children']
): Structure['children'] {
	const array = Array.from(children.values());

	const sorted = array.sort((a, b) => {
		return getWeight(a) - getWeight(b);
	});

	const map = new Map(sorted.map((child) => [child.uuid, child]));

	return map;
}

function getWeight(child: StructureChild) {
	if (child.type === 'group') {
		return 3;
	}

	if (child.type === 'referenced-structure') {
		return 2;
	}

	if (child.type === 'related-content') {
		return 1;
	}

	return 0;
}

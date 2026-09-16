/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Group, Structure, StructureChild} from '../../types/Structure';
import {Uuid} from '../../types/Uuid';
import sortChildren from './sortChildren';

export default function moveChildren({
	items,
	root,
	targetUuid,
}: {
	items: StructureChild[];
	root: Structure | Group;
	targetUuid: Uuid;
}): Structure['children'] | Group['children'] {
	const children = new Map();

	// Iterate over children

	for (const rootChild of root.children.values()) {

		// Skip if this child is one of the items to move

		if (items.some(({uuid}) => uuid === rootChild.uuid)) {
			continue;
		}

		// If it's a repeatable group, build it with recursive call

		if (rootChild.type === 'group') {
			const group: Group = {
				...rootChild,
				children: moveChildren({
					items,
					root: rootChild,
					targetUuid,
				}),
			};

			children.set(group.uuid, group);
		}
		else {
			children.set(rootChild.uuid, rootChild);
		}
	}

	// Insert items if this is the target parent

	if (root.uuid === targetUuid) {
		for (const item of items) {
			children.set(item.uuid, item);
		}
	}

	return sortChildren(children);
}

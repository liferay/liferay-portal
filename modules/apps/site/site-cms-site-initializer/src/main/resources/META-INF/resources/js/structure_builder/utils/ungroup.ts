/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {RepeatableGroup, Structure} from '../types/Structure';
import {Uuid} from '../types/Uuid';

export default function ungroup({
	root,
	uuid,
}: {
	root: Structure | RepeatableGroup;
	uuid: Uuid;
}): Structure['children'] | RepeatableGroup['children'] {
	const children = new Map();

	// Iterate over children

	for (const child of root.children.values()) {

		// If it's the group we are ungrouping, insert its children

		if (child.uuid === uuid && child.type === 'repeatable-group') {
			for (const grandChild of child.children.values()) {
				const nextGrandChild = {
					...grandChild,
					parent: child.parent,
				};

				children.set(nextGrandChild.uuid, nextGrandChild);
			}
		}

		// Insert the child. If it's a repeatable group, build it with recursive call

		else if (child.type === 'repeatable-group') {
			const group: RepeatableGroup = {
				...child,
				children: ungroup({
					root: child,
					uuid,
				}),
			};

			children.set(group.uuid, group);
		}
		else {
			children.set(child.uuid, child);
		}
	}

	return children;
}

/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Group, Structure, StructureChild} from '../../types/Structure';

export default function updateChild({
	child: updatedChild,
	root,
}: {
	child: StructureChild;
	root: Structure | Group;
}): Structure['children'] | Group['children'] {
	const children = new Map();

	// Iterate over children

	for (const child of root.children.values()) {

		// Insert updated child if it applies

		if (child.uuid === updatedChild.uuid) {
			children.set(updatedChild.uuid, updatedChild);
		}

		// If it's a repeatable group, build it with recursive call

		else if (child.type === 'group') {
			const group: Group = {
				...child,
				children: updateChild({child: updatedChild, root: child}),
			};

			children.set(group.uuid, group);
		}

		// Insert the child as it is

		else {
			children.set(child.uuid, child);
		}
	}

	return children;
}

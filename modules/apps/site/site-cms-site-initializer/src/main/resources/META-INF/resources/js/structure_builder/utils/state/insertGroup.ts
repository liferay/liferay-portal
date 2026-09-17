/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Group, Structure, StructureChild} from '../../types/Structure';
import sortChildren from './sortChildren';

export default function insertGroup({
	group,
	groupChildren,
	root,
}: {
	group: Group;
	groupChildren: StructureChild[];
	root: Structure | Group;
}): Structure['children'] | Group['children'] {
	const children = new Map();

	for (const child of root.children.values()) {
		if (groupChildren.some(({uuid}) => uuid === child.uuid)) {
			continue;
		}

		if (child.type === 'group') {
			const container: Group = {
				...child,
				children: insertGroup({group, groupChildren, root: child}),
			};

			children.set(container.uuid, container);
		}
		else {
			children.set(child.uuid, child);
		}
	}

	if (root.uuid === group.parent) {
		children.set(group.uuid, group);
	}

	return sortChildren(children);
}

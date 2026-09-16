/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	RepeatableGroup,
	Structure,
	StructureChild,
} from '../../types/Structure';
import sortChildren from './sortChildren';

export default function addChild({
	child,
	root,
}: {
	child: StructureChild;
	root: Structure | RepeatableGroup;
}): Structure['children'] | RepeatableGroup['children'] {
	const children = new Map();

	// Iterate over children

	for (const rootChild of root.children.values()) {

		// Insert the child. If it's a repeatable group, build it with recursive call

		if (rootChild.type === 'group') {
			const group: RepeatableGroup = {
				...rootChild,
				children: addChild({
					child,
					root: rootChild,
				}),
			};

			children.set(group.uuid, group);
		}
		else {
			children.set(rootChild.uuid, rootChild);
		}
	}

	// Insert child if this is the correct parent

	if (root.uuid === child.parent) {
		children.set(child.uuid, child);
	}

	return sortChildren(children);
}

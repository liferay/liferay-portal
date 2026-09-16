/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	RepeatableGroup,
	Structure,
	StructureChild,
} from '../../types/Structure';
import {Uuid} from '../../types/Uuid';
import isLocked from '../isLocked';

export default function deleteChildren({
	root,
	uuids,
}: {
	root: Structure | RepeatableGroup;
	uuids: Uuid[];
}): {
	deletedChildrenUuids: Set<Uuid>;
	updatedChildren: Structure['children'] | RepeatableGroup['children'];
} {
	const deletedChildrenUuids = new Set<Uuid>();
	const children = new Map(root.children);

	// Iterate over existing children

	for (const child of root.children.values()) {

		// Delete child if it applies

		if (uuids.includes(child.uuid) && !isLocked({root, uuid: child.uuid})) {
			children.delete(child.uuid);

			getDeletedChildrenUuids({child}).forEach((uuid) => {
				deletedChildrenUuids.add(uuid);
			});
		}

		// If it's a repeatable group, do recursive call with its children

		else if (child.type === 'group') {
			const {
				deletedChildrenUuids: groupDeletedChildrenUuids,
				updatedChildren: groupChildren,
			} = deleteChildren({
				root: child,
				uuids,
			});

			groupDeletedChildrenUuids.forEach((uuid) => {
				deletedChildrenUuids.add(uuid);
			});

			// Delete group if it has no children now

			if (!groupChildren.size) {
				deletedChildrenUuids.add(child.uuid);
				children.delete(child.uuid);
			}

			// Otherwise update the group with updated children

			else {
				const group: RepeatableGroup = {
					...child,
					children: groupChildren,
				};

				children.set(group.uuid, group);
			}
		}
	}

	return {
		deletedChildrenUuids,
		updatedChildren: children,
	};
}

function getDeletedChildrenUuids({child}: {child: StructureChild}): Set<Uuid> {
	const deletedChildrenUuids = new Set<Uuid>();

	deletedChildrenUuids.add(child.uuid);

	if (child.type === 'group') {
		for (const groupChild of child.children.values()) {
			const groupDeletedChildrenUuids = getDeletedChildrenUuids({
				child: groupChild,
			});

			groupDeletedChildrenUuids.forEach((uuid) => {
				deletedChildrenUuids.add(uuid);
			});
		}
	}

	return deletedChildrenUuids;
}

/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {RepeatableGroup, Structure} from '../types/Structure';
import {Uuid} from '../types/Uuid';
import isLocked from './isLocked';

export default function deleteChildren({
	root,
	uuids,
}: {
	root: Structure | RepeatableGroup;
	uuids: Uuid[];
}): Structure['children'] | RepeatableGroup['children'] {
	const children = new Map(root.children);

	// Iterate over existing children

	for (const child of root.children.values()) {

		// Delete child if it applies

		if (uuids.includes(child.uuid) && !isLocked(child)) {
			children.delete(child.uuid);
		}

		// If it's a repeatable group, do recursive call with its children

		else if (child.type === 'repeatable-group') {
			const groupChildren = deleteChildren({
				root: child,
				uuids,
			});

			// Delete group if it has no children now

			if (!groupChildren.size) {
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

	return children;
}

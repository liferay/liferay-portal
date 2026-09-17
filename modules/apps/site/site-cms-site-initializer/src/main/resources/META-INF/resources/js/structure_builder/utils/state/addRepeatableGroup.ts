/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import buildLocalizedValue from '../../../common/utils/buildLocalizedValue';
import {
	Group,
	RepeatableGroup,
	Structure,
	StructureChild,
} from '../../types/Structure';
import {Uuid} from '../../types/Uuid';
import getRandomId from '../getRandomId';
import getRandomName from '../getRandomName';
import sortChildren from './sortChildren';

export default function addRepeatableGroup({
	groupChildren,
	groupParent,
	groupUuid,
	root,
}: {
	groupChildren: StructureChild[];
	groupParent: Uuid;
	groupUuid: Uuid;
	root: Structure | Group;
}): Structure['children'] | RepeatableGroup['children'] {
	const children = new Map();

	// Iterate over children

	for (const child of root.children.values()) {

		// Don't insert the child if it belongs to the new group

		if (groupChildren.some(({uuid}) => uuid === child.uuid)) {
			continue;
		}

		// Insert the child. If it's a repeatable group, build it with recursive call

		if (child.type === 'group') {
			const group: Group = {
				...child,
				children: addRepeatableGroup({
					groupChildren,
					groupParent,
					groupUuid,
					root: child,
				}),
			};

			children.set(group.uuid, group);
		}
		else {
			children.set(child.uuid, child);
		}
	}

	// Add new group if this is the correct parent

	if (root.uuid === groupParent) {
		const group: RepeatableGroup = {
			children: new Map(
				groupChildren.map((child) => [
					child.uuid,
					{...child, parent: groupUuid},
				])
			),
			erc: getRandomId(),
			isRepeatable: true,
			label: buildLocalizedValue('repeatable-group'),
			name: getRandomName({capitalize: true}),
			parent: groupParent,
			relationshipERC: getRandomId(),
			relationshipName: getRandomName(),
			type: 'group',
			uuid: groupUuid,
		};

		children.set(group.uuid, group);
	}

	return sortChildren(children);
}

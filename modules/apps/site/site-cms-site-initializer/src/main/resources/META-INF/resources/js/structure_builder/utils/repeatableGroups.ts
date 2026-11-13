/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	Group,
	RepeatableGroup,
	Structure,
	StructureChild,
} from '../types/Structure';
import {Uuid} from '../types/Uuid';
import findChild from './findChild';
import getRandomId from './getRandomId';
import getRandomName from './getRandomName';

export const REPEATABLE_GROUP_BOX_NAME_PREFIX = 'repeatable-group-';

/**
 * The structure and each repeatable group are saved as object definitions with
 * their own object layout. A plain group is saved as a tab of that layout when
 * it is at the first level of its object definition, and as a box of its tab
 * otherwise. A repeatable group is saved as a box of its tab, and boxes cannot
 * be nested, so a repeatable group can only be placed at the first level of an
 * object definition or in a plain group at that level.
 */
export function canContainRepeatableGroup({
	structure,
	uuid,
}: {
	structure: Structure;
	uuid: Uuid;
}): boolean {
	if (uuid === structure.uuid) {
		return true;
	}

	const child = findChild({root: structure, uuid});

	if (!child || child.type !== 'group') {
		return true;
	}

	if (child.isRepeatable) {
		return !Liferay.FeatureFlags['LPD-96666'];
	}

	return isObjectDefinitionRoot({structure, uuid: child.parent});
}

/**
 * Returns whether the items can be placed in the target without breaking the
 * rules above, for the items that are repeatable groups and for the repeatable
 * groups inside the items that are plain groups.
 */
export function canPlaceRepeatableGroups({
	items,
	structure,
	targetUuid,
}: {
	items: StructureChild[];
	structure: Structure;
	targetUuid: Uuid;
}): boolean {
	return items.every((item) => {
		if (isRepeatableGroup(item)) {
			return canContainRepeatableGroup({structure, uuid: targetUuid});
		}

		if (item.type === 'group' && hasRepeatableGroups(item.children)) {
			return (
				isObjectDefinitionRoot({structure, uuid: targetUuid}) &&
				canContainRepeatableGroup({structure, uuid: targetUuid})
			);
		}

		return true;
	});
}

/**
 * A structure created from a base structure starts with a copy of its
 * children. Its repeatable groups get object definitions and relationships of
 * their own, because the object definitions of the base's repeatable groups
 * already belong to relationships with the same names. The relationships keep
 * their names, so a repeatable group is reached by the same name in every
 * structure created from the base.
 */
export function copyRepeatableGroups(
	children: Group['children'] | Structure['children']
) {
	for (const child of children.values()) {
		if (child.type !== 'group') {
			continue;
		}

		if (child.isRepeatable) {
			child.erc = getRandomId();
			child.name = getRandomName({capitalize: true});
			child.relationshipERC = getRandomId();
		}

		copyRepeatableGroups(child.children);
	}
}

function isObjectDefinitionRoot({
	structure,
	uuid,
}: {
	structure: Structure;
	uuid: Uuid;
}): boolean {
	if (uuid === structure.uuid) {
		return true;
	}

	const child = findChild({root: structure, uuid});

	return !!child && isRepeatableGroup(child);
}

export function getRepeatableGroups(
	children: Group['children'] | Structure['children']
): RepeatableGroup[] {
	const repeatableGroups: RepeatableGroup[] = [];

	for (const child of children.values()) {
		if (isRepeatableGroup(child)) {
			repeatableGroups.push(child);
		}
		else if (child.type === 'group') {
			repeatableGroups.push(...getRepeatableGroups(child.children));
		}
	}

	return repeatableGroups;
}

export function hasRepeatableGroups(
	children: Group['children'] | Structure['children']
): boolean {
	for (const child of children.values()) {
		if (
			isRepeatableGroup(child) ||
			(child.type === 'group' && hasRepeatableGroups(child.children))
		) {
			return true;
		}
	}

	return false;
}

export function isRepeatableGroup(
	child: StructureChild
): child is RepeatableGroup {
	return child.type === 'group' && child.isRepeatable;
}

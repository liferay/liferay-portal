/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Structure, StructureChild} from '../types/Structure';
import {Uuid} from '../types/Uuid';
import findChild from './findChild';

export const MAX_NESTING = 4;

export default function exceedsMaxNesting({
	items,
	newGroup = false,
	structure,
	targetUuid,
}: {
	items: StructureChild[];
	newGroup?: boolean;
	structure: Structure;
	targetUuid: Uuid;
}): boolean {
	const levelsUp = getLevelsUp({structure, uuid: targetUuid});
	const levelsDown = getLevelsDown(items);

	const levels = levelsUp + levelsDown + (newGroup ? 1 : 0);

	return levels > MAX_NESTING;
}

function getLevelsUp({
	structure,
	uuid,
}: {
	structure: Structure;
	uuid: Uuid;
}): number {
	let currentUuid = uuid;
	let levels = 0;

	while (currentUuid && currentUuid !== structure.uuid) {
		const child = findChild({root: structure, uuid: currentUuid});

		if (!child) {
			break;
		}

		if (child.type === 'group') {
			levels++;
		}

		currentUuid = child.parent;
	}

	return levels;
}

function getLevelsDown(items: StructureChild[]): number {
	let levels = 0;

	for (const item of items) {
		if (item.type !== 'group') {
			continue;
		}

		const itemLevels =
			1 + getLevelsDown(Array.from(item.children.values()));

		levels = Math.max(levels, itemLevels);
	}

	return levels;
}

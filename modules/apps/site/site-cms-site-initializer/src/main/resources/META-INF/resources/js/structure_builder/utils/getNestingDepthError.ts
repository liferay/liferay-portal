/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectDefinitions} from '../../common/types/ObjectDefinition';
import {Structure} from '../types/Structure';
import isRepeatableGroup from './isRepeatableGroup';

export const MAX_DEPTH = 5;

export default function getNestingDepthError({
	objectDefinitions,
	structure,
}: {
	objectDefinitions: ObjectDefinitions;
	structure: Structure;
}): 'exceeded' | 'exceeded-in-references' | null {
	const depth = 1 + getHeight(structure.children);

	if (depth > MAX_DEPTH) {
		return 'exceeded';
	}

	const levelsUp = getLevelsUp({
		erc: structure.erc,
		objectDefinitions,
		visited: new Set(),
	});

	if (depth + levelsUp > MAX_DEPTH) {
		return 'exceeded-in-references';
	}

	return null;
}

function getHeight(children: Structure['children']): number {
	let height = 0;

	for (const child of children.values()) {
		if (child.type === 'referenced-structure' || isRepeatableGroup(child)) {
			height = Math.max(height, 1 + getHeight(child.children));
		}
		else if (child.type === 'group') {
			height = Math.max(height, getHeight(child.children));
		}
	}

	return height;
}

function getLevelsUp({
	erc,
	objectDefinitions,
	visited,
}: {
	erc: string;
	objectDefinitions: ObjectDefinitions;
	visited: Set<string>;
}): number {
	if (visited.has(erc)) {
		return 0;
	}

	visited.add(erc);

	let levels = 0;

	for (const objectDefinition of Object.values(objectDefinitions)) {
		const parent = objectDefinition.objectRelationships?.some(
			(objectRelationship) =>
				objectRelationship.edge &&
				!objectRelationship.reverse &&
				objectRelationship.objectDefinitionExternalReferenceCode2 ===
					erc
		);

		if (!parent) {
			continue;
		}

		levels = Math.max(
			levels,
			1 +
				getLevelsUp({
					erc: objectDefinition.externalReferenceCode,
					objectDefinitions,
					visited: new Set(visited),
				})
		);
	}

	return levels;
}

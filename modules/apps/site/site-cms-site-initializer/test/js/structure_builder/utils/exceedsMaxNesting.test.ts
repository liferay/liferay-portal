/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	NonRepeatableGroup,
	Structure,
	StructureChild,
} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Structure';
import {Uuid} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Uuid';
import exceedsMaxNesting from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/exceedsMaxNesting';
import {getDefaultField} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/field';
import getUuid from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';

const ROOT_UUID = getUuid();

function buildGroup({
	children = [],
	parent,
	uuid,
}: {
	children?: StructureChild[];
	parent: Uuid;
	uuid: Uuid;
}): NonRepeatableGroup {
	return {
		children: new Map(children.map((child) => [child.uuid, child])),
		isRepeatable: false,
		label: {},
		parent,
		type: 'group',
		uuid,
	};
}

function buildStructure(children: StructureChild[]): Structure {
	return {
		children: new Map(children.map((child) => [child.uuid, child])),
		erc: 'root-erc',
		label: {},
		name: 'Root',
		path: '',
		slug: '',
		spaces: 'all',
		status: 'draft',
		system: false,
		type: 'L_CMS_CONTENT_STRUCTURES',
		uuid: ROOT_UUID,
		workflows: {},
	};
}

const LEVEL_UUIDS = [getUuid(), getUuid(), getUuid(), getUuid()];

function buildNestedGroups(
	depth: number,
	parent: Uuid = ROOT_UUID
): NonRepeatableGroup {
	const uuid = LEVEL_UUIDS[LEVEL_UUIDS.length - depth];

	return buildGroup({
		children: depth > 1 ? [buildNestedGroups(depth - 1, uuid)] : [],
		parent,
		uuid,
	});
}

describe('exceedsMaxNesting', () => {
	it('Allows a new group at the root level', () => {
		const structure = buildStructure([]);

		expect(
			exceedsMaxNesting({
				items: [getDefaultField({parent: ROOT_UUID, type: 'text'})],
				newGroup: true,
				structure,
				targetUuid: ROOT_UUID,
			})
		).toBe(false);
	});

	it('Allows a new group inside three nested groups', () => {
		const structure = buildStructure([buildNestedGroups(3)]);

		expect(
			exceedsMaxNesting({
				items: [],
				newGroup: true,
				structure,
				targetUuid: LEVEL_UUIDS[LEVEL_UUIDS.length - 1],
			})
		).toBe(false);
	});

	it('Rejects a new group inside four nested groups', () => {
		const structure = buildStructure([buildNestedGroups(4)]);

		expect(
			exceedsMaxNesting({
				items: [],
				newGroup: true,
				structure,
				targetUuid: LEVEL_UUIDS[LEVEL_UUIDS.length - 1],
			})
		).toBe(true);
	});

	it('Counts the groups of the moved items', () => {
		const structure = buildStructure([buildNestedGroups(3)]);

		const movedUuid = getUuid();

		const moved = buildGroup({
			children: [
				buildGroup({
					children: [],
					parent: movedUuid,
					uuid: getUuid(),
				}),
			],
			parent: ROOT_UUID,
			uuid: movedUuid,
		});

		expect(
			exceedsMaxNesting({
				items: [moved],
				structure,
				targetUuid: LEVEL_UUIDS[LEVEL_UUIDS.length - 2],
			})
		).toBe(false);

		expect(
			exceedsMaxNesting({
				items: [moved],
				structure,
				targetUuid: LEVEL_UUIDS[LEVEL_UUIDS.length - 1],
			})
		).toBe(true);
	});

	it('Ignores the depth of items that are not groups', () => {
		const structure = buildStructure([buildNestedGroups(4)]);

		expect(
			exceedsMaxNesting({
				items: [
					getDefaultField({
						parent: LEVEL_UUIDS[LEVEL_UUIDS.length - 1],
						type: 'text',
					}),
				],
				structure,
				targetUuid: LEVEL_UUIDS[LEVEL_UUIDS.length - 1],
			})
		).toBe(false);
	});
});

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
	afterEach(() => {
		delete Liferay.FeatureFlags['LPD-96666'];
	});

	it('Allows a new group at the root level', () => {
		expect(
			exceedsMaxNesting({
				items: [
					getDefaultField({
						defaultLanguageLabels: {labels: {}, locale: 'en_US'},
						parent: ROOT_UUID,
						type: 'text',
					}),
				],
				newGroup: true,
				structure: buildStructure([]),
				targetUuid: ROOT_UUID,
			})
		).toBe(false);
	});

	it('Allows four levels without the feature flag', () => {
		expect(
			exceedsMaxNesting({
				items: [],
				newGroup: true,
				structure: buildStructure([buildNestedGroups(3)]),
				targetUuid: LEVEL_UUIDS[LEVEL_UUIDS.length - 1],
			})
		).toBe(false);
	});

	it('Rejects five levels without the feature flag', () => {
		expect(
			exceedsMaxNesting({
				items: [],
				newGroup: true,
				structure: buildStructure([buildNestedGroups(4)]),
				targetUuid: LEVEL_UUIDS[LEVEL_UUIDS.length - 1],
			})
		).toBe(true);
	});

	describe('with the feature flag', () => {
		beforeEach(() => {
			Liferay.FeatureFlags['LPD-96666'] = true;
		});

		it('Allows a new group inside one group', () => {
			expect(
				exceedsMaxNesting({
					items: [],
					newGroup: true,
					structure: buildStructure([buildNestedGroups(1)]),
					targetUuid: LEVEL_UUIDS[LEVEL_UUIDS.length - 1],
				})
			).toBe(false);
		});

		it('Rejects a new group inside two nested groups', () => {
			expect(
				exceedsMaxNesting({
					items: [],
					newGroup: true,
					structure: buildStructure([buildNestedGroups(2)]),
					targetUuid: LEVEL_UUIDS[LEVEL_UUIDS.length - 1],
				})
			).toBe(true);
		});

		it('Counts the groups of the moved items', () => {
			const structure = buildStructure([buildNestedGroups(1)]);

			const targetUuid = LEVEL_UUIDS[LEVEL_UUIDS.length - 1];

			const movedUuid = getUuid();

			expect(
				exceedsMaxNesting({
					items: [buildGroup({parent: ROOT_UUID, uuid: movedUuid})],
					structure,
					targetUuid,
				})
			).toBe(false);

			expect(
				exceedsMaxNesting({
					items: [
						buildGroup({
							children: [
								buildGroup({
									parent: movedUuid,
									uuid: getUuid(),
								}),
							],
							parent: ROOT_UUID,
							uuid: movedUuid,
						}),
					],
					structure,
					targetUuid,
				})
			).toBe(true);
		});

		it('Ignores the depth of items that are not groups', () => {
			const targetUuid = LEVEL_UUIDS[LEVEL_UUIDS.length - 1];

			expect(
				exceedsMaxNesting({
					items: [
						getDefaultField({
							defaultLanguageLabels: {
								labels: {},
								locale: 'en_US',
							},
							parent: targetUuid,
							type: 'text',
						}),
					],
					structure: buildStructure([buildNestedGroups(2)]),
					targetUuid,
				})
			).toBe(false);
		});
	});
});

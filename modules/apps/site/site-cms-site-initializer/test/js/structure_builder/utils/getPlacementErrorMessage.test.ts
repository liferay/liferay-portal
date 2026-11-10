/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	NonRepeatableGroup,
	ReferencedStructure,
	RepeatableGroup,
	Structure,
	StructureChild,
} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Structure';
import {Uuid} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Uuid';
import getPlacementErrorMessage from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getPlacementErrorMessage';
import getUuid from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';
import {canContainRepeatableGroup} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/repeatableGroups';

const REFERENCED_STRUCTURES_MESSAGE =
	'referenced-structures-and-related-content-can-only-be-placed-at-the-first-level';

const REPEATABLE_GROUPS_MESSAGE =
	'repeatable-groups-can-only-be-placed-at-the-first-level-or-inside-a-tab';

const ROOT_UUID = getUuid();

function buildGroup(
	parent: Uuid,
	children: StructureChild[] = []
): NonRepeatableGroup {
	const uuid = getUuid();

	return {
		children: new Map(
			children.map((child) => [child.uuid, {...child, parent: uuid}])
		),
		isRepeatable: false,
		label: {},
		parent,
		type: 'group',
		uuid,
	};
}

function buildRepeatableGroup(
	parent: Uuid,
	children: StructureChild[] = []
): RepeatableGroup {
	return {
		...buildGroup(parent, children),
		erc: 'repeatable-group-erc',
		isRepeatable: true,
		name: 'repeatableGroup',
		relationshipERC: 'relationship-erc',
		relationshipName: 'relationship',
	};
}

function buildReferencedStructure(parent: Uuid): ReferencedStructure {
	return {
		children: new Map(),
		editURL: '',
		erc: 'referenced-erc',
		label: {},
		name: 'referenced',
		parent,
		relationshipERC: 'referenced-relationship-erc',
		relationshipName: 'referencedRelationship',
		spaces: [],
		type: 'referenced-structure',
		uuid: getUuid(),
		workflows: {},
	};
}

function buildStructure(children: StructureChild[]): Structure {
	return {
		children: new Map(children.map((child) => [child.uuid, child])),
		erc: 'root-erc',
		label: {},
		name: 'root',
		path: '',
		settings: {},
		slug: '',
		spaces: [],
		status: 'draft',
		system: false,
		titleFieldName: 'title',
		type: 'L_CMS_CONTENT_STRUCTURES',
		uuid: ROOT_UUID,
		workflows: {},
	};
}

describe('canContainRepeatableGroup', () => {
	it('Allows the structure and a group at the first level', () => {
		const tab = buildGroup(ROOT_UUID);

		const structure = buildStructure([tab]);

		expect(canContainRepeatableGroup({structure, uuid: ROOT_UUID})).toBe(
			true
		);
		expect(canContainRepeatableGroup({structure, uuid: tab.uuid})).toBe(
			true
		);
	});

	it('Allows a group at the first level of a repeatable group', () => {
		const repeatableGroup = buildRepeatableGroup(ROOT_UUID, [
			buildGroup(ROOT_UUID),
		]);

		const [group] = Array.from(repeatableGroup.children.values());

		const structure = buildStructure([repeatableGroup]);

		expect(canContainRepeatableGroup({structure, uuid: group.uuid})).toBe(
			true
		);
	});

	it('Allows a repeatable group only without the feature flag', () => {
		const repeatableGroup = buildRepeatableGroup(ROOT_UUID);

		const structure = buildStructure([repeatableGroup]);

		expect(
			canContainRepeatableGroup({structure, uuid: repeatableGroup.uuid})
		).toBe(true);

		Liferay.FeatureFlags['LPD-96666'] = true;

		try {
			expect(
				canContainRepeatableGroup({
					structure,
					uuid: repeatableGroup.uuid,
				})
			).toBe(false);
		}
		finally {
			delete Liferay.FeatureFlags['LPD-96666'];
		}
	});

	it('Refuses a group inside a group', () => {
		const tab = buildGroup(ROOT_UUID, [buildGroup(ROOT_UUID)]);

		const [nestedGroup] = Array.from(tab.children.values());

		const structure = buildStructure([tab]);

		expect(
			canContainRepeatableGroup({structure, uuid: nestedGroup.uuid})
		).toBe(false);
	});
});

describe('getPlacementErrorMessage', () => {
	afterEach(() => {
		delete Liferay.FeatureFlags['LPD-96666'];
	});

	it('Refuses a repeatable group inside a group of a group', () => {
		const repeatableGroup = buildRepeatableGroup(ROOT_UUID);
		const tab = buildGroup(ROOT_UUID, [buildGroup(ROOT_UUID)]);

		const [nestedGroup] = Array.from(tab.children.values());

		expect(
			getPlacementErrorMessage({
				items: [repeatableGroup],
				structure: buildStructure([repeatableGroup, tab]),
				targetUuid: nestedGroup.uuid,
			})
		).toBe(REPEATABLE_GROUPS_MESSAGE);
	});

	it('Refuses a group holding a repeatable group inside another group', () => {
		const group = buildGroup(ROOT_UUID, [buildRepeatableGroup(ROOT_UUID)]);
		const tab = buildGroup(ROOT_UUID);

		expect(
			getPlacementErrorMessage({
				items: [group],
				structure: buildStructure([group, tab]),
				targetUuid: tab.uuid,
			})
		).toBe(REPEATABLE_GROUPS_MESSAGE);
	});

	describe('with the feature flag', () => {
		beforeEach(() => {
			Liferay.FeatureFlags['LPD-96666'] = true;
		});

		it('Allows a repeatable group inside a group at the first level', () => {
			const repeatableGroup = buildRepeatableGroup(ROOT_UUID);
			const tab = buildGroup(ROOT_UUID);

			expect(
				getPlacementErrorMessage({
					items: [repeatableGroup],
					structure: buildStructure([repeatableGroup, tab]),
					targetUuid: tab.uuid,
				})
			).toBeNull();
		});

		it('Refuses a repeatable group inside a repeatable group', () => {
			const repeatableGroup = buildRepeatableGroup(ROOT_UUID);
			const target = buildRepeatableGroup(ROOT_UUID);

			expect(
				getPlacementErrorMessage({
					items: [repeatableGroup],
					structure: buildStructure([repeatableGroup, target]),
					targetUuid: target.uuid,
				})
			).toBe(REPEATABLE_GROUPS_MESSAGE);
		});

		it('Refuses a referenced structure below the first level', () => {
			const referencedStructure = buildReferencedStructure(ROOT_UUID);
			const tab = buildGroup(ROOT_UUID);

			expect(
				getPlacementErrorMessage({
					items: [referencedStructure],
					structure: buildStructure([referencedStructure, tab]),
					targetUuid: tab.uuid,
				})
			).toBe(REFERENCED_STRUCTURES_MESSAGE);
		});
	});
});

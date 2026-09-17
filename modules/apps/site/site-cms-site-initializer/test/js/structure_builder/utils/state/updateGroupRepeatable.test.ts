/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {State} from '../../../../../src/main/resources/META-INF/resources/js/structure_builder/contexts/StateContext';
import {
	Group,
	NonRepeatableGroup,
	RepeatableGroup,
	Structure,
} from '../../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Structure';
import getUuid from '../../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';
import updateGroupRepeatable from '../../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/state/updateGroupRepeatable';

const ROOT_UUID = getUuid();
const OUTER_UUID = getUuid();
const GROUP_UUID = getUuid();

const EMPTY_HISTORY: State['history'] = {
	deletedChildren: [],
	deletedGroupERCs: [],
	deletedRelationships: [],
	modifiedNames: new Set(),
	modifiedSlugs: new Set(),
};

const NON_REPEATABLE: NonRepeatableGroup = {
	children: new Map(),
	isRepeatable: false,
	label: {},
	parent: OUTER_UUID,
	type: 'group',
	uuid: GROUP_UUID,
};

const REPEATABLE: RepeatableGroup = {
	children: new Map(),
	erc: 'group-erc',
	isRepeatable: true,
	label: {},
	name: 'Group',
	parent: OUTER_UUID,
	relationshipERC: 'relationship-erc',
	relationshipName: 'group',
	type: 'group',
	uuid: GROUP_UUID,
};

function buildStructure(group: Group): Structure {
	const outer: NonRepeatableGroup = {
		children: new Map([[GROUP_UUID, group]]),
		isRepeatable: false,
		label: {},
		parent: ROOT_UUID,
		type: 'group',
		uuid: OUTER_UUID,
	};

	return {
		children: new Map([[OUTER_UUID, outer]]),
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

function findGroup(children: Structure['children']) {
	const outer = children.get(OUTER_UUID) as NonRepeatableGroup;

	return outer.children.get(GROUP_UUID) as Group;
}

describe('updateGroupRepeatable', () => {
	it('Generates the server data when a group becomes repeatable', () => {
		const {children} = updateGroupRepeatable({
			group: NON_REPEATABLE,
			history: EMPTY_HISTORY,
			isRepeatable: true,
			savedChildren: new Set(),
			structure: buildStructure(NON_REPEATABLE),
		});

		const group = findGroup(children);

		expect(group.isRepeatable).toBe(true);
		expect(group.erc).toBeDefined();
		expect(group.name).toBeDefined();
		expect(group.relationshipERC).toBeDefined();
		expect(group.relationshipName).toBeDefined();
	});

	it('Keeps the server data when a group stops being repeatable', () => {
		const {children} = updateGroupRepeatable({
			group: REPEATABLE,
			history: EMPTY_HISTORY,
			isRepeatable: false,
			savedChildren: new Set(),
			structure: buildStructure(REPEATABLE),
		});

		const group = findGroup(children);

		expect(group.isRepeatable).toBe(false);
		expect(group.erc).toBe('group-erc');
		expect(group.relationshipERC).toBe('relationship-erc');
	});

	it('Records the deletion when a saved repeatable group is turned off', () => {
		const {history} = updateGroupRepeatable({
			group: REPEATABLE,
			history: EMPTY_HISTORY,
			isRepeatable: false,
			savedChildren: new Set([GROUP_UUID]),
			structure: buildStructure(REPEATABLE),
		});

		expect(history.deletedGroupERCs).toEqual(['group-erc']);
		expect(history.deletedRelationships).toEqual([
			{
				relationshipERC: 'relationship-erc',
				structureERC: 'root-erc',
			},
		]);
	});

	it('Records nothing when the group was never saved', () => {
		const {history} = updateGroupRepeatable({
			group: REPEATABLE,
			history: EMPTY_HISTORY,
			isRepeatable: false,
			savedChildren: new Set(),
			structure: buildStructure(REPEATABLE),
		});

		expect(history).toEqual(EMPTY_HISTORY);
	});

	it('Withdraws the deletion when the group is turned back on', () => {
		const group: NonRepeatableGroup = {
			...REPEATABLE,
			isRepeatable: false,
		};

		const {history} = updateGroupRepeatable({
			group,
			history: {
				...EMPTY_HISTORY,
				deletedGroupERCs: ['group-erc'],
				deletedRelationships: [
					{
						relationshipERC: 'relationship-erc',
						structureERC: 'root-erc',
					},
				],
			},
			isRepeatable: true,
			savedChildren: new Set([GROUP_UUID]),
			structure: buildStructure(group),
		});

		expect(history.deletedGroupERCs).toEqual([]);
		expect(history.deletedRelationships).toEqual([]);
	});
});

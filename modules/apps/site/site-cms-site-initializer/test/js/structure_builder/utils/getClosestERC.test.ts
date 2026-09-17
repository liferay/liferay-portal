/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	NonRepeatableGroup,
	RepeatableGroup,
	Structure,
} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Structure';
import getClosestERC from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getClosestERC';
import getUuid from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';

const ROOT_UUID = getUuid();
const OUTER_UUID = getUuid();
const REPEATABLE_UUID = getUuid();
const INNER_UUID = getUuid();

const INNER: NonRepeatableGroup = {
	children: new Map(),
	isRepeatable: false,
	label: {},
	parent: REPEATABLE_UUID,
	type: 'group',
	uuid: INNER_UUID,
};

const REPEATABLE: RepeatableGroup = {
	children: new Map([[INNER_UUID, INNER]]),
	erc: 'repeatable-erc',
	isRepeatable: true,
	label: {},
	name: 'Repeatable',
	parent: OUTER_UUID,
	relationshipERC: 'relationship-erc',
	relationshipName: 'repeatable',
	type: 'group',
	uuid: REPEATABLE_UUID,
};

const OUTER: NonRepeatableGroup = {
	children: new Map([[REPEATABLE_UUID, REPEATABLE]]),
	isRepeatable: false,
	label: {},
	parent: ROOT_UUID,
	type: 'group',
	uuid: OUTER_UUID,
};

const STRUCTURE: Structure = {
	children: new Map([[OUTER_UUID, OUTER]]),
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

describe('getClosestERC', () => {
	it('Returns the structure ERC for the structure itself', () => {
		expect(getClosestERC({structure: STRUCTURE, uuid: ROOT_UUID})).toBe(
			'root-erc'
		);
	});

	it('Skips the groups that are not repeatable', () => {
		expect(getClosestERC({structure: STRUCTURE, uuid: OUTER_UUID})).toBe(
			'root-erc'
		);
	});

	it('Returns the ERC of the repeatable group itself', () => {
		expect(
			getClosestERC({structure: STRUCTURE, uuid: REPEATABLE_UUID})
		).toBe('repeatable-erc');
	});

	it('Returns the ERC of the closest repeatable ancestor', () => {
		expect(getClosestERC({structure: STRUCTURE, uuid: INNER_UUID})).toBe(
			'repeatable-erc'
		);
	});
});

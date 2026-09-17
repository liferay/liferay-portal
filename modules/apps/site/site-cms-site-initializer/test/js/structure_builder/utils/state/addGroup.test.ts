/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	NonRepeatableGroup,
	Structure,
} from '../../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Structure';
import {getDefaultField} from '../../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/field';
import getUuid from '../../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';
import addGroup from '../../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/state/addGroup';

const ROOT_UUID = getUuid();
const GROUP_UUID = getUuid();

function buildStructure(children: Structure['children']): Structure {
	return {
		children,
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

describe('addGroup', () => {
	it('Inserts a group that is not repeatable', () => {
		const children = addGroup({
			groupChildren: [],
			groupParent: ROOT_UUID,
			groupUuid: GROUP_UUID,
			root: buildStructure(new Map()),
		});

		const group = children.get(GROUP_UUID) as NonRepeatableGroup;

		expect(group.type).toBe('group');
		expect(group.isRepeatable).toBe(false);
	});

	it('Does not give the group an ERC nor a relationship', () => {
		const children = addGroup({
			groupChildren: [],
			groupParent: ROOT_UUID,
			groupUuid: GROUP_UUID,
			root: buildStructure(new Map()),
		});

		const group = children.get(GROUP_UUID) as NonRepeatableGroup;

		expect(group.erc).toBeUndefined();
		expect(group.name).toBeUndefined();
		expect(group.relationshipERC).toBeUndefined();
		expect(group.relationshipName).toBeUndefined();
	});

	it('Moves the selected children into the new group', () => {
		const field = getDefaultField({parent: ROOT_UUID, type: 'text'});

		const children = addGroup({
			groupChildren: [field],
			groupParent: ROOT_UUID,
			groupUuid: GROUP_UUID,
			root: buildStructure(new Map([[field.uuid, field]])),
		});

		const group = children.get(GROUP_UUID) as NonRepeatableGroup;

		expect(children.has(field.uuid)).toBe(false);
		expect(group.children.get(field.uuid)?.parent).toBe(GROUP_UUID);
	});
});

/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	Group,
	NonRepeatableGroup,
	RelatedContent,
	RepeatableGroup,
	StructureChild,
} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Structure';
import {getDefaultField} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/field';
import getUuid from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';
import {validateGroup} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/validation';

const ROOT_UUID = getUuid();
const GROUP_UUID = getUuid();

function buildChildren(children: StructureChild[]): Group['children'] {
	return new Map(children.map((child) => [child.uuid, child]));
}

function buildGroup(children: StructureChild[]): NonRepeatableGroup {
	return {
		children: buildChildren(children),
		isRepeatable: false,
		label: {en_US: 'Group'},
		parent: ROOT_UUID,
		type: 'group',
		uuid: GROUP_UUID,
	};
}

function buildRepeatableGroup(children: StructureChild[]): RepeatableGroup {
	return {
		...buildGroup(children),
		erc: 'group-erc',
		isRepeatable: true,
		name: 'Group',
		relationshipERC: 'relationship-erc',
		relationshipName: 'group',
	};
}

function buildField() {
	return getDefaultField({
		defaultLanguageLabels: {labels: {}, locale: 'en_US'},
		parent: GROUP_UUID,
		type: 'text',
	});
}

function buildNestedGroup(children: StructureChild[]): NonRepeatableGroup {
	return {
		children: buildChildren(children),
		isRepeatable: false,
		label: {en_US: 'Nested'},
		parent: GROUP_UUID,
		type: 'group',
		uuid: getUuid(),
	};
}

function buildNestedRepeatableGroup(
	children: StructureChild[]
): RepeatableGroup {
	return {
		...buildNestedGroup(children),
		erc: 'nested-erc',
		isRepeatable: true,
		name: 'Nested',
		relationshipERC: 'nested-relationship-erc',
		relationshipName: 'nested',
	};
}

const RELATED_CONTENT: RelatedContent = {
	erc: 'related-content-erc',
	label: {},
	multiselection: false,
	name: 'relatedContent',
	parent: GROUP_UUID,
	relatedStructureERC: 'target-structure-erc',
	type: 'related-content',
	uuid: getUuid(),
};

describe('validateGroup', () => {
	it('Reports an empty label', () => {
		const errors = validateGroup({
			data: {...buildGroup([]), label: {en_US: ''}},
		});

		expect(errors.get('label')).toBe('empty');
	});

	it('Rejects an empty group', () => {
		const errors = validateGroup({data: buildGroup([])});

		expect(errors.get('global')).toBe('no-children');
	});

	it('Accepts a group that only holds related content', () => {
		const errors = validateGroup({data: buildGroup([RELATED_CONTENT])});

		expect(errors.size).toBe(0);
	});

	it('Accepts a group that only holds a repeatable group', () => {
		const errors = validateGroup({
			data: buildGroup([buildNestedRepeatableGroup([buildField()])]),
		});

		expect(errors.size).toBe(0);
	});

	it('Rejects an empty repeatable group', () => {
		const errors = validateGroup({data: buildRepeatableGroup([])});

		expect(errors.get('global')).toBe('no-fields');
	});

	it('Accepts a repeatable group with a field', () => {
		const errors = validateGroup({
			data: buildRepeatableGroup([buildField()]),
		});

		expect(errors.size).toBe(0);
	});

	it('Accepts a repeatable group whose field is in a nested group', () => {
		const errors = validateGroup({
			data: buildRepeatableGroup([buildNestedGroup([buildField()])]),
		});

		expect(errors.size).toBe(0);
	});

	it('Rejects a repeatable group that only holds another repeatable group', () => {
		const errors = validateGroup({
			data: buildRepeatableGroup([
				buildNestedRepeatableGroup([buildField()]),
			]),
		});

		expect(errors.get('global')).toBe('no-fields');
	});
});

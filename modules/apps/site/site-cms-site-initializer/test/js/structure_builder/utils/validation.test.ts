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
import {
	getErrorMessage,
	validateGroup,
	validateStructure,
} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/validation';

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

describe('getErrorMessage', () => {
	it('explains an unexpected error', () => {
		expect(getErrorMessage('global', 'unexpected', {})).toBe(
			'an-unexpected-error-occurred-while-saving-or-publishing-the-content-structure'
		);
	});

	it('explains a missing permission', () => {
		expect(getErrorMessage('global', 'permission', {})).toBe(
			'you-do-not-have-permission-to-access-the-requested-resource'
		);
	});

	it('explains an external reference code collision', () => {
		expect(getErrorMessage('erc', 'in-use', {})).toBe(
			'this-external-reference-code-is-already-in-use'
		);
	});
});

describe('validateStructure', () => {
	it.each([
		['123', 'number'],
		['-', 'invalid-character'],
		['fruit/-/apple', 'invalid-character'],
		['a'.repeat(254), 'max-length'],
	])('flags the friendly URL %s', (slug, error) => {
		expect(validateStructure({data: {slug}}).get('slug')).toBe(error);
	});

	it.each(['fruit', 'fruit-2026', 'a', ''])(
		'accepts the friendly URL "%s"',
		(slug) => {
			expect(validateStructure({data: {slug}}).has('slug')).toBe(false);
		}
	);

	it('clears a friendly URL collision reported by the server once edited', () => {
		expect(
			validateStructure({
				currentErrors: new Map([['slug', 'in-use']]),
				data: {slug: 'fruit'},
			}).has('slug')
		).toBe(false);
	});

	it('flags an external reference code used by another structure', () => {
		expect(
			validateStructure({
				data: {erc: 'fruit', id: 1},
				objectDefinitions: {fruit: {id: 2} as any},
			}).get('erc')
		).toBe('in-use');
	});

	it('accepts the external reference code of the structure itself', () => {
		expect(
			validateStructure({
				data: {erc: 'fruit', id: 1},
				objectDefinitions: {fruit: {id: 1} as any},
			}).has('erc')
		).toBe(false);
	});
});

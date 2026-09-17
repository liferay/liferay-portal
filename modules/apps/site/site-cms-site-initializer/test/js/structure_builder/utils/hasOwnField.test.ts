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
import {Uuid} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Uuid';
import {getDefaultField} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/field';
import getUuid from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';
import hasOwnField from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/hasOwnField';

const PARENT_UUID = getUuid();

function buildChildren(children: StructureChild[]): Group['children'] {
	return new Map(children.map((child) => [child.uuid, child]));
}

function buildGroup(children: StructureChild[]): NonRepeatableGroup {
	return {
		children: buildChildren(children),
		isRepeatable: false,
		label: {},
		parent: PARENT_UUID,
		type: 'group',
		uuid: getUuid(),
	};
}

function buildRepeatableGroup(children: StructureChild[]): RepeatableGroup {
	return {
		...buildGroup(children),
		erc: 'nested-erc',
		isRepeatable: true,
		name: 'Nested',
		relationshipERC: 'nested-relationship-erc',
		relationshipName: 'nested',
	};
}

function buildField(parent: Uuid = PARENT_UUID) {
	return getDefaultField({parent, type: 'text'});
}

const RELATED_CONTENT: RelatedContent = {
	erc: 'related-content-erc',
	label: {},
	multiselection: false,
	name: 'relatedContent',
	parent: PARENT_UUID,
	relatedStructureERC: 'target-structure-erc',
	type: 'related-content',
	uuid: getUuid(),
};

describe('hasOwnField', () => {
	it('Returns false without children', () => {
		expect(hasOwnField(new Map())).toBe(false);
	});

	it('Finds a direct field', () => {
		expect(hasOwnField(buildChildren([buildField()]))).toBe(true);
	});

	it('Finds a field nested in groups that are not repeatable', () => {
		const children = buildChildren([
			buildGroup([buildGroup([buildField()])]),
		]);

		expect(hasOwnField(children)).toBe(true);
	});

	it('Ignores the fields of a nested repeatable group', () => {
		const children = buildChildren([buildRepeatableGroup([buildField()])]);

		expect(hasOwnField(children)).toBe(false);
	});

	it('Ignores the fields behind a repeatable group at any depth', () => {
		const children = buildChildren([
			buildGroup([buildRepeatableGroup([buildField()])]),
		]);

		expect(hasOwnField(children)).toBe(false);
	});

	it('Ignores related content', () => {
		expect(hasOwnField(buildChildren([RELATED_CONTENT]))).toBe(false);
	});
});

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
import getOwnFields from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getOwnFields';
import getUuid from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';

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
	return getDefaultField({
		defaultLanguageLabels: {labels: {}, locale: 'en_US'},
		parent,
		type: 'text',
	});
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

describe('getOwnFields', () => {
	it('Returns false without children', () => {
		expect(getOwnFields(new Map())).toEqual([]);
	});

	it('Finds a direct field', () => {
		expect(getOwnFields(buildChildren([buildField()])).length).toBe(1);
	});

	it('Finds a field nested in groups that are not repeatable', () => {
		const children = buildChildren([
			buildGroup([buildGroup([buildField()])]),
		]);

		expect(getOwnFields(children).length).toBe(1);
	});

	it('Ignores the fields of a nested repeatable group', () => {
		const children = buildChildren([buildRepeatableGroup([buildField()])]);

		expect(getOwnFields(children)).toEqual([]);
	});

	it('Ignores the fields behind a repeatable group at any depth', () => {
		const children = buildChildren([
			buildGroup([buildRepeatableGroup([buildField()])]),
		]);

		expect(getOwnFields(children)).toEqual([]);
	});

	it('Ignores related content', () => {
		expect(getOwnFields(buildChildren([RELATED_CONTENT]))).toEqual([]);
	});
});

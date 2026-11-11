/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinition,
	ObjectDefinitions,
} from '../../../../src/main/resources/META-INF/resources/js/common/types/ObjectDefinition';
import {
	Group,
	ReferencedStructure,
	Structure,
	StructureChild,
} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Structure';
import getNestingDepthError from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getNestingDepthError';
import getUuid from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';

const ROOT_UUID = getUuid();

function toMap(children: StructureChild[]) {
	return new Map(children.map((child) => [child.uuid, child]));
}

function buildGroup({
	children = [],
	repeatable,
}: {
	children?: StructureChild[];
	repeatable: boolean;
}): Group {
	const group = {
		children: toMap(children),
		label: {},
		parent: ROOT_UUID,
		type: 'group' as const,
		uuid: getUuid(),
	};

	if (!repeatable) {
		return {...group, isRepeatable: false};
	}

	return {
		...group,
		erc: getUuid(),
		isRepeatable: true,
		name: 'group',
		relationshipERC: getUuid(),
		relationshipName: 'group',
	};
}

function buildReference(children: StructureChild[] = []): ReferencedStructure {
	return {
		children: toMap(children),
		editURL: '',
		erc: getUuid(),
		label: {},
		name: 'referenced',
		parent: ROOT_UUID,
		relationshipERC: getUuid(),
		relationshipName: 'referenced',
		spaces: 'all',
		type: 'referenced-structure',
		uuid: getUuid(),
		workflows: {},
	};
}

function buildReferences(levels: number): ReferencedStructure[] {
	let children: StructureChild[] = [];

	for (let i = 0; i < levels; i++) {
		children = [buildReference(children)];
	}

	return children as ReferencedStructure[];
}

function buildStructure(children: StructureChild[] = []): Structure {
	return {
		children: toMap(children),
		erc: 'structure-erc',
		label: {},
		name: 'Structure',
		path: '',
		slug: '',
		spaces: 'all',
		status: 'draft',
		system: false,
		titleFieldName: 'title',
		type: 'L_CMS_CONTENT_STRUCTURES',
		uuid: ROOT_UUID,
		workflows: {},
	};
}

function buildObjectDefinition({
	edge = true,
	erc,
	reverse = false,
	target,
}: {
	edge?: boolean;
	erc: string;
	reverse?: boolean;
	target: string;
}): ObjectDefinition {
	return {
		externalReferenceCode: erc,
		objectRelationships: [
			{
				deletionType: 'cascade',
				edge,
				externalReferenceCode: `${erc}-${target}`,
				label: {},
				name: `${erc}${target}`,
				objectDefinitionExternalReferenceCode1: erc,
				objectDefinitionExternalReferenceCode2: target,
				reverse,
				type: 'oneToMany',
			},
		],
	} as ObjectDefinition;
}

function buildParents(levels: number): ObjectDefinitions {
	const objectDefinitions: ObjectDefinitions = {};

	let target = 'structure-erc';

	for (let i = 0; i < levels; i++) {
		const erc = `parent-${i}`;

		objectDefinitions[erc] = buildObjectDefinition({erc, target});

		target = erc;
	}

	return objectDefinitions;
}

describe('getNestingDepthError', () => {
	it('allows a structure with five levels', () => {
		expect(
			getNestingDepthError({
				objectDefinitions: {},
				structure: buildStructure(buildReferences(4)),
			})
		).toBeNull();
	});

	it('rejects a structure with six levels', () => {
		expect(
			getNestingDepthError({
				objectDefinitions: {},
				structure: buildStructure(buildReferences(5)),
			})
		).toBe('exceeded');
	});

	it('counts repeatable groups as levels', () => {
		expect(
			getNestingDepthError({
				objectDefinitions: {},
				structure: buildStructure([
					buildGroup({
						children: buildReferences(4),
						repeatable: true,
					}),
				]),
			})
		).toBe('exceeded');
	});

	it('does not count non-repeatable groups as levels', () => {
		expect(
			getNestingDepthError({
				objectDefinitions: {},
				structure: buildStructure([
					buildGroup({
						children: buildReferences(4),
						repeatable: false,
					}),
				]),
			})
		).toBeNull();
	});

	it('allows a referenced structure whose parents stay within the limit', () => {
		expect(
			getNestingDepthError({
				objectDefinitions: buildParents(2),
				structure: buildStructure(buildReferences(2)),
			})
		).toBeNull();
	});

	it('rejects a referenced structure whose parents would exceed the limit', () => {
		expect(
			getNestingDepthError({
				objectDefinitions: buildParents(3),
				structure: buildStructure(buildReferences(2)),
			})
		).toBe('exceeded-in-references');
	});

	it('ignores relationships that are not edges or are reverse', () => {
		expect(
			getNestingDepthError({
				objectDefinitions: {
					'parent-0': buildObjectDefinition({
						edge: false,
						erc: 'parent-0',
						target: 'structure-erc',
					}),
					'parent-1': buildObjectDefinition({
						erc: 'parent-1',
						reverse: true,
						target: 'structure-erc',
					}),
				},
				structure: buildStructure(buildReferences(4)),
			})
		).toBeNull();
	});

	it('stops on circular references', () => {
		expect(
			getNestingDepthError({
				objectDefinitions: {
					'parent-0': buildObjectDefinition({
						erc: 'parent-0',
						target: 'structure-erc',
					}),
					'structure-erc': buildObjectDefinition({
						erc: 'structure-erc',
						target: 'parent-0',
					}),
				},
				structure: buildStructure(),
			})
		).toBeNull();
	});
});

/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, renderHook} from '@testing-library/react';
import React, {ReactNode} from 'react';

import {ObjectDefinition} from '../../../../src/main/resources/META-INF/resources/js/common/types/ObjectDefinition';
import StateContextProvider, {
	State,
	useSelector,
	useStateDispatch,
} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/contexts/StateContext';
import {
	NonRepeatableGroup,
	RelatedContent,
	RepeatableGroup,
	Structure,
	StructureChild,
} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Structure';
import {Field} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/field';
import getUuid from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/structure_builder/config',
	() => ({
		config: {
			objectFolderExternalReferenceCode: 'L_CMS_CONTENT_STRUCTURES',
		},
	})
);

const CHILD_UUID = getUuid();
const RELATED_CONTENT_UUID = getUuid();
const STRUCTURE_UUID = getUuid();

const RELATED_CONTENT: RelatedContent = {
	erc: 'related-content-erc',
	label: {},
	multiselection: false,
	name: 'relatedContent',
	parent: STRUCTURE_UUID,
	relatedStructureERC: 'target-structure-erc',
	type: 'related-content',
	uuid: RELATED_CONTENT_UUID,
};

function buildField(overrides: Partial<Field> = {}): Field {
	return {
		erc: 'field-erc',
		indexableConfig: {indexed: false},
		label: {en_US: 'Original Label'},
		localized: false,
		locked: false,
		name: 'originalName',
		parent: STRUCTURE_UUID,
		required: false,
		settings: {},
		type: 'text',
		uuid: getUuid(),
		...overrides,
	};
}

function buildGroup(overrides: Partial<RepeatableGroup> = {}): RepeatableGroup {
	return {
		children: new Map(),
		erc: 'group-erc',
		isRepeatable: true,
		label: {en_US: 'Group'},
		name: 'group',
		parent: STRUCTURE_UUID,
		relationshipERC: 'group-relationship-erc',
		relationshipName: 'groupRelationship',
		type: 'group',
		uuid: CHILD_UUID,
		...overrides,
	};
}

function buildObjectDefinition(
	overrides: Partial<ObjectDefinition> = {}
): ObjectDefinition {
	return {
		externalReferenceCode: 'BASE_ERC',
		label: {en_US: 'Base'},
		name: 'base',
		objectFields: [
			{
				DBType: 'String',
				businessType: 'Text',
				externalReferenceCode: 'CODE',
				indexed: false,
				label: {en_US: 'Code'},
				localized: false,
				name: 'code',
				required: false,
				system: false,
			},
			{
				DBType: 'String',
				businessType: 'Text',
				externalReferenceCode: 'WIDTH',
				indexed: false,
				label: {en_US: 'Width'},
				localized: false,
				name: 'width',
				required: false,
				system: false,
			},
		],
		...overrides,
	} as ObjectDefinition;
}

function buildState({
	children = [],
	...structure
}: Partial<Omit<Structure, 'children'>> & {
	children?: StructureChild[];
} = {}): State {
	return {
		clipboard: null,
		defaultLanguageLabels: {labels: {}, locale: 'en_US'},
		history: {
			deletedChildren: [],
			deletedGroupERCs: [],
			deletedRelationships: [],
			modifiedNames: new Set(),
			modifiedSlugs: new Set(),
		},
		invalids: new Map(),
		operation: null,
		publishedChildren: new Set(),
		renamingItemUuid: null,
		savedChildren: new Set(),
		selection: [],
		structure: {
			children: new Map(children.map((child) => [child.uuid, child])),
			erc: 'structure-erc',
			label: {en_US: 'Structure'},
			name: 'myStructure',
			path: '',
			slug: '',
			spaces: [],
			status: 'draft',
			system: false,
			type: 'L_CMS_CONTENT_STRUCTURES',
			uuid: STRUCTURE_UUID,
			workflows: {},
			...structure,
		},
		systemFieldNames: {},
		unsavedChanges: false,
	};
}

function renderState({
	baseObjectDefinition = null,
	state = buildState(),
}: {
	baseObjectDefinition?: ObjectDefinition | null;
	state?: State;
} = {}) {
	const wrapper = ({children}: {children: ReactNode}) => (
		<StateContextProvider
			baseObjectDefinition={baseObjectDefinition}
			initialState={state}
		>
			{children}
		</StateContextProvider>
	);

	return renderHook(
		() => ({
			dispatch: useStateDispatch(),
			state: useSelector((state) => state),
		}),
		{wrapper}
	);
}

describe('StateContext update-field', () => {
	it('Keeps the name of a locked field when its label changes', () => {
		const field = buildField({locked: true, name: 'title'});

		const {result} = renderState({state: buildState({children: [field]})});

		act(() => {
			result.current.dispatch({
				label: {en_US: 'Headline'},
				type: 'update-field',
				uuid: field.uuid,
			});
		});

		const updated = result.current.state.structure.children.get(
			field.uuid
		) as Field;

		expect(updated.label).toEqual({en_US: 'Headline'});
		expect(updated.name).toBe('title');
	});

	it('Auto renames an unlocked field when its label changes', () => {
		const field = buildField();

		const {result} = renderState({state: buildState({children: [field]})});

		act(() => {
			result.current.dispatch({
				label: {en_US: 'Headline'},
				type: 'update-field',
				uuid: field.uuid,
			});
		});

		const updated = result.current.state.structure.children.get(
			field.uuid
		) as Field;

		expect(updated.label).toEqual({en_US: 'Headline'});
		expect(updated.name).toBe('headline');
	});
});

describe('StateContext duplicate-children', () => {
	it('Reparents nested children when a repeatable group is duplicated', () => {
		const group = buildGroup({uuid: getUuid()});

		const nestedField = buildField({parent: group.uuid});

		group.children.set(nestedField.uuid, nestedField);

		const {result} = renderState({state: buildState({children: [group]})});

		act(() => {
			result.current.dispatch({
				type: 'duplicate-children',
				uuids: [group.uuid],
			});
		});

		const [, duplicate] = Array.from(
			result.current.state.structure.children.values()
		) as RepeatableGroup[];

		const [duplicateNested] = Array.from(duplicate.children.values());

		expect(duplicate.uuid).not.toBe(group.uuid);
		expect(duplicateNested.parent).toBe(duplicate.uuid);
	});
});

describe('StateContext rename-item', () => {
	function buildRenameState({
		childLabel,
		structureLabel,
	}: {
		childLabel: Liferay.Language.LocalizedValue<string>;
		structureLabel: Liferay.Language.LocalizedValue<string>;
	}): State {
		return buildState({
			children: [
				buildGroup({
					erc: 'child-erc',
					label: childLabel,
					relationshipERC: 'rel-erc',
					relationshipName: 'rel',
				}),
			],
			label: structureLabel,
			name: 'MyStructure',
			spaces: 'all',
			status: 'new',
		});
	}

	let getLanguageIdSpy: jest.SpyInstance;

	beforeEach(() => {
		getLanguageIdSpy = jest.spyOn(Liferay.ThemeDisplay, 'getLanguageId');
	});

	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('Writes the new label under the current language only, leaving the default language entry intact', () => {
		getLanguageIdSpy.mockReturnValue('es_ES');

		const {result} = renderState({
			state: buildRenameState({
				childLabel: {en_US: 'old', es_ES: 'old'},
				structureLabel: {en_US: 'old', es_ES: 'old'},
			}),
		});

		act(() => {
			result.current.dispatch({
				name: 'new',
				type: 'rename-item',
				uuid: STRUCTURE_UUID,
			});
		});

		expect(result.current.state.structure.label).toEqual({
			en_US: 'old',
			es_ES: 'new',
		});
	});

	it('Overwrites the single label key when the current and the default language match', () => {
		getLanguageIdSpy.mockReturnValue('en_US');

		const {result} = renderState({
			state: buildRenameState({
				childLabel: {en_US: 'old'},
				structureLabel: {en_US: 'old'},
			}),
		});

		act(() => {
			result.current.dispatch({
				name: 'new',
				type: 'rename-item',
				uuid: STRUCTURE_UUID,
			});
		});

		expect(result.current.state.structure.label).toEqual({en_US: 'new'});
		expect(result.current.state.renamingItemUuid).toBeNull();
	});

	it('Renames a child found by uuid under the current language only', () => {
		getLanguageIdSpy.mockReturnValue('es_ES');

		const {result} = renderState({
			state: buildRenameState({
				childLabel: {en_US: 'old', es_ES: 'old'},
				structureLabel: {en_US: 'root', es_ES: 'root'},
			}),
		});

		act(() => {
			result.current.dispatch({
				name: 'new',
				type: 'rename-item',
				uuid: CHILD_UUID,
			});
		});

		const child = result.current.state.structure.children.get(
			CHILD_UUID
		) as RepeatableGroup;

		expect(child.label).toEqual({en_US: 'old', es_ES: 'new'});
		expect(result.current.state.structure.label).toEqual({
			en_US: 'root',
			es_ES: 'root',
		});
	});
});

describe('StateContext update-structure', () => {
	beforeEach(() => {
		jest.spyOn(Liferay.ThemeDisplay, 'getLanguageId').mockReturnValue(
			'en_US'
		);
		jest.spyOn(
			Liferay.ThemeDisplay,
			'getDefaultLanguageId'
		).mockReturnValue('en_US');
	});

	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('Auto generates the friendly URL slug from the label', () => {
		const {result} = renderState({
			state: buildState({label: {en_US: ''}, status: 'new'}),
		});

		act(() => {
			result.current.dispatch({
				label: {en_US: 'Product Categories'},
				objectDefinitions: {},
				type: 'update-structure',
			});
		});

		expect(result.current.state.structure.slug).toBe('product-categories');
	});

	it('Stops auto generating once the friendly URL is edited manually', () => {
		const {result} = renderState({
			state: buildState({label: {en_US: ''}, status: 'new'}),
		});

		act(() => {
			result.current.dispatch({
				label: {en_US: 'Product Categories'},
				objectDefinitions: {},
				type: 'update-structure',
			});
		});

		act(() => {
			result.current.dispatch({
				slug: 'custom-slug',
				type: 'update-structure',
			});
		});

		act(() => {
			result.current.dispatch({
				label: {en_US: 'Something Else'},
				objectDefinitions: {},
				type: 'update-structure',
			});
		});

		expect(result.current.state.structure.slug).toBe('custom-slug');
	});

	it('Resumes auto generating when the friendly URL is cleared', () => {
		const {result} = renderState({
			state: buildState({label: {en_US: ''}, status: 'new'}),
		});

		act(() => {
			result.current.dispatch({
				slug: 'custom-slug',
				type: 'update-structure',
			});
		});

		act(() => {
			result.current.dispatch({slug: '', type: 'update-structure'});
		});

		act(() => {
			result.current.dispatch({
				label: {en_US: 'Product Categories'},
				objectDefinitions: {},
				type: 'update-structure',
			});
		});

		expect(result.current.state.structure.slug).toBe('product-categories');
	});
});

describe('StateContext start-operation', () => {
	function buildPublishedState(): State {
		return buildState({
			label: {},
			name: 'MyStructure',
			spaces: 'all',
			status: 'published',
		});
	}

	it('Keeps the persisted status untouched while an operation is in flight', () => {
		const {result} = renderState({state: buildPublishedState()});

		act(() => {
			result.current.dispatch({
				operation: 'publishing',
				type: 'start-operation',
			});
		});

		expect(result.current.state.operation).toBe('publishing');
		expect(result.current.state.structure.status).toBe('published');
	});

	it('Keeps regenerating the name and the friendly URL of a published structure disabled while publishing', () => {
		const {result} = renderState({state: buildPublishedState()});

		act(() => {
			result.current.dispatch({
				operation: 'publishing',
				type: 'start-operation',
			});
		});

		act(() => {
			result.current.dispatch({
				label: {en_US: 'Product Categories'},
				objectDefinitions: {},
				type: 'update-structure',
			});
		});

		expect(result.current.state.structure.name).toBe('MyStructure');
		expect(result.current.state.structure.slug).toBe('');
	});

	it('Clears the operation once it ends', () => {
		const {result} = renderState({state: buildPublishedState()});

		act(() => {
			result.current.dispatch({
				operation: 'publishing',
				type: 'start-operation',
			});
		});

		act(() => {
			result.current.dispatch({type: 'end-operation'});
		});

		expect(result.current.state.operation).toBeNull();
		expect(result.current.state.structure.status).toBe('published');
	});

	it('Keeps the operation running when an unrelated validation error is added', () => {
		const {result} = renderState({state: buildPublishedState()});

		act(() => {
			result.current.dispatch({
				operation: 'publishing',
				type: 'start-operation',
			});
		});

		act(() => {
			result.current.dispatch({
				error: 'empty',
				property: 'spaces',
				type: 'add-error',
				uuid: STRUCTURE_UUID,
			});
		});

		expect(result.current.state.operation).toBe('publishing');
	});
});

describe('StateContext move-children', () => {
	function buildMoveState(savedChildren: State['savedChildren']): State {
		return {
			...buildState({
				children: [buildGroup({label: {}}), RELATED_CONTENT],
				label: {},
				spaces: 'all',
				status: 'new',
			}),
			savedChildren,
		};
	}

	it('Records the relationship of a saved but unpublished child moved into a group', () => {
		const {result} = renderState({
			state: buildMoveState(new Set([RELATED_CONTENT_UUID])),
		});

		act(() => {
			result.current.dispatch({
				items: [RELATED_CONTENT],
				targetUuid: CHILD_UUID,
				type: 'move-children',
			});
		});

		expect(result.current.state.history.deletedRelationships).toEqual([
			{
				relationshipERC: 'related-content-erc',
				structureERC: 'target-structure-erc',
			},
		]);
	});

	it('Records nothing for a child that was never saved', () => {
		const {result} = renderState({state: buildMoveState(new Set())});

		act(() => {
			result.current.dispatch({
				items: [RELATED_CONTENT],
				targetUuid: CHILD_UUID,
				type: 'move-children',
			});
		});

		expect(result.current.state.history.deletedRelationships).toEqual([]);
	});
});

describe('StateContext base object definition', () => {
	function renderNewStructure(
		baseObjectDefinition: ObjectDefinition | null = null
	) {
		return renderState({
			baseObjectDefinition,
			state: buildState({erc: ''}),
		});
	}

	it('Starts a new structure from the default fields when there is no base', () => {
		const {result} = renderNewStructure();

		expect(
			Array.from(result.current.state.structure.children.values()).map(
				({name}) => name
			)
		).toEqual(['title']);
	});

	it('Starts a new structure from the fields of the base', () => {
		const {result} = renderNewStructure(buildObjectDefinition());

		const names = Array.from(
			result.current.state.structure.children.values()
		).map(({name}) => name);

		expect(names).toContain('code');
		expect(names).not.toContain('title');
		expect(names).toContain('width');
	});

	it('Starts a new structure from the groups of the base object layout', () => {
		const {result} = renderNewStructure(
			buildObjectDefinition({
				objectLayouts: [
					{
						objectLayoutTabs: [
							{
								name: {en_US: 'Details'},
								objectLayoutBoxes: [
									{
										collapsable: false,
										name: {en_US: 'Details'},
										objectLayoutRows: [
											{
												objectLayoutColumns: [
													{objectFieldName: 'code'},
												],
											},
										],
									},
									{
										collapsable: true,
										name: {en_US: 'Dimensions'},
										objectLayoutRows: [
											{
												objectLayoutColumns: [
													{objectFieldName: 'width'},
												],
											},
										],
									},
								],
							},
						],
					},
				],
			} as Partial<ObjectDefinition>)
		);

		const [tab] = Array.from(
			result.current.state.structure.children.values()
		);

		expect(tab.type).toBe('group');
		expect(tab.label).toEqual({en_US: 'Details'});

		const tabChildren = Array.from(
			(tab as NonRepeatableGroup).children.values()
		);

		expect(tabChildren.map(({name}) => name)).toContain('code');

		const panel = tabChildren.find(({type}) => type === 'group');

		expect(panel?.label).toEqual({en_US: 'Dimensions'});
		expect(
			Array.from((panel as NonRepeatableGroup).children.values()).map(
				({name}) => name
			)
		).toEqual(['width']);
	});
});

/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {renderHook} from '@testing-library/react';
import React, {ReactNode} from 'react';

import {ObjectDefinition} from '../../../../src/main/resources/META-INF/resources/js/common/types/ObjectDefinition';
import StateContextProvider, {
	State,
	useSelector,
} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/contexts/StateContext';
import {NonRepeatableGroup} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Structure';
import {setBaseObjectDefinition} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/baseObjectDefinition';
import getUuid from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/structure_builder/config',
	() => ({
		config: {
			objectFolderExternalReferenceCode: 'L_CMS_CONTENT_STRUCTURES',
		},
	})
);

const STRUCTURE_UUID = getUuid();

function buildBaseObjectDefinition(
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

function buildInitialState(): State {
	return {
		clipboard: null,
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
			children: new Map(),
			erc: '',
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
		},
		unsavedChanges: false,
	};
}

function renderStructureHook() {
	const wrapper = ({children}: {children: ReactNode}) => (
		<StateContextProvider initialState={buildInitialState()}>
			{children}
		</StateContextProvider>
	);

	return renderHook(() => useSelector((state) => state.structure), {wrapper});
}

describe('StateContext base object definition', () => {
	afterEach(() => {
		setBaseObjectDefinition(null, {});
	});

	it('Starts a new structure from the default fields when there is no base', () => {
		setBaseObjectDefinition(null, {});

		const {result} = renderStructureHook();

		expect(
			Array.from(result.current.children.values()).map(({name}) => name)
		).toEqual(['title']);
	});

	it('Starts a new structure from the fields of the base', () => {
		setBaseObjectDefinition(buildBaseObjectDefinition(), {});

		const {result} = renderStructureHook();

		const names = Array.from(result.current.children.values()).map(
			({name}) => name
		);

		expect(names).toContain('code');
		expect(names).not.toContain('title');
		expect(names).toContain('width');
	});

	it('Starts a new structure from the groups of the base object layout', () => {
		setBaseObjectDefinition(
			buildBaseObjectDefinition({
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
			} as Partial<ObjectDefinition>),
			{}
		);

		const {result} = renderStructureHook();

		const [tab] = Array.from(result.current.children.values());

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

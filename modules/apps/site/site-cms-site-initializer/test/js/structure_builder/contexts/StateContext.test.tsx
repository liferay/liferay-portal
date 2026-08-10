/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, renderHook} from '@testing-library/react';
import React, {ReactNode} from 'react';

import StateContextProvider, {
	State,
	useSelector,
	useStateDispatch,
} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/contexts/StateContext';
import {
	RepeatableGroup,
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

const STRUCTURE_UUID = getUuid();

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

function buildInitialState(child: StructureChild): State {
	const children = new Map();

	children.set(child.uuid, child);

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
		publishedChildren: new Set(),
		renamingItemUuid: null,
		savedChildren: new Set(),
		selection: [],
		structure: {
			children,
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
		},
		unsavedChanges: false,
	};
}

function renderReducerHook<T extends StructureChild>(child: T) {
	const wrapper = ({children}: {children: ReactNode}) => (
		<StateContextProvider initialState={buildInitialState(child)}>
			{children}
		</StateContextProvider>
	);

	return renderHook(
		() => ({
			dispatch: useStateDispatch(),
			field: useSelector(
				(state) => state.structure.children.get(child.uuid) as T
			),
			structure: useSelector((state) => state.structure),
		}),
		{wrapper}
	);
}

describe('StateContext reducer', () => {
	describe('update-field', () => {
		it('keeps the name of a locked field when its label changes', () => {
			const lockedField = buildField({locked: true, name: 'title'});

			const {result} = renderReducerHook(lockedField);

			act(() => {
				result.current.dispatch({
					label: {en_US: 'Headline'},
					type: 'update-field',
					uuid: lockedField.uuid,
				});
			});

			expect(result.current.field.label).toEqual({en_US: 'Headline'});
			expect(result.current.field.name).toBe('title');
		});

		it('auto-renames an unlocked field when its label changes', () => {
			const unlockedField = buildField({
				locked: false,
				name: 'originalName',
			});

			const {result} = renderReducerHook(unlockedField);

			act(() => {
				result.current.dispatch({
					label: {en_US: 'Headline'},
					type: 'update-field',
					uuid: unlockedField.uuid,
				});
			});

			expect(result.current.field.label).toEqual({en_US: 'Headline'});
			expect(result.current.field.name).toBe('headline');
		});
	});

	it('reparents nested children when a repeatable group is duplicated', () => {
		const group: RepeatableGroup = {
			children: new Map(),
			erc: 'group-erc',
			label: {en_US: 'Group'},
			name: 'group',
			parent: STRUCTURE_UUID,
			relationshipERC: 'group-relationship-erc',
			relationshipName: 'groupRelationship',
			type: 'repeatable-group',
			uuid: getUuid(),
		};

		const nestedField = buildField({parent: group.uuid});

		group.children.set(nestedField.uuid, nestedField);

		const {result} = renderReducerHook(group);

		act(() => {
			result.current.dispatch({
				type: 'duplicate-children',
				uuids: [group.uuid],
			});
		});

		const [, duplicate] = Array.from(
			result.current.structure.children.values()
		) as RepeatableGroup[];

		const [duplicateNested] = Array.from(duplicate.children.values());

		expect(duplicate.uuid).not.toBe(group.uuid);
		expect(duplicateNested.parent).toBe(duplicate.uuid);
	});
});

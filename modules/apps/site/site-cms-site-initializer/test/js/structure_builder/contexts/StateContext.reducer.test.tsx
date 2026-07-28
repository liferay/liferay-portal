/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, render} from '@testing-library/react';
import React, {Dispatch, useEffect} from 'react';

import {
	Action,
	State,
	StateContextProvider,
	useSelector,
	useStateDispatch,
} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/contexts/StateContext';
import {
	RepeatableGroup,
	Structure,
} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Structure';
import getUuid from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';

const STRUCTURE_UUID = getUuid();
const CHILD_UUID = getUuid();

function buildInitialState({
	childLabel,
	structureLabel,
}: {
	childLabel: Liferay.Language.LocalizedValue<string>;
	structureLabel: Liferay.Language.LocalizedValue<string>;
}): State {
	const child: RepeatableGroup = {
		children: new Map(),
		erc: 'child-erc',
		label: childLabel,
		name: 'group',
		parent: STRUCTURE_UUID,
		relationshipERC: 'rel-erc',
		relationshipName: 'rel',
		type: 'repeatable-group',
		uuid: CHILD_UUID,
	};

	const structure: Structure = {
		children: new Map([[CHILD_UUID, child]]),
		erc: 'structure-erc',
		label: structureLabel,
		name: 'MyStructure',
		path: '',
		slug: '',
		spaces: 'all',
		status: 'new',
		system: false,
		type: 'L_CMS_CONTENT_STRUCTURES',
		uuid: STRUCTURE_UUID,
		workflows: {},
	};

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
		selection: [],
		structure,
		unsavedChanges: false,
	};
}

type Refs = {
	dispatch?: Dispatch<Action>;
	state?: State;
};

function renderWithState(initialState: State) {
	const refs: Refs = {};

	function Harness() {
		const state = useSelector((s) => s);
		const dispatch = useStateDispatch();

		useEffect(() => {
			refs.dispatch = dispatch;
			refs.state = state;
		});

		return null;
	}

	render(
		<StateContextProvider initialState={initialState}>
			<Harness />
		</StateContextProvider>
	);

	return refs;
}

describe('StateContext reducer — rename-item', () => {
	let getLanguageIdSpy: jest.SpyInstance;

	beforeEach(() => {
		getLanguageIdSpy = jest.spyOn(Liferay.ThemeDisplay, 'getLanguageId');
	});

	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('writes the new label under the current language only, leaving default-language entry intact', () => {
		getLanguageIdSpy.mockReturnValue('es_ES');

		const refs = renderWithState(
			buildInitialState({
				childLabel: {en_US: 'old', es_ES: 'old'} as any,
				structureLabel: {en_US: 'old', es_ES: 'old'} as any,
			})
		);

		act(() => {
			refs.dispatch!({
				name: 'new',
				type: 'rename-item',
				uuid: STRUCTURE_UUID,
			});
		});

		expect(refs.state!.structure.label).toEqual({
			en_US: 'old',
			es_ES: 'new',
		});
	});

	it('overwrites the single label key when current and default language match', () => {
		getLanguageIdSpy.mockReturnValue('en_US');

		const refs = renderWithState(
			buildInitialState({
				childLabel: {en_US: 'old'} as any,
				structureLabel: {en_US: 'old'} as any,
			})
		);

		act(() => {
			refs.dispatch!({
				name: 'new',
				type: 'rename-item',
				uuid: STRUCTURE_UUID,
			});
		});

		expect(refs.state!.structure.label).toEqual({en_US: 'new'});
		expect(refs.state!.renamingItemUuid).toBeNull();
	});

	it('renames a child found by uuid under the current language only', () => {
		getLanguageIdSpy.mockReturnValue('es_ES');

		const refs = renderWithState(
			buildInitialState({
				childLabel: {en_US: 'old', es_ES: 'old'} as any,
				structureLabel: {en_US: 'root', es_ES: 'root'} as any,
			})
		);

		act(() => {
			refs.dispatch!({
				name: 'new',
				type: 'rename-item',
				uuid: CHILD_UUID,
			});
		});

		const child = refs.state!.structure.children.get(
			CHILD_UUID
		) as RepeatableGroup;

		expect(child.label).toEqual({en_US: 'old', es_ES: 'new'});
		expect(refs.state!.structure.label).toEqual({
			en_US: 'root',
			es_ES: 'root',
		});
	});

	it('leaves labels unchanged when the uuid matches no item', () => {
		getLanguageIdSpy.mockReturnValue('es_ES');

		const refs = renderWithState(
			buildInitialState({
				childLabel: {en_US: 'old', es_ES: 'old'} as any,
				structureLabel: {en_US: 'root', es_ES: 'root'} as any,
			})
		);

		act(() => {
			refs.dispatch!({
				name: 'new',
				type: 'rename-item',
				uuid: getUuid(),
			});
		});

		const child = refs.state!.structure.children.get(
			CHILD_UUID
		) as RepeatableGroup;

		expect(child.label).toEqual({en_US: 'old', es_ES: 'old'});
		expect(refs.state!.structure.label).toEqual({
			en_US: 'root',
			es_ES: 'root',
		});
	});
});

describe('StateContext reducer — update-structure friendly URL', () => {
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

	it('auto-generates the friendly URL slug from the label', () => {
		const refs = renderWithState(
			buildInitialState({
				childLabel: {en_US: 'child'} as any,
				structureLabel: {en_US: ''} as any,
			})
		);

		act(() => {
			refs.dispatch!({
				label: {en_US: 'Product Categories'} as any,
				objectDefinitions: {},
				type: 'update-structure',
			});
		});

		expect(refs.state!.structure.slug).toBe('product-categories');
	});

	it('stops auto-generating once the friendly URL is edited manually', () => {
		const refs = renderWithState(
			buildInitialState({
				childLabel: {en_US: 'child'} as any,
				structureLabel: {en_US: ''} as any,
			})
		);

		act(() => {
			refs.dispatch!({
				label: {en_US: 'Product Categories'} as any,
				objectDefinitions: {},
				type: 'update-structure',
			});
		});

		act(() => {
			refs.dispatch!({
				slug: 'custom-slug',
				type: 'update-structure',
			});
		});

		act(() => {
			refs.dispatch!({
				label: {en_US: 'Something Else'} as any,
				objectDefinitions: {},
				type: 'update-structure',
			});
		});

		expect(refs.state!.structure.slug).toBe('custom-slug');
	});

	it('resumes auto-generating when the friendly URL is cleared', () => {
		const refs = renderWithState(
			buildInitialState({
				childLabel: {en_US: 'child'} as any,
				structureLabel: {en_US: ''} as any,
			})
		);

		act(() => {
			refs.dispatch!({
				slug: 'custom-slug',
				type: 'update-structure',
			});
		});

		act(() => {
			refs.dispatch!({slug: '', type: 'update-structure'});
		});

		act(() => {
			refs.dispatch!({
				label: {en_US: 'Product Categories'} as any,
				objectDefinitions: {},
				type: 'update-structure',
			});
		});

		expect(refs.state!.structure.slug).toBe('product-categories');
	});
});

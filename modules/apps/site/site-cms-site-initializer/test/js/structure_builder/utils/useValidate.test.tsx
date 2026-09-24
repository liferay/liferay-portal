/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, render} from '@testing-library/react';
import React, {useEffect} from 'react';

import {
	State,
	StateContextProvider,
	useSelector,
} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/contexts/StateContext';
import getUuid from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';
import {
	ErrorMap,
	useValidate,
} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/validation';

const STRUCTURE_UUID = getUuid();

function buildState({
	errors,
	label,
}: {
	errors: ErrorMap;
	label: Liferay.Language.LocalizedValue<string>;
}): State {
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
		invalids: new Map([[STRUCTURE_UUID, errors]]),
		operation: null,
		publishedChildren: new Set(),
		renamingItemUuid: null,
		savedChildren: new Set(),
		selection: [],
		structure: {
			children: new Map(),
			erc: 'structure-erc',
			label,
			name: 'MyStructure',
			path: '',
			slug: 'my-structure',
			spaces: 'all',
			status: 'draft',
			system: false,
			titleFieldName: 'title',
			type: 'L_CMS_CONTENT_STRUCTURES',
			uuid: STRUCTURE_UUID,
			workflows: {},
		},
		systemFieldNames: {},
		unsavedChanges: false,
	};
}

function renderValidate(initialState: State) {
	const refs: {state?: State; validate?: () => boolean} = {};

	function Harness() {
		const state = useSelector((s) => s);
		const validate = useValidate();

		useEffect(() => {
			refs.state = state;
			refs.validate = validate;
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

describe('useValidate', () => {
	it('passes when the only errors were reported by a previous rejected save', () => {
		const refs = renderValidate(
			buildState({
				errors: new Map([['global', 'unexpected']]),
				label: {en_US: 'Label'},
			})
		);

		expect(refs.validate!()).toBe(true);
	});

	it('drops the errors reported by the server when other errors remain', () => {
		const refs = renderValidate(
			buildState({
				errors: new Map([['global', 'unexpected']]),
				label: {en_US: ''},
			})
		);

		let valid;

		act(() => {
			valid = refs.validate!();
		});

		expect(valid).toBe(false);
		expect(refs.state!.invalids.get(STRUCTURE_UUID)?.get('global')).toBe(
			'default-language-label'
		);
	});
});

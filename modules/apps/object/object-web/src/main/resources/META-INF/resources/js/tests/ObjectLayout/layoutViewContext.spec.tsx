/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	TAction,
	TYPES,
	layoutReducer,
} from '../../components/Layout/objectLayoutContext';
import {BoxType} from '../../components/Layout/types';

describe('viewReducer ADD_OBJECT_LAYOUT_TAB', () => {
	it('can add new layout tab', () => {
		const state = {
			creationLanguageId: 'en_US' as Liferay.Language.Locale,
			enableCategorization: true,
			enableFriendlyURLCustomization: true,
			isViewOnly: false,
			objectFieldBusinessTypes: [],
			objectFields: [],
			objectLayout: {
				defaultObjectLayout: false,
				name: {
					en_US: 'layout',
				},
				objectDefinitionExternalReferenceCode: '',
				objectLayoutTabs: [],
			},
			objectLayoutId: '',
			objectRelationships: [],
		};

		const action: TAction = {
			payload: {
				name: {
					en_US: 'tab',
				},
				objectRelationshipId: 0,
			},
			type: TYPES.ADD_OBJECT_LAYOUT_TAB,
		};

		const result = layoutReducer(state, action);

		expect(result.objectLayout.objectLayoutTabs[0].name.en_US).toBe('tab');
	});
});

describe('viewReducer ADD_OBJECT_LAYOUT_BOX', () => {
	it('can add new layout box in correct order', () => {
		const state = {
			creationLanguageId: 'en_US' as Liferay.Language.Locale,
			enableCategorization: true,
			enableFriendlyURLCustomization: true,
			isViewOnly: false,
			objectFieldBusinessTypes: [],
			objectFields: [],
			objectLayout: {
				defaultObjectLayout: false,
				name: {
					en_US: 'layout',
				},
				objectDefinitionExternalReferenceCode: '',
				objectLayoutTabs: [
					{
						name: {en_US: 'Main Tab'},
						objectLayoutBoxes: [
							{
								collapsable: false,
								name: {en_US: 'SEO Box'},
								objectLayoutRows: [],
								priority: 0,
								type: 'seo' as BoxType,
							},
						],
						objectRelationshipId: 0,
						priority: 0,
					},
				],
			},
			objectLayoutId: '',
			objectRelationships: [],
		};

		const tabIndex = 0;

		const regularBox1: TAction = {
			payload: {
				name: {
					en_US: 'Regular Box 1',
				},
				tabIndex,
				type: 'regular',
			},
			type: TYPES.ADD_OBJECT_LAYOUT_BOX,
		};

		let result = layoutReducer(state, regularBox1);

		const layoutBoxes =
			result.objectLayout.objectLayoutTabs[tabIndex].objectLayoutBoxes;

		expect(layoutBoxes[0].name.en_US).toBe('Regular Box 1');
		expect(layoutBoxes[1].name.en_US).toBe('SEO Box');
		expect(layoutBoxes.length).toBe(2);

		const addCategorization: TAction = {
			payload: {
				name: {
					en_US: 'Categorization Box',
				},
				tabIndex,
				type: 'categorization',
			},
			type: TYPES.ADD_OBJECT_LAYOUT_BOX,
		};

		result = layoutReducer(result, addCategorization);

		expect(layoutBoxes[0].name.en_US).toBe('Regular Box 1');
		expect(layoutBoxes[1].name.en_US).toBe('Categorization Box');
		expect(layoutBoxes[2].name.en_US).toBe('SEO Box');
		expect(layoutBoxes.length).toBe(3);

		const regularBox2: TAction = {
			payload: {
				name: {
					en_US: 'Regular Box 2',
				},
				tabIndex,
				type: 'regular',
			},
			type: TYPES.ADD_OBJECT_LAYOUT_BOX,
		};

		result = layoutReducer(result, regularBox2);

		expect(layoutBoxes[0].name.en_US).toBe('Regular Box 1');
		expect(layoutBoxes[1].name.en_US).toBe('Regular Box 2');
		expect(layoutBoxes[2].name.en_US).toBe('Categorization Box');
		expect(layoutBoxes[3].name.en_US).toBe('SEO Box');
		expect(layoutBoxes.length).toBe(4);
	});
});

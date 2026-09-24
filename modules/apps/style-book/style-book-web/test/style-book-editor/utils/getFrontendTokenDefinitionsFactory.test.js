/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getFrontendTokenDefinitionsFactory} from '../../../src/main/resources/META-INF/resources/js/style-book-editor/utils/getFrontendTokenDefinitionsFactory';

function getFrontendTokenDefinition(id, frontendTokenName) {
	return {
		frontendTokenCategories: [
			{
				frontendTokenSets: [
					{
						frontendTokens: [
							{defaultValue: '#000', name: frontendTokenName},
						],
						name: 'set1',
					},
				],
				name: 'category1',
			},
		],
		id,
	};
}

describe('getFrontendTokenDefinitionsFactory', () => {
	it('merges the custom definition into the theme definition and drops empty definitions', () => {
		const getFrontendTokenDefinitions = getFrontendTokenDefinitionsFactory({
			frontendTokenDefinitions: [
				getFrontendTokenDefinition('theme', 'themeToken'),
				getFrontendTokenDefinition('clay', 'clayToken'),
				{frontendTokenCategories: [], id: 'empty'},
			],
			themeFrontendTokenDefinitionId: 'theme',
		});

		expect(
			getFrontendTokenDefinitions(
				getFrontendTokenDefinition('custom', 'customToken')
			)
		).toEqual([
			{
				frontendTokenCategories: [
					{
						frontendTokenSets: [
							{
								frontendTokens: [
									{
										defaultValue: '#000',
										name: 'themeToken',
										tokenDefinitionId: 'theme',
									},
									{
										defaultValue: '#000',
										name: 'customToken',
										tokenDefinitionId: 'custom',
									},
								],
								name: 'set1',
							},
						],
						name: 'category1',
					},
				],
				id: 'theme',
			},
			{
				frontendTokenCategories: [
					{
						frontendTokenSets: [
							{
								frontendTokens: [
									{
										defaultValue: '#000',
										name: 'clayToken',
										tokenDefinitionId: 'clay',
									},
								],
								name: 'set1',
							},
						],
						name: 'category1',
					},
				],
				id: 'clay',
			},
		]);
	});

	it('keeps the theme definition when the custom definition is empty', () => {
		const getFrontendTokenDefinitions = getFrontendTokenDefinitionsFactory({
			frontendTokenDefinitions: [
				getFrontendTokenDefinition('theme', 'themeToken'),
			],
			themeFrontendTokenDefinitionId: 'theme',
		});

		expect(getFrontendTokenDefinitions({}).map(({id}) => id)).toEqual([
			'theme',
		]);
	});
});

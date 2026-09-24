/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeCustomFrontendTokenDefinition} from '../../../src/main/resources/META-INF/resources/js/style-book-editor/utils/mergeCustomFrontendTokenDefinition';

function getThemeFrontendTokenDefinition() {
	return {
		frontendTokenCategories: [
			{
				frontendTokenSets: [
					{
						frontendTokens: [
							{defaultValue: '#000', name: 'themeToken1'},
							{
								defaultValue: '#000',
								label: 'Theme Token 2',
								name: 'themeToken2',
							},
						],
						name: 'set1',
					},
				],
				name: 'category1',
			},
		],
		id: 'theme',
	};
}

describe('mergeCustomFrontendTokenDefinition', () => {
	it('merges the custom definition into the theme definition by name', () => {
		const themeFrontendTokenDefinition = getThemeFrontendTokenDefinition();

		const merged = mergeCustomFrontendTokenDefinition({
			customFrontendTokenDefinition: {
				frontendTokenCategories: [
					{
						frontendTokenSets: [
							{
								frontendTokens: [
									{defaultValue: '#fff', name: 'themeToken2'},
									{defaultValue: '#fff', name: 'customToken'},
								],
								name: 'set1',
							},
							{frontendTokens: [], name: 'customSet'},
						],
						name: 'category1',
					},
					{frontendTokenSets: [], name: 'customCategory'},
				],
			},
			frontendTokenDefinition: themeFrontendTokenDefinition,
		});

		expect(merged).toEqual({
			frontendTokenCategories: [
				{
					frontendTokenSets: [
						{
							frontendTokens: [
								{defaultValue: '#000', name: 'themeToken1'},
								{defaultValue: '#fff', name: 'themeToken2'},
								{defaultValue: '#fff', name: 'customToken'},
							],
							name: 'set1',
						},
						{frontendTokens: [], name: 'customSet'},
					],
					name: 'category1',
				},
				{frontendTokenSets: [], name: 'customCategory'},
			],
			id: 'theme',
		});

		expect(themeFrontendTokenDefinition).toEqual(
			getThemeFrontendTokenDefinition()
		);
	});

	it('returns the theme definition when the custom definition is empty', () => {
		const themeFrontendTokenDefinition = getThemeFrontendTokenDefinition();

		expect(
			mergeCustomFrontendTokenDefinition({
				customFrontendTokenDefinition: {},
				frontendTokenDefinition: themeFrontendTokenDefinition,
			})
		).toBe(themeFrontendTokenDefinition);
	});
});

/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getFrontendTokensFactory} from '../../../src/main/resources/META-INF/resources/js/style-book-editor/utils/getFrontendTokensFactory';

function getFrontendTokenDefinition(id, frontendTokenName) {
	return {
		frontendTokenCategories: [
			{
				frontendTokenSets: [
					{
						frontendTokens: [
							{defaultValue: '#000', name: frontendTokenName},
						],
					},
				],
			},
		],
		id,
	};
}

describe('getFrontendTokensFactory', () => {
	it('adds the custom tokens to the frontend tokens', () => {
		const getFrontendTokens = getFrontendTokensFactory({
			frontendTokenDefinitions: [
				getFrontendTokenDefinition('theme', 'themeToken'),
			],
			themeFrontendTokenDefinitionId: 'theme',
		});

		expect(
			Object.keys(
				getFrontendTokens(
					getFrontendTokenDefinition('custom', 'customToken')
				)
			)
		).toEqual(['theme:themeToken', 'themeToken', 'custom:customToken']);
	});
});

/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getFrontendTokens} from '../../../src/main/resources/META-INF/resources/js/style-book-editor/utils/getFrontendTokens';

function getFrontendTokenDefinition(id, frontendTokenName) {
	return {
		frontendTokenCategories: [
			{
				frontendTokenSets: [
					{
						frontendTokens: [
							{defaultValue: '#000', name: frontendTokenName},
						],
						label: 'Set',
					},
				],
				label: 'Category',
			},
		],
		id,
	};
}

describe('getFrontendTokens', () => {
	it('keys every token by definition id and the theme tokens also by name', () => {
		const frontendToken = {
			defaultValue: '#000',
			tokenCategoryLabel: 'Category',
			tokenSetLabel: 'Set',
			value: '#000',
		};

		expect(
			getFrontendTokens(
				[
					getFrontendTokenDefinition('theme', 'themeToken'),
					getFrontendTokenDefinition('clay', 'clayToken'),
				],
				'theme'
			)
		).toEqual({
			'clay:clayToken': {...frontendToken, name: 'clay:clayToken'},
			'theme:themeToken': {...frontendToken, name: 'theme:themeToken'},
			'themeToken': {...frontendToken, name: 'themeToken'},
		});
	});
});

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests} from '@playwright/test';

import {languageOverridePageTest} from '../../../fixtures/languageOverridePageTest';
import {loginTest} from '../../../fixtures/loginTest';
import {TLanguageKey} from '../../../pages/portal-language-override-web/LanguageOverridePage';

const test = mergeTests(loginTest(), languageOverridePageTest);

const EXPECTED_LANGUAGE_KEY: TLanguageKey = {
	key: 'do-you-like-to-eat-pizza-with-anchovies',
	translations: [
		{
			languageId: 'ar-SA',
			value: 'هل تحب أكل البيتزا مع الأنشوفة؟',
		},
		{
			languageId: 'ca-ES',
			value: 'Us agrada menjar pizza amb anxoves?',
		},
		{
			languageId: 'de-AT',
			value: 'Essen Sie gerne Pizza mit Sardellen?',
		},
		{
			languageId: 'de-CH',
			value: 'Essen Sie gerne Pizza mit Sardellen?',
		},
		{
			languageId: 'de-DE',
			value: 'Essen Sie gerne Pizza mit Sardellen?',
		},
		{
			languageId: 'en-US',
			value: 'Do you like to eat pizza with anchovies?',
		},
		{
			languageId: 'es-AR',
			value: '¿Te gusta comer pizza con anchoas?',
		},
		{
			languageId: 'es-CO',
			value: '¿Le gusta comer pizza con anchoas?',
		},
		{
			languageId: 'es-ES',
			value: '¿Le gusta comer pizza con anchoas?',
		},
		{
			languageId: 'es-MX',
			value: '¿Le gusta comer pizza con anchoas?',
		},
		{
			languageId: 'fi-FI',
			value: 'Tykkäätkö pizzasta anjoviksilla?',
		},
		{
			languageId: 'fr-BE',
			value: 'Aimez-vous manger de la pizza aux anchois ?',
		},
		{
			languageId: 'fr-CH',
			value: 'Aimez-vous manger de la pizza aux anchois ?',
		},
		{
			languageId: 'fr-FR',
			value: 'Aimez-vous manger de la pizza aux anchois ?',
		},
		{
			languageId: 'hu-HU',
			value: 'Szereti a szardellás pizzát?',
		},
		{
			languageId: 'ja-JP',
			value: 'アンチョビピザを食べるのは好きですか？',
		},
		{
			languageId: 'nl-BE',
			value: 'Houdt u van pizza met ansjovis?',
		},
		{
			languageId: 'nl-NL',
			value: 'Houdt u van pizza met ansjovis?',
		},
		{
			languageId: 'pt-BR',
			value: 'Você gosta de comer pizza com anchovas?',
		},
		{
			languageId: 'pt-PT',
			value: 'Você gosta de comer pizza com anchovas?',
		},
		{
			languageId: 'sv-SE',
			value: 'Gillar du att äta pizza med ansjovis?',
		},
		{
			languageId: 'zh-CN',
			value: '您喜欢吃加凤尾鱼的披萨吗？',
		},
	],
};

test(
	'Imports the translations of a language client extension',
	{tag: '@LPD-36494'},
	async ({languageOverridePage}) => {
		await test.step('Check that the translations were imported', async () => {
			await languageOverridePage.goto();

			await languageOverridePage.changeFilter('Any Language');

			await languageOverridePage.searchLanguageKey(
				EXPECTED_LANGUAGE_KEY.key
			);

			await languageOverridePage.assertLanguageKeyInListView(
				EXPECTED_LANGUAGE_KEY
			);

			await languageOverridePage.assertLanguageKeyTranslations(
				EXPECTED_LANGUAGE_KEY
			);
		});

		await test.step('Check that a translation for a disabled locale was not imported', async () => {
			await languageOverridePage.goto();

			await languageOverridePage.changeFilter('Any Language');

			await languageOverridePage.searchLanguageKey(
				EXPECTED_LANGUAGE_KEY.key
			);

			await languageOverridePage.assertLanguageKeyHasNoOverrideForLocale(
				EXPECTED_LANGUAGE_KEY.key,
				'th-TH'
			);
		});
	}
);

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import path from 'path';

import {languageOverridePageTest} from '../../../fixtures/languageOverridePageTest';
import {loginTest} from '../../../fixtures/loginTest';
import {TLanguageKey} from '../../../pages/portal-language-override-web/LanguageOverridePage';
import getRandomString from '../../../utils/getRandomString';
import {readFileFromZip} from '../../../utils/zip';

export const test = mergeTests(loginTest(), languageOverridePageTest);

test('LPD-33373 assert that overriden translations can be exported', async ({
	languageOverridePage,
	page,
}) => {
	const languageKeys: TLanguageKey[] = [
		{
			key: getRandomString(),
			translations: [
				{
					languageId: 'en-US',
					value: getRandomString(),
				},
				{
					languageId: 'es-ES',
					value: getRandomString(),
				},
				{
					languageId: 'pt-BR',
					value: getRandomString(),
				},
			],
		},
		{
			key: getRandomString(),
			translations: [
				{
					languageId: 'en-US',
					value: getRandomString(),
				},
			],
		},
	];

	await languageOverridePage.goto();

	await languageOverridePage.addLanguageKeys(languageKeys);

	page.on('download', async (download) => {
		for (const languageKey of languageKeys) {
			for (let i = 0; i < languageKey.translations.length; i++) {
				const {languageId, value} = languageKey.translations[i];

				const fileContent = (await readFileFromZip(
					`Language_${languageId.replace('-', '_')}.properties`,
					await download.path()
				)) as string;

				expect(
					fileContent.includes(`${languageKey.key}=${value}`)
				).toBeTruthy();
			}
		}
	});

	await languageOverridePage.exportOverridenTranslations();
});

test(
	'Can import a language.properties file',
	{tag: '@LPD-55263'},
	async ({languageOverridePage}) => {
		await languageOverridePage.goto();

		await languageOverridePage.importLanguageFile({
			expectedErrorMessage: 'Empty values are not allowed.',
			filePath: path.join(
				__dirname,
				'/dependencies/language-with-invalid-format.properties'
			),
		});

		await languageOverridePage.importLanguageFile({
			expectedErrorMessage:
				'Keys longer than 1000 characters are not allowed.',
			filePath: path.join(
				__dirname,
				'/dependencies/language-with-more-than-1000-characters.properties'
			),
		});

		await languageOverridePage.importLanguageFile({
			filePath: path.join(
				__dirname,
				'/dependencies/language_pt_BR.properties'
			),
			languageId: 'pt-BR',
		});

		const languageKey: TLanguageKey = {
			key: 'inspiring-message',
			translations: [
				{
					languageId: 'pt-BR',
					value: 'Nenhum problema pode ser resolvido pelo mesmo estado de consciência que o criou.',
				},
			],
		};

		try {
			await languageOverridePage.searchLanguageKey(languageKey.key);

			await languageOverridePage.assertLanguageKeyNotInListView(
				languageKey.key
			);

			await languageOverridePage.changeLocale('en-US', 'pt-BR');

			await languageOverridePage.assertLanguageKeyInListView(languageKey);
		}
		finally {
			await languageOverridePage.removeAllTranslationOverrides(
				languageKey.key
			);
		}
	}
);

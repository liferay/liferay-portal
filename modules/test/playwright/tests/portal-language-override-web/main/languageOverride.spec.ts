/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests} from '@playwright/test';

import {languageOverridePageTest} from '../../../fixtures/languageOverridePageTest';
import {loginTest} from '../../../fixtures/loginTest';
import {TLanguageKey} from '../../../pages/portal-language-override-web/LanguageOverridePage';
import getRandomString from '../../../utils/getRandomString';

export const test = mergeTests(loginTest(), languageOverridePageTest);

test(
	'Can remove language translations',
	{tag: '@LPD-55263'},
	async ({languageOverridePage, page}) => {
		const languageKey1: TLanguageKey = {
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
		};
		const languageKey2: TLanguageKey = {
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
		};

		await languageOverridePage.goto();

		await test.step('Add two language keys translated for en-US, es-ES and pt-BR', async () => {
			await languageOverridePage.addLanguageKeys([
				languageKey1,
				languageKey2,
			]);
		});

		const existingLanguageKey = '0-analytics-cloud-connection';
		const value = getRandomString();

		await test.step('Update an existing language key', async () => {
			await languageOverridePage.editLanguageKey(existingLanguageKey);

			await languageOverridePage.updateTranslation('en-US', value);

			await languageOverridePage.saveButton.click();
		});

		await test.step('Filter by selected language (en-US)', async () => {
			await languageOverridePage.changeFilter('Selected Language');
		});

		await test.step('Search for the first language key and remove the translation for en-US', async () => {
			await languageOverridePage.searchLanguageKey(languageKey1.key);

			await languageOverridePage.removeTranslationOverrideForCurrentLocale(
				languageKey1.key
			);
		});

		await test.step('Assert that the first language key no longer has translation for en-US', async () => {
			await languageOverridePage.assertNoLanguageEntriesWereFound();
		});

		await test.step('Change locale to es-ES', async () => {
			await languageOverridePage.changeLocale('en-US', 'es-ES');
		});

		await test.step('Assert that there is a es-ES translation for the first language key', async () => {
			await languageOverridePage.assertLanguageKeyForSelectedLanguage(
				languageKey1.key
			);
		});

		await test.step('Remove all translations for the first language key', async () => {
			await languageOverridePage.removeAllTranslationOverrides(
				languageKey1.key
			);
		});

		await test.step('Assert that the first language key no longer has any translation', async () => {
			await languageOverridePage.assertNoLanguageEntriesWereFound();

			await languageOverridePage.changeLocale('es-ES', 'pt-BR');

			await languageOverridePage.assertNoLanguageEntriesWereFound();
		});

		await test.step('Edit the second language key', async () => {
			await languageOverridePage.searchLanguageKey(languageKey2.key);

			await languageOverridePage.editLanguageKey(languageKey2.key);
		});

		await test.step('From the edit language key page, clear all overrides', async () => {
			page.once('dialog', (dialog) => {
				dialog.accept();
			});

			await page.getByRole('link', {name: 'Clear All Overrides'}).click();
		});

		await test.step('Change filter to "Any Language" and assert that the second language key no longer exists', async () => {
			await languageOverridePage.changeFilter('Any Language');

			await languageOverridePage.searchLanguageKey(languageKey2.key);

			await languageOverridePage.assertNoLanguageEntriesWereFound();
		});

		await test.step('Remove the override for the existing language key', async () => {
			await languageOverridePage.searchLanguageKey(existingLanguageKey);

			await languageOverridePage.editLanguageKey(existingLanguageKey);

			await languageOverridePage.assertLanguageKeyTranslationValue(
				'en-US',
				value
			);

			page.once('dialog', async (dialog) => {
				await dialog.accept();
			});

			await page.getByRole('link', {name: 'Clear All Overrides'}).click();
		});

		await test.step('Assert that the existing language key no longer has overrides', async () => {
			await languageOverridePage.goto();

			await languageOverridePage.editLanguageKey(existingLanguageKey);

			await page
				.getByText('Original Value: Analytics Cloud Connection')
				.waitFor();

			await languageOverridePage.assertLanguageKeyTranslationValue(
				'en-US',
				''
			);
		});
	}
);

test('LPD-33373 assert that overriden translations can be filtered', async ({
	languageOverridePage,
}) => {
	await languageOverridePage.goto();

	const languageKey1 = {
		key: getRandomString(),
		translations: [
			{
				languageId: 'en-US',
				value: getRandomString(),
			},
			{
				languageId: 'pt-BR',
				value: getRandomString(),
			},
		],
	};
	const languageKey2: TLanguageKey = {
		key: getRandomString(),
		translations: [
			{
				languageId: 'en-US',
				value: getRandomString(),
			},
		],
	};

	await languageOverridePage.addLanguageKey(languageKey1);

	await languageOverridePage.addLanguageKey(languageKey2);

	await languageOverridePage.changeFilter('Selected Language');

	await languageOverridePage.changeLocale('en-US', 'pt-BR');

	await languageOverridePage.searchLanguageKey(languageKey1.key);

	await languageOverridePage.assertLanguageKeyInListView(languageKey1);

	await languageOverridePage.searchLanguageKey(languageKey2.key);

	await languageOverridePage.assertLanguageKeyNotInListView(languageKey2.key);

	await languageOverridePage.changeFilter('Any Language');

	await languageOverridePage.changeLocale('pt-BR', 'en-US');

	await languageOverridePage.searchLanguageKey(languageKey1.key);

	await languageOverridePage.assertLanguageKeyInListView(languageKey1);

	await languageOverridePage.searchLanguageKey(languageKey2.key);

	await languageOverridePage.assertLanguageKeyInListView(languageKey2);
});

test('LPD-33373 assert that default and overriden translations show up when no filters are applied', async ({
	languageOverridePage,
}) => {
	await languageOverridePage.goto();

	const languageKey: TLanguageKey = {
		key: getRandomString(),
		translations: [
			{
				languageId: 'en-US',
				value: getRandomString(),
			},
			{
				languageId: 'pt-BR',
				value: getRandomString(),
			},
		],
	};

	await languageOverridePage.searchLanguageKey(
		'0-analytics-cloud-connection'
	);

	await languageOverridePage.assertLanguageKeyInListView({
		key: '0-analytics-cloud-connection',
		translations: [],
	});

	await languageOverridePage.addLanguageKey(languageKey);

	await languageOverridePage.searchLanguageKey(languageKey.key);

	await languageOverridePage.assertLanguageKeyInListView(languageKey);
});

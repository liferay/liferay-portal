/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../../../utils/clickAndExpectToBeVisible';
import {waitForEditor} from '../../../../../utils/waitFor';
import {ckeditorSamplePageTest} from '../../fixtures/ckeditorSamplePageTest';
import {inputLocalizedPageTest} from './fixtures/inputLocalizedPageTest';

export const test = mergeTests(
	apiHelpersTest,
	ckeditorSamplePageTest,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	inputLocalizedPageTest,
	isolatedSiteTest,
	loginTest()
);

test.beforeEach(async ({ckeditorSamplePage, page, site}) => {
	await ckeditorSamplePage.createAndGotoSitePage({site});

	await ckeditorSamplePage.selectTab('CKEditor 5');
	await ckeditorSamplePage.selectTab('Input Localized');

	await waitForEditor({page});
});

test(
	'Editor and languages dropdown properly function',
	{tag: '@LPD-54165'},
	async ({inputLocalizedPage}) => {
		await test.step('Check all the editors are displayed', async () => {
			await expect(inputLocalizedPage.content.editor).toBeVisible();
			await expect(
				inputLocalizedPage.content.languageButton
			).toBeVisible();

			await expect(inputLocalizedPage.default.editor).toBeVisible();
			await expect(
				inputLocalizedPage.default.languageButton
			).toBeVisible();

			await expect(inputLocalizedPage.inputOnly.editor).toBeVisible();
		});

		await test.step('Check the initial content is displayed', async () => {
			await test.step('Check that "English" text is displayed', async () => {
				await expect(inputLocalizedPage.content.editor).toHaveText(
					'English'
				);
			});

			await test.step('Switch to es-ES locale', async () => {
				await clickAndExpectToBeVisible({
					autoClick: true,
					target: inputLocalizedPage.spanishOption,
					trigger: inputLocalizedPage.content.languageButton,
				});

				await expect(async () => {
					await expect(
						inputLocalizedPage.content.languageButton
					).toContainText('es-ES');
				}).toPass();
			});

			await test.step('Check that "Spanish" is in the editor', async () => {
				await expect(inputLocalizedPage.content.editor).toHaveText(
					'Spanish'
				);
			});

			await test.step('Switch to en-US locale', async () => {
				await clickAndExpectToBeVisible({
					autoClick: true,
					target: inputLocalizedPage.englishOption,
					trigger: inputLocalizedPage.content.languageButton,
				});

				await expect(async () => {
					await expect(
						inputLocalizedPage.content.languageButton
					).toContainText('en-US');
				}).toPass();
			});
		});

		await test.step('Check that multiple translations can be added', async () => {
			await test.step('Fill default editor with "English"', async () => {
				await inputLocalizedPage.default.editor.fill('English');
			});

			await test.step('Switch to es-ES and fill editor with "Spanish"', async () => {
				await clickAndExpectToBeVisible({
					autoClick: true,
					target: inputLocalizedPage.spanishOption,
					trigger: inputLocalizedPage.default.languageButton,
				});

				await expect(async () => {
					await expect(
						inputLocalizedPage.default.languageButton
					).toContainText('es-ES');
				}).toPass();

				await inputLocalizedPage.default.editor.fill('Spanish');
			});

			await test.step('Switch to en-US and check editor displays "English" still', async () => {
				await clickAndExpectToBeVisible({
					autoClick: true,
					target: inputLocalizedPage.englishOption,
					trigger: inputLocalizedPage.default.languageButton,
				});

				await expect(async () => {
					await expect(
						inputLocalizedPage.default.languageButton
					).toContainText('en-US');
				}).toPass();

				await expect(async () => {
					await expect(inputLocalizedPage.default.editor).toHaveText(
						'English'
					);
				}).toPass();
			});

			await test.step('Switch to es-ES and check editor displays "Spanish" still', async () => {
				await clickAndExpectToBeVisible({
					autoClick: true,
					target: inputLocalizedPage.spanishOption,
					trigger: inputLocalizedPage.default.languageButton,
				});

				await expect(async () => {
					await expect(
						inputLocalizedPage.default.languageButton
					).toContainText('es-ES');
				}).toPass();

				await expect(async () => {
					expect(inputLocalizedPage.default.editor).toHaveText(
						'Spanish'
					);
				}).toPass();
			});

			await test.step('Switch back to english to reset to original state', async () => {
				await clickAndExpectToBeVisible({
					autoClick: true,
					target: inputLocalizedPage.englishOption,
					trigger: inputLocalizedPage.default.languageButton,
				});

				await expect(async () => {
					await expect(
						inputLocalizedPage.default.languageButton
					).toContainText('en-US');
				}).toPass();
			});
		});

		await test.step('Check that the languages dropdown is hidden', async () => {
			await expect(
				inputLocalizedPage.inputOnly.container.getByTitle(
					'Select a Language'
				)
			).toHaveCount(0);
		});

		await test.step('Check the languages dropdown are synced', async () => {
			await test.step('Switch to es-ES locale', async () => {
				await clickAndExpectToBeVisible({
					autoClick: true,
					target: inputLocalizedPage.spanishOption,
					trigger: inputLocalizedPage.default.languageButton,
				});
			});

			await test.step('Check the default and content language buttons are displaying es-ES', async () => {
				await expect(async () => {
					await expect(
						inputLocalizedPage.default.languageButton
					).toContainText('es-ES');
				}).toPass();

				await expect(async () => {
					await expect(
						inputLocalizedPage.content.languageButton
					).toContainText('es-ES');
				}).toPass();
			});

			await test.step('Switch to en-US locale', async () => {
				await clickAndExpectToBeVisible({
					autoClick: true,
					target: inputLocalizedPage.englishOption,
					trigger: inputLocalizedPage.content.languageButton,
				});
			});

			await test.step('Check the default and content language buttons are displaying en-US', async () => {
				await expect(async () => {
					await expect(
						inputLocalizedPage.default.languageButton
					).toContainText('en-US');
				}).toPass();

				await expect(async () => {
					await expect(
						inputLocalizedPage.content.languageButton
					).toContainText('en-US');
				}).toPass();
			});
		});
	}
);

/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {jsComponentsSamplePageTest} from './fixtures/jsComponentsSamplePageTest';
import {translationManagerPageTest} from './fixtures/translationManagerPageTest';

export const test = mergeTests(
	dataApiHelpersTest,
	apiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	jsComponentsSamplePageTest,
	isolatedSiteTest,
	loginTest(),
	translationManagerPageTest
);

test.beforeEach(
	'Setup site and JS Components Sample widget',
	async ({apiHelpers, jsComponentsSamplePage, page, site}) => {
		await test.step('Create a content site and the js components sample widget', async () => {
			await jsComponentsSamplePage.setupJSComponentsSampleWidget({
				apiHelpers,
				site,
			});
		});

		await test.step('Select Translation Manager tab', async () => {
			await jsComponentsSamplePage.selectTab(
				'Translation Manager',
				page.getByRole('heading', {name: 'AUI Tag'})
			);
		});
	}
);

test(
	'Assert dropdown localization functionality',
	{tag: '@LPD-47235'},
	async ({page, translationManagerPage}) => {
		const {
			adminLocalizedInputContainer,
			catalanChoice,
			localizedInput,
			triggerButton,
		} = translationManagerPage;

		await test.step('Assert dropdown UI is rendered correctly', async () => {
			await expect(adminLocalizedInputContainer).toBeVisible();
			await expect(triggerButton).toBeVisible();
		});

		const randomString = getRandomString();

		await test.step('Assert translation updates content', async () => {
			await localizedInput.fill(randomString);

			await clickAndExpectToBeVisible({
				target: catalanChoice,
				trigger: triggerButton,
			});

			await catalanChoice.click();

			const translationText = page.locator(`text=${randomString}`);
			await expect(translationText).toBeVisible();
			await expect(localizedInput).not.toContainText(randomString);
		});

		await test.step('Assert trigger text changes', async () => {
			await expect(triggerButton).toHaveText('ca-ES');
		});
	}
);

test(
	'Assert translation manager dialog navigation',
	{tag: ['@LPD-47235', '@LPD-114956']},
	async ({translationManagerPage}) => {
		const {
			adminTriggerButton,
			cancelButton,
			dialog,
			englishRow,
			englishTriggerButton,
			frenchRow,
			manageButton,
			manageMenuItem,
		} = translationManagerPage;

		await test.step('Assert manage button exists and opens dialog', async () => {
			await clickAndExpectToBeVisible({
				target: manageButton,
				trigger: englishTriggerButton,
			});

			await clickAndExpectToBeVisible({
				target: dialog,
				trigger: manageButton,
			});
		});

		await test.step('Assert the status of each language in the React selector dialog', async () => {
			await clickAndExpectToBeHidden({
				target: dialog,
				trigger: cancelButton,
			});

			await clickAndExpectToBeVisible({
				target: manageMenuItem,
				trigger: adminTriggerButton,
			});

			await clickAndExpectToBeVisible({
				target: dialog,
				trigger: manageMenuItem,
			});

			await expect(englishRow).toContainText('Default');
			await expect(frenchRow).toContainText('Not Translated');
		});
	}
);

test(
	'Assert that clicking on cancel closes the translation manager and discards the changes',
	{tag: ['@LPD-47235', '@LPD-78957']},
	async ({page, translationManagerPage}) => {
		const {
			cancelButton,
			catalanChoice,
			catalanRow,
			dialog,
			englishTriggerButton,
			manageButton,
		} = translationManagerPage;

		await test.step('Click on a translation', async () => {
			await clickAndExpectToBeVisible({
				target: manageButton,
				trigger: englishTriggerButton,
			});
		});

		await test.step('Assert the translation manager is opened', async () => {
			await clickAndExpectToBeVisible({
				target: dialog,
				trigger: manageButton,
			});
		});

		await test.step('Delete a translation', async () => {
			const deleteButton = catalanRow.getByRole('button', {
				name: 'Delete',
			});

			await deleteButton.click();
		});

		await test.step('Click on cancel', async () => {
			await cancelButton.click();

			await expect(dialog).not.toBeVisible();

			await expect(page.locator('body')).not.toHaveClass(/modal-open/);
		});

		await test.step('Assert the translation was not deleted', async () => {
			await clickAndExpectToBeVisible({
				target: catalanChoice,
				trigger: englishTriggerButton,
			});

			await catalanChoice.click();

			await expect(englishTriggerButton).toHaveText('ca-ES');
		});
	}
);

test(
	'Assert that a new language can be added to the translation manager',
	{tag: '@LPD-47235'},
	async ({page, translationManagerPage}) => {
		const {
			addButton,
			dialog,
			doneButton,
			englishTriggerButton,
			germanChoice,
			manageButton,
		} = translationManagerPage;

		await test.step('Click on a translation', async () => {
			await clickAndExpectToBeVisible({
				target: manageButton,
				trigger: englishTriggerButton,
			});
		});

		await test.step('Assert the translation manager is opened', async () => {
			await clickAndExpectToBeVisible({
				target: dialog,
				trigger: manageButton,
			});
		});

		const newLanguageOption = page.getByRole('menuitem', {
			name: 'de-DE',
		});

		test.step('Add a new language', async () => {
			await clickAndExpectToBeVisible({
				target: newLanguageOption,
				trigger: addButton,
			});

			await newLanguageOption.click();
		});

		test.step('Assert the new language is added to the translation manager', async () => {
			await clickAndExpectToBeVisible({
				target: germanChoice,
				trigger: newLanguageOption,
			});

			// await expect(doneButton).toBeVisible();

			clickAndExpectToBeHidden({
				target: dialog,
				trigger: doneButton,
			});
		});

		test.step('Assert the new language is listed as an option', async () => {
			await expect(englishTriggerButton).toBeVisible();

			await clickAndExpectToBeVisible({
				target: germanChoice,
				trigger: englishTriggerButton,
			});
		});
	}
);

test(
	'Assert that languages can be searched in the translation manager',
	{tag: '@LPD-47235'},
	async ({page, translationManagerPage}) => {
		const {
			catalanRow,
			dialog,
			englishTriggerButton,
			manageButton,
			searchInput,
		} = translationManagerPage;

		await test.step('Click on a translation', async () => {
			await clickAndExpectToBeVisible({
				target: manageButton,
				trigger: englishTriggerButton,
			});
		});

		await test.step('Assert the translation manager is opened', async () => {
			await clickAndExpectToBeVisible({
				target: dialog,
				trigger: manageButton,
			});
		});

		await test.step('Search for a language', async () => {
			await searchInput.fill('Catalan');

			await expect(catalanRow).toBeVisible();
		});

		await test.step('Assert languages not matching are hidden', async () => {
			const searchResult = page.getByRole('row', {
				name: 'English (United States)',
			});

			await expect(searchResult).not.toBeVisible();
		});
	}
);

test(
	'Assert that a language can be deleted from the translation manager',
	{tag: ['@LPD-47235', '@LPD-78957']},
	async ({page, translationManagerPage}) => {
		const {
			dialog,
			doneButton,
			englishTriggerButton,
			frenchRow,
			manageButton,
		} = translationManagerPage;

		await test.step('Click on a translation', async () => {
			await clickAndExpectToBeVisible({
				target: manageButton,
				trigger: englishTriggerButton,
			});
		});

		await test.step('Assert the translation manager is opened', async () => {
			await clickAndExpectToBeVisible({
				target: dialog,
				trigger: manageButton,
			});
		});

		await test.step('Delete a language', async () => {
			const deleteButton = frenchRow.getByRole('button', {
				name: 'Delete',
			});

			await clickAndExpectToBeHidden({
				target: frenchRow,
				trigger: deleteButton,
			});
		});

		await test.step('Save and close the translation manager', async () => {
			await doneButton.click();

			await expect(dialog).not.toBeVisible();

			await expect(page.locator('body')).not.toHaveClass(/modal-open/);
		});
	}
);

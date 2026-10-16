/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {DocumentLibraryEditDocumentTypesPage} from '../../../pages/document-library-web/DocumentLibraryEditDocumentTypesPage';
import {WebContentPage} from '../../../pages/journal-web/WebContentPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../../utils/waitForAlert';

const test = mergeTests(dataApiHelpersTest, loginTest());

const CURRENT_TRANSLATION_BUTTON_NAME =
	'Current translation is English, press enter to select another language.';

const CUSTOM_LANGUAGES_LABEL =
	'Define a custom default language and additional active languages for this asset library.';

const DL_ADMIN_NAMESPACE =
	'_com_liferay_document_library_web_portlet_DLAdminPortlet_';

const DEPOT_ADMIN_NAMESPACE =
	'_com_liferay_depot_web_portlet_DepotAdminPortlet_';

const LANGUAGE_IDS = /[a-z]{2}-[A-Z]{2}/g;

async function getTranslationOrder(page: Page, trigger: Locator) {
	const menu = page.locator('.dropdown-menu:visible');

	await clickAndExpectToBeVisible({autoClick: false, target: menu, trigger});

	const languageIds = (await menu.innerText()).match(LANGUAGE_IDS);

	await page.keyboard.press('Escape');

	return languageIds;
}

async function gotoDepotSettings(
	page: Page,
	assetLibraryId: string,
	screenNavigationEntryKey: 'general' | 'languages'
) {
	await page.goto(
		`/group/guest${PORTLET_URLS.depotAdmin}&${DEPOT_ADMIN_NAMESPACE}mvcRenderCommandName=%2Fdepot%2Fedit_depot_entry&${DEPOT_ADMIN_NAMESPACE}depotEntryId=${assetLibraryId}&${DEPOT_ADMIN_NAMESPACE}screenNavigationEntryKey=${screenNavigationEntryKey}`
	);
}

function getLanguageRow(page: Page, language: string) {
	return page.getByRole('row').filter({hasText: language});
}

async function openLanguageMenu(page: Page, language: string) {
	await clickAndExpectToBeVisible({
		target: page.getByRole('menuitem', {name: 'Move Down'}),
		trigger: getLanguageRow(page, language).getByRole('button'),
	});
}

async function runLanguageAction(
	page: Page,
	language: string,
	action: 'Make Default' | 'Move Down' | 'Move Up'
) {
	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByRole('menuitem', {name: action}),
		trigger: getLanguageRow(page, language).getByRole('button'),
	});

	await page.getByRole('button', {exact: true, name: 'Save'}).click();

	await waitForAlert(page);
}

test(
	'The default language of a depot can be changed',
	{tag: '@LPD-109097'},
	async ({apiHelpers, page}) => {
		const assetLibrary =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary();

		await gotoDepotSettings(page, assetLibrary.id, 'languages');

		await page.getByLabel(CUSTOM_LANGUAGES_LABEL).check();

		await expect(
			getLanguageRow(page, 'English (United States)')
		).toContainText('Default');

		await runLanguageAction(page, 'Spanish (Spain)', 'Make Default');

		await gotoDepotSettings(page, assetLibrary.id, 'languages');

		await expect(getLanguageRow(page, 'Spanish (Spain)')).toContainText(
			'Default'
		);
		await expect(
			getLanguageRow(page, 'English (United States)')
		).not.toContainText('Default');

		await gotoDepotSettings(page, assetLibrary.id, 'general');

		await clickAndExpectToBeVisible({
			target: page.getByRole('button', {
				name: /^Translated into Spanish/,
			}),
			trigger: page
				.getByRole('button', {name: CURRENT_TRANSLATION_BUTTON_NAME})
				.first(),
		});
	}
);

test(
	'The custom language order of a depot is used by its localized fields',
	{tag: '@LPD-109097'},
	async ({apiHelpers, page}) => {
		const assetLibrary =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary();

		const webContentTitle = getRandomString();

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId: await getBasicWebContentStructureId(apiHelpers),
			groupId: assetLibrary.siteId,
			titleMap: {en_US: webContentTitle},
		});

		await test.step('Keep four languages and move French down', async () => {
			await gotoDepotSettings(page, assetLibrary.id, 'languages');

			await page.getByLabel(CUSTOM_LANGUAGES_LABEL).check();

			await page.getByRole('button', {exact: true, name: 'Edit'}).click();

			const dialog = page.getByRole('dialog');

			await expect(
				dialog.getByLabel('Select English (United States)')
			).toBeVisible();

			const checkboxLabels = await dialog
				.getByRole('checkbox', {checked: true})
				.evaluateAll((checkboxes) =>
					checkboxes
						.filter(
							(checkbox) =>
								!(checkbox as HTMLInputElement).disabled
						)
						.map((checkbox) => checkbox.getAttribute('aria-label'))
				);

			for (const checkboxLabel of checkboxLabels) {
				await dialog
					.getByLabel(checkboxLabel!, {exact: true})
					.uncheck();
			}

			for (const language of [
				'French (France)',
				'Japanese (Japan)',
				'Spanish (Spain)',
			]) {
				await dialog.getByLabel(`Select ${language}`).check();
			}

			await dialog.getByRole('button', {name: 'Done'}).click();

			await openLanguageMenu(page, 'English (United States)');

			await expect(
				page.getByRole('menuitem', {name: 'Move Up'})
			).toHaveCount(0);

			await page.keyboard.press('Escape');

			await runLanguageAction(page, 'French (France)', 'Move Down');

			await expect(page.getByRole('row')).toHaveText([
				/Active Language/,
				/English \(United States\)/,
				/Japanese \(Japan\)/,
				/French \(France\)/,
				/Spanish \(Spain\)/,
			]);
		});

		const expectedOrder = ['en-US', 'ja-JP', 'fr-FR', 'es-ES'];

		await test.step('The depot name and description follow the order', async () => {
			await gotoDepotSettings(page, assetLibrary.id, 'general');

			const triggers = page.getByRole('button', {
				name: CURRENT_TRANSLATION_BUTTON_NAME,
			});

			expect(await getTranslationOrder(page, triggers.nth(0))).toEqual(
				expectedOrder
			);
			expect(await getTranslationOrder(page, triggers.nth(1))).toEqual(
				expectedOrder
			);
		});

		const depotURL = `/asset-library-${assetLibrary.id}`;

		await test.step('A new depot document type follows the order', async () => {
			await new DocumentLibraryEditDocumentTypesPage(page).goto(depotURL);

			expect(
				await getTranslationOrder(
					page,
					page
						.getByRole('button', {
							name: CURRENT_TRANSLATION_BUTTON_NAME,
						})
						.first()
				)
			).toEqual(expectedOrder);
		});

		await test.step('A new depot metadata set follows the order', async () => {
			await page.goto(
				`/group${depotURL}/~/control_panel/manage?p_p_id=com_liferay_document_library_web_portlet_DLAdminPortlet&${DL_ADMIN_NAMESPACE}mvcRenderCommandName=%2Fdocument_library%2Fedit_ddm_structure&${DL_ADMIN_NAMESPACE}groupId=${assetLibrary.siteId}`
			);

			expect(
				await getTranslationOrder(
					page,
					page
						.getByRole('button', {
							name: CURRENT_TRANSLATION_BUTTON_NAME,
						})
						.first()
				)
			).toEqual(expectedOrder);
		});

		await test.step('A depot web content follows the order', async () => {
			await new WebContentPage(page).goto(depotURL);

			await page
				.getByRole('link', {name: webContentTitle})
				.first()
				.click();

			expect(
				await getTranslationOrder(
					page,
					page.getByRole('combobox', {name: 'Select a language'})
				)
			).toEqual(expectedOrder);
		});
	}
);

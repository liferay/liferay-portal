/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {accountSettingsPagesTest} from '../../../fixtures/accountSettingsPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitchViaApi, userData} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import getDataStructureDefinition from '../../journal-web/main/utils/getDataStructureDefinition';
import {assetCategoriesPagesTest} from './fixtures/assetCategoriesAdminPagesTest';

const test = mergeTests(
	accountSettingsPagesTest,
	assetCategoriesPagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	pagesAdminPagesTest
);

function collectAlerts(page: Page) {
	const alerts: string[] = [];

	page.on('dialog', async (dialog) => {
		if (dialog.type() === 'alert') {
			alerts.push(dialog.message());

			await dialog.dismiss();
		}
	});

	return alerts;
}

async function selectCategory(
	page: Page,
	vocabularyName: string,
	categoryName: string
) {
	await page.getByRole('button', {name: `Select ${vocabularyName}`}).click();

	const selectCategoryModal = page.getByRole('dialog', {
		name: `Select ${vocabularyName}`,
	});

	await selectCategoryModal
		.getByRole('treeitem', {name: categoryName})
		.getByRole('checkbox')
		.check();

	await selectCategoryModal.getByRole('button', {name: 'Done'}).click();
}

test(
	'Add a vocabulary in a site with XSS names in the site, structure and document type',
	{tag: '@LPD-109100'},
	async ({
		apiHelpers,
		assetCategoriesAdminPage,
		page,
		vocabulariesEditPage,
	}) => {
		const xssSite = await apiHelpers.headlessAdminSite.postSite({
			name: `<script>alert(${getRandomString()})</script>`,
		});

		await apiHelpers.headlessDelivery.postSiteDocumentDataDefinitionType(
			xssSite.id,
			`<script>alert(${getRandomString()})</script>`
		);

		await apiHelpers.dataEngine.createStructure(
			xssSite.id,
			getDataStructureDefinition({
				defaultLanguageId: 'en_US',
				fields: [{fieldType: 'text', name: 'Text'}],
				name: `<script>alert(${getRandomString()})</script>`,
			})
		);

		const alerts = collectAlerts(page);

		await assetCategoriesAdminPage.goto(xssSite.friendlyUrlPath);

		await assetCategoriesAdminPage.newVocabularyButton.click();

		const vocabularyName = getRandomString();

		await vocabulariesEditPage.add({
			assetTypes: ['Web Content Article'],
			name: vocabularyName,
		});

		await expect(
			page.getByRole('heading', {name: vocabularyName})
		).toBeVisible();

		expect(alerts).toEqual([]);
	}
);

test(
	'Select a category with an XSS name in the account settings and in the page configuration',
	{tag: '@LPD-109100'},
	async ({accountSettingsPage, apiHelpers, page, pagesAdminPage, site}) => {
		const categoryName = `><img src=x onerror=alert(${Math.floor(Math.random() * 1000)})>`;
		const vocabularyName = getRandomString();

		// The account settings only show the vocabularies of the Global site

		const company =
			await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
				'liferay.com'
			);

		const companyGroup =
			await apiHelpers.jsonWebServicesGroup.getCompanyGroup(
				company.companyId
			);

		const vocabulary =
			await apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary({
				name: vocabularyName,
				siteId: companyGroup.groupId,
			});

		const category =
			await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
				{name: categoryName, vocabularyId: vocabulary.id}
			);

		// Any user sees the vocabulary in the account settings

		await apiHelpers.headlessAdminTaxonomy.putTaxonomyVocabulariesTaxonomyVocabularyPermissions(
			vocabulary.id,
			{actionIds: ['VIEW'], roleName: 'User'}
		);

		await apiHelpers.headlessAdminTaxonomy.putTaxonomyCategoriesTaxonomyCategoryPermissions(
			category.id,
			{actionIds: ['VIEW'], roleName: 'User'}
		);

		const alerts = collectAlerts(page);

		await test.step('Select the category in the page configuration', async () => {
			const layoutTitle = getRandomString();

			await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				title: layoutTitle,
			});

			await pagesAdminPage.goto(site.friendlyUrlPath);

			await pagesAdminPage.clickOnAction('Configure', layoutTitle);

			await selectCategory(page, vocabularyName, categoryName);

			await page.getByRole('button', {exact: true, name: 'Save'}).click();

			await waitForAlert(
				page,
				'Success:The page was updated successfully.'
			);

			await page.reload();

			await expect(page.getByText(categoryName)).toBeVisible();
		});

		expect(alerts).toEqual([]);

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await performUserSwitchViaApi(page, user.alternateName);

		await test.step('Select the category in the account settings', async () => {
			await accountSettingsPage.goToAccountSettings();

			await selectCategory(page, vocabularyName, categoryName);

			await accountSettingsPage.saveButton.click();

			await waitForAlert(page);

			await page.reload();

			await expect(page.getByText(categoryName)).toBeVisible();
		});

		expect(alerts).toEqual([]);
	}
);

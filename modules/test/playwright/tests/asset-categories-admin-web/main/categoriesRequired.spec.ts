/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {siteSettingsPagesTest} from '../../../fixtures/siteSettingsPagesTest';
import {createCategories} from '../../../helpers/CreateCategories';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import {sitesAdminPagesTest} from '../../site-admin-web/main/fixtures/sitesAdminPagesTest';
import {assetCategoriesPagesTest} from './fixtures/assetCategoriesAdminPagesTest';

const test = mergeTests(
	assetCategoriesPagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	journalPagesTest,
	loginTest(),
	pagesAdminPagesTest,
	siteSettingsPagesTest,
	sitesAdminPagesTest
);

const widgetPagesTest = mergeTests(
	test,
	featureFlagsTest({'LPD-76864': {enabled: true}})
);

widgetPagesTest(
	'Select required categories when adding content and widget pages',
	{tag: '@LPD-109100'},
	async ({apiHelpers, page, pagesAdminPage, site}) => {
		const vocabularyName = getRandomString();

		const categories = await createCategories({
			apiHelpers,
			assetTypes: [{required: true, type: 'WebPage'}],
			categoryNames: [
				{name: getRandomString()},
				{name: getRandomString()},
				{name: getRandomString()},
			],
			siteId: site.id,
			vocabularyName,
		});

		const addPage = async (template: string, categoryNames: string[]) => {
			const pageName = getRandomString();

			await pagesAdminPage.goto(site.friendlyUrlPath);

			await pagesAdminPage.clickNewButtonAndWaitForBlankTemplate();

			await clickAndExpectToBeVisible({
				target: pagesAdminPage.addPageModal,
				timeout: 5000,
				trigger: page
					.locator('.card-page-item')
					.filter({hasText: template}),
			});

			const addPageIFrame = page.frameLocator(
				'iframe[id="addLayoutDialog_iframe_"]'
			);

			await addPageIFrame
				.getByRole('textbox', {name: 'Name Required'})
				.fill(pageName);

			// The page cannot be added without a category of the vocabulary

			await addPageIFrame
				.getByRole('button', {exact: true, name: 'Add'})
				.click();

			await expect(
				addPageIFrame.getByText(
					`Please select at least one category for ${vocabularyName}.`
				)
			).toBeVisible();

			await addPageIFrame
				.getByRole('button', {name: `Select ${vocabularyName}`})
				.click();

			const selectCategoryModal = addPageIFrame.getByRole('dialog', {
				name: `Select ${vocabularyName}`,
			});

			for (const categoryName of categoryNames) {
				await selectCategoryModal
					.getByRole('treeitem', {name: categoryName})
					.getByRole('checkbox')
					.check();
			}

			await selectCategoryModal
				.getByRole('button', {name: 'Done'})
				.click();

			await addPageIFrame
				.getByRole('button', {exact: true, name: 'Add'})
				.click();

			await waitForAlert(page, 'page was created successfully.');

			await pagesAdminPage.goto(site.friendlyUrlPath);

			await expect(
				page.getByRole('link', {exact: true, name: pageName})
			).toBeVisible();
		};

		await widgetPagesTest.step(
			'Add a content page with three categories',
			async () => {
				await addPage(
					'Blank',
					categories.map(({name}) => name)
				);
			}
		);

		await widgetPagesTest.step(
			'Add a widget page with one category',
			async () => {
				await addPage('Widget Page', [categories[0].name]);
			}
		);
	}
);

test(
	'View an error when saving the site settings without a required category',
	{tag: '@LPD-109100'},
	async ({apiHelpers, page, site, siteSettingsPage}) => {
		const vocabularyName = getRandomString();

		await createCategories({
			apiHelpers,
			assetTypes: [{required: true, type: 'WebSite'}],
			categoryNames: [{name: getRandomString()}],
			siteId: site.id,
			vocabularyName,
		});

		await siteSettingsPage.goToSiteSetting(
			'Assets',
			'Categorization',
			site.friendlyUrlPath
		);

		await siteSettingsPage.saveButton.click();

		await expect(
			page.getByText(
				`Please select at least one category for ${vocabularyName}.`
			)
		).toBeVisible();
	}
);

test(
	'Select a required category of the parent site when adding a child site',
	{tag: '@LPD-109100'},
	async ({apiHelpers, page, site, sitesAdminPage}) => {
		const vocabularyName = getRandomString();

		const [category] = await createCategories({
			apiHelpers,
			assetTypes: [{required: true, type: 'WebSite'}],
			categoryNames: [{name: getRandomString()}],
			siteId: site.id,
			vocabularyName,
		});

		const childSiteName = getRandomString();

		try {

			// Add a child site without the required category

			await sitesAdminPage.goto();

			await sitesAdminPage.searchSite(site.name);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {name: 'Add Child Site'}),
				trigger: page
					.getByRole('row', {name: site.name})
					.getByLabel('Show Actions'),
			});

			await page
				.getByRole('button', {name: 'Select Template: Blank Site'})
				.click();

			const addSiteFrame = page.frameLocator('iframe[title="Add Site"]');

			await addSiteFrame.getByLabel('Name').fill(childSiteName);

			await addSiteFrame.getByRole('button', {name: 'Add'}).click();

			await expect(
				addSiteFrame.getByText(
					`Please select at least one category for ${vocabularyName}.`
				)
			).toBeVisible();

			// Select the required category and add the child site

			await addSiteFrame
				.getByRole('button', {name: `Select ${vocabularyName}`})
				.click();

			const selectCategoryModal = addSiteFrame.getByRole('dialog', {
				name: `Select ${vocabularyName}`,
			});

			await selectCategoryModal
				.getByRole('treeitem', {name: category.name})
				.getByRole('checkbox')
				.check();

			await selectCategoryModal
				.getByRole('button', {name: 'Done'})
				.click();

			await addSiteFrame.getByRole('button', {name: 'Add'}).click();

			await waitForAlert(page, 'Success: Site was successfully added.');
		}
		finally {
			const company =
				await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
					'liferay.com'
				);

			const childSite = await apiHelpers.jsonWebServicesGroup
				.getGroupByKey(company.companyId, childSiteName)
				.catch(() => null);

			if (childSite?.externalReferenceCode) {
				await apiHelpers.headlessAdminSite.deleteSite(
					childSite.externalReferenceCode
				);
			}
		}
	}
);

test(
	'A vocabulary required for web contents is only required in the web content editor',
	{tag: '@LPD-109100'},
	async ({
		apiHelpers,
		assetCategoriesAdminPage,
		journalEditArticlePage,
		page,
		pagesAdminPage,
		site,
		vocabulariesEditPage,
	}) => {
		const vocabularyName = getRandomString();

		await test.step('Add a vocabulary for blogs and required for web contents', async () => {
			await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

			await assetCategoriesAdminPage.newVocabularyButton.click();

			await vocabulariesEditPage.fillName(vocabularyName);

			await expect(async () => {
				if (await page.getByLabel('Asset Types').isHidden()) {
					await vocabulariesEditPage.expandPanel(
						'Associated Asset Types'
					);
				}

				await vocabulariesEditPage.addAssociatedAssetType(
					'Blogs Entry',
					0
				);
			}).toPass();

			await vocabulariesEditPage.addRowButton.first().click();

			await vocabulariesEditPage.addAssociatedAssetType(
				'Web Content Article',
				1
			);

			await vocabulariesEditPage.requiredToggle.nth(1).click();

			page.once('dialog', (dialog) => dialog.accept());

			await vocabulariesEditPage.saveButton.click();

			await waitForAlert(page);

			await expect(
				page.getByText('Blogs Entry, Web Content Article *(Required)')
			).toBeVisible();
		});

		const [vocabulary] = (
			await apiHelpers.headlessAdminTaxonomy.getTaxonomyVocabularyBySiteId(
				site.id
			)
		).items.filter(({name}: {name: string}) => name === vocabularyName);

		const categoryName = getRandomString();

		await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
			{name: categoryName, vocabularyId: vocabulary.id}
		);

		await test.step('Add a web content with the required category', async () => {
			const title = getRandomString();

			await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

			await journalEditArticlePage.fillTitle(title);

			await journalEditArticlePage.publishArticle();

			await expect(
				page.getByText(
					`Please select at least one category for ${vocabularyName}.`
				)
			).toBeVisible();

			await journalEditArticlePage.selectCategories(vocabularyName, [
				categoryName,
			]);

			await journalEditArticlePage.publishArticle();

			await journalEditArticlePage.editArticle(title);

			await journalEditArticlePage.openFieldSet(
				'Categorization',
				'categorization'
			);

			await expect(
				page
					.getByRole('group', {name: 'Categorization'})
					.getByText(categoryName, {exact: true})
			).toBeVisible();
		});

		await test.step('Do not view the vocabulary in the page configuration', async () => {

			// A vocabulary for all asset types proves the page configuration
			// shows the categories selectors

			const allAssetTypesVocabularyName = getRandomString();

			await createCategories({
				apiHelpers,
				categoryNames: [{name: getRandomString()}],
				siteId: site.id,
				vocabularyName: allAssetTypesVocabularyName,
			});

			const layoutTitle = getRandomString();

			await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				title: layoutTitle,
			});

			await pagesAdminPage.goto(site.friendlyUrlPath);

			await pagesAdminPage.clickOnAction('Configure', layoutTitle);

			await expect(
				page.getByRole('button', {
					name: `Select ${allAssetTypesVocabularyName}`,
				})
			).toBeVisible();

			await expect(
				page.getByRole('button', {name: `Select ${vocabularyName}`})
			).toHaveCount(0);
		});
	}
);

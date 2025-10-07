/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {depotAdminPageTest} from '../../../fixtures/depotAdminPageTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageSelectorPagesTest} from '../../../fixtures/pageSelectorPagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {createCategories} from '../../../helpers/CreateCategories';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {pagesPagesTest} from '../../layout-admin-web/main/fixtures/pagesPagesTest';
import {navigationMenusPagesTest} from './fixtures/navigationMenusPagesTest';

const test = mergeTests(
	apiHelpersTest,
	depotAdminPageTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	navigationMenusPagesTest,
	pagesAdminPagesTest,
	pagesPagesTest,
	pageSelectorPagesTest
);

test(
	'Drag and drop navigation menu item allows for non-nested placement',
	{
		tag: '@LPS-125802',
	},
	async ({apiHelpers, navigationMenusPage, page, site}) => {
		for (let i = 1; i <= 3; i++) {
			const parentPage = await apiHelpers.headlessDelivery.createSitePage(
				{
					siteId: site.id,
					title: `Parent ${i}`,
				}
			);

			await apiHelpers.headlessDelivery.createSitePage({
				parentSitePage: {
					friendlyUrlPath: parentPage.friendlyUrlPath,
				},
				siteId: site.id,
				title: `Child ${i}`,
			});
		}

		await navigationMenusPage.goto(site.friendlyUrlPath);

		await navigationMenusPage.createNavigationMenu(getRandomString());

		await navigationMenusPage.addPageItem([
			'Parent 1',
			'Parent 2',
			'Parent 3',
			'Child 1',
			'Child 2',
			'Child 3',
		]);

		const source = page.getByRole('button', {name: 'Move Parent 3'});
		const target = page
			.locator('.site_navigation_menu_editor_MenuItem')
			.nth(1);

		const targetRect = await target.evaluate((element) =>
			element.getBoundingClientRect()
		);

		await source.hover();
		await page.mouse.down();
		await page.mouse.move(targetRect.x, targetRect.y + 1);
		await page.mouse.up();

		await page.waitForTimeout(300);

		const cardTitles = await page.locator('.card-title').allTextContents();

		await expect(cardTitles[2]).toBe('Parent 3');
		await expect(cardTitles[3]).toBe('Child 3');
	}
);

test.describe('Add pages to Navigation Menu', () => {
	test('Load more works properly in search results', async ({
		apiHelpers,
		navigationMenusPage,
		pageSelectorPage,
		site,
	}) => {

		// Create 15 Lemon pages

		for (let i = 1; i <= 15; i++) {
			await apiHelpers.headlessDelivery.createSitePage({
				siteId: site.id,
				title: `Lemon ${i}`,
			});
		}

		// Create 30 Apple pages

		for (let i = 1; i <= 30; i++) {
			await apiHelpers.headlessDelivery.createSitePage({
				siteId: site.id,
				title: `Apple ${i}`,
			});
		}

		// Create a navigation menu and open pages selector

		await navigationMenusPage.goto(site.friendlyUrlPath);

		await navigationMenusPage.createNavigationMenu(getRandomString());

		await navigationMenusPage.openAddPageModal();

		// Store modal instance in variable so we can search for things inside it

		const modal = await pageSelectorPage.getModal();

		// Search for another string and check empty state

		await pageSelectorPage.search('Orange');

		await expect(modal.getByText('No Results Found')).toBeVisible();

		// Search for Lemon pages, check it shows all results and does not show Load More button

		await pageSelectorPage.search('Lem');

		await expect(modal.locator('.search-result')).toHaveCount(15);

		await expect(modal.getByText('Load More Results')).not.toBeVisible();

		// Check only Lem substring is marked

		const firstResult = modal.locator('.search-result').first();

		await expect(firstResult.locator('mark')).toHaveText('Lem');

		// Search for Apple pages, check it initially shows 20 items

		await pageSelectorPage.search('App');

		await expect(modal.locator('.search-result')).toHaveCount(20);

		// Load more items and check it loads all results and button disappears

		await pageSelectorPage.loadMore();

		await expect(modal.locator('.search-result')).toHaveCount(30);

		await expect(modal.getByText('Load More Results')).not.toBeVisible();
	});

	test('Checks the correct label for restricted page in the layout tree', async ({
		apiHelpers,
		navigationMenusPage,
		pageSelectorPage,
		site,
	}) => {

		// Create a page with only one permission

		const pageName = getRandomString();

		await apiHelpers.headlessDelivery.createSitePage({
			pagePermissions: [
				{
					actionKeys: ['VIEW'],
					roleKey: 'Owner',
				},
			],
			siteId: site.id,
			title: pageName,
		});

		// Create a navigation menu and open pages selector

		await navigationMenusPage.goto(site.friendlyUrlPath);

		await navigationMenusPage.createNavigationMenu(getRandomString());

		await navigationMenusPage.openAddPageModal();

		const modal = await pageSelectorPage.getModal();

		// Check the correct label for restricted page

		await expect(
			modal
				.locator('div', {
					hasText: pageName,
				})
				.getByLabel('Restricted Page')
		).toBeVisible();
	});
});

test(
	'User can provide translations for Navigation Menu items',
	{
		tag: '@LPS-85566',
	},
	async ({apiHelpers, navigationMenusPage, page, site}) => {
		const pageName = getRandomString();

		await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: pageName,
		});

		await navigationMenusPage.goto(site.friendlyUrlPath);

		const navigationMenuName = getRandomString();

		await navigationMenusPage.createNavigationMenu(navigationMenuName);

		await navigationMenusPage.addPageItem([pageName]);

		const submenuItemName = getRandomString();

		await navigationMenusPage.addSubmenuItem(submenuItemName);

		const urlName = getRandomString();

		await navigationMenusPage.addURLItem(urlName);

		await navigationMenusPage.translateName(pageName, true);
		await navigationMenusPage.translateName(submenuItemName);
		await navigationMenusPage.translateName(urlName);

		await page.goto(
			`/es/group${site.friendlyUrlPath}${PORTLET_URLS.navigationMenus}`
		);

		await page.getByText(navigationMenuName).click();

		await expect(page.getByText(`${pageName} Spanish`)).toBeVisible();

		await page.getByText(`${pageName} Spanish`).click();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.locator("a[data-languageId='es_ES']"),
			trigger: page.getByText('en-US', {exact: true}),
		});

		await expect(
			page.locator(
				'input[id="_com_liferay_site_navigation_admin_web_portlet_SiteNavigationAdminPortlet_name"]'
			)
		).toHaveValue(`${pageName} Spanish`);

		await page.goto('/en');
	}
);

test(
	'Navigation Menu item is prepopulated with existing translation',
	{
		tag: '@LPS-85566',
	},
	async ({
		apiHelpers,
		navigationMenusPage,
		page,
		pageConfigurationPage,
		pagesAdminPage,
		site,
	}) => {
		const pageName = getRandomString();

		await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: pageName,
		});

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pageConfigurationPage.goToSection(pageName, 'General');

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				name: 'Not translated into Spanish.',
			}),
			trigger: page.locator(
				'[id="_com_liferay_layout_admin_web_portlet_GroupPagesPortlet__com_liferay_layout_admin_web_portlet_GroupPagesPortlet_nameMapAsXMLMenu"]'
			),
		});

		await page.getByLabel('Name').fill(`${pageName} Spanish`);

		await page.getByRole('button', {name: 'Save'}).click();

		await navigationMenusPage.goto(site.friendlyUrlPath);

		const navigationMenuName = getRandomString();

		await navigationMenusPage.createNavigationMenu(navigationMenuName);

		await navigationMenusPage.addPageItem([pageName]);

		await page.getByText(pageName).click();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.locator("a[data-languageId='es_ES']"),
			trigger: page.getByText('en-US', {exact: true}),
		});

		await expect(
			page.locator(
				'input[id="_com_liferay_site_navigation_admin_web_portlet_SiteNavigationAdminPortlet_name"]'
			)
		).toHaveValue(`${pageName} Spanish`);
	}
);

test(
	'Add child Navigation Menu item',
	{
		tag: '@LPD-58226',
	},
	async ({apiHelpers, navigationMenusPage, site}) => {
		await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: getRandomString(),
		});

		await navigationMenusPage.goto(site.friendlyUrlPath);

		await navigationMenusPage.createNavigationMenu(getRandomString());

		const submenuName = getRandomString();

		await navigationMenusPage.addSubmenuItem(submenuName);

		const urlItemName = getRandomString();

		await navigationMenusPage.addURLItem(urlItemName, submenuName);

		const nestingLevel =
			await navigationMenusPage.getNestingLevel(urlItemName);

		expect(nestingLevel).toContain('2');
	}
);

test(
	'Add Global or Asset Library Vocabulary Navigation Menu item',
	{
		tag: '@LPD-58226',
	},
	async ({apiHelpers, depotAdminPage, navigationMenusPage, page, site}) => {
		await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: getRandomString(),
		});

		// Add Asset Library

		const depotName = getRandomString();

		const depot =
			await apiHelpers.jsonWebServicesDepot.addDepotEntry(depotName);

		await depotAdminPage.goto();

		await page.getByRole('link', {name: depotName}).click();

		await page.getByRole('link', {name: 'Asset Library Settings'}).click();

		await page.getByRole('link', {name: 'Sites'}).click();

		await page.getByRole('button', {name: 'Add'}).click();

		await page
			.frameLocator('iframe[title="Select Site"]')
			.getByRole('link', {name: 'All Sites'})
			.click();

		await page
			.frameLocator('iframe[title="Select Site"]')
			.getByRole('link', {exact: true, name: site.name})
			.click();

		await page.goBack();

		// Add Vocabulary to Asset Library

		await page.getByRole('link', {name: depotName}).click();

		await page.getByRole('link', {name: 'Categories'}).click();

		await page.getByLabel('Add New Vocabulary').click();

		const vocabularyName = getRandomString();

		await page.getByPlaceholder('Name').fill(vocabularyName);

		page.once('dialog', async (dialog) => {
			await dialog.accept();
		});

		await page.getByRole('button', {name: 'Save'}).click();

		// Create Vocabulary Navigation Menu item

		await navigationMenusPage.goto(site.friendlyUrlPath);

		await navigationMenusPage.createNavigationMenu(getRandomString());

		await navigationMenusPage.openAddVocabularyModal();

		await navigationMenusPage.vocabulariesModal
			.getByLabel(vocabularyName)
			.check();

		await navigationMenusPage.selectButton.click();

		// Assert that the Vocabulary Navigation Menu item was successfully created

		await expect(page.getByText(vocabularyName)).toBeVisible();

		await apiHelpers.jsonWebServicesDepot.deleteDepotEntry(
			depot.depotEntryId
		);
	}
);

test(
	'Add Page to Navigation Menu and view Page status',
	{
		tag: '@LPD-58226',
	},
	async ({apiHelpers, navigationMenusPage, page, site}) => {
		const pageName = getRandomString();

		await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: pageName,
		});

		await navigationMenusPage.goto(site.friendlyUrlPath);

		await navigationMenusPage.createNavigationMenu(getRandomString());

		await navigationMenusPage.addPageItem([pageName]);

		await expect(
			page
				.getByLabel('Open ' + pageName + ' (Page)')
				.locator('span')
				.first()
		).toBeVisible();
	}
);

test(
	'Add sibling Navigation Menu items',
	{
		tag: '@LPD-58226',
	},
	async ({apiHelpers, navigationMenusPage, page, site}) => {
		await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: getRandomString(),
		});

		await navigationMenusPage.goto(site.friendlyUrlPath);

		await navigationMenusPage.createNavigationMenu(getRandomString());

		const submenuName = getRandomString();

		await navigationMenusPage.addSubmenuItem(submenuName);

		const submenuItem = page.getByLabel(
			'Open ' + submenuName + ' (Submenu)'
		);

		await submenuItem.hover();

		await expect(submenuItem.getByRole('button').nth(1)).toBeVisible();

		await submenuItem.getByRole('button').nth(1).hover();

		await expect(
			page.getByText('Add item before ' + submenuName)
		).toBeVisible();

		await submenuItem.hover();

		await expect(submenuItem.getByRole('button').nth(2)).toBeVisible();

		await submenuItem.getByRole('button').nth(2).hover();

		await expect(
			page.getByText('Add item after ' + submenuName)
		).toBeVisible();

		await submenuItem.getByRole('button').nth(2).click();

		await page.getByRole('menuitem', {name: 'URL'}).click();

		const urlItemName1 = getRandomString();

		const urlItemName2 = getRandomString();

		await page.waitForTimeout(1000);

		await navigationMenusPage.urlModal
			.getByPlaceholder('Name')
			.fill(urlItemName1);

		await navigationMenusPage.urlModal
			.getByPlaceholder('http://')
			.fill('www.liferay.com');

		await page.waitForTimeout(300);

		await navigationMenusPage.urlModal
			.getByRole('button', {name: 'Add'})
			.click();

		await expect(page.getByText(urlItemName1)).toBeVisible();

		let nestingLevel =
			await navigationMenusPage.getNestingLevel(urlItemName1);

		expect(nestingLevel).toContain('1');

		await submenuItem.hover();

		await submenuItem.getByRole('button').nth(1).click();

		await page.getByRole('menuitem', {name: 'URL'}).click();

		await page.waitForTimeout(1000);

		await navigationMenusPage.urlModal
			.getByPlaceholder('Name')
			.fill(urlItemName2);

		await navigationMenusPage.urlModal
			.getByPlaceholder('http://')
			.fill('www.liferay.com');

		await page.waitForTimeout(300);

		await navigationMenusPage.urlModal
			.getByRole('button', {name: 'Add'})
			.click();

		await expect(page.getByText(urlItemName2)).toBeVisible();

		nestingLevel = await navigationMenusPage.getNestingLevel(urlItemName2);

		expect(nestingLevel).toContain('1');
	}
);

test(
	'Rename Page type Navigation Menu Item',
	{
		tag: '@LPD-58226',
	},
	async ({apiHelpers, navigationMenusPage, page, site}) => {
		const pageName1 = getRandomString();

		const pageName2 = getRandomString();

		await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: pageName1,
		});

		await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: pageName2,
		});

		await navigationMenusPage.goto(site.friendlyUrlPath);

		await navigationMenusPage.createNavigationMenu(getRandomString());

		await navigationMenusPage.addPageItem([pageName1, pageName2]);

		await page.getByText(pageName1).click();

		await page.getByLabel('Use Custom Name').check();

		const pageName3 = getRandomString();

		await page.getByPlaceholder('Name').fill(pageName3);

		await page.getByRole('button', {name: 'Save'}).click();

		await expect(
			page.getByLabel('Open ' + pageName3 + ' (Page)')
		).toBeVisible();
	}
);

test(
	'Toggle automatically add new Pages to Navigation Menu',
	{
		tag: '@LPD-58226',
	},
	async ({navigationMenusPage, page, pagesAdminPage, site}) => {
		await navigationMenusPage.goto(site.friendlyUrlPath);

		const navigationMenuName1 = getRandomString();

		const navigationMenuName2 = getRandomString();

		await navigationMenusPage.createNavigationMenu(navigationMenuName1);

		await navigationMenusPage.goto(site.friendlyUrlPath);

		await navigationMenusPage.createNavigationMenu(navigationMenuName2);

		await pagesAdminPage.goto(site.friendlyUrlPath);

		const addPageButton = page.getByText('New', {exact: true});

		await page.waitForTimeout(300);

		await addPageButton.click();

		await page.getByRole('button', {name: 'Blank'}).click();

		const pageName = getRandomString();

		const pageIFrame = page.frameLocator('iframe[title="Add Page"]');

		await pageIFrame.getByPlaceholder('Add Page Name').fill(pageName);

		await pageIFrame.getByLabel(navigationMenuName1).check();

		await pageIFrame.getByLabel(navigationMenuName2).check();

		await pageIFrame.getByRole('button', {name: 'Add'}).click();

		await page.getByLabel('Publish').click();

		await navigationMenusPage.goto(site.friendlyUrlPath);

		await page.getByRole('link', {name: navigationMenuName1}).click();

		await expect(page.getByText(pageName)).toBeVisible();

		await navigationMenusPage.goto(site.friendlyUrlPath);

		await page.getByRole('link', {name: navigationMenuName2}).click();

		await expect(page.getByText(pageName)).toBeVisible();

		await page.getByLabel('Open Configuration Panel').click();

		await page.getByLabel('When creating a new page').uncheck();

		await page.getByRole('button', {name: 'Save'}).click();

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await page.waitForTimeout(300);

		await addPageButton.click();

		await page.getByRole('button', {name: 'Blank'}).click();

		await expect(
			pageIFrame.getByLabel('Add This Page to ' + navigationMenuName1)
		).toBeVisible();

		await expect(
			pageIFrame.getByLabel('Add This Page to ' + navigationMenuName2)
		).not.toBeVisible();
	}
);

test(
	'View Category added to nearest ancestor',
	{
		tag: '@LPD-58226',
	},
	async ({apiHelpers, navigationMenusPage, page, site}) => {

		// Create Categories and Sub Categories

		const categoryName = getRandomString();

		const vocabularyName = getRandomString();

		const category: Array<any> = await createCategories({
			apiHelpers,
			categoryNames: [{name: categoryName}],
			siteId: site.id,
			vocabularyName,
		});

		const subCategoryName = getRandomString();

		const subCategory =
			await apiHelpers.headlessAdminTaxonomy.postTaxonomyCategoryTaxonomyCategory(
				{
					name: subCategoryName,
					parentTaxonomyCategoryId: category[0].id,
				}
			);

		const subSubCategoryName = getRandomString();

		await apiHelpers.headlessAdminTaxonomy.postTaxonomyCategoryTaxonomyCategory(
			{
				name: subSubCategoryName,
				parentTaxonomyCategoryId: subCategory.id,
			}
		);

		// Create Navigation Menu and try to select the previously created Categories using the keyboard and mouse

		await navigationMenusPage.goto(site.friendlyUrlPath);

		await navigationMenusPage.createNavigationMenu(getRandomString());

		await navigationMenusPage.openAddCategoryModal();

		for (let index = 0; index < 3; index++) {
			await navigationMenusPage.categoriesModal
				.getByRole('button')
				.nth(index)
				.click();
		}

		for (let index = 0; index < 3; index++) {
			await navigationMenusPage.categoriesModal
				.getByRole('checkbox')
				.nth(index)
				.check();
		}

		await page.getByRole('button', {name: 'Select'}).click();

		let nestingLevel =
			await navigationMenusPage.getNestingLevel(categoryName);

		expect(nestingLevel).toContain('1');

		nestingLevel =
			await navigationMenusPage.getNestingLevel(subCategoryName);

		expect(nestingLevel).toContain('2');

		nestingLevel =
			await navigationMenusPage.getNestingLevel(subSubCategoryName);

		expect(nestingLevel).toContain('3');
	}
);

test(
	'View modal when delete item with children',
	{
		tag: '@LPD-58226',
	},
	async ({apiHelpers, navigationMenusPage, page, site}) => {
		const categoryName1 = getRandomString();

		const categoryName2 = getRandomString();

		const vocabularyName = getRandomString();

		const category: Array<any> = await createCategories({
			apiHelpers,
			categoryNames: [{name: categoryName1}, {name: categoryName2}],
			siteId: site.id,
			vocabularyName,
		});

		const subCategoryName1 = getRandomString();

		const subCategoryName2 = getRandomString();

		await apiHelpers.headlessAdminTaxonomy.postTaxonomyCategoryTaxonomyCategory(
			{
				name: subCategoryName1,
				parentTaxonomyCategoryId: category[0].id,
			}
		);

		await apiHelpers.headlessAdminTaxonomy.postTaxonomyCategoryTaxonomyCategory(
			{
				name: subCategoryName2,
				parentTaxonomyCategoryId: category[1].id,
			}
		);

		await navigationMenusPage.goto(site.friendlyUrlPath);

		await navigationMenusPage.createNavigationMenu(getRandomString());

		await navigationMenusPage.openAddCategoryModal();

		for (let index = 0; index < 3; index++) {
			await navigationMenusPage.categoriesModal
				.getByRole('button')
				.nth(index)
				.click();
		}

		for (let index = 0; index < 4; index++) {
			await navigationMenusPage.categoriesModal
				.getByRole('checkbox')
				.nth(index)
				.check();
		}

		await page.getByRole('button', {name: 'Select'}).click();

		await page.getByText(categoryName1).hover();

		await page
			.getByRole('button', {name: 'View ' + categoryName1 + ' Options'})
			.click();

		await page.getByRole('menuitem', {name: 'Delete'}).click();

		await expect(
			page.getByText('The item you want to delete')
		).toBeVisible();

		await page.getByLabel('Delete item and children').check();

		await page.getByRole('button', {name: 'Delete'}).click();

		await expect(page.getByText(categoryName1)).not.toBeVisible();

		await expect(page.getByText(subCategoryName1)).not.toBeVisible();

		await page.getByText(categoryName2).hover();

		await page
			.getByRole('button', {name: 'View ' + categoryName2 + ' Options'})
			.click();

		await page.getByRole('menuitem', {name: 'Delete'}).click();

		await expect(
			page.getByText('The item you want to delete')
		).toBeVisible();

		await page.getByLabel('Only delete this item').check();

		await page.getByRole('button', {name: 'Delete'}).click();

		await expect(page.getByText(categoryName2)).not.toBeVisible();

		await expect(page.getByText(subCategoryName2)).toBeVisible();
	}
);

test(
	'Cannot add Page type item in global Navigation Menu',
	{
		tag: '@LPD-50258',
	},
	async ({navigationMenusPage, page}) => {
		await navigationMenusPage.gotoGlobalSiteNavigationMenuPortlet();

		await page.getByRole('button', {name: 'Add'}).getByText('New').click();

		const navigationMenuName = getRandomString();

		await page.getByPlaceholder('Name').fill(navigationMenuName);

		await page.getByRole('button', {name: 'Save'}).click();

		await navigationMenusPage.addMenuItemButton.click();

		expect(await navigationMenusPage.getMenuItem('Page')).not.toBeVisible();

		await navigationMenusPage.gotoGlobalSiteNavigationMenuPortlet();

		await page.waitForTimeout(300);

		await page.getByLabel('Show Actions').click();

		await page.getByRole('menuitem', {name: 'Delete'}).click();

		await page.getByRole('button', {name: 'Delete'}).click();
	}
);

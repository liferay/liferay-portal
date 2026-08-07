/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageSelectorPagesTest} from '../../../fixtures/pageSelectorPagesTest';
import {createCategories} from '../../../helpers/CreateCategories';
import {changeManagementToolbarView} from '../../../utils/changeManagementToolbarView';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {navigationMenusPagesTest} from './fixtures/navigationMenusPagesTest';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	navigationMenusPagesTest,
	pageSelectorPagesTest
);

test('The navigation menu creator could add child item via keyboard', async ({
	apiHelpers,
	navigationMenusPage,
	page,
	site,
}) => {
	const pageName = getRandomString();

	await apiHelpers.headlessDelivery.createSitePage({
		siteId: site.id,
		title: pageName,
	});

	const blog = await apiHelpers.headlessDelivery.postBlog(site.id);

	await navigationMenusPage.goto(site.friendlyUrlPath);

	await navigationMenusPage.createNavigationMenu(getRandomString());

	await navigationMenusPage.addPageItem([pageName]);

	await page.getByText(pageName, {exact: true}).hover();

	await page
		.getByLabel(`Open ${pageName} (Page) Configuration`)
		.getByRole('button')
		.nth(1)
		.hover();

	await expect(page.getByText(`Add item before ${pageName}`)).toBeVisible();

	await page
		.getByLabel(`Open ${pageName} (Page)`)
		.getByRole('button')
		.nth(2)
		.hover();

	await expect(page.getByText(`Add item after ${pageName}`)).toBeVisible();

	await page
		.locator('.site_navigation_menu_editor_AppLayout-content')
		.click();

	await page.keyboard.press('Tab');

	await expect(
		page.getByLabel(`Open ${pageName} (Page) Configuration`)
	).toBeFocused();

	await page.keyboard.press('Tab');

	await page.keyboard.press('Tab');

	await page.keyboard.press('Tab');

	await page.keyboard.press('Tab');

	await expect(page.getByLabel(`View ${pageName} Options`)).toBeFocused();

	await page.keyboard.press('Enter');

	await page
		.getByRole('menuitem', {exact: true, name: 'Add Child'})
		.press('ArrowRight');

	await page
		.getByRole('menuitem', {exact: true, name: 'Blogs Entry'})
		.press('Enter');

	await expect(
		page
			.getByLabel('Select Blogs Entry')
			.locator('div')
			.filter({hasText: 'Select Blogs Entry'})
	).toBeVisible();

	await navigationMenusPage.blogsModal
		.getByRole('button', {name: `Select ${blog.friendlyUrlPath}`})
		.click();

	await expect(
		page.getByLabel(`Open ${blog.headline} (Blogs Entry)`)
	).toBeVisible();
});

test('The site designer could select or deselect a parent node and all its child items via keyboard and mouse', async ({
	apiHelpers,
	navigationMenusPage,
	page,
	site,
}) => {

	// Create Categories and Sub Categories

	const categoryName = getRandomString();

	const vocabularyName = 'testTopicName';

	const categories: Array<any> = await createCategories({
		apiHelpers,
		categoryNames: [{name: categoryName}],
		siteId: site.id,
		vocabularyName,
	});

	const subCategoryName = getRandomString();

	for (let i = 1; i <= 3; i++) {
		await apiHelpers.headlessAdminTaxonomy.postTaxonomyCategoryTaxonomyCategory(
			{
				name: subCategoryName + `${i}`,
				parentTaxonomyCategoryId: categories[0].id,
			}
		);
	}

	// Create Navigation Menu and try to select the previously created Categories using the keyboard and mouse

	await navigationMenusPage.goto(site.friendlyUrlPath);

	await navigationMenusPage.createNavigationMenu(getRandomString());

	await navigationMenusPage.openAddCategoryModal();

	await navigationMenusPage.categoriesModal
		.getByPlaceholder('Search')
		.click();

	await page.keyboard.press('Tab');

	await page.keyboard.press('ArrowRight');

	await page.keyboard.down('Shift');

	await navigationMenusPage.categoriesModal.getByText(categoryName).click();

	await page.keyboard.up('Shift');

	await page.keyboard.press('ArrowRight');

	// Expect that the Categories are selected

	for (let i = 1; i <= 3; i++) {
		const regex = new RegExp(`^${subCategoryName + i}$`);

		await expect(
			navigationMenusPage.categoriesModal
				.locator('li')
				.filter({hasText: regex})
				.getByRole('checkbox')
		).toBeChecked();
	}

	// Try to unselect the Categories

	await page.keyboard.down('Shift');

	await navigationMenusPage.categoriesModal.getByText(categoryName).click();

	await page.keyboard.up('Shift');

	// Expect that the Categories are unselected

	for (let i = 1; i <= 3; i++) {
		const regex = new RegExp(`^${subCategoryName + i}$`);

		await expect(
			navigationMenusPage.categoriesModal
				.locator('li')
				.filter({hasText: regex})
				.getByRole('checkbox')
		).not.toBeChecked();
	}
});

test('The site designer could reorder navigation menu items with child via keyboard', async ({
	apiHelpers,
	navigationMenusPage,
	page,
	site,
}) => {

	// Create Site Pages

	const firstParentPageName = getRandomString();

	const parentPage = await apiHelpers.headlessDelivery.createSitePage({
		siteId: site.id,
		title: firstParentPageName,
	});

	const secondParentPageName = getRandomString();

	await apiHelpers.headlessDelivery.createSitePage({
		siteId: site.id,
		title: secondParentPageName,
	});

	// Create Site Child Page

	const firstChildPageName = getRandomString();

	await apiHelpers.headlessDelivery.createSitePage({
		parentSitePage: {
			friendlyUrlPath: parentPage.friendlyUrlPath,
		},
		siteId: site.id,
		title: firstChildPageName,
	});

	// Create Navigation Menu and add Pages to it

	await navigationMenusPage.goto(site.friendlyUrlPath);

	await navigationMenusPage.createNavigationMenu(getRandomString());

	await navigationMenusPage.addPageItem([
		firstParentPageName,
		secondParentPageName,
		firstChildPageName,
	]);

	// Assert the Navigation Menu Items current order

	let cardTitles = await page.locator('.card-title').allTextContents();

	expect(cardTitles[0]).toBe(firstParentPageName);

	expect(cardTitles[1]).toBe(firstChildPageName);

	expect(cardTitles[2]).toBe(secondParentPageName);

	// Use the mouse and keyboard to move Navigation Menu Item

	await page
		.locator('.site_navigation_menu_editor_AppLayout-content')
		.click();

	await page.keyboard.press('Tab');

	await page.keyboard.press('Tab');

	await page.keyboard.press('Enter');

	await expect(
		page.getByText(`${firstParentPageName} and 1 Child`)
	).toBeVisible();

	await page.keyboard.press('ArrowDown');

	await page.keyboard.press('ArrowDown');

	await page.keyboard.press('ArrowDown');

	await page.keyboard.press('Enter');

	// Assert that the Navigation Menu Item was moved

	await page.waitForTimeout(300);

	cardTitles = await page.locator('.card-title').allTextContents();

	expect(cardTitles[0]).toBe(secondParentPageName);

	expect(cardTitles[1]).toBe(firstParentPageName);

	expect(cardTitles[2]).toBe(firstChildPageName);
});

test('The site designer could cancel reorder navigation menu items via keyboard', async ({
	apiHelpers,
	navigationMenusPage,
	page,
	site,
}) => {

	// Create Site Pages

	const firstParentParentPageName = getRandomString();

	await apiHelpers.headlessDelivery.createSitePage({
		siteId: site.id,
		title: firstParentParentPageName,
	});

	const secondParentParentPageName = getRandomString();

	await apiHelpers.headlessDelivery.createSitePage({
		siteId: site.id,
		title: secondParentParentPageName,
	});

	// Create Navigation Menu and add Pages to it

	await navigationMenusPage.goto(site.friendlyUrlPath);

	await navigationMenusPage.createNavigationMenu(getRandomString());

	await navigationMenusPage.addPageItem([
		firstParentParentPageName,
		secondParentParentPageName,
	]);

	// Use the mouse and keyboard to move Navigation Menu Item

	await page
		.locator('.site_navigation_menu_editor_AppLayout-content')
		.click();

	await page.keyboard.press('Tab');

	await page.keyboard.press('Tab');

	await page.keyboard.press('Enter');

	// Assert that the Navigation Menu Item is draggable

	await expect(
		page.locator('.site-navigation__drag-preview__border')
	).toBeVisible();

	await page.keyboard.press('Escape');

	// Assert that the Navigation Menu Item is not draggable

	await expect(
		page.locator('.site-navigation__drag-preview__border')
	).not.toBeVisible();
});

test('The navigation menu creator could add sibling item via keyboard', async ({
	apiHelpers,
	navigationMenusPage,
	page,
	site,
}) => {

	// Create Site Pages

	const firstContentPage = getRandomString();

	await apiHelpers.headlessDelivery.createSitePage({
		siteId: site.id,
		title: firstContentPage,
	});

	const secondContentPage = getRandomString();

	await apiHelpers.headlessDelivery.createSitePage({
		siteId: site.id,
		title: secondContentPage,
	});

	// Create Blog Entry

	const blog = await apiHelpers.headlessDelivery.postBlog(site.id);

	// Create Navigation Menu and add a Page to it

	await navigationMenusPage.goto(site.friendlyUrlPath);

	await navigationMenusPage.createNavigationMenu(getRandomString());

	await navigationMenusPage.addPageItem([firstContentPage]);

	// Use the mouse and keyboard to navigate to the "add before" button from the first Navigation Menu Item

	await page
		.locator('.site_navigation_menu_editor_AppLayout-content')
		.click();

	await page.keyboard.press('Tab');

	await expect(
		page.getByLabel(`Open ${firstContentPage} (Page) Configuration`)
	).toBeFocused();

	await page.keyboard.press('Tab');

	await expect(
		page.getByRole('button', {name: `Move ${firstContentPage}`})
	).toBeFocused();

	await page.keyboard.press('Tab');

	await expect(
		page
			.getByLabel(`Open ${firstContentPage} (Page) Configuration`)
			.getByRole('button')
			.nth(1)
	).toBeFocused();

	await page.keyboard.press('Enter');

	// Use the mouse and keyboard to add another Page before the first Page

	await page.getByRole('menuitem', {exact: true, name: 'Page'}).click();

	for (let i = 1; i <= 5; i++) {
		await page.keyboard.press('ArrowDown');
	}

	await page.waitForTimeout(300);

	await page.keyboard.press('Enter');

	await navigationMenusPage.pagesModal.getByText(secondContentPage).click();

	await navigationMenusPage.selectButton.click();

	// Use the mouse and keyboard to add a Blog Entry after the second Page

	await page
		.locator('.site_navigation_menu_editor_AppLayout-content')
		.click();

	await page.keyboard.press('Tab');

	await page.keyboard.press('ArrowDown');

	await page.keyboard.press('Tab');

	await page.keyboard.press('Tab');

	await page.keyboard.press('Tab');

	await page.waitForTimeout(300);

	await page.keyboard.press('Enter');

	await page
		.getByRole('menuitem', {exact: true, name: 'Blogs Entry'})
		.press('Enter');

	await changeManagementToolbarView(navigationMenusPage.blogsModal, 'Cards');

	await navigationMenusPage.blogsModal
		.getByRole('button', {name: `Select ${blog.headline}`})
		.click();

	await waitForAlert(page, 'Success:1 Blogs Entry was added to this menu.');

	// Assert the order of the Navigation Menu Items

	const cardTitles = await page.locator('.card-title').allTextContents();

	expect(cardTitles[0]).toBe(secondContentPage);

	expect(cardTitles[1]).toBe(firstContentPage);

	expect(cardTitles[2]).toBe(blog.headline);
});

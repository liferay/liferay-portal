/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {PagesAdminPage} from '../../../pages/layout-admin-web/PagesAdminPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {designLibrariesPageTest} from './fixtures/designLibrariesPageTest';

const test = mergeTests(
	dataApiHelpersTest,
	designLibrariesPageTest,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
		'LPD-57283': {enabled: true},
		'LPD-76864': {enabled: true},
	}),
	loginTest()
);

async function openSelectStyleBookDialog(
	page: Page,
	pageName: string,
	pagesAdminPage: PagesAdminPage,
	siteFriendlyUrlPath: string
) {
	await pagesAdminPage.goto(siteFriendlyUrlPath);

	await pagesAdminPage.goToDesignTabConfiguration(pageName);

	const selectStyleBookDialog = page.getByRole('dialog', {
		name: 'Select Style Book',
	});

	await clickAndExpectToBeVisible({
		target: selectStyleBookDialog,
		trigger: page.getByRole('textbox', {name: 'Style Book'}),
	});

	return selectStyleBookDialog;
}

test(
	'Can create a style book within a design library',
	{tag: '@LPD-88092'},
	async ({apiHelpers, designLibrariesPage, page}) => {

		// Create a design library

		const designLibrary =
			await apiHelpers.headlessAssetLibrary.createDesignLibrary();

		// Go to the design library

		await designLibrariesPage.goToDesignLibrary(designLibrary.name);

		await expect(
			page.getByRole('button', {exact: true, name: 'New'})
		).toBeVisible();

		// Cancel the modal

		await designLibrariesPage.clickNewStyleBook();

		const modal = page.getByRole('dialog');

		await expect(
			modal.getByRole('heading', {name: 'Add Style Book'})
		).toBeVisible();

		await modal.getByRole('button', {name: 'Cancel'}).click();

		await expect(modal).toBeHidden();

		// Add a style book

		await designLibrariesPage.clickNewStyleBook();

		const styleBookName = getRandomString();

		await modal.getByLabel('Name').fill(styleBookName);

		await modal.getByRole('button', {name: 'Save'}).click();

		await expect(modal).toBeHidden();
		await expect(page).toHaveURL(/style_book.+edit/);

		const breadcrumb = page
			.getByRole('navigation', {name: 'Breadcrumb'})
			.last();

		await expect(breadcrumb.getByText(styleBookName)).toBeVisible();

		// Click the back button

		await page
			.locator('.control-menu-nav-item')
			.getByTitle(`Go to ${designLibrary.name}`)
			.click();

		await expect(
			page.getByRole('heading', {name: 'Design Libraries'})
		).toBeVisible();
	}
);

test(
	'New button is not visible without permissions',
	{tag: '@LPD-88092'},
	async ({apiHelpers, designLibrariesPage, page}) => {

		// Create a design library

		const designLibrary =
			await apiHelpers.headlessAssetLibrary.createDesignLibrary();

		// Create an unprivileged user

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		// Go to the design library

		await designLibrariesPage.goToDesignLibrary(designLibrary.name);

		const newButton = page.getByRole('button', {name: 'New'});

		await expect(newButton).toBeVisible();

		const designLibraryURL = page.url();

		// Switch to the unprivileged user

		await performLogout(page);

		await performLoginViaApi({page, screenName: user.alternateName});

		// Check the hidden New button

		await page.goto(designLibraryURL);

		await expect(newButton).toBeHidden();
	}
);

test(
	'Design Library content screen lists the style books added to it',
	{tag: '@LPD-74829'},
	async ({apiHelpers, designLibrariesPage, page}) => {

		// Create a design library

		const designLibrary =
			await apiHelpers.headlessAssetLibrary.createDesignLibrary();

		// Add a style book

		const styleBookName = getRandomString();

		await designLibrariesPage.createStyleBook(
			designLibrary.name,
			styleBookName
		);

		// Check the style book row

		const styleBookRow = designLibrariesPage.resourcesTable.getByRole(
			'row',
			{
				name: styleBookName,
			}
		);

		await expect(
			styleBookRow.getByRole('cell', {name: 'Test Test'})
		).toBeVisible();

		// Check the row actions

		await styleBookRow.getByRole('button', {name: /Actions$/}).click();

		await expect(
			page.getByRole('menuitem', {
				exact: true,
				name: 'Edit in Style Book Editor',
			})
		).toBeVisible();

		await expect(
			page.getByRole('menuitem', {exact: true, name: 'Delete'})
		).toBeVisible();
	}
);

test(
	'Editing a Design Library style book opens the editor with Design-Library-aware experience',
	{tag: '@LPD-74829'},
	async ({apiHelpers, designLibrariesPage, page}) => {

		// Create a design library

		const designLibrary =
			await apiHelpers.headlessAssetLibrary.createDesignLibrary();

		// Create a site with a page

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		const pageName = getRandomString();

		await apiHelpers.headlessAdminSite.createPage(
			site.externalReferenceCode,
			{
				name_i18n: {en_US: pageName},
				type: 'WidgetPage',
			}
		);

		// Connect the site

		await apiHelpers.jsonWebServicesDepotGroupRel.addDepotEntryGroupRel(
			designLibrary.id,
			site.id
		);

		// Add a style book

		const styleBookName = getRandomString();

		await designLibrariesPage.createStyleBook(
			designLibrary.name,
			styleBookName
		);

		// Open the style book editor

		const styleBookRow = designLibrariesPage.resourcesTable.getByRole(
			'row',
			{
				name: styleBookName,
			}
		);

		await styleBookRow.getByRole('button', {name: /Actions$/}).click();

		await page
			.getByRole('menuitem', {
				exact: true,
				name: 'Edit in Style Book Editor',
			})
			.click();

		await expect(page).toHaveURL(/edit_style_book_entry/);

		// Check the editor breadcrumb

		const breadcrumb = page
			.getByRole('navigation', {name: 'Breadcrumb'})
			.last();

		await expect(breadcrumb).toBeVisible();

		const items = breadcrumb.getByRole('listitem');

		await expect(items).toHaveCount(2);
		await expect(items.first()).toContainText(designLibrary.name);
		await expect(items.last()).toContainText(styleBookName);
		await expect(items.last()).toHaveClass(/active/);

		// Check the preview pages

		await page
			.getByRole('button', {name: 'Pages'})
			.or(page.getByRole('button', {name: 'Fragments'}))
			.or(page.getByRole('button', {name: 'Masters'}))
			.first()
			.click();

		await page.getByRole('menuitem', {name: 'Pages'}).click();

		await expect(
			page.getByRole('button', {name: `[${site.name}] ${pageName}`})
		).toBeVisible();

		// Go back to the listing

		await breadcrumb.getByRole('link', {name: designLibrary.name}).click();

		await expect(page).toHaveURL(/view_resources_design_library/);

		await expect(styleBookRow).toBeVisible();
	}
);

const testWithSite = mergeTests(test, isolatedSiteTest, pagesAdminPagesTest);

testWithSite(
	'Design library style books are filtered by page theme in the style book selector',
	{tag: '@LPD-83671'},
	async ({apiHelpers, designLibrariesPage, page, pagesAdminPage, site}) => {

		// Create a design library

		const designLibrary =
			await apiHelpers.headlessAssetLibrary.createDesignLibrary();

		// Add a widget page

		const pageName = getRandomString();

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: String(site.id),
			options: {type: 'portlet'},
			title: pageName,
		});

		// Add the Classic style book

		const classicStyleBookName = getRandomString();

		await designLibrariesPage.createStyleBook(
			designLibrary.name,
			classicStyleBookName,
			'Classic Theme'
		);

		// Add the CMS style book

		const cmsStyleBookName = getRandomString();

		await designLibrariesPage.createStyleBook(
			designLibrary.name,
			cmsStyleBookName,
			'CMS Theme'
		);

		// Check the selector before connecting

		let selectStyleBookDialog = await openSelectStyleBookDialog(
			page,
			pageName,
			pagesAdminPage,
			site.friendlyUrlPath
		);

		await expect(
			selectStyleBookDialog.getByText('Styles from Classic Theme', {
				exact: true,
			})
		).toBeVisible();

		await expect(
			selectStyleBookDialog.getByText(classicStyleBookName, {exact: true})
		).toBeHidden();

		await expect(
			selectStyleBookDialog.getByText(cmsStyleBookName, {exact: true})
		).toBeHidden();

		await selectStyleBookDialog.getByLabel('Close').click();

		// Connect the design library

		await apiHelpers.jsonWebServicesDepotGroupRel.addDepotEntryGroupRel(
			designLibrary.id,
			site.id
		);

		// Check the selector after connecting

		selectStyleBookDialog = await openSelectStyleBookDialog(
			page,
			pageName,
			pagesAdminPage,
			site.friendlyUrlPath
		);

		await expect(
			selectStyleBookDialog.getByText(classicStyleBookName, {exact: true})
		).toBeVisible();

		await expect(
			selectStyleBookDialog.getByText(cmsStyleBookName, {exact: true})
		).toBeHidden();

		await selectStyleBookDialog.getByLabel('Close').click();
	}
);

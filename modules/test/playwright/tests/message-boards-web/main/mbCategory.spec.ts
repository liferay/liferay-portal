/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {messageBoardsPagesTest} from '../../../fixtures/messageBoardsTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-105225': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	messageBoardsPagesTest
);

test('Can manage categories from the admin', async ({
	messageBoardsPage,
	page,
	site,
}) => {
	const categoryName = getRandomString();

	page.on('dialog', (dialog) => dialog.accept());

	await messageBoardsPage.goto(site.friendlyUrlPath);

	// Add a category through the admin portlet

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByRole('menuitem', {name: 'Category'}),
		trigger: page.getByRole('button', {exact: true, name: 'New'}),
	});

	await page.locator('[id$="_MBAdminPortlet_name"]').fill(categoryName);

	await page.getByRole('button', {name: 'Save'}).click();

	const categoryLink = page.getByRole('link', {name: categoryName});

	await expect(categoryLink).toBeVisible();

	// Rename the category through its row action

	const editedName = getRandomString();

	const editItem = page
		.locator('.dropdown-menu:visible')
		.getByText('Edit', {exact: true});

	await expect(async () => {
		await page
			.locator('a.component-action.dropdown-toggle')
			.first()
			.click();

		await expect(editItem).toBeVisible({timeout: 3000});
	}).toPass();

	await editItem.click();

	await page.locator('[id$="_MBAdminPortlet_name"]').fill(editedName);

	await page.getByRole('button', {name: 'Save'}).click();

	await expect(page.getByRole('link', {name: editedName})).toBeVisible();

	// Delete the category

	await messageBoardsPage.deleteAllMBEntries();

	await expect(page.getByRole('link', {name: editedName})).toBeHidden();
});

test('Can add a category', async ({messageBoardsWidgetPage, page, site}) => {
	const categoryName = getRandomString();

	const layout = await messageBoardsWidgetPage.addMessageBoardsPortlet(site);

	await messageBoardsWidgetPage.addCategory(site, layout, categoryName);

	await expect(page.getByRole('link', {name: categoryName})).toBeVisible();
});

test('Category cannot be added without a name', async ({
	messageBoardsWidgetPage,
	page,
	site,
}) => {
	const layout = await messageBoardsWidgetPage.addMessageBoardsPortlet(site);

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	const addCategoryLink = page.getByRole('link', {name: 'Add Category'});

	await addCategoryLink.waitFor();
	await addCategoryLink.click();

	const nameField = page.locator(
		'[id="_com_liferay_message_boards_web_portlet_MBPortlet_name"]'
	);

	await nameField.waitFor();

	// Saving with an empty name is blocked, so the form stays open

	await page.getByRole('button', {name: 'Save'}).click();

	await expect(nameField).toBeVisible();
});

test('Can view a thread in a subcategory', async ({
	apiHelpers,
	messageBoardsPage,
	page,
	site,
}) => {
	const categoryName = getRandomString();
	const subcategoryName = getRandomString();
	const threadHeadline = getRandomString();
	const threadBody = getRandomString();

	// Seed a category, a subcategory and a thread in the subcategory

	const category =
		await apiHelpers.headlessDelivery.postSiteMessageBoardSection({
			siteId: site.id,
			title: categoryName,
		});

	const subcategory =
		await apiHelpers.headlessDelivery.postMessageBoardSectionMessageBoardSection(
			{
				parentMessageBoardSectionId: category.id,
				title: subcategoryName,
			}
		);

	await apiHelpers.headlessDelivery.postMessageBoardSectionMessageBoardThread(
		{
			articleBody: threadBody,
			headline: threadHeadline,
			messageBoardSectionId: subcategory.id,
		}
	);

	// Navigate down the category hierarchy to the thread

	await messageBoardsPage.goto(site.friendlyUrlPath);

	await page.getByRole('link', {name: categoryName}).click();

	await page.getByRole('link', {name: subcategoryName}).click();

	await page.getByRole('link', {name: threadHeadline}).click();

	await expect(page.getByText(threadBody)).toBeVisible();
});

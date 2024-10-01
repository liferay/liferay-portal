/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {changeTrackingPagesTest} from '../../fixtures/changeTrackingPagesTest';
import {productMenuPageTest} from '../../fixtures/productMenuPageTest';
import getRandomString from '../../utils/getRandomString';
import {waitForAlert} from '../../utils/waitForAlert';
import {blogsPagesTest} from '../blogs-web/fixtures/blogsPagesTest';
import {journalPagesTest} from '../journal-web/fixtures/journalPagesTest';

export const test = mergeTests(
	apiHelpersTest,
	blogsPagesTest,
	changeTrackingPagesTest,
	journalPagesTest,
	productMenuPageTest
);

test('Resolve deletion modification conflict publications by discarding', async ({
	apiHelpers,
	blogsEditBlogEntryPage,
	blogsPage,
	changeTrackingPage,
	ctCollection,
	journalEditArticlePage,
	journalPage,
	page,
	productMenuPage,
}) => {
	await changeTrackingPage.workOnProduction();

	await journalEditArticlePage.goto();

	const title = getRandomString();

	await journalEditArticlePage.fillTitle(title);

	await page.getByRole('button', {name: 'Publish'}).click();

	await waitForAlert(page, `Success:${title} was created successfully.`);

	await changeTrackingPage.workOnPublication(ctCollection);

	await journalPage.goto();

	await page.getByLabel(`Actions for ${title}`).click();

	await page.getByRole('menuitem', {name: 'Delete'}).click();

	await page.reload();

	await changeTrackingPage.workOnProduction();

	const ctCollection2 =
		await apiHelpers.headlessChangeTracking.createCTCollection(
			getRandomString()
		);

	await changeTrackingPage.workOnPublication(ctCollection2);

	await journalPage.goto();

	await journalEditArticlePage.editArticle(title);

	await page.getByRole('button', {name: 'Publish'}).click();

	await waitForAlert(page, `Success:${title} was updated successfully.`);

	await blogsEditBlogEntryPage.goto();

	const content = getRandomString();

	await blogsEditBlogEntryPage.editBlogEntry({
		content,
		publish: true,
		title,
	});

	await changeTrackingPage.goToReviewChanges(ctCollection.name);

	const publishLink = page.getByRole('link', {name: 'Publish'});

	await publishLink.click();

	await expect(page.getByText('Checking Changes')).toBeVisible();

	const publishButton = page.getByRole('button', {name: 'Publish'});

	await publishButton.click();

	await expect(page.getByRole('link', {name: 'History'})).toBeVisible();

	await page.getByRole('link', {name: ctCollection.name}).click();

	await expect(page.getByText('Deleted')).toBeVisible();

	await changeTrackingPage.workOnProduction();

	await journalPage.goto();

	await expect(page.getByText(title)).not.toBeVisible();

	await changeTrackingPage.workOnPublication(ctCollection2);

	await expect(page.getByText(title)).toBeVisible();

	await changeTrackingPage.goToReviewChanges(ctCollection2.name);

	await publishLink.click();

	await expect(page.getByText('Missing entity')).toBeVisible();

	await page.getByRole('link', {name: 'Discard Change'}).click();

	await page.getByRole('button', {name: 'Discard'}).click();

	await publishButton.click();

	await journalPage.goto();

	await expect(page.getByText('No web content was found.')).toBeVisible();

	await apiHelpers.headlessChangeTracking.deleteCTCollection(
		ctCollection2.id
	);

	await blogsPage.goto();

	await blogsPage.deleteAllBlogEntries();

	await page.waitForTimeout(300);

	await productMenuPage.openProductMenuIfClosed();

	await page.getByRole('menuitem', {name: 'Recycle Bin'}).click();

	await page.getByLabel('Recycle Bin').getByTestId('app').click();

	await expect(
		page
			.getByTestId('header')
			.locator('div')
			.filter({hasText: 'Recycle Bin'})
			.nth(1)
	).toBeVisible();

	await page.getByLabel('Select All Items on the Page').check();

	await page.getByRole('button', {name: 'Delete'}).click();

	await page
		.getByLabel('Delete- Loading')
		.getByRole('button', {name: 'Delete'})
		.click();

	await waitForAlert(page, 'Success:Your request completed successfully.');
});

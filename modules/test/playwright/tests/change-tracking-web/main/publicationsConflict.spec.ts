/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {changeTrackingPagesTest} from '../../../fixtures/changeTrackingPagesTest';
import {customFieldsPagesTest} from '../../../fixtures/customFieldsPagesTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {TCustomField} from '../../../helpers/CustomFieldTypesHelper';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {waitForAlert} from '../../../utils/waitForAlert';
import {blogsPagesTest} from '../../blogs-web/main/fixtures/blogsPagesTest';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';

export const test = mergeTests(
	apiHelpersTest,
	blogsPagesTest,
	changeTrackingPagesTest,
	customFieldsPagesTest,
	journalPagesTest,
	pageEditorPagesTest
);

test(
	'Delete hidden system modification conflict publications by discarding',
	{tag: '@LPD-50810'},
	async ({
		apiHelpers,
		changeTrackingPage,
		ctCollection,
		page,
		pageEditorPage,
	}) => {
		const site =
			await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath(
				'guest'
			);

		// Add a page with a fragment in production

		const layoutTitle = getRandomString();

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {type: 'content'},
			title: layoutTitle,
		});

		try {
			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			await pageEditorPage.addFragment('Basic Components', 'Heading');

			await pageEditorPage.publishPage();

			// Edit fragment in publication

			await changeTrackingPage.workOnPublication(ctCollection);

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			const headingId = await pageEditorPage.getFragmentId('Heading');

			await pageEditorPage.editTextEditable(
				headingId,
				'element-text',
				'Edited Text'
			);

			await pageEditorPage.publishPage();

			// Delete fragment from production

			await changeTrackingPage.workOnProduction();

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			await pageEditorPage.deleteFragment(headingId);

			await pageEditorPage.publishPage();

			// Review and discard publication changes

			await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

			await page.getByRole('link', {name: 'Publish'}).click();

			await expect(page.getByText('Checking Changes')).toBeVisible();

			for (let i = 0; i < 2; i++) {
				await page
					.getByRole('link', {name: 'Discard Change'})
					.first()
					.click();

				await page.getByRole('button', {name: 'Discard'}).click();
			}

			// Assert entries deleted

			await page.getByRole('button', {name: 'Publish'}).click();

			await expect(
				page.getByRole('link', {name: ctCollection.body.name})
			).toBeVisible();

			await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

			await expect(page.getByText('Heading Example')).not.toBeVisible();
		}
		finally {
			await apiHelpers.jsonWebServicesLayout.deleteLayout(layout.plid);
		}
	}
);

test('LPD-54602 Edit in Production action should not be visible if entity does not have an edit url', async ({
	addCustomFieldPage,
	changeTrackingPage,
	ctCollection,
	page,
}) => {
	const customField: TCustomField = {
		fieldName: getRandomString(),
		fieldType: 'inputField',
		resource: 'Page',
	};

	await changeTrackingPage.workOnPublication(ctCollection);

	await addCustomFieldPage.addCustomField(customField);

	await changeTrackingPage.workOnProduction();

	await addCustomFieldPage.addCustomField(customField);

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	await page.getByRole('link', {name: 'Publish'}).click();

	await expect(page.getByText('Checking Changes')).toBeVisible();

	await expect(
		page.getByText('Test Test added a Custom Field')
	).toBeVisible();

	await expect(page.getByRole('menuitem')).not.toBeVisible();

	await expect(
		page.getByRole('link', {name: 'Discard Change'})
	).toBeVisible();
});

test('Resolve deletion modification conflict publications by discarding', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	journalEditArticlePage,
	journalPage,
	page,
}) => {
	await journalEditArticlePage.goto();

	const title = getRandomString();

	await journalEditArticlePage.fillTitle(title);

	await journalEditArticlePage.publishArticle();

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

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByRole('menuitem', {
			exact: true,
			name: 'Publish',
		}),
		trigger: page.getByRole('button', {
			name: 'Select and Confirm Publish Settings',
		}),
	});

	await waitForAlert(page, `Success:${title} was updated successfully.`);

	const site =
		await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('guest');

	const blog = await apiHelpers.headlessDelivery.postBlog(site.id);

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	const publishLink = page.getByRole('link', {name: 'Publish'});

	await publishLink.click();

	await expect(page.getByText('Checking Changes')).toBeVisible();

	const publishButton = page.getByRole('button', {name: 'Publish'});

	await publishButton.click();

	await expect(page.getByRole('link', {name: 'History'})).toBeVisible();

	await page.getByRole('link', {name: ctCollection.body.name}).click();

	await expect(page.getByText('Deleted')).toBeVisible();

	await changeTrackingPage.workOnProduction();

	await journalPage.goto();

	await expect(page.getByText(title)).not.toBeVisible();

	await changeTrackingPage.workOnPublication(ctCollection2);

	await expect(page.getByText(title)).toBeVisible();

	await changeTrackingPage.goToReviewChanges(ctCollection2.body.name);

	await publishLink.click();

	await expect(page.getByText('Missing entity')).toBeVisible();

	await page
		.getByLabel('Test Test added a Web Content')
		.getByRole('button')
		.click();

	await page.getByRole('menuitem', {name: 'Discard Change'}).click();

	await page.getByRole('button', {name: 'Discard'}).click();

	await publishButton.click();

	await journalPage.goto();

	await expect(page.getByText(title)).not.toBeVisible();

	await apiHelpers.headlessChangeTracking.deleteCTCollection(
		ctCollection2.body.id
	);

	await apiHelpers.headlessDelivery.deleteBlog(blog.id);
});

test('Resolve deletion modification conflict publications by restoring from recycle bin', async ({
	changeTrackingPage,
	ctCollection,
	journalEditArticlePage,
	journalPage,
	page,
}) => {
	await journalEditArticlePage.goto();

	const title = getRandomString();

	await journalEditArticlePage.fillTitle(title);

	await journalEditArticlePage.publishArticle();

	await waitForAlert(page, `Success:${title} was created successfully.`);

	await changeTrackingPage.workOnPublication(ctCollection);

	await journalPage.goto();

	await journalEditArticlePage.editArticle(title);

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByRole('menuitem', {
			exact: true,
			name: 'Publish',
		}),
		trigger: page.getByRole('button', {
			name: 'Select and Confirm Publish Settings',
		}),
	});

	await waitForAlert(page, `Success:${title} was updated successfully.`);

	await changeTrackingPage.workOnProduction();

	await journalPage.goto();

	await page.getByLabel(`Actions for ${title}`).click();

	await page.getByRole('menuitem', {name: 'Delete'}).click();

	await page.reload();

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	const publishLink = page.getByRole('link', {name: 'Publish'});

	await publishLink.click();

	await expect(page.getByText('Missing entity')).toBeVisible();

	await page.getByRole('link', {name: 'Restore From Recycle Bin'}).click();

	await waitForAlert(page, 'Success:Your request completed successfully.');

	await journalPage.goto();

	await expect(page.getByText(title)).toBeVisible();

	await changeTrackingPage.workOnProduction();

	await journalPage.goto();

	await page.getByLabel(`Actions for ${title}`).click();

	await page.getByRole('menuitem', {name: 'Delete'}).click();

	await page.goto(`/group/guest${PORTLET_URLS.recycleBin}`);

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

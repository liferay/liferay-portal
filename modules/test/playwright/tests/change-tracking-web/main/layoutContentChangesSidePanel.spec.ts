/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {changeTrackingPagesTest} from '../../../fixtures/changeTrackingPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import getRandomString from '../../../utils/getRandomString';

export const test = mergeTests(
	apiHelpersTest,
	changeTrackingPagesTest,
	featureFlagsTest({
		'LPD-75671': {enabled: true},
	}),
	isolatedSiteTest,
	pagesAdminPagesTest,
	pageEditorPagesTest
);

const pageTitle = getRandomString();
let site;

test.beforeEach(
	async ({
		apiHelpers,
		changeTrackingPage,
		page,
		pageEditorPage,
		pagesAdminPage,
	}) => {
		await changeTrackingPage.workOnProduction();

		site =
			await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath(
				'guest'
			);

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await page
			.getByTestId('creationMenuNewButton')
			.locator('visible=true')
			.click();

		await pagesAdminPage.addPage({
			name: pageTitle,
		});

		await pageEditorPage.addFragment('Basic Components', 'Heading');

		await pageEditorPage.publishPage();
	}
);

test.afterEach(async ({changeTrackingPage, pagesAdminPage}) => {
	await changeTrackingPage.workOnProduction();

	await pagesAdminPage.goto(site.friendlyUrlPath);

	await pagesAdminPage.deletePage(pageTitle);
});

test('LPD-82049 Layout content changes side panel is visible for layouts', async ({
	changeTrackingPage,
	ctCollection,
	page,
	pageEditorPage,
	pagesAdminPage,
}) => {
	await changeTrackingPage.workOnPublication(ctCollection);

	await pagesAdminPage.goto(site.friendlyUrlPath);
	await pagesAdminPage.clickOnAction('Edit', pageTitle);

	const headingId = await pageEditorPage.getFragmentId('Heading');

	await pageEditorPage.editTextEditable(headingId, 'element-text', 'Edited');

	await pageEditorPage.publishPage();

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	await changeTrackingPage.viewChanges({
		click: true,
		title: pageTitle,
		type: 'Page',
	});

	const sidePanelLocator = page
		.getByLabel('Layout Changes Side Panel')
		.filter({hasText: 'Content Changes (1)'});

	await expect(sidePanelLocator).toBeVisible();

	await page.getByLabel('View Content Changes').click();

	await expect(sidePanelLocator).not.toBeVisible();

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	const headingFragment = page
		.locator('.cell-item-actions .dropdown svg.lexicon-icon-ellipsis-v')
		.first();

	await headingFragment.waitFor();
	await headingFragment.click();

	await clickAndExpectToBeHidden({
		target: sidePanelLocator,
		trigger: headingFragment,
	});
});

test('LPD-82049 Can expand and collapse panels', async ({
	changeTrackingPage,
	ctCollection,
	page,
	pageEditorPage,
	pagesAdminPage,
}) => {
	await changeTrackingPage.workOnPublication(ctCollection);

	await pagesAdminPage.goto(site.friendlyUrlPath);
	await pagesAdminPage.clickOnAction('Edit', pageTitle);

	for (let i = 0; i < 4; i++) {
		await pageEditorPage.addFragment('Basic Components', 'Heading');
	}

	await pageEditorPage.publishPage();

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	await changeTrackingPage.viewChanges({
		click: true,
		title: pageTitle,
		type: 'Page',
	});

	await expect(
		page
			.getByLabel('Layout Changes Side Panel')
			.filter({hasText: 'Content Changes (4)'})
	).toBeVisible();

	const panels = await page.locator('button.panel-header-link');

	await panels.nth(2).click();
	await panels.nth(3).click();

	const panelsExpanded = await page.locator(
		'button.panel-header-link[aria-expanded="true"]'
	);

	await expect(panelsExpanded).toHaveCount(2);

	await page.getByRole('button', {name: 'Collapse All'}).click();

	await expect(panelsExpanded).toHaveCount(0);

	await page.getByRole('button', {name: 'Expand All'}).click();

	await expect(panelsExpanded).toHaveCount(4);
});

test('LPD-82049 Changes are limited to 20 panels and can be expanded to view more', async ({
	changeTrackingPage,
	ctCollection,
	page,
	pageEditorPage,
	pagesAdminPage,
}) => {
	await changeTrackingPage.workOnPublication(ctCollection);

	await pagesAdminPage.goto(site.friendlyUrlPath);
	await pagesAdminPage.clickOnAction('Edit', pageTitle);

	for (let i = 0; i < 25; i++) {
		await pageEditorPage.addFragment('Basic Components', 'Heading');
	}

	await pageEditorPage.publishPage();

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	await page.getByRole('link', {name: 'Page'}).click();

	await changeTrackingPage.viewChanges({
		click: true,
		title: pageTitle,
		type: 'Page',
	});

	await expect(
		page
			.getByLabel('Layout Changes Side Panel')
			.filter({hasText: 'Content Changes (25)'})
	).toBeVisible();

	let panelsCount = await page.locator('button.panel-header-link').count();

	await expect(panelsCount).toEqual(20);

	const viewMoreButton = page.getByRole('button', {name: 'View More'});

	await viewMoreButton.scrollIntoViewIfNeeded();
	await viewMoreButton.click();

	await page.waitForTimeout(1000);

	panelsCount = await page.locator('button.panel-header-link').count();

	await expect(panelsCount).toEqual(25);

	await expect(viewMoreButton).not.toBeVisible();
});

test('LPD-82049 Can redirect to fragment entry link change view', async ({
	changeTrackingPage,
	ctCollection,
	page,
	pageEditorPage,
	pagesAdminPage,
}) => {
	await changeTrackingPage.workOnPublication(ctCollection);

	await pagesAdminPage.goto(site.friendlyUrlPath);
	await pagesAdminPage.clickOnAction('Edit', pageTitle);

	await pageEditorPage.addFragment('Basic Components', 'Heading');

	await pageEditorPage.publishPage();

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	await changeTrackingPage.viewChanges({
		click: true,
		title: pageTitle,
		type: 'Page',
	});

	await expect(
		page
			.getByLabel('Layout Changes Side Panel')
			.filter({hasText: 'Content Changes'})
	).toBeVisible();

	const viewDetailsButton = page.getByRole('link', {name: 'View Details'});

	const fragmentTitle = await page
		.locator('button.panel-header-link')
		.innerText();

	await viewDetailsButton.click();

	await expect(page.locator('h2')).toContainText(fragmentTitle, {
		ignoreCase: true,
	});
});

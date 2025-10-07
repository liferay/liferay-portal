/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {changeTrackingPagesTest} from '../../../fixtures/changeTrackingPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../../utils/waitForAlert';

export const test = mergeTests(
	apiHelpersTest,
	changeTrackingPagesTest,
	featureFlagsTest({
		'LPD-20183': {enabled: true},
	})
);

let file;
let journalArticleTitle;

test.beforeEach(async ({apiHelpers, ctCollection}) => {
	const site =
		await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('guest');

	await apiHelpers.headlessChangeTracking.checkoutCTCollection(
		ctCollection.body.id
	);

	file = await apiHelpers.headlessDelivery.postDocument(
		site.id,
		createReadStream(path.join(__dirname, '/dependencies/attachment.txt'))
	);

	const basicWebContentStructureId =
		await getBasicWebContentStructureId(apiHelpers);

	journalArticleTitle = getRandomString();

	await apiHelpers.jsonWebServicesJournal.addWebContent({
		ddmStructureId: basicWebContentStructureId,
		groupId: site.id,
		titleMap: {en_US: journalArticleTitle},
	});
});

test('LPD-26363 Can delete ctEntries in bulk', async ({
	changeTrackingPage,
	ctCollection,
	page,
}) => {
	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByTitle('Select Items'),
		trigger: changeTrackingPage.frontendDataSetEntries.getByText(
			file.title
		),
	});

	await expect(
		page.getByRole('button', {name: 'Discard Changes'})
	).toBeVisible();

	const allSelected = await page.getByText('All Selected');

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page
			.locator('[data-testid="visualization-mode-table"]')
			.getByLabel('Actions'),
		trigger: allSelected,
	});

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByRole('menuitem', {name: 'Discard Changes'}),
		trigger: allSelected,
	});

	await expect(
		page.getByRole('heading', {name: 'Discarded Changes'})
	).toBeVisible();

	await page.getByRole('button', {name: 'Discard'}).click();

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	const publicationActionMenu = page.locator('.publications-tbar .dropdown');

	await publicationActionMenu.click();

	await page
		.getByRole('menuitem', {
			name: 'Show System Changes',
		})
		.click();

	await expect(page.getByText('No Results Found')).toBeVisible();
});

test('LPD-46060 Can move ctEntries in bulk', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	page,
}) => {
	const ctCollection2 =
		await apiHelpers.headlessChangeTracking.createCTCollection(
			getRandomString()
		);

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByTitle('Select Items'),
		trigger: changeTrackingPage.frontendDataSetEntries.getByText(
			file.title
		),
	});

	await expect(
		page.getByRole('button', {name: 'Move Changes'})
	).toBeVisible();

	const allSelected = await page.getByText('All Selected');

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page
			.locator('[data-testid="visualization-mode-table"]')
			.getByLabel('Actions'),
		trigger: allSelected,
	});

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByRole('menuitem', {name: 'Move Changes'}),
		trigger: allSelected,
	});

	await expect(
		page.getByRole('heading', {name: 'Moved Changes'})
	).toBeVisible();

	const publicationSelector = page.locator(
		'#_com_liferay_change_tracking_web_portlet_PublicationsPortlet_toPublication'
	);

	await page.waitForLoadState('domcontentloaded');

	await expect(publicationSelector).toBeVisible();

	await publicationSelector.selectOption(ctCollection2.body.name);

	await page.getByRole('button', {name: 'Move Changes'}).click();

	await waitForAlert(page, 'Success:Your request completed successfully.');

	const publicationActionMenu = page.locator('.publications-tbar .dropdown');

	await publicationActionMenu.click();

	await page
		.getByRole('menuitem', {
			name: 'Show System Changes',
		})
		.click();

	await expect(page.getByText('No Results Found')).toBeVisible();

	await apiHelpers.headlessChangeTracking.deleteCTCollection(
		ctCollection2.body.id
	);
});

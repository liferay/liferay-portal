/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {changeTrackingPagesTest} from '../../fixtures/changeTrackingPagesTest';
import {collectionsPagesTest} from '../../fixtures/collectionsPagesTest';
import {featureFlagsTest} from '../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../fixtures/isolatedSiteTest';
import {loginTest} from '../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../fixtures/pageEditorPagesTest';
import {pagesAdminPagesTest} from '../../fixtures/pagesAdminPagesTest';
import getRandomString from '../../utils/getRandomString';

export const test = mergeTests(
	apiHelpersTest,
	changeTrackingPagesTest,
	collectionsPagesTest,
	pagesAdminPagesTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	pageEditorPagesTest,
	isolatedSiteTest,
	loginTest()
);

test('Can publish a Publication containing an added AssetListEntry', async ({
	apiHelpers,
	changeTrackingPage,
	collectionsPage,
	ctCollection,
	page,
	site,
}) => {
	await changeTrackingPage.workOnPublication(ctCollection);

	let manualAssetListEntryName: string;
	let dynamicAssetListEntryName: string;

	await test.step('Create a manual and dynamic AssetListEntry', async () => {
		manualAssetListEntryName = getRandomString();

		await collectionsPage.goto(site.friendlyUrlPath);
		await collectionsPage.addNewManualCollection(manualAssetListEntryName);

		dynamicAssetListEntryName = getRandomString();

		await collectionsPage.goto(site.friendlyUrlPath);
		await collectionsPage.addNewDynamicCollection(
			dynamicAssetListEntryName
		);
	});

	await test.step('Publish the CTCollection then assert AssetListEntry names and types', async () => {
		await apiHelpers.headlessChangeTracking.publishCTCollection(
			ctCollection.body.id
		);

		await page.reload();

		await collectionsPage.goto(site.friendlyUrlPath);

		await expect(page.getByText(manualAssetListEntryName)).toBeVisible();
		await expect(page.getByText(dynamicAssetListEntryName)).toBeVisible();
	});
});

test('Can publish a Publication containing a deleted AssetListEntry', async ({
	apiHelpers,
	changeTrackingPage,
	collectionsPage,
	ctCollection,
	page,
	site,
}) => {
	let manualAssetListEntryName: string;
	let dynamicAssetListEntryName: string;

	await test.step('Create a manual and dynamic AssetListEntry', async () => {
		manualAssetListEntryName = getRandomString();

		await collectionsPage.goto(site.friendlyUrlPath);
		await collectionsPage.addNewManualCollection(manualAssetListEntryName);

		dynamicAssetListEntryName = getRandomString();

		await collectionsPage.goto(site.friendlyUrlPath);
		await collectionsPage.addNewDynamicCollection(
			dynamicAssetListEntryName
		);
	});

	await test.step('Delete both AssetListEntries in the CTCollection', async () => {
		await changeTrackingPage.workOnPublication(ctCollection);

		await collectionsPage.goto(site.friendlyUrlPath);

		await collectionsPage.deleteCollection(manualAssetListEntryName);
		await collectionsPage.deleteCollection(dynamicAssetListEntryName);
	});

	await test.step('Publish the CTCollection then assert AssetListEntries were deleted', async () => {
		await apiHelpers.headlessChangeTracking.publishCTCollection(
			ctCollection.body.id
		);

		await page.reload();

		await collectionsPage.goto(site.friendlyUrlPath);

		await expect(page.getByText(manualAssetListEntryName)).toBeHidden();
		await expect(page.getByText(dynamicAssetListEntryName)).toBeHidden();
	});
});

test('Can publish a Publication containing an edited AssetListEntry', async ({
	apiHelpers,
	changeTrackingPage,
	collectionsPage,
	ctCollection,
	page,
	site,
}) => {
	let manualAssetListEntryName: string;
	let dynamicAssetListEntryName: string;

	await test.step('Create a manual and dynamic AssetListEntry', async () => {
		manualAssetListEntryName = getRandomString();

		await collectionsPage.goto(site.friendlyUrlPath);
		await collectionsPage.addNewManualCollection(manualAssetListEntryName);

		dynamicAssetListEntryName = getRandomString();

		await collectionsPage.goto(site.friendlyUrlPath);
		await collectionsPage.addNewDynamicCollection(
			dynamicAssetListEntryName
		);
	});

	let newManualAssetListEntryName: string;
	let newDynamicAssetListEntryName: string;

	await test.step('Edit both AssetListEntries in the CTCollection', async () => {
		await changeTrackingPage.workOnPublication(ctCollection);

		newManualAssetListEntryName = getRandomString();

		await collectionsPage.goto(site.friendlyUrlPath);
		await collectionsPage.renameCollection(
			manualAssetListEntryName,
			newManualAssetListEntryName
		);

		newDynamicAssetListEntryName = getRandomString();

		await collectionsPage.goto(site.friendlyUrlPath);
		await collectionsPage.renameCollection(
			dynamicAssetListEntryName,
			newDynamicAssetListEntryName
		);
	});

	await test.step('Publish the CTCollection then assert AssetListEntry names were updated', async () => {
		await apiHelpers.headlessChangeTracking.publishCTCollection(
			ctCollection.body.id
		);

		await page.reload();

		await collectionsPage.goto(site.friendlyUrlPath);

		await expect(page.getByText(newManualAssetListEntryName)).toBeVisible();
		await expect(
			page.getByText(newDynamicAssetListEntryName)
		).toBeVisible();
	});
});

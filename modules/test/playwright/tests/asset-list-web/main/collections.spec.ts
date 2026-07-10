/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {collectionsPagesTest} from '../../../fixtures/collectionsPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';

const test = mergeTests(
	loginTest(),
	isolatedSiteTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-39304': {enabled: true},
		'LPD-78863': {enabled: true, system: true},
	}),
	collectionsPagesTest
);

test.describe('Manual Collection', () => {
	test('Only the selected item types are offered when adding assets', {tag: '@LPS-143093'}, async ({
		collectionsPage,
		page,
		site,
	}) => {
		const excludedTypes = ['Blogs Entry', 'Web Content Article'];
		const includedTypes = [
			'Calendar Event',
			'Translation',
			'Web Content Folder',
		];

		await test.step('Create a manual collection', async () => {
			await collectionsPage.goto(site.friendlyUrlPath);

			await collectionsPage.addNewManualCollection(getRandomString());
		});

		await test.step('Restrict the collection to every item type except Blogs Entry and Web Content Article', async () => {
			await collectionsPage.restrictManualCollectionItemTypes(
				excludedTypes
			);
		});

		await test.step('Open the Type filter in the asset entries item selector', async () => {
			const modal = await collectionsPage.openSelectItemsModal();

			await modal.getByRole('button', {name: 'Filter'}).click();

			await page.getByRole('menuitem', {name: 'Type'}).click();
		});

		await test.step('Only the selected item types are offered', async () => {
			for (const includedType of includedTypes) {
				await expect(
					page.getByRole('checkbox', {exact: true, name: includedType})
				).toBeVisible();
			}

			for (const excludedType of excludedTypes) {
				await expect(
					page.getByRole('checkbox', {exact: true, name: excludedType})
				).toHaveCount(0);
			}
		});
	});

	test('Displays the selected content', {tag: '@LPS-143093'}, async ({
		apiHelpers,
		collectionsPage,
		page,
		site,
	}) => {
		const webContentTitle = getRandomString();

		await test.step('Create a web content article', async () => {
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: await getBasicWebContentStructureId(apiHelpers),
				groupId: site.id,
				titleMap: {en_US: webContentTitle},
			});
		});

		await test.step('Create a manual collection of Web Content Articles', async () => {
			await collectionsPage.goto(site.friendlyUrlPath);

			await collectionsPage.addNewManualCollection(getRandomString());

			await collectionsPage.configureManualCollectionItemType({
				itemSubtype: 'Basic Web Content',
				itemType: 'Web Content Article',
			});
		});

		await test.step('Select the web content article', async () => {
			await collectionsPage.selectAssets([webContentTitle]);
		});

		await test.step('The selected web content article is displayed in the collection', async () => {
			await expect(
				page.getByRole('cell', {name: webContentTitle}).first()
			).toBeVisible();
		});
	});

	test('Can still be edited after deleting a segment used by a variation', {tag: '@LPS-98466'}, async ({
		apiHelpers,
		collectionsPage,
		page,
		site,
	}) => {
		const collectionName = getRandomString();
		const segmentName = getRandomString();
		const webContentTitle = getRandomString();

		let segmentsEntryId: string;

		await test.step('Create a web content article and a segment', async () => {
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: await getBasicWebContentStructureId(apiHelpers),
				groupId: site.id,
				titleMap: {en_US: webContentTitle},
			});

			const segment =
				await apiHelpers.jsonWebServicesSegmentsEntry.addSegmentsEntry({
					criteria: {
						criteria: {
							user: {
								conjunction: 'and',
								filterString: `(firstName eq 'Test')`,
								typeValue: 'model',
							},
						},
						filterString: {model: `(firstName eq 'Test')`},
					},
					groupId: site.id,
					name: segmentName,
					source: 'DEFAULT',
				});

			segmentsEntryId = segment.segmentsEntryId;
		});

		await test.step('Create a manual collection with a personalized variation for the segment', async () => {
			await collectionsPage.goto(site.friendlyUrlPath);

			await collectionsPage.addNewManualCollection(collectionName);

			await collectionsPage.configureManualCollectionItemType({
				itemSubtype: 'All Subtypes',
				itemType: 'Web Content Article',
			});

			await collectionsPage.addPersonalizedVariation(segmentName);
		});

		await test.step('Delete the segment used by the variation', async () => {
			await apiHelpers.jsonWebServicesSegmentsEntry.deleteSegmentsEntry(
				segmentsEntryId
			);
		});

		await test.step('The collection can still be edited and assets selected', async () => {
			await collectionsPage.goto(site.friendlyUrlPath);

			await collectionsPage.openCollection(collectionName);

			await collectionsPage.selectAssets([webContentTitle]);

			await expect(
				page.getByRole('cell', {name: webContentTitle}).first()
			).toBeVisible();
		});
	});
});

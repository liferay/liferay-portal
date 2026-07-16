/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {assetPublisherPagesTest} from '../../../../fixtures/assetPublisherPagesTest';
import {assetPublisherWidgetPagesTest} from '../../../../fixtures/assetPublisherWidgetPagesTest';
import {collectionsPagesTest} from '../../../../fixtures/collectionsPagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageViewModePagesTest} from '../../../../fixtures/pageViewModePagesTest';
import getRandomString from '../../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../../utils/structured-content/getBasicWebContentStructureId';

const test = mergeTests(
	loginTest(),
	isolatedSiteTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-39304': {enabled: true}, // Asset Selection for Asset Publisher
		'LPD-78863': {enabled: true, system: true}, // DXP Segments
	}),
	assetPublisherPagesTest,
	assetPublisherWidgetPagesTest,
	collectionsPagesTest,
	pageViewModePagesTest
);

test.describe('Personalized Variation', () => {
	test('A personalized manual collection can be used in an Asset Publisher', {tag: '@LPS-93726'}, async ({
		apiHelpers,
		assetPublisherPage,
		assetPublisherWidgetPage,
		collectionsPage,
		page,
		site,
		widgetPagePage,
	}) => {
		const collectionName = getRandomString();
		const segmentName = getRandomString();
		const webContentTitle = getRandomString();

		await test.step('Create a web content article and a segment', async () => {
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: await getBasicWebContentStructureId(apiHelpers),
				groupId: site.id,
				titleMap: {en_US: webContentTitle},
			});

			await apiHelpers.jsonWebServicesSegmentsEntry.addSegmentsEntry({
				criteria: {
					criteria: {
						user: {
							conjunction: 'and',
							filterString: `(screenName eq 'test')`,
							typeValue: 'model',
						},
					},
					filterString: {model: `(screenName eq 'test')`},
				},
				groupId: site.id,
				name: segmentName,
				source: 'DEFAULT',
			});
		});

		await test.step('Create a manual collection with a personalized variation', async () => {
			await collectionsPage.goto(site.friendlyUrlPath);

			await collectionsPage.addNewManualCollection(collectionName);

			await collectionsPage.configureSourceItemType({
				itemSubtype: 'All Subtypes',
				itemType: 'Web Content Article',
			});

			await collectionsPage.addPersonalizedVariation(segmentName);

			await collectionsPage.selectAssets([webContentTitle]);

			await collectionsPage.deprioritizeVariation('Anyone');
		});

		await test.step('Use the collection in an Asset Publisher', async () => {
			await assetPublisherWidgetPage.addAssetPublisherPortlet(site);

			await widgetPagePage.clickOnAction(
				'Asset Publisher',
				'Configuration'
			);

			await assetPublisherWidgetPage.selectCollection(collectionName);

			await assetPublisherPage.saveConfiguration();

			await assetPublisherPage.closeConfiguration();
		});

		await test.step('The web content article is shown in the Asset Publisher', async () => {
			await expect(
				page.getByText(webContentTitle).filter({visible: true})
			).toBeVisible();
		});
	});

	test('The same web content can be assigned to more than one variation', {tag: '@LPS-93726'}, async ({
		apiHelpers,
		assetPublisherPage,
		assetPublisherWidgetPage,
		collectionsPage,
		page,
		site,
		widgetPagePage,
	}) => {
		const collectionName = getRandomString();
		const segmentName = getRandomString();
		const webContentTitle = getRandomString();

		let publicURL: string;

		await test.step('Create a web content article and a segment', async () => {
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: await getBasicWebContentStructureId(apiHelpers),
				groupId: site.id,
				titleMap: {en_US: webContentTitle},
			});

			await apiHelpers.jsonWebServicesSegmentsEntry.addSegmentsEntry({
				criteria: {
					criteria: {
						user: {
							conjunction: 'and',
							filterString: `(screenName eq 'test')`,
							typeValue: 'model',
						},
					},
					filterString: {model: `(screenName eq 'test')`},
				},
				groupId: site.id,
				name: segmentName,
				source: 'DEFAULT',
			});
		});

		await test.step('Add the web content to both the default and a personalized variation', async () => {
			await collectionsPage.goto(site.friendlyUrlPath);

			await collectionsPage.addNewManualCollection(collectionName);

			await collectionsPage.configureSourceItemType({
				itemSubtype: 'All Subtypes',
				itemType: 'Web Content Article',
			});

			await collectionsPage.selectAssets([webContentTitle]);

			await collectionsPage.addPersonalizedVariation(segmentName);

			await collectionsPage.selectAssets([webContentTitle]);
		});

		await test.step('Use the collection in an Asset Publisher', async () => {
			const layout =
				await assetPublisherWidgetPage.addAssetPublisherPortlet(site);

			publicURL = `${new URL(page.url()).origin}/web${
				site.friendlyUrlPath
			}${layout.friendlyURL}`;

			await widgetPagePage.clickOnAction(
				'Asset Publisher',
				'Configuration'
			);

			await assetPublisherWidgetPage.selectCollection(collectionName);

			await assetPublisherPage.saveConfiguration();

			await assetPublisherPage.closeConfiguration();
		});

		await test.step('The content is shown in the Asset Publisher', async () => {
			await page.goto(publicURL);

			await expect(
				page.getByText(webContentTitle).filter({visible: true})
			).toBeVisible();
		});
	});
});

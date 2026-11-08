/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {assetPublisherPagesTest} from '../../../fixtures/assetPublisherPagesTest';
import {assetPublisherWidgetPagesTest} from '../../../fixtures/assetPublisherWidgetPagesTest';
import {collectionsPagesTest} from '../../../fixtures/collectionsPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {AssetPublisherPage} from '../../../pages/asset-publisher-web/AssetPublisherPage';
import {AssetPublisherWidgetPage} from '../../../pages/asset-publisher-web/AssetPublisherWidgetPage';
import {WidgetPagePage} from '../../../pages/layout-admin-web/WidgetPagePage';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import getDataStructureDefinition from '../../journal-web/main/utils/getDataStructureDefinition';
import {depotsPagesTest} from './fixtures/depotsPagesTest';
import addConnectedDepot from './utils/addConnectedDepot';
import addEditableDepot from './utils/addEditableDepot';
import gotoDepotInItemSelector from './utils/gotoDepotInItemSelector';

const test = mergeTests(
	assetPublisherPagesTest,
	assetPublisherWidgetPagesTest,
	collectionsPagesTest,
	dataApiHelpersTest,
	depotsPagesTest,
	isolatedSiteTest,
	loginTest(),
	pageViewModePagesTest
);

const IMAGE_PATH = path.join(__dirname, 'dependencies/Document.jpg');

async function selectDepotCollection({
	assetPublisherPage,
	assetPublisherWidgetPage,
	collectionName,
	depotName,
	layout,
	site,
	widgetPagePage,
}: {
	assetPublisherPage: AssetPublisherPage;
	assetPublisherWidgetPage: AssetPublisherWidgetPage;
	collectionName: string;
	depotName: string;
	layout: Layout;
	site: Site;
	widgetPagePage: WidgetPagePage;
}) {
	await widgetPagePage.goto(layout, site.friendlyUrlPath);

	await widgetPagePage.clickOnAction('Asset Publisher', 'Configuration');

	await assetPublisherWidgetPage.collectionInput.click();

	await gotoDepotInItemSelector(
		assetPublisherWidgetPage.collectionSelectorIframe,
		depotName
	);

	await assetPublisherWidgetPage.collectionSelectorIframe
		.getByRole('button', {name: collectionName})
		.click();

	await expect(assetPublisherWidgetPage.collectionInput).toHaveValue(
		collectionName
	);

	await assetPublisherPage.saveConfiguration();

	await assetPublisherPage.closeConfiguration();

	await widgetPagePage.goto(layout, site.friendlyUrlPath);
}

test(
	'An Asset Publisher on a connected site shows the collections of an asset library',
	{tag: '@LPD-112512'},
	async ({
		apiHelpers,
		assetPublisherPage,
		assetPublisherWidgetPage,
		collectionsPage,
		page,
		site,
		widgetPagePage,
	}) => {
		const depot = await addConnectedDepot(apiHelpers, site);

		const depotURL = `/asset-library-${depot.id}`;

		const imageTitle = getRandomString();

		const image = await apiHelpers.headlessDelivery.postDocument(
			depot.siteId,
			createReadStream(IMAGE_PATH),
			{fileName: `${imageTitle}.jpg`, title: imageTitle}
		);

		const ddmStructureId = await getBasicWebContentStructureId(apiHelpers);

		const tag = getRandomString().toLowerCase();

		const taggedWebContentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postStructuredContent({
			contentStructureId: ddmStructureId,
			datePublished: new Date().toISOString(),
			siteId: String(depot.siteId),
			tags: [tag],
			title: taggedWebContentTitle,
		});
		const webContentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postStructuredContent({
			contentStructureId: ddmStructureId,
			datePublished: new Date().toISOString(),
			siteId: String(depot.siteId),
			tags: [getRandomString().toLowerCase()],
			title: webContentTitle,
		});

		const manualCollectionName = getRandomString();

		await test.step('Add a manual collection with an image', async () => {
			await collectionsPage.goto(depotURL);

			await collectionsPage.addNewManualCollection(manualCollectionName);

			await collectionsPage.configureSourceItemType({
				itemSubtype: 'Basic Document',
				itemType: 'Document',
			});

			await collectionsPage.save();

			await collectionsPage.selectAssets([imageTitle]);
		});

		const dynamicCollectionName = getRandomString();

		await test.step('Add a dynamic collection filtered by a tag', async () => {
			await collectionsPage.goto(depotURL);

			await collectionsPage.addNewDynamicCollection(
				dynamicCollectionName
			);

			await collectionsPage.configureSourceItemType({
				itemSubtype: 'All Subtypes',
				itemType: 'Web Content Article',
			});

			await collectionsPage.addFilterConditions([
				{
					field: 'Tags',
					operator: 'Contains',
					quantifier: 'Any of the Following',
				},
			]);

			await collectionsPage
				.getFilterConditionRow(0)
				.getByRole('button', {name: 'Select Tags'})
				.click();

			await page
				.frameLocator('iframe[title="Tags"]')
				.getByRole('row')
				.filter({hasText: tag})
				.getByRole('checkbox')
				.check();

			await page.getByRole('button', {exact: true, name: 'Done'}).click();

			await expect(
				collectionsPage.getFilterConditionRow(0).getByText(tag)
			).toBeVisible();

			await collectionsPage.save();
		});

		const layout =
			await assetPublisherWidgetPage.addAssetPublisherPortlet(site);

		for (const {collectionName, hidden, visible} of [
			{
				collectionName: manualCollectionName,
				hidden: taggedWebContentTitle,
				visible: imageTitle,
			},
			{
				collectionName: dynamicCollectionName,
				hidden: webContentTitle,
				visible: taggedWebContentTitle,
			},
		]) {
			await test.step(`Show ${collectionName}`, async () => {
				await selectDepotCollection({
					assetPublisherPage,
					assetPublisherWidgetPage,
					collectionName,
					depotName: depot.name,
					layout,
					site,
					widgetPagePage,
				});

				await expect(
					page.getByText(visible).filter({visible: true})
				).toBeVisible();
				await expect(page.getByText(hidden)).toHaveCount(0);
			});
		}

		await test.step('Move the image to a folder', async () => {
			const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
				depot.siteId
			);

			await apiHelpers.jsonWebServicesDocumentLibrary.moveFileEntry(
				image.id,
				folder.id
			);

			await selectDepotCollection({
				assetPublisherPage,
				assetPublisherWidgetPage,
				collectionName: manualCollectionName,
				depotName: depot.name,
				layout,
				site,
				widgetPagePage,
			});

			await expect(
				page.getByText(imageTitle).filter({visible: true})
			).toBeVisible();
		});
	}
);

test(
	'An Asset Publisher cannot select the collections of an asset library that disables Collections',
	{tag: '@LPD-112512'},
	async ({
		apiHelpers,
		assetPublisherWidgetPage,
		depotAdminPage,
		site,
		widgetPagePage,
	}) => {
		const depot = await addEditableDepot(apiHelpers);

		await apiHelpers.headlessAssetLibrary.connectSite(
			depot.externalReferenceCode,
			site.externalReferenceCode
		);

		await apiHelpers.jsonWebServicesAssetListEntry.addManualAssetListEntry({
			groupId: String(depot.groupId),
			title: getRandomString(),
		});

		await depotAdminPage.setApplications(
			depot.depotEntryId,
			['Collections'],
			false
		);

		await assetPublisherWidgetPage.addAssetPublisherPortlet(site);

		await widgetPagePage.clickOnAction('Asset Publisher', 'Configuration');

		await assetPublisherWidgetPage.collectionInput.click();

		await gotoDepotInItemSelector(
			assetPublisherWidgetPage.collectionSelectorIframe,
			depot.name
		);

		await expect(
			assetPublisherWidgetPage.collectionSelectorIframe.getByText(
				'This application is not supported for Asset Libraries.'
			)
		).toBeVisible();
	}
);

test(
	'Manual collections select asset library content by its document types and structures',
	{tag: '@LPD-112512'},
	async ({apiHelpers, collectionsPage, page, site}) => {
		const depot = await addConnectedDepot(apiHelpers, site, {
			ddmStructuresAvailable: true,
		});

		const depotURL = `/asset-library-${depot.id}`;

		const documentTypeName = getRandomString();

		await apiHelpers.headlessDelivery.postSiteDocumentDataDefinitionType(
			depot.siteId,
			documentTypeName
		);

		const documentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postDocument(
			depot.siteId,
			createReadStream(IMAGE_PATH),
			{
				documentType: {name: documentTypeName},
				fileName: `${documentTitle}.jpg`,
				title: documentTitle,
			}
		);

		const structureName = getRandomString();

		const structure = await apiHelpers.dataEngine.createStructure(
			depot.siteId,
			getDataStructureDefinition({
				defaultLanguageId: 'en_US',
				fields: [{name: 'Text'}],
				name: structureName,
			})
		);

		const webContentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postStructuredContent({
			contentFields: [
				{
					contentFieldValue: {data: getRandomString()},
					name: 'Text',
				},
			],
			contentStructureId: Number(structure.id),
			datePublished: new Date().toISOString(),
			siteId: String(depot.siteId),
			title: webContentTitle,
		});

		for (const {itemSubtype, itemType, siteURL, title} of [
			{
				itemSubtype: documentTypeName,
				itemType: 'Document',
				siteURL: depotURL,
				title: documentTitle,
			},
			{
				itemSubtype: structureName,
				itemType: 'Web Content Article',
				siteURL: depotURL,
				title: webContentTitle,
			},
			{
				itemSubtype: structureName,
				itemType: 'Web Content Article',
				siteURL: site.friendlyUrlPath,
				title: webContentTitle,
			},
		]) {
			await test.step(`Select ${title} from ${siteURL}`, async () => {
				await collectionsPage.goto(siteURL);

				await collectionsPage.addNewManualCollection(getRandomString());

				await collectionsPage.configureSourceItemType({
					itemSubtype,
					itemType,
				});

				await collectionsPage.save();

				const selectItemsModal =
					await collectionsPage.openSelectItemsModal();

				await selectItemsModal
					.getByRole('checkbox', {name: `Select ${title}`})
					.check();

				await selectItemsModal
					.locator('.modal-footer')
					.getByRole('button', {exact: true, name: 'Select'})
					.click();

				await expect(
					page.getByText(title, {exact: true})
				).toBeVisible();
			});
		}
	}
);

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {assetPublisherPagesTest} from '../../../fixtures/assetPublisherPagesTest';
import {assetPublisherWidgetPagesTest} from '../../../fixtures/assetPublisherWidgetPagesTest';
import {collectionsPagesTest} from '../../../fixtures/collectionsPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';

const test = mergeTests(
	assetPublisherPagesTest,
	assetPublisherWidgetPagesTest,
	apiHelpersTest,
	collectionsPagesTest,
	featureFlagsTest({
		'LPD-39304': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageViewModePagesTest,
	pageEditorPagesTest
);

test(
	'Create manual collection from asset publisher configuration',
	{
		tag: '@LPD-32724',
	},
	async ({
		apiHelpers,
		assetPublisherPage,
		collectionsPage,
		page,
		pageEditorPage,
		site,
	}) => {

		// Create a page with an Asset Publisher Widget

		const widgetId = getRandomString();

		const widgetDefinition = getWidgetDefinition({
			id: widgetId,
			widgetName:
				'com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([widgetDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		// Access to the configuration of the widget from the page editor

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.goToWidgetConfiguration(widgetId);

		// Set asset selection and create a manual collection

		const collectionName = getRandomString();

		await assetPublisherPage.changeAssetSelection('Manual');

		await assetPublisherPage.createCollectionFromAssetPublisher(
			collectionName
		);

		// Check that the collection has been created correctly

		await collectionsPage.goto(site.friendlyUrlPath);

		await expect(page.getByText(collectionName)).toBeVisible();
	}
);

test(
	'Add file to documents and media through asset publisher',
	{
		tag: '@LPD-33784',
	},
	async ({
		assetPublisherPage,
		assetPublisherWidgetPage,
		page,
		site,
		widgetPagePage,
	}) => {

		// Create a page with an Asset Publisher Widget

		const layout =
			await assetPublisherWidgetPage.addAssetPublisherPortlet(site);

		// Access to the configuration of the widget from the page editor

		await widgetPagePage.clickOnAction('Asset Publisher', 'Configuration');

		// Set asset selection and create a manual collection

		const fileName = getRandomString();

		await assetPublisherPage.changeAssetSelection('Dynamic');

		await page.locator('.modal').getByLabel('Close', {exact: true}).click();

		await page
			.locator('.portlet-topper', {hasText: 'Asset Publisher'})
			.getByTitle('Add')
			.click();

		await page.getByRole('menuitem', {name: 'Basic Document'}).click();

		await assetPublisherPage.addFileFromAssetPublisher(fileName);

		// Check that the file has been created correctly and we have been redirected.

		await expect(page).toHaveURL(new RegExp(layout.friendlyURL + '$'));

		await expect(page.getByText(fileName)).toBeVisible();
	}
);

test(
	'Create dynamic collection from asset publisher configuration',
	{
		tag: '@LPD-32724',
	},
	async ({
		apiHelpers,
		assetPublisherPage,
		collectionsPage,
		page,
		pageEditorPage,
		site,
	}) => {

		// Create a page with an Asset Publisher Widget

		const widgetId = getRandomString();

		const widgetDefinition = getWidgetDefinition({
			id: widgetId,
			widgetName:
				'com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([widgetDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		// Access to the configuration of the widget from the page editor

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.goToWidgetConfiguration(widgetId);

		// Set asset selection and create a manual collection

		const collectionName = getRandomString();

		await assetPublisherPage.changeAssetSelection('Dynamic');

		await assetPublisherPage.createCollectionFromAssetPublisher(
			collectionName
		);

		// Check that the collection has been created correctly

		await collectionsPage.goto(site.friendlyUrlPath);

		await expect(page.getByText(collectionName)).toBeVisible();
	}
);

test(
	'Check Asset Publisher configuration, elements are visible and active',
	{
		tag: '@LPD-70684',
	},
	async ({apiHelpers, assetPublisherPage, page, pageEditorPage, site}) => {
		const widgetId = getRandomString();

		const widgetDefinition = getWidgetDefinition({
			id: widgetId,
			widgetName:
				'com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([widgetDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.goToWidgetConfiguration(widgetId);

		const configurationFrame = page.frameLocator(
			'iframe[title="Configuration"]'
		);

		await configurationFrame
			.getByRole('link', {name: 'Communication'})
			.click();

		await assetPublisherPage.assertCommunicationEnabled();

		await configurationFrame
			.getByRole('checkbox', {name: 'lfr-prp-ignore-p_r_p_category'})
			.check();

		await assetPublisherPage.assertCommunicationEnabled(
			['categoryId'],
			false
		);

		await configurationFrame
			.getByRole('checkbox', {name: 'lfr-prp-ignore-p_r_p_reset-cur'})
			.check();

		await assetPublisherPage.assertCommunicationEnabled(
			['resetCur'],
			false
		);

		await assetPublisherPage.assertCommunicationEnabled(
			['assetEntryId', 'tags', 'tag'],
			true
		);
	}
);

test(
	'Metadata move boxes render when configuring an Asset Publisher on a widget page in French',
	{
		tag: '@LPD-97436',
	},
	async ({assetPublisherPage, assetPublisherWidgetPage, page, site}) => {

		// Add an Asset Publisher to a widget page

		const layout =
			await assetPublisherWidgetPage.addAssetPublisherPortlet(site);

		// View the widget page in French

		await page.goto(`/fr/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		// Open the widget configuration ("Facultatif" is French for "Options")

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				exact: true,
				name: 'Configuration',
			}),
			trigger: page
				.locator('.portlet-asset-publisher')
				.getByLabel('Facultatif'),
		});

		// Open the Display Settings panel ("Paramètres d'affichage" in French)

		await assetPublisherPage.configurationIframe
			.getByRole('tab', {name: "Paramètres d'affichage"})
			.click();

		// Expand the metadata section, which is collapsed by default

		await assetPublisherPage.configurationIframe
			.locator('[aria-controls="metadataContent"]')
			.click();

		// Assert a move button rendered in the metadata section

		await expect(
			assetPublisherPage.configurationIframe
				.locator('[id$="metadata"]')
				.getByTestId('ltr')
		).toBeVisible();
	}
);

test(
	'Documents of a global and a site document type can be selected in manual asset selection',
	{
		tag: '@LPD-107305',
	},
	async ({apiHelpers, assetPublisherPage, pageEditorPage, site}) => {

		// Add a document of a global document type and one of a site document type

		const globalSite =
			await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath(
				'global'
			);

		const globalDocumentTypeName = getRandomString();

		const globalDocumentType =
			await apiHelpers.headlessDelivery.postSiteDocumentDataDefinitionType(
				String(globalSite.id),
				globalDocumentTypeName
			);

		const siteDocumentTypeName = getRandomString();

		await apiHelpers.headlessDelivery.postSiteDocumentDataDefinitionType(
			String(site.id),
			siteDocumentTypeName
		);

		const documents = [];

		try {
			for (const documentTypeName of [
				globalDocumentTypeName,
				siteDocumentTypeName,
			]) {
				const title = getRandomString();

				const document = await apiHelpers.headlessDelivery.postDocument(
					site.id,
					{
						buffer: Buffer.from(title),
						mimeType: 'text/plain',
						name: `${title}.txt`,
					},
					{
						documentType: {name: documentTypeName},
						fileName: `${title}.txt`,
						title,
					}
				);

				documents.push({documentTypeName, id: document.id, title});
			}

			// Open the manual asset selection

			const widgetId = getRandomString();

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					getWidgetDefinition({
						id: widgetId,
						widgetName:
							'com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet',
					}),
				]),
				siteId: site.id,
				title: getRandomString(),
			});

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			await pageEditorPage.goToWidgetConfiguration(widgetId);

			await assetPublisherPage.changeAssetSelection('Manual');

			const assetEntries =
				assetPublisherPage.configurationIframe.getByRole('group', {
					name: 'Asset Entries',
				});

			await assetEntries.getByRole('button', {name: 'Select'}).click();

			// Both documents are listed with their document type

			const selectAssetDialog =
				assetPublisherPage.configurationIframe.getByRole('dialog', {
					name: 'Select Asset',
				});

			for (const {documentTypeName, title} of documents) {
				const row = selectAssetDialog
					.getByRole('row')
					.filter({hasText: title});

				await expect(row).toContainText(documentTypeName);

				await row.getByRole('checkbox').check();
			}

			await selectAssetDialog
				.getByRole('button', {exact: true, name: 'Select'})
				.click();

			// Both documents are selected

			for (const {title} of documents) {
				await expect(assetEntries.getByText(title)).toBeVisible();
			}
		}
		finally {
			for (const {id} of documents) {
				await apiHelpers.headlessDelivery.deleteDocument(id);
			}

			await apiHelpers.headlessDelivery.deleteDocumentDataDefinitionType(
				globalDocumentType.id
			);
		}
	}
);

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

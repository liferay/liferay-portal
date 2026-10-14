/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {assetPublisherPagesTest} from '../../../fixtures/assetPublisherPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {SiteSettingsPage} from '../../../pages/site-admin-web/SiteSettingsPage';
import getRandomString from '../../../utils/getRandomString';
import getDataStructureDefinition from '../../journal-web/main/utils/getDataStructureDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';

const test = mergeTests(
	apiHelpersTest,
	assetPublisherPagesTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
);

test(
	'Map display template shows the asset title and summary as text',
	{
		tag: '@LPD-109884',
	},
	async ({apiHelpers, assetPublisherPage, page, pageEditorPage, site}) => {

		// Use OpenStreetMap, whose marker does not depend on external scripts

		const siteSettingsPage = new SiteSettingsPage(page);

		await siteSettingsPage.goToSiteSetting(
			'Maps',
			'Maps',
			site.friendlyUrlPath
		);

		await page.getByLabel('OpenStreetMap', {exact: true}).check();

		await siteSettingsPage.saveConfiguration();

		// Add a web content with a location and markup in its title and summary,
		// whose description is plain text that the summary decodes

		const structure = await apiHelpers.dataEngine.createStructure(
			site.id,
			getDataStructureDefinition({
				defaultLanguageId: 'en_US',
				fields: [{fieldType: 'geolocation', name: 'geolocation'}],
				name: getRandomString(),
			})
		);

		const summary = getRandomString();
		const title = `<i>${getRandomString()}</i>`;

		await apiHelpers.headlessDelivery.postStructuredContent({
			contentFields: [
				{
					contentFieldValue: {
						geo: {latitude: 47.4979, longitude: 19.0402},
					},
					name: 'geolocation',
				},
			],
			contentStructureId: Number(structure.id),
			datePublished: '2024-01-01T00:00:00Z',
			description: `&lt;i&gt;${summary}&lt;/i&gt;`,
			siteId: site.id,
			title,
			viewableBy: 'Anyone',
		});

		// Add an Asset Publisher with the Map display template

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

		await assetPublisherPage.openAssetSelectionTab();

		await assetPublisherPage.selectCollectionProvider('Recent Content');

		await assetPublisherPage.openDisplaySettingsTab();

		await assetPublisherPage.selectDisplayTemplate('Map');

		await assetPublisherPage.saveConfiguration();

		await assetPublisherPage.closeConfiguration();

		await pageEditorPage.publishPage();

		// Check that the popup of the marker shows the title and summary as text

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await page.locator('.leaflet-marker-icon').click();

		const popup = page.locator('.leaflet-popup-content');

		await expect(popup.getByRole('heading', {level: 3})).toHaveText(title);

		await expect(popup).toContainText(`<i>${summary}</i>`);

		await expect(popup.locator('i')).toHaveCount(0);
	}
);

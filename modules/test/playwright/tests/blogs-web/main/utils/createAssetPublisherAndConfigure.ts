/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

import {AssetPublisherPage} from '../../../../pages/asset-publisher-web/AssetPublisherPage';
import {PageEditorPage} from '../../../../pages/layout-content-page-editor-web/PageEditorPage';
import getRandomString from '../../../../utils/getRandomString';
import {openFieldset} from '../../../../utils/openFieldset';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';

import type {ApiHelpers} from '../../../../helpers/ApiHelpers';

export async function createAssetPublisherAndConfigure({
	apiHelpers,
	page,
	site,
}: {
	apiHelpers: ApiHelpers;
	page: Page;
	site: Site;
}) {
	const assetPublisherPage = new AssetPublisherPage(page);
	const pageEditorPage = new PageEditorPage(page);
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

	const configurationIframe = await assetPublisherPage.configurationIframe;

	await pageEditorPage.goToWidgetConfiguration(widgetId);
	await configurationIframe.locator('.portlet-body').waitFor();

	await assetPublisherPage.changeAssetSelection('Dynamic');

	await openFieldset(configurationIframe, 'Source');
	await configurationIframe.getByLabel('Asset Type').selectOption({
		label: 'Blogs Entry',
	});

	await assetPublisherPage.saveConfiguration();
	await page
		.locator('.modal-header')
		.getByLabel('Close', {exact: true})
		.click();

	await pageEditorPage.publishPage();
}

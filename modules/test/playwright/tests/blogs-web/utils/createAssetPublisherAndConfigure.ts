/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {openFieldset} from '../../../utils/openFieldset';
import {waitForAlert} from '../../../utils/waitForAlert';
import getPageDefinition from '../../layout-content-page-editor-web/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/utils/getWidgetDefinition';

import type {ApiHelpers} from '../../../helpers/ApiHelpers';
import type {PageEditorPage} from '../../../pages/layout-content-page-editor-web/PageEditorPage';

export async function createAssetPublisherAndConfigure({
	apiHelpers,
	page,
	pageEditorPage,
	site,
}: {
	apiHelpers: ApiHelpers;
	page: Page;
	pageEditorPage: PageEditorPage;
	site: Site;
}) {
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

	const topper = pageEditorPage.getTopper(widgetId);
	await topper.hover();
	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByRole('menuitem', {
			exact: true,
			name: 'Configuration',
		}),
		trigger: topper.locator('.portlet-options'),
	});

	const configurationModal = await page.frameLocator(
		'iframe[title*="Asset Publisher"][title*="Configuration"]'
	);
	await configurationModal.locator('.portlet-body').waitFor();

	const configurationDynamicInput = await configurationModal.getByLabel(
		'Dynamic',
		{exact: true}
	);
	if (await configurationDynamicInput.isHidden()) {
		await configurationModal
			.getByRole('link', {name: 'Asset Selection'})
			.click();
	}
	if (!(await configurationDynamicInput.isChecked())) {
		await configurationDynamicInput.click();

		await waitForAlert(
			configurationModal,
			'Success:You have successfully updated the setup.'
		);
	}

	await openFieldset(configurationModal, 'Source');
	await configurationModal.getByLabel('Asset Type').selectOption({
		label: 'Blogs Entry',
	});

	await configurationModal.getByRole('button', {name: 'Save'}).click();
	await waitForAlert(
		configurationModal,
		'Success:You have successfully updated the setup.'
	);
	await page.getByLabel('close', {exact: true}).click();

	await pageEditorPage.publishPage();
}

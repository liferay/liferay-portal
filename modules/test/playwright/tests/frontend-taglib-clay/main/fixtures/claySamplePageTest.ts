/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {liferayConfig} from '../../../../liferay.config';
import getRandomString from '../../../../utils/getRandomString';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {ClaySamplePage} from '../pages/ClaySamplePage';

export const test = mergeTests(apiHelpersTest, isolatedSiteTest, loginTest());

const claySamplePageTest = test.extend<{
	claySamplePage: ClaySamplePage;
}>({
	claySamplePage: async ({apiHelpers, page, site}, use) => {
		const url = await setupClaySampleWidget(apiHelpers, page, site);

		const claySamplePage = new ClaySamplePage(page, url);

		await claySamplePage.goto();

		await use(claySamplePage);
	},
});

async function setupClaySampleWidget(apiHelpers, page, site) {
	const widgetDefinition = getWidgetDefinition({
		id: getRandomString(),
		widgetName: 'com_liferay_clay_sample_web_portlet_ClaySamplePortlet',
	});

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([widgetDefinition]),
		siteId: site.id,
		title: getRandomString(),
	});

	return `${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`;
}

export {claySamplePageTest};

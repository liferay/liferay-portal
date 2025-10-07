/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests} from '@playwright/test';

import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {liferayConfig} from '../../../../liferay.config';
import getRandomString from '../../../../utils/getRandomString';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {SamplePage} from '../pages/SamplePage';

const test = mergeTests(isolatedSiteTest, loginTest());

const samplePageTest = test.extend<{
	samplePage: SamplePage;
}>({
	samplePage: async ({page, site}, use) => {
		const url = await setupSampleWidget(new ApiHelpers(page), site);

		const samplePage = new SamplePage(page, url);

		await samplePage.goto();

		await use(samplePage);
	},
});

async function setupSampleWidget(apiHelpers, site) {
	const widgetDefinition = getWidgetDefinition({
		id: getRandomString(),
		widgetName:
			'com_liferay_frontend_taglib_sample_web_portlet_SamplePortlet',
	});

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([widgetDefinition]),
		siteId: site.id,
		title: getRandomString(),
	});

	return `${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`;
}

export {samplePageTest};

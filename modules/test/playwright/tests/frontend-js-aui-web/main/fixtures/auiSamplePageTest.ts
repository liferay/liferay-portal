/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {liferayConfig} from '../../../../liferay.config';
import getRandomString from '../../../../utils/getRandomString';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {AUISamplePage} from '../pages/AUISamplePage';

const auiSamplePageTest = isolatedSiteTest.extend<{
	auiSamplePage: AUISamplePage;
}>({
	auiSamplePage: async ({page, site}, use) => {
		const widgetDefinition = getWidgetDefinition({
			id: getRandomString(),
			widgetName:
				'com_liferay_frontend_js_aui_sample_web_portlet_FrontendJSAUISampleWebPortlet',
		});

		const apiHelpers = new ApiHelpers(page);

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([widgetDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		const auiSamplePage = new AUISamplePage(
			page,
			`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await auiSamplePage.goto();

		await use(auiSamplePage);
	},
});

export {auiSamplePageTest};

/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {liferayConfig} from '../../../../liferay.config';
import getRandomString from '../../../../utils/getRandomString';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {ImageEditorSamplePage} from '../pages/ImageEditorSamplePage';

const imageEditorSamplePageTest = mergeTests(
	isolatedSiteTest,
	featureFlagsTest({'LPS-178052': {enabled: true}}),
	loginTest()
).extend<{
	imageEditorSamplePage: ImageEditorSamplePage;
}>({
	imageEditorSamplePage: async ({page, site}, use) => {
		const widgetDefinition = getWidgetDefinition({
			id: getRandomString(),
			widgetName:
				'com_liferay_frontend_js_image_editor_sample_web_internal_portlet_FrontendJSImageEditorSampleWebPortlet',
		});

		const apiHelpers = new ApiHelpers(page);

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([widgetDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		const imageEditorSamplePage = new ImageEditorSamplePage(
			page,
			`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await imageEditorSamplePage.goto();

		await use(imageEditorSamplePage);
	},
});

export {imageEditorSamplePageTest};

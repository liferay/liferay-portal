/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {liferayConfig} from '../../../../liferay.config';
import getRandomString from '../../../../utils/getRandomString';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';

export class SamplePage {
	readonly apiHelpers: ApiHelpers;
	readonly page: Page;
	readonly tabList: Locator;

	constructor(page: Page) {
		this.apiHelpers = new ApiHelpers(page);
		this.page = page;
		this.tabList = page.getByRole('tab');
	}

	async selectLink(tabName: string) {
		const tabHeading = this.tabList.getByText(tabName);

		await expect(tabHeading).toBeInViewport();

		await tabHeading.click();
	}

	async setupSampleWidget({
		locale = 'en-US',
		site,
	}: {
		locale?: string;
		site: Site;
	}) {
		const widgetDefinition = getWidgetDefinition({
			id: getRandomString(),
			widgetName:
				'com_liferay_frontend_js_clay_sample_web_internal_portlet_FrontendJSClaySampleWebPortlet',
		});

		const layout = await this.apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([widgetDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await this.page.goto(
			`${liferayConfig.environment.baseUrl}/${locale}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
		);
	}
}

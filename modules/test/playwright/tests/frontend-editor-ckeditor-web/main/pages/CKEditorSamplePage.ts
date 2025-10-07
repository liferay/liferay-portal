/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {liferayConfig} from '../../../../liferay.config';
import getRandomString from '../../../../utils/getRandomString';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';

export class CKEditorSamplePage {
	readonly apiHelpers: ApiHelpers;
	readonly page: Page;

	constructor(page: Page) {
		this.apiHelpers = new ApiHelpers(page);
		this.page = page;
	}

	async createAndGotoSitePage({site}: {site: Site}) {
		const widgetDefinition = getWidgetDefinition({
			id: getRandomString(),
			widgetName:
				'com_liferay_editor_ckeditor_sample_web_internal_portlet_CKEditorSamplePortlet',
		});

		const title = getRandomString();

		await this.apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([widgetDefinition]),
			siteId: site.id,
			title,
		});

		await this.page.goto(
			`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}/${title}`
		);

		const productMenuToggle = this.page.getByLabel('Close Product Menu');

		if (await productMenuToggle.isVisible()) {
			await productMenuToggle.click();
		}
	}

	async selectTab(label: string) {
		const navLink = this.page
			.locator('.nav-link')
			.filter({has: this.page.locator(`text="${label}"`)});

		await navLink.click();

		await expect(navLink).toHaveClass(/active/);
	}
}

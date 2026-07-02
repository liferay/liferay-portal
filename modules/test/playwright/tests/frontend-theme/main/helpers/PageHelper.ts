/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {PageEditorPage} from '../../../../pages/layout-content-page-editor-web/PageEditorPage';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';

export class PageHelper {
	constructor(
		private readonly apiHelpers: ApiHelpers,
		private readonly page: Page,
		private readonly pageEditorPage: PageEditorPage,
		private readonly site: Site
	) {}

	async createPage(pageName: string, pageElements: PageElement[] = []) {
		return await this.apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition(pageElements),
			siteId: this.site.id,
			title: pageName,
		});
	}

	async expectPageToUseThemeCss(themeContextPath: string) {
		await expect(
			this.page
				.locator(`link.lfr-css-file[href*="/o/${themeContextPath}/"]`)
				.first()
		).toBeAttached();
	}

	async goToPage(page: Layout) {
		await this.page.goto(
			`/web${this.site.friendlyUrlPath}${page.friendlyUrlPath}`
		);
	}

	async goToPageEditor(page: Layout) {
		await this.pageEditorPage.goto(page, this.site.friendlyUrlPath);
	}
}

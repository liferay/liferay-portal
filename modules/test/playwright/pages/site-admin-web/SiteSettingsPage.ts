/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

import {ProductMenuPage} from '../../pages/product-navigation-control-menu-web/ProductMenuPage';
import { PORTLET_URLS } from '../../utils/portletUrls';

export class SiteSettingsPage {
	readonly page: Page;

	readonly productMenuPage: ProductMenuPage;

	constructor(page: Page) {
		this.page = page;

		this.productMenuPage = new ProductMenuPage(page);
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.siteSettings}`
		);
	}

	async goToSiteSetting(
		categoryKey: string,
		configurationName?: string,
		siteUrl?: Site['friendlyUrlPath']
	) {
		await this.goto(siteUrl);
		await this.page
			.getByRole('link', {
				exact: true,
				name: categoryKey,
			})
			.click();

		if (configurationName) {
			await this.page
				.getByRole('menuitem', {
					exact: true,
					name: configurationName,
				})
				.click();
		}
	}
}

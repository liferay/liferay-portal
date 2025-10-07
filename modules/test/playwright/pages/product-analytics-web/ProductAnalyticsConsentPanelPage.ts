/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

export class ProductAnalyticsConsentPanelPage {
	readonly acceptAllButton: Locator;
	readonly acceptSelectedButton: Locator;
	readonly consentPanelFormLocator: Locator;
	readonly consentPanelIFrame: FrameLocator;
	readonly page: Page;
	readonly panelLocator: Locator;
	readonly useNecessaryCookiesOnlyButton: Locator;

	constructor(page: Page) {
		this.panelLocator = page.locator('#productAnalyticsConsentPanel');

		this.acceptAllButton = this.panelLocator.getByRole('button', {
			exact: true,
			name: 'Accept All',
		});
		this.acceptSelectedButton = this.panelLocator.getByRole('button', {
			exact: true,
			name: 'Accept Selected',
		});
		this.consentPanelFormLocator = page.locator(
			'[id="_com_liferay_my_account_web_portlet_MyAccountPortlet_productAnalyticsConsentPanelForm"]'
		);
		this.consentPanelIFrame = page.frameLocator(
			'iframe[title="Liferay Platform Consent Preferences"]'
		);
		this.page = page;
		this.useNecessaryCookiesOnlyButton = this.panelLocator.getByRole(
			'button',
			{
				exact: true,
				name: 'Use Necessary Cookies Only',
			}
		);
	}

	async getCookieTypeToggle(cookieType: string, useIFrame = true) {
		if (useIFrame) {
			return this.consentPanelIFrame.locator(
				`[data-cookie-key="${cookieType}"]`
			);
		}
		else {
			return this.consentPanelFormLocator.locator(
				`[data-cookie-key="${cookieType}"]`
			);
		}
	}
}

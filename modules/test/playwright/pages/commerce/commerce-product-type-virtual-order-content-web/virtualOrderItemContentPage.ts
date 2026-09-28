/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

export const VIRTUAL_ORDER_ITEM_CONTENT_PORTLET_ID =
	'com_liferay_commerce_product_type_virtual_order_content_web_internal_portlet_CommerceVirtualOrderItemContentPortlet';

export class VirtualOrderItemContentPage {
	readonly downloadButton: Locator;
	readonly page: Page;
	readonly portlet: Locator;
	readonly termsOfUseContent: Locator;
	readonly termsOfUseModalFrame: FrameLocator;

	constructor(page: Page) {
		this.page = page;
		this.portlet = page.locator(
			`#portlet_${VIRTUAL_ORDER_ITEM_CONTENT_PORTLET_ID}`
		);
		this.downloadButton = this.portlet.getByRole('button', {
			name: 'Download',
		});
		this.termsOfUseContent = page.locator('.journal-article-preview');
		this.termsOfUseModalFrame = page.frameLocator('.commerce-modal iframe');
	}

	async gotoTermsOfUse(
		layoutURL: string,
		parameters: {[key: string]: string} = {}
	) {
		const namespace = `_${VIRTUAL_ORDER_ITEM_CONTENT_PORTLET_ID}_`;

		const searchParams = new URLSearchParams({
			[`${namespace}mvcRenderCommandName`]:
				'/commerce_virtual_order_item_content/view_commerce_virtual_order_item_terms_of_use',
			p_p_id: VIRTUAL_ORDER_ITEM_CONTENT_PORTLET_ID,
			p_p_lifecycle: '0',
		});

		for (const [key, value] of Object.entries(parameters)) {
			searchParams.set(`${namespace}${key}`, value);
		}

		await this.page.goto(`${layoutURL}?${searchParams}`);
	}
}

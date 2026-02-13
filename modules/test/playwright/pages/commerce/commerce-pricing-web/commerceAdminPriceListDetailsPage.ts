/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {CommerceDNDTablePage} from '../commerceDNDTablePage';

export class CommerceAdminPriceListDetailsPage extends CommerceDNDTablePage {
	readonly addTierPriceButton: Locator;
	readonly addTierPriceEntryFrame: FrameLocator;
	readonly addTierPriceEntryPrice: Locator;
	readonly addTierPriceEntryQuantity: Locator;
	readonly addTierPriceEntrySaveButton: Locator;
	readonly editPriceTierFrame: FrameLocator;
	readonly editPriceTierPrice: Locator;
	readonly entriesTab: Locator;
	readonly findSkuInput: Locator;
	readonly page: Page;
	readonly scheduleLabel: Locator;
	readonly selectButton: Locator;
	readonly skuLink: (price: string) => Locator;
	readonly skusTableRowLink: (skuName: string) => Locator;

	constructor(page: Page) {
		super(
			page,
			'#_com_liferay_commerce_pricing_web_internal_portlet_CommercePriceListPortlet_fm .fds table'
		);

		this.addTierPriceButton = page
			.frameLocator('iframe')
			.getByTestId('managementToolbar')
			.locator('[data-testid="fdsCreationActionButton"]');
		this.addTierPriceEntryFrame = page.frameLocator('iframe >> nth=1');
		this.addTierPriceEntryPrice = this.addTierPriceEntryFrame.getByLabel(
			'Tier Price Required'
		);
		this.addTierPriceEntryQuantity =
			this.addTierPriceEntryFrame.getByLabel('Quantity Required');
		this.addTierPriceEntrySaveButton =
			this.addTierPriceEntryFrame.getByRole('button', {name: 'Submit'});
		this.editPriceTierFrame = page
			.frameLocator('iframe')
			.frameLocator('iframe');
		this.editPriceTierPrice = this.editPriceTierFrame.getByLabel(
			'Tier Price Required'
		);
		this.entriesTab = page.getByRole('link', {name: 'Entries'});
		this.findSkuInput = page.getByPlaceholder('Find a SKU');
		this.page = page;
		this.scheduleLabel = page.getByText('Schedule');
		this.selectButton = page.getByRole('button', {name: 'Select'});
		this.skuLink = (price: string) =>
			page
				.frameLocator('iframe')
				.first()
				.getByRole('link', {name: price});
		this.skusTableRowLink = (sku: string) =>
			page.getByRole('link', {exact: true, name: sku});
	}
}

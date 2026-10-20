/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';

export class CommerceAdminChannelDetailsProductDisplayPagesPage {
	readonly addDisplayLayoutButton: Locator;
	readonly addDisplayLayoutFrame: FrameLocator;
	readonly displayPageName: Locator;
	readonly doneButton: Locator;
	readonly page: Page;
	readonly productDisplayPagesTab: Locator;
	readonly saveButton: Locator;
	readonly selectProductButton: Locator;
	readonly selectProductDisplayPageFrame: FrameLocator;
	readonly selectProductDisplayPageIframe: Locator;
	readonly selectProductFrame: FrameLocator;
	readonly selectProductIframe: Locator;
	readonly successMessage: Locator;

	constructor(page: Page) {
		this.addDisplayLayoutButton = page.getByLabel('Add Display Layout');
		this.addDisplayLayoutFrame = page.frameLocator('.is-visible iframe');
		this.displayPageName = this.addDisplayLayoutFrame.locator(
			'[id$="displayPageNameInput"]'
		);
		this.doneButton = this.addDisplayLayoutFrame.getByRole('button', {
			exact: true,
			name: 'Done',
		});
		this.page = page;
		this.productDisplayPagesTab = page.getByRole('link', {
			name: 'Product Display Pages',
		});
		this.saveButton = this.addDisplayLayoutFrame.getByRole('button', {
			exact: true,
			name: 'Save',
		});
		this.selectProductButton = this.addDisplayLayoutFrame.getByRole(
			'button',
			{exact: true, name: 'Select Product'}
		);
		this.selectProductDisplayPageIframe =
			this.addDisplayLayoutFrame.locator(
				'iframe[title="Select Product Display Page"]'
			);
		this.selectProductDisplayPageFrame =
			this.selectProductDisplayPageIframe.contentFrame();
		this.selectProductIframe = this.addDisplayLayoutFrame.locator(
			'iframe[title="Select Product"]'
		);
		this.selectProductFrame = this.selectProductIframe.contentFrame();
		this.successMessage = this.addDisplayLayoutFrame.getByText(
			'Success:Your request completed successfully.'
		);
	}

	async chooseDisplayPage(tabName: string) {
		await clickAndExpectToBeVisible({
			target: this.addDisplayLayoutFrame.locator('.nav-link.active', {
				hasText: tabName,
			}),
			trigger: this.addDisplayLayoutFrame.getByRole('link', {
				exact: true,
				name: tabName,
			}),
		});

		await clickAndExpectToBeVisible({
			target: this.selectProductDisplayPageIframe,
			trigger: this.addDisplayLayoutFrame.getByRole('button', {
				exact: true,
				name: 'Choose',
			}),
		});
	}

	async goToProductDisplayPages() {
		await this.productDisplayPagesTab.click();
	}

	async selectProduct(productName: string) {
		await clickAndExpectToBeVisible({
			target: this.selectProductIframe,
			trigger: this.selectProductButton,
		});

		await this.selectProductFrame
			.getByRole('row')
			.filter({hasText: productName})
			.getByRole('button', {name: 'Choose'})
			.click();
	}
}

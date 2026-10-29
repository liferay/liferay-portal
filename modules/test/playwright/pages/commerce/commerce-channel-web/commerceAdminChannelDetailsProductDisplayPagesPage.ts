/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';

export class CommerceAdminChannelDetailsProductDisplayPagesPage {
	readonly addDisplayLayoutButton: Locator;
	readonly addDisplayLayoutFrame: FrameLocator;
	readonly closeButton: Locator;
	readonly displayPageName: Locator;
	readonly doneButton: Locator;
	readonly page: Page;
	readonly productDisplayPagesTab: Locator;
	readonly saveButton: Locator;
	readonly selectProductButton: Locator;
	readonly selectProductDisplayPageFrame: FrameLocator;
	readonly selectProductDisplayPageIframe: Locator;
	readonly selectProductModal: Locator;
	readonly successMessage: Locator;
	readonly tableRow: (productName: string) => Locator;

	constructor(page: Page) {
		this.addDisplayLayoutButton = page.getByLabel('Add Display Layout');
		this.addDisplayLayoutFrame = page.frameLocator('.is-visible iframe');
		this.closeButton = this.addDisplayLayoutFrame
			.locator('.side-panel-iframe-header')
			.getByRole('button');
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
		this.selectProductModal = this.addDisplayLayoutFrame.getByRole(
			'dialog',
			{
				exact: true,
				name: 'Select Product',
			}
		);
		this.successMessage = this.addDisplayLayoutFrame.getByText(
			'Success:Your request completed successfully.'
		);
		this.tableRow = (productName: string) =>
			page
				.locator('.fds table')
				.getByRole('row')
				.filter({hasText: productName});
	}

	async chooseDisplayPage(tabName: string) {
		const chooseButton = this.addDisplayLayoutFrame.getByRole('button', {
			exact: true,
			name: 'Choose',
		});

		await chooseButton.waitFor();

		const tabLink = this.addDisplayLayoutFrame.getByRole('link', {
			exact: true,
			name: tabName,
		});

		if (await tabLink.count()) {
			await clickAndExpectToBeVisible({
				target: this.addDisplayLayoutFrame.locator('.nav-link.active', {
					hasText: tabName,
				}),
				trigger: tabLink,
			});
		}

		await clickAndExpectToBeVisible({
			target: this.selectProductDisplayPageIframe,
			trigger: chooseButton,
		});
	}

	async clickTableRowAction(productName: string, actionName: string) {
		await this.tableRow(productName)
			.getByRole('button', {name: 'Actions'})
			.click();

		await this.page
			.getByRole('menuitem', {exact: true, name: actionName})
			.click();
	}

	async goToProductDisplayPages() {
		await this.productDisplayPagesTab.click();
	}

	async selectLayout(layoutName: string) {
		await this.chooseDisplayPage('Layout');

		await this.selectProductDisplayPageFrame
			.getByText(layoutName, {exact: true})
			.click();

		await this.doneButton.click();
	}

	async selectProduct(productName: string) {
		await clickAndExpectToBeVisible({
			target: this.selectProductModal,
			trigger: this.selectProductButton,
		});

		await this.selectProductModal
			.getByRole('row')
			.filter({hasText: productName})
			.getByRole('radio')
			.check();
		await this.selectProductModal
			.getByRole('button', {exact: true, name: 'Select'})
			.click();

		await expect(this.selectProductModal).toBeHidden();
	}
}

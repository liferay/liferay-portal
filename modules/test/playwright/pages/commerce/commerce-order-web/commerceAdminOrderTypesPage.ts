/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {getRandomInt} from '../../../utils/getRandomInt';
import {waitForAlert} from '../../../utils/waitForAlert';
import {ApplicationsMenuPage} from '../../product-navigation-applications-menu/ApplicationsMenuPage';
import {CommerceAdminOrderTypeDetailsPage} from './commerceAdminOrderTypeDetailsPage';

export class CommerceAdminOrderTypesPage {
	readonly addOrderTypeButton: Locator;
	readonly addOrderTypeFrame: FrameLocator;
	readonly addOrderTypeModalField: (fieldName: string) => Promise<Locator>;
	readonly addOrderTypeModalHeaderTitle: Locator;
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly page: Page;
	readonly pageTitle: Locator;
	readonly submitAddOrderTypeModalButton: Locator;

	constructor(page: Page) {
		this.addOrderTypeButton = page
			.getByRole('button', {
				name: 'Add Order Type',
			})
			.first();
		this.addOrderTypeFrame = page.frameLocator('iframe >> nth=1');
		this.addOrderTypeModalField = async (fieldName: string) => {
			return this.addOrderTypeFrame.getByLabel(fieldName);
		};
		this.addOrderTypeModalHeaderTitle = this.addOrderTypeFrame.getByRole(
			'heading',
			{
				name: 'Add Order Type',
			}
		);
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.page = page;
		this.pageTitle = page
			.getByTestId('headerTitle')
			.filter({hasText: 'Order Types'});
		this.submitAddOrderTypeModalButton = this.addOrderTypeFrame.getByRole(
			'button',
			{
				name: 'Submit',
			}
		);
	}

	async addOrderType(
		apiHelpers: DataApiHelpers,
		active = true,
		name = 'Order Type Name ' + getRandomInt(),
		description = 'Order Type Description ' + getRandomInt()
	) {
		const commerceAdminOrderTypeDetailsPage =
			new CommerceAdminOrderTypeDetailsPage(this.page);

		await this.goto();

		await expect(this.pageTitle).toBeVisible();

		await this.addOrderTypeButton.click();

		await expect(this.addOrderTypeModalHeaderTitle).toBeVisible();

		await (await this.addOrderTypeModalField('Name Required')).fill(name);
		await (
			await this.addOrderTypeModalField('Description')
		).fill(description);

		await this.submitAddOrderTypeModalButton.click();

		await expect(
			commerceAdminOrderTypeDetailsPage.headerDetailsTitle
		).toHaveText(name);

		const orderTypeId =
			await commerceAdminOrderTypeDetailsPage.orderTypeId.textContent();

		apiHelpers.data.push({id: orderTypeId, type: 'orderType'});

		await commerceAdminOrderTypeDetailsPage.activeToggle.setChecked(active);

		await commerceAdminOrderTypeDetailsPage.publishLink.click();

		await waitForAlert(this.page);

		return {orderTypeId, orderTypeName: name};
	}

	async goto() {
		await this.applicationsMenuPage.goToCommerceOrderTypes();
	}
}

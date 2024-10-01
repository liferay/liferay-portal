/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {waitForAlert} from '../../utils/waitForAlert';
import {ApplicationsMenuPage} from '../product-navigation-applications-menu/ApplicationsMenuPage';
import {searchTableRowByValue} from './commerceDNDTablePage';

export class CommerceAdminChannelsPage {
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly buyerOrderApprovalWorkflow: Locator;
	readonly channelsTable: Locator;
	readonly channelsTableRow: (
		colPosition: number,
		value: number | string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly channelsTableRowLink: (channelName: string) => Promise<Locator>;
	readonly commerceSiteType: Locator;
	readonly healthCheckAction: (actionName: string) => Locator;
	readonly headerActions: Locator;
	readonly headerActionsSaveButton: Locator;
	readonly page: Page;
	readonly sellerOrderAcceptanceWorkflow: Locator;
	readonly shippingMethodActiveField: Locator;
	readonly shippingMethodOptionsAddButton: Locator;
	readonly shippingMethodOptionsLink: Locator;
	readonly shippingMethodSaveButton: Locator;
	readonly shippingMethodsPanel: FrameLocator;
	readonly shippingOptionKeyField: Locator;
	readonly shippingOptionNameField: Locator;
	readonly shippingOptionSaveButton: Locator;
	readonly shippingOptionsPanel: FrameLocator;

	constructor(page: Page) {
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.buyerOrderApprovalWorkflow = page.getByLabel(
			'Buyer Order Approval Workflow'
		);
		this.channelsTable = page.locator(
			'#portlet_com_liferay_commerce_channel_web_internal_portlet_CommerceChannelsPortlet .dnd-table'
		);
		this.channelsTableRow = async (
			colPosition: number,
			value: number | string,
			strictEqual: boolean = false
		) => {
			return await searchTableRowByValue(
				this.channelsTable,
				colPosition,
				String(value),
				strictEqual
			);
		};
		this.channelsTableRowLink = async (channelName: string) => {
			const channelsTableRow = await this.channelsTableRow(
				0,
				channelName,
				true
			);

			if (channelsTableRow && channelsTableRow.column) {
				return channelsTableRow.column.getByRole('link', {
					name: String(channelName),
				});
			}

			throw new Error(
				`Cannot locate channel row with name ${channelName}`
			);
		};
		this.commerceSiteType = page.getByLabel('Commerce Site Type');
		this.healthCheckAction = (actionName: string) =>
			page
				.locator('.dnd-tr')
				.filter({has: page.getByText(actionName, {exact: true})})
				.locator('.item-actions .btn');
		this.headerActions = page.locator('.header-actions');
		this.headerActionsSaveButton = this.headerActions.getByText('Save');
		this.page = page;
		this.sellerOrderAcceptanceWorkflow = page.getByLabel(
			'Seller Order Acceptance Workflow'
		);
		this.shippingMethodsPanel = page.frameLocator('iframe').nth(2);

		this.shippingMethodActiveField =
			this.shippingMethodsPanel.getByLabel('Active');
		this.shippingMethodOptionsAddButton = this.shippingMethodsPanel
			.getByTestId('management-toolbar')
			.locator('[data-testid="fdsCreationActionButton"]');
		this.shippingMethodOptionsLink = this.shippingMethodsPanel.getByRole(
			'link',
			{name: 'Shipping Options'}
		);
		this.shippingMethodSaveButton = this.shippingMethodsPanel.getByRole(
			'button',
			{exact: true, name: 'Save'}
		);
		this.shippingOptionsPanel =
			this.shippingMethodsPanel.frameLocator('iframe');

		this.shippingOptionKeyField =
			this.shippingOptionsPanel.getByLabel('Key');
		this.shippingOptionNameField =
			this.shippingOptionsPanel.getByLabel('Name');
		this.shippingOptionSaveButton = this.shippingOptionsPanel.getByRole(
			'button',
			{exact: true, name: 'Save'}
		);
	}

	async goto() {
		await this.applicationsMenuPage.goToCommerceChannels();
	}

	async changeCommerceChannelBuyerOrderApprovalWorkflow(
		buyerOrderApprovalWorkflow: string,
		channelName: string,
		skipNavigation: boolean = false
	) {
		if (!skipNavigation) {
			await this.goto();

			await (await this.channelsTableRowLink(channelName)).click();
		}

		await this.buyerOrderApprovalWorkflow.selectOption({
			label: buyerOrderApprovalWorkflow,
		});
		await this.headerActionsSaveButton.click();

		await waitForAlert(this.page);
	}

	async changeCommerceChannelSellerOrderAcceptanceWorkflow(
		sellerOrderAcceptanceWorkflow: string,
		channelName: string,
		skipNavigation: boolean = false
	) {
		if (!skipNavigation) {
			await this.goto();

			await (await this.channelsTableRowLink(channelName)).click();
		}

		await this.sellerOrderAcceptanceWorkflow.selectOption({
			label: sellerOrderAcceptanceWorkflow,
		});
		await this.headerActionsSaveButton.click();

		await waitForAlert(this.page);
	}

	async changeCommerceChannelSiteType(
		channelName: string,
		siteType: string,
		skipNavigation: boolean = false
	) {
		if (!skipNavigation) {
			await this.goto();

			await (await this.channelsTableRowLink(channelName)).click();
		}

		await this.commerceSiteType.selectOption({label: siteType});
		await this.headerActionsSaveButton.click();
		await this.page.waitForTimeout(200);
	}

	async fixCommerceChannelIssue(
		actionNames: [string],
		channelName,
		skipNavigation: boolean = false
	) {
		if (!skipNavigation) {
			await this.goto();

			await (await this.channelsTableRowLink(channelName)).click();
		}

		return Promise.all(
			actionNames.map((actionName) => {
				this.healthCheckAction(actionName).click();

				this.page.waitForTimeout(200);
			})
		);
	}

	async setupCommerceChannelShippingMethod(
		channelName: string,
		shippingMethodName: string,
		shippingOptions: string[]
	) {
		await this.goto();

		await (await this.channelsTableRowLink(channelName)).click();

		await this.page
			.getByRole('link', {exact: true, name: shippingMethodName})
			.click();
		await this.shippingMethodActiveField.check();
		await this.shippingMethodSaveButton.click();
		await this.shippingMethodOptionsLink.click();
		await this.shippingMethodOptionsAddButton.click();

		for (const shippingOption of shippingOptions) {
			await this.shippingOptionNameField.fill(shippingOption);
			await this.shippingOptionKeyField.fill(shippingOption);
			await this.shippingOptionSaveButton.click();
			await expect(
				this.shippingMethodsPanel.getByText(shippingOption)
			).toBeVisible();
		}
	}
}

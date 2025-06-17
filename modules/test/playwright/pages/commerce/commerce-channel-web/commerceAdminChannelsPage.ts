/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {waitForAlert} from '../../../utils/waitForAlert';
import {ApplicationsMenuPage} from '../../product-navigation-applications-menu/ApplicationsMenuPage';
import {searchTableRowByValue} from '../commerceDNDTablePage';

export class CommerceAdminChannelsPage {
	readonly addButton: Locator;
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly buyerOrderApprovalWorkflow: Locator;
	readonly channelsTable: Locator;
	readonly channelsTableRow: (
		colPosition: number,
		value: number | string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly channelsTableRowLink: (channelName: string) => Promise<Locator>;
	readonly modalAddButton: Locator;
	readonly modalFieldName: Locator;
	readonly modalFrameLocator: FrameLocator;
	readonly modalSelectType: Locator;
	readonly commerceSiteType: Locator;
	readonly healthCheckAction: (actionName: string) => Locator;
	readonly headerActions: Locator;
	readonly headerActionsSaveButton: Locator;
	readonly ordersTabToggle: (toggleName: string) => Locator;
	readonly page: Page;
	readonly sellerOrderAcceptanceWorkflow: Locator;
	readonly sidePanelFrameLocator: FrameLocator;
	readonly shippingMethodActiveField: Locator;
	readonly shippingMethodOptionsAddButton: Locator;
	readonly shippingMethodOptionsLink: Locator;
	readonly shippingMethodSaveButton: Locator;
	readonly shippingOptionAmountField: Locator;
	readonly shippingOptionKeyField: Locator;
	readonly shippingOptionNameField: Locator;
	readonly shippingOptionSaveButton: Locator;
	readonly shippingOptionsPanel: FrameLocator;

	constructor(page: Page) {
		this.addButton = page
			.getByTestId('management-toolbar')
			.locator('[data-testid="fdsCreationActionButton"]');
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.buyerOrderApprovalWorkflow = page.getByLabel(
			'Buyer Order Approval Workflow'
		);
		this.channelsTable = page.locator(
			'#portlet_com_liferay_commerce_channel_web_internal_portlet_CommerceChannelsPortlet .fds table'
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
				.locator('tr')
				.filter({
					has: page.locator('td.cell-name', {hasText: actionName}),
				})
				.locator('td.cell-item-actions .btn');
		this.headerActions = page.locator('.header-actions');
		this.headerActionsSaveButton = this.headerActions.getByText('Save');
		this.modalFrameLocator = page.frameLocator('.fds-modal-body iframe');
		this.modalAddButton = this.modalFrameLocator.getByRole('button', {
			name: 'Add',
		});
		this.modalFieldName =
			this.modalFrameLocator.getByLabel('Name Required');
		this.modalSelectType =
			this.modalFrameLocator.getByLabel('Type Required');
		this.ordersTabToggle = (toggleName) => page.getByLabel(toggleName);
		this.page = page;
		this.sellerOrderAcceptanceWorkflow = page.getByLabel(
			'Seller Order Acceptance Workflow'
		);
		this.sidePanelFrameLocator = page.frameLocator('.is-visible iframe');

		this.shippingMethodActiveField =
			this.sidePanelFrameLocator.getByLabel('Active');
		this.shippingMethodOptionsAddButton = this.sidePanelFrameLocator
			.getByTestId('management-toolbar')
			.locator('[data-testid="fdsCreationActionButton"]');
		this.shippingMethodOptionsLink = this.sidePanelFrameLocator.getByRole(
			'link',
			{name: 'Shipping Options'}
		);
		this.shippingMethodSaveButton = this.sidePanelFrameLocator.getByRole(
			'button',
			{exact: true, name: 'Save'}
		);
		this.shippingOptionsPanel =
			this.sidePanelFrameLocator.frameLocator('.is-visible iframe');

		this.shippingOptionAmountField =
			this.shippingOptionsPanel.getByLabel('Amount');
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
		shippingOptions: string[],
		amount: boolean = false,
		skipNavigation: boolean = false
	) {
		if (!skipNavigation) {
			await this.goto();

			await (await this.channelsTableRowLink(channelName)).click();
		}

		await this.page
			.getByRole('link', {exact: true, name: shippingMethodName})
			.click();
		await this.shippingMethodActiveField.check();
		await this.shippingMethodSaveButton.click();
		await this.shippingMethodOptionsLink.click();

		let shippingPrice = 10;

		await this.shippingMethodOptionsAddButton.click();
		for (const shippingOption of shippingOptions) {
			await this.shippingOptionNameField.fill(shippingOption);
			if (amount) {
				await this.shippingOptionAmountField.fill(
					String(shippingPrice++)
				);
			}
			await this.shippingOptionKeyField.fill(shippingOption);
			await this.shippingOptionSaveButton.click();

			await waitForAlert(this.shippingOptionsPanel);

			await expect(
				this.sidePanelFrameLocator.getByText(shippingOption)
			).toBeVisible();
		}
	}
}

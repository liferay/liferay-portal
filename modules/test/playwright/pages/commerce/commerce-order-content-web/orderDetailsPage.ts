/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';

export class OrderDetailsPage {
	readonly accountNotQualifiedError: Locator;
	readonly actionsMenuButton: Locator;
	readonly applyCouponCodeButton: Locator;
	readonly checkoutButton: Locator;
	readonly deleteMenuItem: Locator;
	readonly couponCodeHeading: (code: string) => Locator;
	readonly couponCodeUsageLimitError: Locator;
	readonly discountNotApplicableError: Locator;
	readonly editButton: Locator;
	readonly enterPromoCodeInput: Locator;
	readonly minimumOrderAmountWarning: (
		minAmount: string,
		additionalAmount: string
	) => Locator;
	readonly notesButton: Locator;
	readonly orderTotal: (amount: string) => Locator;
	readonly page: Page;
	readonly processQuoteButton: Locator;
	readonly quoteProcessedStatus: Locator;
	readonly quoteRequestedStatus: Locator;
	readonly requestAQuoteButton: Locator;
	readonly requestAQuoteDialog: Locator;
	readonly requestAQuoteNoteInput: Locator;
	readonly requestAQuoteSubmitButton: Locator;
	readonly totalDiscountLabel: Locator;

	constructor(page: Page) {
		this.accountNotQualifiedError = page.getByText(
			'The account is not qualified to use the discount.'
		);
		this.actionsMenuButton = page.locator('.thumb-menu');
		this.applyCouponCodeButton = page.getByRole('button', {
			exact: true,
			name: 'Apply',
		});
		this.deleteMenuItem = page.getByRole('link', {
			exact: true,
			name: 'Delete',
		});
		this.checkoutButton = page.getByRole('button', {
			exact: true,
			name: 'Checkout',
		});
		this.couponCodeHeading = (code: string) =>
			page.getByRole('heading', {name: code});
		this.couponCodeUsageLimitError = page.getByText(
			'The inserted coupon code has reached its usage limit.'
		);
		this.discountNotApplicableError = page.getByText(
			'The discount is not applicable to the current order.'
		);
		this.editButton = page.getByRole('button', {name: 'Edit'});
		this.enterPromoCodeInput = page.getByPlaceholder('Enter Promo Code');
		this.minimumOrderAmountWarning = (
			minAmount: string,
			additionalAmount: string
		) =>
			page.getByText(
				`The minimum order amount is ${minAmount}. This order needs an additional ${additionalAmount} to meet the requirement.`,
				{exact: false}
			);
		this.notesButton = page.locator('span[title="Notes"] a');
		this.orderTotal = (amount: string) => page.getByText(amount).first();
		this.page = page;
		this.processQuoteButton = page.getByRole('button', {
			name: 'Process Quote',
		});
		this.quoteProcessedStatus = page.getByText('Quote Processed', {
			exact: true,
		});
		this.quoteRequestedStatus = page.getByText('Quote Requested', {
			exact: true,
		});
		this.requestAQuoteButton = page.locator('button.request-quote');
		this.requestAQuoteDialog = page.locator('.modal-dialog').filter({
			has: page.locator('.modal-title', {hasText: 'Request a Quote'}),
		});
		this.requestAQuoteNoteInput = this.requestAQuoteDialog.getByPlaceholder(
			'Type your note here.'
		);
		this.requestAQuoteSubmitButton = this.requestAQuoteDialog.getByRole(
			'button',
			{name: 'Submit'}
		);
		this.totalDiscountLabel = page.getByText('Total Discount').first();
	}

	async deleteOrder() {
		this.page.once('dialog', (dialog) => dialog.accept());

		await clickAndExpectToBeVisible({
			target: this.deleteMenuItem,
			trigger: this.actionsMenuButton,
		});

		await this.deleteMenuItem.click();
	}

	async applyCouponCode(code: string) {
		await expect(this.enterPromoCodeInput).toBeVisible();

		await this.enterPromoCodeInput.fill(code);

		await this.applyCouponCodeButton.click();

		await this.page.waitForLoadState('networkidle');
	}
}

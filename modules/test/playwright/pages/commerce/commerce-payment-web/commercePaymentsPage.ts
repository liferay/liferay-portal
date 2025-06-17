/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {ApplicationsMenuPage} from '../../product-navigation-applications-menu/ApplicationsMenuPage';

export class CommercePaymentsPage {
	readonly addCommentButton: Locator;
	readonly amountInput: Locator;
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly backLink: Locator;
	readonly commentInput: Locator;
	readonly commentSubmitButton: Locator;
	readonly ercInput: Locator;
	readonly ercModalOpenerButton: Locator;
	readonly ercSubmitButton: Locator;
	readonly headerDetailsTitle: Locator;
	readonly instanceSettingsRefundReasonsLink: (reasonName: string) => Locator;
	readonly makeRefundButton: Locator;
	readonly page: Page;
	readonly reasonInput: Locator;
	readonly saveButton: Locator;
	readonly submitButton: Locator;

	constructor(page: Page) {
		this.addCommentButton = page.getByRole('link', {
			exact: true,
			name: 'Add',
		});
		this.amountInput = page.getByLabel('Amount');
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.backLink = page.getByRole('link', {exact: true, name: 'Back'});
		this.commentInput = page
			.frameLocator('iframe[title="Comment"]')
			.getByLabel('Note', {exact: true});
		this.commentSubmitButton = page
			.locator('.modal-item-last')
			.getByRole('button', {exact: true, name: 'Submit'});
		this.ercInput = page
			.frameLocator('iframe[title="Edit External Reference Code"]')
			.getByLabel('External Reference Code');
		this.ercModalOpenerButton = page.locator('#erc-edit-modal-opener');
		this.ercSubmitButton = page
			.frameLocator('iframe[title="Edit External Reference Code"]')
			.getByRole('button', {exact: true, name: 'Submit'});
		this.headerDetailsTitle = page.getByTestId('headerDetailsTitle');
		this.instanceSettingsRefundReasonsLink = (nameReason: string) =>
			page.getByRole('link', {exact: true, name: nameReason});
		this.makeRefundButton = page.getByLabel('Make a Refund', {exact: true});
		this.reasonInput = page.getByLabel('Reason');
		this.saveButton = page.getByRole('link', {exact: true, name: 'Save'});
	}

	async goto(checkTabVisibility = true) {
		await this.applicationsMenuPage.goToPayments(checkTabVisibility);
	}
}

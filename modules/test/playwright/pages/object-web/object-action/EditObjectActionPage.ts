/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {ViewObjectActionsPage} from './ViewObjectActionsPage';

export class EditObjectActionPage {
	readonly actionBuilderTab: Locator;
	readonly actionLabelInput: Locator;
	readonly basicInfoTab: Locator;
	readonly checkbox: Locator;
	readonly enableConditionToggle: Locator;
	readonly expressionInput: Locator;
	readonly iframeLocator: FrameLocator;
	readonly inputNotificationsCombo: Locator;
	readonly inputThenCombo: Locator;
	readonly inputWhenCombo: Locator;
	readonly optionNotification: Locator;
	readonly page: Page;
	readonly viewObjectActionsPage: ViewObjectActionsPage;
	readonly saveButton: Locator;
	readonly userPreferredLanguage: Locator;

	constructor(page: Page) {
		this.actionBuilderTab = page
			.frameLocator('iframe')
			.getByRole('tab', {name: 'Action Builder'});
		this.actionLabelInput = page
			.frameLocator('iframe')
			.getByPlaceholder('Text to translate');
		this.basicInfoTab = page
			.frameLocator('iframe')
			.getByRole('tab', {name: 'Basic Info'});
		this.checkbox = page.frameLocator('iframe').getByRole('checkbox');
		this.enableConditionToggle = page
			.frameLocator('iframe')
			.getByLabel('Enable Condition');
		this.expressionInput = page
			.frameLocator('iframe')
			.getByPlaceholder('Create an expression.');
		this.iframeLocator = page.frameLocator('iframe');
		this.inputNotificationsCombo = page
			.frameLocator('iframe')
			.getByRole('combobox')
			.getByText('Select an Option');
		this.inputThenCombo = page
			.frameLocator('iframe')
			.getByRole('combobox')
			.getByText('Choose an Action');
		this.inputWhenCombo = page
			.frameLocator('iframe')
			.getByRole('combobox')
			.getByText('Choose a Trigger');
		this.optionNotification = page
			.frameLocator('iframe')
			.getByRole('option', {name: 'Notification'});
		this.saveButton = page
			.frameLocator('iframe')
			.getByRole('button', {name: 'Save'});
		this.userPreferredLanguage = page
			.frameLocator('iframe')
			.getByText(
				`Send email notifications in the guest user's preferred language.`
			);
		this.page = page;
		this.viewObjectActionsPage = new ViewObjectActionsPage(page);
	}

	async addNewAction({
		expressionBuilderValue,
		notificationTemplateName,
		thenOption,
		whenOption,
	}: {
		expressionBuilderValue?: string;
		notificationTemplateName?: string;
		thenOption: string;
		whenOption: string;
	}) {
		await this.viewObjectActionsPage.openObjectActionSidePanel();

		await this.actionLabelInput.fill(whenOption);

		await this.actionBuilderTab.click();

		await this.inputWhenCombo.click();
		await this.iframeLocator
			.getByRole('option', {name: whenOption})
			.click();

		if (expressionBuilderValue) {
			await this.fillExpression(expressionBuilderValue);
		}

		await this.inputThenCombo.click();
		await this.iframeLocator
			.getByRole('option', {name: thenOption})
			.click();

		if (thenOption === 'Notification') {
			await this.inputNotificationsCombo.click();

			await this.iframeLocator
				.getByRole('option', {name: notificationTemplateName})
				.click();
		}

		// Saving closes the side panel and reloads the parent from a timer, so
		// the reload is still in flight when the click resolves. Leaving it
		// pending lands it in whatever step runs next, replacing the page under
		// that step. Wait for it here.

		await Promise.all([
			this.page.waitForNavigation(),
			this.saveButton.click(),
		]);
	}

	async chooseNotificationOption() {
		await this.inputThenCombo.click();
		await this.optionNotification.click();
	}

	async clickInputNotificationsCombo() {
		await this.inputNotificationsCombo.click();
	}

	async fillExpression(expression: string) {
		await this.enableConditionToggle.check();

		await this.expressionInput.fill(expression);
	}

	async openActionBuilderTab() {
		await this.actionBuilderTab.click();
	}
}

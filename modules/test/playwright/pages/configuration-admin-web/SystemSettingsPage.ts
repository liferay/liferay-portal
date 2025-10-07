/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {waitForAlert} from '../../utils/waitForAlert';
import {ApplicationsMenuPage} from '../product-navigation-applications-menu/ApplicationsMenuPage';

export class SystemSettingsPage {
	private applicationsMenuPage;
	readonly page: Page;
	readonly saveButton: Locator;

	constructor(page: Page) {
		this.page = page;
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.saveButton = page
			.getByRole('button', {name: 'Save'})
			.or(page.getByRole('button', {name: 'Update'}));
	}

	async goto() {
		await this.applicationsMenuPage.goToSystemSettings();
	}

	async goToSystemSetting(categoryKey: string, configurationName: string) {
		await this.goto();
		await this.page
			.getByRole('link', {
				exact: true,
				name: categoryKey,
			})
			.click();
		await this.page
			.getByRole('menuitem', {
				exact: true,
				name: configurationName,
			})
			.first()
			.click();
	}

	async assertOptionVisible(options: {
		customLocator?: Locator;
		description?: string;
		label?: string;
	}) {
		const {customLocator, description, label} = options;

		if (label) {
			await expect(this.page.getByLabel(label).first()).toBeVisible();
		}

		if (description) {
			await expect(
				this.page.getByText(description).first()
			).toBeVisible();
		}

		if (customLocator) {
			await expect(customLocator).toBeVisible();
		}
	}

	async checkOption(label: string, checked: boolean) {
		const checkbox = this.page.getByLabel(label).first();
		await expect(checkbox).toBeVisible();
		checked ? await checkbox.check() : await checkbox.uncheck();
	}

	async saveAndWaitForAlert({
		autoClose,
		text = 'Success:Your request completed successfully.',
		type,
	}: {
		autoClose?: boolean;
		text?: string;
		type?: 'success' | 'info' | 'warning' | 'danger';
	} = {}) {
		await this.saveButton.click();

		await waitForAlert(this.page, text, {autoClose, type});
	}
}

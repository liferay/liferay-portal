/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {waitForAlert} from '../../utils/waitForAlert';
import {waitForPageToBeLoaded} from '../../utils/waitForPageToBeLoaded';
import {GlobalMenuPage} from '../product-navigation-applications-menu/GlobalMenuPage';

export class SystemSettingsPage {
	private readonly globalMenuPage: GlobalMenuPage;
	readonly page: Page;
	readonly saveButton: Locator;

	constructor(page: Page) {
		this.page = page;
		this.globalMenuPage = new GlobalMenuPage(page);
		this.saveButton = page
			.getByRole('button', {name: 'Save'})
			.or(page.getByRole('button', {name: 'Update'}));
	}

	async goto() {
		await this.globalMenuPage.goToControlPanel('System Settings');
	}

	async goToSystemSetting(
		categoryKey: string,
		configurationName: string,
		sectionName?: string
	) {
		await this.globalMenuPage.goToHome();
		await this.globalMenuPage.goToControlPanel('System Settings');

		if (
			await this.page
				.getByRole('tab', {name: 'Close Product Menu'})
				.isVisible()
		) {
			await this.page
				.getByRole('tab', {name: 'Close Product Menu'})
				.click();
		}

		await this.page
			.getByRole('link', {
				exact: true,
				name: categoryKey,
			})
			.click();

		let parent: Locator | Page = this.page;

		if (sectionName) {
			parent = this.page
				.locator('div')
				.filter({hasText: sectionName})
				.locator('+ div')
				.getByRole('menubar');
		}

		await parent
			.getByRole('menuitem', {
				exact: true,
				name: configurationName,
			})
			.first()
			.click();

		await waitForPageToBeLoaded(this.page);
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

	async clickOnAction(actionName: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: actionName}),
			trigger: this.page.getByRole('button', {name: 'Actions'}),
		});
	}

	async resetToDefaultValues() {
		await this.clickOnAction('Reset Default Values');

		await waitForAlert(this.page);
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

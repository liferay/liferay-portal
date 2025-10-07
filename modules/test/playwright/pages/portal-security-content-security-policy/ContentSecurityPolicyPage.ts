/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {waitForAlert} from '../../utils/waitForAlert';
import {ApplicationsMenuPage} from '../product-navigation-applications-menu/ApplicationsMenuPage';

export class ContentSecurityPolicyPage {
	readonly actions: Locator;
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly contentSecurityPolicy: Locator;
	readonly duplicateExcludedPathsButton: Locator;
	readonly enabled: Locator;
	readonly newExcludedPaths: Locator;
	readonly page: Page;
	readonly reportOnly: Locator;
	readonly resetDefaultValues: Locator;
	readonly saveButton: Locator;
	readonly updateButton: Locator;

	constructor(page: Page) {
		this.actions = page.getByRole('button', {name: 'Actions'});
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.contentSecurityPolicy = page.getByLabel('Content Security Policy');
		this.duplicateExcludedPathsButton = page
			.locator('.ddm-form-field-repeatable-add-button')
			.first();
		this.enabled = page.getByLabel('Enabled');
		this.newExcludedPaths = page.locator('textarea:empty');
		this.page = page;
		this.reportOnly = page.getByLabel('Report Only');
		this.resetDefaultValues = page.getByText('Reset Default Values');
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.updateButton = page.getByRole('button', {name: 'Update'});
	}

	async addExcludedPaths(excludedPaths: string) {
		await this.duplicateExcludedPathsButton.click();

		await this.newExcludedPaths.fill(excludedPaths);

		await expect(
			this.page.locator('textarea[id*="excludedPaths"]', {
				hasText: excludedPaths,
			})
		).toBeVisible();
	}

	async disableCSP() {
		await this.enabled.uncheck();

		await expect(this.enabled).not.toBeChecked();

		await this.saveConfiguration();
	}

	async enableCSP() {
		await this.enabled.check();

		await expect(this.enabled).toBeChecked();

		await this.saveConfiguration();
	}

	async goto() {
		await this.applicationsMenuPage.goToInstanceSettings();

		await this.page
			.getByRole('link', {name: 'Content Security Policy'})
			.click();

		await this.enabled.waitFor();
	}

	async gotoAndConfigurePolicy(
		policy: string,
		isReportOnly: boolean = false
	) {
		await this.goto();

		await this.setPolicy(policy);

		if (!isReportOnly) {
			await this.reportOnly.uncheck();
		}

		await this.enableCSP();
	}

	async removeExcludedPaths(excludedPaths: string) {
		const excludedPathsParentDiv = this.page.locator(
			'div[data-field-reference="excludedPaths"]',
			{
				has: this.page.locator(`div[title="${excludedPaths}"]`),
			}
		);

		const removeExcludedPathsButton = excludedPathsParentDiv.locator(
			'button[title="Remove"]'
		);

		await removeExcludedPathsButton.click();

		await expect(excludedPathsParentDiv).not.toBeVisible();
	}

	async resetCSPConfiguration() {
		if (await this.actions.isVisible()) {
			await clickAndExpectToBeVisible({
				autoClick: true,
				target: this.resetDefaultValues,
				trigger: this.actions,
			});

			await waitForAlert(this.page);
		}
	}

	async saveConfiguration() {
		if (await this.page.isVisible('button:has-text("Update")')) {
			await this.updateButton.click();
		}
		else {
			await this.saveButton.click();
		}

		await waitForAlert(
			this.page,
			`Success:Your request completed successfully.`
		);
	}

	async setPolicy(policy: string) {
		await this.contentSecurityPolicy.fill(policy);

		await expect(this.contentSecurityPolicy).toHaveText(policy);
	}
}

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {liferayConfig} from '../../liferay.config';
import {waitForAlert} from '../../utils/waitForAlert';

export class AccountSettingsPage {
	readonly accountSettingsMenuItem: Locator;
	readonly cookieManagerMenuItem: Locator;
	readonly currentPasswordInput: Locator;
	private readonly dataAndPrivacyNavigationItem: Locator;
	private readonly displayMenuItem: Locator;
	readonly formSubmitButton: Locator;
	readonly languageSelect: Locator;
	readonly membershipsMenuItem: Locator;
	readonly multiFactorAuthentitacionNavigationItem: Locator;
	readonly newPasswordInput: Locator;
	readonly optionalHeading: (headingName: string) => Locator;
	readonly organizationsMenuItem: Locator;
	readonly page: Page;
	readonly passwordErrorMessage: (message: string) => Locator;
	readonly passwordMenuItem: Locator;
	private readonly preferencesNavigationItem: Locator;
	readonly productAnalyticsMenuItem: Locator;
	readonly reenterPasswordInput: Locator;
	readonly rolesMenuItem: Locator;
	readonly saveButton: Locator;
	private readonly timeZoneSelect: Locator;
	readonly userDisplayData: Locator;
	readonly userPersonalMenuButton: Locator;

	constructor(page: Page) {
		this.accountSettingsMenuItem = page.getByRole('menuitem', {
			name: 'Account Settings',
		});
		this.cookieManagerMenuItem = page.getByRole('link', {
			name: 'Cookie Manager',
		});
		this.currentPasswordInput = page.getByLabel('Current Password');
		this.dataAndPrivacyNavigationItem = page.locator('.nav-link', {
			hasText: 'Data And Privacy',
		});
		this.displayMenuItem = page.getByRole('link', {
			name: 'Display Settings',
		});
		this.formSubmitButton = page
			.locator(
				'[name=_com_liferay_my_account_web_portlet_MyAccountPortlet_fm]'
			)
			.locator('button[type=submit]');
		this.languageSelect = page.locator(
			'id=_com_liferay_my_account_web_portlet_MyAccountPortlet_languageId'
		);
		this.membershipsMenuItem = page.getByRole('link', {
			name: 'Memberships',
		});
		this.multiFactorAuthentitacionNavigationItem = page.getByRole('link', {
			name: 'Multi-Factor Authentication',
		});
		this.newPasswordInput = page.getByLabel('New Password');
		this.optionalHeading = (headingName: string) => {
			return page.getByRole('heading', {
				level: 2,
				name: headingName,
			});
		};
		this.organizationsMenuItem = page.getByRole('link', {
			name: 'Organizations',
		});
		this.page = page;
		this.passwordErrorMessage = (message: string) => {
			return page.getByText(message);
		};
		this.passwordMenuItem = page.getByRole('link', {name: 'Password'});
		this.preferencesNavigationItem = page.locator('.nav-link', {
			hasText: 'Preferences',
		});
		this.productAnalyticsMenuItem = page.getByRole('link', {
			name: 'Product Analytics',
		});
		this.reenterPasswordInput = page.getByLabel('Reenter Password');
		this.rolesMenuItem = page.getByRole('link', {
			name: 'Roles',
		});
		this.saveButton = page.getByRole('button', {
			name: 'Save',
		});
		this.timeZoneSelect = page.getByLabel('Time Zone');
		this.userDisplayData = page.getByText('User Display Data');
		this.userPersonalMenuButton = page.getByTitle('User Profile Menu');
	}

	async goToAccountSettings() {
		await this.userPersonalMenuButton.click();

		await expect(this.accountSettingsMenuItem).toBeVisible();

		await this.accountSettingsMenuItem.click();

		await expect(this.userDisplayData).toBeVisible();
	}

	async goToAccountSettingsRoles() {
		await this.goToAccountSettings();
		await Promise.all([
			this.rolesMenuItem.click(),
			this.page.waitForResponse(
				(resp) =>
					resp.status() === 200 &&
					resp.url().includes('screenNavigationEntryKey=roles')
			),
		]);
	}

	async goToDataAndPrivacy() {
		await this.goToAccountSettings();

		await this.dataAndPrivacyNavigationItem.click();
	}

	async goToDisplaySettings() {
		await this.goToAccountSettings();

		await this.preferencesNavigationItem.click();

		await this.displayMenuItem.click();
	}

	async goToMultiFactorAuthenticationSettings() {
		await this.goToAccountSettings();

		await expect(
			this.multiFactorAuthentitacionNavigationItem
		).toBeVisible();

		await this.multiFactorAuthentitacionNavigationItem.click();

		await expect(this.page.getByLabel('Shared Secret')).toBeVisible();
	}

	async selectAccountLanguage({
		languageId,
		navigate = false,
	}: {
		languageId: string;
		navigate?: boolean;
	}) {
		if (navigate) {

			// do not use `goToAccountSettings`, so this works in multiple locales

			const accountSettingsPageURL =
				'/group/control_panel/manage?p_p_id=com_liferay_my_account_web_portlet_MyAccountPortlet';

			await this.page.goto(
				`${liferayConfig.environment.baseUrl}${accountSettingsPageURL}`
			);
		}

		await this.languageSelect.selectOption(languageId);
		await this.formSubmitButton.click();

		await this.page.locator('.alert-success').waitFor({state: 'visible'});
	}

	async setTimeZone(timeZone: string) {
		await this.timeZoneSelect.selectOption(timeZone);

		await this.saveButton.click();

		await waitForAlert(this.page);
	}
}

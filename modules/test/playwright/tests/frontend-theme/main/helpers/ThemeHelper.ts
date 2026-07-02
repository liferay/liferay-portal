/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

import {PagesAdminPage} from '../../../../pages/layout-admin-web/PagesAdminPage';
import {PageEditorPage} from '../../../../pages/layout-content-page-editor-web/PageEditorPage';
import {AppManagerPage} from '../../../../pages/marketplace-app-manager-web/AppManagerPage';
import {BundleBlacklistPage} from '../../../../pages/marketplace-app-manager-web/BundleBlacklistPage';
import {doAndGoBack} from '../../../../utils/doAndGoBack';
import {reloadUntilNotVisible} from '../../../../utils/reloadUntilNotVisible';
import {reloadUntilVisible} from '../../../../utils/reloadUntilVisible';

export class ThemeHelper {
	private readonly classicThemeName = 'Classic';
	private readonly cmsThemeName = 'CMS';
	private readonly cmsAppName = 'cms-theme';

	constructor(
		private readonly appManagerPage: AppManagerPage,
		private readonly bundleBlacklistPage: BundleBlacklistPage,
		private readonly page: Page,
		private readonly pageEditorPage: PageEditorPage,
		private readonly pagesAdminPage: PagesAdminPage,
		private readonly site: Site
	) {}

	async uninstallCMSTheme(testPageName?: string) {
		await doAndGoBack(this.page, async () => {
			await this.appManagerPage.uninstallApp(this.cmsAppName);

			if (testPageName) {
				await this.goToDesignTabConfiguration(testPageName);

				await this.expectThemeToBeDeactivated(this.cmsThemeName);
			}
		});
	}

	async reinstallCMSTheme(testPageName?: string) {
		await doAndGoBack(this.page, async () => {
			await this.bundleBlacklistPage.updateBundleBlacklist('');

			if (testPageName) {
				await this.goToDesignTabConfiguration(testPageName);

				await this.expectThemeToBeActivated(this.cmsThemeName);
			}
		});
	}

	async deactivateCMSTheme(testPageName: string) {
		await doAndGoBack(this.page, async () => {
			await this.appManagerPage.deactivateApp(this.cmsAppName);

			await this.goToDesignTabConfiguration(testPageName);

			await this.expectThemeToBeDeactivated(this.cmsThemeName);
		});
	}

	async activateCMSTheme(testPageName: string) {
		await doAndGoBack(this.page, async () => {
			await this.appManagerPage.activateApp(this.cmsAppName);

			await this.goToDesignTabConfiguration(testPageName);

			await this.expectThemeToBeActivated(this.cmsThemeName);
		});
	}

	async changePageThemeToCMS(pageName: string) {
		await this.changePageTheme(pageName, this.cmsThemeName);
	}

	async changePageThemeToClassic(pageName: string) {
		await this.changePageTheme(pageName, this.classicThemeName);
	}

	async changePageTheme(pageName: string, themeName: string) {
		await doAndGoBack(this.page, async () => {
			await this.goToDesignTabConfiguration(pageName);

			await this.pagesAdminPage.changeTheme(themeName);
		});
	}

	async goToDesignTabConfiguration(pageName: string) {
		await this.pagesAdminPage.goto(this.site.friendlyUrlPath);

		await this.pagesAdminPage.goToDesignTabConfiguration(pageName);
	}

	async publishPage(pageName: string) {
		await doAndGoBack(this.page, async () => {
			await this.pagesAdminPage.goto(this.site.friendlyUrlPath);

			await this.pagesAdminPage.editPage(pageName);

			await this.pageEditorPage.publishPage();
		});
	}

	async expectThemeToBeDeactivated(themeName: string) {
		const themeCard = this.pagesAdminPage.getThemeCard(themeName);

		await reloadUntilNotVisible({
			beforeReload: async () =>
				await this.pagesAdminPage.openThemeSelector(),
			maxAttempts: 10,
			myLocator: themeCard,
			page: this.page,
		});

		await expect(themeCard).toBeHidden();
	}

	async expectThemeToBeActivated(themeName: string) {
		const themeCard = this.pagesAdminPage.getThemeCard(themeName);

		await reloadUntilVisible({
			beforeReload: async () =>
				await this.pagesAdminPage.openThemeSelector(),
			maxAttempts: 10,
			myLocator: themeCard,
			page: this.page,
		});

		await expect(themeCard).toBeVisible();
	}

	async expectCurrentThemeToBeClassic(pageName: string) {
		await this.expectCurrentThemeToBe(pageName, this.classicThemeName);
	}

	async expectCurrentThemeToBeCMS(pageName: string) {
		await this.expectCurrentThemeToBe(pageName, this.cmsThemeName);
	}

	async expectCurrentThemeToBe(pageName: string, themeName: string) {
		await doAndGoBack(this.page, async () => {
			await this.goToDesignTabConfiguration(pageName);

			const currentThemeIndicator = this.page
				.locator(
					'[id="_com_liferay_layout_admin_web_portlet_GroupPagesPortlet_currentThemeContainer"]'
				)
				.getByLabel(themeName, {exact: true});

			await expect(currentThemeIndicator).toBeVisible();
		});
	}
}

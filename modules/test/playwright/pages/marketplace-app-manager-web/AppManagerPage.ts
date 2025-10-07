/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {ApplicationsMenuPage} from '../product-navigation-applications-menu/ApplicationsMenuPage';

export class AppManagerPage {
	readonly activateLink: Locator;
	readonly activeFilterMenuItem: Locator;
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly appLink: (appName: string) => Locator;
	readonly appRow: (appName: string) => Locator;
	readonly appRowOptionsMenu: (appName: string) => Locator;
	readonly deactivateLink: Locator;
	readonly filterButton: Locator;
	readonly installedFilterMenuItem: Locator;
	readonly noAppsMessage: Locator;
	readonly noResultsMessage: Locator;
	readonly optionsMenu: Locator;
	readonly page: Page;
	readonly resolvedFilterMenuItem: Locator;
	readonly searchInput: Locator;
	readonly uninstallLink: Locator;
	readonly uploadFrame: FrameLocator;
	readonly uploadFrameCloseButton: Locator;
	readonly uploadFrameErrorMessage: Locator;
	readonly uploadFrameFileInput: Locator;
	readonly uploadFrameInstallButton: Locator;
	readonly uploadMenuItem: Locator;

	constructor(page: Page) {
		this.activateLink = page.getByRole('link', {name: 'Activate'});
		this.activeFilterMenuItem = page.getByRole('menuitem', {
			name: 'Active',
		});
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.appLink = (appName) =>
			page
				.getByRole('link', {name: appName})
				.or(page.getByRole('heading', {name: appName}))
				.first();
		this.appRow = (appName) => this.appLink(appName).locator('../../..');
		this.appRowOptionsMenu = (appName) =>
			this.appRow(appName).locator('.lfr-icon-menu > a');
		this.deactivateLink = page.getByRole('link', {name: 'Deactivate'});
		this.filterButton = page.getByLabel('Filter', {exact: true});
		this.installedFilterMenuItem = page.getByRole('menuitem', {
			name: 'Installed',
		});
		this.noAppsMessage = page.getByText('No apps were found.');
		this.noResultsMessage = page.getByText('No results were found.');
		this.optionsMenu = page.getByLabel('Options').first();
		this.page = page;
		this.resolvedFilterMenuItem = page.getByRole('menuitem', {
			name: 'Resolved',
		});
		this.searchInput = page.getByPlaceholder('Search for');
		this.uninstallLink = page.getByRole('link', {name: 'Uninstall'});
		this.uploadFrame = page.frameLocator('iframe[title="Upload"]');
		this.uploadFrameErrorMessage = this.uploadFrame.getByText(
			'Please upload a file with a valid extension (JAR, LPKG, or WAR File).'
		);
		this.uploadFrameCloseButton = page.getByLabel('Close', {exact: true});
		this.uploadFrameInstallButton = this.uploadFrame.getByRole('button', {
			name: 'Install',
		});
		this.uploadFrameFileInput = this.uploadFrame.getByRole('textbox', {
			name: 'File',
		});
		this.uploadMenuItem = page.getByRole('menuitem', {name: 'Upload'});
	}

	async goto() {
		await this.applicationsMenuPage.goToAppManager();
	}
}

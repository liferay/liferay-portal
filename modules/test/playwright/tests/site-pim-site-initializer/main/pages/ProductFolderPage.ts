/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {DataSetPage} from '../../../site-cms-site-initializer/main/pages/DataSetPage';

export class ProductFolderPage {
	readonly apiHelpers: ApiHelpers;
	readonly dataSetFragmentPage: DataSetPage;
	readonly emptyState: Locator;
	readonly generalTab: Locator;
	readonly loadingAnimation: Locator;
	readonly menuItem: (name: string) => Locator;
	readonly newButton: Locator;
	readonly page: Page;
	readonly row: (name: string) => Locator;
	readonly rowActionsMenu: Locator;
	readonly rowLink: (name: string) => Locator;

	constructor(page: Page) {
		this.apiHelpers = new ApiHelpers(page);
		this.dataSetFragmentPage = new DataSetPage(page);
		this.emptyState = page.getByText('No products yet');
		this.generalTab = page.getByRole('tab', {name: 'General'});
		this.loadingAnimation = page.locator('.loading-animation').nth(0);
		this.menuItem = (name: string) =>
			page.getByRole('menuitem', {exact: true, name});
		this.newButton = page.getByTestId('fdsCreationActionButton').first();
		this.page = page;
		this.row = (name: string) => this.dataSetFragmentPage.getRow(name);
		this.rowActionsMenu = page.locator('.dropdown-menu.show');
		this.rowLink = (name: string) =>
			this.row(name).getByRole('link', {name});
	}

	async goto(folderId: number) {
		const className =
			await this.apiHelpers.jsonWebServicesClassName.fetchClassName(
				'com.liferay.object.model.ObjectEntryFolder'
			);

		await this.page.goto(
			`${PORTLET_URLS.cmsViewFolder}/${className.classNameId}/${folderId}`
		);

		await this.newButton.waitFor();
	}

	async openNewMenu() {
		await clickAndExpectToBeVisible({
			target: this.menuItem('Folder'),
			trigger: this.newButton,
		});
	}

	async openNewProductEditor(type: string) {
		await this.openNewMenu();

		await this.menuItem(type).click();

		await this.generalTab.waitFor();

		await this.loadingAnimation.waitFor({state: 'hidden'});
	}

	async openRowActions(name: string) {
		await clickAndExpectToBeVisible({
			target: this.rowActionsMenu,
			trigger: this.row(name).getByRole('button', {
				name: `${name} Actions`,
			}),
		});
	}

	async expectRowActions(name: string, actions: string[]) {
		await this.openRowActions(name);

		await expect(this.rowActionsMenu.getByRole('menuitem')).toHaveText(
			actions
		);

		await this.page.keyboard.press('Escape');
	}
}

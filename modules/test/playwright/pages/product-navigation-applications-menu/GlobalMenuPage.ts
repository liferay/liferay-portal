/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';

type Categories = 'Applications' | 'CMS' | 'Commerce' | 'Control Panel';

export class GlobalMenuPage {
	readonly categoriesList: Locator;
	readonly globalMenuButton: Locator;
	readonly page: Page;
	readonly sitesList: Locator;

	constructor(page: Page) {
		this.categoriesList = page
			.getByRole('menu')
			.and(page.locator('.categories-list'));
		this.globalMenuButton = page
			.getByRole('button', {name: 'Applications Menu'})
			.or(page.getByTestId('globalMenu'));
		this.page = page;
		this.sitesList = page
			.getByRole('menu')
			.and(page.locator('.global-menu .sites-list'));
	}

	async closeProductMenu(name: string) {
		const productMenu = this.page.getByLabel(`${name} Menu`, {exact: true});

		const isClosed = await productMenu.evaluate(
			(element) => !element.classList.contains('c-slideout-show')
		);

		if (!isClosed) {
			const button = this.page.getByTestId('sideNavigationToggler');

			await expect(button).toHaveAttribute('aria-expanded', 'true');

			await expect(async () => {
				await button.click();

				await expect(productMenu).not.toHaveClass(/c-slideout-show/, {
					timeout: 2000,
				});

				await expect(button).toHaveAttribute('aria-expanded', 'false');
			}).toPass();
		}
	}

	async goTo(categoryName: Categories) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: categoryName}),
			trigger: this.globalMenuButton,
		});

		const menu = this.page.getByLabel(`${categoryName} Menu`, {
			exact: true,
		});

		await expect(menu).toBeAttached();

		if (!(await menu.isVisible())) {
			this.openProductMenu(categoryName);
		}
	}

	async goToApplications() {
		await this.goTo('Applications');
	}

	async goToCMS() {
		await this.goTo('CMS');
	}

	async goToCommerce() {
		await this.goTo('Commerce');
	}

	async goToControlPanel() {
		await this.goTo('Control Panel');
	}

	async openGlobalMenu() {
		await clickAndExpectToBeVisible({
			target: this.categoriesList,
			trigger: this.globalMenuButton,
		});
	}

	async openProductMenu(name: string) {
		const menu = this.page.getByLabel(`${name} Menu`, {exact: true});

		const isOpen = await menu.evaluate((element) =>
			element.classList.contains('c-slideout-show')
		);

		if (!isOpen) {
			const button = this.page.getByTestId('sideNavigationToggler');

			await expect(button).toHaveAttribute('aria-expanded', 'false');

			await expect(async () => {
				await button.click();

				await expect(menu).toHaveClass(/c-slideout-show/, {
					timeout: 2000,
				});

				await expect(button).toHaveAttribute('aria-expanded', 'true');
			}).toPass();
		}
	}
}

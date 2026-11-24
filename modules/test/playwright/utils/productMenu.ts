/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

export async function openProductMenu(page: Page) {
	const productMenu = page
		.getByLabel('Product Menu', {exact: true})
		.or(page.getByTestId('sideNavigation'));

	const isOpen = await productMenu.evaluate(
		(element) =>
			element.classList.contains('open') ||
			element.classList.contains('c-slideout-show')
	);

	if (!isOpen) {
		const button = page
			.getByLabel('Open Product Menu')
			.or(page.getByTestId('sideNavigationToggler'));

		await expect(async () => {
			await button.click();

			await expect(productMenu).toHaveClass(/open|c-slideout-show/, {
				timeout: 2000,
			});
		}).toPass();
	}
}

export async function closeProductMenu(page: Page) {
	const productMenu = page
		.getByLabel('Product Menu', {exact: true})
		.or(page.getByTestId('sideNavigation'));

	const isClosed = async () =>
		await productMenu.evaluate(
			(element) =>
				!element.classList.contains('open') &&
				!element.classList.contains('c-slideout-show')
		);

	if (!(await isClosed())) {
		const button = page.getByRole('tab', {name: 'Close Product Menu'}).or(
			page.getByRole('button', {
				exact: true,
				expanded: true,
				name: 'Close Product Menu',
			})
		);

		await expect(async () => {
			await button.click();

			expect(await isClosed()).toBeTruthy();
		}).toPass({
			timeout: 5000,
		});
	}
}

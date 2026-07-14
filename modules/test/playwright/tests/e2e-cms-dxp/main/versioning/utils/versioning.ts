/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

import {AssetsPage} from '../../../../site-cms-site-initializer/main/pages/AssetsPage';

async function clickRowAction(page: Page, rowText: string, action: string) {
	const actionsButton = page
		.locator('tbody tr', {hasText: rowText})
		.first()
		.getByRole('button', {name: `${rowText} Actions`});

	const menuItem = page.getByRole('menuitem', {exact: true, name: action});

	await expect(async () => {
		if (!(await menuItem.isVisible())) {
			await actionsButton.click({timeout: 5000});

			await expect(menuItem).toBeVisible({timeout: 5000});
		}
	}).toPass({timeout: 30000});

	await menuItem.click();
}

async function clickVersionRowAction(
	page: Page,
	versionNumber: string,
	action: string
) {
	const row = page.locator('tbody tr', {
		has: page.getByRole('cell', {exact: true, name: versionNumber}),
	});

	const actionsButton = row.getByRole('button', {name: /Actions$/});

	const menuItem = page.getByRole('menuitem', {exact: true, name: action});

	await expect(async () => {
		if (!(await menuItem.isVisible())) {
			await actionsButton.click({timeout: 5000});

			await expect(menuItem).toBeVisible({timeout: 5000});
		}
	}).toPass({timeout: 30000});

	await menuItem.click();
}

export async function deleteVersion(page: Page, versionNumber: string) {
	const rows = page.locator('tbody tr');

	const rowCount = await rows.count();

	await clickVersionRowAction(page, versionNumber, 'Delete');

	const modal = page.locator('.modal.show');

	await expect(modal).toBeVisible({timeout: 10000});

	await modal.getByRole('button', {exact: true, name: 'Delete'}).click();

	await expect(rows).toHaveCount(rowCount - 1, {timeout: 15000});
}

export async function restoreVersionByNumber(
	page: Page,
	versionNumber: string
) {
	const rows = page.locator('tbody tr');

	const rowCount = await rows.count();

	await clickVersionRowAction(page, versionNumber, 'Restore Version');

	await expect(rows).toHaveCount(rowCount + 1, {timeout: 15000});
}

export async function openVersionHistory(
	assetsPage: AssetsPage,
	headTitle: string
) {
	await assetsPage.execItemAction({
		action: 'View History',
		filter: headTitle,
	});

	await expect(
		assetsPage.page.getByRole('heading', {name: `"${headTitle}" History`})
	).toBeVisible({timeout: 15000});
}

export async function restoreVersion(page: Page, versionTitle: string) {
	const rows = page.locator('tbody tr');

	const rowCount = await rows.count();

	await clickRowAction(page, versionTitle, 'Restore Version');

	await expect(rows).toHaveCount(rowCount + 1, {timeout: 15000});
}

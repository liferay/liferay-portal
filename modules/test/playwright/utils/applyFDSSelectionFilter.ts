/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

// When a filter is already applied, reopening the Filter dropdown lands on the
// previous filter's option list; `chained` clicks Back to reach the filter
// menu before selecting the next one.

export async function applyFDSSelectionFilter(
	page: Page,
	{
		chained = false,
		exclude = false,
		filter,
		value,
	}: {chained?: boolean; exclude?: boolean; filter: string; value: string}
) {
	await page.getByRole('button', {exact: true, name: 'Filter'}).click();

	if (chained) {
		await page.getByRole('button', {exact: true, name: 'Back'}).click();
	}

	await page.getByRole('menuitem', {exact: true, name: filter}).click();

	await page.getByRole('checkbox', {exact: true, name: value}).check();

	if (exclude) {
		await page.getByRole('switch', {exact: true, name: 'Exclude'}).click();
	}

	await page.getByRole('button', {exact: true, name: 'Add Filter'}).click();

	const chipName = exclude
		? `${filter}: (Exclude) ${value}`
		: `${filter}: ${value}`;

	await expect(page.getByRole('button', {name: chipName})).toBeVisible();
}

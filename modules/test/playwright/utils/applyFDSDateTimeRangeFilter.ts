/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

export async function applyFDSDateTimeRangeFilter(
	page: Page,
	{filter, from, to}: {filter: string; from?: Date; to?: Date}
) {
	await page.getByRole('button', {exact: true, name: 'Filter'}).click();

	await page.getByRole('menuitem', {exact: true, name: filter}).click();

	if (from) {
		await page.getByLabel('From').fill(formatDateTimeForUI(from));
	}

	if (to) {
		await page
			.getByLabel('To', {exact: true})
			.fill(formatDateTimeForUI(to));
	}

	await page.getByRole('button', {exact: true, name: 'Add Filter'}).click();

	await expect(
		page.getByRole('button', {name: new RegExp(`^${filter}:`)})
	).toBeVisible();
}

export function formatDateForUI(date: Date): string {
	const month = String(date.getMonth() + 1).padStart(2, '0');
	const day = String(date.getDate()).padStart(2, '0');
	const year = date.getFullYear();

	return `${month}/${day}/${year}`;
}

export function formatDateTimeForUI(date: Date): string {
	const month = String(date.getMonth() + 1).padStart(2, '0');
	const day = String(date.getDate()).padStart(2, '0');
	const year = date.getFullYear();

	let hour = date.getHours();
	const ampm = hour >= 12 ? 'PM' : 'AM';

	hour = hour % 12 || 12;

	const minute = String(date.getMinutes()).padStart(2, '0');

	return `${month}/${day}/${year} ${String(hour).padStart(2, '0')}:${minute} ${ampm}`;
}

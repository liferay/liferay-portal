/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Page, expect} from '@playwright/test';

function getPaginator(page: Page | FrameLocator) {
	return page.locator('[data-qa-id="paginator"]');
}

export async function previousPage(page) {
	await getPaginator(page).getByTitle('Previous page').click();
}

export async function nextPage(page) {
	await getPaginator(page).getByTitle('next page').click();
}

export async function gotoPage(page, pageNumber: number) {
	await getPaginator(page)
		.getByRole('link', {name: `Page ${pageNumber}`})
		.click();
}

export async function getResultsTotal(
	page: Page | FrameLocator
): Promise<number> {
	const text =
		(await page
			.getByText(/Showing \d+ to \d+ of \d+ entries/)
			.first()
			.textContent()) || '';

	const match = text.match(/of (\d+) entries/);

	return match ? Number(match[1]) : 0;
}

export async function setItemsPerPage(page, limit: 20 | 40 | 60) {
	const timeout = 300;
	const option = getPaginator(page).getByRole('option', {
		name: `${limit} Entries per Page`,
	});

	await expect(async () => {
		if (await option.isHidden({timeout})) {
			await getPaginator(page)
				.getByLabel('Items per Page')
				.click({timeout});
		}

		await expect(option).toBeVisible({timeout});
	}).toPass({
		intervals: [timeout * 2, timeout * 3, timeout * 4],
	});

	await option.press('Enter');
}

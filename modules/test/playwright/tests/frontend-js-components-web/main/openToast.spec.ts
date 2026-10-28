/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {performLoginViaApi, performLogout} from '../../../utils/performLogin';

export const test = mergeTests(loginTest());

test('Check timeout toast has focus', {tag: '@LPD-79043'}, async ({page}) => {
	await performLogout(page);
	await performLoginViaApi({page, rememberMe: false, screenName: 'test'});

	await expect(page.getByText('Due to inactivity')).toBeVisible();

	await expect(
		page.locator('#sessionToast').locator('.alert-autofit-row')
	).toBeFocused();
});

test(
	'Clicks below a toast reach the page under the toast container',
	{tag: '@LPD-112203'},
	async ({page}) => {
		await page.goto('/');

		await page.evaluate(() =>
			Liferay.Util.openToast({message: 'Toast', type: 'success'})
		);

		const toast = page.locator('#ToastAlertContainer .alert');

		await expect(toast).toBeVisible();

		const containerBox = await page
			.locator('#ToastAlertContainer')
			.boundingBox();
		const toastBox = await toast.boundingBox();

		const toastBottom = toastBox.y + toastBox.height;

		expect(containerBox.y + containerBox.height).toBeGreaterThan(
			toastBottom + 2
		);

		const insideContainer = await page.evaluate(
			({x, y}) =>
				!!document
					.elementFromPoint(x, y)
					?.closest('#ToastAlertContainer'),
			{
				x: toastBox.x + toastBox.width / 2,
				y: toastBottom + 2,
			}
		);

		expect(insideContainer).toBe(false);

		const closeButton = toast.getByRole('button', {name: 'Close'});

		await closeButton.click();

		await expect(toast).toBeHidden();
	}
);

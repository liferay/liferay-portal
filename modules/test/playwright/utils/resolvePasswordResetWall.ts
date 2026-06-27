/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

/**
 * Clears the "Change Password" wall that an admin-created user hits the first
 * time it signs in. Until it is resolved the wall intercepts every navigation,
 * so the user never reaches the page under test. Call it right after signing
 * the user in and before navigating to the content. The new password must
 * differ from the original, so it is not reused for any later sign-in.
 */
export async function resolvePasswordResetWall(
	page: Page,
	newPassword = 'Test12345!'
) {
	await page.goto('/');

	const changePasswordText = page
		.getByText('Change Password', {exact: true})
		.first();

	const onWall = await changePasswordText
		.waitFor({state: 'visible', timeout: 10000})
		.then(() => true)
		.catch(() => false);

	if (!onWall) {
		return;
	}

	const passwordInputs = page.locator('input[type="password"]');

	await passwordInputs.nth(0).fill(newPassword);

	await passwordInputs.nth(1).fill(newPassword);

	await page.getByRole('button', {name: 'Save'}).click();

	await expect(changePasswordText).toBeHidden({timeout: 15000});
}

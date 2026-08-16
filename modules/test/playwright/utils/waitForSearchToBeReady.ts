/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

/**
 * Waits until the management toolbar's search accepts Enter.
 *
 * The server ships the search form's default submit button disabled, and a form
 * whose default button is disabled skips implicit submission entirely, so Enter
 * produces no submit event and no request. The typed text simply sits there, the
 * listing is never filtered, and an assertion about the filtered result fails
 * against unfiltered content. The button is enabled once the client script runs,
 * which is what this waits for.
 *
 * A load state is not enough on its own: after an SPA navigation the document
 * has already fired load while the swapped-in markup is still inert.
 */
export async function waitForSearchToBeReady(page: Page) {
	await expect(
		page.locator('.management-bar button[type="submit"]')
	).toBeEnabled();
}

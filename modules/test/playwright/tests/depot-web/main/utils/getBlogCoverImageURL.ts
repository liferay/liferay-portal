/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

export default async function getBlogCoverImageURL(page: Page) {
	const coverImage = page.locator('.cover-image');

	await expect(coverImage).toHaveCSS('background-image', /url\(/);

	return coverImage.evaluate(
		(element) =>
			getComputedStyle(element).backgroundImage.match(
				/url\("?(.*?)"?\)/
			)[1]
	);
}

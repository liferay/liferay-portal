/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

export async function assertHTMLAttribute(
	page: Page,
	enabled: boolean,
	attributeName: string,
	attributeValue: string
) {
	const html = page.locator('html');

	if (enabled) {
		await expect(html).toHaveAttribute(attributeName, attributeValue);
	}
	else {
		await expect(html).not.toHaveAttribute(attributeName, attributeValue);
	}
}

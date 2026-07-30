/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect} from '@playwright/test';

export async function expectToPass(
	callback: () => Promise<void>,
	{timeout}: {timeout: number}
) {
	const deadline = Date.now() + timeout;

	while (true) {
		const remaining = deadline - Date.now();

		try {
			await expect(callback).toPass({timeout: Math.max(remaining, 0)});

			return;
		}
		catch (error) {
			if (Date.now() >= deadline) {
				throw error;
			}
		}
	}
}

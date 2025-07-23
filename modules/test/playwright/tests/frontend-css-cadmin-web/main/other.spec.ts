/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';

export const test = mergeTests(loginTest());

test(
	'CSS files do not have BOM characters inside',
	{tag: '@LPD-61387'},
	async ({page}) => {
		const response = await page.goto(
			'o/frontend-css-cadmin-web/clay_admin.css?minifierType=css'
		);

		expect(response.status()).toBe(200);

		const buffer = await response.body();

		let bomFound = false;

		for (let i = 0; !bomFound && i < buffer.length - 2; i++) {
			bomFound =
				buffer.readUInt8(i) === 0xef &&
				buffer.readUint8(i + 1) === 0xbb &&
				buffer.readUint8(i + 2) === 0xbf;
		}

		expect(bomFound).toBe(false);
	}
);

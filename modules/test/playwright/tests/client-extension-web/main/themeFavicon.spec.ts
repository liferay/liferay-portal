/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {ViewClientExtensionPage} from '../pages/ViewClientExtensionPage';

export const testSample = mergeTests(loginTest());

testSample.describe('Samples', () => {
	const SAMPLE = {
		erc: 'LXC:liferay-sample-theme-favicon',
		name: 'Liferay Sample Theme Favicon',
		url: '',
	};

	testSample(`${SAMPLE.name} is registered`, async ({page}) => {
		const viewClientExtensionPage = new ViewClientExtensionPage(
			page,
			SAMPLE.erc
		);

		await viewClientExtensionPage.goto();

		SAMPLE.url = await viewClientExtensionPage
			.getInputByLabel('URL')
			.inputValue();

		await expect(viewClientExtensionPage.nameInput).toHaveValue(
			SAMPLE.name
		);
	});

	testSample(
		`${SAMPLE.name}'s .ico file can be downloaded`,
		async ({page}) => {
			const response = await page.goto(SAMPLE.url);

			expect(response.status()).toBe(200);
			expect(await response.headerValue('Content-Type')).toBe(
				'image/vnd.microsoft.icon'
			);
		}
	);
});

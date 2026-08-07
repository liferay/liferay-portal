/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {ViewClientExtensionPage} from '../pages/ViewClientExtensionPage';

const testSample = mergeTests(loginTest());

testSample.describe('Samples', () => {
	const SAMPLES = [
		{
			erc: 'LXC:liferay-sample-global-css-1',
			name: 'Liferay Sample Global CSS 1',
			url: '',
		},
		{
			erc: 'LXC:liferay-sample-global-css-2',
			name: 'Liferay Sample Global CSS 2',
			url: '',
		},
	];

	for (const sample of SAMPLES) {
		testSample(`${sample.name} is registered`, async ({page}) => {
			const viewClientExtensionPage = new ViewClientExtensionPage(
				page,
				sample.erc
			);

			await viewClientExtensionPage.goto();

			await expect(viewClientExtensionPage.nameInput).toHaveValue(
				sample.name
			);

			sample.url = await viewClientExtensionPage
				.getInputByLabel('URL')
				.inputValue();

			await expect(
				viewClientExtensionPage.getInputByLabel('URL')
			).toHaveValue(sample.url);
		});

		testSample(
			`${sample.name}'s .css file can be downloaded`,
			async ({page}) => {
				const response = await page.goto(sample.url);

				expect(response.status()).toBe(200);
				expect(await response.headerValue('Content-Type')).toBe(
					'text/css;charset=UTF-8'
				);
			}
		);
	}
});

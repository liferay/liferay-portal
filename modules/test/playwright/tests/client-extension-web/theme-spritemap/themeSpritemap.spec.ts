/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import getRandomString from '../../../utils/getRandomString';
import {clientExtensionsPageTest} from '../fixtures/clientExtensionsPageTest';
import {ViewClientExtensionPage} from '../pages/ViewClientExtensionPage';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	clientExtensionsPageTest,
	loginTest(),
	pagesAdminPagesTest
);

const testSample = mergeTests(loginTest());

testSample.describe('Samples', () => {
	const SAMPLES = [
		{
			erc: 'LXC:liferay-sample-theme-spritemap-1',
			name: 'Liferay Sample Theme Spritemap 1',
			url: '',
		},
		{
			erc: 'LXC:liferay-sample-theme-spritemap-2',
			name: 'Liferay Sample Theme Spritemap 2',
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

			sample.url = await viewClientExtensionPage
				.getInputByLabel('URL')
				.inputValue();

			await expect(viewClientExtensionPage.nameInput).toHaveValue(
				sample.name
			);
		});

		testSample(
			`${sample.name}'s .svg file can be downloaded`,
			async ({page}) => {
				const response = await page.goto(sample.url);

				expect(response.status()).toBe(200);
				expect(await response.headerValue('Content-Type')).toBe(
					'image/svg+xml'
				);
			}
		);
	}
});

test(
	'Client extension is applied',
	{tag: ['@LPS-166479', '@LPD-75288']},
	async ({apiHelpers, page, pagesAdminPage, site}) => {
		const sample = {
			erc: 'LXC:liferay-sample-theme-spritemap-1',
			name: 'Liferay Sample Theme Spritemap 1',
			url: '',
		};

		await test.step(`Get "Liferay Sample Theme Spritemap 1"'s URL`, async () => {
			const viewClientExtensionPage = new ViewClientExtensionPage(
				page,
				sample.erc
			);

			await viewClientExtensionPage.goto();

			sample.url = await viewClientExtensionPage
				.getInputByLabel('URL')
				.inputValue();
		});

		await test.step('Apply new client extension to site', async () => {
			await pagesAdminPage.selectClientExtension({
				clientExtensionName: 'Liferay Sample Theme Spritemap 1',
				siteUrl: site.friendlyUrlPath,
				type: 'themeSpritemap',
			});
		});

		await test.step('Create an empty page and go to it', async () => {
			const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				title: getRandomString(),
			});

			await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);
		});

		await test.step('Assert new spritemap is used', async () => {
			const spritemap = await page.evaluate(
				() => Liferay.Icons.spritemap
			);

			expect(spritemap).toContain(sample.url);
		});
	}
);

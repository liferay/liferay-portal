/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {clientExtensionsPageTest} from '../fixtures/clientExtensionsPageTest';
import {WaitAction} from '../pages/EditClientExtensionsPage';
import {ViewClientExtensionPage} from '../pages/ViewClientExtensionPage';
import {editJSImportMapsPageTest} from './fixtures/editJSImportMapsExtensionPageTest';

const test = mergeTests(
	clientExtensionsPageTest,
	editJSImportMapsPageTest,
	loginTest()
);

const testSample = mergeTests(loginTest());

testSample.describe('Samples', () => {
	const SAMPLES = [
		{
			bareSpecifier: 'jquery',
			erc: 'LXC:liferay-sample-js-import-maps-entry',
			name: 'Liferay Sample JS Import Maps Entry',
			url: '',
		},
		{
			bareSpecifier: 'my-utils',
			erc: 'LXC:liferay-sample-etc-frontend-js-import-maps-entry',
			name: 'Liferay Sample Etc Frontend JS Import Maps Entry',
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
		});

		testSample(
			`${sample.name}'s .js file can be downloaded`,
			async ({page}) => {
				const response = await page.goto(sample.url);

				expect(response.status()).toBe(200);
				expect(await response.headerValue('Content-Type')).toContain(
					'application/javascript'
				);
			}
		);

		testSample(`${sample.name} appears in import maps`, async ({page}) => {
			await page.goto('/');

			const importMap = await page
				.locator('script[type="importmap"]')
				.evaluate((node: HTMLScriptElement) => node.innerText);

			expect(importMap).toContain(
				`"${sample.bareSpecifier}":"${sample.url}"`
			);
		});
	}
});

test(
	'Can cancel creation of a JS Import Maps entry',
	{tag: '@LPS-180167'},
	async ({clientExtensionsPage, editJSImportMapsPage}) => {
		const name = getRandomString();

		await editJSImportMapsPage.goto();

		await editJSImportMapsPage.nameInput.fill(name);
		await editJSImportMapsPage.bareSpecifierInput.fill('test-specifier');
		await editJSImportMapsPage.javaScriptURLInput.fill(
			'https://www.example.com/test.js'
		);

		await editJSImportMapsPage.cancel();

		await clientExtensionsPage.goto();

		await expect(clientExtensionsPage.getRowByText(name)).not.toBeVisible();
	}
);

test(
	'Can create a JS Import Maps entry with only required fields filled',
	{tag: '@LPS-180167'},
	async ({clientExtensionsPage, editJSImportMapsPage}) => {
		const name = getRandomString();

		await editJSImportMapsPage.goto();

		await editJSImportMapsPage.nameInput.fill(name);
		await editJSImportMapsPage.bareSpecifierInput.fill('test-specifier');
		await editJSImportMapsPage.javaScriptURLInput.fill(
			'https://www.example.com/test.js'
		);

		await editJSImportMapsPage.publish(WaitAction.SUCCESS);

		await test.step('Verify entry was created', async () => {
			await clientExtensionsPage.goto();

			await clientExtensionsPage.search(name);

			await expect(clientExtensionsPage.getRowByText(name)).toBeVisible();
		});

		await test.step('Clean up', async () => {
			await clientExtensionsPage.deleteClientExtension(name);
		});
	}
);

test(
	'Cannot publish when Bare Specifier field is empty',
	{tag: '@LPS-180167'},
	async ({editJSImportMapsPage, page}) => {
		await editJSImportMapsPage.goto();

		await editJSImportMapsPage.nameInput.fill(getRandomString());
		await editJSImportMapsPage.javaScriptURLInput.fill(
			'https://www.example.com/test.js'
		);

		await editJSImportMapsPage.publish(WaitAction.NONE);

		await expect(
			page.getByText(/bare specifier.*required|required.*bare specifier/i)
		).toBeVisible();
	}
);

test(
	'Cannot publish when Name field is empty',
	{tag: '@LPS-180167'},
	async ({editJSImportMapsPage}) => {
		await editJSImportMapsPage.goto();

		await editJSImportMapsPage.bareSpecifierInput.fill('test-specifier');
		await editJSImportMapsPage.javaScriptURLInput.fill(
			'https://www.example.com/test.js'
		);

		await editJSImportMapsPage.publish(WaitAction.ERROR);
	}
);

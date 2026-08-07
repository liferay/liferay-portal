/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import path from 'path';

import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {clientExtensionsPageTest} from '../fixtures/clientExtensionsPageTest';
import {WaitAction} from '../pages/EditClientExtensionsPage';
import {editCustomElementPageTest} from './fixtures/editCustomElementPageTest';

const test = mergeTests(
	clientExtensionsPageTest,
	editCustomElementPageTest,
	loginTest()
);

test(
	'Can export a Custom Element as JSON',
	{tag: '@LPS-182184'},
	async ({clientExtensionsPage, editCustomElementPage, page}) => {
		const clientExtensionName = getRandomString();

		await test.step('Create a Custom Element', async () => {
			await editCustomElementPage.goto();

			await editCustomElementPage.nameInput.fill(clientExtensionName);
			await editCustomElementPage.htmlElementNameInput.fill(
				`html-${getRandomString()}`
			);
			await editCustomElementPage.javaScriptURLInput.fill(
				'https://www.example.com/test.js'
			);

			await editCustomElementPage.publish(WaitAction.SUCCESS);
		});

		await test.step('Export the Custom Element as JSON', async () => {
			await clientExtensionsPage.goto();

			await page
				.locator('.fds tr')
				.filter({has: page.getByText(clientExtensionName)})
				.getByRole('button', {name: 'Actions'})
				.click();

			const downloadPromise = page.waitForEvent('download');

			await page.getByRole('menuitem', {name: 'Export as JSON'}).click();

			const download = await downloadPromise;

			expect(download.suggestedFilename()).toMatch(
				/client-extension-entry\.[0-9a-f-]+\.json/g
			);
		});

		await test.step('Clean up', async () => {
			await clientExtensionsPage.goto();

			await clientExtensionsPage.deleteClientExtension(
				clientExtensionName
			);
		});
	}
);

test(
	'Can import a Client Extension from JSON',
	{tag: '@LPS-182184'},
	async ({clientExtensionsPage, editCustomElementPage, page}) => {
		const clientExtensionName = getRandomString();

		let downloadPath: string;

		await test.step('Create and export a Custom Element', async () => {
			await editCustomElementPage.goto();

			await editCustomElementPage.nameInput.fill(clientExtensionName);
			await editCustomElementPage.htmlElementNameInput.fill(
				`html-${getRandomString()}`
			);
			await editCustomElementPage.javaScriptURLInput.fill(
				'https://www.example.com/test.js'
			);

			await editCustomElementPage.publish(WaitAction.SUCCESS);

			await clientExtensionsPage.goto();

			await page
				.locator('.fds tr')
				.filter({has: page.getByText(clientExtensionName)})
				.getByRole('button', {name: 'Actions'})
				.click();

			const downloadPromise = page.waitForEvent('download');

			await page.getByRole('menuitem', {name: 'Export as JSON'}).click();

			const download = await downloadPromise;

			downloadPath = await download.path();
		});

		await test.step('Delete the Custom Element', async () => {
			await clientExtensionsPage.goto();

			await clientExtensionsPage.deleteClientExtension(
				clientExtensionName
			);

			await expect(
				clientExtensionsPage.getRowByText(clientExtensionName)
			).not.toBeVisible();
		});

		await test.step('Import the Client Extension', async () => {
			await clientExtensionsPage.goto();

			// Dismiss any lingering success alerts from prior steps

			const existingAlert = page.locator(
				'.alert-success .close, .alert-dismissible .close'
			);

			if (
				await existingAlert
					.isVisible({timeout: 1000})
					.catch(() => false)
			) {
				await existingAlert.click();
				await existingAlert.waitFor({state: 'hidden'});
			}

			await page.getByLabel('Options').click();

			await page.getByRole('menuitem', {name: 'Import'}).click();

			const fileInput = page.locator('input[type="file"]');

			await fileInput.setInputFiles(downloadPath);

			await page
				.getByRole('button', {exact: true, name: 'Import'})
				.click();

			await expect(
				page.getByText(
					/imported successfully|request completed successfully/i
				)
			).toBeVisible();
		});

		await test.step('Verify import and clean up', async () => {
			await clientExtensionsPage.goto();

			await clientExtensionsPage.search(clientExtensionName);

			await expect(
				clientExtensionsPage.getRowByText(clientExtensionName)
			).toBeVisible();

			await clientExtensionsPage.deleteClientExtension(
				clientExtensionName
			);
		});
	}
);

test(
	'Can clear JSON file field during import',
	{tag: '@LPS-182184'},
	async ({clientExtensionsPage, editCustomElementPage, page}) => {
		const clientExtensionName = getRandomString();

		let downloadPath: string;

		await test.step('Create and export a Custom Element', async () => {
			await editCustomElementPage.goto();

			await editCustomElementPage.nameInput.fill(clientExtensionName);
			await editCustomElementPage.htmlElementNameInput.fill(
				`html-${getRandomString()}`
			);
			await editCustomElementPage.javaScriptURLInput.fill(
				'https://www.example.com/test.js'
			);

			await editCustomElementPage.publish(WaitAction.SUCCESS);

			await clientExtensionsPage.goto();

			await page
				.locator('.fds tr')
				.filter({has: page.getByText(clientExtensionName)})
				.getByRole('button', {name: 'Actions'})
				.click();

			const downloadPromise = page.waitForEvent('download');

			await page.getByRole('menuitem', {name: 'Export as JSON'}).click();

			const download = await downloadPromise;

			downloadPath = await download.path();
		});

		await test.step('Open import dialog and set file', async () => {
			await clientExtensionsPage.goto();

			await page.getByLabel('Options').click();

			await page.getByRole('menuitem', {name: 'Import'}).click();

			const fileInput = page.locator('input[type="file"]');

			await fileInput.setInputFiles(downloadPath);
		});

		await test.step('Clear the file field', async () => {
			await page.getByRole('button', {name: 'Clear'}).click();

			await expect(page.locator('input[type="file"]')).toHaveValue('');
		});

		await test.step('Clean up', async () => {
			await clientExtensionsPage.goto();

			await clientExtensionsPage.deleteClientExtension(
				clientExtensionName
			);
		});
	}
);

test(
	'Cannot import an invalid file',
	{tag: '@LPS-182184'},
	async ({clientExtensionsPage, page}) => {
		await clientExtensionsPage.goto();

		await page.getByLabel('Options').click();

		await page.getByRole('menuitem', {name: 'Import'}).click();

		const fileInput = page.locator('input[type="file"]');

		const invalidFilePath = path.join(
			__dirname,
			'..',
			'..',
			'..',
			'package.json'
		);

		await fileInput.setInputFiles(invalidFilePath);

		await page.getByRole('button', {exact: true, name: 'Import'}).click();

		await expect(page.getByText(/error|invalid|failed/i)).toBeVisible({
			timeout: 10000,
		});
	}
);

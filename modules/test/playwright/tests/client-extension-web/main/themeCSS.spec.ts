/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {styleBookPageTest} from '../../../fixtures/styleBookPageTest';
import getRandomString from '../../../utils/getRandomString';
import {clientExtensionsPageTest} from './fixtures/clientExtensionsPageTest';
import {editThemeCSSClientExtensionsPageTest} from './fixtures/editThemeCSSClientExtensionsPageTest';
import {WaitAction} from './pages/EditClientExtensionsPage';
import {ViewClientExtensionPage} from './pages/ViewClientExtensionPage';
import uploadAndValidateFile from './utils/uploadAndValidateFile';

const test = mergeTests(
	clientExtensionsPageTest,
	loginTest(),
	pagesAdminPagesTest,
	styleBookPageTest,
	editThemeCSSClientExtensionsPageTest
);
const testControlPanelScoped = mergeTests(
	loginTest(),
	clientExtensionsPageTest
);
const testSample = mergeTests(loginTest());

testSample.describe('Samples', () => {
	const SAMPLES = [
		{
			erc: 'LXC:liferay-sample-theme-css-1',
			mainURL: '/o/liferay-sample-theme-css-1/css/main.css',
			name: 'Liferay Sample Theme CSS 1',
		},
		{
			erc: 'LXC:liferay-sample-theme-css-2',
			mainURL: '/o/liferay-sample-theme-css-2/css/main.css',
			name: 'Liferay Sample Theme CSS 2',
		},
		{
			erc: 'LXC:liferay-sample-theme-css-3',
			mainURL: '/o/liferay-sample-theme-css-3/css/main.css',
			name: 'Liferay Sample Theme CSS 3',
		},
		{
			erc: 'LXC:liferay-sample-theme-css-4',
			mainURL: '/o/liferay-sample-theme-css-4/css/main.css',
			name: 'Liferay Sample Theme CSS 4',
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
			await expect(
				viewClientExtensionPage.getInputByLabel('Main URL')
			).toHaveValue(sample.mainURL);
		});

		testSample(
			`${sample.name}'s .css file can be downloaded`,
			async ({page}) => {
				const response = await page.goto(sample.mainURL);

				expect(response.status()).toBe(200);
				expect(await response.headerValue('Content-Type')).toBe(
					'text/css;charset=UTF-8'
				);
			}
		);
	}
});

testControlPanelScoped.describe('Samples (control panel scoped)', () => {
	testControlPanelScoped(
		'LPD-37516 Sample control panel theme CSS client extension is properly deployed',
		async ({clientExtensionsPage, page}) => {
			await clientExtensionsPage.goto();

			const background = await page
				.locator('.control-menu')
				.evaluate((controlMenu) => {
					const computedStyle = window.getComputedStyle(controlMenu);

					return computedStyle.background;
				});

			expect(background).toBe(
				'rgba(0, 0, 0, 0) linear-gradient(105deg, rgb(0, 63, 91), rgb(43, 75, 125), rgb(95, 81, 149), rgb(152, 80, 157), rgb(204, 76, 145), rgb(242, 83, 117), rgb(255, 111, 78), rgb(255, 153, 19)) repeat scroll 0% 0% / auto padding-box border-box'
			);
		}
	);
});

test('ThemeCSS client extension supports frontend token definition JSON file upload', async ({
	editThemeCSSClientExtensionsPage,
	page,
}) => {
	await editThemeCSSClientExtensionsPage.goto();

	await uploadAndValidateFile(
		'frontend-token-definition-invalid-schema.json',
		'The format is invalid. Please upload a valid Frontend Token Definition JSON file.',
		page,
		editThemeCSSClientExtensionsPage
	);

	await uploadAndValidateFile(
		'empty-json-file.json',
		'The frontend token definition JSON file was uploaded and contributed 0 token categories, 0 token sets, and 0 tokens.',
		page,
		editThemeCSSClientExtensionsPage
	);

	await uploadAndValidateFile(
		'frontend-token-definition.json',
		'The frontend token definition JSON file was uploaded and contributed 1 token categories, 1 token sets, and 3 tokens.',
		page,
		editThemeCSSClientExtensionsPage
	);

	await uploadAndValidateFile(
		'frontend-token-definition-empty-object.json',
		'The frontend token definition JSON file was uploaded and contributed 0 token categories, 0 token sets, and 0 tokens.',
		page,
		editThemeCSSClientExtensionsPage
	);
});

test('ThemeCSS client extension frontend token definition tokens appears stylebooks', async ({
	clientExtensionsPage,
	editThemeCSSClientExtensionsPage,
	page,
	pagesAdminPage,
	styleBooksPage,
}) => {
	const clientExtensionName = getRandomString();

	await test.step('Create Theme CSS client extension', async () => {
		await editThemeCSSClientExtensionsPage.goto();

		await editThemeCSSClientExtensionsPage.nameInput.fill(
			clientExtensionName
		);

		await uploadAndValidateFile(
			'frontend-token-definition.json',
			'The frontend token definition JSON file was uploaded and contributed 1 token categories, 1 token sets, and 3 tokens.',
			page,
			editThemeCSSClientExtensionsPage
		);

		await editThemeCSSClientExtensionsPage.publish(WaitAction.SUCCESS);
	});

	await test.step('Apply Theme CSS client extension to all pages', async () => {
		await pagesAdminPage.gotoPagesConfiguration();

		await pagesAdminPage.selectThemeCSSClientExtension(clientExtensionName);
	});

	const styleBookName = getRandomString();

	await test.step('Create style book', async () => {
		await styleBooksPage.goto();

		await styleBooksPage.create(styleBookName);
	});

	await test.step('Assert that the frontend token set defined in the frontendTokenDefinition.json file is available in the style book', async () => {
		const frontendTokenSetLabel = page.getByText('primary-buttons');

		await expect(frontendTokenSetLabel).toBeVisible();
	});

	await test.step('Clean up', async () => {
		await styleBooksPage.goto();

		await styleBooksPage.delete(styleBookName);

		await clientExtensionsPage.goto();

		await clientExtensionsPage.deleteClientExtension(clientExtensionName);
	});
});

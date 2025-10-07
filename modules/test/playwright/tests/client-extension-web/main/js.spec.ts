/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {siteSettingsPagesTest} from '../../../fixtures/siteSettingsPagesTest';
import {styleBookPageTest} from '../../../fixtures/styleBookPageTest';
import {PagesAdminPage} from '../../../pages/layout-admin-web/PagesAdminPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {clientExtensionsPageTest} from './fixtures/clientExtensionsPageTest';
import {editJSClientExtensionsPageTest} from './fixtures/editJSClientExtensionsPageTest';
import {ClientExtensionsPage} from './pages/ClientExtensionsPage';
import {WaitAction} from './pages/EditClientExtensionsPage';
import {EditJSClientExtensionsPage} from './pages/EditJSClientExtensionsPage';
import {ViewClientExtensionPage} from './pages/ViewClientExtensionPage';

const test = mergeTests(
	clientExtensionsPageTest,
	editJSClientExtensionsPageTest,
	isolatedSiteTest,
	loginTest(),
	pagesAdminPagesTest,
	siteSettingsPagesTest
);
const testSample = mergeTests(loginTest());
const testSampleInstanceScoped = mergeTests(
	editJSClientExtensionsPageTest,
	featureFlagsTest({
		'LPD-30371': {enabled: true},
	}),
	loginTest(),
	styleBookPageTest
);

const SAMPLES = [
	{
		erc: 'LXC:liferay-sample-global-js-1',
		name: 'Liferay Sample Global JS 1',
		url: '',
	},
	{
		erc: 'LXC:liferay-sample-global-js-2',
		name: 'Liferay Sample Global JS 2',
		url: '',
	},
	{
		erc: 'LXC:liferay-sample-global-js-3',
		name: 'Liferay Sample Global JS 3',
		url: '',
	},
];

testSample.describe('Samples', () => {
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
				.getInputByLabel('JavaScript URL')
				.inputValue();

			await expect(
				viewClientExtensionPage.getInputByLabel('JavaScript URL')
			).toHaveValue(sample.url);
		});

		testSample(
			`${sample.name}'s .js file can be downloaded`,
			async ({page}) => {
				const response = await page.goto(sample.url);

				expect(response.status()).toBe(200);
				expect(await response.headerValue('Content-Type')).toBe(
					'application/javascript'
				);
			}
		);
	}
});

testSampleInstanceScoped.describe('Samples (instance scoped)', () => {
	testSampleInstanceScoped(
		'Assert that the instance scoped client extensions are injected into site pages, site control panel pages, and instance control panel pages',
		async ({editJSClientExtensionsPage, page, styleBooksPage}) => {
			const scriptLocator = page.locator(
				`script[src="${SAMPLES[2].url}"]`
			);

			await testSampleInstanceScoped.step(
				'Assert that the client extension is imported into a site page',
				async () => {
					await page.goto('/');

					await expect(scriptLocator).toBeAttached();
				}
			);

			await testSampleInstanceScoped.step(
				'Assert that the client extension is imported into an instance control panel page',
				async () => {
					await editJSClientExtensionsPage.goto();

					await expect(scriptLocator).toBeAttached();
				}
			);

			await testSampleInstanceScoped.step(
				'Assert that the client extension is imported into a site control panel page',
				async () => {
					await styleBooksPage.goto();

					await expect(scriptLocator).toBeAttached();
				}
			);
		}
	);
});

test('Create a new JS client extension with a script element attribute', async ({
	clientExtensionsPage,
	editJSClientExtensionsPage,
	page,
	pagesAdminPage,
}) => {

	// Create a new JS client extension with a script element attribute.

	await editJSClientExtensionsPage.goto();

	const clientExtensionName = getRandomString();
	const clientExtensionValue = getRandomString();

	await editJSClientExtensionsPage.nameInput.fill(clientExtensionName);

	await editJSClientExtensionsPage.javaScriptURLInput.fill(
		'https://www.example.com/script.js'
	);

	await editJSClientExtensionsPage.addScriptAttribute(
		'id',
		'string',
		clientExtensionValue
	);

	await editJSClientExtensionsPage.publish(WaitAction.SUCCESS);

	// Apply JS client extension to all pages.

	await pagesAdminPage.selectClientExtension({
		clientExtensionName,
		type: 'globalJS',
	});

	await page.goto('/');

	await expect(
		page.locator(`script[id="${clientExtensionValue}"]`)
	).toBeAttached();

	// Clean up

	await clientExtensionsPage.goto();

	await clientExtensionsPage.deleteClientExtension(clientExtensionName);
});

test('JS client extension does not allow "src" as a script element attribute', async ({
	editJSClientExtensionsPage,
	page,
}) => {
	await editJSClientExtensionsPage.goto();

	await editJSClientExtensionsPage.addScriptAttribute('src', 'string', '');

	await expect(
		page.getByText('Use the "JavaScript URL" field.')
	).toBeVisible();

	await expect(editJSClientExtensionsPage.publishButton).toBeDisabled();
});

test('JS client extension does not allow a script element attribute with an empty name', async ({
	editJSClientExtensionsPage,
	page,
}) => {
	await editJSClientExtensionsPage.goto();

	await editJSClientExtensionsPage.addScriptAttribute('', 'string', 'value');

	await expect(page.getByText('Attribute field is required.')).toBeVisible();

	await expect(editJSClientExtensionsPage.publishButton).toBeDisabled();
});

test('Assert the help link is pointing to the correct url', async ({
	editJSClientExtensionsPage,
	page,
}) => {
	await editJSClientExtensionsPage.goto();

	const link = page.getByRole('link', {
		name: 'Learn more about browser based client extensions.',
	});

	await expect(link).toBeVisible();

	expect(await link.getAttribute('href')).toBe(
		'https://learn.liferay.com/w/dxp/development/customizing-liferays-look-and-feel'
	);
});

const assertDefaultSelectedLoadType = async (
	clientExtensionName: string,
	page: Page,
	loadType: string
) => {
	const loadTypeSelector = page
		.locator('tr', {hasText: clientExtensionName})
		.locator('.load-type-select');

	await expect(loadTypeSelector).toBeDisabled();

	await expect(loadTypeSelector).toHaveValue(loadType);
};

type TScriptAttribute = {
	name: string;
	type: 'boolean' | 'string';
	value: string;
	valueWhenInPage: string | null;
};

type TJSClientExtensionWithAttributes = {
	clientExtensionName: string;
	clientExtensionsPage: ClientExtensionsPage;
	defaultSelectedLoadType?: string;
	editJSClientExtensionsPage: EditJSClientExtensionsPage;
	page: Page;
	pagesAdminPage: PagesAdminPage;
	scriptAttributes: TScriptAttribute[];
};

const testJSClientExtensionWithAttributes = async ({
	clientExtensionName,
	clientExtensionsPage,
	defaultSelectedLoadType,
	editJSClientExtensionsPage,
	page,
	pagesAdminPage,
	scriptAttributes,
}: TJSClientExtensionWithAttributes) => {

	// Create the JS Client Extension

	await editJSClientExtensionsPage.goto();

	await editJSClientExtensionsPage.nameInput.fill(clientExtensionName);

	await editJSClientExtensionsPage.javaScriptURLInput.fill(
		'https://www.example.com/script.js'
	);

	for (const {name, type, value} of scriptAttributes) {
		await editJSClientExtensionsPage.addScriptAttribute(name, type, value);
	}

	await editJSClientExtensionsPage.publish(WaitAction.SUCCESS);

	// Apply the JS client extension and assert its attributes

	await pagesAdminPage.selectClientExtension({
		clientExtensionName,
		type: 'globalJS',
	});

	await pagesAdminPage.clickOnJavaScriptClientExtensionsTab();

	if (defaultSelectedLoadType) {
		await assertDefaultSelectedLoadType(
			clientExtensionName,
			page,
			defaultSelectedLoadType
		);
	}

	await page.goto('/');

	const scriptElement = page.locator(`script[id="${clientExtensionName}"]`);

	await expect(scriptElement).toBeAttached();

	for (const {name, valueWhenInPage} of scriptAttributes) {
		expect(await scriptElement.getAttribute(name)).toBe(valueWhenInPage);
	}

	// Clean up

	await clientExtensionsPage.goto();

	await clientExtensionsPage.deleteClientExtension(clientExtensionName);
};

test('JS client extension with async and defer attributes set to true', async ({
	clientExtensionsPage,
	editJSClientExtensionsPage,
	page,
	pagesAdminPage,
}) => {
	const clientExtensionName = getRandomString();

	await testJSClientExtensionWithAttributes({
		clientExtensionName,
		clientExtensionsPage,
		defaultSelectedLoadType: 'async',
		editJSClientExtensionsPage,
		page,
		pagesAdminPage,
		scriptAttributes: [
			{
				name: 'async',
				type: 'boolean',
				value: 'True',
				valueWhenInPage: '',
			},
			{
				name: 'defer',
				type: 'boolean',
				value: 'True',
				valueWhenInPage: null,
			},
			{
				name: 'id',
				type: 'string',
				value: clientExtensionName,
				valueWhenInPage: clientExtensionName,
			},
		],
	});
});

test('JS client extension with async attribute set to true', async ({
	clientExtensionsPage,
	editJSClientExtensionsPage,
	page,
	pagesAdminPage,
}) => {
	const clientExtensionName = getRandomString();

	await testJSClientExtensionWithAttributes({
		clientExtensionName,
		clientExtensionsPage,
		defaultSelectedLoadType: 'async',
		editJSClientExtensionsPage,
		page,
		pagesAdminPage,
		scriptAttributes: [
			{
				name: 'async',
				type: 'boolean',
				value: 'True',
				valueWhenInPage: '',
			},
			{
				name: 'id',
				type: 'string',
				value: clientExtensionName,
				valueWhenInPage: clientExtensionName,
			},
		],
	});
});

test('JS client extension with defer attribute set to true', async ({
	clientExtensionsPage,
	editJSClientExtensionsPage,
	page,
	pagesAdminPage,
}) => {
	const clientExtensionName = getRandomString();

	await testJSClientExtensionWithAttributes({
		clientExtensionName,
		clientExtensionsPage,
		defaultSelectedLoadType: 'defer',
		editJSClientExtensionsPage,
		page,
		pagesAdminPage,
		scriptAttributes: [
			{
				name: 'defer',
				type: 'boolean',
				value: 'True',
				valueWhenInPage: '',
			},
			{
				name: 'id',
				type: 'string',
				value: clientExtensionName,
				valueWhenInPage: clientExtensionName,
			},
		],
	});
});

test('JS client extension with async and defer attributes set to false and data-senna-track and type are overridden', async ({
	clientExtensionsPage,
	editJSClientExtensionsPage,
	page,
	pagesAdminPage,
}) => {
	const clientExtensionName = getRandomString();

	await testJSClientExtensionWithAttributes({
		clientExtensionName,
		clientExtensionsPage,
		editJSClientExtensionsPage,
		page,
		pagesAdminPage,
		scriptAttributes: [
			{
				name: 'async',
				type: 'boolean',
				value: 'False',
				valueWhenInPage: null,
			},
			{
				name: 'defer',
				type: 'boolean',
				value: 'False',
				valueWhenInPage: null,
			},
			{
				name: 'data-senna-track',
				type: 'string',
				value: 'permanent',
				valueWhenInPage: 'permanent',
			},
			{
				name: 'id',
				type: 'string',
				value: clientExtensionName,
				valueWhenInPage: clientExtensionName,
			},
			{
				name: 'type',
				type: 'string',
				value: 'module',
				valueWhenInPage: 'module',
			},
		],
	});
});

test('JS client extension can be created with name translations while having a language configuration for the site settings', async ({
	clientExtensionsPage,
	editJSClientExtensionsPage,
	page,
	site,
	siteSettingsLocalizationPage,
}) => {
	await test.step('Set spanish as default language for the site', async () => {
		await siteSettingsLocalizationPage.setCustomDefaultLanguage(
			'Spanish (Spain)',
			site.friendlyUrlPath
		);
	});

	const englishName = getRandomString();
	const spanishName = getRandomString();

	await test.step('Create a JS client extension with english and spanish translations', async () => {
		await editJSClientExtensionsPage.goto();

		await expect(
			page.getByLabel('Current translation is English', {exact: false})
		).toBeVisible();

		await editJSClientExtensionsPage.nameInput.fill(englishName);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				name: 'Not translated into Spanish.',
			}),
			trigger: page.getByRole('button', {
				exact: false,
				name: 'Current translation',
			}),
		});

		await expect(
			page.getByLabel('Current translation is Spanish', {exact: false})
		).toBeVisible();

		await editJSClientExtensionsPage.nameInput.fill(spanishName);

		await editJSClientExtensionsPage.javaScriptURLInput.fill(
			'https://www.example.com/script.js'
		);

		await editJSClientExtensionsPage.publish(WaitAction.SUCCESS);
	});

	await test.step('Assert the name translations for the new JS client extension', async () => {
		await page.getByRole('link', {name: englishName}).click();

		await expect(
			page.getByLabel('Current translation is English', {exact: false})
		).toBeVisible();

		await expect(editJSClientExtensionsPage.nameInput).toHaveValue(
			englishName
		);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				name: 'Translated into Spanish.',
			}),
			trigger: page.getByRole('button', {
				exact: false,
				name: 'Current translation',
			}),
		});

		await expect(editJSClientExtensionsPage.nameInput).toHaveValue(
			spanishName
		);
	});

	await test.step('Delete the JS client extension', async () => {
		await clientExtensionsPage.goto();

		await clientExtensionsPage.deleteClientExtension(englishName);
	});
});

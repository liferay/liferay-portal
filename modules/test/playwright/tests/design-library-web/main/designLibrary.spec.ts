/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {checkAccessibility} from '../../../utils/checkAccessibility';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {designLibrariesPageTest} from './fixtures/designLibrariesPageTest';

const test = mergeTests(
	dataApiHelpersTest,
	designLibrariesPageTest,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
		'LPD-57283': {enabled: true},
	}),
	loginTest()
);

async function expectRedirectionToLibrary(name: string, page: Page) {
	const breadcrumb = page.getByRole('navigation', {
		name: 'Breadcrumb',
	});

	await expect(breadcrumb).toBeVisible();

	const links = breadcrumb.getByRole('link');

	await expect(links).toHaveCount(2);

	await expect(links.first()).toHaveText('Design Libraries');
	await expect(links.last()).toHaveText(name);
}

test(
	'Check if design library is working correctly',
	{tag: '@LPD-79427'},
	async ({designLibrariesPage, page}) => {

		// Go to the design libraries

		await designLibrariesPage.goto();

		await expect(page.getByTestId('header')).toHaveText('Design Libraries');

		// Check the empty state labels

		await expect(page.getByText('No Design Libraries Yet')).toBeVisible();

		await expect(
			page.getByText('Click "New" to create your first Design Library.')
		).toBeVisible();

		await expect(
			page.getByRole('button', {name: 'New Design Library'})
		).toBeVisible();

		// Check the empty state image

		await expect(
			designLibrariesPage.emptyStateContainer.locator(
				'img[src$="/states/design_library_empty_state.svg"]'
			)
		).toBeVisible();
	}
);

test(
	'Can navigate to a design library dashboard',
	{tag: ['@LPD-79427', '@LPD-81219']},
	async ({apiHelpers, designLibrariesPage, page}) => {

		// Create a design library

		const designLibraryName = getRandomString();

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: designLibraryName,
			settings: {},
			type: 'DesignLibrary',
		});

		// Go to the design library

		await designLibrariesPage.goToDesignLibrary(designLibraryName);

		// Check the dashboard elements

		await expectRedirectionToLibrary(designLibraryName, page);

		await page.getByRole('button', {name: 'More Actions'}).click();

		await expect(
			page.getByRole('menu').getByRole('menuitem', {name: 'Settings'})
		).toBeVisible();

		await expect(
			page
				.getByRole('menu')
				.getByRole('menuitem', {name: 'Connected Sites'})
		).toBeVisible();

		await expect(
			page
				.getByRole('menu')
				.getByRole('menuitem', {name: 'Manage Members'})
		).toBeVisible();

		await expect(
			page.getByRole('menu').getByRole('menuitem', {name: 'Import'})
		).toBeVisible();

		await expect(
			page.getByRole('menu').getByRole('menuitem', {name: 'Export'})
		).toBeVisible();

		await expect(
			page.getByRole('menu').getByRole('menuitem', {name: 'Delete'})
		).toBeVisible();
	}
);

test(
	'Should allow managing design libraries through creation, validation, and deletion',
	{tag: '@LPD-79452'},
	async ({designLibrariesPage, page}) => {

		// Create the design libraries

		const mainDesignLibraryName = getRandomString();

		const successScenarios = [
			{
				description: getRandomString(),
				name: mainDesignLibraryName,
			},
			{
				name: getRandomString(),
			},
		];

		for (const scenario of successScenarios) {
			await designLibrariesPage.goto();

			await designLibrariesPage.create(scenario);

			await waitForAlert(
				page,
				`Success:${scenario.name} was created successfully.`
			);

			await expectRedirectionToLibrary(scenario.name, page);
		}

		// Check the empty name error

		await designLibrariesPage.goto();

		await designLibrariesPage.create({name: ''});

		await expect(
			page
				.locator('.form-feedback-item')
				.getByText('Error: This field is required.')
		).toBeVisible();

		await expect(page.getByRole('button', {name: 'Save'})).toBeDisabled();

		// Check the duplicate name error

		await designLibrariesPage.goto();

		await designLibrariesPage.create({name: mainDesignLibraryName});

		await waitForAlert(page, 'Error:Please enter a unique name.', {
			timeout: 5000,
			type: 'danger',
		});

		await expect(
			page
				.locator('.form-feedback-item')
				.getByText('Error: Please enter a unique name.')
		).toBeVisible();

		await expect(page.getByLabel('Name')).toHaveValue(
			mainDesignLibraryName
		);

		// Delete the design libraries

		await designLibrariesPage.goto();

		for (const {name} of successScenarios) {
			await designLibrariesPage.delete(name);
		}
	}
);

test(
	'Can connect and disconnect a site from a design library',
	{tag: '@LPD-79453'},
	async ({apiHelpers, designLibrariesPage, page}) => {

		// Create a design library

		const designLibraryName = getRandomString();

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: designLibraryName,
			settings: {},
			type: 'DesignLibrary',
		});

		// Connect a site

		await designLibrariesPage.goToDesignLibrary(designLibraryName);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page
				.getByRole('menu')
				.getByRole('menuitem', {name: 'Connected Sites'}),
			trigger: page.getByRole('button', {name: 'More Actions'}),
		});

		const connectedSitesDialog = page.getByRole('dialog');
		const siteName = 'Liferay DXP Site';

		await connectedSitesDialog
			.getByPlaceholder('Select a Site')
			.fill(siteName);

		await page.getByRole('option', {name: siteName}).click();

		await expect(
			connectedSitesDialog.getByRole('button', {name: 'Connect'})
		).toBeEnabled();

		await connectedSitesDialog
			.getByRole('button', {name: 'Connect'})
			.click();

		await waitForAlert(
			page,
			`Success:Site ${siteName} was successfully connected to the design library.`,
			{autoClose: false}
		);

		// Disconnect the site

		await expect(
			connectedSitesDialog.getByRole('button', {name: 'Disconnect'})
		).toBeVisible();

		await connectedSitesDialog
			.getByRole('button', {name: 'Disconnect'})
			.click();

		await waitForAlert(
			page,
			`Success:Site ${siteName} was successfully disconnected from the design library.`,
			{autoClose: false}
		);
	}
);

test(
	'Can export and import a design library from the listing row',
	{tag: ['@LPD-79455']},
	async ({apiHelpers, designLibrariesPage, page}) => {

		// Create a design library

		const designLibraryName = getRandomString();

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: designLibraryName,
			settings: {},
			type: 'DesignLibrary',
		});

		const openRowActions = async () => {
			await page
				.getByRole('cell', {name: `${designLibraryName} Actions`})
				.getByRole('button', {name: 'Actions'})
				.click();

			await expect(page.getByRole('menu')).toBeVisible();
		};

		const expectCleanBreadcrumb = async () => {
			const breadcrumb = page.locator('.breadcrumb').first();

			await expect(breadcrumb).toBeVisible();

			await expect(breadcrumb.getByText('Asset Libraries')).toBeHidden();

			await expect(breadcrumb.getByText(designLibraryName)).toBeVisible();
		};

		// Check the row actions

		await designLibrariesPage.goto();

		await openRowActions();

		await expect(
			page.getByRole('menu').getByRole('menuitem', {name: 'Export'})
		).toBeVisible();

		await expect(
			page.getByRole('menu').getByRole('menuitem', {name: 'Import'})
		).toBeVisible();

		// Go to Export

		await page
			.getByRole('menu')
			.getByRole('menuitem', {name: 'Export'})
			.click();

		await expect(page).toHaveURL(/asset-library-\d+/);
		await expect(page).toHaveURL(/backURL=/);
		await expect(page).toHaveURL(
			/p_p_id=com_liferay_exportimport_web_portlet_ExportPortlet/
		);

		// Check the Export breadcrumb

		await expectCleanBreadcrumb();

		// Go to Import

		await designLibrariesPage.goto();

		await openRowActions();

		await page
			.getByRole('menu')
			.getByRole('menuitem', {name: 'Import'})
			.click();

		await expect(page).toHaveURL(/asset-library-\d+/);
		await expect(page).toHaveURL(/backURL=/);
		await expect(page).toHaveURL(
			/p_p_id=com_liferay_exportimport_web_portlet_ImportPortlet/
		);

		// Check the Import breadcrumb

		await expectCleanBreadcrumb();
	}
);

test(
	'Can view and edit a design library settings',
	{tag: '@LPD-79533'},
	async ({apiHelpers, designLibrariesPage, page}) => {

		// Create a design library

		const designLibraryName = getRandomString();

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: designLibraryName,
			settings: {},
			type: 'DesignLibrary',
		});

		// Check the design library settings

		await designLibrariesPage.goToDesignLibrary(designLibraryName);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page
				.getByRole('menu')
				.getByRole('menuitem', {name: 'Settings'}),
			trigger: page.getByRole('button', {name: 'More Actions'}),
		});

		const headerTitle = page.getByTestId('headerTitle');

		await expect(headerTitle).toBeVisible();
		await expect(headerTitle).toHaveText(`${designLibraryName} Settings`);

		await expect(page.getByRole('textbox', {name: 'Name'})).toHaveValue(
			designLibraryName
		);

		await expect(
			page.getByRole('textbox', {name: 'Description'})
		).toHaveValue('');

		// Edit the design library settings

		const editedDesignLibraryName = getRandomString();

		await page
			.getByRole('textbox', {name: 'Name'})
			.fill(editedDesignLibraryName);

		const editedDesignLibraryDescription = getRandomString();

		await page
			.getByRole('textbox', {name: 'Description'})
			.fill(editedDesignLibraryDescription);

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(
			page,
			`Success:${editedDesignLibraryName} was saved successfully.`
		);

		await expect(page.getByRole('textbox', {name: 'Name'})).toHaveValue(
			editedDesignLibraryName
		);

		await expect(
			page.getByRole('textbox', {name: 'Description'})
		).toHaveValue(editedDesignLibraryDescription);

		// Check the accessibility

		await checkAccessibility({
			page,
			selectors: ['.portlet-body'],
		});
	}
);

test(
	'Can open the manage members modal from a design library',
	{tag: '@LPD-79454'},
	async ({apiHelpers, designLibrariesPage, page}) => {

		// Create a design library

		const designLibraryName = getRandomString();

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: designLibraryName,
			settings: {},
			type: 'DesignLibrary',
		});

		// Open the Manage Members modal

		await designLibrariesPage.goToDesignLibrary(designLibraryName);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page
				.getByRole('menu')
				.getByRole('menuitem', {name: 'Manage Members'}),
			trigger: page.getByRole('button', {name: 'More Actions'}),
		});

		const manageMembersDialog = page.getByRole('dialog');

		await expect(
			manageMembersDialog.getByText('Manage Members')
		).toBeVisible();

		// Check the form and members

		await expect(
			manageMembersDialog.getByRole('combobox', {
				name: 'Add People to Collaborate',
			})
		).toBeVisible();

		const membersList = manageMembersDialog.getByRole('list', {
			name: 'Who Has Access',
		});

		const ownerItem = membersList.getByRole('listitem');

		await expect(ownerItem).toBeVisible();

		await expect(ownerItem).toHaveText('Test Test(You)(Owner)', {
			ignoreCase: true,
		});
	}
);

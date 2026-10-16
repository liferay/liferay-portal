/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {checkAccessibility} from '../../../utils/checkAccessibility';
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

test(
	'Check if design library is working correctly',
	{tag: '@LPD-79427'},
	async ({designLibrariesPage, page}) => {

		// Go to the design libraries

		await designLibrariesPage.goto();

		await expect(page.getByTestId('headerTitle')).toHaveText(
			'Design Libraries'
		);

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

		const designLibrary =
			await apiHelpers.headlessAssetLibrary.createDesignLibrary();

		// Go to the design library

		await designLibrariesPage.goToDesignLibrary(designLibrary.name);

		// Check the dashboard elements

		await designLibrariesPage.assertDesignLibraryBreadcrumb(
			designLibrary.name
		);

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

		await designLibrariesPage.addDesignLibrary({
			description: getRandomString(),
			name: mainDesignLibraryName,
		});

		const designLibraryName = getRandomString();

		await designLibrariesPage.addDesignLibrary({name: designLibraryName});

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

		await designLibrariesPage.delete(designLibraryName);
		await designLibrariesPage.delete(mainDesignLibraryName);
	}
);

test(
	'Can connect and disconnect a site from a design library',
	{tag: '@LPD-79453'},
	async ({apiHelpers, designLibrariesPage, page}) => {

		// Create a design library

		const designLibrary =
			await apiHelpers.headlessAssetLibrary.createDesignLibrary();

		// Connect a site

		await designLibrariesPage.goToDesignLibrary(designLibrary.name);

		await designLibrariesPage.clickMoreActionsMenuItem('Connected Sites');

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

		const designLibrary =
			await apiHelpers.headlessAssetLibrary.createDesignLibrary();

		const expectPortlet = async (portletName: string) => {
			await expect(page).toHaveURL(/asset-library-\d+/);
			await expect(page).toHaveURL(/backURL=/);
			await expect(page).toHaveURL(
				new RegExp(
					`p_p_id=com_liferay_exportimport_web_portlet_${portletName}`
				)
			);

			const breadcrumb = page.locator('.breadcrumb').first();

			await expect(breadcrumb).toBeVisible();

			await expect(breadcrumb.getByText('Asset Libraries')).toBeHidden();

			await expect(
				breadcrumb.getByText(designLibrary.name)
			).toBeVisible();
		};

		// Check the row actions

		await designLibrariesPage.goto();

		await page
			.getByRole('cell', {name: `${designLibrary.name} Actions`})
			.getByRole('button', {name: 'Actions'})
			.click();

		await expect(
			page.getByRole('menu').getByRole('menuitem', {name: 'Export'})
		).toBeVisible();

		await expect(
			page.getByRole('menu').getByRole('menuitem', {name: 'Import'})
		).toBeVisible();

		// Go to Export

		await designLibrariesPage.clickRowActionsMenuItem(
			designLibrary.name,
			'Export'
		);

		await expectPortlet('ExportPortlet');

		// Go to Import

		await designLibrariesPage.goto();

		await designLibrariesPage.clickRowActionsMenuItem(
			designLibrary.name,
			'Import'
		);

		await expectPortlet('ImportPortlet');
	}
);

test(
	'Can view and edit a design library settings',
	{tag: '@LPD-79533'},
	async ({apiHelpers, designLibrariesPage, page}) => {

		// Create a design library

		const designLibrary =
			await apiHelpers.headlessAssetLibrary.createDesignLibrary();

		// Check the design library settings

		await designLibrariesPage.goToDesignLibrary(designLibrary.name);

		await designLibrariesPage.clickMoreActionsMenuItem('Settings');

		const headerTitle = page.getByTestId('headerTitle');

		await expect(headerTitle).toBeVisible();
		await expect(headerTitle).toHaveText(`${designLibrary.name} Settings`);

		await expect(page.getByRole('textbox', {name: 'Name'})).toHaveValue(
			designLibrary.name
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

		const designLibrary =
			await apiHelpers.headlessAssetLibrary.createDesignLibrary();

		// Open the Manage Members modal

		await designLibrariesPage.goToDesignLibrary(designLibrary.name);

		await designLibrariesPage.clickMoreActionsMenuItem('Manage Members');

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

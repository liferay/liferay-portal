/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {checkAccessibility} from '../../../utils/checkAccessibility';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {designLibrariesPageTest} from './fixtures/designLibrariesPageTest';

const test = mergeTests(
	apiHelpersTest,
	designLibrariesPageTest,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
		'LPD-17564': {enabled: true},
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
		await test.step('Can navigate to design libraries page', async () => {
			await designLibrariesPage.goto();

			await expect(page.getByTestId('header')).toHaveText(
				'Design Libraries'
			);
		});

		await test.step('Check that the empty state labels are correct', async () => {
			await expect(
				page.getByText('No Design Libraries Yet')
			).toBeVisible();

			await expect(
				page.getByText(
					'Click "New" to create your first Design Library.'
				)
			).toBeVisible();

			await expect(
				page.getByRole('button', {name: 'New Design Library'})
			).toBeVisible();
		});

		await test.step('Check that the "/states/design_library_empty_state.svg" image is displayed', async () => {
			await expect(
				designLibrariesPage.emptyStateContainer.locator(
					'img[src$="/states/design_library_empty_state.svg"]'
				)
			).toBeVisible();
		});
	}
);

test(
	'Can navigate to a design library dashboard',
	{tag: ['@LPD-79427', '@LPD-81219']},
	async ({apiHelpers, designLibrariesPage, page}) => {
		const designLibraryName = getRandomString();

		const createdDesignLibrary =
			await test.step('Create temporary design library via headless', async () => {
				return await apiHelpers.headlessAssetLibrary.createAssetLibrary(
					{
						name: designLibraryName,
						settings: {},
						type: 'DesignLibrary',
					}
				);
			});

		await test.step('Navigate to a design library dashboard', async () => {
			await designLibrariesPage.goto();

			const designLibraryLink = page.getByRole('link', {
				name: designLibraryName,
			});

			await expect(designLibraryLink).toBeVisible();

			await designLibraryLink.click();
		});

		await test.step('Check dashboard elements', async () => {
			const breadcrumb = page.getByRole('navigation', {
				name: 'Breadcrumb',
			});

			await expect(breadcrumb).toBeVisible();

			const links = breadcrumb.getByRole('link');

			expect(await links.count()).toEqual(2);

			expect(links.first()).toHaveText('Design Libraries');

			expect(links.last()).toHaveText(designLibraryName);

			const moreActionsButton = page.getByRole('button', {
				name: 'More Actions',
			});

			await moreActionsButton.click();

			await expect(page.getByRole('menu')).toBeVisible();

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
		});

		await test.step('Remove temporary design library', async () => {
			await apiHelpers.headlessAssetLibrary.deleteAssetLibrary(
				createdDesignLibrary.externalReferenceCode
			);
		});
	}
);

test(
	'Should allow managing design libraries through creation, validation, and deletion',
	{tag: '@LPD-79452'},
	async ({designLibrariesPage, page}) => {
		const mainDesignLibraryName = getRandomString();

		const successScenarios = [
			{
				description: getRandomString(),
				name: mainDesignLibraryName,
				stepName: 'Create a design library with all fields populated',
			},
			{
				name: getRandomString(),
				stepName: 'Create a design library with only mandatory fields',
			},
		];

		for (const scenario of successScenarios) {
			await test.step(scenario.stepName, async () => {
				await designLibrariesPage.goto();

				await designLibrariesPage.create(scenario);

				await waitForAlert(
					page,
					`Success:${scenario.name} was created successfully.`
				);

				await expectRedirectionToLibrary(scenario.name, page);
			});
		}

		await test.step('Prevent creation of design library with empty name', async () => {
			await designLibrariesPage.goto();

			await designLibrariesPage.create({
				name: '',
			});

			await expect(
				page
					.locator('.form-feedback-item')
					.getByText('Error: This field is required.')
			).toBeVisible();

			await expect(
				page.getByRole('button', {name: 'Save'})
			).toBeDisabled();
		});

		await test.step('Prevent creation of duplicate design libraries and maintain modal state', async () => {
			await designLibrariesPage.goto();

			await designLibrariesPage.create({
				name: mainDesignLibraryName,
			});

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
		});

		await test.step('Delete created design libraries', async () => {
			await designLibrariesPage.goto();

			for (const {name} of successScenarios) {
				await designLibrariesPage.delete(name);
			}
		});
	}
);

test(
	'Can connect and disconnect a site from a design library',
	{tag: '@LPD-79453'},
	async ({apiHelpers, designLibrariesPage, page}) => {
		const designLibraryName = getRandomString();
		const siteName = 'Liferay DXP Site';

		const connectedSitesDialog = page.getByRole('dialog');

		const createdDesignLibrary =
			await test.step('Create a new design library via headless', async () => {
				return await apiHelpers.headlessAssetLibrary.createAssetLibrary(
					{
						name: designLibraryName,
						settings: {},
						type: 'DesignLibrary',
					}
				);
			});

		await test.step('Connect a site to design library', async () => {
			await designLibrariesPage.goToDesignLibrary(designLibraryName);

			const connectedSitesMenuItem = page
				.getByRole('menu')
				.getByRole('menuitem', {name: 'Connected Sites'});

			await page
				.getByRole('button', {
					name: 'More Actions',
				})
				.click();

			await expect(page.getByRole('menu')).toBeVisible();

			await expect(connectedSitesMenuItem).toBeVisible();

			await connectedSitesMenuItem.click();

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
		});

		await test.step('Disconnect a site from a design library', async () => {
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
		});

		await test.step('Remove created design library', async () => {
			await apiHelpers.headlessAssetLibrary.deleteAssetLibrary(
				createdDesignLibrary.externalReferenceCode
			);
		});
	}
);

test(
	'Can export and import a design library from the listing row',
	{tag: ['@LPD-79455']},
	async ({apiHelpers, designLibrariesPage, page}) => {
		const designLibraryName = getRandomString();

		const createdDesignLibrary =
			await test.step('Create temporary design library via headless', async () => {
				return await apiHelpers.headlessAssetLibrary.createAssetLibrary(
					{
						name: designLibraryName,
						settings: {},
						type: 'DesignLibrary',
					}
				);
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

		try {
			await test.step('Listing row ellipsis exposes Export and Import actions', async () => {
				await designLibrariesPage.goto();

				await openRowActions();

				await expect(
					page
						.getByRole('menu')
						.getByRole('menuitem', {name: 'Export'})
				).toBeVisible();

				await expect(
					page
						.getByRole('menu')
						.getByRole('menuitem', {name: 'Import'})
				).toBeVisible();
			});

			await test.step('Clicking Export navigates to the Export portlet scoped to the design library with a back URL', async () => {
				await page
					.getByRole('menu')
					.getByRole('menuitem', {name: 'Export'})
					.click();

				await expect(page).toHaveURL(
					/p_p_id=com_liferay_exportimport_web_portlet_ExportPortlet/
				);

				await expect(page).toHaveURL(/asset-library-\d+/);

				await expect(page).toHaveURL(/backURL=/);
			});

			await test.step('Export portlet breadcrumb omits Asset Libraries and includes the design library', async () => {
				await expectCleanBreadcrumb();
			});

			await test.step('Clicking Import navigates to the Import portlet scoped to the design library with a back URL', async () => {
				await designLibrariesPage.goto();

				await openRowActions();

				await page
					.getByRole('menu')
					.getByRole('menuitem', {name: 'Import'})
					.click();

				await expect(page).toHaveURL(
					/p_p_id=com_liferay_exportimport_web_portlet_ImportPortlet/
				);

				await expect(page).toHaveURL(/asset-library-\d+/);

				await expect(page).toHaveURL(/backURL=/);
			});

			await test.step('Import portlet breadcrumb omits Asset Libraries and includes the design library', async () => {
				await expectCleanBreadcrumb();
			});
		}
		finally {
			await test.step('Remove temporary design library', async () => {
				await apiHelpers.headlessAssetLibrary.deleteAssetLibrary(
					createdDesignLibrary.externalReferenceCode
				);
			});
		}
	}
);

test(
	'Can view and edit a design library settings',
	{tag: '@LPD-79533'},
	async ({apiHelpers, designLibrariesPage, page}) => {
		const designLibraryName = getRandomString();
		const editedDesignLibraryName = getRandomString();
		const editedDesignLibraryDescription = getRandomString();

		const createdDesignLibrary =
			await test.step('Create a new design library via headless', async () => {
				return await apiHelpers.headlessAssetLibrary.createAssetLibrary(
					{
						name: designLibraryName,
						settings: {},
						type: 'DesignLibrary',
					}
				);
			});

		await test.step('View the design library settings', async () => {
			await designLibrariesPage.goToDesignLibrary(designLibraryName);

			const settingsMenuItem = page
				.getByRole('menu')
				.getByRole('menuitem', {name: 'Settings'});

			await page
				.getByRole('button', {
					name: 'More Actions',
				})
				.click();

			await expect(page.getByRole('menu')).toBeVisible();

			await expect(settingsMenuItem).toBeVisible();

			await settingsMenuItem.click();

			const headerTitle = page.getByTestId('headerTitle');

			await expect(headerTitle).toBeVisible();

			await expect(headerTitle).toHaveText(
				`${designLibraryName} Settings`
			);

			await expect(page.getByRole('textbox', {name: 'Name'})).toHaveValue(
				designLibraryName
			);

			await expect(
				page.getByRole('textbox', {name: 'Description'})
			).toHaveValue('');
		});

		await test.step('Edit the design library settings', async () => {
			await page
				.getByRole('textbox', {name: 'Name'})
				.fill(editedDesignLibraryName);

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
		});

		await test.step('Check the accessibility for the design library settings page', async () => {
			await checkAccessibility({
				page,
				selectors: ['.portlet-body'],
			});
		});

		await test.step('Remove created design library', async () => {
			await apiHelpers.headlessAssetLibrary.deleteAssetLibrary(
				createdDesignLibrary.externalReferenceCode
			);
		});
	}
);

test(
	'Can open the manage members modal from a design library',
	{tag: '@LPD-79454'},
	async ({apiHelpers, designLibrariesPage, page}) => {
		const designLibraryName = getRandomString();

		const manageMembersDialog = page.getByRole('dialog');

		const createdDesignLibrary =
			await test.step('Create a new design library via headless', async () => {
				return await apiHelpers.headlessAssetLibrary.createAssetLibrary(
					{
						name: designLibraryName,
						settings: {},
						type: 'DesignLibrary',
					}
				);
			});

		await test.step('Open the design library actions menu', async () => {
			await designLibrariesPage.goToDesignLibrary(designLibraryName);

			await page.getByRole('button', {name: 'More Actions'}).click();

			await expect(page.getByRole('menu')).toBeVisible();
		});

		await test.step('Open the manage members modal from the menu', async () => {
			await page
				.getByRole('menu')
				.getByRole('menuitem', {name: 'Manage Members'})
				.click();

			await expect(manageMembersDialog).toBeVisible();

			await expect(
				manageMembersDialog.getByText('Manage Members')
			).toBeVisible();
		});

		await test.step('Check the add people to collaborate form and member list', async () => {
			await expect(
				manageMembersDialog.getByRole('combobox', {
					name: 'Add People to Collaborate',
				})
			).toBeVisible();

			const membersList = manageMembersDialog.getByRole('list', {
				name: 'Who Has Access',
			});

			await expect(membersList).toBeVisible();

			const ownerItem = membersList.getByRole('listitem');

			await expect(ownerItem).toBeVisible();

			expect(ownerItem).toHaveText('Test Test(You)(Owner)', {
				ignoreCase: true,
			});
		});

		await test.step('Remove created design library', async () => {
			await apiHelpers.headlessAssetLibrary.deleteAssetLibrary(
				createdDesignLibrary.externalReferenceCode
			);
		});
	}
);

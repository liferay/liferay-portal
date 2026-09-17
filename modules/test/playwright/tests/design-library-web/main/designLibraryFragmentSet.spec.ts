/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {designLibrariesPageTest} from './fixtures/designLibrariesPageTest';

const test = mergeTests(
	apiHelpersTest,
	designLibrariesPageTest,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
		'LPD-35443': {enabled: true},
		'LPD-57283': {enabled: true},
		'LPD-76864': {enabled: true},
	}),
	loginTest()
);

test(
	'Editing a Design Library fragment set redirects back to the resources listing',
	{tag: ['@LPD-97638', '@LPD-102114']},
	async ({apiHelpers, designLibrariesPage, page}) => {
		const designLibraryName = getRandomString();
		const fragmentSetName = getRandomString();

		const createdDesignLibrary =
			await test.step('Create a design library via headless', async () => {
				return await apiHelpers.headlessAssetLibrary.createAssetLibrary(
					{
						name: designLibraryName,
						settings: {},
						type: 'DesignLibrary',
					}
				);
			});

		try {
			const modal = page.getByRole('dialog', {name: 'Add Fragment Set'});

			await test.step('Create a fragment set from the design library resources view', async () => {
				await designLibrariesPage.goToDesignLibrary(designLibraryName);

				await page
					.getByRole('button', {name: 'New Fragment Set'})
					.or(page.getByRole('button', {exact: true, name: 'New'}))
					.first()
					.click();

				const newFragmentSetMenuItem = page.getByRole('menuitem', {
					name: 'New Fragment Set',
				});

				if (await newFragmentSetMenuItem.isVisible()) {
					await newFragmentSetMenuItem.click();
				}

				await expect(modal).toBeVisible();

				await modal.getByLabel('Name').fill(fragmentSetName);

				await modal.getByRole('button', {name: 'Save'}).click();

				await expect(modal).toBeHidden();
			});

			const contentTable = page.locator(
				'.design-library-fds-wrapper--resources table'
			);

			const fragmentSetRow = contentTable.getByRole('row', {
				name: fragmentSetName,
			});

			await test.step('The new fragment set appears in the resources listing', async () => {
				await expect(fragmentSetRow).toBeVisible();
			});

			await test.step('Open the fragment set editor from the row actions menu', async () => {
				await fragmentSetRow
					.getByRole('button', {name: /Actions$/})
					.click();

				await page
					.getByRole('menuitem', {exact: true, name: 'Edit'})
					.click();

				await expect(page).toHaveURL(/fragment_collection/);
				await expect(page).toHaveURL(/redirect=/);
			});

			const editedFragmentSetName = getRandomString();

			await test.step('Saving the edited fragment set returns to the resources listing', async () => {
				await page
					.getByRole('textbox', {name: 'Name'})
					.fill(editedFragmentSetName);

				await page.getByRole('button', {name: 'Save'}).click();

				await expect(page).toHaveURL(/view_resources_design_library/);

				await expect(
					contentTable.getByRole('row', {
						name: editedFragmentSetName,
					})
				).toBeVisible();
			});

			await test.step('Open the fragment set from the resources listing row link', async () => {
				await contentTable
					.getByRole('row', {name: editedFragmentSetName})
					.getByRole('link')
					.first()
					.click();

				await expect(page).toHaveURL(/fragment_collection/);
			});

			await test.step('Reload the fragment set without the back parameters, as when returning from the fragment editor', async () => {
				const url = new URL(page.url());

				for (const key of [...url.searchParams.keys()]) {
					if (key.endsWith('_backURL') || key.endsWith('_redirect')) {
						url.searchParams.delete(key);
					}
				}

				await page.goto(url.toString());
			});

			const backLink = page.locator(
				'.control-menu-nav-item a.control-menu-nav-link'
			);

			await test.step('The back button does not point at the fragment set itself', async () => {
				await expect(backLink).toHaveAttribute(
					'href',
					/view_resources_design_library/
				);
			});

			await test.step('Clicking the back button returns to the resources listing', async () => {
				await backLink.click();

				await expect(
					page.getByRole('link', {name: editedFragmentSetName})
				).toBeVisible();

				await expect(page).toHaveURL(/view_resources_design_library/);
			});
		}
		finally {
			await test.step('Remove the design library', async () => {
				await apiHelpers.headlessAssetLibrary.deleteAssetLibrary(
					createdDesignLibrary.externalReferenceCode
				);
			});
		}
	}
);

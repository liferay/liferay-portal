/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {fragmentsPagesTest} from '../../../fixtures/fragmentPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {designLibrariesPageTest} from './fixtures/designLibrariesPageTest';

const test = mergeTests(
	dataApiHelpersTest,
	designLibrariesPageTest,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
		'LPD-57283': {enabled: true},
		'LPD-76864': {enabled: true},
	}),
	fragmentsPagesTest,
	loginTest()
);

test(
	'Publishing a new Design Library fragment returns to its fragment set',
	{tag: '@LPD-110093'},
	async ({apiHelpers, designLibrariesPage, fragmentEditorPage, page}) => {

		// Create a design library

		const designLibraryName = getRandomString();

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: designLibraryName,
			settings: {},
			type: 'DesignLibrary',
		});

		// Add a fragment in a new fragment set

		await designLibrariesPage.goToDesignLibrary(designLibraryName);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				name: 'New Basic Fragment',
			}),
			trigger: page.getByRole('button', {
				exact: true,
				name: 'New',
			}),
		});

		const fragmentEntryName = getRandomString();
		const modal = page.getByRole('dialog', {name: 'Add Fragment'});

		await modal.getByLabel('Fragment Name').fill(fragmentEntryName);

		await modal.getByLabel('Fragment Set Name').fill(getRandomString());

		await modal.getByRole('button', {name: 'Save'}).click();

		await expect(page).toHaveURL(/fragment_entry/);

		// Publish and check the fragment set

		await fragmentEditorPage.publish();

		await expect(page).not.toHaveURL(/view_resources_design_library/);

		await expect(
			page.getByRole('link', {exact: true, name: fragmentEntryName})
		).toBeVisible();
	}
);

test(
	'Editing a Design Library fragment set redirects back to the resources listing',
	{tag: ['@LPD-97638', '@LPD-102114']},
	async ({apiHelpers, designLibrariesPage, page}) => {

		// Create a design library

		const designLibraryName = getRandomString();

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: designLibraryName,
			settings: {},
			type: 'DesignLibrary',
		});

		// Add a fragment set

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

		const fragmentSetName = getRandomString();
		const modal = page.getByRole('dialog', {name: 'Add Fragment Set'});

		await expect(modal).toBeVisible();

		await modal.getByLabel('Name').fill(fragmentSetName);

		await modal.getByRole('button', {name: 'Save'}).click();

		await expect(modal).toBeHidden();

		// Check the fragment set in the listing

		const contentTable = page.locator(
			'.design-library-fds-wrapper--resources table'
		);

		const fragmentSetRow = contentTable.getByRole('row', {
			name: fragmentSetName,
		});

		await expect(fragmentSetRow).toBeVisible();

		// Edit the fragment set

		await fragmentSetRow.getByRole('button', {name: /Actions$/}).click();

		await page.getByRole('menuitem', {exact: true, name: 'Edit'}).click();

		await expect(page).toHaveURL(/fragment_collection/);
		await expect(page).toHaveURL(/redirect=/);

		// Save and check the listing

		const editedFragmentSetName = getRandomString();

		await page
			.getByRole('textbox', {name: 'Name'})
			.fill(editedFragmentSetName);

		await page.getByRole('button', {name: 'Save'}).click();

		await expect(page).toHaveURL(/view_resources_design_library/);

		await expect(
			contentTable.getByRole('row', {name: editedFragmentSetName})
		).toBeVisible();

		// Open the fragment set

		await contentTable
			.getByRole('row', {name: editedFragmentSetName})
			.getByRole('link')
			.first()
			.click();

		await expect(page).toHaveURL(/fragment_collection/);

		// Reload without the back parameters

		const url = new URL(page.url());

		for (const key of [...url.searchParams.keys()]) {
			if (key.endsWith('_backURL') || key.endsWith('_redirect')) {
				url.searchParams.delete(key);
			}
		}

		await page.goto(url.toString());

		const backLink = page.locator(
			'.control-menu-nav-item a.control-menu-nav-link'
		);

		// Check the back link

		await expect(backLink).toHaveAttribute(
			'href',
			/view_resources_design_library/
		);

		// Go back and check the listing

		await backLink.click();

		await expect(
			page.getByRole('link', {name: editedFragmentSetName})
		).toBeVisible();

		await expect(page).toHaveURL(/view_resources_design_library/);
	}
);

/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {checkAccessibility} from '../../../utils/checkAccessibility';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {hoverAndExpectToBeVisible} from '../../../utils/hoverAndExpectToBeVisible';
import {designLibrariesPageTest} from './fixtures/designLibrariesPageTest';

const test = mergeTests(
	dataApiHelpersTest,
	designLibrariesPageTest,
	featureFlagsTest({
		'LPD-57283': {enabled: true},
	}),
	loginTest()
);

test(
	'Shows the revamped per-library home with members and connected sites',
	{tag: ['@LPD-108834', '@LPD-99371']},
	async ({apiHelpers, designLibrariesPage, page}) => {

		// Create a design library

		const designLibrary =
			await apiHelpers.headlessAssetLibrary.createDesignLibrary();

		// Go to the design library

		await designLibrariesPage.goToDesignLibrary(designLibrary.name);

		// Check the Design Assets section

		await expect(
			page.getByRole('heading', {name: 'Design Assets'})
		).toBeVisible();

		await expect(
			page.getByRole('button', {name: 'Add Asset'})
		).toBeVisible();

		// Check the Add Asset menu

		const fragmentsMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Fragments',
		});

		await clickAndExpectToBeVisible({
			target: fragmentsMenuItem,
			trigger: page.getByRole('button', {name: 'Add Asset'}),
		});

		const newContentPageTemplateMenuItem = page.getByRole('menuitem', {
			name: 'New Content Page Template',
		});

		await hoverAndExpectToBeVisible({
			target: newContentPageTemplateMenuItem,
			trigger: page.getByRole('menuitem', {
				exact: true,
				name: 'Page Templates',
			}),
		});
		await hoverAndExpectToBeVisible({
			target: page.getByRole('menuitem', {name: 'New Basic Fragment'}),
			trigger: fragmentsMenuItem,
		});

		await expect(newContentPageTemplateMenuItem).toBeHidden();

		await expect(async () => {
			await page.keyboard.press('Escape');

			await expect(fragmentsMenuItem).toBeHidden({timeout: 500});
		}).toPass();

		// Check the Members section

		await expect(
			page.getByRole('heading', {name: /^Members/})
		).toBeVisible();

		await expect(page.getByRole('tab', {name: 'Users'})).toBeVisible();

		await expect(
			page.getByRole('tab', {name: 'User Groups'})
		).toBeVisible();

		await expect(page.getByText('(Owner)')).toBeVisible();

		// Check the Manage Members button

		await expect(
			page.getByRole('button', {name: 'Manage Members'})
		).toBeVisible();

		// Check the Connected Sites section

		await expect(
			page.getByRole('heading', {name: /^Connected Sites/})
		).toBeVisible();

		await expect(
			page.getByText('No Sites Are Connected Yet')
		).toBeVisible();

		await expect(
			page.getByRole('button', {name: 'Manage Sites'})
		).toBeHidden();

		// Check the accessibility

		await checkAccessibility({
			page,
			selectors: ['.portlet-body'],
		});
	}
);

/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {checkAccessibility} from '../../../utils/checkAccessibility';
import getRandomString from '../../../utils/getRandomString';
import {designLibrariesPageTest} from './fixtures/designLibrariesPageTest';

const test = mergeTests(
	apiHelpersTest,
	designLibrariesPageTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
		'LPD-57283': {enabled: true},
	}),
	loginTest()
);

test(
	'Shows the revamped per-library home with members and connected sites',
	{tag: '@LPD-99371'},
	async ({apiHelpers, designLibrariesPage, page}) => {
		const designLibraryName = getRandomString();

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

		try {
			await test.step('Navigate into the design library', async () => {
				await designLibrariesPage.goToDesignLibrary(designLibraryName);
			});

			await test.step('Design Assets section exposes the Add Asset action', async () => {
				await expect(
					page.getByRole('heading', {name: 'Design Assets'})
				).toBeVisible();

				await expect(
					page.getByRole('button', {name: 'Add Asset'})
				).toBeVisible();
			});

			await test.step('Members section shows the owner and the Users and User Groups tabs', async () => {
				await expect(
					page.getByRole('heading', {name: /^Members/})
				).toBeVisible();

				await expect(
					page.getByRole('tab', {name: 'Users'})
				).toBeVisible();

				await expect(
					page.getByRole('tab', {name: 'User Groups'})
				).toBeVisible();

				await expect(page.getByText('(Owner)')).toBeVisible();
			});

			await test.step('An owner can manage members', async () => {
				await expect(
					page.getByRole('button', {name: 'Manage Members'})
				).toBeVisible();
			});

			await test.step('Connected Sites section is empty and hides Manage Sites when there are none', async () => {
				await expect(
					page.getByRole('heading', {name: /^Connected Sites/})
				).toBeVisible();

				await expect(
					page.getByText('No Sites Are Connected Yet')
				).toBeVisible();

				await expect(
					page.getByRole('button', {name: 'Manage Sites'})
				).toBeHidden();
			});

			await test.step('Check the accessibility for the design library home', async () => {
				await checkAccessibility({
					page,
					selectors: ['.portlet-body'],
				});
			});
		}
		finally {
			await test.step('Remove created design library', async () => {
				await apiHelpers.headlessAssetLibrary.deleteAssetLibrary(
					createdDesignLibrary.externalReferenceCode
				);
			});
		}
	}
);

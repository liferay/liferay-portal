/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {fragmentsPagesTest} from '../../../fixtures/fragmentPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageManagementSiteTest} from '../../../fixtures/pageManagementSiteTest';
import getRandomString from '../../../utils/getRandomString';
import performLogin, {
	performLogout,
	userData,
} from '../../../utils/performLogin';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	featureFlagsTest({
		'LPD-35941': {enabled: true},
	}),
	loginTest(),
	fragmentsPagesTest,
	pageEditorPagesTest,
	pageManagementSiteTest
);

test(
	'Show Marketplace Button and the related modal',
	{
		tag: ['@LPD-46101'],
	},
	async ({apiHelpers, fragmentsPage, page, site}) => {

		// Create a new user with admin role

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		const role =
			await apiHelpers.headlessAdminUser.getRoleByName('Administrator');

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role.externalReferenceCode,
			user.id
		);

		// Log in with the new user

		await performLogout(page);

		await performLogin(page, user.alternateName);

		// Go to fragment administration and look for the badge in the marketplace button

		await fragmentsPage.goto(site.friendlyUrlPath);

		await expect(
			page.locator('.marketplace-button--notification')
		).toBeVisible();

		// Click the marketplace button and wait for the modal

		await page.getByTitle('Open Marketplace Explorer').click();

		await expect(
			page
				.getByRole('dialog')
				.getByRole('heading', {name: 'Marketplace is now in'})
		).toBeVisible();

		// Close the modal and check that the badge in the marketplace button is hide

		await page.getByRole('button', {name: 'Cancel'}).click();

		await expect(
			page.locator('.marketplace-button--notification')
		).not.toBeVisible();
	}
);

test(
	'Show modal with fragments from marketplace in Fragment Administration',
	{
		tag: ['@LPD-48223'],
	},
	async ({apiHelpers, fragmentsPage, page, site}) => {

		// Create a new user with admin role

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		const role =
			await apiHelpers.headlessAdminUser.getRoleByName('Administrator');

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role.externalReferenceCode,
			user.id
		);

		// Log in with the new user

		await performLogout(page);

		await performLogin(page, user.alternateName);

		// Go to fragment administration and click the marketplace button

		await fragmentsPage.goto(site.friendlyUrlPath);

		await page.getByTitle('Open Marketplace Explorer').click();

		// Wait for the modal is shown click on the Explore Marketplace button

		await expect(
			page
				.getByRole('dialog')
				.getByRole('heading', {name: 'Marketplace is now in'})
		).toBeVisible();
	}
);

test(
	'Check available actions of marketplace fragment',
	{
		tag: '@LPD-43455',
	},
	async ({apiHelpers, fragmentsPage, page, site}) => {

		// Create new fragment collection

		const fragmentCollectionName = getRandomString();

		const {fragmentCollectionId} =
			await apiHelpers.jsonWebServicesFragmentCollection.addFragmentCollection(
				{
					groupId: site.id,
					name: fragmentCollectionName,
				}
			);

		const fragmentName = getRandomString();

		await apiHelpers.jsonWebServicesFragmentEntry.addFragmentEntry({
			fragmentCollectionId,
			groupId: site.id,
			html: `<div class="fragment-example">
				  Example marketplace fragment
				</div>`,
			marketplace: true,
			name: fragmentName,
			type: 'component',
		});

		// Go to fragment administration

		await fragmentsPage.goto(site.friendlyUrlPath);

		// Click the More Actions button to open the actions

		await page
			.locator('.card-row')
			.filter({hasText: fragmentName})
			.getByLabel('More actions')
			.click();

		// Check available actions

		['View Usages', 'Move', 'Delete'].forEach(async (action) => {
			await expect(
				page.getByRole('menuitem', {name: action})
			).toBeVisible();
		});

		[
			'Edit',
			'Change Thumbnail',
			'Mark as Cacheable',
			'Export',
			'Make a Copy',
			'Rename',
		].forEach(async (action) => {
			await expect(
				page.getByRole('menuitem', {name: action})
			).not.toBeVisible();
		});
	}
);

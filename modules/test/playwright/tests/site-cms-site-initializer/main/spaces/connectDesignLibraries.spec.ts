/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-57283': {enabled: true},
	}),
	loginTest()
);

test(
	'Can connect and disconnect a design library',
	{tag: '@LPD-110549'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const assetLibrary =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary();
		const designLibrary =
			await apiHelpers.headlessAssetLibrary.createDesignLibrary();
		const space = await apiHelpers.headlessAssetLibrary.createSpace({
			settings: {logoColor: 'outline-3'},
		});

		// Offer only design libraries

		await spaceSummaryPage.goto(space.name);

		await spaceSummaryPage.openConnectSitesDialog();

		await page.getByLabel('Type', {exact: true}).click();

		await page
			.getByRole('option', {exact: true, name: 'Design Libraries'})
			.click();

		await page
			.getByPlaceholder('Select a Design Library', {exact: true})
			.click();

		await expect(
			page.getByRole('option', {exact: true, name: designLibrary.name})
		).toBeVisible();
		await expect(
			page.getByRole('option', {exact: true, name: assetLibrary.name})
		).toHaveCount(0);

		// Connect the design library

		await page
			.getByRole('option', {exact: true, name: designLibrary.name})
			.click();
		await page
			.getByRole('dialog')
			.getByRole('button', {exact: true, name: 'Connect'})
			.click();

		await waitForAlert(
			page,
			`Success:Design library ${designLibrary.name} was successfully connected to the space.`,
			{autoClose: false}
		);

		const designLibraryRow = page
			.getByLabel('Connections')
			.getByRole('listitem')
			.filter({
				has: page.getByText(`${designLibrary.name} (Design Library)`, {
					exact: true,
				}),
			});

		await expect(designLibraryRow).toBeVisible();

		// Disconnect the design library

		await designLibraryRow.getByRole('button', {name: /Actions/i}).click();

		await page.getByRole('menuitem', {name: 'Disconnect'}).click();

		await waitForAlert(
			page,
			`Success:Design library ${designLibrary.name} was successfully disconnected from the space.`,
			{autoClose: false}
		);

		await expect(designLibraryRow).toHaveCount(0);
	}
);

/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {depotsPagesTest} from './fixtures/depotsPagesTest';

const test = mergeTests(
	dataApiHelpersTest,
	depotsPagesTest,
	featureFlagsTest({'LPD-57283': {enabled: true}}),
	loginTest()
);

test(
	'An asset library can be connected to a design library from its sites',
	{tag: '@LPD-110549'},
	async ({apiHelpers, depotAdminPage, page}) => {
		const assetLibrary =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary();
		const designLibrary =
			await apiHelpers.headlessAssetLibrary.createDesignLibrary();

		await depotAdminPage.gotoSites(assetLibrary.id);

		await page.getByRole('button', {exact: true, name: 'Add'}).click();

		const itemSelector = page.frameLocator('iframe[title="Select Site"]');

		await itemSelector
			.getByRole('link', {exact: true, name: 'Design Libraries'})
			.click();

		await itemSelector.getByText(designLibrary.name, {exact: true}).click();

		await expect(
			page.getByText(`${designLibrary.name} (Design Library)`, {
				exact: true,
			})
		).toBeVisible();
	}
);

test(
	'A connected design library is labeled as such in the asset library sites',
	{tag: '@LPD-110549'},
	async ({apiHelpers, depotAdminPage, page}) => {
		const assetLibrary =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary();
		const designLibrary =
			await apiHelpers.headlessAssetLibrary.createDesignLibrary();

		await apiHelpers.headlessAssetLibrary.connectSite(
			assetLibrary.externalReferenceCode,
			designLibrary.externalReferenceCode
		);

		await depotAdminPage.gotoSites(assetLibrary.id);

		await expect(
			page.getByText(`${designLibrary.name} (Design Library)`, {
				exact: true,
			})
		).toBeVisible();
	}
);

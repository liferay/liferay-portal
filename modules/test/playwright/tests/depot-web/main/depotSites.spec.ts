/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {depotsPagesTest} from './fixtures/depotsPagesTest';

const test = mergeTests(dataApiHelpersTest, depotsPagesTest, loginTest());

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

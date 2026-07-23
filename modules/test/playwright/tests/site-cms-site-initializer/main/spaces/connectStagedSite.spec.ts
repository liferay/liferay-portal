/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {enableLocalStaging} from '../../../../utils/staging';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest()
);

test(
	'Distinguishes live and staging rows in the connected sites list',
	{tag: ['@LPD-91062']},
	async ({apiHelpers, page, site, spaceSummaryPage}) => {
		const spaceName = getRandomString();

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {logoColor: 'outline-3'},
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			site.externalReferenceCode
		);

		await enableLocalStaging(apiHelpers, page, site);

		await spaceSummaryPage.goto(spaceName);

		const stagingWord = await page.evaluate(() =>
			Liferay.Language.get('staging')
		);

		const connectedSites = page.getByTestId(
			'space-summary-connected-sites'
		);

		const liveRow = connectedSites.getByRole('row').filter({
			has: page.getByText(site.name, {exact: true}),
		});
		const stagingRow = connectedSites.getByRole('row').filter({
			hasText: `${site.name} (${stagingWord})`,
		});

		await expect(async () => {
			await page.reload();

			await expect(liveRow).toBeVisible();
			await expect(stagingRow).toBeVisible();
		}).toPass();
	}
);

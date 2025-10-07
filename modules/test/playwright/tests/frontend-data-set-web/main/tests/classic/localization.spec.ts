/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {usersAndOrganizationsPagesTest} from '../../../../../fixtures/usersAndOrganizationsPagesTest';
import {liferayConfig} from '../../../../../liferay.config';
import {EFDSVisualizationMode, waitForFDS} from '../../../../../utils/waitFor';
import {fdsSamplePageTest} from '../../fixtures/fdsSamplePageTest';

const test = mergeTests(
	dataApiHelpersTest,
	fdsSamplePageTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	usersAndOrganizationsPagesTest
);

let fdsSamplePageLayout: Layout;

test.beforeEach(async ({fdsSamplePage, page, site}) => {
	const {layout} = await fdsSamplePage.setupFDSSampleWidget({site});

	fdsSamplePageLayout = layout;

	await fdsSamplePage.selectTab('Classic');

	await waitForFDS({page, visualizationMode: EFDSVisualizationMode.TABLE});
});

test(
	'Headers are localized',
	{tag: '@LPS-162792'},
	async ({fdsSamplePage, page, site}) => {
		await test.step('Check headers are localized in English', async () => {
			await expect(fdsSamplePage.table.headerCells).toHaveCount(4);
			await expect(fdsSamplePage.table.headerCells).toContainText([
				'First Name',
				'Last Name',
				'Email Address',
			]);
		});

		await test.step('Check headers are localized in Spanish', async () => {
			const url = `${liferayConfig.environment.baseUrl}/es/web${site.friendlyUrlPath}${fdsSamplePageLayout.friendlyUrlPath}`;

			await page.goto(url);

			await fdsSamplePage.selectTab('Classic');

			await expect(page.getByText('test@liferay.com')).toBeVisible();

			expect(
				await fdsSamplePage.table.headerCells.allInnerTexts()
			).toEqual([
				'Nombre',
				'Apellido',
				'Dirección de correo',
				'Acciones del elemento',
			]);
		});
	}
);

/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../fixtures/isolatedSiteTest';
import {loginTest} from '../../fixtures/loginTest';
import {fdsSamplePageTest} from './fixtures/fdsSamplePageTest';

const test = mergeTests(
	fdsSamplePageTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

let fdsSamplePageURL: string;

test.beforeEach(async ({fdsSamplePage, page, site}) => {
    const locale = 'es';

	const {url} = await fdsSamplePage.setupFDSSampleWidget({site, locale});

	fdsSamplePageURL = url;

	await fdsSamplePage.selectTab('Classic');

	await expect(
		page.getByText('test@liferay.com')
	).toBeVisible();
});

test(
	'Assert the details shown in the FDS table',
	{tag: '@LPS-162792'},
	async ({fdsSamplePage}) => {

		await test.step('Check headers are localized in Spanish', async () => {
            expect(
				await fdsSamplePage.table.headerCells
					.allInnerTexts()
			).toEqual(['Nombre', 'Apellido', 'Dirección de correo', '']);
		});

	}
);
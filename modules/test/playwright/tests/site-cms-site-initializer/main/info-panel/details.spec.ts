/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
	}),
	loginTest()
);

test(
	'Info panel shows title with content structure',
	{tag: ['@LPD-69788', '@LPD-76513']},
	async ({apiHelpers, assetsPage, infoPanelPage, page}) => {
		const title = `Content ${getRandomString()}`;

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title,
			},
			'cms/basic-web-contents',
			'Default'
		);

		await assetsPage.gotoAll();

		await test.step('Open Info Panel and assert that title is not empty', async () => {
			await assetsPage.execItemAction({
				action: 'Show Details',
				filter: title,
			});

			await expect(
				page.getByRole('heading', {name: title})
			).toBeVisible();
		});

		await test.step('Assert that all tabs are visible', async () => {
			await expect(infoPanelPage.selectTab('Performance')).toBeVisible();

			await expect(infoPanelPage.selectTab('Comments')).toBeVisible();

			await infoPanelPage.selectTab('Categorization').click();

			await expect(page.getByPlaceholder('Add tag')).toBeVisible();
			await expect(page.getByPlaceholder('Add category')).toBeVisible();
		});
	}
);

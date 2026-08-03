/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {cmsPagesTest} from '../../../site-cms-site-initializer/main/fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
	}),
	loginTest()
);

test(
	'Setting a friendly URL already used by another entry in the same Space is blocked',
	{tag: ['@LPD-95548', '@LPD-95548/TC-23.f', '@LPD-100262']},
	async ({apiHelpers, assetsPage, contentsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const existingTitle = getRandomString();
		const friendlyUrl = getRandomString();
		const newTitle = getRandomString();

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: `Space ${getRandomString()}`,
			type: 'Space',
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				friendlyUrlPath: friendlyUrl,
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: existingTitle,
			},
			applicationName,
			space.name
		);

		await test.step('Create a new content and set the same friendly URL', async () => {
			await assetsPage.gotoContents(space.name);

			await contentsPage.createContent('Basic Web Content', space.name);

			await page.getByLabel('Title').fill(newTitle);
			await page.getByLabel('Friendly URL').fill(friendlyUrl);

			await contentsPage.publishButton.click();
		});

		await test.step('An error indicates the URL conflict and the entry is not saved', async () => {
			test.fail(
				true,
				'LPD-100262: the conflicting friendly URL is silently renamed instead of reported.'
			);

			await expect(
				page.getByText(
					'The friendly URL is already in use. Please enter a unique friendly URL.'
				)
			).toBeVisible();

			await expect(
				page.getByRole('heading', {name: 'New'})
			).toBeVisible();
		});
	}
);

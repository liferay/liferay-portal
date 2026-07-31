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
import {structureBuilderPagesTest} from '../../../site-cms-site-initializer/structure-builder/fixtures/structureBuilderPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
	}),
	loginTest(),
	structureBuilderPagesTest
);

test(
	'A structure with no workflow assigned offers no submit for review action',
	{tag: ['@LPD-95548', '@LPD-95548/TC-23.k']},
	async ({
		apiHelpers,
		assetsPage,
		contentsPage,
		page,
		structureBuilderPage,
	}) => {
		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: `Space ${getRandomString()}`,
			type: 'Space',
		});

		const structureLabel = `Structure${getRandomString()}`;

		await structureBuilderPage.createStructureFromData({
			label: structureLabel,
			page: structureBuilderPage,
			spaces: [space.name],
		});

		await test.step('Create a content entry using the structure', async () => {
			await assetsPage.gotoContents(space.name);

			await contentsPage.createContent(structureLabel, space.name);

			await page
				.getByRole('textbox', {exact: true, name: 'Title'})
				.fill(getRandomString());
		});

		await test.step('The editor offers Publish rather than a workflow submission', async () => {
			await expect(
				page.getByText('Publish', {exact: true})
			).toBeVisible();

			await expect(
				page.getByText('Submit for Workflow', {exact: true})
			).toBeHidden();
		});

		await test.step('The publish options hold no submit for review action', async () => {
			await page.getByTitle('Publish Options').click();

			await expect(
				page.getByRole('menuitem', {name: 'Schedule Publication'})
			).toBeVisible();

			await expect(
				page.getByRole('menuitem', {name: /Submit|Review/})
			).toBeHidden();
		});
	}
);

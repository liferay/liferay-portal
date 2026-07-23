/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {cmsPagesTest} from '../../site-cms-site-initializer/main/fixtures/cmsPagesTest';
import {cmpPagesTest} from './fixtures/cmpPagesTest';

const test = mergeTests(
	cmpPagesTest,
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-58677': {enabled: true},
	}),
	loginTest()
);

const CMP_PROJECT = 'cmp/projects';
const CMP_TASK = 'cmp/tasks';

test(
	'Info panel opens without crashing when showing details for a related asset',
	{tag: ['@LPD-97663']},
	async ({apiHelpers, page, projectPage, projectsPage}) => {
		const assetTitle = `Asset ${getRandomString()}`;
		const projectTitle = `Project ${getRandomString()}`;
		const taskTag = 'L_CMP_TASK_' + Math.floor(Math.random() * 100000000);

		let project;

		await test.step('Create a project and a task', async () => {
			project = await apiHelpers.objectEntry.postObjectEntry(
				{
					title: projectTitle,
				},
				CMP_PROJECT
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					keywords: [taskTag],
					r_cmpProjectToCMPTasks_c_cmpProjectId: project.id,
					title: getRandomString(),
				},
				CMP_TASK,
				project.scopeKey
			);
		});

		await test.step('Associate an asset to the task', async () => {
			const space =
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name: getRandomString(),
					settings: {trashEnabled: true},
					type: 'Space',
				});

			await apiHelpers.objectEntry.postObjectEntry(
				{
					keywords: [taskTag],
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: assetTitle,
				},
				'cms/basic-web-contents',
				space.name
			);
		});

		await test.step('Open the project Assets tab', async () => {
			await projectsPage.goto();

			await projectsPage.getProject(projectTitle).click();

			await projectPage.assetsTab.click();
		});

		await test.step('Click the Asset details option and assert it renders', async () => {
			await page.getByRole('button', {name: assetTitle}).click();

			await page.getByRole('menuitem', {name: 'Show Details'}).click();

			await expect(
				page.getByRole('heading', {name: assetTitle})
			).toBeVisible();
		});
	}
);

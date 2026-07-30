/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {cmsPagesTest} from '../../site-cms-site-initializer/main/fixtures/cmsPagesTest';
import {cmpPagesTest} from './fixtures/cmpPagesTest';

const test = mergeTests(
	cmpPagesTest,
	cmsPagesTest,
	dataApiHelpersTest,
	loginTest()
);

const CMP_PROJECT = 'cmp/projects';
const CMP_TASK = 'cmp/tasks';
const CMP_TASK_LINK = 'cmp/task-links';
const CMS_BASIC_WEB_CONTENT = 'cms/basic-web-contents';

test(
	'Info panel opens without crashing when showing details for a related asset',
	{tag: ['@LPD-97663']},
	async ({apiHelpers, page, projectPage, projectsPage}) => {
		const assetTitle = `Asset ${getRandomString()}`;
		const projectTitle = `Project ${getRandomString()}`;

		let project;
		let task;

		await test.step('Create a project and a task', async () => {
			project = await apiHelpers.objectEntry.postObjectEntry(
				{
					title: projectTitle,
				},
				CMP_PROJECT
			);

			task = await apiHelpers.objectEntry.postObjectEntry(
				{
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

			const asset = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: assetTitle,
				},
				CMS_BASIC_WEB_CONTENT,
				space.name
			);

			const objectDefinition =
				await apiHelpers.objectAdmin.getObjectDefinitionByName(
					'CMSBasicWebContent'
				);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					classExternalReferenceCode: asset.externalReferenceCode,
					className: objectDefinition.className,
					groupExternalReferenceCode:
						asset.systemProperties.scope.externalReferenceCode,
					r_cmpTaskToCMPTaskLinks_c_cmpTaskId: task.id,
				},
				CMP_TASK_LINK,
				project.scopeKey
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

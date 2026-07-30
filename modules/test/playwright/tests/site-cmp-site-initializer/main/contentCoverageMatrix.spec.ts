/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {getTaxonomyCategoryId} from '../../../utils/getTaxonomyCategoryId';
import {cmsPagesTest} from '../../site-cms-site-initializer/main/fixtures/cmsPagesTest';
import {DataSetPage} from '../../site-cms-site-initializer/main/pages/DataSetPage';
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
	'Filters the related assets table with AND and highlights the cell when a matrix cell is clicked',
	{tag: ['@LPD-93351']},
	async ({apiHelpers, page, projectPage, projectsPage}) => {
		const dataSetPage = new DataSetPage(page);

		const projectTitle = getRandomString();

		const championAwarenessAsset = getRandomString();

		const championConsiderationAsset = getRandomString();

		const decisionMakerAwarenessAsset = getRandomString();

		let project;
		let task;

		try {
			const {awarenessId, championId, considerationId, decisionMakerId} =
				await test.step('Resolve the persona and funnel-stage category ids', async () => {
					const siteId = await apiHelpers.headlessAdminUser
						.getSiteByFriendlyUrlPath('cms')
						.then((response) => response.id);

					return {
						awarenessId: await getTaxonomyCategoryId(
							apiHelpers,
							siteId,
							'Funnel Stage',
							'Awareness'
						),
						championId: await getTaxonomyCategoryId(
							apiHelpers,
							siteId,
							'Personas',
							'Champion'
						),
						considerationId: await getTaxonomyCategoryId(
							apiHelpers,
							siteId,
							'Funnel Stage',
							'Consideration'
						),
						decisionMakerId: await getTaxonomyCategoryId(
							apiHelpers,
							siteId,
							'Personas',
							'Decision Maker'
						),
					};
				});

			await test.step('Create a project categorized with the matrix axes', async () => {
				project = await apiHelpers.objectEntry.postObjectEntry(
					{
						taxonomyCategoryIds: [
							championId,
							decisionMakerId,
							awarenessId,
							considerationId,
						],
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

			await test.step('Create related assets and link them to the task', async () => {
				const space =
					await apiHelpers.headlessAssetLibrary.createAssetLibrary({
						name: getRandomString(),
						settings: {trashEnabled: true},
						type: 'Space',
					});

				const objectDefinition =
					await apiHelpers.objectAdmin.getObjectDefinitionByName(
						'CMSBasicWebContent'
					);

				for (const {taxonomyCategoryIds, title} of [
					{
						taxonomyCategoryIds: [championId, awarenessId],
						title: championAwarenessAsset,
					},
					{
						taxonomyCategoryIds: [championId, considerationId],
						title: championConsiderationAsset,
					},
					{
						taxonomyCategoryIds: [decisionMakerId, awarenessId],
						title: decisionMakerAwarenessAsset,
					},
				]) {
					const asset = await apiHelpers.objectEntry.postObjectEntry(
						{
							objectEntryFolderExternalReferenceCode:
								'L_CONTENTS',
							taxonomyCategoryIds,
							title,
						},
						CMS_BASIC_WEB_CONTENT,
						space.name
					);

					await apiHelpers.objectEntry.postObjectEntry(
						{
							classExternalReferenceCode:
								asset.externalReferenceCode,
							className: objectDefinition.className,
							groupExternalReferenceCode:
								asset.systemProperties.scope
									.externalReferenceCode,
							r_cmpTaskToCMPTaskLinks_c_cmpTaskId: task.id,
						},
						CMP_TASK_LINK,
						project.scopeKey
					);
				}
			});

			await test.step('Open the project Assets tab', async () => {
				await projectsPage.goto();

				await projectsPage.getProject(projectTitle).click();

				await projectPage.assetsTab.click();

				await expect(
					projectPage.getMatrixCell('Champion', 'Awareness', 1)
				).toBeVisible();
			});

			await test.step('All related assets are listed initially', async () => {
				await expect(
					dataSetPage.assetLink(championAwarenessAsset)
				).toBeVisible();
				await expect(
					dataSetPage.assetLink(championConsiderationAsset)
				).toBeVisible();
				await expect(
					dataSetPage.assetLink(decisionMakerAwarenessAsset)
				).toBeVisible();
			});

			await test.step('Clicking a cell filters the table with AND and highlights the cell', async () => {
				await projectPage.filterByMatrixCell(
					'Champion',
					'Awareness',
					1
				);

				await expect(
					projectPage.getMatrixCell('Champion', 'Awareness', 1)
				).toHaveClass(/lfr-cmp__content-gap-cell--selected/);

				await expect(
					dataSetPage.assetLink(championAwarenessAsset)
				).toBeVisible();
				await expect(
					dataSetPage.assetLink(championConsiderationAsset)
				).toBeHidden();
				await expect(
					dataSetPage.assetLink(decisionMakerAwarenessAsset)
				).toBeHidden();
			});

			await test.step('Clearing the filter restores all assets and removes the highlight', async () => {
				await page.getByRole('button', {name: 'Clear'}).click();

				await expect(
					projectPage.getMatrixCell('Champion', 'Awareness', 1)
				).not.toHaveClass(/lfr-cmp__content-gap-cell--selected/);

				await expect(
					dataSetPage.assetLink(championAwarenessAsset)
				).toBeVisible();
				await expect(
					dataSetPage.assetLink(championConsiderationAsset)
				).toBeVisible();
				await expect(
					dataSetPage.assetLink(decisionMakerAwarenessAsset)
				).toBeVisible();
			});

			await test.step('Clicking a cell with no matching assets yields an empty table', async () => {
				await projectPage.filterByMatrixCell(
					'Decision Maker',
					'Consideration',
					0
				);

				await expect(
					dataSetPage.assetLink(championAwarenessAsset)
				).toBeHidden();
				await expect(
					dataSetPage.assetLink(championConsiderationAsset)
				).toBeHidden();
				await expect(
					dataSetPage.assetLink(decisionMakerAwarenessAsset)
				).toBeHidden();

				await page
					.getByRole('button', {exact: true, name: 'Clear'})
					.click();
			});
		}
		finally {
			if (project) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					CMP_PROJECT,
					String(project.id)
				);
			}
		}
	}
);

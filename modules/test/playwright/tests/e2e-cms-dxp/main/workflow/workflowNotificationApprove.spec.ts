/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {workflowPagesTest} from '../../../../fixtures/workflowPagesTest';
import {
	assignWorkflowToStructure,
	unassignWorkflowFromStructure,
} from '../../../../utils/cmsWorkflow';
import getRandomString from '../../../../utils/getRandomString';
import {NotificationsPage} from '../../../notifications-web/main/pages/NotificationsPage';

const test = mergeTests(dataApiHelpersTest, loginTest(), workflowPagesTest);

const APPLICATION_NAME = 'cms/basic-web-contents';

test(
	'A reviewer can reach a pending task from its workflow notification and approve the content',
	{tag: ['@LPD-95526', '@LPD-95526/TC-3.e']},
	async ({apiHelpers, configurationTabPage, page, workflowTasksPage}) => {
		test.setTimeout(300000);

		const notificationsPage = new NotificationsPage(page);

		const spaceName = `Space ${getRandomString()}`;
		const contentTitle = `Title ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		await assignWorkflowToStructure(
			configurationTabPage,
			'Single Approver',
			'Basic Web Content'
		);

		try {
			const entry = await apiHelpers.objectEntry.postObjectEntry(
				{
					content: `<p>${getRandomString()}</p>`,
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: contentTitle,
				},
				APPLICATION_NAME,
				spaceName
			);

			await test.step('The approval notification is visible and links to the task', async () => {
				await expect(async () => {
					await notificationsPage.goto();

					await expect(
						notificationsPage.workflowReviewMessage(
							'Basic Web Content'
						)
					).toBeVisible({timeout: 5000});
				}).toPass({timeout: 120000});

				await notificationsPage
					.workflowReviewMessage('Basic Web Content')
					.click();
			});

			await test.step('The reviewer approves the content', async () => {
				await workflowTasksPage.goToAssignedToMyRoles();
				await workflowTasksPage.assignToMe(contentTitle);
				await workflowTasksPage.approve(contentTitle);

				const updatedEntry =
					await apiHelpers.objectEntry.getObjectEntryById(
						APPLICATION_NAME,
						String(entry.id)
					);

				expect(JSON.stringify(updatedEntry.status ?? '')).toMatch(
					/approved|"code":0/i
				);
			});
		}
		finally {
			await unassignWorkflowFromStructure(
				configurationTabPage,
				'Basic Web Content'
			);
		}
	}
);

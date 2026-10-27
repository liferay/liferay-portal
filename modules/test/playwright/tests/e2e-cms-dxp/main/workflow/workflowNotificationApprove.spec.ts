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
import {structureBuilderPagesTest} from '../../../site-cms-site-initializer/structure-builder/fixtures/structureBuilderPagesTest';

const test = mergeTests(
	dataApiHelpersTest,
	loginTest(),
	structureBuilderPagesTest,
	workflowPagesTest
);

test(
	'A reviewer can reach a pending task from its workflow notification and approve the content',
	{tag: ['@LPD-95526', '@LPD-95526/TC-3.e']},
	async ({
		apiHelpers,
		configurationTabPage,
		page,
		structureBuilderPage,
		workflowTasksPage,
	}) => {
		test.setTimeout(300000);

		const notificationsPage = new NotificationsPage(page);

		const contentTitle = `Title ${getRandomString()}`;
		const structureLabel = `Structure ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createSpace();

		const objectDefinitionId =
			await structureBuilderPage.createStructureFromData({
				label: structureLabel,
				page: structureBuilderPage,
				spaces: [space.name],
			});

		const objectDefinition = await apiHelpers.get(
			`${apiHelpers.baseUrl}object-admin/v1.0/object-definitions/${objectDefinitionId}`
		);

		const applicationName = objectDefinition.restContextPath.replace(
			'/o/',
			''
		);

		await assignWorkflowToStructure(
			configurationTabPage,
			'Single Approver',
			structureLabel
		);

		try {
			const entry = await apiHelpers.objectEntry.postObjectEntry(
				{
					[objectDefinition.titleObjectFieldName]: contentTitle,
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				},
				applicationName,
				space.name
			);

			await test.step('The approval notification is visible and links to the task', async () => {
				await expect(async () => {
					await notificationsPage.goto();

					await expect(
						notificationsPage.workflowReviewMessage(structureLabel)
					).toBeVisible({timeout: 5000});
				}).toPass({timeout: 120000});

				await notificationsPage
					.workflowReviewMessage(structureLabel)
					.click();
			});

			await test.step('The reviewer approves the content', async () => {
				await workflowTasksPage.goToAssignedToMyRoles();
				await workflowTasksPage.assignToMe(contentTitle);
				await workflowTasksPage.approve(contentTitle);

				const updatedEntry =
					await apiHelpers.objectEntry.getObjectEntryById(
						applicationName,
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
				structureLabel
			);
		}
	}
);

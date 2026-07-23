/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {workflowPagesTest} from '../../../../fixtures/workflowPagesTest';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {
	assignWorkflowToStructure,
	unassignWorkflowFromStructure,
} from '../../../../utils/cmsWorkflow';
import {getRandomInt} from '../../../../utils/getRandomInt';
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
	'A reviewer can reach a pending custom-structure task from its notification and reject it with a comment, leaving it not approved',
	{tag: ['@LPD-95526', '@LPD-95526/TC-3.f']},
	async ({
		apiHelpers,
		configurationTabPage,
		page,
		structureBuilderPage,
		workflowTaskDetailsPage,
		workflowTasksPage,
	}) => {
		test.setTimeout(600000);

		const notificationsPage = new NotificationsPage(page);

		const spaceName = `Space ${getRandomString()}`;
		const structureLabel = `Event ${getRandomString()}`;
		const structureName = `Event${getRandomInt()}`;
		const titleValue = `Title ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		const objectDefinitionId =
			await test.step('Build a custom Event structure (localizable Body text)', async () => {
				const id = await structureBuilderPage.createStructureFromData({
					label: structureLabel,
					name: structureName,
					page: structureBuilderPage,
					publish: false,
					spaces: [spaceName],
				});

				await structureBuilderPage.addField('Text');
				await structureBuilderPage.selectFields([{label: 'Text'}]);
				await structureBuilderPage.changeFieldSettings({
					label: 'Body',
					localizable: true,
				});

				await structureBuilderPage.publishStructure();

				return id;
			});

		const objectDefinition = await apiHelpers.get(
			`${apiHelpers.baseUrl}object-admin/v1.0/object-definitions/${objectDefinitionId}`
		);

		const applicationName = objectDefinition.restContextPath.replace(
			'/o/',
			''
		);

		const bodyField = objectDefinition.objectFields.find(
			(objectField) =>
				objectField.label && objectField.label.en_US === 'Body'
		);

		if (!bodyField) {
			throw new Error('Body field not found in object definition');
		}

		await assignWorkflowToStructure(
			configurationTabPage,
			'Single Approver',
			objectDefinition.label.en_US
		);

		try {
			const entry = await apiHelpers.objectEntry.postObjectEntry(
				{
					[bodyField.name]: `Body ${getRandomString()}`,
					[objectDefinition.titleObjectFieldName]: titleValue,
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				},
				applicationName,
				spaceName
			);

			await test.step('The review notification is visible and links to the task', async () => {
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

			await test.step('The reviewer rejects the task with an inline comment', async () => {
				await expect(async () => {
					await workflowTasksPage.goToAssignedToMyRoles();

					await expect(
						page.getByRole('row').filter({hasText: titleValue})
					).toHaveCount(1, {timeout: 5000});
				}).toPass({timeout: 120000});

				await workflowTasksPage.assignToMe(titleValue);

				await workflowTaskDetailsPage.goTo(titleValue);

				await clickAndExpectToBeVisible({
					autoClick: true,
					target: workflowTaskDetailsPage.rejectMenuItem,
					timeout: 2000,
					trigger: workflowTaskDetailsPage.reviewActionMenu,
				});

				await workflowTaskDetailsPage.reviewComment.fill(
					`Please revise ${getRandomString()}`,
					{timeout: 10000}
				);

				await workflowTaskDetailsPage.clickDoneButton();
			});

			await test.step('The content is not approved', async () => {
				const updatedEntry =
					await apiHelpers.objectEntry.getObjectEntryById(
						applicationName,
						String(entry.id)
					);

				expect(JSON.stringify(updatedEntry.status ?? '')).not.toMatch(
					/approved|"code":0/i
				);
			});
		}
		finally {
			await unassignWorkflowFromStructure(
				configurationTabPage,
				objectDefinition.label.en_US
			);
		}
	}
);

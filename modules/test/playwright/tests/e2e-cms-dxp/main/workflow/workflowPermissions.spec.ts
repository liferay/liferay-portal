/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {workflowPagesTest} from '../../../../fixtures/workflowPagesTest';
import {addSpaceUser} from '../../../../utils/addSpaceUser';
import {
	assignWorkflowToStructure,
	unassignWorkflowFromStructure,
} from '../../../../utils/cmsWorkflow';
import getRandomString from '../../../../utils/getRandomString';
import {performLoginViaApi} from '../../../../utils/performLogin';

const test = mergeTests(dataApiHelpersTest, loginTest(), workflowPagesTest);

const APPLICATION_NAME = 'cms/basic-web-contents';

test(
	'A Space Member cannot assign or act on a pending workflow task',
	{tag: ['@LPD-95526', '@LPD-95526/TC-3.g']},
	async ({
		apiHelpers,
		browser,
		configurationTabPage,
		page,
		workflowTasksPage,
	}) => {
		test.setTimeout(600000);

		const spaceName = `Space ${getRandomString()}`;
		const contentTitle = `Title ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		const spaceMember = await addSpaceUser(
			apiHelpers,
			space.externalReferenceCode,
			'Asset Library Member'
		);

		await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(spaceMember.id);
		await apiHelpers.jsonWebServicesUser.answerReminderQuery(
			spaceMember.id
		);

		await assignWorkflowToStructure(
			configurationTabPage,
			'Single Approver',
			'Basic Web Content'
		);

		try {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					content: `<p>${getRandomString()}</p>`,
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: contentTitle,
				},
				APPLICATION_NAME,
				spaceName
			);

			await test.step('The reviewer (Administrator) sees the pending task', async () => {
				await expect(async () => {
					await workflowTasksPage.goToAssignedToMyRoles();

					await expect(
						page.getByRole('row').filter({hasText: contentTitle})
					).toHaveCount(1, {timeout: 5000});
				}).toPass({timeout: 120000});
			});

			await test.step('The Space Member cannot see or act on the task', async () => {
				const memberContext = await browser.newContext();

				const memberPage = await memberContext.newPage();

				try {
					await performLoginViaApi({
						page: memberPage,
						screenName: spaceMember.alternateName,
					});

					const myWorkflowTasksURL =
						'/group/guest/~/control_panel/manage?p_p_id=com_liferay_portal_workflow_task_web_portlet_MyWorkflowTaskPortlet';

					for (const tab of [
						'assigned-to-my-roles',
						'assigned-to-me',
					]) {
						await memberPage.goto(
							`${myWorkflowTasksURL}&_com_liferay_portal_workflow_task_web_portlet_MyWorkflowTaskPortlet_tabs1=${tab}`
						);

						await expect(
							memberPage
								.getByRole('row')
								.filter({hasText: contentTitle})
						).toHaveCount(0, {timeout: 15000});
					}
				}
				finally {
					await memberContext.close();
				}
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

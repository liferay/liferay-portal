/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import getRandomString from '../../../utils/getRandomString';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	loginTest(),
	workflowPagesTest
);

test(
	'Shows a pending workflow task with its asset title, type and status',
	{tag: '@LPD-112143'},
	async ({
		apiHelpers,
		configurationTabPage,
		page,
		processBuilderPage,
		viewTasksPage,
	}) => {
		await processBuilderPage.goto('/test');
		await configurationTabPage.configurationTabLink.waitFor({
			state: 'visible',
		});
		await configurationTabPage.configurationTabLink.click({force: true});
		await configurationTabPage.page.waitForURL((url) =>
			url.href.includes('=configuration')
		);

		await configurationTabPage.assignWorkflowToAssetType(
			'Single Approver',
			'Basic Web Content'
		);

		try {
			const contentName = getRandomString();

			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: contentName,
				},
				'cms/basic-web-contents',
				'Default'
			);

			await viewTasksPage.goto();

			const taskRow = page.getByRole('row').filter({hasText: contentName});

			await expect(taskRow).toBeVisible();
			await expect(taskRow.getByText('Basic Web Content')).toBeVisible();
			await expect(taskRow.getByText('Pending')).toBeVisible();

			await test.step('Can view the workflow task from its asset title link', async () => {
				await taskRow.getByRole('link', {name: contentName}).click();

				await expect(page).toHaveURL(/edit_workflow_task\.jsp/);
				await expect(page).toHaveURL(/workflowTaskId=\d+/);
			});
		}
		finally {

			// Teardown for created web content workflow

			await processBuilderPage.goto('/test');

			await configurationTabPage.configurationTabLink.waitFor({
				state: 'visible',
			});

			await configurationTabPage.configurationTabLink.click({
				force: true,
			});

			await configurationTabPage.page.waitForURL((url) =>
				url.href.includes('=configuration')
			);

			await configurationTabPage.unassignWorkflowFromAssetType(
				'Basic Web Content'
			);
		}
	}
);

/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';

export const test = mergeTests(
	featureFlagsTest({
		'LPD-34594': {enabled: true},
	}),
	loginTest(),
	objectPagesTest,
	workflowPagesTest
);

let hasWorkflowAssigned = false;

test.afterEach(async ({processBuilderPage}) => {
	if (hasWorkflowAssigned) {
		await processBuilderPage.goto();

		await processBuilderPage.configurationTab.click();

		await processBuilderPage.updateAccountWorkflow('Single Approver', '');

		hasWorkflowAssigned = false;
	}
});

test.describe('Object definition assigned workflow section', () => {
	test('can be seen and updated when an object is already published', async ({
		editObjectDetailsPage,
		page,
	}) => {
		await editObjectDetailsPage.goto('Account');

		const workflowAssignedInput = page.getByPlaceholder('No Workflow');

		await expect(workflowAssignedInput).toHaveValue('No Workflow');

		const newTabPagePromise = new Promise<Page>((resolve) =>
			editObjectDetailsPage.page.once('popup', resolve)
		);

		await page
			.getByRole('button', {name: 'Process Builder Configurations'})
			.click();

		const newTabPage = await newTabPagePromise;

		await newTabPage.waitForLoadState('domcontentloaded');

		await newTabPage
			.getByRole('row', {name: 'Account No Workflow Edit'})
			.getByRole('button')
			.click();

		await newTabPage
			.locator(
				'[id="_com_liferay_portal_workflow_web_portlet_ControlPanelWorkflowPortlet_workflowDefinitionName-com-liferay-account-model-AccountEntry"]'
			)
			.selectOption('Single Approver@1');

		await newTabPage.getByRole('button', {name: 'Save'}).click();

		await expect(
			newTabPage.getByRole('cell', {name: 'Single Approver'})
		).toBeVisible();

		hasWorkflowAssigned = true;

		await newTabPage.close();

		await editObjectDetailsPage.goto('Account');

		await editObjectDetailsPage.page
			.getByRole('button', {name: 'Refresh'})
			.click();

		await expect(workflowAssignedInput).toHaveValue('Single Approver');
	});
});

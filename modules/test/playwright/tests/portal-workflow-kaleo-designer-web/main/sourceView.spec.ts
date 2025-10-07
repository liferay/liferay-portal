/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import postSingleApproverCopy from './utils/postSingleApproverCopy';

export const test = mergeTests(apiHelpersTest, loginTest(), workflowPagesTest);

let workflowDefinitionIds: number[] = [];

let workflowDefinitionName: string;

test.beforeEach(async ({apiHelpers}) => {
	const workFlowDefinition = await postSingleApproverCopy(apiHelpers);

	workflowDefinitionIds.push(workFlowDefinition.id);
	workflowDefinitionName = workFlowDefinition.name;
});

test.afterEach(async ({apiHelpers}) => {
	for (const workflowDefinitionId of workflowDefinitionIds) {
		await apiHelpers.headlessAdminWorkflow.deleteWorkflowDefinition(
			workflowDefinitionId
		);
	}

	workflowDefinitionIds = [];
});

test(
	'Changing the definition name in source view should keep the definition content when switching to diagram view',
	{tag: '@LPD-59515'},
	async ({diagramViewPage, page, processBuilderPage, sourceViewPage}) => {
		await test.step('Navigate to the workflow definition and edit its name in source view', async () => {
			await processBuilderPage.goto();

			await processBuilderPage.clickWorkflowDefinitionName(
				workflowDefinitionName
			);

			await diagramViewPage.clickSourceViewButton();

			const nameLine = page.getByText(workflowDefinitionName);

			await nameLine.click({clickCount: 3});

			await nameLine.pressSequentially(
				`	<name>New ${workflowDefinitionName}</name>`
			);

			await nameLine.press('Enter');
		});

		await test.step('Assert that expected nodes and transitions are present in diagram view', async () => {
			await sourceViewPage.clickDiagramViewButton();

			expect(page.getByText('created')).toBeVisible();

			expect(page.getByText('approved')).toBeVisible();

			expect(page.getByText('review', {exact: true})).toBeVisible();

			expect(page.getByText('update')).toBeVisible();

			expect(page.getByText('REVIEW', {exact: true})).toBeVisible();

			expect(page.getByText('APPROVE', {exact: true})).toBeVisible();

			expect(page.getByText('RESUBMIT', {exact: true})).toBeVisible();

			expect(page.getByText('REJECT', {exact: true})).toBeVisible();
		});
	}
);

/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import {getWorkflowDefinition} from './utils/getWorkflowDefinition';

export const test = mergeTests(apiHelpersTest, loginTest(), workflowPagesTest);

let workflowDefinitionIds: number[] = [];

test.afterEach(async ({apiHelpers}) => {
	for (const workflowDefinitionId of workflowDefinitionIds) {
		await apiHelpers.headlessAdminWorkflow.deleteWorkflowDefinition(
			workflowDefinitionId
		);
	}

	workflowDefinitionIds = [];
});

test.describe('Transition', () => {
	test('name cannot be changed if another transition with the same name and source node already exists', async ({
		apiHelpers,
		diagramViewPage,
		page,
		processBuilderPage,
		transitionInfoPage,
	}) => {
		await test.step('Post same-source-transitions workflow definition and navitage to it', async () => {
			const workflowDefinitionName =
				'Workflow Definition' + getRandomInt();

			const workflowDefinition =
				await apiHelpers.headlessAdminWorkflow.postWorkflowDefinitionSave(
					workflowDefinitionName,
					getWorkflowDefinition('same-source-transitions')
				);

			workflowDefinitionIds.push(workflowDefinition.id);

			await processBuilderPage.goto();

			await processBuilderPage.clickWorkflowDefinitionName(
				workflowDefinitionName
			);
		});

		await test.step('Assert that transitions with different source nodes can have the same name', async () => {
			await diagramViewPage.clickTransition(
				'TRANSITION FROM CHILD TASK 1 TO END'
			);

			await transitionInfoPage.transitionNameField.fill('Same Name');

			await diagramViewPage.clickTransition(
				'TRANSITION FROM CHILD TASK 2 TO END'
			);

			await transitionInfoPage.transitionNameField.fill('Same Name');

			expect(
				page.getByText(
					'A transition with that name already exists. Enter a unique name.'
				)
			).not.toBeVisible();
		});

		await test.step('Assert that transitions with the same source node cannot have the same name', async () => {
			await diagramViewPage.clickTransition(
				'TRANSITION FROM PARENT TASK TO CHILD TASK 2'
			);

			await transitionInfoPage.transitionNameField.fill(
				'Different Name 1'
			);

			expect(
				page.getByText(
					'A transition with that name already exists. Enter a unique name.'
				)
			).toBeVisible();
		});

		await test.step('Assert that a repeated name is reverted to a previously valid name after navigating back', async () => {
			await diagramViewPage.clickTransition(
				'TRANSITION FROM PARENT TASK TO CHILD TASK 1'
			);

			await diagramViewPage.clickTransition(
				'TRANSITION FROM PARENT TASK TO CHILD TASK 2'
			);

			expect(page.getByLabel('Transition Name*')).toHaveValue(
				'Different Name 2'
			);
		});
	});
});

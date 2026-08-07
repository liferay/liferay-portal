/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import {waitForAlert} from '../../../utils/waitForAlert';
import {clientExtensionsPageTest} from '../../client-extension-web/fixtures/clientExtensionsPageTest';
import {getWorkflowDefinition} from './utils/getWorkflowDefinition';
import postSingleApproverCopy from './utils/postSingleApproverCopy';

export const test = mergeTests(
	apiHelpersTest,
	loginTest(),
	workflowPagesTest,
	clientExtensionsPageTest
);

let workflowDefinitionId: number;
let workflowDefinitionName: string;

test.beforeEach(async ({apiHelpers, scriptManagementPage}) => {
	const workFlowDefinition = await postSingleApproverCopy(apiHelpers);

	workflowDefinitionId = workFlowDefinition.id;
	workflowDefinitionName = workFlowDefinition.name;

	await scriptManagementPage.enableScriptManagementConfiguration();
});

test.afterEach(async ({apiHelpers, scriptManagementPage}) => {
	await apiHelpers.headlessAdminWorkflow.deleteWorkflowDefinition(
		workflowDefinitionId
	);

	await scriptManagementPage.enableScriptManagementConfiguration();
});

test('can see groovy and java options in the action type select when script management configuration is enabled', async ({
	actionPage,
	diagramViewPage,
	nodePropertiesSidebarPage,
	processBuilderPage,
}) => {
	await processBuilderPage.goto();

	await processBuilderPage.clickWorkflowDefinitionName(
		workflowDefinitionName
	);

	await diagramViewPage.clickNode('review');

	await nodePropertiesSidebarPage.addActionButton.click();

	expect(await actionPage.getTypeSelectOption('java')).not.toBeNull();
	expect(await actionPage.getTypeSelectOption('groovy')).not.toBeNull();
});

test('cannot see groovy and java options in the action type select when script management configuration is disabled', async ({
	actionPage,
	diagramViewPage,
	nodePropertiesSidebarPage,
	processBuilderPage,
	scriptManagementPage,
}) => {
	await scriptManagementPage.disableScriptManagementConfiguration();

	await processBuilderPage.goto();

	await processBuilderPage.clickWorkflowDefinitionName(
		workflowDefinitionName
	);

	await diagramViewPage.clickNode('review');

	await nodePropertiesSidebarPage.addActionButton.click();

	expect(await actionPage.getTypeSelectOption('java')).toBeNull();
	expect(await actionPage.getTypeSelectOption('groovy')).toBeNull();
});

test('cannot save a workflow definition that has a groovy action when script management configuration is disabled', async ({
	actionPage,
	diagramViewPage,
	nodePropertiesSidebarPage,
	page,
	processBuilderPage,
	scriptManagementPage,
}) => {
	await processBuilderPage.goto();

	await processBuilderPage.clickWorkflowDefinitionName(
		workflowDefinitionName
	);

	await diagramViewPage.clickNode('review');

	await nodePropertiesSidebarPage.addActionButton.click();

	await actionPage.fillWorkflowAction({
		name: 'Groovy Action',
		script: 'scriptTest',
		typeOption: 'Groovy',
	});

	await diagramViewPage.saveWorkflowDefinition();

	await scriptManagementPage.disableScriptManagementConfiguration();

	await processBuilderPage.goto();

	await processBuilderPage.clickWorkflowDefinitionName(
		workflowDefinitionName
	);

	await diagramViewPage.saveWorkflowDefinition();

	await expect(page.getByText('Error Updating Definition')).toBeVisible();
});

test('cannot save a workflow definition that has a java action when the script management configuration is disabled', async ({
	actionPage,
	diagramViewPage,
	nodePropertiesSidebarPage,
	page,
	processBuilderPage,
	scriptManagementPage,
}) => {
	await processBuilderPage.goto();

	await processBuilderPage.clickWorkflowDefinitionName(
		workflowDefinitionName
	);

	await diagramViewPage.clickNode('review');

	await nodePropertiesSidebarPage.addActionButton.click();

	await actionPage.fillWorkflowAction({
		name: 'Java Action',
		script: 'scriptTest',
		typeOption: 'Java',
	});

	await diagramViewPage.saveWorkflowDefinition();

	await scriptManagementPage.disableScriptManagementConfiguration();

	await processBuilderPage.goto();

	await processBuilderPage.clickWorkflowDefinitionName(
		workflowDefinitionName
	);

	await diagramViewPage.saveWorkflowDefinition();

	await expect(page.getByText('Error Updating Definition')).toBeVisible();
});

test('can save a workflow definition that has a customer extension action after changing from an UI edited UpdateStatus to a customer extension action', async ({
	actionPage,
	apiHelpers,
	diagramViewPage,
	nodePropertiesSidebarPage,
	page,
	processBuilderPage,
}) => {
	await apiHelpers.headlessAdminWorkflow.postWorkflowDefinitionSave(
		workflowDefinitionName,
		getWorkflowDefinition('sample-start-end')
	);

	await processBuilderPage.goto();

	await processBuilderPage.clickWorkflowDefinitionName(
		workflowDefinitionName
	);

	const nodeTitle = 'Start';

	await diagramViewPage.clickNode(nodeTitle);

	await nodePropertiesSidebarPage.addActionButton.click();

	await actionPage.fillWorkflowAction({
		name: 'Test Name',
		statusOption: '0',
		typeOption: 'Update Status',
	});

	await diagramViewPage.saveWorkflowDefinition();

	await actionPage.backButton.click();

	await diagramViewPage.clickNode(nodeTitle);

	await nodePropertiesSidebarPage.editActionButton.click();

	const typeActionOption =
		'function#liferay-sample-etc-spring-boot-workflow-action-1';

	await actionPage.fillWorkflowAction({
		name: 'Test Name',
		typeOption: typeActionOption,
	});

	await diagramViewPage.saveWorkflowDefinition();

	await waitForAlert(page, 'Success:Workflow saved.');

	await actionPage.backButton.click();

	await diagramViewPage.clickNode(nodeTitle);

	await actionPage.editActionButton.click();

	await expect(actionPage.selectActionType).toHaveValue(typeActionOption);

	await diagramViewPage.publishWorkflowDefinition();

	await waitForAlert(page, 'Success:Workflow published successfully.');

	await actionPage.backButton.click();

	await diagramViewPage.clickNode(nodeTitle);

	await actionPage.editActionButton.click();

	await expect(actionPage.selectActionType).toHaveValue(typeActionOption);
});

test('can save a workflow definition that has a customer extension action after changing from an UpdateStatus to a customer extension action', async ({
	apiHelpers,
	diagramViewPage,
	nodePropertiesSidebarPage,
	page,
	processBuilderPage,
}) => {
	const workflowDefinition =
		await apiHelpers.headlessAdminWorkflow.postWorkflowDefinitionSave(
			workflowDefinitionName,
			getWorkflowDefinition('sample-client-extension')
		);

	workflowDefinitionName = workflowDefinition.name;

	await processBuilderPage.goto();

	await processBuilderPage.clickWorkflowDefinitionName(
		workflowDefinitionName
	);

	await diagramViewPage.clickNode('Review');

	await nodePropertiesSidebarPage.editActionButton.click();

	await page
		.locator('#type')
		.selectOption(
			'function#liferay-sample-etc-spring-boot-workflow-action-1'
		);

	await diagramViewPage.saveWorkflowDefinition();

	await diagramViewPage.publishWorkflowDefinition();

	await expect(
		page.getByText('Workflow published successfully.')
	).toBeVisible();
});

test('can save a workflow definition that uses a script type action filling it type before the action name', async ({
	actionPage,
	diagramViewPage,
	nodePropertiesSidebarPage,
	page,
	processBuilderPage,
}) => {
	await processBuilderPage.goto();

	await processBuilderPage.clickWorkflowDefinitionName(
		workflowDefinitionName
	);

	await diagramViewPage.clickNode('created');

	await nodePropertiesSidebarPage.addActionButton.click();

	await actionPage.selectActionType.selectOption('Groovy');
	await actionPage.scriptInput.fill('scriptTest');
	await actionPage.scriptInput.blur();
	await actionPage.nameInput.fill('Groovy Action');

	await diagramViewPage.publishWorkflowDefinition();

	await expect(
		page.getByText('Workflow published successfully.')
	).toBeVisible();
});

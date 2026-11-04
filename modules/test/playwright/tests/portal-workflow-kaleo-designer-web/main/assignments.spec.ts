/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import postSingleApproverCopy from './utils/postSingleApproverCopy';

export const test = mergeTests(apiHelpersTest, loginTest(), workflowPagesTest);

let workflowDefinitionId: number;
let workflowDefinitionName: string;

test.beforeEach(async ({apiHelpers}) => {
	const workFlowDefinition = await postSingleApproverCopy(apiHelpers);

	workflowDefinitionId = workFlowDefinition.id;
	workflowDefinitionName = workFlowDefinition.name;
});

test.afterEach(async ({apiHelpers, scriptManagementPage}) => {
	await apiHelpers.headlessAdminWorkflow.deleteWorkflowDefinition(
		workflowDefinitionId
	);

	await scriptManagementPage.enableScriptManagementConfiguration();
});

test('can see Scripted Assignment option when script management configuration is enabled', async ({
	diagramViewPage,
	page,
	processBuilderPage,
}) => {
	await processBuilderPage.goto();

	await processBuilderPage.clickWorkflowDefinitionName(
		workflowDefinitionName
	);

	await diagramViewPage.clickNode('update');

	await page.getByRole('link', {exact: true, name: 'Asset Creator'}).click();

	expect(
		await page.$(`#assignment-type option[value="scriptedAssignment"]`)
	).not.toBeNull();
});

test('cannot see Scripted Assignment option when script management configuration is disabled', async ({
	diagramViewPage,
	page,
	processBuilderPage,
	scriptManagementPage,
}) => {
	await scriptManagementPage.disableScriptManagementConfiguration();

	await processBuilderPage.goto();

	await processBuilderPage.clickWorkflowDefinitionName(
		workflowDefinitionName
	);

	await diagramViewPage.clickNode('update');

	await page.getByRole('link', {exact: true, name: 'Asset Creator'}).click();

	expect(
		await page.$(`#assignment-type option[value="scriptedAssignment"]`)
	).toBeNull();
});

test('role name search box does not support regex strings', async ({
	diagramViewPage,
	nodePropertiesSidebarPage,
	page,
	processBuilderPage,
}) => {
	await processBuilderPage.goto();

	await processBuilderPage.clickWorkflowDefinitionName(
		workflowDefinitionName
	);

	await diagramViewPage.clickNode('review');

	await page.getByRole('link', {name: 'Role Type'}).click();

	await nodePropertiesSidebarPage.roleNameInput.fill('a');

	expect(
		await page.getByRole('menuitem').filter({hasText: 'Account'}).first()
	).toBeVisible();

	await nodePropertiesSidebarPage.roleNameInput.fill('a+');

	expect(
		await page.getByRole('menuitem').filter({hasText: 'Account'})
	).not.toBeVisible();
});

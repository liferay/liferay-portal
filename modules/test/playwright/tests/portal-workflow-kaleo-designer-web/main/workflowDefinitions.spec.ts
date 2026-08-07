/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {clientExtensionsPageTest} from '../../client-extension-web/fixtures/clientExtensionsPageTest';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import {getWorkflowDefinition} from './utils/getWorkflowDefinition';

export const test = mergeTests(
	apiHelpersTest,
	clientExtensionsPageTest,
	isolatedSiteTest,
	journalPagesTest,
	loginTest(),
	workflowPagesTest
);

let workflowDefinitionIds: number[] = [];

test.afterEach(async ({apiHelpers}) => {
	for (const workflowDefinitionId of workflowDefinitionIds) {
		await apiHelpers.headlessAdminWorkflow.deleteWorkflowDefinition(
			workflowDefinitionId
		);
	}

	workflowDefinitionIds = [];
});

test('can create a workflow definition with a slash in the name tag', async ({
	apiHelpers,
	diagramViewPage,
	processBuilderPage,
}) => {
	const workflowDefinitionName =
		'WorkflowDefinitionName/' + getRandomString();

	const workflowDefinition =
		await apiHelpers.headlessAdminWorkflow.postWorkflowDefinitionSave(
			workflowDefinitionName,
			getWorkflowDefinition('basic')
		);

	workflowDefinitionIds.push(workflowDefinition.id);

	await processBuilderPage.goto();

	await processBuilderPage.clickWorkflowDefinitionName(
		workflowDefinitionName
	);

	await expect(diagramViewPage.workflowDefinitionTitle).toHaveValue(
		workflowDefinitionName
	);
});

test('LPD-49034 Custom Workflow Action Client Extension not working when the asset has related contents', async ({
	apiHelpers,
	diagramViewPage,
	journalEditArticlePage,
	journalPage,
	page,
	processBuilderPage,
	site,
	workflowPage,
}) => {
	const workflowDefinitionName = getRandomString();

	const workflowDefinition =
		await apiHelpers.headlessAdminWorkflow.postWorkflowDefinitionSave(
			workflowDefinitionName,
			getWorkflowDefinition('sample-client-extension')
		);

	workflowDefinitionIds.push(workflowDefinition.id);

	await processBuilderPage.goto();

	await processBuilderPage.clickWorkflowDefinitionName(
		workflowDefinitionName
	);

	await expect(diagramViewPage.workflowDefinitionTitle).toHaveValue(
		workflowDefinitionName
	);

	await diagramViewPage.publishWorkflowDefinition();

	await diagramViewPage.goBack();

	await workflowPage.goto(site.friendlyUrlPath);

	await workflowPage.changeWorkflow(
		'Web Content Article',
		workflowDefinitionName
	);

	const basicWebContentStructureId =
		await getBasicWebContentStructureId(apiHelpers);

	const journalArticleTitle1 = getRandomString();

	await apiHelpers.jsonWebServicesJournal.addWebContent({
		ddmStructureId: basicWebContentStructureId,
		groupId: site.id,
		titleMap: {en_US: journalArticleTitle1},
	});

	await journalPage.goto(site.friendlyUrlPath);

	await expect(page.getByText(`${journalArticleTitle1}`)).toBeVisible();

	await expect(async () => {
		await journalPage.goto(site.friendlyUrlPath);

		await expect(
			page
				.locator('.list-group-item', {hasText: journalArticleTitle1})
				.getByText('Approved')
		).toBeVisible();
	}).toPass({
		intervals: [2000],
		timeout: 15000,
	});

	const journalArticleTitle2 = getRandomString();

	await journalPage.goToCreateArticle();

	await journalPage.articleTitleInput.fill(journalArticleTitle2);

	await page
		.locator('[data-qa-id="content"]')
		.getByRole('textbox', {name: 'Rich Text Editor'})
		.fill(getRandomString());

	const row = page
		.frameLocator('iframe[title="Select Basic Web Content"]')
		.locator('.list-group-item', {hasText: journalArticleTitle1});

	await expect(async () => {
		await journalEditArticlePage.openRelatedAsset();

		await expect(page.getByText('Select Basic Web Content')).toBeVisible({
			timeout: 3000,
		});
	}).toPass();

	await row.getByRole('checkbox').check();

	await clickAndExpectToBeHidden({
		target: page.locator('.modal-dialog'),
		trigger: page.getByRole('button', {name: 'Done'}),
	});

	await journalEditArticlePage.submitArticleForWorkflow(journalArticleTitle2);

	await expect(async () => {
		await journalPage.goto(site.friendlyUrlPath);

		await expect(
			page
				.locator('.list-group-item', {hasText: journalArticleTitle2})
				.getByText('Approved')
		).toBeVisible();
	}).toPass({
		intervals: [2000],
		timeout: 15000,
	});

	await workflowPage.goto(site.friendlyUrlPath);

	await workflowPage.changeWorkflow('Web Content Article', 'No Workflow', {
		disable: true,
	});

	await processBuilderPage.goto();

	await page
		.getByRole('row', {name: workflowDefinitionName})
		.getByRole('button')
		.click();

	await page.getByRole('link', {name: 'Unpublish'}).click();
});

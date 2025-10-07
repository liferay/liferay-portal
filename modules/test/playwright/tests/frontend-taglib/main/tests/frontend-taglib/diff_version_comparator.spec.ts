/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {workflowPagesTest} from '../../../../../fixtures/workflowPagesTest';
import getRandomString from '../../../../../utils/getRandomString';
import {waitForAlert} from '../../../../../utils/waitForAlert';
import {journalPagesTest} from '../../../../journal-web/main/fixtures/journalPagesTest';
import {samplePageTest} from '../../fixtures/samplePageTest';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	journalPagesTest,
	samplePageTest,
	workflowPagesTest
);

test(
	'Check Diff Versions are displaying correctly',
	{tag: '@LPD-61235'},
	async ({
		journalEditArticlePage,
		journalPage,
		page,
		workflowPage,
		workflowTasksPage,
	}) => {
		const title = getRandomString();

		await test.step('Enable Single Approval for Web Content', async () => {
			await workflowPage.goto();

			await workflowPage.changeWorkflow(
				'Web Content Article',
				'Single Approver'
			);
		});

		await test.step('Create web content for workflow and approve it', async () => {
			await journalPage.goto();

			await journalPage.changeView('List');

			await journalEditArticlePage.goto();

			await journalEditArticlePage.submitArticleForWorkflow(title);

			await workflowTasksPage.goToAssignedToMyRoles();

			await workflowTasksPage.assignToMe(title);

			await workflowTasksPage.approve(title);
		});

		await test.step('Make edit to web content and approve workflow', async () => {
			await journalPage.goto();

			await page.getByLabel(`Actions for ${title}`).click();

			await page.getByRole('menuitem', {name: 'Edit'}).click();

			await page
				.getByRole('application', {name: 'Content,'})
				.frameLocator('iframe[title="editor"]')
				.getByRole('textbox')
				.fill('test');

			await page.getByLabel('Select and Confirm Submit').click();

			await page
				.getByRole('menuitem', {
					exact: true,
					name: 'Submit for Workflow',
				})
				.click();

			await waitForAlert(page, 'Success:');

			await workflowTasksPage.goToAssignedToMyRoles();

			await workflowTasksPage.assignToMe(title);

			await workflowTasksPage.approve(title);
		});

		await test.step('Check version dropdown displays multiple versions', async () => {
			await page
				.getByRole('cell', {name: `${title}`})
				.nth(1)
				.click();

			await page.getByLabel('Diffs').click();

			await page
				.frameLocator('iframe[title="Diffs"]')
				.getByRole('button', {name: 'Version 1.0'})
				.click();

			await expect(
				page
					.frameLocator('iframe[title="Diffs"]')
					.getByRole('menuitem', {name: 'Version 1.1'})
			).toBeVisible();
		});
	}
);

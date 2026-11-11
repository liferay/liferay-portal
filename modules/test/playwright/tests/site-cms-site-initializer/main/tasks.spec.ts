/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	loginTest(),
	workflowPagesTest
);

test.beforeEach(async ({apiHelpers}) => {
	const cmpProjectObjectDefinition =
		await apiHelpers.objectAdmin.getObjectDefinitionByName('CMPProject');

	test.skip(
		Boolean(cmpProjectObjectDefinition),
		'CMP overrides the CMS /tasks page with its own tabbed UI (LPD-112720).'
	);
});

function getQuickFilterButton(page: Page, label: string) {
	return page.getByRole('button', {name: new RegExp(`^\\d+\\s*${label}$`)});
}

async function getQuickFilterCount(page: Page, label: string) {
	const text = await getQuickFilterButton(page, label).innerText();

	return Number(text.match(/\d+/)?.[0]);
}

async function expectResultsMatchQuickFilter(page: Page, label: string) {
	const count = await getQuickFilterCount(page, label);

	await expect(
		page.getByText(new RegExp(`^${count} Results? Found for`))
	).toBeVisible();
}

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

			const taskRow = page
				.getByRole('row')
				.filter({hasText: contentName});

			await expect(taskRow).toBeVisible();
			await expect(taskRow.getByText('Basic Web Content')).toBeVisible();
			await expect(taskRow.getByText('Pending')).toBeVisible();

			await test.step('Row actions include Assign to Me, Assign to... and Update Due Date', async () => {
				await taskRow.getByRole('button').click();

				await expect(
					page.getByRole('menuitem', {name: 'Assign to Me'})
				).toBeVisible();
				await expect(
					page.getByRole('menuitem', {name: 'Assign to...'})
				).toBeVisible();
				await expect(
					page.getByRole('menuitem', {name: 'Update Due Date'})
				).toBeVisible();

				await page
					.getByRole('menuitem', {name: 'Assign to Me'})
					.click();
				await page.getByRole('button', {name: 'Save'}).click();

				await waitForAlert(
					page,
					'Success:Your request completed successfully.'
				);
			});

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

test(
	'Filters the tasks with the Pending, Overdue and Completed cards',
	{tag: '@LPD-113940'},
	async ({apiHelpers, page, viewTasksPage}) => {
		const space = await apiHelpers.headlessAssetLibrary.createSpace();

		const objectDefinition =
			await apiHelpers.objectAdmin.getObjectDefinitionByName(
				'CMSBasicWebContent'
			);

		const workflowDefinition =
			await apiHelpers.headlessAdminWorkflow.getWorkflowDefinitionByName(
				'Single Approver'
			);

		await apiHelpers.headlessAdminWorkflow.postWorkflowDefinitionLink(
			objectDefinition.className,
			space.siteId,
			workflowDefinition.id,
			workflowDefinition.name,
			Number(workflowDefinition.version)
		);

		const completedTitle = `Completed ${getRandomString()}`;
		const overdueTitle = `Overdue ${getRandomString()}`;
		const pendingTitle = `Pending ${getRandomString()}`;

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: pendingTitle,
			},
			'cms/basic-web-contents',
			space.name
		);

		const completedObjectEntry =
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: completedTitle,
				},
				'cms/basic-web-contents',
				space.name
			);

		const overdueObjectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: overdueTitle,
			},
			'cms/basic-web-contents',
			space.name
		);

		const userAccount =
			await apiHelpers.headlessAdminUser.getMyUserAccount();

		async function getWorkflowTask(objectEntryId: number) {
			let workflowTask;

			await expect(async () => {
				workflowTask =
					await apiHelpers.headlessAdminWorkflow.getWorkflowTaskByAsset(
						objectDefinition.className,
						String(objectEntryId)
					);

				expect(workflowTask).toBeTruthy();
			}).toPass();

			await apiHelpers.headlessAdminWorkflow.postAssignTaskToUser(
				workflowTask.id,
				userAccount.id
			);

			return workflowTask;
		}

		const completedWorkflowTask = await getWorkflowTask(
			completedObjectEntry.id
		);

		await apiHelpers.headlessAdminWorkflow.postWorkflowTaskChangeTransition(
			completedWorkflowTask.id,
			'approve'
		);

		const overdueWorkflowTask = await getWorkflowTask(
			overdueObjectEntry.id
		);

		await apiHelpers.headlessAdminWorkflow.postWorkflowTaskUpdateDueDate(
			overdueWorkflowTask.id,
			new Date(Date.parse(overdueWorkflowTask.dateCreated) + 1000)
		);

		await page.clock.install({time: Date.now() + 2 * 60 * 1000});

		await viewTasksPage.goto();

		const completedRow = page
			.getByRole('row')
			.filter({hasText: completedTitle});
		const overdueRow = page
			.getByRole('row')
			.filter({hasText: overdueTitle});
		const pendingRow = page
			.getByRole('row')
			.filter({hasText: pendingTitle});

		await test.step('Show the due date with its time', async () => {
			await expect(overdueRow).toContainText(/\d{1,2}:\d{2}/);
		});

		await test.step('Show a dash for tasks without due date', async () => {
			await expect(
				pendingRow.getByRole('cell', {exact: true, name: '--'})
			).toBeVisible();
		});

		await test.step('Show the assignee', async () => {
			await expect(overdueRow).toContainText(userAccount.name);
			await expect(pendingRow).toContainText('Administrator');
		});

		await test.step('Filter by the Pending card', async () => {
			await getQuickFilterButton(page, 'Pending').click();

			await expect(getQuickFilterButton(page, 'Pending')).toHaveAttribute(
				'aria-pressed',
				'true'
			);

			await expect(pendingRow).toBeVisible();
			await expect(overdueRow).toBeVisible();
			await expect(completedRow).toBeHidden();

			await expectResultsMatchQuickFilter(page, 'Pending');
		});

		await test.step('Filter by the Completed card', async () => {
			await getQuickFilterButton(page, 'Completed').click();

			await expect(
				getQuickFilterButton(page, 'Completed')
			).toHaveAttribute('aria-pressed', 'true');
			await expect(getQuickFilterButton(page, 'Pending')).toHaveAttribute(
				'aria-pressed',
				'false'
			);

			await expect(completedRow).toBeVisible();
			await expect(overdueRow).toBeHidden();
			await expect(pendingRow).toBeHidden();

			await expectResultsMatchQuickFilter(page, 'Completed');
		});

		await test.step('Filter by the Overdue card', async () => {
			await getQuickFilterButton(page, 'Overdue').click();

			await expect(getQuickFilterButton(page, 'Overdue')).toHaveAttribute(
				'aria-pressed',
				'true'
			);

			await expect(overdueRow).toBeVisible();
			await expect(completedRow).toBeHidden();
			await expect(pendingRow).toBeHidden();

			await expectResultsMatchQuickFilter(page, 'Overdue');
		});

		await test.step('Clear the filters', async () => {
			await page.getByRole('button', {name: 'Clear'}).click();

			for (const label of ['Completed', 'Overdue', 'Pending']) {
				await expect(getQuickFilterButton(page, label)).toHaveAttribute(
					'aria-pressed',
					'false'
				);
			}

			await expect(completedRow).toBeVisible();
			await expect(overdueRow).toBeVisible();
			await expect(pendingRow).toBeVisible();
		});
	}
);

test(
	'Shows the Content Marketing Platform banner until the user dismisses it',
	{tag: '@LPD-113939'},
	async ({page, viewTasksPage}) => {
		const banner = page.getByRole('region', {
			name: 'Run Smarter Campaigns With Liferay Content Marketing Platform',
		});

		function resetBanner() {
			return page.evaluate(() =>
				Liferay.Util.Session.set(
					'com_liferay_site_cms_site_initializer_cmpMarketplaceBannerDismissed',
					'false'
				)
			);
		}

		await viewTasksPage.goto();
		await resetBanner();
		await viewTasksPage.goto();

		await expect(banner).toBeVisible();

		const marketplaceLink = banner.getByRole('link', {
			name: 'Explore Marketplace (Opens a new window)',
		});

		await expect(marketplaceLink).toHaveAttribute(
			'href',
			'https://marketplace.liferay.com/p/content-marketing-platform?utm_medium=referral&utm_source=ft-cms&utm_content=cmp&utm_cid=701VO00001AIWBFYA5'
		);
		await expect(marketplaceLink).toHaveAttribute('target', '_blank');

		await banner.getByRole('button', {name: 'Close'}).click();

		await expect(banner).toBeHidden();

		await viewTasksPage.goto();

		await expect(banner).toBeHidden();

		await resetBanner();
	}
);

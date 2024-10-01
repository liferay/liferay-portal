/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import moment from 'moment';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {changeTrackingPagesTest} from '../../fixtures/changeTrackingPagesTest';
import {workflowPagesTest} from '../../fixtures/workflowPagesTest';
import getRandomString from '../../utils/getRandomString';
import {waitForAlert} from '../../utils/waitForAlert';
import {journalPagesTest} from '../journal-web/fixtures/journalPagesTest';

export const test = mergeTests(
	apiHelpersTest,
	journalPagesTest,
	changeTrackingPagesTest,
	workflowPagesTest
);

let date;
let journalName;
const displayData = [
	'Status',
	'Assigned to',
	'Task Name',
	'Create Date',
	'Due Date',
	'Usages',
	'Activities',
];

test.beforeEach(
	async ({
		apiHelpers,
		ctCollection,
		journalEditArticlePage,
		workflowPage,
	}) => {
		await apiHelpers.headlessChangeTracking.checkoutCTCollection('0');

		await workflowPage.goto();
		await workflowPage.changeWorkflow(
			'Web Content Article',
			'Single Approver'
		);

		await apiHelpers.headlessChangeTracking.checkoutCTCollection(
			ctCollection.id
		);

		journalName = getRandomString();
		await journalEditArticlePage.goto();
		await journalEditArticlePage.submitArticleForWorkflow(journalName);

		date = moment().format('M/D/YY h:mm A');
	}
);

test.afterEach(async ({apiHelpers, page, workflowPage}) => {
	await apiHelpers.headlessChangeTracking.checkoutCTCollection('0');

	await workflowPage.goto();

	const row = await page
		.getByRole('row')
		.filter({hasText: 'Web Content Article'});

	const workflowEnabled = await row
		.getByTitle('Workflow Definition')
		.filter({hasText: 'Single Approver'});

	if (workflowEnabled) {
		await workflowPage.changeWorkflow(
			'Web Content Article',
			'No Workflow',
			{
				disable: true,
			}
		);
	}
});

test('LPD-19748 Add workflow info to the View Change screen', async ({
	changeTrackingPage,
	ctCollection,
	page,
}) => {
	await changeTrackingPage.goToReviewChanges(ctCollection.name);

	await changeTrackingPage.reviewChange(journalName);

	await expect(page.getByText(`Pending`)).toBeVisible();

	await changeTrackingPage.viewDisplayTab('Workflow');
});

test('LPD-19748 Workflow data is displayed in tab', async ({
	changeTrackingPage,
	ctCollection,
	page,
}) => {
	await changeTrackingPage.goToReviewChanges(ctCollection.name);

	await changeTrackingPage.reviewChange(journalName);

	await changeTrackingPage.selectTab('Workflow');

	for (const data of displayData) {
		await expect(page.getByText(data, {exact: true})).toBeVisible();
	}
});

test('LPD-19748 Only workflow status is displayed when workflow is disabled', async ({
	changeTrackingPage,
	ctCollection,
	page,
	workflowPage,
}) => {
	await workflowPage.goto();

	await workflowPage.changeWorkflow('Web Content Article', 'No Workflow', {
		disable: true,
	});

	await changeTrackingPage.goToReviewChanges(ctCollection.name);

	await changeTrackingPage.reviewChange(journalName);

	await expect(page.getByText(`Pending`)).toBeVisible();

	await changeTrackingPage.viewDisplayTab('Workflow', {isHidden: true});
});

test('LPD-19763 Workflow assign actions are displayed in dropdown', async ({
	changeTrackingPage,
	ctCollection,
	page,
}) => {
	await changeTrackingPage.goToReviewChanges(ctCollection.name);

	await changeTrackingPage.reviewChange(journalName);

	const moreActionsButton = page.getByLabel('more-actions');

	await moreActionsButton.click();

	const assignToMeMenuItem = page.getByRole('menuitem', {
		name: 'Assign to me',
	});

	await expect(assignToMeMenuItem).toBeVisible();

	await expect(
		page.getByRole('menuitem', {
			name: 'Assign to...',
		})
	).toBeVisible();

	await assignToMeMenuItem.click();

	const doneButton = page
		.frameLocator('iframe[title="Assign to Me"]')
		.getByRole('button', {exact: true, name: 'Done'});

	await doneButton.click();

	await changeTrackingPage.selectTab('Workflow');

	await page.getByRole('cell', {exact: true, name: 'Test Test'});

	await moreActionsButton.click();

	await expect(
		page.getByRole('menuitem', {
			name: 'Assign to...',
		})
	).toBeVisible();
});

test('LPD-22673 View Usages link is added to workflow info display', async ({
	changeTrackingPage,
	ctCollection,
	page,
}) => {
	await changeTrackingPage.goToReviewChanges(ctCollection.name);

	await changeTrackingPage.reviewChange(journalName);

	await changeTrackingPage.selectTab('Workflow');

	await page.getByRole('link', {exact: true, name: 'View Usages'}).click();

	await expect(page.getByText(`Usages: ${journalName}`)).toBeVisible();
});

test('LPD-23974 Comments link is added to workflow info display', async ({
	changeTrackingPage,
	ctCollection,
	page,
}) => {
	await changeTrackingPage.goToReviewChanges(ctCollection.name);

	await changeTrackingPage.reviewChange(journalName);

	await changeTrackingPage.selectTab('Workflow');

	await page.getByRole('link', {exact: true, name: '0 Comments'}).click();

	await expect(
		page.getByTestId('headerTitle').getByText(`Review: ${journalName}`)
	).toBeVisible();

	await page.getByRole('button', {name: 'Comments'}).click();

	await page
		.frameLocator('iframe')
		.getByRole('textbox')
		.fill('Sample Comment');

	await page.getByRole('button', {name: 'Reply'}).click();

	await page.getByRole('link', {name: 'Back'}).click();

	await changeTrackingPage.selectTab('Workflow');

	await expect(
		page.getByRole('link', {exact: true, name: '1 Comment'})
	).toBeVisible();
});

test('LPD-23331 Workflow data is displayed when workflow task is approved', async ({
	changeTrackingPage,
	ctCollection,
	page,
	workflowTasksPage,
}) => {
	await workflowTasksPage.goToAssignedToMyRoles();

	await workflowTasksPage.assignToMe(journalName);

	await workflowTasksPage.approve(journalName);

	await changeTrackingPage.goToReviewChanges(ctCollection.name);

	await changeTrackingPage.reviewChange(journalName);

	await changeTrackingPage.viewDisplayTab('Workflow');

	await changeTrackingPage.selectTab('Workflow');

	for (const data of displayData) {
		await expect(page.getByText(data, {exact: true})).toBeVisible();
	}
});

test('LPD-23969 Activities tab is added to workflow info display', async ({
	changeTrackingPage,
	ctCollection,
	page,
}) => {
	await changeTrackingPage.goToReviewChanges(ctCollection.name);

	await changeTrackingPage.reviewChange(journalName);

	await changeTrackingPage.selectTab('Workflow');

	await page.getByRole('button', {exact: true, name: 'Activities'}).click();

	await expect(
		page.getByRole('cell', {exact: true, name: 'Activity Description'})
	).toBeVisible();

	await expect(
		page.getByRole('cell', {exact: true, name: 'Date'})
	).toBeVisible();

	await expect(
		page
			.getByRole('row', {
				exact: true,
				name: `Task initially assigned to the Administrator role. Assigned initial task. ${date}`,
			})
			.getByRole('cell')
			.nth(1)
	).toBeVisible();

	await expect(
		page.getByRole('button', {exact: true, name: 'View More'})
	).toBeHidden();
});

test('LPD-25058 View More button is added to workflow Activities tab for many rows', async ({
	changeTrackingPage,
	ctCollection,
	page,
	workflowTasksPage,
}) => {
	await workflowTasksPage.goToAssignedToMyRoles();

	await workflowTasksPage.assignToMe(journalName);

	await workflowTasksPage.reject(journalName);

	await workflowTasksPage.resubmit(journalName);

	await changeTrackingPage.goToReviewChanges(ctCollection.name);

	await changeTrackingPage.reviewChange(journalName);

	await changeTrackingPage.selectTab('Workflow');

	await page.getByRole('button', {exact: true, name: 'Activities'}).click();

	await page.getByRole('button', {exact: true, name: 'View More'}).click();

	await expect(
		page.getByRole('button', {exact: true, name: 'View More'})
	).toBeHidden();
});

test('LPD-24645 Workflow tab is not present for draft', async ({
	changeTrackingPage,
	ctCollection,
	journalEditArticlePage,
	page,
}) => {
	const title = getRandomString();

	await journalEditArticlePage.goto();

	await journalEditArticlePage.fillTitle(title);

	await page.getByRole('button', {name: 'Save as Draft'}).click();

	await page.getByText('Version: 1.0 Draft').waitFor();

	await changeTrackingPage.goToReviewChanges(ctCollection.name);

	await changeTrackingPage.reviewChange(title);

	await changeTrackingPage.viewDisplayTab('Workflow', {isHidden: true});
});

test('LPD-22771 Assign button added to workflow view', async ({
	changeTrackingPage,
	ctCollection,
	page,
}) => {
	await changeTrackingPage.goToReviewChanges(ctCollection.name);

	await changeTrackingPage.reviewChange(journalName);

	await changeTrackingPage.selectTab('Workflow');

	await page
		.getByRole('button', {
			exact: true,
			name: 'Assign to...',
		})
		.click();

	await page
		.frameLocator('iframe[title="Assign to\\.\\.\\."]')
		.getByLabel('Assign to')
		.selectOption('test (Test Test)');

	await page
		.frameLocator('iframe[title="Assign to..."]')
		.getByRole('button', {exact: true, name: 'Done'})
		.click();

	await expect(
		page.getByRole('cell').and(page.getByText('Test Test'))
	).toBeVisible();
});

test('LPD-22771 Assign button is not visible in other publications', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	page,
}) => {
	await apiHelpers.headlessChangeTracking.checkoutCTCollection('0');

	await changeTrackingPage.goToReviewChanges(ctCollection.name);

	await changeTrackingPage.reviewChange(journalName);

	await changeTrackingPage.selectTab('Workflow');

	const assignButton = page.getByRole('button', {
		exact: true,
		name: 'Assign to...',
	});

	await expect(assignButton).toBeVisible({visible: false});
});

test('LPD-23430 Workflow transition actions are displayed in dropdown', async ({
	changeTrackingPage,
	ctCollection,
	page,
}) => {
	await changeTrackingPage.goToReviewChanges(ctCollection.name);

	await changeTrackingPage.reviewChange(journalName);

	const moreActionsButton = page.getByLabel('more-actions');

	await moreActionsButton.click();

	const assignToMeMenuItem = page.getByRole('menuitem', {
		name: 'Assign to me',
	});

	await assignToMeMenuItem.click();

	await page
		.frameLocator('iframe[title="Assign to Me"]')
		.getByRole('button', {exact: true, name: 'Done'})
		.click();

	await moreActionsButton.click();

	await page.getByRole('menuitem', {name: 'Reject'}).click();

	await expect(page.getByRole('heading', {name: 'Reject'})).toBeVisible();

	const doneButton = page.getByText('Done');

	await doneButton.click();

	await page.reload();

	await expect(
		page.locator('span').filter({hasText: 'Pending'}).first()
	).toBeVisible();

	await moreActionsButton.click();

	await page.getByRole('menuitem', {name: 'Resubmit'}).click();

	await expect(page.getByRole('heading', {name: 'Resubmit'})).toBeVisible();

	await doneButton.click();

	await page.reload();

	await moreActionsButton.click();

	await assignToMeMenuItem.click();

	await page
		.frameLocator('iframe[title="Assign to Me"]')
		.getByRole('button', {exact: true, name: 'Done'})
		.click();

	await page.getByRole('cell', {exact: true, name: 'Test Test'});

	await moreActionsButton.click();

	await page.getByRole('menuitem', {name: 'Approve'}).click();

	await expect(page.getByRole('heading', {name: 'Approve'})).toBeVisible();

	await doneButton.click();

	await expect(
		page.locator('span').filter({hasText: 'Approved'}).first()
	).toBeVisible();
});

test('LPD-27013 Cannot assign tasks once task is completed', async ({
	changeTrackingPage,
	ctCollection,
	page,
	workflowTasksPage,
}) => {
	await workflowTasksPage.goToAssignedToMyRoles();

	await workflowTasksPage.assignToMe(journalName);

	await workflowTasksPage.approve(journalName);

	await changeTrackingPage.goToReviewChanges(ctCollection.name);

	await changeTrackingPage.reviewChange(journalName);

	await changeTrackingPage.viewDisplayTab('Workflow');

	await changeTrackingPage.selectTab('Workflow');

	const assignButton = page.getByRole('button', {
		exact: true,
		name: 'Assign to...',
	});

	await expect(assignButton).toBeVisible({visible: false});

	await page.getByLabel('more-actions').click();

	await expect(
		page.getByRole('menuitem', {
			name: 'Assign to...',
		})
	).toBeVisible({visible: false});
});

test('LPD-24758 Error when viewing Workflow tab in publication history', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	page,
	workflowTasksPage,
}) => {
	await workflowTasksPage.goToAssignedToMyRoles();

	await workflowTasksPage.assignToMe(journalName);

	await workflowTasksPage.approve(journalName);

	await apiHelpers.headlessChangeTracking.publishCTCollection(
		ctCollection.id
	);

	await changeTrackingPage.goToReviewChangesHistory(ctCollection.name);

	await changeTrackingPage.reviewChange(journalName);

	await changeTrackingPage.selectTab('Workflow');

	for (const data of displayData) {
		await expect(page.getByText(data, {exact: true})).toBeVisible();
	}

	await expect(page.getByLabel('more-actions')).toBeHidden();
});

test('LPD-28970 Error when viewing data tab after viewing Workflow tab', async ({
	changeTrackingPage,
	ctCollection,
	page,
}) => {
	await changeTrackingPage.goToReviewChanges(ctCollection.name);

	await changeTrackingPage.reviewChange(journalName);

	await changeTrackingPage.selectTab('Workflow');

	await changeTrackingPage.selectTab('Data');

	await expect(page.locator('.alert-danger')).not.toBeVisible();

	await expect(
		page.getByRole('cell', {exact: true, name: journalName})
	).toBeVisible();
});

test('LPD-28734 SuccessMessage appears on Workflow Portlet after doing workflow transition actions', async ({
	changeTrackingPage,
	ctCollection,
	page,
}) => {
	await changeTrackingPage.goToReviewChanges(ctCollection.name);

	await changeTrackingPage.reviewChange(journalName);

	await changeTrackingPage.selectTab('Workflow');

	const moreActionsButton = page.getByLabel('more-actions');

	await moreActionsButton.click();

	const assignToMeMenuItem = page.getByRole('menuitem', {
		name: 'Assign to me',
	});

	await expect(assignToMeMenuItem).toBeVisible();

	await assignToMeMenuItem.click();

	const assignDoneButton = page
		.frameLocator('iframe[title="Assign to Me"]')
		.getByRole('button', {exact: true, name: 'Done'});

	await assignDoneButton.click();

	await moreActionsButton.click();

	const approveMenuItem = page.getByRole('menuitem', {name: 'Approve'});

	await expect(approveMenuItem).toBeVisible();

	await approveMenuItem.click();

	await expect(page.getByRole('heading', {name: 'Approve'})).toBeVisible();

	const approveDoneButton = page.getByRole('button', {
		exact: true,
		name: 'Done',
	});

	await approveDoneButton.click();

	await expect(
		page.locator('span').filter({hasText: 'Approved'}).first()
	).toBeVisible();

	await page.locator('button[data-qa-id="userPersonalMenu"]').click();

	await page.getByRole('menuitem', {name: 'My Workflow Tasks'}).click();

	await expect(
		page.getByRole('heading', {name: 'My Workflow Tasks'})
	).toBeVisible();

	await expect(
		page.locator('#ToastAlertContainer .alert-success').first()
	).toBeHidden();
});

test('LPD-28975 Workflow tab shows unexpected error for asset added in publication and subsequently enabling workflow', async ({
	changeTrackingPage,
	ctCollection,
	journalEditArticlePage,
	page,
	workflowPage,
}) => {
	await changeTrackingPage.workOnProduction();

	await workflowPage.goto();

	await workflowPage.changeWorkflow('Web Content Article', 'No Workflow', {
		disable: true,
	});

	await changeTrackingPage.workOnPublication(ctCollection);

	await journalEditArticlePage.goto();

	const title1 = getRandomString();

	await journalEditArticlePage.fillTitle(title1);

	await page.getByRole('button', {name: 'Publish'}).click();

	await waitForAlert(page, `Success:${title1} was created successfully.`);

	await workflowPage.goto();

	await workflowPage.changeWorkflow('Web Content Article', 'Single Approver');

	await journalEditArticlePage.goto();

	const title2 = getRandomString();

	await journalEditArticlePage.submitArticleForWorkflow(title2);

	await changeTrackingPage.goToReviewChanges(ctCollection.name);

	await changeTrackingPage.reviewChange(title1);

	await changeTrackingPage.viewDisplayTab('Workflow', {isHidden: true});
});

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {changeTrackingPagesTest} from '../../../fixtures/changeTrackingPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import getRandomString from '../../../utils/getRandomString';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';

export const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPS-164563': {enabled: true},
	}),
	journalPagesTest,
	changeTrackingPagesTest,
	workflowPagesTest
);

let journalName;

test.beforeEach(
	async ({
		apiHelpers,
		ctCollection,
		journalEditArticlePage,
		workflowPage,
	}) => {
		await workflowPage.goto();
		await workflowPage.changeWorkflow(
			'Web Content Article',
			'Single Approver'
		);

		await apiHelpers.headlessChangeTracking.checkoutCTCollection(
			ctCollection.body.id
		);

		journalName = getRandomString();
		await journalEditArticlePage.goto();
		await journalEditArticlePage.submitArticleForWorkflow(journalName);
	}
);

test.afterEach(async ({apiHelpers, workflowPage}) => {
	await apiHelpers.headlessChangeTracking.checkoutCTCollection(0);

	await workflowPage.goto();

	await workflowPage.changeWorkflow('Web Content Article', 'No Workflow', {
		disable: true,
	});
});

test('LPD-71138 Add new Pending Approval status', async ({
	changeTrackingPage,
	ctCollection,
	page,
	workflowTasksPage,
}) => {
	await changeTrackingPage.goto();

	await expect(page.getByText(`Pending Approval`)).toBeVisible();

	await workflowTasksPage.goToAssignedToMyRoles();

	await workflowTasksPage.assignToMe(journalName);

	await workflowTasksPage.approve(journalName);

	await changeTrackingPage.goto();

	const row = page
		.locator('tr')
		.filter({hasText: ctCollection.body.name})
		.filter({hasText: 'In Progress'});

	await expect(row).toBeVisible();
});

test('LPD-71138 Add status filter for ongoing publications', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	page,
}) => {
	const inProgressCTCollection =
		await apiHelpers.headlessChangeTracking.createCTCollection(
			getRandomString()
		);

	await changeTrackingPage.goto();

	const filtersDropdown = page.locator('.filters-dropdown-button');

	await filtersDropdown.waitFor();
	await filtersDropdown.click();

	await page.getByRole('menuitem', {name: 'Status'}).click();

	const pendingApprovalCheckbox = page.getByRole('checkbox', {
		name: 'Pending Approval',
	});

	await pendingApprovalCheckbox.check();

	const addFilterButton = page.getByRole('button', {
		exact: true,
		name: 'Add Filter',
	});

	await addFilterButton.click();

	await expect(
		page.getByRole('link', {name: ctCollection.body.name})
	).toBeVisible();

	await expect(
		page.getByRole('link', {name: inProgressCTCollection.body.name})
	).not.toBeVisible();

	await filtersDropdown.waitFor();
	await filtersDropdown.click();

	const inProgressCheckbox = page.getByRole('checkbox', {
		name: 'In Progress',
	});

	await inProgressCheckbox.check();
	await pendingApprovalCheckbox.uncheck();

	await page.getByRole('button', {name: 'Show Results'}).click();

	await expect(
		page.getByRole('link', {name: inProgressCTCollection.body.name})
	).toBeVisible();

	await expect(
		page.getByRole('link', {name: ctCollection.body.name})
	).not.toBeVisible();

	await apiHelpers.headlessChangeTracking.deleteCTCollection(
		inProgressCTCollection.body.id
	);
});

test('LPD-76512 User custom view is enabled for publications', async ({
	changeTrackingPage,
	page,
}) => {
	await changeTrackingPage.goto();

	const viewsSelectorButton = page.getByLabel('Views');

	await expect(viewsSelectorButton).toBeVisible();

	await expect(viewsSelectorButton).toHaveText('Default View');
});

/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {workflowPagesTest} from '../../../../../fixtures/workflowPagesTest';
import {liferayConfig} from '../../../../../liferay.config';
import getRandomString from '../../../../../utils/getRandomString';
import {EFDSVisualizationMode, waitForFDS} from '../../../../../utils/waitFor';
import {fdsSampleApiHelpersTest} from '../../fixtures/fdsSampleApiHelpersTest';
import {fdsSamplePageTest} from '../../fixtures/fdsSamplePageTest';

const test = mergeTests(
	apiHelpersTest,
	fdsSampleApiHelpersTest,
	fdsSamplePageTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	workflowPagesTest
);

const fdsItemERC = getRandomString();
const fdsItemTitle = 'Sample 300';

test.afterEach(async ({fdsSampleApiHelpers}) => {
	await fdsSampleApiHelpers.deleteFDSSampleItem({erc: fdsItemERC});
});

test('Dynamic Actions', async ({
	configurationTabPage,
	fdsSampleApiHelpers,
	fdsSamplePage,
	page,
	site,
}) => {
	let fdsSampleUrl: string;

	await test.step('Configure FDS sample', async () => {
		const {url} = await fdsSamplePage.setupFDSSampleWidget({site});

		fdsSampleUrl = url;

		await fdsSamplePage.selectTab('Advanced');

		await waitForFDS({
			page,
			visualizationMode: EFDSVisualizationMode.TABLE,
		});
	});

	await test.step('Assign "Single Approver" workflow to FDSSample', async () => {
		const processBuilderWorkflowsUrl =
			'/group/control_panel/manage?p_p_id=com_liferay_portal_workflow_web_portlet_ControlPanelWorkflowPortlet';
		const processBuilderConfigurationUrl =
			'_com_liferay_portal_workflow_web_portlet_ControlPanelWorkflowPortlet_tab=configuration';

		await page.goto(
			`${liferayConfig.environment.baseUrl}${processBuilderWorkflowsUrl}&${processBuilderConfigurationUrl}`
		);

		await page.waitForLoadState('networkidle');

		await page
			.getByRole('searchbox', {
				name: 'Search for:',
			})
			.fill('Frontend Data Set Sample');

		await page.keyboard.press('Enter');

		await page
			.locator('.lfr-search-container-wrapper table')
			.waitFor({state: 'visible'});

		await configurationTabPage.assignWorkflowToAssetType(
			'Single Approver',
			'Frontend Data Set Sample'
		);
	});

	await test.step('Create a new FDS Sample item', async () => {
		await fdsSampleApiHelpers.createFDSSampleItem({
			color: 'Black',
			date: new Date().toISOString(),
			description: `Description of ${fdsItemTitle}`,
			externalReferenceCode: fdsItemERC,
			imageURL: '/',
			size: 'Tiny',
			title: fdsItemTitle,
		});
	});

	await test.step('Check that the new FDS Sample item appears and has status "PENDING"', async () => {
		await page.goto(fdsSampleUrl);

		await fdsSamplePage.selectTab('Advanced');

		await waitForFDS({
			page,
			visualizationMode: EFDSVisualizationMode.TABLE,
		});

		await fdsSamplePage.activeFiltersToolbar.clearButton.click();

		const searchInput = fdsSamplePage.managementToolbar.searchInput;

		await searchInput.fill(fdsItemTitle);

		await fdsSamplePage.managementToolbar.searchButton.click();

		await waitForFDS({
			page,
			visualizationMode: EFDSVisualizationMode.TABLE,
		});

		await expect(fdsSamplePage.table.bodyRows).toHaveCount(1);

		await expect(
			fdsSamplePage.table.bodyRows
				.first()
				.getByRole('cell', {name: 'Pending'})
		).toBeVisible();
	});

	await test.step('Go to Dynamic Actions tab and check that there is pending task', async () => {
		await fdsSamplePage.selectTab('Dynamic Actions');

		await waitForFDS({
			container: '.fds-roles-tasks',
			page,
			visualizationMode: EFDSVisualizationMode.TABLE,
		});

		await waitForFDS({
			container: '.fds-user-tasks',
			empty: true,
			page,
		});
	});

	await test.step('Assign the pending task to the current user', async () => {
		await fdsSamplePage.assignTaskToMe();
	});

	await test.step('Check that the pending task appears for the current user', async () => {
		await page.reload();

		await waitForFDS({
			container: '.fds-user-tasks',
			page,
			visualizationMode: EFDSVisualizationMode.TABLE,
		});

		await expect(
			page.locator('.fds-user-tasks table tbody tr')
		).toHaveCount(1);

		await expect(page.getByRole('cell', {name: fdsItemERC})).toBeVisible();
	});

	await test.step('Check that the pending task has workflow actions', async () => {
		const tableItemActionButton =
			fdsSamplePage.table.itemActionButtons.first();

		await expect(tableItemActionButton).toBeVisible();

		await tableItemActionButton.click();

		await expect(
			fdsSamplePage.dropdownMenu.getByRole('menuitem')
		).toHaveText(['Approve', 'Reject']);

		await page.keyboard.press('Escape');
	});

	await test.step('Can Reject the pending task with a comment', async () => {
		await fdsSamplePage.clickItemAction('Reject');

		await fdsSamplePage.fillAndSaveWorkflowModal({
			comment: 'Rejected',
			name: 'Reject',
		});

		await expect(
			fdsSamplePage.table.itemActionButtons
		).not.toBeInViewport();
	});

	await test.step('Can resend the task for approval', async () => {
		await page.reload();

		await waitForFDS({
			container: '.fds-user-tasks',
			page,
			visualizationMode: EFDSVisualizationMode.TABLE,
		});

		await expect(
			page.locator('.fds-user-tasks table tbody tr')
		).toHaveCount(2);

		await fdsSamplePage.resubmitButton.click();

		await fdsSamplePage.fillAndSaveWorkflowModal({
			comment: 'Resend for approval',
			name: 'Resubmit',
		});
	});

	await test.step('Assign the pending task to the current user', async () => {
		await page.reload();

		await waitForFDS({
			container: '.fds-roles-tasks',
			page,
			visualizationMode: EFDSVisualizationMode.TABLE,
		});

		await fdsSamplePage.assignTaskToMe();
	});

	await test.step('Approve the pending task with a comment', async () => {
		await page.reload();

		await waitForFDS({
			container: '.fds-user-tasks',
			page,
			visualizationMode: EFDSVisualizationMode.TABLE,
		});

		await expect(
			page.locator('.fds-user-tasks table tbody tr')
		).toHaveCount(3);

		await fdsSamplePage.clickItemAction('Approve');

		await fdsSamplePage.fillAndSaveWorkflowModal({
			comment: 'Seal of Approval',
			name: 'Approve',
		});

		await expect(
			fdsSamplePage.table.itemActionButtons
		).not.toBeInViewport();
	});

	await test.step('Go to Advanced tab and check that the new FDS Sample item appears and has status "APPROVED"', async () => {
		await fdsSamplePage.selectTab('Advanced');

		await waitForFDS({
			page,
			visualizationMode: EFDSVisualizationMode.TABLE,
		});

		await fdsSamplePage.activeFiltersToolbar.clearButton.click();

		const searchInput = fdsSamplePage.managementToolbar.searchInput;

		await searchInput.fill(fdsItemTitle);

		await fdsSamplePage.managementToolbar.searchButton.click();

		await waitForFDS({
			page,
			visualizationMode: EFDSVisualizationMode.TABLE,
		});

		await expect(fdsSamplePage.table.bodyRows).toHaveCount(1);

		await expect(
			fdsSamplePage.table.bodyRows
				.first()
				.getByRole('cell', {name: 'Approved'})
		).toBeVisible();
	});
});

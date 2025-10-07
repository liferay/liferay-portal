/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import getRandomString from '../../../utils/getRandomString';
import {performLoginViaApi, userData} from '../../../utils/performLogin';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import getFormContainerDefinition from '../../layout-content-page-editor-web/main/utils/getFormContainerDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';

export const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	workflowPagesTest
);

const assignments = [];

test('Pagination of Pending Items works correctly', async ({
	apiHelpers,
	metricsPage,
	page,
	site,
}) => {
	test.slow();
	await test.step('assign the "Single Approver" workflow to Web Content Article', async () => {
		await page.goto(
			`/group${site.friendlyUrlPath}${PORTLET_URLS.workflow}`
		);

		await page.waitForLoadState('networkidle');

		await page
			.getByRole('row', {name: 'Web Content Article'})
			.getByRole('button', {name: 'Edit'})
			.click();

		await page.getByRole('combobox').selectOption('Single Approver@1');

		await page.getByRole('button', {name: 'Save'}).click();
	});

	const basicWebContentStructureId =
		await getBasicWebContentStructureId(apiHelpers);

	for (let i = 1; i <= 21; i++) {
		const webContent =
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: basicWebContentStructureId,
				groupId: site.id,
				titleMap: {en_US: `Web content ${i}`},
			});

		apiHelpers.data.push({
			id: `${site.id}_${webContent.articleId}`,
			type: 'webContent',
		});
	}
	await test.step('set web content workflow assignments to single approver', async () => {
		await metricsPage.goTo();

		await page
			.getByRole('cell', {name: 'Single Approver'})
			.getByRole('link')
			.click();

		await page
			.getByRole('link')
			.filter({hasText: 'Total Pending'})
			.first()
			.click();
	});

	await test.step('assert that the correct number of entries based on the selected entries per page option is displayed', async () => {
		await expect(
			page.getByRole('row').filter({hasText: 'Web content'})
		).toHaveCount(20);

		await page.getByLabel('Go to the next page').click();

		await expect(
			page.getByRole('row').filter({hasText: 'Web content'})
		).toHaveCount(1);
	});

	await test.step('assert that ascending Creation Date sorting is preserved when the user changes the pagination', async () => {
		await page.getByLabel('Items per Page').click();

		await page.getByRole('option').filter({hasText: '40'}).click();

		await page.getByRole('link', {name: 'Creation Date'}).dblclick();

		for (let i = 1; i <= 21; i++) {
			await expect(
				page
					.getByRole('cell', {
						exact: true,
						name: `Web Content Article: Web content ${i}`,
					})
					.last()
			).toBeVisible();
		}
	});
});

test('Can search assignees and steps in Performance by Assignee and Step views', async ({
	apiHelpers,
	metricsPage,
	page,
	performanceByAssigneePage,
	performanceByStepPage,
	processMetricsPage,
	site,
	workflowTasksPage,
}) => {
	test.slow();
	page.setViewportSize({height: 1080, width: 1920});

	await test.step('assign the "Single Approver" workflow to Web Content Article', async () => {
		await page.goto(
			`/group${site.friendlyUrlPath}${PORTLET_URLS.workflow}`
		);

		await page.waitForLoadState('networkidle');

		await page
			.getByRole('row', {name: 'Web Content Article'})
			.getByRole('button', {name: 'Edit'})
			.click();

		await page.getByRole('combobox').selectOption('Single Approver@1');

		await page.getByRole('button', {name: 'Save'}).click();
	});

	await test.step('create a new site page', async () => {
		const formId = getRandomString();

		const formDefinition = getFormContainerDefinition({
			id: formId,
		});

		const pageName = getRandomString();

		await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([formDefinition]),
			siteId: site.id,
			title: pageName,
		});
	});

	await test.step('create three users and three web contents', async () => {
		const role =
			await apiHelpers.headlessAdminUser.getRoleByName('Administrator');

		for (let i = 0; i < 3; i++) {
			const user = await apiHelpers.headlessAdminUser.postUserAccount();

			userData[user.alternateName] = {
				name: user.givenName,
				password: 'test',
				surname: user.familyName,
			};

			await apiHelpers.headlessAdminUser.assignUserToRole(
				role.externalReferenceCode,
				user.id
			);

			apiHelpers.data.push({
				id: user.id,
				type: 'userAccount',
			});

			const basicWebContentStructureId =
				await getBasicWebContentStructureId(apiHelpers);

			const webContent =
				await apiHelpers.jsonWebServicesJournal.addWebContent({
					ddmStructureId: basicWebContentStructureId,
					groupId: site.id,
					titleMap: {en_US: `Web content ${i}`},
				});

			apiHelpers.data.push({
				id: `${site.id}_${webContent.articleId}`,
				type: 'webContent',
			});

			assignments.push({user, webContent});
		}
	});

	await test.step('assign each web content to its corresponding user', async () => {
		for (const {user, webContent} of assignments) {
			await workflowTasksPage.goToAssignedToMyRoles();

			await workflowTasksPage.assignToUser(webContent.title, user);
		}
	});

	await test.step('log in as each user and approve their assigned web content task', async () => {
		for (const {user, webContent} of assignments) {
			await page.getByTitle('User Profile Menu').click();

			await page.getByRole('menuitem', {name: 'Sign Out'}).click();

			await page.waitForURL('**/home');

			await performLoginViaApi({
				page,
				screenName: user.alternateName,
			});

			await page.goto(
				`/group${site.friendlyUrlPath}${PORTLET_URLS.myWorkflowTasks}`
			);

			await workflowTasksPage.approve(webContent.title);
		}
	});

	await test.step('log back in as the test user and navigate to the View All Assignees page', async () => {
		await page.getByTitle('User Profile Menu').click();

		await page.getByRole('menuitem', {name: 'Sign Out'}).click();

		await performLoginViaApi({
			page,
			screenName: 'test',
		});

		await metricsPage.goTo();

		await workflowTasksPage.processSingleAprover.click();

		await workflowTasksPage.performanceTab.click();

		await processMetricsPage.viewAllAssigneesButton.click();
	});

	await test.step('assert that it is possible to search for an assignee in the View All Assignees page', async () => {
		await performanceByAssigneePage.searchBar.fill(
			assignments[0].user.alternateName
		);

		await page.getByRole('search').getByRole('button').click();

		await expect(
			page.getByRole('cell', {
				name: `${assignments[0].user.alternateName} ${assignments[0].user.alternateName}`,
			})
		).toBeVisible();

		await performanceByAssigneePage.searchBar.fill('something else');

		await page.getByRole('search').getByRole('button').click();

		await expect(page.getByText('No results found')).toBeVisible();
	});

	await metricsPage.goTo();

	await workflowTasksPage.processSingleAprover.click();

	await workflowTasksPage.performanceTab.click();

	await processMetricsPage.viewAllStepsButton.click();

	await test.step('assert that it is possible to seach and filter a step in the View All Steps page', async () => {
		await performanceByStepPage.searchBar.fill('Update');

		await page.getByRole('search').getByRole('button').click();

		const row = page
			.getByRole('row', {name: 'Update'})
			.filter({
				has: page.getByRole('cell', {name: '0 (0%)'}),
			})
			.filter({
				has: page.getByRole('cell', {name: '0min'}),
			});

		await expect(row).toBeVisible();

		await page.getByRole('button', {name: 'Last'}).click();

		await page.getByText('Yesterday').click();

		await expect(row).toBeVisible();
	});

	await page.locator('#backButton').getByRole('link').click();

	await test.step('assert custom date range filter displays selected dates', async () => {
		const panel = page.locator('.panel').filter({
			has: page.getByText('Performance by Step'),
		});

		await panel
			.getByRole('button', {
				name: 'Last 30 Days',
			})
			.click();

		const dropdown = page.locator('.dropdown-menu.show');

		await dropdown.getByRole('menuitem', {name: 'Custom Range'}).click();

		const customRangeForm = page.locator(
			'.dropdown-menu.dropdown-menu-inline-table.show'
		);

		await customRangeForm
			.locator('input[name="dateStart"]')
			.fill('09/22/2018');

		await customRangeForm
			.locator('input[name="dateEnd"]')
			.fill('11/11/2018');

		await customRangeForm.getByRole('button', {name: 'Apply'}).click();

		await expect(
			panel.getByRole('button', {
				name: 'Sep 22, 2018 - Nov 11, 2018',
			})
		).toBeVisible();
	});
});

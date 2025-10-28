/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {calendarPagesTest} from '../../../fixtures/calendarPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import getRandomString from '../../../utils/getRandomString';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';

export const test = mergeTests(
	apiHelpersTest,
	calendarPagesTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	pagesAdminPagesTest,
	workflowPagesTest
);

test(
	'show calendar event after workflow approval',
	{tag: '@LPD-44354'},
	async ({
		apiHelpers,
		calendarWidgetPage,
		page,
		pageEditorPage,
		site,
		workflowPage,
		workflowTasksPage,
	}) => {
		let layout: Layout;

		await test.step('Create and publish a site page with a calendar widget', async () => {
			layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					getWidgetDefinition({
						id: getRandomString(),
						widgetName:
							'com_liferay_calendar_web_portlet_CalendarPortlet',
					}),
				]),
				siteId: site.id,
				title: getRandomString(),
			});

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			await pageEditorPage.publishPage();

			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);
		});

		await test.step('Publish the first calendar event without triggering workflow', async () => {
			await calendarWidgetPage.createAndSubmitEvent({
				title: 'Calendar Event Without Workflow',
			});

			await expect(
				page.locator('.calendar-portlet-event-approved').nth(0)
			).toBeVisible();
		});

		await test.step('Enable Single Approver workflow for calendar events', async () => {
			await workflowPage.goto(site.friendlyUrlPath);

			await workflowPage.changeWorkflow(
				'Calendar Event',
				'Single Approver'
			);
		});

		await test.step('Submit the second calendar event that will require workflow approval', async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await calendarWidgetPage.createAndSubmitEvent({
				invitationUser: 'Test Test',
				title: 'Calendar Event With Workflow',
				withWorkflow: true,
			});

			await expect(
				page.locator('.calendar-portlet-event-approved')
			).toHaveCount(1);

			await expect(
				page.locator('.calendar-portlet-event-pending')
			).toHaveCount(1);
		});

		await test.step('Assign and approve the workflow task for the submitted event', async () => {
			await workflowTasksPage.goToAssignedToMyRoles();

			await workflowTasksPage.assignToMe('Calendar Event With Workflow');

			await workflowTasksPage.approve('Calendar Event With Workflow');
		});

		await test.step('Ensure the approved event is now visible in the calendar', async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await expect(
				page.locator('.calendar-portlet-event-approved')
			).toHaveCount(2);
		});
	}
);

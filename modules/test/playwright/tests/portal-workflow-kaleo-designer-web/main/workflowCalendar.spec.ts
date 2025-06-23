/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {calendarPagesTest} from '../../../fixtures/calendarPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';

export const test = mergeTests(
	calendarPagesTest,
	loginTest(),
	pagesAdminPagesTest,
	workflowPagesTest
);

let createdEvents: string[] = [];
let calendarPageCreated = false;
let workflowSet = false;

test.afterEach(
	async ({calendarWidgetPage, page, pagesAdminPage, workflowPage}) => {
		await test.step('Cleanup: delete calendar events, widget page and workflow assignment for calendar events', async () => {
			await page.goto('/');

			if (!calendarPageCreated || !createdEvents.length) {
				return;
			}

			await page.getByRole('menuitem', {name: 'Calendar Page'}).click();
			await calendarWidgetPage.deleteApprovedEvents(createdEvents);
			createdEvents = [];

			if (!calendarPageCreated) {
				return;
			}

			await pagesAdminPage.goto();
			await pagesAdminPage.deletePage('Calendar Page');
			calendarPageCreated = false;

			if (!workflowSet) {
				return;
			}

			await workflowPage.goto();
			await workflowPage.changeCalendarEventWorkflow('');
			workflowSet = false;
		});
	}
);

test(
	'show calendar event after workflow approval',
	{tag: '@LPD-44354'},
	async ({
		calendarWidgetPage,
		page,
		pagesAdminPage,
		workflowPage,
		workflowTasksPage,
	}) => {
		await test.step('Create a new widget page to host the calendar', async () => {
			await pagesAdminPage.goto();

			await pagesAdminPage.addWidgetPage({name: 'Calendar Page'});

			calendarPageCreated = true;

			await page.getByText('1 Column', {exact: true}).click();

			await page.getByRole('button', {name: 'Save'}).click();
		});

		await test.step('Add the Calendar widget to the newly created page', async () => {
			await page.goto('/');

			await page.getByRole('menuitem', {name: 'Calendar Page'}).click();

			await page.getByLabel('Add').click();

			await page
				.getByRole('textbox', {name: 'Search Form'})
				.fill('calendar');

			const addContentButton = page
				.locator('li.sidebar-body__add-panel__tab-item', {
					has: page.locator('div[title="Calendar"]'),
				})
				.locator('button[aria-label="Add Content"]');

			await addContentButton.hover();

			await addContentButton.click();
		});

		await test.step('Publish the first calendar event without triggering workflow', async () => {
			await calendarWidgetPage.createAndSubmitEvent({
				title: 'Calendar Event Without Workflow',
			});

			createdEvents.push('Calendar Event Without Workflow');

			await expect(
				page.locator('.calendar-portlet-event-approved').nth(0)
			).toBeVisible();
		});

		await test.step('Enable Single Approver workflow for calendar events', async () => {
			await workflowPage.goto();

			await workflowPage.changeCalendarEventWorkflow('Single Approver@1');

			workflowSet = true;
		});

		await test.step('Submit the second calendar event that will require workflow approval', async () => {
			await page.goto('/');

			await page.getByRole('menuitem', {name: 'Calendar Page'}).click();

			await calendarWidgetPage.createAndSubmitEvent({
				invitationUser: 'Test Test',
				title: 'Calendar Event With Workflow',
				withWorkflow: true,
			});

			createdEvents.push('Calendar Event With Workflow');

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
			await page.goto('/');

			await page.getByRole('menuitem', {name: 'Calendar Page'}).click();

			await expect(
				page.locator('.calendar-portlet-event-approved')
			).toHaveCount(2);
		});
	}
);

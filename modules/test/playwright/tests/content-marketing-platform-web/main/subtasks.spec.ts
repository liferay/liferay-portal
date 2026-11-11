/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {cmpPagesTest} from './fixtures/cmpPagesTest';

const test = mergeTests(
	cmpPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-107678': {enabled: true},
	}),
	loginTest()
);

const cmpProject = 'cmp/projects';
const cmpTask = 'cmp/tasks';

test.use({viewport: {height: 1080, width: 1920}});

test(
	'Create subtasks from the subtask editor',
	{tag: ['@LPD-114769']},
	async ({apiHelpers, editTaskPage, page}) => {
		const project = await apiHelpers.objectEntry.postObjectEntry(
			{
				title: getRandomString(),
			},
			cmpProject
		);

		const parentTask = await apiHelpers.objectEntry.postObjectEntry(
			{
				dueDate: '2026-12-11T00:00:00Z',
				r_cmpProjectToCMPTasks_c_cmpProjectId: project.id,
				state: {key: 'done'},
				title: getRandomString(),
			},
			cmpTask,
			project.scopeKey
		);

		const subtaskTitles = [getRandomString(), getRandomString()];

		await test.step('Open the subtask editor', async () => {
			await editTaskPage.gotoAddSubtask(parentTask.id);

			await expect(
				page.getByRole('heading', {name: 'New Subtask'})
			).toBeVisible();
		});

		await test.step('Dismiss the alert about the done parent task', async () => {
			const doneParentTaskAlert = page.locator('.alert-info', {
				hasText: `The parent task "${parentTask.title}" is Done. Adding this subtask will not change its status.`,
			});

			await expect(doneParentTaskAlert).toBeVisible();

			await doneParentTaskAlert.getByLabel('Close').click();

			await expect(doneParentTaskAlert).toBeHidden();
		});

		await test.step('Show an info when the due date is after the parent task due date', async () => {
			const dueDateInfo = page.getByText(
				'This date is after the parent task due date (12/11/2026).'
			);

			const dueDateInput = page.getByRole('textbox', {name: 'Due Date'});

			await dueDateInput.fill('12/20/2026');

			await expect(dueDateInfo).toBeVisible();

			await dueDateInput.fill('12/01/2026');

			await expect(dueDateInfo).toBeHidden();
		});

		await test.step('Save and add another subtask', async () => {
			await editTaskPage.titleInput.fill(subtaskTitles[0]);

			await page
				.getByRole('button', {name: 'Save and Add Another'})
				.click();

			await waitForAlert(
				page,
				`The subtask "${subtaskTitles[0]}" was successfully created.`
			);

			await expect(
				page.getByRole('heading', {name: 'New Subtask'})
			).toBeVisible();
			await expect(editTaskPage.titleInput).toBeEmpty();
		});

		await test.step('Save the subtask and go back to the parent task', async () => {
			await editTaskPage.titleInput.fill(subtaskTitles[1]);

			await page.getByRole('button', {name: 'Save New Subtask'}).click();

			await page.waitForURL(`**/web/cms/e/task/*/${parentTask.id}`);

			await page
				.locator('.alert-success')
				.getByRole('link', {name: subtaskTitles[1]})
				.click();

			await expect(
				page
					.getByRole('navigation', {name: 'Breadcrumb'})
					.getByText(subtaskTitles[1], {exact: true})
			).toBeVisible();
		});
	}
);

test(
	'Edit a subtask',
	{tag: ['@LPD-114769']},
	async ({apiHelpers, editTaskPage, page}) => {
		const project = await apiHelpers.objectEntry.postObjectEntry(
			{
				title: getRandomString(),
			},
			cmpProject
		);

		const parentTask = await apiHelpers.objectEntry.postObjectEntry(
			{
				r_cmpProjectToCMPTasks_c_cmpProjectId: project.id,
				title: getRandomString(),
			},
			cmpTask,
			project.scopeKey
		);

		const subtask = await apiHelpers.objectEntry.postObjectEntry(
			{
				r_cmpProjectToCMPTasks_c_cmpProjectId: project.id,
				r_cmpTaskToCMPTasks_c_cmpTaskId: parentTask.id,
				title: getRandomString(),
			},
			cmpTask,
			project.scopeKey
		);

		await test.step('Open the subtask editor', async () => {
			await editTaskPage.goto(subtask.id);

			await expect(
				page.getByRole('heading', {name: 'Edit Subtask'})
			).toBeVisible();
			await expect(page.locator('.alert-info')).toBeHidden();
			await expect(
				page.getByRole('button', {name: 'Save and Add Another'})
			).toBeHidden();
		});

		await test.step('Update the subtask title', async () => {
			const title = getRandomString();

			await editTaskPage.titleInput.fill(title);

			await page.getByRole('button', {name: 'Save Edit Subtask'}).click();

			await waitForAlert(page, `${title} was updated successfully.`);

			await expect(
				page
					.getByRole('navigation', {name: 'Breadcrumb'})
					.getByText(title, {exact: true})
			).toBeVisible();
		});
	}
);

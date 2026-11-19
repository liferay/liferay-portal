/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import dragAndDropElement from '../../../utils/dragAndDropElement';
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
	'Count the subtasks in the bulk delete confirmation',
	{tag: ['@LPD-114773']},
	async ({apiHelpers, page, projectPage, projectsPage, tasksPage}) => {
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

		await apiHelpers.objectEntry.postObjectEntry(
			{
				r_cmpProjectToCMPTasks_c_cmpProjectId: project.id,
				r_cmpTaskToCMPTasks_c_cmpTaskId: subtask.id,
				title: getRandomString(),
			},
			cmpTask,
			project.scopeKey
		);

		const task = await apiHelpers.objectEntry.postObjectEntry(
			{
				r_cmpProjectToCMPTasks_c_cmpProjectId: project.id,
				title: getRandomString(),
			},
			cmpTask,
			project.scopeKey
		);

		await test.step('Select a task, its subtask and another task', async () => {
			await projectsPage.goto();

			await projectsPage.getProject(project.title).click();

			await projectPage.tasksTab.click();

			for (const selectedTask of [parentTask, subtask, task]) {
				await tasksPage
					.getItem(selectedTask.title)
					.locator('input[title="Select Item"]')
					.check();
			}
		});

		await test.step('Count the selected subtask once and the unselected sub-subtask in the confirmation title', async () => {
			await tasksPage.execBulkItemAction('Delete');

			const deleteDialog = page.getByRole('dialog', {
				name: 'Delete 4 Tasks',
			});

			await expect(deleteDialog).toBeVisible();

			await deleteDialog.getByRole('button', {name: 'Cancel'}).click();

			await expect(deleteDialog).toBeHidden();
		});
	}
);

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

test(
	'Delete confirmation counts the subtasks of a task',
	{tag: ['@LPD-114770']},
	async ({
		apiHelpers,
		page,
		projectPage,
		projectsPage,
		taskPage,
		tasksPage,
	}) => {
		const project = await apiHelpers.objectEntry.postObjectEntry(
			{
				title: getRandomString(),
			},
			cmpProject
		);

		const task = await apiHelpers.objectEntry.postObjectEntry(
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
				r_cmpTaskToCMPTasks_c_cmpTaskId: task.id,
				title: getRandomString(),
			},
			cmpTask,
			project.scopeKey
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				r_cmpProjectToCMPTasks_c_cmpProjectId: project.id,
				r_cmpTaskToCMPTasks_c_cmpTaskId: subtask.id,
				title: getRandomString(),
			},
			cmpTask,
			project.scopeKey
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				r_cmpProjectToCMPTasks_c_cmpProjectId: project.id,
				r_cmpTaskToCMPTasks_c_cmpTaskId: task.id,
				status: {code: 2},
				title: getRandomString(),
			},
			cmpTask,
			project.scopeKey
		);

		const deleteConfirmationMessage = `You are about to delete the task "${task.title}" and its 2 subtasks.`;

		await test.step('Count the subtasks from the row action', async () => {
			await tasksPage.goto();

			await tasksPage.projectTasksTab.click();

			await tasksPage.execItemAction({
				action: 'Delete',
				filter: task.title,
			});

			const dialog = page
				.getByRole('alert')
				.filter({hasText: 'You are about to delete'});

			await expect(dialog).toContainText(deleteConfirmationMessage);

			await dialog.getByRole('button', {name: 'Cancel'}).click();
		});

		await test.step('Count the subtasks from the Kanban card actions', async () => {
			await projectsPage.goto();

			await projectsPage.getProject(project.title).click();

			await projectPage.tasksTab.click();

			await tasksPage.tableViewButton.click();

			await tasksPage.kanbanView.viewOption.click();

			await clickAndExpectToBeVisible({
				target: page.getByRole('menuitem', {name: 'Delete'}),
				trigger: page
					.locator('.lfr__kaban-task-card', {hasText: task.title})
					.getByLabel('Actions'),
			});

			await page.getByRole('menuitem', {name: 'Delete'}).click();

			const dialog = page
				.getByRole('dialog')
				.filter({hasText: 'You are about to delete'});

			await expect(dialog).toContainText(deleteConfirmationMessage);

			await dialog.getByRole('button', {name: 'Cancel'}).click();
		});

		await test.step('Count the subtasks from the task page menu', async () => {
			await taskPage.goto(task.id);

			await taskPage.openMoreActions();

			await taskPage.deleteMenuItem.click();

			const dialog = page.getByRole('alertdialog');

			await expect(dialog).toContainText(deleteConfirmationMessage);

			await dialog.getByRole('button', {name: 'Cancel'}).click();
		});
	}
);

test(
	'Informs about open subtasks when a task is set to Done',
	{tag: ['@LPD-114770']},
	async ({
		apiHelpers,
		page,
		projectPage,
		projectsPage,
		taskPage,
		tasksPage,
	}) => {
		const project = await apiHelpers.objectEntry.postObjectEntry(
			{
				title: getRandomString(),
			},
			cmpProject
		);

		const tasks = [];

		for (const subtaskState of ['notStarted', 'done', 'notStarted']) {
			const task = await apiHelpers.objectEntry.postObjectEntry(
				{
					r_cmpProjectToCMPTasks_c_cmpProjectId: project.id,
					state: {key: 'inProgress'},
					title: getRandomString(),
				},
				cmpTask,
				project.scopeKey
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					r_cmpProjectToCMPTasks_c_cmpProjectId: project.id,
					r_cmpTaskToCMPTasks_c_cmpTaskId: task.id,
					state: {key: subtaskState},
					title: getRandomString(),
				},
				cmpTask,
				project.scopeKey
			);

			tasks.push(task);
		}

		await test.step('Inform from the Info panel', async () => {
			await taskPage.goto(tasks[0].id);

			await page.locator('.lfr-cmp__state-selector').click();

			await page.getByRole('option', {name: 'Done'}).click();

			await waitForAlert(
				page,
				'Task was marked as done. 1 subtask is not done yet.',
				{type: 'info'}
			);
		});

		await test.step('Do not inform when every subtask is done', async () => {
			await taskPage.goto(tasks[1].id);

			await page.locator('.lfr-cmp__state-selector').click();

			await page.getByRole('option', {name: 'Done'}).click();

			await waitForAlert(page, 'State was successfully updated.');

			await expect(page.locator('.alert-info')).toBeHidden();
		});

		await test.step('Inform from Kanban', async () => {
			await projectsPage.goto();

			await projectsPage.getProject(project.title).click();

			await projectPage.tasksTab.click();

			await tasksPage.tableViewButton.click();

			await tasksPage.kanbanView.viewOption.click();

			await dragAndDropElement({
				dragTarget: tasksPage
					.getKanbanColumn('In Progress')
					.getByText(tasks[2].title, {exact: true}),
				dropTarget: tasksPage.getKanbanColumn('Done'),
			});

			await waitForAlert(
				page,
				'Task was marked as done. 1 subtask is not done yet.',
				{type: 'info'}
			);
		});
	}
);

test(
	'Informs once about open subtasks after a bulk Update State to Done',
	{tag: ['@LPD-114770']},
	async ({apiHelpers, page, tasksPage}) => {
		const project = await apiHelpers.objectEntry.postObjectEntry(
			{
				title: getRandomString(),
			},
			cmpProject
		);

		const tasks = [];

		for (let i = 0; i < 2; i++) {
			const task = await apiHelpers.objectEntry.postObjectEntry(
				{
					r_cmpProjectToCMPTasks_c_cmpProjectId: project.id,
					state: {key: 'inProgress'},
					title: getRandomString(),
				},
				cmpTask,
				project.scopeKey
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					r_cmpProjectToCMPTasks_c_cmpProjectId: project.id,
					r_cmpTaskToCMPTasks_c_cmpTaskId: task.id,
					title: getRandomString(),
				},
				cmpTask,
				project.scopeKey
			);

			tasks.push(task);
		}

		await tasksPage.goto();

		await tasksPage.projectTasksTab.click();

		for (const task of tasks) {
			await tasksPage
				.getItem(task.title)
				.locator('input[title="Select Item"]')
				.check();
		}

		await tasksPage.execBulkItemAction('Update State');

		await tasksPage.updateStateSelector.click();

		await page.getByRole('option', {name: 'Done'}).click();

		await tasksPage.saveButton.click();

		await waitForAlert(
			page,
			'Task was marked as done. 2 subtasks are not done yet.',
			{
				timeout: 30000,
				type: 'info',
			}
		);
	}
);

test(
	'Filter the project tasks by tasks or subtasks',
	{tag: ['@LPD-114772']},
	async ({apiHelpers, page, projectPage, projectsPage, tasksPage}) => {
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

		await projectsPage.goto();

		await projectsPage.getProject(project.title).click();

		await projectPage.tasksTab.click();

		await expect(tasksPage.getItem(parentTask.title)).toBeVisible();
		await expect(tasksPage.getItem(subtask.title)).toBeVisible();

		await test.step('Show only the subtasks', async () => {
			await page.getByRole('button', {name: 'Filter'}).click();

			await page.getByRole('menuitem', {name: 'Type'}).click();

			await page
				.getByRole('radio', {exact: true, name: 'Subtasks'})
				.check();

			await page.getByRole('button', {name: 'Add Filter'}).click();

			await expect(tasksPage.getItem(subtask.title)).toBeVisible();
			await expect(tasksPage.getItem(parentTask.title)).toBeHidden();
		});

		await test.step('Show only the tasks', async () => {
			await page.getByRole('button', {name: /^Type/}).click();

			await page.getByRole('radio', {exact: true, name: 'Tasks'}).check();

			await page.getByRole('button', {name: 'Show Results'}).click();

			await expect(tasksPage.getItem(parentTask.title)).toBeVisible();
			await expect(tasksPage.getItem(subtask.title)).toBeHidden();
		});
	}
);

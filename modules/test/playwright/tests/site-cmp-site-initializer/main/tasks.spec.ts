/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import {addSpaceUser} from '../../../utils/addSpaceUser';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {cmsPagesTest} from '../../site-cms-site-initializer/main/fixtures/cmsPagesTest';
import {cmpPagesTest} from './fixtures/cmpPagesTest';
import {toDateString} from './utils/toDateString';

const test = mergeTests(
	cmpPagesTest,
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-58677': {enabled: true},
		'LPD-69885': {enabled: true},
	}),
	globalMenuPagesTest,
	loginTest(),
	workflowPagesTest
);

const cmpProject = 'cmp/projects';
const cmpTask = 'cmp/tasks';
let project;
const tasks = [];
let taskNames: string[] = [];
let taskTags: string[] = [];

const generateTaskTag = () =>
	'L_CMP_TASK_' + Math.floor(Math.random() * 100000000);

/**
 * Formats a date as its long month name and year.
 * For example: getMonthYearLabel(new Date(2026, 5, 15)) // "June 2026"
 */
const getMonthYearLabel = (date: Date): string =>
	date.toLocaleDateString('en-US', {month: 'long', year: 'numeric'});

test.beforeEach(async ({apiHelpers}) => {
	taskNames = [getRandomString(), getRandomString(), getRandomString()];
	taskTags = [];

	project = await apiHelpers.objectEntry.postObjectEntry(
		{
			title: getRandomString(),
		},
		cmpProject
	);

	for (const taskName of taskNames) {
		const taskTag = generateTaskTag();

		taskTags.push(taskTag);

		const task = await apiHelpers.objectEntry.postObjectEntry(
			{
				keywords: [taskTag],
				r_cmpProjectToCMPTasks_c_cmpProjectId: project.id,
				title: taskName,
			},
			cmpTask,
			project.scopeKey
		);
		tasks.push(task);
	}
});

test.afterEach(async ({apiHelpers}) => {
	if (project) {
		await apiHelpers.objectEntry.deleteObjectEntry(
			cmpProject,
			String(project.id)
		);
	}

	const bulkActionTasks = 'cms/bulk-action-tasks';

	const bulkTasks =
		await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
			bulkActionTasks
		);

	for (const bulkTask of bulkTasks.items) {
		await apiHelpers.objectEntry.deleteObjectEntry(
			bulkActionTasks,
			bulkTask.id
		);
	}
});

test('Bulk delete tasks', {tag: ['@LPD-75299']}, async ({page, tasksPage}) => {
	await test.step('Select 2 task and delete them using the Bulk Action', async () => {
		await tasksPage.goto();

		await tasksPage.projectTasksTab.click();

		await tasksPage
			.getItem(taskNames[0])
			.locator('input[title="Select Item"]')
			.check();
		await tasksPage
			.getItem(taskNames[1])
			.locator('input[title="Select Item"]')
			.check();

		await tasksPage.execBulkItemAction('Delete');

		await tasksPage.dialogDeleteButton.click();

		await waitForAlert(page, 'Info:Delete action started for 2 tasks.', {
			autoClose: true,
			type: 'info',
		});

		await expect(async () => {
			await tasksPage.goto();

			await expect(tasksPage.getItem(taskNames[0])).toBeHidden();
			await expect(tasksPage.getItem(taskNames[1])).toBeHidden();
			await expect(tasksPage.getItem(taskNames[2])).toBeVisible();
		}).toPass({timeout: 10000});
	});
});

test(
	'Bulk update the assignee of an task',
	{tag: ['@LPD-75299']},
	async ({page, tasksPage}) => {
		await test.step('Select 2 task and update its assignee using the Bulk Action', async () => {
			await tasksPage.goto();

			await tasksPage.projectTasksTab.click();

			await tasksPage
				.getItem(taskNames[0])
				.locator('input[title="Select Item"]')
				.check();
			await tasksPage
				.getItem(taskNames[1])
				.locator('input[title="Select Item"]')
				.check();

			await tasksPage.execBulkItemAction('Assign to...');

			await expect(tasksPage.assignTaskToDialog).toBeVisible();

			await page
				.getByPlaceholder('Unassigned')
				.fill('Asset Library Content Reviewer');

			await page
				.getByRole('option', {
					name: 'Asset Library Content Reviewer',
				})
				.click();

			await tasksPage.saveButton.click();

			await expect(async () => {
				await tasksPage.goto();

				await expect(
					page.getByRole('row', {
						name: 'Asset Library Content Reviewer',
					})
				).toHaveCount(2, {timeout: 1000});
			}).toPass({timeout: 10000});
		});
	}
);

test(
	'Bulk update the due date of an task',
	{tag: ['@LPD-75299']},
	async ({page, tasksPage}) => {
		await test.step('Select 2 task and update its due date using the Bulk Action', async () => {
			await tasksPage.goto();

			await tasksPage.projectTasksTab.click();

			await tasksPage
				.getItem(taskNames[0])
				.locator('input[title="Select Item"]')
				.check();
			await tasksPage
				.getItem(taskNames[1])
				.locator('input[title="Select Item"]')
				.check();

			await tasksPage.execBulkItemAction('Update Due Date');

			await expect(tasksPage.updateDueDateDialog).toBeVisible();

			const locale = await page.evaluate(() => {
				return Liferay.ThemeDisplay.getBCP47LanguageId();
			});

			const tomorrow = new Date();

			tomorrow.setDate(tomorrow.getDate() + 1);

			const dateString = tomorrow.toLocaleDateString(locale, {
				day: '2-digit',
				month: '2-digit',
				year: 'numeric',
			});

			await page.getByPlaceholder('MM/DD/YYYY').fill(dateString);

			await tasksPage.saveButton.click();

			await expect(async () => {
				await tasksPage.goto();

				const expectedDate = tomorrow.toLocaleDateString(locale, {
					day: 'numeric',
					month: 'short',
					year: 'numeric',
				});

				await expect(
					page.getByRole('row', {name: expectedDate})
				).toHaveCount(2);
			}).toPass({timeout: 10000});
		});
	}
);

test(
	'Bulk update the state of an task',
	{tag: ['@LPD-75299']},
	async ({page, tasksPage}) => {
		await test.step('Select 2 task and update its state using the Bulk Action', async () => {
			await tasksPage.goto();

			await tasksPage.projectTasksTab.click();

			await tasksPage
				.getItem(taskNames[0])
				.locator('input[title="Select Item"]')
				.check();
			await tasksPage
				.getItem(taskNames[1])
				.locator('input[title="Select Item"]')
				.check();

			await tasksPage.execBulkItemAction('Update State');

			await expect(tasksPage.updateStateDialog).toBeVisible();

			await tasksPage.updateStateSelector.click();

			await page.getByRole('option', {name: 'Blocked'}).click();

			await tasksPage.saveButton.click();

			await expect(async () => {
				await tasksPage.goto();

				await expect(
					page.getByRole('row', {name: 'Blocked'})
				).toHaveCount(2);
			}).toPass({timeout: 10000});
		});
	}
);

test(
	'Calendar view can drag tasks to update their due dates',
	{tag: ['@LPD-69885', '@LPD-93269']},
	async ({apiHelpers, page, projectPage, projectsPage, tasksPage}) => {
		const taskTitle = getRandomString();

		const rescheduledDate = new Date();

		rescheduledDate.setDate(18);

		const scheduledDate = new Date();

		scheduledDate.setDate(15);

		const unscheduledTaskDate = new Date();

		unscheduledTaskDate.setDate(20);

		// FullCalendar tracks the mouse itself rather than using native drag
		// events, so simulate the drag with manual mouse actions instead of
		// Playwright's dragTo.

		const dragToDayCell = async (source: Locator, dayCell: Locator) => {
			const getCenter = async (locator: Locator) => {
				const box = await locator.boundingBox();

				if (!box) {
					throw new Error('The dragged element is not visible');
				}

				return {x: box.x + box.width / 2, y: box.y + box.height / 2};
			};

			const sourceCenter = await getCenter(source);

			await source.hover();

			await page.mouse.down();

			// FullCalendar starts the drag only after the pointer travels a few
			// pixels, so nudge it near the source before crossing over to the
			// target.

			await page.mouse.move(sourceCenter.x + 10, sourceCenter.y + 10, {
				steps: 5,
			});

			const dayCellCenter = await getCenter(dayCell);

			await page.mouse.move(dayCellCenter.x, dayCellCenter.y, {
				steps: 10,
			});

			await page.mouse.up();
		};

		const {calendarView} = tasksPage;

		await test.step('Create a task with a due date', async () => {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					dueDate: `${toDateString(scheduledDate)}T00:00:00Z`,
					r_cmpProjectToCMPTasks_c_cmpProjectId: project.id,
					title: taskTitle,
				},
				cmpTask,
				project.scopeKey
			);
		});

		await test.step('View the project and open its calendar view', async () => {
			await projectsPage.goto();

			await projectsPage.getProject(project.title).click();

			await projectPage.tasksTab.click();

			await tasksPage.tableViewButton.click();

			await calendarView.viewOption.click();

			await expect(calendarView.title).toBeVisible();
		});

		await test.step('Dragging the task to another day updates its due date', async () => {
			const sourceCell = tasksPage.getCalendarDayCell(scheduledDate);
			const targetCell = tasksPage.getCalendarDayCell(rescheduledDate);

			await dragToDayCell(
				sourceCell.getByText(taskTitle, {exact: true}),
				targetCell
			);

			await waitForAlert(
				page,
				`${taskTitle} due date was successfully updated.`
			);

			await expect(
				targetCell.getByText(taskTitle, {exact: true})
			).toBeVisible();

			await expect(
				sourceCell.getByText(taskTitle, {exact: true})
			).toBeHidden();
		});

		await test.step('Dragging an unscheduled task into the calendar schedules it', async () => {
			await expect(calendarView.unscheduledTasksButton).toContainText(
				'3 Unscheduled Tasks'
			);

			await clickAndExpectToBeVisible({
				target: calendarView.unscheduledTasksPanel,
				trigger: calendarView.unscheduledTasksButton,
			});

			await dragToDayCell(
				calendarView.unscheduledTasksPanel.getByText(taskNames[0], {
					exact: true,
				}),
				tasksPage.getCalendarDayCell(unscheduledTaskDate)
			);

			await waitForAlert(
				page,
				`${taskNames[0]} due date was successfully updated.`
			);

			await expect(calendarView.unscheduledTasksPanel).toBeVisible();

			await expect(
				calendarView.unscheduledTasksPanel.getByText(taskNames[0], {
					exact: true,
				})
			).toBeHidden();

			await expect(calendarView.unscheduledTasksButton).toContainText(
				'2 Unscheduled Tasks'
			);
		});

		await test.step('The new due dates persist after the page reloads', async () => {
			await page.reload();

			await expect(calendarView.title).toBeVisible();

			await expect(
				tasksPage
					.getCalendarDayCell(rescheduledDate)
					.getByText(taskTitle, {exact: true})
			).toBeVisible();

			await expect(
				tasksPage
					.getCalendarDayCell(unscheduledTaskDate)
					.getByText(taskNames[0], {exact: true})
			).toBeVisible();

			await expect(calendarView.unscheduledTasksButton).toContainText(
				'2 Unscheduled Tasks'
			);
		});
	}
);

test(
	'Calendar view properly displays tasks',
	{tag: ['@LPD-69885', '@LPD-96021']},
	async ({apiHelpers, page, projectPage, projectsPage, tasksPage}) => {
		const now = new Date();

		const currentLabel = getMonthYearLabel(now);
		const nextLabel = getMonthYearLabel(
			new Date(now.getFullYear(), now.getMonth() + 1, 1)
		);
		const previousLabel = getMonthYearLabel(
			new Date(now.getFullYear(), now.getMonth() - 1, 1)
		);
		const todayDate = toDateString(now);

		const tomorrow = new Date();

		tomorrow.setDate(tomorrow.getDate() + 1);

		const dueDate = toDateString(tomorrow);

		const taskTitleBase = getRandomString();

		const taskTitles = Array.from(
			{length: 3},
			(_, index) => `${taskTitleBase}-${index}`
		);

		const unscheduledTaskTitle = `${taskTitleBase}-unscheduled`;

		await test.step('Create three tasks on the same due date and one without a due date', async () => {
			for (const title of taskTitles) {
				await apiHelpers.objectEntry.postObjectEntry(
					{
						dueDate: `${dueDate}T00:00:00Z`,
						r_cmpProjectToCMPTasks_c_cmpProjectId: project.id,
						title,
					},
					cmpTask,
					project.scopeKey
				);
			}

			await apiHelpers.objectEntry.postObjectEntry(
				{
					r_cmpProjectToCMPTasks_c_cmpProjectId: project.id,
					title: unscheduledTaskTitle,
				},
				cmpTask,
				project.scopeKey
			);
		});

		const {calendarView} = tasksPage;

		await test.step('View the project and open its Tasks tab', async () => {
			await projectsPage.goto();

			await projectsPage.getProject(project.title).click();

			await projectPage.tasksTab.click();
		});

		await test.step('Calendar view is available and can be selected', async () => {
			await tasksPage.tableViewButton.click();

			await expect(calendarView.viewOption).toBeVisible();

			await calendarView.viewOption.click();
		});

		await test.step('Calendar shows the current month and year', async () => {
			await expect(calendarView.title).toBeVisible();

			await expect(calendarView.title).toContainText(currentLabel);
		});

		await test.step('Next and previous buttons change the title', async () => {
			await calendarView.nextMonthButton.click();

			await expect(calendarView.title).toContainText(nextLabel);

			await calendarView.previousMonthButton.click();
			await calendarView.previousMonthButton.click();

			await expect(calendarView.title).toContainText(previousLabel);
		});

		await test.step('Date picker jumps the calendar to the selected month', async () => {
			await calendarView.title.click();

			await expect(calendarView.datePickerMenu).toBeVisible();

			await calendarView.datePickerMenu
				.getByText('15', {exact: true})
				.click();

			await expect(calendarView.title).toContainText(currentLabel);
		});

		await test.step('Today button returns to the current month', async () => {
			await calendarView.previousMonthButton.click();

			await expect(calendarView.title).toContainText(previousLabel);

			await calendarView.todayButton.click();

			await expect(calendarView.title).toContainText(currentLabel);
		});

		await test.step('The current date is highlighted', async () => {
			await expect(page.locator('.fc-day-today')).toBeVisible();

			await expect(page.locator('.fc-day-today')).toHaveAttribute(
				'data-date',
				todayDate
			);
		});

		await test.step('The tasks appear on their due date', async () => {
			await expect(
				tasksPage
					.getCalendarDayCell(tomorrow)
					.getByText(taskTitles[0], {exact: true})
			).toBeVisible();
		});

		await test.step('Clicking a task opens its view page', async () => {
			await tasksPage
				.getCalendarDayCell(tomorrow)
				.getByText(taskTitles[0], {exact: true})
				.click();

			await expect(page).toHaveURL(/\/e\/task\//);

			await expect(
				page.getByText(taskTitles[0], {exact: true})
			).toBeVisible();

			await page.goBack();

			await expect(calendarView.title).toBeVisible();
		});

		await test.step('A More link reveals the tasks hidden in a dense day', async () => {
			const dayCell = tasksPage.getCalendarDayCell(tomorrow);

			await expect(
				dayCell.getByText(taskTitles[1], {exact: true})
			).toBeHidden();
			await expect(
				dayCell.getByText(taskTitles[2], {exact: true})
			).toBeHidden();

			await expect(calendarView.moreLinkButton).toBeVisible();

			await test.step('Check that the custom popover opens', async () => {
				await clickAndExpectToBeVisible({
					target: calendarView.moreLinkPopover,
					trigger: calendarView.moreLinkButton,
				});
			});

			await test.step('Check that the default FullCalendar popover does not open', async () => {
				await expect(page.locator('.fc-popover')).toBeHidden();
			});

			await expect(
				calendarView.moreLinkPopover.getByText(taskTitles[1], {
					exact: true,
				})
			).toBeVisible();
			await expect(
				calendarView.moreLinkPopover.getByText(taskTitles[2], {
					exact: true,
				})
			).toBeVisible();
		});

		await test.step('Clicking a task in the more popover opens its view page', async () => {
			await calendarView.moreLinkPopover
				.getByText(taskTitles[1], {exact: true})
				.click();

			await expect(page).toHaveURL(/\/e\/task\//);

			await expect(
				page.getByText(taskTitles[1], {exact: true})
			).toBeVisible();

			await page.goBack();

			await expect(calendarView.title).toBeVisible();
		});

		await test.step('The unscheduled tasks button shows the count', async () => {
			await expect(calendarView.unscheduledTasksButton).toBeVisible();

			await expect(calendarView.unscheduledTasksButton).toContainText(
				/\d+ Unscheduled Tasks?/
			);
		});

		await test.step('Opening the panel lists every unscheduled task', async () => {
			await clickAndExpectToBeVisible({
				target: calendarView.unscheduledTasksPanel,
				trigger: calendarView.unscheduledTasksButton,
			});

			for (const taskName of [...taskNames, unscheduledTaskTitle]) {
				await expect(
					calendarView.unscheduledTasksPanel.getByText(taskName, {
						exact: true,
					})
				).toBeVisible();
			}
		});

		await test.step('Switching to another view hides the panel', async () => {
			await tasksPage.viewSelectorButton.click();

			await tasksPage.dropdownTableViewButton.click();

			await expect(calendarView.unscheduledTasksPanel).toBeHidden();
		});
	}
);

test(
	'Calendar view switches between day, week, and month views',
	{tag: ['@LPD-69885', '@LPD-94174']},
	async ({page, projectPage, projectsPage, tasksPage}) => {
		const {calendarView} = tasksPage;

		await test.step('View the project and open its calendar view', async () => {
			await projectsPage.goto();

			await projectsPage.getProject(project.title).click();

			await projectPage.tasksTab.click();

			await tasksPage.tableViewButton.click();

			await calendarView.viewOption.click();

			await expect(calendarView.title).toBeVisible();
		});

		// The FDS view selector keeps focus after selecting Calendar and its
		// tooltip overlaps the view switcher, so blur it before clicking.

		await page.evaluate(() =>
			(document.activeElement as HTMLElement)?.blur()
		);

		await test.step('Switch to the week view', async () => {
			await calendarView.weekViewButton.click();

			await expect(page.locator('.fc-dayGridWeek-view')).toBeVisible();

			await expect(calendarView.weekViewButton).toHaveAttribute(
				'aria-pressed',
				'true'
			);
		});

		await test.step('Switch to the day view', async () => {
			await calendarView.dayViewButton.click();

			await expect(page.locator('.fc-dayGridDay-view')).toBeVisible();

			await expect(calendarView.dayViewButton).toHaveAttribute(
				'aria-pressed',
				'true'
			);
		});

		await test.step('Switch back to the month view', async () => {
			await calendarView.monthViewButton.click();

			await expect(page.locator('.fc-dayGridMonth-view')).toBeVisible();

			await expect(calendarView.monthViewButton).toHaveAttribute(
				'aria-pressed',
				'true'
			);
		});
	}
);

test(
	"Calendar's task actions are displayed",
	{tag: ['@LPD-69885', '@LPD-96185']},
	async ({apiHelpers, page, projectPage, projectsPage, tasksPage}) => {
		const scheduledTaskTitle = getRandomString();

		const targetDate = new Date();

		targetDate.setDate(15);

		const dueDate = toDateString(targetDate);

		const {calendarView} = tasksPage;

		const dayCell = tasksPage.getCalendarDayCell(targetDate);

		const kebab = dayCell.getByLabel('Actions');

		const taskEvent = dayCell.getByText(scheduledTaskTitle);

		await test.step('Create a task with a due date', async () => {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					dueDate: `${dueDate}T00:00:00Z`,
					r_cmpProjectToCMPTasks_c_cmpProjectId: project.id,
					title: scheduledTaskTitle,
				},
				cmpTask,
				project.scopeKey
			);
		});

		await test.step('View the project and open its calendar view', async () => {
			await projectsPage.goto();

			await projectsPage.getProject(project.title).click();

			await projectPage.tasksTab.click();

			await tasksPage.tableViewButton.click();

			await calendarView.viewOption.click();

			await expect(calendarView.title).toBeVisible();
		});

		await test.step('Open the kebab and run an action without navigating away', async () => {
			await taskEvent.hover();

			await kebab.click();

			await expect(page).not.toHaveURL(/\/e\/task\//);

			await page.getByRole('menuitem', {name: 'Watch Task'}).click();

			await expect(page).not.toHaveURL(/\/e\/task\//);
		});

		await test.step('Check the unscheduled panel tasks display a kebab and clicking it shows the actions', async () => {
			await clickAndExpectToBeVisible({
				target: calendarView.unscheduledTasksPanel,
				trigger: calendarView.unscheduledTasksButton,
			});

			await clickAndExpectToBeVisible({
				target: page.getByRole('menuitem', {name: 'Delete'}),
				trigger: calendarView.unscheduledTasksPanel
					.getByLabel('Actions')
					.first(),
			});
		});
	}
);

test(
	'Create a task from the calendar by clicking a day',
	{tag: ['@LPD-93258', '@LPD-97621']},
	async ({page, projectPage, projectsPage, tasksPage}) => {
		const taskTitle = getRandomString();

		const targetDate = new Date();

		targetDate.setDate(15);

		const dayCell = tasksPage.getCalendarDayCell(targetDate);

		await test.step('View the project and open its calendar view', async () => {
			await projectsPage.goto();

			await projectsPage.getProject(project.title).click();

			await projectPage.tasksTab.click();

			await tasksPage.tableViewButton.click();

			await tasksPage.calendarView.viewOption.click();

			await expect(tasksPage.calendarView.title).toBeVisible();
		});

		await test.step('Click the add task button to open the create task modal', async () => {
			const addTaskButton = dayCell.getByLabel('Add Task');

			await dayCell.hover();

			await clickAndExpectToBeVisible({
				target: tasksPage.titleInput,
				trigger: addTaskButton,
			});
		});

		await test.step('The clicked day is pre-filled as the due date', async () => {
			const locale = await page.evaluate(() =>
				Liferay.ThemeDisplay.getBCP47LanguageId()
			);

			const expectedDueDate = targetDate.toLocaleDateString(locale, {
				day: '2-digit',
				month: '2-digit',
				year: 'numeric',
			});

			await expect(page.getByLabel('Due Date')).toHaveValue(
				expectedDueDate
			);
		});

		await test.step('Fill in the title and save', async () => {
			await tasksPage.titleInput.fill(taskTitle);

			await tasksPage.saveButton.click();
		});

		await test.step('The new task appears on the clicked day', async () => {
			await expect(
				dayCell.getByText(taskTitle, {exact: true})
			).toBeVisible();
		});

		await test.step('Clicking a day slot opens the create task modal with that day pre-filled', async () => {
			const daySlotDate = new Date();

			daySlotDate.setDate(10);

			await clickAndExpectToBeVisible({
				target: tasksPage.titleInput,
				trigger: tasksPage.getCalendarDayCell(daySlotDate),
			});

			const locale = await page.evaluate(() =>
				Liferay.ThemeDisplay.getBCP47LanguageId()
			);

			await expect(page.getByLabel('Due Date')).toHaveValue(
				daySlotDate.toLocaleDateString(locale, {
					day: '2-digit',
					month: '2-digit',
					year: 'numeric',
				})
			);

			await page.getByRole('button', {name: 'Cancel'}).click();

			await expect(tasksPage.titleInput).toBeHidden();
		});

		await test.step('Saving from a day slot keeps the calendar on the navigated month', async () => {
			const nextMonthTaskTitle = getRandomString();

			const nextMonthDate = new Date();

			nextMonthDate.setDate(15);
			nextMonthDate.setMonth(nextMonthDate.getMonth() + 1);

			await tasksPage.calendarView.nextMonthButton.click();

			const nextMonthDayCell =
				tasksPage.getCalendarDayCell(nextMonthDate);

			await clickAndExpectToBeVisible({
				target: tasksPage.titleInput,
				trigger: nextMonthDayCell,
			});

			await tasksPage.titleInput.fill(nextMonthTaskTitle);

			await tasksPage.saveButton.click();

			await expect(
				nextMonthDayCell.getByText(nextMonthTaskTitle, {exact: true})
			).toBeVisible();
		});
	}
);

test(
	'Day view navigates to the previous day, next day, and today',
	{tag: ['@LPD-69885', '@LPD-94175']},
	async ({apiHelpers, page, projectPage, projectsPage, tasksPage}) => {
		const {calendarView} = tasksPage;

		const todayDate = toDateString(new Date());

		const taskTitle = getRandomString();

		await apiHelpers.objectEntry.postObjectEntry(
			{
				dueDate: `${todayDate}T00:00:00Z`,
				r_cmpProjectToCMPTasks_c_cmpProjectId: project.id,
				title: taskTitle,
			},
			cmpTask,
			project.scopeKey
		);

		await tasksPage.openProjectDayView(
			projectsPage,
			projectPage,
			project.title
		);

		const todayTask = page
			.locator(`[data-date="${todayDate}"]`)
			.getByText(taskTitle, {exact: true});

		await expect(todayTask).toBeVisible();

		await calendarView.nextDayButton.click();

		await expect(todayTask).toBeHidden();

		await calendarView.previousDayButton.click();

		await expect(todayTask).toBeVisible();

		await calendarView.nextDayButton.click();

		await expect(todayTask).toBeHidden();

		await calendarView.todayButton.click();

		await expect(todayTask).toBeVisible();
	}
);

test(
	'Day view shows a task on its due date with the expanded card',
	{tag: ['@LPD-69885', '@LPD-94175']},
	async ({apiHelpers, page, projectPage, projectsPage, tasksPage}) => {
		const todayDate = toDateString(new Date());

		const taskTitle = getRandomString();

		await apiHelpers.objectEntry.postObjectEntry(
			{
				dueDate: `${todayDate}T00:00:00Z`,
				r_cmpProjectToCMPTasks_c_cmpProjectId: project.id,
				title: taskTitle,
			},
			cmpTask,
			project.scopeKey
		);

		await tasksPage.openProjectDayView(
			projectsPage,
			projectPage,
			project.title
		);

		const todayCell = page.locator(`[data-date="${todayDate}"]`);

		await expect(
			todayCell.getByText(taskTitle, {exact: true})
		).toBeVisible();

		await expect(
			todayCell.getByText('Not Started', {exact: true}).first()
		).toBeVisible();
	}
);

test(
	'Ensure that the "All Tasks" tab disables highlighted bulk actions when project and workflow tasks are selected together',
	{tag: ['@LPD-88846']},
	async ({apiHelpers, assignWorkflowToAssetType, page, tasksPage}) => {
		await assignWorkflowToAssetType('Single Approver', 'Blog');

		const blogTitle = getRandomString();

		await apiHelpers.objectEntry.postObjectEntry(
			{
				keywords: [taskTags[0]],
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: blogTitle,
			},
			'cms/blogs',
			'Default'
		);

		await tasksPage.goto();

		await tasksPage.allTasksTab.click();

		await tasksPage.getItem(taskNames[0]).getByLabel('Select Item').check();

		await tasksPage.getItem(blogTitle).getByLabel('Select Item').check();

		await expect(
			page.getByRole('button', {name: 'Update Due Date'})
		).toBeDisabled();
		await expect(
			page.getByRole('button', {name: 'Assign to...'})
		).toBeDisabled();
		await expect(
			page.getByRole('button', {name: 'Update State'})
		).toBeDisabled();
		await expect(page.getByRole('button', {name: 'Delete'})).toBeDisabled();
	}
);

test(
	'Kanban View Task creation generates a tag',
	{tag: ['@LPD-80545']},
	async ({apiHelpers, page, tasksPage}) => {
		const cmpProjectApplicationName = 'cmp/projects';
		const cmpTaskApplicationName = 'cmp/tasks';

		const project = await apiHelpers.objectEntry.postObjectEntry(
			{
				title: getRandomString(),
			},
			cmpProjectApplicationName
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				r_cmpProjectToCMPTasks_c_cmpProjectId: project.id,
				title: getRandomString(),
			},
			cmpTaskApplicationName,
			project.scopeKey
		);

		const taskTitle = getRandomString();

		await test.step('Go to tasks page and switch to kanban view', async () => {
			await tasksPage.goto();

			await tasksPage.projectTasksTab.click();

			await tasksPage.tableViewButton.click();

			await tasksPage.dropdownKanbanViewButton.click();
		});

		await test.step('Add a new task', async () => {
			await tasksPage.addTaskKanbanButton.click();

			await tasksPage.titleInput.fill(taskTitle);

			await tasksPage.projectTitleButton.click();

			await page.getByRole('option', {name: project.title}).click();

			await tasksPage.saveButton.click();
		});

		await test.step('Go to tasks page and select the created task', async () => {
			await tasksPage.kanbanViewButton.click();

			await tasksPage.dropdownTableViewButton.click();

			await page.getByRole('link', {name: taskTitle}).click();
		});

		await test.step("Check if the created task's AssetTagName follows the pattern", async () => {
			await expect(tasksPage.assetTagNameField).toContainText(
				'L_CMP_TASK_'
			);
		});
	}
);

test(
	'Verify task visibility across Global Tasks tabs based on user permission',
	{tag: ['@LPD-88846']},
	async ({apiHelpers, assignWorkflowToAssetType, page, tasksPage}) => {
		await assignWorkflowToAssetType('Single Approver', 'Blog');

		const spaces =
			await apiHelpers.headlessAssetLibrary.getAssetLibrariesPage();

		const defaultSpace = spaces.find((space) => space.name === 'Default');

		const user = await addSpaceUser(
			apiHelpers,
			defaultSpace.externalReferenceCode,
			'Asset Library Administrator'
		);

		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
			project.systemProperties.scope.externalReferenceCode,
			user.externalReferenceCode
		);

		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccountRoles(
			project.systemProperties.scope.externalReferenceCode,
			user.externalReferenceCode,
			['Asset Library Administrator']
		);

		const assignedBlogTitle = getRandomString();
		const unassignedBlogTitle = getRandomString();

		await test.step('Create two CMS Blog entries; both generate KaleoTaskInstanceTokens', async () => {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					keywords: [taskTags[0]],
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: assignedBlogTitle,
				},
				'cms/blogs',
				'Default'
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					keywords: [taskTags[0]],
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: unassignedBlogTitle,
				},
				'cms/blogs',
				'Default'
			);
		});

		await test.step('Assign a workflow tasks to admin user', async () => {
			await tasksPage.goto();

			await tasksPage
				.getItem(assignedBlogTitle)
				.getByRole('button')
				.click();

			await page.getByRole('menuitem', {name: 'Assign to Me'}).click();

			await tasksPage.saveButton.click();

			await page.reload();
		});

		await test.step('Admin sees tasks separated by tab regardless of assignment', async () => {
			await tasksPage.goto();

			await tasksPage.allTasksTab.click();

			await expect(tasksPage.getItem(taskNames[0])).toBeVisible();
			await expect(tasksPage.getItem(assignedBlogTitle)).toBeVisible();
			await expect(tasksPage.getItem(unassignedBlogTitle)).toBeVisible();

			await tasksPage.projectTasksTab.click();

			await expect(tasksPage.getItem(taskNames[0])).toBeVisible();
			await expect(tasksPage.getItem(assignedBlogTitle)).toBeHidden();
			await expect(tasksPage.getItem(unassignedBlogTitle)).toBeHidden();

			await tasksPage.workflowTasksTab.click();

			await expect(tasksPage.getItem(taskNames[0])).toBeHidden();
			await expect(tasksPage.getItem(assignedBlogTitle)).toBeVisible();
			await expect(tasksPage.getItem(unassignedBlogTitle)).toBeVisible();
		});

		await test.step('Space admin sees tasks separated by tab based on assignment', async () => {
			await performUserSwitch(page, user.alternateName);

			await tasksPage.goto();

			await tasksPage.allTasksTab.click();

			await expect(tasksPage.getItem(taskNames[0])).toBeVisible();
			await expect(tasksPage.getItem(assignedBlogTitle)).toBeHidden();
			await expect(tasksPage.getItem(unassignedBlogTitle)).toBeVisible();

			await tasksPage.projectTasksTab.click();

			await expect(tasksPage.getItem(taskNames[0])).toBeVisible();
			await expect(tasksPage.getItem(assignedBlogTitle)).toBeHidden();
			await expect(tasksPage.getItem(unassignedBlogTitle)).toBeHidden();

			await tasksPage.workflowTasksTab.click();

			await expect(tasksPage.getItem(taskNames[0])).toBeHidden();
			await expect(tasksPage.getItem(assignedBlogTitle)).toBeHidden();
			await expect(tasksPage.getItem(unassignedBlogTitle)).toBeVisible();
		});

		await performUserSwitch(page, 'test');
	}
);

test(
	'View selector is visible only on Project Tasks tab',
	{tag: ['@LPD-88846']},
	async ({apiHelpers, assignWorkflowToAssetType, tasksPage}) => {
		await assignWorkflowToAssetType('Single Approver', 'Blog');

		const blogTitle = getRandomString();

		await apiHelpers.objectEntry.postObjectEntry(
			{
				keywords: [taskTags[0]],
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: blogTitle,
			},
			'cms/blogs',
			'Default'
		);

		await tasksPage.goto();

		await tasksPage.allTasksTab.click();

		await expect(tasksPage.viewSelectorButton).toBeHidden();

		await tasksPage.projectTasksTab.click();

		await expect(tasksPage.viewSelectorButton).toBeVisible();

		await tasksPage.workflowTasksTab.click();

		await expect(tasksPage.viewSelectorButton).toBeHidden();
	}
);

test(
	'Week view navigates to the previous week, next week, and today',
	{tag: ['@LPD-69885', '@LPD-94174']},
	async ({apiHelpers, page, projectPage, projectsPage, tasksPage}) => {
		const {calendarView} = tasksPage;

		const todayDate = toDateString(new Date());

		const taskTitle = getRandomString();

		await apiHelpers.objectEntry.postObjectEntry(
			{
				dueDate: `${todayDate}T00:00:00Z`,
				r_cmpProjectToCMPTasks_c_cmpProjectId: project.id,
				title: taskTitle,
			},
			cmpTask,
			project.scopeKey
		);

		await tasksPage.openProjectWeekView(
			projectsPage,
			projectPage,
			project.title
		);

		const todayTask = page
			.locator(`[data-date="${todayDate}"]`)
			.getByText(taskTitle, {exact: true});

		await expect(todayTask).toBeVisible();

		await calendarView.nextWeekButton.click();

		await expect(todayTask).toBeHidden();

		await calendarView.previousWeekButton.click();

		await expect(todayTask).toBeVisible();

		await calendarView.nextWeekButton.click();

		await expect(todayTask).toBeHidden();

		await calendarView.todayButton.click();

		await expect(todayTask).toBeVisible();
	}
);

test(
	'Week view schedules and unschedules a task',
	{tag: ['@LPD-69885', '@LPD-94174']},
	async ({apiHelpers, page, projectPage, projectsPage, tasksPage}) => {
		const {calendarView} = tasksPage;

		const todayDate = toDateString(new Date());

		const taskTitle = getRandomString();

		await apiHelpers.objectEntry.postObjectEntry(
			{
				r_cmpProjectToCMPTasks_c_cmpProjectId: project.id,
				title: taskTitle,
			},
			cmpTask,
			project.scopeKey
		);

		await tasksPage.openProjectWeekView(
			projectsPage,
			projectPage,
			project.title
		);

		const todayCell = page.locator(`[data-date="${todayDate}"]`);

		const unscheduledEntry = calendarView.unscheduledTasksPanel.getByText(
			taskTitle,
			{exact: true}
		);

		await test.step('Scheduling a task moves it onto its day', async () => {
			await calendarView.unscheduledTasksButton.click();

			await page
				.locator('[data-testid="calendarUnscheduledTasksSearch"]')
				.fill(taskTitle);

			await page
				.getByRole('button', {exact: true, name: 'Actions'})
				.click();

			await page.getByRole('menuitem', {name: 'Edit'}).click();

			await page.getByRole('textbox', {name: 'Due Date'}).fill(todayDate);

			await tasksPage.saveButton.click();

			await waitForAlert(
				page,
				`Success:${taskTitle} was updated successfully.`
			);

			await projectPage.tasksTab.click();

			await tasksPage.tableViewButton.click();

			await calendarView.viewOption.click();

			await tasksPage.switchToWeekView();

			await expect(
				todayCell.getByText(taskTitle, {exact: true})
			).toBeVisible();

			await clickAndExpectToBeVisible({
				target: calendarView.unscheduledTasksPanel,
				trigger: calendarView.unscheduledTasksButton,
			});

			await expect(unscheduledEntry).toBeHidden();

			await calendarView.unscheduledTasksButton.click();

			await expect(calendarView.unscheduledTasksPanel).toBeHidden();
		});

		await test.step('Clearing the due date returns the task to the panel', async () => {
			await page.getByRole('button', {name: taskTitle}).hover();

			await page
				.getByRole('button', {exact: true, name: 'Actions'})
				.click();

			await page.getByRole('menuitem', {name: 'Edit'}).click();

			await page.getByRole('textbox', {name: 'Due Date'}).clear();

			await tasksPage.saveButton.click();

			await waitForAlert(
				page,
				`Success:${taskTitle} was updated successfully.`
			);

			await projectPage.tasksTab.click();

			await tasksPage.tableViewButton.click();

			await calendarView.viewOption.click();

			await tasksPage.switchToWeekView();

			await clickAndExpectToBeVisible({
				target: calendarView.unscheduledTasksPanel,
				trigger: calendarView.unscheduledTasksButton,
			});

			await expect(unscheduledEntry).toBeVisible();
		});
	}
);

test(
	'Week view shows a task on its due date with the expanded card',
	{tag: ['@LPD-69885', '@LPD-94174']},
	async ({apiHelpers, page, projectPage, projectsPage, tasksPage}) => {
		const todayDate = toDateString(new Date());

		const taskTitle = getRandomString();

		await apiHelpers.objectEntry.postObjectEntry(
			{
				dueDate: `${todayDate}T00:00:00Z`,
				r_cmpProjectToCMPTasks_c_cmpProjectId: project.id,
				title: taskTitle,
			},
			cmpTask,
			project.scopeKey
		);

		await tasksPage.openProjectWeekView(
			projectsPage,
			projectPage,
			project.title
		);

		const todayCell = page.locator(`[data-date="${todayDate}"]`);

		await expect(
			todayCell.getByText(taskTitle, {exact: true})
		).toBeVisible();

		await expect(
			todayCell.getByText('Not Started', {exact: true}).first()
		).toBeVisible();
	}
);

test(
	'Week view shows seven day columns',
	{tag: ['@LPD-69885', '@LPD-94174']},
	async ({page, projectPage, projectsPage, tasksPage}) => {
		await tasksPage.openProjectWeekView(
			projectsPage,
			projectPage,
			project.title
		);

		await expect(page.locator('.fc-daygrid-day')).toHaveCount(7);
	}
);

/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {DataSetPage} from '../../../site-cms-site-initializer/main/pages/DataSetPage';
import {toDateString} from '../utils/toDateString';

import type {ProjectPage} from './ProjectPage';
import type {ProjectsPage} from './ProjectsPage';

interface ExecItemActionArgs {
	action: 'Assign Task' | 'Delete' | 'Update Due Date' | 'Update State';
	filter: string;
}

export class TasksPage {
	readonly addTaskKanbanButton: Locator;
	readonly allTasksTab: Locator;
	readonly assetTagNameField: Locator;
	readonly assignTaskToDialog: Locator;
	readonly calendarView: {
		datePickerMenu: Locator;
		dayViewButton: Locator;
		monthViewButton: Locator;
		moreLinkButton: Locator;
		moreLinkPopover: Locator;
		nextDayButton: Locator;
		nextMonthButton: Locator;
		nextWeekButton: Locator;
		previousDayButton: Locator;
		previousMonthButton: Locator;
		previousWeekButton: Locator;
		title: Locator;
		todayButton: Locator;
		unscheduledTasksButton: Locator;
		unscheduledTasksPanel: Locator;
		viewOption: Locator;
		weekViewButton: Locator;
	};
	readonly dataSetFragmentPage: DataSetPage;
	readonly dialogDeleteButton: Locator;
	readonly dropdownKanbanViewButton: Locator;
	readonly dropdownTableViewButton: Locator;
	readonly kanbanViewButton: Locator;
	readonly page: Page;
	readonly projectTasksTab: Locator;
	readonly projectTitleButton: Locator;
	readonly saveButton: Locator;
	readonly tableViewButton: Locator;
	readonly titleInput: Locator;
	readonly viewSelectorButton: Locator;
	readonly updateDueDateDialog: Locator;
	readonly updateStateDialog: Locator;
	readonly updateStateSelector: Locator;
	readonly workflowTasksTab: Locator;

	constructor(page: Page) {
		this.page = page;

		this.addTaskKanbanButton = page
			.getByRole('button', {name: 'Add Task'})
			.first();
		this.allTasksTab = page.getByRole('tab', {name: 'All Tasks'});
		this.assetTagNameField = page
			.locator('span')
			.filter({hasText: 'L_CMP_TASK_'})
			.first();

		this.assignTaskToDialog = page.getByRole('dialog', {
			name: 'Assign Tasks to',
		});
		this.calendarView = {
			datePickerMenu: page.getByRole('dialog', {name: 'Select Date'}),
			dayViewButton: page.getByRole('button', {
				exact: true,
				name: 'Day',
			}),
			monthViewButton: page.getByRole('button', {
				exact: true,
				name: 'Month',
			}),
			moreLinkButton: page.getByText(/\d+ More/),
			moreLinkPopover: page.getByTestId('calendarMoreLinkPopover'),
			nextDayButton: page.getByRole('button', {
				exact: true,
				name: 'Next Day',
			}),
			nextMonthButton: page.getByRole('button', {
				exact: true,
				name: 'Next Month',
			}),
			nextWeekButton: page.getByRole('button', {
				exact: true,
				name: 'Next Week',
			}),
			previousDayButton: page.getByRole('button', {
				exact: true,
				name: 'Previous Day',
			}),
			previousMonthButton: page.getByRole('button', {
				exact: true,
				name: 'Previous Month',
			}),
			previousWeekButton: page.getByRole('button', {
				exact: true,
				name: 'Previous Week',
			}),
			title: page.getByTestId('calendarTitle'),
			todayButton: page.getByRole('button', {name: 'Today'}),
			unscheduledTasksButton: page.getByText(/\d+ Unscheduled Tasks?/),
			unscheduledTasksPanel: page.getByTestId(
				'calendarUnscheduledTasksPanel'
			),
			viewOption: page.getByRole('option', {name: 'Calendar'}),
			weekViewButton: page.getByRole('button', {
				exact: true,
				name: 'Week',
			}),
		};
		this.dataSetFragmentPage = new DataSetPage(page);
		this.dialogDeleteButton = page
			.getByRole('dialog')
			.getByRole('button', {name: 'Delete'});
		this.dropdownKanbanViewButton = page.getByRole('option', {
			name: 'Kanban',
		});
		this.dropdownTableViewButton = page.getByRole('option', {
			name: 'Table',
		});
		this.projectTasksTab = page.getByRole('tab', {name: 'Project Tasks'});
		this.kanbanViewButton = page.getByRole('combobox', {
			name: 'Kanban View Selected',
		});
		this.projectTitleButton = page.locator(
			'#r_cmpProjectToCMPTasks_c_cmpProjectId'
		);
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.tableViewButton = page.getByRole('combobox', {
			name: 'Table View Selected',
		});
		this.titleInput = page.locator('#title');
		this.updateDueDateDialog = page.getByRole('dialog', {
			name: 'Update Due Date',
		});
		this.updateStateDialog = page.getByRole('dialog', {
			name: 'Update State',
		});
		this.updateStateSelector = this.updateStateDialog.getByRole('combobox');
		this.viewSelectorButton = page.getByRole('combobox', {
			name: /View Selected$/,
		});
		this.workflowTasksTab = page.getByRole('tab', {name: 'Workflow'});
	}

	getCalendarDayCell(date: Date): Locator {
		return this.page.locator(`[data-date="${toDateString(date)}"]`);
	}

	async dragCalendarItemToDay(source: Locator, dayCell: Locator) {
		const getCenter = async (locator: Locator) => {
			const box = await locator.boundingBox();

			if (!box) {
				throw new Error('The dragged element is not visible');
			}

			return {x: box.x + box.width / 2, y: box.y + box.height / 2};
		};

		const sourceCenter = await getCenter(source);

		await source.hover();

		await this.page.mouse.down();

		await this.page.mouse.move(sourceCenter.x + 10, sourceCenter.y + 10, {
			steps: 5,
		});

		const dayCellCenter = await getCenter(dayCell);

		await this.page.mouse.move(dayCellCenter.x, dayCellCenter.y, {
			steps: 10,
		});

		await this.page.mouse.up();
	}

	getItem(filter: string) {
		return this.page
			.getByRole('tabpanel')
			.locator(this.dataSetFragmentPage.getRow(filter));
	}

	async execBulkItemAction(action: string) {
		await this.dataSetFragmentPage.execBulkItemAction({action});
	}

	async execItemAction({action, filter}: ExecItemActionArgs) {
		await this.dataSetFragmentPage.execItemAction({
			action,
			filter,
		});
	}

	async goto() {
		await this.page.goto(PORTLET_URLS.cmpTasks);
	}

	async openProjectDayView(
		projectsPage: ProjectsPage,
		projectPage: ProjectPage,
		projectTitle: string
	) {
		await projectsPage.goto();

		await projectsPage.getProject(projectTitle).click();

		await projectPage.tasksTab.click();

		await this.tableViewButton.click();

		await this.calendarView.viewOption.click();

		await this.calendarView.title.waitFor({state: 'visible'});

		await this.switchToDayView();
	}

	async openProjectWeekView(
		projectsPage: ProjectsPage,
		projectPage: ProjectPage,
		projectTitle: string
	) {
		await projectsPage.goto();

		await projectsPage.getProject(projectTitle).click();

		await projectPage.tasksTab.click();

		await this.tableViewButton.click();

		await this.calendarView.viewOption.click();

		await this.calendarView.title.waitFor({state: 'visible'});

		await this.switchToWeekView();
	}

	async switchToDayView() {

		// The FDS view selector keeps focus after selecting Calendar and its
		// tooltip overlaps the view switcher, so blur it before switching.

		await this.page.evaluate(() =>
			(document.activeElement as HTMLElement)?.blur()
		);

		await this.calendarView.dayViewButton.click();

		await this.page
			.locator('.fc-dayGridDay-view')
			.waitFor({state: 'visible', timeout: 15000});
	}

	async switchToWeekView() {

		// The FDS view selector keeps focus after selecting Calendar and its
		// tooltip overlaps the view switcher, so blur it before switching.

		await this.page.evaluate(() =>
			(document.activeElement as HTMLElement)?.blur()
		);

		await this.calendarView.weekViewButton.click();

		await this.page
			.locator('.fc-dayGridWeek-view')
			.waitFor({state: 'visible', timeout: 15000});
	}
}

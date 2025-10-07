/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {waitForAlert} from '../../utils/waitForAlert';
import {ModalRecurrencePage} from './ModalRecurrencePage';

type RecurrenceOption = 'Entire Series' | 'Following Events' | 'Single Event';

export class CalendarWidgetPage {
	readonly addCalendarMenuItem: Locator;
	readonly addEventButton: Locator;
	readonly addEventMenuItem: Locator;
	readonly allDayCheckbox: Locator;
	readonly calendarColumns: Locator;
	readonly calendarOptions: Locator;
	readonly calendarWidget: Locator;
	readonly closeConfigurationButton: Locator;
	readonly closeEventModalButton: Locator;
	readonly configurationMenuItem: Locator;
	readonly endDate: Locator;
	readonly endTime: Locator;
	readonly hideSidebarIcon: Locator;
	readonly invitations: Locator;
	readonly inviteResource: Locator;
	readonly manageCalendarsMenuItem: Locator;
	readonly miniCalendarBase: Locator;
	readonly miniCalendarGrid: Locator;
	readonly miniCalendarHeaderLabel: Locator;
	readonly miniCalendarNextMonthButton: Locator;
	readonly miniCalendarPastMonthButton: Locator;
	readonly modalRecurrencePage: ModalRecurrencePage;
	readonly monthViewTab: Locator;
	readonly page: Page;
	readonly previousButton: Locator;
	readonly publishEventButton: Locator;
	readonly repeatCheckbox: Locator;
	readonly saveConfigurationButton: Locator;
	readonly startDate: Locator;
	readonly startTime: Locator;
	readonly submitForWorkflowButton: Locator;
	readonly successAlert: Locator;
	readonly timeZoneDropdown: Locator;
	readonly title: Locator;
	readonly titleLocalesDropdown: Locator;
	readonly unhideSidebarIcon: Locator;
	readonly useGlobalTimeZoneCheckBox: Locator;

	constructor(page: Page) {
		this.addCalendarMenuItem = page.getByRole('menuitem', {
			name: 'Add Calendar',
		});
		this.addEventButton = page.getByRole('button', {name: 'Add Event'});
		this.addEventMenuItem = page.getByRole('menuitem', {name: 'Add Event'});
		this.allDayCheckbox = page
			.frameLocator('iframe')
			.getByRole('checkbox', {
				exact: true,
				name: 'All Day',
			});
		this.calendarColumns = page.locator(
			'div.scheduler-view-day-table-col-shim'
		);
		this.calendarOptions = page
			.locator('#wrapper')
			.getByRole('button', {name: 'Options'});
		this.calendarWidget = page.locator(
			'.lfr-layout-structure-item-com-liferay-calendar-web-portlet-calendarportlet'
		);
		this.closeConfigurationButton = page
			.locator('.modal-header')
			.getByRole('button', {
				exact: true,
				name: 'Close',
			});
		this.closeEventModalButton = page.getByRole('button', {name: 'Close'});
		this.configurationMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Configuration',
		});
		this.endDate = page
			.frameLocator('iframe')
			.getByLabel('Ends Required', {exact: true});
		this.endTime = page
			.frameLocator('iframe')
			.locator('input[type="time"]')
			.last();
		this.hideSidebarIcon = page.locator(
			'.calendar-portlet-column-toggler .lexicon-icon-caret-left'
		);
		this.invitations = page
			.frameLocator('iframe')
			.getByText('Invitations', {exact: true});
		this.inviteResource = page
			.frameLocator('iframe')
			.getByTitle('Invite Resource', {exact: true});
		this.manageCalendarsMenuItem = page.getByRole('menuitem', {
			name: 'Manage Calendars',
		});
		this.miniCalendarBase = page.locator('.yui3-calendarbase');
		this.miniCalendarGrid = page.locator('.yui3-calendar-grid');
		this.miniCalendarHeaderLabel = page.locator(
			'.yui3-calendar-header-label'
		);
		this.miniCalendarNextMonthButton = page.locator(
			'.yui3-calendarnav-nextmonth'
		);
		this.miniCalendarPastMonthButton = page.locator(
			'.yui3-calendarnav-prevmonth'
		);
		this.modalRecurrencePage = new ModalRecurrencePage(page);
		this.monthViewTab = page.getByRole('tab', {name: 'Month View'});
		this.page = page;
		this.previousButton = page.getByLabel('Previous');
		this.publishEventButton = page
			.frameLocator('iframe')
			.getByRole('button', {exact: true, name: 'Publish'});
		this.repeatCheckbox = page
			.frameLocator('iframe')
			.getByRole('checkbox', {
				exact: true,
				name: 'Repeat',
			});
		this.saveConfigurationButton = page
			.frameLocator('iframe')
			.getByRole('button', {exact: true, name: 'Save'});
		this.startDate = page
			.frameLocator('iframe')
			.getByLabel('Starts Required', {exact: true});
		this.startTime = page
			.frameLocator('iframe')
			.locator('input[type="time"]')
			.first();
		this.submitForWorkflowButton = page
			.locator('iframe[title="New Event"]')
			.contentFrame()
			.getByRole('button', {name: 'Submit for Workflow'});
		this.successAlert = page
			.frameLocator('iframe')
			.locator('.alert-success', {
				hasText: 'Success:Your request completed successfully.',
			});
		this.timeZoneDropdown = page
			.frameLocator('iframe')
			.getByLabel('Time Zone', {exact: true});
		this.title = page
			.frameLocator('iframe')
			.getByLabel('Title', {exact: true});
		this.titleLocalesDropdown = page
			.frameLocator('iframe')
			.locator('[id$="titleMenu"]');
		this.unhideSidebarIcon = page.locator(
			'.calendar-portlet-column-toggler .lexicon-icon-caret-right'
		);
		this.useGlobalTimeZoneCheckBox = page
			.frameLocator('iframe')
			.getByRole('checkbox', {
				exact: true,
				name: 'Use Global Time Zone',
			});
	}

	async addEvent({
		allDay,
		endDate,
		endTime,
		publishEvent,
		startDate,
		startTime,
		throughCalendarActionMenu,
		title,
	}: {
		allDay: boolean;
		endDate?: string;
		endTime?: string;
		publishEvent?: boolean;
		startDate?: string;
		startTime?: string;
		throughCalendarActionMenu?: {calendarName: string};
		title?: string;
	}) {
		if (throughCalendarActionMenu) {
			await this.openCalendarActionsDropdownMenu(
				throughCalendarActionMenu.calendarName
			);
			await this.clickAddEventMenuitem();
		}
		else {
			await this.clickAddEventButton();
		}

		await this.allDayCheckbox.hover();
		await this.allDayCheckbox.setChecked(allDay);

		if (title) {
			await this.title.fill(title);
		}

		if (startDate) {
			await this.startDate.fill(startDate);
		}

		if (startTime) {
			await this.startTime.pressSequentially(startTime, {delay: 100});
		}

		if (endDate) {
			await this.endDate.fill(endDate);
		}

		if (endTime) {
			await this.endTime.pressSequentially(endTime, {delay: 100});
		}

		if (publishEvent) {
			await this.publishEvent({waitForSuccessAlert: true});
		}
	}

	async addEventOnGrid() {
		await this.calendarColumns.nth(0).click();
	}

	async addInvitation(userName: string) {
		await this.openInvitations();

		await this.inviteResource.fill(userName);

		await this.page
			.frameLocator('iframe')
			.getByRole('option', {name: userName})
			.click();
	}

	async clickAddEventButton() {
		await this.addEventButton.click();

		await this.page.waitForLoadState('networkidle');
	}

	async clickAddEventMenuitem() {
		await this.addEventMenuItem.click();

		await this.page.waitForLoadState('networkidle');
	}

	async clickCalendarColor(calendarColorHex: string) {
		await this.page.getByRole('radio', {name: calendarColorHex}).click();
	}

	async clickEvent(title: string) {
		await this.page.getByText(title).click();
	}

	async closeModalEvent() {
		await this.closeEventModalButton.click();
	}

	async createAndSubmitEvent({
		allDay = false,
		invitationUser,
		title,
		withWorkflow = false,
	}: {
		allDay?: boolean;
		invitationUser?: string;
		title: string;
		withWorkflow?: boolean;
	}) {
		await this.addEvent({allDay, title});

		if (invitationUser) {
			await this.addInvitation(invitationUser);
		}

		if (withWorkflow) {
			await this.submitEventForWorkflow();
		}
		else {
			await this.publishEvent();
		}

		await this.closeModalEvent();
	}

	async deleteApprovedEvents(eventTitles: string[]) {
		for (const title of [...eventTitles].reverse()) {
			const eventLocator = this.page.locator(
				'.calendar-portlet-event-approved .scheduler-event-content',
				{hasText: title}
			);

			await eventLocator.click();

			this.page.once('dialog', async (dialog) => {
				await dialog.accept();
			});

			await this.page.getByRole('button', {name: 'Delete'}).click();
		}
	}

	async fillEventWithRecurrenceAndAllDay(
		allDay: boolean,
		recurrence: Recurrence
	) {
		await this.clickAddEventButton();

		await this.allDayCheckbox.hover();
		await this.allDayCheckbox.setChecked(allDay);

		await this.repeatCheckbox.setChecked(true);

		await this.modalRecurrencePage.addRecurrence(recurrence);
	}

	async fillEventWithRecurrenceUntilDate({
		daysFromNow,
	}: {
		daysFromNow: number;
	}) {
		await this.clickAddEventButton();

		await this.repeatCheckbox.setChecked(true);

		await this.modalRecurrencePage.addRecurrenceUntilDate(daysFromNow);
	}

	async hideSidebar() {
		if (await this.hideSidebarIcon.isVisible()) {
			await this.page.waitForLoadState('networkidle');
			await this.hideSidebarIcon.click();
		}
	}

	async openCalendarActionsDropdownMenu(calendarName: string) {
		await this.page
			.getByLabel(`Show Actions for Calendar ${calendarName}`)
			.click();
	}

	async openCalendarGroupActionsDropdownMenu(groupName: string) {
		await this.page.getByLabel(`Manage Calendar ${groupName}`).click();
	}

	async openInvitations() {
		await this.invitations.click();
	}

	async publishEvent({
		recurrenceOption,
		waitForSuccessAlert,
	}: {
		recurrenceOption?: RecurrenceOption;
		waitForSuccessAlert?: boolean;
	} = {}) {
		await this.publishEventButton.click();

		if (recurrenceOption) {
			await this.page
				.frameLocator('iframe')
				.getByRole('button', {name: recurrenceOption})
				.click();
		}

		if (waitForSuccessAlert) {
			await waitForAlert(
				this.page.frameLocator('iframe'),
				`Success:Your request completed successfully.`
			);
		}
	}

	async setCalendarWidgetConfiguration(
		timeZone: string,
		useGlobalTimeZone: boolean
	) {
		await this.calendarWidget.click();

		await this.calendarOptions.click();

		await this.configurationMenuItem.click();

		await this.useGlobalTimeZoneCheckBox.setChecked(useGlobalTimeZone);

		if (!useGlobalTimeZone) {
			await this.timeZoneDropdown.selectOption(timeZone);
		}

		await this.saveConfigurationButton.click();
		await this.closeConfigurationButton.click();
	}

	async submitEventForWorkflow({
		waitForSuccessAlert,
	}: {
		waitForSuccessAlert?: boolean;
	} = {}) {
		await this.submitForWorkflowButton.click();

		if (waitForSuccessAlert) {
			await waitForAlert(
				this.page.frameLocator('iframe'),
				`Success:Your request completed successfully.`
			);
		}
	}

	async unhideSidebar() {
		if (await this.unhideSidebarIcon.isVisible()) {
			await this.page.waitForLoadState('networkidle');
			await this.unhideSidebarIcon.click();
		}
	}
}

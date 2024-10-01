/** allDayCheckbox
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {waitForAlert} from '../../utils/waitForAlert';
import {ModalRecurrencePage} from './ModalRecurrencePage';

export class CalendarWidgetPage {
	readonly addEventButton: Locator;
	readonly allDayCheckbox: Locator;
	readonly calendarGrid: Locator;
	readonly calendarWidget: Locator;
	readonly closeConfigurationButton: Locator;
	readonly configurationMenuItem: Locator;
	readonly endTime: Locator;
	readonly modalRecurrencePage: ModalRecurrencePage;
	readonly page: Page;
	readonly publishEventButton: Locator;
	readonly repeatCheckbox: Locator;
	readonly saveConfigurationButton: Locator;
	readonly startTime: Locator;
	readonly timeZoneDropdown: Locator;
	readonly useGlobalTimeZoneCheckBox: Locator;

	constructor(page: Page) {
		this.addEventButton = page.getByRole('button', {name: 'Add Event'});
		this.allDayCheckbox = page
			.frameLocator('iframe')
			.getByRole('checkbox', {
				exact: true,
				name: 'All Day',
			});
		this.calendarGrid = page.locator('.yui3-calendar-grid');
		this.calendarWidget = page.locator(
			'.lfr-layout-structure-item-com-liferay-calendar-web-portlet-calendarportlet'
		);
		this.closeConfigurationButton = page.getByRole('button', {
			exact: true,
			name: 'close',
		});
		this.configurationMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Configuration',
		});
		this.endTime = page
			.frameLocator('iframe')
			.getByLabel('Ends', {exact: true});
		this.modalRecurrencePage = new ModalRecurrencePage(page);
		this.page = page;
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
		this.startTime = page
			.frameLocator('iframe')
			.getByLabel('Starts', {exact: true});
		this.timeZoneDropdown = page
			.frameLocator('iframe')
			.getByLabel('Time Zone', {exact: true});
		this.useGlobalTimeZoneCheckBox = page
			.frameLocator('iframe')
			.getByRole('checkbox', {
				exact: true,
				name: 'Use Global Time Zone',
			});
	}

	async addEvent(allDay: boolean) {
		await this.addEventButton.click();

		await this.allDayCheckbox.hover();
		await this.allDayCheckbox.setChecked(allDay);

		await this.publishEvent();
	}

	async publishEvent() {
		await this.publishEventButton.click();
		await waitForAlert(
			this.page.frameLocator('iframe'),
			`Success:Your request completed successfully.`
		);
	}

	async fillEventWithRecurrence(allDay: boolean, recurrence: Recurrence) {
		await this.addEventButton.click();

		await this.allDayCheckbox.hover();
		await this.allDayCheckbox.setChecked(allDay);

		await this.repeatCheckbox.setChecked(true);

		await this.modalRecurrencePage.addRecurrence(recurrence);
	}

	async setCalendarWidgetConfiguration(
		timeZone: string,
		useGlobalTimeZone: boolean
	) {
		await this.calendarWidget.click();
		await this.calendarWidget.getByLabel('Options').click();

		await this.configurationMenuItem.click();

		await this.useGlobalTimeZoneCheckBox.setChecked(useGlobalTimeZone);

		if (!useGlobalTimeZone) {
			await this.timeZoneDropdown.selectOption(timeZone);
		}

		await this.saveConfigurationButton.click();
		await this.closeConfigurationButton.click();
	}
}

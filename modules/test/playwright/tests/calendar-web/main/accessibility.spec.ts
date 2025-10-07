/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {calendarPagesTest} from '../../../fixtures/calendarPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {toLocalDateTimeFormatted} from './utils/toLocalDateTimeFormatted';

export const test = mergeTests(
	apiHelpersTest,
	calendarPagesTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
);

test.beforeEach(
	async ({apiHelpers, calendarWidgetPage, page, pageEditorPage, site}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
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

		await calendarWidgetPage.setCalendarWidgetConfiguration(
			'Europe/Paris',
			false
		);

		await pageEditorPage.publishPage();

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);
	}
);

test.describe('Add new event modal', () => {
	test('follows accessibility guidelines related to focus when navigating with tab', async ({
		calendarWidgetPage,
		page,
	}) => {
		await calendarWidgetPage.clickAddEventButton();

		await expect(page.locator('h1.modal-title')).toBeFocused();

		await page.keyboard.down('Tab');

		await expect(calendarWidgetPage.closeEventModalButton).toBeFocused();

		await page.keyboard.down('Tab');

		await expect(calendarWidgetPage.title).toBeFocused();

		await page.keyboard.down('Tab');

		await expect(calendarWidgetPage.titleLocalesDropdown).toBeFocused();

		await page.keyboard.down('Tab');

		await expect(calendarWidgetPage.startDate).toBeFocused();
	});

	test('has description editor with an aria-label', async ({
		calendarWidgetPage,
		page,
	}) => {
		await calendarWidgetPage.clickAddEventButton();

		await page.waitForLoadState('networkidle');

		await expect(
			page
				.frameLocator('iframe')
				.frameLocator('iframe[title="editor"]')
				.getByLabel('Description')
		).toHaveAttribute('aria-label', 'Description');
	});
});

test.describe('Event creation pop-up', () => {
	test('has necessary roles to indicate when its open', async ({
		calendarWidgetPage,
		page,
	}) => {
		await calendarWidgetPage.addEventOnGrid();

		await page.getByRole('button', {name: 'Save'}).click();

		await page.waitForTimeout(1000);

		const eventTitle = page.getByTitle('e.g. Meeting');

		await calendarWidgetPage.hideSidebar();

		await expect(eventTitle).toHaveAttribute('aria-haspopup', 'dialog');
		await expect(eventTitle).toHaveAttribute('aria-expanded', 'false');

		await eventTitle.click();

		await expect(page.getByRole('dialog')).toHaveAttribute(
			'tabindex',
			'-1'
		);
		await expect(eventTitle).toHaveAttribute('aria-haspopup', 'dialog');
		await expect(eventTitle).toHaveAttribute('aria-expanded', 'true');
	});
});

test('assert that the screen reader reads the event date', async ({
	calendarWidgetPage,
	page,
}) => {
	await calendarWidgetPage.monthViewTab.click();

	const eventStartDay = new Date();

	const eventEndDay = new Date();
	eventEndDay.setDate(eventStartDay.getDate() + 1);

	const [startDate, endDate] = [eventStartDay, eventEndDay].map((date) =>
		toLocalDateTimeFormatted(date.toUTCString(), {
			day: '2-digit',
			month: '2-digit',
			year: 'numeric',
		})
	);

	const title = getRandomInt().toString();

	await calendarWidgetPage.addEvent({
		allDay: false,
		endDate,
		endTime: '1230PM',
		publishEvent: true,
		startDate,
		startTime: '1200PM',
		title,
	});

	await calendarWidgetPage.closeModalEvent();

	const screenReaderElement = page.getByText(
		`${eventStartDay.toLocaleDateString('en-US', {weekday: 'long'})}, ${eventStartDay.toLocaleDateString('en-US', {month: 'long'})} ${eventStartDay.getDate()}, ${eventStartDay.getFullYear()}`
	);

	await expect(screenReaderElement).toHaveCSS('display', 'block');
});

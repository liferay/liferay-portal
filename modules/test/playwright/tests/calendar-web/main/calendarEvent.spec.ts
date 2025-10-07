/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {calendarPagesTest} from '../../../fixtures/calendarPagesTest';
import {collectionsPagesTest} from '../../../fixtures/collectionsPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import performLogin, {
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {toLocalDateTimeFormatted} from './utils/toLocalDateTimeFormatted';

export const test = mergeTests(
	apiHelpersTest,
	calendarPagesTest,
	collectionsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	journalPagesTest,
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
);

const recurrence = {
	frequency: 'WEEKLY',
	ocurrences: '2',
	repeatDays: [
		new Date().toLocaleDateString('en-US', {
			timeZone: 'Europe/Paris',
			weekday: 'long',
		}),
	],
} as Recurrence;

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

test('assert that past events have respective class within the click more dropdown', async ({
	calendarWidgetPage,
	page,
}) => {
	await calendarWidgetPage.previousButton.click();

	for (let i = 0; i < 3; i++) {
		await calendarWidgetPage.addEvent({
			allDay: false,
			publishEvent: true,
			title: 'Event' + getRandomString(),
		});

		await calendarWidgetPage.closeModalEvent();
	}

	await calendarWidgetPage.monthViewTab.click();

	await page.getByRole('link', {name: 'Show 1 More'}).click();

	await expect(
		page
			.locator(
				'.scheduler-view-table-events-overlay-node-body .scheduler-event'
			)
			.nth(2)
	).toHaveClass(/scheduler-event-past/);
});

test('assert that unprivileged users are denied access to calendar URLs', async ({
	apiHelpers,
	calendarWidgetPage,
	collectionsPage,
	page,
	pageEditorPage,
	site,
}) => {

	// Add calendar event

	const title = getRandomString();

	await calendarWidgetPage.addEvent({
		allDay: true,
		publishEvent: true,
		title,
	});

	// Add calendar event dynamic collection

	await collectionsPage.goto(site.friendlyUrlPath);

	const calendarEventDynamicCollection = getRandomString();

	await collectionsPage.addNewDynamicCollection(
		calendarEventDynamicCollection
	);

	await page.getByLabel('Item Type').selectOption({label: 'Calendar Event'});

	await page.getByRole('button', {name: 'Save'}).click();

	// Add collection display

	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		options: {type: 'content'},
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	await pageEditorPage.addFragment('Content Display', 'Collection Display');

	await pageEditorPage.selectFragment(
		await pageEditorPage.getFragmentId('Collection Display')
	);

	await pageEditorPage.chooseCollectionDisplayCollection(
		'Collections',
		calendarEventDynamicCollection
	);

	await pageEditorPage.waitForChangesSaved();

	// Add heading fragment

	await pageEditorPage.addFragment(
		'Basic Components',
		'Heading',
		page.locator('.page-editor__collection-item.empty').first()
	);

	// Map heading fragment to event URL field

	await pageEditorPage.goToSidebarTab('Browser');

	await page.getByLabel('Select element-text').click();

	await page.getByLabel('Field').selectOption('Event URL');

	await pageEditorPage.waitForChangesSaved();

	// Access calendar event URL

	const eventURL = await page
		.getByText('/calendar/shared/-/calendar/')
		.textContent();

	await page.goto(eventURL);

	await expect(page.getByText(site.key)).toBeVisible();
	await expect(page.getByText(title)).toBeVisible();

	// Add new user

	const user = await apiHelpers.headlessAdminUser.postUserAccount();

	apiHelpers.data.push({
		id: user.id,
		type: 'userAccount',
	});

	userData[user.alternateName] = {
		name: user.givenName,
		password: 'test',
		surname: user.familyName,
	};

	await performLogout(page);
	await performLogin(page, user.alternateName);

	// Access event URL as new user

	await page.goto(eventURL);

	await expect(page.getByText(site.key)).not.toBeVisible();
	await expect(page.getByText(title)).not.toBeVisible();

	// Access calendar portlet as new user

	await page.goto(
		`/group/guest/~/control_panel/manage?p_p_id=com_liferay_calendar_web_portlet_CalendarPortlet&p_p_lifecycle=0&p_p_state=maximized&p_p_auth=obAWFnzM`
	);

	await expect(
		page
			.getByText(
				'You do not have the roles required to access this portlet.'
			)
			.first()
	).toBeVisible();
});

test('can create all-day calendar event with different time zone', async ({
	calendarWidgetPage,
}) => {
	await calendarWidgetPage.addEvent({allDay: true, publishEvent: true});

	const endDate = await calendarWidgetPage.endDate.inputValue();
	const startDate = await calendarWidgetPage.startDate.inputValue();

	await expect(endDate).toEqual(startDate);
});

test('can create an all-day calendar event in a different time zone, ensuring that the recurrence link remains consistent', async ({
	calendarWidgetPage,
}) => {
	await calendarWidgetPage.fillEventWithRecurrenceAndAllDay(true, recurrence);

	const {frequency, ocurrences, repeatDays} = recurrence;

	const expectedLink =
		frequency +
		', on ' +
		repeatDays.join(',') +
		', ' +
		ocurrences +
		' Times';

	await expect(
		calendarWidgetPage.page.frameLocator('iframe').getByRole('link', {
			name: expectedLink,
		})
	).toBeVisible();

	await calendarWidgetPage.publishEvent({waitForSuccessAlert: true});

	await expect(
		calendarWidgetPage.page.frameLocator('iframe').getByRole('link', {
			name: expectedLink,
		})
	).toBeVisible();
});

test('can create calendar event different start/end dates ensuring that the end date is displayed', async ({
	calendarWidgetPage,
	page,
}) => {
	const startDate = new Date();

	const endDate = new Date(startDate);
	endDate.setDate(endDate.getDate() + 1);

	const endDateFormatted = toLocalDateTimeFormatted(endDate.toUTCString(), {
		day: '2-digit',
		hour: '2-digit',
		month: '2-digit',
		timeZone: 'UTC',
		year: 'numeric',
	} as const);

	const title = getRandomInt().toString();

	await calendarWidgetPage.addEvent({
		allDay: false,
		endDate: endDateFormatted,
		publishEvent: true,
		title,
	});

	await calendarWidgetPage.closeModalEvent();

	await calendarWidgetPage.clickEvent(title);

	await expect(page.locator('.scheduler-event-recorder-date')).toHaveText(
		new RegExp(
			toLocalDateTimeFormatted(endDate.toUTCString(), {
				day: '2-digit',
				month: 'long',
				timeZone: 'UTC',
				weekday: 'short',
			} as const)
		)
	);
});

test('can create calendar event with invitation', async ({
	apiHelpers,
	calendarWidgetPage,
	page,
}) => {
	const user1 = await apiHelpers.headlessAdminUser.postUserAccount();
	const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

	try {

		// Access the calendar widget page as new user to create user calendar resources

		const currentURL = page.url();

		await page.goto(`${currentURL}?doAsUserId=${user1.id}`);
		await page.goto(`${currentURL}?doAsUserId=${user2.id}`);

		// As user 2, add an event and send an invitation to user 1

		await calendarWidgetPage.addEvent({allDay: true, publishEvent: false});

		await calendarWidgetPage.addInvitation(user1.name);

		await calendarWidgetPage.publishEvent({waitForSuccessAlert: true});

		await calendarWidgetPage.openInvitations();

		await expect(
			calendarWidgetPage.page
				.frameLocator('iframe')
				.getByText('Pending (1)')
		).toBeVisible();
		await expect(
			calendarWidgetPage.page.frameLocator('iframe').getByText(user1.name)
		).toBeVisible();
	}
	finally {
		await apiHelpers.headlessAdminUser.deleteUserAccount(Number(user1.id));
		await apiHelpers.headlessAdminUser.deleteUserAccount(Number(user2.id));
	}
});

test('can see calendar event inputs alerts', async ({
	calendarWidgetPage,
	page,
}) => {
	await calendarWidgetPage.clickAddEventButton();

	await calendarWidgetPage.startDate.fill('');

	await calendarWidgetPage.startDate.blur();

	await expect(
		page
			.frameLocator('iframe')
			.getByText(
				'This field will be automatically filled if it is empty or incomplete.'
			)
	).toBeVisible();

	await calendarWidgetPage.startDate.fill('abc');

	await calendarWidgetPage.startDate.blur();

	await expect(
		page.frameLocator('iframe').getByText('Please enter a valid date.')
	).toBeVisible();

	await calendarWidgetPage.startDate.fill('10/10/2010');

	await calendarWidgetPage.startDate.blur();

	await expect(
		page.frameLocator('iframe').getByText('Please enter a valid date.')
	).toBeHidden();

	await expect(
		page
			.frameLocator('iframe')
			.getByText(
				'This field will be automatically filled if it is empty or incomplete.'
			)
	).toBeHidden();

	await calendarWidgetPage.startTime.focus();

	await page.keyboard.press('Backspace');

	await calendarWidgetPage.startTime.blur();

	await expect(
		page.frameLocator('iframe').getByText('Please enter a valid time')
	).toBeVisible();

	await calendarWidgetPage.startTime.evaluate((startTime) => {
		(startTime as HTMLInputElement).value = '';
	});

	await calendarWidgetPage.startTime.focus();

	await calendarWidgetPage.startTime.blur();

	await expect(
		page
			.frameLocator('iframe')
			.getByText(
				'This field will be automatically filled if it is empty or incomplete.'
			)
	).toBeVisible();

	await calendarWidgetPage.startTime.evaluate((startTime) => {
		(startTime as HTMLInputElement).value = '14:30';
	});

	await calendarWidgetPage.startTime.focus();

	await calendarWidgetPage.startTime.blur();

	await expect(
		page.frameLocator('iframe').getByText('Please enter a valid time.')
	).toBeHidden();

	await expect(
		page
			.frameLocator('iframe')
			.getByText(
				'This field will be automatically filled if it is empty or incomplete.'
			)
	).toBeHidden();
});

test('can update an event with recurrence', async ({
	calendarWidgetPage,
	modalRecurrencePage,
}) => {
	await calendarWidgetPage.fillEventWithRecurrenceUntilDate({daysFromNow: 5});

	await expect(modalRecurrencePage.inputDate).toBeEnabled();

	await modalRecurrencePage.doneButton.click();

	await calendarWidgetPage.publishEvent();

	await expect(calendarWidgetPage.successAlert).toBeVisible();

	await calendarWidgetPage.publishEvent({
		recurrenceOption: 'Following Events',
	});

	await expect(calendarWidgetPage.successAlert).toBeVisible();

	await calendarWidgetPage.publishEvent({recurrenceOption: 'Entire Series'});

	await expect(calendarWidgetPage.successAlert).toBeVisible();

	await calendarWidgetPage.repeatCheckbox.setChecked(false);

	await calendarWidgetPage.repeatCheckbox.setChecked(true);

	await modalRecurrencePage.doneButton.nth(1).click();

	await calendarWidgetPage.publishEvent({recurrenceOption: 'Single Event'});

	await expect(calendarWidgetPage.successAlert).toBeVisible();
});

test('event ending at midnight does not render on the next day', async ({
	calendarWidgetPage,
	page,
}) => {
	const eventStartDay = new Date();
	eventStartDay.setDate(15);

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
		endTime: '1200AM',
		publishEvent: true,
		startDate,
		startTime: '1200PM',
		title,
	});

	await calendarWidgetPage.closeModalEvent();
	await calendarWidgetPage.monthViewTab.click();

	await expect(page.getByTitle(title, {exact: true})).toHaveCount(1);
	await expect(page.locator('.lfr-busy-day')).toHaveCount(1);
});

test('event with weekly recurrence has default value for repeat on field and has at least one value for repeat on options', async ({
	calendarWidgetPage,
	modalRecurrencePage,
	page,
}) => {
	await calendarWidgetPage.clickAddEventButton();

	await calendarWidgetPage.repeatCheckbox.setChecked(true);

	await modalRecurrencePage.repeatSelect.selectOption('Weekly');

	const today = new Date().toLocaleDateString('en-US', {
		timeZone: 'Europe/Paris',
		weekday: 'long',
	});

	const dayOfWeekCheckbox = page
		.frameLocator('iframe')
		.getByRole('checkbox', {name: today});

	await expect(dayOfWeekCheckbox).toBeChecked();

	await dayOfWeekCheckbox.click();

	await expect(dayOfWeekCheckbox).toBeChecked();

	await modalRecurrencePage.doneButton.click();

	await calendarWidgetPage.publishEvent();

	await expect(calendarWidgetPage.successAlert).toBeVisible();
});

/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import fs from 'fs';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {calendarPagesTest} from '../../../fixtures/calendarPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
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
	pageEditorPagesTest
);

let siteName: string;

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

		siteName = site.name;

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await calendarWidgetPage.setCalendarWidgetConfiguration(
			'Europe/Paris',
			false
		);

		await pageEditorPage.publishPage();

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);
	}
);

test(
	'can export a calendar to an ICS file',
	{tag: '@LPD-104213'},
	async ({calendarWidgetPage, page}) => {
		const eventTitle = getRandomString();

		await test.step('Add an event so the exported file has content', async () => {
			await calendarWidgetPage.addEvent({
				allDay: false,
				publishEvent: true,
				throughCalendarActionMenu: {calendarName: 'Test Test'},
				title: eventTitle,
			});

			await calendarWidgetPage.page.keyboard.press('Escape');
		});

		const download =
			await test.step('Export the calendar from Manage Calendars', async () => {
				await calendarWidgetPage.unhideSidebar();

				await calendarWidgetPage.openCalendarGroupActionsDropdownMenu(
					'My Calendars'
				);

				await calendarWidgetPage.manageCalendarsMenuItem.click();

				const exportMenuItem = page.getByRole('link', {
					exact: true,
					name: 'Export',
				});

				await clickAndExpectToBeVisible({
					target: exportMenuItem,
					trigger: page
						.getByRole('row', {name: 'Test Test'})
						.getByRole('button'),
				});

				const downloadPromise = page.waitForEvent('download');

				await exportMenuItem.click();

				return downloadPromise;
			});

		await test.step('Check the downloaded ICS file carries the event', async () => {
			expect(download.suggestedFilename()).toContain('.ics');

			expect(fs.readFileSync(await download.path(), 'utf8')).toContain(
				eventTitle
			);
		});
	}
);

test(
	'can import an ICS file into a calendar',
	{tag: '@LPD-104213'},
	async ({calendarWidgetPage, page}) => {
		await calendarWidgetPage.unhideSidebar();

		await calendarWidgetPage.openCalendarGroupActionsDropdownMenu(siteName);

		await calendarWidgetPage.manageCalendarsMenuItem.click();

		const importMenuItem = page.getByRole('link', {
			exact: true,
			name: 'Import',
		});

		await clickAndExpectToBeVisible({
			target: importMenuItem,
			trigger: page
				.getByRole('row', {name: siteName})
				.getByRole('button'),
		});

		await importMenuItem.click();

		await page
			.locator('input[type="file"]')
			.setInputFiles(
				path.join(
					__dirname,
					'dependencies',
					'calendar_microsoft_outlook_calendar.ics'
				)
			);

		await page.getByRole('button', {name: 'Import'}).click();

		await expect(
			page.locator('.portlet-msg-success:not(.hide)')
		).toBeVisible();
	}
);

test('color column in manage calendar page is updated when the user changes the calendar color', async ({
	calendarWidgetPage,
}) => {
	await calendarWidgetPage.unhideSidebar();

	await calendarWidgetPage.openCalendarActionsDropdownMenu('Test Test');

	await calendarWidgetPage.clickCalendarColor('#E0C240');

	await calendarWidgetPage.openCalendarGroupActionsDropdownMenu(
		'My Calendars'
	);

	await calendarWidgetPage.manageCalendarsMenuItem.click();

	await expect(
		calendarWidgetPage.page.locator('.calendar-portlet-color-box')
	).toHaveCSS('background-color', 'rgb(224, 194, 64)');
});

test('can choose color when adding a calendar and then change it', async ({
	calendarWidgetPage,
}) => {
	await calendarWidgetPage.unhideSidebar();

	await calendarWidgetPage.page.waitForLoadState('networkidle');

	await calendarWidgetPage.openCalendarGroupActionsDropdownMenu(siteName);

	await calendarWidgetPage.addCalendarMenuItem.click();

	const calendarName = 'Calendar' + getRandomInt();

	const calendarIframeLocator = calendarWidgetPage.page.frameLocator(
		'iframe[title="Add Calendar"]'
	);

	await calendarIframeLocator.getByLabel('Name').fill(calendarName);

	await calendarIframeLocator.getByRole('radio', {name: '#85AAA5'}).click();

	await waitForAlert(
		calendarIframeLocator,
		`Success:Your request completed successfully.`
	);

	await calendarWidgetPage.page.keyboard.press('Escape');

	await calendarWidgetPage.openCalendarActionsDropdownMenu(calendarName);

	// check if the color we chose while adding a calendar was applied

	await expect(
		calendarWidgetPage.page.locator('.simple-color-picker-item-selected')
	).toHaveCSS('background-color', 'rgb(133, 170, 165)');

	await calendarWidgetPage.clickCalendarColor('#4CB052');

	const eventTitle = 'Event' + getRandomInt();

	await calendarWidgetPage.addEvent({
		allDay: false,
		publishEvent: true,
		throughCalendarActionMenu: {calendarName},
		title: eventTitle,
	});

	await calendarWidgetPage.page.keyboard.press('Escape');

	// check if the color we changed to was applied in the regular event element

	expect(calendarWidgetPage.page.getByTitle(eventTitle)).toHaveCSS(
		'background-color',
		'rgb(76, 176, 82)'
	);
});

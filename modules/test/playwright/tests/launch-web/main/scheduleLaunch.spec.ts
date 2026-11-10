/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {launchPagesTest} from '../../../fixtures/launchPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-72278': {enabled: true},
	}),
	launchPagesTest,
	loginTest()
).extend<{launchSetIds: number[]}>({
	launchSetIds: async ({apiHelpers}, use) => {
		const launchSetIds: number[] = [];

		await use(launchSetIds);

		for (const launchSetId of launchSetIds) {
			await apiHelpers.launch.deleteLaunchSet(launchSetId);
		}
	},
});

test(
	'Schedules a launch from the Launches list in another time zone',
	{tag: ['@LPD-114056']},
	async ({apiHelpers, launchSetIds, launchesPage, page}) => {
		const launchName = getRandomString();

		const launchSet = await apiHelpers.launch.createLaunchSet(launchName);

		launchSetIds.push(launchSet.id);

		const row = page.getByRole('row').filter({hasText: launchName});

		await test.step('Schedule it at 9:00 AM in New York', async () => {
			await launchesPage.goto();

			await expect(row).toContainText('In Progress');

			await row.getByRole('button', {name: 'Actions'}).click();

			await page.getByRole('menuitem', {name: 'Schedule'}).click();

			const dialog = page.getByRole('dialog');

			await expect(dialog).toContainText(`Schedule ${launchName}`);

			await dialog
				.getByLabel('Date and Time')
				.fill('10/31/2030 09:00 AM');

			await dialog
				.getByLabel('Time Zone')
				.selectOption('America/New_York');

			await expect(dialog).toContainText('Your local time');

			await dialog.getByRole('button', {name: 'Schedule'}).click();

			await expect(dialog).toBeHidden();
		});

		await test.step('Show the launch as scheduled in the user time zone', async () => {
			const scheduledDate = await page.evaluate(() =>
				new Intl.DateTimeFormat(
					Liferay.ThemeDisplay.getBCP47LanguageId(),
					{
						day: 'numeric',
						hour: 'numeric',
						minute: 'numeric',
						month: 'short',
						second: 'numeric',
						timeZone: Liferay.ThemeDisplay.getTimeZone(),
						year: 'numeric',
					}
				).format(new Date(Date.UTC(2030, 9, 31, 13)))
			);

			await expect(row).toContainText('Scheduled');
			await expect(row).toContainText(scheduledDate);
		});
	}
);

/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

test(
	'A new Space can be created from the All Spaces view and shows up in the left panel',
	{tag: '@LPD-85670'},
	async ({page}) => {
		const spaceName = `Space ${getRandomString()}`;

		await test.step('Open All Spaces view', async () => {
			await page.goto(PORTLET_URLS.cmsAllSpaces);
		});

		await test.step('Create a new Space via UI', async () => {
			await page.getByLabel('Add Space').first().click();
			await page.getByLabel('Space Name').fill(spaceName);
			await page.getByRole('button', {name: 'Continue'}).click();

			await expect(
				page.getByRole('heading', {
					name: `Add Members to ${spaceName}`,
				})
			).toBeVisible();

			await page.getByRole('button', {name: /Continue/}).click();
		});

		await test.step('Space is visible in the All Spaces table and the left panel', async () => {
			await page.goto(PORTLET_URLS.cmsAllSpaces);

			await expect(
				page.getByRole('link', {exact: true, name: spaceName})
			).toBeVisible();

			await expect(
				page.getByRole('menuitem', {name: spaceName})
			).toBeVisible();
		});
	}
);

test(
	'Space general settings can be opened and saved with an updated description',
	{tag: '@LPD-85670'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const description = `Description ${getRandomString()}`;
		const spaceName = `Space ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {},
			type: 'Space',
		});

		await spaceSummaryPage.goto(spaceName);

		await page.getByRole('button', {name: 'More Actions'}).click();
		await page.getByRole('menuitem', {name: 'Settings'}).click();

		const descriptionTextbox = page.getByRole('textbox', {
			name: 'Description',
		});

		await descriptionTextbox.fill(description);

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page, 'Success');

		await spaceSummaryPage.goto(spaceName);

		await page.getByRole('button', {name: 'More Actions'}).click();
		await page.getByRole('menuitem', {name: 'Settings'}).click();

		await expect(descriptionTextbox).toHaveValue(description);
	}
);

test(
	'Trash entries max age field shows the company default in days and rejects values below 1',
	{tag: '@LPD-91035'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const spaceName = `Space ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {},
			type: 'Space',
		});

		await spaceSummaryPage.goto(spaceName);

		await page.getByRole('button', {name: 'More Actions'}).click();
		await page.getByRole('menuitem', {name: 'Settings'}).click();

		const trashEntriesMaxAgeField = page.getByRole('spinbutton', {
			name: 'Trash Entries Max Age',
		});

		await expect(trashEntriesMaxAgeField).toHaveValue('30');

		await trashEntriesMaxAgeField.fill('0');
		await page.getByRole('button', {name: 'Save'}).click();

		await expect(
			page.getByText('Please enter a value greater than or equal to 1')
		).toBeVisible();

		await trashEntriesMaxAgeField.fill('7');
		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page, 'Success');

		await spaceSummaryPage.goto(spaceName);

		await page.getByRole('button', {name: 'More Actions'}).click();
		await page.getByRole('menuitem', {name: 'Settings'}).click();

		await expect(trashEntriesMaxAgeField).toHaveValue('7');
	}
);

test(
	'The Permissions modal can be opened from the All Spaces row actions',
	{tag: '@LPD-85670'},
	async ({apiHelpers, page}) => {
		const spaceName = `Space ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {},
			type: 'Space',
		});

		await page.goto(PORTLET_URLS.cmsAllSpaces);

		const spaceRow = page.getByRole('row', {name: spaceName});

		await spaceRow.getByRole('button', {name: 'Actions'}).click();

		await page
			.getByRole('menuitem', {exact: true, name: 'Permissions'})
			.click();

		await page
			.getByRole('menuitem', {exact: true, name: 'Permissions'})
			.last()
			.click();

		await expect(
			page.getByRole('dialog').getByRole('heading', {name: 'Permissions'})
		).toBeVisible();
	}
);

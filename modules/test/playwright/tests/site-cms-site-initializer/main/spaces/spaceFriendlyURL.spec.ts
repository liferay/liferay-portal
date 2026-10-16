/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

test(
	'Persists a custom friendly URL via Headless Asset Library PATCH',
	{tag: '@LPD-88344'},
	async ({apiHelpers}) => {
		const space = await apiHelpers.headlessAssetLibrary.createSpace();

		const friendlyURL = `/cms-${getRandomString()}`;

		const patchedAssetLibrary =
			await apiHelpers.headlessAssetLibrary.patchAssetLibrary(
				space.externalReferenceCode,
				{friendlyURL}
			);

		expect(patchedAssetLibrary.friendlyURL).toBe(friendlyURL);

		const fetchedAssetLibrary =
			await apiHelpers.headlessAssetLibrary.getAssetLibrary(
				space.externalReferenceCode
			);

		expect(fetchedAssetLibrary.friendlyURL).toBe(friendlyURL);
	}
);

test(
	'Rejects a reserved friendly URL with FRIENDLY_URL_KEYWORD_CONFLICT',
	{tag: '@LPD-88344'},
	async ({apiHelpers}) => {
		const space = await apiHelpers.headlessAssetLibrary.createSpace();

		const problem =
			await apiHelpers.headlessAssetLibrary.patchAssetLibraryWithProblem(
				space.externalReferenceCode,
				{friendlyURL: '/api'}
			);

		expect(problem.status).toBe('BAD_REQUEST');
		expect(problem.type).toBe('FRIENDLY_URL_KEYWORD_CONFLICT');
	}
);

test(
	'Rejects invalid characters with FRIENDLY_URL_INVALID_CHARACTERS',
	{tag: '@LPD-88344'},
	async ({apiHelpers}) => {
		const space = await apiHelpers.headlessAssetLibrary.createSpace();

		const problem =
			await apiHelpers.headlessAssetLibrary.patchAssetLibraryWithProblem(
				space.externalReferenceCode,
				{friendlyURL: '/.'}
			);

		expect(problem.status).toBe('BAD_REQUEST');
		expect(problem.type).toBe('FRIENDLY_URL_INVALID_CHARACTERS');
	}
);

test(
	'Rejects a duplicate friendly URL with FRIENDLY_URL_DUPLICATE',
	{tag: '@LPD-88344'},
	async ({apiHelpers}) => {
		const sharedFriendlyURL = `/cms-${getRandomString()}`;

		const space1 = await apiHelpers.headlessAssetLibrary.createSpace();

		await apiHelpers.headlessAssetLibrary.patchAssetLibrary(
			space1.externalReferenceCode,
			{friendlyURL: sharedFriendlyURL}
		);

		const space2 = await apiHelpers.headlessAssetLibrary.createSpace();

		const problem =
			await apiHelpers.headlessAssetLibrary.patchAssetLibraryWithProblem(
				space2.externalReferenceCode,
				{friendlyURL: sharedFriendlyURL}
			);

		expect(problem.status).toBe('BAD_REQUEST');
		expect(problem.type).toBe('FRIENDLY_URL_DUPLICATE');
	}
);

test(
	'Cancelling the save confirmation does not change the Friendly URL',
	{tag: '@LPD-88344'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const space = await apiHelpers.headlessAssetLibrary.createSpace();

		await spaceSummaryPage.goto(space.name);

		await page.getByRole('button', {name: 'More Actions'}).click();

		await page.getByRole('menuitem', {name: 'Space Settings'}).click();

		const friendlyURL = page.getByRole('textbox', {
			name: 'Friendly URL Required',
		});

		await friendlyURL.fill('cms-should-not-save');

		await page.getByRole('button', {name: 'Save'}).click();

		const dialog = page.getByRole('alertdialog', {
			name: 'Save Custom Friendly URL',
		});

		await dialog.waitFor();

		await dialog.getByRole('button', {name: 'Cancel'}).click();

		const fetchedAssetLibrary =
			await apiHelpers.headlessAssetLibrary.getAssetLibrary(
				space.externalReferenceCode
			);

		expect(fetchedAssetLibrary.friendlyURL).toBe(space.friendlyURL);
	}
);

test(
	'Confirming the save persists a custom Friendly URL entered in Space Settings',
	{tag: '@LPD-103464'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const space = await apiHelpers.headlessAssetLibrary.createSpace();

		const friendlyURLPath = `cms-${getRandomString()}`.toLowerCase();

		await spaceSummaryPage.goto(space.name);

		await page.getByRole('button', {name: 'More Actions'}).click();

		await page.getByRole('menuitem', {name: 'Space Settings'}).click();

		const friendlyURLTextbox = page.getByRole('textbox', {
			name: 'Friendly URL Required',
		});

		await expect(friendlyURLTextbox).toHaveValue(space.friendlyURL);

		await friendlyURLTextbox.fill(friendlyURLPath);

		await page.getByRole('button', {name: 'Save'}).click();

		const dialog = page.getByRole('alertdialog', {
			name: 'Save Custom Friendly URL',
		});

		await dialog.waitFor();

		await dialog.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(
			page,
			`Success:${space.name} was saved successfully.`
		);

		const fetchedAssetLibrary =
			await apiHelpers.headlessAssetLibrary.getAssetLibrary(
				space.externalReferenceCode
			);

		expect(fetchedAssetLibrary.friendlyURL).toBe(`/${friendlyURLPath}`);
	}
);

test(
	'Defaults the Friendly URL of a new Space to its name',
	{tag: '@LPD-108842'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const randomString = getRandomString();

		const spaceName = `Space ${randomString}`;

		const space = await apiHelpers.headlessAssetLibrary.createSpace({
			name: spaceName,
		});

		expect(space.friendlyURL).toBe(`/space-${randomString}`);

		await spaceSummaryPage.goto(spaceName);

		await page.getByRole('button', {name: 'More Actions'}).click();

		await page.getByRole('menuitem', {name: 'Space Settings'}).click();

		await expect(
			page.getByRole('textbox', {name: 'Friendly URL Required'})
		).toHaveValue(`/space-${randomString}`);
	}
);

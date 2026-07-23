/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

test(
	'Persists a custom friendly URL via Headless Asset Library PATCH',
	{tag: '@LPD-88344'},
	async ({apiHelpers}) => {
		const assetLibrary =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: `Space ${getRandomString()}`,
				type: 'Space',
			});

		const friendlyURL = `/cms-${getRandomString()}`;

		const patchedAssetLibrary =
			await apiHelpers.headlessAssetLibrary.patchAssetLibrary(
				assetLibrary.externalReferenceCode,
				{friendlyURL}
			);

		expect(patchedAssetLibrary.friendlyURL).toBe(friendlyURL);

		const fetchedAssetLibrary =
			await apiHelpers.headlessAssetLibrary.getAssetLibrary(
				assetLibrary.externalReferenceCode
			);

		expect(fetchedAssetLibrary.friendlyURL).toBe(friendlyURL);
	}
);

test(
	'Rejects a reserved friendly URL with FRIENDLY_URL_KEYWORD_CONFLICT',
	{tag: '@LPD-88344'},
	async ({apiHelpers}) => {
		const assetLibrary =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: `Space ${getRandomString()}`,
				type: 'Space',
			});

		const problem =
			await apiHelpers.headlessAssetLibrary.patchAssetLibraryWithProblem(
				assetLibrary.externalReferenceCode,
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
		const assetLibrary =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: `Space ${getRandomString()}`,
				type: 'Space',
			});

		const problem =
			await apiHelpers.headlessAssetLibrary.patchAssetLibraryWithProblem(
				assetLibrary.externalReferenceCode,
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

		const assetLibrary1 =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: `Space ${getRandomString()}`,
				type: 'Space',
			});

		await apiHelpers.headlessAssetLibrary.patchAssetLibrary(
			assetLibrary1.externalReferenceCode,
			{friendlyURL: sharedFriendlyURL}
		);

		const assetLibrary2 =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: `Space ${getRandomString()}`,
				type: 'Space',
			});

		const problem =
			await apiHelpers.headlessAssetLibrary.patchAssetLibraryWithProblem(
				assetLibrary2.externalReferenceCode,
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
		const spaceName = `Space ${getRandomString()}`;

		const assetLibrary =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				type: 'Space',
			});

		const initialFriendlyURL = `/asset-library-${assetLibrary.id}`;

		await spaceSummaryPage.goto(spaceName);

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
				assetLibrary.externalReferenceCode
			);

		expect(fetchedAssetLibrary.friendlyURL).toBe(initialFriendlyURL);
	}
);

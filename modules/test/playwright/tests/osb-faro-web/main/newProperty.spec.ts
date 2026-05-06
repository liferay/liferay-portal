/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {loginAnalyticsCloudTest} from '../../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../../fixtures/loginTest';
import {ACPage, navigateToACSettingsViaURL} from './utils/navigation';

const test = mergeTests(apiHelpersTest, loginAnalyticsCloudTest(), loginTest());

test(
	'Cannot create a property with its name in blank or null',
	{
		tag: '@LRAC-9126',
	},
	async ({apiHelpers, page}) => {

		// Go to the AC properties settings page

		const projects = await apiHelpers.jsonWebServicesOSBFaro.getProjects();

		const project = projects.find(({name}) => name === 'FARO-DEV-liferay');

		await navigateToACSettingsViaURL({
			acPage: ACPage.propertiesPage,
			page,
			projectID: project.groupId,
		});

		// Open the new property dialog and leave the name empty

		await page.getByRole('button', {name: 'New Property'}).click();

		const nameInput = page.getByLabel('Property Name');

		await nameInput.fill('x');
		await nameInput.clear();

		// Assert that the save button is disabled

		await expect(page.getByRole('button', {name: 'Save'})).toBeDisabled();
	}
);

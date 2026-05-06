/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {loginAnalyticsCloudTest} from '../../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {createChannel} from './utils/channel';
import {ACPage, navigateToACSettingsViaURL} from './utils/navigation';

const test = mergeTests(apiHelpersTest, loginAnalyticsCloudTest(), loginTest());

test(
	'Can edit a user role from the user management settings page',
	{
		tag: '@LRAC-9062',
	},
	async ({apiHelpers, page}) => {

		// Create a channel and invite a user via API

		const channelName = 'My Property - ' + getRandomString();

		const {channel, project} = await createChannel({
			apiHelpers,
			channelName,
		});

		const userEmail = `ac-${getRandomString()}@liferay.com`;

		const [user] = await apiHelpers.jsonWebServicesOSBFaro.createUser(
			userEmail,
			project.groupId,
			'Site Member'
		);

		// Go to the user management settings page

		await navigateToACSettingsViaURL({
			acPage: ACPage.userManagementPage,
			page,
			projectID: project.groupId,
		});

		const userRow = page.getByRole('row', {name: userEmail});

		// Edit the role from Member to Administrator using the row's edit button

		await userRow.getByRole('button', {name: 'Edit'}).click();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Administrator'}),
			trigger: page.getByRole('button', {name: 'Member'}),
		});

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page, 'Permissions have been changed', {
			autoClose: false,
		});

		// Assert that the user role is now Administrator

		await expect(userRow.getByText('Administrator')).toBeVisible();

		// Bulk-change the role back to Member via the Change Permissions toolbar action

		await userRow.getByRole('checkbox').check();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('button', {name: 'Select Permission'}),
			trigger: page.getByRole('button', {name: 'Change Permissions'}),
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Member'}),
			trigger: page.getByRole('button', {name: 'Select Permission'}),
		});

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page, 'Permissions have been changed', {
			autoClose: false,
		});

		// Assert that the user role is now Member

		await expect(userRow.getByText('Member')).toBeVisible();

		// Cleanup the invited user and the channel

		await apiHelpers.jsonWebServicesOSBFaro.deleteUser(
			user.id,
			project.groupId
		);

		await apiHelpers.jsonWebServicesOSBFaro.deleteChannel(
			`[${channel.id}]`,
			project.groupId
		);
	}
);

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pushNotificationsPagesTest} from '../../../fixtures/pushNotificationsPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';

const test = mergeTests(
	dataApiHelpersTest,
	loginTest(),
	pushNotificationsPagesTest
);

test(
	'JavaScript in a device user name is not executed when viewing the devices',
	{tag: '@LPD-102339'},
	async ({apiHelpers, page, pushNotificationsPage}) => {
		const dialogs: string[] = [];

		page.on('dialog', async (dialog) => {
			dialogs.push(dialog.message());

			await dialog.dismiss();
		});

		// Create a user whose last name carries a script payload. The full name
		// is abbreviated past 75 characters, so keep it short enough to be
		// rendered whole

		const familyName = `${getRandomInt()}<img src=x onerror=alert('XSS')>`;

		const userAccount = await apiHelpers.headlessAdminUser.postUserAccount({
			familyName,
			givenName: 'Xss',
		});

		const companyId = await page.evaluate(() =>
			Liferay.ThemeDisplay.getCompanyId()
		);

		// A device belongs to whoever registers it, so the user has to make
		// the call themselves for their name to reach the devices list

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			rolePermissions: [
				{
					actionIds: ['MANAGE_DEVICES'],
					primaryKey: companyId,
					resourceName: 'com.liferay.push.notifications',
					scope: 1,
				},
			],
		});

		await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
			role.externalReferenceCode,
			userAccount.id
		);

		await apiHelpers.jsonWebServicesPushNotificationsDevice.addPushNotificationsDevice(
			{
				authorization: `Basic ${btoa(`${userAccount.emailAddress}:test`)}`,
			}
		);

		await pushNotificationsPage.goto();

		// The script in the device user name is rendered as text, not executed

		await expect(
			pushNotificationsPage.devicesTable.cell(
				`${userAccount.givenName} ${familyName}`
			)
		).toBeVisible();

		expect(dialogs).toHaveLength(0);
	}
);

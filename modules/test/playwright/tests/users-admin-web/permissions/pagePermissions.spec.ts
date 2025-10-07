/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {portletConfigurationPermissionsPageTest} from '../../../fixtures/portletConfigurationPermissionsPagesTest';

export const test = mergeTests(
	loginTest(),
	portletConfigurationPermissionsPageTest
);

test(
	'Page role permission should be saved when permissions.propagation.enabled is true',
	{tag: ['@LPD-34368']},
	async ({portletConfigurationPermissionsPage}) => {
		await portletConfigurationPermissionsPage.goToEditPagePermissions();

		await expect(async () => {
			await portletConfigurationPermissionsPage.saveButton.click({
				timeout: 1000,
			});
		}).toPass();

		await expect(
			portletConfigurationPermissionsPage.successMessage
		).toBeVisible();
	}
);

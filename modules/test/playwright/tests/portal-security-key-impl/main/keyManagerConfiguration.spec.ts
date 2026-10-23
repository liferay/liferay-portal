/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';

const testWithFeatureFlag = mergeTests(
	featureFlagsTest({
		'LPD-88411': {enabled: true, system: true},
	}),
	loginTest(),
	systemSettingsPageTest
);

const testWithoutFeatureFlag = mergeTests(
	featureFlagsTest({
		'LPD-88411': {enabled: false, system: true},
	}),
	loginTest(),
	systemSettingsPageTest
);

testWithFeatureFlag(
	'Assert that the Key Manager configurations are visible when the feature flag is enabled',
	{tag: '@LPD-111573'},
	async ({systemSettingsPage}) => {
		await systemSettingsPage.goToSystemSetting(
			'Key Manager',
			'Key Manager Configuration'
		);

		await expect(systemSettingsPage.saveButton).toBeVisible();

		await systemSettingsPage.goToSystemSetting(
			'Key Manager',
			'Key Manager Custom Profile Configuration'
		);

		await expect(systemSettingsPage.saveButton).toBeVisible();
	}
);

testWithoutFeatureFlag(
	'Assert that the Key Manager category is hidden when the feature flag is disabled',
	{tag: '@LPD-111573'},
	async ({page, systemSettingsPage}) => {
		await systemSettingsPage.goto();

		await expect(
			page.getByRole('link', {exact: true, name: 'Security Tools'})
		).toBeVisible();

		await expect(
			page.getByRole('link', {exact: true, name: 'Key Manager'})
		).toBeHidden();
	}
);

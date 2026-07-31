/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {instanceSettingsPagesTest} from '../../../fixtures/instanceSettingsPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';

const test = mergeTests(
	featureFlagsTest({
		'LPD-6417': {enabled: true},
	}),
	instanceSettingsPagesTest,
	loginTest(),
	systemSettingsPageTest
);

const testWithoutFeatureFlag = mergeTests(
	featureFlagsTest({
		'LPD-6417': {enabled: false},
	}),
	loginTest(),
	systemSettingsPageTest
);

test(
	'Assert that the audit configuration is rendered on the instance scope',
	{tag: '@LPD-98544'},
	async ({instanceSettingsPage, page}) => {
		await instanceSettingsPage.goToInstanceSetting(
			'Audit',
			'Audit',
			true,
			'Virtual Instance Scope'
		);

		await expect(page.getByLabel('Enabled')).toBeChecked();
		await expect(instanceSettingsPage.saveButton).toBeVisible();
	}
);

test(
	'Assert that the audit message maximum queue size is not rendered on the system scope when the feature flag is enabled',
	{tag: '@LPD-98544'},
	async ({page, systemSettingsPage}) => {
		await systemSettingsPage.goToSystemSetting('Audit', 'Audit');

		await expect(page.getByLabel('Enabled')).toBeVisible();
		await expect(
			page.getByLabel('Audit Message Maximum Queue Size')
		).toBeHidden();
	}
);

testWithoutFeatureFlag(
	'Assert that the audit message maximum queue size is rendered on the system scope when the feature flag is disabled',
	{tag: '@LPD-98544'},
	async ({page, systemSettingsPage}) => {
		await systemSettingsPage.goToSystemSetting('Audit', 'Audit');

		await expect(page.getByLabel('Enabled')).toBeVisible();
		await expect(
			page.getByLabel('Audit Message Maximum Queue Size')
		).toBeVisible();
	}
);

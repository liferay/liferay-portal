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
	'Assert that the database processor configuration is rendered on the instance scope',
	{tag: '@LPD-98545'},
	async ({instanceSettingsPage, page}) => {
		await instanceSettingsPage.goToInstanceSetting(
			'Audit',
			'Audit',
			true,
			'Virtual Instance Scope'
		);

		await expect(
			page.getByRole('heading', {name: 'Database Processor'})
		).toBeVisible();

		await expect(
			page.getByLabel('Enable Database Processor')
		).toBeChecked();
		await expect(page.getByLabel('Buffer Size')).toHaveValue('2000');
		await expect(
			page.getByLabel('Flush Interval in Milliseconds')
		).toHaveValue('60000');
	}
);

test(
	'Assert that the database processor configuration is rendered on the system scope',
	{tag: '@LPD-98545'},
	async ({page, systemSettingsPage}) => {
		await systemSettingsPage.goToSystemSetting('Audit', 'Audit');

		await expect(
			page.getByRole('heading', {name: 'Database Processor'})
		).toBeVisible();

		await expect(
			page.getByLabel('Enable Database Processor')
		).toBeChecked();
		await expect(page.getByLabel('Buffer Size')).toHaveValue('2000');
		await expect(
			page.getByLabel('Flush Interval in Milliseconds')
		).toHaveValue('60000');
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

test(
	'Assert that the database processor has no separate configuration entry on the instance scope',
	{tag: '@LPD-98545'},
	async ({instanceSettingsPage, page}) => {
		await instanceSettingsPage.goto();

		await page.getByRole('link', {exact: true, name: 'Audit'}).click();

		const menubar = page
			.locator('div')
			.filter({hasText: 'Virtual Instance Scope'})
			.locator('+ div')
			.getByRole('menubar');

		await expect(
			menubar.getByRole('menuitem', {exact: true, name: 'Audit'})
		).toBeVisible();

		await expect(
			menubar.getByRole('menuitem', {
				name: 'Persistent Message Audit Message Processor',
			})
		).toBeHidden();
	}
);

testWithoutFeatureFlag(
	'Assert that the database processor has a separate configuration entry on the system scope when the feature flag is disabled',
	{tag: '@LPD-98545'},
	async ({page, systemSettingsPage}) => {
		await systemSettingsPage.goToSystemSetting('Audit', 'Audit');

		await expect(
			page.getByRole('menuitem', {
				name: 'Persistent Message Audit Message Processor',
			})
		).toBeVisible();

		await expect(
			page.getByRole('heading', {name: 'Database Processor'})
		).toBeHidden();
	}
);

test.describe('Database Processor instance configuration', () => {
	test.afterEach(async ({instanceSettingsPage, page}) => {
		await instanceSettingsPage.goToInstanceSetting(
			'Audit',
			'Audit',
			true,
			'Virtual Instance Scope'
		);

		await instanceSettingsPage.checkOption(
			'Enable Database Processor',
			true
		);
		await page.getByLabel('Buffer Size').fill('2000');
		await page.getByLabel('Flush Interval in Milliseconds').fill('60000');

		await instanceSettingsPage.saveAndWaitForAlert();
	});

	test(
		'Assert that the database processor configuration is saved and persisted on the instance scope',
		{tag: '@LPD-98545'},
		async ({instanceSettingsPage, page}) => {
			await instanceSettingsPage.goToInstanceSetting(
				'Audit',
				'Audit',
				true,
				'Virtual Instance Scope'
			);

			await instanceSettingsPage.checkOption(
				'Enable Database Processor',
				false
			);
			await page.getByLabel('Buffer Size').fill('500');
			await page
				.getByLabel('Flush Interval in Milliseconds')
				.fill('30000');

			await instanceSettingsPage.saveAndWaitForAlert();

			await instanceSettingsPage.goToInstanceSetting(
				'Audit',
				'Audit',
				true,
				'Virtual Instance Scope'
			);

			await expect(
				page.getByLabel('Enable Database Processor')
			).not.toBeChecked();
			await expect(page.getByLabel('Buffer Size')).toHaveValue('500');
			await expect(
				page.getByLabel('Flush Interval in Milliseconds')
			).toHaveValue('30000');
		}
	);

	test(
		'Assert that a database processor override on the instance scope does not change the system scope',
		{tag: '@LPD-98545'},
		async ({instanceSettingsPage, page, systemSettingsPage}) => {
			await instanceSettingsPage.goToInstanceSetting(
				'Audit',
				'Audit',
				true,
				'Virtual Instance Scope'
			);

			await page.getByLabel('Buffer Size').fill('500');

			await instanceSettingsPage.saveAndWaitForAlert();

			await systemSettingsPage.goToSystemSetting('Audit', 'Audit');

			await expect(page.getByLabel('Buffer Size')).toHaveValue('2000');
		}
	);
});

test.describe('Database Processor system configuration', () => {
	test.afterEach(async ({page, systemSettingsPage}) => {
		await systemSettingsPage.goToSystemSetting('Audit', 'Audit');

		await systemSettingsPage.checkOption('Enable Database Processor', true);
		await page.getByLabel('Buffer Size').fill('2000');
		await page.getByLabel('Flush Interval in Milliseconds').fill('60000');

		await systemSettingsPage.saveAndWaitForAlert();
	});

	test(
		'Assert that the database processor configuration is saved and persisted on the system scope',
		{tag: '@LPD-98545'},
		async ({page, systemSettingsPage}) => {
			await systemSettingsPage.goToSystemSetting('Audit', 'Audit');

			await systemSettingsPage.checkOption(
				'Enable Database Processor',
				false
			);
			await page.getByLabel('Buffer Size').fill('500');
			await page
				.getByLabel('Flush Interval in Milliseconds')
				.fill('30000');

			await systemSettingsPage.saveAndWaitForAlert();

			await systemSettingsPage.goToSystemSetting('Audit', 'Audit');

			await expect(
				page.getByLabel('Enable Database Processor')
			).not.toBeChecked();
			await expect(page.getByLabel('Buffer Size')).toHaveValue('500');
			await expect(
				page.getByLabel('Flush Interval in Milliseconds')
			).toHaveValue('30000');
		}
	);
});

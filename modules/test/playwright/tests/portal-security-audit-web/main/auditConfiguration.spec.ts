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

test(
	'Assert that the file system processor configuration is rendered on the instance scope',
	{tag: '@LPD-98546'},
	async ({instanceSettingsPage, page}) => {
		await instanceSettingsPage.goToInstanceSetting(
			'Audit',
			'Audit',
			true,
			'Virtual Instance Scope'
		);

		await expect(
			page.getByRole('heading', {name: 'File System Processor'})
		).toBeVisible();

		await expect(
			page.getByLabel('Enable File System Processor')
		).not.toBeChecked();
		await expect(page.getByLabel('Generate Checksum')).not.toBeChecked();
		await expect(page.getByLabel('Output Directory')).not.toHaveValue('');
		await expect(page.getByLabel('Output Format')).toHaveValue('NDJSON');
	}
);

testWithoutFeatureFlag(
	'Assert that the file system processor has no separate configuration entry on the system scope when the feature flag is disabled',
	{tag: '@LPD-98546'},
	async ({page, systemSettingsPage}) => {
		await systemSettingsPage.goToSystemSetting('Audit', 'Audit');

		await expect(
			page.getByRole('menuitem', {
				name: 'File System Audit Message Processor',
			})
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

		await page.getByLabel('Enable Database Processor').setChecked(true);
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

			await page
				.getByLabel('Enable Database Processor')
				.setChecked(false);
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

		await page.getByLabel('Enable Database Processor').setChecked(true);
		await page.getByLabel('Buffer Size').fill('2000');
		await page.getByLabel('Flush Interval in Milliseconds').fill('60000');

		await systemSettingsPage.saveAndWaitForAlert();
	});

	test(
		'Assert that the database processor configuration is saved and persisted on the system scope',
		{tag: '@LPD-98545'},
		async ({page, systemSettingsPage}) => {
			await systemSettingsPage.goToSystemSetting('Audit', 'Audit');

			await page
				.getByLabel('Enable Database Processor')
				.setChecked(false);
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

test.describe('File System Processor instance configuration', () => {
	let defaultOutputDirectory: string;

	test.afterEach(async ({instanceSettingsPage, page}) => {
		await instanceSettingsPage.goToInstanceSetting(
			'Audit',
			'Audit',
			true,
			'Virtual Instance Scope'
		);

		await page.getByLabel('Enable File System Processor').setChecked(false);
		await page.getByLabel('Generate Checksum').setChecked(false);

		if (defaultOutputDirectory !== undefined) {
			await page
				.getByLabel('Output Directory')
				.fill(defaultOutputDirectory);
		}

		await page.getByLabel('Output Format').selectOption('NDJSON');

		await instanceSettingsPage.saveAndWaitForAlert();
	});

	test(
		'Assert that the file system processor configuration is saved and persisted on the instance scope',
		{tag: '@LPD-98546'},
		async ({instanceSettingsPage, page}) => {
			await instanceSettingsPage.goToInstanceSetting(
				'Audit',
				'Audit',
				true,
				'Virtual Instance Scope'
			);

			defaultOutputDirectory = await page
				.getByLabel('Output Directory')
				.inputValue();

			await page
				.getByLabel('Enable File System Processor')
				.setChecked(true);
			await page.getByLabel('Generate Checksum').setChecked(true);
			await page.getByLabel('Output Directory').fill('data/test');
			await page.getByLabel('Output Format').selectOption('CSV');

			await instanceSettingsPage.saveAndWaitForAlert();

			await instanceSettingsPage.goToInstanceSetting(
				'Audit',
				'Audit',
				true,
				'Virtual Instance Scope'
			);

			await expect(
				page.getByLabel('Enable File System Processor')
			).toBeChecked();
			await expect(page.getByLabel('Generate Checksum')).toBeChecked();
			await expect(page.getByLabel('Output Directory')).toHaveValue(
				'data/test'
			);
			await expect(page.getByLabel('Output Format')).toHaveValue('CSV');
		}
	);
});

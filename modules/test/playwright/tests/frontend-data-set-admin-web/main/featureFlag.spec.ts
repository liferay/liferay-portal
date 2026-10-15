/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {featureFlagPagesTest} from '../../feature-flag-web/main/fixtures/featureFlagPagesTest';
import {customDataSetsPageTest} from './fixtures/customDataSetsPageTest';

export const test = mergeTests(
	customDataSetsPageTest,
	featureFlagPagesTest,
	featureFlagsTest({
		'LPS-164563': {enabled: true},
	}),
	loginTest()
);

test.describe('Data Set Manager with Feature Flag Enabled', () => {
	test('Confirm the description in the Feature Flag page @LPS-188590', async ({
		featureFlagsInstanceSettingsPage,
		page,
	}) => {
		await test.step('Navigate to Feature Flag page', async () => {
			await featureFlagsInstanceSettingsPage.goto('Release');
		});

		await test.step('Check that the feature flag description is displayed', async () => {
			await expect(
				page.getByText(
					'Create tables to show data coming from headless resources. Choose the columns to show as well as how data will be paginated. Use a frontend data set cell renderer client extension to customize how data is rendered.'
				)
			).toBeVisible();
		});
	});

	test('Confirm in the application menu, "Data Set" is in the "Object" category @LPS-188590', async ({
		customDataSetsPage,
		page,
	}) => {
		await test.step('Open the application menu and go to the Control Panel tab', async () => {
			await customDataSetsPage.globalMenuPage.goToControlPanel();
		});

		await test.step('Check that "Data Sets" is in the "Object" category', async () => {
			const objectHeading = page.locator('.nav-link.collapse-icon', {
				hasText: 'Object',
			});

			await objectHeading.waitFor();

			await expect(objectHeading.locator('..')).toContainText(
				'Data Sets'
			);
		});
	});

	test('Confirm in Client Extensions, Frontend Data Set Cell Renderer is displayed @LPS-188590', async ({
		customDataSetsPage,
		page,
	}) => {
		await test.step('Navigate to Client Extensions page', async () => {
			await customDataSetsPage.globalMenuPage.goToApplications(
				'Client Extensions'
			);
		});

		await test.step('Open add menu', async () => {
			await page.getByRole('button', {name: 'New'}).first().click();
		});

		await test.step('Check that Frontend Data Set Cell Renderer is displayed', async () => {
			await expect(
				page.getByRole('menuitem', {
					exact: true,
					name: 'Add Frontend Data Set Cell Renderer',
				})
			).toBeVisible();
		});
	});

	test('Confirm Data Set fragment is displayed @LPS-188590', async ({
		page,
	}) => {
		await test.step('Go to home edit page', async () => {
			await page.goto(`/web/guest/home?p_l_mode=edit`);
		});

		await test.step('Check that "Data Set" is not displayed as a fragment', async () => {
			await page
				.getByLabel('Search Fragments and Widgets')
				.fill('Data Set');

			await expect(
				page.getByRole('menuitem', {
					exact: true,
					name: 'Data Set Add Data Set Mark Data Set as Favorite',
				})
			).toBeVisible();
		});
	});
});

export const disabledTest = mergeTests(
	customDataSetsPageTest,
	featureFlagPagesTest,
	featureFlagsTest({
		'LPS-164563': {enabled: false},
	}),
	loginTest()
);

disabledTest.describe('Data Set Manager with Feature Flag Disabled', () => {
	disabledTest(
		'Confirm in the application menu, Data Set is not displayed @LPS-188590',
		async ({customDataSetsPage, page}) => {
			await test.step('Open application menu and go to control panel tab', async () => {
				await customDataSetsPage.globalMenuPage.goToControlPanel();
			});

			await test.step('Check that "Data Sets" is not displayed as a menu item', async () => {
				await expect(
					page.getByRole('menuitem', {
						exact: true,
						name: 'Data Sets',
					})
				).toBeHidden();
			});
		}
	);

	disabledTest(
		'Confirm in Client Extensions, Frontend Data Set Cell Renderer is not displayed @LPS-188590',
		async ({customDataSetsPage, page}) => {
			await test.step('Navigate to Client Extensions page', async () => {
				await customDataSetsPage.globalMenuPage.goToApplications(
					'Client Extensions'
				);
			});

			await test.step('Open add menu', async () => {
				await page.getByRole('button', {name: 'New'}).first().click();
			});

			await test.step('Check that Frontend Data Set Cell Renderer is not displayed', async () => {
				await expect(
					page.getByRole('menuitem', {
						exact: true,
						name: 'Add Frontend Data Set Cell Renderer',
					})
				).toBeHidden();
			});
		}
	);

	disabledTest(
		'Confirm Data Set fragment is not displayed @LPS-188590',
		async ({page}) => {
			await test.step('Go to home edit page', async () => {
				await page.goto(`/web/guest/home?p_l_mode=edit`);
			});

			await test.step('Check that "Data Set" is not displayed as a fragment', async () => {
				await page
					.getByLabel('Search Fragments and Widgets')
					.fill('Data Set');

				await expect(
					page.getByRole('menuitem', {
						exact: true,
						name: 'Data Set Add Data Set Mark Data Set as Favorite',
					})
				).toBeHidden();
			});
		}
	);

	disabledTest(
		'Confirm that the Data Set fragment is displayed when the Feature Flag is enabled through the UI @LPS-188590',
		async ({
			customDataSetsPage,
			featureFlagsInstanceSettingsPage,
			page,
		}) => {
			try {
				await test.step('Navigate to Feature Flag page', async () => {
					await featureFlagsInstanceSettingsPage.goto('Release');
				});

				await test.step('Enable the Data Set Manager feature flag', async () => {
					await featureFlagsInstanceSettingsPage.updateFeatureFlag(
						'LPS-164563',
						true
					);

					const featureFlagToggle =
						await featureFlagsInstanceSettingsPage.getFeatureFlagToggle(
							'LPS-164563'
						);

					await expect(featureFlagToggle).toBeChecked();
				});

				await test.step('Go to home edit page', async () => {
					await page.goto(`/web/guest/home?p_l_mode=edit`);
				});

				await test.step('Check that "Data Set" is not displayed as a fragment', async () => {
					await page
						.getByLabel('Search Fragments and Widgets')
						.fill('Data Set');

					await expect(
						page.getByRole('menuitem', {
							exact: true,
							name: 'Data Set Add Data Set Mark Data Set as Favorite',
						})
					).toBeVisible();
				});
			}
			finally {
				await test.step('Navigate to Feature Flag page', async () => {
					await customDataSetsPage.globalMenuPage.goToHome();
					await featureFlagsInstanceSettingsPage.goto('Release');
				});

				await test.step('Enable the Data Set Manager feature flag', async () => {
					await featureFlagsInstanceSettingsPage.updateFeatureFlag(
						'LPS-164563',
						false
					);

					const featureFlagToggle =
						await featureFlagsInstanceSettingsPage.getFeatureFlagToggle(
							'LPS-164563'
						);

					await expect(featureFlagToggle).toBeChecked({
						checked: false,
					});
				});
			}
		}
	);
});

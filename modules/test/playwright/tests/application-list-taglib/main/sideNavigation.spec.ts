/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {waitForPageToBeLoaded} from '../../../utils/waitForPageToBeLoaded';

const test = mergeTests(
	featureFlagsTest({
		'LPD-36105': {enabled: true},
	}),
	globalMenuPagesTest,
	loginTest()
);

test(
	'The navigation item links have active state based on the current page',
	{tag: '@LPD-73706'},
	async ({globalMenuPage, page}) => {
		await test.step('Click on a navigation item and check if it navigates to the correct page', async () => {
			await globalMenuPage.goToControlPanel();

			const homeItem = page.getByRole('menuitem', {
				name: 'Home',
			});

			await expect(homeItem).toHaveClass(/active/);

			const userGroupsItem = page.getByRole('menuitem', {
				name: 'User Groups',
			});

			await expect(userGroupsItem).not.toHaveClass(/active/);

			await userGroupsItem.click();

			await expect(
				page.getByRole('heading', {name: 'User Groups'})
			).toBeAttached();
			await expect(homeItem).not.toHaveClass(/active/);
			await expect(userGroupsItem).toHaveClass(/active/);
		});
	}
);

test(
	'A user can select a site using the site selector',
	{tag: '@LPD-73706'},
	async ({globalMenuPage, page}) => {
		await test.step('Go to an Applications Panel page', async () => {
			await globalMenuPage.goToApplications();
		});

		await test.step('Open site selector and select a site', async () => {
			const goToOtherSiteButton = page.getByRole('button', {
				name: 'Go to Other Site',
			});

			await clickAndExpectToBeVisible({
				target: page.getByRole('heading', {
					name: 'Select Site',
				}),
				trigger: goToOtherSiteButton,
			});

			await page
				.frameLocator('iframe[title="Select Site"]')
				.getByRole('link', {name: /^Liferay( DXP)?$/})
				.click();

			await waitForPageToBeLoaded(page);
		});
	}
);

test(
	'The toggle button opens/hides the side navigation menu',
	{tag: '@LPD-73706'},
	async ({globalMenuPage, page}) => {
		await test.step('Go to an Applications Panel page', async () => {
			await globalMenuPage.goToApplications();
		});

		await test.step('Click the toggle button and check if navigation is open/hidden', async () => {
			const menu = page.getByLabel('Applications Menu', {exact: true});

			await globalMenuPage.openProductMenu('Applications');

			await expect(menu).toBeVisible();

			await globalMenuPage.closeProductMenu('Applications');

			await expect(menu).not.toBeVisible();
		});
	}
);

test(
	'The side navigation menu visibility persists across page reloads',
	{tag: '@LPD-73706'},
	async ({globalMenuPage, page}) => {
		await test.step('Go to an Applications Panel page', async () => {
			await globalMenuPage.goToApplications();
		});

		const menu = page.getByLabel('Applications Menu', {exact: true});
		const toggler = page.getByTestId('sideNavigationToggler');

		const testCases = [
			{expectedState: false, initialState: true},
			{expectedState: true, initialState: false},
		];

		for (const {expectedState, initialState} of testCases) {
			await test.step(`Set the navigation visibility to ${expectedState} and assert after reload`, async () => {
				await expect(menu).toBeVisible({
					visible: initialState,
				});

				await toggler.click();

				await expect(menu).toBeVisible({
					visible: expectedState,
				});

				await page.reload();

				await waitForPageToBeLoaded(page);

				await expect(menu).toBeVisible({
					visible: expectedState,
				});
			});
		}
	}
);

test(
	'Navigation item groups maintain their state when the page reloads',
	{tag: '@LPD-79369'},
	async ({globalMenuPage, page}) => {
		await globalMenuPage.goToApplications();

		const workflowItem = page.getByRole('menuitem', {name: 'Workflow'});

		try {
			const categories = page.locator('button.collapse-icon');

			const categoriesExpanded = page.locator(
				'button.collapse-icon[aria-expanded="true"]'
			);

			await expect(categories).toHaveCount(
				await categoriesExpanded.count()
			);

			await workflowItem.click();

			await page.reload();

			await waitForPageToBeLoaded(page);

			await expect(workflowItem).toHaveAttribute(
				'aria-expanded',
				'false'
			);
		}
		finally {

			// Reset the state of the navigation item

			await expect(async () => {
				await workflowItem.click();

				await expect(workflowItem).toHaveAttribute(
					'aria-expanded',
					'true'
				);
			}).toPass();
		}
	}
);

test(
	'Escape key does not close the side navigation menu',
	{tag: '@LPD-79543'},
	async ({globalMenuPage, page}) => {
		const menu = page.getByLabel('Applications Menu', {exact: true});

		await test.step('Go to an Applications Panel page', async () => {
			await globalMenuPage.goToApplications();

			await expect(menu).toBeVisible();
		});

		await test.step('Press the Escape key and check if the navigation menu is still visible', async () => {
			await page.keyboard.press('Escape');

			await page.reload();

			await expect(menu).toBeVisible();
		});
	}
);

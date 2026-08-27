/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {closeProductMenu, openProductMenu} from '../../../utils/productMenu';
import {waitForPageToBeLoaded} from '../../../utils/waitForPageToBeLoaded';
import {contentDashboardPagesTest} from '../../content-dashboard-web/main/fixtures/contentDashboardPagesTest';

const test = mergeTests(
	contentDashboardPagesTest,
	globalMenuPagesTest,
	isolatedSiteTest,
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
				.getByRole('link', {exact: true, name: 'Liferay DXP Site'})
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

			await openProductMenu(page);

			await expect(menu).toBeVisible();

			await closeProductMenu(page);

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

		const workflowItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Workflow',
		});

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

test(
	'The side navigation shows a visible divider separating it from the content',
	{tag: '@LPD-93647'},
	async ({globalMenuPage, page}) => {
		await test.step('Go to an Applications Panel page', async () => {
			await globalMenuPage.goToApplications();

			await expect(
				page.getByLabel('Applications Menu', {exact: true})
			).toBeVisible();
		});

		await test.step('Assert the divider border is actually rendered', async () => {
			const {borderRightColor, borderRightStyle, borderRightWidth} =
				await page.getByTestId('sideNavigation').evaluate((element) => {
					const computedStyle = window.getComputedStyle(element);

					return {
						borderRightColor: computedStyle.borderRightColor,
						borderRightStyle: computedStyle.borderRightStyle,
						borderRightWidth: computedStyle.borderRightWidth,
					};
				});

			expect(borderRightStyle).toBe('solid');

			expect(parseFloat(borderRightWidth)).toBeGreaterThan(0);

			expect(borderRightColor).not.toBe('rgba(0, 0, 0, 0)');
		});
	}
);

test(
	'Side navigation remains visible after Liferay.Portlet.refresh call',
	{tag: '@LPD-86410'},
	async ({contentDashboardPage, page, site}) => {
		async function expectSideNavigationToBeRendered() {
			const sideNavigation = page.getByLabel('Applications Menu', {
				exact: true,
			});
			const toggler = page.getByTestId('sideNavigationToggler');

			await expect(sideNavigation).toBeVisible();
			await expect(toggler).toBeVisible();

			await toggler.click();

			await expect(sideNavigation).toBeHidden();

			await toggler.click();

			await expect(sideNavigation).toBeVisible();
		}

		await test.step('Go to Applications > Content Dashboard', async () => {
			await contentDashboardPage.goto(site.friendlyUrlPath);

			await waitForPageToBeLoaded(page);

			await expectSideNavigationToBeRendered();
		});

		const modalTitle = page.getByRole('heading', {
			exact: true,
			name: 'Configuration',
		});

		await test.step('Click on the settings (cog) icon to open the modal', async () => {
			await page
				.locator('button', {
					has: page.locator('svg.lexicon-icon-cog'),
				})
				.click();

			await expect(modalTitle).toBeVisible();
		});

		await test.step('Close the modal and verify side navigation visibility', async () => {
			await page
				.getByRole('button', {exact: true, name: 'Close'})
				.click();

			await expect(modalTitle).toBeHidden();

			await expectSideNavigationToBeRendered();
		});
	}
);

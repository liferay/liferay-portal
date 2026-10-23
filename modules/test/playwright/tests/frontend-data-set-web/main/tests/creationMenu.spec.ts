/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {checkAccessibility} from '../../../../utils/checkAccessibility';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {hoverAndExpectToBeVisible} from '../../../../utils/hoverAndExpectToBeVisible';
import {waitForFDS} from '../../../../utils/waitFor';
import {fdsSamplePageTest} from '../fixtures/fdsSamplePageTest';

const test = mergeTests(
	apiHelpersTest,
	fdsSamplePageTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

const DATA_SET_WRAPPER_SELECTOR = '.data-set-wrapper';
const FOCUS_TRAP_HIDDEN_SELECTOR = '[data-aria-hidden="true"]';
const MENU_SELECTOR = '.dropdown-menu.show:has([aria-haspopup="true"])';
const OPEN_DROPDOWN_SELECTOR = '.dropdown-menu.show';
const SUBMENU_SELECTOR =
	'.dropdown-menu.show:not(:has([aria-haspopup="true"]))';

test.beforeEach(async ({fdsSamplePage, site}) => {
	await fdsSamplePage.setupFDSSampleWidget({site});
});

test(
	'Creation menu shows the nested actions of Java creation menus',
	{tag: '@LPD-111515'},
	async ({fdsSamplePage, page}) => {
		const newButton = page.getByRole('button', {exact: true, name: 'New'});

		await test.step('Classic opens a submenu', async () => {
			await fdsSamplePage.selectTab('Classic');

			await waitForFDS({page});

			const moreMenuItem = page.getByRole('menuitem', {name: 'More'});

			await clickAndExpectToBeVisible({
				target: moreMenuItem,
				trigger: newButton,
			});

			await expect(moreMenuItem).toHaveAttribute('aria-haspopup', 'true');

			await hoverAndExpectToBeVisible({
				target: page.getByRole('menuitem', {name: 'New Wiki Page'}),
				trigger: moreMenuItem,
			});

			await expect(
				page.getByRole('menuitem', {name: 'New Web Content'})
			).toBeVisible();

			await checkAccessibility({
				page,
				selectors: [
					DATA_SET_WRAPPER_SELECTOR,
					MENU_SELECTOR,
					SUBMENU_SELECTOR,
				],
				selectorsToExclude: [FOCUS_TRAP_HIDDEN_SELECTOR],
			});

			await page.keyboard.press('Escape');
		});

		await test.step('Advanced shows a group and hides the actions without permission', async () => {
			await fdsSamplePage.selectTab('Advanced');

			await waitForFDS({page});

			const formsGroup = page.getByRole('group', {name: 'Forms'});

			await clickAndExpectToBeVisible({
				target: formsGroup,
				trigger: newButton,
			});

			await expect(
				formsGroup.getByRole('menuitem', {name: 'New Form'})
			).toBeVisible();
			await expect(
				page.getByRole('menuitem', {name: 'New Task'})
			).toBeVisible();
			await expect(
				page.getByRole('menu').getByRole('separator')
			).toBeVisible();
			await expect(
				page.getByRole('menuitem', {name: 'Open Form'})
			).toBeHidden();
			await expect(
				page.getByRole('menuitem', {name: 'Templates'})
			).toBeHidden();

			await checkAccessibility({
				page,
				selectors: [DATA_SET_WRAPPER_SELECTOR, OPEN_DROPDOWN_SELECTOR],
				selectorsToExclude: [FOCUS_TRAP_HIDDEN_SELECTOR],
			});

			await page.keyboard.press('Escape');
		});

		await test.step('Custom Internal View shows a group', async () => {
			await fdsSamplePage.selectTab('Custom Internal View');

			const dateAndTimeGroup = page.getByRole('group', {
				name: 'Date and Time',
			});

			await clickAndExpectToBeVisible({
				target: dateAndTimeGroup,
				trigger: newButton,
			});

			await expect(
				dateAndTimeGroup.getByRole('menuitem', {name: 'Date'})
			).toBeVisible();
			await expect(
				dateAndTimeGroup.getByRole('menuitem', {name: 'Time'})
			).toBeVisible();

			await page.keyboard.press('Escape');
		});

		await test.step('Minimum shows the only action of a single group as a button', async () => {
			await fdsSamplePage.selectTab('Minimum');

			await waitForFDS({page});

			await expect(
				page.getByRole('button', {name: 'New Entry'})
			).toBeVisible();
			await expect(newButton).toBeHidden();
		});
	}
);

test(
	'Creation menu shows the nested actions of JavaScript creation menus',
	{tag: '@LPD-111515'},
	async ({fdsSamplePage, page}) => {
		const newButton = page.getByRole('button', {exact: true, name: 'New'});

		await test.step('Single Selection opens a submenu from a props transformer', async () => {
			await fdsSamplePage.selectTab('Single Selection');

			await waitForFDS({page});

			const importItemsMenuItem = page.getByRole('menuitem', {
				name: 'Import Items',
			});

			await clickAndExpectToBeVisible({
				target: importItemsMenuItem,
				trigger: newButton,
			});

			await hoverAndExpectToBeVisible({
				target: page.getByRole('menuitem', {name: 'From File'}),
				trigger: importItemsMenuItem,
			});

			await checkAccessibility({
				page,
				selectors: [
					DATA_SET_WRAPPER_SELECTOR,
					MENU_SELECTOR,
					SUBMENU_SELECTOR,
				],
				selectorsToExclude: [FOCUS_TRAP_HIDDEN_SELECTOR],
			});

			await page.keyboard.press('Escape');
		});

		await test.step('React opens a submenu with the keyboard and triggers its action', async () => {
			await fdsSamplePage.selectTab('React');

			await waitForFDS({page});

			const templatesGroup = page.getByRole('group', {name: 'Templates'});

			await clickAndExpectToBeVisible({
				target: templatesGroup,
				trigger: newButton,
			});

			await expect(
				templatesGroup.getByRole('menuitem', {name: 'Blank Sample'})
			).toBeVisible();

			const importSamplesMenuItem = page.getByRole('menuitem', {
				name: 'Import Samples',
			});

			await importSamplesMenuItem.focus();

			await page.keyboard.press('ArrowRight');

			await expect(
				page.getByRole('menuitem', {name: 'From File'})
			).toBeFocused();

			await page.keyboard.press('ArrowDown');

			await expect(
				page.getByRole('menuitem', {name: 'From URL'})
			).toBeFocused();

			let dialogMessage = '';

			page.once('dialog', async (dialog) => {
				dialogMessage = dialog.message();

				await dialog.accept();
			});

			await page.keyboard.press('Enter');

			await expect(() =>
				expect(dialogMessage).toBe('From URL clicked!')
			).toPass();
		});
	}
);

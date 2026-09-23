/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	featureFlagsTest({
		'LPD-57655': {enabled: true},
	}),
	loginTest()
);

test(
	'The Vocabularies view exposes separate Export and Import items for Vocabularies and Tags',
	{tag: '@LPD-88927'},
	async ({page, vocabulariesPage}) => {
		await vocabulariesPage.goto();

		await page.getByRole('button', {name: 'More Actions'}).click();

		await expect(
			page.getByRole('menuitem', {
				exact: true,
				name: 'Export Vocabularies',
			})
		).toBeVisible();
		await expect(
			page.getByRole('menuitem', {
				exact: true,
				name: 'Import Vocabularies',
			})
		).toBeVisible();
		await expect(
			page.getByRole('menuitem', {exact: true, name: 'Export Tags'})
		).toBeVisible();
		await expect(
			page.getByRole('menuitem', {exact: true, name: 'Import Tags'})
		).toBeVisible();

		await expect(
			page.getByRole('menuitem', {
				exact: true,
				name: 'Export/Import Vocabularies',
			})
		).toBeHidden();
		await expect(
			page.getByRole('menuitem', {
				exact: true,
				name: 'Export/Import Tags',
			})
		).toBeHidden();
	}
);

test(
	'The Tags view exposes the same four separate Export and Import items',
	{tag: '@LPD-88927'},
	async ({page, tagsPage}) => {
		await tagsPage.goto();

		await page.getByRole('button', {name: 'More Actions'}).click();

		await expect(
			page.getByRole('menuitem', {
				exact: true,
				name: 'Export Vocabularies',
			})
		).toBeVisible();
		await expect(
			page.getByRole('menuitem', {
				exact: true,
				name: 'Import Vocabularies',
			})
		).toBeVisible();
		await expect(
			page.getByRole('menuitem', {exact: true, name: 'Export Tags'})
		).toBeVisible();
		await expect(
			page.getByRole('menuitem', {exact: true, name: 'Import Tags'})
		).toBeVisible();
	}
);

test(
	'Clicking Export Vocabularies navigates to the dedicated CMS Export/Import page without opening a modal',
	{tag: '@LPD-88927'},
	async ({page, vocabulariesPage}) => {
		await vocabulariesPage.goto();

		await page.getByRole('button', {name: 'More Actions'}).click();
		await page
			.getByRole('menuitem', {exact: true, name: 'Export Vocabularies'})
			.click();

		await expect(page).toHaveURL(/\/export-import/);
		await expect(page.locator('.modal-content')).toBeHidden();
	}
);

test(
	'Clicking Import Tags from the Tags view navigates to the dedicated CMS Export/Import page',
	{tag: '@LPD-88927'},
	async ({page, tagsPage}) => {
		await tagsPage.goto();

		await page.getByRole('button', {name: 'More Actions'}).click();
		await page
			.getByRole('menuitem', {exact: true, name: 'Import Tags'})
			.click();

		await expect(page).toHaveURL(/\/export-import/);
		await expect(page.locator('.modal-content')).toBeHidden();
	}
);

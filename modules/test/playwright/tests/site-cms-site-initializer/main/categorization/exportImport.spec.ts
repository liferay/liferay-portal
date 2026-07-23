/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';

const testWithoutFF = mergeTests(cmsPagesTest, loginTest());

const testWithFF = mergeTests(
	cmsPagesTest,
	featureFlagsTest({
		'LPD-57655': {enabled: true},
	}),
	loginTest()
);

testWithoutFF(
	'Without LPD-57655, the Vocabularies view opens the combined Export/Import Vocabularies action in a modal',
	{tag: '@LPD-88927'},
	async ({page, vocabulariesPage}) => {
		await vocabulariesPage.goto();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				exact: true,
				name: 'Export/Import Vocabularies',
			}),
			trigger: page.getByRole('button', {name: 'More Actions'}),
		});

		await expect(page.locator('.modal-content')).toBeVisible();
	}
);

testWithoutFF(
	'Without LPD-57655, the Tags view opens the combined Export/Import Tags action in a modal',
	{tag: '@LPD-88927'},
	async ({page, tagsPage}) => {
		await tagsPage.goto();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				exact: true,
				name: 'Export/Import Tags',
			}),
			trigger: page.getByRole('button', {name: 'More Actions'}),
		});

		await expect(page.locator('.modal-content')).toBeVisible();
	}
);

testWithFF(
	'With LPD-57655, the Vocabularies view exposes separate Export and Import items for Vocabularies and Tags',
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

testWithFF(
	'With LPD-57655, the Tags view exposes the same four separate Export and Import items',
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

testWithFF(
	'With LPD-57655, clicking Export Vocabularies navigates to the dedicated CMS Export/Import page without opening a modal',
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

testWithFF(
	'With LPD-57655, clicking Import Tags from the Tags view navigates to the dedicated CMS Export/Import page',
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

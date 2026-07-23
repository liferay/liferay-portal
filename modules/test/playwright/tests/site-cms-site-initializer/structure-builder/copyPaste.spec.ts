/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {cmsPagesTest} from '../main/fixtures/cmsPagesTest';
import {structureBuilderPagesTest} from './fixtures/structureBuilderPagesTest';

const test = mergeTests(cmsPagesTest, loginTest(), structureBuilderPagesTest);

test(
	'Fields can be copied and pasted',
	{tag: '@LPD-87275'},
	async ({page, structureBuilderPage}) => {

		// Create structure

		await structureBuilderPage.createStructureFromData({
			label: getRandomString(),
			name: `StructureName${getRandomInt()}`,
			page: structureBuilderPage,
			publish: false,
		});

		// Add fields

		await structureBuilderPage.addField('Text');
		await structureBuilderPage.addField('Numeric');
		await structureBuilderPage.addField('Boolean');

		// Create a repeatable group with Boolean inside

		await structureBuilderPage.createRepeatableGroup({
			fields: [{label: 'Boolean'}],
			label: 'Group 1',
		});

		// Bulk-copy two fields at root

		await page.locator('.treeview-link', {hasText: 'Text'}).click();

		await expect(async () => {
			await page
				.locator('.treeview-link', {hasText: 'Numeric'})
				.click({modifiers: ['Shift'], timeout: 500});

			await expect(page.getByText('2 Items Selected')).toBeVisible({
				timeout: 500,
			});
		}).toPass();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {exact: true, name: 'Copy'}),
			trigger: page.getByLabel('Selection Options'),
		});

		// Paste them inside the repeatable group

		await structureBuilderPage.clickFieldAction(
			{label: 'Group 1'},
			'Paste'
		);

		// Group 1 should now hold Boolean + the pasted Text and Numeric

		await expect(
			page.locator('.treeview-link', {hasText: 'Text'})
		).toHaveCount(2);

		await expect(
			page.locator('.treeview-link', {hasText: 'Numeric'})
		).toHaveCount(2);

		// Pasting on an invalid target shows an error toast

		await structureBuilderPage.selectFields([{label: 'Text'}]);

		await page.keyboard.press('Control+v');

		await expect(
			page.getByText(
				'Items could not be pasted because the target is not allowed.'
			)
		).toBeVisible();
	}
);

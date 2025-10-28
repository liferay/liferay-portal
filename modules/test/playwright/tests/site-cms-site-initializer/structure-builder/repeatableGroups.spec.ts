/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {cmsPagesTest} from '../main/fixtures/cmsPagesTest';
import {structureBuilderPagesTest} from './fixtures/structureBuilderPagesTest';

const test = mergeTests(
	cmsPagesTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
		'LPS-179669': {enabled: true},
	}),
	loginTest(),
	pageEditorPagesTest,
	structureBuilderPagesTest
);

test(
	'Groups can be created, persisted and ungrouped',
	{
		tag: '@LPD-50378',
	},
	async ({page, structureBuilderPage}) => {

		// Create structure

		const id = await structureBuilderPage.createStructureFromData({
			label: getRandomString(),
			name: `StructureName${getRandomInt()}`,
			page: structureBuilderPage,
			publish: false,
		});

		// Add fields

		await structureBuilderPage.addField('Text');
		await structureBuilderPage.addField('Date');
		await structureBuilderPage.addField('Decimal');

		// Create repeatable group with two of them

		await structureBuilderPage.createRepeatableGroup({
			fields: [{label: 'Text'}, {label: 'Date'}],
			label: 'Repeatable Group 1',
		});

		// Check recently added group is expanded by default

		await expect(
			page.locator('.treeview-link', {hasText: 'Text'})
		).toBeVisible();

		await expect(
			page.locator('.treeview-link', {hasText: 'Date'})
		).toBeVisible();

		// Create another group inside the first one

		await structureBuilderPage.createRepeatableGroup({
			fields: [{label: 'Date'}],
			label: 'Repeatable Group 2',
		});

		// Assert correct label style

		await expect(
			page.locator('.label-success', {hasText: 'Repeatable Group'})
		).toBeVisible();

		// Check groups are persisted

		await structureBuilderPage.publishStructure();

		await structureBuilderPage.editStructure(id);

		await expect(
			page.locator('.treeview-link', {hasText: 'Repeatable Group 1'})
		).toBeVisible();

		await structureBuilderPage.expandField({
			label: 'Repeatable Group 1',
		});

		await expect(
			page.locator('.treeview-link', {hasText: 'Repeatable Group 2'})
		).toBeVisible();

		// Add a new group and ungroup it

		await structureBuilderPage.addField('Boolean');

		await structureBuilderPage.createRepeatableGroup({
			fields: [{label: 'Boolean'}],
			label: 'Repeatable Group 3',
		});

		await structureBuilderPage.clickFieldAction(
			{label: 'Repeatable Group 3'},
			'Ungroup'
		);

		await expect(
			page.locator('.treeview-link', {hasText: 'Repeatable Group 2'})
		).toBeVisible();

		await expect(
			page.locator('.treeview-link', {hasText: 'Boolean'})
		).toBeVisible();
	}
);

test(
	'Check restrictions for group creation',
	{
		tag: '@LPD-50378',
	},
	async ({page, structureBuilderPage}) => {

		// Create structure

		const id = await structureBuilderPage.createStructureFromData({
			label: getRandomString(),
			name: `StructureName${getRandomInt()}`,
			page: structureBuilderPage,
			publish: false,
		});

		// Add fields

		await structureBuilderPage.addField('Text');
		await structureBuilderPage.addField('Date');
		await structureBuilderPage.addField('Decimal');
		await structureBuilderPage.addField('Numeric');
		await structureBuilderPage.addField('Boolean');

		// Create a repeatable group and delete one field

		await structureBuilderPage.createRepeatableGroup({
			fields: [{label: 'Numeric'}, {label: 'Boolean'}],
			label: 'Repeatable Group 0',
		});

		await structureBuilderPage.deleteFields([{label: 'Boolean'}]);

		// Check a group can't be created if there's only one field

		await structureBuilderPage.selectFields([{label: 'Numeric'}]);

		await structureBuilderPage.clickFieldAction(
			{label: 'Numeric'},
			'Create Repeatable Group'
		);

		await clickAndExpectToBeVisible({
			target: page.getByText(
				'The repeatable group cannot be created because at least one field is required.'
			),
			trigger: page.getByRole('menuitem', {
				name: 'Create Repeatable Group',
			}),
		});

		await clickAndExpectToBeHidden({
			target: page.getByText(
				'The repeatable group cannot be created because at least one field is required.'
			),
			trigger: page.locator('.modal-footer').getByText('Done'),
		});

		// Delete the group

		await structureBuilderPage.deleteFields([
			{label: 'Repeatable Group 0'},
		]);

		// Create repeatable group with two of them

		await structureBuilderPage.createRepeatableGroup({
			fields: [{label: 'Text'}, {label: 'Date'}],
			label: 'Repeatable Group 1',
		});

		// Check a group can't be created with fields that have different parent

		await structureBuilderPage.selectFields([
			{label: 'Date'},
			{label: 'Decimal'},
		]);

		await clickAndExpectToBeVisible({
			target: page.getByRole('menuitem', {
				name: 'Create Repeatable Group',
			}),
			trigger: page.getByLabel('Selection Options'),
		});

		await clickAndExpectToBeVisible({
			target: page.getByText(
				'A repeatable group requires all selected items to be at the same hierarchy level. Adjust your selection and try again.'
			),
			trigger: page.getByRole('menuitem', {
				name: 'Create Repeatable Group',
			}),
		});

		await clickAndExpectToBeHidden({
			target: page.getByText(
				'A repeatable group requires all selected items to be at the same hierarchy level. Adjust your selection and try again.'
			),
			trigger: page.locator('.modal-footer').getByText('Done'),
		});

		// Check a group can't be created with system fields

		await structureBuilderPage.publishStructure();

		await structureBuilderPage.selectFields([
			{label: 'Title'},
			{label: 'Decimal'},
		]);

		await clickAndExpectToBeVisible({
			target: page.getByRole('menuitem', {
				name: 'Create Repeatable Group',
			}),
			trigger: page.getByLabel('Selection Options'),
		});

		await clickAndExpectToBeVisible({
			target: page.getByText(
				'The repeatable group cannot be created because one or more fields of the selection are system fields.'
			),
			trigger: page.getByRole('menuitem', {
				name: 'Create Repeatable Group',
			}),
		});

		await clickAndExpectToBeHidden({
			target: page.getByText(
				'The repeatable group cannot be created because one or more fields of the selection are system fields.'
			),
			trigger: page.locator('.modal-footer').getByText('Done'),
		});

		// Check we can't ungroup the published group

		await structureBuilderPage.publishStructure();

		await structureBuilderPage.editStructure(id);

		await structureBuilderPage.clickFieldAction(
			{label: 'Repeatable Group 1'},
			'Ungroup'
		);

		await page
			.getByText(
				'The ungroup action cannot be done because this repeatable group is already published.'
			)
			.waitFor();

		await clickAndExpectToBeHidden({
			target: page.getByText(
				'The ungroup action cannot be done because this repeatable group is already published.'
			),
			trigger: page.locator('.modal-footer').getByText('Done'),
		});
	}
);

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {cmsPagesTest} from '../main/fixtures/cmsPagesTest';
import {structureBuilderPagesTest} from './fixtures/structureBuilderPagesTest';

const test = mergeTests(
	cmsPagesTest,
	loginTest(),
	objectPagesTest,
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
		await structureBuilderPage.addField('Decimal');

		// Create repeatable group with two of them

		await structureBuilderPage.createRepeatableGroup({
			fields: [{label: 'Text'}],
			label: 'Repeatable Group 1',
		});

		// Check recently added group is expanded by default

		await expect(
			page.locator('.treeview-link', {hasText: 'Text'})
		).toBeVisible();

		// Add another field directly inside the group

		await structureBuilderPage.addField('Date', {
			label: 'Repeatable Group 1',
		});

		// Create another group inside the first one

		await structureBuilderPage.createRepeatableGroup({
			fields: [{label: 'Date'}],
			label: 'Repeatable Group 2',
		});

		// Assert correct label style

		await expect(
			page.locator('.label-inverse-success', {
				hasText: 'Repeatable Group',
			})
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

		// Assert keyboard behavior

		await structureBuilderPage.selectFields([
			{label: 'Repeatable Group 3'},
		]);

		await page
			.getByRole('treeitem', {name: 'Repeatable Group 3'})
			.getByRole('button', {name: 'Field Options'})
			.click();

		await page.getByRole('menuitem', {name: 'Ungroup'}).press('Enter');

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
			label: 'Group 0',
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

		await structureBuilderPage.deleteFields([{label: 'Group 0'}]);

		// Create repeatable group with two of them

		await structureBuilderPage.createRepeatableGroup({
			fields: [{label: 'Text'}, {label: 'Date'}],
			label: 'Group 1',
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

		// Check we can create a group with published fields by confirming action

		await structureBuilderPage.publishStructure();

		await structureBuilderPage.clickFieldAction(
			{label: 'Decimal'},
			'Create Repeatable Group'
		);

		await page
			.getByText(
				'Creating a repeatable group with published fields will permanently delete existing field data after publishing the structure.'
			)
			.waitFor();

		await clickAndExpectToBeHidden({
			target: page.getByText(
				'The repeatable group cannot be created because one or more fields of the selection are system fields.'
			),
			trigger: page
				.locator('.modal-footer')
				.getByText('Create Repeatable Group'),
		});

		await expect(
			page.locator('.treeview-link', {
				hasText: 'Repeatable Group',
			})
		).toBeVisible();

		// Publish and check we can't ungroup the published group

		await expect(async () => {
			await structureBuilderPage.publishButton.click({timeout: 1000});

			await expect(
				page.getByText(
					'You have made changes to the content structure that may impact existing stored data once published'
				)
			).toBeVisible({timeout: 2000});

			await page
				.locator('.modal-footer')
				.getByText('Publish')
				.click({timeout: 1000});

			await waitForAlert(page, 'published successfully', {
				timeout: 10000,
			});

			await page
				.locator('.modal-footer')
				.waitFor({state: 'hidden', timeout: 5000});
		}).toPass();

		await structureBuilderPage.editStructure(id);

		await structureBuilderPage.clickFieldAction(
			{label: 'Group 1'},
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

test(
	'Repeatable groups object definitions are deleted when updating or deleting the structure',
	{
		tag: '@LPD-72877',
	},
	async ({
		page,
		structureBuilderPage,
		structuresPage,
		viewObjectDefinitionsPage,
	}) => {

		// Create structure

		const structureLabel = getRandomString();

		await structureBuilderPage.createStructureFromData({
			autoDelete: false,
			label: structureLabel,
			name: `StructureName${getRandomInt()}`,
			page: structureBuilderPage,
		});

		// Add fields

		await structureBuilderPage.addField('Text');
		await structureBuilderPage.addField('Date');

		// Create nested repeatable groups

		const group1Label = getRandomString();
		const group2Label = getRandomString();

		await structureBuilderPage.createRepeatableGroup({
			fields: [{label: 'Text'}],
			label: group1Label,
		});

		await structureBuilderPage.createRepeatableGroup({
			fields: [{label: 'Date'}],
			label: group2Label,
		});

		// Publish

		await structureBuilderPage.publishStructure();

		// Now delete one group, publish and check object definition was deleted as well

		await structureBuilderPage.deleteFields([{label: group2Label}]);

		await expect(async () => {
			await structureBuilderPage.publishButton.click({timeout: 1000});

			await expect(
				page.getByText(
					'You have made changes to the content structure that may impact existing stored data once published'
				)
			).toBeVisible({timeout: 2000});

			await page
				.locator('.modal-footer')
				.getByText('Publish')
				.click({timeout: 1000});

			await waitForAlert(page, 'published successfully', {
				timeout: 5000,
			});

			await page
				.locator('.modal-footer')
				.waitFor({state: 'hidden', timeout: 5000});
		}).toPass();

		await structureBuilderPage.publishStructure();

		await viewObjectDefinitionsPage.goto();

		await expect(async () => {
			await viewObjectDefinitionsPage.openObjectFolder(
				'CMS Structure Repeatable Groups'
			);

			await expect(
				page.getByRole('button', {name: group1Label})
			).toBeVisible();
		}).toPass();

		await expect(
			page.getByRole('button', {name: group2Label})
		).not.toBeVisible();

		// Now go to Structures admin, delete the structure and check
		// the other object definition was also deleted

		await structuresPage.goto();

		await expect(async () => {
			await structuresPage.execItemAction({
				action: 'Delete',
				filter: structureLabel,
				timeout: 1000,
			});

			await page
				.getByText(
					'Deleting a content structure will also remove all of its associated entries'
				)
				.waitFor({timeout: 2000});

			await page
				.getByPlaceholder('Confirm Content Structure Name')
				.fill(structureLabel, {timeout: 1000});

			await page
				.locator('.modal-footer')
				.getByText('Delete')
				.click({timeout: 1000});

			await waitForAlert(page, 'was deleted successfully', {
				timeout: 2000,
			});
		}).toPass();

		await viewObjectDefinitionsPage.goto();

		await page
			.locator('[class*="card-header"]', {hasText: 'Default'})
			.first()
			.waitFor();

		await expect(async () => {
			await viewObjectDefinitionsPage.openObjectFolder(
				'CMS Structure Repeatable Groups',
				{timeout: 1000}
			);

			await page
				.locator('[class*="card-header"]', {
					hasText: 'CMS Structure Repeatable Groups',
				})
				.first()
				.waitFor({timeout: 1000});

			await expect(
				page.getByRole('button', {name: group1Label})
			).not.toBeVisible({
				timeout: 1000,
			});
		}).toPass();
	}
);

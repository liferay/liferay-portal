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
import {waitForAlert} from '../../../utils/waitForAlert';
import {cmsPagesTest} from '../main/fixtures/cmsPagesTest';
import {structureBuilderPagesTest} from './fixtures/structureBuilderPagesTest';
import {StructureBuilderPage} from './pages/StructureBuilderPage';

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
	'Can reference several structures and they are persisted',
	{
		tag: '@LPD-49645',
	},
	async ({page, structureBuilderPage}) => {
		const label1 = getRandomString();
		const label2 = getRandomString();
		const label3 = getRandomString();
		const label4 = getRandomString();

		const name1 = `StructureName${getRandomInt()}`;
		const name2 = `StructureName${getRandomInt()}`;

		// Create three structures, one of them in draft

		await structureBuilderPage.createStructureFromData({
			label: label1,
			name: name1,
			page: structureBuilderPage,
		});

		await structureBuilderPage.createStructureFromData({
			label: label2,
			name: name2,
			page: structureBuilderPage,
		});

		await structureBuilderPage.createStructureFromData({
			label: label3,
			page: structureBuilderPage,
			publish: false,
		});

		// Create another one and reference the first two

		const id4 = await structureBuilderPage.createStructureFromData({
			label: label4,
			page: structureBuilderPage,
		});

		await structureBuilderPage.addReferencedStructures([label1, label2]);

		// Assert correct label style

		await expect(
			page.locator('.label-warning', {
				hasText: 'Referenced Content Structure',
			})
		).toBeVisible();

		// Check the one in draft can't be referenced

		await expect(async () => {
			await clickAndExpectToBeVisible({
				target: page.getByRole('menuitem', {
					exact: true,
					name: 'Referenced Content Structure',
				}),
				trigger: page.getByLabel('Add Field'),
			});

			await clickAndExpectToBeVisible({
				target: page.locator('.modal-title', {
					hasText: 'Referenced Content Structure',
				}),
				timeout: 2000,
				trigger: page.getByRole('menuitem', {
					exact: true,
					name: 'Referenced Content Structure',
				}),
			});

			await page.getByLabel('Structures').click({timeout: 1000});

			await expect(
				page.getByRole('option', {name: label1})
			).toBeVisible();

			await expect(
				page.getByRole('option', {name: label3})
			).not.toBeVisible();

			// Check we can't click Add without structures

			await page
				.locator('.modal-title', {
					hasText: 'Referenced Content Structure',
				})
				.click({timeout: 500});

			await clickAndExpectToBeVisible({
				target: page
					.locator('.modal-body')
					.getByText('This field is required'),
				trigger: page.locator('.modal-footer').getByText('Add'),
			});

			// Close modal

			await clickAndExpectToBeHidden({
				target: page.locator('.modal-title', {
					hasText: 'Referenced Content Structure',
				}),
				timeout: 2000,
				trigger: page.locator('.modal-header .close'),
			});
		}).toPass();

		// Publish the structure

		await structureBuilderPage.publishStructure();

		// Check everything is persisted

		await structureBuilderPage.editStructure(id4);

		await expect(
			page.locator('.treeview-link', {hasText: label1})
		).toBeVisible();

		await expect(
			page.locator('.treeview-link', {hasText: label2})
		).toBeVisible();

		// Select Referenced Content Structures and check correct values are shown

		await structureBuilderPage.selectFields([{label: label1}]);

		await expect(page.getByLabel('Content Structure Name')).toHaveValue(
			name1
		);

		await structureBuilderPage.selectFields([{label: label2}]);

		await expect(page.getByLabel('Content Structure Name')).toHaveValue(
			name2
		);
	}
);

test(
	'Can edit Referenced Content Structure in another tab',
	{
		tag: '@LPD-49645',
	},
	async ({apiHelpers, context, page, structureBuilderPage}) => {
		const label1 = getRandomString();
		const label2 = getRandomString();

		// Create one structure

		await structureBuilderPage.createStructureFromData({
			label: label1,
			page: structureBuilderPage,
		});

		// Create another one and reference the first one

		await structureBuilderPage.createStructureFromData({
			label: label2,
			page: structureBuilderPage,
		});

		await structureBuilderPage.addReferencedStructures([label1]);

		// Check we can't edit Referenced Content Structure

		await structureBuilderPage.selectFields([{label: label1}]);

		await expect(page.getByLabel('Content Structure Name')).toBeDisabled();
		await expect(page.getByLabel('ERC')).toBeDisabled();
		await expect(structureBuilderPage.spaceSelector).toBeDisabled();

		// Check we can't edit Referenced Content Structure fields

		await structureBuilderPage.selectFields([{label: 'Title', nth: 1}]);

		await expect(
			page.getByRole('button', {name: 'Field Options'})
		).not.toBeVisible();

		await expect(page.getByLabel('Label')).toBeDisabled();
		await expect(page.getByLabel('ERC')).toBeDisabled();
		await expect(page.getByLabel('Field Name')).toBeDisabled();

		// Publish the structure

		await structureBuilderPage.publishStructure();

		// Edit Referenced Content Structure in another tab

		const pagePromise = context.waitForEvent('page');

		await structureBuilderPage.selectFields([{label: label1}]);

		await structureBuilderPage.clickFieldAction({label: label1}, 'Edit');

		const newPage = await pagePromise;

		const newStructureBuilderPage = new StructureBuilderPage(
			newPage,
			apiHelpers
		);

		await newPage.locator('.component-tbar').getByText(label1).waitFor();

		// Add new fields and publish

		await newStructureBuilderPage.addField('Date');
		await newStructureBuilderPage.addField('Long Text');

		await expect(async () => {
			await newStructureBuilderPage.publishButton.click({
				timeout: 500,
			});

			await expect(
				newPage.locator('.modal-title', {hasText: 'Publish'})
			).toBeVisible({timeout: 3000});

			await newPage
				.getByText('Publish and Propagate')
				.click({timeout: 500});

			await waitForAlert(newPage, 'published', {timeout: 2000});
		}).toPass();

		// Check in first structure that the tree is updated with the new field

		await structureBuilderPage.expandField({label: label1});

		const dateTreeItem = page.locator('.treeview-link', {
			hasText: 'Date',
		});

		await expect(dateTreeItem).toBeVisible();

		// Check we can't delete Referenced Content Structure fields

		await structureBuilderPage.selectFields([{label: 'Date'}]);

		await expect(
			dateTreeItem.getByLabel('Field Options')
		).not.toBeVisible();

		// Change field and check correct values are shown

		await structureBuilderPage.selectFields([{label: 'Long Text'}]);

		await expect(page.getByLabel('Label')).toHaveValue('Long Text');

		await structureBuilderPage.selectFields([{label: 'Date'}]);

		await expect(page.getByLabel('Label')).toHaveValue('Date');
	}
);

test(
	'Can not do references that cause circular dependencies',
	{
		tag: '@LPD-50628',
	},
	async ({page, structureBuilderPage}) => {
		const labelA = getRandomString();
		const labelB = getRandomString();
		const labelC = getRandomString();
		const labelD = getRandomString();

		// Create four structures, one of them in draft

		const idA = await structureBuilderPage.createStructureFromData({
			erc: labelA,
			label: labelA,
			page: structureBuilderPage,
		});

		await structureBuilderPage.createStructureFromData({
			erc: labelB,
			label: labelB,
			page: structureBuilderPage,
		});

		const idC = await structureBuilderPage.createStructureFromData({
			erc: labelC,
			label: labelC,
			page: structureBuilderPage,
		});

		const idD = await structureBuilderPage.createStructureFromData({
			erc: labelD,
			label: labelD,
			page: structureBuilderPage,
		});

		// C will reference A and D will reference C

		await structureBuilderPage.editStructure(idC);

		await structureBuilderPage.addReferencedStructures([labelA]);

		await structureBuilderPage.publishStructure();

		await structureBuilderPage.editStructure(idD);

		await structureBuilderPage.addReferencedStructures([labelC]);

		await structureBuilderPage.publishStructure();

		// Now edit A and check we can reference B, but not C or D

		await structureBuilderPage.editStructure(idA);

		await clickAndExpectToBeVisible({
			target: page.getByRole('menuitem', {
				exact: true,
				name: 'Referenced Content Structure',
			}),
			trigger: page.getByLabel('Add Field'),
		});

		await clickAndExpectToBeVisible({
			target: page.locator('.modal-title', {
				hasText: 'Referenced Content Structure',
			}),
			timeout: 2000,
			trigger: page.getByRole('menuitem', {
				exact: true,
				name: 'Referenced Content Structure',
			}),
		});

		await clickAndExpectToBeVisible({
			target: page.getByRole('option', {name: labelB}),
			trigger: page.getByLabel('Structures'),
		});

		await expect(
			page.getByRole('option', {name: labelC})
		).not.toBeVisible();

		await expect(
			page.getByRole('option', {name: labelD})
		).not.toBeVisible();
	}
);

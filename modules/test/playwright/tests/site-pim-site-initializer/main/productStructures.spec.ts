/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {cmsPagesTest} from '../../site-cms-site-initializer/main/fixtures/cmsPagesTest';
import {structureBuilderPagesTest} from '../../site-cms-site-initializer/structure-builder/fixtures/structureBuilderPagesTest';
import {pimPagesTest} from './fixtures/pimPagesTest';

const test = mergeTests(
	cmsPagesTest,
	loginTest(),
	pimPagesTest,
	structureBuilderPagesTest
);

test(
	'Create a product structure from the basic product and use its own field',
	{tag: ['@LPD-99448', '@LPD-105922']},
	async ({
		productPage,
		productStructuresPage,
		productsPage,
		structureBuilderPage,
	}) => {
		const fieldLabel = `Packaging Note ${getRandomInt()}`;
		const fieldName = `packagingNote${getRandomInt()}`;
		const productTitle = getRandomString();
		const structureLabel = `ProductType${getRandomInt()}`;

		try {
			await productStructuresPage.goto();

			await test.step('The new structure starts from the basic product', async () => {
				await expect(
					productStructuresPage.getTreeItem('Units of Measure')
				).toBeVisible();
				await expect(
					productStructuresPage.getTreeItem('Details')
				).toBeVisible();

				await structureBuilderPage.expandField({label: 'Details'});

				await structureBuilderPage.checkIsParent({
					child: {label: 'Image'},
					parent: {label: 'Images'},
				});
				await structureBuilderPage.checkIsParent({
					child: {label: 'Attachment'},
					parent: {label: 'Attachments'},
				});
			});

			await test.step('Add a field to the Units of Measure tab', async () => {
				await structureBuilderPage.addField('Text', {
					label: 'Units of Measure',
				});

				await structureBuilderPage.changeFieldSettings({
					label: fieldLabel,
					name: fieldName,
				});

				await structureBuilderPage.checkIsParent({
					child: {label: fieldLabel},
					parent: {label: 'Units of Measure'},
				});
			});

			await structureBuilderPage.selectStructure();

			await structureBuilderPage.changeStructureSettings({
				label: structureLabel,
			});

			await structureBuilderPage.saveStructure();

			await structureBuilderPage.publishStructure();

			await test.step('A product of the new type shows the field in the Units of Measure tab', async () => {
				await productsPage.goto();

				await productsPage.openNewProductEditor(
					'Default',
					structureLabel
				);

				await clickAndExpectToBeVisible({
					target: productPage.title,
					trigger: productPage.getTab('Details'),
				});

				await productPage.sku.fill(getRandomString());
				await productPage.title.fill(productTitle);

				await expect(
					productPage.getRepeatableGroup('Images')
				).toBeVisible();
				await expect(
					productPage.getRepeatableGroup('Attachments')
				).toBeVisible();

				await clickAndExpectToBeVisible({
					target: productPage.unitOfMeasureName,
					trigger: productPage.getTab('Units of Measure'),
				});

				await expect(productPage.getField(fieldName)).toBeVisible();

				await productPage.getField(fieldName).fill('Fragile');

				await productPage.save();

				await expect(
					productsPage.getProduct(productTitle)
				).toBeVisible();
			});

			await test.step('The field value is persisted', async () => {
				await productsPage.goto();

				await productsPage.openProductEditor(productTitle);

				await clickAndExpectToBeVisible({
					target: productPage.unitOfMeasureName,
					trigger: productPage.getTab('Units of Measure'),
				});

				await expect(productPage.getField(fieldName)).toHaveValue(
					'Fragile'
				);
			});
		}
		finally {
			await productsPage.goto();

			await productsPage.deleteProduct(productTitle);
		}
	}
);

test(
	'Create a product structure with its own tab from the basic product',
	{tag: ['@LPD-105923']},
	async ({
		productPage,
		productStructuresPage,
		productsPage,
		structureBuilderPage,
	}) => {
		const fieldLabel = `Warehouse Note ${getRandomInt()}`;
		const fieldName = `warehouseNote${getRandomInt()}`;
		const productTitle = getRandomString();
		const structureLabel = `ProductType${getRandomInt()}`;
		const tabLabel = `Logistics ${getRandomInt()}`;

		try {
			await productStructuresPage.goto();

			await test.step('The new structure starts from the basic product', async () => {
				await expect(
					productStructuresPage.getTreeItem('Details')
				).toBeVisible();
				await expect(
					productStructuresPage.getTreeItem('Units of Measure')
				).toBeVisible();
			});

			await test.step('Add a tab holding a new field', async () => {
				await productStructuresPage.addTab(tabLabel);

				await structureBuilderPage.addField('Text', {label: tabLabel});

				await structureBuilderPage.changeFieldSettings({
					label: fieldLabel,
					name: fieldName,
				});

				await structureBuilderPage.checkIsParent({
					child: {label: fieldLabel},
					parent: {label: tabLabel},
				});
			});

			await structureBuilderPage.selectStructure();

			await structureBuilderPage.changeStructureSettings({
				label: structureLabel,
			});

			await structureBuilderPage.saveStructure();

			await structureBuilderPage.publishStructure();

			await test.step('A product of the new type renders the new tab and its field', async () => {
				await productsPage.goto();

				await productsPage.openNewProductEditor(
					'Default',
					structureLabel
				);

				await expect(productPage.tabs).toHaveText([
					'Details',
					'Units of Measure',
					tabLabel,
					'Relationships',
				]);

				await clickAndExpectToBeVisible({
					target: productPage.title,
					trigger: productPage.getTab('Details'),
				});

				await productPage.sku.fill(getRandomString());
				await productPage.title.fill(productTitle);

				await clickAndExpectToBeVisible({
					target: productPage.getField(fieldName),
					trigger: productPage.getTab(tabLabel),
				});

				await productPage.getField(fieldName).fill('Aisle 3');

				await productPage.save();

				await expect(
					productsPage.getProduct(productTitle)
				).toBeVisible();
			});
		}
		finally {
			await productsPage.goto();

			await productsPage.deleteProduct(productTitle);
		}
	}
);

test(
	'Make a group inside a tab of a product structure repeatable',
	{tag: ['@LPD-114145']},
	async ({
		page,
		productPage,
		productStructuresPage,
		productsPage,
		structureBuilderPage,
	}) => {
		const fieldLabel = `Caption ${getRandomInt()}`;
		const groupLabel = `Images ${getRandomInt()}`;
		const productTitle = getRandomString();
		const structureLabel = `ProductType${getRandomInt()}`;
		const tabLabel = `Media ${getRandomInt()}`;

		try {
			await productStructuresPage.goto();

			await test.step('Create a repeatable group inside a new tab', async () => {
				await productStructuresPage.addTab(tabLabel);

				await structureBuilderPage.addField('Text', {label: tabLabel});

				await structureBuilderPage.changeFieldSettings({
					label: fieldLabel,
					name: `caption${getRandomInt()}`,
				});

				await productStructuresPage.createRepeatableGroup({
					fieldLabel,
					label: groupLabel,
				});

				await structureBuilderPage.checkIsParent({
					child: {label: groupLabel},
					parent: {label: tabLabel},
				});
			});

			await structureBuilderPage.selectStructure();

			await structureBuilderPage.changeStructureSettings({
				label: structureLabel,
			});

			const structureId = await structureBuilderPage.saveStructure();

			await structureBuilderPage.publishStructure();

			await test.step('The repeatable group is still inside its tab after a reload', async () => {
				await structureBuilderPage.editStructure(structureId);

				await structureBuilderPage.expandField({label: tabLabel});

				await structureBuilderPage.checkIsParent({
					child: {label: groupLabel},
					parent: {label: tabLabel},
				});

				await structureBuilderPage.expandField({label: groupLabel});

				await structureBuilderPage.checkIsParent({
					child: {label: fieldLabel},
					parent: {label: groupLabel},
				});
			});

			const captions = page.getByRole('textbox', {name: fieldLabel});

			await test.step('A product of the new type adds entries to the repeatable group in its tab', async () => {
				await productsPage.goto();

				await productsPage.openNewProductEditor(
					'Default',
					structureLabel
				);

				await clickAndExpectToBeVisible({
					target: productPage.title,
					trigger: productPage.getTab('Details'),
				});

				await productPage.sku.fill(getRandomString());
				await productPage.title.fill(productTitle);

				await clickAndExpectToBeVisible({
					target: captions.first(),
					trigger: productPage.getTab(tabLabel),
				});

				await captions.first().fill('Front');

				await page.getByRole('button', {name: 'Add New'}).click();

				await expect(captions).toHaveCount(2);

				await captions.last().fill('Back');

				await productPage.save();

				await expect(
					productsPage.getProduct(productTitle)
				).toBeVisible();
			});

			await test.step('The entries are persisted', async () => {
				await productsPage.goto();

				await productsPage.openProductEditor(productTitle);

				await clickAndExpectToBeVisible({
					target: captions.first(),
					trigger: productPage.getTab(tabLabel),
				});

				await expect(captions).toHaveCount(2);
				await expect(captions.first()).toHaveValue('Front');
				await expect(captions.last()).toHaveValue('Back');
			});
		}
		finally {
			await productsPage.goto();

			await productsPage.deleteProduct(productTitle);
		}
	}
);

test(
	'Show a repeatable group at the first level of a product structure in the tab named after the structure',
	{tag: ['@LPD-114145']},
	async ({
		page,
		productPage,
		productStructuresPage,
		productsPage,
		structureBuilderPage,
	}) => {
		const fieldLabel = `Note ${getRandomInt()}`;
		const groupLabel = `Notes ${getRandomInt()}`;
		const structureLabel = `ProductType${getRandomInt()}`;

		await productStructuresPage.goto();

		await structureBuilderPage.addField('Text');

		await structureBuilderPage.changeFieldSettings({
			label: fieldLabel,
			name: `note${getRandomInt()}`,
		});

		await productStructuresPage.createRepeatableGroup({
			fieldLabel,
			label: groupLabel,
		});

		await structureBuilderPage.selectStructure();

		await structureBuilderPage.changeStructureSettings({
			label: structureLabel,
		});

		await structureBuilderPage.saveStructure();

		await structureBuilderPage.publishStructure();

		await productsPage.goto();

		await productsPage.openNewProductEditor('Default', structureLabel);

		await expect(productPage.getTab(groupLabel)).toHaveCount(0);

		await clickAndExpectToBeVisible({
			target: page.getByRole('textbox', {name: fieldLabel}),
			trigger: productPage.getTab(structureLabel),
		});
	}
);

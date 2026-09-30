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
	'Create a product structure from the base SKU and use its own field',
	{tag: ['@LPD-99448', '@LPD-105922']},
	async ({
		productPage,
		productStructuresPage,
		productsPage,
		structureBuilderPage,
	}) => {
		const fieldLabel = `Packaging Note ${getRandomInt()}`;
		const fieldName = `packagingNote${getRandomInt()}`;
		const productName = getRandomString();
		const structureLabel = `ProductType${getRandomInt()}`;

		try {
			await productStructuresPage.goto();

			await test.step('The new structure starts from the base SKU', async () => {
				await expect(
					productStructuresPage.getTreeItem('Units of Measure')
				).toBeVisible();
				await expect(
					productStructuresPage.getTreeItem('Details')
				).toBeVisible();
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
					target: productPage.name,
					trigger: productPage.getTab('Details'),
				});

				await productPage.code.fill(getRandomString());
				await productPage.name.fill(productName);

				await clickAndExpectToBeVisible({
					target: productPage.unitOfMeasureName,
					trigger: productPage.getTab('Units of Measure'),
				});

				await expect(productPage.getField(fieldName)).toBeVisible();

				await productPage.getField(fieldName).fill('Fragile');

				await productPage.save();

				await expect(
					productsPage.getProduct(productName)
				).toBeVisible();
			});

			await test.step('The field value is persisted', async () => {
				await productsPage.goto();

				await productsPage.openProductEditor(productName);

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

			await productsPage.deleteProduct(productName);
		}
	}
);

test(
	'Create a product structure with its own tab from the base SKU',
	{tag: ['@LPD-105923']},
	async ({
		productPage,
		productStructuresPage,
		productsPage,
		structureBuilderPage,
	}) => {
		const fieldLabel = `Warehouse Note ${getRandomInt()}`;
		const fieldName = `warehouseNote${getRandomInt()}`;
		const productName = getRandomString();
		const structureLabel = `ProductType${getRandomInt()}`;
		const tabLabel = `Logistics ${getRandomInt()}`;

		try {
			await productStructuresPage.goto();

			await test.step('The new structure starts from the base SKU', async () => {
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
					target: productPage.name,
					trigger: productPage.getTab('Details'),
				});

				await productPage.code.fill(getRandomString());
				await productPage.name.fill(productName);

				await clickAndExpectToBeVisible({
					target: productPage.getField(fieldName),
					trigger: productPage.getTab(tabLabel),
				});

				await productPage.getField(fieldName).fill('Aisle 3');

				await productPage.save();

				await expect(
					productsPage.getProduct(productName)
				).toBeVisible();
			});
		}
		finally {
			await productsPage.goto();

			await productsPage.deleteProduct(productName);
		}
	}
);

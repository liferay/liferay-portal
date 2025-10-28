/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {cmsPagesTest} from '../main/fixtures/cmsPagesTest';
import {structureBuilderPagesTest} from './fixtures/structureBuilderPagesTest';
import {FIELD_TYPES} from './pages/StructureBuilderPage';

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
	'Structures can be saved and published',
	{tag: '@LPD-36752'},
	async ({page, structureBuilderPage}) => {

		// Go to the Structure Builder

		await structureBuilderPage.goToCreateStructure();

		// Check it's enabled for all spaces by default

		await expect(structureBuilderPage.spaceCheckbox).toBeChecked();

		await expect(structureBuilderPage.spaceSelector).toBeDisabled();

		// Change label and name

		const label = `Structure${getRandomInt()}`;

		await structureBuilderPage.changeStructureSettings({
			label,
			name: label,
		});

		// Add fields and check they are selected by default

		await structureBuilderPage.addField('Text');

		await expect(
			page.locator('.breadcrumb-link', {hasText: 'Text'})
		).toBeVisible();

		await structureBuilderPage.addField('Decimal');

		await expect(
			page.locator('.breadcrumb-link', {hasText: 'Decimal'})
		).toBeVisible();

		// Select fields and check its values are shown

		await structureBuilderPage.selectFields([{label: 'Text'}]);

		await expect(page.getByLabel('Label')).toHaveValue('Text');

		await structureBuilderPage.selectFields([{label: 'Decimal'}]);

		await expect(page.getByLabel('Label')).toHaveValue('Decimal');

		// Save the structure

		await structureBuilderPage.saveStructure();

		await expect(page.locator('.alert-danger')).not.toBeVisible();

		// Remove a field

		await structureBuilderPage.deleteFields([{label: 'Decimal'}]);

		// Publish it

		await structureBuilderPage.publishStructure();

		// Check name changes in toolbar

		await expect(
			page.locator('.component-tbar').getByText(label)
		).toBeVisible();

		// Check another field with same name can not be added

		await structureBuilderPage.addField('Text');
		await structureBuilderPage.selectFields([{label: 'Text', nth: 1}]);
		await structureBuilderPage.changeFieldSettings({name: 'text'});
	}
);

test(
	'Structures can be saved with all type of fields',
	{tag: '@LPD-36752'},
	async ({picklistBuilderPage, structureBuilderPage}) => {

		// Add a picklist

		const picklist = await picklistBuilderPage.createPicklist();

		// Create structure

		const label = `Structure${getRandomInt()}`;

		await structureBuilderPage.createStructureFromData({
			label,
			name: label,
			page: structureBuilderPage,
		});

		// Add a field of each type

		for (const type of FIELD_TYPES) {
			await structureBuilderPage.addField(type);

			if (type === 'Single Select' || type === 'Multiselect') {
				await structureBuilderPage.changeFieldSettings({
					picklist: picklist.name,
				});
			}
		}

		// Publish the structure

		await structureBuilderPage.publishStructure();

		// Delete picklist

		await picklistBuilderPage.deletePicklist(picklist.id);
	}
);

test(
	'Can delete multiple fields',
	{tag: '@LPD-36767'},
	async ({structureBuilderPage}) => {

		// Create structure

		const label = `Structure${getRandomInt()}`;

		await structureBuilderPage.createStructureFromData({
			label,
			name: label,
			page: structureBuilderPage,
		});

		// Add four fields

		const types = ['Text', 'Long Text', 'Upload', 'Numeric'] as const;

		for (const type of types) {
			await structureBuilderPage.addField(type);
		}

		// Publish the structure

		await structureBuilderPage.publishStructure();

		// Select and delete three fields

		await structureBuilderPage.deleteFields([
			{label: 'Text'},
			{label: 'Long Text'},
			{label: 'Upload'},
		]);
	}
);

test(
	'Can configure a text field',
	{tag: '@LPD-49168'},
	async ({page, structureBuilderPage}) => {

		// Create structure

		const label = `Structure${getRandomInt()}`;
		const erc = getRandomString();

		await structureBuilderPage.createStructureFromData({
			erc,
			label,
			name: label,
			page: structureBuilderPage,
		});

		// Add a text field

		await structureBuilderPage.addField('Text');

		await structureBuilderPage.selectFields([{label: 'Text'}]);

		// Assert correct label style

		await expect(
			page.locator('.label-info', {hasText: 'Text'})
		).toBeVisible();

		// Configure the field

		await structureBuilderPage.changeFieldSettings({
			erc,
			label: 'Text Edited',
			localizable: true,
			name: 'textEdited',
		});

		await page.getByLabel('Accept Unique Values Only').click();

		await page.getByLabel('Limit Characters').click();

		const maximumNumberOfCharactersInput = page.getByLabel(
			'Maximum Number of Characters'
		);
		maximumNumberOfCharactersInput.fill('10');

		await maximumNumberOfCharactersInput.blur();

		// Publish the structure

		const {objectFields} = await structureBuilderPage.publishStructure();

		// Check the text field is created with the correct settings

		const textObjectField = objectFields.find(
			({name}) => name === 'textEdited'
		);

		expect(textObjectField).toBeDefined();

		expect(textObjectField.label).toStrictEqual({en_US: 'Text Edited'});
		expect(textObjectField.localized).toBe(true);
		expect(textObjectField.name).toBe('textEdited');
		expect(textObjectField.objectFieldSettings[0]).toStrictEqual({
			name: 'uniqueValues',
			value: true,
		});
		expect(textObjectField.objectFieldSettings[1]).toStrictEqual({
			name: 'maxLength',
			value: 10,
		});
		expect(textObjectField.objectFieldSettings[2]).toStrictEqual({
			name: 'showCounter',
			value: true,
		});
	}
);

test(
	'Create a picklist from the structure builder by opening other tab',
	{tag: '@LPD-52544'},
	async ({context, page, picklistBuilderPage, structureBuilderPage}) => {

		// Go to the Structure Builder

		await structureBuilderPage.goToCreateStructure();

		// Add a Single Select field and select it

		await structureBuilderPage.addField('Single Select');

		await structureBuilderPage.selectFields([{label: 'Single Select'}]);

		// Create new picklist from the button "New Picklist"

		const pagePromise = context.waitForEvent('page');

		await page.getByText('New Picklist').click();

		const newPage = await pagePromise;

		// The picklist builder opens in a new tab

		await expect(
			newPage.getByRole('heading', {name: 'New Picklist'})
		).toBeAttached();

		// Change the picklist name

		await newPage.getByLabel('Picklist Name').fill('Plants');

		// Save the picklist

		await newPage.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(newPage, 'Success:Plants was saved successfully.');

		// Check the new picklist in the structure builder opening the picker by keyboard

		const picklistPicker = page.getByLabel('Picklist');

		await picklistPicker.press('Enter');

		await expect(picklistPicker).toBeFocused();

		await expect(page.getByRole('option', {name: 'Plants'})).toBeAttached();

		// Delete picklist

		const picklist = await picklistBuilderPage.getPicklist('Plants');

		await picklistBuilderPage.deletePicklist(picklist.id);
	}
);

test(
	'Add correct initial fields depending on type',
	{
		tag: '@LPD-50371',
	},
	async ({page, structureBuilderPage}) => {

		// Create structure

		await structureBuilderPage.createStructureFromData({
			label: `StructureName${getRandomInt()}`,
			page: structureBuilderPage,
		});

		// Type content and check initial fields

		await structureBuilderPage.changeStructureSettings({
			label: getRandomString(),
		});

		await expect(
			page.locator('.treeview-link', {hasText: 'Title'})
		).toBeVisible();

		await expect(
			page.locator('.treeview-link', {hasText: 'File'})
		).not.toBeVisible();

		// Check with type file

		await structureBuilderPage.goToCreateStructure('file');

		await structureBuilderPage.changeStructureSettings({
			label: getRandomString(),
		});

		await expect(
			page.locator('.treeview-link', {hasText: 'Title'})
		).toBeVisible();

		await expect(
			page.locator('.treeview-link', {hasText: 'File'})
		).toBeVisible();

		// Check fields are not editable

		await structureBuilderPage.selectFields([{label: 'Title'}]);

		await expect(page.getByLabel('Label')).toBeDisabled();
		await expect(page.getByLabel('ERC')).toBeDisabled();
		await expect(page.getByLabel('Field Name')).toBeDisabled();

		await structureBuilderPage.selectFields([{label: 'File'}]);

		await expect(page.getByLabel('Label')).toBeDisabled();
		await expect(page.getByLabel('ERC')).toBeDisabled();
		await expect(page.getByLabel('Field Name')).toBeDisabled();
	}
);

test(
	'Fields are sorted',
	{
		tag: '@LPD-61206',
	},
	async ({page, structureBuilderPage}) => {
		const label1 = getRandomString();

		// Create one structure to reference it later

		await structureBuilderPage.createStructureFromData({
			label: label1,
			page: structureBuilderPage,
		});

		// Create main structure

		const id = await structureBuilderPage.createStructureFromData({
			label: getRandomString(),
			page: structureBuilderPage,
		});

		// Add a referenced structure

		await structureBuilderPage.addReferencedStructures([label1]);

		// Add two fields and check order

		await structureBuilderPage.addField('Text');
		await structureBuilderPage.addField('Boolean');

		await expect(page.locator('.treeview-link').nth(2)).toHaveText('Text');

		await expect(page.locator('.treeview-link').nth(3)).toHaveText(
			'Boolean'
		);

		await expect(page.locator('.treeview-link').nth(4)).toHaveText(label1);

		// Create repeatable group

		await structureBuilderPage.createRepeatableGroup({
			fields: [{label: 'Boolean'}],
			label: 'Repeatable Group',
		});

		// Add another field and check order is correct

		await structureBuilderPage.addField('Date');

		await expect(page.locator('.treeview-link').nth(2)).toHaveText('Text');

		await expect(page.locator('.treeview-link').nth(3)).toHaveText('Date');

		await expect(page.locator('.treeview-link').nth(4)).toHaveText(label1);

		await expect(page.locator('.treeview-link').nth(6)).toHaveText(
			'Repeatable Group'
		);

		// Publish, refresh and check order

		await structureBuilderPage.publishStructure();

		await structureBuilderPage.editStructure(id);

		await expect(page.locator('.treeview-link').nth(2)).toHaveText('Text');

		await expect(page.locator('.treeview-link').nth(3)).toHaveText('Date');

		await expect(page.locator('.treeview-link').nth(4)).toHaveText(label1);

		await expect(page.locator('.treeview-link').nth(5)).toHaveText(
			'Repeatable Group'
		);
	}
);

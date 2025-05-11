/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {cmsPagesTest} from './fixtures/cmsPagesTest';
import {FIELD_TYPES} from './pages/StructureBuilderPage';

const test = mergeTests(
	cmsPagesTest,
	featureFlagsTest({
		'LPD-11232': {enabled: true},
		'LPD-17564': {enabled: true},
	}),
	loginTest()
);

test(
	'Structures can be saved and published',
	{tag: '@LPD-36752'},
	async ({page, structureBuilderPage}) => {

		// Go to the Structure Builder

		await structureBuilderPage.goto();

		await structureBuilderPage.enableForAllSpaces();

		// Change label and name

		const label = `Structure${getRandomInt()}`;

		await structureBuilderPage.changeStructureSettings({
			label,
			name: label,
		});

		// Check we can't publish without adding a field

		await clickAndExpectToBeVisible({
			target: page.getByText('At least one field must be added'),
			trigger: structureBuilderPage.saveButton,
		});

		// Add fields and check they are selected by default

		await structureBuilderPage.addField('Text');

		await expect(
			page.locator('.breadcrumb-link', {hasText: 'Text'})
		).toBeVisible();

		await structureBuilderPage.addField('Long Text');

		await expect(
			page.locator('.breadcrumb-link', {hasText: 'Long Text'})
		).toBeVisible();

		// Save the structure

		const {id} = await structureBuilderPage.saveStructure();

		await expect(page.locator('.alert-danger')).not.toBeVisible();

		// Remove a field

		await structureBuilderPage.deleteFields([{label: 'Long Text'}]);

		// Publish it

		await structureBuilderPage.publishStructure();

		// Check name changes in management bar

		await expect(
			page.locator('.management-bar').getByText(label)
		).toBeVisible();

		// Check another field with same name can not be added

		await structureBuilderPage.addField('Text');
		await structureBuilderPage.selectFields([{label: 'Text', nth: 1}]);
		await structureBuilderPage.changeFieldSettings({name: 'text'});

		// Delete structure

		await structureBuilderPage.deleteStructure(id);
	}
);

test(
	'Structures can be saved with all type of fields',
	{tag: '@LPD-36752'},
	async ({picklistBuilderPage, structureBuilderPage}) => {

		// Add a picklist

		const picklist = await picklistBuilderPage.createPicklist();

		// Go to the Structure Builder

		await structureBuilderPage.goto();

		await structureBuilderPage.enableForAllSpaces();

		// Change label and name

		const label = `Structure${getRandomInt()}`;

		await structureBuilderPage.changeStructureSettings({
			label,
			name: label,
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

		// Save and publish the structure

		const {id} = await structureBuilderPage.saveStructure();

		await structureBuilderPage.publishStructure();

		// Delete picklist

		await picklistBuilderPage.deletePicklist(picklist.id);

		// Delete structure

		await structureBuilderPage.deleteStructure(id);
	}
);

test(
	'Can delete multiple fields',
	{tag: '@LPD-36767'},
	async ({structureBuilderPage}) => {

		// Go to the Structure Builder

		await structureBuilderPage.goto();

		await structureBuilderPage.enableForAllSpaces();

		// Change label and name

		const label = `Structure${getRandomInt()}`;

		await structureBuilderPage.changeStructureSettings({
			label,
			name: label,
		});

		// Add four fields

		const types = ['Text', 'Long Text', 'Upload', 'Numeric'] as const;

		for (const type of types) {
			await structureBuilderPage.addField(type);
		}

		// Save and publish the structure

		const {id} = await structureBuilderPage.saveStructure();
		await structureBuilderPage.publishStructure();

		// Select and delete three fields

		await structureBuilderPage.deleteFields([
			{label: 'Text'},
			{label: 'Long Text'},
			{label: 'Upload'},
		]);

		// Delete structure

		await structureBuilderPage.deleteStructure(id);
	}
);

test(
	'Can configure a text field',
	{tag: '@LPD-49168'},
	async ({page, structureBuilderPage}) => {

		// Go to the Structure Builder

		await structureBuilderPage.goto();

		await structureBuilderPage.enableForAllSpaces();

		// Change label,name and erc

		const label = `Structure${getRandomInt()}`;
		const erc = getRandomString();

		await structureBuilderPage.changeStructureSettings({
			erc,
			label,
			name: label,
		});

		// Add a text field

		await structureBuilderPage.addField('Text');

		await structureBuilderPage.selectFields([{label: 'Text'}]);

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

		// Save and publish the structure

		const {id} = await structureBuilderPage.saveStructure();

		await expect(page.locator('.alert-danger')).not.toBeVisible();

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

		// Delete structure

		await structureBuilderPage.deleteStructure(id);
	}
);

test.describe('Frontend validations', () => {
	test(
		'Validations when saving the structure',
		{tag: '@LPD-36752'},
		async ({page, picklistBuilderPage, structureBuilderPage}) => {

			// Add a picklist

			const picklist = await picklistBuilderPage.createPicklist();

			// Go to the Structure Builder

			await structureBuilderPage.goto();

			// Add a Text field

			await structureBuilderPage.addField('Text');

			// Try to save and check we can't publish without spaces

			await expect(async () => {
				await structureBuilderPage.saveButton.click();

				await expect(
					page.getByText('Spaces must be selected')
				).toBeAttached({
					timeout: 500,
				});
			}).toPass();

			await structureBuilderPage.enableForAllSpaces();

			// Set label and empty name

			const label = `Structure${getRandomInt()}`;

			await structureBuilderPage.changeStructureSettings({
				label,
				name: '',
			});

			await expect(
				page.getByText('This field is required')
			).toBeVisible();

			// Add a Single Select field and select it

			await structureBuilderPage.addField('Single Select');

			await structureBuilderPage.selectFields([{label: 'Single Select'}]);

			// Put empty name

			await structureBuilderPage.changeFieldSettings({name: ''});

			// Try to save and check it redirects to structure view

			await clickAndExpectToBeVisible({
				target: page.getByText('Structure Name'),
				trigger: structureBuilderPage.saveButton,
			});

			// Fill name

			await structureBuilderPage.changeStructureSettings({name: label});

			// Now try to save and check it redirects to field view

			await clickAndExpectToBeVisible({
				target: page.locator('.breadcrumb-link', {
					hasText: 'Single Select',
				}),
				trigger: structureBuilderPage.saveButton,
			});

			// Fill name, select picklist and save again

			await structureBuilderPage.changeFieldSettings({name: 'name'});

			await structureBuilderPage.changeFieldSettings({
				picklist: picklist.name,
			});

			// Check picklist setting is saved

			await structureBuilderPage.selectFields([{label: 'Text'}]);

			await structureBuilderPage.selectFields([{label: 'Single Select'}]);

			await expect(page.getByText(picklist.name)).toBeVisible();

			// Save

			const {id} = await structureBuilderPage.saveStructure();

			// Publish structure

			await structureBuilderPage.publishStructure();

			// Delete one field and check warning modal is show when publishing

			await structureBuilderPage.deleteFields([{label: 'Text'}]);

			await clickAndExpectToBeVisible({
				target: page.getByText(
					'You removed one or more fields from the structure'
				),
				trigger: structureBuilderPage.publishButton,
			});

			await clickAndExpectToBeHidden({
				target: page.getByText(
					'You removed one or more fields from the structure'
				),
				trigger: page.locator('.btn-danger'),
			});

			await waitForAlert(page, 'published successfully', {
				timeout: 5000,
			});

			// Check the warning does not appear anymore

			await structureBuilderPage.publishStructure();

			// Delete structure

			await structureBuilderPage.deleteStructure(id);

			// Delete picklist

			await picklistBuilderPage.deletePicklist(picklist.id);
		}
	);

	test(
		'Validations in the picklist picker',
		{tag: '@LPD-51647'},
		async ({page, picklistBuilderPage, structureBuilderPage}) => {

			// Add a picklist

			const picklist = await picklistBuilderPage.createPicklist();

			// Go to the Structure Builder

			await structureBuilderPage.goto();

			// Add a Single Select field and check for blur error

			await structureBuilderPage.addField('Single Select');

			await structureBuilderPage.selectFields([{label: 'Single Select'}]);

			const picklistPicker = page.getByLabel('Picklist');

			const errorMessage = page.getByText('This field is required.');

			await expect(errorMessage).not.toBeAttached();

			await picklistPicker.press('Tab');

			await expect(errorMessage).toBeAttached();

			await structureBuilderPage.changeFieldSettings({
				picklist: picklist.name,
			});

			await expect(errorMessage).not.toBeAttached();

			// Add a Multiselect field and check for outside click error

			await structureBuilderPage.addField('Multiselect');

			await structureBuilderPage.selectFields([{label: 'Multiselect'}]);

			await expect(errorMessage).not.toBeAttached();

			await picklistPicker.click();

			await page.locator('body').click();

			await expect(errorMessage).toBeAttached();

			// Delete picklist

			await picklistBuilderPage.deletePicklist(picklist.id);
		}
	);
});

test(
	'Create a picklist from the structure builder by opening other tab',
	{tag: '@LPD-52544'},
	async ({context, page, picklistBuilderPage, structureBuilderPage}) => {

		// Go to the Structure Builder

		await structureBuilderPage.goto();

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

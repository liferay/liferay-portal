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
	'Validations when saving the structure',
	{tag: '@LPD-36752'},
	async ({page, picklistBuilderPage, structureBuilderPage}) => {

		// Add a picklist

		const picklist = await picklistBuilderPage.createPicklist();

		// Go to the Structure Builder

		await structureBuilderPage.goToCreateStructure();

		// Try to save and check we can't publish without spaces

		await expect(async () => {
			await structureBuilderPage.selectSpaces([]);

			await structureBuilderPage.saveButton.click();

			await expect(
				page.getByText('Spaces must be selected')
			).toBeAttached({
				timeout: 500,
			});
		}).toPass();

		await structureBuilderPage.enableForAllSpaces();

		// Check validation in name field

		await structureBuilderPage.changeStructureSettings({name: 'name'});

		await expect(
			page.getByText('The first character must be an uppercase letter')
		).toBeVisible();

		await structureBuilderPage.changeStructureSettings({name: 'Name---'});

		await expect(
			page.getByText('This field must only contain letters and digits')
		).toBeVisible();

		let longName = `Name${getRandomInt()}`;

		while (longName.length < 41) {
			longName += getRandomInt();
		}

		await structureBuilderPage.changeStructureSettings({name: longName});

		await expect(
			page.getByText('Maximum Number of Characters Exceeded')
		).toBeVisible();

		await structureBuilderPage.changeStructureSettings({name: 'CMSBlog'});

		await expect(
			page.getByText('This name is already in use')
		).toBeVisible();

		// Check validation in ERC field

		await structureBuilderPage.changeStructureSettings({erc: 'L_erc'});

		await expect(page.getByText('The prefix L_ is reserved')).toBeVisible();

		while (longName.length < 75) {
			longName += getRandomInt();
		}

		await structureBuilderPage.changeStructureSettings({erc: longName});

		await expect(
			page.getByText('Maximum Number of Characters Exceeded')
		).toBeVisible();

		// Add a Text field

		await structureBuilderPage.addField('Text');

		// Check validation for field name and ERC fields

		await structureBuilderPage.changeFieldSettings({name: 'Field'});

		await expect(
			page.getByText('The first character must be a lowercase letter')
		).toBeVisible();

		await structureBuilderPage.changeFieldSettings({name: 'field'});

		// Set label and empty name

		await structureBuilderPage.selectStructure();

		const label = `Structure${getRandomInt()}`;

		await structureBuilderPage.changeStructureSettings({
			label,
			name: '',
		});

		await expect(page.getByText('This field is required')).toBeVisible();

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

		// Fill name and ERC

		await structureBuilderPage.changeStructureSettings({
			erc: getRandomString(),
			name: label,
		});

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

		await structureBuilderPage.saveStructure();

		// Publish structure

		await structureBuilderPage.publishStructure();

		// Delete one field and check warning modal is show when publishing

		await structureBuilderPage.deleteFields([{label: 'Text'}]);

		await clickAndExpectToBeVisible({
			target: page.getByText(
				'You removed one or more fields from the content structure'
			),
			trigger: structureBuilderPage.publishButton,
		});

		await clickAndExpectToBeHidden({
			target: page.getByText(
				'You removed one or more fields from the content structure'
			),
			trigger: page.locator('.btn-danger'),
		});

		await waitForAlert(page, 'published successfully', {
			timeout: 5000,
		});

		// Check the warning does not appear anymore

		await structureBuilderPage.publishStructure();

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

		await structureBuilderPage.goToCreateStructure();

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

		await page.getByText('Content Structure Fields').click();

		await expect(errorMessage).toBeAttached();

		// Delete picklist

		await picklistBuilderPage.deletePicklist(picklist.id);
	}
);

/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {fragmentsPagesTest} from '../../../fixtures/fragmentPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {structureBuilderPagesTest} from '../structure-builder/fixtures/structureBuilderPagesTest';
import {cmsPagesTest} from './fixtures/cmsPagesTest';
import {PicklistBuilderPage} from './pages/PicklistBuilderPage';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
		'LPD-32050': {enabled: true},
		'LPS-179669': {enabled: true},
	}),
	loginTest(),
	fragmentsPagesTest,
	pageEditorPagesTest,
	structureBuilderPagesTest
);

test(
	'Custom structure takes title as name field',
	{tag: '@LPD-62896'},
	async ({contentsPage, page, structureBuilderPage}) => {

		// Create structure

		const structureLabel = `Structure${getRandomInt()}`;
		const structureERC = getRandomString();

		await structureBuilderPage.createStructureFromData({
			erc: structureERC,
			label: structureLabel,
			name: structureLabel,
			page: structureBuilderPage,
		});

		// Go to CMS Contents

		await contentsPage.goto();

		// Create new content for recently created structure

		await contentsPage.createContent(structureLabel);

		// Fill data and save

		const title = getRandomString();

		await page.getByLabel('Title').fill(title);

		await contentsPage.saveContent();

		// Edit the content again and check title is taken as name field

		await contentsPage.editContent(title);

		await expect(page.getByText(`Edit ${title}`)).toBeVisible();

		// Delete content

		await contentsPage.goto();

		await contentsPage.deleteContent(title);
	}
);

test(
	'Can translate a content with a Select field',
	{tag: '@LPD-62179'},
	async ({
		contentsPage,
		context,
		localizationSelectPage,
		page,
		structureBuilderPage,
	}) => {

		// Create new structure

		const structureLabel = `StructureName${getRandomInt()}`;

		await structureBuilderPage.createStructureFromData({
			label: structureLabel,
			page: structureBuilderPage,
		});

		// Add a Single Select field and Multiselect fields

		await structureBuilderPage.addField('Single Select');
		await structureBuilderPage.addField('Multiselect');

		// Create new picklist from the button "New Picklist"

		await structureBuilderPage.selectFields([{label: 'Single Select'}]);

		const pagePromise = context.waitForEvent('page');

		await page.getByText('New Picklist').click();

		const newPage = await pagePromise;

		// The picklist builder opens in a new tab

		const picklistBuilderPage = new PicklistBuilderPage(newPage);

		await expect(
			newPage.getByRole('heading', {name: 'New Picklist'})
		).toBeAttached();

		const picklistName = getRandomString();

		await newPage.getByLabel('Picklist Name').fill(picklistName);

		// Add options

		await picklistBuilderPage.addOption('Yellow');
		await picklistBuilderPage.addOption('Blue');

		// Save the picklist

		await newPage.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(newPage, 'was saved successfully.');

		// Select the picklist in both fields

		const picklistPicker = page.getByLabel('Picklist');

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {name: picklistName}),
			trigger: picklistPicker,
		});

		await structureBuilderPage.selectFields([{label: 'Multiselect'}]);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {name: picklistName}),
			trigger: picklistPicker,
		});

		// Publish the structure

		await structureBuilderPage.publishStructure();

		// Now create a content for this structure

		await contentsPage.goto();

		await contentsPage.createContent(structureLabel);

		// Fill data in default language

		const contentTitle = getRandomString();

		await contentsPage.fillData([{label: 'Title', value: contentTitle}]);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {name: 'Yellow'}),
			trigger: page.locator('.select-from-list'),
		});

		await page.getByLabel('Blue').check();

		// Switch to spanish and change values

		await localizationSelectPage.switchLanguage('es-ES');

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {name: 'Blue'}),
			trigger: page.locator('.select-from-list'),
		});

		await page.getByLabel('Yellow').check();

		// Save content, edit it again and check values were persisted

		await contentsPage.saveContent();

		await contentsPage.editContent(contentTitle);

		await expect(page.getByLabel('Blue')).toBeChecked();

		await expect(page.getByPlaceholder('Choose an Option')).toHaveValue(
			'Yellow'
		);

		await localizationSelectPage.switchLanguage('es-ES');

		await expect(page.getByLabel('Blue')).toBeChecked();
		await expect(page.getByLabel('Yellow')).toBeChecked();

		await expect(page.getByLabel('Single Select')).toHaveValue('Blue');

		// Delete picklist

		const picklist = await picklistBuilderPage.getPicklist(picklistName);

		await picklistBuilderPage.deletePicklist(picklist.id);
	}
);

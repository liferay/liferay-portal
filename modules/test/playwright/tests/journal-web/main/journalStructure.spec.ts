/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import createTempFile from '../../../utils/createTempFile';
import fillAndClickOutside from '../../../utils/fillAndClickOutside';
import getRandomString from '../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {waitForAlert} from '../../../utils/waitForAlert';
import {journalPagesTest} from './fixtures/journalPagesTest';
import getDataStructureDefinition from './utils/getDataStructureDefinition';

export const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	journalPagesTest,
	loginTest()
);

test(
	'Import and Override an empty file on an existing structure gives an error',
	{
		tag: '@LPD-33986',
	},
	async ({apiHelpers, journalStructuresPage, page, site}) => {
		const structureName = 'Structure Test';

		const dataDefinition = getDataStructureDefinition({
			defaultLanguageId: 'en_US',
			fields: [{name: 'Text', repeatable: false}],
			name: structureName,
		});

		await apiHelpers.dataEngine.createStructure(site.id, dataDefinition);

		await journalStructuresPage.goto(site.friendlyUrlPath);

		await expect(
			page.getByRole('link', {name: structureName})
		).toBeVisible();

		const filePath = createTempFile(getRandomString() + '.json');

		await journalStructuresPage.importAndOverride(filePath, structureName);

		await expect(page.locator('.alert-danger')).toContainText(
			'argument "content" is null'
		);
	}
);

test(
	'Import Structure using a JSON file without any field gives an error',
	{
		tag: '@LPD-33986',
	},
	async ({journalStructuresPage, page, site}) => {
		await journalStructuresPage.goto(site.friendlyUrlPath);

		const filePath = createTempFile(
			getRandomString() + '.json',
			'{"availableLanguageIds":["en_US"],"dataDefinitionFields":[],"name":{"en_US": "Structure Test"}}'
		);

		await journalStructuresPage.openOptionsMenu();
		await journalStructuresPage.import(filePath, 'Structure Test');

		await expect(page.locator('.alert-danger')).toContainText(
			'At least one field must be added.'
		);
	}
);

test(
	'Can add a fieldset that contains a Rich text',
	{
		tag: '@LPD-42003',
	},
	async ({journalEditStructurePage, journalStructuresPage, page, site}) => {
		await journalStructuresPage.goto(site.friendlyUrlPath);

		await expect(journalStructuresPage.newButton).toBeVisible();
		await journalStructuresPage.newButton.click();

		await journalEditStructurePage.fieldsetsTab.click();
		await page
			.getByRole('button', {name: 'Press enter to add Basic Web'})
			.dblclick();

		await expect(
			page.locator('.ddm-field-container > .ddm-field')
		).toBeVisible();
	}
);

test(
	'Can not duplicate a repeatable field in the "Edit Default Values" form',
	{
		tag: '@LPD-29489',
	},
	async ({apiHelpers, journalStructuresPage, page, site}) => {
		const structureName = 'Structure Test';
		const fieldName = 'TextFieldTest';

		await apiHelpers.dataEngine.createStructure(
			site.id,
			getDataStructureDefinition({
				defaultLanguageId: 'en_US',
				fields: [{name: fieldName, repeatable: true}],
				name: structureName,
			})
		);
		await journalStructuresPage.goto(site.friendlyUrlPath);
		await journalStructuresPage.goToJournalStructureAction(
			'Edit Default Values',
			structureName
		);

		await expect(
			page.getByLabel(`Add Duplicate Field ${fieldName}`)
		).toBeDisabled();
	}
);

test(
	'HTML in the name of a highlighted structure is not executed',
	{
		tag: '@LPD-101973',
	},
	async ({apiHelpers, journalPage, page, site}) => {
		page.on('dialog', (dialog) => {
			dialog.accept();

			expect(
				dialog.message(),
				'This alert should not be shown'
			).toBeNull();
		});

		const structurePrefix = getRandomString();
		const structureName = `${structurePrefix} <img src=x onerror=alert(1)>`;

		await apiHelpers.dataEngine.createStructure(
			site.id,
			getDataStructureDefinition({
				defaultLanguageId: 'en_US',
				fields: [{name: 'TextFieldTest', repeatable: false}],
				name: structureName,
			})
		);

		await journalPage.goto(site.friendlyUrlPath);

		await page
			.getByTestId('headerOptions')
			.getByLabel('Options')
			.and(page.locator('[aria-haspopup]'))
			.click();

		await page.getByRole('menuitem', {name: 'Configuration'}).click();

		await page.getByLabel('Select Highlighted Structures').click();

		await page
			.frameLocator('iframe[title="Select Structures"]')
			.getByLabel(structurePrefix)
			.click();

		await page.getByRole('button', {name: 'Add'}).click();

		await expect(page.getByText(structurePrefix)).toBeVisible();

		await page.getByRole('button', {name: 'Save'}).click();

		await page.getByText('Success:You have successfully').waitFor();

		await journalPage.goto(site.friendlyUrlPath);

		await page.locator('a[href*="highlightedDDMStructureId"]').click();

		await expect(page.locator('h2.sheet-title')).toHaveText(structureName);
	}
);

test(
	'The content references warning is not hidden behind the toolbar when web content exists for the structure',
	{
		tag: '@LPD-104383',
	},
	async ({apiHelpers, journalStructuresPage, page, site}) => {
		const structureName = getRandomString();

		const structure = await apiHelpers.dataEngine.createStructure(
			site.id,
			getDataStructureDefinition({
				defaultLanguageId: 'en_US',
				fields: Array.from({length: 10}, (_, index) => ({
					name: `TextFieldTest${index}`,
					repeatable: false,
				})),
				name: structureName,
			})
		);

		await apiHelpers.headlessDelivery.postStructuredContent({
			contentStructureId: Number(structure.id),
			datePublished: null,
			siteId: site.id,
			title: getRandomString(),
		});

		await journalStructuresPage.goto(site.friendlyUrlPath);

		await page
			.getByRole('link', {exact: true, name: structureName})
			.click();

		await expect(page.locator('.ddm-field-container')).toHaveCount(10);

		await expect.poll(() => page.evaluate(() => window.scrollY)).toBe(0);

		const alert = page.locator('.alert-warning').filter({
			hasText: 'There are content references to this structure',
		});
		const toolbar = page.locator('.component-tbar.tbar-structure');

		await expect(alert).toBeVisible();
		await expect(toolbar).toBeVisible();

		const alertBoundingBox = await alert.boundingBox();
		const toolbarBoundingBox = await toolbar.boundingBox();

		expect(alertBoundingBox.y).toBeGreaterThanOrEqual(
			toolbarBoundingBox.y + toolbarBoundingBox.height
		);

		const stickyTop = await toolbar.evaluate((element) =>
			parseFloat(getComputedStyle(element).top)
		);

		await alert.hover();
		await page.mouse.wheel(0, 400);

		await expect
			.poll(async () => (await alert.boundingBox()).y)
			.toBeLessThan(alertBoundingBox.y);

		const scrolledToolbarBoundingBox = await toolbar.boundingBox();

		expect(
			Math.abs(scrolledToolbarBoundingBox.y - stickyTop)
		).toBeLessThanOrEqual(1);
	}
);

test(
	'A structure added from a URL with a language prefix is saved once',
	{
		tag: '@LPS-77387',
	},
	async ({journalEditStructurePage, journalStructuresPage, page, site}) => {
		const structureName = getRandomString();

		await page.goto(
			`/en/group${site.friendlyUrlPath}${PORTLET_URLS.journalStructures}`
		);

		await journalStructuresPage.newButton.click();

		await expect(page).toHaveURL(/\/en\/group\//);

		await page.getByTitle('Text', {exact: true}).dblclick();

		await fillAndClickOutside(
			page,
			page.getByRole('textbox', {name: 'Name Required'}),
			structureName
		);

		await journalEditStructurePage.save();

		await journalStructuresPage.goto(site.friendlyUrlPath);

		await expect(
			page.getByRole('link', {exact: true, name: structureName})
		).toHaveCount(1);
	}
);

test(
	'The default values of a structure built from a fieldset are kept',
	{
		tag: '@LPD-113664',
	},
	async ({
		apiHelpers,
		journalEditStructureDefaultValuesPage,
		journalEditStructurePage,
		journalStructuresPage,
		page,
		site,
	}) => {
		const fieldsetName = getRandomString();
		const structureName = getRandomString();
		const textValue = getRandomString();

		// Add a structure from a fieldset

		await apiHelpers.dataEngine.createStructure(
			site.id,
			getDataStructureDefinition({
				defaultLanguageId: 'en_US',
				fields: [{name: 'FieldsetText'}],
				name: fieldsetName,
			})
		);

		await journalStructuresPage.goto(site.friendlyUrlPath);

		await journalStructuresPage.newButton.click();

		await journalEditStructurePage.fieldsetsTab.click();

		await page
			.getByRole('button', {name: `Press enter to add ${fieldsetName}`})
			.dblclick();

		await fillAndClickOutside(
			page,
			page.getByRole('textbox', {name: 'Name Required'}),
			structureName
		);

		await journalEditStructurePage.save();

		// Edit the default values

		await journalEditStructureDefaultValuesPage.goto({
			siteUrl: site.friendlyUrlPath,
			structureName,
		});

		await journalEditStructureDefaultValuesPage.fillTextField(
			'FieldsetText',
			textValue
		);

		await journalEditStructureDefaultValuesPage.save();

		// Check the default values

		await journalEditStructureDefaultValuesPage.goto({
			siteUrl: site.friendlyUrlPath,
			structureName,
		});

		await expect(
			page.getByRole('textbox', {name: 'FieldsetText'})
		).toHaveValue(textValue);
	}
);

test(
	'A structure with every field type can be viewed, edited and deleted',
	{
		tag: '@LPS-125933',
	},
	async ({journalEditStructurePage, journalStructuresPage, page, site}) => {
		const fieldTypes = [
			'Text',
			'Select from List',
			'Single Selection',
			'Multiple Selection',
			'Grid',
			'Date',
			'Numeric',
			'Image',
			'Rich Text',
			'Upload',
			'Color',
			'Web Content',
			'Geolocation',
			'Link to Page',
			'Separator',
		];
		const backButton = page.getByRole('button', {name: 'Back'});
		const getFieldLabel = (label: string) =>
			page
				.locator('.ddm-form-builder-wrapper')
				.getByText(label, {exact: true})
				.and(page.locator(':not(.actions-label)'))
				.first();
		const nameInput = page.getByRole('textbox', {name: 'Name Required'});
		const structureName = getRandomString();

		// Add a structure with every field type

		await journalStructuresPage.goto(site.friendlyUrlPath);

		await journalStructuresPage.newButton.click();

		for (const fieldType of fieldTypes) {
			await page.getByTitle(fieldType, {exact: true}).dblclick();

			await backButton.click();
		}

		await fillAndClickOutside(page, nameInput, structureName);

		await journalEditStructurePage.save();

		// View and edit the fields

		await journalEditStructurePage.goto({
			siteUrl: site.friendlyUrlPath,
			structureName,
		});

		for (const fieldType of fieldTypes) {
			await expect(getFieldLabel(fieldType)).toBeVisible();
		}

		await journalEditStructurePage.showFieldProperties('Text');

		await journalEditStructurePage.fillFieldProperty(
			page.getByRole('textbox', {exact: true, name: 'Label'}),
			'Text Edit'
		);

		await backButton.click();

		const editedStructureName = `${structureName} Edit`;

		await fillAndClickOutside(page, nameInput, editedStructureName);

		await journalEditStructurePage.save();

		await journalEditStructurePage.goto({
			siteUrl: site.friendlyUrlPath,
			structureName: editedStructureName,
		});

		await expect(getFieldLabel('Text Edit')).toBeVisible();

		// Delete the structure

		await journalStructuresPage.goto(site.friendlyUrlPath);

		page.once('dialog', (dialog) => dialog.accept());

		await journalStructuresPage.goToJournalStructureAction(
			'Delete',
			editedStructureName
		);

		await waitForAlert(
			page,
			'Success:Your request completed successfully.'
		);

		await expect(
			page.getByRole('link', {exact: true, name: editedStructureName})
		).toHaveCount(0);
	}
);

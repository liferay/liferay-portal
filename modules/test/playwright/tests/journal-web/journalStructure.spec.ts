/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../fixtures/isolatedSiteTest';
import {loginTest} from '../../fixtures/loginTest';
import createTempFile from '../../utils/createTempFile';
import getRandomString from '../../utils/getRandomString';
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

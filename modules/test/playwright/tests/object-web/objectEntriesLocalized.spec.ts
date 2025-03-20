/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectDefinitionApi} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {accountSettingsPagesTest} from '../../fixtures/accountSettingsPagesTest';
import {applicationsMenuPageTest} from '../../fixtures/applicationsMenuPageTest';
import {collectionsPagesTest} from '../../fixtures/collectionsPagesTest';
import {dataApiHelpersTest} from '../../fixtures/dataApiHelpersTest';
import {editObjectDefinitionPagesTest} from '../../fixtures/editObjectDefinitionPagesTest';
import {featureFlagsTest} from '../../fixtures/featureFlagsTest';
import {formsPagesTest} from '../../fixtures/formsPagesTest';
import {isolatedSiteTest} from '../../fixtures/isolatedSiteTest';
import {loginTest} from '../../fixtures/loginTest';
import {objectPagesTest} from '../../fixtures/objectPagesTest';
import {pageEditorPagesTest} from '../../fixtures/pageEditorPagesTest';
import {workflowPagesTest} from '../../fixtures/workflowPagesTest';
import {getRandomDouble} from '../../utils/getRandomDouble';
import {getRandomInt} from '../../utils/getRandomInt';
import getRandomString from '../../utils/getRandomString';
import {journalPagesTest} from '../journal-web/fixtures/journalPagesTest';
import {createObjectFields, mockObjectFields} from './utils/mockObjectFields';

export const test = mergeTests(
	accountSettingsPagesTest,
	applicationsMenuPageTest,
	collectionsPagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	editObjectDefinitionPagesTest,
	featureFlagsTest({
		'LPD-32050': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	formsPagesTest,
	journalPagesTest,
	loginTest(),
	objectPagesTest,
	pageEditorPagesTest,
	workflowPagesTest
);

let siteLanguage = 'en';

test.afterEach(async ({page}) => {
	if (siteLanguage !== 'en') {
		await page.goto('en');

		siteLanguage = 'en';
	}
});

test.describe('Localized object entries are saved correctly', () => {
	test('Attachment fields', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinitionLabel = 'ObjectDefinitionLabel' + getRandomInt();
		const objectDefinitionName = 'ObjectDefinitionName' + getRandomInt();

		const {objectFields, titleObjectFieldName} = await mockObjectFields({
			apiHelpers,
			localizeAllLocalizable: true,
			objectFieldBusinessTypes: ['attachment', 'attachment'],
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionApi);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				enableLocalization: true,
				label: {
					en_US: objectDefinitionLabel,
				},
				name: objectDefinitionName,
				objectFields,
				pluralLabel: {
					en_US: objectDefinitionLabel,
				},
				portlet: true,
				scope: 'company',
				status: {
					code: 0,
				},
				titleObjectFieldName,
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.addObjectEntryButton.click();

		const FIRST_ATTACHMENT_FILE_NAME = 'astronaut.png';

		const SECOND_ATTACHMENT_FILE_NAME = 'planet.png';

		const firstUplodadButton = page
			.getByRole('button', {name: 'Select File'})
			.first();

		const secondUploadButton = page
			.getByRole('button', {name: 'Select File'})
			.nth(1);

		const firstTranslationsDropdownTrigger = page
			.getByTestId('triggerButton')
			.first();

		const secondTranslationsDropdownTrigger = page
			.getByTestId('triggerButton')
			.nth(1);

		// with english locale, fill both inputs

		await firstUplodadButton.click();

		await viewObjectEntriesPage.selectFileFromDocumentsAndMedia(
			FIRST_ATTACHMENT_FILE_NAME
		);

		await secondUploadButton.click();

		await viewObjectEntriesPage.selectFileFromDocumentsAndMedia(
			SECOND_ATTACHMENT_FILE_NAME
		);

		// use first dropdown locale to switch to catalan

		await firstTranslationsDropdownTrigger.click();

		const catalanOptions = page.getByTestId(
			'availableLocalesDropdownca_ES'
		);

		await catalanOptions.first().click();

		// with catalan locale selected for the first time, all values should be copied from english

		await expect(
			page.getByRole('button', {name: FIRST_ATTACHMENT_FILE_NAME})
		).toBeVisible();

		await expect(
			page.getByRole('button', {name: SECOND_ATTACHMENT_FILE_NAME})
		).toBeVisible();

		// change first catalan input

		const TRANSLATED_ATTACHMENT_FILE_NAME = 'moon.png';
		await firstUplodadButton.click();

		await viewObjectEntriesPage.selectFileFromDocumentsAndMedia(
			TRANSLATED_ATTACHMENT_FILE_NAME
		);

		await secondTranslationsDropdownTrigger.click();

		// check for labels in dropdown, catalan should show as translated

		await expect(
			catalanOptions.first().locator('.label-item-expand')
		).toHaveText('translated', {ignoreCase: true});

		const englishOption = page.getByTestId('availableLocalesDropdownen_US');

		await expect(
			englishOption.first().locator('.label-item-expand')
		).toHaveText('default', {ignoreCase: true});

		// save

		const responsePromise = page.waitForResponse(
			`**${objectDefinition.restContextPath}`
		);

		await catalanOptions.nth(1).click();

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		const response = await responsePromise;

		await expect(
			page.getByText('Success:Your request completed successfully.')
		).toBeVisible();

		// go back to list

		await page.getByRole('link', {name: 'Back'}).click();

		const responseBody = await response.json();

		// navigate to the entry

		const entryLink = page.getByRole('link', {name: responseBody.id});

		await entryLink.click();

		// check if the saved entry is exactly as we set before

		await expect(
			page.getByRole('button', {name: FIRST_ATTACHMENT_FILE_NAME})
		).toBeVisible();

		await expect(
			page.getByRole('button', {name: SECOND_ATTACHMENT_FILE_NAME})
		).toBeVisible();

		await firstTranslationsDropdownTrigger.click();

		await catalanOptions.first().click();

		await expect(
			page.getByRole('button', {name: TRANSLATED_ATTACHMENT_FILE_NAME})
		).toBeVisible();

		await expect(
			page.getByRole('button', {name: SECOND_ATTACHMENT_FILE_NAME})
		).toBeVisible();
	});

	test('Boolean fields', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinitionLabel = 'ObjectDefinitionLabel' + getRandomInt();
		const objectDefinitionName = 'ObjectDefinitionName' + getRandomInt();

		const {objectFields, titleObjectFieldName} = await mockObjectFields({
			apiHelpers,
			localizeAllLocalizable: true,
			objectFieldBusinessTypes: ['boolean', 'boolean'],
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionApi);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				enableLocalization: true,
				label: {
					en_US: objectDefinitionLabel,
				},
				name: objectDefinitionName,
				objectFields,
				pluralLabel: {
					en_US: objectDefinitionLabel,
				},
				portlet: true,
				scope: 'company',
				status: {
					code: 0,
				},
				titleObjectFieldName,
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.addObjectEntryButton.click();

		const firstCheckBox = page.getByRole('checkbox', {
			name: objectFields[0].label['en_US'],
		});

		const secondCheckBox = page.getByRole('checkbox', {
			name: objectFields[1].label['en_US'],
		});

		const firstTranslationsDropdownTrigger = page
			.getByTestId('triggerButton')
			.first();

		const secondTranslationsDropdownTrigger = page
			.getByTestId('triggerButton')
			.nth(1);

		// with english locale, select both checkboxes

		await firstCheckBox.check();

		await secondCheckBox.check();

		// use first dropdown locale to switch to catalan

		await firstTranslationsDropdownTrigger.click();

		const catalanOptions = page.getByTestId(
			'availableLocalesDropdownca_ES'
		);

		await catalanOptions.first().click();

		// with catalan locale selected for the first time, all values should be copied from english

		await expect(firstCheckBox).toBeChecked();

		await expect(secondCheckBox).toBeChecked();

		// uncheck firt catalan checkbox, to differentiate from english

		await firstCheckBox.uncheck();

		secondTranslationsDropdownTrigger.click();

		// check for labels in dropdown, catalan should show as translated

		await expect(
			catalanOptions.first().locator('.label-item-expand')
		).toHaveText('translated', {ignoreCase: true});

		const englishOption = page.getByTestId('availableLocalesDropdownen_US');

		await expect(
			englishOption.first().locator('.label-item-expand')
		).toHaveText('default', {ignoreCase: true});

		// save

		const responsePromise = page.waitForResponse(
			`**${objectDefinition.restContextPath}`
		);

		await catalanOptions.nth(1).click();

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		const response = await responsePromise;

		await expect(
			page.getByText('Success:Your request completed successfully.')
		).toBeVisible();

		// go back to list

		await page.getByRole('link', {name: 'Back'}).click();

		const responseBody = await response.json();

		// navigate to the entry

		const entryLink = page.getByRole('link', {name: responseBody.id});

		await entryLink.click();

		// check if the saved entry is exactly as we set before

		await expect(firstCheckBox).toBeChecked();

		await expect(secondCheckBox).toBeChecked();

		await firstTranslationsDropdownTrigger.click();

		await catalanOptions.first().click();

		await expect(firstCheckBox).not.toBeChecked();

		await expect(secondCheckBox).toBeChecked();
	});

	test('Date fields', async ({apiHelpers, page, viewObjectEntriesPage}) => {
		const objectDefinitionLabel = 'ObjectDefinitionLabel' + getRandomInt();
		const objectDefinitionName = 'ObjectDefinitionName' + getRandomInt();

		const {objectFields, titleObjectFieldName} = await mockObjectFields({
			apiHelpers,
			localizeAllLocalizable: true,
			objectFieldBusinessTypes: ['date', 'dateTime'],
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionApi);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				enableLocalization: true,
				label: {
					en_US: objectDefinitionLabel,
				},
				name: objectDefinitionName,
				objectFields,
				pluralLabel: {
					en_US: objectDefinitionLabel,
				},
				portlet: true,
				scope: 'company',
				status: {
					code: 0,
				},
				titleObjectFieldName,
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.addObjectEntryButton.click();

		const dateInput = page.getByPlaceholder('__/__/____').first();

		const dateTimeInput = page.getByPlaceholder('__/__/____').nth(1);

		const firstTranslationsDropdownTrigger = page
			.getByTestId('triggerButton')
			.first();

		const secondTranslationsDropdownTrigger = page
			.getByTestId('triggerButton')
			.nth(1);

		// with english locale, fill both inputs

		await dateInput.fill('01/10/2025');

		await dateTimeInput.fill('02/20/2025 10:00 PM');

		// use first dropdown locale to switch to catalan

		await firstTranslationsDropdownTrigger.click();

		const catalanOptions = page.getByTestId(
			'availableLocalesDropdownca_ES'
		);

		await catalanOptions.first().click();

		// with catalan locale selected for the first time, all values should be copied from english

		await expect(dateInput).toHaveValue('10/01/2025');

		await expect(dateTimeInput).toHaveValue('20/02/2025 22:00');

		// change first catalan input

		await dateInput.fill('11/01/2025');

		await secondTranslationsDropdownTrigger.click();

		// check for labels in dropdown, catalan should show as translated

		await expect(
			catalanOptions.first().locator('.label-item-expand')
		).toHaveText('translated', {ignoreCase: true});

		const englishOption = page.getByTestId('availableLocalesDropdownen_US');

		await expect(
			englishOption.first().locator('.label-item-expand')
		).toHaveText('default', {ignoreCase: true});

		// save

		const responsePromise = page.waitForResponse(
			`**${objectDefinition.restContextPath}`
		);

		await catalanOptions.nth(1).click();

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		const response = await responsePromise;

		await expect(
			page.getByText('Success:Your request completed successfully.')
		).toBeVisible();

		// go back to list

		await page.getByRole('link', {name: 'Back'}).click();

		const responseBody = await response.json();

		// navigate to the entry

		const entryLink = page.getByRole('link', {name: responseBody.id});

		await entryLink.click();

		// check if the saved entry is exactly as we set before

		await expect(dateInput).toHaveValue('01/10/2025');

		await expect(dateTimeInput).toHaveValue('02/20/2025 10:00 PM');

		await firstTranslationsDropdownTrigger.click();

		await catalanOptions.first().click();

		await expect(dateInput).toHaveValue('11/01/2025');

		await expect(dateTimeInput).toHaveValue('20/02/2025 22:00');
	});

	test('Multiselect Picklist fields', async ({
		apiHelpers,
		formFieldsPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinitionLabel = 'ObjectDefinitionLabel' + getRandomInt();
		const objectDefinitionName = 'ObjectDefinitionName' + getRandomInt();

		const {
			listTypeDefinitionItems,
			objectFields,
			titleObjectFieldName,
			translatedListTypeDefinitionItems,
		} = await mockObjectFields({
			apiHelpers,
			localeToTranslateListTypeItems: 'ca_ES',
			localizeAllLocalizable: true,
			objectFieldBusinessTypes: [
				'multiselectPicklist',
				'multiselectPicklist',
			],
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionApi);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				enableLocalization: true,
				label: {
					en_US: objectDefinitionLabel,
				},
				name: objectDefinitionName,
				objectFields,
				pluralLabel: {
					en_US: objectDefinitionLabel,
				},
				portlet: true,
				scope: 'company',
				status: {
					code: 0,
				},
				titleObjectFieldName,
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.addObjectEntryButton.click();

		for (const item of listTypeDefinitionItems) {
			await formFieldsPage.addSelectItem(item, 0);
			await formFieldsPage.addSelectItem(item, 1);
		}

		const responsePromise = page.waitForResponse(
			`**${objectDefinition.restContextPath}`
		);

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		const response = await responsePromise;

		// expect new entry to be saved successfully

		await expect(
			page.getByText('Success:Your request completed successfully.')
		).toBeVisible();

		await page.getByRole('link', {name: 'Back'}).click();

		const responseBody = await response.json();

		const entryLink = page.getByRole('link', {name: responseBody.id});

		await entryLink.click();

		// expect saved entry to have all added items

		for (const item of listTypeDefinitionItems) {
			await expect(
				page.getByRole('row', {name: `Remove ${item}`}).first()
			).toBeVisible();

			await expect(
				page.getByRole('row', {name: `Remove ${item}`}).nth(1)
			).toBeVisible();
		}

		// expect unaltered entry to be saved successfully

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(
			page.getByText('Success:Your request completed successfully.')
		).toBeVisible();

		// remove the first item from the first field
		// so expect this locator to only be found once after save

		await formFieldsPage.removeMultipleSelectItem(
			listTypeDefinitionItems[0],
			0
		);

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		const itemLocators = formFieldsPage.getMultipleSelectItemsLocators(
			listTypeDefinitionItems
		);

		async function expectFinalEnglishState() {
			await expect(itemLocators[0]).toHaveCount(1);

			for (let index = 1; index <= 2; index++) {
				await expect(itemLocators[index].nth(0)).toBeVisible();
				await expect(itemLocators[index].nth(1)).toBeVisible();
			}
		}

		await expectFinalEnglishState();

		// after navigating to catalan for the first time
		// expect catalan items to be a copy of the default language

		await page.waitForTimeout(2000);

		const translationsDropdownTrigger = page
			.getByTestId('triggerButton')
			.first();

		await translationsDropdownTrigger.click();

		const catalanOption = page.getByTestId('availableLocalesDropdownca_ES');

		await catalanOption.first().click();

		const catalanItemLocators = translatedListTypeDefinitionItems.map(
			(item) => page.getByRole('row', {name: `Remove ${item}`})
		);

		expect(catalanItemLocators[0]).toHaveCount(1);

		for (let index = 1; index <= 2; index++) {
			await expect(catalanItemLocators[index].nth(0)).toBeVisible();
			await expect(catalanItemLocators[index].nth(1)).toBeVisible();
		}

		// remove some of the items from catalan entry

		await formFieldsPage.removeMultipleSelectItem(
			listTypeDefinitionItems[2],
			0
		);
		await formFieldsPage.removeMultipleSelectItem(
			listTypeDefinitionItems[2],
			0
		);
		await formFieldsPage.removeMultipleSelectItem(
			listTypeDefinitionItems[1],
			1
		);

		// expect only the remaining to be visible

		async function expectFinalCatalanState() {
			await expect(catalanItemLocators[0]).toBeVisible();
			await expect(catalanItemLocators[1]).toBeVisible();
		}

		await expectFinalCatalanState();

		// save, navigate back to entry and expect final states to be persisted

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await page.waitForTimeout(2000);

		await page.getByRole('link', {name: 'Back'}).click();

		await entryLink.click();

		await expectFinalEnglishState();

		await translationsDropdownTrigger.click();

		await catalanOption.first().click();

		await expectFinalCatalanState();
	});

	test('Numeric fields', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinitionLabel = 'ObjectDefinitionLabel' + getRandomInt();
		const objectDefinitionName = 'ObjectDefinitionName' + getRandomInt();

		const {objectFields, titleObjectFieldName} = await mockObjectFields({
			apiHelpers,
			localizeAllLocalizable: true,
			objectFieldBusinessTypes: [
				'decimal',
				'integer',
				'longInteger',
				'precisionDecimal',
			],
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionApi);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				enableLocalization: true,
				label: {
					en_US: objectDefinitionLabel,
				},
				name: objectDefinitionName,
				objectFields,
				pluralLabel: {
					en_US: objectDefinitionLabel,
				},
				portlet: true,
				scope: 'company',
				status: {
					code: 0,
				},
				titleObjectFieldName,
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.addObjectEntryButton.click();

		let englishValues: {[key: string]: string} = {};

		for (const {businessType, label, name} of objectFields) {
			if (
				businessType === 'Decimal' ||
				businessType === 'PrecisionDecimal'
			) {
				englishValues = {
					...englishValues,
					[`${name}`]: String(getRandomDouble()),
				};

				await page.getByLabel(label['en_US']).fill(englishValues[name]);
			}
			else {
				englishValues = {
					...englishValues,
					[`${name}`]: String(getRandomInt()).substring(0, 5),
				};

				await page.getByLabel(label['en_US']).fill(englishValues[name]);
			}
		}

		const translationsDropdownTrigger = page
			.getByTestId('triggerButton')
			.first();

		await translationsDropdownTrigger.click();

		const catalanOption = page.getByTestId('availableLocalesDropdownca_ES');

		await catalanOption.first().click();

		const responsePromise = page.waitForResponse(
			`**${objectDefinition.restContextPath}`
		);

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		const response = await responsePromise;

		await expect(
			page.getByText('Success:Your request completed successfully.')
		).toBeVisible();

		await page.getByRole('link', {name: 'Back'}).click();

		const responseBody = await response.json();

		const entryLink = page.getByRole('link', {name: responseBody.id});

		await entryLink.click();

		await translationsDropdownTrigger.click();

		await catalanOption.first().click();

		let catalanValues: {[key: string]: string} = {};

		for (const {businessType, label, name} of objectFields) {
			const input = page.getByLabel(label['en_US']);

			expect(
				(await input.inputValue()) ===
					englishValues[name].replace('.', ',')
			).toBeTruthy();

			if (
				businessType === 'Decimal' ||
				businessType === 'PrecisionDecimal'
			) {
				catalanValues = {
					...catalanValues,
					[`${name}`]: String(getRandomDouble()).replace('.', ','),
				};

				await input.fill(catalanValues[name]);
			}
			else {
				catalanValues = {
					...catalanValues,
					[`${name}`]: String(getRandomInt()).substring(0, 5),
				};

				await page.getByLabel(label['en_US']).fill(catalanValues[name]);
			}
		}

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await page.waitForTimeout(1000);

		await page.getByRole('link', {name: 'Back'}).click();

		await entryLink.click();

		for (const {label, name} of objectFields) {
			const input = page.getByLabel(label['en_US']);

			expect(
				(await input.inputValue()) === englishValues[name]
			).toBeTruthy();
		}

		await translationsDropdownTrigger.click();

		await catalanOption.first().click();

		for (const {label, name} of objectFields) {
			const inputValue = await page
				.getByLabel(label['en_US'])
				.inputValue();

			expect(inputValue === catalanValues[name]).toBeTruthy();
		}
	});

	test('Picklist fields', async ({
		apiHelpers,
		formFieldsPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinitionLabel = 'ObjectDefinitionLabel' + getRandomInt();
		const objectDefinitionName = 'ObjectDefinitionName' + getRandomInt();

		const {
			listTypeDefinitionItems,
			objectFields,
			titleObjectFieldName,
			translatedListTypeDefinitionItems,
		} = await mockObjectFields({
			apiHelpers,
			localeToTranslateListTypeItems: 'ca_ES',
			localizeAllLocalizable: true,
			objectFieldBusinessTypes: ['picklist', 'picklist'],
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionApi);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				enableLocalization: true,
				label: {
					en_US: objectDefinitionLabel,
				},
				name: objectDefinitionName,
				objectFields,
				pluralLabel: {
					en_US: objectDefinitionLabel,
				},
				portlet: true,
				scope: 'company',
				status: {
					code: 0,
				},
				titleObjectFieldName,
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.addObjectEntryButton.click();

		await formFieldsPage.addSelectItem(listTypeDefinitionItems[0], 0);
		await formFieldsPage.addSelectItem(listTypeDefinitionItems[1], 1);

		const responsePromise = page.waitForResponse(
			`**${objectDefinition.restContextPath}`
		);

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		const response = await responsePromise;

		// expect new entry to be saved successfully

		await expect(
			page.getByText('Success:Your request completed successfully.')
		).toBeVisible();

		await page.getByRole('link', {name: 'Back'}).click();

		const responseBody = await response.json();

		const entryLink = page.getByRole('link', {name: responseBody.id});

		await entryLink.click();

		// expect saved entry to have all added items

		const englishItemLocators = listTypeDefinitionItems.map((item) =>
			page.getByRole('combobox').filter({hasText: item})
		);

		async function expectFinalEnglishState() {
			await expect(englishItemLocators[0]).toBeVisible();
			await expect(englishItemLocators[1]).toBeVisible();
		}

		await expectFinalEnglishState();

		// expect unaltered entry to be saved successfully

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(
			page.getByText('Success:Your request completed successfully.')
		).toBeVisible();

		await expectFinalEnglishState();

		// after navigating to catalan for the first time
		// expect catalan items to be a copy of the default language

		const translationsDropdownTrigger = page
			.getByTestId('triggerButton')
			.first();

		await translationsDropdownTrigger.click();

		const catalanOption = page.getByTestId('availableLocalesDropdownca_ES');

		await catalanOption.first().click();

		const catalanItemLocators = translatedListTypeDefinitionItems.map(
			(item) => page.getByRole('combobox').filter({hasText: item})
		);

		async function expectFinalCatalanState() {
			await expect(catalanItemLocators[0]).toBeVisible();
			await expect(catalanItemLocators[1]).toBeVisible();
		}

		await expectFinalCatalanState();

		// save, navigate back to entry and expect final states to be persisted

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await page
			.getByText('Success:Your request completed successfully.')
			.first()
			.waitFor({state: 'hidden'});

		await page.getByRole('link', {name: 'Back'}).click();

		await entryLink.click();

		await expectFinalEnglishState();

		await translationsDropdownTrigger.click();

		await catalanOption.first().click();

		await expectFinalCatalanState();
	});
});

test.describe('Required localized object fields', () => {
	test('assert that when required error is thrown the locale dropdown switches back to the default language', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinitionLabel = 'ObjectDefinitionLabel' + getRandomInt();
		const objectDefinitionName = 'ObjectDefinitionName' + getRandomInt();

		const objectFields = createObjectFields(
			'boolean',
			[
				{
					label: 'booleanField',
					name: 'booleanField',
				},
			],
			{required: true},
			true
		);

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionApi);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				enableLocalization: true,
				label: {
					en_US: objectDefinitionLabel,
				},
				name: objectDefinitionName,
				objectFields,
				pluralLabel: {
					en_US: objectDefinitionLabel,
				},
				portlet: true,
				scope: 'company',
				status: {
					code: 0,
				},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.addObjectEntryButton.click();

		await expect(page.getByRole('button', {name: 'en-us'})).toBeVisible();

		const translationsDropdownTrigger = page
			.getByTestId('triggerButton')
			.first();

		await translationsDropdownTrigger.click();

		const englishOption = page.getByTestId('availableLocalesDropdownen_US');

		await expect(englishOption.locator('.label-item-expand')).toHaveText(
			'default',
			{ignoreCase: true}
		);

		const catalanOption = page.getByTestId('availableLocalesDropdownca_ES');

		await catalanOption.locator('.label-item-expand').click();

		await expect(page.getByRole('button', {name: 'ca-es'})).toBeVisible();

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(page.getByRole('button', {name: 'en-us'})).toBeVisible();

		await expect(
			page
				.locator('.form-feedback-item')
				.getByText('This field is required.')
		).toBeVisible();

		await page.getByRole('checkbox').check();

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(
			page.getByText('Success:Your request completed successfully.')
		).toBeVisible();
	});

	test('verify that default language id is required', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinitionLabel = 'ObjectDefinitionLabel' + getRandomInt();
		const objectDefinitionName = 'ObjectDefinitionName' + getRandomInt();

		const objectFields = createObjectFields(
			'text',
			[
				{
					label: 'textField',
					name: 'textField',
				},
			],
			{required: true},
			true
		);

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionApi);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				enableLocalization: true,
				label: {
					en_US: objectDefinitionLabel,
				},
				name: objectDefinitionName,
				objectFields,
				pluralLabel: {
					en_US: objectDefinitionLabel,
				},
				portlet: true,
				scope: 'company',
				status: {
					code: 0,
				},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				defaultLanguageId: 'ca_ES',
				textField: getRandomString(),
			},
			'c/' + objectDefinition.name.toLowerCase() + 's'
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await page.getByRole('link', {name: String(objectEntry.id)}).click();

		await expect(page.getByRole('button', {name: 'ca-es'})).toBeVisible();

		const translationsDropdownTrigger = page
			.getByTestId('triggerButton')
			.first();

		await translationsDropdownTrigger.click();

		const catalanOption = page.getByTestId('availableLocalesDropdownca_ES');

		await expect(catalanOption.locator('.label-item-expand')).toHaveText(
			'default',
			{ignoreCase: true}
		);

		await catalanOption.locator('.label-item-expand').click();

		const fieldInput = page.getByTestId('visibleChangeInput');

		await fieldInput.fill(getRandomString());

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(
			page.getByText('Success:Your request completed successfully.')
		).toBeVisible();

		await fieldInput.fill('');

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(
			page.getByText('This field is required.', {exact: true})
		).toBeVisible();

		await translationsDropdownTrigger.click();

		const englishOption = page.getByTestId('availableLocalesDropdownen_US');

		await englishOption.locator('.label-item-expand').click();

		await fieldInput.fill(getRandomString());

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(
			page.getByText('This field is required.', {exact: true})
		).toBeVisible();
	});
});

/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinitionAPI,
	ObjectRelationshipAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {accountSettingsPagesTest} from '../../../fixtures/accountSettingsPagesTest';
import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';
import {collectionsPagesTest} from '../../../fixtures/collectionsPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {editObjectDefinitionPagesTest} from '../../../fixtures/editObjectDefinitionPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {formsPagesTest} from '../../../fixtures/formsPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import {getRandomDouble} from '../../../utils/getRandomDouble';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import {generateObjectFields} from './utils/generateObjectFields';
import {postListTypeDefinitionListTypeEntries} from './utils/postListTypeDefinitionListTypeEntries';

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

		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: [
				{
					businessType: 'Attachment',
					localized: true,
				},
				{
					businessType: 'Attachment',
					localized: true,
				},
			],
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
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

		await viewObjectEntriesPage.clickAddObjectEntry(objectDefinitionLabel);

		const FIRST_ATTACHMENT_FILE_NAME = 'astronaut.png';

		const SECOND_ATTACHMENT_FILE_NAME = 'planet.png';

		const firstUplodadButton = page
			.getByRole('button', {name: 'Select File'})
			.first();

		const secondUploadButton = page
			.getByRole('button', {name: 'Select File'})
			.nth(1);

		const firstTranslationsDropdownTrigger = page
			.getByRole('button', {name: 'en-us'})
			.first();

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

		const catalanOption = page.getByRole('menuitem', {
			name: 'català (Espanya)',
		});

		await catalanOption.first().click();

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

		await page.getByRole('button', {name: 'ca-es'}).last().click();

		// check for labels in dropdown, catalan should show as translated

		await expect(catalanOption.getByText('translated')).toBeVisible();

		await expect(
			page
				.getByRole('menuitem', {
					name: 'English (United States)',
				})
				.getByText('default')
		).toBeVisible();

		// save

		const responsePromise = page.waitForResponse(
			`**${objectDefinition.restContextPath}`
		);

		await catalanOption.click();

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		const response = await responsePromise;

		await expect(
			page.getByText('Success:Your request completed successfully.')
		).toBeVisible();

		// go back to list

		await viewObjectEntriesPage.backButton.click();

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

		await catalanOption.click();

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

		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: [
				{
					businessType: 'Boolean',
					localized: true,
				},
				{
					businessType: 'Boolean',
					localized: true,
				},
			],
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

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

		await viewObjectEntriesPage.clickAddObjectEntry(objectDefinitionLabel);

		const firstCheckBox = page.getByRole('checkbox', {
			name: objectFields[0].label['en_US'],
		});

		const secondCheckBox = page.getByRole('checkbox', {
			name: objectFields[1].label['en_US'],
		});

		// with english locale, select both checkboxes

		await firstCheckBox.check();

		await secondCheckBox.check();

		// use first dropdown locale to switch to catalan

		await page.getByRole('button', {name: 'en-us'}).first().click();

		const catalanOption = page.getByRole('menuitem', {
			name: 'català (Espanya)',
		});

		await catalanOption.click();

		// with catalan locale selected for the first time, all values should be copied from english

		await expect(firstCheckBox).toBeChecked();

		await expect(secondCheckBox).toBeChecked();

		// uncheck firt catalan checkbox, to differentiate from english

		await firstCheckBox.uncheck();

		await page.getByRole('button', {name: 'ca-es'}).last().click();

		// check for labels in dropdown, catalan should show as translated

		await expect(catalanOption.getByText('translated')).toBeVisible();

		await expect(
			page
				.getByRole('menuitem', {
					name: 'English (United States)',
				})
				.getByText('default')
		).toBeVisible();

		// save

		const responsePromise = page.waitForResponse(
			`**${objectDefinition.restContextPath}`
		);

		await catalanOption.click();

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		const response = await responsePromise;

		await expect(
			page.getByText('Success:Your request completed successfully.')
		).toBeVisible();

		// go back to list

		await viewObjectEntriesPage.backButton.click();

		const responseBody = await response.json();

		// navigate to the entry

		const entryLink = page.getByRole('link', {name: responseBody.id});

		await entryLink.click();

		// check if the saved entry is exactly as we set before

		await expect(firstCheckBox).toBeChecked();

		await expect(secondCheckBox).toBeChecked();

		await page.getByRole('button', {name: 'en-us'}).first().click();

		await catalanOption.first().click();

		await expect(firstCheckBox).not.toBeChecked();

		await expect(secondCheckBox).toBeChecked();
	});

	test('Date fields', async ({apiHelpers, page, viewObjectEntriesPage}) => {
		const objectDefinitionLabel = 'ObjectDefinitionLabel' + getRandomInt();
		const objectDefinitionName = 'ObjectDefinitionName' + getRandomInt();

		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: [
				{
					businessType: 'Date',
					localized: true,
				},
				{
					businessType: 'DateTime',
					localized: true,
				},
			],
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

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

		await viewObjectEntriesPage.clickAddObjectEntry(objectDefinitionLabel);

		const dateInput = page.getByPlaceholder('__/__/____').first();

		const dateTimeInput = page.getByPlaceholder('__/__/____').nth(1);

		// with english locale, fill both inputs

		await dateInput.fill('01/10/2025');

		await dateTimeInput.fill('02/20/2025 10:00 PM');

		// use first dropdown locale to switch to catalan

		await page.getByRole('button', {name: 'en-us'}).first().click();

		const catalanOption = page.getByRole('menuitem', {
			name: 'català (Espanya)',
		});

		await catalanOption.click();

		// with catalan locale selected for the first time, all values should be copied from english

		await expect(dateInput).toHaveValue('10/01/2025');

		await expect(dateTimeInput).toHaveValue('20/02/2025 22:00');

		// change first catalan input

		await dateInput.fill('11/01/2025');

		await page.getByRole('button', {name: 'ca-es'}).last().click();

		// check for labels in dropdown, catalan should show as translated

		await expect(catalanOption.getByText('translated')).toBeVisible();

		await expect(
			page
				.getByRole('menuitem', {
					name: 'English (United States)',
				})
				.getByText('default')
		).toBeVisible();

		// save

		const responsePromise = page.waitForResponse(
			`**${objectDefinition.restContextPath}`
		);

		await catalanOption.click();

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		const response = await responsePromise;

		await expect(
			page.getByText('Success:Your request completed successfully.')
		).toBeVisible();

		// go back to list

		await viewObjectEntriesPage.backButton.click();

		const responseBody = await response.json();

		// navigate to the entry

		const entryLink = page.getByRole('link', {name: responseBody.id});

		await entryLink.click();

		// check if the saved entry is exactly as we set before

		await expect(dateInput).toHaveValue('01/10/2025');

		await expect(dateTimeInput).toHaveValue('02/20/2025 10:00 PM');

		await page.getByRole('button', {name: 'en-us'}).first().click();

		await catalanOption.click();

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

		const {listTypeDefinition, listTypeEntries} =
			await postListTypeDefinitionListTypeEntries({
				apiHelpers,
				locale: 'ca_ES',
			});

		const objectFields = generateObjectFields({
			listTypeDefinitionExternalReferenceCode:
				listTypeDefinition.externalReferenceCode,
			objectFieldBusinessTypes: [
				{
					businessType: 'MultiselectPicklist',
					localized: true,
				},
				{
					businessType: 'MultiselectPicklist',
					localized: true,
				},
			],
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

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

		await viewObjectEntriesPage.clickAddObjectEntry(objectDefinitionLabel);

		for (const {name_i18n: listTypeEntry_i18n} of listTypeEntries) {
			await formFieldsPage.addSelectItem(listTypeEntry_i18n['en-US'], 0);
			await formFieldsPage.addSelectItem(listTypeEntry_i18n['en-US'], 1);
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

		await viewObjectEntriesPage.backButton.click();

		const responseBody = await response.json();

		const entryLink = page.getByRole('link', {name: responseBody.id});

		await entryLink.click();

		// expect saved entry to have all added items

		for (const {name_i18n: listTypeEntry_i18n} of listTypeEntries) {
			await expect(
				page
					.getByRole('row', {
						name: `Remove ${listTypeEntry_i18n['en-US']}`,
					})
					.first()
			).toBeVisible();

			await expect(
				page
					.getByRole('row', {
						name: `Remove ${listTypeEntry_i18n['en-US']}`,
					})
					.nth(1)
			).toBeVisible();
		}

		// expect unaltered entry to be saved successfully

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(
			page.getByText('Success:Your request completed successfully.')
		).toBeVisible();

		// remove the first item from the first field
		// so expect this locator to only be found once after save

		const listTypeDefinitionEntry = listTypeEntries[0];

		await formFieldsPage.removeMultipleSelectItem(
			listTypeDefinitionEntry.name_i18n['en-US'],
			0
		);

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		const itemLocators = formFieldsPage.getMultipleSelectItemsLocators(
			listTypeEntries.map(
				(listTypeEntry) => listTypeEntry.name_i18n['en-US']
			)
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

		await page.getByRole('button', {name: 'en-us'}).first().click();

		const catalanOption = page.getByRole('menuitem', {
			name: 'català (Espanya)',
		});

		await catalanOption.click();

		const catalanItemLocators = listTypeEntries.map((listTypeEntry) =>
			page.getByRole('row', {
				name: `Remove ${listTypeEntry.name_i18n['ca-ES']}`,
			})
		);

		expect(catalanItemLocators[0]).toHaveCount(1);

		for (let index = 1; index <= 2; index++) {
			await expect(catalanItemLocators[index].nth(0)).toBeVisible();
			await expect(catalanItemLocators[index].nth(1)).toBeVisible();
		}

		// remove some of the items from catalan entry

		await formFieldsPage.removeMultipleSelectItem(
			listTypeEntries[0].name_i18n['en-US'],
			0
		);

		await formFieldsPage.removeMultipleSelectItem(
			listTypeEntries[1].name_i18n['en-US'],
			1
		);

		// expect only the remaining to be visible

		async function expectFinalCatalanState() {
			await expect(catalanItemLocators[1].nth(0)).toBeVisible();
			await expect(catalanItemLocators[2].nth(0)).toBeVisible();
			await expect(catalanItemLocators[3].nth(0)).toBeVisible();
			await expect(catalanItemLocators[2].nth(1)).toBeVisible();
			await expect(catalanItemLocators[3].nth(1)).toBeVisible();
		}

		await expectFinalCatalanState();

		// save, navigate back to entry and expect final states to be persisted

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await page.waitForTimeout(2000);

		await viewObjectEntriesPage.backButton.click();

		await entryLink.click();

		await expectFinalEnglishState();

		await page.getByRole('button', {name: 'en-us'}).first().click();

		await catalanOption.click();

		await expectFinalCatalanState();
	});

	test('Non-localizable object fields are disabled and have correct tooltip information when managing translations', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinitionLabel = 'ObjectDefinitionLabel' + getRandomInt();
		const objectDefinitionName = 'ObjectDefinitionName' + getRandomInt();

		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: [
				{
					businessType: 'Encrypted',
					localized: false,
				},
				{
					businessType: 'Text',
					localized: false,
				},
				{
					businessType: 'Integer',
					localized: true,
				},
			],
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

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

		await viewObjectEntriesPage.clickAddObjectEntry(objectDefinitionLabel);

		await page.getByRole('button', {name: 'en-us'}).first().click();

		await page
			.getByRole('menuitem', {
				name: 'català (Espanya)',
			})
			.click();

		const encryptedContainer = page
			.locator('.form-group')
			.filter({has: page.getByLabel(objectFields[0].label['en_US'])});

		await expect(encryptedContainer.getByRole('textbox')).toBeDisabled();

		await expect(
			encryptedContainer.getByTitle(
				'This field does not support translations.'
			)
		).toBeVisible();

		const textContainer = page
			.locator('.form-group')
			.filter({has: page.getByLabel(objectFields[1].label['en_US'])});

		await expect(textContainer.getByRole('textbox')).toBeDisabled();

		await expect(
			textContainer.getByTitle('Translation is disabled for this field.')
		).toBeVisible();

		await page.getByRole('button', {name: 'ca-es'}).first().click();

		await page
			.getByRole('menuitem', {
				name: 'English (United States)',
			})
			.click();

		await expect(encryptedContainer.getByRole('textbox')).toBeEnabled();

		await expect(
			encryptedContainer.getByTitle(
				'This field does not support translations.'
			)
		).toBeHidden();

		await expect(textContainer.getByRole('textbox')).toBeEnabled();

		await expect(
			textContainer.getByTitle('Translation is disabled for this field.')
		).toBeHidden();
	});

	test('Numeric fields', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinitionLabel = 'ObjectDefinitionLabel' + getRandomInt();
		const objectDefinitionName = 'ObjectDefinitionName' + getRandomInt();

		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: [
				{
					businessType: 'Decimal',
					localized: true,
				},
				{
					businessType: 'Integer',
					localized: true,
				},
				{
					businessType: 'LongInteger',
					localized: true,
				},
				{
					businessType: 'PrecisionDecimal',
					localized: true,
				},
			],
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

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

		await viewObjectEntriesPage.clickAddObjectEntry(objectDefinitionLabel);

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

		await page.getByRole('button', {name: 'en-us'}).first().click();

		await page
			.getByRole('menuitem', {
				name: 'català (Espanya)',
			})
			.click();

		const responsePromise = page.waitForResponse(
			`**${objectDefinition.restContextPath}`
		);

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		const response = await responsePromise;

		await expect(
			page.getByText('Success:Your request completed successfully.')
		).toBeVisible();

		await viewObjectEntriesPage.backButton.click();

		const responseBody = await response.json();

		const entryLink = page.getByRole('link', {name: responseBody.id});

		await entryLink.click();

		await page.getByRole('button', {name: 'en-us'}).first().click();

		await page
			.getByRole('menuitem', {
				name: 'català (Espanya)',
			})
			.click();

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

		await viewObjectEntriesPage.backButton.click();

		await entryLink.click();

		for (const {label, name} of objectFields) {
			const input = page.getByLabel(label['en_US']);

			expect(
				(await input.inputValue()) === englishValues[name]
			).toBeTruthy();
		}

		await page.getByRole('button', {name: 'en-us'}).first().click();

		await page
			.getByRole('menuitem', {
				name: 'català (Espanya)',
			})
			.click();

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

		const {listTypeDefinition, listTypeEntries} =
			await postListTypeDefinitionListTypeEntries({
				apiHelpers,
				locale: 'ca_ES',
			});

		const objectFields = generateObjectFields({
			listTypeDefinitionExternalReferenceCode:
				listTypeDefinition.externalReferenceCode,
			objectFieldBusinessTypes: [
				{
					businessType: 'Picklist',
					localized: true,
				},
				{
					businessType: 'Picklist',
					localized: true,
				},
			],
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

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

		await viewObjectEntriesPage.clickAddObjectEntry(objectDefinitionLabel);

		for (let i = 0; i < 2; i++) {
			await formFieldsPage.addSelectItem(
				listTypeEntries[i].name_i18n['en-US'],
				i
			);
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

		await viewObjectEntriesPage.backButton.click();

		const responseBody = await response.json();

		const entryLink = page.getByRole('link', {name: responseBody.id});

		await entryLink.click();

		// expect saved entry to have all added items

		const englishItemLocators = listTypeEntries.map(({name_i18n}) =>
			page.getByRole('combobox').filter({hasText: name_i18n['en-US']})
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

		await page.getByRole('button', {name: 'en-us'}).first().click();

		await page
			.getByRole('menuitem', {
				name: 'català (Espanya)',
			})
			.click();

		const catalanItemLocators = listTypeEntries.map(({name_i18n}) =>
			page.getByRole('combobox').filter({
				hasText: name_i18n['ca-ES'],
			})
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

		await viewObjectEntriesPage.backButton.click();

		await entryLink.click();

		await expectFinalEnglishState();

		await page.getByRole('button', {name: 'en-us'}).first().click();

		await page
			.getByRole('menuitem', {
				name: 'català (Espanya)',
			})
			.click();

		await expectFinalCatalanState();
	});
});

test.describe('Manage object entries through Page Templates', () => {
	test('can view all entries related to an object in the relationship field', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: [
				{
					businessType: 'Boolean',
					localized: true,
					name: 'booleanField',
				},
				{
					businessType: 'Text',
					localized: true,
					name: 'textField',
				},
			],
		});

		const objectDefinitionExternalReferenceCode =
			'ObjectDefinition' + getRandomInt();

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition1} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				enableLocalization: true,
				externalReferenceCode: objectDefinitionExternalReferenceCode,
				label: {
					en_US: objectDefinitionExternalReferenceCode,
				},
				name: objectDefinitionExternalReferenceCode,
				objectFields,
				pluralLabel: {
					en_US: objectDefinitionExternalReferenceCode,
				},
				portlet: true,
				scope: 'company',
				status: {code: 0},
				titleObjectFieldName: 'booleanField',
			});

		apiHelpers.data.push({
			id: objectDefinition1.id,
			type: 'objectDefinition',
		});

		const objectDefinition2 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition2.id,
			type: 'objectDefinition',
		});

		const objectRelationshipLabel =
			'objectRelationshipLabel' + getRandomInt();
		const objectRelationshipName =
			'objectRelationshipName' + Math.floor(Math.random() * 99);

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
			objectDefinition1.externalReferenceCode,
			{
				label: {
					en_US: objectRelationshipLabel,
				},
				name: objectRelationshipName,
				objectDefinitionExternalReferenceCode1:
					objectDefinition1.externalReferenceCode,
				objectDefinitionExternalReferenceCode2:
					objectDefinition2.externalReferenceCode,
				objectDefinitionId1: objectDefinition1.id,
				objectDefinitionId2: objectDefinition2.id,
				objectDefinitionName2: objectDefinition2.name,
				type: 'oneToMany',
			}
		);

		const applicationName =
			'c/' + objectDefinition1.name.toLowerCase() + 's';

		const itemValues = [];

		for (let i = 0; i <= 15; i++) {
			const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
				{
					booleanField_i18n: {
						en_US: false,
						pt_BR: true,
					},
					textField_i18n: {
						en_US: 'entry_en_US' + i,
						pt_BR: 'entry_pt_BR' + i,
					},
				},
				applicationName
			);

			itemValues.push({
				booleanField: objectEntry.booleanField_i18n['pt_BR'],
				textField: objectEntry.textField_i18n['pt_BR'],
			});
		}

		await viewObjectEntriesPage.goto(objectDefinition2.className, 'pt');

		siteLanguage = 'pt';

		await page
			.getByLabel('Adicionar ' + objectDefinition2.label['en_US'])
			.first()
			.click();

		await viewObjectEntriesPage.editObjectEntryForm.waitFor({
			state: 'visible',
		});

		await page.getByPlaceholder('Buscar', {exact: true}).click();

		itemValues.forEach((itemValue, index) => {
			expect(
				page
					.getByRole('menuitem', {
						exact: true,
						name: String(itemValue.booleanField),
					})
					.nth(index)
			).toBeVisible();
		});

		await objectDefinitionAPIClient.patchObjectDefinition(
			objectDefinition1.id,
			{
				titleObjectFieldName: 'textField',
			}
		);

		await viewObjectEntriesPage.goto(objectDefinition2.className, 'pt');

		await page
			.getByLabel('Adicionar ' + objectDefinition2.label['en_US'])
			.first()
			.click();

		await viewObjectEntriesPage.editObjectEntryForm.waitFor({
			state: 'visible',
		});

		await page.getByPlaceholder('Buscar', {exact: true}).click();

		itemValues.forEach((itemValue) => {
			expect(
				page.getByRole('menuitem', {
					exact: true,
					name: String(itemValue.textField),
				})
			).toBeVisible();
		});
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

		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: [
				{
					businessType: 'Boolean',
					localized: true,
					required: true,
				},
			],
		});
		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

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

		await viewObjectEntriesPage.clickAddObjectEntry(objectDefinitionLabel);

		await expect(page.getByRole('button', {name: 'en-us'})).toBeVisible();

		await page.getByRole('button', {name: 'en-us'}).first().click();

		await expect(
			page
				.getByRole('menuitem', {
					name: 'English (United States)',
				})
				.getByText('default')
		).toBeVisible();

		await page
			.getByRole('menuitem', {
				name: 'català (Espanya)',
			})
			.click();

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

		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: [
				{
					businessType: 'Text',
					localized: true,
					name: 'textField',
					required: true,
				},
			],
		});
		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

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

		await page.getByRole('button', {name: 'ca-es'}).click();

		const catalanOption = page.getByRole('menuitem', {
			name: 'català (Espanya)',
		});

		await expect(catalanOption.getByText('default')).toBeVisible();

		await catalanOption.click();

		await page.getByRole('textbox').fill(getRandomString());

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(
			page.getByText('Success:Your request completed successfully.')
		).toBeVisible();

		await page.getByRole('textbox').fill('');

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(
			page.getByText('This field is required.', {exact: true})
		).toBeVisible();

		await page.getByRole('button', {name: 'ca-es'}).click();

		const englishOption = page.getByRole('menuitem', {name: /English/});

		await englishOption.click();

		await page.getByRole('textbox').fill(getRandomString());

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(
			page.getByText('This field is required.', {exact: true})
		).toBeVisible();
	});

	test('verify that labels of single/multi select picklist options are present when the field is required', async ({
		apiHelpers,
		formFieldsPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinitionLabel = 'ObjectDefinitionLabel' + getRandomInt();
		const objectDefinitionName = 'ObjectDefinitionName' + getRandomInt();

		const {listTypeDefinition, listTypeEntries} =
			await postListTypeDefinitionListTypeEntries({
				apiHelpers,
			});

		const objectFields = generateObjectFields({
			listTypeDefinitionExternalReferenceCode:
				listTypeDefinition.externalReferenceCode,
			objectFieldBusinessTypes: [
				{
					businessType: 'Picklist',
					localized: true,
					required: true,
				},
				{
					businessType: 'MultiselectPicklist',
					localized: true,
					required: true,
				},
			],
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
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

		await viewObjectEntriesPage.clickAddObjectEntry(objectDefinitionLabel);

		await expect(
			page.getByRole('button', {name: 'en-us'}).first()
		).toBeVisible();

		await formFieldsPage.addSelectItem(listTypeEntries[0].name, 0);

		await formFieldsPage.addSelectItem(listTypeEntries[0].name, 1);

		await expect(
			page.getByText('This field is required.', {exact: true})
		).not.toBeVisible();

		await expect(
			page.getByRole('gridcell', {
				exact: true,
				name: listTypeEntries[0].name,
			})
		).toBeVisible();

		await page.getByRole('combobox').nth(1).click();

		listTypeEntries.forEach((item) => {
			expect(page.getByRole('option', {name: item.name})).toBeVisible();
		});
	});
});

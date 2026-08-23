/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectDefinition} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {formsPagesTest} from '../../../fixtures/formsPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {smtpPagesTest} from '../../../fixtures/smtpPagesTest';
import {liferayConfig} from '../../../liferay.config';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {deleteItems} from '../../dynamic-data-mapping-form-web/main/utils/deleteItems';
import {
	getFDSDateFormat,
	getObjectEntryAPIDateTimeFormat,
	getObjectEntryUIDateTimeFormat,
} from '../utils/dateFormat';
import {generateObjectFields} from '../utils/generateObjectFields';
import {postListTypeDefinitionListTypeEntries} from '../utils/postListTypeDefinitionListTypeEntries';

const test = mergeTests(
	dataApiHelpersTest,
	formsPagesTest,
	loginTest(),
	objectPagesTest,
	smtpPagesTest
);

test.afterEach(async ({formsPage}) => {
	await formsPage.goTo();

	await deleteItems(formsPage);
});

test.beforeEach(({page}) => {
	page.setViewportSize({height: 1280, width: 1920});
});

test(
	'allow multiple selection option is not available for Select From List field when form is mapped to an object',
	{tag: '@LPS-139472'},
	async ({
		apiHelpers,
		formBuilderPage,
		formBuilderSidePanelPage,
		formSettingsModalPage,
		page,
	}) => {
		const {listTypeDefinition} =
			await postListTypeDefinitionListTypeEntries({
				apiHelpers,
				listTypeEntriesLength: 2,
			});

		apiHelpers.data.push({
			id: listTypeDefinition.id,
			type: 'listTypeDefinition',
		});

		const objectFields = generateObjectFields({
			listTypeDefinitionExternalReferenceCode:
				listTypeDefinition.externalReferenceCode,
			objectFieldBusinessTypes: ['Picklist'],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const picklistFieldLabel = objectFields[0].label!['en_US'];

		await formBuilderPage.goToNew();

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.selectStorageType('Object');

		await formSettingsModalPage.selectObject(
			objectDefinition.label['en_US']
		);

		await formSettingsModalPage.clickDoneButton();

		await formBuilderSidePanelPage.addFieldByDoubleClick(
			'Select from List'
		);

		await formBuilderSidePanelPage.clickAdvancedTab();

		await formBuilderSidePanelPage.selectObjectField(picklistFieldLabel);

		await expect(
			page.getByText('Allow Multiple Selections')
		).not.toBeVisible();
	}
);

test(
	'can delete a form mapped to an object after adding entry',
	{tag: '@LPS-139464'},
	async ({
		apiHelpers,
		formBuilderPage,
		formBuilderSidePanelPage,
		formSettingsModalPage,
		formsPage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const formTitle = 'Form' + getRandomInt();

		await formBuilderPage.goToNew();

		await expect(formBuilderPage.newFormHeading).toBeVisible();

		await formBuilderPage.fillFormTitle(formTitle);

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.selectStorageType('Object');

		await formSettingsModalPage.selectObject(
			objectDefinition.label['en_US']
		);

		await formSettingsModalPage.clickDoneButton();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

		await formBuilderSidePanelPage.clickAdvancedTab();

		await formBuilderSidePanelPage.selectObjectField('textField');

		await page.waitForTimeout(1000);

		await formBuilderPage.clickPublishFormButton();

		const formSubmissionURL = await formBuilderPage.getFormSubmissionURL();

		await page.goto(formSubmissionURL, {waitUntil: 'networkidle'});

		await page.getByLabel('Text').fill('Entry Test');

		await page.getByRole('button', {name: 'Save'}).click();

		await expect(
			page.getByText('Your request completed successfully.')
		).toBeVisible();

		await formsPage.goTo();

		await deleteItems(formsPage);

		await expect(
			page.getByRole('link', {exact: true, name: formTitle})
		).not.toBeVisible();
	}
);

test(
	'can map and view entries for field group',
	{tag: '@LPS-133365'},
	async ({
		apiHelpers,
		formBuilderPage,
		formBuilderSidePanelPage,
		formSettingsModalPage,
		page,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['Text', 'Text'],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const fieldLabel1 = objectFields[0].label!['en_US'];

		const fieldLabel2 = objectFields[1].label!['en_US'];

		await formBuilderPage.goToNew();

		await expect(formBuilderPage.newFormHeading).toBeVisible();

		await formBuilderPage.fillFormTitle('Form' + getRandomInt());

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.selectStorageType('Object');

		await formSettingsModalPage.selectObject(
			objectDefinition.label['en_US']
		);

		await formSettingsModalPage.clickDoneButton();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

		await formBuilderSidePanelPage.clickAdvancedTab();

		await formBuilderSidePanelPage.selectObjectField(fieldLabel1);

		await page.waitForTimeout(1000);

		await formBuilderSidePanelPage.clickBackButton();

		await formBuilderSidePanelPage.addFieldToFieldGroup('Text', 0);

		await formBuilderSidePanelPage.clickAdvancedTab();

		await formBuilderSidePanelPage.selectObjectField(fieldLabel2);

		await page.waitForTimeout(1000);

		await expect(
			page.getByLabel('Fields Group', {exact: true})
		).toBeVisible();

		await formBuilderPage.clickPublishFormButton();

		const formSubmissionURL = await formBuilderPage.getFormSubmissionURL();

		await page.goto(formSubmissionURL, {waitUntil: 'networkidle'});

		const textValue1 = getRandomString();

		const textValue2 = getRandomString();

		await page.getByLabel('Text', {exact: true}).first().fill(textValue1);

		await page.getByLabel('Text', {exact: true}).last().fill(textValue2);

		await page.getByRole('button', {name: 'Save'}).click();

		await expect(
			page.getByText('Your request completed successfully.')
		).toBeVisible();

		const {items} =
			await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
				'c/' + objectDefinition.name.toLowerCase() + 's'
			);

		expect(items).toHaveLength(1);

		const fieldName1 = objectFields[0].name;

		const fieldName2 = objectFields[1].name;

		expect(items[0][fieldName1]).toBe(textValue1);

		expect(items[0][fieldName2]).toBe(textValue2);
	}
);

test(
	'can map object field of Boolean type to form and add entries',
	{tag: '@LPS-133365'},
	async ({
		apiHelpers,
		formBuilderPage,
		formBuilderSidePanelPage,
		formSettingsModalPage,
		page,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['Boolean'],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const fieldLabel = objectFields[0].label!['en_US'];

		await formBuilderPage.goToNew();

		await expect(formBuilderPage.newFormHeading).toBeVisible();

		await formBuilderPage.fillFormTitle('Form' + getRandomInt());

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.selectStorageType('Object');

		await formSettingsModalPage.selectObject(
			objectDefinition.label['en_US']
		);

		await formSettingsModalPage.clickDoneButton();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Boolean');

		await formBuilderSidePanelPage.clickAdvancedTab();

		await formBuilderSidePanelPage.selectObjectField(fieldLabel);

		await page.waitForTimeout(1000);

		await formBuilderPage.clickPublishFormButton();

		const formSubmissionURL = await formBuilderPage.getFormSubmissionURL();

		await page.goto(formSubmissionURL, {waitUntil: 'networkidle'});

		await page.getByLabel('Boolean').check();

		await page.getByRole('button', {name: 'Save'}).click();

		await expect(
			page.getByText('Your request completed successfully.')
		).toBeVisible();

		const {items} =
			await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
				'c/' + objectDefinition.name.toLowerCase() + 's'
			);

		expect(items).toHaveLength(1);

		const fieldName = objectFields[0].name;

		expect(items[0][fieldName]).toBe(true);
	}
);

test(
	'can map object field of Date type to form and add entries',
	{tag: '@LPS-133365'},
	async ({
		apiHelpers,
		formBuilderPage,
		formBuilderSidePanelPage,
		formSettingsModalPage,
		page,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['Date'],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const fieldLabel = objectFields[0].label!['en_US'];

		await formBuilderPage.goToNew();

		await expect(formBuilderPage.newFormHeading).toBeVisible();

		await formBuilderPage.fillFormTitle('Form' + getRandomInt());

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.selectStorageType('Object');

		await formSettingsModalPage.selectObject(
			objectDefinition.label['en_US']
		);

		await formSettingsModalPage.clickDoneButton();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Date');

		await formBuilderSidePanelPage.clickAdvancedTab();

		await formBuilderSidePanelPage.selectObjectField(fieldLabel);

		await page.waitForTimeout(1000);

		await formBuilderPage.clickPublishFormButton();

		const formSubmissionURL = await formBuilderPage.getFormSubmissionURL();

		await page.goto(formSubmissionURL, {waitUntil: 'networkidle'});

		const date = new Date();

		const inputDate = getObjectEntryUIDateTimeFormat(date);

		await page.getByRole('textbox', {name: 'Date'}).fill(inputDate);

		await page.getByRole('button', {name: 'Save'}).click();

		await expect(
			page.getByText('Your request completed successfully.')
		).toBeVisible();

		const {items} =
			await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
				'c/' + objectDefinition.name.toLowerCase() + 's'
			);

		expect(items).toHaveLength(1);

		const apiDate = getObjectEntryAPIDateTimeFormat(date).split('T')[0];

		const fieldName = objectFields[0].name;

		expect(items[0][fieldName]).toContain(apiDate);
	}
);

test(
	'can map object field of Decimal type to form and add entries',
	{tag: '@LPS-133365'},
	async ({
		apiHelpers,
		formBuilderPage,
		formBuilderSidePanelPage,
		formSettingsModalPage,
		page,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['Decimal'],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const fieldLabel = objectFields[0].label!['en_US'];

		await formBuilderPage.goToNew();

		await expect(formBuilderPage.newFormHeading).toBeVisible();

		await formBuilderPage.fillFormTitle('Form' + getRandomInt());

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.selectStorageType('Object');

		await formSettingsModalPage.selectObject(
			objectDefinition.label['en_US']
		);

		await formSettingsModalPage.clickDoneButton();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Numeric');

		await formBuilderSidePanelPage.numericTypeDecimal.click();

		await formBuilderSidePanelPage.clickAdvancedTab();

		await formBuilderSidePanelPage.selectObjectField(fieldLabel);

		await page.waitForTimeout(1000);

		await formBuilderPage.clickPublishFormButton();

		const formSubmissionURL = await formBuilderPage.getFormSubmissionURL();

		await page.goto(formSubmissionURL, {waitUntil: 'networkidle'});

		const decimalValue = (getRandomInt() / 100).toFixed(2);

		await page.getByLabel('Numeric').fill(decimalValue);

		await page.getByRole('button', {name: 'Save'}).click();

		await expect(
			page.getByText('Your request completed successfully.')
		).toBeVisible();

		const {items} =
			await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
				'c/' + objectDefinition.name.toLowerCase() + 's'
			);

		expect(items).toHaveLength(1);

		const fieldName = objectFields[0].name;

		expect(items[0][fieldName]).toBe(Number(decimalValue));
	}
);

test(
	'can map object field of Integer type to form and add entries',
	{tag: '@LPS-133365'},
	async ({
		apiHelpers,
		formBuilderPage,
		formBuilderSidePanelPage,
		formSettingsModalPage,
		page,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['Integer'],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const fieldLabel = objectFields[0].label!['en_US'];

		await formBuilderPage.goToNew();

		await expect(formBuilderPage.newFormHeading).toBeVisible();

		await formBuilderPage.fillFormTitle('Form' + getRandomInt());

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.selectStorageType('Object');

		await formSettingsModalPage.selectObject(
			objectDefinition.label['en_US']
		);

		await formSettingsModalPage.clickDoneButton();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Numeric');

		await formBuilderSidePanelPage.clickAdvancedTab();

		await formBuilderSidePanelPage.selectObjectField(fieldLabel);

		await page.waitForTimeout(1000);

		await formBuilderPage.clickPublishFormButton();

		const formSubmissionURL = await formBuilderPage.getFormSubmissionURL();

		await page.goto(formSubmissionURL, {waitUntil: 'networkidle'});

		const integerValue = getRandomInt().toString().slice(1, 4);

		await page.getByLabel('Numeric').fill(integerValue);

		await page.getByRole('button', {name: 'Save'}).click();

		await expect(
			page.getByText('Your request completed successfully.')
		).toBeVisible();

		const {items} =
			await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
				'c/' + objectDefinition.name.toLowerCase() + 's'
			);

		expect(items).toHaveLength(1);

		const fieldName = objectFields[0].name;

		expect(items[0][fieldName]).toBe(Number(integerValue));
	}
);

test(
	'can map object field of LongInteger type to form and add entries',
	{tag: '@LPS-133365'},
	async ({
		apiHelpers,
		formBuilderPage,
		formBuilderSidePanelPage,
		formSettingsModalPage,
		page,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['LongInteger'],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const fieldLabel = objectFields[0].label!['en_US'];

		await formBuilderPage.goToNew();

		await expect(formBuilderPage.newFormHeading).toBeVisible();

		await formBuilderPage.fillFormTitle('Form' + getRandomInt());

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.selectStorageType('Object');

		await formSettingsModalPage.selectObject(
			objectDefinition.label['en_US']
		);

		await formSettingsModalPage.clickDoneButton();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Numeric');

		await formBuilderSidePanelPage.clickAdvancedTab();

		await formBuilderSidePanelPage.clickAdvancedTab();

		await formBuilderSidePanelPage.selectObjectField(fieldLabel);

		await page.waitForTimeout(1000);

		await formBuilderPage.clickPublishFormButton();

		const formSubmissionURL = await formBuilderPage.getFormSubmissionURL();

		await page.goto(formSubmissionURL, {waitUntil: 'networkidle'});

		const longValue = getRandomInt();

		await page.getByLabel('Numeric').fill(String(longValue));

		await page.getByRole('button', {name: 'Save'}).click();

		await expect(
			page.getByText('Your request completed successfully.')
		).toBeVisible();

		const {items} =
			await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
				'c/' + objectDefinition.name.toLowerCase() + 's'
			);

		expect(items).toHaveLength(1);

		const fieldName = objectFields[0].name;

		expect(items[0][fieldName]).toBe(longValue);
	}
);

test(
	'can map object field of LongText type to form and add entries',
	{tag: '@LPS-133365'},
	async ({
		apiHelpers,
		formBuilderPage,
		formBuilderSidePanelPage,
		formSettingsModalPage,
		page,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['LongText'],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const fieldLabel = objectFields[0].label!['en_US'];

		await formBuilderPage.goToNew();

		await expect(formBuilderPage.newFormHeading).toBeVisible();

		await formBuilderPage.fillFormTitle('Form' + getRandomInt());

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.selectStorageType('Object');

		await formSettingsModalPage.selectObject(
			objectDefinition.label['en_US']
		);

		await formSettingsModalPage.clickDoneButton();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

		await formBuilderSidePanelPage.clickAdvancedTab();

		await formBuilderSidePanelPage.selectObjectField(fieldLabel);

		await page.waitForTimeout(1000);

		await formBuilderPage.clickPublishFormButton();

		const formSubmissionURL = await formBuilderPage.getFormSubmissionURL();

		await page.goto(formSubmissionURL, {waitUntil: 'networkidle'});

		const entryText =
			'By building a vibrant business, making technology useful, and investing in communities, we make it possible for people to reach their full potential to serve others.';

		await page.getByRole('textbox').fill(entryText);

		await page.getByRole('button', {name: 'Save'}).click();

		await expect(
			page.getByText('Your request completed successfully.')
		).toBeVisible();

		const {items} =
			await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
				'c/' + objectDefinition.name.toLowerCase() + 's'
			);

		expect(items).toHaveLength(1);

		const fieldName = objectFields[0].name;

		expect(items[0][fieldName]).toBe(entryText);
	}
);

test(
	'can map object field of Picklist type to form and add entries',
	{tag: '@LPS-138495'},
	async ({
		apiHelpers,
		formBuilderPage,
		formBuilderSidePanelPage,
		formSettingsModalPage,
		page,
	}) => {
		const {listTypeDefinition, listTypeEntries} =
			await postListTypeDefinitionListTypeEntries({
				apiHelpers,
				listTypeEntriesLength: 3,
			});

		apiHelpers.data.push({
			id: listTypeDefinition.id,
			type: 'listTypeDefinition',
		});

		const objectFields = generateObjectFields({
			listTypeDefinitionExternalReferenceCode:
				listTypeDefinition.externalReferenceCode,
			objectFieldBusinessTypes: ['Picklist'],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const fieldLabel = objectFields[0].label!['en_US'];

		await formBuilderPage.goToNew();

		await expect(formBuilderPage.newFormHeading).toBeVisible();

		await formBuilderPage.fillFormTitle('Form' + getRandomInt());

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.selectStorageType('Object');

		await formSettingsModalPage.selectObject(
			objectDefinition.label['en_US']
		);

		await formSettingsModalPage.clickDoneButton();

		await formBuilderSidePanelPage.addFieldByDoubleClick(
			'Select from List'
		);

		await formBuilderSidePanelPage.clickAdvancedTab();

		await formBuilderSidePanelPage.selectObjectField(fieldLabel);

		await page.waitForTimeout(1000);

		await formBuilderPage.clickPublishFormButton();

		const formSubmissionURL = await formBuilderPage.getFormSubmissionURL();

		await page.goto(formSubmissionURL, {waitUntil: 'networkidle'});

		const firstListTypeEntry = listTypeEntries[0];

		await page.getByLabel('Select from List').click();

		await page.getByRole('option', {name: firstListTypeEntry.name}).click();

		await page.getByRole('button', {name: 'Save'}).click();

		await expect(
			page.getByText('Your request completed successfully.')
		).toBeVisible();

		const {items} =
			await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
				'c/' + objectDefinition.name.toLowerCase() + 's'
			);

		expect(items).toHaveLength(1);

		const fieldName = objectFields[0].name;

		expect(items[0][fieldName]).toMatchObject({
			key: firstListTypeEntry.key,
			name: firstListTypeEntry.name,
		});
	}
);

test(
	'can map object field of PrecisionDecimal type to form and add entries',
	{tag: '@LPS-133365'},
	async ({
		apiHelpers,
		formBuilderPage,
		formBuilderSidePanelPage,
		formSettingsModalPage,
		page,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['PrecisionDecimal'],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const fieldLabel = objectFields[0].label!['en_US'];

		await formBuilderPage.goToNew();

		await expect(formBuilderPage.newFormHeading).toBeVisible();

		await formBuilderPage.fillFormTitle('Form' + getRandomInt());

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.selectStorageType('Object');

		await formSettingsModalPage.selectObject(
			objectDefinition.label['en_US']
		);

		await formSettingsModalPage.clickDoneButton();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Numeric');

		await formBuilderSidePanelPage.numericTypeDecimal.click();

		await formBuilderSidePanelPage.clickAdvancedTab();

		await formBuilderSidePanelPage.selectObjectField(fieldLabel);

		await page.waitForTimeout(1000);

		await formBuilderPage.clickPublishFormButton();

		const formSubmissionURL = await formBuilderPage.getFormSubmissionURL();

		await page.goto(formSubmissionURL, {waitUntil: 'networkidle'});

		const decimalValue = (getRandomInt() / 100).toFixed(2);

		await page.getByLabel('Numeric').fill(decimalValue);

		await page.getByRole('button', {name: 'Save'}).click();

		await expect(
			page.getByText('Your request completed successfully.')
		).toBeVisible();

		const {items} =
			await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
				'c/' + objectDefinition.name.toLowerCase() + 's'
			);

		expect(items).toHaveLength(1);

		const fieldName = objectFields[0].name;

		expect(items[0][fieldName]).toBe(Number(decimalValue));
	}
);

test(
	'can map object field of RichText type to form and add entries',
	{tag: '@LPS-133365'},
	async ({
		apiHelpers,
		formBuilderPage,
		formBuilderSidePanelPage,
		formSettingsModalPage,
		page,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['RichText'],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const fieldLabel = objectFields[0].label!['en_US'];

		await formBuilderPage.goToNew();

		await expect(formBuilderPage.newFormHeading).toBeVisible();

		await formBuilderPage.fillFormTitle('Form' + getRandomInt());

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.selectStorageType('Object');

		await formSettingsModalPage.selectObject(
			objectDefinition.label['en_US']
		);

		await formSettingsModalPage.clickDoneButton();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Rich Text');

		await formBuilderSidePanelPage.clickAdvancedTab();

		await formBuilderSidePanelPage.selectObjectField(fieldLabel);

		await page.waitForTimeout(1000);

		await formBuilderPage.clickPublishFormButton();

		const formSubmissionURL = await formBuilderPage.getFormSubmissionURL();

		await page.goto(formSubmissionURL, {waitUntil: 'load'});

		const richTextContent =
			'By building a vibrant business, making technology useful, and investing in communities, we make it possible for people to reach their full potential to serve others.';

		const richTextEditor = page.getByRole('paragraph');

		await richTextEditor.waitFor();

		await richTextEditor.click();

		await page.keyboard.type(richTextContent);

		await page.getByRole('button', {name: 'Save'}).click();

		await expect(
			page.getByText('Your request completed successfully.')
		).toBeVisible();

		const {items} =
			await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
				'c/' + objectDefinition.name.toLowerCase() + 's'
			);

		expect(items).toHaveLength(1);

		const fieldName = objectFields[0].name;

		expect(items[0][fieldName]).toContain(richTextContent);
	}
);

test(
	'can map object field of Text type to form and add entries',
	{tag: '@LPS-133365'},
	async ({
		apiHelpers,
		formBuilderPage,
		formBuilderSidePanelPage,
		formSettingsModalPage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await formBuilderPage.goToNew();

		await expect(formBuilderPage.newFormHeading).toBeVisible();

		await formBuilderPage.fillFormTitle('Form' + getRandomInt());

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.selectStorageType('Object');

		await formSettingsModalPage.selectObject(
			objectDefinition.label['en_US']
		);

		await formSettingsModalPage.clickDoneButton();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

		await formBuilderSidePanelPage.clickAdvancedTab();

		await formBuilderSidePanelPage.selectObjectField('textField');

		await page.waitForTimeout(1000);

		await formBuilderPage.clickPublishFormButton();

		const formSubmissionURL = await formBuilderPage.getFormSubmissionURL();

		await page.goto(formSubmissionURL, {waitUntil: 'networkidle'});

		const textValue = getRandomString();

		await page.getByLabel('Text').fill(textValue);

		await page.getByRole('button', {name: 'Save'}).click();

		await expect(
			page.getByText('Your request completed successfully.')
		).toBeVisible();

		const {items} =
			await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
				'c/' + objectDefinition.name.toLowerCase() + 's'
			);

		expect(items).toHaveLength(1);

		expect(items[0]['textField']).toBe(textValue);
	}
);

test(
	'can retrieve objects data from Data Providers and use on Select from List and Text fields',
	{tag: '@LPS-135429'},
	async ({
		apiHelpers,
		dataProviderPage,
		formBuilderFieldSettingsSidePanelPage,
		formBuilderPage,
		formBuilderSidePanelPage,
		formsPage,
		page,
		systemSettingsPage,
	}) => {
		try {
			let objectDefinition: ObjectDefinition;

			await test.step('create an object definition with a Text field', async () => {
				objectDefinition =
					await apiHelpers.objectAdmin.postRandomObjectDefinition({
						status: {code: 0},
					});

				apiHelpers.data.push({
					id: objectDefinition.id,
					type: 'objectDefinition',
				});
			});

			const objectEntry = 'Entry' + getRandomInt();

			await test.step('create an object entry', async () => {
				await apiHelpers.objectEntry.postObjectEntry(
					{textField: objectEntry},
					'c/' + objectDefinition.name.toLowerCase() + 's'
				);
			});

			await test.step('enable local network data provider access', async () => {
				await systemSettingsPage.goToSystemSetting(
					'Data Providers',
					'Data Providers'
				);

				await page.getByLabel('Access Local Network').check();

				await page
					.getByRole('button', {name: 'Save'})
					.or(page.getByRole('button', {name: 'Update'}))
					.click();

				await waitForAlert(page);

				await expect(
					page.getByLabel('Access Local Network')
				).toBeChecked();
			});

			const dataProviderName = 'ObjectEntries' + getRandomInt();

			await test.step('create a data provider pointing to the object definition API', async () => {
				await formsPage.goTo();

				await formsPage.dataProvidersTab.click();

				await dataProviderPage.addNewDataProviderLink.first().click();

				await dataProviderPage.nameInputField.fill(dataProviderName);

				await dataProviderPage.urlInputField.fill(
					`${liferayConfig.environment.baseUrl}/o/c/${objectDefinition.name.toLowerCase()}s`
				);

				await dataProviderPage.userNameInputField.fill(
					'test@liferay.com'
				);

				await dataProviderPage.passwordInputField.fill('test');

				await dataProviderPage.outputPathField.fill(
					'$.items..textField'
				);

				await dataProviderPage.selectOutputType('List');

				await dataProviderPage.outputLabelField.fill('Entry');

				await dataProviderPage.saveButton.click();

				await expect(
					page.getByText('Success:Your request')
				).toBeVisible();
			});

			await test.step('create a form with Select from List field configured with data provider', async () => {
				await formBuilderPage.goToNew();

				await expect(formBuilderPage.newFormHeading).toBeVisible();

				await formBuilderPage.fillFormTitle('Form' + getRandomInt());

				await formBuilderSidePanelPage.addFieldByDoubleClick(
					'Select from List'
				);

				await formBuilderSidePanelPage.label.fill(
					'Data Provider Select Field'
				);

				await formBuilderFieldSettingsSidePanelPage.selectCreateListSetting(
					'From Data Provider'
				);

				await expect(
					formBuilderFieldSettingsSidePanelPage.dataProviderSelect
				).toBeVisible();

				await formBuilderFieldSettingsSidePanelPage.selectDataProviderSetting(
					dataProviderName
				);

				await expect(
					formBuilderFieldSettingsSidePanelPage.outputParameterSelect
				).toBeVisible();

				await formBuilderFieldSettingsSidePanelPage.selectOutputParameterSetting(
					'Entry'
				);

				await page.waitForTimeout(1000);
			});

			await test.step('add a text field configured with autocomplete from Data Provider', async () => {
				await formBuilderSidePanelPage.backButton.click();

				await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

				await page.getByRole('tab', {name: 'Autocomplete'}).click();

				await page.getByRole('switch', {name: 'Autocomplete'}).click();

				await page.getByLabel('From Data Provider').click();

				await expect(
					formBuilderFieldSettingsSidePanelPage.dataProviderSelect
				).toBeVisible();

				await formBuilderFieldSettingsSidePanelPage.selectDataProviderSetting(
					dataProviderName
				);

				await expect(
					formBuilderFieldSettingsSidePanelPage.dataProviderSelect
				).toHaveText(dataProviderName);

				await expect(
					formBuilderFieldSettingsSidePanelPage.outputParameterSelect
				).toBeVisible();

				await formBuilderFieldSettingsSidePanelPage.selectOutputParameterSetting(
					'Entry'
				);

				await expect(
					formBuilderFieldSettingsSidePanelPage.outputParameterSelect
				).toHaveText('Entry');

				await page.waitForTimeout(1000);
			});

			await test.step('Publish the form, navigate to it and assert entry value is available as an option', async () => {
				await formBuilderPage.clickPublishFormButton();

				const formSubmissionURL =
					await formBuilderPage.getFormSubmissionURL();

				await page.goto(formSubmissionURL, {waitUntil: 'networkidle'});

				await page.getByLabel('Data Provider Select Field').click();

				await expect(
					page.getByRole('option', {name: objectEntry})
				).toBeVisible();

				await page.getByRole('option', {name: objectEntry}).click();

				await expect(
					page.getByLabel('Data Provider Select Field')
				).toContainText(objectEntry);

				await page.getByLabel('Text').fill('Entry');

				await expect(
					page.getByRole('menuitem', {name: objectEntry})
				).toBeVisible();
			});
		}
		finally {
			await formsPage.goTo();

			await formsPage.dataProvidersTab.click();

			await deleteItems(formsPage);

			await systemSettingsPage.goToSystemSetting(
				'Data Providers',
				'Data Providers'
			);

			await page.getByLabel('Access Local Network').uncheck();

			await page
				.getByRole('button', {name: 'Save'})
				.or(page.getByRole('button', {name: 'Update'}))
				.click();

			await waitForAlert(page);
		}
	}
);

test(
	'can see entries of a field with capitalized letters in the name',
	{tag: '@LPS-136451'},
	async ({
		apiHelpers,
		formBuilderPage,
		formBuilderSidePanelPage,
		formSettingsModalPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: [
				{
					businessType: 'Date',
					label: {en_US: 'DueDate' + getRandomInt()},
					name: 'dueDate' + getRandomInt(),
				},
			],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const fieldLabel = objectFields[0].label!['en_US'];

		await formBuilderPage.goToNew();

		await expect(formBuilderPage.newFormHeading).toBeVisible();

		await formBuilderPage.fillFormTitle('Form' + getRandomInt());

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.selectStorageType('Object');

		await formSettingsModalPage.selectObject(
			objectDefinition.label['en_US']
		);

		await formSettingsModalPage.clickDoneButton();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Date');

		await formBuilderSidePanelPage.clickAdvancedTab();

		await formBuilderSidePanelPage.selectObjectField(fieldLabel);

		await page.waitForTimeout(1000);

		await formBuilderPage.clickPublishFormButton();

		const formSubmissionURL = await formBuilderPage.getFormSubmissionURL();

		await page.goto(formSubmissionURL, {waitUntil: 'networkidle'});

		const date = new Date();

		const inputDate = getObjectEntryUIDateTimeFormat(date);

		await page.getByRole('textbox', {name: 'Date'}).fill(inputDate);

		await page.getByRole('button', {name: 'Save'}).click();

		await expect(
			page.getByText('Your request completed successfully.')
		).toBeVisible();

		await viewObjectEntriesPage.goto(objectDefinition.className);

		const fdsDate = getFDSDateFormat(date);

		await expect(page.getByRole('cell', {name: fdsDate})).toBeVisible();
	}
);

test(
	'can send form email when form is related with Object',
	{tag: '@LPS-200847'},
	async ({
		apiHelpers,
		formBuilderPage,
		formBuilderSidePanelPage,
		formSettingsModalPage,
		mockMockPage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await formBuilderPage.goToNew();

		await formBuilderPage.fillFormTitle('Form' + getRandomInt());

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.selectStorageType('Object');

		await formSettingsModalPage.selectObject(
			objectDefinition.label['en_US']
		);

		await page.getByRole('tab', {name: 'Notifications'}).click();

		await page
			.getByLabel('Send an Email Notification for Each Entry')
			.click();

		await page.getByLabel('From Name').fill('Sender Name');

		await page.getByLabel('From Address').fill('test@liferay.com');

		await page.getByLabel('To Address').fill('formreviewer@liferay.com');

		await page.getByLabel('Subject').fill('Form Subject');

		await formSettingsModalPage.clickDoneButton();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

		await formBuilderSidePanelPage.clickAdvancedTab();

		await formBuilderSidePanelPage.selectObjectField('textField');

		await page.waitForTimeout(1000);

		await formBuilderPage.clickPublishFormButton();

		await mockMockPage.deleteAll();

		const formSubmissionURL = await formBuilderPage.getFormSubmissionURL();

		await page.goto(formSubmissionURL, {waitUntil: 'networkidle'});

		await page.getByLabel('Text').fill('Entry test');

		await page.getByRole('button', {name: 'Save'}).click();

		await expect(
			page.getByText('Your request completed successfully.')
		).toBeVisible();

		await mockMockPage.assertEmail({
			body: 'Entry test',
			subject: 'Form Subject',
		});
	}
);

test(
	'can submit blank form entry with object field of Decimal type that is not required',
	{tag: '@LPS-137865'},
	async ({
		apiHelpers,
		formBuilderPage,
		formBuilderSidePanelPage,
		formSettingsModalPage,
		page,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: [{businessType: 'Decimal'}],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const fieldLabel = objectFields[0].label!['en_US'];

		await formBuilderPage.goToNew();

		await formBuilderPage.fillFormTitle('Form' + getRandomInt());

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.selectStorageType('Object');

		await formSettingsModalPage.selectObject(
			objectDefinition.label['en_US']
		);

		await formSettingsModalPage.clickDoneButton();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Numeric');

		await formBuilderSidePanelPage.numericTypeDecimal.click();

		await formBuilderSidePanelPage.clickAdvancedTab();

		await formBuilderSidePanelPage.selectObjectField(fieldLabel);

		await page.waitForTimeout(1000);

		await formBuilderPage.clickPublishFormButton();

		const formSubmissionURL = await formBuilderPage.getFormSubmissionURL();

		await page.goto(formSubmissionURL, {waitUntil: 'networkidle'});

		await page.getByRole('button', {name: 'Save'}).click();

		await expect(
			page.getByText('Your request completed successfully.')
		).toBeVisible();

		const {items} =
			await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
				'c/' + objectDefinition.name.toLowerCase() + 's'
			);

		expect(items).toHaveLength(1);
	}
);

test(
	'cannot change required option when field is mapped to object field',
	{tag: '@LPS-136456'},
	async ({
		apiHelpers,
		formBuilderPage,
		formBuilderSidePanelPage,
		formSettingsModalPage,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: [{businessType: 'Text', required: true}],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const fieldLabel = objectFields[0].label!['en_US'];

		await formBuilderPage.goToNew();

		await formBuilderPage.fillFormTitle('Form' + getRandomInt());

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.selectStorageType('Object');

		await formSettingsModalPage.selectObject(
			objectDefinition.label['en_US']
		);

		await formSettingsModalPage.clickDoneButton();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

		await formBuilderSidePanelPage.clickAdvancedTab();

		await formBuilderSidePanelPage.selectObjectField(fieldLabel);

		await formBuilderSidePanelPage.clickBasicTab();

		await expect(
			formBuilderSidePanelPage.requiredFieldToggleSwitch
		).toBeVisible();

		await expect(
			formBuilderSidePanelPage.requiredFieldToggleSwitch
		).toBeDisabled();
	}
);

test(
	'cannot edit picklist entries in forms sidebar',
	{tag: '@LPS-138495'},
	async ({
		apiHelpers,
		formBuilderPage,
		formBuilderSidePanelPage,
		formSettingsModalPage,
		page,
	}) => {
		const {listTypeDefinition} =
			await postListTypeDefinitionListTypeEntries({
				apiHelpers,
				listTypeEntriesLength: 2,
			});

		apiHelpers.data.push({
			id: listTypeDefinition.id,
			type: 'listTypeDefinition',
		});

		const objectFields = generateObjectFields({
			listTypeDefinitionExternalReferenceCode:
				listTypeDefinition.externalReferenceCode,
			objectFieldBusinessTypes: ['Picklist'],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const picklistFieldLabel = objectFields[0].label!['en_US'];

		await formBuilderPage.goToNew();

		await formBuilderPage.fillFormTitle('Form' + getRandomInt());

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.selectStorageType('Object');

		await formSettingsModalPage.selectObject(
			objectDefinition.label['en_US']
		);

		await formSettingsModalPage.clickDoneButton();

		await formBuilderSidePanelPage.addFieldByDoubleClick(
			'Select from List'
		);

		await formBuilderSidePanelPage.clickAdvancedTab();

		await formBuilderSidePanelPage.selectObjectField(picklistFieldLabel);

		await formBuilderSidePanelPage.clickBasicTab();

		await expect(page.getByText('Create List')).not.toBeVisible();
	}
);

test(
	'cannot map fields with different types',
	{tag: '@LPS-133365'},
	async ({
		apiHelpers,
		formBuilderPage,
		formBuilderSidePanelPage,
		formSettingsModalPage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await formBuilderPage.goToNew();

		await formBuilderPage.fillFormTitle('Form' + getRandomInt());

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.selectStorageType('Object');

		await formSettingsModalPage.selectObject(
			objectDefinition.label['en_US']
		);

		await formSettingsModalPage.clickDoneButton();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Numeric');

		await formBuilderSidePanelPage.clickAdvancedTab();

		await formBuilderSidePanelPage.objectFieldSelect.click();

		await expect(
			page.getByRole('option', {
				name: 'There are no compatible object fields to map.',
			})
		).toBeVisible();
	}
);

test(
	'cannot select an unpublished Object in form settings',
	{tag: '@LPS-137316'},
	async ({apiHelpers, formBuilderPage, formSettingsModalPage}) => {
		const draftObjectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 2},
			});

		apiHelpers.data.push({
			id: draftObjectDefinition.id,
			type: 'objectDefinition',
		});

		await formBuilderPage.goToNew();

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.selectStorageType('Object');

		await formSettingsModalPage.objectSelect.click();

		await expect(
			formSettingsModalPage.getSelectOptionLocator(
				draftObjectDefinition.label['en_US']
			)
		).not.toBeVisible();
	}
);

test(
	'cannot view forms entries when form is mapped to an object',
	{tag: '@LPS-136456'},
	async ({
		apiHelpers,
		formBuilderPage,
		formBuilderSidePanelPage,
		formSettingsModalPage,
		formsPage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await formBuilderPage.goToNew();

		const formTitle = 'Form' + getRandomInt();

		await formBuilderPage.fillFormTitle(formTitle);

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.selectStorageType('Object');

		await formSettingsModalPage.selectObject(
			objectDefinition.label['en_US']
		);

		await formSettingsModalPage.clickDoneButton();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

		await formBuilderSidePanelPage.clickAdvancedTab();

		await formBuilderSidePanelPage.selectObjectField('textField');

		await page.waitForTimeout(1000);

		await formBuilderPage.clickSaveButton();

		await expect(formBuilderPage.entriesTab).not.toBeVisible();

		await formsPage.goTo();

		await page
			.getByRole('row', {name: formTitle})
			.getByRole('button', {name: 'Show Actions'})
			.click();

		await expect(
			page.getByRole('menuitem', {name: 'View Entries'})
		).not.toBeVisible();
	}
);

test(
	'mapping form to a object definition and fields to an object field is required when using Object as storage type',
	{tag: '@LPD-78504'},
	async ({
		apiHelpers,
		formBuilderPage,
		formBuilderSidePanelPage,
		formSettingsModalPage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		await formBuilderPage.goToNew();

		await expect(formBuilderPage.newFormHeading).toBeVisible();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.selectStorageType('Object');

		await formSettingsModalPage.clickDoneButton();

		await formBuilderPage.publishButton.click();

		await waitForAlert(
			page,
			'You must define an object for the selected storage type.',
			{type: 'danger'}
		);

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.selectObject(
			objectDefinition.label['en_US']
		);

		await formSettingsModalPage.clickDoneButtonAndWaitForObjectFields(
			objectDefinition.id
		);

		await formBuilderPage.publishButton.click();

		await expect(
			page.getByRole('heading', {name: 'Unmapped Object Required'})
		).toBeVisible();

		await expect(
			page.getByText(
				'All fields in this form must be mapped to a field in the object.'
			)
		).toBeVisible();
	}
);

test(
	'object entries are not deleted when form is deleted',
	{tag: '@LPS-139902'},
	async ({
		apiHelpers,
		formBuilderPage,
		formBuilderSidePanelPage,
		formSettingsModalPage,
		formsPage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await formBuilderPage.goToNew();

		await expect(formBuilderPage.newFormHeading).toBeVisible();

		await formBuilderPage.fillFormTitle('Form' + getRandomInt());

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.selectStorageType('Object');

		await formSettingsModalPage.selectObject(
			objectDefinition.label['en_US']
		);

		await formSettingsModalPage.clickDoneButton();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

		await formBuilderSidePanelPage.clickAdvancedTab();

		await formBuilderSidePanelPage.selectObjectField('textField');

		await page.waitForTimeout(1000);

		await formBuilderPage.clickPublishFormButton();

		const formSubmissionURL = await formBuilderPage.getFormSubmissionURL();

		const textValue = getRandomString();

		await test.step('create an object entry through the form', async () => {
			await page.goto(formSubmissionURL, {waitUntil: 'networkidle'});

			await page.getByLabel('Text').fill(textValue);

			await page.getByRole('button', {name: 'Save'}).click();

			await expect(
				page.getByText('Your request completed successfully.')
			).toBeVisible();
		});

		await test.step('verify entry exists before deleting the form', async () => {
			const {items: itemsBefore} =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
					'c/' + objectDefinition.name.toLowerCase() + 's'
				);

			expect(itemsBefore).toHaveLength(1);
		});

		await test.step('delete the form', async () => {
			await formsPage.goTo();

			await deleteItems(formsPage);
		});

		await test.step('verify entry still exists after form deletion', async () => {
			const {items: itemsAfter} =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
					'c/' + objectDefinition.name.toLowerCase() + 's'
				);

			expect(itemsAfter).toHaveLength(1);

			const item = itemsAfter[0];

			expect(item['textField']).toBe(textValue);
		});
	}
);

test(
	'repeatable and searchable options are not available on form mapped to an object',
	{tag: '@LPS-136456'},
	async ({
		apiHelpers,
		formBuilderPage,
		formBuilderSidePanelPage,
		formSettingsModalPage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await formBuilderPage.goToNew();

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.selectStorageType('Object');

		await formSettingsModalPage.selectObject(
			objectDefinition.label['en_US']
		);

		await formSettingsModalPage.clickDoneButton();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

		await formBuilderSidePanelPage.clickAdvancedTab();

		await expect(
			formBuilderSidePanelPage.repeatableFieldToggleSwitch
		).not.toBeVisible();

		await expect(page.getByLabel('Searchable')).not.toBeVisible();
	}
);

test(
	'verify that form entry submission is blocked when an Object is inactivated and restored when reactivated',
	{tag: '@LPS-139005'},
	async ({
		apiHelpers,
		formBuilderPage,
		formBuilderSidePanelPage,
		formSettingsModalPage,
		formsPage,
		page,
		viewObjectDefinitionsPage,
		viewObjectEntriesPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await formBuilderPage.goToNew();

		const formTitle = 'Form' + getRandomInt();

		await formBuilderPage.fillFormTitle(formTitle);

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.selectStorageType('Object');

		await formSettingsModalPage.selectObject(
			objectDefinition.label['en_US']
		);

		await formSettingsModalPage.clickDoneButton();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

		await formBuilderSidePanelPage.clickAdvancedTab();

		await formBuilderSidePanelPage.selectObjectField('textField');

		await page.waitForTimeout(1000);

		await formBuilderSidePanelPage.clickBackButton();

		await formBuilderPage.clickSaveButton();

		await formBuilderPage.clickPublishFormButton();

		const formSubmissionURL = await formBuilderPage.getFormSubmissionURL();

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.changeObjectActivateStatus(
			objectDefinition.name
		);

		await formsPage.goTo();

		await page.reload();

		await expect(
			page.getByRole('cell', {
				exact: true,
				name: `This form was created using an inactive object as storage type. 
				Activate "${objectDefinition.name}" object to make it available for 
				editing. ${formTitle}`,
			})
		).toBeVisible();

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.changeObjectActivateStatus(
			objectDefinition.name
		);

		await formsPage.goTo();

		await page.reload();

		await expect(
			page.getByRole('cell', {
				exact: true,
				name: `This form was created using an inactive object as storage type. 
				Activate "${objectDefinition.name}" object to make it available for 
				editing. ${formTitle}`,
			})
		).toBeHidden();

		await page.goto(formSubmissionURL);

		await page.getByLabel('Text').fill('Entry 1');

		await page.getByRole('button', {name: 'Save'}).click();

		await expect(
			page.getByText(
				'Your information was successfully received. Thank you for filling out the form.'
			)
		).toBeVisible();

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(page.getByText('Entry 1')).toBeVisible();
	}
);

test(
	'verify that the Object visibility in the Form storage type changes according to activation status',
	{tag: '@LPS-139005'},
	async ({
		apiHelpers,
		formBuilderPage,
		formSettingsModalPage,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.changeObjectActivateStatus(
			objectDefinition.name
		);

		await formBuilderPage.goToNew();

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.storageTypeSelect.click();

		await page.getByRole('option', {name: 'Object'}).click();

		await formSettingsModalPage.objectSelect.click();

		await expect(
			page.getByRole('option', {
				name: objectDefinition.label['en_US'],
			})
		).toBeHidden();

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.changeObjectActivateStatus(
			objectDefinition.name
		);

		await formBuilderPage.goToNew();

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.storageTypeSelect.click();

		await page.getByRole('option', {name: 'Object'}).click();

		await formSettingsModalPage.objectSelect.click();

		await expect(
			page.getByRole('option', {
				name: objectDefinition.label['en_US'],
			})
		).toBeVisible();
	}
);

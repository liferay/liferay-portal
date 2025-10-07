/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinition,
	ObjectDefinitionAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {accountSettingsPagesTest} from '../../../fixtures/accountSettingsPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {formsPagesTest} from '../../../fixtures/formsPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {listTypeDefinitionsPagesTest} from '../../../fixtures/listTypeDefinitionsPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {siteSettingsPagesTest} from '../../../fixtures/siteSettingsPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import {waitForAlert} from '../../../utils/waitForAlert';
import {generateObjectFields} from './utils/generateObjectFields';
import {postListTypeDefinitionListTypeEntries} from './utils/postListTypeDefinitionListTypeEntries';

export const test = mergeTests(
	accountSettingsPagesTest,
	dataApiHelpersTest,
	formsPagesTest,
	isolatedSiteTest,
	listTypeDefinitionsPagesTest,
	loginTest(),
	objectPagesTest,
	siteSettingsPagesTest
);

let siteLanguage = 'en';
let userLanguage = 'en_US';

test.afterEach(async ({accountSettingsPage, page}) => {
	if (siteLanguage !== 'en') {
		await page.goto('en');

		siteLanguage = 'en';
	}

	if (userLanguage !== 'en_US') {
		await page.goto('en');

		await page.locator('button[data-qa-id="userPersonalMenu"]').click();

		await page.getByRole('menuitem', {name: 'Account Settings'}).click();

		await accountSettingsPage.selectAccountLanguage({languageId: 'en_US'});

		userLanguage = 'en_US';
	}
});

test.describe('manage picklists inside the picklists portlet', () => {
	test('can create a picklist', async ({
		apiHelpers,
		listTypeDefinitionPage,
		page,
	}) => {
		const listTypeDefinition: ListTypeDefinition =
			await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

		apiHelpers.data.push({
			id: listTypeDefinition.id,
			type: 'listTypeDefinition',
		});

		await listTypeDefinitionPage.goto();

		await expect(
			page.getByRole('link', {name: listTypeDefinition.name})
		).toBeVisible();
	});

	test('can create a picklist when the instance language is different from the site language', async ({
		apiHelpers,
		listTypeDefinitionPage,
		page,
		site,
		siteSettingsLocalizationPage,
	}) => {
		await siteSettingsLocalizationPage.goto(site.friendlyUrlPath);

		await siteSettingsLocalizationPage.setCustomDefaultLanguage(
			'pt_BR',
			site.friendlyUrlPath
		);

		const listTypeDefinition: ListTypeDefinition =
			await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

		apiHelpers.data.push({
			id: listTypeDefinition.id,
			type: 'listTypeDefinition',
		});

		await listTypeDefinitionPage.goto();

		await expect(
			page.getByRole('link', {name: listTypeDefinition.name})
		).toBeVisible();
	});

	test('ensure picklist entry keys starting with upper case are correctly rendered in the entries', async ({
		apiHelpers,
		listTypeDefinitionPage,
		page,
	}) => {
		const listTypeDefinition: ListTypeDefinition =
			await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

		apiHelpers.data.push({
			id: listTypeDefinition.id,
			type: 'listTypeDefinition',
		});

		await listTypeDefinitionPage.goto();

		const listTypeDefinitionName: string = listTypeDefinition.name;

		const listTypeDefinitionEntryName = 'ListTypeDefinitionEntryName';

		const listTypeDefinitionEntryKey = 'ListTypeDefinitionEntryKey';

		await listTypeDefinitionPage.addPicklistItem(
			listTypeDefinitionName,
			listTypeDefinitionEntryName,
			listTypeDefinitionEntryKey
		);

		const [response] =
			await apiHelpers.listTypeAdmin.getFilteredListTypeDefinition(
				'name',
				listTypeDefinitionName
			);

		const [responseEntries]: ListTypeEntry[] = response.listTypeEntries;

		const frameElement = await page.$('iframe');
		const frame = await frameElement.contentFrame();
		await frame.waitForLoadState('networkidle');

		const listTypeDefinitionHeader =
			await listTypeDefinitionPage.frameLocator
				.locator('.fds th')
				.allInnerTexts();

		const listTypeDefinitionContent =
			await listTypeDefinitionPage.frameLocator
				.locator('.fds td')
				.allInnerTexts();

		const listTypeDefinitionHeaderTemplate = [
			'Name',
			'Key',
			'External Reference Code',
		];

		const listTypeDefinitionContentTemplate = [
			listTypeDefinitionEntryName,
			listTypeDefinitionEntryKey,
			responseEntries.externalReferenceCode,
		];

		for (let i = 0; i < 3; i++) {
			expect(listTypeDefinitionHeaderTemplate[i]).toBe(
				listTypeDefinitionHeader[i]
			);
			expect(listTypeDefinitionContentTemplate[i]).toBe(
				listTypeDefinitionContent[i]
			);
		}
	});

	test('ensure that attempting to add picklist item with empty name/key will show a required error', async ({
		apiHelpers,
		listTypeDefinitionPage,
		page,
	}) => {
		const listTypeDefinition: ListTypeDefinition =
			await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

		apiHelpers.data.push({
			id: listTypeDefinition.id,
			type: 'listTypeDefinition',
		});

		await listTypeDefinitionPage.goto();

		await page.getByRole('link', {name: listTypeDefinition.name}).click();

		await listTypeDefinitionPage.addPicklistItemButton.click();

		await listTypeDefinitionPage.modalSaveButton.click();

		expect(await page.getByText('Required').count()).toBe(2);

		await listTypeDefinitionPage.modalNameInput.fill(
			'picklisItem' + getRandomInt()
		);

		await listTypeDefinitionPage.modalSaveButton.click();

		await waitForAlert(page, 'The picklist item was created successfully.');
	});

	test('can delete a picklist item', async ({
		apiHelpers,
		listTypeDefinitionPage,
		page,
	}) => {
		const listTypeDefinition: ListTypeDefinition =
			await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

		apiHelpers.data.push({
			id: listTypeDefinition.id,
			type: 'listTypeDefinition',
		});

		await listTypeDefinitionPage.goto();

		const listTypeDefinitionName: string = listTypeDefinition.name;

		const listTypeDefinitionEntryName = 'ListTypeDefinitionEntryName';

		const listTypeDefinitionEntryKey = 'ListTypeDefinitionEntryKey';

		await listTypeDefinitionPage.addPicklistItem(
			listTypeDefinitionName,
			listTypeDefinitionEntryName,
			listTypeDefinitionEntryKey
		);

		const frameElement = await page.$('iframe');
		const frame = await frameElement.contentFrame();
		await frame.waitForLoadState('load');

		await listTypeDefinitionPage.deletePicklistItem();
		await frame.waitForLoadState('load');
		await expect(frame.getByText('No Results Found')).toBeVisible();
	});
});

test.describe('ensure picklist translation', () => {
	test('verify if title of clear all button on multiselect picklist field is translated', async ({
		apiHelpers,
		formFieldsPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const {listTypeDefinition, listTypeEntries} =
			await postListTypeDefinitionListTypeEntries({
				apiHelpers,
				listTypeEntriesLength: 4,
				locale: 'pt_BR',
			});

		const objectFields = generateObjectFields({
			listTypeDefinitionExternalReferenceCode:
				listTypeDefinition.externalReferenceCode,
			objectFieldBusinessTypes: ['MultiselectPicklist'],
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				enableLocalization: true,
				label: {
					en_US: 'ObjectDefinitionLabel' + getRandomInt(),
				},
				name: 'ObjectDefinitionName' + getRandomInt(),
				objectFields,
				pluralLabel: {
					en_US: 'ObjectDefinitionLabel' + getRandomInt(),
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

		await viewObjectEntriesPage.goto(objectDefinition.className, 'pt');

		await viewObjectEntriesPage.addObjectEntryButton.click();

		const [{name_i18n: listTypeEntry_i18n}] = listTypeEntries;

		await formFieldsPage.addSelectItem(listTypeEntry_i18n['pt-BR']);

		await expect(page.getByTitle('Limpar Todos')).toBeVisible();
	});

	test('verify if translated picklist will be displayed on object admin', async ({
		accountSettingsPage,
		apiHelpers,
		listTypeDefinitionPage,
		page,
		viewObjectDefinitionsPage,
	}) => {

		// Create a picklist

		const listTypeDefinition: ListTypeDefinition =
			await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

		apiHelpers.data.push({
			id: listTypeDefinition.id,
			type: 'listTypeDefinition',
		});

		const listTypeDefinitionName: string = listTypeDefinition.name;

		// Translate picklist

		await listTypeDefinitionPage.goto();

		await listTypeDefinitionPage.translatePicklist(
			listTypeDefinitionName,
			'pt_BR'
		);

		// Create custom object with the picklist

		const objectDefinition: ObjectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await page.goto('/');

		await page.locator('button[data-qa-id="userPersonalMenu"]').click();

		await page.getByRole('menuitem', {name: 'Account Settings'}).click();

		await accountSettingsPage.selectAccountLanguage({languageId: 'pt_BR'});

		userLanguage = 'pt_BR';

		await page.waitForLoadState('networkidle');

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.clickEditObjectDefinitionLink(
			objectDefinition.label['en_US']
		);

		await page.getByRole('link', {name: 'Campos'}).click();

		await page
			.getByRole('button', {name: 'Adicionar campo de objeto'})
			.click();

		await page.getByText('Selecione uma opção').click();

		await page
			.getByRole('option', {exact: true, name: 'Lista de seleção'})
			.click();

		await page.getByLabel('Lista de seleção').click();

		await expect(
			page.getByRole('option', {
				name: listTypeDefinitionName + ' translated',
			})
		).toBeVisible();
	});

	test('verify if translated picklist item will be displayed on forms', async ({
		apiHelpers,
		editObjectDetailsPage,
		formBuilderPage,
		formBuilderSidePanelPage,
		formSettingsModalPage,
		listTypeDefinitionPage,
		objectFieldsPage,
		page,
		viewObjectDefinitionsPage,
	}) => {

		// Create a picklist

		const listTypeDefinition: ListTypeDefinition =
			await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

		apiHelpers.data.push({
			id: listTypeDefinition.id,
			type: 'listTypeDefinition',
		});

		const listTypeDefinitionName: string = listTypeDefinition.name;

		// Create a picklist item

		const listTypeEntryName: string = 'picklistItem' + getRandomInt();

		await apiHelpers.listTypeAdmin.postListTypeEntry({
			key: listTypeEntryName,
			listTypeDefinitionExternalReferenceCode:
				listTypeDefinition.externalReferenceCode,
			name_i18n: {en_US: listTypeEntryName},
		});

		// Translate picklist item

		await listTypeDefinitionPage.goto();

		await listTypeDefinitionPage.translatePicklistItem(
			listTypeDefinitionName,
			listTypeEntryName,
			'pt_BR'
		);

		await expect(listTypeDefinitionPage.basicInfoHeading).toBeVisible();

		// Create custom object with the picklist

		const objectDefinition: ObjectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});
		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await viewObjectDefinitionsPage.goto();

		await objectFieldsPage.goto(objectDefinition.label['en_US']);

		const fieldLabel = 'picklistField' + getRandomInt();

		await objectFieldsPage.addObjectField({
			listTypeDefinitionName: listTypeDefinition.name,
			objectFieldBusinessType: 'Picklist',
			objectFieldLabel: fieldLabel,
		});

		await editObjectDetailsPage.goToDetailsTab();

		await editObjectDetailsPage.saveObjectDefinition();

		await page.goto('/');

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

		await expect(formBuilderSidePanelPage.objectFieldSelect).toBeVisible();

		// Preview form

		await apiHelpers.dynamicDataMapping.waitForDDMEvaluate(page);

		const newTabPage = await formBuilderPage.openPreviewForm();

		await page.goto('pt');

		await newTabPage.reload();

		siteLanguage = 'pt';

		await newTabPage.getByLabel('Select from List').click();

		await expect(
			newTabPage.getByRole('option', {
				name: listTypeEntryName + ' translated',
			})
		).toBeVisible();
	});
});

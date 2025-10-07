/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinitionAPI,
	ObjectField,
} from '@liferay/object-admin-rest-client-js';
import {Locator, expect, mergeTests} from '@playwright/test';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../fixtures/displayPageTemplatesPagesTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {fragmentsPagesTest} from '../../../fixtures/fragmentPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {masterPagesPagesTest} from '../../../fixtures/masterPagesPagesTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageManagementSiteTest} from '../../../fixtures/pageManagementSiteTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import fillAndClickOutside from '../../../utils/fillAndClickOutside';
import getRandomString from '../../../utils/getRandomString';
import {goToObjectEntity} from '../../setup/page-management-site/main/utils/goToObjectEntity';
import {cmsPagesTest} from '../../site-cms-site-initializer/main/fixtures/cmsPagesTest';
import {structureBuilderPagesTest} from '../../site-cms-site-initializer/structure-builder/fixtures/structureBuilderPagesTest';
import chooseFileFromDocumentLibrary from '../main/utils/chooseFileFromDocumentLibrary';
import getFormContainerDefinition from '../main/utils/getFormContainerDefinition';
import getFragmentDefinition from '../main/utils/getFragmentDefinition';
import getPageDefinition from '../main/utils/getPageDefinition';

const test = mergeTests(
	apiHelpersTest,
	cmsPagesTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	documentLibraryPagesTest,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
		'LPD-17564': {enabled: true},
		'LPD-21926': {enabled: true},
		'LPD-32050': {enabled: true},
		'LPD-60546': {enabled: true},
		'LPS-178052': {enabled: true},
		'LPS-179669': {enabled: true},
	}),
	fragmentsPagesTest,
	isolatedSiteTest,
	loginTest(),
	masterPagesPagesTest,
	objectPagesTest,
	pageEditorPagesTest,
	pageManagementSiteTest,
	structureBuilderPagesTest
);

test(
	'Can translate text form fields',
	{tag: '@LPD-37927'},
	async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

		// Create a page with a Form fragment

		const formId = getRandomString();

		const formDefinition = getFormContainerDefinition({
			id: formId,
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([formDefinition]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Map the form to the All Fields object and publish the page

		await pageEditorPage.mapFormFragment(formId, 'All Fields', 'all', {
			addLocalizationSelect: true,
		});

		await expect(
			page.locator('[data-name="Localization Select"]')
		).toBeAttached();

		await pageEditorPage.publishPage();

		// Go to view mode and fill the form

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await page.getByLabel('Long Text').fill('long text english');

		await page
			.getByRole('textbox', {exact: true, name: 'Text'})
			.fill('text english');

		const ckEditor = page.locator('.ck-editor__editable');

		ckEditor.waitFor();

		await ckEditor.fill('rich text english');

		await ckEditor.blur();

		// Add translations and check translation status

		const translationSelector = page.getByLabel(
			'Select a language, current language:'
		);

		await translationSelector.click();

		const option = page.getByRole('option').filter({hasText: 'es-ES'});

		await expect(option).toContainText(/Not Translated/);

		await option.click();

		await page.getByLabel('Long Text').fill('long text español');

		await page
			.getByRole('textbox', {exact: true, name: 'Text'})
			.fill('text español');

		await translationSelector.click();

		await expect(option).toContainText(/Translating 2\/3/);

		await option.click();

		await ckEditor.waitFor();

		await ckEditor.fill('rich text español');

		await ckEditor.blur();

		await translationSelector.click();

		await expect(option).toContainText(/Translated/);

		await option.click();

		// Publish the form

		await page.getByRole('button', {name: 'Submit'}).click();

		await expect(
			page.getByText(
				'Thank you. Your information was successfully received.'
			)
		).toBeVisible();

		// Go to custom object admin an check the values

		await goToObjectEntity({
			entityName: 'All Fields',
			page,
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				exact: true,
				name: 'View',
			}),
			trigger: page
				.locator('.fds tbody .cell-item-actions .dropdown-toggle')
				.last(),
		});

		await page.getByRole('textbox', {name: 'Long Text'}).waitFor();

		await expect(page.getByText('long text english')).toBeVisible();

		await expect(ckEditor.getByText('rich text english')).toBeVisible();

		await expect(page.locator('input.ddm-field-text')).toHaveValue(
			'text english'
		);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				name: 'Español (España)',
			}),
			trigger: page.getByTestId('triggerButton').first(),
		});

		await expect(page.getByText('long text español')).toBeVisible();

		await expect(ckEditor.getByText('rich text español')).toBeVisible();

		await expect(page.locator('input.ddm-field-text')).toHaveValue(
			'text español'
		);
	}
);

test(
	'Can translate numeric form fields',
	{tag: '@LPD-43808'},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create object definition

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				enableLocalization: true,
				externalReferenceCode: 'numericERC',
				label: {
					en_US: 'Numeric',
				},
				name: 'Numeric',
				objectFields: [
					{
						DBType: 'Long',
						businessType: 'LongInteger',
						externalReferenceCode: 'longIntegerERC',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Long Integer',
						},
						localized: true,
						name: 'longInteger',
						required: false,
					},
					{
						DBType: 'Integer',
						businessType: 'Integer',
						externalReferenceCode: 'integerERC',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Integer',
						},
						localized: true,
						name: 'integer',
						required: false,
					},
					{
						DBType: 'BigDecimal',
						businessType: 'PrecisionDecimal',
						externalReferenceCode: 'precisionDecimalERC',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Precision Decimal',
						},
						localized: true,
						name: 'precisionDecimal',
						required: false,
					},
					{
						DBType: 'Double',
						businessType: 'Decimal',
						externalReferenceCode: 'decimalERC',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Decimal',
						},
						localized: true,
						name: 'decimal',
						required: false,
					},
				],
				pluralLabel: {
					en_US: 'Numerics',
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

		// Create a page with a Form fragment

		const formId = getRandomString();

		const formDefinition = getFormContainerDefinition({
			id: formId,
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([formDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Map the form to the Plant object and publish the page

		await pageEditorPage.mapFormFragment(formId, 'Numeric', 'all', {
			addLocalizationSelect: true,
		});

		await pageEditorPage.publishPage();

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		const decimalInput = page.getByRole('spinbutton', {
			exact: true,
			name: 'Decimal',
		});
		const integerInput = page.getByRole('spinbutton', {
			exact: true,
			name: 'Integer',
		});
		const longIntegerInput = page.getByRole('spinbutton', {
			exact: true,
			name: 'Long Integer',
		});
		const precisionDecimalInput = page.getByRole('spinbutton', {
			exact: true,
			name: 'Precision Decimal',
		});

		await decimalInput.fill('1111.22222');
		await integerInput.fill('1111');
		await longIntegerInput.fill('11111111111');
		await precisionDecimalInput.fill('111.11');

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option').filter({hasText: 'es-ES'}),
			trigger: page.getByLabel('Select a language, current language:'),
		});

		await decimalInput.fill('2222.33333');
		await integerInput.fill('2222');
		await longIntegerInput.fill('22222222222');
		await precisionDecimalInput.fill('222.22');

		await page.getByRole('button', {name: 'Submit'}).click();

		await expect(
			page.getByText(
				'Thank you. Your information was successfully received.'
			)
		).toBeVisible();

		const {items} =
			await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
				'c/numerics'
			);

		const item = items[0];

		expect(item.precisionDecimal_i18n).toStrictEqual({
			en_US: 111.11,
			es_ES: 222.22,
		});

		expect(item.decimal_i18n).toStrictEqual({
			en_US: 1111.22222,
			es_ES: 2222.33333,
		});

		expect(item.integer_i18n).toStrictEqual({
			en_US: 1111,
			es_ES: 2222,
		});

		expect(item.longInteger_i18n).toStrictEqual({
			en_US: 11111111111,
			es_ES: 22222222222,
		});
	}
);

test(
	'Can translate checkbox form field',
	{tag: '@LPD-46483'},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create object definition with a localized boolean

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				enableLocalization: true,
				externalReferenceCode: 'booleanERC',
				label: {
					en_US: 'Boolean',
				},
				name: 'Boolean',
				objectFields: [
					{
						DBType: 'Boolean',
						businessType: 'Boolean',
						externalReferenceCode: 'legalThingsERC',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Legal Things',
						},
						localized: true,
						name: 'legalThings',
						required: false,
					},
				],
				pluralLabel: {
					en_US: 'Booleans',
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

		// Create a page with a Form fragment

		const formId = getRandomString();

		const formDefinition = getFormContainerDefinition({
			id: formId,
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([formDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Map the form to the Boolean object

		await pageEditorPage.mapFormFragment(formId, 'Boolean', 'all', {
			addLocalizationSelect: true,
		});

		await pageEditorPage.publishPage();

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await page.getByLabel('Legal Things').check();

		const spanishOption = page
			.getByRole('option')
			.filter({hasText: 'es-ES'});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: spanishOption,
			trigger: page.getByLabel('Select a language, current language:'),
		});

		await page.getByLabel('Legal Things').uncheck();

		// Check the translation in the localization select

		await page.getByLabel('Select a language, current language:').click();

		await expect(spanishOption).toContainText('Language: Translated');

		await page.keyboard.press('Escape');

		// Save the form and publish the page

		await page.getByRole('button', {name: 'Submit'}).click();

		await expect(
			page.getByText(
				'Thank you. Your information was successfully received.'
			)
		).toBeVisible();

		// Check the object entry

		const {items} =
			await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
				'c/booleans'
			);

		const item = items[0];

		expect(item.legalThings_i18n).toStrictEqual({
			en_US: true,
			es_ES: false,
		});
	}
);

test(
	'Can translate select form field',
	{tag: '@LPD-46485'},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create object definition with a localized select

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const listTypeDefinition =
			await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

		const options = ['Spain', 'Italy', 'Germany', 'Brasil'];

		for (const option of options) {
			await apiHelpers.listTypeAdmin.postListTypeEntry({
				key: option,
				listTypeDefinitionExternalReferenceCode:
					listTypeDefinition.externalReferenceCode,
				name_i18n: {en_US: option},
			});
		}

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				enableLocalization: true,
				externalReferenceCode: 'SelectERC',
				label: {
					en_US: 'Select',
				},
				name: 'Select',
				objectFields: [
					{
						DBType: 'String',
						businessType: 'Picklist',
						externalReferenceCode: 'selectCountryERC',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Select a Country',
						},
						listTypeDefinitionExternalReferenceCode:
							listTypeDefinition.externalReferenceCode,
						listTypeDefinitionId: listTypeDefinition.id,
						localized: true,
						name: 'selectCountry',
						required: false,
					},
				],
				panelCategoryKey: 'control_panel.object',
				pluralLabel: {
					en_US: 'Select',
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

		// Create a page with a Form fragment

		const formId = getRandomString();

		const formDefinition = getFormContainerDefinition({
			id: formId,
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([formDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Map the form to the Select object

		await pageEditorPage.mapFormFragment(formId, 'Select', 'all', {
			addLocalizationSelect: true,
		});

		await pageEditorPage.publishPage();

		// Go to view mode

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		const input = page.getByPlaceholder('Choose an Option');

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {
				name: 'Italy',
			}),
			trigger: input,
		});

		const valueInput = page.locator('[name="ObjectField_selectCountry"]');

		await expect(valueInput).toHaveValue('italy');

		// Check we can leave it blank after setting a value

		await input.fill('');

		await input.blur();

		await expect(valueInput).toHaveValue('');

		// Select value again

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {
				name: 'Italy',
			}),
			trigger: input,
		});

		// Switch to spanish and select another value

		const spanishOption = page
			.getByRole('option')
			.filter({hasText: 'es-ES'});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: spanishOption,
			trigger: page.getByLabel('Select a language, current language:'),
		});

		await expect(async () => {
			await page
				.getByPlaceholder('Choose an Option')
				.click({timeout: 1000});

			await expect(
				page.getByRole('option', {
					name: 'Germany',
				})
			).toBeVisible({timeout: 1000});

			await page
				.getByRole('option', {
					name: 'Germany',
				})
				.click({timeout: 1000});
		}).toPass();

		// Check the translation in the localization select

		await page.getByLabel('Select a language, current language:').click();

		await expect(spanishOption).toContainText('Language: Translated');

		await page.keyboard.press('Escape');

		// Save the form and publish the page

		await page.getByRole('button', {name: 'Submit'}).click();

		await expect(
			page.getByText(
				'Thank you. Your information was successfully received.'
			)
		).toBeVisible();

		// Check the object entry

		const {items} =
			await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
				'c/selects'
			);

		const item = items[0];

		expect(item.selectCountry_i18n).toStrictEqual({
			en_US: {key: 'italy', name: 'Italy'},
			es_ES: {key: 'germany', name: 'Germany'},
		});
	}
);

test(
	'Can translate multiselect form field',
	{tag: '@LPD-48344'},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create object definition

		const listTypeDefinition =
			await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

		for (const option of ['Spain', 'Italy', 'Germany']) {
			await apiHelpers.listTypeAdmin.postListTypeEntry({
				key: option,
				listTypeDefinitionExternalReferenceCode:
					listTypeDefinition.externalReferenceCode,
				name_i18n: {en_US: option},
			});
		}

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				enableLocalization: true,
				externalReferenceCode: 'plantERC',
				label: {
					en_US: 'Plant',
				},
				name: 'Plant',
				objectFields: [
					{
						DBType: 'String',
						businessType: 'MultiselectPicklist',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Growth Areas',
						},
						listTypeDefinitionExternalReferenceCode:
							listTypeDefinition.externalReferenceCode,
						listTypeDefinitionId: listTypeDefinition.id,
						localized: true,
						name: 'growthAreas',
						required: false,
					},
				],
				pluralLabel: {
					en_US: 'Plants',
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

		// Create a page with a Form fragment

		const formId = getRandomString();

		const formDefinition = getFormContainerDefinition({
			id: formId,
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([formDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Map the form to the Plant object

		await pageEditorPage.mapFormFragment(formId, 'Plant', 'all', {
			addLocalizationSelect: true,
		});

		await pageEditorPage.publishPage();

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		const translationTrigger = page.getByLabel(
			'Select a language, current language:'
		);

		await translationTrigger.waitFor();

		await page.getByRole('checkbox', {name: 'Spain'}).check();
		await page.getByRole('checkbox', {name: 'Italy'}).check();

		const spanishOption = page
			.getByRole('option')
			.filter({hasText: 'es-ES'});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: spanishOption,
			trigger: translationTrigger,
		});

		await page.getByRole('checkbox', {name: 'Germany'}).check();

		// Check the translation in the localization multiselect

		await translationTrigger.click();

		await expect(spanishOption).toContainText('Language: Translated');

		await page.keyboard.press('Escape');

		// Save the form and publish the page

		await page.getByRole('button', {name: 'Submit'}).click();

		await expect(
			page.getByText(
				'Thank you. Your information was successfully received.'
			)
		).toBeVisible();

		// Check the object entry

		const {items} =
			await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
				'c/plants'
			);

		expect(items[0].growthAreas_i18n).toStrictEqual({
			en_US: [
				{key: 'spain', name: 'Spain'},
				{key: 'italy', name: 'Italy'},
			],
			es_ES: [
				{key: 'spain', name: 'Spain'},
				{key: 'italy', name: 'Italy'},
				{key: 'germany', name: 'Germany'},
			],
		});
	}
);

test(
	'Can translate date and date time form fields',
	{tag: '@LPD-43805'},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create object definition

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				enableLocalization: true,
				externalReferenceCode: 'calendarERC',
				label: {
					en_US: 'Calendar',
				},
				name: 'Calendar',
				objectFields: [
					{
						DBType: 'Date',
						businessType: 'Date',
						externalReferenceCode: 'dateERC',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Date',
						},
						localized: true,
						name: 'date',
						required: false,
					},
					{
						DBType: 'DateTime',
						businessType: 'DateTime',
						externalReferenceCode: 'dateTimeERC',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Date Time',
						},
						localized: true,
						name: 'dateTime',
						objectFieldSettings: [
							{
								name: 'timeStorage',
								value: 'convertToUTC',
							} as any,
						],
						required: false,
					},
				],
				pluralLabel: {
					en_US: 'Calendars',
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

		// Create a page with a Form fragment

		const formId = getRandomString();

		const formDefinition = getFormContainerDefinition({
			id: formId,
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([formDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Map the form to the Calendar object and publish the page

		await pageEditorPage.mapFormFragment(formId, 'Calendar', 'all', {
			addLocalizationSelect: true,
		});

		await pageEditorPage.publishPage();

		// Go to view mode and fill the form

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		// Fill the form in spanish

		const dateInput = page.getByRole('textbox', {
			exact: true,
			name: 'Date',
		});
		const dateTimeInput = page.getByRole('textbox', {
			exact: true,
			name: 'Date Time',
		});

		await fillAndClickOutside(page, dateInput, '1970-01-01');

		await fillAndClickOutside(page, dateTimeInput, '1971-01-01T00:00');

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option').filter({hasText: 'es-ES'}),
			trigger: page.getByLabel('Select a language, current language:'),
		});

		await fillAndClickOutside(page, dateInput, '1970-01-02');

		await fillAndClickOutside(page, dateTimeInput, '1971-01-02T01:01');

		// Submit the form

		await page.getByRole('button', {name: 'Submit'}).click();

		await expect(
			page.getByText(
				'Thank you. Your information was successfully received.'
			)
		).toBeVisible();

		// Check the object entry

		const {items} =
			await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
				'c/calendars'
			);

		const item = items[0];

		expect(item.date_i18n).toStrictEqual({
			en_US: '1970-01-01T00:00:00.000Z',
			es_ES: '1970-01-02T00:00:00.000Z',
		});

		expect(item.dateTime_i18n).toStrictEqual({
			en_US: '1971-01-01T00:00:00.000Z',
			es_ES: '1971-01-02T01:01:00.000Z',
		});
	}
);

test(
	'Can translate attachment form fields',
	{tag: '@LPD-46482'},
	async ({
		contentsPage,
		localizationSelectPage,
		page,
		pageEditorPage,
		structureBuilderPage,
	}) => {
		const selectFileFromComputer = async (
			fragment: Locator,
			file: string
		) => {
			const fileChooserPromise = page.waitForEvent('filechooser');

			await fragment.getByText('Select File', {exact: true}).click();

			const fileChooser = await fileChooserPromise;

			await fileChooser.setFiles(file);
		};

		// Create a CMS structure

		const structureERC = getRandomString();
		const structureLabel = getRandomString();

		await structureBuilderPage.createStructureFromData({
			erc: structureERC,
			label: structureLabel,
			name: 'Bananza',
			page: structureBuilderPage,
			publish: false,
		});

		// Add two fields of type select and configure one of them to select files from document library

		await structureBuilderPage.addField('Upload');

		await structureBuilderPage.changeFieldSettings({
			label: 'Upload from computer',
			name: 'uploadFromComputer',
			requestFile: 'computer',
		});

		await structureBuilderPage.addField('Upload');

		await structureBuilderPage.changeFieldSettings({
			label: 'Upload from DM',
			name: 'uploadFromDM',
			requestFile: 'document-library',
		});

		// Publish the structure

		await structureBuilderPage.publishStructure();

		// Customize experience and delete Friendly URL fragment

		await structureBuilderPage.customizeExperience();

		await pageEditorPage.deleteFragment(
			await pageEditorPage.getFragmentId('Friendly URL')
		);

		await pageEditorPage.publishPage();

		// Create a content for this structure

		await contentsPage.goto();

		const contentTitle = getRandomString();

		await contentsPage.createContent(structureLabel);

		await contentsPage.publishButton.waitFor();

		await contentsPage.fillData([{label: 'Title', value: contentTitle}]);

		// Select files for default language

		const filePath1 = path.join(
			__dirname,
			'../main/dependencies/file_upload_image_1.jpg'
		);
		const filePath2 = path.join(
			__dirname,
			'../main/dependencies/file_upload_image_2.jpg'
		);
		const filePath3 = path.join(
			__dirname,
			'../main/dependencies/file_upload_image_3.jpg'
		);
		const filePath4 = path.join(
			__dirname,
			'../main/dependencies/file_upload_image_4.jpg'
		);

		// Select file from computer in the default language

		const computerFileFragment = page.locator('.file-upload').first();

		await selectFileFromComputer(computerFileFragment, filePath1);

		await expect(
			computerFileFragment.getByText('file_upload_image_1.jpg')
		).toBeVisible();

		// Select file from document library in the default language

		const dmFileFragment = page.locator('.file-upload').nth(1);

		await chooseFileFromDocumentLibrary({
			filePath: filePath3,
			page,
			trigger: dmFileFragment.getByText('Select File', {
				exact: true,
			}),
		});

		// Publish

		await contentsPage.saveContent();

		// Edit the content again and change file for DM fragment in Spanish

		await expect(async () => {
			await contentsPage.goto();

			await contentsPage.editContent(contentTitle);

			await localizationSelectPage.switchLanguage('es-ES');

			await expect(async () => {
				await chooseFileFromDocumentLibrary({
					filePath: filePath4,
					page,
					trigger: dmFileFragment.getByText('Select File', {
						exact: true,
					}),
				});
			}).toPass({timeout: 5000});
		}).toPass();

		// Change file for computer fragment

		await selectFileFromComputer(computerFileFragment, filePath2);

		await expect(
			computerFileFragment.getByText('file_upload_image_2.jpg')
		).toBeVisible();

		// Translate also title

		await contentsPage.fillData([
			{label: 'Title', value: `Spanish ${contentTitle}`},
		]);

		// Check translation status

		expect(await localizationSelectPage.getLanguageStatus('es-ES')).toBe(
			'translated'
		);
	}
);

test(
	'Can remove a translation and keep its value in the attachment form field',
	{tag: '@LPD-46482'},
	async ({
		apiHelpers,
		localizationSelectPage,
		page,
		pageEditorPage,
		pageManagementSite,
	}) => {

		// Create object definition

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				enableLocalization: true,
				externalReferenceCode: 'attachmentERC',
				label: {
					en_US: 'Attachment',
				},
				name: 'Attachment',
				objectFields: [
					{
						DBType: 'Long',
						businessType: 'Attachment',
						defaultValue: 'null',
						externalReferenceCode: 'filesFromComputerERC',
						label: {
							en_US: 'Files from Computer',
						},
						localized: true,
						name: 'filesFromComputer',
						objectFieldSettings: [
							{
								name: 'acceptedFileExtensions',
								value: 'jpeg, jpg, pdf, png',
							} as any,
							{
								name: 'maximumFileSize',
								value: 100,
							} as any,
							{
								name: 'fileSource',
								value: 'userComputer',
							} as any,
							{
								name: 'showFilesInDocumentsAndMedia',
								value: false,
							} as any,
						],
						required: false,
					},
					{
						DBType: 'Long',
						businessType: 'Attachment',
						defaultValue: 'null',
						externalReferenceCode: 'filesFromLibraryERC',
						label: {
							en_US: 'Files from Document Library',
						},
						localized: true,
						name: 'filesFromLibrary',
						objectFieldSettings: [
							{
								name: 'acceptedFileExtensions',
								value: 'jpeg, jpg, pdf, png',
							} as any,
							{
								name: 'maximumFileSize',
								value: 100,
							} as any,
							{
								name: 'fileSource',
								value: 'documentsAndMedia',
							} as any,
						],
						required: false,
					},
				],
				pluralLabel: {
					en_US: 'Attachments',
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

		// Create a page with a Form fragment

		const formId = getRandomString();

		const formDefinition = getFormContainerDefinition({
			id: formId,
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([formDefinition]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Map the form to the Attachment object and publish the page

		await pageEditorPage.mapFormFragment(formId, 'Attachment', 'all', {
			addLocalizationSelect: true,
		});

		await pageEditorPage.publishPage();

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		// Select file from computer in the default language

		const fileChooserPromise = page.waitForEvent('filechooser');

		const firstFileUploadFragment = page.locator('.file-upload').first();

		await firstFileUploadFragment
			.getByText('Select File', {exact: true})
			.click();

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles(
			path.join(__dirname, '../main/dependencies/file_upload_image_1.jpg')
		);

		await expect(
			firstFileUploadFragment.getByText('file_upload_image_1.jpg')
		).toBeVisible();

		// Select file from document library in the default language

		const secondFileUploadFragment = page.locator('.file-upload').nth(1);

		await chooseFileFromDocumentLibrary({
			fileName: 'balinese.jpg',
			page,
			trigger: secondFileUploadFragment.getByText('Select File', {
				exact: true,
			}),
		});

		// Change the translation to spanish and remove the files

		await localizationSelectPage.switchLanguage('es-ES');

		await firstFileUploadFragment.getByTitle('Remove Item').click();

		await expect(
			firstFileUploadFragment.getByText('file_upload_image_1.jpg')
		).not.toBeVisible();

		await secondFileUploadFragment.getByTitle('Remove Item').click();

		await expect(
			secondFileUploadFragment.getByText('balinese.jpg')
		).not.toBeVisible();

		// Check that the translations are kept properly

		await localizationSelectPage.switchLanguage('en-US');

		await expect(
			firstFileUploadFragment.getByText('file_upload_image_1.jpg')
		).toBeVisible();

		await expect(
			secondFileUploadFragment.getByText('balinese.jpg')
		).toBeVisible();

		await localizationSelectPage.switchLanguage('es-ES');

		await expect(
			firstFileUploadFragment.getByText('file_upload_image_1.jpg')
		).not.toBeVisible();

		await expect(
			secondFileUploadFragment.getByText('balinese.jpg')
		).not.toBeVisible();

		await localizationSelectPage.switchLanguage('ca-ES');

		await expect(
			firstFileUploadFragment.getByText('file_upload_image_1.jpg')
		).toBeVisible();

		await expect(
			secondFileUploadFragment.getByText('balinese.jpg')
		).toBeVisible();
	}
);

test(
	'Translate an upload field to a language and check that the default language is empty',
	{tag: '@LPD-46482'},
	async ({
		apiHelpers,
		localizationSelectPage,
		page,
		pageEditorPage,
		pageManagementSite,
	}) => {

		// Create object definition

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				enableLocalization: true,
				externalReferenceCode: 'attachmentERC',
				label: {
					en_US: 'Attachment',
				},
				name: 'Attachment',
				objectFields: [
					{
						DBType: 'Long',
						businessType: 'Attachment',
						defaultValue: 'null',
						externalReferenceCode: 'filesFromComputerERC',
						label: {
							en_US: 'Files from Computer',
						},
						localized: true,
						name: 'filesFromComputer',
						objectFieldSettings: [
							{
								name: 'acceptedFileExtensions',
								value: 'jpeg, jpg, pdf, png',
							} as any,
							{
								name: 'maximumFileSize',
								value: 100,
							} as any,
							{
								name: 'fileSource',
								value: 'userComputer',
							} as any,
							{
								name: 'showFilesInDocumentsAndMedia',
								value: false,
							} as any,
						],
						required: false,
					},
					{
						DBType: 'Long',
						businessType: 'Attachment',
						defaultValue: 'null',
						externalReferenceCode: 'filesFromLibraryERC',
						label: {
							en_US: 'Files from Document Library',
						},
						localized: true,
						name: 'filesFromLibrary',
						objectFieldSettings: [
							{
								name: 'acceptedFileExtensions',
								value: 'jpeg, jpg, pdf, png',
							} as any,
							{
								name: 'maximumFileSize',
								value: 100,
							} as any,
							{
								name: 'fileSource',
								value: 'documentsAndMedia',
							} as any,
						],
						required: false,
					},
				],
				pluralLabel: {
					en_US: 'Attachments',
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

		// Create a page with a Form fragment

		const formId = getRandomString();

		const formDefinition = getFormContainerDefinition({
			id: formId,
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([formDefinition]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Map the form to the Attachment object and publish the page

		await pageEditorPage.mapFormFragment(formId, 'Attachment', 'all', {
			addLocalizationSelect: true,
		});

		await pageEditorPage.publishPage();

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		// Change the translation to spanish

		await localizationSelectPage.switchLanguage('es-ES');

		// Select file from computer in spanish

		const fileChooserPromise = page.waitForEvent('filechooser');

		const firstFileUploadFragment = page.locator('.file-upload').first();

		await firstFileUploadFragment
			.getByText('Select File', {exact: true})
			.click();

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles(
			path.join(__dirname, '../main/dependencies/file_upload_image_1.jpg')
		);

		await expect(
			firstFileUploadFragment.getByText('file_upload_image_1.jpg')
		).toBeVisible();

		// Select file from document library in spanish

		const secondFileUploadFragment = page.locator('.file-upload').nth(1);

		await chooseFileFromDocumentLibrary({
			fileName: 'balinese.jpg',
			page,
			trigger: secondFileUploadFragment.getByText('Select File', {
				exact: true,
			}),
		});

		await expect(
			firstFileUploadFragment.getByText('file_upload_image_1.jpg')
		).toBeVisible();

		await expect(
			secondFileUploadFragment.getByText('balinese.jpg')
		).toBeVisible();

		// Check that the translations in the default language are empty

		await localizationSelectPage.switchLanguage('en-US');

		await expect(
			firstFileUploadFragment.getByText('file_upload_image_1.jpg')
		).not.toBeVisible();

		await expect(
			secondFileUploadFragment.getByText('balinese.jpg')
		).not.toBeVisible();
	}
);

test(
	'Shows a warning modal when the page is published and there is a Localization Select fragment but no localizable fields',
	{tag: '@LPD-37927'},
	async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

		// Create a page with a Form fragment and Localication Select

		const formId = getRandomString();

		const formDefinition = getFormContainerDefinition({
			id: formId,
		});

		const localizationSelectDefinition = getFragmentDefinition({
			id: getRandomString(),
			key: 'localization-select',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				localizationSelectDefinition,
				formDefinition,
			]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Map the form to the All Fields, only the Boolean field

		await pageEditorPage.mapFormFragment(formId, 'All Fields', ['Boolean']);

		// Publish and check the warning modal

		await pageEditorPage.publishButton.click();

		await expect(
			page.getByText('Localizable Fields Hidden or Missing')
		).toBeVisible();
	}
);

test(
	'Set unlocalized fields to disabled or readonly when changing language',
	{tag: '@LPD-37927'},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create object definition

		const listTypeDefinition =
			await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

		for (const option of ['Spain', 'Italy']) {
			await apiHelpers.listTypeAdmin.postListTypeEntry({
				key: option,
				listTypeDefinitionExternalReferenceCode:
					listTypeDefinition.externalReferenceCode,
				name_i18n: {en_US: option},
			});
		}

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				enableLocalization: true,
				externalReferenceCode: 'plantERC',
				label: {
					en_US: 'Plant',
				},
				name: 'Plant',
				objectFields: [
					{
						DBType: 'String',
						businessType: 'Text',
						externalReferenceCode: 'countryERC',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Country',
						},
						localized: false,
						name: 'country',
						required: false,
					},
					{
						DBType: 'Clob',
						businessType: 'RichText',
						externalReferenceCode: 'descriptionERC',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Description',
						},
						localized: false,
						name: 'description',
						required: false,
					},
					{
						DBType: 'String',
						businessType: 'Text',
						externalReferenceCode: 'nameERC',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Name',
						},
						localized: true,
						name: 'name',
						required: false,
					},
					{
						DBType: 'Clob',
						businessType: 'LongText',
						externalReferenceCode: 'scientificName',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Scientific Name',
						},
						localized: false,
						name: 'scientificName',
						required: false,
					},
					{
						DBType: 'Boolean',
						businessType: 'Boolean',
						externalReferenceCode: 'evergreen',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Evergreen',
						},
						localized: false,
						name: 'evergreen',
						required: false,
					},
					{
						DBType: 'String',
						businessType: 'Picklist',
						externalReferenceCode: 'selectOriginERC',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Select Origin',
						},
						listTypeDefinitionExternalReferenceCode:
							listTypeDefinition.externalReferenceCode,
						listTypeDefinitionId: listTypeDefinition.id,
						localized: false,
						name: 'selectOrigin',
						required: false,
					},
					{
						DBType: 'String',
						businessType: 'MultiselectPicklist',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Growth Areas',
						},
						listTypeDefinitionExternalReferenceCode:
							listTypeDefinition.externalReferenceCode,
						listTypeDefinitionId: listTypeDefinition.id,
						localized: false,
						name: 'growthAreas',
						required: false,
					},
					{
						DBType: 'Long',
						businessType: 'Attachment',
						defaultValue: 'null',
						externalReferenceCode: 'filesFromLibraryERC',
						label: {
							en_US: 'Files from Document Library',
						},
						localized: false,
						name: 'filesFromLibrary',
						objectFieldSettings: [
							{
								name: 'acceptedFileExtensions',
								value: 'jpeg, jpg, pdf, png',
							} as any,
							{
								name: 'maximumFileSize',
								value: 100,
							} as any,
							{
								name: 'fileSource',
								value: 'documentsAndMedia',
							} as any,
						],
						required: false,
					},
					{
						DBType: 'Integer',
						businessType: 'Integer',
						externalReferenceCode: 'idealTemperatureERC',
						indexed: true,
						indexedAsKeyword: false,
						indexedLanguageId: '',
						label: {
							en_US: 'Ideal Temperature (ºC)',
						},
						localized: false,
						name: 'idealTemperature',
						required: false,
					},
					{
						DBType: 'DateTime',
						externalReferenceCode: 'lastWateringERC',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Last Watering',
						},
						localized: false,
						name: 'lastWatering',
						objectFieldSettings: [
							{
								name: 'timeStorage',
								value: {},
							},
						],
					},
					{
						DBType: 'Date',
						externalReferenceCode: 'plantingDateERC',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Planting Date',
						},
						localized: false,
						name: 'plantingDate',
					},
				],
				pluralLabel: {
					en_US: 'Plants',
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

		// Create a page with a Form fragment

		const formId = getRandomString();

		const formDefinition = getFormContainerDefinition({
			id: formId,
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([formDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Map the form to the Plant object and publish the page

		await pageEditorPage.mapFormFragment(formId, 'Plant', 'all', {
			addLocalizationSelect: true,
		});

		await pageEditorPage.publishPage();

		// Go to view mode

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		// Change the translation language to spanish

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option').filter({hasText: 'es-ES'}),
			trigger: page.getByLabel('Select a language, current language:'),
		});

		// Check the tooltip when the unlocalized fields are disabled

		await expect(
			page.getByLabel('Evergreen field cannot be localized')
		).toBeVisible();

		await expect(
			page.getByLabel('Country field cannot be localized')
		).toBeVisible();

		await expect(
			page.getByLabel('Description field cannot be localized')
		).toBeVisible();

		await expect(
			page.getByLabel('Scientific Name field cannot be localized')
		).toBeVisible();

		await expect(
			page.getByLabel('Select Origin field cannot be localized')
		).toBeVisible();

		await expect(
			page.getByLabel('Growth Areas field cannot be localized')
		).toBeVisible();

		await expect(
			page.getByLabel(
				'Files from Document Library field cannot be localized'
			)
		).toBeVisible();

		await expect(
			page.getByLabel('Ideal Temperature (ºC) field cannot be localized')
		).toBeVisible();

		await expect(
			page.getByLabel('Last Watering field cannot be localized')
		).toBeVisible();

		await expect(
			page.getByLabel('Planting Date field cannot be localized')
		).toBeVisible();

		// Check that unlocalized fields are disabled

		await expect(
			page.getByRole('checkbox', {
				name: 'Evergreen',
			})
		).toBeDisabled();

		await expect(
			page.getByRole('textbox', {name: 'Country'})
		).toBeDisabled();

		const richTextToolbarButton = page
			.locator('.rich-text-input', {
				hasText: 'Description',
			})
			.locator('.ck-button')
			.last();

		await expect(richTextToolbarButton).toBeDisabled();

		await expect(page.locator('.rich-text-input--disabled')).toBeAttached();

		await expect(
			page.getByRole('textbox', {name: 'Scientific Name'})
		).toBeDisabled();

		await expect(page.getByPlaceholder('Choose an option')).toBeDisabled();

		const firstMultiSelectOption = page.getByRole('checkbox', {
			name: 'Spain',
		});
		const secondMultiSelectOption = page.getByRole('checkbox', {
			name: 'Italy',
		});

		await expect(firstMultiSelectOption).toBeDisabled();
		await expect(secondMultiSelectOption).toBeDisabled();

		await expect(page.getByText('Select File')).toBeDisabled();

		await expect(
			page.getByRole('spinbutton', {name: 'Ideal Temperature (ºC)'})
		).toBeDisabled();

		await expect(
			page.getByRole('textbox', {name: 'Last Watering'})
		).toBeDisabled();

		await expect(
			page.getByRole('textbox', {name: 'Planting Date'})
		).toBeDisabled();

		// Check that the read only labels are not visibles

		const checkboxReadOnlyLabel = page
			.getByText('Evergreen')
			.getByText('(Read Only)');

		const inputTextReadOnlyLabel = page
			.getByText('Country')
			.getByText('(Read Only)');

		const textareaReadOnlyLabel = page
			.getByText('Scientific Name')
			.getByText('(Read Only)');

		const selectReadOnlyLabel = page
			.getByText('Select Origin')
			.getByText('(Read Only)');

		const multiSelectReadOnlyLabel = page
			.getByText('Growth Areas')
			.getByText('(Read Only)');

		const uploadFileReadOnlyLabel = page
			.getByText('Files from Document Library')
			.getByText('(Read Only)');

		const numericReadOnlyLabel = page
			.getByText('Ideal Temperature (ºC)')
			.getByText('(Read Only)');

		const dateReadOnlyLabel = page
			.getByText('Planting Date')
			.getByText('(Read Only)');

		const dateTimeReadOnlyLabel = page
			.getByText('Last Watering')
			.getByText('(Read Only)');

		await expect(checkboxReadOnlyLabel).not.toBeVisible();
		await expect(inputTextReadOnlyLabel).not.toBeVisible();
		await expect(textareaReadOnlyLabel).not.toBeVisible();
		await expect(selectReadOnlyLabel).not.toBeVisible();
		await expect(multiSelectReadOnlyLabel).not.toBeVisible();
		await expect(uploadFileReadOnlyLabel).not.toBeVisible();
		await expect(numericReadOnlyLabel).not.toBeVisible();
		await expect(dateReadOnlyLabel).not.toBeVisible();
		await expect(dateTimeReadOnlyLabel).not.toBeVisible();

		// Go to edit mode and change unlocalized field configuration to read only

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.selectFragment(
			await pageEditorPage.getFragmentId('Form Container')
		);

		await pageEditorPage.changeConfiguration({
			fieldLabel: 'Unlocalizable Fields State',
			tab: 'General',
			value: 'read-only',
		});

		await pageEditorPage.changeConfiguration({
			fieldLabel: 'Unlocalizable Fields Message',
			tab: 'General',
			value: 'field is not localizable message',
		});

		await pageEditorPage.publishPage();

		// Go to view mode and check that the config is applied

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option').filter({hasText: 'es-ES'}),
			trigger: page.getByLabel('Select a language, current language:'),
		});

		// Check the read only unlocalized fields

		await expect(
			page.getByLabel('field is not localizable message')
		).toHaveCount(10);

		await expect(checkboxReadOnlyLabel).toBeVisible();
		await expect(inputTextReadOnlyLabel).toBeVisible();
		await expect(textareaReadOnlyLabel).toBeVisible();
		await expect(selectReadOnlyLabel).toBeVisible();
		await expect(multiSelectReadOnlyLabel).toBeVisible();
		await expect(uploadFileReadOnlyLabel).toBeVisible();
		await expect(numericReadOnlyLabel).toBeVisible();
		await expect(dateReadOnlyLabel).toBeVisible();
		await expect(dateTimeReadOnlyLabel).toBeVisible();

		await expect(page.getByLabel('Country')).toHaveAttribute('readonly');

		await expect(richTextToolbarButton).toBeDisabled();

		await expect(
			page.locator('.rich-text-input--disabled')
		).not.toBeAttached();

		await expect(page.getByLabel('Scientific Name')).toHaveAttribute(
			'readonly'
		);

		await expect(page.getByLabel('Select Origin')).toHaveAttribute(
			'readonly'
		);

		await firstMultiSelectOption.click({force: true});
		await secondMultiSelectOption.click({force: true});

		await expect(firstMultiSelectOption).not.toBeChecked();
		await expect(secondMultiSelectOption).not.toBeChecked();

		await expect(page.getByText('No file selected')).toHaveAttribute(
			'readonly'
		);

		await expect(page.getByLabel('Ideal Temperature (ºC)')).toHaveAttribute(
			'readonly'
		);

		await expect(page.getByLabel('Last Watering')).toHaveAttribute(
			'readonly'
		);

		await expect(page.getByLabel('Planting Date')).toHaveAttribute(
			'readonly'
		);
	}
);

test(
	'Can visualize and edit translations',
	{tag: '@LPD-37927'},
	async ({
		apiHelpers,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {

		// Create an object with translations

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				enableLocalization: true,
				externalReferenceCode: 'translationFieldsGroupERC',
				label: {
					en_US: 'Translation Fields Group',
				},
				name: 'TranslationFieldsGroup',
				objectFields: [
					{
						DBType: 'Clob',
						businessType: 'RichText',
						externalReferenceCode: 'richTextERC',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Rich Text',
						},
						localized: true,
						name: 'richText',
						required: false,
					},
					{
						DBType: 'Clob',
						businessType: 'LongText',
						externalReferenceCode: 'longTextERC',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Long Text',
						},
						localized: true,
						name: 'longText',
						required: false,
					},
					{
						DBType: 'String',
						businessType: 'Text',
						externalReferenceCode: 'text',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Text',
						},
						localized: true,
						name: 'text',
						required: false,
					},
					{
						DBType: 'Boolean',
						businessType: 'Boolean',
						externalReferenceCode: 'booleanERC',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Boolean',
						},
						localized: true,
						name: 'boolean',
						required: false,
					},
				],
				panelCategoryKey: 'control_panel.object',
				pluralLabel: {
					en_US: 'Translation Fields Groups',
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
				boolean_i18n: {
					en_US: true,
					es_ES: false,
				},
				longText_i18n: {
					en_US: 'long text english',
					es_ES: 'long text spanish',
				},
				richText_i18n: {
					en_US: 'rich text english',
					es_ES: 'rich text spanish',
				},
				text_i18n: {
					en_US: 'text english',
					es_ES: 'text spanish',
				},
			},
			'c/' + objectDefinition.name.toLowerCase() + 's'
		);

		// Create a display page and add a form container with localization select

		const displayPageTemplateName = getRandomString();

		const className =
			await apiHelpers.jsonWebServicesClassName.fetchClassName(
				objectDefinition.className
			);

		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
			{
				classNameId: className.classNameId,
				classTypeId: '0',
				groupId: site.id,
				name: displayPageTemplateName,
			}
		);

		// Go to edit display page template

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		await displayPageTemplatesPage.editTemplate(displayPageTemplateName);

		await pageEditorPage.addFragment('Form Components', 'Form Container');

		const formId = await pageEditorPage.getFragmentId('Form Container');

		await pageEditorPage.mapFormFragment(
			formId,
			'Translation Fields Group (Default)',
			'all',
			{addLocalizationSelect: true}
		);

		await displayPageTemplatesPage.publishTemplate();

		// Go to the object display page

		await page.goto(
			`/web${site.friendlyUrlPath}/e/${displayPageTemplateName}/${className.classNameId}/${objectEntry.id}`
		);

		// Assert that translation is displayed correctly

		const checkboxField = page.getByRole('checkbox', {name: 'Boolean'});

		const longTextField = page.getByRole('textbox', {
			exact: true,
			name: 'Long Text',
		});

		const richTextField = page.locator('.ck-editor__editable');

		const textField = page.getByRole('textbox', {
			exact: true,
			name: 'Text',
		});

		await expect(checkboxField).toBeChecked();

		await expect(longTextField).toHaveValue('long text english');

		await expect(
			richTextField.getByText('rich text english')
		).toBeVisible();

		await expect(textField).toHaveValue('text english');

		// Fill new values for the translation

		await checkboxField.uncheck();

		await longTextField.fill('long text english 1');

		await textField.fill('text english 1');

		await richTextField.fill('rich text english 1');

		// Assert spanish translation is correct

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option').filter({hasText: 'es-ES'}),
			trigger: page.getByLabel('Select a language, current language:'),
		});

		await expect(checkboxField).not.toBeChecked();

		await expect(longTextField).toHaveValue('long text spanish');

		await expect(
			richTextField.getByText('rich text spanish')
		).toBeVisible();

		await expect(textField).toHaveValue('text spanish');

		// Fill new values

		await checkboxField.check();

		await longTextField.fill('long text spanish 1');

		await textField.fill('text spanish 1');

		await richTextField.fill('rich text spanish 1');

		// Edit the object

		await page.getByRole('button', {name: 'Submit'}).click();

		await expect(
			page.getByText(
				'Thank you. Your information was successfully received.'
			)
		).toBeVisible();

		// Go to custom object admin an check the values

		await goToObjectEntity({
			entityName: 'Translation Fields Group',
			entityPluralName: 'Translation Fields Groups',
			page,
			siteScope: 'Control Panel',
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				exact: true,
				name: 'View',
			}),
			trigger: page
				.locator('.fds tbody .cell-item-actions .dropdown-toggle')
				.last(),
		});

		await longTextField.waitFor();

		await expect(checkboxField).not.toBeChecked();

		await expect(page.getByText('long text english 1')).toBeVisible();

		await expect(
			richTextField.getByText('rich text english 1')
		).toBeVisible();

		await expect(page.locator('input.ddm-field-text')).toHaveValue(
			'text english 1'
		);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				name: 'Español (España)',
			}),
			trigger: page.getByTestId('triggerButton').first(),
		});

		await expect(checkboxField).toBeChecked();

		await expect(page.getByText('long text spanish 1')).toBeVisible();

		await expect(
			richTextField.getByText('rich text spanish 1')
		).toBeVisible();

		await expect(page.locator('input.ddm-field-text')).toHaveValue(
			'text spanish 1'
		);
	}
);

test(
	'Visualize text fields in RTL languages',
	{tag: '@LPD-48787'},
	async ({apiHelpers, page, pageEditorPage, site}) => {
		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const listTypeDefinition =
			await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

		for (const option of ['Spain', 'Italy']) {
			await apiHelpers.listTypeAdmin.postListTypeEntry({
				key: option,
				listTypeDefinitionExternalReferenceCode:
					listTypeDefinition.externalReferenceCode,
				name_i18n: {en_US: option},
			});
		}

		const objectFields: ObjectField[] = [
			{
				DBType: 'Clob',
				businessType: 'RichText',
				externalReferenceCode: 'richTextERC',
				indexed: true,
				indexedAsKeyword: false,
				label: {
					en_US: 'Rich Text',
				},
				localized: true,
				name: 'richText',
				required: false,
			},
			{
				DBType: 'Clob',
				businessType: 'LongText',
				externalReferenceCode: 'longTextERC',
				indexed: true,
				indexedAsKeyword: false,
				label: {
					en_US: 'Long Text',
				},
				localized: true,
				name: 'longText',
				required: false,
			},
			{
				DBType: 'String',
				businessType: 'Text',
				externalReferenceCode: 'text',
				indexed: true,
				indexedAsKeyword: false,
				label: {
					en_US: 'Text',
				},
				localized: true,
				name: 'text',
				required: false,
			},
			{
				DBType: 'Integer',
				externalReferenceCode: 'numeric-erc',
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: '',
				label: {
					en_US: 'Numeric',
				},
				localized: true,
				name: 'numeric',
			},
			{
				DBType: 'DateTime',
				externalReferenceCode: 'date-time-erc',
				indexed: true,
				indexedAsKeyword: false,
				label: {
					en_US: 'Date And Time',
				},
				localized: true,
				name: 'dateAndTime',
				objectFieldSettings: [
					{
						name: 'timeStorage',
						value: {},
					},
				],
			},
			{
				DBType: 'Date',
				externalReferenceCode: 'date-erc',
				indexed: true,
				indexedAsKeyword: false,
				label: {
					en_US: 'Date',
				},
				localized: true,
				name: 'date',
			},
			{
				DBType: 'String',
				businessType: 'Picklist',
				externalReferenceCode: 'selectERC',
				indexed: true,
				indexedAsKeyword: false,
				label: {
					en_US: 'Select',
				},
				listTypeDefinitionExternalReferenceCode:
					listTypeDefinition.externalReferenceCode,
				listTypeDefinitionId: listTypeDefinition.id,
				localized: true,
				name: 'select',
				required: false,
			},
		];

		// Create an object with localizable fields

		const {body: localizableObjectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				enableLocalization: true,
				externalReferenceCode: 'localizableFieldsGroupERC',
				label: {
					en_US: 'Localizable Fields Group',
				},
				name: 'LocalizableFieldsGroup',
				objectFields,
				panelCategoryKey: 'control_panel.object',
				pluralLabel: {
					en_US: 'Localizable Fields Groups',
				},
				portlet: true,
				scope: 'company',
				status: {
					code: 0,
				},
			});

		apiHelpers.data.push({
			id: localizableObjectDefinition.id,
			type: 'objectDefinition',
		});

		// Create an object with unlocalizable fields

		const {body: nonLocalizableObjectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				enableLocalization: true,
				externalReferenceCode: 'UnlocalizableFieldsGroupERC',
				label: {
					en_US: 'Unlocalizable Fields Group',
				},
				name: 'UnlocalizableFieldsGroup',
				objectFields: objectFields.map((field) => ({
					...field,
					localized: true,
				})),
				panelCategoryKey: 'control_panel.object',
				pluralLabel: {
					en_US: 'Unlocalizable Fields Groups',
				},
				portlet: true,
				scope: 'company',
				status: {
					code: 0,
				},
			});

		apiHelpers.data.push({
			id: nonLocalizableObjectDefinition.id,
			type: 'objectDefinition',
		});

		// Create a page with two Forms for both objects

		const localizableFormId = getRandomString();

		const localizableFormDefinition = getFormContainerDefinition({
			id: localizableFormId,
		});

		const unlocalizableFormId = getRandomString();

		const unlocalizableFormDefinition = getFormContainerDefinition({
			id: unlocalizableFormId,
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				localizableFormDefinition,
				unlocalizableFormDefinition,
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Map the forms to both objects and publish the page

		await pageEditorPage.mapFormFragment(
			localizableFormId,
			'Localizable Fields Group',
			'all'
		);

		await pageEditorPage.mapFormFragment(
			unlocalizableFormId,
			'Unlocalizable Fields Group',
			'all'
		);

		await pageEditorPage.addFragment(
			'Form Components',
			'Localization Select'
		);

		await pageEditorPage.publishPage();

		// Go to view mode and check the "dir" attribute of the fields

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		const localizableForm = page
			.locator('.lfr-layout-structure-item-form')
			.first();

		const unlocalizableForm = page
			.locator('.lfr-layout-structure-item-form')
			.nth(1);

		const getFields = (form: Locator) => [
			form.getByRole('textbox', {
				exact: true,
				name: 'Text',
			}),
			form.getByRole('textbox', {
				exact: true,
				name: 'Long Text',
			}),
			form.getByLabel('Numeric'),
			form.getByRole('textbox', {
				exact: true,
				name: 'Date',
			}),
			form.getByRole('textbox', {
				exact: true,
				name: 'Date And Time',
			}),
			form.locator('.ck-editor__editable'),
			form.getByPlaceholder('Choose an Option'),
		];

		// Check the "dir" attribute before changing the translation language

		const localizableFields = getFields(localizableForm);
		const unlocalizableFields = getFields(unlocalizableForm);

		for (const field of localizableFields) {
			await expect(field).toHaveAttribute('dir', 'ltr');
		}

		for (const field of unlocalizableFields) {
			await expect(field).toHaveAttribute('dir', 'ltr');
		}

		// Change the translation language

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {
				name: 'Arabic (Saudi Arabia) Language',
			}),
			trigger: page.getByLabel('Select a language, current language:'),
		});

		// Check the "dir" attribute after changing the translation language

		for (const field of localizableFields) {
			await expect(field).toHaveAttribute('dir', 'rtl');
		}

		for (const field of unlocalizableFields) {
			await expect(field).toHaveAttribute('dir', 'rtl');
		}
	}
);

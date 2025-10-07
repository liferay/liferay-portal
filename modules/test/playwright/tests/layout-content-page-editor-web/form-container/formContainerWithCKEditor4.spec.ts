/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinitionAPI,
	ObjectField,
	ObjectFieldAPI,
} from '@liferay/object-admin-rest-client-js';
import {Locator, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../fixtures/displayPageTemplatesPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {fragmentsPagesTest} from '../../../fixtures/fragmentPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageManagementSiteTest} from '../../../fixtures/pageManagementSiteTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {goToObjectEntity} from '../../setup/page-management-site/main/utils/goToObjectEntity';
import getFormContainerDefinition from '../main/utils/getFormContainerDefinition';
import getPageDefinition from '../main/utils/getPageDefinition';

const test = mergeTests(
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
		'LPD-17564': {enabled: true},
		'LPD-21926': {enabled: true},
		'LPD-32050': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	fragmentsPagesTest,
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	pageManagementSiteTest
);

test(
	'Can translate text form fields with CKEditor 4',
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

		await page.locator('iframe[title="editor"]').waitFor();

		await page.getByLabel('Long Text').fill('long text english');

		await page
			.getByRole('textbox', {exact: true, name: 'Text'})
			.fill('text english');

		await page.evaluate(() => {
			Object.values((window as any).CKEDITOR.instances).forEach(
				(editor: any) => editor.setData('rich text english')
			);
		});

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

		await page.evaluate(() => {
			Object.values((window as any).CKEDITOR.instances).forEach(
				(editor: any) => editor.setData('rich text español')
			);
		});

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
		await expect(
			page
				.frameLocator('iframe[title="editor"]')
				.getByText('rich text english')
		).toBeVisible();

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
		await expect(
			page
				.frameLocator('iframe[title="editor"]')
				.getByText('rich text español')
		).toBeVisible();
		await expect(page.locator('input.ddm-field-text')).toHaveValue(
			'text español'
		);
	}
);

test(
	'Set unlocalized fields to disabled or readonly when changing language with CKEditor 4',
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

		await expect(
			page
				.locator('.rich-text-input', {
					hasText: 'Description',
				})
				.frameLocator('iframe[title="editor"]')
				.locator('body')
		).toHaveAttribute('aria-disabled', 'true');

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

		await expect(
			page
				.locator('.rich-text-input', {
					hasText: 'Description (Read Only)',
				})
				.frameLocator('iframe')
				.locator('body')
		).toHaveAttribute('aria-readonly', 'true');

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
	'Can visualize and edit translations with CKEditor 4',
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

		const textField = page.getByRole('textbox', {
			exact: true,
			name: 'Text',
		});

		const richTextField = page.frameLocator('iframe[title="editor"]');

		await expect(async () => {
			await expect(
				richTextField.getByText('rich text english')
			).toBeVisible();
		}).toPass();

		await expect(checkboxField).toBeChecked();

		await expect(longTextField).toHaveValue('long text english');

		await expect(textField).toHaveValue('text english');

		// Fill new values for the translation

		await checkboxField.uncheck();

		await longTextField.fill('long text english 1');

		await textField.fill('text english 1');

		await page.evaluate(() => {
			Object.values((window as any).CKEDITOR.instances).forEach(
				(editor: any) => editor.setData('rich text english 1')
			);
		});

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

		await page.evaluate(() => {
			Object.values((window as any).CKEDITOR.instances).forEach(
				(editor: any) => editor.setData('rich text spanish 1')
			);
		});

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
	'Visualize text fields in RTL languages with CKEditor 4',
	{tag: '@LPD-48787'},
	async ({
		apiHelpers,
		localizationSelectPage,
		page,
		pageEditorPage,
		site,
	}) => {
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
			form.frameLocator('iframe[title="editor"]').locator('html'),
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

		await localizationSelectPage.switchLanguage('ar-SA');

		// Check the "dir" attribute after changing the translation language

		for (const field of localizableFields) {
			await expect(field).toHaveAttribute('dir', 'rtl');
		}

		for (const field of unlocalizableFields) {
			await expect(field).toHaveAttribute('dir', 'rtl');
		}
	}
);

test(
	'Check read-only fields with CKEditor 4',
	{tag: ['@LPD-44528']},
	async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

		// Create a new object definition with all fields

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				externalReferenceCode: 'readonly-object-erc',
				label: {
					en_US: 'Read Only Object',
				},
				name: 'ReadOnlyObject',
				objectFields: [
					{
						DBType: 'Boolean',
						externalReferenceCode: 'boolean-erc',
						indexed: true,
						indexedAsKeyword: true,
						label: {
							en_US: 'Boolean',
						},
						name: 'boolean',
					},
					{
						DBType: 'DateTime',
						externalReferenceCode: 'date-time-erc',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Date And Time',
						},
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
						name: 'date',
					},
					{
						DBType: 'Clob',
						externalReferenceCode: 'long-text-erc',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Long Text',
						},
						name: 'longText',
					},
					{
						DBType: 'Long',
						businessType: 'Attachment',
						externalReferenceCode: 'dl-file-upload-erc',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'DL File',
						},
						localized: false,
						name: 'dlFileUpload',
						objectFieldSettings: [
							{
								name: 'acceptedFileExtensions',
								value: 'pdf',
							},
							{
								name: 'maximumFileSize',
								value: 100,
							},
							{
								name: 'fileSource',
								value: 'documentsAndMedia',
							},
						] as any,
						type: 'Long',
					},
					{
						DBType: 'String',
						externalReferenceCode: 'text-erc',
						indexed: true,
						indexedAsKeyword: true,
						label: {
							en_US: 'Text',
						},
						name: 'text',
					},
					{
						DBType: 'Clob',
						businessType: 'RichText',
						externalReferenceCode: 'rich-text-erc',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Rich Text',
						},
						name: 'richText',
					},
					{
						DBType: 'String',
						businessType: 'Picklist',
						externalReferenceCode: 'picklist-erc',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Picklist',
						},
						listTypeDefinitionExternalReferenceCode:
							'lemon-dimensions-picklist-erc',
						name: 'picklist',
					},
					{
						DBType: 'String',
						businessType: 'MultiselectPicklist',
						externalReferenceCode: 'multiselect-picklist-erc',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'MultiSelect Picklist',
						},
						listTypeDefinitionExternalReferenceCode:
							'lemon-dimensions-picklist-erc',
						name: 'multiSelectPicklist',
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
						name: 'numeric',
					},
				],
				pluralLabel: {
					en_US: 'ReadOnlyObjects',
				},
				scope: 'company',
				status: {
					code: 0,
				},
			});

		// Set readOnly to true for all fields

		const objectFieldAPIClient =
			await apiHelpers.buildRestClient(ObjectFieldAPI);

		for (const objectField of objectDefinition.objectFields) {
			await objectFieldAPIClient.putObjectField(objectField.id, {
				...objectField,
				readOnly: 'true',
			});
		}

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		// Create a page with a form mapped to 'Read Only Object'

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

		await pageEditorPage.mapFormFragment(formId, 'Read Only Object');

		await pageEditorPage.publishPage();

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		// Check that all fields have the corresponding attribute and label

		await expect(
			page
				.locator('.rich-text-input', {
					hasText: 'Rich Text (Read Only)',
				})
				.frameLocator('iframe')
				.locator('body')
		).toHaveAttribute('aria-readonly', 'true');

		[
			'Boolean (Read Only)',
			'Date (Read Only)',
			'Date And Time (Read Only)',
			'Long Text (Read Only)',
			'DL File (Read Only)',
			'Text (Read Only)',
			'Picklist (Read Only)',
			'Numeric (Read Only)',
		].forEach(async (label) => {
			await expect(page.getByLabel(label, {exact: true})).toHaveAttribute(
				'readonly',
				''
			);
		});

		(
			await page
				.getByLabel('MultiSelect Picklist (Read Only)')
				.locator('input')
				.all()
		).forEach(async (input) => {
			await expect(input).toHaveAttribute('readonly', '');
		});
	}
);

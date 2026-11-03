/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {formsPagesTest} from '../../../fixtures/formsPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import {liferayConfig} from '../../../liferay.config';
import {FormViewPage} from '../../../pages/dynamic-data-mapping-form-web/FormViewPage';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {deleteItems} from './utils/deleteItems';

const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-31212': {enabled: true},
	}),
	loginTest(),
	formsPagesTest,
	systemSettingsPageTest
);

test.afterEach(async ({formsPage, page, systemSettingsPage}) => {
	await formsPage.goTo();

	await deleteItems(formsPage);

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

	await expect(page.getByText('Success:Your request')).toBeVisible();
});

test.beforeEach(({page}) => {
	page.setViewportSize({height: 1080, width: 1920});
});

test('Assert multiple selection field associated with rules', async ({
	formBuilderFieldSettingsSidePanelPage,
	formBuilderPage,
	formBuilderSidePanelPage,
	page,
	rulesBuilderPage,
}) => {
	await test.step('creating form and associated rule', async () => {
		await formBuilderPage.goToNew();

		await expect(formBuilderPage.newFormHeading).toBeVisible();

		await formBuilderPage.fillFormTitle('Form' + getRandomInt());

		await formBuilderSidePanelPage.addFieldByDoubleClick(
			'Single Selection'
		);

		await formBuilderFieldSettingsSidePanelPage.addOptions(
			2,
			'single option '
		);

		await formBuilderSidePanelPage.clickBackButton();

		await formBuilderSidePanelPage.addFieldByDoubleClick(
			'Multiple Selection'
		);

		await formBuilderFieldSettingsSidePanelPage.addOptions(
			4,
			'multiple option '
		);

		await rulesBuilderPage.rulesTab.click();

		await rulesBuilderPage.addElementsButton.click();

		await rulesBuilderPage.selectConditionLeftFormField('Single Selection');

		await rulesBuilderPage.selectConditionOperator('Is Equal To');

		await rulesBuilderPage.selectConditionOperatorValueSource('Value');

		await rulesBuilderPage.selectConditionRightFormField('single option 0');

		await rulesBuilderPage.selectAction('Show');

		await page.getByTitle('Choose an Option').click();

		await page.getByRole('option', {name: 'Multiple Selection'}).click();

		await rulesBuilderPage.addFormRuleActionButton.click();

		await page.getByTitle('Choose an Option').click();

		await page.getByRole('option', {name: 'Require'}).click();

		await page.getByTitle('Choose an Option').click();

		await page.getByRole('option', {name: 'Multiple Selection'}).click();

		await rulesBuilderPage.saveButton.click();

		await formBuilderPage.formTab.click();
	});

	await test.step('verify that association with show and require rules clears its values when hidden and displays no errors when shown again', async () => {
		const formPreviewPagePromise = page.waitForEvent('popup');

		await formBuilderPage.previewButton.click();

		const formPreviewPage = await formPreviewPagePromise;

		await formPreviewPage
			.locator('label')
			.filter({hasText: 'single option 0'})
			.click();

		await formPreviewPage.getByLabel('multiple option 1').click();

		await formPreviewPage
			.locator('label')
			.filter({hasText: 'single option 1'})
			.click();

		await expect(
			formPreviewPage.getByRole('group', {name: 'Multiple Selection'})
		).toBeHidden();

		await formPreviewPage
			.locator('label')
			.filter({hasText: 'single option 0'})
			.click();

		await expect(
			formPreviewPage.getByText('This field is required.')
		).toBeHidden();

		const multipleOption = (optionNumber: number) =>
			formPreviewPage.getByLabel(`multiple option ${optionNumber}`);

		await expect(multipleOption(0)).not.toBeChecked();

		await expect(multipleOption(1)).not.toBeChecked();

		await expect(multipleOption(2)).not.toBeChecked();

		await expect(multipleOption(3)).not.toBeChecked();

		await formPreviewPage.close();
	});

	await test.step('verify that association with a show rule mantains its predefined value on change', async () => {
		await formBuilderSidePanelPage.advancedTab.click();

		await formBuilderFieldSettingsSidePanelPage.fillMultiplePredefinedValues(
			['multiple option 0', 'multiple option 3']
		);

		await page.waitForTimeout(1000);

		const formPreviewPagePromise = page.waitForEvent('popup');

		await formBuilderPage.previewButton.click();

		const formPreviewPage = await formPreviewPagePromise;

		await formPreviewPage
			.locator('label')
			.filter({hasText: 'single option 0'})
			.click();

		await formPreviewPage
			.locator('label')
			.filter({hasText: 'single option 1'})
			.click();

		await expect(
			formPreviewPage.getByRole('group', {name: 'Multiple Selection'})
		).toBeHidden();

		await formPreviewPage
			.locator('label')
			.filter({hasText: 'single option 0'})
			.click();

		await expect(
			formPreviewPage.getByText('This field is required.')
		).toBeHidden();

		const multipleOption = (optionNumber: number) =>
			formPreviewPage.getByLabel(`multiple option ${optionNumber}`);

		await expect(multipleOption(0)).toBeChecked();

		await expect(multipleOption(1)).not.toBeChecked();

		await expect(multipleOption(2)).not.toBeChecked();

		await expect(multipleOption(3)).toBeChecked();

		await formPreviewPage.getByLabel('multiple option 1').click();

		await expect(multipleOption(0)).toBeChecked();

		await expect(multipleOption(1)).toBeChecked();

		await expect(multipleOption(2)).not.toBeChecked();

		await expect(multipleOption(3)).toBeChecked();

		await formPreviewPage.close();
	});
});

test('Assert that Show action rule is not triggered when typing into an unrelated field', async ({
	formBuilderPage,
	formBuilderSidePanelPage,
	page,
	rulesBuilderPage,
}) => {
	await formBuilderPage.goToNew();

	await expect(formBuilderPage.newFormHeading).toBeVisible();

	await formBuilderPage.fillFormTitle('Form' + getRandomInt());

	await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

	await formBuilderSidePanelPage.label.fill('Text 1');

	await formBuilderSidePanelPage.clickBackButton();

	await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

	await formBuilderSidePanelPage.label.fill('Text 2');

	await formBuilderSidePanelPage.clickBackButton();

	await formBuilderSidePanelPage.addFieldByDoubleClick('Date');

	await formBuilderSidePanelPage.requiredFieldToggleSwitch.click();

	await rulesBuilderPage.rulesTab.click();

	await rulesBuilderPage.addElementsButton.click();

	await rulesBuilderPage.selectConditionLeftFormField('Text 1');

	await rulesBuilderPage.selectConditionOperator('Is Equal To');

	await rulesBuilderPage.selectConditionOperatorValueSource('Value');

	await rulesBuilderPage.conditionRightFormFieldInput.click();

	await rulesBuilderPage.conditionRightFormFieldInput.fill('1');

	await rulesBuilderPage.selectAction('Show');

	await page.getByRole('combobox').nth(4).click();
	await page.getByRole('option', {name: 'Date'}).click();

	await rulesBuilderPage.saveButton.click();

	await formBuilderPage.formTab.click();

	const formPreviewPagePromise = page.waitForEvent('popup');

	await formBuilderPage.previewButton.click();

	const formPreviewPage = await formPreviewPagePromise;

	await formPreviewPage.getByLabel('Text 2').click();

	await formPreviewPage.getByLabel('Text 2').fill('1');

	const dateField = formPreviewPage.getByPlaceholder('__/__/____');

	await expect(dateField).toBeHidden();

	await formPreviewPage.getByLabel('Text 2').fill('');
	await formPreviewPage.getByLabel('Text 1').click();
	await formPreviewPage.getByLabel('Text 1').fill('1');

	await expect(dateField).toBeVisible();

	await formPreviewPage.close();
});

test('Select from list with multiple selections allowed is auto-filled by data provider defined in rule', async ({
	dataProviderPage,
	formBuilderFieldSettingsSidePanelPage,
	formBuilderPage,
	formBuilderSidePanelPage,
	formsPage,
	page,
	rulesBuilderPage,
	systemSettingsPage,
}) => {
	test.slow();

	// configure data provider

	await systemSettingsPage.goToSystemSetting(
		'Data Providers',
		'Data Providers'
	);

	await page.getByLabel('Access Local Network').check();

	await page
		.getByRole('button', {name: 'Save'})
		.or(page.getByRole('button', {name: 'Update'}))
		.click();

	await expect(page.getByText('Success:Your request')).toBeVisible();

	await expect(page.getByLabel('Access Local Network')).toBeChecked();

	await formsPage.goTo();

	await formsPage.dataProvidersTab.click();

	await dataProviderPage.addNewDataProviderLink.click();

	const dataProviderName = getRandomString();

	await dataProviderPage.nameInputField.fill(dataProviderName);

	await dataProviderPage.urlInputField.fill(
		`${liferayConfig.environment.baseUrl}/api/jsonws/country/get-countries/`
	);

	await dataProviderPage.userNameInputField.fill('test@liferay.com');

	await dataProviderPage.passwordInputField.fill('test');

	await dataProviderPage.timeoutInputField.fill('30000');

	await dataProviderPage.outputPathField.fill('$..nameCurrentValue;$..name');

	await dataProviderPage.selectOutputType('List');

	await dataProviderPage.outputLabelField.fill('Name');

	await dataProviderPage.saveButton.click();

	await expect(page.getByText('Success:Your request')).toBeVisible();

	await formsPage.formsTab.click();

	// create a form and configure two fields and a translation

	await formsPage.newFormButton.click();

	await formBuilderSidePanelPage.addSingleSelectionButton.dblclick();

	await formBuilderFieldSettingsSidePanelPage.addOptions(2);

	await formBuilderSidePanelPage.backButton.click();

	await formBuilderSidePanelPage.addSelectFromListButton.dblclick();

	await formBuilderFieldSettingsSidePanelPage.selectCreateListSetting(
		'From Autofill'
	);

	await formBuilderFieldSettingsSidePanelPage.advancedTabButton.click();

	await formBuilderFieldSettingsSidePanelPage.allowMultipleSelections();

	await page.waitForLoadState('networkidle');

	await formBuilderSidePanelPage.backButton.click();

	await formBuilderPage.translationManagerPlusButton.click();

	await page.getByRole('menuitem', {name: 'Portuguese (Brazil)'}).click();

	// configure rule

	await rulesBuilderPage.rulesTab.click();

	await rulesBuilderPage.addElementsButton.click();

	await rulesBuilderPage.selectConditionLeftFormField('Single Selection');

	await rulesBuilderPage.selectConditionOperator('Is Equal To');

	await rulesBuilderPage.selectConditionOperatorValueSource('Value');

	await rulesBuilderPage.selectConditionRightFormField('Option1');

	await rulesBuilderPage.selectAction('Autofill');

	await rulesBuilderPage.selectAutofillDataProvider(dataProviderName);

	await rulesBuilderPage.selectDataProviderOutput('Select from List');

	await rulesBuilderPage.saveButton.click();

	// assert the presence of auto-filled options in multiple selection field only when rule condition is met

	await formBuilderPage.formTab.click();

	const formPreviewPagePromise = page.waitForEvent('popup');

	await formBuilderPage.previewButton.click();

	const formPreviewPage = await formPreviewPagePromise;

	const triggerRule = async () => {
		await formPreviewPage.getByLabel('Option0').check();

		await formPreviewPage.getByPlaceholder('Choose Options').click();

		await expect(
			formPreviewPage.getByRole('option', {name: 'No results found'})
		).toBeVisible();

		await formPreviewPage.getByLabel('Option1').check();

		await formPreviewPage.getByPlaceholder('Choose Options').click();
	};

	await triggerRule();

	await expect(
		formPreviewPage.getByRole('option', {name: 'Afghanistan'})
	).toBeVisible();

	await expect(
		formPreviewPage.getByRole('option', {name: 'Aland Islands'})
	).toBeVisible();

	await formPreviewPage.keyboard.press('Escape');

	// assert the presence of auto-filled translated options in multiple selection field

	const formViewPage = new FormViewPage(formPreviewPage);

	await formViewPage.languageSelector.click();

	await formPreviewPage
		.getByRole('menuitem', {name: 'português-Brasil'})
		.click();

	await triggerRule();

	await expect(
		formPreviewPage.getByRole('option', {name: 'Afeganistão'})
	).toBeVisible();

	await expect(
		formPreviewPage.getByRole('option', {name: 'Alanda'})
	).toBeVisible();

	await formPreviewPage.close();
});

/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {formsPagesTest} from '../../../fixtures/formsPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {virtualInstancesPagesTest} from '../../../fixtures/virtualInstancesPagesTest';
import {liferayConfig} from '../../../liferay.config';
import {DataProviderPage} from '../../../pages/dynamic-data-mapping-form-web/DataProviderPage';
import {FormBuilderFieldSettingsSidePanelPage} from '../../../pages/dynamic-data-mapping-form-web/FormBuilderFieldSettingsSidePanelPage';
import {FormBuilderPage} from '../../../pages/dynamic-data-mapping-form-web/FormBuilderPage';
import {FormBuilderSidePanelPage} from '../../../pages/dynamic-data-mapping-form-web/FormBuilderSidePanelPage';
import {FormsPage} from '../../../pages/dynamic-data-mapping-form-web/FormsPage';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import performLogin from '../../../utils/performLogin';
import {deleteItems} from './utils/deleteItems';

export const test = mergeTests(
	loginTest(),
	formsPagesTest,
	virtualInstancesPagesTest
);

const COUNTRIES_API_URL = `${liferayConfig.environment.baseUrl}/api/jsonws/country/get-countries`;

const DEFAULT_VIRTUAL_INSTANCE_NAME = 'www.able.com';

const deleteAfterTestVirtualInstances = new Set<string>();
let hasDataProvider: boolean = false;

test.afterEach(
	async ({
		dataProvidersConfigurationPage,
		formsPage,
		virtualInstancesPage,
	}) => {
		await formsPage.goTo();

		await deleteItems(formsPage);

		if (hasDataProvider) {
			await formsPage.page.waitForLoadState();

			await formsPage.dataProvidersTab.click();

			await deleteItems(formsPage);

			await dataProvidersConfigurationPage.setAccessLocalNetwork(false);

			hasDataProvider = false;
		}

		for (const virtualInstanceName of deleteAfterTestVirtualInstances) {
			await virtualInstancesPage.deleteVirtualInstance(
				virtualInstanceName
			);

			deleteAfterTestVirtualInstances.delete(virtualInstanceName);
		}
	}
);

test.describe('Manage forms through submission page', () => {
	test('assert that data provider works on virtual instance', async ({
		browser,
		dataProvidersConfigurationPage,
		virtualInstancesPage,
	}) => {
		test.slow();

		hasDataProvider = true;

		await virtualInstancesPage.addNewVirtualInstance(
			DEFAULT_VIRTUAL_INSTANCE_NAME
		);

		deleteAfterTestVirtualInstances.add(DEFAULT_VIRTUAL_INSTANCE_NAME);

		const virtualInstancePage = await browser.newPage({
			baseURL: `http://${DEFAULT_VIRTUAL_INSTANCE_NAME}:${liferayConfig.environment.port}`,
		});

		await performLogin(
			virtualInstancePage,
			'test',
			'?p_p_id=com_liferay_login_web_portlet_LoginPortlet&' +
				'p_p_state=maximized',
			`@${DEFAULT_VIRTUAL_INSTANCE_NAME}.com`
		);

		const dataProviderName = 'DataProvider' + getRandomString();
		const formTitle = 'FormTitle' + getRandomInt();
		const virtualInstanceDataProviderPage = new DataProviderPage(
			virtualInstancePage
		);
		const virtualInstanceFormBuilderPage = new FormBuilderPage(
			virtualInstancePage
		);
		const virtualInstanceFormBuilderSidePanelPage =
			new FormBuilderSidePanelPage(virtualInstancePage);
		const virtualInstanceFormBuilderFieldSettingsSidePanelPage =
			new FormBuilderFieldSettingsSidePanelPage(virtualInstancePage);
		const virtualInstanceFormsPage = new FormsPage(virtualInstancePage);

		await dataProvidersConfigurationPage.setAccessLocalNetwork(true);

		await test.step('create a data provider with a country name list output', async () => {
			await virtualInstanceFormsPage.goTo();

			await virtualInstanceFormsPage.dataProvidersTab.click();

			await virtualInstanceDataProviderPage.addNewDataProviderLink.click();

			await virtualInstanceDataProviderPage.nameInputField.fill(
				dataProviderName
			);

			await virtualInstanceDataProviderPage.urlInputField.fill(
				COUNTRIES_API_URL
			);

			await virtualInstanceDataProviderPage.userNameInputField.fill(
				'test@liferay.com'
			);

			await virtualInstanceDataProviderPage.passwordInputField.fill(
				liferayConfig.environment.password
			);

			await virtualInstanceDataProviderPage.timeoutInputField.fill(
				'30000'
			);

			await virtualInstanceDataProviderPage.outputPathField.fill(
				'$..nameCurrentValue'
			);

			await virtualInstanceDataProviderPage.selectOutputType('List');

			await virtualInstanceDataProviderPage.outputLabelField.fill(
				'Country Name'
			);

			await virtualInstanceDataProviderPage.saveButton.click();

			await expect(
				virtualInstancePage.getByText('Success:Your request')
			).toBeVisible();
		});

		await test.step('create a form with a select from list field and publish it', async () => {
			await virtualInstancePage.setViewportSize({
				height: 1080,
				width: 1920,
			});

			await virtualInstanceFormBuilderPage.goToNew();

			await virtualInstanceFormBuilderPage.fillFormTitle(formTitle);

			await virtualInstanceFormBuilderSidePanelPage.addFieldByDoubleClick(
				'Select from List'
			);

			await virtualInstanceFormBuilderFieldSettingsSidePanelPage.selectCreateListSetting(
				'From Data Provider'
			);

			await expect(
				virtualInstanceFormBuilderFieldSettingsSidePanelPage.dataProviderSelect
			).toBeVisible();

			await virtualInstanceFormBuilderFieldSettingsSidePanelPage.selectDataProviderSetting(
				dataProviderName
			);

			await expect(
				virtualInstanceFormBuilderFieldSettingsSidePanelPage.dataProviderSelect
			).toHaveText(dataProviderName);

			await expect(
				virtualInstanceFormBuilderFieldSettingsSidePanelPage.outputParameterSelect
			).toBeVisible();

			await virtualInstanceFormBuilderFieldSettingsSidePanelPage.selectOutputParameterSetting(
				'Country Name'
			);

			await expect(
				virtualInstanceFormBuilderFieldSettingsSidePanelPage.outputParameterSelect
			).toHaveText('Country Name');

			await virtualInstancePage.waitForTimeout(2000);

			await virtualInstanceFormBuilderPage.clickPublishFormButton();
		});

		await test.step('go to form submission page, submit an entry and assert it is persisted', async () => {
			const formSubmissionURL =
				await virtualInstanceFormBuilderPage.getFormSubmissionURL();

			await virtualInstancePage.goto(formSubmissionURL, {
				waitUntil: 'networkidle',
			});

			await virtualInstancePage.getByLabel('Select from List').click();

			await virtualInstancePage
				.getByRole('option', {name: 'Brazil'})
				.click();

			await virtualInstancePage
				.getByRole('button', {name: 'Submit'})
				.click();

			await expect(
				virtualInstancePage.getByText(
					'Your information was successfully received. Thank you for filling out the form.'
				)
			).toBeVisible();

			await virtualInstanceFormsPage.goTo();

			await virtualInstanceFormsPage.openForm(formTitle);

			await virtualInstanceFormBuilderPage.entriesTab.click();

			await expect(
				virtualInstancePage.getByText('Brazil', {exact: true})
			).toBeVisible();
		});

		await virtualInstancePage.close();
	});

	test('can submit manual entry while using data provider autofill rule', async ({
		dataProviderPage,
		dataProvidersConfigurationPage,
		formBuilderPage,
		formBuilderSidePanelPage,
		formsPage,
		page,
		rulesBuilderPage,
	}) => {
		hasDataProvider = true;

		const dataProviderName = 'DataProvider' + getRandomString();
		const formTitle = 'FormTitle' + getRandomInt();
		const manualCountryName = 'Country' + getRandomInt();

		await dataProvidersConfigurationPage.setAccessLocalNetwork(true);

		await test.step('create a data provider with active input and country name output', async () => {
			await formsPage.goTo();

			await formsPage.dataProvidersTab.click();

			await dataProviderPage.addNewDataProviderLink.click();

			await dataProviderPage.nameInputField.fill(dataProviderName);

			await dataProviderPage.urlInputField.fill(
				`${COUNTRIES_API_URL}/active/{active}`
			);

			await dataProviderPage.userNameInputField.fill('test@liferay.com');

			await dataProviderPage.passwordInputField.fill(
				liferayConfig.environment.password
			);

			await dataProviderPage.timeoutInputField.fill('30000');

			await dataProviderPage.inputParameterField.fill('active');

			await dataProviderPage.selectInputType('Text');

			await dataProviderPage.inputLabelField.fill('Active');

			await dataProviderPage.outputPathField.fill(
				'$[0].nameCurrentValue'
			);

			await dataProviderPage.selectOutputType('Text');

			await dataProviderPage.outputLabelField.fill('Country');

			await dataProviderPage.saveButton.click();

			await expect(page.getByText('Success:Your request')).toBeVisible();
		});

		await test.step('create a form with active and country fields', async () => {
			await formBuilderPage.goToNew();

			await formBuilderPage.fillFormTitle(formTitle);

			await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

			await formBuilderSidePanelPage.label.fill('Active');

			await formBuilderSidePanelPage.advancedTab.click();

			await formBuilderSidePanelPage.fieldReference.fill('Active');

			await formBuilderSidePanelPage.predefinedValueField.fill('true');

			await formBuilderSidePanelPage.backButton.click();

			await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

			await formBuilderSidePanelPage.label.fill('Country');

			await formBuilderSidePanelPage.advancedTab.click();

			await formBuilderSidePanelPage.fieldReference.fill('Country');
		});

		await test.step('create a rule to autofill fields with data from provider', async () => {
			await rulesBuilderPage.rulesTab.click();

			await rulesBuilderPage.addElementsButton.click();

			await rulesBuilderPage.selectConditionLeftFormField('Active');

			await rulesBuilderPage.selectConditionOperator('Is Not Empty');

			await rulesBuilderPage.selectAction('Autofill');

			await rulesBuilderPage.selectAutofillDataProvider(dataProviderName);

			await rulesBuilderPage.selectDataProviderInput('Active');

			await rulesBuilderPage.selectDataProviderOutput('Country');

			await rulesBuilderPage.saveButton.click();
		});

		await test.step('go to form submission, override autofilled value and assert that it is persisted', async () => {
			await formBuilderPage.formTab.click();

			await formBuilderPage.clickPublishFormButton();

			const formSubmissionURL =
				await formBuilderPage.getFormSubmissionURL();

			await page.goto(formSubmissionURL, {waitUntil: 'networkidle'});

			await expect(page.getByLabel('Country')).not.toHaveValue('');

			await page.getByLabel('Country').fill(manualCountryName);

			await page.getByRole('button', {name: 'Submit'}).click();

			await expect(
				page.getByText(
					'Your information was successfully received. Thank you for filling out the form.'
				)
			).toBeVisible();

			await formsPage.goTo();

			await formsPage.openForm(formTitle);

			await formBuilderPage.entriesTab.click();

			await expect(page.getByText(manualCountryName)).toBeVisible();
		});
	});

	test('verify that a Form can require CAPTCHA before being accessed', async ({
		formBuilderPage,
		formBuilderSidePanelPage,
		formsPage,
		page,
	}) => {
		await formsPage.goTo();

		await test.step('create a form containing a text field and CAPTCHA validation', async () => {
			await formsPage.newFormButton.first().click();

			await formBuilderPage.fillFormTitle('Form' + getRandomInt());

			await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

			await formBuilderPage.formSettingsButton.click();

			await formBuilderPage.requireCaptchaToggle.click();

			await formBuilderPage.formSettingsDoneButton.click();

			await formBuilderPage.clickPublishFormButton();
		});

		await test.step('navigate to the form page and assert that CAPTCHA is required', async () => {
			const formSubmissionURL =
				await formBuilderPage.getFormSubmissionURL();

			await page.goto(formSubmissionURL, {waitUntil: 'networkidle'});

			await page
				.getByLabel('Text', {exact: true})
				.fill('Text field value');

			const submitButton = page.getByRole('button', {name: 'Submit'});

			await submitButton.click();

			await expect(submitButton).toBeDisabled();

			await expect(
				page.getByText('The Text Verification field is required.')
			).toBeVisible();

			await expect(submitButton).toBeEnabled();

			await page.getByRole('textbox').last().fill('1');

			await page.getByRole('textbox').last().blur();

			await expect(
				page.getByText('The Text Verification field is required.')
			).not.toBeVisible();

			await submitButton.click();

			await expect(
				page.getByText('Close Error:Text verification')
			).toBeVisible();
		});
	});
});

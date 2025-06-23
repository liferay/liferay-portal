/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinitionAPI,
	ObjectField,
	ObjectFieldAPI,
	ObjectValidationRuleAPI,
} from '@liferay/object-admin-rest-client-js';
import {Locator, Page, expect, mergeTests} from '@playwright/test';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../fixtures/displayPageTemplatesPagesTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {masterPagesPagesTest} from '../../../fixtures/masterPagesPagesTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageManagementSiteTest} from '../../../fixtures/pageManagementSiteTest';
import {PageEditorPage} from '../../../pages/layout-content-page-editor-web/PageEditorPage';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {expandSection} from '../../../utils/expandSection';
import fillAndClickOutside from '../../../utils/fillAndClickOutside';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {getObjectERC} from '../../setup/page-management-site/main/utils/getObjectERC';
import {goToObjectEntity} from '../../setup/page-management-site/main/utils/goToObjectEntity';
import chooseFileFromDocumentLibrary from './utils/chooseFileFromDocumentLibrary';
import getContainerDefinition from './utils/getContainerDefinition';
import getFormContainerDefinition from './utils/getFormContainerDefinition';
import getFragmentDefinition from './utils/getFragmentDefinition';
import getPageDefinition from './utils/getPageDefinition';
import getWidgetDefinition from './utils/getWidgetDefinition';

const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	documentLibraryPagesTest,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
		'LPD-17564': {enabled: true},
		'LPD-21926': {enabled: true},
		'LPD-32050': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	masterPagesPagesTest,
	objectPagesTest,
	pageEditorPagesTest,
	pageManagementSiteTest
);

const testWithCKEditor4 = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
		'LPD-21926': {enabled: true},
		'LPD-32050': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	pageManagementSiteTest
);

test.describe('Form Configuration', () => {
	test(
		'Can add custom translations for success message',
		{
			tag: ['@LPS-155529', '@LPS-188036'],
		},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Create a page with a Form fragment and a widget

			const formId = getRandomString();

			const formDefinition = getFormContainerDefinition({
				id: formId,
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to edit mode and change form configuration

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			await pageEditorPage.mapFormFragment(formId, 'Lemon', [
				'Lemon Size',
				'Lemon Basket to Lemons',
			]);

			await pageEditorPage.selectFragment(formId);

			await page
				.locator('.panel', {hasText: 'Actions After Submit'})
				.getByLabel('Success Action', {exact: true})
				.selectOption({label: 'Show Embedded Message'});

			await pageEditorPage.switchLanguage('es-ES');

			await page
				.getByLabel('Embedded Message', {exact: true})
				.fill('Estamos muy agradecidos de recibir su formulario.');

			// Preview message

			await page
				.getByLabel('Preview Success Message', {exact: true})
				.click();

			await expect(
				page.getByText(
					'Estamos muy agradecidos de recibir su formulario.'
				)
			).toBeVisible();

			await pageEditorPage.publishPage();

			// Go to view mode in spanish language

			await page.goto(
				`/es-es/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			// Submit form

			await page.getByRole('button', {name: 'Submit'}).click();

			// Assert spanish success message

			await page
				.getByText('Estamos muy agradecidos de recibir su formulario.')
				.waitFor();

			// Go to view mode in english language

			await page.goto(
				`/en/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			// Submit form

			await page.getByRole('button', {name: 'Submit'}).click();

			// Assert english success message

			await page
				.getByText(
					'Thank you. Your information was successfully received.'
				)
				.waitFor();
		}
	);

	test(
		'Show success message only one time',
		{
			tag: ['@LPD-37435', '@LPS-188036'],
		},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Create a page with a Form fragment and a widget

			const formId = getRandomString();

			const formDefinition = getFormContainerDefinition({
				id: formId,
			});

			const widgetId = getRandomString();

			const widgetDefinition = getWidgetDefinition({
				id: widgetId,
				widgetName:
					'com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet',
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					formDefinition,
					widgetDefinition,
				]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to edit mode and change form configuration

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			await pageEditorPage.mapFormFragment(formId, 'Lemon', [
				'Lemon Size',
				'Lemon Basket to Lemons',
				'Lemon Weight',
			]);

			await pageEditorPage.selectFragment(formId);

			await page
				.locator('.panel', {hasText: 'Actions After Submit'})
				.getByLabel('Success Action', {exact: true})
				.selectOption({label: 'Stay in Page'});

			await page
				.getByLabel('Show Notification After Submit', {exact: true})
				.check();

			await page
				.getByLabel('Success Notification Text', {exact: true})
				.fill('Request received correctly');

			// add a Tabs fragment that includes drop-zone

			await pageEditorPage.addFragment('Basic Components', 'Tabs');

			await pageEditorPage.publishPage();

			// Go to view mode and check picklist values

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			const lemonWeightInput = page.getByRole('spinbutton', {
				name: 'Lemon Weight',
			});

			await lemonWeightInput.fill('100');

			// Submit form

			await page.getByRole('button', {name: 'Submit'}).click();

			// Wait for the first alert

			await page.getByText('Request received correctly').waitFor();

			// Verify that the first alert disappears without any more alerts being displayed

			let moreAlertsAppear = false;
			let firstAlertDisappears = false;

			await expect(async () => {
				const alerts = await page
					.getByText('Request received correctly')
					.all();

				if (alerts.length > 1) {
					moreAlertsAppear = true;
				}
				else if (!alerts.length) {
					firstAlertDisappears = true;
				}

				expect(firstAlertDisappears).toBe(true);
				expect(moreAlertsAppear).toBe(false);
			}).toPass();
		}
	);

	test(
		'Success notification with toast message must be compatible with go to entry display page redirect option',
		{
			tag: '@LPS-188036',
		},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Create a default display page for lemon object

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('Lemon')
				)
			).body;

			const className =
				await apiHelpers.jsonWebServicesClassName.fetchClassName(
					objectDefinitionClassName
				);

			const displayPage =
				await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
					{
						classNameId: className.classNameId,
						groupId: pageManagementSite.id,
						name: getRandomString(),
					}
				);

			await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.markAsDefaultDisplayPageLayoutPageTemplateEntry(
				{
					layoutPageTemplateEntryId:
						displayPage.layoutPageTemplateEntryId,
				}
			);

			// Create a page with a form fragment

			const formId = getRandomString();

			const textDefinition = getFragmentDefinition({
				fragmentConfig: {
					inputFieldId: 'ObjectField_lemonSize',
				},
				id: getRandomString(),
				key: 'INPUTS-text-input',
			});

			const submitFragmentDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'INPUTS-submit-button',
			});

			const formDefinition = getFormContainerDefinition({
				id: formId,
				objectDefinitionClassName,
				pageElements: [textDefinition, submitFragmentDefinition],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to edit mode and change form configuration

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Success Action',
				fragmentId: formId,
				tab: 'General',
				value: 'Go to Entry Display Page',
			});

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Display Page',
				fragmentId: formId,
				tab: 'General',
				value: 'Default',
			});

			await page
				.getByLabel('Show Notification After Submit', {exact: true})
				.check();

			await page
				.getByLabel('Success Notification Text', {exact: true})
				.fill('Request received correctly');

			await pageEditorPage.publishPage();

			// Go to view mode

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			// Assert form is not redirected if there are validation errors

			const input = page.getByRole('textbox', {name: 'Lemon Size'});

			await input.click();

			await page.keyboard.type('a'.repeat(290));

			await page.getByText('Submit', {exact: true}).click();

			await expect(
				page.getByText(
					'Value exceeds maximum length of 280 for field Lemon Size.'
				)
			).toBeVisible();

			// Clear input and assert success notification

			await input.clear();

			await page.getByText('Submit', {exact: true}).click();

			await waitForAlert(page, 'Request received correctly');

			// Delete the display page

			await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.deleteLayoutPageTemplateEntry(
				{
					layoutPageTemplateEntryId:
						displayPage.layoutPageTemplateEntryId,
				}
			);
		}
	);

	test(
		'Form Fragment redirect to correct success page',
		{tag: '@LPS-155529'},
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

			// Create a success page with a heading fragment

			const successLayoutTitle = getRandomString();

			const succesLayout =
				await apiHelpers.headlessDelivery.createSitePage({
					pageDefinition: getPageDefinition([
						getFragmentDefinition({
							fragmentFields: [
								{
									id: 'element-text',
									value: {
										text: {
											value_i18n: {
												en_US: 'Success Page',
											},
										},
									},
								},
							],
							id: getRandomString(),
							key: 'BASIC_COMPONENT-heading',
						}),
					]),
					siteId: pageManagementSite.id,
					title: successLayoutTitle,
				});

			// Go to edit mode and map the form to Lemon object

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			await pageEditorPage.mapFormFragment(formId, 'Lemon', [
				'Lemon Weight',
			]);

			// Change the success action to go to the success page

			await pageEditorPage.selectFragment(formId);

			await pageEditorPage.changeConfiguration({
				fieldLabel: 'Success Action',
				tab: 'General',
				value: 'Go to Page',
			});

			const layoutTreeItem = page
				.frameLocator('iframe[title="Select"]')
				.getByLabel(successLayoutTitle);

			await clickAndExpectToBeVisible({
				target: layoutTreeItem,
				timeout: 3000,
				trigger: page.getByLabel('Select Page', {exact: true}),
			});

			await clickAndExpectToBeHidden({
				target: page.locator('.modal-dialog'),
				trigger: layoutTreeItem,
			});

			await pageEditorPage.publishPage();

			// Go to view mode and submit form

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			const lemonWeightInput = page.getByRole('spinbutton', {
				name: 'Lemon Weight',
			});

			await lemonWeightInput.fill('100');

			await page.getByText('Submit', {exact: true}).click();

			// Assert that the success page is displayed

			await expect(page.getByText('Success Page')).toBeVisible();

			// Delete the success page

			await apiHelpers.jsonWebServicesLayout.deleteLayout(
				succesLayout.id
			);

			// Publish the form again and check that the default message is displayed

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await lemonWeightInput.fill('100');

			await page.getByText('Submit', {exact: true}).click();

			await expect(
				page.getByText(
					'Thank you. Your information was successfully received.'
				)
			).toBeVisible();
		}
	);
});

test.describe('Captcha Fragment', () => {
	test(
		'The user could see an error message when submit a form with wrong captcha verification code',
		{
			tag: ['@LPS-151402', '@LPS-155168'],
		},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Create a page with a form fragment with a captcha fragment

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('Lemon')
				)
			).body;

			const captchaDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'INPUTS-captcha',
			});

			const submitFragmentDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'INPUTS-submit-button',
			});

			const formDefinition = getFormContainerDefinition({
				id: getRandomString(),
				objectDefinitionClassName,
				pageElements: [captchaDefinition, submitFragmentDefinition],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to edit mode and assert captcha is disabled

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			await expect(page.locator('.form-input-captcha')).toHaveAttribute(
				'disabled'
			);

			// Go to view mode and assert error message with wrong captcha verification code when submit the form

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await page.getByText('Submit', {exact: true}).click();

			await expect(
				page.getByText('CAPTCHA verification failed. Please try again.')
			).toBeVisible();
		}
	);
});

test.describe('Checkbox Fragment', () => {
	test(
		'The page designer can configure checkbox fragment',
		{
			tag: '@LPS-151157',
		},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Create a page with a form fragment with a checkbox fragment

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('All Fields')
				)
			).body;

			const checkboxId = getRandomString();

			const checkboxDefinition = getFragmentDefinition({
				fragmentConfig: {
					inputFieldId: 'ObjectField_boolean',
				},
				id: checkboxId,
				key: 'INPUTS-checkbox',
			});

			const formDefinition = getFormContainerDefinition({
				id: getRandomString(),
				objectDefinitionClassName,
				pageElements: [checkboxDefinition],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to edit mode

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			// Change label

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Label',
				fragmentId: checkboxId,
				tab: 'General',
				value: 'Are you a fun of Stephen Curry?',
			});

			const checkboxInput = page.locator('.forms-checkbox');

			await expect(
				checkboxInput.getByText('Are you a fun of Stephen Curry?')
			).not.toHaveClass(/sr-only/);

			// Hide label

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Show Label',
				fragmentId: checkboxId,
				tab: 'General',
				value: false,
			});

			await expect(
				checkboxInput.getByText('Are you a fun of Stephen Curry?')
			).toHaveClass(/sr-only/);

			// Show help text

			await expect(checkboxInput).not.toContainText(
				/Add your help text here./
			);

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Show Help Text',
				fragmentId: checkboxId,
				tab: 'General',
				value: true,
			});

			await expect(checkboxInput).toContainText(
				/Add your help text here./
			);
		}
	);

	test(
		'User should see error message below checkbox fragment',
		{
			tag: '@LPS-182728',
		},
		async ({apiHelpers, page, pageManagementSite}) => {

			// Adds checkbox validation

			const objectValidationRuleAPIClient =
				await apiHelpers.buildRestClient(ObjectValidationRuleAPI);

			const {body: objectValidationRule} =
				await objectValidationRuleAPIClient.postObjectDefinitionByExternalReferenceCodeObjectValidationRule(
					getObjectERC('All Fields'),
					{
						active: true,
						engine: 'ddm',
						engineLabel: 'Expression Builder',
						errorLabel: {
							en_US: 'Please accept the terms of use and Privacy Policy.',
						},
						name: {
							en_US: 'Checkbox Validation',
						},
						objectValidationRuleSettings: [
							{
								name: 'outputObjectFieldExternalReferenceCode',
								value: 'boolean-erc',
							} as any,
						],
						outputType: 'partialValidation',
						script: 'boolean == true',
						system: false,
					}
				);

			// Create a page with a form fragment with a checkbox fragment

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('All Fields')
				)
			).body;

			const checkboxId = getRandomString();

			const checkboxDefinition = getFragmentDefinition({
				fragmentConfig: {
					inputFieldId: 'ObjectField_boolean',
				},
				id: checkboxId,
				key: 'INPUTS-checkbox',
			});

			const submitFragmentDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'INPUTS-submit-button',
			});

			const formDefinition = getFormContainerDefinition({
				id: getRandomString(),
				objectDefinitionClassName,
				pageElements: [checkboxDefinition, submitFragmentDefinition],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to view mode

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			// Assert error message

			await page.getByText('Submit', {exact: true}).click();

			await expect(page.locator('.forms-checkbox')).toContainText(
				'Please accept the terms of use and Privacy Policy.'
			);

			// Delete validation

			await objectValidationRuleAPIClient.deleteObjectValidationRule(
				objectValidationRule.id
			);
		}
	);
});

test.describe('Date Fragment', () => {
	test(
		'The page designer could map date field to date fragment',
		{
			tag: ['@LPS-151158', '@LPS-155502'],
		},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Create a page with a form fragment with a date fragment

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('All Fields')
				)
			).body;

			const dateId = getRandomString();

			const dateDefinition = getFragmentDefinition({
				fragmentConfig: {
					inputFieldId: 'ObjectField_date',
				},
				id: dateId,
				key: 'INPUTS-date-input',
			});

			const formDefinition = getFormContainerDefinition({
				id: getRandomString(),
				objectDefinitionClassName,
				pageElements: [dateDefinition],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to edit mode

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			// Change label

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Label',
				fragmentId: dateId,
				tab: 'General',
				value: 'Expiration Date',
			});

			const dateInput = page.locator('.date-input');

			await expect(
				dateInput.getByText('Expiration Date')
			).not.toHaveClass(/sr-only/);

			// Hide label

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Show Label',
				fragmentId: dateId,
				tab: 'General',
				value: false,
			});

			await expect(dateInput.getByText('Expiration Date')).toHaveClass(
				/sr-only/
			);

			// Show help text

			await expect(dateInput).not.toContainText(
				/Add your help text here./
			);

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Show Help Text',
				fragmentId: dateId,
				tab: 'General',
				value: true,
			});

			await expect(dateInput).toContainText(/Add your help text here./);
		}
	);

	test(
		'User should see error message below date fragment',
		{
			tag: '@LPS-182728',
		},
		async ({apiHelpers, page, pageManagementSite}) => {

			// Adds date validation

			const objectValidationRuleAPIClient =
				await apiHelpers.buildRestClient(ObjectValidationRuleAPI);

			const {body: objectValidationRule} =
				await objectValidationRuleAPIClient.postObjectDefinitionByExternalReferenceCodeObjectValidationRule(
					getObjectERC('All Fields'),
					{
						active: true,
						engine: 'ddm',
						engineLabel: 'Expression Builder',
						errorLabel: {
							en_US: 'Please enter a valid date.',
						},
						name: {
							en_US: 'Date Validation',
						},
						objectValidationRuleSettings: [
							{
								name: 'outputObjectFieldExternalReferenceCode',
								value: 'date-erc',
							} as any,
						],
						outputType: 'partialValidation',
						script: "futureDates(date, '2022-06-01')",
						system: false,
					}
				);

			// Create a page with a form fragment with a date fragment

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('All Fields')
				)
			).body;

			const dateId = getRandomString();

			const dateDefinition = getFragmentDefinition({
				fragmentConfig: {
					inputFieldId: 'ObjectField_date',
				},
				id: dateId,
				key: 'INPUTS-date-input',
			});

			const submitFragmentDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'INPUTS-submit-button',
			});

			const formDefinition = getFormContainerDefinition({
				id: getRandomString(),
				objectDefinitionClassName,
				pageElements: [dateDefinition, submitFragmentDefinition],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to view mode

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			// Edit date

			await page.locator('input[name="date"]').click();

			await page.keyboard.type('07/11/2020');

			await page.locator('body').click();

			await page.getByText('Submit', {exact: true}).click();

			// Assert error message

			await expect(page.locator('.date-input')).toContainText(
				'Please enter a valid date.'
			);

			// Delete validation

			const objectvalidationRuleAPIClient =
				await apiHelpers.buildRestClient(ObjectValidationRuleAPI);

			await objectvalidationRuleAPIClient.deleteObjectValidationRule(
				objectValidationRule.id
			);
		}
	);
});

test.describe('Date and Time Fragment', () => {
	test(
		'The page designer could map date and time field to date and time fragment',
		{
			tag: '@LPS-191312',
		},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Create a page with a form fragment with a date and time fragment

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('All Fields')
				)
			).body;

			const dateId = getRandomString();

			const dateDefinition = getFragmentDefinition({
				fragmentConfig: {
					inputFieldId: 'ObjectField_date',
				},
				id: dateId,
				key: 'INPUTS-date-input',
			});

			const dateTimeId = getRandomString();

			const dateTimeDefinition = getFragmentDefinition({
				fragmentConfig: {
					inputFieldId: 'ObjectField_dateAndTime',
				},
				id: dateTimeId,
				key: 'INPUTS-date-time-input',
			});

			const textDefinition = getFragmentDefinition({
				fragmentConfig: {
					inputFieldId: 'ObjectField_text',
				},
				id: getRandomString(),
				key: 'INPUTS-text-input',
			});

			const submitFragmentDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'INPUTS-submit-button',
			});

			const formDefinition = getFormContainerDefinition({
				id: getRandomString(),
				objectDefinitionClassName,
				pageElements: [
					dateDefinition,
					dateTimeDefinition,
					textDefinition,
					submitFragmentDefinition,
				],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to edit mode and change label

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Label',
				fragmentId: dateTimeId,
				tab: 'General',
				value: 'Clock',
			});

			await pageEditorPage.publishPage();

			// Go to view mode

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			// Edit date

			await page.locator('input[name="date"]').click();

			await page.keyboard.type('01/07/2023');

			// Edit date and time

			await page.locator('input[name="dateAndTime"]').click();

			await page.keyboard.type('10/10/2022');
			await page.keyboard.press('ArrowRight');
			await page.keyboard.type('10:10');
			await page.keyboard.press('ArrowRight');
			await page.keyboard.type('AM');

			await fillAndClickOutside(
				page,
				page.getByLabel('Text'),
				'Date And Time'
			);

			await page.getByText('Submit', {exact: true}).click();

			// Assert success message

			await expect(
				page.getByText(
					'Thank you. Your information was successfully received.'
				)
			).toBeVisible();

			// Go to custom object admin

			await goToObjectEntity({
				entityName: 'All Fields',
				page,
			});

			// Check the date and time of the object entry

			const row = page.locator('.fds tbody tr').first();

			await expect(row).toContainText('Oct 10, 2022, 10:10:00 AM');
		}
	);
});

test.describe('File Upload Fragment', () => {
	test(
		"Cannot clear object entry's mandatory attached file via associated display page",
		{
			tag: '@LPS-191357',
		},
		async ({
			apiHelpers,
			displayPageTemplatesPage,
			page,
			pageEditorPage,
			pageManagementSite,
		}) => {

			// Create a Display page for the all fields object

			await displayPageTemplatesPage.goto(
				pageManagementSite.friendlyUrlPath
			);

			const displayPageTemplateName = getRandomString();

			await displayPageTemplatesPage.createTemplate({
				contentType: 'All Fields',
				name: displayPageTemplateName,
			});

			await displayPageTemplatesPage.editTemplate(
				displayPageTemplateName
			);

			// Add a Form Container and map it to file upload field

			await pageEditorPage.addFragment(
				'Form Components',
				'Form Container'
			);

			const fragmentId =
				await pageEditorPage.getFragmentId('Form Container');

			await pageEditorPage.mapFormFragment(
				fragmentId,
				'All Fields (Default)',
				['Computer File']
			);

			// Mark upload file as required

			const dptFileUploadId =
				await pageEditorPage.getFragmentId('File Upload');

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Mark as Required',
				fragmentId: dptFileUploadId,
				tab: 'General',
				value: true,
			});

			await displayPageTemplatesPage.publishTemplate();

			// Mark display page as default

			await displayPageTemplatesPage.goto(
				pageManagementSite.friendlyUrlPath
			);

			await displayPageTemplatesPage.markAsDefault(
				displayPageTemplateName
			);

			// Create a page with a form fragment with a file upload fragment

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('All Fields')
				)
			).body;

			const fileUploadId = getRandomString();

			const fileUploadDefinition = getFragmentDefinition({
				fragmentConfig: {
					inputFieldId: 'ObjectField_fileUpload',
				},
				id: fileUploadId,
				key: 'INPUTS-file-upload',
			});

			const submitFragmentDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'INPUTS-submit-button',
			});

			const formId = getRandomString();

			const formDefinition = getFormContainerDefinition({
				id: formId,
				objectDefinitionClassName,
				pageElements: [fileUploadDefinition, submitFragmentDefinition],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to edit mode

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			// Mark upload file as required

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Mark as Required',
				fragmentId: fileUploadId,
				tab: 'General',
				value: true,
			});

			// Change redirect to display page after submit

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Success Action',
				fragmentId: formId,
				panel: 'Actions After Submit',
				tab: 'General',
				value: 'Go to Entry Display Page',
			});

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Display Page',
				fragmentId: formId,
				tab: 'General',
				value: 'Default',
			});

			await pageEditorPage.publishPage();

			// Go to view mode

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			// Select file from computer

			const fileChooserPromise = page.waitForEvent('filechooser');

			const fileUploadInput = page.locator('.file-upload');

			await fileUploadInput
				.getByText('Select File', {exact: true})
				.click();

			const fileChooser = await fileChooserPromise;

			await fileChooser.setFiles(
				path.join(__dirname, '/dependencies/file_upload_image_1.jpg')
			);

			await expect(
				fileUploadInput.getByText('file_upload_image_1')
			).toBeVisible();

			// Submit form

			await page.getByRole('button', {name: 'Submit'}).click();

			// Assert form is submitted and user is redirected to display page

			await expect(page.getByText('Computer File')).toBeVisible();

			await expect(
				fileUploadInput.getByText('file_upload_image_1')
			).toBeVisible();

			// Assert form is not submitted if mandatory field is cleared

			await page.locator('[id*="file-upload-remove-button"]').click();

			await page.getByRole('button', {name: 'Submit'}).click();

			await expect(page.getByText('Computer File')).toBeVisible();

			await expect(
				page.getByText(
					'Thank you. Your information was successfully received.'
				)
			).not.toBeVisible();

			// Add another file and submit form

			const displayPageFileChooserPromise =
				page.waitForEvent('filechooser');

			await fileUploadInput
				.getByText('Select File', {exact: true})
				.click();

			const displayPageFileChooser = await displayPageFileChooserPromise;

			await displayPageFileChooser.setFiles(
				path.join(__dirname, '/dependencies/file_upload_image_2.jpg')
			);

			await page.getByRole('button', {name: 'Submit'}).click();

			// Check that is edited correctly

			await expect(
				page.getByText(
					'Thank you. Your information was successfully received.'
				)
			).toBeVisible();

			await page.reload();

			await expect(
				fileUploadInput.getByText('file_upload_image_2')
			).toBeVisible();
		}
	);

	test(
		'Configure fragment mapped to File Upload field',
		{
			tag: '@LPS-157806',
		},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Create a page with a form fragment with a file upload fragment

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('All Fields')
				)
			).body;

			const fileUploadId = getRandomString();

			const fileUploadDefinition = getFragmentDefinition({
				fragmentConfig: {
					inputFieldId: 'ObjectField_fileUpload',
				},
				id: fileUploadId,
				key: 'INPUTS-file-upload',
			});

			const submitFragmentDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'INPUTS-submit-button',
			});

			const formDefinition = getFormContainerDefinition({
				id: getRandomString(),
				objectDefinitionClassName,
				pageElements: [fileUploadDefinition, submitFragmentDefinition],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to edit mode

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			// Assert default configuration values

			await pageEditorPage.selectFragment(fileUploadId);

			await expect(
				page.getByLabel('Show Label', {exact: true})
			).toBeChecked();

			await expect(
				page.getByLabel('Show Help Text', {exact: true})
			).not.toBeChecked();

			await expect(
				page.getByLabel('Help Text', {exact: true})
			).toHaveValue('Add your help text here.');

			await expect(
				page.getByLabel('Show Supported File Info', {exact: true})
			).toBeChecked();

			const fileUploadInput = page.locator('.file-upload');

			await expect(
				fileUploadInput.getByText(
					'Upload a .jpeg,.jpg,.pdf,.png no larger than 2 MB.',
					{exact: true}
				)
			).toBeVisible();

			// Change button text

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Button Text',
				fragmentId: fileUploadId,
				tab: 'General',
				value: 'Upload',
			});

			await expect(
				fileUploadInput.getByText('Upload', {exact: true})
			).toBeVisible();

			await pageEditorPage.publishPage();

			// Go to view mode

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await expect(
				fileUploadInput.getByRole('button', {name: 'Upload'})
			).toBeVisible();
		}
	);

	test(
		'Upload file from computer',
		{
			tag: '@LPS-155170',
		},
		async ({apiHelpers, documentLibraryPage, page, pageManagementSite}) => {

			// Create a page with a form fragment with a file upload fragment

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('All Fields')
				)
			).body;

			const fileUploadId = getRandomString();

			const fileUploadDefinition = getFragmentDefinition({
				fragmentConfig: {
					inputFieldId: 'ObjectField_fileUpload',
				},
				id: fileUploadId,
				key: 'INPUTS-file-upload',
			});

			const submitFragmentDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'INPUTS-submit-button',
			});

			const formDefinition = getFormContainerDefinition({
				id: getRandomString(),
				objectDefinitionClassName,
				pageElements: [fileUploadDefinition, submitFragmentDefinition],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to view mode

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			// Select file from computer

			const fileChooserPromise = page.waitForEvent('filechooser');

			const fileUploadInput = page.locator('.file-upload');

			await fileUploadInput
				.getByText('Select File', {exact: true})
				.click();

			const fileChooser = await fileChooserPromise;

			await fileChooser.setFiles(
				path.join(__dirname, '/dependencies/file_upload_image_2.jpg')
			);

			await expect(
				fileUploadInput.getByText('file_upload_image_2')
			).toBeVisible();

			// Submit form

			await page.getByRole('button', {name: 'Submit'}).click();

			// Assert document is added to document library

			await documentLibraryPage.goto(pageManagementSite.friendlyUrlPath);

			await page.getByRole('link', {name: 'FileUpload'}).click();

			await expect(
				page.getByRole('link', {
					exact: true,
					name: 'file_upload_image_2',
				})
			).toBeVisible();
		}
	);

	test(
		'Upload file from document library',
		{
			tag: '@LPS-194129',
		},
		async ({apiHelpers, page, pageManagementSite}) => {

			// Create a page with a form fragment with a file upload fragment

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('All Fields')
				)
			).body;

			const fileUploadId = getRandomString();

			const fileUploadDefinition = getFragmentDefinition({
				fragmentConfig: {
					inputFieldId: 'ObjectField_dlFileUpload',
				},
				id: fileUploadId,
				key: 'INPUTS-file-upload',
			});

			const submitFragmentDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'INPUTS-submit-button',
			});

			const formDefinition = getFormContainerDefinition({
				id: getRandomString(),
				objectDefinitionClassName,
				pageElements: [fileUploadDefinition, submitFragmentDefinition],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to view mode

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			// Select file from document library

			const fileUploadInput = page.locator('.file-upload');

			await fileUploadInput
				.getByText('Select File', {exact: true})
				.click();

			// Assert jpg files are not present

			const dialogIFrame = page.frameLocator('iframe');

			await expect(
				dialogIFrame.getByText(
					'Drag & Drop Your Files or Browse to Upload'
				)
			).toBeVisible();

			await expect(
				dialogIFrame.getByText('balinese.jpg')
			).not.toBeVisible();
		}
	);

	test(
		'View error messages from File Upload field',
		{
			tag: '@LPS-151402',
		},
		async ({apiHelpers, page, pageManagementSite}) => {

			// Create a page with a form fragment with a file upload fragment

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('All Fields')
				)
			).body;

			const fileUploadId = getRandomString();

			const fileUploadDefinition = getFragmentDefinition({
				fragmentConfig: {
					inputFieldId: 'ObjectField_fileUpload',
				},
				id: fileUploadId,
				key: 'INPUTS-file-upload',
			});

			const submitFragmentDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'INPUTS-submit-button',
			});

			const formDefinition = getFormContainerDefinition({
				id: getRandomString(),
				objectDefinitionClassName,
				pageElements: [fileUploadDefinition, submitFragmentDefinition],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to view mode

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			// Select file from computer

			const fileChooserPromise = page.waitForEvent('filechooser');

			const fileUploadInput = page.locator('.file-upload');

			await fileUploadInput
				.getByText('Select File', {exact: true})
				.click();

			const fileChooser = await fileChooserPromise;

			await fileChooser.setFiles(
				path.join(__dirname, '/dependencies/high_resolution_image.jpg')
			);

			await expect(
				fileUploadInput.getByText('high_resolution_image')
			).toBeVisible();

			// Submit form

			await page.getByRole('button', {name: 'Submit'}).click();

			// Assert error message

			await expect(
				page.getByText(
					'File size is larger than the allowed maximum upload size (2 MB).'
				)
			).toBeVisible();
		}
	);
});

test.describe('Friendly URL Fragment', () => {
	test(
		'Check the mapping field',
		{
			tag: '@LPD-52418',
		},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Create an object with the friendly url field enabled

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {body: objectDefinition} =
				await objectDefinitionAPIClient.postObjectDefinition({
					active: true,
					enableFriendlyURLCustomization: true,
					enableLocalization: true,
					externalReferenceCode: 'erc',
					label: {
						en_US: 'Test',
					},
					name: 'Test',
					objectFields: [
						{
							DBType: 'String',
							businessType: 'Text',
							externalReferenceCode: 'text-erc',
							indexed: true,
							indexedAsKeyword: true,
							label: {
								en_US: 'Text',
							},
							localized: true,
							name: 'text',
							required: false,
						},
					],
					pluralLabel: {
						en_US: 'Tests',
					},
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

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			// Map the form to the All Field object and add only the Friendly URL field

			await pageEditorPage.mapFormFragment(formId, 'Test', [
				'Friendly URL',
			]);

			await pageEditorPage.selectFragment(
				await pageEditorPage.getFragmentId('Friendly URL')
			);

			// Check if the mapping field

			await expect(
				page.getByRole('combobox', {name: 'Field'})
			).toHaveValue('ObjectEntry_objectEntryFriendlyURL');

			// Change its label

			await pageEditorPage.changeConfiguration({
				fieldLabel: 'Label',
				tab: 'General',
				value: 'My new friendly url label',
			});

			await expect(
				page.getByLabel('My new friendly url label')
			).toBeAttached();
		}
	);
});

// Remove when the feature flag LPD-11235 is removed

testWithCKEditor4.describe('Form Localization with CKEditor 4', () => {
	testWithCKEditor4(
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

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

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

	testWithCKEditor4(
		'Set unlocalized fields to disabled or readonly when changing language with CKEditor 4',
		{tag: '@LPD-37927'},
		async ({apiHelpers, page, pageEditorPage, site}) => {

			// Create object definition

			const listTypeDefinition =
				await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

			for (const option of ['Spain', 'Italy']) {
				await apiHelpers.listTypeAdmin.postListTypeEntry(
					listTypeDefinition.externalReferenceCode,
					option
				);
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

			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			// Change the translation language to spanish

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('option').filter({hasText: 'es-ES'}),
				trigger: page.getByLabel(
					'Select a language, current language:'
				),
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
				page.getByLabel(
					'Ideal Temperature (ºC) field cannot be localized'
				)
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

			await expect(
				page.locator('.rich-text-input--disabled')
			).toBeAttached();

			await expect(
				page.getByRole('textbox', {name: 'Scientific Name'})
			).toBeDisabled();

			await expect(
				page.getByPlaceholder('Choose an option')
			).toBeDisabled();

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

			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('option').filter({hasText: 'es-ES'}),
				trigger: page.getByLabel(
					'Select a language, current language:'
				),
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

			await expect(page.getByLabel('Country')).toHaveAttribute(
				'readonly'
			);

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

			await expect(
				page.getByLabel('Ideal Temperature (ºC)')
			).toHaveAttribute('readonly');

			await expect(page.getByLabel('Last Watering')).toHaveAttribute(
				'readonly'
			);

			await expect(page.getByLabel('Planting Date')).toHaveAttribute(
				'readonly'
			);
		}
	);

	testWithCKEditor4(
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

			await displayPageTemplatesPage.editTemplate(
				displayPageTemplateName
			);

			await pageEditorPage.addFragment(
				'Form Components',
				'Form Container'
			);

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
				trigger: page.getByLabel(
					'Select a language, current language:'
				),
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

	testWithCKEditor4(
		'Visualize text fields in RTL languages with CKEditor 4',
		{tag: '@LPD-48787'},
		async ({apiHelpers, page, pageEditorPage, site}) => {
			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const listTypeDefinition =
				await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

			for (const option of ['Spain', 'Italy']) {
				await apiHelpers.listTypeAdmin.postListTypeEntry(
					listTypeDefinition.externalReferenceCode,
					option
				);
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
				'all',
				{
					addLocalizationSelect: true,
				}
			);

			await pageEditorPage.mapFormFragment(
				unlocalizableFormId,
				'Unlocalizable Fields Group',
				'all'
			);

			await pageEditorPage.publishPage();

			// Go to view mode and check the "dir" attribute of the fields

			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

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

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('option', {
					name: 'Arabic (Saudi Arabia) Language',
				}),
				trigger: page.getByLabel(
					'Select a language, current language:'
				),
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
});

test.describe('Form Localization', () => {
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

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

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

			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

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
				trigger: page.getByLabel(
					'Select a language, current language:'
				),
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

			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await page.getByLabel('Legal Things').check();

			const spanishOption = page
				.getByRole('option')
				.filter({hasText: 'es-ES'});

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: spanishOption,
				trigger: page.getByLabel(
					'Select a language, current language:'
				),
			});

			await page.getByLabel('Legal Things').uncheck();

			// Check the translation in the localization select

			await page
				.getByLabel('Select a language, current language:')
				.click();

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
				await apiHelpers.listTypeAdmin.postListTypeEntry(
					listTypeDefinition.externalReferenceCode,
					option
				);
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

			// Map the form to the Boolean object

			await pageEditorPage.mapFormFragment(formId, 'Select', 'all', {
				addLocalizationSelect: true,
			});

			await pageEditorPage.publishPage();

			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('option', {
					name: 'Italy',
				}),
				trigger: page.getByPlaceholder('Choose an Option'),
			});

			const spanishOption = page
				.getByRole('option')
				.filter({hasText: 'es-ES'});

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: spanishOption,
				trigger: page.getByLabel(
					'Select a language, current language:'
				),
			});

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('option', {
					name: 'Germany',
				}),
				trigger: page.getByPlaceholder('Choose an Option'),
			});

			// Check the translation in the localization select

			await page
				.getByLabel('Select a language, current language:')
				.click();

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
				await apiHelpers.listTypeAdmin.postListTypeEntry(
					listTypeDefinition.externalReferenceCode,
					option
				);
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

			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

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

			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

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
				trigger: page.getByLabel(
					'Select a language, current language:'
				),
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
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

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

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

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

			const firstFileUploadFragment = page
				.locator('.file-upload')
				.first();

			await firstFileUploadFragment
				.getByText('Select File', {exact: true})
				.click();

			const fileChooser = await fileChooserPromise;

			await fileChooser.setFiles(
				path.join(__dirname, '/dependencies/file_upload_image_1.jpg')
			);

			await expect(
				firstFileUploadFragment.getByText('file_upload_image_1.jpg')
			).toBeVisible();

			// Select file from document library in the default language

			const secondFileUploadFragment = page
				.locator('.file-upload')
				.nth(1);

			await chooseFileFromDocumentLibrary({
				fileName: 'balinese.jpg',
				page,
				trigger: secondFileUploadFragment.getByText('Select File', {
					exact: true,
				}),
			});

			// Change the translation to spanish and update the files

			const spanishOption = page
				.getByRole('option')
				.filter({hasText: 'es-ES'});

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: spanishOption,
				trigger: page.getByLabel(
					'Select a language, current language:'
				),
			});

			await fileChooser.setFiles(
				path.join(__dirname, '/dependencies/file_upload_image_2.jpg')
			);

			// Check that the files have been selected and the fields have been translated

			await expect(
				firstFileUploadFragment.getByText('file_upload_image_2.jpg')
			).toBeVisible();

			await chooseFileFromDocumentLibrary({
				fileName: 'cats.jpg',
				page,
				trigger: secondFileUploadFragment.getByText('Select File', {
					exact: true,
				}),
			});

			await page
				.getByLabel('Select a language, current language:')
				.click();

			await expect(spanishOption).toContainText('Language: Translated');

			// Choose other language to check the default values

			const catalanOption = page
				.getByRole('option')
				.filter({hasText: 'ca-ES'});

			await expect(catalanOption).toContainText(
				'Language: Not Translated'
			);

			await catalanOption.click();

			await expect(
				firstFileUploadFragment.getByText('file_upload_image_1.jpg')
			).toBeVisible();

			await expect(
				secondFileUploadFragment.getByText('balinese.jpg')
			).toBeVisible();

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
					'c/attachments'
				);

			const item = items[0];

			const filesFromComputer = Object.entries(
				item.filesFromComputer_i18n
			).map(([locale, value]: [string, any]) => [locale, value.name]);

			expect(filesFromComputer).toStrictEqual([
				['en_US', 'file_upload_image_1.jpg'],
				['es_ES', 'file_upload_image_2.jpg'],
			]);

			const filesFromLibrary = Object.entries(
				item.filesFromLibrary_i18n
			).map(([locale, value]: [string, any]) => [locale, value.name]);

			expect(filesFromLibrary).toStrictEqual([
				['en_US', 'balinese.jpg'],
				['es_ES', 'cats.jpg'],
			]);
		}
	);

	test(
		'Can remove a translation and keep its value in the attachment form field',
		{tag: '@LPD-46482'},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

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

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

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

			const firstFileUploadFragment = page
				.locator('.file-upload')
				.first();

			await firstFileUploadFragment
				.getByText('Select File', {exact: true})
				.click();

			const fileChooser = await fileChooserPromise;

			await fileChooser.setFiles(
				path.join(__dirname, '/dependencies/file_upload_image_1.jpg')
			);

			await expect(
				firstFileUploadFragment.getByText('file_upload_image_1.jpg')
			).toBeVisible();

			// Select file from document library in the default language

			const secondFileUploadFragment = page
				.locator('.file-upload')
				.nth(1);

			await chooseFileFromDocumentLibrary({
				fileName: 'balinese.jpg',
				page,
				trigger: secondFileUploadFragment.getByText('Select File', {
					exact: true,
				}),
			});

			// Change the translation to spanish and remove the files

			const trigger = page.getByLabel(
				'Select a language, current language:'
			);

			await trigger.waitFor();

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('option').filter({hasText: 'es-ES'}),
				trigger,
			});

			await firstFileUploadFragment.getByTitle('Remove Item').click();

			await expect(
				firstFileUploadFragment.getByText('file_upload_image_1.jpg')
			).not.toBeVisible();

			await secondFileUploadFragment.getByTitle('Remove Item').click();

			await expect(
				secondFileUploadFragment.getByText('balinese.jpg')
			).not.toBeVisible();

			// Check that the translations are kept properly

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('option', {
					name: 'English (United States) Language',
				}),
				trigger,
			});

			await expect(
				firstFileUploadFragment.getByText('file_upload_image_1.jpg')
			).toBeVisible();

			await expect(
				secondFileUploadFragment.getByText('balinese.jpg')
			).toBeVisible();

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('option').filter({hasText: 'es-ES'}),
				trigger,
			});

			await expect(
				firstFileUploadFragment.getByText('file_upload_image_1.jpg')
			).not.toBeVisible();

			await expect(
				secondFileUploadFragment.getByText('balinese.jpg')
			).not.toBeVisible();

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('option').filter({hasText: 'ca-ES'}),
				trigger,
			});

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
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

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

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			// Map the form to the Attachment object and publish the page

			await pageEditorPage.mapFormFragment(formId, 'Attachment', 'all', {
				addLocalizationSelect: true,
			});

			await pageEditorPage.publishPage();

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			// Change the translation to spanish

			const trigger = page.getByLabel(
				'Select a language, current language:'
			);

			await trigger.waitFor();

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('option').filter({hasText: 'es-ES'}),
				trigger,
			});

			// Select file from computer in spanish

			const fileChooserPromise = page.waitForEvent('filechooser');

			const firstFileUploadFragment = page
				.locator('.file-upload')
				.first();

			await firstFileUploadFragment
				.getByText('Select File', {exact: true})
				.click();

			const fileChooser = await fileChooserPromise;

			await fileChooser.setFiles(
				path.join(__dirname, '/dependencies/file_upload_image_1.jpg')
			);

			await expect(
				firstFileUploadFragment.getByText('file_upload_image_1.jpg')
			).toBeVisible();

			// Select file from document library in spanish

			const secondFileUploadFragment = page
				.locator('.file-upload')
				.nth(1);

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

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('option', {
					name: 'English (United States) Language',
				}),
				trigger,
			});

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

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			// Map the form to the All Fields, only the Boolean field

			await pageEditorPage.mapFormFragment(formId, 'All Fields', [
				'Boolean',
			]);

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
				await apiHelpers.listTypeAdmin.postListTypeEntry(
					listTypeDefinition.externalReferenceCode,
					option
				);
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

			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			// Change the translation language to spanish

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('option').filter({hasText: 'es-ES'}),
				trigger: page.getByLabel(
					'Select a language, current language:'
				),
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
				page.getByLabel(
					'Ideal Temperature (ºC) field cannot be localized'
				)
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
				.first();

			await expect(richTextToolbarButton).toBeDisabled();

			await expect(
				page.locator('.rich-text-input--disabled')
			).toBeAttached();

			await expect(
				page.getByRole('textbox', {name: 'Scientific Name'})
			).toBeDisabled();

			await expect(
				page.getByPlaceholder('Choose an option')
			).toBeDisabled();

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

			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('option').filter({hasText: 'es-ES'}),
				trigger: page.getByLabel(
					'Select a language, current language:'
				),
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

			await expect(page.getByLabel('Country')).toHaveAttribute(
				'readonly'
			);

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

			await expect(
				page.getByLabel('Ideal Temperature (ºC)')
			).toHaveAttribute('readonly');

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

			await displayPageTemplatesPage.editTemplate(
				displayPageTemplateName
			);

			await pageEditorPage.addFragment(
				'Form Components',
				'Form Container'
			);

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
				trigger: page.getByLabel(
					'Select a language, current language:'
				),
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
				await apiHelpers.listTypeAdmin.postListTypeEntry(
					listTypeDefinition.externalReferenceCode,
					option
				);
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
				'all',
				{
					addLocalizationSelect: true,
				}
			);

			await pageEditorPage.mapFormFragment(
				unlocalizableFormId,
				'Unlocalizable Fields Group',
				'all'
			);

			await pageEditorPage.publishPage();

			// Go to view mode and check the "dir" attribute of the fields

			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

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
				trigger: page.getByLabel(
					'Select a language, current language:'
				),
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
});

test.describe('Numeric input field', () => {
	test('Check the numeric input configuration', async ({
		apiHelpers,
		page,
		pageEditorPage,
		pageManagementSite,
	}) => {

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

		// Go to edit mode and map the form to Lemon object, specifically to the "Lemon Weight" field

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		await pageEditorPage.mapFormFragment(formId, 'Lemon', ['Lemon Weight']);

		// Check Mark as Required field

		const numericInputId = await pageEditorPage.getFragmentId('Numeric');

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Mark as Required',
			fragmentId: numericInputId,
			tab: 'General',
			value: true,
		});

		const requireIcon = page
			.locator('label', {hasText: 'Lemon Weight'})
			.locator('svg.reference-mark');

		await expect(requireIcon).toBeAttached();

		// Check Label and Show Label fields

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Label',
			fragmentId: numericInputId,
			tab: 'General',
			value: 'Lemon weight in grams',
		});

		const label = page.locator('label', {hasText: 'Lemon weight in grams'});

		await expect(label).not.toHaveClass(/sr-only/);

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Show Label',
			fragmentId: numericInputId,
			tab: 'General',
			value: false,
		});

		await expect(label).toHaveClass(/sr-only/);

		// Check Help Text and Show Help Text fields

		const helpText = page.getByText('Add your help text here.', {
			exact: true,
		});

		await expect(helpText).not.toBeAttached();

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Show Help Text',
			fragmentId: numericInputId,
			tab: 'General',
			value: true,
		});

		await expect(helpText).toBeVisible();

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Help Text',
			fragmentId: numericInputId,
			tab: 'General',
			value: 'The lemon weight must be in grams',
		});

		await expect(
			page.getByText('The lemon weight must be in grams')
		).toBeVisible();

		// Check Placeholder field

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Placeholder',
			fragmentId: numericInputId,
			tab: 'General',
			value: 'Lemon weight in grams',
		});

		await expect(
			page.getByPlaceholder('Lemon weight in grams')
		).toBeVisible();
	});

	test('Check the numeric input error', async ({
		apiHelpers,
		page,
		pageEditorPage,
		pageManagementSite,
	}) => {

		// Create a new object validation rule

		const objectValidationRuleAPIClient = await apiHelpers.buildRestClient(
			ObjectValidationRuleAPI
		);

		const {body: objectValidationRule} =
			await objectValidationRuleAPIClient.postObjectDefinitionByExternalReferenceCodeObjectValidationRule(
				getObjectERC('Lemon'),
				{
					active: true,
					engine: 'ddm',
					engineLabel: 'Expression Builder',
					errorLabel: {
						en_US: 'The lemon weight must be greater than 0',
					},
					externalReferenceCode: 'lemon-weight-validation-erc',
					name: {
						en_US: 'Lemon Weight Validation',
					},
					objectDefinitionExternalReferenceCode: 'lemon-object-erc',
					objectValidationRuleSettings: [
						{
							name: 'outputObjectFieldExternalReferenceCode',
							value: 'lemon-weight-erc' as any,
						},
					],
					outputType: 'partialValidation',
					script: 'isEmpty(lemonWeight) OR lemonWeight > 0',
					system: false,
				}
			);

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

		// Go to edit mode and map the form to Lemon object, specifically to the "Lemon Weight" field

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		await pageEditorPage.mapFormFragment(formId, 'Lemon', ['Lemon Weight']);

		await pageEditorPage.publishPage();

		// Go to view mode and check that the input type is numeric and has the attributes max and min

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		const lemonWeightInput = page.getByRole('spinbutton', {
			name: 'Lemon Weight',
		});

		await expect(lemonWeightInput).toHaveAttribute('type', 'number');
		await expect(lemonWeightInput).toHaveAttribute('max');
		await expect(lemonWeightInput).toHaveAttribute('min');

		// Submit the form with a wrong value

		await lemonWeightInput.fill('-1');

		await page.getByText('Submit', {exact: true}).click();

		await expect(
			page.getByText('The lemon weight must be greater than 0')
		).toBeVisible();

		// Submit the form with a correct value

		await lemonWeightInput.fill('10');

		await page.getByText('Submit', {exact: true}).click();

		await expect(
			page.getByText(
				'Thank you. Your information was successfully received.'
			)
		).toBeVisible();

		// Delete validation

		await objectValidationRuleAPIClient.deleteObjectValidationRule(
			objectValidationRule.id
		);
	});
});

test.describe('Text input field', () => {
	test(
		'Check the Text input configuration',
		{tag: '@LPS-149725'},
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

			// Go to edit mode and map the form to Lemon object, specifically to the "Lemon Size" field

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			await pageEditorPage.mapFormFragment(formId, 'Lemon', [
				'Lemon Size',
			]);

			// Check Mark as Required field

			const inputId = await pageEditorPage.getFragmentId('Text');

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Mark as Required',
				fragmentId: inputId,
				tab: 'General',
				value: true,
			});

			const requireIcon = page
				.locator('label', {hasText: 'Lemon Size'})
				.locator('svg.reference-mark');

			await expect(requireIcon).toBeAttached();

			// Check Label and Show Label fields

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Label',
				fragmentId: inputId,
				tab: 'General',
				value: 'Lemon size in cm',
			});

			const label = page.locator('label', {hasText: 'Lemon size in cm'});

			await expect(label).not.toHaveClass(/sr-only/);

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Show Label',
				fragmentId: inputId,
				tab: 'General',
				value: false,
			});

			await expect(label).toHaveClass(/sr-only/);

			// Check Help Text and Show Help Text fields

			const helpText = page.getByText('Add your help text here.', {
				exact: true,
			});

			await expect(helpText).not.toBeAttached();

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Show Help Text',
				fragmentId: inputId,
				tab: 'General',
				value: true,
			});

			await expect(helpText).toBeVisible();

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Help Text',
				fragmentId: inputId,
				tab: 'General',
				value: 'The lemon size must be in cm',
			});

			await expect(
				page.getByText('The lemon size must be in cm')
			).toBeVisible();

			// Check Placeholder field

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Placeholder',
				fragmentId: inputId,
				tab: 'General',
				value: 'Type the lemon size',
			});

			await expect(
				page.getByPlaceholder('Type the lemon size')
			).toBeVisible();

			// Show characters count

			const characterText = page.getByText('0 / 280');

			await expect(characterText).toHaveClass(/sr-only/);

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Show Characters Count',
				fragmentId: inputId,
				tab: 'General',
				value: true,
			});

			await expect(characterText).not.toHaveClass(/sr-only/);
		}
	);

	test(
		'An error is shown when the number of input characters is exceeded',
		{tag: ['@LPS-149725', '@LPS-173849']},
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

			// Go to edit mode and map the form to Lemon object, specifically to the "Lemon Size" field

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			await pageEditorPage.mapFormFragment(formId, 'Lemon', [
				'Lemon Size',
			]);

			// Publish and go to view mode

			await pageEditorPage.publishPage();

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			// Type 290 characters and check that the input error is shown

			const inputError = page.getByText(
				'Maximum Number of Characters Exceeded: 290 / 280'
			);

			await page.getByRole('textbox', {name: 'Lemon Size'}).click();

			await page.keyboard.type('a'.repeat(290));

			await expect(inputError).toBeVisible();

			// Submit the form and check that the error

			await page.getByText('Submit', {exact: true}).click();

			await expect(inputError).not.toBeVisible();

			await expect(
				page.getByText('Value exceeds maximum length of 280.')
			).toBeVisible();
		}
	);

	test(
		'Check that the input is a required field by default',
		{tag: '@LPS-151400'},
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

			// Go to edit mode and map the form to Potato object, specifically to the "Potato Origin" field

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			await pageEditorPage.mapFormFragment(formId, 'Potato', [
				'Potato Origin',
			]);

			// Select the input fragment and check that it is a required field

			const inputId = await pageEditorPage.getFragmentId('Text');

			await pageEditorPage.selectFragment(inputId);

			await pageEditorPage.goToConfigurationTab('Styles');

			await pageEditorPage.goToConfigurationTab('General');

			const selectedOption = page
				.getByLabel('Field', {exact: true})
				.getByRole('option', {selected: true});

			await expect(selectedOption).toContainText('Potato Origin*');

			await expect(
				page.getByLabel('Mark as Required', {exact: true})
			).toBeDisabled();

			// Publish and check that the input has the attribute required

			await pageEditorPage.publishPage();

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await expect(
				page.getByRole('textbox', {name: 'Potato Origin'})
			).toHaveAttribute('required');
		}
	);
});

test.describe('Submit button', () => {
	test(
		"Cannot save a value as draft in the object when 'Allow Users to Save Entries as Draft' option is not enabled",
		{tag: '@LPS-191474'},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Create a Content page with a form

			const formId = getRandomString();

			const formDefinition = getFormContainerDefinition({
				id: formId,
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to edit mode and map the form to Lemon Weight field

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			await pageEditorPage.mapFormFragment(formId, 'Lemon', [
				'Lemon Weight',
			]);

			// Change the "Submitted Entry Status" configuration to Draft

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Submitted Entry Status',
				fragmentId: await pageEditorPage.getFragmentId('Form Button'),
				tab: 'General',
				value: 'Draft',
			});

			// Publish with a draft submit button

			await page.getByLabel('Publish', {exact: true}).click();

			await expect(
				page.getByText(
					'form does not allow creating entries as draft. Review the button configuration and set it to approved to generate valid entries.'
				)
			).toBeVisible();

			await page
				.locator('.modal')
				.getByText('Publish', {exact: true})
				.click();

			await waitForAlert(
				page,
				'Success:The page was published successfully.'
			);

			// Go to view mode and check that the value cannot be saved

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await page
				.getByRole('spinbutton', {name: 'Lemon Weight'})
				.fill('200');

			await page.getByText('Submit', {exact: true}).click();

			await expect(
				page.getByText(
					'An error occurred while sending the form information.'
				)
			).toBeVisible();
		}
	);

	test(
		'It is not possible to change an object from approved status to draft status',
		{tag: '@LPS-191474'},
		async ({
			apiHelpers,
			displayPageTemplatesPage,
			page,
			pageEditorPage,
			pageManagementSite,
		}) => {
			const getObjectEntry = async () => {
				const {items} =
					await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
						'c/drafts'
					);

				const item = items[0];

				return item;
			};

			// Create object definition

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {body: objectDefinition} =
				await objectDefinitionAPIClient.postObjectDefinition({
					active: true,
					enableLocalization: true,
					enableObjectEntryDraft: true,
					externalReferenceCode: 'draftERC',
					label: {
						en_US: 'Draft',
					},
					name: 'Draft',
					objectFields: [
						{
							DBType: 'String',
							businessType: 'Text',
							externalReferenceCode: 'textERC',
							indexed: true,
							indexedAsKeyword: false,
							label: {
								en_US: 'Text',
							},
							localized: true,
							name: 'text',
							required: false,
						},
					],
					pluralLabel: {
						en_US: 'Drafts',
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

			const displayPageTemplateName = getRandomString();

			await test.step('Create a Display Page Template with a Form container mapped to Lemon object and two buttons, one to save as Draft and other to save as Approved', async () => {

				// Create a Display page for the Lemon object

				await displayPageTemplatesPage.goto(
					pageManagementSite.friendlyUrlPath
				);

				await displayPageTemplatesPage.createTemplate({
					contentType: 'Draft',
					name: displayPageTemplateName,
				});

				await displayPageTemplatesPage.editTemplate(
					displayPageTemplateName
				);

				// Add a Form Container and map it to Lemon Weight field

				await pageEditorPage.addFragment(
					'Form Components',
					'Form Container'
				);

				const fragmentId =
					await pageEditorPage.getFragmentId('Form Container');

				await pageEditorPage.mapFormFragment(
					fragmentId,
					'Draft (Default)'
				);

				// Add another submit button with the "Submitted Entry Status" configuration as Draft

				const dptSubmitButtonId =
					await pageEditorPage.getFragmentId('Form Button');

				await pageEditorPage.clickFragmentOption(
					dptSubmitButtonId,
					'Duplicate'
				);

				await pageEditorPage.editTextEditable(
					dptSubmitButtonId,
					'submit-button-text',
					'Submit as draft'
				);

				await pageEditorPage.changeFragmentConfiguration({
					fieldLabel: 'Submitted Entry Status',
					fragmentId: dptSubmitButtonId,
					tab: 'General',
					value: 'Draft',
				});

				await displayPageTemplatesPage.publishTemplate();
			});

			const headingId = getRandomString();
			const formId = getRandomString();
			let layout = null;

			await test.step('Create a Content page with a Form fragment mapped to Lemon object with a draft Submit Button and a Heading fragment', async () => {

				// Create a Content page

				const formDefinition = getFormContainerDefinition({
					id: formId,
				});

				const headingDefinition = getFragmentDefinition({
					id: headingId,
					key: 'BASIC_COMPONENT-heading',
				});

				layout = await apiHelpers.headlessDelivery.createSitePage({
					pageDefinition: getPageDefinition([
						formDefinition,
						headingDefinition,
					]),
					siteId: pageManagementSite.id,
					title: getRandomString(),
				});

				// Go to edit mode

				await pageEditorPage.goto(
					layout,
					pageManagementSite.friendlyUrlPath
				);

				// Map the form to Draft object

				await pageEditorPage.mapFormFragment(formId, 'Draft');

				// Change the "Submitted Entry Status" configuration to Draft

				const submitButtonId =
					await pageEditorPage.getFragmentId('Form Button');

				await pageEditorPage.editTextEditable(
					submitButtonId,
					'submit-button-text',
					'Submit as draft'
				);

				await pageEditorPage.changeFragmentConfiguration({
					fieldLabel: 'Submitted Entry Status',
					fragmentId: submitButtonId,
					tab: 'General',
					value: 'Draft',
				});

				await pageEditorPage.publishPage();
			});

			const input = page.getByRole('textbox', {name: 'Text'});
			const submitDraftButton = page.getByText('Submit as draft', {
				exact: true,
			});

			await test.step('Go to view mode and save the Text field value as draft', async () => {
				await page.goto(
					`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
				);

				await fillAndClickOutside(page, input, '100');

				await submitDraftButton.click();

				await page
					.getByText(
						'Thank you. Your information was successfully received.'
					)
					.waitFor();
			});

			// Check the saved value

			const objectEntry = await getObjectEntry();

			expect(objectEntry.text).toBe('100');
			expect(objectEntry.status.label).toBe('draft');

			// Go to display page

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('Lemon')
				)
			).body;

			const className =
				await apiHelpers.jsonWebServicesClassName.fetchClassName(
					objectDefinitionClassName
				);

			await test.step('Go to object display page and save the field value as Draft', async () => {
				await page.goto(
					`/web${pageManagementSite.friendlyUrlPath}/e/${displayPageTemplateName}/${className.classNameId}/${objectEntry.id}`
				);

				// Set new value and submit as draft

				await fillAndClickOutside(page, input, '200');

				await submitDraftButton.click();

				await page
					.getByText(
						'Thank you. Your information was successfully received.'
					)
					.waitFor();

				// Check the saved value

				const updatedObjectEntry = await getObjectEntry();

				expect(updatedObjectEntry.text).toBe('200');
				expect(updatedObjectEntry.status.label).toBe('draft');
			});

			await test.step('Go to object display page and save the field value as Approved', async () => {
				await page.goto(
					`/web${pageManagementSite.friendlyUrlPath}/e/${displayPageTemplateName}/${className.classNameId}/${objectEntry.id}`
				);

				// Set new value and submit as approved

				await fillAndClickOutside(page, input, '300');

				await page.getByText('Submit', {exact: true}).click();

				await page
					.getByText(
						'Thank you. Your information was successfully received.'
					)
					.waitFor();

				// Check the saved value

				const updatedObjectEntry = await getObjectEntry();

				expect(updatedObjectEntry.text).toBe('300');
				expect(updatedObjectEntry.status.label).toBe('approved');
			});

			await test.step('Go to view mode, click in the Heading and try to save the field value as Draft again', async () => {
				await page.goto(
					`/web${pageManagementSite.friendlyUrlPath}/e/${displayPageTemplateName}/${className.classNameId}/${objectEntry.id}`
				);

				// Set new value and submit as draft

				await fillAndClickOutside(page, input, '400');

				await submitDraftButton.click();

				await expect(
					page.getByText(
						'An error occurred while sending the form information.'
					)
				).toBeVisible();

				// Check the saved value

				const updatedObjectEntry = await getObjectEntry();

				expect(updatedObjectEntry.text).toBe('300');
				expect(updatedObjectEntry.status.label).toBe('approved');
			});
		}
	);
});

test.describe('Textarea input field', () => {
	test(
		'Check the Textarea input configuration',
		{tag: '@LPS-170206'},
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

			// Go to edit mode and map the form to Lemon object, specifically to the "Lemon History" field

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			await pageEditorPage.mapFormFragment(formId, 'Lemon', [
				'Lemon History',
			]);

			const textareaInput = page.locator('[name="lemonHistory"]');

			// Check the role of the input is textbox

			await expect(textareaInput).toHaveRole('textbox');

			// Check Number of Lines config

			await expect(textareaInput).toHaveAttribute('rows', '5');

			await pageEditorPage.selectFragment(formId);

			const textareaInputId =
				await pageEditorPage.getFragmentId('Textarea');

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Number of Lines',
				fragmentId: textareaInputId,
				tab: 'General',
				value: '2',
			});

			await expect(textareaInput).toHaveAttribute('rows', '2');

			// Check Mark as Required field

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Mark as Required',
				fragmentId: textareaInputId,
				tab: 'General',
				value: true,
			});

			const requireIcon = page
				.locator('label', {hasText: 'Lemon History'})
				.locator('svg.reference-mark');

			await expect(requireIcon).toBeAttached();

			// Check Label and Show Label fields

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Label',
				fragmentId: textareaInputId,
				tab: 'General',
				value: 'Describe the history of the lemon',
			});

			const label = page.locator('label', {
				hasText: 'Describe the history of the lemon',
			});

			await expect(label).not.toHaveClass(/sr-only/);

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Show Label',
				fragmentId: textareaInputId,
				tab: 'General',
				value: false,
			});

			await expect(label).toHaveClass(/sr-only/);

			// Check Help Text and Show Help Text fields

			const helpText = page.getByText('Add your help text here.', {
				exact: true,
			});

			await expect(helpText).not.toBeAttached();

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Show Help Text',
				fragmentId: textareaInputId,
				tab: 'General',
				value: true,
			});

			await expect(helpText).toBeVisible();

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Help Text',
				fragmentId: textareaInputId,
				tab: 'General',
				value: 'Brief description of the lemon history',
			});

			await expect(
				page.getByText('Brief description of the lemon history')
			).toBeVisible();

			// Check Placeholder field

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Placeholder',
				fragmentId: textareaInputId,
				tab: 'General',
				value: 'Type the lemon history',
			});

			await expect(
				page.getByPlaceholder('Type the lemon history')
			).toBeVisible();

			// Show characters count

			const characterText = page.getByText('0 / 300');

			await expect(characterText).toHaveClass(/sr-only/);

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Show Characters Count',
				fragmentId: textareaInputId,
				tab: 'General',
				value: true,
			});

			await expect(characterText).not.toHaveClass(/sr-only/);
		}
	);

	test(
		'Check the Textarea input errors',
		{tag: ['@LPS-170206', '@LPS-173849', '@LPS-182728']},
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

			// Go to edit mode and map the form to Lemon object, specifically to the "Lemon History" field

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			await pageEditorPage.mapFormFragment(formId, 'Lemon', [
				'Lemon History',
			]);

			// Publish and go to view mode

			await pageEditorPage.publishPage();

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			// Type 310 characters and check that the input error is shown

			const inputError = page.getByText(
				'Maximum Number of Characters Exceeded: 310 / 300'
			);

			await page.getByRole('textbox', {name: 'Lemon History'}).click();

			await page.keyboard.type('a'.repeat(310));

			await expect(inputError).toBeVisible();

			// Submit the form and check the error

			await page.getByText('Submit', {exact: true}).click();

			await expect(inputError).not.toBeVisible();

			await expect(
				page.getByText('Value exceeds maximum length of 300.')
			).toBeVisible();
		}
	);
});

test.describe('Picklist input field', () => {
	test(
		'Can see more than 10 options on dropdown menu of select from list',
		{
			tag: '@LPD-194759',
		},
		async ({apiHelpers, page, pageManagementSite}) => {

			// Create list type

			const listTypeDefinition =
				await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

			const countries = [
				'Argentina',
				'Brasil',
				'Canada',
				'France',
				'Germany',
				'Hungary',
				'Italy',
				'India',
				'Portugal',
				'Rusia',
				'Spain',
			];

			for (const country of countries) {
				await apiHelpers.listTypeAdmin.postListTypeEntry(
					listTypeDefinition.externalReferenceCode,
					country
				);
			}

			// Create object definition

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
							businessType: 'Picklist',
							externalReferenceCode: 'countryERC',
							indexed: true,
							indexedAsKeyword: false,
							label: {
								en_US: 'Country',
							},
							listTypeDefinitionExternalReferenceCode:
								listTypeDefinition.externalReferenceCode,
							listTypeDefinitionId: listTypeDefinition.id,
							localized: false,
							name: 'country',
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

			// Create a content page with form container

			const picklistId = getRandomString();

			const picklistDefinition = getFragmentDefinition({
				fragmentConfig: {
					inputFieldId: 'ObjectField_country',
				},
				id: picklistId,
				key: 'INPUTS-select-from-list',
			});

			const submitFragmentDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'INPUTS-submit-button',
			});

			const formDefinition = getFormContainerDefinition({
				id: getRandomString(),
				objectDefinitionClassName: objectDefinition.className,
				pageElements: [picklistDefinition, submitFragmentDefinition],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to view mode and assert select options

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await page.getByPlaceholder('Choose an Option').click();

			for (const country of countries) {
				await expect(
					page.getByRole('option', {name: country})
				).toBeVisible();
			}
		}
	);

	test('Shows correct options in picklist field selected as title in related object', async ({
		apiHelpers,
		page,
		pageEditorPage,
		pageManagementSite,
	}) => {

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

		// Go to edit mode

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Map the form to Lemon Basket object, select fields and publish

		await pageEditorPage.mapFormFragment(formId, 'Lemon', [
			'Lemon Size',
			'Lemon Basket to Lemons',
		]);

		await pageEditorPage.publishPage();

		// Go to view mode and check picklist values

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await page
			.getByRole('combobox', {name: 'Lemon Basket to Lemons'})
			.click();

		await expect(page.getByText('Plastic', {exact: true})).toBeVisible();
		await expect(page.getByText('Carton', {exact: true})).toBeVisible();
	});

	test(
		'Checks changing the option when one default is selected set the value correctly',
		{
			tag: '@LPD-31856',
		},
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

			// Go to edit mode and map it to the object

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			await pageEditorPage.mapFormFragment(formId, 'Lemon Basket', [
				'Lemon Dimensions',
				'Material',
			]);

			// Publish and go to view mode

			await pageEditorPage.publishPage();

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			// Check that the value after selecting the item is correct

			const materialField = page.getByRole('combobox', {
				name: 'Material',
			});

			await materialField.click();

			await page.getByText('carton').click();

			await expect(materialField).toHaveValue('Carton');
		}
	);

	test(
		'The page designer map the Select from List fragment to objects fields on content pages',
		{
			tag: ['@LPS-151159', '@LPS-182728'],
		},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Create a page with a Form fragment

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('Lemon Basket')
				)
			).body;

			const picklistId = getRandomString();

			const picklistDefinition = getFragmentDefinition({
				fragmentConfig: {
					inputFieldId: 'ObjectField_material',
				},
				id: picklistId,
				key: 'INPUTS-select-from-list',
			});

			const multiselectPicklistDefinition = getFragmentDefinition({
				fragmentConfig: {
					inputFieldId: 'ObjectField_lemonDimensions',
				},
				id: getRandomString(),
				key: 'INPUTS-select-from-list',
			});

			const submitFragmentDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'INPUTS-submit-button',
			});

			const formDefinition = getFormContainerDefinition({
				id: getRandomString(),
				objectDefinitionClassName,
				pageElements: [
					picklistDefinition,
					multiselectPicklistDefinition,
					submitFragmentDefinition,
				],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to edit mode and map it to the object

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			// Change label and help text

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Label',
				fragmentId: picklistId,
				tab: 'General',
				value: 'Select your material',
			});

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Help Text',
				fragmentId: picklistId,
				tab: 'General',
				value: 'Just one material can be selected',
			});

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Show Help Text',
				fragmentId: picklistId,
				tab: 'General',
				value: true,
			});

			// Mark field as required

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Mark as Required',
				fragmentId: picklistId,
				tab: 'General',
				value: true,
			});

			// Publish and go to view mode

			await pageEditorPage.publishPage();

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			// Assert help text and label

			await expect(page.getByText('Select your material')).toBeVisible();

			await expect(
				page.getByText('Just one material can be selected')
			).toBeVisible();
		}
	);
});

test.describe('Rich Text Fragment', () => {
	test(
		'The page designer can configure rich text fragment',
		{
			tag: '@LPS-170205',
		},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Create a page with a form fragment with a rich text fragment

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('All Fields')
				)
			).body;

			const richTextId = getRandomString();

			const richTextDefinition = getFragmentDefinition({
				fragmentConfig: {
					inputFieldId: 'ObjectField_richText',
				},
				id: richTextId,
				key: 'INPUTS-rich-text-input',
			});

			const formDefinition = getFormContainerDefinition({
				id: getRandomString(),
				objectDefinitionClassName,
				pageElements: [richTextDefinition],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to edit mode

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			// Change label

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Label',
				fragmentId: richTextId,
				tab: 'General',
				value: 'Description',
			});

			const richTexInput = page.locator('.rich-text-input');

			await expect(richTexInput.getByText('Description')).toBeVisible();

			// Hide label

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Show Label',
				fragmentId: richTextId,
				tab: 'General',
				value: false,
			});

			await expect(
				richTexInput.getByText('Description')
			).not.toBeVisible();

			// Show help text

			await expect(richTexInput).not.toContainText(
				/Add your help text here./
			);

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Show Help Text',
				fragmentId: richTextId,
				tab: 'General',
				value: true,
			});

			await expect(richTexInput).toContainText(
				/Add your help text here./
			);
		}
	);

	test(
		'Check that the object entry for a Rich Text field is saved correctly',
		{
			tag: '@LPD-55891',
		},
		async ({
			apiHelpers,
			displayPageTemplatesPage,
			page,
			pageEditorPage,
			pageManagementSite,
		}) => {

			// Create an object "Student" with text and rich text fields

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {body: objectDefinition} =
				await objectDefinitionAPIClient.postObjectDefinition({
					active: true,
					externalReferenceCode: 'studentERC',
					label: {
						en_US: 'Student',
					},
					name: 'Student',
					objectFields: [
						{
							DBType: 'String',
							businessType: 'Text',
							externalReferenceCode: 'nameERC',
							indexed: true,
							indexedAsKeyword: false,
							label: {
								en_US: 'Name',
							},
							name: 'name',
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
							name: 'description',
						},
					],
					pluralLabel: {
						en_US: 'Students',
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

			// Create a Display page for the Student object

			await displayPageTemplatesPage.goto(
				pageManagementSite.friendlyUrlPath
			);

			const displayPageTemplateName = getRandomString();

			await displayPageTemplatesPage.createTemplate({
				contentType: 'Student',
				name: displayPageTemplateName,
			});

			await displayPageTemplatesPage.editTemplate(
				displayPageTemplateName
			);

			// Add a Form Container and map it to the Student object

			await pageEditorPage.addFragment(
				'Form Components',
				'Form Container'
			);

			const dptFormId =
				await pageEditorPage.getFragmentId('Form Container');

			await pageEditorPage.mapFormFragment(
				dptFormId,
				'Student (Default)'
			);

			await displayPageTemplatesPage.publishTemplate();

			// Mark display page as default

			await displayPageTemplatesPage.goto(
				pageManagementSite.friendlyUrlPath
			);

			await displayPageTemplatesPage.markAsDefault(
				displayPageTemplateName
			);

			// Create a content page with a form mapped to the Student object

			const textInputFragment = getFragmentDefinition({
				fragmentConfig: {
					inputFieldId: 'ObjectField_name',
				},
				id: getRandomString(),
				key: 'INPUTS-text-input',
			});

			const richTextInputDefinition = getFragmentDefinition({
				fragmentConfig: {
					inputFieldId: 'ObjectField_description',
				},
				id: getRandomString(),
				key: 'INPUTS-rich-text-input',
			});

			const submitFragmentDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'INPUTS-submit-button',
			});

			const formId = getRandomString();

			const formDefinition = getFormContainerDefinition({
				id: formId,
				objectDefinitionClassName: objectDefinition.className,
				pageElements: [
					textInputFragment,
					richTextInputDefinition,
					submitFragmentDefinition,
				],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to edit mode and change redirect to display page after submit

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Success Action',
				fragmentId: formId,
				panel: 'Actions After Submit',
				tab: 'General',
				value: 'Go to Entry Display Page',
			});

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Display Page',
				fragmentId: formId,
				tab: 'General',
				value: 'Default',
			});

			await pageEditorPage.publishPage();

			// Go to view mode

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			// Fill the description field and change its style

			const descriptionField = page.locator('.ck-editor__editable');

			await descriptionField.waitFor();

			await descriptionField.fill('This is the student description');

			await descriptionField.click();

			await page.getByText('student description').selectText();

			const toolbar = page.locator('.ck-toolbar', {
				hasText: 'Text alignment',
			});

			await toolbar.waitFor();

			// Check that the button is visible and works

			await toolbar.getByLabel('Text alignment', {exact: true}).click();
			await toolbar.getByLabel('Align right', {exact: true}).click();

			// Fill the name field

			const nameField = page.getByRole('textbox', {name: 'Name'});

			await nameField.fill('Charlie');

			// Submit the form

			await page.getByText('Submit', {exact: true}).click();

			// Check that the values have been submitted and user is redirected to display page

			await expect(
				page.getByText(
					'Thank you. Your information was successfully received.'
				)
			).not.toBeVisible();

			await descriptionField.waitFor();

			await expect(descriptionField).toContainText(
				'This is the student description'
			);

			await expect(nameField).toHaveValue('Charlie');

			// Change the name in the display page template

			await nameField.fill('Adam');

			await page.getByRole('button', {name: 'Submit'}).click();

			await expect(
				page.getByText(
					'Thank you. Your information was successfully received.'
				)
			).toBeVisible();

			// Check the object entry

			const {items} =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
					'c/students'
				);

			expect(items[0].description).toStrictEqual(
				'<p style="text-align: right;">This is the student description</p>'
			);

			expect(items[0].name).toStrictEqual('Adam');
		}
	);

	test(
		'User should see error message below rich text fragment',
		{
			tag: '@LPS-182728',
		},
		async ({apiHelpers, page, pageManagementSite}) => {

			// Adds rich text validation

			const objectValidationRuleAPIClient =
				await apiHelpers.buildRestClient(ObjectValidationRuleAPI);

			const {body: objectValidationRule} =
				await objectValidationRuleAPIClient.postObjectDefinitionByExternalReferenceCodeObjectValidationRule(
					getObjectERC('All Fields'),
					{
						active: true,
						engine: 'ddm',
						engineLabel: 'Expression Builder',
						errorLabel: {
							en_US: 'Please enter a valid description.',
						},
						name: {
							en_US: 'Rich Text Validation',
						},
						objectValidationRuleSettings: [
							{
								name: 'outputObjectFieldExternalReferenceCode',
								value: 'rich-text-erc',
							} as any,
						],
						outputType: 'partialValidation',
						script: 'NOT(isEmpty(richText))',
						system: false,
					}
				);

			// Create a page with a form fragment with a rich text fragment

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('All Fields')
				)
			).body;

			const richTextId = getRandomString();

			const richTextDefinition = getFragmentDefinition({
				fragmentConfig: {
					inputFieldId: 'ObjectField_richText',
				},
				id: richTextId,
				key: 'INPUTS-rich-text-input',
			});

			const submitFragmentDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'INPUTS-submit-button',
			});

			const formDefinition = getFormContainerDefinition({
				id: getRandomString(),
				objectDefinitionClassName,
				pageElements: [richTextDefinition, submitFragmentDefinition],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to view mode

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			// Assert error message

			await page.getByText('Submit', {exact: true}).click();

			await expect(page.locator('.rich-text-input')).toContainText(
				'Please enter a valid description.'
			);

			// Delete validation

			await objectValidationRuleAPIClient.deleteObjectValidationRule(
				objectValidationRule.id
			);
		}
	);
});

test.describe('Relationships', () => {
	test('Allow selecting fields from main object and relationships in fields modal', async ({
		apiHelpers,
		pageEditorPage,
		pageManagementSite,
	}) => {

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

		// Go to edit mode

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Map the form to Lemon object and select fields

		await pageEditorPage.mapFormFragment(formId, 'Lemon', [
			'Lemon Size',
			'Lemon Basket Color',
		]);

		const form = pageEditorPage.getFragment(formId);

		await expect(form.getByText('Lemon Size')).toBeVisible();
		await expect(form.getByText('Lemon Basket Color')).toBeVisible();
	});
});

test.describe('Multiselect', () => {
	test(
		'Page designer can define number of options of multiselect fragment',
		{tag: ['@LPS-169936', '@LPS-182728']},
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

			// Go to edit mode

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			// Map the form to object and select fields

			await pageEditorPage.mapFormFragment(formId, 'Lemon Basket', [
				'Lemon Dimensions',
			]);

			// Assert help text

			await expect(page.getByText('Lemon Dimensions')).toBeVisible();

			const inputId = await pageEditorPage.getFragmentId('Multiselect');

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Show Help Text',
				fragmentId: inputId,
				tab: 'General',
				value: true,
			});

			await expect(
				page.getByText('Add your help text here.')
			).toBeVisible();

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Help Text',
				fragmentId: inputId,
				tab: 'General',
				value: 'Preferences',
			});

			await expect(page.getByText('Preferences')).toBeVisible();

			// Assert mandatory symbol

			await expect(
				page.locator('.custom-checkbox .lexicon-icon-asterisk')
			).toBeVisible();

			// Change number of options configuration

			await pageEditorPage.selectFragment(inputId);

			await expect(
				page.getByLabel('Number of Options', {exact: true})
			).toHaveValue('5');

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Number of Options',
				fragmentId: inputId,
				tab: 'General',
				value: '2',
			});

			// Assert options in edit mode

			const multiselect = page.locator(
				'.custom-checkbox .custom-control-label'
			);

			await expect(multiselect.nth(0).getByText('Large')).toBeVisible();
			await expect(multiselect.nth(1).getByText('Medium')).toBeVisible();
			await expect(
				multiselect.nth(2).getByText('Small')
			).not.toBeVisible();

			await pageEditorPage.publishPage();

			// Assert options in view mode

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await expect(multiselect.nth(0).getByText('Large')).toBeVisible();
			await expect(multiselect.nth(1).getByText('Medium')).toBeVisible();
			await expect(
				multiselect.nth(2).getByText('Small')
			).not.toBeVisible();

			// Click show all

			await page.getByRole('button', {name: 'Show All'}).click();

			await expect(multiselect.nth(0).getByText('Large')).toBeVisible();
			await expect(multiselect.nth(1).getByText('Medium')).toBeVisible();
			await expect(multiselect.nth(2).getByText('Small')).toBeVisible();

			// Go to edit mode and update show all options configuration

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Show All Options',
				fragmentId: inputId,
				tab: 'General',
				value: true,
			});

			// Assert options in edit mode

			await expect(multiselect.nth(0).getByText('Large')).toBeVisible();
			await expect(multiselect.nth(1).getByText('Medium')).toBeVisible();
			await expect(multiselect.nth(2).getByText('Small')).toBeVisible();

			await pageEditorPage.publishPage();

			// Assert options in view mode

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await expect(multiselect.nth(0).getByText('Large')).toBeVisible();
			await expect(multiselect.nth(1).getByText('Medium')).toBeVisible();
			await expect(multiselect.nth(2).getByText('Small')).toBeVisible();
		}
	);
});

test.describe('Multistep', () => {
	test(
		'Change to multistep when adding a stepper fragment and remove it when changing to simple',
		{tag: '@LPD-10727'},
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

			// Go to edit mode

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			// Map the form to Lemon object and select fields

			await pageEditorPage.mapFormFragment(formId, 'Lemon', [
				'Lemon Size',
				'Lemon Basket Color',
			]);

			// Add stepper and check multistep modal is shown

			await pageEditorPage.addFragment('Form Components', 'Stepper');

			await expect(
				page.getByText(
					'Adding a stepper fragment inside a simple form will turn it into a multistep form. Are you sure you want to continue?'
				)
			).toBeVisible();

			await page.getByRole('button', {name: 'Continue'}).click();

			// Check that the form is now multistep

			await pageEditorPage.selectFragment(
				await pageEditorPage.getFragmentId('Form Container')
			);

			await expect(
				page.getByLabel('Form Type', {exact: true})
			).toHaveValue('multistep');

			// Change to simple and check stepper is removed

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Form Type',
				fragmentId: formId,
				tab: 'General',
				value: 'simple',
			});

			await page.getByRole('button', {name: 'Continue'}).click();

			await expect(
				page.locator('[data-name="Stepper"]')
			).not.toBeVisible();
		}
	);

	test(
		'Can add and configure a Stepper fragment',
		{tag: '@LPD-10727'},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Get the id of Lemon object from the site initializer

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('Lemon')
				)
			).body;

			// Create a page with a Form fragment with a Stepper fragment

			const stepperId = getRandomString();

			const stepperFragment = getFragmentDefinition({
				id: stepperId,
				key: 'INPUTS-stepper',
			});

			const formId = getRandomString();

			const formDefinition = getFormContainerDefinition({
				id: formId,
				objectDefinitionClassName,
				pageElements: [stepperFragment],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to edit mode

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			// Check steps titles and bullets numbers are displayed

			await page.locator('.multi-step-nav').getByText('Step 1').waitFor();

			await page
				.locator('.multi-step-icon[data-multi-step-icon="1"]')
				.waitFor();

			// Hide both and check they are not displayed

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Show Bullets Numbers',
				fragmentId: stepperId,
				tab: 'General',
				value: false,
			});

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Show Step Titles',
				fragmentId: stepperId,
				tab: 'General',
				value: false,
			});

			await expect(
				page.locator('.multi-step-nav').getByText('Step 1')
			).not.toBeVisible();

			await expect(
				page.locator('.multi-step-icon[data-multi-step-icon="1"]')
			).not.toBeVisible();
		}
	);

	test(
		'Can configure multistep options for a form container',
		{tag: '@LPD-10727'},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Get the id of Lemon object from the site initializer

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('Lemon')
				)
			).body;

			// Create a page with a form container

			const formId = getRandomString();

			const formDefinition = getFormContainerDefinition({
				id: formId,
				objectDefinitionClassName,
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to edit mode

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			// Check steps are not displayed

			await expect(page.locator('.page-editor__form-step')).toHaveCount(
				0
			);

			// Change to Multistep and check first step is displayed

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Form Type',
				fragmentId: formId,
				tab: 'General',
				value: 'Multistep',
			});

			await expect(page.locator('.page-editor__form-step')).toHaveCount(
				2
			);

			await expect(
				page.locator('.page-editor__form-step').nth(0)
			).toBeVisible();

			await expect(
				page.locator('.page-editor__form-step').nth(1)
			).not.toBeVisible();

			// Check option to display all steps and check it works

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Display All Steps in Edit Mode',
				fragmentId: formId,
				tab: 'General',
				value: true,
			});

			await expect(
				page.locator('.page-editor__form-step').nth(0)
			).toBeVisible();

			await expect(
				page.locator('.page-editor__form-step').nth(1)
			).toBeVisible();

			// Change number of steps and check it works

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Number of Steps',
				fragmentId: formId,
				tab: 'General',
				value: '3',
			});

			await expect(page.locator('.page-editor__form-step')).toHaveCount(
				3
			);
		}
	);

	test(
		'Can change step with the stepper fragment',
		{tag: ['@LPD-10727', '@LPD-45551']},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Get the id of Lemon object from the site initializer

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('Lemon')
				)
			).body;

			// Definition for the Stepper fragment

			const stepperFragment = getFragmentDefinition({
				id: getRandomString(),
				key: 'INPUTS-stepper',
			});

			// Create a form with two steps and the Stepper

			const headingDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-heading',
			});

			const buttonDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-button',
			});

			const formDefinition = getFormContainerDefinition({
				id: getRandomString(),
				objectDefinitionClassName,
				pageElements: [stepperFragment],
				steps: [[headingDefinition], [buttonDefinition]],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to edit mode

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			// Check step change works properly

			const button = page.locator(
				'.lfr-layout-structure-item-basic-component-button'
			);

			const heading = page.locator(
				'.lfr-layout-structure-item-basic-component-heading'
			);

			await expect(button).not.toBeVisible();
			await expect(heading).toBeVisible();

			const stepButtons = await page.locator('.multi-step-icon').all();
			await stepButtons[1].click();

			await expect(button).toBeVisible();
			await expect(heading).not.toBeVisible();

			// Check in other viewports too

			await pageEditorPage.switchViewport('Tablet');

			const viewportIframe = page.frameLocator(
				'.page-editor__global-context-iframe'
			);

			await viewportIframe.locator(button).waitFor();

			await expect(async () => {
				await viewportIframe
					.locator(`.multi-step-icon`)
					.nth(0)
					.click({timeout: 1000});

				await expect(viewportIframe.locator(button)).not.toBeVisible({
					timeout: 1000,
				});

				await expect(viewportIframe.locator(heading)).toBeVisible({
					timeout: 1000,
				});
			}).toPass();
		}
	);

	test(
		'Can change step with the form button fragment',
		{tag: '@LPD-10727'},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Get the id of Lemon object from the site initializer

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('Lemon')
				)
			).body;

			// Create a form with two steps and two form buttons

			const headingDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-heading',
			});

			const formButtonNextId = getRandomString();

			const formButtonNext = getFragmentDefinition({
				id: formButtonNextId,
				key: 'INPUTS-submit-button',
			});

			const buttonDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-button',
			});

			const formButtonPrevious = getFragmentDefinition({
				fragmentConfig: {
					type: 'previous',
				},
				id: getRandomString(),
				key: 'INPUTS-submit-button',
			});

			const formButtonSubmit = getFragmentDefinition({
				id: getRandomString(),
				key: 'INPUTS-submit-button',
			});

			const formDefinition = getFormContainerDefinition({
				id: getRandomString(),
				objectDefinitionClassName,
				steps: [
					[headingDefinition, formButtonNext],
					[buttonDefinition, formButtonPrevious, formButtonSubmit],
				],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Create function that check form buttons behavior

			const checkFormButtonsBehavior = async () => {

				// Check initial state

				const button = page.locator(
					'.lfr-layout-structure-item-basic-component-button'
				);

				const heading = page.locator(
					'.lfr-layout-structure-item-basic-component-heading'
				);

				await expect(heading).toBeVisible();
				await expect(button).not.toBeVisible();

				// Check Next button works

				await page
					.locator('.btn', {hasText: 'Next'})
					.click({position: {x: 10, y: 10}});

				await expect(heading).not.toBeVisible();
				await expect(button).toBeVisible();

				// Check Previous button works

				await page
					.locator('.btn', {hasText: 'Previous'})
					.click({position: {x: 10, y: 10}});

				await expect(heading).toBeVisible();
				await expect(button).not.toBeVisible();
			};

			// Check in edit mode

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Type',
				fragmentId: formButtonNextId,
				tab: 'General',
				value: 'Next',
			});

			await expect(
				page.locator('.component-button').getByText('Next')
			).toBeVisible();

			await checkFormButtonsBehavior();

			await pageEditorPage.publishPage();

			// Check in view mode

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await checkFormButtonsBehavior();
		}
	);

	test(
		'Step change affects only desired form',
		{tag: '@LPD-10727'},
		async ({apiHelpers, page, pageManagementSite}) => {

			// Get the id of Lemon object from the site initializer

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('Lemon')
				)
			).body;

			// Definition for the Steppers fragment

			const stepperFragment1 = getFragmentDefinition({
				id: getRandomString(),
				key: 'INPUTS-stepper',
			});

			const stepperFragment2 = getFragmentDefinition({
				id: getRandomString(),
				key: 'INPUTS-stepper',
			});

			// Create two forms with two steps and the Stepper

			const headingDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-heading',
			});

			const buttonDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-button',
			});

			const form1Definition = getFormContainerDefinition({
				id: getRandomString(),
				objectDefinitionClassName,
				pageElements: [stepperFragment1],
				steps: [[headingDefinition], []],
			});

			const form2Definition = getFormContainerDefinition({
				id: getRandomString(),
				objectDefinitionClassName,
				pageElements: [stepperFragment2],
				steps: [[buttonDefinition], []],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					form1Definition,
					form2Definition,
				]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to view mode of page

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			// Check step change affects only desired form

			const button = page.locator(
				'.lfr-layout-structure-item-basic-component-button'
			);

			const heading = page.locator(
				'.lfr-layout-structure-item-basic-component-heading'
			);

			await expect(button).toBeVisible();
			await expect(heading).toBeVisible();

			const firstForm = page
				.locator('.lfr-layout-structure-item-form')
				.first();

			const firstFormSteps = await firstForm
				.locator('.multi-step-icon')
				.all();
			await firstFormSteps[1].click();

			await expect(heading).not.toBeVisible();
			await expect(button).toBeVisible();
		}
	);

	test(
		'Stepper gets number of steps from parent form',
		{tag: '@LPD-10727'},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Get the id of Lemon object from the site initializer

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('Lemon')
				)
			).body;

			// Create a form with a Stepper

			const stepperId = getRandomString();

			const stepperFragment = getFragmentDefinition({
				id: stepperId,
				key: 'INPUTS-stepper',
			});

			const formId = getRandomString();

			const formDefinition = getFormContainerDefinition({
				id: formId,
				objectDefinitionClassName,
				pageElements: [stepperFragment],
				steps: [[]],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to edit mode of page

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			// Check changing number of steps in Form affects the Stepper

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Number of Steps',
				fragmentId: formId,
				tab: 'General',
				value: '4',
			});

			await expect(page.locator('.multi-step-indicator')).toHaveCount(4);

			// Delete the Stepper and check that when adding it again, it takes the correct number of steps

			await pageEditorPage.deleteFragment(stepperId);

			await pageEditorPage.addFragment('Form Components', 'Stepper');

			await expect(page.locator('.multi-step-indicator')).toHaveCount(4);
		}
	);

	test(
		'Undoing the action of adding a stepper to a simple form changes the form type to simple again',
		{tag: '@LPD-10727'},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Get the id of Lemon object from the site initializer

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('Lemon')
				)
			).body;

			// Create a page with a Form fragment

			const formId = getRandomString();

			const formDefinition = getFormContainerDefinition({
				id: formId,
				objectDefinitionClassName,
				pageElements: [],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to edit mode

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			// Add a Stepper

			await pageEditorPage.addFragment(
				'Form Components',
				'Stepper',
				page.locator('.page-editor__form .page-editor__container')
			);

			await page
				.locator('.modal-title', {hasText: 'Convert to Multistep Form'})
				.waitFor();

			await page.locator('.modal-footer').getByText('Continue').click();

			await pageEditorPage.waitForChangesSaved();

			// Check type changed to Multistep

			await pageEditorPage.selectFragment(formId);

			await expect(
				page.getByLabel('Form Type', {exact: true})
			).toHaveValue('multistep');

			// Undo the action

			await pageEditorPage.undoButton.click();

			await pageEditorPage.waitForChangesSaved();

			// Check Stepper disappeared and type changed to Simple again

			await expect(
				page.getByLabel('Form Type', {exact: true})
			).toHaveValue('simple');

			await expect(page.locator('.multi-step-nav')).not.toBeVisible();
		}
	);

	test(
		'Undoing the action of moving a stepper from a multistep to a simple form changes the form type to simple again',
		{tag: '@LPD-10727'},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Get the id of Lemon object from the site initializer

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('Lemon')
				)
			).body;

			// Create a page containing a multistep form with a stepper and a simple form

			const stepperId = getRandomString();

			const stepperFragment = getFragmentDefinition({
				id: stepperId,
				key: 'INPUTS-stepper',
			});

			// Create a form with two steps and the Stepper

			const headingDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-heading',
			});

			const buttonDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-button',
			});

			const firstFormId = getRandomString();

			const firstFormDefinition = getFormContainerDefinition({
				id: firstFormId,
				objectDefinitionClassName,
				pageElements: [stepperFragment],
				steps: [[headingDefinition], [buttonDefinition]],
			});

			const secondFormId = getRandomString();

			const secondFormDefinition = getFormContainerDefinition({
				id: secondFormId,
				objectDefinitionClassName,
				pageElements: [],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					firstFormDefinition,
					secondFormDefinition,
				]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to edit mode

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			// Move the stepper to the second form

			await pageEditorPage.goToSidebarTab('Browser');

			await pageEditorPage.selectFragment(stepperId);

			await pageEditorPage.dragTreeNode({
				source: {label: 'Stepper'},
				target: {label: 'Form Container', nth: 1},
			});

			await page
				.locator('.modal-title', {hasText: 'Convert to Multistep Form'})
				.waitFor();

			await page.locator('.modal-footer').getByText('Continue').click();

			await pageEditorPage.waitForChangesSaved();

			// Check type changed to Multistep

			await pageEditorPage.selectFragment(secondFormId);

			await expect(
				page.getByLabel('Form Type', {exact: true})
			).toHaveValue('multistep');

			// Undo the action

			await pageEditorPage.undoButton.click();

			await pageEditorPage.waitForChangesSaved();

			// Check Stepper disappeared and type changed to Simple again

			await expect(
				page.getByLabel('Form Type', {exact: true})
			).toHaveValue('simple');

			const secondForm = page
				.locator('.page-editor__form .page-editor__container')
				.last();

			await expect(
				secondForm.locator('.multi-step-nav')
			).not.toBeVisible();

			// Check Stepper is present in the first shape in the first position

			const firstForm = page
				.locator('.page-editor__form .page-editor__container')
				.first();

			await expect(firstForm.locator('.multi-step-nav')).toBeVisible();

			await expect(
				firstForm.locator('.page-editor__topper').first()
			).toContainText(/Step 1/);
		}
	);

	test(
		'Correctly handle multistep form errors in view mode',
		{tag: '@LPD-10727'},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Get the id of Potato object from the site initializer

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('Potato')
				)
			).body;

			// Create a form with three steps and a stepper

			const stepperId = getRandomString();

			const stepperFragment = getFragmentDefinition({
				fragmentConfig: {
					numberOfSteps: 3,
				},
				id: stepperId,
				key: 'INPUTS-stepper',
			});

			const textInputId = getRandomString();

			const textInputFragment = getFragmentDefinition({
				id: textInputId,
				key: 'INPUTS-text-input',
			});

			const submitButtonFragment = getFragmentDefinition({
				fragmentConfig: {
					type: 'submit',
				},
				id: getRandomString(),
				key: 'INPUTS-submit-button',
			});

			const formId = getRandomString();

			const formDefinition = getFormContainerDefinition({
				id: formId,
				objectDefinitionClassName,
				pageElements: [stepperFragment],
				steps: [[], [textInputFragment], [submitButtonFragment]],
			});

			// Create page and go to edit mode

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			// Map text input fragment to Potato Origin field

			await page.locator('[data-multi-step-icon="2"]').click();

			await pageEditorPage.selectFragment(textInputId);

			await pageEditorPage.goToConfigurationTab('Styles');

			await pageEditorPage.goToConfigurationTab('General');

			await page.getByLabel('Field', {exact: true}).waitFor();

			await page
				.getByLabel('Field', {exact: true})
				.selectOption('Potato Origin*');

			// Publish

			await clickAndExpectToBeVisible({
				target: page.locator('.modal-title', {hasText: 'Form Errors'}),
				timeout: 3000,
				trigger: pageEditorPage.publishButton,
			});

			await page.locator('.modal-footer').getByText('Publish').click();

			await waitForAlert(
				page,
				'Success:The page was published successfully.'
			);

			// Go to view mode

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			// Create function to submit form

			const submitForm = async () => {
				await expect(async () => {
					await page.locator('[data-multi-step-icon="2"]').click();

					const submitButton = page.getByRole('button', {
						name: 'Submit',
					});

					await page.locator('[data-multi-step-icon="3"]').click();

					await expect(submitButton).toBeVisible({timeout: 100});

					await submitButton.click();
				}).toPass();
			};

			// Try to submit and check it takes to step 2 because field is required

			const field = page.getByRole('textbox', {name: 'Potato Origin'});

			await submitForm();

			await field.waitFor();

			// Fill field with incorrect value, submit and check it shows error

			await field.fill('Madrid');

			await submitForm();

			await page
				.getByText('Potato Origin should be Canary Islands')
				.waitFor();

			// Fill field with correct value, submit and check it submits

			await field.fill('Canary Islands');

			await submitForm();

			await expect(
				page.getByText('Your information was successfully received')
			).toBeVisible();
		}
	);

	test(
		'The last step is selected when the active one is removed',
		{tag: '@LPD-38514'},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Get the id of Lemon object from the site initializer

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('Lemon')
				)
			).body;

			// Create a form with a Stepper

			const stepperId = getRandomString();

			const stepperFragment = getFragmentDefinition({
				fragmentConfig: {
					numberOfSteps: 3,
				},
				id: stepperId,
				key: 'INPUTS-stepper',
			});

			const headingDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-heading',
			});

			const formId = getRandomString();

			const formDefinition = getFormContainerDefinition({
				id: formId,
				objectDefinitionClassName,
				pageElements: [stepperFragment],
				steps: [[], [headingDefinition], []],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to edit mode of page and select third step

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			await page.locator('.multi-step-indicator').nth(2).click();

			await expect(page.getByText('Heading Example')).not.toBeVisible();

			// Change the number of steps to 2 and check step 2 is selected

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Number of Steps',
				fragmentId: formId,
				tab: 'General',
				value: '2',
			});

			await expect(page.getByText('Heading Example')).toBeVisible();
		}
	);

	test(
		'Step is changed when selecting a fragment in the tree',
		{tag: ['@LPD-37501']},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Get the id of Lemon object from the site initializer

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('Lemon')
				)
			).body;

			// Definition for the Stepper fragment

			const stepperFragment = getFragmentDefinition({
				id: getRandomString(),
				key: 'INPUTS-stepper',
			});

			// Create a form with two steps and the Stepper

			const headingDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-heading',
			});

			const buttonDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-button',
			});

			const formDefinition = getFormContainerDefinition({
				id: getRandomString(),
				objectDefinitionClassName,
				pageElements: [stepperFragment],
				steps: [[headingDefinition], [buttonDefinition]],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to edit mode

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			await page
				.getByText('Select a Page Element', {exact: true})
				.waitFor();

			await expect(page.locator('.multi-step-item').nth(0)).toHaveClass(
				/active/,
				{timeout: 1000}
			);

			// Go to the tree and select the form step container so the steps appear

			await pageEditorPage.goToSidebarTab('Browser');

			await page
				.locator('.page-editor__page-structure__tree-node', {
					hasText: 'Form Container',
				})
				.click();

			await page
				.locator('.page-editor__page-structure__tree-node', {
					hasText: 'Form Steps',
				})
				.click();

			await expect(async () => {

				// Select the button fragment present in first step

				await page
					.locator('.page-editor__page-structure__tree-node', {
						hasText: 'Step 1',
					})
					.click({timeout: 1000});

				await page
					.locator('.page-editor__page-structure__tree-node', {
						hasText: 'Heading',
					})
					.click({timeout: 1000});

				// Select the button fragment present in second step

				await page
					.locator('.page-editor__page-structure__tree-node', {
						hasText: 'Step 2',
					})
					.click({timeout: 1000});

				await page
					.locator('.page-editor__page-structure__tree-node', {
						hasText: 'Button',
					})
					.click({timeout: 1000});

				// Check button is visible and stepper is updated

				await expect(
					page.locator('.page-editor__topper__title', {
						hasText: 'Button',
					})
				).toBeVisible({timeout: 1000});

				await expect(
					page.locator('.multi-step-item').nth(1)
				).toHaveClass(/active/, {timeout: 1000});
			}).toPass();
		}
	);

	test(
		'Step can be removed and restored via undo',
		{tag: ['@LPD-37578']},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Get the id of Lemon object from the site initializer

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('Lemon')
				)
			).body;

			// Definition for the Stepper fragment

			const stepperFragment = getFragmentDefinition({
				fragmentConfig: {
					numberOfSteps: 3,
				},
				id: getRandomString(),
				key: 'INPUTS-stepper',
			});

			// Create a form with three steps and the Stepper

			const headingId = getRandomString();

			const headingDefinition = getFragmentDefinition({
				id: headingId,
				key: 'BASIC_COMPONENT-heading',
			});

			const buttonId = getRandomString();

			const buttonDefinition = getFragmentDefinition({
				id: buttonId,
				key: 'BASIC_COMPONENT-button',
			});

			const paragraphId = getRandomString();

			const paragraphDefinition = getFragmentDefinition({
				id: paragraphId,
				key: 'BASIC_COMPONENT-paragraph',
			});

			const formDefinition = getFormContainerDefinition({
				id: getRandomString(),
				objectDefinitionClassName,
				pageElements: [stepperFragment],
				steps: [
					[headingDefinition],
					[buttonDefinition],
					[paragraphDefinition],
				],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to edit mode

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			await page
				.getByText('Select a Page Element', {exact: true})
				.waitFor();

			await expect(page.locator('.multi-step-item').nth(0)).toHaveClass(
				/active/,
				{timeout: 1000}
			);

			// Check the first step can't be removed

			await pageEditorPage.selectFragment(headingId);

			await clickAndExpectToBeVisible({
				target: page.locator('.page-editor__topper__title', {
					hasText: 'Step 1',
				}),
				trigger: page.locator('.breadcrumb-link', {hasText: 'Step 1'}),
			});

			await clickAndExpectToBeVisible({
				target: page.getByRole('menuitem', {name: 'Paste'}),
				timeout: 500,
				trigger: page
					.locator('.page-editor__topper__item')
					.getByLabel('Options'),
			});

			await expect(
				page.getByRole('menuitem', {name: 'Remove Step'})
			).not.toBeVisible();

			// Select third step and remove it

			const heading = page.locator(
				'.lfr-layout-structure-item-basic-component-heading'
			);

			const button = page.locator(
				'.lfr-layout-structure-item-basic-component-button'
			);

			const paragraph = page.locator(
				'.lfr-layout-structure-item-basic-component-paragraph'
			);

			const stepButtons = await page.locator('.multi-step-icon').all();
			await stepButtons[2].click();

			await expect(heading).not.toBeVisible();
			await expect(button).not.toBeVisible();
			await expect(paragraph).toBeVisible();

			await pageEditorPage.selectFragment(paragraphId);

			await clickAndExpectToBeVisible({
				target: page.locator('.page-editor__topper__title', {
					hasText: 'Step 3',
				}),
				trigger: page.locator('.breadcrumb-link', {hasText: 'Step 3'}),
			});

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {name: 'Remove Step'}),
				timeout: 500,
				trigger: page
					.locator('.page-editor__topper__item')
					.getByLabel('Options'),
			});

			// Check the step 2 is now active

			await expect(page.locator('.multi-step-item').nth(1)).toHaveClass(
				/active/,
				{timeout: 1000}
			);

			await expect(button).toBeVisible();
			await expect(heading).not.toBeVisible();
			await expect(paragraph).not.toBeVisible();

			// Check undo/redo works

			await pageEditorPage.undoAction();

			await expect(stepButtons[2]).toBeVisible();

			await pageEditorPage.redoAction();

			await expect(stepButtons[2]).not.toBeVisible();

			// Remove the step 2 and check the form is converted to simple

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {name: 'Remove Step'}),
				timeout: 500,
				trigger: page
					.locator('.page-editor__topper__item')
					.getByLabel('Options'),
			});

			await page.getByText('Remove and Convert').waitFor();

			await page.getByText('Remove and Convert').click();

			// Check the stepper is removed as the form is converted to simple

			await expect(stepButtons[0]).not.toBeVisible();

			await expect(heading).toBeVisible();
			await expect(button).not.toBeVisible();
			await expect(paragraph).not.toBeVisible();
		}
	);
});

test.describe('Edit mode language changes', () => {
	test('Input fragments show correct label, help text and placeholder when switching language', async ({
		apiHelpers,
		page,
		pageEditorPage,
		pageManagementSite,
	}) => {

		// Create a page with a Form fragment

		const formId = getRandomString();

		const formDefinition = getFormContainerDefinition({
			id: formId,
		});

		const languageSelectorDefinition = getWidgetDefinition({
			id: getRandomString(),
			widgetName:
				'com_liferay_site_navigation_language_web_portlet_SiteNavigationLanguagePortlet',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				formDefinition,
				languageSelectorDefinition,
			]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		// Go to edit mode

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		await pageEditorPage.mapFormFragment(formId, 'Lemon', ['Lemon Size']);

		const fragmentId = await pageEditorPage.getFragmentId('Text');

		// Add translations to label, help text and placeholder

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Label',
			fragmentId,
			tab: 'General',
			value: 'English Label',
		});

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Help Text',
			fragmentId,
			tab: 'General',
			value: 'English Help Text',
		});

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Placeholder',
			fragmentId,
			tab: 'General',
			value: 'English Placeholder',
		});

		await pageEditorPage.switchLanguage('es-ES');

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Label',
			fragmentId,
			tab: 'General',
			value: 'Spanish Label',
		});

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Show Help Text',
			fragmentId,
			tab: 'General',
			value: true,
		});

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Help Text',
			fragmentId,
			tab: 'General',
			value: 'Spanish Help Text',
		});

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Placeholder',
			fragmentId,
			tab: 'General',
			value: 'Spanish Placeholder',
		});

		// Check that the translations are correctly displayed

		await pageEditorPage.switchLanguage('en-US');

		const englishLabel = page.getByText('English Label');
		const englishHelpText = page.getByText('English Help Text');
		const englishPlaceholder = page.getByPlaceholder('English Placeholder');

		const spanishLabel = page.getByText('Spanish Label');
		const spanishHelpText = page.getByText('Spanish Help Text');
		const spanishPlaceholder = page.getByPlaceholder('Spanish Placeholder');

		await expect(englishLabel).toBeVisible();
		await expect(englishHelpText).toBeVisible();
		await expect(englishPlaceholder).toBeVisible();

		await pageEditorPage.switchLanguage('es-ES');

		await expect(spanishLabel).toBeVisible();
		await expect(spanishHelpText).toBeVisible();
		await expect(spanishPlaceholder).toBeVisible();

		// Check the translations in the view mode

		await pageEditorPage.publishPage();

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'español-España'}),
			trigger: page.getByTitle('Select a Language', {exact: true}),
		});

		await expect(spanishLabel).toBeVisible();
		await expect(spanishHelpText).toBeVisible();
		await expect(spanishPlaceholder).toBeVisible();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'english-United States'}),
			trigger: page.getByTitle('Seleccionar un idioma', {exact: true}),
		});

		await expect(englishLabel).toBeVisible();
		await expect(englishHelpText).toBeVisible();
		await expect(englishPlaceholder).toBeVisible();
	});
});

test.describe('Edit mode form errors', () => {
	async function assertWarningMessage(
		headingMessage: string,
		page: Page,
		pageEditorPage: PageEditorPage,
		warningMessage: string
	) {
		await clickAndExpectToBeVisible({
			target: page.getByRole('heading', {name: headingMessage}),
			timeout: 2000,
			trigger: pageEditorPage.publishButton,
		});

		await expect(page.getByText(warningMessage)).toBeVisible();

		await clickAndExpectToBeHidden({
			target: page.getByRole('heading', {name: headingMessage}),
			timeout: 2000,
			trigger: page.getByRole('button', {name: 'Cancel'}),
		});
	}

	test(
		'Can only drop form fragments inside a mapped form container except for Localization Select',
		{
			tag: ['@LPS-149984', '@LPS-157740'],
		},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Create a content page

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition(),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to edit mode

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			// Assert form fragments can only be dropped inside a mapped form container

			await pageEditorPage.goToSidebarTab('Components');

			const header = page.getByRole('menuitem', {
				exact: true,
				name: 'Form Components',
			});

			await expandSection(header);

			await page.getByLabel(`Add Textarea`).focus();

			await page.keyboard.press('Enter');

			await waitForAlert(
				page,
				'Error:This form component can only be placed inside a mapped form container.',
				{type: 'danger'}
			);

			// Assert that Localization select can be added outside a form container

			await pageEditorPage.addFragment(
				'Form Components',
				'Localization Select'
			);

			await expect(
				page.locator('.lfr-layout-structure-item-localization-select')
			).toBeVisible();

			// Assert form fragments cannot be placed inside an unmapped form container

			await pageEditorPage.addFragment(
				'Form Components',
				'Form Container'
			);

			await pageEditorPage.addFragment(
				'Form Components',
				'Stepper',
				page.locator('.page-editor__form .page-editor__container')
			);

			await waitForAlert(
				page,
				'Error:Fragments cannot be placed inside an unmapped form container.',
				{type: 'danger'}
			);

			// Publish the page and check the localization selector is visible

			await pageEditorPage.publishPage();

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await expect(
				page.getByLabel('Select a language, current language:')
			).toBeVisible();
		}
	);

	test(
		'Show a warning message when there is a form with unmapped input fragments, hidden required input fragments, missing required input fragments, hidden submit button or missing submit button',
		{tag: ['@LPS-150278', '@LPS-157998']},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Create a new object definition with a required field

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {body: objectDefinition} =
				await objectDefinitionAPIClient.postObjectDefinition({
					active: true,
					externalReferenceCode: 'studentERC',
					label: {
						en_US: 'Student',
					},
					name: 'Student',
					objectFields: [
						{
							DBType: 'String',
							businessType: 'Text',
							externalReferenceCode: 'nameERC',
							indexed: true,
							indexedAsKeyword: true,
							label: {
								en_US: 'Name',
							},
							localized: false,
							name: 'name',
							required: true,
						},
						{
							DBType: 'Integer',
							businessType: 'Integer',
							externalReferenceCode: 'ageERC',
							indexed: true,
							indexedAsKeyword: false,
							indexedLanguageId: '',
							label: {
								en_US: 'Age',
							},
							name: 'age',
							required: false,
						},
					],
					pluralLabel: {
						en_US: 'Students',
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

			const textContainerId = getRandomString();
			const textContainerDefinition = getContainerDefinition({
				id: textContainerId,
			});

			const numericContainerId = getRandomString();
			const numericContainerDefinition = getContainerDefinition({
				id: numericContainerId,
			});

			const submitContainerId = getRandomString();
			const submitContainerDefinition = getContainerDefinition({
				id: submitContainerId,
			});

			const formId = getRandomString();
			const formDefinition = getFormContainerDefinition({
				id: formId,
				objectDefinitionClassName: objectDefinition.className,
				pageElements: [
					textContainerDefinition,
					numericContainerDefinition,
					submitContainerDefinition,
				],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to edit mode

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			// Publish and assert multiple wargning messages

			await assertWarningMessage(
				'Form Errors',
				page,
				pageEditorPage,
				'Submit button is hidden or missing. Your users may not be able to submit the form.'
			);

			await assertWarningMessage(
				'Form Errors',
				page,
				pageEditorPage,
				'One or more required fields are not mapped from the form. A form with missing required fields will not generate a valid entry.'
			);

			// Add text input fragment and map to required field

			await pageEditorPage.addFragment(
				'Form Components',
				'Textarea',
				page.locator(`.lfr-layout-structure-item-${textContainerId}`)
			);

			const textareaId = await pageEditorPage.getFragmentId('Textarea');

			await pageEditorPage.selectFragment(textareaId);

			await page.getByLabel('Field', {exact: true}).selectOption('Name*');

			await pageEditorPage.waitForChangesSaved();

			// Publish and check warning message for missing submit button

			await assertWarningMessage(
				'Submit Button Missing',
				page,
				pageEditorPage,
				'Student form has a hidden or missing submit button. If you continue, your users may not be able to submit the form. Are you sure you want to publish it?'
			);

			// Add submit button

			await pageEditorPage.addFragment(
				'Form Components',
				'Form Button',
				page.locator(`.lfr-layout-structure-item-${submitContainerId}`)
			);

			// Hide submit button container

			await pageEditorPage.hideFragment(submitContainerId);

			// Publish and check warning message for hide submit button

			await assertWarningMessage(
				'Submit Button Missing',
				page,
				pageEditorPage,
				'Student form has a hidden or missing submit button. If you continue, your users may not be able to submit the form. Are you sure you want to publish it?'
			);

			await waitForAlert(
				page,
				'The hidden fragment contained required fields. A form with missing required fields will not generate a valid entry.',
				{type: 'warning'}
			);

			// Show submit button container

			await pageEditorPage.goToSidebarTab('Browser');

			await page.locator(`[data-item-id="${submitContainerId}"]`).click();

			await page.getByLabel('Show Container').click();

			// Add unmapped numeric input fragment

			await pageEditorPage.addFragment(
				'Form Components',
				'Numeric',
				page.locator(`.lfr-layout-structure-item-${numericContainerId}`)
			);

			// Publish and check warning message for unmapped numeric input fragment

			await assertWarningMessage(
				'Fragment Mapping Missing',
				page,
				pageEditorPage,
				'Student form has some fragments not mapped to object fields. Unmapped fragments data will not be stored. Are you sure you want to publish?'
			);

			// Map numeric input fragment

			const numericId = await pageEditorPage.getFragmentId('Numeric');

			await pageEditorPage.selectFragment(numericId);

			await page.getByLabel('Field', {exact: true}).selectOption('Age');

			await pageEditorPage.waitForChangesSaved();

			// Hide text container

			await pageEditorPage.hideFragment(textContainerId);

			// Publish and check warning message for hidden required input fragment

			await assertWarningMessage(
				'Required Fields Hidden',
				page,
				pageEditorPage,
				'Student form contains one or more hidden fragments mapped to required fields. A form with missing required fields will not generate a valid entry. Are you sure you want to publish it?'
			);

			await waitForAlert(
				page,
				'The hidden fragment contained required fields. A form with missing required fields will not generate a valid entry.',
				{type: 'warning'}
			);
		}
	);

	test(
		'Show an error when there is no Submit Button',
		{tag: '@LPS-151754'},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Get the id of Lemon object from the site initializer

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('Lemon')
				)
			).body;

			// Create a page with a Form fragment

			const formId = getRandomString();

			const formDefinition = getFormContainerDefinition({
				id: formId,
				objectDefinitionClassName,
				pageElements: [],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to edit mode

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			// Publish and check it shows Submit Button error

			await clickAndExpectToBeVisible({
				target: page.getByText('Submit Button Missing'),
				timeout: 3000,
				trigger: pageEditorPage.publishButton,
			});
		}
	);

	test(
		'Show errors for empty steps and missing Next/Previous buttons',
		{tag: '@LPD-10727'},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Get the id of Lemon object from the site initializer

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('Lemon')
				)
			).body;

			// Create a forms with three steps, forcing errors

			const nextButton = getFragmentDefinition({
				fragmentConfig: {
					type: 'next',
				},
				id: getRandomString(),
				key: 'INPUTS-submit-button',
			});

			const previousButton = getFragmentDefinition({
				fragmentConfig: {
					type: 'previous',
				},
				id: getRandomString(),
				key: 'INPUTS-submit-button',
			});

			const formDefinition = getFormContainerDefinition({
				id: getRandomString(),
				objectDefinitionClassName,
				steps: [[nextButton], [previousButton], [], [nextButton]],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to edit mode

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			// Publish and check errors

			await clickAndExpectToBeVisible({
				target: page.locator('.modal-title', {hasText: 'Form Errors'}),
				timeout: 3000,
				trigger: pageEditorPage.publishButton,
			});

			await expect(
				page.getByText('Next button is hidden or missing in Step 2')
			).toBeVisible();

			await expect(
				page.getByText('Previous button is hidden or missing in Step 4')
			).toBeVisible();

			await expect(page.getByText('Step 3 is empty')).toBeVisible();
		}
	);

	test(
		'Show	 error message after mapping the Form Container to object when multiple OOTB input fragments are unavailable',
		{tag: '@LPS-158143'},
		async ({
			apiHelpers,
			masterPagesPage,
			page,
			pageEditorPage,
			pageManagementSite,
		}) => {
			const layoutPageTemplateEntryName = getRandomString();

			const masterPage =
				await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addLayoutPageTemplateEntry(
					{
						groupId: pageManagementSite.id,
						name: layoutPageTemplateEntryName,
						type: 'master-layout',
					}
				);

			await masterPagesPage.goto(pageManagementSite.friendlyUrlPath);

			await masterPagesPage.editMaster(layoutPageTemplateEntryName);

			await masterPagesPage.configureAllowedFragments({
				fragmentNames: ['Checkbox', 'Date'],
				mode: 'unselect',
				prefilter: 'Form Components',
			});

			await pageEditorPage.publishPage();

			const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: pageManagementSite.id,
				masterLayoutPlid: masterPage.plid,
				options: {type: 'content'},
				title: getRandomString(),
			});

			// Go to edit mode

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			await pageEditorPage.addFragment(
				'Form Components',
				'Form Container'
			);

			const fragment = pageEditorPage.getFragment(
				await pageEditorPage.getFragmentId('Form Container')
			);

			await fragment
				.getByLabel('Content Type')
				.selectOption('All Fields');

			const fieldsModal = page.frameLocator(
				'iframe[title="Manage Form Fields"]'
			);

			await fieldsModal
				.getByLabel('Select All Items on the Page')
				.check({trial: true});

			await fieldsModal
				.getByLabel('Select All Items on the Page')
				.check();

			await clickAndExpectToBeHidden({
				target: page.locator('.modal-title', {
					hasText: 'Manage Form Fields',
				}),
				trigger: page.locator('.modal-footer').getByText('Save'),
			});

			await expect(page.locator('.alert-danger')).toContainText(
				'Some fragments are missing. Boolean and Date fields cannot have an associated fragment or cannot be available in master.'
			);
		}
	);
});

test.describe('View mode form errors', () => {
	test(
		'Show only the first error message when multiple validation issues happen after submitting a form',
		{
			tag: '@LPS-151402',
		},
		async ({apiHelpers, page, pageManagementSite}) => {

			// Create a new object validation rule

			const objectValidationRuleAPIClient =
				await apiHelpers.buildRestClient(ObjectValidationRuleAPI);

			const {body: objectValidationRule} =
				await objectValidationRuleAPIClient.postObjectDefinitionByExternalReferenceCodeObjectValidationRule(
					getObjectERC('Lemon'),
					{
						active: true,
						engine: 'ddm',
						engineLabel: 'Expression Builder',
						errorLabel: {
							en_US: 'The lemon weight must be greater than 0',
						},
						externalReferenceCode: 'lemon-weight-validation-erc',
						name: {
							en_US: 'Lemon Weight Validation',
						},
						objectDefinitionExternalReferenceCode:
							'lemon-object-erc',
						objectValidationRuleSettings: [
							{
								name: 'outputObjectFieldExternalReferenceCode',
								value: 'lemon-weight-erc' as any,
							},
						],
						outputType: 'partialValidation',
						script: 'isEmpty(lemonWeight) OR lemonWeight > 0',
						system: false,
					}
				);

			// Create a default display page for lemon object

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('Lemon')
				)
			).body;

			const className =
				await apiHelpers.jsonWebServicesClassName.fetchClassName(
					objectDefinitionClassName
				);

			const displayPage =
				await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
					{
						classNameId: className.classNameId,
						groupId: pageManagementSite.id,
						name: getRandomString(),
					}
				);

			await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.markAsDefaultDisplayPageLayoutPageTemplateEntry(
				{
					layoutPageTemplateEntryId:
						displayPage.layoutPageTemplateEntryId,
				}
			);

			// Create a page with a form fragment

			const formId = getRandomString();

			const textInputDefinition1 = getFragmentDefinition({
				fragmentConfig: {
					inputFieldId: 'ObjectField_lemonSize',
				},
				id: getRandomString(),
				key: 'INPUTS-text-input',
			});

			const textInputDefinition2 = getFragmentDefinition({
				fragmentConfig: {
					inputFieldId: 'ObjectField_lemonWeight',
				},
				id: getRandomString(),
				key: 'INPUTS-text-input',
			});

			const submitFragmentDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'INPUTS-submit-button',
			});

			const formDefinition = getFormContainerDefinition({
				id: formId,
				objectDefinitionClassName,
				pageElements: [
					textInputDefinition1,
					textInputDefinition2,
					submitFragmentDefinition,
				],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to view mode

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			// Assert first error message is shown when there are multiple error messages

			const lemonSizeField = page.getByRole('textbox', {
				name: 'Lemon Size',
			});

			const lemonWeightField = page.getByRole('textbox', {
				name: 'Lemon Weight',
			});

			await lemonSizeField.click();

			await page.keyboard.type('a'.repeat(290));

			await lemonWeightField.fill(getRandomString());

			await page.getByText('Submit', {exact: true}).click();

			const formError = page.getByText(
				'Value exceeds maximum length of 280 for field Lemon Size.'
			);

			await expect(formError).toBeVisible();

			await expect(formError).toHaveClass(/alert/);

			await expect(
				page.getByText('The lemon weight must be greater than 0')
			).not.toBeVisible();

			// Assert second error message

			await lemonSizeField.clear();

			await lemonWeightField.fill('-1');

			await page.getByText('Submit', {exact: true}).click();

			await expect(formError).not.toBeVisible();

			await expect(
				page.getByText('The lemon weight must be greater than 0')
			).toBeVisible();

			// Delete the display page

			await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.deleteLayoutPageTemplateEntry(
				{
					layoutPageTemplateEntryId:
						displayPage.layoutPageTemplateEntryId,
				}
			);

			// Delete validation

			await objectValidationRuleAPIClient.deleteObjectValidationRule(
				objectValidationRule.id
			);
		}
	);
});

// Remove when the feature flag LPD-11235 is removed

testWithCKEditor4(
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

test(
	'Check read-only fields',
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

		const richTextToolbarButton = page
			.locator('.rich-text-input', {
				hasText: 'Rich Text (Read Only)',
			})
			.locator('.ck-button')
			.first();

		await expect(richTextToolbarButton).toBeDisabled();

		await expect(
			page.locator('.rich-text-input--disabled')
		).not.toBeAttached();

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

test(
	'Submitted entry status configuration is only visible if the form button is submit',
	{tag: '@LPD-37217'},
	async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {
		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {className: objectDefinitionClassName} = (
			await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
				getObjectERC('Lemon')
			)
		).body;

		const formId = getRandomString();

		const submitFragmentId = getRandomString();

		const submitFragmentDefinition = getFragmentDefinition({
			id: submitFragmentId,
			key: 'INPUTS-submit-button',
		});

		const formDefinition = getFormContainerDefinition({
			id: formId,
			objectDefinitionClassName,
			pageElements: [submitFragmentDefinition],
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([formDefinition]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		await pageEditorPage.selectFragment(submitFragmentId);

		await expect(page.getByLabel('Type', {exact: true})).toHaveValue(
			'submit'
		);

		await expect(
			page.getByLabel('Submitted Entry Status', {exact: true})
		).toBeVisible();

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Type',
			fragmentId: submitFragmentId,
			tab: 'General',
			value: 'Next',
		});

		await expect(
			page.getByLabel('Submitted Entry Status')
		).not.toBeVisible();
	}
);

test.describe('URL Video Previewer Fragment', () => {
	test(
		'Configure URL Video Previewer fragment',
		{
			tag: '@LPD-55079',
		},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Create a page with a form fragment with a URL Video Previewer fragment

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('All Fields')
				)
			).body;

			const videoPreviewerId = getRandomString();

			const videoPreviewerDefinition = getFragmentDefinition({
				fragmentConfig: {
					inputFieldId: 'ObjectField_text',
				},
				id: videoPreviewerId,
				key: 'INPUTS-video-previewer-input',
			});

			const formDefinition = getFormContainerDefinition({
				id: getRandomString(),
				objectDefinitionClassName,
				pageElements: [videoPreviewerDefinition],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to edit mode

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			// Change label

			const inputLabel = page.locator('label', {
				hasText: 'Type your video url here!',
			});

			await expect(inputLabel).not.toBeAttached();

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Label',
				fragmentId: videoPreviewerId,
				tab: 'General',
				value: 'Type your video url here!',
			});

			await expect(inputLabel).toBeAttached();

			// Hide label

			await expect(inputLabel).not.toHaveClass(/sr-only/);

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Show Label',
				fragmentId: videoPreviewerId,
				tab: 'General',
				value: false,
			});

			await expect(inputLabel).toHaveClass(/sr-only/);

			// Show help text

			const videoPreviewerFragment = page.locator(
				'.video-previewer-input'
			);

			await expect(videoPreviewerFragment).not.toContainText(
				/Add your help text here./
			);

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Show Help Text',
				fragmentId: videoPreviewerId,
				tab: 'General',
				value: true,
			});

			await expect(videoPreviewerFragment).toContainText(
				/Add your help text here./
			);

			// Add placeholder

			await expect(page.getByPlaceholder('https://')).not.toBeAttached();

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Placeholder',
				fragmentId: videoPreviewerId,
				tab: 'General',
				value: 'https://',
			});

			await expect(page.getByPlaceholder('https://')).toBeAttached();

			// Show characters count

			await expect(page.getByText('0 / 280')).toHaveClass(/sr-only/);

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Show Characters Count',
				fragmentId: videoPreviewerId,
				tab: 'General',
				value: true,
			});

			await expect(page.getByText('0 / 280')).not.toHaveClass(/sr-only/);

			// Change preview label

			const previewLabel = page.locator('label', {
				hasText: 'This is my video preview',
			});

			await expect(previewLabel).not.toBeAttached();

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Preview Label',
				fragmentId: videoPreviewerId,
				tab: 'General',
				value: 'This is my video preview',
			});

			await expect(previewLabel).toBeAttached();

			// Hide preview label

			await expect(previewLabel).not.toHaveClass(/sr-only/);

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Show Preview Label',
				fragmentId: videoPreviewerId,
				tab: 'General',
				value: false,
			});

			await expect(previewLabel).toHaveClass(/sr-only/);
		}
	);

	test(
		'Preview a video taking into account the localizable field',
		{
			tag: '@LPD-55079',
		},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Create a page with a form fragment with a URL Video Previewer fragment

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {className: objectDefinitionClassName} = (
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					getObjectERC('All Fields')
				)
			).body;

			const localizationSelectDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'localization-select',
			});

			const submitButtonDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'INPUTS-submit-button',
			});

			const videoPreviewerId = getRandomString();

			const videoPreviewerDefinition = getFragmentDefinition({
				fragmentConfig: {
					inputFieldId: 'ObjectField_text',
				},
				id: videoPreviewerId,
				key: 'INPUTS-video-previewer-input',
			});

			const formDefinition = getFormContainerDefinition({
				id: getRandomString(),
				objectDefinitionClassName,
				pageElements: [
					localizationSelectDefinition,
					videoPreviewerDefinition,
					submitButtonDefinition,
				],
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to view mode and check the preview for the default language

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			const input = page.getByLabel('Text');

			const fillAndBlurInput = async (value: string) => {
				await input.waitFor();
				await input.fill(value);
				await input.blur();
			};

			const iframe = page.locator('.video-preview iframe');

			await expect(iframe).not.toBeAttached();

			await fillAndBlurInput(
				'https://www.youtube.com/watch?v=2EPZxIC5ogU'
			);

			await expect(iframe).toBeAttached();

			await expect(iframe).toHaveAttribute(
				'title',
				'Life at Liferay - A Look into Liferay Culture'
			);

			// Fill the form for other language

			const translationSelector = page.getByLabel(
				'Select a language, current language:'
			);

			const japaneseOption = page
				.getByRole('option')
				.filter({hasText: 'ja-JP'});

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: japaneseOption,
				trigger: translationSelector,
			});

			await fillAndBlurInput(
				'https://www.youtube.com/watch?v=nlNUEBl53BI'
			);

			await expect(iframe).toHaveAttribute(
				'title',
				/Hello! from Liferay Japan/
			);

			// Change the language again and check that the video updates

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('option').filter({hasText: 'en-US'}),
				trigger: translationSelector,
			});

			await expect(iframe).toHaveAttribute(
				'title',
				'Life at Liferay - A Look into Liferay Culture'
			);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: japaneseOption,
				trigger: translationSelector,
			});

			await expect(iframe).toHaveAttribute(
				'title',
				/Hello! from Liferay Japan/
			);

			// Go to edit mode and change the video title

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Video Title',
				fragmentId: videoPreviewerId,
				tab: 'General',
				value: 'This is my super cool video',
			});

			await pageEditorPage.publishPage();

			// Check the video title

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await fillAndBlurInput(
				'https://www.youtube.com/watch?v=2EPZxIC5ogU'
			);

			await expect(iframe).toHaveAttribute(
				'title',
				'This is my super cool video'
			);

			// Clear the video preview

			await fillAndBlurInput('');

			await expect(iframe).not.toBeAttached();
		}
	);
});

/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinitionAPI,
	ObjectValidationRuleAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

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
import {getObjectERC} from '../../setup/page-management-site/main/utils/getObjectERC';
import {goToObjectEntity} from '../../setup/page-management-site/main/utils/goToObjectEntity';
import getFormContainerDefinition from '../main/utils/getFormContainerDefinition';
import getFragmentDefinition from '../main/utils/getFragmentDefinition';
import getPageDefinition from '../main/utils/getPageDefinition';

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
		'LPD-60546': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	fragmentsPagesTest,
	isolatedSiteTest,
	loginTest(),
	masterPagesPagesTest,
	objectPagesTest,
	pageEditorPagesTest,
	pageManagementSiteTest
);

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

			await page.locator('input[name="ObjectField_date"]').click();

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

			await page.locator('input[name="ObjectField_date"]').click();

			await page.keyboard.type('01/07/2023');

			// Edit date and time

			await page.locator('input[name="ObjectField_dateAndTime"]').click();

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

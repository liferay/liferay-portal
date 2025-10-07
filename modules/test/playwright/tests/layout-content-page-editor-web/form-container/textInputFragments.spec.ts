/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinitionAPI,
	ObjectValidationRuleAPI,
} from '@liferay/object-admin-rest-client-js';
import {Locator, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../fixtures/displayPageTemplatesPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageManagementSiteTest} from '../../../fixtures/pageManagementSiteTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {getObjectERC} from '../../setup/page-management-site/main/utils/getObjectERC';
import getFormContainerDefinition from '../main/utils/getFormContainerDefinition';
import getFragmentDefinition from '../main/utils/getFragmentDefinition';
import getPageDefinition from '../main/utils/getPageDefinition';

const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
		'LPD-17564': {enabled: true},
		'LPD-21926': {enabled: true},
		'LPD-32050': {enabled: true},
		'LPD-60546': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	loginTest(),
	pageEditorPagesTest,
	pageManagementSiteTest
);

const testWithCKEditor4 = mergeTests(
	test,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
		'LPD-32050': {enabled: true},
		'LPS-178052': {enabled: true},
	})
);

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

			const textareaInput = page.locator(
				'[name="ObjectField_lemonHistory"]'
			);

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

	test(
		'Check required errors for different Rich Text field types',
		{tag: ['@LPD-65544']},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Create a new object 'Article' with the following rich text fields:
			// - A required field localizable (Name)
			// - A required field not localizable (Content)
			// - A field not localizable (Summary)

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {body: objectDefinition} =
				await objectDefinitionAPIClient.postObjectDefinition({
					enableLocalization: true,
					externalReferenceCode: 'article-erc',
					label: {
						en_US: 'Article',
					},
					name: 'Article',
					objectFields: [
						{
							DBType: 'Clob',
							businessType: 'RichText',
							externalReferenceCode: 'article-name-erc',
							indexed: true,
							indexedAsKeyword: false,
							label: {
								en_US: 'Name',
							},
							localized: true,
							name: 'name',
							required: true,
						},
						{
							DBType: 'Clob',
							businessType: 'RichText',
							externalReferenceCode: 'article-content-erc',
							indexed: true,
							indexedAsKeyword: false,
							label: {
								en_US: 'Content',
							},
							name: 'content',
							required: true,
						},

						{
							DBType: 'Clob',
							businessType: 'RichText',
							externalReferenceCode: 'article-summary-erc',
							indexed: true,
							indexedAsKeyword: false,
							label: {
								en_US: 'Summary',
							},
							name: 'summary',
						},
					],
					pluralLabel: {
						en_US: 'Articles',
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

			// Create a page with a form mapped to 'Article'

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

			await pageEditorPage.mapFormFragment(formId, 'Article', 'all', {
				addLocalizationSelect: true,
			});

			// Mark as required the summary field and publish the page

			const fragmentId = await pageEditorPage.getFragmentId(
				'Rich Text',
				2
			);

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Mark as Required',
				fragmentId,
				tab: 'General',
				value: true,
			});

			await pageEditorPage.publishPage();

			// Go to the view mode, submit the form and check that there are 3 validation errors

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await page.getByText('Submit', {exact: true}).click();

			const error = page.getByText('This field is required');

			await expect(error).toHaveCount(3);

			// Change the translation language to spanish and fill the Name field

			const translationSelector = page.getByLabel(
				'Select a language, current language:'
			);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('option').filter({hasText: 'es-ES'}),
				trigger: translationSelector,
			});

			const nameEditor = page.getByLabel(/Name. Press/);

			await nameEditor.click();

			await page.keyboard.type('How to water the plants?');
			await page.keyboard.press('Tab');

			// Check that no errors disappear when the default language is not filled in

			await expect(error).toHaveCount(3);

			// Change the translation language to the default language and fill the Name field

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('option').filter({hasText: 'en-US'}),
				trigger: translationSelector,
			});

			await nameEditor.click();

			await page.keyboard.type('How to water the plants?');
			await page.keyboard.press('Tab');

			await expect(error).toHaveCount(2);

			// Fill the Content field and check the validation errors

			await page.getByLabel(/Content. Press/).click();

			await page.keyboard.type('To water the plants, first check ...');
			await page.keyboard.press('Tab');

			await expect(error).toHaveCount(1);

			// Submit the form and check that the input is focused

			await page.getByText('Submit').click();

			await expect(page.getByLabel(/Summary. Press/)).toHaveClass(
				/ck-focused/
			);

			await expect(
				page.getByLabel(/Summary. Press/)
			).toHaveAccessibleDescription('This field is required.');

			// Fill the Summary field and check the validation errors

			await page.keyboard.type('In summary, plants need water ...');
			await page.keyboard.press('Tab');

			await expect(error).not.toBeAttached();

			// Submit the form

			await page.getByText('Submit').click();

			await expect(
				page.getByText(
					'Thank you. Your information was successfully received.'
				)
			).toBeVisible();
		}
	);
});

testWithCKEditor4.describe('Rich Text Fragment with CKEditor 4', () => {
	testWithCKEditor4(
		'Check required errors for different Rich Text field types with CKEditor 4',
		{tag: ['@LPD-65544']},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {
			const getEditor = (label: Locator) => {
				return label
					.locator('..')
					.frameLocator('iframe')
					.locator('[contenteditable="true"]');
			};

			// Create a new object 'Article' with the following rich text fields:
			// - A required field localizable (Name)
			// - A required field not localizable (Content)
			// - A field not localizable (Summary)

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {body: objectDefinition} =
				await objectDefinitionAPIClient.postObjectDefinition({
					enableLocalization: true,
					externalReferenceCode: 'article-erc',
					label: {
						en_US: 'Article',
					},
					name: 'Article',
					objectFields: [
						{
							DBType: 'Clob',
							businessType: 'RichText',
							externalReferenceCode: 'article-name-erc',
							indexed: true,
							indexedAsKeyword: false,
							label: {
								en_US: 'Name',
							},
							localized: true,
							name: 'name',
							required: true,
						},
						{
							DBType: 'Clob',
							businessType: 'RichText',
							externalReferenceCode: 'article-content-erc',
							indexed: true,
							indexedAsKeyword: false,
							label: {
								en_US: 'Content',
							},
							name: 'content',
							required: true,
						},

						{
							DBType: 'Clob',
							businessType: 'RichText',
							externalReferenceCode: 'article-summary-erc',
							indexed: true,
							indexedAsKeyword: false,
							label: {
								en_US: 'Summary',
							},
							name: 'summary',
						},
					],
					pluralLabel: {
						en_US: 'Articles',
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

			// Create a page with a form mapped to 'Article'

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

			await pageEditorPage.mapFormFragment(formId, 'Article', 'all', {
				addLocalizationSelect: true,
			});

			// Mark as required the summary field and publish the page

			const fragmentId = await pageEditorPage.getFragmentId(
				'Rich Text',
				2
			);

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Mark as Required',
				fragmentId,
				tab: 'General',
				value: true,
			});

			await pageEditorPage.publishPage();

			// Go to the view mode, submit the form and check that there are 3 validation errors

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			const nameEditor = getEditor(
				page.locator('label', {hasText: /Name/})
			);

			await nameEditor.waitFor();

			await page.getByText('Submit', {exact: true}).click();

			const error = page.getByText('This field is required');

			await expect(error).toHaveCount(3);

			// Change the translation language to spanish and fill the Name field

			const translationSelector = page.getByLabel(
				'Select a language, current language:'
			);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('option').filter({hasText: 'es-ES'}),
				trigger: translationSelector,
			});

			await nameEditor.click();

			await page.keyboard.type('How to water the plants?');
			await page.keyboard.press('Tab');

			// Check that no errors disappear when the default language is not filled in

			await expect(error).toHaveCount(3);

			// Change the translation language to the default language and fill the Name field

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('option').filter({hasText: 'en-US'}),
				trigger: translationSelector,
			});

			await nameEditor.click();

			await page.keyboard.type('How to water the plants?');
			await page.keyboard.press('Tab');

			await expect(error).toHaveCount(2);

			// Fill the Content field and check the validation errors

			await getEditor(
				page.locator('label', {hasText: /Content/})
			).click();

			await page.keyboard.type('To water the plants, first check ...');
			await page.keyboard.press('Tab');

			await expect(error).toHaveCount(1);

			// Submit the form and check that the input is focused

			await page.getByText('Submit').click();

			await expect(
				page.getByLabel('Rich Text Editor').nth(2)
			).toHaveClass(/cke_focus/);

			// Fill the Summary field and check the validation errors

			await page.keyboard.type('In summary, plants need water ...');
			await page.keyboard.press('Tab');

			await expect(error).not.toBeAttached();

			// Submit the form

			await page.getByText('Submit').click();

			await expect(
				page.getByText(
					'Thank you. Your information was successfully received.'
				)
			).toBeVisible();
		}
	);
});

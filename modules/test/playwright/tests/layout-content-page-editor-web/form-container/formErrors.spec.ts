/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinitionAPI,
	ObjectValidationRuleAPI,
} from '@liferay/object-admin-rest-client-js';
import {Page, expect, mergeTests} from '@playwright/test';

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
import {PageEditorPage} from '../../../pages/layout-content-page-editor-web/PageEditorPage';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {expandSection} from '../../../utils/expandSection';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {getObjectERC} from '../../setup/page-management-site/main/utils/getObjectERC';
import getContainerDefinition from '../main/utils/getContainerDefinition';
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
		'Show error message after mapping the Form Container to object when multiple OOTB input fragments are unavailable',
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

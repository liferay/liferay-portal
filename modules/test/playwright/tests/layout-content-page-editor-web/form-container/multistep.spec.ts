/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectDefinitionAPI} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageManagementSiteTest} from '../../../fixtures/pageManagementSiteTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {getObjectERC} from '../../setup/page-management-site/main/utils/getObjectERC';
import getFormContainerDefinition from '../main/utils/getFormContainerDefinition';
import getFragmentDefinition from '../main/utils/getFragmentDefinition';
import getPageDefinition from '../main/utils/getPageDefinition';

const test = mergeTests(
	apiHelpersTest,
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

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

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

		await expect(page.getByLabel('Form Type', {exact: true})).toHaveValue(
			'multistep'
		);

		// Change to simple and check stepper is removed

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Form Type',
			fragmentId: formId,
			tab: 'General',
			value: 'simple',
		});

		await page.getByRole('button', {name: 'Continue'}).click();

		await expect(page.locator('[data-name="Stepper"]')).not.toBeVisible();
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

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

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

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Check steps are not displayed

		await expect(page.locator('.page-editor__form-step')).toHaveCount(0);

		// Change to Multistep and check first step is displayed

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Form Type',
			fragmentId: formId,
			tab: 'General',
			value: 'Multistep',
		});

		await expect(page.locator('.page-editor__form-step')).toHaveCount(2);

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

		await expect(page.locator('.page-editor__form-step')).toHaveCount(3);
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

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

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

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

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

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

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

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

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

		await expect(page.getByLabel('Form Type', {exact: true})).toHaveValue(
			'multistep'
		);

		// Undo the action

		await pageEditorPage.undoButton.click();

		await pageEditorPage.waitForChangesSaved();

		// Check Stepper disappeared and type changed to Simple again

		await expect(page.getByLabel('Form Type', {exact: true})).toHaveValue(
			'simple'
		);

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

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

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

		await expect(page.getByLabel('Form Type', {exact: true})).toHaveValue(
			'multistep'
		);

		// Undo the action

		await pageEditorPage.undoButton.click();

		await pageEditorPage.waitForChangesSaved();

		// Check Stepper disappeared and type changed to Simple again

		await expect(page.getByLabel('Form Type', {exact: true})).toHaveValue(
			'simple'
		);

		const secondForm = page
			.locator('.page-editor__form .page-editor__container')
			.last();

		await expect(secondForm.locator('.multi-step-nav')).not.toBeVisible();

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

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

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

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

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

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		await page.getByText('Select a Page Element', {exact: true}).waitFor();

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

			await expect(page.locator('.multi-step-item').nth(1)).toHaveClass(
				/active/,
				{timeout: 1000}
			);
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

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		await page.getByText('Select a Page Element', {exact: true}).waitFor();

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

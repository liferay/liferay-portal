/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../fixtures/featureFlagsTest';
import {loginTest} from '../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../fixtures/pageEditorPagesTest';
import {pageManagementSiteTest} from '../../fixtures/pageManagementSiteTest';
import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../utils/getRandomString';
import {waitForAlert} from '../../utils/waitForAlert';
import {
	LEMON_OBJECT_ERC,
	POTATO_OBJECT_ERC,
} from '../setup/page-management-site/constants';
import {deleteObjectEntries} from '../setup/page-management-site/utils/deleteObjectEntries';
import getFormContainerDefinition from './utils/getFormContainerDefinition';
import getFragmentDefinition from './utils/getFragmentDefinition';
import getPageDefinition from './utils/getPageDefinition';
import getWidgetDefinition from './utils/getWidgetDefinition';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-10727': true,
		'LPS-178052': true,
	}),
	loginTest(),
	pageEditorPagesTest,
	pageManagementSiteTest
);

test.describe('Form Configuration', () => {
	test(
		'Show success message only one time',
		{
			tag: '@LPD-37435',
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
			]);

			await pageEditorPage.selectFragment(formId);

			await page
				.getByLabel('Success Action', {exact: true})
				.selectOption({label: 'Stay in Page'});

			await page
				.getByLabel('Show Notification After Submit', {exact: true})
				.check();

			await page
				.getByLabel('Success Notification Text', {exact: true})
				.fill('Request received correctly');

			await pageEditorPage.publishPage();

			// Go to view mode and check picklist values

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

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

			// Delete Lemon entry

			await deleteObjectEntries({
				entityName: 'Lemons',
				page,
				siteUrl: pageManagementSite.friendlyUrlPath,
			});
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

		// Go to edit mode and map the form to Lemon object, specifically to the "Lemon Size" field

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
			.locator('label')
			.filter({hasText: 'Lemon Weight'})
			.locator('svg.reference-mark');

		await expect(requireIcon).toBeAttached();

		// Check Label and Show Label fields

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Label',
			fragmentId: numericInputId,
			tab: 'General',
			value: 'Lemon weight in grams',
		});

		const label = page
			.locator('label')
			.filter({hasText: 'Lemon weight in grams'});

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

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		await pageEditorPage.mapFormFragment(formId, 'Lemon', ['Lemon Weight']);

		await pageEditorPage.publishPage();

		// Go to view mode and check that the input type is numeric and has the attributes max and min

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		const lemonWeightInput = page.getByLabel('Lemon Weight');

		expect(lemonWeightInput).toHaveAttribute('type', 'number');
		expect(lemonWeightInput).toHaveAttribute('max');
		expect(lemonWeightInput).toHaveAttribute('min');

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

		await deleteObjectEntries({
			entityName: 'Lemons',
			page,
			siteUrl: pageManagementSite.friendlyUrlPath,
		});
	});
});

test.describe('Picklist input field', () => {
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

		await page.getByLabel('Lemon Basket to Lemons').click();

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

			await page.getByLabel('Material').click();

			await page.getByText('carton').click();

			expect(page.getByLabel('Material')).toHaveValue('Carton');
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

			expect(
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

			const {id: objectDefinitionId} =
				await apiHelpers.objectAdmin.getObjectDefinitionByExternalReferenceCode(
					LEMON_OBJECT_ERC
				);

			// Create a page with a Form fragment with a Stepper fragment

			const stepperId = getRandomString();

			const stepperFragment = getFragmentDefinition({
				id: stepperId,
				key: 'INPUTS-stepper',
			});

			const formId = getRandomString();

			const formDefinition = getFormContainerDefinition({
				id: formId,
				objectDefinitionId,
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
				fieldLabel: 'Show Steps Titles',
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

			const {id: objectDefinitionId} =
				await apiHelpers.objectAdmin.getObjectDefinitionByExternalReferenceCode(
					LEMON_OBJECT_ERC
				);

			// Create a page with a form container

			const formId = getRandomString();

			const formDefinition = getFormContainerDefinition({
				id: formId,
				objectDefinitionId,
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
		{tag: '@LPD-10727'},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Get the id of Lemon object from the site initializer

			const {id: objectDefinitionId} =
				await apiHelpers.objectAdmin.getObjectDefinitionByExternalReferenceCode(
					LEMON_OBJECT_ERC
				);

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
				objectDefinitionId,
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
		}
	);

	test(
		'Can change step with the form button fragment',
		{tag: '@LPD-10727'},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Get the id of Lemon object from the site initializer

			const {id: objectDefinitionId} =
				await apiHelpers.objectAdmin.getObjectDefinitionByExternalReferenceCode(
					LEMON_OBJECT_ERC
				);

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
				objectDefinitionId,
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

			const {id: objectDefinitionId} =
				await apiHelpers.objectAdmin.getObjectDefinitionByExternalReferenceCode(
					LEMON_OBJECT_ERC
				);

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
				objectDefinitionId,
				pageElements: [stepperFragment1],
				steps: [[headingDefinition], []],
			});

			const form2Definition = getFormContainerDefinition({
				id: getRandomString(),
				objectDefinitionId,
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

			const {id: objectDefinitionId} =
				await apiHelpers.objectAdmin.getObjectDefinitionByExternalReferenceCode(
					LEMON_OBJECT_ERC
				);

			// Create a form with a Stepper

			const stepperId = getRandomString();

			const stepperFragment = getFragmentDefinition({
				id: stepperId,
				key: 'INPUTS-stepper',
			});

			const formId = getRandomString();

			const formDefinition = getFormContainerDefinition({
				id: formId,
				objectDefinitionId,
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

			// Check changing number of stepps in Form affects the Stepper

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

			const {id: objectDefinitionId} =
				await apiHelpers.objectAdmin.getObjectDefinitionByExternalReferenceCode(
					LEMON_OBJECT_ERC
				);

			// Create a page with a Form fragment

			const formId = getRandomString();

			const formDefinition = getFormContainerDefinition({
				id: formId,
				objectDefinitionId,
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

			const {id: objectDefinitionId} =
				await apiHelpers.objectAdmin.getObjectDefinitionByExternalReferenceCode(
					LEMON_OBJECT_ERC
				);

			// Create a page containing a multistep form with a stepper and a simple form

			const stepperId = getRandomString();

			const stepperFragment = getFragmentDefinition({
				id: stepperId,
				key: 'INPUTS-stepper',
			});

			const firstFormId = getRandomString();

			const firstFormDefinition = getFormContainerDefinition({
				id: firstFormId,
				objectDefinitionId,
				pageElements: [stepperFragment],
				steps: [[]],
			});

			const secondFormId = getRandomString();

			const secondFormDefinition = getFormContainerDefinition({
				id: secondFormId,
				objectDefinitionId,
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

			const stepper = pageEditorPage.getFragment(stepperId);

			const secondForm = page
				.locator('.page-editor__form .page-editor__container')
				.last();

			await stepper.dragTo(
				secondForm.locator('.page-editor__no-fragments-state__message')
			);

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

			await expect(
				secondForm.locator('.multi-step-nav')
			).not.toBeVisible();
		}
	);

	test(
		'Correctly handle multistep form errors in view mode',
		{tag: '@LPD-10727'},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Get the id of Potato object from the site initializer

			const {id: objectDefinitionId} =
				await apiHelpers.objectAdmin.getObjectDefinitionByExternalReferenceCode(
					POTATO_OBJECT_ERC
				);

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
				objectDefinitionId,
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

			const field = page.getByLabel('Potato Origin');

			await submitForm();

			await field.waitFor();

			// Fill field with incorrect value, submit and check it shows error

			await field.fill('Madrid');

			await submitForm();

			await page
				.getByText('Potato Origin should be Canary Islands')
				.waitFor();

			// Fill field with correct value, submit and check it submits

			await page.getByLabel('Potato Origin').fill('Canary Islands');

			await submitForm();

			await expect(
				page.getByText('Your information was successfully received')
			).toBeVisible();
		}
	);
});

test.describe('Edit mode language changes', () => {
	test('Input fragments show correct label when switching language', async ({
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

		await pageEditorPage.mapFormFragment(formId, 'Lemon', [
			'Lemon Size',
			'Lemon Basket to Lemons',
		]);

		const fragmentId = await pageEditorPage.getFragmentId('Text');

		// Add translation to label

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Label',
			fragmentId,
			tab: 'General',
			value: 'English Label',
		});

		await pageEditorPage.switchLanguage('es-ES');

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Label',
			fragmentId,
			tab: 'General',
			value: 'Spanish Label',
		});

		// Check labels are correctly displayed

		await pageEditorPage.switchLanguage('en-US');

		await expect(page.getByLabel('English Label')).toBeVisible();

		await pageEditorPage.switchLanguage('es-ES');

		await expect(page.getByLabel('Spanish Label')).toBeVisible();
	});
});

test.describe('Edit mode form errors', () => {
	test(
		'Show an error when there is no Submit Button',
		{tag: '@LPS-151754'},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Get the id of Lemon object from the site initializer

			const {id: objectDefinitionId} =
				await apiHelpers.objectAdmin.getObjectDefinitionByExternalReferenceCode(
					LEMON_OBJECT_ERC
				);

			// Create a page with a Form fragment

			const formId = getRandomString();

			const formDefinition = getFormContainerDefinition({
				id: formId,
				objectDefinitionId,
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

			const {id: objectDefinitionId} =
				await apiHelpers.objectAdmin.getObjectDefinitionByExternalReferenceCode(
					LEMON_OBJECT_ERC
				);

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
				objectDefinitionId,
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
});

/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinition,
	ObjectDefinitionAPI,
	ObjectField,
	ObjectFieldAPI,
	ObjectRelationshipAPI,
	ObjectValidationRuleAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {editObjectDefinitionPagesTest} from '../../../fixtures/editObjectDefinitionPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {scriptManagementPagesTest} from '../../../fixtures/scriptManagementPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import getFormContainerDefinition from '../../layout-content-page-editor-web/main/utils/getFormContainerDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';

export const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	editObjectDefinitionPagesTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	objectPagesTest,
	pageEditorPagesTest,
	scriptManagementPagesTest,
	journalPagesTest
);

test.describe('Object Expression Builder Validation', () => {
	test('can add valid entry when validation is set to full validation', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields: [
					{
						DBType: 'Double',
						businessType: 'Decimal',
						label: {en_US: 'Decimal'},
						name: 'decimal',
						required: false,
					},
				] as Partial<ObjectField>[],
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const objectValidationRuleAPIClient = await apiHelpers.buildRestClient(
			ObjectValidationRuleAPI
		);

		await objectValidationRuleAPIClient.postObjectDefinitionByExternalReferenceCodeObjectValidationRule(
			objectDefinition.externalReferenceCode,
			{
				active: true,
				engine: 'ddm',
				engineLabel: 'Expression Builder',
				errorLabel: {
					en_US: 'This entry is not valid for decimal entry.',
				},
				name: {en_US: 'Custom validation'},
				objectValidationRuleSettings: [],
				outputType: 'fullValidation',
				script: 'decimal == 13.579',
				system: false,
			}
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);

		await page.getByLabel('Decimal', {exact: true}).fill('13.579');

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(viewObjectEntriesPage.successMessage).toBeVisible();

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(page.getByText('13.579')).toBeVisible();
	});

	test('can create an expression builder validation on a custom object', async ({
		apiHelpers,
		editObjectValidationPage,
		modalAddObjectValidationPage,
		objectValidationsPage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields: [
					{
						DBType: 'String',
						businessType: 'Text',
						label: {en_US: 'Custom Field'},
						name: 'customText',
						required: false,
					},
				] as Partial<ObjectField>[],
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectValidationsPage.goto(objectDefinition.label['en_US']);

		await objectValidationsPage.addObjectValidationButton.click();

		await modalAddObjectValidationPage.objectValidationLabelInput.fill(
			'Custom Validation'
		);

		await modalAddObjectValidationPage.fillObjectValidationInputs(
			'Custom Validation',
			'Expression Builder'
		);

		await page.getByRole('link', {name: 'Custom Validation'}).click();

		await expect(
			objectValidationsPage.iframe.getByRole('tab', {
				name: 'Basic Info',
			})
		).toBeVisible();

		await expect(
			objectValidationsPage.iframe.getByRole('heading', {
				name: 'Basic Info',
			})
		).toBeVisible();

		await objectValidationsPage.activeValitionToggle.check();

		await expect(
			objectValidationsPage.iframe.getByRole('heading', {
				name: 'Trigger Event',
			})
		).toBeVisible();

		await objectValidationsPage.iframe
			.getByRole('tab', {
				name: 'Conditions',
			})
			.click();

		await expect(
			objectValidationsPage.iframe.getByRole('heading', {
				name: 'Expression Builder',
			})
		).toBeVisible();

		await page
			.locator('iframe')
			.contentFrame()
			.locator('.CodeMirror-scroll')
			.click();

		await page.keyboard.type("contains(customText, 'Allowed Entry')");

		await objectValidationsPage.errorMessageInput.fill(
			'This entry is not valid.'
		);

		await editObjectValidationPage.saveObjectValidationButton.click();

		await expect(
			page.getByText('The object validation was updated successfully.')
		).toBeVisible();
	});

	test('can create an expression builder validation on a system object', async ({
		apiHelpers,
		editObjectValidationPage,
		modalAddObjectValidationPage,
		objectValidationsPage,
		page,
	}) => {
		await objectValidationsPage.viewObjectDefinitionsPage.goto();

		await objectValidationsPage.viewObjectDefinitionsPage.clickEditObjectDefinitionLink(
			'User'
		);

		await objectValidationsPage.validationTabItem.click();

		await objectValidationsPage.addObjectValidationButton.click();

		await modalAddObjectValidationPage.objectValidationLabelInput.fill(
			'Custom Validation'
		);

		await modalAddObjectValidationPage.fillObjectValidationInputs(
			'Custom Validation',
			'Expression Builder'
		);

		await page.getByRole('link', {name: 'Custom Validation'}).click();

		await expect(
			objectValidationsPage.iframe.getByRole('tab', {
				name: 'Basic Info',
			})
		).toBeVisible();

		await expect(
			objectValidationsPage.iframe.getByRole('heading', {
				name: 'Basic Info',
			})
		).toBeVisible();

		await expect(
			objectValidationsPage.iframe.getByRole('heading', {
				name: 'Trigger Event',
			})
		).toBeVisible();

		await objectValidationsPage.iframe
			.getByRole('tab', {
				name: 'Conditions',
			})
			.click();

		await expect(
			objectValidationsPage.iframe.getByRole('heading', {
				name: 'Expression Builder',
			})
		).toBeVisible();

		await page
			.locator('iframe')
			.contentFrame()
			.locator('.CodeMirror-scroll')
			.click();

		await page.keyboard.type("contains(familyName, 'Allowed Entry')");

		await objectValidationsPage.errorMessageInput.fill(
			'This entry is not valid.'
		);

		await editObjectValidationPage.saveObjectValidationButton.click();

		await expect(
			page.getByText('The object validation was updated successfully.')
		).toBeVisible();

		await test.step('delete the created validation', async () => {
			const objectValidationRuleAPIClient =
				await apiHelpers.buildRestClient(ObjectValidationRuleAPI);

			const objectValidation =
				await objectValidationRuleAPIClient.getObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage(
					'L_USER'
				);

			await objectValidationRuleAPIClient.deleteObjectValidationRule(
				objectValidation.body.items[0].id
			);
		});
	});

	test('can deactivate validation and add previously invalid entry', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields: [
					{
						DBType: 'Clob',
						businessType: 'LongText',
						label: {en_US: 'Long Text'},
						name: 'longText',
						required: false,
					},
				] as Partial<ObjectField>[],
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const objectValidationRuleAPIClient = await apiHelpers.buildRestClient(
			ObjectValidationRuleAPI
		);

		const {body: validationRule} =
			await objectValidationRuleAPIClient.postObjectDefinitionByExternalReferenceCodeObjectValidationRule(
				objectDefinition.externalReferenceCode,
				{
					active: true,
					engine: 'ddm',
					engineLabel: 'Expression Builder',
					errorLabel: {
						en_US: 'This entry is not valid for long text field.',
					},
					name: {en_US: 'Custom validation'},
					objectValidationRuleSettings: [],
					script: "longText == 'Build Incredible Digital Experiences with Liferay DXP'",
					system: false,
				}
			);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);

		await page
			.getByLabel('Long Text', {exact: true})
			.fill('Digital Experiences with Liferay DXP');

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(
			page.getByText('This entry is not valid for long text field.')
		).toBeVisible();

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(
			page.getByText('Build Incredible Digital Experiences')
		).not.toBeVisible();

		await objectValidationRuleAPIClient.putObjectValidationRule(
			validationRule.id,
			{
				...validationRule,
				active: false,
			}
		);

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);

		await page
			.getByLabel('Long Text', {exact: true})
			.fill('Build Incredible Digital Experiences with Liferay DXP');

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(viewObjectEntriesPage.successMessage).toBeVisible();

		await expect(
			page.getByText('This entry is not valid for long text field.')
		).toBeHidden();

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(
			page.getByText('Build Incredible Digital Experiences')
		).toBeVisible();
	});

	test('can display error messages under object fields in a mapped form container when submitting invalid entries', async ({
		apiHelpers,
		page,
		pageEditorPage,
		site,
	}) => {
		let objectDefinition;

		await test.step('create the object definition', async () => {
			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {body} = await objectDefinitionAPIClient.postObjectDefinition(
				{
					active: true,
					externalReferenceCode:
						'objectDefinitionERC' + getRandomInt(),
					label: {
						en_US: 'objectDefinition',
					},
					name: 'ObjectDefinition' + getRandomInt(),
					objectFields: [
						{
							DBType: 'Integer',
							businessType: 'Integer',
							externalReferenceCode: 'intERC',
							indexed: true,
							label: {
								en_US: 'objectFieldLabelInt',
							},
							name: `fieldInt${getRandomInt()}`,
							required: false,
						},
						{
							DBType: 'Double',
							businessType: 'Decimal',
							externalReferenceCode: 'doubleERC',
							indexed: true,
							label: {
								en_US: 'objectFieldLabelDouble',
							},
							name: `fieldDouble${getRandomInt()}`,
							required: false,
						},
					],
					pluralLabel: {
						en_US: 'objectDefinitions',
					},
					portlet: true,
					scope: 'company',
					status: {
						code: 0,
					},
				}
			);

			objectDefinition = body;

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});
		});

		const objectValidationErrorLabelInt = 'Should be greater than 5.';

		const objectValidationErrorLabelDouble = 'Should be less than 6.5.';

		await test.step('Create object field validations', async () => {
			const allObjectDefinitionFields =
				await apiHelpers.objectAdmin.getAllObjectDefinitionsFields(
					objectDefinition.id
				);

			const objectDefinitionValidation = await apiHelpers.buildRestClient(
				ObjectValidationRuleAPI
			);

			const intField = allObjectDefinitionFields.items.find(
				(item: ObjectField) =>
					item.label?.en_US === 'objectFieldLabelInt'
			);

			await objectDefinitionValidation.postObjectDefinitionObjectValidationRule(
				objectDefinition.id,
				{
					active: true,
					engine: 'ddm',
					engineLabel: 'Expression Builder',
					errorLabel: {en_US: objectValidationErrorLabelInt},
					name: {
						en_US: `validation${getRandomInt()}`,
					},
					objectDefinitionExternalReferenceCode:
						objectDefinition.externalReferenceCode,
					objectValidationRuleSettings: [
						{
							name: 'outputObjectFieldExternalReferenceCode',
							value: intField.externalReferenceCode,
						},
					],
					outputType: 'partialValidation',
					script: `${intField.name} > 5`,
					system: false,
				}
			);

			const doubleField = allObjectDefinitionFields.items.find(
				(item: ObjectField) =>
					item.label?.en_US === 'objectFieldLabelDouble'
			);

			await objectDefinitionValidation.postObjectDefinitionObjectValidationRule(
				objectDefinition.id,
				{
					active: true,
					engine: 'ddm',
					engineLabel: 'Expression Builder',
					errorLabel: {en_US: objectValidationErrorLabelDouble},
					name: {
						en_US: `validation${getRandomInt()}`,
					},
					objectDefinitionExternalReferenceCode:
						objectDefinition.externalReferenceCode,
					objectValidationRuleSettings: [
						{
							name: 'outputObjectFieldExternalReferenceCode',
							value: doubleField.externalReferenceCode,
						},
					],
					outputType: 'partialValidation',
					script: `${doubleField.name} < 6.5`,
					system: false,
				}
			);
		});

		let formId: string;
		let layout: Layout;

		await test.step('Create a content page with a form container and go to edit mode', async () => {
			formId = getRandomString();

			const formDefinition = getFormContainerDefinition({
				id: formId,
			});

			const pageName = getRandomString();

			layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: site.id,
				title: pageName,
			});

			await pageEditorPage.goto(layout, site.friendlyUrlPath);
		});

		await test.step('Map the form container to the object and post page', async () => {
			await pageEditorPage.mapFormFragment(
				formId,
				`${objectDefinition.label.en_US}`
			);

			await pageEditorPage.publishPage();
		});

		await test.step('Go to the page, fill the form and check the validations', async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await page
				.getByRole('spinbutton', {name: 'objectFieldLabelDouble'})
				.fill('7.1');

			await page
				.getByRole('spinbutton', {name: 'objectFieldLabelInt'})
				.fill('4');

			await page.getByText('Submit').click();

			await expect(
				page.getByText(objectValidationErrorLabelDouble)
			).toBeVisible();

			await page
				.getByRole('spinbutton', {name: 'objectFieldLabelDouble'})
				.fill('5.2');

			await page.getByText('Submit').click();

			await expect(
				page.getByText(objectValidationErrorLabelInt)
			).toBeVisible();

			await page
				.getByRole('spinbutton', {name: 'objectFieldLabelInt'})
				.fill('6');

			await page.getByText('Submit').click();

			await expect(
				page.getByText('Thank you. Your information')
			).toBeVisible();
		});
	});

	test('can validate specific field with partial validation showing error for invalid entry', async ({
		apiHelpers,
		editObjectValidationPage,
		modalAddObjectValidationPage,
		objectValidationsPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields: [
					{
						DBType: 'String',
						businessType: 'Text',
						label: {en_US: 'Custom Field'},
						name: 'customText',
						required: false,
					},
				] as Partial<ObjectField>[],
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectValidationsPage.goto(objectDefinition.label['en_US']);

		await test.step('create object validation', async () => {
			await objectValidationsPage.addObjectValidationButton.click();

			await modalAddObjectValidationPage.objectValidationLabelInput.fill(
				'Custom Validation'
			);

			await modalAddObjectValidationPage.fillObjectValidationInputs(
				'Custom Validation',
				'Expression Builder'
			);

			await page.getByRole('link', {name: 'Custom Validation'}).click();

			await expect(
				objectValidationsPage.iframe.getByRole('tab', {
					name: 'Basic Info',
				})
			).toBeVisible();

			await expect(
				objectValidationsPage.iframe.getByRole('heading', {
					name: 'Basic Info',
				})
			).toBeVisible();

			await objectValidationsPage.activeValitionToggle.check();

			await expect(
				objectValidationsPage.iframe.getByRole('heading', {
					name: 'Trigger Event',
				})
			).toBeVisible();

			await objectValidationsPage.iframe
				.getByRole('tab', {
					name: 'Conditions',
				})
				.click();

			await expect(
				objectValidationsPage.iframe.getByRole('heading', {
					name: 'Expression Builder',
				})
			).toBeVisible();

			await page
				.locator('iframe')
				.contentFrame()
				.locator('.CodeMirror-scroll')
				.click();

			await page.keyboard.type(`contains(customText, 'Allowed Entry')`);

			await objectValidationsPage.errorMessageInput.fill(
				'This entry is not valid.'
			);

			await editObjectValidationPage.partialValidationInlineRadio.check();

			await editObjectValidationPage.fieldsMandatoryCombobox.click();

			await page
				.frameLocator('iframe')
				.getByRole('option', {name: 'Custom Field'})
				.click();

			await editObjectValidationPage.saveObjectValidationButton.click();

			await expect(
				page.getByText(
					'The object validation was updated successfully.'
				)
			).toBeVisible();
		});

		await test.step('validate invalid entry', async () => {
			await viewObjectEntriesPage.goto(objectDefinition.className);

			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinition.label['en_US']
			);

			await page
				.getByLabel('Custom Field', {exact: true})
				.fill('Decline Entry');

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await expect(
				page.getByText('This entry is not valid.')
			).toBeVisible();

			await viewObjectEntriesPage.goto(objectDefinition.className);

			await expect(page.getByText('Decline Entry')).not.toBeVisible();

			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinition.label['en_US']
			);

			await page
				.getByLabel('Custom Field', {exact: true})
				.fill('Allowed Entry');

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await expect(viewObjectEntriesPage.successMessage).toBeVisible();

			await viewObjectEntriesPage.goto(objectDefinition.className);

			await expect(page.getByText('Allowed Entry')).toBeVisible();
		});
	});

	test('empty state is displayed when no validations are added', async ({
		apiHelpers,
		objectValidationsPage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectValidationsPage.goto(objectDefinition.label['en_US']);

		await expect(page.getByText('No Results Found')).toBeVisible();
	});

	test('entry update succeeds only when passing all validations', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields: [
					{
						DBType: 'String',
						businessType: 'Text',
						label: {en_US: 'Custom Field'},
						name: 'customField',
						required: false,
					},
				] as Partial<ObjectField>[],
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const objectValidationRuleAPIClient = await apiHelpers.buildRestClient(
			ObjectValidationRuleAPI
		);

		await objectValidationRuleAPIClient.postObjectDefinitionByExternalReferenceCodeObjectValidationRule(
			objectDefinition.externalReferenceCode,
			{
				active: true,
				engine: 'ddm',
				engineLabel: 'Expression Builder',
				errorLabel: {en_US: 'Please enter a valid entry.'},
				name: {en_US: 'Custom validation'},
				objectValidationRuleSettings: [],
				script: "customField != 'Decline Entry'",
				system: false,
			}
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{customField: 'Entry Test'},
			'c/' + objectDefinition.name.toLowerCase() + 's'
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.frontendDatasetActions.click();

		await viewObjectEntriesPage.frontendDatasetViewAction.click();

		await page
			.getByLabel('Custom Field', {exact: true})
			.fill('Decline Entry');

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(
			page.getByText('Please enter a valid entry.')
		).toBeVisible();

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(page.getByText('Decline Entry')).not.toBeVisible();

		await viewObjectEntriesPage.frontendDatasetActions.click();

		await viewObjectEntriesPage.frontendDatasetViewAction.click();

		await page
			.getByLabel('Custom Field', {exact: true})
			.fill('Update Entry');

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(viewObjectEntriesPage.successMessage).toBeVisible();

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(page.getByText('Update Entry')).toBeVisible();
	});

	test('error message and script fields are required on validation creation', async ({
		apiHelpers,
		editObjectValidationPage,
		modalAddObjectValidationPage,
		objectValidationsPage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectValidationsPage.goto(objectDefinition.label['en_US']);

		await objectValidationsPage.addObjectValidationButton.click();

		await modalAddObjectValidationPage.fillObjectValidationInputs(
			'Custom Validation',
			'Expression Builder'
		);

		await page.getByRole('link', {name: 'Custom Validation'}).click();

		await objectValidationsPage.iframe
			.getByRole('tab', {name: 'Conditions'})
			.click();

		await editObjectValidationPage.saveObjectValidationButton.click();

		await expect(
			page.getByText('Please fill out all required fields.')
		).toBeVisible();

		await expect(
			page.locator('iframe').contentFrame().getByText('Required')
		).toHaveCount(2);
	});

	test('label field is required when adding a new validation', async ({
		apiHelpers,
		modalAddObjectValidationPage,
		objectValidationsPage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectValidationsPage.goto(objectDefinition.label['en_US']);

		await objectValidationsPage.addObjectValidationButton.click();

		await modalAddObjectValidationPage.fillObjectValidationInputs(
			'',
			'Composite Key'
		);

		await expect(page.getByText('Required')).toBeVisible();
	});

	test('specific error is shown when expression builder syntax is incorrect', async ({
		apiHelpers,
		editObjectValidationPage,
		modalAddObjectValidationPage,
		objectValidationsPage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields: [
					{
						DBType: 'String',
						businessType: 'Text',
						label: {en_US: 'Custom Field'},
						name: 'customField',
						required: false,
					},
				] as Partial<ObjectField>[],
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectValidationsPage.goto(objectDefinition.label['en_US']);

		await objectValidationsPage.addObjectValidationButton.click();

		await modalAddObjectValidationPage.fillObjectValidationInputs(
			'Custom Validation',
			'Expression Builder'
		);

		await page.getByRole('link', {name: 'Custom Validation'}).click();

		await objectValidationsPage.iframe
			.getByRole('tab', {name: 'Conditions'})
			.click();

		await page
			.locator('iframe')
			.contentFrame()
			.locator('.CodeMirror-scroll')
			.click();

		await page.keyboard.type('#');

		await objectValidationsPage.errorMessageInput.fill('Error message');

		await editObjectValidationPage.saveObjectValidationButton.click();

		await expect(
			page.locator('iframe').contentFrame().getByText('Syntax Error')
		).toBeVisible();
	});

	test('updated validation is applied when editing an entry', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields: [
					{
						DBType: 'String',
						businessType: 'Text',
						label: {en_US: 'Custom Field'},
						name: 'customField',
						required: false,
					},
				] as Partial<ObjectField>[],
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const objectValidationRuleAPIClient = await apiHelpers.buildRestClient(
			ObjectValidationRuleAPI
		);

		const {body: validationRule} =
			await objectValidationRuleAPIClient.postObjectDefinitionByExternalReferenceCodeObjectValidationRule(
				objectDefinition.externalReferenceCode,
				{
					active: true,
					engine: 'ddm',
					engineLabel: 'Expression Builder',
					errorLabel: {en_US: 'Please enter a valid entry.'},
					name: {en_US: 'Custom validation'},
					objectValidationRuleSettings: [],
					outputType: 'fullValidation',
					script: "customField == 'Allowed Entry'",
					system: false,
				}
			);

		await apiHelpers.objectEntry.postObjectEntry(
			{customField: 'Allowed Entry'},
			'c/' + objectDefinition.name.toLowerCase() + 's'
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(page.getByText('Allowed Entry')).toBeVisible();

		await objectValidationRuleAPIClient.putObjectValidationRule(
			validationRule.id,
			{
				...validationRule,
				script: "customField != 'Allowed Entry'",
			}
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);

		await page
			.getByLabel('Custom Field', {exact: true})
			.fill('Allowed Entry');

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(
			page.getByText('Please enter a valid entry.')
		).toBeVisible();
	});

	test(
		'Can create validation using oldValue function with Picklist field',
		{tag: '@LPD-78504'},
		async ({apiHelpers}) => {
			const suffix = getRandomString().substring(0, 8);

			const listTypeDefinition =
				await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

			apiHelpers.data.push({
				id: listTypeDefinition.id,
				type: 'listTypeDefinition',
			});

			await apiHelpers.listTypeAdmin.postListTypeEntry({
				key: 'open',
				listTypeDefinitionExternalReferenceCode:
					listTypeDefinition.externalReferenceCode,
				name_i18n: {en_US: 'Open'},
			});

			await apiHelpers.listTypeAdmin.postListTypeEntry({
				key: 'inprogress',
				listTypeDefinitionExternalReferenceCode:
					listTypeDefinition.externalReferenceCode,
				name_i18n: {en_US: 'In Progress'},
			});

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectDefinitionExternalReferenceCode: `Obj${suffix}`,
					objectFields: [
						{
							DBType: 'String',
							businessType: 'Picklist',
							externalReferenceCode: 'customPicklistField',
							indexed: true,
							indexedAsKeyword: false,
							indexedLanguageId: '',
							label: {en_US: 'Custom Picklist Field'},
							listTypeDefinitionExternalReferenceCode:
								listTypeDefinition.externalReferenceCode,
							listTypeDefinitionId: listTypeDefinition.id,
							localized: false,
							name: 'customPicklistField',
							required: false,
							system: false,
							type: 'String',
						},
					],
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const objectValidationRuleAPIClient =
				await apiHelpers.buildRestClient(ObjectValidationRuleAPI);

			const {body: objectValidationRule} =
				await objectValidationRuleAPIClient.postObjectDefinitionByExternalReferenceCodeObjectValidationRule(
					objectDefinition.externalReferenceCode!,
					{
						active: false,
						engine: 'ddm',
						errorLabel: {en_US: 'Validation error'},
						name: {en_US: 'Custom validation'},
						script: "oldValue('customPicklistField') == 'open'",
					}
				);

			const applicationName =
				'c/' + objectDefinition.name!.toLowerCase() + 's';

			const entry = await apiHelpers.objectEntry.postObjectEntry(
				{customPicklistField: {key: 'open', name: 'Open'}},
				applicationName
			);

			await objectValidationRuleAPIClient.putObjectValidationRule(
				objectValidationRule.id!,
				{
					...objectValidationRule,
					active: true,
				}
			);

			// First patch (open -> inprogress): oldValue is 'open', so the rule
			// passes and the entry updates successfully.

			const updatedEntry = await apiHelpers.objectEntry.patchObjectEntry(
				{customPicklistField: {key: 'inprogress', name: 'In Progress'}},
				applicationName,
				entry.id
			);

			expect(updatedEntry.customPicklistField.key).toBe('inprogress');

			// Second patch (inprogress -> open): oldValue is now 'inprogress',
			// so the rule fails and the API responds with a validation error.

			const failedResponse =
				(await apiHelpers.objectEntry.patchObjectEntry(
					{customPicklistField: {key: 'open', name: 'Open'}},
					applicationName,
					entry.id
				)) as unknown as {detail: string};

			expect(failedResponse.detail).toContain('Validation error');
		}
	);
});

test.describe('Object Groovy Validation', () => {
	test('can create a groovy validation', async ({
		apiHelpers,
		editObjectValidationPage,
		modalAddObjectValidationPage,
		objectValidationsPage,
		page,
		scriptManagementPage,
	}) => {
		await scriptManagementPage.enableScriptManagementConfiguration();

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields: [
					{
						DBType: 'String',
						businessType: 'Text',
						label: {en_US: 'Custom Field'},
						name: 'customText',
						required: false,
					},
				] as Partial<ObjectField>[],
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectValidationsPage.goto(objectDefinition.label['en_US']);

		await objectValidationsPage.addObjectValidationButton.click();

		await modalAddObjectValidationPage.objectValidationLabelInput.fill(
			'Custom Validation'
		);

		await modalAddObjectValidationPage.fillObjectValidationInputs(
			'Custom Validation',
			'Groovy'
		);

		await page.getByRole('link', {name: 'Custom Validation'}).click();

		await expect(
			objectValidationsPage.iframe.getByRole('tab', {
				name: 'Basic Info',
			})
		).toBeVisible();

		await expect(
			objectValidationsPage.iframe.getByRole('heading', {
				name: 'Basic Info',
			})
		).toBeVisible();

		await objectValidationsPage.activeValitionToggle.check();

		await expect(
			objectValidationsPage.iframe.getByRole('heading', {
				name: 'Trigger Event',
			})
		).toBeVisible();

		await objectValidationsPage.iframe
			.getByRole('tab', {
				name: 'Conditions',
			})
			.click();

		await expect(
			objectValidationsPage.iframe.getByRole('heading', {
				name: 'Groovy',
			})
		).toBeVisible();

		await page
			.locator('iframe')
			.contentFrame()
			.locator('.CodeMirror-scroll')
			.click();

		await page.keyboard.type(
			"invalidFields = (customText == 'Disallowed Entry')"
		);

		await objectValidationsPage.errorMessageInput.fill(
			'This entry is not valid.'
		);

		await editObjectValidationPage.saveObjectValidationButton.click();

		await expect(
			page.getByText('The object validation was updated successfully.')
		).toBeVisible();
	});

	test('groovy validation is not active by default', async ({
		apiHelpers,
		modalAddObjectValidationPage,
		objectValidationsPage,
		page,
		scriptManagementPage,
	}) => {
		await scriptManagementPage.enableScriptManagementConfiguration();

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectValidationsPage.goto(objectDefinition.label['en_US']);

		await objectValidationsPage.addObjectValidationButton.click();

		await modalAddObjectValidationPage.fillObjectValidationInputs(
			'Custom Validation',
			'Groovy'
		);

		// Saving the validation leaves a navigation in flight that aborts the
		// reload, and it lands after the page has otherwise gone quiet, so
		// waiting for a load state does not avoid it. Retry the reload instead.

		await expect(async () => {
			await page.reload({timeout: 15000});
		}).toPass({timeout: 60000});

		await expect(page.getByRole('cell', {name: 'No'})).toBeVisible();
	});

	test('specific error is shown when groovy syntax is incorrect', async ({
		apiHelpers,
		editObjectValidationPage,
		modalAddObjectValidationPage,
		objectValidationsPage,
		page,
		scriptManagementPage,
	}) => {
		await scriptManagementPage.enableScriptManagementConfiguration();

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields: [
					{
						DBType: 'String',
						businessType: 'Text',
						label: {en_US: 'Custom Field'},
						name: 'customField',
						required: false,
					},
				] as Partial<ObjectField>[],
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectValidationsPage.goto(objectDefinition.label['en_US']);

		await objectValidationsPage.addObjectValidationButton.click();

		await modalAddObjectValidationPage.fillObjectValidationInputs(
			'Custom Validation',
			'Groovy'
		);

		await page.getByRole('link', {name: 'Custom Validation'}).click();

		await objectValidationsPage.iframe
			.getByRole('tab', {name: 'Conditions'})
			.click();

		await page
			.locator('iframe')
			.contentFrame()
			.locator('.CodeMirror-scroll')
			.click();

		await page.keyboard.type("if = (customField == 'Syntax is incorrect')");

		await objectValidationsPage.errorMessageInput.fill('Error message');

		await editObjectValidationPage.saveObjectValidationButton.click();

		await expect(
			page.locator('iframe').contentFrame().getByText('Syntax Error')
		).toBeVisible();
	});
});

test.describe('Object Unique Composite Key Validation', () => {
	let objectDefinition1: ObjectDefinition;
	let objectDefinition2: ObjectDefinition;

	test.beforeEach(async ({apiHelpers}) => {
		const newObjectDefinition1 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		const newObjectDefinition2 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		objectDefinition1 = newObjectDefinition1;
		objectDefinition2 = newObjectDefinition2;
	});

	test.afterEach(async ({apiHelpers}) => {
		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		await objectDefinitionAPIClient.deleteObjectDefinition(
			objectDefinition1.id
		);
		await objectDefinitionAPIClient.deleteObjectDefinition(
			objectDefinition2.id
		);
	});

	test('can create an object unique composite key validation', async ({
		apiHelpers,
		editObjectValidationPage,
		modalAddObjectValidationPage,
		objectValidationsPage,
		page,
	}) => {
		const objectFieldAPIClient =
			await apiHelpers.buildRestClient(ObjectFieldAPI);

		await objectFieldAPIClient.postObjectDefinitionByExternalReferenceCodeObjectField(
			objectDefinition1.externalReferenceCode,
			{
				DBType: 'Integer',
				businessType: 'Integer',
				externalReferenceCode: 'integerField',
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: '',
				label: {en_US: 'integerField'},
				listTypeDefinitionId: 0,
				localized: false,
				name: 'integerField',
				readOnly: 'false',
				required: false,
				state: false,
				system: false,
			}
		);

		objectValidationsPage.goto(objectDefinition1.label['en_US']);

		await objectValidationsPage.addObjectValidationButton.click();

		const objectValidationLabel =
			'UniqueCompositeKeyValidation' + getRandomInt();

		await modalAddObjectValidationPage.fillObjectValidationInputs(
			objectValidationLabel,
			'Composite Key'
		);

		const newValidationLink = page.getByRole('link', {
			name: objectValidationLabel,
		});

		await newValidationLink.click();

		await editObjectValidationPage.uniqueCompositeKeyTab.click();

		await editObjectValidationPage.addObjectFieldsButton.click();

		await editObjectValidationPage.clickSelectAllFields();

		await editObjectValidationPage.saveObjectValidationButton.click();

		await expect(
			page.getByText('The object validation was updated successfully.')
		).toBeVisible();
	});

	test('can use an object unique composite key validation', async ({
		apiHelpers,
		viewObjectEntriesPage,
	}) => {
		const integerFieldName = 'integerField' + getRandomInt();

		const objectFieldAPIClient =
			await apiHelpers.buildRestClient(ObjectFieldAPI);

		await objectFieldAPIClient.postObjectDefinitionByExternalReferenceCodeObjectField(
			objectDefinition1.externalReferenceCode,
			{
				DBType: 'Integer',
				businessType: 'Integer',
				externalReferenceCode: integerFieldName,
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: '',
				label: {en_US: integerFieldName},
				listTypeDefinitionId: 0,
				localized: false,
				name: integerFieldName,
				readOnly: 'false',
				required: false,
				state: false,
				system: false,
			}
		);

		const objectValidationName =
			'Unique Composite Key Object Validation' + getRandomInt();

		const objectValidationRuleAPIClient = await apiHelpers.buildRestClient(
			ObjectValidationRuleAPI
		);

		await objectValidationRuleAPIClient.postObjectDefinitionByExternalReferenceCodeObjectValidationRule(
			objectDefinition1.externalReferenceCode,
			{
				active: true,
				engine: 'compositeKey',
				engineLabel: 'Composite Key',
				errorLabel: {
					en_US: 'The field values are already in use. Please choose unique values.',
				},
				name: {
					en_US: objectValidationName,
				},
				objectValidationRuleSettings: [
					{
						name: 'compositeKeyObjectFieldExternalReferenceCode',
						value: 'textField',
					} as any,
					{
						name: 'compositeKeyObjectFieldExternalReferenceCode',
						value: integerFieldName,
					} as any,
				],
				outputType: 'fullValidation',
				script: '',
				system: false,
			}
		);

		const applicationName =
			'c/' + objectDefinition1.name.toLowerCase() + 's';

		const textObjectEntry = {
			textField: 'entry',
		};

		await apiHelpers.objectEntry.postObjectEntry(
			textObjectEntry,
			applicationName
		);

		await viewObjectEntriesPage.goto(objectDefinition1.className);

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition1.label['en_US']
		);

		await viewObjectEntriesPage.fillObjectEntry({
			objectFieldLabel: integerFieldName,
			objectFieldValue: '0',
		});

		await viewObjectEntriesPage.fillObjectEntry({
			objectFieldLabel: 'textField',
			objectFieldValue: 'entry',
		});

		await viewObjectEntriesPage.saveObjectEntryButton.click();
		await viewObjectEntriesPage.assertErrorWithDuplicateEntryValue();

		await viewObjectEntriesPage.backButton.click();

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition1.label['en_US']
		);

		await viewObjectEntriesPage.fillObjectEntry({
			objectFieldLabel: integerFieldName,
			objectFieldValue: '123',
		});

		await viewObjectEntriesPage.fillObjectEntry({
			objectFieldLabel: 'textField',
			objectFieldValue: 'entry 2',
		});

		await viewObjectEntriesPage.saveObjectEntryButton.click();
		await expect(viewObjectEntriesPage.successMessage).toBeVisible();
	});

	test('check if only specific object field business types (AutoIncrement, Integer, Picklist, Relationship, Text) will be accepted in unique composite key validation', async ({
		apiHelpers,
		editObjectValidationPage,
		modalAddObjectValidationPage,
		objectValidationsPage,
		page,
	}) => {
		const autoIncrementFieldName = 'autoIncrementField' + getRandomInt();
		const dateFieldName = 'dateField' + getRandomInt();
		const integerFieldName = 'integerField' + getRandomInt();
		const objectRelationshipLabel =
			'objectRelationshipLabel' + getRandomInt();
		const objectRelationshipName =
			'objectRelationshipName' + Math.floor(Math.random() * 99);
		const picklistFieldName = 'picklistField' + getRandomInt();

		const objectFieldAPIClient =
			await apiHelpers.buildRestClient(ObjectFieldAPI);

		await objectFieldAPIClient.postObjectDefinitionByExternalReferenceCodeObjectField(
			objectDefinition1.externalReferenceCode,
			{
				DBType: 'String',
				businessType: 'AutoIncrement',
				externalReferenceCode: autoIncrementFieldName,
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: '',
				label: {en_US: autoIncrementFieldName},
				listTypeDefinitionId: 0,
				localized: false,
				name: autoIncrementFieldName,
				objectFieldSettings: [
					{
						name: 'initialValue',
						value: '1234',
					} as any,
				],
				readOnly: 'false',
				required: false,
				state: false,
				system: false,
			}
		);

		await objectFieldAPIClient.postObjectDefinitionByExternalReferenceCodeObjectField(
			objectDefinition1.externalReferenceCode,
			{
				DBType: 'Date',
				businessType: 'Date',
				externalReferenceCode: dateFieldName,
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: '',
				label: {en_US: dateFieldName},
				listTypeDefinitionId: 0,
				localized: false,
				name: dateFieldName,
				readOnly: 'false',
				required: false,
				state: false,
				system: false,
			}
		);

		await objectFieldAPIClient.postObjectDefinitionByExternalReferenceCodeObjectField(
			objectDefinition1.externalReferenceCode,
			{
				DBType: 'Integer',
				businessType: 'Integer',
				externalReferenceCode: integerFieldName,
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: '',
				label: {en_US: integerFieldName},
				listTypeDefinitionId: 0,
				localized: false,
				name: integerFieldName,
				readOnly: 'false',
				required: false,
				state: false,
				system: false,
			}
		);

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
			objectDefinition2.externalReferenceCode,
			{
				label: {
					en_US: objectRelationshipLabel,
				},
				name: objectRelationshipName,
				objectDefinitionExternalReferenceCode1:
					objectDefinition2.externalReferenceCode,
				objectDefinitionExternalReferenceCode2:
					objectDefinition1.externalReferenceCode,
				objectDefinitionId1: objectDefinition2.id,
				objectDefinitionId2: objectDefinition1.id,
				objectDefinitionName2: objectDefinition1.name,
				type: 'oneToMany',
			}
		);

		const listTypeDefinition =
			await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

		await objectFieldAPIClient.postObjectDefinitionByExternalReferenceCodeObjectField(
			objectDefinition1.externalReferenceCode,
			{
				DBType: 'String',
				businessType: 'Picklist',
				externalReferenceCode: picklistFieldName,
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: '',
				label: {en_US: picklistFieldName},
				listTypeDefinitionExternalReferenceCode:
					listTypeDefinition.externalReferenceCode,
				listTypeDefinitionId: listTypeDefinition.id,
				localized: false,
				name: picklistFieldName,
				readOnly: 'false',
				required: false,
				state: false,
				system: false,
			}
		);

		objectValidationsPage.goto(objectDefinition1.label['en_US']);

		await objectValidationsPage.addObjectValidationButton.click();

		const objectValidationLabel =
			'UniqueCompositeKeyValidation' + getRandomInt();

		await modalAddObjectValidationPage.fillObjectValidationInputs(
			objectValidationLabel,
			'Composite Key'
		);

		const newValidationLink = page.getByRole('link', {
			name: objectValidationLabel,
		});

		await expect(newValidationLink).toBeVisible();

		await newValidationLink.click();

		await editObjectValidationPage.uniqueCompositeKeyTab.click();

		await editObjectValidationPage.addObjectFieldsButton.click();

		await expect(page.getByText(autoIncrementFieldName)).toBeVisible();
		await expect(page.getByText(dateFieldName)).not.toBeVisible();
		await expect(page.getByText(integerFieldName)).toBeVisible();
		await expect(page.getByText(objectRelationshipLabel)).toBeVisible();
		await expect(page.getByText(picklistFieldName)).toBeVisible();

		// Clean Up

		await apiHelpers.listTypeAdmin.deleteListTypeDefinition(
			listTypeDefinition.id
		);
	});

	test('cannot select a object field that already has an entry in a new composite key validation', async ({
		apiHelpers,
		editObjectValidationPage,
		modalAddObjectValidationPage,
		objectValidationsPage,
		page,
	}) => {
		const objectFieldAPIClient =
			await apiHelpers.buildRestClient(ObjectFieldAPI);

		await objectFieldAPIClient.postObjectDefinitionByExternalReferenceCodeObjectField(
			objectDefinition1.externalReferenceCode,
			{
				DBType: 'Integer',
				businessType: 'Integer',
				externalReferenceCode: 'integerField',
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: '',
				label: {en_US: 'integerField'},
				listTypeDefinitionId: 0,
				localized: false,
				name: 'integerField',
				readOnly: 'false',
				required: false,
				state: false,
				system: false,
			}
		);

		const applicationName =
			'c/' + objectDefinition1.name.toLowerCase() + 's';

		const textObjectEntry = {
			textField: 'entry',
		};

		await apiHelpers.objectEntry.postObjectEntry(
			textObjectEntry,
			applicationName
		);

		objectValidationsPage.goto(objectDefinition1.label['en_US']);

		await objectValidationsPage.addObjectValidationButton.click();

		const objectValidationLabel =
			'UniqueCompositeKeyValidation' + getRandomInt();

		await modalAddObjectValidationPage.fillObjectValidationInputs(
			objectValidationLabel,
			'Composite Key'
		);

		const newValidationLink = page.getByRole('link', {
			name: objectValidationLabel,
		});

		await expect(newValidationLink).toBeVisible();

		await newValidationLink.click();

		await editObjectValidationPage.uniqueCompositeKeyTab.click();

		await editObjectValidationPage.addObjectFieldsButton.click();

		await editObjectValidationPage.clickSelectAllFields();

		await editObjectValidationPage.saveObjectValidationButton.click();

		await expect(
			editObjectValidationPage.getObjectFieldAlreadyHasEntryErrorLocator(
				'textField, integerField'
			)
		).toBeVisible();
	});

	test('cannot add unique composite key validation with just one field', async ({
		editObjectValidationPage,
		modalAddObjectValidationPage,
		objectValidationsPage,
		page,
	}) => {
		objectValidationsPage.goto(objectDefinition1.label['en_US']);

		await objectValidationsPage.addObjectValidationButton.click();

		const validationLabel = 'UniqueCompositeKeyValidation' + getRandomInt();

		await modalAddObjectValidationPage.fillObjectValidationInputs(
			validationLabel,
			'Composite Key'
		);

		const newValidationLink = page.getByRole('link', {
			name: validationLabel,
		});

		await expect(newValidationLink).toBeVisible();

		await newValidationLink.click();

		await editObjectValidationPage.uniqueCompositeKeyTab.click();

		await editObjectValidationPage.addObjectFieldsButton.click();

		await editObjectValidationPage.clickSelectAllFields();

		await editObjectValidationPage.saveObjectValidationButton.click();

		await expect(
			editObjectValidationPage.addTwoObjectFieldsErrorMessage
		).toBeVisible();
	});
});

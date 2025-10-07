/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinition,
	ObjectDefinitionAPI,
	ObjectField,
	ObjectRelationship,
	ObjectRelationshipAPI,
	ObjectValidationRuleAPI,
} from '@liferay/object-admin-rest-client-js';
import {Locator, expect, mergeTests} from '@playwright/test';
import fs from 'fs';
import path from 'path';

import {accountSettingsPagesTest} from '../../../fixtures/accountSettingsPagesTest';
import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';
import {collectionsPagesTest} from '../../../fixtures/collectionsPagesTest';
import {commercePagesTest} from '../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {editObjectDefinitionPagesTest} from '../../../fixtures/editObjectDefinitionPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {formsPagesTest} from '../../../fixtures/formsPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import createUserWithPermissions from '../../../utils/createUserWithPermissions';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {templatesPageTest} from '../../template-web/main/fixtures/templatesPageTest';
import {
	getObjectEntryUIDateTimeFormat,
	getPageEditorDateFormat,
	getUTCOffsetFormatted,
} from './utils/dateFormat';
import {createFile, deleteFile} from './utils/fileHelpers';
import {generateObjectEntryValues} from './utils/generateObjectEntry';
import {generateObjectFields} from './utils/generateObjectFields';
import evaluateKeepCheckingAfterFound from './utils/keepCheckingAfterFound';
import {pasteFile} from './utils/pasteFile';
import {postListTypeDefinitionListTypeEntries} from './utils/postListTypeDefinitionListTypeEntries';

const test = mergeTests(
	accountSettingsPagesTest,
	applicationsMenuPageTest,
	apiHelpersTest,
	collectionsPagesTest,
	commercePagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	editObjectDefinitionPagesTest,
	featureFlagsTest({
		'LPD-21926': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	formsPagesTest,
	journalPagesTest,
	loginTest(),
	objectPagesTest,
	pageEditorPagesTest,
	pagesAdminPagesTest,
	templatesPageTest,
	workflowPagesTest,
	usersAndOrganizationsPagesTest
);

const assigneeTest = mergeTests(
	test,
	featureFlagsTest({
		'LPD-6233': {enabled: true},
		'LPS-179669': {enabled: true},
	})
);

const scheduleTest = mergeTests(
	test,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
	})
);

let contentPageName: string;
let displayPageId: string;
let informationTemplateName: string;
let siteLanguage = 'en';

test.afterEach(async ({apiHelpers, page, pagesAdminPage, templatesPage}) => {
	if (contentPageName) {
		await pagesAdminPage.goto();

		await pagesAdminPage.deletePage(contentPageName);
	}

	if (displayPageId) {
		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.deleteLayoutPageTemplateEntry(
			{
				layoutPageTemplateEntryId: displayPageId,
			}
		);

		displayPageId = '';
	}

	if (informationTemplateName) {
		await templatesPage.goto();

		await templatesPage.deleteInformationTemplate(informationTemplateName);
	}

	if (siteLanguage !== 'en') {
		await page.goto('en');

		siteLanguage = 'en';
	}
});

assigneeTest(
	'can add and update an entry with assignee object field',
	async ({apiHelpers, page, viewObjectEntriesPage}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['Assignee'],
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				externalReferenceCode: getRandomString(),
				label: {
					en_US: getRandomString(),
				},
				name: 'ObjectDefinitionName' + getRandomInt(),
				objectFields,
				panelCategoryKey: 'control_panel.object',
				pluralLabel: {
					en_US: 'NewObject',
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

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);

		const {objectEntry} = await generateObjectEntryValues({
			objectEntryFormat: 'UI',
			objectFields,
			role: 'Asset Library Owner',
		});

		const objectFieldObjectEntryValues =
			await viewObjectEntriesPage.fillObjectFields({
				objectEntry,
				objectFields,
			});

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(viewObjectEntriesPage.successMessage).toBeVisible();

		await viewObjectEntriesPage.backButton.click();

		for (const {entry} of objectFieldObjectEntryValues) {
			await expect(
				page.locator('td').getByText(entry, {exact: true})
			).toBeVisible();
		}

		const {objectEntry: newObjectEntryValues} =
			await generateObjectEntryValues({
				objectEntryFormat: 'UI',
				objectFields,
				role: 'Site Owner',
			});

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.frontendDatasetItems.first().click();

		const newObjectFieldObjectEntryValues =
			await viewObjectEntriesPage.fillObjectFields({
				objectEntry: newObjectEntryValues,
				objectFields,
			});

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(viewObjectEntriesPage.successMessage).toBeVisible();

		await viewObjectEntriesPage.backButton.click();

		for (const {entry} of newObjectFieldObjectEntryValues) {
			await expect(
				page.locator('td').getByText(entry, {exact: true})
			).toBeVisible();
		}
	}
);

test.describe('Manage object entries through Friendly URL', () => {
	let _objectDefinition: ObjectDefinition;
	let _objectEntryFriendlyURLPath: string;
	let _objectField: ObjectField;

	test.beforeEach(async ({apiHelpers, site, viewObjectEntriesPage}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: [
				{
					businessType: 'Text',
					localized: true,
				},
			],
		});

		_objectField = objectFields[0];

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				enableFriendlyURLCustomization: true,
				enableLocalization: true,
				label: {
					en_US: getRandomString(),
				},
				name: 'ObjectDefinitionName' + getRandomInt(),
				objectFields,
				panelCategoryKey: 'site_administration.content',
				pluralLabel: {
					en_US: getRandomString(),
				},
				scope: 'site',
				status: {
					code: 0,
				},
			});

		_objectDefinition = objectDefinition;

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		_objectEntryFriendlyURLPath =
			'/c_' + _objectDefinition.name.toLowerCase() + '/';

		await viewObjectEntriesPage.goto(
			_objectDefinition.className,
			'en',
			site.friendlyUrlPath
		);

		await viewObjectEntriesPage.clickAddObjectEntry();
	});

	test('can access object entry via friendly URL', async ({
		apiHelpers,
		displayPageTemplatesPage,
		editObjectDetailsPage,
		page,
		pageEditorPage,
		site,
		viewObjectEntriesPage,
	}) => {
		let displayPage: LayoutPageTemplateEntry;
		const displayPageTemplateName = getRandomString();
		const objectFieldValue = getRandomString();

		await test.step('Create object entry with friendly URL', async () => {
			const friendlyUrl = page.getByLabel('Friendly URL').nth(1);

			await friendlyUrl.fill('Test URL');

			await page.getByTestId('visibleChangeInput').fill(objectFieldValue);

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await expect(viewObjectEntriesPage.successMessage).toBeVisible();

			await expect(friendlyUrl).toHaveValue('test-url');
		});

		await test.step('Create display page template', async () => {
			const className =
				await apiHelpers.jsonWebServicesClassName.fetchClassName(
					_objectDefinition.className
				);

			displayPage =
				await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
					{
						classNameId: className.classNameId,
						groupId: site.id,
						name: displayPageTemplateName,
					}
				);

			displayPageId = displayPage.layoutPageTemplateEntryId;

			await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.markAsDefaultDisplayPageLayoutPageTemplateEntry(
				{
					layoutPageTemplateEntryId:
						displayPage.layoutPageTemplateEntryId,
				}
			);
		});

		await test.step('Add heading fragment and map it to the object field', async () => {
			displayPageTemplatesPage.goto(site.friendlyUrlPath);

			displayPageTemplatesPage.editTemplate(displayPageTemplateName);

			await pageEditorPage.addFragment('Basic Components', 'Heading');

			await page.getByText('Heading Example', {exact: true}).click();

			await pageEditorPage.setMappingConfiguration({
				mapping: {
					field: _objectField.label['en_US'],
				},
				source: 'structure',
			});

			await displayPageTemplatesPage.publishTemplate();
		});

		await test.step('Access the object entry via friendly URL', async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}${_objectEntryFriendlyURLPath}` +
					'test-url',
				{
					waitUntil: 'networkidle',
				}
			);

			await expect(page.getByText(objectFieldValue)).toBeVisible();
		});

		await test.step('Change the object friendly URL separator and access the object entry again', async () => {
			const newObjectFriendlyURLSeparator = 'c_separator_updated';

			await editObjectDetailsPage.goto(_objectDefinition.label['en_US']);

			await page
				.getByRole('textbox', {name: 'Object Entry URL Separator'})
				.fill(newObjectFriendlyURLSeparator);

			await editObjectDetailsPage.saveObjectDefinition();

			await page.waitForLoadState('networkidle');

			await page.goto(
				`/web${site.friendlyUrlPath}/${newObjectFriendlyURLSeparator}/` +
					'test-url',
				{
					waitUntil: 'networkidle',
				}
			);

			await expect(page.getByText(objectFieldValue)).toBeVisible();
		});
	});

	test('can restore old friendly URL', async ({
		apiHelpers,
		page,
		site,
		viewObjectEntriesPage,
	}) => {

		// Create object entry with friendly URL

		const applicationName =
			'c/' + _objectDefinition.name.toLowerCase() + 's';

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{friendlyUrlPath: 'first-url'},
			applicationName,
			site.key
		);

		// Edit the friendly URL

		await apiHelpers.objectEntry.putObjectEntry(
			{friendlyUrlPath: 'second-url'},
			applicationName,
			objectEntry.id
		);

		// Verify that the current friendly URL matches the last one defined

		await viewObjectEntriesPage.goto(
			_objectDefinition.className,
			'en',
			site.friendlyUrlPath
		);

		await page.getByRole('link', {name: String(objectEntry.id)}).click();

		const friendlyUrl = page.getByLabel('Friendly URL').nth(1);

		await expect(friendlyUrl).toHaveValue('second-url');

		// Open the history modal

		await page.getByRole('button', {name: 'History'}).click();

		await expect(page.getByText('Active URL')).toBeVisible();
		await expect(page.getByText('second-url')).toBeVisible();

		// Restore the friendly URL to its first value

		await page.getByText('first-url').hover();

		await page.locator("button[data-title='Restore URL']").click();

		await page.getByRole('button', {name: 'Close'}).click();

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(viewObjectEntriesPage.successMessage).toBeVisible();

		await expect(friendlyUrl).toHaveValue('first-url');
	});

	test('friendly URL input is disabled when viewed inside workflow task detail', async ({
		applicationsMenuPage,
		configurationTabPage,
		page,
		site,
		viewObjectEntriesPage,
		workflowTaskDetailsPage,
		workflowTasksPage,
	}) => {
		let friendlyUrlInput: Locator;

		await test.step('Assign the single approver workflow to the object created', async () => {
			await applicationsMenuPage.goToProcessBuilder();

			await configurationTabPage.configurationTabLink.click();

			await configurationTabPage.assignWorkflowToAssetType(
				'Single Approver',
				_objectDefinition.label['en_US']
			);
		});

		await test.step('Assert that the friendly URL is enabled', async () => {
			await viewObjectEntriesPage.goto(
				_objectDefinition.className,
				'en',
				site.friendlyUrlPath
			);

			await viewObjectEntriesPage.clickAddObjectEntry();

			friendlyUrlInput = page.getByLabel('Friendly URL', {exact: true});

			await expect(friendlyUrlInput).not.toBeDisabled();
		});

		await test.step('Add an object entry', async () => {
			await friendlyUrlInput.fill('test-url');

			await page.getByTestId('visibleChangeInput').fill('test entry');

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await expect(viewObjectEntriesPage.successMessage).toBeVisible();
		});

		await test.step('Go to the workflow task detail and verify that the friendly URL input is disabled', async () => {
			await workflowTasksPage.goToAssignedToMyRoles();

			await workflowTaskDetailsPage.selectAsset(
				_objectDefinition.label['en_US']
			);

			await expect(friendlyUrlInput).toBeDisabled();
		});
	});

	test('verify that friendly URL field is not visible when customization is disabled', async ({
		apiHelpers,
		page,
	}) => {
		await expect(page.getByLabel('Friendly URL').nth(1)).toBeVisible();
		await expect(
			page.getByText(
				'The friendly URL is automatically generated based on the entry title field.'
			)
		).toBeVisible();
		await expect(
			page.getByTitle(_objectEntryFriendlyURLPath)
		).toBeVisible();

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		await objectDefinitionAPIClient.patchObjectDefinition(
			_objectDefinition.id,
			{
				enableFriendlyURLCustomization: false,
			}
		);

		await page.reload();

		await expect(page.getByLabel('Friendly URL').nth(1)).not.toBeVisible();
		await expect(
			page.getByText(
				'The friendly URL is automatically generated based on the entry title field.'
			)
		).not.toBeVisible();
		await expect(
			page.getByTitle(_objectEntryFriendlyURLPath)
		).not.toBeVisible();
	});

	test('verify that locale dropdowns for friendly URL and localizable object field are synchronized', async ({
		page,
	}) => {
		await page.getByText('en-us', {exact: true}).click();

		await page.getByText('português (Brasil)').click();

		await expect(page.getByText('pt-br', {exact: true})).toBeVisible();
		await expect(page.getByText('pt-BR', {exact: true})).toBeVisible();

		await page.getByText('pt-BR', {exact: true}).click();

		await page.locator("a[data-languageId='ca_ES']").click();

		await expect(page.getByText('ca-es', {exact: true})).toBeVisible();
		await expect(page.getByText('ca-ES', {exact: true})).toBeVisible();
	});
});

test.describe('Manage object entries through Object Definition widget', () => {
	test('verify that previous validation alerts are removed from the page when editing the entry', async ({
		apiHelpers,
		page,
		pageEditorPage,
		site,
		viewObjectEntriesPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				className: 'com.liferay.object.model.ObjectDefinition#1234',
				status: {code: 0},
				titleObjectFieldName: 'textField',
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
				errorLabel: {
					en_US: 'The field is empty',
				},
				name: {
					en_US: 'Validation 1',
				},
				objectValidationRuleSettings: [],
				script: 'not(isEmpty(textField))',
				system: false,
			}
		);
		await objectValidationRuleAPIClient.postObjectDefinitionByExternalReferenceCodeObjectValidationRule(
			objectDefinition.externalReferenceCode,
			{
				active: true,
				engine: 'ddm',
				errorLabel: {
					en_US: 'The URL is invalid',
				},
				name: {
					en_US: 'Validation 2',
				},
				objectValidationRuleSettings: [],
				script: 'isEmpty(textField) OR isURL(textField)',
				system: false,
			}
		);

		const objectDefinitionWidgetDefinition = getWidgetDefinition({
			id: getRandomString(),
			widgetName:
				'com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet_1234',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				objectDefinitionWidgetDefinition,
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.publishPage();

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await viewObjectEntriesPage.clickAddObjectEntry();

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(page.getByText('The field is empty')).toBeVisible();

		const objectFieldValue = getRandomString();

		await page.getByLabel('textField').fill(objectFieldValue);

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(page.getByText('The field is empty')).not.toBeVisible();
		await expect(page.getByText('The URL is invalid')).toBeVisible();
	});
});

test.describe('Manage object entries through Page Templates', () => {
	test('verify if the object entries are displayed when selecting to preview an object entry on a page template', async ({
		apiHelpers,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
	}) => {
		test.slow();
		const objectDefinitionLabel = 'ObjectDefinitionLabel' + getRandomInt();
		const objectDefinitionName = 'ObjectDefinitionName' + getRandomInt();

		const {listTypeDefinition, listTypeEntries} =
			await postListTypeDefinitionListTypeEntries({
				apiHelpers,
			});

		const objectFields = generateObjectFields({
			listTypeDefinitionExternalReferenceCode:
				listTypeDefinition.externalReferenceCode,
			objectFieldBusinessTypes: [
				'AutoIncrement',
				'Decimal',
				'Date',
				'Boolean',
				'Encrypted',
				'Integer',
				'LongInteger',
				'LongText',
				'MultiselectPicklist',
				'Picklist',
				'PrecisionDecimal',
				'RichText',
				'Text',
			],
		});

		apiHelpers.data.push({
			id: listTypeDefinition.id,
			type: 'listTypeDefinition',
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				label: {
					en_US: objectDefinitionLabel,
				},
				name: objectDefinitionName,
				objectFields,
				pluralLabel: {
					en_US: objectDefinitionLabel,
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

		const {objectEntry: objectEntryValues} =
			await generateObjectEntryValues({
				listTypeEntries: listTypeEntries.map(
					(listTypeEntry) => listTypeEntry.name
				),
				objectEntryFormat: 'API',
				objectFields,
			});

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			objectEntryValues,
			applicationName
		);

		await displayPageTemplatesPage.goto();

		const displayPageTemplateName = getRandomString();

		await displayPageTemplatesPage.createTemplate({
			contentType: objectDefinition.label['en_US'],
			name: displayPageTemplateName,
		});

		await page.getByTitle(displayPageTemplateName).click();

		overloop: for (const [_, objectField] of objectDefinition.objectFields
			.filter((objectField) => !objectField.system)
			.entries()) {
			await pageEditorPage.addFragment('Basic Components', 'Heading');

			await page.getByText('Heading Example', {exact: true}).click();

			await pageEditorPage.setMappingConfiguration({
				mapping: {
					entity: objectDefinitionLabel,
					entry: objectEntry.externalReferenceCode,
					field: objectField.label['en_US'],
				},
				source: 'content',
			});

			let matchString: string;

			switch (objectField.businessType) {
				case 'Date': {
					const date = new Date(
						Date.parse(
							objectEntryValues[objectField.name] as string
						)
					);

					matchString = getPageEditorDateFormat(date);

					// Defer date validation for CI trace view analysis (issue #LRCI-4253)

					continue overloop;
				}
				case 'Picklist': {
					matchString = (
						objectEntryValues[objectField.name] as {
							key: string;
						}
					).key;

					break;
				}
				case 'MultiselectPicklist': {
					(objectEntryValues[objectField.name] as string[]).forEach(
						(listTypeEntry, index) => {
							index < 1
								? (matchString = `${listTypeEntry}`)
								: (matchString += `, ${listTypeEntry}`);
						}
					);

					break;
				}
				default: {
					matchString =
						objectEntryValues[objectField.name].toString();
				}
			}

			await expect(
				page.getByTitle('Edit Text').filter({hasText: matchString})
			).toBeVisible();
		}

		// Clean up

		await displayPageTemplatesPage.goto();

		await displayPageTemplatesPage.deleteTemplate(objectDefinitionLabel);
	});

	test('verify it is possible to create a information template with an object as an item type and see its entries', async ({
		apiHelpers,
		page,
		pageEditorPage,
		pagesAdminPage,
		templatesPage,
	}) => {
		const {listTypeDefinition, listTypeEntries} =
			await postListTypeDefinitionListTypeEntries({
				apiHelpers,
			});

		const objectFields = generateObjectFields({
			listTypeDefinitionExternalReferenceCode:
				listTypeDefinition.externalReferenceCode,
			objectFieldBusinessTypes: [
				'Boolean',
				'Decimal',
				'Integer',
				'LongText',
				'Picklist',
				'Text',
			],
		});

		apiHelpers.data.push({
			id: listTypeDefinition.id,
			type: 'listTypeDefinition',
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const {objectEntry: objectEntryValues} =
			await generateObjectEntryValues({
				listTypeEntries: listTypeEntries.map(
					(listTypeEntry) => listTypeEntry.name
				),
				objectEntryFormat: 'API',
				objectFields,
			});

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			objectEntryValues,
			applicationName
		);

		informationTemplateName = 'Object Template' + getRandomInt();

		await test.step('create information template and add object fields', async () => {
			await templatesPage.goto();

			await templatesPage.createInformationTemplate({
				itemType: objectDefinition.label['en_US'],
				name: informationTemplateName,
			});

			for (const objectField of objectFields) {
				await page
					.getByRole('button', {name: objectField.label['en_US']})
					.click();
			}

			await templatesPage.saveTemplate(informationTemplateName);
		});

		contentPageName = getRandomString();

		await test.step('create page template with HTML element linked to the informationTemplateName', async () => {
			await pagesAdminPage.goto();

			await pagesAdminPage.createNewPage({
				name: contentPageName,
			});

			await pagesAdminPage.editPage(contentPageName);

			await pageEditorPage.addFragment('Basic Components', 'HTML');

			const htmlFragmentId = await pageEditorPage.getFragmentId('HTML');

			await pageEditorPage.selectEditable(htmlFragmentId, 'element-html');

			await pageEditorPage.setMappedItem({
				entity: objectDefinition.label['en_US'],
				entry: objectEntry.id.toString(),
				entryLocator: page
					.frameLocator('iframe[title="Select"]')
					.getByText(objectEntry.id.toString())
					.first(),
				field: informationTemplateName,
			});

			await pageEditorPage.waitForChangesSaved();

			await pageEditorPage.publishPage();
		});

		await test.step('go to created page and assert object entries', async () => {
			await page.goto(`/web/guest/${contentPageName}`);

			const entries = Object.values(objectEntryValues)
				.map((value) => {
					if (typeof value === 'boolean') {
						return value ? 'Yes' : 'No';
					}

					if (
						typeof value === 'object' &&
						value !== null &&
						'key' in (value as object)
					) {
						return (value as {key: string}).key;
					}

					return String(value);
				})
				.join(' ');

			await expect(page.getByText(entries)).toBeVisible();
		});
	});
});

test.describe('Manage object entries through View Object Entries', () => {
	test('can add and update an entry with all object fields', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const ATTACHMENT_FILE_NAME_1 = 'astronaut.png';
		const ATTACHMENT_FILE_NAME_2 = 'earth.png';

		const {listTypeDefinition, listTypeEntries} =
			await postListTypeDefinitionListTypeEntries({
				apiHelpers,
			});

		const objectFields = generateObjectFields({
			listTypeDefinitionExternalReferenceCode:
				listTypeDefinition.externalReferenceCode,
			objectFieldBusinessTypes: [
				'Attachment',
				'Boolean',
				'Date',
				'Decimal',
				'Integer',
				'LongInteger',
				'LongText',
				'Picklist',
				'PrecisionDecimal',
				'RichText',
				'Text',
			],
		});

		apiHelpers.data.push({
			id: listTypeDefinition.id,
			type: 'listTypeDefinition',
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				externalReferenceCode: getRandomString(),
				label: {
					en_US: getRandomString(),
				},
				name: 'ObjectDefinitionName' + getRandomInt(),
				objectFields,
				panelCategoryKey: 'control_panel.object',
				pluralLabel: {
					en_US: 'NewObject',
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

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);

		const {objectEntry} = await generateObjectEntryValues({
			listTypeEntries: listTypeEntries.map(
				(listTypeEntry) => listTypeEntry.name
			),
			objectEntryFormat: 'UI',
			objectFields,
		});

		const objectFieldObjectEntryValues =
			await viewObjectEntriesPage.fillObjectFields({
				attachmentFileName: ATTACHMENT_FILE_NAME_1,
				objectEntry,
				objectFields,
			});

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(viewObjectEntriesPage.successMessage).toBeVisible();

		await viewObjectEntriesPage.backButton.click();

		for (const {entry} of objectFieldObjectEntryValues) {
			await expect(
				page.locator('td').getByText(entry, {exact: true})
			).toBeVisible();
		}

		const selectedListTypeEntry = objectFieldObjectEntryValues.find(
			(objectFieldObjectEntryValue) =>
				objectFieldObjectEntryValue.businessType === 'Picklist'
		)?.entry;

		const newListTypeEntries = listTypeEntries.filter(
			(listTypeEntry) => listTypeEntry.key !== selectedListTypeEntry
		);

		const {objectEntry: newObjectEntryValues} =
			await generateObjectEntryValues({
				listTypeEntries: newListTypeEntries.map(
					(listTypeEntry) => listTypeEntry.name
				),
				objectEntryFormat: 'UI',
				objectFields,
			});

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.frontendDatasetItems.first().click();

		const newObjectFieldObjectEntryValues =
			await viewObjectEntriesPage.fillObjectFields({
				attachmentFileName: ATTACHMENT_FILE_NAME_2,
				objectEntry: newObjectEntryValues,
				objectFields,
			});

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(viewObjectEntriesPage.successMessage).toBeVisible();

		await viewObjectEntriesPage.backButton.click();

		for (const {entry} of newObjectFieldObjectEntryValues) {
			await expect(
				page.locator('td').getByText(entry, {exact: true})
			).toBeVisible();
		}
	});

	test('can add and update an entry with multi-select object field', async ({
		apiHelpers,
		formFieldsPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const {listTypeDefinition, listTypeEntries} =
			await postListTypeDefinitionListTypeEntries({
				apiHelpers,
			});

		const objectDefinitionLabel = 'ObjectDefinitionLabel' + getRandomInt();

		const objectFields = generateObjectFields({
			listTypeDefinitionExternalReferenceCode:
				listTypeDefinition.externalReferenceCode,
			objectFieldBusinessTypes: ['MultiselectPicklist'],
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				enableLocalization: true,
				label: {
					en_US: objectDefinitionLabel,
				},
				name: 'ObjectDefinitionName' + getRandomInt(),
				objectFields,
				pluralLabel: {
					en_US: objectDefinitionLabel,
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

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.addObjectEntryButton.click();

		const listTypeEntry = listTypeEntries[0];

		const {name_i18n: listTypeEntry_i18n} = listTypeEntry;

		await formFieldsPage.addSelectItem(listTypeEntry_i18n['en-US']);

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(viewObjectEntriesPage.successMessage).toBeVisible();

		await expect(viewObjectEntriesPage.successMessage).toBeHidden();

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(viewObjectEntriesPage.successMessage).toBeVisible();

		await expect(
			page.getByRole('gridcell', {
				exact: true,
				name: listTypeEntry.name_i18n['en-US'],
			})
		).toBeVisible();
	});

	test('can add entry for site scoped definition with versioning enabled', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				scope: 'site',
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		await objectDefinitionAPIClient.patchObjectDefinition(
			objectDefinition.id,
			{
				enableObjectEntryVersioning: true,
			}
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);

		await viewObjectEntriesPage.fillObjectEntry({
			objectFieldBusinessType: 'Text',
			objectFieldLabel: 'textField',
			objectFieldValue: 'test',
		});

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(viewObjectEntriesPage.successMessage).toBeVisible();

		await viewObjectEntriesPage.backButton.click();

		await expect(
			page.locator('td').getByText('test', {exact: true})
		).toBeVisible();
	});

	test(
		'can attach files after changing the overall maximum upload request size setting',
		{tag: ['@LPD-56964']},
		async ({
			apiHelpers,
			objectFieldsPage,
			page,
			systemSettingsPage,
			viewObjectDefinitionsPage,
			viewObjectEntriesPage,
		}) => {
			try {
				await test.step('set overall maximum upload request size to 2MB in system settings', async () => {
					await systemSettingsPage.goToSystemSetting(
						'Infrastructure',
						'Upload Servlet Request'
					);

					await page
						.getByLabel('Overall Maximum Upload Request Size')
						.fill('2097152');

					await page
						.getByRole('button', {name: 'Save'})
						.or(page.getByRole('button', {name: 'Update'}))
						.click();
				});

				const objectDefinition: ObjectDefinition =
					await apiHelpers.objectAdmin.postRandomObjectDefinition({
						status: {code: 0},
					});

				await test.step('create attachment object field', async () => {
					apiHelpers.data.push({
						id: objectDefinition.id,
						type: 'objectDefinition',
					});

					await viewObjectDefinitionsPage.goto();

					await objectFieldsPage.goto(
						objectDefinition.label['en_US']
					);

					await objectFieldsPage.addObjectField({
						attachmentSource: 'Upload Directly from the User',
						objectFieldBusinessType: 'Attachment',
						objectFieldLabel: 'Attachment',
					});
				});

				await test.step('Verify attachment field maximum file size validation', async () => {
					await objectFieldsPage.openObjectField('Attachment');

					await expect(objectFieldsPage.maximumFileSize).toHaveValue(
						'0'
					);

					await objectFieldsPage.maximumFileSize.fill('3');

					await objectFieldsPage.editFieldSaveButton.click();

					await expect(
						objectFieldsPage.getMaximumFileSizeErrorMessage({
							maximumFileSizeAllowed: '2',
						})
					).toBeVisible();
				});

				const FILE_NAME_3MB = '3MB.txt';
				const FILE_SIZE_3MB = 3;

				createFile(FILE_NAME_3MB, FILE_SIZE_3MB);

				await test.step('attempt upload with file exceeding attachment maximum allowed size', async () => {
					await viewObjectEntriesPage.goto(
						objectDefinition.className
					);

					await viewObjectEntriesPage.clickAddObjectEntry(
						objectDefinition.label['en_US']
					);

					await expect(
						page.getByText(
							'Upload a jpeg, jpg, pdf, png no larger than 2 MB.',
							{exact: true}
						)
					).toBeVisible();

					await viewObjectEntriesPage.selectFileFromUserComputer(
						__dirname,
						FILE_NAME_3MB
					);

					await expect(
						viewObjectEntriesPage.getMaximumFileSizeErrorMessage({
							maximumFileSizeAllowed: '2',
						})
					).toBeVisible();
				});

				const FILE_NAME_2MB = '2MB.png';
				const FILE_SIZE_2MB = 2;

				createFile(FILE_NAME_2MB, FILE_SIZE_2MB);

				await test.step('successfully upload file at maximum allowed size', async () => {
					await viewObjectEntriesPage.selectFileFromUserComputer(
						__dirname,
						FILE_NAME_2MB
					);

					await expect(
						viewObjectEntriesPage.getMaximumFileSizeErrorMessage({
							maximumFileSizeAllowed: '2',
						})
					).toBeHidden();

					await viewObjectEntriesPage.saveObjectEntryButton.click();

					await waitForAlert(
						page,
						'Success:Your request completed successfully.'
					);
				});

				deleteFile(FILE_NAME_3MB);
				deleteFile(FILE_NAME_2MB);
			}
			finally {
				await test.step('set overall maximum upload request size to 10MB in system settings', async () => {
					await systemSettingsPage.goToSystemSetting(
						'Infrastructure',
						'Upload Servlet Request'
					);

					await page
						.getByLabel('Overall Maximum Upload Request Size')
						.fill('104857600');

					await page.getByRole('button', {name: 'Update'}).click();
				});
			}
		}
	);

	test('can download and delete a file from the Attachment field when adding an object entry', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const ATTACHMENT_FILE_NAME = 'astronaut.png';

		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['Attachment'],
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				externalReferenceCode: getRandomString(),
				label: {
					en_US: getRandomString(),
				},
				name: 'ObjectDefinitionName' + getRandomInt(),
				objectFields,
				panelCategoryKey: 'control_panel.object',
				pluralLabel: {
					en_US: 'NewObject',
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

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);

		await viewObjectEntriesPage.selectFileButton.click();

		await viewObjectEntriesPage.selectFileFromDocumentsAndMedia(
			ATTACHMENT_FILE_NAME
		);

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(viewObjectEntriesPage.successMessage).toBeVisible();

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(viewObjectEntriesPage.successMessage).toBeVisible();

		const downloadPromise = page.waitForEvent('download');

		await page.getByRole('button', {name: ATTACHMENT_FILE_NAME}).hover();

		await page.locator('.lexicon-icon-download').click();

		expect((await downloadPromise).suggestedFilename()).toStrictEqual(
			`${ATTACHMENT_FILE_NAME}`
		);

		await viewObjectEntriesPage.deleteFileButton.click();

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(
			viewObjectEntriesPage.successMessage.first()
		).toBeVisible();
	});

	test('can view all entries related to an object in the relationship field using autocomplete', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinition1 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
				titleObjectFieldName: 'textField',
			});

		apiHelpers.data.push({
			id: objectDefinition1.id,
			type: 'objectDefinition',
		});

		const objectDefinition2 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition2.id,
			type: 'objectDefinition',
		});

		const objectRelationshipLabel =
			'objectRelationshipLabel' + getRandomInt();
		const objectRelationshipName =
			'objectRelationshipName' + Math.floor(Math.random() * 99);

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
			objectDefinition1.externalReferenceCode,
			{
				label: {
					en_US: objectRelationshipLabel,
				},
				name: objectRelationshipName,
				objectDefinitionExternalReferenceCode1:
					objectDefinition1.externalReferenceCode,
				objectDefinitionExternalReferenceCode2:
					objectDefinition2.externalReferenceCode,
				objectDefinitionId1: objectDefinition1.id,
				objectDefinitionId2: objectDefinition2.id,
				objectDefinitionName2: objectDefinition2.name,
				type: 'oneToMany',
			}
		);

		const applicationName =
			'c/' + objectDefinition1.name.toLowerCase() + 's';

		await apiHelpers.objectEntry.postObjectEntry(
			{textField: 'test 1'},
			applicationName
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{textField: 'test 2'},
			applicationName
		);

		await viewObjectEntriesPage.goto(objectDefinition2.className);
		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition2.label['en_US']
		);

		await page.getByPlaceholder('Search', {exact: true}).fill('t 1');
		await expect(page.getByRole('menuitem')).toContainText('test 1');

		await page.locator('input[value="t 1"]').fill('t 2');
		await expect(page.getByRole('menuitem')).toContainText('test 2');

		await page.locator('input[value="t 2"]').fill('tes');
		await expect(page.getByRole('menu')).toContainText('test 1');
		await expect(page.getByRole('menu')).toContainText('test 2');
	});

	test('can filter entries in a M:M relationship entries page using search container', async ({
		apiHelpers,
		objectLayoutsPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectField = 'textField';

		const objectDefinition1 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				scope: 'company',
				status: {code: 0},
				titleObjectFieldName: objectField,
			});

		apiHelpers.data.push({
			id: objectDefinition1.id,
			type: 'objectDefinition',
		});

		const objectDefinition2 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				scope: 'company',
				status: {code: 0},
				titleObjectFieldName: objectField,
			});

		apiHelpers.data.push({
			id: objectDefinition2.id,
			type: 'objectDefinition',
		});

		const objectRelationshipLabel =
			'objectRelationshipLabel' + getRandomInt();
		const objectRelationshipName =
			'objectRelationshipName' + getRandomInt();

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		const objectRelationshipData: Partial<ObjectRelationship> = {
			label: {
				en_US: objectRelationshipLabel,
			},
			name: objectRelationshipName,
			objectDefinitionExternalReferenceCode1:
				objectDefinition1.externalReferenceCode,
			objectDefinitionExternalReferenceCode2:
				objectDefinition2.externalReferenceCode,
			objectDefinitionId1: objectDefinition1.id,
			objectDefinitionId2: objectDefinition2.id,
			objectDefinitionName2: objectDefinition2.name,
			type: 'manyToMany',
		};

		await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
			objectDefinition1.externalReferenceCode,
			objectRelationshipData
		);

		const applicationName =
			'c/' + objectDefinition1.name.toLowerCase() + 's';

		await apiHelpers.objectEntry.postObjectEntry(
			{textField: 'test 1'},
			applicationName
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{textField: 'test 2'},
			applicationName
		);

		const objectLayoutName = getRandomString();

		const objectRelationshipTabName = getRandomString();

		await objectLayoutsPage.goto(objectDefinition2.name);

		await objectLayoutsPage.createObjectLayout(objectLayoutName);

		await page.getByRole('link', {name: objectLayoutName}).click();

		await objectLayoutsPage.markAsDefaultButton.check();

		await objectLayoutsPage.layoutTab.click();

		await objectLayoutsPage.createObjectLayoutTab(getRandomString());

		await objectLayoutsPage.createObjectLayoutBlock(getRandomString());

		await objectLayoutsPage.openObjectLayoutObjectField();

		await objectLayoutsPage.iframeLocator
			.getByRole('option', {name: objectField})
			.click();

		await objectLayoutsPage.saveAddFieldButton.click();

		await objectLayoutsPage.createObjectRelationshipTab(
			objectLayoutName,
			objectRelationshipTabName,
			objectRelationshipLabel
		);

		await viewObjectEntriesPage.goto(objectDefinition2.className);

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition2.label['en_US']
		);

		await viewObjectEntriesPage.fillObjectEntry({
			objectFieldBusinessType: 'Text',
			objectFieldLabel: objectField,
			objectFieldValue: 'tests',
		});

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await page.getByRole('link', {name: objectRelationshipTabName}).click();

		await viewObjectEntriesPage.addObjectEntryButton.click();

		await expect(viewObjectEntriesPage.searchButton).toBeEnabled();
		await viewObjectEntriesPage.searchBar.click();
		await viewObjectEntriesPage.searchBar.fill('t 1');
		await viewObjectEntriesPage.searchButton.click();
		await expect(viewObjectEntriesPage.searchContainer).toContainText(
			'test 1'
		);
		await expect(viewObjectEntriesPage.searchContainer).not.toContainText(
			'test 2'
		);

		await expect(viewObjectEntriesPage.searchButton).toBeEnabled();
		await viewObjectEntriesPage.searchBar.click();
		await viewObjectEntriesPage.searchBar.fill('t 2');
		await viewObjectEntriesPage.searchButton.click();
		await expect(viewObjectEntriesPage.searchContainer).toContainText(
			'test 2'
		);
		await expect(viewObjectEntriesPage.searchContainer).not.toContainText(
			'test 1'
		);

		await expect(viewObjectEntriesPage.searchButton).toBeEnabled();
		await viewObjectEntriesPage.searchBar.click();
		await viewObjectEntriesPage.searchBar.fill('tes');
		await viewObjectEntriesPage.searchButton.click();
		await expect(viewObjectEntriesPage.searchContainer).toContainText(
			'test 1'
		);
		await expect(viewObjectEntriesPage.searchContainer).toContainText(
			'test 2'
		);
	});

	test('can view success message entirely in arabic', async ({
		apiHelpers,
		viewObjectEntriesPage,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['Attachment'],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await viewObjectEntriesPage.goto(objectDefinition.className, 'ar');

		await viewObjectEntriesPage.addObjectEntryButton.click();

		await viewObjectEntriesPage.selectFileFromDocumentsAndMediaArabic();

		await viewObjectEntriesPage.saveObjectEntryButtonArabic.click();

		await expect(viewObjectEntriesPage.successMessageArabic).toBeVisible();
	});

	test('can delete relation on relationship tab', async ({
		apiHelpers,
		editObjectDetailsPage,
		objectLayoutsPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: [
				{
					businessType: 'Text',
					label: {en_US: 'Custom Field'},
					name: 'customField',
				},
			],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				panelCategoryKey: 'control_panel.object',
				status: {code: 0},
				titleObjectFieldName: 'customField',
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
			objectDefinition.externalReferenceCode,
			{
				deletionType: 'disassociate',
				label: {
					en_US: 'Relationship',
				},
				name: 'relationship',
				objectDefinitionExternalReferenceCode1:
					objectDefinition.externalReferenceCode,
				objectDefinitionExternalReferenceCode2:
					objectDefinition.externalReferenceCode,
				objectDefinitionId1: objectDefinition.id,
				objectDefinitionId2: objectDefinition.id,
				type: 'oneToMany',
			}
		);

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		const objectEntryA = await apiHelpers.objectEntry.postObjectEntry(
			{
				customField: 'Entry A',
			},
			applicationName
		);

		const objectEntryB = await apiHelpers.objectEntry.postObjectEntry(
			{
				customField: 'Entry B',
			},
			applicationName
		);

		const objectLayoutName = 'Layout Name';

		await objectLayoutsPage.goto(objectDefinition.name);

		await objectLayoutsPage.createObjectLayout(objectLayoutName);

		await page.getByRole('link', {name: objectLayoutName}).click();

		await objectLayoutsPage.markAsDefaultButton.check();

		await objectLayoutsPage.createObjectLayoutContent({
			objectLayoutBlockName: 'Block 1',
			objectLayoutName,
			objectLayoutTabName: 'Field Tab',
		});

		await objectLayoutsPage.iframeLocator
			.getByRole('option', {name: 'Custom Field Optional'})
			.click();

		await objectLayoutsPage.saveAddFieldButton.click();

		await objectLayoutsPage.openObjectLayoutObjectField();

		await objectLayoutsPage.iframeLocator
			.getByRole('option', {name: 'Relationship Optional'})
			.click();

		await objectLayoutsPage.saveAddFieldButton.click();

		await objectLayoutsPage.createObjectRelationshipTab(
			objectLayoutName,
			'Relationship Tab',
			'Relationship'
		);

		await editObjectDetailsPage.goto(objectDefinition.name);

		await editObjectDetailsPage.saveButton.click();

		await waitForAlert(page, 'Success:The object was saved successfully.');

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await page
			.getByRole('link', {name: objectEntryB.id.toString()})
			.click();

		await page.getByPlaceholder('Search').click();

		await page.getByRole('menuitem', {name: 'Entry A'}).click();

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await waitForAlert(page);

		await page.getByRole('link', {name: 'Relationship Tab'}).click();

		await page.getByTestId('fdsCreationActionButton').first().click();

		await page.getByRole('menuitem', {name: 'Select Existing One'}).click();

		await expect(viewObjectEntriesPage.searchButton).toBeEnabled();

		await viewObjectEntriesPage.frameSelect.getByText('Entry A').click();

		await page.waitForTimeout(2000);

		await page.getByRole('link', {name: 'Field Tab'}).click();

		await expect(viewObjectEntriesPage.saveObjectEntryButton).toBeEnabled();

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await waitForAlert(page);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await page
			.getByRole('link', {name: objectEntryA.id.toString()})
			.click();

		await page.getByRole('link', {name: 'Relationship Tab'}).click();

		await viewObjectEntriesPage.frontendDatasetActions.click();

		await viewObjectEntriesPage.frontendDatasetDeleteAction.click();

		await page.waitForTimeout(2000);

		await page.getByRole('link', {name: 'Field Tab'}).click();

		await expect(viewObjectEntriesPage.saveObjectEntryButton).toBeEnabled();

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await waitForAlert(page);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await page
			.getByRole('link', {name: objectEntryB.id.toString()})
			.click();

		await expect(page.getByPlaceholder('Search')).not.toContainText(
			'Entry A'
		);
	});

	test('can edit object entry relationship', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		let objectDefinition;
		let objectEntryB;

		await test.step('Setup', async () => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: [
					{
						businessType: 'Text',
						label: {en_US: 'Custom Field'},
						name: 'customField',
					},
				],
			});

			objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields,
					panelCategoryKey: 'control_panel.object',
					status: {code: 0},
					titleObjectFieldName: 'customField',
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			const objectRelationship =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					objectDefinition.externalReferenceCode,
					{
						label: {
							en_US: 'Relationship',
						},
						name: 'relationship',
						objectDefinitionExternalReferenceCode1:
							objectDefinition.externalReferenceCode,
						objectDefinitionExternalReferenceCode2:
							objectDefinition.externalReferenceCode,
						objectDefinitionId1: objectDefinition.id,
						objectDefinitionId2: objectDefinition.id,
						type: 'oneToMany',
					}
				);

			const applicationName =
				'c/' + objectDefinition.name.toLowerCase() + 's';

			const objectEntryA = await apiHelpers.objectEntry.postObjectEntry(
				{
					customField: 'Entry A',
				},
				applicationName
			);

			objectEntryB = await apiHelpers.objectEntry.postObjectEntry(
				{
					customField: 'Entry B',
					[objectRelationship.body.objectField.name]:
						objectEntryA.id.toString(),
				},
				applicationName
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					customField: 'Entry C',
				},
				applicationName
			);
		});

		await test.step('Assert that the object entry relationship can be updated', async () => {
			await viewObjectEntriesPage.goto(objectDefinition.className);

			await page
				.getByRole('link', {name: objectEntryB.id.toString()})
				.click();

			await expect(page.getByPlaceholder('Search')).toHaveValue(
				'Entry A'
			);

			await page.getByPlaceholder('Search').click();

			await page.getByRole('menuitem', {name: 'Entry C'}).click();

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await waitForAlert(page);

			await viewObjectEntriesPage.goto(objectDefinition.className);

			await page
				.getByRole('link', {name: objectEntryB.id.toString()})
				.click();

			await expect(page.getByPlaceholder('Search')).toHaveValue(
				'Entry C'
			);
		});
	});

	test('change the object entry status from Draft to Approved after processing an update', async ({
		apiHelpers,
		objectLayoutsPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['Text'],
		});

		const objectField: ObjectField = objectFields[0];

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				enableObjectEntryDraft: true,
				label: {
					en_US: 'ObjectDefinitionLabel' + getRandomInt(),
				},
				name: 'ObjectDefinitionName' + getRandomInt(),
				objectFields,
				pluralLabel: {
					en_US: 'ObjectDefinitionLabel' + getRandomInt(),
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

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		const objectEntry1 = await apiHelpers.objectEntry.postObjectEntry(
			{
				[objectField.name]: 'test',
				status: {
					code: 2,
					label: 'draft',
					label_i18n: 'Draft',
				},
			},
			applicationName
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(page.getByRole('cell', {name: 'Draft'})).toBeVisible();

		await page.getByRole('link', {name: String(objectEntry1.id)}).click();

		await viewObjectEntriesPage.fillObjectEntry({
			objectFieldBusinessType: 'Text',
			objectFieldLabel: objectField.label['en_US'],
			objectFieldValue: 'test 1',
		});

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(viewObjectEntriesPage.successMessage).toBeVisible();

		await viewObjectEntriesPage.backButton.click();

		await expect(page.getByRole('cell', {name: 'Approved'})).toBeVisible();

		await expect(
			page.locator('td').getByText('test 1', {exact: true})
		).toBeVisible();

		const objectLayoutName = getRandomString();

		await objectLayoutsPage.goto(objectDefinition.label['en_US']);

		await objectLayoutsPage.createObjectLayout(objectLayoutName);

		await objectLayoutsPage.createObjectLayoutContent({
			objectLayoutBlockName: getRandomString(),
			objectLayoutName,
			objectLayoutTabName: getRandomString(),
		});

		await objectLayoutsPage.addObjectLayoutObjectField(
			objectField.label['en_US']
		);

		const objectEntry2 = await apiHelpers.objectEntry.postObjectEntry(
			{
				[objectField.name]: 'test',
				status: {
					code: 2,
					label: 'draft',
					label_i18n: 'Draft',
				},
			},
			applicationName
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(page.getByRole('cell', {name: 'Draft'})).toBeVisible();

		await page.getByRole('link', {name: String(objectEntry2.id)}).click();

		await viewObjectEntriesPage.fillObjectEntry({
			objectFieldBusinessType: 'Text',
			objectFieldLabel: objectField.label['en_US'],
			objectFieldValue: 'test 2',
		});

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(viewObjectEntriesPage.successMessage).toBeVisible();

		await viewObjectEntriesPage.backButton.click();

		await expect(
			page.getByRole('cell', {name: 'Approved'}).nth(1)
		).toBeVisible();

		await expect(
			page.locator('td').getByText('test 2', {exact: true})
		).toBeVisible();
	});

	test(
		'different versions of Commerce Products have same input values when used as relationship of an object entry',
		{tag: '@LPD-65249'},
		async ({
			apiHelpers,
			commerceCatalogSystemSettingsPage,
			page,
			viewObjectEntriesPage,
		}) => {
			const objectDefinitionLabel =
				'ObjectDefinitionLabel' + getRandomInt();

			const objectDefinitionName =
				'ObjectDefinitionName' + getRandomInt();

			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['Text'],
			});

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {body: objectDefinition} =
				await objectDefinitionAPIClient.postObjectDefinition({
					active: true,
					label: {
						en_US: objectDefinitionLabel,
					},
					name: objectDefinitionName,
					objectFields,
					pluralLabel: {
						en_US: objectDefinitionLabel,
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

			const objectRelationshipLabel =
				'objectRelationshipLabel' + getRandomInt();

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
				'L_COMMERCE_PRODUCT_DEFINITION',
				{
					label: {
						en_US: objectRelationshipLabel,
					},
					name: 'objectRelationshipName',
					objectDefinitionExternalReferenceCode1:
						'L_COMMERCE_PRODUCT_DEFINITION',
					objectDefinitionExternalReferenceCode2:
						objectDefinition.externalReferenceCode,
					type: 'oneToMany',
				}
			);

			const catalog =
				await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

			const productVersion1 =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					catalogId: catalog.id,
				});

			await commerceCatalogSystemSettingsPage.toggleProductVersioning();

			await apiHelpers.headlessCommerceAdminCatalog.patchProduct(
				productVersion1.productId.toString()
			);

			const productVersion2 =
				await apiHelpers.headlessCommerceAdminCatalog.getProductByVersion(
					productVersion1.productId,
					2
				);

			await viewObjectEntriesPage.goto(objectDefinition.className);

			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinition.label['en_US']
			);

			await viewObjectEntriesPage.selectDropdownItemWithSearch(
				productVersion1.name['en_US']
			);

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await expect(viewObjectEntriesPage.successMessage).toBeVisible();

			const fieldContainer = page.locator(
				'[data-field-name="r_objectRelationshipName_CProductId"]'
			);

			const productVersion1Value = await fieldContainer
				.locator('input[type="hidden"][name]:not([name$="_edited"])')
				.inputValue();

			await viewObjectEntriesPage.selectDropdownItemWithSearch(
				productVersion2.name['en_US']
			);

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await expect(viewObjectEntriesPage.successMessage).toBeVisible();

			const productVersion2Value = await fieldContainer
				.locator('input[type="hidden"][name]:not([name$="_edited"])')
				.inputValue();

			await expect(productVersion2Value).toEqual(productVersion1Value);

			await apiHelpers.headlessCommerceAdminCatalog.deleteProductByVersion(
				productVersion1.productId,
				2
			);

			await apiHelpers.headlessCommerceAdminCatalog.deleteProductByVersion(
				productVersion1.productId,
				1
			);

			await commerceCatalogSystemSettingsPage.toggleProductVersioning();
		}
	);

	test('error message is displayed in the language of the site context', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: [
				{
					businessType: 'Text',
					label: {ar_SA: 'النص مطلوب', en_US: 'Text Required'},
					required: true,
				},
			],
		});

		const objectDefinitionName = 'ObjectDefinition' + getRandomInt();

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				label: {
					ar_SA: objectDefinitionName + 'ar_SA',
					en_US: objectDefinitionName + 'en_US',
				},
				name: objectDefinitionName,
				objectFields,
				pluralLabel: {
					en_US: objectDefinitionName + 's',
				},
				scope: 'company',
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		siteLanguage = 'ar';

		await viewObjectEntriesPage.goto(
			objectDefinition.className,
			siteLanguage
		);

		await page
			.getByRole('button', {
				name: `إضافة ${objectDefinition.label['ar_SA']}`,
			})
			.first()
			.click();

		await page.getByRole('textbox', {name: 'النص مطلوب'}).click();

		await viewObjectEntriesPage.saveObjectEntryButtonArabic.click();

		await expect(page.getByText('هذا الحقل مطلوب.')).toBeVisible();
	});

	test(
		'error message is displayed when trying to view a deleted object entry with a user with view-only permissions',
		{tag: ['@LPD-61276']},
		async ({apiHelpers, page, viewObjectEntriesPage}) => {
			let entryUrl: string;

			const objectName = 'ObjectName' + getRandomInt();

			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['Text'],
			});

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {body: customObject} =
				await objectDefinitionAPIClient.postObjectDefinition({
					active: true,
					label: {
						en_US: objectName,
					},
					name: objectName,
					objectFields,
					pluralLabel: {
						en_US: objectName + 's',
					},
					scope: 'company',
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: customObject.id,
				type: 'objectDefinition',
			});

			const company =
				await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
					'liferay.com'
				);

			const user = await createUserWithPermissions({
				apiHelpers,
				rolePermissions: [
					{
						actionIds: ['VIEW_CONTROL_PANEL'],
						primaryKey: company.companyId,
						resourceName: '90',
						scope: 1,
					},
					{
						actionIds: ['ACCESS_IN_CONTROL_PANEL'],
						primaryKey: company.companyId,
						resourceName:
							'com_liferay_users_admin_web_portlet_UsersAdminPortlet',
						scope: 1,
					},
					{
						actionIds: ['ACCESS_IN_CONTROL_PANEL'],
						primaryKey: company.companyId,
						resourceName: `com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet_${customObject.className.split('#')[1]}`,
						scope: 1,
					},
					{
						actionIds: ['VIEW'],
						primaryKey: company.companyId,
						resourceName: `${customObject.className}`,
						scope: 1,
					},
				],
			});

			await test.step('Create object entry and get its URL', async () => {
				await viewObjectEntriesPage.goto(customObject.className);

				await viewObjectEntriesPage.addObjectEntryButton.click();

				const objectFieldName = objectFields[0].name;

				await page.getByLabel(objectFieldName).fill(getRandomString());

				await viewObjectEntriesPage.saveObjectEntryButton.click();

				await page.waitForURL(/externalReferenceCode=/);

				entryUrl = page.url();
			});

			await test.step('Delete the object entry', async () => {
				await viewObjectEntriesPage.backButton.click();

				await viewObjectEntriesPage.frontendDatasetActions.click();

				await viewObjectEntriesPage.frontendDatasetDeleteAction.click();

				await page.getByRole('button', {name: 'Delete'}).click();
			});

			await test.step('Switch user and assert that the error message is displayed', async () => {
				await performUserSwitch(page, user.alternateName);

				await page.goto(entryUrl);

				await expect(
					page.getByText('Error:The object entry could not be found.')
				).toBeVisible();
			});
		}
	);

	test('loading element count is one even when pressing save button multiple times', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: [
				{
					businessType: 'Text',
					required: true,
				},
			],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.addObjectEntryButton.click();

		for (let i = 0; i <= 10; i++) {
			await viewObjectEntriesPage.saveObjectEntryButton.click();
		}

		await expect(page.locator('.loading-animation')).toHaveCount(1);
	});

	test(
		'multiselect picklist field does not flicker',
		{tag: ['@LPD-26139', '@LPD-56673']},
		async ({apiHelpers, page, viewObjectEntriesPage}) => {
			const placeHolderText = 'Choose Options';

			const multiselectPicklistFieldKeepsAttached = async () => {
				return await evaluateKeepCheckingAfterFound({
					duration: 4000,
					page,
					selector: `input[placeholder="${placeHolderText}"]`,
				});
			};

			const {listTypeDefinition, listTypeEntries} =
				await postListTypeDefinitionListTypeEntries({
					apiHelpers,
				});

			const objectFields = generateObjectFields({
				listTypeDefinitionExternalReferenceCode:
					listTypeDefinition.externalReferenceCode,
				objectFieldBusinessTypes: [
					'MultiselectPicklist',
					{
						businessType: 'Text',
						label: {en_US: 'Text Field'},
						required: true,
					},
				],
			});

			await test.step('setup and navigate to add object entry', async () => {
				apiHelpers.data.push({
					id: listTypeDefinition.id,
					type: 'listTypeDefinition',
				});

				const objectDefinitionAPIClient =
					await apiHelpers.buildRestClient(ObjectDefinitionAPI);

				const {body: objectDefinition} =
					await objectDefinitionAPIClient.postObjectDefinition({
						active: true,
						externalReferenceCode: getRandomString(),
						label: {
							en_US: getRandomString(),
						},
						name: 'ObjectDefinitionName' + getRandomInt(),
						objectFields,
						panelCategoryKey: 'control_panel.object',
						pluralLabel: {
							en_US: 'NewObject',
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

				await viewObjectEntriesPage.goto(objectDefinition.className);

				await viewObjectEntriesPage.clickAddObjectEntry(
					objectDefinition.label['en_US']
				);

				await page.waitForLoadState('domcontentloaded');
			});

			await test.step('Assert that it does not flicker when option is deselected', async () => {
				await expect(
					page.getByPlaceholder(placeHolderText)
				).toBeVisible();

				await page.getByPlaceholder(placeHolderText).click();

				const firstOptionName = listTypeEntries[0].name;

				await page.getByTestId(`labelItem-${firstOptionName}`).click();

				await expect
					.soft(page.getByText(firstOptionName, {exact: true}))
					.toBeVisible({timeout: 50});

				const removeOptionButton = page.getByLabel(
					'Remove ' + firstOptionName
				);

				await removeOptionButton.click();

				expect
					.soft(await multiselectPicklistFieldKeepsAttached())
					.toBeTruthy();
			});

			await test.step('Assert that it does not flicker when interacting with mandatory field', async () => {
				const textField = page.getByLabel('Text Field');

				await textField.focus();

				await textField.press('a');

				expect
					.soft(await multiselectPicklistFieldKeepsAttached())
					.toBeTruthy();
			});

			expect(test.info().errors).toHaveLength(0);
		}
	);

	test('verify that relationship API is called only once when adding object entry', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
			'L_ACCOUNT',
			{
				label: {
					en_US: 'objectRelationshipLabel' + getRandomInt(),
				},
				name: 'objectRelationshipName' + getRandomInt(),
				objectDefinitionExternalReferenceCode1: 'L_ACCOUNT',
				objectDefinitionExternalReferenceCode2:
					objectDefinition.externalReferenceCode,
				type: 'oneToMany',
			}
		);

		let apiCalls = 0;

		page.on('request', (request) => {
			if (
				request
					.url()
					.includes('/o/headless-admin-user/v1.0/accounts') &&
				request.method() === 'GET'
			) {
				apiCalls++;
			}
		});

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.addObjectEntryButton.click();

		await page.waitForResponse(
			(response) =>
				response
					.url()
					.includes('/o/headless-admin-user/v1.0/accounts') &&
				response.request().method() === 'GET'
		);

		expect(apiCalls).toBe(1);
	});

	test('verify that its not possible to paste file on richText field', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['RichText'],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await test.step('go to entry page, try to upload file by pasting it into editor and verify error message', async () => {
			await viewObjectEntriesPage.goto(objectDefinition.className);

			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinition.label['en_US']
			);

			const editorFrame = page.frameLocator('iframe[title="editor"]');

			const editorBody = editorFrame.locator('body');

			const file = fs.readFileSync(
				path.join(__dirname, 'dependencies', 'tree.png')
			);

			await pasteFile(editorBody, {
				buffer: file,
				fileName: 'tree.png',
				fileType: 'image/png',
			});

			await expect(editorFrame.locator('img')).not.toBeVisible();
		});
	});

	test('Verify that temporary files are deleted from the database if the object creation is not completed', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {

		// Create object definition with attachment object field

		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: [
				{
					businessType: 'Attachment',
					name: 'testAttachment',
					objectFieldSettings: [
						{
							name: 'acceptedFileExtensions',
							value: 'jpeg, jpg, pdf, png, txt',
						},
						{
							name: 'maximumFileSize',
							value: 100,
						},
						{
							name: 'fileSource',
							value: 'userComputer',
						},
						{
							name: 'showFilesInDocumentsAndMedia',
							value: false,
						},
					],
				},
			],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.clickAddObjectEntry(objectDefinition.name);

		// Upload first file from user computer

		await viewObjectEntriesPage.selectFileFromUserComputer(
			__dirname,
			'sampleFile.txt'
		);

		await page
			.getByRole('button', {name: 'sampleFile.txt'})
			.waitFor({state: 'visible'});

		const fileEntryId1 = await page.getAttribute(
			'input[data-field-name^="testAttachment"]',
			'value'
		);

		expect(
			await apiHelpers.headlessDelivery.getDocument(fileEntryId1)
		).toEqual(
			expect.objectContaining({
				id: Number(fileEntryId1),
			})
		);

		// Verify that the first file is removed after the second file is uploaded

		await viewObjectEntriesPage.selectFileFromUserComputer(
			__dirname,
			'astronaut.png'
		);

		await page
			.getByRole('button', {name: 'astronaut.png'})
			.waitFor({state: 'visible'});

		expect(
			await apiHelpers.headlessDelivery.getDocument(fileEntryId1)
		).toEqual({status: 'NOT_FOUND'});

		const fileEntryId2 = await page.getAttribute(
			'input[data-field-name^="testAttachment"]',
			'value'
		);

		expect(
			await apiHelpers.headlessDelivery.getDocument(fileEntryId2)
		).toEqual(
			expect.objectContaining({
				id: Number(fileEntryId2),
			})
		);

		// Verify that the delete button removes the second file

		await viewObjectEntriesPage.deleteFileButton.click();

		expect(
			await apiHelpers.headlessDelivery.getDocument(fileEntryId2)
		).toEqual({status: 'NOT_FOUND'});

		// Verify that the file is removed after page reload

		await viewObjectEntriesPage.selectFileFromUserComputer(
			__dirname,
			'sampleFile.txt'
		);

		await page
			.getByRole('button', {name: 'sampleFile.txt'})
			.waitFor({state: 'visible'});

		const fileEntryId3 = await page.getAttribute(
			'input[data-field-name^="testAttachment"]',
			'value'
		);

		await page.reload();

		expect(
			await apiHelpers.headlessDelivery.getDocument(fileEntryId3)
		).toEqual({status: 'NOT_FOUND'});

		// Verify that the file is saved successfully when clicking submit

		await viewObjectEntriesPage.selectFileFromUserComputer(
			__dirname,
			'astronaut.png'
		);

		await page
			.getByRole('button', {name: 'astronaut.png'})
			.waitFor({state: 'visible'});

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(viewObjectEntriesPage.successMessage).toBeVisible();
		await expect(
			viewObjectEntriesPage.page.getByText('astronaut.png')
		).toBeVisible();

		await viewObjectEntriesPage.selectFileFromUserComputer(
			__dirname,
			'sampleFile.txt'
		);

		await page
			.getByRole('button', {name: 'sampleFile.txt'})
			.waitFor({state: 'visible'});

		await page.reload();

		await expect(
			viewObjectEntriesPage.page.getByText('astronaut.png')
		).toBeVisible();
	});
});

test.describe('Manage object entries through Workflow', () => {
	test('can edit object entry through workflow task page', async ({
		apiHelpers,
		applicationsMenuPage,
		configurationTabPage,
		page,
		viewObjectEntriesPage,
		workflowTaskDetailsPage,
		workflowTasksPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
				titleObjectFieldName: 'textField',
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await applicationsMenuPage.goToProcessBuilder();

		await configurationTabPage.configurationTabLink.click();

		await configurationTabPage.assignWorkflowToAssetType(
			'Single Approver',
			objectDefinition.label['en_US']
		);

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		await apiHelpers.objectEntry.postObjectEntry(
			{textField: 'entry'},
			applicationName
		);

		await workflowTasksPage.goToAssignedToMyRoles();

		await workflowTasksPage.assignToMe('entry');

		await workflowTasksPage.goto();

		await workflowTaskDetailsPage.selectAsset(
			objectDefinition.label['en_US']
		);

		await workflowTaskDetailsPage.editAssetButton.click();

		const objectFieldValue = getRandomString();

		await viewObjectEntriesPage.fillObjectEntry({
			objectFieldBusinessType: 'Text',
			objectFieldLabel: objectDefinition.titleObjectFieldName,
			objectFieldValue,
		});

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(viewObjectEntriesPage.successMessage).toBeVisible();

		await viewObjectEntriesPage.backButton.click();

		await expect(page.getByLabel('textField', {exact: true})).toHaveValue(
			objectFieldValue
		);
	});

	test('can view Asset Title, Asset Type and Item Subject of an entry on metrics page', async ({
		apiHelpers,
		applicationsMenuPage,
		configurationTabPage,
		metricsPage,
		page,
	}) => {
		const assetType = 'Single Approver';

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
				titleObjectFieldName: 'textField',
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await applicationsMenuPage.goToProcessBuilder();

		await configurationTabPage.configurationTabLink.click();

		await configurationTabPage.assignWorkflowToAssetType(
			assetType,
			objectDefinition.label['en_US']
		);

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{textField: 'entry'},
			applicationName
		);

		await applicationsMenuPage.goToMetrics();

		await metricsPage.chooseProcess(assetType);

		await metricsPage.viewAllPendingItems();

		const itemSubject =
			objectDefinition.label['en_US'] + ': ' + objectEntry.textField;

		await expect(page.getByLabel(itemSubject)).toBeVisible();

		await page.locator('.link-text').click();

		await expect(
			page.getByText(objectDefinition.label['en_US'])
		).toBeVisible();

		await expect(page.getByText(objectEntry.textField)).toBeVisible();
	});

	test(
		"Date and time are adjusted to the user's time zone",
		{tag: '@LPD-54895'},
		async ({
			apiHelpers,
			page,
			usersAndOrganizationsPage,
			viewObjectEntriesPage,
		}) => {

			// Create object definition with date time

			const objectDefinitionLabel =
				'ObjectDefinitionLabel' + getRandomInt();
			const objectDefinitionName =
				'ObjectDefinitionName' + getRandomInt();

			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['DateTime'],
			});

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {body: objectDefinition} =
				await objectDefinitionAPIClient.postObjectDefinition({
					active: true,
					enableLocalization: true,
					label: {
						en_US: objectDefinitionLabel,
					},
					name: objectDefinitionName,
					objectFields,
					pluralLabel: {
						en_US: objectDefinitionLabel,
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

			await viewObjectEntriesPage.goto(objectDefinition.className);

			// Create object entry date time

			await viewObjectEntriesPage.addObjectEntryButton.click();

			await viewObjectEntriesPage.dateTimeInput.fill(
				'10/05/2025 12:00 PM'
			);

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await expect(viewObjectEntriesPage.successMessage).toBeVisible();

			// Create user with permissions

			const company =
				await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
					'liferay.com'
				);

			const user = await createUserWithPermissions({
				apiHelpers,
				rolePermissions: [
					{
						actionIds: ['VIEW_CONTROL_PANEL'],
						primaryKey: company.companyId,
						resourceName: '90',
						scope: 1,
					},
					{
						actionIds: ['ACCESS_IN_CONTROL_PANEL'],
						primaryKey: company.companyId,
						resourceName:
							'com_liferay_users_admin_web_portlet_UsersAdminPortlet',
						scope: 1,
					},
					{
						actionIds: ['ACCESS_IN_CONTROL_PANEL'],
						primaryKey: company.companyId,
						resourceName: `com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet_${objectDefinition.className.split('#')[1]}`,
						scope: 1,
					},
					{
						actionIds: ['VIEW'],
						primaryKey: company.companyId,
						resourceName: `${objectDefinition.className}`,
						scope: 1,
					},
				],
			});

			// Switch to created user

			await performUserSwitch(page, user.alternateName);

			// Change user timezone

			await usersAndOrganizationsPage.goToUsersWithLimitedAccess();

			await (
				await usersAndOrganizationsPage.usersTableRowLink(
					user.alternateName
				)
			).click();

			await usersAndOrganizationsPage.userPreferencesButton.click();

			await usersAndOrganizationsPage.displaySettingsButton.click();

			await usersAndOrganizationsPage.timeZoneSelect.selectOption(
				'America/Sao_Paulo'
			);

			await usersAndOrganizationsPage.saveTimeZoneButton.click();

			// Check if the time has changed

			await viewObjectEntriesPage.goToObjectDefinitionEntry(
				objectDefinition.className
			);

			await expect(
				page.locator(
					'input[placeholder="__/__/____ __:__ _"][value="10/05/2025 09:00 AM"]'
				)
			).toHaveValue('10/05/2025 09:00 AM');
		}
	);
});

scheduleTest.describe('Manage object entries schedule properties', () => {
	let _objectDefinition: ObjectDefinition;

	scheduleTest.afterEach(async ({accountSettingsPage}) => {
		await accountSettingsPage.goToDisplaySettings();

		await accountSettingsPage.setTimeZone('UTC');
	});

	scheduleTest.beforeEach(async ({accountSettingsPage, apiHelpers, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		_objectDefinition = objectDefinition;

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const shouldEnableConfiguration = !scheduleTest
			.info()
			.tags.includes('@enableObjectEntryScheduleFalse');

		if (shouldEnableConfiguration) {
			await objectDefinitionAPIClient.patchObjectDefinition(
				_objectDefinition.id,
				{
					enableObjectEntrySchedule: true,
				}
			);
		}

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const utcOffsetFormatted = getUTCOffsetFormatted(new Date());

		await accountSettingsPage.goToDisplaySettings();

		if (utcOffsetFormatted === 'UTC') {
			return await accountSettingsPage.setTimeZone('UTC');
		}

		const timeZoneValue = await page
			.locator('select option', {hasText: utcOffsetFormatted})
			.first()
			.getAttribute('value');

		await accountSettingsPage.setTimeZone(timeZoneValue);
	});

	scheduleTest(
		'can create, read, update, and delete a displayDate of an object entry',
		async ({page, viewObjectEntriesPage}) => {
			await viewObjectEntriesPage.goto(_objectDefinition.className);

			await viewObjectEntriesPage.clickAddObjectEntry(
				_objectDefinition.label['en_US']
			);

			await viewObjectEntriesPage.choosePublicationOption('schedule');

			await viewObjectEntriesPage.scheduleForCurrentDate('Publish');

			await page.keyboard.press('Escape');

			await viewObjectEntriesPage.schedulePublicationButton.click();

			await waitForAlert(page);

			const date = new Date();

			const today = getObjectEntryUIDateTimeFormat(date);

			await viewObjectEntriesPage.choosePublicationOption('schedule');

			await expect(viewObjectEntriesPage.publishDateInput).toHaveValue(
				today
			);

			date.setDate(date.getDate() + 1);

			const tomorrow = getObjectEntryUIDateTimeFormat(date);

			await viewObjectEntriesPage.publishDateInput.fill(tomorrow);

			await viewObjectEntriesPage.schedulePublicationButton.click();

			await waitForAlert(page);

			await viewObjectEntriesPage.choosePublicationOption('schedule');

			await expect(viewObjectEntriesPage.publishDateInput).toHaveValue(
				tomorrow
			);

			await page.getByRole('button', {name: 'Close'}).click();

			await viewObjectEntriesPage.choosePublicationOption('publish');

			await waitForAlert(page);

			await viewObjectEntriesPage.choosePublicationOption('schedule');

			await expect(viewObjectEntriesPage.publishDateInput).toHaveValue(
				''
			);

			await page.getByRole('button', {name: 'Close'}).click();
		}
	);

	scheduleTest(
		'can create, read, update, and delete a expirationDate of an object entry',
		async ({page, viewObjectEntriesPage}) => {
			await viewObjectEntriesPage.goto(_objectDefinition.className);

			await viewObjectEntriesPage.clickAddObjectEntry(
				_objectDefinition.label['en_US']
			);

			await viewObjectEntriesPage.neverExpire.uncheck();

			const date = new Date();

			// Add a few minutes since expiration cant be scheduled for current dateTime

			date.setMinutes(date.getMinutes() + 2);

			const today = getObjectEntryUIDateTimeFormat(date);

			await viewObjectEntriesPage.expirationDateInput.fill(today);

			await page.keyboard.press('Escape');

			await viewObjectEntriesPage.choosePublicationOption('publish');

			await waitForAlert(page);

			await expect(viewObjectEntriesPage.expirationDateInput).toHaveValue(
				today
			);

			date.setDate(date.getDate() + 1);

			const tomorrow = getObjectEntryUIDateTimeFormat(date);

			await viewObjectEntriesPage.expirationDateInput.fill(tomorrow);

			await viewObjectEntriesPage.choosePublicationOption('publish');

			await waitForAlert(page);

			await expect(viewObjectEntriesPage.expirationDateInput).toHaveValue(
				tomorrow
			);

			await viewObjectEntriesPage.neverExpire.check();

			await viewObjectEntriesPage.choosePublicationOption('publish');

			await waitForAlert(page);

			await expect(viewObjectEntriesPage.expirationDateInput).toHaveValue(
				''
			);
		}
	);

	scheduleTest(
		'can create, read, update, and delete a reviewDate of an object entry',
		async ({page, viewObjectEntriesPage}) => {
			await viewObjectEntriesPage.goto(_objectDefinition.className);

			await viewObjectEntriesPage.clickAddObjectEntry(
				_objectDefinition.label['en_US']
			);

			await viewObjectEntriesPage.neverReview.uncheck();

			await viewObjectEntriesPage.scheduleForCurrentDate('Review');

			await page.keyboard.press('Escape');

			await viewObjectEntriesPage.choosePublicationOption('publish');

			await waitForAlert(page);

			const date = new Date();

			const today = getObjectEntryUIDateTimeFormat(date);

			await expect(viewObjectEntriesPage.reviewDateInput).toHaveValue(
				today
			);

			date.setDate(date.getDate() + 1);

			const tomorrow = getObjectEntryUIDateTimeFormat(date);

			await viewObjectEntriesPage.reviewDateInput.fill(tomorrow);

			await viewObjectEntriesPage.choosePublicationOption('publish');

			await waitForAlert(page);

			await expect(viewObjectEntriesPage.reviewDateInput).toHaveValue(
				tomorrow
			);

			await viewObjectEntriesPage.neverReview.check();

			await viewObjectEntriesPage.choosePublicationOption('publish');

			await waitForAlert(page);

			await expect(viewObjectEntriesPage.reviewDateInput).toHaveValue('');
		}
	);

	scheduleTest(
		'can see approved and scheduled labels for entry with a display date versioning enabled and at least one version approved',
		async ({apiHelpers, page, viewObjectEntriesPage}) => {
			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			await objectDefinitionAPIClient.patchObjectDefinition(
				_objectDefinition.id,
				{
					enableObjectEntryVersioning: true,
				}
			);

			await viewObjectEntriesPage.goto(_objectDefinition.className);

			await viewObjectEntriesPage.clickAddObjectEntry(
				_objectDefinition.label['en_US']
			);

			await viewObjectEntriesPage.choosePublicationOption('publish');

			await waitForAlert(page);

			await viewObjectEntriesPage.backButton.click();

			await expect(page.getByText('Approved')).toBeVisible();

			await expect(page.getByText('Scheduled')).not.toBeVisible();

			const applicationName =
				'c/' + _objectDefinition.name.toLowerCase() + 's';

			const {items} =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
					applicationName
				);

			const objectEntryId = items[0].id;

			await page.getByRole('link', {name: objectEntryId}).click();

			const date = new Date();

			date.setDate(date.getDate() + 1);

			const tomorrow = getObjectEntryUIDateTimeFormat(date);

			await viewObjectEntriesPage.choosePublicationOption('schedule');

			await viewObjectEntriesPage.publishDateInput.fill(tomorrow);

			await viewObjectEntriesPage.schedulePublicationButton.click();

			await waitForAlert(page);

			await viewObjectEntriesPage.backButton.click();

			await expect(page.getByText('Approved')).toBeVisible();

			await expect(page.getByText('Scheduled')).toBeVisible();
		}
	);

	scheduleTest(
		'cannot submit an empty displayDate',
		async ({page, viewObjectEntriesPage}) => {
			await viewObjectEntriesPage.goto(_objectDefinition.className);

			await viewObjectEntriesPage.clickAddObjectEntry(
				_objectDefinition.label['en_US']
			);

			await viewObjectEntriesPage.choosePublicationOption('schedule');

			let requestWasMade = false;

			page.on('request', (request) => {
				if (request.url().includes(_objectDefinition.restContextPath)) {
					requestWasMade = true;
				}
			});

			await viewObjectEntriesPage.schedulePublicationButton.click();

			// Wait a second before doing the assertion to simulate the time needed for the request to happen

			await page.waitForTimeout(1000);

			expect(requestWasMade).toBe(false);

			await expect(
				page.getByText('This field is required')
			).toBeVisible();

			await viewObjectEntriesPage.schedulePublicationCloseButton.click();
		}
	);

	scheduleTest(
		'cannot submit an empty expirationDate and reviewDate when it is enabled',
		async ({page, viewObjectEntriesPage}) => {
			await viewObjectEntriesPage.goto(_objectDefinition.className);

			await viewObjectEntriesPage.clickAddObjectEntry(
				_objectDefinition.label['en_US']
			);

			for (const scheduleProperty of ['Expire', 'Review']) {
				await viewObjectEntriesPage.page
					.getByLabel(`Never ${scheduleProperty}`, {exact: true})
					.uncheck();

				let requestWasMade = false;

				page.on('request', (request) => {
					if (
						request
							.url()
							.includes(_objectDefinition.restContextPath)
					) {
						requestWasMade = true;
					}
				});

				await viewObjectEntriesPage.choosePublicationOption('publish');

				// Wait a second before doing the assertion to simulate the time needed for the request to happen

				await page.waitForTimeout(1000);

				expect(requestWasMade).toBe(false);

				await expect(
					page.getByText('This field is required')
				).toBeVisible();

				await viewObjectEntriesPage.page
					.getByLabel(`Never ${scheduleProperty}`, {exact: true})
					.check();
			}
		}
	);

	scheduleTest(
		'cannot submit a past expirationDate',
		async ({page, viewObjectEntriesPage}) => {
			await viewObjectEntriesPage.goto(_objectDefinition.className);

			await viewObjectEntriesPage.clickAddObjectEntry(
				_objectDefinition.label['en_US']
			);

			await viewObjectEntriesPage.neverExpire.uncheck();

			await viewObjectEntriesPage.scheduleForCurrentDate('Expiration');

			await viewObjectEntriesPage.page.keyboard.press('Escape');

			let requestWasMade = false;

			page.on('request', (request) => {
				if (request.url().includes(_objectDefinition.restContextPath)) {
					requestWasMade = true;
				}
			});

			await viewObjectEntriesPage.choosePublicationOption('publish');

			// Wait a second before doing the assertion to simulate the time needed for the request to happen

			await page.waitForTimeout(1000);

			expect(requestWasMade).toBe(false);

			await expect(
				page.getByText('The date entered is in the past')
			).toBeVisible();
		}
	);

	scheduleTest(
		'schedule container is not visible when enableObjectEntrySchedule is disabled',
		{tag: '@enableObjectEntryScheduleFalse'},
		async ({viewObjectEntriesPage}) => {
			await viewObjectEntriesPage.goto(_objectDefinition.className);

			await viewObjectEntriesPage.clickAddObjectEntry(
				_objectDefinition.label['en_US']
			);

			await expect(
				viewObjectEntriesPage.schedulePanelButton
			).not.toBeVisible();

			await expect(
				viewObjectEntriesPage.expirationDateInput
			).not.toBeVisible();

			await expect(
				viewObjectEntriesPage.reviewDateInput
			).not.toBeVisible();
		}
	);
});

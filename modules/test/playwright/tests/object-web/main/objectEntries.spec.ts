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
import {expect, mergeTests} from '@playwright/test';

import {accountSettingsPagesTest} from '../../../fixtures/accountSettingsPagesTest';
import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';
import {collectionsPagesTest} from '../../../fixtures/collectionsPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {editObjectDefinitionPagesTest} from '../../../fixtures/editObjectDefinitionPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {formsPagesTest} from '../../../fixtures/formsPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
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
import {mockedObjectFields} from './dependencies/objectMockedFields';
import {
	getFDSDateFormat,
	getObjectEntryUIDateTimeFormat,
	getPageEditorDateFormat,
} from './utils/dateFormat';
import evaluateKeepCheckingAfterFound from './utils/keepCheckingAfterFound';
import {createObjectFields, mockObjectFields} from './utils/mockObjectFields';

const test = mergeTests(
	accountSettingsPagesTest,
	applicationsMenuPageTest,
	collectionsPagesTest,
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
	workflowPagesTest,
	usersAndOrganizationsPagesTest
);

const scheduleTest = mergeTests(
	test,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
	})
);

let displayPageId: string;
let siteLanguage = 'en';

test.afterEach(async ({apiHelpers, page}) => {
	if (siteLanguage !== 'en') {
		await page.goto('en');

		siteLanguage = 'en';
	}

	if (displayPageId) {
		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.deleteLayoutPageTemplateEntry(
			{
				layoutPageTemplateEntryId: displayPageId,
			}
		);

		displayPageId = '';
	}
});

test.describe('Manage object entries through Friendly URL', () => {
	let _objectDefinition: ObjectDefinition;
	let _objectEntryFriendlyURLPath: string;
	let _objectField: ObjectField;

	test.beforeEach(async ({apiHelpers, site, viewObjectEntriesPage}) => {
		const {objectFields} = await mockObjectFields({
			apiHelpers,
			localizeAllLocalizable: true,
			objectFieldBusinessTypes: ['text'],
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
				objectFolderExternalReferenceCode: 'default',
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

		const {
			listTypeDefinition,
			objectEntry,
			objectFields,
			titleObjectFieldName,
		} = await mockObjectFields({
			apiHelpers,
			objectEntryReturn: {format: 'API'},
			objectFieldBusinessTypes: [
				'autoIncrement',
				'boolean',
				'date',
				'decimal',
				'encrypted',
				'integer',
				'longInteger',
				'longText',
				'multiselectPicklist',
				'picklist',
				'precisionDecimal',
				'richText',
				'text',
			],
			titleObjectFieldName: 'text',
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
				titleObjectFieldName,
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		await apiHelpers.objectEntry.postObjectEntry(
			objectEntry,
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
					entry: objectEntry[titleObjectFieldName],
					field: objectField.label.en_US,
				},
				source: 'content',
			});

			let matchString: string;

			switch (objectField.businessType) {
				case 'AutoIncrement': {
					matchString = '1';

					break;
				}
				case 'Date': {
					const date = new Date(
						Date.parse(objectEntry[objectField.name])
					);

					matchString = getPageEditorDateFormat(date);

					// Defer date validation for CI trace view analysis (issue #LRCI-4253)

					continue overloop;
				}
				case 'Picklist': {
					matchString = (
						objectEntry[objectField.name] as {key: string}
					).key;

					break;
				}
				case 'MultiselectPicklist': {
					(objectEntry[objectField.name] as string[]).forEach(
						(listTypeEntry, index) => {
							index < 1
								? (matchString = `${listTypeEntry}`)
								: (matchString += `, ${listTypeEntry}`);
						}
					);

					break;
				}
				default: {
					matchString = objectEntry[objectField.name].toString();
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
});

test.describe('Manage object entries through View Object Entries', () => {
	test('can add an entry with all object fields', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const ATTACHMENT_FILE_NAME = 'astronaut.png';
		const {listTypeDefinition, objectEntry, objectFields} =
			await mockObjectFields({
				apiHelpers,
				objectEntryReturn: {format: 'UI'},
				objectFieldBusinessTypes: [
					'attachment',
					'boolean',
					'date',
					'decimal',
					'integer',
					'longInteger',
					'longText',
					'picklist',
					'precisionDecimal',
					'richText',
					'text',
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

		for (const objectField of objectFields) {
			switch (objectField.businessType) {
				case 'Attachment': {
					await viewObjectEntriesPage.selectFileButton.click();

					await viewObjectEntriesPage.selectFileFromDocumentsAndMedia(
						ATTACHMENT_FILE_NAME
					);

					break;
				}
				case 'Boolean': {
					objectEntry[objectField.name]
						? await page
								.getByLabel(objectField.label['en_US'])
								.check()
						: await page
								.getByLabel(objectField.label['en_US'])
								.uncheck();

					break;
				}
				case 'Picklist': {
					await viewObjectEntriesPage.selectDropdownItem(
						objectField.label['en_US'],
						objectEntry[objectField.name].key.toString()
					);

					break;
				}
				default: {
					await viewObjectEntriesPage.fillObjectEntry({
						objectFieldBusinessType: objectField.businessType,
						objectFieldLabel: objectField.label['en_US'],
						objectFieldValue:
							objectEntry[objectField.name].toString(),
					});
				}
			}
		}

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(viewObjectEntriesPage.successMessage).toBeVisible();

		await viewObjectEntriesPage.backButton.click();

		for (const {businessType, name} of objectFields) {
			let matchString: string;

			switch (businessType) {
				case 'Attachment': {
					matchString = ATTACHMENT_FILE_NAME;

					break;
				}
				case 'Boolean': {
					matchString = objectEntry[name] ? 'Yes' : 'No';

					break;
				}
				case 'Date': {
					const date = new Date(objectEntry[name]);

					matchString = getFDSDateFormat(date);

					break;
				}
				case 'Picklist': {
					matchString = (objectEntry[name] as {key: string}).key;

					break;
				}
				case 'MultiselectPicklist': {
					(objectEntry[name] as string[]).forEach(
						(listTypeEntry, index) => {
							index < 1
								? (matchString = `${listTypeEntry}`)
								: (matchString += `, ${listTypeEntry}`);
						}
					);

					break;
				}
				case 'RichText': {
					matchString = objectEntry[name].substring(0, 35);

					break;
				}
				default: {
					matchString = objectEntry[name];
				}
			}

			await expect(
				page.locator('td').getByText(matchString, {exact: true})
			).toBeVisible();
		}
	});

	test('can add and update an entry with multi-select object field', async ({
		apiHelpers,
		formFieldsPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinitionLabel = 'ObjectDefinitionLabel' + getRandomInt();

		const {listTypeDefinitionItems, objectFields, titleObjectFieldName} =
			await mockObjectFields({
				apiHelpers,
				objectFieldBusinessTypes: ['multiselectPicklist'],
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
				titleObjectFieldName,
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.addObjectEntryButton.click();

		await formFieldsPage.addSelectItem(listTypeDefinitionItems[0]);

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(viewObjectEntriesPage.successMessage).toBeVisible();

		await expect(viewObjectEntriesPage.successMessage).toBeHidden();

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(viewObjectEntriesPage.successMessage).toBeVisible();

		await expect(
			page.getByRole('gridcell', {
				exact: true,
				name: listTypeDefinitionItems[0],
			})
		).toBeVisible();
	});

	test(
		'multiselect picklist field does not flicker',
		{tag: ['@LPD-26139', '@LPD-56673']},
		async ({apiHelpers, page, viewObjectEntriesPage}) => {
			let objectEntry: Partial<ObjectEntry>;
			let objectFields: ObjectField[];
			let textFieldData: ObjectField;

			const placeHolderText = 'Choose Options';

			const multiselectPicklistFieldKeepsAttached = async () => {
				return await evaluateKeepCheckingAfterFound({
					duration: 4000,
					page,
					selector: `input[placeholder="${placeHolderText}"]`,
				});
			};

			await test.step('setup and navigate to add object entry', async () => {
				const mockedObjectFields = await mockObjectFields({
					apiHelpers,
					objectEntryReturn: {format: 'UI'},
					objectFieldBusinessTypes: ['text', 'multiselectPicklist'],
				});

				const listTypeDefinition =
					mockedObjectFields.listTypeDefinition;

				objectFields = mockedObjectFields.objectFields;

				objectEntry = mockedObjectFields.objectEntry;

				textFieldData = objectFields[0];

				textFieldData.required = true;

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

				const multiselectPicklistField = objectFields.find(
					({businessType}) => businessType === 'MultiselectPicklist'
				);

				const firstOptionName =
					objectEntry[multiselectPicklistField.name][0];

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
				const textField = page.getByLabel(textFieldData.label['en_US']);

				await textField.focus();

				await textField.press('a');

				expect
					.soft(await multiselectPicklistFieldKeepsAttached())
					.toBeTruthy();
			});

			expect(test.info().errors).toHaveLength(0);
		}
	);

	test('can download and delete a file from the Attachment field when adding an object entry', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const ATTACHMENT_FILE_NAME = 'astronaut.png';
		const {objectFields} = await mockObjectFields({
			apiHelpers,
			objectFieldBusinessTypes: ['attachment'],
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
				objectFolderExternalReferenceCode: 'default',
				status: {code: 0},
				titleObjectFieldName: 'textField',
			});

		apiHelpers.data.push({
			id: objectDefinition1.id,
			type: 'objectDefinition',
		});

		const objectDefinition2 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode: 'default',
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
				objectFolderExternalReferenceCode: 'default',
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
				objectFolderExternalReferenceCode: 'default',
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
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields: [
					mockedObjectFields.attachmentFieldDocumentsAndMedia,
				],
				objectFolderExternalReferenceCode: 'default',
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
		const objectFields = createObjectFields('text', [
			{
				label: 'Custom Field',
				name: 'customField',
			},
		]);

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				objectFolderExternalReferenceCode: 'default',
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

		await page
			.getByTestId('visualization-mode-table')
			.getByText('New')
			.click();

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
			const objectFields = createObjectFields('text', [
				{
					label: 'Custom Field',
					name: 'customField',
				},
			]);

			objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields,
					objectFolderExternalReferenceCode: 'default',
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

	test('Verify that temporary files are deleted from the database if the object creation is not completed', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {

		// Create object definition with attachment object field

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields: [mockedObjectFields.attachmentFieldUserComputer],
				objectFolderExternalReferenceCode: 'default',
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

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(viewObjectEntriesPage.successMessage).toBeVisible();
		await expect(
			viewObjectEntriesPage.page.getByText('astronaut.png')
		).toBeVisible();

		await viewObjectEntriesPage.selectFileFromUserComputer(
			__dirname,
			'sampleFile.txt'
		);

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
				objectFolderExternalReferenceCode: 'default',
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
				objectFolderExternalReferenceCode: 'default',
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

			const {objectFields, titleObjectFieldName} = await mockObjectFields(
				{
					apiHelpers,
					objectFieldBusinessTypes: ['dateTime'],
				}
			);

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
					titleObjectFieldName,
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
	scheduleTest(
		'can create, read, update, and delete a reviewDate of an object entry',
		async ({apiHelpers, page, viewObjectEntriesPage}) => {
			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			await viewObjectEntriesPage.goto(objectDefinition.className);

			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinition.label['en_US']
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
		'cannot submit an empty displayDate',
		async ({apiHelpers, page, viewObjectEntriesPage}) => {
			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			await viewObjectEntriesPage.goto(objectDefinition.className);

			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinition.label['en_US']
			);

			await viewObjectEntriesPage.choosePublicationOption('schedule');

			await viewObjectEntriesPage.schedulePublicationButton.click();

			await expect(
				page.getByText('This field is required')
			).toBeVisible();
		}
	);

	scheduleTest(
		'cannot submit an empty reviewDate when neverReview is not checked',
		async ({apiHelpers, page, viewObjectEntriesPage}) => {
			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			await viewObjectEntriesPage.goto(objectDefinition.className);

			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinition.label['en_US']
			);

			await viewObjectEntriesPage.neverReview.uncheck();

			await viewObjectEntriesPage.choosePublicationOption('publish');

			await expect(
				page.getByText('This field is required')
			).toBeVisible();
		}
	);
});

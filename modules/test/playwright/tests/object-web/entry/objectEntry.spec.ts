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
import fs from 'fs';
import path from 'path';

import {accountSettingsPagesTest} from '../../../fixtures/accountSettingsPagesTest';
import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {collectionsPagesTest} from '../../../fixtures/collectionsPagesTest';
import {commercePagesTest} from '../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {editObjectDefinitionPagesTest} from '../../../fixtures/editObjectDefinitionPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {formsPagesTest} from '../../../fixtures/formsPagesTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import createUserWithPermissions from '../../../utils/createUserWithPermissions';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch, userData} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import createSiteTemplate from '../../layout-set-prototype-web/main/utils/createSiteTemplate';
import {cmsPagesTest} from '../../site-cms-site-initializer/main/fixtures/cmsPagesTest';
import {
	getFDSDateTimeFormat,
	getObjectEntryUIDateTimeFormat,
	getUTCOffsetFormatted,
} from '../utils/dateFormat';
import {createFile, deleteFile} from '../utils/fileHelpers';
import {generateObjectEntryValues} from '../utils/generateObjectEntry';
import {generateObjectFields} from '../utils/generateObjectFields';
import evaluateKeepCheckingAfterFound from '../utils/keepCheckingAfterFound';
import {pasteFile} from '../utils/pasteFile';
import {postListTypeDefinitionListTypeEntries} from '../utils/postListTypeDefinitionListTypeEntries';

const test = mergeTests(
	accountSettingsPagesTest,
	apiHelpersTest,
	collectionsPagesTest,
	commercePagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	editObjectDefinitionPagesTest,
	featureFlagsTest({
		'LPD-83570': {enabled: true}, // Phone Number field
		'LPS-178052': {enabled: true},
	}),
	globalMenuPagesTest,
	formsPagesTest,
	journalPagesTest,
	loginTest(),
	objectPagesTest,
	pageEditorPagesTest,
	pagesAdminPagesTest,
	productMenuPageTest,
	workflowPagesTest,
	usersAndOrganizationsPagesTest
);

const assigneeTest = test;

const cmsTest = mergeTests(
	test,
	cmsPagesTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
		'LPD-17564': {enabled: true},
		'LPD-34594': {enabled: true},
	})
);

const ckEditor4Test = mergeTests(
	test,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
	})
);

let displayPageId: string;
let siteLanguage = 'en';

test.afterEach(async ({accountSettingsPage, apiHelpers, page}) => {
	if (displayPageId) {
		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.deleteLayoutPageTemplateEntry(
			{
				layoutPageTemplateEntryId: displayPageId,
			}
		);

		displayPageId = '';
	}

	if (siteLanguage !== 'en') {
		await accountSettingsPage.selectAccountLanguage({
			languageId: 'en_US',
			navigate: true,
		});

		await page.goto('en');

		siteLanguage = 'en';
	}
});

assigneeTest(
	'can create, read, update and delete an entry with assignee object field',
	{tag: ['@LPD-64955', '@LPD-66725', '@LPD-66525']},
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

		await test.step('create', async () => {
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
		});

		const objectFieldLabel = objectFields[0].label['en_US'];

		const assigneeLocator = page.getByRole('combobox', {
			name: objectFieldLabel,
		});

		await test.step('read', async () => {
			await viewObjectEntriesPage.frontendDatasetItems.first().click();

			await assigneeLocator.click();

			await assigneeLocator.blur();

			expect(assigneeLocator).toHaveValue('Asset Library Owner');

			await viewObjectEntriesPage.backButton.click();
		});

		await test.step('update', async () => {
			const {objectEntry: newObjectEntryValues} =
				await generateObjectEntryValues({
					objectEntryFormat: 'UI',
					objectFields,
					role: 'Site Owner',
				});

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
		});

		await test.step('delete', async () => {
			await viewObjectEntriesPage.frontendDatasetItems.first().click();

			await assigneeLocator.fill('');

			await page.keyboard.press('Escape');

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await expect(viewObjectEntriesPage.successMessage).toBeVisible();

			await viewObjectEntriesPage.backButton.click();

			await expect(
				page.locator('td').getByText('', {exact: true}).first()
			).toBeVisible();
		});
	}
);

cmsTest.describe('Manage attachment ObjectField download permission', () => {
	cmsTest(
		'Verify file download restrictions',
		async ({apiHelpers, page, viewObjectEntriesPage}) => {
			const ATTACHMENT_FILE_NAME = 'astronaut.png';

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

			let entryUrl: string;

			await test.step('go to entry page, upload a file, save the entry and check download button is present', async () => {
				await viewObjectEntriesPage.goto(objectDefinition.className);

				await viewObjectEntriesPage.clickAddObjectEntry(
					objectDefinition.label['en_US']
				);

				await viewObjectEntriesPage.selectFileButton.click();

				await viewObjectEntriesPage.selectFileFromDocumentsAndMedia(
					ATTACHMENT_FILE_NAME
				);

				await viewObjectEntriesPage.saveObjectEntryButton.click();

				await expect(
					viewObjectEntriesPage.successMessage
				).toBeVisible();

				entryUrl = page.url();

				await expect(
					viewObjectEntriesPage.downloadFileButton
				).toBeVisible();
			});

			await test.step('login user with only view permission, then check the user is unable to perform the file download', async () => {
				await performUserSwitch(page, user.alternateName);

				await viewObjectEntriesPage.goto(objectDefinition.className);

				await page
					.getByRole('link', {name: ATTACHMENT_FILE_NAME})
					.click();

				try {
					await page.waitForEvent('download', {timeout: 1000});
				}
				catch (error) {
					expect(error.message.includes('Timeout')).toBeTruthy();
				}

				await page.goto(entryUrl);

				await expect(
					viewObjectEntriesPage.downloadFileButton
				).not.toBeVisible();
			});

			await test.step('add download permission to the user then check the user is able to perform the file download', async () => {
				await performUserSwitch(page, 'test');

				await viewObjectEntriesPage.goto(objectDefinition.className);

				await viewObjectEntriesPage.frontendDatasetActions.click();

				await viewObjectEntriesPage.frontendDatasetPermissionsAction.click();

				const iframeLocator = page.frameLocator(
					'iframe[title="Permissions"]'
				);

				const objectField = objectFields[0];

				const objectFieldActionCheckbox = iframeLocator.locator(
					'#guest_ACTION_DOWNLOAD_' + objectField.name.toUpperCase()
				);

				await page.waitForTimeout(500);

				await objectFieldActionCheckbox.check();

				await expect(objectFieldActionCheckbox).toBeChecked();

				await iframeLocator.getByRole('button', {name: 'Save'}).click();

				await expect(
					iframeLocator.getByText('Success:Your request')
				).toBeVisible();

				await performUserSwitch(page, user.alternateName);

				await viewObjectEntriesPage.goto(objectDefinition.className);

				const downloadPromise = page.waitForEvent('download');

				await page.getByRole('link', {name: 'astronaut.png'}).click();

				expect(
					(await downloadPromise).suggestedFilename()
				).toStrictEqual(`${ATTACHMENT_FILE_NAME}`);

				await page.goto(entryUrl);

				await expect(
					viewObjectEntriesPage.downloadFileButton
				).toBeVisible();
			});
		}
	);
});

cmsTest.describe('Manage attachment ObjectField storage locations', () => {
	cmsTest(
		'can submit object entry with CMS storages types',
		async ({apiHelpers, page, viewObjectEntriesPage}) => {
			const fileName = `file_${getRandomString()}.png`;
			const fileTitle = `title ${getRandomString()}`;
			const spaceName = `Space ${getRandomString()}`;
			let space;

			await test.step('Create a new Space and add a file entry to it', async () => {
				space =
					await apiHelpers.headlessAssetLibrary.createAssetLibrary({
						name: spaceName,
						settings: {},
						type: 'Space',
					});

				await apiHelpers.objectEntry.postObjectEntry(
					{
						file: {
							fileBase64: 'R0lGODlhAQABAAAAACw=',
							name: fileName,
						},
						objectEntryFolderExternalReferenceCode: 'L_FILES',
						title: fileTitle,
					},
					'cms/basic-documents',
					space.name
				);
			});

			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: [
					{
						businessType: 'Attachment',
						name: 'cmsBasicDocument',
						objectFieldSettings: [
							{
								name: 'acceptedFileExtensions',
								value: 'jpeg, jpg, pdf, png, txt',
							},
							{
								name: 'maximumFileSize',
								value: 0,
							},
							{
								name: 'fileSource',
								value: 'CMSBasicDocument',
							},
						],
					},
					{
						businessType: 'Attachment',
						name: 'userComputerToCMSBasicDocument',
						objectFieldSettings: [
							{
								name: 'acceptedFileExtensions',
								value: 'jpeg, jpg, pdf, png, txt',
							},
							{
								name: 'maximumFileSize',
								value: 0,
							},
							{
								name: 'fileSource',
								value: 'userComputerToCMSBasicDocument',
							},
							{
								name: 'showFilesInLibrary',
								value: true,
							},
							{
								name: 'storageDLFolderPath',
								value: `/myCMSFolder`,
							},
							{
								name: 'storageDepotGroup',
								value: space.externalReferenceCode,
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

			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinition.label['en_US']
			);

			await viewObjectEntriesPage.selectFileButton.first().click();

			await page.getByRole('img', {name: spaceName}).click();

			await page.getByLabel(fileTitle, {exact: true}).click();

			await page
				.getByRole('button', {exact: true, name: 'Select'})
				.click();

			await viewObjectEntriesPage.selectFileFromUserComputer(
				__dirname,
				'astronaut.png',
				1
			);

			await page
				.getByRole('button', {name: 'astronaut.png'})
				.waitFor({state: 'visible'});

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await waitForAlert(page);

			await expect(
				viewObjectEntriesPage.page.getByText(fileName)
			).toBeVisible();

			await expect(
				viewObjectEntriesPage.page.getByText('astronaut.png')
			).toBeVisible();
		}
	);

	cmsTest(
		'can upload file to CMS through CMSFilesItemSelector',
		async ({apiHelpers, page, viewObjectEntriesPage}) => {
			const spaceName = getRandomString();

			await test.step('Create a new Space', async () => {
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name: spaceName,
					settings: {},
					type: 'Space',
				});
			});

			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: [
					{
						businessType: 'Attachment',
						name: 'cmsBasicDocument',
						objectFieldSettings: [
							{
								name: 'acceptedFileExtensions',
								value: 'jpeg, jpg, pdf, png, txt',
							},
							{
								name: 'maximumFileSize',
								value: 0,
							},
							{
								name: 'fileSource',
								value: 'CMSBasicDocument',
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

			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinition.label['en_US']
			);

			await viewObjectEntriesPage.selectFileButton.first().click();

			await page.getByRole('img', {name: spaceName}).click();

			await page
				.getByTestId('managementToolbar')
				.locator('[data-testid="fdsCreationActionButton"]')
				.click();

			const fileChooserPromise = page.waitForEvent('filechooser');

			await page.getByRole('button', {name: 'Select Files'}).click();

			const fileChooser = await fileChooserPromise;

			await fileChooser.setFiles(
				path.join(__dirname, '../dependencies', 'astronaut.png')
			);

			await page.getByRole('button', {name: 'Upload (1)'}).click();

			await page.getByLabel('astronaut.png', {exact: true}).click();

			await page
				.getByRole('button', {exact: true, name: 'Select'})
				.click();

			await page
				.getByRole('button', {name: 'astronaut.png'})
				.waitFor({state: 'visible'});

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await waitForAlert(page);

			await expect(
				viewObjectEntriesPage.page.getByText('astronaut.png')
			).toBeVisible();
		}
	);

	cmsTest(
		'creates nested CMS folders when attachment storage path contains subfolders',
		{tag: '@LPD-80971'},
		async ({apiHelpers, page, spaceSummaryPage, viewObjectEntriesPage}) => {
			const childFolderName = `Child${getRandomString()}`;
			const parentFolderName = `Parent${getRandomString()}`;
			const spaceName = getRandomString();

			const space =
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name: spaceName,
					settings: {},
					type: 'Space',
				});

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields: generateObjectFields({
						objectFieldBusinessTypes: [
							{
								businessType: 'Attachment',
								name: 'userComputerToCMSBasicDocument',
								objectFieldSettings: [
									{
										name: 'acceptedFileExtensions',
										value: 'jpeg, jpg, pdf, png, txt',
									},
									{
										name: 'maximumFileSize',
										value: 0,
									},
									{
										name: 'fileSource',
										value: 'userComputerToCMSBasicDocument',
									},
									{
										name: 'showFilesInLibrary',
										value: true,
									},
									{
										name: 'storageDLFolderPath',
										value: `/${parentFolderName}/${childFolderName}`,
									},
									{
										name: 'storageDepotGroup',
										value: space.externalReferenceCode,
									},
								],
							},
						],
					}),
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			await test.step('Create object entry uploading a file from user computer', async () => {
				await viewObjectEntriesPage.goto(objectDefinition.className);

				await viewObjectEntriesPage.clickAddObjectEntry(
					objectDefinition.label['en_US']
				);

				await viewObjectEntriesPage.selectFileFromUserComputer(
					__dirname,
					'../dependencies/astronaut.png'
				);

				await page
					.getByRole('button', {name: 'astronaut.png'})
					.waitFor({state: 'visible'});

				await viewObjectEntriesPage.saveObjectEntryButton.click();

				await waitForAlert(page);
			});

			await test.step('Verify nested folders were created in CMS Files', async () => {
				await spaceSummaryPage.goto(space.name);

				const parentFolder = page.getByRole('link', {
					exact: true,
					name: parentFolderName,
				});

				await expect(parentFolder).toBeVisible();

				await expect(
					page.getByText(`${parentFolderName}/${childFolderName}`)
				).toHaveCount(0);

				await parentFolder.click();

				const childFolder = page.getByRole('link', {
					exact: true,
					name: childFolderName,
				});

				await expect(childFolder).toBeVisible();

				await childFolder.click();

				await expect(
					page.getByText('astronaut.png', {exact: false}).first()
				).toBeVisible();
			});
		}
	);

	cmsTest(
		'draft files are not shown on CMS files selector',
		async ({apiHelpers, page, viewObjectEntriesPage}) => {
			let space;

			await test.step('Create a new Space and add a draft file to it', async () => {
				space =
					await apiHelpers.headlessAssetLibrary.createAssetLibrary({
						name: `Space ${getRandomString()}`,
						settings: {},
						type: 'Space',
					});

				await apiHelpers.objectEntry.postObjectEntry(
					{
						objectEntryFolderExternalReferenceCode: 'L_FILES',
						status: {code: 2},
					},
					'cms/basic-documents',
					space.name
				);
			});

			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: [
					{
						businessType: 'Attachment',
						name: 'cmsBasicDocument',
						objectFieldSettings: [
							{
								name: 'acceptedFileExtensions',
								value: 'jpeg, jpg, pdf, png, txt',
							},
							{
								name: 'maximumFileSize',
								value: 0,
							},
							{
								name: 'fileSource',
								value: 'CMSBasicDocument',
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

			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinition.label['en_US']
			);

			await viewObjectEntriesPage.selectFileButton.click();

			await page.getByRole('img', {name: space.name}).click();

			await expect(page.getByText('No Results Found')).toBeVisible();
		}
	);
});

cmsTest.describe('Manage object entries schedule properties', () => {
	let _objectDefinition: ObjectDefinition;

	cmsTest.afterEach(async ({accountSettingsPage}) => {
		await accountSettingsPage.goToDisplaySettings();

		await accountSettingsPage.setTimeZone('UTC');
	});

	cmsTest.beforeEach(async ({accountSettingsPage, apiHelpers, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		_objectDefinition = objectDefinition;

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const shouldEnableConfiguration = !cmsTest
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

	cmsTest(
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

	cmsTest(
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

	cmsTest(
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

	cmsTest(
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

	cmsTest(
		'can submit an object entry with scheduling dates via a custom layout',
		{tag: ['@LPP-63890']},
		async ({objectLayoutsPage, page, viewObjectEntriesPage}) => {
			const objectLayoutName = getRandomString();

			await objectLayoutsPage.goto(_objectDefinition.label['en_US']);

			await objectLayoutsPage.createObjectLayout(objectLayoutName);

			await page.getByRole('link', {name: objectLayoutName}).click();

			await objectLayoutsPage.setObjectLayoutAsDefault();

			await objectLayoutsPage.createObjectLayoutContent({
				objectFieldNames: [
					'Display Date',
					'Expiration Date',
					'Review Date',
				],
				objectLayoutName,
				objectLayoutRegularBlockName: getRandomString(),
				objectLayoutTabName: getRandomString(),
			});

			await objectLayoutsPage.saveUpdateLayoutButton.click();

			await viewObjectEntriesPage.goto(_objectDefinition.className);

			await viewObjectEntriesPage.clickAddObjectEntry(
				_objectDefinition.label['en_US']
			);

			const date = new Date();

			date.setDate(date.getDate() + 1);

			await page
				.locator('[data-field-name*="displayDate"]')
				.getByLabel('Display Date', {exact: true})
				.fill(getObjectEntryUIDateTimeFormat(date));

			await page
				.locator('[data-field-name*="expirationDate"]')
				.getByLabel('Expiration Date', {exact: true})
				.fill(getObjectEntryUIDateTimeFormat(date));

			await page
				.locator('[data-field-name*="reviewDate"]')
				.getByLabel('Review Date', {exact: true})
				.fill(getObjectEntryUIDateTimeFormat(date));

			const objectEntryRequestPromise = page.waitForRequest(
				(request) =>
					request.method() === 'POST' &&
					request.url().includes('/o/c/')
			);

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			const objectEntryRequest = await objectEntryRequestPromise;

			const objectEntryRequestPayload =
				objectEntryRequest.postDataJSON() as Record<string, string>;

			for (const fieldName of [
				'displayDate',
				'expirationDate',
				'reviewDate',
			]) {
				expect(objectEntryRequestPayload[fieldName]).toMatch(
					/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}(:\d{2})?Z$/
				);
			}

			await waitForAlert(page);
		}
	);

	cmsTest(
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

	cmsTest(
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

	cmsTest(
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

	cmsTest(
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

test.describe('can use bulk on object entries', () => {
	test(
		'can bulk delete object entries',
		{tag: ['@LPD-69713']},
		async ({apiHelpers, page, viewObjectEntriesPage}) => {
			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
					titleObjectFieldName: 'textField',
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const objectEntries = [];

			for (let i = 0; i < 25; i++) {
				const {objectEntry} = await generateObjectEntryValues({
					objectEntryFormat: 'API',
					objectFields: [objectDefinition.objectFields.at(-1)],
				});
				objectEntries.push(objectEntry);
			}

			await apiHelpers.objectEntry.postObjectEntriesBatch(
				'c/' + objectDefinition.name.toLowerCase() + 's',
				objectEntries
			);

			await viewObjectEntriesPage.goto(objectDefinition.className);

			await expect(
				page.getByText('Showing 1 to 20 of 25 entries.')
			).toBeVisible();

			const rowCheckboxes = page.locator('tbody').getByRole('checkbox', {
				name: 'Select',
			});

			await rowCheckboxes.nth(0).check();

			await rowCheckboxes.nth(1).check();

			await rowCheckboxes.nth(2).check();

			await viewObjectEntriesPage.bulkActionButton.click();

			await viewObjectEntriesPage.deleteMenuItem.click();

			await expect(
				viewObjectEntriesPage.deleteConfirmationModal
			).toBeVisible();

			await viewObjectEntriesPage.deleteConfirmationModal.click();

			await waitForAlert(page, 'Deletion completed successfully.');

			await expect(
				page.getByText('Showing 1 to 20 of 22 entries.')
			).toBeVisible();

			await viewObjectEntriesPage.selectAllPage.check();

			await page.getByRole('button', {name: 'Select All'}).click();

			await viewObjectEntriesPage.bulkActionButton.click();

			await viewObjectEntriesPage.deleteMenuItem.click();

			await expect(
				viewObjectEntriesPage.deleteAllConfirmationModal
			).toBeVisible();

			await viewObjectEntriesPage.deleteAllConfirmationModal.click();

			await waitForAlert(page, 'Deletion completed successfully.');

			await expect(page.getByText('No Results Found')).toBeVisible();
		}
	);
});

test.describe('Manage object entries through Friendly URL', () => {
	let _objectDefinition: ObjectDefinition;
	let _objectEntryFriendlyURLPath: string;
	let _objectField: ObjectField;

	test.beforeEach(async ({apiHelpers, page, site, viewObjectEntriesPage}) => {
		page.setViewportSize({height: 1080, width: 1920});

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

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);
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
			await viewObjectEntriesPage.friendlyUrlInput.fill('Test URL');

			await page
				.getByLabel(_objectField.label.en_US)
				.fill(objectFieldValue);

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await expect(viewObjectEntriesPage.successMessage).toBeVisible();

			await expect(viewObjectEntriesPage.friendlyUrlInput).toHaveValue(
				'test-url'
			);
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

			await pageEditorPage.addFragment(
				'Basic Components',
				'Heading',
				page.getByText('Drag and drop fragments or widgets here')
			);

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

		await expect(viewObjectEntriesPage.friendlyUrlInput).toHaveValue(
			'second-url'
		);

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

		await expect(viewObjectEntriesPage.friendlyUrlInput).toHaveValue(
			'first-url'
		);
	});

	test('friendly URL input is disabled when viewed inside workflow task detail', async ({
		configurationTabPage,
		globalMenuPage,
		page,
		site,
		viewObjectEntriesPage,
		workflowTaskDetailsPage,
		workflowTasksPage,
	}) => {
		await test.step('Assign the single approver workflow to the object created', async () => {
			await globalMenuPage.goToApplications('Process Builder');

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

			await viewObjectEntriesPage.clickAddObjectEntry(
				_objectDefinition.label['en_US']
			);

			await expect(
				viewObjectEntriesPage.friendlyUrlInput
			).not.toBeDisabled();
		});

		await test.step('Add an object entry', async () => {
			await viewObjectEntriesPage.friendlyUrlInput.fill('test-url');

			await page.getByRole('textbox').first().fill('test entry');

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await expect(viewObjectEntriesPage.successMessage).toBeVisible();
		});

		await test.step('Go to the workflow task detail and verify that the friendly URL input is disabled', async () => {
			await workflowTasksPage.goToAssignedToMyRoles();

			await workflowTaskDetailsPage.selectAsset(
				_objectDefinition.label['en_US']
			);

			await expect(viewObjectEntriesPage.friendlyUrlInput).toBeDisabled();
		});
	});

	test('verify that friendly URL field is not visible when customization is disabled', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		await expect(viewObjectEntriesPage.friendlyUrlInput).toBeVisible();
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

		await expect(viewObjectEntriesPage.friendlyUrlInput).not.toBeVisible();
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
	test('verify that object labels are shown according to user language', async ({
		accountSettingsPage,
		apiHelpers,
		objectDetailsPage,
		page,
		viewObjectDefinitionsPage,
		viewObjectEntriesPage,
	}) => {
		siteLanguage = 'pt';

		await accountSettingsPage.selectAccountLanguage({
			languageId: 'pt_BR',
			navigate: true,
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.clickEditObjectDefinitionLink(
			objectDefinition.name,
			'Buscar'
		);

		const newLabel = objectDefinition.name + 'pt_BR';

		const newPluralLabel = objectDefinition.name + 'pt_BR plural';

		await objectDetailsPage.changeLanguageLabels(
			'pt_BR',
			newLabel,
			newPluralLabel
		);

		await page.getByRole('button', {name: 'Salvar'}).click();

		await page
			.locator('.alert-success')
			.filter({hasText: 'O objeto foi salvo com sucesso.'})
			.waitFor();

		await viewObjectEntriesPage.goto(
			objectDefinition.className,
			siteLanguage
		);

		await expect(
			page.getByRole('heading', {name: newPluralLabel})
		).toBeVisible();
	});

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

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);
		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(page.getByText('The field is empty')).toBeVisible();

		const objectFieldValue = getRandomString();

		await page.getByLabel('textField').fill(objectFieldValue);

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(page.getByText('The field is empty')).not.toBeVisible();
		await expect(page.getByText('The URL is invalid')).toBeVisible();
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
				'DateTime',
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

		const dataRow = page.getByRole('row').nth(1);

		const columnHeaderLocator = page.getByRole('columnheader');

		await columnHeaderLocator.first().waitFor();

		const columnHeaders = await columnHeaderLocator.allInnerTexts();

		const columnMap = new Map(
			columnHeaders.map((text, index) => [
				text.trim().toLowerCase(),
				index,
			])
		);

		for (const {entry, name} of objectFieldObjectEntryValues) {
			const columnIndex = columnMap.get(name.toLowerCase());

			await expect(
				dataRow.getByRole('cell').nth(columnIndex!)
			).toHaveText(entry);
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

		await viewObjectEntriesPage.clickAddObjectEntry(objectDefinitionLabel);

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

	test('can add entry for site scoped definition in a site template', async ({
		apiHelpers,
		page,
		productMenuPage,
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

		const siteTemplateName: string = 'Template-' + getRandomString();

		const layoutSetPrototype = await createSiteTemplate({
			apiHelpers,
			page,
			productMenuPage,
			templateName: siteTemplateName,
		});

		apiHelpers.data.push({
			id: layoutSetPrototype.layoutSetPrototypeId,
			type: 'layoutSetPrototype',
		});

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
		'can add entry with empty value for date field',
		{tag: '@LPS-147658'},
		async ({apiHelpers, page, viewObjectEntriesPage}) => {
			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields: generateObjectFields({
						objectFieldBusinessTypes: ['Date'],
					}),
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

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await waitForAlert(page);

			await viewObjectEntriesPage.goto(objectDefinition.className);

			await expect(
				page.getByText('Showing 1 to 1 of 1 entries.')
			).toBeVisible();
		}
	);

	test('can add entry with empty value for picklist field', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const {listTypeDefinition} =
			await postListTypeDefinitionListTypeEntries({
				apiHelpers,
				listTypeEntriesLength: 2,
			});

		const objectFields = generateObjectFields({
			listTypeDefinitionExternalReferenceCode:
				listTypeDefinition.externalReferenceCode,
			objectFieldBusinessTypes: [
				{
					businessType: 'Picklist',
				},
				{
					businessType: 'Text',
					label: {en_US: 'Text Field'},
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

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);

		await viewObjectEntriesPage.fillObjectEntry({
			objectFieldBusinessType: 'Text',
			objectFieldLabel: 'Text Field',
			objectFieldValue: 'test',
		});

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(viewObjectEntriesPage.successMessage).toBeVisible();

		await viewObjectEntriesPage.backButton.click();

		await expect(
			page.locator('td').getByText('test', {exact: true})
		).toBeVisible();
	});

	test('can add object entry with add permission', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['Text'],
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

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const user = await createUserWithPermissions({
			apiHelpers,
			rolePermissions: [
				{
					actionIds: ['VIEW_CONTROL_PANEL'],
					primaryKey: companyId,
					resourceName: '90',
					scope: 1,
				},
				{
					actionIds: ['ACCESS_IN_CONTROL_PANEL'],
					primaryKey: companyId,
					resourceName: `com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet_${objectDefinition.className.split('#')[1]}`,
					scope: 1,
				},
				{
					actionIds: ['ADD_OBJECT_ENTRY'],
					primaryKey: companyId,
					resourceName: `com.liferay.object#${objectDefinition.id}`,
					scope: 1,
				},
			],
		});

		await performUserSwitch(page, user.alternateName);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);

		await viewObjectEntriesPage.fillObjectEntry({
			objectFieldBusinessType: 'Text',
			objectFieldLabel: objectFields[0].label['en_US'],
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

	test('can cancel entry update', async ({
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
						label: {en_US: 'Field'},
						name: 'textField',
						required: false,
					},
				],
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{textField: 'Test Entry'},
			'c/' + objectDefinition.name.toLowerCase() + 's'
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.frontendDatasetItems.first().click();

		await page.getByLabel('Field', {exact: true}).clear();

		await viewObjectEntriesPage.fillObjectEntry({
			objectFieldLabel: 'Field',
			objectFieldValue: 'Test Entry 2',
		});

		await viewObjectEntriesPage.cancelObjectEntryButton.click();

		await expect(page.getByText('Test Entry 2')).not.toBeVisible();

		await expect(page.getByText('Test Entry')).toBeVisible();
	});

	test('can change columns to be displayed on auto-generated table', async ({
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

		await apiHelpers.objectEntry.postObjectEntry(
			{textField: 'Test text'},
			'c/' + objectDefinition.name.toLowerCase() + 's'
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(
			page.getByRole('columnheader').getByText('ID')
		).toBeVisible();

		await page.getByLabel('Manage Columns Visibility').click();

		await page.getByRole('menuitem', {name: 'ID'}).click();

		await page.keyboard.press('Escape');

		await expect(
			page.getByRole('columnheader').getByText('ID')
		).not.toBeVisible();
	});

	test('can create an object entry with aggregation field', async ({
		apiHelpers,
		objectFieldsPage,
		page,
	}) => {
		const objectFieldsA = generateObjectFields({
			objectFieldBusinessTypes: ['Integer'],
		});

		const objectDefinition1 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields: objectFieldsA,
				status: {code: 2},
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

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
			objectDefinition2.externalReferenceCode!,
			{
				label: {en_US: 'Relationship'},
				name: 'relationship' + Math.floor(Math.random() * 99),
				objectDefinitionExternalReferenceCode2:
					objectDefinition1.externalReferenceCode,
				objectDefinitionId2: objectDefinition1.id,
				objectDefinitionName2: objectDefinition1.name,
				type: 'oneToMany',
			}
		);

		await objectFieldsPage.goto(objectDefinition2.label['en_US']);

		await objectFieldsPage.addObjectField({
			aggregationField: objectFieldsA[0].name,
			aggregationFieldFunction: 'Sum',
			aggregationFieldRelationship: 'Relationship',
			objectFieldBusinessType: 'Aggregation',
			objectFieldLabel: 'Custom Aggregation',
		});

		await expect(
			page.getByRole('link', {name: 'Custom Aggregation'})
		).toBeVisible();
	});

	test('can create an object entry with date and time field and the time storage set to user input', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: [
				{
					businessType: 'DateTime',
					objectFieldSettings: [
						{
							name: 'timeStorage',
							value: 'useInputAsEntered',
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

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);

		const date = new Date(2023, 5, 1, 12, 0, 0);

		await viewObjectEntriesPage.fillObjectEntry({
			objectFieldLabel: objectFields[0].label['en_US'],
			objectFieldValue: getObjectEntryUIDateTimeFormat(date),
		});

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await waitForAlert(page);

		await viewObjectEntriesPage.backButton.click();

		await expect(
			page.getByRole('cell', {name: getFDSDateTimeFormat(date)})
		).toHaveText('Jun 1, 2023, 12:00:00 PM');
	});

	test('can create an object entry with special characters on a text field named Name', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: [
				{
					businessType: 'Text',
					label: {en_US: 'Name'},
					name: 'name',
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

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);

		await viewObjectEntriesPage.fillObjectEntry({
			objectFieldBusinessType: 'Text',
			objectFieldLabel: 'Name',
			objectFieldValue: '@~!& ^%$&_-',
		});

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(viewObjectEntriesPage.successMessage).toBeVisible();

		await viewObjectEntriesPage.backButton.click();

		await expect(
			page.locator('td').getByText('@~!& ^%$&_-', {exact: true})
		).toBeVisible();
	});

	test('can delete a custom field when existing entries', async ({
		apiHelpers,
		objectFieldsPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['Integer', 'LongInteger'],
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

		const integerFieldName = objectFields[0].name;
		const longIntegerFieldName = objectFields[1].name;

		const applicationName =
			'c/' + objectDefinition.name!.toLowerCase() + 's';

		await apiHelpers.objectEntry.postObjectEntry(
			{[integerFieldName]: 18, [longIntegerFieldName]: 187082187082},
			applicationName
		);

		await objectFieldsPage.goto(objectDefinition.label['en_US']);

		const longIntegerLabel = objectFields[1].label.en_US;

		await objectFieldsPage.deleteObjectFieldByLabel(longIntegerLabel);

		await waitForAlert(
			page,
			`Success:${longIntegerLabel} was deleted successfully.`
		);

		await viewObjectEntriesPage.goto(objectDefinition.className!);

		await expect(page.getByText(longIntegerLabel)).toBeHidden();
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
			objectFieldNames: ['Custom Field', 'Relationship'],
			objectLayoutName,
			objectLayoutRegularBlockName: 'Block 1',
			objectLayoutTabName: 'Field Tab',
		});

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

		await page.getByRole('textbox', {name: 'Search'}).click();

		await page.getByRole('menuitem', {name: 'Entry A'}).click();

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await waitForAlert(page);

		await page.getByRole('link', {name: 'Relationship Tab'}).click();

		await page.getByRole('button', {name: 'New'}).first().click();

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

		await expect(
			page.getByRole('textbox', {name: 'Search'})
		).not.toContainText('Entry A');
	});

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

		await viewObjectEntriesPage.downloadFileButton.click();

		expect((await downloadPromise).suggestedFilename()).toStrictEqual(
			`${ATTACHMENT_FILE_NAME}`
		);

		await viewObjectEntriesPage.deleteFileButton.click();

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(
			viewObjectEntriesPage.successMessage.first()
		).toBeVisible();
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

			await expect(
				page.locator('#editObjectEntry').getByPlaceholder('Search')
			).toHaveValue('Entry A');

			await page
				.locator('#editObjectEntry')
				.getByPlaceholder('Search')
				.click();

			await page.getByRole('menuitem', {name: 'Entry C'}).click();

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await waitForAlert(page);

			await viewObjectEntriesPage.goto(objectDefinition.className);

			await page
				.getByRole('link', {name: objectEntryB.id.toString()})
				.click();

			await expect(
				page.locator('#editObjectEntry').getByPlaceholder('Search')
			).toHaveValue('Entry C');
		});
	});

	test('can edit object entry with object field picklist mark as state', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const {listTypeDefinition, listTypeEntries} =
			await postListTypeDefinitionListTypeEntries({
				apiHelpers,
				listTypeEntriesLength: 3,
			});

		const objectFields = generateObjectFields({
			listTypeDefinitionExternalReferenceCode:
				listTypeDefinition.externalReferenceCode,
			objectFieldBusinessTypes: [
				{
					businessType: 'Picklist',
					objectFieldSettings: [
						{
							name: 'defaultValueType',
							value: 'inputAsValue',
						},
						{
							name: 'defaultValue',
							value: listTypeEntries[0].name,
						},
					],
					required: true,
					state: true,
				},
			],
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				label: {
					en_US: 'ObjectDefinitionLabel' + getRandomInt(),
				},
				name: 'ObjectDefinitionName' + getRandomInt(),
				objectFields,
				pluralLabel: {
					en_US: 'ObjectDefinitionLabel' + getRandomInt(),
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

		const picklistFieldLabel = objectFields[0].label['en_US'];
		const firstEntryName = listTypeEntries[0].name_i18n['en-US'];
		const secondEntryName = listTypeEntries[1].name_i18n['en-US'];

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);

		await viewObjectEntriesPage.selectDropdownItem(
			picklistFieldLabel,
			firstEntryName
		);

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await waitForAlert(page);

		await viewObjectEntriesPage.backButton.click();

		await viewObjectEntriesPage.frontendDatasetItems.first().click();

		await viewObjectEntriesPage.selectDropdownItem(
			picklistFieldLabel,
			secondEntryName
		);

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await waitForAlert(page);
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

		await objectLayoutsPage.createObjectLayoutBlock({
			objectLayoutRegularBlockName: getRandomString(),
		});

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

		await page
			.getByRole('button', {name: 'Select Existing One'})
			.first()
			.click();

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

	test(
		'can link an object entry to a system object via relationship picker',
		{tag: '@LPS-145393'},
		async ({apiHelpers, page, viewObjectEntriesPage}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['Text'],
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

			const userAccount =
				await apiHelpers.headlessAdminUser.postUserAccount();

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			const objectRelationshipName =
				'objectRelationshipName' + Math.floor(Math.random() * 99);

			const {body: objectRelationship} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					'L_USER',
					{
						label: {en_US: 'Relationship'},
						name: objectRelationshipName,
						objectDefinitionExternalReferenceCode1: 'L_USER',
						objectDefinitionExternalReferenceCode2:
							objectDefinition.externalReferenceCode,
						objectDefinitionId2: objectDefinition.id,
						objectDefinitionName2: objectDefinition.name,
						type: 'oneToMany',
					}
				);

			apiHelpers.data.push({
				id: objectRelationship.id,
				type: 'objectRelationship',
			});

			await viewObjectEntriesPage.goto(objectDefinition.className);

			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinition.label['en_US']
			);

			await page.getByPlaceholder('Search').click();

			await page
				.getByRole('menuitem', {name: userAccount.givenName})
				.click();

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await expect(viewObjectEntriesPage.successMessage).toBeVisible();

			expect(
				(
					await page.getByPlaceholder('Search').inputValue()
				).toLowerCase()
			).toBe(userAccount.givenName.toLowerCase());
		}
	);

	test(
		'can link multiple object entries to a system object via relationship picker',
		{tag: '@LPS-145393'},
		async ({apiHelpers, page, viewObjectEntriesPage}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['Text'],
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

			const userAccount =
				await apiHelpers.headlessAdminUser.postUserAccount();

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			const objectRelationshipName =
				'objectRelationshipName' + Math.floor(Math.random() * 99);

			const {body: objectRelationship} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					'L_USER',
					{
						label: {en_US: 'Relationship'},
						name: objectRelationshipName,
						objectDefinitionExternalReferenceCode1: 'L_USER',
						objectDefinitionExternalReferenceCode2:
							objectDefinition.externalReferenceCode,
						objectDefinitionId2: objectDefinition.id,
						objectDefinitionName2: objectDefinition.name,
						type: 'oneToMany',
					}
				);

			apiHelpers.data.push({
				id: objectRelationship.id,
				type: 'objectRelationship',
			});

			const textFieldName = objectFields[0].name;

			const entryA = await apiHelpers.objectEntry.postObjectEntry(
				{[textFieldName]: 'Entry A'},
				`c/${objectDefinition.name.toLowerCase()}s`
			);

			const entryB = await apiHelpers.objectEntry.postObjectEntry(
				{[textFieldName]: 'Entry B'},
				`c/${objectDefinition.name.toLowerCase()}s`
			);

			expect(entryA.id).toBeTruthy();
			expect(entryB.id).toBeTruthy();

			for (const entryLabel of ['Entry A', 'Entry B']) {
				await viewObjectEntriesPage.goto(objectDefinition.className);

				await page
					.getByRole('row', {name: new RegExp(entryLabel)})
					.getByRole('button', {name: 'Actions'})
					.first()
					.click();

				await viewObjectEntriesPage.frontendDatasetViewAction.click();

				await viewObjectEntriesPage.editObjectEntryForm.waitFor({
					state: 'visible',
				});

				await page.getByPlaceholder('Search').click();

				await page
					.getByRole('menuitem', {name: userAccount.givenName})
					.click();

				await viewObjectEntriesPage.saveObjectEntryButton.click();

				await expect(
					viewObjectEntriesPage.successMessage
				).toBeVisible();

				expect(
					(
						await page.getByPlaceholder('Search').inputValue()
					).toLowerCase()
				).toBe(userAccount.givenName.toLowerCase());
			}
		}
	);

	test('can only see entries from their own account', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const account1 = await apiHelpers.headlessAdminUser.postAccount();
		const account2 = await apiHelpers.headlessAdminUser.postAccount();

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

		apiHelpers.data.push({id: user1.id, type: 'userAccount'});
		apiHelpers.data.push({id: user2.id, type: 'userAccount'});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account1.id,
			[user1.emailAddress]
		);
		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account2.id,
			[user2.emailAddress]
		);

		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['Text'],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				panelCategoryKey: 'control_panel.object',
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: accountObjectDefinition} =
			await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
				'L_ACCOUNT'
			);

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		const {body: objectRelationship} =
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
					objectDefinitionId1: accountObjectDefinition.id,
					objectDefinitionId2: objectDefinition.id,
					objectDefinitionName2: objectDefinition.name,
					type: 'oneToMany',
				}
			);

		apiHelpers.data.push({
			id: objectRelationship.id,
			type: 'objectRelationship',
		});

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: 'ObjRole' + getRandomInt(),
			rolePermissions: [
				{
					actionIds: ['VIEW_CONTROL_PANEL'],
					primaryKey: companyId,
					resourceName: '90',
					scope: 1,
				},
				{
					actionIds: ['ACCESS_IN_CONTROL_PANEL'],
					primaryKey: companyId,
					resourceName: `com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet_${objectDefinition.className.split('#')[1]}`,
					scope: 1,
				},
				{
					actionIds: ['ADD_OBJECT_ENTRY'],
					primaryKey: companyId,
					resourceName: `com.liferay.object#${objectDefinition.id}`,
					scope: 1,
				},
			],
		});

		apiHelpers.data.push({id: role.id, type: 'role'});

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role.externalReferenceCode,
			user1.id
		);
		await apiHelpers.headlessAdminUser.assignUserToRole(
			role.externalReferenceCode,
			user2.id
		);

		userData[user1.alternateName] = {
			name: user1.givenName,
			password: 'test',
			surname: user1.familyName,
		};
		userData[user2.alternateName] = {
			name: user2.givenName,
			password: 'test',
			surname: user2.familyName,
		};

		await performUserSwitch(page, user1.alternateName);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);

		await page.getByRole('textbox', {name: 'Search'}).click();

		await expect(
			page.getByRole('menuitem', {name: account1.name})
		).toBeVisible();
		await expect(
			page.getByRole('menuitem', {name: account2.name})
		).toBeHidden();

		await page.getByRole('menuitem', {name: account1.name}).click();

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await waitForAlert(page);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(page.getByText(account1.name)).toBeVisible();

		await performUserSwitch(page, user2.alternateName);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);

		await page.getByRole('textbox', {name: 'Search'}).click();

		await expect(
			page.getByRole('menuitem', {name: account1.name})
		).toBeHidden();
		await expect(
			page.getByRole('menuitem', {name: account2.name})
		).toBeVisible();

		await page.getByRole('menuitem', {name: account2.name}).click();

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await waitForAlert(page);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(page.getByText(account2.name)).toBeVisible();
	});

	test('can order auto-generated table by entry', async ({
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

		for (const number of ['1', '2']) {
			await apiHelpers.objectEntry.postObjectEntry(
				{textField: `Test text ${number}`},
				'c/' + objectDefinition.name.toLowerCase() + 's'
			);
		}

		await viewObjectEntriesPage.goto(objectDefinition.className);

		const textHeader = page.getByRole('columnheader', {name: 'textField'});

		await textHeader.click();

		const rows = page
			.getByRole('row')
			.filter({hasNot: page.getByRole('columnheader')});

		await expect(rows.first()).toContainText('Test text 1');
		await expect(rows.last()).toContainText('Test text 2');

		await textHeader.click();

		await expect(rows.first()).toContainText('Test text 2');
		await expect(rows.last()).toContainText('Test text 1');
	});

	test('can prevent duplicate value when creating an entry with unique values', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectFieldLabel = 'textField';

		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: [
				{
					businessType: 'Text',
					label: {en_US: objectFieldLabel},
					name: objectFieldLabel,
					objectFieldSettings: [
						{
							name: 'uniqueValues',
							value: true,
						},
					],
					unique: true,
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

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		await apiHelpers.objectEntry.postObjectEntry(
			{[objectFieldLabel]: 'UniqueTestValue'},
			applicationName
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);

		await viewObjectEntriesPage.fillObjectEntry({
			objectFieldLabel,
			objectFieldValue: 'UniqueTestValue',
		});

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await waitForAlert(
			page,
			`Error:The ${objectFieldLabel} is already in use. Please enter a unique ${objectFieldLabel}.`,
			{type: 'danger'}
		);
	});

	test('can prevent duplicate value when editing an existing entry with unique values', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectFieldLabel = 'integerField';

		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: [
				{
					businessType: 'Integer',
					label: {en_US: objectFieldLabel},
					name: objectFieldLabel,
					objectFieldSettings: [
						{
							name: 'uniqueValues',
							value: true,
						},
					],
					unique: true,
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

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		await apiHelpers.objectEntry.postObjectEntry(
			{[objectFieldLabel]: 100},
			applicationName
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{[objectFieldLabel]: 200},
			applicationName
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.frontendDatasetItems.first().click();

		await page.getByLabel(objectFieldLabel).clear();

		await viewObjectEntriesPage.fillObjectEntry({
			objectFieldLabel,
			objectFieldValue: '200',
		});

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await waitForAlert(
			page,
			`Error:The ${objectFieldLabel} is already in use. Please enter a unique ${objectFieldLabel}.`,
			{type: 'danger'}
		);
	});

	test('can search for an entry on auto-generated table', async ({
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

		await apiHelpers.objectEntry.postObjectEntry(
			{textField: 'Test 1'},
			'c/' + objectDefinition.name.toLowerCase() + 's'
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{textField: 'Entry 2'},
			'c/' + objectDefinition.name.toLowerCase() + 's'
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await page.getByPlaceholder('Search').fill('Entry 2');

		await page.getByPlaceholder('Search').press('Enter');

		await expect(
			page.getByRole('table').getByText('Entry 2')
		).toBeVisible();

		await expect(
			page.getByRole('table').getByText('Test 1')
		).not.toBeVisible();
	});

	test('can set picklist default value via expression builder', async ({
		apiHelpers,
		objectFieldsPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const {listTypeDefinition, listTypeEntries} =
			await postListTypeDefinitionListTypeEntries({
				apiHelpers,
				listTypeEntriesLength: 5,
			});

		const objectFields = generateObjectFields({
			listTypeDefinitionExternalReferenceCode:
				listTypeDefinition.externalReferenceCode,
			objectFieldBusinessTypes: ['Picklist', 'Text'],
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

		await objectFieldsPage.goto(objectDefinition.label['en_US']);

		const picklistField = objectFields[0];

		await objectFieldsPage.openObjectField(picklistField.label['en_US']);

		await objectFieldsPage.advancedTab.click();

		await objectFieldsPage.useDefaultValueToggle.check();

		await objectFieldsPage.iframeLocator
			.getByRole('button', {name: 'Expression Builder'})
			.click();

		const textFieldName = objectFields[1].name;

		await objectFieldsPage.iframeLocator
			.getByPlaceholder('Create an expression.')
			.fill(textFieldName);

		await objectFieldsPage.editFieldSaveButton.click();

		await waitForAlert(
			page,
			'Success:The object field was updated successfully'
		);

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		const firstItemName = listTypeEntries[0].name;

		await apiHelpers.objectEntry.postObjectEntry(
			{[textFieldName]: firstItemName},
			applicationName
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(
			page.locator(`[data-id$=":${picklistField.name},name"]`)
		).toHaveText(firstItemName);
	});

	test('can verify auto increment field is read only in object entries', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: [
				'Text',
				{
					businessType: 'AutoIncrement',
					objectFieldSettings: [
						{name: 'prefix', value: 'HAT-'},
						{name: 'initialValue', value: '1'},
						{name: 'suffix', value: ''},
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

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);

		const autoIncrementFieldLabel = objectFields[1].label['en_US'];

		const autoIncrementInput = page.getByLabel(autoIncrementFieldLabel);

		await expect(autoIncrementInput).toBeHidden();

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await waitForAlert(page);

		await viewObjectEntriesPage.backButton.click();

		await viewObjectEntriesPage.frontendDatasetItems.first().click();

		await expect(autoIncrementInput).toBeVisible();

		await expect(autoIncrementInput).toHaveAttribute('readonly', '');

		await expect(autoIncrementInput).toHaveValue('HAT-1');
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

	test('can view other users entry with view permission', async ({
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

		const objectEntry = getRandomString();

		await apiHelpers.objectEntry.postObjectEntry(
			{textField: objectEntry},
			'c/' + objectDefinition.name.toLowerCase() + 's'
		);

		const company =
			await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
				'liferay.com'
			);

		const user = await createUserWithPermissions({
			apiHelpers,
			rolePermissions: [
				{
					actionIds: ['ACCESS_IN_CONTROL_PANEL'] as any[],
					primaryKey: company.companyId,
					resourceName: `com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet_${objectDefinition.className.split('#')[1]}`,
					scope: 1,
				},
				{
					actionIds: ['VIEW'] as any[],
					primaryKey: company.companyId,
					resourceName: objectDefinition.className,
					scope: 1,
				},
			],
		});

		await performUserSwitch(page, user.alternateName);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(
			page.getByRole('cell', {exact: true, name: objectEntry})
		).toBeVisible();
	});

	test('can view success message entirely in arabic', async ({
		apiHelpers,
		page,
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

		await page
			.getByTitle(`إضافة ${objectDefinition.label['en_US']}`)
			.click();

		await viewObjectEntriesPage.selectFileFromDocumentsAndMediaArabic();

		await viewObjectEntriesPage.saveObjectEntryButtonArabic.click();

		await expect(viewObjectEntriesPage.successMessageArabic).toBeVisible();
	});

	test('can view user name on author column', async ({
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

		await apiHelpers.objectEntry.postObjectEntry(
			{textField: getRandomString()},
			'c/' + objectDefinition.name.toLowerCase() + 's'
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(page.getByRole('cell', {name: 'Test Test'})).toBeVisible();
	});

	test('cannot add translation to a non-translatable field', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinitionName = 'ObjectDefinitionName' + getRandomInt();

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: [
				{
					businessType: 'Text',
					localized: false,
				},
			],
		});

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				enableLocalization: true,
				label: {en_US: objectDefinitionName},
				name: objectDefinitionName,
				objectFields,
				pluralLabel: {en_US: objectDefinitionName},
				portlet: true,
				scope: 'company',
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

		const translationButton = page.getByRole('button', {name: 'en-us'});

		await expect(translationButton).toHaveCount(0);
	});

	test('cannot view other users entry without view permission', async ({
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

		const objectEntry = getRandomString();

		await apiHelpers.objectEntry.postObjectEntry(
			{textField: objectEntry},
			'c/' + objectDefinition.name.toLowerCase() + 's'
		);

		const company =
			await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
				'liferay.com'
			);

		const user = await createUserWithPermissions({
			apiHelpers,
			rolePermissions: [
				{
					actionIds: ['ACCESS_IN_CONTROL_PANEL'] as any[],
					primaryKey: company.companyId,
					resourceName: `com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet_${objectDefinition.className.split('#')[1]}`,
					scope: 1,
				},
				{
					actionIds: ['ADD_OBJECT_ENTRY'] as any[],
					primaryKey: company.companyId,
					resourceName: `com.liferay.object#${objectDefinition.id}`,
					scope: 1,
				},
			],
		});

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(
			page.getByRole('cell', {exact: true, name: objectEntry})
		).toBeVisible();

		await performUserSwitch(page, user.alternateName);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(
			page
				.getByTestId('managementToolbar')
				.locator('[data-testid="fdsCreationActionButton"]')
		).toBeVisible();

		await expect(
			page.getByRole('cell', {exact: true, name: objectEntry})
		).not.toBeVisible();
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
			objectFieldNames: [objectField.label['en_US']],
			objectLayoutName,
			objectLayoutRegularBlockName: getRandomString(),
			objectLayoutTabName: getRandomString(),
		});

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
		'character count is updated dynamically when typing in text field',
		{tag: '@LPS-146889'},
		async ({apiHelpers, page, viewObjectEntriesPage}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: [
					{
						businessType: 'Text',
						name: 'customText',
						objectFieldSettings: [
							{
								name: 'showCounter',
								value: true,
							},
							{
								name: 'maxLength',
								value: 10,
							},
						],
						required: false,
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

			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinition.label['en_US']
			);

			await expect(page.getByText('0/10 Characters')).toBeVisible();

			const fieldInput = page.getByLabel(objectFields[0].label.en_US);

			await fieldInput.click();

			await page.keyboard.type('entry');

			await expect(page.getByText('5/10 Characters')).toBeVisible();
		}
	);

	test('columns ID, Fields and Status are displayed', async ({
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
						label: {en_US: 'Field'},
						name: 'textField',
						required: false,
					},
				],
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{textField: 'String'},
			'c/' + objectDefinition.name.toLowerCase() + 's'
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(
			page.getByRole('columnheader').getByText('ID')
		).toBeVisible();

		await expect(
			page.getByRole('columnheader').getByText('Field')
		).toBeVisible();

		await expect(
			page.getByRole('columnheader').getByText('Status')
		).toBeVisible();
	});

	test(
		'different versions of Commerce Products have same input values when used as relationship of an object entry',
		{tag: '@LPD-65249'},
		async ({
			apiHelpers,
			commerceInstanceSettingsPage,
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

			await commerceInstanceSettingsPage.toggleProductVersioning();

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

			await commerceInstanceSettingsPage.toggleProductVersioning();
		}
	);

	test('duplicated entry is not submitted when refreshing page', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['Text'],
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

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);

		await viewObjectEntriesPage.fillObjectEntry({
			objectFieldBusinessType: 'Text',
			objectFieldLabel: objectFields[0].label['en_US'],
			objectFieldValue: 'test',
		});

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(viewObjectEntriesPage.successMessage).toBeVisible();

		await page.reload();

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(
			page.locator('td').getByText('test', {exact: true})
		).toHaveCount(1);
	});

	test('empty state is displayed when no entry exists', async ({
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

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(page.getByText('No Results Found')).toBeVisible();
	});

	test('empty state is displayed when searching for nonexistent value', async ({
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

		await apiHelpers.objectEntry.postObjectEntry(
			{textField: 'Test text'},
			'c/' + objectDefinition.name.toLowerCase() + 's'
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await page.getByPlaceholder('Search').fill('Lorem ipsum');

		await page.getByPlaceholder('Search').press('Enter');

		await expect(page.getByText('No Results Found')).toBeVisible();
	});

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

				await viewObjectEntriesPage.clickAddObjectEntry(objectName);

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

	test('FDS table respects useInputAsEntered configuration not mutating value to UTC', async ({
		accountSettingsPage,
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		try {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: [
					{
						businessType: 'DateTime',
						objectFieldSettings: [
							{
								name: 'timeStorage',
								value: 'useInputAsEntered',
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

			await accountSettingsPage.goToDisplaySettings();

			await accountSettingsPage.setTimeZone('America/Sao_Paulo');

			await viewObjectEntriesPage.goto(objectDefinition.className);

			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinition.label['en_US']
			);

			const date = new Date();

			date.setHours(date.getHours() + 3);

			const objectFieldLabel = page.getByLabel(
				objectFields[0].label['en_US']
			);

			await objectFieldLabel.fill(getObjectEntryUIDateTimeFormat(date));

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await waitForAlert(page);

			await viewObjectEntriesPage.backButton.click();

			let formattedDate = date.toLocaleString('en-US', {
				day: 'numeric',
				hour: 'numeric',
				hour12: true,
				minute: '2-digit',
				month: 'short',
				year: 'numeric',
			});

			// inserts ":00" before the last space and "PM/AM"

			formattedDate = formattedDate.replace(/(\s[AP]M)$/, ':00$1');

			await expect(page.getByText(formattedDate)).toBeVisible();
		}
		finally {
			await accountSettingsPage.goToDisplaySettings();

			await accountSettingsPage.setTimeZone('UTC');
		}
	});

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

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);

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

				await page
					.getByRole('checkbox', {name: firstOptionName})
					.click();

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

	test('verify that relationship API is called only once and uses pagination when adding object entry', async ({
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
		let apiURL = '';

		page.on('request', (request) => {
			if (
				request
					.url()
					.includes('/o/headless-admin-user/v1.0/accounts') &&
				request.method() === 'GET'
			) {
				apiCalls++;
				apiURL = request.url();
			}
		});

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);

		await page.waitForResponse(
			(response) =>
				response
					.url()
					.includes('/o/headless-admin-user/v1.0/accounts') &&
				response.request().method() === 'GET'
		);

		expect(apiCalls).toBe(1);
		expect(apiURL).not.toContain('pageSize=-1');
	});

	test('verify that temporary files are deleted from the database if the object creation is not completed', async ({
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
							value: 'userComputerToDocumentsAndMedia',
						},
						{
							name: 'showFilesInLibrary',
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

	test('verify that updating the default value of an object field picklist type only affects new entries', async ({
		apiHelpers,
		objectFieldsPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const {listTypeDefinition, listTypeEntries} =
			await postListTypeDefinitionListTypeEntries({
				apiHelpers,
				listTypeEntriesLength: 2,
			});

		const objectFields = generateObjectFields({
			listTypeDefinitionExternalReferenceCode:
				listTypeDefinition.externalReferenceCode,
			objectFieldBusinessTypes: ['Picklist'],
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

		const firstItemName = listTypeEntries[0].name;
		const secondItemName = listTypeEntries[1].name;

		await objectFieldsPage.goto(objectDefinition.label['en_US']);

		await objectFieldsPage.setDefaultValue({
			defaultValue: firstItemName,
			objectFieldBusinessType: 'Picklist',
			objectFieldName: objectFields[0].label['en_US'],
		});

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await waitForAlert(page);

		await viewObjectEntriesPage.backButton.click();

		await expect(
			page.getByRole('cell', {name: firstItemName})
		).toBeVisible();

		await objectFieldsPage.goto(objectDefinition.label['en_US']);

		await objectFieldsPage.setDefaultValue({
			defaultValue: secondItemName,
			objectFieldBusinessType: 'Picklist',
			objectFieldName: objectFields[0].label['en_US'],
		});

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await waitForAlert(page);

		await viewObjectEntriesPage.backButton.click();

		await expect(
			page.getByRole('cell', {name: firstItemName})
		).toBeVisible();

		await expect(
			page.getByRole('cell', {name: secondItemName})
		).toBeVisible();
	});

	test(
		'can add an entry with phone number object field where prefix type is defined by user',
		{tag: ['@LPD-83570']},
		async ({apiHelpers, page, viewObjectEntriesPage}) => {
			const localNumber = '8775433729';
			const prefix = '+1';

			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['PhoneNumber'],
			});

			const objectFieldLabel = objectFields[0].label!['en_US'];

			const fieldContainer = page.getByRole('group', {
				name: objectFieldLabel,
			});

			let objectDefinition: ObjectDefinition;

			await test.step('Create an object definition', async () => {
				objectDefinition =
					await apiHelpers.objectAdmin.postRandomObjectDefinition({
						objectFields,
						status: {code: 0},
					});

				apiHelpers.data.push({
					id: objectDefinition.id,
					type: 'objectDefinition',
				});
			});

			await test.step('Navigate to the object definition and add an entry', async () => {
				await viewObjectEntriesPage.goto(objectDefinition.className);

				await viewObjectEntriesPage.clickAddObjectEntry(
					objectDefinition.label['en_US']
				);
			});

			await test.step('Select the "United States" prefix, fill the phone number field, and save the entry', async () => {
				await fieldContainer.getByLabel('Country Code').click();

				await page.getByRole('option', {name: /United States/}).click();

				await expect(fieldContainer.getByText(prefix)).toBeVisible();

				await fieldContainer
					.getByLabel('Phone Number')
					.fill(localNumber);

				await viewObjectEntriesPage.saveObjectEntryButton.click();

				await expect(
					viewObjectEntriesPage.successMessage
				).toBeVisible();
			});

			await test.step('Verify the phone number field values are saved', async () => {
				await viewObjectEntriesPage.backButton.click();

				await viewObjectEntriesPage.frontendDatasetItems
					.first()
					.click();

				await expect(
					fieldContainer.getByLabel('Country Code')
				).toHaveText(prefix);

				await expect(
					fieldContainer.getByLabel('Phone Number')
				).toHaveValue(localNumber);
			});

			await test.step('Verify that the country code icon is correct for the local number entered', async () => {
				await expect(
					fieldContainer
						.getByLabel('Country Code')
						.locator('.lexicon-icon-en-us')
				).toBeVisible();
			});
		}
	);

	test(
		'can add an entry with phone number object field where prefix type is fixed',
		{tag: ['@LPD-83570']},
		async ({apiHelpers, page, viewObjectEntriesPage}) => {
			const localNumber = '11987654321';
			const prefix = '+1';

			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: [
					{
						businessType: 'PhoneNumber',
						objectFieldSettings: [
							{
								name: 'prefixType',
								value: 'fixed',
							},
							{
								name: 'prefix',
								value: prefix,
							},
						],
					},
				],
			});

			const objectFieldLabel = objectFields[0].label!['en_US'];

			const fieldContainer = page.getByRole('group', {
				name: objectFieldLabel,
			});

			let objectDefinition: ObjectDefinition;

			await test.step('Create an object definition', async () => {
				objectDefinition =
					await apiHelpers.objectAdmin.postRandomObjectDefinition({
						objectFields,
						status: {code: 0},
					});

				apiHelpers.data.push({
					id: objectDefinition.id,
					type: 'objectDefinition',
				});
			});

			await test.step('Navigate to the object definition and add an entry', async () => {
				await viewObjectEntriesPage.goto(objectDefinition.className);

				await viewObjectEntriesPage.clickAddObjectEntry(
					objectDefinition.label['en_US']
				);
			});

			await test.step('Verify the error message is displayed', async () => {
				const errorMessage = page
					.locator('.form-group', {has: fieldContainer})
					.getByText('Please enter a valid phone number.');

				await fieldContainer.getByLabel('Phone Number').fill('1');

				await fieldContainer.getByLabel('Phone Number').blur();

				await expect(errorMessage).toBeVisible();

				await fieldContainer.getByLabel('Phone Number').clear();

				await expect(errorMessage).not.toBeVisible();
			});

			await test.step('Fill the phone number field and save the entry', async () => {
				await expect(fieldContainer.getByText(prefix)).toBeVisible();

				await fieldContainer
					.getByLabel('Phone Number')
					.fill(localNumber);

				await viewObjectEntriesPage.saveObjectEntryButton.click();

				await expect(
					viewObjectEntriesPage.successMessage
				).toBeVisible();
			});

			await test.step('Verify the phone number field values are saved', async () => {
				await viewObjectEntriesPage.backButton.click();

				await viewObjectEntriesPage.frontendDatasetItems
					.first()
					.click();

				await expect(fieldContainer.getByText(prefix)).toBeVisible();

				await expect(
					fieldContainer.getByLabel('Phone Number')
				).toHaveValue(localNumber);
			});
		}
	);
});

ckEditor4Test.describe('Manage object entries with CKEditor 4', () => {
	ckEditor4Test(
		'verify that its not possible to paste file on richText field',
		async ({apiHelpers, page, viewObjectEntriesPage}) => {
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

			await ckEditor4Test.step(
				'go to entry page, try to upload file by pasting it into editor and verify error message',
				async () => {
					await viewObjectEntriesPage.goto(
						objectDefinition.className
					);

					await viewObjectEntriesPage.clickAddObjectEntry(
						objectDefinition.label['en_US']
					);

					const editorFrame = page.frameLocator(
						'iframe[title="editor"]'
					);

					const editorBody = editorFrame.locator('body');

					const file = fs.readFileSync(
						path.join(__dirname, '../dependencies', 'tree.png')
					);

					await pasteFile(editorBody, {
						buffer: file,
						fileName: 'tree.png',
						fileType: 'image/png',
					});

					await expect(editorFrame.locator('img')).not.toBeVisible();
				}
			);
		}
	);
});

test.describe('Manage object entries through Workflow', () => {
	test('can edit object entry through workflow task page', async ({
		apiHelpers,
		configurationTabPage,
		globalMenuPage,
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

		await globalMenuPage.goToApplications('Process Builder');

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
		configurationTabPage,
		globalMenuPage,
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

		await globalMenuPage.goToApplications('Process Builder');

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

		await globalMenuPage.goToApplications('Metrics');

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

			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinitionLabel
			);

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

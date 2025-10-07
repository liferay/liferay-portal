/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectDefinitionAPI} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../fixtures/displayPageTemplatesPagesTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {fragmentsPagesTest} from '../../../fixtures/fragmentPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageManagementSiteTest} from '../../../fixtures/pageManagementSiteTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {getObjectERC} from '../../setup/page-management-site/main/utils/getObjectERC';
import chooseFileFromDocumentLibrary from '../main/utils/chooseFileFromDocumentLibrary';
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
	loginTest(),
	pageEditorPagesTest,
	pageManagementSiteTest
);

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
				path.join(
					__dirname,
					'../main/dependencies/file_upload_image_1.jpg'
				)
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
				path.join(
					__dirname,
					'../main/dependencies/file_upload_image_2.jpg'
				)
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
				path.join(
					__dirname,
					'../main/dependencies/file_upload_image_2.jpg'
				)
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
				path.join(
					__dirname,
					'../main/dependencies/high_resolution_image.jpg'
				)
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

test.describe('Drag and Drop Upload Fragment', () => {
	test('Check the functionality of the Drag and Drop Upload Fragment when the attachments are localizable', async ({
		apiHelpers,
		fragmentsPage,
		page,
		pageEditorPage,
		pageManagementSite,
	}) => {

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

		// Go to fragment administration

		await fragmentsPage.goto(pageManagementSite.friendlyUrlPath);

		// Go to configuration

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page
				.locator('.dropdown-menu')
				.getByRole('menuitem', {name: 'Configuration'}),
			trigger: page.getByLabel('Options', {exact: true}),
		});

		// Change default file upload fragment

		await page
			.locator('tr')
			.filter({hasText: 'File'})
			.getByRole('button', {name: 'Select'})
			.click();

		const frameLocator = page.frameLocator(
			'iframe[title="Select Fragment"]'
		);

		await frameLocator.getByRole('link', {name: 'Form Components'}).click();

		await frameLocator.getByLabel('Drag and Drop Upload').click();

		await page.getByRole('button', {name: 'Save'}).click();

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

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Map the form to the Attachment object and publish the page

		await pageEditorPage.mapFormFragment(formId, 'Attachment', 'all', {
			addLocalizationSelect: true,
		});

		await pageEditorPage.publishPage();

		// Go to view mode

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		// Select file from computer in the default language

		const fileChooserPromise = page.waitForEvent('filechooser');

		const firstFileUploadFragment = page
			.locator('.drag-and-drop-upload')
			.first();

		await firstFileUploadFragment
			.getByText('Select File', {exact: true})
			.click();

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles(
			path.join(__dirname, '../main/dependencies/file_upload_image_1.jpg')
		);

		await expect(
			page.getByLabel(
				'Change the file, the current file is file_upload_image_1.jpg'
			)
		).toBeVisible();

		// Select file from document library in the default language

		const secondFileUploadFragment = page
			.locator('.drag-and-drop-upload')
			.nth(1);

		await chooseFileFromDocumentLibrary({
			fileName: 'balinese.jpg',
			page,
			trigger: secondFileUploadFragment.getByText('Select File', {
				exact: true,
			}),
		});

		await expect(
			page.getByLabel('Change the file, the current file is balinese.jpg')
		).toBeVisible();

		// Change the translation to spanish and update the files

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option').filter({hasText: 'es-ES'}),
			trigger: page.getByLabel('Select a language, current language:'),
		});

		await firstFileUploadFragment
			.getByTitle('Change File', {exact: true})
			.click();

		await fileChooser.setFiles(
			path.join(__dirname, '../main/dependencies/file_upload_image_2.jpg')
		);

		await expect(
			page.getByLabel(
				'Change the file, the current file is file_upload_image_2.jpg'
			)
		).toBeVisible();

		// Choose other language to check the default values

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option').filter({hasText: 'ja-JP'}),
			trigger: page.getByLabel('Select a language, current language:'),
		});

		await expect(
			page.getByLabel(
				'Change the file, the current file is file_upload_image_1.jpg'
			)
		).toBeVisible();

		await expect(
			page.getByLabel('Change the file, the current file is balinese.jpg')
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

		const filesFromLibrary = Object.entries(item.filesFromLibrary_i18n).map(
			([locale, value]: [string, any]) => [locale, value.name]
		);

		expect(filesFromLibrary).toStrictEqual([['en_US', 'balinese.jpg']]);
	});

	test('Check the functionality of the Drag and Drop Upload Fragment when the attachments are not localizable', async ({
		apiHelpers,
		fragmentsPage,
		page,
		pageEditorPage,
		pageManagementSite,
	}) => {

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
						localized: false,
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

		// Go to fragment administration

		await fragmentsPage.goto(pageManagementSite.friendlyUrlPath);

		// Go to configuration

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page
				.locator('.dropdown-menu')
				.getByRole('menuitem', {name: 'Configuration'}),
			trigger: page.getByLabel('Options', {exact: true}),
		});

		// Change default file upload fragment

		await page
			.locator('tr')
			.filter({hasText: 'File'})
			.getByRole('button', {name: 'Select'})
			.click();

		const frameLocator = page.frameLocator(
			'iframe[title="Select Fragment"]'
		);

		await frameLocator.getByRole('link', {name: 'Form Components'}).click();

		await frameLocator.getByLabel('Drag and Drop Upload').click();

		await page.getByRole('button', {name: 'Save'}).click();

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

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Map the form to the Attachment object and publish the page

		await pageEditorPage.mapFormFragment(formId, 'Attachment', 'all');

		await pageEditorPage.publishPage();

		// Go to view mode

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		// Select file from computer in the default language

		const fileChooserPromise = page.waitForEvent('filechooser');

		const firstFileUploadFragment = page
			.locator('.drag-and-drop-upload')
			.first();

		await firstFileUploadFragment
			.getByText('Select File', {exact: true})
			.click();

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles(
			path.join(__dirname, '../main/dependencies/file_upload_image_1.jpg')
		);

		await expect(
			page.getByLabel(
				'Change the file, the current file is file_upload_image_1.jpg'
			)
		).toBeVisible();

		// Select file from document library in the default language

		const secondFileUploadFragment = page
			.locator('.drag-and-drop-upload')
			.nth(1);

		await chooseFileFromDocumentLibrary({
			fileName: 'balinese.jpg',
			page,
			trigger: secondFileUploadFragment.getByText('Select File', {
				exact: true,
			}),
		});

		await expect(
			page.getByLabel('Change the file, the current file is balinese.jpg')
		).toBeVisible();

		// Check you can't change the language

		await expect(
			page.getByLabel('Select a language, current language:')
		).not.toBeAttached();

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

		expect(item.filesFromComputer.name).toBe('file_upload_image_1.jpg');
		expect(item.filesFromLibrary.name).toBe('balinese.jpg');
	});
});

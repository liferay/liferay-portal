/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import getRandomString from '../../../../utils/getRandomString';
import {performUserSwitchViaApi} from '../../../../utils/performLogin';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {cmsPagesTest} from '../../../site-cms-site-initializer/main/fixtures/cmsPagesTest';
import {registerUserCredentials} from '../../../site-cms-site-initializer/main/spaces/helpers/roleMembership';
import {structureBuilderPagesTest} from '../../../site-cms-site-initializer/structure-builder/fixtures/structureBuilderPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
	}),
	loginTest(),
	structureBuilderPagesTest
);

const IMAGE_BASE64 = readFileSync(
	path.join(__dirname, '../../dependencies/sample_small_wide_400x300.jpg')
).toString('base64');

async function createSpace(apiHelpers: DataApiHelpers) {
	return apiHelpers.headlessAssetLibrary.createAssetLibrary({
		name: getRandomString(),
		settings: {},
		type: 'Space',
	});
}

async function prepareUser(apiHelpers: DataApiHelpers) {
	const user = await apiHelpers.headlessAdminUser.postUserAccount();

	registerUserCredentials(user);

	await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(user.id);
	await apiHelpers.jsonWebServicesUser.answerReminderQuery(user.id);

	return user;
}

async function addSpaceUser(
	apiHelpers: DataApiHelpers,
	spaceExternalReferenceCode: string,
	spaceRoleNames: string[] = []
) {
	const user = await prepareUser(apiHelpers);

	await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
		spaceExternalReferenceCode,
		user.externalReferenceCode
	);

	if (spaceRoleNames.length) {
		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccountRoles(
			spaceExternalReferenceCode,
			user.externalReferenceCode,
			spaceRoleNames
		);
	}

	return user;
}

async function startSessionAs(page: Page, alternateName: string) {
	await performUserSwitchViaApi(page, alternateName);

	await page.goto(PORTLET_URLS.cmsHome, {waitUntil: 'domcontentloaded'});
}

// Publishing a structure edit that may impact stored data (such as removing a
// field) raises a "Publish Content Structure Changes" confirmation that the
// shared publishStructure helper does not handle.

async function publishStructureWithDataChange(page: Page) {
	await page.getByRole('button', {exact: true, name: 'Publish'}).click();

	const confirmButton = page
		.getByRole('dialog', {name: 'Publish Content Structure Changes'})
		.getByRole('button', {name: 'Publish'});

	if (
		await confirmButton
			.waitFor({state: 'visible', timeout: 3000})
			.then(() => true)
			.catch(() => false)
	) {
		await confirmButton.click();
	}

	await waitForAlert(page, 'published successfully', {timeout: 10000});
}

async function createBasicContentStructure(
	apiHelpers: DataApiHelpers,
	spaceExternalReferenceCode: string,
	listTypeDefinition: {externalReferenceCode: string; id: number}
) {
	const definition = await apiHelpers.objectAdmin.postRandomObjectDefinition({
		objectDefinitionSettings: [
			{
				name: 'acceptedGroupExternalReferenceCodes',
				value: spaceExternalReferenceCode as unknown as object,
			},
		],
		objectFields: [
			{
				DBType: 'String',
				businessType: 'Text',
				externalReferenceCode: getRandomString(),
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: 'en_US',
				label: {en_US: 'Title'},
				localized: true,
				name: 'title',
				required: true,
			},
			{
				DBType: 'Integer',
				businessType: 'Integer',
				externalReferenceCode: getRandomString(),
				indexed: true,
				indexedAsKeyword: false,
				label: {en_US: 'Number'},
				name: 'number',
			},
			{
				DBType: 'Date',
				businessType: 'Date',
				externalReferenceCode: getRandomString(),
				indexed: true,
				indexedAsKeyword: false,
				label: {en_US: 'Date'},
				name: 'date',
			},
			{
				DBType: 'Boolean',
				businessType: 'Boolean',
				externalReferenceCode: getRandomString(),
				indexed: true,
				indexedAsKeyword: false,
				label: {en_US: 'Checkbox'},
				name: 'checkbox',
			},
			{
				DBType: 'String',
				businessType: 'Picklist',
				externalReferenceCode: getRandomString(),
				indexed: true,
				indexedAsKeyword: false,
				label: {en_US: 'Select'},
				listTypeDefinitionExternalReferenceCode:
					listTypeDefinition.externalReferenceCode,
				listTypeDefinitionId: listTypeDefinition.id,
				name: 'select',
			},
		],
		objectFolderExternalReferenceCode: 'L_CMS_CONTENT_STRUCTURES',
		scope: 'depot',
		status: {code: 0},
		titleObjectFieldName: 'title',
	});

	apiHelpers.data.push({
		id: definition.id as number,
		type: 'objectDefinition',
	});

	return {
		applicationName: (definition.restContextPath as string).replace(
			/^\/o\//,
			''
		),
		label: (definition.label?.en_US ?? definition.name) as string,
	};
}

async function createBasicFileStructure(
	apiHelpers: DataApiHelpers,
	spaceExternalReferenceCode: string
) {
	const definition = await apiHelpers.objectAdmin.postRandomObjectDefinition({
		objectDefinitionSettings: [
			{
				name: 'acceptedGroupExternalReferenceCodes',
				value: spaceExternalReferenceCode as unknown as object,
			},
		],
		objectFields: [
			{
				DBType: 'String',
				businessType: 'Text',
				externalReferenceCode: getRandomString(),
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: 'en_US',
				label: {en_US: 'Title'},
				localized: true,
				name: 'title',
				required: true,
			},
			{
				DBType: 'Long',
				businessType: 'Attachment',
				externalReferenceCode: getRandomString(),
				label: {en_US: 'File'},
				name: 'file',
				objectFieldSettings: [
					{
						name: 'acceptedFileExtensions',
						value: '*' as unknown as object,
					},
					{name: 'maximumFileSize', value: 100 as unknown as object},
					{
						name: 'fileSource',
						value: 'userComputerToCMSBasicDocument' as unknown as object,
					},
					{
						name: 'showFilesInLibrary',
						value: false as unknown as object,
					},
				],
				required: true,
			},
			{
				DBType: 'String',
				businessType: 'Text',
				externalReferenceCode: getRandomString(),
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: 'en_US',
				label: {en_US: 'Caption'},
				localized: true,
				name: 'caption',
			},
			{
				DBType: 'Integer',
				businessType: 'Integer',
				externalReferenceCode: getRandomString(),
				indexed: true,
				indexedAsKeyword: false,
				label: {en_US: 'Pages'},
				name: 'pages',
			},
			{
				DBType: 'Date',
				businessType: 'Date',
				externalReferenceCode: getRandomString(),
				indexed: true,
				indexedAsKeyword: false,
				label: {en_US: 'Release Date'},
				name: 'publishdate',
			},
			{
				DBType: 'Boolean',
				businessType: 'Boolean',
				externalReferenceCode: getRandomString(),
				indexed: true,
				indexedAsKeyword: false,
				label: {en_US: 'Featured'},
				name: 'featured',
			},
		],
		objectFolderExternalReferenceCode: 'L_CMS_FILE_TYPES',
		scope: 'depot',
		status: {code: 0},
		titleObjectFieldName: 'title',
	});

	apiHelpers.data.push({
		id: definition.id as number,
		type: 'objectDefinition',
	});

	return {
		applicationName: (definition.restContextPath as string).replace(
			/^\/o\//,
			''
		),
		id: definition.id as number,
		label: (definition.label?.en_US ?? definition.name) as string,
	};
}

async function createSimpleContentStructure(
	apiHelpers: DataApiHelpers,
	spaceExternalReferenceCode: string
) {
	const definition = await apiHelpers.objectAdmin.postRandomObjectDefinition({
		objectDefinitionSettings: [
			{
				name: 'acceptedGroupExternalReferenceCodes',
				value: spaceExternalReferenceCode as unknown as object,
			},
		],
		objectFields: [
			{
				DBType: 'String',
				businessType: 'Text',
				externalReferenceCode: getRandomString(),
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: 'en_US',
				label: {en_US: 'Title'},
				localized: true,
				name: 'title',
				required: true,
			},
			{
				DBType: 'String',
				businessType: 'Text',
				externalReferenceCode: getRandomString(),
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: 'en_US',
				label: {en_US: 'Body'},
				localized: true,
				name: 'body',
			},
		],
		objectFolderExternalReferenceCode: 'L_CMS_CONTENT_STRUCTURES',
		scope: 'depot',
		status: {code: 0},
		titleObjectFieldName: 'title',
	});

	apiHelpers.data.push({
		id: definition.id as number,
		type: 'objectDefinition',
	});

	return {
		applicationName: (definition.restContextPath as string).replace(
			/^\/o\//,
			''
		),
		id: definition.id as number,
		label: (definition.label?.en_US ?? definition.name) as string,
	};
}

async function createPicklist(apiHelpers: DataApiHelpers, options: string[]) {
	const listTypeDefinition =
		await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

	for (const option of options) {
		await apiHelpers.listTypeAdmin.postListTypeEntry({
			key: option,
			listTypeDefinitionExternalReferenceCode:
				listTypeDefinition.externalReferenceCode,
			name_i18n: {en_US: option},
		});
	}

	return listTypeDefinition;
}

test(
	'A file uploaded with a basic File Structure saves and displays all field values',
	{tag: ['@LPD-95535', '@LPD-95535/TC-12.a']},
	async ({apiHelpers, assetsPage, page}) => {
		test.setTimeout(120000);

		const id = getRandomString();
		const title = `File ${id}`;

		const space = await createSpace(apiHelpers);

		const {applicationName} = await createBasicFileStructure(
			apiHelpers,
			space.externalReferenceCode
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				caption: 'A descriptive caption',
				featured: true,
				file: {fileBase64: IMAGE_BASE64, name: `${title}.jpg`},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				pages: 7,
				publishdate: '2026-06-15',
				title,
			},
			applicationName,
			space.name
		);

		await assetsPage.gotoSpaceFiles(space.name);

		await expect(assetsPage.getItem(title)).toBeVisible({timeout: 15000});

		await assetsPage.execItemAction({action: 'Edit', filter: title});

		await expect(page.getByRole('textbox', {name: 'Title'})).toHaveValue(
			title
		);
		await expect(page.getByRole('textbox', {name: 'Caption'})).toHaveValue(
			'A descriptive caption'
		);
		await expect(page.getByRole('spinbutton', {name: 'Pages'})).toHaveValue(
			'7'
		);
		await expect(
			page.getByRole('textbox', {name: 'Release Date'})
		).toHaveValue(/2026/);
		await expect(
			page.getByRole('checkbox', {name: 'Featured'})
		).toBeChecked();
	}
);

test(
	'A content entry created with a basic Content Structure saves and displays all field values',
	{tag: ['@LPD-95535', '@LPD-95535/TC-12.c']},
	async ({apiHelpers, assetsPage, contentsPage, page}) => {
		test.setTimeout(120000);

		const id = getRandomString();
		const title = `Title ${id}`;

		const space = await createSpace(apiHelpers);

		const listTypeDefinition = await createPicklist(apiHelpers, [
			'Apple',
			'Banana',
		]);

		const {applicationName} = await createBasicContentStructure(
			apiHelpers,
			space.externalReferenceCode,
			listTypeDefinition
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				checkbox: true,
				date: '2026-06-15',
				number: 42,
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				select: {key: 'apple'},
				title,
			},
			applicationName,
			space.name
		);

		await assetsPage.gotoContents(space.name);

		await contentsPage.editContent(title);

		await expect(page.getByRole('textbox', {name: 'Title'})).toHaveValue(
			title
		);
		await expect(
			page.getByRole('spinbutton', {name: 'Number'})
		).toHaveValue('42');
		await expect(page.getByRole('textbox', {name: 'Date'})).toHaveValue(
			/2026/
		);
		await expect(
			page.getByRole('checkbox', {name: 'Checkbox'})
		).toBeChecked();
		await expect(
			page.locator('[role="option"][data-option-value="apple"]')
		).toHaveAttribute('aria-selected', 'true');
	}
);

test(
	'Structures are not accessible to a Space Administrator or a Space Member',
	{tag: ['@LPD-95535', '@LPD-95535/TC-12.i']},
	async ({apiHelpers, page}) => {
		test.setTimeout(120000);

		const space = await createSpace(apiHelpers);

		const spaceAdministrator = await addSpaceUser(
			apiHelpers,
			space.externalReferenceCode,
			['Asset Library Administrator']
		);

		const spaceMember = await addSpaceUser(
			apiHelpers,
			space.externalReferenceCode,
			['Asset Library Member']
		);

		for (const user of [spaceAdministrator, spaceMember]) {
			await startSessionAs(page, user.alternateName);

			await page.goto(PORTLET_URLS.cmsStructures, {
				waitUntil: 'domcontentloaded',
			});

			await expect(
				page.getByRole('heading', {name: 'Content Structures'})
			).toBeHidden();
		}
	}
);

test(
	'A Content Structure available to all Spaces can be used to create content in any Space',
	{tag: ['@LPD-95535', '@LPD-95535/TC-12.j']},
	async ({apiHelpers, contentsPage, page, structureBuilderPage}) => {
		test.setTimeout(120000);

		const firstSpace = await createSpace(apiHelpers);
		const secondSpace = await createSpace(apiHelpers);

		const label = `Structure ${getRandomString()}`;

		await structureBuilderPage.goToCreateStructure('content');

		await structureBuilderPage.enableForAllSpaces();

		await structureBuilderPage.changeStructureSettings({label});

		await structureBuilderPage.saveStructure({autoDelete: true});

		await structureBuilderPage.publishStructure();

		for (const space of [firstSpace, secondSpace]) {
			await contentsPage.goto();

			await contentsPage.createContent(label, space.name);

			await expect(
				page.getByRole('tab', {name: 'General'})
			).toBeVisible();
		}
	}
);

test(
	'Adding an optional field to a Content Structure keeps existing entries intact and is fillable on new entries',
	{tag: ['@LPD-95535', '@LPD-95535/TC-12.e']},
	async ({
		apiHelpers,
		assetsPage,
		contentsPage,
		page,
		structureBuilderPage,
	}) => {
		test.setTimeout(180000);

		const space = await createSpace(apiHelpers);

		const {applicationName, id, label} = await createSimpleContentStructure(
			apiHelpers,
			space.externalReferenceCode
		);

		const existingTitle = `Existing ${getRandomString()}`;

		await apiHelpers.objectEntry.postObjectEntry(
			{
				body: 'Original body',
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: existingTitle,
			},
			applicationName,
			space.name
		);

		await test.step('CA adds a new optional field to the structure', async () => {
			await structureBuilderPage.editStructure(id);

			await structureBuilderPage.addField('Text');

			await structureBuilderPage.selectFields([{label: 'Text'}]);

			await structureBuilderPage.changeFieldSettings({label: 'Subtitle'});

			await structureBuilderPage.publishStructure();
		});

		await test.step('The pre-existing entry is intact and the new field is empty', async () => {
			await assetsPage.gotoContents(space.name);

			await contentsPage.editContent(existingTitle);

			await expect(
				page.getByRole('textbox', {name: 'Title'}).first()
			).toHaveValue(existingTitle);
			await expect(page.getByRole('textbox', {name: 'Body'})).toHaveValue(
				'Original body'
			);
			await expect(
				page.getByRole('textbox', {name: 'Subtitle'})
			).toHaveValue('');
		});

		await test.step('A new entry exposes the new field as fillable', async () => {
			await contentsPage.goto();

			await contentsPage.createContent(label, space.name);

			const subtitle = page.getByRole('textbox', {name: 'Subtitle'});

			await subtitle.fill('A new subtitle');

			await expect(subtitle).toHaveValue('A new subtitle');
		});
	}
);

test(
	'Removing a non-mandatory field from a File Structure keeps existing files intact and hides the field on new uploads',
	{tag: ['@LPD-95535', '@LPD-95535/TC-12.f']},
	async ({apiHelpers, assetsPage, page, structureBuilderPage}) => {
		test.setTimeout(180000);

		const space = await createSpace(apiHelpers);

		const {applicationName, id} = await createBasicFileStructure(
			apiHelpers,
			space.externalReferenceCode
		);

		const existingTitle = `File ${getRandomString()}`;

		await apiHelpers.objectEntry.postObjectEntry(
			{
				caption: 'Keep this caption',
				featured: false,
				file: {fileBase64: IMAGE_BASE64, name: `${existingTitle}.jpg`},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				pages: 3,
				title: existingTitle,
			},
			applicationName,
			space.name
		);

		await test.step('CA removes a non-mandatory field from the structure', async () => {
			await structureBuilderPage.editStructure(id);

			await structureBuilderPage.deleteFields([{label: 'Caption'}]);

			await publishStructureWithDataChange(page);
		});

		await test.step('The pre-existing file is intact and no longer shows the removed field', async () => {
			await assetsPage.gotoSpaceFiles(space.name);

			await assetsPage.execItemAction({
				action: 'Edit',
				filter: existingTitle,
			});

			await expect(
				page.getByRole('textbox', {name: 'Title'})
			).toHaveValue(existingTitle);
			await expect(
				page.getByRole('textbox', {name: 'Caption'})
			).toBeHidden();
		});

		await test.step('A newly uploaded file no longer shows the removed field', async () => {
			const newTitle = `File ${getRandomString()}`;

			await apiHelpers.objectEntry.postObjectEntry(
				{
					featured: true,
					file: {
						fileBase64: IMAGE_BASE64,
						name: `${newTitle}.jpg`,
					},
					objectEntryFolderExternalReferenceCode: 'L_FILES',
					pages: 5,
					title: newTitle,
				},
				applicationName,
				space.name
			);

			await assetsPage.gotoSpaceFiles(space.name);

			await assetsPage.execItemAction({action: 'Edit', filter: newTitle});

			await expect(
				page.getByRole('textbox', {name: 'Title'})
			).toHaveValue(newTitle);
			await expect(
				page.getByRole('textbox', {name: 'Caption'})
			).toBeHidden();
		});
	}
);

test(
	'A duplicated Content Structure can be modified without affecting entries of the original',
	{tag: ['@LPD-95535', '@LPD-95535/TC-12.h']},
	async ({
		apiHelpers,
		assetsPage,
		contentsPage,
		page,
		structureBuilderPage,
	}) => {
		test.setTimeout(180000);

		const space = await createSpace(apiHelpers);

		// The CMS has no one-click structure duplication, so the duplicate is
		// produced by recreating the original structure's fields.

		const original = await createSimpleContentStructure(
			apiHelpers,
			space.externalReferenceCode
		);

		const duplicate = await createSimpleContentStructure(
			apiHelpers,
			space.externalReferenceCode
		);

		const originalTitle = `Original ${getRandomString()}`;
		const duplicateTitle = `Duplicate ${getRandomString()}`;

		await apiHelpers.objectEntry.postObjectEntry(
			{
				body: 'Original body',
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: originalTitle,
			},
			original.applicationName,
			space.name
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				body: 'Duplicate body',
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: duplicateTitle,
			},
			duplicate.applicationName,
			space.name
		);

		await test.step('A field on the duplicate is renamed', async () => {
			await structureBuilderPage.editStructure(duplicate.id);

			await structureBuilderPage.selectFields([{label: 'Body'}]);

			await structureBuilderPage.changeFieldSettings({
				label: 'Renamed Body',
			});

			await publishStructureWithDataChange(page);
		});

		await test.step('The duplicate entry reflects the renamed field', async () => {
			await assetsPage.gotoContents(space.name);

			await contentsPage.editContent(duplicateTitle);

			await expect(
				page.getByRole('textbox', {name: 'Renamed Body'})
			).toHaveValue('Duplicate body');
			await expect(
				page.getByRole('textbox', {exact: true, name: 'Body'})
			).toBeHidden();
		});

		await test.step('The original entry is unaffected and keeps its field', async () => {
			await assetsPage.gotoContents(space.name);

			await contentsPage.editContent(originalTitle);

			await expect(
				page.getByRole('textbox', {exact: true, name: 'Body'})
			).toHaveValue('Original body');
			await expect(
				page.getByRole('textbox', {name: 'Renamed Body'})
			).toBeHidden();
		});
	}
);

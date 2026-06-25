/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch, userData} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
		'LPD-17564': {enabled: true},
	}),
	loginTest()
);

function createSpace(apiHelpers: DataApiHelpers, name: string) {
	return apiHelpers.headlessAssetLibrary.createAssetLibrary({
		name,
		settings: {},
		type: 'Space',
	});
}

function createSpaces(apiHelpers: DataApiHelpers, ...names: string[]) {
	return Promise.all(names.map((name) => createSpace(apiHelpers, name)));
}

function createFolder(
	apiHelpers: DataApiHelpers,
	{
		parentObjectEntryFolderExternalReferenceCode,
		scopeKey,
		title,
	}: {
		parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS' | 'L_FILES';
		scopeKey: string;
		title: string;
	}
) {
	return apiHelpers.objectFolder.createObjectEntryFolder({
		parentObjectEntryFolderExternalReferenceCode,
		scopeKey,
		title,
	});
}

function createContent(
	apiHelpers: DataApiHelpers,
	{
		applicationName = 'cms/basic-web-contents',
		objectEntryFolderExternalReferenceCode = 'L_CONTENTS',
		scopeKey,
		title,
	}: {
		applicationName?: string;
		objectEntryFolderExternalReferenceCode?: string;
		scopeKey: string;
		title: string;
	}
) {
	return apiHelpers.objectEntry.postObjectEntry(
		{objectEntryFolderExternalReferenceCode, title},
		applicationName,
		scopeKey
	);
}

function createFile(
	apiHelpers: DataApiHelpers,
	{
		objectEntryFolderExternalReferenceCode = 'L_FILES',
		scopeKey,
		title,
	}: {
		objectEntryFolderExternalReferenceCode?: string;
		scopeKey: string;
		title: string;
	}
) {
	return apiHelpers.objectEntry.postObjectEntry(
		{
			file: {
				fileBase64: 'R0lGODlhAQABAAAAACw=',
				name: `file_${getRandomString()}.png`,
			},
			objectEntryFolderExternalReferenceCode,
			title,
		},
		'cms/basic-documents',
		scopeKey
	);
}

test.beforeEach(async ({apiHelpers, page}) => {
	const user = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[user.alternateName] = {
		name: user.givenName,
		password: 'test',
		surname: user.familyName,
	};

	const cmsAdminRole =
		await apiHelpers.headlessAdminUser.getRoleByName('CMS Administrator');

	await apiHelpers.headlessAdminUser.postRoleUserAccountAssociation(
		cmsAdminRole.id,
		Number(user.id)
	);

	await performUserSwitch(page, user.alternateName);
});

test(
	'Can move a content to a folder in the same Space',
	{tag: '@LPD-89762'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const spaceName = `Space ${getRandomString()}`;
		const destinationFolderName = `Destination ${getRandomString()}`;
		const contentTitle = `Content ${getRandomString()}`;

		await test.step('Create a new Space', async () => {
			await createSpace(apiHelpers, spaceName);
		});

		let destinationFolderId: number;

		await test.step('Create a destination folder in the Space', async () => {
			const folder = await createFolder(apiHelpers, {
				parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				scopeKey: spaceName,
				title: destinationFolderName,
			});

			destinationFolderId = folder.id;
		});

		await test.step('Create a content at the root of the Space', async () => {
			await createContent(apiHelpers, {
				applicationName,
				scopeKey: spaceName,
				title: contentTitle,
			});
		});

		await test.step('Move the content to the destination folder', async () => {
			await assetsPage.gotoAll();

			await assetsPage.moveTo({
				destinationFolder: destinationFolderName,
				destinationSpace: spaceName,
				itemTitle: contentTitle,
			});
		});

		await test.step('Info alert for the move is displayed', async () => {
			await waitForAlert(
				page,
				`Info:Moving ${contentTitle} to ${destinationFolderName}.`,
				{type: 'info'}
			);
		});

		await test.step('Success alert for the move is displayed', async () => {
			await waitForAlert(
				page,
				`Success:${contentTitle} was successfully moved to ${destinationFolderName}.`,
				{first: true}
			);
		});

		await test.step('The content is in the destination folder', async () => {
			const response =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
					applicationName,
					encodeURIComponent(spaceName),
					new URLSearchParams({pageSize: '100'})
				);

			const movedItems = response.items.filter(
				(item: {objectEntryFolderId: number}) =>
					item.objectEntryFolderId === destinationFolderId
			);

			expect(movedItems).toHaveLength(1);
			expect(movedItems[0].title).toBe(contentTitle);
		});
	}
);

test(
	'Can move a content to a folder in a different Space',
	{tag: '@LPD-89762'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const sourceSpaceName = `Space ${getRandomString()}`;
		const destinationSpaceName = `Space ${getRandomString()}`;
		const destinationFolderName = `Destination ${getRandomString()}`;
		const contentTitle = `Content ${getRandomString()}`;

		await test.step('Create source and destination Spaces', async () => {
			await createSpaces(
				apiHelpers,
				sourceSpaceName,
				destinationSpaceName
			);
		});

		let destinationFolderId: number;

		await test.step('Create a destination folder in the destination Space', async () => {
			const folder = await createFolder(apiHelpers, {
				parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				scopeKey: destinationSpaceName,
				title: destinationFolderName,
			});

			destinationFolderId = folder.id;
		});

		await test.step('Create a content in the source Space', async () => {
			await createContent(apiHelpers, {
				applicationName,
				scopeKey: sourceSpaceName,
				title: contentTitle,
			});
		});

		await test.step('Move the content to the destination folder in the destination Space', async () => {
			await assetsPage.gotoAll();

			await assetsPage.moveTo({
				destinationFolder: destinationFolderName,
				destinationSpace: destinationSpaceName,
				itemTitle: contentTitle,
			});
		});

		await test.step('Info alert for the move is displayed', async () => {
			await waitForAlert(
				page,
				`Info:Moving ${contentTitle} to ${destinationFolderName}.`,
				{type: 'info'}
			);
		});

		await test.step('Success alert for the move is displayed', async () => {
			await waitForAlert(
				page,
				`Success:${contentTitle} was successfully moved to ${destinationFolderName}.`,
				{first: true}
			);
		});

		await test.step('The content is in the destination folder in the destination Space', async () => {
			const response =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
					applicationName,
					encodeURIComponent(destinationSpaceName),
					new URLSearchParams({pageSize: '100'})
				);

			const movedItems = response.items.filter(
				(item: {objectEntryFolderId: number}) =>
					item.objectEntryFolderId === destinationFolderId
			);

			expect(movedItems).toHaveLength(1);
			expect(movedItems[0].title).toBe(contentTitle);
		});

		await test.step('The content is no longer in the source Space', async () => {
			const response =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
					applicationName,
					encodeURIComponent(sourceSpaceName),
					new URLSearchParams({pageSize: '100'})
				);

			const sourceItems = response.items.filter(
				(item: {title: string}) => item.title === contentTitle
			);

			expect(sourceItems).toHaveLength(0);
		});
	}
);

test(
	'Can copy a file to a folder in a different Space',
	{tag: '@LPD-89762'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-documents';
		const sourceSpaceName = `Space ${getRandomString()}`;
		const destinationSpaceName = `Space ${getRandomString()}`;
		const destinationFolderName = `Destination ${getRandomString()}`;
		const fileTitle = `File ${getRandomString()}`;

		await test.step('Create source and destination Spaces', async () => {
			await createSpaces(
				apiHelpers,
				sourceSpaceName,
				destinationSpaceName
			);
		});

		let destinationFolderId: number;

		await test.step('Create a destination folder in the destination Space', async () => {
			const folder = await createFolder(apiHelpers, {
				parentObjectEntryFolderExternalReferenceCode: 'L_FILES',
				scopeKey: destinationSpaceName,
				title: destinationFolderName,
			});

			destinationFolderId = folder.id;
		});

		await test.step('Create a file in the source Space', async () => {
			await createFile(apiHelpers, {
				scopeKey: sourceSpaceName,
				title: fileTitle,
			});
		});

		await test.step('Copy the file to the destination folder in the destination Space', async () => {
			await assetsPage.gotoAll();

			await assetsPage.copyTo({
				destinationFolder: destinationFolderName,
				destinationSpace: destinationSpaceName,
				itemTitle: fileTitle,
			});
		});

		await test.step('Info alert for the copy is displayed', async () => {
			await waitForAlert(
				page,
				`Info:Copying ${fileTitle} to ${destinationFolderName}.`,
				{type: 'info'}
			);
		});

		await test.step('Success alert for the copy is displayed', async () => {
			await waitForAlert(
				page,
				`Success:${fileTitle} was successfully copied to ${destinationFolderName}.`,
				{first: true}
			);
		});

		await test.step('The file is in the destination folder in the destination Space', async () => {
			const response =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
					applicationName,
					encodeURIComponent(destinationSpaceName),
					new URLSearchParams({pageSize: '100'})
				);

			const copiedItems = response.items.filter(
				(item: {objectEntryFolderId: number}) =>
					item.objectEntryFolderId === destinationFolderId
			);

			expect(copiedItems).toHaveLength(1);
			expect(copiedItems[0].title).toBe(fileTitle);
		});

		await test.step('The original file is still present in the source Space', async () => {
			const response =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
					applicationName,
					encodeURIComponent(sourceSpaceName),
					new URLSearchParams({pageSize: '100'})
				);

			const sourceItems = response.items.filter(
				(item: {title: string}) => item.title === fileTitle
			);

			expect(sourceItems).toHaveLength(1);
		});
	}
);

test(
	'Can move a folder with its contents to a different Space',
	{tag: '@LPD-89762'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const sourceSpaceName = `Space ${getRandomString()}`;
		const destinationSpaceName = `Space ${getRandomString()}`;
		const destinationFolderName = `Destination ${getRandomString()}`;
		const sourceFolderName = `Source ${getRandomString()}`;
		const contentTitle = `Content ${getRandomString()}`;

		await test.step('Create source and destination Spaces', async () => {
			await createSpaces(
				apiHelpers,
				sourceSpaceName,
				destinationSpaceName
			);
		});

		await test.step('Create a destination folder in the destination Space', async () => {
			await createFolder(apiHelpers, {
				parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				scopeKey: destinationSpaceName,
				title: destinationFolderName,
			});
		});

		await test.step('Create a source folder with a content inside it in the source Space', async () => {
			const sourceFolder = await createFolder(apiHelpers, {
				parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				scopeKey: sourceSpaceName,
				title: sourceFolderName,
			});

			await createContent(apiHelpers, {
				applicationName,
				objectEntryFolderExternalReferenceCode:
					sourceFolder.externalReferenceCode,
				scopeKey: sourceSpaceName,
				title: contentTitle,
			});
		});

		await test.step('Move the source folder to the destination folder in the destination Space', async () => {
			await assetsPage.gotoSpaceContents(sourceSpaceName);

			await assetsPage.moveTo({
				destinationFolder: destinationFolderName,
				destinationSpace: destinationSpaceName,
				itemTitle: sourceFolderName,
			});
		});

		await test.step('Info alert for the folder move is displayed', async () => {
			await waitForAlert(
				page,
				`Info:Moving ${sourceFolderName} to ${destinationFolderName}.`,
				{type: 'info'}
			);
		});

		await test.step('Success alert for the folder move is displayed', async () => {
			await waitForAlert(
				page,
				`Success:${sourceFolderName} was successfully moved to ${destinationFolderName}.`,
				{first: true}
			);
		});

		await test.step('The content is now in the destination Space', async () => {
			const response =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
					applicationName,
					encodeURIComponent(destinationSpaceName),
					new URLSearchParams({pageSize: '100'})
				);

			const movedItems = response.items.filter(
				(item: {title: string}) => item.title === contentTitle
			);

			expect(movedItems).toHaveLength(1);
		});

		await test.step('The content is no longer in the source Space', async () => {
			const response =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
					applicationName,
					encodeURIComponent(sourceSpaceName),
					new URLSearchParams({pageSize: '100'})
				);

			const sourceItems = response.items.filter(
				(item: {title: string}) => item.title === contentTitle
			);

			expect(sourceItems).toHaveLength(0);
		});
	}
);

test(
	'Can copy a folder with its contents to a different Space',
	{tag: '@LPD-89762'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const sourceSpaceName = `Space ${getRandomString()}`;
		const destinationSpaceName = `Space ${getRandomString()}`;
		const destinationFolderName = `Destination ${getRandomString()}`;
		const sourceFolderName = `Source ${getRandomString()}`;
		const contentTitle = `Content ${getRandomString()}`;

		await test.step('Create source and destination Spaces', async () => {
			await createSpaces(
				apiHelpers,
				sourceSpaceName,
				destinationSpaceName
			);
		});

		await test.step('Create a destination folder in the destination Space', async () => {
			await createFolder(apiHelpers, {
				parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				scopeKey: destinationSpaceName,
				title: destinationFolderName,
			});
		});

		await test.step('Create a source folder with a content inside it in the source Space', async () => {
			const sourceFolder = await createFolder(apiHelpers, {
				parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				scopeKey: sourceSpaceName,
				title: sourceFolderName,
			});

			await createContent(apiHelpers, {
				applicationName,
				objectEntryFolderExternalReferenceCode:
					sourceFolder.externalReferenceCode,
				scopeKey: sourceSpaceName,
				title: contentTitle,
			});
		});

		await test.step('Copy the source folder to the destination folder in the destination Space', async () => {
			await assetsPage.gotoSpaceContents(sourceSpaceName);

			await assetsPage.copyTo({
				destinationFolder: destinationFolderName,
				destinationSpace: destinationSpaceName,
				itemTitle: sourceFolderName,
			});
		});

		await test.step('Info alert for the folder copy is displayed', async () => {
			await waitForAlert(
				page,
				`Info:Copying ${sourceFolderName} to ${destinationFolderName}.`,
				{type: 'info'}
			);
		});

		await test.step('Success alert for the folder copy is displayed', async () => {
			await waitForAlert(
				page,
				`Success:${sourceFolderName} was successfully copied to ${destinationFolderName}.`,
				{first: true}
			);
		});

		await test.step('The content is now in the destination Space', async () => {
			const response =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
					applicationName,
					encodeURIComponent(destinationSpaceName),
					new URLSearchParams({pageSize: '100'})
				);

			const copiedItems = response.items.filter(
				(item: {title: string}) => item.title === contentTitle
			);

			expect(copiedItems).toHaveLength(1);
		});

		await test.step('The original content is still in the source Space', async () => {
			const response =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
					applicationName,
					encodeURIComponent(sourceSpaceName),
					new URLSearchParams({pageSize: '100'})
				);

			const sourceItems = response.items.filter(
				(item: {title: string}) => item.title === contentTitle
			);

			expect(sourceItems).toHaveLength(1);
		});
	}
);

test(
	'Move shows an error when the destination Space lacks the content structure',
	{tag: '@LPD-89762'},
	async ({apiHelpers, assetsPage, page}) => {
		const sourceSpaceName = `Space ${getRandomString()}`;
		const destinationSpaceName = `Space ${getRandomString()}`;
		const destinationFolderName = `Destination ${getRandomString()}`;
		const contentTitle = `Content ${getRandomString()}`;

		let sourceSpaceERC: string;

		await test.step('Create source and destination Spaces', async () => {
			const [sourceSpace] = await createSpaces(
				apiHelpers,
				sourceSpaceName,
				destinationSpaceName
			);

			sourceSpaceERC = sourceSpace.externalReferenceCode;
		});

		await test.step('Create a destination folder in the destination Space', async () => {
			await createFolder(apiHelpers, {
				parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				scopeKey: destinationSpaceName,
				title: destinationFolderName,
			});
		});

		let applicationName: string;

		await test.step('Create a content structure available only in the source Space', async () => {
			const definition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectDefinitionSettings: [
						{
							name: 'acceptedGroupExternalReferenceCodes',
							value: sourceSpaceERC as unknown as object,
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
					],
					objectFolderExternalReferenceCode:
						'L_CMS_CONTENT_STRUCTURES',
					scope: 'depot',
					status: {code: 0},
					titleObjectFieldName: 'title',
				});

			expect(
				definition.id,
				'object definition was created'
			).toBeDefined();
			expect(
				definition.restContextPath,
				'object definition has a REST context path'
			).toBeDefined();

			apiHelpers.data.push({
				id: definition.id as number,
				type: 'objectDefinition',
			});

			applicationName = (definition.restContextPath as string).replace(
				/^\/o\//,
				''
			);
		});

		await test.step('Seed a content in the source Space', async () => {
			await createContent(apiHelpers, {
				applicationName,
				scopeKey: sourceSpaceName,
				title: contentTitle,
			});
		});

		await test.step('Try to move the content to the destination Space', async () => {
			await assetsPage.gotoAll();

			await assetsPage.moveTo({
				destinationFolder: destinationFolderName,
				destinationSpace: destinationSpaceName,
				itemTitle: contentTitle,
			});
		});

		await test.step('Error toast informs the asset cannot be moved', async () => {
			await waitForAlert(
				page,
				'Error:The asset cannot be moved because its content type is not available in the destination space.',
				{type: 'danger'}
			);
		});
	}
);

test(
	'Destination picker filters folders by section when moving content vs file',
	{tag: '@LPD-89762'},
	async ({apiHelpers, assetsPage}) => {
		const spaceName = `Space ${getRandomString()}`;
		const contentTitle = `Content ${getRandomString()}`;
		const fileTitle = `File ${getRandomString()}`;

		await test.step('Create a new Space', async () => {
			await createSpace(apiHelpers, spaceName);
		});

		await test.step('Create a content in that Space', async () => {
			await createContent(apiHelpers, {
				scopeKey: spaceName,
				title: contentTitle,
			});
		});

		await test.step('Create a file in that Space', async () => {
			await createFile(apiHelpers, {
				scopeKey: spaceName,
				title: fileTitle,
			});
		});

		await test.step('Move To picker for a content shows only the Contents folder', async () => {
			await assetsPage.gotoAll();

			await assetsPage.execItemAction({
				action: 'Move',
				filter: contentTitle,
			});

			const dialog = assetsPage.getCopyOrMoveDestinationDialog();

			await dialog.getByLabel(spaceName).click();

			await expect(
				dialog.getByText('Showing 1 to 1 of 1 entries.')
			).toBeVisible();
			await expect(
				dialog.getByLabel('Contents', {exact: true})
			).toBeVisible();
			await expect(
				dialog.getByLabel('Files', {exact: true})
			).not.toBeVisible();

			await dialog
				.getByRole('button', {exact: true, name: 'Cancel'})
				.click();
		});

		await test.step('Move To picker for a file shows only the Files folder', async () => {
			await assetsPage.gotoAll();

			await assetsPage.execItemAction({
				action: 'Move',
				filter: fileTitle,
			});

			const dialog = assetsPage.getCopyOrMoveDestinationDialog();

			await dialog.getByLabel(spaceName).click();

			await expect(
				dialog.getByText('Showing 1 to 1 of 1 entries.')
			).toBeVisible();
			await expect(
				dialog.getByLabel('Files', {exact: true})
			).toBeVisible();
			await expect(
				dialog.getByLabel('Contents', {exact: true})
			).not.toBeVisible();

			await dialog
				.getByRole('button', {exact: true, name: 'Cancel'})
				.click();
		});
	}
);

test(
	'Can move a file to a folder in a different Space',
	{tag: '@LPD-89762'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-documents';
		const sourceSpaceName = `Space ${getRandomString()}`;
		const destinationSpaceName = `Space ${getRandomString()}`;
		const destinationFolderName = `Destination ${getRandomString()}`;
		const fileTitle = `File ${getRandomString()}`;

		await test.step('Create source and destination Spaces', async () => {
			await createSpaces(
				apiHelpers,
				sourceSpaceName,
				destinationSpaceName
			);
		});

		let destinationFolderId: number;

		await test.step('Create a destination folder in the destination Space', async () => {
			const folder = await createFolder(apiHelpers, {
				parentObjectEntryFolderExternalReferenceCode: 'L_FILES',
				scopeKey: destinationSpaceName,
				title: destinationFolderName,
			});

			destinationFolderId = folder.id;
		});

		await test.step('Create a file in the source Space', async () => {
			await createFile(apiHelpers, {
				scopeKey: sourceSpaceName,
				title: fileTitle,
			});
		});

		await test.step('Move the file to the destination folder in the destination Space', async () => {
			await assetsPage.gotoAll();

			await assetsPage.moveTo({
				destinationFolder: destinationFolderName,
				destinationSpace: destinationSpaceName,
				itemTitle: fileTitle,
			});
		});

		await test.step('Info alert for the move is displayed', async () => {
			await waitForAlert(
				page,
				`Info:Moving ${fileTitle} to ${destinationFolderName}.`,
				{type: 'info'}
			);
		});

		await test.step('Success alert for the move is displayed', async () => {
			await waitForAlert(
				page,
				`Success:${fileTitle} was successfully moved to ${destinationFolderName}.`,
				{first: true}
			);
		});

		await test.step('The file is in the destination folder in the destination Space', async () => {
			const response =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
					applicationName,
					encodeURIComponent(destinationSpaceName),
					new URLSearchParams({pageSize: '100'})
				);

			const movedItems = response.items.filter(
				(item: {objectEntryFolderId: number}) =>
					item.objectEntryFolderId === destinationFolderId
			);

			expect(movedItems).toHaveLength(1);
			expect(movedItems[0].title).toBe(fileTitle);
		});

		await test.step('The file is no longer in the source Space', async () => {
			const response =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
					applicationName,
					encodeURIComponent(sourceSpaceName),
					new URLSearchParams({pageSize: '100'})
				);

			const sourceItems = response.items.filter(
				(item: {title: string}) => item.title === fileTitle
			);

			expect(sourceItems).toHaveLength(0);
		});
	}
);

test(
	'Can copy a content to a folder in a different Space',
	{tag: '@LPD-89762'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const sourceSpaceName = `Space ${getRandomString()}`;
		const destinationSpaceName = `Space ${getRandomString()}`;
		const destinationFolderName = `Destination ${getRandomString()}`;
		const contentTitle = `Content ${getRandomString()}`;

		await test.step('Create source and destination Spaces', async () => {
			await createSpaces(
				apiHelpers,
				sourceSpaceName,
				destinationSpaceName
			);
		});

		let destinationFolderId: number;

		await test.step('Create a destination folder in the destination Space', async () => {
			const folder = await createFolder(apiHelpers, {
				parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				scopeKey: destinationSpaceName,
				title: destinationFolderName,
			});

			destinationFolderId = folder.id;
		});

		await test.step('Create a content in the source Space', async () => {
			await createContent(apiHelpers, {
				applicationName,
				scopeKey: sourceSpaceName,
				title: contentTitle,
			});
		});

		await test.step('Copy the content to the destination folder in the destination Space', async () => {
			await assetsPage.gotoAll();

			await assetsPage.copyTo({
				destinationFolder: destinationFolderName,
				destinationSpace: destinationSpaceName,
				itemTitle: contentTitle,
			});
		});

		await test.step('Info alert for the copy is displayed', async () => {
			await waitForAlert(
				page,
				`Info:Copying ${contentTitle} to ${destinationFolderName}.`,
				{type: 'info'}
			);
		});

		await test.step('Success alert for the copy is displayed', async () => {
			await waitForAlert(
				page,
				`Success:${contentTitle} was successfully copied to ${destinationFolderName}.`,
				{first: true}
			);
		});

		await test.step('The content is in the destination folder in the destination Space', async () => {
			const response =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
					applicationName,
					encodeURIComponent(destinationSpaceName),
					new URLSearchParams({pageSize: '100'})
				);

			const copiedItems = response.items.filter(
				(item: {objectEntryFolderId: number}) =>
					item.objectEntryFolderId === destinationFolderId
			);

			expect(copiedItems).toHaveLength(1);
			expect(copiedItems[0].title).toBe(contentTitle);
		});

		await test.step('The original content is still present in the source Space', async () => {
			const response =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
					applicationName,
					encodeURIComponent(sourceSpaceName),
					new URLSearchParams({pageSize: '100'})
				);

			const sourceItems = response.items.filter(
				(item: {title: string}) => item.title === contentTitle
			);

			expect(sourceItems).toHaveLength(1);
		});
	}
);

test(
	'Can move a folder of files to a different Space',
	{tag: '@LPD-89762'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-documents';
		const sourceSpaceName = `Space ${getRandomString()}`;
		const destinationSpaceName = `Space ${getRandomString()}`;
		const destinationFolderName = `Destination ${getRandomString()}`;
		const sourceFolderName = `Source ${getRandomString()}`;
		const fileTitle = `File ${getRandomString()}`;

		await test.step('Create source and destination Spaces', async () => {
			await createSpaces(
				apiHelpers,
				sourceSpaceName,
				destinationSpaceName
			);
		});

		await test.step('Create a destination Files folder in the destination Space', async () => {
			await createFolder(apiHelpers, {
				parentObjectEntryFolderExternalReferenceCode: 'L_FILES',
				scopeKey: destinationSpaceName,
				title: destinationFolderName,
			});
		});

		await test.step('Create a source Files folder with a file inside it in the source Space', async () => {
			const sourceFolder = await createFolder(apiHelpers, {
				parentObjectEntryFolderExternalReferenceCode: 'L_FILES',
				scopeKey: sourceSpaceName,
				title: sourceFolderName,
			});

			await createFile(apiHelpers, {
				objectEntryFolderExternalReferenceCode:
					sourceFolder.externalReferenceCode,
				scopeKey: sourceSpaceName,
				title: fileTitle,
			});
		});

		await test.step('Move the source folder to the destination folder in the destination Space', async () => {
			await assetsPage.gotoSpaceFiles(sourceSpaceName);

			await assetsPage.moveTo({
				destinationFolder: destinationFolderName,
				destinationSpace: destinationSpaceName,
				itemTitle: sourceFolderName,
			});
		});

		await test.step('Info alert for the folder move is displayed', async () => {
			await waitForAlert(
				page,
				`Info:Moving ${sourceFolderName} to ${destinationFolderName}.`,
				{type: 'info'}
			);
		});

		await test.step('Success alert for the folder move is displayed', async () => {
			await waitForAlert(
				page,
				`Success:${sourceFolderName} was successfully moved to ${destinationFolderName}.`,
				{first: true}
			);
		});

		await test.step('The file is now in the destination Space', async () => {
			const response =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
					applicationName,
					encodeURIComponent(destinationSpaceName),
					new URLSearchParams({pageSize: '100'})
				);

			const movedItems = response.items.filter(
				(item: {title: string}) => item.title === fileTitle
			);

			expect(movedItems).toHaveLength(1);
		});

		await test.step('The file is no longer in the source Space', async () => {
			const response =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
					applicationName,
					encodeURIComponent(sourceSpaceName),
					new URLSearchParams({pageSize: '100'})
				);

			const sourceItems = response.items.filter(
				(item: {title: string}) => item.title === fileTitle
			);

			expect(sourceItems).toHaveLength(0);
		});
	}
);

test(
	'Can copy a folder of files to a different Space',
	{tag: '@LPD-89762'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-documents';
		const sourceSpaceName = `Space ${getRandomString()}`;
		const destinationSpaceName = `Space ${getRandomString()}`;
		const destinationFolderName = `Destination ${getRandomString()}`;
		const sourceFolderName = `Source ${getRandomString()}`;
		const fileTitle = `File ${getRandomString()}`;

		await test.step('Create source and destination Spaces', async () => {
			await createSpaces(
				apiHelpers,
				sourceSpaceName,
				destinationSpaceName
			);
		});

		await test.step('Create a destination Files folder in the destination Space', async () => {
			await createFolder(apiHelpers, {
				parentObjectEntryFolderExternalReferenceCode: 'L_FILES',
				scopeKey: destinationSpaceName,
				title: destinationFolderName,
			});
		});

		await test.step('Create a source Files folder with a file inside it in the source Space', async () => {
			const sourceFolder = await createFolder(apiHelpers, {
				parentObjectEntryFolderExternalReferenceCode: 'L_FILES',
				scopeKey: sourceSpaceName,
				title: sourceFolderName,
			});

			await createFile(apiHelpers, {
				objectEntryFolderExternalReferenceCode:
					sourceFolder.externalReferenceCode,
				scopeKey: sourceSpaceName,
				title: fileTitle,
			});
		});

		await test.step('Copy the source folder to the destination folder in the destination Space', async () => {
			await assetsPage.gotoSpaceFiles(sourceSpaceName);

			await assetsPage.copyTo({
				destinationFolder: destinationFolderName,
				destinationSpace: destinationSpaceName,
				itemTitle: sourceFolderName,
			});
		});

		await test.step('Info alert for the folder copy is displayed', async () => {
			await waitForAlert(
				page,
				`Info:Copying ${sourceFolderName} to ${destinationFolderName}.`,
				{type: 'info'}
			);
		});

		await test.step('Success alert for the folder copy is displayed', async () => {
			await waitForAlert(
				page,
				`Success:${sourceFolderName} was successfully copied to ${destinationFolderName}.`,
				{first: true}
			);
		});

		await test.step('The file is now in the destination Space', async () => {
			const response =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
					applicationName,
					encodeURIComponent(destinationSpaceName),
					new URLSearchParams({pageSize: '100'})
				);

			const copiedItems = response.items.filter(
				(item: {title: string}) => item.title === fileTitle
			);

			expect(copiedItems).toHaveLength(1);
		});

		await test.step('The original file is still in the source Space', async () => {
			const response =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
					applicationName,
					encodeURIComponent(sourceSpaceName),
					new URLSearchParams({pageSize: '100'})
				);

			const sourceItems = response.items.filter(
				(item: {title: string}) => item.title === fileTitle
			);

			expect(sourceItems).toHaveLength(1);
		});
	}
);

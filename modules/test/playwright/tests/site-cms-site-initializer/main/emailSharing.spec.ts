/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

test(
	'External email collaborator is added as a guest with VIEW-only access',
	{tag: '@LPD-85836'},
	async ({apiHelpers, assetsPage, page, shareModalPage}) => {
		const spaceName = `Space ${getRandomString()}`;

		await test.step('Create a new space', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				type: 'Space',
			});
		});

		const objectEntryTitle = `Content ${getRandomString()}`;

		const objectEntry =
			await test.step('Create a content in the space', async () => {
				return await apiHelpers.objectEntry.postObjectEntry(
					{
						objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
						title: objectEntryTitle,
					},
					'cms/basic-web-contents',
					spaceName
				);
			});

		const emailAddress = `external-${getRandomString()}@liferay.com`;

		await test.step('Open the Share modal on the content', async () => {
			await assetsPage.gotoContents(spaceName);

			await assetsPage.execItemAction({
				action: 'Share',
				filter: objectEntryTitle,
			});

			await expect(
				shareModalPage.getHeader(objectEntryTitle)
			).toBeVisible();
		});

		await test.step('Type the external email and pick the invite option', async () => {
			await shareModalPage.typeInCollaboratorInput(emailAddress);

			await expect(shareModalPage.inviteExternalUserOption).toBeVisible();

			await shareModalPage.inviteExternalUserOption.click();
		});

		await test.step('Verify the guest chip is rendered with VIEW-only access', async () => {
			await expect(
				page.getByText(emailAddress, {exact: false})
			).toBeVisible();

			await expect(page.getByText('(guest)')).toBeVisible();

			await expect(page.getByText('To Be Shared')).toBeVisible();
		});

		await test.step('Submit the share modal', async () => {
			await shareModalPage.submit();

			await expect(
				shareModalPage.getHeader(objectEntryTitle)
			).not.toBeVisible();
		});

		await test.step('Verify the collaborator is persisted as type=Email', async () => {
			const collaborators =
				await apiHelpers.objectEntry.getObjectEntryCollaboratorsPage(
					'cms/basic-web-contents',
					objectEntry.id
				);

			expect(collaborators).toHaveLength(1);
			expect(collaborators[0]).toMatchObject({
				actionIds: ['VIEW'],
				emailAddress,
				type: 'Email',
			});
		});

		// Deleting the object entry deletes its sharing entries but never the
		// invitation ticket, so the collaborator list has to be emptied here.

		await test.step('Remove the invitation', async () => {
			await apiHelpers.objectEntry.postObjectEntryCollaborators(
				[],
				'cms/basic-web-contents',
				objectEntry.id
			);
		});
	}
);

test(
	'Email matching an existing user resolves to a User collaborator instead of a guest',
	{tag: '@LPD-85836'},
	async ({apiHelpers, assetsPage, page, shareModalPage}) => {
		const spaceName = `Space ${getRandomString()}`;

		await test.step('Create a new space', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				type: 'Space',
			});
		});

		const objectEntryTitle = `Content ${getRandomString()}`;

		await test.step('Create a content in the space', async () => {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: objectEntryTitle,
				},
				'cms/basic-web-contents',
				spaceName
			);
		});

		const user =
			await test.step('Create a user that already exists in the system', async () => {
				return await apiHelpers.headlessAdminUser.postUserAccount();
			});

		await test.step('Open the Share modal on the content', async () => {
			await assetsPage.gotoContents(spaceName);

			await assetsPage.execItemAction({
				action: 'Share',
				filter: objectEntryTitle,
			});

			await expect(
				shareModalPage.getHeader(objectEntryTitle)
			).toBeVisible();
		});

		await test.step('Type the existing user email', async () => {
			await shareModalPage.typeInCollaboratorInput(user.emailAddress);
		});

		await test.step('Verify the autocomplete offers the User option, not the Invite external user option', async () => {
			await expect(
				page.getByText(user.name, {exact: false})
			).toBeVisible();

			await expect(
				shareModalPage.inviteExternalUserOption
			).not.toBeVisible();
		});
	}
);

test(
	'Folders cannot be shared with an external email',
	{tag: '@LPD-85836'},
	async ({apiHelpers, assetsPage, shareModalPage}) => {
		const spaceName = `Space ${getRandomString()}`;

		const space = await test.step('Create a new space', async () => {
			return await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				type: 'Space',
			});
		});

		const folderName = `Folder ${getRandomString()}`;

		await test.step('Create a folder in the space', async () => {
			await apiHelpers.objectFolder.createObjectEntryFolder({
				scopeKey: String(space.siteId),
				title: folderName,
			});
		});

		await test.step('Open the Share modal on the folder', async () => {
			await assetsPage.gotoSpaceFiles(spaceName);

			await assetsPage.execItemAction({
				action: 'Share',
				filter: folderName,
			});

			await expect(shareModalPage.getHeader(folderName)).toBeVisible();
		});

		await test.step('Type a valid external email and verify the invite option is not offered', async () => {
			await shareModalPage.typeInCollaboratorInput(
				`external-${getRandomString()}@liferay.com`
			);

			await expect(
				shareModalPage.inviteExternalUserOption
			).not.toBeVisible();
		});
	}
);

test(
	'An invited external collaborator can be removed from the Share modal',
	{tag: '@LPD-103688'},
	async ({apiHelpers, assetsPage, page, shareModalPage}) => {
		const spaceName = `Space ${getRandomString()}`;

		await test.step('Create a new space', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				type: 'Space',
			});
		});

		const objectEntryTitle = `Content ${getRandomString()}`;

		const objectEntry =
			await test.step('Create a content in the space', async () => {
				return await apiHelpers.objectEntry.postObjectEntry(
					{
						objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
						title: objectEntryTitle,
					},
					'cms/basic-web-contents',
					spaceName
				);
			});

		const emailAddress = `external-${getRandomString()}@liferay.com`;

		await test.step('Invite the external user', async () => {
			await apiHelpers.objectEntry.postObjectEntryCollaborators(
				[
					{
						actionIds: ['VIEW'],
						emailAddress,
						share: false,
						type: 'Email',
					},
				],
				'cms/basic-web-contents',
				objectEntry.id
			);
		});

		await test.step('Open the Share modal on the content', async () => {
			await assetsPage.gotoContents(spaceName);

			await assetsPage.execItemAction({
				action: 'Share',
				filter: objectEntryTitle,
			});

			await expect(
				shareModalPage.getHeader(objectEntryTitle)
			).toBeVisible();
		});

		await test.step('Verify the invited guest is listed', async () => {
			await expect(
				shareModalPage.collaborator(emailAddress)
			).toBeVisible();

			await expect(
				page.getByText('Invited', {exact: true})
			).toBeVisible();
		});

		await test.step('Remove the access and submit', async () => {
			await shareModalPage.removeAccess(emailAddress);

			await expect(
				shareModalPage.collaborator(emailAddress)
			).not.toBeVisible();

			await shareModalPage.submit();

			await expect(
				shareModalPage.getHeader(objectEntryTitle)
			).not.toBeVisible();
		});

		await test.step('Verify the invitation is gone from the server', async () => {
			const collaborators =
				await apiHelpers.objectEntry.getObjectEntryCollaboratorsPage(
					'cms/basic-web-contents',
					objectEntry.id
				);

			expect(collaborators).toHaveLength(0);
		});
	}
);

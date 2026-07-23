/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch, userData} from '../../../utils/performLogin';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

async function expectViewModalToBeReadOnly(
	page: Page,
	assetRow: Locator,
	objectEntryTitle: string
) {
	const previewFrame = page.frameLocator('iframe[title="Preview"]');

	const titleInput = previewFrame.getByRole('textbox', {
		name: /Title \(Read Only\)/,
	});

	const quickViewButton = assetRow
		.locator('button:has(.lexicon-icon-view), a:has(.lexicon-icon-view)')
		.first();

	const actionsButton = assetRow.getByRole('button', {name: 'Actions'});

	const viewMenuItem = page.getByRole('menuitem', {
		exact: true,
		name: 'View',
	});

	await expect(async () => {
		await assetRow.hover();

		if (await quickViewButton.isVisible()) {
			await quickViewButton.click({timeout: 1000});
		}
		else {
			await actionsButton.click({timeout: 1000});

			await viewMenuItem.click({timeout: 1000});
		}

		await expect(titleInput).toHaveValue(objectEntryTitle, {timeout: 5000});
	}).toPass();

	await expect(titleInput).toBeDisabled();

	await expect(previewFrame.locator('fieldset[disabled]')).toHaveCount(1);

	await page.getByRole('button', {exact: true, name: 'Close'}).click();
}

test(
	'Shared entries show the same read-only state in the View modal regardless of permissions',
	{tag: '@LPD-89997'},
	async ({apiHelpers, page, sharedWithMePage}) => {
		const memberSpaceName = `Space ${getRandomString()}`;
		let memberSpace = null;
		const nonMemberSpaceName = `Space ${getRandomString()}`;

		await test.step('Create two spaces', async () => {
			memberSpace =
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name: memberSpaceName,
					type: 'Space',
				});

			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: nonMemberSpaceName,
				type: 'Space',
			});
		});

		const editableObjectEntryTitle = `Content ${getRandomString()}`;
		let editableObjectEntry = null;
		const viewOnlyObjectEntryTitle = `Content ${getRandomString()}`;
		let viewOnlyObjectEntry = null;

		await test.step('Create a content in each space', async () => {
			editableObjectEntry = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: editableObjectEntryTitle,
				},
				'cms/basic-web-contents',
				memberSpaceName
			);

			viewOnlyObjectEntry = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: viewOnlyObjectEntryTitle,
				},
				'cms/basic-web-contents',
				nonMemberSpaceName
			);
		});

		let user = null;

		await test.step('Create a user and add as member of the first space', async () => {
			user = await apiHelpers.headlessAdminUser.postUserAccount();

			userData[user.alternateName] = {
				name: user.givenName,
				password: 'test',
				surname: user.familyName,
			};

			await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(user.id);
			await apiHelpers.jsonWebServicesUser.answerReminderQuery(user.id);

			await apiHelpers.jsonWebServicesUser.addGroupUsers(
				memberSpace.siteId,
				[user.id]
			);
		});

		await test.step('Share the content from the member space with UPDATE permission and the content from the non-member space with VIEW permission', async () => {
			await apiHelpers.objectEntry.postObjectEntryCollaborators(
				[
					{
						actionIds: ['UPDATE', 'VIEW'],
						id: user.id,
						share: false,
						type: 'User',
					},
				],
				'cms/basic-web-contents',
				editableObjectEntry.id
			);

			await apiHelpers.objectEntry.postObjectEntryCollaborators(
				[
					{
						actionIds: ['VIEW'],
						id: user.id,
						share: false,
						type: 'User',
					},
				],
				'cms/basic-web-contents',
				viewOnlyObjectEntry.id
			);
		});

		await test.step('Switch to the user', async () => {
			await performUserSwitch(page, user.alternateName);

			await sharedWithMePage.goto();
		});

		for (const objectEntryTitle of [
			editableObjectEntryTitle,
			viewOnlyObjectEntryTitle,
		]) {
			await test.step(`Verify ${objectEntryTitle} renders read-only inputs in the View modal`, async () => {
				const assetRow = page.getByRole('row', {
					name: objectEntryTitle,
				});

				await expect(assetRow).toBeVisible();

				await expectViewModalToBeReadOnly(
					page,
					assetRow,
					objectEntryTitle
				);
			});
		}
	}
);

test(
	'Shared entries show status "Not Visible" when assets are sent to the Recycle Bin',
	{tag: '@LPD-83882'},
	async ({apiHelpers, page, sharedWithMePage}) => {
		const spaceName = `Space ${getRandomString()}`;
		let space = null;

		await test.step('Create a new space with recycle bin enabled', async () => {
			space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				settings: {
					trashEnabled: true,
				},
				type: 'Space',
			});
		});

		const objectEntryTitle = `Content ${getRandomString()}`;
		let objectEntry = null;

		await test.step('Create a content', async () => {
			objectEntry = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: objectEntryTitle,
				},
				'cms/basic-web-contents',
				spaceName
			);
		});

		const objectEntryFolderTitle = `Folder ${getRandomString()}`;
		let objectEntryFolder = null;

		await test.step('Create a folder', async () => {
			objectEntryFolder =
				await apiHelpers.objectFolder.createObjectEntryFolder({
					parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					scopeKey: spaceName,
					title: objectEntryFolderTitle,
				});
		});

		let user = null;

		await test.step('Create a user', async () => {
			user = await apiHelpers.headlessAdminUser.postUserAccount();

			userData[user.alternateName] = {
				name: user.givenName,
				password: 'test',
				surname: user.familyName,
			};

			await apiHelpers.jsonWebServicesUser.addGroupUsers(space.siteId, [
				user.id,
			]);
		});

		await test.step('Share the content to the user', async () => {
			await apiHelpers.objectEntry.postObjectEntryCollaborators(
				[
					{
						actionIds: ['DOWNLOAD', 'VIEW'],
						id: user.id,
						share: false,
						type: 'User',
					},
				],
				'cms/basic-web-contents',
				objectEntry.id
			);
		});

		await test.step('Share the folder to the user', async () => {
			await apiHelpers.objectFolder.postObjectEntryFolderCollaborators(
				[
					{
						actionIds: ['VIEW'],
						id: user.id,
						share: false,
						type: 'User',
					},
				],
				objectEntryFolder.id
			);
		});

		await test.step('Verify that the user can see the shared assets', async () => {
			await performUserSwitch(page, user.alternateName);

			await sharedWithMePage.expectAssetEntryToBeVisible(
				objectEntryTitle
			);
			await sharedWithMePage.expectAssetEntryToBeVisible(
				objectEntryFolderTitle
			);
		});

		await test.step('Delete the content and the folder so they go into the Recycle Bin', async () => {
			await performUserSwitch(page, 'test');

			await expect(
				await apiHelpers.objectEntry.deleteObjectEntry(
					'cms/basic-web-contents',
					objectEntry.id
				)
			).toBeOK();

			await expect(
				await apiHelpers.objectFolder.deleteObjectEntryFolder(
					objectEntryFolder.id
				)
			).toBeOK();
		});

		await test.step('Verify that the user can see deleted shared assets as "Not Visible"', async () => {
			await performUserSwitch(page, user.alternateName);

			await sharedWithMePage.expectAssetEntryNotToBeVisible(
				objectEntryTitle
			);
			await sharedWithMePage.expectAssetEntryNotToBeVisible(
				objectEntryFolderTitle
			);
		});
	}
);

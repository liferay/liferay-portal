/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitchViaApi, userData} from '../../../utils/performLogin';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

async function openShareModal(
	page: Page,
	assetRow: Locator,
	objectEntryTitle: string
) {
	const actionsButton = assetRow.getByRole('button', {name: 'Actions'});

	const shareMenuItem = page.getByRole('menuitem', {
		exact: true,
		name: 'Share',
	});

	const shareModalTitle = page.getByText(`Share "${objectEntryTitle}"`);

	await expect(async () => {
		if (!(await shareMenuItem.isVisible())) {
			await actionsButton.click({timeout: 1000});

			await expect(shareMenuItem).toBeVisible({timeout: 2000});
		}

		await shareMenuItem.click({timeout: 1000});

		await expect(shareModalTitle).toBeVisible({timeout: 5000});
	}).toPass();
}

test(
	'User with Reshare permission should not see Allow Resharing or Remove Access actions',
	{tag: '@LPD-86111'},
	async ({apiHelpers, page, sharedWithMePage}) => {
		const spaceName = `Space ${getRandomString()}`;
		let space = null;

		await test.step('Create a new space', async () => {
			space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				type: 'Space',
			});
		});

		const objectEntryTitle = `Content ${getRandomString()}`;
		let objectEntry = null;

		await test.step('Create a content in the space', async () => {
			objectEntry = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: objectEntryTitle,
				},
				'cms/basic-web-contents',
				spaceName
			);
		});

		let user = null;

		await test.step('Create a user and add as space member', async () => {
			user = await apiHelpers.headlessAdminUser.postUserAccount();

			userData[user.alternateName] = {
				name: user.givenName,
				password: 'test',
				surname: user.familyName,
			};

			await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(user.id);
			await apiHelpers.jsonWebServicesUser.answerReminderQuery(user.id);

			await apiHelpers.jsonWebServicesUser.addGroupUsers(space.siteId, [
				user.id,
			]);
		});

		await test.step('Share the content with the user with VIEW + Reshare enabled', async () => {
			await apiHelpers.objectEntry.postObjectEntryCollaborators(
				[
					{
						actionIds: ['VIEW'],
						id: user.id,
						share: true,
						type: 'User',
					},
				],
				'cms/basic-web-contents',
				objectEntry.id
			);
		});

		await test.step('Switch to the user and open the Share modal', async () => {
			await performUserSwitchViaApi(page, user.alternateName);

			await sharedWithMePage.goto();

			const assetRow = page.getByRole('row', {name: objectEntryTitle});

			await expect(assetRow).toBeVisible();

			await openShareModal(page, assetRow, objectEntryTitle);
		});

		await test.step('Verify Allow Resharing and Remove Access are not available', async () => {
			await expect(page.getByLabel('More Options')).not.toBeVisible();

			await expect(page.getByLabel('Allow Resharing')).not.toBeVisible();

			await expect(page.getByLabel('Remove Access')).not.toBeVisible();
		});

		await test.step('Verify the user can still add new collaborators', async () => {
			await expect(
				page.getByText('Add People to Collaborate')
			).toBeVisible();
		});
	}
);

test(
	'User with Reshare and Update permission should see Allow Resharing and Remove Access actions',
	{tag: '@LPD-86111'},
	async ({apiHelpers, page, sharedWithMePage}) => {
		const spaceName = `Space ${getRandomString()}`;
		let space = null;

		await test.step('Create a new space', async () => {
			space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				type: 'Space',
			});
		});

		const objectEntryTitle = `Content ${getRandomString()}`;
		let objectEntry = null;

		await test.step('Create a content in the space', async () => {
			objectEntry = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: objectEntryTitle,
				},
				'cms/basic-web-contents',
				spaceName
			);
		});

		let user = null;

		await test.step('Create a user and add as space member', async () => {
			user = await apiHelpers.headlessAdminUser.postUserAccount();

			userData[user.alternateName] = {
				name: user.givenName,
				password: 'test',
				surname: user.familyName,
			};

			await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(user.id);
			await apiHelpers.jsonWebServicesUser.answerReminderQuery(user.id);

			await apiHelpers.jsonWebServicesUser.addGroupUsers(space.siteId, [
				user.id,
			]);
		});

		await test.step('Share the content with the user with VIEW + UPDATE + Reshare enabled', async () => {
			await apiHelpers.objectEntry.postObjectEntryCollaborators(
				[
					{
						actionIds: ['UPDATE', 'VIEW'],
						id: user.id,
						share: true,
						type: 'User',
					},
				],
				'cms/basic-web-contents',
				objectEntry.id
			);
		});

		await test.step('Switch to the user and open the Share modal', async () => {
			await performUserSwitchViaApi(page, user.alternateName);

			await sharedWithMePage.goto();

			const assetRow = page.getByRole('row', {name: objectEntryTitle});

			await expect(assetRow).toBeVisible();

			await openShareModal(page, assetRow, objectEntryTitle);
		});

		await test.step('Verify Allow Resharing and Remove Access are available', async () => {
			const moreOptionsButton = page.getByLabel('More Options');

			await expect(moreOptionsButton).toBeVisible();

			await moreOptionsButton.click();

			await expect(page.getByLabel('Allow Resharing')).toBeVisible();

			await expect(page.getByLabel('Remove Access')).toBeVisible();
		});
	}
);

test(
	'Removing access stops the collaborator from seeing the shared content',
	{tag: '@LPD-102734'},
	async ({apiHelpers, assetsPage, page, sharedWithMePage}) => {
		const spaceName = `Space ${getRandomString()}`;
		let space = null;

		await test.step('Create a new space', async () => {
			space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				type: 'Space',
			});
		});

		const objectEntryTitle = `Content ${getRandomString()}`;
		let objectEntry = null;

		await test.step('Create a content in the space', async () => {
			objectEntry = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: objectEntryTitle,
				},
				'cms/basic-web-contents',
				spaceName
			);
		});

		let user = null;

		await test.step('Create a user and add as space member', async () => {
			user = await apiHelpers.headlessAdminUser.postUserAccount();

			userData[user.alternateName] = {
				name: user.givenName,
				password: 'test',
				surname: user.familyName,
			};

			await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(user.id);
			await apiHelpers.jsonWebServicesUser.answerReminderQuery(user.id);

			await apiHelpers.jsonWebServicesUser.addGroupUsers(space.siteId, [
				user.id,
			]);
		});

		await test.step('Share the content with the user', async () => {
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
				objectEntry.id
			);
		});

		const assetRow = page.getByRole('row', {name: objectEntryTitle});

		await test.step('Verify the user sees the shared content', async () => {
			await performUserSwitchViaApi(page, user.alternateName);

			await sharedWithMePage.goto();

			await expect(assetRow).toBeVisible();
		});

		await test.step('Remove the access as the administrator', async () => {
			await performUserSwitchViaApi(page, 'test');

			await assetsPage.gotoSpaceContents(spaceName);

			await openShareModal(page, assetRow, objectEntryTitle);

			await page.getByLabel('More Options').click();

			await page.getByLabel('Remove Access').click();

			await page
				.getByRole('button', {exact: true, name: 'Share'})
				.click();
		});

		await test.step('Verify the user no longer sees the content', async () => {
			await performUserSwitchViaApi(page, user.alternateName);

			await sharedWithMePage.goto();

			await expect(assetRow).toBeHidden();
		});
	}
);

test(
	'The Share action is offered only to a collaborator allowed to reshare',
	{tag: '@LPD-102734'},
	async ({apiHelpers, page, sharedWithMePage}) => {
		const spaceName = `Space ${getRandomString()}`;
		let space = null;

		await test.step('Create a new space', async () => {
			space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				type: 'Space',
			});
		});

		const resharableTitle = `Content ${getRandomString()}`;
		const viewOnlyTitle = `Content ${getRandomString()}`;
		let resharableObjectEntry = null;
		let viewOnlyObjectEntry = null;

		await test.step('Create two contents in the space', async () => {
			resharableObjectEntry =
				await apiHelpers.objectEntry.postObjectEntry(
					{
						objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
						title: resharableTitle,
					},
					'cms/basic-web-contents',
					spaceName
				);

			viewOnlyObjectEntry = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: viewOnlyTitle,
				},
				'cms/basic-web-contents',
				spaceName
			);
		});

		let user = null;

		await test.step('Create a user and add as space member', async () => {
			user = await apiHelpers.headlessAdminUser.postUserAccount();

			userData[user.alternateName] = {
				name: user.givenName,
				password: 'test',
				surname: user.familyName,
			};

			await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(user.id);
			await apiHelpers.jsonWebServicesUser.answerReminderQuery(user.id);

			await apiHelpers.jsonWebServicesUser.addGroupUsers(space.siteId, [
				user.id,
			]);
		});

		await test.step('Share both contents, only one of them resharable', async () => {
			await apiHelpers.objectEntry.postObjectEntryCollaborators(
				[
					{
						actionIds: ['VIEW'],
						id: user.id,
						share: true,
						type: 'User',
					},
				],
				'cms/basic-web-contents',
				resharableObjectEntry.id
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

		await test.step('Verify only the resharable content offers the Share action', async () => {
			await performUserSwitchViaApi(page, user.alternateName);

			await sharedWithMePage.goto();

			const resharableRow = page.getByRole('row', {
				name: resharableTitle,
			});
			const viewOnlyRow = page.getByRole('row', {name: viewOnlyTitle});

			await expect(resharableRow).toBeVisible();
			await expect(viewOnlyRow).toBeVisible();

			// The view-only row carries no Actions button at all, since
			// resharing is the only action a view-only collaborator has.

			await expect(
				viewOnlyRow.getByRole('button', {name: 'Actions'})
			).toHaveCount(0);

			await openShareModal(page, resharableRow, resharableTitle);
		});
	}
);

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
			await performUserSwitch(page, user.alternateName);

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
			await performUserSwitch(page, user.alternateName);

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

/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {addCMSAdministrator} from '../../../utils/addCMSAdministrator';
import getRandomString from '../../../utils/getRandomString';
import {
	performLoginViaApi,
	performUserSwitchViaApi,
	userData,
} from '../../../utils/performLogin';
import {SITE_CMS_SPACE_EXTERNAL_REFERENCE_CODE} from '../../setup/site-cms-site/constants/space';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

const spaceName = 'Default';

async function clearFirstSignInWalls(
	apiHelpers: DataApiHelpers,
	user: TUserAccount
) {
	await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(user.id);
	await apiHelpers.jsonWebServicesUser.answerReminderQuery(user.id);
}

let cmsAdminUser: TUserAccount;
let setupData: Array<{id: number | string; type: string}>;
let spaceAdminUser: TUserAccount;

test.beforeAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	cmsAdminUser = await addCMSAdministrator(apiHelpers);

	apiHelpers.data.push({id: cmsAdminUser.id, type: 'userAccount'});

	await clearFirstSignInWalls(apiHelpers, cmsAdminUser);

	const addRoleUser = async (role: string) => {
		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		apiHelpers.data.push({id: user.id, type: 'userAccount'});

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
			SITE_CMS_SPACE_EXTERNAL_REFERENCE_CODE,
			user.externalReferenceCode
		);

		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccountRoles(
			SITE_CMS_SPACE_EXTERNAL_REFERENCE_CODE,
			user.externalReferenceCode,
			[role]
		);

		await clearFirstSignInWalls(apiHelpers, user);

		return user;
	};

	spaceAdminUser = await addRoleUser('Asset Library Administrator');

	setupData = [...apiHelpers.data];

	await page.close();
});

test.afterAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	apiHelpers.setData(setupData);

	await apiHelpers.clearData();

	await page.close();
});

// The Space Member negative case lives in
// tests/e2e-cms-dxp/main/sharing/sharingPermissions.spec.ts (@LPD-95527/TC-4.i).
//
// A Space Content Reviewer is deliberately not covered here. Epic LPD-52006
// asks for it, but sharing is gated on owning the content or administering
// its scope. SharingPermissionImpl.containsSharePermission opens for the
// asset owner, for a sharing entry that allows resharing, and for
// DepotPermissionCheckerWrapper.isGroupAdmin, which accepts CMS Administrator
// and the Asset Library Administrator and Owner roles and not Asset Library
// Content Reviewer. So a Content Reviewer shares what they created and not
// what they review. Raised on LPD-85358.

test(
	'The Share action is available to a CMS Administrator and a Space Administrator',
	{tag: ['@LPD-103688', '@LPD-95527']},
	async ({apiHelpers, assetsPage, page, shareModalPage}) => {
		const contentTitle = `Content ${getRandomString()}`;

		await test.step('Create a content for the Space', async () => {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: contentTitle,
				},
				'cms/basic-web-contents',
				spaceName
			);
		});

		const expectShareActionAvailable = async (screenName: string) => {
			await performUserSwitchViaApi(page, screenName);

			await assetsPage.gotoSpaceContents(spaceName);

			await assetsPage.execItemAction({
				action: 'Share',
				filter: contentTitle,
			});

			await expect(shareModalPage.getHeader(contentTitle)).toBeVisible();

			await page.keyboard.press('Escape');

			await expect(
				shareModalPage.getHeader(contentTitle)
			).not.toBeVisible();
		};

		await test.step('A CMS Administrator can open the Share modal', async () => {
			await expectShareActionAvailable(cmsAdminUser.alternateName);
		});

		await test.step('A Space Administrator can open the Share modal', async () => {
			await expectShareActionAvailable(spaceAdminUser.alternateName);
		});
	}
);

test(
	'A Space Administrator can invite an external user by email',
	{tag: ['@LPD-103688', '@LPD-85836']},
	async ({apiHelpers, assetsPage, page, shareModalPage}) => {
		const contentTitle = `Content ${getRandomString()}`;
		const emailAddress = `external-${getRandomString()}@liferay.com`;

		const objectEntry =
			await test.step('Create a content for the Space', async () => {
				return await apiHelpers.objectEntry.postObjectEntry(
					{
						objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
						title: contentTitle,
					},
					'cms/basic-web-contents',
					spaceName
				);
			});

		await test.step('Open the Share modal as a Space Administrator', async () => {
			await performUserSwitchViaApi(page, spaceAdminUser.alternateName);

			await assetsPage.gotoSpaceContents(spaceName);

			await assetsPage.execItemAction({
				action: 'Share',
				filter: contentTitle,
			});

			await expect(shareModalPage.getHeader(contentTitle)).toBeVisible();
		});

		await test.step('Invite the external user and submit', async () => {
			await shareModalPage.typeInCollaboratorInput(emailAddress);

			await expect(shareModalPage.inviteExternalUserOption).toBeVisible();

			await shareModalPage.inviteExternalUserOption.click();

			await shareModalPage.submit();

			await expect(
				shareModalPage.getHeader(contentTitle)
			).not.toBeVisible();
		});

		await test.step('Verify the invitation reached the server', async () => {
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
			await performUserSwitchViaApi(page, 'test');

			await apiHelpers.objectEntry.postObjectEntryCollaborators(
				[],
				'cms/basic-web-contents',
				objectEntry.id
			);
		});
	}
);

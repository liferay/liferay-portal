/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {addCMSAdministrator} from '../../../utils/addCMSAdministrator';
import getRandomString from '../../../utils/getRandomString';
import {
	performLoginViaApi,
	performUserSwitch,
	userData,
} from '../../../utils/performLogin';
import {SITE_CMS_SPACE_EXTERNAL_REFERENCE_CODE} from '../../setup/site-cms-site/constants/space';
import {AssetsPage} from '../main/pages/AssetsPage';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
	}),
	loginTest()
);

const ownedSpaceName = 'Default';

let cmsAdminUser: TUserAccount;
let otherSpaceName: string;
let setupData: Array<{id: number | string; type: string}>;
let spaceAdminUser: TUserAccount;
let spaceContentReviewerUser: TUserAccount;
let spaceMemberUser: TUserAccount;

test.beforeAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	cmsAdminUser = await addCMSAdministrator(apiHelpers);

	apiHelpers.data.push({id: cmsAdminUser.id, type: 'userAccount'});

	const addRoleUser = async (role?: string) => {
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

		if (role) {
			await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccountRoles(
				SITE_CMS_SPACE_EXTERNAL_REFERENCE_CODE,
				user.externalReferenceCode,
				[role]
			);
		}

		return user;
	};

	spaceAdminUser = await addRoleUser('Asset Library Administrator');
	spaceContentReviewerUser = await addRoleUser(
		'Asset Library Content Reviewer'
	);
	spaceMemberUser = await addRoleUser();

	const otherSpace = await apiHelpers.headlessAssetLibrary.createAssetLibrary(
		{
			name: `Space ${getRandomString()}`,
			settings: {},
			type: 'Space',
		}
	);

	otherSpaceName = otherSpace.name;

	for (const user of [spaceAdminUser, spaceContentReviewerUser]) {
		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
			otherSpace.externalReferenceCode,
			user.externalReferenceCode
		);
	}

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

async function expectConfirmationModalInSpaceContents(
	assetsPage: AssetsPage,
	spaceName: string,
	title: string,
	action: string
) {
	await assetsPage.gotoSpaceContents(spaceName);

	await expect(assetsPage.getItem(title)).toBeVisible();

	await assetsPage.selectItems([title]);

	await assetsPage.execBulkItemAction(action);

	await expect(
		assetsPage.modal.footer.getByRole('button', {exact: true, name: action})
	).toBeVisible();

	await assetsPage.modal.footer
		.getByRole('button', {exact: true, name: 'Cancel'})
		.click();

	await expect(assetsPage.modal.container).toBeHidden();
}

async function expectBulkActionUnavailable(
	assetsPage: AssetsPage,
	page: Page,
	spaceName: string,
	title: string,
	action: string
) {
	await assetsPage.gotoSpaceContents(spaceName);

	await expect(assetsPage.getItem(title)).toBeVisible();

	await assetsPage.selectItems([title]);

	await assetsPage.expectBulkItemActionHidden(action);

	await page.keyboard.press('Escape');
}

test(
	'Duplicate bulk action confirmation depends on user role and space',
	{tag: '@LPD-90340'},
	async ({apiHelpers, assetsPage, page}) => {
		const ownedContentTitle = `Content ${getRandomString()}`;
		const otherContentTitle = `Content ${getRandomString()}`;

		await test.step('Create contents in both spaces', async () => {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: ownedContentTitle,
				},
				'cms/basic-web-contents',
				ownedSpaceName
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: otherContentTitle,
				},
				'cms/basic-web-contents',
				otherSpaceName
			);
		});

		await test.step('CMS Administrator sees the confirmation across all spaces', async () => {
			await performUserSwitch(page, cmsAdminUser.alternateName);

			await expectConfirmationModalInSpaceContents(
				assetsPage,
				ownedSpaceName,
				ownedContentTitle,
				'Duplicate'
			);

			await expectConfirmationModalInSpaceContents(
				assetsPage,
				otherSpaceName,
				otherContentTitle,
				'Duplicate'
			);
		});

		await test.step('Space Administrator sees the confirmation only within owned spaces', async () => {
			await performUserSwitch(page, spaceAdminUser.alternateName);

			await expectConfirmationModalInSpaceContents(
				assetsPage,
				ownedSpaceName,
				ownedContentTitle,
				'Duplicate'
			);

			await expectBulkActionUnavailable(
				assetsPage,
				page,
				otherSpaceName,
				otherContentTitle,
				'Duplicate'
			);
		});

		await test.step('Space Content Reviewer sees the confirmation only within its space', async () => {
			await performUserSwitch(
				page,
				spaceContentReviewerUser.alternateName
			);

			await expectConfirmationModalInSpaceContents(
				assetsPage,
				ownedSpaceName,
				ownedContentTitle,
				'Duplicate'
			);

			await expectBulkActionUnavailable(
				assetsPage,
				page,
				otherSpaceName,
				otherContentTitle,
				'Duplicate'
			);
		});

		await test.step('Space Member does not see the Duplicate bulk action', async () => {
			await performUserSwitch(page, spaceMemberUser.alternateName);

			await expectBulkActionUnavailable(
				assetsPage,
				page,
				ownedSpaceName,
				ownedContentTitle,
				'Duplicate'
			);
		});
	}
);

test(
	'Expire bulk action confirmation depends on user role and space',
	{tag: '@LPD-90340'},
	async ({apiHelpers, assetsPage, page}) => {
		const ownedContentTitle = `Content ${getRandomString()}`;
		const otherContentTitle = `Content ${getRandomString()}`;

		await test.step('Create approved contents in both spaces', async () => {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: ownedContentTitle,
				},
				'cms/basic-web-contents',
				ownedSpaceName
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: otherContentTitle,
				},
				'cms/basic-web-contents',
				otherSpaceName
			);
		});

		await test.step('CMS Administrator sees the confirmation across all spaces', async () => {
			await performUserSwitch(page, cmsAdminUser.alternateName);

			await expectConfirmationModalInSpaceContents(
				assetsPage,
				ownedSpaceName,
				ownedContentTitle,
				'Expire'
			);

			await expectConfirmationModalInSpaceContents(
				assetsPage,
				otherSpaceName,
				otherContentTitle,
				'Expire'
			);
		});

		await test.step('Space Administrator sees the confirmation only within owned spaces', async () => {
			await performUserSwitch(page, spaceAdminUser.alternateName);

			await expectConfirmationModalInSpaceContents(
				assetsPage,
				ownedSpaceName,
				ownedContentTitle,
				'Expire'
			);

			await expectBulkActionUnavailable(
				assetsPage,
				page,
				otherSpaceName,
				otherContentTitle,
				'Expire'
			);
		});

		await test.step('Space Content Reviewer sees the confirmation only within its space', async () => {
			await performUserSwitch(
				page,
				spaceContentReviewerUser.alternateName
			);

			await expectConfirmationModalInSpaceContents(
				assetsPage,
				ownedSpaceName,
				ownedContentTitle,
				'Expire'
			);

			await expectBulkActionUnavailable(
				assetsPage,
				page,
				otherSpaceName,
				otherContentTitle,
				'Expire'
			);
		});

		await test.step('Space Member does not see the Expire bulk action', async () => {
			await performUserSwitch(page, spaceMemberUser.alternateName);

			await expectBulkActionUnavailable(
				assetsPage,
				page,
				ownedSpaceName,
				ownedContentTitle,
				'Expire'
			);
		});
	}
);

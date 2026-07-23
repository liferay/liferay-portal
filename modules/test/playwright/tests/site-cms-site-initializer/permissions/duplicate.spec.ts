/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

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
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
	}),
	loginTest()
);

const spaceName = 'Default';

let cmsAdminUser: TUserAccount;
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

test(
	'Duplicate visibility depends on user role',
	{tag: '@LPD-88346'},
	async ({apiHelpers, assetsPage, page}) => {
		const fileTitle = `Content ${getRandomString()}`;

		await test.step('Create a content for the Space', async () => {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: fileTitle,
				},
				'cms/basic-web-contents',
				spaceName
			);
		});

		const duplicateAndVerify = async (expectedSuffixedTitle: string) => {
			await assetsPage.gotoSpaceContents(spaceName);

			await assetsPage.execItemAction({
				action: 'Duplicate',
				filter: fileTitle,
				parentAction: 'Copy',
			});

			await expect(
				page.getByRole('link', {
					exact: true,
					name: expectedSuffixedTitle,
				})
			).toBeVisible();
		};

		await test.step('CMS Administrator can duplicate the content', async () => {
			await performUserSwitch(page, cmsAdminUser.alternateName);

			await duplicateAndVerify(`${fileTitle} (Copy)`);
		});

		await test.step('Space Administrator can duplicate the content', async () => {
			await performUserSwitch(page, spaceAdminUser.alternateName);

			await duplicateAndVerify(`${fileTitle} (Copy 1)`);
		});

		await test.step('Space Content Reviewer can duplicate the content', async () => {
			await performUserSwitch(
				page,
				spaceContentReviewerUser.alternateName
			);

			await duplicateAndVerify(`${fileTitle} (Copy 2)`);
		});

		await test.step('Space Member does not see Duplicate', async () => {
			await performUserSwitch(page, spaceMemberUser.alternateName);

			await assetsPage.gotoSpaceContents(spaceName);

			await assetsPage.table.bodyRows
				.filter({hasText: fileTitle})
				.getByRole('button', {name: `${fileTitle} Actions`})
				.click();

			await expect(
				page.getByRole('menuitem', {exact: true, name: 'Duplicate'})
			).toBeHidden();
		});
	}
);

test(
	'Folder Duplicate visibility depends on user role',
	{tag: '@LPD-88657'},
	async ({apiHelpers, assetsPage, page}) => {
		const folderName = `Folder ${getRandomString()}`;

		await test.step('Create a folder for the Space', async () => {
			await apiHelpers.objectFolder.createObjectEntryFolder({
				parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				scopeKey: spaceName,
				title: folderName,
			});
		});

		const duplicateAndVerify = async (expectedSuffixedTitle: string) => {
			await assetsPage.gotoSpaceContents(spaceName);

			await assetsPage.execItemAction({
				action: 'Duplicate',
				filter: folderName,
				parentAction: 'Copy',
			});

			await expect(
				page.getByRole('link', {
					exact: true,
					name: expectedSuffixedTitle,
				})
			).toBeVisible();
		};

		await test.step('CMS Administrator can duplicate the folder', async () => {
			await performUserSwitch(page, cmsAdminUser.alternateName);

			await duplicateAndVerify(`${folderName} (Copy)`);
		});

		await test.step('Space Administrator can duplicate the folder', async () => {
			await performUserSwitch(page, spaceAdminUser.alternateName);

			await duplicateAndVerify(`${folderName} (Copy 1)`);
		});

		await test.step('Space Content Reviewer can duplicate the folder', async () => {
			await performUserSwitch(
				page,
				spaceContentReviewerUser.alternateName
			);

			await duplicateAndVerify(`${folderName} (Copy 2)`);
		});

		await test.step('Space Member does not see Duplicate', async () => {
			await performUserSwitch(page, spaceMemberUser.alternateName);

			await assetsPage.gotoSpaceContents(spaceName);

			await assetsPage.table.bodyRows
				.filter({hasText: folderName})
				.getByRole('button', {name: `${folderName} Actions`})
				.click();

			await expect(
				page.getByRole('menuitem', {exact: true, name: 'Duplicate'})
			).toBeHidden();
		});
	}
);

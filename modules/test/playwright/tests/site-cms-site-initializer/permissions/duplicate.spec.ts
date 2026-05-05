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
import {SpaceSummaryPage} from '../main/pages/SpaceSummaryPage';
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

let cmsAdminUser;
let setupData: Array<{id: number | string; type: string}>;
let spaceAdminUser;
let spaceContentReviewerUser;
let spaceMemberUser;
let spaceName: string;

test.beforeAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	spaceName = `Space ${getRandomString()}`;

	const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
		name: spaceName,
		settings: {},
		type: 'Space',
	});

	apiHelpers.data.push({id: space.id, type: 'assetLibrary'});

	cmsAdminUser = await addCMSAdministrator(apiHelpers);

	apiHelpers.data.push({id: cmsAdminUser.id, type: 'userAccount'});

	const spaceSummaryPage = new SpaceSummaryPage(page);

	await spaceSummaryPage.goto(spaceName);

	const addRoleUser = async (role?: string) => {
		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		apiHelpers.data.push({id: user.id, type: 'userAccount'});

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await spaceSummaryPage.addUserOrUserGroup(user.name, 'users');

		if (role) {
			await spaceSummaryPage.addRoleToSpaceMember(role, user.name);
		}

		return user;
	};

	spaceAdminUser = await addRoleUser('Space Administrator');
	spaceContentReviewerUser = await addRoleUser('Space Content Reviewer');
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

		const expectDuplicateHidden = async () => {
			await assetsPage.gotoSpaceContents(spaceName);

			await assetsPage.table.bodyRows
				.filter({hasText: fileTitle})
				.getByRole('button', {name: `${fileTitle} Actions`})
				.click();

			await expect(
				page.getByRole('menuitem', {exact: true, name: 'Duplicate'})
			).toBeHidden();
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

			await expectDuplicateHidden();
		});
	}
);

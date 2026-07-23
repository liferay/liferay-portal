/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {
	performUserSwitchViaApi,
	userData,
} from '../../../../utils/performLogin';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
	}),
	loginTest()
);

test(
	'Comment actions are restricted to users with permission over the content',
	{tag: ['@LPD-90003', '@LPD-93064']},
	async ({apiHelpers, contentsPage, page}) => {
		const spaceName = getRandomString();
		const commentBody = getRandomString();
		const contentTitle = 'Untitled Asset';

		const assetLibrary =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				settings: {},
				type: 'Space',
			});

		await contentsPage.goto();

		await contentsPage.createContent('Blog', spaceName);

		await contentsPage.openSidePanel('Comments');

		const editor = page.getByLabel('Add Comment.');

		await expect(editor).toBeVisible();

		await editor.scrollIntoViewIfNeeded();
		await editor.click();
		await page.keyboard.type(commentBody);

		await page
			.getByRole('button', {exact: true, name: 'Save'})
			.first()
			.click();

		const comment = page.locator('article').filter({hasText: commentBody});

		await expect(comment).toBeVisible();
		await expect(comment.getByTitle('actions')).toBeVisible();

		const spaceAdmin = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[spaceAdmin.alternateName] = {
			name: spaceAdmin.givenName,
			password: 'test',
			surname: spaceAdmin.familyName,
		};

		const spaceMember =
			await apiHelpers.headlessAdminUser.postUserAccount();

		userData[spaceMember.alternateName] = {
			name: spaceMember.givenName,
			password: 'test',
			surname: spaceMember.familyName,
		};

		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
			assetLibrary.externalReferenceCode,
			spaceAdmin.externalReferenceCode
		);
		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccountRoles(
			assetLibrary.externalReferenceCode,
			spaceAdmin.externalReferenceCode,
			['Asset Library Administrator']
		);
		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
			assetLibrary.externalReferenceCode,
			spaceMember.externalReferenceCode
		);

		const contentRow = page
			.locator('.fds table tbody tr')
			.filter({hasText: contentTitle});

		await test.step('Space Administrator can edit and delete the admin comment', async () => {
			await performUserSwitchViaApi(page, spaceAdmin.alternateName);

			await contentsPage.goto();

			await contentRow.getByRole('link', {name: contentTitle}).click();

			await contentsPage.openSidePanel('Comments');

			await expect(comment).toBeVisible();
			await expect(comment.getByTitle('actions')).toBeVisible();
		});

		await test.step('Space Member cannot edit or delete the admin comment', async () => {
			await performUserSwitchViaApi(page, spaceMember.alternateName);

			await page.goto(PORTLET_URLS.cmsContents);

			await contentRow.getByRole('checkbox').check();

			await page.getByRole('button', {name: 'Info'}).click();

			await page.getByRole('tab', {name: 'Comments'}).click();

			await expect(comment).toBeVisible();
			await expect(comment.getByTitle('actions')).toBeHidden();
		});
	}
);

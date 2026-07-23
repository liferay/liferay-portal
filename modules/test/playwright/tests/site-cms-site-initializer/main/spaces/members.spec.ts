/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import getRandomString from '../../../../utils/getRandomString';
import {
	performUserSwitchViaApi,
	userData,
} from '../../../../utils/performLogin';
import {
	SITE_CMS_SPACE_EXTERNAL_REFERENCE_CODE,
	SITE_CMS_SPACE_NAME,
} from '../../../setup/site-cms-site/constants/space';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';
import {SpaceSummaryPage} from '../pages/SpaceSummaryPage';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

type SpaceMemberRole = 'Space Administrator' | 'Space Content Reviewer';

async function promoteNewMember({
	apiHelpers,
	page,
	role,
	spaceSummaryPage,
}: {
	apiHelpers: DataApiHelpers;
	page: Page;
	role: SpaceMemberRole;
	spaceSummaryPage: SpaceSummaryPage;
}) {
	const user = await apiHelpers.headlessAdminUser.postUserAccount();
	const userFullName = `${user.givenName} ${user.familyName}`;

	await spaceSummaryPage.addUserOrUserGroup(userFullName, 'users');

	await spaceSummaryPage.addRoleToSpaceMember(role, userFullName);

	await spaceSummaryPage.viewAllMembersLink.click();

	const userRow = page.getByRole('listitem').filter({hasText: userFullName});

	await expect(userRow.getByRole('button', {name: role})).toBeVisible();

	await spaceSummaryPage.closeButton.click();
}

test(
	'A user can be added as a member of a space and removed afterwards',
	{tag: '@LPD-85670'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const spaceName = `Space ${getRandomString()}`;
		const user = await apiHelpers.headlessAdminUser.postUserAccount();
		const userFullName = `${user.givenName} ${user.familyName}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {},
			type: 'Space',
		});

		await spaceSummaryPage.goto(spaceName);

		await spaceSummaryPage.addUserOrUserGroup(userFullName, 'users');

		await spaceSummaryPage.viewAllMembersLink.click();

		await expect(
			page.getByRole('listitem').filter({hasText: userFullName})
		).toHaveCount(1);

		await spaceSummaryPage.closeMembersDialog();

		await spaceSummaryPage.removeUserOrUserGroup(userFullName, 'users');

		await spaceSummaryPage.viewAllMembersLink.click();

		await expect(
			page.getByRole('listitem').filter({hasText: userFullName})
		).toHaveCount(0);
	}
);

test(
	'A space member can be promoted to Space Administrator or Space Content Reviewer',
	{tag: '@LPD-85670'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const spaceName = `Space ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {},
			type: 'Space',
		});

		await spaceSummaryPage.goto(spaceName);

		await test.step('Promote a member to Space Administrator', () =>
			promoteNewMember({
				apiHelpers,
				page,
				role: 'Space Administrator',
				spaceSummaryPage,
			}));

		await test.step('Promote a member to Space Content Reviewer', () =>
			promoteNewMember({
				apiHelpers,
				page,
				role: 'Space Content Reviewer',
				spaceSummaryPage,
			}));
	}
);

test(
	'A Space Administrator sees assigned role names in the View All Members list',
	{tag: '@LPD-89984'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const reviewer = await apiHelpers.headlessAdminUser.postUserAccount();
		const reviewerFullName = `${reviewer.givenName} ${reviewer.familyName}`;

		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
			SITE_CMS_SPACE_EXTERNAL_REFERENCE_CODE,
			reviewer.externalReferenceCode
		);

		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccountRoles(
			SITE_CMS_SPACE_EXTERNAL_REFERENCE_CODE,
			reviewer.externalReferenceCode,
			['Asset Library Content Reviewer']
		);

		const spaceAdmin = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[spaceAdmin.alternateName] = {
			name: spaceAdmin.givenName,
			password: 'test',
			surname: spaceAdmin.familyName,
		};

		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
			SITE_CMS_SPACE_EXTERNAL_REFERENCE_CODE,
			spaceAdmin.externalReferenceCode
		);

		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccountRoles(
			SITE_CMS_SPACE_EXTERNAL_REFERENCE_CODE,
			spaceAdmin.externalReferenceCode,
			['Asset Library Administrator']
		);

		await performUserSwitchViaApi(page, spaceAdmin.alternateName);

		await spaceSummaryPage.goto(SITE_CMS_SPACE_NAME);
		await spaceSummaryPage.viewAllMembersLink.click();

		const reviewerRow = page
			.getByRole('listitem')
			.filter({hasText: reviewerFullName});

		await expect(
			reviewerRow.locator('.permission-select-trigger-text')
		).toHaveText('Space Content Reviewer');
	}
);

test(
	'A Space Administrator sees their own role list disabled in the View All Members list',
	{tag: '@LPD-89984'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const spaceAdmin = await apiHelpers.headlessAdminUser.postUserAccount();
		const spaceAdminFullName = `${spaceAdmin.givenName} ${spaceAdmin.familyName}`;

		userData[spaceAdmin.alternateName] = {
			name: spaceAdmin.givenName,
			password: 'test',
			surname: spaceAdmin.familyName,
		};

		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
			SITE_CMS_SPACE_EXTERNAL_REFERENCE_CODE,
			spaceAdmin.externalReferenceCode
		);

		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccountRoles(
			SITE_CMS_SPACE_EXTERNAL_REFERENCE_CODE,
			spaceAdmin.externalReferenceCode,
			['Asset Library Administrator']
		);

		await performUserSwitchViaApi(page, spaceAdmin.alternateName);

		await spaceSummaryPage.goto(SITE_CMS_SPACE_NAME);
		await spaceSummaryPage.viewAllMembersLink.click();

		const currentUserRow = page
			.getByRole('listitem')
			.filter({hasText: spaceAdminFullName});

		await expect(
			currentUserRow.locator('.permission-select-trigger-text')
		).toHaveText('Space Administrator');

		await expect(
			currentUserRow.getByRole('button', {name: 'Space Administrator'})
		).toBeDisabled();
	}
);

test(
	'A non-admin space member can search members in the space',
	{tag: '@LPD-58201'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: `Space ${getRandomString()}`,
			settings: {},
			type: 'Space',
		});

		const viewer = await apiHelpers.headlessAdminUser.postUserAccount();
		const viewerFullName = `${viewer.givenName} ${viewer.familyName}`;

		userData[viewer.alternateName] = {
			name: viewer.givenName,
			password: 'test',
			surname: viewer.familyName,
		};

		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
			space.externalReferenceCode,
			viewer.externalReferenceCode
		);

		const reviewer = await apiHelpers.headlessAdminUser.postUserAccount();
		const reviewerFullName = `${reviewer.givenName} ${reviewer.familyName}`;

		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
			space.externalReferenceCode,
			reviewer.externalReferenceCode
		);

		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccountRoles(
			space.externalReferenceCode,
			reviewer.externalReferenceCode,
			['Asset Library Content Reviewer']
		);

		await performUserSwitchViaApi(page, viewer.alternateName);

		await spaceSummaryPage.goto(space.name);
		await spaceSummaryPage.viewAllMembersLink.click();

		const searchInput = page.getByPlaceholder('Search for name or email');

		await expect(searchInput).toBeVisible();

		await searchInput.fill(reviewer.givenName);

		const reviewerRow = page
			.getByRole('listitem')
			.filter({hasText: reviewerFullName});

		await expect(reviewerRow).toBeVisible();
		await expect(
			page.getByRole('listitem').filter({hasText: viewerFullName})
		).toBeHidden();

		await expect(
			reviewerRow.getByRole('button', {name: 'Space Content Reviewer'})
		).toBeHidden();
	}
);

/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import getRandomString from '../../../../utils/getRandomString';
import {performUserSwitchViaApi} from '../../../../utils/performLogin';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {RecycleBinPage} from '../../../site-cms-site-initializer/main/pages/RecycleBinPage';
import {registerUserCredentials} from '../../../site-cms-site-initializer/main/spaces/helpers/roleMembership';
import {cmsPagesTest} from '../../../site-cms-site-initializer/permissions/fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
	}),
	loginTest()
);

async function prepareUser(apiHelpers: DataApiHelpers) {
	const user = await apiHelpers.headlessAdminUser.postUserAccount();

	registerUserCredentials(user);

	await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(user.id);
	await apiHelpers.jsonWebServicesUser.answerReminderQuery(user.id);

	return user;
}

async function createSpaceMember(
	apiHelpers: DataApiHelpers,
	spaceRoleNames: string[] = []
) {
	const assetLibrary =
		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: getRandomString(),
			settings: {},
			type: 'Space',
		});

	const user = await prepareUser(apiHelpers);

	await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
		assetLibrary.externalReferenceCode,
		user.externalReferenceCode
	);

	if (spaceRoleNames.length) {
		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccountRoles(
			assetLibrary.externalReferenceCode,
			user.externalReferenceCode,
			spaceRoleNames
		);
	}

	return {assetLibrary, user};
}

async function createSpace(apiHelpers: DataApiHelpers) {
	return apiHelpers.headlessAssetLibrary.createAssetLibrary({
		name: getRandomString(),
		settings: {},
		type: 'Space',
	});
}

async function addSpaceUser(
	apiHelpers: DataApiHelpers,
	spaceExternalReferenceCode: string,
	spaceRoleNames: string[] = []
) {
	const user = await prepareUser(apiHelpers);

	await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
		spaceExternalReferenceCode,
		user.externalReferenceCode
	);

	if (spaceRoleNames.length) {
		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccountRoles(
			spaceExternalReferenceCode,
			user.externalReferenceCode,
			spaceRoleNames
		);
	}

	return user;
}

async function createBasicWebContent(
	apiHelpers: DataApiHelpers,
	spaceName: string,
	title: string
) {
	return apiHelpers.objectEntry.postObjectEntry(
		{objectEntryFolderExternalReferenceCode: 'L_CONTENTS', title},
		'cms/basic-web-contents',
		spaceName
	);
}

async function startSessionAs(page: Page, alternateName: string) {
	await performUserSwitchViaApi(page, alternateName);

	await page.goto(PORTLET_URLS.cmsHome, {waitUntil: 'domcontentloaded'});
}

test(
	'Space Administrator, Content Reviewer, and Member cannot access the Content Structures section',
	{tag: ['@LPD-95532', '@LPD-95532/TC-9.g']},
	async ({apiHelpers, page}) => {
		const members = [];

		for (const role of [
			'Asset Library Administrator',
			'Asset Library Content Reviewer',
			'Asset Library Member',
		]) {
			members.push(await createSpaceMember(apiHelpers, [role]));
		}

		for (const {user} of members) {
			await startSessionAs(page, user.alternateName);

			await page.goto(PORTLET_URLS.cmsStructures);

			await expect(
				page.getByRole('heading', {name: 'Content Structures'})
			).toBeHidden();
		}
	}
);

test(
	'Content from one Space is not visible in another Space and members see only their Space',
	{tag: ['@LPD-95532', '@LPD-95532/TC-9.f']},
	async ({apiHelpers, assetsPage, page}) => {
		const spaceA = await createSpace(apiHelpers);
		const spaceB = await createSpace(apiHelpers);

		const titleA = `Content A ${getRandomString()}`;
		const titleB = `Content B ${getRandomString()}`;

		await createBasicWebContent(apiHelpers, spaceA.name, titleA);
		await createBasicWebContent(apiHelpers, spaceB.name, titleB);

		const spaceAdministrator = await addSpaceUser(
			apiHelpers,
			spaceA.externalReferenceCode,
			['Asset Library Administrator']
		);

		await startSessionAs(page, spaceAdministrator.alternateName);

		await test.step('Space Administrator of Space A sees only their content', async () => {
			await assetsPage.gotoAll();

			await expect(
				page.getByRole('cell', {name: titleA}).first()
			).toBeVisible();

			await expect(
				page.getByRole('cell', {name: titleB}).first()
			).toBeHidden();
		});

		await test.step('Space Administrator of Space A cannot see Space B', async () => {
			await page.goto(PORTLET_URLS.cms);

			await expect(
				page.getByRole('menuitem', {name: spaceA.name})
			).toBeVisible();

			await expect(
				page.getByRole('menuitem', {name: spaceB.name})
			).toBeHidden();
		});
	}
);

test(
	'A Space Member can only access Spaces they are a member of',
	{tag: ['@LPD-95532', '@LPD-95532/TC-9.h']},
	async ({apiHelpers, page}) => {
		const memberSpace = await createSpace(apiHelpers);
		const otherSpace = await createSpace(apiHelpers);

		const member = await addSpaceUser(
			apiHelpers,
			memberSpace.externalReferenceCode
		);

		await startSessionAs(page, member.alternateName);

		await page.goto(PORTLET_URLS.cms);

		await expect(
			page.getByRole('menuitem', {name: memberSpace.name})
		).toBeVisible();

		await expect(
			page.getByRole('menuitem', {name: otherSpace.name})
		).toBeHidden();
	}
);

test(
	'Recycle Bin restore is available to a Space Administrator but not a Space Member',
	{tag: ['@LPD-95532', '@LPD-95532/TC-9.d']},
	async ({apiHelpers, contentsPage, page}) => {
		const recycleBinPage = new RecycleBinPage(page);

		const space = await createSpace(apiHelpers);

		const id = getRandomString();
		const adminTitle = `Restore SPA ${id}`;
		const memberTitle = `Restore SM ${id}`;

		for (const title of [adminTitle, memberTitle]) {
			await createBasicWebContent(apiHelpers, space.name, title);
		}

		const spaceAdministrator = await addSpaceUser(
			apiHelpers,
			space.externalReferenceCode,
			['Asset Library Administrator']
		);
		const member = await addSpaceUser(
			apiHelpers,
			space.externalReferenceCode
		);

		await test.step('Delete the content into the Recycle Bin', async () => {
			await contentsPage.goto();

			for (const title of [adminTitle, memberTitle]) {
				await contentsPage.deleteContent(title);
			}
		});

		await test.step('Space Administrator can restore', async () => {
			await startSessionAs(page, spaceAdministrator.alternateName);

			await recycleBinPage.goto();

			await recycleBinPage.execItemAction({
				action: 'Restore',
				filter: adminTitle,
			});

			await expect(recycleBinPage.getItem(adminTitle)).toBeHidden();
		});

		await test.step('Member can access the Recycle Bin but has no restore action', async () => {
			await startSessionAs(page, member.alternateName);

			await recycleBinPage.goto();

			await expect(recycleBinPage.getItem(memberTitle)).toBeVisible();

			await expect(
				recycleBinPage.getItem(memberTitle).getByRole('button')
			).toBeHidden();
		});
	}
);

test(
	'A Space Content Reviewer can restore only the content they have Delete permission on',
	{tag: ['@LPD-95532', '@LPD-95532/TC-9.d']},
	async ({apiHelpers, contentsPage, page}) => {
		const recycleBinPage = new RecycleBinPage(page);

		const space = await createSpace(apiHelpers);

		const id = getRandomString();
		const permittedTitle = `Restore SCR Permitted ${id}`;
		const restrictedTitle = `Restore SCR Restricted ${id}`;

		await createBasicWebContent(apiHelpers, space.name, permittedTitle);

		const restrictedEntry = await createBasicWebContent(
			apiHelpers,
			space.name,
			restrictedTitle
		);

		// Content Reviewer has Delete on new content by default; revoke it
		// here to construct the entry they cannot restore.

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			'cms/basic-web-contents',
			restrictedEntry.id,
			[
				{
					actionIds: ['VIEW'],
					roleName: 'Asset Library Content Reviewer',
				},
			]
		);

		const contentReviewer = await addSpaceUser(
			apiHelpers,
			space.externalReferenceCode,
			['Asset Library Content Reviewer']
		);

		await test.step('Delete both entries into the Recycle Bin', async () => {
			await contentsPage.goto();

			for (const title of [permittedTitle, restrictedTitle]) {
				await contentsPage.deleteContent(title);
			}
		});

		await startSessionAs(page, contentReviewer.alternateName);

		await recycleBinPage.goto();

		await test.step('Content Reviewer restores the entry they have Delete permission on', async () => {
			await recycleBinPage.execItemAction({
				action: 'Restore',
				filter: permittedTitle,
			});

			await expect(recycleBinPage.getItem(permittedTitle)).toBeHidden();
		});

		await test.step('Content Reviewer has no Restore action on the entry they lack Delete permission on', async () => {
			await expect(recycleBinPage.getItem(restrictedTitle)).toBeVisible();

			await expect(
				recycleBinPage.getItem(restrictedTitle).getByRole('button')
			).toBeHidden();
		});
	}
);

test(
	'A file uploaded with the Home quick action appears in the All section and Recent Assets',
	{tag: ['@LPD-95532', '@LPD-95532/TC-9.c']},
	async ({apiHelpers, assetsPage, homePage, page}) => {
		const space = await createSpace(apiHelpers);
		const title = `File ${getRandomString()}`;

		await test.step('Upload a file with the Home quick action', async () => {
			await homePage.goto();

			await homePage.basicDocumentButton.click();

			await homePage.selectSpace(space.name);

			await page.locator('input[type="file"]').setInputFiles({
				buffer: Buffer.from('R0lGODlhAQABAAAAACw=', 'base64'),
				mimeType: 'image/gif',
				name: `${title}.gif`,
			});

			// The upload field processes the file asynchronously. Wait for the
			// file name to register before publishing, otherwise the form
			// submits with an empty file and validation blocks the publish.

			await expect(page.getByText(`${title}.gif`)).toBeVisible();

			await page.getByPlaceholder('New Basic Document').fill(title);

			await page
				.getByRole('button', {name: /Publish Basic Document/})
				.click();

			await waitForAlert(page, '');

			await page.waitForURL(/\/web\/cms\/home/);
		});

		await test.step('The file appears in the All section', async () => {
			await assetsPage.gotoAll();

			await expect(
				page.getByRole('cell', {name: title}).first()
			).toBeVisible();
		});

		await test.step('The file appears in Recent Assets', async () => {
			await homePage.goto();

			await expect(page.getByText(title).first()).toBeVisible();
		});
	}
);

test(
	'A Space Administrator can assign member roles but a Content Reviewer and Member cannot see roles',
	{tag: ['@LPD-95532', '@LPD-95532/TC-9.e']},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const space = await createSpace(apiHelpers);

		await test.step('Administrator adds a member and assigns a role', async () => {
			const newUser =
				await apiHelpers.headlessAdminUser.postUserAccount();
			const newUserFullName = `${newUser.givenName} ${newUser.familyName}`;

			await spaceSummaryPage.goto(space.name);

			await spaceSummaryPage.addUserOrUserGroup(newUserFullName, 'users');

			await spaceSummaryPage.addRoleToSpaceMember(
				'Space Content Reviewer',
				newUserFullName
			);

			await spaceSummaryPage.openMembersDialog();

			await expect(
				page
					.getByRole('listitem')
					.filter({hasText: newUserFullName})
					.getByRole('button', {name: 'Space Content Reviewer'})
			).toBeVisible();

			await spaceSummaryPage.closeButton.click();
		});

		const contentReviewer = await addSpaceUser(
			apiHelpers,
			space.externalReferenceCode,
			['Asset Library Content Reviewer']
		);
		const member = await addSpaceUser(
			apiHelpers,
			space.externalReferenceCode
		);

		const expectMembersWithoutRoles = async (alternateName: string) => {
			await startSessionAs(page, alternateName);

			await spaceSummaryPage.goto(space.name);

			await spaceSummaryPage.openMembersDialog();

			await expect(page.getByRole('listitem').first()).toBeVisible();

			await expect(
				page.locator('.permission-select-trigger-text').first()
			).toBeHidden();
		};

		await test.step('Content Reviewer can see members but not roles', async () => {
			await expectMembersWithoutRoles(contentReviewer.alternateName);
		});

		await test.step('Member can see members but not roles', async () => {
			await expectMembersWithoutRoles(member.alternateName);
		});
	}
);

test(
	'A user with no CMS role or Space membership is denied access to the CMS',
	{tag: ['@LPD-95532', '@LPD-95532/TC-9.i']},
	async ({apiHelpers, page}) => {
		const user = await prepareUser(apiHelpers);

		await startSessionAs(page, user.alternateName);

		await page.goto(PORTLET_URLS.cmsHome);

		await expect(
			page.getByRole('heading', {name: 'Quick Actions'})
		).toBeHidden();
	}
);

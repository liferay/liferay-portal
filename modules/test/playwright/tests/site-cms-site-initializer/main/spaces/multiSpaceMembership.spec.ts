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
import {cmsPagesTest} from '../fixtures/cmsPagesTest';
import {SpaceSummaryPage} from '../pages/SpaceSummaryPage';
import {registerUserCredentials} from './helpers/roleMembership';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
	}),
	loginTest()
);

function createSpace(apiHelpers: DataApiHelpers, spaceName: string) {
	return apiHelpers.headlessAssetLibrary.createAssetLibrary({
		name: spaceName,
		settings: {},
		type: 'Space',
	});
}

async function addMemberToSpaces({
	apiHelpers,
	page,
	spaceNames,
	spaceSummaryPage,
}: {
	apiHelpers: DataApiHelpers;
	page: Page;
	spaceNames: string[];
	spaceSummaryPage: SpaceSummaryPage;
}) {
	const user = await apiHelpers.headlessAdminUser.postUserAccount();
	const userFullName = `${user.givenName} ${user.familyName}`;

	registerUserCredentials(user);

	for (const spaceName of spaceNames) {
		await spaceSummaryPage.goto(spaceName);
		await spaceSummaryPage.addUserOrUserGroup(userFullName, 'users');
	}

	await performUserSwitchViaApi(page, user.alternateName);

	return {user, userFullName};
}

test(
	'A user member of multiple spaces sees only their member spaces and content',
	{tag: '@LPD-85681'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const applicationName = 'cms/basic-web-contents';
		const memberSpace1ContentTitle = `Title ${getRandomString()}`;
		const memberSpace2ContentTitle = `Title ${getRandomString()}`;
		const memberSpaceName1 = `Space ${getRandomString()}`;
		const memberSpaceName2 = `Space ${getRandomString()}`;
		const nonMemberSpaceContentTitle = `Title ${getRandomString()}`;
		const nonMemberSpaceName = `Space ${getRandomString()}`;

		const memberSpace1 = await createSpace(apiHelpers, memberSpaceName1);
		const memberSpace2 = await createSpace(apiHelpers, memberSpaceName2);
		const nonMemberSpace = await createSpace(
			apiHelpers,
			nonMemberSpaceName
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: memberSpace1ContentTitle,
			},
			applicationName,
			memberSpace1.name
		);
		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: memberSpace2ContentTitle,
			},
			applicationName,
			memberSpace2.name
		);
		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: nonMemberSpaceContentTitle,
			},
			applicationName,
			nonMemberSpace.name
		);

		await addMemberToSpaces({
			apiHelpers,
			page,
			spaceNames: [memberSpaceName1, memberSpaceName2],
			spaceSummaryPage,
		});

		await test.step('Sees both member spaces in the Spaces Widget', async () => {
			await page.goto(PORTLET_URLS.cmsHome);

			await expect(
				page.getByRole('menuitem', {name: memberSpaceName1})
			).toBeVisible();
			await expect(
				page.getByRole('menuitem', {name: memberSpaceName2})
			).toBeVisible();
		});

		await test.step('Sees content from both member spaces and not from non-member spaces in All Content', async () => {
			await page.goto(PORTLET_URLS.cmsContents);

			await expect(
				page.getByRole('link', {
					exact: true,
					name: memberSpace1ContentTitle,
				})
			).toBeVisible();
			await expect(
				page.getByRole('link', {
					exact: true,
					name: memberSpace2ContentTitle,
				})
			).toBeVisible();
			await expect(
				page.getByRole('link', {
					exact: true,
					name: nonMemberSpaceContentTitle,
				})
			).not.toBeVisible();
		});
	}
);

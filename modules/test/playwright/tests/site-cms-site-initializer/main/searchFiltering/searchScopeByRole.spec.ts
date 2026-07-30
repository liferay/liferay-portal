/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';
import {addRoleMemberAndSwitch} from '../spaces/helpers/roleMembership';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
	}),
	loginTest()
);

const APPLICATION_NAME = 'cms/basic-web-contents';

async function createTwoSpacesWithKeywordContent(apiHelpers: any) {
	const keyword = getRandomString();
	const ownSpaceTitle = `${keyword} Own Space`;
	const otherSpaceTitle = `${keyword} Other Space`;

	const ownSpace = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
		name: `Space ${getRandomString()}`,
		type: 'Space',
	});

	const otherSpace = await apiHelpers.headlessAssetLibrary.createAssetLibrary(
		{name: `Space ${getRandomString()}`, type: 'Space'}
	);

	await apiHelpers.objectEntry.postObjectEntry(
		{
			objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
			title: ownSpaceTitle,
		},
		APPLICATION_NAME,
		ownSpace.name
	);

	await apiHelpers.objectEntry.postObjectEntry(
		{
			objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
			title: otherSpaceTitle,
		},
		APPLICATION_NAME,
		otherSpace.name
	);

	return {keyword, otherSpaceTitle, ownSpace, ownSpaceTitle};
}

test(
	'A Space Administrator searching a keyword sees only content from their own Spaces',
	{tag: ['@LPD-95544', '@LPD-95544/TC-21.g']},
	async ({apiHelpers, assetsPage, page, spaceSummaryPage}) => {
		const {keyword, otherSpaceTitle, ownSpace, ownSpaceTitle} =
			await createTwoSpacesWithKeywordContent(apiHelpers);

		await addRoleMemberAndSwitch({
			apiHelpers,
			page,
			role: 'Space Administrator',
			spaceName: ownSpace.name,
			spaceSummaryPage,
		});

		await assetsPage.gotoAll();
		await assetsPage.dataSetFragmentPage.search(keyword);

		await expect(assetsPage.getItem(ownSpaceTitle)).toBeVisible();
		await expect(assetsPage.getItem(otherSpaceTitle)).toBeHidden();
	}
);

test(
	'A Space Content Reviewer searching a keyword sees only content from their own Spaces',
	{tag: ['@LPD-95544', '@LPD-95544/TC-21.h']},
	async ({apiHelpers, assetsPage, page, spaceSummaryPage}) => {
		const {keyword, otherSpaceTitle, ownSpace, ownSpaceTitle} =
			await createTwoSpacesWithKeywordContent(apiHelpers);

		await addRoleMemberAndSwitch({
			apiHelpers,
			page,
			role: 'Space Content Reviewer',
			spaceName: ownSpace.name,
			spaceSummaryPage,
		});

		await assetsPage.gotoAll();
		await assetsPage.dataSetFragmentPage.search(keyword);

		await expect(assetsPage.getItem(ownSpaceTitle)).toBeVisible();
		await expect(assetsPage.getItem(otherSpaceTitle)).toBeHidden();
	}
);

test(
	'A Space Member searching a keyword sees only content they have view access to in their own Spaces',
	{tag: ['@LPD-95544', '@LPD-95544/TC-21.i']},
	async ({apiHelpers, assetsPage, page, spaceSummaryPage}) => {
		const {keyword, otherSpaceTitle, ownSpace, ownSpaceTitle} =
			await createTwoSpacesWithKeywordContent(apiHelpers);

		const restrictedTitle = `${keyword} Restricted`;

		const restrictedEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: restrictedTitle,
			},
			APPLICATION_NAME,
			ownSpace.name
		);

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			APPLICATION_NAME,
			restrictedEntry.id,
			[
				{actionIds: ['VIEW'], roleName: 'Asset Library Administrator'},
				{
					actionIds: ['VIEW'],
					roleName: 'Asset Library Content Reviewer',
				},
			]
		);

		await addRoleMemberAndSwitch({
			apiHelpers,
			page,
			role: null,
			spaceName: ownSpace.name,
			spaceSummaryPage,
		});

		await assetsPage.gotoAll();
		await assetsPage.dataSetFragmentPage.search(keyword);

		await expect(assetsPage.getItem(ownSpaceTitle)).toBeVisible();
		await expect(assetsPage.getItem(otherSpaceTitle)).toBeHidden();
		await expect(assetsPage.getItem(restrictedTitle)).toBeHidden();
	}
);

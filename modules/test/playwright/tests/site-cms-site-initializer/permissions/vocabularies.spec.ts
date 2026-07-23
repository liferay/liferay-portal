/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitchViaApi} from '../../../utils/performLogin';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {registerUserCredentials} from '../main/spaces/helpers/roleMembership';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
	}),
	loginTest()
);

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

	const user = await apiHelpers.headlessAdminUser.postUserAccount();

	registerUserCredentials(user);

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

test(
	'A Space Content Reviewer cannot access the Vocabularies admin page',
	{tag: ['@LPD-89497', '@LPD-93287']},
	async ({apiHelpers, page}) => {
		const {user} = await createSpaceMember(apiHelpers, [
			'Asset Library Content Reviewer',
		]);

		await performUserSwitchViaApi(page, user.alternateName);

		await page.goto(PORTLET_URLS.cmsVocabularies);

		await expect(
			page.getByRole('heading', {name: 'Categorization'})
		).toBeHidden();
	}
);

test(
	'A Space Content Reviewer does not see the Vocabulary Quick Action on the CMS Home Page',
	{tag: ['@LPD-90072', '@LPD-93287']},
	async ({apiHelpers, homePage, page}) => {
		const {user} = await createSpaceMember(apiHelpers, [
			'Asset Library Content Reviewer',
		]);

		await performUserSwitchViaApi(page, user.alternateName);

		await homePage.goto();

		await expect(
			page.getByRole('heading', {name: 'Quick Actions'})
		).toBeVisible();

		await expect(homePage.vocabularyButton).toBeHidden();
	}
);

test(
	'A CMS Administrator sees the Vocabulary Quick Action on the CMS Home Page',
	{tag: ['@LPD-90072', '@LPD-93287']},
	async ({apiHelpers, homePage, page}) => {
		const {user} = await createSpaceMember(apiHelpers);

		const cmsAdministratorRole =
			await apiHelpers.headlessAdminUser.getRoleByName(
				'CMS Administrator'
			);

		await apiHelpers.headlessAdminUser.postRoleUserAccountAssociation(
			cmsAdministratorRole.id,
			Number(user.id)
		);

		await performUserSwitchViaApi(page, user.alternateName);

		await homePage.goto();

		await expect(
			page.getByRole('heading', {name: 'Quick Actions'})
		).toBeVisible();

		await expect(homePage.vocabularyButton).toBeVisible();
	}
);

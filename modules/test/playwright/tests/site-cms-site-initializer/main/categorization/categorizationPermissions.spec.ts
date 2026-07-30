/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {performLoginViaApi, userData} from '../../../../utils/performLogin';
import {PORTLET_URLS} from '../../../../utils/portletUrls';

const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
	}),
	loginTest()
);

async function createSpaceUserWithRole(apiHelpers: any, roleName: string) {
	const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
		name: `Space ${getRandomString()}`,
		type: 'Space',
	});

	const user = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[user.alternateName] = {
		name: user.givenName,
		password: 'test',
		surname: user.familyName,
	};

	await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(user.id);
	await apiHelpers.jsonWebServicesUser.answerReminderQuery(user.id);

	await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
		space.externalReferenceCode,
		user.externalReferenceCode
	);
	await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccountRoles(
		space.externalReferenceCode,
		user.externalReferenceCode,
		[roleName]
	);

	return {space, user};
}

test(
	'A Space Admin cannot access Categorization',
	{tag: ['@LPD-95543', '@LPD-95543/TC-20.f']},
	async ({apiHelpers, page}) => {
		const {user} = await createSpaceUserWithRole(
			apiHelpers,
			'Asset Library Administrator'
		);

		await performLoginViaApi({page, screenName: user.alternateName});

		await page.goto(PORTLET_URLS.cms);

		await expect(
			page.getByRole('link', {exact: true, name: 'Categorization'})
		).toBeHidden();

		await page.goto(PORTLET_URLS.cmsVocabularies);

		await expect(
			page.getByRole('heading', {name: 'Categorization'})
		).toBeHidden();
	}
);

test(
	'A Space Content Reviewer cannot access or create in Categorization',
	{tag: ['@LPD-95543', '@LPD-95543/TC-20.g']},
	async ({apiHelpers, page}) => {
		const {user} = await createSpaceUserWithRole(
			apiHelpers,
			'Asset Library Content Reviewer'
		);

		await performLoginViaApi({page, screenName: user.alternateName});

		await page.goto(PORTLET_URLS.cms);

		await expect(
			page.getByRole('link', {exact: true, name: 'Categorization'})
		).toBeHidden();

		await page.goto(PORTLET_URLS.cmsNewVocabulary);

		await expect(
			page.getByRole('heading', {name: 'New Vocabulary'})
		).toBeHidden();
	}
);

test(
	'A Space Member cannot access the Admin area',
	{tag: ['@LPD-95543', '@LPD-95543/TC-20.h']},
	async ({apiHelpers, page}) => {
		const {user} = await createSpaceUserWithRole(
			apiHelpers,
			'Asset Library Member'
		);

		await performLoginViaApi({page, screenName: user.alternateName});

		await page.goto(PORTLET_URLS.cms);

		await expect(page.getByText('ADMIN', {exact: true})).toBeHidden();
		await expect(
			page.getByRole('link', {exact: true, name: 'Categorization'})
		).toBeHidden();
		await expect(
			page.getByRole('link', {exact: true, name: 'Content Structures'})
		).toBeHidden();

		await page.goto(PORTLET_URLS.cmsVocabularies);

		await expect(
			page.getByRole('heading', {name: 'Categorization'})
		).toBeHidden();
	}
);

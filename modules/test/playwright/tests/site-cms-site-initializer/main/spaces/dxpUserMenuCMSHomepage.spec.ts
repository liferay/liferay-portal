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
import {cmsPagesTest} from '../fixtures/cmsPagesTest';
import {SpaceSummaryPage} from '../pages/SpaceSummaryPage';
import {SpaceRole, addRoleMemberAndSwitch} from './helpers/roleMembership';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
	}),
	loginTest()
);

async function expectCMSHomeNavigationForRole({
	apiHelpers,
	page,
	role,
	spaceName,
	spaceSummaryPage,
}: {
	apiHelpers: DataApiHelpers;
	page: Page;
	role: SpaceRole;
	spaceName: string;
	spaceSummaryPage: SpaceSummaryPage;
}) {
	await performUserSwitchViaApi(page, 'test');

	const {userFullName} = await addRoleMemberAndSwitch({
		apiHelpers,
		page,
		role,
		spaceName,
		spaceSummaryPage,
	});

	await page
		.getByRole('button', {name: `${userFullName} User Profile`})
		.click();

	await page.getByRole('menuitem', {name: 'My CMS Homepage'}).click();

	await expect(page).toHaveURL(/\/web\/cms\/home/);
}

test(
	'A Space role lands on the CMS Home from the DXP user menu link',
	{tag: '@LPD-85681'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const spaceName = `Space ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {},
			type: 'Space',
		});

		await test.step('A Space Admin lands on the CMS Home from the DXP user menu link', () =>
			expectCMSHomeNavigationForRole({
				apiHelpers,
				page,
				role: 'Space Administrator',
				spaceName,
				spaceSummaryPage,
			}));

		await test.step('A Space Member lands on the CMS Home from the DXP user menu link', () =>
			expectCMSHomeNavigationForRole({
				apiHelpers,
				page,
				role: null,
				spaceName,
				spaceSummaryPage,
			}));

		await test.step('A Space Content Reviewer lands on the CMS Home from the DXP user menu link', () =>
			expectCMSHomeNavigationForRole({
				apiHelpers,
				page,
				role: 'Space Content Reviewer',
				spaceName,
				spaceSummaryPage,
			}));
	}
);

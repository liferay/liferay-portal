/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {addCMSAdministrator} from '../../../../utils/addCMSAdministrator';
import getRandomString from '../../../../utils/getRandomString';
import {performUserSwitchViaApi} from '../../../../utils/performLogin';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
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

const ALWAYS_VISIBLE_SECTIONS = [
	'Home',
	'Contents',
	'Files',
	'Shared with Me',
	'Recycle Bin',
];

const CMS_ADMIN_ONLY_SECTIONS = [
	'Dashboard',
	'Content Structures',
	'Categorization',
];

function createSpace(apiHelpers: DataApiHelpers, spaceName: string) {
	return apiHelpers.headlessAssetLibrary.createAssetLibrary({
		name: spaceName,
		settings: {},
		type: 'Space',
	});
}

async function expectSectionsVisibility(
	page: Page,
	{hidden, visible}: {hidden: string[]; visible: string[]}
) {
	for (const section of visible) {
		await expect(page.getByRole('menuitem', {name: section})).toBeVisible();
	}

	for (const section of hidden) {
		await expect(
			page.getByRole('menuitem', {name: section})
		).not.toBeVisible();
	}
}

async function expectAllowedSectionsForRole({
	apiHelpers,
	hidden,
	page,
	role,
	spaceName,
	spaceSummaryPage,
	visible,
}: {
	apiHelpers: DataApiHelpers;
	hidden: string[];
	page: Page;
	role: SpaceRole;
	spaceName: string;
	spaceSummaryPage: SpaceSummaryPage;
	visible: string[];
}) {
	await performUserSwitchViaApi(page, 'test');

	await addRoleMemberAndSwitch({
		apiHelpers,
		page,
		role,
		spaceName,
		spaceSummaryPage,
	});

	await page.goto(PORTLET_URLS.cmsHome);

	await expectSectionsVisibility(page, {hidden, visible});
}

async function expectMemberSpacesOnlyForRole({
	apiHelpers,
	memberSpaceName,
	nonMemberSpaceName,
	page,
	role,
	spaceSummaryPage,
}: {
	apiHelpers: DataApiHelpers;
	memberSpaceName: string;
	nonMemberSpaceName: string;
	page: Page;
	role: SpaceRole;
	spaceSummaryPage: SpaceSummaryPage;
}) {
	await performUserSwitchViaApi(page, 'test');

	await addRoleMemberAndSwitch({
		apiHelpers,
		page,
		role,
		spaceName: memberSpaceName,
		spaceSummaryPage,
	});

	await page.goto(PORTLET_URLS.cmsHome);

	await expect(
		page.getByRole('menuitem', {name: memberSpaceName})
	).toBeVisible();
	await expect(
		page.getByRole('menuitem', {name: nonMemberSpaceName})
	).not.toBeVisible();
}

test(
	'A CMS Admin sees all CMS sections in the navigation',
	{tag: '@LPD-85681'},
	async ({apiHelpers, page}) => {
		const cmsAdmin = await addCMSAdministrator(apiHelpers);

		await performUserSwitchViaApi(page, cmsAdmin.alternateName);

		await page.goto(PORTLET_URLS.cmsHome);

		await expectSectionsVisibility(page, {
			hidden: [],
			visible: [...ALWAYS_VISIBLE_SECTIONS, ...CMS_ADMIN_ONLY_SECTIONS],
		});
	}
);

test(
	'A space role sees only the allowed CMS sections',
	{tag: '@LPD-85681'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const spaceName = `Space ${getRandomString()}`;

		await createSpace(apiHelpers, spaceName);

		await test.step('A Space Member sees only the allowed CMS sections', () =>
			expectAllowedSectionsForRole({
				apiHelpers,
				hidden: CMS_ADMIN_ONLY_SECTIONS,
				page,
				role: null,
				spaceName,
				spaceSummaryPage,
				visible: ALWAYS_VISIBLE_SECTIONS,
			}));

		await test.step('A Content Reviewer sees only the allowed CMS sections', () =>
			expectAllowedSectionsForRole({
				apiHelpers,
				hidden: CMS_ADMIN_ONLY_SECTIONS,
				page,
				role: 'Space Content Reviewer',
				spaceName,
				spaceSummaryPage,
				visible: ALWAYS_VISIBLE_SECTIONS,
			}));

		await test.step('A Space Admin sees only the allowed CMS sections', () =>
			expectAllowedSectionsForRole({
				apiHelpers,
				hidden: CMS_ADMIN_ONLY_SECTIONS,
				page,
				role: 'Space Administrator',
				spaceName,
				spaceSummaryPage,
				visible: ALWAYS_VISIBLE_SECTIONS,
			}));
	}
);

test(
	'The Spaces Widget lists every space for a CMS Admin',
	{tag: '@LPD-85681'},
	async ({apiHelpers, page}) => {
		const spaceName1 = `Space ${getRandomString()}`;
		const spaceName2 = `Space ${getRandomString()}`;

		await createSpace(apiHelpers, spaceName1);
		await createSpace(apiHelpers, spaceName2);

		const cmsAdmin = await addCMSAdministrator(apiHelpers);

		await performUserSwitchViaApi(page, cmsAdmin.alternateName);

		await page.goto(PORTLET_URLS.cmsHome);

		await expect(
			page.getByRole('menuitem', {name: spaceName1})
		).toBeVisible();
		await expect(
			page.getByRole('menuitem', {name: spaceName2})
		).toBeVisible();
	}
);

test(
	'The Spaces Widget lists only member spaces for each space role',
	{tag: '@LPD-85681'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const spaceName1 = `Space ${getRandomString()}`;
		const spaceName2 = `Space ${getRandomString()}`;

		await createSpace(apiHelpers, spaceName1);
		await createSpace(apiHelpers, spaceName2);

		await test.step('The Spaces Widget lists only member spaces for a Space Member', () =>
			expectMemberSpacesOnlyForRole({
				apiHelpers,
				memberSpaceName: spaceName1,
				nonMemberSpaceName: spaceName2,
				page,
				role: null,
				spaceSummaryPage,
			}));

		await test.step('The Spaces Widget lists only member spaces for a Content Reviewer', () =>
			expectMemberSpacesOnlyForRole({
				apiHelpers,
				memberSpaceName: spaceName1,
				nonMemberSpaceName: spaceName2,
				page,
				role: 'Space Content Reviewer',
				spaceSummaryPage,
			}));

		await test.step('The Spaces Widget lists only member spaces for a Space Admin', () =>
			expectMemberSpacesOnlyForRole({
				apiHelpers,
				memberSpaceName: spaceName1,
				nonMemberSpaceName: spaceName2,
				page,
				role: 'Space Administrator',
				spaceSummaryPage,
			}));
	}
);

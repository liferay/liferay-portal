/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
		'LPS-179669': {enabled: true},
	}),
	loginTest()
);

test(
	'Can access to View All Files page',
	{tag: '@LPD-62706'},
	async ({page, spaceSummaryPage}) => {
		const spaceName = 'Default';

		await spaceSummaryPage.goto(spaceName);
		await spaceSummaryPage.viewAllFilesLink.click();

		expect(page.getByRole('link', {name: spaceName})).toBeVisible();
		expect(page.getByRole('link', {name: 'Files'})).toBeVisible();
		expect(page.getByText('No Files Yet')).toBeVisible();
	}
);

test(
	'Can view added files in the space summary page',
	{tag: '@LPD-62706'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const applicationName = 'cms/basic-documents';
		const spaceName = 'Default';

		const file1Title = `title ${getRandomString()}`;

		const objectEntry1 = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: 'R0lGODlhAQABAAAAACw=',
					name: `file_${getRandomString()}.png`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: file1Title,
			},
			applicationName,
			spaceName
		);

		await spaceSummaryPage.goto(spaceName);

		expect(page.getByText(file1Title)).toBeVisible();

		await apiHelpers.objectEntry.deleteObjectEntry(
			applicationName,
			String(objectEntry1.id)
		);
	}
);

test(
	'Can access to View All Content page',
	{tag: '@LPD-62706'},
	async ({page, spaceSummaryPage}) => {
		const spaceName = 'Default';

		await spaceSummaryPage.goto(spaceName);
		await spaceSummaryPage.viewAllContentLink.click();

		expect(page.getByRole('link', {name: spaceName})).toBeVisible();
		expect(page.getByRole('link', {name: 'Contents'})).toBeVisible();
		expect(page.getByText('No Content Yet')).toBeVisible();
	}
);

test(
	'Can add and delete a user group as a member of the space',
	{tag: '@LPD-61617'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const spaceName = 'Default';

		await spaceSummaryPage.goto(spaceName);

		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

		await spaceSummaryPage.addUserOrUserGroup(userGroup.name, 'groups');

		await spaceSummaryPage.userGroupsTab.click();

		await expect(page.getByText(userGroup.name)).toBeVisible();

		await spaceSummaryPage.removeUserOrUserGroup(userGroup.name, 'groups');

		await spaceSummaryPage.userGroupsTab.click();

		await expect(page.getByText(userGroup.name)).not.toBeVisible();
	}
);

test(
	'Can view added content in the space summary page',
	{tag: '@LPD-62706'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const applicationName = 'cms/basic-web-contents';
		const spaceName = 'Default';

		const file1Title = `title ${getRandomString()}`;

		const objectEntry1 = await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: file1Title,
			},
			applicationName,
			spaceName
		);

		await spaceSummaryPage.goto(spaceName);

		expect(page.getByText(file1Title)).toBeVisible();

		await apiHelpers.objectEntry.deleteObjectEntry(
			applicationName,
			String(objectEntry1.id)
		);
	}
);

test(
	'Can connect and disconnect a site for the Default space',
	{tag: '@LPD-39906'},
	async ({page, spaceSummaryPage}) => {
		const spaceName = 'Default';
		const siteName = 'Global';

		const globalSiteLocator = page
			.getByTestId('space-summary-connected-sites')
			.getByText(siteName, {exact: true});

		await spaceSummaryPage.goto(spaceName);

		expect(globalSiteLocator).not.toBeVisible();

		await spaceSummaryPage.connectSite(siteName);

		await expect(
			page.getByRole('heading', {name: 'Sites (1)'})
		).toBeVisible();
		await expect(globalSiteLocator).toBeVisible();

		await page
			.getByRole('row', {name: `${siteName} ${siteName} Actions`})
			.getByRole('button')
			.click();
		await page.getByRole('menuitem', {name: 'Disconnect'}).click();

		expect(globalSiteLocator).not.toBeVisible();
	}
);

test(
	'Can view Share modal for added content',
	{tag: '@LPD-62554'},
	async ({apiHelpers, assetsPage, page, spaceSummaryPage}) => {
		const applicationName = 'cms/basic-web-contents';
		const file1Title = `Title ${getRandomString()}`;
		const spaceName = `Space ${getRandomString()}`;
		let objectEntry1;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {
				logoColor: 'outline-3',
				sharingEnabled: true,
			},
			type: 'Space',
		});

		try {
			objectEntry1 = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: file1Title,
				},
				applicationName,
				spaceName
			);

			await spaceSummaryPage.goto(spaceName);

			await assetsPage.execItemAction({
				action: 'Share',
				filter: file1Title,
			});

			await expect(page.locator('.modal-title')).toContainText(
				file1Title
			);
		}
		finally {
			await apiHelpers.objectEntry.deleteObjectEntry(
				applicationName,
				String(objectEntry1.id)
			);
		}
	}
);

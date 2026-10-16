/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {performUserSwitchViaApi} from '../../../../utils/performLogin';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';
import {registerUserCredentials} from './helpers/roleMembership';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

test(
	'Opens in Gallery View by default for files',
	{tag: '@LPD-72056'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const applicationName = 'cms/basic-documents';
		const spaceName = 'Default';

		const file1Title = `title ${getRandomString()}`;

		await apiHelpers.objectEntry.postObjectEntry(
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
		await spaceSummaryPage.viewAllFilesLink.click();

		await expect(
			page.getByRole('combobox', {name: 'Gallery View Selected'})
		).toBeVisible();

		await expect(
			spaceSummaryPage.galleryPreview.getByText('No Preview Available')
		).toBeVisible();

		await expect(page.getByText(file1Title)).toBeVisible();
	}
);

test(
	'Can access Add button when there are no items',
	{tag: '@LPD-62706'},
	async ({page, spaceSummaryPage}) => {
		const spaceName = 'Default';

		await spaceSummaryPage.goto(spaceName);

		await page.getByRole('button', {name: `Add Content`}).click();

		let dropdown = page.locator('.dropdown-menu.show');

		await Promise.all([
			expect(dropdown.getByText('Basic Web Content')).toBeVisible(),
			expect(dropdown.getByText('Blog')).toBeVisible(),
			expect(dropdown.getByText('Folder')).toBeVisible(),
		]);

		await page.getByRole('button', {name: `Add Content`}).click();

		await page.getByRole('button', {name: `Add Files`}).click();

		dropdown = page.locator('.dropdown-menu.show');

		await Promise.all([
			expect(dropdown.getByText('External Video')).toBeVisible(),
			expect(dropdown.getByText('Folder')).toBeVisible(),
			expect(dropdown.getByText('Multiple Files')).toBeVisible(),
			expect(dropdown.getByText('Single File')).toBeVisible(),
		]);
	}
);

test(
	'Can access to View All Files page if file is available',
	{tag: '@LPD-62706'},
	async ({page, spaceSummaryPage}) => {
		const spaceName = 'Default';

		await spaceSummaryPage.goto(spaceName);

		await spaceSummaryPage.createFileFolder('Folder' + getRandomInt());

		await spaceSummaryPage.viewAllFilesLink.click();

		await expect(page.getByRole('link', {name: spaceName})).toBeVisible();
		await expect(page.getByRole('link', {name: 'Files'})).toBeVisible();
	}
);

test(
	'View All Files link is not rendered when there are no files',
	{tag: '@LPD-85991'},
	async ({apiHelpers, spaceSummaryPage}) => {
		const space = await apiHelpers.headlessAssetLibrary.createSpace({
			settings: {
				logoColor: 'outline-3',
			},
		});

		await spaceSummaryPage.goto(space.name);

		await expect(spaceSummaryPage.viewAllFilesLink).not.toBeVisible();

		await spaceSummaryPage.createFileFolder('Folder' + getRandomInt());

		await expect(spaceSummaryPage.viewAllFilesLink).toBeVisible();
	}
);

test(
	'Can view added files in the space summary page',
	{tag: ['@LPD-62706', '@LPD-86299']},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const applicationName = 'cms/basic-documents';
		const spaceName = 'Default';

		const fileTitle = `title ${getRandomString()}`;

		await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: 'R0lGODlhAQABAAAAACw=',
					name: `file_${getRandomString()}.png`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: fileTitle,
			},
			applicationName,
			spaceName
		);

		await spaceSummaryPage.goto(spaceName);

		await expect(page.getByText(fileTitle)).toBeVisible();

		await page.getByText(fileTitle).click();

		await expect(page.getByRole('textbox', {name: 'Title'})).toBeVisible();
	}
);

test(
	'Can access to View All Content page if content is available',
	{tag: '@LPD-62706'},
	async ({page, spaceSummaryPage}) => {
		const spaceName = 'Default';

		await spaceSummaryPage.goto(spaceName);

		await spaceSummaryPage.createContentFolder('Folder' + getRandomInt());

		await spaceSummaryPage.viewAllContentLink.click();

		await expect(page.getByRole('link', {name: spaceName})).toBeVisible();
		await expect(page.getByRole('link', {name: 'Contents'})).toBeVisible();
	}
);

test(
	'View All Content link is not rendered when there is no content',
	{tag: '@LPD-85991'},
	async ({apiHelpers, spaceSummaryPage}) => {
		const space = await apiHelpers.headlessAssetLibrary.createSpace({
			settings: {
				logoColor: 'outline-3',
			},
		});

		await spaceSummaryPage.goto(space.name);

		await expect(spaceSummaryPage.viewAllContentLink).not.toBeVisible();

		await spaceSummaryPage.createContentFolder('Folder' + getRandomInt());

		await expect(spaceSummaryPage.viewAllContentLink).toBeVisible();
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
	{tag: ['@LPD-62706', '@LPD-86299']},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const applicationName = 'cms/basic-web-contents';
		const spaceName = 'Default';

		const contentTitle = `title ${getRandomString()}`;

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentTitle,
			},
			applicationName,
			spaceName
		);

		await spaceSummaryPage.goto(spaceName);

		await expect(
			page.getByRole('link', {name: contentTitle})
		).toBeVisible();

		await page.getByRole('link', {name: contentTitle}).click();

		await expect(page.getByRole('textbox', {name: 'Title'})).toBeVisible();
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

		await expect(globalSiteLocator).not.toBeVisible();

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

		await expect(globalSiteLocator).not.toBeVisible();
		await expect(
			page.getByRole('heading', {name: 'Sites (0)'})
		).toBeVisible();
	}
);

test(
	'Space member without assign-members permission cannot see the Add Members button',
	{tag: '@LPD-89584'},
	async ({apiHelpers, spaceSummaryPage}) => {
		const space = await apiHelpers.headlessAssetLibrary.createSpace();

		const user = await apiHelpers.headlessAdminUser.postUserAccount();
		const userFullName = `${user.givenName} ${user.familyName}`;

		registerUserCredentials(user);

		await spaceSummaryPage.goto(space.name);

		await spaceSummaryPage.addUserOrUserGroup(userFullName, 'users');

		await performUserSwitchViaApi(
			spaceSummaryPage.page,
			user.alternateName
		);

		await spaceSummaryPage.goto(space.name);

		await spaceSummaryPage.usersTab.click();

		await expect(spaceSummaryPage.addMembersButton).toBeHidden();

		await spaceSummaryPage.userGroupsTab.click();

		await expect(spaceSummaryPage.addMembersButton).toBeHidden();
	}
);

test(
	'Can view Share modal for added content',
	{tag: '@LPD-62554'},
	async ({apiHelpers, assetsPage, page, spaceSummaryPage}) => {
		const applicationName = 'cms/basic-web-contents';
		const file1Title = `Title ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createSpace({
			settings: {
				logoColor: 'outline-3',
				sharingEnabled: true,
			},
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: file1Title,
			},
			applicationName,
			space.name
		);

		await spaceSummaryPage.goto(space.name);

		await assetsPage.execItemAction({
			action: 'Share',
			filter: file1Title,
		});

		await expect(page.locator('.modal-title')).toContainText(file1Title);
	}
);

test(
	'Displays at most 8 contents in the space summary page, with the rest in the View All Content page',
	{tag: '@LPD-85670'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const applicationName = 'cms/basic-web-contents';

		const space = await apiHelpers.headlessAssetLibrary.createSpace();

		const titles: string[] = [];

		for (let index = 0; index < 9; index++) {
			titles.push(`Title ${index} ${getRandomString()}`);
		}

		for (const title of titles) {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title,
				},
				applicationName,
				space.name
			);
		}

		await spaceSummaryPage.goto(space.name);

		await expect(
			page.getByRole('link', {
				exact: true,
				name: titles[titles.length - 1],
			})
		).toBeVisible();

		await expect(
			page.getByRole('link', {
				exact: true,
				name: titles[0],
			})
		).not.toBeVisible();

		const visibleCount = await page
			.getByRole('link', {name: /^Title \d+ /})
			.count();

		expect(visibleCount).toBe(8);

		await spaceSummaryPage.viewAllContentLink.click();

		for (const title of titles) {
			await expect(
				page.getByRole('link', {exact: true, name: title})
			).toBeVisible();
		}
	}
);

test(
	'Displays the most recently modified file first in the space summary page',
	{tag: '@LPD-85670'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const applicationName = 'cms/basic-web-contents';
		const newerFileTitle = `Newer ${getRandomString()}`;
		const olderFileTitle = `Older ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createSpace();

		await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: 'SGVsbG8sIHdvcmxkIQ==',
					name: `older_${getRandomString()}.txt`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: olderFileTitle,
			},
			applicationName,
			space.name
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: 'SGVsbG8sIHdvcmxkIQ==',
					name: `newer_${getRandomString()}.txt`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: newerFileTitle,
			},
			applicationName,
			space.name
		);

		await spaceSummaryPage.goto(space.name);

		const firstFileTitle = await page
			.getByRole('link', {name: /Newer|Older/})
			.first()
			.innerText();

		expect(firstFileTitle).toContain(newerFileTitle);
	}
);

test(
	'Displays the most recently modified content first in the space summary page',
	{tag: '@LPD-85670'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const applicationName = 'cms/basic-web-contents';
		const newerContentTitle = `Newer ${getRandomString()}`;
		const olderContentTitle = `Older ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createSpace();

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: olderContentTitle,
			},
			applicationName,
			space.name
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: newerContentTitle,
			},
			applicationName,
			space.name
		);

		await spaceSummaryPage.goto(space.name);

		const firstContentTitle = await page
			.getByRole('link', {name: /Newer|Older/})
			.first()
			.innerText();

		expect(firstContentTitle).toContain(newerContentTitle);
	}
);

test(
	'Displays at most 8 files in the space summary page, with the rest in the View All Files page',
	{tag: '@LPD-85670'},
	async ({apiHelpers, assetsPage, page, spaceSummaryPage}) => {
		const applicationName = 'cms/basic-documents';

		const space = await apiHelpers.headlessAssetLibrary.createSpace();

		const titles: string[] = [];

		for (let index = 0; index < 9; index++) {
			titles.push(`File ${index} ${getRandomString()}`);
		}

		for (const title of titles) {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					file: {
						fileBase64: 'SGVsbG8sIHdvcmxkIQ==',
						name: `file_${getRandomString()}.txt`,
					},
					objectEntryFolderExternalReferenceCode: 'L_FILES',
					title,
				},
				applicationName,
				space.name
			);
		}

		await spaceSummaryPage.goto(space.name);

		await expect(
			page.getByRole('link', {
				exact: true,
				name: titles[titles.length - 1],
			})
		).toBeVisible();

		await expect(
			page.getByRole('link', {
				exact: true,
				name: titles[0],
			})
		).not.toBeVisible();

		const visibleCount = await page
			.getByRole('link', {name: /^File \d+ /})
			.count();

		expect(visibleCount).toBe(8);

		await spaceSummaryPage.viewAllFilesLink.click();

		await assetsPage.changeVisualizationMode('Table');

		for (const title of titles) {
			await expect(
				page.getByRole('link', {exact: true, name: title})
			).toBeVisible();
		}
	}
);

test(
	'Home view caps content at 8 items mixing folders and entries',
	{tag: '@LPD-85670'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const applicationName = 'cms/basic-web-contents';

		const space = await apiHelpers.headlessAssetLibrary.createSpace();

		const folderTitles: string[] = [];
		const entryTitles: string[] = [];

		for (let index = 0; index < 5; index++) {
			folderTitles.push(`Folder ${index} ${getRandomString()}`);
			entryTitles.push(`Entry ${index} ${getRandomString()}`);
		}

		for (const title of folderTitles) {
			await apiHelpers.objectFolder.createObjectEntryFolder({
				parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				scopeKey: space.siteId,
				title,
			});
		}

		for (const title of entryTitles) {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title,
				},
				applicationName,
				space.name
			);
		}

		await spaceSummaryPage.goto(space.name);

		const allTitles = [...folderTitles, ...entryTitles];

		await expect(
			page.getByRole('link', {
				exact: true,
				name: allTitles[allTitles.length - 1],
			})
		).toBeVisible();

		const visibleCount = await page
			.getByRole('link', {name: /^(Folder|Entry) \d+ /})
			.count();

		expect(visibleCount).toBe(8);

		await spaceSummaryPage.viewAllContentLink.click();

		for (const title of allTitles) {
			await expect(
				page.getByRole('link', {exact: true, name: title})
			).toBeVisible();
		}
	}
);

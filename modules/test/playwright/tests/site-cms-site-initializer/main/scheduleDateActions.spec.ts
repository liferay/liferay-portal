/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import getRandomString from '../../../utils/getRandomString';
import {cmsPagesTest} from './fixtures/cmsPagesTest';
import {addRoleMemberAndSwitch} from './spaces/helpers/roleMembership';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

const APPLICATION_NAME = 'cms/basic-web-contents';

const DATE_ACTIONS = ['Update Expiration Date', 'Update Review Date'];

async function createContent(apiHelpers: DataApiHelpers, scope = 'Default') {
	const title = getRandomString();

	const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
		{
			objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
			title,
		},
		APPLICATION_NAME,
		scope
	);

	apiHelpers.data.push({
		applicationName: APPLICATION_NAME,
		id: objectEntry.id,
		type: 'objectEntry',
	});

	return title;
}

async function createFile(apiHelpers: DataApiHelpers) {
	const title = getRandomString();

	const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
		{
			objectEntryFolderExternalReferenceCode: 'L_FILES',
			title,
			videoURL: 'https://www.youtube.com/watch?v=IqCSx3omX4o',
		},
		'cms/external-videos',
		'Default'
	);

	apiHelpers.data.push({
		id: objectEntry.id,
		type: 'document',
	});

	return title;
}

async function expectDateActionsInMenu(page: Page, trigger: Locator) {
	await trigger.click();

	const menu = page.getByRole('menu');

	await expect(menu).toBeVisible();

	for (const action of DATE_ACTIONS) {
		await expect(
			menu.getByRole('menuitem', {exact: true, name: action})
		).toBeVisible();
	}

	await page.keyboard.press('Escape');

	await expect(
		page.getByRole('menuitem', {exact: true, name: DATE_ACTIONS[0]})
	).toBeHidden();
}

function getBulkActionsTrigger(page: Page) {
	return page
		.getByTestId(/visualization-mode/)
		.getByLabel('Actions', {exact: true});
}

test(
	'The date actions appear in the Home, Contents, and Files sections',
	{tag: '@LPD-103652'},
	async ({apiHelpers, assetsPage, homePage, page}) => {
		const contentTitle = await createContent(apiHelpers);
		const fileTitle = await createFile(apiHelpers);

		await homePage.goto();

		await expectDateActionsInMenu(
			page,
			page.getByRole('button', {name: `${contentTitle} Actions`})
		);

		await assetsPage.gotoContents();

		await expectDateActionsInMenu(
			page,
			assetsPage
				.getItem(contentTitle)
				.getByRole('button', {name: `${contentTitle} Actions`})
		);

		await assetsPage.gotoFiles();

		await expectDateActionsInMenu(
			page,
			assetsPage.getCardItem(fileTitle).getByLabel(`${fileTitle} Actions`)
		);
	}
);

test(
	'The date actions are hidden for folders but offered for content',
	{tag: '@LPD-103652'},
	async ({apiHelpers, assetsPage, page}) => {
		const contentTitle = await createContent(apiHelpers);

		const folderTitle = getRandomString();

		const folder = await apiHelpers.objectFolder.createObjectEntryFolder({
			parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
			scopeKey: 'Default',
			title: folderTitle,
		});

		try {
			await assetsPage.gotoContents();

			await expectDateActionsInMenu(
				page,
				assetsPage
					.getItem(contentTitle)
					.getByRole('button', {name: `${contentTitle} Actions`})
			);

			for (const action of DATE_ACTIONS) {
				await assetsPage.expectItemActionHidden(action, folderTitle);
			}
		}
		finally {
			await apiHelpers.objectFolder.deleteObjectEntryFolder(folder.id);
		}
	}
);

test(
	'The date bulk actions are hidden when a folder is selected',
	{tag: '@LPD-103652'},
	async ({apiHelpers, assetsPage, page}) => {
		const contentTitle = await createContent(apiHelpers);

		const folderTitle = getRandomString();

		const folder = await apiHelpers.objectFolder.createObjectEntryFolder({
			parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
			scopeKey: 'Default',
			title: folderTitle,
		});

		try {
			await assetsPage.gotoContents();

			await assetsPage.selectItems([contentTitle]);

			await expectDateActionsInMenu(page, getBulkActionsTrigger(page));

			await assetsPage.selectItems([folderTitle]);

			for (const action of DATE_ACTIONS) {
				await assetsPage.expectBulkItemActionHidden(action);
			}
		}
		finally {
			await apiHelpers.objectFolder.deleteObjectEntryFolder(folder.id);
		}
	}
);

test(
	'The date bulk actions appear in the Contents and Files sections',
	{tag: '@LPD-103652'},
	async ({apiHelpers, assetsPage, page}) => {
		const contentTitle = await createContent(apiHelpers);
		const fileTitle = await createFile(apiHelpers);

		const bulkActionsTrigger = getBulkActionsTrigger(page);

		await assetsPage.gotoContents();

		await assetsPage.selectItems([contentTitle]);

		await expectDateActionsInMenu(page, bulkActionsTrigger);

		await assetsPage.gotoFiles();

		await assetsPage.selectItems([fileTitle]);

		await expectDateActionsInMenu(page, bulkActionsTrigger);
	}
);

for (const role of ['Space Administrator', 'Space Content Reviewer'] as const) {
	test(
		`A ${role} sees the date actions on their space content`,
		{tag: '@LPD-103652'},
		async ({apiHelpers, assetsPage, page, spaceSummaryPage}) => {
			const space =
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name: `Space ${getRandomString()}`,
					type: 'Space',
				});

			apiHelpers.data.push({id: space.id, type: 'assetLibrary'});

			const contentTitle = getRandomString();

			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: contentTitle,
				},
				APPLICATION_NAME,
				space.name
			);

			await addRoleMemberAndSwitch({
				apiHelpers,
				page,
				role,
				spaceName: space.name,
				spaceSummaryPage,
			});

			await assetsPage.gotoSpaceContents(space.name);

			await expectDateActionsInMenu(
				page,
				assetsPage
					.getItem(contentTitle)
					.getByRole('button', {name: `${contentTitle} Actions`})
			);
		}
	);
}

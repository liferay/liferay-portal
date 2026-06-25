/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import fs from 'fs/promises';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import performLogin, {
	performLogout,
	userData,
} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
		'LPD-17564': {enabled: true},
	}),
	loginTest()
);

test(
	'Info Panel Versions actions',
	{tag: '@LPD-62554'},
	async ({apiHelpers, assetsPage, contentsPage, infoPanelPage, page}) => {
		const contentApplicationName = 'cms/basic-web-contents';
		const fileApplicationName = 'cms/basic-documents';
		let objectEntryContent;
		let objectEntryFile;
		const spaceName = 'Default';

		const content1 = `title ${getRandomString()}`;
		const fileNameImg = `file_${getRandomString()}.png`;
		const image1 = `title ${getRandomString()}`;

		try {
			objectEntryContent = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: content1,
				},
				contentApplicationName,
				spaceName
			);

			objectEntryFile = await apiHelpers.objectEntry.postObjectEntry(
				{
					file: {
						fileBase64:
							'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAAC0lEQVR42mNkqAcAAIUAgUW0RjgAAAAASUVORK5CYII=',
						name: fileNameImg,
					},
					objectEntryFolderExternalReferenceCode: 'L_FILES',
					title: image1,
				},
				fileApplicationName,
				'Default'
			);

			await test.step('Go to All Assets and update all the assets', async () => {
				await assetsPage.gotoAll();
				await assetsPage.execItemAction({
					action: 'Edit',
					filter: content1,
				});

				await contentsPage.publishButton.click();
				await assetsPage.execItemAction({
					action: 'Edit',
					filter: image1,
				});

				await contentsPage.publishButton.click();
			});

			await test.step('Open the Info Panel Versions of a content asset and check that the versions actions are visible', async () => {
				await assetsPage.execItemAction({
					action: 'Show Details',
					filter: content1,
				});

				await expect(
					page.getByRole('heading', {name: content1})
				).toBeVisible();

				await infoPanelPage.selectTab('More').click();
				await infoPanelPage.dropdownTab('Versions').click();

				await expect(page.getByRole('tabpanel')).toContainText(
					'Version 1'
				);
				await expect(page.getByRole('tabpanel')).toContainText(
					'Version 2'
				);

				await infoPanelPage.dropdownVersionAction('Version 2').click();

				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('Expire')
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('Make a Copy')
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('View')
				).toBeVisible();

				await infoPanelPage.dropdownVersionAction('Version 2').click();

				await infoPanelPage.dropdownVersionAction('Version 1').click();

				await expect(
					infoPanelPage.dropdownVersionActionMenuItem(
						'Restore Version'
					)
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('Expire')
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('Make a Copy')
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('Delete')
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('View')
				).toBeVisible();
			});

			await test.step('Click on the asset content version and check the functionality of the actions', async () => {
				await infoPanelPage
					.dropdownVersionActionMenuItem('View')
					.click();

				await expect(
					page.getByRole('heading', {name: `${content1} (Version 1)`})
				).toBeVisible();

				await page
					.getByLabel(`${content1} (Version 1)`)
					.getByLabel('Close')
					.click();

				await infoPanelPage.dropdownVersionAction('Version 1').click();
				await infoPanelPage
					.dropdownVersionActionMenuItem('Make a Copy')
					.click();

				await expect(
					assetsPage
						.getItem(`${content1} (Copy)`)
						.locator('input[title="Select Item"]')
				).toBeVisible();

				await page.reload();

				await assetsPage.execItemAction({
					action: 'Delete',
					filter: `${content1} (Copy)`,
				});

				await waitForAlert(
					page,
					`${content1} (Copy) was moved to the Recycle Bin.`
				);

				await assetsPage.execItemAction({
					action: 'Show Details',
					filter: `${content1}`,
				});

				await infoPanelPage.selectTab('More').click();
				await infoPanelPage.dropdownTab('Versions').click();
				await infoPanelPage.dropdownVersionAction('Version 1').click();
				await infoPanelPage
					.dropdownVersionActionMenuItem('Restore Version')
					.click();

				await waitForAlert(
					page,
					'Version 1 of the content has been successfully restored.'
				);

				await expect(infoPanelPage.selectTab('More')).toBeVisible();
				await expect(page.getByRole('tabpanel')).toContainText(
					'Version 3'
				);

				await infoPanelPage.dropdownVersionAction('Version 1').click();
				await infoPanelPage
					.dropdownVersionActionMenuItem('Expire')
					.click();

				await waitForAlert(
					page,
					'Version 1 of the content has been successfully expired.'
				);

				await expect(page.getByRole('tabpanel')).toContainText(
					'Expire'
				);

				await infoPanelPage.dropdownVersionAction('Version 1').click();

				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('Delete')
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('View')
				).toBeVisible();

				await infoPanelPage
					.dropdownVersionActionMenuItem('Delete')
					.click();

				await assetsPage.modalDeleteButton.click();

				await waitForAlert(
					page,
					'Version 1 of the content has been successfully deleted.'
				);

				await expect(
					infoPanelPage.dropdownVersionAction('Version 1')
				).not.toBeVisible();

				await assetsPage
					.getItem(content1)
					.locator('input[title="Select Item"]')
					.uncheck();
			});

			await test.step('Open the Info Panel Versions of a file asset and check that the versions actions are visible', async () => {
				await assetsPage.execItemAction({
					action: 'Show Details',
					filter: image1,
				});

				await expect(
					page.getByRole('heading', {name: image1})
				).toBeVisible();

				await infoPanelPage.selectTab('More').click();
				await infoPanelPage.dropdownTab('Versions').click();

				await expect(page.getByRole('tabpanel')).toContainText(
					'Version 1'
				);
				await expect(page.getByRole('tabpanel')).toContainText(
					'Version 2'
				);

				await infoPanelPage.dropdownVersionAction('Version 2').click();

				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('Expire')
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('Make a Copy')
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('Download')
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('View')
				).toBeVisible();

				await infoPanelPage.dropdownVersionAction('Version 2').click();

				await infoPanelPage.dropdownVersionAction('Version 1').click();

				await expect(
					infoPanelPage.dropdownVersionActionMenuItem(
						'Restore Version'
					)
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('Expire')
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('Make a Copy')
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('Download')
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('Delete')
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('View')
				).toBeVisible();
			});

			await test.step('Click on the version and check the functionality of the actions', async () => {
				await infoPanelPage
					.dropdownVersionActionMenuItem('View')
					.click();

				await expect(
					page.getByRole('heading', {name: `${image1} (Version 1)`})
				).toBeVisible();

				await page
					.getByLabel(`${image1} (Version 1)`)
					.getByLabel('Close')
					.click();

				await infoPanelPage.dropdownVersionAction('Version 1').click();

				const downloadPromise = page.waitForEvent('download');

				await infoPanelPage
					.dropdownVersionActionMenuItem('Download')
					.click();

				const download = await downloadPromise;
				expect(download.suggestedFilename()).toBe(`${fileNameImg}`);

				const downloadStat = await fs.stat(await download.path());
				expect(downloadStat.size).toBeGreaterThan(10);

				await infoPanelPage.dropdownVersionAction('Version 1').click();
				await infoPanelPage
					.dropdownVersionActionMenuItem('Make a Copy')
					.click();

				await page.reload();

				await expect(
					assetsPage
						.getItem(`${image1} (Copy)`)
						.locator('input[title="Select Item"]')
				).toBeVisible();

				await assetsPage.execItemAction({
					action: 'Delete',
					filter: `${image1} (Copy)`,
				});

				await waitForAlert(
					page,
					`${image1} (Copy) was moved to the Recycle Bin.`
				);

				await assetsPage.execItemAction({
					action: 'Show Details',
					filter: `${image1}`,
				});

				await infoPanelPage.selectTab('More').click();
				await infoPanelPage.dropdownTab('Versions').click();
				await infoPanelPage.dropdownVersionAction('Version 1').click();
				await infoPanelPage
					.dropdownVersionActionMenuItem('Restore Version')
					.click();

				await waitForAlert(
					page,
					'Version 1 of the content has been successfully restored.'
				);

				await expect(infoPanelPage.selectTab('More')).toBeVisible();
				await expect(page.getByRole('tabpanel')).toContainText(
					'Version 3'
				);

				await infoPanelPage.dropdownVersionAction('Version 1').click();
				await infoPanelPage
					.dropdownVersionActionMenuItem('Expire')
					.click();

				await waitForAlert(
					page,
					'Version 1 of the content has been successfully expired.'
				);

				await expect(page.getByRole('tabpanel')).toContainText(
					'Expire'
				);

				await infoPanelPage.dropdownVersionAction('Version 1').click();

				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('Delete')
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('View')
				).toBeVisible();

				await infoPanelPage
					.dropdownVersionActionMenuItem('Delete')
					.click();

				await assetsPage.modalDeleteButton.click();

				await waitForAlert(
					page,
					'Version 1 of the content has been successfully deleted.'
				);

				await expect(
					infoPanelPage.dropdownVersionAction('Version 1')
				).not.toBeVisible();
			});
		}
		finally {
			if (objectEntryContent) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					contentApplicationName,
					String(objectEntryContent.id)
				);
			}

			if (objectEntryFile) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					fileApplicationName,
					String(objectEntryFile.id)
				);
			}
		}
	}
);

test(
	'Versions tab should not be visible for Space Member role',
	{tag: '@LPD-86002'},
	async ({apiHelpers, assetsPage, infoPanelPage, page, spaceSummaryPage}) => {
		const contentApplicationName = 'cms/basic-web-contents';
		let objectEntryContent;
		const spaceName = 'Default';
		let user;

		const content = `title ${getRandomString()}`;

		try {
			objectEntryContent = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: content,
				},
				contentApplicationName,
				spaceName
			);

			await test.step('Create an user and add to the Space', async () => {
				user = await apiHelpers.headlessAdminUser.postUserAccount();

				userData[user.alternateName] = {
					name: user.givenName,
					password: 'test',
					surname: user.familyName,
				};

				await spaceSummaryPage.goto(spaceName);
				await spaceSummaryPage.addUserOrUserGroup(user.name, 'users');
			});

			await test.step('Login as a space member and open Info Panel', async () => {
				await performLogout(page);

				await performLogin(page, user.alternateName);

				await assetsPage.gotoAll();
				await assetsPage.execItemAction({
					action: 'Show Details',
					filter: content,
				});

				await expect(
					page.getByRole('heading', {name: content})
				).toBeVisible();
			});

			await test.step('Check versions tab is not visible', async () => {
				await expect(infoPanelPage.selectTab('More')).not.toBeVisible();
				await expect(
					infoPanelPage.selectTab('Versions')
				).not.toBeVisible();
				await expect(infoPanelPage.selectTab('Comments')).toBeVisible();
			});
		}
		finally {
			await performLogout(page);

			await performLogin(page, 'test');

			if (objectEntryContent) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					contentApplicationName,
					String(objectEntryContent.id)
				);
			}
		}
	}
);

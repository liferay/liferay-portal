/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {performUserSwitchViaApi, userData} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';

const test = mergeTests(
	dataApiHelpersTest,
	documentLibraryPagesTest,
	isolatedSiteTest,
	loginTest()
);

test(
	'Subscription and unsubscription of a File Entry',
	{tag: '@LPD-42444'},
	async ({apiHelpers, documentLibraryPage, page, site}) => {
		const fileEntryTitle =
			await test.step('Create a new File Entry', async () => {
				const fileEntry =
					await apiHelpers.headlessDelivery.postDocument(
						site.id,
						createReadStream(
							path.join(__dirname, '/dependencies/image1.jpeg')
						)
					);

				return fileEntry.title;
			});

		await test.step(`Subscription and unsubscription through File Entry Actions`, async () => {
			await documentLibraryPage.goto(site.friendlyUrlPath);
			await documentLibraryPage.goToFileEntryAction(
				'Subscribe',
				fileEntryTitle
			);
			await documentLibraryPage.goToFileEntryAction(
				'Unsubscribe',
				fileEntryTitle
			);
			await documentLibraryPage.assertFileEntryAction(
				'Subscribe',
				fileEntryTitle
			);
		});

		await test.step(`Subscription and unsubscription through File Entry Info Panel`, async () => {
			await documentLibraryPage.goto(site.friendlyUrlPath);
			await documentLibraryPage.selectFileEntry(fileEntryTitle);
			await documentLibraryPage.openInfoPanel(fileEntryTitle, 'Details');
			await expect(page.getByLabel('Subscribe')).toBeVisible();
			await page.getByLabel('Subscribe').click();

			await documentLibraryPage.goto(site.friendlyUrlPath);
			await documentLibraryPage.selectFileEntry(fileEntryTitle);
			await documentLibraryPage.openInfoPanel(fileEntryTitle, 'Details');
			await expect(page.getByLabel('Unsubscribe')).toBeVisible();
			await page.getByLabel('Unsubscribe').click();

			await documentLibraryPage.goto(site.friendlyUrlPath);
			await documentLibraryPage.selectFileEntry(fileEntryTitle);
			await documentLibraryPage.openInfoPanel(fileEntryTitle, 'Details');
			await expect(page.getByLabel('Subscribe')).toBeVisible();
		});

		await test.step(`Subscribed to a Parent Folder`, async () => {
			await documentLibraryPage.goto(site.friendlyUrlPath);
			await documentLibraryPage.openInfoPanel('Home', 'Details');
			await page.getByLabel('Subscribe').click();

			await documentLibraryPage.assertFileEntryAction(
				'Subscribed to a Parent Folder',
				fileEntryTitle
			);

			await documentLibraryPage.goto(site.friendlyUrlPath);
			await documentLibraryPage.selectFileEntry(fileEntryTitle);
			await documentLibraryPage.openInfoPanel(fileEntryTitle, 'Details');
			await expect(
				page.getByLabel('Subscribed to a Parent Folder')
			).toBeVisible();

			await documentLibraryPage.goto(site.friendlyUrlPath);
			await documentLibraryPage.openInfoPanel('Home', 'Details');
			await page.getByLabel('Unsubscribe').click();

			await documentLibraryPage.assertFileEntryAction(
				'Subscribe',
				fileEntryTitle
			);
		});
	}
);

test(
	'Subscription and unsubscription of a Folder',
	{tag: '@LPD-42444'},
	async ({apiHelpers, documentLibraryPage, page, site}) => {
		const folderName = await test.step('Create a new Folder', async () => {
			const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
				site.id
			);

			return folder.name;
		});

		await test.step(`Subscription and unsubscription through Folder Actions`, async () => {
			await documentLibraryPage.goto(site.friendlyUrlPath);
			await documentLibraryPage.goToFileEntryAction(
				'Subscribe',
				folderName
			);
			await documentLibraryPage.goToFileEntryAction(
				'Unsubscribe',
				folderName
			);
			await documentLibraryPage.assertFileEntryAction(
				'Subscribe',
				folderName
			);
		});

		await test.step(`Subscription and unsubscription through Folder Info Panel`, async () => {
			await documentLibraryPage.goto(site.friendlyUrlPath);
			await documentLibraryPage.selectFolder(folderName);
			await documentLibraryPage.openInfoPanel(folderName, 'Details');
			await expect(page.getByLabel('Subscribe')).toBeVisible();
			await page.getByLabel('Subscribe').click();

			await documentLibraryPage.goto(site.friendlyUrlPath);
			await documentLibraryPage.selectFolder(folderName);
			await documentLibraryPage.openInfoPanel(folderName, 'Details');
			await expect(page.getByLabel('Unsubscribe')).toBeVisible();
			await page.getByLabel('Unsubscribe').click();

			await documentLibraryPage.goto(site.friendlyUrlPath);
			await documentLibraryPage.selectFolder(folderName);
			await documentLibraryPage.openInfoPanel(folderName, 'Details');
			await expect(page.getByLabel('Subscribe')).toBeVisible();
		});

		await test.step(`Subscribed to a Parent Folder`, async () => {
			await documentLibraryPage.goto(site.friendlyUrlPath);
			await documentLibraryPage.openInfoPanel('Home', 'Details');
			await page.getByLabel('Subscribe').click();

			await documentLibraryPage.assertFileEntryAction(
				'Subscribed to a Parent Folder',
				folderName
			);

			await documentLibraryPage.goto(site.friendlyUrlPath);
			await documentLibraryPage.selectFolder(folderName);
			await documentLibraryPage.openInfoPanel(folderName, 'Details');
			await expect(
				page.getByLabel('Subscribed to a Parent Folder')
			).toBeVisible();

			await documentLibraryPage.goto(site.friendlyUrlPath);
			await documentLibraryPage.openInfoPanel('Home', 'Details');
			await page.getByLabel('Unsubscribe').click();

			await documentLibraryPage.assertFileEntryAction(
				'Subscribe',
				folderName
			);
		});
	}
);

test(
	'Unsubscription of the Home Folder through My Subscriptions',
	{tag: '@LPD-115150'},
	async ({apiHelpers, documentLibraryPage, page, site}) => {
		await test.step('Log in as a new Site Administrator', async () => {
			const user = await apiHelpers.headlessAdminUser.postUserAccount();

			userData[user.alternateName] = {
				name: user.givenName,
				password: 'test',
				surname: user.familyName,
			};

			const siteAdministratorRole =
				await apiHelpers.headlessAdminUser.getRoleByName(
					'Site Administrator'
				);

			await apiHelpers.headlessAdminUser.assignUserToSite(
				siteAdministratorRole.id,
				site.id,
				user.id
			);

			await performUserSwitchViaApi(page, user.alternateName);
		});

		await test.step('Subscribe to the Home Folder through the Info Panel', async () => {
			await documentLibraryPage.goto(site.friendlyUrlPath);
			await documentLibraryPage.openInfoPanel('Home', 'Details');
			await page.getByLabel('Subscribe').click();

			await waitForAlert(page);
		});

		await test.step('Unsubscribe through My Subscriptions', async () => {
			await page.getByTestId('userPersonalMenu').click();
			await page
				.getByRole('menuitem', {name: 'My Subscriptions'})
				.click();

			const row = page.getByRole('row').filter({hasText: 'Home'});

			await expect(row).toContainText('Documents Folder');

			await row.getByRole('checkbox').check();
			await page.getByRole('button', {name: 'Unsubscribe'}).click();

			await expect(
				page.getByText('No subscriptions were found.')
			).toBeVisible();
		});

		await test.step('Assert the Info Panel offers to subscribe again', async () => {
			await documentLibraryPage.goto(site.friendlyUrlPath);
			await documentLibraryPage.openInfoPanel('Home', 'Details');

			await expect(page.getByLabel('Subscribe')).toBeVisible();
		});
	}
);

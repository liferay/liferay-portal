/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import getRandomString from '../../../utils/getRandomString';
import {openFieldset} from '../../../utils/openFieldset';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {waitForAlert} from '../../../utils/waitForAlert';

const test = mergeTests(
	apiHelpersTest,
	documentLibraryPagesTest,
	loginTest(),
	isolatedSiteTest,
	usersAndOrganizationsPagesTest
);

test(
	'Can create DM folder in French language',
	{tag: '@LPD-27271'},
	async ({page, site}) => {
		const folderTitle = 'DM Folder FR';

		await page.goto(
			`/fr/group${site.friendlyUrlPath}${PORTLET_URLS.documentLibrary}`
		);
		await page.getByRole('button', {name: 'Nouveau'}).click();

		await page
			.getByRole('menuitem', {
				name: 'Répertoire',
			})
			.click();

		await page.getByRole('textbox').first().fill(folderTitle);
		await page.getByRole('button', {name: 'Enregistrer'}).click();

		await expect(page.getByRole('link', {name: folderTitle})).toBeVisible();

		// change back to english language

		await page.goto('/en');
	}
);

test(
	'Folders and documents can be edited and moved in the admin',
	{tag: '@LPS-127681'},
	async ({
		apiHelpers,
		documentLibraryEditFolderPage,
		documentLibraryPage,
		documentLibraryViewFileEntryPage,
		page,
		site,
	}) => {
		const documentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(__dirname, 'dependencies/Document_1.txt')
			),
			{fileName: 'Document_1.txt', title: documentTitle}
		);

		const folder1 = await apiHelpers.headlessDelivery.postDocumentFolder(
			site.id
		);
		const folder2 = await apiHelpers.headlessDelivery.postDocumentFolder(
			site.id
		);

		const folder2Name = getRandomString();

		await test.step('CanEditFolder', async () => {
			await documentLibraryPage.goto(site.friendlyUrlPath);

			await documentLibraryPage.goToEditFolder(folder2.name);

			await documentLibraryEditFolderPage.fillTitle(folder2Name);

			await page
				.getByLabel('Characters Maximum:')
				.fill(getRandomString());

			await documentLibraryEditFolderPage.save();

			await expect(
				page.getByRole('link', {exact: true, name: folder2.name})
			).toBeHidden();
			await expect(
				page.getByRole('link', {exact: true, name: folder2Name})
			).toBeVisible();
		});

		const selectFolderDialog = page.getByRole('dialog');

		await test.step('CanMoveFolderToFolder', async () => {
			await documentLibraryPage.goToFolderAction('Move', folder2Name);

			await selectFolderDialog
				.frameLocator('iframe')
				.getByRole('row', {name: folder1.name})
				.getByRole('button', {exact: true, name: 'Select'})
				.click();

			await waitForAlert(page);

			await expect(
				page.getByRole('link', {exact: true, name: folder2Name})
			).toBeHidden();

			await page
				.getByRole('link', {exact: true, name: folder1.name})
				.click();

			await expect(
				page.getByRole('link', {exact: true, name: folder2Name})
			).toBeVisible();
		});

		await test.step('CanMoveDocumentToFolder', async () => {
			await documentLibraryPage.goto(site.friendlyUrlPath);

			await documentLibraryPage.goToFileEntryAction(
				'Move',
				documentTitle
			);

			await selectFolderDialog
				.frameLocator('iframe')
				.getByRole('row', {name: folder1.name})
				.getByRole('button', {exact: true, name: 'Select'})
				.click();

			await waitForAlert(page);

			await expect(
				page.getByRole('link', {exact: true, name: documentTitle})
			).toBeHidden();

			await page
				.getByRole('link', {exact: true, name: folder1.name})
				.click();

			await documentLibraryPage.goToViewFileEntry(documentTitle);

			await documentLibraryViewFileEntryPage.openInfoTab();

			await expect(
				documentLibraryViewFileEntryPage.infoPanel.locator(
					'dt:text-is("Location") + dd'
				)
			).toHaveText(folder1.name);
			await expect(
				documentLibraryViewFileEntryPage.infoPanel.locator(
					'dt:text-is("Size") + dd'
				)
			).toHaveText('259 B');
		});

		await test.step('CanMoveFolderToParentWithSameName', async () => {
			const subfolder =
				await apiHelpers.headlessDelivery.postDocumentFolderDocumentFolder(
					folder1.id,
					{name: folder1.name}
				);

			await documentLibraryPage.gotoFolder({
				folderId: folder1.id,
				siteUrl: site.friendlyUrlPath,
			});

			await documentLibraryPage.goToFolderAction('Move', subfolder.name);

			const selectFolderFrame = selectFolderDialog.frameLocator('iframe');

			await selectFolderFrame
				.getByRole('link', {exact: true, name: site.name})
				.click();

			await clickAndExpectToBeHidden({
				target: selectFolderDialog,
				trigger: selectFolderFrame.getByRole('button', {
					exact: true,
					name: 'Select This Folder',
				}),
			});

			await expect(
				page.getByText(
					'The folder you selected already has an entry with this name. Please select a different folder.'
				)
			).toBeVisible();
		});

		await test.step('CanMoveFolderToRecycleBin', async () => {
			await documentLibraryPage.goto(site.friendlyUrlPath);

			await documentLibraryPage.moveFolderToRecycleBin(folder1.name);

			await expect(
				page.getByRole('link', {exact: true, name: folder1.name})
			).toBeHidden();
		});
	}
);

test(
	'Test Advance Update permission for DM folder',
	{tag: '@LPD-46006'},
	async ({
		apiHelpers,
		documentLibraryEditFolderPage,
		documentLibraryPage,
		page,
		site,
		usersAndOrganizationsPage,
	}) => {
		const testUser = await apiHelpers.headlessAdminUser.postUserAccount();

		const title = getRandomString();

		let doAsUserIdURL = '';

		await test.step('Assign the test user to the site as a Site Member', async () => {
			const role =
				await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

			await apiHelpers.headlessAdminUser.assignUserToSite(
				String(role.id),
				site.id,
				testUser.id
			);
		});

		await test.step('Create a folder granting Site Member only Advanced Update permission', async () => {
			await documentLibraryPage.goto(site.friendlyUrlPath);
			await documentLibraryPage.goToCreateNewFolder();

			await documentLibraryEditFolderPage.fillTitle(title);

			await openFieldset(page, 'Permissions');

			await page
				.getByLabel(
					'Give Update permission to users with role Site Member.'
				)
				.uncheck();

			await page
				.getByLabel(
					'Give Advanced Update permission to users with role Site Member.'
				)
				.check();

			await page.getByRole('button', {name: 'Save'}).click();

			await documentLibraryPage.waitForSuccessAlert();
		});

		await test.step('Build the edit folder URL impersonating the test user', async () => {
			await documentLibraryPage.goToEditFolder(title);

			await expect(documentLibraryEditFolderPage.title).toBeEnabled();

			const editFolderURL = page.url();

			await usersAndOrganizationsPage.goto();

			await usersAndOrganizationsPage.usersSearchBar.fill(
				testUser.alternateName
			);

			await usersAndOrganizationsPage.usersSearchBar.press('Enter');

			const impersonateLink = page.locator('a[href*="doAsUserId"]');

			await expect(impersonateLink).toHaveCount(1);

			const impersonateURL = new URL(
				await impersonateLink.getAttribute('href'),
				page.url()
			);

			doAsUserIdURL = `${editFolderURL}&doAsUserId=${impersonateURL.searchParams.get(
				'doAsUserId'
			)}`;
		});

		await test.step('Verify Advanced Update disables the folder name', async () => {
			await page.goto(doAsUserIdURL);

			await expect(documentLibraryEditFolderPage.title).toBeDisabled();

			await expect(
				page.getByRole('button', {
					name: 'Document Type Restrictions',
				})
			).toBeVisible();
		});

		await test.step('Grant Update and remove Advanced Update permission', async () => {
			await documentLibraryPage.goto(site.friendlyUrlPath);
			await documentLibraryPage.goToFolderAction('Permissions', title);

			await documentLibraryPage.permissionsFrameLocator
				.locator('#site-member_ACTION_UPDATE')
				.check();

			await documentLibraryPage.permissionsFrameLocator
				.locator('#site-member_ACTION_ADVANCED_UPDATE')
				.uncheck();

			await documentLibraryPage.permissionsFrameLocator
				.getByRole('button', {name: 'Save'})
				.click();

			await clickAndExpectToBeHidden({
				target: page.locator('.modal'),
				trigger: page
					.locator('.modal')
					.getByLabel('Close', {exact: true}),
			});
		});

		await test.step('Verify Update enables the folder name', async () => {
			await page.goto(doAsUserIdURL);

			await expect(documentLibraryEditFolderPage.title).toBeEnabled();

			await expect(
				page.getByRole('button', {
					name: 'Document Type Restrictions',
				})
			).toBeHidden();
		});
	}
);

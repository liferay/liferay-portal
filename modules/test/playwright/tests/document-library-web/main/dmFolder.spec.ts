/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {openFieldset} from '../../../utils/openFieldset';
import {PORTLET_URLS} from '../../../utils/portletUrls';

const test = mergeTests(
	apiHelpersTest,
	documentLibraryPagesTest,
	loginTest(),
	isolatedSiteTest
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
	'Test Advance Update permission for DM folder',
	{tag: '@LPD-46006'},
	async ({
		apiHelpers,
		documentLibraryEditFolderPage,
		documentLibraryPage,
		page,
		site,
	}) => {
		const testUser = await apiHelpers.headlessAdminUser.postUserAccount();

		const role =
			await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

		await apiHelpers.headlessAdminUser.assignUserToSite(
			String(role.id),
			site.id,
			testUser.id
		);

		await documentLibraryPage.goto(site.friendlyUrlPath);
		await documentLibraryPage.goToCreateNewFolder();

		const title = getRandomString();

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

		await documentLibraryPage.goToEditFolder(title);

		await page.waitForURL(/edit_folder/);

		const doAsUserIdURL = `${page.url()}&doAsUserId=${testUser.id}`;

		await page.goto(doAsUserIdURL);

		await expect(documentLibraryEditFolderPage.title).toBeDisabled();
		await expect(
			page.getByRole('button', {name: 'Document Type Restrictions'})
		).toBeVisible();

		await documentLibraryPage.goto(site.friendlyUrlPath);
		await documentLibraryPage.goToFolderAction('Permissions', title);

		const permissionIframe = page.frameLocator(
			'iframe[title="Permissions"]'
		);

		await permissionIframe.locator('#site-member_ACTION_UPDATE').check();

		await permissionIframe
			.locator('#site-member_ACTION_ADVANCED_UPDATE')
			.uncheck();

		await permissionIframe.getByRole('button', {name: 'Save'}).click();

		await page.locator('.modal').getByLabel('Close', {exact: true}).click();

		await page.goto(doAsUserIdURL);

		await expect(documentLibraryEditFolderPage.title).toBeEnabled();
		await expect(
			page.getByRole('button', {name: 'Document Type Restrictions'})
		).toBeHidden();
	}
);

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {
	performLogout,
	performUserSwitch,
	userData,
} from '../../../utils/performLogin';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';

const test = mergeTests(
	dataApiHelpersTest,
	documentLibraryPagesTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

test(
	'DM Permissions initial status',
	{tag: ['@LPD-39898', '@LPD-39899']},
	async ({documentLibraryEditFilePage, page, site}) => {
		await documentLibraryEditFilePage.goto(site.friendlyUrlPath);

		await documentLibraryEditFilePage.openFieldset('Permissions');

		await expect(
			page.getByText('Viewable and Downloadable By')
		).toBeVisible();
		await expect(
			page.getByRole('columnheader', {exact: true, name: 'Role'})
		).toBeVisible();
	}
);

test(
	'View and Download permissions are linked in DM',
	{tag: '@LPD-39724'},
	async ({documentLibraryEditFilePage, documentLibraryPage, site}) => {
		const title = getRandomString();

		await documentLibraryEditFilePage.publishNewFileWithOwnerViewPermission(
			title,
			site.friendlyUrlPath
		);

		await documentLibraryPage.assertFileEntryPermissions(
			[
				{enabled: false, locator: '#guest_ACTION_DOWNLOAD'},
				{enabled: false, locator: '#guest_ACTION_VIEW'},
				{enabled: false, locator: '#site-member_ACTION_DOWNLOAD'},
				{enabled: false, locator: '#site-member_ACTION_VIEW'},
				{enabled: true, locator: '#owner_ACTION_DOWNLOAD'},
				{enabled: true, locator: '#owner_ACTION_VIEW'},
			],
			title
		);
	}
);

test(
	'A site member does not see the actions their permissions do not allow',
	{tag: '@LPD-113625'},
	async ({apiHelpers, page, site}) => {
		const documentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(path.join(__dirname, '/dependencies/image1.jpeg')),
			{fileName: documentTitle + '.jpeg', title: documentTitle}
		);

		const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
			site.id
		);

		const shortcutTargetTitle = getRandomString();

		const shortcutTarget =
			await apiHelpers.headlessDelivery.postDocumentFolderDocument(
				folder.id,
				createReadStream(
					path.join(__dirname, '/dependencies/image1.jpeg')
				),
				{
					fileName: shortcutTargetTitle + '.jpeg',
					title: shortcutTargetTitle,
				}
			);

		await apiHelpers.headlessDelivery.postDocumentShortcut(site.id, {
			targetDocumentId: shortcutTarget.id,
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetConfig: {showActions: 'true'},
					widgetName:
						'com_liferay_document_library_web_portlet_DLPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await page.goto(`/web${site.friendlyUrlPath}`);

		const companyId = await page.evaluate(() =>
			String(Liferay.ThemeDisplay.getCompanyId())
		);

		const siteMemberRole = await apiHelpers.jsonWebServicesRole.getRole(
			companyId,
			'Site Member'
		);

		await test.step('Remove Subscribe on the folder for site members', async () => {
			await apiHelpers.jsonWebServicesResourcePermissionApiHelper.removeResourcePermission(
				'SUBSCRIBE',
				companyId,
				String(site.id),
				'com.liferay.document.library.kernel.model.DLFolder',
				String(folder.id),
				String(siteMemberRole.roleId),
				'4'
			);
		});

		const member = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToSite(
			Number(siteMemberRole.roleId),
			site.id,
			member.id
		);

		userData[member.alternateName] = {
			name: member.givenName,
			password: 'test',
			surname: member.familyName,
		};

		await performUserSwitch(page, member.alternateName);

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		const assertActions = async (
			entryTitle: string,
			triggerLabel: string,
			visibleAction: RegExp | string,
			hiddenActions: string[]
		) => {
			await clickAndExpectToBeVisible({
				target: page.getByRole('menuitem', {
					exact: true,
					name: visibleAction,
				}),
				trigger: page
					.locator(`.card-body:has-text('${entryTitle}')`)
					.getByLabel(triggerLabel),
			});

			for (const hiddenAction of hiddenActions) {
				await expect(
					page.getByRole('menuitem', {
						exact: true,
						name: hiddenAction,
					})
				).toBeHidden();
			}

			await page.keyboard.press('Escape');
		};

		await test.step('The document has no Delete, Edit or Permissions', async () => {
			await assertActions(documentTitle, 'Actions', 'Copy To', [
				'Delete',
				'Edit',
				'Permissions',
			]);
		});

		await test.step('The shortcut has no Delete or Edit', async () => {
			await assertActions(shortcutTargetTitle, 'Actions', 'Copy To', [
				'Delete',
				'Edit',
			]);
		});

		await test.step('The folder has no Delete, Edit, Permissions or Subscribe', async () => {
			await assertActions(folder.name, 'More actions', 'Copy To', [
				'Delete',
				'Edit',
				'Permissions',
				'Subscribe',
			]);
		});

		await test.step('A guest sees no actions that change the entries', async () => {
			await performLogout(page);

			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await expect(page.getByRole('button', {name: 'New'})).toBeHidden();

			await assertActions(documentTitle, 'Actions', /^Download/, [
				'Delete',
				'Edit',
			]);

			await assertActions(folder.name, 'More actions', /^Download/, [
				'Delete',
				'Edit',
				'Subscribe',
			]);
		});
	}
);

test(
	'The New menu follows the add document and add shortcut permissions',
	{tag: '@LPD-113625'},
	async ({apiHelpers, page, site}) => {
		const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
			site.id
		);

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetConfig: {showActions: 'true'},
					widgetName:
						'com_liferay_document_library_web_portlet_DLPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const pageURL = `/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`;

		await page.goto(pageURL);

		const companyId = await page.evaluate(() =>
			String(Liferay.ThemeDisplay.getCompanyId())
		);

		const siteMemberRole = await apiHelpers.jsonWebServicesRole.getRole(
			companyId,
			'Site Member'
		);

		const removePermission = async (
			actionId: string,
			name: string,
			primKey: string
		) => {
			await apiHelpers.jsonWebServicesResourcePermissionApiHelper.removeResourcePermission(
				actionId,
				companyId,
				String(site.id),
				name,
				primKey,
				String(siteMemberRole.roleId),
				'4'
			);
		};

		const newButton = page.getByRole('button', {name: 'New'});

		const assertNewMenu = async (
			visibleItems: string[],
			hiddenItems: string[]
		) => {
			await clickAndExpectToBeVisible({
				target: page.getByRole('menuitem', {
					exact: true,
					name: visibleItems[0],
				}),
				timeout: 2000,
				trigger: newButton,
			});

			for (const visibleItem of visibleItems) {
				await expect(
					page.getByRole('menuitem', {exact: true, name: visibleItem})
				).toBeVisible();
			}

			for (const hiddenItem of hiddenItems) {
				await expect(
					page.getByRole('menuitem', {exact: true, name: hiddenItem})
				).toBeHidden();
			}

			await page.keyboard.press('Escape');
		};

		const addUser = async () => {
			const user = await apiHelpers.headlessAdminUser.postUserAccount();

			userData[user.alternateName] = {
				name: user.givenName,
				password: 'test',
				surname: user.familyName,
			};

			return user;
		};

		const viewAs = async (alternateName: string, path = pageURL) => {
			await performUserSwitch(page, alternateName);

			await page.goto(path);

			await expect(page.getByText(folder.name).first()).toBeVisible();
		};

		const addDocumentRolePermissions = (
			primaryKey: string,
			scope: number
		) => [
			{
				actionIds: ['ADD_DOCUMENT'],
				primaryKey,
				resourceName: 'com.liferay.document.library',
				scope,
			},
		];

		const regularRole = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
		});
		const regularRoleUser = await addUser();

		await apiHelpers.headlessAdminUser.postRoleUserAccountAssociation(
			regularRole.id,
			Number(regularRoleUser.id)
		);

		const addDocumentRole = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			rolePermissions: addDocumentRolePermissions(companyId, 1),
		});
		const addDocumentUser = await addUser();

		await apiHelpers.headlessAdminUser.postRoleUserAccountAssociation(
			addDocumentRole.id,
			Number(addDocumentUser.id)
		);

		const siteMember = await addUser();

		await apiHelpers.headlessAdminUser.assignUserToSite(
			Number(siteMemberRole.roleId),
			site.id,
			siteMember.id
		);

		const siteRole = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			rolePermissions: addDocumentRolePermissions('0', 3),
			roleType: 'site',
		});

		await test.step('A regular role needs the add document permission', async () => {
			await viewAs(regularRoleUser.alternateName);

			await expect(newButton).toBeHidden();

			await viewAs(addDocumentUser.alternateName);

			await assertNewMenu(['File Upload'], []);

			await performUserSwitch(page, 'test');

			await apiHelpers.headlessAdminUser.deleteRoleUserAccountAssociation(
				addDocumentRole.id,
				Number(addDocumentUser.id)
			);

			await viewAs(addDocumentUser.alternateName);

			await expect(newButton).toBeHidden();
		});

		await test.step('A site member loses the items whose permissions are removed', async () => {
			await viewAs(siteMember.alternateName);

			await assertNewMenu(['File Upload', 'Shortcut'], []);

			await performUserSwitch(page, 'test');

			await removePermission(
				'ADD_DOCUMENT',
				'com.liferay.document.library.kernel.model.DLFolder',
				String(folder.id)
			);

			await viewAs(siteMember.alternateName);

			await page.getByRole('link', {name: folder.name}).click();

			await assertNewMenu(
				['Folder'],
				['File Upload', 'Multiple Files Upload']
			);

			await performUserSwitch(page, 'test');

			for (const actionId of ['ADD_DOCUMENT', 'ADD_SHORTCUT']) {
				await removePermission(
					actionId,
					'com.liferay.document.library',
					String(site.id)
				);
			}

			await viewAs(siteMember.alternateName);

			await expect(newButton).toBeHidden();

			await performUserSwitch(page, 'test');

			await apiHelpers.headlessAdminUser.assignUserToSite(
				siteRole.id,
				site.id,
				siteMember.id
			);

			await viewAs(siteMember.alternateName);

			await assertNewMenu(['File Upload'], ['Shortcut']);
		});
	}
);

test(
	'A document can still be viewed after its author is deleted',
	{tag: ['@LPD-113625', '@LPS-93031']},
	async ({apiHelpers, documentLibraryPage, page, site}) => {
		await page.goto(`/web${site.friendlyUrlPath}`);

		const companyId = await page.evaluate(() =>
			String(Liferay.ThemeDisplay.getCompanyId())
		);

		const administratorRole = await apiHelpers.jsonWebServicesRole.getRole(
			companyId,
			'Administrator'
		);

		const author = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.postRoleUserAccountAssociation(
			Number(administratorRole.roleId),
			Number(author.id)
		);

		userData[author.alternateName] = {
			name: author.givenName,
			password: 'test',
			surname: author.familyName,
		};

		const documentTitle = getRandomString();

		await test.step('The author adds a document', async () => {
			await performUserSwitch(page, author.alternateName);

			await apiHelpers.headlessDelivery.postDocument(
				site.id,
				createReadStream(
					path.join(__dirname, '/dependencies/image1.jpeg')
				),
				{fileName: documentTitle + '.jpeg', title: documentTitle}
			);
		});

		await test.step('The author is deleted and the document still opens', async () => {
			await performUserSwitch(page, 'test');

			await apiHelpers.headlessAdminUser.deleteUserAccount(
				Number(author.id)
			);

			await documentLibraryPage.goto(site.friendlyUrlPath);

			await documentLibraryPage.goToViewFileEntry(documentTitle);

			await expect(
				page.getByText('Download', {exact: true}).first()
			).toBeVisible();
		});
	}
);

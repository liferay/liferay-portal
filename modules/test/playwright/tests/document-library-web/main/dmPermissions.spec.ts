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
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch, userData} from '../../../utils/performLogin';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';

const test = mergeTests(
	apiHelpersTest,
	documentLibraryPagesTest,
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
			visibleAction: string,
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
					page.getByRole('menuitem', {exact: true, name: hiddenAction})
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
	}
);

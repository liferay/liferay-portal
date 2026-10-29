/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
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
import {DocumentLibraryViewFileEntryPage} from '../../../pages/document-library-web/DocumentLibraryViewFileEntryPage';
import {WebContentPage} from '../../../pages/journal-web/WebContentPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitchViaApi} from '../../../utils/performLogin';
import {
	createRecipient,
	withRecipientPage,
} from '../../../utils/sharingRecipient';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../../utils/waitForAlert';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import addConnectedDepot from './utils/addConnectedDepot';

const test = mergeTests(
	dataApiHelpersTest,
	documentLibraryPagesTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	journalPagesTest,
	loginTest()
);

const USERS_ADMIN_NAMESPACE =
	'_com_liferay_users_admin_web_portlet_UsersAdminPortlet_';

test(
	'A connected site member gets the depot permissions of the connected site member role',
	{tag: '@LPD-109574'},
	async ({apiHelpers, browser, journalPage, page, site}) => {
		const assetLibrary = await addConnectedDepot(apiHelpers, site);

		const user = await createRecipient(apiHelpers);

		await apiHelpers.jsonWebServicesUser.assignUsersToSite(
			String(site.id),
			String(user.id)
		);

		// The connected site member role is not listed in the user roles

		await page.goto(
			`/group/control_panel/manage?p_p_id=com_liferay_users_admin_web_portlet_UsersAdminPortlet&${USERS_ADMIN_NAMESPACE}mvcRenderCommandName=%2Fusers_admin%2Fedit_user&${USERS_ADMIN_NAMESPACE}p_u_i_d=${user.id}&${USERS_ADMIN_NAMESPACE}screenNavigationCategoryKey=general&${USERS_ADMIN_NAMESPACE}screenNavigationEntryKey=roles`
		);

		await expect(
			page.getByText(
				'This user is not assigned any asset library or space roles.'
			)
		).toBeVisible();

		// Display a depot web content only its owner can view

		const content = getRandomString();
		const title = getRandomString();

		const structuredContent =
			await apiHelpers.headlessDelivery.postStructuredContent({
				contentFields: [
					{contentFieldValue: {data: content}, name: 'content'},
				],
				contentStructureId:
					await getBasicWebContentStructureId(apiHelpers),
				datePublished: null,
				siteId: String(assetLibrary.siteId),
				title,
				viewableBy: 'Owner',
			});

		const layoutTitle = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetConfig: {
						articleId: structuredContent.key,
						groupId: String(assetLibrary.siteId),
					},
					widgetName:
						'com_liferay_journal_content_web_portlet_JournalContentPortlet',
				}),
			]),
			siteId: site.id,
			title: layoutTitle,
		});

		const layoutURL = `/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`;

		await withRecipientPage(
			browser,
			user.alternateName,
			async (recipientPage) => {
				await recipientPage.goto(layoutURL);

				await expect(recipientPage).toHaveTitle(
					new RegExp(layoutTitle)
				);
				await expect(recipientPage.getByText(content)).toHaveCount(0);
			}
		);

		// Grant view and update to the connected site member role

		await journalPage.goto(`/asset-library-${assetLibrary.id}`);

		// The display style is a preference of the user

		await journalPage.changeView('list');

		await journalPage.goToJournalArticleAction('Permissions', title);

		for (const action of ['UPDATE', 'VIEW']) {
			await journalPage.permissionsFrameLocator
				.locator(
					`#asset-library-connected-site-member_ACTION_${action}`
				)
				.check();
		}

		await journalPage.permissionsFrameLocator
			.getByRole('button', {name: 'Save'})
			.click();

		await waitForAlert(journalPage.permissionsFrameLocator);

		await withRecipientPage(
			browser,
			user.alternateName,
			async (recipientPage) => {
				await recipientPage.goto(layoutURL);

				await expect(recipientPage.getByText(content)).toBeVisible();

				// The actions are only visible on hover

				await recipientPage.getByText(content).hover();

				await clickAndExpectToBeVisible({
					target: recipientPage.getByRole('menuitem', {
						name: 'Edit Web Content',
					}),
					timeout: 1000,
					trigger: recipientPage.getByLabel('Show Actions'),
				});
			}
		);
	}
);

test(
	'A depot role grants its web content and document permissions to a depot member',
	{tag: '@LPD-109574'},
	async ({
		apiHelpers,
		documentLibraryEditFilePage,
		documentLibraryPage,
		journalEditArticlePage,
		journalPage,
		page,
	}) => {
		const assetLibrary =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary();

		// The member does not own these, so only the depot role grants access

		const webContentTitle = getRandomString();

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId: await getBasicWebContentStructureId(apiHelpers),
			groupId: assetLibrary.siteId,
			titleMap: {en_US: webContentTitle},
		});

		const documentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postAssetLibraryDocument(
			assetLibrary.id,
			createReadStream(path.join(__dirname, 'dependencies/Document.jpg')),
			{fileName: `${documentTitle}.jpg`, title: documentTitle}
		);

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			rolePermissions: [
				[
					'com.liferay.depot.model.DepotEntry',
					['VIEW_SITE_ADMINISTRATION'],
				],
				['com.liferay.document.library', ['VIEW']],
				[
					'com.liferay.document.library.kernel.model.DLFileEntry',
					['UPDATE', 'VIEW'],
				],
				['com.liferay.journal', ['ADD_ARTICLE', 'VIEW']],
				[
					'com.liferay.journal.model.JournalArticle',
					['DELETE', 'UPDATE', 'VIEW'],
				],
				[
					'com_liferay_document_library_web_portlet_DLAdminPortlet',
					['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
				],
				[
					'com_liferay_journal_web_portlet_JournalPortlet',
					['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
				],
			].map(([resourceName, actionIds]) => ({
				actionIds: actionIds as string[],
				primaryKey: '0',
				resourceName: resourceName as string,
				scope: 3,
			})),
			roleType: 'depot',
		});

		const user = await createRecipient(apiHelpers);

		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
			assetLibrary.externalReferenceCode,
			user.externalReferenceCode
		);
		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccountRoles(
			assetLibrary.externalReferenceCode,
			user.externalReferenceCode,
			[role.name]
		);

		await performUserSwitchViaApi(page, user.alternateName);

		const depotURL = `/asset-library-${assetLibrary.id}`;

		await test.step('Add, edit and recycle web content', async () => {
			await journalPage.goto(depotURL);

			// With a single structure available, New is a link to the editor

			await page.getByRole('link', {name: 'Basic Web Content'}).click();

			const title = getRandomString();

			await journalEditArticlePage.fillTitle(title);
			await journalEditArticlePage.fillContent(getRandomString());

			await journalEditArticlePage.publishArticle();

			// The display style is a preference of the user

			await journalPage.changeView('list');

			const webContentPage = new WebContentPage(page);

			await webContentPage.assertEntryPresent(title);

			await journalEditArticlePage.editArticle(webContentTitle);

			const editedContent = getRandomString();
			const editedTitle = getRandomString();

			await journalEditArticlePage.fillTitle(editedTitle);
			await journalEditArticlePage.fillContent(editedContent);

			await journalEditArticlePage.publishArticle(true);

			await journalEditArticlePage.editArticle(editedTitle);

			await expect(journalEditArticlePage.titleInput).toHaveValue(
				editedTitle
			);
			await expect(journalPage.articleContentTextBox).toContainText(
				editedContent
			);

			await journalPage.goto(depotURL);

			await webContentPage.moveToRecycleBin(editedTitle);

			await webContentPage.assertEntryAbsent(editedTitle);
		});

		await test.step('Edit a document without adding one', async () => {
			await documentLibraryPage.goto(depotURL);

			await expect(
				page.getByText(documentTitle, {exact: true})
			).toBeVisible();
			await expect(
				page.getByRole('button', {exact: true, name: 'New'})
			).toHaveCount(0);

			await documentLibraryPage.goToEditFileEntry(documentTitle);

			const editedTitle = getRandomString();

			await documentLibraryEditFilePage.titleSelector.fill(editedTitle);

			await documentLibraryEditFilePage.publishFileEntry();

			// Publishing goes back to the document view

			const documentLibraryViewFileEntryPage =
				new DocumentLibraryViewFileEntryPage(page);

			await documentLibraryViewFileEntryPage.openInfoPanel(
				editedTitle,
				'Details'
			);

			await expect(
				documentLibraryViewFileEntryPage.infoPanel
			).toContainText(/Version\s*1\.1/);
		});
	}
);

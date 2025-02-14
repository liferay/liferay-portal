/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'node:path';

import {applicationsMenuPageTest} from '../../fixtures/applicationsMenuPageTest';
import {contactsCenterPagesTest} from '../../fixtures/contactsCenterPagesTest';
import {dataApiHelpersTest} from '../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../fixtures/isolatedSiteTest';
import {loginTest} from '../../fixtures/loginTest';
import {productMenuPageTest} from '../../fixtures/productMenuPageTest';
import {siteStagingPageTest} from '../../fixtures/siteStagingPageTest';
import {usersAndOrganizationsPagesTest} from '../../fixtures/usersAndOrganizationsPagesTest';
import {getRandomInt} from '../../utils/getRandomInt';
import getRandomString from '../../utils/getRandomString';
import performLogin, {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../utils/performLogin';
import {PORTLET_URLS} from '../../utils/portletUrls';
import getBasicWebContentStructureId from '../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../utils/waitForAlert';
import {blogsPagesTest} from '../blogs-web/fixtures/blogsPagesTest';
import {journalPagesTest} from '../journal-web/fixtures/journalPagesTest';

export const test = mergeTests(
	contactsCenterPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-35013': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	loginTest({screenName: 'demo.company.admin'}),
	usersAndOrganizationsPagesTest
);

export const testAdmin = mergeTests(
	applicationsMenuPageTest,
	blogsPagesTest,
	contactsCenterPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-35013': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	journalPagesTest,
	loginTest(),
	productMenuPageTest,
	siteStagingPageTest,
	usersAndOrganizationsPagesTest
);

async function checkUsernameAssociatedWithObjects(
	counters: {
		blog: number;
		document: number;
		message: number;
		webContent: number;
		wiki: number;
	},
	page: Page,
	userAccountName: string,
	siteName: string,
	wikiNodeName: string
) {
	const portletBaseURL = `/group/${siteName}`;

	await page.goto(`${portletBaseURL}${PORTLET_URLS.journal}`);
	await expect(page.getByText(userAccountName)).toHaveCount(
		counters.webContent
	);

	await page.goto(`${portletBaseURL}${PORTLET_URLS.blogs}`);
	await expect(page.getByText(userAccountName)).toHaveCount(counters.blog);

	await page.goto(`${portletBaseURL}${PORTLET_URLS.documentLibrary}`);
	await expect(page.getByText(userAccountName)).toHaveCount(
		counters.document
	);

	await page.goto(`${portletBaseURL}${PORTLET_URLS.messageBoardsAdmin}`);
	await expect(page.getByText(userAccountName)).toHaveCount(counters.message);

	await page.goto(`${portletBaseURL}${PORTLET_URLS.wikiAdmin}`);
	await page.getByRole('link', {name: wikiNodeName}).click();
	await expect(page.getByText(userAccountName)).toHaveCount(counters.wiki);
}

test.describe('LPD-25858 Refactor of GDPR#CanExportMultipleEntries', () => {
	test('Exports multiple entries from different applications', async ({
		apiHelpers,
		contactsCenterPage,
		exportUserDataPage,
		page,
		usersAndOrganizationsPage,
	}) => {
		test.setTimeout(120000);

		const site = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

		await contactsCenterPage.createPage(apiHelpers, site.id, {
			title: 'contact',
		});

		await page.goto(`/web/${site.name}/contact`);

		await contactsCenterPage.addContactButton.click();
		await contactsCenterPage.nameInput.fill(getRandomString());
		await contactsCenterPage.emailAddressInput.fill(
			`${getRandomString()}@liferay.com`
		);
		await contactsCenterPage.saveButton.click();

		await expect(contactsCenterPage.successMessage).toBeVisible();

		const announcement =
			await apiHelpers.jsonWebServicesAnnouncementsEntryApiHelper.addEntry();

		apiHelpers.data.push({id: announcement.entryId, type: 'announcement'});

		await apiHelpers.headlessDelivery.postBlog(site.id);

		const contentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId: contentStructureId,
			groupId: site.id,
		});

		const folder = await apiHelpers.jsonWebServicesJournal.addFolder({
			groupId: site.id,
		});

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId: contentStructureId,
			folderId: folder.folderId,
			groupId: site.id,
		});

		await apiHelpers.jsonWebServicesMBApiHelper.addMessage({
			groupId: site.id,
		});

		const wikiNode = await apiHelpers.headlessDelivery.postWikiNode(
			site.id
		);

		await apiHelpers.headlessDelivery.postWikiPage(wikiNode.id);

		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(__dirname, '/dependencies/attachment.txt')
			)
		);

		await performLogout(page);

		await performLogin(page, 'test');

		await page.goto(`/web/${site.name}`);

		await usersAndOrganizationsPage.goToUsers(false);

		await (
			await usersAndOrganizationsPage.usersTableRowActions(
				'demo.company.admin'
			)
		).click();
		await usersAndOrganizationsPage.exportPersonalDataItem.click();

		await exportUserDataPage.addExportProcessesButton.click();

		await exportUserDataPage.announcementsCheckbox.check();
		await exportUserDataPage.blogsCheckbox.check();
		await exportUserDataPage.contactsCenterCheckbox.check();
		await exportUserDataPage.documentsAndMediaCheckbox.check();
		await exportUserDataPage.messageBoardsCheckbox.check();
		await exportUserDataPage.webContentCheckbox.check();
		await exportUserDataPage.wikiCheckbox.check();

		await exportUserDataPage.exportButton.click();

		await expect(exportUserDataPage.announcementsStatus).toBeVisible();
		await expect(exportUserDataPage.blogsStatus).toBeVisible();
		await expect(exportUserDataPage.contactsCenterStatus).toBeVisible();
		await expect(exportUserDataPage.documentsAndMediaStatus).toBeVisible();
		await expect(exportUserDataPage.messageBoardsStatus).toBeVisible();
		await expect(exportUserDataPage.webContentStatus).toBeVisible();
		await expect(exportUserDataPage.wikiStatus).toBeVisible();

		await exportUserDataPage.creationMenuNewButton.click();

		await expect(exportUserDataPage.announcementsCheckbox).toBeVisible();
	});
});

testAdmin.describe('LPD-27068 Refactor of GDPR#CanAnonymizeAllEntries', () => {
	testAdmin(
		'Adds entries from different applications and anonymizes all entries via Select All',
		async ({
			apiHelpers,
			page,
			personalDataErasurePage,
			usersAndOrganizationsPage,
		}) => {
			testAdmin.setTimeout(120000);

			const userAccount =
				await apiHelpers.headlessAdminUser.postUserAccount();

			userData[userAccount.alternateName] = {
				name: userAccount.givenName,
				password: 'test',
				surname: userAccount.familyName,
			};

			const role =
				await apiHelpers.headlessAdminUser.getRoleByName(
					'Administrator'
				);

			await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
				role.externalReferenceCode,
				userAccount.id
			);

			await performLogout(page);

			await performLogin(page, userAccount.alternateName);

			const site = await apiHelpers.headlessSite.createSite({
				name: getRandomString(),
			});

			apiHelpers.data.push({id: site.id, type: 'site'});

			await apiHelpers.headlessDelivery.postBlog(site.id);

			const contentStructureId =
				await getBasicWebContentStructureId(apiHelpers);

			const folder = await apiHelpers.jsonWebServicesJournal.addFolder({
				groupId: site.id,
			});

			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: contentStructureId,
				groupId: site.id,
			});

			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: contentStructureId,
				folderId: folder.folderId,
				groupId: site.id,
			});

			await apiHelpers.headlessDelivery.postDocument(
				site.id,
				createReadStream(
					path.join(__dirname, '/dependencies/attachment.txt')
				)
			);

			await apiHelpers.jsonWebServicesMBApiHelper.addMessage({
				groupId: site.id,
			});

			const wikiNode = await apiHelpers.headlessDelivery.postWikiNode(
				site.id
			);

			await apiHelpers.headlessDelivery.postWikiPage(wikiNode.id);

			await performLogout(page);

			await performLogin(page, 'test');

			await page.goto(`/web/${site.name}`);

			await checkUsernameAssociatedWithObjects(
				{blog: 1, document: 1, message: 1, webContent: 2, wiki: 1},
				page,
				userAccount.name,
				site.name,
				wikiNode.name
			);

			await usersAndOrganizationsPage.goToUsers(false);
			await (
				await usersAndOrganizationsPage.usersTableRowActions(
					userAccount.alternateName
				)
			).click();

			page.on('dialog', (dialog) => {
				dialog.accept().catch(() => {});
			});

			await usersAndOrganizationsPage.deletePersonalDataMenuItem.click();

			await personalDataErasurePage.selectAllItemsOnPageCheckbox.check();
			await personalDataErasurePage.allSelectedButton.click();

			await personalDataErasurePage.anonymizeMenuItem.click();

			await personalDataErasurePage.anonymizeButton.click();

			await checkUsernameAssociatedWithObjects(
				{blog: 0, document: 0, message: 0, webContent: 0, wiki: 0},
				page,
				userAccount.name,
				site.name,
				wikiNode.name
			);
		}
	);
});

testAdmin(
	'LPD-31206 Can delete a single staged and live blogs entry',
	async ({
		apiHelpers,
		blogsPage,
		page,
		personalDataErasurePage,
		productMenuPage,
		siteStagingPage,
		usersAndOrganizationsPage,
	}) => {
		testAdmin.setTimeout(120000);

		page.on('dialog', (dialog) => {
			dialog.accept();
		});

		const userAccount =
			await apiHelpers.headlessAdminUser.postUserAccount();

		userData[userAccount.alternateName] = {
			name: userAccount.givenName,
			password: 'test',
			surname: userAccount.familyName,
		};

		const site = await apiHelpers.headlessSite.createSite({
			name: 'Site' + getRandomInt(),
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: 'Page' + getRandomInt(),
		});

		const role =
			await apiHelpers.headlessAdminUser.getRoleByName('Administrator');

		await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
			role.externalReferenceCode,
			userAccount.id
		);

		await performLogout(page);
		await performLogin(page, userAccount.alternateName);

		const blog1Name = 'Blog1';
		const blog2Name = 'Blog2';
		const blog3Name = 'Blog3';

		const blog1 = await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: blog1Name,
		});
		const blog2 = await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: blog2Name,
		});
		await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: blog3Name,
		});

		await page.goto(`/group/${site.name}/${layout.friendlyUrlPath}`);

		await productMenuPage.openProductMenuButton.click();
		await productMenuPage.publishingButton.click();
		await productMenuPage.stagingMenuItem.click();

		await siteStagingPage.localStagingCheckbox.check();
		await siteStagingPage.blogsCheckbox.check();
		await siteStagingPage.saveButton.click();

		await waitForAlert(page, 'Local staging is successfully enabled.');

		await performLogout(page);
		await performLogin(page, 'test');

		await usersAndOrganizationsPage.goToUsers(false);
		await (
			await usersAndOrganizationsPage.usersTableRowActions(
				userAccount.alternateName
			)
		).click();

		await usersAndOrganizationsPage.deletePersonalDataMenuItem.click();

		await expect(
			personalDataErasurePage.selectAllItemsOnPageCheckbox
		).toBeVisible();

		await personalDataErasurePage.objectCountLink('6').click();

		await personalDataErasurePage
			.blogCheckBox(blog1.id, blog1Name, true)
			.check();
		await personalDataErasurePage
			.blogCheckBox(blog2.id, blog2Name, false)
			.check();

		await personalDataErasurePage.actionsButton.click();
		await personalDataErasurePage.menuItemDelete.click();

		await expect(
			personalDataErasurePage.selectAllItemsOnPageCheckbox
		).toBeVisible();

		await page.goto(`/group/${site.name}-staging${PORTLET_URLS.blogs}`);

		await expect(blogsPage.blogName(blog1Name)).toHaveCount(1);
		await expect(blogsPage.blogName(blog2Name)).toHaveCount(0);
		await expect(blogsPage.blogName(blog3Name)).toHaveCount(1);

		await page.goto(`/group/${site.name}${PORTLET_URLS.blogs}`);

		await expect(blogsPage.blogName(blog1Name)).toHaveCount(0);
		await expect(blogsPage.blogName(blog2Name)).toHaveCount(1);
		await expect(blogsPage.blogName(blog3Name)).toHaveCount(1);
	}
);

testAdmin(
	'LPD-32063 Can anonymize a single staged and live web content entry',
	async ({
		apiHelpers,
		journalEditArticlePage,
		journalPage,
		page,
		personalDataErasurePage,
		site,
		siteStagingPage,
		userAssociatedDataJournalPage,
		userAssociatedDataSiteStagingPage,
		usersAndOrganizationsPage,
	}) => {
		testAdmin.setTimeout(120000);

		page.on('dialog', (dialog) => {
			dialog.accept();
		});

		const userAccount =
			await apiHelpers.headlessAdminUser.postUserAccount();

		userData[userAccount.alternateName] = {
			name: userAccount.givenName,
			password: 'test',
			surname: userAccount.familyName,
		};

		const role =
			await apiHelpers.headlessAdminUser.getRoleByName('Administrator');

		await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
			role.externalReferenceCode,
			userAccount.id
		);

		await page.goto(`/group/${site.name}${PORTLET_URLS.staging}`);

		await siteStagingPage.localStagingCheckbox.check();
		await userAssociatedDataSiteStagingPage.webContentCheckbox.check();
		await siteStagingPage.saveButton.click();

		await waitForAlert(page, 'Local staging is successfully enabled.');

		await performLogout(page);
		await performLoginViaApi(page, userAccount.alternateName);

		const webContent1Name = 'wcontent1';
		const webContent2Name = 'wcontent2';
		const webContent3Name = 'wcontent3';

		await page.goto(`/group/${site.name}-staging${PORTLET_URLS.journal}`);

		for (const articleName of [
			webContent1Name,
			webContent2Name,
			webContent3Name,
		]) {
			await journalPage.goToCreateArticle();
			await journalEditArticlePage.createAndPublishBasicArticle(
				articleName
			);
			await waitForAlert(page, 'was created successfully');
		}

		await performLogout(page);
		await performLoginViaApi(page, 'test');

		await page.goto(`/group/${site.name}-staging${PORTLET_URLS.journal}`);

		await userAssociatedDataJournalPage.optionsButton.click();
		await userAssociatedDataSiteStagingPage.stagingMenuItem.click();
		await userAssociatedDataSiteStagingPage.stagingFramePublishToLiveButton.click();

		await expect(
			userAssociatedDataSiteStagingPage.stagingFrameSuccessfulStatusCell
		).toBeVisible();

		await page.reload();

		await usersAndOrganizationsPage.goToUsers(false);
		await (
			await usersAndOrganizationsPage.usersTableRowActions(
				userAccount.alternateName
			)
		).click();

		await usersAndOrganizationsPage.deletePersonalDataMenuItem.click();

		await expect(
			personalDataErasurePage.selectAllItemsOnPageCheckbox
		).toBeVisible();

		await personalDataErasurePage.objectCountLink('6').click();

		const article1 =
			await apiHelpers.jsonWebServicesJournal.getArticleByUrlTitle(
				site.id,
				webContent1Name
			);
		const article2 =
			await apiHelpers.jsonWebServicesJournal.getArticleByUrlTitle(
				site.id,
				webContent2Name
			);

		await personalDataErasurePage
			.journalArticleCheckBox(article1.articleId, webContent1Name, true)
			.check();
		await personalDataErasurePage
			.journalArticleCheckBox(article2.articleId, webContent2Name, false)
			.check();
		await personalDataErasurePage.actionsButton.click();
		await personalDataErasurePage.anonymizeMenuItem.click();

		await waitForAlert(page);

		await page.goto(`/group/${site.name}-staging${PORTLET_URLS.journal}`);

		await expect(
			userAssociatedDataJournalPage.articleLink(webContent1Name)
		).toHaveCount(1);
		await expect(
			userAssociatedDataJournalPage.articleLink(webContent2Name)
		).toHaveCount(1);
		await expect(
			userAssociatedDataJournalPage.articleLink(webContent3Name)
		).toHaveCount(1);
		await expect(
			userAssociatedDataJournalPage.articleCreator(
				'Anonymous Anonymous',
				webContent1Name
			)
		).toBeVisible();
		await expect(
			userAssociatedDataJournalPage.articleCreator(
				userAccount.name,
				webContent2Name
			)
		).toBeVisible();
		await expect(
			userAssociatedDataJournalPage.articleCreator(
				userAccount.name,
				webContent3Name
			)
		).toBeVisible();

		await page.goto(`/group/${site.name}${PORTLET_URLS.journal}`);

		await expect(
			userAssociatedDataJournalPage.articleLink(webContent1Name)
		).toHaveCount(1);
		await expect(
			userAssociatedDataJournalPage.articleLink(webContent2Name)
		).toHaveCount(1);
		await expect(
			userAssociatedDataJournalPage.articleLink(webContent3Name)
		).toHaveCount(1);
		await expect(
			userAssociatedDataJournalPage.articleCreator(
				'Anonymous Anonymous',
				webContent2Name
			)
		).toBeVisible();
		await expect(
			userAssociatedDataJournalPage.articleCreator(
				userAccount.name,
				webContent1Name
			)
		).toBeVisible();
		await expect(
			userAssociatedDataJournalPage.articleCreator(
				userAccount.name,
				webContent3Name
			)
		).toBeVisible();
	}
);

testAdmin(
	'LPD-48828 Can delete multiple entries from an application',
	async ({
		apiHelpers,
		page,
		personalDataErasurePage,
		usersAndOrganizationsPage,
	}) => {
		page.on('dialog', (dialog) => {
			dialog.accept().catch(() => {});
		});

		const userAccount =
			await apiHelpers.headlessAdminUser.postUserAccount();

		userData[userAccount.alternateName] = {
			name: userAccount.givenName,
			password: 'test',
			surname: userAccount.familyName,
		};

		const role =
			await apiHelpers.headlessAdminUser.getRoleByName('Administrator');

		await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
			role.externalReferenceCode,
			userAccount.id
		);

		await performLogout(page);
		await performLoginViaApi(page, userAccount.alternateName);

		const site = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

		const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
			site.id
		);

		const attachment1 = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(__dirname, '/dependencies/attachment.docx')
			)
		);

		const attachment2 = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(__dirname, '/dependencies/attachment.jpeg')
			)
		);

		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(__dirname, '/dependencies/attachment.txt')
			)
		);

		await performLogout(page);
		await performLoginViaApi(page, 'test');

		await usersAndOrganizationsPage.goToUsers(false);
		await (
			await usersAndOrganizationsPage.usersTableRowActions(
				userAccount.alternateName
			)
		).click();
		await usersAndOrganizationsPage.deletePersonalDataMenuItem.click();

		await expect(
			personalDataErasurePage.selectAllItemsOnPageCheckbox
		).toBeVisible();

		await personalDataErasurePage.documentsAndMediaRadioButton.check();
		await (
			await personalDataErasurePage.userAssociatedDataTableRowCheckBox(
				folder.name
			)
		).check();
		await (
			await personalDataErasurePage.userAssociatedDataTableRowCheckBox(
				attachment1.fileName
			)
		).check();
		await (
			await personalDataErasurePage.userAssociatedDataTableRowCheckBox(
				attachment2.fileName
			)
		).check();
		await personalDataErasurePage.actionsButton.click();
		await personalDataErasurePage.deleteMenuItem.click();

		await waitForAlert(page);

		await expect(
			personalDataErasurePage.objectLink(folder.name)
		).not.toBeVisible();
		await expect(
			personalDataErasurePage.objectLink(attachment1.fileName)
		).not.toBeVisible();
		await expect(
			personalDataErasurePage.objectLink(attachment2.fileName)
		).not.toBeVisible();

		await page.goto(`/group/${site.name}${PORTLET_URLS.documentLibrary}`);

		await expect(page.getByText(userAccount.name)).toHaveCount(1);
	}
);

testAdmin(
	'LPD-48828 Can anonymize multiple entries from an application',
	async ({
		apiHelpers,
		page,
		personalDataErasurePage,
		userAssociatedDataDocumentLibraryPage,
		usersAndOrganizationsPage,
	}) => {
		page.on('dialog', (dialog) => {
			dialog.accept().catch(() => {});
		});

		const userAccount =
			await apiHelpers.headlessAdminUser.postUserAccount();

		userData[userAccount.alternateName] = {
			name: userAccount.givenName,
			password: 'test',
			surname: userAccount.familyName,
		};

		const role =
			await apiHelpers.headlessAdminUser.getRoleByName('Administrator');

		await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
			role.externalReferenceCode,
			userAccount.id
		);

		await performLogout(page);
		await performLoginViaApi(page, userAccount.alternateName);

		const site = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

		const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
			site.id
		);

		const attachment1 = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(__dirname, '/dependencies/attachment.docx')
			)
		);

		const attachment2 = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(__dirname, '/dependencies/attachment.jpeg')
			)
		);

		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(__dirname, '/dependencies/attachment.txt')
			)
		);

		await performLogout(page);
		await performLoginViaApi(page, 'test');

		await usersAndOrganizationsPage.goToUsers(false);
		await (
			await usersAndOrganizationsPage.usersTableRowActions(
				userAccount.alternateName
			)
		).click();
		await usersAndOrganizationsPage.deletePersonalDataMenuItem.click();

		await expect(
			personalDataErasurePage.selectAllItemsOnPageCheckbox
		).toBeVisible();

		await personalDataErasurePage.documentsAndMediaRadioButton.check();
		await (
			await personalDataErasurePage.userAssociatedDataTableRowCheckBox(
				folder.name
			)
		).check();
		await (
			await personalDataErasurePage.userAssociatedDataTableRowCheckBox(
				attachment1.fileName
			)
		).check();
		await (
			await personalDataErasurePage.userAssociatedDataTableRowCheckBox(
				attachment2.fileName
			)
		).check();
		await personalDataErasurePage.actionsButton.click();
		await personalDataErasurePage.anonymizeMenuItem.click();

		await waitForAlert(page);

		await expect(page.getByText(folder.name)).not.toBeVisible();
		await expect(page.getByText(attachment1.fileName)).not.toBeVisible();
		await expect(page.getByText(attachment2.fileName)).not.toBeVisible();

		await page.goto(`/group/${site.name}${PORTLET_URLS.documentLibrary}`);

		const anonymousUser = 'Anonymous Anonymous';

		await expect(page.getByText(anonymousUser)).toHaveCount(2);

		await userAssociatedDataDocumentLibraryPage
			.mediaLink(folder.name)
			.click();

		await expect(
			userAssociatedDataDocumentLibraryPage.toogleInfoPanelButtonForFolder
		).toBeVisible();

		await userAssociatedDataDocumentLibraryPage.toogleInfoPanelButtonForFolder.click();

		await expect(
			userAssociatedDataDocumentLibraryPage.infoPanelSideBarCreatedByText
		).toContainText(anonymousUser);

		await page.goto(`/group/${site.name}${PORTLET_URLS.documentLibrary}`);

		await userAssociatedDataDocumentLibraryPage
			.mediaLink(attachment1.title)
			.click();

		await expect(
			userAssociatedDataDocumentLibraryPage.toogleInfoPanelButtonForFiles
		).toBeVisible();

		await expect(async () => {
			await userAssociatedDataDocumentLibraryPage.toogleInfoPanelButtonForFiles.click();

			await expect(
				userAssociatedDataDocumentLibraryPage.infoPanelSideBarOwnerText
			).toBeVisible();
		}).toPass();

		await expect(
			userAssociatedDataDocumentLibraryPage.infoPanelSideBarOwnerText
		).toContainText(anonymousUser);
		await expect(
			userAssociatedDataDocumentLibraryPage.infoPanelSideBarCreatedByText
		).toContainText(anonymousUser);
		await expect(
			userAssociatedDataDocumentLibraryPage.infoPanelSideBarModifiedByText
		).toContainText(anonymousUser);

		await page.goto(`/group/${site.name}${PORTLET_URLS.documentLibrary}`);

		await userAssociatedDataDocumentLibraryPage
			.mediaLink(attachment2.title)
			.click();

		await expect(
			userAssociatedDataDocumentLibraryPage.toogleInfoPanelButtonForFiles
		).toBeVisible();

		await expect(async () => {
			await userAssociatedDataDocumentLibraryPage.toogleInfoPanelButtonForFiles.click();

			await expect(
				userAssociatedDataDocumentLibraryPage.infoPanelSideBarOwnerText
			).toBeVisible();
		}).toPass();

		await expect(
			userAssociatedDataDocumentLibraryPage.infoPanelSideBarOwnerText
		).toContainText(anonymousUser);
		await expect(
			userAssociatedDataDocumentLibraryPage.infoPanelSideBarCreatedByText
		).toContainText(anonymousUser);
		await expect(
			userAssociatedDataDocumentLibraryPage.infoPanelSideBarModifiedByText
		).toContainText(anonymousUser);
	}
);

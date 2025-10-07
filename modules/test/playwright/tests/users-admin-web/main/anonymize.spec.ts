/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'node:path';

import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';
import {contactsCenterPagesTest} from '../../../fixtures/contactsCenterPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {messageBoardsPagesTest} from '../../../fixtures/messageBoardsTest';
import {passwordPoliciesAdminPageTest} from '../../../fixtures/passwordPoliciesAdminConfigPageTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import {siteStagingPageTest} from '../../../fixtures/siteStagingPageTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {TPasswordPolicy} from '../../../helpers/PasswordPolicyApiHelper';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../../utils/waitForAlert';
import {blogsPagesTest} from '../../blogs-web/main/fixtures/blogsPagesTest';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';

export const test = mergeTests(
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
	messageBoardsPagesTest,
	passwordPoliciesAdminPageTest,
	productMenuPageTest,
	siteStagingPageTest,
	usersAndOrganizationsPagesTest
);

const anonymousUserName = 'Anonymous Anonymous';

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

test(
	'Can anonymize all entries',
	{tag: '@LPD-27068'},
	async ({
		apiHelpers,
		page,
		personalDataErasurePage,
		usersAndOrganizationsPage,
	}) => {
		test.setTimeout(120000);

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
		await performLoginViaApi({page, screenName: userAccount.alternateName});

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
		await performLoginViaApi({page, screenName: 'test'});

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

test(
	'Can anonymize a single staged and live web content entry',
	{tag: '@LPD-32063'},
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
		test.setTimeout(120000);

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
		await performLoginViaApi({page, screenName: userAccount.alternateName});

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
		await performLoginViaApi({page, screenName: 'test'});

		await page.goto(`/group/${site.name}-staging${PORTLET_URLS.journal}`);

		await expect(userAssociatedDataJournalPage.optionsButton).toBeEnabled();

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

		await personalDataErasurePage.webContentRadioButton.check();
		await personalDataErasurePage.journalArticleRadioButton.check();

		await personalDataErasurePage.journalArticleCheckBox('1').check();
		await personalDataErasurePage.journalArticleCheckBox('4').check();
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
				anonymousUserName,
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
				anonymousUserName,
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

test(
	'Can anonymize multiple entries from an application',
	{tag: '@LPD-48828'},
	async ({
		apiHelpers,
		page,
		personalDataErasurePage,
		userAssociatedDataDocumentLibraryPage,
		usersAndOrganizationsPage,
	}) => {
		test.setTimeout(120000);

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
		await performLoginViaApi({page, screenName: userAccount.alternateName});

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
		await performLoginViaApi({page, screenName: 'test'});

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

		await expect(page.getByText(anonymousUserName)).toHaveCount(2);

		await userAssociatedDataDocumentLibraryPage.checkFolderCreator(
			folder,
			anonymousUserName
		);

		await page.goto(`/group/${site.name}${PORTLET_URLS.documentLibrary}`);

		await userAssociatedDataDocumentLibraryPage.checkDocumentCreator(
			attachment1,
			anonymousUserName
		);

		await page.goto(`/group/${site.name}${PORTLET_URLS.documentLibrary}`);

		await userAssociatedDataDocumentLibraryPage.checkDocumentCreator(
			attachment2,
			anonymousUserName
		);
	}
);

test(
	'Can anonymize all staged data from DM',
	{tag: '@LPD-49859'},
	async ({
		apiHelpers,
		page,
		personalDataErasurePage,
		productMenuPage,
		siteStagingPage,
		userAssociatedDataDocumentLibraryPage,
		usersAndOrganizationsPage,
	}) => {
		test.setTimeout(120000);

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
		await performLoginViaApi({page, screenName: userAccount.alternateName});

		const site = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: 'Page' + getRandomInt(),
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

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		await page.goto(`/group/${site.name}/${layout.friendlyUrlPath}`);

		await productMenuPage.openProductMenuIfClosed();
		await productMenuPage.publishingButton.click();
		await productMenuPage.stagingMenuItem.click();
		await siteStagingPage.localStagingCheckbox.check();
		await siteStagingPage.saveButton.click();

		await waitForAlert(page, 'Local staging is successfully enabled.');

		await usersAndOrganizationsPage.goToUsers(false);
		await (
			await usersAndOrganizationsPage.usersTableRowActions(
				userAccount.alternateName
			)
		).click();
		await usersAndOrganizationsPage.deletePersonalDataMenuItem.click();

		await waitForAlert(page);

		await expect(
			personalDataErasurePage.selectAllItemsOnPageCheckbox
		).toBeVisible();

		await personalDataErasurePage.documentsAndMediaRadioButton.check();
		await personalDataErasurePage
			.objectCheckBox(folder.id, folder.name, false)
			.check();
		await personalDataErasurePage
			.objectCheckBox(attachment1.id, attachment1.fileName, false)
			.check();
		await personalDataErasurePage
			.objectCheckBox(attachment2.id, attachment2.fileName, false)
			.check();
		await personalDataErasurePage.actionsButton.click();
		await personalDataErasurePage.anonymizeMenuItem.click();

		await expect(
			personalDataErasurePage.objectCheckBox(
				folder.id,
				folder.name,
				false
			)
		).not.toBeVisible();
		await expect(
			personalDataErasurePage.objectCheckBox(
				attachment1.id,
				attachment1.fileName,
				false
			)
		).not.toBeVisible();
		await expect(
			personalDataErasurePage.objectCheckBox(
				attachment2.id,
				attachment2.fileName,
				false
			)
		).not.toBeVisible();

		await waitForAlert(page);

		await page.goto(`/group/${site.name}${PORTLET_URLS.documentLibrary}`);

		await expect(page.getByText(anonymousUserName)).toHaveCount(0);

		await userAssociatedDataDocumentLibraryPage.checkFolderCreator(
			folder,
			userAccount.name
		);

		await page.goto(`/group/${site.name}${PORTLET_URLS.documentLibrary}`);

		await userAssociatedDataDocumentLibraryPage.checkDocumentCreator(
			attachment1,
			userAccount.name
		);

		await page.goto(`/group/${site.name}${PORTLET_URLS.documentLibrary}`);

		await userAssociatedDataDocumentLibraryPage.checkDocumentCreator(
			attachment2,
			userAccount.name
		);

		await page.goto(
			`/group/${site.name}-staging${PORTLET_URLS.documentLibrary}`
		);

		await expect(page.getByText(anonymousUserName)).toHaveCount(2);

		await userAssociatedDataDocumentLibraryPage.checkFolderCreator(
			folder,
			anonymousUserName
		);

		await page.goto(
			`/group/${site.name}-staging${PORTLET_URLS.documentLibrary}`
		);

		await userAssociatedDataDocumentLibraryPage.checkDocumentCreator(
			attachment1,
			anonymousUserName
		);

		await page.goto(
			`/group/${site.name}-staging${PORTLET_URLS.documentLibrary}`
		);

		await userAssociatedDataDocumentLibraryPage.checkDocumentCreator(
			attachment2,
			anonymousUserName
		);
	}
);

test(
	'Can anonymize via info panel',
	{tag: '@LPD-50002'},
	async ({
		apiHelpers,
		blogsPage,
		page,
		personalDataErasurePage,
		usersAndOrganizationsPage,
	}) => {
		test.setTimeout(120000);

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
		await performLoginViaApi({page, screenName: userAccount.alternateName});

		const blog1Name = 'Blog' + getRandomInt();
		const blog2Name = 'Blog' + getRandomInt();

		const blog1 = await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: blog1Name,
		});

		await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: blog2Name,
		});

		await page.goto(`/group/${site.name}/${layout.friendlyUrlPath}`);

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		await usersAndOrganizationsPage.goToUsers(false);
		await (
			await usersAndOrganizationsPage.usersTableRowActions(
				userAccount.alternateName
			)
		).click();

		await usersAndOrganizationsPage.deletePersonalDataMenuItem.click();

		await waitForAlert(page);

		await expect(
			personalDataErasurePage.selectAllItemsOnPageCheckbox
		).toBeVisible();

		await personalDataErasurePage.blogsRadioButton.check();
		await personalDataErasurePage
			.objectCheckBox(blog1.id, blog1Name, true)
			.check();

		await expect(async () => {
			await personalDataErasurePage.infoPanelButton.click();
			await personalDataErasurePage
				.infoPanelEllipsisButton(blog1Name)
				.click();
		}).toPass();

		await personalDataErasurePage.anonymizeLink.click();

		await expect(
			personalDataErasurePage.objectLink(blog1.headline)
		).not.toBeVisible();

		await page.goto(`/group/${site.name}${PORTLET_URLS.blogs}`);

		await expect(blogsPage.blogName(blog1Name)).toHaveCount(1);
		await expect(blogsPage.blogName(blog2Name)).toHaveCount(1);
		await expect(page.getByText(anonymousUserName)).toHaveCount(1);
	}
);

test(
	'Entries are still anonymous after activating user',
	{tag: '@LPD-50693'},
	async ({
		apiHelpers,
		page,
		personalDataErasurePage,
		userAssociatedDataDocumentLibraryPage,
		usersAndOrganizationsPage,
	}) => {
		test.setTimeout(120000);

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

		await performLogout(page);
		await performLoginViaApi({page, screenName: userAccount.alternateName});

		const site = await apiHelpers.headlessSite.createSite({
			name: 'Site' + getRandomInt(),
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

		const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
			site.id
		);

		const attachment = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(__dirname, '/dependencies/attachment.txt')
			),
			{
				description: getRandomString(),
				fileName: 'attachment.txt',
				title: getRandomString(),
			}
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		await usersAndOrganizationsPage.goToUsers(false);
		await (
			await usersAndOrganizationsPage.usersTableRowActions(
				userAccount.alternateName
			)
		).click();
		await usersAndOrganizationsPage.deletePersonalDataMenuItem.click();
		await personalDataErasurePage.documentsAndMediaRadioButton.check();
		await (
			await personalDataErasurePage.userAssociatedDataTableRowCheckBox(
				folder.name
			)
		).check();
		await (
			await personalDataErasurePage.userAssociatedDataTableRowCheckBox(
				attachment.fileName
			)
		).check();
		await personalDataErasurePage.actionsButton.click();
		await personalDataErasurePage.anonymizeMenuItem.click();

		await waitForAlert(page);

		await page.goto(`/group/${site.name}${PORTLET_URLS.documentLibrary}`);

		await expect(page.getByText(anonymousUserName)).toHaveCount(1);

		await userAssociatedDataDocumentLibraryPage.checkFolderCreator(
			folder,
			anonymousUserName
		);

		await page.goto(`/group/${site.name}${PORTLET_URLS.documentLibrary}`);

		await userAssociatedDataDocumentLibraryPage.checkDocumentCreator(
			attachment,
			anonymousUserName
		);

		await usersAndOrganizationsPage.goToUsers(false);
		await usersAndOrganizationsPage.filterUsers('inactive');
		await usersAndOrganizationsPage.activateUsers([userAccount.name]);

		await page.goto(`/group/${site.name}${PORTLET_URLS.documentLibrary}`);

		await expect(page.getByText(anonymousUserName)).toHaveCount(1);

		await userAssociatedDataDocumentLibraryPage.checkFolderCreator(
			folder,
			anonymousUserName
		);

		await page.goto(`/group/${site.name}${PORTLET_URLS.documentLibrary}`);

		await userAssociatedDataDocumentLibraryPage.checkDocumentCreator(
			attachment,
			anonymousUserName
		);
	}
);

test(
	'Can anonymize all entries from an application',
	{tag: '@LPD-50693'},
	async ({
		apiHelpers,
		page,
		personalDataErasurePage,
		userAssociatedDataDocumentLibraryPage,
		usersAndOrganizationsPage,
	}) => {
		test.setTimeout(120000);

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
		await performLoginViaApi({page, screenName: userAccount.alternateName});

		const site = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

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

		const attachment3 = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(__dirname, '/dependencies/attachment.txt')
			)
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

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
				attachment1.fileName
			)
		).check();
		await (
			await personalDataErasurePage.userAssociatedDataTableRowCheckBox(
				attachment2.fileName
			)
		).check();
		await (
			await personalDataErasurePage.userAssociatedDataTableRowCheckBox(
				attachment3.fileName
			)
		).check();
		await personalDataErasurePage.actionsButton.click();
		await personalDataErasurePage.anonymizeMenuItem.click();

		await waitForAlert(page);

		await expect(page.getByText(attachment1.fileName)).not.toBeVisible();
		await expect(page.getByText(attachment2.fileName)).not.toBeVisible();
		await expect(page.getByText(attachment3.fileName)).not.toBeVisible();

		await page.goto(`/group/${site.name}${PORTLET_URLS.documentLibrary}`);

		await expect(page.getByText(anonymousUserName)).toHaveCount(3);

		await userAssociatedDataDocumentLibraryPage.checkDocumentCreator(
			attachment1,
			anonymousUserName
		);

		await page.goto(`/group/${site.name}${PORTLET_URLS.documentLibrary}`);

		await userAssociatedDataDocumentLibraryPage.checkDocumentCreator(
			attachment2,
			anonymousUserName
		);

		await page.goto(`/group/${site.name}${PORTLET_URLS.documentLibrary}`);

		await userAssociatedDataDocumentLibraryPage.checkDocumentCreator(
			attachment3,
			anonymousUserName
		);
	}
);

test(
	'Can anonymize entry from application',
	{tag: '@LPD-50693'},
	async ({
		apiHelpers,
		page,
		personalDataErasurePage,
		userAssociatedDataDocumentLibraryPage,
		usersAndOrganizationsPage,
	}) => {
		test.setTimeout(120000);

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
		await performLoginViaApi({page, screenName: userAccount.alternateName});

		const site = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

		const attachment = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(__dirname, '/dependencies/attachment.txt')
			)
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

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
				attachment.fileName
			)
		).check();
		await personalDataErasurePage.actionsButton.click();
		await personalDataErasurePage.anonymizeMenuItem.click();

		await waitForAlert(page);

		await page.goto(`/group/${site.name}${PORTLET_URLS.documentLibrary}`);

		await expect(page.getByText(anonymousUserName)).toHaveCount(1);

		await userAssociatedDataDocumentLibraryPage.checkDocumentCreator(
			attachment,
			anonymousUserName
		);
	}
);

test(
	'Can anonymize a related asset',
	{tag: '@LPD-51202'},
	async ({
		apiHelpers,
		messageBoardsEditThreadPage,
		messageBoardsPage,
		page,
		personalDataErasurePage,
		userAssociatedDataEditMessageBoardThreadPage,
		userAssociatedDataMessageBoardPage,
		usersAndOrganizationsPage,
	}) => {
		test.setTimeout(120000);

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
		await performLoginViaApi({page, screenName: userAccount.alternateName});

		const site = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

		const blog = await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: 'Blog' + getRandomInt(),
		});

		const document = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(__dirname, '/dependencies/attachment.txt')
			)
		);

		const threadSubject = 'Thread' + getRandomInt();

		await messageBoardsEditThreadPage.gotoAndPublishNewBasicThread(
			threadSubject,
			getRandomString(),
			site.friendlyUrlPath
		);

		await waitForAlert(page);

		await userAssociatedDataMessageBoardPage.actionButton.click();
		await userAssociatedDataMessageBoardPage.editMenuItem.click();

		await expect(
			userAssociatedDataEditMessageBoardThreadPage.relatedAssetsButton
		).toBeVisible();

		await userAssociatedDataEditMessageBoardThreadPage.relatedAssetsButton.click();

		await expect(async () => {
			await userAssociatedDataEditMessageBoardThreadPage.selectButton.click();
			await expect(
				userAssociatedDataEditMessageBoardThreadPage.blogEntryMenuItem
			).toBeVisible();
		}).toPass();

		await userAssociatedDataEditMessageBoardThreadPage.blogEntryMenuItem.click();

		await expect(
			await userAssociatedDataEditMessageBoardThreadPage.tableRowCheckBox(
				blog.headline
			)
		).toBeVisible();

		await (
			await userAssociatedDataEditMessageBoardThreadPage.tableRowCheckBox(
				blog.headline
			)
		).check();
		await userAssociatedDataEditMessageBoardThreadPage.doneButton.click();

		await expect(async () => {
			await userAssociatedDataEditMessageBoardThreadPage.selectButton.click();
			await expect(
				userAssociatedDataEditMessageBoardThreadPage.basicDocumentMenuItem
			).toBeVisible();
		}).toPass();

		await userAssociatedDataEditMessageBoardThreadPage.basicDocumentMenuItem.click();

		await expect(
			await userAssociatedDataEditMessageBoardThreadPage.tableRowCheckBox(
				document.title
			)
		).toBeVisible();

		await (
			await userAssociatedDataEditMessageBoardThreadPage.tableRowCheckBox(
				document.title
			)
		).check();
		await userAssociatedDataEditMessageBoardThreadPage.doneButton.click();
		await userAssociatedDataEditMessageBoardThreadPage.publishButton.click();

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		await usersAndOrganizationsPage.goToUsers(false);
		await (
			await usersAndOrganizationsPage.usersTableRowActions(
				userAccount.alternateName
			)
		).click();
		await usersAndOrganizationsPage.deletePersonalDataMenuItem.click();

		await waitForAlert(page);

		await personalDataErasurePage.blogsRadioButton.check();
		await (
			await personalDataErasurePage.userAssociatedDataTableRowCheckBox(
				blog.headline
			)
		).check();
		await personalDataErasurePage.actionsButton.click();
		await personalDataErasurePage.anonymizeMenuItem.click();

		await waitForAlert(page);

		await messageBoardsPage.goto(site.friendlyUrlPath);

		await expect(page.getByText(userAccount.name)).toHaveCount(1);

		await userAssociatedDataMessageBoardPage
			.threadSubjectLink(threadSubject)
			.click();
		await userAssociatedDataEditMessageBoardThreadPage
			.relatedAssetLink(blog.headline)
			.click();

		await expect(page.getByText(anonymousUserName)).toHaveCount(1);

		await messageBoardsPage.goto(site.friendlyUrlPath);
		await userAssociatedDataMessageBoardPage
			.threadSubjectLink(threadSubject)
			.click();
		await userAssociatedDataEditMessageBoardThreadPage
			.relatedAssetLink(document.title)
			.click();

		await expect(page.getByText(userAccount.name)).toHaveCount(1);
	}
);

test(
	'Can anonymize with password minimum upper case change and password length change',
	{tag: '@LPD-55588'},
	async ({
		apiHelpers,
		page,
		passwordPoliciesAdminConfigPage,
		personalDataErasurePage,
		userAssociatedDataDocumentLibraryPage,
		usersAndOrganizationsPage,
	}) => {
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

		const site = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

		await performLogout(page);
		await performLoginViaApi({page, screenName: userAccount.alternateName});

		const document = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(__dirname, '/dependencies/attachment.txt')
			)
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		try {
			await passwordPoliciesAdminConfigPage.goTo();

			const passwordPolicy: TPasswordPolicy = {
				checkSyntaxToggle: true,
				minLength: 9,
				minUpperCase: 2,
			};

			await passwordPoliciesAdminConfigPage.editDefaultPasswordPolicy(
				passwordPolicy
			);

			await usersAndOrganizationsPage.goToUsers(false);
			await (
				await usersAndOrganizationsPage.usersTableRowActions(
					userAccount.alternateName
				)
			).click();
			await usersAndOrganizationsPage.deletePersonalDataMenuItem.click();

			await waitForAlert(page);

			await expect(
				personalDataErasurePage.selectAllItemsOnPageCheckbox
			).toBeVisible();

			await personalDataErasurePage.documentsAndMediaRadioButton.check();
			await (
				await personalDataErasurePage.userAssociatedDataTableRowCheckBox(
					document.fileName
				)
			).check();
			await personalDataErasurePage.actionsButton.click();
			await personalDataErasurePage.anonymizeMenuItem.click();

			await waitForAlert(page);

			await page.goto(
				`/group/${site.name}${PORTLET_URLS.documentLibrary}`
			);

			await expect(page.getByText(anonymousUserName)).toHaveCount(1);

			await userAssociatedDataDocumentLibraryPage.checkDocumentCreator(
				document,
				anonymousUserName
			);
		}
		finally {
			await passwordPoliciesAdminConfigPage.goTo();
			await passwordPoliciesAdminConfigPage.resetDefaultPasswordPolicy();
		}
	}
);

test(
	'Can delete and anonymize all entries',
	{tag: '@LPD-56476'},
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
		await performLoginViaApi({page, screenName: userAccount.alternateName});

		const site = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(__dirname, '/dependencies/attachment.txt')
			),
			{
				fileName: 'Document' + getRandomInt(),
			}
		);

		await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: 'Blog' + getRandomInt(),
		});

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		await usersAndOrganizationsPage.goToUsers(false);
		await (
			await usersAndOrganizationsPage.usersTableRowActions(
				userAccount.alternateName
			)
		).click();
		await usersAndOrganizationsPage.deletePersonalDataMenuItem.click();

		await waitForAlert(page);

		await expect(
			personalDataErasurePage.selectAllItemsOnPageCheckbox
		).toBeVisible();

		await personalDataErasurePage.selectAllItemsOnPageCheckbox.check();
		await personalDataErasurePage.actionsButton.click();
		await personalDataErasurePage.deleteMenuItem.click();

		await expect(personalDataErasurePage.anonymizeButton).toBeVisible();

		await personalDataErasurePage.anonymizeButton.click();
		await personalDataErasurePage.reviewDataLink.click();

		await expect(
			personalDataErasurePage.anonymizedAllRemainingDataMessage
		).toBeVisible();

		await waitForAlert(page, 'Success:User successfully deleted.');

		await usersAndOrganizationsPage.goToUsers(true);

		await expect(
			usersAndOrganizationsPage.usersTableCell(userAccount.name)
		).not.toBeVisible();
	}
);

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'node:path';

import {contactsCenterPagesTest} from '../../../fixtures/contactsCenterPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {formsPagesTest} from '../../../fixtures/formsPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {messageBoardsPagesTest} from '../../../fixtures/messageBoardsTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import {siteStagingPageTest} from '../../../fixtures/siteStagingPageTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch, userData} from '../../../utils/performLogin';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../../utils/waitForAlert';
import {blogsPagesTest} from '../../blogs-web/main/fixtures/blogsPagesTest';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';

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
	blogsPagesTest,
	contactsCenterPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
		'LPD-35013': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	formsPagesTest,
	isolatedSiteTest,
	journalPagesTest,
	loginTest(),
	messageBoardsPagesTest,
	pagesAdminPagesTest,
	productMenuPageTest,
	siteStagingPageTest,
	usersAndOrganizationsPagesTest
);

testAdmin(
	'Can export multiple entries',
	{tag: '@LPD-25858'},
	async ({
		apiHelpers,
		contactsCenterPage,
		exportUserDataPage,
		page,
		usersAndOrganizationsPage,
	}) => {
		testAdmin.setTimeout(120000);

		const contentUser =
			await apiHelpers.headlessAdminUser.postUserAccount();

		const adminRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Administrator');

		await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
			adminRole.externalReferenceCode,
			contentUser.id
		);

		userData[contentUser.alternateName] = {
			name: contentUser.givenName,
			password: userData['test'].password,
			surname: contentUser.familyName,
		};

		await performUserSwitch(page, contentUser.alternateName);

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

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

		await performUserSwitch(page, 'test');

		await page.goto(`/web/${site.name}`);

		await usersAndOrganizationsPage.goToUsers(false);

		await (
			await usersAndOrganizationsPage.usersTableRowActions(
				contentUser.alternateName
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

		await expect(
			exportUserDataPage.announcementsStatusSuccessful
		).toBeVisible();
		await expect(exportUserDataPage.blogsStatusSuccessful).toBeVisible();
		await expect(
			exportUserDataPage.contactsCenterStatusSuccessful
		).toBeVisible();
		await expect(
			exportUserDataPage.documentsAndMediaStatusSuccessful
		).toBeVisible();
		await expect(
			exportUserDataPage.messageBoardsStatusSuccessful
		).toBeVisible();
		await expect(
			exportUserDataPage.webContentStatusSuccessful
		).toBeVisible();
		await expect(exportUserDataPage.wikiStatusSuccessful).toBeVisible();

		await exportUserDataPage.creationMenuNewButton.click();

		await expect(exportUserDataPage.announcementsCheckbox).toBeVisible();
	}
);

testAdmin(
	'Can delete a single staged and live blogs entry',
	{tag: '@LPD-31206'},
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

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: 'Site' + getRandomInt(),
		});

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

		await performUserSwitch(page, userAccount.alternateName);

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

		await productMenuPage.openProductMenuIfClosed();
		await productMenuPage.publishingButton.click();
		await productMenuPage.stagingMenuItem.click();
		await siteStagingPage.localStagingCheckbox.check();
		await siteStagingPage.blogsCheckbox.check();
		await siteStagingPage.saveButton.click();

		await waitForAlert(page, 'Local staging is successfully enabled.');

		await performUserSwitch(page, 'test');

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
			.objectCheckBox(blog1.id, blog1Name, true)
			.check();
		await personalDataErasurePage
			.objectCheckBox(blog2.id, blog2Name, false)
			.check();

		await personalDataErasurePage.actionsButton.click();
		await personalDataErasurePage.deleteMenuItem.click();

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
	'Can delete multiple entries from an application',
	{tag: '@LPD-48828'},
	async ({
		apiHelpers,
		page,
		personalDataErasurePage,
		usersAndOrganizationsPage,
	}) => {
		testAdmin.setTimeout(120000);

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

		await performUserSwitch(page, userAccount.alternateName);

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

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

		await performUserSwitch(page, 'test');

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
	'Applications without entries are visible but disabled in new data export',
	{tag: '@LPD-50594'},
	async ({
		apiHelpers,
		exportUserDataPage,
		page,
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

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: 'Site' + getRandomInt(),
		});

		const role =
			await apiHelpers.headlessAdminUser.getRoleByName('Administrator');

		await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
			role.externalReferenceCode,
			userAccount.id
		);

		await performUserSwitch(page, userAccount.alternateName);

		await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: getRandomString(),
		});

		const contentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId: contentStructureId,
			groupId: site.id,
		});

		await apiHelpers.jsonWebServicesMBApiHelper.addMessage({
			groupId: site.id,
		});

		await performUserSwitch(page, 'test');

		await usersAndOrganizationsPage.goToUsers(false);

		await (
			await usersAndOrganizationsPage.usersTableRowActions(
				userAccount.alternateName
			)
		).click();
		await usersAndOrganizationsPage.exportPersonalDataItem.click();
		await exportUserDataPage.addExportProcessesButton.click();

		await expect(exportUserDataPage.blogsCheckbox).toBeEnabled();
		await expect(exportUserDataPage.webContentCheckbox).toBeEnabled();
		await expect(exportUserDataPage.messageBoardsCheckbox).toBeEnabled();
		await expect(exportUserDataPage.announcementsCheckbox).toBeDisabled();
		await expect(exportUserDataPage.contactsCenterCheckbox).toBeDisabled();
		await expect(
			exportUserDataPage.documentsAndMediaCheckbox
		).toBeDisabled();
		await expect(exportUserDataPage.formsCheckbox).toBeDisabled();
		await expect(exportUserDataPage.wikiCheckbox).toBeDisabled();

		await exportUserDataPage.blogsCheckbox.check();
		await exportUserDataPage.webContentCheckbox.check();
		await exportUserDataPage.messageBoardsCheckbox.check();
		await exportUserDataPage.exportButton.click();

		await expect(exportUserDataPage.blogsStatusSuccessful).toBeVisible();
		await expect(
			exportUserDataPage.webContentStatusSuccessful
		).toBeVisible();
		await expect(
			exportUserDataPage.messageBoardsStatusSuccessful
		).toBeVisible();
		await expect(
			exportUserDataPage.announcementsStatusSuccessful
		).not.toBeVisible();
		await expect(
			exportUserDataPage.contactsCenterStatusSuccessful
		).not.toBeVisible();
		await expect(
			exportUserDataPage.documentsAndMediaStatusSuccessful
		).not.toBeVisible();
		await expect(
			exportUserDataPage.formsStatusSuccessful
		).not.toBeVisible();
		await expect(exportUserDataPage.wikiStatusSuccessful).not.toBeVisible();
	}
);

testAdmin(
	'Documents and Media entries display details in info panel during personal data deletion',
	{tag: '@LPD-50608'},
	async ({
		apiHelpers,
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

		const role =
			await apiHelpers.headlessAdminUser.getRoleByName('Administrator');

		await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
			role.externalReferenceCode,
			userAccount.id
		);

		await performUserSwitch(page, userAccount.alternateName);

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: 'Site' + getRandomInt(),
		});

		const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
			site.id
		);

		const document = await apiHelpers.headlessDelivery.postDocument(
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

		await performUserSwitch(page, 'test');

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

		await expect(personalDataErasurePage.dlFileEntryText).toBeVisible();
		await expect(personalDataErasurePage.dlFolderText).toBeVisible();

		await (
			await personalDataErasurePage.userAssociatedDataTableRowCheckBox(
				folder.name
			)
		).check();
		await personalDataErasurePage.infoPanelButton.click();

		await expect(personalDataErasurePage.infoPanelSidebar).toContainText(
			folder.name
		);
		await expect(personalDataErasurePage.infoPanelSidebar).toContainText(
			folder.description
		);

		await (
			await personalDataErasurePage.userAssociatedDataTableRowCheckBox(
				folder.name
			)
		).uncheck();
		await (
			await personalDataErasurePage.userAssociatedDataTableRowCheckBox(
				document.fileName
			)
		).check();

		await expect(personalDataErasurePage.infoPanelSidebar).toContainText(
			document.title
		);
		await expect(personalDataErasurePage.infoPanelSidebar).toContainText(
			document.description
		);
		await expect(personalDataErasurePage.infoPanelSidebar).toContainText(
			'txt'
		);
	}
);

testAdmin(
	'Can delete all staged data from an application',
	{tag: '@LPD-51202'},
	async ({
		apiHelpers,
		page,
		personalDataErasurePage,
		productMenuPage,
		siteStagingPage,
		usersAndOrganizationsPage,
	}) => {
		testAdmin.setTimeout(120000);

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

		await performUserSwitch(page, userAccount.alternateName);

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: 'Page' + getRandomInt(),
		});

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

		const blog1 = await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: getRandomString(),
		});
		const blog2 = await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: getRandomString(),
		});

		await page.goto(`/group/${site.name}/${layout.friendlyUrlPath}`);

		await productMenuPage.openProductMenuIfClosed();
		await productMenuPage.publishingButton.click();
		await productMenuPage.stagingMenuItem.click();
		await siteStagingPage.localStagingCheckbox.check();
		await siteStagingPage.blogsCheckbox.check();
		await siteStagingPage.saveButton.click();

		await waitForAlert(page, 'Local staging is successfully enabled.');

		await performUserSwitch(page, 'test');

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
			.objectCheckBox(attachment1.id, attachment1.fileName, false)
			.check();
		await personalDataErasurePage
			.objectCheckBox(attachment2.id, attachment2.fileName, false)
			.check();
		await personalDataErasurePage.actionsButton.click();
		await personalDataErasurePage.deleteMenuItem.click();

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

		await personalDataErasurePage.blogsRadioButton.check();
		await personalDataErasurePage
			.objectCheckBox(blog1.id, blog1.headline, false)
			.check();
		await personalDataErasurePage
			.objectCheckBox(blog2.id, blog2.headline, false)
			.check();
		await personalDataErasurePage.actionsButton.click();
		await personalDataErasurePage.deleteMenuItem.click();

		await expect(
			personalDataErasurePage.objectCheckBox(
				blog1.id,
				blog1.headline,
				false
			)
		).not.toBeVisible();
		await expect(
			personalDataErasurePage.objectCheckBox(
				blog2.id,
				blog2.headline,
				false
			)
		).not.toBeVisible();

		await waitForAlert(page);

		await page.goto(`/group/${site.name}${PORTLET_URLS.blogs}`);

		await expect(page.getByText(userAccount.name)).toHaveCount(2);
		await expect(page.getByText(blog1.headline)).toHaveCount(1);
		await expect(page.getByText(blog2.headline)).toHaveCount(1);

		await page.goto(`/group/${site.name}-staging${PORTLET_URLS.blogs}`);

		await expect(page.getByText(userAccount.name)).toHaveCount(0);
		await expect(page.getByText(blog1.headline)).toHaveCount(0);
		await expect(page.getByText(blog2.headline)).toHaveCount(0);

		await page.goto(`/group/${site.name}${PORTLET_URLS.documentLibrary}`);

		await expect(page.getByText(userAccount.name)).toHaveCount(2);
		await expect(page.getByText(attachment1.title)).toHaveCount(1);
		await expect(page.getByText(attachment2.title)).toHaveCount(1);

		await page.goto(
			`/group/${site.name}-staging${PORTLET_URLS.documentLibrary}`
		);

		await expect(page.getByText(userAccount.name)).toHaveCount(0);
		await expect(page.getByText(attachment1.title)).toHaveCount(0);
		await expect(page.getByText(attachment2.title)).toHaveCount(0);
	}
);

testAdmin(
	'Can delete a related asset',
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

		await performUserSwitch(page, userAccount.alternateName);

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

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

		await userAssociatedDataEditMessageBoardThreadPage.selectRelatedAssets([
			blog.headline,
			document.title,
		]);

		await userAssociatedDataEditMessageBoardThreadPage.publishButton.click();

		await performUserSwitch(page, 'test');

		await usersAndOrganizationsPage.goToUsers(false);
		await (
			await usersAndOrganizationsPage.usersTableRowActions(
				userAccount.alternateName
			)
		).click();
		await usersAndOrganizationsPage.deletePersonalDataMenuItem.click();

		await waitForAlert(page);

		await personalDataErasurePage.documentsAndMediaRadioButton.check();
		await (
			await personalDataErasurePage.userAssociatedDataTableRowCheckBox(
				document.fileName
			)
		).check();
		await personalDataErasurePage.actionsButton.click();
		await personalDataErasurePage.deleteMenuItem.click();

		await waitForAlert(page);

		await messageBoardsPage.goto(site.friendlyUrlPath);

		await userAssociatedDataMessageBoardPage
			.threadSubjectLink(threadSubject)
			.click();

		await expect(page.getByText(blog.headline)).toBeVisible();
		await expect(page.getByText(document.title)).toHaveCount(0);
	}
);

testAdmin(
	'Can publish to live a deleted live entry',
	{tag: '@LPD-55588'},
	async ({
		apiHelpers,
		blogsPage,
		page,
		personalDataErasurePage,
		productMenuPage,
		siteStagingPage,
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

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: 'Site' + getRandomInt(),
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: 'Page' + getRandomInt(),
		});

		await performUserSwitch(page, userAccount.alternateName);

		const blog = await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: getRandomString(),
		});

		await page.goto(`/group/${site.name}/${layout.friendlyUrlPath}`);

		await productMenuPage.openProductMenuIfClosed();
		await productMenuPage.publishingButton.click();
		await productMenuPage.stagingMenuItem.click();
		await siteStagingPage.localStagingCheckbox.check();
		await siteStagingPage.blogsCheckbox.check();
		await siteStagingPage.saveButton.click();

		await waitForAlert(page, 'Local staging is successfully enabled.');

		await performUserSwitch(page, 'test');

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

		await personalDataErasurePage.blogsRadioButton.check();
		await personalDataErasurePage
			.objectCheckBox(blog.id, blog.headline, true)
			.check();
		await personalDataErasurePage.actionsButton.click();
		await personalDataErasurePage.deleteMenuItem.click();

		await expect(
			personalDataErasurePage.selectAllItemsOnPageCheckbox
		).toBeVisible();

		await page.goto(`/group/${site.name}${PORTLET_URLS.blogs}`);

		await expect(blogsPage.blogName(blog.headline)).toHaveCount(0);

		await page.goto(`/group/${site.name}-staging${PORTLET_URLS.blogs}`);

		await expect(blogsPage.blogName(blog.headline)).toHaveCount(1);

		await blogsPage.goToBlogEntryAction('Publish to Live', blog.title);
		await blogsPage.successMessage.waitFor();

		await page.goto(`/group/${site.name}${PORTLET_URLS.blogs}`);

		await expect(blogsPage.blogName(blog.headline)).toHaveCount(1);
	}
);

testAdmin(
	'Can filter and view data',
	{tag: '@LPD-56386'},
	async ({
		apiHelpers,
		contactsCenterPage,
		formBuilderPage,
		formBuilderSidePanelPage,
		page,
		personalDataErasurePage,
		userAssociatedDataFormPage,
		usersAndOrganizationsPage,
	}) => {
		testAdmin.setTimeout(90000);

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

		await performUserSwitch(page, userAccount.alternateName);

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		const formTitle = 'Form' + getRandomInt();
		const textFieldLabel = 'Text Field';

		await formBuilderPage.goToNew(site.friendlyUrlPath);
		await formBuilderPage.fillFormTitle(formTitle);
		await formBuilderSidePanelPage.addFieldByDoubleClick('Text');
		await formBuilderSidePanelPage.label.fill(textFieldLabel);
		await formBuilderPage.publishButton.click();

		await waitForAlert(page);

		const formPageName = 'form-page-' + getRandomInt();

		await userAssociatedDataFormPage.createFormPage(
			apiHelpers,
			formTitle,
			site,
			{
				title: formPageName,
			}
		);

		await page.goto(`/web/${site.name}/${formPageName}`);

		await expect(
			userAssociatedDataFormPage.formWidgetTextFieldLabel(textFieldLabel)
		).toBeVisible();

		await userAssociatedDataFormPage
			.formWidgetTextFieldLabel(textFieldLabel)
			.fill(`${textFieldLabel} value`);
		await userAssociatedDataFormPage.formWidgetSubmitButton.click();

		await waitForAlert(page);

		const contactsCenterPageName = 'contact-center-' + getRandomInt();

		await contactsCenterPage.createPage(apiHelpers, site.id, {
			title: contactsCenterPageName,
		});

		await page.goto(`/web/${site.name}/${contactsCenterPageName}`);

		await contactsCenterPage.addContactButton.click();

		const name = getRandomString();
		const email = `${getRandomString()}@liferay.com`;

		await contactsCenterPage.nameInput.fill(name);
		await contactsCenterPage.emailAddressInput.fill(email);
		await contactsCenterPage.saveButton.click();

		await expect(contactsCenterPage.successMessage).toBeVisible();

		const contentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const webContent =
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: contentStructureId,
				groupId: site.id,
			});

		await performUserSwitch(page, 'test');

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

		await personalDataErasurePage.contactsCenterRadioButton.check();

		await expect(page.getByText(name)).toHaveCount(1);
		await expect(page.getByText(email)).toHaveCount(1);

		await personalDataErasurePage.regularSitesRadioButton.check();

		await expect(personalDataErasurePage.formsRadioButton).toBeVisible();

		await personalDataErasurePage.formsRadioButton.check();

		await expect(page.getByText(formTitle)).toHaveCount(1);

		await personalDataErasurePage.webContentRadioButton.check();
		await personalDataErasurePage.journalArticleRadioButton.check();

		await expect(page.getByText(webContent.title)).toHaveCount(1);
	}
);

testAdmin(
	'Remaining items count is accurate',
	{tag: ['@LPD-56386', '@LPS-91766']},
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

		await performUserSwitch(page, userAccount.alternateName);

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
			site.id
		);

		const attachment = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(__dirname, '/dependencies/attachment.docx')
			)
		);

		const blog = await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: getRandomString(),
		});

		await performUserSwitch(page, 'test');

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
		await expect(
			personalDataErasurePage.remainingItemsCount('3')
		).toBeVisible();
		await expect(
			await personalDataErasurePage.allApplicationsDataTableRowCount(
				'1',
				'Blogs'
			)
		).toBeVisible();
		await expect(
			await personalDataErasurePage.allApplicationsDataTableRowCount(
				'2',
				'Documents and Media'
			)
		).toBeVisible();
		await expect(
			personalDataErasurePage.objectRadioButtonLabelCount('Blogs', '1')
		).toBeVisible();
		await expect(
			personalDataErasurePage.objectRadioButtonLabelCount(
				'Documents and Media',
				'2'
			)
		).toBeVisible();

		await personalDataErasurePage.documentsAndMediaRadioButton.check();

		await expect(
			personalDataErasurePage.selectAllItemsOnPageCheckbox
		).toBeVisible();

		await personalDataErasurePage
			.objectCheckBox(folder.id, folder.name, true)
			.check();
		await personalDataErasurePage
			.objectCheckBox(attachment.id, attachment.fileName, true)
			.check();
		await personalDataErasurePage.actionsButton.click();
		await personalDataErasurePage.deleteMenuItem.click();

		await waitForAlert(page);

		await expect(
			personalDataErasurePage.remainingItemsCount('1')
		).toBeVisible();

		await personalDataErasurePage.blogsRadioButton.check();

		await expect(
			personalDataErasurePage.selectAllItemsOnPageCheckbox
		).toBeVisible();

		await personalDataErasurePage
			.objectCheckBox(blog.id, blog.headline, true)
			.check();
		await personalDataErasurePage.actionsButton.click();
		await personalDataErasurePage.deleteMenuItem.click();

		await waitForAlert(page);

		await expect(personalDataErasurePage.anonymizeButton).toBeVisible();

		await personalDataErasurePage.reviewDataLink.click();

		await expect(personalDataErasurePage.emptyMessage).toBeVisible();
		await expect(
			personalDataErasurePage.objectRadioButtonLabelCount('Blogs', '0')
		).toBeVisible();
		await expect(
			personalDataErasurePage.objectRadioButtonLabelCount(
				'Documents and Media',
				'0'
			)
		).toBeVisible();
		await expect(
			personalDataErasurePage.remainingItemsCount('0')
		).toBeVisible();
	}
);

testAdmin(
	'Can delete an export process',
	{tag: '@LPD-56386'},
	async ({
		apiHelpers,
		exportUserDataPage,
		page,
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

		await performUserSwitch(page, userAccount.alternateName);

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: 'Site' + getRandomInt(),
		});

		await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: getRandomString(),
		});

		const contentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId: contentStructureId,
			groupId: site.id,
		});

		await apiHelpers.jsonWebServicesMBApiHelper.addMessage({
			groupId: site.id,
		});

		await performUserSwitch(page, 'test');

		await usersAndOrganizationsPage.goToUsers(false);
		await (
			await usersAndOrganizationsPage.usersTableRowActions(
				userAccount.alternateName
			)
		).click();
		await usersAndOrganizationsPage.exportPersonalDataItem.click();
		await exportUserDataPage.addExportProcessesButton.click();

		await exportUserDataPage.blogsCheckbox.check();
		await exportUserDataPage.webContentCheckbox.check();
		await exportUserDataPage.messageBoardsCheckbox.check();
		await exportUserDataPage.exportButton.click();

		await waitForAlert(page);

		await expect(exportUserDataPage.blogsStatusSuccessful).toBeVisible();
		await expect(
			exportUserDataPage.webContentStatusSuccessful
		).toBeVisible();
		await expect(
			exportUserDataPage.messageBoardsStatusSuccessful
		).toBeVisible();

		await expect(async () => {
			await (
				await exportUserDataPage.rowActions('Blogs', 0, false)
			).click();

			await exportUserDataPage.deleteLink.click({
				timeout: 1000,
			});

			await expect(
				exportUserDataPage.blogsStatusSuccessful
			).not.toBeVisible();
		}).toPass({timeout: 5000});

		await expect(async () => {
			await (
				await exportUserDataPage.rowActions('Message Boards', 0, false)
			).click();

			await exportUserDataPage.deleteLink.click({
				timeout: 1000,
			});

			await expect(
				exportUserDataPage.messageBoardsStatusSuccessful
			).not.toBeVisible();
		}).toPass({timeout: 5000});

		await expect(async () => {
			await (
				await exportUserDataPage.rowActions('Web Content', 0, false)
			).click();

			await exportUserDataPage.deleteLink.click({
				timeout: 1000,
			});

			await expect(
				exportUserDataPage.webContentStatusSuccessful
			).not.toBeVisible();
		}).toPass({timeout: 5000});

		await expect(
			exportUserDataPage.emptyExportProcessesMessage
		).toBeVisible();
	}
);

testAdmin(
	'Can order data in view data',
	{tag: ['@LPD-56386', '@LPS-77749']},
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

		await performUserSwitch(page, userAccount.alternateName);

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		const documentA = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(__dirname, '/dependencies/attachment.txt')
			),
			{
				description: '1',
				fileName: 'A Document',
			}
		);
		const documentB = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(__dirname, '/dependencies/attachment.docx')
			),
			{
				description: '2',
				fileName: 'B Document',
			}
		);
		const documentC = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(__dirname, '/dependencies/attachment.jpeg')
			),
			{
				description: '3',
				fileName: 'C Document',
			}
		);

		await performUserSwitch(page, 'test');

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

		await expect(
			personalDataErasurePage.selectAllItemsOnPageCheckbox
		).toBeVisible();

		await expect(async () => {
			await personalDataErasurePage.orderButton.click();

			await personalDataErasurePage
				.orderMenuItem('Description')
				.click({timeout: 1000});
		}).toPass({timeout: 5000});

		await expect(async () => {
			await personalDataErasurePage.orderButton.click();

			await personalDataErasurePage
				.orderMenuItem('Descending')
				.click({timeout: 1000});
		}).toPass({timeout: 5000});

		await expect(
			personalDataErasurePage.optionalColumnRow(3, 2)
		).toHaveText(documentC.description);
		await expect(
			personalDataErasurePage.optionalColumnRow(3, 3)
		).toHaveText(documentB.description);
		await expect(
			personalDataErasurePage.optionalColumnRow(3, 4)
		).toHaveText(documentA.description);

		await expect(async () => {
			await personalDataErasurePage.orderButton.click();

			await personalDataErasurePage
				.orderMenuItem('Ascending')
				.click({timeout: 1000});
		}).toPass({timeout: 5000});

		await expect(
			personalDataErasurePage.optionalColumnRow(3, 2)
		).toHaveText(documentA.description);
		await expect(
			personalDataErasurePage.optionalColumnRow(3, 3)
		).toHaveText(documentB.description);
		await expect(
			personalDataErasurePage.optionalColumnRow(3, 4)
		).toHaveText(documentC.description);

		await expect(async () => {
			await personalDataErasurePage.orderButton.click();

			await personalDataErasurePage
				.orderMenuItem('Name')
				.click({timeout: 1000});
		}).toPass({timeout: 5000});

		await expect(
			personalDataErasurePage.optionalColumnRow(1, 2)
		).toHaveText(documentA.fileName);
		await expect(
			personalDataErasurePage.optionalColumnRow(1, 3)
		).toHaveText(documentB.fileName);
		await expect(
			personalDataErasurePage.optionalColumnRow(1, 4)
		).toHaveText(documentC.fileName);

		await expect(async () => {
			await personalDataErasurePage.orderButton.click();

			await personalDataErasurePage
				.orderMenuItem('Descending')
				.click({timeout: 1000});
		}).toPass({timeout: 5000});

		await expect(
			personalDataErasurePage.optionalColumnRow(1, 2)
		).toHaveText(documentC.fileName);
		await expect(
			personalDataErasurePage.optionalColumnRow(1, 3)
		).toHaveText(documentB.fileName);
		await expect(
			personalDataErasurePage.optionalColumnRow(1, 4)
		).toHaveText(documentA.fileName);
	}
);

testAdmin(
	'Can delete all entries from instance scope',
	{tag: '@LPD-56386'},
	async ({
		apiHelpers,
		page,
		personalDataErasurePage,
		userAssociatedDataAnnouncementPage,
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

		await performUserSwitch(page, userAccount.alternateName);

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		const announcementsPage =
			await userAssociatedDataAnnouncementPage.createAnnouncementPage(
				apiHelpers,
				site,
				{
					title: 'Announcements Page',
				}
			);

		const announcement =
			await apiHelpers.jsonWebServicesAnnouncementsEntryApiHelper.addEntry(
				{
					content: 'This is an announcement added via json.',
					title: 'Announcement Entry Title',
				}
			);

		await page.goto(
			`/web/${site.name}${announcementsPage.friendlyUrlPath}`
		);

		await expect(page.getByText(announcement.title)).toBeVisible();

		await performUserSwitch(page, 'test');

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
		await expect(personalDataErasurePage.instanceRadioButton).toBeChecked();
		await expect(
			personalDataErasurePage.allApplicationsRadioButton
		).toBeChecked();
		await expect(
			personalDataErasurePage.objectRadioButtonLabelCount(
				'Announcements',
				'1'
			)
		).toBeVisible();

		await personalDataErasurePage.selectAllItemsOnPageCheckbox.check();
		await personalDataErasurePage.actionsButton.click();
		await personalDataErasurePage.deleteMenuItem.click();

		await expect(personalDataErasurePage.anonymizeButton).toBeVisible();

		await page.goto(
			`/web/${site.name}${announcementsPage.friendlyUrlPath}`
		);

		await expect(page.getByText(announcement.title)).toHaveCount(0);
	}
);

testAdmin(
	'Can edit entry from application',
	{tag: '@LPD-56476'},
	async ({
		apiHelpers,
		page,
		personalDataErasurePage,
		userAssociatedDataEditDocumentPage,
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

		await performUserSwitch(page, userAccount.alternateName);

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		const document = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(__dirname, '/dependencies/attachment.txt')
			),
			{
				description: 'This is a document description',
				fileName: 'Name of the file',
			}
		);

		await performUserSwitch(page, 'test');

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

		await expect(async () => {
			await (
				await personalDataErasurePage.userAssociatedDataTableRowActions(
					document.fileName
				)
			).click();

			await personalDataErasurePage.editMenuItem.click({
				timeout: 1000,
			});
		}).toPass({timeout: 5000});

		await expect(
			userAssociatedDataEditDocumentPage.selectFileButton
		).toBeVisible();

		const newDocumentFileName = getRandomString();
		const newDocumentDescription = getRandomString();

		await userAssociatedDataEditDocumentPage.documentFileName.fill(
			newDocumentFileName
		);
		await userAssociatedDataEditDocumentPage.documentDescription.fill(
			newDocumentDescription
		);
		await userAssociatedDataEditDocumentPage.publishButton.click();

		await expect(
			personalDataErasurePage.remainingItemsCount('1')
		).toBeVisible();
		await expect(
			personalDataErasurePage.optionalColumnRow(1, 2)
		).toContainText(newDocumentFileName);
		await expect(
			personalDataErasurePage.optionalColumnRow(3, 2)
		).toContainText(newDocumentDescription);
	}
);

testAdmin(
	'Can delete all entries from personal site scope',
	{tag: '@LPD-56476'},
	async ({
		apiHelpers,
		page,
		personalDataErasurePage,
		productMenuPage,
		userAssociatedDataBlogPage,
		userAssociatedDataEditMessageBoardThreadPage,
		userAssociatedDataMessageBoardPage,
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

		await performUserSwitch(page, userAccount.alternateName);

		// My profile

		await page.goto(`/web/${userAccount.alternateName}`);

		await productMenuPage.goToBlogs();

		await userAssociatedDataBlogPage.newButton.click();
		await userAssociatedDataBlogPage.blogTitleInput.fill(
			'Blog' + getRandomInt()
		);
		await userAssociatedDataBlogPage.blogContentInput.click();
		await userAssociatedDataBlogPage.blogContentInput.fill(
			getRandomString()
		);
		await userAssociatedDataBlogPage.publishButton.click();

		// My dashboard

		await page.goto(`/user/${userAccount.alternateName}`);

		await productMenuPage.goToBlogs();

		await userAssociatedDataBlogPage.newButton.click();
		await userAssociatedDataBlogPage.blogTitleInput.fill(
			'Blog' + getRandomInt()
		);
		await userAssociatedDataBlogPage.blogContentInput.click();
		await userAssociatedDataBlogPage.blogContentInput.fill(
			getRandomString()
		);
		await userAssociatedDataBlogPage.publishButton.click();

		await expect(async () => {
			await productMenuPage.goToMessageBoards();

			await userAssociatedDataMessageBoardPage.newButton.click();
		}).toPass({timeout: 5000});

		await userAssociatedDataMessageBoardPage.threadMenuItem.click();
		await userAssociatedDataEditMessageBoardThreadPage.subjectInput.fill(
			getRandomString()
		);
		await userAssociatedDataEditMessageBoardThreadPage.editorFrameTextInput.click();
		await userAssociatedDataEditMessageBoardThreadPage.editorFrameTextInput.fill(
			getRandomString()
		);
		await userAssociatedDataEditMessageBoardThreadPage.publishButton.click();

		await performUserSwitch(page, 'test');

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

		await expect(
			personalDataErasurePage.personalSiteRadioButton
		).toBeChecked();
		await expect(
			personalDataErasurePage.allApplicationsRadioButton
		).toBeChecked();

		await personalDataErasurePage.selectAllItemsOnPageCheckbox.check();
		await personalDataErasurePage.actionsButton.click();
		await personalDataErasurePage.deleteMenuItem.click();

		await expect(personalDataErasurePage.anonymizeButton).toBeVisible();

		await personalDataErasurePage.reviewDataLink.click();

		await expect(personalDataErasurePage.emptyMessage).toBeVisible();
	}
);

testAdmin(
	'Can filter and order export processes',
	{tag: '@LPD-56476'},
	async ({
		apiHelpers,
		exportUserDataPage,
		page,
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

		await performUserSwitch(page, userAccount.alternateName);

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: 'Blog' + getRandomInt(),
		});

		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(__dirname, '/dependencies/attachment.txt')
			),
			{
				fileName: 'Document' + getRandomInt(),
			}
		);

		await apiHelpers.jsonWebServicesMBApiHelper.addMessage({
			groupId: site.id,
			subject: 'Message' + getRandomInt(),
		});

		await performUserSwitch(page, 'test');

		await usersAndOrganizationsPage.goToUsers(false);
		await (
			await usersAndOrganizationsPage.usersTableRowActions(
				userAccount.alternateName
			)
		).click();

		await usersAndOrganizationsPage.exportPersonalDataItem.click();
		await exportUserDataPage.addExportProcessesButton.click();
		await exportUserDataPage.blogsCheckbox.check();
		await exportUserDataPage.messageBoardsCheckbox.check();
		await exportUserDataPage.documentsAndMediaCheckbox.check();
		await exportUserDataPage.exportButton.click();

		await waitForAlert(page);

		await expect(async () => {
			await exportUserDataPage.filterButton.click();
			await exportUserDataPage
				.filterMenuItem('Successful')
				.click({timeout: 1000});
		}).toPass({timeout: 5000});

		await expect(exportUserDataPage.blogsStatusSuccessful).toBeVisible();
		await expect(
			exportUserDataPage.messageBoardsStatusSuccessful
		).toBeVisible();
		await expect(
			exportUserDataPage.documentsAndMediaStatusSuccessful
		).toBeVisible();
		await expect(exportUserDataPage.statusText('Failed')).toHaveCount(0);
		await expect(exportUserDataPage.statusText('In Progress')).toHaveCount(
			0
		);

		await expect(async () => {
			await exportUserDataPage.orderButton.click();
			await exportUserDataPage.clickOrderMenuItem('Name');
		}).toPass({timeout: 5000});

		await expect(async () => {
			await exportUserDataPage.orderButton.click();
			await exportUserDataPage.clickOrderMenuItem('Descending');
		}).toPass({timeout: 5000});

		await expect(exportUserDataPage.optionalColumnRow(0, 1)).toContainText(
			'Message Boards'
		);
		await expect(exportUserDataPage.optionalColumnRow(0, 2)).toContainText(
			'Documents and Media'
		);
		await expect(exportUserDataPage.optionalColumnRow(0, 3)).toContainText(
			'Blogs'
		);

		await expect(async () => {
			await exportUserDataPage.filterButton.click();
			await exportUserDataPage
				.filterMenuItem('Failed')
				.click({timeout: 1000});
		}).toPass({timeout: 5000});

		await expect(
			exportUserDataPage.emptyExportProcessesMessage
		).toBeVisible();
	}
);

testAdmin(
	'Can delete all entries from regular sites scope',
	{tag: '@LPD-56476'},
	async ({
		apiHelpers,
		blogsPage,
		page,
		personalDataErasurePage,
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

		await performUserSwitch(page, userAccount.alternateName);

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		const blog1 = await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: 'Blog' + getRandomInt(),
		});
		const blog2 = await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: 'Blog' + getRandomInt(),
		});

		await performUserSwitch(page, 'test');

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

		await expect(
			personalDataErasurePage.regularSitesRadioButton
		).toBeChecked();
		await expect(
			personalDataErasurePage.allApplicationsRadioButton
		).toBeChecked();

		await personalDataErasurePage.selectAllItemsOnPageCheckbox.check();
		await personalDataErasurePage.actionsButton.click();
		await personalDataErasurePage.deleteMenuItem.click();

		await expect(personalDataErasurePage.anonymizeButton).toBeVisible();

		await page.goto(`/group/${site.name}${PORTLET_URLS.blogs}`);

		await expect(blogsPage.blogName(blog1.headline)).toHaveCount(0);
		await expect(blogsPage.blogName(blog2.headline)).toHaveCount(0);
	}
);

testAdmin(
	'Can delete entry from application',
	{tag: '@LPD-56476'},
	async ({
		apiHelpers,
		blogsPage,
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

		await performUserSwitch(page, userAccount.alternateName);

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		const blog = await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: 'Blog' + getRandomInt(),
		});

		await performUserSwitch(page, 'test');

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

		await personalDataErasurePage.blogsRadioButton.check();

		await expect(
			personalDataErasurePage.objectRadioButtonLabelCount('Blogs', '1')
		).toBeVisible();
		await expect(
			personalDataErasurePage.remainingItemsCount('1')
		).toBeVisible();

		await expect(async () => {
			await (
				await personalDataErasurePage.userAssociatedDataTableRowActions(
					blog.headline
				)
			).click();

			await personalDataErasurePage.deleteLink.click({
				timeout: 1000,
			});
		}).toPass({timeout: 5000});

		await expect(personalDataErasurePage.anonymizeButton).toBeVisible();

		await personalDataErasurePage.reviewDataLink.click();

		await expect(
			personalDataErasurePage.objectRadioButtonLabelCount('Blogs', '0')
		).toBeVisible();
		await expect(
			personalDataErasurePage.remainingItemsCount('0')
		).toBeVisible();
		await expect(personalDataErasurePage.emptyMessage).toBeVisible();

		await page.goto(`/group/${site.name}${PORTLET_URLS.blogs}`);

		await expect(blogsPage.blogName(blog.headline)).toHaveCount(0);
	}
);

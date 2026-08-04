/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {webContentDisplayPageTest} from '../../../fixtures/webContentDisplayPageTest';
import getRandomString from '../../../utils/getRandomString';
import {
	performLogout,
	performUserSwitchViaApi,
	userData,
} from '../../../utils/performLogin';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {journalPagesTest} from './fixtures/journalPagesTest';
import getDataStructureDefinition from './utils/getDataStructureDefinition';

const IMAGE_FILE_NAME = 'sample_image.png';

const JOURNAL_DDM_STRUCTURE_RESOURCE_NAME =
	'com.liferay.dynamic.data.mapping.model.DDMStructure-com.liferay.journal.model.JournalArticle';

const IMAGE_PATH = path.join(
	__dirname,
	'../../frontend-js-item-selector-web/main/dependencies',
	IMAGE_FILE_NAME
);

const WEB_CONTENT_ADMINISTRATOR_PERMISSIONS = [
	{
		actionIds: [
			'ADD_ARTICLE',
			'ADD_FEED',
			'ADD_FOLDER',
			'ADD_STRUCTURE',
			'ADD_TEMPLATE',
			'PERMISSIONS',
			'SUBSCRIBE',
			'UPDATE',
			'VIEW',
		],
		resourceName: 'com.liferay.journal',
	},
	{
		actionIds: [
			'ADD_DISCUSSION',
			'DELETE',
			'DELETE_DISCUSSION',
			'EXPIRE',
			'PERMISSIONS',
			'SUBSCRIBE',
			'UPDATE',
			'UPDATE_DISCUSSION',
			'VIEW',
		],
		resourceName: 'com.liferay.journal.model.JournalArticle',
	},
	{
		actionIds: [
			'ACCESS',
			'ADD_ARTICLE',
			'ADD_SUBFOLDER',
			'DELETE',
			'PERMISSIONS',
			'SUBSCRIBE',
			'UPDATE',
			'VIEW',
		],
		resourceName: 'com.liferay.journal.model.JournalFolder',
	},
	{
		actionIds: ['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
		resourceName: 'com_liferay_journal_web_portlet_JournalPortlet',
	},
	{
		actionIds: ['VIEW_SITE_ADMINISTRATION'],
		resourceName: 'com.liferay.portal.kernel.model.Group',
	},
];

const baseTest = mergeTests(dataApiHelpersTest, journalPagesTest, loginTest());

const ckeditor5Test = mergeTests(
	baseTest,
	featureFlagsTest({'LPD-11235': {enabled: false}})
);

const isolatedSiteCkeditor5Test = mergeTests(ckeditor5Test, isolatedSiteTest);

const guestPermissionTest = mergeTests(
	baseTest,
	isolatedSiteTest,
	pageViewModePagesTest,
	webContentDisplayPageTest
);

ckeditor5Test(
	'A site administrator can add a web content with an image based on a structure from the parent site',
	{
		tag: '@LPS-145308',
	},
	async ({
		apiHelpers,
		journalEditArticlePage,
		journalEditStructureDefaultValuesPage,
		page,
	}) => {
		const description = getRandomString();
		const structureName = getRandomString();
		const webContentTitle = getRandomString();

		// Create a parent site and a child site under it

		const parentSite = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		const childSite = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
			parentSiteExternalReferenceCode: parentSite.externalReferenceCode,
		});

		// Create a structure with an image field on the parent site

		const structure = await apiHelpers.dataEngine.createStructure(
			parentSite.id,
			getDataStructureDefinition({
				defaultLanguageId: 'en_US',
				fields: [{fieldType: 'image', name: 'Image'}],
				name: structureName,
			})
		);

		// Let every user view the structure from the parent site

		const company =
			await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
				'liferay.com'
			);

		const userRole =
			await apiHelpers.headlessAdminUser.getRoleByName('User');

		await apiHelpers.jsonWebServicesResourcePermissionApiHelper.setIndividualResourcePermissions(
			['VIEW'],
			String(company.companyId),
			String(parentSite.id),
			JOURNAL_DDM_STRUCTURE_RESOURCE_NAME,
			String(structure.id),
			String(userRole.id)
		);

		// Edit the default values of the structure

		await journalEditStructureDefaultValuesPage.goto({
			siteUrl: parentSite.friendlyUrlPath,
			structureName,
		});

		await journalEditStructureDefaultValuesPage.fillRichTextField(
			'Description',
			description
		);

		await journalEditStructureDefaultValuesPage.save();

		await journalEditStructureDefaultValuesPage.goto({
			siteUrl: parentSite.friendlyUrlPath,
			structureName,
		});

		await expect(
			journalEditStructureDefaultValuesPage.getRichTextField(
				'Description'
			)
		).toContainText(description);

		// Make a site administrator of the child site

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
			childSite.id,
			user.id
		);

		await performUserSwitchViaApi(page, user.alternateName);

		// Add a web content on the child site based on the parent structure

		await journalEditArticlePage.goto({
			siteUrl: childSite.friendlyUrlPath,
			structureName,
		});

		await journalEditArticlePage.fillTitle(webContentTitle);

		await journalEditArticlePage.uploadImageFromWebContentImages(
			IMAGE_PATH
		);

		await journalEditArticlePage.publishArticle();

		// The uploaded image is kept on the image field

		await journalEditArticlePage.editArticle(webContentTitle);

		await expect(page.getByLabel('Image', {exact: true})).toHaveValue(
			IMAGE_FILE_NAME
		);

		await expect(
			page.locator('.image-picker-preview img[src*="/documents/"]')
		).toBeVisible();
	}
);

isolatedSiteCkeditor5Test(
	'A user with a web content administrator role can edit a web content',
	{
		tag: '@LPD-100988',
	},
	async ({apiHelpers, journalEditArticlePage, journalPage, page, site}) => {
		const webContentContent = getRandomString();
		const webContentContentEdit = getRandomString();
		const webContentTitle = getRandomString();
		const webContentTitleEdit = getRandomString();

		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const webContent =
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				content: webContentContent,
				ddmStructureId: basicWebContentStructureId,
				groupId: site.id,
				titleMap: {en_US: webContentTitle},
			});

		apiHelpers.data.push({
			id: `${site.id}_${webContent.articleId}`,
			type: 'webContent',
		});

		// Create a role able to administer web contents

		const company =
			await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
				'liferay.com'
			);

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			rolePermissions: WEB_CONTENT_ADMINISTRATOR_PERMISSIONS.map(
				({actionIds, resourceName}) => ({
					actionIds,
					primaryKey: String(company.companyId),
					resourceName,
					scope: 1,
				})
			),
			roleType: 'regular',
		});

		// Assign the role and the site to a new user

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role.externalReferenceCode,
			user.id
		);

		await apiHelpers.jsonWebServicesUser.assignUsersToSite(
			String(site.id),
			String(user.id)
		);

		await performUserSwitchViaApi(page, user.alternateName);

		// Edit the web content as the new user

		await journalPage.goto(site.friendlyUrlPath);

		await journalEditArticlePage.editArticle(webContentTitle);

		await journalEditArticlePage.fillTitle(webContentTitleEdit);

		await journalEditArticlePage.fillContent(webContentContentEdit);

		await journalEditArticlePage.publishArticle(true);

		await expect(
			page.getByTitle(webContentTitleEdit, {exact: true})
		).toBeVisible();

		// The new user shows up as the last editor

		await journalPage.changeView('list');

		await expect(
			page
				.locator('.list-group-item')
				.filter({hasText: webContentTitleEdit})
		).toContainText(`${user.givenName} ${user.familyName}`);
	}
);

guestPermissionTest(
	'A web content is hidden from guests after removing their view permission',
	{
		tag: '@LPD-100988',
	},
	async ({apiHelpers, page, site, webContentDisplayPage, widgetPagePage}) => {
		const webContentContent = getRandomString();
		const webContentTitle = getRandomString();

		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const webContent =
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				content: webContentContent,
				ddmStructureId: basicWebContentStructureId,
				groupId: site.id,
				titleMap: {en_US: webContentTitle},
			});

		apiHelpers.data.push({
			id: `${site.id}_${webContent.articleId}`,
			type: 'webContent',
		});

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {type: 'portlet'},
			title: getRandomString(),
		});

		const company =
			await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
				'liferay.com'
			);

		const guestRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Guest');

		await apiHelpers.jsonWebServicesResourcePermissionApiHelper.removeResourcePermission(
			'VIEW',
			String(company.companyId),
			String(site.id),
			'com.liferay.journal.model.JournalArticle',
			String(webContent.resourcePrimKey),
			String(guestRole.id),
			'4'
		);

		// Display the web content through a Web Content Display widget

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await page.getByLabel('Add', {exact: true}).click();

		await widgetPagePage.addPortlet(
			'Web Content Display',
			'Content Management'
		);

		await webContentDisplayPage.addWebContentWithDisplay({
			pageType: 'widget',
			webContentName: webContentTitle,
		});

		await expect(page.getByText(webContentContent)).toBeVisible();

		// Guests reach the page but neither the widget nor the content

		await performLogout(page);

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await expect(page).toHaveURL(new RegExp(layout.friendlyURL));

		await expect(page.getByText('Web Content Display')).toBeHidden();

		await expect(page.getByText(webContentContent)).toBeHidden();
	}
);

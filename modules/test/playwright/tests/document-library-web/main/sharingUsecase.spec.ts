/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import * as path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {sharingPagesTest} from '../../../fixtures/sharingPagesTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {SystemSettingsPage} from '../../../pages/configuration-admin-web/SystemSettingsPage';
import {DocumentLibraryPage} from '../../../pages/document-library-web/DocumentLibraryPage';
import {SharePage} from '../../../pages/sharing-web/SharePage';
import {SharedContentPage} from '../../../pages/sharing-web/SharedContentPage';
import getRandomString from '../../../utils/getRandomString';
import {
	createRecipient,
	withRecipientPage,
} from '../../../utils/sharingRecipient';
import {blogsPagesTest} from '../../blogs-web/main/fixtures/blogsPagesTest';
import {BlogsPage} from '../../blogs-web/main/pages/BlogsPage';

const test = mergeTests(
	loginTest(),
	dataApiHelpersTest,
	isolatedSiteTest,
	documentLibraryPagesTest,
	blogsPagesTest,
	sharingPagesTest,
	systemSettingsPageTest
);

const IMAGE_PATH = path.join(__dirname, 'dependencies', 'image1.jpeg');

// The virtual-instance (company) scope of the Sharing configuration. The Poshi
// suite also covered the system scope, but toggling a system-scoped
// configuration restarts the JAX-RS whiteboard and cannot run against the
// shared Playwright runtime, so that case stays in Poshi.

const SHARING_COMPANY_CONFIGURATION_PID =
	'com.liferay.sharing.internal.configuration.SharingCompanyConfiguration';

async function createDocument(
	apiHelpers: DataApiHelpers,
	siteId: string,
	title: string
) {
	return apiHelpers.headlessDelivery.postDocument(
		siteId,
		createReadStream(IMAGE_PATH),
		{fileName: `${title}.jpeg`, title, viewableBy: 'Owner'}
	);
}

async function newRecipient(apiHelpers: DataApiHelpers) {
	const user = await createRecipient(apiHelpers);

	return {
		emailAddress: user.emailAddress,
		fullName: `${user.givenName} ${user.familyName}`,
		screenName: user.alternateName,
	};
}

async function setSharingEnabled(
	systemSettingsPage: SystemSettingsPage,
	enabled: boolean
) {

	// The System Settings "Sharing" category card lands directly on the System
	// Scope configuration, which makes the scope side-nav item the active link.
	// Clicking it reloads the page and detaches the element mid-click, so
	// navigate straight to the virtual-instance configuration instead.

	await systemSettingsPage.page.goto(
		`/group/control_panel/manage?p_p_id=com_liferay_configuration_admin_web_portlet_SystemSettingsPortlet&p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view&_com_liferay_configuration_admin_web_portlet_SystemSettingsPortlet_factoryPid=${SHARING_COMPANY_CONFIGURATION_PID}&_com_liferay_configuration_admin_web_portlet_SystemSettingsPortlet_mvcRenderCommandName=%2Fconfiguration_admin%2Fedit_configuration&_com_liferay_configuration_admin_web_portlet_SystemSettingsPortlet_pid=${SHARING_COMPANY_CONFIGURATION_PID}`
	);

	await systemSettingsPage.checkOption('Enabled', enabled);

	await systemSettingsPage.saveAndWaitForAlert();
}

async function shareBlog(
	blogsPage: BlogsPage,
	sharePage: SharePage,
	{
		emailAddress,
		friendlyUrlPath,
		headline,
	}: {emailAddress: string; friendlyUrlPath: string; headline: string}
) {
	await blogsPage.goto(friendlyUrlPath);

	await blogsPage.goToBlogEntryAction('Share', headline);

	await sharePage.share(emailAddress);
}

async function shareDocument(
	documentLibraryPage: DocumentLibraryPage,
	sharePage: SharePage,
	{
		emailAddress,
		friendlyUrlPath,
		title,
	}: {emailAddress: string; friendlyUrlPath: string; title: string}
) {
	await documentLibraryPage.goto(friendlyUrlPath);

	await documentLibraryPage.goToShareFileEntry(title);

	await sharePage.share(emailAddress);
}

test(
	'Can filter shared content by asset type',
	{tag: '@LPD-97157'},
	async ({
		apiHelpers,
		blogsPage,
		browser,
		documentLibraryPage,
		sharePage,
		site,
	}) => {
		const documentTitle = getRandomString();
		const headline = getRandomString();

		await createDocument(apiHelpers, site.id, documentTitle);

		await apiHelpers.headlessDelivery.postBlog(site.id, {headline});

		const recipient = await newRecipient(apiHelpers);

		await shareDocument(documentLibraryPage, sharePage, {
			emailAddress: recipient.emailAddress,
			friendlyUrlPath: site.friendlyUrlPath,
			title: documentTitle,
		});

		await shareBlog(blogsPage, sharePage, {
			emailAddress: recipient.emailAddress,
			friendlyUrlPath: site.friendlyUrlPath,
			headline,
		});

		await withRecipientPage(
			browser,
			recipient.screenName,
			async (recipientPage) => {
				const sharedContentPage = new SharedContentPage(recipientPage);

				await sharedContentPage.goto('Shared with Me');

				// Filter to documents only.

				await sharedContentPage.filterByAssetType('Document');

				await expect(
					sharedContentPage.entryLink(documentTitle)
				).toBeVisible();

				await expect(sharedContentPage.entryRow(headline)).toBeHidden();

				// Filter to blog entries only.

				await sharedContentPage.filterByAssetType('Blogs Entry', {
					restore: true,
				});

				await expect(
					sharedContentPage.entryLink(headline)
				).toBeVisible();

				await expect(
					sharedContentPage.entryRow(documentTitle)
				).toBeHidden();
			}
		);
	}
);

test(
	'Cannot share when sharing is disabled at the virtual instance scope',
	{tag: '@LPD-97157'},
	async ({
		apiHelpers,
		blogsPage,
		documentLibraryPage,
		site,
		systemSettingsPage,
	}) => {
		const documentTitle = getRandomString();
		const headline = getRandomString();

		try {
			await setSharingEnabled(systemSettingsPage, false);

			await createDocument(apiHelpers, site.id, documentTitle);

			await apiHelpers.headlessDelivery.postBlog(site.id, {headline});

			await documentLibraryPage.goto(site.friendlyUrlPath);

			await documentLibraryPage.assertFileEntryActionAbsent(
				'Share',
				documentTitle
			);

			await blogsPage.goto(site.friendlyUrlPath);

			await blogsPage.assertBlogEntryActionAbsent('Share', headline);
		}
		finally {
			await setSharingEnabled(systemSettingsPage, true);
		}
	}
);

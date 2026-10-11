/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {sharingPagesTest} from '../../../fixtures/sharingPagesTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {DocumentLibraryPage} from '../../../pages/document-library-web/DocumentLibraryPage';
import {SharedContentPage} from '../../../pages/sharing-web/SharedContentPage';
import {SharedContentViewerPage} from '../../../pages/sharing-web/SharedContentViewerPage';
import {SharingNotificationPage} from '../../../pages/sharing-web/SharingNotificationPage';
import getRandomString from '../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {
	createRecipient,
	withRecipientPage,
} from '../../../utils/sharingRecipient';
import {waitForAlert} from '../../../utils/waitForAlert';
import addEditableDepot from './utils/addEditableDepot';

const test = mergeTests(
	dataApiHelpersTest,
	documentLibraryPagesTest,
	isolatedSiteTest,
	loginTest(),
	sharingPagesTest
);

const DEPOT_ADMIN_NAMESPACE =
	'_com_liferay_depot_web_portlet_DepotAdminPortlet_';

async function addDocument(
	apiHelpers: DataApiHelpers,
	assetLibraryId: number | string
) {
	const title = getRandomString();

	await apiHelpers.headlessDelivery.postAssetLibraryDocument(
		assetLibraryId,
		createReadStream(path.join(__dirname, 'dependencies/Document.jpg')),
		{fileName: `${title}.jpg`, title}
	);

	return title;
}

async function openDocument(
	documentLibraryPage: DocumentLibraryPage,
	depotURL: string,
	title: string
) {
	await documentLibraryPage.goto(depotURL);

	await documentLibraryPage.page
		.getByRole('link', {exact: true, name: title})
		.click();

	await expect(
		documentLibraryPage.page.getByRole('heading', {name: title}).first()
	).toBeVisible();
}

async function gotoDepotGeneralSettings(page: Page, depotEntryId: string) {
	await page.goto(
		`/group/guest${PORTLET_URLS.depotAdmin}&${DEPOT_ADMIN_NAMESPACE}mvcRenderCommandName=%2Fdepot%2Fedit_depot_entry&${DEPOT_ADMIN_NAMESPACE}depotEntryId=${depotEntryId}&${DEPOT_ADMIN_NAMESPACE}screenNavigationEntryKey=general`
	);

	const sharingPanel = page.getByRole('button', {
		exact: true,
		name: 'Sharing',
	});

	if ((await sharingPanel.getAttribute('aria-expanded')) !== 'true') {
		await sharingPanel.click();
	}
}

test(
	'Sharing can be disabled in a depot',
	{tag: '@LPD-109572'},
	async ({apiHelpers, documentLibraryPage, page}) => {
		const depot = await addEditableDepot(apiHelpers);

		const title = await addDocument(apiHelpers, depot.depotEntryId);

		const depotURL = `/asset-library-${depot.depotEntryId}`;
		const shareButton = page.getByRole('button', {
			exact: true,
			name: 'Share',
		});

		await openDocument(documentLibraryPage, depotURL, title);

		await expect(shareButton).toBeVisible();

		await test.step('Disable sharing in the depot settings', async () => {
			await gotoDepotGeneralSettings(page, depot.depotEntryId);

			await page.getByLabel('Enable Sharing').uncheck();

			await page.getByRole('button', {exact: true, name: 'Save'}).click();

			await waitForAlert(page);

			await gotoDepotGeneralSettings(page, depot.depotEntryId);

			await expect(page.getByLabel('Enable Sharing')).not.toBeChecked();
		});

		await openDocument(documentLibraryPage, depotURL, title);

		await expect(shareButton).toHaveCount(0);
	}
);

test(
	'A depot document shared with a user reaches them with the shared permission',
	{tag: '@LPD-109572'},
	async ({
		apiHelpers,
		browser,
		documentLibraryPage,
		page,
		sharePage,
		site,
	}) => {
		const assetLibrary =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: getRandomString(),
				type: 'AssetLibrary',
			});

		await apiHelpers.headlessAssetLibrary.connectSite(
			assetLibrary.externalReferenceCode,
			site.externalReferenceCode
		);

		// The notification is only shown in a site connected to the depot

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: String(site.id),
			title: getRandomString(),
		});

		const title = await addDocument(apiHelpers, assetLibrary.id);

		const recipient = await createRecipient(apiHelpers);

		const depotURL = `/asset-library-${assetLibrary.id}`;

		await test.step('Share the document from the depot', async () => {
			await documentLibraryPage.goto(depotURL);

			await documentLibraryPage.goToShareFileEntry(title);

			await sharePage.share(recipient.emailAddress, {
				permission: 'Update',
			});

			await documentLibraryPage.goto(depotURL);

			await expect(
				page
					.locator('.card', {hasText: title})
					.locator('.lexicon-icon-users')
			).toBeVisible();
		});

		await withRecipientPage(
			browser,
			recipient.alternateName,
			async (recipientPage) => {
				const sharingNotificationPage = new SharingNotificationPage(
					recipientPage
				);

				await recipientPage.goto(`/web${site.friendlyUrlPath}`);

				await sharingNotificationPage.goToNotifications();

				await sharingNotificationPage
					.sharedContentNotification('Test Test', title, 'updating')
					.click();

				await new SharedContentViewerPage(
					recipientPage
				).assertSharingPermission(title, 'Update');

				const sharedContentPage = new SharedContentPage(recipientPage);

				await sharedContentPage.goto(
					'Shared with Me',
					site.friendlyUrlPath.slice(1)
				);

				await expect(sharedContentPage.entryRow(title)).toHaveCount(1);
			}
		);
	}
);

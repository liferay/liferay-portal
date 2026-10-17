/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {DocumentLibraryPage} from '../../../pages/document-library-web/DocumentLibraryPage';
import {WebContentPage} from '../../../pages/journal-web/WebContentPage';
import getRandomString from '../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {BlogsEditBlogEntryPage} from '../../blogs-web/main/pages/BlogsEditBlogEntryPage';
import {depotsPagesTest} from './fixtures/depotsPagesTest';
import addEditableDepot from './utils/addEditableDepot';

const test = mergeTests(
	dataApiHelpersTest,
	depotsPagesTest,
	isolatedSiteTest,
	loginTest()
);

const APPLICATIONS = [
	'Collections',
	'Documents and Media',
	'Translation Processes',
	'Web Content',
];

function getApplicationCard(page: Page, application: string) {
	return page.locator('.card-page-item-asset').filter({hasText: application});
}

test(
	'Depot applications can be disabled and enabled again with their content',
	{tag: '@LPD-109572'},
	async ({apiHelpers, depotAdminPage, page}) => {
		const depot = await addEditableDepot(apiHelpers);

		const documentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postAssetLibraryDocument(
			depot.depotEntryId,
			createReadStream(path.join(__dirname, 'dependencies/Document.jpg')),
			{fileName: `${documentTitle}.jpg`, title: documentTitle}
		);

		const webContentTitle = getRandomString();

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId: await getBasicWebContentStructureId(apiHelpers),
			groupId: depot.groupId,
			titleMap: {en_US: webContentTitle},
		});

		const collectionTitle = getRandomString();

		await apiHelpers.jsonWebServicesAssetListEntry.addManualAssetListEntry({
			groupId: String(depot.groupId),
			title: collectionTitle,
		});

		await test.step('All applications are enabled by default', async () => {
			await depotAdminPage.gotoGeneralSettings(depot.depotEntryId);

			for (const application of APPLICATIONS) {
				await expect(
					page.getByLabel(application, {exact: true})
				).toBeChecked();
			}
		});

		await test.step('Disabled applications leave the depot dashboard', async () => {
			await depotAdminPage.setApplications(
				depot.depotEntryId,
				APPLICATIONS,
				false
			);

			await depotAdminPage.gotoDashboard(depot.depotEntryId);

			for (const application of [...APPLICATIONS, 'Recycle Bin']) {
				await expect(getApplicationCard(page, application)).toHaveCount(
					0
				);
			}
		});

		await test.step('Enabled applications are back with their content', async () => {
			await depotAdminPage.setApplications(
				depot.depotEntryId,
				APPLICATIONS,
				true
			);

			await depotAdminPage.gotoDashboard(depot.depotEntryId);

			for (const application of APPLICATIONS) {
				await expect(
					getApplicationCard(page, application)
				).toBeVisible();
			}

			const depotURL = `/asset-library-${depot.depotEntryId}`;

			await new DocumentLibraryPage(page).goto(depotURL);

			await expect(
				page.getByRole('link', {exact: true, name: documentTitle})
			).toBeVisible();

			await new WebContentPage(page).goto(depotURL);

			await expect(page.getByText(webContentTitle)).toBeVisible();

			await page.goto(
				`/group${depotURL}/~/control_panel/manage?p_p_id=com_liferay_asset_list_web_portlet_AssetListPortlet`
			);

			await expect(page.getByText(collectionTitle)).toBeVisible();
		});
	}
);

test(
	'A connected site cannot select depot documents once the depot disables Documents and Media',
	{tag: '@LPD-109572'},
	async ({apiHelpers, depotAdminPage, page, site}) => {
		const depot = await addEditableDepot(apiHelpers);

		await apiHelpers.headlessAssetLibrary.connectSite(
			depot.externalReferenceCode,
			site.externalReferenceCode
		);

		await depotAdminPage.setApplications(
			depot.depotEntryId,
			['Documents and Media'],
			false
		);

		await new BlogsEditBlogEntryPage(page).goto(site.friendlyUrlPath);

		await page.getByRole('button', {name: 'Select File'}).first().click();

		const itemSelector = page.frameLocator('iframe[title="Select File"]');

		await itemSelector
			.getByRole('link', {name: 'Documents and Media'})
			.click();

		await itemSelector
			.getByRole('link', {exact: true, name: 'Sites and Libraries'})
			.click();

		await itemSelector.getByText('Asset Library', {exact: true}).click();

		await itemSelector.getByText(depot.name, {exact: true}).click();

		await expect(
			itemSelector.getByText(
				'This application is not supported for Asset Libraries.'
			)
		).toBeVisible();
	}
);

test(
	'The breadcrumb of a depot application leads back to the depot',
	{tag: ['@LPD-109572', '@LPS-133904']},
	async ({apiHelpers, page}) => {
		const assetLibrary =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: getRandomString(),
				type: 'AssetLibrary',
			});

		const depotURL = `/asset-library-${assetLibrary.id}`;

		for (const gotoApplication of [
			() => new DocumentLibraryPage(page).goto(depotURL),
			() => page.goto(`/group${depotURL}${PORTLET_URLS.translation}`),
			() => new WebContentPage(page).goto(depotURL),
		]) {
			await gotoApplication();

			await page
				.locator('.breadcrumb')
				.getByRole('link', {exact: true, name: assetLibrary.name})
				.click();

			await expect(getApplicationCard(page, 'Web Content')).toBeVisible();
		}
	}
);

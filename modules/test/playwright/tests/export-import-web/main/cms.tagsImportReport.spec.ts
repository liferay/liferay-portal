/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {tagsPagesTest} from '../../asset-tags-admin-web/main/fixtures/tagsAdminPagesTest';
import {portletExportImportPageTest} from './fixtures/portletExportImportPageTest';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-57655': {enabled: false},
	}),
	isolatedSiteTest,
	loginTest(),
	portletExportImportPageTest,
	tagsPagesTest
);

test('Can view report entries from Tags Export/Import after import with errors', async ({
	apiHelpers,
	page,
	portletExportImportPage,
	site,
	tagsAdminPage,
}) => {
	const tagName = 'tag1';

	const tag = await apiHelpers.headlessAdminTaxonomy.postSiteKeyword({
		name: tagName,
		siteId: site.id,
	});

	await tagsAdminPage.goto(site.friendlyUrlPath);

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page
			.locator('.dropdown-menu')
			.getByRole('menuitem', {name: 'Export / Import'}),
		trigger: page.getByLabel('Options'),
	});

	const {filePath} = await portletExportImportPage.exportLARFile({
		fileNamePattern: /.*\.portlet\.lar/,
	});

	await apiHelpers.headlessAdminTaxonomy.deleteKeyword({id: tag.id});

	await apiHelpers.headlessAdminTaxonomy.postSiteKeyword({
		name: tagName,
		siteId: site.id,
	});

	await tagsAdminPage.goto(site.friendlyUrlPath);

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page
			.locator('.dropdown-menu')
			.getByRole('menuitem', {name: 'Export / Import'}),
		trigger: page.getByLabel('Options'),
	});

	await portletExportImportPage.importLARFile({
		filePath,
		taskStatus: 'completedWithErrors',
	});

	await clickAndExpectToBeVisible({
		target: portletExportImportPage.viewReportEntriesMenuItem,
		trigger: portletExportImportPage.taskActionsMenu,
	});

	await portletExportImportPage.viewReportEntriesMenuItem.click();

	await expect(
		portletExportImportPage.frame.getByRole('link', {
			name: 'Report Entries',
		})
	).toBeVisible();
});

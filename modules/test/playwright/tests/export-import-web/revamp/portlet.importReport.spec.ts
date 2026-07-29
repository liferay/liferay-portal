/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {tagsPagesTest} from '../../asset-tags-admin-web/main/fixtures/tagsAdminPagesTest';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';
import {importReportPagesTest} from './fixtures/importReportPagesTest';

export const test = mergeTests(
	dataApiHelpersTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-57655': {enabled: true},
	}),
	importReportPagesTest,
	isolatedSiteTest,
	loginTest(),
	tagsPagesTest
);

test(
	'Can view report entries from a Tags import with errors',
	{tag: '@LPD-99961'},
	async ({
		apiHelpers,
		exportImportPage,
		importReportPage,
		page,
		site,
		tagsAdminPage,
	}) => {
		const tagName = `tag-${getRandomString()}`;

		const tag = await apiHelpers.headlessAdminTaxonomy.postSiteKeyword({
			name: tagName,
			siteId: site.id,
		});

		await tagsAdminPage.goToOptionsMenu(site.friendlyUrlPath);
		await exportImportPage.exportMenuItem.click();

		await expect(exportImportPage.newButton).toBeVisible();

		const exportName = `MyExport-${getRandomString()}`;

		await exportImportPage.export(exportName);

		await expect(
			exportImportPage.taskStatusLabel(exportName)
		).toBeVisible();

		const folderPath = await exportImportPage.download(exportName);

		await apiHelpers.headlessAdminTaxonomy.deleteKeyword({id: tag.id});

		await apiHelpers.headlessAdminTaxonomy.postSiteKeyword({
			name: tagName,
			siteId: site.id,
		});

		await tagsAdminPage.goToOptionsMenu(site.friendlyUrlPath);
		await exportImportPage.importMenuItem.click();

		await expect(exportImportPage.newButton).toBeVisible();

		await exportImportPage.newButton.click();

		const importName = `MyImport-${getRandomString()}`;

		await exportImportPage.import({
			folderPath,
			name: importName,
			taskStatus: 'completedWithErrors',
		});

		await exportImportPage.goToImportDetails(importName);

		await expect(
			page.getByRole('cell', {name: tag.externalReferenceCode})
		).toBeVisible();

		await importReportPage.goToEntryDetails(tag.externalReferenceCode);

		await expect(page.getByText('ScopeSite')).toBeVisible();
	}
);

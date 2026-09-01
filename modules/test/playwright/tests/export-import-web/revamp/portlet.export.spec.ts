/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import getRandomString from '../../../utils/getRandomString';
import {stagingPageTest} from '../main/fixtures/stagingPageTest';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';

export const test = mergeTests(
	documentLibraryPagesTest,
	productMenuPageTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-57655': {enabled: true},
	}),
	loginTest(),
	stagingPageTest
);

const portletTest = mergeTests(
	apiHelpersTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-57655': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageViewModePagesTest
);

test('Can export using the new navigation buttons', async ({
	documentLibraryPage,
	exportImportPage,
}) => {
	await documentLibraryPage.goto();
	await documentLibraryPage.openOptionsMenu();
	await exportImportPage.exportMenuItem.click();

	await expect(exportImportPage.newButton).toBeVisible();

	const exportName = `Test export-${getRandomString()}`;

	await exportImportPage.export(exportName);
});

portletTest(
	'Can export a portlet from its page topper menu',
	{tag: '@LPD-104237'},
	async ({apiHelpers, exportImportPage, page, site, widgetPagePage}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
			typeSettings:
				'layout-template-id=1_column\ncolumn-1=com_liferay_document_library_web_portlet_DLPortlet\n',
		});

		await apiHelpers.headlessDelivery.postDocumentFolder(site.id);

		await page.goto('/web' + site.friendlyUrlPath + layout.friendlyURL);

		await widgetPagePage.clickOnAction('Documents and Media', 'Export');

		await expect(
			page.locator('.portlet-title-text', {
				hasText: 'Export Documents and Media',
			})
		).toBeVisible();

		await exportImportPage.clickNew();

		await expect(
			page.getByText('1 Item', {exact: true}).first()
		).toBeVisible();

		const exportName = getRandomString();

		await exportImportPage.nameInput.fill(exportName);

		await exportImportPage.exportButton.click();

		await expect(
			exportImportPage.taskStatusLabel(exportName)
		).toBeVisible();
	}
);

test(
	'sanitizes a javascript: redirect parameter on the portlet export Cancel link',
	{tag: '@LPD-92456'},
	async ({page}) => {
		const NAMESPACE =
			'_com_liferay_exportimport_web_portlet_ExportImportPortlet_';

		const searchParams = new URLSearchParams({
			p_p_id: 'com_liferay_exportimport_web_portlet_ExportImportPortlet',
			p_p_lifecycle: '0',
			[NAMESPACE + 'mvcPath']: '/export_portlet.jsp',
			[NAMESPACE + 'portletConfiguration']: 'true',
			[NAMESPACE + 'portletResource']:
				'com_liferay_journal_web_portlet_JournalPortlet',
			[NAMESPACE + 'redirect']: 'javascript:alert(document.domain)',
			[NAMESPACE + 'resourcePrimKey']:
				'1_LAYOUT_com_liferay_journal_web_portlet_JournalPortlet',
		});

		await page.goto(
			`/group/guest/~/control_panel/manage?${searchParams.toString()}`
		);

		const cancelLink = page.getByRole('button', {name: 'Cancel'});

		await expect(cancelLink).toBeVisible();

		await expect(cancelLink).toHaveAttribute(
			'href',
			/^(?!\s*javascript:)/i
		);
	}
);

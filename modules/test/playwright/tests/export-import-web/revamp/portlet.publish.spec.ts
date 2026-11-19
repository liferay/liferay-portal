/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import getRandomString from '../../../utils/getRandomString';
import {enableLocalStaging} from '../../../utils/staging';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';

export const test = mergeTests(
	dataApiHelpersTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-96689': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageViewModePagesTest
);

test(
	'Can publish a portlet from its page topper menu',
	{tag: '@LPD-116356'},
	async ({apiHelpers, exportImportPage, page, site, widgetPagePage}) => {
		await apiHelpers.headlessDelivery.postDocumentFolder(site.id);

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
			typeSettings:
				'layout-template-id=1_column\ncolumn-1=com_liferay_document_library_web_portlet_DLPortlet_INSTANCE_publish\n',
		});

		await enableLocalStaging(apiHelpers, page, site);

		await page.goto(
			`/web${site.friendlyUrlPath}-staging${layout.friendlyURL}`
		);

		await widgetPagePage.clickOnAction('Documents and Media', 'Staging');

		await expect(
			page.locator('.portlet-title-text', {
				hasText: 'Staging - Documents and Media',
			})
		).toBeVisible();

		await expect(
			page.getByRole('link', {exact: true, name: 'Scheduled'})
		).toBeHidden();

		await exportImportPage.clickNew();

		await expect(
			page.getByText('1 Item', {exact: true}).first()
		).toBeVisible();
		await expect(
			page.locator('.portlet-title-text', {
				hasText: 'New Publish Process - Documents and Media',
			})
		).toBeVisible();

		await expect(
			page.getByRole('radio', {name: /Schedule for Later/})
		).toBeHidden();

		const name = `Publication-${getRandomString()}`;

		await exportImportPage.nameInput.fill(name);

		await page
			.locator('form')
			.getByRole('button', {name: 'Publish to Live'})
			.click();

		await expect(exportImportPage.taskStatusLabel(name)).toBeVisible();
	}
);

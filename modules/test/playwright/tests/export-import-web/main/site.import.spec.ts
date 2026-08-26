/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {accountSettingsPagesTest} from '../../../fixtures/accountSettingsPagesTest';
import {accountsPagesTest} from '../../../fixtures/accountsPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {depotAdminPageTest} from '../../../fixtures/depotAdminPageTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import {styleBookPageTest} from '../../../fixtures/styleBookPageTest';
import {uiElementsPageTest} from '../../../fixtures/uiElementsTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import getRandomString from '../../../utils/getRandomString';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';
import {stagingPageTest} from './fixtures/stagingPageTest';

export const test = mergeTests(
	accountSettingsPagesTest,
	accountsPagesTest,
	dataApiHelpersTest,
	depotAdminPageTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-35013': {enabled: true},
		'LPD-35443': {enabled: false},
		'LPD-44307': {enabled: true},
		'LPD-44771': {enabled: true},
		'LPD-45276': {enabled: true},
		'LPD-57655': {enabled: false},
		'LPD-76864': {enabled: true},
	}),
	globalMenuPagesTest,
	isolatedSiteTest,
	loginTest(),
	objectPagesTest,
	productMenuPageTest,
	stagingPageTest,
	styleBookPageTest,
	usersAndOrganizationsPagesTest,
	uiElementsPageTest
);

const testWithDeprecationFFDisabled = mergeTests(
	exportImportPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-35443': {enabled: false},
		'LPD-44307': {enabled: false},
		'LPD-44771': {enabled: false},
		'LPD-57655': {enabled: false},
	}),
	loginTest(),
	uiElementsPageTest
);

test(
	'Can XSS with `searchContainerId` in Asset Libraries import',
	{tag: '@LPS-195766'},
	async ({apiHelpers, depotAdminPage, page}) => {
		const depotName = getRandomString();

		await apiHelpers.jsonWebServicesDepot.addDepotEntry(depotName);

		await depotAdminPage.goToDepotByName(depotName);

		await depotAdminPage.gotoImport();

		const paramName =
			'_com_liferay_exportimport_web_portlet_ImportPortlet_searchContainerId';

		const requestPromise = page.waitForRequest(
			(request) =>
				request.method() === 'GET' && request.url().includes(paramName)
		);

		const request = await requestPromise;

		const insertString = '%22%3E%3Cimg%20src=1%20onerror=alert(123)%3E';

		const [urlBase, urlParam] = request.url().split(`${paramName}=`);

		const newUrl = `${urlBase}${paramName}=${urlParam.replace(/([^&]+)/, `$1${insertString}`)}`;

		let alertTriggered = false;

		page.on('dialog', async (dialog) => {
			if (dialog.type() === 'alert') {
				alertTriggered = true;
				await dialog.dismiss();
			}
		});

		await page.goto(newUrl);

		expect(alertTriggered).toBe(false);
	}
);

testWithDeprecationFFDisabled(
	"Hide 'Delete Application Data' checkbox and 'Copy as New' radio button when deprecation FF is false",
	{tag: ['@LPD-44771', '@LPD-44307']},
	async ({apiHelpers, exportImportPage}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await exportImportPage.goToExport();

		const exportFilePath = await exportImportPage.export();

		await exportImportPage.goToImportOptions(exportFilePath);

		await expect(
			exportImportPage.page.getByText('Copy as New:')
		).not.toBeVisible();

		await expect(
			exportImportPage.page.getByLabel('Delete Application Data')
		).not.toBeVisible();
	}
);

/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {DepotSitesPage} from '../../../pages/depot-web/DepotSitesPage';
import getRandomString from '../../../utils/getRandomString';

const test = mergeTests(dataApiHelpersTest, isolatedSiteTest, loginTest());

const DEPOT_ADMIN_NAMESPACE =
	'_com_liferay_depot_web_portlet_DepotAdminPortlet_';

async function expectConnectedSites(
	page: Page,
	depotName: string,
	connectedSites: string
) {

	// The asset library admin lists the asset libraries of every test

	await page.goto(
		`/group/guest/~/control_panel/manage?p_p_id=com_liferay_depot_web_portlet_DepotAdminPortlet&${DEPOT_ADMIN_NAMESPACE}displayStyle=icon&${DEPOT_ADMIN_NAMESPACE}keywords=${depotName}`
	);

	await expect(
		page
			.locator('.card')
			.filter({hasText: depotName})
			.getByText(connectedSites, {exact: true})
	).toBeVisible();
}

test(
	'Sites of every membership type and child sites can be connected to and disconnected from a depot',
	{tag: '@LPD-109574'},
	async ({apiHelpers, page, site}) => {
		const depot = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: getRandomString(),
		});

		const childSite = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
			parentSiteExternalReferenceCode: site.externalReferenceCode,
		});
		const privateSite = await apiHelpers.headlessAdminSite.postSite({
			membershipType: 'private',
			name: getRandomString(),
		});
		const restrictedSite = await apiHelpers.headlessAdminSite.postSite({
			membershipType: 'restricted',
			name: getRandomString(),
		});

		await expectConnectedSites(page, depot.name, '0 Connected Sites');

		const depotSitesPage = new DepotSitesPage(page);

		await depotSitesPage.goto(depot.id);

		await expect(depotSitesPage.noConnectedSitesMessage).toBeVisible();

		// An asset library cannot be connected to another asset library
		// (LPS-104832)

		await depotSitesPage.openSelectSite();

		await expect(
			depotSitesPage.selectSiteFrame.getByRole('link', {
				exact: true,
				name: 'All Sites',
			})
		).toBeVisible();
		await expect(
			depotSitesPage.selectSiteFrame.getByRole('link', {
				name: 'Asset Libraries',
			})
		).toHaveCount(0);

		await page.keyboard.press('Escape');

		// Connect the sites

		for (const connectedSite of [
			site,
			privateSite,
			restrictedSite,
			childSite,
		]) {
			await depotSitesPage.connectSite(connectedSite.name);

			await expect(
				depotSitesPage.getSiteRow(connectedSite.name)
			).toContainText('Yes');
		}

		await expectConnectedSites(page, depot.name, '4 Connected Sites');

		// A deactivated site stays connected

		await apiHelpers.headlessAdminSite.putSite(
			privateSite.externalReferenceCode,
			{active: false, name: privateSite.name}
		);

		await depotSitesPage.goto(depot.id);

		await expect(depotSitesPage.getSiteRow(privateSite.name)).toBeVisible();

		// Disconnect the sites

		expect(await depotSitesPage.disconnectSite(site.name)).toBe(
			'Removing this site connection will not allow the site to consume data from this asset library directly.'
		);

		await expect(depotSitesPage.getSiteRow(site.name)).toHaveCount(0);

		for (const connectedSite of [privateSite, restrictedSite]) {
			await apiHelpers.headlessAssetLibrary.disconnectSite(
				depot.externalReferenceCode,
				connectedSite.externalReferenceCode
			);
		}

		await expectConnectedSites(page, depot.name, '1 Connected Site');

		await depotSitesPage.goto(depot.id);

		await depotSitesPage.disconnectSite(childSite.name);

		await expect(depotSitesPage.noConnectedSitesMessage).toBeVisible();
	}
);

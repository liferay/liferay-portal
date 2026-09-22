/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {instanceSettingsPagesTest} from '../../../fixtures/instanceSettingsPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {
	performLoginViaApi,
	performUserSwitch,
	userData,
} from '../../../utils/performLogin';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {goToSegmentsAdmin} from '../../change-tracking-web/main/utils/segments';
import {SimulationMenuPage} from '../../layout-admin-web/main/pages/SimulationMenuPage';

const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-78863': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	instanceSettingsPagesTest,
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
);

test.beforeEach(async ({instanceSettingsPage}) => {
	await instanceSettingsPage.goToInstanceSetting(
		'Segments',
		'Segments Service'
	);

	await instanceSettingsPage.checkOption('Enable Segmentation', false);

	await instanceSettingsPage.saveAndWaitForAlert();
});

test.afterEach(async ({instanceSettingsPage}) => {
	await instanceSettingsPage.goToInstanceSetting(
		'Segments',
		'Segments Service'
	);

	await instanceSettingsPage.resetInstanceSetting();
});

test(
	'Asserts the segmentation-disabled alert can be dismissed and recovered at the Dynamic Collection editor, and that the re-enable link reaches Segments Service',
	{tag: ['@LPS-154019', '@LPS-152539']},
	async ({apiHelpers, page, site}) => {

		// Create a dynamic asset list

		await apiHelpers.jsonWebServicesAssetListEntry.addDynamicAssetListEntry(
			{
				groupId: site.id,
				title: 'Dynamic Collection Test',
			}
		);

		// Open the Asset Lists Admin and edit the collection

		await page.goto(
			`/group${site.friendlyUrlPath}${PORTLET_URLS.collections}`
		);

		await page.getByRole('link', {name: 'Dynamic Collection Test'}).click();

		const warning = page.locator('.alert-warning');

		const alert = warning.getByText(
			'Personalized variations cannot be displayed because segmentation is disabled.'
		);

		// Close the alert and assert it is hidden

		await expect(alert).toBeVisible();

		await clickAndExpectToBeHidden({
			target: alert,
			trigger: warning.getByRole('button', {name: 'Close'}),
		});

		// Refresh and assert the alert is back

		await page.reload();

		await expect(alert).toBeVisible();

		// Click the re-enable link and assert Segments Service is reached

		await clickAndExpectToBeVisible({
			target: page.getByRole('heading', {name: 'Segments Service'}),
			trigger: page.getByRole('link', {
				name: 'To enable, go to Instance Settings.',
			}),
		});

		await expect(page.getByText('Virtual Instance Scope')).toBeVisible();
	}
);

test(
	'Asserts the segmentation-disabled alert can be dismissed and recovered in the Experience simulation sidepanel, and that it explains how to re-enable segmentation',
	{tag: ['@LPS-154019', '@LPS-151362']},
	async ({apiHelpers, page, site}) => {

		// Create a content page and a segment so the simulation sidebar renders the segments section

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: getRandomString(),
		});

		await apiHelpers.jsonWebServicesSegmentsEntry.addSegmentsEntry({
			criteria: {
				criteria: {
					user: {
						conjunction: 'and',
						filterString: `(firstName eq 'userfn3')`,
						typeValue: 'model',
					},
				},
				filterString: {
					model: `(firstName eq 'userfn3')`,
				},
			},
			groupId: site.id,
			name: 'Segment With User3',
		});

		// Visit the page in view mode and open the simulation panel

		const simulationMenuPage = new SimulationMenuPage(page);

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await simulationMenuPage.openSimulationPanel();

		const warning = page.locator('.alert-warning');

		const alert = warning.getByText(
			'Experiences cannot be displayed because segmentation is disabled.'
		);

		// Assert the alert explains how to re-enable segmentation

		await expect(alert).toBeVisible();

		await expect(
			warning.getByRole('link', {
				name: 'To enable, go to Instance Settings.',
			})
		).toBeVisible();

		// Close the alert and assert it is hidden

		await clickAndExpectToBeHidden({
			target: alert,
			trigger: warning.getByRole('button', {name: 'Close'}),
		});

		// Refresh, reopen the simulation panel, and assert the alert is back

		await page.reload();

		await simulationMenuPage.openSimulationPanel();

		await expect(alert).toBeVisible();
	}
);

test(
	'Asserts the segmentation-disabled alert can be dismissed and recovered at the Segments admin list and editor, and that the re-enable link reaches Segments Service',
	{tag: ['@LPS-154019', '@LPS-152539']},
	async ({page, site}) => {

		// Open the Segments admin list

		await goToSegmentsAdmin(page, site.friendlyUrlPath);

		const warning = page.locator('.alert-warning');

		const alert = warning.getByText('Segmentation is disabled.');

		// Close the alert and assert it is hidden on the list

		await expect(alert).toBeVisible();

		await clickAndExpectToBeHidden({
			target: alert,
			trigger: warning.getByRole('button', {name: 'Close'}),
		});

		// Refresh and assert the alert is back on the list

		await page.reload();

		await expect(alert).toBeVisible();

		// Open the segment editor and close the alert again

		await page.getByRole('link', {name: 'Add New User Segment'}).click();

		await expect(alert).toBeVisible();

		await clickAndExpectToBeHidden({
			target: alert,
			trigger: warning.getByRole('button', {name: 'Close'}),
		});

		// Refresh and assert the alert is back on the editor

		await page.reload();

		await expect(alert).toBeVisible();

		// Click the re-enable link and assert Segments Service is reached

		await clickAndExpectToBeVisible({
			target: page.getByRole('heading', {name: 'Segments Service'}),
			trigger: page.getByRole('link', {
				name: 'To enable, go to Instance Settings.',
			}),
		});

		await expect(page.getByText('Virtual Instance Scope')).toBeVisible();
	}
);

test(
	'Asserts the segmentation-disabled alert can be dismissed and recovered at the Experiences menu of the page editor, and that the re-enable link reaches Segments Service',
	{tag: ['@LPS-154019', '@LPS-151362', '@LPS-152539']},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create a content page and open it in the editor

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Open the Experiences menu

		await pageEditorPage.openExperienceSelector();

		const warning = page.locator('.alert-warning');

		const alert = warning.getByText(
			'Experiences cannot be displayed because segmentation is disabled.'
		);

		// Close the alert and assert it is hidden

		await expect(alert).toBeVisible();

		await clickAndExpectToBeHidden({
			target: alert,
			trigger: warning.getByRole('button', {name: 'Close'}),
		});

		// Refresh, reopen the Experiences menu, and assert the alert is back

		await page.reload();

		await pageEditorPage.openExperienceSelector();

		await expect(alert).toBeVisible();

		// Click the re-enable link and assert Segments Service is reached

		await clickAndExpectToBeVisible({
			target: page.getByRole('heading', {name: 'Segments Service'}),
			trigger: page.getByRole('link', {
				name: 'To enable, go to Instance Settings.',
			}),
		});

		await expect(page.getByText('Virtual Instance Scope')).toBeVisible();
	}
);

test(
	'Asserts the segmentation-disabled alert can be dismissed and recovered at the Manual Collection editor, and that the re-enable link reaches Segments Service',
	{tag: ['@LPS-154019', '@LPS-152539']},
	async ({apiHelpers, page, site}) => {

		// Create a manual asset list

		await apiHelpers.jsonWebServicesAssetListEntry.addManualAssetListEntry({
			groupId: site.id,
			title: 'Manual Collection Test',
		});

		// Open the Asset Lists Admin and edit the collection

		await page.goto(
			`/group${site.friendlyUrlPath}${PORTLET_URLS.collections}`
		);

		await page.getByRole('link', {name: 'Manual Collection Test'}).click();

		const warning = page.locator('.alert-warning');

		const alert = warning.getByText(
			'Personalized variations cannot be displayed because segmentation is disabled.'
		);

		// Close the alert and assert it is hidden

		await expect(alert).toBeVisible();

		await clickAndExpectToBeHidden({
			target: alert,
			trigger: warning.getByRole('button', {name: 'Close'}),
		});

		// Refresh and assert the alert is back

		await page.reload();

		await expect(alert).toBeVisible();

		// Click the re-enable link and assert Segments Service is reached

		await clickAndExpectToBeVisible({
			target: page.getByRole('heading', {name: 'Segments Service'}),
			trigger: page.getByRole('link', {
				name: 'To enable, go to Instance Settings.',
			}),
		});

		await expect(page.getByText('Virtual Instance Scope')).toBeVisible();
	}
);

test(
	'Asserts that a user without Instance Settings permissions sees the contact-administrator alert across the Dynamic Collection editor, Manual Collection editor, and Segments admin',
	{tag: '@LPS-152539'},
	async ({apiHelpers, page, site}) => {

		// Create the data the lower-permission user needs to reach each surface

		await apiHelpers.jsonWebServicesAssetListEntry.addDynamicAssetListEntry(
			{
				groupId: site.id,
				title: 'Dynamic Collection Test',
			}
		);

		await apiHelpers.jsonWebServicesAssetListEntry.addManualAssetListEntry({
			groupId: site.id,
			title: 'Manual Collection Test',
		});

		// Create a role bundling the union of permissions needed to reach the three surfaces, and assign it to a new user

		const companyId = await page.evaluate(() =>
			Liferay.ThemeDisplay.getCompanyId()
		);

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: 'Segmentation Alert Permissions ' + getRandomInt(),
			rolePermissions: [
				{
					actionIds: ['ACCESS_IN_CONTROL_PANEL'],
					primaryKey: String(companyId),
					resourceName:
						'com_liferay_asset_list_web_portlet_AssetListPortlet',
					scope: 1,
				},
				{
					actionIds: ['ACCESS_IN_CONTROL_PANEL'],
					primaryKey: String(companyId),
					resourceName:
						'com_liferay_segments_web_internal_portlet_SegmentsPortlet',
					scope: 1,
				},
				{
					actionIds: ['UPDATE'],
					primaryKey: String(companyId),
					resourceName: 'com.liferay.asset.list.model.AssetListEntry',
					scope: 1,
				},
				{
					actionIds: ['VIEW_SITE_ADMINISTRATION'],
					primaryKey: String(companyId),
					resourceName: 'com.liferay.depot.model.DepotEntry',
					scope: 1,
				},
				{
					actionIds: ['VIEW_SITE_ADMINISTRATION'],
					primaryKey: String(companyId),
					resourceName: 'com.liferay.portal.kernel.model.Group',
					scope: 1,
				},
				{
					actionIds: ['VIEW'],
					primaryKey: String(companyId),
					resourceName: 'com.liferay.segments',
					scope: 1,
				},
			],
		});

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

		// Switch to the lower-permission user

		await performUserSwitch(page, user.alternateName);

		const warning = page.locator('.alert-warning');

		const contactAdministratorMessage = warning.getByText(
			'Contact your system administrator to enable it.'
		);

		const reEnableLink = warning.getByRole('link', {
			name: 'To enable, go to Instance Settings.',
		});

		// Visit the Dynamic Collection editor

		await page.goto(
			`/group${site.friendlyUrlPath}${PORTLET_URLS.collections}`
		);

		await page.getByRole('link', {name: 'Dynamic Collection Test'}).click();

		await expect(contactAdministratorMessage).toBeVisible();

		await expect(reEnableLink).toBeHidden();

		// Visit the Manual Collection editor

		await page.goto(
			`/group${site.friendlyUrlPath}${PORTLET_URLS.collections}`
		);

		await page.getByRole('link', {name: 'Manual Collection Test'}).click();

		await expect(contactAdministratorMessage).toBeVisible();

		await expect(reEnableLink).toBeHidden();

		// Visit the Segments admin

		await goToSegmentsAdmin(page, site.friendlyUrlPath);

		await expect(contactAdministratorMessage).toBeVisible();

		await expect(reEnableLink).toBeHidden();

		// Switch back to the test user so the afterEach hook can reset Instance Settings

		await performLoginViaApi({page, screenName: 'test'});
	}
);

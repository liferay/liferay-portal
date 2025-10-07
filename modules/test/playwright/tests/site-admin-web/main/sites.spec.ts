/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {instanceSettingsPagesTest} from '../../../fixtures/instanceSettingsPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {virtualInstancesPagesTest} from '../../../fixtures/virtualInstancesPagesTest';
import getRandomString from '../../../utils/getRandomString';
import {pageViewModePagesTest} from './../../../fixtures/pageViewModePagesTest';
import {siteSettingsPagesTest} from './../../../fixtures/siteSettingsPagesTest';
import {systemSettingsPageTest} from './../../../fixtures/systemSettingsPageTest';
import {waitForAlert} from './../../../utils/waitForAlert';
import {sitesAdminPagesTest} from './fixtures/sitesAdminPagesTest';

const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	instanceSettingsPagesTest,
	loginTest(),
	pageEditorPagesTest,
	pageViewModePagesTest,
	sitesAdminPagesTest,
	siteSettingsPagesTest,
	systemSettingsPageTest,
	virtualInstancesPagesTest
);

test('User can add and delete site and child site', async ({
	page,
	sitesAdminPage,
}) => {
	const childSiteName = getRandomString();
	const parentSiteName = getRandomString();

	await sitesAdminPage.goto();

	await page.getByRole('link', {name: 'Add Site'}).click();
	await sitesAdminPage.addBlankSite(parentSiteName);

	await sitesAdminPage.goto();

	await expect(page.getByText(parentSiteName)).toBeVisible();

	await sitesAdminPage.addChildSite(childSiteName, parentSiteName);

	await sitesAdminPage.goto();

	await expect(
		page
			.getByRole('row', {name: parentSiteName})
			.getByRole('cell', {exact: true, name: '1 Child Sites'})
	).toBeVisible();

	await sitesAdminPage.viewChildSites(parentSiteName);

	await expect(page.getByText(childSiteName)).toBeVisible();

	await sitesAdminPage.deleteSite(childSiteName);

	await expect(page.getByText('No sites were found.')).toBeVisible();

	await page.getByTitle('Go to Sites').click();

	await sitesAdminPage.deleteSite(parentSiteName);

	await expect(page.getByText(parentSiteName)).not.toBeVisible();
});

test('Site is still created even if modal window is closed', async ({
	page,
	sitesAdminPage,
}) => {
	const siteName = getRandomString();

	await sitesAdminPage.goto();

	await page.getByRole('link', {name: 'Add Site'}).click();
	await sitesAdminPage.addBlankSite(siteName, true);

	await sitesAdminPage.goto();

	await expect(page.getByText(siteName)).toBeVisible();

	await sitesAdminPage.deleteSite(siteName);

	await expect(page.getByText(siteName)).not.toBeVisible();
});

test('Inactivate and reactivate site', async ({
	apiHelpers,
	page,
	sitesAdminPage,
	systemSettingsPage,
	widgetPagePage,
}) => {
	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});

	try {

		// Activate 'Show Inactive Request Message' configuration in system settings

		await systemSettingsPage.goToSystemSetting(
			'Infrastructure',
			'Inactive Request Handler'
		);

		const showInactiveRequestCheckbox = page.getByLabel(
			'Show Inactive Request Message'
		);

		if ((await showInactiveRequestCheckbox.isChecked()) === false) {
			await showInactiveRequestCheckbox.check();
			await page.getByRole('button', {name: 'Save'}).click();
		}

		// Create Layout

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		// Deactivate Site

		await sitesAdminPage.goto();

		await page
			.getByRole('row', {name: site.name})
			.getByLabel('Show Actions')
			.click();

		page.once('dialog', async (dialog) => {
			await dialog.accept();
		});

		await page.getByRole('menuitem', {name: 'Deactivate'}).click();

		await waitForAlert(page);

		// Verify that the message alerting that the Site is deactivated appears

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await expect(
			page.getByText(
				'This site is inactive. Please contact the administrator.',
				{exact: true}
			)
		).toBeVisible();

		// Activate Site

		await sitesAdminPage.goto();

		await page
			.getByRole('row', {name: site.name})
			.getByLabel('Show Actions')
			.click();

		await page.getByRole('menuitem', {name: 'Activate'}).click();

		await waitForAlert(page);

		// Verify that the message alerting that the Site is deactivated does not appears

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await expect(
			page.getByText(
				'This site is inactive. Please contact the administrator.',
				{exact: true}
			)
		).not.toBeVisible();
	}
	finally {
		await apiHelpers.headlessSite.deleteSite(site.id);
	}
});

test('Could edit site name', async ({
	apiHelpers,
	page,
	siteSettingsPage,
	sitesAdminPage,
}) => {
	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});

	try {
		await siteSettingsPage.goToSiteSetting(
			'Site Configuration',
			'Details',
			site.friendlyUrlPath
		);

		await page.waitForTimeout(300);

		const newSiteName = getRandomString();

		await page.getByPlaceholder('Name').fill(newSiteName);

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page);

		await sitesAdminPage.goto();

		await expect(page.getByRole('link', {name: newSiteName})).toBeVisible();
	}
	finally {
		await apiHelpers.headlessSite.deleteSite(site.id);
	}
});

test('Could edit friendly URL and access by it', async ({
	apiHelpers,
	page,
	siteSettingsPage,
	widgetPagePage,
}) => {
	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});

	try {

		// Create public Layout

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		// Update site's friendly URL

		await siteSettingsPage.goToSiteSetting(
			'Site Configuration',
			'Site URL',
			site.friendlyUrlPath
		);

		await page.waitForTimeout(300);

		const newSiteFriendlyURL = '/' + getRandomString();

		await page.getByLabel('Friendly URL').fill(newSiteFriendlyURL);

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page);

		// Assert that the redirection to the site's public page works using the new site's friendly URL

		await widgetPagePage.goto(layout, newSiteFriendlyURL);

		await expect(
			page.getByRole('link', {name: 'Go to ' + site.name})
		).toBeVisible();

		await expect(
			page.getByRole('menuitem', {name: layout.nameCurrentValue})
		).toBeVisible();
	}
	finally {
		await apiHelpers.headlessSite.deleteSite(site.id);
	}
});

test('Could not add invalid friendly URL and can access by former friendly URL', async ({
	apiHelpers,
	page,
	siteSettingsPage,
}) => {
	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});

	try {

		// Create Layout

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		// Try to add a invalid friendly URL to the site

		await siteSettingsPage.goToSiteSetting(
			'Site Configuration',
			'Site URL',
			site.friendlyUrlPath
		);

		await page.waitForTimeout(300);

		const oldSiteFriendlyURL = site.friendlyUrlPath;

		const newSiteFriendlyURL =
			'/' + getRandomString() + '/' + getRandomString();

		await page.getByLabel('Friendly URL').fill(newSiteFriendlyURL);

		await page.getByRole('button', {name: 'Save'}).click();

		// Expect that it fails

		await expect(
			page.getByText('Close Error: The friendly URL')
		).toBeVisible();

		await waitForAlert(page, 'Error:Your request failed to complete.', {
			type: 'danger',
		});

		// Expect that the friendly URL field has the old value of the site's friendly URL

		await siteSettingsPage.goToSiteSetting(
			'Site Configuration',
			'Site URL',
			site.friendlyUrlPath
		);

		await expect(page.getByLabel('Friendly URL')).toHaveValue(
			oldSiteFriendlyURL
		);
	}
	finally {
		await apiHelpers.headlessSite.deleteSite(site.id);
	}
});

test('Able to search and find site by site name', async ({
	apiHelpers,
	page,
	sitesAdminPage,
}) => {
	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});
	try {
		await sitesAdminPage.goto();

		await page.getByPlaceholder('Search for').fill(site.name);

		await page.getByLabel('Search for', {exact: true}).click();

		await page.getByText('1 Result Found for "' + site.name + '"').click();

		await expect(
			page.getByRole('cell', {exact: true, name: site.name})
		).toBeVisible();
	}
	finally {
		await apiHelpers.headlessSite.deleteSite(site.id);
	}
});

test('View public page via virtual host URL', async ({
	apiHelpers,
	page,
	siteSettingsPage,
}) => {
	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});

	try {

		// Create Layout

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		// Configure a Virtual Host for the site

		await siteSettingsPage.goToSiteSetting(
			'Site Configuration',
			'Site URL',
			site.friendlyUrlPath
		);
		const VIRTUAL_HOST_NAME = 'www.able.com';

		await page.getByPlaceholder('Virtual Host').fill(VIRTUAL_HOST_NAME);

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page);

		// Access the site's page using the Virtual Host

		await page.goto(
			`http://${VIRTUAL_HOST_NAME}:8080/web${site.friendlyUrlPath}${layout.friendlyURL}`
		);

		await expect(
			page.getByRole('menuitem', {
				name: layout.nameCurrentValue,
			})
		).toBeVisible();
	}
	finally {
		await apiHelpers.headlessSite.deleteSite(site.id);
	}
});

test('Can not choose its own site as parent site', async ({
	apiHelpers,
	page,
	siteSettingsPage,
	sitesAdminPage,
}) => {
	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});

	try {
		await sitesAdminPage.goto();

		await siteSettingsPage.goToSiteSetting(
			'Site Configuration',
			'Details',
			site.friendlyUrlPath
		);

		await page.getByRole('button', {name: 'Change'}).click();

		const selectSiteModal = page.frameLocator(
			'iframe[title="Select Site"]'
		);

		await expect(
			selectSiteModal.getByRole('link', {exact: true, name: site.name})
		).not.toBeVisible();
	}
	finally {
		await apiHelpers.headlessSite.deleteSite(site.id);
	}
});

test('Deleting friendly URL makes it not able to access page via old friendly URL', async ({
	apiHelpers,
	page,
	siteSettingsPage,
	widgetPagePage,
}) => {
	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});

	const originalSiteFriendlyURL = site.friendlyUrlPath;

	try {

		// Create public Layout

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		// Create private Layout

		const privateLayout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			privateLayout: 'true',
			title: getRandomString(),
		});

		await siteSettingsPage.goToSiteSetting(
			'Site Configuration',
			'Site URL',
			site.friendlyUrlPath
		);

		// Fill the site's friendly URL with a empty value

		await page.waitForTimeout(300);

		await page.getByLabel('Friendly URL').fill('');

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page);

		// Assert that the empty value was not saved and the current value is now '/group-<groupId>'

		await expect(page.getByLabel('Friendly URL')).toHaveValue(
			'/group-' + site.id
		);

		// Assert that the public page is not accessible using the old site's friendly URL

		await widgetPagePage.goto(layout, originalSiteFriendlyURL);

		await expect(
			page.getByRole('menuitem', {name: layout.nameCurrentValue})
		).not.toBeVisible();

		// Assert that the private page is not accessible using the old site's friendly URL

		await widgetPagePage.goto(privateLayout, originalSiteFriendlyURL);

		await expect(
			page.getByRole('menuitem', {name: privateLayout.nameCurrentValue})
		).not.toBeVisible();
	}
	finally {
		await apiHelpers.headlessSite.deleteSite(site.id);
	}
});

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {appManagerPagesTest} from '../../../fixtures/appManagerPagesTest';
import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';
import {loginTest} from '../../../fixtures/loginTest';
import {virtualInstancesPagesTest} from '../../../fixtures/virtualInstancesPagesTest';
import performLogin from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {zipFolder} from '../../../utils/zip';

export const test = mergeTests(
	apiHelpersTest,
	applicationsMenuPageTest,
	appManagerPagesTest,
	loginTest(),
	virtualInstancesPagesTest
);

test(
	'Install / Uninstall app via upload',
	{tag: '@LPD-61776'},
	async ({appManagerPage, bundleBlacklistPage, page}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const appName = 'test-app-portlet';

		const fileChooserPromise = page.waitForEvent('filechooser');

		await appManagerPage.goto();

		await expect(async () => {
			await appManagerPage.optionsMenu.click();
			await appManagerPage.uploadMenuItem.click({timeout: 1000});
		}).toPass();

		await appManagerPage.uploadFrameFileInput.click();

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles(
			await zipFolder(
				path.join(__dirname, `/dependencies/${appName}.war`)
			)
		);

		await appManagerPage.uploadFrameInstallButton.click();

		await waitForAlert(
			appManagerPage.uploadFrame,
			'The plugin was uploaded successfully and is now being installed.'
		);

		await appManagerPage.uploadFrameCloseButton.click();

		await expect(async () => {
			await expect(appManagerPage.searchInput).toBeEnabled();

			await appManagerPage.searchInput.fill(appName);
			await appManagerPage.searchInput.press('Enter');

			await expect(appManagerPage.appLink(appName)).toBeVisible({
				timeout: 2000,
			});
		}).toPass();

		await expect(appManagerPage.appRow(appName)).toContainText('Active');

		try {
			await expect(async () => {
				await appManagerPage.appRowOptionsMenu(appName).click();
				await appManagerPage.uninstallLink.click({timeout: 1000});

				await waitForAlert(page);

				await expect(appManagerPage.noResultsMessage).toBeVisible();
			}).toPass();
		}
		finally {
			await bundleBlacklistPage.goto();

			await bundleBlacklistPage.blacklistBundleSymbolicInput.fill('');
			await bundleBlacklistPage.updateButton.click();

			await waitForAlert(page);
		}
	}
);

test(
	'Installing an app with an invalid file should display an error message',
	{tag: '@LPD-61776'},
	async ({appManagerPage, page}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const fileChooserPromise = page.waitForEvent('filechooser');

		await appManagerPage.goto();

		await expect(async () => {
			await appManagerPage.optionsMenu.click();
			await appManagerPage.uploadMenuItem.click({timeout: 1000});
		}).toPass();

		await appManagerPage.uploadFrameFileInput.click();

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles(
			path.join(__dirname, '/dependencies/attachment.txt')
		);

		await appManagerPage.uploadFrameInstallButton.click();

		await waitForAlert(
			appManagerPage.uploadFrame,
			'Your request failed to complete.',
			{type: 'danger'}
		);

		await expect(appManagerPage.uploadFrameErrorMessage).toBeVisible();
	}
);

[
	{
		name: 'Liferay Adaptive Media API',
		search: 'com.liferay.adaptive.media.api',
	},
	{name: 'Liferay Blogs API', search: 'com.liferay.blogs.api'},
	{name: 'Liferay Comment API', search: 'com.liferay.comment.api'},
	{name: 'Liferay Depot Web', search: 'com.liferay.depot.web'},
	{
		name: 'Liferay Document Library Opener Service',
		search: 'com.liferay.document.library.opener.service',
	},
	{
		name: 'Liferay Knowledge Base API',
		search: 'com.liferay.knowledge.base.api',
	},
	{
		name: 'Liferay Message Boards API',
		search: 'com.liferay.message.boards.api',
	},
	{
		name: 'Liferay Notifications Service',
		search: 'com.liferay.notifications.service',
	},
	{name: 'Liferay Questions Web', search: 'com.liferay.questions.web'},
	{name: 'Liferay Ratings API', search: 'com.liferay.ratings.api'},
	{name: 'Liferay Sharing API', search: 'com.liferay.sharing.api'},
	{name: 'Liferay Translation API', search: 'com.liferay.translation.api'},
	{name: 'Liferay Wiki API', search: 'com.liferay.wiki.api'},
].forEach((app) => {
	test(
		`Can deactivate and activate ${app.name}`,
		{tag: '@LPD-61776'},
		async ({appManagerPage, page}) => {
			page.on('dialog', (dialog) => dialog.accept());

			await appManagerPage.goto();

			await expect(async () => {
				await expect(appManagerPage.searchInput).toBeEnabled();

				await appManagerPage.searchInput.fill(app.search);
				await appManagerPage.searchInput.press('Enter');

				await expect(appManagerPage.appLink(app.name)).toBeVisible({
					timeout: 2000,
				});
				await expect(appManagerPage.appRow(app.name)).toContainText(
					'Active'
				);
			}).toPass();

			await expect(async () => {
				await appManagerPage.appRowOptionsMenu(app.name).click();
				await appManagerPage.deactivateLink.click({timeout: 1000});

				await waitForAlert(page);
			}).toPass();

			await expect(async () => {
				await expect(appManagerPage.searchInput).toBeEnabled();

				await appManagerPage.searchInput.fill(app.search);
				await appManagerPage.searchInput.press('Enter');

				await expect(appManagerPage.appLink(app.name)).toBeVisible({
					timeout: 2000,
				});
				await expect(appManagerPage.appRow(app.name)).toContainText(
					'Resolved'
				);
			}).toPass();

			await expect(async () => {
				await appManagerPage.appRowOptionsMenu(app.name).click();
				await appManagerPage.activateLink.click({timeout: 1000});

				await waitForAlert(page);
			}).toPass();

			await expect(async () => {
				await expect(appManagerPage.searchInput).toBeEnabled();

				await appManagerPage.searchInput.fill(app.search);
				await appManagerPage.searchInput.press('Enter');

				await expect(appManagerPage.appLink(app.name)).toBeVisible({
					timeout: 2000,
				});
				await expect(appManagerPage.appRow(app.name)).toContainText(
					'Active'
				);
			}).toPass();
		}
	);
});

test(
	'Can filter app by status',
	{tag: ['@LPD-61776']},
	async ({appManagerPage}) => {
		await appManagerPage.goto();

		const appName = 'Independent Modules';

		await expect(appManagerPage.appLink(appName)).toBeVisible();
		await expect(appManagerPage.appRow(appName)).toContainText('Active');
		await expect(appManagerPage.noAppsMessage).not.toBeVisible();

		await expect(async () => {
			await appManagerPage.filterButton.click();
			await appManagerPage.resolvedFilterMenuItem.click();

			await expect(appManagerPage.appLink(appName)).toHaveCount(0);
			await expect(appManagerPage.noAppsMessage).toBeVisible();
		}).toPass();

		await expect(async () => {
			await appManagerPage.filterButton.click();
			await appManagerPage.activeFilterMenuItem.click();

			await expect(appManagerPage.appLink(appName)).toBeVisible();
			await expect(appManagerPage.noAppsMessage).not.toBeVisible();
		}).toPass();

		await expect(async () => {
			await appManagerPage.filterButton.click();
			await appManagerPage.installedFilterMenuItem.click();

			await expect(appManagerPage.appLink(appName)).toHaveCount(0);
			await expect(appManagerPage.noAppsMessage).toBeVisible();
		}).toPass();

		await expect(async () => {
			await appManagerPage.filterButton.click();
			await appManagerPage.activeFilterMenuItem.click();

			await expect(appManagerPage.appLink(appName)).toBeVisible();
			await expect(appManagerPage.noAppsMessage).not.toBeVisible();
		}).toPass();
	}
);

test(
	'View License Manager',
	{tag: ['@LPD-61776', '@LPS-99031']},
	async ({licenseManagerPage}) => {
		await licenseManagerPage.goto();

		await expect(licenseManagerPage.frameHostNameCell).toBeVisible();
		await expect(licenseManagerPage.frameIPAddressCell).toBeVisible();
		await expect(
			licenseManagerPage.frameLicensesRegisteredCell
		).toBeVisible();
		await expect(licenseManagerPage.frameOwnerCell).toBeVisible();
		await expect(licenseManagerPage.frameServerInfoCell).toBeVisible();
		await expect(licenseManagerPage.frameStartDateCell).toBeVisible();
		await expect(licenseManagerPage.headerTitle).toHaveCount(1);
	}
);

test(
	'App Manager do not display in non default instances',
	{tag: ['@LPD-61776']},
	async ({applicationsMenuPage, browser, page, virtualInstancesPage}) => {
		test.setTimeout(300000);

		const DEFAULT_VIRTUAL_INSTANCE_NAME = 'www.able.com';

		const links = ['App Manager', 'License Manager', 'Purchased', 'Store'];

		await applicationsMenuPage.goToControlPanel();

		for (const link of links) {
			await expect(
				page.getByRole('menuitem', {
					exact: true,
					name: link,
				})
			).toBeVisible();
		}

		let newPage: Page;

		try {
			await virtualInstancesPage.addNewVirtualInstance(
				DEFAULT_VIRTUAL_INSTANCE_NAME
			);

			newPage = await browser.newPage({
				baseURL: `http://${DEFAULT_VIRTUAL_INSTANCE_NAME}:8080`,
			});

			await performLogin(
				newPage,
				'test',
				'',
				`@${DEFAULT_VIRTUAL_INSTANCE_NAME}.com`
			);

			await applicationsMenuPage.goToControlPanel();

			for (const link of links) {
				await expect(
					page.getByRole('menuitem', {
						exact: true,
						name: link,
					})
				).toHaveCount(0);
			}
		}
		finally {
			if (newPage) {
				await newPage.close();
			}

			await virtualInstancesPage.deleteVirtualInstance(
				DEFAULT_VIRTUAL_INSTANCE_NAME
			);
		}
	}
);

/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch, userData} from '../../../utils/performLogin';
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
	pageEditorPagesTest
);

test(
	'Can publish a site from the revamped Staging application',
	{tag: '@LPD-116356'},
	async ({apiHelpers, exportImportPage, page, site}) => {
		await enableLocalStaging(apiHelpers, page, site);

		await page.goto(
			`/group${site.friendlyUrlPath}-staging/~/control_panel/manage?p_p_id=com_liferay_staging_processes_web_portlet_StagingProcessesPortlet`
		);

		await exportImportPage.clickNew();

		await expect(
			page.getByRole('heading', {name: 'New Publish Process'})
		).toBeVisible();

		const name = `Publication-${getRandomString()}`;

		await exportImportPage.nameInput.fill(name);

		await page.getByRole('button', {name: 'Publish to Live'}).click();

		await expect(exportImportPage.taskStatusLabel(name)).toBeVisible();
	}
);

test(
	'Can publish to live through the new publish page',
	{tag: '@LPD-116053'},
	async ({apiHelpers, exportImportPage, page, pageEditorPage, site}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {type: 'content'},
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);
		await pageEditorPage.publishPage();

		await enableLocalStaging(apiHelpers, page, site);

		await page.goto(`/web${site.friendlyUrlPath}-staging`);

		const stagingBarPublishLink = page
			.locator('.staging-bar')
			.getByRole('link', {name: 'Publish to Live'});

		await test.step('Open the new publish page without a modal', async () => {
			await stagingBarPublishLink.click();

			await expect(page).toHaveURL(/view_new_publish/);
			await expect(page.getByRole('dialog')).toBeHidden();
			await expect(exportImportPage.nameInput).toBeVisible();
		});

		await test.step('Cancel returns to the staging page', async () => {
			await page.getByRole('link', {name: 'Cancel'}).click();

			await expect(page).toHaveURL(
				new RegExp(`/web${site.friendlyUrlPath}-staging`)
			);
		});

		const publishButton = page.getByRole('button', {
			name: 'Publish to Live',
		});

		await test.step('Publish from the staging bar lands on the processes list', async () => {
			await stagingBarPublishLink.click();

			const name = getRandomString();

			await exportImportPage.nameInput.fill(name);

			await publishButton.click();

			await expect(page).toHaveURL(/tabs1=processes/);
			await expect(exportImportPage.taskStatusLabel(name)).toBeVisible();
		});

		await test.step('Publish from the processes list returns to it', async () => {
			await exportImportPage.clickNew();

			await expect(page).toHaveURL(/view_new_publish/);

			const name = getRandomString();

			await exportImportPage.nameInput.fill(name);

			await publishButton.click();

			await expect(page).toHaveURL(/tabs1=processes/);
			await expect(exportImportPage.taskStatusLabel(name)).toBeVisible();
		});
	}
);

test(
	'Can view the publications without the publish permission',
	{tag: '@LPD-116356'},
	async ({apiHelpers, exportImportPage, page, site}) => {
		await enableLocalStaging(apiHelpers, page, site);

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			rolePermissions: [
				{
					actionIds: ['VIEW_SITE_ADMINISTRATION', 'VIEW_STAGING'],
					primaryKey: '0',
					resourceName: 'com.liferay.portal.kernel.model.Group',
					scope: 3,
				},
			],
			roleType: 'site',
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUserToSite(
			role.id,
			site.id,
			user.id
		);

		await performUserSwitch(page, user.alternateName);

		await page.goto(
			`/group${site.friendlyUrlPath}-staging/~/control_panel/manage?p_p_id=com_liferay_staging_processes_web_portlet_StagingProcessesPortlet`
		);

		await expect(
			page.getByRole('link', {exact: true, name: 'Scheduled'})
		).toBeVisible();
		await expect(
			page.locator('.cell-status').getByText('Successful', {exact: true})
		).toBeVisible();

		await expect(exportImportPage.newButton).toBeHidden();

		await performUserSwitch(page, 'test');
	}
);

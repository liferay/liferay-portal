/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectDefinition} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch, userData} from '../../../utils/performLogin';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';

export const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	productMenuPageTest
);

test(
	'Error message for a user without permissions only displays once',
	{tag: ['@LPD-60969']},
	async ({apiHelpers, page, pageEditorPage, productMenuPage, site}) => {
		let layout: Layout;
		let objectDefinition: ObjectDefinition;
		let userAccount: TUserAccount;
		let webContentPageURL: string;

		await test.step('Setup data', async () => {
			await test.step('Go to web content page and get URL', async () => {
				await productMenuPage.openProductMenuIfClosed();

				await productMenuPage.goToWebContent();

				await expect(
					page.getByRole('heading', {
						name: 'Web Content',
					})
				).toBeVisible();

				webContentPageURL = page.url();
			});

			await test.step('Create a new user', async () => {
				userAccount =
					await apiHelpers.headlessAdminUser.postUserAccount();

				userData[userAccount.alternateName] = {
					name: userAccount.givenName,
					password: 'test',
					surname: userAccount.familyName,
				};
			});

			await test.step('Create an object', async () => {
				objectDefinition =
					await apiHelpers.objectAdmin.postRandomObjectDefinition({
						objectFolderExternalReferenceCode: 'default',
						status: {code: 0},
					});

				apiHelpers.data.push({
					id: objectDefinition.id,
					type: 'objectDefinition',
				});
			});

			await test.step('Create a new page', async () => {
				layout = await apiHelpers.headlessDelivery.createSitePage({
					pageDefinition: getPageDefinition([]),
					siteId: site.id,
					title: getRandomString(),
				});
			});
		});

		await test.step('Add object widget to a page', async () => {
			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			await pageEditorPage.addWidget('Object', objectDefinition.name);

			await pageEditorPage.publishPage();
		});

		await test.step('Login as newly created user', async () => {
			await performUserSwitch(page, userAccount.alternateName);
		});

		await test.step('Check that only 1 error message is displayed for the object widget', async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			const errorAlertLocator = page.locator('.alert-danger');

			await expect(errorAlertLocator).toBeVisible();

			await expect(errorAlertLocator).toHaveCount(1);
			await expect(errorAlertLocator).toContainText(
				'You do not have the roles required to access this portlet.'
			);
		});

		await test.step('Check that only 1 error message is displayed for the web content page', async () => {
			await page.goto(webContentPageURL);

			const errorAlertLocator = page.locator('.alert-danger');

			await expect(errorAlertLocator).toBeVisible();

			await expect(errorAlertLocator).toHaveCount(1);
			await expect(errorAlertLocator).toContainText(
				'You do not have the roles required to access this portlet.'
			);
		});
	}
);

/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {collectionsPagesTest} from '../../../../fixtures/collectionsPagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {waitForAlert} from '../../../../utils/waitForAlert';

const test = mergeTests(
	collectionsPagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest()
);

test(
	'Dynamic Collection scoped to a Space saves and displays the Space, not the CMS group',
	{tag: '@LPD-89353'},
	async ({apiHelpers, collectionsPage, page, site}) => {
		const collectionName = getRandomString();
		const spaceName = `Space ${getRandomString()}`;

		const expectScopeRowShowsSpace = async () => {
			const scopeCollapse = page.getByRole('button', {name: 'Scope'});

			await expect(scopeCollapse).toBeVisible();

			if (
				(await scopeCollapse.getAttribute('aria-expanded')) !== 'true'
			) {
				await scopeCollapse.click();
			}

			await expect(
				page.getByRole('row').filter({hasText: spaceName})
			).toContainText('Asset Library or Space');
		};

		await test.step('Create a Space', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				settings: {},
				type: 'Space',
			});
		});

		await test.step('Connect the Space to the site', async () => {
			await page.goto(PORTLET_URLS.cmsAllSpaces);

			await expect(
				page.getByRole('row', {name: spaceName})
			).toBeVisible();

			await page
				.getByRole('row', {name: spaceName})
				.getByRole('button', {name: 'Actions'})
				.click();
			await page
				.getByRole('menuitem', {name: 'View Connected Sites'})
				.click();
			await page.getByPlaceholder('Select a Site').fill(site.name);
			await page.getByRole('option', {name: site.name}).click();

			await expect(
				page.getByRole('button', {name: 'Connect'})
			).toBeEnabled();

			await page.getByRole('button', {name: 'Connect'}).click();

			await waitForAlert(
				page,
				`Success:Site ${site.name} was successfully connected to the space.`
			);
		});

		await test.step('Create a Dynamic Collection on the site', async () => {
			await collectionsPage.goto(site.friendlyUrlPath);
			await collectionsPage.addNewDynamicCollection(collectionName);

			await page
				.getByLabel('Item Type')
				.selectOption({label: 'Basic Web Content (CMS)'});
		});

		await test.step('Add the Space to the collection scope', async () => {
			await page.getByRole('button', {name: 'Scope'}).click();

			await page.getByRole('button', {name: 'Select Site'}).click();

			await page
				.getByRole('menuitem', {name: 'Other Site, Asset Library, or'})
				.click();

			const scopeFrame = page
				.locator('iframe[title="Scope"]')
				.contentFrame();

			await scopeFrame.getByRole('link', {name: 'Spaces'}).click();

			await scopeFrame
				.getByRole('link', {exact: true, name: spaceName})
				.click();
		});

		await test.step('Scope row shows the Space name and the "Asset Library or Space" type', async () => {
			await expectScopeRowShowsSpace();
		});

		await test.step('Save the collection and reopen it for editing', async () => {
			await page.getByRole('button', {name: 'Save'}).click();

			await waitForAlert(page);

			await collectionsPage.goto(site.friendlyUrlPath);

			await page.getByRole('link', {name: collectionName}).click();

			await page.getByRole('button', {name: 'Scope'}).click();
		});

		await test.step('After reload, the scope row still shows the Space, not the CMS group', async () => {
			await expectScopeRowShowsSpace();
		});
	}
);

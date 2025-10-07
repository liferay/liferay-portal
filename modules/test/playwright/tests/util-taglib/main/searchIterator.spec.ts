/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {samplePageTest} from '../../frontend-taglib/main/fixtures/samplePageTest';
import {TabName} from '../../frontend-taglib/main/pages/SamplePage';

export const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	pageViewModePagesTest,
	samplePageTest
);
const testLegacy = mergeTests(dataApiHelpersTest, loginTest());

test(
	'Search Iterator overlaps fixed header on scrolling',
	{tag: '@LPD-40036'},
	async ({apiHelpers, page, site, widgetPagePage}) => {
		await test.step('Create a content site, add frontend taglib sample widget and open permissions configuration', async () => {
			const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				title: getRandomString(),
			});

			await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

			await widgetPagePage.addPortlet('Taglib Sample');

			await widgetPagePage.clickOnAction('Taglib Sample', 'Permissions');
		});

		await test.step('Check header sizes', async () => {
			const permissionsIframe = page.frameLocator(
				'iframe[title*="Permissions"]'
			);

			await permissionsIframe.locator('#main-content').hover();

			await page.mouse.wheel(0, 150);

			const mainHeaderWidth = await permissionsIframe
				.locator('.table-responsive')
				.evaluate((element) => element.getBoundingClientRect().width);

			const fixedHeaderWidth = await permissionsIframe
				.locator('.lfr-search-iterator-fixed-header-inner-wrapper')
				.evaluate((element) => element.getBoundingClientRect().width);

			expect(mainHeaderWidth).toBe(fixedHeaderWidth);
		});
	}
);

test(
	'Checkboxes can be selected when using RowChecker',
	{tag: '@LPD-63803'},
	async ({page, samplePage}) => {
		await test.step('Select Search Iterator tab', async () => {
			await samplePage.selectTab(TabName.SEARCH_ITERATOR);
		});

		await test.step('Select checkbox and assert it remains marked', async () => {
			const firstCheckbox = page
				.locator(
					'table[data-searchcontainerid*="stringItemSearchContainer"] input[type="checkbox"]'
				)
				.first();

			await firstCheckbox.check();

			await expect(firstCheckbox).toBeChecked();
		});
	}
);

testLegacy(
	'Check fixed permission header is visible',
	{tag: ['@LPD-39339']},
	async ({apiHelpers, page}) => {
		const contentStructureId =
			await getBasicWebContentStructureId(apiHelpers);
		const randomTitle = getRandomString();
		const siteId = await page.evaluate(() => {
			return String(Liferay.ThemeDisplay.getSiteGroupId());
		});

		const webContent =
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: contentStructureId,
				groupId: siteId,
				titleMap: {en_US: randomTitle},
			});

		apiHelpers.data.push({
			id: `${siteId}_${webContent.articleId}`,
			type: 'webContent',
		});

		await page.goto('/');

		const openProductButton = page.getByLabel('Open Product Menu');

		if (await openProductButton.isVisible()) {
			await openProductButton.click();
		}

		const contentAndDataTab = page.getByRole('menuitem', {
			name: 'Content & Data',
		});

		await contentAndDataTab.waitFor({state: 'visible'});

		await contentAndDataTab.click();

		const webContentButton = page.getByRole('menuitem', {
			name: 'Web Content',
		});

		await webContentButton.waitFor({state: 'visible'});

		await webContentButton.click();

		const webContentPage = page.getByRole('heading', {name: 'Web Content'});

		await webContentPage.waitFor({state: 'visible'});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				name: 'Permissions',
			}),
			trigger: page.locator(
				`button[aria-label="Actions for ${randomTitle}"]`
			),
		});

		const permissionHeading = page.getByRole('heading', {
			name: 'Permissions',
		});

		await permissionHeading.waitFor({state: 'visible'});

		const fixedHeaderRow = page
			.frameLocator('iframe[title="Permissions"]')
			.locator(
				'[id="_com_liferay_portlet_configuration_web_portlet_PortletConfigurationPortlet_rolesSearchContainerfixedHeader"]'
			);

		await expect(fixedHeaderRow).toHaveCSS('display', 'none');

		await page
			.frameLocator('iframe[title="Permissions"]')
			.getByRole('cell', {exact: true, name: 'Role'})
			.click();

		await page.keyboard.down('PageDown');

		await expect(fixedHeaderRow).not.toHaveCSS('display', 'none');
	}
);

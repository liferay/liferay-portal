/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {PageEditorPage} from '../../../pages/layout-content-page-editor-web/PageEditorPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {hoverAndExpectToBeVisible} from '../../../utils/hoverAndExpectToBeVisible';
import {performLogout} from '../../../utils/performLogin';
import getFragmentDefinition from '../main/utils/getFragmentDefinition';
import getPageDefinition from '../main/utils/getPageDefinition';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-38869': {enabled: true},
		'LPD-39304': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
);

async function changeSource(
	menuDisplayId: string,
	name: string,
	page: Page,
	pageEditorPage: PageEditorPage
) {
	await pageEditorPage.selectFragment(menuDisplayId);

	const iframe = page.frameLocator('iframe[title="Select"]');

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: iframe.getByText(name),
		timeout: 3000,
		trigger: page.getByLabel('Change Source'),
	});

	await iframe.getByRole('button', {name: 'Select This Level'}).click();

	await pageEditorPage.waitForChangesSaved();
}

test(
	'Can configure sublevels, display style, selected item color and hovered item color',
	{
		tag: ['@LPS-120091', '@LPS-140988'],
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Add layouts

		const parentLayoutTitle = getRandomString();

		const parentLayout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: parentLayoutTitle,
		});

		const childLayoutTitle = getRandomString();

		const childLayout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			parentLayoutId: parentLayout.layoutId,
			title: childLayoutTitle,
		});

		const grandChildLayoutLayoutTitle = getRandomString();

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			parentLayoutId: childLayout.layoutId,
			title: grandChildLayoutLayoutTitle,
		});

		// Add layout with menu display fragment and go to edit mode

		const menuDisplayId = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					fragmentConfig: {
						sublevels: -1,
					},
					id: menuDisplayId,
					key: 'com.liferay.fragment.renderer.menu.display.internal.MenuDisplayFragmentRenderer',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Assert layouts in menu display fragment

		const menuDisplay = page.locator(
			'.lfr-layout-structure-item-com-liferay-fragment-renderer-menu-display-internal-menudisplayfragmentrenderer'
		);

		await expect(menuDisplay.getByText(parentLayoutTitle)).toBeVisible();

		await hoverAndExpectToBeVisible({
			autoClick: false,
			target: menuDisplay.getByText(childLayoutTitle),
			trigger: menuDisplay.locator('.nav-link', {
				hasText: parentLayoutTitle,
			}),
		});

		await expect(
			menuDisplay.getByText(grandChildLayoutLayoutTitle)
		).toBeVisible();

		// Configure levels

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Sublevels',
			fragmentId: menuDisplayId,
			tab: 'General',
			value: '1',
		});

		// Assert layouts in menu display fragment

		await hoverAndExpectToBeVisible({
			autoClick: false,
			target: menuDisplay.getByText(childLayoutTitle),
			trigger: menuDisplay.locator('.nav-link', {
				hasText: parentLayoutTitle,
			}),
		});

		await expect(
			menuDisplay.getByText(grandChildLayoutLayoutTitle)
		).not.toBeVisible();

		// Change displayStyle

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Display Style',
			fragmentId: menuDisplayId,
			tab: 'General',
			value: 'Stacked',
		});

		// Assert layouts in menu display fragment

		await expect(menuDisplay.getByText(parentLayoutTitle)).toBeVisible();

		await expect(menuDisplay.getByText(childLayoutTitle)).toBeVisible();

		await expect(
			menuDisplay.getByText(grandChildLayoutLayoutTitle)
		).not.toBeVisible();

		// Change color style

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Selected Item Color',
			fragmentId: menuDisplayId,
			tab: 'Styles',
			value: 'Success',
			valueFromStylebook: true,
		});

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Hovered Item Color',
			fragmentId: menuDisplayId,
			tab: 'Styles',
			value: 'Warning',
			valueFromStylebook: true,
		});

		// Assert color style in edit mode

		await menuDisplay.getByText(parentLayoutTitle).hover();

		await expect(menuDisplay.getByText(parentLayoutTitle)).toHaveCSS(
			'color',
			'rgb(185, 80, 0)'
		);

		// Publish page

		await pageEditorPage.publishPage();

		// logout and assert guest user can see menu fragment

		await performLogout(page);

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await expect(menuDisplay.getByText(parentLayoutTitle)).toBeVisible();

		// Assert color style in view mode

		await expect(menuDisplay.getByText(parentLayoutTitle)).toHaveCSS(
			'color',
			'rgb(28, 28, 36)'
		);
	}
);

test(
	'Can select a navigation menu from existing ones',
	{
		tag: '@LPS-120091',
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Add public and private layouts

		const publicLayoutTitle = getRandomString();

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: publicLayoutTitle,
		});

		const privateLayoutTitle = getRandomString();

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			privateLayout: 'true',
			title: privateLayoutTitle,
		});

		// Add navigation menu

		const siteNavigationMenuName = getRandomString();

		await apiHelpers.jsonWebServicesSiteNavigationMenu.addSiteNavigationMenu(
			site.id,
			siteNavigationMenuName
		);

		// Add layout with menu display fragment and go to edit mode

		const menuDisplayId = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					fragmentConfig: {
						sublevels: -1,
					},
					id: menuDisplayId,
					key: 'com.liferay.fragment.renderer.menu.display.internal.MenuDisplayFragmentRenderer',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Assert public page

		const menuDisplay = page.locator(
			'.lfr-layout-structure-item-com-liferay-fragment-renderer-menu-display-internal-menudisplayfragmentrenderer'
		);

		await expect(menuDisplay.getByText(publicLayoutTitle)).toBeVisible();

		// Select private pages hierarchy

		await changeSource(
			menuDisplayId,
			'Private Pages Hierarchy',
			page,
			pageEditorPage
		);

		// Assert private page

		await expect(menuDisplay.getByText(privateLayoutTitle)).toBeVisible();

		// Select navigation menu

		await changeSource(
			menuDisplayId,
			siteNavigationMenuName,
			page,
			pageEditorPage
		);

		// Assert navigation item

		await expect(
			menuDisplay.getByText('There are no menu items to display')
		).toBeVisible();
	}
);

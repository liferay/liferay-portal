/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import getRandomString from '../../../utils/getRandomString';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	pageViewModePagesTest
);

test(
	'Checks center text alignment in Look and Feel',
	{
		tag: '@LPD-31641',
	},
	async ({apiHelpers, page, site, widgetPagePage}) => {

		// Create page and go to view mode

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		// Add Asset Publisher widget

		await widgetPagePage.addPortlet('Asset Publisher');

		// Open Look and Feel Configuration

		await widgetPagePage.clickOnAction(
			'Asset Publisher',
			'Look and Feel Configuration'
		);

		const lookAndFeelIFrame = page.frameLocator(
			'iframe[title="Look and Feel Configuration"]'
		);

		// Update Look and Feel Configuration

		await lookAndFeelIFrame.getByRole('tab', {name: 'General'}).click();

		await lookAndFeelIFrame
			.getByLabel('Application Decorators')
			.selectOption('Decorate');

		await lookAndFeelIFrame.getByRole('tab', {name: 'Text Styles'}).click();

		await lookAndFeelIFrame.getByLabel('Alignment').selectOption('center');

		await lookAndFeelIFrame.getByLabel('Font').selectOption('Verdana');

		await lookAndFeelIFrame.getByRole('button', {name: 'Save'}).click();

		// Assert custom styles in configuration modal

		await expect(lookAndFeelIFrame.getByLabel('Alignment')).toHaveValue(
			'center'
		);

		await expect(lookAndFeelIFrame.getByLabel('Font')).toHaveValue(
			'Verdana'
		);

		// Refresh page and assert custom styles in view mode

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		const portlet = page.locator('.portlet-asset-publisher');

		await expect(portlet).toHaveClass(/portlet-decorate/);

		await expect(portlet.locator('.portlet-content')).toHaveCSS(
			'font-family',
			'Verdana'
		);

		await expect(portlet.locator('.portlet-content')).toHaveCSS(
			'text-align',
			'center'
		);
	}
);

test(
	'Checks background color for nested widgets',
	{
		tag: '@LPD-63964',
	},
	async ({apiHelpers, page, site, widgetPagePage}) => {

		// Create widget page

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		// Add nested applications widget

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await widgetPagePage.addPortlet('Nested Applications');

		// Configure styles

		await widgetPagePage.clickOnAction(
			'Nested Applications',
			'Look and Feel Configuration'
		);

		let lookAndFeelIFrame = page.frameLocator(
			'iframe[title="Look and Feel Configuration"]'
		);

		await lookAndFeelIFrame
			.getByRole('tab', {name: 'Background Styles'})
			.click();

		await lookAndFeelIFrame
			.locator('#backgroundStyles')
			.getByLabel('Color selection is ')
			.fill('000000');

		await lookAndFeelIFrame.getByRole('button', {name: 'Save'}).click();

		await page.reload();

		await widgetPagePage.addPortlet('Web Content Display');

		await widgetPagePage.clickOnAction(
			'Web Content Display',
			'Look and Feel Configuration'
		);

		lookAndFeelIFrame = page.frameLocator(
			'iframe[title="Look and Feel Configuration"]'
		);

		await lookAndFeelIFrame
			.getByRole('tab', {name: 'Background Styles'})
			.click();

		await lookAndFeelIFrame
			.locator('#backgroundStyles')
			.getByLabel('Color selection is ')
			.fill('FFFFFF');

		await lookAndFeelIFrame.getByRole('button', {name: 'Save'}).click();

		await page
			.getByRole('dialog')
			.getByRole('button', {name: 'Close'})
			.click();

		// Add web content display widget inside nested portlet

		await widgetPagePage.dragPortlet(
			'Web Content Display',
			page
				.locator('.portlet-nested-portlets .portlet-dropzone.empty')
				.first()
		);

		await page.reload();

		// Check that styles are applied correctly

		let portlet = page.locator('.portlet-content', {
			hasText: 'Nested Applications',
		});

		await expect(portlet).toHaveCSS('background-color', 'rgb(0, 0, 0)');

		portlet = portlet.locator('.portlet-content', {
			hasText: 'Web Content Display',
		});

		await expect(portlet).toHaveCSS(
			'background-color',
			'rgb(255, 255, 255)'
		);
	}
);

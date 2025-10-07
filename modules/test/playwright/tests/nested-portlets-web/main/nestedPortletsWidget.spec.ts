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

test('User can nest a widget inside nested portlets widget', async ({
	apiHelpers,
	page,
	site,
	widgetPagePage,
}) => {

	// Add widget page and navigate to view

	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: getRandomString(),
	});

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	// Add nested applications widget and assert default template

	await widgetPagePage.addPortlet('Nested Applications');

	await widgetPagePage.clickOnAction('Nested Applications', 'Configuration');

	const configurationIFrame = page.frameLocator(
		'iframe[title*="Nested Applications"]'
	);

	await expect(
		configurationIFrame.getByLabel('2 Columns (50/50)')
	).toBeChecked();

	await page
		.locator('.modal-header')
		.getByLabel('Close', {exact: true})
		.click();

	// Add web content display widget and drag into nested applications widget

	await widgetPagePage.addPortlet('Web Content Display');

	await widgetPagePage.dragPortlet(
		'Web Content Display',
		page.locator('.portlet-nested-portlets .portlet-dropzone.empty').first()
	);

	// Check if the web content display widget is added to the nested portlets widget

	await expect(
		page
			.locator(
				'.portlet-nested-portlets .portlet-column-content-first .portlet-title-default'
			)
			.getByText('Web Content Display')
	).toBeAttached();
});

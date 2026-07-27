/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {liferayConfig} from '../../../liferay.config';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	pageViewModePagesTest
);

test(
	'The user can configure display settings on rss publisher widget',
	{
		tag: '@LPS-107942',
	},
	async ({apiHelpers, page, site, widgetPagePage}) => {

		// Add widget page and navigate to view

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		// Add rss publisher widget

		await widgetPagePage.addPortlet('RSS Publisher');

		await widgetPagePage.clickOnAction('RSS Publisher', 'Configuration');

		const configurationIFrame = page.frameLocator(
			'iframe[title*="RSS Publisher"]'
		);

		// Add url

		await configurationIFrame
			.getByLabel('Title', {exact: true})
			.fill('LA Times: Technology News');

		const file = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(path.join(__dirname, '/dependencies/rss2.0.xml'))
		);

		await configurationIFrame
			.getByLabel('URL')
			.fill(liferayConfig.environment.baseUrl + file.contentUrl);

		await widgetPagePage.save('RSS Publisher');

		// Go to display settings

		await configurationIFrame
			.getByRole('tab', {name: 'Display Settings'})
			.click();

		// Assert number of entries per feed allows integer numbers greater than 10

		await configurationIFrame
			.getByLabel('# of Entries per Feed', {exact: true})
			.fill('11');

		await widgetPagePage.save('RSS Publisher');

		await configurationIFrame
			.getByRole('tab', {name: 'Display Settings'})
			.click();

		await expect(
			configurationIFrame.getByLabel('# of Entries per Feed', {
				exact: true,
			})
		).toHaveValue('11');

		// Disable feed title

		await configurationIFrame
			.getByLabel('Show Feed Title', {exact: true})
			.uncheck();

		await widgetPagePage.saveAndClose('RSS Publisher');

		// Assert feed title is not present

		await expect(
			page.getByText('LA Times: Technology News')
		).not.toBeVisible();
	}
);

test('The user can configure feeds on rss publisher widget', async ({
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

	// Add rss publisher widget

	await widgetPagePage.addPortlet('RSS Publisher');

	// Assert info message

	await expect(
		page.getByText(
			'Info: This application is not visible to users yet. Select at least one valid rss feed to make it visible.'
		)
	).toBeVisible();

	// Open configuration

	await widgetPagePage.clickOnAction('RSS Publisher', 'Configuration');

	const configurationIFrame = page.frameLocator(
		'iframe[title*="RSS Publisher"]'
	);

	// Assert invalid url

	await configurationIFrame
		.getByLabel('Title', {exact: true})
		.fill('LA Times: Technology News');

	await configurationIFrame
		.getByLabel('URL', {exact: true})
		.fill('www.theverge.com/rss/index.xml');

	await configurationIFrame.getByRole('button', {name: 'Save'}).click();

	await waitForAlert(
		configurationIFrame,
		'Error:Your request failed to complete.',
		{
			type: 'danger',
		}
	);

	await expect(
		configurationIFrame.getByText('Error: The following are invalid URLs: ')
	).toBeVisible();

	// Add url

	const file = await apiHelpers.headlessDelivery.postDocument(
		site.id,
		createReadStream(path.join(__dirname, '/dependencies/rss2.0.xml'))
	);

	await configurationIFrame
		.getByLabel('URL')
		.fill(liferayConfig.environment.baseUrl + file.contentUrl);

	await widgetPagePage.saveAndClose('RSS Publisher');

	// Assert feed title

	await expect(page.getByText('LA Times: Technology News')).toBeVisible();

	// Delete feed

	await widgetPagePage.clickOnAction('RSS Publisher', 'Configuration');

	await configurationIFrame.getByTitle('Remove').click();

	await widgetPagePage.saveAndClose('RSS Publisher');

	// Assert info message

	await expect(
		page.getByText(
			'Info: This application is not visible to users yet. Select at least one valid rss feed to make it visible.'
		)
	).toBeVisible();
});

/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {redirectPagesTest} from '../../../fixtures/redirectPagesTest';
import {liferayConfig} from '../../../liferay.config';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {waitForAlert} from '../../../utils/waitForAlert';

export const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	redirectPagesTest
);

test.afterEach(async ({redirectPage}) => {
	await redirectPage.configureRedirectNotFound(false);
});

test.beforeEach(async ({redirectPage}) => {
	await redirectPage.configureRedirectNotFound(true);
});

test('Ensure that a redirect can be added from an active 404 URL', async ({
	apiHelpers,
	page,
	redirectPage,
	site,
}) => {
	const destinationPage = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: 'Destination Page',
	});

	await page.goto(`/web/${site.name}/invalid-page`);

	await redirectPage.goto(site.friendlyUrlPath);

	await page.getByRole('link', {name: 'URLs'}).click();

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByRole('menuitem', {
			exact: true,
			name: 'Create Redirect',
		}),
		trigger: page.getByRole('button', {name: 'Show Actions'}),
	});

	await page.waitForTimeout(500);

	await page
		.getByLabel('Destination URL')
		.fill(
			`${liferayConfig.environment.baseUrl}/web/${site.name}${destinationPage.friendlyURL}`
		);

	await page.getByRole('button', {name: 'Create'}).click();

	await waitForAlert(page);

	await page.goto(`/web/${site.name}/invalid-page`);

	await expect(page.url()).toContain(destinationPage.friendlyURL);
});

test('Ensure that a redirect can be added from an ignored 404 URL', async ({
	apiHelpers,
	page,
	redirectPage,
	site,
}) => {
	const destinationPage = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: 'Destination Page',
	});

	await page.goto(`/web/${site.name}/invalid-page`);

	await redirectPage.goto(site.friendlyUrlPath);

	await page.getByRole('link', {name: 'URLs'}).click();

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByRole('menuitem', {
			exact: true,
			name: 'Ignore',
		}),
		trigger: page.getByRole('button', {name: 'Show Actions'}),
	});

	await expect(
		page.getByText('All your pages are connected or redirected.')
	).toBeVisible();

	await page.getByLabel('Filter', {exact: true}).click();

	await page.getByRole('menuitem', {name: 'Ignored URLs'}).click();

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByRole('menuitem', {
			exact: true,
			name: 'Create Redirect',
		}),
		trigger: page.getByRole('button', {name: 'Show Actions'}),
	});

	await page.waitForTimeout(500);

	await page
		.getByLabel('Destination URL')
		.fill(
			`${liferayConfig.environment.baseUrl}/web/${site.name}${destinationPage.friendlyURL}`
		);

	await page.getByRole('button', {name: 'Create'}).click();

	await waitForAlert(page);

	await page.goto(`/web/${site.name}/invalid-page`);

	await expect(page.url()).toContain(destinationPage.friendlyURL);
});

test('Ensure that 404 URL tracking is not performed when disabled', async ({
	page,
	redirectPage,
	site,
}) => {
	await page.goto(`/web/${site.name}/invalid-page`);

	await redirectPage.goto(site.friendlyUrlPath);

	await page.getByRole('link', {name: 'URLs'}).click();

	await expect(page.getByText('invalid-page')).toBeVisible();

	await expect(
		page.locator('.lfr-requests-column', {hasText: '1'})
	).toBeVisible();

	await redirectPage.configureRedirectNotFound(false);

	await page.goto(`/web/${site.name}/invalid-page`);

	await page.goto(`/web/${site.name}/non-existing-url`);

	await redirectPage.goto(site.friendlyUrlPath);

	await expect(page.getByText('404 URLs')).not.toBeVisible();

	await redirectPage.configureRedirectNotFound(true);

	await redirectPage.goto(site.friendlyUrlPath);

	await page.getByRole('link', {name: 'URLs'}).click();

	await expect(page.getByText('invalid-page')).toBeVisible();

	await expect(
		page.locator('.lfr-requests-column', {hasText: '1'})
	).toBeVisible();

	await expect(page.getByText('non-existing-url')).not.toBeVisible();
});

test('Ensure that 404 URLs can be ordered by number of requests', async ({
	page,
	redirectPage,
	site,
}) => {
	await page.goto(`/web/${site.name}/invalid-page`);

	await page.goto(`/web/${site.name}/non-existing-url`);
	await page.goto(`/web/${site.name}/non-existing-url`);
	await page.goto(`/web/${site.name}/non-existing-url`);

	await redirectPage.goto(site.friendlyUrlPath);

	await page.getByRole('link', {name: 'URLs'}).click();

	await redirectPage.orderBy('Requests');

	await redirectPage.orderBy('Ascending');

	await redirectPage.assertNotFoundURLsOrder([
		'non-existing-url',
		'invalid-page',
	]);

	await redirectPage.orderBy('Descending');

	await redirectPage.assertNotFoundURLsOrder([
		'invalid-page',
		'non-existing-url',
	]);
});

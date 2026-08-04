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

export const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	redirectPagesTest
);

test('Ensure that the user will be redirected to the cached target URL of a permanent redirection after it is updated', async ({
	apiHelpers,
	page,
	redirectPage,
	site,
}) => {
	const destinationPage = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: 'Destination Page',
	});

	const newDestinationPage = await apiHelpers.jsonWebServicesLayout.addLayout(
		{
			groupId: site.id,
			title: 'New Destination Page',
		}
	);

	await redirectPage.goto(site.friendlyUrlPath);

	await redirectPage.addRedirect(
		'test/source/url',
		`${liferayConfig.environment.baseUrl}/web/${site.name}${destinationPage.friendlyURL}`,
		true
	);

	await page.goto(`/web/${site.name}/test/source/url`);

	await expect(page.url()).toContain(destinationPage.friendlyURL);

	await redirectPage.goto(site.friendlyUrlPath);

	await redirectPage.editRedirect(
		'/test/source/url',
		'test/source/url',
		`${liferayConfig.environment.baseUrl}/web/${site.name}${newDestinationPage.friendlyURL}`,
		true
	);

	await page.goto(`/web/${site.name}/test/source/url`);

	await expect(page.url()).toContain(destinationPage.friendlyURL);
});

test('Ensure that the user will be redirected to the latest target URL of a temporary redirection after it is updated', async ({
	apiHelpers,
	page,
	redirectPage,
	site,
}) => {
	const destinationPage = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: 'Destination Page',
	});

	const newDestinationPage = await apiHelpers.jsonWebServicesLayout.addLayout(
		{
			groupId: site.id,
			title: 'New Destination Page',
		}
	);

	await redirectPage.goto(site.friendlyUrlPath);

	await redirectPage.addRedirect(
		'test/source/url',
		`${liferayConfig.environment.baseUrl}/web/${site.name}${destinationPage.friendlyURL}`,
		false
	);

	await page.goto(`/web/${site.name}/test/source/url`);

	await expect(page.url()).toContain(destinationPage.friendlyURL);

	await redirectPage.goto(site.friendlyUrlPath);

	await redirectPage.editRedirect(
		'/test/source/url',
		'test/source/url',
		`${liferayConfig.environment.baseUrl}/web/${site.name}${newDestinationPage.friendlyURL}`,
		false
	);

	await page.goto(`/web/${site.name}/test/source/url`);

	await expect(page.url()).toContain(newDestinationPage.friendlyURL);
});

test('Ensure that a redirect can be added without updating references for redirect chain', async ({
	apiHelpers,
	page,
	redirectPage,
	site,
}) => {
	await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: 'Test Page 1',
	});

	await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: 'Test Page 2',
	});

	await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: 'Test Page 3',
	});

	await redirectPage.goto(site.friendlyUrlPath);

	await redirectPage.addRedirect(
		'test-page-1',
		`${liferayConfig.environment.baseUrl}/web/${site.name}/test-page-2`,
		false
	);

	await page.getByRole('link', {name: 'Add'}).click();

	await redirectPage.fillRedirectDetails(
		'test-page-2',
		`${liferayConfig.environment.baseUrl}/web/${site.name}/test-page-3`,
		false
	);

	await page.getByRole('button', {name: 'Create'}).click();

	await redirectPage.updateReferences(false);

	await expect(
		page.locator('.lfr-destination-url-column', {hasText: 'test-page-2'})
	).toBeVisible();

	await page.goto(`/web/${site.name}/test-page-1`);

	await expect(page.url()).toContain('test-page-3');

	await page.goto(`/web/${site.name}/test-page-2`);

	await expect(page.url()).toContain('test-page-3');
});

test('Ensure that a redirect can be added with updating references for redirect chain', async ({
	apiHelpers,
	page,
	redirectPage,
	site,
}) => {
	await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: 'Test Page 1',
	});

	await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: 'Test Page 2',
	});

	await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: 'Test Page 3',
	});

	await redirectPage.goto(site.friendlyUrlPath);

	await redirectPage.addRedirect(
		'test-page-1',
		`${liferayConfig.environment.baseUrl}/web/${site.name}/test-page-2`,
		false
	);

	await page.getByRole('link', {name: 'Add'}).click();

	await redirectPage.fillRedirectDetails(
		'test-page-2',
		`${liferayConfig.environment.baseUrl}/web/${site.name}/test-page-3`,
		false
	);

	await page.getByRole('button', {name: 'Create'}).click();

	await redirectPage.updateReferences();

	await expect(
		page.locator('.lfr-destination-url-column', {hasText: 'test-page-3'})
	).toHaveCount(2);

	await page.goto(`/web/${site.name}/test-page-1`);

	await expect(page.url()).toContain('test-page-3');

	await page.goto(`/web/${site.name}/test-page-2`);

	await expect(page.url()).toContain('test-page-3');
});

test('Ensure destination URL is validated before it can be saved', async ({
	page,
	redirectPage,
	site,
}) => {
	await redirectPage.goto(site.friendlyUrlPath);

	await page.getByRole('link', {name: 'Add'}).click();

	await redirectPage.assertDestinationURLValidation(' ');
	await redirectPage.assertDestinationURLValidation('liferay.com');
	await redirectPage.assertDestinationURLValidation('test');
	await redirectPage.assertDestinationURLValidation('redirect/test');
});

test('Ensure HTTP prefix is added to destination URL', async ({
	page,
	redirectPage,
	site,
}) => {
	await redirectPage.goto(site.friendlyUrlPath);

	await redirectPage.addRedirect('test/source/url', `www.liferay.com`, false);

	await expect(
		page.locator('.lfr-destination-url-column', {
			hasText: 'http://www.liferay.com',
		})
	).toBeVisible();
});

test('Ensure Check URL button opens to correct destination URL', async ({
	apiHelpers,
	page,
	redirectPage,
	site,
}) => {
	const destinationPage = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: 'Destination Page',
	});

	await redirectPage.goto(site.friendlyUrlPath);

	await page.getByRole('link', {name: 'Add'}).click();

	await redirectPage.fillRedirectDetails(
		'test/source/url',
		`${liferayConfig.environment.baseUrl}/web/${site.name}${destinationPage.friendlyURL}`,
		false
	);

	const [newPage] = await Promise.all([
		page.waitForEvent('popup'),
		page.getByLabel('Check URL').click(),
	]);

	await expect(newPage.url()).toContain(destinationPage.friendlyURL);
});

test('Ensure redirect entry can be deleted', async ({
	page,
	redirectPage,
	site,
}) => {
	await redirectPage.goto(site.friendlyUrlPath);

	await redirectPage.addRedirect(
		'test/source/url',
		`http://www.liferay.com`,
		false
	);

	await redirectPage.deleteRedirect('test/source/url');

	await expect(page.getByText('No redirects were found.')).toBeVisible();

	await page.goto(`/web/${site.name}/test/source/url`);

	await expect(page.url()).toContain('test/source/url');
});

test('Ensure all redirect entries can be deleted simultaneously', async ({
	page,
	redirectPage,
	site,
}) => {
	await redirectPage.goto(site.friendlyUrlPath);

	await redirectPage.addRedirect(
		'test/source/url',
		`http://www.liferay.com`,
		false
	);
	await redirectPage.addRedirect(
		'test/source/url2',
		`http://www.liferay.com`,
		false
	);

	await page.getByLabel('Select All Items on the Page').click();

	await page.getByRole('button', {name: 'Delete'}).click();

	await expect(page.getByText('No redirects were found.')).toBeVisible();

	await page.goto(`/web/${site.name}/test/source/url`);

	await expect(page.url()).toContain('test/source/url');
});

test('Ensure an expired redirect entry can be reset', async ({
	apiHelpers,
	page,
	redirectPage,
	site,
}) => {
	const destinationPage = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: 'Destination Page',
	});

	await redirectPage.goto(site.friendlyUrlPath);

	await redirectPage.addRedirect(
		'test/source/url',
		`${liferayConfig.environment.baseUrl}/web/${site.name}${destinationPage.friendlyURL}`,
		false,
		'01/01/2000'
	);

	await page.goto(`/web/${site.name}/test/source/url`);

	await expect(page.url()).toContain('/test/source/url');

	await redirectPage.goto(site.friendlyUrlPath);

	await redirectPage.editRedirect(
		'/test/source/url',
		'test/source/url',
		`${liferayConfig.environment.baseUrl}/web/${site.name}${destinationPage.friendlyURL}`,
		false,
		'12/31/2099'
	);

	await page.goto(`/web/${site.name}/test/source/url`);

	await expect(page.url()).toContain(destinationPage.friendlyURL);
});

test('Ensure redirect entries can be found by search', async ({
	page,
	redirectPage,
	site,
}) => {
	await redirectPage.goto(site.friendlyUrlPath);

	await redirectPage.addRedirect(
		'test/source',
		`${liferayConfig.environment.baseUrl}/web/${site.name}/test/destination`,
		false
	);

	await redirectPage.addRedirect(
		'test/origin',
		`${liferayConfig.environment.baseUrl}/web/${site.name}/test/landing`,
		true,
		'12/31/2099'
	);

	await page.getByPlaceholder('Search for').fill('source');

	await page.keyboard.press('Enter');

	await expect(page.getByText('1 Result Found')).toBeVisible();

	await expect(
		page.locator('.lfr-source-url-column', {hasText: 'test/source'})
	).toBeVisible();

	await expect(
		page.locator('.lfr-destination-url-column', {
			hasText: 'test/destination',
		})
	).toBeVisible();

	await expect(
		page.locator('.lfr-type-column', {hasText: 'Temporary'})
	).toBeVisible();

	await expect(
		page.locator('.lfr-expiration-column', {hasText: '-'})
	).toBeVisible();

	await page.getByPlaceholder('Search for').fill('origin');

	await page.keyboard.press('Enter');

	await expect(page.getByText('1 Result Found')).toBeVisible();

	await expect(
		page.locator('.lfr-source-url-column', {hasText: 'test/origin'})
	).toBeVisible();

	await expect(
		page.locator('.lfr-destination-url-column', {hasText: 'test/landing'})
	).toBeVisible();

	await expect(
		page.locator('.lfr-type-column', {hasText: 'Permanent'})
	).toBeVisible();

	await expect(
		page.locator('.lfr-expiration-column', {hasText: '12/31/99'})
	).toBeVisible();
});

test('Ensure warning messages are displayed when staging is enabled', async ({
	apiHelpers,
	page,
	redirectPage,
	site,
}) => {
	await apiHelpers.jsonWebServicesStaging.enableLocalStaging({
		groupId: site.id,
	});

	await page.waitForTimeout(2000);

	await redirectPage.goto(`${site.friendlyUrlPath}-staging`);

	await expect(
		page.getByText('Redirections are unavailable in staged sites.')
	).toBeVisible();

	await redirectPage.goto(site.friendlyUrlPath);

	await expect(
		page.getByText(
			'Redirect functionality may not work as expected in the staging environment.'
		)
	).toBeVisible();
});

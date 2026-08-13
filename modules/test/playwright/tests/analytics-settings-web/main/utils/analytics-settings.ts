/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {liferayConfig} from '../../../../liferay.config';
import {clickAndExpectToBeHidden} from '../../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {createChannel} from '../../../osb-faro-web/main/utils/channel';
import {createDataSource} from '../../../osb-faro-web/main/utils/data-source';
import {acceptsCookiesBanner} from '../../../osb-faro-web/main/utils/portal';

export const PROPERTY_SITE_COLUMN_INDEX = 1;

export async function connectToAnalyticsCloud(
	page: Page,
	{token}: {token: string}
) {
	await page.getByPlaceholder('Paste token here.').fill(token);

	await page.getByRole('button', {name: 'Connect'}).last().click();
}

export async function connectToAnalyticsCloudWithNoSiteSynced(page: Page) {
	const {token} = await createDataSource(page);

	await goToAnalyticsCloudInstanceSettings(page);

	await acceptsCookiesBanner(page);

	await disconnectFromAnalyticsCloud(page);

	await connectToAnalyticsCloud(page, {token});

	await goNextStep(page);

	await goNextStep(page);

	await page.getByRole('button', {name: 'Finish'}).click();
}

export async function disconnectFromAnalyticsCloud(page: Page) {
	const disconnectButton = page.getByRole('button', {name: 'Disconnect'});

	if (await disconnectButton.isVisible()) {
		await disconnectButton.click();

		const confirmationModal = page.getByLabel('Disconnecting Data Source');

		const confirmationButton = confirmationModal.getByRole('button', {
			name: 'Disconnect',
		});

		await confirmationButton.click();

		await waitForAlert(page, 'Workspace disconnected.');
	}
}

export async function expectPropertyColumn({
	channelName,
	expectedValue,
	index,
	page,
}: {
	channelName: string;
	expectedValue: string;
	index: number;
	page: Page;
}) {
	const channel = await findChannel({channelName, page});

	const cellContents = await channel.locator('td').allTextContents();

	expect(cellContents[index]).toBe(expectedValue);
}

export async function findChannel({
	channelName,
	page,
}: {
	channelName: string;
	page: Page;
}): Promise<any> {
	const searchInput = page.getByRole('textbox', {name: 'Search'}).first();

	const clearButton = page.getByRole('button', {name: 'Clear'}).first();

	if (await clearButton.isVisible()) {
		await searchInput.clear();

		await clickAndExpectToBeHidden({
			target: clearButton,
			trigger: clearButton,
		});
	}

	await searchInput.fill(channelName);

	await page.getByRole('button', {name: 'Search'}).first().click();

	await expect(page.locator('table.table tbody tr')).toHaveCount(1);

	await expect(
		page.getByRole('cell', {exact: true, name: channelName})
	).toBeVisible();

	return page.locator('table.table tbody tr:first-child');
}

export async function goToAnalyticsCloudInstanceSettings(page: Page) {
	await page.goto(liferayConfig.environment.baseUrl);

	await page.goto(`${PORTLET_URLS.analyticsCloudConnection}`);

	await page.getByText('Analytics Cloud Token').waitFor({state: 'visible'});
}

export async function goToSettingsStep({
	page,
	stepName,
}: {
	page: Page;
	stepName: string;
}) {
	await goToAnalyticsCloudInstanceSettings(page);

	await page.getByRole('menuitem', {name: stepName}).click();
}

export async function syncAllContacts(page: Page) {
	const wizard = page.locator('[data-testid="VIEW_WIZARD_MODE"]');

	await expect(wizard.getByText('Sync People')).toBeVisible({
		timeout: 100 * 1000,
	});

	const syncContactsButton = page.locator(
		'[data-testid="sync-all-contacts-and-accounts__false"]'
	);

	if (await syncContactsButton.isVisible()) {
		await syncContactsButton.click();
	}
}

export async function syncAnalyticsCloudViaAPI({
	apiHelpers,
	channel,
	channelName,
	project,
	siteId,
	syncedOrganizationIds,
	syncedUserGroupIds,
}: {
	apiHelpers: ApiHelpers;
	channel?: any;
	channelName?: string;
	project?: any;
	siteId?: number;
	syncedOrganizationIds?: number[];
	syncedUserGroupIds?: number[];
}): Promise<{
	channel: any;
	project: any;
}> {
	if (!channel) {
		({channel, project} = await createChannel({
			apiHelpers,
			channelName,
		}));
	}

	const connectionToken =
		await apiHelpers.jsonWebServicesOSBFaro.fetchDataSourceConnectionToken(
			project.groupId
		);

	await apiHelpers.analyticsSettingsRest.postDataSource(connectionToken);

	if (siteId !== undefined) {
		await apiHelpers.analyticsSettingsRest.syncSitesToChannel(channel.id, [
			siteId,
		]);
	}

	if (syncedOrganizationIds?.length) {
		await apiHelpers.analyticsSettingsRest.putContactsConfiguration({
			syncedOrganizationIds,
		});
	}
	else if (syncedUserGroupIds?.length) {
		await apiHelpers.analyticsSettingsRest.putContactsConfiguration({
			syncedUserGroupIds,
		});
	}
	else {
		await apiHelpers.analyticsSettingsRest.putContactsConfiguration({
			syncAllAccounts: true,
			syncAllContacts: true,
		});
	}

	return {
		channel,
		project,
	};
}

export async function syncAnalyticsCloud({
	apiHelpers,
	channel,
	channelName,
	organizationName,
	page,
	project,
	siteName,
	userGroupName,
}: {
	apiHelpers: ApiHelpers;
	channel?: any;
	channelName?: string;
	organizationName?: string;
	page: Page;
	project?: any;
	siteName?: string;
	userGroupName?: string;
}): Promise<{
	channel: any;
	project: any;
}> {
	if (!channel) {
		({channel, project} = await createChannel({
			apiHelpers,
			channelName,
		}));
	}

	const {token} = await createDataSource(page);

	await goToAnalyticsCloudInstanceSettings(page);

	await acceptsCookiesBanner(page);

	await disconnectFromAnalyticsCloud(page);

	await connectToAnalyticsCloud(page, {token});

	await toggleSiteSync({
		channelName: channel.name,
		page,
		siteName,
	});

	await goNextStep(page);

	if (userGroupName || organizationName) {
		await syncContactsData({
			organizationName,
			page,
			userGroupName,
		});
	}
	else {
		await syncAllContacts(page);
	}

	await goNextStep(page);

	const nextButton = page.getByRole('button', {
		exact: true,
		name: 'Next',
	});

	if (await nextButton.isVisible()) {
		await nextButton.click();
	}

	await page.getByRole('button', {name: 'Finish'}).click();

	await waitForAlert(
		page,
		'Success:DXP has successfully connected to Analytics Cloud. You will begin to see data as activities occur on your sites.'
	);

	return {
		channel,
		project,
	};
}

export async function syncContactsData({
	organizationName,
	page,
	userGroupName,
}: {
	organizationName?: string;
	page: Page;
	userGroupName?: string;
}) {
	const selectContactsCollapsed = page
		.locator('[aria-expanded="false"]')
		.getByText('Select Contacts');

	if (await selectContactsCollapsed.isVisible()) {
		await page.getByRole('button', {name: 'Select Contacts'}).click();
	}

	if (userGroupName) {
		await page.getByText('User Groups').click();

		await page
			.locator(`[data-testid="${userGroupName}"]`)
			.locator('[type="checkbox"]')
			.check();

		await page.getByRole('button', {name: 'Add'}).click();
	}
	else if (organizationName) {
		await page.getByText('Organizations', {exact: true}).click();

		await page
			.locator(`[data-testid="${organizationName}"]`)
			.locator('[type="checkbox"]')
			.check();

		await page.getByRole('button', {name: 'Add'}).click();
	}
}

export async function toggleSiteSync({
	channelName,
	page,
	siteName = 'Liferay DXP Site',
	synced = true,
}: {
	channelName: string;
	page: Page;
	siteName?: string;
	synced?: boolean;
}) {
	const channel = await findChannel({channelName, page});

	await clickAndExpectToBeVisible({
		target: page.getByRole('dialog'),
		trigger: channel.locator('button'),
	});

	await page
		.getByText('Sites can only be assigned to a single property at a time')
		.waitFor({state: 'visible'});

	await page.locator('.modal').getByPlaceholder('Search').fill(siteName);

	await page.locator('.modal').getByRole('button', {name: 'Search'}).click();

	await expect(page.locator('span[data-testid="loading"]')).toBeHidden();

	const sitesTable = page.locator('[data-testid="sites"]');

	await expect(sitesTable).toBeVisible();

	const siteRow = sitesTable.locator('tbody tr').filter({hasText: siteName});

	await expect(siteRow).toBeVisible();

	const checkbox = siteRow.locator('input[type="checkbox"]');

	if (synced) {
		await checkbox.check();
	}
	else {
		await checkbox.uncheck();
	}

	await page.locator('.modal .modal-item-last button.btn-primary').click();

	await waitForAlert(page, 'Properties settings have been saved.');
}

export async function goNextStep(page: Page) {
	await page.getByRole('button', {exact: true, name: 'Next'}).click();
}

export async function goPreviousStep(page: Page) {
	await page.getByRole('button', {exact: true, name: 'Previous'}).click();
}

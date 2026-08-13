/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedChannelTest} from '../../../fixtures/isolatedChannelTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginAnalyticsCloudTest} from '../../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {
	PROPERTY_SITE_COLUMN_INDEX,
	expectPropertyColumn,
	findChannel,
	goToSettingsStep,
	syncAnalyticsCloud,
	toggleSiteSync,
} from './utils/analytics-settings';

export const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-20640': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedChannelTest,
	isolatedSiteTest,
	loginAnalyticsCloudTest(),
	loginTest()
);

// Opens the "Assign Sites" modal for the given property

async function openAssignModal(page: Page, propertyName: string) {
	const propertyRow = await findChannel({channelName: propertyName, page});

	await clickAndExpectToBeVisible({
		target: page.getByRole('dialog'),
		trigger: propertyRow.locator("[role='assign-button']"),
	});
}

// Searches the assign modal for the given term

async function searchAssignModal(page: Page, searchTerm: string) {
	const modal = page.locator('.modal');

	// The site list loads asynchronously; wait for it before searching

	await expect(modal.locator('.pagination-results')).toBeVisible();

	const searchField = modal.getByPlaceholder('Search');

	await searchField.fill(searchTerm);

	await searchField.press('Enter');
}

test(
	'Sites can be searched in the assign property modal',
	{
		tag: '@LRAC-12579',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project, site}) => {

		// Single-token names so the modal search (which tokenizes on spaces) matches exactly one

		const otherSite = await apiHelpers.headlessAdminSite.postSite({
			name: 'other' + getRandomString(),
		});

		try {
			await syncAnalyticsCloud({
				apiHelpers,
				channel,
				page,
				project,
				siteName: site.name,
			});

			await goToSettingsStep({page, stepName: 'Properties'});

			await openAssignModal(page, channel.name);

			const resultsMessage = page.locator('.modal .pagination-results');

			await searchAssignModal(page, otherSite.name);

			await expect(resultsMessage).toHaveText(
				'Showing 1 to 1 of 1 entries.'
			);

			await expect(
				page.locator(`.modal tr[data-testid="${otherSite.name}"]`)
			).toBeVisible();

			await expect(
				page.locator(`.modal tr[data-testid="${site.name}"]`)
			).toHaveCount(0);

			// A search with no match shows the empty message

			await searchAssignModal(page, 'ZZ' + getRandomString());

			await expect(page.getByText('No sites were found.')).toBeVisible();
		}
		finally {
			await apiHelpers.headlessAdminSite
				.deleteSite(otherSite.externalReferenceCode)
				.catch(() => {});
		}
	}
);

test(
	'A site assigned to one property is unavailable to another property',
	{
		tag: '@LRAC-12577',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project, site}) => {
		const propertyB = await apiHelpers.jsonWebServicesOSBFaro.createChannel(
			'propertyB' + getRandomString(),
			project.groupId
		);

		try {
			await syncAnalyticsCloud({
				apiHelpers,
				channel,
				page,
				project,
				siteName: site.name,
			});

			await goToSettingsStep({page, stepName: 'Properties'});

			// Confirm the second property (an AC channel) surfaces as an
			// assignable DXP property before relying on it

			await page
				.getByRole('textbox', {name: 'Search'})
				.first()
				.fill(propertyB.name);

			await page.getByRole('button', {name: 'Search'}).first().click();

			await expect(
				page.getByRole('cell', {name: propertyB.name})
			).toBeVisible();

			// Open the second property's assign modal

			await openAssignModal(page, propertyB.name);

			// The site already used by the first property cannot be selected

			await searchAssignModal(page, site.name);

			await expect(
				page.locator(
					`.modal tr[data-testid="${site.name}"] input[type="checkbox"]`
				)
			).toBeDisabled();
		}
		finally {
			await apiHelpers.jsonWebServicesOSBFaro
				.deleteChannel(`[${propertyB.id}]`, project.groupId)
				.catch(() => {});
		}
	}
);

test(
	'Two different properties can each sync their own site',
	{
		tag: '@LRAC-12576',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project, site}) => {
		const propertyB = await apiHelpers.jsonWebServicesOSBFaro.createChannel(
			'propertyB' + getRandomString(),
			project.groupId
		);

		const siteB = await apiHelpers.headlessAdminSite.postSite({
			name: 'siteB' + getRandomString(),
		});

		try {
			await syncAnalyticsCloud({
				apiHelpers,
				channel,
				page,
				project,
				siteName: site.name,
			});

			await goToSettingsStep({page, stepName: 'Properties'});

			// The list needs to settle before the property rows can be acted on

			await page
				.getByRole('textbox', {name: 'Search'})
				.first()
				.fill(propertyB.name);

			await page.getByRole('button', {name: 'Search'}).first().click();

			await expect(
				page.getByRole('cell', {name: propertyB.name})
			).toBeVisible();

			// Second property: assign its own site (the first property's site is
			// already synced by syncAnalyticsCloud)

			await toggleSiteSync({
				channelName: propertyB.name,
				page,
				siteName: siteB.name,
			});

			// Each property counts exactly one site

			for (const propertyName of [channel.name, propertyB.name]) {
				await expectPropertyColumn({
					channelName: propertyName,
					expectedValue: '1',
					index: PROPERTY_SITE_COLUMN_INDEX,
					page,
				});
			}
		}
		finally {
			await apiHelpers.headlessAdminSite.deleteSite(
				siteB.externalReferenceCode
			);

			await apiHelpers.jsonWebServicesOSBFaro.deleteChannel(
				`[${propertyB.id}]`,
				project.groupId
			);
		}
	}
);

test(
	'Sites can be paginated in the assign property modal',
	{
		tag: '@LRAC-12580',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project, site}) => {
		const siteToken = 'pgsite' + getRandomString();

		const sites = [];

		// Six sites, isolated by a unique token, so the modal search yields a
		// deterministic six entries regardless of other data in the instance

		for (let i = 0; i < 6; i++) {
			sites.push(
				await apiHelpers.headlessAdminSite.postSite({
					name: `${siteToken}${i}`,
				})
			);
		}

		try {
			await syncAnalyticsCloud({
				apiHelpers,
				channel,
				page,
				project,
				siteName: site.name,
			});

			await goToSettingsStep({page, stepName: 'Properties'});

			await openAssignModal(page, channel.name);

			const modal = page.locator('.modal');

			const resultsMessage = modal.locator('.pagination-results');

			await searchAssignModal(page, siteToken);

			await expect(resultsMessage).toHaveText(
				'Showing 1 to 6 of 6 entries.'
			);

			// Reduce the page size so the six entries span two pages

			await modal.getByLabel('Items Per Page').click();

			await page
				.locator('.dropdown-menu.show')
				.getByText('5', {exact: true})
				.click();

			await expect(resultsMessage).toHaveText(
				'Showing 1 to 5 of 6 entries.'
			);

			await expect(modal.locator('tbody tr')).toHaveCount(5);

			await modal.getByLabel('Go to page, 2').click();

			await expect(resultsMessage).toHaveText(
				'Showing 6 to 6 of 6 entries.'
			);

			await expect(modal.locator('tbody tr')).toHaveCount(1);
		}
		finally {
			for (const createdSite of sites) {
				await apiHelpers.headlessAdminSite
					.deleteSite(createdSite.externalReferenceCode)
					.catch(() => {});
			}
		}
	}
);

test(
	'Sites can be sorted by name in the assign property modal',
	{
		tag: '@LRAC-12581',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project, site}) => {
		const siteToken = 'pgsorts' + getRandomString();

		const sites = [];

		// Create out of order so the assertion proves the sort, not the
		// creation order

		for (const suffix of ['c', 'a', 'b']) {
			sites.push(
				await apiHelpers.headlessAdminSite.postSite({
					name: `${siteToken}${suffix}`,
				})
			);
		}

		try {
			await syncAnalyticsCloud({
				apiHelpers,
				channel,
				page,
				project,
				siteName: site.name,
			});

			await goToSettingsStep({page, stepName: 'Properties'});

			await openAssignModal(page, channel.name);

			const modal = page.locator('.modal');

			await searchAssignModal(page, siteToken);

			await expect(modal.locator('.pagination-results')).toHaveText(
				'Showing 1 to 3 of 3 entries.'
			);

			// Order by the name column

			await modal.getByRole('button', {name: 'Filter and Order'}).click();

			await page
				.locator('.dropdown-menu.show')
				.getByText('Site Name', {exact: true})
				.click();

			const readOrder = () =>
				modal
					.locator('tbody tr')
					.evaluateAll((rows) =>
						rows.map((row) => row.dataset.testid)
					);

			const ascending = ['a', 'b', 'c'].map(
				(suffix) => `${siteToken}${suffix}`
			);

			const descending = [...ascending].reverse();

			const order = await readOrder();

			expect([ascending, descending]).toContainEqual(order);

			// Toggling the sort direction reverses the list

			await modal.getByLabel('sort').click();

			await expect.poll(() => readOrder()).toEqual([...order].reverse());
		}
		finally {
			for (const createdSite of sites) {
				await apiHelpers.headlessAdminSite
					.deleteSite(createdSite.externalReferenceCode)
					.catch(() => {});
			}
		}
	}
);

test(
	'Assigned sites are counted on the property after syncing',
	{
		tag: '@LRAC-12574',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project, site}) => {
		const siteB = await apiHelpers.headlessAdminSite.postSite({
			name: 'siteB' + getRandomString(),
		});

		try {
			await syncAnalyticsCloud({
				apiHelpers,
				channel,
				page,
				project,
				siteName: site.name,
			});

			await goToSettingsStep({page, stepName: 'Properties'});

			// Assign a second site to the property

			await toggleSiteSync({
				channelName: channel.name,
				page,
				siteName: siteB.name,
			});

			// Two sites are counted

			await expectPropertyColumn({
				channelName: channel.name,
				expectedValue: '2',
				index: PROPERTY_SITE_COLUMN_INDEX,
				page,
			});
		}
		finally {
			await apiHelpers.headlessAdminSite
				.deleteSite(siteB.externalReferenceCode)
				.catch(() => {});
		}
	}
);

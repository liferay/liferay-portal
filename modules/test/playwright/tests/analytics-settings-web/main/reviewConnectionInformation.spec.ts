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
import {waitForAlert} from '../../../utils/waitForAlert';
import {syncAnalyticsCloud} from './utils/analytics-settings';

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

// The "N Selected" preview shown next to a sync category in the review sidebar

function categoryPreview(page: Page, category: string) {
	return page
		.locator('.list-group-item')
		.filter({hasText: category})
		.locator('.list-group-text')
		.first();
}

async function getSelectedCount(page: Page, category: string) {
	const text = (await categoryPreview(page, category).textContent()) || '';

	return Number(text.replace(/[^0-9]/g, ''));
}

// The category counts load progressively, so wait until the value is non-zero and stable

async function readStableCount(page: Page, category: string) {
	let previous = -1;

	await expect
		.poll(
			async () => {
				const current = await getSelectedCount(page, category);

				const stable = current > 0 && current === previous;

				previous = current;

				return stable;
			},
			{intervals: [500], timeout: 30000}
		)
		.toBe(true);

	return getSelectedCount(page, category);
}

// Opens a category attribute dialog, toggles the given attributes, and syncs

async function toggleAttributes(
	page: Page,
	category: string,
	attributeNames: string[]
) {
	const dialog = page.getByRole('dialog');

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: dialog,
		trigger: page
			.locator('.list-group-item')
			.filter({hasText: category})
			.getByRole('button'),
	});

	await expect(dialog.getByText(`Sync ${category} Attributes`)).toBeVisible();

	for (const attributeName of attributeNames) {
		await dialog.getByPlaceholder('Search').fill(attributeName);

		await page.keyboard.press('Enter');

		await dialog
			.locator(
				`tr[data-testid='${attributeName}'] input[type='checkbox']`
			)
			.click({force: true});
	}

	await dialog.getByRole('button', {name: 'Sync'}).click();
}

test(
	'Contacts and accounts sync can be modified in the people review sidebar',
	{
		tag: '@LRAC-12677',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project, site}) => {
		const accountGroups = await Promise.all(
			['Brazilian', 'European', 'North American'].map((name) =>
				apiHelpers.headlessAdminUser.postAccountGroup({
					name: `${name} ${getRandomString()}`,
				})
			)
		);

		try {

			// Connect DXP to AC through the wizard

			await syncAnalyticsCloud({
				apiHelpers,
				channel,
				page,
				project,
				siteName: site.name,
			});

			// Open the People review sidebar

			await page.getByRole('menuitem', {name: 'People'}).click();

			// Disable the global sync, then enable only the contacts sync

			await page
				.getByTestId('sync-all-contacts-and-accounts__true')
				.click();

			await page.getByRole('button', {name: 'Select Contacts'}).click();

			await expect(
				page.getByTestId('sync-all-contacts-and-accounts__true')
			).toHaveCount(0);

			await expect(
				page.getByTestId('sync-all-accounts__true')
			).toHaveCount(0);

			await page.getByTestId('sync-all-contacts__false').click();

			await expect(
				page.getByTestId('sync-all-contacts__true')
			).toBeVisible();

			// Sync one account group and assert the selected count

			const accountGroupsPreview = page.getByText(
				'Sync All AccountsSync'
			);

			const selectAccountGroup = async (accountGroupName: string) => {
				const syncByAccountGroups = page
					.getByRole('region', {name: 'Sync All Accounts Sync'})
					.getByRole('listitem');

				if (!(await syncByAccountGroups.isVisible())) {
					await page.getByText('Select Accounts').click();
				}

				await syncByAccountGroups.click();

				// The account group list loads asynchronously, so wait for the row

				const accountGroupRow = page
					.getByRole('row', {name: accountGroupName})
					.first();

				await accountGroupRow.waitFor({
					state: 'attached',
					timeout: 30000,
				});

				await accountGroupRow.locator('input').click({force: true});

				await page.getByRole('button', {name: 'Add'}).click();

				await waitForAlert(page, 'Success');
			};

			await selectAccountGroup(accountGroups[1].name);

			await expect(
				accountGroupsPreview.getByText('1 Selected')
			).toBeVisible();

			await selectAccountGroup(accountGroups[2].name);

			await expect(
				accountGroupsPreview.getByText('2 Selected')
			).toBeVisible();
		}
		finally {
			for (const accountGroup of accountGroups) {
				await apiHelpers.headlessAdminUser
					.deleteAccountGroup(accountGroup.id)
					.catch(() => {});
			}
		}
	}
);

test(
	'Synced attributes can be modified in the attribute review sidebar',
	{
		tag: '@LRAC-12678',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project, site}) => {
		await syncAnalyticsCloud({
			apiHelpers,
			channel,
			page,
			project,
			siteName: site.name,
		});

		await page.getByRole('menuitem', {name: 'Attributes'}).click();

		// Deselecting two People attributes lowers the People count by two

		const peopleStart = await readStableCount(page, 'People');

		await toggleAttributes(page, 'People', ['birthday', 'timeZoneId']);

		await expect
			.poll(() => getSelectedCount(page, 'People'))
			.toBe(peopleStart - 2);

		// Selecting one People attribute raises the People count by one

		const peopleAfterRemoval = await getSelectedCount(page, 'People');

		await toggleAttributes(page, 'People', ['facebookId']);

		await expect
			.poll(() => getSelectedCount(page, 'People'))
			.toBe(peopleAfterRemoval + 1);

		// Deselecting two Account attributes lowers the Account count by two

		const accountStart = await readStableCount(page, 'Account');

		await toggleAttributes(page, 'Account', [
			'description',
			'defaultCPaymentMethodKey',
		]);

		await expect
			.poll(() => getSelectedCount(page, 'Account'))
			.toBe(accountStart - 2);

		// Selecting one Account attribute raises the Account count by one

		const accountAfterRemoval = await getSelectedCount(page, 'Account');

		await toggleAttributes(page, 'Account', ['externalReferenceCode']);

		await expect
			.poll(() => getSelectedCount(page, 'Account'))
			.toBe(accountAfterRemoval + 1);
	}
);

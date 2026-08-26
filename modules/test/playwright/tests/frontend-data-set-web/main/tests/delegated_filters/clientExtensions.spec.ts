/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../../fixtures/pageEditorPagesTest';
import {waitForFDS} from '../../../../../utils/waitFor';
import {fdsSamplePageTest} from '../../fixtures/fdsSamplePageTest';

// The Delegated Filters data set declares Color, Size, and Status, shows no
// filter UI once a client extension takes the filtering over, and pages the
// 100 sample entries ten at a time. The entries are seeded in a fixed cycle,
// which is what makes the totals below predictable: the color rotates over
// four values, so a quarter of the entries are Blue and entry 1 is a Green
// one, and the size rotates over six.

const ITEMS_PER_PAGE = 10;

const TOTAL_ENTRIES = 100;

// The Color filter the data set declares arrives preloaded with Blue, Green,
// and Yellow selected, which leaves out the quarter of the entries that are
// Red. Nothing reaches the request while a client extension owns the
// filtering, so this total is what the data set shows only while it filters
// for itself.

const PRELOADED_COLOR_ENTRIES = 75;

const test = mergeTests(
	apiHelpersTest,
	fdsSamplePageTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
);

let customElement: Locator;
let fdsPageUrl: string;

test.beforeEach(async ({fdsSamplePage, page, site}) => {
	const {url} = await fdsSamplePage.setupFDSSampleWidget({site});

	fdsPageUrl = url;

	customElement = page.locator('liferay-sample-custom-element-8');
});

// The data set sorts by title, which is a string, so the entry a filter
// leaves in first place is not the one with the lowest number. Whether a
// given entry is in the result set at all is what the seeding cycle makes
// predictable, so the assertions below name an entry rather than a position.

function getEntry({
	fdsSamplePage,
	position,
}: {
	fdsSamplePage: {table: {container: Locator}};
	position: number;
}) {
	return fdsSamplePage.table.container.getByText(
		`This is a description for sample ${position}.`
	);
}

async function expectTotalEntries({page, total}: {page: Page; total: number}) {
	await expect(
		page.getByText(
			`Showing 1 to ${Math.min(
				total,
				ITEMS_PER_PAGE
			)} of ${total} entries.`
		)
	).toBeVisible();
}

function getFilterOptionCheckbox({name}: {name: string}) {
	return customElement.getByRole('checkbox', {name});
}

async function expandFilterPanel({name}: {name: string}) {
	const panelButton = customElement.getByRole('button', {name});

	await panelButton.click();

	await expect(panelButton).toHaveAttribute('aria-expanded', 'true');
}

async function goToDelegatedFiltersTab({
	fdsSamplePage,
	page,
}: {
	fdsSamplePage: {selectTab: (label: string) => Promise<void>};
	page: Page;
}) {
	await page.goto(fdsPageUrl);

	await fdsSamplePage.selectTab('Delegated Filters');

	await waitForFDS({page});
}

test(
	'A data set nothing has connected to filters and offers the filter UI for what it declares',
	{
		tag: ['@LPD-96001'],
	},
	async ({fdsSamplePage, page}) => {
		await goToDelegatedFiltersTab({fdsSamplePage, page});

		await test.step('The data set offers its filters dropdown', async () => {
			await expect(
				fdsSamplePage.managementToolbar.filterButton
			).toBeVisible();
		});

		await test.step('The preloaded Color filter is resumed as a chip', async () => {
			await expect(
				fdsSamplePage.activeFiltersToolbar.container.getByRole(
					'button',
					{name: 'Color: Blue, Green, Yellow'}
				)
			).toBeVisible();
		});

		await test.step('The preloaded Color filter reaches the request', async () => {
			await expectTotalEntries({
				page,
				total: PRELOADED_COLOR_ENTRIES,
			});
		});
	}
);

test.describe('with a client extension that owns the filtering', () => {
	test.beforeEach(async ({page, pageEditorPage}) => {
		await test.step('Add the Custom Element 8 widget to the page', async () => {
			await page.goto(`${fdsPageUrl}?p_l_mode=edit`);

			await pageEditorPage.addWidget(
				'Client Extensions',
				'Liferay Sample Custom Element 8'
			);

			await pageEditorPage.publishPage();
		});
	});

	test(
		'Taking the filtering over replaces the filter UI of the data set',
		{
			tag: ['@LPD-96001'],
		},
		async ({fdsSamplePage, page}) => {
			await goToDelegatedFiltersTab({fdsSamplePage, page});

			await test.step('The client extension becomes ready and offers a panel per declared filter', async () => {
				for (const name of ['Color', 'Size', 'Status']) {
					await expect(
						customElement.getByRole('button', {name})
					).toBeEnabled();
				}
			});

			await test.step('The data set no longer offers its filters dropdown', async () => {
				await expect(
					fdsSamplePage.managementToolbar.filterButton
				).toBeHidden();
			});

			await test.step('The data set no longer resumes the preloaded Color filter as a chip', async () => {
				await expect(
					fdsSamplePage.activeFiltersToolbar.container.getByRole(
						'button',
						{name: 'Color: Blue, Green, Yellow'}
					)
				).toBeHidden();
			});

			await test.step('The filters the data set declares no longer reach the request', async () => {
				await expectTotalEntries({page, total: TOTAL_ENTRIES});
			});
		}
	);

	test(
		'Removing the client extension hands the filter UI back to the data set',
		{
			tag: ['@LPD-96001'],
		},
		async ({fdsSamplePage, page, pageEditorPage}) => {
			await goToDelegatedFiltersTab({fdsSamplePage, page});

			await test.step('The client extension owns the filtering to begin with', async () => {
				await expect(
					customElement.getByRole('button', {name: 'Color'})
				).toBeEnabled();

				await expect(
					fdsSamplePage.managementToolbar.filterButton
				).toBeHidden();
			});

			await test.step('Remove the Custom Element 8 widget from the page', async () => {
				await page.goto(`${fdsPageUrl}?p_l_mode=edit`);

				const customElementId = await pageEditorPage.getFragmentId(
					'Liferay Sample Custom Element 8'
				);

				await pageEditorPage.removeFragment(customElementId);

				await pageEditorPage.publishPage();
			});

			await goToDelegatedFiltersTab({fdsSamplePage, page});

			await test.step('The data set offers its filters dropdown again', async () => {
				await expect(
					fdsSamplePage.managementToolbar.filterButton
				).toBeVisible();
			});

			await test.step('The data set resumes the preloaded Color filter as a chip again', async () => {
				await expect(
					fdsSamplePage.activeFiltersToolbar.container.getByRole(
						'button',
						{name: 'Color: Blue, Green, Yellow'}
					)
				).toBeVisible();
			});

			await test.step('The filters the data set declares reach the request again', async () => {
				await expectTotalEntries({
					page,
					total: PRELOADED_COLOR_ENTRIES,
				});
			});
		}
	);

	test(
		'The filters of the client extension reach the data set',
		{
			tag: ['@LPD-96001'],
		},
		async ({fdsSamplePage, page}) => {
			await goToDelegatedFiltersTab({fdsSamplePage, page});

			await expect(
				customElement.getByRole('button', {name: 'Color'})
			).toBeEnabled();

			await test.step('Picking one option of a multiple selection filter narrows the data set', async () => {
				await getFilterOptionCheckbox({name: 'Blue'}).check();

				await expectTotalEntries({page, total: 25});

				await expect(
					getEntry({fdsSamplePage, position: 1})
				).toBeHidden();
			});

			await test.step('Picking a second option of the same filter widens the selection', async () => {
				await getFilterOptionCheckbox({name: 'Green'}).check();

				await expectTotalEntries({page, total: 50});

				await expect(
					getEntry({fdsSamplePage, position: 1})
				).toBeVisible();
			});

			await test.step('The chip resumes both options', async () => {
				await expect(
					customElement.getByText('Color: Blue, Green')
				).toBeVisible();
			});

			await test.step('Clearing one filter hands its entries back', async () => {
				await customElement
					.getByRole('button', {name: 'Remove the Color filter'})
					.click();

				await expectTotalEntries({page, total: TOTAL_ENTRIES});
			});

			await test.step('A single selection filter replaces what is picked rather than adding to it', async () => {
				await expandFilterPanel({name: 'Size'});

				await getFilterOptionCheckbox({name: 'Large'}).check();

				await expectTotalEntries({page, total: 17});

				await getFilterOptionCheckbox({name: 'Medium'}).check();

				// Adding to the selection rather than replacing it would leave
				// the entries of both sizes in play, which is twice as many.

				await expectTotalEntries({page, total: 17});

				await expect(
					customElement.getByText('Size: Medium')
				).toBeVisible();
			});

			await test.step('Two filters narrow the data set together', async () => {
				await getFilterOptionCheckbox({name: 'Blue'}).check();

				// The two expressions are joined with "and", so what is left is
				// the entries that are both Blue and Medium, such as entry 8.

				await expectTotalEntries({page, total: 8});

				await expect(
					getEntry({fdsSamplePage, position: 8})
				).toBeVisible();
			});

			await test.step('Clearing every filter hands the whole data set back', async () => {
				await customElement
					.getByRole('button', {name: 'Clear all'})
					.click();

				await expectTotalEntries({page, total: TOTAL_ENTRIES});
			});

			await test.step('A filter over a collection of integers reaches the request', async () => {
				await expandFilterPanel({name: 'Status'});

				await getFilterOptionCheckbox({name: 'Draft'}).check();

				await waitForFDS({empty: true, page});

				await getFilterOptionCheckbox({name: 'Draft'}).uncheck();

				await getFilterOptionCheckbox({name: 'Approved'}).check();

				await expectTotalEntries({page, total: TOTAL_ENTRIES});
			});

			await test.step('A raw OData expression filters the data set', async () => {
				await customElement
					.getByRole('button', {name: 'Filter manually'})
					.click();

				await customElement
					.getByLabel('OData filter expression')
					.fill("title eq 'Sample5'");

				await customElement
					.getByRole('button', {name: 'Apply'})
					.click();

				await expectTotalEntries({page, total: 1});

				await expect(
					getEntry({fdsSamplePage, position: 5})
				).toBeVisible();
			});

			await test.step('Swapping back to the options clears what the expression applied', async () => {
				await customElement
					.getByRole('button', {name: 'Choose from the options'})
					.click();

				await expectTotalEntries({page, total: TOTAL_ENTRIES});
			});
		}
	);
});

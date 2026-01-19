/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {frontendSPAInfrastructureConfigurationTest} from '../../../../../fixtures/frontendSPAInfrastructureConfigurationTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {EFDSVisualizationMode, waitForFDS} from '../../../../../utils/waitFor';
import {fdsSamplePageTest} from '../../fixtures/fdsSamplePageTest';
import {FDSSamplePage} from '../../pages/FDSSamplePage';
import JsonURL from '@jsonurl/jsonurl';

interface IConfigInURL {
	delta: number;
	filters: any[];
	page: number;
	q: string;
	sorts: any[];
	vf: any;
	view: string;
}

const test = mergeTests(
	apiHelpersTest,
	fdsSamplePageTest,
	frontendSPAInfrastructureConfigurationTest,
	featureFlagsTest({
		'LPD-22473': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

const getConfigFromURL = (
	url: string,
	fdsId: string
): Partial<IConfigInURL> => {
	const params = new URLSearchParams(url);

	const configParam = params.get(
		`com_liferay_frontend_data_set_sample_web_internal_portlet_FDSSamplePortlet-${fdsId}_fdsConfig`
	);

	if (!configParam) {
		return null;
	}

	let config = {};

	try {
		config = JsonURL.parse(configParam);
	}
	catch (error) {
		return null;
	}

	return config;
};

const assertDelta = async (
	delta: number,
	fdsId: string,
	fdsSamplePage: FDSSamplePage,
	page: Page
) => {
	await expect(() => {
		const config = getConfigFromURL(new URL(page.url()).search, fdsId);

		expect(config.delta).toBe(delta);
	}).toPass();

	await expect(fdsSamplePage.paginator.itemsPerPageSelector).toHaveText(
		`${delta} Items`
	);
};

const assertNoActiveFiltersInURL = async (fdsId: string, page: Page) => {
	await waitForFDS({page, visualizationMode: EFDSVisualizationMode.TABLE});

	await expect(() => {
		const config = getConfigFromURL(new URL(page.url()).search, fdsId);
		expect(config.filters.length).toBe(0);
	}).toPass();
};

const assertActiveFiltersInURL = async (
	active: boolean,
	ids: string[],
	fdsId: string,
	page: Page
) => {
	await waitForFDS({page, visualizationMode: EFDSVisualizationMode.TABLE});

	await expect(() => {
		const config = getConfigFromURL(new URL(page.url()).search, fdsId);

		for (const id of ids) {
			expect(config.filters.some((filter: any) => filter.id === id)).toBe(
				active
			);
		}
	}).toPass();
};

const assertActiveFilter = async (
	active: boolean,
	id: string,
	fdsId: string,
	filterResumeExpectedText: string,
	page: Page
) => {
	await assertActiveFiltersInURL(active, [id], fdsId, page);

	const filterResumeLocator = page.getByRole('button', {
		name: filterResumeExpectedText,
	});

	if (active) {
		await expect(filterResumeLocator).toBeVisible();
	}
	else {
		await expect(filterResumeLocator).not.toBeVisible();
	}
};

const activateFilter = async (
	checkItems: string[],
	filterLabel: string,
	fdsSamplePage: FDSSamplePage,
	page: Page
) => {
	await fdsSamplePage.managementToolbar.container
		.getByRole('button', {name: 'Filter'})
		.click();

	const backButton = page.getByRole('button', {name: 'Back'});

	if (await backButton.isVisible()) {
		await backButton.click();
	}

	await page
		.locator('.dropdown-menu')
		.getByRole('menuitem', {name: filterLabel})
		.click();

	for (const item of checkItems) {
		await page
			.locator('.dropdown-menu')
			.getByRole('checkbox', {name: item})
			.check();
	}

	await page
		.locator('.dropdown-menu')
		.getByRole('button', {name: 'Add Filter'})
		.click();
};

const removeFilter = async (filterResumeInitialText: string, page: Page) => {
	const filterResumeLocator = page.getByRole('button', {
		name: filterResumeInitialText,
	});

	await page
		.getByRole('group')
		.filter({has: filterResumeLocator})
		.getByRole('button', {name: 'Remove Filter'})
		.click();
};

const changeFilterSelections = async (
	checkItems: string[],
	filterResumeInitialText: string,
	page: Page,
	uncheckItems: string[]
) => {
	await page.getByRole('button', {name: filterResumeInitialText}).click();

	for (const item of checkItems) {
		await page.getByRole('checkbox', {name: item}).check();
	}

	for (const item of uncheckItems) {
		await page.getByRole('checkbox', {name: item}).uncheck();
	}

	await page.getByRole('button', {name: 'Show Results'}).click();

	await waitForFDS({page});
};

const assertView = async (
	fdsId: string,
	page: Page,
	visualizationMode: EFDSVisualizationMode,
	viewName?: string
) => {
	await waitForFDS({page, visualizationMode});

	await expect(() => {
		const config = getConfigFromURL(new URL(page.url()).search, fdsId);

		expect(config.view).toBe(viewName ? viewName : visualizationMode);
	}).toPass();
};

const changeDelta = async (
	delta: number,
	fdsId: string,
	fdsSamplePage: FDSSamplePage,
	page: Page
) => {
	await fdsSamplePage.changeItemsPerPage({delta: `${delta} Items`});

	await waitForFDS({page, visualizationMode: EFDSVisualizationMode.TABLE});

	await assertDelta(delta, fdsId, fdsSamplePage, page);
};

const spaConfigurations = [
	{
		configure: async ({frontendSPAInfrastructureConfigurationPage}) => {
			await frontendSPAInfrastructureConfigurationPage.goto();
			await frontendSPAInfrastructureConfigurationPage.disableSPA();
		},
		name: 'SPA is disabled',
	},
	{
		configure: async ({frontendSPAInfrastructureConfigurationPage}) => {
			await frontendSPAInfrastructureConfigurationPage.goto();
			await frontendSPAInfrastructureConfigurationPage.enableSPA();
		},
		name: 'SPA is enabled',
	},
];

for (const spaConfiguration of spaConfigurations) {
	test.describe(`Config in URL, ${spaConfiguration.name}`, () => {
		test.beforeEach(
			async ({
				fdsSamplePage,
				frontendSPAInfrastructureConfigurationPage,
				page,
				site,
			}) => {
				await test.step('Configure SPA', async () => {
					await spaConfiguration.configure({
						frontendSPAInfrastructureConfigurationPage,
					});
				});

				await fdsSamplePage.setupFDSSampleWidget({site});

				await fdsSamplePage.selectTab('Advanced');

				await waitForFDS({
					page,
					visualizationMode: EFDSVisualizationMode.TABLE,
				});
			}
		);

		test(
			'push history, delta',
			{tag: '@LPD-20947'},
			async ({fdsSamplePage, page}) => {
				const setDelta = (delta) =>
					changeDelta(delta, 'advanced', fdsSamplePage, page);

				const checkDelta = (delta) =>
					assertDelta(delta, 'advanced', fdsSamplePage, page);

				await test.step('Change delta via UI several times', async () => {
					await setDelta(40);
					await setDelta(60);
					await setDelta(20);
				});

				await test.step('Check back navigation', async () => {
					await page.goBack();
					await checkDelta(60);
					await page.goBack();
					await checkDelta(40);

					// initial delta is 20

					await page.goBack();
					await checkDelta(20);
				});

				await test.step('Check forward navigation', async () => {
					await page.goForward();
					await checkDelta(40);
					await page.goForward();
					await checkDelta(60);
					await page.goForward();
					await checkDelta(20);
				});

				await test.step('Mix navigation and change via UI', async () => {
					await page.goBack();
					await checkDelta(60);

					await setDelta(40); // last delta value (20) is removed from history

					await page.goBack();
					await checkDelta(60);
					await page.goForward();
					await checkDelta(40);
					expect(await page.goForward()).toBeNull();
				});
			}
		);

		test(
			'push history, view name',
			{tag: '@LPD-20947'},
			async ({fdsSamplePage, page}) => {
				const checkView = (visualizationMode: EFDSVisualizationMode) =>
					assertView(
						'advanced',
						page,
						visualizationMode,
						visualizationMode === EFDSVisualizationMode.TABLE
							? 'customizedTable'
							: undefined
					);

				await test.step('Change view name via UI several times', async () => {
					await fdsSamplePage.changeVisualizationMode({
						page,
						visualizationMode: EFDSVisualizationMode.CARDS,
					});
					await checkView(EFDSVisualizationMode.CARDS);
					await fdsSamplePage.changeVisualizationMode({
						page,
						visualizationMode: EFDSVisualizationMode.LIST,
					});
					await checkView(EFDSVisualizationMode.LIST);
					await fdsSamplePage.changeVisualizationMode({
						page,
						visualizationMode: EFDSVisualizationMode.TABLE,
					});
					await checkView(EFDSVisualizationMode.TABLE);
				});

				await test.step('Check back navigation', async () => {
					await page.goBack();
					await checkView(EFDSVisualizationMode.LIST);
					await page.goBack();
					await checkView(EFDSVisualizationMode.CARDS);

					// initial view is table

					await page.goBack();
					await checkView(EFDSVisualizationMode.TABLE);
				});

				await test.step('Check forward navigation', async () => {
					await page.goForward();
					await checkView(EFDSVisualizationMode.CARDS);
					await page.goForward();
					await checkView(EFDSVisualizationMode.LIST);
					await page.goForward();
					await checkView(EFDSVisualizationMode.TABLE);
				});

				await test.step('Mix navigation and change via UI', async () => {
					await page.goBack();
					await checkView(EFDSVisualizationMode.LIST);
					await fdsSamplePage.changeVisualizationMode({
						page,
						visualizationMode: EFDSVisualizationMode.CARDS,
					});

					// last view value (table) is removed from history

					await page.goBack();
					await checkView(EFDSVisualizationMode.LIST);
					await page.goForward();
					await checkView(EFDSVisualizationMode.CARDS);
					expect(await page.goForward()).toBeNull();
				});
			}
		);

		test(
			'push history, filters',
			{tag: '@LPD-20947'},
			async ({fdsSamplePage, page}) => {
				const checkFilter = (
					active: boolean,
					id: string,
					filterResumeText: string
				) =>
					assertActiveFilter(
						active,
						id,
						'advanced',
						filterResumeText,
						page
					);

				const checkFiltersInURL = (active: boolean, ids: string[]) =>
					assertActiveFiltersInURL(active, ids, 'advanced', page);

				await test.step('Check color filter is pre-applied, and no other else', async () => {
					await checkFilter(
						true,
						'color',
						'Color: Blue, Green, Yellow'
					);

					await checkFiltersInURL(false, [
						'date',
						'size',
						'status',
						'title',
					]);
				});

				await test.step('Deactivate color filter', async () => {
					await removeFilter('Color: Blue, Green, Yellow', page);
					await checkFilter(
						false,
						'color',
						'Color: Blue, Green, Yellow'
					);
					await assertNoActiveFiltersInURL('advanced', page);
				});

				await test.step('Change filter name via UI several times', async () => {
					await activateFilter(
						['Blue', 'Yellow'],
						'Color',
						fdsSamplePage,
						page
					);

					await checkFilter(true, 'color', 'Color: Blue, Yellow');

					await changeFilterSelections(
						['Green'],
						'Color: Blue, Yellow',
						page,
						['Blue']
					);

					await checkFilter(true, 'color', 'Color: Yellow, Green');

					await changeFilterSelections(
						['Red'],
						'Color: Yellow, Green',
						page,
						['Yellow']
					);

					await checkFilter(true, 'color', 'Color: Green, Red');
				});

				await test.step('Check back navigation', async () => {
					await page.goBack();
					await waitForFDS({page});
					await checkFilter(true, 'color', 'Color: Yellow, Green');

					await page.goBack();
					await waitForFDS({page});
					await checkFilter(true, 'color', 'Color: Blue, Yellow');

					await page.goBack();
					await waitForFDS({page});
					await checkFilter(
						false,
						'color',
						'Color: Blue, Green, Yellow'
					);

					// initial, pre-applied filter

					await page.goBack();
					await waitForFDS({page});
					await checkFilter(
						true,
						'color',
						'Color: Blue, Green, Yellow'
					);
				});

				await test.step('Check forward navigation', async () => {
					await page.goForward();
					await waitForFDS({page});
					await checkFilter(
						false,
						'color',
						'Color: Blue, Green, Yellow'
					);

					await page.goForward();
					await waitForFDS({page});
					await checkFilter(true, 'color', 'Color: Blue, Yellow');

					await page.goForward();
					await waitForFDS({page});
					await checkFilter(true, 'color', 'Color: Yellow, Green');

					await page.goForward();
					await waitForFDS({page});
					await checkFilter(true, 'color', 'Color: Green, Red');
				});

				await test.step('Mix navigation and change via UI', async () => {
					await page.goBack();
					await waitForFDS({page});
					await checkFilter(true, 'color', 'Color: Yellow, Green');

					await changeFilterSelections(
						['Blue'],
						'Color: Yellow, Green',
						page,
						['Yellow', 'Green']
					);

					// last view value (green, red) is removed from history

					await page.goBack();
					await waitForFDS({page});
					await checkFilter(true, 'color', 'Color: Yellow, Green');

					await page.goForward();
					await waitForFDS({page});
					await checkFilter(true, 'color', 'Color: Blue');

					await checkFiltersInURL(false, [
						'date',
						'size',
						'status',
						'title',
					]);

					expect(await page.goForward()).toBeNull();
				});

				await test.step('Operating with several filters', async () => {
					await checkFilter(true, 'color', 'Color: Blue');

					await activateFilter(
						['Approved', 'Draft'],
						'Status',
						fdsSamplePage,
						page
					);
					await checkFilter(true, 'color', 'Color: Blue');
					await checkFilter(
						true,
						'status',
						'Status: Approved, Draft'
					);
					await checkFiltersInURL(false, ['date', 'size', 'title']);

					await removeFilter('Color: Blue', page);
					await checkFilter(
						true,
						'status',
						'Status: Approved, Draft'
					);
					await checkFiltersInURL(false, [
						'color',
						'date',
						'size',
						'title',
					]);

					await removeFilter('Status: Approved, Draft', page);
					await assertNoActiveFiltersInURL('advanced', page);
				});
			}
		);

		test(
			'replace history',
			{tag: '@LPD-20947'},
			async ({fdsSamplePage, page}) => {
				const checkDelta = (delta) =>
					assertDelta(
						delta,
						'customInternalView',
						fdsSamplePage,
						page
					);

				const checkView = (visualizationMode: EFDSVisualizationMode) =>
					assertView('customInternalView', page, visualizationMode);

				const setDelta = (delta) =>
					changeDelta(
						delta,
						'customInternalView',
						fdsSamplePage,
						page
					);

				await test.step('Change to custom view component tab', async () => {
					await fdsSamplePage.selectTab('Custom Internal View');
					await page.locator('.fds-carousel-view-sample').waitFor();
				});

				await test.step('Make several state changes via UI', async () => {
					await fdsSamplePage.changeVisualizationMode({
						page,
						visualizationMode: EFDSVisualizationMode.TABLE,
					});
					await checkView(EFDSVisualizationMode.TABLE);

					await setDelta(40);
				});

				await test.step('Check back navigation', async () => {
					await page.goBack();

					// we are now in the advanced sample because we are not stacking state changes onto URL history

					await assertView(
						'advanced',
						page,
						EFDSVisualizationMode.TABLE,
						'customizedTable'
					);
					await assertDelta(20, 'advanced', fdsSamplePage, page);
					await assertActiveFilter(
						true,
						'color',
						'advanced',
						'Color: Blue, Green, Yellow',
						page
					);
				});

				await test.step('Check forward navigation', async () => {
					await page.goForward();
					await checkView(EFDSVisualizationMode.TABLE);
					await checkDelta(40);
					await assertNoActiveFiltersInURL(
						'customInternalView',
						page
					);
				});
			}
		);

		test(
			'push history, page number',
			{tag: '@LPD-20947'},
			async ({page}) => {
				const assertPageNumber = async (
					pageNumber: number,
					fdsId: string,
					page: Page
				) => {
					const pageSelector = page.getByLabel(
						`Go to page, ${pageNumber}`
					);

					await page
						.locator('li.page-item.active', {has: pageSelector})
						.waitFor({state: 'visible'});

					await expect(() => {
						const config = getConfigFromURL(
							new URL(page.url()).search,
							fdsId
						);

						expect(config.page).toBe(pageNumber);
					}).toPass();
				};

				const changePageNumber = async (
					pageNumber: number,
					fdsId: string,
					page: Page
				) => {
					await page.getByLabel(`Go to page, ${pageNumber}`).click();

					await assertPageNumber(pageNumber, fdsId, page);
				};

				const setPageNumber = (pageNumber: number) =>
					changePageNumber(pageNumber, 'advanced', page);

				const checkPageNumber = (pageNumber: number) =>
					assertPageNumber(pageNumber, 'advanced', page);

				await test.step('Change page number via UI several times', async () => {
					await setPageNumber(2);
					await setPageNumber(3);
					await setPageNumber(1);
				});

				await test.step('Check back navigation', async () => {
					await page.goBack();
					await checkPageNumber(3);

					await page.goBack();
					await checkPageNumber(2);

					await page.goBack();
					await checkPageNumber(1);
				});

				await test.step('Check forward navigation', async () => {
					await page.goForward();
					await checkPageNumber(2);

					await page.goForward();
					await checkPageNumber(3);

					await page.goForward();
					await checkPageNumber(1);
				});

				await test.step('Mix navigation and change via UI', async () => {
					await page.goBack();
					await checkPageNumber(3);

					await setPageNumber(2);

					await page.goBack();
					await checkPageNumber(3);

					await page.goForward();
					await checkPageNumber(2);

					expect(await page.goForward()).toBeNull();
				});
			}
		);

		test('push history, sorting', {tag: '@LPD-20947'}, async ({page}) => {
			const assertSort = async (
				fieldName: string,
				direction: 'asc' | 'desc'
			) => {
				await waitForFDS({
					page,
					visualizationMode: EFDSVisualizationMode.TABLE,
				});

				await expect(() => {
					const config = getConfigFromURL(
						new URL(page.url()).search,
						'advanced'
					);

					expect(config.sorts).toBeDefined();
					expect(config.sorts).toHaveLength(1);
					expect(config.sorts[0].key).toBe(fieldName);
					expect(config.sorts[0].direction).toBe(direction);
				}).toPass();

				await waitForFDS({
					page,
					visualizationMode: EFDSVisualizationMode.TABLE,
				});
			};

			const idColumnHeader = page.getByRole('columnheader').nth(1);

			const titleColumnHeader = page.getByRole('columnheader').nth(2);

			const idSortButton = idColumnHeader.getByRole('button');

			const titleSortButton = titleColumnHeader.getByRole('button');

			await test.step('Click on Sortable Content button', async () => {
				await titleSortButton.click();

				await assertSort('title', 'desc');
			});

			await test.step('Check sorting in the UI', async () => {
				const tableBodyRows = await page
					.locator('.table tbody tr')
					.all();

				const textsFromSecondColumn = await Promise.all(
					tableBodyRows.map((row) =>
						row.locator('td').nth(2).innerText()
					)
				);

				textsFromSecondColumn.forEach((text, index) => {
					if (index < textsFromSecondColumn.length - 1) {
						expect(
							text > textsFromSecondColumn[index + 1]
						).toBeTruthy();
					}
				});
			});

			await test.step('Change sort several times', async () => {
				await idSortButton.click();
				await assertSort('id', 'asc');
				await idSortButton.click();
				await assertSort('id', 'desc');
				await titleSortButton.click();
				await assertSort('title', 'asc');
				await titleSortButton.click();
				await assertSort('title', 'desc');
			});

			await test.step('Check back navigation', async () => {
				await page.goBack();

				await assertSort('title', 'asc');
				await page.goBack();

				await assertSort('id', 'desc');

				await page.goBack();

				await assertSort('id', 'asc');
			});

			await test.step('Check forward navigation', async () => {
				await page.goForward();

				await assertSort('id', 'desc');
				await page.goForward();

				await assertSort('title', 'asc');
				await page.goForward();

				await assertSort('title', 'desc');
			});

			await test.step('Mix navigation and change via UI', async () => {
				await page.goBack();

				await assertSort('title', 'asc');

				await idSortButton.click();

				await assertSort('id', 'asc');

				await page.goBack();

				await assertSort('title', 'asc');

				await page.goForward();

				await assertSort('id', 'asc');

				expect(await page.goForward()).toBeNull();
			});
		});

		test(
			'push history, search parameter',
			{tag: '@LPD-20947'},
			async ({fdsSamplePage, page}) => {
				const assertSearchParam = async (
					searchParam: string,
					fdsId: string,
					page: Page
				) => {
					await expect(() => {
						const config = getConfigFromURL(
							new URL(page.url()).search,
							fdsId
						);

						if (searchParam) {
							expect(config.q).toBe(searchParam);
						}
						else {
							expect(config.q).toBeUndefined();
						}
					}).toPass();

					await expect(
						fdsSamplePage.managementToolbar.searchInput
					).toHaveValue(searchParam);
				};

				const changeSearchParam = async (
					searchParam: string,
					fdsId: string,
					fdsSamplePage: FDSSamplePage,
					page: Page
				) => {
					await fdsSamplePage.managementToolbar.searchInput.fill(
						searchParam
					);
					await fdsSamplePage.managementToolbar.searchButton.click();

					await assertSearchParam(searchParam, fdsId, page);
				};

				const setSearchParam = (searchParam: string) =>
					changeSearchParam(
						searchParam,
						'advanced',
						fdsSamplePage,
						page
					);

				const checkSearchParam = (searchParam: string) =>
					assertSearchParam(searchParam, 'advanced', page);

				const clearSearchParam = async () => {
					await fdsSamplePage.activeFiltersToolbar.clearSearchButton.click();

					await assertSearchParam('', 'advanced', page);
				};
				await test.step('Change search parameter via UI several times', async () => {
					await setSearchParam('test1');
					await waitForFDS({empty: true, page});

					await clearSearchParam();
					await waitForFDS({page});

					await setSearchParam('test3');
					await waitForFDS({empty: true, page});
				});

				await test.step('Check back navigation', async () => {
					await page.goBack();
					await waitForFDS({page});
					await checkSearchParam('');

					await page.goBack();
					await waitForFDS({empty: true, page});
					await checkSearchParam('test1');

					await page.goBack();
					await waitForFDS({page});
					await checkSearchParam('');
				});

				await test.step('Check forward navigation', async () => {
					await page.goForward();
					await waitForFDS({empty: true, page});
					await checkSearchParam('test1');

					await page.goForward();
					await waitForFDS({page});
					await checkSearchParam('');

					await page.goForward();
					await waitForFDS({empty: true, page});
					await checkSearchParam('test3');
				});

				await test.step('Mix navigation and change via UI', async () => {
					await page.goBack();
					await waitForFDS({page});
					await checkSearchParam('');

					await setSearchParam('test4');
					await waitForFDS({empty: true, page});

					await page.goBack();
					await waitForFDS({page});
					await checkSearchParam('');

					await page.goForward();
					await waitForFDS({empty: true, page});
					await checkSearchParam('test4');

					expect(await page.goForward()).toBeNull();
				});
			}
		);

		test(
			'config in URL is turned off by default',
			{tag: '@LPD-20947'},
			async ({fdsSamplePage, page}) => {
				await test.step('Change to single selection tab', async () => {
					await fdsSamplePage.selectTab('Single Selection');
					await waitForFDS({
						page,
						visualizationMode: EFDSVisualizationMode.TABLE,
					});
				});

				await test.step('Assert there is no config in URL', async () => {
					expect(
						getConfigFromURL(
							new URL(page.url()).search,
							'singleSelection'
						)
					).toBeNull();
				});
			}
		);

		test(
			'push history, visible fields',
			{tag: '@LPD-20947'},
			async ({fdsSamplePage, page}) => {
				const tableFields = {
					Author: 'creator,name',
					ID: 'id',
					Title: 'title',
				};

				const assertAllFieldsAreVisible = async () => {
					await expect(fdsSamplePage.table.headerCells).toHaveCount(
						10
					);

					await expect(() => {
						const config = getConfigFromURL(
							new URL(page.url()).search,
							'advanced'
						);

						expect(config.vf).toBeDefined();
						expect(
							Object.keys(config.vf).reduce(
								(acc: boolean, key: string) =>
									acc && config.vf[key],
								true
							)
						).toBeTruthy();
					}).toPass();
				};

				const assertFieldVisibility = async (
					fieldName: string,
					expectedNumberOfVisibleColumns: number,
					visible: boolean
				) => {
					const tableColumnHeaderName =
						fdsSamplePage.table.headerCells.filter({
							hasText: fieldName,
						});

					if (visible) {
						await expect(tableColumnHeaderName).toBeVisible();
					}
					else {
						await expect(tableColumnHeaderName).not.toBeVisible();
					}

					await expect(fdsSamplePage.table.headerCells).toHaveCount(
						expectedNumberOfVisibleColumns
					);

					await expect(() => {
						const config = getConfigFromURL(
							new URL(page.url()).search,
							'advanced'
						);
						expect(config.vf).toBeDefined();
						expect(config.vf[tableFields[fieldName]]).toBe(visible);
					}).toPass();
				};

				await test.step('Update visible fields via UI', async () => {
					await expect(
						fdsSamplePage.table.manageColumnsVisibilityButton
					).toBeAttached();

					await fdsSamplePage.table.manageColumnsVisibilityButton.click();

					const menuItem = page.getByRole('menuitem').nth(0);

					await menuItem.click();

					await assertFieldVisibility('ID', 9, false);

					await page
						.getByRole('menu')
						.getByRole('menuitem', {name: 'author'})
						.click();

					await assertFieldVisibility('Author', 8, false);
				});

				await test.step('Check back navigation', async () => {
					await page.goBack();

					await assertFieldVisibility('Author', 9, true);

					await page.goBack();

					await assertAllFieldsAreVisible();
				});

				await test.step('Check forward navigation', async () => {
					await page.goForward();

					await assertFieldVisibility('ID', 9, false);
					await assertFieldVisibility('Author', 9, true);

					await page.goForward();

					await assertFieldVisibility('ID', 8, false);
					await assertFieldVisibility('Author', 8, false);
				});

				await test.step('Mix navigation and change via UI', async () => {
					await page.goBack();

					await assertFieldVisibility('ID', 9, false);
					await assertFieldVisibility('Author', 9, true);

					await fdsSamplePage.table.manageColumnsVisibilityButton.click();
					await page.getByRole('menu').waitFor();

					const titleMenuItem = page
						.getByRole('menu')
						.getByRole('menuitem', {name: 'title'});

					await titleMenuItem.click();

					await assertFieldVisibility('Author', 8, true);
					await assertFieldVisibility('Title', 8, false);

					await page.goBack();

					await assertFieldVisibility('Author', 9, true);
					await assertFieldVisibility('Title', 9, true);

					await page.goForward();

					await assertFieldVisibility('Author', 8, true);
					await assertFieldVisibility('Title', 8, false);

					expect(await page.goForward()).toBeNull();
				});
			}
		);
	});
}

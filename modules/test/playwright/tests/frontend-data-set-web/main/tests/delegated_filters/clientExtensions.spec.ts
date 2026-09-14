/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {frontendSPAInfrastructureConfigurationTest} from '../../../../../fixtures/frontendSPAInfrastructureConfigurationTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../../fixtures/pageEditorPagesTest';
import getRandomString from '../../../../../utils/getRandomString';
import {waitForFDS} from '../../../../../utils/waitFor';
import {waitForSPAToBeLoaded} from '../../../../../utils/waitForSPAToBeLoaded';
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

// How long a data set whose address carries state a consumer left waits
// before giving up on the connection that was going to take it, matching
// RESTORE_TIMEOUT in useRestoredConnectionState.

const RESTORE_GIVE_UP = 10000;

const test = mergeTests(
	apiHelpersTest,
	fdsSamplePageTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	frontendSPAInfrastructureConfigurationTest,
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
);

let customElement: Locator;
let customElements: Locator;
let fdsPageUrl: string;

// Whether the browser loads a page or Liferay swaps it in underneath is not
// something this contract should be able to tell apart, and it is the sort of
// difference that only shows up in a test that navigates. Every test below
// therefore runs twice, once each way.
//
// The connection keeps who owns the filtering of a data set in the module
// itself, which a single page application navigation does not reload, so the
// two runs genuinely differ: with the SPA on, what one test leaves behind is
// still there for the next navigation to trip over.

const spaConfigurations = [
	{
		configure: async ({frontendSPAInfrastructureConfigurationPage}) => {
			await frontendSPAInfrastructureConfigurationPage.goto();
			await frontendSPAInfrastructureConfigurationPage.disableSPA();
		},
		name: 'SPA is disabled',
		spa: false,
	},
	{
		configure: async ({frontendSPAInfrastructureConfigurationPage}) => {
			await frontendSPAInfrastructureConfigurationPage.goto();
			await frontendSPAInfrastructureConfigurationPage.enableSPA();
		},
		name: 'SPA is enabled',
		spa: true,
	},
];

for (const spaConfiguration of spaConfigurations) {
	test.describe(`Delegated filters, ${spaConfiguration.name}`, () => {
		test.beforeEach(
			async ({frontendSPAInfrastructureConfigurationPage}) => {
				await test.step('Configure SPA', async () => {
					await spaConfiguration.configure({
						frontendSPAInfrastructureConfigurationPage,
					});
				});
			}
		);

		test.beforeEach(async ({fdsSamplePage, page, site}) => {
			const {url} = await fdsSamplePage.setupFDSSampleWidget({site});

			fdsPageUrl = url;

			customElements = page.locator('liferay-sample-custom-element-8');

			customElement = customElements.first();
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

		async function expectTotalEntries({
			page,
			total,
		}: {
			page: Page;
			total: number;
		}) {
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

		// The button that opens a filter panel is named for the filter, and
		// takes a count once anything in it is selected: "Color" becomes
		// "Color (1)". The button that removes the chip is named for the
		// filter as well, as "Remove the Color filter". So a bare name matches
		// two buttons as soon as the filter is applied, and an exact one
		// matches neither.

		function getFilterPanelButton({name}: {name: string}) {
			return customElement.getByRole('button', {
				name: new RegExp(`^${name}( \\(\\d+\\))?$`),
			});
		}

		async function expandFilterPanel({name}: {name: string}) {
			const panelButton = getFilterPanelButton({name});

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
								getFilterPanelButton({name})
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
							getFilterPanelButton({name: 'Color'})
						).toBeEnabled();

						await expect(
							fdsSamplePage.managementToolbar.filterButton
						).toBeHidden();
					});

					await test.step('Remove the Custom Element 8 widget from the page', async () => {
						await page.goto(`${fdsPageUrl}?p_l_mode=edit`);

						const customElementId =
							await pageEditorPage.getFragmentId(
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
						getFilterPanelButton({name: 'Color'})
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
							.getByRole('button', {
								name: 'Remove the Color filter',
							})
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

						await getFilterOptionCheckbox({
							name: 'Draft',
						}).uncheck();

						await getFilterOptionCheckbox({
							name: 'Approved',
						}).check();

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
							.getByRole('button', {
								name: 'Choose from the options',
							})
							.click();

						await expectTotalEntries({page, total: TOTAL_ENTRIES});
					});
				}
			);

			test(
				'The filters of the client extension are part of the address of the data set',
				{
					tag: ['@LPD-96001'],
				},
				async ({fdsSamplePage, page}) => {
					await goToDelegatedFiltersTab({fdsSamplePage, page});

					await expect(
						getFilterPanelButton({name: 'Color'})
					).toBeEnabled();

					await test.step('Filter by a color and a size', async () => {
						await getFilterOptionCheckbox({name: 'Blue'}).check();

						await expandFilterPanel({name: 'Size'});

						await getFilterOptionCheckbox({name: 'Medium'}).check();

						await expectTotalEntries({page, total: 8});
					});

					const filteredURL = page.url();

					await test.step('The filters reach the address', async () => {
						expect(filteredURL).toContain('Blue');
						expect(filteredURL).toContain('Medium');
					});

					await test.step('Reloading brings the filters, the results, and the filter UI back', async () => {
						await page.reload();

						await waitForFDS({page});

						await expectTotalEntries({page, total: 8});

						await expect(
							getFilterOptionCheckbox({name: 'Blue'})
						).toBeChecked();

						await expect(
							customElement.getByText('Color: Blue')
						).toBeVisible();

						await expect(
							customElement.getByText('Size: Medium')
						).toBeVisible();

						await expect(
							fdsSamplePage.managementToolbar.filterButton
						).toBeHidden();
					});

					await test.step('Going back returns to the filters of the previous entry', async () => {
						await page.goBack();

						await waitForFDS({page});

						// The color alone was applied first, which is a quarter of the
						// entries rather than the eight that are also Medium.

						await expectTotalEntries({page, total: 25});

						await expect(
							customElement.getByText('Color: Blue')
						).toBeVisible();

						await expect(
							customElement.getByText('Size: Medium')
						).toBeHidden();
					});

					await test.step('Opening the address afresh filters the data set the same way', async () => {
						await page.goto(filteredURL);

						await waitForFDS({page});

						await expectTotalEntries({page, total: 8});

						await expect(
							customElement.getByText('Color: Blue')
						).toBeVisible();

						await expect(
							customElement.getByText('Size: Medium')
						).toBeVisible();
					});

					await test.step('A manually typed expression is part of the address too', async () => {
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

						await page.reload();

						await waitForFDS({page});

						await expectTotalEntries({page, total: 1});

						await expect(
							getEntry({fdsSamplePage, position: 5})
						).toBeVisible();

						// Which of the two ways of filtering was in use is restored
						// along with the filter, since the expression can only be seen
						// or undone in the one that produced it.

						await expect(
							customElement.getByLabel('OData filter expression')
						).toHaveValue("title eq 'Sample5'");
					});
				}
			);

			// Going back and forward between an address that filters and one that does
			// not. What the client extension draws has to move with the address, since
			// its filter UI is put back from what the address carries rather than from
			// whatever the element last knew: an address carrying nothing has to leave
			// it showing nothing, which is the one direction applying a filter never
			// reaches.
			//
			// Both entries are visited outright before the buttons are used, so that
			// each is one entry of the history whatever the data set does to the
			// address as it filters: the first filter of a visit replaces the entry it
			// was applied on rather than adding one, which would otherwise leave the
			// unfiltered address nowhere in the history to go back to.

			test(
				'Going back and forward moves the data set between what each address filters by',
				{
					tag: ['@LPD-96001'],
				},
				async ({fdsSamplePage, page}) => {
					await goToDelegatedFiltersTab({fdsSamplePage, page});

					await expect(
						getFilterPanelButton({name: 'Color'})
					).toBeEnabled();

					const unfilteredURL = page.url();

					await test.step('Filter by a color', async () => {
						await getFilterOptionCheckbox({name: 'Blue'}).check();

						await expectTotalEntries({page, total: 25});
					});

					const filteredURL = page.url();

					expect(filteredURL).not.toEqual(unfilteredURL);

					await test.step('Visit the unfiltered address, and then the filtered one', async () => {
						await page.goto(unfilteredURL);

						await waitForFDS({page});

						await expectTotalEntries({page, total: TOTAL_ENTRIES});

						await page.goto(filteredURL);

						await waitForFDS({page});

						await expectTotalEntries({page, total: 25});

						await expect(
							customElement.getByText('Color: Blue')
						).toBeVisible();
					});

					await test.step('Going back to the address that filters nothing leaves the element showing nothing', async () => {
						await page.goBack();

						await waitForFDS({page});

						await expectTotalEntries({page, total: TOTAL_ENTRIES});

						await expect(
							customElement.getByText('Color: Blue')
						).toBeHidden();

						// The chips and the button that clears them are drawn only
						// while something is applied, so their absence is the element
						// having put its own filter UI back to nothing.

						await expect(
							customElement.getByRole('button', {
								name: 'Clear all',
							})
						).toBeHidden();
					});

					await test.step('Going forward puts the color back', async () => {
						await page.goForward();

						await waitForFDS({page});

						await expectTotalEntries({page, total: 25});

						await expect(
							customElement.getByText('Color: Blue')
						).toBeVisible();

						await expect(
							getFilterOptionCheckbox({name: 'Blue'})
						).toBeChecked();
					});
				}
			);

			// A link outlives the page it was taken from, and the widget that wrote it
			// may have been swapped for another since. What the address carries is then
			// filed under an app that is nowhere on the page, and nobody is coming for
			// it: the data set has to notice as much and get on with it, rather than
			// hold its first request until it gives up waiting.

			test(
				'A link naming an app that is not on the page leaves the data set filtering nothing',
				{
					tag: ['@LPD-96001'],
				},
				async ({fdsSamplePage, page}) => {
					await goToDelegatedFiltersTab({fdsSamplePage, page});

					await expect(
						getFilterPanelButton({name: 'Color'})
					).toBeEnabled();

					await getFilterOptionCheckbox({name: 'Blue'}).check();

					await expectTotalEntries({page, total: 25});

					// The same address, with what it carries filed under an app that
					// never connects. The element on the page answers to
					// "sampleCustomElement8", so it is offered nothing of its own.

					const foreignURL = page
						.url()
						.replace(
							'sampleCustomElement8',
							'sampleCustomElement9'
						);

					expect(foreignURL).not.toEqual(page.url());

					await page.goto(foreignURL);

					await waitForFDS({page});

					await test.step('The data set shows every entry', async () => {
						await expectTotalEntries({page, total: TOTAL_ENTRIES});
					});

					await test.step('The element draws no filters it was never handed', async () => {
						await expect(
							customElement.getByText('Color: Blue')
						).toBeHidden();

						await expect(
							customElement.getByRole('button', {
								name: 'Clear all',
							})
						).toBeHidden();
					});

					await test.step('The element owns the filtering all the same, and filters as it did before', async () => {
						await getFilterOptionCheckbox({name: 'Blue'}).check();

						await expectTotalEntries({page, total: 25});

						await expect(
							fdsSamplePage.managementToolbar.filterButton
						).toBeHidden();
					});
				}
			);

			// Leaving the page by a single page application navigation is the
			// one way a client extension goes without the browser throwing its
			// page away, and the connection has to hand the filtering back on
			// its own account then. Both records of who owns the filtering
			// outlive such a navigation, since neither the module nor the atom
			// registry is reloaded: the owners the connection keeps by data set
			// name, and the filteringOwnerAppId it writes into the state of the
			// data set for everyone else to read. A connection that left
			// quietly would leave both standing over a data set nobody can
			// reach any more, and coming back would be refused by the claim it
			// made itself. Liferay says it is leaving through beforeNavigate,
			// which is where the connection disconnects.
			//
			// Only declared with the SPA on. With it off the browser loads the
			// page afresh, both records go with it, and there is nothing left
			// to outlive anything.
			//
			// Coming back is also the way round that the data set is slowest to
			// notice it may request. It waits while the address carries state a
			// consumer left, and it stops waiting either when the owner takes
			// its key out of what is offered or when it gives up. Arriving to an
			// atom the last visit left behind, the connection is listening
			// before the offer is even made and takes its key in the same turn,
			// so the data set is told it offered rather than left to catch the
			// offer going past. Missing it once cost the whole of that give-up,
			// which is why the wait below is measured rather than merely waited
			// out: a data set that takes as long as the give-up is a data set
			// that learnt nothing from the offer.
			//
			// Neither navigation reloads the page and the address returned to is
			// exactly the one the filters were left at: both are asserted below,
			// and both passed.

			if (spaConfiguration.spa) {
				test(
					'A client extension that leaves the page without reloading it gives the filtering back',
					{
						tag: ['@LPD-96001'],
					},
					async ({apiHelpers, fdsSamplePage, page, site}) => {
						const otherPageTitle = getRandomString();

						const otherPage =
							await apiHelpers.headlessDelivery.createSitePage({
								siteId: site.id,
								title: otherPageTitle,
							});

						await goToDelegatedFiltersTab({fdsSamplePage, page});

						await expect(
							getFilterPanelButton({name: 'Color'})
						).toBeEnabled();

						await test.step('Filter, so that there is a claim and filters to leave behind', async () => {
							await getFilterOptionCheckbox({
								name: 'Blue',
							}).check();

							await expectTotalEntries({page, total: 25});
						});

						const filteredURL = page.url();

						// Nothing on the window survives a page load, so this
						// is what tells a navigation Liferay handled from one
						// the browser did. Without it the test would pass on a
						// full load, which is precisely the case it is not
						// about.

						await page.evaluate(() => {
							(
								window as Window & {spaMarker?: boolean}
							).spaMarker = true;
						});

						// A page made through the API is not in the site
						// navigation, so the links a user would have followed
						// are put on the page here. What the test needs of them
						// is only that they are ordinary internal links:
						// Liferay intercepts the click on one exactly as it
						// does on any other, which is the navigation this is
						// about.
						//
						// Coming back is a second such link rather than the
						// back button, so that the address returned to is the
						// one the filters were left at. What the history holds
						// is not this test's subject, and it does not hold what
						// it looks like it should: selecting the tab is an
						// entry of its own, and the first filter of a visit
						// replaces the entry it was applied on rather than
						// adding one.

						const goByLink = async (href: string, id: string) => {
							await page.evaluate(
								([href, id]) => {
									const link = document.createElement('a');

									link.href = href;
									link.id = id;
									link.textContent = id;

									document.body.prepend(link);
								},
								[href, id]
							);

							await page.locator(`#${id}`).click();

							await waitForSPAToBeLoaded(page);
						};

						await test.step('Leave the page by a link to the other one', async () => {
							await goByLink(
								`/en/web${site.friendlyUrlPath}${otherPage.friendlyUrlPath}`,
								'otherPageLink'
							);

							await expect(page).toHaveURL(
								new RegExp(otherPage.friendlyUrlPath)
							);
						});

						// Not a threshold picked for comfort: it is the give-up
						// itself, which starts only once the page is back, so a
						// data set that fell back on it cannot come in under
						// this however fast everything else was. One that
						// learnt the offer was taken has no reason to be near
						// it.

						const cameBackAt = Date.now();

						await test.step('Come back to the data set by a link to where it was left', async () => {
							await goByLink(filteredURL, 'dataSetPageLink');

							await expect(page).toHaveURL(filteredURL);

							await waitForFDS({page});

							await expectTotalEntries({page, total: 25});
						});

						await test.step('The data set requested without waiting out its give-up', async () => {
							const cameBackIn = Date.now() - cameBackAt;

							expect(
								cameBackIn,
								`coming back took ${cameBackIn}ms of the ${RESTORE_GIVE_UP}ms give-up`
							).toBeLessThan(RESTORE_GIVE_UP);
						});

						await test.step('Neither navigation reloaded the page', async () => {
							expect(
								await page.evaluate(
									() =>
										(
											window as Window & {
												spaMarker?: boolean;
											}
										).spaMarker
								)
							).toBe(true);
						});

						await test.step('The element is granted the filtering again rather than refused', async () => {
							await expect(
								getFilterPanelButton({name: 'Color'})
							).toBeEnabled();

							await expect(
								getFilterOptionCheckbox({name: 'Blue'})
							).toBeChecked();

							await expect(
								fdsSamplePage.managementToolbar.filterButton
							).toBeHidden();
						});

						await test.step('And filters as it did before', async () => {
							await getFilterOptionCheckbox({
								name: 'Blue',
							}).uncheck();

							await expectTotalEntries({
								page,
								total: TOTAL_ENTRIES,
							});
						});
					}
				);
			}
		});

		// A data set has one filtering owner, and two instances of the same client
		// extension on a page are the plainest way to ask it for two. The element is
		// instanceable, so this needs no second client extension: the widget goes on
		// the page twice, and only the instance that connects first is granted the
		// filtering.
		//
		// Which instance that is follows the order the page mounts them in, and the
		// contract says not to rely on it, so nothing below names an instance by
		// position. The refused one is told through a status that is not "ready", and
		// the element enables its controls only once ready, so the two are told apart
		// by which one has controls that work.

		const BLUE_ENTRIES = 25;

		test.describe('with two client extensions that both ask for the filtering', () => {
			test.beforeEach(async ({page, pageEditorPage}) => {
				await test.step('Add the Custom Element 8 widget to the page twice', async () => {
					await page.goto(`${fdsPageUrl}?p_l_mode=edit`);

					for (let i = 0; i < 2; i++) {
						await pageEditorPage.addWidget(
							'Client Extensions',
							'Liferay Sample Custom Element 8'
						);
					}

					await pageEditorPage.publishPage();
				});
			});

			test(
				'The data set warns on the page when a second client extension asks for the filtering it already gave away',
				{
					tag: ['@LPD-96001'],
				},
				async ({fdsSamplePage, page}) => {
					await goToDelegatedFiltersTab({fdsSamplePage, page});

					await test.step('Both instances are on the page', async () => {
						await expect(customElements).toHaveCount(2);
					});

					await test.step('The refusal is reported by the data set, where the person looking at the page will see it', async () => {
						await expect(
							page
								.locator('.alert-container')
								.getByText(
									'Another widget is already filtering this data set'
								)
						).toBeVisible();
					});

					await test.step('The data set still keeps its own filter UI out of the way, and nothing is filtered yet', async () => {
						await expect(
							fdsSamplePage.managementToolbar.filterButton
						).toBeHidden();

						await expectTotalEntries({page, total: TOTAL_ENTRIES});
					});
				}
			);

			test(
				'Only the client extension that was granted the filtering offers controls that work',
				{
					tag: ['@LPD-96001'],
				},
				async ({fdsSamplePage, page}) => {
					await goToDelegatedFiltersTab({fdsSamplePage, page});

					const colorOption = page.getByRole('checkbox', {
						name: 'Blue',
					});

					await test.step('Both instances draw the filter, and exactly one of them can be used', async () => {
						await expect(colorOption).toHaveCount(2);

						await expect(
							page.getByRole('checkbox', {
								disabled: false,
								name: 'Blue',
							})
						).toHaveCount(1);

						await expect(
							page.getByRole('checkbox', {
								disabled: true,
								name: 'Blue',
							})
						).toHaveCount(1);
					});

					await test.step('The filters of the instance that can be used reach the data set', async () => {
						await page
							.getByRole('checkbox', {
								disabled: false,
								name: 'Blue',
							})
							.check();

						await expectTotalEntries({page, total: BLUE_ENTRIES});
					});

					await test.step('The refused instance is left as it was, since nothing it offers reaches the data set', async () => {
						await expect(
							page.getByRole('checkbox', {
								disabled: true,
								name: 'Blue',
							})
						).not.toBeChecked();
					});
				}
			);

			test(
				'The client extension that owns the filtering is the one named in the address',
				{
					tag: ['@LPD-96001'],
				},
				async ({fdsSamplePage, page}) => {
					await goToDelegatedFiltersTab({fdsSamplePage, page});

					await page
						.getByRole('checkbox', {disabled: false, name: 'Blue'})
						.check();

					await expectTotalEntries({page, total: BLUE_ENTRIES});

					await test.step('What the owner filters by is filed in the address under its name, once', async () => {

						// Naming the connection in the URL even while one owner writes
						// is what lets a link saved today keep its meaning once the
						// filtering can be split between several client extensions.

						const url = page.url();

						expect(url).toContain('sampleCustomElement8');

						expect(url.match(/sampleCustomElement8/g)).toHaveLength(
							1
						);
					});

					await test.step('Reloading filters the data set the same way, and still grants only one instance the filtering', async () => {
						await page.reload();

						await waitForFDS({page});

						await expectTotalEntries({page, total: BLUE_ENTRIES});

						await expect(
							page.getByRole('checkbox', {
								disabled: false,
								name: 'Blue',
							})
						).toBeChecked();

						await expect(
							page.getByRole('checkbox', {
								disabled: true,
								name: 'Blue',
							})
						).toHaveCount(1);
					});
				}
			);
		});
	});
}

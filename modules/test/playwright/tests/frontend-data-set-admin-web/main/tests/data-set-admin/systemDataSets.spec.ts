/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {waitForAlert} from '../../../../../utils/waitForAlert';
import {fdsSamplePageTest} from '../../../../frontend-data-set-web/main/fixtures/fdsSamplePageTest';
import {dataSetManagerApiHelpersTest} from '../../fixtures/dataSetManagerApiHelpersTest';
import {actionsPageTest} from './fixtures/actionsPageTest';
import {filtersPageTest} from './fixtures/filtersPageTest';
import {sortingPageTest} from './fixtures/sortingPageTest';
import {systemDataSetsPageTest} from './fixtures/systemDataSetsPageTest';
import {visualizationModesPageTest} from './fixtures/visualizationModesPageTest';

export const test = mergeTests(
	actionsPageTest,
	dataSetManagerApiHelpersTest,
	filtersPageTest,
	fdsSamplePageTest,
	isolatedSiteTest,
	sortingPageTest,
	systemDataSetsPageTest,
	visualizationModesPageTest,
	featureFlagsTest({
		'LPS-164563': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	loginTest()
);

const dataSetERCs: string[] = [];

test.afterEach(async ({dataSetManagerApiHelpers}) => {
	for (const erc of dataSetERCs) {
		await dataSetManagerApiHelpers.deleteDataSet({
			erc,
		});
	}
});

async function findTextIndexInLocators(
	locators: Locator[],
	textToFind: string
): Promise<number> {
	const texts = await Promise.all(
		locators.map(async (element) => {
			return await element.textContent();
		})
	);

	return texts.findIndex((text) => text?.trim().includes(textToFind));
}

test(
	'Import a system data set to customize',
	{tag: ['@LPD-37531', '@LPD-40949', '@LPD-49128']},
	async ({
		actionsPage,
		fdsSamplePage,
		filtersPage,
		page,
		site,
		sortingPage,
		systemDataSetsPage,
		visualizationModesPage,
	}) => {
		await test.step('Add FDS Sample Widget for object definition generation', async () => {
			await fdsSamplePage.setupFDSSampleWidget({site});
		});

		await test.step('Navigate to system data sets page', async () => {
			await systemDataSetsPage.goto();
		});

		const creationModal = systemDataSetsPage.creationModal;

		const advancedSampleListItem = creationModal.listItems.filter({
			hasText: 'Advanced Sample',
		});

		const classicSampleListItem = creationModal.listItems.filter({
			hasText: 'Classic Sample',
		});

		const customInternalViewSampleListItem = creationModal.listItems.filter(
			{
				hasText: 'Custom Internal View Sample',
			}
		);

		await test.step('Open creation modal and assert modal content', async () => {
			await systemDataSetsPage.createButton.click();

			await expect(
				creationModal.header.getByText(
					'Create System Data Set Customization'
				)
			).toBeVisible();

			await expect(creationModal.searchInput).toBeVisible();

			await expect(advancedSampleListItem).toBeVisible();
			await expect(classicSampleListItem).toBeVisible();
			await expect(customInternalViewSampleListItem).toBeVisible();
		});

		await test.step('Assert the items are listed in alphabetical order', async () => {
			const systemDataSets = await creationModal.body
				.locator('.data-set-content-wrapper .list-group-title')
				.all();

			const classicSampleIndex = await findTextIndexInLocators(
				systemDataSets,
				'Classic Sample'
			);

			expect(
				await findTextIndexInLocators(systemDataSets, 'Advanced Sample')
			).toBeLessThan(classicSampleIndex);

			expect(classicSampleIndex).toBeLessThan(
				await findTextIndexInLocators(
					systemDataSets,
					'Custom Internal View Sample'
				)
			);
		});

		await test.step('Search system data set items', async () => {
			await creationModal.searchInput.fill('Classic');

			await creationModal.searchInput.press('Enter');

			await expect(advancedSampleListItem).not.toBeAttached();
			await expect(classicSampleListItem).toBeVisible();
			await expect(customInternalViewSampleListItem).not.toBeAttached();

			await creationModal.searchInput.fill('aaa');

			await creationModal.searchInput.press('Enter');

			await expect(advancedSampleListItem).not.toBeAttached();
			await expect(classicSampleListItem).not.toBeAttached();
			await expect(customInternalViewSampleListItem).not.toBeAttached();

			await expect(
				creationModal.body.getByText('No Results Found')
			).toBeVisible();

			await creationModal.searchInput.fill('');

			await creationModal.searchInput.press('Enter');

			await expect(advancedSampleListItem).toBeVisible();
			await expect(classicSampleListItem).toBeVisible();
			await expect(customInternalViewSampleListItem).toBeVisible();
		});

		await test.step('Select and import system data sets', async () => {
			await advancedSampleListItem.click();

			await expect(advancedSampleListItem).toHaveClass(/selected/);

			dataSetERCs.push(

				// eslint-disable-next-line @liferay/no-get-data-attribute
				await advancedSampleListItem.getAttribute('data-erc')
			);

			await creationModal.createButton.click();

			await waitForAlert(systemDataSetsPage.page);

			await systemDataSetsPage.createButton.click();

			await classicSampleListItem.click();

			await expect(classicSampleListItem).toHaveClass(/selected/);

			dataSetERCs.push(

				// eslint-disable-next-line @liferay/no-get-data-attribute
				await classicSampleListItem.getAttribute('data-erc')
			);

			await creationModal.createButton.click();

			await waitForAlert(systemDataSetsPage.page);

			await systemDataSetsPage.createButton.click();

			await customInternalViewSampleListItem.click();

			await expect(customInternalViewSampleListItem).toHaveClass(
				/selected/
			);

			dataSetERCs.push(

				// eslint-disable-next-line @liferay/no-get-data-attribute
				await customInternalViewSampleListItem.getAttribute('data-erc')
			);

			await creationModal.createButton.click();

			await waitForAlert(systemDataSetsPage.page);
		});

		const fdsRows = systemDataSetsPage.pageContainer.locator('.fds tr');

		const advancedSampleRow = fdsRows.filter({
			hasText: 'Advanced Sample',
		});

		await test.step('Check system data set is imported and are "Active" by default', async () => {
			await expect(advancedSampleRow).toBeVisible();

			expect(
				fdsRows.filter({
					hasText: 'Classic Sample',
				})
			).toBeVisible();
			expect(
				fdsRows.filter({
					hasText: 'Custom Internal View Sample',
				})
			).toBeVisible();

			await expect(systemDataSetsPage.activeToggle.first()).toBeVisible();
		});

		await test.step('Can deactivate the system data set', async () => {
			await systemDataSetsPage.activeToggle.first().click();

			await waitForAlert(page);

			await expect(
				systemDataSetsPage.inactiveToggle.first()
			).toBeVisible();
		});

		await test.step('Can activate the system data set', async () => {
			await systemDataSetsPage.inactiveToggle.first().click();

			await waitForAlert(page);

			await expect(systemDataSetsPage.activeToggle.first()).toBeVisible();
		});

		await test.step('Check the creation modal labels the data set as created and is disabled', async () => {
			await systemDataSetsPage.createButton.click();

			await expect(advancedSampleListItem).toContainText('Created');
			await expect(advancedSampleListItem).toHaveClass(/disabled/);

			await creationModal.cancelButton.click();
		});

		await test.step('Advanced Sample items actions are imported with "item proxy" import policy', async () => {
			await actionsPage.open({dataSetLabel: 'Advanced Sample'});

			const itemActionRows = actionsPage.itemActionsTable
				.locator('tr')
				.filter({hasText: 'System Action'});

			await expect(itemActionRows).toHaveCount(14);

			for (const itemActionRow of await itemActionRows.all()) {
				await expect(
					itemActionRow.locator('.dropdown-toggle')
				).not.toBeAttached();
			}
		});

		await test.step('Creation actions are imported with "detached" import policy', async () => {
			await actionsPage.selectTab({
				container: actionsPage.actionsTabs,
				label: 'Creation Actions',
			});

			const creationActionRow = actionsPage.creationActionsTable
				.locator('tr')
				.filter({hasText: 'Open Form'})
				.first();

			await creationActionRow.locator('.dropdown-toggle').click();

			await actionsPage.page
				.locator('.dropdown-menu.show')
				.getByRole('menuitem', {name: 'Edit'})
				.click();

			const form = actionsPage.actionForm;

			await expect(form.labelInput).toHaveValue('Open Form');
			await expect(form.iconInput).toHaveValue('bolt');
			await expect(form.typeSelect).toHaveValue('modal');
			await expect(form.variantSelect).toHaveValue('full-screen');
			await expect(form.titleInput).toHaveValue('My Products');
			await expect(form.urlInput).toHaveValue('#');
			await expect(form.headlessActionKeyInput).toHaveValue('update');

			await form.cancelButton.click();

			await page.getByTitle('Back').click();
		});

		await test.step('Classic Sample items actions are imported with "item proxy" import policy', async () => {
			await actionsPage.open({dataSetLabel: 'Classic Sample'});

			const itemActionRows = actionsPage.itemActionsTable
				.locator('tr')
				.filter({hasText: 'System Action'});

			await expect(itemActionRows).toHaveCount(5);

			for (const itemActionRow of await itemActionRows.all()) {
				await expect(
					itemActionRow.locator('.dropdown-toggle')
				).not.toBeAttached();
			}
		});

		await test.step('Creation actions are imported with "item proxy" import policy', async () => {
			await actionsPage.selectTab({
				container: actionsPage.actionsTabs,
				label: 'Creation Actions',
			});

			const creationActionRows = actionsPage.creationActionsTable
				.locator('tr')
				.filter({hasText: 'System Action'});

			await expect(creationActionRows).toHaveCount(1);

			await expect(
				creationActionRows.getByText('Calendar', {exact: true})
			).toBeVisible();

			await page.getByTitle('Back').click();
		});

		await test.step('Item actions are imported with "group proxy" import policy', async () => {
			await actionsPage.open({
				dataSetLabel: 'Custom Internal View Sample',
			});

			const itemActionRows = actionsPage.itemActionsTable
				.locator('tr')
				.filter({hasText: 'Group of System Actions'});

			await expect(itemActionRows).toHaveCount(1);

			for (const itemActionRow of await itemActionRows.all()) {
				await expect(
					itemActionRow.locator('.dropdown-toggle')
				).not.toBeAttached();
			}
		});

		await test.step('Creation actions are imported with "group proxy" import policy', async () => {
			await actionsPage.selectTab({
				container: actionsPage.actionsTabs,
				label: 'Creation Actions',
			});

			const creationActionRows = actionsPage.creationActionsTable
				.locator('tr')
				.filter({hasText: 'Group of System Actions'});

			await expect(creationActionRows).toHaveCount(1);

			await page.getByTitle('Back').click();
		});

		await test.step('Table sections are imported', async () => {
			const assertTableSectionEntries = async (
				dataSetLabel: string,
				fields: object
			) => {
				await visualizationModesPage.open({
					dataSetLabel,
				});

				await visualizationModesPage.selectTab('Table');

				for (const fieldName of Object.keys(fields)) {
					for (const cell of fields[fieldName]) {
						const cellLocator = visualizationModesPage
							.getRowByText(fieldName)
							.locator('td')
							.nth(cell.index);

						await cellLocator.scrollIntoViewIfNeeded();

						await expect(cellLocator).toHaveText(cell.expected);
					}
				}
				await visualizationModesPage.assertTableFieldRowCount(
					Object.keys(fields).length
				);
			};

			const buildTableRowSpec = (sortableValue, rendererValue) => [
				{
					expected: sortableValue,
					index: visualizationModesPage.SORTABLE_COLUMN_INDEX,
				},
				{
					expected: rendererValue,
					index: visualizationModesPage.RENDERER_COLUMN_INDEX,
				},
			];

			await assertTableSectionEntries('Advanced Sample', {
				'color': buildTableRowSpec('false', ''),
				'creator.name': buildTableRowSpec(
					'false',
					'customAuthorTableCellRenderer'
				),
				'date': buildTableRowSpec('false', 'Date and Time'),
				'description': buildTableRowSpec('false', 'Default'),
				'id': buildTableRowSpec('true', 'Action Link'),
				'size': buildTableRowSpec('false', ''),
				'status': buildTableRowSpec('false', 'Status'),
				'title': buildTableRowSpec('true', 'Action Link'),
			});

			await page.getByTitle('Back').click();

			await assertTableSectionEntries('Classic Sample', {
				emailAddress: buildTableRowSpec('false', 'Default'),
				firstName: buildTableRowSpec('false', 'Default'),
				lastName: buildTableRowSpec('true', 'Default'),
			});

			await page.getByTitle('Back').click();
		});

		await test.step('Cards sections are imported', async () => {
			const assertCardsSectionEntries = async (
				dataSetLabel: string,
				sections: object
			) => {
				await visualizationModesPage.open({
					dataSetLabel,
				});

				await visualizationModesPage.selectTab('Cards');

				for (const sectionLabel of Object.keys(sections)) {
					const assignedFieldLocator =
						await visualizationModesPage.getAssignedFieldLocator({
							container:
								visualizationModesPage.cardsVisualizationModeContainer,
							sectionLabel,
						});
					await expect(assignedFieldLocator).toHaveText(
						sections[sectionLabel]
					);
				}
			};

			await assertCardsSectionEntries('Advanced Sample', {
				Description: 'description',
				Title: 'title',
			});

			await page.getByTitle('Back').click();
		});

		await test.step('List sections are imported', async () => {
			const assertListSectionEntries = async (
				dataSetLabel: string,
				sections: object
			) => {
				await visualizationModesPage.open({
					dataSetLabel,
				});

				await visualizationModesPage.selectTab('List');

				for (const sectionLabel of Object.keys(sections)) {
					const assignedFieldLocator =
						await visualizationModesPage.getAssignedFieldLocator({
							container:
								visualizationModesPage.cardsVisualizationModeContainer,
							sectionLabel,
						});
					await expect(assignedFieldLocator).toHaveText(
						sections[sectionLabel]
					);
				}
			};

			await assertListSectionEntries('Advanced Sample', {
				Description: 'description',
				Title: 'title',
			});

			await page.getByTitle('Back').click();
		});

		await test.step('Filters are imported', async () => {
			const assertFilterEntries = async (
				dataSetLabel: string,
				sections: Array<Array<string | boolean>>
			) => {
				await filtersPage.open({
					dataSetLabel,
				});

				let i = 0;

				for (const section of sections) {
					await filtersPage.assertTableCellContent({
						filterData: {
							actionsDropdown: section[3],
							fieldName: section[1],
							name: section[0],
							status: 'Active',
							type: section[2],
						},
						page: filtersPage.page,
						rowIndex: i++,
					});
				}

				await filtersPage.assertFiltersTableRowCount(sections.length);
			};

			await assertFilterEntries('Advanced Sample', [
				['Client Extension', 'id', 'Client Extension Filter', true],
				['Invalid', 'invalid', 'Client Extension Filter', true],
				['Date Range', 'date', 'Date Filter', true],
				['Color', 'color', 'System Filter', false],
				['Size', 'size', 'System Filter', false],
				['Status', 'status', 'System Filter', false],
				['Title', 'title', 'System Filter', false],
			]);

			await page.getByTitle('Back').click();
		});

		await test.step('Sorts are imported', async () => {
			const assertSortEntries = async (
				dataSetLabel: string,
				sections: Array<Array<string | boolean>>
			) => {
				await sortingPage.open({
					dataSetLabel,
				});

				let i = 0;

				for (const section of sections) {
					await sortingPage.assertTableCellContent({
						page: sortingPage.page,
						rowIndex: i++,
						sortData: {
							actionsDropdown: section[3],
							default: section[2],
							name: section[0],
							sortBy: section[1],
							status: 'Active',
						},
					});
				}

				await sortingPage.assertSortsTableRowCount(
					Object.keys(sections).length
				);
			};

			await assertSortEntries('Advanced Sample', [
				['By Title', 'title', 'Yes', true],
				['By Color', 'color', 'No', true],
			]);

			await page.getByTitle('Back').click();

			await assertSortEntries('Classic Sample', [
				['Last Name', 'lastName', 'Yes', false],
				['Email Address', 'emailAddress', 'No', false],
				['First Name', 'firstName', 'No', false],
			]);

			await page.getByTitle('Back').click();

			await assertSortEntries('Custom Internal View Sample', [
				['*', '*', 'No', false],
			]);

			await page.getByTitle('Back').click();
		});
		await test.step('Delete an imported system data set', async () => {
			await advancedSampleRow.locator('.dropdown-toggle').click();

			await systemDataSetsPage.page
				.locator('.dropdown-menu.show')
				.getByRole('menuitem', {name: 'Delete'})
				.click();

			const deleteModal = systemDataSetsPage.page.getByRole('dialog');

			await deleteModal.getByRole('button', {name: 'Delete'}).click();

			await waitForAlert(systemDataSetsPage.page);

			await expect(advancedSampleRow).not.toBeAttached();
		});

		await test.step('Check that deleted data set is again available for import ', async () => {
			await systemDataSetsPage.createButton.click();

			await expect(advancedSampleListItem).not.toContainText('Created');
			await expect(advancedSampleListItem).not.toHaveClass(/disabled/);

			await creationModal.cancelButton.click();
		});
	}
);

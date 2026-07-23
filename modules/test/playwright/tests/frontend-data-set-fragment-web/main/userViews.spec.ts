/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {dataSetManagerApiHelpersTest} from '../../../fixtures/dataSetManagerApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedLayoutTest} from '../../../fixtures/isolatedLayoutTest';
import {loginTest} from '../../../fixtures/loginTest';
import {createRecipientWithDataSetViewerRole} from '../../../helpers/DataSetManagerApiHelpers';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {NotificationsPage} from '../../notifications-web/main/pages/NotificationsPage';
import {dataSetFragmentPageTest} from './fixtures/dataSetFragmentPageTest';

export const test = mergeTests(
	dataApiHelpersTest,
	dataSetManagerApiHelpersTest,
	featureFlagsTest({
		'LPS-164563': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedLayoutTest({publish: false}),
	loginTest(),
	dataSetFragmentPageTest
);

let dataSetERC: string;
let dataSetLabel: string;
const dataSetERCs = [];

test.beforeEach(async ({dataSetManagerApiHelpers}) => {
	dataSetERC = getRandomString();
	dataSetLabel = getRandomString();

	dataSetERCs.push(dataSetERC);

	await test.step('Create data set', async () => {
		await dataSetManagerApiHelpers.createDataSet({
			defaultVisualizationMode: 'table',
			erc: dataSetERC,
			label: dataSetLabel,
			restEndpoint: '/',
			restSchema: 'DataSet',
		});
	});

	await test.step('Add some table sections', async () => {
		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'id',
			label_i18n: {en_US: 'Id'},
			type: 'string',
		});

		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'active',
			label_i18n: {en_US: 'Active'},
			sortable: false,
			type: 'boolean',
		});

		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'description',
			label_i18n: {en_US: 'Description'},
			type: 'string',
		});

		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'label',
			label_i18n: {en_US: 'Label'},
			type: 'string',
		});

		await dataSetManagerApiHelpers.createDataSetTableSection({
			dataSetERC,
			fieldName: 'restSchema',
			label_i18n: {en_US: 'Schema'},
			type: 'string',
		});
	});

	await test.step('Add some card sections', async () => {
		await dataSetManagerApiHelpers.createDataSetCardsSection({
			dataSetERC,
			fieldName: 'label',
			name: 'title',
		});

		await dataSetManagerApiHelpers.createDataSetCardsSection({
			dataSetERC,
			fieldName: 'description',
			name: 'description',
		});
	});
});

test.afterEach(async ({dataSetManagerApiHelpers}) => {
	for (const erc of dataSetERCs) {
		await dataSetManagerApiHelpers.deleteDataSet({
			erc,
		});
	}

	dataSetERCs.length = 0;
});

test(
	'Data Set does not show "User Views" (snapshots) if they are not enabled',
	{tag: '@LPD-10683'},
	async ({dataSetFragmentPage, layout}) => {
		await test.step('Configure Data Set fragment', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		await test.step('Check that the User Views (snapshots) controls are not present', async () => {
			await expect(
				dataSetFragmentPage.userViewsSelectorButton
			).not.toBeInViewport();
			await expect(
				dataSetFragmentPage.userViewsActionsButton
			).not.toBeInViewport();
		});
	}
);

test(
	'Can create, edit and delete User Views',
	{tag: ['@LPD-10683', '@LPD-74823']},
	async ({dataSetFragmentPage, dataSetManagerApiHelpers, layout, page}) => {
		let userViewsActionsDropdown: Locator;
		let userViewsDropdown: Locator;
		let columnsVisibilityDropdown: Locator;

		const userView1Name = getRandomString();
		const userView2Name = getRandomString();

		await test.step('Create collection of Data Sets', async () => {
			const testDataSetERCs = Array.from(Array(5).keys()).map(() =>
				getRandomString()
			);

			for (const DATA_SET_ERC of testDataSetERCs) {
				dataSetERCs.push(DATA_SET_ERC);

				await dataSetManagerApiHelpers.createDataSet({
					erc: DATA_SET_ERC,
					label: getRandomString(),
					restEndpoint: '/',
					restSchema: 'DataSet',
				});
			}
		});

		await test.step('Enable User Views (snapshots)', async () => {
			await dataSetManagerApiHelpers.updateDataSet({
				erc: dataSetERC,
				snapshotsEnabled: true,
			});
		});

		await test.step('Configure Data Set fragment', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		await test.step('User Views controls are present', async () => {
			await expect(
				dataSetFragmentPage.userViewsSelectorButton
			).toBeInViewport();
			await expect(
				dataSetFragmentPage.userViewsSelectorButton
			).toHaveText('Default View');

			await expect(
				dataSetFragmentPage.userViewsActionsButton
			).toBeInViewport();
		});

		await test.step('Get dropdown references', async () => {

			// Click on dropdown toggle button adds the aria-controls attribute

			await dataSetFragmentPage.userViewsActionsButton.click();

			const userViewsActionsDropdownId =
				await dataSetFragmentPage.userViewsActionsButton.getAttribute(
					'aria-controls'
				);

			userViewsActionsDropdown = page.locator(
				`#${userViewsActionsDropdownId}`
			);

			page.keyboard.press('Escape');

			await dataSetFragmentPage.userViewsSelectorButton.click();

			const userViewsDropdownId =
				await dataSetFragmentPage.userViewsSelectorButton.getAttribute(
					'aria-controls'
				);

			userViewsDropdown = page.locator(`#${userViewsDropdownId}`);

			page.keyboard.press('Escape');

			await dataSetFragmentPage.table.manageColumnsVisibilityButton.click();

			page.keyboard.press('Escape');
		});

		await test.step('Changing FDS configuration marks the user view as updated (* added)', async () => {
			const itemsPerPageButton =
				dataSetFragmentPage.paginationWrapper.getByLabel(
					'Items Per Page'
				);

			await expect(itemsPerPageButton).toHaveText('20 Items');

			await itemsPerPageButton.click();

			const paginationOptionsDropdownId =
				await itemsPerPageButton.evaluate((node) =>
					node.getAttribute('aria-controls')
				);

			await dataSetFragmentPage.page
				.locator(`#${paginationOptionsDropdownId}`)
				.waitFor();

			const paginationOptions = dataSetFragmentPage.page
				.locator(`#${paginationOptionsDropdownId}`)
				.getByRole('option');

			await paginationOptions.filter({hasText: '4 Items'}).click();

			await dataSetFragmentPage.table.manageColumnsVisibilityButton.click();

			const columnsVisibilityDropdownId =
				await dataSetFragmentPage.table.manageColumnsVisibilityButton.getAttribute(
					'aria-controls'
				);

			columnsVisibilityDropdown = page.locator(
				`#${columnsVisibilityDropdownId}`
			);

			await columnsVisibilityDropdown
				.getByRole('menuitem', {name: 'Description'})
				.click();

			page.keyboard.press('Escape');

			await expect(
				dataSetFragmentPage.userViewsSelectorButton
			).toHaveText('Default ViewDefault View Updated');
		});

		await test.step('Can save changes and create a new view', async () => {
			await dataSetFragmentPage.userViewsActionsButton.click();

			await userViewsActionsDropdown
				.filter({has: page.getByRole('menu')})
				.waitFor();

			const menuItem = userViewsActionsDropdown.getByRole('menuitem', {
				name: 'Save View As...',
			});

			await expect(menuItem).toBeVisible();

			await menuItem.click();

			await expect(
				dataSetFragmentPage.userViewsSaveModal
			).toBeInViewport();

			await dataSetFragmentPage.userViewsSaveModal
				.getByLabel('NameRequired')
				.fill(userView1Name);
			await dataSetFragmentPage.userViewsSaveModal
				.getByRole('button', {name: 'Save'})
				.click();

			await waitForAlert(page, 'Success:View was saved successfully.');

			await expect(
				dataSetFragmentPage.userViewsSelectorButton
			).toHaveText(userView1Name);
			await dataSetFragmentPage.userViewsSelectorButton.click();

			await expect(userViewsDropdown.getByRole('menuitem')).toHaveCount(
				2
			);

			page.keyboard.press('Escape');
		});

		await test.step('Cannot save a view with a blank or duplicate name', async () => {
			await dataSetFragmentPage.userViewsActionsButton.click();

			await userViewsActionsDropdown
				.filter({has: page.getByRole('menu')})
				.waitFor();

			await userViewsActionsDropdown
				.getByRole('menuitem', {name: 'Save View As...'})
				.click();

			await expect(
				dataSetFragmentPage.userViewsSaveModal
			).toBeInViewport();

			const nameInput =
				dataSetFragmentPage.userViewsSaveModal.getByLabel(
					'NameRequired'
				);
			const saveButton = dataSetFragmentPage.userViewsSaveModal.getByRole(
				'button',
				{name: 'Save'}
			);

			await nameInput.fill('   ');

			await expect(saveButton).toBeDisabled();

			await nameInput.fill(userView1Name.toUpperCase());

			await expect(saveButton).toBeEnabled();

			await saveButton.click();

			await expect(
				dataSetFragmentPage.userViewsSaveModal.getByText(
					'A view with this name already exists.'
				)
			).toBeVisible();

			await dataSetFragmentPage.userViewsSaveModal
				.getByRole('button', {name: 'Cancel'})
				.click();

			await expect(
				dataSetFragmentPage.userViewsSaveModal
			).not.toBeInViewport();
		});

		await test.step('Confirm that changes in an user view does not affect Default View', async () => {
			await expect(
				dataSetFragmentPage.userViewsSelectorButton
			).toHaveText(userView1Name);
			await expect(dataSetFragmentPage.table.headerCells).toHaveCount(5);

			await dataSetFragmentPage.userViewsSelectorButton.click();

			await userViewsDropdown.waitFor();

			await userViewsDropdown
				.getByRole('menuitem', {name: 'Default View'})
				.click();

			await expect(dataSetFragmentPage.table.headerCells).toHaveCount(6);
		});

		await test.step('Can update the new view', async () => {
			await dataSetFragmentPage.userViewsSelectorButton.click();

			await userViewsDropdown.waitFor();

			await userViewsDropdown
				.getByRole('menuitem', {name: userView1Name})
				.click();

			await dataSetFragmentPage.changeVisualizationMode('Cards');

			await dataSetFragmentPage.cardsWrapper.waitFor({
				state: 'visible',
			});

			await expect(dataSetFragmentPage.cardsWrapper).toBeInViewport();

			await expect(
				dataSetFragmentPage.userViewsSelectorButton
			).toHaveText(`${userView1Name}${userView1Name} Updated`);
			await dataSetFragmentPage.userViewsActionsButton.click();

			await userViewsActionsDropdown
				.filter({has: page.getByRole('menu')})
				.waitFor();

			const menuItem = userViewsActionsDropdown.getByRole('menuitem', {
				exact: true,
				name: 'Save View',
			});

			await expect(menuItem).toBeVisible();

			await menuItem.click();

			await waitForAlert(page, 'Success:View was saved successfully.');
		});

		await test.step('Can restore the Default View settings', async () => {
			await dataSetFragmentPage.userViewsSelectorButton.click();

			await userViewsDropdown
				.getByRole('menuitem', {name: 'Default View'})
				.click();

			await dataSetFragmentPage.table.container.waitFor({
				state: 'visible',
			});

			await expect(dataSetFragmentPage.table.container).toBeInViewport();
		});

		await test.step('Can rename a user view', async () => {
			await dataSetFragmentPage.userViewsSelectorButton.click();

			await userViewsDropdown
				.getByRole('menuitem', {name: userView1Name})
				.click();

			await dataSetFragmentPage.cardsWrapper.waitFor({
				state: 'visible',
			});

			await expect(dataSetFragmentPage.cardsWrapper).toBeInViewport();

			await dataSetFragmentPage.changeVisualizationMode('Table');

			await dataSetFragmentPage.table.container.waitFor({
				state: 'visible',
			});

			await expect(
				dataSetFragmentPage.userViewsSelectorButton
			).toHaveText(`${userView1Name}${userView1Name} Updated`);

			await dataSetFragmentPage.userViewsActionsButton.click();

			await userViewsActionsDropdown
				.filter({has: page.getByRole('menu')})
				.waitFor();

			const menuItem = userViewsActionsDropdown.getByRole('menuitem', {
				exact: true,
				name: 'Rename View',
			});

			await expect(menuItem).toBeVisible();

			await menuItem.click();

			await expect(
				dataSetFragmentPage.userViewsRenameModal
			).toBeInViewport();

			await dataSetFragmentPage.userViewsRenameModal
				.getByLabel('NameRequired')
				.fill(userView2Name);

			await dataSetFragmentPage.userViewsRenameModal
				.getByRole('button', {name: 'Save'})
				.click();

			await waitForAlert(page, 'Success:View was renamed successfully.');
		});

		await test.step('Renaming a view keeps its unsaved changes mark', async () => {
			await expect(
				dataSetFragmentPage.userViewsSelectorButton
			).toHaveText(`${userView2Name}${userView2Name} Updated`);
		});

		await test.step('Can delete a user view', async () => {
			await dataSetFragmentPage.userViewsActionsButton.click();

			await userViewsActionsDropdown
				.filter({has: page.getByRole('menu')})
				.waitFor();

			const menuItem = userViewsActionsDropdown.getByRole('menuitem', {
				exact: true,
				name: 'Delete View',
			});

			await expect(menuItem).toBeVisible();

			await menuItem.click();

			await expect(
				dataSetFragmentPage.userViewsDeleteAlert
			).toBeVisible();

			await dataSetFragmentPage.userViewsDeleteAlert
				.getByRole('button', {name: 'Delete'})
				.click();

			await dataSetFragmentPage.userViewsSelectorButton.click();

			await userViewsDropdown.waitFor();

			await expect(
				userViewsDropdown.getByRole('menuitem', {name: userView2Name})
			).not.toBeVisible();
		});
	}
);

test(
	'Can search the User Views dropdown to filter the list',
	{tag: '@LPD-75911'},
	async ({dataSetFragmentPage, dataSetManagerApiHelpers, layout, page}) => {
		await test.step('Enable User Views (snapshots)', async () => {
			await dataSetManagerApiHelpers.updateDataSet({
				erc: dataSetERC,
				snapshotsEnabled: true,
			});
		});

		await test.step('Configure Data Set fragment on the page', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		await test.step('Seed several user views via the API', async () => {
			for (const snapshotName of [
				'Active Orders',
				'Archived Orders',
				'Pending Invoices',
			]) {
				await dataSetManagerApiHelpers.createDataSetSnapshot({
					dataSetERC,
					snapshotName,
				});
			}
		});

		await test.step('Reload so the seeded views are listed', async () => {
			await dataSetFragmentPage.goToPage({layout});

			await page
				.locator('.data-set-content-wrapper')
				.waitFor({state: 'visible'});
		});

		await dataSetFragmentPage.userViewsSelectorButton.click();

		const userViewsDropdownId =
			await dataSetFragmentPage.userViewsSelectorButton.getAttribute(
				'aria-controls'
			);
		const userViewsDropdown = page.locator(`#${userViewsDropdownId}`);

		await userViewsDropdown.waitFor();

		const searchInput = userViewsDropdown.getByPlaceholder('Search');

		// "Default View" plus the three seeded views

		const allViewsCount = 4;

		await test.step('The full list and the search input are shown initially', async () => {
			await expect(searchInput).toBeVisible();
			await expect(userViewsDropdown.getByRole('menuitem')).toHaveCount(
				allViewsCount
			);
		});

		await test.step('Typing filters the list in real time', async () => {
			await searchInput.fill('orders');

			await expect(userViewsDropdown.getByRole('menuitem')).toHaveCount(
				2
			);
			await expect(
				userViewsDropdown.getByRole('menuitem', {name: 'Active Orders'})
			).toBeVisible();
			await expect(
				userViewsDropdown.getByRole('menuitem', {
					name: 'Archived Orders',
				})
			).toBeVisible();
		});

		await test.step('The search is case-insensitive', async () => {
			await searchInput.fill('INVOICES');

			await expect(userViewsDropdown.getByRole('menuitem')).toHaveCount(
				1
			);
			await expect(
				userViewsDropdown.getByRole('menuitem', {
					name: 'Pending Invoices',
				})
			).toBeVisible();
		});

		await test.step('The Default View is filtered along with the rest', async () => {
			await searchInput.fill('default');

			await expect(userViewsDropdown.getByRole('menuitem')).toHaveCount(
				1
			);
			await expect(
				userViewsDropdown.getByRole('menuitem', {name: 'Default View'})
			).toBeVisible();
		});

		await test.step('An empty state is shown when nothing matches', async () => {
			await searchInput.fill('nonexistent view name');

			await expect(userViewsDropdown.getByRole('menuitem')).toHaveCount(
				0
			);
			await expect(
				userViewsDropdown.getByText('No Results Found')
			).toBeVisible();
		});

		await test.step('Clearing the search restores the full list', async () => {
			await userViewsDropdown
				.getByRole('button', {name: 'Clear'})
				.click();

			await expect(searchInput).toHaveValue('');
			await expect(userViewsDropdown.getByRole('menuitem')).toHaveCount(
				allViewsCount
			);
		});
	}
);

test(
	'Share modal for User Views is configured for the FDS use case',
	{tag: '@LPD-81808'},
	async ({
		apiHelpers,
		dataSetFragmentPage,
		dataSetManagerApiHelpers,
		layout,
		page,
	}) => {
		const snapshotName = `Snapshot ${getRandomString().slice(0, 8)}`;

		let recipient: {alternateName: string; id: number | string};

		await test.step('Enable User Views (snapshots)', async () => {
			await dataSetManagerApiHelpers.updateDataSet({
				erc: dataSetERC,
				snapshotsEnabled: true,
			});
		});

		await test.step('Configure Data Set fragment on the page', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		await test.step('Create a recipient user', async () => {
			recipient = await createRecipientWithDataSetViewerRole({
				apiHelpers,
				page,
			});
		});

		await test.step('Create a snapshot to share', async () => {
			await dataSetManagerApiHelpers.createDataSetSnapshot({
				dataSetERC,
				snapshotName,
			});
		});

		await test.step('Reload and select the new snapshot', async () => {
			await dataSetFragmentPage.goToPage({layout});

			await page
				.locator('.data-set-content-wrapper')
				.waitFor({state: 'visible'});

			await dataSetFragmentPage.userViewsSelectorButton.click();

			const userViewsDropdownId =
				await dataSetFragmentPage.userViewsSelectorButton.getAttribute(
					'aria-controls'
				);

			await page
				.locator(`#${userViewsDropdownId}`)
				.getByRole('menuitem', {name: snapshotName})
				.click();
		});

		await test.step('Open the Share modal from the actions dropdown', async () => {
			await dataSetFragmentPage.userViewsActionsButton.click();

			const actionsDropdownId =
				await dataSetFragmentPage.userViewsActionsButton.getAttribute(
					'aria-controls'
				);

			await page
				.locator(`#${actionsDropdownId}`)
				.getByRole('menuitem', {name: 'Share View'})
				.click();
		});

		const shareModal = page
			.locator('.liferay-modal')
			.filter({hasText: `Share "${snapshotName}"`});

		await expect(shareModal).toBeVisible();

		await test.step('FDS-specific labels and section title are visible', async () => {
			await expect(
				shareModal.getByText('Add People', {exact: true})
			).toBeVisible();

			const helpIcon = shareModal.locator(
				'svg.lexicon-icon-question-circle-full'
			);

			await expect(helpIcon).toBeVisible();
			await expect(helpIcon).toHaveAttribute(
				'data-title',
				/Sharing recipients can use the view/
			);

			await expect(
				shareModal.getByText(/Who Can Use This View/)
			).toBeVisible();
		});

		await test.step('Add the recipient via the autocomplete', async () => {
			await shareModal
				.locator('#collaboratorAutocomplete')
				.fill(recipient.alternateName);

			await page
				.getByRole('listbox')
				.getByRole('option')
				.filter({hasText: recipient.alternateName})
				.first()
				.click();
		});

		await test.step('The recipient appears in the collaborators list', async () => {
			await expect(
				shareModal
					.locator('.list-group-item')
					.filter({hasText: recipient.alternateName})
			).toBeVisible();
		});

		await test.step('Permission, expiration date, and resharing controls are hidden on the collaborator row', async () => {
			await expect(shareModal.getByLabel('Edit Permissions')).toHaveCount(
				0
			);

			await expect(
				shareModal.getByLabel('Set Expiration Date')
			).toHaveCount(0);

			await expect(shareModal.getByLabel('More Options')).toHaveCount(0);

			await expect(
				shareModal.getByRole('button', {name: 'Remove Access'})
			).toBeVisible();
		});

		await test.step('Save the share and confirm a success toast appears', async () => {
			await shareModal.getByRole('button', {name: 'Share'}).click();

			await waitForAlert(page, 'was shared successfully');
		});

		await test.step('Reopen the Share modal', async () => {
			await dataSetFragmentPage.userViewsActionsButton.click();

			const actionsDropdownId =
				await dataSetFragmentPage.userViewsActionsButton.getAttribute(
					'aria-controls'
				);

			await page
				.locator(`#${actionsDropdownId}`)
				.getByRole('menuitem', {name: 'Share View'})
				.click();

			await expect(shareModal).toBeVisible();
		});

		await test.step('The recipient is still in the list with a Remove Access button', async () => {
			const collaboratorRow = shareModal
				.locator('.list-group-item')
				.filter({hasText: recipient.alternateName});

			await expect(collaboratorRow).toBeVisible();
			await expect(
				collaboratorRow.getByRole('button', {name: 'Remove Access'})
			).toBeVisible();
		});

		await test.step('Clicking Remove Access removes the recipient from the list', async () => {
			await shareModal
				.locator('.list-group-item')
				.filter({hasText: recipient.alternateName})
				.getByRole('button', {name: 'Remove Access'})
				.click();

			await expect(
				shareModal
					.locator('.list-group-item')
					.filter({hasText: recipient.alternateName})
			).toHaveCount(0);
		});

		await test.step('Share the removal and confirm a success toast appears', async () => {
			await shareModal.getByRole('button', {name: 'Share'}).click();

			await waitForAlert(page, 'was updated successfully');
		});
	}
);

test(
	'Shared user views appear under "Shared with Me" and are read-only for the recipient',
	{tag: ['@LPD-78095', '@LPD-87016']},
	async ({
		apiHelpers,
		dataSetFragmentPage,
		dataSetManagerApiHelpers,
		layout,
		page,
	}) => {
		const sharedSnapshotName = `Shared Snapshot ${getRandomString().slice(
			0,
			8
		)}`;

		let snapshotId: number;
		let user: {alternateName: string; id: number | string};

		await test.step('Enable User Views (snapshots)', async () => {
			await dataSetManagerApiHelpers.updateDataSet({
				erc: dataSetERC,
				snapshotsEnabled: true,
			});
		});

		await test.step('Configure Data Set fragment on the page', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		await test.step('Create a snapshot as the current user', async () => {
			const snapshot =
				(await dataSetManagerApiHelpers.createDataSetSnapshot({
					dataSetERC,
					snapshotName: sharedSnapshotName,
				})) as {id: number};

			snapshotId = snapshot.id;
		});

		await test.step('Create a recipient user with VIEW permission on Data Sets and snapshots', async () => {
			user = await createRecipientWithDataSetViewerRole({
				apiHelpers,
				page,
			});
		});

		await test.step('Share the snapshot with the recipient', async () => {
			await apiHelpers.objectEntry.postObjectEntryCollaborators(
				[
					{
						actionIds: ['VIEW'],
						id: user.id,
						share: false,
						type: 'User',
					},
				],
				'data-set-admin/snapshots',
				snapshotId
			);
		});

		await test.step('Switch to the recipient user and load the page', async () => {
			await performUserSwitch(page, user.alternateName);

			await dataSetFragmentPage.goToPage({layout});

			await page
				.locator('.data-set-content-wrapper')
				.waitFor({state: 'visible'});
		});

		await test.step('"Shared with Me" section and the shared view are visible in the dropdown', async () => {
			await dataSetFragmentPage.userViewsSelectorButton.click();

			const userViewsDropdownId =
				await dataSetFragmentPage.userViewsSelectorButton.getAttribute(
					'aria-controls'
				);
			const userViewsDropdown = page.locator(`#${userViewsDropdownId}`);

			await userViewsDropdown.waitFor();

			await expect(
				userViewsDropdown.getByText('Shared with Me')
			).toBeVisible();

			await expect(
				userViewsDropdown.getByRole('menuitem', {
					name: sharedSnapshotName,
				})
			).toBeVisible();
		});

		await test.step('Selecting the shared view makes it the active view', async () => {
			await page
				.getByRole('menuitem', {name: sharedSnapshotName})
				.click();

			await expect(
				dataSetFragmentPage.userViewsSelectorButton
			).toHaveText(sharedSnapshotName);
		});

		await test.step('The shared view offers only "Save View As", not save, rename, share, or delete', async () => {
			await dataSetFragmentPage.userViewsActionsButton.click();

			const userViewsActionsDropdownId =
				await dataSetFragmentPage.userViewsActionsButton.getAttribute(
					'aria-controls'
				);
			const userViewsActionsDropdown = page.locator(
				`#${userViewsActionsDropdownId}`
			);

			await userViewsActionsDropdown
				.filter({has: page.getByRole('menu')})
				.waitFor();

			await expect(
				userViewsActionsDropdown.getByRole('menuitem', {
					name: 'Save View As...',
				})
			).toBeVisible();

			await expect(
				userViewsActionsDropdown.getByRole('menuitem', {
					exact: true,
					name: 'Save View',
				})
			).toHaveCount(0);

			await expect(
				userViewsActionsDropdown.getByRole('menuitem', {
					name: 'Rename View',
				})
			).toHaveCount(0);

			await expect(
				userViewsActionsDropdown.getByRole('menuitem', {
					name: 'Share View',
				})
			).toHaveCount(0);

			await expect(
				userViewsActionsDropdown.getByRole('menuitem', {
					name: 'Delete View',
				})
			).toHaveCount(0);
		});
	}
);

test(
	'Recipient receives a notification when a user view is shared with them',
	{tag: '@LPD-87024'},
	async ({
		apiHelpers,
		dataSetFragmentPage,
		dataSetManagerApiHelpers,
		layout,
		page,
	}) => {
		const sharedSnapshotName = `Shared Snapshot ${getRandomString().slice(
			0,
			8
		)}`;

		let snapshotId: number;
		let user: {
			alternateName: string;
			id: number | string;
			name: string;
		};

		await test.step('Enable User Views (snapshots)', async () => {
			await dataSetManagerApiHelpers.updateDataSet({
				erc: dataSetERC,
				snapshotsEnabled: true,
			});
		});

		await test.step('Configure Data Set fragment on the page', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		await test.step('Create a snapshot as the current user', async () => {
			const snapshot =
				(await dataSetManagerApiHelpers.createDataSetSnapshot({
					dataSetERC,
					snapshotName: sharedSnapshotName,
				})) as {id: number};

			snapshotId = snapshot.id;
		});

		await test.step('Create a recipient user with VIEW permission on Data Sets and snapshots', async () => {
			user = await createRecipientWithDataSetViewerRole({
				apiHelpers,
				page,
			});
		});

		await test.step('Share the snapshot with the recipient', async () => {
			await apiHelpers.objectEntry.postObjectEntryCollaborators(
				[
					{
						actionIds: ['VIEW'],
						id: user.id,
						share: false,
						type: 'User',
					},
				],
				'data-set-admin/snapshots',
				snapshotId
			);
		});

		await test.step('Switch to the recipient user', async () => {
			await performUserSwitch(page, user.alternateName);
		});

		await test.step('Recipient sees the share notification', async () => {
			const notificationsPage = new NotificationsPage(page);

			await notificationsPage.goto(user.name);

			await expect(
				notificationsPage.sharingNotificationMessage(
					'Test Test',
					`'${sharedSnapshotName}'`
				)
			).toBeVisible();
		});

		await test.step('Switch back to the admin user so afterEach cleanup runs with delete permissions', async () => {
			await performUserSwitch(page, 'test');
		});
	}
);

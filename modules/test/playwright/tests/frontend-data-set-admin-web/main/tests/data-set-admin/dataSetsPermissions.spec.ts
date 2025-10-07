/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {liferayConfig} from '../../../../../liferay.config';
import getRandomString from '../../../../../utils/getRandomString';
import performLogin, {
	performUserSwitch,
} from '../../../../../utils/performLogin';
import {waitForModal} from '../../../../../utils/waitFor';
import {waitForAlert} from '../../../../../utils/waitForAlert';
import {dataSetManagerApiHelpersTest} from '../../fixtures/dataSetManagerApiHelpersTest';
import clickRowAction from '../../utils/clickRowAction';
import getRowByText from '../../utils/getRowByText';
import saveFromModal from '../../utils/saveFromModal';
import {setupUserRoleAndLoginAsUser} from '../../utils/setupUserRoleAndLoginAsUser';
import {EItemActionTarget} from '../../utils/types';
import {actionsPageTest} from './fixtures/actionsPageTest';
import {customDataSetsPageTest} from './fixtures/customDataSetsPageTest';
import {filtersPageTest} from './fixtures/filtersPageTest';
import {sortingPageTest} from './fixtures/sortingPageTest';
import {CustomDataSetsPage} from './pages/CustomDataSetsPage';

export const test = mergeTests(
	actionsPageTest,
	customDataSetsPageTest,
	dataApiHelpersTest,
	dataSetManagerApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	filtersPageTest,
	sortingPageTest
);

const createdDataSetERCs = [];
const createdRoleIds = [];
const createdUserIds = [];
const dataSetUserRoleName = `ds_user_${getRandomString()}`;

const blogPostsDataSetConfig = {
	name: 'BlogPost',
	restApplication: '/headless-delivery/v1.0',
	restEndpoint: '/v1.0/sites/{siteId}/blog-postings',
	restSchema: 'BlogPosting',
};

async function openActionsDropdown({
	customDataSetsPage,
	text,
}: {
	customDataSetsPage: CustomDataSetsPage;
	text: string;
}) {
	const row = customDataSetsPage.table.bodyRows
		.filter({hasText: text})
		.first();

	const actionsButton = row.locator('.cell-item-actions button');

	await expect(actionsButton).toBeInViewport();

	await actionsButton.click();
}

test.beforeEach(async ({page}) => {
	await performLogin(page, 'test');
});

test.afterEach(async ({apiHelpers, dataSetManagerApiHelpers, page}) => {
	await performUserSwitch(page, 'test');

	for (const erc of createdDataSetERCs) {
		await dataSetManagerApiHelpers.deleteDataSet({
			erc,
		});
	}

	createdDataSetERCs.length = 0;

	for (const id of createdRoleIds) {
		await apiHelpers.headlessAdminUser.deleteRole(id);
	}

	createdRoleIds.length = 0;

	for (const id of createdUserIds) {
		await apiHelpers.headlessAdminUser.deleteUserAccount(id);
	}

	createdUserIds.length = 0;
});

test('A user with "View" and "Permissions" permission', async ({
	apiHelpers,
	customDataSetsPage,
	dataSetManagerApiHelpers,
	page,
}) => {
	await test.step('Create a data set', async () => {
		const blogPostDataSetERC = getRandomString();
		createdDataSetERCs.push(blogPostDataSetERC);

		await dataSetManagerApiHelpers.createDataSet({
			...blogPostsDataSetConfig,
			erc: blogPostDataSetERC,
			label: blogPostsDataSetConfig.name,
		});
	});

	await test.step('Setup user role and login as user', async () => {
		const userRoleAndAccount = await setupUserRoleAndLoginAsUser({
			apiHelpers,
			dataSetResourcePermissions: [
				{
					actions: ['PERMISSIONS', 'VIEW'],
					resourceName: 'Data Set',
				},
			],
			dataSetUserRoleName,
			page,
		});

		createdRoleIds.push(userRoleAndAccount.dataSetUserRole.id);
		createdUserIds.push(userRoleAndAccount.userAccount.id);
	});

	await test.step('Go to Data Sets', async () => {
		await customDataSetsPage.goto({checkTabVisibility: false});
	});

	await test.step('Check that the "Permissions" button action is visible', async () => {
		await expect(
			customDataSetsPage.dataSetPermissionsButton.first()
		).toBeVisible();
	});

	await test.step('Open Permissions modal', async () => {
		await customDataSetsPage.dataSetPermissionsButton.first().click();

		await expect(
			customDataSetsPage.permissionsModal.locator('#guest_ACTION_VIEW')
		).not.toBeChecked();

		// wait for hydration

		await page.waitForTimeout(200);
	});

	await test.step('Enable "View" permission for "User" role', async () => {
		await customDataSetsPage.permissionsModal
			.locator('#guest_ACTION_VIEW')
			.setChecked(true);

		await expect(
			customDataSetsPage.permissionsModal.locator('#guest_ACTION_VIEW')
		).toBeChecked();
	});

	await test.step('Save Permissions modal', async () => {
		await customDataSetsPage.permissionsModal
			.getByRole('button', {name: 'Save'})
			.click();

		await waitForAlert(customDataSetsPage.permissionsModal);
	});

	await test.step('Click "Cancel" in the Permissions modal', async () => {
		await customDataSetsPage.permissionsModal
			.getByRole('button', {name: 'Cancel'})
			.click();
	});

	await test.step('Check that the Permissions modal is closed', async () => {
		await expect(
			page.getByRole('heading', {name: 'Permissions'})
		).not.toBeVisible();
	});

	await test.step('Open Permissions modal', async () => {
		await customDataSetsPage.dataSetPermissionsButton.first().click();
	});

	await test.step('Confirm "View" permission is persisted', async () => {
		await expect(
			customDataSetsPage.permissionsModal.locator('#guest_ACTION_VIEW')
		).toBeChecked();
	});
});

test('A user with only "View" permission', async ({
	apiHelpers,
	customDataSetsPage,
	dataSetManagerApiHelpers,
	page,
}) => {
	await test.step('Create a data set', async () => {
		const blogPostDataSetERC = getRandomString();
		createdDataSetERCs.push(blogPostDataSetERC);

		await dataSetManagerApiHelpers.createDataSet({
			...blogPostsDataSetConfig,
			erc: blogPostDataSetERC,
			label: blogPostsDataSetConfig.name,
		});
	});

	await test.step('Setup user role and login as user', async () => {
		const userRoleAndAccount = await setupUserRoleAndLoginAsUser({
			apiHelpers,
			dataSetResourcePermissions: [
				{
					actions: ['VIEW'],
					resourceName: 'Data Set',
				},
			],
			dataSetUserRoleName,
			page,
		});

		createdRoleIds.push(userRoleAndAccount.dataSetUserRole.id);
		createdUserIds.push(userRoleAndAccount.userAccount.id);
	});

	await test.step('Go to Data Sets', async () => {
		await customDataSetsPage.goto({checkTabVisibility: false});
	});

	await test.step('Check that there is no actions dropdown', async () => {
		const row = customDataSetsPage.table.bodyRows.filter({
			has: page
				.getByText(blogPostsDataSetConfig.name, {exact: true})
				.first(),
		});

		const actionsButton = row.locator('.cell-item-actions button');

		await expect(actionsButton).not.toBeInViewport();
	});

	await test.step('Check that "Permissions" is not visible', async () => {
		await expect(
			customDataSetsPage.dataSetPermissionsButton
		).not.toBeVisible();
		await expect(
			customDataSetsPage.dataSetPermissionsMenuItem
		).not.toBeVisible();
	});

	await test.step('Check that "Delete" is not visible', async () => {
		await expect(customDataSetsPage.dataSetDeleteButton).not.toBeVisible();
		await expect(
			customDataSetsPage.dataSetDeleteMenuItem
		).not.toBeVisible();
	});
});

test('A user without "View" permission on Data Set items', async ({
	apiHelpers,
	customDataSetsPage,
	dataSetManagerApiHelpers,
	page,
}) => {
	await test.step('Create a data set', async () => {
		const blogPostDataSetERC = getRandomString();
		createdDataSetERCs.push(blogPostDataSetERC);

		await dataSetManagerApiHelpers.createDataSet({
			...blogPostsDataSetConfig,
			erc: blogPostDataSetERC,
			label: blogPostsDataSetConfig.name,
		});
	});

	await test.step('Setup user role and login as user', async () => {
		const userRoleAndAccount = await setupUserRoleAndLoginAsUser({
			apiHelpers,
			dataSetUserRoleName,
			page,
		});

		createdRoleIds.push(userRoleAndAccount.dataSetUserRole.id);
		createdUserIds.push(userRoleAndAccount.userAccount.id);
	});

	await test.step('Go to Data Sets', async () => {
		await customDataSetsPage.goto({checkTabVisibility: false});
	});

	await test.step('Assert that no data sets appear on the table', async () => {
		await expect(customDataSetsPage.dataSetsEmptyState).toBeVisible();
	});
});

test('A user with "Delete" permission', async ({
	apiHelpers,
	customDataSetsPage,
	dataSetManagerApiHelpers,
	page,
}) => {
	await test.step('Create a data set', async () => {
		const blogPostDataSetERC = getRandomString();
		createdDataSetERCs.push(blogPostDataSetERC);

		await dataSetManagerApiHelpers.createDataSet({
			...blogPostsDataSetConfig,
			erc: blogPostDataSetERC,
			label: blogPostsDataSetConfig.name,
		});
	});

	await test.step('Setup user role and login as user', async () => {
		const userRoleAndAccount = await setupUserRoleAndLoginAsUser({
			apiHelpers,
			dataSetResourcePermissions: [
				{
					actions: ['DELETE', 'VIEW'],
					resourceName: 'Data Set',
				},
			],
			dataSetUserRoleName,
			page,
		});

		createdRoleIds.push(userRoleAndAccount.dataSetUserRole.id);
		createdUserIds.push(userRoleAndAccount.userAccount.id);
	});

	await test.step('Go to Data Sets', async () => {
		await customDataSetsPage.goto({checkTabVisibility: false});
	});

	await test.step('Open actions dropdown', async () => {
		await openActionsDropdown({
			customDataSetsPage,
			text: blogPostsDataSetConfig.name,
		});
	});

	await test.step('Check that "Delete" is visible', async () => {
		await expect(
			customDataSetsPage.dataSetDeleteButton.first()
		).toBeVisible();
	});
});

test('Check "Edit" permission', async ({
	actionsPage,
	apiHelpers,
	customDataSetsPage,
	dataSetManagerApiHelpers,
	filtersPage,
	page,
	sortingPage,
}) => {
	const actionLabel = getRandomString();
	const blogPostDataSetERC = getRandomString();
	const filterLabel = getRandomString();
	const sortingLabel = getRandomString();
	let userAccount;

	await test.step('Create a data set', async () => {
		createdDataSetERCs.push(blogPostDataSetERC);

		await dataSetManagerApiHelpers.createDataSet({
			...blogPostsDataSetConfig,
			erc: blogPostDataSetERC,
			label: blogPostsDataSetConfig.name,
		});
	});

	await test.step('Create data set filters, sorting and actions', async () => {
		await dataSetManagerApiHelpers.createDataSetDateFilter({
			dataSetERC: blogPostDataSetERC,
			fieldName: 'dateCreated',
			label_i18n: {en_US: filterLabel},
			type: 'date',
		});

		await dataSetManagerApiHelpers.createDataSetSort({
			dataSetERC: blogPostDataSetERC,
			fieldName: 'id',
			label_i18n: {en_US: sortingLabel},
		});

		await dataSetManagerApiHelpers.createDataSetItemAction({
			dataSetERC: blogPostDataSetERC,
			icon: 'pencil',
			label_i18n: {en_US: actionLabel},
			target: EItemActionTarget.LINK,
		});
	});

	await test.step('Setup user role and login as user', async () => {
		const userRoleAndAccount = await setupUserRoleAndLoginAsUser({
			apiHelpers,
			dataSetResourcePermissions: [
				{
					actions: ['VIEW'],
					resourceName: 'Data Set',
				},
			],
			dataSetUserRoleName,
			page,
		});

		userAccount = userRoleAndAccount.userAccount;
		createdRoleIds.push(userRoleAndAccount.dataSetUserRole.id);
		createdUserIds.push(userRoleAndAccount.userAccount.id);
	});

	await test.step('Go to Data Sets', async () => {
		await customDataSetsPage.goto({checkTabVisibility: false});
	});

	await test.step('Check that there is no actions dropdown', async () => {
		const row = customDataSetsPage.table.bodyRows.filter({
			has: page
				.getByText(blogPostsDataSetConfig.name, {exact: true})
				.first(),
		});

		const actionsButton = row.locator('.cell-item-actions button');

		await expect(actionsButton).not.toBeInViewport();
	});

	await test.step('Check that the user can not enter to Data Set details pages', async () => {
		const dataSetRows = customDataSetsPage.table.bodyRows.filter({
			hasText: blogPostsDataSetConfig.name,
		});

		await dataSetRows
			.first()
			.getByText(blogPostsDataSetConfig.name)
			.first()
			.click();

		await expect(
			page.getByRole('button', {name: 'Details'})
		).not.toBeVisible();

		await page.goto(
			`${liferayConfig.environment.baseUrl}/group/guest/~/control_panel/manage?p_p_id=com_liferay_frontend_data_set_admin_web_internal_portlet_FDSAdminPortlet&p_p_lifecycle=0&_com_liferay_frontend_data_set_admin_web_internal_portlet_FDSAdminPortlet_mvcRenderCommandName=%2Ffrontend_data_set_admin%2Fedit_data_set&_com_liferay_frontend_data_set_admin_web_internal_portlet_FDSAdminPortlet_dataSetERC=${blogPostDataSetERC}&_com_liferay_frontend_data_set_admin_web_internal_portlet_FDSAdminPortlet_dataSetLabel=${blogPostsDataSetConfig.name}`
		);
		await waitForAlert(page, 'Error:Your request failed to complete.', {
			type: 'danger',
		});
	});

	await test.step('Do logout and login as administrator', async () => {
		await performUserSwitch(page, 'test');
	});

	await test.step('Grant Data Sets "Update" permission for the new role', async () => {
		await customDataSetsPage.goto({checkTabVisibility: false});

		await openActionsDropdown({
			customDataSetsPage,
			text: blogPostsDataSetConfig.name,
		});

		await customDataSetsPage.dataSetPermissionsMenuItem.click();

		await expect(
			customDataSetsPage.permissionsModal.locator(
				`#${dataSetUserRoleName}_ACTION_UPDATE`
			)
		).not.toBeChecked();

		// wait for hydration

		await page.waitForTimeout(200);

		await customDataSetsPage.permissionsModal
			.locator(`#${dataSetUserRoleName}_ACTION_UPDATE`)
			.setChecked(true);

		await customDataSetsPage.permissionsModal
			.getByRole('button', {name: 'Save'})
			.click();

		await waitForAlert(customDataSetsPage.permissionsModal);
	});

	await test.step('Do logout and login with the new user', async () => {
		await performUserSwitch(page, userAccount.alternateName);
	});

	await test.step('Navigate to Data Set page', async () => {
		await customDataSetsPage.goto({checkTabVisibility: false});
	});

	await test.step('Check that the user has only "Edit" option on actions menu', async () => {
		await expect(
			customDataSetsPage.dataSetEditButton.first()
		).toBeVisible();
	});

	await test.step('Check that the user can now edit the data set', async () => {
		await customDataSetsPage.dataSetEditButton.first().click();

		await expect(
			page.getByRole('heading', {name: 'Details'})
		).toBeVisible();
	});

	const modalContainer = page.locator('.liferay-modal');

	const confirmDeleteButton = modalContainer.getByRole('button', {
		name: 'Delete',
	});

	let filtersTableRow: Locator;

	await test.step('Check that the user can edit data set filters', async () => {
		await filtersPage.selectTab('Filters');

		filtersTableRow = await getRowByText({
			page,
			table: filtersPage.filterTable,
			text: filterLabel,
		});

		await clickRowAction({
			actionLabel: 'Edit',
			page,
			row: filtersTableRow,
		});

		const nameInput = filtersPage.newDateRangeFilterForm.nameInput;

		await expect(nameInput).toBeInViewport();

		await filtersPage.saveAddFilterForm();

		await waitForAlert(page);
	});

	await test.step('Check that the user can delete data set filters', async () => {
		await clickRowAction({
			actionLabel: 'Delete',
			page,
			row: filtersTableRow,
		});

		await waitForModal({
			page,
		});

		await confirmDeleteButton.click();

		await waitForAlert(page);
	});

	let sortingsTableRow: Locator;

	await test.step('Check that the user can edit data set sortings', async () => {
		await sortingPage.selectTab('Sorting');

		sortingsTableRow = await getRowByText({
			page,
			table: sortingPage.sortingTable,
			text: sortingLabel,
		});

		await clickRowAction({
			actionLabel: 'Edit',
			page,
			row: sortingsTableRow,
		});

		await expect(
			sortingPage.page.getByLabel('Use as Default Sorting')
		).toBeInViewport();

		await saveFromModal({page: sortingPage.page});
	});

	await test.step('Check that the user can delete data set sortings', async () => {
		await clickRowAction({
			actionLabel: 'Delete',
			page,
			row: sortingsTableRow,
		});

		await waitForModal({
			page,
		});

		await confirmDeleteButton.click();

		await waitForAlert(page);
	});

	let actionsTableRow: Locator;

	await test.step('Check that the user can edit data set actions', async () => {
		await actionsPage.dataSetPage.selectTab('Actions');
		await expect(actionsPage.itemActionsTab).toBeInViewport();

		actionsTableRow = await getRowByText({
			page,
			table: actionsPage.itemActionsTable,
			text: actionLabel,
		});

		await clickRowAction({
			actionLabel: 'Edit',
			page,
			row: actionsTableRow,
		});

		await expect(actionsPage.actionForm.changeIconButton).toBeInViewport();

		await actionsPage.actionForm.saveButton.click();

		await waitForAlert(actionsPage.page);
	});

	await test.step('Check that the user can delete data set actions', async () => {
		await clickRowAction({
			actionLabel: 'Delete',
			page,
			row: actionsTableRow,
		});

		await waitForModal({
			page,
		});

		await confirmDeleteButton.click();

		await waitForAlert(page);
	});
});

test('A user with "Add Object Entry" permission', async ({
	apiHelpers,
	customDataSetsPage,
	page,
}) => {
	await test.step('Setup user role and login as user with "Add Object Entry"', async () => {
		const userRoleAndAccount = await setupUserRoleAndLoginAsUser({
			apiHelpers,
			dataSetResourcePermissions: [
				{
					actions: ['ADD_OBJECT_ENTRY'],
					resourceName: 'Data Sets',
				},
			],
			dataSetUserRoleName,
			page,
		});

		createdRoleIds.push(userRoleAndAccount.dataSetUserRole.id);
		createdUserIds.push(userRoleAndAccount.userAccount.id);
	});

	await test.step('Go to Data Sets', async () => {
		await customDataSetsPage.goto({checkTabVisibility: false});
	});

	await test.step('Confirm that the user can create a Data Set', async () => {
		await expect(customDataSetsPage.newDataSetButton).toBeVisible();

		await customDataSetsPage.createDataSet(blogPostsDataSetConfig);
	});

	await test.step('Delete Data Set', async () => {
		await openActionsDropdown({
			customDataSetsPage,
			text: blogPostsDataSetConfig.name,
		});

		await customDataSetsPage.dataSetDeleteMenuItem.click();

		const deleteModal = customDataSetsPage.page.getByRole('dialog');

		await deleteModal.getByRole('button', {name: 'Delete'}).click();
	});
});

test('A user without "Add Object Entry" permission', async ({
	apiHelpers,
	customDataSetsPage,
	page,
}) => {
	await test.step('Setup user role and login as user with "View" permission', async () => {
		const userRoleAndAccount = await setupUserRoleAndLoginAsUser({
			apiHelpers,
			dataSetResourcePermissions: [
				{
					actions: ['VIEW'],
					resourceName: 'Data Set',
				},
			],
			dataSetUserRoleName,
			page,
		});

		createdRoleIds.push(userRoleAndAccount.dataSetUserRole.id);
		createdUserIds.push(userRoleAndAccount.userAccount.id);
	});

	await test.step('Go to Data Sets', async () => {
		await customDataSetsPage.goto({checkTabVisibility: false});
	});

	await test.step('Confirm that the user can not create a Data Set', async () => {
		await expect(customDataSetsPage.newDataSetButton).not.toBeVisible();
	});
});

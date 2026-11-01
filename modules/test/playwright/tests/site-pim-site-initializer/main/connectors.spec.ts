/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {applyFDSSelectionFilter} from '../../../utils/applyFDSSelectionFilter';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {pimPagesTest} from './fixtures/pimPagesTest';

const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({'LPD-96666': {enabled: true}}),
	loginTest(),
	pimPagesTest
);

test.afterEach(async ({apiHelpers}) => {
	const {items} =
		await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
			'pim/connectors',
			new URLSearchParams({pageSize: '-1'})
		);

	for (const {id} of items) {
		await apiHelpers.objectEntry.deleteObjectEntry('pim/connectors', id);
	}
});

test(
	'Open the schedule of a connector',
	{tag: ['@LPD-108228', '@LPD-109731']},
	async ({connectorSchedulePage, connectorsPage, fieldMappingsPage}) => {
		const connectorName = getRandomString();

		await connectorsPage.createConnector({
			active: true,
			connector: 'Liferay Commerce',
			name: connectorName,
		});

		await test.step('An incomplete mapping cannot be executed', async () => {
			await expect(
				connectorsPage.getConnectorMappingStatus(connectorName)
			).toHaveText('Incomplete');

			await connectorsPage.dataSetFragmentPage.expectItemActionHidden({
				action: 'Execute',
				filter: connectorName,
			});
		});

		await test.step('Map the required channel fields', async () => {
			await connectorsPage.getConnector(connectorName).click();

			await fieldMappingsPage.mapRequiredChannelFields();

			await connectorsPage.goto();

			await expect(
				connectorsPage.getConnectorMappingStatus(connectorName)
			).toHaveText('Complete');
		});

		await test.step('The connector was never executed', async () => {
			await expect(
				connectorsPage.getConnectorLastExecutionStatus(connectorName)
			).toHaveText('Never Executed');
			await expect(
				connectorsPage.getConnectorLastExecutionDate(connectorName)
			).toHaveText('');
		});

		await test.step('Open the connector schedule', async () => {
			await connectorsPage.openConnectorSchedule(connectorName);

			await expect(connectorSchedulePage.cronExpressionInput).toHaveValue(
				'0 0 0 * * ?'
			);
		});

		await test.step('Go back to the connectors', async () => {
			await connectorSchedulePage.backLink.click();

			await expect(
				connectorsPage.getConnector(connectorName)
			).toBeVisible();
		});
	}
);

test(
	'Hide the search bar in the connectors empty state',
	{tag: ['@LPD-98441']},
	async ({connectorsPage}) => {
		await connectorsPage.goto();

		await expect(connectorsPage.emptyStateTitle).toBeVisible();
		await expect(
			connectorsPage.dataSetFragmentPage.searchInput
		).toBeHidden();
		await expect(connectorsPage.newConnectorButton).toBeVisible();
	}
);

test(
	'Mark the name and the connector as required fields',
	{tag: ['@LPD-101792']},
	async ({connectorsPage, editConnectorPage}) => {
		await connectorsPage.goto();

		await connectorsPage.newConnectorButton.click();

		await expect(
			editConnectorPage.referenceMark(editConnectorPage.nameInput)
		).toBeVisible();
		await expect(
			editConnectorPage.referenceMark(editConnectorPage.connectorSelect)
		).toBeVisible();
	}
);

test(
	'Create a connector',
	{tag: ['@LPD-98441']},
	async ({connectorsPage, editConnectorPage}) => {
		const connectorName = getRandomString();

		await connectorsPage.goto();

		await connectorsPage.newConnectorButton.click();

		await editConnectorPage.createConnector({
			connector: 'Liferay Commerce',
			name: connectorName,
		});

		await expect(connectorsPage.getConnector(connectorName)).toBeVisible();
	}
);

test(
	'Confirm a connector deletion through a modal',
	{tag: ['@LPD-107968']},
	async ({connectorsPage}) => {
		const connectorName = getRandomString();

		await connectorsPage.createConnector({
			connector: 'Liferay Commerce',
			name: connectorName,
		});

		await test.step('The modal spells out what is deleted', async () => {
			await connectorsPage.openDeleteConfirmation(connectorName);

			await expect(connectorsPage.deleteModalTitle).toContainText(
				`Delete ${connectorName}`
			);
			await expect(connectorsPage.deleteModal).toContainText(
				'The connector and all its field mappings will be deleted. This action cannot be undone.'
			);
			await expect(connectorsPage.deleteModalCancelButton).toBeVisible();
			await expect(connectorsPage.deleteModalConfirmButton).toBeVisible();
		});

		await test.step('Cancelling keeps the connector', async () => {
			await connectorsPage.deleteModalCancelButton.click();

			await expect(connectorsPage.deleteModal).toBeHidden();
			await expect(
				connectorsPage.getConnector(connectorName)
			).toBeVisible();
		});

		await test.step('Confirming deletes it', async () => {
			await connectorsPage.openDeleteConfirmation(connectorName);

			await connectorsPage.deleteModalConfirmButton.click();

			await expect(
				connectorsPage.getConnector(connectorName)
			).toBeHidden();
		});
	}
);

test(
	'Narrow the connectors list by status and by name',
	{tag: ['@LPD-106219', '@LPD-108228']},
	async ({connectorsPage, editConnectorPage, page}) => {
		const connectorName = getRandomString();

		await test.step('Create an inactive connector', async () => {
			await connectorsPage.goto();

			await connectorsPage.newConnectorButton.click();

			await editConnectorPage.createConnector({
				connector: 'Liferay Commerce',
				name: connectorName,
			});

			await expect(
				connectorsPage.getConnectorStatus(connectorName)
			).toHaveText('Inactive');
		});

		await test.step('An inactive connector cannot be executed', async () => {
			await connectorsPage.dataSetFragmentPage.expectItemActionHidden({
				action: 'Execute',
				filter: connectorName,
			});
		});

		await test.step('Activate the connector', async () => {
			await connectorsPage.dataSetFragmentPage.execItemAction({
				action: 'Edit',
				filter: connectorName,
			});

			await expect(editConnectorPage.activeToggle).not.toBeChecked();

			await editConnectorPage.activeToggle.click();

			await expect(editConnectorPage.activeToggle).toBeChecked();

			await editConnectorPage.updateConnector({name: connectorName});

			await expect(
				connectorsPage.getConnectorStatus(connectorName)
			).toHaveText('Active');
		});

		await test.step('Check the available filters', async () => {
			await clickAndExpectToBeVisible({
				target: connectorsPage.filterMenuItem('Connector'),
				trigger: connectorsPage.filterButton,
			});

			await expect(connectorsPage.filterMenuItem('Status')).toBeVisible();

			await page.keyboard.press('Escape');
		});

		await test.step('Filter out the active connectors', async () => {
			await applyFDSSelectionFilter(page, {
				filter: 'Status',
				value: 'Inactive',
			});

			await expect(
				connectorsPage.getConnector(connectorName)
			).toBeHidden();
		});

		await test.step('Search the connector by name', async () => {
			await connectorsPage.goto();

			await connectorsPage.dataSetFragmentPage.search(connectorName);

			await expect(
				connectorsPage.dataSetFragmentPage.table.bodyRows
			).toHaveCount(1);
		});
	}
);

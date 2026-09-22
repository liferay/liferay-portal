/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {applyFDSSelectionFilter} from '../../../utils/applyFDSSelectionFilter';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {pimPagesTest} from './fixtures/pimPagesTest';

const test = mergeTests(loginTest(), pimPagesTest);

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

		try {
			await connectorsPage.goto();

			await connectorsPage.newConnectorButton.click();

			await editConnectorPage.createConnector({
				connector: 'Liferay Commerce',
				name: connectorName,
			});

			await expect(
				connectorsPage.getConnector(connectorName)
			).toBeVisible();
		}
		finally {
			await connectorsPage.goto();

			await connectorsPage.deleteConnector(connectorName);
		}
	}
);

test(
	'Narrow the connectors list by status and by name',
	{tag: ['@LPD-106219']},
	async ({connectorsPage, editConnectorPage, page}) => {
		const connectorName = getRandomString();

		try {
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

				await expect(
					connectorsPage.filterMenuItem('Status')
				).toBeVisible();

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
		finally {
			await connectorsPage.goto();

			await connectorsPage.deleteConnector(connectorName);
		}
	}
);

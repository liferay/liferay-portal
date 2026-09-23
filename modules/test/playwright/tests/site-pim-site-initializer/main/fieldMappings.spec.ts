/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect} from '@playwright/test';

import getRandomString from '../../../utils/getRandomString';
import {pimPagesTest as test} from './fixtures/pimPagesTest';

test(
	'List the channel fields of a connector and their mapping status',
	{tag: ['@LPD-106220']},
	async ({connectorsPage, editConnectorPage, fieldMappingsPage, page}) => {
		const connectorName = getRandomString();

		try {
			await test.step('Create a connector', async () => {
				await connectorsPage.goto();

				await connectorsPage.newConnectorButton.click();

				await editConnectorPage.createConnector({
					connector: 'Liferay Commerce',
					name: connectorName,
				});

				await expect(
					connectorsPage.getConnector(connectorName)
				).toBeVisible();
			});

			await test.step('Reach the field mappings from the connector name', async () => {
				await connectorsPage.getConnector(connectorName).click();

				await expect(page).toHaveURL(/\/field-mappings\?/);

				await expect(
					fieldMappingsPage.dataSetFragmentPage.table.bodyRows
				).toHaveCount(5);
			});

			await test.step('Every channel field of a new connector is unmapped', async () => {
				await expect(fieldMappingsPage.status('Catalog ID')).toHaveText(
					'Required - Not Mapped'
				);
				await expect(
					fieldMappingsPage.status('Description')
				).toHaveText('Not Mapped');
				await expect(fieldMappingsPage.status('Name')).toHaveText(
					'Required - Not Mapped'
				);
				await expect(fieldMappingsPage.status('SKU')).toHaveText(
					'Required - Not Mapped'
				);
				await expect(fieldMappingsPage.status('Tags[]')).toHaveText(
					'Not Mapped'
				);
			});

			await test.step('Search the channel fields', async () => {
				await fieldMappingsPage.dataSetFragmentPage.search('SKU');

				await expect(
					fieldMappingsPage.dataSetFragmentPage.table.bodyRows
				).toHaveCount(1);

				await expect(
					fieldMappingsPage.channelField('SKU')
				).toBeVisible();
			});
		}
		finally {
			await connectorsPage.goto();

			await connectorsPage.deleteConnector(connectorName);
		}
	}
);

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
	async ({connectorsPage, fieldMappingsPage, page}) => {
		const connectorName = getRandomString();

		try {
			await test.step('Create a connector', async () => {
				await connectorsPage.createConnector({
					connector: 'Liferay Commerce',
					name: connectorName,
				});
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

test(
	'Declare where a channel field takes its value from',
	{tag: ['@LPD-106221']},
	async ({
		connectorsPage,
		editFieldMappingsPage,
		fieldMappingsPage,
		page,
	}) => {
		const connectorName = getRandomString();

		try {
			await test.step('Create a connector', async () => {
				await connectorsPage.createConnector({
					connector: 'Liferay Commerce',
					name: connectorName,
				});
			});

			await test.step('Open a channel field from the field mappings', async () => {
				await connectorsPage.getConnector(connectorName).click();

				await expect(fieldMappingsPage.status('SKU')).toHaveText(
					'Required - Not Mapped'
				);

				await fieldMappingsPage.channelField('SKU').click();

				await expect(page).toHaveURL(/\/edit-field-mappings\?/);
				await expect(
					editFieldMappingsPage.destinationHeading
				).toBeVisible();
			});

			await test.step('Map the channel field to a source attribute', async () => {
				await editFieldMappingsPage.mapToSourceAttribute('Code (code)');

				await editFieldMappingsPage.saveButton.click();

				await expect(page).toHaveURL(/\/field-mappings\?/);
				await expect(fieldMappingsPage.status('SKU')).toHaveText(
					'Mapped'
				);
				await expect(
					fieldMappingsPage.sourceAttributes('SKU')
				).toHaveText('Code');
			});

			await test.step('The mapping comes back on reopening', async () => {
				await fieldMappingsPage.channelField('SKU').click();

				await expect(
					editFieldMappingsPage.sourceAttribute(0)
				).toHaveValue('code');
			});

			await test.step('Narrow the mapping to one structure', async () => {
				await editFieldMappingsPage.source(0).selectOption({
					label: 'PIM Base SKU',
				});
				await editFieldMappingsPage.mapToSourceAttribute('Code (code)');

				await editFieldMappingsPage.saveButton.click();

				await expect(page).toHaveURL(/\/field-mappings\?/);
				await expect(
					fieldMappingsPage.sourceAttributes('SKU')
				).toHaveText('PIM Base SKU/Code');
			});

			await test.step('Add a fixed value as a second source', async () => {
				await fieldMappingsPage.channelField('SKU').click();

				await editFieldMappingsPage.addRowButton.click();

				await editFieldMappingsPage.mapToValue('ABC-1', 1);

				await editFieldMappingsPage.saveButton.click();

				await expect(page).toHaveURL(/\/field-mappings\?/);
				await expect(
					fieldMappingsPage.sourceAttributes('SKU')
				).toHaveText('PIM Base SKU/CodeABC-1');
			});

			await test.step('Unmap the channel field', async () => {
				await fieldMappingsPage.channelField('SKU').click();

				await editFieldMappingsPage.deleteRowButton.click();

				await editFieldMappingsPage.unmap();

				await editFieldMappingsPage.saveButton.click();

				await expect(page).toHaveURL(/\/field-mappings\?/);
				await expect(fieldMappingsPage.status('SKU')).toHaveText(
					'Required - Not Mapped'
				);
			});
		}
		finally {
			await connectorsPage.goto();

			await connectorsPage.deleteConnector(connectorName);
		}
	}
);

test(
	'Edit and clear a channel field mapping from its row actions',
	{tag: ['@LPD-106221']},
	async ({
		connectorsPage,
		editFieldMappingsPage,
		fieldMappingsPage,
		page,
	}) => {
		const connectorName = getRandomString();

		try {
			await test.step('Create a connector', async () => {
				await connectorsPage.createConnector({
					connector: 'Liferay Commerce',
					name: connectorName,
				});
			});

			await test.step('An unmapped channel field still offers the clear action', async () => {
				await connectorsPage.getConnector(connectorName).click();

				await expect(fieldMappingsPage.status('SKU')).toHaveText(
					'Required - Not Mapped'
				);

				await fieldMappingsPage.expectClearVisible('SKU');
			});

			await test.step('Edit the channel field from its row actions', async () => {
				await fieldMappingsPage.editMapping('SKU');

				await expect(page).toHaveURL(/\/edit-field-mappings\?/);

				await editFieldMappingsPage.mapToSourceAttribute('Code (code)');

				await editFieldMappingsPage.saveButton.click();

				await expect(page).toHaveURL(/\/field-mappings\?/);
				await expect(fieldMappingsPage.status('SKU')).toHaveText(
					'Mapped'
				);
			});

			await test.step('Clear the mapping from the row actions', async () => {
				await fieldMappingsPage.clearMapping('SKU');

				await expect(fieldMappingsPage.status('SKU')).toHaveText(
					'Required - Not Mapped'
				);
				await expect(
					fieldMappingsPage.sourceAttributes('SKU')
				).toBeEmpty();
			});
		}
		finally {
			await connectorsPage.goto();

			await connectorsPage.deleteConnector(connectorName);
		}
	}
);

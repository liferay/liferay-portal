/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import getRandomString from '../../../utils/getRandomString';
import {pimPagesTest} from './fixtures/pimPagesTest';

const test = mergeTests(dataApiHelpersTest, pimPagesTest);

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
	'List the channel fields of a connector and their mapping status',
	{tag: ['@LPD-106220']},
	async ({connectorsPage, fieldMappingsPage, page}) => {
		const connectorName = getRandomString();

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
			).toHaveCount(15);
		});

		await test.step('Every channel field of a new connector is unmapped', async () => {
			await expect(fieldMappingsPage.status('Catalog ID')).toHaveText(
				'Required - Not Mapped'
			);
			await expect(fieldMappingsPage.status('Description')).toHaveText(
				'Not Mapped'
			);
			await expect(fieldMappingsPage.status('Name')).toHaveText(
				'Required - Not Mapped'
			);
			await expect(
				fieldMappingsPage.status('Product Options[]')
			).toHaveText('Not Mapped');
			await expect(fieldMappingsPage.status('Product Type')).toHaveText(
				'Required - Not Mapped'
			);
			await expect(fieldMappingsPage.status('SKU')).toHaveText(
				'Required - Not Mapped'
			);
			await expect(fieldMappingsPage.status('Tags[]')).toHaveText(
				'Not Mapped'
			);
			await expect(
				fieldMappingsPage.status('Unit of Measure Name')
			).toHaveText('Not Mapped');
		});

		await test.step('Search the channel fields', async () => {
			await fieldMappingsPage.dataSetFragmentPage.search('SKU');

			await expect(
				fieldMappingsPage.dataSetFragmentPage.table.bodyRows
			).toHaveCount(1);

			await expect(fieldMappingsPage.channelField('SKU')).toBeVisible();
		});
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
			await editFieldMappingsPage.mapToSourceAttribute('SKU (sku)');

			await editFieldMappingsPage.saveButton.click();

			await expect(page).toHaveURL(/\/field-mappings\?/);
			await expect(fieldMappingsPage.status('SKU')).toHaveText('Mapped');
			await expect(fieldMappingsPage.sourceAttributes('SKU')).toHaveText(
				'SKU'
			);
		});

		await test.step('The mapping comes back on reopening', async () => {
			await fieldMappingsPage.channelField('SKU').click();

			await expect(editFieldMappingsPage.sourceAttribute(0)).toHaveValue(
				'sku'
			);
		});

		await test.step('Narrow the mapping to one structure', async () => {
			await editFieldMappingsPage.source(0).selectOption({
				label: 'PIM Basic Product',
			});
			await editFieldMappingsPage.mapToSourceAttribute('SKU (sku)');

			await editFieldMappingsPage.saveButton.click();

			await expect(page).toHaveURL(/\/field-mappings\?/);
			await expect(fieldMappingsPage.sourceAttributes('SKU')).toHaveText(
				'PIM Basic Product/SKU'
			);
		});

		await test.step('Add a fixed value as a second source', async () => {
			await fieldMappingsPage.channelField('SKU').click();

			await editFieldMappingsPage.addRowButton.click();

			await editFieldMappingsPage.mapToValue('ABC-1', 1);

			await editFieldMappingsPage.saveButton.click();

			await expect(page).toHaveURL(/\/field-mappings\?/);
			await expect(fieldMappingsPage.sourceAttributes('SKU')).toHaveText(
				'PIM Basic Product/SKUABC-1'
			);
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
);

test(
	'Map a channel field to two fixed values at once',
	{tag: ['@LPD-107968']},
	async ({
		connectorsPage,
		editFieldMappingsPage,
		fieldMappingsPage,
		page,
	}) => {
		const connectorName = getRandomString();

		await connectorsPage.createConnector({
			connector: 'Liferay Commerce',
			name: connectorName,
		});

		await connectorsPage.getConnector(connectorName).click();

		await fieldMappingsPage.channelField('SKU').click();

		await expect(editFieldMappingsPage.destinationHeading).toBeVisible();

		await editFieldMappingsPage.mapToValue('ABC-1');

		await editFieldMappingsPage.addRowButton.click();

		await editFieldMappingsPage.mapToValue('ABC-2', 1);

		await editFieldMappingsPage.saveButton.click();

		await expect(page).toHaveURL(/\/field-mappings\?/);
		await expect(fieldMappingsPage.status('SKU')).toHaveText('Mapped');
		await expect(fieldMappingsPage.sourceAttributes('SKU')).toHaveText(
			'ABC-1ABC-2'
		);
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

			await editFieldMappingsPage.mapToSourceAttribute('SKU (sku)');

			await editFieldMappingsPage.saveButton.click();

			await expect(page).toHaveURL(/\/field-mappings\?/);
			await expect(fieldMappingsPage.status('SKU')).toHaveText('Mapped');
		});

		await test.step('Clear the mapping from the row actions', async () => {
			await fieldMappingsPage.clearMapping('SKU');

			await expect(fieldMappingsPage.status('SKU')).toHaveText(
				'Required - Not Mapped'
			);
			await expect(fieldMappingsPage.sourceAttributes('SKU')).toBeEmpty();
		});
	}
);

test(
	'Clearing a required channel field mapping hides execute',
	{tag: ['@LPD-109731']},
	async ({connectorsPage, fieldMappingsPage, page}) => {
		const connectorName = getRandomString();

		await test.step('Create an active connector with a complete mapping', async () => {
			await connectorsPage.createConnector({
				active: true,
				connector: 'Liferay Commerce',
				name: connectorName,
			});

			await connectorsPage.getConnector(connectorName).click();

			await fieldMappingsPage.mapRequiredChannelFields();

			await fieldMappingsPage.expectMoreActionsMenuItems({
				visible: ['Execute', 'Schedule'],
			});
		});

		await test.step('Clear a required mapping without reloading the page', async () => {
			let loads = 0;

			page.on('load', () => loads++);

			await fieldMappingsPage.clearMapping('SKU');

			await expect(fieldMappingsPage.status('SKU')).toHaveText(
				'Required - Not Mapped'
			);

			await fieldMappingsPage.expectMoreActionsMenuItems({
				hidden: ['Execute'],
				visible: ['Schedule'],
			});

			expect(loads).toBe(0);
		});

		await test.step('The connector stays active with an incomplete mapping', async () => {
			await connectorsPage.goto();

			await expect(
				connectorsPage.getConnectorStatus(connectorName)
			).toHaveText('Active');
			await expect(
				connectorsPage.getConnectorMappingStatus(connectorName)
			).toHaveText('Incomplete');
		});
	}
);

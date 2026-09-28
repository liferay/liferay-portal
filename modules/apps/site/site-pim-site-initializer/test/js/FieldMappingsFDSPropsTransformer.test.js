/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {waitFor} from '@testing-library/react';

import propsTransformer from '../../src/main/resources/META-INF/resources/js/FieldMappingsFDSPropsTransformer';
import FieldMappingChannelFieldRenderer from '../../src/main/resources/META-INF/resources/js/cell_renderers/FieldMappingChannelFieldRenderer';
import FieldMappingSourceAttributesRenderer from '../../src/main/resources/META-INF/resources/js/cell_renderers/FieldMappingSourceAttributesRenderer';

const mockFetch = jest.fn();
const mockOpenToast = jest.fn();

jest.mock('frontend-js-components-web', () => ({
	openToast: (...args) => mockOpenToast(...args),
}));

jest.mock('frontend-js-web', () => ({
	fetch: (...args) => mockFetch(...args),
}));

const CLEAR_ACTION = {
	data: {apiURL: '/o/pim/connector-field-mappings', id: 'clear'},
};

const ITEM_DATA = {channelField: 'SKU', fieldMappingIds: [7, 11]};

describe('FieldMappingsFDSPropsTransformer', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		mockFetch.mockResolvedValue({ok: true});
	});

	it('registers the cell renderers under the names the table view declares', () => {
		const result = propsTransformer({});

		expect(result.customRenderers.tableCell).toEqual([
			{
				component: FieldMappingChannelFieldRenderer,
				name: 'channelFieldTableCellRenderer',
				type: 'internal',
			},
			{
				component: FieldMappingSourceAttributesRenderer,
				name: 'sourceAttributesTableCellRenderer',
				type: 'internal',
			},
		]);
	});

	it('forces hideManagementBarInEmptyState to true and preserves the other props', () => {
		const result = propsTransformer({
			apiURL: '/o/frontend-data-set-taglib/app',
			hideManagementBarInEmptyState: false,
			id: 'field-mappings',
		});

		expect(result.apiURL).toBe('/o/frontend-data-set-taglib/app');
		expect(result.hideManagementBarInEmptyState).toBe(true);
		expect(result.id).toBe('field-mappings');
	});

	it('deletes every field mapping the cleared channel field has', async () => {
		const loadData = jest.fn();

		propsTransformer({}).onActionDropdownItemClick({
			action: CLEAR_ACTION,
			itemData: ITEM_DATA,
			loadData,
		});

		await waitFor(() => expect(loadData).toHaveBeenCalled());

		expect(mockFetch).toHaveBeenCalledTimes(2);
		expect(mockFetch).toHaveBeenCalledWith(
			'/o/pim/connector-field-mappings/7',
			{method: 'DELETE'}
		);
		expect(mockFetch).toHaveBeenCalledWith(
			'/o/pim/connector-field-mappings/11',
			{method: 'DELETE'}
		);
		expect(mockOpenToast).toHaveBeenCalledWith(
			expect.objectContaining({type: 'success'})
		);
	});

	it('does nothing when the channel field has no mapping to clear', () => {
		const loadData = jest.fn();

		propsTransformer({}).onActionDropdownItemClick({
			action: CLEAR_ACTION,
			itemData: {channelField: 'SKU', fieldMappingIds: []},
			loadData,
		});

		expect(mockFetch).not.toHaveBeenCalled();
		expect(loadData).not.toHaveBeenCalled();
		expect(mockOpenToast).not.toHaveBeenCalled();
	});

	it('leaves an action other than clear alone', () => {
		propsTransformer({}).onActionDropdownItemClick({
			action: {data: {id: 'edit'}},
			itemData: ITEM_DATA,
			loadData: jest.fn(),
		});

		expect(mockFetch).not.toHaveBeenCalled();
	});

	it('keeps the rows when a delete fails', async () => {
		mockFetch.mockResolvedValue({ok: false});

		const loadData = jest.fn();

		propsTransformer({}).onActionDropdownItemClick({
			action: CLEAR_ACTION,
			itemData: ITEM_DATA,
			loadData,
		});

		await waitFor(() =>
			expect(mockOpenToast).toHaveBeenCalledWith(
				expect.objectContaining({type: 'danger'})
			)
		);

		expect(loadData).not.toHaveBeenCalled();
	});
});

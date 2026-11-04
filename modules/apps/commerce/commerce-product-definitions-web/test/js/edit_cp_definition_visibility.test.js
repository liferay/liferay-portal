/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openItemSelectorModal} from '@liferay/frontend-js-item-selector-web';
import {CommerceServiceProvider} from 'commerce-frontend-js';
import {openToast} from 'frontend-js-components-web';

import editCPDefinitionVisibility from '../../src/main/resources/META-INF/resources/js/edit_cp_definition_visibility';

jest.mock('@liferay/frontend-js-item-selector-web', () => ({
	openItemSelectorModal: jest.fn(),
}));

jest.mock('commerce-frontend-js', () => ({
	CommerceServiceProvider: {
		AdminCatalogAPI: jest.fn(),
	},
}));

jest.mock('frontend-js-components-web', () => ({
	openToast: jest.fn(),
}));

const updateProduct = jest.fn();

const eventHandlers = {};

function getFilter(apiURL) {
	const url = new URL(apiURL);

	return url.searchParams.get('filter');
}

async function select(eventName, items) {
	eventHandlers[`ns${eventName}`]();

	const {apiURL, onItemsChange} = openItemSelectorModal.mock.lastCall[0];

	await onItemsChange(items);

	return getFilter(apiURL);
}

describe('edit_cp_definition_visibility', () => {
	beforeEach(() => {
		CommerceServiceProvider.AdminCatalogAPI.mockReturnValue({
			updateProduct,
		});

		Liferay.on.mockImplementation((eventName, eventHandler) => {
			eventHandlers[eventName] = eventHandler;

			return {detach: jest.fn()};
		});

		editCPDefinitionVisibility({
			accountGroupDataSetId: 'accountGroupDataSetId',
			accountGroupIds: [10],
			channelDataSetId: 'channelDataSetId',
			commerceChannelIds: [50],
			namespace: 'ns',
			productId: 1,
		});
	});

	afterEach(() => {
		Liferay.on.mockReset();
		jest.clearAllMocks();
	});

	it('keeps the account groups added before when adding more', async () => {
		updateProduct.mockResolvedValue({});

		expect(await select('selectCommerceAccountGroup', [{id: 20}])).toBe(
			'not (id in (10))'
		);

		expect(updateProduct).toHaveBeenLastCalledWith(1, {
			productAccountGroupFilter: true,
			productAccountGroups: [{accountGroupId: 10}, {accountGroupId: 20}],
		});

		expect(await select('selectCommerceAccountGroup', [{id: 30}])).toBe(
			'not (id in (10,20))'
		);

		expect(updateProduct).toHaveBeenLastCalledWith(1, {
			productAccountGroupFilter: true,
			productAccountGroups: [
				{accountGroupId: 10},
				{accountGroupId: 20},
				{accountGroupId: 30},
			],
		});
	});

	it('keeps the channels added before when adding more', async () => {
		updateProduct.mockResolvedValue({});

		await select('selectCommerceChannel', [{id: 60}]);

		expect(await select('selectCommerceChannel', [{id: 70}])).toBe(
			'not (id in (50,60))'
		);

		expect(updateProduct).toHaveBeenLastCalledWith(1, {
			productChannelFilter: true,
			productChannels: [
				{channelId: 50},
				{channelId: 60},
				{channelId: 70},
			],
		});
	});

	it('does not keep the account groups of a failed update', async () => {
		updateProduct.mockRejectedValue(new Error());

		await select('selectCommerceAccountGroup', [{id: 20}]);

		expect(openToast).toHaveBeenCalledWith(
			expect.objectContaining({type: 'danger'})
		);

		updateProduct.mockResolvedValue({});

		expect(await select('selectCommerceAccountGroup', [{id: 30}])).toBe(
			'not (id in (10))'
		);

		expect(updateProduct).toHaveBeenLastCalledWith(1, {
			productAccountGroupFilter: true,
			productAccountGroups: [{accountGroupId: 10}, {accountGroupId: 30}],
		});
	});

	it('does not update the product when nothing is selected', async () => {
		await select('selectCommerceAccountGroup', []);

		expect(updateProduct).not.toHaveBeenCalled();
	});
});

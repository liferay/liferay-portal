/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {openToast} from 'frontend-js-components-web';
import React from 'react';

import '@testing-library/jest-dom';

import ProductRelationshipSelectorModal from '../../src/main/resources/META-INF/resources/js/ProductRelationshipSelectorModal';

const mockFetch = fetch as jest.MockedFunction<typeof fetch>;

const mockFrontendDataSet = jest.fn();

const PRODUCTS = [
	{
		embedded: {externalReferenceCode: 'erc-2', id: 2, title: 'Product B'},
		entryClassName: 'com.liferay.object.model.ObjectDefinition#P',
	},
	{
		embedded: {externalReferenceCode: 'erc-3', id: 3, title: 'Product C'},
		entryClassName: 'com.liferay.object.model.ObjectDefinition#Q',
	},
];

jest.mock('@liferay/frontend-data-set-web', () => ({
	FrontendDataSet: (props: any) => {
		mockFrontendDataSet(props);

		const {onSelectedItemsChange, selectedItems} = props;

		return PRODUCTS.map((product) => (
			<label key={product.embedded.id}>
				<input
					checked={selectedItems.includes(product)}
					onChange={() =>
						onSelectedItemsChange([...selectedItems, product])
					}
					type="checkbox"
				/>

				{product.embedded.title}
			</label>
		));
	},
}));

jest.mock('frontend-js-components-web', () => ({
	openToast: jest.fn(),
}));

const PROPS = {
	className: 'com.liferay.object.model.ObjectDefinition#P',
	externalReferenceCode: 'erc-source',
	filters: '[{"id":"status"}]',
	scopeKey: '123',
	searchAPIURL:
		'/o/search/v1.0/search?filter=not%20%28entryClassPK%20in%20%2842{relatedObjectEntryIds}%29%29',
	title: 'Product A',
};

function mockRelatedObjectEntryIds(ids: number[]) {
	mockFetch.mockResolvedValueOnce({
		json: () => Promise.resolve({items: ids.map((id) => ({id}))}),
		ok: true,
	} as Response);
}

describe('ProductRelationshipSelectorModal', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('lists the products that are not related yet', async () => {
		mockRelatedObjectEntryIds([111, 222]);

		render(<ProductRelationshipSelectorModal {...PROPS} />);

		expect(await screen.findByLabelText('Product B')).toBeInTheDocument();

		expect(mockFetch.mock.calls[0][0]).toContain(
			'/o/headless-pim/v1.0/scopes/123/links?className=com.liferay.object.model.ObjectDefinition%23P&externalReferenceCode=erc-source'
		);

		const {apiURL, filters} = mockFrontendDataSet.mock.calls[0][0];

		expect(apiURL).toContain('%2842,111,222%29%29');
		expect(filters).toEqual([{id: 'status'}]);
	});

	it('keeps Save disabled until a product is selected', async () => {
		mockRelatedObjectEntryIds([]);

		render(<ProductRelationshipSelectorModal {...PROPS} />);

		expect(await screen.findByText('save')).toBeDisabled();

		await userEvent.click(screen.getByLabelText('Product B'));

		expect(screen.getByText('save')).toBeEnabled();
	});

	it('relates the selected products with their own class names', async () => {
		const loadData = jest.fn();

		mockRelatedObjectEntryIds([]);

		mockFetch.mockResolvedValueOnce({ok: true} as Response);

		render(
			<ProductRelationshipSelectorModal {...PROPS} loadData={loadData} />
		);

		await userEvent.click(await screen.findByLabelText('Product B'));
		await userEvent.click(screen.getByLabelText('Product C'));
		await userEvent.click(screen.getByText('save'));

		await waitFor(() => expect(loadData).toHaveBeenCalled());

		const [url, init] = mockFetch.mock.calls[1];

		expect(url).toBe('/o/headless-pim/v1.0/scopes/123/links');
		expect(JSON.parse(init!.body as string)).toEqual({
			sourceLinkReference: {
				className: 'com.liferay.object.model.ObjectDefinition#P',
				externalReferenceCode: 'erc-source',
			},
			targetLinkReferences: [
				{
					className: 'com.liferay.object.model.ObjectDefinition#P',
					externalReferenceCode: 'erc-2',
				},
				{
					className: 'com.liferay.object.model.ObjectDefinition#Q',
					externalReferenceCode: 'erc-3',
				},
			],
			type: 'variant',
		});
		expect(openToast).toHaveBeenCalledWith(
			expect.objectContaining({type: 'success'})
		);
	});

	it('stays open with the selection when saving fails', async () => {
		const loadData = jest.fn();

		mockRelatedObjectEntryIds([]);

		mockFetch.mockResolvedValueOnce({ok: false} as Response);

		render(
			<ProductRelationshipSelectorModal {...PROPS} loadData={loadData} />
		);

		await userEvent.click(await screen.findByLabelText('Product B'));
		await userEvent.click(screen.getByText('save'));

		await waitFor(() =>
			expect(openToast).toHaveBeenCalledWith(
				expect.objectContaining({type: 'danger'})
			)
		);

		expect(loadData).not.toHaveBeenCalled();
		expect(screen.getByLabelText('Product B')).toBeChecked();
		expect(screen.getByText('save')).toBeEnabled();
	});
});

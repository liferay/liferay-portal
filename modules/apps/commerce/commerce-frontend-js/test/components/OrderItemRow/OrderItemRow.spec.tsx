/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// @ts-ignore

import fetchMock from 'fetch-mock';

import {
	IDeliveryGroup,
	IOrderItem,
} from '../../../src/main/resources/META-INF/resources/components/multishipping/Types';

import '@testing-library/jest-dom';
import {RenderResult, cleanup, render, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';
import {act} from 'react-dom/test-utils';

import OrderItemRow from '../../../src/main/resources/META-INF/resources/components/multishipping/OrderItemRow';
import {setFieldValue} from '../../tests_utilities/utils.spec';

interface ILocators {
	deliveryGroup1Cell: HTMLElement | null;
	deliveryGroup1Input: HTMLInputElement | HTMLSelectElement | null;
	deliveryGroup2Cell: HTMLElement | null;
	deliveryGroup2Input: HTMLInputElement | HTMLSelectElement | null;
	image: HTMLImageElement;
	quantityCell: HTMLElement;
	row0Actions: HTMLButtonElement;
	row0CopyColumnAction: HTMLButtonElement;
	row0RemoveItemAction: HTMLButtonElement;
	row0ResetRowAction: HTMLButtonElement;
	row0Select: HTMLInputElement;
	row0SplitQuantityAction: HTMLButtonElement;
	skuNameCell: HTMLElement;
}

const getLocators = (
	deliveryGroups: Array<IDeliveryGroup>,
	orderItemId: number,
	renderedComponent: RenderResult
): ILocators => {
	return {
		deliveryGroup1Cell: renderedComponent.queryByTestId(
			`orderItem${orderItemId}-${deliveryGroups[0]?.id}`
		),
		deliveryGroup1Input: renderedComponent.queryByTestId(
			`orderItem${orderItemId}-${deliveryGroups[0]?.id}Input`
		) as HTMLInputElement | HTMLSelectElement,
		deliveryGroup2Cell: renderedComponent.queryByTestId(
			`orderItem${orderItemId}-${deliveryGroups[1]?.id}`
		),
		deliveryGroup2Input: renderedComponent.queryByTestId(
			`orderItem${orderItemId}-${deliveryGroups[1]?.id}Input`
		) as HTMLInputElement | HTMLSelectElement,
		image: renderedComponent.queryByRole('img') as HTMLImageElement,
		quantityCell: renderedComponent.queryByTestId(
			`orderItem${orderItemId}Quantity`
		) as HTMLElement,
		row0Actions: renderedComponent.queryByTestId(
			'row0Actions'
		) as HTMLButtonElement,
		row0CopyColumnAction: renderedComponent.queryByTestId(
			'row0CopyColumn'
		) as HTMLButtonElement,
		row0RemoveItemAction: renderedComponent.queryByTestId(
			'row0RemoveItem'
		) as HTMLButtonElement,
		row0ResetRowAction: renderedComponent.queryByTestId(
			'row0ResetRow'
		) as HTMLButtonElement,
		row0Select: renderedComponent.queryByTestId(
			'row0Select'
		) as HTMLInputElement,
		row0SplitQuantityAction: renderedComponent.queryByTestId(
			'row0SplitQuantity'
		) as HTMLButtonElement,
		skuNameCell: renderedComponent.queryByRole('cell', {
			name: 'sku-name',
		}) as HTMLElement,
	};
};

describe('OrderItemRow', () => {
	const handleSubmit = jest.fn();

	beforeEach(() => {
		(window as any).Liferay = {
			...(window as any).Liferay,
			CustomDialogs: {},
		};
	});

	afterEach(() => {
		fetchMock.restore();
		jest.restoreAllMocks();
		jest.clearAllMocks();

		cleanup();
	});

	it('Must display order item information without inputs', async () => {
		const orderItem = {
			id: 100,
			name: 'Product1',
			options: '[]',
			productId: 1000,
			quantity: 2,
			replacedSkuId: 0,
			settings: {
				maxQuantity: 10000,
				minQuantity: 1,
				multipleQuantity: 1,
			},
			sku: 'SKU1',
			skuId: 1001,
			thumbnail: '/o/commerce-media/default/?groupId=33472',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemRow
				deliveryGroups={[]}
				handleSelection={jest.fn()}
				handleSubmit={handleSubmit}
				orderId={10}
				orderItem={orderItem}
			/>
		);

		const {deliveryGroup1Cell, image, quantityCell, skuNameCell} =
			getLocators([], orderItem.id, renderedComponent);

		expect(image).toHaveAttribute('src', orderItem.thumbnail);
		expect(skuNameCell).toHaveTextContent(orderItem.sku as string);
		expect(quantityCell).toHaveTextContent(String(orderItem.quantity));
		expect(deliveryGroup1Cell).not.toBeInTheDocument();
	});

	it('Must display order item information with empty inputs for all delivery groups', async () => {
		const deliveryGroups = [
			{
				addressId: 100,
				deliveryDate: '',
				id: 10000,
				name: 'DeliveryGroup1',
			},
			{
				addressId: 100,
				deliveryDate: '',
				id: 10001,
				name: 'DeliveryGroup2',
			},
		];

		const orderItem = {
			id: 100,
			name: 'Product1',
			options: '[]',
			productId: 1000,
			quantity: 2,
			replacedSkuId: 0,
			settings: {
				maxQuantity: 10000,
				minQuantity: 1,
				multipleQuantity: 1,
			},
			sku: 'SKU1',
			skuId: 1001,
			thumbnail: '/o/commerce-media/default/?groupId=33472',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemRow
				deliveryGroups={deliveryGroups}
				handleSelection={jest.fn()}
				handleSubmit={handleSubmit}
				orderId={10}
				orderItem={orderItem}
			/>
		);

		const {
			deliveryGroup1Cell,
			deliveryGroup1Input,
			deliveryGroup2Cell,
			deliveryGroup2Input,
			image,
			quantityCell,
			skuNameCell,
		} = getLocators(deliveryGroups, orderItem.id, renderedComponent);

		expect(image).toHaveAttribute('src', orderItem.thumbnail);
		expect(skuNameCell).toHaveTextContent(orderItem.sku as string);
		expect(quantityCell).toHaveTextContent(String(orderItem.quantity));
		expect(deliveryGroup1Cell).toBeInTheDocument();
		expect(deliveryGroup2Cell).toBeInTheDocument();
		expect(deliveryGroup1Input).toBeInTheDocument();
		expect(deliveryGroup1Input).not.toHaveValue();
		expect(deliveryGroup2Input).toBeInTheDocument();
		expect(deliveryGroup2Input).not.toHaveValue();
	});

	it('Must disable all the fields', async () => {
		const deliveryGroups = [
			{
				addressId: 100,
				deliveryDate: '',
				id: 10000,
				name: 'DeliveryGroup1',
			},
			{
				addressId: 100,
				deliveryDate: '',
				id: 10001,
				name: 'DeliveryGroup2',
			},
		];

		const orderItem: IOrderItem = {
			id: 100,
			name: 'Product1',
			options: '[]',
			productId: 1000,
			quantity: 8,
			replacedSkuId: 0,
			requestedDeliveryDate: '',
			settings: {
				maxQuantity: 10000,
				minQuantity: 1,
				multipleQuantity: 1,
			},
			shippingAddressId: 0,
			sku: 'SKU1',
			skuId: 1001,
			skuUnitOfMeasure: {} as any,
			thumbnail: '/o/commerce-media/default/?groupId=33472',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemRow
				deliveryGroups={deliveryGroups}
				disabled={true}
				handleSelection={jest.fn()}
				handleSubmit={handleSubmit}
				orderId={10}
				orderItem={orderItem as any}
			/>
		);

		const {deliveryGroup1Input, deliveryGroup2Input, row0Select} =
			getLocators(deliveryGroups, orderItem.id, renderedComponent);

		expect(row0Select).toBeInTheDocument();
		expect(row0Select).toBeDisabled();
		expect(deliveryGroup1Input).toBeInTheDocument();
		expect(deliveryGroup1Input).toBeDisabled();
		expect(deliveryGroup2Input).toBeInTheDocument();
		expect(deliveryGroup2Input).toBeDisabled();
	});

	it('Must hide actions if readonly', async () => {
		const deliveryGroups = [
			{
				addressId: 100,
				deliveryDate: '',
				id: 10000,
				name: 'DeliveryGroup1',
			},
			{
				addressId: 100,
				deliveryDate: '',
				id: 10001,
				name: 'DeliveryGroup2',
			},
		];

		const orderItem: IOrderItem = {
			id: 100,
			name: 'Product1',
			options: '[]',
			productId: 1000,
			quantity: 8,
			replacedSkuId: 0,
			requestedDeliveryDate: '',
			settings: {
				maxQuantity: 10000,
				minQuantity: 1,
				multipleQuantity: 1,
			},
			shippingAddressId: 0,
			sku: 'SKU1',
			skuId: 1001,
			skuUnitOfMeasure: {} as any,
			thumbnail: '/o/commerce-media/default/?groupId=33472',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemRow
				deliveryGroups={deliveryGroups}
				handleSelection={jest.fn()}
				handleSubmit={handleSubmit}
				orderId={10}
				orderItem={orderItem as any}
				readonly={true}
			/>
		);

		const {row0Actions, row0Select} = getLocators(
			deliveryGroups,
			orderItem.id,
			renderedComponent
		);

		expect(row0Actions).not.toBeInTheDocument();
		expect(row0Select).not.toBeInTheDocument();
	});

	it('Must display only first delivery group input field filled', async () => {
		const deliveryGroups = [
			{
				addressId: 100,
				deliveryDate: '',
				id: 10000,
				name: 'DeliveryGroup1',
			},
			{
				addressId: 100,
				deliveryDate: '',
				id: 10001,
				name: 'DeliveryGroup2',
			},
		];

		const orderItem: IOrderItem = {
			deliveryGroups: {
				10000: {
					options: '[]',
					orderItemId: 100,
					originalQuantity: 4,
					quantity: 4,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			},
			id: 100,
			name: 'Product1',
			options: '[]',
			productId: 1000,
			quantity: 4,
			replacedSkuId: 0,
			requestedDeliveryDate: '',
			settings: {
				maxQuantity: 10000,
				minQuantity: 1,
				multipleQuantity: 1,
			},
			shippingAddressId: 0,
			sku: 'SKU1',
			skuId: 1001,
			skuUnitOfMeasure: {} as any,
			thumbnail: '/o/commerce-media/default/?groupId=33472',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemRow
				deliveryGroups={deliveryGroups}
				handleSelection={jest.fn()}
				handleSubmit={handleSubmit}
				orderId={10}
				orderItem={orderItem as any}
			/>
		);

		const {deliveryGroup1Input, deliveryGroup2Input, quantityCell} =
			getLocators(deliveryGroups, orderItem.id, renderedComponent);

		expect(quantityCell).toHaveTextContent(String(orderItem.quantity));
		expect(deliveryGroup1Input).toBeInTheDocument();
		expect(deliveryGroup1Input).toHaveValue(4);
		expect(deliveryGroup2Input).toBeInTheDocument();
		expect(deliveryGroup2Input).not.toHaveValue();
	});

	it('Must display only second delivery group input field filled', async () => {
		const deliveryGroups = [
			{
				addressId: 100,
				deliveryDate: '',
				id: 10000,
				name: 'DeliveryGroup1',
			},
			{
				addressId: 100,
				deliveryDate: '',
				id: 10001,
				name: 'DeliveryGroup2',
			},
		];

		const orderItem: IOrderItem = {
			deliveryGroups: {
				10001: {
					options: '[]',
					orderItemId: 100,
					originalQuantity: 4,
					quantity: 4,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			},
			id: 100,
			name: 'Product1',
			options: '[]',
			productId: 1000,
			quantity: 4,
			replacedSkuId: 0,
			requestedDeliveryDate: '',
			settings: {
				maxQuantity: 10000,
				minQuantity: 1,
				multipleQuantity: 1,
			},
			shippingAddressId: 0,
			sku: 'SKU1',
			skuId: 1001,
			skuUnitOfMeasure: {} as any,
			thumbnail: '/o/commerce-media/default/?groupId=33472',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemRow
				deliveryGroups={deliveryGroups}
				handleSelection={jest.fn()}
				handleSubmit={handleSubmit}
				orderId={10}
				orderItem={orderItem as any}
			/>
		);

		const {deliveryGroup1Input, deliveryGroup2Input, quantityCell} =
			getLocators(deliveryGroups, orderItem.id, renderedComponent);

		expect(quantityCell).toHaveTextContent(String(orderItem.quantity));
		expect(deliveryGroup1Input).toBeInTheDocument();
		expect(deliveryGroup1Input).not.toHaveValue();
		expect(deliveryGroup2Input).toBeInTheDocument();
		expect(deliveryGroup2Input).toHaveValue(4);
	});

	it('Must display both delivery group input fields filled', async () => {
		const deliveryGroups = [
			{
				addressId: 100,
				deliveryDate: '',
				id: 10000,
				name: 'DeliveryGroup1',
			},
			{
				addressId: 100,
				deliveryDate: '',
				id: 10001,
				name: 'DeliveryGroup2',
			},
		];

		const orderItem: IOrderItem = {
			deliveryGroups: {
				10000: {
					options: '[]',
					orderItemId: 100,
					originalQuantity: 4,
					quantity: 4,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
				10001: {
					options: '[]',
					orderItemId: 101,
					originalQuantity: 4,
					quantity: 4,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			},
			id: 100,
			name: 'Product1',
			options: '[]',
			productId: 1000,
			quantity: 8,
			replacedSkuId: 0,
			requestedDeliveryDate: '',
			settings: {
				maxQuantity: 10000,
				minQuantity: 1,
				multipleQuantity: 1,
			},
			shippingAddressId: 0,
			sku: 'SKU1',
			skuId: 1001,
			skuUnitOfMeasure: {} as any,
			thumbnail: '/o/commerce-media/default/?groupId=33472',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemRow
				deliveryGroups={deliveryGroups}
				handleSelection={jest.fn()}
				handleSubmit={handleSubmit}
				orderId={10}
				orderItem={orderItem as any}
			/>
		);

		const {deliveryGroup1Input, deliveryGroup2Input, quantityCell} =
			getLocators(deliveryGroups, orderItem.id, renderedComponent);

		expect(quantityCell).toHaveTextContent(String(orderItem.quantity));
		expect(deliveryGroup1Input).toBeInTheDocument();
		expect(deliveryGroup1Input).toHaveValue(4);
		expect(deliveryGroup2Input).toBeInTheDocument();
		expect(deliveryGroup2Input).toHaveValue(4);
	});

	it('Must update quantity', async () => {
		fetchMock
			.patch(
				/headless-commerce-delivery-cart\/.*\/cart-items\/100/i,
				(): IOrderItem => {
					return {
						deliveryGroupName: '10000',
						options: '[]',
						quantity: 10,
						replacedSkuId: 0,
						requestedDeliveryDate: '',
						shippingAddressId: 100,
						skuId: 1001,
					} as IOrderItem;
				}
			)
			.patch(
				/headless-commerce-delivery-cart\/.*\/cart-items\/101/i,
				(): IOrderItem => {
					return {
						deliveryGroupName: '10001',
						options: '[]',
						quantity: 8,
						replacedSkuId: 0,
						requestedDeliveryDate: '',
						shippingAddressId: 100,
						skuId: 1001,
					} as IOrderItem;
				}
			);

		const handleSubmitWrapper = jest.fn((param: IOrderItem) => {
			handleSubmit(param.deliveryGroups);
		});

		const deliveryGroups = [
			{
				addressId: 100,
				deliveryDate: '',
				id: 10000,
				name: 'DeliveryGroupName1',
			},
			{
				addressId: 100,
				deliveryDate: '',
				id: 10001,
				name: 'DeliveryGroupName2',
			},
		];

		const orderItem: IOrderItem = {
			deliveryGroups: {
				10000: {
					options: '[]',
					orderItemId: 100,
					originalQuantity: 4,
					quantity: 4,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
				10001: {
					options: '[]',
					orderItemId: 101,
					originalQuantity: 4,
					quantity: 4,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			},
			id: 100,
			name: 'Product1',
			options: '[]',
			productId: 1000,
			quantity: 8,
			replacedSkuId: 0,
			requestedDeliveryDate: '',
			settings: {
				maxQuantity: 10000,
				minQuantity: 1,
				multipleQuantity: 1,
			},
			shippingAddressId: 0,
			sku: 'SKU1',
			skuId: 1001,
			skuUnitOfMeasure: {} as any,
			thumbnail: '/o/commerce-media/default/?groupId=33472',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemRow
				deliveryGroups={deliveryGroups}
				handleSelection={jest.fn()}
				handleSubmit={handleSubmitWrapper}
				orderId={10}
				orderItem={orderItem as any}
			/>
		);

		const {deliveryGroup1Input, deliveryGroup2Input, quantityCell} =
			getLocators(deliveryGroups, orderItem.id, renderedComponent);

		expect(quantityCell).toHaveTextContent(String(orderItem.quantity));
		expect(deliveryGroup1Input).toBeInTheDocument();
		expect(deliveryGroup1Input).toHaveValue(4);
		expect(deliveryGroup2Input).toBeInTheDocument();
		expect(deliveryGroup2Input).toHaveValue(4);

		await setFieldValue(deliveryGroup1Input, 10);

		expect(quantityCell).toHaveTextContent(String(14));

		await waitFor(() => {
			expect(handleSubmit).toBeCalledWith({
				10000: {
					options: '[]',
					orderItemId: 100,
					originalQuantity: 10,
					quantity: 10,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
				10001: {
					options: '[]',
					orderItemId: 101,
					originalQuantity: 4,
					quantity: 4,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			});
		});

		expect(fetchMock.calls().matched[0][1].body).toBe(
			'{"deliveryGroupName":"DeliveryGroupName1","options":"[]","quantity":10,"replacedSkuId":0,"requestedDeliveryDate":"","shippingAddressId":100,"skuId":1001,"skuUnitOfMeasure":{}}'
		);
		expect(fetchMock.calls().matched[0][1].method).toBe('PATCH');

		await setFieldValue(deliveryGroup2Input, 8);

		expect(quantityCell).toHaveTextContent(String(18));

		await waitFor(() => {
			expect(handleSubmit).toBeCalledWith({
				10000: {
					options: '[]',
					orderItemId: 100,
					originalQuantity: 10,
					quantity: 10,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
				10001: {
					options: '[]',
					orderItemId: 101,
					originalQuantity: 8,
					quantity: 8,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			});
		});

		expect(fetchMock.calls().matched[1][1].body).toBe(
			'{"deliveryGroupName":"DeliveryGroupName2","options":"[]","quantity":8,"replacedSkuId":0,"requestedDeliveryDate":"","shippingAddressId":100,"skuId":1001,"skuUnitOfMeasure":{}}'
		);
		expect(fetchMock.calls().matched[1][1].method).toBe('PATCH');
	});

	it('Must save a new delivery group', async () => {
		fetchMock
			.post(
				/headless-commerce-delivery-cart\/.*\/carts\/.*\/items/i,
				(): IOrderItem => {
					return {
						deliveryGroupName: '10001',
						id: 200,
						options: '[]',
						quantity: 20,
						replacedSkuId: 0,
						requestedDeliveryDate: '',
						shippingAddressId: 100,
						skuId: 1001,
					} as IOrderItem;
				}
			)
			.patch(
				/headless-commerce-delivery-cart\/.*\/cart-items\/200/i,
				(): IOrderItem => {
					return {
						deliveryGroupName: '10001',
						options: '[]',
						quantity: 8,
						replacedSkuId: 0,
						requestedDeliveryDate: '',
						shippingAddressId: 100,
						skuId: 1001,
					} as IOrderItem;
				}
			);

		const handleSubmitWrapper = jest.fn((param: IOrderItem) => {
			handleSubmit(param.deliveryGroups);
		});

		const deliveryGroups = [
			{
				addressId: 100,
				deliveryDate: '',
				id: 10000,
				name: 'DeliveryGroupName1',
			},
			{
				addressId: 100,
				deliveryDate: '',
				id: 10001,
				name: 'DeliveryGroupName2',
			},
		];

		const orderItem: IOrderItem = {
			deliveryGroups: {
				10000: {
					options: '[]',
					orderItemId: 100,
					originalQuantity: 4,
					quantity: 4,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			},
			id: 100,
			name: 'Product1',
			options: '[]',
			productId: 1000,
			quantity: 4,
			replacedSkuId: 0,
			requestedDeliveryDate: '',
			settings: {
				maxQuantity: 10000,
				minQuantity: 1,
				multipleQuantity: 1,
			},
			shippingAddressId: 0,
			sku: 'SKU1',
			skuId: 1001,
			skuUnitOfMeasure: {} as any,
			thumbnail: '/o/commerce-media/default/?groupId=33472',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemRow
				deliveryGroups={deliveryGroups}
				handleSelection={jest.fn()}
				handleSubmit={handleSubmitWrapper}
				orderId={10}
				orderItem={orderItem as any}
			/>
		);

		const {deliveryGroup2Input, quantityCell} = getLocators(
			deliveryGroups,
			orderItem.id,
			renderedComponent
		);

		expect(quantityCell).toHaveTextContent(String(orderItem.quantity));
		expect(deliveryGroup2Input).toBeInTheDocument();
		expect(deliveryGroup2Input).not.toHaveValue();

		await setFieldValue(deliveryGroup2Input, 20);

		expect(quantityCell).toHaveTextContent(String(24));

		await waitFor(() => {
			expect(handleSubmit).toBeCalledWith({
				10000: {
					options: '[]',
					orderItemId: 100,
					originalQuantity: 4,
					quantity: 4,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
				10001: {
					options: '[]',
					orderItemId: 200,
					originalQuantity: 20,
					quantity: 20,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			});
		});

		expect(fetchMock.calls().matched[0][1].body).toBe(
			'{"deliveryGroupName":"DeliveryGroupName2","options":"[]","quantity":20,"replacedSkuId":0,"requestedDeliveryDate":"","shippingAddressId":100,"skuId":1001,"skuUnitOfMeasure":{}}'
		);
		expect(fetchMock.calls().matched[0][1].method).toBe('POST');

		await setFieldValue(deliveryGroup2Input, 8);

		expect(quantityCell).toHaveTextContent(String(12));

		await waitFor(() => {
			expect(handleSubmit).toBeCalledWith({
				10000: {
					options: '[]',
					orderItemId: 100,
					originalQuantity: 4,
					quantity: 4,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
				10001: {
					options: '[]',
					orderItemId: 200,
					originalQuantity: 8,
					quantity: 8,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			});
		});

		expect(fetchMock.calls().matched[1][1].body).toBe(
			'{"deliveryGroupName":"DeliveryGroupName2","options":"[]","quantity":8,"replacedSkuId":0,"requestedDeliveryDate":"","shippingAddressId":100,"skuId":1001,"skuUnitOfMeasure":{}}'
		);
		expect(fetchMock.calls().matched[1][1].method).toBe('PATCH');
	});

	it('Must delete a new delivery group', async () => {
		fetchMock.delete(
			/headless-commerce-delivery-cart\/.*\/cart-items\/.*/i,
			JSON.stringify({})
		);

		const handleSubmitWrapper = jest.fn((param: IOrderItem) => {
			handleSubmit(param.deliveryGroups);
		});

		const deliveryGroups = [
			{
				addressId: 100,
				deliveryDate: '',
				id: 10000,
				name: 'DeliveryGroupName1',
			},
			{
				addressId: 100,
				deliveryDate: '',
				id: 10001,
				name: 'DeliveryGroupName2',
			},
		];

		const orderItem: IOrderItem = {
			deliveryGroups: {
				10000: {
					options: '[]',
					orderItemId: 100,
					originalQuantity: 4,
					quantity: 4,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
				10001: {
					options: '[]',
					orderItemId: 101,
					originalQuantity: 4,
					quantity: 4,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			},
			id: 100,
			name: 'Product1',
			options: '[]',
			productId: 1000,
			quantity: 8,
			replacedSkuId: 0,
			requestedDeliveryDate: '',
			settings: {
				maxQuantity: 10000,
				minQuantity: 1,
				multipleQuantity: 1,
			},
			shippingAddressId: 0,
			sku: 'SKU1',
			skuId: 1001,
			skuUnitOfMeasure: {} as any,
			thumbnail: '/o/commerce-media/default/?groupId=33472',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemRow
				deliveryGroups={deliveryGroups}
				handleSelection={jest.fn()}
				handleSubmit={handleSubmitWrapper}
				orderId={10}
				orderItem={orderItem as any}
			/>
		);

		const {deliveryGroup1Input, deliveryGroup2Input, quantityCell} =
			getLocators(deliveryGroups, orderItem.id, renderedComponent);

		expect(quantityCell).toHaveTextContent(String(orderItem.quantity));
		expect(deliveryGroup1Input).toBeInTheDocument();
		expect(deliveryGroup1Input).toHaveValue(4);
		expect(deliveryGroup2Input).toBeInTheDocument();
		expect(deliveryGroup2Input).toHaveValue(4);

		await setFieldValue(deliveryGroup1Input, 0);

		expect(quantityCell).toHaveTextContent(String(4));

		await waitFor(() => {
			expect(handleSubmit).toBeCalledWith({
				10001: {
					options: '[]',
					orderItemId: 101,
					originalQuantity: 4,
					quantity: 4,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			});
		});

		await setFieldValue(deliveryGroup2Input, 0);

		expect(quantityCell).toHaveTextContent(String(0));
		expect(fetchMock.calls().matched[0][1].method).toBe('DELETE');

		await waitFor(() => {
			expect(handleSubmit).toBeCalledWith({});
		});
	});

	it('Must open sku modal detail', async () => {
		const orderItem: IOrderItem = {
			id: 100,
			name: 'Product1',
			options: '[]',
			productId: 1000,
			quantity: 8,
			replacedSkuId: 0,
			requestedDeliveryDate: '',
			settings: {
				maxQuantity: 10000,
				minQuantity: 1,
				multipleQuantity: 1,
			},
			shippingAddressId: 0,
			sku: 'SKU1',
			skuId: 1001,
			skuUnitOfMeasure: {} as any,
			thumbnail: '/o/commerce-media/default/?groupId=33472',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemRow
				deliveryGroups={[]}
				handleSelection={jest.fn()}
				handleSubmit={handleSubmit}
				orderId={10}
				orderItem={orderItem as any}
			/>
		);

		const skuName = renderedComponent.getByText(orderItem.sku as string);

		await act(async () => {
			skuName.click();
		});

		await waitFor(() => {
			expect(
				renderedComponent.getByText(`view ${orderItem.sku} details`)
			).toBeVisible();
		});
	});

	it('Must show quantity select', async () => {
		fetchMock
			.delete(
				/headless-commerce-delivery-cart\/.*\/cart-items\/.*/i,
				JSON.stringify({})
			)
			.post(
				/headless-commerce-delivery-cart\/.*\/carts\/.*\/items/i,
				(): IOrderItem => {
					return {
						deliveryGroupName: '10000',
						id: 300,
						options: '[]',
						quantity: 6,
						replacedSkuId: 0,
						requestedDeliveryDate: '',
						shippingAddressId: 100,
						skuId: 1001,
					} as IOrderItem;
				}
			);

		const handleSubmitWrapper = jest.fn((param: IOrderItem) => {
			handleSubmit(param.deliveryGroups);
		});

		const deliveryGroups = [
			{
				addressId: 100,
				deliveryDate: '',
				id: 10000,
				name: 'DeliveryGroupName1',
			},
		];

		const orderItem: IOrderItem = {
			deliveryGroupName: 'DeliveryGroupName1',
			deliveryGroups: {
				10000: {
					options: '[]',
					orderItemId: 100,
					originalQuantity: 4,
					quantity: 4,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			},
			id: 100,
			name: 'Product1',
			options: '[]',
			productId: 1000,
			quantity: 4,
			replacedSkuId: 0,
			requestedDeliveryDate: '',
			settings: {
				allowedQuantities: [2, 3, 4],
				maxQuantity: 10000,
				minQuantity: 1,
				multipleQuantity: 1,
			},
			shippingAddressId: 100,
			sku: 'SKU1',
			skuId: 1001,
			skuUnitOfMeasure: {} as any,
			thumbnail: '/o/commerce-media/default/?groupId=33472',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemRow
				deliveryGroups={deliveryGroups}
				handleSelection={jest.fn()}
				handleSubmit={handleSubmitWrapper}
				orderId={10}
				orderItem={orderItem as any}
			/>
		);

		const {deliveryGroup1Input, quantityCell} = getLocators(
			deliveryGroups,
			orderItem.id,
			renderedComponent
		);

		expect(quantityCell).toHaveTextContent(String(orderItem.quantity));
		expect(deliveryGroup1Input).toBeInTheDocument();
		expect(deliveryGroup1Input).toHaveValue(String(4));

		await setFieldValue(deliveryGroup1Input, '');

		await waitFor(() => {
			expect(handleSubmit).toBeCalledWith({});
		});

		expect(quantityCell).toHaveTextContent(String(0));

		await setFieldValue(deliveryGroup1Input, String(3));

		expect(quantityCell).toHaveTextContent(String(3));

		await waitFor(() => {
			expect(quantityCell).toHaveTextContent(String(3));
		});
	});

	it('Must preload checkbox', async () => {
		const deliveryGroups = [
			{
				addressId: 100,
				deliveryDate: '',
				id: 10000,
				name: 'DeliveryGroupName1',
			},
		];

		const orderItem: IOrderItem = {
			deliveryGroupName: 'DeliveryGroupName1',
			deliveryGroups: {
				10000: {
					options: '[]',
					orderItemId: 100,
					originalQuantity: 4,
					quantity: 4,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			},
			id: 100,
			name: 'Product1',
			options: '[]',
			productId: 1000,
			quantity: 4,
			replacedSkuId: 0,
			requestedDeliveryDate: '',
			settings: {
				maxQuantity: 10000,
				minQuantity: 1,
				multipleQuantity: 1,
			},
			shippingAddressId: 100,
			sku: 'SKU1',
			skuId: 1001,
			skuUnitOfMeasure: {} as any,
			thumbnail: '/o/commerce-media/default/?groupId=33472',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemRow
				checked={true}
				deliveryGroups={deliveryGroups}
				handleSelection={jest.fn()}
				handleSubmit={jest.fn()}
				orderId={10}
				orderItem={orderItem as any}
			/>
		);

		const {row0Select} = getLocators(
			deliveryGroups,
			orderItem.id,
			renderedComponent
		);

		expect(row0Select).toBeChecked();
		expect(row0Select).toBeEnabled();
	});

	it('Must propagate the selection status', async () => {
		const handleSelection = jest.fn(() => {});

		const deliveryGroups = [
			{
				addressId: 100,
				deliveryDate: '',
				id: 10000,
				name: 'DeliveryGroupName1',
			},
		];

		const orderItem: IOrderItem = {
			deliveryGroupName: 'DeliveryGroupName1',
			deliveryGroups: {
				10000: {
					options: '[]',
					orderItemId: 100,
					originalQuantity: 4,
					quantity: 4,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			},
			id: 100,
			name: 'Product1',
			options: '[]',
			productId: 1000,
			quantity: 4,
			replacedSkuId: 0,
			requestedDeliveryDate: '',
			settings: {
				maxQuantity: 10000,
				minQuantity: 1,
				multipleQuantity: 1,
			},
			shippingAddressId: 100,
			sku: 'SKU1',
			skuId: 1001,
			skuUnitOfMeasure: {} as any,
			thumbnail: '/o/commerce-media/default/?groupId=33472',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemRow
				deliveryGroups={deliveryGroups}
				handleSelection={handleSelection}
				handleSubmit={jest.fn()}
				orderId={10}
				orderItem={orderItem as any}
			/>
		);

		const {row0Select} = getLocators(
			deliveryGroups,
			orderItem.id,
			renderedComponent
		);

		expect(row0Select).not.toBeChecked();

		userEvent.click(row0Select);

		expect(handleSelection).toBeCalledWith(100);
		expect(row0Select).toBeChecked();

		userEvent.click(row0Select);

		expect(handleSelection).toBeCalledWith(100);
		expect(row0Select).not.toBeChecked();
	});
});

describe('OrderItemRow - actions', () => {
	const handleSubmit = jest.fn();

	beforeEach(() => {
		(window as any).Liferay = {
			...(window as any).Liferay,
			CustomDialogs: {},
		};

		fetchMock.mock('*', JSON.stringify({}));
	});

	afterEach(() => {
		fetchMock.restore();
		jest.restoreAllMocks();
		jest.clearAllMocks();

		cleanup();
	});

	it('Must display row actions', async () => {
		const deliveryGroups = [
			{
				addressId: 100,
				deliveryDate: '',
				id: 10000,
				name: 'DeliveryGroupName1',
			},
			{
				addressId: 100,
				deliveryDate: '',
				id: 10001,
				name: 'DeliveryGroupName2',
			},
		];

		const orderItem: IOrderItem = {
			deliveryGroups: {
				10000: {
					options: '[]',
					orderItemId: 100,
					originalQuantity: 4,
					quantity: 4,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			},
			id: 100,
			name: 'Product1',
			options: '[]',
			productId: 1000,
			quantity: 4,
			replacedSkuId: 0,
			requestedDeliveryDate: '',
			settings: {
				maxQuantity: 10000,
				minQuantity: 1,
				multipleQuantity: 1,
			},
			shippingAddressId: 0,
			sku: 'SKU1',
			skuId: 1001,
			skuUnitOfMeasure: {} as any,
			thumbnail: '/o/commerce-media/default/?groupId=33472',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemRow
				deliveryGroups={deliveryGroups}
				handleSelection={jest.fn()}
				handleSubmit={handleSubmit}
				orderId={10}
				orderItem={orderItem as any}
			/>
		);

		const {
			quantityCell,
			row0Actions,
			row0CopyColumnAction,
			row0RemoveItemAction,
			row0ResetRowAction,
			row0SplitQuantityAction,
		} = getLocators(deliveryGroups, orderItem.id, renderedComponent);

		expect(quantityCell).toHaveTextContent(String(orderItem.quantity));
		expect(row0Actions).toBeVisible();

		await act(async () => {
			row0Actions.click();
		});

		await waitFor(() => {
			expect(row0CopyColumnAction).toBeVisible();
			expect(row0CopyColumnAction).toBeEnabled();
			expect(row0RemoveItemAction).toBeVisible();
			expect(row0RemoveItemAction).toBeEnabled();
			expect(row0ResetRowAction).toBeVisible();
			expect(row0ResetRowAction).toBeEnabled();
			expect(row0SplitQuantityAction).toBeVisible();
			expect(row0SplitQuantityAction).toBeEnabled();
		});
	});

	it('Must display actions disabled with no delivery group', async () => {
		const deliveryGroups: Array<IDeliveryGroup> = [];

		const orderItem: IOrderItem = {
			id: 100,
			name: 'Product1',
			options: '[]',
			productId: 1000,
			quantity: 4,
			replacedSkuId: 0,
			requestedDeliveryDate: '',
			settings: {
				maxQuantity: 10000,
				minQuantity: 1,
				multipleQuantity: 1,
			},
			shippingAddressId: 0,
			sku: 'SKU1',
			skuId: 1001,
			skuUnitOfMeasure: {} as any,
			thumbnail: '/o/commerce-media/default/?groupId=33472',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemRow
				deliveryGroups={deliveryGroups}
				handleSelection={jest.fn()}
				handleSubmit={handleSubmit}
				orderId={10}
				orderItem={orderItem as any}
			/>
		);

		const {
			quantityCell,
			row0Actions,
			row0CopyColumnAction,
			row0RemoveItemAction,
			row0ResetRowAction,
			row0SplitQuantityAction,
		} = getLocators(deliveryGroups, orderItem.id, renderedComponent);

		expect(quantityCell).toHaveTextContent(String(orderItem.quantity));
		expect(row0Actions).toBeVisible();

		await act(async () => {
			row0Actions.click();
		});

		await waitFor(() => {
			expect(row0CopyColumnAction).toBeVisible();
			expect(row0CopyColumnAction).toBeDisabled();
			expect(row0RemoveItemAction).toBeVisible();
			expect(row0RemoveItemAction).toBeEnabled();
			expect(row0ResetRowAction).toBeVisible();
			expect(row0ResetRowAction).toBeDisabled();
			expect(row0SplitQuantityAction).toBeVisible();
			expect(row0SplitQuantityAction).toBeDisabled();
		});
	});

	it('Must display actions disabled with one delivery group', async () => {
		const deliveryGroups = [
			{
				addressId: 100,
				deliveryDate: '',
				id: 10000,
				name: 'DeliveryGroupName1',
			},
		];

		const orderItem: IOrderItem = {
			deliveryGroups: {
				10000: {
					options: '[]',
					orderItemId: 100,
					originalQuantity: 4,
					quantity: 4,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			},
			id: 100,
			name: 'Product1',
			options: '[]',
			productId: 1000,
			quantity: 4,
			replacedSkuId: 0,
			requestedDeliveryDate: '',
			settings: {
				maxQuantity: 10000,
				minQuantity: 1,
				multipleQuantity: 1,
			},
			shippingAddressId: 0,
			sku: 'SKU1',
			skuId: 1001,
			skuUnitOfMeasure: {} as any,
			thumbnail: '/o/commerce-media/default/?groupId=33472',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemRow
				deliveryGroups={deliveryGroups}
				handleSelection={jest.fn()}
				handleSubmit={handleSubmit}
				orderId={10}
				orderItem={orderItem as any}
			/>
		);

		const {
			quantityCell,
			row0Actions,
			row0CopyColumnAction,
			row0RemoveItemAction,
			row0ResetRowAction,
			row0SplitQuantityAction,
		} = getLocators(deliveryGroups, orderItem.id, renderedComponent);

		expect(quantityCell).toHaveTextContent(String(orderItem.quantity));
		expect(row0Actions).toBeVisible();

		await act(async () => {
			row0Actions.click();
		});

		await waitFor(() => {
			expect(row0CopyColumnAction).toBeVisible();
			expect(row0CopyColumnAction).toBeDisabled();
			expect(row0RemoveItemAction).toBeVisible();
			expect(row0RemoveItemAction).toBeEnabled();
			expect(row0ResetRowAction).toBeVisible();
			expect(row0ResetRowAction).toBeEnabled();
			expect(row0SplitQuantityAction).toBeVisible();
			expect(row0SplitQuantityAction).toBeDisabled();
		});
	});

	it('Must reset the quantities', async () => {
		jest.spyOn(window, 'confirm')
			.mockImplementationOnce(() => false)
			.mockImplementation(() => true);

		const handleSubmitWrapper = jest.fn((param: IOrderItem) => {
			handleSubmit(param.deliveryGroups);
		});

		const deliveryGroups = [
			{
				addressId: 100,
				deliveryDate: '',
				id: 10000,
				name: 'DeliveryGroupName1',
			},
			{
				addressId: 100,
				deliveryDate: '',
				id: 10001,
				name: 'DeliveryGroupName2',
			},
		];

		const orderItem: IOrderItem = {
			deliveryGroups: {
				10000: {
					options: '[]',
					orderItemId: 100,
					originalQuantity: 4,
					quantity: 4,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
				10001: {
					options: '[]',
					orderItemId: 101,
					originalQuantity: 5,
					quantity: 5,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			},
			id: 100,
			name: 'Product1',
			options: '[]',
			productId: 1000,
			quantity: 9,
			replacedSkuId: 0,
			requestedDeliveryDate: '',
			settings: {
				maxQuantity: 10000,
				minQuantity: 2,
				multipleQuantity: 1,
			},
			shippingAddressId: 0,
			sku: 'SKU1',
			skuId: 1001,
			skuUnitOfMeasure: {} as any,
			thumbnail: '/o/commerce-media/default/?groupId=33472',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemRow
				deliveryGroups={deliveryGroups}
				handleSelection={jest.fn()}
				handleSubmit={handleSubmitWrapper}
				orderId={10}
				orderItem={orderItem as any}
			/>
		);

		const {
			deliveryGroup1Input,
			deliveryGroup2Input,
			quantityCell,
			row0Actions,
			row0ResetRowAction,
		} = getLocators(deliveryGroups, orderItem.id, renderedComponent);

		expect(quantityCell).toHaveTextContent(String(orderItem.quantity));
		expect(deliveryGroup1Input).toHaveValue(4);
		expect(deliveryGroup2Input).toHaveValue(5);
		expect(row0Actions).toBeVisible();

		await act(async () => {
			row0Actions.click();
		});

		await waitFor(() => {
			expect(row0ResetRowAction).toBeVisible();
			expect(row0ResetRowAction).toBeEnabled();
		});

		await act(async () => {
			row0ResetRowAction.click();
		});

		await waitFor(() => {
			expect(quantityCell).toBeVisible();
			expect(quantityCell).toHaveTextContent(String(9));
			expect(deliveryGroup1Input).toHaveValue(4);
			expect(deliveryGroup2Input).toHaveValue(5);
		});

		expect(handleSubmit).not.toBeCalled();

		await act(async () => {
			row0Actions.click();
		});

		await waitFor(() => {
			expect(row0ResetRowAction).toBeVisible();
			expect(row0ResetRowAction).toBeEnabled();
		});

		await act(async () => {
			row0ResetRowAction.click();
		});

		await waitFor(() => {
			expect(quantityCell).toBeVisible();
			expect(quantityCell).toHaveTextContent(
				String(orderItem.settings?.minQuantity)
			);
			expect(deliveryGroup1Input).toHaveValue(
				orderItem.settings?.minQuantity
			);
			expect(deliveryGroup2Input).not.toHaveValue();

			expect(handleSubmit).toBeCalledWith({
				10000: {
					options: '[]',
					orderItemId: 100,
					originalQuantity: 4,
					quantity: 2,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
				10001: {
					options: '[]',
					orderItemId: 101,
					originalQuantity: 5,
					quantity: 0,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			});
		});
	});

	it('Must reset the quantities if not primary delivery group', async () => {
		jest.spyOn(window, 'confirm').mockImplementation(() => true);

		const handleSubmitWrapper = jest.fn((param: IOrderItem) => {
			handleSubmit(param.deliveryGroups);
		});

		const deliveryGroups = [
			{
				addressId: 100,
				deliveryDate: '',
				id: 10000,
				name: 'DeliveryGroupName1',
			},
			{
				addressId: 100,
				deliveryDate: '',
				id: 10001,
				name: 'DeliveryGroupName2',
			},
		];

		const orderItem: IOrderItem = {
			deliveryGroups: {
				10001: {
					options: '[]',
					orderItemId: 101,
					originalQuantity: 5,
					quantity: 5,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			},
			id: 100,
			name: 'Product1',
			options: '[]',
			productId: 1000,
			quantity: 5,
			replacedSkuId: 0,
			requestedDeliveryDate: '',
			settings: {
				maxQuantity: 10000,
				minQuantity: 2,
				multipleQuantity: 1,
			},
			shippingAddressId: 0,
			sku: 'SKU1',
			skuId: 1001,
			skuUnitOfMeasure: {} as any,
			thumbnail: '/o/commerce-media/default/?groupId=33472',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemRow
				deliveryGroups={deliveryGroups}
				handleSelection={jest.fn()}
				handleSubmit={handleSubmitWrapper}
				orderId={10}
				orderItem={orderItem as any}
			/>
		);

		const {
			deliveryGroup1Input,
			deliveryGroup2Input,
			quantityCell,
			row0Actions,
			row0ResetRowAction,
		} = getLocators(deliveryGroups, orderItem.id, renderedComponent);

		expect(quantityCell).toHaveTextContent(String(orderItem.quantity));
		expect(deliveryGroup1Input).not.toHaveValue();
		expect(deliveryGroup2Input).toHaveValue(5);
		expect(row0Actions).toBeVisible();

		await act(async () => {
			row0Actions.click();
		});

		await waitFor(() => {
			expect(row0ResetRowAction).toBeVisible();
			expect(row0ResetRowAction).toBeEnabled();
		});

		await act(async () => {
			row0ResetRowAction.click();
		});

		await waitFor(() => {
			expect(quantityCell).toBeVisible();
			expect(quantityCell).toHaveTextContent(
				String(orderItem.settings?.minQuantity)
			);
			expect(deliveryGroup1Input).toHaveValue(
				orderItem.settings?.minQuantity
			);
			expect(deliveryGroup2Input).not.toHaveValue();

			expect(handleSubmit).toBeCalledWith({
				10000: {
					options: '[]',
					orderItemId: 0,
					originalQuantity: 2,
					quantity: 2,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
				10001: {
					options: '[]',
					orderItemId: 101,
					originalQuantity: 5,
					quantity: 0,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			});
		});
	});

	it('Must copy the quantities', async () => {
		const handleSubmitWrapper = jest.fn((param: IOrderItem) => {
			handleSubmit(param.deliveryGroups);
		});

		const deliveryGroups = [
			{
				addressId: 100,
				deliveryDate: '',
				id: 10000,
				name: 'DeliveryGroupName1',
			},
			{
				addressId: 100,
				deliveryDate: '',
				id: 10001,
				name: 'DeliveryGroupName2',
			},
		];

		const orderItem: IOrderItem = {
			deliveryGroups: {
				10000: {
					options: '[]',
					orderItemId: 100,
					originalQuantity: 4,
					quantity: 4,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
				10001: {
					options: '[]',
					orderItemId: 101,
					originalQuantity: 5,
					quantity: 5,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			},
			id: 100,
			name: 'Product1',
			options: '[]',
			productId: 1000,
			quantity: 9,
			replacedSkuId: 0,
			requestedDeliveryDate: '',
			settings: {
				maxQuantity: 10000,
				minQuantity: 2,
				multipleQuantity: 1,
			},
			shippingAddressId: 0,
			sku: 'SKU1',
			skuId: 1001,
			skuUnitOfMeasure: {} as any,
			thumbnail: '/o/commerce-media/default/?groupId=33472',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemRow
				deliveryGroups={deliveryGroups}
				handleSelection={jest.fn()}
				handleSubmit={handleSubmitWrapper}
				orderId={10}
				orderItem={orderItem as any}
			/>
		);

		const {
			deliveryGroup1Input,
			deliveryGroup2Input,
			quantityCell,
			row0Actions,
			row0CopyColumnAction,
		} = getLocators(deliveryGroups, orderItem.id, renderedComponent);

		expect(quantityCell).toHaveTextContent(String(orderItem.quantity));
		expect(deliveryGroup1Input).toHaveValue(4);
		expect(deliveryGroup2Input).toHaveValue(5);
		expect(row0Actions).toBeVisible();

		await act(async () => {
			row0Actions.click();
		});

		await waitFor(() => {
			expect(row0CopyColumnAction).toBeVisible();
			expect(row0CopyColumnAction).toBeEnabled();
		});

		await act(async () => {
			row0CopyColumnAction.click();
		});

		await waitFor(() => {
			expect(handleSubmit).toBeCalledWith({
				10000: {
					options: '[]',
					orderItemId: 100,
					originalQuantity: 4,
					quantity: 4,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
				10001: {
					options: '[]',
					orderItemId: 101,
					originalQuantity: 5,
					quantity: 4,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			});

			expect(quantityCell).toBeVisible();
			expect(quantityCell).toHaveTextContent(String(8));
			expect(deliveryGroup1Input).toHaveValue(4);
			expect(deliveryGroup2Input).toHaveValue(4);
		});
	});

	it('Must copy be disabled if no primary delivery group', async () => {
		const deliveryGroups = [
			{
				addressId: 100,
				deliveryDate: '',
				id: 10000,
				name: 'DeliveryGroupName1',
			},
			{
				addressId: 100,
				deliveryDate: '',
				id: 10001,
				name: 'DeliveryGroupName2',
			},
		];

		const orderItem: IOrderItem = {
			deliveryGroups: {
				10001: {
					options: '[]',
					orderItemId: 101,
					originalQuantity: 5,
					quantity: 5,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			},
			id: 100,
			name: 'Product1',
			options: '[]',
			productId: 1000,
			quantity: 5,
			replacedSkuId: 0,
			requestedDeliveryDate: '',
			settings: {
				maxQuantity: 10000,
				minQuantity: 2,
				multipleQuantity: 1,
			},
			shippingAddressId: 0,
			sku: 'SKU1',
			skuId: 1001,
			skuUnitOfMeasure: {} as any,
			thumbnail: '/o/commerce-media/default/?groupId=33472',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemRow
				deliveryGroups={deliveryGroups}
				handleSelection={jest.fn()}
				handleSubmit={handleSubmit}
				orderId={10}
				orderItem={orderItem as any}
			/>
		);

		const {
			deliveryGroup1Input,
			deliveryGroup2Input,
			quantityCell,
			row0Actions,
		} = getLocators(deliveryGroups, orderItem.id, renderedComponent);

		expect(quantityCell).toHaveTextContent(String(orderItem.quantity));
		expect(deliveryGroup1Input).not.toHaveValue();
		expect(deliveryGroup2Input).toHaveValue(5);
		expect(row0Actions).toBeVisible();

		await act(async () => {
			row0Actions.click();
		});

		await waitFor(() => {
			const {row0CopyColumnAction} = getLocators(
				deliveryGroups,
				orderItem.id,
				renderedComponent
			);

			expect(row0CopyColumnAction).toBeVisible();
			expect(row0CopyColumnAction).toBeDisabled();
		});
	});

	it('Must copy be disabled if too much quantity', async () => {
		const deliveryGroups = [
			{
				addressId: 100,
				deliveryDate: '',
				id: 10000,
				name: 'DeliveryGroupName1',
			},
			{
				addressId: 100,
				deliveryDate: '',
				id: 10001,
				name: 'DeliveryGroupName2',
			},
			{
				addressId: 100,
				deliveryDate: '',
				id: 10002,
				name: 'DeliveryGroupName3',
			},
		];

		const orderItem: IOrderItem = {
			deliveryGroups: {
				10000: {
					options: '[]',
					orderItemId: 100,
					originalQuantity: 2,
					quantity: 10,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			},
			id: 100,
			name: 'Product1',
			options: '[]',
			productId: 1000,
			quantity: 10,
			replacedSkuId: 0,
			requestedDeliveryDate: '',
			settings: {
				maxQuantity: 20,
				minQuantity: 2,
				multipleQuantity: 1,
			},
			shippingAddressId: 0,
			sku: 'SKU1',
			skuId: 1001,
			skuUnitOfMeasure: {} as any,
			thumbnail: '/o/commerce-media/default/?groupId=33472',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemRow
				deliveryGroups={deliveryGroups}
				handleSelection={jest.fn()}
				handleSubmit={handleSubmit}
				orderId={10}
				orderItem={orderItem as any}
			/>
		);

		const {
			deliveryGroup1Input,
			deliveryGroup2Input,
			quantityCell,
			row0Actions,
		} = getLocators(deliveryGroups, orderItem.id, renderedComponent);

		expect(quantityCell).toHaveTextContent(String(orderItem.quantity));
		expect(deliveryGroup1Input).toHaveValue(10);
		expect(deliveryGroup2Input).not.toHaveValue();
		expect(row0Actions).toBeVisible();

		await act(async () => {
			row0Actions.click();
		});

		await waitFor(() => {
			const {row0CopyColumnAction} = getLocators(
				deliveryGroups,
				orderItem.id,
				renderedComponent
			);

			expect(row0CopyColumnAction).toBeVisible();
			expect(row0CopyColumnAction).toBeDisabled();
		});
	});

	it('Must delete the order item', async () => {
		jest.spyOn(window, 'confirm')
			.mockImplementationOnce(() => false)
			.mockImplementation(() => true);

		const handleSubmitWrapper = jest.fn((param: IOrderItem) => {
			handleSubmit(param.deliveryGroups);
		});

		const deliveryGroups = [
			{
				addressId: 100,
				deliveryDate: '',
				id: 10000,
				name: 'DeliveryGroupName1',
			},
			{
				addressId: 100,
				deliveryDate: '',
				id: 10001,
				name: 'DeliveryGroupName2',
			},
		];

		const orderItem: IOrderItem = {
			deliveryGroups: {
				10000: {
					options: '[]',
					orderItemId: 100,
					originalQuantity: 4,
					quantity: 4,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
				10001: {
					options: '[]',
					orderItemId: 101,
					originalQuantity: 5,
					quantity: 5,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			},
			id: 100,
			name: 'Product1',
			options: '[]',
			productId: 1000,
			quantity: 9,
			replacedSkuId: 0,
			requestedDeliveryDate: '',
			settings: {
				maxQuantity: 10000,
				minQuantity: 2,
				multipleQuantity: 1,
			},
			shippingAddressId: 0,
			sku: 'SKU1',
			skuId: 1001,
			skuUnitOfMeasure: {} as any,
			thumbnail: '/o/commerce-media/default/?groupId=33472',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemRow
				deliveryGroups={deliveryGroups}
				handleSelection={jest.fn()}
				handleSubmit={handleSubmitWrapper}
				orderId={10}
				orderItem={orderItem as any}
			/>
		);

		const {
			deliveryGroup1Input,
			deliveryGroup2Input,
			quantityCell,
			row0Actions,
			row0RemoveItemAction,
		} = getLocators(deliveryGroups, orderItem.id, renderedComponent);

		expect(quantityCell).toHaveTextContent(String(orderItem.quantity));
		expect(deliveryGroup1Input).toHaveValue(4);
		expect(deliveryGroup2Input).toHaveValue(5);
		expect(row0Actions).toBeVisible();

		await act(async () => {
			row0Actions.click();
		});

		await waitFor(() => {
			expect(row0RemoveItemAction).toBeVisible();
			expect(row0RemoveItemAction).toBeEnabled();
		});

		await act(async () => {
			row0RemoveItemAction.click();
		});

		await waitFor(() => {
			expect(quantityCell).toBeVisible();
			expect(quantityCell).toHaveTextContent(String(9));
			expect(deliveryGroup1Input).toHaveValue(4);
			expect(deliveryGroup2Input).toHaveValue(5);
		});

		expect(handleSubmit).not.toBeCalled();

		await act(async () => {
			row0Actions.click();
		});

		await waitFor(() => {
			expect(row0RemoveItemAction).toBeVisible();
			expect(row0RemoveItemAction).toBeEnabled();
		});

		await act(async () => {
			row0RemoveItemAction.click();
		});

		await waitFor(() => {
			expect(quantityCell).toBeVisible();
			expect(quantityCell).toHaveTextContent(String(0));
			expect(deliveryGroup1Input).not.toHaveValue();
			expect(deliveryGroup2Input).not.toHaveValue();

			expect(handleSubmit).toBeCalledWith({
				10000: {
					options: '[]',
					orderItemId: 100,
					originalQuantity: 4,
					quantity: 0,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
				10001: {
					options: '[]',
					orderItemId: 101,
					originalQuantity: 5,
					quantity: 0,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			});
		});
	});

	it('Must delete the order item also without delivery groups', async () => {
		jest.spyOn(window, 'confirm').mockImplementation(() => true);

		const deliveryGroups: Array<IDeliveryGroup> = [];

		const orderItem: IOrderItem = {
			id: 100,
			name: 'Product1',
			options: '[]',
			productId: 1000,
			quantity: 9,
			replacedSkuId: 0,
			requestedDeliveryDate: '',
			settings: {
				maxQuantity: 10000,
				minQuantity: 2,
				multipleQuantity: 1,
			},
			shippingAddressId: 0,
			sku: 'SKU1',
			skuId: 1001,
			skuUnitOfMeasure: {} as any,
			thumbnail: '/o/commerce-media/default/?groupId=33472',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemRow
				deliveryGroups={deliveryGroups}
				handleSelection={jest.fn()}
				handleSubmit={handleSubmit}
				orderId={10}
				orderItem={orderItem as any}
			/>
		);

		const {quantityCell, row0Actions, row0RemoveItemAction} = getLocators(
			deliveryGroups,
			orderItem.id,
			renderedComponent
		);

		expect(quantityCell).toHaveTextContent(String(orderItem.quantity));
		expect(row0Actions).toBeVisible();

		await act(async () => {
			row0Actions.click();
		});

		await waitFor(() => {
			expect(row0RemoveItemAction).toBeVisible();
			expect(row0RemoveItemAction).toBeEnabled();
		});

		await act(async () => {
			row0RemoveItemAction.click();
		});

		await waitFor(() => {
			expect(quantityCell).toBeVisible();
			expect(quantityCell).toHaveTextContent(String(0));

			expect(handleSubmit).toBeCalledWith(
				{
					...orderItem,
					deliveryGroups: {},
					quantity: 0,
				},
				true
			);
		});
	});

	it('Must split be disabled if not enough quantity', async () => {
		const deliveryGroups = [
			{
				addressId: 100,
				deliveryDate: '',
				id: 10000,
				name: 'DeliveryGroupName1',
			},
			{
				addressId: 100,
				deliveryDate: '',
				id: 10001,
				name: 'DeliveryGroupName2',
			},
			{
				addressId: 100,
				deliveryDate: '',
				id: 10002,
				name: 'DeliveryGroupName3',
			},
		];

		const orderItem: IOrderItem = {
			deliveryGroups: {
				10000: {
					options: '[]',
					orderItemId: 100,
					originalQuantity: 2,
					quantity: 3,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			},
			id: 100,
			name: 'Product1',
			options: '[]',
			productId: 1000,
			quantity: 3,
			replacedSkuId: 0,
			requestedDeliveryDate: '',
			settings: {
				maxQuantity: 10000,
				minQuantity: 2,
				multipleQuantity: 1,
			},
			shippingAddressId: 0,
			sku: 'SKU1',
			skuId: 1001,
			skuUnitOfMeasure: {} as any,
			thumbnail: '/o/commerce-media/default/?groupId=33472',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemRow
				deliveryGroups={deliveryGroups}
				handleSelection={jest.fn()}
				handleSubmit={handleSubmit}
				orderId={10}
				orderItem={orderItem as any}
			/>
		);

		const {
			deliveryGroup1Input,
			deliveryGroup2Input,
			quantityCell,
			row0Actions,
			row0SplitQuantityAction,
		} = getLocators(deliveryGroups, orderItem.id, renderedComponent);

		expect(quantityCell).toHaveTextContent(String(orderItem.quantity));
		expect(deliveryGroup1Input).toHaveValue(3);
		expect(deliveryGroup2Input).not.toHaveValue();
		expect(row0Actions).toBeVisible();

		await act(async () => {
			row0Actions.click();
		});

		await waitFor(() => {
			expect(row0SplitQuantityAction).toBeVisible();
			expect(row0SplitQuantityAction).toBeDisabled();
		});
	});

	it('Must split be disabled if allowed quantities provided', async () => {
		const deliveryGroups = [
			{
				addressId: 100,
				deliveryDate: '',
				id: 10000,
				name: 'DeliveryGroupName1',
			},
			{
				addressId: 100,
				deliveryDate: '',
				id: 10001,
				name: 'DeliveryGroupName2',
			},
		];

		const orderItem: IOrderItem = {
			deliveryGroups: {
				10000: {
					options: '[]',
					orderItemId: 100,
					originalQuantity: 10,
					quantity: 10,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			},
			id: 100,
			name: 'Product1',
			options: '[]',
			productId: 1000,
			quantity: 10,
			replacedSkuId: 0,
			requestedDeliveryDate: '',
			settings: {
				allowedQuantities: [2, 3, 4, 10],
				maxQuantity: 10000,
				minQuantity: 2,
				multipleQuantity: 1,
			},
			shippingAddressId: 0,
			sku: 'SKU1',
			skuId: 1001,
			skuUnitOfMeasure: {} as any,
			thumbnail: '/o/commerce-media/default/?groupId=33472',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemRow
				deliveryGroups={deliveryGroups}
				handleSelection={jest.fn()}
				handleSubmit={handleSubmit}
				orderId={10}
				orderItem={orderItem as any}
			/>
		);

		const {
			deliveryGroup1Input,
			deliveryGroup2Input,
			quantityCell,
			row0Actions,
			row0SplitQuantityAction,
		} = getLocators(deliveryGroups, orderItem.id, renderedComponent);

		expect(quantityCell).toHaveTextContent(String(orderItem.quantity));
		expect(deliveryGroup1Input).toHaveValue(String(10));
		expect(deliveryGroup2Input).not.toHaveValue();
		expect(row0Actions).toBeVisible();

		await act(async () => {
			row0Actions.click();
		});

		await waitFor(() => {
			expect(row0SplitQuantityAction).toBeVisible();
			expect(row0SplitQuantityAction).toBeDisabled();
		});
	});

	it('Must split the quantities', async () => {
		jest.spyOn(window, 'confirm')
			.mockImplementationOnce(() => false)
			.mockImplementation(() => true);

		const handleSubmitWrapper = jest.fn((param: IOrderItem) => {
			handleSubmit(param.deliveryGroups);
		});

		const deliveryGroups = [
			{
				addressId: 100,
				deliveryDate: '',
				id: 10000,
				name: 'DeliveryGroupName1',
			},
			{
				addressId: 100,
				deliveryDate: '',
				id: 10001,
				name: 'DeliveryGroupName2',
			},
		];

		const orderItem: IOrderItem = {
			deliveryGroups: {
				10000: {
					options: '[]',
					orderItemId: 100,
					originalQuantity: 8,
					quantity: 8,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
				10001: {
					options: '[]',
					orderItemId: 101,
					originalQuantity: 2,
					quantity: 2,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			},
			id: 100,
			name: 'Product1',
			options: '[]',
			productId: 1000,
			quantity: 10,
			replacedSkuId: 0,
			requestedDeliveryDate: '',
			settings: {
				maxQuantity: 10000,
				minQuantity: 2,
				multipleQuantity: 1,
			},
			shippingAddressId: 0,
			sku: 'SKU1',
			skuId: 1001,
			skuUnitOfMeasure: {} as any,
			thumbnail: '/o/commerce-media/default/?groupId=33472',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemRow
				deliveryGroups={deliveryGroups}
				handleSelection={jest.fn()}
				handleSubmit={handleSubmitWrapper}
				orderId={10}
				orderItem={orderItem as any}
			/>
		);

		const {
			deliveryGroup1Input,
			deliveryGroup2Input,
			quantityCell,
			row0Actions,
			row0SplitQuantityAction,
		} = getLocators(deliveryGroups, orderItem.id, renderedComponent);

		expect(quantityCell).toHaveTextContent(String(orderItem.quantity));
		expect(deliveryGroup1Input).toHaveValue(8);
		expect(deliveryGroup2Input).toHaveValue(2);
		expect(row0Actions).toBeVisible();

		await act(async () => {
			row0Actions.click();
		});

		await waitFor(() => {
			expect(row0SplitQuantityAction).toBeVisible();
			expect(row0SplitQuantityAction).toBeEnabled();
		});

		await act(async () => {
			row0SplitQuantityAction.click();
		});

		await waitFor(() => {
			expect(quantityCell).toBeVisible();
			expect(quantityCell).toHaveTextContent(String(10));
			expect(deliveryGroup1Input).toHaveValue(8);
			expect(deliveryGroup2Input).toHaveValue(2);
		});

		expect(handleSubmit).not.toBeCalled();

		await act(async () => {
			row0Actions.click();
		});

		await waitFor(() => {
			expect(row0SplitQuantityAction).toBeVisible();
			expect(row0SplitQuantityAction).toBeEnabled();
		});

		await act(async () => {
			row0SplitQuantityAction.click();
		});

		await waitFor(() => {
			expect(quantityCell).toBeVisible();
			expect(quantityCell).toHaveTextContent(String(10));
			expect(deliveryGroup1Input).toHaveValue(5);
			expect(deliveryGroup2Input).toHaveValue(5);

			expect(handleSubmit).toBeCalledWith({
				10000: {
					options: '[]',
					orderItemId: 100,
					originalQuantity: 5,
					quantity: 5,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
				10001: {
					options: '[]',
					orderItemId: 101,
					originalQuantity: 2,
					quantity: 5,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			});
		});
	});

	it('Must split the quantities with remainder', async () => {
		jest.spyOn(window, 'confirm').mockImplementation(() => true);

		const handleSubmitWrapper = jest.fn((param: IOrderItem) => {
			handleSubmit(param.deliveryGroups);
		});

		const deliveryGroups = [
			{
				addressId: 100,
				deliveryDate: '',
				id: 10000,
				name: 'DeliveryGroupName1',
			},
			{
				addressId: 100,
				deliveryDate: '',
				id: 10001,
				name: 'DeliveryGroupName2',
			},
		];

		const orderItem: IOrderItem = {
			deliveryGroups: {
				10000: {
					options: '[]',
					orderItemId: 100,
					originalQuantity: 5,
					quantity: 5,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			},
			id: 100,
			name: 'Product1',
			options: '[]',
			productId: 1000,
			quantity: 5,
			replacedSkuId: 0,
			requestedDeliveryDate: '',
			settings: {
				maxQuantity: 10000,
				minQuantity: 2,
				multipleQuantity: 1,
			},
			shippingAddressId: 0,
			sku: 'SKU1',
			skuId: 1001,
			skuUnitOfMeasure: {} as any,
			thumbnail: '/o/commerce-media/default/?groupId=33472',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemRow
				deliveryGroups={deliveryGroups}
				handleSelection={jest.fn()}
				handleSubmit={handleSubmitWrapper}
				orderId={10}
				orderItem={orderItem as any}
			/>
		);

		const {
			deliveryGroup1Input,
			deliveryGroup2Input,
			quantityCell,
			row0Actions,
			row0SplitQuantityAction,
		} = getLocators(deliveryGroups, orderItem.id, renderedComponent);

		expect(quantityCell).toHaveTextContent(String(orderItem.quantity));
		expect(deliveryGroup1Input).toHaveValue(5);
		expect(deliveryGroup2Input).not.toHaveValue();
		expect(row0Actions).toBeVisible();

		await act(async () => {
			row0Actions.click();
		});

		await waitFor(() => {
			expect(row0SplitQuantityAction).toBeVisible();
			expect(row0SplitQuantityAction).toBeEnabled();
		});

		await act(async () => {
			row0SplitQuantityAction.click();
		});

		await waitFor(() => {
			expect(quantityCell).toBeVisible();
			expect(quantityCell).toHaveTextContent(String(5));
			expect(deliveryGroup1Input).toHaveValue(3);
			expect(deliveryGroup2Input).toHaveValue(2);

			expect(handleSubmit).toBeCalledWith({
				10000: {
					options: '[]',
					orderItemId: 100,
					originalQuantity: 3,
					quantity: 3,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
				10001: {
					options: '[]',
					orderItemId: 0,
					originalQuantity: 2,
					quantity: 2,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			});
		});
	});

	it('Must split the quantities if no primary delivery group', async () => {
		jest.spyOn(window, 'confirm').mockImplementation(() => true);

		const handleSubmitWrapper = jest.fn((param: IOrderItem) => {
			handleSubmit(param.deliveryGroups);
		});

		const deliveryGroups = [
			{
				addressId: 100,
				deliveryDate: '',
				id: 10000,
				name: 'DeliveryGroupName1',
			},
			{
				addressId: 100,
				deliveryDate: '',
				id: 10001,
				name: 'DeliveryGroupName2',
			},
		];

		const orderItem: IOrderItem = {
			deliveryGroups: {
				10001: {
					options: '[]',
					orderItemId: 101,
					originalQuantity: 5,
					quantity: 5,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			},
			id: 100,
			name: 'Product1',
			options: '[]',
			productId: 1000,
			quantity: 5,
			replacedSkuId: 0,
			requestedDeliveryDate: '',
			settings: {
				maxQuantity: 10000,
				minQuantity: 2,
				multipleQuantity: 1,
			},
			shippingAddressId: 0,
			sku: 'SKU1',
			skuId: 1001,
			skuUnitOfMeasure: {} as any,
			thumbnail: '/o/commerce-media/default/?groupId=33472',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemRow
				deliveryGroups={deliveryGroups}
				handleSelection={jest.fn()}
				handleSubmit={handleSubmitWrapper}
				orderId={10}
				orderItem={orderItem as any}
			/>
		);

		const {
			deliveryGroup1Input,
			deliveryGroup2Input,
			quantityCell,
			row0Actions,
			row0SplitQuantityAction,
		} = getLocators(deliveryGroups, orderItem.id, renderedComponent);

		expect(quantityCell).toHaveTextContent(String(orderItem.quantity));
		expect(deliveryGroup1Input).not.toHaveValue();
		expect(deliveryGroup2Input).toHaveValue(5);
		expect(row0Actions).toBeVisible();

		await act(async () => {
			row0Actions.click();
		});

		await waitFor(() => {
			expect(row0SplitQuantityAction).toBeVisible();
			expect(row0SplitQuantityAction).toBeEnabled();
		});

		await act(async () => {
			row0SplitQuantityAction.click();
		});

		await waitFor(() => {
			expect(quantityCell).toBeVisible();
			expect(quantityCell).toHaveTextContent(String(5));
			expect(deliveryGroup1Input).toHaveValue(3);
			expect(deliveryGroup2Input).toHaveValue(2);

			expect(handleSubmit).toBeCalledWith({
				10000: {
					options: '[]',
					orderItemId: 0,
					originalQuantity: 3,
					quantity: 3,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
				10001: {
					options: '[]',
					orderItemId: 101,
					originalQuantity: 5,
					quantity: 2,
					replacedSkuId: 0,
					skuId: 1001,
					skuUnitOfMeasure: {},
				},
			});
		});
	});
});

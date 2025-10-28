/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {RenderResult, cleanup, render} from '@testing-library/react';
import React from 'react';

import OrderItemDetailModal from '../../../src/main/resources/META-INF/resources/components/multishipping/OrderItemDetailModal';
import {IOrderItem} from '../../../src/main/resources/META-INF/resources/components/multishipping/Types';

interface ILocators {
	orderItemName: HTMLElement;
	uom: HTMLElement | null;
}

const getLocators = (
	orderItem: IOrderItem,
	renderedComponent: RenderResult
): ILocators => {
	return {
		orderItemName: renderedComponent.getByText(orderItem.name as string),
		uom: renderedComponent.queryByText(
			`uom: ${orderItem.skuUnitOfMeasure?.key}`
		),
	};
};

describe('OrderItemDetailModal', () => {
	afterEach(() => {
		cleanup();
	});

	it('Must show modal with order item data', async () => {
		const orderItem = {
			name: 'ABS Sensor',
			options: JSON.stringify([]),
			sku: 'MIN93015',
			skuUnitOfMeasure: {
				key: 'lt',
			},
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemDetailModal
				observer={{
					dispatch: jest.fn(),
					mutation: [true, true],
				}}
				orderItem={orderItem}
			/>
		);

		const {orderItemName, uom} = getLocators(orderItem, renderedComponent);

		expect(orderItemName).toBeVisible();
		expect(uom).toBeVisible();
	});

	it('Must show modal without uom', async () => {
		const orderItem = {
			name: 'ABS Sensor',
			options: JSON.stringify([]),
			sku: 'MIN93015',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemDetailModal
				observer={{
					dispatch: jest.fn(),
					mutation: [true, true],
				}}
				orderItem={orderItem}
			/>
		);

		const {orderItemName, uom} = getLocators(orderItem, renderedComponent);

		expect(orderItemName).toBeVisible();
		expect(uom).not.toBeInTheDocument();
	});

	it('Must show modal without options', async () => {
		const orderItem = {
			name: 'ABS Sensor',
			options: JSON.stringify([]),
			sku: 'MIN93015',
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemDetailModal
				observer={{
					dispatch: jest.fn(),
					mutation: [true, true],
				}}
				orderItem={orderItem}
			/>
		);

		const {orderItemName} = getLocators(orderItem, renderedComponent);

		expect(orderItemName).toBeVisible();
		expect(
			renderedComponent.queryByText('Options')
		).not.toBeInTheDocument();
	});

	it('Must show modal with complex order item data', async () => {
		const orderItem = {
			cartItems: [
				{
					name: 'ABS Sensor 2',
					quantity: 10,
					skuId: 10,
					skuUnitOfMeasure: {
						key: 'lt2',
					},
				},
			],
			name: 'ABS Sensor',
			options: JSON.stringify([
				{
					key: 'test-option-1',
					skuId: 10,
					skuOptionKey: 'test-option-1',
					skuOptionName: 'Test option1',
					value: ['Lorem ipsum dolor sit amet'],
				},
				{
					key: 'test-option-2',
					skuId: null,
					skuOptionKey: 'test-option-2',
					skuOptionName: 'Test option2',
					value: ['2024-10-19'],
				},
			]),
			sku: 'MIN93015',
			skuUnitOfMeasure: {
				key: 'lt',
			},
		} as IOrderItem;

		const renderedComponent = render(
			<OrderItemDetailModal
				observer={{
					dispatch: jest.fn(),
					mutation: [true, true],
				}}
				orderItem={orderItem}
			/>
		);

		const {orderItemName, uom} = getLocators(orderItem, renderedComponent);

		expect(orderItemName).toBeVisible();
		expect(uom).toBeVisible();
		expect(renderedComponent.getByText('Test option1')).toBeVisible();
		expect(
			renderedComponent.getByText('Lorem ipsum dolor sit amet')
		).toBeVisible();
		expect(
			renderedComponent.getByText('(10 × ABS Sensor 2 lt2)')
		).toBeVisible();
		expect(renderedComponent.getByText('Test option2')).toBeVisible();
		expect(renderedComponent.getByText('2024-10-19')).toBeVisible();
	});
});

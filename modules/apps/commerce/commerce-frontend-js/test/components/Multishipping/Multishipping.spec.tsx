/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// @ts-ignore

import fetchMock from 'fetch-mock';

import {
	IOrderItem,
	IOrderItemAPIResponse,
} from '../../../src/main/resources/META-INF/resources/components/multishipping/Types';

import '@testing-library/jest-dom';
import {RenderResult, cleanup, render, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';
import {act} from 'react-dom/test-utils';

import * as ErrorMessage from '../../../src/main/resources/META-INF/resources/components/multishipping/ErrorMessage';
import Multishipping from '../../../src/main/resources/META-INF/resources/components/multishipping/Multishipping';
import {isArrayEqual, setFieldValue} from '../../tests_utilities/utils.spec';

interface ILocators {
	addDeliveryGroupButton: HTMLButtonElement;
	bulkCopyActionButton: HTMLButtonElement;
	bulkRemoveActionButton: HTMLButtonElement;
	bulkResetActionButton: HTMLButtonElement;
	bulkSplitActionButton: HTMLButtonElement;
	loadingSpinner: HTMLElement;
	orderItem1Row: HTMLTableRowElement;
	orderItem2Row: HTMLTableRowElement;
	row0SelectCheckbox: HTMLInputElement;
	row1SelectCheckbox: HTMLInputElement;
	searchInput: HTMLInputElement;
	selectAllButton: HTMLButtonElement;
	selectAllCheckbox: HTMLInputElement;
	selectionStatsSpan: HTMLSpanElement;
}

const getLocators = (
	orderItems: Array<IOrderItem>,
	renderedComponent: RenderResult
): ILocators => {
	return {
		addDeliveryGroupButton: renderedComponent.queryByRole('button', {
			name: 'add-delivery-group',
		}) as HTMLButtonElement,
		bulkCopyActionButton: renderedComponent.queryByTestId(
			'bulkCopyAction'
		) as HTMLButtonElement,
		bulkRemoveActionButton: renderedComponent.queryByTestId(
			'bulkRemoveAction'
		) as HTMLButtonElement,
		bulkResetActionButton: renderedComponent.queryByTestId(
			'bulkResetAction'
		) as HTMLButtonElement,
		bulkSplitActionButton: renderedComponent.queryByTestId(
			'bulkSplitAction'
		) as HTMLButtonElement,
		loadingSpinner: renderedComponent.queryByTestId(
			'loadingSpinner'
		) as HTMLInputElement,
		orderItem1Row: renderedComponent.queryByTestId(
			`orderItem${orderItems[0].id}Row`
		) as HTMLTableRowElement,
		orderItem2Row: renderedComponent.queryByTestId(
			`orderItem${orderItems[1].id}Row`
		) as HTMLTableRowElement,
		row0SelectCheckbox: renderedComponent.queryByTestId(
			`row0Select`
		) as HTMLInputElement,
		row1SelectCheckbox: renderedComponent.queryByTestId(
			`row1Select`
		) as HTMLInputElement,
		searchInput: renderedComponent.queryByRole('textbox', {
			name: 'search',
		}) as HTMLInputElement,
		selectAllButton: renderedComponent.queryByTestId(
			`selectAllButton`
		) as HTMLButtonElement,
		selectAllCheckbox: renderedComponent.queryByTestId(
			`selectAllCheckbox`
		) as HTMLInputElement,
		selectionStatsSpan: renderedComponent.queryByTestId(
			`selectionStats`
		) as HTMLSpanElement,
	};
};

describe('Multishipping', () => {
	afterEach(() => {
		fetchMock.restore();
		jest.clearAllMocks();

		cleanup();
	});

	it('Must display order items with no delivery groups', async () => {
		const orderItems = Array(2)
			.fill(0)
			.map((_, currentIndex) => {
				return {
					deliveryGroupName: `DeliveryGroupName${currentIndex}`,
					id: 1000 + currentIndex,
					name: `ProductName${currentIndex}`,
					options: '[]',
					quantity: 3 + currentIndex,
					replacedSkuId: 0,
					settings: {
						maxQuantity: 10000,
						minQuantity: 1,
						multipleQuantity: 1,
					},
					shippingAddressId: 1000 + currentIndex,
					sku: `SKU${currentIndex}`,
					skuId: 100 + currentIndex,
					thumbnail: '/o/commerce-media/default/?groupId=33472',
				};
			}) as Array<IOrderItem>;

		fetchMock.get(
			/headless-commerce-delivery-cart\/.*\/carts\/.*\/items\?.*/i,
			(): IOrderItemAPIResponse => {
				return {
					items: orderItems,
				} as IOrderItemAPIResponse;
			}
		);

		const renderedComponent = render(
			<Multishipping accountId={10} orderId={10} />
		);

		const {addDeliveryGroupButton, loadingSpinner, searchInput} =
			getLocators(orderItems, renderedComponent);

		expect(addDeliveryGroupButton).toBeVisible();
		expect(addDeliveryGroupButton).toBeDisabled();
		expect(loadingSpinner).toBeVisible();
		expect(searchInput).toBeVisible();
		expect(searchInput).toBeDisabled();

		await waitFor(() => {
			expect(loadingSpinner).not.toBeInTheDocument();
		});

		expect(addDeliveryGroupButton).toBeEnabled();
		expect(searchInput).toBeEnabled();

		const {orderItem1Row, orderItem2Row} = getLocators(
			orderItems,
			renderedComponent
		);

		expect(orderItem1Row).toBeVisible();
		expect(orderItem2Row).toBeVisible();
	});

	it('Must paginate the order items', async () => {
		const orderItems = Array(5)
			.fill(0)
			.map((_, currentIndex) => {
				return {
					deliveryGroupName: `DeliveryGroupName${currentIndex}`,
					id: 1000 + currentIndex,
					name: `ProductName${currentIndex}`,
					options: '[]',
					quantity: 3 + currentIndex,
					replacedSkuId: 0,
					settings: {
						maxQuantity: 10000,
						minQuantity: 1,
						multipleQuantity: 1,
					},
					shippingAddressId: 1000 + currentIndex,
					sku: `SKU${currentIndex}`,
					skuId: 100 + currentIndex,
					thumbnail: '/o/commerce-media/default/?groupId=33472',
				};
			}) as Array<IOrderItem>;

		fetchMock.get(
			/headless-commerce-delivery-cart\/.*\/carts\/.*\/items\?.*/i,
			(): IOrderItemAPIResponse => {
				return {
					items: orderItems,
				} as IOrderItemAPIResponse;
			}
		);

		const renderedComponent = render(
			<Multishipping accountId={10} orderId={10} />
		);

		const {loadingSpinner} = getLocators(orderItems, renderedComponent);

		await waitFor(() => {
			expect(loadingSpinner).not.toBeInTheDocument();
		});

		expect(
			renderedComponent.queryByTestId(`orderItem${orderItems[0].id}Row`)
		).toBeVisible();
		expect(
			renderedComponent.queryByTestId(`orderItem${orderItems[1].id}Row`)
		).toBeVisible();
		expect(
			renderedComponent.queryByTestId(`orderItem${orderItems[2].id}Row`)
		).toBeVisible();
		expect(
			renderedComponent.queryByTestId(`orderItem${orderItems[3].id}Row`)
		).toBeVisible();
		expect(
			renderedComponent.queryByTestId(`orderItem${orderItems[4].id}Row`)
		).toBeVisible();

		await act(async () => {
			expect(
				renderedComponent.queryByRole('combobox', {
					name: 'Items Per Page',
				})
			).not.toBeNull();
			(
				renderedComponent.queryByRole('combobox', {
					name: 'Items Per Page',
				}) as HTMLButtonElement
			).click();
		});

		await waitFor(() => {
			expect(
				renderedComponent.queryByRole('option', {name: '4 items'})
			).toBeVisible();
		});

		await act(async () => {
			expect(
				renderedComponent.queryByRole('option', {name: '4 items'})
			).not.toBeNull();
			(
				renderedComponent.queryByRole('option', {
					name: '4 items',
				}) as HTMLButtonElement
			).click();
		});

		expect(
			renderedComponent.queryByTestId(`orderItem${orderItems[0].id}Row`)
		).toBeVisible();
		expect(
			renderedComponent.queryByTestId(`orderItem${orderItems[1].id}Row`)
		).toBeVisible();
		expect(
			renderedComponent.queryByTestId(`orderItem${orderItems[2].id}Row`)
		).toBeVisible();
		expect(
			renderedComponent.queryByTestId(`orderItem${orderItems[3].id}Row`)
		).toBeVisible();
		expect(
			renderedComponent.queryByTestId(`orderItem${orderItems[4].id}Row`)
		).toBeNull();

		await act(async () => {
			expect(
				renderedComponent.queryByRole('button', {
					name: 'Go to the next page, 2',
				})
			).not.toBeNull();
			(
				renderedComponent.queryByRole('button', {
					name: 'Go to the next page, 2',
				}) as HTMLButtonElement
			).click();
		});

		expect(
			renderedComponent.queryByTestId(`orderItem${orderItems[0].id}Row`)
		).toBeNull();
		expect(
			renderedComponent.queryByTestId(`orderItem${orderItems[1].id}Row`)
		).toBeNull();
		expect(
			renderedComponent.queryByTestId(`orderItem${orderItems[2].id}Row`)
		).toBeNull();
		expect(
			renderedComponent.queryByTestId(`orderItem${orderItems[3].id}Row`)
		).toBeNull();
		expect(
			renderedComponent.queryByTestId(`orderItem${orderItems[4].id}Row`)
		).toBeVisible();
	});

	it('Table must be searchable', async () => {
		const orderItems = Array(2)
			.fill(0)
			.map((_, currentIndex) => {
				return {
					deliveryGroupName: `DeliveryGroupName${currentIndex}`,
					id: 1000 + currentIndex,
					name: `ProductName${currentIndex}`,
					options: '[]',
					quantity: 3 + currentIndex,
					replacedSkuId: 0,
					settings: {
						maxQuantity: 10000,
						minQuantity: 1,
						multipleQuantity: 1,
					},
					shippingAddressId: 1000 + currentIndex,
					sku: `SKU${currentIndex}`,
					skuId: 100 + currentIndex,
					thumbnail: '/o/commerce-media/default/?groupId=33472',
				};
			}) as Array<IOrderItem>;

		fetchMock.get(
			/headless-commerce-delivery-cart\/.*\/carts\/.*\/items\?.*/i,
			(): IOrderItemAPIResponse => {
				return {
					items: orderItems,
				} as IOrderItemAPIResponse;
			}
		);

		const renderedComponent = render(
			<Multishipping accountId={10} orderId={10} />
		);

		const {loadingSpinner, searchInput} = getLocators(
			orderItems,
			renderedComponent
		);

		await waitFor(() => {
			expect(loadingSpinner).not.toBeInTheDocument();
		});

		expect(
			renderedComponent.queryByTestId(`orderItem${orderItems[0].id}Row`)
		).toBeVisible();
		expect(
			renderedComponent.queryByTestId(`orderItem${orderItems[1].id}Row`)
		).toBeVisible();

		await setFieldValue(searchInput, orderItems[0].name as string);

		expect(
			renderedComponent.queryByTestId(`orderItem${orderItems[0].id}Row`)
		).toBeVisible();
		expect(
			renderedComponent.queryByTestId(`orderItem${orderItems[1].id}Row`)
		).toBeNull();

		await setFieldValue(searchInput, orderItems[1].sku as string);

		expect(
			renderedComponent.queryByTestId(`orderItem${orderItems[0].id}Row`)
		).toBeNull();
		expect(
			renderedComponent.queryByTestId(`orderItem${orderItems[1].id}Row`)
		).toBeVisible();

		await setFieldValue(searchInput, 'S');

		expect(
			renderedComponent.queryByTestId(`orderItem${orderItems[0].id}Row`)
		).toBeVisible();
		expect(
			renderedComponent.queryByTestId(`orderItem${orderItems[1].id}Row`)
		).toBeVisible();
	});

	it('Must disable add delivery group if 20 are already available', async () => {
		const orderItems = Array(20)
			.fill(0)
			.map((_, currentIndex) => {
				return {
					deliveryGroupName: `DeliveryGroupName${currentIndex}`,
					id: 1000 + currentIndex,
					name: `ProductName${currentIndex}`,
					options: '[]',
					quantity: 3 + currentIndex,
					replacedSkuId: 0,
					settings: {
						maxQuantity: 10000,
						minQuantity: 1,
						multipleQuantity: 1,
					},
					shippingAddressId: 1000 + currentIndex,
					sku: `SKU${currentIndex}`,
					skuId: 100 + currentIndex,
					thumbnail: '/o/commerce-media/default/?groupId=33472',
				};
			}) as Array<IOrderItem>;

		fetchMock.get(
			/headless-commerce-delivery-cart\/.*\/carts\/.*\/items\?.*/i,
			(): IOrderItemAPIResponse => {
				return {
					items: orderItems,
				} as IOrderItemAPIResponse;
			}
		);

		const renderedComponent = render(
			<Multishipping accountId={10} orderId={10} />
		);

		const {addDeliveryGroupButton, loadingSpinner} = getLocators(
			orderItems,
			renderedComponent
		);

		await waitFor(() => {
			expect(loadingSpinner).not.toBeInTheDocument();
		});

		expect(addDeliveryGroupButton).toBeDisabled();
	});

	it('Must disable and hide everything if readonly', async () => {
		const orderItems = Array(2)
			.fill(0)
			.map((_, currentIndex) => {
				return {
					deliveryGroupName: `DeliveryGroupName${currentIndex}`,
					id: 1000 + currentIndex,
					name: `ProductName${currentIndex}`,
					options: '[]',
					quantity: 3 + currentIndex,
					replacedSkuId: 0,
					settings: {
						maxQuantity: 10000,
						minQuantity: 1,
						multipleQuantity: 1,
					},
					shippingAddressId: 1000 + currentIndex,
					sku: `SKU${currentIndex}`,
					skuId: 100 + currentIndex,
					thumbnail: '/o/commerce-media/default/?groupId=33472',
				};
			}) as Array<IOrderItem>;

		fetchMock.get(
			/headless-commerce-delivery-cart\/.*\/carts\/.*\/items\?.*/i,
			(): IOrderItemAPIResponse => {
				return {
					items: orderItems,
				} as IOrderItemAPIResponse;
			}
		);

		const renderedComponent = render(
			<Multishipping accountId={10} orderId={10} readonly={true} />
		);

		const {addDeliveryGroupButton, loadingSpinner, selectAllCheckbox} =
			getLocators(orderItems, renderedComponent);

		await waitFor(() => {
			expect(loadingSpinner).not.toBeInTheDocument();
		});

		expect(addDeliveryGroupButton).not.toBeInTheDocument();
		expect(selectAllCheckbox).not.toBeInTheDocument();

		renderedComponent
			.getAllByTestId(/orderItem.*Input/)
			.forEach((input) => expect(input).toBeDisabled());

		expect(
			renderedComponent.queryAllByTestId(/deliveryGroup.*Actions/).length
		).toBe(0);
	});

	it('Table should not display delivery group columns', async () => {
		const orderItems = Array(2)
			.fill(0)
			.map((_, currentIndex) => {
				return {
					id: 1000 + currentIndex,
					name: `ProductName${currentIndex}`,
					options: '[]',
					quantity: 3 + currentIndex,
					replacedSkuId: 0,
					settings: {
						maxQuantity: 10000,
						minQuantity: 1,
						multipleQuantity: 1,
					},
					sku: `SKU${currentIndex}`,
					skuId: 100 + currentIndex,
					thumbnail: '/o/commerce-media/default/?groupId=33472',
				};
			}) as Array<IOrderItem>;

		fetchMock.get(
			/headless-commerce-delivery-cart\/.*\/carts\/.*\/items\?.*/i,
			(): IOrderItemAPIResponse => {
				return {
					items: orderItems,
				} as IOrderItemAPIResponse;
			}
		);

		const renderedComponent = render(
			<Multishipping accountId={10} orderId={10} />
		);

		const {loadingSpinner} = getLocators(orderItems, renderedComponent);

		await waitFor(() => {
			expect(loadingSpinner).not.toBeInTheDocument();
		});

		expect(
			renderedComponent.queryAllByTestId(/orderItem.*Input/)
		).toHaveLength(0);
	});

	it('Table should display delivery group column with correct quantities', async () => {
		const orderItems = Array(3)
			.fill(0)
			.map((_, currentIndex) => {
				return {
					deliveryGroupName: `DeliveryGroupName`,
					id: 1000 + currentIndex,
					name: `ProductName${currentIndex}`,
					options: '[]',
					quantity: 3 + currentIndex,
					replacedSkuId: 0,
					settings: {
						maxQuantity: 10000,
						minQuantity: 1,
						multipleQuantity: 1,
					},
					shippingAddressId: 1000,
					sku: `SKU${currentIndex}`,
					skuId: 100 + currentIndex,
					thumbnail: '/o/commerce-media/default/?groupId=33472',
				};
			}) as Array<IOrderItem>;

		fetchMock.get(
			/headless-commerce-delivery-cart\/.*\/carts\/.*\/items\?.*/i,
			(): IOrderItemAPIResponse => {
				return {
					items: orderItems,
				} as IOrderItemAPIResponse;
			}
		);

		const renderedComponent = render(
			<Multishipping accountId={10} orderId={10} />
		);

		const {loadingSpinner} = getLocators(orderItems, renderedComponent);

		await waitFor(() => {
			expect(loadingSpinner).not.toBeInTheDocument();
		});

		expect(
			renderedComponent.queryAllByTestId(/deliveryGroup.*Actions/)
		).toHaveLength(1);

		renderedComponent
			.queryAllByTestId(/orderItem.*Input/)
			.forEach((input, index) => {
				expect(input).toHaveValue(3 + index);
			});
	});

	it('Table should display same product in multiple delivery groups', async () => {
		const orderItems = [
			{
				deliveryGroupName: `DeliveryGroupName1`,
				id: 1000,
				name: `ProductName1`,
				options: '[]',
				quantity: 3,
				replacedSkuId: 0,
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1000,
				sku: `SKU1`,
				skuId: 100,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
			{
				deliveryGroupName: `DeliveryGroupName2`,
				id: 1001,
				name: `ProductName1`,
				options: '[]',
				quantity: 5,
				replacedSkuId: 0,
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1001,
				sku: `SKU1`,
				skuId: 100,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
			{
				deliveryGroupName: `DeliveryGroupName2`,
				id: 1002,
				name: `ProductName2`,
				options: '[]',
				quantity: 8,
				replacedSkuId: 0,
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1001,
				sku: `SKU2`,
				skuId: 102,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
		] as Array<IOrderItem>;

		fetchMock.get(
			/headless-commerce-delivery-cart\/.*\/carts\/.*\/items\?.*/i,
			(): IOrderItemAPIResponse => {
				return {
					items: orderItems,
				} as IOrderItemAPIResponse;
			}
		);

		const renderedComponent = render(
			<Multishipping accountId={10} orderId={10} />
		);

		const {loadingSpinner} = getLocators(orderItems, renderedComponent);

		await waitFor(() => {
			expect(loadingSpinner).not.toBeInTheDocument();
		});

		expect(
			renderedComponent.queryAllByTestId(/deliveryGroup.*Actions/)
		).toHaveLength(2);

		const orderItem1Inputs =
			renderedComponent.queryAllByTestId(/orderItem1000.*Input/);

		expect(orderItem1Inputs[0]).toHaveValue(3);
		expect(orderItem1Inputs[1]).toHaveValue(5);

		const orderItem2Inputs =
			renderedComponent.queryAllByTestId(/orderItem1002.*Input/);

		expect(orderItem2Inputs[0]).not.toHaveValue();
		expect(orderItem2Inputs[1]).toHaveValue(8);
	});

	it('Must fix missing delivery groups', async () => {
		const orderItems = [
			{
				deliveryGroupName: `DeliveryGroupName1`,
				id: 1000,
				name: `ProductName1`,
				options: '[]',
				quantity: 3,
				replacedSkuId: 0,
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1000,
				sku: `SKU1`,
				skuId: 100,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
			{
				deliveryGroupName: '',
				id: 1001,
				name: `ProductName1`,
				options: '[]',
				quantity: 5,
				replacedSkuId: 0,
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 0,
				sku: `SKU1`,
				skuId: 100,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
			{
				deliveryGroupName: `DeliveryGroupName1`,
				id: 1002,
				name: `ProductName2`,
				options: '[]',
				quantity: 8,
				replacedSkuId: 0,
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1000,
				sku: `SKU2`,
				skuId: 102,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
		] as Array<IOrderItem>;

		const mockFetch = jest
			.fn()
			.mockReturnValueOnce(() => {
				return {
					items: orderItems,
				} as IOrderItemAPIResponse;
			})
			.mockReturnValue(() => {
				orderItems[1].deliveryGroupName = 'DeliveryGroupName1';
				orderItems[1].shippingAddressId = 1000;

				return {
					cartItems: orderItems,
				} as IOrderItemAPIResponse;
			});

		fetchMock.get(
			/headless-commerce-delivery-cart\/.*\/carts\/.*\/items\?.*/i,
			mockFetch
		);

		fetchMock.patch(
			/headless-commerce-delivery-cart\/.*\/carts\/.*\?.*/i,
			mockFetch
		);

		const renderedComponent = render(
			<Multishipping accountId={10} orderId={10} />
		);

		const {loadingSpinner} = getLocators(orderItems, renderedComponent);

		await waitFor(() => {
			expect(loadingSpinner).not.toBeInTheDocument();
		});

		expect(
			renderedComponent.queryAllByTestId(/deliveryGroup.*Actions/)
		).toHaveLength(1);
		expect(
			renderedComponent.queryAllByTestId(/orderItem.*Row/)
		).toHaveLength(3);
		expect(
			renderedComponent.queryByTestId(/orderItem1001.*Input/)
		).toHaveValue(5);

		expect(
			isArrayEqual(
				JSON.parse(fetchMock.calls().matched[1][1].body).cartItems,
				JSON.parse(
					'[{"deliveryGroupName":"DeliveryGroupName1","id":1000,"options":"[]","quantity":3,"replacedSkuId":0,"requestedDeliveryDate":"","shippingAddressId":1000,"skuId":100},{"deliveryGroupName":"DeliveryGroupName1","id":1001,"options":"[]","quantity":5,"replacedSkuId":0,"requestedDeliveryDate":"","shippingAddressId":1000,"skuId":100},{"deliveryGroupName":"DeliveryGroupName1","id":1002,"options":"[]","quantity":8,"replacedSkuId":0,"requestedDeliveryDate":"","shippingAddressId":1000,"skuId":102}]'
				)
			)
		).toBeTruthy();
		expect(fetchMock.calls().matched[1][1].method).toBe('PATCH');
	});

	it('Must show correct display groups for different products', async () => {
		const orderItems = [
			{
				deliveryGroupName: `DeliveryGroupName1`,
				id: 1000,
				name: `ProductName1`,
				options: '[]',
				quantity: 3,
				replacedSkuId: 0,
				requestedDeliveryDate: '2024-12-12',
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1000,
				sku: `SKU1`,
				skuId: 100,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
			{
				deliveryGroupName: `DeliveryGroupName1`,
				id: 1002,
				name: `ProductName2`,
				options: '[]',
				quantity: 8,
				replacedSkuId: 0,
				requestedDeliveryDate: '2024-12-12',
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1000,
				sku: `SKU2`,
				skuId: 102,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
		] as Array<IOrderItem>;

		const mockFetch = jest
			.fn()
			.mockReturnValueOnce(() => {
				return {
					items: orderItems,
				} as IOrderItemAPIResponse;
			})
			.mockReturnValueOnce(() => {
				orderItems[1].deliveryGroupName = 'DeliveryGroupName2';
				orderItems[1].requestedDeliveryDate = '2024-12-12';
				orderItems[1].shippingAddressId = 1000;

				return {
					items: orderItems,
				} as IOrderItemAPIResponse;
			})
			.mockReturnValueOnce(() => {
				orderItems[1].deliveryGroupName = 'DeliveryGroupName1';
				orderItems[1].requestedDeliveryDate = '2024-12-13';
				orderItems[1].shippingAddressId = 1000;

				return {
					items: orderItems,
				} as IOrderItemAPIResponse;
			})
			.mockReturnValueOnce(() => {
				orderItems[1].deliveryGroupName = 'DeliveryGroupName1';
				orderItems[1].requestedDeliveryDate = '2024-12-12';
				orderItems[1].shippingAddressId = 1001;

				return {
					items: orderItems,
				} as IOrderItemAPIResponse;
			});

		fetchMock.get(
			/headless-commerce-delivery-cart\/.*\/carts\/.*\/items\?.*/i,
			mockFetch
		);

		let renderedComponent = render(
			<Multishipping accountId={10} orderId={10} />
		);

		let {loadingSpinner} = getLocators(orderItems, renderedComponent);

		await waitFor(() => {
			expect(loadingSpinner).not.toBeInTheDocument();
		});

		expect(
			renderedComponent.queryAllByTestId(/deliveryGroup.*Actions/)
		).toHaveLength(1);
		expect(
			renderedComponent.queryAllByTestId(/orderItem.*Row/)
		).toHaveLength(2);

		renderedComponent.unmount();

		renderedComponent = render(
			<Multishipping accountId={10} orderId={10} />
		);

		({loadingSpinner} = getLocators(orderItems, renderedComponent));

		await waitFor(() => {
			expect(loadingSpinner).not.toBeInTheDocument();
		});

		expect(
			renderedComponent.queryAllByTestId(/deliveryGroup.*Actions/)
		).toHaveLength(2);
		expect(
			renderedComponent.queryAllByTestId(/orderItem.*Row/)
		).toHaveLength(2);

		renderedComponent.unmount();

		renderedComponent = render(
			<Multishipping accountId={10} orderId={10} />
		);

		({loadingSpinner} = getLocators(orderItems, renderedComponent));

		await waitFor(() => {
			expect(loadingSpinner).not.toBeInTheDocument();
		});

		expect(
			renderedComponent.queryAllByTestId(/deliveryGroup.*Actions/)
		).toHaveLength(2);
		expect(
			renderedComponent.queryAllByTestId(/orderItem.*Row/)
		).toHaveLength(2);

		renderedComponent.unmount();

		renderedComponent = render(
			<Multishipping accountId={10} orderId={10} />
		);

		({loadingSpinner} = getLocators(orderItems, renderedComponent));

		await waitFor(() => {
			expect(loadingSpinner).not.toBeInTheDocument();
		});

		expect(
			renderedComponent.queryAllByTestId(/deliveryGroup.*Actions/)
		).toHaveLength(2);
		expect(
			renderedComponent.queryAllByTestId(/orderItem.*Row/)
		).toHaveLength(2);
	});

	it('Must show correct display groups for same product', async () => {
		const orderItems = [
			{
				deliveryGroupName: `DeliveryGroupName1`,
				id: 1000,
				name: `ProductName1`,
				options: '[]',
				quantity: 3,
				replacedSkuId: 0,
				requestedDeliveryDate: '2024-12-12',
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1000,
				sku: `SKU1`,
				skuId: 100,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
			{
				deliveryGroupName: `DeliveryGroupName1`,
				id: 1002,
				name: `ProductName1`,
				options: '[]',
				quantity: 8,
				replacedSkuId: 0,
				requestedDeliveryDate: '2024-12-12',
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1000,
				sku: `SKU1`,
				skuId: 100,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
		] as Array<IOrderItem>;

		const mockFetch = jest
			.fn()
			.mockReturnValueOnce(() => {
				return {
					items: orderItems,
				} as IOrderItemAPIResponse;
			})
			.mockReturnValueOnce(() => {
				orderItems[1].deliveryGroupName = 'DeliveryGroupName2';
				orderItems[1].requestedDeliveryDate = '2024-12-12';
				orderItems[1].shippingAddressId = 1000;

				return {
					items: orderItems,
				} as IOrderItemAPIResponse;
			})
			.mockReturnValueOnce(() => {
				orderItems[1].deliveryGroupName = 'DeliveryGroupName1';
				orderItems[1].requestedDeliveryDate = '2024-12-13';
				orderItems[1].shippingAddressId = 1000;

				return {
					items: orderItems,
				} as IOrderItemAPIResponse;
			})
			.mockReturnValueOnce(() => {
				orderItems[1].deliveryGroupName = 'DeliveryGroupName1';
				orderItems[1].requestedDeliveryDate = '2024-12-12';
				orderItems[1].shippingAddressId = 1001;

				return {
					items: orderItems,
				} as IOrderItemAPIResponse;
			});

		fetchMock.get(
			/headless-commerce-delivery-cart\/.*\/carts\/.*\/items\?.*/i,
			mockFetch
		);

		let renderedComponent = render(
			<Multishipping accountId={10} orderId={10} />
		);

		let {loadingSpinner} = getLocators(orderItems, renderedComponent);

		await waitFor(() => {
			expect(loadingSpinner).not.toBeInTheDocument();
		});

		expect(
			renderedComponent.queryAllByTestId(/deliveryGroup.*Actions/)
		).toHaveLength(1);
		expect(
			renderedComponent.queryAllByTestId(/orderItem.*Row/)
		).toHaveLength(2);

		renderedComponent.unmount();

		renderedComponent = render(
			<Multishipping accountId={10} orderId={10} />
		);

		({loadingSpinner} = getLocators(orderItems, renderedComponent));

		await waitFor(() => {
			expect(loadingSpinner).not.toBeInTheDocument();
		});

		expect(
			renderedComponent.queryAllByTestId(/deliveryGroup.*Actions/)
		).toHaveLength(2);
		expect(
			renderedComponent.queryAllByTestId(/orderItem.*Row/)
		).toHaveLength(1);

		renderedComponent.unmount();

		renderedComponent = render(
			<Multishipping accountId={10} orderId={10} />
		);

		({loadingSpinner} = getLocators(orderItems, renderedComponent));

		await waitFor(() => {
			expect(loadingSpinner).not.toBeInTheDocument();
		});

		expect(
			renderedComponent.queryAllByTestId(/deliveryGroup.*Actions/)
		).toHaveLength(2);
		expect(
			renderedComponent.queryAllByTestId(/orderItem.*Row/)
		).toHaveLength(1);

		renderedComponent.unmount();

		renderedComponent = render(
			<Multishipping accountId={10} orderId={10} />
		);

		({loadingSpinner} = getLocators(orderItems, renderedComponent));

		await waitFor(() => {
			expect(loadingSpinner).not.toBeInTheDocument();
		});

		expect(
			renderedComponent.queryAllByTestId(/deliveryGroup.*Actions/)
		).toHaveLength(2);
		expect(
			renderedComponent.queryAllByTestId(/orderItem.*Row/)
		).toHaveLength(1);
	});

	it('Must create the default delivery group if address is passed and no delivery groups are there', async () => {
		const orderItems = [
			{
				deliveryGroupName: '',
				id: 1000,
				name: `ProductName1`,
				options: '[]',
				quantity: 3,
				replacedSkuId: 0,
				requestedDeliveryDate: '',
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 0,
				sku: `SKU1`,
				skuId: 100,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
			{
				deliveryGroupName: '',
				id: 1002,
				name: `ProductName1`,
				options: '[]',
				quantity: 8,
				replacedSkuId: 0,
				requestedDeliveryDate: '',
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 0,
				sku: `SKU2`,
				skuId: 101,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
		] as Array<IOrderItem>;

		const mockFetch = jest
			.fn()
			.mockReturnValueOnce(() => {
				return {
					items: orderItems,
				} as IOrderItemAPIResponse;
			})
			.mockReturnValue(() => {
				orderItems[0].deliveryGroupName = 'Default';
				orderItems[0].shippingAddressId = 1000;
				orderItems[1].deliveryGroupName = 'Default';
				orderItems[1].shippingAddressId = 1000;

				return {
					cartItems: orderItems,
				} as IOrderItemAPIResponse;
			});

		fetchMock.get(
			/headless-commerce-delivery-cart\/.*\/carts\/.*\/items\?.*/i,
			mockFetch
		);

		fetchMock.patch(
			/headless-commerce-delivery-cart\/.*\/carts\/.*\?.*/i,
			mockFetch
		);

		const renderedComponent = render(
			<Multishipping
				accountId={10}
				defaultAddressId={1000}
				orderId={10}
			/>
		);

		const {loadingSpinner} = getLocators(orderItems, renderedComponent);

		await waitFor(() => {
			expect(loadingSpinner).not.toBeInTheDocument();
		});

		const defaultDeliveryGroup =
			renderedComponent.queryAllByTestId(/deliveryGroup[0-9]*$/);

		expect(defaultDeliveryGroup).toHaveLength(1);
		expect(defaultDeliveryGroup[0]).toHaveTextContent('Default');
		expect(
			renderedComponent.queryAllByTestId(/orderItem.*Row/)
		).toHaveLength(2);

		expect(
			isArrayEqual(
				JSON.parse(fetchMock.calls().matched[1][1].body).cartItems,
				JSON.parse(
					'[{"deliveryGroupName":"Default","options":"[]","quantity":3,"replacedSkuId":0,"requestedDeliveryDate":"","shippingAddressId":1000,"skuId":100},{"deliveryGroupName":"Default","options":"[]","quantity":8,"replacedSkuId":0,"requestedDeliveryDate":"","shippingAddressId":1000,"skuId":101}]'
				)
			)
		).toBeTruthy();
		expect(fetchMock.calls().matched[1][1].method).toBe('PATCH');
	});
});

describe('MultiShipping - bulk actions', () => {
	beforeEach(() => {
		(window as any).Liferay = {
			...(window as any).Liferay,
			CustomDialogs: {},
		};

		jest.spyOn(window, 'confirm').mockImplementation(() => true);
	});

	afterEach(() => {
		fetchMock.restore();
		jest.clearAllMocks();

		cleanup();
	});

	it('Must check all the rows if select all is checked and the bulk actions are available', async () => {
		const orderItems = [
			{
				deliveryGroupName: `DeliveryGroupName1`,
				id: 1000,
				name: `ProductName1`,
				options: '[]',
				quantity: 3,
				replacedSkuId: 0,
				requestedDeliveryDate: '2024-12-12',
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1000,
				sku: `SKU1`,
				skuId: 100,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
			{
				deliveryGroupName: 'DeliveryGroupName1',
				id: 1002,
				name: `ProductName1`,
				options: '[]',
				quantity: 8,
				replacedSkuId: 0,
				requestedDeliveryDate: '2024-12-12',
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1000,
				sku: `SKU2`,
				skuId: 101,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
		] as Array<IOrderItem>;

		fetchMock.get(
			/headless-commerce-delivery-cart\/.*\/carts\/.*\/items\?.*/i,
			(): IOrderItemAPIResponse => {
				return {
					items: orderItems,
				} as IOrderItemAPIResponse;
			}
		);

		const renderedComponent = render(
			<Multishipping accountId={10} orderId={10} />
		);

		const {loadingSpinner} = getLocators(orderItems, renderedComponent);

		await waitFor(() => {
			expect(loadingSpinner).not.toBeInTheDocument();
		});

		const {row0SelectCheckbox, row1SelectCheckbox} = getLocators(
			orderItems,
			renderedComponent
		);

		let {
			bulkCopyActionButton,
			bulkRemoveActionButton,
			bulkResetActionButton,
			bulkSplitActionButton,
			selectAllCheckbox,
			selectionStatsSpan,
		} = getLocators(orderItems, renderedComponent);

		expect(bulkCopyActionButton).not.toBeInTheDocument();
		expect(bulkRemoveActionButton).not.toBeInTheDocument();
		expect(bulkResetActionButton).not.toBeInTheDocument();
		expect(bulkSplitActionButton).not.toBeInTheDocument();
		expect(row0SelectCheckbox).toBeVisible();
		expect(row0SelectCheckbox).not.toBeChecked();
		expect(row1SelectCheckbox).toBeVisible();
		expect(row1SelectCheckbox).not.toBeChecked();
		expect(selectionStatsSpan).not.toBeInTheDocument();
		expect(selectAllCheckbox).toBeVisible();
		expect(selectAllCheckbox).toBeEnabled();
		expect(selectAllCheckbox).not.toBeChecked();

		userEvent.click(selectAllCheckbox);

		({
			bulkCopyActionButton,
			bulkRemoveActionButton,
			bulkResetActionButton,
			bulkSplitActionButton,
			selectAllCheckbox,
			selectionStatsSpan,
		} = getLocators(orderItems, renderedComponent));

		expect(bulkCopyActionButton).toBeInTheDocument();
		expect(bulkRemoveActionButton).toBeInTheDocument();
		expect(bulkResetActionButton).toBeInTheDocument();
		expect(bulkSplitActionButton).toBeInTheDocument();
		expect(row0SelectCheckbox).toBeChecked();
		expect(row1SelectCheckbox).toBeChecked();
		expect(selectAllCheckbox).toBeChecked();
		expect(selectionStatsSpan).toBeInTheDocument();
		expect(selectionStatsSpan).toHaveTextContent('2 of 2');

		userEvent.click(selectAllCheckbox);

		({selectAllCheckbox} = getLocators(orderItems, renderedComponent));

		expect(bulkCopyActionButton).not.toBeInTheDocument();
		expect(bulkRemoveActionButton).not.toBeInTheDocument();
		expect(bulkResetActionButton).not.toBeInTheDocument();
		expect(bulkSplitActionButton).not.toBeInTheDocument();
		expect(row0SelectCheckbox).not.toBeChecked();
		expect(row1SelectCheckbox).not.toBeChecked();
		expect(selectAllCheckbox).not.toBeChecked();
		expect(selectionStatsSpan).not.toBeInTheDocument();
	});

	it('Must check a single row and the bulk actions are available', async () => {
		const orderItems = [
			{
				deliveryGroupName: `DeliveryGroupName1`,
				id: 1000,
				name: `ProductName1`,
				options: '[]',
				quantity: 3,
				replacedSkuId: 0,
				requestedDeliveryDate: '2024-12-12',
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1000,
				sku: `SKU1`,
				skuId: 100,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
			{
				deliveryGroupName: 'DeliveryGroupName1',
				id: 1002,
				name: `ProductName1`,
				options: '[]',
				quantity: 8,
				replacedSkuId: 0,
				requestedDeliveryDate: '2024-12-12',
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1000,
				sku: `SKU2`,
				skuId: 101,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
		] as Array<IOrderItem>;

		fetchMock.get(
			/headless-commerce-delivery-cart\/.*\/carts\/.*\/items\?.*/i,
			(): IOrderItemAPIResponse => {
				return {
					items: orderItems,
				} as IOrderItemAPIResponse;
			}
		);

		const renderedComponent = render(
			<Multishipping accountId={10} orderId={10} />
		);

		const {loadingSpinner} = getLocators(orderItems, renderedComponent);

		await waitFor(() => {
			expect(loadingSpinner).not.toBeInTheDocument();
		});

		const {row0SelectCheckbox, row1SelectCheckbox} = getLocators(
			orderItems,
			renderedComponent
		);

		let {
			bulkCopyActionButton,
			bulkRemoveActionButton,
			bulkResetActionButton,
			bulkSplitActionButton,
			selectAllCheckbox,
			selectionStatsSpan,
		} = getLocators(orderItems, renderedComponent);

		expect(bulkCopyActionButton).not.toBeInTheDocument();
		expect(bulkRemoveActionButton).not.toBeInTheDocument();
		expect(bulkResetActionButton).not.toBeInTheDocument();
		expect(bulkSplitActionButton).not.toBeInTheDocument();
		expect(row0SelectCheckbox).toBeVisible();
		expect(row0SelectCheckbox).not.toBeChecked();
		expect(row1SelectCheckbox).toBeVisible();
		expect(row1SelectCheckbox).not.toBeChecked();
		expect(selectionStatsSpan).not.toBeInTheDocument();
		expect(selectAllCheckbox).toBeVisible();
		expect(selectAllCheckbox).not.toBeChecked();

		userEvent.click(row0SelectCheckbox);

		({
			bulkCopyActionButton,
			bulkRemoveActionButton,
			bulkResetActionButton,
			bulkSplitActionButton,
			selectAllCheckbox,
			selectionStatsSpan,
		} = getLocators(orderItems, renderedComponent));

		expect(bulkCopyActionButton).toBeInTheDocument();
		expect(bulkRemoveActionButton).toBeInTheDocument();
		expect(bulkResetActionButton).toBeInTheDocument();
		expect(bulkSplitActionButton).toBeInTheDocument();
		expect(row0SelectCheckbox).toBeChecked();
		expect(row1SelectCheckbox).not.toBeChecked();
		expect(selectAllCheckbox).toBeChecked();
		expect(selectionStatsSpan).toBeInTheDocument();
		expect(selectionStatsSpan).toHaveTextContent('1 of 2');

		userEvent.click(row1SelectCheckbox);

		expect(bulkCopyActionButton).toBeInTheDocument();
		expect(bulkRemoveActionButton).toBeInTheDocument();
		expect(bulkResetActionButton).toBeInTheDocument();
		expect(bulkSplitActionButton).toBeInTheDocument();
		expect(row0SelectCheckbox).toBeChecked();
		expect(row1SelectCheckbox).toBeChecked();
		expect(selectAllCheckbox).toBeChecked();
		expect(selectionStatsSpan).toBeInTheDocument();
		expect(selectionStatsSpan).toHaveTextContent('2 of 2');

		userEvent.click(row1SelectCheckbox);

		expect(bulkCopyActionButton).toBeInTheDocument();
		expect(bulkRemoveActionButton).toBeInTheDocument();
		expect(bulkResetActionButton).toBeInTheDocument();
		expect(bulkSplitActionButton).toBeInTheDocument();
		expect(row0SelectCheckbox).toBeChecked();
		expect(row1SelectCheckbox).not.toBeChecked();
		expect(selectAllCheckbox).toBeChecked();
		expect(selectionStatsSpan).toBeInTheDocument();
		expect(selectionStatsSpan).toHaveTextContent('1 of 2');

		userEvent.click(row0SelectCheckbox);

		({selectAllCheckbox} = getLocators(orderItems, renderedComponent));

		expect(bulkCopyActionButton).not.toBeInTheDocument();
		expect(bulkRemoveActionButton).not.toBeInTheDocument();
		expect(bulkResetActionButton).not.toBeInTheDocument();
		expect(bulkSplitActionButton).not.toBeInTheDocument();
		expect(row0SelectCheckbox).not.toBeChecked();
		expect(row1SelectCheckbox).not.toBeChecked();
		expect(selectAllCheckbox).not.toBeChecked();
		expect(selectionStatsSpan).not.toBeInTheDocument();
	});

	it('Must select all the rows', async () => {
		const orderItems = [
			{
				deliveryGroupName: `DeliveryGroupName1`,
				id: 1000,
				name: `ProductName1`,
				options: '[]',
				quantity: 3,
				replacedSkuId: 0,
				requestedDeliveryDate: '2024-12-12',
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1000,
				sku: `SKU1`,
				skuId: 100,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
			{
				deliveryGroupName: 'DeliveryGroupName1',
				id: 1002,
				name: `ProductName1`,
				options: '[]',
				quantity: 8,
				replacedSkuId: 0,
				requestedDeliveryDate: '2024-12-12',
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1000,
				sku: `SKU2`,
				skuId: 101,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
		] as Array<IOrderItem>;

		fetchMock.get(
			/headless-commerce-delivery-cart\/.*\/carts\/.*\/items\?.*/i,
			(): IOrderItemAPIResponse => {
				return {
					items: orderItems,
				} as IOrderItemAPIResponse;
			}
		);

		const renderedComponent = render(
			<Multishipping accountId={10} orderId={10} />
		);

		const {loadingSpinner} = getLocators(orderItems, renderedComponent);

		await waitFor(() => {
			expect(loadingSpinner).not.toBeInTheDocument();
		});

		const {row0SelectCheckbox, row1SelectCheckbox} = getLocators(
			orderItems,
			renderedComponent
		);

		expect(row0SelectCheckbox).toBeVisible();
		expect(row0SelectCheckbox).not.toBeChecked();
		expect(row1SelectCheckbox).toBeVisible();
		expect(row1SelectCheckbox).not.toBeChecked();

		userEvent.click(row0SelectCheckbox);

		const {selectAllButton, selectionStatsSpan} = getLocators(
			orderItems,
			renderedComponent
		);

		expect(row0SelectCheckbox).toBeChecked();
		expect(row1SelectCheckbox).not.toBeChecked();
		expect(selectAllButton).toBeInTheDocument();
		expect(selectionStatsSpan).toHaveTextContent('1 of 2');

		userEvent.click(selectAllButton);

		await waitFor(() => {
			expect(row0SelectCheckbox).toBeChecked();
			expect(row1SelectCheckbox).toBeChecked();
			expect(selectionStatsSpan).toHaveTextContent('2 of 2');
		});
	});

	it('Bulk actions should be disabled if no delivery groups', async () => {
		const orderItems = [
			{
				id: 1000,
				name: `ProductName1`,
				options: '[]',
				quantity: 3,
				replacedSkuId: 0,
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 0,
				sku: `SKU1`,
				skuId: 100,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
			{
				id: 1002,
				name: `ProductName1`,
				options: '[]',
				quantity: 8,
				replacedSkuId: 0,
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 0,
				sku: `SKU2`,
				skuId: 101,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
		] as Array<IOrderItem>;

		fetchMock.get(
			/headless-commerce-delivery-cart\/.*\/carts\/.*\/items\?.*/i,
			(): IOrderItemAPIResponse => {
				return {
					items: orderItems,
				} as IOrderItemAPIResponse;
			}
		);

		const renderedComponent = render(
			<Multishipping accountId={10} orderId={10} />
		);

		const {loadingSpinner} = getLocators(orderItems, renderedComponent);

		await waitFor(() => {
			expect(loadingSpinner).not.toBeInTheDocument();
		});

		const {row0SelectCheckbox} = getLocators(orderItems, renderedComponent);

		expect(row0SelectCheckbox).toBeVisible();
		expect(row0SelectCheckbox).not.toBeChecked();

		userEvent.click(row0SelectCheckbox);

		const {
			bulkCopyActionButton,
			bulkRemoveActionButton,
			bulkResetActionButton,
			bulkSplitActionButton,
		} = getLocators(orderItems, renderedComponent);

		expect(bulkCopyActionButton).toBeDisabled();
		expect(bulkRemoveActionButton).toBeEnabled();
		expect(bulkResetActionButton).toBeDisabled();
		expect(bulkSplitActionButton).toBeDisabled();
	});

	it('Bulk split action', async () => {
		const orderItems = [
			{
				deliveryGroupName: `DeliveryGroupName1`,
				id: 1000,
				name: `ProductName1`,
				options: '[]',
				quantity: 3,
				replacedSkuId: 0,
				requestedDeliveryDate: '2024-12-12',
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1000,
				sku: `SKU1`,
				skuId: 100,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
			{
				deliveryGroupName: 'DeliveryGroupName1',
				id: 1002,
				name: `ProductName2`,
				options: '[]',
				quantity: 8,
				replacedSkuId: 0,
				requestedDeliveryDate: '2024-12-12',
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1000,
				sku: `SKU2`,
				skuId: 101,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
			{
				deliveryGroupName: 'DeliveryGroupName2',
				id: 1003,
				name: `ProductName3`,
				options: '[]',
				quantity: 8,
				replacedSkuId: 0,
				requestedDeliveryDate: '2024-12-13',
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1000,
				sku: `SKU3`,
				skuId: 102,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
		] as Array<IOrderItem>;

		const mockFetch = jest
			.fn()
			.mockReturnValueOnce(() => {
				return {
					items: orderItems,
				} as IOrderItemAPIResponse;
			})
			.mockReturnValue(() => {
				return {
					cartItems: orderItems,
				} as IOrderItemAPIResponse;
			});

		fetchMock.get(
			/headless-commerce-delivery-cart\/.*\/carts\/.*\/items\?.*/i,
			mockFetch
		);

		fetchMock.patch(
			/headless-commerce-delivery-cart\/.*\/carts\/.*\?.*/i,
			mockFetch
		);

		const renderedComponent = render(
			<Multishipping accountId={10} orderId={10} />
		);

		const {loadingSpinner} = getLocators(orderItems, renderedComponent);

		await waitFor(() => {
			expect(loadingSpinner).not.toBeInTheDocument();
		});

		const {row0SelectCheckbox, row1SelectCheckbox} = getLocators(
			orderItems,
			renderedComponent
		);

		userEvent.click(row0SelectCheckbox);
		userEvent.click(row1SelectCheckbox);

		const {bulkSplitActionButton} = getLocators(
			orderItems,
			renderedComponent
		);

		expect(bulkSplitActionButton).toBeEnabled();

		userEvent.click(bulkSplitActionButton);

		expect(
			isArrayEqual(
				JSON.parse(fetchMock.calls().matched[1][1].body).cartItems,
				JSON.parse(
					'[{"deliveryGroupName":"DeliveryGroupName2","id":0,"options":"[]","quantity":1,"replacedSkuId":0,"requestedDeliveryDate":"2024-12-13","shippingAddressId":1000,"skuId":100},{"deliveryGroupName":"DeliveryGroupName1","id":1000,"options":"[]","quantity":2,"replacedSkuId":0,"requestedDeliveryDate":"2024-12-12","shippingAddressId":1000,"skuId":100},{"deliveryGroupName":"DeliveryGroupName1","id":1002,"options":"[]","quantity":4,"replacedSkuId":0,"requestedDeliveryDate":"2024-12-12","shippingAddressId":1000,"skuId":101},{"deliveryGroupName":"DeliveryGroupName2","id":0,"options":"[]","quantity":4,"replacedSkuId":0,"requestedDeliveryDate":"2024-12-13","shippingAddressId":1000,"skuId":101},{"deliveryGroupName":"DeliveryGroupName2","id":1003,"options":"[]","quantity":8,"replacedSkuId":0,"requestedDeliveryDate":"2024-12-13","shippingAddressId":1000,"skuId":102}]'
				)
			)
		).toBeTruthy();
		expect(fetchMock.calls().matched[1][1].method).toBe('PATCH');
	});

	it('Bulk split action error', async () => {
		const orderItems = [
			{
				deliveryGroupName: `DeliveryGroupName1`,
				id: 1000,
				name: `ProductName1`,
				options: '[]',
				quantity: 3,
				replacedSkuId: 0,
				requestedDeliveryDate: '2024-12-12',
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1000,
				sku: `SKU1`,
				skuId: 100,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
			{
				deliveryGroupName: 'DeliveryGroupName1',
				id: 1002,
				name: `ProductName2`,
				options: '[]',
				quantity: 8,
				replacedSkuId: 0,
				requestedDeliveryDate: '2024-12-12',
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1000,
				sku: `SKU2`,
				skuId: 101,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
		] as Array<IOrderItem>;

		fetchMock.get(
			/headless-commerce-delivery-cart\/.*\/carts\/.*\/items\?.*/i,
			{
				items: orderItems,
			} as IOrderItemAPIResponse
		);

		const spyOnShowError = jest.spyOn(ErrorMessage, 'showError');

		const renderedComponent = render(
			<Multishipping accountId={10} orderId={10} />
		);

		const {loadingSpinner} = getLocators(orderItems, renderedComponent);

		await waitFor(() => {
			expect(loadingSpinner).not.toBeInTheDocument();
		});

		const {row0SelectCheckbox, row1SelectCheckbox} = getLocators(
			orderItems,
			renderedComponent
		);

		userEvent.click(row0SelectCheckbox);
		userEvent.click(row1SelectCheckbox);

		const {bulkSplitActionButton} = getLocators(
			orderItems,
			renderedComponent
		);

		expect(bulkSplitActionButton).toBeEnabled();

		userEvent.click(bulkSplitActionButton);

		expect(spyOnShowError).toBeCalledWith({
			detail: 'the-item-s-quantity-is-not-valid-for-the-number-of-delivery-groups',
		});
	});

	it('Bulk copy action', async () => {
		const orderItems = [
			{
				deliveryGroupName: `DeliveryGroupName1`,
				id: 1000,
				name: `ProductName1`,
				options: '[]',
				quantity: 3,
				replacedSkuId: 0,
				requestedDeliveryDate: '2024-12-12',
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1000,
				sku: `SKU1`,
				skuId: 100,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
			{
				deliveryGroupName: 'DeliveryGroupName1',
				id: 1002,
				name: `ProductName2`,
				options: '[]',
				quantity: 8,
				replacedSkuId: 0,
				requestedDeliveryDate: '2024-12-12',
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1000,
				sku: `SKU2`,
				skuId: 101,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
			{
				deliveryGroupName: 'DeliveryGroupName2',
				id: 1003,
				name: `ProductName3`,
				options: '[]',
				quantity: 8,
				replacedSkuId: 0,
				requestedDeliveryDate: '2024-12-13',
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1000,
				sku: `SKU3`,
				skuId: 102,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
		] as Array<IOrderItem>;

		const mockFetch = jest
			.fn()
			.mockReturnValueOnce(() => {
				return {
					items: orderItems,
				} as IOrderItemAPIResponse;
			})
			.mockReturnValue(() => {
				return {
					cartItems: orderItems,
				} as IOrderItemAPIResponse;
			});

		fetchMock.get(
			/headless-commerce-delivery-cart\/.*\/carts\/.*\/items\?.*/i,
			mockFetch
		);

		fetchMock.patch(
			/headless-commerce-delivery-cart\/.*\/carts\/.*\?.*/i,
			mockFetch
		);

		const renderedComponent = render(
			<Multishipping accountId={10} orderId={10} />
		);

		const {loadingSpinner} = getLocators(orderItems, renderedComponent);

		await waitFor(() => {
			expect(loadingSpinner).not.toBeInTheDocument();
		});

		const {row0SelectCheckbox, row1SelectCheckbox} = getLocators(
			orderItems,
			renderedComponent
		);

		userEvent.click(row0SelectCheckbox);
		userEvent.click(row1SelectCheckbox);

		const {bulkCopyActionButton} = getLocators(
			orderItems,
			renderedComponent
		);

		expect(bulkCopyActionButton).toBeEnabled();

		userEvent.click(bulkCopyActionButton);

		expect(
			isArrayEqual(
				JSON.parse(fetchMock.calls().matched[1][1].body).cartItems,
				JSON.parse(
					'[{"deliveryGroupName":"DeliveryGroupName2","id":0,"options":"[]","quantity":3,"replacedSkuId":0,"requestedDeliveryDate":"2024-12-13","shippingAddressId":1000,"skuId":100},{"deliveryGroupName":"DeliveryGroupName1","id":1000,"options":"[]","quantity":3,"replacedSkuId":0,"requestedDeliveryDate":"2024-12-12","shippingAddressId":1000,"skuId":100},{"deliveryGroupName":"DeliveryGroupName2","id":0,"options":"[]","quantity":8,"replacedSkuId":0,"requestedDeliveryDate":"2024-12-13","shippingAddressId":1000,"skuId":101},{"deliveryGroupName":"DeliveryGroupName1","id":1002,"options":"[]","quantity":8,"replacedSkuId":0,"requestedDeliveryDate":"2024-12-12","shippingAddressId":1000,"skuId":101},{"deliveryGroupName":"DeliveryGroupName2","id":1003,"options":"[]","quantity":8,"replacedSkuId":0,"requestedDeliveryDate":"2024-12-13","shippingAddressId":1000,"skuId":102}]'
				)
			)
		).toBeTruthy();
		expect(fetchMock.calls().matched[1][1].method).toBe('PATCH');
	});

	it('Bulk copy action error', async () => {
		const orderItems = [
			{
				deliveryGroupName: `DeliveryGroupName1`,
				id: 1000,
				name: `ProductName1`,
				options: '[]',
				quantity: 3,
				replacedSkuId: 0,
				requestedDeliveryDate: '2024-12-12',
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1000,
				sku: `SKU1`,
				skuId: 100,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
			{
				deliveryGroupName: 'DeliveryGroupName1',
				id: 1002,
				name: `ProductName2`,
				options: '[]',
				quantity: 8,
				replacedSkuId: 0,
				requestedDeliveryDate: '2024-12-12',
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1000,
				sku: `SKU2`,
				skuId: 101,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
		] as Array<IOrderItem>;

		fetchMock.get(
			/headless-commerce-delivery-cart\/.*\/carts\/.*\/items\?.*/i,
			{
				items: orderItems,
			} as IOrderItemAPIResponse
		);

		const spyOnShowError = jest.spyOn(ErrorMessage, 'showError');

		const renderedComponent = render(
			<Multishipping accountId={10} orderId={10} />
		);

		const {loadingSpinner} = getLocators(orderItems, renderedComponent);

		await waitFor(() => {
			expect(loadingSpinner).not.toBeInTheDocument();
		});

		const {row0SelectCheckbox, row1SelectCheckbox} = getLocators(
			orderItems,
			renderedComponent
		);

		userEvent.click(row0SelectCheckbox);
		userEvent.click(row1SelectCheckbox);

		const {bulkCopyActionButton} = getLocators(
			orderItems,
			renderedComponent
		);

		expect(bulkCopyActionButton).toBeEnabled();

		userEvent.click(bulkCopyActionButton);

		expect(spyOnShowError).toBeCalledWith({
			detail: 'the-item-s-quantity-is-not-valid-for-the-number-of-delivery-groups',
		});
	});

	it('Bulk reset action', async () => {
		const orderItems = [
			{
				deliveryGroupName: `DeliveryGroupName1`,
				id: 1000,
				name: `ProductName1`,
				options: '[]',
				quantity: 3,
				replacedSkuId: 0,
				requestedDeliveryDate: '2024-12-12',
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1000,
				sku: `SKU1`,
				skuId: 100,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
			{
				deliveryGroupName: `DeliveryGroupName2`,
				id: 1001,
				name: `ProductName1`,
				options: '[]',
				quantity: 5,
				replacedSkuId: 0,
				requestedDeliveryDate: '2024-12-13',
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1000,
				sku: `SKU1`,
				skuId: 100,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
			{
				deliveryGroupName: 'DeliveryGroupName2',
				id: 1003,
				name: `ProductName3`,
				options: '[]',
				quantity: 8,
				replacedSkuId: 0,
				requestedDeliveryDate: '2024-12-13',
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1000,
				sku: `SKU3`,
				skuId: 102,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
		] as Array<IOrderItem>;

		const mockFetch = jest
			.fn()
			.mockReturnValueOnce(() => {
				return {
					items: orderItems,
				} as IOrderItemAPIResponse;
			})
			.mockReturnValue(() => {
				return {
					cartItems: orderItems,
				} as IOrderItemAPIResponse;
			});

		fetchMock.get(
			/headless-commerce-delivery-cart\/.*\/carts\/.*\/items\?.*/i,
			mockFetch
		);

		fetchMock.patch(
			/headless-commerce-delivery-cart\/.*\/carts\/.*\?.*/i,
			mockFetch
		);

		const renderedComponent = render(
			<Multishipping accountId={10} orderId={10} />
		);

		const {loadingSpinner} = getLocators(orderItems, renderedComponent);

		await waitFor(() => {
			expect(loadingSpinner).not.toBeInTheDocument();
		});

		const {row0SelectCheckbox, row1SelectCheckbox} = getLocators(
			orderItems,
			renderedComponent
		);

		userEvent.click(row0SelectCheckbox);
		userEvent.click(row1SelectCheckbox);

		const {bulkResetActionButton} = getLocators(
			orderItems,
			renderedComponent
		);

		expect(bulkResetActionButton).toBeEnabled();

		userEvent.click(bulkResetActionButton);

		expect(
			isArrayEqual(
				JSON.parse(fetchMock.calls().matched[1][1].body).cartItems,
				JSON.parse(
					'[{"deliveryGroupName":"DeliveryGroupName1","id":1000,"options":"[]","quantity":1,"replacedSkuId":0,"requestedDeliveryDate":"2024-12-12","shippingAddressId":1000,"skuId":100},{"deliveryGroupName":"DeliveryGroupName1","id":0,"options":"[]","quantity":1,"replacedSkuId":0,"requestedDeliveryDate":"2024-12-12","shippingAddressId":1000,"skuId":102}]'
				)
			)
		).toBeTruthy();
		expect(fetchMock.calls().matched[1][1].method).toBe('PATCH');
	});

	it('Bulk remove action', async () => {
		const orderItems = [
			{
				deliveryGroupName: `DeliveryGroupName1`,
				id: 1000,
				name: `ProductName1`,
				options: '[]',
				quantity: 3,
				replacedSkuId: 0,
				requestedDeliveryDate: '2024-12-12',
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1000,
				sku: `SKU1`,
				skuId: 100,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
			{
				deliveryGroupName: 'DeliveryGroupName2',
				id: 1003,
				name: `ProductName3`,
				options: '[]',
				quantity: 8,
				replacedSkuId: 0,
				requestedDeliveryDate: '2024-12-13',
				settings: {
					maxQuantity: 10000,
					minQuantity: 1,
					multipleQuantity: 1,
				},
				shippingAddressId: 1000,
				sku: `SKU3`,
				skuId: 102,
				thumbnail: '/o/commerce-media/default/?groupId=33472',
			},
		] as Array<IOrderItem>;

		const mockFetch = jest
			.fn()
			.mockReturnValueOnce(() => {
				return {
					items: orderItems,
				} as IOrderItemAPIResponse;
			})
			.mockReturnValue(() => {
				return {
					cartItems: orderItems,
				} as IOrderItemAPIResponse;
			});

		fetchMock.get(
			/headless-commerce-delivery-cart\/.*\/carts\/.*\/items\?.*/i,
			mockFetch
		);

		fetchMock.patch(
			/headless-commerce-delivery-cart\/.*\/carts\/.*\?.*/i,
			mockFetch
		);

		const renderedComponent = render(
			<Multishipping accountId={10} orderId={10} />
		);

		const {loadingSpinner} = getLocators(orderItems, renderedComponent);

		await waitFor(() => {
			expect(loadingSpinner).not.toBeInTheDocument();
		});

		const {row0SelectCheckbox} = getLocators(orderItems, renderedComponent);

		userEvent.click(row0SelectCheckbox);

		const {bulkRemoveActionButton} = getLocators(
			orderItems,
			renderedComponent
		);

		expect(bulkRemoveActionButton).toBeEnabled();

		userEvent.click(bulkRemoveActionButton);

		expect(
			isArrayEqual(
				JSON.parse(fetchMock.calls().matched[1][1].body).cartItems,
				JSON.parse(
					'[{"deliveryGroupName":"DeliveryGroupName2","id":1003,"options":"[]","quantity":8,"replacedSkuId":0,"requestedDeliveryDate":"2024-12-13","shippingAddressId":1000,"skuId":102}]'
				)
			)
		).toBeTruthy();
		expect(fetchMock.calls().matched[1][1].method).toBe('PATCH');
	});
});

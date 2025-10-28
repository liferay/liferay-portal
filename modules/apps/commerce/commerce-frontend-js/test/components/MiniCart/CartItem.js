/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../tests_utilities/polyfills';

import '@testing-library/jest-dom';
import {act, cleanup, fireEvent, render, wait} from '@testing-library/react';
import React from 'react';

import CartItem from '../../../src/main/resources/META-INF/resources/components/mini_cart/CartItem';
import MiniCartContext from '../../../src/main/resources/META-INF/resources/components/mini_cart/MiniCartContext';
import {
	REMOVAL_CANCELING_TIMEOUT,
	REMOVAL_TIMEOUT,
} from '../../../src/main/resources/META-INF/resources/components/mini_cart/util/constants';
import * as MiniCarttests_utilities from '../../../src/main/resources/META-INF/resources/components/mini_cart/util/index';
import {PRODUCT_REMOVED_FROM_CART} from '../../../src/main/resources/META-INF/resources/utilities/eventsDefinitions';

describe.skip('MiniCart Item', () => {
	const BASE_CONTEXT_MOCK = {
		CartResource: {
			deleteItemById: jest.fn().mockReturnValue(Promise.resolve()),
			updateItemById: jest.fn().mockReturnValue(Promise.resolve()),
		},
		actionURLs: {
			orderDetailURL: 'http://order-detail.url',
			productURLSeparator: 'p',
			siteDefaultURL: 'http://localhost:8080/group/siteDefaultUrl',
		},
		cartState: {
			id: 101,
		},
		displayDiscountLevels: false,
		setIsUpdating: jest.fn(),
		updateCartModel: jest.fn().mockReturnValue(Promise.resolve()),
	};

	const BASE_PROPS = {
		item: {
			adaptiveMediaImageHTMLTag: '<picture></picture>',
			cartItems: [],
			id: 202,
			name: 'An Item',
			options: '[]',
			price: {
				currency: 'USD',
				discount: 0.0,
				discountFormatted: '$ 0.00',
				discountPercentage: '0.00',
				discountPercentageLevel1: 0.0,
				discountPercentageLevel2: 0.0,
				discountPercentageLevel3: 0.0,
				discountPercentageLevel4: 0.0,
				finalPrice: 8,
				finalPriceFormatted: '$ 8.00',
				price: 8,
				priceFormatted: '$ 8.00',
				promoPrice: 8,
				promoPriceFormatted: '$ 8.00',
			},
			productURLs: {
				en_US: 'u-joint',
			},
			quantity: 1,
			settings: {
				maxQuantity: 2,
				minQuantity: 1,
				multipleQuantity: 1,
			},
			sku: 'ITEM0001',
			skuId: 10001,
			thumbnail: '',
		},
	};

	const COMPONENT_SELECTOR = '.mini-cart-item';

	const REMOVAL_ANIMATION_MS = 1000;

	beforeEach(() => {
		jest.useFakeTimers();

		BASE_CONTEXT_MOCK.CartResource.deleteItemById = jest
			.fn()
			.mockReturnValue(Promise.resolve());

		BASE_CONTEXT_MOCK.CartResource.updateItemById = jest
			.fn()
			.mockReturnValue(Promise.resolve());

		BASE_CONTEXT_MOCK.setIsUpdating = jest.fn();

		BASE_CONTEXT_MOCK.updateCartModel = jest
			.fn()
			.mockReturnValue(Promise.resolve());

		jest.spyOn(MiniCarttests_utilities, 'parseOptions');

		window.Liferay = {
			Language: {
				get: jest.fn((text) => text),
			},

			fire: jest.fn(),
		};
	});

	afterEach(() => {
		jest.resetAllMocks();

		cleanup();
	});

	afterAll(() => {
		jest.useRealTimers();
	});

	describe('by default', () => {
		it(
			'renders the cart item with its information (ItemInfoView), ' +
				'quantity (QuantitySelector), price (Price), ' +
				'and a button to remove it from the cart, ' +
				'plus a hidden div to handle the item removal cancellation',
			() => {
				const {container} = render(
					<MiniCartContext.Provider value={BASE_CONTEXT_MOCK}>
						<CartItem {...BASE_PROPS} />
					</MiniCartContext.Provider>
				);

				const CartItemElement =
					container.querySelector(COMPONENT_SELECTOR);

				expect(CartItemElement.innerHTML).toMatchSnapshot();
			}
		);

		it("...also with the item's thumbnail", () => {
			const WITH_THUMBNAIL = {
				item: {
					...BASE_PROPS.item,
					thumbnail: 'http://some.url/thumbnail.png',
				},
			};

			const {container} = render(
				<MiniCartContext.Provider value={BASE_CONTEXT_MOCK}>
					<CartItem {...WITH_THUMBNAIL} />
				</MiniCartContext.Provider>
			);

			const CartItemElement = container.querySelector(COMPONENT_SELECTOR);
			const CartItemThumbnailElement = CartItemElement.querySelector(
				`${COMPONENT_SELECTOR}-thumbnail`
			);

			expect(CartItemThumbnailElement).toBeInTheDocument();
			expect(CartItemElement.innerHTML).toMatchSnapshot();
		});

		it('always parses the cart item options', () => {
			render(
				<MiniCartContext.Provider value={BASE_CONTEXT_MOCK}>
					<CartItem {...BASE_PROPS} />
				</MiniCartContext.Provider>
			);

			expect(MiniCarttests_utilities.parseOptions).toHaveBeenCalledTimes(
				1
			);
			expect(MiniCarttests_utilities.parseOptions).toHaveBeenCalledWith(
				BASE_PROPS.item.options
			);
		});

		it('On click redirect to product page', () => {
			Liferay.Util = {navigate: jest.fn()};

			const {getByRole} = render(
				<MiniCartContext.Provider value={BASE_CONTEXT_MOCK}>
					<CartItem {...BASE_PROPS} />
				</MiniCartContext.Provider>
			);

			expect(getByRole('link').href).toBe(
				BASE_CONTEXT_MOCK.actionURLs.siteDefaultURL +
					'/' +
					BASE_CONTEXT_MOCK.actionURLs.productURLSeparator +
					'/' +
					BASE_PROPS.item.productURLs.en_US
			);
		});

		it('On quantitySelector click, no redirect to product page', () => {
			const {getByRole} = render(
				<MiniCartContext.Provider value={BASE_CONTEXT_MOCK}>
					<CartItem {...BASE_PROPS} />
				</MiniCartContext.Provider>
			);
			const mockClick = jest.fn();
			getByRole('link').addEventListener('click', mockClick);

			fireEvent.click(getByRole('textbox'));
			fireEvent.change(getByRole('textbox'), {value: '12'});

			expect(mockClick).not.toBeCalled();
		});

		it('On remove button click, no redirect to product page', () => {
			const {getAllByRole, getByRole} = render(
				<MiniCartContext.Provider value={BASE_CONTEXT_MOCK}>
					<CartItem {...BASE_PROPS} />
				</MiniCartContext.Provider>
			);
			const mockClick = jest.fn();
			getByRole('link').addEventListener('click', mockClick);

			fireEvent.click(getAllByRole('button')[0]);

			expect(mockClick).not.toBeCalled();
		});
	});

	describe('by data flow', () => {
		describe('if the parsed options string is non-empty', () => {
			it('adds the "options" class name to the ItemInfoView wrapper div', () => {
				MiniCarttests_utilities.parseOptions.mockReturnValue('24, L');

				const {container} = render(
					<MiniCartContext.Provider value={BASE_CONTEXT_MOCK}>
						<CartItem {...BASE_PROPS} />
					</MiniCartContext.Provider>
				);

				const ItemInfoViewWrapper = container.querySelector(
					`${COMPONENT_SELECTOR}-info`
				);

				expect(ItemInfoViewWrapper.classList.contains('options')).toBe(
					true
				);
				expect(ItemInfoViewWrapper.innerHTML).toMatchSnapshot();
			});
		});

		describe('if the cart item has errors', () => {
			it('renders a div with icon and error text', () => {
				const ERRORS_PROPS = {
					item: {
						...BASE_PROPS.item,
						errorMessages: 'error',
					},
				};

				const {container} = render(
					<MiniCartContext.Provider value={BASE_CONTEXT_MOCK}>
						<CartItem {...ERRORS_PROPS} />
					</MiniCartContext.Provider>
				);

				const ErrorsElement = container.querySelector(
					`${COMPONENT_SELECTOR}-errors`
				);

				expect(ErrorsElement).toBeInTheDocument();
				expect(ErrorsElement.innerHTML).toMatchSnapshot();
				expect(window.Liferay.Language.get).toHaveBeenCalledWith(
					'an-unexpected-error-occurred'
				);
			});
		});
	});

	describe('by interaction', () => {
		describe('if the cart item delete button is clicked', () => {
			it('unhides the div to handle the item removal cancellation', async () => {
				const {container} = render(
					<MiniCartContext.Provider value={BASE_CONTEXT_MOCK}>
						<CartItem {...BASE_PROPS} />
					</MiniCartContext.Provider>
				);

				const CartItemDeleteButton = container.querySelector(
					`${COMPONENT_SELECTOR}-delete button`
				);

				await act(async () => {
					fireEvent.click(CartItemDeleteButton);
				});

				await wait(() => {
					jest.advanceTimersByTime(REMOVAL_TIMEOUT);

					const CartItemElement =
						container.querySelector(COMPONENT_SELECTOR);
					const CartItemRemovalElement =
						CartItemElement.querySelector(
							`${COMPONENT_SELECTOR}-removing.active`
						);

					expect(CartItemRemovalElement).toBeInTheDocument();
					expect(CartItemElement.innerHTML).toMatchSnapshot();
				});
			});

			it('if no action is performed, calls to API to remove the item from the order', async () => {
				const {container} = render(
					<MiniCartContext.Provider value={BASE_CONTEXT_MOCK}>
						<CartItem {...BASE_PROPS} />
					</MiniCartContext.Provider>
				);

				const CartItemElement =
					container.querySelector(COMPONENT_SELECTOR);
				const CartItemDeleteButton = container.querySelector(
					`${COMPONENT_SELECTOR}-delete button`
				);

				await act(async () => {
					fireEvent.click(CartItemDeleteButton);
				});

				await wait(() => {
					jest.advanceTimersByTime(
						REMOVAL_ANIMATION_MS +
							REMOVAL_CANCELING_TIMEOUT +
							REMOVAL_TIMEOUT
					);

					const {CartResource, setIsUpdating, updateCartModel} =
						BASE_CONTEXT_MOCK;

					expect(CartResource.deleteItemById).toHaveBeenCalledWith(
						BASE_PROPS.item.id
					);

					const {id: orderId} = BASE_CONTEXT_MOCK.cartState;

					expect(updateCartModel).toHaveBeenCalledWith({id: orderId});

					expect(setIsUpdating).toHaveBeenCalledTimes(2);
					expect(setIsUpdating.mock.calls).toEqual([[true], [false]]);
					expect(window.Liferay.fire).toHaveBeenCalledWith(
						PRODUCT_REMOVED_FROM_CART,
						{
							skuId: BASE_PROPS.item.skuId,
						}
					);

					expect(
						CartItemElement.classList.contains('is-removed')
					).toBe(true);
				});
			});

			it('if the "undo" button is clicked, cancels the removal of the item', async () => {
				const {container, getByText} = render(
					<MiniCartContext.Provider value={BASE_CONTEXT_MOCK}>
						<CartItem {...BASE_PROPS} />
					</MiniCartContext.Provider>
				);

				const CartItemDeleteButton = container.querySelector(
					`${COMPONENT_SELECTOR}-delete button`
				);

				await act(async () => {
					fireEvent.click(CartItemDeleteButton);
				});

				await act(async () => {
					jest.advanceTimersByTime(REMOVAL_ANIMATION_MS);

					fireEvent.click(getByText('undo'));
				});

				await wait(() => {
					const CartItemRemovalElement = container.querySelector(
						`${COMPONENT_SELECTOR}-removing`
					);

					expect(CartItemRemovalElement).toBeInTheDocument();
					expect(
						CartItemRemovalElement.classList.contains('canceled')
					).toBe(true);

					jest.advanceTimersByTime(REMOVAL_CANCELING_TIMEOUT);
				});
			});
		});

		describe('if the cart item quantity is edited, calls the API and', () => {
			it('updates the item quantity', async () => {
				const UPDATED_QUANTITY = '2';

				const {container} = render(
					<MiniCartContext.Provider value={BASE_CONTEXT_MOCK}>
						<CartItem {...BASE_PROPS} />
					</MiniCartContext.Provider>
				);

				const InputQuantitySelector = container.querySelector(
					`${COMPONENT_SELECTOR}-quantity input`
				);

				await act(async () => {
					fireEvent.change(InputQuantitySelector, {
						target: {
							value: UPDATED_QUANTITY,
						},
					});
				});

				await wait(() => {
					jest.advanceTimersByTime(200);

					const {CartResource, setIsUpdating, updateCartModel} =
						BASE_CONTEXT_MOCK;

					expect(CartResource.updateItemById).toHaveBeenCalledWith(
						BASE_PROPS.item.id,
						{
							...BASE_PROPS.item,
							quantity: parseInt(UPDATED_QUANTITY, 10),
						}
					);

					const {id: orderId} = BASE_CONTEXT_MOCK.cartState;

					expect(updateCartModel).toHaveBeenCalledWith({id: orderId});

					expect(setIsUpdating).toHaveBeenCalledTimes(2);
					expect(setIsUpdating.mock.calls).toEqual([[true], [false]]);
				});
			});

			describe('if the request fails', () => {
				it('renders a div with icon and error text', async () => {
					BASE_CONTEXT_MOCK.CartResource.updateItemById = jest.fn(
						() => Promise.reject()
					);

					const UPDATED_QUANTITY = '2';

					const {container} = render(
						<MiniCartContext.Provider value={BASE_CONTEXT_MOCK}>
							<CartItem {...BASE_PROPS} />
						</MiniCartContext.Provider>
					);

					const InputQuantitySelector = container.querySelector(
						`${COMPONENT_SELECTOR}-quantity input`
					);

					await act(async () => {
						fireEvent.change(InputQuantitySelector, {
							target: {
								value: UPDATED_QUANTITY,
							},
						});
					});

					await wait(() => {
						jest.advanceTimersByTime(200);

						const {CartResource, updateCartModel} =
							BASE_CONTEXT_MOCK;

						expect(
							CartResource.updateItemById
						).toHaveBeenCalledWith(BASE_PROPS.item.id, {
							...BASE_PROPS.item,
							quantity: parseInt(UPDATED_QUANTITY, 10),
						});

						expect(updateCartModel).not.toHaveBeenCalled();

						const ErrorsElement = container.querySelector(
							`${COMPONENT_SELECTOR}-errors`
						);

						expect(ErrorsElement).toBeInTheDocument();
						expect(ErrorsElement.innerHTML).toMatchSnapshot();
						expect(
							window.Liferay.Language.get
						).toHaveBeenCalledWith('an-unexpected-error-occurred');
					});
				});

				it('...which automatically disappears in 2000ms', async () => {
					BASE_CONTEXT_MOCK.CartResource.updateItemById = jest.fn(
						() => Promise.reject()
					);

					const UPDATED_QUANTITY = '2';

					const {container} = render(
						<MiniCartContext.Provider value={BASE_CONTEXT_MOCK}>
							<CartItem {...BASE_PROPS} />
						</MiniCartContext.Provider>
					);

					const InputQuantitySelector = container.querySelector(
						`${COMPONENT_SELECTOR}-quantity input`
					);

					await act(async () => {
						fireEvent.change(InputQuantitySelector, {
							target: {
								value: UPDATED_QUANTITY,
							},
						});
					});

					await wait(() => {
						jest.advanceTimersByTime(2200);

						const ErrorsElement = container.querySelector(
							`${COMPONENT_SELECTOR}-errors`
						);

						expect(ErrorsElement).not.toBeInTheDocument();
					});
				});
			});
		});
	});
});

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, fireEvent, render, waitFor} from '@testing-library/react';
import React from 'react';

import ServiceProvider from '../../../src/main/resources/META-INF/resources/ServiceProvider/index';
import {ALL} from '../../../src/main/resources/META-INF/resources/components/add_to_cart/constants';
import CartItemsListActions from '../../../src/main/resources/META-INF/resources/components/mini_cart/CartItemsListActions';
import MiniCartContext from '../../../src/main/resources/META-INF/resources/components/mini_cart/MiniCartContext';
import {
	REMOVE_ALL_ITEMS,
	VIEW_DETAILS,
} from '../../../src/main/resources/META-INF/resources/components/mini_cart/util/constants';
import {DEFAULT_LABELS} from '../../../src/main/resources/META-INF/resources/components/mini_cart/util/labels';
import * as Basetests_utilities from '../../../src/main/resources/META-INF/resources/utilities';
import {CART_PRODUCT_QUANTITY_CHANGED} from '../../../src/main/resources/META-INF/resources/utilities/eventsDefinitions';

jest.mock(
	'../../../src/main/resources/META-INF/resources/ServiceProvider/index',
	() => {
		const updateCartById = jest.fn();
		const cartApi = {updateCartById};

		return {
			__esModule: true,
			default: {
				DeliveryCartAPI: jest.fn(() => cartApi),
			},
		};
	}
);

describe('MiniCart Items List Actions', () => {
	const CartResource = ServiceProvider.DeliveryCartAPI('v1');

	const BASE_CONTEXT_MOCK = {
		actionURLs: {
			orderDetailURL: 'http://order-detail.url',
		},
		cartState: {
			id: 101,
			summary: {},
		},
		labels: DEFAULT_LABELS,
		setIsUpdating: jest.fn(),
		updateCartModel: jest.fn().mockReturnValue(Promise.resolve()),
	};

	const COMPONENT_SELECTOR = '.mini-cart-header';

	beforeEach(() => {
		CartResource.updateCartById.mockReturnValue(Promise.resolve({id: 101}));
		BASE_CONTEXT_MOCK.setIsUpdating = jest.fn();
		BASE_CONTEXT_MOCK.updateCartModel = jest
			.fn()
			.mockReturnValue(Promise.resolve());

		jest.spyOn(Basetests_utilities, 'liferayNavigate').mockImplementation(
			() => {}
		);

		window.Liferay = {
			Language: {
				get: jest.fn((text) => text),
			},

			ThemeDisplay: {
				isSignedIn: jest.fn(() => true),
			},

			fire: jest.fn(),
		};
	});

	afterEach(() => {
		jest.resetAllMocks();
	});

	describe('by default', () => {
		it(
			'renders the MiniCart items list action buttons ' +
				`${VIEW_DETAILS} and ${REMOVE_ALL_ITEMS} as disabled, ` +
				'plus the action confirmation prompt as hidden',
			() => {
				const {container} = render(
					<MiniCartContext.Provider value={BASE_CONTEXT_MOCK}>
						<CartItemsListActions />
					</MiniCartContext.Provider>
				);

				const ActionsWrapperElement =
					container.querySelector(COMPONENT_SELECTOR);
				const ActionsElement = ActionsWrapperElement.querySelector(
					`${COMPONENT_SELECTOR}-actions`
				);
				const ActionButtonsElements =
					ActionsElement.querySelectorAll('.action');

				expect(ActionsWrapperElement).toBeInTheDocument();
				expect(ActionsElement).toBeInTheDocument();
				expect(ActionButtonsElements.length).toEqual(2);

				ActionButtonsElements.forEach((element) => {
					expect(element).toBeInTheDocument();
					expect(element).toBeDisabled();
					expect(element.innerHTML).toMatchSnapshot();
				});

				const ConfirmationPromptElement = container.querySelector(
					'.confirmation-prompt'
				);

				expect(ConfirmationPromptElement).toBeInTheDocument();
				expect(
					ConfirmationPromptElement.classList.contains('hide')
				).toBe(true);
			}
		);
	});

	describe('if the user is not signed in', () => {
		it(`does not render the "${VIEW_DETAILS}" button`, () => {
			window.Liferay.ThemeDisplay.isSignedIn.mockReturnValue(false);

			const {container} = render(
				<MiniCartContext.Provider value={BASE_CONTEXT_MOCK}>
					<CartItemsListActions />
				</MiniCartContext.Provider>
			);

			const ActionsElement = container.querySelector(
				`${COMPONENT_SELECTOR}-actions`
			);
			const ActionButtonsElements =
				ActionsElement.querySelectorAll('.action');

			expect(ActionButtonsElements.length).toEqual(1);
			expect(
				ActionButtonsElements[0].classList.contains('text-danger')
			).toBe(true);
		});
	});

	describe('by data flow', () => {
		const WITH_ITEMS_CONTEXT_MOCK = {
			...BASE_CONTEXT_MOCK,
			cartState: {
				...BASE_CONTEXT_MOCK.cartState,
				summary: {itemsCount: 1},
			},
		};

		describe('if there are cart items', () => {
			it('the MiniCart items list action buttons are clickable', () => {
				const {container} = render(
					<MiniCartContext.Provider value={WITH_ITEMS_CONTEXT_MOCK}>
						<CartItemsListActions />
					</MiniCartContext.Provider>
				);

				const ActionsWrapperElement =
					container.querySelector(COMPONENT_SELECTOR);
				const ActionsElement = ActionsWrapperElement.querySelector(
					`${COMPONENT_SELECTOR}-actions`
				);
				const ActionButtonsElements =
					ActionsElement.querySelectorAll('.action');

				expect(ActionsElement).toBeInTheDocument();
				expect(ActionButtonsElements.length).toEqual(2);

				ActionButtonsElements.forEach((element) => {
					expect(element).toBeInTheDocument();
					expect(element).not.toBeDisabled();
					expect(element.innerHTML).toMatchSnapshot();
				});
			});

			it('renders the items count in singular noun form', () => {
				const {container} = render(
					<MiniCartContext.Provider value={WITH_ITEMS_CONTEXT_MOCK}>
						<CartItemsListActions />
					</MiniCartContext.Provider>
				);

				const ActionsWrapperElement =
					container.querySelector(COMPONENT_SELECTOR);
				const ItemsCountTextElement =
					ActionsWrapperElement.querySelector(
						`${COMPONENT_SELECTOR}-resume`
					);

				expect(ItemsCountTextElement).toBeInTheDocument();

				expect(ItemsCountTextElement.innerHTML).toMatchSnapshot();
				expect(window.Liferay.Language.get).toHaveBeenCalledWith(
					'product'
				);
			});

			it('renders the items count in plural noun form', () => {
				const {container} = render(
					<MiniCartContext.Provider
						value={{
							...WITH_ITEMS_CONTEXT_MOCK,
							cartState: {
								...WITH_ITEMS_CONTEXT_MOCK.cartState,
								summary: {itemsCount: 2},
							},
						}}
					>
						<CartItemsListActions />
					</MiniCartContext.Provider>
				);

				const ActionsWrapperElement =
					container.querySelector(COMPONENT_SELECTOR);
				const ItemsCountTextElement =
					ActionsWrapperElement.querySelector(
						`${COMPONENT_SELECTOR}-resume`
					);

				expect(ItemsCountTextElement).toBeInTheDocument();

				expect(ItemsCountTextElement.innerHTML).toMatchSnapshot();
				expect(window.Liferay.Language.get).toHaveBeenCalledWith(
					'products'
				);
			});

			describe('by interaction', () => {
				it(`if the "${VIEW_DETAILS}" button is clicked, navigates to the order detail page URL`, async () => {
					const {container} = render(
						<MiniCartContext.Provider
							value={WITH_ITEMS_CONTEXT_MOCK}
						>
							<CartItemsListActions />
						</MiniCartContext.Provider>
					);

					const ActionsWrapperElement =
						container.querySelector(COMPONENT_SELECTOR);
					const ActionsElement = ActionsWrapperElement.querySelector(
						`${COMPONENT_SELECTOR}-actions`
					);
					const [viewDetailsButton] =
						ActionsElement.querySelectorAll('.action');

					await act(async () => {
						fireEvent.click(viewDetailsButton);
					});

					await waitFor(() => {
						expect(
							Basetests_utilities.liferayNavigate
						).toHaveBeenCalledWith(
							WITH_ITEMS_CONTEXT_MOCK.actionURLs.orderDetailURL
						);
					});
				});

				it(
					`if the "${REMOVE_ALL_ITEMS}" button is clicked, ` +
						'unhides the confirmation prompt with yes/no buttons',
					async () => {
						const {container} = render(
							<MiniCartContext.Provider
								value={WITH_ITEMS_CONTEXT_MOCK}
							>
								<CartItemsListActions />
							</MiniCartContext.Provider>
						);

						const ActionsWrapperElement =
							container.querySelector(COMPONENT_SELECTOR);
						const ActionsElement =
							ActionsWrapperElement.querySelector(
								`${COMPONENT_SELECTOR}-actions`
							);
						const [, removeAllItemsButton] =
							ActionsElement.querySelectorAll('.action');

						await act(async () => {
							fireEvent.click(removeAllItemsButton);
						});

						await waitFor(() => {
							const ConfirmationPromptElement =
								container.querySelector('.confirmation-prompt');

							expect(
								ConfirmationPromptElement
							).toBeInTheDocument();
							expect(
								ConfirmationPromptElement.classList.contains(
									'hide'
								)
							).toBe(false);
							expect(ConfirmationPromptElement).toMatchSnapshot();
						});
					}
				);

				it(
					`if the "${REMOVE_ALL_ITEMS}" button is clicked, ` +
						'and then the "No" button is clicked, ' +
						'hides the confirmation prompt with yes/no buttons',
					async () => {
						const {container, getByText} = render(
							<MiniCartContext.Provider
								value={WITH_ITEMS_CONTEXT_MOCK}
							>
								<CartItemsListActions />
							</MiniCartContext.Provider>
						);

						const ActionsWrapperElement =
							container.querySelector(COMPONENT_SELECTOR);
						const ActionsElement =
							ActionsWrapperElement.querySelector(
								`${COMPONENT_SELECTOR}-actions`
							);
						const [, removeAllItemsButton] =
							ActionsElement.querySelectorAll('.action');

						await act(async () => {
							fireEvent.click(removeAllItemsButton);
						});

						await act(async () => {
							fireEvent.click(getByText('no'));
						});

						await waitFor(() => {
							const ConfirmationPromptElement =
								container.querySelector('.confirmation-prompt');

							expect(
								ConfirmationPromptElement.classList.contains(
									'hide'
								)
							).toBe(true);
						});
					}
				);

				it(
					`if the "${REMOVE_ALL_ITEMS}" button is clicked, ` +
						'and then the "Yes" button is clicked, ' +
						'calls the API to empty the cart and returns to the initial state on success',
					async () => {
						const {container, getByText} = render(
							<MiniCartContext.Provider
								value={WITH_ITEMS_CONTEXT_MOCK}
							>
								<CartItemsListActions />
							</MiniCartContext.Provider>
						);

						const ActionsWrapperElement =
							container.querySelector(COMPONENT_SELECTOR);
						const ActionsElement =
							ActionsWrapperElement.querySelector(
								`${COMPONENT_SELECTOR}-actions`
							);
						const [, removeAllItemsButton] =
							ActionsElement.querySelectorAll('.action');

						await act(async () => {
							fireEvent.click(removeAllItemsButton);
						});

						await act(async () => {
							fireEvent.click(getByText('yes'));
						});

						await waitFor(() => {
							const {setIsUpdating, updateCartModel} =
								WITH_ITEMS_CONTEXT_MOCK;
							const {id: orderId} =
								WITH_ITEMS_CONTEXT_MOCK.cartState;

							expect(
								CartResource.updateCartById
							).toHaveBeenCalledWith(orderId, {cartItems: []});
							expect(updateCartModel).toHaveBeenCalledWith({
								order: {id: orderId},
								refreshItems: true,
							});
							expect(setIsUpdating).toHaveBeenCalledTimes(2);
							expect(setIsUpdating.mock.calls).toEqual([
								[true],
								[false],
							]);
							expect(window.Liferay.fire).toHaveBeenCalledWith(
								CART_PRODUCT_QUANTITY_CHANGED,
								{
									quantity: 0,
									skuId: ALL,
								}
							);
						});
					}
				);
			});
		});
	});
});

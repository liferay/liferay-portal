/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, cleanup, fireEvent, render, wait} from '@testing-library/react';
import React from 'react';

import MiniCartContext from '../../../src/main/resources/META-INF/resources/components/mini_cart/MiniCartContext';
import OrderButton from '../../../src/main/resources/META-INF/resources/components/mini_cart/OrderButton';
import {
	REVIEW_ORDER,
	SUBMIT_ORDER,
	WORKFLOW_STATUS_APPROVED,
} from '../../../src/main/resources/META-INF/resources/components/mini_cart/util/constants';
import {DEFAULT_LABELS} from '../../../src/main/resources/META-INF/resources/components/mini_cart/util/labels';
import * as Basetests_utilities from '../../../src/main/resources/META-INF/resources/utilities';

describe.skip('MiniCart Order Button', () => {
	const DEFAULT_BUTTON_CLASSES = ['btn', 'btn-block', 'btn-primary'];

	const BASE_CONTEXT_MOCK = {
		actionURLs: {
			checkoutURL: 'http://checkout.url',
			orderDetailURL: 'http://order-detail.url',
		},
		cartState: {},
		labels: DEFAULT_LABELS,
	};

	beforeEach(() => {
		jest.spyOn(Basetests_utilities, 'liferayNavigate');
	});

	afterEach(() => {
		jest.resetAllMocks();

		cleanup();
	});

	describe('by default', () => {
		it('renders a block-styled button element', () => {
			const {container, getByRole} = render(
				<MiniCartContext.Provider value={BASE_CONTEXT_MOCK}>
					<OrderButton />
				</MiniCartContext.Provider>
			);

			const buttonWrapper = container.querySelector('.mini-cart-submit');
			const button = getByRole('button');

			expect(buttonWrapper).toBeInTheDocument();
			expect(button).toBeInTheDocument();
			expect(button.className).toEqual(DEFAULT_BUTTON_CLASSES.join(' '));

			expect(buttonWrapper.innerHTML).toMatchSnapshot();
		});

		it('the button element is disabled as the cart is empty', async () => {
			const {getByRole} = render(
				<MiniCartContext.Provider value={BASE_CONTEXT_MOCK}>
					<OrderButton />
				</MiniCartContext.Provider>
			);

			const button = getByRole('button');

			expect(button.disabled).toBe(true);

			await act(async () => {
				fireEvent.click(button);
			});

			await wait(() => {
				expect(
					Basetests_utilities.liferayNavigate
				).not.toHaveBeenCalled();
			});
		});
	});

	describe('by data flow', () => {
		describe('if order workflow status is', () => {
			it(`not set or ${WORKFLOW_STATUS_APPROVED}, renders a button w/ label as "${BASE_CONTEXT_MOCK.labels[SUBMIT_ORDER]}" to checkout the order`, async () => {
				const {getByText} = render(
					<MiniCartContext.Provider
						value={{
							...BASE_CONTEXT_MOCK,
							...{
								cartState: {
									workflowStatusInfo: {
										code: WORKFLOW_STATUS_APPROVED,
									},
								},
							},
						}}
					>
						<OrderButton />
					</MiniCartContext.Provider>
				);

				const button = getByText(
					BASE_CONTEXT_MOCK.labels[SUBMIT_ORDER]
				);

				expect(button).toBeInTheDocument();
			});

			it(`different from ${WORKFLOW_STATUS_APPROVED}, renders a button w/ label as "${BASE_CONTEXT_MOCK.labels[REVIEW_ORDER]}" to review the order`, async () => {
				const {getByText} = render(
					<MiniCartContext.Provider
						value={{
							...BASE_CONTEXT_MOCK,
							...{
								cartState: {
									workflowStatusInfo: {
										code: 1234,
									},
								},
							},
						}}
					>
						<OrderButton />
					</MiniCartContext.Provider>
				);

				const button = getByText(
					BASE_CONTEXT_MOCK.labels[REVIEW_ORDER]
				);

				expect(button).toBeInTheDocument();
			});
		});

		describe(`if there are cart items`, () => {
			const CONTEXT_MOCK = {
				...BASE_CONTEXT_MOCK,
				cartState: {
					cartItems: [{id: 1}],
					workflowStatusInfo: {},
				},
			};

			it('renders a clickable button element to checkout the order', async () => {
				const {getByRole} = render(
					<MiniCartContext.Provider value={CONTEXT_MOCK}>
						<OrderButton />
					</MiniCartContext.Provider>
				);

				const button = getByRole('button');

				expect(button.disabled).toBe(false);

				await act(async () => {
					fireEvent.click(button);
				});

				await wait(() => {
					expect(
						Basetests_utilities.liferayNavigate
					).toHaveBeenCalledWith(CONTEXT_MOCK.actionURLs.checkoutURL);

					expect(button.innerHTML).toMatchSnapshot();
				});
			});

			describe('if some cart item has errors', () => {
				const CONTEXT_MOCK = {
					...BASE_CONTEXT_MOCK,
					cartState: {
						cartItems: [{errorMessages: 'Error', id: 1}],
					},
				};

				it('renders a clickable button element to review the order', async () => {
					const {getByText} = render(
						<MiniCartContext.Provider value={CONTEXT_MOCK}>
							<OrderButton />
						</MiniCartContext.Provider>
					);

					const button = getByText(CONTEXT_MOCK.labels[REVIEW_ORDER]);

					expect(button.disabled).toBe(false);

					await act(async () => {
						fireEvent.click(button);
					});

					await wait(() => {
						expect(
							Basetests_utilities.liferayNavigate
						).toHaveBeenCalledWith(
							CONTEXT_MOCK.actionURLs.orderDetailURL
						);
					});
				});
			});

			describe('if order workflow status is', () => {
				it(`not set or ${WORKFLOW_STATUS_APPROVED}, renders a clickable button w/ label as "${CONTEXT_MOCK.labels[SUBMIT_ORDER]}" to checkout the order`, async () => {
					const {getByText} = render(
						<MiniCartContext.Provider
							value={{
								...CONTEXT_MOCK,
								...{
									cartState: {
										...CONTEXT_MOCK.cartState,
										workflowStatusInfo: {
											code: WORKFLOW_STATUS_APPROVED,
										},
									},
								},
							}}
						>
							<OrderButton />
						</MiniCartContext.Provider>
					);

					const button = getByText(CONTEXT_MOCK.labels[SUBMIT_ORDER]);

					expect(button).toBeInTheDocument();

					await act(async () => {
						fireEvent.click(button);
					});

					await wait(() => {
						expect(
							Basetests_utilities.liferayNavigate
						).toHaveBeenCalledWith(
							CONTEXT_MOCK.actionURLs.checkoutURL
						);
					});
				});

				it(`different from ${WORKFLOW_STATUS_APPROVED}, renders a clickable button w/ label as "${CONTEXT_MOCK.labels[REVIEW_ORDER]}" to review the order`, async () => {
					const {getByText} = render(
						<MiniCartContext.Provider
							value={{
								...CONTEXT_MOCK,
								...{
									cartState: {
										...CONTEXT_MOCK.cartState,
										workflowStatusInfo: {
											code: 1234,
										},
									},
								},
							}}
						>
							<OrderButton />
						</MiniCartContext.Provider>
					);

					const button = getByText(CONTEXT_MOCK.labels[REVIEW_ORDER]);

					expect(button).toBeInTheDocument();

					await act(async () => {
						fireEvent.click(button);
					});

					await wait(() => {
						expect(
							Basetests_utilities.liferayNavigate
						).toHaveBeenCalledWith(
							CONTEXT_MOCK.actionURLs.orderDetailURL
						);
					});
				});
			});
		});
	});
});

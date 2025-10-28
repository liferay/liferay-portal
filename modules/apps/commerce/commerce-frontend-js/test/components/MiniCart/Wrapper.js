/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render} from '@testing-library/react';
import React from 'react';

import MiniCartContext from '../../../src/main/resources/META-INF/resources/components/mini_cart/MiniCartContext';
import Wrapper from '../../../src/main/resources/META-INF/resources/components/mini_cart/Wrapper';
import {
	HEADER,
	ITEMS_LIST,
	ORDER_BUTTON,
} from '../../../src/main/resources/META-INF/resources/components/mini_cart/util/constants';

describe.skip('MiniCart Wrapper', () => {
	const BASE_CONTEXT_MOCK = {
		CartViews: {
			[HEADER]: () => <div>{HEADER}</div>,
			[ORDER_BUTTON]: () => <div>{ORDER_BUTTON}</div>,
		},
		isOpen: false,
	};

	const COMPONENT_SELECTOR = '.mini-cart-wrapper';

	afterEach(() => {
		jest.resetAllMocks();

		cleanup();
	});

	describe('by default', () => {
		it(`renders the MiniCart Wrapper component w/ only ${HEADER}, an empty div and the ${ORDER_BUTTON}`, () => {
			const {asFragment, container, getByText} = render(
				<MiniCartContext.Provider value={BASE_CONTEXT_MOCK}>
					<Wrapper />
				</MiniCartContext.Provider>
			);

			const WrapperElement = container.querySelector(COMPONENT_SELECTOR);
			const ItemsWrapperElement = WrapperElement.querySelector(
				`${COMPONENT_SELECTOR}-items`
			);

			expect(WrapperElement).toBeInTheDocument();
			expect(ItemsWrapperElement).toBeInTheDocument();
			expect(ItemsWrapperElement.children.length).toEqual(0);

			expect(getByText(HEADER)).toBeInTheDocument();
			expect(getByText(ORDER_BUTTON)).toBeInTheDocument();

			expect(asFragment()).toMatchSnapshot();
		});
	});

	describe('by data flow', () => {
		it(
			'if the cart is open, renders the MiniCart Wrapper component w/ ' +
				`${HEADER}, a DataSetDisplay component wrapping the ${ITEMS_LIST}, ` +
				`and the ${ORDER_BUTTON}`,
			() => {
				const {asFragment, container, getByText} = render(
					<MiniCartContext.Provider
						value={{
							...BASE_CONTEXT_MOCK,
							...{
								CartViews: {
									...BASE_CONTEXT_MOCK.CartViews,
									[ITEMS_LIST]: () => (
										<div id={ITEMS_LIST}>{ITEMS_LIST}</div>
									),
								},
								isOpen: true,
							},
						}}
					>
						<Wrapper />
					</MiniCartContext.Provider>
				);

				const WrapperElement =
					container.querySelector(COMPONENT_SELECTOR);
				const ItemsWrapperElement = WrapperElement.querySelector(
					`${COMPONENT_SELECTOR}-items`
				);

				expect(WrapperElement).toBeInTheDocument();
				expect(ItemsWrapperElement).toBeInTheDocument();

				expect(getByText(HEADER)).toBeInTheDocument();
				expect(getByText(ORDER_BUTTON)).toBeInTheDocument();

				const ItemsListElement = ItemsWrapperElement.querySelector(
					`#${ITEMS_LIST}`
				);

				expect(ItemsListElement).toBeInTheDocument();

				expect(asFragment()).toMatchSnapshot();
			}
		);
	});
});

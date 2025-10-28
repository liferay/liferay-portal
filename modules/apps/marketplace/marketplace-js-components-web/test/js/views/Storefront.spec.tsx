/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';

import {MarketplaceContext} from '../../../src/main/resources/META-INF/resources/js/MarketplaceContext';
import {MarketplaceConfiguration} from '../../../src/main/resources/META-INF/resources/js/types';
import {MarketplaceStorefront} from '../../../src/main/resources/META-INF/resources/js/views/Storefront';
import product from '../__mock__/product';

const marketplaceConfiguration: MarketplaceConfiguration = {
	serviceURL: 'https://backend.marketplace.liferay.com',
	settings: {
		account: {id: 123, name: 'Liferay Labs'},
		channelId: 123,
		cloudProject: 'exte5a2marketplace-extuat',
		references: {fragmentsFilter: '', paymentMethodFilter: ''},
		siteId: 123,
		userAccount: {id: 123, name: 'Ray'},
	},
	url: 'https://marketplace.liferay.com',
};

const onClickBack = jest.fn();
const primaryButton = <button>children</button>;
const setView = jest.fn();

globalThis.Liferay.Util = {
	openToast: jest.fn(),
} as any;

describe('MarketplaceStorefront', () => {
	afterEach(() => {
		cleanup();

		jest.clearAllMocks();
	});

	beforeEach(() => {
		cleanup();

		jest.clearAllTimers();
		jest.restoreAllMocks();
		jest.useFakeTimers();
	});

	it('rendering component with its props', async () => {
		const {queryByText} = render(
			<MarketplaceContext.Provider
				value={
					{
						marketplaceConfiguration,
						product,
						setView,
					} as any
				}
			>
				<MarketplaceStorefront
					onClickBack={onClickBack}
					primaryButton={primaryButton}
				/>
			</MarketplaceContext.Provider>
		);

		const backToListButton = queryByText(
			'back-to-list'
		) as HTMLButtonElement;
		const publisherSupportButton = queryByText(
			'publisher-support'
		) as HTMLButtonElement;

		expect(backToListButton).toBeInTheDocument();
		expect(publisherSupportButton).toBeInTheDocument();
		expect(queryByText('children')).toBeInTheDocument();
		expect(queryByText('copy-and-share')).toBeInTheDocument();
		expect(queryByText('developer')).toBeInTheDocument();
		expect(queryByText('edition')).toBeInTheDocument();
		expect(queryByText('published-date')).toBeInTheDocument();
		expect(queryByText('supported-offerings')).toBeInTheDocument();
		expect(queryByText('supported-versions')).toBeInTheDocument();
		expect(queryByText('terms-and-conditions')).toBeInTheDocument();
		expect(queryByText(product.catalogName)).toBeInTheDocument();
		expect(queryByText(product.description)).toBeInTheDocument();
		expect(queryByText(product.name)).toBeInTheDocument();
		expect(queryByText('Cloud App')).toBeInTheDocument();

		fireEvent.click(backToListButton);

		expect(onClickBack).toHaveBeenCalledTimes(1);
		expect(setView).toHaveBeenCalledTimes(0);

		fireEvent.click(publisherSupportButton);

		await act(async () => {
			jest.runAllTimers();
		});

		expect(queryByText('phone')).toBeInTheDocument();
		expect(
			queryByText('publisher-support-contact-info')
		).toBeInTheDocument();
		expect(queryByText('publisher-support-url')).toBeInTheDocument();
		expect(queryByText('support-email-address')).toBeInTheDocument();
	});

	it('clicking button to call setView', async () => {
		const {queryByText} = render(
			<MarketplaceContext.Provider
				value={
					{
						marketplaceConfiguration,
						product,
						setView,
					} as any
				}
			>
				<MarketplaceStorefront />
			</MarketplaceContext.Provider>
		);

		const backToListButton = queryByText('back-to-list');

		expect(backToListButton).not.toBeInTheDocument();
		expect(onClickBack).toHaveBeenCalledTimes(0);
	});

	it('testing copy to cliboard', async () => {
		Object.assign(navigator, {
			clipboard: {
				writeText: jest.fn().mockResolvedValue(undefined),
			},
		});

		globalThis.Liferay = {
			Language: {
				get: jest.fn((key) => key),
			},
			Util: {
				openToast: jest.fn(),
			},
		} as any;

		const {queryByText} = render(
			<MarketplaceContext.Provider
				value={
					{
						marketplaceConfiguration,
						product,
						setView,
					} as any
				}
			>
				<MarketplaceStorefront
					onClickBack={onClickBack}
					primaryButton={primaryButton}
				/>
			</MarketplaceContext.Provider>
		);

		const copyAndShare = queryByText('copy-and-share') as HTMLSpanElement;

		fireEvent.click(copyAndShare);

		expect(navigator.clipboard.writeText).toHaveBeenCalled();
	});
});

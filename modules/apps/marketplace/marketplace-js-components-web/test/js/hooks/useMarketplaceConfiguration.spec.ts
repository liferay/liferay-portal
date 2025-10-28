/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {renderHook, waitFor} from '@testing-library/react';

import {useMarketplaceConfiguration} from '../../../src/main/resources/META-INF/resources/js/hooks/useMarketplaceConfiguration';

jest.mock('frontend-js-web', () => ({
	createResourceURL: jest.fn(() => ({
		toString: () => 'mocked-url',
	})),
	fetch: jest.fn(),
}));

describe('useMarketplaceConfiguration', () => {
	const mockResponse = {
		authorized: true,
		data: {
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
		},
		loading: false,
	};

	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('will return loading initially and then the fetched data', async () => {
		const {fetch} = require('frontend-js-web');

		fetch.mockResolvedValueOnce({
			json: jest.fn().mockResolvedValue(mockResponse),
			ok: true,
		});

		const {result} = renderHook(() =>
			useMarketplaceConfiguration('mock-base-url')
		);

		expect(result.current).toEqual({
			authorized: false,
			data: null,
			loading: false,
		});

		await waitFor(() =>
			expect(result.current.data).toEqual(mockResponse.data)
		);
	});

	it('will handle fetch failure gracefully', async () => {
		const {fetch} = require('frontend-js-web');

		fetch.mockResolvedValueOnce({
			ok: false,
		});

		const {result} = renderHook(() =>
			useMarketplaceConfiguration('mock-base-url')
		);

		await waitFor(() => expect(result.current.loading).toBe(false));

		expect(result.current).toEqual({
			authorized: false,
			data: null,
			loading: false,
		});
	});
});

/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {renderHook, waitFor} from '@testing-library/react';

import {useMarketplaceAuthorization} from '../../../src/main/resources/META-INF/resources/js/hooks/useMarketplaceAuthorization';
import {MarketplaceAuthorization} from '../../../src/main/resources/META-INF/resources/js/types';

jest.mock('frontend-js-web', () => ({
	createResourceURL: jest.fn(() => ({
		toString: () => 'mocked-url',
	})),
	fetch: jest.fn(),
}));

describe('useMarketplaceAuthorization', () => {
	const mockResponse: MarketplaceAuthorization = {
		accessToken: 'test-token',
		accessTokenExpirationTime: '2025-01-01T00:00:00Z',
	};

	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('will return loading initially and then return the fetched data', async () => {
		const {fetch} = require('frontend-js-web');

		fetch.mockResolvedValueOnce({
			json: jest.fn().mockResolvedValue(mockResponse),
			ok: true,
		});

		const {result} = renderHook(() =>
			useMarketplaceAuthorization('mock-base-url')
		);

		expect(result.current).toEqual({data: null, loading: false});

		jest.mock('frontend-js-web', () => ({
			createResourceURL: jest.fn(() => ({
				toString: () => 'mocked-url',
			})),
			fetch: jest.fn(),
		}));

		await waitFor(() => expect(result.current.data).toEqual(mockResponse));

		expect(result.current).toEqual({data: mockResponse, loading: false});
	});

	it('will handle fetch failure', async () => {
		const {fetch} = require('frontend-js-web');

		fetch.mockResolvedValueOnce({
			ok: false,
		});

		const {result} = renderHook(() =>
			useMarketplaceAuthorization('mock-base-url')
		);

		await waitFor(() => expect(result.current.loading).toBe(false));

		expect(result.current).toEqual({data: null, loading: false});
	});
});

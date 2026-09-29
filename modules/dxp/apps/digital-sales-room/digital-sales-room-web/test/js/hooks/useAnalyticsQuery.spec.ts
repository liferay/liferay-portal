/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// @ts-ignore - Check possibility to install package in ts format

import sha256 from 'hash.js/lib/hash/sha/256';

import {toRequestParams} from '../../../src/main/resources/META-INF/resources/js/common/hooks/useAnalyticsQuery';
import {
	AnalyticsFilters,
	TAnalyticsFilter,
} from '../../../src/main/resources/META-INF/resources/js/main_view/analytics/types';

jest.mock('hash.js/lib/hash/sha/256', () => {
	const digest = jest.fn(() => 'mocked-hash');
	const update = jest.fn(() => ({digest}));

	return {
		__esModule: true,
		default: jest.fn(() => ({update})),
	};
});

const mockSha256 = sha256 as unknown as jest.Mock;

function getFilters(userValue: string[]): TAnalyticsFilter {
	return {
		[AnalyticsFilters.DATE_RANGE]: {
			value: {from: '2026-01-01', to: '2026-01-07'},
		},
		[AnalyticsFilters.ROOM]: {value: {room: null}},
		[AnalyticsFilters.USER]: {value: userValue},
	} as unknown as TAnalyticsFilter;
}

describe('toRequestParams entityId hashing', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('hashes the normalized email address through the sha256 hex digest', () => {
		const params = toRequestParams(
			getFilters(['  Test@Example.COM  ']),
			{}
		);

		expect(params.entityId).toBe('mocked-hash');
		expect(mockSha256).toHaveBeenCalledTimes(1);

		const mockUpdate = mockSha256.mock.results[0].value.update;

		expect(mockUpdate).toHaveBeenCalledWith('test@example.com');

		const mockDigest = mockUpdate.mock.results[0].value.digest;

		expect(mockDigest).toHaveBeenCalledWith('hex');
	});

	it('returns an empty entityId when no email address is present', () => {
		const params = toRequestParams(getFilters([]), {});

		expect(params.entityId).toBe('');
		expect(mockSha256).not.toHaveBeenCalled();
	});
});

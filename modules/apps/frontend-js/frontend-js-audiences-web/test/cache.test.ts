/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Cache} from '../src/main/resources/META-INF/resources/main/cache';

const ANALYTICS_WAIT = 50;

const getBatchSegmentExternalReferenceCodes = jest.fn();

const getRealTimeSegmentExternalReferenceCodes = jest.fn();

function mockAnalytics() {
	Object.defineProperty(global, 'Analytics', {
		configurable: true,
		value: {
			segment: {
				getBatchSegmentExternalReferenceCodes,
				getRealTimeSegmentExternalReferenceCodes,
			},
		},
	});
}

describe('cache', () => {
	afterEach(() => {
		delete (global as any).Analytics;

		delete (navigator as any).userAgent;
	});

	beforeEach(() => {
		jest.clearAllMocks();

		getBatchSegmentExternalReferenceCodes.mockResolvedValue([
			'SEGMENT_BATCH',
		]);
		getRealTimeSegmentExternalReferenceCodes.mockResolvedValue([
			'SEGMENT_REAL_TIME',
		]);

		mockAnalytics();

		Object.defineProperty(navigator, 'userAgent', {
			configurable: true,
			value: 'Mozilla/5.0 (X11; Linux x86_64; rv:151.0) Gecko/20100101 Firefox/151.0',
		});
	});

	describe('getACSegments', () => {
		it('returns the batch and the real time segments', async () => {
			const acSegments = await new Cache(
				new AbortController().signal
			).getACSegments();

			expect(acSegments).toEqual(
				new Set(['SEGMENT_BATCH', 'SEGMENT_REAL_TIME'])
			);
		});

		it('returns a segment reported by both sources once', async () => {
			getBatchSegmentExternalReferenceCodes.mockResolvedValue([
				'SEGMENT_BATCH',
				'SEGMENT_BOTH',
			]);
			getRealTimeSegmentExternalReferenceCodes.mockResolvedValue([
				'SEGMENT_BOTH',
			]);

			const acSegments = await new Cache(
				new AbortController().signal
			).getACSegments();

			expect(acSegments).toEqual(
				new Set(['SEGMENT_BATCH', 'SEGMENT_BOTH'])
			);
		});

		it('requests the segments as soon as the cache is created', async () => {
			const cache = new Cache(new AbortController().signal);

			expect(getBatchSegmentExternalReferenceCodes).toHaveBeenCalledTimes(
				1
			);
			expect(
				getRealTimeSegmentExternalReferenceCodes
			).toHaveBeenCalledTimes(1);

			await cache.getACSegments();
		});

		it('returns the same segments to every caller', async () => {
			const cache = new Cache(new AbortController().signal);

			expect(await cache.getACSegments()).toBe(
				await cache.getACSegments()
			);

			expect(getBatchSegmentExternalReferenceCodes).toHaveBeenCalledTimes(
				1
			);
			expect(
				getRealTimeSegmentExternalReferenceCodes
			).toHaveBeenCalledTimes(1);
		});

		it('waits for the Analytics global object to show up', async () => {
			delete (global as any).Analytics;

			const cache = new Cache(new AbortController().signal);

			expect(
				getBatchSegmentExternalReferenceCodes
			).not.toHaveBeenCalled();

			setTimeout(mockAnalytics, ANALYTICS_WAIT);

			expect(await cache.getACSegments()).toEqual(
				new Set(['SEGMENT_BATCH', 'SEGMENT_REAL_TIME'])
			);
		});

		it('fails when the segments cannot be retrieved', async () => {
			getRealTimeSegmentExternalReferenceCodes.mockRejectedValue(
				new Error('Analytics Cloud is unreachable')
			);

			await expect(
				new Cache(new AbortController().signal).getACSegments()
			).rejects.toThrow('Analytics Cloud is unreachable');
		});
	});

	describe('getUAParser', () => {
		it('parses the current user agent', async () => {
			const cache = new Cache(new AbortController().signal);

			expect(cache.getUAParser().getBrowser().name).toBe('Firefox');

			await cache.getACSegments();
		});

		it('returns the same parser to every caller', async () => {
			const cache = new Cache(new AbortController().signal);

			expect(cache.getUAParser()).toBe(cache.getUAParser());

			await cache.getACSegments();
		});
	});
});

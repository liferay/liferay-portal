/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// @ts-ignore - Check possibility to install package in ts format

import fetchMock from 'fetch-mock';

import AnalyticsClient from '../../src/analytics';
import {DEBOUNCE} from '../../src/utils/constants';
import {INITIAL_ANALYTICS_CONFIG, wait} from '../helpers';

const applicationId = 'Page';

describe('Scrolling Plugin', () => {
	let Analytics: AnalyticsClient;

	beforeEach(() => {

		// Force attaching DOM Content Loaded event

		Object.defineProperty(document, 'readyState', {
			value: 'loading',
			writable: false,
		});

		// Give the document a finite height so the page depth resolves to a
		// meaningful level.

		Object.defineProperty(document.body, 'clientHeight', {
			configurable: true,
			value: 1000,
		});

		fetchMock.mock('*', () => 200);

		// Recreate with a flush interval large enough that the queue is not
		// drained before the debounced scroll depth event is asserted.

		Analytics = AnalyticsClient.create({
			...INITIAL_ANALYTICS_CONFIG,
			flushInterval: 60000,
		});
	});

	afterEach(() => {
		Analytics.reset();
		AnalyticsClient.dispose();

		fetchMock.restore();
	});

	describe('pageDepthReached event', () => {
		it('is fired on scroll when the page reaches a depth level', async () => {
			document.dispatchEvent(new Event('scroll'));

			await wait(DEBOUNCE + 200);

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'pageDepthReached'
			);

			expect(events.length).toBeGreaterThanOrEqual(1);

			expect(events[0]).toEqual(
				expect.objectContaining({
					applicationId,
					eventId: 'pageDepthReached',
					properties: expect.objectContaining({
						depth: expect.any(Number),
					}),
				})
			);

			expect(events[0].properties.depth).toBeGreaterThan(0);
		});
	});
});

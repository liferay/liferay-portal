/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// @ts-ignore - Check possibility to install package in ts format

import fetchMock from 'fetch-mock';

import AnalyticsClient from '../../src/analytics';
import {INITIAL_ANALYTICS_CONFIG} from '../helpers';

const applicationId = 'Page';

describe('Timing Plugin', () => {
	let Analytics: AnalyticsClient;

	beforeEach(() => {

		// Force attaching DOM Content Loaded event

		Object.defineProperty(document, 'readyState', {
			value: 'loading',
			writable: false,
		});

		fetchMock.mock('*', () => 200);

		Analytics = AnalyticsClient.create(INITIAL_ANALYTICS_CONFIG);
	});

	afterEach(() => {
		Analytics.reset();
		AnalyticsClient.dispose();

		fetchMock.restore();
	});

	describe('pageLoaded event', () => {
		it('is fired on the window load event with the page load time', () => {
			window.dispatchEvent(new Event('load'));

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'pageLoaded'
			);

			expect(events.length).toBe(1);

			// test/setup.ts mocks performance.timing with loadEventStart: 1 and
			// navigationStart: 0, so pageLoadTime resolves to 1.

			expect(events[0]).toEqual(
				expect.objectContaining({
					applicationId,
					eventId: 'pageLoaded',
					properties: expect.objectContaining({
						pageLoadTime: 1,
					}),
				})
			);
		});
	});
});

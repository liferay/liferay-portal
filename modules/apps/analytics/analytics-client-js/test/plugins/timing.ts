/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// @ts-ignore - Check possibility to install package in ts format

import fetchMock from 'fetch-mock';

import AnalyticsClient from '../../src/analytics';
import {MARK_NAVIGATION_START} from '../../src/utils/constants';
import {INITIAL_ANALYTICS_CONFIG, mockUserTiming} from '../helpers';

const applicationId = 'Page';

describe('Timing Plugin', () => {
	let Analytics: AnalyticsClient;

	beforeEach(() => {

		// Force attaching DOM Content Loaded event

		Object.defineProperty(document, 'readyState', {
			value: 'loading',
			writable: false,
		});

		mockUserTiming();

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

	describe('pageUnloaded event', () => {
		it('is fired on the window pagehide event with the view duration', () => {
			window.dispatchEvent(new Event('pagehide'));

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'pageUnloaded'
			);

			expect(events.length).toBe(1);

			expect(events[0]).toEqual(
				expect.objectContaining({
					applicationId,
					eventId: 'pageUnloaded',
					properties: expect.objectContaining({
						viewDuration: expect.any(Number),
					}),
				})
			);
		});

		it('is not fired on the deprecated window unload event', () => {
			window.dispatchEvent(new Event('unload'));

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'pageUnloaded'
			);

			expect(events.length).toBe(0);
		});
	});

	describe('back/forward cache', () => {
		it('restarts the view duration when the page is restored', () => {
			window.performance.clearMarks(MARK_NAVIGATION_START);

			const event = new Event('pageshow');

			Object.defineProperty(event, 'persisted', {value: true});

			window.dispatchEvent(event);

			expect(
				window.performance.getEntriesByName(MARK_NAVIGATION_START)
					.length
			).toBe(1);
		});

		it('keeps the current measurement when the page is not restored', () => {
			window.performance.clearMarks(MARK_NAVIGATION_START);

			window.dispatchEvent(new Event('pageshow'));

			expect(
				window.performance.getEntriesByName(MARK_NAVIGATION_START)
					.length
			).toBe(0);
		});
	});
});

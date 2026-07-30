/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// @ts-ignore - Check possibility to install package in ts format

import fetchMock from 'fetch-mock';

import AnalyticsClient from '../../src/analytics';
import {INITIAL_ANALYTICS_CONFIG, mockUserTiming} from '../helpers';

const applicationId = 'Page';

describe('Visibility Plugin', () => {
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

	describe('tabBlurred event', () => {
		it('is fired when the document becomes hidden', () => {
			Object.defineProperty(document, 'hidden', {
				configurable: true,
				value: true,
			});

			document.dispatchEvent(new Event('visibilitychange'));

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'tabBlurred'
			);

			expect(events.length).toBe(1);

			expect(events[0]).toEqual(
				expect.objectContaining({
					applicationId,
					eventId: 'tabBlurred',
				})
			);
		});
	});

	describe('tabFocused event', () => {
		it('is fired when the document becomes visible', () => {
			Object.defineProperty(document, 'hidden', {
				configurable: true,
				value: false,
			});

			document.dispatchEvent(new Event('visibilitychange'));

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'tabFocused'
			);

			expect(events.length).toBe(1);

			expect(events[0]).toEqual(
				expect.objectContaining({
					applicationId,
					eventId: 'tabFocused',
				})
			);
		});
	});

	describe('back/forward cache', () => {
		beforeEach(() => {
			Object.defineProperty(document, 'hidden', {
				configurable: true,
				value: true,
			});

			// Leaving the page disables the tab events

			window.dispatchEvent(new Event('beforeunload'));
		});

		it('reports the tab events again when the page is restored', () => {
			const event = new Event('pageshow');

			Object.defineProperty(event, 'persisted', {value: true});

			window.dispatchEvent(event);

			document.dispatchEvent(new Event('visibilitychange'));

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'tabBlurred'
			);

			expect(events.length).toBe(1);
		});

		it('keeps the tab events disabled when the page is not restored', () => {
			window.dispatchEvent(new Event('pageshow'));

			document.dispatchEvent(new Event('visibilitychange'));

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'tabBlurred'
			);

			expect(events.length).toBe(0);
		});
	});
});

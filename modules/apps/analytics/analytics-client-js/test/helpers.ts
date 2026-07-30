/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Analytics from '../src/analytics';
import {Analytics as AnalyticsType} from '../src/types';

const ENDPOINT_URL = 'https://ac-server.io';
const FARO_BACKEND_ENDPOINT_URL = 'https://ac-backend-server.io';

export const INITIAL_ANALYTICS_CONFIG: AnalyticsType.Config = {
	channelId: '4321',
	dataSourceId: '1234',
	demandbaseAccountEndpoint: '',
	endpointUrl: ENDPOINT_URL,
	faroBackendUrl: FARO_BACKEND_ENDPOINT_URL,
	flushInterval: 0,
	identity: {
		emailAddressHashed: '',
	},
	identityEndpoint: '',
	projectId: '',
	userId: '',
};

/**
 * Flush the current Promise queue.
 */
export function flushPromises() {
	return new Promise((resolve) => setImmediate(resolve));
}

/**
 * Generate a single dummy event.
 */
export function getDummyEvent(eventId = 0, data = {}) {
	return {
		applicationId: 'test' as AnalyticsType.ApplicationId,
		eventId: String(eventId) as AnalyticsType.EventId,
		properties: {
			a: 1,
			b: 2,
			c: 3,
		},
		...data,
	};
}

/**
 * Generate dummy events.
 */
export function getDummyEvents(eventsNumber = 5) {
	const events = [];

	for (let i = 0; i < eventsNumber; i++) {
		events.push(getDummyEvent(i));
	}

	return events;
}

/**
 * Sends dummy events to test the Analytics API
 */
export async function sendDummyEvents(
	analyticsInstance: Analytics,
	eventsNumber?: number
) {
	const events = getDummyEvents(eventsNumber);

	await events.forEach((event) => {
		analyticsInstance.send(
			event.eventId,
			event.applicationId,
			event.properties
		);
	});
}
export async function trackDummyEvents(
	analyticsInstance: Analytics,
	eventsNumber: number
) {
	const events = getDummyEvents(eventsNumber);

	await events.forEach((event) => {
		analyticsInstance.track(event.eventId, event.properties);
	});
}

/**
 * Wait during a test. Cannot use with jest.useFakeTimers()
 */
export function wait(msToWait: number) {
	return new Promise((resolve) => {
		setTimeout(resolve, msToWait);
	});
}

/**
 * jsdom does not implement the User Timing API, which the timing plugin relies
 * on to measure how long a page has been viewed. An unknown start mark resolves
 * to the time origin, mirroring the legacy `navigationStart` timing attribute
 * the plugin falls back to.
 */
export function mockUserTiming() {
	const marks = new Map<string, number>();
	const measures = new Map<string, number>();

	Object.assign(global.performance, {
		clearMarks(name: string) {
			marks.delete(name);
		},
		getEntriesByName(name: string) {
			if (marks.has(name)) {
				return [{duration: 0, name, startTime: marks.get(name)}];
			}

			if (measures.has(name)) {
				return [{duration: measures.get(name), name, startTime: 0}];
			}

			return [];
		},
		mark(name: string) {
			marks.set(name, global.performance.now());
		},
		measure(name: string, startMark: string, endMark?: string) {
			const endTime = endMark
				? marks.get(endMark) || 0
				: global.performance.now();

			measures.set(name, endTime - (marks.get(startMark) || 0));
		},
	});
}

/**
 * Makes an element report a visible, in-viewport bounding box. Pass a partial
 * rect to override specific fields (e.g. to place it outside the viewport).
 */
export function mockVisibleRect(
	element: HTMLElement,
	rect: Partial<DOMRect> = {}
) {
	jest.spyOn(element, 'getBoundingClientRect').mockImplementation(
		() =>
			({
				bottom: 500,
				height: 500,
				left: 0,
				right: 500,
				top: 0,
				width: 500,
				...rect,
			}) as DOMRect
	);
}

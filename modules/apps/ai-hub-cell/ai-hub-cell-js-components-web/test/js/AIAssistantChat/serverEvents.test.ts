/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EventSource} from 'eventsource';

import {subscribeToServerEvents} from '../../../src/main/resources/META-INF/resources/js/AIAssistantChat/serverEvents';

function createFakeEventSource() {
	const listeners = new Map<string, Set<(event: MessageEvent) => void>>();

	return {
		addEventListener: (
			eventName: string,
			listener: (event: MessageEvent) => void
		) => {
			if (!listeners.has(eventName)) {
				listeners.set(eventName, new Set());
			}

			listeners.get(eventName)?.add(listener);
		},
		dispatch: (eventName: string, data: string) => {
			listeners
				.get(eventName)
				?.forEach((listener) => listener({data} as MessageEvent));
		},
		removeEventListener: (
			eventName: string,
			listener: (event: MessageEvent) => void
		) => {
			listeners.get(eventName)?.delete(listener);
		},
	};
}

describe('subscribeToServerEvents', () => {
	beforeEach(() => {
		jest.useFakeTimers();

		(Liferay.fire as jest.Mock).mockClear();
	});

	afterEach(() => {
		jest.useRealTimers();
	});

	it('fires the content-changed event a few seconds after a content server event arrives, whatever its payload', () => {
		const eventSource = createFakeEventSource();

		subscribeToServerEvents(eventSource as unknown as EventSource);

		eventSource.dispatch('Content Created', 'any payload');

		expect(Liferay.fire).not.toHaveBeenCalled();

		jest.runAllTimers();

		expect(Liferay.fire).toHaveBeenCalledWith(
			'cms:aiAssistant:contentChanged'
		);
	});

	it('ignores server events that are not bound', () => {
		const eventSource = createFakeEventSource();

		subscribeToServerEvents(eventSource as unknown as EventSource);

		eventSource.dispatch('Chat Message Sent', '{"data": "hello"}');

		jest.runAllTimers();

		expect(Liferay.fire).not.toHaveBeenCalled();
	});

	it('stops firing after unsubscribing', () => {
		const eventSource = createFakeEventSource();

		const unsubscribe = subscribeToServerEvents(
			eventSource as unknown as EventSource
		);

		unsubscribe();

		eventSource.dispatch('Content Updated', 'any payload');

		jest.runAllTimers();

		expect(Liferay.fire).not.toHaveBeenCalled();
	});
});

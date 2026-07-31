/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EventSource} from 'eventsource';

export const CONTENT_CHANGED_EVENT = 'cms:aiAssistant:contentChanged';

const CONTENT_CHANGED_DELAY = 2000;

const SERVER_EVENT_NAMES = ['Content Created', 'Content Updated'];

export function fireContentChanged() {
	setTimeout(() => {
		Liferay.fire(CONTENT_CHANGED_EVENT);
	}, CONTENT_CHANGED_DELAY);
}

export function subscribeToServerEvents(eventSource: EventSource): () => void {
	const unsubscribes = SERVER_EVENT_NAMES.map((serverEventName) => {
		const listener = () => fireContentChanged();

		eventSource.addEventListener(serverEventName, listener);

		return () => eventSource.removeEventListener(serverEventName, listener);
	});

	return () => unsubscribes.forEach((unsubscribe) => unsubscribe());
}

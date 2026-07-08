/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EventSource} from 'eventsource';

import {
	createCategorizationEventSource,
	postCategorizationAgentInstance,
} from '../api';
import {CATEGORIZATION_INTENT_AGENT, IntentVerdict} from '../types';
import {PASSTHROUGH, parseIntent} from '../utils/parseIntent';

const TIMEOUT = 10000;

export function classifyCategorizationIntent(
	message: string
): Promise<IntentVerdict> {
	return new Promise<IntentVerdict>((resolve) => {
		const timeout: {id?: ReturnType<typeof setTimeout>} = {};

		let posted = false;
		let settled = false;
		let source: EventSource | null = null;

		const finish = (verdict: IntentVerdict) => {
			if (settled) {
				return;
			}

			settled = true;

			clearTimeout(timeout.id);

			source?.close();

			resolve(verdict);
		};

		timeout.id = setTimeout(() => finish(PASSTHROUGH), TIMEOUT);

		createCategorizationEventSource()
			.then((eventSource) => {
				if (settled) {
					eventSource?.close();

					return;
				}

				if (!eventSource) {
					finish(PASSTHROUGH);

					return;
				}

				source = eventSource;

				eventSource.addEventListener('Subscribe', (event) => {
					if (posted) {
						return;
					}

					posted = true;

					postCategorizationAgentInstance({
						agent: CATEGORIZATION_INTENT_AGENT,
						context: {message},
						sseEventSinkKey: event.data,
					}).catch(() => finish(PASSTHROUGH));
				});

				eventSource.addEventListener(
					CATEGORIZATION_INTENT_AGENT,
					(event) => {
						try {
							finish(
								parseIntent(JSON.parse(event.data).data ?? '')
							);
						}
						catch {
							finish(PASSTHROUGH);
						}
					}
				);

				eventSource.addEventListener('Agent Invocation Failed', () =>
					finish(PASSTHROUGH)
				);

				eventSource.addEventListener('error', () =>
					finish(PASSTHROUGH)
				);
			})
			.catch(() => finish(PASSTHROUGH));
	});
}

/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EventSource} from 'eventsource';

import {
	AgentContext,
	createAgentInvocationEventSource,
	postAgentInvocation,
} from './api';

export function invokeAgent({
	agentExternalReferenceCode,
	context,
	signal,
	timeout = 60000,
}: {
	agentExternalReferenceCode: string;
	context: AgentContext;
	signal?: AbortSignal;
	timeout?: number;
}): Promise<string> {
	return new Promise<string>((resolve, reject) => {
		let eventSource: EventSource | undefined;
		let settled = false;

		const timeoutId = setTimeout(() => {
			settle(() =>
				reject(new Error('Timed out waiting for the agent response'))
			);
		}, timeout);

		function settle(action: () => void) {
			if (settled) {
				return;
			}

			settled = true;

			clearTimeout(timeoutId);
			eventSource?.close();

			action();
		}

		if (signal) {
			const handleAbort = () => {
				settle(() =>
					reject(new Error('The agent invocation was cancelled'))
				);
			};

			signal.addEventListener('abort', handleAbort, {once: true});

			if (signal.aborted) {
				handleAbort();

				return;
			}
		}

		createAgentInvocationEventSource()
			.then((createdEventSource) => {
				if (settled) {
					createdEventSource.close();

					return;
				}

				eventSource = createdEventSource;

				eventSource.addEventListener(
					'Subscribe',
					(event) => {
						postAgentInvocation({
							agentExternalReferenceCode,
							context,
							sseEventSinkKey: event.data,
						}).catch((error) => {
							settle(() => reject(error));
						});
					},
					{once: true}
				);

				// The SSE event is named after the agent's external reference
				// code.

				eventSource.addEventListener(
					agentExternalReferenceCode,
					(event) => {
						try {
							const {data} = JSON.parse(event.data);

							settle(() => resolve(data ?? ''));
						}
						catch (error) {
							settle(() => reject(error as Error));
						}
					},
					{once: true}
				);

				// A transient reconnect also fires "error" with readyState
				// CONNECTING; only a CLOSED connection has actually failed.

				eventSource.addEventListener('error', () => {
					if (eventSource?.readyState !== EventSource.CLOSED) {
						return;
					}

					settle(() =>
						reject(new Error('Unable to connect to the agent'))
					);
				});
			})
			.catch((error) => {
				settle(() => reject(error));
			});
	});
}

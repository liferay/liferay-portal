/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	createAgentInvocationEventSource,
	postAgentInvocation,
} from '../../../js/agent/api';
import {invokeAgent} from '../../../js/agent/invokeAgent';

jest.mock('../../../js/agent/api');

const AGENT_EXTERNAL_REFERENCE_CODE = 'L_TITLE_GENERATOR';
const AGENT_RESPONSE = 'Best Espresso Machines for Home Brewing';
const PAGE_CONTENT = 'Espresso machines for home brewing';
const SSE_EVENT_SINK_KEY = 'sink-1';
const SUBSCRIBE_EVENT_TYPE = 'Subscribe';

const mockCreateAgentInvocationEventSource =
	createAgentInvocationEventSource as jest.MockedFunction<
		typeof createAgentInvocationEventSource
	>;
const mockPostAgentInvocation = postAgentInvocation as jest.MockedFunction<
	typeof postAgentInvocation
>;

function createFakeEventSource() {
	const listeners: Record<string, (event: {data: string}) => void> = {};

	return {
		addEventListener: jest.fn(
			(type: string, handler: (event: {data: string}) => void) => {
				listeners[type] = handler;
			}
		),
		close: jest.fn(),
		emit(type: string, data: string) {
			listeners[type]?.({data});
		},
	};
}

describe('invokeAgent', () => {
	beforeEach(() => {
		mockCreateAgentInvocationEventSource.mockReset();
		mockPostAgentInvocation.mockReset();
		mockPostAgentInvocation.mockResolvedValue(undefined as never);
	});

	it('subscribes, posts the agent instance, and resolves with the agent response', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateAgentInvocationEventSource.mockResolvedValue(
			fakeEventSource as never
		);

		const promise = invokeAgent({
			agentExternalReferenceCode: AGENT_EXTERNAL_REFERENCE_CODE,
			context: {pageContent: PAGE_CONTENT},
		});

		await Promise.resolve();

		fakeEventSource.emit(SUBSCRIBE_EVENT_TYPE, SSE_EVENT_SINK_KEY);

		expect(mockPostAgentInvocation).toHaveBeenCalledWith({
			agentExternalReferenceCode: AGENT_EXTERNAL_REFERENCE_CODE,
			context: {pageContent: PAGE_CONTENT},
			sseEventSinkKey: SSE_EVENT_SINK_KEY,
		});

		fakeEventSource.emit(
			AGENT_EXTERNAL_REFERENCE_CODE,
			JSON.stringify({data: AGENT_RESPONSE})
		);

		await expect(promise).resolves.toBe(AGENT_RESPONSE);
		expect(fakeEventSource.close).toHaveBeenCalledTimes(1);
	});

	it('rejects when the event source fails to connect', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateAgentInvocationEventSource.mockResolvedValue(
			fakeEventSource as never
		);

		const promise = invokeAgent({
			agentExternalReferenceCode: AGENT_EXTERNAL_REFERENCE_CODE,
			context: {},
		});

		await Promise.resolve();

		fakeEventSource.emit('error', '');

		await expect(promise).rejects.toThrow('Unable to connect to the agent');
	});

	it('rejects when the authorization token request fails', async () => {
		mockCreateAgentInvocationEventSource.mockRejectedValue(
			new Error('Unable to generate authorization token: Unauthorized')
		);

		const promise = invokeAgent({
			agentExternalReferenceCode: AGENT_EXTERNAL_REFERENCE_CODE,
			context: {},
		});

		await expect(promise).rejects.toThrow(
			'Unable to generate authorization token: Unauthorized'
		);
	});

	it('rejects immediately when the agent invocation request fails', async () => {
		mockPostAgentInvocation.mockRejectedValue(
			new Error('Agent invocation failed with status 404: Not Found')
		);

		const fakeEventSource = createFakeEventSource();

		mockCreateAgentInvocationEventSource.mockResolvedValue(
			fakeEventSource as never
		);

		const promise = invokeAgent({
			agentExternalReferenceCode: AGENT_EXTERNAL_REFERENCE_CODE,
			context: {},
		});

		await Promise.resolve();

		fakeEventSource.emit(SUBSCRIBE_EVENT_TYPE, SSE_EVENT_SINK_KEY);

		await expect(promise).rejects.toThrow(
			'Agent invocation failed with status 404: Not Found'
		);
		expect(fakeEventSource.close).toHaveBeenCalledTimes(1);
	});

	it('closes the event source and stops waiting when the signal is aborted', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateAgentInvocationEventSource.mockResolvedValue(
			fakeEventSource as never
		);

		const controller = new AbortController();

		const promise = invokeAgent({
			agentExternalReferenceCode: AGENT_EXTERNAL_REFERENCE_CODE,
			context: {},
			signal: controller.signal,
		});

		controller.abort();

		await expect(promise).rejects.toThrow(
			'The agent invocation was cancelled'
		);
		expect(fakeEventSource.close).toHaveBeenCalledTimes(1);
	});

	it('rejects when no response arrives before the timeout', async () => {
		jest.useFakeTimers();

		const fakeEventSource = createFakeEventSource();

		mockCreateAgentInvocationEventSource.mockResolvedValue(
			fakeEventSource as never
		);

		const promise = invokeAgent({
			agentExternalReferenceCode: AGENT_EXTERNAL_REFERENCE_CODE,
			context: {},
			timeout: 1000,
		});

		promise.catch(() => {});

		jest.advanceTimersByTime(1000);

		await expect(promise).rejects.toThrow(
			'Timed out waiting for the agent response'
		);
		expect(fakeEventSource.close).toHaveBeenCalledTimes(1);

		jest.useRealTimers();
	});
});

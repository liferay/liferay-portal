/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	createCategorizationEventSource,
	postCategorizationAgentInstance,
} from '../../../../src/main/resources/META-INF/resources/js/Categorization/api';
import {classifyCategorizationIntent} from '../../../../src/main/resources/META-INF/resources/js/Categorization/services/classifyCategorizationIntent';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/Categorization/api'
);

const mockCreateEventSource =
	createCategorizationEventSource as jest.MockedFunction<
		typeof createCategorizationEventSource
	>;
const mockPostAgentInstance =
	postCategorizationAgentInstance as jest.MockedFunction<
		typeof postCategorizationAgentInstance
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

const flush = () => new Promise((resolve) => setTimeout(resolve, 0));

describe('classifyCategorizationIntent', () => {
	beforeEach(() => {
		mockCreateEventSource.mockReset();
		mockPostAgentInstance.mockReset();
		mockPostAgentInstance.mockResolvedValue(undefined);
	});

	it('posts the message and resolves the parsed verdict', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);

		const promise = classifyCategorizationIntent('tag this');

		await flush();

		fakeEventSource.emit('Subscribe', 'sink-1');

		fakeEventSource.emit(
			'L_CATEGORIZATION_INTENT',
			JSON.stringify({
				data: JSON.stringify({
					actions: [{agent: 'tag', count: null, targets: []}],
					passthrough: false,
				}),
			})
		);

		const verdict = await promise;

		expect(mockPostAgentInstance).toHaveBeenCalledWith({
			agent: 'L_CATEGORIZATION_INTENT',
			context: {message: 'tag this'},
			sseEventSinkKey: 'sink-1',
		});
		expect(verdict).toEqual({
			actions: [{agent: 'tag', count: 3, targets: []}],
			passthrough: false,
		});
		expect(fakeEventSource.close).toHaveBeenCalled();
	});

	it('passes through and closes the event source after the timeout', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);

		const realSetTimeout = global.setTimeout;

		let timeoutCallback: (() => void) | undefined;

		const setTimeoutSpy = jest
			.spyOn(global, 'setTimeout')
			.mockImplementation(((callback: () => void, ms?: number) => {
				if (ms === 10000) {
					timeoutCallback = callback;

					return 0 as never;
				}

				return realSetTimeout(callback, ms);
			}) as never);

		const promise = classifyCategorizationIntent('what is Liferay?');

		await flush();

		fakeEventSource.emit('Subscribe', 'sink-1');

		timeoutCallback?.();

		const verdict = await promise;

		expect(verdict).toEqual({actions: [], passthrough: true});
		expect(fakeEventSource.close).toHaveBeenCalled();

		setTimeoutSpy.mockRestore();
	});

	it('passes through when the classifier emits an invocation failure', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);

		const promise = classifyCategorizationIntent('tag this');

		await flush();

		fakeEventSource.emit('Subscribe', 'sink-1');

		fakeEventSource.emit('Agent Invocation Failed', '');

		const verdict = await promise;

		expect(verdict).toEqual({actions: [], passthrough: true});
		expect(fakeEventSource.close).toHaveBeenCalled();
	});

	it('passes through when the event source errors', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);

		const promise = classifyCategorizationIntent('tag this');

		await flush();

		fakeEventSource.emit('Subscribe', 'sink-1');

		fakeEventSource.emit('error', '');

		const verdict = await promise;

		expect(verdict).toEqual({actions: [], passthrough: true});
		expect(fakeEventSource.close).toHaveBeenCalled();
	});

	it('passes through when no event source is available', async () => {
		mockCreateEventSource.mockResolvedValue(null);

		const verdict = await classifyCategorizationIntent('tag this');

		expect(verdict).toEqual({actions: [], passthrough: true});
		expect(mockPostAgentInstance).not.toHaveBeenCalled();
	});

	it('passes through and closes the event source when the invocation fails', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);
		mockPostAgentInstance.mockRejectedValue(new Error('boom'));

		const promise = classifyCategorizationIntent('tag this');

		await flush();

		fakeEventSource.emit('Subscribe', 'sink-1');

		await flush();

		const verdict = await promise;

		expect(verdict).toEqual({actions: [], passthrough: true});
		expect(fakeEventSource.close).toHaveBeenCalled();
	});
});

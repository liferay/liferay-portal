/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, renderHook} from '@testing-library/react-hooks';

import {
	createCategorizationEventSource,
	postCategorizationAgentInstance,
} from '../../../src/main/resources/META-INF/resources/js/Categorization/api';
import {ECategorizationAgent} from '../../../src/main/resources/META-INF/resources/js/Categorization/types';
import useCategorizationAgent from '../../../src/main/resources/META-INF/resources/js/Categorization/useCategorizationAgent';

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/Categorization/api'
);

const mockCreateEventSource =
	createCategorizationEventSource as jest.MockedFunction<
		typeof createCategorizationEventSource
	>;
const mockPostAgentInstance =
	postCategorizationAgentInstance as jest.MockedFunction<
		typeof postCategorizationAgentInstance
	>;

const CANDIDATES = [{id: 39001, name: 'International', vocabulary: 'Travel'}];

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

async function renderAgent(agent: ECategorizationAgent) {
	const fakeEventSource = createFakeEventSource();

	mockCreateEventSource.mockResolvedValue(fakeEventSource as never);

	let result;

	await act(async () => {
		({result} = renderHook(() => useCategorizationAgent(agent)));
	});

	return {fakeEventSource, result: result as never};
}

describe('useCategorizationAgent', () => {
	beforeEach(() => {
		mockCreateEventSource.mockReset();
		mockPostAgentInstance.mockReset();
		mockPostAgentInstance.mockResolvedValue(undefined);
	});

	it('posts the stringified candidate context and parses the suggestions', async () => {
		const {fakeEventSource, result} = await renderAgent(
			ECategorizationAgent.AUTO_CATEGORIZE
		);

		await act(async () => {
			(result as {current: {run: Function}}).current.run({
				candidateCategories: CANDIDATES,
				content: 'Japan article',
			});
		});

		act(() => fakeEventSource.emit('Subscribe', 'sink-1'));

		expect(mockPostAgentInstance).toHaveBeenCalledWith({
			agent: 'L_AUTO_CATEGORIZE',
			context: {
				candidateCategories: JSON.stringify(CANDIDATES),
				content: 'Japan article',
				count: 3,
			},
			sseEventSinkKey: 'sink-1',
		});

		expect((result as {current: {status: string}}).current.status).toBe(
			'loading'
		);

		await act(async () => {
			fakeEventSource.emit(
				'L_AUTO_CATEGORIZE',
				JSON.stringify({
					data: '```json\n{"suggestions":[{"id":39001,"confidence":0.9}]}\n```',
					nodeName: 'llm',
				})
			);
		});

		expect(
			(result as {current: {status: string; suggestions: unknown[]}})
				.current
		).toMatchObject({
			status: 'ready',
			suggestions: [{id: 39001, name: 'International'}],
		});
	});

	it('reports an empty status when nothing matches', async () => {
		const {fakeEventSource, result} = await renderAgent(
			ECategorizationAgent.AUTO_CATEGORIZE
		);

		await act(async () => {
			(result as {current: {run: Function}}).current.run({
				candidateCategories: CANDIDATES,
				content: 'unrelated',
			});
		});

		act(() => fakeEventSource.emit('Subscribe', 'sink-1'));

		await act(async () => {
			fakeEventSource.emit(
				'L_AUTO_CATEGORIZE',
				JSON.stringify({data: '{"suggestions":[]}', nodeName: 'llm'})
			);
		});

		expect((result as {current: {status: string}}).current.status).toBe(
			'empty'
		);
	});

	it('defers the invoke until the subscribe event arrives', async () => {
		const {fakeEventSource, result} = await renderAgent(
			ECategorizationAgent.GENERATE_TAGS
		);

		await act(async () => {
			(result as {current: {run: Function}}).current.run({
				content: 'x',
				existingTags: ['Japan'],
			});
		});

		expect(mockPostAgentInstance).not.toHaveBeenCalled();

		await act(async () => {
			fakeEventSource.emit('Subscribe', 'sink-2');
		});

		expect(mockPostAgentInstance).toHaveBeenCalledWith(
			expect.objectContaining({
				agent: 'L_GENERATE_TAGS',
				context: expect.objectContaining({
					existingTags: JSON.stringify(['Japan']),
				}),
				sseEventSinkKey: 'sink-2',
			})
		);
	});

	it('stays stopped when the request fails after it was stopped', async () => {
		mockPostAgentInstance.mockRejectedValue(new Error('Request failed'));

		const {fakeEventSource, result} = await renderAgent(
			ECategorizationAgent.AUTO_CATEGORIZE
		);

		await act(async () => {
			(result as {current: {run: Function}}).current.run({
				candidateCategories: CANDIDATES,
				content: 'Japan article',
			});
		});

		act(() => {
			fakeEventSource.emit('Subscribe', 'sink-1');

			(result as {current: {stop: Function}}).current.stop();
		});

		await act(async () => {});

		expect((result as {current: {status: string}}).current).toMatchObject({
			status: 'stopped',
		});
	});

	it('surfaces an error when the stream fails', async () => {
		const {fakeEventSource, result} = await renderAgent(
			ECategorizationAgent.AUTO_CATEGORIZE
		);

		await act(async () => {
			(result as {current: {run: Function}}).current.run({
				candidateCategories: CANDIDATES,
				content: 'Japan article',
			});
		});

		act(() => fakeEventSource.emit('Subscribe', 'sink-1'));

		await act(async () => {
			fakeEventSource.emit('error', '');
		});

		expect((result as {current: {status: string}}).current).toMatchObject({
			status: 'error',
		});
		expect(fakeEventSource.close).toHaveBeenCalled();
	});

	it('surfaces the error text on agent invocation failure', async () => {
		const {fakeEventSource, result} = await renderAgent(
			ECategorizationAgent.AUTO_CATEGORIZE
		);

		await act(async () => {
			(result as {current: {run: Function}}).current.run({
				candidateCategories: CANDIDATES,
				content: 'Japan article',
			});
		});

		act(() => fakeEventSource.emit('Subscribe', 'sink-1'));

		await act(async () => {
			fakeEventSource.emit(
				'Agent Invocation Failed',
				JSON.stringify({data: 'boom'})
			);
		});

		expect(
			(result as {current: {error: string; status: string}}).current
		).toMatchObject({error: 'boom', status: 'error'});
	});

	it('releases the connect latch and surfaces the error when the channel fails to open', async () => {
		const {result} = await renderAgent(
			ECategorizationAgent.AUTO_CATEGORIZE
		);

		mockCreateEventSource.mockReset();
		mockCreateEventSource.mockRejectedValue(new Error('no channel'));

		await act(async () => {
			(result as {current: {run: Function}}).current.run({
				candidateCategories: CANDIDATES,
				content: 'Japan article',
			});
		});

		expect(
			(result as {current: {error: string; status: string}}).current
		).toMatchObject({
			error: 'an-unexpected-error-occurred',
			status: 'error',
		});

		await act(async () => {
			(result as {current: {run: Function}}).current.run({
				candidateCategories: CANDIDATES,
				content: 'Japan article',
			});
		});

		expect(mockCreateEventSource).toHaveBeenCalledTimes(2);
	});

	it('surfaces the error when the channel is unavailable', async () => {
		const {result} = await renderAgent(
			ECategorizationAgent.AUTO_CATEGORIZE
		);

		mockCreateEventSource.mockReset();
		mockCreateEventSource.mockResolvedValue(null);

		await act(async () => {
			(result as {current: {run: Function}}).current.run({
				candidateCategories: CANDIDATES,
				content: 'Japan article',
			});
		});

		expect(
			(result as {current: {error: string; status: string}}).current
		).toMatchObject({
			error: 'an-unexpected-error-occurred',
			status: 'error',
		});
	});

	it('regenerates by re-posting the last context', async () => {
		const {fakeEventSource, result} = await renderAgent(
			ECategorizationAgent.AUTO_CATEGORIZE
		);

		await act(async () => {
			(result as {current: {run: Function}}).current.run({
				candidateCategories: CANDIDATES,
				content: 'Japan article',
			});
		});

		act(() => fakeEventSource.emit('Subscribe', 'sink-1'));

		await act(async () => {
			(result as {current: {regenerate: Function}}).current.regenerate();
		});

		expect(mockPostAgentInstance).toHaveBeenCalledTimes(2);
	});

	it('closes the channel after a result and opens a fresh one per run', async () => {
		const {fakeEventSource, result} = await renderAgent(
			ECategorizationAgent.AUTO_CATEGORIZE
		);

		await act(async () => {
			(result as {current: {run: Function}}).current.run({
				candidateCategories: CANDIDATES,
				content: 'Japan article',
			});
		});

		act(() => fakeEventSource.emit('Subscribe', 'sink-1'));

		await act(async () => {
			fakeEventSource.emit(
				'L_AUTO_CATEGORIZE',
				JSON.stringify({
					data: '{"suggestions":[{"id":39001,"confidence":0.9}]}',
					nodeName: 'llm',
				})
			);
		});

		expect(fakeEventSource.close).toHaveBeenCalledTimes(1);
		expect(mockCreateEventSource).toHaveBeenCalledTimes(1);

		await act(async () => {
			(result as {current: {run: Function}}).current.run({
				candidateCategories: CANDIDATES,
				content: 'Another article',
			});
		});

		expect(mockCreateEventSource).toHaveBeenCalledTimes(2);
	});
});

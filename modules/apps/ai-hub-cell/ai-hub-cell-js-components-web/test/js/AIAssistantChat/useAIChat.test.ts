/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, renderHook} from '@testing-library/react';

import {
	createEventSource,
	postChatByExternalReferenceCodeMessage,
} from '../../../src/main/resources/META-INF/resources/js/AIAssistantChat/api';
import useAIChat from '../../../src/main/resources/META-INF/resources/js/AIAssistantChat/useAIChat';

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/AIAssistantChat/api',
	() => ({
		createEventSource: jest.fn(() => Promise.resolve(null)),
		executeHttpRequestAction: jest.fn(() => Promise.resolve()),
		postChatByExternalReferenceCodeMessage: jest.fn(() =>
			Promise.resolve()
		),
	})
);

const mockCreateEventSource = createEventSource as jest.MockedFunction<
	typeof createEventSource
>;
const mockPostChat =
	postChatByExternalReferenceCodeMessage as jest.MockedFunction<
		typeof postChatByExternalReferenceCodeMessage
	>;

describe('useAIChat', () => {
	beforeEach(() => {
		mockCreateEventSource.mockReset();
		mockCreateEventSource.mockResolvedValue(null as never);
		mockPostChat.mockReset();
		mockPostChat.mockResolvedValue(undefined as never);
	});

	async function renderSubscribed() {
		const listeners: Record<string, (event: {data: string}) => void> = {};

		const fakeEventSource = {
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

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);

		const {result} = renderHook(() =>
			useAIChat({instructionDefinitionScope: ''})
		);

		await act(async () => {});

		await act(async () => {
			fakeEventSource.emit('Subscribe', 'reference-1');
		});

		return result;
	}

	it('answers a message with an error when the request fails', async () => {
		mockPostChat.mockRejectedValue(new Error('Request failed'));

		const result = await renderSubscribed();

		let sent;

		await act(async () => {
			sent = await result.current.sendMessage('Tag this article');
		});

		expect(sent).toBe(false);
		expect(result.current.messages).toEqual([
			{sender: 'user', text: 'Tag this article'},
			expect.objectContaining({error: true, sender: 'assistant'}),
		]);
		expect(result.current.isGenerating).toBe(false);
	});

	it('reports success when the request is accepted', async () => {
		const result = await renderSubscribed();

		let sent;

		await act(async () => {
			sent = await result.current.sendMessage('Tag this article');
		});

		expect(sent).toBe(true);
		expect(result.current.messages).toEqual([
			{sender: 'user', text: 'Tag this article'},
		]);
	});

	it('answers a message sent with no connection with an error', async () => {
		const {result} = renderHook(() =>
			useAIChat({instructionDefinitionScope: ''})
		);

		await act(async () => {});

		let sent;

		await act(async () => {
			sent = await result.current.sendMessage('Tag this article');
		});

		expect(sent).toBe(false);
		expect(result.current.messages).toEqual([
			{sender: 'user', text: 'Tag this article'},
			expect.objectContaining({error: true, sender: 'assistant'}),
		]);
		expect(result.current.isGenerating).toBe(false);
		expect(Liferay.Util.openToast).toHaveBeenCalledWith(
			expect.objectContaining({type: 'danger'})
		);
		expect(mockPostChat).not.toHaveBeenCalled();
	});

	it('keeps the generating indicator until every balloon has finished', async () => {
		const {result} = renderHook(() =>
			useAIChat({instructionDefinitionScope: ''})
		);

		await act(async () => {});

		act(() => {
			result.current.setBalloonGenerating('balloon-1', true);
			result.current.setBalloonGenerating('balloon-2', true);
		});

		expect(result.current.isGenerating).toBe(true);

		act(() => {
			result.current.setBalloonGenerating('balloon-1', false);
		});

		expect(result.current.isGenerating).toBe(true);

		act(() => {
			result.current.setBalloonGenerating('balloon-2', false);
		});

		expect(result.current.isGenerating).toBe(false);
	});
});

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

	function renderWithChatbot(chatbotExternalReferenceCode?: string) {
		return renderHook(
			(props: {chatbotExternalReferenceCode?: string}) =>
				useAIChat({
					chatbotExternalReferenceCode:
						props.chatbotExternalReferenceCode,
					instructionDefinitionScope: '',
				}),
			{initialProps: {chatbotExternalReferenceCode}}
		);
	}

	it('subscribes with the chatbot', async () => {
		renderWithChatbot('chatbot-1');

		await act(async () => {});

		expect(mockCreateEventSource).toHaveBeenCalledTimes(1);
		expect(mockCreateEventSource).toHaveBeenCalledWith('chatbot-1');
	});

	it('subscribes again when the chatbot changes before a message is sent', async () => {
		const fakeEventSource1 = createFakeEventSource();
		const fakeEventSource2 = createFakeEventSource();

		mockCreateEventSource
			.mockResolvedValueOnce(fakeEventSource1 as never)
			.mockResolvedValueOnce(fakeEventSource2 as never);

		const {rerender, result} = renderWithChatbot();

		await act(async () => {});

		await act(async () => {
			fakeEventSource1.emit('Subscribe', 'reference-1');
		});

		rerender({chatbotExternalReferenceCode: 'chatbot-1'});

		await act(async () => {});

		expect(mockCreateEventSource).toHaveBeenLastCalledWith('chatbot-1');
		expect(fakeEventSource1.close).toHaveBeenCalled();

		await act(async () => {
			fakeEventSource2.emit('Subscribe', 'reference-2');
		});

		await act(async () => {
			await result.current.sendMessage('Hello');
		});

		expect(mockPostChat).toHaveBeenCalledWith(
			expect.objectContaining({
				chatbotExternalReferenceCode: 'chatbot-1',
				eventSourceReference: 'reference-2',
			})
		);
	});

	it('keeps the subscription when the chatbot changes after a message is sent', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);

		const {rerender, result} = renderWithChatbot('chatbot-1');

		await act(async () => {});

		await act(async () => {
			fakeEventSource.emit('Subscribe', 'reference-1');
		});

		await act(async () => {
			await result.current.sendMessage('Hello');
		});

		rerender({chatbotExternalReferenceCode: 'chatbot-2'});

		await act(async () => {});

		expect(mockCreateEventSource).toHaveBeenCalledTimes(1);
		expect(fakeEventSource.close).not.toHaveBeenCalled();
	});

	it('keeps the subscription when the chatbot is cleared', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);

		const {rerender} = renderWithChatbot('chatbot-1');

		await act(async () => {});

		rerender({chatbotExternalReferenceCode: undefined});

		await act(async () => {});

		expect(mockCreateEventSource).toHaveBeenCalledTimes(1);
		expect(fakeEventSource.close).not.toHaveBeenCalled();
	});

	it('closes a subscription that opens after it was replaced', async () => {
		const fakeEventSource1 = createFakeEventSource();
		const fakeEventSource2 = createFakeEventSource();

		let resolveEventSource1: (value: never) => void = () => {};

		mockCreateEventSource
			.mockReturnValueOnce(
				new Promise((resolve) => {
					resolveEventSource1 = resolve;
				})
			)
			.mockResolvedValueOnce(fakeEventSource2 as never);

		const {rerender} = renderWithChatbot();

		rerender({chatbotExternalReferenceCode: 'chatbot-1'});

		await act(async () => {});

		await act(async () => {
			resolveEventSource1(fakeEventSource1 as never);
		});

		expect(fakeEventSource1.close).toHaveBeenCalled();
		expect(fakeEventSource1.addEventListener).not.toHaveBeenCalled();
		expect(fakeEventSource2.close).not.toHaveBeenCalled();
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

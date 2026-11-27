/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EventSource} from 'eventsource';
import {fetch} from 'frontend-js-web';

import {
	createEventSource,
	postChatByExternalReferenceCodeMessage,
} from '../../../src/main/resources/META-INF/resources/js/AIAssistantChat/api';
import {RequestTooLargeError} from '../../../src/main/resources/META-INF/resources/js/utils/throwIfRequestTooLarge';

jest.mock('eventsource', () => ({EventSource: jest.fn()}));

jest.mock('frontend-js-web', () => ({fetch: jest.fn()}));

const mockFetch = fetch as jest.MockedFunction<typeof fetch>;

const authorizationToken = {
	accessToken: 'access-token',
	serviceURL: 'https://ai-hub.liferay.com',
	userToken: 'user-token',
};

const messageArguments = {
	chatContext: {},
	chatbotExternalReferenceCode: 'chatbot-1',
	eventSourceReference: 'sink-1',
	instructionDefinitionScope: 'scope',
	message: 'Hello',
};

function jsonResponse(body: unknown, ok = true, status = ok ? 200 : 500) {
	return {
		json: () => Promise.resolve(body),
		ok,
		status,
		statusText: ok ? 'OK' : 'Internal Server Error',
		text: () => Promise.resolve(JSON.stringify(body)),
	};
}

describe('createEventSource', () => {
	beforeEach(() => {
		document.body.classList.add('has-edit-mode-menu');

		mockFetch.mockReset();
	});

	afterEach(() => {
		document.body.classList.remove('has-edit-mode-menu');
	});

	it('subscribes to the chats in edit mode', async () => {
		mockFetch.mockResolvedValueOnce(
			jsonResponse(authorizationToken) as never
		);

		await createEventSource();

		expect(EventSource).toHaveBeenCalledWith(
			'https://ai-hub.liferay.com/o/ai-hub/v1.0/chats/subscribe',
			expect.anything()
		);
	});

	it('subscribes to the chats with the chatbot', async () => {
		mockFetch.mockResolvedValueOnce(
			jsonResponse(authorizationToken) as never
		);

		await createEventSource('chatbot 1');

		expect(EventSource).toHaveBeenCalledWith(
			'https://ai-hub.liferay.com/o/ai-hub/v1.0/chats/subscribe?chatbotExternalReferenceCode=chatbot%201',
			expect.anything()
		);
	});
});

describe('postChatByExternalReferenceCodeMessage', () => {
	beforeEach(() => {
		mockFetch.mockReset();
	});

	it('throws a too-long error when the request is too large', async () => {
		mockFetch.mockResolvedValueOnce(
			jsonResponse(authorizationToken) as never
		);
		mockFetch.mockResolvedValueOnce(jsonResponse({}, false, 413) as never);

		const error = await postChatByExternalReferenceCodeMessage(
			messageArguments
		).catch((caught) => caught);

		expect(error).toBeInstanceOf(RequestTooLargeError);
		expect(error.message).toBe(
			'the-message-is-too-long-shorten-it-and-try-again'
		);
	});

	it('throws the generic error on any other failure', async () => {
		mockFetch.mockResolvedValueOnce(
			jsonResponse(authorizationToken) as never
		);
		mockFetch.mockResolvedValueOnce(jsonResponse({}, false) as never);

		const error = await postChatByExternalReferenceCodeMessage(
			messageArguments
		).catch((caught) => caught);

		expect(error).not.toBeInstanceOf(RequestTooLargeError);
		expect(error.message).toBe('Unable to send the chat message: 500');
	});
});

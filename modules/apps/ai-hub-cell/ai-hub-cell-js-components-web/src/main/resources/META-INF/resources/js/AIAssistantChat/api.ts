/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EventSource} from 'eventsource';
import {fetch} from 'frontend-js-web';

import postAuthorizationToken from '../utils/postAuthorizationToken';
import {HttpRequestAction} from './types';

const AI_HUB_ENDPOINT = '/o/ai-hub/v1.0';

export interface AIAssistantActionOutcome {
	response?: Response;
	success: boolean;
}

export interface ChatContext {
	fileUploadSelector?: string;
	groupId?: number | string;
	objectEntryFolderExternalReferenceCode?: string;
	[key: string]: unknown;
}

export async function createEventSource() {
	const editMode = document.body.classList.contains('has-edit-mode-menu');

	if (editMode) {
		return null;
	}

	const authorizationToken = await postAuthorizationToken();

	if (!authorizationToken) {
		return null;
	}

	return new EventSource(
		`${authorizationToken.serviceURL}${AI_HUB_ENDPOINT}/chats/subscribe`,
		{
			fetch: (input, init) =>
				fetch(input as RequestInfo, {
					...init,
					headers: new Headers({
						Accept: 'text/event-stream',
						Authorization: `Bearer ${authorizationToken.accessToken}`,
					}),
				}),
			withCredentials: true,
		}
	);
}

async function executeHttpRequestAction({
	body,
	href,
	method,
}: HttpRequestAction) {
	const authorizationToken = await postAuthorizationToken();

	if (!authorizationToken) {
		return;
	}

	return await fetch(href, {
		body: JSON.stringify(body),
		headers: new Headers({
			'Accept': 'application/json',
			'Authorization': `Bearer ${authorizationToken.accessToken}`,
			'Content-Type': 'application/json',
			'Liferay-AI-Hub-Cell-On-Behalf-Of': authorizationToken.userToken,
		}),
		method,
	});
}

export async function postChatByExternalReferenceCodeMessage({
	chatContext,
	chatbotExternalReferenceCode,
	eventSourceReference,
	instructionDefinitionScope,
	message,
}: {
	chatContext: ChatContext;
	chatbotExternalReferenceCode?: string;
	eventSourceReference: string;
	instructionDefinitionScope: string;
	message: string;
}) {
	const authorizationToken = await postAuthorizationToken();

	if (!authorizationToken) {
		throw new Error('Unable to authorize the chat message request');
	}

	const response = await fetch(
		`${authorizationToken.serviceURL}${AI_HUB_ENDPOINT}/chats/by-external-reference-code/${eventSourceReference}/messages`,
		{
			body: JSON.stringify({
				chatbotExternalReferenceCode,
				context: chatContext,
				instructionDefinitionScope,
				text: message,
			}),
			headers: new Headers({
				'Accept': 'application/json',
				'Authorization': `Bearer ${authorizationToken.accessToken}`,
				'Content-Type': 'application/json',
				'Liferay-AI-Hub-Cell-On-Behalf-Of':
					authorizationToken.userToken,
			}),
			method: 'POST',
		}
	);

	if (!response.ok) {
		throw new Error(`Unable to send the chat message: ${response.status}`);
	}

	return response;
}

export async function requestActionOutcome(
	httpRequestAction: HttpRequestAction
): Promise<AIAssistantActionOutcome> {
	try {
		const response = await executeHttpRequestAction(httpRequestAction);

		return {response, success: response?.ok ?? false};
	}
	catch {
		return {success: false};
	}
}

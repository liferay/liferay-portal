/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EventSource} from 'eventsource';

import type {AuthorizationToken, ChatbotConfiguration} from './types';

const AI_HUB_ENDPOINT = '/o/ai-hub/v1.0';

let aiHubURL = '';
let liferayDXPURL = '';

let cellAuthorizationAvailable: boolean | null = null;

export function setURLs(aiHub: string, liferayDXP: string) {
	aiHubURL = aiHub;
	liferayDXPURL = liferayDXP;
}

function isCellAuthorizationAvailable(): boolean {
	return (
		Boolean((window as any).Liferay) && cellAuthorizationAvailable !== false
	);
}

async function postAuthorizationToken(): Promise<AuthorizationToken | null> {
	try {
		const csrfToken = (window as any).Liferay?.authToken;

		const response = await fetch(
			`${liferayDXPURL}/o/ai-hub-cell/v1.0/authorization-tokens`,
			{
				headers: csrfToken
					? new Headers({'x-csrf-token': csrfToken})
					: undefined,
				method: 'POST',
			}
		);

		if (response.status === 404) {
			cellAuthorizationAvailable = false;

			return null;
		}

		if (!response.ok) {
			throw new Error(
				`Unable to generate authorization token: ${response.statusText}`
			);
		}

		const data = (await response.json()) as Partial<AuthorizationToken>;

		if (!data?.accessToken) {
			throw new Error('Unable to generate authorization token.');
		}

		if (!data?.userToken) {
			throw new Error('Unable to generate user token.');
		}

		if (!data?.serviceURL) {
			throw new Error('Unable to find service URL.');
		}

		cellAuthorizationAvailable = true;

		return data as AuthorizationToken;
	}
	catch {
		return null;
	}
}

export async function getChatbotConfiguration(
	chatbotExternalReferenceCode: string
): Promise<ChatbotConfiguration> {
	const headers = new Headers({
		Accept: 'application/json',
	});

	const languageId = (
		window as any
	).Liferay?.ThemeDisplay?.getBCP47LanguageId?.();

	if (languageId) {
		headers.set('Accept-Language', languageId);
	}

	const response = await fetch(
		`${aiHubURL}${AI_HUB_ENDPOINT}/chatbots/by-external-reference-code/${chatbotExternalReferenceCode}`,
		{
			headers,
		}
	);

	if (!response.ok) {
		throw new Error(
			`Unable to fetch chatbot configuration: ${response.statusText}`
		);
	}

	return (await response.json()) as ChatbotConfiguration;
}

export async function createEventSource(): Promise<EventSource | null> {
	return new EventSource(`${aiHubURL}${AI_HUB_ENDPOINT}/chats/subscribe`, {
		fetch: async (input, init) => {
			const headers = new Headers({Accept: 'text/event-stream'});

			if (isCellAuthorizationAvailable()) {
				const authorizationToken = await postAuthorizationToken();

				if (authorizationToken?.accessToken) {
					headers.set(
						'Authorization',
						`Bearer ${authorizationToken.accessToken}`
					);
				}
			}

			return fetch(input as RequestInfo, {
				...init,
				headers,
			});
		},
		withCredentials: true,
	});
}

export async function postChatMessage(
	chatbotExternalReferenceCode: string,
	eventSourceReference: string,
	text: string
): Promise<Response> {
	const headers = new Headers({
		'Accept': 'application/json',
		'Content-Type': 'application/json',
	});

	if (isCellAuthorizationAvailable()) {
		const authorizationToken = await postAuthorizationToken();

		if (authorizationToken) {
			headers.set(
				'Authorization',
				`Bearer ${authorizationToken.accessToken}`
			);
			headers.set(
				'Liferay-AI-Hub-Cell-On-Behalf-Of',
				authorizationToken.userToken
			);
		}
	}

	return fetch(
		`${aiHubURL}${AI_HUB_ENDPOINT}/chats/by-external-reference-code/${eventSourceReference}/messages`,
		{
			body: JSON.stringify({
				chatbotExternalReferenceCode,
				context: {},
				instructionDefinitionScope: 'clickToChat',
				text,
			}),
			headers,
			method: 'POST',
		}
	);
}

export type ReportFeedbackReason =
	| 'agentError'
	| 'harmfulContent'
	| 'inaccurateResponse'
	| 'other'
	| 'personalDataExposure';

export type ReportFeedbackType = 'negative' | 'positive';

export interface ReportFeedbackPayload {
	agentDefinitionExternalReferenceCodes: string[];
	chatbotExternalReferenceCode: string;
	feedback: ReportFeedbackType;
	reason?: ReportFeedbackReason;
	surface: 'clickToChat';
	userMessage?: string;
}

export async function postAIIssueReport(
	payload: ReportFeedbackPayload
): Promise<{id: string}> {
	const response = await fetch(`${aiHubURL}/o/ai-hub/v1.0/reports`, {
		body: JSON.stringify(payload),
		headers: new Headers({
			'Accept': 'application/json',
			'Content-Type': 'application/json',
		}),
		method: 'POST',
	});

	if (!response.ok) {
		throw new Error(
			`Unable to send feedback (${response.status} ${response.statusText})`
		);
	}

	return (await response.json()) as {id: string};
}

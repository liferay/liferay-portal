/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EventSource} from 'eventsource';
import {fetch} from 'frontend-js-web';

import {CATEGORIZATION_INTENT_AGENT, ECategorizationAgent} from './types';

const AI_HUB_ENDPOINT = '/o/ai-hub/v1.0';

interface AuthorizationToken {
	accessToken: string;
	serviceURL: string;
	userToken: string;
}

async function postAuthorizationToken(): Promise<
	AuthorizationToken | undefined
> {
	try {
		const response = await fetch(
			'/o/ai-hub-cell/v1.0/authorization-tokens',
			{
				method: 'POST',
			}
		);

		if (!response.ok) {
			throw new Error(
				`Unable to generate authorization token: ${response.statusText}`
			);
		}

		const data = await response.json();

		if (!data?.accessToken) {
			throw new Error('Unable to generate authorization token.');
		}

		if (!data?.userToken) {
			throw new Error('Unable to generate user token.');
		}

		if (!data?.serviceURL) {
			throw new Error('Unable to find service URL.');
		}

		return data;
	}
	catch (error) {
		console.warn((error as Error).message);
	}
}

export async function createCategorizationEventSource(): Promise<EventSource | null> {
	const editMode = document.body.classList.contains('has-edit-mode-menu');

	if (editMode) {
		return null;
	}

	const authorizationToken = await postAuthorizationToken();

	if (!authorizationToken) {
		throw new Error('Unable to generate authorization token.');
	}

	return new EventSource(
		`${authorizationToken.serviceURL}${AI_HUB_ENDPOINT}/agent-instances/subscribe`,
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

export async function postCategorizationAgentInstance({
	agent,
	context,
	sseEventSinkKey,
}: {
	agent: ECategorizationAgent | typeof CATEGORIZATION_INTENT_AGENT;
	context: Record<string, unknown>;
	sseEventSinkKey: string;
}): Promise<void> {
	const authorizationToken = await postAuthorizationToken();

	if (!authorizationToken) {
		throw new Error('Unable to generate authorization token.');
	}

	const response = await fetch(
		`${authorizationToken.serviceURL}${AI_HUB_ENDPOINT}/agent-instances`,
		{
			body: JSON.stringify({
				agentDefinitionExternalReferenceCode: agent,
				context,
				sseEventSinkKey,
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
		throw new Error(`Unable to invoke agent: ${response.statusText}`);
	}
}

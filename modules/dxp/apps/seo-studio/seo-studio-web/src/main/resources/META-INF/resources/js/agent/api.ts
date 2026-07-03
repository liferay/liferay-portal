/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EventSource} from 'eventsource';
import {fetch} from 'frontend-js-web';

const AI_HUB_ENDPOINT = '/o/ai-hub/v1.0';

export type AgentContext = Record<string, unknown>;

interface AuthorizationToken {
	accessToken: string;
	serviceURL: string;
	userToken: string;
}

async function postAuthorizationToken(): Promise<AuthorizationToken> {
	const response = await fetch('/o/ai-hub-cell/v1.0/authorization-tokens', {
		method: 'POST',
	});

	if (!response.ok) {
		throw new Error(
			`Unable to generate authorization token: ${response.statusText}`
		);
	}

	const data = await response.json();

	if (!data?.accessToken) {
		throw new Error('Unable to generate authorization token');
	}

	if (!data?.userToken) {
		throw new Error('Unable to generate user token');
	}

	if (!data?.serviceURL) {
		throw new Error('Unable to find service URL');
	}

	return data;
}

export async function createAgentInvocationEventSource(): Promise<EventSource> {
	const authorizationToken = await postAuthorizationToken();

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

export async function postAgentInvocation({
	agentExternalReferenceCode,
	context,
	sseEventSinkKey,
}: {
	agentExternalReferenceCode: string;
	context: AgentContext;
	sseEventSinkKey: string;
}): Promise<Response> {
	const authorizationToken = await postAuthorizationToken();

	const response = await fetch(
		`${authorizationToken.serviceURL}${AI_HUB_ENDPOINT}/agent-instances`,
		{
			body: JSON.stringify({
				agentDefinitionExternalReferenceCode:
					agentExternalReferenceCode,
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

	return response;
}

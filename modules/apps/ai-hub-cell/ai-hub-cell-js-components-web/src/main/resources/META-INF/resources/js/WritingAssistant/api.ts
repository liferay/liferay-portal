/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EventSource} from 'eventsource';
import {fetch} from 'frontend-js-web';

import postAuthorizationToken from '../utils/postAuthorizationToken';
import {EActionType} from './types';

const AI_HUB_ENDPOINT = '/o/ai-hub/v1.0';

export async function createEventSource() {
	const editMode = document.body.classList.contains('has-edit-mode-menu');

	if (editMode) {
		return;
	}

	const authorizationToken = await postAuthorizationToken();

	if (!authorizationToken) {
		return;
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

export async function postAgentInstance(
	content: string,
	eventSourceReference: string,
	type: EActionType
) {
	const authorizationToken = await postAuthorizationToken();

	if (!authorizationToken) {
		return;
	}

	await fetch(
		`${authorizationToken.serviceURL}${AI_HUB_ENDPOINT}/agent-instances`,
		{
			body: JSON.stringify({
				agentDefinitionExternalReferenceCode: type,
				context: {
					text: content,
				},
				sseEventSinkKey: eventSourceReference,
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
}

/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

import postAuthorizationToken from '../utils/postAuthorizationToken';
import throwIfRequestTooLarge from '../utils/throwIfRequestTooLarge';

const AI_HUB_ENDPOINT = '/o/ai-hub/v1.0';

export async function putAgentInstanceResume({
	agentInstanceId,
	context,
}: {
	agentInstanceId: number;
	context: Record<string, unknown>;
}) {
	const authorizationToken = await postAuthorizationToken();

	if (!authorizationToken) {
		return;
	}

	const response = await fetch(
		`${authorizationToken.serviceURL}${AI_HUB_ENDPOINT}/agent-instances/${agentInstanceId}/resume`,
		{
			body: JSON.stringify({context}),
			headers: new Headers({
				'Accept': 'application/json',
				'Authorization': `Bearer ${authorizationToken.accessToken}`,
				'Content-Type': 'application/json',
				'Liferay-AI-Hub-Cell-On-Behalf-Of':
					authorizationToken.userToken,
			}),
			method: 'PUT',
		}
	);

	throwIfRequestTooLarge(
		response,
		Liferay.Language.get('the-content-is-too-long-shorten-it-and-try-again')
	);

	if (!response.ok) {
		throw new Error(`Unable to resume agent: ${response.statusText}`);
	}

	return response;
}

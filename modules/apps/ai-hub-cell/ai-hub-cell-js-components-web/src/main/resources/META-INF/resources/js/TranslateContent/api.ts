/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

import postAuthorizationToken from '../utils/postAuthorizationToken';

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

	return await fetch(
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
}

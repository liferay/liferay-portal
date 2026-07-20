/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

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

async function postAuthorizationToken() {
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

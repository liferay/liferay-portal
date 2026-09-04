/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

import postAuthorizationToken from '../utils/postAuthorizationToken';
import throwIfRequestTooLarge from '../utils/throwIfRequestTooLarge';

const AI_HUB_ENDPOINT = '/o/ai-hub/v1.0';

export type ReportFeedbackReason =
	| 'agentError'
	| 'harmfulContent'
	| 'inaccurateResponse'
	| 'other'
	| 'personalDataExposure';

export type ReportFeedbackSurface =
	| 'aiAssistant'
	| 'clickToChat'
	| 'writingAssistant';

export type ReportFeedbackType = 'negative' | 'positive';

export interface ReportFeedbackPayload {
	agentDefinitionExternalReferenceCodes: string[];
	feedback: ReportFeedbackType;
	reason?: ReportFeedbackReason;
	surface: ReportFeedbackSurface;
	userMessage?: string;
}

export async function postAIIssueReport(payload: ReportFeedbackPayload) {
	const authorizationToken = await postAuthorizationToken();

	if (!authorizationToken) {
		throw new Error('Unable to generate authorization token.');
	}

	const response = await fetch(
		`${authorizationToken.serviceURL}${AI_HUB_ENDPOINT}/reports`,
		{
			body: JSON.stringify(payload),
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
		throwIfRequestTooLarge(
			response,
			Liferay.Language.get(
				'the-comment-is-too-long-shorten-it-and-try-again'
			)
		);

		throw new Error(
			`Unable to send feedback (${response.status} ${response.statusText})`
		);
	}

	const responseText = await response.text();

	return responseText ? JSON.parse(responseText) : null;
}

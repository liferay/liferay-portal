/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

import {invokeAgent} from '../../../agent/invokeAgent';
import {AutofixDefinition} from '../autofix_definitions/AutofixDefinition';
import {extractJSON} from '../extractJSON';
import {AutofixCandidate} from '../types/Autofix';

const AUTOFIX_BASE_URI = '/o/seo-studio/v1.0';

export const WORKFLOW_STATUS_APPROVED = 0;
export const WORKFLOW_STATUS_PENDING = 1;

// Fetched and stripped server side to avoid a cross origin request to the
// customer instance.

export async function getPageContent(pageURL: string): Promise<string> {
	const response = await fetch(
		`${AUTOFIX_BASE_URI}/page-content?pageURL=${encodeURIComponent(
			pageURL
		)}`,
		{
			headers: new Headers({
				Accept: 'application/json',
			}),
		}
	);

	if (!response.ok) {
		throw new Error('Unable to fetch the page content');
	}

	const data = await response.json();

	return data.content ?? '';
}

function parseErrorMessage(response: string): string | undefined {
	const parsed = extractJSON(response) as {error?: unknown};

	if (typeof parsed?.error === 'string') {
		return parsed.error;
	}

	return undefined;
}

export async function generateCandidates(
	definition: AutofixDefinition,
	pageContent: string,
	signal?: AbortSignal
): Promise<AutofixCandidate[]> {
	const response = await invokeAgent({
		agentExternalReferenceCode: definition.agentExternalReferenceCode,
		context: {pageContent},
		signal,
	});

	const errorMessage = parseErrorMessage(response);

	if (errorMessage) {
		throw new Error(errorMessage);
	}

	const candidates = definition.parseCandidates(response);

	if (!Array.isArray(candidates)) {
		throw new Error(response.trim());
	}

	return candidates;
}

export async function postAutofix({
	insightType,
	pageURL,
	value,
}: {
	insightType: string;
	pageURL: string;
	value: string;
}): Promise<void> {
	const response = await fetch(`${AUTOFIX_BASE_URI}/autofix`, {
		body: JSON.stringify({insightType, pageURL, value}),
		headers: new Headers({
			'Accept': 'application/json',
			'Content-Type': 'application/json',
		}),
		method: 'POST',
	});

	if (!response.ok) {
		throw new Error('Unable to apply the fix');
	}
}

export async function patchScanInsight(scanInsightId: number): Promise<void> {
	const response = await fetch(
		`/o/seo-studio/scan-insights/${scanInsightId}`,
		{
			body: JSON.stringify({
				resolvedDate: new Date().toISOString(),
				state: WORKFLOW_STATUS_APPROVED,
			}),
			headers: new Headers({
				'Accept': 'application/json',
				'Content-Type': 'application/json',
			}),
			method: 'PATCH',
		}
	);

	if (!response.ok) {
		throw new Error('Unable to mark the insight as resolved');
	}
}

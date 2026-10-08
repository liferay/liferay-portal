/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

import {contextUrl} from '../constants';

export const userBaseURL = '/o/headless-admin-user/v1.0';
export const workflowBaseURL = '/o/headless-admin-workflow/v1.0';

export const HEADERS = new Headers({
	'Accept': 'application/json',
	'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
	'Content-Type': 'application/json',
	'x-csrf-token': Liferay.authToken,
});

export async function publishDefinitionRequest(
	requestBody: WorkflowDefinition,
	groupExternalReferenceCode?: string,
	scope?: string
) {
	const isFeatureFlagActive =
		Liferay.FeatureFlags && Liferay.FeatureFlags['LPD-62272'];

	let body:
		| WorkflowDefinition
		| (WorkflowDefinition & {
				groupExternalReferenceCode: string;
				scope: string;
		  }) = requestBody;

	if (scope === 'ai' && isFeatureFlagActive && groupExternalReferenceCode) {
		body = {...requestBody, groupExternalReferenceCode, scope};
	}

	return await fetch(`${workflowBaseURL}/workflow-definitions/deploy`, {
		body: JSON.stringify(body),
		headers: HEADERS,
		method: 'POST',
	});
}

export function retrieveAccountRoles(accountId: number) {
	return fetch(`${userBaseURL}/accounts/${accountId}/account-roles`, {
		headers: HEADERS,
		method: 'GET',
	});
}

export function retrieveDefinitionRequest(
	definitionName: string,
	versionNumber: number
) {
	let url = `${workflowBaseURL}/workflow-definitions/by-name/${encodeURIComponent(encodeURIComponent(definitionName))}?contentFormat=xml`;

	if (versionNumber) {
		url = `${url}&version=${versionNumber}`;
	}

	return fetch(url, {
		headers: HEADERS,
		method: 'GET',
	});
}

export function retrieveRole(
	roleExternalReferenceCode: string,
	roleId: number
) {
	if (roleExternalReferenceCode) {
		return fetch(
			`${window.location.origin}${contextUrl}${userBaseURL}/roles/by-external-reference-code/${encodeURIComponent(roleExternalReferenceCode)}`,
			{
				headers: HEADERS,
				method: 'GET',
			}
		);
	}

	return fetch(
		`${window.location.origin}${contextUrl}${userBaseURL}/roles/${roleId}`,
		{
			headers: HEADERS,
			method: 'GET',
		}
	);
}

export function retrieveRoles() {
	return fetch(
		`${window.location.origin}${contextUrl}${userBaseURL}/roles?fields=id,name,roleType&pageSize=-1`,
		{
			headers: HEADERS,
			method: 'GET',
		}
	);
}

export function retrieveUsersBy(filterType: string, keywords: string[]) {
	let filterParameter = String();
	for (const keyword of keywords) {
		filterParameter =
			filterParameter + filterType + " eq '" + keyword + "' or ";
	}
	filterParameter = encodeURIComponent(filterParameter)
		.replace(/'/g, '%27')
		.slice(0, -8);

	return fetch(
		`${window.location.origin}${contextUrl}${userBaseURL}/user-accounts?filter=${filterParameter}`,
		{
			headers: HEADERS,
			method: 'GET',
		}
	);
}

export async function saveDefinitionRequest(
	requestBody: WorkflowDefinition,
	groupExternalReferenceCode?: string,
	scope?: string
) {
	const isFeatureFlagActive =
		Liferay.FeatureFlags && Liferay.FeatureFlags['LPD-62272'];

	let body:
		| WorkflowDefinition
		| (WorkflowDefinition & {
				groupExternalReferenceCode: string;
				scope: string;
		  }) = requestBody;

	if (scope === 'ai' && isFeatureFlagActive && groupExternalReferenceCode) {
		body = {...requestBody, groupExternalReferenceCode, scope};
	}

	return await fetch(`${workflowBaseURL}/workflow-definitions/save`, {
		body: JSON.stringify(body),
		headers: HEADERS,
		method: 'POST',
	});
}

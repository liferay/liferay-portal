/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

import {WORKFLOW_STATUS_DRAFT} from '../constants';
import {AgentDefinition} from '../types/AgentDefinition';

const AGENT_DEFINITION_BASE_URI = '/o/ai-hub/agent-definitions';

const AGENT_DEFINITION_BY_ERC_URI = `${AGENT_DEFINITION_BASE_URI}/by-external-reference-code/`;

const AGENT_DEFINITION_V1_0_BASE_URI = '/o/ai-hub/v1.0/agent-definitions';

async function disassociateAgentDefinitionFromContentRetriever(
	agentDefinitionERC: string,
	contentRetrieverERC: string
) {
	return fetch(
		`${AGENT_DEFINITION_BY_ERC_URI}${agentDefinitionERC}` +
			`/agentDefinitionsToContentRetrievers/${contentRetrieverERC}/disassociate`,
		{method: 'POST'}
	);
}

async function disassociateAgentDefinitionFromGuardrail(
	agentDefinitionERC: string,
	guardrailERC: string
) {
	return fetch(
		`${AGENT_DEFINITION_BY_ERC_URI}${agentDefinitionERC}` +
			`/aiHubAgentDefinitionsToAIHubGuardrails/${guardrailERC}/disassociate`,
		{method: 'POST'}
	);
}

async function getAgentDefinition(externalReferenceCode: string) {
	const response = await fetch(
		`${AGENT_DEFINITION_BY_ERC_URI}${externalReferenceCode}` +
			'?nestedFields=agentDefinitionsToContentRetrievers,aiHubAgentDefinitionsToAIHubGuardrails',
		{
			method: 'GET',
		}
	);

	return response.json();
}

async function getAgentDefinitions(params?: Record<string, string>) {
	const baseURL = '/o/ai-hub/v1.0/agent-definitions';

	const queryString = params ? new URLSearchParams(params).toString() : '';

	const url = queryString ? `${baseURL}?${queryString}` : baseURL;

	const response = await fetch(url, {
		method: 'GET',
	});

	return response.json();
}

async function postAgentDefinition(agentDefinition: AgentDefinition) {
	const response = await fetch(AGENT_DEFINITION_BASE_URI, {
		body: JSON.stringify(agentDefinition),
		headers: {
			'Content-Type': 'application/json',
		},
		method: 'POST',
	});

	if (!response.ok) {
		const errorBody = await response.json().catch(() => ({}));

		throw new Error(errorBody?.detail || errorBody?.title || '');
	}

	return response.json();
}

async function postAgentDefinitionDraft() {
	const response = await fetch(`${AGENT_DEFINITION_V1_0_BASE_URI}/draft`, {
		headers: {
			'Content-Type': 'application/json',
		},
		method: 'POST',
	});

	if (!response.ok) {
		const errorBody = await response.json().catch(() => ({}));

		throw new Error(errorBody?.detail || errorBody?.title || '');
	}

	return response.json();
}

async function putAgentDefinition(
	agentDefinition: AgentDefinition,
	externalReferenceCode: string
) {
	const response = await fetch(
		`${AGENT_DEFINITION_BY_ERC_URI}${externalReferenceCode}`,
		{
			body: JSON.stringify(agentDefinition),
			headers: {
				'Content-Type': 'application/json',
			},
			method: 'PUT',
		}
	);

	if (!response.ok) {
		const errorBody = await response.json().catch(() => ({}));

		throw new Error(errorBody?.detail || errorBody?.title || '');
	}

	return response.json();
}

async function putAgentDefinitionDraft(agentDefinition: AgentDefinition) {
	const response = await fetch(
		`${AGENT_DEFINITION_BY_ERC_URI}${agentDefinition.externalReferenceCode}`,
		{
			body: JSON.stringify({
				...agentDefinition,
				status: {code: WORKFLOW_STATUS_DRAFT},
			}),
			headers: {
				'Content-Type': 'application/json',
			},
			method: 'PUT',
		}
	);

	if (!response.ok) {
		const errorBody = await response.json().catch(() => ({}));

		throw new Error(errorBody?.detail || errorBody?.title || '');
	}

	return response.json();
}

async function putAgentDefinitionToContentRetrievers(
	agentDefinitionERC: string,
	contentRetrieverERC: string
) {
	return fetch(
		`${AGENT_DEFINITION_BY_ERC_URI}${agentDefinitionERC}` +
			`/agentDefinitionsToContentRetrievers/${contentRetrieverERC}`,
		{method: 'PUT'}
	);
}

async function putAgentDefinitionToGuardrails(
	agentDefinitionERC: string,
	guardrailERC: string
) {
	return fetch(
		`${AGENT_DEFINITION_BY_ERC_URI}${agentDefinitionERC}` +
			`/aiHubAgentDefinitionsToAIHubGuardrails/${guardrailERC}`,
		{method: 'PUT'}
	);
}

export {
	disassociateAgentDefinitionFromContentRetriever,
	disassociateAgentDefinitionFromGuardrail,
	getAgentDefinition,
	getAgentDefinitions,
	postAgentDefinition,
	postAgentDefinitionDraft,
	putAgentDefinition,
	putAgentDefinitionToContentRetrievers,
	putAgentDefinitionToGuardrails,
	putAgentDefinitionDraft,
};

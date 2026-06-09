/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

import {Chatbot} from '../types/Chatbot';

const CHATBOT_BASE_URI = '/o/ai-hub/chatbots';

const CHATBOT_BY_ERC_URI = `${CHATBOT_BASE_URI}/by-external-reference-code/`;

const HEADERS = new Headers({
	'Accept': 'application/json',
	'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
	'Content-Type': 'application/json',
});

async function disassociateChatbotFromAgentDefinition(
	chatbotERC: string,
	agentERC: string
) {
	return fetch(
		`${CHATBOT_BY_ERC_URI}${chatbotERC}/agentDefinitionsToChatbots/${agentERC}/disassociate`,
		{
			headers: HEADERS,
			method: 'POST',
		}
	);
}

async function getChatbots(params?: Record<string, string>) {
	const queryString = params ? new URLSearchParams(params).toString() : '';

	const url = queryString
		? `${CHATBOT_BASE_URI}?${queryString}`
		: CHATBOT_BASE_URI;

	const response = await fetch(url, {
		headers: HEADERS,
		method: 'GET',
	});

	if (!response.ok) {
		throw new Error('Failed to fetch chatbots');
	}

	return response.json();
}

async function getChatbot(externalReferenceCode: string) {
	const response = await fetch(
		`${CHATBOT_BY_ERC_URI}${externalReferenceCode}?nestedFields=agentDefinitionsToChatbots`,
		{
			headers: HEADERS,
			method: 'GET',
		}
	);

	if (!response.ok) {
		throw new Error();
	}

	return response.json();
}

async function postChatbot(chatbot: Chatbot) {
	const response = await fetch(CHATBOT_BASE_URI, {
		body: JSON.stringify(chatbot),
		headers: HEADERS,
		method: 'POST',
	});

	if (!response.ok) {
		const {message, title} = await response.json().catch(() => ({}));

		throw new Error(title || message || '');
	}

	return response.json();
}

async function putChatbot(
	existingExternalReferenceCode: string,
	chatbot: Chatbot
) {
	const response = await fetch(
		`${CHATBOT_BY_ERC_URI}${existingExternalReferenceCode}`,
		{
			body: JSON.stringify(chatbot),
			headers: HEADERS,
			method: 'PUT',
		}
	);

	if (!response.ok) {
		const {message, title} = await response.json().catch(() => ({}));

		throw new Error(title || message || '');
	}

	return response.json();
}

async function putChatbotAgentDefinitionRelationship(
	chatbotERC: string,
	agentERC: string
) {
	return fetch(
		`${CHATBOT_BY_ERC_URI}${chatbotERC}/agentDefinitionsToChatbots/${agentERC}`,
		{
			headers: HEADERS,
			method: 'PUT',
		}
	);
}

export {
	disassociateChatbotFromAgentDefinition,
	getChatbot,
	getChatbots,
	postChatbot,
	putChatbot,
	putChatbotAgentDefinitionRelationship,
};

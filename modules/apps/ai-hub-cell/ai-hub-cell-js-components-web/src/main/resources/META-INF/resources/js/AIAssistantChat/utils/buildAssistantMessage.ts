/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CONTENT_GAP_ANALYSIS_ERC} from '../constants';
import {ChatMessageSentData, Message} from '../types';
import formatContentGapAnalysis from './formatContentGapAnalysis';

const TEXT_ANSWER_FORMATTERS: Record<string, (data: string) => string | null> =
	{
		[CONTENT_GAP_ANALYSIS_ERC]: formatContentGapAnalysis,
	};

function formatTextAnswer(
	data: string,
	agentDefinitionExternalReferenceCodes: string[]
): string {
	for (const agentDefinitionExternalReferenceCode of agentDefinitionExternalReferenceCodes) {
		const formatter =
			TEXT_ANSWER_FORMATTERS[agentDefinitionExternalReferenceCode];

		if (formatter) {
			return formatter(data) ?? data;
		}
	}

	return data;
}

export default function buildAssistantMessage(
	dataJSON: ChatMessageSentData
): Message {
	const agentDefinitionExternalReferenceCodes =
		dataJSON.agentDefinitionExternalReferenceCodes ?? [];

	const data = dataJSON.data ?? '';

	if (dataJSON.type === 'component' && dataJSON.component) {
		return {
			agentDefinitionExternalReferenceCodes,
			component: dataJSON.component,
			sender: 'assistant',
			text: '',
		};
	}

	if (dataJSON.type === 'image') {
		return {
			agentDefinitionExternalReferenceCodes,
			images: [`data:${dataJSON.mimeType ?? 'image/png'};base64,${data}`],
			sender: 'assistant',
			text: '',
		};
	}

	return {
		agentDefinitionExternalReferenceCodes,
		sender: 'assistant',
		text: formatTextAnswer(data, agentDefinitionExternalReferenceCodes),
	};
}

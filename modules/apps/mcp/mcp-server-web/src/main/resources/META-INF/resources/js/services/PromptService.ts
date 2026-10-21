/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Prompt, PromptPayload} from '../types/Prompt';
import ApiHelper from './ApiHelper';

const PROMPTS_URL = '/o/mcp/server-prompts';

function deletePrompt(id: number) {
	return ApiHelper.del<void>(`${PROMPTS_URL}/${id}`);
}

function getPrompt(id: number) {
	return ApiHelper.get<Prompt>(`${PROMPTS_URL}/${id}`);
}

function patchPrompt(id: number, payload: PromptPayload) {
	return ApiHelper.patch<Prompt>(`${PROMPTS_URL}/${id}`, payload);
}

function postPrompt(payload: PromptPayload) {
	return ApiHelper.post<Prompt>(PROMPTS_URL, payload);
}

export default {
	deletePrompt,
	getPrompt,
	patchPrompt,
	postPrompt,
};

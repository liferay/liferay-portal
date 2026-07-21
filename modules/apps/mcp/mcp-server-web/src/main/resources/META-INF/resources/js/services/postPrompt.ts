/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Prompt, PromptPayload} from '../types';
import ApiHelper, {RequestResult} from './ApiHelper';
import {PROMPTS_URL} from './constants';

export function postPrompt(
	payload: PromptPayload
): Promise<RequestResult<Prompt>> {
	return ApiHelper.post<Prompt>(PROMPTS_URL, payload);
}

/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {addParams} from 'frontend-js-web';

import ApiHelper, {RequestResult} from './ApiHelper';

const REDACTION_URL = '/o/headless-data-mask/v1.0/redaction';

export type RedactionRequest = {
	detectionRegex: string;
	replacementRegex: string;
	replacementValue: string;
	text: string;
};

export type Redaction = {
	error?: string;
	output: string;
};

export function getRedaction(
	request: RedactionRequest
): Promise<RequestResult<Redaction>> {
	const params: Record<string, string> = {
		detectionRegex: request.detectionRegex,
		replacementValue: request.replacementValue,
		text: request.text,
	};

	if (request.replacementRegex) {
		params.replacementRegex = request.replacementRegex;
	}

	return ApiHelper.get<Redaction>(addParams(params, REDACTION_URL));
}

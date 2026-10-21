/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {addParams} from 'frontend-js-web';

import {Redaction, RedactionRequest} from '../types/Redaction';
import ApiHelper from './ApiHelper';

const REDACTION_URL = '/o/headless-data-mask/v1.0/redaction';

function getRedaction(request: RedactionRequest) {
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

export default {
	getRedaction,
};

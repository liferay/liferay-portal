/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper, {RequestResult} from './ApiHelper';

const VALIDATE_URL = '/o/headless-data-masking/v1.0/data-masks/validate';

export type ValidationRequest = {
	detectionRegex: string;
	replacementRegex: string;
	replacementValue: string;
	sampleText: string;
};

export type ValidationResult = {
	error?: string;
	output: string;
};

export function postValidateDataMask(
	request: ValidationRequest
): Promise<RequestResult<ValidationResult>> {
	return ApiHelper.post<ValidationResult>(VALIDATE_URL, request);
}

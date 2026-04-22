/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper, {RequestResult} from '../services/ApiHelper';

const getEndpointByScope = (isCompanyGroup: boolean) => {
	return isCompanyGroup
		? '/o/export-import/v1.0/validate'
		: `/o/export-import/v1.0/scopes/${Liferay.ThemeDisplay.getScopeGroupId() ?? 0}/validate`;
};

export async function getValidateLarFile({
	file,
	isCompanyGroup,
	onProgress,
	signal,
}: {
	file: File;
	isCompanyGroup: boolean;
	onProgress: (progressEvent: number) => void;
	signal?: AbortSignal;
}): Promise<
	RequestResult<{
		errorMessages: string[];
		success: boolean;
		tempFilePath: string;
	}>
> {
	return await ApiHelper.postFormDataWithProgress<{
		errorMessages: string[];
		success: boolean;
		tempFilePath: string;
	}>(
		getEndpointByScope(isCompanyGroup),
		Liferay.Util.objectToFormData({file}),
		(progressEvent) => {
			onProgress(progressEvent);
		},
		signal
	);
}

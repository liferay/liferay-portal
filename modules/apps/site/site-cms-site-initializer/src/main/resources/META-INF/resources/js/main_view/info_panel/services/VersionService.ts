/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper, {RequestResult} from '../../../common/services/ApiHelper';

async function getObjectEntryVersions(
	url: string,
	parametersMap: Record<string, any> = {}
): Promise<RequestResult<any>> {
	const getURL: URL = new URL(url, window.location.origin);
	const parameters = Object.entries(parametersMap);

	if (parameters.length) {
		parameters.forEach(([key, value]) => {
			getURL.searchParams.append(key, value);
		});
	}

	return await ApiHelper.get(getURL.toString());
}

export default {getObjectEntryVersions};

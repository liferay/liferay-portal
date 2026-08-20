/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper, {RequestResult} from './ApiHelper';
import {ScheduledPublishProcessParams} from './getScheduledPublishProcess';

export function deleteScheduledPublishProcess({
	url,
}: ScheduledPublishProcessParams): Promise<RequestResult<void>> {
	return ApiHelper.delete(url);
}

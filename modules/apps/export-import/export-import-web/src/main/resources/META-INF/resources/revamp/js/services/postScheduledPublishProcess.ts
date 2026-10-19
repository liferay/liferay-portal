/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ScheduledPublishProcess,
	ScheduledPublishProcessRequest,
} from '../types/exportImportProcess';
import ApiHelper, {RequestResult} from './ApiHelper';

export interface ScheduledPublishProcessRequestParams {
	scheduledPublishProcessRequest: ScheduledPublishProcessRequest;
	url: string;
}

export function postScheduledPublishProcess({
	scheduledPublishProcessRequest,
	url,
}: ScheduledPublishProcessRequestParams): Promise<
	RequestResult<ScheduledPublishProcess>
> {
	return ApiHelper.post<ScheduledPublishProcess>(
		url,
		scheduledPublishProcessRequest
	);
}

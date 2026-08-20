/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ScheduledPublishProcess} from '../types/exportImportProcess';
import ApiHelper, {RequestResult} from './ApiHelper';

export interface ScheduledPublishProcessParams {
	url: string;
}

export function getScheduledPublishProcess({
	url,
}: ScheduledPublishProcessParams): Promise<
	RequestResult<ScheduledPublishProcess>
> {
	return ApiHelper.get<ScheduledPublishProcess>(url);
}

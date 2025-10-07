/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IBulkActionTaskPage, TBulkActionTaskDTO} from '../types/BulkActionTask';
import ApiHelper, {RequestResult} from './ApiHelper';

async function createTask(
	body: TBulkActionTaskDTO,
	url: string
): Promise<RequestResult<IBulkActionTaskPage>> {
	return await ApiHelper.post<IBulkActionTaskPage>(url, body);
}

async function getTasks({
	filter = '',
	pageSize = -1,
	sort = '',
}: {
	filter?: string;
	pageSize?: number;
	sort?: string;
}): Promise<RequestResult<IBulkActionTaskPage>> {
	const url = new URL(
		`${Liferay.ThemeDisplay.getPortalURL()}/o/cms/bulk-action-tasks`
	);

	Object.entries({filter, pageSize, sort}).map(([key, value]) => {
		if (value) {
			url.searchParams.append(key, value as string);
		}
	});

	return await ApiHelper.get<IBulkActionTaskPage>(url.toString());
}

export default {
	createTask,
	getTasks,
};

/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper, {RequestResult} from './ApiHelper';

const REMOTE_IMPORT_PROCESSES_URL = '/o/remote-import-processes';

const RELATIONSHIP_OBJECT_FIELD_NAME =
	'r_remoteInstanceToRemoteImportProcesses_c_remoteInstanceId';

export async function addRemoteImportProcess(
	exportProcessId: number,
	remoteInstanceId: number
): Promise<RequestResult<{id: number}>> {
	return ApiHelper.post<{id: number}>(
		`${REMOTE_IMPORT_PROCESSES_URL}?fields=id`,
		{
			[RELATIONSHIP_OBJECT_FIELD_NAME]: remoteInstanceId,
			exportProcessId,
		}
	);
}

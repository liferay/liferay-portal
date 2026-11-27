/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.constants;

import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;

/**
 * @author Adolfo Pérez
 */
public class RemoteInstanceConstants {

	public static final String CLIENT_SECRET_PREFIX = "{AES_GCM}";

	public static final String
		OBJECT_DEFINITION_EXTERNAL_REFERENCE_CODE_REMOTE_IMPORT_PROCESS =
			"L_REMOTE_IMPORT_PROCESS";

	public static final String OBJECT_FIELD_NAME_REMOTE_INSTANCE_ID =
		"r_remoteInstanceToRemoteImportProcesses_c_remoteInstanceId";

	public static final int[] STATUSES_COMPLETED = {
		BackgroundTaskConstants.STATUS_CANCELLED,
		BackgroundTaskConstants.STATUS_COMPLETED_WITH_ERRORS,
		BackgroundTaskConstants.STATUS_FAILED,
		BackgroundTaskConstants.STATUS_SUCCESSFUL
	};

}
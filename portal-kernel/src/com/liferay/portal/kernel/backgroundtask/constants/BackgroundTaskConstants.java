/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.backgroundtask.constants;

import com.liferay.petra.string.StringPool;

/**
 * @author Daniel Kocsis
 * @author Eduardo García
 */
public class BackgroundTaskConstants {

	public static final int GROUP_ID_DEFAULT = 0;

	public static final int ISOLATION_LEVEL_CLASS = 1;

	public static final int ISOLATION_LEVEL_COMPANY = 2;

	public static final int ISOLATION_LEVEL_CUSTOM = 6;

	public static final int ISOLATION_LEVEL_GROUP = 3;

	public static final int ISOLATION_LEVEL_NOT_ISOLATED = 4;

	public static final int ISOLATION_LEVEL_TASK_NAME = 5;

	public static final String LABEL_CANCELLED = "cancelled";

	public static final String LABEL_FAILED = "failed";

	public static final String LABEL_IN_PROGRESS = "in-progress";

	public static final String LABEL_NEW = "new";

	public static final String LABEL_QUEUED = "queued";

	public static final String LABEL_SUCCESSFUL = "successful";

	public static final String MESSAGE_KEY_BACKGROUND_TASK_ID =
		"backgroundTaskId";

	public static final int STATUS_CANCELLED = 5;

	public static final int STATUS_FAILED = 2;

	public static final int STATUS_IN_PROGRESS = 1;

	public static final int STATUS_NEW = 0;

	public static final int STATUS_QUEUED = 4;

	public static final int STATUS_SUCCESSFUL = 3;

	public static String getStatusCssClass(int status) {
		if (status == STATUS_CANCELLED) {
			return "text-info";
		}
		else if (status == STATUS_FAILED) {
			return "text-danger";
		}
		else if (status == STATUS_IN_PROGRESS) {
			return "text-warning";
		}
		else if ((status == BackgroundTaskConstants.STATUS_NEW) ||
				 (status == BackgroundTaskConstants.STATUS_QUEUED)) {

			return "text-info";
		}
		else if (status == STATUS_SUCCESSFUL) {
			return "text-success";
		}

		return StringPool.BLANK;
	}

	public static String getStatusLabel(int status) {
		if (status == STATUS_CANCELLED) {
			return LABEL_CANCELLED;
		}
		else if (status == STATUS_FAILED) {
			return LABEL_FAILED;
		}
		else if (status == STATUS_IN_PROGRESS) {
			return LABEL_IN_PROGRESS;
		}
		else if (status == STATUS_NEW) {
			return LABEL_NEW;
		}
		else if (status == STATUS_QUEUED) {
			return LABEL_QUEUED;
		}
		else if (status == STATUS_SUCCESSFUL) {
			return LABEL_SUCCESSFUL;
		}

		return StringPool.BLANK;
	}

}
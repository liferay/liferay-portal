/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.action.util;

import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;

/**
 * @author Brian Wing Shun Chan
 */
public class ObjectActionStatusUtil {

	public static void updateStatusAfterCommit(
		ObjectActionLocalService objectActionLocalService, long objectActionId,
		int status) {

		// The status is only bookkeeping and must never fail the caller. The
		// callback runs the write after the commit, and the catch handles
		// the inline call when there is no transaction.

		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				try {
					objectActionLocalService.updateStatus(
						objectActionId, status);
				}
				catch (Exception exception) {
					_log.error(
						"Unable to update the status of object action " +
							objectActionId,
						exception);
				}

				return null;
			});
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectActionStatusUtil.class);

}
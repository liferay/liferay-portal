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

		// The status write waits for the surrounding transaction to commit,
		// so it can neither fail that transaction nor record an outcome for
		// an execution whose effects roll back with it. Without a surrounding
		// transaction, the callback runs immediately, and
		// TransactionCallbackUtil rethrows what it throws instead of logging
		// it the way it logs a callback that ran after a commit. The status
		// is only bookkeeping, so this catch logs a failed write rather than
		// letting it reach the caller on either path.

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
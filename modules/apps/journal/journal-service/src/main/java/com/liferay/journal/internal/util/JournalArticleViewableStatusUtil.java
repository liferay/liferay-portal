/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.util;

import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

/**
 * @author Jürgen Kappler
 */
public class JournalArticleViewableStatusUtil {

	public static long getViewerUserId(
		long groupId, PermissionChecker permissionChecker) {

		if ((permissionChecker == null) ||
			permissionChecker.hasPermission(
				groupId, JournalArticle.class.getName(),
				JournalArticle.class.getName(), ActionKeys.UPDATE)) {

			return 0;
		}

		return permissionChecker.getUserId();
	}

	public static boolean isViewable(
		JournalArticle journalArticle, long viewerUserId) {

		if ((viewerUserId == 0) ||
			(!journalArticle.isDraft() && !journalArticle.isScheduled())) {

			return true;
		}

		if (journalArticle.getUserId() == viewerUserId) {
			return true;
		}

		return false;
	}

	public static String replaceViewableStatus(
		long groupId, boolean inlineSQLHelper, String sql) {

		long viewerUserId = 0;

		if (inlineSQLHelper) {
			viewerUserId = getViewerUserId(
				groupId, PermissionThreadLocal.getPermissionChecker());
		}

		if (viewerUserId == 0) {
			return StringUtil.replace(
				sql,
				new String[] {
					"[$VIEWABLE_STATUS$] AND", "[$VIEWABLE_STATUS_JOIN$] AND"
				},
				new String[] {StringPool.BLANK, StringPool.BLANK});
		}

		return StringUtil.replace(
			sql,
			new String[] {"[$VIEWABLE_STATUS$]", "[$VIEWABLE_STATUS_JOIN$]"},
			new String[] {
				_getViewableStatusSQL("JournalArticle", viewerUserId),
				_getViewableStatusSQL("tempJournalArticle", viewerUserId)
			});
	}

	private static String _getViewableStatusSQL(
		String tableName, long viewerUserId) {

		return StringBundler.concat(
			"((", tableName, ".status NOT IN (", WorkflowConstants.STATUS_DRAFT,
			", ", WorkflowConstants.STATUS_SCHEDULED, ")) OR (", tableName,
			".userId = ", viewerUserId, "))");
	}

}
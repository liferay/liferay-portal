/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.util;

import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author Jürgen Kappler
 */
public class JournalArticleViewableStatusUtil {

	public static List<JournalArticle> getViewableArticles(
		BiFunction<Integer, Integer, List<JournalArticle>> biFunction,
		int start, int end, long viewerUserId) {

		if (viewerUserId == 0) {
			return biFunction.apply(start, end);
		}

		return getViewableArticles(
			biFunction,
			journalArticle -> _isViewable(journalArticle, viewerUserId), start,
			end);
	}

	public static List<JournalArticle> getViewableArticles(
		BiFunction<Integer, Integer, List<JournalArticle>> biFunction,
		Predicate<JournalArticle> predicate, int start, int end) {

		return ListUtil.subList(
			ListUtil.filter(
				biFunction.apply(QueryUtil.ALL_POS, QueryUtil.ALL_POS),
				predicate),
			start, end);
	}

	public static List<JournalArticle> getViewableArticles(
		List<JournalArticle> journalArticles, long viewerUserId) {

		if (viewerUserId == 0) {
			return journalArticles;
		}

		return ListUtil.filter(
			journalArticles,
			journalArticle -> _isViewable(journalArticle, viewerUserId));
	}

	public static int getViewableArticlesCount(
		Supplier<Integer> countSupplier,
		Supplier<List<JournalArticle>> listSupplier, long viewerUserId) {

		if (viewerUserId == 0) {
			return countSupplier.get();
		}

		List<JournalArticle> journalArticles = getViewableArticles(
			listSupplier.get(), viewerUserId);

		return journalArticles.size();
	}

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

	private static boolean _isViewable(
		JournalArticle journalArticle, long viewerUserId) {

		if ((!journalArticle.isDraft() && !journalArticle.isScheduled()) ||
			(journalArticle.getUserId() == viewerUserId)) {

			return true;
		}

		return false;
	}

}
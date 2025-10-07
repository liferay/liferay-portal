/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.comment.CommentManagerUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.ratings.kernel.model.RatingsStats;
import com.liferay.ratings.kernel.service.RatingsStatsLocalServiceUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;

/**
 * @author Víctor Galán
 */
public class CommentUtil {

	public static JSONObject getCommentJSONObject(
			Comment comment, HttpServletRequest httpServletRequest)
		throws PortalException {

		Date createDate = comment.getCreateDate();

		Date modifiedDate = comment.getModifiedDate();

		String modifiedDateDescription = LanguageUtil.format(
			httpServletRequest, "x-ago",
			LanguageUtil.getTimeDescription(
				httpServletRequest,
				System.currentTimeMillis() - modifiedDate.getTime(), true));

		RatingsStats ratingsStats = RatingsStatsLocalServiceUtil.fetchStats(
			comment.getClassName(), comment.getCommentId());

		return JSONUtil.put(
			"author", _getAuthorJSONObject(comment, httpServletRequest)
		).put(
			"body", comment.getBody()
		).put(
			"className", comment.getClassName()
		).put(
			"commentId", comment.getCommentId()
		).put(
			"dateDescription", modifiedDateDescription
		).put(
			"edited", !createDate.equals(modifiedDate)
		).put(
			"negativeVotes",
			() -> {
				if (ratingsStats != null) {
					return ratingsStats.getTotalEntries() -
						(int)Math.round(ratingsStats.getTotalScore());
				}

				return 0;
			}
		).put(
			"positiveVotes",
			() -> {
				if (ratingsStats != null) {
					return (int)Math.round(ratingsStats.getTotalScore());
				}

				return 0;
			}
		).put(
			"rootComment",
			() -> {
				Comment parentComment = CommentManagerUtil.fetchComment(
					comment.getParentCommentId());

				return parentComment.isRoot();
			}
		);
	}

	private static JSONObject _getAuthorJSONObject(
			Comment comment, HttpServletRequest httpServletRequest)
		throws PortalException {

		User commentUser = comment.getUser();

		if (commentUser == null) {
			return JSONUtil.put(
				"fullName", LanguageUtil.get(httpServletRequest, "deleted-user")
			).put(
				"portraitURL", StringPool.BLANK
			).put(
				"userId", 0L
			);
		}

		String portraitURL = StringPool.BLANK;

		if (commentUser.getPortraitId() > 0) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			portraitURL = commentUser.getPortraitURL(themeDisplay);
		}

		return JSONUtil.put(
			"fullName", commentUser.getFullName()
		).put(
			"portraitURL", portraitURL
		).put(
			"userId", commentUser.getUserId()
		);
	}

}
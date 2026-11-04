/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.comment.CommentManagerUtil;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.editor.configuration.EditorConfiguration;
import com.liferay.portal.kernel.editor.configuration.EditorConfigurationFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFunction;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.ratings.kernel.model.RatingsStats;
import com.liferay.ratings.kernel.service.RatingsStatsLocalServiceUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Víctor Galán
 */
public class CommentUtil {

	public static JSONObject getCommentJSONObject(
			Comment comment, DiscussionPermission discussionPermission,
			HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

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
			"hasDeletePermission",
			discussionPermission.hasDeletePermission(
				themeDisplay.getPermissionChecker(), comment.getCommentId())
		).put(
			"hasUpdatePermission",
			discussionPermission.hasUpdatePermission(
				themeDisplay.getPermissionChecker(), comment.getCommentId())
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

	public static Map<String, Object> getCommentsProps(
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return HashMapBuilder.<String, Object>put(
			"addCommentURL",
			StringBundler.concat(
				themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
				GroupConstants.CMS_FRIENDLY_URL, "/add_content_item_comment")
		).put(
			"deleteCommentURL",
			StringBundler.concat(
				themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
				GroupConstants.CMS_FRIENDLY_URL, "/delete_content_item_comment")
		).put(
			"editCommentURL",
			StringBundler.concat(
				themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
				GroupConstants.CMS_FRIENDLY_URL, "/edit_content_item_comment")
		).put(
			"editorConfig",
			() -> {
				EditorConfiguration contentItemCommentEditorConfiguration =
					EditorConfigurationFactoryUtil.getEditorConfiguration(
						StringPool.BLANK, "contentItemCommentEditor",
						StringPool.BLANK, Collections.emptyMap(), themeDisplay,
						RequestBackedPortletURLFactoryUtil.create(
							httpServletRequest));

				Map<String, Object> data =
					contentItemCommentEditorConfiguration.getData();

				return data.get("editorConfig");
			}
		).put(
			"getCommentsURL",
			StringBundler.concat(
				themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
				GroupConstants.CMS_FRIENDLY_URL, "/get_asset_comments")
		).build();
	}

	public static String getSanitizedBody(
			String body, String className, long classPK,
			DiscussionPermission discussionPermission,
			ThemeDisplay themeDisplay)
		throws PortalException {

		if (!FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-104086")) {

			return body;
		}

		StringBundler sb = new StringBundler();

		Matcher matcher = _mentionUserPattern.matcher(body);
		int end = 0;

		while (matcher.find()) {
			sb.append(body.substring(end, matcher.start()));

			User user = UserLocalServiceUtil.fetchUserByExternalReferenceCode(
				matcher.group(1), themeDisplay.getCompanyId());

			if ((user != null) &&
				discussionPermission.hasViewPermission(
					PermissionCheckerFactoryUtil.create(user),
					themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId(),
					className, classPK)) {

				sb.append(matcher.group());
			}
			else {
				sb.append(matcher.group(2));
			}

			end = matcher.end();
		}

		sb.append(body.substring(end));

		return sb.toString();
	}

	public static Function<String, ServiceContext> getServiceContextFunction(
		long classPK, HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Function<String, ServiceContext> serviceContextFunction =
			new ServiceContextFunction(httpServletRequest);

		if (!FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-104086")) {

			return serviceContextFunction;
		}

		String contentURL = StringBundler.concat(
			themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
			GroupConstants.CMS_FRIENDLY_URL,
			"/edit_content_item?objectEntryId=", classPK, "&p_l_mode=",
			Constants.READ);

		return serviceContextFunction.andThen(
			serviceContext -> {
				serviceContext.setAttribute("contentURL", contentURL);
				serviceContext.setAttribute("namespace", StringPool.BLANK);

				return serviceContext;
			});
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

	private static final Pattern _mentionUserPattern = Pattern.compile(
		"<span class=\"mention mention-user-([^\"\\s]+)\">([^<]*)</span>");

}
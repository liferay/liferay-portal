/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.comment;

import com.liferay.layout.content.page.editor.web.internal.workflow.WorkflowUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.comment.WorkflowableComment;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.function.Function;

/**
 * @author Alejandro Tardín
 */
public class CommentUtil {

	public static JSONObject getCommentJSONObject(
			Comment comment, HttpServletRequest httpServletRequest)
		throws PortalException {

		Date createDate = comment.getCreateDate();

		String dateDescription = LanguageUtil.format(
			httpServletRequest, "x-ago",
			LanguageUtil.getTimeDescription(
				httpServletRequest,
				System.currentTimeMillis() - createDate.getTime(), true));

		Date modifiedDate = comment.getModifiedDate();

		String modifiedDateDescription = LanguageUtil.format(
			httpServletRequest, "x-ago",
			LanguageUtil.getTimeDescription(
				httpServletRequest,
				System.currentTimeMillis() - modifiedDate.getTime(), true));

		return JSONUtil.put(
			"author", _getAuthorJSONObject(comment, httpServletRequest)
		).put(
			"body", comment.getBody()
		).put(
			"commentId", comment.getCommentId()
		).put(
			"dateDescription", dateDescription
		).put(
			"edited", !createDate.equals(modifiedDate)
		).put(
			"modifiedDateDescription", modifiedDateDescription
		).put(
			"resolved", _isResolved(comment)
		);
	}

	public static Function<String, ServiceContext> getServiceContextFunction(
			ActionRequest actionRequest, ThemeDisplay themeDisplay)
		throws PortalException {

		Function<String, ServiceContext> serviceContextFunction =
			WorkflowUtil.getServiceContextFunction(
				_getWorkflowAction(actionRequest), actionRequest);

		String notificationRedirect = HttpComponentsUtil.setParameter(
			PortalUtil.getLayoutFullURL(themeDisplay), "p_l_mode",
			Constants.EDIT);

		return serviceContextFunction.andThen(
			serviceContext -> {
				serviceContext.setAttribute("contentURL", notificationRedirect);
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

	private static int _getWorkflowAction(ActionRequest actionRequest) {
		boolean resolved = ParamUtil.getBoolean(actionRequest, "resolved");

		if (resolved) {
			return WorkflowConstants.ACTION_SAVE_DRAFT;
		}

		return WorkflowConstants.ACTION_PUBLISH;
	}

	private static boolean _isResolved(Comment comment) {
		if (comment instanceof WorkflowableComment) {
			WorkflowableComment workflowableComment =
				(WorkflowableComment)comment;

			if (workflowableComment.getStatus() ==
					WorkflowConstants.STATUS_DRAFT) {

				return true;
			}
		}

		return false;
	}

}
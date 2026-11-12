/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.struts;

import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cms.site.initializer.internal.util.CommentUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = "path=/cms/edit_content_item_comment",
	service = StrutsAction.class
)
public class EditContentItemCommentStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			long commentId = ParamUtil.getLong(httpServletRequest, "commentId");

			Comment comment = _commentManager.fetchComment(commentId);

			if (comment == null) {
				throw new NoSuchModelException(
					"No comment exists with the primary key " + commentId);
			}

			if (themeDisplay.getUserId() != comment.getUserId()) {
				_discussionPermission.checkUpdatePermission(
					themeDisplay.getPermissionChecker(), commentId);
			}

			String body = CommentUtil.getSanitizedBody(
				ParamUtil.getString(httpServletRequest, "body"),
				comment.getClassName(), comment.getClassPK(),
				_discussionPermission, themeDisplay);

			_commentManager.updateComment(
				themeDisplay.getUserId(), comment.getClassName(),
				comment.getClassPK(), commentId, null, body,
				CommentUtil.getServiceContextFunction(
					comment.getClassName(), comment.getClassPK(),
					comment.getBody(), httpServletRequest));

			comment = _commentManager.fetchComment(commentId);

			ServletResponseUtil.write(
				httpServletResponse,
				JSONUtil.toString(
					CommentUtil.getCommentJSONObject(
						comment, _discussionPermission, httpServletRequest)));
		}
		catch (Exception exception) {
			_log.error(exception);

			ServletResponseUtil.write(
				httpServletResponse,
				JSONUtil.toString(
					JSONUtil.put(
						"error",
						_language.get(
							themeDisplay.getLocale(),
							"an-unexpected-error-occurred"))));
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditContentItemCommentStrutsAction.class);

	@Reference
	private CommentManager _commentManager;

	@Reference
	private DiscussionPermission _discussionPermission;

	@Reference
	private Language _language;

}
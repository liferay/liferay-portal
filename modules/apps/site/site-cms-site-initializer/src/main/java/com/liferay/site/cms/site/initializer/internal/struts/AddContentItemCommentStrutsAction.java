/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.struts;

import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFunction;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cms.site.initializer.internal.util.CommentUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.function.Function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = "path=/cms/add_content_item_comment",
	service = StrutsAction.class
)
public class AddContentItemCommentStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		String className1 = ParamUtil.getString(
			httpServletRequest, "className", null);

		if (className1 == null) {
			long classNameId = ParamUtil.getLong(
				httpServletRequest, "classNameId");

			ClassName className2 = _classNameLocalService.getClassName(
				classNameId);

			className1 = className2.getClassName();
		}

		long classPK = ParamUtil.getLong(httpServletRequest, "classPK");

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_discussionPermission.checkAddPermission(
			themeDisplay.getPermissionChecker(), themeDisplay.getCompanyId(),
			themeDisplay.getScopeGroupId(), className1, classPK);

		User user = themeDisplay.getUser();

		Function<String, ServiceContext> serviceContextFunction =
			new ServiceContextFunction(httpServletRequest);

		String body = ParamUtil.getString(httpServletRequest, "body");
		long parentCommentId = ParamUtil.getLong(
			httpServletRequest, "parentCommentId");

		long commentId = 0;

		if (parentCommentId == 0) {
			commentId = _commentManager.addComment(
				null, user.getUserId(), themeDisplay.getScopeGroupId(),
				className1, classPK, user.getFullName(), null, body,
				serviceContextFunction);
		}
		else {
			commentId = _commentManager.addComment(
				null, user.getUserId(), className1, classPK, user.getFullName(),
				parentCommentId, null, body, serviceContextFunction);
		}

		Comment comment = _commentManager.fetchComment(commentId);

		ServletResponseUtil.write(
			httpServletResponse,
			JSONUtil.toString(
				CommentUtil.getCommentJSONObject(comment, httpServletRequest)));

		return null;
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CommentManager _commentManager;

	@Reference
	private DiscussionPermission _discussionPermission;

}
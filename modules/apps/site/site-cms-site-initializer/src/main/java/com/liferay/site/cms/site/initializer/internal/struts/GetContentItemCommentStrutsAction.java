/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.struts;

import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.site.cms.site.initializer.internal.util.CommentUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Fabio Monaco
 */
@Component(
	property = "path=/cms/get_asset_comments", service = StrutsAction.class
)
public class GetContentItemCommentStrutsAction implements StrutsAction {

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

		if (!_commentManager.hasDiscussion(className1, classPK)) {
			return null;
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_discussionPermission.checkAddPermission(
			themeDisplay.getPermissionChecker(), themeDisplay.getCompanyId(),
			themeDisplay.getScopeGroupId(), className1, classPK);

		List<Comment> rootComments = _commentManager.getRootComments(
			className1, classPK, WorkflowConstants.STATUS_ANY,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (Comment rootComment : rootComments) {
			JSONObject commentJSONObject = CommentUtil.getCommentJSONObject(
				rootComment, httpServletRequest);

			List<Comment> childComments = _commentManager.getChildComments(
				rootComment.getCommentId(), WorkflowConstants.STATUS_APPROVED,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			JSONArray childCommentsJSONArray = _jsonFactory.createJSONArray();

			for (Comment childComment : childComments) {
				childCommentsJSONArray.put(
					CommentUtil.getCommentJSONObject(
						childComment, httpServletRequest));
			}

			commentJSONObject.put("children", childCommentsJSONArray);

			jsonArray.put(commentJSONObject);
		}

		ServletResponseUtil.write(
			httpServletResponse, JSONUtil.toString(jsonArray));

		return null;
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CommentManager _commentManager;

	@Reference
	private DiscussionPermission _discussionPermission;

	@Reference
	private JSONFactory _jsonFactory;

}
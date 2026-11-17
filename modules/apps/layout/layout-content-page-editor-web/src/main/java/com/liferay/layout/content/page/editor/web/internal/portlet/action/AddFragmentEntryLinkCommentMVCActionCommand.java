/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.fragment.exception.NoSuchEntryLinkException;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.comment.CommentUtil;
import com.liferay.layout.content.page.editor.web.internal.workflow.WorkflowUtil;
import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Objects;
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/add_fragment_entry_link_comment"
	},
	service = MVCActionCommand.class
)
public class AddFragmentEntryLinkCommentMVCActionCommand
	extends BaseContentPageEditorTransactionalMVCActionCommand {

	@Override
	protected JSONObject doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		LayoutPermissionUtil.check(
			themeDisplay.getPermissionChecker(), layout, ActionKeys.UPDATE);

		long fragmentEntryLinkId = ParamUtil.getLong(
			actionRequest, "fragmentEntryLinkId");

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentEntryLinkId);

		if ((fragmentEntryLink == null) ||
			(fragmentEntryLink.getPlid() != themeDisplay.getPlid())) {

			throw new NoSuchEntryLinkException(
				"No fragment entry link exists with the primary key " +
					fragmentEntryLinkId);
		}

		long parentCommentId = ParamUtil.getLong(
			actionRequest, "parentCommentId");

		if (parentCommentId != 0) {
			Comment parentComment = _commentManager.fetchComment(
				parentCommentId);

			if ((parentComment == null) ||
				!Objects.equals(
					parentComment.getClassName(),
					FragmentEntryLink.class.getName()) ||
				(parentComment.getClassPK() != fragmentEntryLinkId)) {

				throw new NoSuchModelException(
					"No comment exists with the primary key " +
						parentCommentId);
			}
		}

		User user = themeDisplay.getUser();

		return CommentUtil.getCommentJSONObject(
			_commentManager.fetchComment(
				WorkflowUtil.withoutWorkflow(
					() -> {
						_commentManager.subscribeDiscussion(
							user.getUserId(), themeDisplay.getScopeGroupId(),
							FragmentEntryLink.class.getName(),
							fragmentEntryLinkId);

						Function<String, ServiceContext>
							serviceContextFunction =
								CommentUtil.getServiceContextFunction(
									actionRequest, themeDisplay);

						if (parentCommentId == 0) {
							_commentManager.subscribeDiscussion(
								layout.getUserId(),
								themeDisplay.getScopeGroupId(),
								FragmentEntryLink.class.getName(),
								fragmentEntryLinkId);

							return _commentManager.addComment(
								null, themeDisplay.getUserId(),
								themeDisplay.getScopeGroupId(),
								FragmentEntryLink.class.getName(),
								fragmentEntryLinkId, user.getFullName(), null,
								ParamUtil.getString(actionRequest, "body"),
								serviceContextFunction);
						}

						return _commentManager.addComment(
							null, themeDisplay.getUserId(),
							FragmentEntryLink.class.getName(),
							fragmentEntryLinkId, user.getFullName(),
							parentCommentId, null,
							ParamUtil.getString(actionRequest, "body"),
							serviceContextFunction);
					})),
			_portal.getHttpServletRequest(actionRequest));
	}

	@Reference
	private CommentManager _commentManager;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private Portal _portal;

}
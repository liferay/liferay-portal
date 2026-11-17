/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.comment.CommentUtil;
import com.liferay.layout.content.page.editor.web.internal.workflow.WorkflowUtil;
import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/edit_fragment_entry_link_comment"
	},
	service = MVCActionCommand.class
)
public class EditFragmentEntryLinkCommentMVCActionCommand
	extends BaseContentPageEditorTransactionalMVCActionCommand {

	@Override
	protected JSONObject doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		LayoutPermissionUtil.check(
			themeDisplay.getPermissionChecker(), themeDisplay.getLayout(),
			ActionKeys.UPDATE);

		long commentId = ParamUtil.getLong(actionRequest, "commentId");

		Comment comment = _commentManager.fetchComment(commentId);

		FragmentEntryLink fragmentEntryLink = null;

		if ((comment != null) &&
			Objects.equals(
				comment.getClassName(), FragmentEntryLink.class.getName())) {

			fragmentEntryLink =
				_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
					comment.getClassPK());
		}

		if ((fragmentEntryLink == null) ||
			(fragmentEntryLink.getPlid() != themeDisplay.getPlid())) {

			throw new NoSuchModelException(
				"No comment exists with the primary key " + commentId);
		}

		String body = ParamUtil.getString(
			actionRequest, "body", comment.getBody());

		if (!body.equals(comment.getBody()) &&
			(comment.getUserId() != themeDisplay.getUserId())) {

			throw new PrincipalException();
		}

		WorkflowUtil.withoutWorkflow(
			() -> {
				_commentManager.updateComment(
					themeDisplay.getUserId(), FragmentEntryLink.class.getName(),
					comment.getClassPK(), commentId, null, body,
					CommentUtil.getServiceContextFunction(
						actionRequest, themeDisplay));

				for (Comment childComment :
						_commentManager.getChildComments(
							commentId, WorkflowConstants.STATUS_ANY,
							QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

					_commentManager.updateComment(
						themeDisplay.getUserId(),
						FragmentEntryLink.class.getName(),
						childComment.getClassPK(), childComment.getCommentId(),
						null, childComment.getBody(),
						CommentUtil.getServiceContextFunction(
							actionRequest, themeDisplay));
				}
			});

		return CommentUtil.getCommentJSONObject(
			_commentManager.fetchComment(commentId),
			_portal.getHttpServletRequest(actionRequest));
	}

	@Reference
	private CommentManager _commentManager;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private Portal _portal;

}
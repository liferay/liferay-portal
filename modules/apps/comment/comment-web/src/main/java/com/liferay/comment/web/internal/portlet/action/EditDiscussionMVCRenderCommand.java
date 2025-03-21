/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.comment.web.internal.portlet.action;

import com.liferay.comment.web.internal.constants.CommentPortletKeys;
import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lino Alves
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommentPortletKeys.COMMENT,
		"mvc.command.name=/comment/edit_discussion"
	},
	service = MVCRenderCommand.class
)
public class EditDiscussionMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		long commentId = ParamUtil.getLong(renderRequest, "commentId");

		Comment comment = _commentManager.fetchComment(commentId);

		renderRequest.setAttribute(WebKeys.COMMENT, comment);

		return "/edit_discussion.jsp";
	}

	@Reference
	private CommentManager _commentManager;

}
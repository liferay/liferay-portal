/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.display.context;

import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.comment.CommentManagerUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.RenderRequest;

/**
 * @author Danny Situ
 */
public class CommerceReturnItemCommentEditDisplayContext {

	public CommerceReturnItemCommentEditDisplayContext(
			RenderRequest renderRequest)
		throws PortalException {

		long commentId = ParamUtil.getLong(renderRequest, "commentId");

		_comment = CommentManagerUtil.fetchComment(commentId);
	}

	public Comment getComment() {
		return _comment;
	}

	private final Comment _comment;

}
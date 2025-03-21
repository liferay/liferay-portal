/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.comment.taglib.internal.context;

import com.liferay.portal.kernel.comment.Discussion;
import com.liferay.portal.kernel.comment.DiscussionComment;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.comment.display.context.CommentSectionDisplayContext;
import com.liferay.portal.kernel.comment.display.context.CommentTreeDisplayContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Adolfo Pérez
 */
public class CommentDisplayContextProviderUtil {

	public static CommentSectionDisplayContext getCommentSectionDisplayContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		DiscussionPermission discussionPermission, Discussion discussion) {

		return _commentDisplayContextProvider.getCommentSectionDisplayContext(
			httpServletRequest, httpServletResponse, discussionPermission,
			discussion);
	}

	public static CommentTreeDisplayContext getCommentTreeDisplayContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		DiscussionPermission discussionPermission,
		DiscussionComment discussionComment) {

		return _commentDisplayContextProvider.getCommentTreeDisplayContext(
			httpServletRequest, httpServletResponse, discussionPermission,
			discussionComment);
	}

	private static final CommentDisplayContextProvider
		_commentDisplayContextProvider =
			new CommentDisplayContextProviderImpl();

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.comment.taglib.internal.context;

import com.liferay.comment.taglib.internal.context.helper.DiscussionRequestHelper;
import com.liferay.comment.taglib.internal.context.helper.DiscussionTaglibHelper;
import com.liferay.portal.kernel.comment.Discussion;
import com.liferay.portal.kernel.comment.DiscussionComment;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.comment.display.context.CommentDisplayContextFactory;
import com.liferay.portal.kernel.comment.display.context.CommentSectionDisplayContext;
import com.liferay.portal.kernel.comment.display.context.CommentTreeDisplayContext;
import com.liferay.portal.kernel.display.context.BaseDisplayContextProvider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Adolfo Pérez
 */
public class CommentDisplayContextProviderImpl
	extends BaseDisplayContextProvider<CommentDisplayContextFactory>
	implements CommentDisplayContextProvider {

	public CommentDisplayContextProviderImpl() {
		super(CommentDisplayContextFactory.class);
	}

	@Override
	public CommentSectionDisplayContext getCommentSectionDisplayContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		DiscussionPermission discussionPermission, Discussion discussion) {

		DiscussionRequestHelper discussionRequestHelper =
			new DiscussionRequestHelper(httpServletRequest);
		DiscussionTaglibHelper discussionTaglibHelper =
			new DiscussionTaglibHelper(httpServletRequest);

		CommentSectionDisplayContext commentSectionDisplayContext =
			new DefaultCommentSectionDisplayContext(
				discussionRequestHelper, discussionTaglibHelper,
				discussionPermission, discussion);

		for (CommentDisplayContextFactory commentDisplayContextFactory :
				getDisplayContextFactories()) {

			commentSectionDisplayContext =
				commentDisplayContextFactory.getCommentSectionDisplayContext(
					commentSectionDisplayContext, httpServletRequest,
					httpServletResponse, discussionPermission, discussion);
		}

		return commentSectionDisplayContext;
	}

	@Override
	public CommentTreeDisplayContext getCommentTreeDisplayContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		DiscussionPermission discussionPermission,
		DiscussionComment discussionComment) {

		DiscussionRequestHelper discussionRequestHelper =
			new DiscussionRequestHelper(httpServletRequest);
		DiscussionTaglibHelper discussionTaglibHelper =
			new DiscussionTaglibHelper(httpServletRequest);

		CommentTreeDisplayContext commentTreeDisplayContext =
			new DefaultCommentTreeDisplayContext(
				discussionRequestHelper, discussionTaglibHelper,
				discussionPermission, discussionComment);

		for (CommentDisplayContextFactory commentDisplayContextFactory :
				getDisplayContextFactories()) {

			commentTreeDisplayContext =
				commentDisplayContextFactory.getCommentTreeDisplayContext(
					commentTreeDisplayContext, httpServletRequest,
					httpServletResponse, discussionPermission,
					discussionComment);
		}

		return commentTreeDisplayContext;
	}

}
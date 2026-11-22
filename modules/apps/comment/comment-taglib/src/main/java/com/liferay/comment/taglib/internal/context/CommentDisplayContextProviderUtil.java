/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.comment.taglib.internal.context;

import com.liferay.comment.taglib.internal.context.helper.DiscussionRequestHelper;
import com.liferay.comment.taglib.internal.context.helper.DiscussionTaglibHelper;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.comment.Discussion;
import com.liferay.portal.kernel.comment.DiscussionComment;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.comment.display.context.CommentDisplayContextFactory;
import com.liferay.portal.kernel.comment.display.context.CommentSectionDisplayContext;
import com.liferay.portal.kernel.comment.display.context.CommentTreeDisplayContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Comparator;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Adolfo Pérez
 */
public class CommentDisplayContextProviderUtil {

	public static CommentSectionDisplayContext getCommentSectionDisplayContext(
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
				_commentDisplayContextFactories) {

			commentSectionDisplayContext =
				commentDisplayContextFactory.getCommentSectionDisplayContext(
					commentSectionDisplayContext, httpServletRequest,
					httpServletResponse, discussionPermission, discussion);
		}

		return commentSectionDisplayContext;
	}

	public static CommentTreeDisplayContext getCommentTreeDisplayContext(
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
				_commentDisplayContextFactories) {

			commentTreeDisplayContext =
				commentDisplayContextFactory.getCommentTreeDisplayContext(
					commentTreeDisplayContext, httpServletRequest,
					httpServletResponse, discussionPermission,
					discussionComment);
		}

		return commentTreeDisplayContext;
	}

	private static final ServiceTrackerList<CommentDisplayContextFactory>
		_commentDisplayContextFactories;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			CommentDisplayContextProviderUtil.class);

		_commentDisplayContextFactories = ServiceTrackerListFactory.open(
			bundle.getBundleContext(), CommentDisplayContextFactory.class,
			Comparator.naturalOrder());
	}

}
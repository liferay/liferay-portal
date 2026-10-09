/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.comment.demo.data.creator.internal;

import com.liferay.comment.demo.data.creator.CommentDemoDataCreator;
import com.liferay.comment.demo.data.creator.MultipleCommentDemoDataCreator;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.RandomUtil;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Hernández
 */
@Component(service = MultipleCommentDemoDataCreator.class)
public class MultipleCommentDemoDataCreatorImpl
	implements MultipleCommentDemoDataCreator {

	@Override
	public void create(ClassedModel classedModel) throws PortalException {
		_addComments(
			TransformUtil.transform(
				_userLocalService.getUsers(
					0, Math.min(_userLocalService.getUsersCount(), _MAX_USERS)),
				user -> {
					if (_isRegularUser(user)) {
						return user.getUserId();
					}

					return null;
				}),
			classedModel, _COMMENT_ID, RandomUtil.nextInt(_MAX_COMMENTS), 1);
	}

	@Override
	public void delete() throws PortalException {
		_commentDemoDataCreator.delete();
	}

	private int _addComments(
			List<Long> userIds, ClassedModel classedModel, long commentId,
			int maxComments, int level)
		throws PortalException {

		int commentsCount = 0;
		int maxReplies = RandomUtil.nextInt(_MAX_REPLIES / level);
		int repliesCount = 0;

		while ((commentsCount < maxComments) && (repliesCount < maxReplies)) {
			Comment comment = null;

			long userId = _getRandomElement(userIds);

			if (commentId == _COMMENT_ID) {
				comment = _commentDemoDataCreator.create(userId, classedModel);
			}
			else {
				comment = _commentDemoDataCreator.create(userId, commentId);

				repliesCount++;
			}

			commentsCount++;

			if (level >= _MAX_LEVEL) {
				continue;
			}

			commentsCount += _addComments(
				userIds, classedModel, comment.getCommentId(),
				maxComments - commentsCount, level + 1);
		}

		return commentsCount;
	}

	private <T> T _getRandomElement(List<T> list) {
		return list.get(RandomUtil.nextInt(list.size()));
	}

	private boolean _isRegularUser(User user) {
		return !_excludedUsers.contains(user.getEmailAddress());
	}

	private static final int _COMMENT_ID = 0;

	private static final int _MAX_COMMENTS = 100;

	private static final int _MAX_LEVEL = 3;

	private static final int _MAX_REPLIES = 10;

	private static final int _MAX_USERS = 100;

	private static final List<String> _excludedUsers = Arrays.asList(
		"test@liferay.com", "default@liferay.com");

	@Reference
	private CommentDemoDataCreator _commentDemoDataCreator;

	@Reference
	private UserLocalService _userLocalService;

}
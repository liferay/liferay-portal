/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mentions.internal.util;

import com.liferay.petra.function.UnsafePredicate;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.UserGroupLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.SubscriptionSender;
import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Mikel Lorza
 */
public class MentionsSubscriptionSender extends SubscriptionSender {

	@Override
	public void flushNotifications() throws Exception {
		super.flushNotifications();

		Set<Long> userIds = new HashSet<>(_excludedUserIds);

		for (long excludedUserGroupId : _excludedUserGroupIds) {
			for (long userId :
					UserGroupLocalServiceUtil.getUserPrimaryKeys(
						excludedUserGroupId)) {

				userIds.add(userId);
			}
		}

		for (long userGroupId : _userGroupIds) {
			UserGroup userGroup = UserGroupLocalServiceUtil.fetchUserGroup(
				userGroupId);

			if (userGroup == null) {
				continue;
			}

			for (User user :
					UserLocalServiceUtil.getUserGroupUsers(userGroupId)) {

				if (!user.isActive() || (user.getUserId() == currentUserId) ||
					!userIds.add(user.getUserId()) ||
					!_mentionableUnsafePredicate.test(user)) {

					continue;
				}

				addRuntimeSubscribers(
					user.getEmailAddress(), user.getFullName());
			}

			_userGroupName = userGroup.getName();

			super.flushNotifications();
		}
	}

	public void setDiscussionClassName(String discussionClassName) {
		_discussionClassName = discussionClassName;
	}

	public void setDiscussionClassPK(long discussionClassPK) {
		_discussionClassPK = discussionClassPK;
	}

	public void setExcludedUserGroupIds(Set<Long> excludedUserGroupIds) {
		_excludedUserGroupIds = excludedUserGroupIds;
	}

	public void setExcludedUserIds(Set<Long> excludedUserIds) {
		_excludedUserIds = excludedUserIds;
	}

	public void setMentionableUnsafePredicate(
		UnsafePredicate<User, Exception> mentionableUnsafePredicate) {

		_mentionableUnsafePredicate = mentionableUnsafePredicate;
	}

	public void setUserGroupIds(Set<Long> userGroupIds) {
		_userGroupIds = userGroupIds;
	}

	@Override
	protected void populateNotificationEventJSONObject(
		JSONObject notificationEventJSONObject) {

		super.populateNotificationEventJSONObject(notificationEventJSONObject);

		if (Validator.isNotNull(_userGroupName)) {
			notificationEventJSONObject.put("userGroupName", _userGroupName);
		}

		if (Validator.isNull(_discussionClassName)) {
			return;
		}

		notificationEventJSONObject.put(
			"discussionClassName", _discussionClassName
		).put(
			"discussionClassPK", _discussionClassPK
		);
	}

	private String _discussionClassName;
	private long _discussionClassPK;
	private Set<Long> _excludedUserGroupIds = Collections.emptySet();
	private Set<Long> _excludedUserIds = Collections.emptySet();
	private UnsafePredicate<User, Exception> _mentionableUnsafePredicate;
	private Set<Long> _userGroupIds = Collections.emptySet();
	private String _userGroupName;

}
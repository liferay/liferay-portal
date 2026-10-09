/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.notification;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationManagerUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Portal;

import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(service = CTUserNotificationSender.class)
public class CTUserNotificationSender {

	public long[] getPublicationRoleUserIds(
		CTCollection ctCollection, boolean includeOwner,
		String... publicationRoleNames) {

		Set<Long> userIds = new HashSet<>();

		if (includeOwner) {
			userIds.add(ctCollection.getUserId());
		}

		Group group = _groupLocalService.fetchGroup(
			ctCollection.getCompanyId(),
			_portal.getClassNameId(CTCollection.class),
			ctCollection.getCtCollectionId());

		if (group == null) {
			return ArrayUtil.toLongArray(userIds);
		}

		for (String publicationRoleName : publicationRoleNames) {
			Role role = _roleLocalService.fetchRole(
				group.getCompanyId(), publicationRoleName);

			if (role == null) {
				continue;
			}

			userIds.addAll(
				TransformUtil.transform(
					_userGroupRoleLocalService.getUserGroupRolesByGroupAndRole(
						group.getGroupId(), role.getRoleId()),
					UserGroupRole::getUserId));
		}

		return ArrayUtil.toLongArray(userIds);
	}

	public void sendUserNotificationEvents(
			CTCollection ctCollection, JSONObject notificationEventJSONObject,
			long[] userIds)
		throws PortalException {

		for (long userId : userIds) {
			if (!UserNotificationManagerUtil.isDeliver(
					userId, CTPortletKeys.PUBLICATIONS, 0,
					UserNotificationDefinition.NOTIFICATION_TYPE_REVIEW_ENTRY,
					UserNotificationDeliveryConstants.TYPE_WEBSITE)) {

				continue;
			}

			_userNotificationEventLocalService.sendUserNotificationEvents(
				userId, CTPortletKeys.PUBLICATIONS,
				UserNotificationDeliveryConstants.TYPE_WEBSITE, false,
				notificationEventJSONObject);
		}
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}
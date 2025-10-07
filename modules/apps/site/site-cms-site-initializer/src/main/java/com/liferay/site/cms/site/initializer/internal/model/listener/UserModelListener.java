/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.model.listener;

import com.liferay.depot.constants.DepotPortletKeys;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.notifications.NotificationEvent;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationManagerUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Balázs Sáfrány-Kovalik
 */
@Component(service = ModelListener.class)
public class UserModelListener extends BaseModelListener<User> {

	@Override
	public void onAfterAddAssociation(
			Object classPK, String associationClassName,
			Object associationClassPK)
		throws ModelListenerException {

		try {
			_onAfterAddAssociation(
				classPK, associationClassName, associationClassPK);
		}
		catch (PortalException portalException) {
			throw new ModelListenerException(portalException);
		}
	}

	private void _addUserNotificationEvent(long groupId, User user)
		throws PortalException {

		if (!UserNotificationManagerUtil.isDeliver(
				user.getUserId(), DepotPortletKeys.DEPOT_ADMIN, 0,
				UserNotificationDefinition.NOTIFICATION_TYPE_ADD_ENTRY,
				UserNotificationDeliveryConstants.TYPE_WEBSITE)) {

			return;
		}

		DepotEntry depotEntry = _depotEntryLocalService.getGroupDepotEntry(
			groupId);

		NotificationEvent notificationEvent = new NotificationEvent(
			System.currentTimeMillis(), DepotPortletKeys.DEPOT_ADMIN,
			JSONUtil.put(
				"classPK", depotEntry.getDepotEntryId()
			).put(
				"notificationType",
				UserNotificationDefinition.NOTIFICATION_TYPE_ADD_ENTRY
			).put(
				"userId", user.getUserId()
			).put(
				"userName", user.getFullName()
			));

		notificationEvent.setDeliveryType(
			UserNotificationDeliveryConstants.TYPE_WEBSITE);

		_userNotificationEventLocalService.addUserNotificationEvent(
			user.getUserId(), notificationEvent);
	}

	private void _onAfterAddAssociation(
			Object classPK, String associationClassName,
			Object associationClassPK)
		throws PortalException {

		User user = _userLocalService.getUser((Long)classPK);

		if (!FeatureFlagManagerUtil.isEnabled(
				user.getCompanyId(), "LPD-17564")) {

			return;
		}

		if (associationClassName.equals(Group.class.getName())) {
			_onAfterAddAssociationGroup(user, (Long)associationClassPK);
		}
		else if (associationClassName.equals(UserGroup.class.getName())) {
			_onAfterAddAssociationUserGroup(user, (Long)associationClassPK);
		}
	}

	private void _onAfterAddAssociationGroup(User user, long groupId)
		throws PortalException {

		Group group = _groupLocalService.getGroup(groupId);

		if (group.isDepot() && (group.getCreatorUserId() != user.getUserId())) {
			_addUserNotificationEvent(groupId, user);
		}
	}

	private void _onAfterAddAssociationUserGroup(User user, long userGroupId)
		throws PortalException {

		for (Group group : _groupLocalService.getUserGroupGroups(userGroupId)) {
			if (group.isDepot()) {
				_addUserNotificationEvent(group.getGroupId(), user);
			}
		}
	}

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.notifications;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.BaseUserNotificationHandler;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationFeedEntry;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.util.Portal;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = "jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
	service = UserNotificationHandler.class
)
public class PublicationsUserNotificationHandler
	extends BaseUserNotificationHandler {

	public PublicationsUserNotificationHandler() {
		setPortletId(CTPortletKeys.PUBLICATIONS);
	}

	@Override
	public UserNotificationFeedEntry interpret(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws PortalException {

		UserNotificationHandler userNotificationHandler =
			_getUserNotificationHandler(userNotificationEvent);

		if (userNotificationHandler == null) {
			return super.interpret(userNotificationEvent, serviceContext);
		}

		return userNotificationHandler.interpret(
			userNotificationEvent, serviceContext);
	}

	@Activate
	protected void activate() {
		_publicationChangeSizeClassificationUserNotificationHandler =
			new PublicationChangeSizeClassificationUserNotificationHandler(
				_ctCollectionLocalService, _jsonFactory, _language, _portal);
		_publicationInviteUserNotificationHandler =
			new PublicationInviteUserNotificationHandler(
				_ctCollectionLocalService, _groupLocalService, _jsonFactory,
				_language, _portal, _roleLocalService,
				_userGroupRoleLocalService, _userNotificationEventLocalService);
		_scheduledPublicationUserNotificationHandler =
			new ScheduledPublicationUserNotificationHandler(
				_ctCollectionLocalService, _jsonFactory, _language, _portal,
				_roleLocalService, _userLocalService,
				_userNotificationEventLocalService);
	}

	private UserNotificationHandler _getUserNotificationHandler(
		UserNotificationEvent userNotificationEvent) {

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				userNotificationEvent.getPayload());

			int notificationType = jsonObject.getInt("notificationType");

			if (notificationType ==
					UserNotificationDefinition.NOTIFICATION_TYPE_ADD_ENTRY) {

				return _publicationInviteUserNotificationHandler;
			}

			if (notificationType ==
					UserNotificationDefinition.NOTIFICATION_TYPE_REVIEW_ENTRY) {

				return _scheduledPublicationUserNotificationHandler;
			}

			if (notificationType ==
					UserNotificationDefinition.NOTIFICATION_TYPE_UPDATE_ENTRY) {

				return _publicationChangeSizeClassificationUserNotificationHandler;
			}

			return null;
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PublicationsUserNotificationHandler.class);

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	private PublicationChangeSizeClassificationUserNotificationHandler
		_publicationChangeSizeClassificationUserNotificationHandler;
	private PublicationInviteUserNotificationHandler
		_publicationInviteUserNotificationHandler;

	@Reference
	private RoleLocalService _roleLocalService;

	private ScheduledPublicationUserNotificationHandler
		_scheduledPublicationUserNotificationHandler;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}
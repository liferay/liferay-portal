/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.internal.type;

import com.liferay.notification.constants.NotificationConstants;
import com.liferay.notification.constants.NotificationQueueEntryConstants;
import com.liferay.notification.constants.NotificationRecipientConstants;
import com.liferay.notification.constants.NotificationRecipientSettingConstants;
import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.internal.type.users.provider.DefaultUsersProvider;
import com.liferay.notification.internal.type.users.provider.RoleUsersProvider;
import com.liferay.notification.internal.type.users.provider.TermUsersProvider;
import com.liferay.notification.internal.type.users.provider.UserGroupUsersProvider;
import com.liferay.notification.internal.type.users.provider.UsersProvider;
import com.liferay.notification.model.NotificationQueueEntry;
import com.liferay.notification.model.NotificationRecipient;
import com.liferay.notification.model.NotificationRecipientSetting;
import com.liferay.notification.model.NotificationRecipientSettingModel;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.type.BaseNotificationType;
import com.liferay.notification.type.NotificationType;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationManagerUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.SetUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 */
@Component(service = NotificationType.class)
public class UserNotificationType extends BaseNotificationType {

	@Override
	public NotificationQueueEntry createNotificationQueueEntry(
		User user, String body, NotificationContext notificationContext,
		String subject) {

		NotificationQueueEntry notificationQueueEntry =
			super.createNotificationQueueEntry(
				user, body, notificationContext, subject);

		notificationQueueEntry.setStatus(
			NotificationQueueEntryConstants.STATUS_SENT);

		return notificationQueueEntry;
	}

	@Override
	public Set<String> getAllowedNotificationRecipientSettingsNames() {
		return SetUtil.fromArray(
			NotificationRecipientSettingConstants.NAME_ROLE_NAME,
			NotificationRecipientSettingConstants.NAME_TERM,
			NotificationRecipientSettingConstants.NAME_USER_GROUP_NAME,
			NotificationRecipientSettingConstants.NAME_USER_SCREEN_NAME);
	}

	@Override
	public String getRecipientSummary(
		NotificationQueueEntry notificationQueueEntry) {

		NotificationRecipient notificationRecipient =
			notificationQueueEntry.getNotificationRecipient();

		List<String> values = new ArrayList<>();

		for (NotificationRecipientSetting notificationRecipientSetting :
				notificationRecipient.getNotificationRecipientSettings()) {

			values.add(notificationRecipientSetting.getValue());
		}

		return ListUtil.toString(
			values, (String)null, StringPool.COMMA_AND_SPACE);
	}

	@Override
	public String getType() {
		return NotificationConstants.TYPE_USER_NOTIFICATION;
	}

	@Override
	public String getTypeLanguageKey() {
		return "user-notification";
	}

	@Override
	public void sendNotification(NotificationContext notificationContext)
		throws PortalException {

		NotificationTemplate notificationTemplate =
			notificationContext.getNotificationTemplate();

		if (Objects.equals(
				notificationTemplate.getRecipientType(),
				NotificationRecipientConstants.TYPE_USER_GROUP) &&
			!FeatureFlagManagerUtil.isEnabled("LPD-50091")) {

			return;
		}

		boolean enqueue = false;
		List<Map<String, String>> notificationRecipientSettings =
			new ArrayList<>();

		UsersProvider usersProvider = _usersProviders.get(
			notificationTemplate.getRecipientType());

		NotificationRecipient notificationRecipient =
			notificationTemplate.getNotificationRecipient();

		for (User user :
				usersProvider.provide(
					notificationContext,
					TransformUtil.unsafeTransform(
						notificationRecipient.
							getNotificationRecipientSettings(),
						NotificationRecipientSettingModel::getValue))) {

			boolean deliver = UserNotificationManagerUtil.isDeliver(
				user.getUserId(), notificationContext.getPortletId(),
				_classNameLocalService.getClassNameId(
					notificationContext.getClassName()),
				UserNotificationDefinition.NOTIFICATION_TYPE_UPDATE_ENTRY,
				UserNotificationDeliveryConstants.TYPE_WEBSITE);

			if (!deliver ||
				!_objectEntryService.hasModelResourcePermission(
					user, notificationContext.getClassPK(), ActionKeys.VIEW)) {

				continue;
			}

			enqueue = true;
			siteDefaultLocale = portal.getSiteDefaultLocale(user.getGroupId());
			userLocale = user.getLocale();

			_userNotificationEventLocalService.sendUserNotificationEvents(
				user.getUserId(), notificationContext.getPortletId(),
				UserNotificationDeliveryConstants.TYPE_WEBSITE,
				JSONUtil.put(
					"className", notificationContext.getClassName()
				).put(
					"classPK", notificationContext.getClassPK()
				).put(
					"externalReferenceCode",
					notificationContext.getExternalReferenceCode()
				).put(
					"notificationMessage",
					formatLocalizedContent(
						notificationTemplate.getSubjectMap(),
						notificationContext)
				).put(
					"portletId", notificationContext.getPortletId()
				));

			notificationRecipientSettings.add(
				HashMapBuilder.put(
					"userFullName", user.getFullName()
				).build());
		}

		if (enqueue) {
			User user = userLocalService.getUser(
				notificationContext.getUserId());

			siteDefaultLocale = portal.getSiteDefaultLocale(user.getGroupId());
			userLocale = user.getLocale();

			prepareNotificationContext(
				user, null, notificationContext, notificationRecipientSettings,
				formatLocalizedContent(
					notificationTemplate.getSubjectMap(), notificationContext));

			notificationQueueEntryLocalService.addNotificationQueueEntry(
				notificationContext);
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		RoleUsersProvider roleUsersProvider = new RoleUsersProvider(
			_permissionCheckerFactory, _roleLocalService,
			_userGroupRoleLocalService, userLocalService);

		_usersProviders.put(
			NotificationRecipientConstants.TYPE_ROLE, roleUsersProvider);
		_usersProviders.put(
			NotificationRecipientConstants.TYPE_TERM,
			new TermUsersProvider(
				notificationTermEvaluatorTracker, _permissionCheckerFactory,
				_roleLocalService, roleUsersProvider, userLocalService));

		_usersProviders.put(
			NotificationRecipientConstants.TYPE_USER,
			new DefaultUsersProvider(
				_permissionCheckerFactory, userLocalService));
		_usersProviders.put(
			NotificationRecipientConstants.TYPE_USER_GROUP,
			new UserGroupUsersProvider(
				_permissionCheckerFactory, _userGroupLocalService,
				userLocalService));
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

	private final Map<String, UsersProvider> _usersProviders = new HashMap<>();

}
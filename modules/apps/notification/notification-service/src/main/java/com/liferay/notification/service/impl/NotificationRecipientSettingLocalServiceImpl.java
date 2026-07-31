/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.service.impl;

import com.liferay.notification.constants.NotificationRecipientConstants;
import com.liferay.notification.constants.NotificationRecipientSettingConstants;
import com.liferay.notification.model.NotificationRecipientSetting;
import com.liferay.notification.service.base.NotificationRecipientSettingLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 */
@Component(
	property = "model.class.name=com.liferay.notification.model.NotificationRecipientSetting",
	service = AopService.class
)
public class NotificationRecipientSettingLocalServiceImpl
	extends NotificationRecipientSettingLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public NotificationRecipientSetting addNotificationRecipientSetting(
			long userId, long notificationRecipientId, String name,
			Object value)
		throws PortalException {

		NotificationRecipientSetting notificationRecipientSetting =
			notificationRecipientSettingPersistence.create(
				counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		notificationRecipientSetting.setCompanyId(user.getCompanyId());
		notificationRecipientSetting.setUserId(user.getUserId());
		notificationRecipientSetting.setUserName(user.getFullName());

		notificationRecipientSetting.setNotificationRecipientId(
			notificationRecipientId);
		notificationRecipientSetting.setName(name);

		_setValue(notificationRecipientSetting, value);

		return notificationRecipientSettingPersistence.update(
			notificationRecipientSetting);
	}

	@Override
	public List<NotificationRecipientSetting>
		createNotificationRecipientSettings(
			long notificationRecipientId, Object[] recipients, User user) {

		List<NotificationRecipientSetting> notificationRecipientSettings =
			new ArrayList<>();

		for (Object recipient : recipients) {
			Map<String, Object> recipientMap = (Map<String, Object>)recipient;

			for (Map.Entry<String, Object> entry : recipientMap.entrySet()) {
				if (Objects.equals(
						recipientMap.get(
							NotificationRecipientSettingConstants.
								getRecipientTypeName(entry.getKey())),
						NotificationRecipientConstants.TYPE_SUBSCRIBERS)) {

					continue;
				}

				_addNotificationRecipientSetting(
					entry, notificationRecipientId,
					notificationRecipientSettings,
					GetterUtil.getString(
						recipientMap.get(
							NotificationRecipientSettingConstants.
								getRecipientTypeName(entry.getKey()))),
					user);
			}
		}

		return notificationRecipientSettings;
	}

	@Override
	public NotificationRecipientSetting fetchNotificationRecipientSetting(
		long notificationRecipientId, String name) {

		return notificationRecipientSettingPersistence.fetchByNRI_N(
			notificationRecipientId, name);
	}

	@Override
	public List<NotificationRecipientSetting> getNotificationRecipientSettings(
		long notificationRecipientId) {

		return notificationRecipientSettingPersistence.
			findByNotificationRecipientId(notificationRecipientId);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public NotificationRecipientSetting updateNotificationRecipientSetting(
		long notificationRecipientId, String name, Object value) {

		NotificationRecipientSetting notificationRecipientSetting =
			notificationRecipientSettingPersistence.fetchByNRI_N(
				notificationRecipientId, name);

		_setValue(notificationRecipientSetting, value);

		return notificationRecipientSettingPersistence.update(
			notificationRecipientSetting);
	}

	private void _addNotificationRecipientSetting(
		Map.Entry<String, Object> entry, long notificationRecipientId,
		List<NotificationRecipientSetting> notificationRecipientSettings,
		String recipientType, User user) {

		if (Objects.equals(
				recipientType, NotificationRecipientConstants.TYPE_ROLE)) {

			Set<String> roleNames = new HashSet<>();

			for (Map<String, String> roleMap : _toList(entry.getValue())) {
				String roleName = roleMap.get(
					NotificationRecipientSettingConstants.NAME_ROLE_NAME);

				if (Validator.isNull(roleName) ||
					roleNames.contains(roleName)) {

					continue;
				}

				Role role = _roleLocalService.fetchRole(
					user.getCompanyId(), roleName);

				if ((role == null) ||
					((role.getType() != RoleConstants.TYPE_ACCOUNT) &&
					 (role.getType() != RoleConstants.TYPE_ORGANIZATION) &&
					 (role.getType() != RoleConstants.TYPE_REGULAR))) {

					continue;
				}

				roleNames.add(roleName);

				_addNotificationRecipientSetting(
					entry.getKey(), notificationRecipientId,
					notificationRecipientSettings, user, roleName);
			}
		}
		else if (Objects.equals(
					recipientType,
					NotificationRecipientConstants.TYPE_USER_GROUP)) {

			Set<String> userGroupNames = new HashSet<>();

			for (Map<String, String> userGroupMap : _toList(entry.getValue())) {
				String userGroupName = userGroupMap.get(
					NotificationRecipientSettingConstants.NAME_USER_GROUP_NAME);

				if (Validator.isNull(userGroupName) ||
					userGroupNames.contains(userGroupName)) {

					continue;
				}

				UserGroup userGroup = _userGroupLocalService.fetchUserGroup(
					user.getCompanyId(), userGroupName);

				if (userGroup == null) {
					continue;
				}

				userGroupNames.add(userGroupName);

				_addNotificationRecipientSetting(
					entry.getKey(), notificationRecipientId,
					notificationRecipientSettings, user, userGroupName);
			}
		}
		else {
			_addNotificationRecipientSetting(
				entry.getKey(), notificationRecipientId,
				notificationRecipientSettings, user, entry.getValue());
		}
	}

	private void _addNotificationRecipientSetting(
		String name, long notificationRecipientId,
		List<NotificationRecipientSetting> notificationRecipientSettings,
		User user, Object value) {

		NotificationRecipientSetting notificationRecipientSetting =
			notificationRecipientSettingPersistence.create(0);

		notificationRecipientSetting.setCompanyId(user.getCompanyId());
		notificationRecipientSetting.setUserId(user.getUserId());
		notificationRecipientSetting.setUserName(user.getFullName());
		notificationRecipientSetting.setNotificationRecipientId(
			notificationRecipientId);
		notificationRecipientSetting.setName(name);

		if (value instanceof Map) {
			notificationRecipientSetting.setValueMap(
				LocalizedMapUtil.getLocalizedMap((Map)value));
		}
		else {
			notificationRecipientSetting.setValue(String.valueOf(value));
		}

		notificationRecipientSettings.add(notificationRecipientSetting);
	}

	private void _setValue(
		NotificationRecipientSetting notificationRecipientSetting,
		Object value) {

		if (value instanceof String) {
			notificationRecipientSetting.setValue(String.valueOf(value));
		}
		else {
			notificationRecipientSetting.setValueMap(
				(Map<Locale, String>)value);
		}
	}

	private List<Map<String, String>> _toList(Object value) {
		if (value instanceof Object[]) {
			value = Arrays.asList((Object[])value);
		}

		return (List<Map<String, String>>)value;
	}

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.service.impl;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.notification.constants.NotificationRecipientConstants;
import com.liferay.notification.constants.NotificationRecipientSettingConstants;
import com.liferay.notification.exception.NotificationRecipientSettingValueException;
import com.liferay.notification.model.NotificationRecipientSetting;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.service.base.NotificationRecipientSettingLocalServiceBaseImpl;
import com.liferay.notification.type.util.NotificationTypeUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.NoSuchRoleException;
import com.liferay.portal.kernel.exception.NoSuchUserGroupException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.RoleService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserGroupService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.roles.admin.role.type.contributor.RoleTypeContributor;
import com.liferay.roles.admin.role.type.contributor.provider.RoleTypeContributorProvider;

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
			long notificationRecipientId,
			NotificationTemplate notificationTemplate, Object[] recipients,
			User user) {

		List<NotificationRecipientSetting> notificationRecipientSettings =
			new ArrayList<>();

		List<String> missingUserScreenNames = new ArrayList<>();

		for (Object recipient : recipients) {
			Map<String, Object> recipientMap = (Map<String, Object>)recipient;

			for (Map.Entry<String, Object> entry : recipientMap.entrySet()) {
				if (_isRecipientReferenceName(entry.getKey())) {
					continue;
				}

				String recipientType = GetterUtil.getString(
					recipientMap.get(
						NotificationRecipientSettingConstants.
							getRecipientTypeName(entry.getKey())));

				if (Objects.equals(
						recipientType,
						NotificationRecipientConstants.TYPE_ROLE) ||
					Objects.equals(
						recipientType,
						NotificationRecipientConstants.TYPE_USER_GROUP)) {

					_addMultipleValueNotificationRecipientSettings(
						entry, notificationRecipientId,
						notificationRecipientSettings, recipientType, user);
				}
				else if (!Objects.equals(
							recipientType,
							NotificationRecipientConstants.TYPE_SUBSCRIBERS)) {

					_addSingleValueNotificationRecipientSetting(
						entry, missingUserScreenNames, notificationRecipientId,
						notificationRecipientSettings, recipientMap, user);
				}
			}
		}

		_reportMissingUserRecipients(
			missingUserScreenNames, notificationTemplate, user);

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

	private void _addMultipleValueNotificationRecipientSettings(
		Map.Entry<String, Object> entry, long notificationRecipientId,
		List<NotificationRecipientSetting> notificationRecipientSettings,
		String recipientType, User user) {

		if (Objects.equals(
				recipientType, NotificationRecipientConstants.TYPE_ROLE)) {

			Set<String> roleNames = new HashSet<>();

			for (Map<String, String> roleMap : _toList(entry.getValue())) {
				Role role = _getRole(
					roleMap.get(
						NotificationRecipientSettingConstants.
							NAME_ROLE_EXTERNAL_REFERENCE_CODE),
					roleMap.get(
						NotificationRecipientSettingConstants.NAME_ROLE_NAME),
					roleMap.get(
						NotificationRecipientSettingConstants.NAME_ROLE_TYPE),
					user);

				if ((role == null) ||
					((role.getType() != RoleConstants.TYPE_ACCOUNT) &&
					 (role.getType() != RoleConstants.TYPE_ORGANIZATION) &&
					 (role.getType() != RoleConstants.TYPE_REGULAR)) ||
					roleNames.contains(role.getName())) {

					continue;
				}

				roleNames.add(role.getName());

				_addNotificationRecipientSetting(
					entry.getKey(), notificationRecipientId,
					notificationRecipientSettings, user, role.getName());
			}
		}
		else {
			Set<String> userGroupNames = new HashSet<>();

			for (Map<String, String> userGroupMap : _toList(entry.getValue())) {
				UserGroup userGroup = _getUserGroup(
					userGroupMap.get(
						NotificationRecipientSettingConstants.
							NAME_USER_GROUP_EXTERNAL_REFERENCE_CODE),
					userGroupMap.get(
						NotificationRecipientSettingConstants.
							NAME_USER_GROUP_NAME),
					user);

				if ((userGroup == null) ||
					userGroupNames.contains(userGroup.getName())) {

					continue;
				}

				userGroupNames.add(userGroup.getName());

				_addNotificationRecipientSetting(
					entry.getKey(), notificationRecipientId,
					notificationRecipientSettings, user, userGroup.getName());
			}
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
			notificationRecipientSetting.setValueMap(_toLocalizedMap(value));
		}
		else {
			notificationRecipientSetting.setValue(String.valueOf(value));
		}

		notificationRecipientSettings.add(notificationRecipientSetting);
	}

	private void _addSingleValueNotificationRecipientSetting(
		Map.Entry<String, Object> entry, List<String> missingUserScreenNames,
		long notificationRecipientId,
		List<NotificationRecipientSetting> notificationRecipientSettings,
		Map<String, Object> recipientMap, User user) {

		if (!FeatureFlagManagerUtil.isEnabled(
				user.getCompanyId(), "LPD-49854")) {

			_addNotificationRecipientSetting(
				entry.getKey(), notificationRecipientId,
				notificationRecipientSettings, user, entry.getValue());

			return;
		}

		String value = GetterUtil.getString(entry.getValue());

		if (Objects.equals(
				entry.getKey(),
				NotificationRecipientSettingConstants.NAME_ROLE_NAME)) {

			Role role = _getRole(
				GetterUtil.getString(
					recipientMap.get(
						NotificationRecipientSettingConstants.
							NAME_ROLE_EXTERNAL_REFERENCE_CODE)),
				value,
				GetterUtil.getString(
					recipientMap.get(
						NotificationRecipientSettingConstants.NAME_ROLE_TYPE)),
				user);

			if (role != null) {
				_addNotificationRecipientSetting(
					entry.getKey(), notificationRecipientId,
					notificationRecipientSettings, user, role.getName());
			}
		}
		else if (Objects.equals(
					entry.getKey(),
					NotificationRecipientSettingConstants.
						NAME_USER_GROUP_NAME)) {

			UserGroup userGroup = _getUserGroup(
				GetterUtil.getString(
					recipientMap.get(
						NotificationRecipientSettingConstants.
							NAME_USER_GROUP_EXTERNAL_REFERENCE_CODE)),
				value, user);

			if (userGroup != null) {
				_addNotificationRecipientSetting(
					entry.getKey(), notificationRecipientId,
					notificationRecipientSettings, user, userGroup.getName());
			}
		}
		else if (Objects.equals(
					entry.getKey(),
					NotificationRecipientSettingConstants.
						NAME_USER_SCREEN_NAME) &&
				 !NotificationTypeUtil.isTermValue(value)) {

			User recipientUser = _getUser(
				GetterUtil.getString(
					recipientMap.get(
						NotificationRecipientSettingConstants.
							NAME_USER_EXTERNAL_REFERENCE_CODE)),
				value, user);

			if (recipientUser != null) {
				_addNotificationRecipientSetting(
					entry.getKey(), notificationRecipientId,
					notificationRecipientSettings, user,
					recipientUser.getScreenName());
			}
			else {
				missingUserScreenNames.add(value);
			}
		}
		else {
			_addNotificationRecipientSetting(
				entry.getKey(), notificationRecipientId,
				notificationRecipientSettings, user, entry.getValue());
		}
	}

	private Role _getRole(
		String externalReferenceCode, String name, String typeLabel,
		User user) {

		if (Validator.isNotNull(externalReferenceCode) &&
			FeatureFlagManagerUtil.isEnabled(
				user.getCompanyId(), "LPD-49854")) {

			try {
				return _roleService.getOrAddEmptyRole(
					externalReferenceCode, _getRoleClassName(typeLabel), 0,
					name, RoleConstants.getLabelType(typeLabel));
			}
			catch (NoSuchRoleException noSuchRoleException) {
				return ReflectionUtil.throwException(
					new NotificationRecipientSettingValueException.
						RoleMustExist(
							externalReferenceCode, noSuchRoleException));
			}
			catch (Exception exception) {
				return ReflectionUtil.throwException(exception);
			}
		}

		if (Validator.isNull(name)) {
			return null;
		}

		return _roleLocalService.fetchRole(user.getCompanyId(), name);
	}

	private String _getRoleClassName(String typeLabel) {
		for (RoleTypeContributor roleTypeContributor :
				_roleTypeContributorProvider.getRoleTypeContributors()) {

			if (StringUtil.equals(
					roleTypeContributor.getTypeLabel(), typeLabel)) {

				return roleTypeContributor.getClassName();
			}
		}

		return null;
	}

	private User _getUser(
		String externalReferenceCode, String screenName, User user) {

		if (Validator.isNotNull(externalReferenceCode)) {
			User recipientUser =
				_userLocalService.fetchUserByExternalReferenceCode(
					externalReferenceCode, user.getCompanyId());

			if (recipientUser != null) {
				return recipientUser;
			}
		}

		if (Validator.isNull(screenName)) {
			return null;
		}

		return _userLocalService.fetchUserByScreenName(
			user.getCompanyId(), screenName);
	}

	private UserGroup _getUserGroup(
		String externalReferenceCode, String name, User user) {

		if (Validator.isNotNull(externalReferenceCode) &&
			FeatureFlagManagerUtil.isEnabled(
				user.getCompanyId(), "LPD-49854")) {

			try {
				return _userGroupService.getOrAddEmptyUserGroup(
					externalReferenceCode, name);
			}
			catch (NoSuchUserGroupException noSuchUserGroupException) {
				return ReflectionUtil.throwException(
					new NotificationRecipientSettingValueException.
						UserGroupMustExist(
							externalReferenceCode, noSuchUserGroupException));
			}
			catch (PortalException portalException) {
				return ReflectionUtil.throwException(portalException);
			}
		}

		if (Validator.isNull(name)) {
			return null;
		}

		return _userGroupLocalService.fetchUserGroup(user.getCompanyId(), name);
	}

	private boolean _isRecipientReferenceName(String name) {
		if (name.equals(
				NotificationRecipientSettingConstants.
					NAME_ROLE_EXTERNAL_REFERENCE_CODE) ||
			name.equals(NotificationRecipientSettingConstants.NAME_ROLE_TYPE) ||
			name.equals(
				NotificationRecipientSettingConstants.
					NAME_USER_EXTERNAL_REFERENCE_CODE) ||
			name.equals(
				NotificationRecipientSettingConstants.
					NAME_USER_GROUP_EXTERNAL_REFERENCE_CODE)) {

			return true;
		}

		return false;
	}

	private void _reportMissingUserRecipients(
		List<String> missingUserScreenNames,
		NotificationTemplate notificationTemplate, User user) {

		if (missingUserScreenNames.isEmpty() ||
			!ExportImportThreadLocal.isImportInProcess() ||
			(notificationTemplate == null)) {

			return;
		}

		_exportImportReportEntryLocalService.getOrAddExportImportReportEntry(
			0, user.getCompanyId(),
			notificationTemplate.getExternalReferenceCode(),
			_portal.getClassNameId(NotificationTemplate.class.getName()),
			notificationTemplate.getNotificationTemplateId(),
			GetterUtil.getLong(
				ExportImportThreadLocal.getExportImportConfigurationId()),
			ExportImportReportEntryConstants.TYPE_WARNING,
			_language.format(
				LocaleUtil.getDefault(),
				"the-following-users-do-not-exist-and-were-removed-from-the-" +
					"recipients-of-notification-template-x-x",
				new Object[] {
					StringUtil.merge(
						missingUserScreenNames, StringPool.COMMA_AND_SPACE),
					notificationTemplate.getName(LocaleUtil.getDefault())
				}),
			null, "notification-template");
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

	private Map<Locale, String> _toLocalizedMap(Object value) {
		Map<?, ?> map = (Map<?, ?>)value;

		for (Object key : map.keySet()) {
			if (!(key instanceof Locale)) {
				return LocalizedMapUtil.getLocalizedMap(
					(Map<String, String>)value);
			}
		}

		return (Map<Locale, String>)value;
	}

	@Reference
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private RoleService _roleService;

	@Reference
	private RoleTypeContributorProvider _roleTypeContributorProvider;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

	@Reference
	private UserGroupService _userGroupService;

	@Reference
	private UserLocalService _userLocalService;

}
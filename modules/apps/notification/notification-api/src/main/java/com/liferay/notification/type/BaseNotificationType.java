/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.type;

import com.liferay.notification.constants.NotificationQueueEntryConstants;
import com.liferay.notification.constants.NotificationRecipientConstants;
import com.liferay.notification.constants.NotificationRecipientSettingConstants;
import com.liferay.notification.constants.NotificationTemplateConstants;
import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.exception.NotificationQueueEntrySubjectException;
import com.liferay.notification.exception.NotificationRecipientSettingNameException;
import com.liferay.notification.exception.NotificationTemplateAttachmentObjectFieldIdException;
import com.liferay.notification.exception.NotificationTemplateDescriptionException;
import com.liferay.notification.exception.NotificationTemplateEditorTypeException;
import com.liferay.notification.exception.NotificationTemplateExternalReferenceCodeException;
import com.liferay.notification.exception.NotificationTemplateNameException;
import com.liferay.notification.exception.NotificationTemplateObjectDefinitionIdException;
import com.liferay.notification.exception.NotificationTemplateSubjectException;
import com.liferay.notification.model.NotificationQueueEntry;
import com.liferay.notification.model.NotificationRecipient;
import com.liferay.notification.model.NotificationRecipientSetting;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.service.NotificationQueueEntryLocalService;
import com.liferay.notification.service.NotificationRecipientLocalService;
import com.liferay.notification.service.NotificationRecipientSettingLocalService;
import com.liferay.notification.term.evaluator.NotificationTermEvaluatorTracker;
import com.liferay.notification.type.util.NotificationTypeUtil;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectFieldLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 */
public abstract class BaseNotificationType implements NotificationType {

	@Override
	public NotificationQueueEntry createNotificationQueueEntry(
		User user, String body, NotificationContext notificationContext,
		String subject) {

		NotificationQueueEntry notificationQueueEntry =
			notificationQueueEntryLocalService.createNotificationQueueEntry(0L);

		notificationQueueEntry.setCompanyId(user.getCompanyId());
		notificationQueueEntry.setUserId(user.getUserId());
		notificationQueueEntry.setUserName(user.getFullName());

		NotificationTemplate notificationTemplate =
			notificationContext.getNotificationTemplate();

		if (notificationTemplate == null) {
			notificationQueueEntry.setNotificationTemplateId(0L);
		}
		else {
			notificationQueueEntry.setNotificationTemplateId(
				notificationTemplate.getNotificationTemplateId());
		}

		notificationQueueEntry.setBody(body);
		notificationQueueEntry.setClassName(notificationContext.getClassName());
		notificationQueueEntry.setClassPK(notificationContext.getClassPK());
		notificationQueueEntry.setPriority(0);
		notificationQueueEntry.setSubject(subject);
		notificationQueueEntry.setType(getType());
		notificationQueueEntry.setStatus(
			NotificationQueueEntryConstants.STATUS_UNSENT);

		return notificationQueueEntry;
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

				String recipientType = GetterUtil.getString(
					recipientMap.get(
						NotificationRecipientSettingConstants.
							getRecipientTypeName(entry.getKey())));

				_addNotificationRecipientSetting(
					entry, notificationRecipientId,
					notificationRecipientSettings, recipientType, user);
			}
		}

		return notificationRecipientSettings;
	}

	@Override
	public String getType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getTypeLanguageKey() {
		return getType();
	}

	@Override
	public void sendNotification(NotificationContext notificationContext)
		throws PortalException {

		throw new UnsupportedOperationException();
	}

	@Override
	public void sendNotification(NotificationQueueEntry notificationQueueEntry)
		throws PortalException {

		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toRecipients(
		List<NotificationRecipientSetting> notificationRecipientSettings) {

		return TransformUtil.transformToArray(
			notificationRecipientSettings,
			notificationRecipientSetting -> HashMapBuilder.put(
				notificationRecipientSetting.getName(),
				notificationRecipientSetting.getValue()
			).build(),
			Object.class);
	}

	@Override
	public void validateNotificationQueueEntry(
			NotificationContext notificationContext)
		throws PortalException {

		NotificationQueueEntry notificationQueueEntry =
			notificationContext.getNotificationQueueEntry();

		if (Validator.isNull(notificationQueueEntry.getSubject())) {
			throw new NotificationQueueEntrySubjectException("Subject is null");
		}
	}

	@Override
	public void validateNotificationTemplate(
			NotificationContext notificationContext)
		throws PortalException {

		Set<String> allowedNotificationRecipientSettingsNames =
			getAllowedNotificationRecipientSettingsNames();

		if (SetUtil.isNotEmpty(allowedNotificationRecipientSettingsNames)) {
			Set<String> notAllowedNotificationRecipientSettingsNames =
				new LinkedHashSet<>();

			ListUtil.isNotEmptyForEach(
				notificationContext.getNotificationRecipientSettings(),
				notificationRecipientSetting -> {
					if (!allowedNotificationRecipientSettingsNames.contains(
							notificationRecipientSetting.getName())) {

						notAllowedNotificationRecipientSettingsNames.add(
							notificationRecipientSetting.getName());
					}
				});

			if (!notAllowedNotificationRecipientSettingsNames.isEmpty()) {
				throw new NotificationRecipientSettingNameException.
					NotAllowedNames(
						notAllowedNotificationRecipientSettingsNames);
			}
		}

		NotificationTemplate notificationTemplate =
			notificationContext.getNotificationTemplate();

		if (FeatureFlagManagerUtil.isEnabled(
				notificationContext.getCompanyId(), "LPD-17564") &&
			!notificationTemplate.isSystem() &&
			StringUtil.startsWith(
				notificationTemplate.getExternalReferenceCode(),
				NotificationTemplateConstants.
					EXTERNAL_REFERENCE_CODE_PREFIX_SYSTEM_NOTIFICATION_TEMPLATE)) {

			throw new NotificationTemplateExternalReferenceCodeException.
				MustNotStartWithPrefix();
		}

		if (notificationTemplate.getObjectDefinitionId() > 0) {
			ObjectDefinition objectDefinition =
				ObjectDefinitionLocalServiceUtil.fetchObjectDefinition(
					notificationTemplate.getObjectDefinitionId());

			if (objectDefinition == null) {
				throw new NotificationTemplateObjectDefinitionIdException();
			}
		}

		String description = notificationTemplate.getDescription();

		if (description.length() > 255) {
			throw new NotificationTemplateDescriptionException(
				"The description cannot contain more than 255 characters");
		}

		if (Validator.isNull(notificationTemplate.getEditorType())) {
			throw new NotificationTemplateEditorTypeException(
				"Editor type is null");
		}

		if (Validator.isNull(notificationTemplate.getName())) {
			throw new NotificationTemplateNameException("Name is null");
		}

		if (Validator.isNull(notificationTemplate.getSubject())) {
			throw new NotificationTemplateSubjectException("Subject is null");
		}

		for (long attachmentObjectFieldId :
				notificationContext.getAttachmentObjectFieldIds()) {

			ObjectField objectField =
				ObjectFieldLocalServiceUtil.fetchObjectField(
					attachmentObjectFieldId);

			if ((objectField == null) ||
				!Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT) ||
				!Objects.equals(
					objectField.getObjectDefinitionId(),
					notificationTemplate.getObjectDefinitionId())) {

				throw new NotificationTemplateAttachmentObjectFieldIdException();
			}
		}
	}

	protected NotificationRecipient createNotificationRecipient(
		User user, long notificationQueueEntryId) {

		NotificationRecipient notificationRecipient =
			notificationRecipientLocalService.createNotificationRecipient(0L);

		notificationRecipient.setCompanyId(user.getCompanyId());
		notificationRecipient.setUserId(user.getUserId());
		notificationRecipient.setUserName(user.getFullName());
		notificationRecipient.setClassName(
			NotificationQueueEntry.class.getName());
		notificationRecipient.setClassPK(notificationQueueEntryId);

		return notificationRecipient;
	}

	protected List<NotificationRecipientSetting>
		createNotificationRecipientSettings(
			User user, long notificationRecipientId,
			Map<String, String> notificationRecipientSettingsMap) {

		List<NotificationRecipientSetting> notificationRecipientSettings =
			new ArrayList<>();

		for (Map.Entry<String, String> entry :
				notificationRecipientSettingsMap.entrySet()) {

			NotificationRecipientSetting notificationRecipientSetting =
				notificationRecipientSettingLocalService.
					createNotificationRecipientSetting(0L);

			notificationRecipientSetting.setCompanyId(user.getCompanyId());
			notificationRecipientSetting.setUserId(user.getUserId());
			notificationRecipientSetting.setUserName(user.getFullName());
			notificationRecipientSetting.setNotificationRecipientId(
				notificationRecipientId);
			notificationRecipientSetting.setName(entry.getKey());
			notificationRecipientSetting.setValue(entry.getValue());

			notificationRecipientSettings.add(notificationRecipientSetting);
		}

		return notificationRecipientSettings;
	}

	protected String formatLocalizedContent(
			Map<Locale, String> contentMap,
			NotificationContext notificationContext)
		throws PortalException {

		String content = NotificationTypeUtil.evaluateTerms(
			contentMap.get(userLocale), notificationContext,
			notificationTermEvaluatorTracker);

		if (Validator.isNotNull(content)) {
			return content;
		}

		return NotificationTypeUtil.evaluateTerms(
			contentMap.get(siteDefaultLocale), notificationContext,
			notificationTermEvaluatorTracker);
	}

	protected void prepareNotificationContext(
		User user, String body, NotificationContext notificationContext,
		Object evaluatedNotificationRecipientSettings, String subject) {

		NotificationQueueEntry notificationQueueEntry =
			createNotificationQueueEntry(
				user, body, notificationContext, subject);

		notificationContext.setNotificationQueueEntry(notificationQueueEntry);

		NotificationRecipient notificationQueueEntryRecipient =
			createNotificationRecipient(
				user, notificationQueueEntry.getNotificationQueueEntryId());

		notificationContext.setNotificationRecipient(
			notificationQueueEntryRecipient);

		if (evaluatedNotificationRecipientSettings instanceof Map) {
			notificationContext.setNotificationRecipientSettings(
				createNotificationRecipientSettings(
					user,
					notificationQueueEntryRecipient.
						getNotificationRecipientId(),
					(Map<String, String>)
						evaluatedNotificationRecipientSettings));
		}
		else {
			List<NotificationRecipientSetting> notificationRecipientSettings =
				new ArrayList<>();

			for (Map<String, String> evaluatedNotificationRecipientSetting :
					(List<Map<String, String>>)
						evaluatedNotificationRecipientSettings) {

				notificationRecipientSettings.addAll(
					createNotificationRecipientSettings(
						user,
						notificationQueueEntryRecipient.
							getNotificationRecipientId(),
						evaluatedNotificationRecipientSetting));
			}

			notificationContext.setNotificationRecipientSettings(
				notificationRecipientSettings);
		}
	}

	@Reference
	protected NotificationQueueEntryLocalService
		notificationQueueEntryLocalService;

	@Reference
	protected NotificationRecipientLocalService
		notificationRecipientLocalService;

	@Reference
	protected NotificationRecipientSettingLocalService
		notificationRecipientSettingLocalService;

	@Reference
	protected NotificationTermEvaluatorTracker notificationTermEvaluatorTracker;

	@Reference
	protected Portal portal;

	@Reference
	protected RoleLocalService roleLocalService;

	protected Locale siteDefaultLocale;

	@Reference
	protected UserGroupLocalService userGroupLocalService;

	protected Locale userLocale;

	@Reference
	protected UserLocalService userLocalService;

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

				Role role = roleLocalService.fetchRole(
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

				UserGroup userGroup = userGroupLocalService.fetchUserGroup(
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
			notificationRecipientSettingLocalService.
				createNotificationRecipientSetting(0);

		notificationRecipientSetting.setCompanyId(user.getCompanyId());
		notificationRecipientSetting.setUserId(user.getUserId());
		notificationRecipientSetting.setUserName(user.getFullName());
		notificationRecipientSetting.setNotificationRecipientId(
			notificationRecipientId);
		notificationRecipientSetting.setName(name);

		if (value instanceof LinkedHashMap) {
			notificationRecipientSetting.setValueMap(
				LocalizedMapUtil.getLocalizedMap((LinkedHashMap)value));
		}
		else {
			notificationRecipientSetting.setValue(String.valueOf(value));
		}

		notificationRecipientSettings.add(notificationRecipientSetting);
	}

	private List<Map<String, String>> _toList(Object value) {
		if (value instanceof Object[]) {
			value = Arrays.asList((Object[])value);
		}

		return (List<Map<String, String>>)value;
	}

}
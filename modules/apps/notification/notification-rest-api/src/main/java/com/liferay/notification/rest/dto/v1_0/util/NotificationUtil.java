/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.rest.dto.v1_0.util;

import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.model.NotificationRecipient;
import com.liferay.notification.model.NotificationRecipientSetting;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.service.NotificationRecipientLocalServiceUtil;
import com.liferay.notification.service.NotificationTemplateLocalServiceUtil;
import com.liferay.notification.type.NotificationType;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Feliphe Marinho
 */
public class NotificationUtil {

	public static NotificationContext toNotificationContext(
		com.liferay.notification.rest.dto.v1_0.NotificationTemplate
			notificationTemplate,
		ObjectFieldLocalService objectFieldLocalService) {

		NotificationContext notificationContext = new NotificationContext();

		List<Long> attachmentObjectFieldIds = new ArrayList<>();

		String[] attachmentObjectFieldExternalReferenceCodes =
			notificationTemplate.
				getAttachmentObjectFieldExternalReferenceCodes();

		if (attachmentObjectFieldExternalReferenceCodes != null) {
			for (String attachmentObjectFieldExternalReferenceCode :
					attachmentObjectFieldExternalReferenceCodes) {

				ObjectField objectField =
					objectFieldLocalService.fetchObjectField(
						attachmentObjectFieldExternalReferenceCode,
						notificationTemplate.getObjectDefinitionId());

				if (objectField == null) {
					attachmentObjectFieldIds.clear();

					break;
				}

				attachmentObjectFieldIds.add(objectField.getObjectFieldId());
			}
		}

		if (attachmentObjectFieldIds.isEmpty()) {
			notificationContext.setAttachmentObjectFieldIds(
				ListUtil.fromArray(
					notificationTemplate.getAttachmentObjectFieldIds()));
		}
		else {
			notificationContext.setAttachmentObjectFieldIds(
				attachmentObjectFieldIds);
		}

		notificationContext.setType(notificationTemplate.getType());

		return notificationContext;
	}

	public static NotificationRecipient toNotificationRecipient(
		User user, long classPK) {

		NotificationRecipient notificationRecipient =
			NotificationRecipientLocalServiceUtil.
				fetchNotificationRecipientByClassPK(classPK);

		if (notificationRecipient != null) {
			return notificationRecipient;
		}

		notificationRecipient =
			NotificationRecipientLocalServiceUtil.createNotificationRecipient(
				CounterLocalServiceUtil.increment());

		notificationRecipient.setCompanyId(user.getCompanyId());
		notificationRecipient.setUserId(user.getUserId());
		notificationRecipient.setUserName(user.getFullName());
		notificationRecipient.setClassPK(classPK);

		return notificationRecipient;
	}

	public static List<NotificationRecipientSetting>
		toNotificationRecipientSetting(
			long notificationRecipientId, NotificationType notificationType,
			Object[] recipients, User user) {

		return notificationType.createNotificationRecipientSettings(
			notificationRecipientId, recipients, user);
	}

	public static NotificationTemplate toNotificationTemplate(
		long notificationTemplateId,
		com.liferay.notification.rest.dto.v1_0.NotificationTemplate
			notificationTemplate,
		ObjectDefinitionLocalService objectDefinitionLocalService, User user) {

		NotificationTemplate serviceBuilderNotificationTemplate =
			NotificationTemplateLocalServiceUtil.fetchNotificationTemplate(
				notificationTemplateId);

		if (serviceBuilderNotificationTemplate == null) {
			serviceBuilderNotificationTemplate =
				NotificationTemplateLocalServiceUtil.createNotificationTemplate(
					0L);
		}

		serviceBuilderNotificationTemplate.setExternalReferenceCode(
			notificationTemplate.getExternalReferenceCode());
		serviceBuilderNotificationTemplate.setCompanyId(user.getCompanyId());
		serviceBuilderNotificationTemplate.setUserId(user.getUserId());
		serviceBuilderNotificationTemplate.setUserName(user.getFullName());

		long objectDefinitionId = GetterUtil.getLong(
			notificationTemplate.getObjectDefinitionId());

		String objectDefinitionExternalReferenceCode =
			notificationTemplate.getObjectDefinitionExternalReferenceCode();

		if (Validator.isNotNull(objectDefinitionExternalReferenceCode)) {
			try {
				ObjectDefinition objectDefinition =
					objectDefinitionLocalService.
						getObjectDefinitionByExternalReferenceCode(
							objectDefinitionExternalReferenceCode,
							user.getCompanyId());

				objectDefinitionId = objectDefinition.getObjectDefinitionId();
			}
			catch (PortalException portalException) {
				_log.error(portalException);
			}
		}

		serviceBuilderNotificationTemplate.setObjectDefinitionId(
			objectDefinitionId);

		serviceBuilderNotificationTemplate.setBodyMap(
			LocalizedMapUtil.getLocalizedMap(notificationTemplate.getBody()));
		serviceBuilderNotificationTemplate.setDescription(
			notificationTemplate.getDescription());
		serviceBuilderNotificationTemplate.setEditorType(
			GetterUtil.getString(notificationTemplate.getEditorTypeAsString()));
		serviceBuilderNotificationTemplate.setName(
			notificationTemplate.getName());
		serviceBuilderNotificationTemplate.setRecipientType(
			notificationTemplate.getRecipientType());
		serviceBuilderNotificationTemplate.setSubjectMap(
			LocalizedMapUtil.getLocalizedMap(
				notificationTemplate.getSubject()));
		serviceBuilderNotificationTemplate.setSystem(
			GetterUtil.getBoolean(notificationTemplate.getSystem()));
		serviceBuilderNotificationTemplate.setType(
			notificationTemplate.getType());

		return serviceBuilderNotificationTemplate;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		NotificationUtil.class);

}
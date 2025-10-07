/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.action.executor;

import com.liferay.notification.context.NotificationContextBuilder;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.notification.type.NotificationType;
import com.liferay.notification.type.NotificationTypeServiceTracker;
import com.liferay.object.action.executor.ObjectActionExecutor;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.internal.action.util.ObjectEntryVariablesUtil;
import com.liferay.object.internal.notification.term.evaluator.util.ObjectDefinitionNotificationTermEvaluatorUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gabriel Albuquerque
 */
@Component(service = ObjectActionExecutor.class)
public class NotificationTemplateObjectActionExecutorImpl
	implements ObjectActionExecutor {

	@Override
	public void execute(
			long companyId, long objectActionId,
			UnicodeProperties parametersUnicodeProperties,
			JSONObject payloadJSONObject, long userId)
		throws Exception {

		NotificationTemplate notificationTemplate =
			_notificationTemplateLocalService.getNotificationTemplate(
				GetterUtil.getLong(
					parametersUnicodeProperties.get("notificationTemplateId")));

		NotificationType notificationType =
			_notificationTypeServiceTracker.getNotificationType(
				notificationTemplate.getType());

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				payloadJSONObject.getLong("objectDefinitionId"));

		Map<String, Object> termValues = _getTermValues(
			objectDefinition,
			ObjectEntryVariablesUtil.getVariables(
				_dtoConverterRegistry, objectDefinition, payloadJSONObject,
				_systemObjectDefinitionManagerRegistry));

		notificationType.sendNotification(
			new NotificationContextBuilder(
			).className(
				objectDefinition.getClassName()
			).classPK(
				GetterUtil.getLong(termValues.get("id"))
			).companyId(
				objectDefinition.getCompanyId()
			).externalReferenceCode(
				GetterUtil.getString(termValues.get("externalReferenceCode"))
			).groupId(
				GetterUtil.getLong(termValues.get("groupId"))
			).notificationTemplate(
				notificationTemplate
			).termValues(
				termValues
			).userId(
				userId
			).portletId(
				objectDefinition.isUnmodifiableSystemObject() ?
					StringPool.BLANK : objectDefinition.getPortletId()
			).preferredLanguageId(
				payloadJSONObject.getString("preferredLanguageId")
			).usePreferredLanguageForGuests(
				GetterUtil.getBoolean(
					parametersUnicodeProperties.get(
						"usePreferredLanguageForGuests"))
			).build());
	}

	@Override
	public String getKey() {
		return ObjectActionExecutorConstants.KEY_NOTIFICATION;
	}

	private Map<String, Object> _getTermValues(
		ObjectDefinition objectDefinition, Map<String, Object> variables) {

		Map<String, Object> termValues = (Map<String, Object>)variables.get(
			"baseModel");

		termValues.put(
			"objectDefinitionId", objectDefinition.getObjectDefinitionId());

		for (ObjectField objectField :
				_objectFieldLocalService.getObjectFields(
					objectDefinition.getObjectDefinitionId())) {

			if (termValues.get(objectField.getName()) == null) {
				continue;
			}

			Object termValue = termValues.get(objectField.getName());

			if (Validator.isNull(termValue) && objectField.isLocalized() &&
				termValues.containsKey("entryDTO")) {

				Map<String, Object> entryDTO =
					(Map<String, Object>)termValues.get("entryDTO");

				if (entryDTO.containsKey("properties")) {
					Map<String, Object> properties =
						(Map<String, Object>)entryDTO.get("properties");

					termValue = properties.get(objectField.getName());
				}
			}

			termValues.put(
				objectField.getName(),
				ObjectDefinitionNotificationTermEvaluatorUtil.getTermValue(
					objectField, termValue));
		}

		return termValues;
	}

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private NotificationTemplateLocalService _notificationTemplateLocalService;

	@Reference
	private NotificationTypeServiceTracker _notificationTypeServiceTracker;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.dto.v1_0.util;

import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.object.admin.rest.dto.v1_0.ObjectAction;
import com.liferay.object.admin.rest.dto.v1_0.Status;
import com.liferay.object.constants.ObjectActionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Gabriel Albuquerque
 */
public class ObjectActionUtil {

	public static ObjectAction toObjectAction(
		Map<String, Map<String, String>> actions, Locale locale,
		NotificationTemplateLocalService notificationTemplateLocalService,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		com.liferay.object.model.ObjectAction serviceBuilderObjectAction) {

		if (serviceBuilderObjectAction == null) {
			return null;
		}

		ObjectAction objectAction = new ObjectAction() {
			{
				setActive(serviceBuilderObjectAction::isActive);
				setConditionExpression(
					serviceBuilderObjectAction::getConditionExpression);
				setDateCreated(serviceBuilderObjectAction::getCreateDate);
				setDateModified(serviceBuilderObjectAction::getModifiedDate);
				setDescription(
					() -> LocalizedMapUtil.getLanguageIdMap(
						serviceBuilderObjectAction.getDescriptionMap()));
				setErrorMessage(
					() -> LocalizedMapUtil.getLanguageIdMap(
						serviceBuilderObjectAction.getErrorMessageMap()));
				setExternalReferenceCode(
					serviceBuilderObjectAction::getExternalReferenceCode);
				setId(serviceBuilderObjectAction::getObjectActionId);
				setLabel(
					() -> LocalizedMapUtil.getLanguageIdMap(
						serviceBuilderObjectAction.getLabelMap()));
				setName(serviceBuilderObjectAction::getName);
				setObjectActionExecutorKey(
					serviceBuilderObjectAction::getObjectActionExecutorKey);
				setObjectActionTriggerKey(
					serviceBuilderObjectAction::getObjectActionTriggerKey);
				setParameters(
					() -> toParameters(
						notificationTemplateLocalService,
						objectDefinitionLocalService,
						serviceBuilderObjectAction.
							getParametersUnicodeProperties()));
				setStatus(
					() -> new Status() {
						{
							setCode(serviceBuilderObjectAction::getStatus);
							setLabel(
								() -> ObjectActionConstants.getStatusLabel(
									serviceBuilderObjectAction.getStatus()));
							setLabel_i18n(
								() -> LanguageUtil.get(
									locale,
									ObjectActionConstants.getStatusLabel(
										serviceBuilderObjectAction.
											getStatus())));
						}
					});
				setSystem(serviceBuilderObjectAction::isSystem);
			}
		};

		objectAction.setActions(() -> actions);

		return objectAction;
	}

	public static Map<String, Object> toParameters(
		NotificationTemplateLocalService notificationTemplateLocalService,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		UnicodeProperties parametersUnicodeProperties) {

		Map<String, Object> parameters = new HashMap<>();

		for (Map.Entry<String, String> entry :
				parametersUnicodeProperties.entrySet()) {

			Object value = entry.getValue();

			if (Objects.equals(entry.getKey(), "notificationTemplateId")) {
				try {
					NotificationTemplate notificationTemplate =
						notificationTemplateLocalService.
							getNotificationTemplate(GetterUtil.getLong(value));

					parameters.put(
						"notificationTemplateExternalReferenceCode",
						notificationTemplate.getExternalReferenceCode());
					parameters.put("type", notificationTemplate.getType());
				}
				catch (PortalException portalException) {
					_log.error(portalException);
				}

				value = GetterUtil.getLong(value);
			}
			else if (Objects.equals(entry.getKey(), "objectDefinitionId")) {
				try {
					ObjectDefinition objectDefinition =
						objectDefinitionLocalService.getObjectDefinition(
							GetterUtil.getLong(value));

					parameters.put(
						"objectDefinitionExternalReferenceCode",
						objectDefinition.getExternalReferenceCode());
				}
				catch (PortalException portalException) {
					_log.error(portalException);
				}

				value = GetterUtil.getLong(value);
			}
			else if (Objects.equals(entry.getKey(), "predefinedValues")) {
				value = JSONFactoryUtil.looseDeserialize((String)value);
			}
			else if (Objects.equals(entry.getKey(), "relatedObjectEntries")) {
				value = GetterUtil.getBoolean(value);
			}
			else if (Objects.equals(
						entry.getKey(), "usePreferredLanguageForGuests")) {

				value = GetterUtil.getBoolean(value);
			}

			parameters.put(entry.getKey(), value);
		}

		return parameters;
	}

	public static UnicodeProperties toParametersUnicodeProperties(
		Map<String, ?> parameters) {

		Map<String, String> map = new HashMap<>();

		for (Map.Entry<String, ?> entry : parameters.entrySet()) {
			Object value = entry.getValue();

			if (value instanceof ArrayList || value instanceof Object[]) {
				value = JSONFactoryUtil.looseSerialize(value);
			}

			map.put(entry.getKey(), value.toString());
		}

		return UnicodePropertiesBuilder.create(
			map, true
		).build();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectActionUtil.class);

}
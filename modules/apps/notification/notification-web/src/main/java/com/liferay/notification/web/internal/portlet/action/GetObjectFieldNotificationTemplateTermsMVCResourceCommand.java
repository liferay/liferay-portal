/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.web.internal.portlet.action;

import com.liferay.notification.constants.NotificationPortletKeys;
import com.liferay.notification.term.provider.NotificationTermProvider;
import com.liferay.notification.term.provider.NotificationTermProviderRegistry;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Paulo Albuquerque
 */
@Component(
	property = {
		"jakarta.portlet.name=" + NotificationPortletKeys.NOTIFICATION_TEMPLATES,
		"mvc.command.name=/notification_templates/get_object_field_notification_template_terms"
	},
	service = MVCResourceCommand.class
)
public class GetObjectFieldNotificationTemplateTermsMVCResourceCommand
	extends BaseNotificationTemplateTermsMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				ParamUtil.getLong(resourceRequest, "objectDefinitionId"));

		if (objectDefinition == null) {
			return;
		}

		JSONArray relationshipSectionsJSONArray = jsonFactory.createJSONArray();
		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		for (ObjectRelationship objectRelationship :
				_objectRelationshipLocalService.
					getObjectRelationshipsByObjectDefinitionId2(
						objectDefinition.getObjectDefinitionId())) {

			if (!Objects.equals(
					objectRelationship.getType(),
					ObjectRelationshipConstants.TYPE_ONE_TO_MANY)) {

				continue;
			}

			relationshipSectionsJSONArray.put(
				JSONUtil.put(
					"objectRelationshipId",
					objectRelationship.getObjectRelationshipId()
				).put(
					"sectionLabel",
					() -> {
						ObjectDefinition relatedObjectDefinition =
							_objectDefinitionLocalService.getObjectDefinition(
								objectRelationship.getObjectDefinitionId1());

						return StringBundler.concat(
							objectRelationship.getLabel(
								themeDisplay.getLocale()),
							" (",
							StringUtil.upperCase(
								relatedObjectDefinition.getLabel(
									themeDisplay.getLocale())),
							StringPool.CLOSE_PARENTHESIS);
					}
				));
		}

		JSONArray termsJSONArray = getTermsJSONArray(
			_objectFieldLocalService.getObjectFields(
				objectDefinition.getObjectDefinitionId()),
			objectDefinition.getShortName(), themeDisplay);

		for (NotificationTermProvider notificationTermProvider :
				_notificationTermProviderRegistry.getNotificationTermProviders(
					objectDefinition.getClassName())) {

			Map<String, String> notificationTerms =
				notificationTermProvider.getNotificationTerms();

			for (Map.Entry<String, String> entry :
					notificationTerms.entrySet()) {

				termsJSONArray.put(
					JSONUtil.put(
						"termLabel",
						language.get(themeDisplay.getLocale(), entry.getKey())
					).put(
						"termName", entry.getValue()
					));
			}
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"relationshipSections", relationshipSectionsJSONArray
			).put(
				"terms", termsJSONArray
			));
	}

	@Reference
	private NotificationTermProviderRegistry _notificationTermProviderRegistry;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}
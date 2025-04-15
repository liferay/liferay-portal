/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.web.internal.portlet.action;

import com.liferay.notification.constants.NotificationPortletKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Paulo Albuquerque
 */
@Component(
	property = {
		"jakarta.portlet.name=" + NotificationPortletKeys.NOTIFICATION_TEMPLATES,
		"mvc.command.name=/notification_templates/get_parent_object_field_notification_template_terms"
	},
	service = MVCResourceCommand.class
)
public class GetParentObjectFieldNotificationTemplateTermsMVCResourceCommand
	extends BaseNotificationTemplateTermsMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.fetchObjectRelationship(
				ParamUtil.getLong(resourceRequest, "objectRelationshipId"));

		if (objectRelationship == null) {
			return;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectRelationship.getObjectDefinitionId1());

		if (objectDefinition == null) {
			return;
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			getTermsJSONArray(
				_objectFieldLocalService.getObjectFields(
					objectDefinition.getObjectDefinitionId()),
				ObjectRelationshipUtil.getNotificationTermNamePrefix(
					objectDefinition, objectRelationship),
				(ThemeDisplay)resourceRequest.getAttribute(
					WebKeys.THEME_DISPLAY)));
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}
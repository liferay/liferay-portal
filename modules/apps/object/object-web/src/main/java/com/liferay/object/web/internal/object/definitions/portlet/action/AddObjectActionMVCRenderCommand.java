/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.portlet.action;

import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.object.action.executor.ObjectActionExecutorRegistry;
import com.liferay.object.action.trigger.ObjectActionTriggerRegistry;
import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.constants.ObjectWebKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.web.internal.object.definitions.display.context.ObjectDefinitionsActionsDisplayContext;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.script.management.configuration.helper.ScriptManagementConfigurationHelper;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Milton Castro
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ObjectPortletKeys.OBJECT_DEFINITIONS,
		"mvc.command.name=/object_definitions/add_object_action"
	},
	service = MVCRenderCommand.class
)
public class AddObjectActionMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			ObjectDefinition objectDefinition =
				_objectDefinitionService.getObjectDefinition(
					ParamUtil.getLong(renderRequest, "objectDefinitionId"));

			renderRequest.setAttribute(
				ObjectWebKeys.OBJECT_DEFINITION, objectDefinition);

			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				new ObjectDefinitionsActionsDisplayContext(
					_portal.getHttpServletRequest(renderRequest), _jsonFactory,
					_notificationTemplateLocalService,
					_objectActionExecutorRegistry, _objectActionTriggerRegistry,
					_objectDefinitionLocalService,
					_objectDefinitionModelResourcePermission,
					_objectFieldLocalService, _objectFolderLocalService,
					_scriptManagementConfigurationHelper));
		}
		catch (PortalException portalException) {
			SessionErrors.add(renderRequest, portalException.getClass());
		}

		return "/object_definitions/add_object_action.jsp";
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private NotificationTemplateLocalService _notificationTemplateLocalService;

	@Reference
	private ObjectActionExecutorRegistry _objectActionExecutorRegistry;

	@Reference
	private ObjectActionTriggerRegistry _objectActionTriggerRegistry;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.object.model.ObjectDefinition)"
	)
	private ModelResourcePermission<ObjectDefinition>
		_objectDefinitionModelResourcePermission;

	@Reference
	private ObjectDefinitionService _objectDefinitionService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectFolderLocalService _objectFolderLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private ScriptManagementConfigurationHelper
		_scriptManagementConfigurationHelper;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.frontend.taglib.servlet.taglib;

import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.object.action.executor.ObjectActionExecutorRegistry;
import com.liferay.object.action.trigger.ObjectActionTriggerRegistry;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.web.internal.object.definitions.constants.ObjectDefinitionsScreenNavigationEntryConstants;
import com.liferay.object.web.internal.object.definitions.display.context.ObjectDefinitionsActionsDisplayContext;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.script.management.configuration.helper.ScriptManagementConfigurationHelper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Renan Vasconcelos
 */
@Component(
	property = "screen.navigation.entry.order:Integer=10",
	service = ScreenNavigationEntry.class
)
public class ActionsObjectDefinitionsScreenNavigationEntry
	extends BaseObjectDefinitionsScreenNavigationEntry {

	@Override
	public String getCategoryKey() {
		return ObjectDefinitionsScreenNavigationEntryConstants.
			CATEGORY_KEY_ACTIONS;
	}

	@Override
	public String getJspPath() {
		return "/object_definitions/object_definition/actions.jsp";
	}

	@Override
	public boolean isVisible(User user, ObjectDefinition objectDefinition) {
		return objectDefinition.isDefaultStorageType();
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		httpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			new ObjectDefinitionsActionsDisplayContext(
				httpServletRequest, _jsonFactory,
				_notificationTemplateLocalService,
				_objectActionExecutorRegistry, _objectActionTriggerRegistry,
				_objectDefinitionLocalService,
				_objectDefinitionModelResourcePermission,
				_objectFieldLocalService, _objectFolderLocalService,
				_scriptManagementConfigurationHelper));

		super.render(httpServletRequest, httpServletResponse);
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
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectFolderLocalService _objectFolderLocalService;

	@Reference
	private ScriptManagementConfigurationHelper
		_scriptManagementConfigurationHelper;

}
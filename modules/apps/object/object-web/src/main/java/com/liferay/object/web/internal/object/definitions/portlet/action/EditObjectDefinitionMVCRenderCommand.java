/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.portlet.action;

import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.constants.ObjectWebKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.web.internal.object.definitions.display.context.ObjectDefinitionsDetailsDisplayContext;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Gabriel Albuquerque
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ObjectPortletKeys.OBJECT_DEFINITIONS,
		"mvc.command.name=/object_definitions/edit_object_definition"
	},
	service = MVCRenderCommand.class
)
public class EditObjectDefinitionMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		long objectDefinitionId = ParamUtil.getLong(
			renderRequest, "objectDefinitionId");

		try {
			renderRequest.setAttribute(
				ObjectWebKeys.OBJECT_DEFINITION,
				_objectDefinitionLocalService.getObjectDefinition(
					objectDefinitionId));
			renderRequest.setAttribute(
				ObjectWebKeys.OBJECT_FIELDS,
				_objectFieldLocalService.getObjectFields(objectDefinitionId));
			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				new ObjectDefinitionsDetailsDisplayContext(
					_configurationProvider,
					_portal.getHttpServletRequest(renderRequest),
					_objectDefinitionModelResourcePermission,
					_objectEntryManagerRegistry, _objectFolderLocalService,
					null));
		}
		catch (PortalException portalException) {
			SessionErrors.add(renderRequest, portalException.getClass());
		}

		return "/object_definitions/edit_object_definition.jsp";
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.object.model.ObjectDefinition)"
	)
	private ModelResourcePermission<ObjectDefinition>
		_objectDefinitionModelResourcePermission;

	@Reference
	private ObjectEntryManagerRegistry _objectEntryManagerRegistry;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectFolderLocalService _objectFolderLocalService;

	@Reference
	private Portal _portal;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.portlet.action;

import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.constants.ObjectWebKeys;
import com.liferay.object.field.business.type.ObjectFieldBusinessTypeRegistry;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectLayout;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectLayoutLocalService;
import com.liferay.object.web.internal.object.definitions.display.context.ObjectDefinitionsLayoutsDisplayContext;
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
 * @author Gabriel Albuquerque
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ObjectPortletKeys.OBJECT_DEFINITIONS,
		"mvc.command.name=/object_definitions/edit_object_layout"
	},
	service = MVCRenderCommand.class
)
public class EditObjectLayoutMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			ObjectLayout objectLayout =
				_objectLayoutLocalService.getObjectLayout(
					ParamUtil.getLong(renderRequest, "objectLayoutId"));

			renderRequest.setAttribute(
				ObjectWebKeys.OBJECT_DEFINITION,
				_objectDefinitionLocalService.getObjectDefinition(
					objectLayout.getObjectDefinitionId()));
			renderRequest.setAttribute(
				ObjectWebKeys.OBJECT_LAYOUT, objectLayout);

			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				new ObjectDefinitionsLayoutsDisplayContext(
					_portal.getHttpServletRequest(renderRequest),
					_objectDefinitionModelResourcePermission,
					_objectFieldBusinessTypeRegistry,
					_objectFolderLocalService));
		}
		catch (PortalException portalException) {
			SessionErrors.add(renderRequest, portalException.getClass());
		}

		return "/object_definitions/edit_object_layout.jsp";
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.object.model.ObjectDefinition)"
	)
	private ModelResourcePermission<ObjectDefinition>
		_objectDefinitionModelResourcePermission;

	@Reference
	private ObjectFieldBusinessTypeRegistry _objectFieldBusinessTypeRegistry;

	@Reference
	private ObjectFolderLocalService _objectFolderLocalService;

	@Reference
	private ObjectLayoutLocalService _objectLayoutLocalService;

	@Reference
	private Portal _portal;

}
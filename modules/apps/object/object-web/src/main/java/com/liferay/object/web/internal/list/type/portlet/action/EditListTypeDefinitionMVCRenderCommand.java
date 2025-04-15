/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.list.type.portlet.action;

import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.constants.ObjectWebKeys;
import com.liferay.object.web.internal.list.type.display.context.ViewListTypeEntriesDisplayContext;
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
		"jakarta.portlet.name=" + ObjectPortletKeys.LIST_TYPE_DEFINITIONS,
		"mvc.command.name=/list_type_definitions/edit_list_type_definition"
	},
	service = MVCRenderCommand.class
)
public class EditListTypeDefinitionMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			renderRequest.setAttribute(
				ObjectWebKeys.LIST_TYPE_DEFINITION,
				_listTypeDefinitionLocalService.getListTypeDefinition(
					ParamUtil.getLong(renderRequest, "listTypeDefinitionId")));
		}
		catch (PortalException portalException) {
			SessionErrors.add(renderRequest, portalException.getClass());
		}

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			new ViewListTypeEntriesDisplayContext(
				_portal.getHttpServletRequest(renderRequest),
				_listTypeDefinitionModelResourcePermission));

		return "/list_type_definitions/edit_list_type_definition.jsp";
	}

	@Reference
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.list.type.model.ListTypeDefinition)"
	)
	private ModelResourcePermission<ListTypeDefinition>
		_listTypeDefinitionModelResourcePermission;

	@Reference
	private Portal _portal;

}
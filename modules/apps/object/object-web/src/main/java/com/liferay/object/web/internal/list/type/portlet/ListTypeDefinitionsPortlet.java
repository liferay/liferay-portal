/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.list.type.portlet;

import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.web.internal.list.type.display.context.ViewListTypeDefinitionsDisplayContext;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gabriel Albuquerque
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-objects",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.preferences-unique-per-layout=false",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.scopeable=false",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Picklists",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.portlet-title-based-navigation=true",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/list_type_definitions/view.jsp",
		"jakarta.portlet.name=" + ObjectPortletKeys.LIST_TYPE_DEFINITIONS,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class ListTypeDefinitionsPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			new ViewListTypeDefinitionsDisplayContext(
				_portal.getHttpServletRequest(renderRequest),
				_listTypeDefinitionModelResourcePermission));

		super.render(renderRequest, renderResponse);
	}

	@Reference(
		target = "(model.class.name=com.liferay.list.type.model.ListTypeDefinition)"
	)
	private ModelResourcePermission<ListTypeDefinition>
		_listTypeDefinitionModelResourcePermission;

	@Reference
	private Portal _portal;

}
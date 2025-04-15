/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.web.internal.blueprint.admin.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.search.experiences.constants.SXPPortletKeys;
import com.liferay.search.experiences.model.SXPElement;
import com.liferay.search.experiences.web.internal.blueprint.admin.display.context.ViewSXPElementsDisplayContext;
import com.liferay.search.experiences.web.internal.constants.SXPWebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Kevin Tan
 */
@Component(
	enabled = false,
	property = {
		"jakarta.portlet.name=" + SXPPortletKeys.SXP_BLUEPRINT_ADMIN,
		"mvc.command.name=/sxp_blueprint_admin/view_sxp_elements"
	},
	service = MVCRenderCommand.class
)
public class ViewSXPElementsMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		renderRequest.setAttribute(
			SXPWebKeys.VIEW_SXP_ELEMENTS_DISPLAY_CONTEXT,
			new ViewSXPElementsDisplayContext(
				_portal.getHttpServletRequest(renderRequest),
				_sxpElementModelResourcePermission));

		return "/sxp_blueprint_admin/view.jsp";
	}

	@Reference
	private Portal _portal;

	@Reference(
		target = "(model.class.name=com.liferay.search.experiences.model.SXPElement)"
	)
	private ModelResourcePermission<SXPElement>
		_sxpElementModelResourcePermission;

}
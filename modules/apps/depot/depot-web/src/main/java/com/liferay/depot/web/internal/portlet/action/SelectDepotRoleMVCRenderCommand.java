/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.portlet.action;

import com.liferay.depot.web.internal.constants.DepotAdminWebKeys;
import com.liferay.depot.web.internal.constants.DepotPortletKeys;
import com.liferay.depot.web.internal.display.context.DepotAdminSelectRoleDisplayContext;
import com.liferay.depot.web.internal.display.context.DepotAdminSelectRoleManagementToolbarDisplayContext;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DepotPortletKeys.DEPOT_ADMIN,
		"mvc.command.name=/depot/select_depot_role"
	},
	service = MVCRenderCommand.class
)
public class SelectDepotRoleMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			DepotAdminSelectRoleDisplayContext
				depotAdminSelectRoleDisplayContext =
					new DepotAdminSelectRoleDisplayContext(
						renderRequest, renderResponse);

			renderRequest.setAttribute(
				DepotAdminWebKeys.DEPOT_ADMIN_SELECT_ROLE_DISPLAY_CONTEXT,
				depotAdminSelectRoleDisplayContext);

			DepotAdminSelectRoleDisplayContext.Step step =
				depotAdminSelectRoleDisplayContext.getStep();

			renderRequest.setAttribute(
				DepotAdminWebKeys.
					DEPOT_ADMIN_SELECT_ROLE_MANAGEMENT_TOOLBAL_DISPLAY_CONTEXT,
				new DepotAdminSelectRoleManagementToolbarDisplayContext(
					_portal.getHttpServletRequest(renderRequest),
					_portal.getLiferayPortletRequest(renderRequest),
					_portal.getLiferayPortletResponse(renderResponse),
					step.getSearchContainer()));

			return "/select_depot_role.jsp";
		}
		catch (PortalException portalException) {
			throw new PortletException(portalException);
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	@Reference
	private Portal _portal;

}
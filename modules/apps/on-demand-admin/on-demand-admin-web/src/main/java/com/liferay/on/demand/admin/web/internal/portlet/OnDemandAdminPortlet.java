/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.on.demand.admin.web.internal.portlet;

import com.liferay.on.demand.admin.constants.OnDemandAdminPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=On-Demand Admin",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + OnDemandAdminPortletKeys.ON_DEMAND_ADMIN,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class OnDemandAdminPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		_checkPermission(renderRequest);

		super.render(renderRequest, renderResponse);
	}

	@Override
	public void serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException, PortletException {

		_checkPermission(resourceRequest);

		super.serveResource(resourceRequest, resourceResponse);
	}

	private void _checkPermission(PortletRequest portletRequest)
		throws PortletException {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			PermissionChecker permissionChecker =
				themeDisplay.getPermissionChecker();

			if (!PortletPermissionUtil.hasControlPanelAccessPermission(
					permissionChecker, themeDisplay.getScopeGroupId(),
					OnDemandAdminPortletKeys.ON_DEMAND_ADMIN)) {

				throw new PrincipalException.MustHavePermission(
					permissionChecker, OnDemandAdminPortletKeys.ON_DEMAND_ADMIN,
					0, ActionKeys.ACCESS_IN_CONTROL_PANEL);
			}
		}
		catch (PortalException portalException) {
			throw new PortletException(portalException);
		}
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.admin.web.internal.portlet.action;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.site.navigation.admin.constants.SiteNavigationAdminPortletKeys;
import com.liferay.site.navigation.admin.web.internal.handler.SiteNavigationMenuExceptionRequestHandlerUtil;
import com.liferay.site.navigation.service.SiteNavigationMenuService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SiteNavigationAdminPortletKeys.SITE_NAVIGATION_ADMIN,
		"mvc.command.name=/site_navigation_admin/update_site_navigation_menu"
	},
	service = MVCActionCommand.class
)
public class UpdateSiteNavigationMenuMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long siteNavigationMenuId = ParamUtil.getLong(
			actionRequest, "siteNavigationMenuId");

		String name = ParamUtil.getString(actionRequest, "name");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		try {
			_siteNavigationMenuService.updateSiteNavigationMenu(
				siteNavigationMenuId, name, serviceContext);

			JSONObject jsonObject = JSONUtil.put(
				"redirectURL", _getRedirectURL(actionResponse));

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse, jsonObject);
		}
		catch (PortalException portalException) {
			hideDefaultSuccessMessage(actionRequest);

			SiteNavigationMenuExceptionRequestHandlerUtil.handlePortalException(
				actionRequest, actionResponse, portalException);
		}
	}

	private String _getRedirectURL(ActionResponse actionResponse) {
		return PortletURLBuilder.createRenderURL(
			_portal.getLiferayPortletResponse(actionResponse)
		).setMVCPath(
			"/view.jsp"
		).buildString();
	}

	@Reference
	private Portal _portal;

	@Reference
	private SiteNavigationMenuService _siteNavigationMenuService;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.admin.web.internal.portlet.action;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.site.navigation.admin.constants.SiteNavigationAdminPortletKeys;
import com.liferay.site.navigation.menu.item.util.SiteNavigationMenuItemUtil;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SiteNavigationAdminPortletKeys.SITE_NAVIGATION_ADMIN,
		"mvc.command.name=/site_navigation_admin/edit_site_navigation_menu_item"
	},
	service = MVCActionCommand.class
)
public class EditSiteNavigationMenuItemMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long siteNavigationMenuItemId = ParamUtil.getLong(
			actionRequest, "siteNavigationMenuItemId");

		UnicodeProperties typeSettingsUnicodeProperties =
			SiteNavigationMenuItemUtil.getSiteNavigationMenuItemProperties(
				actionRequest, "TypeSettingsProperties--");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			SiteNavigationMenuItem.class.getName(), actionRequest);

		try {
			_siteNavigationMenuItemService.updateSiteNavigationMenuItem(
				siteNavigationMenuItemId,
				typeSettingsUnicodeProperties.toString(), serviceContext);
		}
		catch (PortalException portalException) {
			hideDefaultErrorMessage(actionRequest);

			SessionErrors.add(
				actionRequest, portalException.getClass(), portalException);

			sendRedirect(actionRequest, actionResponse);
		}
	}

	@Reference
	private SiteNavigationMenuItemService _siteNavigationMenuItemService;

}
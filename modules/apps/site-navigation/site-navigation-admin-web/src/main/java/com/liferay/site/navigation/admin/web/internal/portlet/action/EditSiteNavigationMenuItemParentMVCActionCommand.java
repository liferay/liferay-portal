/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.admin.web.internal.portlet.action;

import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.navigation.admin.constants.SiteNavigationAdminPortletKeys;
import com.liferay.site.navigation.admin.web.internal.util.SiteNavigationMenuPortletUtil;
import com.liferay.site.navigation.exception.InvalidSiteNavigationMenuItemOrderException;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemService;
import com.liferay.site.navigation.type.SiteNavigationMenuItemTypeRegistry;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SiteNavigationAdminPortletKeys.SITE_NAVIGATION_ADMIN,
		"mvc.command.name=/site_navigation_admin/edit_site_navigation_menu_item_parent"
	},
	service = MVCActionCommand.class
)
public class EditSiteNavigationMenuItemParentMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long siteNavigationMenuItemId = ParamUtil.getLong(
			actionRequest, "siteNavigationMenuItemId");

		long parentSiteNavigationMenuItemId = ParamUtil.getLong(
			actionRequest, "parentSiteNavigationMenuItemId");
		int order = ParamUtil.getInteger(actionRequest, "order");

		try {
			SiteNavigationMenuItem siteNavigationMenuItem =
				_siteNavigationMenuItemService.updateSiteNavigationMenuItem(
					siteNavigationMenuItemId, parentSiteNavigationMenuItemId,
					order);

			jsonObject.put(
				"siteNavigationMenuItems",
				SiteNavigationMenuPortletUtil.
					getSiteNavigationMenuItemsJSONArray(
						0, siteNavigationMenuItem.getSiteNavigationMenuId(),
						_siteNavigationMenuItemTypeRegistry, themeDisplay));
		}
		catch (InvalidSiteNavigationMenuItemOrderException
					invalidSiteNavigationMenuItemOrderException) {

			Class<?> exceptionClass =
				invalidSiteNavigationMenuItemOrderException.getClass();

			jsonObject.put(
				"error",
				_language.get(
					themeDisplay.getRequest(), exceptionClass.getName()));
		}
		catch (Exception exception) {
			_log.error(exception);

			jsonObject.put(
				"error",
				_language.get(
					themeDisplay.getRequest(), "an-unexpected-error-occurred"));
		}

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse, jsonObject);

		hideDefaultSuccessMessage(actionRequest);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditSiteNavigationMenuItemParentMVCActionCommand.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private SiteNavigationMenuItemService _siteNavigationMenuItemService;

	@Reference
	private SiteNavigationMenuItemTypeRegistry
		_siteNavigationMenuItemTypeRegistry;

}
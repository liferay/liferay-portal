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
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.navigation.admin.constants.SiteNavigationAdminPortletKeys;
import com.liferay.site.navigation.exception.SiteNavigationMenuItemNameException;
import com.liferay.site.navigation.menu.item.util.SiteNavigationMenuItemUtil;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemService;
import com.liferay.site.navigation.type.SiteNavigationMenuItemType;
import com.liferay.site.navigation.type.SiteNavigationMenuItemTypeRegistry;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Arrays;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SiteNavigationAdminPortletKeys.SITE_NAVIGATION_ADMIN,
		"mvc.command.name=/site_navigation_admin/add_site_navigation_menu_item"
	},
	service = MVCActionCommand.class
)
public class AddSiteNavigationMenuItemMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long siteNavigationMenuId = ParamUtil.getLong(
			actionRequest, "siteNavigationMenuId");
		long parentSiteNavigationMenuItemId = ParamUtil.getLong(
			actionRequest, "parentSiteNavigationMenuItemId");

		String type = ParamUtil.getString(actionRequest, "type");

		UnicodeProperties typeSettingsUnicodeProperties =
			SiteNavigationMenuItemUtil.getSiteNavigationMenuItemProperties(
				actionRequest, "TypeSettingsProperties--");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		try {
			SiteNavigationMenuItem siteNavigationMenuItem =
				_siteNavigationMenuItemService.addSiteNavigationMenuItem(
					null, themeDisplay.getScopeGroupId(), siteNavigationMenuId,
					parentSiteNavigationMenuItemId, type,
					typeSettingsUnicodeProperties.toString(), serviceContext);

			int order = ParamUtil.getInteger(actionRequest, "order", -1);

			if (order >= 0) {
				_siteNavigationMenuItemService.updateSiteNavigationMenuItem(
					siteNavigationMenuItem.getSiteNavigationMenuItemId(),
					parentSiteNavigationMenuItemId, order);
			}

			jsonObject.put(
				"siteNavigationMenuItemId",
				siteNavigationMenuItem.getSiteNavigationMenuItemId());

			SiteNavigationMenuItemType siteNavigationMenuItemType =
				_siteNavigationMenuItemTypeRegistry.
					getSiteNavigationMenuItemType(type);

			SessionMessages.add(
				actionRequest, "siteNavigationMenuItemsAdded",
				_language.format(
					themeDisplay.getLocale(), "x-x-was-added-to-this-menu",
					Arrays.asList(
						1,
						siteNavigationMenuItemType.getLabel(
							themeDisplay.getLocale()))));
		}
		catch (SiteNavigationMenuItemNameException
					siteNavigationMenuItemNameException) {

			if (_log.isDebugEnabled()) {
				_log.debug(siteNavigationMenuItemNameException);
			}

			jsonObject.put(
				"errorMessage",
				_language.format(
					_portal.getHttpServletRequest(actionRequest),
					"please-enter-a-name-with-fewer-than-x-characters",
					ModelHintsUtil.getMaxLength(
						SiteNavigationMenuItem.class.getName(), "name")));
		}

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse, jsonObject);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddSiteNavigationMenuItemMVCActionCommand.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private SiteNavigationMenuItemService _siteNavigationMenuItemService;

	@Reference
	private SiteNavigationMenuItemTypeRegistry
		_siteNavigationMenuItemTypeRegistry;

}
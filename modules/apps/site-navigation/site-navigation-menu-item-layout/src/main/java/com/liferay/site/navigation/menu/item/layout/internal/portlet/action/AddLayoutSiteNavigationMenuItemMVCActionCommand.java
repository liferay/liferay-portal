/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.menu.item.layout.internal.portlet.action;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.navigation.admin.constants.SiteNavigationAdminPortletKeys;
import com.liferay.site.navigation.exception.SiteNavigationMenuItemNameException;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SiteNavigationAdminPortletKeys.SITE_NAVIGATION_ADMIN,
		"mvc.command.name=/navigation_menu/add_layout_site_navigation_menu_item"
	},
	service = MVCActionCommand.class
)
public class AddLayoutSiteNavigationMenuItemMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long siteNavigationMenuId = ParamUtil.getLong(
			actionRequest, "siteNavigationMenuId");
		String siteNavigationMenuItemType = ParamUtil.getString(
			actionRequest, "siteNavigationMenuItemType");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		Map<Long, SiteNavigationMenuItem> layoutSiteNavigationMenuItemMap =
			new LinkedHashMap<>();

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		try {
			JSONArray jsonArray = _jsonFactory.createJSONArray(
				ParamUtil.getString(actionRequest, "items"));

			Iterator<JSONObject> iterator = jsonArray.iterator();

			while (iterator.hasNext()) {
				JSONObject itemJSONObject = iterator.next();

				String layoutUuid = itemJSONObject.getString("id");
				long groupId = itemJSONObject.getLong("groupId");
				boolean privateLayout = itemJSONObject.getBoolean(
					"privateLayout");

				Layout layout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
					layoutUuid, groupId, privateLayout);

				if (layout == null) {
					continue;
				}

				long parentSiteNavigationMenuItemId = ParamUtil.getLong(
					actionRequest, "parentSiteNavigationMenuItemId");

				SiteNavigationMenuItem siteNavigationMenuItem =
					_siteNavigationMenuItemService.addSiteNavigationMenuItem(
						null, themeDisplay.getScopeGroupId(),
						siteNavigationMenuId, parentSiteNavigationMenuItemId,
						siteNavigationMenuItemType,
						UnicodePropertiesBuilder.create(
							true
						).put(
							"groupId", String.valueOf(groupId)
						).put(
							"layoutUuid", layoutUuid
						).put(
							"privateLayout", String.valueOf(privateLayout)
						).put(
							"title", layout.getName(themeDisplay.getLocale())
						).buildString(),
						serviceContext);

				layoutSiteNavigationMenuItemMap.put(
					layout.getPlid(), siteNavigationMenuItem);
			}

			int order = ParamUtil.getInteger(actionRequest, "order", -1);

			int nextOrder = order;

			for (Map.Entry<Long, SiteNavigationMenuItem> entry :
					layoutSiteNavigationMenuItemMap.entrySet()) {

				if (order < 0) {
					Layout layout = _layoutLocalService.fetchLayout(
						entry.getKey());

					if (layout.getParentPlid() <= 0) {
						continue;
					}

					SiteNavigationMenuItem parentSiteNavigationMenuItem =
						layoutSiteNavigationMenuItemMap.get(
							layout.getParentPlid());

					if (parentSiteNavigationMenuItem == null) {
						continue;
					}

					SiteNavigationMenuItem siteNavigationMenuItem =
						entry.getValue();

					_siteNavigationMenuItemService.updateSiteNavigationMenuItem(
						siteNavigationMenuItem.getSiteNavigationMenuItemId(),
						parentSiteNavigationMenuItem.
							getSiteNavigationMenuItemId(),
						layout.getPriority());
				}
				else {
					SiteNavigationMenuItem siteNavigationMenuItem =
						entry.getValue();

					_siteNavigationMenuItemService.updateSiteNavigationMenuItem(
						siteNavigationMenuItem.getSiteNavigationMenuItemId(),
						siteNavigationMenuItem.
							getParentSiteNavigationMenuItemId(),
						nextOrder);

					nextOrder++;
				}
			}

			if (MapUtil.isEmpty(layoutSiteNavigationMenuItemMap)) {
				jsonObject.put(
					"errorMessage",
					_language.get(
						_portal.getHttpServletRequest(actionRequest),
						"please-choose-at-least-one-page"));
			}
			else {
				jsonObject.put(
					"siteNavigationMenuItemId",
					layoutSiteNavigationMenuItemMap);

				String message = _language.format(
					themeDisplay.getLocale(), "x-x-was-added-to-this-menu",
					Arrays.asList(jsonArray.length(), "page"));

				if (jsonArray.length() > 1) {
					message = _language.format(
						themeDisplay.getLocale(), "x-x-were-added-to-this-menu",
						Arrays.asList(jsonArray.length(), "pages"));
				}

				SessionMessages.add(
					actionRequest, "siteNavigationMenuItemsAdded", message);
			}
		}
		catch (SiteNavigationMenuItemNameException
					siteNavigationMenuItemNameException) {

			if (_log.isDebugEnabled()) {
				_log.debug(siteNavigationMenuItemNameException);
			}

			jsonObject.put(
				"errorMessage",
				_language.get(
					_portal.getHttpServletRequest(actionRequest),
					"an-unexpected-error-occurred"));
		}

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse, jsonObject);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddLayoutSiteNavigationMenuItemMVCActionCommand.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private SiteNavigationMenuItemService _siteNavigationMenuItemService;

}
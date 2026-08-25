/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.product.menu.web.internal.portlet.action;

import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.PanelCategory;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.product.menu.constants.ProductNavigationProductMenuPortletKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mario Leandro
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ProductNavigationProductMenuPortletKeys.PRODUCT_NAVIGATION_PRODUCT_MENU,
		"mvc.command.name=/product_navigation_product_menu/get_navigation_items"
	},
	service = MVCResourceCommand.class
)
public class GetNavigationItemsMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Activate
	protected void activate() {
		_panelCategoryHelper = new PanelCategoryHelper(_panelAppRegistry);
	}

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"navigationItems",
				_getNavigationItemsJSONObject(resourceRequest)));
	}

	private JSONObject _getNavigationItemsJSONObject(
			ResourceRequest resourceRequest)
		throws Exception {

		JSONObject navigationItemsJSONObject = _jsonFactory.createJSONObject();

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		// Read the selected application from the request. In the resource phase
		// the theme display reports the product menu instead, since that is the
		// portlet serving this request.

		PanelCategory panelCategory =
			_panelCategoryHelper.getActivePanelCategory(
				PanelCategoryKeys.APPLICATIONS_MENU,
				ParamUtil.getString(resourceRequest, "selectedPortletId"),
				themeDisplay);

		if (panelCategory == null) {
			return navigationItemsJSONObject;
		}

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			resourceRequest);

		_putNavigationItems(
			httpServletRequest, navigationItemsJSONObject, panelCategory,
			themeDisplay);

		for (PanelCategory childPanelCategory :
				_panelCategoryHelper.getChildPanelCategories(
					panelCategory.getKey(), themeDisplay)) {

			_putNavigationItems(
				httpServletRequest, navigationItemsJSONObject,
				childPanelCategory, themeDisplay);
		}

		return navigationItemsJSONObject;
	}

	private void _putNavigationItems(
			HttpServletRequest httpServletRequest,
			JSONObject navigationItemsJSONObject, PanelCategory panelCategory,
			ThemeDisplay themeDisplay)
		throws Exception {

		// Filter the applications with the three argument overload. The single
		// argument one applies neither PanelAppShowFilter nor PanelApp#isShow,
		// so it would serve the screens of applications the user cannot open.

		for (PanelApp panelApp :
				_panelAppRegistry.getPanelApps(
					panelCategory.getKey(), themeDisplay.getPermissionChecker(),
					themeDisplay.getScopeGroup())) {

			List<PanelAppNavigationItem> panelAppNavigationItems =
				panelApp.getPanelAppNavigationItems(httpServletRequest);

			if (ListUtil.isEmpty(panelAppNavigationItems)) {
				continue;
			}

			JSONArray navigationItemsJSONArray = _jsonFactory.createJSONArray();

			String portletId = panelApp.getPortletId();

			for (int i = 0; i < panelAppNavigationItems.size(); i++) {
				PanelAppNavigationItem panelAppNavigationItem =
					panelAppNavigationItems.get(i);

				// A null label is dropped by JSONObject, so the client would
				// receive an item it cannot render or match.

				if (Validator.isNull(panelAppNavigationItem.getLabel())) {
					continue;
				}

				navigationItemsJSONArray.put(
					JSONUtil.put(
						"canonicalName",
						panelAppNavigationItem.getCanonicalName()
					).put(
						"href", panelAppNavigationItem.getHref()
					).put(
						"id", portletId + StringPool.UNDERLINE + i
					).put(
						"label", panelAppNavigationItem.getLabel()
					).put(
						"parentLabel", panelAppNavigationItem.getParentLabel()
					));
			}

			navigationItemsJSONObject.put(portletId, navigationItemsJSONArray);
		}
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private PanelAppRegistry _panelAppRegistry;

	private PanelCategoryHelper _panelCategoryHelper;

	@Reference
	private Portal _portal;

}
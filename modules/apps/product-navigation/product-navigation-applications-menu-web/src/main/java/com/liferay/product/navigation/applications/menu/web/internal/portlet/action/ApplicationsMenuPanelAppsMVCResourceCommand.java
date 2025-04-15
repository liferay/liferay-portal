/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.applications.menu.web.internal.portlet.action;

import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.PanelCategory;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.applications.menu.web.internal.constants.ProductNavigationApplicationsMenuPortletKeys;
import com.liferay.product.navigation.applications.menu.web.internal.util.ApplicationsMenuUtil;
import com.liferay.site.item.selector.SiteItemSelectorCriterion;
import com.liferay.site.manager.RecentGroupManager;
import com.liferay.site.provider.GroupURLProvider;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ProductNavigationApplicationsMenuPortletKeys.PRODUCT_NAVIGATION_APPLICATIONS_MENU,
		"mvc.command.name=/applications_menu/panel_apps"
	},
	service = MVCResourceCommand.class
)
public class ApplicationsMenuPanelAppsMVCResourceCommand
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
			_getApplicationsMenuContextJSONObject(
				resourceRequest, resourceResponse));
	}

	private JSONObject _getApplicationsMenuContextJSONObject(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			resourceRequest);
		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return JSONUtil.put(
			"items",
			_getPanelCategoriesJSONArray(httpServletRequest, themeDisplay)
		).put(
			"portletNamespace", resourceResponse.getNamespace()
		).put(
			"sites",
			_getSitesJSONObject(
				httpServletRequest, resourceRequest, resourceResponse,
				themeDisplay)
		);
	}

	private JSONArray _getChildPanelCategoriesJSONArray(
			HttpServletRequest httpServletRequest, String key,
			ThemeDisplay themeDisplay)
		throws Exception {

		JSONArray childPanelCategoriesJSONArray =
			_jsonFactory.createJSONArray();

		List<PanelCategory> childPanelCategories =
			_panelCategoryHelper.getChildPanelCategories(key, themeDisplay);

		for (PanelCategory childPanelCategory : childPanelCategories) {
			JSONArray panelAppsJSONArray = _getPanelAppsJSONArray(
				httpServletRequest, childPanelCategory.getKey(), themeDisplay);

			if ((panelAppsJSONArray == null) ||
				(panelAppsJSONArray.length() <= 0)) {

				continue;
			}

			childPanelCategoriesJSONArray.put(
				JSONUtil.put(
					"key", childPanelCategory.getKey()
				).put(
					"label",
					childPanelCategory.getLabel(themeDisplay.getLocale())
				).put(
					"panelApps", panelAppsJSONArray
				));
		}

		List<PanelApp> panelApps = _panelAppRegistry.getPanelApps(
			key, themeDisplay.getPermissionChecker(),
			themeDisplay.getScopeGroup());

		for (PanelApp panelApp : panelApps) {
			childPanelCategoriesJSONArray.put(
				JSONUtil.put(
					"key", panelApp.getKey()
				).put(
					"label", panelApp.getLabel(themeDisplay.getLocale())
				).put(
					"panelApps",
					JSONUtil.putAll(
						_getPanelAppJSONObject(
							httpServletRequest, panelApp, themeDisplay))
				));
		}

		return childPanelCategoriesJSONArray;
	}

	private JSONObject _getPanelAppJSONObject(
			HttpServletRequest httpServletRequest, PanelApp panelApp,
			ThemeDisplay themeDisplay)
		throws Exception {

		return JSONUtil.put(
			"label", panelApp.getLabel(themeDisplay.getLocale())
		).put(
			"portletId", panelApp.getPortletId()
		).put(
			"url", panelApp.getPortletURL(httpServletRequest)
		);
	}

	private JSONArray _getPanelAppsJSONArray(
			HttpServletRequest httpServletRequest, String key,
			ThemeDisplay themeDisplay)
		throws Exception {

		JSONArray panelAppsJSONArray = _jsonFactory.createJSONArray();

		List<PanelApp> panelApps = _panelAppRegistry.getPanelApps(
			key, themeDisplay.getPermissionChecker(),
			themeDisplay.getScopeGroup());

		for (PanelApp panelApp : panelApps) {
			panelAppsJSONArray.put(
				_getPanelAppJSONObject(
					httpServletRequest, panelApp, themeDisplay));
		}

		return panelAppsJSONArray;
	}

	private JSONArray _getPanelCategoriesJSONArray(
			HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay)
		throws Exception {

		JSONArray panelCategoriesJSONArray = _jsonFactory.createJSONArray();

		List<PanelCategory> applicationsMenuPanelCategories =
			_panelCategoryHelper.getChildPanelCategories(
				PanelCategoryKeys.APPLICATIONS_MENU, themeDisplay);

		for (PanelCategory panelCategory : applicationsMenuPanelCategories) {
			JSONArray childCategoriesJSONArray =
				_getChildPanelCategoriesJSONArray(
					httpServletRequest, panelCategory.getKey(), themeDisplay);

			if ((childCategoriesJSONArray == null) ||
				(childCategoriesJSONArray.length() <= 0)) {

				continue;
			}

			panelCategoriesJSONArray.put(
				JSONUtil.put(
					"childCategories", childCategoriesJSONArray
				).put(
					"key", panelCategory.getKey()
				).put(
					"label", panelCategory.getLabel(themeDisplay.getLocale())
				));
		}

		return panelCategoriesJSONArray;
	}

	private JSONArray _getSitesJSONArray(
			List<Group> groups, ResourceRequest resourceRequest,
			ThemeDisplay themeDisplay)
		throws Exception {

		JSONArray recentSitesJSONArray = _jsonFactory.createJSONArray();

		boolean applicationMenuApp = _isApplicationMenuApp(
			resourceRequest, themeDisplay);

		for (Group group : groups) {
			recentSitesJSONArray.put(
				JSONUtil.put(
					"current",
					!applicationMenuApp &&
					(group.getGroupId() == themeDisplay.getScopeGroupId())
				).put(
					"key", group.getGroupKey()
				).put(
					"label", group.getDescriptiveName(themeDisplay.getLocale())
				).put(
					"logoURL", group.getLogoURL(themeDisplay, false)
				).put(
					"url", _groupURLProvider.getGroupURL(group, resourceRequest)
				));
		}

		return recentSitesJSONArray;
	}

	private JSONObject _getSitesJSONObject(
			HttpServletRequest httpServletRequest,
			ResourceRequest resourceRequest, ResourceResponse resourceResponse,
			ThemeDisplay themeDisplay)
		throws Exception {

		JSONObject sitesJSONObject = _jsonFactory.createJSONObject();

		int max = 8;

		List<Group> recentGroups = _recentGroupManager.getRecentGroups(
			httpServletRequest);

		if (ListUtil.isNotEmpty(recentGroups)) {
			sitesJSONObject.put(
				"recentSites",
				_getSitesJSONArray(
					ListUtil.subList(recentGroups, 0, max), resourceRequest,
					themeDisplay));

			max -= recentGroups.size();
		}

		if (max >= 0) {
			List<Group> filteredGroups = new ArrayList<>();

			User user = themeDisplay.getUser();

			List<Group> mySiteGroups = user.getMySiteGroups(
				new String[] {
					Company.class.getName(), Group.class.getName(),
					Organization.class.getName()
				},
				QueryUtil.ALL_POS);

			for (Group group : mySiteGroups) {
				if (!recentGroups.contains(group)) {
					filteredGroups.add(group);
				}
			}

			if (ListUtil.isNotEmpty(filteredGroups)) {
				if (ListUtil.isNotEmpty(recentGroups)) {
					max--;
				}

				sitesJSONObject.put(
					"mySites",
					_getSitesJSONArray(
						ListUtil.subList(filteredGroups, 0, Math.max(0, max)),
						resourceRequest, themeDisplay));

				max -= filteredGroups.size();
			}
		}

		if (max < 0) {
			sitesJSONObject.put(
				"viewAllURL",
				_getViewAllURL(resourceRequest, resourceResponse));
		}

		return sitesJSONObject;
	}

	private String _getViewAllURL(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse) {

		SiteItemSelectorCriterion siteItemSelectorCriterion =
			new SiteItemSelectorCriterion();

		siteItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new URLItemSelectorReturnType());

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(resourceRequest),
				resourceResponse.getNamespace() + "selectSite",
				siteItemSelectorCriterion));
	}

	private boolean _isApplicationMenuApp(
		ResourceRequest resourceRequest, ThemeDisplay themeDisplay) {

		if (!ApplicationsMenuUtil.isEnableApplicationsMenu(
				themeDisplay.getCompanyId(), _configurationProvider)) {

			return false;
		}

		String selectedPortletId = ParamUtil.getString(
			resourceRequest, "selectedPortletId");

		PanelCategoryHelper panelCategoryHelper = new PanelCategoryHelper(
			_panelAppRegistry);

		if (Validator.isNull(selectedPortletId) ||
			!panelCategoryHelper.isApplicationsMenuApp(selectedPortletId)) {

			return false;
		}

		return true;
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private GroupURLProvider _groupURLProvider;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private PanelAppRegistry _panelAppRegistry;

	private PanelCategoryHelper _panelCategoryHelper;

	@Reference
	private Portal _portal;

	@Reference
	private RecentGroupManager _recentGroupManager;

}
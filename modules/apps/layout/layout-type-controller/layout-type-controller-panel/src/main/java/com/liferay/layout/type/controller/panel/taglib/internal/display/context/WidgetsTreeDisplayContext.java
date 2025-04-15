/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.panel.taglib.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.model.PortletCategory;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.util.comparator.PortletCategoryComparator;
import com.liferay.portal.kernel.util.comparator.PortletTitleComparator;
import com.liferay.portal.util.PortletCategoryUtil;
import com.liferay.portal.util.WebAppPool;

import jakarta.portlet.PortletConfig;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author Diego Hu
 */
public class WidgetsTreeDisplayContext {

	public WidgetsTreeDisplayContext(
		HttpServletRequest httpServletRequest,
		LayoutTypePortlet layoutTypePortlet, User user) {

		_httpServletRequest = httpServletRequest;
		_layoutTypePortlet = layoutTypePortlet;
		_user = user;

		_selLayout = (Layout)httpServletRequest.getAttribute(
			WebKeys.SEL_LAYOUT);
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getData() throws Exception {
		return HashMapBuilder.<String, Object>put(
			"items",
			() -> {
				PortletCategory rootPortletCategory =
					(PortletCategory)WebAppPool.get(
						_themeDisplay.getCompanyId(), WebKeys.PORTLET_CATEGORY);

				PortletCategory portletCategory =
					PortletCategoryUtil.getRelevantPortletCategory(
						_themeDisplay.getPermissionChecker(),
						_themeDisplay.getCompanyId(), _themeDisplay.getLayout(),
						rootPortletCategory,
						_themeDisplay.getLayoutTypePortlet());

				return _getPortletCategoryJSONArray(portletCategory);
			}
		).put(
			"selectedPortlets", _getSelectedPortletsJSONArray()
		).build();
	}

	private JSONArray _getPortletCategoryJSONArray(
		PortletCategory portletCategory) {

		JSONArray portletCategoryJSONArray = JSONFactoryUtil.createJSONArray();

		List<PortletCategory> portletCategories = new ArrayList<>(
			portletCategory.getCategories());

		portletCategories.sort(
			new PortletCategoryComparator(_themeDisplay.getLocale()));

		for (PortletCategory currentPortletCategory : portletCategories) {
			if (currentPortletCategory.isHidden()) {
				continue;
			}

			portletCategoryJSONArray.put(
				JSONUtil.put(
					"categories",
					() -> {
						JSONArray childPortletCategoriesJSONArray =
							_getPortletCategoryJSONArray(
								currentPortletCategory);

						if (childPortletCategoriesJSONArray.length() > 0) {
							return childPortletCategoriesJSONArray;
						}

						return null;
					}
				).put(
					"children",
					() -> {
						JSONArray portletsJSONArray = _getPortletsJSONArray(
							currentPortletCategory);

						if (portletsJSONArray.length() > 0) {
							return portletsJSONArray;
						}

						return null;
					}
				).put(
					"id",
					StringUtil.replace(
						currentPortletCategory.getPath(),
						new String[] {"/", "."}, new String[] {"-", "-"})
				).put(
					"name", _getPortletCategoryTitle(currentPortletCategory)
				));
		}

		return portletCategoryJSONArray;
	}

	private String _getPortletCategoryTitle(PortletCategory portletCategory) {
		for (String portletId :
				PortletCategoryUtil.getFirstChildPortletIds(portletCategory)) {

			Portlet portlet = PortletLocalServiceUtil.getPortletById(
				_themeDisplay.getCompanyId(), portletId);

			if (portlet == null) {
				continue;
			}

			PortletApp portletApp = portlet.getPortletApp();

			if (!portletApp.isWARFile()) {
				continue;
			}

			PortletConfig portletConfig = PortletConfigFactoryUtil.create(
				portlet, _httpServletRequest.getServletContext());

			ResourceBundle portletResourceBundle =
				portletConfig.getResourceBundle(_themeDisplay.getLocale());

			String title = ResourceBundleUtil.getString(
				portletResourceBundle, portletCategory.getName());

			if (Validator.isNotNull(title)) {
				return title;
			}
		}

		return LanguageUtil.get(_httpServletRequest, portletCategory.getName());
	}

	private List<Portlet> _getPortlets(PortletCategory portletCategory) {
		List<Portlet> portlets = new ArrayList<>();

		for (String portletId : portletCategory.getPortletIds()) {
			Portlet portlet = PortletLocalServiceUtil.getPortletById(
				_themeDisplay.getCompanyId(), portletId);

			if ((portlet == null) || portlet.isSystem() ||
				!portlet.isActive() || portlet.isInstanceable()) {

				continue;
			}

			if (!portlet.isInstanceable() &&
				_layoutTypePortlet.hasPortletId(portlet.getPortletId())) {

				portlets.add(portlet);

				continue;
			}

			if (!portlet.hasAddPortletPermission(_user.getUserId())) {
				continue;
			}

			portlets.add(portlet);
		}

		return portlets;
	}

	private JSONArray _getPortletsJSONArray(PortletCategory portletCategory) {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		ServletContext servletContext = _httpServletRequest.getServletContext();

		List<Portlet> portlets = ListUtil.sort(
			_getPortlets(portletCategory),
			new PortletTitleComparator(
				servletContext, _themeDisplay.getLocale()));

		for (Portlet portlet : portlets) {
			jsonArray.put(
				JSONUtil.put(
					"id", portlet.getPortletId()
				).put(
					"name",
					PortalUtil.getPortletTitle(
						portlet, servletContext, _themeDisplay.getLocale())
				));
		}

		return jsonArray;
	}

	private JSONArray _getSelectedPortletsJSONArray() throws Exception {
		if (_selLayout == null) {
			return JSONFactoryUtil.createJSONArray();
		}

		UnicodeProperties typeSettingsUnicodeProperties =
			_selLayout.getTypeSettingsProperties();

		String[] panelSelectedPortlets = StringUtil.split(
			typeSettingsUnicodeProperties.getProperty(
				"panelSelectedPortlets", StringPool.BLANK));

		return JSONUtil.toJSONArray(
			panelSelectedPortlets, portletId -> portletId);
	}

	private final HttpServletRequest _httpServletRequest;
	private final LayoutTypePortlet _layoutTypePortlet;
	private final Layout _selLayout;
	private final ThemeDisplay _themeDisplay;
	private final User _user;

}
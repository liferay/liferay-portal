/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.theme.item.selector.web.internal.display.context;

import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.theme.item.selector.web.internal.util.comparator.ThemeNameComparator;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.service.ThemeLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.display.context.GroupDisplayContextHelper;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Stefan Tanasie
 */
public class LayoutThemeItemSelectorDisplayContext {

	public LayoutThemeItemSelectorDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		PortletURL portletURL) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_portletURL = portletURL;
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest, LayoutAdminPortletKeys.GROUP_PAGES,
			"select-order-by-col", "name");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest, LayoutAdminPortletKeys.GROUP_PAGES,
			"select-order-by-type", "asc");

		return _orderByType;
	}

	public SearchContainer<Theme> getThemesSearchContainer() {
		if (_themesSearchContainer != null) {
			return _themesSearchContainer;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		SearchContainer<Theme> themesSearchContainer = new SearchContainer(
			_renderRequest, _portletURL, null, null);

		themesSearchContainer.setOrderByCol(getOrderByCol());
		themesSearchContainer.setOrderByType(getOrderByType());

		GroupDisplayContextHelper groupDisplayContextHelper =
			new GroupDisplayContextHelper(_httpServletRequest);

		boolean orderByAsc = false;

		if (Objects.equals(getOrderByType(), "asc")) {
			orderByAsc = true;
		}

		List<Theme> themes = ListUtil.filter(
			ThemeLocalServiceUtil.getPageThemes(
				themeDisplay.getCompanyId(),
				groupDisplayContextHelper.getLiveGroupId(),
				themeDisplay.getUserId()),
			theme -> {
				if (Objects.equals(theme.getThemeId(), "cms_WAR_cmstheme")) {
					return FeatureFlagManagerUtil.isEnabled(
						themeDisplay.getCompanyId(), "LPD-17564");
				}

				return true;
			});

		themesSearchContainer.setResultsAndTotal(
			ListUtil.sort(themes, new ThemeNameComparator(orderByAsc)));

		_themesSearchContainer = themesSearchContainer;

		return _themesSearchContainer;
	}

	private final HttpServletRequest _httpServletRequest;
	private String _orderByCol;
	private String _orderByType;
	private final PortletURL _portletURL;
	private final RenderRequest _renderRequest;
	private SearchContainer<Theme> _themesSearchContainer;

}
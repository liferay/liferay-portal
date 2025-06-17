/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.item.selector.web.internal;

import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.service.SiteNavigationMenuServiceUtil;
import com.liferay.site.navigation.util.comparator.SiteNavigationMenuModifiedDateComparator;
import com.liferay.site.navigation.util.comparator.SiteNavigationMenuNameComparator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class SiteNavigationMenuItemSelectorViewDescriptor
	implements ItemSelectorViewDescriptor<SiteNavigationMenu> {

	public SiteNavigationMenuItemSelectorViewDescriptor(
		HttpServletRequest httpServletRequest, PortletURL portletURL) {

		_httpServletRequest = httpServletRequest;
		_portletURL = portletURL;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public String getDefaultDisplayStyle() {
		return "list";
	}

	@Override
	public ItemDescriptor getItemDescriptor(
		SiteNavigationMenu siteNavigationMenu) {

		return new SiteNavigationMenuItemDescriptor(
			siteNavigationMenu, _httpServletRequest);
	}

	@Override
	public ItemSelectorReturnType getItemSelectorReturnType() {
		return new UUIDItemSelectorReturnType();
	}

	@Override
	public String[] getOrderByKeys() {
		return new String[] {"modified-date", "name"};
	}

	@Override
	public SearchContainer<SiteNavigationMenu> getSearchContainer()
		throws PortalException {

		SearchContainer<SiteNavigationMenu> searchContainer =
			new SearchContainer<>(
				(PortletRequest)_httpServletRequest.getAttribute(
					JavaConstants.JAKARTA_PORTLET_REQUEST),
				_portletURL, null, "there-are-no-navigation-menus");

		String orderByCol = ParamUtil.getString(
			_httpServletRequest, "orderByCol", "modified-date");

		searchContainer.setOrderByCol(orderByCol);

		String orderByType = ParamUtil.getString(
			_httpServletRequest, "orderByType", "asc");

		searchContainer.setOrderByComparator(
			_getOrderByComparator(orderByCol, orderByType));
		searchContainer.setOrderByType(orderByType);

		long[] groupIds = PortalUtil.getCurrentAndAncestorSiteGroupIds(
			_themeDisplay.getScopeGroupId(), true);

		String keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		if (Validator.isNotNull(keywords)) {
			searchContainer.setResultsAndTotal(
				() -> SiteNavigationMenuServiceUtil.getSiteNavigationMenus(
					groupIds, keywords, searchContainer.getStart(),
					searchContainer.getEnd(),
					searchContainer.getOrderByComparator()),
				SiteNavigationMenuServiceUtil.getSiteNavigationMenusCount(
					groupIds, keywords));
		}
		else {
			searchContainer.setResultsAndTotal(
				() -> SiteNavigationMenuServiceUtil.getSiteNavigationMenus(
					groupIds, searchContainer.getStart(),
					searchContainer.getEnd(),
					searchContainer.getOrderByComparator()),
				SiteNavigationMenuServiceUtil.getSiteNavigationMenusCount(
					groupIds));
		}

		return searchContainer;
	}

	@Override
	public boolean isShowBreadcrumb() {
		return false;
	}

	@Override
	public boolean isShowSearch() {
		return true;
	}

	private OrderByComparator<SiteNavigationMenu> _getOrderByComparator(
		String orderByCol, String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		if (orderByCol.equals("modified-date")) {
			return SiteNavigationMenuModifiedDateComparator.getInstance(
				orderByAsc);
		}
		else if (orderByCol.equals("name")) {
			return SiteNavigationMenuNameComparator.getInstance(orderByAsc);
		}

		return null;
	}

	private final HttpServletRequest _httpServletRequest;
	private final PortletURL _portletURL;
	private final ThemeDisplay _themeDisplay;

}
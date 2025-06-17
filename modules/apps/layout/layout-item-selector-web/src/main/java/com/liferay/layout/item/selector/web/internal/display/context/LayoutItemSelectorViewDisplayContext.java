/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.item.selector.web.internal.display.context;

import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.layout.item.selector.LayoutItemSelectorReturnType;
import com.liferay.layout.item.selector.criterion.LayoutItemSelectorCriterion;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Roberto Díaz
 */
public class LayoutItemSelectorViewDisplayContext {

	public LayoutItemSelectorViewDisplayContext(
		HttpServletRequest httpServletRequest,
		LayoutItemSelectorCriterion layoutItemSelectorCriterion,
		PortletURL portletURL, String itemSelectedEventName,
		boolean privateLayout) {

		_httpServletRequest = httpServletRequest;
		_layoutItemSelectorCriterion = layoutItemSelectorCriterion;
		_portletURL = portletURL;
		_itemSelectedEventName = itemSelectedEventName;
		_privateLayout = privateLayout;

		_renderResponse = (RenderResponse)httpServletRequest.getAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE);
	}

	public String getItemSelectedEventName() {
		return _itemSelectedEventName;
	}

	public String getItemSelectedReturnType() {
		if (_itemSelectedReturnType != null) {
			return _itemSelectedReturnType;
		}

		_itemSelectedReturnType = URLItemSelectorReturnType.class.getName();

		for (ItemSelectorReturnType itemSelectorReturnType :
				_layoutItemSelectorCriterion.
					getDesiredItemSelectorReturnTypes()) {

			Class<?> clazz = itemSelectorReturnType.getClass();

			if (_supportedItemSelectorReturnTypesClassNames.contains(
					clazz.getName())) {

				_itemSelectedReturnType = clazz.getName();

				break;
			}
		}

		return _itemSelectedReturnType;
	}

	public List<BreadcrumbEntry> getPortletBreadcrumbEntries()
		throws PortalException, PortletException {

		return Arrays.asList(
			_getSitesAndAssetLibrariesBreadcrumbEntry(),
			_getHomeBreadcrumbEntry());
	}

	public boolean isCheckDisplayPage() {
		return _layoutItemSelectorCriterion.isCheckDisplayPage();
	}

	public boolean isEnableCurrentPage() {
		return _layoutItemSelectorCriterion.isEnableCurrentPage();
	}

	public boolean isMultiSelection() {
		return _layoutItemSelectorCriterion.isMultiSelection();
	}

	public boolean isPrivateLayout() {
		return _privateLayout;
	}

	public boolean isShowBreadcrumb() {
		return _layoutItemSelectorCriterion.isShowBreadcrumb();
	}

	private BreadcrumbEntry _getHomeBreadcrumbEntry() throws PortalException {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		BreadcrumbEntry breadcrumbEntry = new BreadcrumbEntry();

		breadcrumbEntry.setTitle(themeDisplay.getSiteGroupName());

		return breadcrumbEntry;
	}

	private BreadcrumbEntry _getSitesAndAssetLibrariesBreadcrumbEntry()
		throws PortletException {

		BreadcrumbEntry breadcrumbEntry = new BreadcrumbEntry();

		breadcrumbEntry.setTitle(
			LanguageUtil.get(_httpServletRequest, "sites-and-libraries"));
		breadcrumbEntry.setURL(
			PortletURLBuilder.create(
				PortletURLUtil.clone(
					_portletURL,
					PortalUtil.getLiferayPortletResponse(_renderResponse))
			).setParameter(
				"groupType", "site"
			).setParameter(
				"showGroupSelector", true
			).buildString());

		return breadcrumbEntry;
	}

	private static final List<String>
		_supportedItemSelectorReturnTypesClassNames =
			Collections.unmodifiableList(
				ListUtil.fromArray(
					LayoutItemSelectorReturnType.class.getName(),
					URLItemSelectorReturnType.class.getName(),
					UUIDItemSelectorReturnType.class.getName()));

	private final HttpServletRequest _httpServletRequest;
	private final String _itemSelectedEventName;
	private String _itemSelectedReturnType;
	private final LayoutItemSelectorCriterion _layoutItemSelectorCriterion;
	private final PortletURL _portletURL;
	private final boolean _privateLayout;
	private final RenderResponse _renderResponse;

}
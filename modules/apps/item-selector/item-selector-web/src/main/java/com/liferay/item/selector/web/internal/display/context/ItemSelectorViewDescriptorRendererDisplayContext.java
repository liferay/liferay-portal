/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.web.internal.display.context;

import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Alejandro Tardín
 */
public class ItemSelectorViewDescriptorRendererDisplayContext {

	public ItemSelectorViewDescriptorRendererDisplayContext(
		HttpServletRequest httpServletRequest, String itemSelectedEventName,
		ItemSelectorViewDescriptor<Object> itemSelectorViewDescriptor,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_httpServletRequest = httpServletRequest;
		_itemSelectedEventName = itemSelectedEventName;
		_itemSelectorViewDescriptor = itemSelectorViewDescriptor;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
	}

	public List<BreadcrumbEntry> getBreadcrumbEntries(PortletURL currentURL)
		throws PortalException, PortletException {

		return Arrays.asList(
			_getGroupSelectorBreadcrumbEntry(currentURL),
			_getCurrentGroupBreadcrumbEntry(currentURL));
	}

	public String getDisplayStyle() {
		if (_displayStyle != null) {
			return _displayStyle;
		}

		_displayStyle = ParamUtil.getString(
			_httpServletRequest, "displayStyle",
			_itemSelectorViewDescriptor.getDefaultDisplayStyle());

		return _displayStyle;
	}

	public String getItemSelectedEventName() {
		return _itemSelectedEventName;
	}

	public ItemSelectorViewDescriptor<Object> getItemSelectorViewDescriptor() {
		return _itemSelectorViewDescriptor;
	}

	public String getReturnType() {
		ItemSelectorReturnType itemSelectorReturnType =
			_itemSelectorViewDescriptor.getItemSelectorReturnType();

		Class<? extends ItemSelectorReturnType> itemSelectorReturnTypeClass =
			itemSelectorReturnType.getClass();

		return itemSelectorReturnTypeClass.getName();
	}

	public SearchContainer<Object> getSearchContainer() throws PortalException {
		if (_searchContainer == null) {
			_searchContainer = _itemSelectorViewDescriptor.getSearchContainer();

			if (isMultipleSelection()) {
				if (_searchContainer.getRowChecker() == null) {
					_searchContainer.setRowChecker(
						new EmptyOnClickRowChecker(_liferayPortletResponse));
				}
			}
			else {
				_searchContainer.setRowChecker(null);
			}

			_searchContainer.setIteratorURL(
				PortletURLUtil.getCurrent(
					_liferayPortletRequest, _liferayPortletResponse));
		}

		return _searchContainer;
	}

	public boolean isDescriptiveDisplayStyle() {
		return Objects.equals(getDisplayStyle(), "descriptive");
	}

	public boolean isIconDisplayStyle() {
		return Objects.equals(getDisplayStyle(), "icon");
	}

	public boolean isMultipleSelection() {
		return _itemSelectorViewDescriptor.isMultipleSelection();
	}

	private BreadcrumbEntry _getCurrentGroupBreadcrumbEntry(
			PortletURL currentURL)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group scopeGroup = themeDisplay.getScopeGroup();

		BreadcrumbEntry breadcrumbEntry = new BreadcrumbEntry();

		breadcrumbEntry.setTitle(
			scopeGroup.getDescriptiveName(_httpServletRequest.getLocale()));
		breadcrumbEntry.setURL(currentURL.toString());

		return breadcrumbEntry;
	}

	private BreadcrumbEntry _getGroupSelectorBreadcrumbEntry(
			PortletURL currentURL)
		throws PortletException {

		BreadcrumbEntry breadcrumbEntry = new BreadcrumbEntry();

		breadcrumbEntry.setTitle(
			LanguageUtil.get(_httpServletRequest, "sites-and-libraries"));
		breadcrumbEntry.setURL(
			PortletURLBuilder.create(
				PortletURLUtil.clone(currentURL, _liferayPortletResponse)
			).setParameter(
				"groupType", "site"
			).setParameter(
				"showGroupSelector", true
			).buildString());

		return breadcrumbEntry;
	}

	private String _displayStyle;
	private final HttpServletRequest _httpServletRequest;
	private final String _itemSelectedEventName;
	private final ItemSelectorViewDescriptor<Object>
		_itemSelectorViewDescriptor;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private SearchContainer<Object> _searchContainer;

}
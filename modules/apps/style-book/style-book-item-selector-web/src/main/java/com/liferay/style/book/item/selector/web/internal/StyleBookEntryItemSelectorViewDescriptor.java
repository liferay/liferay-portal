/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.item.selector.web.internal;

import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.item.selector.criteria.AssetEntryItemSelectorReturnType;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.style.book.item.selector.StyleBookEntryItemSelectorCriterion;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.service.StyleBookEntryLocalServiceUtil;
import com.liferay.style.book.util.StyleBookUtil;
import com.liferay.style.book.util.comparator.StyleBookEntryNameComparator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class StyleBookEntryItemSelectorViewDescriptor
	implements ItemSelectorViewDescriptor<StyleBookEntry> {

	public StyleBookEntryItemSelectorViewDescriptor(
		FrontendTokenDefinitionRegistry frontendTokenDefinitionRegistry,
		HttpServletRequest httpServletRequest, PortletURL portletURL,
		StyleBookEntryItemSelectorCriterion styleBookEntryItemSelectorCriterion,
		StyleBookEntryLocalService styleBookEntryLocalService) {

		_frontendTokenDefinitionRegistry = frontendTokenDefinitionRegistry;
		_httpServletRequest = httpServletRequest;
		_portletURL = portletURL;
		_styleBookEntryItemSelectorCriterion =
			styleBookEntryItemSelectorCriterion;
		_styleBookEntryLocalService = styleBookEntryLocalService;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public String getDefaultDisplayStyle() {
		return "icon";
	}

	@Override
	public String[] getDisplayViews() {
		return new String[] {"icon"};
	}

	@Override
	public ItemDescriptor getItemDescriptor(StyleBookEntry styleBookEntry) {
		return new StyleBookEntryItemDescriptor(
			_getSelLayout(), styleBookEntry);
	}

	@Override
	public ItemSelectorReturnType getItemSelectorReturnType() {
		return new AssetEntryItemSelectorReturnType();
	}

	@Override
	public String[] getOrderByKeys() {
		return new String[] {"name", "create-date"};
	}

	@Override
	public SearchContainer<StyleBookEntry> getSearchContainer()
		throws PortalException {

		SearchContainer<StyleBookEntry> styleBookEntrySearchContainer =
			new SearchContainer<>(
				(PortletRequest)_httpServletRequest.getAttribute(
					JavaConstants.JAKARTA_PORTLET_REQUEST),
				_portletURL, null, "there-are-no-style-books");

		styleBookEntrySearchContainer.setResultsAndTotal(
			_getStyleBookEntries());

		return styleBookEntrySearchContainer;
	}

	@Override
	public boolean isShowBreadcrumb() {
		return false;
	}

	@Override
	public boolean isShowManagementToolbar() {
		return false;
	}

	private Layout _getSelLayout() {
		if (_selLayout != null) {
			return _selLayout;
		}

		if (_styleBookEntryItemSelectorCriterion.getSelPlid() !=
				LayoutConstants.DEFAULT_PLID) {

			_selLayout = LayoutLocalServiceUtil.fetchLayout(
				_styleBookEntryItemSelectorCriterion.getSelPlid());
		}

		return _selLayout;
	}

	private List<StyleBookEntry> _getStyleBookEntries() throws PortalException {
		List<StyleBookEntry> styleBookEntries = ListUtil.fromArray(
			StyleBookUtil.getStyleFromThemeStyleBookEntry(
				_getSelLayout(), _themeDisplay.getLocale()));

		if (!FeatureFlagManagerUtil.isEnabled(
				_themeDisplay.getCompanyId(), "LPD-30204")) {

			styleBookEntries.addAll(
				StyleBookEntryLocalServiceUtil.getStyleBookEntries(
					StagingUtil.getLiveGroupId(_themeDisplay.getScopeGroupId()),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					StyleBookEntryNameComparator.getInstance(true)));

			return styleBookEntries;
		}

		FrontendTokenDefinition frontendTokenDefinition =
			_frontendTokenDefinitionRegistry.getFrontendTokenDefinition(
				_getSelLayout());

		if (frontendTokenDefinition == null) {
			return Collections.emptyList();
		}

		styleBookEntries.addAll(
			_styleBookEntryLocalService.getStyleBookEntries(
				StagingUtil.getLiveGroupId(_themeDisplay.getScopeGroupId()),
				frontendTokenDefinition.getThemeId()));

		return styleBookEntries;
	}

	private final FrontendTokenDefinitionRegistry
		_frontendTokenDefinitionRegistry;
	private final HttpServletRequest _httpServletRequest;
	private final PortletURL _portletURL;
	private Layout _selLayout;
	private final StyleBookEntryItemSelectorCriterion
		_styleBookEntryItemSelectorCriterion;
	private final StyleBookEntryLocalService _styleBookEntryLocalService;
	private final ThemeDisplay _themeDisplay;

}
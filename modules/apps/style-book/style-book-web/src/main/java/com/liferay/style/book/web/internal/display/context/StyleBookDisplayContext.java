/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.display.context;

import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.style.book.constants.StyleBookActionKeys;
import com.liferay.style.book.constants.StyleBookPortletKeys;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalServiceUtil;
import com.liferay.style.book.util.StyleBookUtil;
import com.liferay.style.book.util.comparator.StyleBookEntryCreateDateComparator;
import com.liferay.style.book.util.comparator.StyleBookEntryNameComparator;
import com.liferay.style.book.web.internal.security.permissions.resource.StyleBookPermission;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class StyleBookDisplayContext {

	public StyleBookDisplayContext(
		FrontendTokenDefinitionRegistry frontendTokenDefinitionRegistry,
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_frontendTokenDefinitionRegistry = frontendTokenDefinitionRegistry;
		_httpServletRequest = httpServletRequest;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
	}

	public PortletURL getPortletURL() {
		PortletURL portletURL = _liferayPortletResponse.createRenderURL();

		String keywords = _getKeywords();

		if (Validator.isNotNull(keywords)) {
			portletURL.setParameter("keywords", keywords);
		}

		String orderByCol = _getOrderByCol();

		if (Validator.isNotNull(orderByCol)) {
			portletURL.setParameter("orderByCol", orderByCol);
		}

		String orderByType = _getOrderByType();

		if (Validator.isNotNull(orderByType)) {
			portletURL.setParameter("orderByType", orderByType);
		}

		return portletURL;
	}

	public SearchContainer<StyleBookEntry>
		getStyleBookEntriesSearchContainer() {

		if (_styleBookEntriesSearchContainer != null) {
			return _styleBookEntriesSearchContainer;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		SearchContainer<StyleBookEntry> styleBookEntriesSearchContainer =
			new SearchContainer<>(
				_liferayPortletRequest, getPortletURL(), null,
				"there-are-no-style-books");

		styleBookEntriesSearchContainer.setOrderByCol(_getOrderByCol());
		styleBookEntriesSearchContainer.setOrderByComparator(
			_getStyleBookEntryOrderByComparator());
		styleBookEntriesSearchContainer.setOrderByType(_getOrderByType());

		if (_isSearch()) {
			styleBookEntriesSearchContainer.setResultsAndTotal(
				() -> StyleBookEntryLocalServiceUtil.getStyleBookEntries(
					themeDisplay.getScopeGroupId(), _getKeywords(),
					styleBookEntriesSearchContainer.getStart(),
					styleBookEntriesSearchContainer.getEnd(),
					styleBookEntriesSearchContainer.getOrderByComparator()),
				StyleBookEntryLocalServiceUtil.getStyleBookEntriesCount(
					themeDisplay.getScopeGroupId(), _getKeywords()));
		}
		else {
			List<StyleBookEntry> styleBookEntries = new ArrayList<>();

			int styleBookEntriesCount =
				StyleBookEntryLocalServiceUtil.getStyleBookEntriesCount(
					themeDisplay.getScopeGroupId());

			int start = styleBookEntriesSearchContainer.getStart();
			int end = styleBookEntriesSearchContainer.getEnd();

			if (start == 0) {
				end -= 1;

				if (FeatureFlagManagerUtil.isEnabled(
						themeDisplay.getCompanyId(), "LPD-30204")) {

					styleBookEntries.addAll(
						_getStyleFromThemeStyleBookEntries(
							themeDisplay.getScopeGroupId()));
				}
				else {
					styleBookEntries.add(
						StyleBookUtil.getStyleFromThemeStyleBookEntry(
							themeDisplay.getLayout(),
							themeDisplay.getLocale()));
				}
			}
			else {
				start -= 1;
			}

			styleBookEntries.addAll(
				StyleBookEntryLocalServiceUtil.getStyleBookEntries(
					themeDisplay.getScopeGroupId(), start, end,
					styleBookEntriesSearchContainer.getOrderByComparator()));

			styleBookEntriesSearchContainer.setResultsAndTotal(
				() -> styleBookEntries, styleBookEntriesCount + 1);
		}

		if (StyleBookPermission.contains(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(),
				StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES)) {

			styleBookEntriesSearchContainer.setRowChecker(
				new EmptyOnClickRowChecker(_liferayPortletResponse));
		}

		_styleBookEntriesSearchContainer = styleBookEntriesSearchContainer;

		return _styleBookEntriesSearchContainer;
	}

	private String _getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	private String _getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest, StyleBookPortletKeys.STYLE_BOOK,
			"create-date");

		return _orderByCol;
	}

	private String _getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest, StyleBookPortletKeys.STYLE_BOOK, "asc");

		return _orderByType;
	}

	private OrderByComparator<StyleBookEntry>
		_getStyleBookEntryOrderByComparator() {

		boolean orderByAsc = false;

		if (Objects.equals(_getOrderByType(), "asc")) {
			orderByAsc = true;
		}

		OrderByComparator<StyleBookEntry> orderByComparator = null;

		if (Objects.equals(_getOrderByCol(), "create-date")) {
			orderByComparator = StyleBookEntryCreateDateComparator.getInstance(
				orderByAsc);
		}
		else if (Objects.equals(_getOrderByCol(), "name")) {
			orderByComparator = StyleBookEntryNameComparator.getInstance(
				orderByAsc);
		}

		return orderByComparator;
	}

	private List<StyleBookEntry> _getStyleFromThemeStyleBookEntries(
		long groupId) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		List<StyleBookEntry> styleFromThemeStyleBookEntries = new ArrayList<>();

		for (FrontendTokenDefinition frontendTokenDefinition :
				_frontendTokenDefinitionRegistry.getFrontendTokenDefinitions(
					themeDisplay.getCompanyId())) {

			styleFromThemeStyleBookEntries.add(
				StyleBookUtil.getStyleFromThemeStyleBookEntry(
					frontendTokenDefinition, groupId,
					themeDisplay.getLocale()));
		}

		return styleFromThemeStyleBookEntries;
	}

	private boolean _isSearch() {
		return Validator.isNotNull(_getKeywords());
	}

	private final FrontendTokenDefinitionRegistry
		_frontendTokenDefinitionRegistry;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private String _orderByCol;
	private String _orderByType;
	private SearchContainer<StyleBookEntry> _styleBookEntriesSearchContainer;

}
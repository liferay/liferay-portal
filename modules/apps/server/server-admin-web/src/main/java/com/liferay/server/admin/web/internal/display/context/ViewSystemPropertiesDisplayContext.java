/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsValues;
import com.liferay.server.admin.web.internal.constants.ServerAdminNavigationEntryConstants;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * @author Mariano Álvaro Sáiz
 */
public class ViewSystemPropertiesDisplayContext {

	public ViewSystemPropertiesDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletRequest = liferayPortletRequest;
		_renderResponse = renderResponse;
	}

	public PortletURL getPortletURL() {
		if (_portletURL != null) {
			return _portletURL;
		}

		_portletURL = PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/server_admin/view"
		).setTabs1(
			ParamUtil.getString(_httpServletRequest, "tabs1", "resources")
		).setParameter(
			"delta",
			ParamUtil.getInteger(
				_httpServletRequest, SearchContainer.DEFAULT_DELTA_PARAM,
				SearchContainer.DEFAULT_DELTA)
		).setParameter(
			"screenNavigationCategoryKey",
			ParamUtil.getString(
				_httpServletRequest, "screenNavigationCategoryKey",
				ServerAdminNavigationEntryConstants.
					CATEGORY_KEY_SYSTEM_PROPERTIES)
		).setParameter(
			"screenNavigationEntryKey",
			ParamUtil.getString(
				_httpServletRequest, "screenNavigationEntryKey",
				ServerAdminNavigationEntryConstants.ENTRY_KEY_SYSTEM_PROPERTIES)
		).buildPortletURL();

		return _portletURL;
	}

	public SearchContainer<Map.Entry<String, String>> getSearchContainer() {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		String emptyResultsMessage = null;

		if (_isPortalPropertiesTab()) {
			emptyResultsMessage =
				"no-portal-properties-were-found-that-matched-the-keywords";
		}
		else {
			emptyResultsMessage =
				"no-system-properties-were-found-that-matched-the-keywords";
		}

		_searchContainer = new SearchContainer(
			_liferayPortletRequest, getPortletURL(), null, emptyResultsMessage);

		_searchContainer.setResultsAndTotal(_loadFilteredProperties());

		return _searchContainer;
	}

	public int getSearchContainerTotal() {
		SearchContainer<Map.Entry<String, String>> searchContainer =
			getSearchContainer();

		return searchContainer.getTotal();
	}

	private String _getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	private boolean _isPortalPropertiesTab() {
		if (_portalPropertiesTab != null) {
			return _portalPropertiesTab;
		}

		String tab = ParamUtil.getString(_httpServletRequest, "tabs2");

		_portalPropertiesTab = tab.equals("portal-properties");

		return _portalPropertiesTab;
	}

	private List<Map.Entry<String, String>> _loadFilteredProperties() {
		if (_filteredProperties != null) {
			return _filteredProperties;
		}

		Map<String, String> filteredProperties = new TreeMap<>();

		Properties properties = System.getProperties();

		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
			String property = String.valueOf(entry.getKey());
			String value = StringPool.BLANK;

			if (ArrayUtil.contains(
					PropsValues.ADMIN_OBFUSCATED_PROPERTIES, property)) {

				value = StringPool.EIGHT_STARS;
			}
			else {
				value = String.valueOf(entry.getValue());
			}

			String keywords = _getKeywords();

			if (Validator.isNull(keywords) || property.contains(keywords) ||
				value.contains(keywords)) {

				filteredProperties.put(property, value);
			}
		}

		_filteredProperties = ListUtil.fromCollection(
			filteredProperties.entrySet());

		return _filteredProperties;
	}

	private List<Map.Entry<String, String>> _filteredProperties;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private final LiferayPortletRequest _liferayPortletRequest;
	private Boolean _portalPropertiesTab;
	private PortletURL _portletURL;
	private final RenderResponse _renderResponse;
	private SearchContainer<Map.Entry<String, String>> _searchContainer;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.item.selector.web.internal.display.context;

import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.SearchDisplayStyleUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Alessio Antonio Rendina
 */
public abstract class BaseCPItemSelectorViewDisplayContext<T> {

	public BaseCPItemSelectorViewDisplayContext(
		HttpServletRequest httpServletRequest, PortletURL portletURL,
		String itemSelectedEventName, String portalPreferenceNamespace) {

		this.httpServletRequest = httpServletRequest;
		_portletURL = portletURL;
		_itemSelectedEventName = itemSelectedEventName;
		_portalPreferenceNamespace = portalPreferenceNamespace;

		portalPreferences = PortletPreferencesFactoryUtil.getPortalPreferences(
			this.httpServletRequest);

		cpRequestHelper = new CPRequestHelper(httpServletRequest);

		liferayPortletRequest = cpRequestHelper.getLiferayPortletRequest();
		liferayPortletResponse = cpRequestHelper.getLiferayPortletResponse();

		_defaultOrderByCol = "title";
		_defaultOrderByType = "asc";
	}

	public String getDisplayStyle() {
		if (Validator.isNotNull(_displayStyle)) {
			return _displayStyle;
		}

		_displayStyle = SearchDisplayStyleUtil.getDisplayStyle(
			httpServletRequest, _portalPreferenceNamespace, "list", true);

		return _displayStyle;
	}

	public String getItemSelectedEventName() {
		return _itemSelectedEventName;
	}

	public String getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(httpServletRequest, "keywords");

		return _keywords;
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			httpServletRequest, _portalPreferenceNamespace, _defaultOrderByCol);

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			httpServletRequest, _portalPreferenceNamespace,
			_defaultOrderByType);

		return _orderByType;
	}

	public PortletURL getPortletURL() {
		return _portletURL;
	}

	public long getScopeGroupId() {
		return cpRequestHelper.getScopeGroupId();
	}

	public abstract SearchContainer<T> getSearchContainer()
		throws PortalException;

	public boolean isSearch() {
		return Validator.isNotNull(getKeywords());
	}

	public boolean isShowInfoPanel() {
		return !isSearch();
	}

	public void setDefaultOrderByCol(String defaultOrderByCol) {
		_defaultOrderByCol = defaultOrderByCol;
	}

	public void setDefaultOrderByType(String defaultOrderByType) {
		_defaultOrderByType = defaultOrderByType;
	}

	protected RowChecker getRowChecker() {
		if (_rowChecker == null) {
			_rowChecker = new EmptyOnClickRowChecker(liferayPortletResponse);
		}

		return _rowChecker;
	}

	protected final CPRequestHelper cpRequestHelper;
	protected final HttpServletRequest httpServletRequest;
	protected final LiferayPortletRequest liferayPortletRequest;
	protected final LiferayPortletResponse liferayPortletResponse;
	protected final PortalPreferences portalPreferences;
	protected SearchContainer<T> searchContainer;

	private String _defaultOrderByCol;
	private String _defaultOrderByType;
	private String _displayStyle;
	private final String _itemSelectedEventName;
	private String _keywords;
	private String _orderByCol;
	private String _orderByType;
	private final String _portalPreferenceNamespace;
	private final PortletURL _portletURL;
	private RowChecker _rowChecker;

}
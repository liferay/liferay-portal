/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.options.web.internal.display.context;

import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.commerce.product.options.web.internal.portlet.action.helper.ActionHelper;
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
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Alessio Antonio Rendina
 */
public abstract class BaseCPOptionsDisplayContext<T> {

	public BaseCPOptionsDisplayContext(
		ActionHelper actionHelper, HttpServletRequest httpServletRequest,
		String portalPreferenceNamespace,
		PortletResourcePermission portletResourcePermission) {

		this.actionHelper = actionHelper;
		this.httpServletRequest = httpServletRequest;
		_portalPreferenceNamespace = portalPreferenceNamespace;
		_portletResourcePermission = portletResourcePermission;

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

	public PortletURL getPortletURL() throws PortalException {
		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		String redirect = ParamUtil.getString(httpServletRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			portletURL.setParameter("redirect", redirect);
		}

		String delta = ParamUtil.getString(httpServletRequest, "delta");

		if (Validator.isNotNull(delta)) {
			portletURL.setParameter("delta", delta);
		}

		String deltaEntry = ParamUtil.getString(
			httpServletRequest, "deltaEntry");

		if (Validator.isNotNull(deltaEntry)) {
			portletURL.setParameter("deltaEntry", deltaEntry);
		}

		String displayStyle = ParamUtil.getString(
			httpServletRequest, "displayStyle");

		if (Validator.isNotNull(displayStyle)) {
			portletURL.setParameter("displayStyle", getDisplayStyle());
		}

		String keywords = ParamUtil.getString(httpServletRequest, "keywords");

		if (Validator.isNotNull(keywords)) {
			portletURL.setParameter("keywords", keywords);
		}

		String orderByCol = getOrderByCol();

		if (Validator.isNotNull(orderByCol)) {
			portletURL.setParameter("orderByCol", orderByCol);
		}

		String orderByType = getOrderByType();

		if (Validator.isNotNull(orderByType)) {
			portletURL.setParameter("orderByType", orderByType);
		}

		return portletURL;
	}

	public abstract SearchContainer<T> getSearchContainer()
		throws PortalException;

	public boolean hasPermission(String actionId) throws PortalException {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _portletResourcePermission.contains(
			themeDisplay.getPermissionChecker(), null, actionId);
	}

	public boolean isShowInfoPanel() {
		return true;
	}

	protected String getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(httpServletRequest, "keywords");

		return _keywords;
	}

	protected RowChecker getRowChecker() {
		if (_rowChecker == null) {
			_rowChecker = new EmptyOnClickRowChecker(liferayPortletResponse);
		}

		return _rowChecker;
	}

	protected void setDefaultOrderByCol(String defaultOrderByCol) {
		_defaultOrderByCol = defaultOrderByCol;
	}

	protected final ActionHelper actionHelper;
	protected final CPRequestHelper cpRequestHelper;
	protected final HttpServletRequest httpServletRequest;
	protected final LiferayPortletRequest liferayPortletRequest;
	protected final LiferayPortletResponse liferayPortletResponse;
	protected final PortalPreferences portalPreferences;
	protected SearchContainer<T> searchContainer;

	private String _defaultOrderByCol;
	private final String _defaultOrderByType;
	private String _displayStyle;
	private String _keywords;
	private String _orderByCol;
	private String _orderByType;
	private final String _portalPreferenceNamespace;
	private final PortletResourcePermission _portletResourcePermission;
	private RowChecker _rowChecker;

}
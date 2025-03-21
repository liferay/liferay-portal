/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.web.internal.display.context;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.web.internal.constants.AnalyticsSettingsWebKeys;
import com.liferay.analytics.settings.web.internal.search.UserGroupChecker;
import com.liferay.analytics.settings.web.internal.search.UserGroupSearch;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.UserGroupServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.util.comparator.UserGroupNameComparator;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * @author André Miranda
 */
public class UserGroupDisplayContext {

	public UserGroupDisplayContext(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_analyticsConfiguration =
			(AnalyticsConfiguration)renderRequest.getAttribute(
				AnalyticsSettingsWebKeys.ANALYTICS_CONFIGURATION);
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_renderRequest, AnalyticsSettingsWebKeys.ANALYTICS_CONFIGURATION,
			"user-group-order-by-type", "asc");

		return _orderByType;
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/analytics_settings/edit_synced_contacts_groups"
		).buildPortletURL();
	}

	public UserGroupSearch getUserGroupSearch() {
		UserGroupSearch userGroupSearch = new UserGroupSearch(
			_renderRequest, getPortletURL());

		userGroupSearch.setOrderByCol(_getOrderByCol());
		userGroupSearch.setOrderByType(getOrderByType());
		userGroupSearch.setResultsAndTotal(
			() -> UserGroupServiceUtil.search(
				_getCompanyId(), _getKeywords(), _getUserGroupParams(),
				userGroupSearch.getStart(), userGroupSearch.getEnd(),
				UserGroupNameComparator.getInstance(_isOrderByAscending())),
			UserGroupServiceUtil.searchCount(
				_getCompanyId(), _getKeywords(), _getUserGroupParams()));
		userGroupSearch.setRowChecker(
			new UserGroupChecker(
				_renderResponse,
				SetUtil.fromArray(
					_analyticsConfiguration.syncedUserGroupIds())));

		return userGroupSearch;
	}

	private long _getCompanyId() {
		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return themeDisplay.getCompanyId();
	}

	private String _getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_renderRequest, "keywords");

		return _keywords;
	}

	private String _getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_renderRequest, AnalyticsSettingsWebKeys.ANALYTICS_CONFIGURATION,
			"user-group-order-by-col", "user-group-name");

		return _orderByCol;
	}

	private LinkedHashMap<String, Object> _getUserGroupParams() {
		return LinkedHashMapBuilder.<String, Object>put(
			"active", Boolean.TRUE
		).build();
	}

	private boolean _isOrderByAscending() {
		return Objects.equals(getOrderByType(), "asc");
	}

	private final AnalyticsConfiguration _analyticsConfiguration;
	private String _keywords;
	private String _orderByCol;
	private String _orderByType;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}
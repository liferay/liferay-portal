/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.web.internal.display.context;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.web.internal.constants.AnalyticsSettingsWebKeys;
import com.liferay.analytics.settings.web.internal.search.OrganizationChecker;
import com.liferay.analytics.settings.web.internal.search.OrganizationSearch;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.util.comparator.OrganizationNameComparator;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * @author André Miranda
 */
public class OrganizationDisplayContext {

	public OrganizationDisplayContext(
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
			"organization-order-by-type", "asc");

		return _orderByType;
	}

	public OrganizationSearch getOrganizationSearch() {
		OrganizationSearch organizationSearch = new OrganizationSearch(
			_renderRequest, getPortletURL());

		organizationSearch.setOrderByCol(_getOrderByCol());
		organizationSearch.setOrderByType(getOrderByType());
		organizationSearch.setResultsAndTotal(
			() -> OrganizationLocalServiceUtil.search(
				_getCompanyId(),
				OrganizationConstants.ANY_PARENT_ORGANIZATION_ID,
				_getKeywords(), null, null, null, _getOrganizationParams(),
				organizationSearch.getStart(), organizationSearch.getEnd(),
				OrganizationNameComparator.getInstance(_isOrderByAscending())),
			OrganizationLocalServiceUtil.searchCount(
				_getCompanyId(),
				OrganizationConstants.ANY_PARENT_ORGANIZATION_ID,
				_getKeywords(), null, null, null, _getOrganizationParams()));
		organizationSearch.setRowChecker(
			new OrganizationChecker(
				_renderResponse,
				SetUtil.fromArray(
					_analyticsConfiguration.syncedOrganizationIds())));

		return organizationSearch;
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/analytics_settings/edit_synced_contacts_organizations"
		).buildPortletURL();
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
			"organization-order-by-col", "organization-name");

		return _orderByCol;
	}

	private LinkedHashMap<String, Object> _getOrganizationParams() {
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
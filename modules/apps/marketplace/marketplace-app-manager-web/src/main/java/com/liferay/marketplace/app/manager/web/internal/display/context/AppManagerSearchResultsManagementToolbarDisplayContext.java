/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.app.manager.web.internal.display.context;

import com.liferay.marketplace.app.manager.web.internal.util.MarketplaceAppManagerSearchUtil;
import com.liferay.marketplace.app.manager.web.internal.util.comparator.MarketplaceAppManagerComparator;
import com.liferay.marketplace.util.BundleManagerUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Pei-Jung Lan
 */
public class AppManagerSearchResultsManagementToolbarDisplayContext
	extends BaseAppManagerManagementToolbarDisplayContext {

	public AppManagerSearchResultsManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse);
	}

	public String getKeywords() {
		return ParamUtil.getString(httpServletRequest, "keywords");
	}

	@Override
	public PortletURL getPortletURL() {
		PortletURL portletURL = PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setMVCPath(
			"/view_search_results.jsp"
		).setRedirect(
			ParamUtil.getString(
				httpServletRequest, "redirect",
				String.valueOf(liferayPortletResponse.createRenderURL()))
		).setKeywords(
			() -> {
				if (Validator.isNotNull(getKeywords())) {
					return getKeywords();
				}

				return null;
			}
		).setParameter(
			"category", getCategory()
		).setParameter(
			"orderByType", getOrderByType()
		).setParameter(
			"state", getState()
		).buildPortletURL();

		if (_searchContainer != null) {
			portletURL.setParameter(
				_searchContainer.getCurParam(),
				String.valueOf(_searchContainer.getCur()));
			portletURL.setParameter(
				_searchContainer.getDeltaParam(),
				String.valueOf(_searchContainer.getDelta()));
		}

		return portletURL;
	}

	@Override
	public SearchContainer<Object> getSearchContainer() throws Exception {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		SearchContainer<Object> searchContainer = new SearchContainer(
			liferayPortletRequest, getPortletURL(), null,
			"no-results-were-found");

		searchContainer.setOrderByCol(getOrderByCol());
		searchContainer.setOrderByType(getOrderByType());
		searchContainer.setResultsAndTotal(
			ListUtil.sort(
				MarketplaceAppManagerSearchUtil.getResults(
					BundleManagerUtil.getBundles(), getKeywords(),
					httpServletRequest.getLocale()),
				new MarketplaceAppManagerComparator(getOrderByType())));

		_searchContainer = searchContainer;

		return _searchContainer;
	}

	private SearchContainer<Object> _searchContainer;

}
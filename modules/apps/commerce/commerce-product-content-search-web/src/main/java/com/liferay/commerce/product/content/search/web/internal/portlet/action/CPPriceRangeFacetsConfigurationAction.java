/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.search.web.internal.portlet.action;

import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.content.search.web.internal.display.context.CPPriceRangeFacetsDisplayContext;
import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;
import com.liferay.portlet.display.template.portlet.action.BaseConfigurationAction;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 */
@Component(
	property = "jakarta.portlet.name=" + CPPortletKeys.CP_PRICE_RANGE_FACETS,
	service = ConfigurationAction.class
)
public class CPPriceRangeFacetsConfigurationAction
	extends BaseConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		CPRequestHelper cpRequestHelper = new CPRequestHelper(
			httpServletRequest);

		PortletSharedSearchResponse portletSharedSearchResponse =
			_portletSharedSearchRequest.search(
				cpRequestHelper.getRenderRequest());

		try {
			CPPriceRangeFacetsDisplayContext cpPriceRangeFacetsDisplayContext =
				new CPPriceRangeFacetsDisplayContext(
					_commercePriceFormatter, _configurationProvider,
					cpRequestHelper.getRenderRequest(), null,
					_getPaginationStartParameterName(
						portletSharedSearchResponse),
					portletSharedSearchResponse);

			httpServletRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				cpPriceRangeFacetsDisplayContext);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return "/price_range_facets/configuration.jsp";
	}

	private String _getPaginationStartParameterName(
		PortletSharedSearchResponse portletSharedSearchResponse) {

		SearchResponse searchResponse =
			portletSharedSearchResponse.getSearchResponse();

		SearchRequest searchRequest = searchResponse.getRequest();

		return searchRequest.getPaginationStartParameterName();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPPriceRangeFacetsConfigurationAction.class);

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private PortletSharedSearchRequest _portletSharedSearchRequest;

}
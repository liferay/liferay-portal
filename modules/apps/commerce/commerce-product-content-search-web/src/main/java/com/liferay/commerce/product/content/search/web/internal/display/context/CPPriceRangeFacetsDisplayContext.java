/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.search.web.internal.display.context;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.product.content.search.web.internal.configuration.CPPriceRangeFacetsPortletInstanceConfiguration;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.search.facet.util.RangeParserUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;

import jakarta.portlet.RenderRequest;

import java.math.BigDecimal;

/**
 * @author Alec Sloan
 */
public class CPPriceRangeFacetsDisplayContext {

	public CPPriceRangeFacetsDisplayContext(
			CommercePriceFormatter commercePriceFormatter,
			ConfigurationProvider configurationProvider,
			RenderRequest renderRequest, Facet facet,
			String paginationStartParameterName,
			PortletSharedSearchResponse portletSharedSearchResponse)
		throws PortalException {

		_commercePriceFormatter = commercePriceFormatter;
		_configurationProvider = configurationProvider;
		_renderRequest = renderRequest;
		_facet = facet;
		_paginationStartParameterName = paginationStartParameterName;
		_portletSharedSearchResponse = portletSharedSearchResponse;

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_cpPriceRangeFacetsPortletInstanceConfiguration =
			configurationProvider.getPortletInstanceConfiguration(
				CPPriceRangeFacetsPortletInstanceConfiguration.class,
				_themeDisplay);
	}

	public String getCurrentCommerceCurrencySymbol() throws PortalException {
		CommerceContext commerceContext =
			(CommerceContext)_renderRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		CommerceCurrency commerceCurrency =
			commerceContext.getCommerceCurrency();

		return commerceCurrency.getSymbol();
	}

	public Facet getFacet() {
		return _facet;
	}

	public String getPaginationStartParameterName() {
		return _paginationStartParameterName;
	}

	public String getPriceRangeLabel(String facetTerm) throws PortalException {
		CommerceContext commerceContext =
			(CommerceContext)_renderRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		CommerceCurrency commerceCurrency =
			commerceContext.getCommerceCurrency();

		String[] priceRange = RangeParserUtil.parserRange(facetTerm);

		String formattedRangeLow = _commercePriceFormatter.format(
			commerceCurrency, new BigDecimal(priceRange[0]),
			_themeDisplay.getLocale());

		if (Double.valueOf(priceRange[1]) == Double.MAX_VALUE) {
			return formattedRangeLow + StringPool.PLUS;
		}

		String formattedRangeHigh = _commercePriceFormatter.format(
			commerceCurrency, new BigDecimal(priceRange[1]),
			_themeDisplay.getLocale());

		return StringBundler.concat(
			formattedRangeLow, " - ", formattedRangeHigh);
	}

	public String getRangesJSONArrayString() {
		return _cpPriceRangeFacetsPortletInstanceConfiguration.
			rangesJSONArrayString();
	}

	public boolean hasCommerceChannel() throws PortalException {
		CommerceContext commerceContext =
			(CommerceContext)_renderRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		if (commerceContext == null) {
			return false;
		}

		long commerceChannelId = commerceContext.getCommerceChannelId();

		if (commerceChannelId > 0) {
			return true;
		}

		return false;
	}

	public boolean isCPPriceRangeValueSelected(
			String fieldName, String fieldValue)
		throws PortalException {

		return ArrayUtil.contains(
			_portletSharedSearchResponse.getParameterValues(
				fieldName, _renderRequest),
			fieldValue);
	}

	public boolean isFacetVisible() {
		if (_facet == null) {
			return false;
		}

		FacetCollector facetCollector = _facet.getFacetCollector();

		for (TermCollector termCollector : facetCollector.getTermCollectors()) {
			if (termCollector.getFrequency() > 0) {
				return true;
			}
		}

		return false;
	}

	public boolean isShowClear(String fieldName) {
		String[] parameterValues =
			_portletSharedSearchResponse.getParameterValues(
				fieldName, _renderRequest);

		if (parameterValues != null) {
			return true;
		}

		return false;
	}

	public boolean isStagingEnabled() {
		Group group = _themeDisplay.getScopeGroup();

		return group.isStaged();
	}

	public boolean showInputRange() {
		return _cpPriceRangeFacetsPortletInstanceConfiguration.showInputRange();
	}

	private final CommercePriceFormatter _commercePriceFormatter;
	private final ConfigurationProvider _configurationProvider;
	private final CPPriceRangeFacetsPortletInstanceConfiguration
		_cpPriceRangeFacetsPortletInstanceConfiguration;
	private final Facet _facet;
	private final String _paginationStartParameterName;
	private final PortletSharedSearchResponse _portletSharedSearchResponse;
	private final RenderRequest _renderRequest;
	private final ThemeDisplay _themeDisplay;

}
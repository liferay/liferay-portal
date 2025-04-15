/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.engine.internal.frontend.data.set.provider;

import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.exception.CommerceShippingEngineException;
import com.liferay.commerce.model.CommerceShippingEngine;
import com.liferay.commerce.model.CommerceShippingOption;
import com.liferay.commerce.shipping.engine.internal.constants.FunctionCommerceShippingEngineFDSNames;
import com.liferay.commerce.util.CommerceShippingEngineRegistry;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(
	property = "fds.data.provider.key=" + FunctionCommerceShippingEngineFDSNames.FUNCTION_COMMERCE_SHIPPING_ENGINE_OPTIONS,
	service = FDSDataProvider.class
)
public class FunctionCommerceShippingEngineOptionsPageFDSDataProvider
	implements FDSDataProvider<CommerceShippingOption> {

	@Override
	public List<CommerceShippingOption> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		return _getCommerceShippingOptions(httpServletRequest);
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		List<CommerceShippingOption> commerceShippingOptions =
			_getCommerceShippingOptions(httpServletRequest);

		return commerceShippingOptions.size();
	}

	private List<CommerceShippingOption> _getCommerceShippingOptions(
			HttpServletRequest httpServletRequest)
		throws CommerceShippingEngineException {

		String commerceShippingMethodEngineKey = ParamUtil.getString(
			httpServletRequest, "commerceShippingMethodEngineKey");

		CommerceShippingEngine commerceShippingEngine =
			_commerceShippingEngineRegistry.getCommerceShippingEngine(
				commerceShippingMethodEngineKey);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long commerceChannelId = ParamUtil.getLong(
			httpServletRequest, "commerceChannelId");

		CommerceContext commerceContext = _commerceContextFactory.create(
			0, commerceChannelId, null, 0, themeDisplay.getCompanyId());

		return commerceShippingEngine.getCommerceShippingOptions(
			commerceContext, null, themeDisplay.getLocale());
	}

	@Reference
	private CommerceContextFactory _commerceContextFactory;

	@Reference
	private CommerceShippingEngineRegistry _commerceShippingEngineRegistry;

}
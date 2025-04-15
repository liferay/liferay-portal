/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.channel.web.internal.frontend.data.set.provider;

import com.liferay.commerce.channel.web.internal.constants.CommerceChannelFDSNames;
import com.liferay.commerce.channel.web.internal.frontend.util.CommerceChannelClayTableUtil;
import com.liferay.commerce.channel.web.internal.model.ShippingMethod;
import com.liferay.commerce.model.CommerceShippingEngine;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.commerce.service.CommerceShippingMethodService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Victor Silvestre
 */
@Component(
	property = "fds.data.provider.key=" + CommerceChannelFDSNames.SHIPPING_METHOD,
	service = FDSDataProvider.class
)
public class CommerceShippingMethodFDSDataProvider
	implements FDSDataProvider<ShippingMethod> {

	@Override
	public List<ShippingMethod> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long commerceChannelId = ParamUtil.getLong(
			httpServletRequest, "commerceChannelId");

		CommerceChannel commerceChannel =
			_commerceChannelService.getCommerceChannel(commerceChannelId);

		Map<String, CommerceShippingEngine> commerceShippingEngines =
			_commerceShippingEngineRegistry.getCommerceShippingEngines();

		List<ShippingMethod> shippingMethods = new ArrayList<>();

		for (Map.Entry<String, CommerceShippingEngine> entry :
				commerceShippingEngines.entrySet()) {

			CommerceShippingEngine commerceShippingEngine = entry.getValue();

			CommerceShippingMethod commerceShippingMethod =
				_commerceShippingMethodService.fetchCommerceShippingMethod(
					commerceChannel.getGroupId(), entry.getKey());

			String commerceShippingDescription =
				commerceShippingEngine.getDescription(themeDisplay.getLocale());
			String commerceShippingName = commerceShippingEngine.getName(
				themeDisplay.getLocale());

			shippingMethods.add(
				new ShippingMethod(
					commerceShippingDescription, entry.getKey(),
					commerceShippingName,
					commerceShippingEngine.getName(themeDisplay.getLocale()),
					CommerceChannelClayTableUtil.getLabelField(
						_isActive(commerceShippingMethod),
						themeDisplay.getLocale())));
		}

		return shippingMethods;
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		Map<String, CommerceShippingEngine> commerceShippingEngines =
			_commerceShippingEngineRegistry.getCommerceShippingEngines();

		return commerceShippingEngines.size();
	}

	private boolean _isActive(CommerceShippingMethod commerceShippingMethod) {
		if (commerceShippingMethod == null) {
			return false;
		}

		return commerceShippingMethod.isActive();
	}

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private CommerceShippingEngineRegistry _commerceShippingEngineRegistry;

	@Reference
	private CommerceShippingMethodService _commerceShippingMethodService;

}
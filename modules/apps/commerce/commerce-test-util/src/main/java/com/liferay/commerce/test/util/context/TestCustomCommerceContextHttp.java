/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.test.util.context;

import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.commerce.context.BaseCommerceContextHttp;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.product.discovery.CPConfigurationListDiscovery;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.util.CommerceAccountHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Alec Sloan
 */
public class TestCustomCommerceContextHttp extends BaseCommerceContextHttp {

	public TestCustomCommerceContextHttp(
		HttpServletRequest httpServletRequest,
		AccountGroupLocalService accountGroupLocalService,
		CommerceAccountHelper commerceAccountHelper,
		CommerceCatalogLocalService commerceCatalogLocalService,
		CommerceChannelAccountEntryRelLocalService
			commerceChannelAccountEntryRelLocalService,
		CommerceChannelLocalService commerceChannelLocalService,
		CommerceCurrencyLocalService commerceCurrencyLocalService,
		CommerceOrderHttpHelper commerceOrderHttpHelper,
		ConfigurationProvider configurationProvider,
		CPConfigurationListDiscovery cpConfigurationListDiscovery,
		Portal portal) {

		super(
			httpServletRequest, accountGroupLocalService, commerceAccountHelper,
			commerceCatalogLocalService,
			commerceChannelAccountEntryRelLocalService,
			commerceChannelLocalService, commerceCurrencyLocalService,
			commerceOrderHttpHelper, configurationProvider,
			cpConfigurationListDiscovery, portal);

		_httpServletRequest = httpServletRequest;
		_commerceCurrencyLocalService = commerceCurrencyLocalService;
		_portal = portal;
	}

	@Override
	public CommerceCurrency getCommerceCurrency() throws PortalException {
		if (_commerceCurrency != null) {
			return _commerceCurrency;
		}

		CommerceOrder commerceOrder = getCommerceOrder();

		if (commerceOrder != null) {
			return commerceOrder.getCommerceCurrency();
		}

		String commerceCurrencyCode = CookiesManagerUtil.getCookieValue(
			CommerceCurrency.class.getName() + StringPool.POUND +
				getCommerceChannelGroupId(),
			_httpServletRequest);

		if (!Validator.isBlank(commerceCurrencyCode)) {
			_commerceCurrency =
				_commerceCurrencyLocalService.fetchCommerceCurrency(
					_portal.getCompanyId(_httpServletRequest),
					commerceCurrencyCode);

			if ((_commerceCurrency != null) && _commerceCurrency.isActive()) {
				return _commerceCurrency;
			}
		}

		long companyId = _portal.getCompanyId(_httpServletRequest);

		_commerceCurrency = _commerceCurrencyLocalService.getCommerceCurrency(
			companyId, "USD");

		if (_commerceCurrency == null) {
			_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
				companyId, "USD");
		}

		return _commerceCurrency;
	}

	private CommerceCurrency _commerceCurrency;
	private final CommerceCurrencyLocalService _commerceCurrencyLocalService;
	private final HttpServletRequest _httpServletRequest;
	private final Portal _portal;

}
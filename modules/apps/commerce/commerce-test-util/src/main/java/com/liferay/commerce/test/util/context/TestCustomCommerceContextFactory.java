/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.test.util.context;

import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.product.discovery.CPConfigurationListDiscovery;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.util.CommerceAccountHelper;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 */
@Component(
	property = "service.ranking:Integer=1",
	service = CommerceContextFactory.class
)
public class TestCustomCommerceContextFactory
	implements CommerceContextFactory {

	@Override
	public CommerceContext create(HttpServletRequest httpServletRequest) {
		return new TestCustomCommerceContextHttp(
			httpServletRequest, _accountGroupLocalService,
			_commerceAccountHelper, _commerceCatalogLocalService,
			_commerceChannelAccountEntryRelLocalService,
			_commerceChannelLocalService, _commerceCurrencyLocalService,
			_commerceOrderHttpHelper, _configurationProvider,
			_cpConfigurationListDiscovery, _portal);
	}

	@Override
	public CommerceContext create(
		long commerceAccountId, long commerceChannelGroupId,
		String commerceCurrencyCode, long commerceOrderId, long companyId) {

		return new TestCustomCommerceContext(
			_accountEntryLocalService, _accountGroupLocalService,
			commerceAccountId, _commerceCatalogLocalService,
			_commerceChannelAccountEntryRelLocalService, commerceChannelGroupId,
			_commerceChannelLocalService, commerceCurrencyCode,
			_commerceCurrencyLocalService, commerceOrderId,
			_commerceOrderService, companyId, _configurationProvider,
			_cpConfigurationListDiscovery);
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AccountGroupLocalService _accountGroupLocalService;

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Reference
	private CommerceChannelAccountEntryRelLocalService
		_commerceChannelAccountEntryRelLocalService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Reference
	private CommerceOrderHttpHelper _commerceOrderHttpHelper;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CPConfigurationListDiscovery _cpConfigurationListDiscovery;

	@Reference
	private Portal _portal;

}
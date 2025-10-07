/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.fragment.internal.renderer;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.configuration.CommerceOrderCheckoutConfiguration;
import com.liferay.commerce.configuration.CommerceOrderFieldsConfiguration;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.fragment.internal.constants.CommerceFragmentCollectionKeys;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.util.HashMapBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Francesco Acciaro
 * @author Michele Vigilante
 * @author Alessio Antonio Rendina
 */
@Component(service = FragmentRenderer.class)
public class CreateOrderButtonFragmentRenderer
	extends BaseComponentFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return CommerceFragmentCollectionKeys.
			COMMERCE_ACCOUNT_SELECTOR_FRAGMENTS;
	}

	@Override
	public String getIcon() {
		return "button";
	}

	@Override
	public boolean isSelectable(HttpServletRequest httpServletRequest) {
		return FeatureFlagManagerUtil.isEnabled("LPD-58472");
	}

	@Override
	protected String getConfigurationPath() {
		return "create_order_button/dependencies/configuration.json";
	}

	@Override
	protected String getLabelKey() {
		return "create-order-button";
	}

	@Override
	protected String getModule() {
		return "{CreateOrder} from commerce-frontend-js";
	}

	@Override
	protected Map<String, Object> getProps(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest)
		throws Exception {

		CommerceContext commerceContext =
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		return HashMapBuilder.<String, Object>putAll(
			getConfigurationValuesMap(fragmentRendererContext)
		).put(
			"addCommerceOrderURL",
			_commerceOrderHttpHelper.getCommerceCartBaseURL(httpServletRequest)
		).put(
			"commerceChannelId", commerceContext.getCommerceChannelId()
		).put(
			"currencyCode",
			() -> {
				CommerceCurrency commerceCurrency =
					commerceContext.getCommerceCurrency();

				if (commerceCurrency == null) {
					return null;
				}

				return commerceCurrency.getCode();
			}
		).put(
			"currentCommerceAccountId",
			() -> {
				AccountEntry accountEntry = commerceContext.getAccountEntry();

				if (accountEntry == null) {
					return null;
				}

				return accountEntry.getAccountEntryId();
			}
		).put(
			"hasAddCommerceOrderPermission",
			_hasAddCommerceOrderPermission(commerceContext)
		).build();
	}

	private boolean _hasAddCommerceOrderPermission(
			CommerceContext commerceContext)
		throws Exception {

		AccountEntry accountEntry = commerceContext.getAccountEntry();

		if (accountEntry == null) {
			return false;
		}

		CommerceOrderFieldsConfiguration commerceOrderFieldsConfiguration =
			_configurationProvider.getConfiguration(
				CommerceOrderFieldsConfiguration.class,
				new GroupServiceSettingsLocator(
					commerceContext.getCommerceChannelGroupId(),
					CommerceConstants.SERVICE_NAME_COMMERCE_ORDER_FIELDS));

		int commerceOrdersCount =
			(int)_commerceOrderService.getPendingCommerceOrdersCount(
				accountEntry.getCompanyId(),
				commerceContext.getCommerceChannelGroupId());

		if ((commerceOrderFieldsConfiguration.accountCartMaxAllowed() > 0) &&
			(commerceOrdersCount >=
				commerceOrderFieldsConfiguration.accountCartMaxAllowed())) {

			return false;
		}

		CommerceOrderCheckoutConfiguration commerceOrderCheckoutConfiguration =
			_configurationProvider.getConfiguration(
				CommerceOrderCheckoutConfiguration.class,
				new GroupServiceSettingsLocator(
					commerceContext.getCommerceChannelGroupId(),
					CommerceConstants.SERVICE_NAME_COMMERCE_ORDER));

		if (accountEntry.isGuestAccount() &&
			commerceOrderCheckoutConfiguration.guestCheckoutEnabled()) {

			return true;
		}

		return _commerceOrderPortletResourcePermission.contains(
			PermissionThreadLocal.getPermissionChecker(),
			accountEntry.getAccountEntryGroupId(),
			CommerceOrderActionKeys.ADD_COMMERCE_ORDER);
	}

	@Reference
	private CommerceOrderHttpHelper _commerceOrderHttpHelper;

	@Reference(
		target = "(resource.name=" + CommerceOrderConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _commerceOrderPortletResourcePermission;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private ConfigurationProvider _configurationProvider;

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.fragment.internal.renderer;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.fragment.internal.constants.CommerceFragmentCollectionKeys;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.Cart;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Account;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = FragmentRenderer.class)
public class AccountSelectorButtonFragmentRenderer
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
		return "account_selector_button/dependencies/configuration.json";
	}

	@Override
	protected String getLabelKey() {
		return "account-selector-button";
	}

	@Override
	protected String getModule() {
		return "{AccountSelectorButton} from commerce-fragment-impl";
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
			"account",
			() -> {
				AccountEntry accountEntry = commerceContext.getAccountEntry();

				if (accountEntry == null) {
					return null;
				}

				return _accountDTOConverter.toDTO(
					new DefaultDTOConverterContext(
						_dtoConverterRegistry, accountEntry.getAccountEntryId(),
						fragmentRendererContext.getLocale(), null,
						_portal.getUser(httpServletRequest)));
			}
		).put(
			"order",
			() -> {
				CommerceOrder commerceOrder =
					commerceContext.getCommerceOrder();

				if (commerceOrder == null) {
					return null;
				}

				return _cartDTOConverter.toDTO(
					new DefaultDTOConverterContext(
						_dtoConverterRegistry,
						commerceOrder.getCommerceOrderId(),
						fragmentRendererContext.getLocale(), null,
						_portal.getUser(httpServletRequest)));
			}
		).build();
	}

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter.AccountDTOConverter)"
	)
	private DTOConverter<AccountEntry, Account> _accountDTOConverter;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.delivery.cart.internal.dto.v1_0.converter.CartDTOConverter)"
	)
	private DTOConverter<CommerceOrder, Cart> _cartDTOConverter;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private Portal _portal;

}
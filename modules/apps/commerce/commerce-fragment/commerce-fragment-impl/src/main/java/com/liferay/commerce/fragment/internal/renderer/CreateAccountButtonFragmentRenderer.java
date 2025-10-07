/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.fragment.internal.renderer;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.fragment.internal.constants.CommerceFragmentCollectionKeys;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Francesco Acciaro
 * @author Michele Vigilante
 * @author Alessio Antonio Rendina
 */
@Component(service = FragmentRenderer.class)
public class CreateAccountButtonFragmentRenderer
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
		return "create_account_button/dependencies/configuration.json";
	}

	@Override
	protected String getLabelKey() {
		return "create-account-button";
	}

	@Override
	protected String getModule() {
		return "{CreateAccount} from commerce-frontend-js";
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
			"accountEntryAllowedTypes",
			commerceContext.getAccountEntryAllowedTypes()
		).put(
			"commerceChannelId", commerceContext.getCommerceChannelId()
		).put(
			"currentAccountURL",
			PortalUtil.getPortalURL(httpServletRequest) +
				PortalUtil.getPathContext() +
					"/o/commerce-ui/set-current-account"
		).put(
			"hasAddAccountsPermission",
			PortalPermissionUtil.contains(
				PermissionThreadLocal.getPermissionChecker(),
				AccountActionKeys.ADD_ACCOUNT_ENTRY)
		).build();
	}

}
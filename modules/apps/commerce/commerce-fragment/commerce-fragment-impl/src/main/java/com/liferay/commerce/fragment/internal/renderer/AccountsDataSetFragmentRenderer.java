/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.fragment.internal.renderer;

import com.liferay.commerce.fragment.internal.constants.CommerceFragmentCollectionKeys;
import com.liferay.commerce.fragment.internal.display.context.AccountsDataSetDisplayContext;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = FragmentRenderer.class)
public class AccountsDataSetFragmentRenderer
	extends BaseJSPFragmentRenderer<AccountsDataSetDisplayContext> {

	@Override
	public String getCollectionKey() {
		return CommerceFragmentCollectionKeys.
			COMMERCE_ACCOUNT_SELECTOR_FRAGMENTS;
	}

	@Override
	public String getIcon() {
		return "catalog";
	}

	@Override
	public boolean isSelectable(HttpServletRequest httpServletRequest) {
		return FeatureFlagManagerUtil.isEnabled(
			portal.getCompanyId(httpServletRequest), "LPD-58472");
	}

	@Override
	protected String getConfigurationPath() {
		return "accounts_data_set/dependencies/configuration.json";
	}

	@Override
	protected AccountsDataSetDisplayContext getDisplayContext(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest) {

		return new AccountsDataSetDisplayContext(
			getConfigurationValuesMap(fragmentRendererContext),
			httpServletRequest, language, portal);
	}

	@Override
	protected String getLabelKey() {
		return "accounts-data-set";
	}

}
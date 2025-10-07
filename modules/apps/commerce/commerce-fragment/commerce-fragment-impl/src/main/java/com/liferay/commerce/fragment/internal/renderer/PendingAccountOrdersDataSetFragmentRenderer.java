/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.fragment.internal.renderer;

import com.liferay.commerce.fragment.internal.constants.CommerceFragmentCollectionKeys;
import com.liferay.commerce.fragment.internal.display.context.PendingAccountOrdersDataSetDisplayContext;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = FragmentRenderer.class)
public class PendingAccountOrdersDataSetFragmentRenderer
	extends BaseJSPFragmentRenderer<PendingAccountOrdersDataSetDisplayContext> {

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
		return FeatureFlagManagerUtil.isEnabled("LPD-58472");
	}

	@Override
	protected String getConfigurationPath() {
		return "account_orders_data_set/dependencies/configuration.json";
	}

	@Override
	protected PendingAccountOrdersDataSetDisplayContext getDisplayContext(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest) {

		return new PendingAccountOrdersDataSetDisplayContext(
			getConfigurationValuesMap(fragmentRendererContext),
			httpServletRequest, language, _portal, _portletURLFactory);
	}

	@Override
	protected String getLabelKey() {
		return "account-orders-data-set";
	}

	@Reference
	private Portal _portal;

	@Reference
	private PortletURLFactory _portletURLFactory;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.checkout.web.internal.display.context;

import com.liferay.commerce.checkout.web.internal.portlet.configuration.CommerceCheckoutPortletInstanceConfiguration;
import com.liferay.commerce.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.commerce.util.CommerceCheckoutStep;
import com.liferay.commerce.util.CommerceCheckoutStepRegistry;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.taglib.servlet.PipingServletResponseFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.PageContext;

import java.util.List;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public class CheckoutDisplayContext {

	public CheckoutDisplayContext(
			CommerceCheckoutStepRegistry commerceCheckoutStepRegistry,
			ConfigurationProvider configurationProvider,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse, Portal portal)
		throws Exception {

		_commerceCheckoutStepRegistry = commerceCheckoutStepRegistry;
		_configurationProvider = configurationProvider;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;

		_httpServletRequest = portal.getHttpServletRequest(
			liferayPortletRequest);
		_httpServletResponse = portal.getHttpServletResponse(
			liferayPortletResponse);

		CPRequestHelper cpRequestHelper = new CPRequestHelper(
			_httpServletRequest);

		_themeDisplay = cpRequestHelper.getThemeDisplay();

		_commerceOrder = (CommerceOrder)_httpServletRequest.getAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER);

		String checkoutStepName = ParamUtil.getString(
			liferayPortletRequest, "checkoutStepName");

		CommerceCheckoutStep commerceCheckoutStep =
			commerceCheckoutStepRegistry.getCommerceCheckoutStep(
				checkoutStepName);

		if ((commerceCheckoutStep == null) && (_commerceOrder != null)) {
			List<CommerceCheckoutStep> commerceCheckoutSteps =
				commerceCheckoutStepRegistry.getCommerceCheckoutSteps(
					_httpServletRequest, _httpServletResponse, true);

			commerceCheckoutStep = commerceCheckoutSteps.get(0);
		}

		_commerceCheckoutStep = commerceCheckoutStep;
	}

	public List<CommerceCheckoutStep> getCommerceCheckoutSteps()
		throws Exception {

		return _commerceCheckoutStepRegistry.getCommerceCheckoutSteps(
			_httpServletRequest, _httpServletResponse, true);
	}

	public String getCommerceOrderUuid() {
		return _commerceOrder.getUuid();
	}

	public String getCurrentCheckoutStepName() {
		return _commerceCheckoutStep.getName();
	}

	public String getPreviousCheckoutStepName() throws Exception {
		CommerceCheckoutStep commerceCheckoutStep =
			_commerceCheckoutStepRegistry.getPreviousCommerceCheckoutStep(
				_commerceCheckoutStep.getName(), _httpServletRequest,
				_httpServletResponse);

		if ((commerceCheckoutStep == null) ||
			(_commerceCheckoutStep.isOrder() &&
			 !commerceCheckoutStep.isOrder())) {

			return null;
		}

		return commerceCheckoutStep.getName();
	}

	public boolean hasCommerceChannel() throws PortalException {
		CommerceContext commerceContext =
			(CommerceContext)_httpServletRequest.getAttribute(
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

	public boolean isEmptyCommerceOrder() {
		if (_commerceOrder == null) {
			return true;
		}

		List<CommerceOrderItem> commerceOrderItems =
			_commerceOrder.getCommerceOrderItems();

		return commerceOrderItems.isEmpty();
	}

	public boolean isOrderSummaryShowFullAddressEnabled()
		throws PortalException {

		CommerceCheckoutPortletInstanceConfiguration
			commerceCheckoutPortletInstanceConfiguration =
				_configurationProvider.getPortletInstanceConfiguration(
					CommerceCheckoutPortletInstanceConfiguration.class,
					_themeDisplay);

		return commerceCheckoutPortletInstanceConfiguration.
			orderSummaryShowFullAddress();
	}

	public boolean isOrderSummaryShowPhoneNumberEnabled()
		throws PortalException {

		CommerceCheckoutPortletInstanceConfiguration
			commerceCheckoutPortletInstanceConfiguration =
				_configurationProvider.getPortletInstanceConfiguration(
					CommerceCheckoutPortletInstanceConfiguration.class,
					_themeDisplay);

		return commerceCheckoutPortletInstanceConfiguration.
			orderSummaryShowPhoneNumber();
	}

	public boolean isSennaDisabled() {
		return _commerceCheckoutStep.isSennaDisabled();
	}

	public void renderCurrentCheckoutStep(PageContext pageContext)
		throws Exception {

		_commerceCheckoutStep.render(
			_httpServletRequest,
			PipingServletResponseFactory.createPipingServletResponse(
				pageContext));
	}

	public boolean showControls() {
		return _commerceCheckoutStep.showControls(
			_httpServletRequest, _httpServletResponse);
	}

	private final CommerceCheckoutStep _commerceCheckoutStep;
	private final CommerceCheckoutStepRegistry _commerceCheckoutStepRegistry;
	private final CommerceOrder _commerceOrder;
	private final ConfigurationProvider _configurationProvider;
	private final HttpServletRequest _httpServletRequest;
	private final HttpServletResponse _httpServletResponse;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final ThemeDisplay _themeDisplay;

}
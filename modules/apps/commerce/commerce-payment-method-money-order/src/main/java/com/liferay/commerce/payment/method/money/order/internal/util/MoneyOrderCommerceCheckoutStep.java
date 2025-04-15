/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.method.money.order.internal.util;

import com.liferay.commerce.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.payment.method.money.order.internal.MoneyOrderCommercePaymentMethod;
import com.liferay.commerce.payment.method.money.order.internal.configuration.MoneyOrderGroupServiceConfiguration;
import com.liferay.commerce.payment.method.money.order.internal.constants.MoneyOrderCommercePaymentEngineMethodConstants;
import com.liferay.commerce.payment.method.money.order.internal.display.context.MoneyOrderCheckoutStepDisplayContext;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.util.BaseCommerceCheckoutStep;
import com.liferay.commerce.util.CommerceCheckoutStep;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 */
@Component(
	property = {
		"commerce.checkout.step.name=" + MoneyOrderCommerceCheckoutStep.NAME,
		"commerce.checkout.step.order:Integer=" + (Integer.MAX_VALUE - 160)
	},
	service = CommerceCheckoutStep.class
)
public class MoneyOrderCommerceCheckoutStep extends BaseCommerceCheckoutStep {

	public static final String NAME = "money-order";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean isActive(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		CommerceOrder commerceOrder = _getCommerceOrder(httpServletRequest);

		if (MoneyOrderCommercePaymentMethod.KEY.equals(
				commerceOrder.getCommercePaymentMethodKey())) {

			MoneyOrderGroupServiceConfiguration
				moneyOrderGroupServiceConfiguration =
					_configurationProvider.getConfiguration(
						MoneyOrderGroupServiceConfiguration.class,
						new GroupServiceSettingsLocator(
							commerceOrder.getGroupId(),
							MoneyOrderCommercePaymentEngineMethodConstants.
								SERVICE_NAME));

			return moneyOrderGroupServiceConfiguration.showMessagePage();
		}

		return false;
	}

	@Override
	public boolean isOrder() {
		return true;
	}

	@Override
	public boolean isVisible(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		CommerceOrder commerceOrder = _getCommerceOrder(httpServletRequest);

		MoneyOrderGroupServiceConfiguration
			moneyOrderGroupServiceConfiguration =
				_configurationProvider.getConfiguration(
					MoneyOrderGroupServiceConfiguration.class,
					new GroupServiceSettingsLocator(
						commerceOrder.getGroupId(),
						MoneyOrderCommercePaymentEngineMethodConstants.
							SERVICE_NAME));

		return moneyOrderGroupServiceConfiguration.showMessagePage();
	}

	@Override
	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		MoneyOrderCheckoutStepDisplayContext
			moneyOrderCheckoutStepDisplayContext =
				new MoneyOrderCheckoutStepDisplayContext(
					_configurationProvider, httpServletRequest);

		httpServletRequest.setAttribute(
			CommerceCheckoutWebKeys.COMMERCE_CHECKOUT_STEP_DISPLAY_CONTEXT,
			moneyOrderCheckoutStepDisplayContext);

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/checkout_step/money_order.jsp");
	}

	private CommerceOrder _getCommerceOrder(
			HttpServletRequest httpServletRequest)
		throws Exception {

		String commerceOrderUuid = ParamUtil.getString(
			httpServletRequest, "commerceOrderUuid");

		if (Validator.isNotNull(commerceOrderUuid)) {
			long groupId =
				_commerceChannelLocalService.
					getCommerceChannelGroupIdBySiteGroupId(
						_portal.getScopeGroupId(httpServletRequest));

			return _commerceOrderService.getCommerceOrderByUuidAndGroupId(
				commerceOrderUuid, groupId);
		}

		return _commerceOrderHttpHelper.getCurrentCommerceOrder(
			httpServletRequest);
	}

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceOrderHttpHelper _commerceOrderHttpHelper;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.payment.method.money.order)"
	)
	private ServletContext _servletContext;

}
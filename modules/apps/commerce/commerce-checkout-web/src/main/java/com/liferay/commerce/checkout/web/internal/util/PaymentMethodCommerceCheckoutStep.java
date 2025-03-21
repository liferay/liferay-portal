/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.checkout.web.internal.util;

import com.liferay.commerce.checkout.helper.CommerceCheckoutStepHttpHelper;
import com.liferay.commerce.checkout.web.internal.display.context.PaymentMethodCheckoutStepDisplayContext;
import com.liferay.commerce.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.exception.CommerceOrderPaymentMethodException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.payment.engine.CommercePaymentEngine;
import com.liferay.commerce.payment.integration.CommercePaymentIntegrationRegistry;
import com.liferay.commerce.payment.method.CommercePaymentMethodRegistry;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelQualifierLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.util.BaseCommerceCheckoutStep;
import com.liferay.commerce.util.CommerceCheckoutStep;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"commerce.checkout.step.name=" + PaymentMethodCommerceCheckoutStep.NAME,
		"commerce.checkout.step.order:Integer=40"
	},
	service = CommerceCheckoutStep.class
)
public class PaymentMethodCommerceCheckoutStep
	extends BaseCommerceCheckoutStep {

	public static final String NAME = "payment-method";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean isActive(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		CommerceOrder commerceOrder =
			(CommerceOrder)httpServletRequest.getAttribute(
				CommerceCheckoutWebKeys.COMMERCE_ORDER);

		return _commerceCheckoutStepHttpHelper.
			isActivePaymentMethodCommerceCheckoutStep(
				httpServletRequest, commerceOrder);
	}

	@Override
	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			_updateCommerceOrderPaymentMethod(actionRequest);
		}
		catch (Exception exception) {
			if (exception instanceof CommerceOrderPaymentMethodException) {
				SessionErrors.add(actionRequest, exception.getClass());

				return;
			}

			throw exception;
		}
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		PaymentMethodCheckoutStepDisplayContext
			paymentMethodCheckoutStepDisplayContext =
				new PaymentMethodCheckoutStepDisplayContext(
					_commercePaymentEngine, _commercePaymentIntegrationRegistry,
					_commercePaymentMethodGroupRelLocalService,
					_commercePaymentMethodGroupRelQualifierLocalService,
					_commercePaymentMethodRegistry, httpServletRequest);

		httpServletRequest.setAttribute(
			CommerceCheckoutWebKeys.COMMERCE_CHECKOUT_STEP_DISPLAY_CONTEXT,
			paymentMethodCheckoutStepDisplayContext);

		_jspRenderer.renderJSP(
			httpServletRequest, httpServletResponse,
			"/checkout_step/payment_method.jsp");
	}

	private void _updateCommerceOrderPaymentMethod(ActionRequest actionRequest)
		throws Exception {

		String commercePaymentMethodKey = ParamUtil.getString(
			actionRequest, "commercePaymentMethodKey");

		if (commercePaymentMethodKey.isEmpty()) {
			throw new CommerceOrderPaymentMethodException();
		}

		CommerceOrder commerceOrder = (CommerceOrder)actionRequest.getAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER);

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!_commerceOrderModelResourcePermission.contains(
				themeDisplay.getPermissionChecker(), commerceOrder,
				CommerceOrderActionKeys.CHECKOUT_COMMERCE_ORDER)) {

			return;
		}

		commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			null, commerceOrder.getCommerceOrderId(),
			commerceOrder.getBillingAddressId(),
			commerceOrder.getCommerceShippingMethodId(),
			commerceOrder.getShippingAddressId(),
			commerceOrder.getAdvanceStatus(), commercePaymentMethodKey, null,
			commerceOrder.getPurchaseOrderNumber(),
			commerceOrder.getShippingAmount(),
			commerceOrder.getShippingOptionName(), commerceOrder.getSubtotal(),
			commerceOrder.getTotal());

		commerceOrder = _commerceOrderLocalService.resetTermsAndConditions(
			commerceOrder.getCommerceOrderId(), false, true);

		actionRequest.setAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER, commerceOrder);
	}

	@Reference
	private CommerceCheckoutStepHttpHelper _commerceCheckoutStepHttpHelper;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private ModelResourcePermission<CommerceOrder>
		_commerceOrderModelResourcePermission;

	@Reference
	private CommercePaymentEngine _commercePaymentEngine;

	@Reference
	private CommercePaymentIntegrationRegistry
		_commercePaymentIntegrationRegistry;

	@Reference
	private CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;

	@Reference
	private CommercePaymentMethodGroupRelQualifierLocalService
		_commercePaymentMethodGroupRelQualifierLocalService;

	@Reference
	private CommercePaymentMethodRegistry _commercePaymentMethodRegistry;

	@Reference
	private JSPRenderer _jspRenderer;

}
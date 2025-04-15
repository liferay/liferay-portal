/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.checkout.web.internal.util;

import com.liferay.commerce.checkout.web.internal.display.context.PaymentProcessCheckoutStepDisplayContext;
import com.liferay.commerce.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.util.BaseCommerceCheckoutStep;
import com.liferay.commerce.util.CommerceCheckoutStep;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.headless.commerce.delivery.cart.resource.v1_0.CartResource;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.math.BigDecimal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(
	property = {
		"commerce.checkout.step.name=" + PaymentProcessCommerceCheckoutStep.NAME,
		"commerce.checkout.step.order:Integer=" + (Integer.MAX_VALUE - 90)
	},
	service = CommerceCheckoutStep.class
)
public class PaymentProcessCommerceCheckoutStep
	extends BaseCommerceCheckoutStep {

	public static final String NAME = "payment-process";

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

		if (BigDecimalUtil.lte(commerceOrder.getTotal(), BigDecimal.ZERO)) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isVisible(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		return false;
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

		PaymentProcessCheckoutStepDisplayContext
			paymentProcessCheckoutStepDisplayContext =
				new PaymentProcessCheckoutStepDisplayContext(
					_cartResourceFactory, httpServletRequest);

		// Redirection only works with the original servlet response

		HttpServletResponse originalHttpServletResponse = httpServletResponse;

		while (originalHttpServletResponse instanceof
					HttpServletResponseWrapper) {

			HttpServletResponseWrapper httpServletResponseWrapper =
				(HttpServletResponseWrapper)originalHttpServletResponse;

			originalHttpServletResponse =
				(HttpServletResponse)httpServletResponseWrapper.getResponse();
		}

		String redirect = _portal.escapeRedirect(
			paymentProcessCheckoutStepDisplayContext.getPaymentURL());

		if (Validator.isNotNull(redirect) &&
			!originalHttpServletResponse.isCommitted()) {

			originalHttpServletResponse.sendRedirect(redirect);
		}

		if (originalHttpServletResponse.isCommitted()) {
			return;
		}

		// Backend redirection has failed: fall back to frontend redirect

		httpServletRequest.setAttribute(
			CommerceCheckoutWebKeys.COMMERCE_CHECKOUT_STEP_DISPLAY_CONTEXT,
			paymentProcessCheckoutStepDisplayContext);

		_jspRenderer.renderJSP(
			httpServletRequest, httpServletResponse,
			"/checkout_step/payment_process.jsp");
	}

	@Override
	public boolean showControls(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		return false;
	}

	@Reference
	private CartResource.Factory _cartResourceFactory;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference
	private Portal _portal;

}
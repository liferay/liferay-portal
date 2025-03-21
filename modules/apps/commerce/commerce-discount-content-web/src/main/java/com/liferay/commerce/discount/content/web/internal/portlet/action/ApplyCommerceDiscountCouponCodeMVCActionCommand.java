/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.content.web.internal.portlet.action;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.discount.constants.CommerceDiscountPortletKeys;
import com.liferay.commerce.discount.exception.CommerceDiscountCouponCodeException;
import com.liferay.commerce.discount.exception.CommerceDiscountLimitationTimesException;
import com.liferay.commerce.discount.exception.CommerceDiscountValidatorException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommerceDiscountPortletKeys.COMMERCE_DISCOUNT_CONTENT_WEB,
		"mvc.command.name=/commerce_discount_content/apply_commerce_discount_coupon_code"
	},
	service = MVCActionCommand.class
)
public class ApplyCommerceDiscountCouponCodeMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			actionRequest);

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		CommerceContext commerceContext =
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		CommerceOrder commerceOrder = commerceContext.getCommerceOrder();

		if (cmd.equals(Constants.ADD)) {
			String couponCode = ParamUtil.getString(
				actionRequest, "couponCode");

			hideDefaultErrorMessage(actionRequest);

			try {
				_commerceOrderService.applyCouponCode(
					commerceOrder.getCommerceOrderId(), couponCode,
					commerceContext);
			}
			catch (CommerceDiscountCouponCodeException |
				   CommerceDiscountLimitationTimesException |
				   CommerceDiscountValidatorException exception) {

				if (exception instanceof CommerceDiscountValidatorException) {
					throw exception;
				}

				SessionErrors.add(actionRequest, exception.getClass());

				return;
			}
		}
		else if (cmd.equals(Constants.REMOVE)) {
			_commerceOrderService.applyCouponCode(
				commerceOrder.getCommerceOrderId(), null, commerceContext);
		}

		hideDefaultSuccessMessage(actionRequest);

		sendRedirect(
			actionRequest, actionResponse,
			ParamUtil.getString(actionRequest, "redirect"));
	}

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private Portal _portal;

}
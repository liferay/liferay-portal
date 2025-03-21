/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.checkout.web.internal.display.context;

import com.liferay.commerce.checkout.web.internal.display.context.helper.CommerceCheckoutRequestHelper;
import com.liferay.commerce.checkout.web.internal.util.PaymentProcessCommerceCheckoutStep;
import com.liferay.commerce.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderPayment;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderPaymentLocalService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Andrea Di Giorgi
 */
public class OrderConfirmationCheckoutStepDisplayContext {

	public OrderConfirmationCheckoutStepDisplayContext(
		CommerceChannelLocalService commerceChannelLocalService,
		CommerceOrderHttpHelper commerceOrderHttpHelper,
		CommerceOrderPaymentLocalService commerceOrderPaymentLocalService,
		CommerceOrderService commerceOrderService,
		HttpServletRequest httpServletRequest, Portal portal) {

		_commerceChannelLocalService = commerceChannelLocalService;
		_commerceOrderHttpHelper = commerceOrderHttpHelper;
		_commerceOrderPaymentLocalService = commerceOrderPaymentLocalService;
		_commerceOrderService = commerceOrderService;
		_httpServletRequest = httpServletRequest;
		_portal = portal;

		_commerceCheckoutRequestHelper = new CommerceCheckoutRequestHelper(
			httpServletRequest);
	}

	public CommerceOrder getCommerceOrder() throws PortalException {
		if (_commerceOrder != null) {
			return _commerceOrder;
		}

		_commerceOrder = (CommerceOrder)_httpServletRequest.getAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER);

		if (_commerceOrder != null) {
			return _commerceOrder;
		}

		String commerceOrderUuid = ParamUtil.getString(
			_httpServletRequest, "commerceOrderUuid");

		if (Validator.isNotNull(commerceOrderUuid)) {
			long groupId =
				_commerceChannelLocalService.
					getCommerceChannelGroupIdBySiteGroupId(
						_portal.getScopeGroupId(_httpServletRequest));

			_commerceOrder =
				_commerceOrderService.getCommerceOrderByUuidAndGroupId(
					commerceOrderUuid, groupId);
		}

		return _commerceOrder;
	}

	public CommerceOrderPayment getCommerceOrderPayment()
		throws PortalException {

		return _commerceOrderPaymentLocalService.
			fetchLatestCommerceOrderPayment(getCommerceOrderId());
	}

	public String getOrderDetailURL() throws PortalException {
		return _commerceOrderHttpHelper.getCommerceCartPortletURL(
			_httpServletRequest, getCommerceOrder());
	}

	public String getRetryPaymentURL() throws PortalException {
		return PortletURLBuilder.createRenderURL(
			_commerceCheckoutRequestHelper.getLiferayPortletResponse()
		).setParameter(
			"checkoutStepName", PaymentProcessCommerceCheckoutStep.NAME
		).setParameter(
			"commerceOrderUuid",
			() -> {
				CommerceOrder commerceOrder = getCommerceOrder();

				return commerceOrder.getUuid();
			}
		).buildString();
	}

	protected long getCommerceOrderId() throws PortalException {
		CommerceOrder commerceOrder = getCommerceOrder();

		if (commerceOrder == null) {
			return 0;
		}

		return commerceOrder.getCommerceOrderId();
	}

	private final CommerceChannelLocalService _commerceChannelLocalService;
	private final CommerceCheckoutRequestHelper _commerceCheckoutRequestHelper;
	private CommerceOrder _commerceOrder;
	private final CommerceOrderHttpHelper _commerceOrderHttpHelper;
	private final CommerceOrderPaymentLocalService
		_commerceOrderPaymentLocalService;
	private final CommerceOrderService _commerceOrderService;
	private final HttpServletRequest _httpServletRequest;
	private final Portal _portal;

}
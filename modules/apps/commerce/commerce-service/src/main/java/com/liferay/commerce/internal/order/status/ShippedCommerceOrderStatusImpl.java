/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.order.status;

import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.helper.CommerceShippingHelper;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.order.engine.CommerceOrderEngine;
import com.liferay.commerce.order.status.CommerceOrderStatus;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.BigDecimalUtil;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Alec Sloan
 */
@Component(
	property = {
		"commerce.order.status.key=" + ShippedCommerceOrderStatusImpl.KEY,
		"commerce.order.status.priority:Integer=" + ShippedCommerceOrderStatusImpl.PRIORITY
	},
	service = CommerceOrderStatus.class
)
public class ShippedCommerceOrderStatusImpl implements CommerceOrderStatus {

	public static final int KEY = CommerceOrderConstants.ORDER_STATUS_SHIPPED;

	public static final int PRIORITY = 60;

	@Override
	public CommerceOrder doTransition(
			CommerceOrder commerceOrder, long userId, boolean secure)
		throws PortalException {

		commerceOrder.setOrderStatus(KEY);

		if (!_commerceShippingHelper.isShippable(commerceOrder)) {
			commerceOrder = _commerceOrderEngine.transitionCommerceOrder(
				commerceOrder, CommerceOrderConstants.ORDER_STATUS_COMPLETED,
				userId, secure);
		}

		if (secure) {
			commerceOrder = _commerceOrderService.updateCommerceOrder(
				commerceOrder);
		}
		else {
			commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
				commerceOrder);
		}

		return commerceOrder;
	}

	@Override
	public int getKey() {
		return KEY;
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(
			locale, CommerceOrderConstants.getOrderStatusLabel(KEY));
	}

	@Override
	public int getPriority() {
		return PRIORITY;
	}

	@Override
	public boolean isComplete(CommerceOrder commerceOrder) {
		if ((commerceOrder.getOrderStatus() ==
				CommerceOrderConstants.ORDER_STATUS_SHIPPED) ||
			(commerceOrder.getOrderStatus() ==
				CommerceOrderConstants.ORDER_STATUS_COMPLETED)) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isEnabled(CommerceOrder commerceOrder)
		throws PortalException {

		return !commerceOrder.isQuote();
	}

	@Override
	public boolean isTransitionCriteriaMet(CommerceOrder commerceOrder)
		throws PortalException {

		boolean allOrderItemsShipped = true;

		for (CommerceOrderItem shippedCommerceOrderItem :
				commerceOrder.getCommerceOrderItems()) {

			if (!BigDecimalUtil.lt(
					shippedCommerceOrderItem.getShippedQuantity(),
					shippedCommerceOrderItem.getQuantity()) ||
				!shippedCommerceOrderItem.isShippable()) {

				continue;
			}

			allOrderItemsShipped = false;
		}

		if (((commerceOrder.getOrderStatus() ==
				CommerceOrderConstants.ORDER_STATUS_PROCESSING) ||
			 (commerceOrder.getOrderStatus() ==
				 CommerceOrderConstants.ORDER_STATUS_PARTIALLY_SHIPPED)) &&
			allOrderItemsShipped) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isValidForOrder(CommerceOrder commerceOrder)
		throws PortalException {

		return _commerceShippingHelper.isShippable(commerceOrder);
	}

	@Reference
	private CommerceOrderEngine _commerceOrderEngine;

	@Reference
	private volatile CommerceOrderLocalService _commerceOrderLocalService;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile CommerceOrderService _commerceOrderService;

	@Reference
	private CommerceShippingHelper _commerceShippingHelper;

	@Reference
	private Language _language;

}
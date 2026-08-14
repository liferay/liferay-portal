/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.internal.dto.v1_0.converter;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.headless.commerce.machine.learning.dto.v1_0.Order;
import com.liferay.headless.commerce.machine.learning.dto.v1_0.OrderItem;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Ferrari
 */
@Component(
	property = "dto.class.name=com.liferay.commerce.model.CommerceOrder",
	service = DTOConverter.class
)
public class OrderDTOConverter implements DTOConverter<CommerceOrder, Order> {

	@Override
	public String getContentType() {
		return Order.class.getSimpleName();
	}

	@Override
	public CommerceOrder getObject(String externalReferenceCode)
		throws Exception {

		return _commerceOrderLocalService.fetchCommerceOrder(
			GetterUtil.getLong(externalReferenceCode));
	}

	@Override
	public Order toDTO(
			DTOConverterContext dtoConverterContext,
			CommerceOrder commerceOrder)
		throws Exception {

		if (commerceOrder == null) {
			return null;
		}

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.fetchCommerceChannelByGroupClassPK(
				commerceOrder.getGroupId());

		return new Order() {
			{
				setAccountExternalReferenceCode(
					() -> {
						AccountEntry accountEntry =
							commerceOrder.getAccountEntry();

						return accountEntry.getExternalReferenceCode();
					});
				setAccountId(commerceOrder::getCommerceAccountId);
				setChannelId(commerceChannel::getCommerceChannelId);
				setCreateDate(commerceOrder::getCreateDate);
				setCurrencyCode(
					() -> {
						CommerceCurrency commerceCurrency =
							commerceOrder.getCommerceCurrency();

						return commerceCurrency.getCode();
					});
				setCustomFields(
					() -> {
						ExpandoBridge expandoBridge =
							commerceOrder.getExpandoBridge();

						return expandoBridge.getAttributes();
					});
				setExternalReferenceCode(
					commerceOrder::getExternalReferenceCode);
				setGroupId(commerceChannel::getSiteGroupId);
				setId(commerceOrder::getCommerceOrderId);
				setModifiedDate(commerceOrder::getModifiedDate);
				setOrderDate(commerceOrder::getOrderDate);
				setOrderItems(
					() -> TransformUtil.transformToArray(
						commerceOrder.getCommerceOrderItems(),
						commerceOrderItem -> _orderItemDTOConverter.toDTO(
							commerceOrderItem),
						OrderItem.class));
				setOrderStatus(commerceOrder::getOrderStatus);
				setOrderTypeId(commerceOrder::getCommerceOrderTypeId);
				setPaymentMethod(commerceOrder::getCommercePaymentMethodKey);
				setPaymentStatus(commerceOrder::getPaymentStatus);
				setStatus(commerceOrder::getStatus);
				setTotal(commerceOrder::getTotal);
				setUserId(commerceOrder::getUserId);
			}
		};
	}

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.machine.learning.internal.dto.v1_0.converter.OrderItemDTOConverter)"
	)
	private DTOConverter<CommerceOrderItem, OrderItem> _orderItemDTOConverter;

}
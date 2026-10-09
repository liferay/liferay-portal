/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.search.spi.model.index.contributor;

import com.liferay.commerce.address.CommerceAddressFormatter;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.model.CommerceShipmentItem;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceAddressLocalService;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceShipmentItemLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "indexer.class.name=com.liferay.commerce.model.CommerceShipment",
	service = ModelDocumentContributor.class
)
public class CommerceShipmentModelDocumentContributor
	implements ModelDocumentContributor<CommerceShipment> {

	@Override
	public void contribute(
		Document document, CommerceShipment commerceShipment) {

		try {
			document.addNumberSortable(
				Field.ENTRY_CLASS_PK, commerceShipment.getCommerceShipmentId());
			document.addKeyword(Field.STATUS, commerceShipment.getStatus());
			document.addKeyword("carrier", commerceShipment.getCarrier());
			document.addTextSortable("carrier", commerceShipment.getCarrier());
			document.addKeyword(
				"commerceAccountId", commerceShipment.getCommerceAccountId());
			document.addKeyword(
				"commerceAccountName", commerceShipment.getAccountEntryName(),
				true);

			CommerceChannel commerceChannel =
				_commerceChannelLocalService.getCommerceChannelByOrderGroupId(
					commerceShipment.getGroupId());

			document.addKeyword(
				"commerceChannelId", commerceChannel.getCommerceChannelId());
			document.addKeyword(
				"commerceChannelName", commerceChannel.getName(), true);

			Set<Long> commerceOrderIds = _getCommerceOrderIds(
				commerceShipment.getCommerceShipmentId());

			document.addKeyword(
				"commerceOrderIds", ArrayUtil.toStringArray(commerceOrderIds));

			Set<String> commerceOrderUserIds = new HashSet<>();

			for (long commerceOrderId : commerceOrderIds) {
				CommerceOrder commerceOrder =
					_commerceOrderLocalService.fetchCommerceOrder(
						commerceOrderId);

				if (commerceOrder == null) {
					continue;
				}

				commerceOrderUserIds.add(
					String.valueOf(commerceOrder.getUserId()));
			}

			document.addKeyword(
				"commerceOrderUserIds",
				commerceOrderUserIds.toArray(new String[0]));

			Date expectedDate = commerceShipment.getExpectedDate();

			if (expectedDate != null) {
				document.addDate(
					"expectedDate", commerceShipment.getExpectedDate());
				document.addDateSortable(
					"expectedDate", commerceShipment.getExpectedDate());
			}

			document.addNumber(
				"itemsCount",
				_commerceShipmentItemLocalService.getCommerceShipmentItemsCount(
					commerceShipment.getCommerceShipmentId()));

			CommerceAddress commerceAddress =
				_commerceAddressLocalService.fetchCommerceAddress(
					commerceShipment.getCommerceAddressId());

			if (commerceAddress != null) {
				document.addKeyword(
					"oneLineAddress",
					_commerceAddressFormatter.getOneLineAddress(
						commerceAddress));
			}

			Date shippingDate = commerceShipment.getShippingDate();

			if (shippingDate != null) {
				document.addDate(
					"shippingDate", commerceShipment.getShippingDate());
				document.addDateSortable(
					"shippingDate", commerceShipment.getShippingDate());
			}

			document.addKeyword(
				"trackingNumber", commerceShipment.getTrackingNumber());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to index commerce shipment " +
						commerceShipment.getCommerceShipmentId(),
					exception);
			}
		}
	}

	private Set<Long> _getCommerceOrderIds(long commerceShipmentId) {
		Set<Long> commerceOrderIds = new HashSet<>();

		for (CommerceShipmentItem commerceShipmentItem :
				_commerceShipmentItemLocalService.getCommerceShipmentItems(
					commerceShipmentId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			CommerceOrderItem commerceOrderItem =
				_commerceOrderItemLocalService.fetchCommerceOrderItem(
					commerceShipmentItem.getCommerceOrderItemId());

			if (commerceOrderItem == null) {
				continue;
			}

			commerceOrderIds.add(commerceOrderItem.getCommerceOrderId());
		}

		return commerceOrderIds;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceShipmentModelDocumentContributor.class);

	@Reference
	private CommerceAddressFormatter _commerceAddressFormatter;

	@Reference
	private CommerceAddressLocalService _commerceAddressLocalService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference
	private CommerceShipmentItemLocalService _commerceShipmentItemLocalService;

}
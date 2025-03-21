/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipment.web.internal.frontend.data.set.provider;

import com.liferay.commerce.constants.CommerceShipmentFDSNames;
import com.liferay.commerce.frontend.model.ShipmentItem;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseService;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.service.CommerceShipmentItemService;
import com.liferay.commerce.util.CommerceQuantityFormatter;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommerceShipmentFDSNames.SHIPMENT_ITEMS,
	service = FDSDataProvider.class
)
public class CommerceShipmentItemFDSDataProvider
	implements FDSDataProvider<ShipmentItem> {

	@Override
	public List<ShipmentItem> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		long commerceShipmentId = ParamUtil.getLong(
			httpServletRequest, "commerceShipmentId");

		return TransformUtil.transform(
			_commerceShipmentItemService.getCommerceShipmentItems(
				commerceShipmentId, fdsPagination.getStartPosition(),
				fdsPagination.getEndPosition(), null),
			commerceShipmentItem -> {
				CommerceOrderItem commerceOrderItem =
					_commerceOrderItemService.getCommerceOrderItem(
						commerceShipmentItem.getCommerceOrderItemId());

				CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
					_cpInstanceUnitOfMeasureLocalService.
						fetchCPInstanceUnitOfMeasure(
							commerceOrderItem.getCPInstanceId(),
							commerceOrderItem.getUnitOfMeasureKey());

				String commerceInventoryWarehouseName = StringPool.BLANK;

				if (commerceShipmentItem.getCommerceInventoryWarehouseId() >
						0) {

					try {
						CommerceInventoryWarehouse commerceInventoryWarehouse =
							_commerceInventoryWarehouseService.
								fetchByCommerceInventoryWarehouse(
									commerceShipmentItem.
										getCommerceInventoryWarehouseId());

						if (commerceInventoryWarehouse != null) {
							commerceInventoryWarehouseName =
								commerceInventoryWarehouse.getName(
									_portal.getLocale(httpServletRequest));
						}
					}
					catch (Exception exception) {
						if (_log.isDebugEnabled()) {
							_log.debug(exception);
						}
					}
				}

				BigDecimal quantity = commerceOrderItem.getQuantity();
				BigDecimal shipmentItemQuantity =
					commerceShipmentItem.getQuantity();
				BigDecimal shippedQuantity =
					commerceOrderItem.getShippedQuantity();

				return new ShipmentItem(
					commerceShipmentItem.getExternalReferenceCode(),
					commerceOrderItem.getCommerceOrderId(),
					_commerceQuantityFormatter.format(
						cpInstanceUnitOfMeasure,
						quantity.subtract(shippedQuantity)),
					commerceShipmentItem.getCommerceShipmentItemId(),
					_commerceQuantityFormatter.format(
						cpInstanceUnitOfMeasure, shippedQuantity),
					commerceOrderItem.getSku(),
					_commerceQuantityFormatter.format(
						cpInstanceUnitOfMeasure, shipmentItemQuantity),
					commerceOrderItem.getUnitOfMeasureKey(),
					commerceInventoryWarehouseName);
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long commerceShipmentId = ParamUtil.getLong(
			httpServletRequest, "commerceShipmentId");

		return _commerceShipmentItemService.getCommerceShipmentItemsCount(
			commerceShipmentId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceShipmentItemFDSDataProvider.class);

	@Reference
	private CommerceInventoryWarehouseService
		_commerceInventoryWarehouseService;

	@Reference
	private CommerceOrderItemService _commerceOrderItemService;

	@Reference
	private CommerceQuantityFormatter _commerceQuantityFormatter;

	@Reference
	private CommerceShipmentItemService _commerceShipmentItemService;

	@Reference
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

	@Reference
	private Portal _portal;

}
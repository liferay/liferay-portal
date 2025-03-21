/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipment.web.internal.frontend.data.set.provider;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.constants.CommerceShipmentFDSNames;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseItem;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseItemLocalService;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseItemService;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseLocalService;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceShipmentItem;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.service.CommerceShipmentItemLocalService;
import com.liferay.commerce.service.CommerceShipmentItemService;
import com.liferay.commerce.shipment.web.internal.model.Warehouse;
import com.liferay.commerce.shipment.web.internal.model.WarehouseItem;
import com.liferay.commerce.util.CommerceQuantityFormatter;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 */
@Component(
	property = "fds.data.provider.key=" + CommerceShipmentFDSNames.INVENTORY_WAREHOUSE_ITEM,
	service = FDSDataProvider.class
)
public class CommerceInventoryWarehouseItemFDSDataProvider
	implements FDSDataProvider<Warehouse> {

	@Override
	public List<Warehouse> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		List<Warehouse> warehouses = new ArrayList<>();

		long companyId = _portal.getCompanyId(httpServletRequest);

		long commerceShipmentItemId = ParamUtil.getLong(
			httpServletRequest, "commerceShipmentItemId");

		CommerceShipmentItem commerceShipmentItem =
			_commerceShipmentItemService.getCommerceShipmentItem(
				commerceShipmentItemId);

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemService.getCommerceOrderItem(
				commerceShipmentItem.getCommerceOrderItemId());

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.fetchCommerceChannelByGroupClassPK(
				commerceOrderItem.getGroupId());

		_commerceChannelModelResourcePermission.check(
			PermissionThreadLocal.getPermissionChecker(),
			commerceChannel.getCommerceChannelId(), ActionKeys.VIEW);

		CommerceOrder commerceOrder = commerceOrderItem.getCommerceOrder();

		List<CommerceInventoryWarehouse> commerceInventoryWarehouses =
			_commerceInventoryWarehouseLocalService.
				getCommerceInventoryWarehouses(
					companyId, commerceOrder.getCommerceAccountId(),
					commerceOrderItem.getGroupId(), true);

		for (CommerceInventoryWarehouse commerceInventoryWarehouse :
				commerceInventoryWarehouses) {

			long commerceInventoryWarehouseId =
				commerceInventoryWarehouse.getCommerceInventoryWarehouseId();

			String portletNamespace = _portal.getPortletNamespace(
				CommercePortletKeys.COMMERCE_SHIPMENT);

			String inputName =
				portletNamespace + commerceInventoryWarehouseId + "_quantity";

			BigDecimal commerceOrderItemQuantity =
				commerceOrderItem.getQuantity();

			BigDecimal maxShippableQuantity =
				commerceOrderItemQuantity.subtract(
					commerceOrderItem.getShippedQuantity());

			BigDecimal shipmentItemWarehouseItemQuantity = BigDecimal.ZERO;

			long commerceShipmentId = ParamUtil.getLong(
				httpServletRequest, "commerceShipmentId");

			commerceShipmentItem =
				_commerceShipmentItemLocalService.fetchCommerceShipmentItem(
					commerceShipmentId,
					commerceOrderItem.getCommerceOrderItemId(),
					commerceInventoryWarehouseId);

			if (commerceShipmentItem != null) {
				shipmentItemWarehouseItemQuantity =
					commerceShipmentItem.getQuantity();

				maxShippableQuantity = maxShippableQuantity.add(
					commerceShipmentItem.getQuantity());
			}

			CommerceInventoryWarehouseItem commerceInventoryWarehouseItem =
				_commerceInventoryWarehouseItemService.
					fetchCommerceInventoryWarehouseItem(
						commerceInventoryWarehouseId,
						commerceOrderItem.getSku(),
						commerceOrderItem.getUnitOfMeasureKey());

			if (commerceInventoryWarehouseItem != null) {
				CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
					_cpInstanceUnitOfMeasureLocalService.
						fetchCPInstanceUnitOfMeasure(
							commerceInventoryWarehouseItem.getCompanyId(),
							commerceInventoryWarehouseItem.
								getUnitOfMeasureKey(),
							commerceInventoryWarehouseItem.getSku());

				BigDecimal quantity = BigDecimal.ZERO;
				BigDecimal commerceInventoryWarehouseItemQuantity =
					commerceInventoryWarehouseItem.getQuantity();
				BigDecimal incrementalOrderQuantity = BigDecimal.ONE;

				if (commerceInventoryWarehouseItemQuantity != null) {
					quantity = commerceInventoryWarehouseItemQuantity;
				}

				if (BigDecimalUtil.gt(maxShippableQuantity, quantity)) {
					maxShippableQuantity = quantity;
				}

				if (cpInstanceUnitOfMeasure != null) {
					incrementalOrderQuantity =
						_commerceQuantityFormatter.format(
							cpInstanceUnitOfMeasure,
							cpInstanceUnitOfMeasure.
								getIncrementalOrderQuantity());
				}

				warehouses.add(
					new Warehouse(
						commerceInventoryWarehouseId,
						new WarehouseItem(
							inputName,
							_commerceQuantityFormatter.format(
								cpInstanceUnitOfMeasure, maxShippableQuantity),
							BigDecimal.ZERO, incrementalOrderQuantity,
							_commerceQuantityFormatter.format(
								cpInstanceUnitOfMeasure,
								shipmentItemWarehouseItemQuantity)),
						_commerceQuantityFormatter.format(
							cpInstanceUnitOfMeasure, quantity),
						StringPool.BLANK,
						commerceInventoryWarehouse.getName(
							_portal.getLocale(httpServletRequest))));
			}
			else {
				warehouses.add(
					new Warehouse(
						commerceInventoryWarehouseId,
						new WarehouseItem(
							inputName, shipmentItemWarehouseItemQuantity,
							BigDecimal.ZERO, BigDecimal.ZERO,
							shipmentItemWarehouseItemQuantity),
						BigDecimal.ZERO, StringPool.BLANK,
						commerceInventoryWarehouse.getName(
							_portal.getLocale(httpServletRequest))));
			}
		}

		return warehouses;
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long commerceShipmentItemId = ParamUtil.getLong(
			httpServletRequest, "commerceShipmentItemId");

		CommerceShipmentItem commerceShipmentItem =
			_commerceShipmentItemService.getCommerceShipmentItem(
				commerceShipmentItemId);

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemService.getCommerceOrderItem(
				commerceShipmentItem.getCommerceOrderItemId());

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.fetchCommerceChannelByGroupClassPK(
				commerceOrderItem.getGroupId());

		_commerceChannelModelResourcePermission.check(
			PermissionThreadLocal.getPermissionChecker(),
			commerceChannel.getCommerceChannelId(), ActionKeys.VIEW);

		CommerceOrder commerceOrder = commerceOrderItem.getCommerceOrder();

		return _commerceInventoryWarehouseItemLocalService.
			getCommerceInventoryWarehouseItemsCount(
				_portal.getCompanyId(httpServletRequest),
				commerceOrder.getCommerceAccountId(),
				commerceOrderItem.getGroupId(), commerceOrderItem.getSku(),
				commerceOrderItem.getUnitOfMeasureKey());
	}

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CommerceChannel)"
	)
	private ModelResourcePermission<CommerceChannel>
		_commerceChannelModelResourcePermission;

	@Reference
	private CommerceInventoryWarehouseItemLocalService
		_commerceInventoryWarehouseItemLocalService;

	@Reference
	private CommerceInventoryWarehouseItemService
		_commerceInventoryWarehouseItemService;

	@Reference
	private CommerceInventoryWarehouseLocalService
		_commerceInventoryWarehouseLocalService;

	@Reference
	private CommerceOrderItemService _commerceOrderItemService;

	@Reference
	private CommerceQuantityFormatter _commerceQuantityFormatter;

	@Reference
	private CommerceShipmentItemLocalService _commerceShipmentItemLocalService;

	@Reference
	private CommerceShipmentItemService _commerceShipmentItemService;

	@Reference
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

	@Reference
	private Portal _portal;

}
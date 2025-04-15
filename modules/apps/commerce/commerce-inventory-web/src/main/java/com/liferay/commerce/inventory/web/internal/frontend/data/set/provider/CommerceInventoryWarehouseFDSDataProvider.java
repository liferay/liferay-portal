/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.web.internal.frontend.data.set.provider;

import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.service.CommerceInventoryReplenishmentItemService;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseItemService;
import com.liferay.commerce.inventory.web.internal.constants.CommerceInventoryFDSNames;
import com.liferay.commerce.inventory.web.internal.model.Warehouse;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommerceInventoryFDSNames.INVENTORY_WAREHOUSES,
	service = FDSDataProvider.class
)
public class CommerceInventoryWarehouseFDSDataProvider
	implements FDSDataProvider<Warehouse> {

	@Override
	public List<Warehouse> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		String sku = ParamUtil.getString(httpServletRequest, "sku");
		String unitOfMeasureKey = ParamUtil.getString(
			httpServletRequest, "unitOfMeasureKey");

		return TransformUtil.transform(
			_commerceInventoryWarehouseItemService.
				getCommerceInventoryWarehouseItemsByCompanyIdSkuAndUnitOfMeasureKey(
					_portal.getCompanyId(httpServletRequest), sku,
					unitOfMeasureKey, fdsPagination.getStartPosition(),
					fdsPagination.getEndPosition()),
			commerceInventoryWarehouseItem -> {
				CommerceInventoryWarehouse commerceInventoryWarehouse =
					commerceInventoryWarehouseItem.
						getCommerceInventoryWarehouse();

				BigDecimal stockQuantity = BigDecimal.ZERO;

				BigDecimal commerceInventoryWarehouseItemQuantity =
					commerceInventoryWarehouseItem.getQuantity();

				if (commerceInventoryWarehouseItemQuantity != null) {
					stockQuantity = commerceInventoryWarehouseItemQuantity;
				}

				BigDecimal reservedQuantity = BigDecimal.ZERO;

				BigDecimal commerceInventoryWarehouseItemReservedQuantity =
					commerceInventoryWarehouseItem.getReservedQuantity();

				if (commerceInventoryWarehouseItemReservedQuantity != null) {
					reservedQuantity =
						commerceInventoryWarehouseItemReservedQuantity;
				}

				BigDecimal replenishmentQuantity = BigDecimal.ZERO;

				BigDecimal commerceInventoryReplenishmentItemsCount =
					_commerceInventoryReplenishmentItemService.
						getCommerceInventoryReplenishmentItemsCount(
							commerceInventoryWarehouse.
								getCommerceInventoryWarehouseId(),
							sku, unitOfMeasureKey);

				if (commerceInventoryReplenishmentItemsCount != null) {
					replenishmentQuantity =
						commerceInventoryReplenishmentItemsCount;
				}

				return new Warehouse(
					commerceInventoryWarehouseItem.
						getCommerceInventoryWarehouseId(),
					commerceInventoryWarehouseItem.
						getCommerceInventoryWarehouseItemId(),
					commerceInventoryWarehouse.getName(
						_portal.getLocale(httpServletRequest)),
					replenishmentQuantity, reservedQuantity, stockQuantity);
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		String sku = ParamUtil.getString(httpServletRequest, "sku");
		String unitOfMeasureKey = ParamUtil.getString(
			httpServletRequest, "unitOfMeasureKey");

		return _commerceInventoryWarehouseItemService.
			getCommerceInventoryWarehouseItemsCount(
				_portal.getCompanyId(httpServletRequest), sku,
				unitOfMeasureKey);
	}

	@Reference
	private CommerceInventoryReplenishmentItemService
		_commerceInventoryReplenishmentItemService;

	@Reference
	private CommerceInventoryWarehouseItemService
		_commerceInventoryWarehouseItemService;

	@Reference
	private Portal _portal;

}
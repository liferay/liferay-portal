/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service.impl;

import com.liferay.commerce.inventory.model.CommerceInventoryReplenishmentItem;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.service.base.CommerceInventoryReplenishmentItemServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

import java.math.BigDecimal;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceInventoryReplenishmentItem"
	},
	service = AopService.class
)
public class CommerceInventoryReplenishmentItemServiceImpl
	extends CommerceInventoryReplenishmentItemServiceBaseImpl {

	@Override
	public CommerceInventoryReplenishmentItem
			addCommerceInventoryReplenishmentItem(
				String externalReferenceCode, long commerceInventoryWarehouseId,
				Date availabilityDate, BigDecimal quantity, String sku,
				String unitOfMeasureKey)
		throws PortalException {

		_commerceInventoryWarehouseModelResourcePermission.check(
			getPermissionChecker(), commerceInventoryWarehouseId,
			ActionKeys.UPDATE);

		return commerceInventoryReplenishmentItemLocalService.
			addCommerceInventoryReplenishmentItem(
				externalReferenceCode, getUserId(),
				commerceInventoryWarehouseId, availabilityDate, quantity, sku,
				unitOfMeasureKey);
	}

	@Override
	public void deleteCommerceInventoryReplenishmentItem(
			long commerceInventoryReplenishmentItemId)
		throws PortalException {

		CommerceInventoryReplenishmentItem commerceInventoryReplenishmentItem =
			commerceInventoryReplenishmentItemPersistence.fetchByPrimaryKey(
				commerceInventoryReplenishmentItemId);

		if (commerceInventoryReplenishmentItem != null) {
			_commerceInventoryWarehouseModelResourcePermission.check(
				getPermissionChecker(),
				commerceInventoryReplenishmentItem.
					getCommerceInventoryWarehouseId(),
				ActionKeys.DELETE);
		}

		commerceInventoryReplenishmentItemLocalService.
			deleteCommerceInventoryReplenishmentItem(
				commerceInventoryReplenishmentItemId);
	}

	@Override
	public void deleteCommerceInventoryReplenishmentItems(
			long companyId, String sku, String unitOfMeasureKey)
		throws PortalException {

		for (Long commerceInventoryWarehouseId :
				commerceInventoryReplenishmentItemLocalService.
					getCommerceInventoryWarehouseIds(
						companyId, sku, unitOfMeasureKey)) {

			if (!_commerceInventoryWarehouseModelResourcePermission.contains(
					getPermissionChecker(), commerceInventoryWarehouseId,
					ActionKeys.UPDATE)) {

				continue;
			}

			commerceInventoryReplenishmentItemLocalService.
				deleteCommerceInventoryReplenishmentItems(
					commerceInventoryWarehouseId);
		}
	}

	@Override
	public CommerceInventoryReplenishmentItem
			fetchCommerceInventoryReplenishmentItemByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		CommerceInventoryReplenishmentItem commerceInventoryReplenishmentItem =
			commerceInventoryReplenishmentItemPersistence.fetchByERC_C(
				externalReferenceCode, companyId);

		if (commerceInventoryReplenishmentItem != null) {
			_commerceInventoryWarehouseModelResourcePermission.check(
				getPermissionChecker(),
				commerceInventoryReplenishmentItem.
					getCommerceInventoryWarehouseId(),
				ActionKeys.VIEW);
		}

		return commerceInventoryReplenishmentItem;
	}

	@Override
	public CommerceInventoryReplenishmentItem
			getCommerceInventoryReplenishmentItem(
				long commerceInventoryReplenishmentItemId)
		throws PortalException {

		CommerceInventoryReplenishmentItem commerceInventoryReplenishmentItem =
			commerceInventoryReplenishmentItemPersistence.findByPrimaryKey(
				commerceInventoryReplenishmentItemId);

		_commerceInventoryWarehouseModelResourcePermission.check(
			getPermissionChecker(),
			commerceInventoryReplenishmentItem.
				getCommerceInventoryWarehouseId(),
			ActionKeys.VIEW);

		return commerceInventoryReplenishmentItem;
	}

	@Override
	public List<CommerceInventoryReplenishmentItem>
			getCommerceInventoryReplenishmentItemsByCommerceInventoryWarehouseId(
				long commerceInventoryWarehouseId, int start, int end)
		throws PortalException {

		_commerceInventoryWarehouseModelResourcePermission.check(
			getPermissionChecker(), commerceInventoryWarehouseId,
			ActionKeys.VIEW);

		return commerceInventoryReplenishmentItemPersistence.
			findByCommerceInventoryWarehouseId(
				commerceInventoryWarehouseId, start, end);
	}

	@Override
	public List<CommerceInventoryReplenishmentItem>
			getCommerceInventoryReplenishmentItemsByCompanyIdSkuAndUnitOfMeasureKey(
				long companyId, String sku, String unitOfMeasureKey, int start,
				int end)
		throws PortalException {

		return commerceInventoryReplenishmentItemLocalService.
			getCommerceInventoryReplenishmentItemsByCompanyIdSkuAndUnitOfMeasureKey(
				companyId, sku, unitOfMeasureKey, start, end, true);
	}

	@Override
	public BigDecimal getCommerceInventoryReplenishmentItemsCount(
			long commerceInventoryWarehouseId, String sku,
			String unitOfMeasureKey)
		throws PortalException {

		_commerceInventoryWarehouseModelResourcePermission.check(
			getPermissionChecker(), commerceInventoryWarehouseId,
			ActionKeys.VIEW);

		return commerceInventoryReplenishmentItemLocalService.
			getCommerceInventoryReplenishmentItemsCount(
				commerceInventoryWarehouseId, sku, unitOfMeasureKey);
	}

	@Override
	public int
			getCommerceInventoryReplenishmentItemsCountByCommerceInventoryWarehouseId(
				long commerceInventoryWarehouseId)
		throws PortalException {

		_commerceInventoryWarehouseModelResourcePermission.check(
			getPermissionChecker(), commerceInventoryWarehouseId,
			ActionKeys.VIEW);

		return commerceInventoryReplenishmentItemPersistence.
			countByCommerceInventoryWarehouseId(commerceInventoryWarehouseId);
	}

	@Override
	public int
			getCommerceInventoryReplenishmentItemsCountByCompanyIdSkuAndUnitOfMeasureKey(
				long companyId, String sku, String unitOfMeasureKey)
		throws PortalException {

		return commerceInventoryReplenishmentItemPersistence.countByC_S_U(
			companyId, sku, unitOfMeasureKey);
	}

	@Override
	public CommerceInventoryReplenishmentItem
			updateCommerceInventoryReplenishmentItem(
				String externalReferenceCode,
				long commerceInventoryReplenishmentItemId,
				Date availabilityDate, BigDecimal quantity, long mvccVersion)
		throws PortalException {

		CommerceInventoryReplenishmentItem commerceInventoryReplenishmentItem =
			commerceInventoryReplenishmentItemPersistence.fetchByPrimaryKey(
				commerceInventoryReplenishmentItemId);

		if (commerceInventoryReplenishmentItem != null) {
			_commerceInventoryWarehouseModelResourcePermission.check(
				getPermissionChecker(),
				commerceInventoryReplenishmentItem.
					getCommerceInventoryWarehouseId(),
				ActionKeys.UPDATE);
		}

		return commerceInventoryReplenishmentItemLocalService.
			updateCommerceInventoryReplenishmentItem(
				externalReferenceCode, commerceInventoryReplenishmentItemId,
				availabilityDate, quantity, mvccVersion);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.inventory.model.CommerceInventoryWarehouse)"
	)
	private ModelResourcePermission<CommerceInventoryWarehouse>
		_commerceInventoryWarehouseModelResourcePermission;

}
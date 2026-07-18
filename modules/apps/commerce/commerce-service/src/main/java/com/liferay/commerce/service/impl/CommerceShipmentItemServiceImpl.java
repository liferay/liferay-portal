/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.impl;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.model.CommerceShipmentItem;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.service.base.CommerceShipmentItemServiceBaseImpl;
import com.liferay.commerce.service.persistence.CommerceOrderItemPersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.math.BigDecimal;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceShipmentItem"
	},
	service = AopService.class
)
public class CommerceShipmentItemServiceImpl
	extends CommerceShipmentItemServiceBaseImpl {

	@Override
	public CommerceShipmentItem addCommerceShipmentItem(
			String externalReferenceCode, long commerceShipmentId,
			long commerceOrderItemId, long commerceInventoryWarehouseId,
			BigDecimal quantity, String unitOfMeasureKey,
			boolean validateInventory, ServiceContext serviceContext)
		throws PortalException {

		_commerceShipmentModelResourcePermission.check(
			getPermissionChecker(), commerceShipmentId, ActionKeys.UPDATE);

		return commerceShipmentItemLocalService.addCommerceShipmentItem(
			externalReferenceCode, commerceShipmentId, commerceOrderItemId,
			commerceInventoryWarehouseId, quantity, unitOfMeasureKey,
			validateInventory, serviceContext);
	}

	@Override
	public CommerceShipmentItem addOrUpdateCommerceShipmentItem(
			String externalReferenceCode, long commerceShipmentId,
			long commerceOrderItemId, long commerceInventoryWarehouseId,
			BigDecimal quantity, String unitOfMeasureKey,
			boolean validateInventory, ServiceContext serviceContext)
		throws PortalException {

		_commerceShipmentModelResourcePermission.check(
			getPermissionChecker(), commerceShipmentId, ActionKeys.UPDATE);

		return commerceShipmentItemLocalService.addOrUpdateCommerceShipmentItem(
			externalReferenceCode, commerceShipmentId, commerceOrderItemId,
			commerceInventoryWarehouseId, quantity, unitOfMeasureKey,
			validateInventory, serviceContext);
	}

	@Override
	public void deleteCommerceShipmentItem(
			long commerceShipmentItemId, boolean restoreStockQuantity)
		throws PortalException {

		CommerceShipmentItem commerceShipmentItem =
			commerceShipmentItemPersistence.findByPrimaryKey(
				commerceShipmentItemId);

		_commerceShipmentModelResourcePermission.check(
			getPermissionChecker(),
			commerceShipmentItem.getCommerceShipmentId(), ActionKeys.DELETE);

		commerceShipmentItemLocalService.deleteCommerceShipmentItem(
			commerceShipmentItem, restoreStockQuantity);
	}

	@Override
	public void deleteCommerceShipmentItems(
			long commerceShipmentId, boolean restoreStockQuantity)
		throws PortalException {

		_commerceShipmentModelResourcePermission.check(
			getPermissionChecker(), commerceShipmentId, ActionKeys.DELETE);

		commerceShipmentItemLocalService.deleteCommerceShipmentItems(
			commerceShipmentId, restoreStockQuantity);
	}

	@Override
	public CommerceShipmentItem fetchCommerceShipmentItem(
			long commerceShipmentId, long commerceOrderItemId,
			long commerceInventoryWarehouseId)
		throws PortalException {

		_commerceShipmentModelResourcePermission.check(
			getPermissionChecker(), commerceShipmentId, ActionKeys.VIEW);

		return commerceShipmentItemPersistence.fetchByC_C_C(
			commerceShipmentId, commerceOrderItemId,
			commerceInventoryWarehouseId);
	}

	@Override
	public CommerceShipmentItem
			fetchCommerceShipmentItemByExternalReferenceCode(
				long companyId, String externalReferenceCode)
		throws PortalException {

		CommerceShipmentItem commerceShipmentItem =
			commerceShipmentItemLocalService.
				fetchCommerceShipmentItemByExternalReferenceCode(
					externalReferenceCode, companyId);

		if (commerceShipmentItem != null) {
			_commerceShipmentModelResourcePermission.check(
				getPermissionChecker(),
				commerceShipmentItem.getCommerceShipmentId(), ActionKeys.VIEW);
		}

		return commerceShipmentItem;
	}

	@Override
	public CommerceShipmentItem getCommerceShipmentItem(
			long commerceShipmentItemId)
		throws PortalException {

		CommerceShipmentItem commerceShipmentItem =
			commerceShipmentItemPersistence.findByPrimaryKey(
				commerceShipmentItemId);

		_commerceShipmentModelResourcePermission.check(
			getPermissionChecker(),
			commerceShipmentItem.getCommerceShipmentId(), ActionKeys.VIEW);

		return commerceShipmentItem;
	}

	@Override
	public List<CommerceShipmentItem> getCommerceShipmentItems(
			long commerceShipmentId, int start, int end,
			OrderByComparator<CommerceShipmentItem> orderByComparator)
		throws PortalException {

		_commerceShipmentModelResourcePermission.check(
			getPermissionChecker(), commerceShipmentId, ActionKeys.VIEW);

		return commerceShipmentItemPersistence.findByCommerceShipmentId(
			commerceShipmentId, start, end, orderByComparator);
	}

	@Override
	public List<CommerceShipmentItem>
			getCommerceShipmentItemsByCommerceOrderItemId(
				long commerceOrderItemId)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemPersistence.findByPrimaryKey(commerceOrderItemId);

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderItem.getCommerceOrderId(),
			ActionKeys.VIEW);

		return commerceShipmentItemLocalService.
			getCommerceShipmentItemsByCommerceOrderItemId(commerceOrderItemId);
	}

	@Override
	public int getCommerceShipmentItemsCount(long commerceShipmentId)
		throws PortalException {

		_commerceShipmentModelResourcePermission.check(
			getPermissionChecker(), commerceShipmentId, ActionKeys.VIEW);

		return commerceShipmentItemPersistence.countByCommerceShipmentId(
			commerceShipmentId);
	}

	@Override
	public int getCommerceShipmentItemsCountByCommerceOrderItemId(
			long commerceOrderItemId)
		throws PortalException {

		_commerceOrderItemService.fetchCommerceOrderItem(commerceOrderItemId);

		return commerceShipmentItemLocalService.
			getCommerceShipmentItemsCountByCommerceOrderItemId(
				commerceOrderItemId);
	}

	@Override
	public BigDecimal getCommerceShipmentOrderItemsQuantity(
			long commerceShipmentId, long commerceOrderItemId)
		throws PortalException {

		_commerceShipmentModelResourcePermission.check(
			getPermissionChecker(), commerceShipmentId, ActionKeys.VIEW);

		return commerceShipmentItemLocalService.
			getCommerceShipmentOrderItemsQuantity(
				commerceShipmentId, commerceOrderItemId);
	}

	@Override
	public CommerceShipmentItem updateCommerceShipmentItem(
			long commerceShipmentId, long commerceShipmentItemId,
			long commerceInventoryWarehouseId, BigDecimal quantity,
			boolean validateInventory)
		throws PortalException {

		_commerceShipmentModelResourcePermission.check(
			getPermissionChecker(), commerceShipmentId, ActionKeys.UPDATE);

		return commerceShipmentItemLocalService.updateCommerceShipmentItem(
			commerceShipmentItemId, commerceInventoryWarehouseId, quantity,
			validateInventory);
	}

	@Override
	public CommerceShipmentItem updateExternalReferenceCode(
			long commerceShipmentId, long commerceShipmentItemId,
			String externalReferenceCode)
		throws PortalException {

		_commerceShipmentModelResourcePermission.check(
			getPermissionChecker(), commerceShipmentId, ActionKeys.UPDATE);

		return commerceShipmentItemLocalService.updateExternalReferenceCode(
			commerceShipmentItemId, externalReferenceCode);
	}

	@Reference
	private CommerceOrderItemPersistence _commerceOrderItemPersistence;

	@Reference
	private CommerceOrderItemService _commerceOrderItemService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private ModelResourcePermission<CommerceOrder>
		_commerceOrderModelResourcePermission;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceShipment)"
	)
	private ModelResourcePermission<CommerceShipment>
		_commerceShipmentModelResourcePermission;

}
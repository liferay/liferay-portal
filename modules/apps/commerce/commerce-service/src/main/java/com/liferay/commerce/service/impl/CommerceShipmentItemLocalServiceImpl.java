/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.impl;

import com.liferay.commerce.constants.CommerceShipmentConstants;
import com.liferay.commerce.exception.CommerceShipmentInactiveWarehouseException;
import com.liferay.commerce.exception.CommerceShipmentItemQuantityException;
import com.liferay.commerce.exception.CommerceShipmentStatusException;
import com.liferay.commerce.inventory.engine.CommerceInventoryEngine;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseItem;
import com.liferay.commerce.inventory.service.CommerceInventoryBookedQuantityLocalService;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseItemLocalService;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseLocalService;
import com.liferay.commerce.inventory.type.constants.CommerceInventoryAuditTypeConstants;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.model.CommerceShipmentItem;
import com.liferay.commerce.product.exception.NoSuchCPInstanceUnitOfMeasureException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.base.CommerceShipmentItemLocalServiceBaseImpl;
import com.liferay.commerce.service.persistence.CommerceShipmentPersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;

import java.math.BigDecimal;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 * @author Luca Pellizzon
 */
@Component(
	property = "model.class.name=com.liferay.commerce.model.CommerceShipmentItem",
	service = AopService.class
)
public class CommerceShipmentItemLocalServiceImpl
	extends CommerceShipmentItemLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceShipmentItem addCommerceShipmentItem(
			String externalReferenceCode, long commerceShipmentId,
			long commerceOrderItemId, long commerceInventoryWarehouseId,
			BigDecimal quantity, String unitOfMeasureKey,
			boolean validateInventory, ServiceContext serviceContext)
		throws PortalException {

		// Commerce shipment item

		User user = _userLocalService.getUser(serviceContext.getUserId());

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemLocalService.getCommerceOrderItem(
				commerceOrderItemId);

		if (validateInventory) {
			_validate(
				commerceOrderItem,
				_commerceShipmentPersistence.findByPrimaryKey(
					commerceShipmentId),
				commerceInventoryWarehouseId, quantity, quantity);
		}

		long commerceShipmentItemId = counterLocalService.increment();

		CommerceShipmentItem commerceShipmentItem =
			commerceShipmentItemPersistence.create(commerceShipmentItemId);

		commerceShipmentItem.setExternalReferenceCode(externalReferenceCode);
		commerceShipmentItem.setGroupId(commerceOrderItem.getGroupId());
		commerceShipmentItem.setCompanyId(user.getCompanyId());
		commerceShipmentItem.setUserId(user.getUserId());
		commerceShipmentItem.setUserName(user.getFullName());
		commerceShipmentItem.setCommerceShipmentId(commerceShipmentId);
		commerceShipmentItem.setCommerceOrderItemId(commerceOrderItemId);
		commerceShipmentItem.setCommerceInventoryWarehouseId(
			commerceInventoryWarehouseId);
		commerceShipmentItem.setQuantity(quantity);

		if (Validator.isNotNull(unitOfMeasureKey)) {
			CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
				_cpInstanceUnitOfMeasureLocalService.
					fetchCPInstanceUnitOfMeasure(
						commerceOrderItem.getCPInstanceId(), unitOfMeasureKey);

			if (cpInstanceUnitOfMeasure == null) {
				throw new NoSuchCPInstanceUnitOfMeasureException(
					"No commerce product instance unit of measure exists " +
						"with the primary key " + unitOfMeasureKey);
			}

			commerceShipmentItem.setUnitOfMeasureKey(unitOfMeasureKey);
		}
		else {
			commerceShipmentItem.setUnitOfMeasureKey(
				commerceOrderItem.getUnitOfMeasureKey());
		}

		commerceShipmentItem = commerceShipmentItemPersistence.update(
			commerceShipmentItem);

		// Stock quantity

		_updateStockQuantity(
			commerceOrderItem, commerceShipmentItem.getCommerceShipmentItemId(),
			commerceShipmentItem.getQuantity());

		// Commerce Order Item

		_commerceOrderItemLocalService.incrementShippedQuantity(
			commerceShipmentItem.getCommerceOrderItemId(), quantity);

		_reindexCommerceShipment(commerceShipmentItem.getCommerceShipmentId());

		return commerceShipmentItem;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceShipmentItem addDeliverySubscriptionCommerceShipmentItem(
			long groupId, long userId, long commerceShipmentId,
			long commerceOrderItemId)
		throws PortalException {

		long commerceShipmentItemId = counterLocalService.increment();

		CommerceShipmentItem commerceShipmentItem =
			commerceShipmentItemPersistence.create(commerceShipmentItemId);

		commerceShipmentItem.setGroupId(groupId);

		User user = _userLocalService.getUser(userId);

		commerceShipmentItem.setCompanyId(user.getCompanyId());
		commerceShipmentItem.setUserId(user.getUserId());
		commerceShipmentItem.setUserName(user.getFullName());

		commerceShipmentItem.setCommerceShipmentId(commerceShipmentId);
		commerceShipmentItem.setCommerceOrderItemId(commerceOrderItemId);

		commerceShipmentItem = commerceShipmentItemPersistence.update(
			commerceShipmentItem);

		_reindexCommerceShipment(commerceShipmentItem.getCommerceShipmentId());

		return commerceShipmentItem;
	}

	@Override
	public CommerceShipmentItem addOrUpdateCommerceShipmentItem(
			String externalReferenceCode, long commerceShipmentId,
			long commerceOrderItemId, long commerceInventoryWarehouseId,
			BigDecimal quantity, String unitOfMeasureKey,
			boolean validateInventory, ServiceContext serviceContext)
		throws PortalException {

		CommerceShipmentItem commerceShipmentItem = null;

		if (Validator.isNotNull(externalReferenceCode)) {
			commerceShipmentItem = commerceShipmentItemPersistence.fetchByERC_C(
				externalReferenceCode, serviceContext.getCompanyId());
		}

		if (commerceShipmentItem == null) {
			return commerceShipmentItemLocalService.addCommerceShipmentItem(
				externalReferenceCode, commerceShipmentId, commerceOrderItemId,
				commerceInventoryWarehouseId, quantity, unitOfMeasureKey,
				validateInventory, serviceContext);
		}

		return commerceShipmentItemLocalService.updateCommerceShipmentItem(
			commerceShipmentItem.getCommerceShipmentItemId(),
			commerceInventoryWarehouseId, quantity, validateInventory);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceShipmentItem deleteCommerceShipmentItem(
			CommerceShipmentItem commerceShipmentItem,
			boolean restoreStockQuantity)
		throws PortalException {

		commerceShipmentItemPersistence.remove(commerceShipmentItem);

		// Commerce order item

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemLocalService.fetchCommerceOrderItem(
				commerceShipmentItem.getCommerceOrderItemId());

		if (!restoreStockQuantity) {
			if (commerceOrderItem != null) {
				_commerceOrderItemLocalService.updateCommerceOrderItem(
					commerceShipmentItem.getCommerceOrderItemId(), 0);
			}

			return commerceShipmentItem;
		}

		// Commerce order item

		BigDecimal shippedQuantity = commerceShipmentItem.getQuantity();

		try {
			commerceOrderItem =
				_commerceOrderItemLocalService.incrementShippedQuantity(
					commerceShipmentItem.getCommerceOrderItemId(),
					shippedQuantity.negate());

			// Stock quantity

			if ((commerceShipmentItem.getCommerceInventoryWarehouseId() > 0) &&
				(shippedQuantity.compareTo(BigDecimal.ZERO) > 0)) {

				_restoreStockQuantity(
					commerceOrderItem, commerceShipmentItem,
					commerceShipmentItem.getQuantity());
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		_reindexCommerceShipment(commerceShipmentItem.getCommerceShipmentId());

		return commerceShipmentItem;
	}

	@Override
	public void deleteCommerceShipmentItem(
			long commerceShipmentItemId, boolean restoreStockQuantity)
		throws PortalException {

		CommerceShipmentItem commerceShipmentItem =
			commerceShipmentItemPersistence.findByPrimaryKey(
				commerceShipmentItemId);

		commerceShipmentItemLocalService.deleteCommerceShipmentItem(
			commerceShipmentItem, restoreStockQuantity);
	}

	@Override
	public void deleteCommerceShipmentItems(
			long commerceShipmentId, boolean restoreStockQuantity)
		throws PortalException {

		List<CommerceShipmentItem> commerceShipmentItems =
			commerceShipmentItemPersistence.findByCommerceShipmentId(
				commerceShipmentId);

		for (CommerceShipmentItem commerceShipmentItem :
				commerceShipmentItems) {

			commerceShipmentItemLocalService.deleteCommerceShipmentItem(
				commerceShipmentItem, restoreStockQuantity);
		}
	}

	@Override
	public CommerceShipmentItem fetchCommerceShipmentItem(
		long commerceShipmentId, long commerceOrderItemId,
		long commerceInventoryWarehouseId) {

		return commerceShipmentItemPersistence.fetchByC_C_C(
			commerceShipmentId, commerceOrderItemId,
			commerceInventoryWarehouseId);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public List<CommerceShipmentItem> getCommerceShipmentItems(
		long commerceOrderItemId) {

		return commerceShipmentItemLocalService.
			getCommerceShipmentItemsByCommerceOrderItemId(commerceOrderItemId);
	}

	@Override
	public List<CommerceShipmentItem> getCommerceShipmentItems(
		long commerceShipmentId, int start, int end,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return commerceShipmentItemPersistence.findByCommerceShipmentId(
			commerceShipmentId, start, end, orderByComparator);
	}

	@Override
	public List<CommerceShipmentItem> getCommerceShipmentItems(
		long commerceShipmentId, long commerceOrderItemId, int start, int end,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return commerceShipmentItemPersistence.findByC_C(
			commerceShipmentId, commerceOrderItemId, start, end,
			orderByComparator);
	}

	@Override
	public List<CommerceShipmentItem>
		getCommerceShipmentItemsByCommerceOrderItemId(
			long commerceOrderItemId) {

		return commerceShipmentItemPersistence.findByCommerceOrderItemId(
			commerceOrderItemId);
	}

	@Override
	public int getCommerceShipmentItemsCount(long commerceShipmentId) {
		return commerceShipmentItemPersistence.countByCommerceShipmentId(
			commerceShipmentId);
	}

	@Override
	public int getCommerceShipmentItemsCountByCommerceOrderItemId(
		long commerceOrderItemId) {

		return commerceShipmentItemPersistence.countByCommerceOrderItemId(
			commerceOrderItemId);
	}

	@Override
	public BigDecimal getCommerceShipmentOrderItemsQuantity(
		long commerceShipmentId, long commerceOrderItemId) {

		return commerceShipmentItemFinder.getCommerceShipmentOrderItemsQuantity(
			commerceShipmentId, commerceOrderItemId);
	}

	@Override
	public int getValidCommerceShipmentItemsCount(long commerceShipmentId) {
		return commerceShipmentItemPersistence.countByC_NotC_GteQ(
			commerceShipmentId, 0L, BigDecimal.ONE);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceShipmentItem updateCommerceShipmentItem(
			long commerceShipmentItemId, long commerceInventoryWarehouseId,
			BigDecimal quantity, boolean validateInventory)
		throws PortalException {

		// Commerce shipment item

		CommerceShipmentItem commerceShipmentItem =
			commerceShipmentItemPersistence.findByPrimaryKey(
				commerceShipmentItemId);

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemLocalService.getCommerceOrderItem(
				commerceShipmentItem.getCommerceOrderItemId());

		BigDecimal originalQuantity = commerceShipmentItem.getQuantity();

		if (validateInventory) {
			_validate(
				commerceOrderItem, commerceShipmentItem.getCommerceShipment(),
				commerceInventoryWarehouseId, originalQuantity, quantity);
		}

		commerceShipmentItem.setCommerceInventoryWarehouseId(
			commerceInventoryWarehouseId);
		commerceShipmentItem.setQuantity(quantity);

		commerceShipmentItem = commerceShipmentItemPersistence.update(
			commerceShipmentItem);

		BigDecimal quantityDelta = quantity.subtract(originalQuantity);

		// Stock quantity

		if (BigDecimalUtil.eq(
				commerceOrderItem.getQuantity(),
				commerceOrderItem.getShippedQuantity())) {

			_restoreStockQuantity(
				commerceOrderItem, commerceShipmentItem, quantityDelta.abs());
		}
		else {
			_updateStockQuantity(
				commerceOrderItem,
				commerceShipmentItem.getCommerceShipmentItemId(),
				quantityDelta);
		}

		// Commerce order item

		_commerceOrderItemLocalService.incrementShippedQuantity(
			commerceShipmentItem.getCommerceOrderItemId(), quantityDelta);

		_reindexCommerceShipment(commerceShipmentItem.getCommerceShipmentId());

		return commerceShipmentItem;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceShipmentItem updateExternalReferenceCode(
			long commerceShipmentItemId, String externalReferenceCode)
		throws PortalException {

		CommerceShipmentItem commerceShipmentItem =
			commerceShipmentItemPersistence.findByPrimaryKey(
				commerceShipmentItemId);

		if (Objects.equals(
				commerceShipmentItem.getExternalReferenceCode(),
				externalReferenceCode)) {

			return commerceShipmentItem;
		}

		commerceShipmentItem.setExternalReferenceCode(externalReferenceCode);

		return commerceShipmentItemPersistence.update(commerceShipmentItem);
	}

	private CommerceInventoryWarehouseItem _fetchCommerceInventoryWarehouseItem(
			long commerceShipmentItemId, String sku, String unitOfMeasureKey)
		throws PortalException {

		CommerceShipmentItem commerceShipmentItem =
			commerceShipmentItemPersistence.findByPrimaryKey(
				commerceShipmentItemId);

		return _commerceInventoryWarehouseItemLocalService.
			fetchCommerceInventoryWarehouseItem(
				commerceShipmentItem.getCommerceInventoryWarehouseId(), sku,
				unitOfMeasureKey);
	}

	private void _reindexCommerceShipment(long commerceShipmentId)
		throws PortalException {

		Indexer<CommerceShipment> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(CommerceShipment.class);

		indexer.reindex(CommerceShipment.class.getName(), commerceShipmentId);
	}

	private void _restoreStockQuantity(
			CommerceOrderItem commerceOrderItem,
			CommerceShipmentItem commerceShipmentItem, BigDecimal quantity)
		throws PortalException {

		long commerceCatalogGroupId = 0;

		CPDefinition cpDefinition = commerceOrderItem.getCPDefinition();

		if (cpDefinition != null) {
			commerceCatalogGroupId = cpDefinition.getGroupId();
		}

		_commerceInventoryEngine.increaseStockQuantity(
			commerceShipmentItem.getUserId(), commerceCatalogGroupId,
			commerceShipmentItem.getCommerceInventoryWarehouseId(), quantity,
			commerceOrderItem.getSku(),
			commerceOrderItem.getUnitOfMeasureKey());

		_commerceInventoryBookedQuantityLocalService.
			resetCommerceInventoryBookedQuantity(
				commerceOrderItem.getCommerceInventoryBookedQuantityId(),
				commerceOrderItem.getUserId(), null, quantity,
				commerceOrderItem.getSku(),
				commerceOrderItem.getUnitOfMeasureKey(),
				HashMapBuilder.put(
					CommerceInventoryAuditTypeConstants.ORDER_ID,
					String.valueOf(commerceOrderItem.getCommerceOrderId())
				).put(
					CommerceInventoryAuditTypeConstants.ORDER_ITEM_ID,
					String.valueOf(commerceOrderItem.getCommerceOrderItemId())
				).put(
					CommerceInventoryAuditTypeConstants.SHIPMENT_ID,
					String.valueOf(commerceShipmentItem.getCommerceShipmentId())
				).build());
	}

	private void _updateStockQuantity(
			CommerceOrderItem commerceOrderItem, long commerceShipmentItemId,
			BigDecimal quantity)
		throws PortalException {

		if (commerceOrderItem == null) {
			return;
		}

		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem =
			_fetchCommerceInventoryWarehouseItem(
				commerceShipmentItemId, commerceOrderItem.getSku(),
				commerceOrderItem.getUnitOfMeasureKey());

		if (commerceInventoryWarehouseItem == null) {
			return;
		}

		CommerceShipmentItem commerceShipmentItem =
			commerceShipmentItemPersistence.findByPrimaryKey(
				commerceShipmentItemId);

		long commerceCatalogGroupId = 0;

		CPDefinition cpDefinition = commerceOrderItem.getCPDefinition();

		if (cpDefinition != null) {
			commerceCatalogGroupId = cpDefinition.getGroupId();
		}

		_commerceInventoryEngine.consumeQuantity(
			commerceShipmentItem.getUserId(),
			commerceOrderItem.getCommerceInventoryBookedQuantityId(),
			commerceCatalogGroupId,
			commerceShipmentItem.getCommerceInventoryWarehouseId(), quantity,
			commerceOrderItem.getSku(), commerceOrderItem.getUnitOfMeasureKey(),
			HashMapBuilder.put(
				CommerceInventoryAuditTypeConstants.ORDER_ID,
				String.valueOf(commerceOrderItem.getCommerceOrderId())
			).put(
				CommerceInventoryAuditTypeConstants.ORDER_ITEM_ID,
				String.valueOf(commerceOrderItem.getCommerceOrderItemId())
			).put(
				CommerceInventoryAuditTypeConstants.SHIPMENT_ID,
				String.valueOf(commerceShipmentItem.getCommerceShipmentId())
			).put(
				CommerceInventoryAuditTypeConstants.SHIPMENT_ITEM_ID,
				String.valueOf(commerceShipmentItemId)
			).build());
	}

	private void _validate(
			CommerceOrderItem commerceOrderItem,
			CommerceShipment commerceShipment,
			long commerceInventoryWarehouseId, BigDecimal quantity,
			BigDecimal newQuantity)
		throws PortalException {

		if ((commerceShipment != null) &&
			(commerceShipment.getStatus() !=
				CommerceShipmentConstants.SHIPMENT_STATUS_PROCESSING)) {

			throw new CommerceShipmentStatusException();
		}

		if (commerceInventoryWarehouseId <= 0) {
			return;
		}

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			_commerceInventoryWarehouseLocalService.
				getCommerceInventoryWarehouse(commerceInventoryWarehouseId);

		if (!commerceInventoryWarehouse.isActive()) {
			throw new CommerceShipmentInactiveWarehouseException();
		}

		BigDecimal commerceOrderItemQuantity = commerceOrderItem.getQuantity();

		BigDecimal availableQuantity = commerceOrderItemQuantity.subtract(
			commerceOrderItem.getShippedQuantity());

		CommerceShipmentItem commerceShipmentItem =
			commerceShipmentItemPersistence.fetchByC_C_C(
				commerceShipment.getCommerceShipmentId(),
				commerceOrderItem.getCommerceOrderItemId(),
				commerceInventoryWarehouseId);

		if (commerceShipmentItem != null) {
			availableQuantity = availableQuantity.add(
				commerceShipmentItem.getQuantity());
		}

		BigDecimal commerceInventoryWarehouseQuantity =
			_commerceOrderItemLocalService.
				getCommerceInventoryWarehouseItemQuantity(
					commerceOrderItem.getCommerceOrderItemId(),
					commerceInventoryWarehouseId);

		if ((BigDecimalUtil.gt(newQuantity, quantity) &&
			 BigDecimalUtil.gt(newQuantity, availableQuantity)) ||
			BigDecimalUtil.gt(
				newQuantity, commerceInventoryWarehouseQuantity)) {

			throw new CommerceShipmentItemQuantityException();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceShipmentItemLocalServiceImpl.class);

	@Reference
	private CommerceInventoryBookedQuantityLocalService
		_commerceInventoryBookedQuantityLocalService;

	@Reference
	private CommerceInventoryEngine _commerceInventoryEngine;

	@Reference
	private CommerceInventoryWarehouseItemLocalService
		_commerceInventoryWarehouseItemLocalService;

	@Reference
	private CommerceInventoryWarehouseLocalService
		_commerceInventoryWarehouseLocalService;

	@Reference
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Reference
	private CommerceShipmentPersistence _commerceShipmentPersistence;

	@Reference
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
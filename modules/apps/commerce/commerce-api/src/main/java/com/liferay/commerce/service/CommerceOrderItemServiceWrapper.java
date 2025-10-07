/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link CommerceOrderItemService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceOrderItemService
 * @generated
 */
public class CommerceOrderItemServiceWrapper
	implements CommerceOrderItemService,
			   ServiceWrapper<CommerceOrderItemService> {

	public CommerceOrderItemServiceWrapper() {
		this(null);
	}

	public CommerceOrderItemServiceWrapper(
		CommerceOrderItemService commerceOrderItemService) {

		_commerceOrderItemService = commerceOrderItemService;
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem addCommerceOrderItem(
			long commerceOrderId, long cpInstanceId, String json,
			java.math.BigDecimal quantity, long replacedCPInstanceId,
			java.math.BigDecimal shippedQuantity, String unitOfMeasureKey,
			com.liferay.commerce.context.CommerceContext commerceContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.addCommerceOrderItem(
			commerceOrderId, cpInstanceId, json, quantity, replacedCPInstanceId,
			shippedQuantity, unitOfMeasureKey, commerceContext, serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem
			addOrUpdateCommerceOrderItem(
				long commerceOrderId, long cpInstanceId, String json,
				java.math.BigDecimal quantity, long replacedCPInstanceId,
				java.math.BigDecimal shippedQuantity, String unitOfMeasureKey,
				com.liferay.commerce.context.CommerceContext commerceContext,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.addOrUpdateCommerceOrderItem(
			commerceOrderId, cpInstanceId, json, quantity, replacedCPInstanceId,
			shippedQuantity, unitOfMeasureKey, commerceContext, serviceContext);
	}

	@Override
	public int countSubscriptionCommerceOrderItems(long commerceOrderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.countSubscriptionCommerceOrderItems(
			commerceOrderId);
	}

	@Override
	public void deleteCommerceOrderItem(long commerceOrderItemId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceOrderItemService.deleteCommerceOrderItem(commerceOrderItemId);
	}

	@Override
	public void deleteCommerceOrderItem(
			long commerceOrderItemId,
			com.liferay.commerce.context.CommerceContext commerceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceOrderItemService.deleteCommerceOrderItem(
			commerceOrderItemId, commerceContext);
	}

	@Override
	public void deleteCommerceOrderItems(long commerceOrderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceOrderItemService.deleteCommerceOrderItems(commerceOrderId);
	}

	@Override
	public void deleteMissingCommerceOrderItems(
			long commerceOrderId, Long[] commerceOrderItemIds,
			String[] externalReferenceCodes)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceOrderItemService.deleteMissingCommerceOrderItems(
			commerceOrderId, commerceOrderItemIds, externalReferenceCodes);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem fetchCommerceOrderItem(
			long commerceOrderItemId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.fetchCommerceOrderItem(
			commerceOrderItemId);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem
			fetchCommerceOrderItemByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.
			fetchCommerceOrderItemByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderItem>
			getAvailableForShipmentCommerceOrderItems(long commerceOrderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.
			getAvailableForShipmentCommerceOrderItems(commerceOrderId);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderItem>
			getChildCommerceOrderItems(long parentCommerceOrderItemId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.getChildCommerceOrderItems(
			parentCommerceOrderItemId);
	}

	@Override
	public java.math.BigDecimal getCommerceInventoryWarehouseItemQuantity(
			long commerceOrderItemId, long commerceInventoryWarehouseId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.
			getCommerceInventoryWarehouseItemQuantity(
				commerceOrderItemId, commerceInventoryWarehouseId);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem getCommerceOrderItem(
			long commerceOrderItemId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.getCommerceOrderItem(
			commerceOrderItemId);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderItem>
			getCommerceOrderItems(long commerceOrderId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.getCommerceOrderItems(
			commerceOrderId, start, end);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderItem>
			getCommerceOrderItems(
				long groupId, long commerceAccountId, int[] orderStatuses,
				int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.getCommerceOrderItems(
			groupId, commerceAccountId, orderStatuses, start, end);
	}

	@Override
	public int getCommerceOrderItemsCount(long commerceOrderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.getCommerceOrderItemsCount(
			commerceOrderId);
	}

	@Override
	public int getCommerceOrderItemsCount(
			long commerceOrderId, long cpInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.getCommerceOrderItemsCount(
			commerceOrderId, cpInstanceId);
	}

	@Override
	public int getCommerceOrderItemsCount(
			long groupId, long commerceAccountId, int[] orderStatuses)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.getCommerceOrderItemsCount(
			groupId, commerceAccountId, orderStatuses);
	}

	@Override
	public java.math.BigDecimal getCommerceOrderItemsQuantity(
			long commerceOrderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.getCommerceOrderItemsQuantity(
			commerceOrderId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceOrderItemService.getOSGiServiceIdentifier();
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderItem>
			getParentCommerceOrderItems(
				long commerceOrderId, long parentCommerceOrderItemId, int start,
				int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.commerce.model.CommerceOrderItem>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.getParentCommerceOrderItems(
			commerceOrderId, parentCommerceOrderItemId, start, end,
			orderByComparator);
	}

	@Override
	public int getParentCommerceOrderItemsCount(
			long commerceOrderId, long parentCommerceOrderItemId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.getParentCommerceOrderItemsCount(
			commerceOrderId, parentCommerceOrderItemId);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderItem>
			getSupplierCommerceOrderItems(
				long customerCommerceOrderItemId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.getSupplierCommerceOrderItems(
			customerCommerceOrderItemId, start, end);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem importCommerceOrderItem(
			String externalReferenceCode, long commerceOrderItemId,
			long commerceOrderId, long cpInstanceId,
			String cpMeasurementUnitKey, String json,
			java.math.BigDecimal quantity, java.math.BigDecimal shippedQuantity,
			java.math.BigDecimal unitOfMeasureIncrementalOrderQuantity,
			String unitOfMeasureKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.importCommerceOrderItem(
			externalReferenceCode, commerceOrderItemId, commerceOrderId,
			cpInstanceId, cpMeasurementUnitKey, json, quantity, shippedQuantity,
			unitOfMeasureIncrementalOrderQuantity, unitOfMeasureKey,
			serviceContext);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.commerce.model.CommerceOrderItem> searchCommerceOrderItems(
				long commerceOrderId, long parentCommerceOrderItemId,
				String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.searchCommerceOrderItems(
			commerceOrderId, parentCommerceOrderItemId, keywords, start, end,
			sort);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.commerce.model.CommerceOrderItem> searchCommerceOrderItems(
				long commerceOrderId, String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.searchCommerceOrderItems(
			commerceOrderId, keywords, start, end, sort);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.commerce.model.CommerceOrderItem> searchCommerceOrderItems(
				long commerceOrderId, String name, String sku,
				boolean andOperator, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.searchCommerceOrderItems(
			commerceOrderId, name, sku, andOperator, start, end, sort);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem updateCommerceOrderItem(
			long commerceOrderItemId, java.math.BigDecimal quantity,
			com.liferay.commerce.context.CommerceContext commerceContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.updateCommerceOrderItem(
			commerceOrderItemId, quantity, commerceContext, serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem updateCommerceOrderItem(
			long commerceOrderItemId, long cpMeasurementUnitId,
			java.math.BigDecimal quantity,
			com.liferay.commerce.context.CommerceContext commerceContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.updateCommerceOrderItem(
			commerceOrderItemId, cpMeasurementUnitId, quantity, commerceContext,
			serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem updateCommerceOrderItem(
			long commerceOrderItemId, long cpMeasurementUnitId,
			java.math.BigDecimal quantity,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.updateCommerceOrderItem(
			commerceOrderItemId, cpMeasurementUnitId, quantity, serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem updateCommerceOrderItem(
			String externalReferenceCode, long commerceOrderItemId, String json,
			java.math.BigDecimal quantity,
			com.liferay.commerce.context.CommerceContext commerceContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.updateCommerceOrderItem(
			externalReferenceCode, commerceOrderItemId, json, quantity,
			commerceContext, serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem
			updateCommerceOrderItemDeliveryDate(
				long commerceOrderItemId, java.util.Date requestedDeliveryDate)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.updateCommerceOrderItemDeliveryDate(
			commerceOrderItemId, requestedDeliveryDate);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem
			updateCommerceOrderItemInfo(
				long commerceOrderItemId, long shippingAddressId,
				String deliveryGroupName, String printedNote)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.updateCommerceOrderItemInfo(
			commerceOrderItemId, shippingAddressId, deliveryGroupName,
			printedNote);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem
			updateCommerceOrderItemInfo(
				long commerceOrderItemId, long shippingAddressId,
				String deliveryGroupName, String printedNote,
				int requestedDeliveryDateMonth, int requestedDeliveryDateDay,
				int requestedDeliveryDateYear)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.updateCommerceOrderItemInfo(
			commerceOrderItemId, shippingAddressId, deliveryGroupName,
			printedNote, requestedDeliveryDateMonth, requestedDeliveryDateDay,
			requestedDeliveryDateYear);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public com.liferay.commerce.model.CommerceOrderItem
			updateCommerceOrderItemInfo(
				long commerceOrderItemId, String deliveryGroupName,
				long shippingAddressId, String printedNote,
				int requestedDeliveryDateMonth, int requestedDeliveryDateDay,
				int requestedDeliveryDateYear, int requestedDeliveryDateHour,
				int requestedDeliveryDateMinute,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.updateCommerceOrderItemInfo(
			commerceOrderItemId, deliveryGroupName, shippingAddressId,
			printedNote, requestedDeliveryDateMonth, requestedDeliveryDateDay,
			requestedDeliveryDateYear, requestedDeliveryDateHour,
			requestedDeliveryDateMinute, serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem
			updateCommerceOrderItemPrices(
				long commerceOrderItemId, java.math.BigDecimal discountAmount,
				java.math.BigDecimal discountPercentageLevel1,
				java.math.BigDecimal discountPercentageLevel2,
				java.math.BigDecimal discountPercentageLevel3,
				java.math.BigDecimal discountPercentageLevel4,
				java.math.BigDecimal finalPrice,
				java.math.BigDecimal promoPrice, java.math.BigDecimal unitPrice)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.updateCommerceOrderItemPrices(
			commerceOrderItemId, discountAmount, discountPercentageLevel1,
			discountPercentageLevel2, discountPercentageLevel3,
			discountPercentageLevel4, finalPrice, promoPrice, unitPrice);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem
			updateCommerceOrderItemPrices(
				long commerceOrderItemId, java.math.BigDecimal discountAmount,
				java.math.BigDecimal discountAmountWithTaxAmount,
				java.math.BigDecimal discountPercentageLevel1,
				java.math.BigDecimal discountPercentageLevel1WithTaxAmount,
				java.math.BigDecimal discountPercentageLevel2,
				java.math.BigDecimal discountPercentageLevel2WithTaxAmount,
				java.math.BigDecimal discountPercentageLevel3,
				java.math.BigDecimal discountPercentageLevel3WithTaxAmount,
				java.math.BigDecimal discountPercentageLevel4,
				java.math.BigDecimal discountPercentageLevel4WithTaxAmount,
				java.math.BigDecimal finalPrice,
				java.math.BigDecimal finalPriceWithTaxAmount,
				java.math.BigDecimal promoPrice,
				java.math.BigDecimal promoPriceWithTaxAmount,
				java.math.BigDecimal unitPrice,
				java.math.BigDecimal unitPriceWithTaxAmount)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.updateCommerceOrderItemPrices(
			commerceOrderItemId, discountAmount, discountAmountWithTaxAmount,
			discountPercentageLevel1, discountPercentageLevel1WithTaxAmount,
			discountPercentageLevel2, discountPercentageLevel2WithTaxAmount,
			discountPercentageLevel3, discountPercentageLevel3WithTaxAmount,
			discountPercentageLevel4, discountPercentageLevel4WithTaxAmount,
			finalPrice, finalPriceWithTaxAmount, promoPrice,
			promoPriceWithTaxAmount, unitPrice, unitPriceWithTaxAmount);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public com.liferay.commerce.model.CommerceOrderItem
			updateCommerceOrderItemUnitPrice(
				long commerceOrderItemId, java.math.BigDecimal unitPrice)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.updateCommerceOrderItemUnitPrice(
			commerceOrderItemId, unitPrice);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem
			updateCommerceOrderItemUnitPrice(
				long commerceOrderItemId, java.math.BigDecimal quantity,
				java.math.BigDecimal unitPrice)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.updateCommerceOrderItemUnitPrice(
			commerceOrderItemId, quantity, unitPrice);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem updateCustomFields(
			long commerceOrderItemId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.updateCustomFields(
			commerceOrderItemId, serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem
			updateExternalReferenceCode(
				long commerceOrderItemId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemService.updateExternalReferenceCode(
			commerceOrderItemId, externalReferenceCode);
	}

	@Override
	public CommerceOrderItemService getWrappedService() {
		return _commerceOrderItemService;
	}

	@Override
	public void setWrappedService(
		CommerceOrderItemService commerceOrderItemService) {

		_commerceOrderItemService = commerceOrderItemService;
	}

	private CommerceOrderItemService _commerceOrderItemService;

}
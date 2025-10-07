/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

/**
 * Provides the remote service utility for CommerceOrderItem. This utility wraps
 * <code>com.liferay.commerce.service.impl.CommerceOrderItemServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceOrderItemService
 * @generated
 */
public class CommerceOrderItemServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.service.impl.CommerceOrderItemServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static CommerceOrderItem addCommerceOrderItem(
			long commerceOrderId, long cpInstanceId, String json,
			java.math.BigDecimal quantity, long replacedCPInstanceId,
			java.math.BigDecimal shippedQuantity, String unitOfMeasureKey,
			com.liferay.commerce.context.CommerceContext commerceContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceOrderItem(
			commerceOrderId, cpInstanceId, json, quantity, replacedCPInstanceId,
			shippedQuantity, unitOfMeasureKey, commerceContext, serviceContext);
	}

	public static CommerceOrderItem addOrUpdateCommerceOrderItem(
			long commerceOrderId, long cpInstanceId, String json,
			java.math.BigDecimal quantity, long replacedCPInstanceId,
			java.math.BigDecimal shippedQuantity, String unitOfMeasureKey,
			com.liferay.commerce.context.CommerceContext commerceContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateCommerceOrderItem(
			commerceOrderId, cpInstanceId, json, quantity, replacedCPInstanceId,
			shippedQuantity, unitOfMeasureKey, commerceContext, serviceContext);
	}

	public static int countSubscriptionCommerceOrderItems(long commerceOrderId)
		throws PortalException {

		return getService().countSubscriptionCommerceOrderItems(
			commerceOrderId);
	}

	public static void deleteCommerceOrderItem(long commerceOrderItemId)
		throws PortalException {

		getService().deleteCommerceOrderItem(commerceOrderItemId);
	}

	public static void deleteCommerceOrderItem(
			long commerceOrderItemId,
			com.liferay.commerce.context.CommerceContext commerceContext)
		throws PortalException {

		getService().deleteCommerceOrderItem(
			commerceOrderItemId, commerceContext);
	}

	public static void deleteCommerceOrderItems(long commerceOrderId)
		throws PortalException {

		getService().deleteCommerceOrderItems(commerceOrderId);
	}

	public static void deleteMissingCommerceOrderItems(
			long commerceOrderId, Long[] commerceOrderItemIds,
			String[] externalReferenceCodes)
		throws PortalException {

		getService().deleteMissingCommerceOrderItems(
			commerceOrderId, commerceOrderItemIds, externalReferenceCodes);
	}

	public static CommerceOrderItem fetchCommerceOrderItem(
			long commerceOrderItemId)
		throws PortalException {

		return getService().fetchCommerceOrderItem(commerceOrderItemId);
	}

	public static CommerceOrderItem
			fetchCommerceOrderItemByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().fetchCommerceOrderItemByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static List<CommerceOrderItem>
			getAvailableForShipmentCommerceOrderItems(long commerceOrderId)
		throws PortalException {

		return getService().getAvailableForShipmentCommerceOrderItems(
			commerceOrderId);
	}

	public static List<CommerceOrderItem> getChildCommerceOrderItems(
			long parentCommerceOrderItemId)
		throws PortalException {

		return getService().getChildCommerceOrderItems(
			parentCommerceOrderItemId);
	}

	public static java.math.BigDecimal
			getCommerceInventoryWarehouseItemQuantity(
				long commerceOrderItemId, long commerceInventoryWarehouseId)
		throws PortalException {

		return getService().getCommerceInventoryWarehouseItemQuantity(
			commerceOrderItemId, commerceInventoryWarehouseId);
	}

	public static CommerceOrderItem getCommerceOrderItem(
			long commerceOrderItemId)
		throws PortalException {

		return getService().getCommerceOrderItem(commerceOrderItemId);
	}

	public static List<CommerceOrderItem> getCommerceOrderItems(
			long commerceOrderId, int start, int end)
		throws PortalException {

		return getService().getCommerceOrderItems(commerceOrderId, start, end);
	}

	public static List<CommerceOrderItem> getCommerceOrderItems(
			long groupId, long commerceAccountId, int[] orderStatuses,
			int start, int end)
		throws PortalException {

		return getService().getCommerceOrderItems(
			groupId, commerceAccountId, orderStatuses, start, end);
	}

	public static int getCommerceOrderItemsCount(long commerceOrderId)
		throws PortalException {

		return getService().getCommerceOrderItemsCount(commerceOrderId);
	}

	public static int getCommerceOrderItemsCount(
			long commerceOrderId, long cpInstanceId)
		throws PortalException {

		return getService().getCommerceOrderItemsCount(
			commerceOrderId, cpInstanceId);
	}

	public static int getCommerceOrderItemsCount(
			long groupId, long commerceAccountId, int[] orderStatuses)
		throws PortalException {

		return getService().getCommerceOrderItemsCount(
			groupId, commerceAccountId, orderStatuses);
	}

	public static java.math.BigDecimal getCommerceOrderItemsQuantity(
			long commerceOrderId)
		throws PortalException {

		return getService().getCommerceOrderItemsQuantity(commerceOrderId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static List<CommerceOrderItem> getParentCommerceOrderItems(
			long commerceOrderId, long parentCommerceOrderItemId, int start,
			int end, OrderByComparator<CommerceOrderItem> orderByComparator)
		throws PortalException {

		return getService().getParentCommerceOrderItems(
			commerceOrderId, parentCommerceOrderItemId, start, end,
			orderByComparator);
	}

	public static int getParentCommerceOrderItemsCount(
			long commerceOrderId, long parentCommerceOrderItemId)
		throws PortalException {

		return getService().getParentCommerceOrderItemsCount(
			commerceOrderId, parentCommerceOrderItemId);
	}

	public static List<CommerceOrderItem> getSupplierCommerceOrderItems(
			long customerCommerceOrderItemId, int start, int end)
		throws PortalException {

		return getService().getSupplierCommerceOrderItems(
			customerCommerceOrderItemId, start, end);
	}

	public static CommerceOrderItem importCommerceOrderItem(
			String externalReferenceCode, long commerceOrderItemId,
			long commerceOrderId, long cpInstanceId,
			String cpMeasurementUnitKey, String json,
			java.math.BigDecimal quantity, java.math.BigDecimal shippedQuantity,
			java.math.BigDecimal unitOfMeasureIncrementalOrderQuantity,
			String unitOfMeasureKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().importCommerceOrderItem(
			externalReferenceCode, commerceOrderItemId, commerceOrderId,
			cpInstanceId, cpMeasurementUnitKey, json, quantity, shippedQuantity,
			unitOfMeasureIncrementalOrderQuantity, unitOfMeasureKey,
			serviceContext);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommerceOrderItem> searchCommerceOrderItems(
				long commerceOrderId, long parentCommerceOrderItemId,
				String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCommerceOrderItems(
			commerceOrderId, parentCommerceOrderItemId, keywords, start, end,
			sort);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommerceOrderItem> searchCommerceOrderItems(
				long commerceOrderId, String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCommerceOrderItems(
			commerceOrderId, keywords, start, end, sort);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommerceOrderItem> searchCommerceOrderItems(
				long commerceOrderId, String name, String sku,
				boolean andOperator, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCommerceOrderItems(
			commerceOrderId, name, sku, andOperator, start, end, sort);
	}

	public static CommerceOrderItem updateCommerceOrderItem(
			long commerceOrderItemId, java.math.BigDecimal quantity,
			com.liferay.commerce.context.CommerceContext commerceContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommerceOrderItem(
			commerceOrderItemId, quantity, commerceContext, serviceContext);
	}

	public static CommerceOrderItem updateCommerceOrderItem(
			long commerceOrderItemId, long cpMeasurementUnitId,
			java.math.BigDecimal quantity,
			com.liferay.commerce.context.CommerceContext commerceContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommerceOrderItem(
			commerceOrderItemId, cpMeasurementUnitId, quantity, commerceContext,
			serviceContext);
	}

	public static CommerceOrderItem updateCommerceOrderItem(
			long commerceOrderItemId, long cpMeasurementUnitId,
			java.math.BigDecimal quantity,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommerceOrderItem(
			commerceOrderItemId, cpMeasurementUnitId, quantity, serviceContext);
	}

	public static CommerceOrderItem updateCommerceOrderItem(
			String externalReferenceCode, long commerceOrderItemId, String json,
			java.math.BigDecimal quantity,
			com.liferay.commerce.context.CommerceContext commerceContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommerceOrderItem(
			externalReferenceCode, commerceOrderItemId, json, quantity,
			commerceContext, serviceContext);
	}

	public static CommerceOrderItem updateCommerceOrderItemDeliveryDate(
			long commerceOrderItemId, java.util.Date requestedDeliveryDate)
		throws PortalException {

		return getService().updateCommerceOrderItemDeliveryDate(
			commerceOrderItemId, requestedDeliveryDate);
	}

	public static CommerceOrderItem updateCommerceOrderItemInfo(
			long commerceOrderItemId, long shippingAddressId,
			String deliveryGroupName, String printedNote)
		throws PortalException {

		return getService().updateCommerceOrderItemInfo(
			commerceOrderItemId, shippingAddressId, deliveryGroupName,
			printedNote);
	}

	public static CommerceOrderItem updateCommerceOrderItemInfo(
			long commerceOrderItemId, long shippingAddressId,
			String deliveryGroupName, String printedNote,
			int requestedDeliveryDateMonth, int requestedDeliveryDateDay,
			int requestedDeliveryDateYear)
		throws PortalException {

		return getService().updateCommerceOrderItemInfo(
			commerceOrderItemId, shippingAddressId, deliveryGroupName,
			printedNote, requestedDeliveryDateMonth, requestedDeliveryDateDay,
			requestedDeliveryDateYear);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public static CommerceOrderItem updateCommerceOrderItemInfo(
			long commerceOrderItemId, String deliveryGroupName,
			long shippingAddressId, String printedNote,
			int requestedDeliveryDateMonth, int requestedDeliveryDateDay,
			int requestedDeliveryDateYear, int requestedDeliveryDateHour,
			int requestedDeliveryDateMinute,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommerceOrderItemInfo(
			commerceOrderItemId, deliveryGroupName, shippingAddressId,
			printedNote, requestedDeliveryDateMonth, requestedDeliveryDateDay,
			requestedDeliveryDateYear, requestedDeliveryDateHour,
			requestedDeliveryDateMinute, serviceContext);
	}

	public static CommerceOrderItem updateCommerceOrderItemPrices(
			long commerceOrderItemId, java.math.BigDecimal discountAmount,
			java.math.BigDecimal discountPercentageLevel1,
			java.math.BigDecimal discountPercentageLevel2,
			java.math.BigDecimal discountPercentageLevel3,
			java.math.BigDecimal discountPercentageLevel4,
			java.math.BigDecimal finalPrice, java.math.BigDecimal promoPrice,
			java.math.BigDecimal unitPrice)
		throws PortalException {

		return getService().updateCommerceOrderItemPrices(
			commerceOrderItemId, discountAmount, discountPercentageLevel1,
			discountPercentageLevel2, discountPercentageLevel3,
			discountPercentageLevel4, finalPrice, promoPrice, unitPrice);
	}

	public static CommerceOrderItem updateCommerceOrderItemPrices(
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
		throws PortalException {

		return getService().updateCommerceOrderItemPrices(
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
	public static CommerceOrderItem updateCommerceOrderItemUnitPrice(
			long commerceOrderItemId, java.math.BigDecimal unitPrice)
		throws PortalException {

		return getService().updateCommerceOrderItemUnitPrice(
			commerceOrderItemId, unitPrice);
	}

	public static CommerceOrderItem updateCommerceOrderItemUnitPrice(
			long commerceOrderItemId, java.math.BigDecimal quantity,
			java.math.BigDecimal unitPrice)
		throws PortalException {

		return getService().updateCommerceOrderItemUnitPrice(
			commerceOrderItemId, quantity, unitPrice);
	}

	public static CommerceOrderItem updateCustomFields(
			long commerceOrderItemId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCustomFields(
			commerceOrderItemId, serviceContext);
	}

	public static CommerceOrderItem updateExternalReferenceCode(
			long commerceOrderItemId, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			commerceOrderItemId, externalReferenceCode);
	}

	public static CommerceOrderItemService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceOrderItemService> _serviceSnapshot =
		new Snapshot<>(
			CommerceOrderItemServiceUtil.class, CommerceOrderItemService.class);

}
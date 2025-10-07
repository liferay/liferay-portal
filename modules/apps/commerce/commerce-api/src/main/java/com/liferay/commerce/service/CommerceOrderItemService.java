/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.math.BigDecimal;

import java.util.Date;
import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for CommerceOrderItem. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceOrderItemServiceUtil
 * @generated
 */
@AccessControlled
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CommerceOrderItemService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.service.impl.CommerceOrderItemServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the commerce order item remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CommerceOrderItemServiceUtil} if injection and service tracking are not available.
	 */
	public CommerceOrderItem addCommerceOrderItem(
			long commerceOrderId, long cpInstanceId, String json,
			BigDecimal quantity, long replacedCPInstanceId,
			BigDecimal shippedQuantity, String unitOfMeasureKey,
			CommerceContext commerceContext, ServiceContext serviceContext)
		throws PortalException;

	public CommerceOrderItem addOrUpdateCommerceOrderItem(
			long commerceOrderId, long cpInstanceId, String json,
			BigDecimal quantity, long replacedCPInstanceId,
			BigDecimal shippedQuantity, String unitOfMeasureKey,
			CommerceContext commerceContext, ServiceContext serviceContext)
		throws PortalException;

	public int countSubscriptionCommerceOrderItems(long commerceOrderId)
		throws PortalException;

	public void deleteCommerceOrderItem(long commerceOrderItemId)
		throws PortalException;

	public void deleteCommerceOrderItem(
			long commerceOrderItemId, CommerceContext commerceContext)
		throws PortalException;

	public void deleteCommerceOrderItems(long commerceOrderId)
		throws PortalException;

	public void deleteMissingCommerceOrderItems(
			long commerceOrderId, Long[] commerceOrderItemIds,
			String[] externalReferenceCodes)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceOrderItem fetchCommerceOrderItem(long commerceOrderItemId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceOrderItem fetchCommerceOrderItemByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrderItem> getAvailableForShipmentCommerceOrderItems(
			long commerceOrderId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrderItem> getChildCommerceOrderItems(
			long parentCommerceOrderItemId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BigDecimal getCommerceInventoryWarehouseItemQuantity(
			long commerceOrderItemId, long commerceInventoryWarehouseId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceOrderItem getCommerceOrderItem(long commerceOrderItemId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrderItem> getCommerceOrderItems(
			long commerceOrderId, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrderItem> getCommerceOrderItems(
			long groupId, long commerceAccountId, int[] orderStatuses,
			int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceOrderItemsCount(long commerceOrderId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceOrderItemsCount(
			long commerceOrderId, long cpInstanceId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceOrderItemsCount(
			long groupId, long commerceAccountId, int[] orderStatuses)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BigDecimal getCommerceOrderItemsQuantity(long commerceOrderId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrderItem> getParentCommerceOrderItems(
			long commerceOrderId, long parentCommerceOrderItemId, int start,
			int end, OrderByComparator<CommerceOrderItem> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getParentCommerceOrderItemsCount(
			long commerceOrderId, long parentCommerceOrderItemId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrderItem> getSupplierCommerceOrderItems(
			long customerCommerceOrderItemId, int start, int end)
		throws PortalException;

	public CommerceOrderItem importCommerceOrderItem(
			String externalReferenceCode, long commerceOrderItemId,
			long commerceOrderId, long cpInstanceId,
			String cpMeasurementUnitKey, String json, BigDecimal quantity,
			BigDecimal shippedQuantity,
			BigDecimal unitOfMeasureIncrementalOrderQuantity,
			String unitOfMeasureKey, ServiceContext serviceContext)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<CommerceOrderItem> searchCommerceOrderItems(
			long commerceOrderId, long parentCommerceOrderItemId,
			String keywords, int start, int end, Sort sort)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<CommerceOrderItem> searchCommerceOrderItems(
			long commerceOrderId, String keywords, int start, int end,
			Sort sort)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<CommerceOrderItem> searchCommerceOrderItems(
			long commerceOrderId, String name, String sku, boolean andOperator,
			int start, int end, Sort sort)
		throws PortalException;

	public CommerceOrderItem updateCommerceOrderItem(
			long commerceOrderItemId, BigDecimal quantity,
			CommerceContext commerceContext, ServiceContext serviceContext)
		throws PortalException;

	public CommerceOrderItem updateCommerceOrderItem(
			long commerceOrderItemId, long cpMeasurementUnitId,
			BigDecimal quantity, CommerceContext commerceContext,
			ServiceContext serviceContext)
		throws PortalException;

	public CommerceOrderItem updateCommerceOrderItem(
			long commerceOrderItemId, long cpMeasurementUnitId,
			BigDecimal quantity, ServiceContext serviceContext)
		throws PortalException;

	public CommerceOrderItem updateCommerceOrderItem(
			String externalReferenceCode, long commerceOrderItemId, String json,
			BigDecimal quantity, CommerceContext commerceContext,
			ServiceContext serviceContext)
		throws PortalException;

	public CommerceOrderItem updateCommerceOrderItemDeliveryDate(
			long commerceOrderItemId, Date requestedDeliveryDate)
		throws PortalException;

	public CommerceOrderItem updateCommerceOrderItemInfo(
			long commerceOrderItemId, long shippingAddressId,
			String deliveryGroupName, String printedNote)
		throws PortalException;

	public CommerceOrderItem updateCommerceOrderItemInfo(
			long commerceOrderItemId, long shippingAddressId,
			String deliveryGroupName, String printedNote,
			int requestedDeliveryDateMonth, int requestedDeliveryDateDay,
			int requestedDeliveryDateYear)
		throws PortalException;

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public CommerceOrderItem updateCommerceOrderItemInfo(
			long commerceOrderItemId, String deliveryGroupName,
			long shippingAddressId, String printedNote,
			int requestedDeliveryDateMonth, int requestedDeliveryDateDay,
			int requestedDeliveryDateYear, int requestedDeliveryDateHour,
			int requestedDeliveryDateMinute, ServiceContext serviceContext)
		throws PortalException;

	public CommerceOrderItem updateCommerceOrderItemPrices(
			long commerceOrderItemId, BigDecimal discountAmount,
			BigDecimal discountPercentageLevel1,
			BigDecimal discountPercentageLevel2,
			BigDecimal discountPercentageLevel3,
			BigDecimal discountPercentageLevel4, BigDecimal finalPrice,
			BigDecimal promoPrice, BigDecimal unitPrice)
		throws PortalException;

	public CommerceOrderItem updateCommerceOrderItemPrices(
			long commerceOrderItemId, BigDecimal discountAmount,
			BigDecimal discountAmountWithTaxAmount,
			BigDecimal discountPercentageLevel1,
			BigDecimal discountPercentageLevel1WithTaxAmount,
			BigDecimal discountPercentageLevel2,
			BigDecimal discountPercentageLevel2WithTaxAmount,
			BigDecimal discountPercentageLevel3,
			BigDecimal discountPercentageLevel3WithTaxAmount,
			BigDecimal discountPercentageLevel4,
			BigDecimal discountPercentageLevel4WithTaxAmount,
			BigDecimal finalPrice, BigDecimal finalPriceWithTaxAmount,
			BigDecimal promoPrice, BigDecimal promoPriceWithTaxAmount,
			BigDecimal unitPrice, BigDecimal unitPriceWithTaxAmount)
		throws PortalException;

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public CommerceOrderItem updateCommerceOrderItemUnitPrice(
			long commerceOrderItemId, BigDecimal unitPrice)
		throws PortalException;

	public CommerceOrderItem updateCommerceOrderItemUnitPrice(
			long commerceOrderItemId, BigDecimal quantity, BigDecimal unitPrice)
		throws PortalException;

	public CommerceOrderItem updateCustomFields(
			long commerceOrderItemId, ServiceContext serviceContext)
		throws PortalException;

	public CommerceOrderItem updateExternalReferenceCode(
			long commerceOrderItemId, String externalReferenceCode)
		throws PortalException;

}
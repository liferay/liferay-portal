/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.exception.NoSuchOrderItemException;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Date;
import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for CommerceOrderItem. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceOrderItemLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CommerceOrderItemLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.service.impl.CommerceOrderItemLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the commerce order item local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CommerceOrderItemLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the commerce order item to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderItem the commerce order item
	 * @return the commerce order item that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrderItem addCommerceOrderItem(
		CommerceOrderItem commerceOrderItem);

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrderItem addCommerceOrderItem(
			long userId, long commerceOrderId, long cpInstanceId, String json,
			BigDecimal quantity, long replacedCPInstanceId,
			BigDecimal shippedQuantity, String unitOfMeasureKey,
			CommerceContext commerceContext, ServiceContext serviceContext)
		throws PortalException;

	public CommerceOrderItem addOrUpdateCommerceOrderItem(
			long userId, long commerceOrderId, long cpInstanceId, String json,
			BigDecimal quantity, long replacedCPInstanceId,
			BigDecimal shippedQuantity, String unitOfMeasureKey,
			CommerceContext commerceContext, ServiceContext serviceContext)
		throws PortalException;

	public int countSubscriptionCommerceOrderItems(long commerceOrderId);

	/**
	 * Creates a new commerce order item with the primary key. Does not add the commerce order item to the database.
	 *
	 * @param commerceOrderItemId the primary key for the new commerce order item
	 * @return the new commerce order item
	 */
	@Transactional(enabled = false)
	public CommerceOrderItem createCommerceOrderItem(long commerceOrderItemId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the commerce order item from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderItem the commerce order item
	 * @return the commerce order item that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public CommerceOrderItem deleteCommerceOrderItem(
		CommerceOrderItem commerceOrderItem);

	/**
	 * Deletes the commerce order item with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderItemId the primary key of the commerce order item
	 * @return the commerce order item that was removed
	 * @throws PortalException if a commerce order item with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public CommerceOrderItem deleteCommerceOrderItem(long commerceOrderItemId)
		throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	public CommerceOrderItem deleteCommerceOrderItem(
			long userId, CommerceOrderItem commerceOrderItem)
		throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	public CommerceOrderItem deleteCommerceOrderItem(
			long userId, CommerceOrderItem commerceOrderItem,
			CommerceContext commerceContext)
		throws PortalException;

	public CommerceOrderItem deleteCommerceOrderItem(
			long userId, long commerceOrderItemId)
		throws PortalException;

	public void deleteCommerceOrderItems(long userId, long commerceOrderId)
		throws PortalException;

	public void deleteCommerceOrderItemsByCPInstanceId(
			long userId, long cpInstanceId)
		throws PortalException;

	public void deleteMissingCommerceOrderItems(
			long userId, long commerceOrderId, Long[] commerceOrderItemIds,
			String[] externalReferenceCodes)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceOrderItem fetchCommerceOrderItem(long commerceOrderItemId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceOrderItem
		fetchCommerceOrderItemByCommerceInventoryBookedQuantityId(
			long commerceInventoryBookedQuantityId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceOrderItem fetchCommerceOrderItemByExternalReferenceCode(
		String externalReferenceCode, long companyId);

	/**
	 * Returns the commerce order item matching the UUID and group.
	 *
	 * @param uuid the commerce order item's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceOrderItem fetchCommerceOrderItemByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrderItem> getAvailableForShipmentCommerceOrderItems(
		long commerceOrderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrderItem> getChildCommerceOrderItems(
		long parentCommerceOrderItemId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BigDecimal getCommerceInventoryWarehouseItemQuantity(
			long commerceOrderItemId, long commerceInventoryWarehouseId)
		throws PortalException;

	/**
	 * Returns the commerce order item with the primary key.
	 *
	 * @param commerceOrderItemId the primary key of the commerce order item
	 * @return the commerce order item
	 * @throws PortalException if a commerce order item with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceOrderItem getCommerceOrderItem(long commerceOrderItemId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceOrderItem getCommerceOrderItemByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	/**
	 * Returns the commerce order item matching the UUID and group.
	 *
	 * @param uuid the commerce order item's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce order item
	 * @throws PortalException if a matching commerce order item could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceOrderItem getCommerceOrderItemByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns a range of all the commerce order items.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @return the range of commerce order items
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrderItem> getCommerceOrderItems(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrderItem> getCommerceOrderItems(
		long commerceOrderId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrderItem> getCommerceOrderItems(
		long commerceOrderId, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrderItem> getCommerceOrderItems(
		long cpInstanceId, int[] orderStatuses, String unitOfMeasureKey,
		int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrderItem> getCommerceOrderItems(
		long commerceOrderId, long cpInstanceId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrderItem> getCommerceOrderItems(
		long commerceOrderId, long cpInstanceId, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrderItem> getCommerceOrderItems(
		long groupId, long commerceAccountId, int[] orderStatuses, int start,
		int end);

	/**
	 * Returns all the commerce order items matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce order items
	 * @param companyId the primary key of the company
	 * @return the matching commerce order items, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrderItem> getCommerceOrderItemsByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of commerce order items matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce order items
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching commerce order items, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrderItem> getCommerceOrderItemsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator);

	/**
	 * Returns the number of commerce order items.
	 *
	 * @return the number of commerce order items
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceOrderItemsCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceOrderItemsCount(long commerceOrderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceOrderItemsCount(
		long commerceOrderId, long cpInstanceId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceOrderItemsCount(
		long groupId, long commerceAccountId, int[] orderStatuses);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BigDecimal getCommerceOrderItemsQuantity(long commerceOrderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Long> getCustomerCommerceOrderIds(long commerceOrderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCustomerCommerceOrderIdsCount(long commerceOrderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrderItem> getParentCommerceOrderItems(
		long commerceOrderId, long parentCommerceOrderItemId, int start,
		int end, OrderByComparator<CommerceOrderItem> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getParentCommerceOrderItemsCount(
		long commerceOrderId, long parentCommerceOrderItemId);

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrderItem> getSubscriptionCommerceOrderItems(
		long commerceOrderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Long> getSupplierCommerceOrderIds(long commerceOrderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getSupplierCommerceOrderIdsCount(long commerceOrderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrderItem> getSupplierCommerceOrderItems(
		long customerCommerceOrderItemId, int start, int end);

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrderItem importCommerceOrderItem(
			long userId, String externalReferenceCode, long commerceOrderItemId,
			long commerceOrderId, long cpInstanceId,
			String cpMeasurementUnitKey, String json, BigDecimal quantity,
			BigDecimal shippedQuantity,
			BigDecimal unitOfMeasureIncrementalOrderQuantity,
			String unitOfMeasureKey, ServiceContext serviceContext)
		throws PortalException;

	public CommerceOrderItem incrementShippedQuantity(
			long commerceOrderItemId, BigDecimal shippedQuantity)
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

	/**
	 * Updates the commerce order item in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderItem the commerce order item
	 * @return the commerce order item that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrderItem updateCommerceOrderItem(
		CommerceOrderItem commerceOrderItem);

	public CommerceOrderItem updateCommerceOrderItem(
			long commerceOrderItemId, long commerceInventoryBookedQuantityId)
		throws NoSuchOrderItemException;

	public CommerceOrderItem updateCommerceOrderItem(
			long userId, long commerceOrderItemId, BigDecimal quantity,
			CommerceContext commerceContext, ServiceContext serviceContext)
		throws PortalException;

	public CommerceOrderItem updateCommerceOrderItem(
			long userId, long commerceOrderItemId, long cpMeasurementUnitId,
			BigDecimal quantity, CommerceContext commerceContext,
			ServiceContext serviceContext)
		throws PortalException;

	public CommerceOrderItem updateCommerceOrderItem(
			long userId, long commerceOrderItemId, long cpMeasurementUnitId,
			BigDecimal quantity, ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrderItem updateCommerceOrderItem(
			long userId, long commerceOrderItemId, String json,
			BigDecimal quantity, ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrderItem updateCommerceOrderItem(
			String externalReferenceCode, long userId, long commerceOrderItemId,
			String json, BigDecimal quantity, CommerceContext commerceContext,
			ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrderItem updateCommerceOrderItemDeliveryDate(
			long commerceOrderItemId, Date requestedDeliveryDate)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrderItem updateCommerceOrderItemInfo(
			long commerceOrderItemId, long shippingAddressId,
			String deliveryGroupName, String printedNote)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
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

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrderItem updateCommerceOrderItemPrice(
			long commerceOrderItemId, CommerceContext commerceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrderItem updateCommerceOrderItemPrices(
			long commerceOrderItemId, BigDecimal discountAmount,
			BigDecimal discountPercentageLevel1,
			BigDecimal discountPercentageLevel2,
			BigDecimal discountPercentageLevel3,
			BigDecimal discountPercentageLevel4, BigDecimal finalPrice,
			BigDecimal promoPrice, BigDecimal unitPrice)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
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
			long userId, long commerceOrderItemId, BigDecimal quantity,
			BigDecimal unitPrice)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrderItem updateCustomFields(
			long commerceOrderItemId, ServiceContext serviceContext)
		throws PortalException;

	public CommerceOrderItem updateExternalReferenceCode(
			long commerceOrderItemId, String externalReferenceCode)
		throws PortalException;

}
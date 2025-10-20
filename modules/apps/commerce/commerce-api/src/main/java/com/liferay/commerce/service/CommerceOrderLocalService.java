/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.model.CommerceOrder;
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
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.InputStream;
import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for CommerceOrder. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceOrderLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CommerceOrderLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.service.impl.CommerceOrderLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the commerce order local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CommerceOrderLocalServiceUtil} if injection and service tracking are not available.
	 */
	public FileEntry addAttachmentFileEntry(
			String externalReferenceCode, long userId, long commerceOrderId,
			String fileName, InputStream inputStream)
		throws PortalException;

	/**
	 * Adds the commerce order to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrder the commerce order
	 * @return the commerce order that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrder addCommerceOrder(CommerceOrder commerceOrder);

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrder addCommerceOrder(
			long userId, long groupId, long billingAddressId,
			long commerceAccountId, String commerceCurrencyCode,
			long commerceOrderTypeId, long commerceShippingMethodId,
			long shippingAddressId, String commercePaymentMethodKey,
			String name, int orderDateMonth, int orderDateDay,
			int orderDateYear, int orderDateHour, int orderDateMinute,
			int orderStatus, int paymentStatus, String purchaseOrderNumber,
			BigDecimal shippingAmount, String shippingOptionName,
			BigDecimal shippingWithTaxAmount, BigDecimal subtotal,
			BigDecimal subtotalWithTaxAmount, BigDecimal taxAmount,
			BigDecimal total, BigDecimal totalWithTaxAmount,
			ServiceContext serviceContext)
		throws PortalException;

	public CommerceOrder addCommerceOrder(
			long userId, long groupId, long commerceAccountId,
			String commerceCurrencyCode, long commerceOrderTypeId)
		throws PortalException;

	public CommerceOrder addOrUpdateCommerceOrder(
			String externalReferenceCode, long userId, long groupId,
			long billingAddressId, long commerceAccountId,
			String commerceCurrencyCode, long commerceOrderTypeId,
			long commerceShippingMethodId, long shippingAddressId,
			String advanceStatus, String commercePaymentMethodKey, String name,
			int orderDateMonth, int orderDateDay, int orderDateYear,
			int orderDateHour, int orderDateMinute, int orderStatus,
			int paymentStatus, String purchaseOrderNumber,
			BigDecimal shippingAmount, String shippingOptionName,
			BigDecimal shippingWithTaxAmount, BigDecimal subtotal,
			BigDecimal subtotalWithTaxAmount, BigDecimal taxAmount,
			BigDecimal total, BigDecimal totalWithTaxAmount,
			CommerceContext commerceContext, ServiceContext serviceContext)
		throws PortalException;

	public CommerceOrder applyCouponCode(
			long commerceOrderId, String couponCode,
			CommerceContext commerceContext)
		throws PortalException;

	/**
	 * Creates a new commerce order with the primary key. Does not add the commerce order to the database.
	 *
	 * @param commerceOrderId the primary key for the new commerce order
	 * @return the new commerce order
	 */
	@Transactional(enabled = false)
	public CommerceOrder createCommerceOrder(long commerceOrderId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	public void deleteAttachmentFileEntry(
			long attachmentFileEntryId, long commerceOrderId)
		throws PortalException;

	/**
	 * Deletes the commerce order from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrder the commerce order
	 * @return the commerce order that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceOrder deleteCommerceOrder(CommerceOrder commerceOrder)
		throws PortalException;

	/**
	 * Deletes the commerce order with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderId the primary key of the commerce order
	 * @return the commerce order that was removed
	 * @throws PortalException if a commerce order with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public CommerceOrder deleteCommerceOrder(long commerceOrderId)
		throws PortalException;

	public void deleteCommerceOrders(long groupId) throws PortalException;

	/**
	 * @deprecated As of Athanasius (7.3.x), delete by commerceAccountId
	 */
	@Deprecated
	public void deleteCommerceOrders(long userId, Date date, int status);

	public void deleteCommerceOrdersByAccountId(
			long commerceAccountId, Date date, int status)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
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

	public CommerceOrder executeWorkflowTransition(
			long userId, long commerceOrderId, long workflowTaskId,
			String transitionName, String comment)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceOrder fetchCommerceOrder(long commerceOrderId);

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceOrder fetchCommerceOrder(
		long commerceAccountId, long groupId, int orderStatus);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceOrder fetchCommerceOrder(
		long commerceAccountId, long groupId, long userId, int orderStatus);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceOrder fetchCommerceOrderByExternalReferenceCode(
		String externalReferenceCode, long companyId);

	/**
	 * Returns the commerce order matching the UUID and group.
	 *
	 * @param uuid the commerce order's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceOrder fetchCommerceOrderByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns the commerce order with the primary key.
	 *
	 * @param commerceOrderId the primary key of the commerce order
	 * @return the commerce order
	 * @throws PortalException if a commerce order with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceOrder getCommerceOrder(long commerceOrderId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceOrder getCommerceOrderByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	/**
	 * Returns the commerce order matching the UUID and group.
	 *
	 * @param uuid the commerce order's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce order
	 * @throws PortalException if a matching commerce order could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceOrder getCommerceOrderByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns a range of all the commerce orders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of commerce orders
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrder> getCommerceOrders(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrder> getCommerceOrders(
		long groupId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrder> getCommerceOrders(
		long groupId, int[] orderStatuses);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrder> getCommerceOrders(
		long groupId, int[] orderStatuses, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrder> getCommerceOrders(
		long groupId, long commerceAccountId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrder> getCommerceOrders(
			long companyId, long groupId, long[] commerceAccountIds,
			String keywords, int[] orderStatuses, boolean excludeOrderStatus,
			int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrder> getCommerceOrders(
			long companyId, long groupId, long[] commerceAccountIds,
			String keywords, int[] orderStatuses, boolean excludeOrderStatus,
			int start, int end, Sort sort)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrder> getCommerceOrders(
		long groupId, String commercePaymentMethodKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrder> getCommerceOrdersByBillingAddress(
		long billingAddressId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrder> getCommerceOrdersByCommerceAccountId(
		long commerceAccountId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrder> getCommerceOrdersByShippingAddress(
		long shippingAddressId);

	/**
	 * Returns all the commerce orders matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce orders
	 * @param companyId the primary key of the company
	 * @return the matching commerce orders, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrder> getCommerceOrdersByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of commerce orders matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce orders
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching commerce orders, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrder> getCommerceOrdersByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator);

	/**
	 * Returns the number of commerce orders.
	 *
	 * @return the number of commerce orders
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceOrdersCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceOrdersCount(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceOrdersCount(long groupId, long commerceAccountId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long getCommerceOrdersCount(
			long companyId, long groupId, long[] commerceAccountIds,
			String keywords, int[] orderStatuses, boolean excludeOrderStatus)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceOrdersCountByCommerceAccountId(
		long commerceAccountId);

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

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrder> getShippedCommerceOrdersByCommerceShipmentId(
		long commerceShipmentId, int start, int end);

	/**
	 * @deprecated As of Mueller (7.2.x)
	 */
	@Deprecated
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceOrder> getUserCommerceOrders(
		long groupId, long userId, long commerceAccountId, Integer orderStatus,
		boolean excludeOrderStatus, String keywords, int start, int end);

	/**
	 * @deprecated As of Mueller (7.2.x)
	 */
	@Deprecated
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUserCommerceOrdersCount(
		long groupId, long userId, long commerceAccountId, Integer orderStatus,
		boolean excludeOrderStatus, String keywords);

	public void mergeGuestCommerceOrder(
			long userId, long guestCommerceOrderId, long userCommerceOrderId,
			CommerceContext commerceContext, ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrder recalculatePrice(
			long commerceOrderId, CommerceContext commerceContext)
		throws PortalException;

	public CommerceOrder reorderCommerceOrder(
			long userId, long commerceOrderId, CommerceContext commerceContext)
		throws PortalException;

	public CommerceOrder resetCommerceOrderAddresses(
			long commerceOrderId, boolean billingAddress,
			boolean shippingAddress)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrder resetCommerceOrderShipping(long commerceOrderId)
		throws PortalException;

	public void resetCommerceOrderShippingByAddressId(long addressId)
		throws PortalException;

	public CommerceOrder resetTermsAndConditions(
			long commerceOrderId, boolean resetDeliveryCommerceTerm,
			boolean resetPaymentCommerceTermEntry)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<CommerceOrder> searchCommerceOrders(
			SearchContext searchContext)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long searchCommerceOrdersCount(SearchContext searchContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrder updateAccount(
			long commerceOrderId, long userId, long commerceAccountId)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrder updateBillingAddress(
			long commerceOrderId, long billingAddressId)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrder updateBillingAddress(
			long commerceOrderId, long countryId, long regionId, String city,
			String description, String name, String phoneNumber, String street1,
			String street2, String street3, String subtype, String zip,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Updates the commerce order in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrder the commerce order
	 * @return the commerce order that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrder updateCommerceOrder(CommerceOrder commerceOrder);

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrder updateCommerceOrder(
			long userId, String externalReferenceCode, long commerceOrderId,
			long billingAddressId, long commerceAccountId,
			String commerceCurrencyCode, long commerceOrderTypeId,
			long commerceShippingMethodId, long deliveryCommerceTermEntryId,
			long paymentCommerceTermEntryId, long shippingAddressId,
			String advanceStatus, String commercePaymentMethodKey,
			String couponCode, String deliveryCommerceTermEntryDescription,
			String deliveryCommerceTermEntryName, Date lastPriceUpdateDate,
			boolean manuallyAdjusted, String name, Date orderDate,
			int orderStatus, String paymentCommerceTermEntryDescription,
			String paymentCommerceTermEntryName, int paymentStatus,
			String printedNote, String purchaseOrderNumber,
			Date requestedDeliveryDate, boolean shippable,
			BigDecimal shippingAmount, BigDecimal shippingDiscountAmount,
			BigDecimal shippingDiscountPercentageLevel1,
			BigDecimal shippingDiscountPercentageLevel2,
			BigDecimal shippingDiscountPercentageLevel3,
			BigDecimal shippingDiscountPercentageLevel4,
			BigDecimal shippingDiscountPercentageLevel1WithTaxAmount,
			BigDecimal shippingDiscountPercentageLevel2WithTaxAmount,
			BigDecimal shippingDiscountPercentageLevel3WithTaxAmount,
			BigDecimal shippingDiscountPercentageLevel4WithTaxAmount,
			BigDecimal shippingDiscountWithTaxAmount, String shippingOptionName,
			BigDecimal shippingWithTaxAmount, BigDecimal subtotal,
			BigDecimal subtotalDiscountAmount,
			BigDecimal subtotalDiscountPercentageLevel1,
			BigDecimal subtotalDiscountPercentageLevel2,
			BigDecimal subtotalDiscountPercentageLevel3,
			BigDecimal subtotalDiscountPercentageLevel4,
			BigDecimal subtotalDiscountPercentageLevel1WithTaxAmount,
			BigDecimal subtotalDiscountPercentageLevel2WithTaxAmount,
			BigDecimal subtotalDiscountPercentageLevel3WithTaxAmount,
			BigDecimal subtotalDiscountPercentageLevel4WithTaxAmount,
			BigDecimal subtotalDiscountWithTaxAmount,
			BigDecimal subtotalWithTaxAmount, BigDecimal taxAmount,
			BigDecimal total, BigDecimal totalDiscountAmount,
			BigDecimal totalDiscountPercentageLevel1,
			BigDecimal totalDiscountPercentageLevel2,
			BigDecimal totalDiscountPercentageLevel3,
			BigDecimal totalDiscountPercentageLevel4,
			BigDecimal totalDiscountPercentageLevel1WithTaxAmount,
			BigDecimal totalDiscountPercentageLevel2WithTaxAmount,
			BigDecimal totalDiscountPercentageLevel3WithTaxAmount,
			BigDecimal totalDiscountPercentageLevel4WithTaxAmount,
			BigDecimal totalDiscountWithTaxAmount,
			BigDecimal totalWithTaxAmount, String transactionId, int status,
			long statusByUserId, String statusByUserName, Date statusDate,
			boolean recalculate, CommerceContext commerceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrder updateCommerceOrder(
			String externalReferenceCode, long commerceOrderId,
			long billingAddressId, long commerceShippingMethodId,
			long shippingAddressId, String advanceStatus,
			String commercePaymentMethodKey, String name,
			String purchaseOrderNumber, BigDecimal shippingAmount,
			String shippingOptionName, BigDecimal subtotal, BigDecimal total)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrder updateCommerceOrder(
			String externalReferenceCode, long commerceOrderId,
			long billingAddressId, long commerceShippingMethodId,
			long shippingAddressId, String advanceStatus,
			String commercePaymentMethodKey, String name,
			String purchaseOrderNumber, BigDecimal shippingAmount,
			String shippingOptionName, BigDecimal shippingWithTaxAmount,
			BigDecimal subtotal, BigDecimal subtotalWithTaxAmount,
			BigDecimal taxAmount, BigDecimal total,
			BigDecimal totalDiscountAmount, BigDecimal totalWithTaxAmount)
		throws PortalException;

	public void updateCommerceOrderAddresses(long addressId)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrder updateCommerceOrderExternalReferenceCode(
			String externalReferenceCode, long commerceOrderId)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrder updateCommerceOrderPrices(
			long commerceOrderId, BigDecimal shippingAmount,
			BigDecimal shippingDiscountAmount,
			BigDecimal shippingDiscountPercentageLevel1,
			BigDecimal shippingDiscountPercentageLevel2,
			BigDecimal shippingDiscountPercentageLevel3,
			BigDecimal shippingDiscountPercentageLevel4, BigDecimal subtotal,
			BigDecimal subtotalDiscountAmount,
			BigDecimal subtotalDiscountPercentageLevel1,
			BigDecimal subtotalDiscountPercentageLevel2,
			BigDecimal subtotalDiscountPercentageLevel3,
			BigDecimal subtotalDiscountPercentageLevel4, BigDecimal taxAmount,
			BigDecimal total, BigDecimal totalDiscountAmount,
			BigDecimal totalDiscountPercentageLevel1,
			BigDecimal totalDiscountPercentageLevel2,
			BigDecimal totalDiscountPercentageLevel3,
			BigDecimal totalDiscountPercentageLevel4)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrder updateCommerceOrderPrices(
			long commerceOrderId, BigDecimal shippingAmount,
			BigDecimal shippingDiscountAmount,
			BigDecimal shippingDiscountPercentageLevel1,
			BigDecimal shippingDiscountPercentageLevel2,
			BigDecimal shippingDiscountPercentageLevel3,
			BigDecimal shippingDiscountPercentageLevel4,
			BigDecimal shippingDiscountPercentageLevel1WithTaxAmount,
			BigDecimal shippingDiscountPercentageLevel2WithTaxAmount,
			BigDecimal shippingDiscountPercentageLevel3WithTaxAmount,
			BigDecimal shippingDiscountPercentageLevel4WithTaxAmount,
			BigDecimal shippingDiscountWithTaxAmount,
			BigDecimal shippingWithTaxAmount, BigDecimal subtotal,
			BigDecimal subtotalDiscountAmount,
			BigDecimal subtotalDiscountPercentageLevel1,
			BigDecimal subtotalDiscountPercentageLevel2,
			BigDecimal subtotalDiscountPercentageLevel3,
			BigDecimal subtotalDiscountPercentageLevel4,
			BigDecimal subtotalDiscountPercentageLevel1WithTaxAmount,
			BigDecimal subtotalDiscountPercentageLevel2WithTaxAmount,
			BigDecimal subtotalDiscountPercentageLevel3WithTaxAmount,
			BigDecimal subtotalDiscountPercentageLevel4WithTaxAmount,
			BigDecimal subtotalDiscountWithTaxAmount,
			BigDecimal subtotalWithTaxAmount, BigDecimal taxAmount,
			BigDecimal total, BigDecimal totalDiscountAmount,
			BigDecimal totalDiscountPercentageLevel1,
			BigDecimal totalDiscountPercentageLevel2,
			BigDecimal totalDiscountPercentageLevel3,
			BigDecimal totalDiscountPercentageLevel4,
			BigDecimal totalDiscountPercentageLevel1WithTaxAmount,
			BigDecimal totalDiscountPercentageLevel2WithTaxAmount,
			BigDecimal totalDiscountPercentageLevel3WithTaxAmount,
			BigDecimal totalDiscountPercentageLevel4WithTaxAmount,
			BigDecimal totalDiscountWithTaxAmount,
			BigDecimal totalWithTaxAmount)
		throws PortalException;

	public CommerceOrder updateCommercePaymentMethodKey(
			long commerceOrderId, String commercePaymentMethodKey)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrder updateCommerceShippingMethod(
			long commerceOrderId, long commerceShippingMethodId,
			String commerceShippingOptionName, BigDecimal shippingAmount,
			CommerceContext commerceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrder updateCommerceShippingMethod(
			long commerceOrderId, long commerceShippingMethodId,
			String commerceShippingOptionName, CommerceContext commerceContext,
			Locale locale)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrder updateInfo(
			long commerceOrderId, String printedNote,
			int requestedDeliveryDateMonth, int requestedDeliveryDateDay,
			int requestedDeliveryDateYear, int requestedDeliveryDateHour,
			int requestedDeliveryDateMinute, ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrder updateOrderDate(
			long commerceOrderId, int orderDateMonth, int orderDateDay,
			int orderDateYear, int orderDateHour, int orderDateMinute,
			ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrder updatePaymentStatus(
			long userId, long commerceOrderId, int paymentStatus)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrder updatePaymentStatusAndTransactionId(
			long userId, long commerceOrderId, int paymentStatus,
			String transactionId)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrder updatePrintedNote(
			long commerceOrderId, String printedNote)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrder updatePurchaseOrderNumber(
			long commerceOrderId, String purchaseOrderNumber)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrder updateShippingAddress(
			long commerceOrderId, long shippingAddressId)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrder updateShippingAddress(
			long commerceOrderId, long countryId, long regionId, String city,
			String description, String name, String phoneNumber, String street1,
			String street2, String street3, String subtype, String zip,
			ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CommerceOrder updateStatus(
			long userId, long commerceOrderId, int status,
			Map<String, Serializable> workflowContext)
		throws PortalException;

	public CommerceOrder updateTermsAndConditions(
			long commerceOrderId, long deliveryCommerceTermEntryId,
			long paymentCommerceTermEntryId, String languageId)
		throws PortalException;

}
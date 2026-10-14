/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.InputStream;
import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for CommerceOrder. This utility wraps
 * <code>com.liferay.commerce.service.impl.CommerceOrderLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceOrderLocalService
 * @generated
 */
public class CommerceOrderLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.service.impl.CommerceOrderLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static com.liferay.portal.kernel.repository.model.FileEntry
			addAttachmentFileEntry(
				String externalReferenceCode, long userId, long commerceOrderId,
				String fileName, InputStream inputStream)
		throws PortalException {

		return getService().addAttachmentFileEntry(
			externalReferenceCode, userId, commerceOrderId, fileName,
			inputStream);
	}

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
	public static CommerceOrder addCommerceOrder(CommerceOrder commerceOrder) {
		return getService().addCommerceOrder(commerceOrder);
	}

	public static CommerceOrder addCommerceOrder(
			long userId, long groupId, long billingAddressId,
			long commerceAccountId, String commerceCurrencyCode,
			long commerceOrderTypeId, long commerceShippingMethodId,
			long shippingAddressId, String commercePaymentMethodKey,
			String name, int orderDateMonth, int orderDateDay,
			int orderDateYear, int orderDateHour, int orderDateMinute,
			int orderStatus, int paymentStatus, String purchaseOrderNumber,
			java.math.BigDecimal shippingAmount, String shippingOptionName,
			java.math.BigDecimal shippingWithTaxAmount,
			java.math.BigDecimal subtotal,
			java.math.BigDecimal subtotalWithTaxAmount,
			java.math.BigDecimal taxAmount, java.math.BigDecimal total,
			java.math.BigDecimal totalWithTaxAmount,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceOrder(
			userId, groupId, billingAddressId, commerceAccountId,
			commerceCurrencyCode, commerceOrderTypeId, commerceShippingMethodId,
			shippingAddressId, commercePaymentMethodKey, name, orderDateMonth,
			orderDateDay, orderDateYear, orderDateHour, orderDateMinute,
			orderStatus, paymentStatus, purchaseOrderNumber, shippingAmount,
			shippingOptionName, shippingWithTaxAmount, subtotal,
			subtotalWithTaxAmount, taxAmount, total, totalWithTaxAmount,
			serviceContext);
	}

	public static CommerceOrder addCommerceOrder(
			long userId, long groupId, long commerceAccountId,
			String commerceCurrencyCode, long commerceOrderTypeId)
		throws PortalException {

		return getService().addCommerceOrder(
			userId, groupId, commerceAccountId, commerceCurrencyCode,
			commerceOrderTypeId);
	}

	public static CommerceOrder addOrUpdateCommerceOrder(
			String externalReferenceCode, long userId, long groupId,
			long billingAddressId, long commerceAccountId,
			String commerceCurrencyCode, long commerceOrderTypeId,
			long commerceShippingMethodId, long shippingAddressId,
			String advanceStatus, String commercePaymentMethodKey, String name,
			int orderDateMonth, int orderDateDay, int orderDateYear,
			int orderDateHour, int orderDateMinute, int orderStatus,
			int paymentStatus, String purchaseOrderNumber,
			java.math.BigDecimal shippingAmount, String shippingOptionName,
			java.math.BigDecimal shippingWithTaxAmount,
			java.math.BigDecimal subtotal,
			java.math.BigDecimal subtotalWithTaxAmount,
			java.math.BigDecimal taxAmount, java.math.BigDecimal total,
			java.math.BigDecimal totalWithTaxAmount,
			com.liferay.commerce.context.CommerceContext commerceContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateCommerceOrder(
			externalReferenceCode, userId, groupId, billingAddressId,
			commerceAccountId, commerceCurrencyCode, commerceOrderTypeId,
			commerceShippingMethodId, shippingAddressId, advanceStatus,
			commercePaymentMethodKey, name, orderDateMonth, orderDateDay,
			orderDateYear, orderDateHour, orderDateMinute, orderStatus,
			paymentStatus, purchaseOrderNumber, shippingAmount,
			shippingOptionName, shippingWithTaxAmount, subtotal,
			subtotalWithTaxAmount, taxAmount, total, totalWithTaxAmount,
			commerceContext, serviceContext);
	}

	public static CommerceOrder applyCouponCode(
			long commerceOrderId, String couponCode,
			com.liferay.commerce.context.CommerceContext commerceContext)
		throws PortalException {

		return getService().applyCouponCode(
			commerceOrderId, couponCode, commerceContext);
	}

	/**
	 * Creates a new commerce order with the primary key. Does not add the commerce order to the database.
	 *
	 * @param commerceOrderId the primary key for the new commerce order
	 * @return the new commerce order
	 */
	public static CommerceOrder createCommerceOrder(long commerceOrderId) {
		return getService().createCommerceOrder(commerceOrderId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteAttachmentFileEntry(
			long attachmentFileEntryId, long commerceOrderId)
		throws PortalException {

		getService().deleteAttachmentFileEntry(
			attachmentFileEntryId, commerceOrderId);
	}

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
	public static CommerceOrder deleteCommerceOrder(CommerceOrder commerceOrder)
		throws PortalException {

		return getService().deleteCommerceOrder(commerceOrder);
	}

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
	public static CommerceOrder deleteCommerceOrder(long commerceOrderId)
		throws PortalException {

		return getService().deleteCommerceOrder(commerceOrderId);
	}

	public static void deleteCommerceOrders(long groupId)
		throws PortalException {

		getService().deleteCommerceOrders(groupId);
	}

	public static void deleteCommerceOrdersByAccountId(
			long commerceAccountId, java.util.Date date, int status)
		throws PortalException {

		getService().deleteCommerceOrdersByAccountId(
			commerceAccountId, date, status);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
	}

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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
	}

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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static CommerceOrder executeWorkflowTransition(
			long userId, long commerceOrderId, long workflowTaskId,
			String transitionName, String comment)
		throws PortalException {

		return getService().executeWorkflowTransition(
			userId, commerceOrderId, workflowTaskId, transitionName, comment);
	}

	public static CommerceOrder fetchCommerceOrder(long commerceOrderId) {
		return getService().fetchCommerceOrder(commerceOrderId);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public static CommerceOrder fetchCommerceOrder(
		long commerceAccountId, long groupId, int orderStatus) {

		return getService().fetchCommerceOrder(
			commerceAccountId, groupId, orderStatus);
	}

	public static CommerceOrder fetchCommerceOrder(
		long commerceAccountId, long groupId, long userId, int orderStatus) {

		return getService().fetchCommerceOrder(
			commerceAccountId, groupId, userId, orderStatus);
	}

	public static CommerceOrder fetchCommerceOrderByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchCommerceOrderByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce order matching the UUID and group.
	 *
	 * @param uuid the commerce order's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public static CommerceOrder fetchCommerceOrderByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchCommerceOrderByUuidAndGroupId(uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce order with the primary key.
	 *
	 * @param commerceOrderId the primary key of the commerce order
	 * @return the commerce order
	 * @throws PortalException if a commerce order with the primary key could not be found
	 */
	public static CommerceOrder getCommerceOrder(long commerceOrderId)
		throws PortalException {

		return getService().getCommerceOrder(commerceOrderId);
	}

	public static CommerceOrder getCommerceOrderByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCommerceOrderByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce order matching the UUID and group.
	 *
	 * @param uuid the commerce order's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce order
	 * @throws PortalException if a matching commerce order could not be found
	 */
	public static CommerceOrder getCommerceOrderByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getCommerceOrderByUuidAndGroupId(uuid, groupId);
	}

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
	public static List<CommerceOrder> getCommerceOrders(int start, int end) {
		return getService().getCommerceOrders(start, end);
	}

	public static List<CommerceOrder> getCommerceOrders(
		long groupId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return getService().getCommerceOrders(
			groupId, start, end, orderByComparator);
	}

	public static List<CommerceOrder> getCommerceOrders(
		long groupId, int[] orderStatuses) {

		return getService().getCommerceOrders(groupId, orderStatuses);
	}

	public static List<CommerceOrder> getCommerceOrders(
		long groupId, int[] orderStatuses, int start, int end) {

		return getService().getCommerceOrders(
			groupId, orderStatuses, start, end);
	}

	public static List<CommerceOrder> getCommerceOrders(
		long groupId, long commerceAccountId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return getService().getCommerceOrders(
			groupId, commerceAccountId, start, end, orderByComparator);
	}

	public static List<CommerceOrder> getCommerceOrders(
			long companyId, long groupId, long[] commerceAccountIds,
			String keywords, int[] orderStatuses, boolean excludeOrderStatus,
			int start, int end)
		throws PortalException {

		return getService().getCommerceOrders(
			companyId, groupId, commerceAccountIds, keywords, orderStatuses,
			excludeOrderStatus, start, end);
	}

	public static List<CommerceOrder> getCommerceOrders(
			long companyId, long groupId, long[] commerceAccountIds,
			String keywords, int[] orderStatuses, boolean excludeOrderStatus,
			int start, int end, com.liferay.portal.kernel.search.Sort sort)
		throws PortalException {

		return getService().getCommerceOrders(
			companyId, groupId, commerceAccountIds, keywords, orderStatuses,
			excludeOrderStatus, start, end, sort);
	}

	public static List<CommerceOrder> getCommerceOrdersByBillingAddress(
		long billingAddressId) {

		return getService().getCommerceOrdersByBillingAddress(billingAddressId);
	}

	public static List<CommerceOrder> getCommerceOrdersByCommerceAccountId(
		long commerceAccountId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return getService().getCommerceOrdersByCommerceAccountId(
			commerceAccountId, start, end, orderByComparator);
	}

	public static List<CommerceOrder> getCommerceOrdersByShippingAddress(
		long shippingAddressId) {

		return getService().getCommerceOrdersByShippingAddress(
			shippingAddressId);
	}

	/**
	 * Returns all the commerce orders matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce orders
	 * @param companyId the primary key of the company
	 * @return the matching commerce orders, or an empty list if no matches were found
	 */
	public static List<CommerceOrder> getCommerceOrdersByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getCommerceOrdersByUuidAndCompanyId(
			uuid, companyId);
	}

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
	public static List<CommerceOrder> getCommerceOrdersByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return getService().getCommerceOrdersByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce orders.
	 *
	 * @return the number of commerce orders
	 */
	public static int getCommerceOrdersCount() {
		return getService().getCommerceOrdersCount();
	}

	public static int getCommerceOrdersCount(long groupId) {
		return getService().getCommerceOrdersCount(groupId);
	}

	public static int getCommerceOrdersCount(
		long groupId, long commerceAccountId) {

		return getService().getCommerceOrdersCount(groupId, commerceAccountId);
	}

	public static long getCommerceOrdersCount(
			long companyId, long groupId, long[] commerceAccountIds,
			String keywords, int[] orderStatuses, boolean excludeOrderStatus)
		throws PortalException {

		return getService().getCommerceOrdersCount(
			companyId, groupId, commerceAccountIds, keywords, orderStatuses,
			excludeOrderStatus);
	}

	public static int getCommerceOrdersCountByCommerceAccountId(
		long commerceAccountId) {

		return getService().getCommerceOrdersCountByCommerceAccountId(
			commerceAccountId);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static List<CommerceOrder>
		getShippedCommerceOrdersByCommerceShipmentId(
			long commerceShipmentId, int start, int end) {

		return getService().getShippedCommerceOrdersByCommerceShipmentId(
			commerceShipmentId, start, end);
	}

	/**
	 * @deprecated As of Mueller (7.2.x)
	 */
	@Deprecated
	public static List<CommerceOrder> getUserCommerceOrders(
		long groupId, long userId, long commerceAccountId, Integer orderStatus,
		boolean excludeOrderStatus, String keywords, int start, int end) {

		return getService().getUserCommerceOrders(
			groupId, userId, commerceAccountId, orderStatus, excludeOrderStatus,
			keywords, start, end);
	}

	/**
	 * @deprecated As of Mueller (7.2.x)
	 */
	@Deprecated
	public static int getUserCommerceOrdersCount(
		long groupId, long userId, long commerceAccountId, Integer orderStatus,
		boolean excludeOrderStatus, String keywords) {

		return getService().getUserCommerceOrdersCount(
			groupId, userId, commerceAccountId, orderStatus, excludeOrderStatus,
			keywords);
	}

	public static void mergeGuestCommerceOrder(
			long userId, long guestCommerceOrderId, long userCommerceOrderId,
			com.liferay.commerce.context.CommerceContext commerceContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().mergeGuestCommerceOrder(
			userId, guestCommerceOrderId, userCommerceOrderId, commerceContext,
			serviceContext);
	}

	public static CommerceOrder recalculatePrice(
			long commerceOrderId,
			com.liferay.commerce.context.CommerceContext commerceContext)
		throws PortalException {

		return getService().recalculatePrice(commerceOrderId, commerceContext);
	}

	public static CommerceOrder reorderCommerceOrder(
			long userId, long commerceOrderId,
			com.liferay.commerce.context.CommerceContext commerceContext)
		throws PortalException {

		return getService().reorderCommerceOrder(
			userId, commerceOrderId, commerceContext);
	}

	public static CommerceOrder resetCommerceOrderAddresses(
			long commerceOrderId, boolean billingAddress,
			boolean shippingAddress)
		throws PortalException {

		return getService().resetCommerceOrderAddresses(
			commerceOrderId, billingAddress, shippingAddress);
	}

	public static CommerceOrder resetCommerceOrderShipping(long commerceOrderId)
		throws PortalException {

		return getService().resetCommerceOrderShipping(commerceOrderId);
	}

	public static void resetCommerceOrderShippingByAddressId(long addressId)
		throws PortalException {

		getService().resetCommerceOrderShippingByAddressId(addressId);
	}

	public static CommerceOrder resetTermsAndConditions(
			long commerceOrderId, boolean resetDeliveryCommerceTerm,
			boolean resetPaymentCommerceTermEntry)
		throws PortalException {

		return getService().resetTermsAndConditions(
			commerceOrderId, resetDeliveryCommerceTerm,
			resetPaymentCommerceTermEntry);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommerceOrder> searchCommerceOrders(
				com.liferay.portal.kernel.search.SearchContext searchContext)
			throws PortalException {

		return getService().searchCommerceOrders(searchContext);
	}

	public static long searchCommerceOrdersCount(
			com.liferay.portal.kernel.search.SearchContext searchContext)
		throws PortalException {

		return getService().searchCommerceOrdersCount(searchContext);
	}

	public static CommerceOrder updateAccount(
			long commerceOrderId, long userId, long commerceAccountId)
		throws PortalException {

		return getService().updateAccount(
			commerceOrderId, userId, commerceAccountId);
	}

	public static CommerceOrder updateBillingAddress(
			long commerceOrderId, long billingAddressId)
		throws PortalException {

		return getService().updateBillingAddress(
			commerceOrderId, billingAddressId);
	}

	public static CommerceOrder updateBillingAddress(
			long commerceOrderId, long countryId, long regionId, String city,
			String description, String name, String phoneNumber, String street1,
			String street2, String street3, String subtype, String zip,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateBillingAddress(
			commerceOrderId, countryId, regionId, city, description, name,
			phoneNumber, street1, street2, street3, subtype, zip,
			serviceContext);
	}

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
	public static CommerceOrder updateCommerceOrder(
		CommerceOrder commerceOrder) {

		return getService().updateCommerceOrder(commerceOrder);
	}

	public static CommerceOrder updateCommerceOrder(
			long userId, String externalReferenceCode, long commerceOrderId,
			long billingAddressId, long commerceAccountId,
			String commerceCurrencyCode, long commerceOrderTypeId,
			long commerceShippingMethodId, long deliveryCommerceTermEntryId,
			long paymentCommerceTermEntryId, long shippingAddressId,
			String advanceStatus, String commercePaymentMethodKey,
			String couponCode, String deliveryCommerceTermEntryDescription,
			String deliveryCommerceTermEntryName,
			java.util.Date lastPriceUpdateDate, boolean manuallyAdjusted,
			String name, java.util.Date orderDate, int orderStatus,
			String paymentCommerceTermEntryDescription,
			String paymentCommerceTermEntryName, int paymentStatus,
			String printedNote, String purchaseOrderNumber,
			java.util.Date requestedDeliveryDate, boolean shippable,
			java.math.BigDecimal shippingAmount,
			java.math.BigDecimal shippingDiscountAmount,
			java.math.BigDecimal shippingDiscountPercentageLevel1,
			java.math.BigDecimal shippingDiscountPercentageLevel2,
			java.math.BigDecimal shippingDiscountPercentageLevel3,
			java.math.BigDecimal shippingDiscountPercentageLevel4,
			java.math.BigDecimal shippingDiscountPercentageLevel1WithTaxAmount,
			java.math.BigDecimal shippingDiscountPercentageLevel2WithTaxAmount,
			java.math.BigDecimal shippingDiscountPercentageLevel3WithTaxAmount,
			java.math.BigDecimal shippingDiscountPercentageLevel4WithTaxAmount,
			java.math.BigDecimal shippingDiscountWithTaxAmount,
			String shippingOptionName,
			java.math.BigDecimal shippingWithTaxAmount,
			java.math.BigDecimal subtotal,
			java.math.BigDecimal subtotalDiscountAmount,
			java.math.BigDecimal subtotalDiscountPercentageLevel1,
			java.math.BigDecimal subtotalDiscountPercentageLevel2,
			java.math.BigDecimal subtotalDiscountPercentageLevel3,
			java.math.BigDecimal subtotalDiscountPercentageLevel4,
			java.math.BigDecimal subtotalDiscountPercentageLevel1WithTaxAmount,
			java.math.BigDecimal subtotalDiscountPercentageLevel2WithTaxAmount,
			java.math.BigDecimal subtotalDiscountPercentageLevel3WithTaxAmount,
			java.math.BigDecimal subtotalDiscountPercentageLevel4WithTaxAmount,
			java.math.BigDecimal subtotalDiscountWithTaxAmount,
			java.math.BigDecimal subtotalWithTaxAmount,
			java.math.BigDecimal taxAmount, java.math.BigDecimal total,
			java.math.BigDecimal totalDiscountAmount,
			java.math.BigDecimal totalDiscountPercentageLevel1,
			java.math.BigDecimal totalDiscountPercentageLevel2,
			java.math.BigDecimal totalDiscountPercentageLevel3,
			java.math.BigDecimal totalDiscountPercentageLevel4,
			java.math.BigDecimal totalDiscountPercentageLevel1WithTaxAmount,
			java.math.BigDecimal totalDiscountPercentageLevel2WithTaxAmount,
			java.math.BigDecimal totalDiscountPercentageLevel3WithTaxAmount,
			java.math.BigDecimal totalDiscountPercentageLevel4WithTaxAmount,
			java.math.BigDecimal totalDiscountWithTaxAmount,
			java.math.BigDecimal totalWithTaxAmount, String transactionId,
			int status, long statusByUserId, String statusByUserName,
			java.util.Date statusDate, boolean recalculate,
			com.liferay.commerce.context.CommerceContext commerceContext)
		throws PortalException {

		return getService().updateCommerceOrder(
			userId, externalReferenceCode, commerceOrderId, billingAddressId,
			commerceAccountId, commerceCurrencyCode, commerceOrderTypeId,
			commerceShippingMethodId, deliveryCommerceTermEntryId,
			paymentCommerceTermEntryId, shippingAddressId, advanceStatus,
			commercePaymentMethodKey, couponCode,
			deliveryCommerceTermEntryDescription, deliveryCommerceTermEntryName,
			lastPriceUpdateDate, manuallyAdjusted, name, orderDate, orderStatus,
			paymentCommerceTermEntryDescription, paymentCommerceTermEntryName,
			paymentStatus, printedNote, purchaseOrderNumber,
			requestedDeliveryDate, shippable, shippingAmount,
			shippingDiscountAmount, shippingDiscountPercentageLevel1,
			shippingDiscountPercentageLevel2, shippingDiscountPercentageLevel3,
			shippingDiscountPercentageLevel4,
			shippingDiscountPercentageLevel1WithTaxAmount,
			shippingDiscountPercentageLevel2WithTaxAmount,
			shippingDiscountPercentageLevel3WithTaxAmount,
			shippingDiscountPercentageLevel4WithTaxAmount,
			shippingDiscountWithTaxAmount, shippingOptionName,
			shippingWithTaxAmount, subtotal, subtotalDiscountAmount,
			subtotalDiscountPercentageLevel1, subtotalDiscountPercentageLevel2,
			subtotalDiscountPercentageLevel3, subtotalDiscountPercentageLevel4,
			subtotalDiscountPercentageLevel1WithTaxAmount,
			subtotalDiscountPercentageLevel2WithTaxAmount,
			subtotalDiscountPercentageLevel3WithTaxAmount,
			subtotalDiscountPercentageLevel4WithTaxAmount,
			subtotalDiscountWithTaxAmount, subtotalWithTaxAmount, taxAmount,
			total, totalDiscountAmount, totalDiscountPercentageLevel1,
			totalDiscountPercentageLevel2, totalDiscountPercentageLevel3,
			totalDiscountPercentageLevel4,
			totalDiscountPercentageLevel1WithTaxAmount,
			totalDiscountPercentageLevel2WithTaxAmount,
			totalDiscountPercentageLevel3WithTaxAmount,
			totalDiscountPercentageLevel4WithTaxAmount,
			totalDiscountWithTaxAmount, totalWithTaxAmount, transactionId,
			status, statusByUserId, statusByUserName, statusDate, recalculate,
			commerceContext);
	}

	public static CommerceOrder updateCommerceOrder(
			String externalReferenceCode, long commerceOrderId,
			long billingAddressId, long commerceShippingMethodId,
			long shippingAddressId, String advanceStatus,
			String commercePaymentMethodKey, String name,
			String purchaseOrderNumber, java.math.BigDecimal shippingAmount,
			String shippingOptionName, java.math.BigDecimal subtotal,
			java.math.BigDecimal total)
		throws PortalException {

		return getService().updateCommerceOrder(
			externalReferenceCode, commerceOrderId, billingAddressId,
			commerceShippingMethodId, shippingAddressId, advanceStatus,
			commercePaymentMethodKey, name, purchaseOrderNumber, shippingAmount,
			shippingOptionName, subtotal, total);
	}

	public static CommerceOrder updateCommerceOrder(
			String externalReferenceCode, long commerceOrderId,
			long billingAddressId, long commerceShippingMethodId,
			long shippingAddressId, String advanceStatus,
			String commercePaymentMethodKey, String name,
			String purchaseOrderNumber, java.math.BigDecimal shippingAmount,
			String shippingOptionName,
			java.math.BigDecimal shippingWithTaxAmount,
			java.math.BigDecimal subtotal,
			java.math.BigDecimal subtotalWithTaxAmount,
			java.math.BigDecimal taxAmount, java.math.BigDecimal total,
			java.math.BigDecimal totalDiscountAmount,
			java.math.BigDecimal totalWithTaxAmount)
		throws PortalException {

		return getService().updateCommerceOrder(
			externalReferenceCode, commerceOrderId, billingAddressId,
			commerceShippingMethodId, shippingAddressId, advanceStatus,
			commercePaymentMethodKey, name, purchaseOrderNumber, shippingAmount,
			shippingOptionName, shippingWithTaxAmount, subtotal,
			subtotalWithTaxAmount, taxAmount, total, totalDiscountAmount,
			totalWithTaxAmount);
	}

	public static void updateCommerceOrderAddresses(long addressId)
		throws PortalException {

		getService().updateCommerceOrderAddresses(addressId);
	}

	public static CommerceOrder updateCommerceOrderExternalReferenceCode(
			String externalReferenceCode, long commerceOrderId)
		throws PortalException {

		return getService().updateCommerceOrderExternalReferenceCode(
			externalReferenceCode, commerceOrderId);
	}

	public static CommerceOrder updateCommerceOrderPrices(
			long commerceOrderId, java.math.BigDecimal shippingAmount,
			java.math.BigDecimal shippingDiscountAmount,
			java.math.BigDecimal shippingDiscountPercentageLevel1,
			java.math.BigDecimal shippingDiscountPercentageLevel2,
			java.math.BigDecimal shippingDiscountPercentageLevel3,
			java.math.BigDecimal shippingDiscountPercentageLevel4,
			java.math.BigDecimal subtotal,
			java.math.BigDecimal subtotalDiscountAmount,
			java.math.BigDecimal subtotalDiscountPercentageLevel1,
			java.math.BigDecimal subtotalDiscountPercentageLevel2,
			java.math.BigDecimal subtotalDiscountPercentageLevel3,
			java.math.BigDecimal subtotalDiscountPercentageLevel4,
			java.math.BigDecimal taxAmount, java.math.BigDecimal total,
			java.math.BigDecimal totalDiscountAmount,
			java.math.BigDecimal totalDiscountPercentageLevel1,
			java.math.BigDecimal totalDiscountPercentageLevel2,
			java.math.BigDecimal totalDiscountPercentageLevel3,
			java.math.BigDecimal totalDiscountPercentageLevel4)
		throws PortalException {

		return getService().updateCommerceOrderPrices(
			commerceOrderId, shippingAmount, shippingDiscountAmount,
			shippingDiscountPercentageLevel1, shippingDiscountPercentageLevel2,
			shippingDiscountPercentageLevel3, shippingDiscountPercentageLevel4,
			subtotal, subtotalDiscountAmount, subtotalDiscountPercentageLevel1,
			subtotalDiscountPercentageLevel2, subtotalDiscountPercentageLevel3,
			subtotalDiscountPercentageLevel4, taxAmount, total,
			totalDiscountAmount, totalDiscountPercentageLevel1,
			totalDiscountPercentageLevel2, totalDiscountPercentageLevel3,
			totalDiscountPercentageLevel4);
	}

	public static CommerceOrder updateCommerceOrderPrices(
			long commerceOrderId, java.math.BigDecimal shippingAmount,
			java.math.BigDecimal shippingDiscountAmount,
			java.math.BigDecimal shippingDiscountPercentageLevel1,
			java.math.BigDecimal shippingDiscountPercentageLevel2,
			java.math.BigDecimal shippingDiscountPercentageLevel3,
			java.math.BigDecimal shippingDiscountPercentageLevel4,
			java.math.BigDecimal shippingDiscountPercentageLevel1WithTaxAmount,
			java.math.BigDecimal shippingDiscountPercentageLevel2WithTaxAmount,
			java.math.BigDecimal shippingDiscountPercentageLevel3WithTaxAmount,
			java.math.BigDecimal shippingDiscountPercentageLevel4WithTaxAmount,
			java.math.BigDecimal shippingDiscountWithTaxAmount,
			java.math.BigDecimal shippingWithTaxAmount,
			java.math.BigDecimal subtotal,
			java.math.BigDecimal subtotalDiscountAmount,
			java.math.BigDecimal subtotalDiscountPercentageLevel1,
			java.math.BigDecimal subtotalDiscountPercentageLevel2,
			java.math.BigDecimal subtotalDiscountPercentageLevel3,
			java.math.BigDecimal subtotalDiscountPercentageLevel4,
			java.math.BigDecimal subtotalDiscountPercentageLevel1WithTaxAmount,
			java.math.BigDecimal subtotalDiscountPercentageLevel2WithTaxAmount,
			java.math.BigDecimal subtotalDiscountPercentageLevel3WithTaxAmount,
			java.math.BigDecimal subtotalDiscountPercentageLevel4WithTaxAmount,
			java.math.BigDecimal subtotalDiscountWithTaxAmount,
			java.math.BigDecimal subtotalWithTaxAmount,
			java.math.BigDecimal taxAmount, java.math.BigDecimal total,
			java.math.BigDecimal totalDiscountAmount,
			java.math.BigDecimal totalDiscountPercentageLevel1,
			java.math.BigDecimal totalDiscountPercentageLevel2,
			java.math.BigDecimal totalDiscountPercentageLevel3,
			java.math.BigDecimal totalDiscountPercentageLevel4,
			java.math.BigDecimal totalDiscountPercentageLevel1WithTaxAmount,
			java.math.BigDecimal totalDiscountPercentageLevel2WithTaxAmount,
			java.math.BigDecimal totalDiscountPercentageLevel3WithTaxAmount,
			java.math.BigDecimal totalDiscountPercentageLevel4WithTaxAmount,
			java.math.BigDecimal totalDiscountWithTaxAmount,
			java.math.BigDecimal totalWithTaxAmount)
		throws PortalException {

		return getService().updateCommerceOrderPrices(
			commerceOrderId, shippingAmount, shippingDiscountAmount,
			shippingDiscountPercentageLevel1, shippingDiscountPercentageLevel2,
			shippingDiscountPercentageLevel3, shippingDiscountPercentageLevel4,
			shippingDiscountPercentageLevel1WithTaxAmount,
			shippingDiscountPercentageLevel2WithTaxAmount,
			shippingDiscountPercentageLevel3WithTaxAmount,
			shippingDiscountPercentageLevel4WithTaxAmount,
			shippingDiscountWithTaxAmount, shippingWithTaxAmount, subtotal,
			subtotalDiscountAmount, subtotalDiscountPercentageLevel1,
			subtotalDiscountPercentageLevel2, subtotalDiscountPercentageLevel3,
			subtotalDiscountPercentageLevel4,
			subtotalDiscountPercentageLevel1WithTaxAmount,
			subtotalDiscountPercentageLevel2WithTaxAmount,
			subtotalDiscountPercentageLevel3WithTaxAmount,
			subtotalDiscountPercentageLevel4WithTaxAmount,
			subtotalDiscountWithTaxAmount, subtotalWithTaxAmount, taxAmount,
			total, totalDiscountAmount, totalDiscountPercentageLevel1,
			totalDiscountPercentageLevel2, totalDiscountPercentageLevel3,
			totalDiscountPercentageLevel4,
			totalDiscountPercentageLevel1WithTaxAmount,
			totalDiscountPercentageLevel2WithTaxAmount,
			totalDiscountPercentageLevel3WithTaxAmount,
			totalDiscountPercentageLevel4WithTaxAmount,
			totalDiscountWithTaxAmount, totalWithTaxAmount);
	}

	public static CommerceOrder updateCommercePaymentMethodKey(
			long commerceOrderId, String commercePaymentMethodKey)
		throws PortalException {

		return getService().updateCommercePaymentMethodKey(
			commerceOrderId, commercePaymentMethodKey);
	}

	public static CommerceOrder updateCommerceShippingMethod(
			long commerceOrderId, long commerceShippingMethodId,
			String commerceShippingOptionName,
			java.math.BigDecimal shippingAmount,
			com.liferay.commerce.context.CommerceContext commerceContext)
		throws PortalException {

		return getService().updateCommerceShippingMethod(
			commerceOrderId, commerceShippingMethodId,
			commerceShippingOptionName, shippingAmount, commerceContext);
	}

	public static CommerceOrder updateCommerceShippingMethod(
			long commerceOrderId, long commerceShippingMethodId,
			String commerceShippingOptionName,
			com.liferay.commerce.context.CommerceContext commerceContext,
			java.util.Locale locale)
		throws PortalException {

		return getService().updateCommerceShippingMethod(
			commerceOrderId, commerceShippingMethodId,
			commerceShippingOptionName, commerceContext, locale);
	}

	public static CommerceOrder updateInfo(
			long commerceOrderId, String printedNote,
			int requestedDeliveryDateMonth, int requestedDeliveryDateDay,
			int requestedDeliveryDateYear, int requestedDeliveryDateHour,
			int requestedDeliveryDateMinute,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateInfo(
			commerceOrderId, printedNote, requestedDeliveryDateMonth,
			requestedDeliveryDateDay, requestedDeliveryDateYear,
			requestedDeliveryDateHour, requestedDeliveryDateMinute,
			serviceContext);
	}

	public static CommerceOrder updateOrderDate(
			long commerceOrderId, int orderDateMonth, int orderDateDay,
			int orderDateYear, int orderDateHour, int orderDateMinute,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateOrderDate(
			commerceOrderId, orderDateMonth, orderDateDay, orderDateYear,
			orderDateHour, orderDateMinute, serviceContext);
	}

	public static CommerceOrder updatePaymentStatus(
			long userId, long commerceOrderId, int paymentStatus)
		throws PortalException {

		return getService().updatePaymentStatus(
			userId, commerceOrderId, paymentStatus);
	}

	public static CommerceOrder updatePaymentStatusAndTransactionId(
			long userId, long commerceOrderId, int paymentStatus,
			String transactionId)
		throws PortalException {

		return getService().updatePaymentStatusAndTransactionId(
			userId, commerceOrderId, paymentStatus, transactionId);
	}

	public static CommerceOrder updatePrintedNote(
			long commerceOrderId, String printedNote)
		throws PortalException {

		return getService().updatePrintedNote(commerceOrderId, printedNote);
	}

	public static CommerceOrder updatePurchaseOrderNumber(
			long commerceOrderId, String purchaseOrderNumber)
		throws PortalException {

		return getService().updatePurchaseOrderNumber(
			commerceOrderId, purchaseOrderNumber);
	}

	public static CommerceOrder updateShippingAddress(
			long commerceOrderId, long shippingAddressId)
		throws PortalException {

		return getService().updateShippingAddress(
			commerceOrderId, shippingAddressId);
	}

	public static CommerceOrder updateShippingAddress(
			long commerceOrderId, long countryId, long regionId, String city,
			String description, String name, String phoneNumber, String street1,
			String street2, String street3, String subtype, String zip,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateShippingAddress(
			commerceOrderId, countryId, regionId, city, description, name,
			phoneNumber, street1, street2, street3, subtype, zip,
			serviceContext);
	}

	public static CommerceOrder updateStatus(
			long userId, long commerceOrderId, int status,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		return getService().updateStatus(
			userId, commerceOrderId, status, workflowContext);
	}

	public static CommerceOrder updateTermsAndConditions(
			long commerceOrderId, long deliveryCommerceTermEntryId,
			long paymentCommerceTermEntryId, String languageId)
		throws PortalException {

		return getService().updateTermsAndConditions(
			commerceOrderId, deliveryCommerceTermEntryId,
			paymentCommerceTermEntryId, languageId);
	}

	public static CommerceOrderLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceOrderLocalService> _serviceSnapshot =
		new Snapshot<>(
			CommerceOrderLocalServiceUtil.class,
			CommerceOrderLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:636412039
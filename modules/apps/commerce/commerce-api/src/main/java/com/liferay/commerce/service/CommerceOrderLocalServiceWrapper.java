/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceOrderLocalService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceOrderLocalService
 * @generated
 */
public class CommerceOrderLocalServiceWrapper
	implements CommerceOrderLocalService,
			   ServiceWrapper<CommerceOrderLocalService> {

	public CommerceOrderLocalServiceWrapper() {
		this(null);
	}

	public CommerceOrderLocalServiceWrapper(
		CommerceOrderLocalService commerceOrderLocalService) {

		_commerceOrderLocalService = commerceOrderLocalService;
	}

	@Override
	public com.liferay.portal.kernel.repository.model.FileEntry
			addAttachmentFileEntry(
				String externalReferenceCode, long userId, long commerceOrderId,
				String fileName, java.io.InputStream inputStream)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.addAttachmentFileEntry(
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
	@Override
	public com.liferay.commerce.model.CommerceOrder addCommerceOrder(
		com.liferay.commerce.model.CommerceOrder commerceOrder) {

		return _commerceOrderLocalService.addCommerceOrder(commerceOrder);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder addCommerceOrder(
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.addCommerceOrder(
			userId, groupId, billingAddressId, commerceAccountId,
			commerceCurrencyCode, commerceOrderTypeId, commerceShippingMethodId,
			shippingAddressId, commercePaymentMethodKey, name, orderDateMonth,
			orderDateDay, orderDateYear, orderDateHour, orderDateMinute,
			orderStatus, paymentStatus, purchaseOrderNumber, shippingAmount,
			shippingOptionName, shippingWithTaxAmount, subtotal,
			subtotalWithTaxAmount, taxAmount, total, totalWithTaxAmount,
			serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder addCommerceOrder(
			long userId, long groupId, long commerceAccountId,
			String commerceCurrencyCode, long commerceOrderTypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.addCommerceOrder(
			userId, groupId, commerceAccountId, commerceCurrencyCode,
			commerceOrderTypeId);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder addOrUpdateCommerceOrder(
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.addOrUpdateCommerceOrder(
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

	@Override
	public com.liferay.commerce.model.CommerceOrder applyCouponCode(
			long commerceOrderId, String couponCode,
			com.liferay.commerce.context.CommerceContext commerceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.applyCouponCode(
			commerceOrderId, couponCode, commerceContext);
	}

	/**
	 * Creates a new commerce order with the primary key. Does not add the commerce order to the database.
	 *
	 * @param commerceOrderId the primary key for the new commerce order
	 * @return the new commerce order
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrder createCommerceOrder(
		long commerceOrderId) {

		return _commerceOrderLocalService.createCommerceOrder(commerceOrderId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deleteAttachmentFileEntry(
			long attachmentFileEntryId, long commerceOrderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceOrderLocalService.deleteAttachmentFileEntry(
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
	@Override
	public com.liferay.commerce.model.CommerceOrder deleteCommerceOrder(
			com.liferay.commerce.model.CommerceOrder commerceOrder)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.deleteCommerceOrder(commerceOrder);
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
	@Override
	public com.liferay.commerce.model.CommerceOrder deleteCommerceOrder(
			long commerceOrderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.deleteCommerceOrder(commerceOrderId);
	}

	@Override
	public void deleteCommerceOrders(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceOrderLocalService.deleteCommerceOrders(groupId);
	}

	@Override
	public void deleteCommerceOrdersByAccountId(
			long commerceAccountId, java.util.Date date, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceOrderLocalService.deleteCommerceOrdersByAccountId(
			commerceAccountId, date, status);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceOrderLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceOrderLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceOrderLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _commerceOrderLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _commerceOrderLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _commerceOrderLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _commerceOrderLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _commerceOrderLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder executeWorkflowTransition(
			long userId, long commerceOrderId, long workflowTaskId,
			String transitionName, String comment)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.executeWorkflowTransition(
			userId, commerceOrderId, workflowTaskId, transitionName, comment);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder fetchCommerceOrder(
		long commerceOrderId) {

		return _commerceOrderLocalService.fetchCommerceOrder(commerceOrderId);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public com.liferay.commerce.model.CommerceOrder fetchCommerceOrder(
		long commerceAccountId, long groupId, int orderStatus) {

		return _commerceOrderLocalService.fetchCommerceOrder(
			commerceAccountId, groupId, orderStatus);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder fetchCommerceOrder(
		long commerceAccountId, long groupId, long userId, int orderStatus) {

		return _commerceOrderLocalService.fetchCommerceOrder(
			commerceAccountId, groupId, userId, orderStatus);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder
		fetchCommerceOrderByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _commerceOrderLocalService.
			fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce order matching the UUID and group.
	 *
	 * @param uuid the commerce order's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrder
		fetchCommerceOrderByUuidAndGroupId(String uuid, long groupId) {

		return _commerceOrderLocalService.fetchCommerceOrderByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceOrderLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce order with the primary key.
	 *
	 * @param commerceOrderId the primary key of the commerce order
	 * @return the commerce order
	 * @throws PortalException if a commerce order with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrder getCommerceOrder(
			long commerceOrderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.getCommerceOrder(commerceOrderId);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder
			getCommerceOrderByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.
			getCommerceOrderByExternalReferenceCode(
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
	@Override
	public com.liferay.commerce.model.CommerceOrder
			getCommerceOrderByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.getCommerceOrderByUuidAndGroupId(
			uuid, groupId);
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
	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrder>
		getCommerceOrders(int start, int end) {

		return _commerceOrderLocalService.getCommerceOrders(start, end);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrder>
		getCommerceOrders(
			long groupId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.commerce.model.CommerceOrder> orderByComparator) {

		return _commerceOrderLocalService.getCommerceOrders(
			groupId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrder>
		getCommerceOrders(long groupId, int[] orderStatuses) {

		return _commerceOrderLocalService.getCommerceOrders(
			groupId, orderStatuses);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrder>
		getCommerceOrders(
			long groupId, int[] orderStatuses, int start, int end) {

		return _commerceOrderLocalService.getCommerceOrders(
			groupId, orderStatuses, start, end);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrder>
		getCommerceOrders(
			long groupId, long commerceAccountId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.commerce.model.CommerceOrder> orderByComparator) {

		return _commerceOrderLocalService.getCommerceOrders(
			groupId, commerceAccountId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrder>
			getCommerceOrders(
				long companyId, long groupId, long[] commerceAccountIds,
				String keywords, int[] orderStatuses,
				boolean excludeOrderStatus, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.getCommerceOrders(
			companyId, groupId, commerceAccountIds, keywords, orderStatuses,
			excludeOrderStatus, start, end);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrder>
			getCommerceOrders(
				long companyId, long groupId, long[] commerceAccountIds,
				String keywords, int[] orderStatuses,
				boolean excludeOrderStatus, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.getCommerceOrders(
			companyId, groupId, commerceAccountIds, keywords, orderStatuses,
			excludeOrderStatus, start, end, sort);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrder>
		getCommerceOrdersByBillingAddress(long billingAddressId) {

		return _commerceOrderLocalService.getCommerceOrdersByBillingAddress(
			billingAddressId);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrder>
		getCommerceOrdersByCommerceAccountId(
			long commerceAccountId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.commerce.model.CommerceOrder> orderByComparator) {

		return _commerceOrderLocalService.getCommerceOrdersByCommerceAccountId(
			commerceAccountId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrder>
		getCommerceOrdersByShippingAddress(long shippingAddressId) {

		return _commerceOrderLocalService.getCommerceOrdersByShippingAddress(
			shippingAddressId);
	}

	/**
	 * Returns all the commerce orders matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce orders
	 * @param companyId the primary key of the company
	 * @return the matching commerce orders, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrder>
		getCommerceOrdersByUuidAndCompanyId(String uuid, long companyId) {

		return _commerceOrderLocalService.getCommerceOrdersByUuidAndCompanyId(
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
	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrder>
		getCommerceOrdersByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.commerce.model.CommerceOrder> orderByComparator) {

		return _commerceOrderLocalService.getCommerceOrdersByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce orders.
	 *
	 * @return the number of commerce orders
	 */
	@Override
	public int getCommerceOrdersCount() {
		return _commerceOrderLocalService.getCommerceOrdersCount();
	}

	@Override
	public int getCommerceOrdersCount(long groupId) {
		return _commerceOrderLocalService.getCommerceOrdersCount(groupId);
	}

	@Override
	public int getCommerceOrdersCount(long groupId, long commerceAccountId) {
		return _commerceOrderLocalService.getCommerceOrdersCount(
			groupId, commerceAccountId);
	}

	@Override
	public long getCommerceOrdersCount(
			long companyId, long groupId, long[] commerceAccountIds,
			String keywords, int[] orderStatuses, boolean excludeOrderStatus)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.getCommerceOrdersCount(
			companyId, groupId, commerceAccountIds, keywords, orderStatuses,
			excludeOrderStatus);
	}

	@Override
	public int getCommerceOrdersCountByCommerceAccountId(
		long commerceAccountId) {

		return _commerceOrderLocalService.
			getCommerceOrdersCountByCommerceAccountId(commerceAccountId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commerceOrderLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceOrderLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceOrderLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrder>
		getShippedCommerceOrdersByCommerceShipmentId(
			long commerceShipmentId, int start, int end) {

		return _commerceOrderLocalService.
			getShippedCommerceOrdersByCommerceShipmentId(
				commerceShipmentId, start, end);
	}

	/**
	 * @deprecated As of Mueller (7.2.x)
	 */
	@Deprecated
	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrder>
		getUserCommerceOrders(
			long groupId, long userId, long commerceAccountId,
			Integer orderStatus, boolean excludeOrderStatus, String keywords,
			int start, int end) {

		return _commerceOrderLocalService.getUserCommerceOrders(
			groupId, userId, commerceAccountId, orderStatus, excludeOrderStatus,
			keywords, start, end);
	}

	/**
	 * @deprecated As of Mueller (7.2.x)
	 */
	@Deprecated
	@Override
	public int getUserCommerceOrdersCount(
		long groupId, long userId, long commerceAccountId, Integer orderStatus,
		boolean excludeOrderStatus, String keywords) {

		return _commerceOrderLocalService.getUserCommerceOrdersCount(
			groupId, userId, commerceAccountId, orderStatus, excludeOrderStatus,
			keywords);
	}

	@Override
	public void mergeGuestCommerceOrder(
			long userId, long guestCommerceOrderId, long userCommerceOrderId,
			com.liferay.commerce.context.CommerceContext commerceContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceOrderLocalService.mergeGuestCommerceOrder(
			userId, guestCommerceOrderId, userCommerceOrderId, commerceContext,
			serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder recalculatePrice(
			long commerceOrderId,
			com.liferay.commerce.context.CommerceContext commerceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.recalculatePrice(
			commerceOrderId, commerceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder reorderCommerceOrder(
			long userId, long commerceOrderId,
			com.liferay.commerce.context.CommerceContext commerceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.reorderCommerceOrder(
			userId, commerceOrderId, commerceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder resetCommerceOrderAddresses(
			long commerceOrderId, boolean billingAddress,
			boolean shippingAddress)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.resetCommerceOrderAddresses(
			commerceOrderId, billingAddress, shippingAddress);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder resetCommerceOrderShipping(
			long commerceOrderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.resetCommerceOrderShipping(
			commerceOrderId);
	}

	@Override
	public void resetCommerceOrderShippingByAddressId(long addressId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceOrderLocalService.resetCommerceOrderShippingByAddressId(
			addressId);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder resetTermsAndConditions(
			long commerceOrderId, boolean resetDeliveryCommerceTerm,
			boolean resetPaymentCommerceTermEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.resetTermsAndConditions(
			commerceOrderId, resetDeliveryCommerceTerm,
			resetPaymentCommerceTermEntry);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.commerce.model.CommerceOrder> searchCommerceOrders(
				com.liferay.portal.kernel.search.SearchContext searchContext)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.searchCommerceOrders(searchContext);
	}

	@Override
	public long searchCommerceOrdersCount(
			com.liferay.portal.kernel.search.SearchContext searchContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.searchCommerceOrdersCount(
			searchContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder updateAccount(
			long commerceOrderId, long userId, long commerceAccountId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.updateAccount(
			commerceOrderId, userId, commerceAccountId);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder updateBillingAddress(
			long commerceOrderId, long billingAddressId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.updateBillingAddress(
			commerceOrderId, billingAddressId);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder updateBillingAddress(
			long commerceOrderId, long countryId, long regionId, String city,
			String description, String name, String phoneNumber, String street1,
			String street2, String street3, String subtype, String zip,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.updateBillingAddress(
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
	@Override
	public com.liferay.commerce.model.CommerceOrder updateCommerceOrder(
		com.liferay.commerce.model.CommerceOrder commerceOrder) {

		return _commerceOrderLocalService.updateCommerceOrder(commerceOrder);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder updateCommerceOrder(
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.updateCommerceOrder(
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

	@Override
	public com.liferay.commerce.model.CommerceOrder updateCommerceOrder(
			String externalReferenceCode, long commerceOrderId,
			long billingAddressId, long commerceShippingMethodId,
			long shippingAddressId, String advanceStatus,
			String commercePaymentMethodKey, String name,
			String purchaseOrderNumber, java.math.BigDecimal shippingAmount,
			String shippingOptionName, java.math.BigDecimal subtotal,
			java.math.BigDecimal total)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.updateCommerceOrder(
			externalReferenceCode, commerceOrderId, billingAddressId,
			commerceShippingMethodId, shippingAddressId, advanceStatus,
			commercePaymentMethodKey, name, purchaseOrderNumber, shippingAmount,
			shippingOptionName, subtotal, total);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder updateCommerceOrder(
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.updateCommerceOrder(
			externalReferenceCode, commerceOrderId, billingAddressId,
			commerceShippingMethodId, shippingAddressId, advanceStatus,
			commercePaymentMethodKey, name, purchaseOrderNumber, shippingAmount,
			shippingOptionName, shippingWithTaxAmount, subtotal,
			subtotalWithTaxAmount, taxAmount, total, totalDiscountAmount,
			totalWithTaxAmount);
	}

	@Override
	public void updateCommerceOrderAddresses(long addressId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceOrderLocalService.updateCommerceOrderAddresses(addressId);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder
			updateCommerceOrderExternalReferenceCode(
				String externalReferenceCode, long commerceOrderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.
			updateCommerceOrderExternalReferenceCode(
				externalReferenceCode, commerceOrderId);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder updateCommerceOrderPrices(
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.updateCommerceOrderPrices(
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

	@Override
	public com.liferay.commerce.model.CommerceOrder updateCommerceOrderPrices(
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.updateCommerceOrderPrices(
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

	@Override
	public com.liferay.commerce.model.CommerceOrder
			updateCommercePaymentMethodKey(
				long commerceOrderId, String commercePaymentMethodKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.updateCommercePaymentMethodKey(
			commerceOrderId, commercePaymentMethodKey);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder
			updateCommerceShippingMethod(
				long commerceOrderId, long commerceShippingMethodId,
				String commerceShippingOptionName,
				java.math.BigDecimal shippingAmount,
				com.liferay.commerce.context.CommerceContext commerceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.updateCommerceShippingMethod(
			commerceOrderId, commerceShippingMethodId,
			commerceShippingOptionName, shippingAmount, commerceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder
			updateCommerceShippingMethod(
				long commerceOrderId, long commerceShippingMethodId,
				String commerceShippingOptionName,
				com.liferay.commerce.context.CommerceContext commerceContext,
				java.util.Locale locale)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.updateCommerceShippingMethod(
			commerceOrderId, commerceShippingMethodId,
			commerceShippingOptionName, commerceContext, locale);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder updateInfo(
			long commerceOrderId, String printedNote,
			int requestedDeliveryDateMonth, int requestedDeliveryDateDay,
			int requestedDeliveryDateYear, int requestedDeliveryDateHour,
			int requestedDeliveryDateMinute,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.updateInfo(
			commerceOrderId, printedNote, requestedDeliveryDateMonth,
			requestedDeliveryDateDay, requestedDeliveryDateYear,
			requestedDeliveryDateHour, requestedDeliveryDateMinute,
			serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder updateOrderDate(
			long commerceOrderId, int orderDateMonth, int orderDateDay,
			int orderDateYear, int orderDateHour, int orderDateMinute,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.updateOrderDate(
			commerceOrderId, orderDateMonth, orderDateDay, orderDateYear,
			orderDateHour, orderDateMinute, serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder updatePaymentStatus(
			long userId, long commerceOrderId, int paymentStatus)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.updatePaymentStatus(
			userId, commerceOrderId, paymentStatus);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder
			updatePaymentStatusAndTransactionId(
				long userId, long commerceOrderId, int paymentStatus,
				String transactionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.updatePaymentStatusAndTransactionId(
			userId, commerceOrderId, paymentStatus, transactionId);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder updatePrintedNote(
			long commerceOrderId, String printedNote)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.updatePrintedNote(
			commerceOrderId, printedNote);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder updatePurchaseOrderNumber(
			long commerceOrderId, String purchaseOrderNumber)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.updatePurchaseOrderNumber(
			commerceOrderId, purchaseOrderNumber);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder updateShippingAddress(
			long commerceOrderId, long shippingAddressId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.updateShippingAddress(
			commerceOrderId, shippingAddressId);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder updateShippingAddress(
			long commerceOrderId, long countryId, long regionId, String city,
			String description, String name, String phoneNumber, String street1,
			String street2, String street3, String subtype, String zip,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.updateShippingAddress(
			commerceOrderId, countryId, regionId, city, description, name,
			phoneNumber, street1, street2, street3, subtype, zip,
			serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder updateStatus(
			long userId, long commerceOrderId, int status,
			java.util.Map<String, java.io.Serializable> workflowContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.updateStatus(
			userId, commerceOrderId, status, workflowContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrder updateTermsAndConditions(
			long commerceOrderId, long deliveryCommerceTermEntryId,
			long paymentCommerceTermEntryId, String languageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderLocalService.updateTermsAndConditions(
			commerceOrderId, deliveryCommerceTermEntryId,
			paymentCommerceTermEntryId, languageId);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceOrderLocalService.getBasePersistence();
	}

	@Override
	public CommerceOrderLocalService getWrappedService() {
		return _commerceOrderLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceOrderLocalService commerceOrderLocalService) {

		_commerceOrderLocalService = commerceOrderLocalService;
	}

	private CommerceOrderLocalService _commerceOrderLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1395158671
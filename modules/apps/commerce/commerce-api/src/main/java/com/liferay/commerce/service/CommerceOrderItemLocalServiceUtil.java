/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceOrderItem. This utility wraps
 * <code>com.liferay.commerce.service.impl.CommerceOrderItemLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceOrderItemLocalService
 * @generated
 */
public class CommerceOrderItemLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.service.impl.CommerceOrderItemLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
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
	public static CommerceOrderItem addCommerceOrderItem(
		CommerceOrderItem commerceOrderItem) {

		return getService().addCommerceOrderItem(commerceOrderItem);
	}

	public static CommerceOrderItem addCommerceOrderItem(
			long userId, long commerceOrderId, long cpInstanceId, String json,
			java.math.BigDecimal quantity, long replacedCPInstanceId,
			java.math.BigDecimal shippedQuantity, String unitOfMeasureKey,
			com.liferay.commerce.context.CommerceContext commerceContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceOrderItem(
			userId, commerceOrderId, cpInstanceId, json, quantity,
			replacedCPInstanceId, shippedQuantity, unitOfMeasureKey,
			commerceContext, serviceContext);
	}

	public static CommerceOrderItem addOrUpdateCommerceOrderItem(
			long userId, long commerceOrderId, long cpInstanceId, String json,
			java.math.BigDecimal quantity, long replacedCPInstanceId,
			java.math.BigDecimal shippedQuantity, String unitOfMeasureKey,
			com.liferay.commerce.context.CommerceContext commerceContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateCommerceOrderItem(
			userId, commerceOrderId, cpInstanceId, json, quantity,
			replacedCPInstanceId, shippedQuantity, unitOfMeasureKey,
			commerceContext, serviceContext);
	}

	public static int countSubscriptionCommerceOrderItems(
		long commerceOrderId) {

		return getService().countSubscriptionCommerceOrderItems(
			commerceOrderId);
	}

	/**
	 * Creates a new commerce order item with the primary key. Does not add the commerce order item to the database.
	 *
	 * @param commerceOrderItemId the primary key for the new commerce order item
	 * @return the new commerce order item
	 */
	public static CommerceOrderItem createCommerceOrderItem(
		long commerceOrderItemId) {

		return getService().createCommerceOrderItem(commerceOrderItemId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

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
	public static CommerceOrderItem deleteCommerceOrderItem(
		CommerceOrderItem commerceOrderItem) {

		return getService().deleteCommerceOrderItem(commerceOrderItem);
	}

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
	public static CommerceOrderItem deleteCommerceOrderItem(
			long commerceOrderItemId)
		throws PortalException {

		return getService().deleteCommerceOrderItem(commerceOrderItemId);
	}

	public static CommerceOrderItem deleteCommerceOrderItem(
			long userId, CommerceOrderItem commerceOrderItem)
		throws PortalException {

		return getService().deleteCommerceOrderItem(userId, commerceOrderItem);
	}

	public static CommerceOrderItem deleteCommerceOrderItem(
			long userId, CommerceOrderItem commerceOrderItem,
			com.liferay.commerce.context.CommerceContext commerceContext)
		throws PortalException {

		return getService().deleteCommerceOrderItem(
			userId, commerceOrderItem, commerceContext);
	}

	public static CommerceOrderItem deleteCommerceOrderItem(
			long userId, long commerceOrderItemId)
		throws PortalException {

		return getService().deleteCommerceOrderItem(
			userId, commerceOrderItemId);
	}

	public static void deleteCommerceOrderItems(
			long userId, long commerceOrderId)
		throws PortalException {

		getService().deleteCommerceOrderItems(userId, commerceOrderId);
	}

	public static void deleteCommerceOrderItemsByCPInstanceId(
			long userId, long cpInstanceId)
		throws PortalException {

		getService().deleteCommerceOrderItemsByCPInstanceId(
			userId, cpInstanceId);
	}

	public static void deleteMissingCommerceOrderItems(
			long userId, long commerceOrderId, Long[] commerceOrderItemIds,
			String[] externalReferenceCodes)
		throws PortalException {

		getService().deleteMissingCommerceOrderItems(
			userId, commerceOrderId, commerceOrderItemIds,
			externalReferenceCodes);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderItemModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderItemModelImpl</code>.
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

	public static CommerceOrderItem fetchCommerceOrderItem(
		long commerceOrderItemId) {

		return getService().fetchCommerceOrderItem(commerceOrderItemId);
	}

	public static CommerceOrderItem
		fetchCommerceOrderItemByCommerceInventoryBookedQuantityId(
			long commerceInventoryBookedQuantityId) {

		return getService().
			fetchCommerceOrderItemByCommerceInventoryBookedQuantityId(
				commerceInventoryBookedQuantityId);
	}

	public static CommerceOrderItem
		fetchCommerceOrderItemByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return getService().fetchCommerceOrderItemByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce order item matching the UUID and group.
	 *
	 * @param uuid the commerce order item's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem fetchCommerceOrderItemByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchCommerceOrderItemByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<CommerceOrderItem>
		getAvailableForShipmentCommerceOrderItems(long commerceOrderId) {

		return getService().getAvailableForShipmentCommerceOrderItems(
			commerceOrderId);
	}

	public static List<CommerceOrderItem> getChildCommerceOrderItems(
		long parentCommerceOrderItemId) {

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

	/**
	 * Returns the commerce order item with the primary key.
	 *
	 * @param commerceOrderItemId the primary key of the commerce order item
	 * @return the commerce order item
	 * @throws PortalException if a commerce order item with the primary key could not be found
	 */
	public static CommerceOrderItem getCommerceOrderItem(
			long commerceOrderItemId)
		throws PortalException {

		return getService().getCommerceOrderItem(commerceOrderItemId);
	}

	public static CommerceOrderItem getCommerceOrderItemByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCommerceOrderItemByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce order item matching the UUID and group.
	 *
	 * @param uuid the commerce order item's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce order item
	 * @throws PortalException if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem getCommerceOrderItemByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getCommerceOrderItemByUuidAndGroupId(uuid, groupId);
	}

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
	public static List<CommerceOrderItem> getCommerceOrderItems(
		int start, int end) {

		return getService().getCommerceOrderItems(start, end);
	}

	public static List<CommerceOrderItem> getCommerceOrderItems(
		long commerceOrderId, int start, int end) {

		return getService().getCommerceOrderItems(commerceOrderId, start, end);
	}

	public static List<CommerceOrderItem> getCommerceOrderItems(
		long commerceOrderId, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getService().getCommerceOrderItems(
			commerceOrderId, start, end, orderByComparator);
	}

	public static List<CommerceOrderItem> getCommerceOrderItems(
		long cpInstanceId, int[] orderStatuses, String unitOfMeasureKey,
		int start, int end) {

		return getService().getCommerceOrderItems(
			cpInstanceId, orderStatuses, unitOfMeasureKey, start, end);
	}

	public static List<CommerceOrderItem> getCommerceOrderItems(
		long commerceOrderId, long cpInstanceId, int start, int end) {

		return getService().getCommerceOrderItems(
			commerceOrderId, cpInstanceId, start, end);
	}

	public static List<CommerceOrderItem> getCommerceOrderItems(
		long commerceOrderId, long cpInstanceId, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getService().getCommerceOrderItems(
			commerceOrderId, cpInstanceId, start, end, orderByComparator);
	}

	public static List<CommerceOrderItem> getCommerceOrderItems(
		long groupId, long commerceAccountId, int[] orderStatuses, int start,
		int end) {

		return getService().getCommerceOrderItems(
			groupId, commerceAccountId, orderStatuses, start, end);
	}

	/**
	 * Returns all the commerce order items matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce order items
	 * @param companyId the primary key of the company
	 * @return the matching commerce order items, or an empty list if no matches were found
	 */
	public static List<CommerceOrderItem>
		getCommerceOrderItemsByUuidAndCompanyId(String uuid, long companyId) {

		return getService().getCommerceOrderItemsByUuidAndCompanyId(
			uuid, companyId);
	}

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
	public static List<CommerceOrderItem>
		getCommerceOrderItemsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getService().getCommerceOrderItemsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce order items.
	 *
	 * @return the number of commerce order items
	 */
	public static int getCommerceOrderItemsCount() {
		return getService().getCommerceOrderItemsCount();
	}

	public static int getCommerceOrderItemsCount(long commerceOrderId) {
		return getService().getCommerceOrderItemsCount(commerceOrderId);
	}

	public static int getCommerceOrderItemsCount(
		long commerceOrderId, long cpInstanceId) {

		return getService().getCommerceOrderItemsCount(
			commerceOrderId, cpInstanceId);
	}

	public static int getCommerceOrderItemsCount(
		long groupId, long commerceAccountId, int[] orderStatuses) {

		return getService().getCommerceOrderItemsCount(
			groupId, commerceAccountId, orderStatuses);
	}

	public static java.math.BigDecimal getCommerceOrderItemsQuantity(
		long commerceOrderId) {

		return getService().getCommerceOrderItemsQuantity(commerceOrderId);
	}

	public static List<Long> getCustomerCommerceOrderIds(long commerceOrderId) {
		return getService().getCustomerCommerceOrderIds(commerceOrderId);
	}

	public static int getCustomerCommerceOrderIdsCount(long commerceOrderId) {
		return getService().getCustomerCommerceOrderIdsCount(commerceOrderId);
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

	public static List<CommerceOrderItem> getParentCommerceOrderItems(
		long commerceOrderId, long parentCommerceOrderItemId, int start,
		int end, OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getService().getParentCommerceOrderItems(
			commerceOrderId, parentCommerceOrderItemId, start, end,
			orderByComparator);
	}

	public static int getParentCommerceOrderItemsCount(
		long commerceOrderId, long parentCommerceOrderItemId) {

		return getService().getParentCommerceOrderItemsCount(
			commerceOrderId, parentCommerceOrderItemId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static List<CommerceOrderItem> getSubscriptionCommerceOrderItems(
		long commerceOrderId) {

		return getService().getSubscriptionCommerceOrderItems(commerceOrderId);
	}

	public static List<Long> getSupplierCommerceOrderIds(long commerceOrderId) {
		return getService().getSupplierCommerceOrderIds(commerceOrderId);
	}

	public static int getSupplierCommerceOrderIdsCount(long commerceOrderId) {
		return getService().getSupplierCommerceOrderIdsCount(commerceOrderId);
	}

	public static List<CommerceOrderItem> getSupplierCommerceOrderItems(
		long customerCommerceOrderItemId, int start, int end) {

		return getService().getSupplierCommerceOrderItems(
			customerCommerceOrderItemId, start, end);
	}

	public static CommerceOrderItem importCommerceOrderItem(
			long userId, String externalReferenceCode, long commerceOrderItemId,
			long commerceOrderId, long cpInstanceId,
			String cpMeasurementUnitKey, String json,
			java.math.BigDecimal quantity, java.math.BigDecimal shippedQuantity,
			java.math.BigDecimal unitOfMeasureIncrementalOrderQuantity,
			String unitOfMeasureKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().importCommerceOrderItem(
			userId, externalReferenceCode, commerceOrderItemId, commerceOrderId,
			cpInstanceId, cpMeasurementUnitKey, json, quantity, shippedQuantity,
			unitOfMeasureIncrementalOrderQuantity, unitOfMeasureKey,
			serviceContext);
	}

	public static CommerceOrderItem incrementShippedQuantity(
			long commerceOrderItemId, java.math.BigDecimal shippedQuantity)
		throws PortalException {

		return getService().incrementShippedQuantity(
			commerceOrderItemId, shippedQuantity);
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
	public static CommerceOrderItem updateCommerceOrderItem(
		CommerceOrderItem commerceOrderItem) {

		return getService().updateCommerceOrderItem(commerceOrderItem);
	}

	public static CommerceOrderItem updateCommerceOrderItem(
			long commerceOrderItemId, long commerceInventoryBookedQuantityId)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getService().updateCommerceOrderItem(
			commerceOrderItemId, commerceInventoryBookedQuantityId);
	}

	public static CommerceOrderItem updateCommerceOrderItem(
			long userId, long commerceOrderItemId,
			java.math.BigDecimal quantity,
			com.liferay.commerce.context.CommerceContext commerceContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommerceOrderItem(
			userId, commerceOrderItemId, quantity, commerceContext,
			serviceContext);
	}

	public static CommerceOrderItem updateCommerceOrderItem(
			long userId, long commerceOrderItemId, long cpMeasurementUnitId,
			java.math.BigDecimal quantity,
			com.liferay.commerce.context.CommerceContext commerceContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommerceOrderItem(
			userId, commerceOrderItemId, cpMeasurementUnitId, quantity,
			commerceContext, serviceContext);
	}

	public static CommerceOrderItem updateCommerceOrderItem(
			long userId, long commerceOrderItemId, long cpMeasurementUnitId,
			java.math.BigDecimal quantity,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommerceOrderItem(
			userId, commerceOrderItemId, cpMeasurementUnitId, quantity,
			serviceContext);
	}

	public static CommerceOrderItem updateCommerceOrderItem(
			long userId, long commerceOrderItemId, String json,
			java.math.BigDecimal quantity,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommerceOrderItem(
			userId, commerceOrderItemId, json, quantity, serviceContext);
	}

	public static CommerceOrderItem updateCommerceOrderItem(
			String externalReferenceCode, long userId, long commerceOrderItemId,
			String json, java.math.BigDecimal quantity,
			com.liferay.commerce.context.CommerceContext commerceContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommerceOrderItem(
			externalReferenceCode, userId, commerceOrderItemId, json, quantity,
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

	public static CommerceOrderItem updateCommerceOrderItemPrice(
			long commerceOrderItemId,
			com.liferay.commerce.context.CommerceContext commerceContext)
		throws PortalException {

		return getService().updateCommerceOrderItemPrice(
			commerceOrderItemId, commerceContext);
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
			long userId, long commerceOrderItemId,
			java.math.BigDecimal quantity, java.math.BigDecimal unitPrice)
		throws PortalException {

		return getService().updateCommerceOrderItemUnitPrice(
			userId, commerceOrderItemId, quantity, unitPrice);
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

	public static CommerceOrderItemLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceOrderItemLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceOrderItemLocalServiceUtil.class,
			CommerceOrderItemLocalService.class);

}
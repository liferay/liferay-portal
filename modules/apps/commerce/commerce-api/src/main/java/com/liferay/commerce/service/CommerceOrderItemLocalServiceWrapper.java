/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceOrderItemLocalService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceOrderItemLocalService
 * @generated
 */
public class CommerceOrderItemLocalServiceWrapper
	implements CommerceOrderItemLocalService,
			   ServiceWrapper<CommerceOrderItemLocalService> {

	public CommerceOrderItemLocalServiceWrapper() {
		this(null);
	}

	public CommerceOrderItemLocalServiceWrapper(
		CommerceOrderItemLocalService commerceOrderItemLocalService) {

		_commerceOrderItemLocalService = commerceOrderItemLocalService;
	}

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
	@Override
	public com.liferay.commerce.model.CommerceOrderItem addCommerceOrderItem(
		com.liferay.commerce.model.CommerceOrderItem commerceOrderItem) {

		return _commerceOrderItemLocalService.addCommerceOrderItem(
			commerceOrderItem);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem addCommerceOrderItem(
			long userId, long commerceOrderId, long cpInstanceId, String json,
			java.math.BigDecimal quantity, long replacedCPInstanceId,
			java.math.BigDecimal shippedQuantity, String unitOfMeasureKey,
			com.liferay.commerce.context.CommerceContext commerceContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemLocalService.addCommerceOrderItem(
			userId, commerceOrderId, cpInstanceId, json, quantity,
			replacedCPInstanceId, shippedQuantity, unitOfMeasureKey,
			commerceContext, serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem
			addOrUpdateCommerceOrderItem(
				long userId, long commerceOrderId, long cpInstanceId,
				String json, java.math.BigDecimal quantity,
				long replacedCPInstanceId, java.math.BigDecimal shippedQuantity,
				String unitOfMeasureKey,
				com.liferay.commerce.context.CommerceContext commerceContext,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemLocalService.addOrUpdateCommerceOrderItem(
			userId, commerceOrderId, cpInstanceId, json, quantity,
			replacedCPInstanceId, shippedQuantity, unitOfMeasureKey,
			commerceContext, serviceContext);
	}

	@Override
	public int countSubscriptionCommerceOrderItems(long commerceOrderId) {
		return _commerceOrderItemLocalService.
			countSubscriptionCommerceOrderItems(commerceOrderId);
	}

	/**
	 * Creates a new commerce order item with the primary key. Does not add the commerce order item to the database.
	 *
	 * @param commerceOrderItemId the primary key for the new commerce order item
	 * @return the new commerce order item
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrderItem createCommerceOrderItem(
		long commerceOrderItemId) {

		return _commerceOrderItemLocalService.createCommerceOrderItem(
			commerceOrderItemId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemLocalService.createPersistedModel(
			primaryKeyObj);
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
	@Override
	public com.liferay.commerce.model.CommerceOrderItem deleteCommerceOrderItem(
		com.liferay.commerce.model.CommerceOrderItem commerceOrderItem) {

		return _commerceOrderItemLocalService.deleteCommerceOrderItem(
			commerceOrderItem);
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
	@Override
	public com.liferay.commerce.model.CommerceOrderItem deleteCommerceOrderItem(
			long commerceOrderItemId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemLocalService.deleteCommerceOrderItem(
			commerceOrderItemId);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem deleteCommerceOrderItem(
			long userId,
			com.liferay.commerce.model.CommerceOrderItem commerceOrderItem)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemLocalService.deleteCommerceOrderItem(
			userId, commerceOrderItem);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem deleteCommerceOrderItem(
			long userId,
			com.liferay.commerce.model.CommerceOrderItem commerceOrderItem,
			com.liferay.commerce.context.CommerceContext commerceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemLocalService.deleteCommerceOrderItem(
			userId, commerceOrderItem, commerceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem deleteCommerceOrderItem(
			long userId, long commerceOrderItemId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemLocalService.deleteCommerceOrderItem(
			userId, commerceOrderItemId);
	}

	@Override
	public void deleteCommerceOrderItems(long userId, long commerceOrderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceOrderItemLocalService.deleteCommerceOrderItems(
			userId, commerceOrderId);
	}

	@Override
	public void deleteCommerceOrderItemsByCPInstanceId(
			long userId, long cpInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceOrderItemLocalService.deleteCommerceOrderItemsByCPInstanceId(
			userId, cpInstanceId);
	}

	@Override
	public void deleteMissingCommerceOrderItems(
			long userId, long commerceOrderId, Long[] commerceOrderItemIds,
			String[] externalReferenceCodes)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceOrderItemLocalService.deleteMissingCommerceOrderItems(
			userId, commerceOrderId, commerceOrderItemIds,
			externalReferenceCodes);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceOrderItemLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceOrderItemLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceOrderItemLocalService.dynamicQuery();
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

		return _commerceOrderItemLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _commerceOrderItemLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _commerceOrderItemLocalService.dynamicQuery(
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

		return _commerceOrderItemLocalService.dynamicQueryCount(dynamicQuery);
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

		return _commerceOrderItemLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem fetchCommerceOrderItem(
		long commerceOrderItemId) {

		return _commerceOrderItemLocalService.fetchCommerceOrderItem(
			commerceOrderItemId);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem
		fetchCommerceOrderItemByCommerceInventoryBookedQuantityId(
			long commerceInventoryBookedQuantityId) {

		return _commerceOrderItemLocalService.
			fetchCommerceOrderItemByCommerceInventoryBookedQuantityId(
				commerceInventoryBookedQuantityId);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem
		fetchCommerceOrderItemByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _commerceOrderItemLocalService.
			fetchCommerceOrderItemByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce order item matching the UUID and group.
	 *
	 * @param uuid the commerce order item's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrderItem
		fetchCommerceOrderItemByUuidAndGroupId(String uuid, long groupId) {

		return _commerceOrderItemLocalService.
			fetchCommerceOrderItemByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceOrderItemLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderItem>
		getAvailableForShipmentCommerceOrderItems(long commerceOrderId) {

		return _commerceOrderItemLocalService.
			getAvailableForShipmentCommerceOrderItems(commerceOrderId);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderItem>
		getChildCommerceOrderItems(long parentCommerceOrderItemId) {

		return _commerceOrderItemLocalService.getChildCommerceOrderItems(
			parentCommerceOrderItemId);
	}

	@Override
	public java.math.BigDecimal getCommerceInventoryWarehouseItemQuantity(
			long commerceOrderItemId, long commerceInventoryWarehouseId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemLocalService.
			getCommerceInventoryWarehouseItemQuantity(
				commerceOrderItemId, commerceInventoryWarehouseId);
	}

	/**
	 * Returns the commerce order item with the primary key.
	 *
	 * @param commerceOrderItemId the primary key of the commerce order item
	 * @return the commerce order item
	 * @throws PortalException if a commerce order item with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrderItem getCommerceOrderItem(
			long commerceOrderItemId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemLocalService.getCommerceOrderItem(
			commerceOrderItemId);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem
			getCommerceOrderItemByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemLocalService.
			getCommerceOrderItemByExternalReferenceCode(
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
	@Override
	public com.liferay.commerce.model.CommerceOrderItem
			getCommerceOrderItemByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemLocalService.
			getCommerceOrderItemByUuidAndGroupId(uuid, groupId);
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
	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderItem>
		getCommerceOrderItems(int start, int end) {

		return _commerceOrderItemLocalService.getCommerceOrderItems(start, end);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderItem>
		getCommerceOrderItems(long commerceOrderId, int start, int end) {

		return _commerceOrderItemLocalService.getCommerceOrderItems(
			commerceOrderId, start, end);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderItem>
		getCommerceOrderItems(
			long commerceOrderId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.commerce.model.CommerceOrderItem>
					orderByComparator) {

		return _commerceOrderItemLocalService.getCommerceOrderItems(
			commerceOrderId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderItem>
		getCommerceOrderItems(
			long cpInstanceId, int[] orderStatuses, String unitOfMeasureKey,
			int start, int end) {

		return _commerceOrderItemLocalService.getCommerceOrderItems(
			cpInstanceId, orderStatuses, unitOfMeasureKey, start, end);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderItem>
		getCommerceOrderItems(
			long commerceOrderId, long cpInstanceId, int start, int end) {

		return _commerceOrderItemLocalService.getCommerceOrderItems(
			commerceOrderId, cpInstanceId, start, end);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderItem>
		getCommerceOrderItems(
			long commerceOrderId, long cpInstanceId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.commerce.model.CommerceOrderItem>
					orderByComparator) {

		return _commerceOrderItemLocalService.getCommerceOrderItems(
			commerceOrderId, cpInstanceId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderItem>
		getCommerceOrderItems(
			long groupId, long commerceAccountId, int[] orderStatuses,
			int start, int end) {

		return _commerceOrderItemLocalService.getCommerceOrderItems(
			groupId, commerceAccountId, orderStatuses, start, end);
	}

	/**
	 * Returns all the commerce order items matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce order items
	 * @param companyId the primary key of the company
	 * @return the matching commerce order items, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderItem>
		getCommerceOrderItemsByUuidAndCompanyId(String uuid, long companyId) {

		return _commerceOrderItemLocalService.
			getCommerceOrderItemsByUuidAndCompanyId(uuid, companyId);
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
	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderItem>
		getCommerceOrderItemsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.commerce.model.CommerceOrderItem>
					orderByComparator) {

		return _commerceOrderItemLocalService.
			getCommerceOrderItemsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce order items.
	 *
	 * @return the number of commerce order items
	 */
	@Override
	public int getCommerceOrderItemsCount() {
		return _commerceOrderItemLocalService.getCommerceOrderItemsCount();
	}

	@Override
	public int getCommerceOrderItemsCount(long commerceOrderId) {
		return _commerceOrderItemLocalService.getCommerceOrderItemsCount(
			commerceOrderId);
	}

	@Override
	public int getCommerceOrderItemsCount(
		long commerceOrderId, long cpInstanceId) {

		return _commerceOrderItemLocalService.getCommerceOrderItemsCount(
			commerceOrderId, cpInstanceId);
	}

	@Override
	public int getCommerceOrderItemsCount(
		long groupId, long commerceAccountId, int[] orderStatuses) {

		return _commerceOrderItemLocalService.getCommerceOrderItemsCount(
			groupId, commerceAccountId, orderStatuses);
	}

	@Override
	public java.math.BigDecimal getCommerceOrderItemsQuantity(
		long commerceOrderId) {

		return _commerceOrderItemLocalService.getCommerceOrderItemsQuantity(
			commerceOrderId);
	}

	@Override
	public java.util.List<Long> getCustomerCommerceOrderIds(
		long commerceOrderId) {

		return _commerceOrderItemLocalService.getCustomerCommerceOrderIds(
			commerceOrderId);
	}

	@Override
	public int getCustomerCommerceOrderIdsCount(long commerceOrderId) {
		return _commerceOrderItemLocalService.getCustomerCommerceOrderIdsCount(
			commerceOrderId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commerceOrderItemLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceOrderItemLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceOrderItemLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderItem>
		getParentCommerceOrderItems(
			long commerceOrderId, long parentCommerceOrderItemId, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.commerce.model.CommerceOrderItem>
					orderByComparator) {

		return _commerceOrderItemLocalService.getParentCommerceOrderItems(
			commerceOrderId, parentCommerceOrderItemId, start, end,
			orderByComparator);
	}

	@Override
	public int getParentCommerceOrderItemsCount(
		long commerceOrderId, long parentCommerceOrderItemId) {

		return _commerceOrderItemLocalService.getParentCommerceOrderItemsCount(
			commerceOrderId, parentCommerceOrderItemId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderItem>
		getSubscriptionCommerceOrderItems(long commerceOrderId) {

		return _commerceOrderItemLocalService.getSubscriptionCommerceOrderItems(
			commerceOrderId);
	}

	@Override
	public java.util.List<Long> getSupplierCommerceOrderIds(
		long commerceOrderId) {

		return _commerceOrderItemLocalService.getSupplierCommerceOrderIds(
			commerceOrderId);
	}

	@Override
	public int getSupplierCommerceOrderIdsCount(long commerceOrderId) {
		return _commerceOrderItemLocalService.getSupplierCommerceOrderIdsCount(
			commerceOrderId);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderItem>
		getSupplierCommerceOrderItems(
			long customerCommerceOrderItemId, int start, int end) {

		return _commerceOrderItemLocalService.getSupplierCommerceOrderItems(
			customerCommerceOrderItemId, start, end);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem importCommerceOrderItem(
			long userId, String externalReferenceCode, long commerceOrderItemId,
			long commerceOrderId, long cpInstanceId,
			String cpMeasurementUnitKey, String json,
			java.math.BigDecimal quantity, java.math.BigDecimal shippedQuantity,
			java.math.BigDecimal unitOfMeasureIncrementalOrderQuantity,
			String unitOfMeasureKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemLocalService.importCommerceOrderItem(
			userId, externalReferenceCode, commerceOrderItemId, commerceOrderId,
			cpInstanceId, cpMeasurementUnitKey, json, quantity, shippedQuantity,
			unitOfMeasureIncrementalOrderQuantity, unitOfMeasureKey,
			serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem
			incrementShippedQuantity(
				long commerceOrderItemId, java.math.BigDecimal shippedQuantity)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemLocalService.incrementShippedQuantity(
			commerceOrderItemId, shippedQuantity);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.commerce.model.CommerceOrderItem> searchCommerceOrderItems(
				long commerceOrderId, long parentCommerceOrderItemId,
				String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemLocalService.searchCommerceOrderItems(
			commerceOrderId, parentCommerceOrderItemId, keywords, start, end,
			sort);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.commerce.model.CommerceOrderItem> searchCommerceOrderItems(
				long commerceOrderId, String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemLocalService.searchCommerceOrderItems(
			commerceOrderId, keywords, start, end, sort);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.commerce.model.CommerceOrderItem> searchCommerceOrderItems(
				long commerceOrderId, String name, String sku,
				boolean andOperator, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemLocalService.searchCommerceOrderItems(
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
	@Override
	public com.liferay.commerce.model.CommerceOrderItem updateCommerceOrderItem(
		com.liferay.commerce.model.CommerceOrderItem commerceOrderItem) {

		return _commerceOrderItemLocalService.updateCommerceOrderItem(
			commerceOrderItem);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem updateCommerceOrderItem(
			long commerceOrderItemId, long commerceInventoryBookedQuantityId)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return _commerceOrderItemLocalService.updateCommerceOrderItem(
			commerceOrderItemId, commerceInventoryBookedQuantityId);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem updateCommerceOrderItem(
			long userId, long commerceOrderItemId,
			java.math.BigDecimal quantity,
			com.liferay.commerce.context.CommerceContext commerceContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemLocalService.updateCommerceOrderItem(
			userId, commerceOrderItemId, quantity, commerceContext,
			serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem updateCommerceOrderItem(
			long userId, long commerceOrderItemId, long cpMeasurementUnitId,
			java.math.BigDecimal quantity,
			com.liferay.commerce.context.CommerceContext commerceContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemLocalService.updateCommerceOrderItem(
			userId, commerceOrderItemId, cpMeasurementUnitId, quantity,
			commerceContext, serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem updateCommerceOrderItem(
			long userId, long commerceOrderItemId, long cpMeasurementUnitId,
			java.math.BigDecimal quantity,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemLocalService.updateCommerceOrderItem(
			userId, commerceOrderItemId, cpMeasurementUnitId, quantity,
			serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem updateCommerceOrderItem(
			long userId, long commerceOrderItemId, String json,
			java.math.BigDecimal quantity,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemLocalService.updateCommerceOrderItem(
			userId, commerceOrderItemId, json, quantity, serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem updateCommerceOrderItem(
			String externalReferenceCode, long userId, long commerceOrderItemId,
			String json, java.math.BigDecimal quantity,
			com.liferay.commerce.context.CommerceContext commerceContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemLocalService.updateCommerceOrderItem(
			externalReferenceCode, userId, commerceOrderItemId, json, quantity,
			commerceContext, serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem
			updateCommerceOrderItemDeliveryDate(
				long commerceOrderItemId, java.util.Date requestedDeliveryDate)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemLocalService.
			updateCommerceOrderItemDeliveryDate(
				commerceOrderItemId, requestedDeliveryDate);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem
			updateCommerceOrderItemInfo(
				long commerceOrderItemId, long shippingAddressId,
				String deliveryGroupName, String printedNote)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemLocalService.updateCommerceOrderItemInfo(
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

		return _commerceOrderItemLocalService.updateCommerceOrderItemInfo(
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

		return _commerceOrderItemLocalService.updateCommerceOrderItemInfo(
			commerceOrderItemId, deliveryGroupName, shippingAddressId,
			printedNote, requestedDeliveryDateMonth, requestedDeliveryDateDay,
			requestedDeliveryDateYear, requestedDeliveryDateHour,
			requestedDeliveryDateMinute, serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem
			updateCommerceOrderItemPrice(
				long commerceOrderItemId,
				com.liferay.commerce.context.CommerceContext commerceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemLocalService.updateCommerceOrderItemPrice(
			commerceOrderItemId, commerceContext);
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

		return _commerceOrderItemLocalService.updateCommerceOrderItemPrices(
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

		return _commerceOrderItemLocalService.updateCommerceOrderItemPrices(
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

		return _commerceOrderItemLocalService.updateCommerceOrderItemUnitPrice(
			commerceOrderItemId, unitPrice);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem
			updateCommerceOrderItemUnitPrice(
				long userId, long commerceOrderItemId,
				java.math.BigDecimal quantity, java.math.BigDecimal unitPrice)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemLocalService.updateCommerceOrderItemUnitPrice(
			userId, commerceOrderItemId, quantity, unitPrice);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem updateCustomFields(
			long commerceOrderItemId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemLocalService.updateCustomFields(
			commerceOrderItemId, serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderItem
			updateExternalReferenceCode(
				long commerceOrderItemId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderItemLocalService.updateExternalReferenceCode(
			commerceOrderItemId, externalReferenceCode);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceOrderItemLocalService.getBasePersistence();
	}

	@Override
	public CommerceOrderItemLocalService getWrappedService() {
		return _commerceOrderItemLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceOrderItemLocalService commerceOrderItemLocalService) {

		_commerceOrderItemLocalService = commerceOrderItemLocalService;
	}

	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

}
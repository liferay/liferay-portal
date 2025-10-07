/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.commerce.model.CommerceShipmentItem;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceShipmentItem. This utility wraps
 * <code>com.liferay.commerce.service.impl.CommerceShipmentItemLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceShipmentItemLocalService
 * @generated
 */
public class CommerceShipmentItemLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.service.impl.CommerceShipmentItemLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce shipment item to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShipmentItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShipmentItem the commerce shipment item
	 * @return the commerce shipment item that was added
	 */
	public static CommerceShipmentItem addCommerceShipmentItem(
		CommerceShipmentItem commerceShipmentItem) {

		return getService().addCommerceShipmentItem(commerceShipmentItem);
	}

	public static CommerceShipmentItem addCommerceShipmentItem(
			String externalReferenceCode, long commerceShipmentId,
			long commerceOrderItemId, long commerceInventoryWarehouseId,
			java.math.BigDecimal quantity, String unitOfMeasureKey,
			boolean validateInventory,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceShipmentItem(
			externalReferenceCode, commerceShipmentId, commerceOrderItemId,
			commerceInventoryWarehouseId, quantity, unitOfMeasureKey,
			validateInventory, serviceContext);
	}

	public static CommerceShipmentItem
			addDeliverySubscriptionCommerceShipmentItem(
				long groupId, long userId, long commerceShipmentId,
				long commerceOrderItemId)
		throws PortalException {

		return getService().addDeliverySubscriptionCommerceShipmentItem(
			groupId, userId, commerceShipmentId, commerceOrderItemId);
	}

	public static CommerceShipmentItem addOrUpdateCommerceShipmentItem(
			String externalReferenceCode, long commerceShipmentId,
			long commerceOrderItemId, long commerceInventoryWarehouseId,
			java.math.BigDecimal quantity, String unitOfMeasureKey,
			boolean validateInventory,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateCommerceShipmentItem(
			externalReferenceCode, commerceShipmentId, commerceOrderItemId,
			commerceInventoryWarehouseId, quantity, unitOfMeasureKey,
			validateInventory, serviceContext);
	}

	/**
	 * Creates a new commerce shipment item with the primary key. Does not add the commerce shipment item to the database.
	 *
	 * @param commerceShipmentItemId the primary key for the new commerce shipment item
	 * @return the new commerce shipment item
	 */
	public static CommerceShipmentItem createCommerceShipmentItem(
		long commerceShipmentItemId) {

		return getService().createCommerceShipmentItem(commerceShipmentItemId);
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
	 * Deletes the commerce shipment item from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShipmentItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShipmentItem the commerce shipment item
	 * @return the commerce shipment item that was removed
	 */
	public static CommerceShipmentItem deleteCommerceShipmentItem(
		CommerceShipmentItem commerceShipmentItem) {

		return getService().deleteCommerceShipmentItem(commerceShipmentItem);
	}

	public static CommerceShipmentItem deleteCommerceShipmentItem(
			CommerceShipmentItem commerceShipmentItem,
			boolean restoreStockQuantity)
		throws PortalException {

		return getService().deleteCommerceShipmentItem(
			commerceShipmentItem, restoreStockQuantity);
	}

	/**
	 * Deletes the commerce shipment item with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShipmentItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShipmentItemId the primary key of the commerce shipment item
	 * @return the commerce shipment item that was removed
	 * @throws PortalException if a commerce shipment item with the primary key could not be found
	 */
	public static CommerceShipmentItem deleteCommerceShipmentItem(
			long commerceShipmentItemId)
		throws PortalException {

		return getService().deleteCommerceShipmentItem(commerceShipmentItemId);
	}

	public static void deleteCommerceShipmentItem(
			long commerceShipmentItemId, boolean restoreStockQuantity)
		throws PortalException {

		getService().deleteCommerceShipmentItem(
			commerceShipmentItemId, restoreStockQuantity);
	}

	public static void deleteCommerceShipmentItems(
			long commerceShipmentId, boolean restoreStockQuantity)
		throws PortalException {

		getService().deleteCommerceShipmentItems(
			commerceShipmentId, restoreStockQuantity);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShipmentItemModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShipmentItemModelImpl</code>.
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

	public static CommerceShipmentItem fetchCommerceShipmentItem(
		long commerceShipmentItemId) {

		return getService().fetchCommerceShipmentItem(commerceShipmentItemId);
	}

	public static CommerceShipmentItem fetchCommerceShipmentItem(
		long commerceShipmentId, long commerceOrderItemId,
		long commerceInventoryWarehouseId) {

		return getService().fetchCommerceShipmentItem(
			commerceShipmentId, commerceOrderItemId,
			commerceInventoryWarehouseId);
	}

	public static CommerceShipmentItem
		fetchCommerceShipmentItemByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return getService().fetchCommerceShipmentItemByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce shipment item matching the UUID and group.
	 *
	 * @param uuid the commerce shipment item's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem
		fetchCommerceShipmentItemByUuidAndGroupId(String uuid, long groupId) {

		return getService().fetchCommerceShipmentItemByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce shipment item with the primary key.
	 *
	 * @param commerceShipmentItemId the primary key of the commerce shipment item
	 * @return the commerce shipment item
	 * @throws PortalException if a commerce shipment item with the primary key could not be found
	 */
	public static CommerceShipmentItem getCommerceShipmentItem(
			long commerceShipmentItemId)
		throws PortalException {

		return getService().getCommerceShipmentItem(commerceShipmentItemId);
	}

	public static CommerceShipmentItem
			getCommerceShipmentItemByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCommerceShipmentItemByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce shipment item matching the UUID and group.
	 *
	 * @param uuid the commerce shipment item's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce shipment item
	 * @throws PortalException if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem getCommerceShipmentItemByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getCommerceShipmentItemByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the commerce shipment items.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @return the range of commerce shipment items
	 */
	public static List<CommerceShipmentItem> getCommerceShipmentItems(
		int start, int end) {

		return getService().getCommerceShipmentItems(start, end);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public static List<CommerceShipmentItem> getCommerceShipmentItems(
		long commerceOrderItemId) {

		return getService().getCommerceShipmentItems(commerceOrderItemId);
	}

	public static List<CommerceShipmentItem> getCommerceShipmentItems(
		long commerceShipmentId, int start, int end,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return getService().getCommerceShipmentItems(
			commerceShipmentId, start, end, orderByComparator);
	}

	public static List<CommerceShipmentItem> getCommerceShipmentItems(
		long commerceShipmentId, long commerceOrderItemId, int start, int end,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return getService().getCommerceShipmentItems(
			commerceShipmentId, commerceOrderItemId, start, end,
			orderByComparator);
	}

	public static List<CommerceShipmentItem>
		getCommerceShipmentItemsByCommerceOrderItemId(
			long commerceOrderItemId) {

		return getService().getCommerceShipmentItemsByCommerceOrderItemId(
			commerceOrderItemId);
	}

	/**
	 * Returns all the commerce shipment items matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce shipment items
	 * @param companyId the primary key of the company
	 * @return the matching commerce shipment items, or an empty list if no matches were found
	 */
	public static List<CommerceShipmentItem>
		getCommerceShipmentItemsByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().getCommerceShipmentItemsByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of commerce shipment items matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce shipment items
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching commerce shipment items, or an empty list if no matches were found
	 */
	public static List<CommerceShipmentItem>
		getCommerceShipmentItemsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return getService().getCommerceShipmentItemsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce shipment items.
	 *
	 * @return the number of commerce shipment items
	 */
	public static int getCommerceShipmentItemsCount() {
		return getService().getCommerceShipmentItemsCount();
	}

	public static int getCommerceShipmentItemsCount(long commerceShipmentId) {
		return getService().getCommerceShipmentItemsCount(commerceShipmentId);
	}

	public static int getCommerceShipmentItemsCountByCommerceOrderItemId(
		long commerceOrderItemId) {

		return getService().getCommerceShipmentItemsCountByCommerceOrderItemId(
			commerceOrderItemId);
	}

	public static java.math.BigDecimal getCommerceShipmentOrderItemsQuantity(
		long commerceShipmentId, long commerceOrderItemId) {

		return getService().getCommerceShipmentOrderItemsQuantity(
			commerceShipmentId, commerceOrderItemId);
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

	public static int getValidCommerceShipmentItemsCount(
		long commerceShipmentId) {

		return getService().getValidCommerceShipmentItemsCount(
			commerceShipmentId);
	}

	/**
	 * Updates the commerce shipment item in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShipmentItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShipmentItem the commerce shipment item
	 * @return the commerce shipment item that was updated
	 */
	public static CommerceShipmentItem updateCommerceShipmentItem(
		CommerceShipmentItem commerceShipmentItem) {

		return getService().updateCommerceShipmentItem(commerceShipmentItem);
	}

	public static CommerceShipmentItem updateCommerceShipmentItem(
			long commerceShipmentItemId, long commerceInventoryWarehouseId,
			java.math.BigDecimal quantity, boolean validateInventory)
		throws PortalException {

		return getService().updateCommerceShipmentItem(
			commerceShipmentItemId, commerceInventoryWarehouseId, quantity,
			validateInventory);
	}

	public static CommerceShipmentItem updateExternalReferenceCode(
			long commerceShipmentItemId, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			commerceShipmentItemId, externalReferenceCode);
	}

	public static CommerceShipmentItemLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceShipmentItemLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceShipmentItemLocalServiceUtil.class,
			CommerceShipmentItemLocalService.class);

}
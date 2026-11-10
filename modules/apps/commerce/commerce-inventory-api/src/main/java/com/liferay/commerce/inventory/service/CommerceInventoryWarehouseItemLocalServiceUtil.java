/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service;

import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseItem;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceInventoryWarehouseItem. This utility wraps
 * <code>com.liferay.commerce.inventory.service.impl.CommerceInventoryWarehouseItemLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Luca Pellizzon
 * @see CommerceInventoryWarehouseItemLocalService
 * @generated
 */
public class CommerceInventoryWarehouseItemLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.inventory.service.impl.CommerceInventoryWarehouseItemLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce inventory warehouse item to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryWarehouseItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryWarehouseItem the commerce inventory warehouse item
	 * @return the commerce inventory warehouse item that was added
	 */
	public static CommerceInventoryWarehouseItem
		addCommerceInventoryWarehouseItem(
			CommerceInventoryWarehouseItem commerceInventoryWarehouseItem) {

		return getService().addCommerceInventoryWarehouseItem(
			commerceInventoryWarehouseItem);
	}

	public static CommerceInventoryWarehouseItem
			addCommerceInventoryWarehouseItem(
				String externalReferenceCode, long userId,
				long commerceInventoryWarehouseId,
				java.math.BigDecimal quantity,
				java.math.BigDecimal reservedQuantity, String sku,
				String unitOfMeasureKey)
		throws PortalException {

		return getService().addCommerceInventoryWarehouseItem(
			externalReferenceCode, userId, commerceInventoryWarehouseId,
			quantity, reservedQuantity, sku, unitOfMeasureKey);
	}

	public static CommerceInventoryWarehouseItem
			addOrUpdateCommerceInventoryWarehouseItem(
				String externalReferenceCode, long companyId, long userId,
				long commerceInventoryWarehouseId,
				java.math.BigDecimal quantity,
				java.math.BigDecimal reservedQuantity, String sku,
				String unitOfMeasureKey)
		throws PortalException {

		return getService().addOrUpdateCommerceInventoryWarehouseItem(
			externalReferenceCode, companyId, userId,
			commerceInventoryWarehouseId, quantity, reservedQuantity, sku,
			unitOfMeasureKey);
	}

	public static int countItemsByCompanyId(
		long companyId, String sku, boolean replacePermissionCheck) {

		return getService().countItemsByCompanyId(
			companyId, sku, replacePermissionCheck);
	}

	/**
	 * Creates a new commerce inventory warehouse item with the primary key. Does not add the commerce inventory warehouse item to the database.
	 *
	 * @param commerceInventoryWarehouseItemId the primary key for the new commerce inventory warehouse item
	 * @return the new commerce inventory warehouse item
	 */
	public static CommerceInventoryWarehouseItem
		createCommerceInventoryWarehouseItem(
			long commerceInventoryWarehouseItemId) {

		return getService().createCommerceInventoryWarehouseItem(
			commerceInventoryWarehouseItemId);
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
	 * Deletes the commerce inventory warehouse item from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryWarehouseItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryWarehouseItem the commerce inventory warehouse item
	 * @return the commerce inventory warehouse item that was removed
	 */
	public static CommerceInventoryWarehouseItem
		deleteCommerceInventoryWarehouseItem(
			CommerceInventoryWarehouseItem commerceInventoryWarehouseItem) {

		return getService().deleteCommerceInventoryWarehouseItem(
			commerceInventoryWarehouseItem);
	}

	/**
	 * Deletes the commerce inventory warehouse item with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryWarehouseItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryWarehouseItemId the primary key of the commerce inventory warehouse item
	 * @return the commerce inventory warehouse item that was removed
	 * @throws PortalException if a commerce inventory warehouse item with the primary key could not be found
	 */
	public static CommerceInventoryWarehouseItem
			deleteCommerceInventoryWarehouseItem(
				long commerceInventoryWarehouseItemId)
		throws PortalException {

		return getService().deleteCommerceInventoryWarehouseItem(
			commerceInventoryWarehouseItemId);
	}

	public static void deleteCommerceInventoryWarehouseItems(
		long commerceInventoryWarehouseId) {

		getService().deleteCommerceInventoryWarehouseItems(
			commerceInventoryWarehouseId);
	}

	public static void deleteCommerceInventoryWarehouseItems(
		long companyId, String sku, String unitOfMeasureKey) {

		getService().deleteCommerceInventoryWarehouseItems(
			companyId, sku, unitOfMeasureKey);
	}

	public static void deleteCommerceInventoryWarehouseItemsByCompanyId(
		long companyId) {

		getService().deleteCommerceInventoryWarehouseItemsByCompanyId(
			companyId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryWarehouseItemModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryWarehouseItemModelImpl</code>.
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

	public static CommerceInventoryWarehouseItem
		fetchCommerceInventoryWarehouseItem(
			long commerceInventoryWarehouseItemId) {

		return getService().fetchCommerceInventoryWarehouseItem(
			commerceInventoryWarehouseItemId);
	}

	public static CommerceInventoryWarehouseItem
		fetchCommerceInventoryWarehouseItem(
			long commerceInventoryWarehouseId, String sku,
			String unitOfMeasureKey) {

		return getService().fetchCommerceInventoryWarehouseItem(
			commerceInventoryWarehouseId, sku, unitOfMeasureKey);
	}

	public static CommerceInventoryWarehouseItem
		fetchCommerceInventoryWarehouseItemByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return getService().
			fetchCommerceInventoryWarehouseItemByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce inventory warehouse item with the matching UUID and company.
	 *
	 * @param uuid the commerce inventory warehouse item's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce inventory warehouse item, or <code>null</code> if a matching commerce inventory warehouse item could not be found
	 */
	public static CommerceInventoryWarehouseItem
		fetchCommerceInventoryWarehouseItemByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().
			fetchCommerceInventoryWarehouseItemByUuidAndCompanyId(
				uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<Long> getCommerceInventoryWarehouseIds(
		long companyId, String sku, String unitOfMeasureKey) {

		return getService().getCommerceInventoryWarehouseIds(
			companyId, sku, unitOfMeasureKey);
	}

	/**
	 * Returns the commerce inventory warehouse item with the primary key.
	 *
	 * @param commerceInventoryWarehouseItemId the primary key of the commerce inventory warehouse item
	 * @return the commerce inventory warehouse item
	 * @throws PortalException if a commerce inventory warehouse item with the primary key could not be found
	 */
	public static CommerceInventoryWarehouseItem
			getCommerceInventoryWarehouseItem(
				long commerceInventoryWarehouseItemId)
		throws PortalException {

		return getService().getCommerceInventoryWarehouseItem(
			commerceInventoryWarehouseItemId);
	}

	public static CommerceInventoryWarehouseItem
			getCommerceInventoryWarehouseItemByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().
			getCommerceInventoryWarehouseItemByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce inventory warehouse item with the matching UUID and company.
	 *
	 * @param uuid the commerce inventory warehouse item's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce inventory warehouse item
	 * @throws PortalException if a matching commerce inventory warehouse item could not be found
	 */
	public static CommerceInventoryWarehouseItem
			getCommerceInventoryWarehouseItemByUuidAndCompanyId(
				String uuid, long companyId)
		throws PortalException {

		return getService().getCommerceInventoryWarehouseItemByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the commerce inventory warehouse items.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryWarehouseItemModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce inventory warehouse items
	 * @param end the upper bound of the range of commerce inventory warehouse items (not inclusive)
	 * @return the range of commerce inventory warehouse items
	 */
	public static List<CommerceInventoryWarehouseItem>
		getCommerceInventoryWarehouseItems(int start, int end) {

		return getService().getCommerceInventoryWarehouseItems(start, end);
	}

	public static List<CommerceInventoryWarehouseItem>
		getCommerceInventoryWarehouseItems(
			long commerceInventoryWarehouseId, int start, int end) {

		return getService().getCommerceInventoryWarehouseItems(
			commerceInventoryWarehouseId, start, end);
	}

	public static List<CommerceInventoryWarehouseItem>
		getCommerceInventoryWarehouseItemsByCompanyIdSkuAndUnitOfMeasureKey(
			long companyId, String sku, String unitOfMeasureKey, int start,
			int end, boolean replacePermissionCheck) {

		return getService().
			getCommerceInventoryWarehouseItemsByCompanyIdSkuAndUnitOfMeasureKey(
				companyId, sku, unitOfMeasureKey, start, end,
				replacePermissionCheck);
	}

	public static List<CommerceInventoryWarehouseItem>
		getCommerceInventoryWarehouseItemsByModifiedDate(
			long companyId, java.util.Date startDate, java.util.Date endDate,
			int start, int end) {

		return getService().getCommerceInventoryWarehouseItemsByModifiedDate(
			companyId, startDate, endDate, start, end);
	}

	/**
	 * Returns the number of commerce inventory warehouse items.
	 *
	 * @return the number of commerce inventory warehouse items
	 */
	public static int getCommerceInventoryWarehouseItemsCount() {
		return getService().getCommerceInventoryWarehouseItemsCount();
	}

	public static int getCommerceInventoryWarehouseItemsCount(
		long companyId, long accountEntryId, long groupId, String sku,
		String unitOfMeasureKey) {

		return getService().getCommerceInventoryWarehouseItemsCount(
			companyId, accountEntryId, groupId, sku, unitOfMeasureKey);
	}

	public static int getCommerceInventoryWarehouseItemsCount(
		long companyId, String sku, String unitOfMeasureKey,
		boolean replacePermissionCheck) {

		return getService().getCommerceInventoryWarehouseItemsCount(
			companyId, sku, unitOfMeasureKey, replacePermissionCheck);
	}

	public static int getCommerceInventoryWarehouseItemsCountByModifiedDate(
		long companyId, java.util.Date startDate, java.util.Date endDate) {

		return getService().
			getCommerceInventoryWarehouseItemsCountByModifiedDate(
				companyId, startDate, endDate);
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

	public static List<com.liferay.commerce.inventory.model.CIWarehouseItem>
		getItemsByCompanyId(
			long companyId, String sku, int start, int end,
			boolean replacePermissionCheck) {

		return getService().getItemsByCompanyId(
			companyId, sku, start, end, replacePermissionCheck);
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

	public static java.math.BigDecimal getStockQuantity(
		long companyId, long accountEntryId, long groupId, String sku,
		String unitOfMeasureKey) {

		return getService().getStockQuantity(
			companyId, accountEntryId, groupId, sku, unitOfMeasureKey);
	}

	public static java.math.BigDecimal getStockQuantity(
		long companyId, String sku, String unitOfMeasureKey) {

		return getService().getStockQuantity(companyId, sku, unitOfMeasureKey);
	}

	public static CommerceInventoryWarehouseItem
			increaseCommerceInventoryWarehouseItemQuantity(
				long userId, long commerceInventoryWarehouseItemId,
				java.math.BigDecimal quantity)
		throws PortalException {

		return getService().increaseCommerceInventoryWarehouseItemQuantity(
			userId, commerceInventoryWarehouseItemId, quantity);
	}

	public static void moveQuantitiesBetweenWarehouses(
			long userId, long fromCommerceInventoryWarehouseId,
			long toCommerceInventoryWarehouseId, java.math.BigDecimal quantity,
			String sku, String unitOfMeasureKey)
		throws PortalException {

		getService().moveQuantitiesBetweenWarehouses(
			userId, fromCommerceInventoryWarehouseId,
			toCommerceInventoryWarehouseId, quantity, sku, unitOfMeasureKey);
	}

	/**
	 * Updates the commerce inventory warehouse item in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryWarehouseItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryWarehouseItem the commerce inventory warehouse item
	 * @return the commerce inventory warehouse item that was updated
	 */
	public static CommerceInventoryWarehouseItem
		updateCommerceInventoryWarehouseItem(
			CommerceInventoryWarehouseItem commerceInventoryWarehouseItem) {

		return getService().updateCommerceInventoryWarehouseItem(
			commerceInventoryWarehouseItem);
	}

	public static CommerceInventoryWarehouseItem
			updateCommerceInventoryWarehouseItem(
				long userId, long commerceInventoryWarehouseItemId,
				java.math.BigDecimal quantity,
				java.math.BigDecimal reservedQuantity, String unitOfMeasureKey,
				long mvccVersion)
		throws PortalException {

		return getService().updateCommerceInventoryWarehouseItem(
			userId, commerceInventoryWarehouseItemId, quantity,
			reservedQuantity, unitOfMeasureKey, mvccVersion);
	}

	public static CommerceInventoryWarehouseItemLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceInventoryWarehouseItemLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceInventoryWarehouseItemLocalServiceUtil.class,
			CommerceInventoryWarehouseItemLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:14017774
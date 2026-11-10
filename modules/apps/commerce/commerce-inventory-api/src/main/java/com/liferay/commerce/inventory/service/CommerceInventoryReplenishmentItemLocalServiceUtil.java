/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service;

import com.liferay.commerce.inventory.model.CommerceInventoryReplenishmentItem;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceInventoryReplenishmentItem. This utility wraps
 * <code>com.liferay.commerce.inventory.service.impl.CommerceInventoryReplenishmentItemLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Luca Pellizzon
 * @see CommerceInventoryReplenishmentItemLocalService
 * @generated
 */
public class CommerceInventoryReplenishmentItemLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.inventory.service.impl.CommerceInventoryReplenishmentItemLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce inventory replenishment item to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryReplenishmentItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryReplenishmentItem the commerce inventory replenishment item
	 * @return the commerce inventory replenishment item that was added
	 */
	public static CommerceInventoryReplenishmentItem
		addCommerceInventoryReplenishmentItem(
			CommerceInventoryReplenishmentItem
				commerceInventoryReplenishmentItem) {

		return getService().addCommerceInventoryReplenishmentItem(
			commerceInventoryReplenishmentItem);
	}

	public static CommerceInventoryReplenishmentItem
			addCommerceInventoryReplenishmentItem(
				String externalReferenceCode, long userId,
				long commerceInventoryWarehouseId,
				java.util.Date availabilityDate, java.math.BigDecimal quantity,
				String sku, String unitOfMeasureKey)
		throws PortalException {

		return getService().addCommerceInventoryReplenishmentItem(
			externalReferenceCode, userId, commerceInventoryWarehouseId,
			availabilityDate, quantity, sku, unitOfMeasureKey);
	}

	/**
	 * Creates a new commerce inventory replenishment item with the primary key. Does not add the commerce inventory replenishment item to the database.
	 *
	 * @param commerceInventoryReplenishmentItemId the primary key for the new commerce inventory replenishment item
	 * @return the new commerce inventory replenishment item
	 */
	public static CommerceInventoryReplenishmentItem
		createCommerceInventoryReplenishmentItem(
			long commerceInventoryReplenishmentItemId) {

		return getService().createCommerceInventoryReplenishmentItem(
			commerceInventoryReplenishmentItemId);
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
	 * Deletes the commerce inventory replenishment item from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryReplenishmentItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryReplenishmentItem the commerce inventory replenishment item
	 * @return the commerce inventory replenishment item that was removed
	 */
	public static CommerceInventoryReplenishmentItem
		deleteCommerceInventoryReplenishmentItem(
			CommerceInventoryReplenishmentItem
				commerceInventoryReplenishmentItem) {

		return getService().deleteCommerceInventoryReplenishmentItem(
			commerceInventoryReplenishmentItem);
	}

	/**
	 * Deletes the commerce inventory replenishment item with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryReplenishmentItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryReplenishmentItemId the primary key of the commerce inventory replenishment item
	 * @return the commerce inventory replenishment item that was removed
	 * @throws PortalException if a commerce inventory replenishment item with the primary key could not be found
	 */
	public static CommerceInventoryReplenishmentItem
			deleteCommerceInventoryReplenishmentItem(
				long commerceInventoryReplenishmentItemId)
		throws PortalException {

		return getService().deleteCommerceInventoryReplenishmentItem(
			commerceInventoryReplenishmentItemId);
	}

	public static void deleteCommerceInventoryReplenishmentItems(
		long commerceInventoryWarehouseId) {

		getService().deleteCommerceInventoryReplenishmentItems(
			commerceInventoryWarehouseId);
	}

	public static void deleteCommerceInventoryReplenishmentItems(
		long companyId, String sku, String unitOfMeasureKey) {

		getService().deleteCommerceInventoryReplenishmentItems(
			companyId, sku, unitOfMeasureKey);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
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

	public static CommerceInventoryReplenishmentItem
		fetchCommerceInventoryReplenishmentItem(
			long commerceInventoryReplenishmentItemId) {

		return getService().fetchCommerceInventoryReplenishmentItem(
			commerceInventoryReplenishmentItemId);
	}

	public static CommerceInventoryReplenishmentItem
		fetchCommerceInventoryReplenishmentItem(
			long companyId, String sku, String unitOfMeasureKey,
			OrderByComparator<CommerceInventoryReplenishmentItem>
				orderByComparator) {

		return getService().fetchCommerceInventoryReplenishmentItem(
			companyId, sku, unitOfMeasureKey, orderByComparator);
	}

	public static CommerceInventoryReplenishmentItem
		fetchCommerceInventoryReplenishmentItemByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return getService().
			fetchCommerceInventoryReplenishmentItemByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce inventory replenishment item with the matching UUID and company.
	 *
	 * @param uuid the commerce inventory replenishment item's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce inventory replenishment item, or <code>null</code> if a matching commerce inventory replenishment item could not be found
	 */
	public static CommerceInventoryReplenishmentItem
		fetchCommerceInventoryReplenishmentItemByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().
			fetchCommerceInventoryReplenishmentItemByUuidAndCompanyId(
				uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce inventory replenishment item with the primary key.
	 *
	 * @param commerceInventoryReplenishmentItemId the primary key of the commerce inventory replenishment item
	 * @return the commerce inventory replenishment item
	 * @throws PortalException if a commerce inventory replenishment item with the primary key could not be found
	 */
	public static CommerceInventoryReplenishmentItem
			getCommerceInventoryReplenishmentItem(
				long commerceInventoryReplenishmentItemId)
		throws PortalException {

		return getService().getCommerceInventoryReplenishmentItem(
			commerceInventoryReplenishmentItemId);
	}

	public static CommerceInventoryReplenishmentItem
			getCommerceInventoryReplenishmentItemByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().
			getCommerceInventoryReplenishmentItemByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce inventory replenishment item with the matching UUID and company.
	 *
	 * @param uuid the commerce inventory replenishment item's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce inventory replenishment item
	 * @throws PortalException if a matching commerce inventory replenishment item could not be found
	 */
	public static CommerceInventoryReplenishmentItem
			getCommerceInventoryReplenishmentItemByUuidAndCompanyId(
				String uuid, long companyId)
		throws PortalException {

		return getService().
			getCommerceInventoryReplenishmentItemByUuidAndCompanyId(
				uuid, companyId);
	}

	/**
	 * Returns a range of all the commerce inventory replenishment items.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @return the range of commerce inventory replenishment items
	 */
	public static List<CommerceInventoryReplenishmentItem>
		getCommerceInventoryReplenishmentItems(int start, int end) {

		return getService().getCommerceInventoryReplenishmentItems(start, end);
	}

	public static List<CommerceInventoryReplenishmentItem>
		getCommerceInventoryReplenishmentItemsByCompanyIdSkuAndUnitOfMeasureKey(
			long companyId, String sku, String unitOfMeasureKey, int start,
			int end, boolean replacePermissionCheck) {

		return getService().
			getCommerceInventoryReplenishmentItemsByCompanyIdSkuAndUnitOfMeasureKey(
				companyId, sku, unitOfMeasureKey, start, end,
				replacePermissionCheck);
	}

	/**
	 * Returns the number of commerce inventory replenishment items.
	 *
	 * @return the number of commerce inventory replenishment items
	 */
	public static int getCommerceInventoryReplenishmentItemsCount() {
		return getService().getCommerceInventoryReplenishmentItemsCount();
	}

	public static java.math.BigDecimal
		getCommerceInventoryReplenishmentItemsCount(
			long commerceInventoryWarehouseId, String sku,
			String unitOfMeasureKey) {

		return getService().getCommerceInventoryReplenishmentItemsCount(
			commerceInventoryWarehouseId, sku, unitOfMeasureKey);
	}

	public static List<Long> getCommerceInventoryWarehouseIds(
		long companyId, String sku, String unitOfMeasureKey) {

		return getService().getCommerceInventoryWarehouseIds(
			companyId, sku, unitOfMeasureKey);
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

	/**
	 * Updates the commerce inventory replenishment item in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryReplenishmentItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryReplenishmentItem the commerce inventory replenishment item
	 * @return the commerce inventory replenishment item that was updated
	 */
	public static CommerceInventoryReplenishmentItem
		updateCommerceInventoryReplenishmentItem(
			CommerceInventoryReplenishmentItem
				commerceInventoryReplenishmentItem) {

		return getService().updateCommerceInventoryReplenishmentItem(
			commerceInventoryReplenishmentItem);
	}

	public static CommerceInventoryReplenishmentItem
			updateCommerceInventoryReplenishmentItem(
				String externalReferenceCode,
				long commerceInventoryReplenishmentItemId,
				java.util.Date availabilityDate, java.math.BigDecimal quantity,
				long mvccVersion)
		throws PortalException {

		return getService().updateCommerceInventoryReplenishmentItem(
			externalReferenceCode, commerceInventoryReplenishmentItemId,
			availabilityDate, quantity, mvccVersion);
	}

	public static CommerceInventoryReplenishmentItemLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot
		<CommerceInventoryReplenishmentItemLocalService> _serviceSnapshot =
			new Snapshot<>(
				CommerceInventoryReplenishmentItemLocalServiceUtil.class,
				CommerceInventoryReplenishmentItemLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1694088232
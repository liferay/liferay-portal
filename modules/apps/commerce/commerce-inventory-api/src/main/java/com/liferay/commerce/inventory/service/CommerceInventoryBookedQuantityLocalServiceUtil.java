/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service;

import com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for CommerceInventoryBookedQuantity. This utility wraps
 * <code>com.liferay.commerce.inventory.service.impl.CommerceInventoryBookedQuantityLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Luca Pellizzon
 * @see CommerceInventoryBookedQuantityLocalService
 * @generated
 */
public class CommerceInventoryBookedQuantityLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.inventory.service.impl.CommerceInventoryBookedQuantityLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce inventory booked quantity to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryBookedQuantityLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryBookedQuantity the commerce inventory booked quantity
	 * @return the commerce inventory booked quantity that was added
	 */
	public static CommerceInventoryBookedQuantity
		addCommerceInventoryBookedQuantity(
			CommerceInventoryBookedQuantity commerceInventoryBookedQuantity) {

		return getService().addCommerceInventoryBookedQuantity(
			commerceInventoryBookedQuantity);
	}

	public static CommerceInventoryBookedQuantity
			addCommerceInventoryBookedQuantity(
				long userId, java.util.Date expirationDate,
				java.math.BigDecimal quantity, String sku,
				String unitOfMeasureKey, Map<String, String> context)
		throws PortalException {

		return getService().addCommerceInventoryBookedQuantity(
			userId, expirationDate, quantity, sku, unitOfMeasureKey, context);
	}

	public static void checkCommerceInventoryBookedQuantities() {
		getService().checkCommerceInventoryBookedQuantities();
	}

	public static CommerceInventoryBookedQuantity
			consumeCommerceInventoryBookedQuantity(
				long commerceInventoryBookedQuantityId,
				java.math.BigDecimal quantity)
		throws com.liferay.commerce.inventory.exception.
			NoSuchInventoryBookedQuantityException {

		return getService().consumeCommerceInventoryBookedQuantity(
			commerceInventoryBookedQuantityId, quantity);
	}

	/**
	 * Creates a new commerce inventory booked quantity with the primary key. Does not add the commerce inventory booked quantity to the database.
	 *
	 * @param commerceInventoryBookedQuantityId the primary key for the new commerce inventory booked quantity
	 * @return the new commerce inventory booked quantity
	 */
	public static CommerceInventoryBookedQuantity
		createCommerceInventoryBookedQuantity(
			long commerceInventoryBookedQuantityId) {

		return getService().createCommerceInventoryBookedQuantity(
			commerceInventoryBookedQuantityId);
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
	 * Deletes the commerce inventory booked quantity from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryBookedQuantityLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryBookedQuantity the commerce inventory booked quantity
	 * @return the commerce inventory booked quantity that was removed
	 */
	public static CommerceInventoryBookedQuantity
		deleteCommerceInventoryBookedQuantity(
			CommerceInventoryBookedQuantity commerceInventoryBookedQuantity) {

		return getService().deleteCommerceInventoryBookedQuantity(
			commerceInventoryBookedQuantity);
	}

	/**
	 * Deletes the commerce inventory booked quantity with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryBookedQuantityLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryBookedQuantityId the primary key of the commerce inventory booked quantity
	 * @return the commerce inventory booked quantity that was removed
	 * @throws PortalException if a commerce inventory booked quantity with the primary key could not be found
	 */
	public static CommerceInventoryBookedQuantity
			deleteCommerceInventoryBookedQuantity(
				long commerceInventoryBookedQuantityId)
		throws PortalException {

		return getService().deleteCommerceInventoryBookedQuantity(
			commerceInventoryBookedQuantityId);
	}

	public static CommerceInventoryBookedQuantity
			deleteCommerceInventoryBookedQuantity(
				long userId, long commerceInventoryBookedQuantityId,
				Map<String, String> context,
				com.liferay.commerce.inventory.type.CommerceInventoryAuditType
					commerceInventoryAuditType)
		throws PortalException {

		return getService().deleteCommerceInventoryBookedQuantity(
			userId, commerceInventoryBookedQuantityId, context,
			commerceInventoryAuditType);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryBookedQuantityModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryBookedQuantityModelImpl</code>.
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

	public static CommerceInventoryBookedQuantity
		fetchCommerceInventoryBookedQuantity(
			long commerceInventoryBookedQuantityId) {

		return getService().fetchCommerceInventoryBookedQuantity(
			commerceInventoryBookedQuantityId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the commerce inventory booked quantities.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryBookedQuantityModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce inventory booked quantities
	 * @param end the upper bound of the range of commerce inventory booked quantities (not inclusive)
	 * @return the range of commerce inventory booked quantities
	 */
	public static List<CommerceInventoryBookedQuantity>
		getCommerceInventoryBookedQuantities(int start, int end) {

		return getService().getCommerceInventoryBookedQuantities(start, end);
	}

	public static List<CommerceInventoryBookedQuantity>
		getCommerceInventoryBookedQuantities(
			long companyId, String sku, String unitOfMeasureKey, int start,
			int end) {

		return getService().getCommerceInventoryBookedQuantities(
			companyId, sku, unitOfMeasureKey, start, end);
	}

	public static List<CommerceInventoryBookedQuantity>
			getCommerceInventoryBookedQuantities(
				long companyId, String keywords, String sku,
				String unitOfMeasureKey, int start, int end)
		throws PortalException {

		return getService().getCommerceInventoryBookedQuantities(
			companyId, keywords, sku, unitOfMeasureKey, start, end);
	}

	/**
	 * Returns the number of commerce inventory booked quantities.
	 *
	 * @return the number of commerce inventory booked quantities
	 */
	public static int getCommerceInventoryBookedQuantitiesCount() {
		return getService().getCommerceInventoryBookedQuantitiesCount();
	}

	public static int getCommerceInventoryBookedQuantitiesCount(
			long companyId, String keywords, String sku,
			String unitOfMeasureKey)
		throws PortalException {

		return getService().getCommerceInventoryBookedQuantitiesCount(
			companyId, keywords, sku, unitOfMeasureKey);
	}

	/**
	 * Returns the commerce inventory booked quantity with the primary key.
	 *
	 * @param commerceInventoryBookedQuantityId the primary key of the commerce inventory booked quantity
	 * @return the commerce inventory booked quantity
	 * @throws PortalException if a commerce inventory booked quantity with the primary key could not be found
	 */
	public static CommerceInventoryBookedQuantity
			getCommerceInventoryBookedQuantity(
				long commerceInventoryBookedQuantityId)
		throws PortalException {

		return getService().getCommerceInventoryBookedQuantity(
			commerceInventoryBookedQuantityId);
	}

	public static java.math.BigDecimal getCommerceInventoryBookedQuantity(
		long companyId, long commerceChannelGroupId, String sku,
		String unitOfMeasureKey) {

		return getService().getCommerceInventoryBookedQuantity(
			companyId, commerceChannelGroupId, sku, unitOfMeasureKey);
	}

	public static java.math.BigDecimal getCommerceInventoryBookedQuantity(
		long companyId, String sku, String unitOfMeasureKey) {

		return getService().getCommerceInventoryBookedQuantity(
			companyId, sku, unitOfMeasureKey);
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

	public static CommerceInventoryBookedQuantity
			resetCommerceInventoryBookedQuantity(
				long commerceInventoryBookedQuantityId, long userId,
				java.util.Date expirationDate, java.math.BigDecimal quantity,
				String sku, String unitOfMeasureKey,
				Map<String, String> context)
		throws PortalException {

		return getService().resetCommerceInventoryBookedQuantity(
			commerceInventoryBookedQuantityId, userId, expirationDate, quantity,
			sku, unitOfMeasureKey, context);
	}

	public static CommerceInventoryBookedQuantity
			restockCommerceInventoryBookedQuantity(
				long userId, long commerceInventoryBookedQuantityId,
				Map<String, String> context)
		throws PortalException {

		return getService().restockCommerceInventoryBookedQuantity(
			userId, commerceInventoryBookedQuantityId, context);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommerceInventoryBookedQuantity>
				searchCommerceInventoryBookedQuantities(
					com.liferay.portal.kernel.search.SearchContext
						searchContext)
			throws PortalException {

		return getService().searchCommerceInventoryBookedQuantities(
			searchContext);
	}

	public static int searchCommerceInventoryBookedQuantitiesCount(
			com.liferay.portal.kernel.search.SearchContext searchContext)
		throws PortalException {

		return getService().searchCommerceInventoryBookedQuantitiesCount(
			searchContext);
	}

	/**
	 * Updates the commerce inventory booked quantity in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryBookedQuantityLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryBookedQuantity the commerce inventory booked quantity
	 * @return the commerce inventory booked quantity that was updated
	 */
	public static CommerceInventoryBookedQuantity
		updateCommerceInventoryBookedQuantity(
			CommerceInventoryBookedQuantity commerceInventoryBookedQuantity) {

		return getService().updateCommerceInventoryBookedQuantity(
			commerceInventoryBookedQuantity);
	}

	public static CommerceInventoryBookedQuantity
			updateCommerceInventoryBookedQuantity(
				long userId, long commerceInventoryBookedQuantityId,
				java.math.BigDecimal quantity, Map<String, String> context,
				long mvccVersion)
		throws PortalException {

		return getService().updateCommerceInventoryBookedQuantity(
			userId, commerceInventoryBookedQuantityId, quantity, context,
			mvccVersion);
	}

	public static CommerceInventoryBookedQuantityLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceInventoryBookedQuantityLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceInventoryBookedQuantityLocalServiceUtil.class,
			CommerceInventoryBookedQuantityLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1233616534
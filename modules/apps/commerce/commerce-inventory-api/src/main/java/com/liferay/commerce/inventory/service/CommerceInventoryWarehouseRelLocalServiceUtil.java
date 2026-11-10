/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service;

import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceInventoryWarehouseRel. This utility wraps
 * <code>com.liferay.commerce.inventory.service.impl.CommerceInventoryWarehouseRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Luca Pellizzon
 * @see CommerceInventoryWarehouseRelLocalService
 * @generated
 */
public class CommerceInventoryWarehouseRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.inventory.service.impl.CommerceInventoryWarehouseRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce inventory warehouse rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryWarehouseRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryWarehouseRel the commerce inventory warehouse rel
	 * @return the commerce inventory warehouse rel that was added
	 */
	public static CommerceInventoryWarehouseRel
		addCommerceInventoryWarehouseRel(
			CommerceInventoryWarehouseRel commerceInventoryWarehouseRel) {

		return getService().addCommerceInventoryWarehouseRel(
			commerceInventoryWarehouseRel);
	}

	public static CommerceInventoryWarehouseRel
			addCommerceInventoryWarehouseRel(
				long userId, String className, long classPK,
				long commerceInventoryWarehouseId)
		throws PortalException {

		return getService().addCommerceInventoryWarehouseRel(
			userId, className, classPK, commerceInventoryWarehouseId);
	}

	/**
	 * Creates a new commerce inventory warehouse rel with the primary key. Does not add the commerce inventory warehouse rel to the database.
	 *
	 * @param commerceInventoryWarehouseRelId the primary key for the new commerce inventory warehouse rel
	 * @return the new commerce inventory warehouse rel
	 */
	public static CommerceInventoryWarehouseRel
		createCommerceInventoryWarehouseRel(
			long commerceInventoryWarehouseRelId) {

		return getService().createCommerceInventoryWarehouseRel(
			commerceInventoryWarehouseRelId);
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
	 * Deletes the commerce inventory warehouse rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryWarehouseRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryWarehouseRel the commerce inventory warehouse rel
	 * @return the commerce inventory warehouse rel that was removed
	 * @throws PortalException
	 */
	public static CommerceInventoryWarehouseRel
			deleteCommerceInventoryWarehouseRel(
				CommerceInventoryWarehouseRel commerceInventoryWarehouseRel)
		throws PortalException {

		return getService().deleteCommerceInventoryWarehouseRel(
			commerceInventoryWarehouseRel);
	}

	/**
	 * Deletes the commerce inventory warehouse rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryWarehouseRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryWarehouseRelId the primary key of the commerce inventory warehouse rel
	 * @return the commerce inventory warehouse rel that was removed
	 * @throws PortalException if a commerce inventory warehouse rel with the primary key could not be found
	 */
	public static CommerceInventoryWarehouseRel
			deleteCommerceInventoryWarehouseRel(
				long commerceInventoryWarehouseRelId)
		throws PortalException {

		return getService().deleteCommerceInventoryWarehouseRel(
			commerceInventoryWarehouseRelId);
	}

	public static void deleteCommerceInventoryWarehouseRels(
			long commerceInventoryWarehouseId)
		throws PortalException {

		getService().deleteCommerceInventoryWarehouseRels(
			commerceInventoryWarehouseId);
	}

	public static void deleteCommerceInventoryWarehouseRels(
			String className, long commerceInventoryWarehouseId)
		throws PortalException {

		getService().deleteCommerceInventoryWarehouseRels(
			className, commerceInventoryWarehouseId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryWarehouseRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryWarehouseRelModelImpl</code>.
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

	public static CommerceInventoryWarehouseRel
		fetchCommerceInventoryWarehouseRel(
			long commerceInventoryWarehouseRelId) {

		return getService().fetchCommerceInventoryWarehouseRel(
			commerceInventoryWarehouseRelId);
	}

	public static List<CommerceInventoryWarehouseRel>
		getAccountEntryCommerceInventoryWarehouseRels(
			long commerceInventoryWarehouseId, String keywords, int start,
			int end) {

		return getService().getAccountEntryCommerceInventoryWarehouseRels(
			commerceInventoryWarehouseId, keywords, start, end);
	}

	public static int getAccountEntryCommerceInventoryWarehouseRelsCount(
		long commerceInventoryWarehouseId, String keywords) {

		return getService().getAccountEntryCommerceInventoryWarehouseRelsCount(
			commerceInventoryWarehouseId, keywords);
	}

	public static List<CommerceInventoryWarehouseRel>
		getAccountGroupCommerceInventoryWarehouseRels(
			long commerceInventoryWarehouseId, String keywords, int start,
			int end) {

		return getService().getAccountGroupCommerceInventoryWarehouseRels(
			commerceInventoryWarehouseId, keywords, start, end);
	}

	public static int getAccountGroupCommerceInventoryWarehouseRelsCount(
		long commerceInventoryWarehouseId, String keywords) {

		return getService().getAccountGroupCommerceInventoryWarehouseRelsCount(
			commerceInventoryWarehouseId, keywords);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce inventory warehouse rel with the primary key.
	 *
	 * @param commerceInventoryWarehouseRelId the primary key of the commerce inventory warehouse rel
	 * @return the commerce inventory warehouse rel
	 * @throws PortalException if a commerce inventory warehouse rel with the primary key could not be found
	 */
	public static CommerceInventoryWarehouseRel
			getCommerceInventoryWarehouseRel(
				long commerceInventoryWarehouseRelId)
		throws PortalException {

		return getService().getCommerceInventoryWarehouseRel(
			commerceInventoryWarehouseRelId);
	}

	/**
	 * Returns a range of all the commerce inventory warehouse rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryWarehouseRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce inventory warehouse rels
	 * @param end the upper bound of the range of commerce inventory warehouse rels (not inclusive)
	 * @return the range of commerce inventory warehouse rels
	 */
	public static List<CommerceInventoryWarehouseRel>
		getCommerceInventoryWarehouseRels(int start, int end) {

		return getService().getCommerceInventoryWarehouseRels(start, end);
	}

	public static List<CommerceInventoryWarehouseRel>
		getCommerceInventoryWarehouseRels(
			String className, long commerceInventoryWarehouseId) {

		return getService().getCommerceInventoryWarehouseRels(
			className, commerceInventoryWarehouseId);
	}

	/**
	 * Returns the number of commerce inventory warehouse rels.
	 *
	 * @return the number of commerce inventory warehouse rels
	 */
	public static int getCommerceInventoryWarehouseRelsCount() {
		return getService().getCommerceInventoryWarehouseRelsCount();
	}

	public static List<CommerceInventoryWarehouseRel>
		getCommerceOrderTypeCommerceInventoryWarehouseRels(
			long commerceInventoryWarehouseId, String keywords, int start,
			int end) {

		return getService().getCommerceOrderTypeCommerceInventoryWarehouseRels(
			commerceInventoryWarehouseId, keywords, start, end);
	}

	public static int getCommerceOrderTypeCommerceInventoryWarehouseRelsCount(
		long commerceInventoryWarehouseId, String keywords) {

		return getService().
			getCommerceOrderTypeCommerceInventoryWarehouseRelsCount(
				commerceInventoryWarehouseId, keywords);
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
	 * Updates the commerce inventory warehouse rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryWarehouseRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryWarehouseRel the commerce inventory warehouse rel
	 * @return the commerce inventory warehouse rel that was updated
	 */
	public static CommerceInventoryWarehouseRel
		updateCommerceInventoryWarehouseRel(
			CommerceInventoryWarehouseRel commerceInventoryWarehouseRel) {

		return getService().updateCommerceInventoryWarehouseRel(
			commerceInventoryWarehouseRel);
	}

	public static CommerceInventoryWarehouseRelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceInventoryWarehouseRelLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceInventoryWarehouseRelLocalServiceUtil.class,
			CommerceInventoryWarehouseRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-205328917
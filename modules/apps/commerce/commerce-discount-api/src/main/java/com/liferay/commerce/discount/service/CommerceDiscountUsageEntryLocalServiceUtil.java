/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service;

import com.liferay.commerce.discount.model.CommerceDiscountUsageEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceDiscountUsageEntry. This utility wraps
 * <code>com.liferay.commerce.discount.service.impl.CommerceDiscountUsageEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CommerceDiscountUsageEntryLocalService
 * @generated
 */
public class CommerceDiscountUsageEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.discount.service.impl.CommerceDiscountUsageEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce discount usage entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountUsageEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountUsageEntry the commerce discount usage entry
	 * @return the commerce discount usage entry that was added
	 */
	public static CommerceDiscountUsageEntry addCommerceDiscountUsageEntry(
		CommerceDiscountUsageEntry commerceDiscountUsageEntry) {

		return getService().addCommerceDiscountUsageEntry(
			commerceDiscountUsageEntry);
	}

	public static CommerceDiscountUsageEntry addCommerceDiscountUsageEntry(
			long commerceAccountId, long commerceOrderId,
			long commerceDiscountId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceDiscountUsageEntry(
			commerceAccountId, commerceOrderId, commerceDiscountId,
			serviceContext);
	}

	/**
	 * Creates a new commerce discount usage entry with the primary key. Does not add the commerce discount usage entry to the database.
	 *
	 * @param commerceDiscountUsageEntryId the primary key for the new commerce discount usage entry
	 * @return the new commerce discount usage entry
	 */
	public static CommerceDiscountUsageEntry createCommerceDiscountUsageEntry(
		long commerceDiscountUsageEntryId) {

		return getService().createCommerceDiscountUsageEntry(
			commerceDiscountUsageEntryId);
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
	 * Deletes the commerce discount usage entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountUsageEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountUsageEntry the commerce discount usage entry
	 * @return the commerce discount usage entry that was removed
	 */
	public static CommerceDiscountUsageEntry deleteCommerceDiscountUsageEntry(
		CommerceDiscountUsageEntry commerceDiscountUsageEntry) {

		return getService().deleteCommerceDiscountUsageEntry(
			commerceDiscountUsageEntry);
	}

	/**
	 * Deletes the commerce discount usage entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountUsageEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountUsageEntryId the primary key of the commerce discount usage entry
	 * @return the commerce discount usage entry that was removed
	 * @throws PortalException if a commerce discount usage entry with the primary key could not be found
	 */
	public static CommerceDiscountUsageEntry deleteCommerceDiscountUsageEntry(
			long commerceDiscountUsageEntryId)
		throws PortalException {

		return getService().deleteCommerceDiscountUsageEntry(
			commerceDiscountUsageEntryId);
	}

	public static void deleteCommerceUsageEntry(
		long commerceAccountId, long commerceOrderId, long commerceDiscountId) {

		getService().deleteCommerceUsageEntry(
			commerceAccountId, commerceOrderId, commerceDiscountId);
	}

	public static void deleteCommerceUsageEntryByDiscountId(
		long commerceDiscountId) {

		getService().deleteCommerceUsageEntryByDiscountId(commerceDiscountId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountUsageEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountUsageEntryModelImpl</code>.
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

	public static CommerceDiscountUsageEntry fetchCommerceDiscountUsageEntry(
		long commerceDiscountUsageEntryId) {

		return getService().fetchCommerceDiscountUsageEntry(
			commerceDiscountUsageEntryId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the commerce discount usage entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountUsageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce discount usage entries
	 * @param end the upper bound of the range of commerce discount usage entries (not inclusive)
	 * @return the range of commerce discount usage entries
	 */
	public static List<CommerceDiscountUsageEntry>
		getCommerceDiscountUsageEntries(int start, int end) {

		return getService().getCommerceDiscountUsageEntries(start, end);
	}

	/**
	 * Returns the number of commerce discount usage entries.
	 *
	 * @return the number of commerce discount usage entries
	 */
	public static int getCommerceDiscountUsageEntriesCount() {
		return getService().getCommerceDiscountUsageEntriesCount();
	}

	public static int getCommerceDiscountUsageEntriesCount(
		long commerceDiscountId) {

		return getService().getCommerceDiscountUsageEntriesCount(
			commerceDiscountId);
	}

	public static int getCommerceDiscountUsageEntriesCount(
		long commerceAccountId, long commerceOrderId, long commerceDiscountId) {

		return getService().getCommerceDiscountUsageEntriesCount(
			commerceAccountId, commerceOrderId, commerceDiscountId);
	}

	public static int getCommerceDiscountUsageEntriesCountByAccountId(
		long commerceAccountId, long commerceDiscountId) {

		return getService().getCommerceDiscountUsageEntriesCountByAccountId(
			commerceAccountId, commerceDiscountId);
	}

	/**
	 * Returns the commerce discount usage entry with the primary key.
	 *
	 * @param commerceDiscountUsageEntryId the primary key of the commerce discount usage entry
	 * @return the commerce discount usage entry
	 * @throws PortalException if a commerce discount usage entry with the primary key could not be found
	 */
	public static CommerceDiscountUsageEntry getCommerceDiscountUsageEntry(
			long commerceDiscountUsageEntryId)
		throws PortalException {

		return getService().getCommerceDiscountUsageEntry(
			commerceDiscountUsageEntryId);
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
	 * Updates the commerce discount usage entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountUsageEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountUsageEntry the commerce discount usage entry
	 * @return the commerce discount usage entry that was updated
	 */
	public static CommerceDiscountUsageEntry updateCommerceDiscountUsageEntry(
		CommerceDiscountUsageEntry commerceDiscountUsageEntry) {

		return getService().updateCommerceDiscountUsageEntry(
			commerceDiscountUsageEntry);
	}

	public static boolean validateDiscountLimitationUsage(
			long commerceAccountId, long commerceDiscountId)
		throws PortalException {

		return getService().validateDiscountLimitationUsage(
			commerceAccountId, commerceDiscountId);
	}

	public static CommerceDiscountUsageEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceDiscountUsageEntryLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceDiscountUsageEntryLocalServiceUtil.class,
			CommerceDiscountUsageEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1391954603
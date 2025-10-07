/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service;

import com.liferay.osb.faro.model.FaroProjectUsage;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for FaroProjectUsage. This utility wraps
 * <code>com.liferay.osb.faro.service.impl.FaroProjectUsageLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Matthew Kong
 * @see FaroProjectUsageLocalService
 * @generated
 */
public class FaroProjectUsageLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.osb.faro.service.impl.FaroProjectUsageLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the faro project usage to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FaroProjectUsageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param faroProjectUsage the faro project usage
	 * @return the faro project usage that was added
	 */
	public static FaroProjectUsage addFaroProjectUsage(
		FaroProjectUsage faroProjectUsage) {

		return getService().addFaroProjectUsage(faroProjectUsage);
	}

	public static FaroProjectUsage addFaroProjectUsage(
		long companyId, long userId, long faroProjectId,
		long knownIndividualsCount, String monthDateKey, long pageViewsCount,
		java.util.Date usageDate) {

		return getService().addFaroProjectUsage(
			companyId, userId, faroProjectId, knownIndividualsCount,
			monthDateKey, pageViewsCount, usageDate);
	}

	/**
	 * Creates a new faro project usage with the primary key. Does not add the faro project usage to the database.
	 *
	 * @param faroProjectUsageId the primary key for the new faro project usage
	 * @return the new faro project usage
	 */
	public static FaroProjectUsage createFaroProjectUsage(
		long faroProjectUsageId) {

		return getService().createFaroProjectUsage(faroProjectUsageId);
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
	 * Deletes the faro project usage from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FaroProjectUsageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param faroProjectUsage the faro project usage
	 * @return the faro project usage that was removed
	 */
	public static FaroProjectUsage deleteFaroProjectUsage(
		FaroProjectUsage faroProjectUsage) {

		return getService().deleteFaroProjectUsage(faroProjectUsage);
	}

	/**
	 * Deletes the faro project usage with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FaroProjectUsageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param faroProjectUsageId the primary key of the faro project usage
	 * @return the faro project usage that was removed
	 * @throws PortalException if a faro project usage with the primary key could not be found
	 */
	public static FaroProjectUsage deleteFaroProjectUsage(
			long faroProjectUsageId)
		throws PortalException {

		return getService().deleteFaroProjectUsage(faroProjectUsageId);
	}

	public static void deleteFaroProjectUsages() {
		getService().deleteFaroProjectUsages();
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroProjectUsageModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroProjectUsageModelImpl</code>.
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

	public static FaroProjectUsage fetchFaroProjectUsage(
		long faroProjectUsageId) {

		return getService().fetchFaroProjectUsage(faroProjectUsageId);
	}

	public static FaroProjectUsage fetchFaroProjectUsage(
		long faroProjectId, java.util.Date usageDate) {

		return getService().fetchFaroProjectUsage(faroProjectId, usageDate);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the faro project usage with the primary key.
	 *
	 * @param faroProjectUsageId the primary key of the faro project usage
	 * @return the faro project usage
	 * @throws PortalException if a faro project usage with the primary key could not be found
	 */
	public static FaroProjectUsage getFaroProjectUsage(long faroProjectUsageId)
		throws PortalException {

		return getService().getFaroProjectUsage(faroProjectUsageId);
	}

	/**
	 * Returns a range of all the faro project usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroProjectUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of faro project usages
	 * @param end the upper bound of the range of faro project usages (not inclusive)
	 * @return the range of faro project usages
	 */
	public static List<FaroProjectUsage> getFaroProjectUsages(
		int start, int end) {

		return getService().getFaroProjectUsages(start, end);
	}

	/**
	 * Returns the number of faro project usages.
	 *
	 * @return the number of faro project usages
	 */
	public static int getFaroProjectUsagesCount() {
		return getService().getFaroProjectUsagesCount();
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
	 * Updates the faro project usage in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FaroProjectUsageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param faroProjectUsage the faro project usage
	 * @return the faro project usage that was updated
	 */
	public static FaroProjectUsage updateFaroProjectUsage(
		FaroProjectUsage faroProjectUsage) {

		return getService().updateFaroProjectUsage(faroProjectUsage);
	}

	public static FaroProjectUsage updateFaroProjectUsage(
			long faroProjectUsageId, long knownIndividualsCount,
			long pageViewsCount)
		throws PortalException {

		return getService().updateFaroProjectUsage(
			faroProjectUsageId, knownIndividualsCount, pageViewsCount);
	}

	public static FaroProjectUsageLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<FaroProjectUsageLocalService>
		_serviceSnapshot = new Snapshot<>(
			FaroProjectUsageLocalServiceUtil.class,
			FaroProjectUsageLocalService.class);

}
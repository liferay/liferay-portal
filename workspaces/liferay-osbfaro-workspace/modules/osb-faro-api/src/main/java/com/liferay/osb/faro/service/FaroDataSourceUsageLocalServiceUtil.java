/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service;

import com.liferay.osb.faro.model.FaroDataSourceUsage;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for FaroDataSourceUsage. This utility wraps
 * <code>com.liferay.osb.faro.service.impl.FaroDataSourceUsageLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Matthew Kong
 * @see FaroDataSourceUsageLocalService
 * @generated
 */
public class FaroDataSourceUsageLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.osb.faro.service.impl.FaroDataSourceUsageLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the faro data source usage to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FaroDataSourceUsageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param faroDataSourceUsage the faro data source usage
	 * @return the faro data source usage that was added
	 */
	public static FaroDataSourceUsage addFaroDataSourceUsage(
		FaroDataSourceUsage faroDataSourceUsage) {

		return getService().addFaroDataSourceUsage(faroDataSourceUsage);
	}

	public static FaroDataSourceUsage addFaroDataSourceUsage(
			long userId, long billableEventsCount, long dataSourceId,
			String dataSourceName, String dataSourceStatus, long faroProjectId,
			long knownIndividualsCount, java.util.Date usageDate)
		throws PortalException {

		return getService().addFaroDataSourceUsage(
			userId, billableEventsCount, dataSourceId, dataSourceName,
			dataSourceStatus, faroProjectId, knownIndividualsCount, usageDate);
	}

	public static FaroDataSourceUsage addOrUpdateFaroDataSourceUsage(
			long userId, long billableEventsCount, long dataSourceId,
			String dataSourceName, String dataSourceStatus, long faroProjectId,
			long knownIndividualsCount, java.util.Date usageDate)
		throws PortalException {

		return getService().addOrUpdateFaroDataSourceUsage(
			userId, billableEventsCount, dataSourceId, dataSourceName,
			dataSourceStatus, faroProjectId, knownIndividualsCount, usageDate);
	}

	/**
	 * Creates a new faro data source usage with the primary key. Does not add the faro data source usage to the database.
	 *
	 * @param faroDataSourceUsageId the primary key for the new faro data source usage
	 * @return the new faro data source usage
	 */
	public static FaroDataSourceUsage createFaroDataSourceUsage(
		long faroDataSourceUsageId) {

		return getService().createFaroDataSourceUsage(faroDataSourceUsageId);
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
	 * Deletes the faro data source usage from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FaroDataSourceUsageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param faroDataSourceUsage the faro data source usage
	 * @return the faro data source usage that was removed
	 */
	public static FaroDataSourceUsage deleteFaroDataSourceUsage(
		FaroDataSourceUsage faroDataSourceUsage) {

		return getService().deleteFaroDataSourceUsage(faroDataSourceUsage);
	}

	/**
	 * Deletes the faro data source usage with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FaroDataSourceUsageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param faroDataSourceUsageId the primary key of the faro data source usage
	 * @return the faro data source usage that was removed
	 * @throws PortalException if a faro data source usage with the primary key could not be found
	 */
	public static FaroDataSourceUsage deleteFaroDataSourceUsage(
			long faroDataSourceUsageId)
		throws PortalException {

		return getService().deleteFaroDataSourceUsage(faroDataSourceUsageId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroDataSourceUsageModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroDataSourceUsageModelImpl</code>.
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

	public static FaroDataSourceUsage fetchFaroDataSourceUsage(
		long faroDataSourceUsageId) {

		return getService().fetchFaroDataSourceUsage(faroDataSourceUsageId);
	}

	public static FaroDataSourceUsage fetchFaroDataSourceUsage(
		long dataSourceId, long faroProjectId, java.util.Date usageDate) {

		return getService().fetchFaroDataSourceUsage(
			dataSourceId, faroProjectId, usageDate);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the faro data source usage with the primary key.
	 *
	 * @param faroDataSourceUsageId the primary key of the faro data source usage
	 * @return the faro data source usage
	 * @throws PortalException if a faro data source usage with the primary key could not be found
	 */
	public static FaroDataSourceUsage getFaroDataSourceUsage(
			long faroDataSourceUsageId)
		throws PortalException {

		return getService().getFaroDataSourceUsage(faroDataSourceUsageId);
	}

	/**
	 * Returns a range of all the faro data source usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroDataSourceUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of faro data source usages
	 * @param end the upper bound of the range of faro data source usages (not inclusive)
	 * @return the range of faro data source usages
	 */
	public static List<FaroDataSourceUsage> getFaroDataSourceUsages(
		int start, int end) {

		return getService().getFaroDataSourceUsages(start, end);
	}

	public static List<FaroDataSourceUsage> getFaroDataSourceUsages(
		long faroProjectId, java.util.Date startDate, java.util.Date endDate) {

		return getService().getFaroDataSourceUsages(
			faroProjectId, startDate, endDate);
	}

	/**
	 * Returns the number of faro data source usages.
	 *
	 * @return the number of faro data source usages
	 */
	public static int getFaroDataSourceUsagesCount() {
		return getService().getFaroDataSourceUsagesCount();
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
	 * Updates the faro data source usage in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FaroDataSourceUsageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param faroDataSourceUsage the faro data source usage
	 * @return the faro data source usage that was updated
	 */
	public static FaroDataSourceUsage updateFaroDataSourceUsage(
		FaroDataSourceUsage faroDataSourceUsage) {

		return getService().updateFaroDataSourceUsage(faroDataSourceUsage);
	}

	public static FaroDataSourceUsage updateFaroDataSourceUsage(
			long billableEventsCount, String dataSourceName,
			String dataSourceStatus, long faroDataSourceUsageId,
			long knownIndividualsCount)
		throws PortalException {

		return getService().updateFaroDataSourceUsage(
			billableEventsCount, dataSourceName, dataSourceStatus,
			faroDataSourceUsageId, knownIndividualsCount);
	}

	public static FaroDataSourceUsageLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<FaroDataSourceUsageLocalService>
		_serviceSnapshot = new Snapshot<>(
			FaroDataSourceUsageLocalServiceUtil.class,
			FaroDataSourceUsageLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1926927414
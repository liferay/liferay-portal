/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link FaroDataSourceUsageLocalService}.
 *
 * @author Matthew Kong
 * @see FaroDataSourceUsageLocalService
 * @generated
 */
public class FaroDataSourceUsageLocalServiceWrapper
	implements FaroDataSourceUsageLocalService,
			   ServiceWrapper<FaroDataSourceUsageLocalService> {

	public FaroDataSourceUsageLocalServiceWrapper() {
		this(null);
	}

	public FaroDataSourceUsageLocalServiceWrapper(
		FaroDataSourceUsageLocalService faroDataSourceUsageLocalService) {

		_faroDataSourceUsageLocalService = faroDataSourceUsageLocalService;
	}

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
	@Override
	public com.liferay.osb.faro.model.FaroDataSourceUsage
		addFaroDataSourceUsage(
			com.liferay.osb.faro.model.FaroDataSourceUsage
				faroDataSourceUsage) {

		return _faroDataSourceUsageLocalService.addFaroDataSourceUsage(
			faroDataSourceUsage);
	}

	@Override
	public com.liferay.osb.faro.model.FaroDataSourceUsage
			addFaroDataSourceUsage(
				long userId, long billableEventsCount, long dataSourceId,
				String dataSourceName, String dataSourceStatus,
				long faroProjectId, long knownIndividualsCount,
				java.util.Date usageDate)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroDataSourceUsageLocalService.addFaroDataSourceUsage(
			userId, billableEventsCount, dataSourceId, dataSourceName,
			dataSourceStatus, faroProjectId, knownIndividualsCount, usageDate);
	}

	@Override
	public com.liferay.osb.faro.model.FaroDataSourceUsage
			addOrUpdateFaroDataSourceUsage(
				long userId, long billableEventsCount, long dataSourceId,
				String dataSourceName, String dataSourceStatus,
				long faroProjectId, long knownIndividualsCount,
				java.util.Date usageDate)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroDataSourceUsageLocalService.addOrUpdateFaroDataSourceUsage(
			userId, billableEventsCount, dataSourceId, dataSourceName,
			dataSourceStatus, faroProjectId, knownIndividualsCount, usageDate);
	}

	/**
	 * Creates a new faro data source usage with the primary key. Does not add the faro data source usage to the database.
	 *
	 * @param faroDataSourceUsageId the primary key for the new faro data source usage
	 * @return the new faro data source usage
	 */
	@Override
	public com.liferay.osb.faro.model.FaroDataSourceUsage
		createFaroDataSourceUsage(long faroDataSourceUsageId) {

		return _faroDataSourceUsageLocalService.createFaroDataSourceUsage(
			faroDataSourceUsageId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroDataSourceUsageLocalService.createPersistedModel(
			primaryKeyObj);
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
	@Override
	public com.liferay.osb.faro.model.FaroDataSourceUsage
		deleteFaroDataSourceUsage(
			com.liferay.osb.faro.model.FaroDataSourceUsage
				faroDataSourceUsage) {

		return _faroDataSourceUsageLocalService.deleteFaroDataSourceUsage(
			faroDataSourceUsage);
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
	@Override
	public com.liferay.osb.faro.model.FaroDataSourceUsage
			deleteFaroDataSourceUsage(long faroDataSourceUsageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroDataSourceUsageLocalService.deleteFaroDataSourceUsage(
			faroDataSourceUsageId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroDataSourceUsageLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _faroDataSourceUsageLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _faroDataSourceUsageLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _faroDataSourceUsageLocalService.dynamicQuery();
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

		return _faroDataSourceUsageLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _faroDataSourceUsageLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _faroDataSourceUsageLocalService.dynamicQuery(
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

		return _faroDataSourceUsageLocalService.dynamicQueryCount(dynamicQuery);
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

		return _faroDataSourceUsageLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.osb.faro.model.FaroDataSourceUsage
		fetchFaroDataSourceUsage(long faroDataSourceUsageId) {

		return _faroDataSourceUsageLocalService.fetchFaroDataSourceUsage(
			faroDataSourceUsageId);
	}

	@Override
	public com.liferay.osb.faro.model.FaroDataSourceUsage
		fetchFaroDataSourceUsage(
			long dataSourceId, long faroProjectId, java.util.Date usageDate) {

		return _faroDataSourceUsageLocalService.fetchFaroDataSourceUsage(
			dataSourceId, faroProjectId, usageDate);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _faroDataSourceUsageLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the faro data source usage with the primary key.
	 *
	 * @param faroDataSourceUsageId the primary key of the faro data source usage
	 * @return the faro data source usage
	 * @throws PortalException if a faro data source usage with the primary key could not be found
	 */
	@Override
	public com.liferay.osb.faro.model.FaroDataSourceUsage
			getFaroDataSourceUsage(long faroDataSourceUsageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroDataSourceUsageLocalService.getFaroDataSourceUsage(
			faroDataSourceUsageId);
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
	@Override
	public java.util.List<com.liferay.osb.faro.model.FaroDataSourceUsage>
		getFaroDataSourceUsages(int start, int end) {

		return _faroDataSourceUsageLocalService.getFaroDataSourceUsages(
			start, end);
	}

	@Override
	public java.util.List<com.liferay.osb.faro.model.FaroDataSourceUsage>
		getFaroDataSourceUsages(
			long faroProjectId, java.util.Date startDate,
			java.util.Date endDate) {

		return _faroDataSourceUsageLocalService.getFaroDataSourceUsages(
			faroProjectId, startDate, endDate);
	}

	/**
	 * Returns the number of faro data source usages.
	 *
	 * @return the number of faro data source usages
	 */
	@Override
	public int getFaroDataSourceUsagesCount() {
		return _faroDataSourceUsageLocalService.getFaroDataSourceUsagesCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _faroDataSourceUsageLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _faroDataSourceUsageLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroDataSourceUsageLocalService.getPersistedModel(
			primaryKeyObj);
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
	@Override
	public com.liferay.osb.faro.model.FaroDataSourceUsage
		updateFaroDataSourceUsage(
			com.liferay.osb.faro.model.FaroDataSourceUsage
				faroDataSourceUsage) {

		return _faroDataSourceUsageLocalService.updateFaroDataSourceUsage(
			faroDataSourceUsage);
	}

	@Override
	public com.liferay.osb.faro.model.FaroDataSourceUsage
			updateFaroDataSourceUsage(
				long billableEventsCount, String dataSourceName,
				String dataSourceStatus, long faroDataSourceUsageId,
				long knownIndividualsCount)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroDataSourceUsageLocalService.updateFaroDataSourceUsage(
			billableEventsCount, dataSourceName, dataSourceStatus,
			faroDataSourceUsageId, knownIndividualsCount);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _faroDataSourceUsageLocalService.getBasePersistence();
	}

	@Override
	public FaroDataSourceUsageLocalService getWrappedService() {
		return _faroDataSourceUsageLocalService;
	}

	@Override
	public void setWrappedService(
		FaroDataSourceUsageLocalService faroDataSourceUsageLocalService) {

		_faroDataSourceUsageLocalService = faroDataSourceUsageLocalService;
	}

	private FaroDataSourceUsageLocalService _faroDataSourceUsageLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1483387040
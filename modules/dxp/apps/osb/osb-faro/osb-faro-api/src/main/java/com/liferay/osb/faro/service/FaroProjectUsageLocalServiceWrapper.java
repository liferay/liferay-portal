/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link FaroProjectUsageLocalService}.
 *
 * @author Matthew Kong
 * @see FaroProjectUsageLocalService
 * @generated
 */
public class FaroProjectUsageLocalServiceWrapper
	implements FaroProjectUsageLocalService,
			   ServiceWrapper<FaroProjectUsageLocalService> {

	public FaroProjectUsageLocalServiceWrapper() {
		this(null);
	}

	public FaroProjectUsageLocalServiceWrapper(
		FaroProjectUsageLocalService faroProjectUsageLocalService) {

		_faroProjectUsageLocalService = faroProjectUsageLocalService;
	}

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
	@Override
	public com.liferay.osb.faro.model.FaroProjectUsage addFaroProjectUsage(
		com.liferay.osb.faro.model.FaroProjectUsage faroProjectUsage) {

		return _faroProjectUsageLocalService.addFaroProjectUsage(
			faroProjectUsage);
	}

	@Override
	public com.liferay.osb.faro.model.FaroProjectUsage addFaroProjectUsage(
		long companyId, long userId, long faroProjectId,
		long knownIndividualsCount, String monthDateKey, long pageViewsCount,
		java.util.Date usageDate) {

		return _faroProjectUsageLocalService.addFaroProjectUsage(
			companyId, userId, faroProjectId, knownIndividualsCount,
			monthDateKey, pageViewsCount, usageDate);
	}

	/**
	 * Creates a new faro project usage with the primary key. Does not add the faro project usage to the database.
	 *
	 * @param faroProjectUsageId the primary key for the new faro project usage
	 * @return the new faro project usage
	 */
	@Override
	public com.liferay.osb.faro.model.FaroProjectUsage createFaroProjectUsage(
		long faroProjectUsageId) {

		return _faroProjectUsageLocalService.createFaroProjectUsage(
			faroProjectUsageId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroProjectUsageLocalService.createPersistedModel(
			primaryKeyObj);
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
	@Override
	public com.liferay.osb.faro.model.FaroProjectUsage deleteFaroProjectUsage(
		com.liferay.osb.faro.model.FaroProjectUsage faroProjectUsage) {

		return _faroProjectUsageLocalService.deleteFaroProjectUsage(
			faroProjectUsage);
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
	@Override
	public com.liferay.osb.faro.model.FaroProjectUsage deleteFaroProjectUsage(
			long faroProjectUsageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroProjectUsageLocalService.deleteFaroProjectUsage(
			faroProjectUsageId);
	}

	@Override
	public void deleteFaroProjectUsages() {
		_faroProjectUsageLocalService.deleteFaroProjectUsages();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroProjectUsageLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _faroProjectUsageLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _faroProjectUsageLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _faroProjectUsageLocalService.dynamicQuery();
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

		return _faroProjectUsageLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _faroProjectUsageLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _faroProjectUsageLocalService.dynamicQuery(
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

		return _faroProjectUsageLocalService.dynamicQueryCount(dynamicQuery);
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

		return _faroProjectUsageLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.osb.faro.model.FaroProjectUsage fetchFaroProjectUsage(
		long faroProjectUsageId) {

		return _faroProjectUsageLocalService.fetchFaroProjectUsage(
			faroProjectUsageId);
	}

	@Override
	public com.liferay.osb.faro.model.FaroProjectUsage fetchFaroProjectUsage(
		long faroProjectId, java.util.Date usageDate) {

		return _faroProjectUsageLocalService.fetchFaroProjectUsage(
			faroProjectId, usageDate);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _faroProjectUsageLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the faro project usage with the primary key.
	 *
	 * @param faroProjectUsageId the primary key of the faro project usage
	 * @return the faro project usage
	 * @throws PortalException if a faro project usage with the primary key could not be found
	 */
	@Override
	public com.liferay.osb.faro.model.FaroProjectUsage getFaroProjectUsage(
			long faroProjectUsageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroProjectUsageLocalService.getFaroProjectUsage(
			faroProjectUsageId);
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
	@Override
	public java.util.List<com.liferay.osb.faro.model.FaroProjectUsage>
		getFaroProjectUsages(int start, int end) {

		return _faroProjectUsageLocalService.getFaroProjectUsages(start, end);
	}

	/**
	 * Returns the number of faro project usages.
	 *
	 * @return the number of faro project usages
	 */
	@Override
	public int getFaroProjectUsagesCount() {
		return _faroProjectUsageLocalService.getFaroProjectUsagesCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _faroProjectUsageLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _faroProjectUsageLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroProjectUsageLocalService.getPersistedModel(primaryKeyObj);
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
	@Override
	public com.liferay.osb.faro.model.FaroProjectUsage updateFaroProjectUsage(
		com.liferay.osb.faro.model.FaroProjectUsage faroProjectUsage) {

		return _faroProjectUsageLocalService.updateFaroProjectUsage(
			faroProjectUsage);
	}

	@Override
	public com.liferay.osb.faro.model.FaroProjectUsage updateFaroProjectUsage(
			long faroProjectUsageId, long knownIndividualsCount,
			long pageViewsCount)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroProjectUsageLocalService.updateFaroProjectUsage(
			faroProjectUsageId, knownIndividualsCount, pageViewsCount);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _faroProjectUsageLocalService.getBasePersistence();
	}

	@Override
	public FaroProjectUsageLocalService getWrappedService() {
		return _faroProjectUsageLocalService;
	}

	@Override
	public void setWrappedService(
		FaroProjectUsageLocalService faroProjectUsageLocalService) {

		_faroProjectUsageLocalService = faroProjectUsageLocalService;
	}

	private FaroProjectUsageLocalService _faroProjectUsageLocalService;

}
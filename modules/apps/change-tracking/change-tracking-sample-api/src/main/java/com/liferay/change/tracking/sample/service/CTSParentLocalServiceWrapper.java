/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.service;

import com.liferay.change.tracking.sample.model.CTSParent;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CTSParentLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see CTSParentLocalService
 * @generated
 */
public class CTSParentLocalServiceWrapper
	implements CTSParentLocalService, ServiceWrapper<CTSParentLocalService> {

	public CTSParentLocalServiceWrapper() {
		this(null);
	}

	public CTSParentLocalServiceWrapper(
		CTSParentLocalService ctsParentLocalService) {

		_ctsParentLocalService = ctsParentLocalService;
	}

	/**
	 * Adds the cts parent to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTSParentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctsParent the cts parent
	 * @return the cts parent that was added
	 */
	@Override
	public CTSParent addCTSParent(CTSParent ctsParent) {
		return _ctsParentLocalService.addCTSParent(ctsParent);
	}

	@Override
	public CTSParent addCTSParent(long companyId, long ctsGrandParentId) {
		return _ctsParentLocalService.addCTSParent(companyId, ctsGrandParentId);
	}

	/**
	 * Creates a new cts parent with the primary key. Does not add the cts parent to the database.
	 *
	 * @param ctsParentId the primary key for the new cts parent
	 * @return the new cts parent
	 */
	@Override
	public CTSParent createCTSParent(long ctsParentId) {
		return _ctsParentLocalService.createCTSParent(ctsParentId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctsParentLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the cts parent from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTSParentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctsParent the cts parent
	 * @return the cts parent that was removed
	 */
	@Override
	public CTSParent deleteCTSParent(CTSParent ctsParent) {
		return _ctsParentLocalService.deleteCTSParent(ctsParent);
	}

	/**
	 * Deletes the cts parent with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTSParentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctsParentId the primary key of the cts parent
	 * @return the cts parent that was removed
	 * @throws PortalException if a cts parent with the primary key could not be found
	 */
	@Override
	public CTSParent deleteCTSParent(long ctsParentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctsParentLocalService.deleteCTSParent(ctsParentId);
	}

	@Override
	public void deleteCTSParents(long companyId) {
		_ctsParentLocalService.deleteCTSParents(companyId);
	}

	@Override
	public void deleteCTSParentsByCTSGrandParentId(
		long companyId, long ctsGrandParentId) {

		_ctsParentLocalService.deleteCTSParentsByCTSGrandParentId(
			companyId, ctsGrandParentId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctsParentLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _ctsParentLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _ctsParentLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _ctsParentLocalService.dynamicQuery();
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

		return _ctsParentLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.sample.model.impl.CTSParentModelImpl</code>.
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

		return _ctsParentLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.sample.model.impl.CTSParentModelImpl</code>.
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

		return _ctsParentLocalService.dynamicQuery(
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

		return _ctsParentLocalService.dynamicQueryCount(dynamicQuery);
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

		return _ctsParentLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CTSParent fetchCTSParent(long ctsParentId) {
		return _ctsParentLocalService.fetchCTSParent(ctsParentId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _ctsParentLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the cts parent with the primary key.
	 *
	 * @param ctsParentId the primary key of the cts parent
	 * @return the cts parent
	 * @throws PortalException if a cts parent with the primary key could not be found
	 */
	@Override
	public CTSParent getCTSParent(long ctsParentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctsParentLocalService.getCTSParent(ctsParentId);
	}

	/**
	 * Returns a range of all the cts parents.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.sample.model.impl.CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @return the range of cts parents
	 */
	@Override
	public java.util.List<CTSParent> getCTSParents(int start, int end) {
		return _ctsParentLocalService.getCTSParents(start, end);
	}

	@Override
	public java.util.List<CTSParent> getCTSParents(long companyId) {
		return _ctsParentLocalService.getCTSParents(companyId);
	}

	/**
	 * Returns the number of cts parents.
	 *
	 * @return the number of cts parents
	 */
	@Override
	public int getCTSParentsCount() {
		return _ctsParentLocalService.getCTSParentsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _ctsParentLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ctsParentLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctsParentLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the cts parent in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTSParentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctsParent the cts parent
	 * @return the cts parent that was updated
	 */
	@Override
	public CTSParent updateCTSParent(CTSParent ctsParent) {
		return _ctsParentLocalService.updateCTSParent(ctsParent);
	}

	@Override
	public CTSParent updateCTSParent(long ctsParentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctsParentLocalService.updateCTSParent(ctsParentId);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ctsParentLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CTSParent> getCTPersistence() {
		return _ctsParentLocalService.getCTPersistence();
	}

	@Override
	public Class<CTSParent> getModelClass() {
		return _ctsParentLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CTSParent>, R, E> updateUnsafeFunction)
		throws E {

		return _ctsParentLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CTSParentLocalService getWrappedService() {
		return _ctsParentLocalService;
	}

	@Override
	public void setWrappedService(CTSParentLocalService ctsParentLocalService) {
		_ctsParentLocalService = ctsParentLocalService;
	}

	private CTSParentLocalService _ctsParentLocalService;

}
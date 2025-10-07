/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CTSGrandParentLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see CTSGrandParentLocalService
 * @generated
 */
public class CTSGrandParentLocalServiceWrapper
	implements CTSGrandParentLocalService,
			   ServiceWrapper<CTSGrandParentLocalService> {

	public CTSGrandParentLocalServiceWrapper() {
		this(null);
	}

	public CTSGrandParentLocalServiceWrapper(
		CTSGrandParentLocalService ctsGrandParentLocalService) {

		_ctsGrandParentLocalService = ctsGrandParentLocalService;
	}

	/**
	 * Adds the cts grand parent to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTSGrandParentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctsGrandParent the cts grand parent
	 * @return the cts grand parent that was added
	 */
	@Override
	public com.liferay.change.tracking.sample.model.CTSGrandParent
		addCTSGrandParent(
			com.liferay.change.tracking.sample.model.CTSGrandParent
				ctsGrandParent) {

		return _ctsGrandParentLocalService.addCTSGrandParent(ctsGrandParent);
	}

	@Override
	public com.liferay.change.tracking.sample.model.CTSGrandParent
		addCTSGrandParent(long companyId) {

		return _ctsGrandParentLocalService.addCTSGrandParent(companyId);
	}

	@Override
	public com.liferay.change.tracking.sample.model.CTSGrandParent
		addCTSGrandParent(long companyId, long parentCTSGrandParentId) {

		return _ctsGrandParentLocalService.addCTSGrandParent(
			companyId, parentCTSGrandParentId);
	}

	/**
	 * Creates a new cts grand parent with the primary key. Does not add the cts grand parent to the database.
	 *
	 * @param ctsGrandParentId the primary key for the new cts grand parent
	 * @return the new cts grand parent
	 */
	@Override
	public com.liferay.change.tracking.sample.model.CTSGrandParent
		createCTSGrandParent(long ctsGrandParentId) {

		return _ctsGrandParentLocalService.createCTSGrandParent(
			ctsGrandParentId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctsGrandParentLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the cts grand parent from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTSGrandParentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctsGrandParent the cts grand parent
	 * @return the cts grand parent that was removed
	 */
	@Override
	public com.liferay.change.tracking.sample.model.CTSGrandParent
		deleteCTSGrandParent(
			com.liferay.change.tracking.sample.model.CTSGrandParent
				ctsGrandParent) {

		return _ctsGrandParentLocalService.deleteCTSGrandParent(ctsGrandParent);
	}

	/**
	 * Deletes the cts grand parent with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTSGrandParentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctsGrandParentId the primary key of the cts grand parent
	 * @return the cts grand parent that was removed
	 * @throws PortalException if a cts grand parent with the primary key could not be found
	 */
	@Override
	public com.liferay.change.tracking.sample.model.CTSGrandParent
			deleteCTSGrandParent(long ctsGrandParentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctsGrandParentLocalService.deleteCTSGrandParent(
			ctsGrandParentId);
	}

	@Override
	public void deleteCTSGrandParents(long companyId) {
		_ctsGrandParentLocalService.deleteCTSGrandParents(companyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctsGrandParentLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _ctsGrandParentLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _ctsGrandParentLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _ctsGrandParentLocalService.dynamicQuery();
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

		return _ctsGrandParentLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.sample.model.impl.CTSGrandParentModelImpl</code>.
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

		return _ctsGrandParentLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.sample.model.impl.CTSGrandParentModelImpl</code>.
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

		return _ctsGrandParentLocalService.dynamicQuery(
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

		return _ctsGrandParentLocalService.dynamicQueryCount(dynamicQuery);
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

		return _ctsGrandParentLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.change.tracking.sample.model.CTSGrandParent
		fetchCTSGrandParent(long ctsGrandParentId) {

		return _ctsGrandParentLocalService.fetchCTSGrandParent(
			ctsGrandParentId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _ctsGrandParentLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the cts grand parent with the primary key.
	 *
	 * @param ctsGrandParentId the primary key of the cts grand parent
	 * @return the cts grand parent
	 * @throws PortalException if a cts grand parent with the primary key could not be found
	 */
	@Override
	public com.liferay.change.tracking.sample.model.CTSGrandParent
			getCTSGrandParent(long ctsGrandParentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctsGrandParentLocalService.getCTSGrandParent(ctsGrandParentId);
	}

	/**
	 * Returns a range of all the cts grand parents.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.sample.model.impl.CTSGrandParentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts grand parents
	 * @param end the upper bound of the range of cts grand parents (not inclusive)
	 * @return the range of cts grand parents
	 */
	@Override
	public java.util.List
		<com.liferay.change.tracking.sample.model.CTSGrandParent>
			getCTSGrandParents(int start, int end) {

		return _ctsGrandParentLocalService.getCTSGrandParents(start, end);
	}

	@Override
	public java.util.List
		<com.liferay.change.tracking.sample.model.CTSGrandParent>
			getCTSGrandParents(long companyId) {

		return _ctsGrandParentLocalService.getCTSGrandParents(companyId);
	}

	/**
	 * Returns the number of cts grand parents.
	 *
	 * @return the number of cts grand parents
	 */
	@Override
	public int getCTSGrandParentsCount() {
		return _ctsGrandParentLocalService.getCTSGrandParentsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _ctsGrandParentLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ctsGrandParentLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctsGrandParentLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the cts grand parent in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTSGrandParentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctsGrandParent the cts grand parent
	 * @return the cts grand parent that was updated
	 */
	@Override
	public com.liferay.change.tracking.sample.model.CTSGrandParent
		updateCTSGrandParent(
			com.liferay.change.tracking.sample.model.CTSGrandParent
				ctsGrandParent) {

		return _ctsGrandParentLocalService.updateCTSGrandParent(ctsGrandParent);
	}

	@Override
	public com.liferay.change.tracking.sample.model.CTSGrandParent
			updateCTSGrandParent(long ctsCTSGrandParentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctsGrandParentLocalService.updateCTSGrandParent(
			ctsCTSGrandParentId);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ctsGrandParentLocalService.getBasePersistence();
	}

	@Override
	public CTSGrandParentLocalService getWrappedService() {
		return _ctsGrandParentLocalService;
	}

	@Override
	public void setWrappedService(
		CTSGrandParentLocalService ctsGrandParentLocalService) {

		_ctsGrandParentLocalService = ctsGrandParentLocalService;
	}

	private CTSGrandParentLocalService _ctsGrandParentLocalService;

}
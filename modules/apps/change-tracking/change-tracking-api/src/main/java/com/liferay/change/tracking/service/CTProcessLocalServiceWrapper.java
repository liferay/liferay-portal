/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CTProcessLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see CTProcessLocalService
 * @generated
 */
public class CTProcessLocalServiceWrapper
	implements CTProcessLocalService, ServiceWrapper<CTProcessLocalService> {

	public CTProcessLocalServiceWrapper() {
		this(null);
	}

	public CTProcessLocalServiceWrapper(
		CTProcessLocalService ctProcessLocalService) {

		_ctProcessLocalService = ctProcessLocalService;
	}

	/**
	 * Adds the ct process to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTProcessLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctProcess the ct process
	 * @return the ct process that was added
	 */
	@Override
	public com.liferay.change.tracking.model.CTProcess addCTProcess(
		com.liferay.change.tracking.model.CTProcess ctProcess) {

		return _ctProcessLocalService.addCTProcess(ctProcess);
	}

	@Override
	public com.liferay.change.tracking.model.CTProcess addCTProcess(
			long userId, long ctCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctProcessLocalService.addCTProcess(userId, ctCollectionId);
	}

	/**
	 * Creates a new ct process with the primary key. Does not add the ct process to the database.
	 *
	 * @param ctProcessId the primary key for the new ct process
	 * @return the new ct process
	 */
	@Override
	public com.liferay.change.tracking.model.CTProcess createCTProcess(
		long ctProcessId) {

		return _ctProcessLocalService.createCTProcess(ctProcessId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctProcessLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the ct process from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTProcessLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctProcess the ct process
	 * @return the ct process that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.change.tracking.model.CTProcess deleteCTProcess(
			com.liferay.change.tracking.model.CTProcess ctProcess)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctProcessLocalService.deleteCTProcess(ctProcess);
	}

	/**
	 * Deletes the ct process with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTProcessLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctProcessId the primary key of the ct process
	 * @return the ct process that was removed
	 * @throws PortalException if a ct process with the primary key could not be found
	 */
	@Override
	public com.liferay.change.tracking.model.CTProcess deleteCTProcess(
			long ctProcessId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctProcessLocalService.deleteCTProcess(ctProcessId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctProcessLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _ctProcessLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _ctProcessLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _ctProcessLocalService.dynamicQuery();
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

		return _ctProcessLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTProcessModelImpl</code>.
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

		return _ctProcessLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTProcessModelImpl</code>.
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

		return _ctProcessLocalService.dynamicQuery(
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

		return _ctProcessLocalService.dynamicQueryCount(dynamicQuery);
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

		return _ctProcessLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.change.tracking.model.CTProcess fetchCTProcess(
		long ctProcessId) {

		return _ctProcessLocalService.fetchCTProcess(ctProcessId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _ctProcessLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the ct process with the primary key.
	 *
	 * @param ctProcessId the primary key of the ct process
	 * @return the ct process
	 * @throws PortalException if a ct process with the primary key could not be found
	 */
	@Override
	public com.liferay.change.tracking.model.CTProcess getCTProcess(
			long ctProcessId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctProcessLocalService.getCTProcess(ctProcessId);
	}

	/**
	 * Returns a range of all the ct processes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTProcessModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ct processes
	 * @param end the upper bound of the range of ct processes (not inclusive)
	 * @return the range of ct processes
	 */
	@Override
	public java.util.List<com.liferay.change.tracking.model.CTProcess>
		getCTProcesses(int start, int end) {

		return _ctProcessLocalService.getCTProcesses(start, end);
	}

	@Override
	public java.util.List<com.liferay.change.tracking.model.CTProcess>
		getCTProcesses(long ctCollectionId) {

		return _ctProcessLocalService.getCTProcesses(ctCollectionId);
	}

	/**
	 * Returns the number of ct processes.
	 *
	 * @return the number of ct processes
	 */
	@Override
	public int getCTProcessesCount() {
		return _ctProcessLocalService.getCTProcessesCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _ctProcessLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ctProcessLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctProcessLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the ct process in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTProcessLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctProcess the ct process
	 * @return the ct process that was updated
	 */
	@Override
	public com.liferay.change.tracking.model.CTProcess updateCTProcess(
		com.liferay.change.tracking.model.CTProcess ctProcess) {

		return _ctProcessLocalService.updateCTProcess(ctProcess);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ctProcessLocalService.getBasePersistence();
	}

	@Override
	public CTProcessLocalService getWrappedService() {
		return _ctProcessLocalService;
	}

	@Override
	public void setWrappedService(CTProcessLocalService ctProcessLocalService) {
		_ctProcessLocalService = ctProcessLocalService;
	}

	private CTProcessLocalService _ctProcessLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-27994768
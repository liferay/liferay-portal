/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link PatcherProductVersionLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherProductVersionLocalService
 * @generated
 */
public class PatcherProductVersionLocalServiceWrapper
	implements PatcherProductVersionLocalService,
			   ServiceWrapper<PatcherProductVersionLocalService> {

	public PatcherProductVersionLocalServiceWrapper() {
		this(null);
	}

	public PatcherProductVersionLocalServiceWrapper(
		PatcherProductVersionLocalService patcherProductVersionLocalService) {

		_patcherProductVersionLocalService = patcherProductVersionLocalService;
	}

	/**
	 * Adds the patcher product version to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherProductVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherProductVersion the patcher product version
	 * @return the patcher product version that was added
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherProductVersion
		addPatcherProductVersion(
			com.liferay.osb.patcher.model.PatcherProductVersion
				patcherProductVersion) {

		return _patcherProductVersionLocalService.addPatcherProductVersion(
			patcherProductVersion);
	}

	/**
	 * Creates a new patcher product version with the primary key. Does not add the patcher product version to the database.
	 *
	 * @param patcherProductVersionId the primary key for the new patcher product version
	 * @return the new patcher product version
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherProductVersion
		createPatcherProductVersion(long patcherProductVersionId) {

		return _patcherProductVersionLocalService.createPatcherProductVersion(
			patcherProductVersionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherProductVersionLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the patcher product version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherProductVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherProductVersionId the primary key of the patcher product version
	 * @return the patcher product version that was removed
	 * @throws PortalException if a patcher product version with the primary key could not be found
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherProductVersion
			deletePatcherProductVersion(long patcherProductVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherProductVersionLocalService.deletePatcherProductVersion(
			patcherProductVersionId);
	}

	/**
	 * Deletes the patcher product version from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherProductVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherProductVersion the patcher product version
	 * @return the patcher product version that was removed
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherProductVersion
		deletePatcherProductVersion(
			com.liferay.osb.patcher.model.PatcherProductVersion
				patcherProductVersion) {

		return _patcherProductVersionLocalService.deletePatcherProductVersion(
			patcherProductVersion);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherProductVersionLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _patcherProductVersionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _patcherProductVersionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _patcherProductVersionLocalService.dynamicQuery();
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

		return _patcherProductVersionLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherProductVersionModelImpl</code>.
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

		return _patcherProductVersionLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherProductVersionModelImpl</code>.
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

		return _patcherProductVersionLocalService.dynamicQuery(
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

		return _patcherProductVersionLocalService.dynamicQueryCount(
			dynamicQuery);
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

		return _patcherProductVersionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherProductVersion
		fetchPatcherProductVersion(long patcherProductVersionId) {

		return _patcherProductVersionLocalService.fetchPatcherProductVersion(
			patcherProductVersionId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _patcherProductVersionLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _patcherProductVersionLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _patcherProductVersionLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * Returns the patcher product version with the primary key.
	 *
	 * @param patcherProductVersionId the primary key of the patcher product version
	 * @return the patcher product version
	 * @throws PortalException if a patcher product version with the primary key could not be found
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherProductVersion
			getPatcherProductVersion(long patcherProductVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherProductVersionLocalService.getPatcherProductVersion(
			patcherProductVersionId);
	}

	/**
	 * Returns a range of all the patcher product versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherProductVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher product versions
	 * @param end the upper bound of the range of patcher product versions (not inclusive)
	 * @return the range of patcher product versions
	 */
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherProductVersion>
		getPatcherProductVersions(int start, int end) {

		return _patcherProductVersionLocalService.getPatcherProductVersions(
			start, end);
	}

	/**
	 * Returns the number of patcher product versions.
	 *
	 * @return the number of patcher product versions
	 */
	@Override
	public int getPatcherProductVersionsCount() {
		return _patcherProductVersionLocalService.
			getPatcherProductVersionsCount();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherProductVersionLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the patcher product version in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherProductVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherProductVersion the patcher product version
	 * @return the patcher product version that was updated
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherProductVersion
		updatePatcherProductVersion(
			com.liferay.osb.patcher.model.PatcherProductVersion
				patcherProductVersion) {

		return _patcherProductVersionLocalService.updatePatcherProductVersion(
			patcherProductVersion);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _patcherProductVersionLocalService.getBasePersistence();
	}

	@Override
	public PatcherProductVersionLocalService getWrappedService() {
		return _patcherProductVersionLocalService;
	}

	@Override
	public void setWrappedService(
		PatcherProductVersionLocalService patcherProductVersionLocalService) {

		_patcherProductVersionLocalService = patcherProductVersionLocalService;
	}

	private PatcherProductVersionLocalService
		_patcherProductVersionLocalService;

}
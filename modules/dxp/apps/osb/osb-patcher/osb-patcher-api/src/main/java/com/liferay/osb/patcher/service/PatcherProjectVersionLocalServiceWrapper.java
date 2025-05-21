/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link PatcherProjectVersionLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherProjectVersionLocalService
 * @generated
 */
public class PatcherProjectVersionLocalServiceWrapper
	implements PatcherProjectVersionLocalService,
			   ServiceWrapper<PatcherProjectVersionLocalService> {

	public PatcherProjectVersionLocalServiceWrapper() {
		this(null);
	}

	public PatcherProjectVersionLocalServiceWrapper(
		PatcherProjectVersionLocalService patcherProjectVersionLocalService) {

		_patcherProjectVersionLocalService = patcherProjectVersionLocalService;
	}

	/**
	 * Adds the patcher project version to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherProjectVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherProjectVersion the patcher project version
	 * @return the patcher project version that was added
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherProjectVersion
		addPatcherProjectVersion(
			com.liferay.osb.patcher.model.PatcherProjectVersion
				patcherProjectVersion) {

		return _patcherProjectVersionLocalService.addPatcherProjectVersion(
			patcherProjectVersion);
	}

	/**
	 * Creates a new patcher project version with the primary key. Does not add the patcher project version to the database.
	 *
	 * @param patcherProjectVersionId the primary key for the new patcher project version
	 * @return the new patcher project version
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherProjectVersion
		createPatcherProjectVersion(long patcherProjectVersionId) {

		return _patcherProjectVersionLocalService.createPatcherProjectVersion(
			patcherProjectVersionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherProjectVersionLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the patcher project version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherProjectVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherProjectVersionId the primary key of the patcher project version
	 * @return the patcher project version that was removed
	 * @throws PortalException if a patcher project version with the primary key could not be found
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherProjectVersion
			deletePatcherProjectVersion(long patcherProjectVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherProjectVersionLocalService.deletePatcherProjectVersion(
			patcherProjectVersionId);
	}

	/**
	 * Deletes the patcher project version from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherProjectVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherProjectVersion the patcher project version
	 * @return the patcher project version that was removed
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherProjectVersion
		deletePatcherProjectVersion(
			com.liferay.osb.patcher.model.PatcherProjectVersion
				patcherProjectVersion) {

		return _patcherProjectVersionLocalService.deletePatcherProjectVersion(
			patcherProjectVersion);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherProjectVersionLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _patcherProjectVersionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _patcherProjectVersionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _patcherProjectVersionLocalService.dynamicQuery();
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

		return _patcherProjectVersionLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherProjectVersionModelImpl</code>.
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

		return _patcherProjectVersionLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherProjectVersionModelImpl</code>.
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

		return _patcherProjectVersionLocalService.dynamicQuery(
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

		return _patcherProjectVersionLocalService.dynamicQueryCount(
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

		return _patcherProjectVersionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherProjectVersion
		fetchPatcherProjectVersion(long patcherProjectVersionId) {

		return _patcherProjectVersionLocalService.fetchPatcherProjectVersion(
			patcherProjectVersionId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _patcherProjectVersionLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _patcherProjectVersionLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _patcherProjectVersionLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * Returns the patcher project version with the primary key.
	 *
	 * @param patcherProjectVersionId the primary key of the patcher project version
	 * @return the patcher project version
	 * @throws PortalException if a patcher project version with the primary key could not be found
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherProjectVersion
			getPatcherProjectVersion(long patcherProjectVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherProjectVersionLocalService.getPatcherProjectVersion(
			patcherProjectVersionId);
	}

	/**
	 * Returns a range of all the patcher project versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of patcher project versions
	 */
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherProjectVersion>
		getPatcherProjectVersions(int start, int end) {

		return _patcherProjectVersionLocalService.getPatcherProjectVersions(
			start, end);
	}

	/**
	 * Returns the number of patcher project versions.
	 *
	 * @return the number of patcher project versions
	 */
	@Override
	public int getPatcherProjectVersionsCount() {
		return _patcherProjectVersionLocalService.
			getPatcherProjectVersionsCount();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherProjectVersionLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the patcher project version in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherProjectVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherProjectVersion the patcher project version
	 * @return the patcher project version that was updated
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherProjectVersion
		updatePatcherProjectVersion(
			com.liferay.osb.patcher.model.PatcherProjectVersion
				patcherProjectVersion) {

		return _patcherProjectVersionLocalService.updatePatcherProjectVersion(
			patcherProjectVersion);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _patcherProjectVersionLocalService.getBasePersistence();
	}

	@Override
	public PatcherProjectVersionLocalService getWrappedService() {
		return _patcherProjectVersionLocalService;
	}

	@Override
	public void setWrappedService(
		PatcherProjectVersionLocalService patcherProjectVersionLocalService) {

		_patcherProjectVersionLocalService = patcherProjectVersionLocalService;
	}

	private PatcherProjectVersionLocalService
		_patcherProjectVersionLocalService;

}
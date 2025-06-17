/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link PatcherBuildLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherBuildLocalService
 * @generated
 */
public class PatcherBuildLocalServiceWrapper
	implements PatcherBuildLocalService,
			   ServiceWrapper<PatcherBuildLocalService> {

	public PatcherBuildLocalServiceWrapper() {
		this(null);
	}

	public PatcherBuildLocalServiceWrapper(
		PatcherBuildLocalService patcherBuildLocalService) {

		_patcherBuildLocalService = patcherBuildLocalService;
	}

	@Override
	public boolean addPatcherAccountPatcherBuild(
		long patcherAccountId, long patcherBuildId) {

		return _patcherBuildLocalService.addPatcherAccountPatcherBuild(
			patcherAccountId, patcherBuildId);
	}

	@Override
	public boolean addPatcherAccountPatcherBuild(
		long patcherAccountId,
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild) {

		return _patcherBuildLocalService.addPatcherAccountPatcherBuild(
			patcherAccountId, patcherBuild);
	}

	@Override
	public boolean addPatcherAccountPatcherBuilds(
		long patcherAccountId,
		java.util.List<com.liferay.osb.patcher.model.PatcherBuild>
			patcherBuilds) {

		return _patcherBuildLocalService.addPatcherAccountPatcherBuilds(
			patcherAccountId, patcherBuilds);
	}

	@Override
	public boolean addPatcherAccountPatcherBuilds(
		long patcherAccountId, long[] patcherBuildIds) {

		return _patcherBuildLocalService.addPatcherAccountPatcherBuilds(
			patcherAccountId, patcherBuildIds);
	}

	/**
	 * Adds the patcher build to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherBuildLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherBuild the patcher build
	 * @return the patcher build that was added
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherBuild addPatcherBuild(
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild) {

		return _patcherBuildLocalService.addPatcherBuild(patcherBuild);
	}

	@Override
	public boolean addPatcherFixPatcherBuild(
		long patcherFixId, long patcherBuildId) {

		return _patcherBuildLocalService.addPatcherFixPatcherBuild(
			patcherFixId, patcherBuildId);
	}

	@Override
	public boolean addPatcherFixPatcherBuild(
		long patcherFixId,
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild) {

		return _patcherBuildLocalService.addPatcherFixPatcherBuild(
			patcherFixId, patcherBuild);
	}

	@Override
	public boolean addPatcherFixPatcherBuilds(
		long patcherFixId,
		java.util.List<com.liferay.osb.patcher.model.PatcherBuild>
			patcherBuilds) {

		return _patcherBuildLocalService.addPatcherFixPatcherBuilds(
			patcherFixId, patcherBuilds);
	}

	@Override
	public boolean addPatcherFixPatcherBuilds(
		long patcherFixId, long[] patcherBuildIds) {

		return _patcherBuildLocalService.addPatcherFixPatcherBuilds(
			patcherFixId, patcherBuildIds);
	}

	@Override
	public void clearPatcherAccountPatcherBuilds(long patcherAccountId) {
		_patcherBuildLocalService.clearPatcherAccountPatcherBuilds(
			patcherAccountId);
	}

	@Override
	public void clearPatcherFixPatcherBuilds(long patcherFixId) {
		_patcherBuildLocalService.clearPatcherFixPatcherBuilds(patcherFixId);
	}

	/**
	 * Creates a new patcher build with the primary key. Does not add the patcher build to the database.
	 *
	 * @param patcherBuildId the primary key for the new patcher build
	 * @return the new patcher build
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherBuild createPatcherBuild(
		long patcherBuildId) {

		return _patcherBuildLocalService.createPatcherBuild(patcherBuildId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherBuildLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deletePatcherAccountPatcherBuild(
		long patcherAccountId, long patcherBuildId) {

		_patcherBuildLocalService.deletePatcherAccountPatcherBuild(
			patcherAccountId, patcherBuildId);
	}

	@Override
	public void deletePatcherAccountPatcherBuild(
		long patcherAccountId,
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild) {

		_patcherBuildLocalService.deletePatcherAccountPatcherBuild(
			patcherAccountId, patcherBuild);
	}

	@Override
	public void deletePatcherAccountPatcherBuilds(
		long patcherAccountId,
		java.util.List<com.liferay.osb.patcher.model.PatcherBuild>
			patcherBuilds) {

		_patcherBuildLocalService.deletePatcherAccountPatcherBuilds(
			patcherAccountId, patcherBuilds);
	}

	@Override
	public void deletePatcherAccountPatcherBuilds(
		long patcherAccountId, long[] patcherBuildIds) {

		_patcherBuildLocalService.deletePatcherAccountPatcherBuilds(
			patcherAccountId, patcherBuildIds);
	}

	/**
	 * Deletes the patcher build with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherBuildLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherBuildId the primary key of the patcher build
	 * @return the patcher build that was removed
	 * @throws PortalException if a patcher build with the primary key could not be found
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherBuild deletePatcherBuild(
			long patcherBuildId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherBuildLocalService.deletePatcherBuild(patcherBuildId);
	}

	/**
	 * Deletes the patcher build from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherBuildLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherBuild the patcher build
	 * @return the patcher build that was removed
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherBuild deletePatcherBuild(
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild) {

		return _patcherBuildLocalService.deletePatcherBuild(patcherBuild);
	}

	@Override
	public void deletePatcherFixPatcherBuild(
		long patcherFixId, long patcherBuildId) {

		_patcherBuildLocalService.deletePatcherFixPatcherBuild(
			patcherFixId, patcherBuildId);
	}

	@Override
	public void deletePatcherFixPatcherBuild(
		long patcherFixId,
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild) {

		_patcherBuildLocalService.deletePatcherFixPatcherBuild(
			patcherFixId, patcherBuild);
	}

	@Override
	public void deletePatcherFixPatcherBuilds(
		long patcherFixId,
		java.util.List<com.liferay.osb.patcher.model.PatcherBuild>
			patcherBuilds) {

		_patcherBuildLocalService.deletePatcherFixPatcherBuilds(
			patcherFixId, patcherBuilds);
	}

	@Override
	public void deletePatcherFixPatcherBuilds(
		long patcherFixId, long[] patcherBuildIds) {

		_patcherBuildLocalService.deletePatcherFixPatcherBuilds(
			patcherFixId, patcherBuildIds);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherBuildLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _patcherBuildLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _patcherBuildLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _patcherBuildLocalService.dynamicQuery();
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

		return _patcherBuildLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl</code>.
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

		return _patcherBuildLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl</code>.
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

		return _patcherBuildLocalService.dynamicQuery(
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

		return _patcherBuildLocalService.dynamicQueryCount(dynamicQuery);
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

		return _patcherBuildLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherBuild fetchPatcherBuild(
		long patcherBuildId) {

		return _patcherBuildLocalService.fetchPatcherBuild(patcherBuildId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _patcherBuildLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _patcherBuildLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _patcherBuildLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherBuild>
		getPatcherAccountPatcherBuilds(long patcherAccountId) {

		return _patcherBuildLocalService.getPatcherAccountPatcherBuilds(
			patcherAccountId);
	}

	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherBuild>
		getPatcherAccountPatcherBuilds(
			long patcherAccountId, int start, int end) {

		return _patcherBuildLocalService.getPatcherAccountPatcherBuilds(
			patcherAccountId, start, end);
	}

	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherBuild>
		getPatcherAccountPatcherBuilds(
			long patcherAccountId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.osb.patcher.model.PatcherBuild>
					orderByComparator) {

		return _patcherBuildLocalService.getPatcherAccountPatcherBuilds(
			patcherAccountId, start, end, orderByComparator);
	}

	@Override
	public int getPatcherAccountPatcherBuildsCount(long patcherAccountId) {
		return _patcherBuildLocalService.getPatcherAccountPatcherBuildsCount(
			patcherAccountId);
	}

	/**
	 * Returns the patcherAccountIds of the patcher accounts associated with the patcher build.
	 *
	 * @param patcherBuildId the patcherBuildId of the patcher build
	 * @return long[] the patcherAccountIds of patcher accounts associated with the patcher build
	 */
	@Override
	public long[] getPatcherAccountPrimaryKeys(long patcherBuildId) {
		return _patcherBuildLocalService.getPatcherAccountPrimaryKeys(
			patcherBuildId);
	}

	/**
	 * Returns the patcher build with the primary key.
	 *
	 * @param patcherBuildId the primary key of the patcher build
	 * @return the patcher build
	 * @throws PortalException if a patcher build with the primary key could not be found
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherBuild getPatcherBuild(
			long patcherBuildId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherBuildLocalService.getPatcherBuild(patcherBuildId);
	}

	/**
	 * Returns a range of all the patcher builds.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of patcher builds
	 */
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherBuild>
		getPatcherBuilds(int start, int end) {

		return _patcherBuildLocalService.getPatcherBuilds(start, end);
	}

	/**
	 * Returns the number of patcher builds.
	 *
	 * @return the number of patcher builds
	 */
	@Override
	public int getPatcherBuildsCount() {
		return _patcherBuildLocalService.getPatcherBuildsCount();
	}

	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherBuild>
		getPatcherFixPatcherBuilds(long patcherFixId) {

		return _patcherBuildLocalService.getPatcherFixPatcherBuilds(
			patcherFixId);
	}

	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherBuild>
		getPatcherFixPatcherBuilds(long patcherFixId, int start, int end) {

		return _patcherBuildLocalService.getPatcherFixPatcherBuilds(
			patcherFixId, start, end);
	}

	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherBuild>
		getPatcherFixPatcherBuilds(
			long patcherFixId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.osb.patcher.model.PatcherBuild>
					orderByComparator) {

		return _patcherBuildLocalService.getPatcherFixPatcherBuilds(
			patcherFixId, start, end, orderByComparator);
	}

	@Override
	public int getPatcherFixPatcherBuildsCount(long patcherFixId) {
		return _patcherBuildLocalService.getPatcherFixPatcherBuildsCount(
			patcherFixId);
	}

	/**
	 * Returns the patcherFixIds of the patcher fixes associated with the patcher build.
	 *
	 * @param patcherBuildId the patcherBuildId of the patcher build
	 * @return long[] the patcherFixIds of patcher fixes associated with the patcher build
	 */
	@Override
	public long[] getPatcherFixPrimaryKeys(long patcherBuildId) {
		return _patcherBuildLocalService.getPatcherFixPrimaryKeys(
			patcherBuildId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherBuildLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public boolean hasPatcherAccountPatcherBuild(
		long patcherAccountId, long patcherBuildId) {

		return _patcherBuildLocalService.hasPatcherAccountPatcherBuild(
			patcherAccountId, patcherBuildId);
	}

	@Override
	public boolean hasPatcherAccountPatcherBuilds(long patcherAccountId) {
		return _patcherBuildLocalService.hasPatcherAccountPatcherBuilds(
			patcherAccountId);
	}

	@Override
	public boolean hasPatcherFixPatcherBuild(
		long patcherFixId, long patcherBuildId) {

		return _patcherBuildLocalService.hasPatcherFixPatcherBuild(
			patcherFixId, patcherBuildId);
	}

	@Override
	public boolean hasPatcherFixPatcherBuilds(long patcherFixId) {
		return _patcherBuildLocalService.hasPatcherFixPatcherBuilds(
			patcherFixId);
	}

	@Override
	public void setPatcherAccountPatcherBuilds(
		long patcherAccountId, long[] patcherBuildIds) {

		_patcherBuildLocalService.setPatcherAccountPatcherBuilds(
			patcherAccountId, patcherBuildIds);
	}

	@Override
	public void setPatcherFixPatcherBuilds(
		long patcherFixId, long[] patcherBuildIds) {

		_patcherBuildLocalService.setPatcherFixPatcherBuilds(
			patcherFixId, patcherBuildIds);
	}

	/**
	 * Updates the patcher build in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherBuildLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherBuild the patcher build
	 * @return the patcher build that was updated
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherBuild updatePatcherBuild(
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild) {

		return _patcherBuildLocalService.updatePatcherBuild(patcherBuild);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _patcherBuildLocalService.getBasePersistence();
	}

	@Override
	public PatcherBuildLocalService getWrappedService() {
		return _patcherBuildLocalService;
	}

	@Override
	public void setWrappedService(
		PatcherBuildLocalService patcherBuildLocalService) {

		_patcherBuildLocalService = patcherBuildLocalService;
	}

	private PatcherBuildLocalService _patcherBuildLocalService;

}
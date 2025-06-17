/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link PatcherBuildRelLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherBuildRelLocalService
 * @generated
 */
public class PatcherBuildRelLocalServiceWrapper
	implements PatcherBuildRelLocalService,
			   ServiceWrapper<PatcherBuildRelLocalService> {

	public PatcherBuildRelLocalServiceWrapper() {
		this(null);
	}

	public PatcherBuildRelLocalServiceWrapper(
		PatcherBuildRelLocalService patcherBuildRelLocalService) {

		_patcherBuildRelLocalService = patcherBuildRelLocalService;
	}

	/**
	 * Adds the patcher build rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherBuildRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherBuildRel the patcher build rel
	 * @return the patcher build rel that was added
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherBuildRel addPatcherBuildRel(
		com.liferay.osb.patcher.model.PatcherBuildRel patcherBuildRel) {

		return _patcherBuildRelLocalService.addPatcherBuildRel(patcherBuildRel);
	}

	/**
	 * Creates a new patcher build rel with the primary key. Does not add the patcher build rel to the database.
	 *
	 * @param patcherBuildRelId the primary key for the new patcher build rel
	 * @return the new patcher build rel
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherBuildRel createPatcherBuildRel(
		long patcherBuildRelId) {

		return _patcherBuildRelLocalService.createPatcherBuildRel(
			patcherBuildRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherBuildRelLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the patcher build rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherBuildRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherBuildRelId the primary key of the patcher build rel
	 * @return the patcher build rel that was removed
	 * @throws PortalException if a patcher build rel with the primary key could not be found
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherBuildRel deletePatcherBuildRel(
			long patcherBuildRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherBuildRelLocalService.deletePatcherBuildRel(
			patcherBuildRelId);
	}

	/**
	 * Deletes the patcher build rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherBuildRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherBuildRel the patcher build rel
	 * @return the patcher build rel that was removed
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherBuildRel deletePatcherBuildRel(
		com.liferay.osb.patcher.model.PatcherBuildRel patcherBuildRel) {

		return _patcherBuildRelLocalService.deletePatcherBuildRel(
			patcherBuildRel);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherBuildRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _patcherBuildRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _patcherBuildRelLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _patcherBuildRelLocalService.dynamicQuery();
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

		return _patcherBuildRelLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherBuildRelModelImpl</code>.
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

		return _patcherBuildRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherBuildRelModelImpl</code>.
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

		return _patcherBuildRelLocalService.dynamicQuery(
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

		return _patcherBuildRelLocalService.dynamicQueryCount(dynamicQuery);
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

		return _patcherBuildRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherBuildRel fetchPatcherBuildRel(
		long patcherBuildRelId) {

		return _patcherBuildRelLocalService.fetchPatcherBuildRel(
			patcherBuildRelId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _patcherBuildRelLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _patcherBuildRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _patcherBuildRelLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * Returns the patcher build rel with the primary key.
	 *
	 * @param patcherBuildRelId the primary key of the patcher build rel
	 * @return the patcher build rel
	 * @throws PortalException if a patcher build rel with the primary key could not be found
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherBuildRel getPatcherBuildRel(
			long patcherBuildRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherBuildRelLocalService.getPatcherBuildRel(
			patcherBuildRelId);
	}

	/**
	 * Returns a range of all the patcher build rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherBuildRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher build rels
	 * @param end the upper bound of the range of patcher build rels (not inclusive)
	 * @return the range of patcher build rels
	 */
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherBuildRel>
		getPatcherBuildRels(int start, int end) {

		return _patcherBuildRelLocalService.getPatcherBuildRels(start, end);
	}

	/**
	 * Returns the number of patcher build rels.
	 *
	 * @return the number of patcher build rels
	 */
	@Override
	public int getPatcherBuildRelsCount() {
		return _patcherBuildRelLocalService.getPatcherBuildRelsCount();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherBuildRelLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the patcher build rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherBuildRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherBuildRel the patcher build rel
	 * @return the patcher build rel that was updated
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherBuildRel updatePatcherBuildRel(
		com.liferay.osb.patcher.model.PatcherBuildRel patcherBuildRel) {

		return _patcherBuildRelLocalService.updatePatcherBuildRel(
			patcherBuildRel);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _patcherBuildRelLocalService.getBasePersistence();
	}

	@Override
	public PatcherBuildRelLocalService getWrappedService() {
		return _patcherBuildRelLocalService;
	}

	@Override
	public void setWrappedService(
		PatcherBuildRelLocalService patcherBuildRelLocalService) {

		_patcherBuildRelLocalService = patcherBuildRelLocalService;
	}

	private PatcherBuildRelLocalService _patcherBuildRelLocalService;

}
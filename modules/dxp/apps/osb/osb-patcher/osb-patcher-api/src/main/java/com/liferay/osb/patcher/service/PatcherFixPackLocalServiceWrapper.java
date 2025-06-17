/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link PatcherFixPackLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherFixPackLocalService
 * @generated
 */
public class PatcherFixPackLocalServiceWrapper
	implements PatcherFixPackLocalService,
			   ServiceWrapper<PatcherFixPackLocalService> {

	public PatcherFixPackLocalServiceWrapper() {
		this(null);
	}

	public PatcherFixPackLocalServiceWrapper(
		PatcherFixPackLocalService patcherFixPackLocalService) {

		_patcherFixPackLocalService = patcherFixPackLocalService;
	}

	/**
	 * Adds the patcher fix pack to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherFixPackLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherFixPack the patcher fix pack
	 * @return the patcher fix pack that was added
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherFixPack addPatcherFixPack(
		com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack) {

		return _patcherFixPackLocalService.addPatcherFixPack(patcherFixPack);
	}

	@Override
	public boolean addPatcherFixPatcherFixPack(
		long patcherFixId, long patcherFixPackId) {

		return _patcherFixPackLocalService.addPatcherFixPatcherFixPack(
			patcherFixId, patcherFixPackId);
	}

	@Override
	public boolean addPatcherFixPatcherFixPack(
		long patcherFixId,
		com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack) {

		return _patcherFixPackLocalService.addPatcherFixPatcherFixPack(
			patcherFixId, patcherFixPack);
	}

	@Override
	public boolean addPatcherFixPatcherFixPacks(
		long patcherFixId,
		java.util.List<com.liferay.osb.patcher.model.PatcherFixPack>
			patcherFixPacks) {

		return _patcherFixPackLocalService.addPatcherFixPatcherFixPacks(
			patcherFixId, patcherFixPacks);
	}

	@Override
	public boolean addPatcherFixPatcherFixPacks(
		long patcherFixId, long[] patcherFixPackIds) {

		return _patcherFixPackLocalService.addPatcherFixPatcherFixPacks(
			patcherFixId, patcherFixPackIds);
	}

	@Override
	public void clearPatcherFixPatcherFixPacks(long patcherFixId) {
		_patcherFixPackLocalService.clearPatcherFixPatcherFixPacks(
			patcherFixId);
	}

	/**
	 * Creates a new patcher fix pack with the primary key. Does not add the patcher fix pack to the database.
	 *
	 * @param patcherFixPackId the primary key for the new patcher fix pack
	 * @return the new patcher fix pack
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherFixPack createPatcherFixPack(
		long patcherFixPackId) {

		return _patcherFixPackLocalService.createPatcherFixPack(
			patcherFixPackId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherFixPackLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the patcher fix pack with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherFixPackLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherFixPackId the primary key of the patcher fix pack
	 * @return the patcher fix pack that was removed
	 * @throws PortalException if a patcher fix pack with the primary key could not be found
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherFixPack deletePatcherFixPack(
			long patcherFixPackId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherFixPackLocalService.deletePatcherFixPack(
			patcherFixPackId);
	}

	/**
	 * Deletes the patcher fix pack from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherFixPackLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherFixPack the patcher fix pack
	 * @return the patcher fix pack that was removed
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherFixPack deletePatcherFixPack(
		com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack) {

		return _patcherFixPackLocalService.deletePatcherFixPack(patcherFixPack);
	}

	@Override
	public void deletePatcherFixPatcherFixPack(
		long patcherFixId, long patcherFixPackId) {

		_patcherFixPackLocalService.deletePatcherFixPatcherFixPack(
			patcherFixId, patcherFixPackId);
	}

	@Override
	public void deletePatcherFixPatcherFixPack(
		long patcherFixId,
		com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack) {

		_patcherFixPackLocalService.deletePatcherFixPatcherFixPack(
			patcherFixId, patcherFixPack);
	}

	@Override
	public void deletePatcherFixPatcherFixPacks(
		long patcherFixId,
		java.util.List<com.liferay.osb.patcher.model.PatcherFixPack>
			patcherFixPacks) {

		_patcherFixPackLocalService.deletePatcherFixPatcherFixPacks(
			patcherFixId, patcherFixPacks);
	}

	@Override
	public void deletePatcherFixPatcherFixPacks(
		long patcherFixId, long[] patcherFixPackIds) {

		_patcherFixPackLocalService.deletePatcherFixPatcherFixPacks(
			patcherFixId, patcherFixPackIds);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherFixPackLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _patcherFixPackLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _patcherFixPackLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _patcherFixPackLocalService.dynamicQuery();
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

		return _patcherFixPackLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherFixPackModelImpl</code>.
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

		return _patcherFixPackLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherFixPackModelImpl</code>.
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

		return _patcherFixPackLocalService.dynamicQuery(
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

		return _patcherFixPackLocalService.dynamicQueryCount(dynamicQuery);
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

		return _patcherFixPackLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherFixPack fetchPatcherFixPack(
		long patcherFixPackId) {

		return _patcherFixPackLocalService.fetchPatcherFixPack(
			patcherFixPackId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _patcherFixPackLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _patcherFixPackLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _patcherFixPackLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * Returns the patcher fix pack with the primary key.
	 *
	 * @param patcherFixPackId the primary key of the patcher fix pack
	 * @return the patcher fix pack
	 * @throws PortalException if a patcher fix pack with the primary key could not be found
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherFixPack getPatcherFixPack(
			long patcherFixPackId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherFixPackLocalService.getPatcherFixPack(patcherFixPackId);
	}

	/**
	 * Returns a range of all the patcher fix packs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of patcher fix packs
	 */
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherFixPack>
		getPatcherFixPacks(int start, int end) {

		return _patcherFixPackLocalService.getPatcherFixPacks(start, end);
	}

	/**
	 * Returns the number of patcher fix packs.
	 *
	 * @return the number of patcher fix packs
	 */
	@Override
	public int getPatcherFixPacksCount() {
		return _patcherFixPackLocalService.getPatcherFixPacksCount();
	}

	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherFixPack>
		getPatcherFixPatcherFixPacks(long patcherFixId) {

		return _patcherFixPackLocalService.getPatcherFixPatcherFixPacks(
			patcherFixId);
	}

	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherFixPack>
		getPatcherFixPatcherFixPacks(long patcherFixId, int start, int end) {

		return _patcherFixPackLocalService.getPatcherFixPatcherFixPacks(
			patcherFixId, start, end);
	}

	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherFixPack>
		getPatcherFixPatcherFixPacks(
			long patcherFixId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.osb.patcher.model.PatcherFixPack>
					orderByComparator) {

		return _patcherFixPackLocalService.getPatcherFixPatcherFixPacks(
			patcherFixId, start, end, orderByComparator);
	}

	@Override
	public int getPatcherFixPatcherFixPacksCount(long patcherFixId) {
		return _patcherFixPackLocalService.getPatcherFixPatcherFixPacksCount(
			patcherFixId);
	}

	/**
	 * Returns the patcherFixIds of the patcher fixes associated with the patcher fix pack.
	 *
	 * @param patcherFixPackId the patcherFixPackId of the patcher fix pack
	 * @return long[] the patcherFixIds of patcher fixes associated with the patcher fix pack
	 */
	@Override
	public long[] getPatcherFixPrimaryKeys(long patcherFixPackId) {
		return _patcherFixPackLocalService.getPatcherFixPrimaryKeys(
			patcherFixPackId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherFixPackLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public boolean hasPatcherFixPatcherFixPack(
		long patcherFixId, long patcherFixPackId) {

		return _patcherFixPackLocalService.hasPatcherFixPatcherFixPack(
			patcherFixId, patcherFixPackId);
	}

	@Override
	public boolean hasPatcherFixPatcherFixPacks(long patcherFixId) {
		return _patcherFixPackLocalService.hasPatcherFixPatcherFixPacks(
			patcherFixId);
	}

	@Override
	public void setPatcherFixPatcherFixPacks(
		long patcherFixId, long[] patcherFixPackIds) {

		_patcherFixPackLocalService.setPatcherFixPatcherFixPacks(
			patcherFixId, patcherFixPackIds);
	}

	/**
	 * Updates the patcher fix pack in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherFixPackLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherFixPack the patcher fix pack
	 * @return the patcher fix pack that was updated
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherFixPack updatePatcherFixPack(
		com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack) {

		return _patcherFixPackLocalService.updatePatcherFixPack(patcherFixPack);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _patcherFixPackLocalService.getBasePersistence();
	}

	@Override
	public PatcherFixPackLocalService getWrappedService() {
		return _patcherFixPackLocalService;
	}

	@Override
	public void setWrappedService(
		PatcherFixPackLocalService patcherFixPackLocalService) {

		_patcherFixPackLocalService = patcherFixPackLocalService;
	}

	private PatcherFixPackLocalService _patcherFixPackLocalService;

}
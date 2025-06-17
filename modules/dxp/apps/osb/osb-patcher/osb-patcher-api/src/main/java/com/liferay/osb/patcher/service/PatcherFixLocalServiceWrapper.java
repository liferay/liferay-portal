/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link PatcherFixLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherFixLocalService
 * @generated
 */
public class PatcherFixLocalServiceWrapper
	implements PatcherFixLocalService, ServiceWrapper<PatcherFixLocalService> {

	public PatcherFixLocalServiceWrapper() {
		this(null);
	}

	public PatcherFixLocalServiceWrapper(
		PatcherFixLocalService patcherFixLocalService) {

		_patcherFixLocalService = patcherFixLocalService;
	}

	@Override
	public boolean addPatcherBuildPatcherFix(
		long patcherBuildId, long patcherFixId) {

		return _patcherFixLocalService.addPatcherBuildPatcherFix(
			patcherBuildId, patcherFixId);
	}

	@Override
	public boolean addPatcherBuildPatcherFix(
		long patcherBuildId,
		com.liferay.osb.patcher.model.PatcherFix patcherFix) {

		return _patcherFixLocalService.addPatcherBuildPatcherFix(
			patcherBuildId, patcherFix);
	}

	@Override
	public boolean addPatcherBuildPatcherFixes(
		long patcherBuildId,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixes) {

		return _patcherFixLocalService.addPatcherBuildPatcherFixes(
			patcherBuildId, patcherFixes);
	}

	@Override
	public boolean addPatcherBuildPatcherFixes(
		long patcherBuildId, long[] patcherFixIds) {

		return _patcherFixLocalService.addPatcherBuildPatcherFixes(
			patcherBuildId, patcherFixIds);
	}

	/**
	 * Adds the patcher fix to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherFixLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherFix the patcher fix
	 * @return the patcher fix that was added
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherFix addPatcherFix(
		com.liferay.osb.patcher.model.PatcherFix patcherFix) {

		return _patcherFixLocalService.addPatcherFix(patcherFix);
	}

	@Override
	public boolean addPatcherFixPackPatcherFix(
		long patcherFixPackId, long patcherFixId) {

		return _patcherFixLocalService.addPatcherFixPackPatcherFix(
			patcherFixPackId, patcherFixId);
	}

	@Override
	public boolean addPatcherFixPackPatcherFix(
		long patcherFixPackId,
		com.liferay.osb.patcher.model.PatcherFix patcherFix) {

		return _patcherFixLocalService.addPatcherFixPackPatcherFix(
			patcherFixPackId, patcherFix);
	}

	@Override
	public boolean addPatcherFixPackPatcherFixes(
		long patcherFixPackId,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixes) {

		return _patcherFixLocalService.addPatcherFixPackPatcherFixes(
			patcherFixPackId, patcherFixes);
	}

	@Override
	public boolean addPatcherFixPackPatcherFixes(
		long patcherFixPackId, long[] patcherFixIds) {

		return _patcherFixLocalService.addPatcherFixPackPatcherFixes(
			patcherFixPackId, patcherFixIds);
	}

	@Override
	public void clearPatcherBuildPatcherFixes(long patcherBuildId) {
		_patcherFixLocalService.clearPatcherBuildPatcherFixes(patcherBuildId);
	}

	@Override
	public void clearPatcherFixPackPatcherFixes(long patcherFixPackId) {
		_patcherFixLocalService.clearPatcherFixPackPatcherFixes(
			patcherFixPackId);
	}

	/**
	 * Creates a new patcher fix with the primary key. Does not add the patcher fix to the database.
	 *
	 * @param patcherFixId the primary key for the new patcher fix
	 * @return the new patcher fix
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherFix createPatcherFix(
		long patcherFixId) {

		return _patcherFixLocalService.createPatcherFix(patcherFixId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherFixLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deletePatcherBuildPatcherFix(
		long patcherBuildId, long patcherFixId) {

		_patcherFixLocalService.deletePatcherBuildPatcherFix(
			patcherBuildId, patcherFixId);
	}

	@Override
	public void deletePatcherBuildPatcherFix(
		long patcherBuildId,
		com.liferay.osb.patcher.model.PatcherFix patcherFix) {

		_patcherFixLocalService.deletePatcherBuildPatcherFix(
			patcherBuildId, patcherFix);
	}

	@Override
	public void deletePatcherBuildPatcherFixes(
		long patcherBuildId,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixes) {

		_patcherFixLocalService.deletePatcherBuildPatcherFixes(
			patcherBuildId, patcherFixes);
	}

	@Override
	public void deletePatcherBuildPatcherFixes(
		long patcherBuildId, long[] patcherFixIds) {

		_patcherFixLocalService.deletePatcherBuildPatcherFixes(
			patcherBuildId, patcherFixIds);
	}

	/**
	 * Deletes the patcher fix with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherFixLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherFixId the primary key of the patcher fix
	 * @return the patcher fix that was removed
	 * @throws PortalException if a patcher fix with the primary key could not be found
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherFix deletePatcherFix(
			long patcherFixId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherFixLocalService.deletePatcherFix(patcherFixId);
	}

	/**
	 * Deletes the patcher fix from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherFixLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherFix the patcher fix
	 * @return the patcher fix that was removed
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherFix deletePatcherFix(
		com.liferay.osb.patcher.model.PatcherFix patcherFix) {

		return _patcherFixLocalService.deletePatcherFix(patcherFix);
	}

	@Override
	public void deletePatcherFixPackPatcherFix(
		long patcherFixPackId, long patcherFixId) {

		_patcherFixLocalService.deletePatcherFixPackPatcherFix(
			patcherFixPackId, patcherFixId);
	}

	@Override
	public void deletePatcherFixPackPatcherFix(
		long patcherFixPackId,
		com.liferay.osb.patcher.model.PatcherFix patcherFix) {

		_patcherFixLocalService.deletePatcherFixPackPatcherFix(
			patcherFixPackId, patcherFix);
	}

	@Override
	public void deletePatcherFixPackPatcherFixes(
		long patcherFixPackId,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixes) {

		_patcherFixLocalService.deletePatcherFixPackPatcherFixes(
			patcherFixPackId, patcherFixes);
	}

	@Override
	public void deletePatcherFixPackPatcherFixes(
		long patcherFixPackId, long[] patcherFixIds) {

		_patcherFixLocalService.deletePatcherFixPackPatcherFixes(
			patcherFixPackId, patcherFixIds);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherFixLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _patcherFixLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _patcherFixLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _patcherFixLocalService.dynamicQuery();
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

		return _patcherFixLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherFixModelImpl</code>.
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

		return _patcherFixLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherFixModelImpl</code>.
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

		return _patcherFixLocalService.dynamicQuery(
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

		return _patcherFixLocalService.dynamicQueryCount(dynamicQuery);
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

		return _patcherFixLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherFix fetchPatcherFix(
		long patcherFixId) {

		return _patcherFixLocalService.fetchPatcherFix(patcherFixId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _patcherFixLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _patcherFixLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _patcherFixLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherFix>
		getPatcherBuildPatcherFixes(long patcherBuildId) {

		return _patcherFixLocalService.getPatcherBuildPatcherFixes(
			patcherBuildId);
	}

	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherFix>
		getPatcherBuildPatcherFixes(long patcherBuildId, int start, int end) {

		return _patcherFixLocalService.getPatcherBuildPatcherFixes(
			patcherBuildId, start, end);
	}

	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherFix>
		getPatcherBuildPatcherFixes(
			long patcherBuildId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.osb.patcher.model.PatcherFix> orderByComparator) {

		return _patcherFixLocalService.getPatcherBuildPatcherFixes(
			patcherBuildId, start, end, orderByComparator);
	}

	@Override
	public int getPatcherBuildPatcherFixesCount(long patcherBuildId) {
		return _patcherFixLocalService.getPatcherBuildPatcherFixesCount(
			patcherBuildId);
	}

	/**
	 * Returns the patcherBuildIds of the patcher builds associated with the patcher fix.
	 *
	 * @param patcherFixId the patcherFixId of the patcher fix
	 * @return long[] the patcherBuildIds of patcher builds associated with the patcher fix
	 */
	@Override
	public long[] getPatcherBuildPrimaryKeys(long patcherFixId) {
		return _patcherFixLocalService.getPatcherBuildPrimaryKeys(patcherFixId);
	}

	/**
	 * Returns the patcher fix with the primary key.
	 *
	 * @param patcherFixId the primary key of the patcher fix
	 * @return the patcher fix
	 * @throws PortalException if a patcher fix with the primary key could not be found
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherFix getPatcherFix(
			long patcherFixId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherFixLocalService.getPatcherFix(patcherFixId);
	}

	/**
	 * Returns a range of all the patcher fixes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of patcher fixes
	 */
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherFix>
		getPatcherFixes(int start, int end) {

		return _patcherFixLocalService.getPatcherFixes(start, end);
	}

	/**
	 * Returns the number of patcher fixes.
	 *
	 * @return the number of patcher fixes
	 */
	@Override
	public int getPatcherFixesCount() {
		return _patcherFixLocalService.getPatcherFixesCount();
	}

	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherFix>
		getPatcherFixPackPatcherFixes(long patcherFixPackId) {

		return _patcherFixLocalService.getPatcherFixPackPatcherFixes(
			patcherFixPackId);
	}

	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherFix>
		getPatcherFixPackPatcherFixes(
			long patcherFixPackId, int start, int end) {

		return _patcherFixLocalService.getPatcherFixPackPatcherFixes(
			patcherFixPackId, start, end);
	}

	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherFix>
		getPatcherFixPackPatcherFixes(
			long patcherFixPackId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.osb.patcher.model.PatcherFix> orderByComparator) {

		return _patcherFixLocalService.getPatcherFixPackPatcherFixes(
			patcherFixPackId, start, end, orderByComparator);
	}

	@Override
	public int getPatcherFixPackPatcherFixesCount(long patcherFixPackId) {
		return _patcherFixLocalService.getPatcherFixPackPatcherFixesCount(
			patcherFixPackId);
	}

	/**
	 * Returns the patcherFixPackIds of the patcher fix packs associated with the patcher fix.
	 *
	 * @param patcherFixId the patcherFixId of the patcher fix
	 * @return long[] the patcherFixPackIds of patcher fix packs associated with the patcher fix
	 */
	@Override
	public long[] getPatcherFixPackPrimaryKeys(long patcherFixId) {
		return _patcherFixLocalService.getPatcherFixPackPrimaryKeys(
			patcherFixId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherFixLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public boolean hasPatcherBuildPatcherFix(
		long patcherBuildId, long patcherFixId) {

		return _patcherFixLocalService.hasPatcherBuildPatcherFix(
			patcherBuildId, patcherFixId);
	}

	@Override
	public boolean hasPatcherBuildPatcherFixes(long patcherBuildId) {
		return _patcherFixLocalService.hasPatcherBuildPatcherFixes(
			patcherBuildId);
	}

	@Override
	public boolean hasPatcherFixPackPatcherFix(
		long patcherFixPackId, long patcherFixId) {

		return _patcherFixLocalService.hasPatcherFixPackPatcherFix(
			patcherFixPackId, patcherFixId);
	}

	@Override
	public boolean hasPatcherFixPackPatcherFixes(long patcherFixPackId) {
		return _patcherFixLocalService.hasPatcherFixPackPatcherFixes(
			patcherFixPackId);
	}

	@Override
	public void setPatcherBuildPatcherFixes(
		long patcherBuildId, long[] patcherFixIds) {

		_patcherFixLocalService.setPatcherBuildPatcherFixes(
			patcherBuildId, patcherFixIds);
	}

	@Override
	public void setPatcherFixPackPatcherFixes(
		long patcherFixPackId, long[] patcherFixIds) {

		_patcherFixLocalService.setPatcherFixPackPatcherFixes(
			patcherFixPackId, patcherFixIds);
	}

	/**
	 * Updates the patcher fix in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherFixLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherFix the patcher fix
	 * @return the patcher fix that was updated
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherFix updatePatcherFix(
		com.liferay.osb.patcher.model.PatcherFix patcherFix) {

		return _patcherFixLocalService.updatePatcherFix(patcherFix);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _patcherFixLocalService.getBasePersistence();
	}

	@Override
	public PatcherFixLocalService getWrappedService() {
		return _patcherFixLocalService;
	}

	@Override
	public void setWrappedService(
		PatcherFixLocalService patcherFixLocalService) {

		_patcherFixLocalService = patcherFixLocalService;
	}

	private PatcherFixLocalService _patcherFixLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link PatcherAccountLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherAccountLocalService
 * @generated
 */
public class PatcherAccountLocalServiceWrapper
	implements PatcherAccountLocalService,
			   ServiceWrapper<PatcherAccountLocalService> {

	public PatcherAccountLocalServiceWrapper() {
		this(null);
	}

	public PatcherAccountLocalServiceWrapper(
		PatcherAccountLocalService patcherAccountLocalService) {

		_patcherAccountLocalService = patcherAccountLocalService;
	}

	/**
	 * Adds the patcher account to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherAccountLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherAccount the patcher account
	 * @return the patcher account that was added
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherAccount addPatcherAccount(
		com.liferay.osb.patcher.model.PatcherAccount patcherAccount) {

		return _patcherAccountLocalService.addPatcherAccount(patcherAccount);
	}

	@Override
	public boolean addPatcherBuildPatcherAccount(
		long patcherBuildId, long patcherAccountId) {

		return _patcherAccountLocalService.addPatcherBuildPatcherAccount(
			patcherBuildId, patcherAccountId);
	}

	@Override
	public boolean addPatcherBuildPatcherAccount(
		long patcherBuildId,
		com.liferay.osb.patcher.model.PatcherAccount patcherAccount) {

		return _patcherAccountLocalService.addPatcherBuildPatcherAccount(
			patcherBuildId, patcherAccount);
	}

	@Override
	public boolean addPatcherBuildPatcherAccounts(
		long patcherBuildId,
		java.util.List<com.liferay.osb.patcher.model.PatcherAccount>
			patcherAccounts) {

		return _patcherAccountLocalService.addPatcherBuildPatcherAccounts(
			patcherBuildId, patcherAccounts);
	}

	@Override
	public boolean addPatcherBuildPatcherAccounts(
		long patcherBuildId, long[] patcherAccountIds) {

		return _patcherAccountLocalService.addPatcherBuildPatcherAccounts(
			patcherBuildId, patcherAccountIds);
	}

	@Override
	public void clearPatcherBuildPatcherAccounts(long patcherBuildId) {
		_patcherAccountLocalService.clearPatcherBuildPatcherAccounts(
			patcherBuildId);
	}

	/**
	 * Creates a new patcher account with the primary key. Does not add the patcher account to the database.
	 *
	 * @param patcherAccountId the primary key for the new patcher account
	 * @return the new patcher account
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherAccount createPatcherAccount(
		long patcherAccountId) {

		return _patcherAccountLocalService.createPatcherAccount(
			patcherAccountId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherAccountLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the patcher account with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherAccountLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherAccountId the primary key of the patcher account
	 * @return the patcher account that was removed
	 * @throws PortalException if a patcher account with the primary key could not be found
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherAccount deletePatcherAccount(
			long patcherAccountId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherAccountLocalService.deletePatcherAccount(
			patcherAccountId);
	}

	/**
	 * Deletes the patcher account from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherAccountLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherAccount the patcher account
	 * @return the patcher account that was removed
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherAccount deletePatcherAccount(
		com.liferay.osb.patcher.model.PatcherAccount patcherAccount) {

		return _patcherAccountLocalService.deletePatcherAccount(patcherAccount);
	}

	@Override
	public void deletePatcherBuildPatcherAccount(
		long patcherBuildId, long patcherAccountId) {

		_patcherAccountLocalService.deletePatcherBuildPatcherAccount(
			patcherBuildId, patcherAccountId);
	}

	@Override
	public void deletePatcherBuildPatcherAccount(
		long patcherBuildId,
		com.liferay.osb.patcher.model.PatcherAccount patcherAccount) {

		_patcherAccountLocalService.deletePatcherBuildPatcherAccount(
			patcherBuildId, patcherAccount);
	}

	@Override
	public void deletePatcherBuildPatcherAccounts(
		long patcherBuildId,
		java.util.List<com.liferay.osb.patcher.model.PatcherAccount>
			patcherAccounts) {

		_patcherAccountLocalService.deletePatcherBuildPatcherAccounts(
			patcherBuildId, patcherAccounts);
	}

	@Override
	public void deletePatcherBuildPatcherAccounts(
		long patcherBuildId, long[] patcherAccountIds) {

		_patcherAccountLocalService.deletePatcherBuildPatcherAccounts(
			patcherBuildId, patcherAccountIds);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherAccountLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _patcherAccountLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _patcherAccountLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _patcherAccountLocalService.dynamicQuery();
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

		return _patcherAccountLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherAccountModelImpl</code>.
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

		return _patcherAccountLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherAccountModelImpl</code>.
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

		return _patcherAccountLocalService.dynamicQuery(
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

		return _patcherAccountLocalService.dynamicQueryCount(dynamicQuery);
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

		return _patcherAccountLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherAccount fetchPatcherAccount(
		long patcherAccountId) {

		return _patcherAccountLocalService.fetchPatcherAccount(
			patcherAccountId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _patcherAccountLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _patcherAccountLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _patcherAccountLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * Returns the patcher account with the primary key.
	 *
	 * @param patcherAccountId the primary key of the patcher account
	 * @return the patcher account
	 * @throws PortalException if a patcher account with the primary key could not be found
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherAccount getPatcherAccount(
			long patcherAccountId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherAccountLocalService.getPatcherAccount(patcherAccountId);
	}

	/**
	 * Returns a range of all the patcher accounts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @return the range of patcher accounts
	 */
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherAccount>
		getPatcherAccounts(int start, int end) {

		return _patcherAccountLocalService.getPatcherAccounts(start, end);
	}

	/**
	 * Returns the number of patcher accounts.
	 *
	 * @return the number of patcher accounts
	 */
	@Override
	public int getPatcherAccountsCount() {
		return _patcherAccountLocalService.getPatcherAccountsCount();
	}

	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherAccount>
		getPatcherBuildPatcherAccounts(long patcherBuildId) {

		return _patcherAccountLocalService.getPatcherBuildPatcherAccounts(
			patcherBuildId);
	}

	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherAccount>
		getPatcherBuildPatcherAccounts(
			long patcherBuildId, int start, int end) {

		return _patcherAccountLocalService.getPatcherBuildPatcherAccounts(
			patcherBuildId, start, end);
	}

	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherAccount>
		getPatcherBuildPatcherAccounts(
			long patcherBuildId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.osb.patcher.model.PatcherAccount>
					orderByComparator) {

		return _patcherAccountLocalService.getPatcherBuildPatcherAccounts(
			patcherBuildId, start, end, orderByComparator);
	}

	@Override
	public int getPatcherBuildPatcherAccountsCount(long patcherBuildId) {
		return _patcherAccountLocalService.getPatcherBuildPatcherAccountsCount(
			patcherBuildId);
	}

	/**
	 * Returns the patcherBuildIds of the patcher builds associated with the patcher account.
	 *
	 * @param patcherAccountId the patcherAccountId of the patcher account
	 * @return long[] the patcherBuildIds of patcher builds associated with the patcher account
	 */
	@Override
	public long[] getPatcherBuildPrimaryKeys(long patcherAccountId) {
		return _patcherAccountLocalService.getPatcherBuildPrimaryKeys(
			patcherAccountId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherAccountLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public boolean hasPatcherBuildPatcherAccount(
		long patcherBuildId, long patcherAccountId) {

		return _patcherAccountLocalService.hasPatcherBuildPatcherAccount(
			patcherBuildId, patcherAccountId);
	}

	@Override
	public boolean hasPatcherBuildPatcherAccounts(long patcherBuildId) {
		return _patcherAccountLocalService.hasPatcherBuildPatcherAccounts(
			patcherBuildId);
	}

	@Override
	public void setPatcherBuildPatcherAccounts(
		long patcherBuildId, long[] patcherAccountIds) {

		_patcherAccountLocalService.setPatcherBuildPatcherAccounts(
			patcherBuildId, patcherAccountIds);
	}

	/**
	 * Updates the patcher account in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherAccountLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherAccount the patcher account
	 * @return the patcher account that was updated
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherAccount updatePatcherAccount(
		com.liferay.osb.patcher.model.PatcherAccount patcherAccount) {

		return _patcherAccountLocalService.updatePatcherAccount(patcherAccount);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _patcherAccountLocalService.getBasePersistence();
	}

	@Override
	public PatcherAccountLocalService getWrappedService() {
		return _patcherAccountLocalService;
	}

	@Override
	public void setWrappedService(
		PatcherAccountLocalService patcherAccountLocalService) {

		_patcherAccountLocalService = patcherAccountLocalService;
	}

	private PatcherAccountLocalService _patcherAccountLocalService;

}
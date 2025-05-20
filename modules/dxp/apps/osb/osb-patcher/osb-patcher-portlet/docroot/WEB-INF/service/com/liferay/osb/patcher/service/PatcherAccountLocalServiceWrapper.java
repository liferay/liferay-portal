/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.osb.patcher.service;

import com.liferay.portal.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link PatcherAccountLocalService}.
 *
 * @author Calvin Keum
 * @see PatcherAccountLocalService
 * @generated
 */
public class PatcherAccountLocalServiceWrapper
	implements PatcherAccountLocalService,
		ServiceWrapper<PatcherAccountLocalService> {
	public PatcherAccountLocalServiceWrapper(
		PatcherAccountLocalService patcherAccountLocalService) {
		_patcherAccountLocalService = patcherAccountLocalService;
	}

	/**
	* Adds the patcher account to the database. Also notifies the appropriate model listeners.
	*
	* @param patcherAccount the patcher account
	* @return the patcher account that was added
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherAccount addPatcherAccount(
		com.liferay.osb.patcher.model.PatcherAccount patcherAccount)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherAccountLocalService.addPatcherAccount(patcherAccount);
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
		return _patcherAccountLocalService.createPatcherAccount(patcherAccountId);
	}

	/**
	* Deletes the patcher account with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherAccountId the primary key of the patcher account
	* @return the patcher account that was removed
	* @throws PortalException if a patcher account with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherAccount deletePatcherAccount(
		long patcherAccountId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _patcherAccountLocalService.deletePatcherAccount(patcherAccountId);
	}

	/**
	* Deletes the patcher account from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherAccount the patcher account
	* @return the patcher account that was removed
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherAccount deletePatcherAccount(
		com.liferay.osb.patcher.model.PatcherAccount patcherAccount)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherAccountLocalService.deletePatcherAccount(patcherAccount);
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
	* @throws SystemException if a system exception occurred
	*/
	@Override
	@SuppressWarnings("rawtypes")
	public java.util.List dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherAccountLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	* Performs a dynamic query on the database and returns a range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherAccountModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param dynamicQuery the dynamic query
	* @param start the lower bound of the range of model instances
	* @param end the upper bound of the range of model instances (not inclusive)
	* @return the range of matching rows
	* @throws SystemException if a system exception occurred
	*/
	@Override
	@SuppressWarnings("rawtypes")
	public java.util.List dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherAccountLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	* Performs a dynamic query on the database and returns an ordered range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherAccountModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param dynamicQuery the dynamic query
	* @param start the lower bound of the range of model instances
	* @param end the upper bound of the range of model instances (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching rows
	* @throws SystemException if a system exception occurred
	*/
	@Override
	@SuppressWarnings("rawtypes")
	public java.util.List dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherAccountLocalService.dynamicQuery(dynamicQuery, start,
			end, orderByComparator);
	}

	/**
	* Returns the number of rows that match the dynamic query.
	*
	* @param dynamicQuery the dynamic query
	* @return the number of rows that match the dynamic query
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherAccountLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	* Returns the number of rows that match the dynamic query.
	*
	* @param dynamicQuery the dynamic query
	* @param projection the projection to apply to the query
	* @return the number of rows that match the dynamic query
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherAccountLocalService.dynamicQueryCount(dynamicQuery,
			projection);
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherAccount fetchPatcherAccount(
		long patcherAccountId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherAccountLocalService.fetchPatcherAccount(patcherAccountId);
	}

	/**
	* Returns the patcher account with the primary key.
	*
	* @param patcherAccountId the primary key of the patcher account
	* @return the patcher account
	* @throws PortalException if a patcher account with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherAccount getPatcherAccount(
		long patcherAccountId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _patcherAccountLocalService.getPatcherAccount(patcherAccountId);
	}

	@Override
	public com.liferay.portal.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _patcherAccountLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	* Returns a range of all the patcher accounts.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherAccountModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher accounts
	* @param end the upper bound of the range of patcher accounts (not inclusive)
	* @return the range of patcher accounts
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherAccount> getPatcherAccounts(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherAccountLocalService.getPatcherAccounts(start, end);
	}

	/**
	* Returns the number of patcher accounts.
	*
	* @return the number of patcher accounts
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public int getPatcherAccountsCount()
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherAccountLocalService.getPatcherAccountsCount();
	}

	/**
	* Updates the patcher account in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param patcherAccount the patcher account
	* @return the patcher account that was updated
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherAccount updatePatcherAccount(
		com.liferay.osb.patcher.model.PatcherAccount patcherAccount)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherAccountLocalService.updatePatcherAccount(patcherAccount);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void addPatcherBuildPatcherAccount(long patcherBuildId,
		long patcherAccountId)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherAccountLocalService.addPatcherBuildPatcherAccount(patcherBuildId,
			patcherAccountId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void addPatcherBuildPatcherAccount(long patcherBuildId,
		com.liferay.osb.patcher.model.PatcherAccount patcherAccount)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherAccountLocalService.addPatcherBuildPatcherAccount(patcherBuildId,
			patcherAccount);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void addPatcherBuildPatcherAccounts(long patcherBuildId,
		long[] patcherAccountIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherAccountLocalService.addPatcherBuildPatcherAccounts(patcherBuildId,
			patcherAccountIds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void addPatcherBuildPatcherAccounts(long patcherBuildId,
		java.util.List<com.liferay.osb.patcher.model.PatcherAccount> patcherAccounts)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherAccountLocalService.addPatcherBuildPatcherAccounts(patcherBuildId,
			patcherAccounts);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void clearPatcherBuildPatcherAccounts(long patcherBuildId)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherAccountLocalService.clearPatcherBuildPatcherAccounts(patcherBuildId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void deletePatcherBuildPatcherAccount(long patcherBuildId,
		long patcherAccountId)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherAccountLocalService.deletePatcherBuildPatcherAccount(patcherBuildId,
			patcherAccountId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void deletePatcherBuildPatcherAccount(long patcherBuildId,
		com.liferay.osb.patcher.model.PatcherAccount patcherAccount)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherAccountLocalService.deletePatcherBuildPatcherAccount(patcherBuildId,
			patcherAccount);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void deletePatcherBuildPatcherAccounts(long patcherBuildId,
		long[] patcherAccountIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherAccountLocalService.deletePatcherBuildPatcherAccounts(patcherBuildId,
			patcherAccountIds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void deletePatcherBuildPatcherAccounts(long patcherBuildId,
		java.util.List<com.liferay.osb.patcher.model.PatcherAccount> patcherAccounts)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherAccountLocalService.deletePatcherBuildPatcherAccounts(patcherBuildId,
			patcherAccounts);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherAccount> getPatcherBuildPatcherAccounts(
		long patcherBuildId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherAccountLocalService.getPatcherBuildPatcherAccounts(patcherBuildId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherAccount> getPatcherBuildPatcherAccounts(
		long patcherBuildId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherAccountLocalService.getPatcherBuildPatcherAccounts(patcherBuildId,
			start, end);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherAccount> getPatcherBuildPatcherAccounts(
		long patcherBuildId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherAccountLocalService.getPatcherBuildPatcherAccounts(patcherBuildId,
			start, end, orderByComparator);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public int getPatcherBuildPatcherAccountsCount(long patcherBuildId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherAccountLocalService.getPatcherBuildPatcherAccountsCount(patcherBuildId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public boolean hasPatcherBuildPatcherAccount(long patcherBuildId,
		long patcherAccountId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherAccountLocalService.hasPatcherBuildPatcherAccount(patcherBuildId,
			patcherAccountId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public boolean hasPatcherBuildPatcherAccounts(long patcherBuildId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherAccountLocalService.hasPatcherBuildPatcherAccounts(patcherBuildId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void setPatcherBuildPatcherAccounts(long patcherBuildId,
		long[] patcherAccountIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherAccountLocalService.setPatcherBuildPatcherAccounts(patcherBuildId,
			patcherAccountIds);
	}

	/**
	* Returns the Spring bean ID for this bean.
	*
	* @return the Spring bean ID for this bean
	*/
	@Override
	public java.lang.String getBeanIdentifier() {
		return _patcherAccountLocalService.getBeanIdentifier();
	}

	/**
	* Sets the Spring bean ID for this bean.
	*
	* @param beanIdentifier the Spring bean ID for this bean
	*/
	@Override
	public void setBeanIdentifier(java.lang.String beanIdentifier) {
		_patcherAccountLocalService.setBeanIdentifier(beanIdentifier);
	}

	@Override
	public java.lang.Object invokeMethod(java.lang.String name,
		java.lang.String[] parameterTypes, java.lang.Object[] arguments)
		throws java.lang.Throwable {
		return _patcherAccountLocalService.invokeMethod(name, parameterTypes,
			arguments);
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedService}
	 */
	public PatcherAccountLocalService getWrappedPatcherAccountLocalService() {
		return _patcherAccountLocalService;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #setWrappedService}
	 */
	public void setWrappedPatcherAccountLocalService(
		PatcherAccountLocalService patcherAccountLocalService) {
		_patcherAccountLocalService = patcherAccountLocalService;
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
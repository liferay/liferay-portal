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
 * Provides a wrapper for {@link PatcherProjectVersionLocalService}.
 *
 * @author Calvin Keum
 * @see PatcherProjectVersionLocalService
 * @generated
 */
public class PatcherProjectVersionLocalServiceWrapper
	implements PatcherProjectVersionLocalService,
		ServiceWrapper<PatcherProjectVersionLocalService> {
	public PatcherProjectVersionLocalServiceWrapper(
		PatcherProjectVersionLocalService patcherProjectVersionLocalService) {
		_patcherProjectVersionLocalService = patcherProjectVersionLocalService;
	}

	/**
	* Adds the patcher project version to the database. Also notifies the appropriate model listeners.
	*
	* @param patcherProjectVersion the patcher project version
	* @return the patcher project version that was added
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherProjectVersion addPatcherProjectVersion(
		com.liferay.osb.patcher.model.PatcherProjectVersion patcherProjectVersion)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherProjectVersionLocalService.addPatcherProjectVersion(patcherProjectVersion);
	}

	/**
	* Creates a new patcher project version with the primary key. Does not add the patcher project version to the database.
	*
	* @param patcherProjectVersionId the primary key for the new patcher project version
	* @return the new patcher project version
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherProjectVersion createPatcherProjectVersion(
		long patcherProjectVersionId) {
		return _patcherProjectVersionLocalService.createPatcherProjectVersion(patcherProjectVersionId);
	}

	/**
	* Deletes the patcher project version with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherProjectVersionId the primary key of the patcher project version
	* @return the patcher project version that was removed
	* @throws PortalException if a patcher project version with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherProjectVersion deletePatcherProjectVersion(
		long patcherProjectVersionId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _patcherProjectVersionLocalService.deletePatcherProjectVersion(patcherProjectVersionId);
	}

	/**
	* Deletes the patcher project version from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherProjectVersion the patcher project version
	* @return the patcher project version that was removed
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherProjectVersion deletePatcherProjectVersion(
		com.liferay.osb.patcher.model.PatcherProjectVersion patcherProjectVersion)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherProjectVersionLocalService.deletePatcherProjectVersion(patcherProjectVersion);
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
	* @throws SystemException if a system exception occurred
	*/
	@Override
	@SuppressWarnings("rawtypes")
	public java.util.List dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherProjectVersionLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	* Performs a dynamic query on the database and returns a range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherProjectVersionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _patcherProjectVersionLocalService.dynamicQuery(dynamicQuery,
			start, end);
	}

	/**
	* Performs a dynamic query on the database and returns an ordered range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherProjectVersionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _patcherProjectVersionLocalService.dynamicQuery(dynamicQuery,
			start, end, orderByComparator);
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
		return _patcherProjectVersionLocalService.dynamicQueryCount(dynamicQuery);
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
		return _patcherProjectVersionLocalService.dynamicQueryCount(dynamicQuery,
			projection);
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherProjectVersion fetchPatcherProjectVersion(
		long patcherProjectVersionId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherProjectVersionLocalService.fetchPatcherProjectVersion(patcherProjectVersionId);
	}

	/**
	* Returns the patcher project version with the primary key.
	*
	* @param patcherProjectVersionId the primary key of the patcher project version
	* @return the patcher project version
	* @throws PortalException if a patcher project version with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherProjectVersion getPatcherProjectVersion(
		long patcherProjectVersionId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _patcherProjectVersionLocalService.getPatcherProjectVersion(patcherProjectVersionId);
	}

	@Override
	public com.liferay.portal.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _patcherProjectVersionLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	* Returns a range of all the patcher project versions.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherProjectVersionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher project versions
	* @param end the upper bound of the range of patcher project versions (not inclusive)
	* @return the range of patcher project versions
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherProjectVersion> getPatcherProjectVersions(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherProjectVersionLocalService.getPatcherProjectVersions(start,
			end);
	}

	/**
	* Returns the number of patcher project versions.
	*
	* @return the number of patcher project versions
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public int getPatcherProjectVersionsCount()
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherProjectVersionLocalService.getPatcherProjectVersionsCount();
	}

	/**
	* Updates the patcher project version in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param patcherProjectVersion the patcher project version
	* @return the patcher project version that was updated
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherProjectVersion updatePatcherProjectVersion(
		com.liferay.osb.patcher.model.PatcherProjectVersion patcherProjectVersion)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherProjectVersionLocalService.updatePatcherProjectVersion(patcherProjectVersion);
	}

	/**
	* Returns the Spring bean ID for this bean.
	*
	* @return the Spring bean ID for this bean
	*/
	@Override
	public java.lang.String getBeanIdentifier() {
		return _patcherProjectVersionLocalService.getBeanIdentifier();
	}

	/**
	* Sets the Spring bean ID for this bean.
	*
	* @param beanIdentifier the Spring bean ID for this bean
	*/
	@Override
	public void setBeanIdentifier(java.lang.String beanIdentifier) {
		_patcherProjectVersionLocalService.setBeanIdentifier(beanIdentifier);
	}

	@Override
	public java.lang.Object invokeMethod(java.lang.String name,
		java.lang.String[] parameterTypes, java.lang.Object[] arguments)
		throws java.lang.Throwable {
		return _patcherProjectVersionLocalService.invokeMethod(name,
			parameterTypes, arguments);
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedService}
	 */
	public PatcherProjectVersionLocalService getWrappedPatcherProjectVersionLocalService() {
		return _patcherProjectVersionLocalService;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #setWrappedService}
	 */
	public void setWrappedPatcherProjectVersionLocalService(
		PatcherProjectVersionLocalService patcherProjectVersionLocalService) {
		_patcherProjectVersionLocalService = patcherProjectVersionLocalService;
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

	private PatcherProjectVersionLocalService _patcherProjectVersionLocalService;
}
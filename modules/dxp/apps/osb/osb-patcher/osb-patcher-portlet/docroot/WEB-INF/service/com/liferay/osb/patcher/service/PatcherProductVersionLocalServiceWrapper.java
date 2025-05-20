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
 * Provides a wrapper for {@link PatcherProductVersionLocalService}.
 *
 * @author Calvin Keum
 * @see PatcherProductVersionLocalService
 * @generated
 */
public class PatcherProductVersionLocalServiceWrapper
	implements PatcherProductVersionLocalService,
		ServiceWrapper<PatcherProductVersionLocalService> {
	public PatcherProductVersionLocalServiceWrapper(
		PatcherProductVersionLocalService patcherProductVersionLocalService) {
		_patcherProductVersionLocalService = patcherProductVersionLocalService;
	}

	/**
	* Adds the patcher product version to the database. Also notifies the appropriate model listeners.
	*
	* @param patcherProductVersion the patcher product version
	* @return the patcher product version that was added
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherProductVersion addPatcherProductVersion(
		com.liferay.osb.patcher.model.PatcherProductVersion patcherProductVersion)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherProductVersionLocalService.addPatcherProductVersion(patcherProductVersion);
	}

	/**
	* Creates a new patcher product version with the primary key. Does not add the patcher product version to the database.
	*
	* @param patcherProductVersionId the primary key for the new patcher product version
	* @return the new patcher product version
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherProductVersion createPatcherProductVersion(
		long patcherProductVersionId) {
		return _patcherProductVersionLocalService.createPatcherProductVersion(patcherProductVersionId);
	}

	/**
	* Deletes the patcher product version with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherProductVersionId the primary key of the patcher product version
	* @return the patcher product version that was removed
	* @throws PortalException if a patcher product version with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherProductVersion deletePatcherProductVersion(
		long patcherProductVersionId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _patcherProductVersionLocalService.deletePatcherProductVersion(patcherProductVersionId);
	}

	/**
	* Deletes the patcher product version from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherProductVersion the patcher product version
	* @return the patcher product version that was removed
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherProductVersion deletePatcherProductVersion(
		com.liferay.osb.patcher.model.PatcherProductVersion patcherProductVersion)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherProductVersionLocalService.deletePatcherProductVersion(patcherProductVersion);
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
	* @throws SystemException if a system exception occurred
	*/
	@Override
	@SuppressWarnings("rawtypes")
	public java.util.List dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherProductVersionLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	* Performs a dynamic query on the database and returns a range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherProductVersionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _patcherProductVersionLocalService.dynamicQuery(dynamicQuery,
			start, end);
	}

	/**
	* Performs a dynamic query on the database and returns an ordered range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherProductVersionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _patcherProductVersionLocalService.dynamicQuery(dynamicQuery,
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
		return _patcherProductVersionLocalService.dynamicQueryCount(dynamicQuery);
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
		return _patcherProductVersionLocalService.dynamicQueryCount(dynamicQuery,
			projection);
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherProductVersion fetchPatcherProductVersion(
		long patcherProductVersionId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherProductVersionLocalService.fetchPatcherProductVersion(patcherProductVersionId);
	}

	/**
	* Returns the patcher product version with the primary key.
	*
	* @param patcherProductVersionId the primary key of the patcher product version
	* @return the patcher product version
	* @throws PortalException if a patcher product version with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherProductVersion getPatcherProductVersion(
		long patcherProductVersionId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _patcherProductVersionLocalService.getPatcherProductVersion(patcherProductVersionId);
	}

	@Override
	public com.liferay.portal.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _patcherProductVersionLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	* Returns a range of all the patcher product versions.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherProductVersionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher product versions
	* @param end the upper bound of the range of patcher product versions (not inclusive)
	* @return the range of patcher product versions
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherProductVersion> getPatcherProductVersions(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherProductVersionLocalService.getPatcherProductVersions(start,
			end);
	}

	/**
	* Returns the number of patcher product versions.
	*
	* @return the number of patcher product versions
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public int getPatcherProductVersionsCount()
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherProductVersionLocalService.getPatcherProductVersionsCount();
	}

	/**
	* Updates the patcher product version in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param patcherProductVersion the patcher product version
	* @return the patcher product version that was updated
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherProductVersion updatePatcherProductVersion(
		com.liferay.osb.patcher.model.PatcherProductVersion patcherProductVersion)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherProductVersionLocalService.updatePatcherProductVersion(patcherProductVersion);
	}

	/**
	* Returns the Spring bean ID for this bean.
	*
	* @return the Spring bean ID for this bean
	*/
	@Override
	public java.lang.String getBeanIdentifier() {
		return _patcherProductVersionLocalService.getBeanIdentifier();
	}

	/**
	* Sets the Spring bean ID for this bean.
	*
	* @param beanIdentifier the Spring bean ID for this bean
	*/
	@Override
	public void setBeanIdentifier(java.lang.String beanIdentifier) {
		_patcherProductVersionLocalService.setBeanIdentifier(beanIdentifier);
	}

	@Override
	public java.lang.Object invokeMethod(java.lang.String name,
		java.lang.String[] parameterTypes, java.lang.Object[] arguments)
		throws java.lang.Throwable {
		return _patcherProductVersionLocalService.invokeMethod(name,
			parameterTypes, arguments);
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedService}
	 */
	public PatcherProductVersionLocalService getWrappedPatcherProductVersionLocalService() {
		return _patcherProductVersionLocalService;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #setWrappedService}
	 */
	public void setWrappedPatcherProductVersionLocalService(
		PatcherProductVersionLocalService patcherProductVersionLocalService) {
		_patcherProductVersionLocalService = patcherProductVersionLocalService;
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

	private PatcherProductVersionLocalService _patcherProductVersionLocalService;
}
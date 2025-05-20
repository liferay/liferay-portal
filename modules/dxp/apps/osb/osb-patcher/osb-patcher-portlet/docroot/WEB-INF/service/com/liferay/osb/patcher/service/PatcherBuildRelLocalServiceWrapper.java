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
 * Provides a wrapper for {@link PatcherBuildRelLocalService}.
 *
 * @author Calvin Keum
 * @see PatcherBuildRelLocalService
 * @generated
 */
public class PatcherBuildRelLocalServiceWrapper
	implements PatcherBuildRelLocalService,
		ServiceWrapper<PatcherBuildRelLocalService> {
	public PatcherBuildRelLocalServiceWrapper(
		PatcherBuildRelLocalService patcherBuildRelLocalService) {
		_patcherBuildRelLocalService = patcherBuildRelLocalService;
	}

	/**
	* Adds the patcher build rel to the database. Also notifies the appropriate model listeners.
	*
	* @param patcherBuildRel the patcher build rel
	* @return the patcher build rel that was added
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherBuildRel addPatcherBuildRel(
		com.liferay.osb.patcher.model.PatcherBuildRel patcherBuildRel)
		throws com.liferay.portal.kernel.exception.SystemException {
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
		return _patcherBuildRelLocalService.createPatcherBuildRel(patcherBuildRelId);
	}

	/**
	* Deletes the patcher build rel with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherBuildRelId the primary key of the patcher build rel
	* @return the patcher build rel that was removed
	* @throws PortalException if a patcher build rel with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherBuildRel deletePatcherBuildRel(
		long patcherBuildRelId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildRelLocalService.deletePatcherBuildRel(patcherBuildRelId);
	}

	/**
	* Deletes the patcher build rel from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherBuildRel the patcher build rel
	* @return the patcher build rel that was removed
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherBuildRel deletePatcherBuildRel(
		com.liferay.osb.patcher.model.PatcherBuildRel patcherBuildRel)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildRelLocalService.deletePatcherBuildRel(patcherBuildRel);
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
	* @throws SystemException if a system exception occurred
	*/
	@Override
	@SuppressWarnings("rawtypes")
	public java.util.List dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildRelLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	* Performs a dynamic query on the database and returns a range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherBuildRelModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _patcherBuildRelLocalService.dynamicQuery(dynamicQuery, start,
			end);
	}

	/**
	* Performs a dynamic query on the database and returns an ordered range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherBuildRelModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _patcherBuildRelLocalService.dynamicQuery(dynamicQuery, start,
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
		return _patcherBuildRelLocalService.dynamicQueryCount(dynamicQuery);
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
		return _patcherBuildRelLocalService.dynamicQueryCount(dynamicQuery,
			projection);
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherBuildRel fetchPatcherBuildRel(
		long patcherBuildRelId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildRelLocalService.fetchPatcherBuildRel(patcherBuildRelId);
	}

	/**
	* Returns the patcher build rel with the primary key.
	*
	* @param patcherBuildRelId the primary key of the patcher build rel
	* @return the patcher build rel
	* @throws PortalException if a patcher build rel with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherBuildRel getPatcherBuildRel(
		long patcherBuildRelId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildRelLocalService.getPatcherBuildRel(patcherBuildRelId);
	}

	@Override
	public com.liferay.portal.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildRelLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	* Returns a range of all the patcher build rels.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherBuildRelModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher build rels
	* @param end the upper bound of the range of patcher build rels (not inclusive)
	* @return the range of patcher build rels
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherBuildRel> getPatcherBuildRels(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildRelLocalService.getPatcherBuildRels(start, end);
	}

	/**
	* Returns the number of patcher build rels.
	*
	* @return the number of patcher build rels
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public int getPatcherBuildRelsCount()
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildRelLocalService.getPatcherBuildRelsCount();
	}

	/**
	* Updates the patcher build rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param patcherBuildRel the patcher build rel
	* @return the patcher build rel that was updated
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherBuildRel updatePatcherBuildRel(
		com.liferay.osb.patcher.model.PatcherBuildRel patcherBuildRel)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildRelLocalService.updatePatcherBuildRel(patcherBuildRel);
	}

	/**
	* Returns the Spring bean ID for this bean.
	*
	* @return the Spring bean ID for this bean
	*/
	@Override
	public java.lang.String getBeanIdentifier() {
		return _patcherBuildRelLocalService.getBeanIdentifier();
	}

	/**
	* Sets the Spring bean ID for this bean.
	*
	* @param beanIdentifier the Spring bean ID for this bean
	*/
	@Override
	public void setBeanIdentifier(java.lang.String beanIdentifier) {
		_patcherBuildRelLocalService.setBeanIdentifier(beanIdentifier);
	}

	@Override
	public java.lang.Object invokeMethod(java.lang.String name,
		java.lang.String[] parameterTypes, java.lang.Object[] arguments)
		throws java.lang.Throwable {
		return _patcherBuildRelLocalService.invokeMethod(name, parameterTypes,
			arguments);
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedService}
	 */
	public PatcherBuildRelLocalService getWrappedPatcherBuildRelLocalService() {
		return _patcherBuildRelLocalService;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #setWrappedService}
	 */
	public void setWrappedPatcherBuildRelLocalService(
		PatcherBuildRelLocalService patcherBuildRelLocalService) {
		_patcherBuildRelLocalService = patcherBuildRelLocalService;
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
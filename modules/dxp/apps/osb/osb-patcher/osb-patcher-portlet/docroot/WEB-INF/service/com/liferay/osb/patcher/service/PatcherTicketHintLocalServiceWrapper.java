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
 * Provides a wrapper for {@link PatcherTicketHintLocalService}.
 *
 * @author Calvin Keum
 * @see PatcherTicketHintLocalService
 * @generated
 */
public class PatcherTicketHintLocalServiceWrapper
	implements PatcherTicketHintLocalService,
		ServiceWrapper<PatcherTicketHintLocalService> {
	public PatcherTicketHintLocalServiceWrapper(
		PatcherTicketHintLocalService patcherTicketHintLocalService) {
		_patcherTicketHintLocalService = patcherTicketHintLocalService;
	}

	/**
	* Adds the patcher ticket hint to the database. Also notifies the appropriate model listeners.
	*
	* @param patcherTicketHint the patcher ticket hint
	* @return the patcher ticket hint that was added
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherTicketHint addPatcherTicketHint(
		com.liferay.osb.patcher.model.PatcherTicketHint patcherTicketHint)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherTicketHintLocalService.addPatcherTicketHint(patcherTicketHint);
	}

	/**
	* Creates a new patcher ticket hint with the primary key. Does not add the patcher ticket hint to the database.
	*
	* @param patcherTicketHintId the primary key for the new patcher ticket hint
	* @return the new patcher ticket hint
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherTicketHint createPatcherTicketHint(
		long patcherTicketHintId) {
		return _patcherTicketHintLocalService.createPatcherTicketHint(patcherTicketHintId);
	}

	/**
	* Deletes the patcher ticket hint with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherTicketHintId the primary key of the patcher ticket hint
	* @return the patcher ticket hint that was removed
	* @throws PortalException if a patcher ticket hint with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherTicketHint deletePatcherTicketHint(
		long patcherTicketHintId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _patcherTicketHintLocalService.deletePatcherTicketHint(patcherTicketHintId);
	}

	/**
	* Deletes the patcher ticket hint from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherTicketHint the patcher ticket hint
	* @return the patcher ticket hint that was removed
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherTicketHint deletePatcherTicketHint(
		com.liferay.osb.patcher.model.PatcherTicketHint patcherTicketHint)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherTicketHintLocalService.deletePatcherTicketHint(patcherTicketHint);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _patcherTicketHintLocalService.dynamicQuery();
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
		return _patcherTicketHintLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	* Performs a dynamic query on the database and returns a range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherTicketHintModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _patcherTicketHintLocalService.dynamicQuery(dynamicQuery, start,
			end);
	}

	/**
	* Performs a dynamic query on the database and returns an ordered range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherTicketHintModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _patcherTicketHintLocalService.dynamicQuery(dynamicQuery, start,
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
		return _patcherTicketHintLocalService.dynamicQueryCount(dynamicQuery);
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
		return _patcherTicketHintLocalService.dynamicQueryCount(dynamicQuery,
			projection);
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherTicketHint fetchPatcherTicketHint(
		long patcherTicketHintId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherTicketHintLocalService.fetchPatcherTicketHint(patcherTicketHintId);
	}

	/**
	* Returns the patcher ticket hint with the primary key.
	*
	* @param patcherTicketHintId the primary key of the patcher ticket hint
	* @return the patcher ticket hint
	* @throws PortalException if a patcher ticket hint with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherTicketHint getPatcherTicketHint(
		long patcherTicketHintId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _patcherTicketHintLocalService.getPatcherTicketHint(patcherTicketHintId);
	}

	@Override
	public com.liferay.portal.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _patcherTicketHintLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	* Returns a range of all the patcher ticket hints.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherTicketHintModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher ticket hints
	* @param end the upper bound of the range of patcher ticket hints (not inclusive)
	* @return the range of patcher ticket hints
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherTicketHint> getPatcherTicketHints(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherTicketHintLocalService.getPatcherTicketHints(start, end);
	}

	/**
	* Returns the number of patcher ticket hints.
	*
	* @return the number of patcher ticket hints
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public int getPatcherTicketHintsCount()
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherTicketHintLocalService.getPatcherTicketHintsCount();
	}

	/**
	* Updates the patcher ticket hint in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param patcherTicketHint the patcher ticket hint
	* @return the patcher ticket hint that was updated
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherTicketHint updatePatcherTicketHint(
		com.liferay.osb.patcher.model.PatcherTicketHint patcherTicketHint)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherTicketHintLocalService.updatePatcherTicketHint(patcherTicketHint);
	}

	/**
	* Returns the Spring bean ID for this bean.
	*
	* @return the Spring bean ID for this bean
	*/
	@Override
	public java.lang.String getBeanIdentifier() {
		return _patcherTicketHintLocalService.getBeanIdentifier();
	}

	/**
	* Sets the Spring bean ID for this bean.
	*
	* @param beanIdentifier the Spring bean ID for this bean
	*/
	@Override
	public void setBeanIdentifier(java.lang.String beanIdentifier) {
		_patcherTicketHintLocalService.setBeanIdentifier(beanIdentifier);
	}

	@Override
	public java.lang.Object invokeMethod(java.lang.String name,
		java.lang.String[] parameterTypes, java.lang.Object[] arguments)
		throws java.lang.Throwable {
		return _patcherTicketHintLocalService.invokeMethod(name,
			parameterTypes, arguments);
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedService}
	 */
	public PatcherTicketHintLocalService getWrappedPatcherTicketHintLocalService() {
		return _patcherTicketHintLocalService;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #setWrappedService}
	 */
	public void setWrappedPatcherTicketHintLocalService(
		PatcherTicketHintLocalService patcherTicketHintLocalService) {
		_patcherTicketHintLocalService = patcherTicketHintLocalService;
	}

	@Override
	public PatcherTicketHintLocalService getWrappedService() {
		return _patcherTicketHintLocalService;
	}

	@Override
	public void setWrappedService(
		PatcherTicketHintLocalService patcherTicketHintLocalService) {
		_patcherTicketHintLocalService = patcherTicketHintLocalService;
	}

	private PatcherTicketHintLocalService _patcherTicketHintLocalService;
}
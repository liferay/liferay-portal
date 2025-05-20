/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service;

import com.liferay.portal.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link PatcherFixRelLocalService}.
 *
 * @author Calvin Keum
 * @see PatcherFixRelLocalService
 * @generated
 */
public class PatcherFixRelLocalServiceWrapper
	implements PatcherFixRelLocalService,
		ServiceWrapper<PatcherFixRelLocalService> {
	public PatcherFixRelLocalServiceWrapper(
		PatcherFixRelLocalService patcherFixRelLocalService) {
		_patcherFixRelLocalService = patcherFixRelLocalService;
	}

	/**
	* Adds the patcher fix rel to the database. Also notifies the appropriate model listeners.
	*
	* @param patcherFixRel the patcher fix rel
	* @return the patcher fix rel that was added
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherFixRel addPatcherFixRel(
		com.liferay.osb.patcher.model.PatcherFixRel patcherFixRel)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixRelLocalService.addPatcherFixRel(patcherFixRel);
	}

	/**
	* Creates a new patcher fix rel with the primary key. Does not add the patcher fix rel to the database.
	*
	* @param patcherFixRelId the primary key for the new patcher fix rel
	* @return the new patcher fix rel
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherFixRel createPatcherFixRel(
		long patcherFixRelId) {
		return _patcherFixRelLocalService.createPatcherFixRel(patcherFixRelId);
	}

	/**
	* Deletes the patcher fix rel with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherFixRelId the primary key of the patcher fix rel
	* @return the patcher fix rel that was removed
	* @throws PortalException if a patcher fix rel with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherFixRel deletePatcherFixRel(
		long patcherFixRelId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixRelLocalService.deletePatcherFixRel(patcherFixRelId);
	}

	/**
	* Deletes the patcher fix rel from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherFixRel the patcher fix rel
	* @return the patcher fix rel that was removed
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherFixRel deletePatcherFixRel(
		com.liferay.osb.patcher.model.PatcherFixRel patcherFixRel)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixRelLocalService.deletePatcherFixRel(patcherFixRel);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _patcherFixRelLocalService.dynamicQuery();
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
		return _patcherFixRelLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	* Performs a dynamic query on the database and returns a range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixRelModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _patcherFixRelLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	* Performs a dynamic query on the database and returns an ordered range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixRelModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _patcherFixRelLocalService.dynamicQuery(dynamicQuery, start,
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
		return _patcherFixRelLocalService.dynamicQueryCount(dynamicQuery);
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
		return _patcherFixRelLocalService.dynamicQueryCount(dynamicQuery,
			projection);
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherFixRel fetchPatcherFixRel(
		long patcherFixRelId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixRelLocalService.fetchPatcherFixRel(patcherFixRelId);
	}

	/**
	* Returns the patcher fix rel with the primary key.
	*
	* @param patcherFixRelId the primary key of the patcher fix rel
	* @return the patcher fix rel
	* @throws PortalException if a patcher fix rel with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherFixRel getPatcherFixRel(
		long patcherFixRelId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixRelLocalService.getPatcherFixRel(patcherFixRelId);
	}

	@Override
	public com.liferay.portal.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixRelLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	* Returns a range of all the patcher fix rels.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixRelModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher fix rels
	* @param end the upper bound of the range of patcher fix rels (not inclusive)
	* @return the range of patcher fix rels
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherFixRel> getPatcherFixRels(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixRelLocalService.getPatcherFixRels(start, end);
	}

	/**
	* Returns the number of patcher fix rels.
	*
	* @return the number of patcher fix rels
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public int getPatcherFixRelsCount()
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixRelLocalService.getPatcherFixRelsCount();
	}

	/**
	* Updates the patcher fix rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param patcherFixRel the patcher fix rel
	* @return the patcher fix rel that was updated
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherFixRel updatePatcherFixRel(
		com.liferay.osb.patcher.model.PatcherFixRel patcherFixRel)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixRelLocalService.updatePatcherFixRel(patcherFixRel);
	}

	/**
	* Returns the Spring bean ID for this bean.
	*
	* @return the Spring bean ID for this bean
	*/
	@Override
	public java.lang.String getBeanIdentifier() {
		return _patcherFixRelLocalService.getBeanIdentifier();
	}

	/**
	* Sets the Spring bean ID for this bean.
	*
	* @param beanIdentifier the Spring bean ID for this bean
	*/
	@Override
	public void setBeanIdentifier(java.lang.String beanIdentifier) {
		_patcherFixRelLocalService.setBeanIdentifier(beanIdentifier);
	}

	@Override
	public java.lang.Object invokeMethod(java.lang.String name,
		java.lang.String[] parameterTypes, java.lang.Object[] arguments)
		throws java.lang.Throwable {
		return _patcherFixRelLocalService.invokeMethod(name, parameterTypes,
			arguments);
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedService}
	 */
	public PatcherFixRelLocalService getWrappedPatcherFixRelLocalService() {
		return _patcherFixRelLocalService;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #setWrappedService}
	 */
	public void setWrappedPatcherFixRelLocalService(
		PatcherFixRelLocalService patcherFixRelLocalService) {
		_patcherFixRelLocalService = patcherFixRelLocalService;
	}

	@Override
	public PatcherFixRelLocalService getWrappedService() {
		return _patcherFixRelLocalService;
	}

	@Override
	public void setWrappedService(
		PatcherFixRelLocalService patcherFixRelLocalService) {
		_patcherFixRelLocalService = patcherFixRelLocalService;
	}

	private PatcherFixRelLocalService _patcherFixRelLocalService;
}
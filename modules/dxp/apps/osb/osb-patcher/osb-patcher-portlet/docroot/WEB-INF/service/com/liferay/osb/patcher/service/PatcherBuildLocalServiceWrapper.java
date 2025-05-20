/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service;

import com.liferay.portal.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link PatcherBuildLocalService}.
 *
 * @author Calvin Keum
 * @see PatcherBuildLocalService
 * @generated
 */
public class PatcherBuildLocalServiceWrapper implements PatcherBuildLocalService,
	ServiceWrapper<PatcherBuildLocalService> {
	public PatcherBuildLocalServiceWrapper(
		PatcherBuildLocalService patcherBuildLocalService) {
		_patcherBuildLocalService = patcherBuildLocalService;
	}

	/**
	* Adds the patcher build to the database. Also notifies the appropriate model listeners.
	*
	* @param patcherBuild the patcher build
	* @return the patcher build that was added
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherBuild addPatcherBuild(
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildLocalService.addPatcherBuild(patcherBuild);
	}

	/**
	* Creates a new patcher build with the primary key. Does not add the patcher build to the database.
	*
	* @param patcherBuildId the primary key for the new patcher build
	* @return the new patcher build
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherBuild createPatcherBuild(
		long patcherBuildId) {
		return _patcherBuildLocalService.createPatcherBuild(patcherBuildId);
	}

	/**
	* Deletes the patcher build with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherBuildId the primary key of the patcher build
	* @return the patcher build that was removed
	* @throws PortalException if a patcher build with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherBuild deletePatcherBuild(
		long patcherBuildId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildLocalService.deletePatcherBuild(patcherBuildId);
	}

	/**
	* Deletes the patcher build from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherBuild the patcher build
	* @return the patcher build that was removed
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherBuild deletePatcherBuild(
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildLocalService.deletePatcherBuild(patcherBuild);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _patcherBuildLocalService.dynamicQuery();
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
		return _patcherBuildLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	* Performs a dynamic query on the database and returns a range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _patcherBuildLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	* Performs a dynamic query on the database and returns an ordered range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _patcherBuildLocalService.dynamicQuery(dynamicQuery, start, end,
			orderByComparator);
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
		return _patcherBuildLocalService.dynamicQueryCount(dynamicQuery);
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
		return _patcherBuildLocalService.dynamicQueryCount(dynamicQuery,
			projection);
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherBuild fetchPatcherBuild(
		long patcherBuildId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildLocalService.fetchPatcherBuild(patcherBuildId);
	}

	/**
	* Returns the patcher build with the primary key.
	*
	* @param patcherBuildId the primary key of the patcher build
	* @return the patcher build
	* @throws PortalException if a patcher build with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherBuild getPatcherBuild(
		long patcherBuildId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildLocalService.getPatcherBuild(patcherBuildId);
	}

	@Override
	public com.liferay.portal.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	* Returns a range of all the patcher builds.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher builds
	* @param end the upper bound of the range of patcher builds (not inclusive)
	* @return the range of patcher builds
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherBuilds(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildLocalService.getPatcherBuilds(start, end);
	}

	/**
	* Returns the number of patcher builds.
	*
	* @return the number of patcher builds
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public int getPatcherBuildsCount()
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildLocalService.getPatcherBuildsCount();
	}

	/**
	* Updates the patcher build in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param patcherBuild the patcher build
	* @return the patcher build that was updated
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherBuild updatePatcherBuild(
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildLocalService.updatePatcherBuild(patcherBuild);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void addPatcherAccountPatcherBuild(long patcherAccountId,
		long patcherBuildId)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherBuildLocalService.addPatcherAccountPatcherBuild(patcherAccountId,
			patcherBuildId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void addPatcherAccountPatcherBuild(long patcherAccountId,
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherBuildLocalService.addPatcherAccountPatcherBuild(patcherAccountId,
			patcherBuild);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void addPatcherAccountPatcherBuilds(long patcherAccountId,
		long[] patcherBuildIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherBuildLocalService.addPatcherAccountPatcherBuilds(patcherAccountId,
			patcherBuildIds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void addPatcherAccountPatcherBuilds(long patcherAccountId,
		java.util.List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherBuildLocalService.addPatcherAccountPatcherBuilds(patcherAccountId,
			patcherBuilds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void clearPatcherAccountPatcherBuilds(long patcherAccountId)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherBuildLocalService.clearPatcherAccountPatcherBuilds(patcherAccountId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void deletePatcherAccountPatcherBuild(long patcherAccountId,
		long patcherBuildId)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherBuildLocalService.deletePatcherAccountPatcherBuild(patcherAccountId,
			patcherBuildId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void deletePatcherAccountPatcherBuild(long patcherAccountId,
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherBuildLocalService.deletePatcherAccountPatcherBuild(patcherAccountId,
			patcherBuild);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void deletePatcherAccountPatcherBuilds(long patcherAccountId,
		long[] patcherBuildIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherBuildLocalService.deletePatcherAccountPatcherBuilds(patcherAccountId,
			patcherBuildIds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void deletePatcherAccountPatcherBuilds(long patcherAccountId,
		java.util.List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherBuildLocalService.deletePatcherAccountPatcherBuilds(patcherAccountId,
			patcherBuilds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherAccountPatcherBuilds(
		long patcherAccountId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildLocalService.getPatcherAccountPatcherBuilds(patcherAccountId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherAccountPatcherBuilds(
		long patcherAccountId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildLocalService.getPatcherAccountPatcherBuilds(patcherAccountId,
			start, end);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherAccountPatcherBuilds(
		long patcherAccountId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildLocalService.getPatcherAccountPatcherBuilds(patcherAccountId,
			start, end, orderByComparator);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public int getPatcherAccountPatcherBuildsCount(long patcherAccountId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildLocalService.getPatcherAccountPatcherBuildsCount(patcherAccountId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public boolean hasPatcherAccountPatcherBuild(long patcherAccountId,
		long patcherBuildId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildLocalService.hasPatcherAccountPatcherBuild(patcherAccountId,
			patcherBuildId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public boolean hasPatcherAccountPatcherBuilds(long patcherAccountId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildLocalService.hasPatcherAccountPatcherBuilds(patcherAccountId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void setPatcherAccountPatcherBuilds(long patcherAccountId,
		long[] patcherBuildIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherBuildLocalService.setPatcherAccountPatcherBuilds(patcherAccountId,
			patcherBuildIds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void addPatcherFixPatcherBuild(long patcherFixId, long patcherBuildId)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherBuildLocalService.addPatcherFixPatcherBuild(patcherFixId,
			patcherBuildId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void addPatcherFixPatcherBuild(long patcherFixId,
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherBuildLocalService.addPatcherFixPatcherBuild(patcherFixId,
			patcherBuild);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void addPatcherFixPatcherBuilds(long patcherFixId,
		long[] patcherBuildIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherBuildLocalService.addPatcherFixPatcherBuilds(patcherFixId,
			patcherBuildIds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void addPatcherFixPatcherBuilds(long patcherFixId,
		java.util.List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherBuildLocalService.addPatcherFixPatcherBuilds(patcherFixId,
			patcherBuilds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void clearPatcherFixPatcherBuilds(long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherBuildLocalService.clearPatcherFixPatcherBuilds(patcherFixId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void deletePatcherFixPatcherBuild(long patcherFixId,
		long patcherBuildId)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherBuildLocalService.deletePatcherFixPatcherBuild(patcherFixId,
			patcherBuildId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void deletePatcherFixPatcherBuild(long patcherFixId,
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherBuildLocalService.deletePatcherFixPatcherBuild(patcherFixId,
			patcherBuild);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void deletePatcherFixPatcherBuilds(long patcherFixId,
		long[] patcherBuildIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherBuildLocalService.deletePatcherFixPatcherBuilds(patcherFixId,
			patcherBuildIds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void deletePatcherFixPatcherBuilds(long patcherFixId,
		java.util.List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherBuildLocalService.deletePatcherFixPatcherBuilds(patcherFixId,
			patcherBuilds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherFixPatcherBuilds(
		long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildLocalService.getPatcherFixPatcherBuilds(patcherFixId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherFixPatcherBuilds(
		long patcherFixId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildLocalService.getPatcherFixPatcherBuilds(patcherFixId,
			start, end);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherFixPatcherBuilds(
		long patcherFixId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildLocalService.getPatcherFixPatcherBuilds(patcherFixId,
			start, end, orderByComparator);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public int getPatcherFixPatcherBuildsCount(long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildLocalService.getPatcherFixPatcherBuildsCount(patcherFixId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public boolean hasPatcherFixPatcherBuild(long patcherFixId,
		long patcherBuildId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildLocalService.hasPatcherFixPatcherBuild(patcherFixId,
			patcherBuildId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public boolean hasPatcherFixPatcherBuilds(long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuildLocalService.hasPatcherFixPatcherBuilds(patcherFixId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void setPatcherFixPatcherBuilds(long patcherFixId,
		long[] patcherBuildIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherBuildLocalService.setPatcherFixPatcherBuilds(patcherFixId,
			patcherBuildIds);
	}

	/**
	* Returns the Spring bean ID for this bean.
	*
	* @return the Spring bean ID for this bean
	*/
	@Override
	public java.lang.String getBeanIdentifier() {
		return _patcherBuildLocalService.getBeanIdentifier();
	}

	/**
	* Sets the Spring bean ID for this bean.
	*
	* @param beanIdentifier the Spring bean ID for this bean
	*/
	@Override
	public void setBeanIdentifier(java.lang.String beanIdentifier) {
		_patcherBuildLocalService.setBeanIdentifier(beanIdentifier);
	}

	@Override
	public java.lang.Object invokeMethod(java.lang.String name,
		java.lang.String[] parameterTypes, java.lang.Object[] arguments)
		throws java.lang.Throwable {
		return _patcherBuildLocalService.invokeMethod(name, parameterTypes,
			arguments);
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedService}
	 */
	public PatcherBuildLocalService getWrappedPatcherBuildLocalService() {
		return _patcherBuildLocalService;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #setWrappedService}
	 */
	public void setWrappedPatcherBuildLocalService(
		PatcherBuildLocalService patcherBuildLocalService) {
		_patcherBuildLocalService = patcherBuildLocalService;
	}

	@Override
	public PatcherBuildLocalService getWrappedService() {
		return _patcherBuildLocalService;
	}

	@Override
	public void setWrappedService(
		PatcherBuildLocalService patcherBuildLocalService) {
		_patcherBuildLocalService = patcherBuildLocalService;
	}

	private PatcherBuildLocalService _patcherBuildLocalService;
}
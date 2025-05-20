/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service;

import com.liferay.portal.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link PatcherFixLocalService}.
 *
 * @author Calvin Keum
 * @see PatcherFixLocalService
 * @generated
 */
public class PatcherFixLocalServiceWrapper implements PatcherFixLocalService,
	ServiceWrapper<PatcherFixLocalService> {
	public PatcherFixLocalServiceWrapper(
		PatcherFixLocalService patcherFixLocalService) {
		_patcherFixLocalService = patcherFixLocalService;
	}

	/**
	* Adds the patcher fix to the database. Also notifies the appropriate model listeners.
	*
	* @param patcherFix the patcher fix
	* @return the patcher fix that was added
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherFix addPatcherFix(
		com.liferay.osb.patcher.model.PatcherFix patcherFix)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixLocalService.addPatcherFix(patcherFix);
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
	* Deletes the patcher fix with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherFixId the primary key of the patcher fix
	* @return the patcher fix that was removed
	* @throws PortalException if a patcher fix with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherFix deletePatcherFix(
		long patcherFixId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixLocalService.deletePatcherFix(patcherFixId);
	}

	/**
	* Deletes the patcher fix from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherFix the patcher fix
	* @return the patcher fix that was removed
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherFix deletePatcherFix(
		com.liferay.osb.patcher.model.PatcherFix patcherFix)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixLocalService.deletePatcherFix(patcherFix);
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
	* @throws SystemException if a system exception occurred
	*/
	@Override
	@SuppressWarnings("rawtypes")
	public java.util.List dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	* Performs a dynamic query on the database and returns a range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _patcherFixLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	* Performs a dynamic query on the database and returns an ordered range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _patcherFixLocalService.dynamicQuery(dynamicQuery, start, end,
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
		return _patcherFixLocalService.dynamicQueryCount(dynamicQuery);
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
		return _patcherFixLocalService.dynamicQueryCount(dynamicQuery,
			projection);
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherFix fetchPatcherFix(
		long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixLocalService.fetchPatcherFix(patcherFixId);
	}

	/**
	* Returns the patcher fix with the primary key.
	*
	* @param patcherFixId the primary key of the patcher fix
	* @return the patcher fix
	* @throws PortalException if a patcher fix with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherFix getPatcherFix(
		long patcherFixId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixLocalService.getPatcherFix(patcherFixId);
	}

	@Override
	public com.liferay.portal.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	* Returns a range of all the patcher fixs.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher fixs
	* @param end the upper bound of the range of patcher fixs (not inclusive)
	* @return the range of patcher fixs
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherFix> getPatcherFixs(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixLocalService.getPatcherFixs(start, end);
	}

	/**
	* Returns the number of patcher fixs.
	*
	* @return the number of patcher fixs
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public int getPatcherFixsCount()
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixLocalService.getPatcherFixsCount();
	}

	/**
	* Updates the patcher fix in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param patcherFix the patcher fix
	* @return the patcher fix that was updated
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherFix updatePatcherFix(
		com.liferay.osb.patcher.model.PatcherFix patcherFix)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixLocalService.updatePatcherFix(patcherFix);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void addPatcherBuildPatcherFix(long patcherBuildId, long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixLocalService.addPatcherBuildPatcherFix(patcherBuildId,
			patcherFixId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void addPatcherBuildPatcherFix(long patcherBuildId,
		com.liferay.osb.patcher.model.PatcherFix patcherFix)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixLocalService.addPatcherBuildPatcherFix(patcherBuildId,
			patcherFix);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void addPatcherBuildPatcherFixs(long patcherBuildId,
		long[] patcherFixIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixLocalService.addPatcherBuildPatcherFixs(patcherBuildId,
			patcherFixIds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void addPatcherBuildPatcherFixs(long patcherBuildId,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixs)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixLocalService.addPatcherBuildPatcherFixs(patcherBuildId,
			patcherFixs);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void clearPatcherBuildPatcherFixs(long patcherBuildId)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixLocalService.clearPatcherBuildPatcherFixs(patcherBuildId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void deletePatcherBuildPatcherFix(long patcherBuildId,
		long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixLocalService.deletePatcherBuildPatcherFix(patcherBuildId,
			patcherFixId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void deletePatcherBuildPatcherFix(long patcherBuildId,
		com.liferay.osb.patcher.model.PatcherFix patcherFix)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixLocalService.deletePatcherBuildPatcherFix(patcherBuildId,
			patcherFix);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void deletePatcherBuildPatcherFixs(long patcherBuildId,
		long[] patcherFixIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixLocalService.deletePatcherBuildPatcherFixs(patcherBuildId,
			patcherFixIds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void deletePatcherBuildPatcherFixs(long patcherBuildId,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixs)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixLocalService.deletePatcherBuildPatcherFixs(patcherBuildId,
			patcherFixs);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherFix> getPatcherBuildPatcherFixs(
		long patcherBuildId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixLocalService.getPatcherBuildPatcherFixs(patcherBuildId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherFix> getPatcherBuildPatcherFixs(
		long patcherBuildId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixLocalService.getPatcherBuildPatcherFixs(patcherBuildId,
			start, end);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherFix> getPatcherBuildPatcherFixs(
		long patcherBuildId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixLocalService.getPatcherBuildPatcherFixs(patcherBuildId,
			start, end, orderByComparator);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public int getPatcherBuildPatcherFixsCount(long patcherBuildId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixLocalService.getPatcherBuildPatcherFixsCount(patcherBuildId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public boolean hasPatcherBuildPatcherFix(long patcherBuildId,
		long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixLocalService.hasPatcherBuildPatcherFix(patcherBuildId,
			patcherFixId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public boolean hasPatcherBuildPatcherFixs(long patcherBuildId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixLocalService.hasPatcherBuildPatcherFixs(patcherBuildId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void setPatcherBuildPatcherFixs(long patcherBuildId,
		long[] patcherFixIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixLocalService.setPatcherBuildPatcherFixs(patcherBuildId,
			patcherFixIds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void addPatcherFixPackPatcherFix(long patcherFixPackId,
		long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixLocalService.addPatcherFixPackPatcherFix(patcherFixPackId,
			patcherFixId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void addPatcherFixPackPatcherFix(long patcherFixPackId,
		com.liferay.osb.patcher.model.PatcherFix patcherFix)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixLocalService.addPatcherFixPackPatcherFix(patcherFixPackId,
			patcherFix);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void addPatcherFixPackPatcherFixs(long patcherFixPackId,
		long[] patcherFixIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixLocalService.addPatcherFixPackPatcherFixs(patcherFixPackId,
			patcherFixIds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void addPatcherFixPackPatcherFixs(long patcherFixPackId,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixs)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixLocalService.addPatcherFixPackPatcherFixs(patcherFixPackId,
			patcherFixs);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void clearPatcherFixPackPatcherFixs(long patcherFixPackId)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixLocalService.clearPatcherFixPackPatcherFixs(patcherFixPackId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void deletePatcherFixPackPatcherFix(long patcherFixPackId,
		long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixLocalService.deletePatcherFixPackPatcherFix(patcherFixPackId,
			patcherFixId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void deletePatcherFixPackPatcherFix(long patcherFixPackId,
		com.liferay.osb.patcher.model.PatcherFix patcherFix)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixLocalService.deletePatcherFixPackPatcherFix(patcherFixPackId,
			patcherFix);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void deletePatcherFixPackPatcherFixs(long patcherFixPackId,
		long[] patcherFixIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixLocalService.deletePatcherFixPackPatcherFixs(patcherFixPackId,
			patcherFixIds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void deletePatcherFixPackPatcherFixs(long patcherFixPackId,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixs)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixLocalService.deletePatcherFixPackPatcherFixs(patcherFixPackId,
			patcherFixs);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherFix> getPatcherFixPackPatcherFixs(
		long patcherFixPackId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixLocalService.getPatcherFixPackPatcherFixs(patcherFixPackId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherFix> getPatcherFixPackPatcherFixs(
		long patcherFixPackId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixLocalService.getPatcherFixPackPatcherFixs(patcherFixPackId,
			start, end);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherFix> getPatcherFixPackPatcherFixs(
		long patcherFixPackId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixLocalService.getPatcherFixPackPatcherFixs(patcherFixPackId,
			start, end, orderByComparator);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public int getPatcherFixPackPatcherFixsCount(long patcherFixPackId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixLocalService.getPatcherFixPackPatcherFixsCount(patcherFixPackId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public boolean hasPatcherFixPackPatcherFix(long patcherFixPackId,
		long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixLocalService.hasPatcherFixPackPatcherFix(patcherFixPackId,
			patcherFixId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public boolean hasPatcherFixPackPatcherFixs(long patcherFixPackId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixLocalService.hasPatcherFixPackPatcherFixs(patcherFixPackId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void setPatcherFixPackPatcherFixs(long patcherFixPackId,
		long[] patcherFixIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixLocalService.setPatcherFixPackPatcherFixs(patcherFixPackId,
			patcherFixIds);
	}

	/**
	* Returns the Spring bean ID for this bean.
	*
	* @return the Spring bean ID for this bean
	*/
	@Override
	public java.lang.String getBeanIdentifier() {
		return _patcherFixLocalService.getBeanIdentifier();
	}

	/**
	* Sets the Spring bean ID for this bean.
	*
	* @param beanIdentifier the Spring bean ID for this bean
	*/
	@Override
	public void setBeanIdentifier(java.lang.String beanIdentifier) {
		_patcherFixLocalService.setBeanIdentifier(beanIdentifier);
	}

	@Override
	public java.lang.Object invokeMethod(java.lang.String name,
		java.lang.String[] parameterTypes, java.lang.Object[] arguments)
		throws java.lang.Throwable {
		return _patcherFixLocalService.invokeMethod(name, parameterTypes,
			arguments);
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedService}
	 */
	public PatcherFixLocalService getWrappedPatcherFixLocalService() {
		return _patcherFixLocalService;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #setWrappedService}
	 */
	public void setWrappedPatcherFixLocalService(
		PatcherFixLocalService patcherFixLocalService) {
		_patcherFixLocalService = patcherFixLocalService;
	}

	@Override
	public PatcherFixLocalService getWrappedService() {
		return _patcherFixLocalService;
	}

	@Override
	public void setWrappedService(PatcherFixLocalService patcherFixLocalService) {
		_patcherFixLocalService = patcherFixLocalService;
	}

	private PatcherFixLocalService _patcherFixLocalService;
}
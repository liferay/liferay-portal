/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service;

import com.liferay.portal.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link PatcherFixPackLocalService}.
 *
 * @author Calvin Keum
 * @see PatcherFixPackLocalService
 * @generated
 */
public class PatcherFixPackLocalServiceWrapper
	implements PatcherFixPackLocalService,
		ServiceWrapper<PatcherFixPackLocalService> {
	public PatcherFixPackLocalServiceWrapper(
		PatcherFixPackLocalService patcherFixPackLocalService) {
		_patcherFixPackLocalService = patcherFixPackLocalService;
	}

	/**
	* Adds the patcher fix pack to the database. Also notifies the appropriate model listeners.
	*
	* @param patcherFixPack the patcher fix pack
	* @return the patcher fix pack that was added
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherFixPack addPatcherFixPack(
		com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixPackLocalService.addPatcherFixPack(patcherFixPack);
	}

	/**
	* Creates a new patcher fix pack with the primary key. Does not add the patcher fix pack to the database.
	*
	* @param patcherFixPackId the primary key for the new patcher fix pack
	* @return the new patcher fix pack
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherFixPack createPatcherFixPack(
		long patcherFixPackId) {
		return _patcherFixPackLocalService.createPatcherFixPack(patcherFixPackId);
	}

	/**
	* Deletes the patcher fix pack with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherFixPackId the primary key of the patcher fix pack
	* @return the patcher fix pack that was removed
	* @throws PortalException if a patcher fix pack with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherFixPack deletePatcherFixPack(
		long patcherFixPackId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixPackLocalService.deletePatcherFixPack(patcherFixPackId);
	}

	/**
	* Deletes the patcher fix pack from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherFixPack the patcher fix pack
	* @return the patcher fix pack that was removed
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherFixPack deletePatcherFixPack(
		com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixPackLocalService.deletePatcherFixPack(patcherFixPack);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _patcherFixPackLocalService.dynamicQuery();
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
		return _patcherFixPackLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	* Performs a dynamic query on the database and returns a range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixPackModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _patcherFixPackLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	* Performs a dynamic query on the database and returns an ordered range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixPackModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _patcherFixPackLocalService.dynamicQuery(dynamicQuery, start,
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
		return _patcherFixPackLocalService.dynamicQueryCount(dynamicQuery);
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
		return _patcherFixPackLocalService.dynamicQueryCount(dynamicQuery,
			projection);
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherFixPack fetchPatcherFixPack(
		long patcherFixPackId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixPackLocalService.fetchPatcherFixPack(patcherFixPackId);
	}

	/**
	* Returns the patcher fix pack with the primary key.
	*
	* @param patcherFixPackId the primary key of the patcher fix pack
	* @return the patcher fix pack
	* @throws PortalException if a patcher fix pack with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherFixPack getPatcherFixPack(
		long patcherFixPackId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixPackLocalService.getPatcherFixPack(patcherFixPackId);
	}

	@Override
	public com.liferay.portal.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixPackLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	* Returns a range of all the patcher fix packs.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixPackModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher fix packs
	* @param end the upper bound of the range of patcher fix packs (not inclusive)
	* @return the range of patcher fix packs
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherFixPack> getPatcherFixPacks(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixPackLocalService.getPatcherFixPacks(start, end);
	}

	/**
	* Returns the number of patcher fix packs.
	*
	* @return the number of patcher fix packs
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public int getPatcherFixPacksCount()
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixPackLocalService.getPatcherFixPacksCount();
	}

	/**
	* Updates the patcher fix pack in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param patcherFixPack the patcher fix pack
	* @return the patcher fix pack that was updated
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public com.liferay.osb.patcher.model.PatcherFixPack updatePatcherFixPack(
		com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixPackLocalService.updatePatcherFixPack(patcherFixPack);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void addPatcherFixPatcherFixPack(long patcherFixId,
		long patcherFixPackId)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixPackLocalService.addPatcherFixPatcherFixPack(patcherFixId,
			patcherFixPackId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void addPatcherFixPatcherFixPack(long patcherFixId,
		com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixPackLocalService.addPatcherFixPatcherFixPack(patcherFixId,
			patcherFixPack);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void addPatcherFixPatcherFixPacks(long patcherFixId,
		long[] patcherFixPackIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixPackLocalService.addPatcherFixPatcherFixPacks(patcherFixId,
			patcherFixPackIds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void addPatcherFixPatcherFixPacks(long patcherFixId,
		java.util.List<com.liferay.osb.patcher.model.PatcherFixPack> patcherFixPacks)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixPackLocalService.addPatcherFixPatcherFixPacks(patcherFixId,
			patcherFixPacks);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void clearPatcherFixPatcherFixPacks(long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixPackLocalService.clearPatcherFixPatcherFixPacks(patcherFixId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void deletePatcherFixPatcherFixPack(long patcherFixId,
		long patcherFixPackId)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixPackLocalService.deletePatcherFixPatcherFixPack(patcherFixId,
			patcherFixPackId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void deletePatcherFixPatcherFixPack(long patcherFixId,
		com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixPackLocalService.deletePatcherFixPatcherFixPack(patcherFixId,
			patcherFixPack);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void deletePatcherFixPatcherFixPacks(long patcherFixId,
		long[] patcherFixPackIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixPackLocalService.deletePatcherFixPatcherFixPacks(patcherFixId,
			patcherFixPackIds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void deletePatcherFixPatcherFixPacks(long patcherFixId,
		java.util.List<com.liferay.osb.patcher.model.PatcherFixPack> patcherFixPacks)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixPackLocalService.deletePatcherFixPatcherFixPacks(patcherFixId,
			patcherFixPacks);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherFixPack> getPatcherFixPatcherFixPacks(
		long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixPackLocalService.getPatcherFixPatcherFixPacks(patcherFixId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherFixPack> getPatcherFixPatcherFixPacks(
		long patcherFixId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixPackLocalService.getPatcherFixPatcherFixPacks(patcherFixId,
			start, end);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherFixPack> getPatcherFixPatcherFixPacks(
		long patcherFixId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixPackLocalService.getPatcherFixPatcherFixPacks(patcherFixId,
			start, end, orderByComparator);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public int getPatcherFixPatcherFixPacksCount(long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixPackLocalService.getPatcherFixPatcherFixPacksCount(patcherFixId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public boolean hasPatcherFixPatcherFixPack(long patcherFixId,
		long patcherFixPackId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixPackLocalService.hasPatcherFixPatcherFixPack(patcherFixId,
			patcherFixPackId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public boolean hasPatcherFixPatcherFixPacks(long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherFixPackLocalService.hasPatcherFixPatcherFixPacks(patcherFixId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public void setPatcherFixPatcherFixPacks(long patcherFixId,
		long[] patcherFixPackIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherFixPackLocalService.setPatcherFixPatcherFixPacks(patcherFixId,
			patcherFixPackIds);
	}

	/**
	* Returns the Spring bean ID for this bean.
	*
	* @return the Spring bean ID for this bean
	*/
	@Override
	public java.lang.String getBeanIdentifier() {
		return _patcherFixPackLocalService.getBeanIdentifier();
	}

	/**
	* Sets the Spring bean ID for this bean.
	*
	* @param beanIdentifier the Spring bean ID for this bean
	*/
	@Override
	public void setBeanIdentifier(java.lang.String beanIdentifier) {
		_patcherFixPackLocalService.setBeanIdentifier(beanIdentifier);
	}

	@Override
	public java.lang.Object invokeMethod(java.lang.String name,
		java.lang.String[] parameterTypes, java.lang.Object[] arguments)
		throws java.lang.Throwable {
		return _patcherFixPackLocalService.invokeMethod(name, parameterTypes,
			arguments);
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedService}
	 */
	public PatcherFixPackLocalService getWrappedPatcherFixPackLocalService() {
		return _patcherFixPackLocalService;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #setWrappedService}
	 */
	public void setWrappedPatcherFixPackLocalService(
		PatcherFixPackLocalService patcherFixPackLocalService) {
		_patcherFixPackLocalService = patcherFixPackLocalService;
	}

	@Override
	public PatcherFixPackLocalService getWrappedService() {
		return _patcherFixPackLocalService;
	}

	@Override
	public void setWrappedService(
		PatcherFixPackLocalService patcherFixPackLocalService) {
		_patcherFixPackLocalService = patcherFixPackLocalService;
	}

	private PatcherFixPackLocalService _patcherFixPackLocalService;
}
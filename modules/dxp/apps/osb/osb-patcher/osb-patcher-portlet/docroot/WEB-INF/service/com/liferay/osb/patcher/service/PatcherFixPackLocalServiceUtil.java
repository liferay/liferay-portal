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

import com.liferay.portal.kernel.bean.PortletBeanLocatorUtil;
import com.liferay.portal.kernel.util.ReferenceRegistry;
import com.liferay.portal.service.InvokableLocalService;

/**
 * Provides the local service utility for PatcherFixPack. This utility wraps
 * {@link com.liferay.osb.patcher.service.impl.PatcherFixPackLocalServiceImpl} and is the
 * primary access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Calvin Keum
 * @see PatcherFixPackLocalService
 * @see com.liferay.osb.patcher.service.base.PatcherFixPackLocalServiceBaseImpl
 * @see com.liferay.osb.patcher.service.impl.PatcherFixPackLocalServiceImpl
 * @generated
 */
public class PatcherFixPackLocalServiceUtil {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to {@link com.liferay.osb.patcher.service.impl.PatcherFixPackLocalServiceImpl} and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	* Adds the patcher fix pack to the database. Also notifies the appropriate model listeners.
	*
	* @param patcherFixPack the patcher fix pack
	* @return the patcher fix pack that was added
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherFixPack addPatcherFixPack(
		com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().addPatcherFixPack(patcherFixPack);
	}

	/**
	* Creates a new patcher fix pack with the primary key. Does not add the patcher fix pack to the database.
	*
	* @param patcherFixPackId the primary key for the new patcher fix pack
	* @return the new patcher fix pack
	*/
	public static com.liferay.osb.patcher.model.PatcherFixPack createPatcherFixPack(
		long patcherFixPackId) {
		return getService().createPatcherFixPack(patcherFixPackId);
	}

	/**
	* Deletes the patcher fix pack with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherFixPackId the primary key of the patcher fix pack
	* @return the patcher fix pack that was removed
	* @throws PortalException if a patcher fix pack with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherFixPack deletePatcherFixPack(
		long patcherFixPackId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().deletePatcherFixPack(patcherFixPackId);
	}

	/**
	* Deletes the patcher fix pack from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherFixPack the patcher fix pack
	* @return the patcher fix pack that was removed
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherFixPack deletePatcherFixPack(
		com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().deletePatcherFixPack(patcherFixPack);
	}

	public static com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	* Performs a dynamic query on the database and returns the matching rows.
	*
	* @param dynamicQuery the dynamic query
	* @return the matching rows
	* @throws SystemException if a system exception occurred
	*/
	@SuppressWarnings("rawtypes")
	public static java.util.List dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().dynamicQuery(dynamicQuery);
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
	@SuppressWarnings("rawtypes")
	public static java.util.List dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) throws com.liferay.portal.kernel.exception.SystemException {
		return getService().dynamicQuery(dynamicQuery, start, end);
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
	@SuppressWarnings("rawtypes")
	public static java.util.List dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService()
				   .dynamicQuery(dynamicQuery, start, end, orderByComparator);
	}

	/**
	* Returns the number of rows that match the dynamic query.
	*
	* @param dynamicQuery the dynamic query
	* @return the number of rows that match the dynamic query
	* @throws SystemException if a system exception occurred
	*/
	public static long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	* Returns the number of rows that match the dynamic query.
	*
	* @param dynamicQuery the dynamic query
	* @param projection the projection to apply to the query
	* @return the number of rows that match the dynamic query
	* @throws SystemException if a system exception occurred
	*/
	public static long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static com.liferay.osb.patcher.model.PatcherFixPack fetchPatcherFixPack(
		long patcherFixPackId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().fetchPatcherFixPack(patcherFixPackId);
	}

	/**
	* Returns the patcher fix pack with the primary key.
	*
	* @param patcherFixPackId the primary key of the patcher fix pack
	* @return the patcher fix pack
	* @throws PortalException if a patcher fix pack with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherFixPack getPatcherFixPack(
		long patcherFixPackId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().getPatcherFixPack(patcherFixPackId);
	}

	public static com.liferay.portal.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().getPersistedModel(primaryKeyObj);
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
	public static java.util.List<com.liferay.osb.patcher.model.PatcherFixPack> getPatcherFixPacks(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getPatcherFixPacks(start, end);
	}

	/**
	* Returns the number of patcher fix packs.
	*
	* @return the number of patcher fix packs
	* @throws SystemException if a system exception occurred
	*/
	public static int getPatcherFixPacksCount()
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getPatcherFixPacksCount();
	}

	/**
	* Updates the patcher fix pack in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param patcherFixPack the patcher fix pack
	* @return the patcher fix pack that was updated
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherFixPack updatePatcherFixPack(
		com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().updatePatcherFixPack(patcherFixPack);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherFixPatcherFixPack(long patcherFixId,
		long patcherFixPackId)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().addPatcherFixPatcherFixPack(patcherFixId, patcherFixPackId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherFixPatcherFixPack(long patcherFixId,
		com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().addPatcherFixPatcherFixPack(patcherFixId, patcherFixPack);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherFixPatcherFixPacks(long patcherFixId,
		long[] patcherFixPackIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService()
			.addPatcherFixPatcherFixPacks(patcherFixId, patcherFixPackIds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherFixPatcherFixPacks(long patcherFixId,
		java.util.List<com.liferay.osb.patcher.model.PatcherFixPack> patcherFixPacks)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().addPatcherFixPatcherFixPacks(patcherFixId, patcherFixPacks);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void clearPatcherFixPatcherFixPacks(long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().clearPatcherFixPatcherFixPacks(patcherFixId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void deletePatcherFixPatcherFixPack(long patcherFixId,
		long patcherFixPackId)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService()
			.deletePatcherFixPatcherFixPack(patcherFixId, patcherFixPackId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void deletePatcherFixPatcherFixPack(long patcherFixId,
		com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().deletePatcherFixPatcherFixPack(patcherFixId, patcherFixPack);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void deletePatcherFixPatcherFixPacks(long patcherFixId,
		long[] patcherFixPackIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService()
			.deletePatcherFixPatcherFixPacks(patcherFixId, patcherFixPackIds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void deletePatcherFixPatcherFixPacks(long patcherFixId,
		java.util.List<com.liferay.osb.patcher.model.PatcherFixPack> patcherFixPacks)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService()
			.deletePatcherFixPatcherFixPacks(patcherFixId, patcherFixPacks);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherFixPack> getPatcherFixPatcherFixPacks(
		long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getPatcherFixPatcherFixPacks(patcherFixId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherFixPack> getPatcherFixPatcherFixPacks(
		long patcherFixId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService()
				   .getPatcherFixPatcherFixPacks(patcherFixId, start, end);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherFixPack> getPatcherFixPatcherFixPacks(
		long patcherFixId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService()
				   .getPatcherFixPatcherFixPacks(patcherFixId, start, end,
			orderByComparator);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static int getPatcherFixPatcherFixPacksCount(long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getPatcherFixPatcherFixPacksCount(patcherFixId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static boolean hasPatcherFixPatcherFixPack(long patcherFixId,
		long patcherFixPackId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService()
				   .hasPatcherFixPatcherFixPack(patcherFixId, patcherFixPackId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static boolean hasPatcherFixPatcherFixPacks(long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().hasPatcherFixPatcherFixPacks(patcherFixId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void setPatcherFixPatcherFixPacks(long patcherFixId,
		long[] patcherFixPackIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService()
			.setPatcherFixPatcherFixPacks(patcherFixId, patcherFixPackIds);
	}

	/**
	* Returns the Spring bean ID for this bean.
	*
	* @return the Spring bean ID for this bean
	*/
	public static java.lang.String getBeanIdentifier() {
		return getService().getBeanIdentifier();
	}

	/**
	* Sets the Spring bean ID for this bean.
	*
	* @param beanIdentifier the Spring bean ID for this bean
	*/
	public static void setBeanIdentifier(java.lang.String beanIdentifier) {
		getService().setBeanIdentifier(beanIdentifier);
	}

	public static java.lang.Object invokeMethod(java.lang.String name,
		java.lang.String[] parameterTypes, java.lang.Object[] arguments)
		throws java.lang.Throwable {
		return getService().invokeMethod(name, parameterTypes, arguments);
	}

	public static void clearService() {
		_service = null;
	}

	public static PatcherFixPackLocalService getService() {
		if (_service == null) {
			InvokableLocalService invokableLocalService = (InvokableLocalService)PortletBeanLocatorUtil.locate(ClpSerializer.getServletContextName(),
					PatcherFixPackLocalService.class.getName());

			if (invokableLocalService instanceof PatcherFixPackLocalService) {
				_service = (PatcherFixPackLocalService)invokableLocalService;
			}
			else {
				_service = new PatcherFixPackLocalServiceClp(invokableLocalService);
			}

			ReferenceRegistry.registerReference(PatcherFixPackLocalServiceUtil.class,
				"_service");
		}

		return _service;
	}

	/**
	 * @deprecated As of 6.2.0
	 */
	public void setService(PatcherFixPackLocalService service) {
	}

	private static PatcherFixPackLocalService _service;
}
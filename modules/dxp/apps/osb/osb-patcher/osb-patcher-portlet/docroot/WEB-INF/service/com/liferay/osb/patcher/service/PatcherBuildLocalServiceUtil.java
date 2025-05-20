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
 * Provides the local service utility for PatcherBuild. This utility wraps
 * {@link com.liferay.osb.patcher.service.impl.PatcherBuildLocalServiceImpl} and is the
 * primary access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Calvin Keum
 * @see PatcherBuildLocalService
 * @see com.liferay.osb.patcher.service.base.PatcherBuildLocalServiceBaseImpl
 * @see com.liferay.osb.patcher.service.impl.PatcherBuildLocalServiceImpl
 * @generated
 */
public class PatcherBuildLocalServiceUtil {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to {@link com.liferay.osb.patcher.service.impl.PatcherBuildLocalServiceImpl} and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	* Adds the patcher build to the database. Also notifies the appropriate model listeners.
	*
	* @param patcherBuild the patcher build
	* @return the patcher build that was added
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherBuild addPatcherBuild(
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().addPatcherBuild(patcherBuild);
	}

	/**
	* Creates a new patcher build with the primary key. Does not add the patcher build to the database.
	*
	* @param patcherBuildId the primary key for the new patcher build
	* @return the new patcher build
	*/
	public static com.liferay.osb.patcher.model.PatcherBuild createPatcherBuild(
		long patcherBuildId) {
		return getService().createPatcherBuild(patcherBuildId);
	}

	/**
	* Deletes the patcher build with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherBuildId the primary key of the patcher build
	* @return the patcher build that was removed
	* @throws PortalException if a patcher build with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherBuild deletePatcherBuild(
		long patcherBuildId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().deletePatcherBuild(patcherBuildId);
	}

	/**
	* Deletes the patcher build from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherBuild the patcher build
	* @return the patcher build that was removed
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherBuild deletePatcherBuild(
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().deletePatcherBuild(patcherBuild);
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
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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

	public static com.liferay.osb.patcher.model.PatcherBuild fetchPatcherBuild(
		long patcherBuildId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().fetchPatcherBuild(patcherBuildId);
	}

	/**
	* Returns the patcher build with the primary key.
	*
	* @param patcherBuildId the primary key of the patcher build
	* @return the patcher build
	* @throws PortalException if a patcher build with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherBuild getPatcherBuild(
		long patcherBuildId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().getPatcherBuild(patcherBuildId);
	}

	public static com.liferay.portal.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().getPersistedModel(primaryKeyObj);
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
	public static java.util.List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherBuilds(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getPatcherBuilds(start, end);
	}

	/**
	* Returns the number of patcher builds.
	*
	* @return the number of patcher builds
	* @throws SystemException if a system exception occurred
	*/
	public static int getPatcherBuildsCount()
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getPatcherBuildsCount();
	}

	/**
	* Updates the patcher build in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param patcherBuild the patcher build
	* @return the patcher build that was updated
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherBuild updatePatcherBuild(
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().updatePatcherBuild(patcherBuild);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherAccountPatcherBuild(long patcherAccountId,
		long patcherBuildId)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService()
			.addPatcherAccountPatcherBuild(patcherAccountId, patcherBuildId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherAccountPatcherBuild(long patcherAccountId,
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService()
			.addPatcherAccountPatcherBuild(patcherAccountId, patcherBuild);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherAccountPatcherBuilds(long patcherAccountId,
		long[] patcherBuildIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService()
			.addPatcherAccountPatcherBuilds(patcherAccountId, patcherBuildIds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherAccountPatcherBuilds(long patcherAccountId,
		java.util.List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService()
			.addPatcherAccountPatcherBuilds(patcherAccountId, patcherBuilds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void clearPatcherAccountPatcherBuilds(long patcherAccountId)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().clearPatcherAccountPatcherBuilds(patcherAccountId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void deletePatcherAccountPatcherBuild(long patcherAccountId,
		long patcherBuildId)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService()
			.deletePatcherAccountPatcherBuild(patcherAccountId, patcherBuildId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void deletePatcherAccountPatcherBuild(long patcherAccountId,
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService()
			.deletePatcherAccountPatcherBuild(patcherAccountId, patcherBuild);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void deletePatcherAccountPatcherBuilds(
		long patcherAccountId, long[] patcherBuildIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService()
			.deletePatcherAccountPatcherBuilds(patcherAccountId, patcherBuildIds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void deletePatcherAccountPatcherBuilds(
		long patcherAccountId,
		java.util.List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService()
			.deletePatcherAccountPatcherBuilds(patcherAccountId, patcherBuilds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherAccountPatcherBuilds(
		long patcherAccountId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getPatcherAccountPatcherBuilds(patcherAccountId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherAccountPatcherBuilds(
		long patcherAccountId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService()
				   .getPatcherAccountPatcherBuilds(patcherAccountId, start, end);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherAccountPatcherBuilds(
		long patcherAccountId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService()
				   .getPatcherAccountPatcherBuilds(patcherAccountId, start,
			end, orderByComparator);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static int getPatcherAccountPatcherBuildsCount(long patcherAccountId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getPatcherAccountPatcherBuildsCount(patcherAccountId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static boolean hasPatcherAccountPatcherBuild(long patcherAccountId,
		long patcherBuildId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService()
				   .hasPatcherAccountPatcherBuild(patcherAccountId,
			patcherBuildId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static boolean hasPatcherAccountPatcherBuilds(long patcherAccountId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().hasPatcherAccountPatcherBuilds(patcherAccountId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void setPatcherAccountPatcherBuilds(long patcherAccountId,
		long[] patcherBuildIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService()
			.setPatcherAccountPatcherBuilds(patcherAccountId, patcherBuildIds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherFixPatcherBuild(long patcherFixId,
		long patcherBuildId)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().addPatcherFixPatcherBuild(patcherFixId, patcherBuildId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherFixPatcherBuild(long patcherFixId,
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().addPatcherFixPatcherBuild(patcherFixId, patcherBuild);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherFixPatcherBuilds(long patcherFixId,
		long[] patcherBuildIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().addPatcherFixPatcherBuilds(patcherFixId, patcherBuildIds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherFixPatcherBuilds(long patcherFixId,
		java.util.List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().addPatcherFixPatcherBuilds(patcherFixId, patcherBuilds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void clearPatcherFixPatcherBuilds(long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().clearPatcherFixPatcherBuilds(patcherFixId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void deletePatcherFixPatcherBuild(long patcherFixId,
		long patcherBuildId)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().deletePatcherFixPatcherBuild(patcherFixId, patcherBuildId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void deletePatcherFixPatcherBuild(long patcherFixId,
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().deletePatcherFixPatcherBuild(patcherFixId, patcherBuild);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void deletePatcherFixPatcherBuilds(long patcherFixId,
		long[] patcherBuildIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().deletePatcherFixPatcherBuilds(patcherFixId, patcherBuildIds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void deletePatcherFixPatcherBuilds(long patcherFixId,
		java.util.List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().deletePatcherFixPatcherBuilds(patcherFixId, patcherBuilds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherFixPatcherBuilds(
		long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getPatcherFixPatcherBuilds(patcherFixId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherFixPatcherBuilds(
		long patcherFixId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getPatcherFixPatcherBuilds(patcherFixId, start, end);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherFixPatcherBuilds(
		long patcherFixId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService()
				   .getPatcherFixPatcherBuilds(patcherFixId, start, end,
			orderByComparator);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static int getPatcherFixPatcherBuildsCount(long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getPatcherFixPatcherBuildsCount(patcherFixId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static boolean hasPatcherFixPatcherBuild(long patcherFixId,
		long patcherBuildId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService()
				   .hasPatcherFixPatcherBuild(patcherFixId, patcherBuildId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static boolean hasPatcherFixPatcherBuilds(long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().hasPatcherFixPatcherBuilds(patcherFixId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void setPatcherFixPatcherBuilds(long patcherFixId,
		long[] patcherBuildIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().setPatcherFixPatcherBuilds(patcherFixId, patcherBuildIds);
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

	public static PatcherBuildLocalService getService() {
		if (_service == null) {
			InvokableLocalService invokableLocalService = (InvokableLocalService)PortletBeanLocatorUtil.locate(ClpSerializer.getServletContextName(),
					PatcherBuildLocalService.class.getName());

			if (invokableLocalService instanceof PatcherBuildLocalService) {
				_service = (PatcherBuildLocalService)invokableLocalService;
			}
			else {
				_service = new PatcherBuildLocalServiceClp(invokableLocalService);
			}

			ReferenceRegistry.registerReference(PatcherBuildLocalServiceUtil.class,
				"_service");
		}

		return _service;
	}

	/**
	 * @deprecated As of 6.2.0
	 */
	public void setService(PatcherBuildLocalService service) {
	}

	private static PatcherBuildLocalService _service;
}
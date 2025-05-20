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
 * Provides the local service utility for PatcherFix. This utility wraps
 * {@link com.liferay.osb.patcher.service.impl.PatcherFixLocalServiceImpl} and is the
 * primary access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Calvin Keum
 * @see PatcherFixLocalService
 * @see com.liferay.osb.patcher.service.base.PatcherFixLocalServiceBaseImpl
 * @see com.liferay.osb.patcher.service.impl.PatcherFixLocalServiceImpl
 * @generated
 */
public class PatcherFixLocalServiceUtil {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to {@link com.liferay.osb.patcher.service.impl.PatcherFixLocalServiceImpl} and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	* Adds the patcher fix to the database. Also notifies the appropriate model listeners.
	*
	* @param patcherFix the patcher fix
	* @return the patcher fix that was added
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherFix addPatcherFix(
		com.liferay.osb.patcher.model.PatcherFix patcherFix)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().addPatcherFix(patcherFix);
	}

	/**
	* Creates a new patcher fix with the primary key. Does not add the patcher fix to the database.
	*
	* @param patcherFixId the primary key for the new patcher fix
	* @return the new patcher fix
	*/
	public static com.liferay.osb.patcher.model.PatcherFix createPatcherFix(
		long patcherFixId) {
		return getService().createPatcherFix(patcherFixId);
	}

	/**
	* Deletes the patcher fix with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherFixId the primary key of the patcher fix
	* @return the patcher fix that was removed
	* @throws PortalException if a patcher fix with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherFix deletePatcherFix(
		long patcherFixId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().deletePatcherFix(patcherFixId);
	}

	/**
	* Deletes the patcher fix from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherFix the patcher fix
	* @return the patcher fix that was removed
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherFix deletePatcherFix(
		com.liferay.osb.patcher.model.PatcherFix patcherFix)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().deletePatcherFix(patcherFix);
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
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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

	public static com.liferay.osb.patcher.model.PatcherFix fetchPatcherFix(
		long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().fetchPatcherFix(patcherFixId);
	}

	/**
	* Returns the patcher fix with the primary key.
	*
	* @param patcherFixId the primary key of the patcher fix
	* @return the patcher fix
	* @throws PortalException if a patcher fix with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherFix getPatcherFix(
		long patcherFixId)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().getPatcherFix(patcherFixId);
	}

	public static com.liferay.portal.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException,
			com.liferay.portal.kernel.exception.SystemException {
		return getService().getPersistedModel(primaryKeyObj);
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
	public static java.util.List<com.liferay.osb.patcher.model.PatcherFix> getPatcherFixs(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getPatcherFixs(start, end);
	}

	/**
	* Returns the number of patcher fixs.
	*
	* @return the number of patcher fixs
	* @throws SystemException if a system exception occurred
	*/
	public static int getPatcherFixsCount()
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getPatcherFixsCount();
	}

	/**
	* Updates the patcher fix in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param patcherFix the patcher fix
	* @return the patcher fix that was updated
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherFix updatePatcherFix(
		com.liferay.osb.patcher.model.PatcherFix patcherFix)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().updatePatcherFix(patcherFix);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherBuildPatcherFix(long patcherBuildId,
		long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().addPatcherBuildPatcherFix(patcherBuildId, patcherFixId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherBuildPatcherFix(long patcherBuildId,
		com.liferay.osb.patcher.model.PatcherFix patcherFix)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().addPatcherBuildPatcherFix(patcherBuildId, patcherFix);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherBuildPatcherFixs(long patcherBuildId,
		long[] patcherFixIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().addPatcherBuildPatcherFixs(patcherBuildId, patcherFixIds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherBuildPatcherFixs(long patcherBuildId,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixs)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().addPatcherBuildPatcherFixs(patcherBuildId, patcherFixs);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void clearPatcherBuildPatcherFixs(long patcherBuildId)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().clearPatcherBuildPatcherFixs(patcherBuildId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void deletePatcherBuildPatcherFix(long patcherBuildId,
		long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().deletePatcherBuildPatcherFix(patcherBuildId, patcherFixId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void deletePatcherBuildPatcherFix(long patcherBuildId,
		com.liferay.osb.patcher.model.PatcherFix patcherFix)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().deletePatcherBuildPatcherFix(patcherBuildId, patcherFix);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void deletePatcherBuildPatcherFixs(long patcherBuildId,
		long[] patcherFixIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().deletePatcherBuildPatcherFixs(patcherBuildId, patcherFixIds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void deletePatcherBuildPatcherFixs(long patcherBuildId,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixs)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().deletePatcherBuildPatcherFixs(patcherBuildId, patcherFixs);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherFix> getPatcherBuildPatcherFixs(
		long patcherBuildId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getPatcherBuildPatcherFixs(patcherBuildId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherFix> getPatcherBuildPatcherFixs(
		long patcherBuildId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService()
				   .getPatcherBuildPatcherFixs(patcherBuildId, start, end);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherFix> getPatcherBuildPatcherFixs(
		long patcherBuildId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService()
				   .getPatcherBuildPatcherFixs(patcherBuildId, start, end,
			orderByComparator);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static int getPatcherBuildPatcherFixsCount(long patcherBuildId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getPatcherBuildPatcherFixsCount(patcherBuildId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static boolean hasPatcherBuildPatcherFix(long patcherBuildId,
		long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService()
				   .hasPatcherBuildPatcherFix(patcherBuildId, patcherFixId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static boolean hasPatcherBuildPatcherFixs(long patcherBuildId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().hasPatcherBuildPatcherFixs(patcherBuildId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void setPatcherBuildPatcherFixs(long patcherBuildId,
		long[] patcherFixIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().setPatcherBuildPatcherFixs(patcherBuildId, patcherFixIds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherFixPackPatcherFix(long patcherFixPackId,
		long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().addPatcherFixPackPatcherFix(patcherFixPackId, patcherFixId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherFixPackPatcherFix(long patcherFixPackId,
		com.liferay.osb.patcher.model.PatcherFix patcherFix)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().addPatcherFixPackPatcherFix(patcherFixPackId, patcherFix);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherFixPackPatcherFixs(long patcherFixPackId,
		long[] patcherFixIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService()
			.addPatcherFixPackPatcherFixs(patcherFixPackId, patcherFixIds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherFixPackPatcherFixs(long patcherFixPackId,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixs)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().addPatcherFixPackPatcherFixs(patcherFixPackId, patcherFixs);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void clearPatcherFixPackPatcherFixs(long patcherFixPackId)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().clearPatcherFixPackPatcherFixs(patcherFixPackId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void deletePatcherFixPackPatcherFix(long patcherFixPackId,
		long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService()
			.deletePatcherFixPackPatcherFix(patcherFixPackId, patcherFixId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void deletePatcherFixPackPatcherFix(long patcherFixPackId,
		com.liferay.osb.patcher.model.PatcherFix patcherFix)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService().deletePatcherFixPackPatcherFix(patcherFixPackId, patcherFix);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void deletePatcherFixPackPatcherFixs(long patcherFixPackId,
		long[] patcherFixIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService()
			.deletePatcherFixPackPatcherFixs(patcherFixPackId, patcherFixIds);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void deletePatcherFixPackPatcherFixs(long patcherFixPackId,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixs)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService()
			.deletePatcherFixPackPatcherFixs(patcherFixPackId, patcherFixs);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherFix> getPatcherFixPackPatcherFixs(
		long patcherFixPackId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getPatcherFixPackPatcherFixs(patcherFixPackId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherFix> getPatcherFixPackPatcherFixs(
		long patcherFixPackId, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService()
				   .getPatcherFixPackPatcherFixs(patcherFixPackId, start, end);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherFix> getPatcherFixPackPatcherFixs(
		long patcherFixPackId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService()
				   .getPatcherFixPackPatcherFixs(patcherFixPackId, start, end,
			orderByComparator);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static int getPatcherFixPackPatcherFixsCount(long patcherFixPackId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().getPatcherFixPackPatcherFixsCount(patcherFixPackId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static boolean hasPatcherFixPackPatcherFix(long patcherFixPackId,
		long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService()
				   .hasPatcherFixPackPatcherFix(patcherFixPackId, patcherFixId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static boolean hasPatcherFixPackPatcherFixs(long patcherFixPackId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getService().hasPatcherFixPackPatcherFixs(patcherFixPackId);
	}

	/**
	* @throws SystemException if a system exception occurred
	*/
	public static void setPatcherFixPackPatcherFixs(long patcherFixPackId,
		long[] patcherFixIds)
		throws com.liferay.portal.kernel.exception.SystemException {
		getService()
			.setPatcherFixPackPatcherFixs(patcherFixPackId, patcherFixIds);
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

	public static PatcherFixLocalService getService() {
		if (_service == null) {
			InvokableLocalService invokableLocalService = (InvokableLocalService)PortletBeanLocatorUtil.locate(ClpSerializer.getServletContextName(),
					PatcherFixLocalService.class.getName());

			if (invokableLocalService instanceof PatcherFixLocalService) {
				_service = (PatcherFixLocalService)invokableLocalService;
			}
			else {
				_service = new PatcherFixLocalServiceClp(invokableLocalService);
			}

			ReferenceRegistry.registerReference(PatcherFixLocalServiceUtil.class,
				"_service");
		}

		return _service;
	}

	/**
	 * @deprecated As of 6.2.0
	 */
	public void setService(PatcherFixLocalService service) {
	}

	private static PatcherFixLocalService _service;
}
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.model.PatcherProjectVersion;

import com.liferay.portal.kernel.bean.PortletBeanLocatorUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ReferenceRegistry;
import com.liferay.portal.service.ServiceContext;

import java.util.List;

/**
 * The persistence utility for the patcher project version service. This utility wraps {@link PatcherProjectVersionPersistenceImpl} and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherProjectVersionPersistence
 * @see PatcherProjectVersionPersistenceImpl
 * @generated
 */
public class PatcherProjectVersionUtil {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#clearCache(com.liferay.portal.model.BaseModel)
	 */
	public static void clearCache(PatcherProjectVersion patcherProjectVersion) {
		getPersistence().clearCache(patcherProjectVersion);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery)
		throws SystemException {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<PatcherProjectVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery) throws SystemException {
		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<PatcherProjectVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end)
		throws SystemException {
		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<PatcherProjectVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator orderByComparator) throws SystemException {
		return getPersistence()
				   .findWithDynamicQuery(dynamicQuery, start, end,
			orderByComparator);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#update(com.liferay.portal.model.BaseModel)
	 */
	public static PatcherProjectVersion update(
		PatcherProjectVersion patcherProjectVersion) throws SystemException {
		return getPersistence().update(patcherProjectVersion);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#update(com.liferay.portal.model.BaseModel, ServiceContext)
	 */
	public static PatcherProjectVersion update(
		PatcherProjectVersion patcherProjectVersion,
		ServiceContext serviceContext) throws SystemException {
		return getPersistence().update(patcherProjectVersion, serviceContext);
	}

	/**
	* Caches the patcher project version in the entity cache if it is enabled.
	*
	* @param patcherProjectVersion the patcher project version
	*/
	public static void cacheResult(
		com.liferay.osb.patcher.model.PatcherProjectVersion patcherProjectVersion) {
		getPersistence().cacheResult(patcherProjectVersion);
	}

	/**
	* Caches the patcher project versions in the entity cache if it is enabled.
	*
	* @param patcherProjectVersions the patcher project versions
	*/
	public static void cacheResult(
		java.util.List<com.liferay.osb.patcher.model.PatcherProjectVersion> patcherProjectVersions) {
		getPersistence().cacheResult(patcherProjectVersions);
	}

	/**
	* Creates a new patcher project version with the primary key. Does not add the patcher project version to the database.
	*
	* @param patcherProjectVersionId the primary key for the new patcher project version
	* @return the new patcher project version
	*/
	public static com.liferay.osb.patcher.model.PatcherProjectVersion create(
		long patcherProjectVersionId) {
		return getPersistence().create(patcherProjectVersionId);
	}

	/**
	* Removes the patcher project version with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherProjectVersionId the primary key of the patcher project version
	* @return the patcher project version that was removed
	* @throws com.liferay.osb.patcher.NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherProjectVersion remove(
		long patcherProjectVersionId)
		throws com.liferay.osb.patcher.NoSuchPatcherProjectVersionException,
			com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().remove(patcherProjectVersionId);
	}

	public static com.liferay.osb.patcher.model.PatcherProjectVersion updateImpl(
		com.liferay.osb.patcher.model.PatcherProjectVersion patcherProjectVersion)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().updateImpl(patcherProjectVersion);
	}

	/**
	* Returns the patcher project version with the primary key or throws a {@link com.liferay.osb.patcher.NoSuchPatcherProjectVersionException} if it could not be found.
	*
	* @param patcherProjectVersionId the primary key of the patcher project version
	* @return the patcher project version
	* @throws com.liferay.osb.patcher.NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherProjectVersion findByPrimaryKey(
		long patcherProjectVersionId)
		throws com.liferay.osb.patcher.NoSuchPatcherProjectVersionException,
			com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findByPrimaryKey(patcherProjectVersionId);
	}

	/**
	* Returns the patcher project version with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param patcherProjectVersionId the primary key of the patcher project version
	* @return the patcher project version, or <code>null</code> if a patcher project version with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherProjectVersion fetchByPrimaryKey(
		long patcherProjectVersionId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().fetchByPrimaryKey(patcherProjectVersionId);
	}

	/**
	* Returns all the patcher project versions.
	*
	* @return the patcher project versions
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherProjectVersion> findAll()
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findAll();
	}

	/**
	* Returns a range of all the patcher project versions.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherProjectVersionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher project versions
	* @param end the upper bound of the range of patcher project versions (not inclusive)
	* @return the range of patcher project versions
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherProjectVersion> findAll(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findAll(start, end);
	}

	/**
	* Returns an ordered range of all the patcher project versions.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherProjectVersionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher project versions
	* @param end the upper bound of the range of patcher project versions (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of patcher project versions
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherProjectVersion> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	* Removes all the patcher project versions from the database.
	*
	* @throws SystemException if a system exception occurred
	*/
	public static void removeAll()
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().removeAll();
	}

	/**
	* Returns the number of patcher project versions.
	*
	* @return the number of patcher project versions
	* @throws SystemException if a system exception occurred
	*/
	public static int countAll()
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().countAll();
	}

	public static PatcherProjectVersionPersistence getPersistence() {
		if (_persistence == null) {
			_persistence = (PatcherProjectVersionPersistence)PortletBeanLocatorUtil.locate(com.liferay.osb.patcher.service.ClpSerializer.getServletContextName(),
					PatcherProjectVersionPersistence.class.getName());

			ReferenceRegistry.registerReference(PatcherProjectVersionUtil.class,
				"_persistence");
		}

		return _persistence;
	}

	/**
	 * @deprecated As of 6.2.0
	 */
	public void setPersistence(PatcherProjectVersionPersistence persistence) {
	}

	private static PatcherProjectVersionPersistence _persistence;
}
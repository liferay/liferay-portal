/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.model.PatcherProductVersion;

import com.liferay.portal.kernel.bean.PortletBeanLocatorUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ReferenceRegistry;
import com.liferay.portal.service.ServiceContext;

import java.util.List;

/**
 * The persistence utility for the patcher product version service. This utility wraps {@link PatcherProductVersionPersistenceImpl} and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherProductVersionPersistence
 * @see PatcherProductVersionPersistenceImpl
 * @generated
 */
public class PatcherProductVersionUtil {
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
	public static void clearCache(PatcherProductVersion patcherProductVersion) {
		getPersistence().clearCache(patcherProductVersion);
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
	public static List<PatcherProductVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery) throws SystemException {
		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<PatcherProductVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end)
		throws SystemException {
		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<PatcherProductVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator orderByComparator) throws SystemException {
		return getPersistence()
				   .findWithDynamicQuery(dynamicQuery, start, end,
			orderByComparator);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#update(com.liferay.portal.model.BaseModel)
	 */
	public static PatcherProductVersion update(
		PatcherProductVersion patcherProductVersion) throws SystemException {
		return getPersistence().update(patcherProductVersion);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#update(com.liferay.portal.model.BaseModel, ServiceContext)
	 */
	public static PatcherProductVersion update(
		PatcherProductVersion patcherProductVersion,
		ServiceContext serviceContext) throws SystemException {
		return getPersistence().update(patcherProductVersion, serviceContext);
	}

	/**
	* Caches the patcher product version in the entity cache if it is enabled.
	*
	* @param patcherProductVersion the patcher product version
	*/
	public static void cacheResult(
		com.liferay.osb.patcher.model.PatcherProductVersion patcherProductVersion) {
		getPersistence().cacheResult(patcherProductVersion);
	}

	/**
	* Caches the patcher product versions in the entity cache if it is enabled.
	*
	* @param patcherProductVersions the patcher product versions
	*/
	public static void cacheResult(
		java.util.List<com.liferay.osb.patcher.model.PatcherProductVersion> patcherProductVersions) {
		getPersistence().cacheResult(patcherProductVersions);
	}

	/**
	* Creates a new patcher product version with the primary key. Does not add the patcher product version to the database.
	*
	* @param patcherProductVersionId the primary key for the new patcher product version
	* @return the new patcher product version
	*/
	public static com.liferay.osb.patcher.model.PatcherProductVersion create(
		long patcherProductVersionId) {
		return getPersistence().create(patcherProductVersionId);
	}

	/**
	* Removes the patcher product version with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherProductVersionId the primary key of the patcher product version
	* @return the patcher product version that was removed
	* @throws com.liferay.osb.patcher.NoSuchPatcherProductVersionException if a patcher product version with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherProductVersion remove(
		long patcherProductVersionId)
		throws com.liferay.osb.patcher.NoSuchPatcherProductVersionException,
			com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().remove(patcherProductVersionId);
	}

	public static com.liferay.osb.patcher.model.PatcherProductVersion updateImpl(
		com.liferay.osb.patcher.model.PatcherProductVersion patcherProductVersion)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().updateImpl(patcherProductVersion);
	}

	/**
	* Returns the patcher product version with the primary key or throws a {@link com.liferay.osb.patcher.NoSuchPatcherProductVersionException} if it could not be found.
	*
	* @param patcherProductVersionId the primary key of the patcher product version
	* @return the patcher product version
	* @throws com.liferay.osb.patcher.NoSuchPatcherProductVersionException if a patcher product version with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherProductVersion findByPrimaryKey(
		long patcherProductVersionId)
		throws com.liferay.osb.patcher.NoSuchPatcherProductVersionException,
			com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findByPrimaryKey(patcherProductVersionId);
	}

	/**
	* Returns the patcher product version with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param patcherProductVersionId the primary key of the patcher product version
	* @return the patcher product version, or <code>null</code> if a patcher product version with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherProductVersion fetchByPrimaryKey(
		long patcherProductVersionId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().fetchByPrimaryKey(patcherProductVersionId);
	}

	/**
	* Returns all the patcher product versions.
	*
	* @return the patcher product versions
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherProductVersion> findAll()
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findAll();
	}

	/**
	* Returns a range of all the patcher product versions.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherProductVersionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher product versions
	* @param end the upper bound of the range of patcher product versions (not inclusive)
	* @return the range of patcher product versions
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherProductVersion> findAll(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findAll(start, end);
	}

	/**
	* Returns an ordered range of all the patcher product versions.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherProductVersionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher product versions
	* @param end the upper bound of the range of patcher product versions (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of patcher product versions
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherProductVersion> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	* Removes all the patcher product versions from the database.
	*
	* @throws SystemException if a system exception occurred
	*/
	public static void removeAll()
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().removeAll();
	}

	/**
	* Returns the number of patcher product versions.
	*
	* @return the number of patcher product versions
	* @throws SystemException if a system exception occurred
	*/
	public static int countAll()
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().countAll();
	}

	public static PatcherProductVersionPersistence getPersistence() {
		if (_persistence == null) {
			_persistence = (PatcherProductVersionPersistence)PortletBeanLocatorUtil.locate(com.liferay.osb.patcher.service.ClpSerializer.getServletContextName(),
					PatcherProductVersionPersistence.class.getName());

			ReferenceRegistry.registerReference(PatcherProductVersionUtil.class,
				"_persistence");
		}

		return _persistence;
	}

	/**
	 * @deprecated As of 6.2.0
	 */
	public void setPersistence(PatcherProductVersionPersistence persistence) {
	}

	private static PatcherProductVersionPersistence _persistence;
}
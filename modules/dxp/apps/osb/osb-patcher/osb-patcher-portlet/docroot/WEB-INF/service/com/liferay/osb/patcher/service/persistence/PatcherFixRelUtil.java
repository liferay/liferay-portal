/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.model.PatcherFixRel;

import com.liferay.portal.kernel.bean.PortletBeanLocatorUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ReferenceRegistry;
import com.liferay.portal.service.ServiceContext;

import java.util.List;

/**
 * The persistence utility for the patcher fix rel service. This utility wraps {@link PatcherFixRelPersistenceImpl} and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherFixRelPersistence
 * @see PatcherFixRelPersistenceImpl
 * @generated
 */
public class PatcherFixRelUtil {
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
	public static void clearCache(PatcherFixRel patcherFixRel) {
		getPersistence().clearCache(patcherFixRel);
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
	public static List<PatcherFixRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery) throws SystemException {
		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<PatcherFixRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end)
		throws SystemException {
		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<PatcherFixRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator orderByComparator) throws SystemException {
		return getPersistence()
				   .findWithDynamicQuery(dynamicQuery, start, end,
			orderByComparator);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#update(com.liferay.portal.model.BaseModel)
	 */
	public static PatcherFixRel update(PatcherFixRel patcherFixRel)
		throws SystemException {
		return getPersistence().update(patcherFixRel);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#update(com.liferay.portal.model.BaseModel, ServiceContext)
	 */
	public static PatcherFixRel update(PatcherFixRel patcherFixRel,
		ServiceContext serviceContext) throws SystemException {
		return getPersistence().update(patcherFixRel, serviceContext);
	}

	/**
	* Caches the patcher fix rel in the entity cache if it is enabled.
	*
	* @param patcherFixRel the patcher fix rel
	*/
	public static void cacheResult(
		com.liferay.osb.patcher.model.PatcherFixRel patcherFixRel) {
		getPersistence().cacheResult(patcherFixRel);
	}

	/**
	* Caches the patcher fix rels in the entity cache if it is enabled.
	*
	* @param patcherFixRels the patcher fix rels
	*/
	public static void cacheResult(
		java.util.List<com.liferay.osb.patcher.model.PatcherFixRel> patcherFixRels) {
		getPersistence().cacheResult(patcherFixRels);
	}

	/**
	* Creates a new patcher fix rel with the primary key. Does not add the patcher fix rel to the database.
	*
	* @param patcherFixRelId the primary key for the new patcher fix rel
	* @return the new patcher fix rel
	*/
	public static com.liferay.osb.patcher.model.PatcherFixRel create(
		long patcherFixRelId) {
		return getPersistence().create(patcherFixRelId);
	}

	/**
	* Removes the patcher fix rel with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherFixRelId the primary key of the patcher fix rel
	* @return the patcher fix rel that was removed
	* @throws com.liferay.osb.patcher.NoSuchPatcherFixRelException if a patcher fix rel with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherFixRel remove(
		long patcherFixRelId)
		throws com.liferay.osb.patcher.NoSuchPatcherFixRelException,
			com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().remove(patcherFixRelId);
	}

	public static com.liferay.osb.patcher.model.PatcherFixRel updateImpl(
		com.liferay.osb.patcher.model.PatcherFixRel patcherFixRel)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().updateImpl(patcherFixRel);
	}

	/**
	* Returns the patcher fix rel with the primary key or throws a {@link com.liferay.osb.patcher.NoSuchPatcherFixRelException} if it could not be found.
	*
	* @param patcherFixRelId the primary key of the patcher fix rel
	* @return the patcher fix rel
	* @throws com.liferay.osb.patcher.NoSuchPatcherFixRelException if a patcher fix rel with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherFixRel findByPrimaryKey(
		long patcherFixRelId)
		throws com.liferay.osb.patcher.NoSuchPatcherFixRelException,
			com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findByPrimaryKey(patcherFixRelId);
	}

	/**
	* Returns the patcher fix rel with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param patcherFixRelId the primary key of the patcher fix rel
	* @return the patcher fix rel, or <code>null</code> if a patcher fix rel with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherFixRel fetchByPrimaryKey(
		long patcherFixRelId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().fetchByPrimaryKey(patcherFixRelId);
	}

	/**
	* Returns all the patcher fix rels.
	*
	* @return the patcher fix rels
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherFixRel> findAll()
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findAll();
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
	public static java.util.List<com.liferay.osb.patcher.model.PatcherFixRel> findAll(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findAll(start, end);
	}

	/**
	* Returns an ordered range of all the patcher fix rels.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixRelModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher fix rels
	* @param end the upper bound of the range of patcher fix rels (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of patcher fix rels
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherFixRel> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	* Removes all the patcher fix rels from the database.
	*
	* @throws SystemException if a system exception occurred
	*/
	public static void removeAll()
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().removeAll();
	}

	/**
	* Returns the number of patcher fix rels.
	*
	* @return the number of patcher fix rels
	* @throws SystemException if a system exception occurred
	*/
	public static int countAll()
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().countAll();
	}

	public static PatcherFixRelPersistence getPersistence() {
		if (_persistence == null) {
			_persistence = (PatcherFixRelPersistence)PortletBeanLocatorUtil.locate(com.liferay.osb.patcher.service.ClpSerializer.getServletContextName(),
					PatcherFixRelPersistence.class.getName());

			ReferenceRegistry.registerReference(PatcherFixRelUtil.class,
				"_persistence");
		}

		return _persistence;
	}

	/**
	 * @deprecated As of 6.2.0
	 */
	public void setPersistence(PatcherFixRelPersistence persistence) {
	}

	private static PatcherFixRelPersistence _persistence;
}
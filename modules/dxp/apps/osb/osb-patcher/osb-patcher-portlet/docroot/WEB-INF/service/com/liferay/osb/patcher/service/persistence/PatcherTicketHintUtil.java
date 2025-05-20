/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.model.PatcherTicketHint;

import com.liferay.portal.kernel.bean.PortletBeanLocatorUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ReferenceRegistry;
import com.liferay.portal.service.ServiceContext;

import java.util.List;

/**
 * The persistence utility for the patcher ticket hint service. This utility wraps {@link PatcherTicketHintPersistenceImpl} and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherTicketHintPersistence
 * @see PatcherTicketHintPersistenceImpl
 * @generated
 */
public class PatcherTicketHintUtil {
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
	public static void clearCache(PatcherTicketHint patcherTicketHint) {
		getPersistence().clearCache(patcherTicketHint);
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
	public static List<PatcherTicketHint> findWithDynamicQuery(
		DynamicQuery dynamicQuery) throws SystemException {
		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<PatcherTicketHint> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end)
		throws SystemException {
		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<PatcherTicketHint> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator orderByComparator) throws SystemException {
		return getPersistence()
				   .findWithDynamicQuery(dynamicQuery, start, end,
			orderByComparator);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#update(com.liferay.portal.model.BaseModel)
	 */
	public static PatcherTicketHint update(PatcherTicketHint patcherTicketHint)
		throws SystemException {
		return getPersistence().update(patcherTicketHint);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#update(com.liferay.portal.model.BaseModel, ServiceContext)
	 */
	public static PatcherTicketHint update(
		PatcherTicketHint patcherTicketHint, ServiceContext serviceContext)
		throws SystemException {
		return getPersistence().update(patcherTicketHint, serviceContext);
	}

	/**
	* Returns the patcher ticket hint where patcherProductVersionId = &#63; or throws a {@link com.liferay.osb.patcher.NoSuchPatcherTicketHintException} if it could not be found.
	*
	* @param patcherProductVersionId the patcher product version ID
	* @return the matching patcher ticket hint
	* @throws com.liferay.osb.patcher.NoSuchPatcherTicketHintException if a matching patcher ticket hint could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherTicketHint findByPatcherProductVersionId(
		long patcherProductVersionId)
		throws com.liferay.osb.patcher.NoSuchPatcherTicketHintException,
			com.liferay.portal.kernel.exception.SystemException {
		return getPersistence()
				   .findByPatcherProductVersionId(patcherProductVersionId);
	}

	/**
	* Returns the patcher ticket hint where patcherProductVersionId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param patcherProductVersionId the patcher product version ID
	* @return the matching patcher ticket hint, or <code>null</code> if a matching patcher ticket hint could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherTicketHint fetchByPatcherProductVersionId(
		long patcherProductVersionId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence()
				   .fetchByPatcherProductVersionId(patcherProductVersionId);
	}

	/**
	* Returns the patcher ticket hint where patcherProductVersionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param patcherProductVersionId the patcher product version ID
	* @param retrieveFromCache whether to use the finder cache
	* @return the matching patcher ticket hint, or <code>null</code> if a matching patcher ticket hint could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherTicketHint fetchByPatcherProductVersionId(
		long patcherProductVersionId, boolean retrieveFromCache)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence()
				   .fetchByPatcherProductVersionId(patcherProductVersionId,
			retrieveFromCache);
	}

	/**
	* Removes the patcher ticket hint where patcherProductVersionId = &#63; from the database.
	*
	* @param patcherProductVersionId the patcher product version ID
	* @return the patcher ticket hint that was removed
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherTicketHint removeByPatcherProductVersionId(
		long patcherProductVersionId)
		throws com.liferay.osb.patcher.NoSuchPatcherTicketHintException,
			com.liferay.portal.kernel.exception.SystemException {
		return getPersistence()
				   .removeByPatcherProductVersionId(patcherProductVersionId);
	}

	/**
	* Returns the number of patcher ticket hints where patcherProductVersionId = &#63;.
	*
	* @param patcherProductVersionId the patcher product version ID
	* @return the number of matching patcher ticket hints
	* @throws SystemException if a system exception occurred
	*/
	public static int countByPatcherProductVersionId(
		long patcherProductVersionId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence()
				   .countByPatcherProductVersionId(patcherProductVersionId);
	}

	/**
	* Caches the patcher ticket hint in the entity cache if it is enabled.
	*
	* @param patcherTicketHint the patcher ticket hint
	*/
	public static void cacheResult(
		com.liferay.osb.patcher.model.PatcherTicketHint patcherTicketHint) {
		getPersistence().cacheResult(patcherTicketHint);
	}

	/**
	* Caches the patcher ticket hints in the entity cache if it is enabled.
	*
	* @param patcherTicketHints the patcher ticket hints
	*/
	public static void cacheResult(
		java.util.List<com.liferay.osb.patcher.model.PatcherTicketHint> patcherTicketHints) {
		getPersistence().cacheResult(patcherTicketHints);
	}

	/**
	* Creates a new patcher ticket hint with the primary key. Does not add the patcher ticket hint to the database.
	*
	* @param patcherTicketHintId the primary key for the new patcher ticket hint
	* @return the new patcher ticket hint
	*/
	public static com.liferay.osb.patcher.model.PatcherTicketHint create(
		long patcherTicketHintId) {
		return getPersistence().create(patcherTicketHintId);
	}

	/**
	* Removes the patcher ticket hint with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherTicketHintId the primary key of the patcher ticket hint
	* @return the patcher ticket hint that was removed
	* @throws com.liferay.osb.patcher.NoSuchPatcherTicketHintException if a patcher ticket hint with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherTicketHint remove(
		long patcherTicketHintId)
		throws com.liferay.osb.patcher.NoSuchPatcherTicketHintException,
			com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().remove(patcherTicketHintId);
	}

	public static com.liferay.osb.patcher.model.PatcherTicketHint updateImpl(
		com.liferay.osb.patcher.model.PatcherTicketHint patcherTicketHint)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().updateImpl(patcherTicketHint);
	}

	/**
	* Returns the patcher ticket hint with the primary key or throws a {@link com.liferay.osb.patcher.NoSuchPatcherTicketHintException} if it could not be found.
	*
	* @param patcherTicketHintId the primary key of the patcher ticket hint
	* @return the patcher ticket hint
	* @throws com.liferay.osb.patcher.NoSuchPatcherTicketHintException if a patcher ticket hint with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherTicketHint findByPrimaryKey(
		long patcherTicketHintId)
		throws com.liferay.osb.patcher.NoSuchPatcherTicketHintException,
			com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findByPrimaryKey(patcherTicketHintId);
	}

	/**
	* Returns the patcher ticket hint with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param patcherTicketHintId the primary key of the patcher ticket hint
	* @return the patcher ticket hint, or <code>null</code> if a patcher ticket hint with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherTicketHint fetchByPrimaryKey(
		long patcherTicketHintId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().fetchByPrimaryKey(patcherTicketHintId);
	}

	/**
	* Returns all the patcher ticket hints.
	*
	* @return the patcher ticket hints
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherTicketHint> findAll()
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findAll();
	}

	/**
	* Returns a range of all the patcher ticket hints.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherTicketHintModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher ticket hints
	* @param end the upper bound of the range of patcher ticket hints (not inclusive)
	* @return the range of patcher ticket hints
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherTicketHint> findAll(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findAll(start, end);
	}

	/**
	* Returns an ordered range of all the patcher ticket hints.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherTicketHintModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher ticket hints
	* @param end the upper bound of the range of patcher ticket hints (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of patcher ticket hints
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherTicketHint> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	* Removes all the patcher ticket hints from the database.
	*
	* @throws SystemException if a system exception occurred
	*/
	public static void removeAll()
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().removeAll();
	}

	/**
	* Returns the number of patcher ticket hints.
	*
	* @return the number of patcher ticket hints
	* @throws SystemException if a system exception occurred
	*/
	public static int countAll()
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().countAll();
	}

	public static PatcherTicketHintPersistence getPersistence() {
		if (_persistence == null) {
			_persistence = (PatcherTicketHintPersistence)PortletBeanLocatorUtil.locate(com.liferay.osb.patcher.service.ClpSerializer.getServletContextName(),
					PatcherTicketHintPersistence.class.getName());

			ReferenceRegistry.registerReference(PatcherTicketHintUtil.class,
				"_persistence");
		}

		return _persistence;
	}

	/**
	 * @deprecated As of 6.2.0
	 */
	public void setPersistence(PatcherTicketHintPersistence persistence) {
	}

	private static PatcherTicketHintPersistence _persistence;
}
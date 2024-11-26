/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence;

import com.liferay.object.model.ObjectAction;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the object action service. This utility wraps <code>com.liferay.object.service.persistence.impl.ObjectActionPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see ObjectActionPersistence
 * @generated
 */
public class ObjectActionUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(ObjectAction objectAction) {
		getPersistence().clearCache(objectAction);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, ObjectAction> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<ObjectAction> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<ObjectAction> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<ObjectAction> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<ObjectAction> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static ObjectAction update(ObjectAction objectAction) {
		return getPersistence().update(objectAction);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static ObjectAction update(
		ObjectAction objectAction, ServiceContext serviceContext) {

		return getPersistence().update(objectAction, serviceContext);
	}

	/**
	 * Returns all the object actions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching object actions
	 */
	public static List<ObjectAction> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the object actions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @return the range of matching object actions
	 */
	public static List<ObjectAction> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the object actions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object actions
	 */
	public static List<ObjectAction> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectAction> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object actions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object actions
	 */
	public static List<ObjectAction> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectAction> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object action in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public static ObjectAction findByUuid_First(
			String uuid, OrderByComparator<ObjectAction> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectActionException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first object action in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public static ObjectAction fetchByUuid_First(
		String uuid, OrderByComparator<ObjectAction> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the last object action in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public static ObjectAction findByUuid_Last(
			String uuid, OrderByComparator<ObjectAction> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectActionException {

		return getPersistence().findByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the last object action in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public static ObjectAction fetchByUuid_Last(
		String uuid, OrderByComparator<ObjectAction> orderByComparator) {

		return getPersistence().fetchByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the object actions before and after the current object action in the ordered set where uuid = &#63;.
	 *
	 * @param objectActionId the primary key of the current object action
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object action
	 * @throws NoSuchObjectActionException if a object action with the primary key could not be found
	 */
	public static ObjectAction[] findByUuid_PrevAndNext(
			long objectActionId, String uuid,
			OrderByComparator<ObjectAction> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectActionException {

		return getPersistence().findByUuid_PrevAndNext(
			objectActionId, uuid, orderByComparator);
	}

	/**
	 * Removes all the object actions where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of object actions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object actions
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns all the object actions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching object actions
	 */
	public static List<ObjectAction> findByUuid_C(String uuid, long companyId) {
		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the object actions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @return the range of matching object actions
	 */
	public static List<ObjectAction> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the object actions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object actions
	 */
	public static List<ObjectAction> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectAction> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object actions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object actions
	 */
	public static List<ObjectAction> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectAction> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object action in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public static ObjectAction findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectAction> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectActionException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first object action in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public static ObjectAction fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectAction> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last object action in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public static ObjectAction findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<ObjectAction> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectActionException {

		return getPersistence().findByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last object action in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public static ObjectAction fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<ObjectAction> orderByComparator) {

		return getPersistence().fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the object actions before and after the current object action in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param objectActionId the primary key of the current object action
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object action
	 * @throws NoSuchObjectActionException if a object action with the primary key could not be found
	 */
	public static ObjectAction[] findByUuid_C_PrevAndNext(
			long objectActionId, String uuid, long companyId,
			OrderByComparator<ObjectAction> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectActionException {

		return getPersistence().findByUuid_C_PrevAndNext(
			objectActionId, uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the object actions where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of object actions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object actions
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns all the object actions where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object actions
	 */
	public static List<ObjectAction> findByObjectDefinitionId(
		long objectDefinitionId) {

		return getPersistence().findByObjectDefinitionId(objectDefinitionId);
	}

	/**
	 * Returns a range of all the object actions where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @return the range of matching object actions
	 */
	public static List<ObjectAction> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end) {

		return getPersistence().findByObjectDefinitionId(
			objectDefinitionId, start, end);
	}

	/**
	 * Returns an ordered range of all the object actions where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object actions
	 */
	public static List<ObjectAction> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectAction> orderByComparator) {

		return getPersistence().findByObjectDefinitionId(
			objectDefinitionId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object actions where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object actions
	 */
	public static List<ObjectAction> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectAction> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByObjectDefinitionId(
			objectDefinitionId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object action in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public static ObjectAction findByObjectDefinitionId_First(
			long objectDefinitionId,
			OrderByComparator<ObjectAction> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectActionException {

		return getPersistence().findByObjectDefinitionId_First(
			objectDefinitionId, orderByComparator);
	}

	/**
	 * Returns the first object action in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public static ObjectAction fetchByObjectDefinitionId_First(
		long objectDefinitionId,
		OrderByComparator<ObjectAction> orderByComparator) {

		return getPersistence().fetchByObjectDefinitionId_First(
			objectDefinitionId, orderByComparator);
	}

	/**
	 * Returns the last object action in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public static ObjectAction findByObjectDefinitionId_Last(
			long objectDefinitionId,
			OrderByComparator<ObjectAction> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectActionException {

		return getPersistence().findByObjectDefinitionId_Last(
			objectDefinitionId, orderByComparator);
	}

	/**
	 * Returns the last object action in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public static ObjectAction fetchByObjectDefinitionId_Last(
		long objectDefinitionId,
		OrderByComparator<ObjectAction> orderByComparator) {

		return getPersistence().fetchByObjectDefinitionId_Last(
			objectDefinitionId, orderByComparator);
	}

	/**
	 * Returns the object actions before and after the current object action in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectActionId the primary key of the current object action
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object action
	 * @throws NoSuchObjectActionException if a object action with the primary key could not be found
	 */
	public static ObjectAction[] findByObjectDefinitionId_PrevAndNext(
			long objectActionId, long objectDefinitionId,
			OrderByComparator<ObjectAction> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectActionException {

		return getPersistence().findByObjectDefinitionId_PrevAndNext(
			objectActionId, objectDefinitionId, orderByComparator);
	}

	/**
	 * Removes all the object actions where objectDefinitionId = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 */
	public static void removeByObjectDefinitionId(long objectDefinitionId) {
		getPersistence().removeByObjectDefinitionId(objectDefinitionId);
	}

	/**
	 * Returns the number of object actions where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object actions
	 */
	public static int countByObjectDefinitionId(long objectDefinitionId) {
		return getPersistence().countByObjectDefinitionId(objectDefinitionId);
	}

	/**
	 * Returns the object action where objectDefinitionId = &#63; and name = &#63; or throws a <code>NoSuchObjectActionException</code> if it could not be found.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public static ObjectAction findByODI_N(long objectDefinitionId, String name)
		throws com.liferay.object.exception.NoSuchObjectActionException {

		return getPersistence().findByODI_N(objectDefinitionId, name);
	}

	/**
	 * Returns the object action where objectDefinitionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public static ObjectAction fetchByODI_N(
		long objectDefinitionId, String name) {

		return getPersistence().fetchByODI_N(objectDefinitionId, name);
	}

	/**
	 * Returns the object action where objectDefinitionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public static ObjectAction fetchByODI_N(
		long objectDefinitionId, String name, boolean useFinderCache) {

		return getPersistence().fetchByODI_N(
			objectDefinitionId, name, useFinderCache);
	}

	/**
	 * Removes the object action where objectDefinitionId = &#63; and name = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the object action that was removed
	 */
	public static ObjectAction removeByODI_N(
			long objectDefinitionId, String name)
		throws com.liferay.object.exception.NoSuchObjectActionException {

		return getPersistence().removeByODI_N(objectDefinitionId, name);
	}

	/**
	 * Returns the number of object actions where objectDefinitionId = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the number of matching object actions
	 */
	public static int countByODI_N(long objectDefinitionId, String name) {
		return getPersistence().countByODI_N(objectDefinitionId, name);
	}

	/**
	 * Returns all the object actions where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @return the matching object actions
	 */
	public static List<ObjectAction> findByA_OAEK(
		boolean active, String objectActionExecutorKey) {

		return getPersistence().findByA_OAEK(active, objectActionExecutorKey);
	}

	/**
	 * Returns a range of all the object actions where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @return the range of matching object actions
	 */
	public static List<ObjectAction> findByA_OAEK(
		boolean active, String objectActionExecutorKey, int start, int end) {

		return getPersistence().findByA_OAEK(
			active, objectActionExecutorKey, start, end);
	}

	/**
	 * Returns an ordered range of all the object actions where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object actions
	 */
	public static List<ObjectAction> findByA_OAEK(
		boolean active, String objectActionExecutorKey, int start, int end,
		OrderByComparator<ObjectAction> orderByComparator) {

		return getPersistence().findByA_OAEK(
			active, objectActionExecutorKey, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object actions where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object actions
	 */
	public static List<ObjectAction> findByA_OAEK(
		boolean active, String objectActionExecutorKey, int start, int end,
		OrderByComparator<ObjectAction> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByA_OAEK(
			active, objectActionExecutorKey, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object action in the ordered set where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public static ObjectAction findByA_OAEK_First(
			boolean active, String objectActionExecutorKey,
			OrderByComparator<ObjectAction> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectActionException {

		return getPersistence().findByA_OAEK_First(
			active, objectActionExecutorKey, orderByComparator);
	}

	/**
	 * Returns the first object action in the ordered set where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public static ObjectAction fetchByA_OAEK_First(
		boolean active, String objectActionExecutorKey,
		OrderByComparator<ObjectAction> orderByComparator) {

		return getPersistence().fetchByA_OAEK_First(
			active, objectActionExecutorKey, orderByComparator);
	}

	/**
	 * Returns the last object action in the ordered set where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public static ObjectAction findByA_OAEK_Last(
			boolean active, String objectActionExecutorKey,
			OrderByComparator<ObjectAction> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectActionException {

		return getPersistence().findByA_OAEK_Last(
			active, objectActionExecutorKey, orderByComparator);
	}

	/**
	 * Returns the last object action in the ordered set where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public static ObjectAction fetchByA_OAEK_Last(
		boolean active, String objectActionExecutorKey,
		OrderByComparator<ObjectAction> orderByComparator) {

		return getPersistence().fetchByA_OAEK_Last(
			active, objectActionExecutorKey, orderByComparator);
	}

	/**
	 * Returns the object actions before and after the current object action in the ordered set where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * @param objectActionId the primary key of the current object action
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object action
	 * @throws NoSuchObjectActionException if a object action with the primary key could not be found
	 */
	public static ObjectAction[] findByA_OAEK_PrevAndNext(
			long objectActionId, boolean active, String objectActionExecutorKey,
			OrderByComparator<ObjectAction> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectActionException {

		return getPersistence().findByA_OAEK_PrevAndNext(
			objectActionId, active, objectActionExecutorKey, orderByComparator);
	}

	/**
	 * Removes all the object actions where active = &#63; and objectActionExecutorKey = &#63; from the database.
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 */
	public static void removeByA_OAEK(
		boolean active, String objectActionExecutorKey) {

		getPersistence().removeByA_OAEK(active, objectActionExecutorKey);
	}

	/**
	 * Returns the number of object actions where active = &#63; and objectActionExecutorKey = &#63;.
	 *
	 * @param active the active
	 * @param objectActionExecutorKey the object action executor key
	 * @return the number of matching object actions
	 */
	public static int countByA_OAEK(
		boolean active, String objectActionExecutorKey) {

		return getPersistence().countByA_OAEK(active, objectActionExecutorKey);
	}

	/**
	 * Returns the object action where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; or throws a <code>NoSuchObjectActionException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public static ObjectAction findByERC_C_ODI(
			String externalReferenceCode, long companyId,
			long objectDefinitionId)
		throws com.liferay.object.exception.NoSuchObjectActionException {

		return getPersistence().findByERC_C_ODI(
			externalReferenceCode, companyId, objectDefinitionId);
	}

	/**
	 * Returns the object action where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public static ObjectAction fetchByERC_C_ODI(
		String externalReferenceCode, long companyId, long objectDefinitionId) {

		return getPersistence().fetchByERC_C_ODI(
			externalReferenceCode, companyId, objectDefinitionId);
	}

	/**
	 * Returns the object action where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public static ObjectAction fetchByERC_C_ODI(
		String externalReferenceCode, long companyId, long objectDefinitionId,
		boolean useFinderCache) {

		return getPersistence().fetchByERC_C_ODI(
			externalReferenceCode, companyId, objectDefinitionId,
			useFinderCache);
	}

	/**
	 * Removes the object action where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the object action that was removed
	 */
	public static ObjectAction removeByERC_C_ODI(
			String externalReferenceCode, long companyId,
			long objectDefinitionId)
		throws com.liferay.object.exception.NoSuchObjectActionException {

		return getPersistence().removeByERC_C_ODI(
			externalReferenceCode, companyId, objectDefinitionId);
	}

	/**
	 * Returns the number of object actions where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object actions
	 */
	public static int countByERC_C_ODI(
		String externalReferenceCode, long companyId, long objectDefinitionId) {

		return getPersistence().countByERC_C_ODI(
			externalReferenceCode, companyId, objectDefinitionId);
	}

	/**
	 * Returns all the object actions where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @return the matching object actions
	 */
	public static List<ObjectAction> findByC_A_OATK(
		long companyId, boolean active, String objectActionTriggerKey) {

		return getPersistence().findByC_A_OATK(
			companyId, active, objectActionTriggerKey);
	}

	/**
	 * Returns a range of all the object actions where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @return the range of matching object actions
	 */
	public static List<ObjectAction> findByC_A_OATK(
		long companyId, boolean active, String objectActionTriggerKey,
		int start, int end) {

		return getPersistence().findByC_A_OATK(
			companyId, active, objectActionTriggerKey, start, end);
	}

	/**
	 * Returns an ordered range of all the object actions where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object actions
	 */
	public static List<ObjectAction> findByC_A_OATK(
		long companyId, boolean active, String objectActionTriggerKey,
		int start, int end, OrderByComparator<ObjectAction> orderByComparator) {

		return getPersistence().findByC_A_OATK(
			companyId, active, objectActionTriggerKey, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object actions where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object actions
	 */
	public static List<ObjectAction> findByC_A_OATK(
		long companyId, boolean active, String objectActionTriggerKey,
		int start, int end, OrderByComparator<ObjectAction> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_A_OATK(
			companyId, active, objectActionTriggerKey, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object action in the ordered set where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public static ObjectAction findByC_A_OATK_First(
			long companyId, boolean active, String objectActionTriggerKey,
			OrderByComparator<ObjectAction> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectActionException {

		return getPersistence().findByC_A_OATK_First(
			companyId, active, objectActionTriggerKey, orderByComparator);
	}

	/**
	 * Returns the first object action in the ordered set where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public static ObjectAction fetchByC_A_OATK_First(
		long companyId, boolean active, String objectActionTriggerKey,
		OrderByComparator<ObjectAction> orderByComparator) {

		return getPersistence().fetchByC_A_OATK_First(
			companyId, active, objectActionTriggerKey, orderByComparator);
	}

	/**
	 * Returns the last object action in the ordered set where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public static ObjectAction findByC_A_OATK_Last(
			long companyId, boolean active, String objectActionTriggerKey,
			OrderByComparator<ObjectAction> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectActionException {

		return getPersistence().findByC_A_OATK_Last(
			companyId, active, objectActionTriggerKey, orderByComparator);
	}

	/**
	 * Returns the last object action in the ordered set where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public static ObjectAction fetchByC_A_OATK_Last(
		long companyId, boolean active, String objectActionTriggerKey,
		OrderByComparator<ObjectAction> orderByComparator) {

		return getPersistence().fetchByC_A_OATK_Last(
			companyId, active, objectActionTriggerKey, orderByComparator);
	}

	/**
	 * Returns the object actions before and after the current object action in the ordered set where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectActionId the primary key of the current object action
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object action
	 * @throws NoSuchObjectActionException if a object action with the primary key could not be found
	 */
	public static ObjectAction[] findByC_A_OATK_PrevAndNext(
			long objectActionId, long companyId, boolean active,
			String objectActionTriggerKey,
			OrderByComparator<ObjectAction> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectActionException {

		return getPersistence().findByC_A_OATK_PrevAndNext(
			objectActionId, companyId, active, objectActionTriggerKey,
			orderByComparator);
	}

	/**
	 * Removes all the object actions where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 */
	public static void removeByC_A_OATK(
		long companyId, boolean active, String objectActionTriggerKey) {

		getPersistence().removeByC_A_OATK(
			companyId, active, objectActionTriggerKey);
	}

	/**
	 * Returns the number of object actions where companyId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @return the number of matching object actions
	 */
	public static int countByC_A_OATK(
		long companyId, boolean active, String objectActionTriggerKey) {

		return getPersistence().countByC_A_OATK(
			companyId, active, objectActionTriggerKey);
	}

	/**
	 * Returns all the object actions where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @return the matching object actions
	 */
	public static List<ObjectAction> findByO_A_OATK(
		long objectDefinitionId, boolean active,
		String objectActionTriggerKey) {

		return getPersistence().findByO_A_OATK(
			objectDefinitionId, active, objectActionTriggerKey);
	}

	/**
	 * Returns a range of all the object actions where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @return the range of matching object actions
	 */
	public static List<ObjectAction> findByO_A_OATK(
		long objectDefinitionId, boolean active, String objectActionTriggerKey,
		int start, int end) {

		return getPersistence().findByO_A_OATK(
			objectDefinitionId, active, objectActionTriggerKey, start, end);
	}

	/**
	 * Returns an ordered range of all the object actions where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object actions
	 */
	public static List<ObjectAction> findByO_A_OATK(
		long objectDefinitionId, boolean active, String objectActionTriggerKey,
		int start, int end, OrderByComparator<ObjectAction> orderByComparator) {

		return getPersistence().findByO_A_OATK(
			objectDefinitionId, active, objectActionTriggerKey, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object actions where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object actions
	 */
	public static List<ObjectAction> findByO_A_OATK(
		long objectDefinitionId, boolean active, String objectActionTriggerKey,
		int start, int end, OrderByComparator<ObjectAction> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByO_A_OATK(
			objectDefinitionId, active, objectActionTriggerKey, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object action in the ordered set where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public static ObjectAction findByO_A_OATK_First(
			long objectDefinitionId, boolean active,
			String objectActionTriggerKey,
			OrderByComparator<ObjectAction> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectActionException {

		return getPersistence().findByO_A_OATK_First(
			objectDefinitionId, active, objectActionTriggerKey,
			orderByComparator);
	}

	/**
	 * Returns the first object action in the ordered set where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public static ObjectAction fetchByO_A_OATK_First(
		long objectDefinitionId, boolean active, String objectActionTriggerKey,
		OrderByComparator<ObjectAction> orderByComparator) {

		return getPersistence().fetchByO_A_OATK_First(
			objectDefinitionId, active, objectActionTriggerKey,
			orderByComparator);
	}

	/**
	 * Returns the last object action in the ordered set where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public static ObjectAction findByO_A_OATK_Last(
			long objectDefinitionId, boolean active,
			String objectActionTriggerKey,
			OrderByComparator<ObjectAction> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectActionException {

		return getPersistence().findByO_A_OATK_Last(
			objectDefinitionId, active, objectActionTriggerKey,
			orderByComparator);
	}

	/**
	 * Returns the last object action in the ordered set where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public static ObjectAction fetchByO_A_OATK_Last(
		long objectDefinitionId, boolean active, String objectActionTriggerKey,
		OrderByComparator<ObjectAction> orderByComparator) {

		return getPersistence().fetchByO_A_OATK_Last(
			objectDefinitionId, active, objectActionTriggerKey,
			orderByComparator);
	}

	/**
	 * Returns the object actions before and after the current object action in the ordered set where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectActionId the primary key of the current object action
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object action
	 * @throws NoSuchObjectActionException if a object action with the primary key could not be found
	 */
	public static ObjectAction[] findByO_A_OATK_PrevAndNext(
			long objectActionId, long objectDefinitionId, boolean active,
			String objectActionTriggerKey,
			OrderByComparator<ObjectAction> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectActionException {

		return getPersistence().findByO_A_OATK_PrevAndNext(
			objectActionId, objectDefinitionId, active, objectActionTriggerKey,
			orderByComparator);
	}

	/**
	 * Removes all the object actions where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 */
	public static void removeByO_A_OATK(
		long objectDefinitionId, boolean active,
		String objectActionTriggerKey) {

		getPersistence().removeByO_A_OATK(
			objectDefinitionId, active, objectActionTriggerKey);
	}

	/**
	 * Returns the number of object actions where objectDefinitionId = &#63; and active = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param objectActionTriggerKey the object action trigger key
	 * @return the number of matching object actions
	 */
	public static int countByO_A_OATK(
		long objectDefinitionId, boolean active,
		String objectActionTriggerKey) {

		return getPersistence().countByO_A_OATK(
			objectDefinitionId, active, objectActionTriggerKey);
	}

	/**
	 * Returns the object action where objectDefinitionId = &#63; and active = &#63; and name = &#63; and objectActionTriggerKey = &#63; or throws a <code>NoSuchObjectActionException</code> if it could not be found.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param name the name
	 * @param objectActionTriggerKey the object action trigger key
	 * @return the matching object action
	 * @throws NoSuchObjectActionException if a matching object action could not be found
	 */
	public static ObjectAction findByODI_A_N_OATK(
			long objectDefinitionId, boolean active, String name,
			String objectActionTriggerKey)
		throws com.liferay.object.exception.NoSuchObjectActionException {

		return getPersistence().findByODI_A_N_OATK(
			objectDefinitionId, active, name, objectActionTriggerKey);
	}

	/**
	 * Returns the object action where objectDefinitionId = &#63; and active = &#63; and name = &#63; and objectActionTriggerKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param name the name
	 * @param objectActionTriggerKey the object action trigger key
	 * @return the matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public static ObjectAction fetchByODI_A_N_OATK(
		long objectDefinitionId, boolean active, String name,
		String objectActionTriggerKey) {

		return getPersistence().fetchByODI_A_N_OATK(
			objectDefinitionId, active, name, objectActionTriggerKey);
	}

	/**
	 * Returns the object action where objectDefinitionId = &#63; and active = &#63; and name = &#63; and objectActionTriggerKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param name the name
	 * @param objectActionTriggerKey the object action trigger key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object action, or <code>null</code> if a matching object action could not be found
	 */
	public static ObjectAction fetchByODI_A_N_OATK(
		long objectDefinitionId, boolean active, String name,
		String objectActionTriggerKey, boolean useFinderCache) {

		return getPersistence().fetchByODI_A_N_OATK(
			objectDefinitionId, active, name, objectActionTriggerKey,
			useFinderCache);
	}

	/**
	 * Removes the object action where objectDefinitionId = &#63; and active = &#63; and name = &#63; and objectActionTriggerKey = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param name the name
	 * @param objectActionTriggerKey the object action trigger key
	 * @return the object action that was removed
	 */
	public static ObjectAction removeByODI_A_N_OATK(
			long objectDefinitionId, boolean active, String name,
			String objectActionTriggerKey)
		throws com.liferay.object.exception.NoSuchObjectActionException {

		return getPersistence().removeByODI_A_N_OATK(
			objectDefinitionId, active, name, objectActionTriggerKey);
	}

	/**
	 * Returns the number of object actions where objectDefinitionId = &#63; and active = &#63; and name = &#63; and objectActionTriggerKey = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param name the name
	 * @param objectActionTriggerKey the object action trigger key
	 * @return the number of matching object actions
	 */
	public static int countByODI_A_N_OATK(
		long objectDefinitionId, boolean active, String name,
		String objectActionTriggerKey) {

		return getPersistence().countByODI_A_N_OATK(
			objectDefinitionId, active, name, objectActionTriggerKey);
	}

	/**
	 * Caches the object action in the entity cache if it is enabled.
	 *
	 * @param objectAction the object action
	 */
	public static void cacheResult(ObjectAction objectAction) {
		getPersistence().cacheResult(objectAction);
	}

	/**
	 * Caches the object actions in the entity cache if it is enabled.
	 *
	 * @param objectActions the object actions
	 */
	public static void cacheResult(List<ObjectAction> objectActions) {
		getPersistence().cacheResult(objectActions);
	}

	/**
	 * Creates a new object action with the primary key. Does not add the object action to the database.
	 *
	 * @param objectActionId the primary key for the new object action
	 * @return the new object action
	 */
	public static ObjectAction create(long objectActionId) {
		return getPersistence().create(objectActionId);
	}

	/**
	 * Removes the object action with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectActionId the primary key of the object action
	 * @return the object action that was removed
	 * @throws NoSuchObjectActionException if a object action with the primary key could not be found
	 */
	public static ObjectAction remove(long objectActionId)
		throws com.liferay.object.exception.NoSuchObjectActionException {

		return getPersistence().remove(objectActionId);
	}

	public static ObjectAction updateImpl(ObjectAction objectAction) {
		return getPersistence().updateImpl(objectAction);
	}

	/**
	 * Returns the object action with the primary key or throws a <code>NoSuchObjectActionException</code> if it could not be found.
	 *
	 * @param objectActionId the primary key of the object action
	 * @return the object action
	 * @throws NoSuchObjectActionException if a object action with the primary key could not be found
	 */
	public static ObjectAction findByPrimaryKey(long objectActionId)
		throws com.liferay.object.exception.NoSuchObjectActionException {

		return getPersistence().findByPrimaryKey(objectActionId);
	}

	/**
	 * Returns the object action with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectActionId the primary key of the object action
	 * @return the object action, or <code>null</code> if a object action with the primary key could not be found
	 */
	public static ObjectAction fetchByPrimaryKey(long objectActionId) {
		return getPersistence().fetchByPrimaryKey(objectActionId);
	}

	/**
	 * Returns all the object actions.
	 *
	 * @return the object actions
	 */
	public static List<ObjectAction> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the object actions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @return the range of object actions
	 */
	public static List<ObjectAction> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the object actions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of object actions
	 */
	public static List<ObjectAction> findAll(
		int start, int end, OrderByComparator<ObjectAction> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object actions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectActionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object actions
	 * @param end the upper bound of the range of object actions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of object actions
	 */
	public static List<ObjectAction> findAll(
		int start, int end, OrderByComparator<ObjectAction> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the object actions from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of object actions.
	 *
	 * @return the number of object actions
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static ObjectActionPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(ObjectActionPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile ObjectActionPersistence _persistence;

}
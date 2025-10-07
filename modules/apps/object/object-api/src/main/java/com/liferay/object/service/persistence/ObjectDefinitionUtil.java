/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the object definition service. This utility wraps <code>com.liferay.object.service.persistence.impl.ObjectDefinitionPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see ObjectDefinitionPersistence
 * @generated
 */
public class ObjectDefinitionUtil {

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
	public static void clearCache(ObjectDefinition objectDefinition) {
		getPersistence().clearCache(objectDefinition);
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
	public static Map<Serializable, ObjectDefinition> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<ObjectDefinition> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<ObjectDefinition> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<ObjectDefinition> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static ObjectDefinition update(ObjectDefinition objectDefinition) {
		return getPersistence().update(objectDefinition);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static ObjectDefinition update(
		ObjectDefinition objectDefinition, ServiceContext serviceContext) {

		return getPersistence().update(objectDefinition, serviceContext);
	}

	/**
	 * Returns all the object definitions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching object definitions
	 */
	public static List<ObjectDefinition> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the object definitions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions
	 */
	public static List<ObjectDefinition> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the object definitions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions
	 */
	public static List<ObjectDefinition> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definitions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	public static List<ObjectDefinition> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object definition in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findByUuid_First(
			String uuid, OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first object definition in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByUuid_First(
		String uuid, OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the last object definition in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findByUuid_Last(
			String uuid, OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the last object definition in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByUuid_Last(
		String uuid, OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().fetchByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set where uuid = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition[] findByUuid_PrevAndNext(
			long objectDefinitionId, String uuid,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByUuid_PrevAndNext(
			objectDefinitionId, uuid, orderByComparator);
	}

	/**
	 * Returns all the object definitions that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByUuid(String uuid) {
		return getPersistence().filterFindByUuid(uuid);
	}

	/**
	 * Returns a range of all the object definitions that the user has permission to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByUuid(
		String uuid, int start, int end) {

		return getPersistence().filterFindByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().filterFindByUuid(
			uuid, start, end, orderByComparator);
	}

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set of object definitions that the user has permission to view where uuid = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition[] filterFindByUuid_PrevAndNext(
			long objectDefinitionId, String uuid,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().filterFindByUuid_PrevAndNext(
			objectDefinitionId, uuid, orderByComparator);
	}

	/**
	 * Removes all the object definitions where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of object definitions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object definitions
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the number of object definitions that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object definitions that the user has permission to view
	 */
	public static int filterCountByUuid(String uuid) {
		return getPersistence().filterCountByUuid(uuid);
	}

	/**
	 * Returns all the object definitions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching object definitions
	 */
	public static List<ObjectDefinition> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the object definitions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions
	 */
	public static List<ObjectDefinition> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the object definitions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions
	 */
	public static List<ObjectDefinition> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definitions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	public static List<ObjectDefinition> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object definition in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first object definition in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last object definition in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last object definition in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition[] findByUuid_C_PrevAndNext(
			long objectDefinitionId, String uuid, long companyId,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByUuid_C_PrevAndNext(
			objectDefinitionId, uuid, companyId, orderByComparator);
	}

	/**
	 * Returns all the object definitions that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByUuid_C(
		String uuid, long companyId) {

		return getPersistence().filterFindByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the object definitions that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().filterFindByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().filterFindByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set of object definitions that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition[] filterFindByUuid_C_PrevAndNext(
			long objectDefinitionId, String uuid, long companyId,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().filterFindByUuid_C_PrevAndNext(
			objectDefinitionId, uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the object definitions where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of object definitions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object definitions
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of object definitions that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object definitions that the user has permission to view
	 */
	public static int filterCountByUuid_C(String uuid, long companyId) {
		return getPersistence().filterCountByUuid_C(uuid, companyId);
	}

	/**
	 * Returns all the object definitions where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching object definitions
	 */
	public static List<ObjectDefinition> findByCompanyId(long companyId) {
		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the object definitions where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions
	 */
	public static List<ObjectDefinition> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions
	 */
	public static List<ObjectDefinition> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	public static List<ObjectDefinition> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findByCompanyId_First(
			long companyId,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByCompanyId_First(
		long companyId, OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last object definition in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findByCompanyId_Last(
			long companyId,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last object definition in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByCompanyId_Last(
		long companyId, OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().fetchByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set where companyId = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition[] findByCompanyId_PrevAndNext(
			long objectDefinitionId, long companyId,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByCompanyId_PrevAndNext(
			objectDefinitionId, companyId, orderByComparator);
	}

	/**
	 * Returns all the object definitions that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByCompanyId(long companyId) {
		return getPersistence().filterFindByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the object definitions that the user has permission to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().filterFindByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().filterFindByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set of object definitions that the user has permission to view where companyId = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition[] filterFindByCompanyId_PrevAndNext(
			long objectDefinitionId, long companyId,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().filterFindByCompanyId_PrevAndNext(
			objectDefinitionId, companyId, orderByComparator);
	}

	/**
	 * Removes all the object definitions where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of object definitions where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching object definitions
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns the number of object definitions that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching object definitions that the user has permission to view
	 */
	public static int filterCountByCompanyId(long companyId) {
		return getPersistence().filterCountByCompanyId(companyId);
	}

	/**
	 * Returns all the object definitions where objectFolderId = &#63;.
	 *
	 * @param objectFolderId the object folder ID
	 * @return the matching object definitions
	 */
	public static List<ObjectDefinition> findByObjectFolderId(
		long objectFolderId) {

		return getPersistence().findByObjectFolderId(objectFolderId);
	}

	/**
	 * Returns a range of all the object definitions where objectFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param objectFolderId the object folder ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions
	 */
	public static List<ObjectDefinition> findByObjectFolderId(
		long objectFolderId, int start, int end) {

		return getPersistence().findByObjectFolderId(
			objectFolderId, start, end);
	}

	/**
	 * Returns an ordered range of all the object definitions where objectFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param objectFolderId the object folder ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions
	 */
	public static List<ObjectDefinition> findByObjectFolderId(
		long objectFolderId, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().findByObjectFolderId(
			objectFolderId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definitions where objectFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param objectFolderId the object folder ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	public static List<ObjectDefinition> findByObjectFolderId(
		long objectFolderId, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByObjectFolderId(
			objectFolderId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object definition in the ordered set where objectFolderId = &#63;.
	 *
	 * @param objectFolderId the object folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findByObjectFolderId_First(
			long objectFolderId,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByObjectFolderId_First(
			objectFolderId, orderByComparator);
	}

	/**
	 * Returns the first object definition in the ordered set where objectFolderId = &#63;.
	 *
	 * @param objectFolderId the object folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByObjectFolderId_First(
		long objectFolderId,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().fetchByObjectFolderId_First(
			objectFolderId, orderByComparator);
	}

	/**
	 * Returns the last object definition in the ordered set where objectFolderId = &#63;.
	 *
	 * @param objectFolderId the object folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findByObjectFolderId_Last(
			long objectFolderId,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByObjectFolderId_Last(
			objectFolderId, orderByComparator);
	}

	/**
	 * Returns the last object definition in the ordered set where objectFolderId = &#63;.
	 *
	 * @param objectFolderId the object folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByObjectFolderId_Last(
		long objectFolderId,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().fetchByObjectFolderId_Last(
			objectFolderId, orderByComparator);
	}

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set where objectFolderId = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param objectFolderId the object folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition[] findByObjectFolderId_PrevAndNext(
			long objectDefinitionId, long objectFolderId,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByObjectFolderId_PrevAndNext(
			objectDefinitionId, objectFolderId, orderByComparator);
	}

	/**
	 * Returns all the object definitions that the user has permission to view where objectFolderId = &#63;.
	 *
	 * @param objectFolderId the object folder ID
	 * @return the matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByObjectFolderId(
		long objectFolderId) {

		return getPersistence().filterFindByObjectFolderId(objectFolderId);
	}

	/**
	 * Returns a range of all the object definitions that the user has permission to view where objectFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param objectFolderId the object folder ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByObjectFolderId(
		long objectFolderId, int start, int end) {

		return getPersistence().filterFindByObjectFolderId(
			objectFolderId, start, end);
	}

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where objectFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param objectFolderId the object folder ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByObjectFolderId(
		long objectFolderId, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().filterFindByObjectFolderId(
			objectFolderId, start, end, orderByComparator);
	}

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set of object definitions that the user has permission to view where objectFolderId = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param objectFolderId the object folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition[] filterFindByObjectFolderId_PrevAndNext(
			long objectDefinitionId, long objectFolderId,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().filterFindByObjectFolderId_PrevAndNext(
			objectDefinitionId, objectFolderId, orderByComparator);
	}

	/**
	 * Removes all the object definitions where objectFolderId = &#63; from the database.
	 *
	 * @param objectFolderId the object folder ID
	 */
	public static void removeByObjectFolderId(long objectFolderId) {
		getPersistence().removeByObjectFolderId(objectFolderId);
	}

	/**
	 * Returns the number of object definitions where objectFolderId = &#63;.
	 *
	 * @param objectFolderId the object folder ID
	 * @return the number of matching object definitions
	 */
	public static int countByObjectFolderId(long objectFolderId) {
		return getPersistence().countByObjectFolderId(objectFolderId);
	}

	/**
	 * Returns the number of object definitions that the user has permission to view where objectFolderId = &#63;.
	 *
	 * @param objectFolderId the object folder ID
	 * @return the number of matching object definitions that the user has permission to view
	 */
	public static int filterCountByObjectFolderId(long objectFolderId) {
		return getPersistence().filterCountByObjectFolderId(objectFolderId);
	}

	/**
	 * Returns all the object definitions where accountEntryRestricted = &#63;.
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @return the matching object definitions
	 */
	public static List<ObjectDefinition> findByAccountEntryRestricted(
		boolean accountEntryRestricted) {

		return getPersistence().findByAccountEntryRestricted(
			accountEntryRestricted);
	}

	/**
	 * Returns a range of all the object definitions where accountEntryRestricted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions
	 */
	public static List<ObjectDefinition> findByAccountEntryRestricted(
		boolean accountEntryRestricted, int start, int end) {

		return getPersistence().findByAccountEntryRestricted(
			accountEntryRestricted, start, end);
	}

	/**
	 * Returns an ordered range of all the object definitions where accountEntryRestricted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions
	 */
	public static List<ObjectDefinition> findByAccountEntryRestricted(
		boolean accountEntryRestricted, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().findByAccountEntryRestricted(
			accountEntryRestricted, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definitions where accountEntryRestricted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	public static List<ObjectDefinition> findByAccountEntryRestricted(
		boolean accountEntryRestricted, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByAccountEntryRestricted(
			accountEntryRestricted, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object definition in the ordered set where accountEntryRestricted = &#63;.
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findByAccountEntryRestricted_First(
			boolean accountEntryRestricted,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByAccountEntryRestricted_First(
			accountEntryRestricted, orderByComparator);
	}

	/**
	 * Returns the first object definition in the ordered set where accountEntryRestricted = &#63;.
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByAccountEntryRestricted_First(
		boolean accountEntryRestricted,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().fetchByAccountEntryRestricted_First(
			accountEntryRestricted, orderByComparator);
	}

	/**
	 * Returns the last object definition in the ordered set where accountEntryRestricted = &#63;.
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findByAccountEntryRestricted_Last(
			boolean accountEntryRestricted,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByAccountEntryRestricted_Last(
			accountEntryRestricted, orderByComparator);
	}

	/**
	 * Returns the last object definition in the ordered set where accountEntryRestricted = &#63;.
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByAccountEntryRestricted_Last(
		boolean accountEntryRestricted,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().fetchByAccountEntryRestricted_Last(
			accountEntryRestricted, orderByComparator);
	}

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set where accountEntryRestricted = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param accountEntryRestricted the account entry restricted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition[] findByAccountEntryRestricted_PrevAndNext(
			long objectDefinitionId, boolean accountEntryRestricted,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByAccountEntryRestricted_PrevAndNext(
			objectDefinitionId, accountEntryRestricted, orderByComparator);
	}

	/**
	 * Returns all the object definitions that the user has permission to view where accountEntryRestricted = &#63;.
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @return the matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByAccountEntryRestricted(
		boolean accountEntryRestricted) {

		return getPersistence().filterFindByAccountEntryRestricted(
			accountEntryRestricted);
	}

	/**
	 * Returns a range of all the object definitions that the user has permission to view where accountEntryRestricted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByAccountEntryRestricted(
		boolean accountEntryRestricted, int start, int end) {

		return getPersistence().filterFindByAccountEntryRestricted(
			accountEntryRestricted, start, end);
	}

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where accountEntryRestricted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByAccountEntryRestricted(
		boolean accountEntryRestricted, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().filterFindByAccountEntryRestricted(
			accountEntryRestricted, start, end, orderByComparator);
	}

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set of object definitions that the user has permission to view where accountEntryRestricted = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param accountEntryRestricted the account entry restricted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition[]
			filterFindByAccountEntryRestricted_PrevAndNext(
				long objectDefinitionId, boolean accountEntryRestricted,
				OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().filterFindByAccountEntryRestricted_PrevAndNext(
			objectDefinitionId, accountEntryRestricted, orderByComparator);
	}

	/**
	 * Removes all the object definitions where accountEntryRestricted = &#63; from the database.
	 *
	 * @param accountEntryRestricted the account entry restricted
	 */
	public static void removeByAccountEntryRestricted(
		boolean accountEntryRestricted) {

		getPersistence().removeByAccountEntryRestricted(accountEntryRestricted);
	}

	/**
	 * Returns the number of object definitions where accountEntryRestricted = &#63;.
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @return the number of matching object definitions
	 */
	public static int countByAccountEntryRestricted(
		boolean accountEntryRestricted) {

		return getPersistence().countByAccountEntryRestricted(
			accountEntryRestricted);
	}

	/**
	 * Returns the number of object definitions that the user has permission to view where accountEntryRestricted = &#63;.
	 *
	 * @param accountEntryRestricted the account entry restricted
	 * @return the number of matching object definitions that the user has permission to view
	 */
	public static int filterCountByAccountEntryRestricted(
		boolean accountEntryRestricted) {

		return getPersistence().filterCountByAccountEntryRestricted(
			accountEntryRestricted);
	}

	/**
	 * Returns the object definition where className = &#63; or throws a <code>NoSuchObjectDefinitionException</code> if it could not be found.
	 *
	 * @param className the class name
	 * @return the matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findByClassName(String className)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByClassName(className);
	}

	/**
	 * Returns the object definition where className = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param className the class name
	 * @return the matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByClassName(String className) {
		return getPersistence().fetchByClassName(className);
	}

	/**
	 * Returns the object definition where className = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param className the class name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByClassName(
		String className, boolean useFinderCache) {

		return getPersistence().fetchByClassName(className, useFinderCache);
	}

	/**
	 * Removes the object definition where className = &#63; from the database.
	 *
	 * @param className the class name
	 * @return the object definition that was removed
	 */
	public static ObjectDefinition removeByClassName(String className)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().removeByClassName(className);
	}

	/**
	 * Returns the number of object definitions where className = &#63;.
	 *
	 * @param className the class name
	 * @return the number of matching object definitions
	 */
	public static int countByClassName(String className) {
		return getPersistence().countByClassName(className);
	}

	/**
	 * Returns all the object definitions where system = &#63;.
	 *
	 * @param system the system
	 * @return the matching object definitions
	 */
	public static List<ObjectDefinition> findBySystem(boolean system) {
		return getPersistence().findBySystem(system);
	}

	/**
	 * Returns a range of all the object definitions where system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param system the system
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions
	 */
	public static List<ObjectDefinition> findBySystem(
		boolean system, int start, int end) {

		return getPersistence().findBySystem(system, start, end);
	}

	/**
	 * Returns an ordered range of all the object definitions where system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param system the system
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions
	 */
	public static List<ObjectDefinition> findBySystem(
		boolean system, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().findBySystem(
			system, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definitions where system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param system the system
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	public static List<ObjectDefinition> findBySystem(
		boolean system, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findBySystem(
			system, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object definition in the ordered set where system = &#63;.
	 *
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findBySystem_First(
			boolean system,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findBySystem_First(system, orderByComparator);
	}

	/**
	 * Returns the first object definition in the ordered set where system = &#63;.
	 *
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchBySystem_First(
		boolean system, OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().fetchBySystem_First(system, orderByComparator);
	}

	/**
	 * Returns the last object definition in the ordered set where system = &#63;.
	 *
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findBySystem_Last(
			boolean system,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findBySystem_Last(system, orderByComparator);
	}

	/**
	 * Returns the last object definition in the ordered set where system = &#63;.
	 *
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchBySystem_Last(
		boolean system, OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().fetchBySystem_Last(system, orderByComparator);
	}

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set where system = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition[] findBySystem_PrevAndNext(
			long objectDefinitionId, boolean system,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findBySystem_PrevAndNext(
			objectDefinitionId, system, orderByComparator);
	}

	/**
	 * Returns all the object definitions that the user has permission to view where system = &#63;.
	 *
	 * @param system the system
	 * @return the matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindBySystem(boolean system) {
		return getPersistence().filterFindBySystem(system);
	}

	/**
	 * Returns a range of all the object definitions that the user has permission to view where system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param system the system
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindBySystem(
		boolean system, int start, int end) {

		return getPersistence().filterFindBySystem(system, start, end);
	}

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param system the system
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindBySystem(
		boolean system, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().filterFindBySystem(
			system, start, end, orderByComparator);
	}

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set of object definitions that the user has permission to view where system = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition[] filterFindBySystem_PrevAndNext(
			long objectDefinitionId, boolean system,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().filterFindBySystem_PrevAndNext(
			objectDefinitionId, system, orderByComparator);
	}

	/**
	 * Removes all the object definitions where system = &#63; from the database.
	 *
	 * @param system the system
	 */
	public static void removeBySystem(boolean system) {
		getPersistence().removeBySystem(system);
	}

	/**
	 * Returns the number of object definitions where system = &#63;.
	 *
	 * @param system the system
	 * @return the number of matching object definitions
	 */
	public static int countBySystem(boolean system) {
		return getPersistence().countBySystem(system);
	}

	/**
	 * Returns the number of object definitions that the user has permission to view where system = &#63;.
	 *
	 * @param system the system
	 * @return the number of matching object definitions that the user has permission to view
	 */
	public static int filterCountBySystem(boolean system) {
		return getPersistence().filterCountBySystem(system);
	}

	/**
	 * Returns all the object definitions where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching object definitions
	 */
	public static List<ObjectDefinition> findByC_U(
		long companyId, long userId) {

		return getPersistence().findByC_U(companyId, userId);
	}

	/**
	 * Returns a range of all the object definitions where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions
	 */
	public static List<ObjectDefinition> findByC_U(
		long companyId, long userId, int start, int end) {

		return getPersistence().findByC_U(companyId, userId, start, end);
	}

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions
	 */
	public static List<ObjectDefinition> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().findByC_U(
			companyId, userId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	public static List<ObjectDefinition> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_U(
			companyId, userId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findByC_U_First(
			long companyId, long userId,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByC_U_First(
			companyId, userId, orderByComparator);
	}

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByC_U_First(
		long companyId, long userId,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().fetchByC_U_First(
			companyId, userId, orderByComparator);
	}

	/**
	 * Returns the last object definition in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findByC_U_Last(
			long companyId, long userId,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByC_U_Last(
			companyId, userId, orderByComparator);
	}

	/**
	 * Returns the last object definition in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByC_U_Last(
		long companyId, long userId,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().fetchByC_U_Last(
			companyId, userId, orderByComparator);
	}

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition[] findByC_U_PrevAndNext(
			long objectDefinitionId, long companyId, long userId,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByC_U_PrevAndNext(
			objectDefinitionId, companyId, userId, orderByComparator);
	}

	/**
	 * Returns all the object definitions that the user has permission to view where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByC_U(
		long companyId, long userId) {

		return getPersistence().filterFindByC_U(companyId, userId);
	}

	/**
	 * Returns a range of all the object definitions that the user has permission to view where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByC_U(
		long companyId, long userId, int start, int end) {

		return getPersistence().filterFindByC_U(companyId, userId, start, end);
	}

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().filterFindByC_U(
			companyId, userId, start, end, orderByComparator);
	}

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set of object definitions that the user has permission to view where companyId = &#63; and userId = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition[] filterFindByC_U_PrevAndNext(
			long objectDefinitionId, long companyId, long userId,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().filterFindByC_U_PrevAndNext(
			objectDefinitionId, companyId, userId, orderByComparator);
	}

	/**
	 * Removes all the object definitions where companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 */
	public static void removeByC_U(long companyId, long userId) {
		getPersistence().removeByC_U(companyId, userId);
	}

	/**
	 * Returns the number of object definitions where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching object definitions
	 */
	public static int countByC_U(long companyId, long userId) {
		return getPersistence().countByC_U(companyId, userId);
	}

	/**
	 * Returns the number of object definitions that the user has permission to view where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching object definitions that the user has permission to view
	 */
	public static int filterCountByC_U(long companyId, long userId) {
		return getPersistence().filterCountByC_U(companyId, userId);
	}

	/**
	 * Returns the object definition where companyId = &#63; and className = &#63; or throws a <code>NoSuchObjectDefinitionException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param className the class name
	 * @return the matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findByC_C(long companyId, String className)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByC_C(companyId, className);
	}

	/**
	 * Returns the object definition where companyId = &#63; and className = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param className the class name
	 * @return the matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByC_C(
		long companyId, String className) {

		return getPersistence().fetchByC_C(companyId, className);
	}

	/**
	 * Returns the object definition where companyId = &#63; and className = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param className the class name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByC_C(
		long companyId, String className, boolean useFinderCache) {

		return getPersistence().fetchByC_C(
			companyId, className, useFinderCache);
	}

	/**
	 * Removes the object definition where companyId = &#63; and className = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param className the class name
	 * @return the object definition that was removed
	 */
	public static ObjectDefinition removeByC_C(long companyId, String className)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().removeByC_C(companyId, className);
	}

	/**
	 * Returns the number of object definitions where companyId = &#63; and className = &#63;.
	 *
	 * @param companyId the company ID
	 * @param className the class name
	 * @return the number of matching object definitions
	 */
	public static int countByC_C(long companyId, String className) {
		return getPersistence().countByC_C(companyId, className);
	}

	/**
	 * Returns the object definition where companyId = &#63; and name = &#63; or throws a <code>NoSuchObjectDefinitionException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findByC_N(long companyId, String name)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByC_N(companyId, name);
	}

	/**
	 * Returns the object definition where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByC_N(long companyId, String name) {
		return getPersistence().fetchByC_N(companyId, name);
	}

	/**
	 * Returns the object definition where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByC_N(
		long companyId, String name, boolean useFinderCache) {

		return getPersistence().fetchByC_N(companyId, name, useFinderCache);
	}

	/**
	 * Removes the object definition where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the object definition that was removed
	 */
	public static ObjectDefinition removeByC_N(long companyId, String name)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().removeByC_N(companyId, name);
	}

	/**
	 * Returns the number of object definitions where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching object definitions
	 */
	public static int countByC_N(long companyId, String name) {
		return getPersistence().countByC_N(companyId, name);
	}

	/**
	 * Returns all the object definitions where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching object definitions
	 */
	public static List<ObjectDefinition> findByC_S(long companyId, int status) {
		return getPersistence().findByC_S(companyId, status);
	}

	/**
	 * Returns a range of all the object definitions where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions
	 */
	public static List<ObjectDefinition> findByC_S(
		long companyId, int status, int start, int end) {

		return getPersistence().findByC_S(companyId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions
	 */
	public static List<ObjectDefinition> findByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().findByC_S(
			companyId, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	public static List<ObjectDefinition> findByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_S(
			companyId, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findByC_S_First(
			long companyId, int status,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByC_S_First(
			companyId, status, orderByComparator);
	}

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByC_S_First(
		long companyId, int status,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().fetchByC_S_First(
			companyId, status, orderByComparator);
	}

	/**
	 * Returns the last object definition in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findByC_S_Last(
			long companyId, int status,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByC_S_Last(
			companyId, status, orderByComparator);
	}

	/**
	 * Returns the last object definition in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByC_S_Last(
		long companyId, int status,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().fetchByC_S_Last(
			companyId, status, orderByComparator);
	}

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition[] findByC_S_PrevAndNext(
			long objectDefinitionId, long companyId, int status,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByC_S_PrevAndNext(
			objectDefinitionId, companyId, status, orderByComparator);
	}

	/**
	 * Returns all the object definitions that the user has permission to view where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByC_S(
		long companyId, int status) {

		return getPersistence().filterFindByC_S(companyId, status);
	}

	/**
	 * Returns a range of all the object definitions that the user has permission to view where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByC_S(
		long companyId, int status, int start, int end) {

		return getPersistence().filterFindByC_S(companyId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().filterFindByC_S(
			companyId, status, start, end, orderByComparator);
	}

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set of object definitions that the user has permission to view where companyId = &#63; and status = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition[] filterFindByC_S_PrevAndNext(
			long objectDefinitionId, long companyId, int status,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().filterFindByC_S_PrevAndNext(
			objectDefinitionId, companyId, status, orderByComparator);
	}

	/**
	 * Removes all the object definitions where companyId = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	public static void removeByC_S(long companyId, int status) {
		getPersistence().removeByC_S(companyId, status);
	}

	/**
	 * Returns the number of object definitions where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching object definitions
	 */
	public static int countByC_S(long companyId, int status) {
		return getPersistence().countByC_S(companyId, status);
	}

	/**
	 * Returns the number of object definitions that the user has permission to view where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching object definitions that the user has permission to view
	 */
	public static int filterCountByC_S(long companyId, int status) {
		return getPersistence().filterCountByC_S(companyId, status);
	}

	/**
	 * Returns all the object definitions where system = &#63; and status = &#63;.
	 *
	 * @param system the system
	 * @param status the status
	 * @return the matching object definitions
	 */
	public static List<ObjectDefinition> findByS_S(boolean system, int status) {
		return getPersistence().findByS_S(system, status);
	}

	/**
	 * Returns a range of all the object definitions where system = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param system the system
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions
	 */
	public static List<ObjectDefinition> findByS_S(
		boolean system, int status, int start, int end) {

		return getPersistence().findByS_S(system, status, start, end);
	}

	/**
	 * Returns an ordered range of all the object definitions where system = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param system the system
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions
	 */
	public static List<ObjectDefinition> findByS_S(
		boolean system, int status, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().findByS_S(
			system, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definitions where system = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param system the system
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	public static List<ObjectDefinition> findByS_S(
		boolean system, int status, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByS_S(
			system, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object definition in the ordered set where system = &#63; and status = &#63;.
	 *
	 * @param system the system
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findByS_S_First(
			boolean system, int status,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByS_S_First(
			system, status, orderByComparator);
	}

	/**
	 * Returns the first object definition in the ordered set where system = &#63; and status = &#63;.
	 *
	 * @param system the system
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByS_S_First(
		boolean system, int status,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().fetchByS_S_First(
			system, status, orderByComparator);
	}

	/**
	 * Returns the last object definition in the ordered set where system = &#63; and status = &#63;.
	 *
	 * @param system the system
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findByS_S_Last(
			boolean system, int status,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByS_S_Last(
			system, status, orderByComparator);
	}

	/**
	 * Returns the last object definition in the ordered set where system = &#63; and status = &#63;.
	 *
	 * @param system the system
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByS_S_Last(
		boolean system, int status,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().fetchByS_S_Last(
			system, status, orderByComparator);
	}

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set where system = &#63; and status = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param system the system
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition[] findByS_S_PrevAndNext(
			long objectDefinitionId, boolean system, int status,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByS_S_PrevAndNext(
			objectDefinitionId, system, status, orderByComparator);
	}

	/**
	 * Returns all the object definitions that the user has permission to view where system = &#63; and status = &#63;.
	 *
	 * @param system the system
	 * @param status the status
	 * @return the matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByS_S(
		boolean system, int status) {

		return getPersistence().filterFindByS_S(system, status);
	}

	/**
	 * Returns a range of all the object definitions that the user has permission to view where system = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param system the system
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByS_S(
		boolean system, int status, int start, int end) {

		return getPersistence().filterFindByS_S(system, status, start, end);
	}

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where system = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param system the system
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByS_S(
		boolean system, int status, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().filterFindByS_S(
			system, status, start, end, orderByComparator);
	}

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set of object definitions that the user has permission to view where system = &#63; and status = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param system the system
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition[] filterFindByS_S_PrevAndNext(
			long objectDefinitionId, boolean system, int status,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().filterFindByS_S_PrevAndNext(
			objectDefinitionId, system, status, orderByComparator);
	}

	/**
	 * Removes all the object definitions where system = &#63; and status = &#63; from the database.
	 *
	 * @param system the system
	 * @param status the status
	 */
	public static void removeByS_S(boolean system, int status) {
		getPersistence().removeByS_S(system, status);
	}

	/**
	 * Returns the number of object definitions where system = &#63; and status = &#63;.
	 *
	 * @param system the system
	 * @param status the status
	 * @return the number of matching object definitions
	 */
	public static int countByS_S(boolean system, int status) {
		return getPersistence().countByS_S(system, status);
	}

	/**
	 * Returns the number of object definitions that the user has permission to view where system = &#63; and status = &#63;.
	 *
	 * @param system the system
	 * @param status the status
	 * @return the number of matching object definitions that the user has permission to view
	 */
	public static int filterCountByS_S(boolean system, int status) {
		return getPersistence().filterCountByS_S(system, status);
	}

	/**
	 * Returns all the object definitions where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @return the matching object definitions
	 */
	public static List<ObjectDefinition> findByC_A_S(
		long companyId, boolean active, int status) {

		return getPersistence().findByC_A_S(companyId, active, status);
	}

	/**
	 * Returns a range of all the object definitions where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions
	 */
	public static List<ObjectDefinition> findByC_A_S(
		long companyId, boolean active, int status, int start, int end) {

		return getPersistence().findByC_A_S(
			companyId, active, status, start, end);
	}

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions
	 */
	public static List<ObjectDefinition> findByC_A_S(
		long companyId, boolean active, int status, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().findByC_A_S(
			companyId, active, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	public static List<ObjectDefinition> findByC_A_S(
		long companyId, boolean active, int status, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_A_S(
			companyId, active, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findByC_A_S_First(
			long companyId, boolean active, int status,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByC_A_S_First(
			companyId, active, status, orderByComparator);
	}

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByC_A_S_First(
		long companyId, boolean active, int status,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().fetchByC_A_S_First(
			companyId, active, status, orderByComparator);
	}

	/**
	 * Returns the last object definition in the ordered set where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findByC_A_S_Last(
			long companyId, boolean active, int status,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByC_A_S_Last(
			companyId, active, status, orderByComparator);
	}

	/**
	 * Returns the last object definition in the ordered set where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByC_A_S_Last(
		long companyId, boolean active, int status,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().fetchByC_A_S_Last(
			companyId, active, status, orderByComparator);
	}

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition[] findByC_A_S_PrevAndNext(
			long objectDefinitionId, long companyId, boolean active, int status,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByC_A_S_PrevAndNext(
			objectDefinitionId, companyId, active, status, orderByComparator);
	}

	/**
	 * Returns all the object definitions that the user has permission to view where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @return the matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByC_A_S(
		long companyId, boolean active, int status) {

		return getPersistence().filterFindByC_A_S(companyId, active, status);
	}

	/**
	 * Returns a range of all the object definitions that the user has permission to view where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByC_A_S(
		long companyId, boolean active, int status, int start, int end) {

		return getPersistence().filterFindByC_A_S(
			companyId, active, status, start, end);
	}

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByC_A_S(
		long companyId, boolean active, int status, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().filterFindByC_A_S(
			companyId, active, status, start, end, orderByComparator);
	}

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set of object definitions that the user has permission to view where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition[] filterFindByC_A_S_PrevAndNext(
			long objectDefinitionId, long companyId, boolean active, int status,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().filterFindByC_A_S_PrevAndNext(
			objectDefinitionId, companyId, active, status, orderByComparator);
	}

	/**
	 * Removes all the object definitions where companyId = &#63; and active = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 */
	public static void removeByC_A_S(
		long companyId, boolean active, int status) {

		getPersistence().removeByC_A_S(companyId, active, status);
	}

	/**
	 * Returns the number of object definitions where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @return the number of matching object definitions
	 */
	public static int countByC_A_S(long companyId, boolean active, int status) {
		return getPersistence().countByC_A_S(companyId, active, status);
	}

	/**
	 * Returns the number of object definitions that the user has permission to view where companyId = &#63; and active = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param status the status
	 * @return the number of matching object definitions that the user has permission to view
	 */
	public static int filterCountByC_A_S(
		long companyId, boolean active, int status) {

		return getPersistence().filterCountByC_A_S(companyId, active, status);
	}

	/**
	 * Returns all the object definitions where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @return the matching object definitions
	 */
	public static List<ObjectDefinition> findByC_M_S(
		long companyId, boolean modifiable, boolean system) {

		return getPersistence().findByC_M_S(companyId, modifiable, system);
	}

	/**
	 * Returns a range of all the object definitions where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions
	 */
	public static List<ObjectDefinition> findByC_M_S(
		long companyId, boolean modifiable, boolean system, int start,
		int end) {

		return getPersistence().findByC_M_S(
			companyId, modifiable, system, start, end);
	}

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions
	 */
	public static List<ObjectDefinition> findByC_M_S(
		long companyId, boolean modifiable, boolean system, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().findByC_M_S(
			companyId, modifiable, system, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	public static List<ObjectDefinition> findByC_M_S(
		long companyId, boolean modifiable, boolean system, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_M_S(
			companyId, modifiable, system, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findByC_M_S_First(
			long companyId, boolean modifiable, boolean system,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByC_M_S_First(
			companyId, modifiable, system, orderByComparator);
	}

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByC_M_S_First(
		long companyId, boolean modifiable, boolean system,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().fetchByC_M_S_First(
			companyId, modifiable, system, orderByComparator);
	}

	/**
	 * Returns the last object definition in the ordered set where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findByC_M_S_Last(
			long companyId, boolean modifiable, boolean system,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByC_M_S_Last(
			companyId, modifiable, system, orderByComparator);
	}

	/**
	 * Returns the last object definition in the ordered set where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByC_M_S_Last(
		long companyId, boolean modifiable, boolean system,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().fetchByC_M_S_Last(
			companyId, modifiable, system, orderByComparator);
	}

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition[] findByC_M_S_PrevAndNext(
			long objectDefinitionId, long companyId, boolean modifiable,
			boolean system,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByC_M_S_PrevAndNext(
			objectDefinitionId, companyId, modifiable, system,
			orderByComparator);
	}

	/**
	 * Returns all the object definitions that the user has permission to view where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @return the matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByC_M_S(
		long companyId, boolean modifiable, boolean system) {

		return getPersistence().filterFindByC_M_S(
			companyId, modifiable, system);
	}

	/**
	 * Returns a range of all the object definitions that the user has permission to view where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByC_M_S(
		long companyId, boolean modifiable, boolean system, int start,
		int end) {

		return getPersistence().filterFindByC_M_S(
			companyId, modifiable, system, start, end);
	}

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByC_M_S(
		long companyId, boolean modifiable, boolean system, int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().filterFindByC_M_S(
			companyId, modifiable, system, start, end, orderByComparator);
	}

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set of object definitions that the user has permission to view where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition[] filterFindByC_M_S_PrevAndNext(
			long objectDefinitionId, long companyId, boolean modifiable,
			boolean system,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().filterFindByC_M_S_PrevAndNext(
			objectDefinitionId, companyId, modifiable, system,
			orderByComparator);
	}

	/**
	 * Removes all the object definitions where companyId = &#63; and modifiable = &#63; and system = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 */
	public static void removeByC_M_S(
		long companyId, boolean modifiable, boolean system) {

		getPersistence().removeByC_M_S(companyId, modifiable, system);
	}

	/**
	 * Returns the number of object definitions where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @return the number of matching object definitions
	 */
	public static int countByC_M_S(
		long companyId, boolean modifiable, boolean system) {

		return getPersistence().countByC_M_S(companyId, modifiable, system);
	}

	/**
	 * Returns the number of object definitions that the user has permission to view where companyId = &#63; and modifiable = &#63; and system = &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiable the modifiable
	 * @param system the system
	 * @return the number of matching object definitions that the user has permission to view
	 */
	public static int filterCountByC_M_S(
		long companyId, boolean modifiable, boolean system) {

		return getPersistence().filterCountByC_M_S(
			companyId, modifiable, system);
	}

	/**
	 * Returns all the object definitions where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @return the matching object definitions
	 */
	public static List<ObjectDefinition> findByC_A_S_S(
		long companyId, boolean active, boolean system, int status) {

		return getPersistence().findByC_A_S_S(
			companyId, active, system, status);
	}

	/**
	 * Returns a range of all the object definitions where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions
	 */
	public static List<ObjectDefinition> findByC_A_S_S(
		long companyId, boolean active, boolean system, int status, int start,
		int end) {

		return getPersistence().findByC_A_S_S(
			companyId, active, system, status, start, end);
	}

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions
	 */
	public static List<ObjectDefinition> findByC_A_S_S(
		long companyId, boolean active, boolean system, int status, int start,
		int end, OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().findByC_A_S_S(
			companyId, active, system, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	public static List<ObjectDefinition> findByC_A_S_S(
		long companyId, boolean active, boolean system, int status, int start,
		int end, OrderByComparator<ObjectDefinition> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_A_S_S(
			companyId, active, system, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findByC_A_S_S_First(
			long companyId, boolean active, boolean system, int status,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByC_A_S_S_First(
			companyId, active, system, status, orderByComparator);
	}

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByC_A_S_S_First(
		long companyId, boolean active, boolean system, int status,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().fetchByC_A_S_S_First(
			companyId, active, system, status, orderByComparator);
	}

	/**
	 * Returns the last object definition in the ordered set where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findByC_A_S_S_Last(
			long companyId, boolean active, boolean system, int status,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByC_A_S_S_Last(
			companyId, active, system, status, orderByComparator);
	}

	/**
	 * Returns the last object definition in the ordered set where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByC_A_S_S_Last(
		long companyId, boolean active, boolean system, int status,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().fetchByC_A_S_S_Last(
			companyId, active, system, status, orderByComparator);
	}

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition[] findByC_A_S_S_PrevAndNext(
			long objectDefinitionId, long companyId, boolean active,
			boolean system, int status,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByC_A_S_S_PrevAndNext(
			objectDefinitionId, companyId, active, system, status,
			orderByComparator);
	}

	/**
	 * Returns all the object definitions that the user has permission to view where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @return the matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByC_A_S_S(
		long companyId, boolean active, boolean system, int status) {

		return getPersistence().filterFindByC_A_S_S(
			companyId, active, system, status);
	}

	/**
	 * Returns a range of all the object definitions that the user has permission to view where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByC_A_S_S(
		long companyId, boolean active, boolean system, int status, int start,
		int end) {

		return getPersistence().filterFindByC_A_S_S(
			companyId, active, system, status, start, end);
	}

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByC_A_S_S(
		long companyId, boolean active, boolean system, int status, int start,
		int end, OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().filterFindByC_A_S_S(
			companyId, active, system, status, start, end, orderByComparator);
	}

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set of object definitions that the user has permission to view where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition[] filterFindByC_A_S_S_PrevAndNext(
			long objectDefinitionId, long companyId, boolean active,
			boolean system, int status,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().filterFindByC_A_S_S_PrevAndNext(
			objectDefinitionId, companyId, active, system, status,
			orderByComparator);
	}

	/**
	 * Removes all the object definitions where companyId = &#63; and active = &#63; and system = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 */
	public static void removeByC_A_S_S(
		long companyId, boolean active, boolean system, int status) {

		getPersistence().removeByC_A_S_S(companyId, active, system, status);
	}

	/**
	 * Returns the number of object definitions where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @return the number of matching object definitions
	 */
	public static int countByC_A_S_S(
		long companyId, boolean active, boolean system, int status) {

		return getPersistence().countByC_A_S_S(
			companyId, active, system, status);
	}

	/**
	 * Returns the number of object definitions that the user has permission to view where companyId = &#63; and active = &#63; and system = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param system the system
	 * @param status the status
	 * @return the number of matching object definitions that the user has permission to view
	 */
	public static int filterCountByC_A_S_S(
		long companyId, boolean active, boolean system, int status) {

		return getPersistence().filterCountByC_A_S_S(
			companyId, active, system, status);
	}

	/**
	 * Returns all the object definitions where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @return the matching object definitions
	 */
	public static List<ObjectDefinition> findByC_OFI_A_E_S_S(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status) {

		return getPersistence().findByC_OFI_A_E_S_S(
			companyId, objectFolderId, active, enableObjectEntryDraft, scope,
			status);
	}

	/**
	 * Returns a range of all the object definitions where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions
	 */
	public static List<ObjectDefinition> findByC_OFI_A_E_S_S(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status, int start,
		int end) {

		return getPersistence().findByC_OFI_A_E_S_S(
			companyId, objectFolderId, active, enableObjectEntryDraft, scope,
			status, start, end);
	}

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions
	 */
	public static List<ObjectDefinition> findByC_OFI_A_E_S_S(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status, int start,
		int end, OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().findByC_OFI_A_E_S_S(
			companyId, objectFolderId, active, enableObjectEntryDraft, scope,
			status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	public static List<ObjectDefinition> findByC_OFI_A_E_S_S(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status, int start,
		int end, OrderByComparator<ObjectDefinition> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_OFI_A_E_S_S(
			companyId, objectFolderId, active, enableObjectEntryDraft, scope,
			status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findByC_OFI_A_E_S_S_First(
			long companyId, long objectFolderId, boolean active,
			boolean enableObjectEntryDraft, String scope, int status,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByC_OFI_A_E_S_S_First(
			companyId, objectFolderId, active, enableObjectEntryDraft, scope,
			status, orderByComparator);
	}

	/**
	 * Returns the first object definition in the ordered set where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByC_OFI_A_E_S_S_First(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().fetchByC_OFI_A_E_S_S_First(
			companyId, objectFolderId, active, enableObjectEntryDraft, scope,
			status, orderByComparator);
	}

	/**
	 * Returns the last object definition in the ordered set where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findByC_OFI_A_E_S_S_Last(
			long companyId, long objectFolderId, boolean active,
			boolean enableObjectEntryDraft, String scope, int status,
			OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByC_OFI_A_E_S_S_Last(
			companyId, objectFolderId, active, enableObjectEntryDraft, scope,
			status, orderByComparator);
	}

	/**
	 * Returns the last object definition in the ordered set where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByC_OFI_A_E_S_S_Last(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().fetchByC_OFI_A_E_S_S_Last(
			companyId, objectFolderId, active, enableObjectEntryDraft, scope,
			status, orderByComparator);
	}

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition[] findByC_OFI_A_E_S_S_PrevAndNext(
			long objectDefinitionId, long companyId, long objectFolderId,
			boolean active, boolean enableObjectEntryDraft, String scope,
			int status, OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByC_OFI_A_E_S_S_PrevAndNext(
			objectDefinitionId, companyId, objectFolderId, active,
			enableObjectEntryDraft, scope, status, orderByComparator);
	}

	/**
	 * Returns all the object definitions that the user has permission to view where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @return the matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByC_OFI_A_E_S_S(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status) {

		return getPersistence().filterFindByC_OFI_A_E_S_S(
			companyId, objectFolderId, active, enableObjectEntryDraft, scope,
			status);
	}

	/**
	 * Returns a range of all the object definitions that the user has permission to view where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByC_OFI_A_E_S_S(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status, int start,
		int end) {

		return getPersistence().filterFindByC_OFI_A_E_S_S(
			companyId, objectFolderId, active, enableObjectEntryDraft, scope,
			status, start, end);
	}

	/**
	 * Returns an ordered range of all the object definitions that the user has permissions to view where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByC_OFI_A_E_S_S(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status, int start,
		int end, OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().filterFindByC_OFI_A_E_S_S(
			companyId, objectFolderId, active, enableObjectEntryDraft, scope,
			status, start, end, orderByComparator);
	}

	/**
	 * Returns the object definitions before and after the current object definition in the ordered set of object definitions that the user has permission to view where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param objectDefinitionId the primary key of the current object definition
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition[] filterFindByC_OFI_A_E_S_S_PrevAndNext(
			long objectDefinitionId, long companyId, long objectFolderId,
			boolean active, boolean enableObjectEntryDraft, String scope,
			int status, OrderByComparator<ObjectDefinition> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().filterFindByC_OFI_A_E_S_S_PrevAndNext(
			objectDefinitionId, companyId, objectFolderId, active,
			enableObjectEntryDraft, scope, status, orderByComparator);
	}

	/**
	 * Returns all the object definitions that the user has permission to view where companyId = &#63; and objectFolderId = any &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param objectFolderIds the object folder IDs
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @return the matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByC_OFI_A_E_S_S(
		long companyId, long[] objectFolderIds, boolean active,
		boolean enableObjectEntryDraft, String scope, int status) {

		return getPersistence().filterFindByC_OFI_A_E_S_S(
			companyId, objectFolderIds, active, enableObjectEntryDraft, scope,
			status);
	}

	/**
	 * Returns a range of all the object definitions that the user has permission to view where companyId = &#63; and objectFolderId = any &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param objectFolderIds the object folder IDs
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByC_OFI_A_E_S_S(
		long companyId, long[] objectFolderIds, boolean active,
		boolean enableObjectEntryDraft, String scope, int status, int start,
		int end) {

		return getPersistence().filterFindByC_OFI_A_E_S_S(
			companyId, objectFolderIds, active, enableObjectEntryDraft, scope,
			status, start, end);
	}

	/**
	 * Returns an ordered range of all the object definitions that the user has permission to view where companyId = &#63; and objectFolderId = any &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param objectFolderIds the object folder IDs
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions that the user has permission to view
	 */
	public static List<ObjectDefinition> filterFindByC_OFI_A_E_S_S(
		long companyId, long[] objectFolderIds, boolean active,
		boolean enableObjectEntryDraft, String scope, int status, int start,
		int end, OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().filterFindByC_OFI_A_E_S_S(
			companyId, objectFolderIds, active, enableObjectEntryDraft, scope,
			status, start, end, orderByComparator);
	}

	/**
	 * Returns all the object definitions where companyId = &#63; and objectFolderId = any &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param objectFolderIds the object folder IDs
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @return the matching object definitions
	 */
	public static List<ObjectDefinition> findByC_OFI_A_E_S_S(
		long companyId, long[] objectFolderIds, boolean active,
		boolean enableObjectEntryDraft, String scope, int status) {

		return getPersistence().findByC_OFI_A_E_S_S(
			companyId, objectFolderIds, active, enableObjectEntryDraft, scope,
			status);
	}

	/**
	 * Returns a range of all the object definitions where companyId = &#63; and objectFolderId = any &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param objectFolderIds the object folder IDs
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of matching object definitions
	 */
	public static List<ObjectDefinition> findByC_OFI_A_E_S_S(
		long companyId, long[] objectFolderIds, boolean active,
		boolean enableObjectEntryDraft, String scope, int status, int start,
		int end) {

		return getPersistence().findByC_OFI_A_E_S_S(
			companyId, objectFolderIds, active, enableObjectEntryDraft, scope,
			status, start, end);
	}

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and objectFolderId = any &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param objectFolderIds the object folder IDs
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definitions
	 */
	public static List<ObjectDefinition> findByC_OFI_A_E_S_S(
		long companyId, long[] objectFolderIds, boolean active,
		boolean enableObjectEntryDraft, String scope, int status, int start,
		int end, OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().findByC_OFI_A_E_S_S(
			companyId, objectFolderIds, active, enableObjectEntryDraft, scope,
			status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definitions where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param objectFolderIds the object folder IDs
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definitions
	 */
	public static List<ObjectDefinition> findByC_OFI_A_E_S_S(
		long companyId, long[] objectFolderIds, boolean active,
		boolean enableObjectEntryDraft, String scope, int status, int start,
		int end, OrderByComparator<ObjectDefinition> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_OFI_A_E_S_S(
			companyId, objectFolderIds, active, enableObjectEntryDraft, scope,
			status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the object definitions where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 */
	public static void removeByC_OFI_A_E_S_S(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status) {

		getPersistence().removeByC_OFI_A_E_S_S(
			companyId, objectFolderId, active, enableObjectEntryDraft, scope,
			status);
	}

	/**
	 * Returns the number of object definitions where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @return the number of matching object definitions
	 */
	public static int countByC_OFI_A_E_S_S(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status) {

		return getPersistence().countByC_OFI_A_E_S_S(
			companyId, objectFolderId, active, enableObjectEntryDraft, scope,
			status);
	}

	/**
	 * Returns the number of object definitions where companyId = &#63; and objectFolderId = any &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param objectFolderIds the object folder IDs
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @return the number of matching object definitions
	 */
	public static int countByC_OFI_A_E_S_S(
		long companyId, long[] objectFolderIds, boolean active,
		boolean enableObjectEntryDraft, String scope, int status) {

		return getPersistence().countByC_OFI_A_E_S_S(
			companyId, objectFolderIds, active, enableObjectEntryDraft, scope,
			status);
	}

	/**
	 * Returns the number of object definitions that the user has permission to view where companyId = &#63; and objectFolderId = &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param objectFolderId the object folder ID
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @return the number of matching object definitions that the user has permission to view
	 */
	public static int filterCountByC_OFI_A_E_S_S(
		long companyId, long objectFolderId, boolean active,
		boolean enableObjectEntryDraft, String scope, int status) {

		return getPersistence().filterCountByC_OFI_A_E_S_S(
			companyId, objectFolderId, active, enableObjectEntryDraft, scope,
			status);
	}

	/**
	 * Returns the number of object definitions that the user has permission to view where companyId = &#63; and objectFolderId = any &#63; and active = &#63; and enableObjectEntryDraft = &#63; and scope = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param objectFolderIds the object folder IDs
	 * @param active the active
	 * @param enableObjectEntryDraft the enable object entry draft
	 * @param scope the scope
	 * @param status the status
	 * @return the number of matching object definitions that the user has permission to view
	 */
	public static int filterCountByC_OFI_A_E_S_S(
		long companyId, long[] objectFolderIds, boolean active,
		boolean enableObjectEntryDraft, String scope, int status) {

		return getPersistence().filterCountByC_OFI_A_E_S_S(
			companyId, objectFolderIds, active, enableObjectEntryDraft, scope,
			status);
	}

	/**
	 * Returns the object definition where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchObjectDefinitionException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching object definition
	 * @throws NoSuchObjectDefinitionException if a matching object definition could not be found
	 */
	public static ObjectDefinition findByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the object definition where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().fetchByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the object definition where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return getPersistence().fetchByERC_C(
			externalReferenceCode, companyId, useFinderCache);
	}

	/**
	 * Removes the object definition where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the object definition that was removed
	 */
	public static ObjectDefinition removeByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().removeByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the number of object definitions where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching object definitions
	 */
	public static int countByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().countByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Caches the object definition in the entity cache if it is enabled.
	 *
	 * @param objectDefinition the object definition
	 */
	public static void cacheResult(ObjectDefinition objectDefinition) {
		getPersistence().cacheResult(objectDefinition);
	}

	/**
	 * Caches the object definitions in the entity cache if it is enabled.
	 *
	 * @param objectDefinitions the object definitions
	 */
	public static void cacheResult(List<ObjectDefinition> objectDefinitions) {
		getPersistence().cacheResult(objectDefinitions);
	}

	/**
	 * Creates a new object definition with the primary key. Does not add the object definition to the database.
	 *
	 * @param objectDefinitionId the primary key for the new object definition
	 * @return the new object definition
	 */
	public static ObjectDefinition create(long objectDefinitionId) {
		return getPersistence().create(objectDefinitionId);
	}

	/**
	 * Removes the object definition with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectDefinitionId the primary key of the object definition
	 * @return the object definition that was removed
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition remove(long objectDefinitionId)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().remove(objectDefinitionId);
	}

	public static ObjectDefinition updateImpl(
		ObjectDefinition objectDefinition) {

		return getPersistence().updateImpl(objectDefinition);
	}

	/**
	 * Returns the object definition with the primary key or throws a <code>NoSuchObjectDefinitionException</code> if it could not be found.
	 *
	 * @param objectDefinitionId the primary key of the object definition
	 * @return the object definition
	 * @throws NoSuchObjectDefinitionException if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition findByPrimaryKey(long objectDefinitionId)
		throws com.liferay.object.exception.NoSuchObjectDefinitionException {

		return getPersistence().findByPrimaryKey(objectDefinitionId);
	}

	/**
	 * Returns the object definition with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectDefinitionId the primary key of the object definition
	 * @return the object definition, or <code>null</code> if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition fetchByPrimaryKey(long objectDefinitionId) {
		return getPersistence().fetchByPrimaryKey(objectDefinitionId);
	}

	/**
	 * Returns all the object definitions.
	 *
	 * @return the object definitions
	 */
	public static List<ObjectDefinition> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the object definitions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of object definitions
	 */
	public static List<ObjectDefinition> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the object definitions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of object definitions
	 */
	public static List<ObjectDefinition> findAll(
		int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definitions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of object definitions
	 */
	public static List<ObjectDefinition> findAll(
		int start, int end,
		OrderByComparator<ObjectDefinition> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the object definitions from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of object definitions.
	 *
	 * @return the number of object definitions
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static ObjectDefinitionPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(ObjectDefinitionPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile ObjectDefinitionPersistence _persistence;

}
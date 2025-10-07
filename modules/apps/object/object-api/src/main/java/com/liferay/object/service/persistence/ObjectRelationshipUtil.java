/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence;

import com.liferay.object.model.ObjectRelationship;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the object relationship service. This utility wraps <code>com.liferay.object.service.persistence.impl.ObjectRelationshipPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see ObjectRelationshipPersistence
 * @generated
 */
public class ObjectRelationshipUtil {

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
	public static void clearCache(ObjectRelationship objectRelationship) {
		getPersistence().clearCache(objectRelationship);
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
	public static Map<Serializable, ObjectRelationship> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<ObjectRelationship> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<ObjectRelationship> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<ObjectRelationship> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static ObjectRelationship update(
		ObjectRelationship objectRelationship) {

		return getPersistence().update(objectRelationship);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static ObjectRelationship update(
		ObjectRelationship objectRelationship, ServiceContext serviceContext) {

		return getPersistence().update(objectRelationship, serviceContext);
	}

	/**
	 * Returns all the object relationships where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching object relationships
	 */
	public static List<ObjectRelationship> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the object relationships where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public static List<ObjectRelationship> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the object relationships where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object relationships where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByUuid_First(
			String uuid,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByUuid_First(
		String uuid, OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByUuid_Last(
			String uuid,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByUuid_Last(
		String uuid, OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where uuid = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public static ObjectRelationship[] findByUuid_PrevAndNext(
			long objectRelationshipId, String uuid,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByUuid_PrevAndNext(
			objectRelationshipId, uuid, orderByComparator);
	}

	/**
	 * Removes all the object relationships where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of object relationships where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object relationships
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns all the object relationships where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching object relationships
	 */
	public static List<ObjectRelationship> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the object relationships where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public static List<ObjectRelationship> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the object relationships where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object relationships where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public static ObjectRelationship[] findByUuid_C_PrevAndNext(
			long objectRelationshipId, String uuid, long companyId,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByUuid_C_PrevAndNext(
			objectRelationshipId, uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the object relationships where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of object relationships where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object relationships
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns all the object relationships where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching object relationships
	 */
	public static List<ObjectRelationship> findByCompanyId(long companyId) {
		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the object relationships where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public static List<ObjectRelationship> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the object relationships where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object relationships where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByCompanyId_First(
			long companyId,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByCompanyId_First(
		long companyId,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByCompanyId_Last(
			long companyId,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByCompanyId_Last(
		long companyId,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where companyId = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public static ObjectRelationship[] findByCompanyId_PrevAndNext(
			long objectRelationshipId, long companyId,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByCompanyId_PrevAndNext(
			objectRelationshipId, companyId, orderByComparator);
	}

	/**
	 * Removes all the object relationships where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of object relationships where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching object relationships
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns all the object relationships where objectDefinitionId1 = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @return the matching object relationships
	 */
	public static List<ObjectRelationship> findByObjectDefinitionId1(
		long objectDefinitionId1) {

		return getPersistence().findByObjectDefinitionId1(objectDefinitionId1);
	}

	/**
	 * Returns a range of all the object relationships where objectDefinitionId1 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public static List<ObjectRelationship> findByObjectDefinitionId1(
		long objectDefinitionId1, int start, int end) {

		return getPersistence().findByObjectDefinitionId1(
			objectDefinitionId1, start, end);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByObjectDefinitionId1(
		long objectDefinitionId1, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().findByObjectDefinitionId1(
			objectDefinitionId1, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByObjectDefinitionId1(
		long objectDefinitionId1, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByObjectDefinitionId1(
			objectDefinitionId1, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByObjectDefinitionId1_First(
			long objectDefinitionId1,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByObjectDefinitionId1_First(
			objectDefinitionId1, orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByObjectDefinitionId1_First(
		long objectDefinitionId1,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByObjectDefinitionId1_First(
			objectDefinitionId1, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByObjectDefinitionId1_Last(
			long objectDefinitionId1,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByObjectDefinitionId1_Last(
			objectDefinitionId1, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByObjectDefinitionId1_Last(
		long objectDefinitionId1,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByObjectDefinitionId1_Last(
			objectDefinitionId1, orderByComparator);
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId1 = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId1 the object definition id1
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public static ObjectRelationship[] findByObjectDefinitionId1_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId1,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByObjectDefinitionId1_PrevAndNext(
			objectRelationshipId, objectDefinitionId1, orderByComparator);
	}

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 */
	public static void removeByObjectDefinitionId1(long objectDefinitionId1) {
		getPersistence().removeByObjectDefinitionId1(objectDefinitionId1);
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @return the number of matching object relationships
	 */
	public static int countByObjectDefinitionId1(long objectDefinitionId1) {
		return getPersistence().countByObjectDefinitionId1(objectDefinitionId1);
	}

	/**
	 * Returns all the object relationships where objectDefinitionId2 = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @return the matching object relationships
	 */
	public static List<ObjectRelationship> findByObjectDefinitionId2(
		long objectDefinitionId2) {

		return getPersistence().findByObjectDefinitionId2(objectDefinitionId2);
	}

	/**
	 * Returns a range of all the object relationships where objectDefinitionId2 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public static List<ObjectRelationship> findByObjectDefinitionId2(
		long objectDefinitionId2, int start, int end) {

		return getPersistence().findByObjectDefinitionId2(
			objectDefinitionId2, start, end);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId2 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByObjectDefinitionId2(
		long objectDefinitionId2, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().findByObjectDefinitionId2(
			objectDefinitionId2, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId2 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByObjectDefinitionId2(
		long objectDefinitionId2, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByObjectDefinitionId2(
			objectDefinitionId2, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByObjectDefinitionId2_First(
			long objectDefinitionId2,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByObjectDefinitionId2_First(
			objectDefinitionId2, orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByObjectDefinitionId2_First(
		long objectDefinitionId2,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByObjectDefinitionId2_First(
			objectDefinitionId2, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId2 = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByObjectDefinitionId2_Last(
			long objectDefinitionId2,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByObjectDefinitionId2_Last(
			objectDefinitionId2, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId2 = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByObjectDefinitionId2_Last(
		long objectDefinitionId2,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByObjectDefinitionId2_Last(
			objectDefinitionId2, orderByComparator);
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId2 = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId2 the object definition id2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public static ObjectRelationship[] findByObjectDefinitionId2_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId2,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByObjectDefinitionId2_PrevAndNext(
			objectRelationshipId, objectDefinitionId2, orderByComparator);
	}

	/**
	 * Removes all the object relationships where objectDefinitionId2 = &#63; from the database.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 */
	public static void removeByObjectDefinitionId2(long objectDefinitionId2) {
		getPersistence().removeByObjectDefinitionId2(objectDefinitionId2);
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId2 = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @return the number of matching object relationships
	 */
	public static int countByObjectDefinitionId2(long objectDefinitionId2) {
		return getPersistence().countByObjectDefinitionId2(objectDefinitionId2);
	}

	/**
	 * Returns the object relationship where objectFieldId2 = &#63; or throws a <code>NoSuchObjectRelationshipException</code> if it could not be found.
	 *
	 * @param objectFieldId2 the object field id2
	 * @return the matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByObjectFieldId2(long objectFieldId2)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByObjectFieldId2(objectFieldId2);
	}

	/**
	 * Returns the object relationship where objectFieldId2 = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param objectFieldId2 the object field id2
	 * @return the matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByObjectFieldId2(
		long objectFieldId2) {

		return getPersistence().fetchByObjectFieldId2(objectFieldId2);
	}

	/**
	 * Returns the object relationship where objectFieldId2 = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectFieldId2 the object field id2
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByObjectFieldId2(
		long objectFieldId2, boolean useFinderCache) {

		return getPersistence().fetchByObjectFieldId2(
			objectFieldId2, useFinderCache);
	}

	/**
	 * Removes the object relationship where objectFieldId2 = &#63; from the database.
	 *
	 * @param objectFieldId2 the object field id2
	 * @return the object relationship that was removed
	 */
	public static ObjectRelationship removeByObjectFieldId2(long objectFieldId2)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().removeByObjectFieldId2(objectFieldId2);
	}

	/**
	 * Returns the number of object relationships where objectFieldId2 = &#63;.
	 *
	 * @param objectFieldId2 the object field id2
	 * @return the number of matching object relationships
	 */
	public static int countByObjectFieldId2(long objectFieldId2) {
		return getPersistence().countByObjectFieldId2(objectFieldId2);
	}

	/**
	 * Returns all the object relationships where parameterObjectFieldId = &#63;.
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @return the matching object relationships
	 */
	public static List<ObjectRelationship> findByParameterObjectFieldId(
		long parameterObjectFieldId) {

		return getPersistence().findByParameterObjectFieldId(
			parameterObjectFieldId);
	}

	/**
	 * Returns a range of all the object relationships where parameterObjectFieldId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public static List<ObjectRelationship> findByParameterObjectFieldId(
		long parameterObjectFieldId, int start, int end) {

		return getPersistence().findByParameterObjectFieldId(
			parameterObjectFieldId, start, end);
	}

	/**
	 * Returns an ordered range of all the object relationships where parameterObjectFieldId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByParameterObjectFieldId(
		long parameterObjectFieldId, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().findByParameterObjectFieldId(
			parameterObjectFieldId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object relationships where parameterObjectFieldId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByParameterObjectFieldId(
		long parameterObjectFieldId, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByParameterObjectFieldId(
			parameterObjectFieldId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where parameterObjectFieldId = &#63;.
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByParameterObjectFieldId_First(
			long parameterObjectFieldId,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByParameterObjectFieldId_First(
			parameterObjectFieldId, orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where parameterObjectFieldId = &#63;.
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByParameterObjectFieldId_First(
		long parameterObjectFieldId,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByParameterObjectFieldId_First(
			parameterObjectFieldId, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where parameterObjectFieldId = &#63;.
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByParameterObjectFieldId_Last(
			long parameterObjectFieldId,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByParameterObjectFieldId_Last(
			parameterObjectFieldId, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where parameterObjectFieldId = &#63;.
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByParameterObjectFieldId_Last(
		long parameterObjectFieldId,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByParameterObjectFieldId_Last(
			parameterObjectFieldId, orderByComparator);
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where parameterObjectFieldId = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param parameterObjectFieldId the parameter object field ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public static ObjectRelationship[] findByParameterObjectFieldId_PrevAndNext(
			long objectRelationshipId, long parameterObjectFieldId,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByParameterObjectFieldId_PrevAndNext(
			objectRelationshipId, parameterObjectFieldId, orderByComparator);
	}

	/**
	 * Removes all the object relationships where parameterObjectFieldId = &#63; from the database.
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 */
	public static void removeByParameterObjectFieldId(
		long parameterObjectFieldId) {

		getPersistence().removeByParameterObjectFieldId(parameterObjectFieldId);
	}

	/**
	 * Returns the number of object relationships where parameterObjectFieldId = &#63;.
	 *
	 * @param parameterObjectFieldId the parameter object field ID
	 * @return the number of matching object relationships
	 */
	public static int countByParameterObjectFieldId(
		long parameterObjectFieldId) {

		return getPersistence().countByParameterObjectFieldId(
			parameterObjectFieldId);
	}

	/**
	 * Returns all the object relationships where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching object relationships
	 */
	public static List<ObjectRelationship> findByC_U(
		long companyId, long userId) {

		return getPersistence().findByC_U(companyId, userId);
	}

	/**
	 * Returns a range of all the object relationships where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public static List<ObjectRelationship> findByC_U(
		long companyId, long userId, int start, int end) {

		return getPersistence().findByC_U(companyId, userId, start, end);
	}

	/**
	 * Returns an ordered range of all the object relationships where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().findByC_U(
			companyId, userId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object relationships where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_U(
			companyId, userId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByC_U_First(
			long companyId, long userId,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByC_U_First(
			companyId, userId, orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByC_U_First(
		long companyId, long userId,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByC_U_First(
			companyId, userId, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByC_U_Last(
			long companyId, long userId,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByC_U_Last(
			companyId, userId, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByC_U_Last(
		long companyId, long userId,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByC_U_Last(
			companyId, userId, orderByComparator);
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public static ObjectRelationship[] findByC_U_PrevAndNext(
			long objectRelationshipId, long companyId, long userId,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByC_U_PrevAndNext(
			objectRelationshipId, companyId, userId, orderByComparator);
	}

	/**
	 * Removes all the object relationships where companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 */
	public static void removeByC_U(long companyId, long userId) {
		getPersistence().removeByC_U(companyId, userId);
	}

	/**
	 * Returns the number of object relationships where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching object relationships
	 */
	public static int countByC_U(long companyId, long userId) {
		return getPersistence().countByC_U(companyId, userId);
	}

	/**
	 * Returns all the object relationships where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @return the matching object relationships
	 */
	public static List<ObjectRelationship> findByODI1_E(
		long objectDefinitionId1, boolean edge) {

		return getPersistence().findByODI1_E(objectDefinitionId1, edge);
	}

	/**
	 * Returns a range of all the object relationships where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI1_E(
		long objectDefinitionId1, boolean edge, int start, int end) {

		return getPersistence().findByODI1_E(
			objectDefinitionId1, edge, start, end);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI1_E(
		long objectDefinitionId1, boolean edge, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().findByODI1_E(
			objectDefinitionId1, edge, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI1_E(
		long objectDefinitionId1, boolean edge, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByODI1_E(
			objectDefinitionId1, edge, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByODI1_E_First(
			long objectDefinitionId1, boolean edge,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI1_E_First(
			objectDefinitionId1, edge, orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByODI1_E_First(
		long objectDefinitionId1, boolean edge,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByODI1_E_First(
			objectDefinitionId1, edge, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByODI1_E_Last(
			long objectDefinitionId1, boolean edge,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI1_E_Last(
			objectDefinitionId1, edge, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByODI1_E_Last(
		long objectDefinitionId1, boolean edge,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByODI1_E_Last(
			objectDefinitionId1, edge, orderByComparator);
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public static ObjectRelationship[] findByODI1_E_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId1, boolean edge,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI1_E_PrevAndNext(
			objectRelationshipId, objectDefinitionId1, edge, orderByComparator);
	}

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; and edge = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 */
	public static void removeByODI1_E(long objectDefinitionId1, boolean edge) {
		getPersistence().removeByODI1_E(objectDefinitionId1, edge);
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param edge the edge
	 * @return the number of matching object relationships
	 */
	public static int countByODI1_E(long objectDefinitionId1, boolean edge) {
		return getPersistence().countByODI1_E(objectDefinitionId1, edge);
	}

	/**
	 * Returns all the object relationships where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @return the matching object relationships
	 */
	public static List<ObjectRelationship> findByODI1_N(
		long objectDefinitionId1, String name) {

		return getPersistence().findByODI1_N(objectDefinitionId1, name);
	}

	/**
	 * Returns a range of all the object relationships where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI1_N(
		long objectDefinitionId1, String name, int start, int end) {

		return getPersistence().findByODI1_N(
			objectDefinitionId1, name, start, end);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI1_N(
		long objectDefinitionId1, String name, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().findByODI1_N(
			objectDefinitionId1, name, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI1_N(
		long objectDefinitionId1, String name, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByODI1_N(
			objectDefinitionId1, name, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByODI1_N_First(
			long objectDefinitionId1, String name,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI1_N_First(
			objectDefinitionId1, name, orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByODI1_N_First(
		long objectDefinitionId1, String name,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByODI1_N_First(
			objectDefinitionId1, name, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByODI1_N_Last(
			long objectDefinitionId1, String name,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI1_N_Last(
			objectDefinitionId1, name, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByODI1_N_Last(
		long objectDefinitionId1, String name,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByODI1_N_Last(
			objectDefinitionId1, name, orderByComparator);
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public static ObjectRelationship[] findByODI1_N_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId1, String name,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI1_N_PrevAndNext(
			objectRelationshipId, objectDefinitionId1, name, orderByComparator);
	}

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; and name = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 */
	public static void removeByODI1_N(long objectDefinitionId1, String name) {
		getPersistence().removeByODI1_N(objectDefinitionId1, name);
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param name the name
	 * @return the number of matching object relationships
	 */
	public static int countByODI1_N(long objectDefinitionId1, String name) {
		return getPersistence().countByODI1_N(objectDefinitionId1, name);
	}

	/**
	 * Returns all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @return the matching object relationships
	 */
	public static List<ObjectRelationship> findByODI1_R(
		long objectDefinitionId1, boolean reverse) {

		return getPersistence().findByODI1_R(objectDefinitionId1, reverse);
	}

	/**
	 * Returns a range of all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI1_R(
		long objectDefinitionId1, boolean reverse, int start, int end) {

		return getPersistence().findByODI1_R(
			objectDefinitionId1, reverse, start, end);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI1_R(
		long objectDefinitionId1, boolean reverse, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().findByODI1_R(
			objectDefinitionId1, reverse, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI1_R(
		long objectDefinitionId1, boolean reverse, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByODI1_R(
			objectDefinitionId1, reverse, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByODI1_R_First(
			long objectDefinitionId1, boolean reverse,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI1_R_First(
			objectDefinitionId1, reverse, orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByODI1_R_First(
		long objectDefinitionId1, boolean reverse,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByODI1_R_First(
			objectDefinitionId1, reverse, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByODI1_R_Last(
			long objectDefinitionId1, boolean reverse,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI1_R_Last(
			objectDefinitionId1, reverse, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByODI1_R_Last(
		long objectDefinitionId1, boolean reverse,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByODI1_R_Last(
			objectDefinitionId1, reverse, orderByComparator);
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public static ObjectRelationship[] findByODI1_R_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId1,
			boolean reverse,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI1_R_PrevAndNext(
			objectRelationshipId, objectDefinitionId1, reverse,
			orderByComparator);
	}

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 */
	public static void removeByODI1_R(
		long objectDefinitionId1, boolean reverse) {

		getPersistence().removeByODI1_R(objectDefinitionId1, reverse);
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @return the number of matching object relationships
	 */
	public static int countByODI1_R(long objectDefinitionId1, boolean reverse) {
		return getPersistence().countByODI1_R(objectDefinitionId1, reverse);
	}

	/**
	 * Returns all the object relationships where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @return the matching object relationships
	 */
	public static List<ObjectRelationship> findByODI2_E(
		long objectDefinitionId2, boolean edge) {

		return getPersistence().findByODI2_E(objectDefinitionId2, edge);
	}

	/**
	 * Returns a range of all the object relationships where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI2_E(
		long objectDefinitionId2, boolean edge, int start, int end) {

		return getPersistence().findByODI2_E(
			objectDefinitionId2, edge, start, end);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI2_E(
		long objectDefinitionId2, boolean edge, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().findByODI2_E(
			objectDefinitionId2, edge, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI2_E(
		long objectDefinitionId2, boolean edge, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByODI2_E(
			objectDefinitionId2, edge, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByODI2_E_First(
			long objectDefinitionId2, boolean edge,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI2_E_First(
			objectDefinitionId2, edge, orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByODI2_E_First(
		long objectDefinitionId2, boolean edge,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByODI2_E_First(
			objectDefinitionId2, edge, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByODI2_E_Last(
			long objectDefinitionId2, boolean edge,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI2_E_Last(
			objectDefinitionId2, edge, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByODI2_E_Last(
		long objectDefinitionId2, boolean edge,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByODI2_E_Last(
			objectDefinitionId2, edge, orderByComparator);
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public static ObjectRelationship[] findByODI2_E_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId2, boolean edge,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI2_E_PrevAndNext(
			objectRelationshipId, objectDefinitionId2, edge, orderByComparator);
	}

	/**
	 * Removes all the object relationships where objectDefinitionId2 = &#63; and edge = &#63; from the database.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 */
	public static void removeByODI2_E(long objectDefinitionId2, boolean edge) {
		getPersistence().removeByODI2_E(objectDefinitionId2, edge);
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId2 = &#63; and edge = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param edge the edge
	 * @return the number of matching object relationships
	 */
	public static int countByODI2_E(long objectDefinitionId2, boolean edge) {
		return getPersistence().countByODI2_E(objectDefinitionId2, edge);
	}

	/**
	 * Returns all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @return the matching object relationships
	 */
	public static List<ObjectRelationship> findByODI2_R(
		long objectDefinitionId2, boolean reverse) {

		return getPersistence().findByODI2_R(objectDefinitionId2, reverse);
	}

	/**
	 * Returns a range of all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI2_R(
		long objectDefinitionId2, boolean reverse, int start, int end) {

		return getPersistence().findByODI2_R(
			objectDefinitionId2, reverse, start, end);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI2_R(
		long objectDefinitionId2, boolean reverse, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().findByODI2_R(
			objectDefinitionId2, reverse, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI2_R(
		long objectDefinitionId2, boolean reverse, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByODI2_R(
			objectDefinitionId2, reverse, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByODI2_R_First(
			long objectDefinitionId2, boolean reverse,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI2_R_First(
			objectDefinitionId2, reverse, orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByODI2_R_First(
		long objectDefinitionId2, boolean reverse,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByODI2_R_First(
			objectDefinitionId2, reverse, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByODI2_R_Last(
			long objectDefinitionId2, boolean reverse,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI2_R_Last(
			objectDefinitionId2, reverse, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByODI2_R_Last(
		long objectDefinitionId2, boolean reverse,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByODI2_R_Last(
			objectDefinitionId2, reverse, orderByComparator);
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public static ObjectRelationship[] findByODI2_R_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId2,
			boolean reverse,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI2_R_PrevAndNext(
			objectRelationshipId, objectDefinitionId2, reverse,
			orderByComparator);
	}

	/**
	 * Removes all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63; from the database.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 */
	public static void removeByODI2_R(
		long objectDefinitionId2, boolean reverse) {

		getPersistence().removeByODI2_R(objectDefinitionId2, reverse);
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId2 = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @return the number of matching object relationships
	 */
	public static int countByODI2_R(long objectDefinitionId2, boolean reverse) {
		return getPersistence().countByODI2_R(objectDefinitionId2, reverse);
	}

	/**
	 * Returns the object relationship where dbTableName = &#63; and reverse = &#63; or throws a <code>NoSuchObjectRelationshipException</code> if it could not be found.
	 *
	 * @param dbTableName the db table name
	 * @param reverse the reverse
	 * @return the matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByDTN_R(
			String dbTableName, boolean reverse)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByDTN_R(dbTableName, reverse);
	}

	/**
	 * Returns the object relationship where dbTableName = &#63; and reverse = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param dbTableName the db table name
	 * @param reverse the reverse
	 * @return the matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByDTN_R(
		String dbTableName, boolean reverse) {

		return getPersistence().fetchByDTN_R(dbTableName, reverse);
	}

	/**
	 * Returns the object relationship where dbTableName = &#63; and reverse = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param dbTableName the db table name
	 * @param reverse the reverse
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByDTN_R(
		String dbTableName, boolean reverse, boolean useFinderCache) {

		return getPersistence().fetchByDTN_R(
			dbTableName, reverse, useFinderCache);
	}

	/**
	 * Removes the object relationship where dbTableName = &#63; and reverse = &#63; from the database.
	 *
	 * @param dbTableName the db table name
	 * @param reverse the reverse
	 * @return the object relationship that was removed
	 */
	public static ObjectRelationship removeByDTN_R(
			String dbTableName, boolean reverse)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().removeByDTN_R(dbTableName, reverse);
	}

	/**
	 * Returns the number of object relationships where dbTableName = &#63; and reverse = &#63;.
	 *
	 * @param dbTableName the db table name
	 * @param reverse the reverse
	 * @return the number of matching object relationships
	 */
	public static int countByDTN_R(String dbTableName, boolean reverse) {
		return getPersistence().countByDTN_R(dbTableName, reverse);
	}

	/**
	 * Returns the object relationship where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId1 = &#63; or throws a <code>NoSuchObjectRelationshipException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId1 the object definition id1
	 * @return the matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByERC_C_ODI1(
			String externalReferenceCode, long companyId,
			long objectDefinitionId1)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByERC_C_ODI1(
			externalReferenceCode, companyId, objectDefinitionId1);
	}

	/**
	 * Returns the object relationship where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId1 = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId1 the object definition id1
	 * @return the matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByERC_C_ODI1(
		String externalReferenceCode, long companyId,
		long objectDefinitionId1) {

		return getPersistence().fetchByERC_C_ODI1(
			externalReferenceCode, companyId, objectDefinitionId1);
	}

	/**
	 * Returns the object relationship where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId1 = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId1 the object definition id1
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByERC_C_ODI1(
		String externalReferenceCode, long companyId, long objectDefinitionId1,
		boolean useFinderCache) {

		return getPersistence().fetchByERC_C_ODI1(
			externalReferenceCode, companyId, objectDefinitionId1,
			useFinderCache);
	}

	/**
	 * Removes the object relationship where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId1 = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId1 the object definition id1
	 * @return the object relationship that was removed
	 */
	public static ObjectRelationship removeByERC_C_ODI1(
			String externalReferenceCode, long companyId,
			long objectDefinitionId1)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().removeByERC_C_ODI1(
			externalReferenceCode, companyId, objectDefinitionId1);
	}

	/**
	 * Returns the number of object relationships where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId1 = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId1 the object definition id1
	 * @return the number of matching object relationships
	 */
	public static int countByERC_C_ODI1(
		String externalReferenceCode, long companyId,
		long objectDefinitionId1) {

		return getPersistence().countByERC_C_ODI1(
			externalReferenceCode, companyId, objectDefinitionId1);
	}

	/**
	 * Returns all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @return the matching object relationships
	 */
	public static List<ObjectRelationship> findByODI1_ODI2_T(
		long objectDefinitionId1, long objectDefinitionId2, String type) {

		return getPersistence().findByODI1_ODI2_T(
			objectDefinitionId1, objectDefinitionId2, type);
	}

	/**
	 * Returns a range of all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI1_ODI2_T(
		long objectDefinitionId1, long objectDefinitionId2, String type,
		int start, int end) {

		return getPersistence().findByODI1_ODI2_T(
			objectDefinitionId1, objectDefinitionId2, type, start, end);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI1_ODI2_T(
		long objectDefinitionId1, long objectDefinitionId2, String type,
		int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().findByODI1_ODI2_T(
			objectDefinitionId1, objectDefinitionId2, type, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI1_ODI2_T(
		long objectDefinitionId1, long objectDefinitionId2, String type,
		int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByODI1_ODI2_T(
			objectDefinitionId1, objectDefinitionId2, type, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByODI1_ODI2_T_First(
			long objectDefinitionId1, long objectDefinitionId2, String type,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI1_ODI2_T_First(
			objectDefinitionId1, objectDefinitionId2, type, orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByODI1_ODI2_T_First(
		long objectDefinitionId1, long objectDefinitionId2, String type,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByODI1_ODI2_T_First(
			objectDefinitionId1, objectDefinitionId2, type, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByODI1_ODI2_T_Last(
			long objectDefinitionId1, long objectDefinitionId2, String type,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI1_ODI2_T_Last(
			objectDefinitionId1, objectDefinitionId2, type, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByODI1_ODI2_T_Last(
		long objectDefinitionId1, long objectDefinitionId2, String type,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByODI1_ODI2_T_Last(
			objectDefinitionId1, objectDefinitionId2, type, orderByComparator);
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public static ObjectRelationship[] findByODI1_ODI2_T_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId1,
			long objectDefinitionId2, String type,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI1_ODI2_T_PrevAndNext(
			objectRelationshipId, objectDefinitionId1, objectDefinitionId2,
			type, orderByComparator);
	}

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 */
	public static void removeByODI1_ODI2_T(
		long objectDefinitionId1, long objectDefinitionId2, String type) {

		getPersistence().removeByODI1_ODI2_T(
			objectDefinitionId1, objectDefinitionId2, type);
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param type the type
	 * @return the number of matching object relationships
	 */
	public static int countByODI1_ODI2_T(
		long objectDefinitionId1, long objectDefinitionId2, String type) {

		return getPersistence().countByODI1_ODI2_T(
			objectDefinitionId1, objectDefinitionId2, type);
	}

	/**
	 * Returns all the object relationships where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @return the matching object relationships
	 */
	public static List<ObjectRelationship> findByODI1_DT_R(
		long objectDefinitionId1, String deletionType, boolean reverse) {

		return getPersistence().findByODI1_DT_R(
			objectDefinitionId1, deletionType, reverse);
	}

	/**
	 * Returns a range of all the object relationships where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI1_DT_R(
		long objectDefinitionId1, String deletionType, boolean reverse,
		int start, int end) {

		return getPersistence().findByODI1_DT_R(
			objectDefinitionId1, deletionType, reverse, start, end);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI1_DT_R(
		long objectDefinitionId1, String deletionType, boolean reverse,
		int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().findByODI1_DT_R(
			objectDefinitionId1, deletionType, reverse, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI1_DT_R(
		long objectDefinitionId1, String deletionType, boolean reverse,
		int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByODI1_DT_R(
			objectDefinitionId1, deletionType, reverse, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByODI1_DT_R_First(
			long objectDefinitionId1, String deletionType, boolean reverse,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI1_DT_R_First(
			objectDefinitionId1, deletionType, reverse, orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByODI1_DT_R_First(
		long objectDefinitionId1, String deletionType, boolean reverse,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByODI1_DT_R_First(
			objectDefinitionId1, deletionType, reverse, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByODI1_DT_R_Last(
			long objectDefinitionId1, String deletionType, boolean reverse,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI1_DT_R_Last(
			objectDefinitionId1, deletionType, reverse, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByODI1_DT_R_Last(
		long objectDefinitionId1, String deletionType, boolean reverse,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByODI1_DT_R_Last(
			objectDefinitionId1, deletionType, reverse, orderByComparator);
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public static ObjectRelationship[] findByODI1_DT_R_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId1,
			String deletionType, boolean reverse,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI1_DT_R_PrevAndNext(
			objectRelationshipId, objectDefinitionId1, deletionType, reverse,
			orderByComparator);
	}

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 */
	public static void removeByODI1_DT_R(
		long objectDefinitionId1, String deletionType, boolean reverse) {

		getPersistence().removeByODI1_DT_R(
			objectDefinitionId1, deletionType, reverse);
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and deletionType = &#63; and reverse = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param deletionType the deletion type
	 * @param reverse the reverse
	 * @return the number of matching object relationships
	 */
	public static int countByODI1_DT_R(
		long objectDefinitionId1, String deletionType, boolean reverse) {

		return getPersistence().countByODI1_DT_R(
			objectDefinitionId1, deletionType, reverse);
	}

	/**
	 * Returns all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @return the matching object relationships
	 */
	public static List<ObjectRelationship> findByODI1_R_T(
		long objectDefinitionId1, boolean reverse, String type) {

		return getPersistence().findByODI1_R_T(
			objectDefinitionId1, reverse, type);
	}

	/**
	 * Returns a range of all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI1_R_T(
		long objectDefinitionId1, boolean reverse, String type, int start,
		int end) {

		return getPersistence().findByODI1_R_T(
			objectDefinitionId1, reverse, type, start, end);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI1_R_T(
		long objectDefinitionId1, boolean reverse, String type, int start,
		int end, OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().findByODI1_R_T(
			objectDefinitionId1, reverse, type, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI1_R_T(
		long objectDefinitionId1, boolean reverse, String type, int start,
		int end, OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByODI1_R_T(
			objectDefinitionId1, reverse, type, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByODI1_R_T_First(
			long objectDefinitionId1, boolean reverse, String type,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI1_R_T_First(
			objectDefinitionId1, reverse, type, orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByODI1_R_T_First(
		long objectDefinitionId1, boolean reverse, String type,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByODI1_R_T_First(
			objectDefinitionId1, reverse, type, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByODI1_R_T_Last(
			long objectDefinitionId1, boolean reverse, String type,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI1_R_T_Last(
			objectDefinitionId1, reverse, type, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByODI1_R_T_Last(
		long objectDefinitionId1, boolean reverse, String type,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByODI1_R_T_Last(
			objectDefinitionId1, reverse, type, orderByComparator);
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public static ObjectRelationship[] findByODI1_R_T_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId1,
			boolean reverse, String type,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI1_R_T_PrevAndNext(
			objectRelationshipId, objectDefinitionId1, reverse, type,
			orderByComparator);
	}

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 */
	public static void removeByODI1_R_T(
		long objectDefinitionId1, boolean reverse, String type) {

		getPersistence().removeByODI1_R_T(objectDefinitionId1, reverse, type);
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param reverse the reverse
	 * @param type the type
	 * @return the number of matching object relationships
	 */
	public static int countByODI1_R_T(
		long objectDefinitionId1, boolean reverse, String type) {

		return getPersistence().countByODI1_R_T(
			objectDefinitionId1, reverse, type);
	}

	/**
	 * Returns all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @return the matching object relationships
	 */
	public static List<ObjectRelationship> findByODI2_R_T(
		long objectDefinitionId2, boolean reverse, String type) {

		return getPersistence().findByODI2_R_T(
			objectDefinitionId2, reverse, type);
	}

	/**
	 * Returns a range of all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI2_R_T(
		long objectDefinitionId2, boolean reverse, String type, int start,
		int end) {

		return getPersistence().findByODI2_R_T(
			objectDefinitionId2, reverse, type, start, end);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI2_R_T(
		long objectDefinitionId2, boolean reverse, String type, int start,
		int end, OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().findByODI2_R_T(
			objectDefinitionId2, reverse, type, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI2_R_T(
		long objectDefinitionId2, boolean reverse, String type, int start,
		int end, OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByODI2_R_T(
			objectDefinitionId2, reverse, type, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByODI2_R_T_First(
			long objectDefinitionId2, boolean reverse, String type,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI2_R_T_First(
			objectDefinitionId2, reverse, type, orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByODI2_R_T_First(
		long objectDefinitionId2, boolean reverse, String type,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByODI2_R_T_First(
			objectDefinitionId2, reverse, type, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByODI2_R_T_Last(
			long objectDefinitionId2, boolean reverse, String type,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI2_R_T_Last(
			objectDefinitionId2, reverse, type, orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByODI2_R_T_Last(
		long objectDefinitionId2, boolean reverse, String type,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByODI2_R_T_Last(
			objectDefinitionId2, reverse, type, orderByComparator);
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public static ObjectRelationship[] findByODI2_R_T_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId2,
			boolean reverse, String type,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI2_R_T_PrevAndNext(
			objectRelationshipId, objectDefinitionId2, reverse, type,
			orderByComparator);
	}

	/**
	 * Removes all the object relationships where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63; from the database.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 */
	public static void removeByODI2_R_T(
		long objectDefinitionId2, boolean reverse, String type) {

		getPersistence().removeByODI2_R_T(objectDefinitionId2, reverse, type);
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId2 = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId2 the object definition id2
	 * @param reverse the reverse
	 * @param type the type
	 * @return the number of matching object relationships
	 */
	public static int countByODI2_R_T(
		long objectDefinitionId2, boolean reverse, String type) {

		return getPersistence().countByODI2_R_T(
			objectDefinitionId2, reverse, type);
	}

	/**
	 * Returns all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @return the matching object relationships
	 */
	public static List<ObjectRelationship> findByODI1_ODI2_N_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		String type) {

		return getPersistence().findByODI1_ODI2_N_T(
			objectDefinitionId1, objectDefinitionId2, name, type);
	}

	/**
	 * Returns a range of all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI1_ODI2_N_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		String type, int start, int end) {

		return getPersistence().findByODI1_ODI2_N_T(
			objectDefinitionId1, objectDefinitionId2, name, type, start, end);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI1_ODI2_N_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		String type, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().findByODI1_ODI2_N_T(
			objectDefinitionId1, objectDefinitionId2, name, type, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object relationships
	 */
	public static List<ObjectRelationship> findByODI1_ODI2_N_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		String type, int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByODI1_ODI2_N_T(
			objectDefinitionId1, objectDefinitionId2, name, type, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByODI1_ODI2_N_T_First(
			long objectDefinitionId1, long objectDefinitionId2, String name,
			String type,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI1_ODI2_N_T_First(
			objectDefinitionId1, objectDefinitionId2, name, type,
			orderByComparator);
	}

	/**
	 * Returns the first object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByODI1_ODI2_N_T_First(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		String type, OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByODI1_ODI2_N_T_First(
			objectDefinitionId1, objectDefinitionId2, name, type,
			orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByODI1_ODI2_N_T_Last(
			long objectDefinitionId1, long objectDefinitionId2, String name,
			String type,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI1_ODI2_N_T_Last(
			objectDefinitionId1, objectDefinitionId2, name, type,
			orderByComparator);
	}

	/**
	 * Returns the last object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByODI1_ODI2_N_T_Last(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		String type, OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().fetchByODI1_ODI2_N_T_Last(
			objectDefinitionId1, objectDefinitionId2, name, type,
			orderByComparator);
	}

	/**
	 * Returns the object relationships before and after the current object relationship in the ordered set where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param objectRelationshipId the primary key of the current object relationship
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public static ObjectRelationship[] findByODI1_ODI2_N_T_PrevAndNext(
			long objectRelationshipId, long objectDefinitionId1,
			long objectDefinitionId2, String name, String type,
			OrderByComparator<ObjectRelationship> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI1_ODI2_N_T_PrevAndNext(
			objectRelationshipId, objectDefinitionId1, objectDefinitionId2,
			name, type, orderByComparator);
	}

	/**
	 * Removes all the object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 */
	public static void removeByODI1_ODI2_N_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		String type) {

		getPersistence().removeByODI1_ODI2_N_T(
			objectDefinitionId1, objectDefinitionId2, name, type);
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param type the type
	 * @return the number of matching object relationships
	 */
	public static int countByODI1_ODI2_N_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		String type) {

		return getPersistence().countByODI1_ODI2_N_T(
			objectDefinitionId1, objectDefinitionId2, name, type);
	}

	/**
	 * Returns the object relationship where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and reverse = &#63; and type = &#63; or throws a <code>NoSuchObjectRelationshipException</code> if it could not be found.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param reverse the reverse
	 * @param type the type
	 * @return the matching object relationship
	 * @throws NoSuchObjectRelationshipException if a matching object relationship could not be found
	 */
	public static ObjectRelationship findByODI1_ODI2_N_R_T(
			long objectDefinitionId1, long objectDefinitionId2, String name,
			boolean reverse, String type)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByODI1_ODI2_N_R_T(
			objectDefinitionId1, objectDefinitionId2, name, reverse, type);
	}

	/**
	 * Returns the object relationship where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and reverse = &#63; and type = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param reverse the reverse
	 * @param type the type
	 * @return the matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByODI1_ODI2_N_R_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		boolean reverse, String type) {

		return getPersistence().fetchByODI1_ODI2_N_R_T(
			objectDefinitionId1, objectDefinitionId2, name, reverse, type);
	}

	/**
	 * Returns the object relationship where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and reverse = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param reverse the reverse
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	public static ObjectRelationship fetchByODI1_ODI2_N_R_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		boolean reverse, String type, boolean useFinderCache) {

		return getPersistence().fetchByODI1_ODI2_N_R_T(
			objectDefinitionId1, objectDefinitionId2, name, reverse, type,
			useFinderCache);
	}

	/**
	 * Removes the object relationship where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and reverse = &#63; and type = &#63; from the database.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param reverse the reverse
	 * @param type the type
	 * @return the object relationship that was removed
	 */
	public static ObjectRelationship removeByODI1_ODI2_N_R_T(
			long objectDefinitionId1, long objectDefinitionId2, String name,
			boolean reverse, String type)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().removeByODI1_ODI2_N_R_T(
			objectDefinitionId1, objectDefinitionId2, name, reverse, type);
	}

	/**
	 * Returns the number of object relationships where objectDefinitionId1 = &#63; and objectDefinitionId2 = &#63; and name = &#63; and reverse = &#63; and type = &#63;.
	 *
	 * @param objectDefinitionId1 the object definition id1
	 * @param objectDefinitionId2 the object definition id2
	 * @param name the name
	 * @param reverse the reverse
	 * @param type the type
	 * @return the number of matching object relationships
	 */
	public static int countByODI1_ODI2_N_R_T(
		long objectDefinitionId1, long objectDefinitionId2, String name,
		boolean reverse, String type) {

		return getPersistence().countByODI1_ODI2_N_R_T(
			objectDefinitionId1, objectDefinitionId2, name, reverse, type);
	}

	/**
	 * Caches the object relationship in the entity cache if it is enabled.
	 *
	 * @param objectRelationship the object relationship
	 */
	public static void cacheResult(ObjectRelationship objectRelationship) {
		getPersistence().cacheResult(objectRelationship);
	}

	/**
	 * Caches the object relationships in the entity cache if it is enabled.
	 *
	 * @param objectRelationships the object relationships
	 */
	public static void cacheResult(
		List<ObjectRelationship> objectRelationships) {

		getPersistence().cacheResult(objectRelationships);
	}

	/**
	 * Creates a new object relationship with the primary key. Does not add the object relationship to the database.
	 *
	 * @param objectRelationshipId the primary key for the new object relationship
	 * @return the new object relationship
	 */
	public static ObjectRelationship create(long objectRelationshipId) {
		return getPersistence().create(objectRelationshipId);
	}

	/**
	 * Removes the object relationship with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectRelationshipId the primary key of the object relationship
	 * @return the object relationship that was removed
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public static ObjectRelationship remove(long objectRelationshipId)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().remove(objectRelationshipId);
	}

	public static ObjectRelationship updateImpl(
		ObjectRelationship objectRelationship) {

		return getPersistence().updateImpl(objectRelationship);
	}

	/**
	 * Returns the object relationship with the primary key or throws a <code>NoSuchObjectRelationshipException</code> if it could not be found.
	 *
	 * @param objectRelationshipId the primary key of the object relationship
	 * @return the object relationship
	 * @throws NoSuchObjectRelationshipException if a object relationship with the primary key could not be found
	 */
	public static ObjectRelationship findByPrimaryKey(long objectRelationshipId)
		throws com.liferay.object.exception.NoSuchObjectRelationshipException {

		return getPersistence().findByPrimaryKey(objectRelationshipId);
	}

	/**
	 * Returns the object relationship with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectRelationshipId the primary key of the object relationship
	 * @return the object relationship, or <code>null</code> if a object relationship with the primary key could not be found
	 */
	public static ObjectRelationship fetchByPrimaryKey(
		long objectRelationshipId) {

		return getPersistence().fetchByPrimaryKey(objectRelationshipId);
	}

	/**
	 * Returns all the object relationships.
	 *
	 * @return the object relationships
	 */
	public static List<ObjectRelationship> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the object relationships.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of object relationships
	 */
	public static List<ObjectRelationship> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the object relationships.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of object relationships
	 */
	public static List<ObjectRelationship> findAll(
		int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object relationships.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of object relationships
	 */
	public static List<ObjectRelationship> findAll(
		int start, int end,
		OrderByComparator<ObjectRelationship> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the object relationships from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of object relationships.
	 *
	 * @return the number of object relationships
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static ObjectRelationshipPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		ObjectRelationshipPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile ObjectRelationshipPersistence _persistence;

}
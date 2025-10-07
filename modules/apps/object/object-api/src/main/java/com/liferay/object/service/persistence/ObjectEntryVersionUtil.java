/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence;

import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the object entry version service. This utility wraps <code>com.liferay.object.service.persistence.impl.ObjectEntryVersionPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see ObjectEntryVersionPersistence
 * @generated
 */
public class ObjectEntryVersionUtil {

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
	public static void clearCache(ObjectEntryVersion objectEntryVersion) {
		getPersistence().clearCache(objectEntryVersion);
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
	public static Map<Serializable, ObjectEntryVersion> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<ObjectEntryVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<ObjectEntryVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<ObjectEntryVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static ObjectEntryVersion update(
		ObjectEntryVersion objectEntryVersion) {

		return getPersistence().update(objectEntryVersion);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static ObjectEntryVersion update(
		ObjectEntryVersion objectEntryVersion, ServiceContext serviceContext) {

		return getPersistence().update(objectEntryVersion, serviceContext);
	}

	/**
	 * Returns all the object entry versions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching object entry versions
	 */
	public static List<ObjectEntryVersion> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the object entry versions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @return the range of matching object entry versions
	 */
	public static List<ObjectEntryVersion> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the object entry versions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry versions
	 */
	public static List<ObjectEntryVersion> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object entry versions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry versions
	 */
	public static List<ObjectEntryVersion> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object entry version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	public static ObjectEntryVersion findByUuid_First(
			String uuid,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectEntryVersionException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first object entry version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	public static ObjectEntryVersion fetchByUuid_First(
		String uuid, OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the last object entry version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	public static ObjectEntryVersion findByUuid_Last(
			String uuid,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectEntryVersionException {

		return getPersistence().findByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the last object entry version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	public static ObjectEntryVersion fetchByUuid_Last(
		String uuid, OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return getPersistence().fetchByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the object entry versions before and after the current object entry version in the ordered set where uuid = &#63;.
	 *
	 * @param objectEntryVersionId the primary key of the current object entry version
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry version
	 * @throws NoSuchObjectEntryVersionException if a object entry version with the primary key could not be found
	 */
	public static ObjectEntryVersion[] findByUuid_PrevAndNext(
			long objectEntryVersionId, String uuid,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectEntryVersionException {

		return getPersistence().findByUuid_PrevAndNext(
			objectEntryVersionId, uuid, orderByComparator);
	}

	/**
	 * Removes all the object entry versions where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of object entry versions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object entry versions
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns all the object entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching object entry versions
	 */
	public static List<ObjectEntryVersion> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the object entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @return the range of matching object entry versions
	 */
	public static List<ObjectEntryVersion> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the object entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry versions
	 */
	public static List<ObjectEntryVersion> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry versions
	 */
	public static List<ObjectEntryVersion> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object entry version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	public static ObjectEntryVersion findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectEntryVersionException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first object entry version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	public static ObjectEntryVersion fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last object entry version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	public static ObjectEntryVersion findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectEntryVersionException {

		return getPersistence().findByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last object entry version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	public static ObjectEntryVersion fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return getPersistence().fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the object entry versions before and after the current object entry version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param objectEntryVersionId the primary key of the current object entry version
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry version
	 * @throws NoSuchObjectEntryVersionException if a object entry version with the primary key could not be found
	 */
	public static ObjectEntryVersion[] findByUuid_C_PrevAndNext(
			long objectEntryVersionId, String uuid, long companyId,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectEntryVersionException {

		return getPersistence().findByUuid_C_PrevAndNext(
			objectEntryVersionId, uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the object entry versions where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of object entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object entry versions
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns all the object entry versions where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object entry versions
	 */
	public static List<ObjectEntryVersion> findByObjectDefinitionId(
		long objectDefinitionId) {

		return getPersistence().findByObjectDefinitionId(objectDefinitionId);
	}

	/**
	 * Returns a range of all the object entry versions where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @return the range of matching object entry versions
	 */
	public static List<ObjectEntryVersion> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end) {

		return getPersistence().findByObjectDefinitionId(
			objectDefinitionId, start, end);
	}

	/**
	 * Returns an ordered range of all the object entry versions where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry versions
	 */
	public static List<ObjectEntryVersion> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return getPersistence().findByObjectDefinitionId(
			objectDefinitionId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object entry versions where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry versions
	 */
	public static List<ObjectEntryVersion> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByObjectDefinitionId(
			objectDefinitionId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object entry version in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	public static ObjectEntryVersion findByObjectDefinitionId_First(
			long objectDefinitionId,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectEntryVersionException {

		return getPersistence().findByObjectDefinitionId_First(
			objectDefinitionId, orderByComparator);
	}

	/**
	 * Returns the first object entry version in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	public static ObjectEntryVersion fetchByObjectDefinitionId_First(
		long objectDefinitionId,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return getPersistence().fetchByObjectDefinitionId_First(
			objectDefinitionId, orderByComparator);
	}

	/**
	 * Returns the last object entry version in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	public static ObjectEntryVersion findByObjectDefinitionId_Last(
			long objectDefinitionId,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectEntryVersionException {

		return getPersistence().findByObjectDefinitionId_Last(
			objectDefinitionId, orderByComparator);
	}

	/**
	 * Returns the last object entry version in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	public static ObjectEntryVersion fetchByObjectDefinitionId_Last(
		long objectDefinitionId,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return getPersistence().fetchByObjectDefinitionId_Last(
			objectDefinitionId, orderByComparator);
	}

	/**
	 * Returns the object entry versions before and after the current object entry version in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectEntryVersionId the primary key of the current object entry version
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry version
	 * @throws NoSuchObjectEntryVersionException if a object entry version with the primary key could not be found
	 */
	public static ObjectEntryVersion[] findByObjectDefinitionId_PrevAndNext(
			long objectEntryVersionId, long objectDefinitionId,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectEntryVersionException {

		return getPersistence().findByObjectDefinitionId_PrevAndNext(
			objectEntryVersionId, objectDefinitionId, orderByComparator);
	}

	/**
	 * Removes all the object entry versions where objectDefinitionId = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 */
	public static void removeByObjectDefinitionId(long objectDefinitionId) {
		getPersistence().removeByObjectDefinitionId(objectDefinitionId);
	}

	/**
	 * Returns the number of object entry versions where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object entry versions
	 */
	public static int countByObjectDefinitionId(long objectDefinitionId) {
		return getPersistence().countByObjectDefinitionId(objectDefinitionId);
	}

	/**
	 * Returns all the object entry versions where objectEntryId = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @return the matching object entry versions
	 */
	public static List<ObjectEntryVersion> findByObjectEntryId(
		long objectEntryId) {

		return getPersistence().findByObjectEntryId(objectEntryId);
	}

	/**
	 * Returns a range of all the object entry versions where objectEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectEntryId the object entry ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @return the range of matching object entry versions
	 */
	public static List<ObjectEntryVersion> findByObjectEntryId(
		long objectEntryId, int start, int end) {

		return getPersistence().findByObjectEntryId(objectEntryId, start, end);
	}

	/**
	 * Returns an ordered range of all the object entry versions where objectEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectEntryId the object entry ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry versions
	 */
	public static List<ObjectEntryVersion> findByObjectEntryId(
		long objectEntryId, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return getPersistence().findByObjectEntryId(
			objectEntryId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object entry versions where objectEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectEntryId the object entry ID
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry versions
	 */
	public static List<ObjectEntryVersion> findByObjectEntryId(
		long objectEntryId, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByObjectEntryId(
			objectEntryId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object entry version in the ordered set where objectEntryId = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	public static ObjectEntryVersion findByObjectEntryId_First(
			long objectEntryId,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectEntryVersionException {

		return getPersistence().findByObjectEntryId_First(
			objectEntryId, orderByComparator);
	}

	/**
	 * Returns the first object entry version in the ordered set where objectEntryId = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	public static ObjectEntryVersion fetchByObjectEntryId_First(
		long objectEntryId,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return getPersistence().fetchByObjectEntryId_First(
			objectEntryId, orderByComparator);
	}

	/**
	 * Returns the last object entry version in the ordered set where objectEntryId = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	public static ObjectEntryVersion findByObjectEntryId_Last(
			long objectEntryId,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectEntryVersionException {

		return getPersistence().findByObjectEntryId_Last(
			objectEntryId, orderByComparator);
	}

	/**
	 * Returns the last object entry version in the ordered set where objectEntryId = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	public static ObjectEntryVersion fetchByObjectEntryId_Last(
		long objectEntryId,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return getPersistence().fetchByObjectEntryId_Last(
			objectEntryId, orderByComparator);
	}

	/**
	 * Returns the object entry versions before and after the current object entry version in the ordered set where objectEntryId = &#63;.
	 *
	 * @param objectEntryVersionId the primary key of the current object entry version
	 * @param objectEntryId the object entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry version
	 * @throws NoSuchObjectEntryVersionException if a object entry version with the primary key could not be found
	 */
	public static ObjectEntryVersion[] findByObjectEntryId_PrevAndNext(
			long objectEntryVersionId, long objectEntryId,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectEntryVersionException {

		return getPersistence().findByObjectEntryId_PrevAndNext(
			objectEntryVersionId, objectEntryId, orderByComparator);
	}

	/**
	 * Removes all the object entry versions where objectEntryId = &#63; from the database.
	 *
	 * @param objectEntryId the object entry ID
	 */
	public static void removeByObjectEntryId(long objectEntryId) {
		getPersistence().removeByObjectEntryId(objectEntryId);
	}

	/**
	 * Returns the number of object entry versions where objectEntryId = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @return the number of matching object entry versions
	 */
	public static int countByObjectEntryId(long objectEntryId) {
		return getPersistence().countByObjectEntryId(objectEntryId);
	}

	/**
	 * Returns all the object entry versions where companyId = &#63; and createDate = &#63;.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @return the matching object entry versions
	 */
	public static List<ObjectEntryVersion> findByC_CD(
		long companyId, Date createDate) {

		return getPersistence().findByC_CD(companyId, createDate);
	}

	/**
	 * Returns a range of all the object entry versions where companyId = &#63; and createDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @return the range of matching object entry versions
	 */
	public static List<ObjectEntryVersion> findByC_CD(
		long companyId, Date createDate, int start, int end) {

		return getPersistence().findByC_CD(companyId, createDate, start, end);
	}

	/**
	 * Returns an ordered range of all the object entry versions where companyId = &#63; and createDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry versions
	 */
	public static List<ObjectEntryVersion> findByC_CD(
		long companyId, Date createDate, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return getPersistence().findByC_CD(
			companyId, createDate, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object entry versions where companyId = &#63; and createDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry versions
	 */
	public static List<ObjectEntryVersion> findByC_CD(
		long companyId, Date createDate, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_CD(
			companyId, createDate, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object entry version in the ordered set where companyId = &#63; and createDate = &#63;.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	public static ObjectEntryVersion findByC_CD_First(
			long companyId, Date createDate,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectEntryVersionException {

		return getPersistence().findByC_CD_First(
			companyId, createDate, orderByComparator);
	}

	/**
	 * Returns the first object entry version in the ordered set where companyId = &#63; and createDate = &#63;.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	public static ObjectEntryVersion fetchByC_CD_First(
		long companyId, Date createDate,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return getPersistence().fetchByC_CD_First(
			companyId, createDate, orderByComparator);
	}

	/**
	 * Returns the last object entry version in the ordered set where companyId = &#63; and createDate = &#63;.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	public static ObjectEntryVersion findByC_CD_Last(
			long companyId, Date createDate,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectEntryVersionException {

		return getPersistence().findByC_CD_Last(
			companyId, createDate, orderByComparator);
	}

	/**
	 * Returns the last object entry version in the ordered set where companyId = &#63; and createDate = &#63;.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	public static ObjectEntryVersion fetchByC_CD_Last(
		long companyId, Date createDate,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return getPersistence().fetchByC_CD_Last(
			companyId, createDate, orderByComparator);
	}

	/**
	 * Returns the object entry versions before and after the current object entry version in the ordered set where companyId = &#63; and createDate = &#63;.
	 *
	 * @param objectEntryVersionId the primary key of the current object entry version
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry version
	 * @throws NoSuchObjectEntryVersionException if a object entry version with the primary key could not be found
	 */
	public static ObjectEntryVersion[] findByC_CD_PrevAndNext(
			long objectEntryVersionId, long companyId, Date createDate,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectEntryVersionException {

		return getPersistence().findByC_CD_PrevAndNext(
			objectEntryVersionId, companyId, createDate, orderByComparator);
	}

	/**
	 * Removes all the object entry versions where companyId = &#63; and createDate = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 */
	public static void removeByC_CD(long companyId, Date createDate) {
		getPersistence().removeByC_CD(companyId, createDate);
	}

	/**
	 * Returns the number of object entry versions where companyId = &#63; and createDate = &#63;.
	 *
	 * @param companyId the company ID
	 * @param createDate the create date
	 * @return the number of matching object entry versions
	 */
	public static int countByC_CD(long companyId, Date createDate) {
		return getPersistence().countByC_CD(companyId, createDate);
	}

	/**
	 * Returns the object entry version where objectEntryId = &#63; and version = &#63; or throws a <code>NoSuchObjectEntryVersionException</code> if it could not be found.
	 *
	 * @param objectEntryId the object entry ID
	 * @param version the version
	 * @return the matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	public static ObjectEntryVersion findByOEI_V(
			long objectEntryId, int version)
		throws com.liferay.object.exception.NoSuchObjectEntryVersionException {

		return getPersistence().findByOEI_V(objectEntryId, version);
	}

	/**
	 * Returns the object entry version where objectEntryId = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param objectEntryId the object entry ID
	 * @param version the version
	 * @return the matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	public static ObjectEntryVersion fetchByOEI_V(
		long objectEntryId, int version) {

		return getPersistence().fetchByOEI_V(objectEntryId, version);
	}

	/**
	 * Returns the object entry version where objectEntryId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectEntryId the object entry ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	public static ObjectEntryVersion fetchByOEI_V(
		long objectEntryId, int version, boolean useFinderCache) {

		return getPersistence().fetchByOEI_V(
			objectEntryId, version, useFinderCache);
	}

	/**
	 * Removes the object entry version where objectEntryId = &#63; and version = &#63; from the database.
	 *
	 * @param objectEntryId the object entry ID
	 * @param version the version
	 * @return the object entry version that was removed
	 */
	public static ObjectEntryVersion removeByOEI_V(
			long objectEntryId, int version)
		throws com.liferay.object.exception.NoSuchObjectEntryVersionException {

		return getPersistence().removeByOEI_V(objectEntryId, version);
	}

	/**
	 * Returns the number of object entry versions where objectEntryId = &#63; and version = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param version the version
	 * @return the number of matching object entry versions
	 */
	public static int countByOEI_V(long objectEntryId, int version) {
		return getPersistence().countByOEI_V(objectEntryId, version);
	}

	/**
	 * Returns all the object entry versions where objectEntryId = &#63; and status = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @return the matching object entry versions
	 */
	public static List<ObjectEntryVersion> findByOEI_S(
		long objectEntryId, int status) {

		return getPersistence().findByOEI_S(objectEntryId, status);
	}

	/**
	 * Returns a range of all the object entry versions where objectEntryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @return the range of matching object entry versions
	 */
	public static List<ObjectEntryVersion> findByOEI_S(
		long objectEntryId, int status, int start, int end) {

		return getPersistence().findByOEI_S(objectEntryId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the object entry versions where objectEntryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry versions
	 */
	public static List<ObjectEntryVersion> findByOEI_S(
		long objectEntryId, int status, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return getPersistence().findByOEI_S(
			objectEntryId, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object entry versions where objectEntryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry versions
	 */
	public static List<ObjectEntryVersion> findByOEI_S(
		long objectEntryId, int status, int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByOEI_S(
			objectEntryId, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object entry version in the ordered set where objectEntryId = &#63; and status = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	public static ObjectEntryVersion findByOEI_S_First(
			long objectEntryId, int status,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectEntryVersionException {

		return getPersistence().findByOEI_S_First(
			objectEntryId, status, orderByComparator);
	}

	/**
	 * Returns the first object entry version in the ordered set where objectEntryId = &#63; and status = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	public static ObjectEntryVersion fetchByOEI_S_First(
		long objectEntryId, int status,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return getPersistence().fetchByOEI_S_First(
			objectEntryId, status, orderByComparator);
	}

	/**
	 * Returns the last object entry version in the ordered set where objectEntryId = &#63; and status = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version
	 * @throws NoSuchObjectEntryVersionException if a matching object entry version could not be found
	 */
	public static ObjectEntryVersion findByOEI_S_Last(
			long objectEntryId, int status,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectEntryVersionException {

		return getPersistence().findByOEI_S_Last(
			objectEntryId, status, orderByComparator);
	}

	/**
	 * Returns the last object entry version in the ordered set where objectEntryId = &#63; and status = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	public static ObjectEntryVersion fetchByOEI_S_Last(
		long objectEntryId, int status,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return getPersistence().fetchByOEI_S_Last(
			objectEntryId, status, orderByComparator);
	}

	/**
	 * Returns the object entry versions before and after the current object entry version in the ordered set where objectEntryId = &#63; and status = &#63;.
	 *
	 * @param objectEntryVersionId the primary key of the current object entry version
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry version
	 * @throws NoSuchObjectEntryVersionException if a object entry version with the primary key could not be found
	 */
	public static ObjectEntryVersion[] findByOEI_S_PrevAndNext(
			long objectEntryVersionId, long objectEntryId, int status,
			OrderByComparator<ObjectEntryVersion> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectEntryVersionException {

		return getPersistence().findByOEI_S_PrevAndNext(
			objectEntryVersionId, objectEntryId, status, orderByComparator);
	}

	/**
	 * Removes all the object entry versions where objectEntryId = &#63; and status = &#63; from the database.
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 */
	public static void removeByOEI_S(long objectEntryId, int status) {
		getPersistence().removeByOEI_S(objectEntryId, status);
	}

	/**
	 * Returns the number of object entry versions where objectEntryId = &#63; and status = &#63;.
	 *
	 * @param objectEntryId the object entry ID
	 * @param status the status
	 * @return the number of matching object entry versions
	 */
	public static int countByOEI_S(long objectEntryId, int status) {
		return getPersistence().countByOEI_S(objectEntryId, status);
	}

	/**
	 * Caches the object entry version in the entity cache if it is enabled.
	 *
	 * @param objectEntryVersion the object entry version
	 */
	public static void cacheResult(ObjectEntryVersion objectEntryVersion) {
		getPersistence().cacheResult(objectEntryVersion);
	}

	/**
	 * Caches the object entry versions in the entity cache if it is enabled.
	 *
	 * @param objectEntryVersions the object entry versions
	 */
	public static void cacheResult(
		List<ObjectEntryVersion> objectEntryVersions) {

		getPersistence().cacheResult(objectEntryVersions);
	}

	/**
	 * Creates a new object entry version with the primary key. Does not add the object entry version to the database.
	 *
	 * @param objectEntryVersionId the primary key for the new object entry version
	 * @return the new object entry version
	 */
	public static ObjectEntryVersion create(long objectEntryVersionId) {
		return getPersistence().create(objectEntryVersionId);
	}

	/**
	 * Removes the object entry version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectEntryVersionId the primary key of the object entry version
	 * @return the object entry version that was removed
	 * @throws NoSuchObjectEntryVersionException if a object entry version with the primary key could not be found
	 */
	public static ObjectEntryVersion remove(long objectEntryVersionId)
		throws com.liferay.object.exception.NoSuchObjectEntryVersionException {

		return getPersistence().remove(objectEntryVersionId);
	}

	public static ObjectEntryVersion updateImpl(
		ObjectEntryVersion objectEntryVersion) {

		return getPersistence().updateImpl(objectEntryVersion);
	}

	/**
	 * Returns the object entry version with the primary key or throws a <code>NoSuchObjectEntryVersionException</code> if it could not be found.
	 *
	 * @param objectEntryVersionId the primary key of the object entry version
	 * @return the object entry version
	 * @throws NoSuchObjectEntryVersionException if a object entry version with the primary key could not be found
	 */
	public static ObjectEntryVersion findByPrimaryKey(long objectEntryVersionId)
		throws com.liferay.object.exception.NoSuchObjectEntryVersionException {

		return getPersistence().findByPrimaryKey(objectEntryVersionId);
	}

	/**
	 * Returns the object entry version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectEntryVersionId the primary key of the object entry version
	 * @return the object entry version, or <code>null</code> if a object entry version with the primary key could not be found
	 */
	public static ObjectEntryVersion fetchByPrimaryKey(
		long objectEntryVersionId) {

		return getPersistence().fetchByPrimaryKey(objectEntryVersionId);
	}

	/**
	 * Returns all the object entry versions.
	 *
	 * @return the object entry versions
	 */
	public static List<ObjectEntryVersion> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the object entry versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @return the range of object entry versions
	 */
	public static List<ObjectEntryVersion> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the object entry versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of object entry versions
	 */
	public static List<ObjectEntryVersion> findAll(
		int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object entry versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of object entry versions
	 */
	public static List<ObjectEntryVersion> findAll(
		int start, int end,
		OrderByComparator<ObjectEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the object entry versions from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of object entry versions.
	 *
	 * @return the number of object entry versions
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static ObjectEntryVersionPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		ObjectEntryVersionPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile ObjectEntryVersionPersistence _persistence;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence;

import com.liferay.object.model.ObjectField;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the object field service. This utility wraps <code>com.liferay.object.service.persistence.impl.ObjectFieldPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see ObjectFieldPersistence
 * @generated
 */
public class ObjectFieldUtil {

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
	public static void clearCache(ObjectField objectField) {
		getPersistence().clearCache(objectField);
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
	public static Map<Serializable, ObjectField> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<ObjectField> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<ObjectField> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<ObjectField> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static ObjectField update(ObjectField objectField) {
		return getPersistence().update(objectField);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static ObjectField update(
		ObjectField objectField, ServiceContext serviceContext) {

		return getPersistence().update(objectField, serviceContext);
	}

	/**
	 * Returns all the object fields where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching object fields
	 */
	public static List<ObjectField> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the object fields where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	public static List<ObjectField> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the object fields where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	public static List<ObjectField> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object fields where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	public static List<ObjectField> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object field in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByUuid_First(
			String uuid, OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first object field in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByUuid_First(
		String uuid, OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the last object field in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByUuid_Last(
			String uuid, OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the last object field in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByUuid_Last(
		String uuid, OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().fetchByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the object fields before and after the current object field in the ordered set where uuid = &#63;.
	 *
	 * @param objectFieldId the primary key of the current object field
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	public static ObjectField[] findByUuid_PrevAndNext(
			long objectFieldId, String uuid,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByUuid_PrevAndNext(
			objectFieldId, uuid, orderByComparator);
	}

	/**
	 * Removes all the object fields where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of object fields where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object fields
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns all the object fields where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching object fields
	 */
	public static List<ObjectField> findByUuid_C(String uuid, long companyId) {
		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the object fields where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	public static List<ObjectField> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the object fields where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	public static List<ObjectField> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object fields where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	public static List<ObjectField> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object field in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first object field in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last object field in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last object field in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the object fields before and after the current object field in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param objectFieldId the primary key of the current object field
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	public static ObjectField[] findByUuid_C_PrevAndNext(
			long objectFieldId, String uuid, long companyId,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByUuid_C_PrevAndNext(
			objectFieldId, uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the object fields where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of object fields where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object fields
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns all the object fields where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching object fields
	 */
	public static List<ObjectField> findByCompanyId(long companyId) {
		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the object fields where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	public static List<ObjectField> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the object fields where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	public static List<ObjectField> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object fields where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	public static List<ObjectField> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object field in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByCompanyId_First(
			long companyId, OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first object field in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByCompanyId_First(
		long companyId, OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last object field in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByCompanyId_Last(
			long companyId, OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last object field in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByCompanyId_Last(
		long companyId, OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().fetchByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the object fields before and after the current object field in the ordered set where companyId = &#63;.
	 *
	 * @param objectFieldId the primary key of the current object field
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	public static ObjectField[] findByCompanyId_PrevAndNext(
			long objectFieldId, long companyId,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByCompanyId_PrevAndNext(
			objectFieldId, companyId, orderByComparator);
	}

	/**
	 * Removes all the object fields where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of object fields where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching object fields
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns all the object fields where listTypeDefinitionId = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @return the matching object fields
	 */
	public static List<ObjectField> findByListTypeDefinitionId(
		long listTypeDefinitionId) {

		return getPersistence().findByListTypeDefinitionId(
			listTypeDefinitionId);
	}

	/**
	 * Returns a range of all the object fields where listTypeDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	public static List<ObjectField> findByListTypeDefinitionId(
		long listTypeDefinitionId, int start, int end) {

		return getPersistence().findByListTypeDefinitionId(
			listTypeDefinitionId, start, end);
	}

	/**
	 * Returns an ordered range of all the object fields where listTypeDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	public static List<ObjectField> findByListTypeDefinitionId(
		long listTypeDefinitionId, int start, int end,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().findByListTypeDefinitionId(
			listTypeDefinitionId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object fields where listTypeDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	public static List<ObjectField> findByListTypeDefinitionId(
		long listTypeDefinitionId, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByListTypeDefinitionId(
			listTypeDefinitionId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object field in the ordered set where listTypeDefinitionId = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByListTypeDefinitionId_First(
			long listTypeDefinitionId,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByListTypeDefinitionId_First(
			listTypeDefinitionId, orderByComparator);
	}

	/**
	 * Returns the first object field in the ordered set where listTypeDefinitionId = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByListTypeDefinitionId_First(
		long listTypeDefinitionId,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().fetchByListTypeDefinitionId_First(
			listTypeDefinitionId, orderByComparator);
	}

	/**
	 * Returns the last object field in the ordered set where listTypeDefinitionId = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByListTypeDefinitionId_Last(
			long listTypeDefinitionId,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByListTypeDefinitionId_Last(
			listTypeDefinitionId, orderByComparator);
	}

	/**
	 * Returns the last object field in the ordered set where listTypeDefinitionId = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByListTypeDefinitionId_Last(
		long listTypeDefinitionId,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().fetchByListTypeDefinitionId_Last(
			listTypeDefinitionId, orderByComparator);
	}

	/**
	 * Returns the object fields before and after the current object field in the ordered set where listTypeDefinitionId = &#63;.
	 *
	 * @param objectFieldId the primary key of the current object field
	 * @param listTypeDefinitionId the list type definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	public static ObjectField[] findByListTypeDefinitionId_PrevAndNext(
			long objectFieldId, long listTypeDefinitionId,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByListTypeDefinitionId_PrevAndNext(
			objectFieldId, listTypeDefinitionId, orderByComparator);
	}

	/**
	 * Removes all the object fields where listTypeDefinitionId = &#63; from the database.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 */
	public static void removeByListTypeDefinitionId(long listTypeDefinitionId) {
		getPersistence().removeByListTypeDefinitionId(listTypeDefinitionId);
	}

	/**
	 * Returns the number of object fields where listTypeDefinitionId = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @return the number of matching object fields
	 */
	public static int countByListTypeDefinitionId(long listTypeDefinitionId) {
		return getPersistence().countByListTypeDefinitionId(
			listTypeDefinitionId);
	}

	/**
	 * Returns all the object fields where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object fields
	 */
	public static List<ObjectField> findByObjectDefinitionId(
		long objectDefinitionId) {

		return getPersistence().findByObjectDefinitionId(objectDefinitionId);
	}

	/**
	 * Returns a range of all the object fields where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	public static List<ObjectField> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end) {

		return getPersistence().findByObjectDefinitionId(
			objectDefinitionId, start, end);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	public static List<ObjectField> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().findByObjectDefinitionId(
			objectDefinitionId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	public static List<ObjectField> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByObjectDefinitionId(
			objectDefinitionId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByObjectDefinitionId_First(
			long objectDefinitionId,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByObjectDefinitionId_First(
			objectDefinitionId, orderByComparator);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByObjectDefinitionId_First(
		long objectDefinitionId,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().fetchByObjectDefinitionId_First(
			objectDefinitionId, orderByComparator);
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByObjectDefinitionId_Last(
			long objectDefinitionId,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByObjectDefinitionId_Last(
			objectDefinitionId, orderByComparator);
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByObjectDefinitionId_Last(
		long objectDefinitionId,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().fetchByObjectDefinitionId_Last(
			objectDefinitionId, orderByComparator);
	}

	/**
	 * Returns the object fields before and after the current object field in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectFieldId the primary key of the current object field
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	public static ObjectField[] findByObjectDefinitionId_PrevAndNext(
			long objectFieldId, long objectDefinitionId,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByObjectDefinitionId_PrevAndNext(
			objectFieldId, objectDefinitionId, orderByComparator);
	}

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 */
	public static void removeByObjectDefinitionId(long objectDefinitionId) {
		getPersistence().removeByObjectDefinitionId(objectDefinitionId);
	}

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object fields
	 */
	public static int countByObjectDefinitionId(long objectDefinitionId) {
		return getPersistence().countByObjectDefinitionId(objectDefinitionId);
	}

	/**
	 * Returns all the object fields where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching object fields
	 */
	public static List<ObjectField> findByC_U(long companyId, long userId) {
		return getPersistence().findByC_U(companyId, userId);
	}

	/**
	 * Returns a range of all the object fields where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	public static List<ObjectField> findByC_U(
		long companyId, long userId, int start, int end) {

		return getPersistence().findByC_U(companyId, userId, start, end);
	}

	/**
	 * Returns an ordered range of all the object fields where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	public static List<ObjectField> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().findByC_U(
			companyId, userId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object fields where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	public static List<ObjectField> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_U(
			companyId, userId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object field in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByC_U_First(
			long companyId, long userId,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByC_U_First(
			companyId, userId, orderByComparator);
	}

	/**
	 * Returns the first object field in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByC_U_First(
		long companyId, long userId,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().fetchByC_U_First(
			companyId, userId, orderByComparator);
	}

	/**
	 * Returns the last object field in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByC_U_Last(
			long companyId, long userId,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByC_U_Last(
			companyId, userId, orderByComparator);
	}

	/**
	 * Returns the last object field in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByC_U_Last(
		long companyId, long userId,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().fetchByC_U_Last(
			companyId, userId, orderByComparator);
	}

	/**
	 * Returns the object fields before and after the current object field in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param objectFieldId the primary key of the current object field
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	public static ObjectField[] findByC_U_PrevAndNext(
			long objectFieldId, long companyId, long userId,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByC_U_PrevAndNext(
			objectFieldId, companyId, userId, orderByComparator);
	}

	/**
	 * Removes all the object fields where companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 */
	public static void removeByC_U(long companyId, long userId) {
		getPersistence().removeByC_U(companyId, userId);
	}

	/**
	 * Returns the number of object fields where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching object fields
	 */
	public static int countByC_U(long companyId, long userId) {
		return getPersistence().countByC_U(companyId, userId);
	}

	/**
	 * Returns all the object fields where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @return the matching object fields
	 */
	public static List<ObjectField> findByLTDI_S(
		long listTypeDefinitionId, boolean state) {

		return getPersistence().findByLTDI_S(listTypeDefinitionId, state);
	}

	/**
	 * Returns a range of all the object fields where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	public static List<ObjectField> findByLTDI_S(
		long listTypeDefinitionId, boolean state, int start, int end) {

		return getPersistence().findByLTDI_S(
			listTypeDefinitionId, state, start, end);
	}

	/**
	 * Returns an ordered range of all the object fields where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	public static List<ObjectField> findByLTDI_S(
		long listTypeDefinitionId, boolean state, int start, int end,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().findByLTDI_S(
			listTypeDefinitionId, state, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object fields where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	public static List<ObjectField> findByLTDI_S(
		long listTypeDefinitionId, boolean state, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByLTDI_S(
			listTypeDefinitionId, state, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object field in the ordered set where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByLTDI_S_First(
			long listTypeDefinitionId, boolean state,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByLTDI_S_First(
			listTypeDefinitionId, state, orderByComparator);
	}

	/**
	 * Returns the first object field in the ordered set where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByLTDI_S_First(
		long listTypeDefinitionId, boolean state,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().fetchByLTDI_S_First(
			listTypeDefinitionId, state, orderByComparator);
	}

	/**
	 * Returns the last object field in the ordered set where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByLTDI_S_Last(
			long listTypeDefinitionId, boolean state,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByLTDI_S_Last(
			listTypeDefinitionId, state, orderByComparator);
	}

	/**
	 * Returns the last object field in the ordered set where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByLTDI_S_Last(
		long listTypeDefinitionId, boolean state,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().fetchByLTDI_S_Last(
			listTypeDefinitionId, state, orderByComparator);
	}

	/**
	 * Returns the object fields before and after the current object field in the ordered set where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * @param objectFieldId the primary key of the current object field
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	public static ObjectField[] findByLTDI_S_PrevAndNext(
			long objectFieldId, long listTypeDefinitionId, boolean state,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByLTDI_S_PrevAndNext(
			objectFieldId, listTypeDefinitionId, state, orderByComparator);
	}

	/**
	 * Removes all the object fields where listTypeDefinitionId = &#63; and state = &#63; from the database.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 */
	public static void removeByLTDI_S(
		long listTypeDefinitionId, boolean state) {

		getPersistence().removeByLTDI_S(listTypeDefinitionId, state);
	}

	/**
	 * Returns the number of object fields where listTypeDefinitionId = &#63; and state = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param state the state
	 * @return the number of matching object fields
	 */
	public static int countByLTDI_S(long listTypeDefinitionId, boolean state) {
		return getPersistence().countByLTDI_S(listTypeDefinitionId, state);
	}

	/**
	 * Returns all the object fields where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @return the matching object fields
	 */
	public static List<ObjectField> findByODI_BT(
		long objectDefinitionId, String businessType) {

		return getPersistence().findByODI_BT(objectDefinitionId, businessType);
	}

	/**
	 * Returns a range of all the object fields where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	public static List<ObjectField> findByODI_BT(
		long objectDefinitionId, String businessType, int start, int end) {

		return getPersistence().findByODI_BT(
			objectDefinitionId, businessType, start, end);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	public static List<ObjectField> findByODI_BT(
		long objectDefinitionId, String businessType, int start, int end,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().findByODI_BT(
			objectDefinitionId, businessType, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	public static List<ObjectField> findByODI_BT(
		long objectDefinitionId, String businessType, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByODI_BT(
			objectDefinitionId, businessType, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByODI_BT_First(
			long objectDefinitionId, String businessType,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByODI_BT_First(
			objectDefinitionId, businessType, orderByComparator);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByODI_BT_First(
		long objectDefinitionId, String businessType,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().fetchByODI_BT_First(
			objectDefinitionId, businessType, orderByComparator);
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByODI_BT_Last(
			long objectDefinitionId, String businessType,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByODI_BT_Last(
			objectDefinitionId, businessType, orderByComparator);
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByODI_BT_Last(
		long objectDefinitionId, String businessType,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().fetchByODI_BT_Last(
			objectDefinitionId, businessType, orderByComparator);
	}

	/**
	 * Returns the object fields before and after the current object field in the ordered set where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * @param objectFieldId the primary key of the current object field
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	public static ObjectField[] findByODI_BT_PrevAndNext(
			long objectFieldId, long objectDefinitionId, String businessType,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByODI_BT_PrevAndNext(
			objectFieldId, objectDefinitionId, businessType, orderByComparator);
	}

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; and businessType = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 */
	public static void removeByODI_BT(
		long objectDefinitionId, String businessType) {

		getPersistence().removeByODI_BT(objectDefinitionId, businessType);
	}

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and businessType = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param businessType the business type
	 * @return the number of matching object fields
	 */
	public static int countByODI_BT(
		long objectDefinitionId, String businessType) {

		return getPersistence().countByODI_BT(objectDefinitionId, businessType);
	}

	/**
	 * Returns all the object fields where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @return the matching object fields
	 */
	public static List<ObjectField> findByODI_DTN(
		long objectDefinitionId, String dbTableName) {

		return getPersistence().findByODI_DTN(objectDefinitionId, dbTableName);
	}

	/**
	 * Returns a range of all the object fields where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	public static List<ObjectField> findByODI_DTN(
		long objectDefinitionId, String dbTableName, int start, int end) {

		return getPersistence().findByODI_DTN(
			objectDefinitionId, dbTableName, start, end);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	public static List<ObjectField> findByODI_DTN(
		long objectDefinitionId, String dbTableName, int start, int end,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().findByODI_DTN(
			objectDefinitionId, dbTableName, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	public static List<ObjectField> findByODI_DTN(
		long objectDefinitionId, String dbTableName, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByODI_DTN(
			objectDefinitionId, dbTableName, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByODI_DTN_First(
			long objectDefinitionId, String dbTableName,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByODI_DTN_First(
			objectDefinitionId, dbTableName, orderByComparator);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByODI_DTN_First(
		long objectDefinitionId, String dbTableName,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().fetchByODI_DTN_First(
			objectDefinitionId, dbTableName, orderByComparator);
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByODI_DTN_Last(
			long objectDefinitionId, String dbTableName,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByODI_DTN_Last(
			objectDefinitionId, dbTableName, orderByComparator);
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByODI_DTN_Last(
		long objectDefinitionId, String dbTableName,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().fetchByODI_DTN_Last(
			objectDefinitionId, dbTableName, orderByComparator);
	}

	/**
	 * Returns the object fields before and after the current object field in the ordered set where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * @param objectFieldId the primary key of the current object field
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	public static ObjectField[] findByODI_DTN_PrevAndNext(
			long objectFieldId, long objectDefinitionId, String dbTableName,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByODI_DTN_PrevAndNext(
			objectFieldId, objectDefinitionId, dbTableName, orderByComparator);
	}

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; and dbTableName = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 */
	public static void removeByODI_DTN(
		long objectDefinitionId, String dbTableName) {

		getPersistence().removeByODI_DTN(objectDefinitionId, dbTableName);
	}

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and dbTableName = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbTableName the db table name
	 * @return the number of matching object fields
	 */
	public static int countByODI_DTN(
		long objectDefinitionId, String dbTableName) {

		return getPersistence().countByODI_DTN(objectDefinitionId, dbTableName);
	}

	/**
	 * Returns all the object fields where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @return the matching object fields
	 */
	public static List<ObjectField> findByODI_I(
		long objectDefinitionId, boolean indexed) {

		return getPersistence().findByODI_I(objectDefinitionId, indexed);
	}

	/**
	 * Returns a range of all the object fields where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	public static List<ObjectField> findByODI_I(
		long objectDefinitionId, boolean indexed, int start, int end) {

		return getPersistence().findByODI_I(
			objectDefinitionId, indexed, start, end);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	public static List<ObjectField> findByODI_I(
		long objectDefinitionId, boolean indexed, int start, int end,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().findByODI_I(
			objectDefinitionId, indexed, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	public static List<ObjectField> findByODI_I(
		long objectDefinitionId, boolean indexed, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByODI_I(
			objectDefinitionId, indexed, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByODI_I_First(
			long objectDefinitionId, boolean indexed,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByODI_I_First(
			objectDefinitionId, indexed, orderByComparator);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByODI_I_First(
		long objectDefinitionId, boolean indexed,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().fetchByODI_I_First(
			objectDefinitionId, indexed, orderByComparator);
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByODI_I_Last(
			long objectDefinitionId, boolean indexed,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByODI_I_Last(
			objectDefinitionId, indexed, orderByComparator);
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByODI_I_Last(
		long objectDefinitionId, boolean indexed,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().fetchByODI_I_Last(
			objectDefinitionId, indexed, orderByComparator);
	}

	/**
	 * Returns the object fields before and after the current object field in the ordered set where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * @param objectFieldId the primary key of the current object field
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	public static ObjectField[] findByODI_I_PrevAndNext(
			long objectFieldId, long objectDefinitionId, boolean indexed,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByODI_I_PrevAndNext(
			objectFieldId, objectDefinitionId, indexed, orderByComparator);
	}

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; and indexed = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 */
	public static void removeByODI_I(long objectDefinitionId, boolean indexed) {
		getPersistence().removeByODI_I(objectDefinitionId, indexed);
	}

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param indexed the indexed
	 * @return the number of matching object fields
	 */
	public static int countByODI_I(long objectDefinitionId, boolean indexed) {
		return getPersistence().countByODI_I(objectDefinitionId, indexed);
	}

	/**
	 * Returns all the object fields where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @return the matching object fields
	 */
	public static List<ObjectField> findByODI_L(
		long objectDefinitionId, boolean localized) {

		return getPersistence().findByODI_L(objectDefinitionId, localized);
	}

	/**
	 * Returns a range of all the object fields where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	public static List<ObjectField> findByODI_L(
		long objectDefinitionId, boolean localized, int start, int end) {

		return getPersistence().findByODI_L(
			objectDefinitionId, localized, start, end);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	public static List<ObjectField> findByODI_L(
		long objectDefinitionId, boolean localized, int start, int end,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().findByODI_L(
			objectDefinitionId, localized, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	public static List<ObjectField> findByODI_L(
		long objectDefinitionId, boolean localized, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByODI_L(
			objectDefinitionId, localized, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByODI_L_First(
			long objectDefinitionId, boolean localized,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByODI_L_First(
			objectDefinitionId, localized, orderByComparator);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByODI_L_First(
		long objectDefinitionId, boolean localized,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().fetchByODI_L_First(
			objectDefinitionId, localized, orderByComparator);
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByODI_L_Last(
			long objectDefinitionId, boolean localized,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByODI_L_Last(
			objectDefinitionId, localized, orderByComparator);
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByODI_L_Last(
		long objectDefinitionId, boolean localized,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().fetchByODI_L_Last(
			objectDefinitionId, localized, orderByComparator);
	}

	/**
	 * Returns the object fields before and after the current object field in the ordered set where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * @param objectFieldId the primary key of the current object field
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	public static ObjectField[] findByODI_L_PrevAndNext(
			long objectFieldId, long objectDefinitionId, boolean localized,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByODI_L_PrevAndNext(
			objectFieldId, objectDefinitionId, localized, orderByComparator);
	}

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; and localized = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 */
	public static void removeByODI_L(
		long objectDefinitionId, boolean localized) {

		getPersistence().removeByODI_L(objectDefinitionId, localized);
	}

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and localized = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @return the number of matching object fields
	 */
	public static int countByODI_L(long objectDefinitionId, boolean localized) {
		return getPersistence().countByODI_L(objectDefinitionId, localized);
	}

	/**
	 * Returns the object field where objectDefinitionId = &#63; and name = &#63; or throws a <code>NoSuchObjectFieldException</code> if it could not be found.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByODI_N(long objectDefinitionId, String name)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByODI_N(objectDefinitionId, name);
	}

	/**
	 * Returns the object field where objectDefinitionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByODI_N(
		long objectDefinitionId, String name) {

		return getPersistence().fetchByODI_N(objectDefinitionId, name);
	}

	/**
	 * Returns the object field where objectDefinitionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByODI_N(
		long objectDefinitionId, String name, boolean useFinderCache) {

		return getPersistence().fetchByODI_N(
			objectDefinitionId, name, useFinderCache);
	}

	/**
	 * Removes the object field where objectDefinitionId = &#63; and name = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the object field that was removed
	 */
	public static ObjectField removeByODI_N(
			long objectDefinitionId, String name)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().removeByODI_N(objectDefinitionId, name);
	}

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the number of matching object fields
	 */
	public static int countByODI_N(long objectDefinitionId, String name) {
		return getPersistence().countByODI_N(objectDefinitionId, name);
	}

	/**
	 * Returns all the object fields where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @return the matching object fields
	 */
	public static List<ObjectField> findByODI_S(
		long objectDefinitionId, boolean system) {

		return getPersistence().findByODI_S(objectDefinitionId, system);
	}

	/**
	 * Returns a range of all the object fields where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	public static List<ObjectField> findByODI_S(
		long objectDefinitionId, boolean system, int start, int end) {

		return getPersistence().findByODI_S(
			objectDefinitionId, system, start, end);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	public static List<ObjectField> findByODI_S(
		long objectDefinitionId, boolean system, int start, int end,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().findByODI_S(
			objectDefinitionId, system, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	public static List<ObjectField> findByODI_S(
		long objectDefinitionId, boolean system, int start, int end,
		OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByODI_S(
			objectDefinitionId, system, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByODI_S_First(
			long objectDefinitionId, boolean system,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByODI_S_First(
			objectDefinitionId, system, orderByComparator);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByODI_S_First(
		long objectDefinitionId, boolean system,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().fetchByODI_S_First(
			objectDefinitionId, system, orderByComparator);
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByODI_S_Last(
			long objectDefinitionId, boolean system,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByODI_S_Last(
			objectDefinitionId, system, orderByComparator);
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByODI_S_Last(
		long objectDefinitionId, boolean system,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().fetchByODI_S_Last(
			objectDefinitionId, system, orderByComparator);
	}

	/**
	 * Returns the object fields before and after the current object field in the ordered set where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * @param objectFieldId the primary key of the current object field
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	public static ObjectField[] findByODI_S_PrevAndNext(
			long objectFieldId, long objectDefinitionId, boolean system,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByODI_S_PrevAndNext(
			objectFieldId, objectDefinitionId, system, orderByComparator);
	}

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; and system = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 */
	public static void removeByODI_S(long objectDefinitionId, boolean system) {
		getPersistence().removeByODI_S(objectDefinitionId, system);
	}

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param system the system
	 * @return the number of matching object fields
	 */
	public static int countByODI_S(long objectDefinitionId, boolean system) {
		return getPersistence().countByODI_S(objectDefinitionId, system);
	}

	/**
	 * Returns the object field where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; or throws a <code>NoSuchObjectFieldException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByERC_C_ODI(
			String externalReferenceCode, long companyId,
			long objectDefinitionId)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByERC_C_ODI(
			externalReferenceCode, companyId, objectDefinitionId);
	}

	/**
	 * Returns the object field where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByERC_C_ODI(
		String externalReferenceCode, long companyId, long objectDefinitionId) {

		return getPersistence().fetchByERC_C_ODI(
			externalReferenceCode, companyId, objectDefinitionId);
	}

	/**
	 * Returns the object field where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByERC_C_ODI(
		String externalReferenceCode, long companyId, long objectDefinitionId,
		boolean useFinderCache) {

		return getPersistence().fetchByERC_C_ODI(
			externalReferenceCode, companyId, objectDefinitionId,
			useFinderCache);
	}

	/**
	 * Removes the object field where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the object field that was removed
	 */
	public static ObjectField removeByERC_C_ODI(
			String externalReferenceCode, long companyId,
			long objectDefinitionId)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().removeByERC_C_ODI(
			externalReferenceCode, companyId, objectDefinitionId);
	}

	/**
	 * Returns the number of object fields where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object fields
	 */
	public static int countByERC_C_ODI(
		String externalReferenceCode, long companyId, long objectDefinitionId) {

		return getPersistence().countByERC_C_ODI(
			externalReferenceCode, companyId, objectDefinitionId);
	}

	/**
	 * Returns all the object fields where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @return the matching object fields
	 */
	public static List<ObjectField> findByODI_DBT_I(
		long objectDefinitionId, String dbType, boolean indexed) {

		return getPersistence().findByODI_DBT_I(
			objectDefinitionId, dbType, indexed);
	}

	/**
	 * Returns a range of all the object fields where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	public static List<ObjectField> findByODI_DBT_I(
		long objectDefinitionId, String dbType, boolean indexed, int start,
		int end) {

		return getPersistence().findByODI_DBT_I(
			objectDefinitionId, dbType, indexed, start, end);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	public static List<ObjectField> findByODI_DBT_I(
		long objectDefinitionId, String dbType, boolean indexed, int start,
		int end, OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().findByODI_DBT_I(
			objectDefinitionId, dbType, indexed, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	public static List<ObjectField> findByODI_DBT_I(
		long objectDefinitionId, String dbType, boolean indexed, int start,
		int end, OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByODI_DBT_I(
			objectDefinitionId, dbType, indexed, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByODI_DBT_I_First(
			long objectDefinitionId, String dbType, boolean indexed,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByODI_DBT_I_First(
			objectDefinitionId, dbType, indexed, orderByComparator);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByODI_DBT_I_First(
		long objectDefinitionId, String dbType, boolean indexed,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().fetchByODI_DBT_I_First(
			objectDefinitionId, dbType, indexed, orderByComparator);
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByODI_DBT_I_Last(
			long objectDefinitionId, String dbType, boolean indexed,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByODI_DBT_I_Last(
			objectDefinitionId, dbType, indexed, orderByComparator);
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByODI_DBT_I_Last(
		long objectDefinitionId, String dbType, boolean indexed,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().fetchByODI_DBT_I_Last(
			objectDefinitionId, dbType, indexed, orderByComparator);
	}

	/**
	 * Returns the object fields before and after the current object field in the ordered set where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * @param objectFieldId the primary key of the current object field
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	public static ObjectField[] findByODI_DBT_I_PrevAndNext(
			long objectFieldId, long objectDefinitionId, String dbType,
			boolean indexed, OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByODI_DBT_I_PrevAndNext(
			objectFieldId, objectDefinitionId, dbType, indexed,
			orderByComparator);
	}

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 */
	public static void removeByODI_DBT_I(
		long objectDefinitionId, String dbType, boolean indexed) {

		getPersistence().removeByODI_DBT_I(objectDefinitionId, dbType, indexed);
	}

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and dbType = &#63; and indexed = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param dbType the db type
	 * @param indexed the indexed
	 * @return the number of matching object fields
	 */
	public static int countByODI_DBT_I(
		long objectDefinitionId, String dbType, boolean indexed) {

		return getPersistence().countByODI_DBT_I(
			objectDefinitionId, dbType, indexed);
	}

	/**
	 * Returns all the object fields where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @return the matching object fields
	 */
	public static List<ObjectField> findByODI_L_S(
		long objectDefinitionId, boolean localized, boolean system) {

		return getPersistence().findByODI_L_S(
			objectDefinitionId, localized, system);
	}

	/**
	 * Returns a range of all the object fields where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of matching object fields
	 */
	public static List<ObjectField> findByODI_L_S(
		long objectDefinitionId, boolean localized, boolean system, int start,
		int end) {

		return getPersistence().findByODI_L_S(
			objectDefinitionId, localized, system, start, end);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object fields
	 */
	public static List<ObjectField> findByODI_L_S(
		long objectDefinitionId, boolean localized, boolean system, int start,
		int end, OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().findByODI_L_S(
			objectDefinitionId, localized, system, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object fields where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object fields
	 */
	public static List<ObjectField> findByODI_L_S(
		long objectDefinitionId, boolean localized, boolean system, int start,
		int end, OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByODI_L_S(
			objectDefinitionId, localized, system, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByODI_L_S_First(
			long objectDefinitionId, boolean localized, boolean system,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByODI_L_S_First(
			objectDefinitionId, localized, system, orderByComparator);
	}

	/**
	 * Returns the first object field in the ordered set where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByODI_L_S_First(
		long objectDefinitionId, boolean localized, boolean system,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().fetchByODI_L_S_First(
			objectDefinitionId, localized, system, orderByComparator);
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field
	 * @throws NoSuchObjectFieldException if a matching object field could not be found
	 */
	public static ObjectField findByODI_L_S_Last(
			long objectDefinitionId, boolean localized, boolean system,
			OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByODI_L_S_Last(
			objectDefinitionId, localized, system, orderByComparator);
	}

	/**
	 * Returns the last object field in the ordered set where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object field, or <code>null</code> if a matching object field could not be found
	 */
	public static ObjectField fetchByODI_L_S_Last(
		long objectDefinitionId, boolean localized, boolean system,
		OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().fetchByODI_L_S_Last(
			objectDefinitionId, localized, system, orderByComparator);
	}

	/**
	 * Returns the object fields before and after the current object field in the ordered set where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * @param objectFieldId the primary key of the current object field
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	public static ObjectField[] findByODI_L_S_PrevAndNext(
			long objectFieldId, long objectDefinitionId, boolean localized,
			boolean system, OrderByComparator<ObjectField> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByODI_L_S_PrevAndNext(
			objectFieldId, objectDefinitionId, localized, system,
			orderByComparator);
	}

	/**
	 * Removes all the object fields where objectDefinitionId = &#63; and localized = &#63; and system = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 */
	public static void removeByODI_L_S(
		long objectDefinitionId, boolean localized, boolean system) {

		getPersistence().removeByODI_L_S(objectDefinitionId, localized, system);
	}

	/**
	 * Returns the number of object fields where objectDefinitionId = &#63; and localized = &#63; and system = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param localized the localized
	 * @param system the system
	 * @return the number of matching object fields
	 */
	public static int countByODI_L_S(
		long objectDefinitionId, boolean localized, boolean system) {

		return getPersistence().countByODI_L_S(
			objectDefinitionId, localized, system);
	}

	/**
	 * Caches the object field in the entity cache if it is enabled.
	 *
	 * @param objectField the object field
	 */
	public static void cacheResult(ObjectField objectField) {
		getPersistence().cacheResult(objectField);
	}

	/**
	 * Caches the object fields in the entity cache if it is enabled.
	 *
	 * @param objectFields the object fields
	 */
	public static void cacheResult(List<ObjectField> objectFields) {
		getPersistence().cacheResult(objectFields);
	}

	/**
	 * Creates a new object field with the primary key. Does not add the object field to the database.
	 *
	 * @param objectFieldId the primary key for the new object field
	 * @return the new object field
	 */
	public static ObjectField create(long objectFieldId) {
		return getPersistence().create(objectFieldId);
	}

	/**
	 * Removes the object field with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectFieldId the primary key of the object field
	 * @return the object field that was removed
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	public static ObjectField remove(long objectFieldId)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().remove(objectFieldId);
	}

	public static ObjectField updateImpl(ObjectField objectField) {
		return getPersistence().updateImpl(objectField);
	}

	/**
	 * Returns the object field with the primary key or throws a <code>NoSuchObjectFieldException</code> if it could not be found.
	 *
	 * @param objectFieldId the primary key of the object field
	 * @return the object field
	 * @throws NoSuchObjectFieldException if a object field with the primary key could not be found
	 */
	public static ObjectField findByPrimaryKey(long objectFieldId)
		throws com.liferay.object.exception.NoSuchObjectFieldException {

		return getPersistence().findByPrimaryKey(objectFieldId);
	}

	/**
	 * Returns the object field with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectFieldId the primary key of the object field
	 * @return the object field, or <code>null</code> if a object field with the primary key could not be found
	 */
	public static ObjectField fetchByPrimaryKey(long objectFieldId) {
		return getPersistence().fetchByPrimaryKey(objectFieldId);
	}

	/**
	 * Returns all the object fields.
	 *
	 * @return the object fields
	 */
	public static List<ObjectField> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the object fields.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @return the range of object fields
	 */
	public static List<ObjectField> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the object fields.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of object fields
	 */
	public static List<ObjectField> findAll(
		int start, int end, OrderByComparator<ObjectField> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object fields.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectFieldModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object fields
	 * @param end the upper bound of the range of object fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of object fields
	 */
	public static List<ObjectField> findAll(
		int start, int end, OrderByComparator<ObjectField> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the object fields from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of object fields.
	 *
	 * @return the number of object fields
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static ObjectFieldPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(ObjectFieldPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile ObjectFieldPersistence _persistence;

}
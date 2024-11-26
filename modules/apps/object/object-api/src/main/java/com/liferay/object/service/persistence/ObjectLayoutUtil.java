/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence;

import com.liferay.object.model.ObjectLayout;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the object layout service. This utility wraps <code>com.liferay.object.service.persistence.impl.ObjectLayoutPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see ObjectLayoutPersistence
 * @generated
 */
public class ObjectLayoutUtil {

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
	public static void clearCache(ObjectLayout objectLayout) {
		getPersistence().clearCache(objectLayout);
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
	public static Map<Serializable, ObjectLayout> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<ObjectLayout> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<ObjectLayout> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<ObjectLayout> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<ObjectLayout> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static ObjectLayout update(ObjectLayout objectLayout) {
		return getPersistence().update(objectLayout);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static ObjectLayout update(
		ObjectLayout objectLayout, ServiceContext serviceContext) {

		return getPersistence().update(objectLayout, serviceContext);
	}

	/**
	 * Returns all the object layouts where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching object layouts
	 */
	public static List<ObjectLayout> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the object layouts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @return the range of matching object layouts
	 */
	public static List<ObjectLayout> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the object layouts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object layouts
	 */
	public static List<ObjectLayout> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectLayout> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object layouts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layouts
	 */
	public static List<ObjectLayout> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectLayout> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	public static ObjectLayout findByUuid_First(
			String uuid, OrderByComparator<ObjectLayout> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectLayoutException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first object layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	public static ObjectLayout fetchByUuid_First(
		String uuid, OrderByComparator<ObjectLayout> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the last object layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	public static ObjectLayout findByUuid_Last(
			String uuid, OrderByComparator<ObjectLayout> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectLayoutException {

		return getPersistence().findByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the last object layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	public static ObjectLayout fetchByUuid_Last(
		String uuid, OrderByComparator<ObjectLayout> orderByComparator) {

		return getPersistence().fetchByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the object layouts before and after the current object layout in the ordered set where uuid = &#63;.
	 *
	 * @param objectLayoutId the primary key of the current object layout
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object layout
	 * @throws NoSuchObjectLayoutException if a object layout with the primary key could not be found
	 */
	public static ObjectLayout[] findByUuid_PrevAndNext(
			long objectLayoutId, String uuid,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectLayoutException {

		return getPersistence().findByUuid_PrevAndNext(
			objectLayoutId, uuid, orderByComparator);
	}

	/**
	 * Removes all the object layouts where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of object layouts where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object layouts
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns all the object layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching object layouts
	 */
	public static List<ObjectLayout> findByUuid_C(String uuid, long companyId) {
		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the object layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @return the range of matching object layouts
	 */
	public static List<ObjectLayout> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the object layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object layouts
	 */
	public static List<ObjectLayout> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectLayout> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layouts
	 */
	public static List<ObjectLayout> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectLayout> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	public static ObjectLayout findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectLayoutException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first object layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	public static ObjectLayout fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectLayout> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last object layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	public static ObjectLayout findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectLayoutException {

		return getPersistence().findByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last object layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	public static ObjectLayout fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<ObjectLayout> orderByComparator) {

		return getPersistence().fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the object layouts before and after the current object layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param objectLayoutId the primary key of the current object layout
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object layout
	 * @throws NoSuchObjectLayoutException if a object layout with the primary key could not be found
	 */
	public static ObjectLayout[] findByUuid_C_PrevAndNext(
			long objectLayoutId, String uuid, long companyId,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectLayoutException {

		return getPersistence().findByUuid_C_PrevAndNext(
			objectLayoutId, uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the object layouts where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of object layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object layouts
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns all the object layouts where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object layouts
	 */
	public static List<ObjectLayout> findByObjectDefinitionId(
		long objectDefinitionId) {

		return getPersistence().findByObjectDefinitionId(objectDefinitionId);
	}

	/**
	 * Returns a range of all the object layouts where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @return the range of matching object layouts
	 */
	public static List<ObjectLayout> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end) {

		return getPersistence().findByObjectDefinitionId(
			objectDefinitionId, start, end);
	}

	/**
	 * Returns an ordered range of all the object layouts where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object layouts
	 */
	public static List<ObjectLayout> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectLayout> orderByComparator) {

		return getPersistence().findByObjectDefinitionId(
			objectDefinitionId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object layouts where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layouts
	 */
	public static List<ObjectLayout> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectLayout> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByObjectDefinitionId(
			objectDefinitionId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object layout in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	public static ObjectLayout findByObjectDefinitionId_First(
			long objectDefinitionId,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectLayoutException {

		return getPersistence().findByObjectDefinitionId_First(
			objectDefinitionId, orderByComparator);
	}

	/**
	 * Returns the first object layout in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	public static ObjectLayout fetchByObjectDefinitionId_First(
		long objectDefinitionId,
		OrderByComparator<ObjectLayout> orderByComparator) {

		return getPersistence().fetchByObjectDefinitionId_First(
			objectDefinitionId, orderByComparator);
	}

	/**
	 * Returns the last object layout in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	public static ObjectLayout findByObjectDefinitionId_Last(
			long objectDefinitionId,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectLayoutException {

		return getPersistence().findByObjectDefinitionId_Last(
			objectDefinitionId, orderByComparator);
	}

	/**
	 * Returns the last object layout in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	public static ObjectLayout fetchByObjectDefinitionId_Last(
		long objectDefinitionId,
		OrderByComparator<ObjectLayout> orderByComparator) {

		return getPersistence().fetchByObjectDefinitionId_Last(
			objectDefinitionId, orderByComparator);
	}

	/**
	 * Returns the object layouts before and after the current object layout in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectLayoutId the primary key of the current object layout
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object layout
	 * @throws NoSuchObjectLayoutException if a object layout with the primary key could not be found
	 */
	public static ObjectLayout[] findByObjectDefinitionId_PrevAndNext(
			long objectLayoutId, long objectDefinitionId,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectLayoutException {

		return getPersistence().findByObjectDefinitionId_PrevAndNext(
			objectLayoutId, objectDefinitionId, orderByComparator);
	}

	/**
	 * Removes all the object layouts where objectDefinitionId = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 */
	public static void removeByObjectDefinitionId(long objectDefinitionId) {
		getPersistence().removeByObjectDefinitionId(objectDefinitionId);
	}

	/**
	 * Returns the number of object layouts where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object layouts
	 */
	public static int countByObjectDefinitionId(long objectDefinitionId) {
		return getPersistence().countByObjectDefinitionId(objectDefinitionId);
	}

	/**
	 * Returns all the object layouts where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @return the matching object layouts
	 */
	public static List<ObjectLayout> findByC_DOL(
		long companyId, boolean defaultObjectLayout) {

		return getPersistence().findByC_DOL(companyId, defaultObjectLayout);
	}

	/**
	 * Returns a range of all the object layouts where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @return the range of matching object layouts
	 */
	public static List<ObjectLayout> findByC_DOL(
		long companyId, boolean defaultObjectLayout, int start, int end) {

		return getPersistence().findByC_DOL(
			companyId, defaultObjectLayout, start, end);
	}

	/**
	 * Returns an ordered range of all the object layouts where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object layouts
	 */
	public static List<ObjectLayout> findByC_DOL(
		long companyId, boolean defaultObjectLayout, int start, int end,
		OrderByComparator<ObjectLayout> orderByComparator) {

		return getPersistence().findByC_DOL(
			companyId, defaultObjectLayout, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object layouts where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layouts
	 */
	public static List<ObjectLayout> findByC_DOL(
		long companyId, boolean defaultObjectLayout, int start, int end,
		OrderByComparator<ObjectLayout> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_DOL(
			companyId, defaultObjectLayout, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object layout in the ordered set where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	public static ObjectLayout findByC_DOL_First(
			long companyId, boolean defaultObjectLayout,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectLayoutException {

		return getPersistence().findByC_DOL_First(
			companyId, defaultObjectLayout, orderByComparator);
	}

	/**
	 * Returns the first object layout in the ordered set where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	public static ObjectLayout fetchByC_DOL_First(
		long companyId, boolean defaultObjectLayout,
		OrderByComparator<ObjectLayout> orderByComparator) {

		return getPersistence().fetchByC_DOL_First(
			companyId, defaultObjectLayout, orderByComparator);
	}

	/**
	 * Returns the last object layout in the ordered set where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	public static ObjectLayout findByC_DOL_Last(
			long companyId, boolean defaultObjectLayout,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectLayoutException {

		return getPersistence().findByC_DOL_Last(
			companyId, defaultObjectLayout, orderByComparator);
	}

	/**
	 * Returns the last object layout in the ordered set where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	public static ObjectLayout fetchByC_DOL_Last(
		long companyId, boolean defaultObjectLayout,
		OrderByComparator<ObjectLayout> orderByComparator) {

		return getPersistence().fetchByC_DOL_Last(
			companyId, defaultObjectLayout, orderByComparator);
	}

	/**
	 * Returns the object layouts before and after the current object layout in the ordered set where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param objectLayoutId the primary key of the current object layout
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object layout
	 * @throws NoSuchObjectLayoutException if a object layout with the primary key could not be found
	 */
	public static ObjectLayout[] findByC_DOL_PrevAndNext(
			long objectLayoutId, long companyId, boolean defaultObjectLayout,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectLayoutException {

		return getPersistence().findByC_DOL_PrevAndNext(
			objectLayoutId, companyId, defaultObjectLayout, orderByComparator);
	}

	/**
	 * Removes all the object layouts where companyId = &#63; and defaultObjectLayout = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 */
	public static void removeByC_DOL(
		long companyId, boolean defaultObjectLayout) {

		getPersistence().removeByC_DOL(companyId, defaultObjectLayout);
	}

	/**
	 * Returns the number of object layouts where companyId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param companyId the company ID
	 * @param defaultObjectLayout the default object layout
	 * @return the number of matching object layouts
	 */
	public static int countByC_DOL(
		long companyId, boolean defaultObjectLayout) {

		return getPersistence().countByC_DOL(companyId, defaultObjectLayout);
	}

	/**
	 * Returns all the object layouts where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @return the matching object layouts
	 */
	public static List<ObjectLayout> findByODI_DOL(
		long objectDefinitionId, boolean defaultObjectLayout) {

		return getPersistence().findByODI_DOL(
			objectDefinitionId, defaultObjectLayout);
	}

	/**
	 * Returns a range of all the object layouts where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @return the range of matching object layouts
	 */
	public static List<ObjectLayout> findByODI_DOL(
		long objectDefinitionId, boolean defaultObjectLayout, int start,
		int end) {

		return getPersistence().findByODI_DOL(
			objectDefinitionId, defaultObjectLayout, start, end);
	}

	/**
	 * Returns an ordered range of all the object layouts where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object layouts
	 */
	public static List<ObjectLayout> findByODI_DOL(
		long objectDefinitionId, boolean defaultObjectLayout, int start,
		int end, OrderByComparator<ObjectLayout> orderByComparator) {

		return getPersistence().findByODI_DOL(
			objectDefinitionId, defaultObjectLayout, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object layouts where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object layouts
	 */
	public static List<ObjectLayout> findByODI_DOL(
		long objectDefinitionId, boolean defaultObjectLayout, int start,
		int end, OrderByComparator<ObjectLayout> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByODI_DOL(
			objectDefinitionId, defaultObjectLayout, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object layout in the ordered set where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	public static ObjectLayout findByODI_DOL_First(
			long objectDefinitionId, boolean defaultObjectLayout,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectLayoutException {

		return getPersistence().findByODI_DOL_First(
			objectDefinitionId, defaultObjectLayout, orderByComparator);
	}

	/**
	 * Returns the first object layout in the ordered set where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	public static ObjectLayout fetchByODI_DOL_First(
		long objectDefinitionId, boolean defaultObjectLayout,
		OrderByComparator<ObjectLayout> orderByComparator) {

		return getPersistence().fetchByODI_DOL_First(
			objectDefinitionId, defaultObjectLayout, orderByComparator);
	}

	/**
	 * Returns the last object layout in the ordered set where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout
	 * @throws NoSuchObjectLayoutException if a matching object layout could not be found
	 */
	public static ObjectLayout findByODI_DOL_Last(
			long objectDefinitionId, boolean defaultObjectLayout,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectLayoutException {

		return getPersistence().findByODI_DOL_Last(
			objectDefinitionId, defaultObjectLayout, orderByComparator);
	}

	/**
	 * Returns the last object layout in the ordered set where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	public static ObjectLayout fetchByODI_DOL_Last(
		long objectDefinitionId, boolean defaultObjectLayout,
		OrderByComparator<ObjectLayout> orderByComparator) {

		return getPersistence().fetchByODI_DOL_Last(
			objectDefinitionId, defaultObjectLayout, orderByComparator);
	}

	/**
	 * Returns the object layouts before and after the current object layout in the ordered set where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param objectLayoutId the primary key of the current object layout
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object layout
	 * @throws NoSuchObjectLayoutException if a object layout with the primary key could not be found
	 */
	public static ObjectLayout[] findByODI_DOL_PrevAndNext(
			long objectLayoutId, long objectDefinitionId,
			boolean defaultObjectLayout,
			OrderByComparator<ObjectLayout> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectLayoutException {

		return getPersistence().findByODI_DOL_PrevAndNext(
			objectLayoutId, objectDefinitionId, defaultObjectLayout,
			orderByComparator);
	}

	/**
	 * Removes all the object layouts where objectDefinitionId = &#63; and defaultObjectLayout = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 */
	public static void removeByODI_DOL(
		long objectDefinitionId, boolean defaultObjectLayout) {

		getPersistence().removeByODI_DOL(
			objectDefinitionId, defaultObjectLayout);
	}

	/**
	 * Returns the number of object layouts where objectDefinitionId = &#63; and defaultObjectLayout = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param defaultObjectLayout the default object layout
	 * @return the number of matching object layouts
	 */
	public static int countByODI_DOL(
		long objectDefinitionId, boolean defaultObjectLayout) {

		return getPersistence().countByODI_DOL(
			objectDefinitionId, defaultObjectLayout);
	}

	/**
	 * Caches the object layout in the entity cache if it is enabled.
	 *
	 * @param objectLayout the object layout
	 */
	public static void cacheResult(ObjectLayout objectLayout) {
		getPersistence().cacheResult(objectLayout);
	}

	/**
	 * Caches the object layouts in the entity cache if it is enabled.
	 *
	 * @param objectLayouts the object layouts
	 */
	public static void cacheResult(List<ObjectLayout> objectLayouts) {
		getPersistence().cacheResult(objectLayouts);
	}

	/**
	 * Creates a new object layout with the primary key. Does not add the object layout to the database.
	 *
	 * @param objectLayoutId the primary key for the new object layout
	 * @return the new object layout
	 */
	public static ObjectLayout create(long objectLayoutId) {
		return getPersistence().create(objectLayoutId);
	}

	/**
	 * Removes the object layout with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectLayoutId the primary key of the object layout
	 * @return the object layout that was removed
	 * @throws NoSuchObjectLayoutException if a object layout with the primary key could not be found
	 */
	public static ObjectLayout remove(long objectLayoutId)
		throws com.liferay.object.exception.NoSuchObjectLayoutException {

		return getPersistence().remove(objectLayoutId);
	}

	public static ObjectLayout updateImpl(ObjectLayout objectLayout) {
		return getPersistence().updateImpl(objectLayout);
	}

	/**
	 * Returns the object layout with the primary key or throws a <code>NoSuchObjectLayoutException</code> if it could not be found.
	 *
	 * @param objectLayoutId the primary key of the object layout
	 * @return the object layout
	 * @throws NoSuchObjectLayoutException if a object layout with the primary key could not be found
	 */
	public static ObjectLayout findByPrimaryKey(long objectLayoutId)
		throws com.liferay.object.exception.NoSuchObjectLayoutException {

		return getPersistence().findByPrimaryKey(objectLayoutId);
	}

	/**
	 * Returns the object layout with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectLayoutId the primary key of the object layout
	 * @return the object layout, or <code>null</code> if a object layout with the primary key could not be found
	 */
	public static ObjectLayout fetchByPrimaryKey(long objectLayoutId) {
		return getPersistence().fetchByPrimaryKey(objectLayoutId);
	}

	/**
	 * Returns all the object layouts.
	 *
	 * @return the object layouts
	 */
	public static List<ObjectLayout> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the object layouts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @return the range of object layouts
	 */
	public static List<ObjectLayout> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the object layouts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of object layouts
	 */
	public static List<ObjectLayout> findAll(
		int start, int end, OrderByComparator<ObjectLayout> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object layouts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of object layouts
	 */
	public static List<ObjectLayout> findAll(
		int start, int end, OrderByComparator<ObjectLayout> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the object layouts from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of object layouts.
	 *
	 * @return the number of object layouts
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static ObjectLayoutPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(ObjectLayoutPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile ObjectLayoutPersistence _persistence;

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence;

import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the object definition setting service. This utility wraps <code>com.liferay.object.service.persistence.impl.ObjectDefinitionSettingPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see ObjectDefinitionSettingPersistence
 * @generated
 */
public class ObjectDefinitionSettingUtil {

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
	public static void clearCache(
		ObjectDefinitionSetting objectDefinitionSetting) {

		getPersistence().clearCache(objectDefinitionSetting);
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
	public static Map<Serializable, ObjectDefinitionSetting> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<ObjectDefinitionSetting> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<ObjectDefinitionSetting> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<ObjectDefinitionSetting> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static ObjectDefinitionSetting update(
		ObjectDefinitionSetting objectDefinitionSetting) {

		return getPersistence().update(objectDefinitionSetting);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static ObjectDefinitionSetting update(
		ObjectDefinitionSetting objectDefinitionSetting,
		ServiceContext serviceContext) {

		return getPersistence().update(objectDefinitionSetting, serviceContext);
	}

	/**
	 * Returns all the object definition settings where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching object definition settings
	 */
	public static List<ObjectDefinitionSetting> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the object definition settings where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @return the range of matching object definition settings
	 */
	public static List<ObjectDefinitionSetting> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the object definition settings where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definition settings
	 */
	public static List<ObjectDefinitionSetting> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definition settings where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definition settings
	 */
	public static List<ObjectDefinitionSetting> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object definition setting in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	public static ObjectDefinitionSetting findByUuid_First(
			String uuid,
			OrderByComparator<ObjectDefinitionSetting> orderByComparator)
		throws com.liferay.object.exception.
			NoSuchObjectDefinitionSettingException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first object definition setting in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	public static ObjectDefinitionSetting fetchByUuid_First(
		String uuid,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the last object definition setting in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	public static ObjectDefinitionSetting findByUuid_Last(
			String uuid,
			OrderByComparator<ObjectDefinitionSetting> orderByComparator)
		throws com.liferay.object.exception.
			NoSuchObjectDefinitionSettingException {

		return getPersistence().findByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the last object definition setting in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	public static ObjectDefinitionSetting fetchByUuid_Last(
		String uuid,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		return getPersistence().fetchByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the object definition settings before and after the current object definition setting in the ordered set where uuid = &#63;.
	 *
	 * @param objectDefinitionSettingId the primary key of the current object definition setting
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a object definition setting with the primary key could not be found
	 */
	public static ObjectDefinitionSetting[] findByUuid_PrevAndNext(
			long objectDefinitionSettingId, String uuid,
			OrderByComparator<ObjectDefinitionSetting> orderByComparator)
		throws com.liferay.object.exception.
			NoSuchObjectDefinitionSettingException {

		return getPersistence().findByUuid_PrevAndNext(
			objectDefinitionSettingId, uuid, orderByComparator);
	}

	/**
	 * Removes all the object definition settings where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of object definition settings where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object definition settings
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns all the object definition settings where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching object definition settings
	 */
	public static List<ObjectDefinitionSetting> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the object definition settings where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @return the range of matching object definition settings
	 */
	public static List<ObjectDefinitionSetting> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the object definition settings where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definition settings
	 */
	public static List<ObjectDefinitionSetting> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definition settings where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definition settings
	 */
	public static List<ObjectDefinitionSetting> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object definition setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	public static ObjectDefinitionSetting findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectDefinitionSetting> orderByComparator)
		throws com.liferay.object.exception.
			NoSuchObjectDefinitionSettingException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first object definition setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	public static ObjectDefinitionSetting fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last object definition setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	public static ObjectDefinitionSetting findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<ObjectDefinitionSetting> orderByComparator)
		throws com.liferay.object.exception.
			NoSuchObjectDefinitionSettingException {

		return getPersistence().findByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last object definition setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	public static ObjectDefinitionSetting fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		return getPersistence().fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the object definition settings before and after the current object definition setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param objectDefinitionSettingId the primary key of the current object definition setting
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a object definition setting with the primary key could not be found
	 */
	public static ObjectDefinitionSetting[] findByUuid_C_PrevAndNext(
			long objectDefinitionSettingId, String uuid, long companyId,
			OrderByComparator<ObjectDefinitionSetting> orderByComparator)
		throws com.liferay.object.exception.
			NoSuchObjectDefinitionSettingException {

		return getPersistence().findByUuid_C_PrevAndNext(
			objectDefinitionSettingId, uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the object definition settings where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of object definition settings where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object definition settings
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns all the object definition settings where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object definition settings
	 */
	public static List<ObjectDefinitionSetting> findByObjectDefinitionId(
		long objectDefinitionId) {

		return getPersistence().findByObjectDefinitionId(objectDefinitionId);
	}

	/**
	 * Returns a range of all the object definition settings where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @return the range of matching object definition settings
	 */
	public static List<ObjectDefinitionSetting> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end) {

		return getPersistence().findByObjectDefinitionId(
			objectDefinitionId, start, end);
	}

	/**
	 * Returns an ordered range of all the object definition settings where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definition settings
	 */
	public static List<ObjectDefinitionSetting> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		return getPersistence().findByObjectDefinitionId(
			objectDefinitionId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definition settings where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definition settings
	 */
	public static List<ObjectDefinitionSetting> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByObjectDefinitionId(
			objectDefinitionId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object definition setting in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	public static ObjectDefinitionSetting findByObjectDefinitionId_First(
			long objectDefinitionId,
			OrderByComparator<ObjectDefinitionSetting> orderByComparator)
		throws com.liferay.object.exception.
			NoSuchObjectDefinitionSettingException {

		return getPersistence().findByObjectDefinitionId_First(
			objectDefinitionId, orderByComparator);
	}

	/**
	 * Returns the first object definition setting in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	public static ObjectDefinitionSetting fetchByObjectDefinitionId_First(
		long objectDefinitionId,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		return getPersistence().fetchByObjectDefinitionId_First(
			objectDefinitionId, orderByComparator);
	}

	/**
	 * Returns the last object definition setting in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	public static ObjectDefinitionSetting findByObjectDefinitionId_Last(
			long objectDefinitionId,
			OrderByComparator<ObjectDefinitionSetting> orderByComparator)
		throws com.liferay.object.exception.
			NoSuchObjectDefinitionSettingException {

		return getPersistence().findByObjectDefinitionId_Last(
			objectDefinitionId, orderByComparator);
	}

	/**
	 * Returns the last object definition setting in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	public static ObjectDefinitionSetting fetchByObjectDefinitionId_Last(
		long objectDefinitionId,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		return getPersistence().fetchByObjectDefinitionId_Last(
			objectDefinitionId, orderByComparator);
	}

	/**
	 * Returns the object definition settings before and after the current object definition setting in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionSettingId the primary key of the current object definition setting
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a object definition setting with the primary key could not be found
	 */
	public static ObjectDefinitionSetting[]
			findByObjectDefinitionId_PrevAndNext(
				long objectDefinitionSettingId, long objectDefinitionId,
				OrderByComparator<ObjectDefinitionSetting> orderByComparator)
		throws com.liferay.object.exception.
			NoSuchObjectDefinitionSettingException {

		return getPersistence().findByObjectDefinitionId_PrevAndNext(
			objectDefinitionSettingId, objectDefinitionId, orderByComparator);
	}

	/**
	 * Removes all the object definition settings where objectDefinitionId = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 */
	public static void removeByObjectDefinitionId(long objectDefinitionId) {
		getPersistence().removeByObjectDefinitionId(objectDefinitionId);
	}

	/**
	 * Returns the number of object definition settings where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object definition settings
	 */
	public static int countByObjectDefinitionId(long objectDefinitionId) {
		return getPersistence().countByObjectDefinitionId(objectDefinitionId);
	}

	/**
	 * Returns all the object definition settings where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching object definition settings
	 */
	public static List<ObjectDefinitionSetting> findByC_N(
		long companyId, String name) {

		return getPersistence().findByC_N(companyId, name);
	}

	/**
	 * Returns a range of all the object definition settings where companyId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @return the range of matching object definition settings
	 */
	public static List<ObjectDefinitionSetting> findByC_N(
		long companyId, String name, int start, int end) {

		return getPersistence().findByC_N(companyId, name, start, end);
	}

	/**
	 * Returns an ordered range of all the object definition settings where companyId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object definition settings
	 */
	public static List<ObjectDefinitionSetting> findByC_N(
		long companyId, String name, int start, int end,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		return getPersistence().findByC_N(
			companyId, name, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definition settings where companyId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definition settings
	 */
	public static List<ObjectDefinitionSetting> findByC_N(
		long companyId, String name, int start, int end,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_N(
			companyId, name, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object definition setting in the ordered set where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	public static ObjectDefinitionSetting findByC_N_First(
			long companyId, String name,
			OrderByComparator<ObjectDefinitionSetting> orderByComparator)
		throws com.liferay.object.exception.
			NoSuchObjectDefinitionSettingException {

		return getPersistence().findByC_N_First(
			companyId, name, orderByComparator);
	}

	/**
	 * Returns the first object definition setting in the ordered set where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	public static ObjectDefinitionSetting fetchByC_N_First(
		long companyId, String name,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		return getPersistence().fetchByC_N_First(
			companyId, name, orderByComparator);
	}

	/**
	 * Returns the last object definition setting in the ordered set where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	public static ObjectDefinitionSetting findByC_N_Last(
			long companyId, String name,
			OrderByComparator<ObjectDefinitionSetting> orderByComparator)
		throws com.liferay.object.exception.
			NoSuchObjectDefinitionSettingException {

		return getPersistence().findByC_N_Last(
			companyId, name, orderByComparator);
	}

	/**
	 * Returns the last object definition setting in the ordered set where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	public static ObjectDefinitionSetting fetchByC_N_Last(
		long companyId, String name,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		return getPersistence().fetchByC_N_Last(
			companyId, name, orderByComparator);
	}

	/**
	 * Returns the object definition settings before and after the current object definition setting in the ordered set where companyId = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionSettingId the primary key of the current object definition setting
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a object definition setting with the primary key could not be found
	 */
	public static ObjectDefinitionSetting[] findByC_N_PrevAndNext(
			long objectDefinitionSettingId, long companyId, String name,
			OrderByComparator<ObjectDefinitionSetting> orderByComparator)
		throws com.liferay.object.exception.
			NoSuchObjectDefinitionSettingException {

		return getPersistence().findByC_N_PrevAndNext(
			objectDefinitionSettingId, companyId, name, orderByComparator);
	}

	/**
	 * Removes all the object definition settings where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 */
	public static void removeByC_N(long companyId, String name) {
		getPersistence().removeByC_N(companyId, name);
	}

	/**
	 * Returns the number of object definition settings where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching object definition settings
	 */
	public static int countByC_N(long companyId, String name) {
		return getPersistence().countByC_N(companyId, name);
	}

	/**
	 * Returns the object definition setting where objectDefinitionId = &#63; and name = &#63; or throws a <code>NoSuchObjectDefinitionSettingException</code> if it could not be found.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	public static ObjectDefinitionSetting findByODI_N(
			long objectDefinitionId, String name)
		throws com.liferay.object.exception.
			NoSuchObjectDefinitionSettingException {

		return getPersistence().findByODI_N(objectDefinitionId, name);
	}

	/**
	 * Returns the object definition setting where objectDefinitionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	public static ObjectDefinitionSetting fetchByODI_N(
		long objectDefinitionId, String name) {

		return getPersistence().fetchByODI_N(objectDefinitionId, name);
	}

	/**
	 * Returns the object definition setting where objectDefinitionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	public static ObjectDefinitionSetting fetchByODI_N(
		long objectDefinitionId, String name, boolean useFinderCache) {

		return getPersistence().fetchByODI_N(
			objectDefinitionId, name, useFinderCache);
	}

	/**
	 * Removes the object definition setting where objectDefinitionId = &#63; and name = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the object definition setting that was removed
	 */
	public static ObjectDefinitionSetting removeByODI_N(
			long objectDefinitionId, String name)
		throws com.liferay.object.exception.
			NoSuchObjectDefinitionSettingException {

		return getPersistence().removeByODI_N(objectDefinitionId, name);
	}

	/**
	 * Returns the number of object definition settings where objectDefinitionId = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the number of matching object definition settings
	 */
	public static int countByODI_N(long objectDefinitionId, String name) {
		return getPersistence().countByODI_N(objectDefinitionId, name);
	}

	/**
	 * Caches the object definition setting in the entity cache if it is enabled.
	 *
	 * @param objectDefinitionSetting the object definition setting
	 */
	public static void cacheResult(
		ObjectDefinitionSetting objectDefinitionSetting) {

		getPersistence().cacheResult(objectDefinitionSetting);
	}

	/**
	 * Caches the object definition settings in the entity cache if it is enabled.
	 *
	 * @param objectDefinitionSettings the object definition settings
	 */
	public static void cacheResult(
		List<ObjectDefinitionSetting> objectDefinitionSettings) {

		getPersistence().cacheResult(objectDefinitionSettings);
	}

	/**
	 * Creates a new object definition setting with the primary key. Does not add the object definition setting to the database.
	 *
	 * @param objectDefinitionSettingId the primary key for the new object definition setting
	 * @return the new object definition setting
	 */
	public static ObjectDefinitionSetting create(
		long objectDefinitionSettingId) {

		return getPersistence().create(objectDefinitionSettingId);
	}

	/**
	 * Removes the object definition setting with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectDefinitionSettingId the primary key of the object definition setting
	 * @return the object definition setting that was removed
	 * @throws NoSuchObjectDefinitionSettingException if a object definition setting with the primary key could not be found
	 */
	public static ObjectDefinitionSetting remove(long objectDefinitionSettingId)
		throws com.liferay.object.exception.
			NoSuchObjectDefinitionSettingException {

		return getPersistence().remove(objectDefinitionSettingId);
	}

	public static ObjectDefinitionSetting updateImpl(
		ObjectDefinitionSetting objectDefinitionSetting) {

		return getPersistence().updateImpl(objectDefinitionSetting);
	}

	/**
	 * Returns the object definition setting with the primary key or throws a <code>NoSuchObjectDefinitionSettingException</code> if it could not be found.
	 *
	 * @param objectDefinitionSettingId the primary key of the object definition setting
	 * @return the object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a object definition setting with the primary key could not be found
	 */
	public static ObjectDefinitionSetting findByPrimaryKey(
			long objectDefinitionSettingId)
		throws com.liferay.object.exception.
			NoSuchObjectDefinitionSettingException {

		return getPersistence().findByPrimaryKey(objectDefinitionSettingId);
	}

	/**
	 * Returns the object definition setting with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectDefinitionSettingId the primary key of the object definition setting
	 * @return the object definition setting, or <code>null</code> if a object definition setting with the primary key could not be found
	 */
	public static ObjectDefinitionSetting fetchByPrimaryKey(
		long objectDefinitionSettingId) {

		return getPersistence().fetchByPrimaryKey(objectDefinitionSettingId);
	}

	/**
	 * Returns all the object definition settings.
	 *
	 * @return the object definition settings
	 */
	public static List<ObjectDefinitionSetting> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the object definition settings.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @return the range of object definition settings
	 */
	public static List<ObjectDefinitionSetting> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the object definition settings.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of object definition settings
	 */
	public static List<ObjectDefinitionSetting> findAll(
		int start, int end,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object definition settings.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of object definition settings
	 */
	public static List<ObjectDefinitionSetting> findAll(
		int start, int end,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the object definition settings from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of object definition settings.
	 *
	 * @return the number of object definition settings
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static ObjectDefinitionSettingPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		ObjectDefinitionSettingPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile ObjectDefinitionSettingPersistence _persistence;

}